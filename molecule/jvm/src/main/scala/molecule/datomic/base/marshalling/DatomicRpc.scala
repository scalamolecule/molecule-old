package molecule.datomic.base.marshalling

import java.io.StringReader
import java.net.URI
import java.util
import java.util.{Collections, Date, UUID, List => jList}
import datomic.Peer.toT
import datomic.Util._
import datomic.core.db.{Db => ClientDb}
import datomic.{Util, Database => PeerDb, Datom => PeerDatom}
import datomicClient.ClojureBridge
import datomicScala.client.api.{Datom => ClientDatom}
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling._
import molecule.core.marshalling.nodes.Obj
import molecule.core.util.testing.TimerPrint
import molecule.core.util.{DateHandling, Helpers, JavaConversions}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade._
import molecule.datomic.base.marshalling.packers.PackEntityMap
import molecule.datomic.client.facade.{Conn_Client, DatomicDb_Client, Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.{Conn_Peer, DatomicDb_Peer, Datomic_Peer}
import moleculeBuildInfo.BuildInfo._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

object DatomicRpc extends MoleculeRpc
  with DateHandling with DateStrLocal
  with Helpers with ClojureBridge
  with PackEntityMap
  with Serializations
  with PackBase
  with JavaConversions {

  // Necessary for `readString` to encode uri in transactions
  require("clojure.core.async")

  // Api ---------------------------------------------

  def transact(
    connProxy: ConnProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReportRPC] = {
    for {
      conn <- getConn(connProxy)

      _ = println(stmtsEdn)

      javaStmts = getJavaStmts(stmtsEdn, uriAttrs)
      txReport <- conn.transactRaw(javaStmts)
    } yield {
      TxReportRPC(txReport.eids, txReport.t, txReport.tx, txReport.inst, txReport.toString)
    }
  }

  def query2packed(
    connProxy: ConnProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, String, String)],
    ll: Seq[(Int, String, Seq[String])],
    lll: Seq[(Int, String, Seq[Seq[String]])],
    maxRows0: Int,
    obj: Obj,
    nestedLevels: Int,
    isOptNested: Boolean,
    refIndexes: List[List[Int]],
    tacitIndexes: List[List[Int]]
  ): Future[String] = Future {
    try {
      val log = new log
      val t   = TimerPrint("DatomicRpc")

      //    println("------------------------------")
      //    println("================================================================================")
      //      println(datalogQuery)
      //      if (rules.nonEmpty) {
      //        println("Rules:")
      //        rules foreach println
      //      }
      //      println("l  : " + l)
      //      println("ll : " + ll)
      //      println("lll: " + lll)

      val inputs    = unmarshallInputs(l ++ ll ++ lll)
      val allInputs = if (rules.nonEmpty) rules ++ inputs else inputs

      //      inputs.foreach(i => println(s"$i   " + i.getClass))

      for {
        conn <- getConn(connProxy)
        allRows <- conn.qRaw(conn.db, datalogQuery, allInputs)
      } yield {
        val rowCountAll = allRows.size
        val maxRows     = if (maxRows0 == -1 || rowCountAll < maxRows0) rowCountAll else maxRows0
        val queryTime   = t.delta
        val space       = " " * (70 - datalogQuery.split('\n').last.length)
        val time        = qTime(queryTime)
        val timeRight   = " " * (8 - time.length) + time

        log("================================================================================")
        log(datalogQuery + space + timeRight)
        if (allInputs.nonEmpty)
          log(allInputs.mkString("Inputs:\n", "\n", ""))

        //      log(datalogQuery + space + timeRight + "  " + conn.asInstanceOf[Conn_Peer].peerConn.db)
        //      log(s"\n---- Querying Datomic... --------------------")
        //      log(datalogQuery)
        //      log(qTime(queryTime) + "  " + datalogQuery)
        //      log("connProxy uuid: " + connProxy.uuid)
        //      log("Query time  : " + thousands(queryTime) + " ms")
        //      log("rowCountAll : " + rowCountAll)
        //      log("maxRows     : " + (if (maxRows == -1) "all" else maxRows))
        //      log("rowCount    : " + rowCount)

        log("-------------------------------")
        //      log(obj.toString)
        //      log("-------------------------------")
        //      log(refIndexes.mkString("\n"))
        //      log("-------------------------------")
        //      log(tacitIndexes.mkString("\n"))
        //      log("-------------------------------")
        allRows.forEach(row => log(row.toString))
        log.print

        val packed = if (isOptNested) {
          OptNested2packed(obj, allRows, maxRows, refIndexes, tacitIndexes).getPacked
        } else if (nestedLevels == 0) {
          // Flat and composites
          Flat2packed(obj, allRows, maxRows).getPacked
        } else {
          Nested2packed(obj, allRows, nestedLevels).getPacked
        }

        //      println("-------------------------------" + packed)
        //        log("Sending data to client... Total server time: " + t.msTotal)
        packed
      }
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }
  }.flatten

  // Unmarshall to Datomic types
  private def unmarshallInputs(lists: Seq[(Int, String, Any)]): Seq[Object] = {
    lists.sortBy(_._1).map {
      case (_, tpe, vs) =>
        val cast = tpe match {
          case "String"     => (v: String) => possiblyEnum(v)
          case "Int"        => (v: String) => new java.lang.Long(v)
          case "Long"       => (v: String) => new java.lang.Long(v)
          case "Double"     => (v: String) => new java.lang.Double(v)
          case "Boolean"    => (v: String) => v.toBoolean.asInstanceOf[Object]
          case "Date"       => (v: String) => str2date(v).asInstanceOf[Object]
          case "URI"        => (v: String) => new java.net.URI(v).asInstanceOf[Object]
          case "UUID"       => (v: String) => java.util.UUID.fromString(v).asInstanceOf[Object]
          case "BigInt"     => (v: String) => new java.math.BigInteger(v).asInstanceOf[Object]
          case "BigDecimal" => (v: String) => new java.math.BigDecimal(v).asInstanceOf[Object]
          case "Any"        => (s: String) =>
            val v = s.drop(10)
            s.take(10) match {
              case "String    " => v
              case "Int       " => new java.lang.Long(v)
              case "Long      " => new java.lang.Long(v)
              case "Double    " => new java.lang.Double(v)
              case "Boolean   " => v.toBoolean.asInstanceOf[Object]
              case "Date      " => str2date(v).asInstanceOf[Object]
              case "URI       " => new URI(v).asInstanceOf[Object]
              case "UUID      " => UUID.fromString(v).asInstanceOf[Object]
              case "BigInt    " => new java.math.BigInteger(v).asInstanceOf[Object]
              case "BigDecimal" => new java.math.BigDecimal(v).asInstanceOf[Object]
            }
          case _            => throw MoleculeException(s"Unexpected type to cast: $tpe")
        }

        vs match {
          case l: Seq[_] =>
            Util.list(l.collect {
              case l2: Seq[_] =>
                val Seq(k, v: String) = l2
                Util.list(k.toString.asInstanceOf[Object], cast(v))

              case v: String => cast(v)
            }: _*)

          case v: String => cast(v)
          case _         => throw MoleculeException("Unexpected input values")
        }
    }
  }

  private def possiblyEnum(s: String): AnyRef = if (s.startsWith("__enum__"))
    s match {
      case r"__enum__:([A-Za-z0-9\.]+)$ns/([A-Za-z0-9]+)$enum" => clojure.lang.Keyword.intern(ns, enum)
      case other                                               =>
        throw MoleculeException(s"Unexpected enum input: `$other`")
    }
  else s

  def index2packed(
    connProxy: ConnProxy,
    api: String,
    index: String,
    args: IndexArgs,
    attrs: Seq[String]
  ): Future[String] = {
    def castTpeV(tpe: String, v: String): Object = {
      (tpe, v) match {
        case ("String", v)     => possiblyEnum(v)
        case ("Int", v)        => v.toInt.asInstanceOf[Object]
        case ("Long", v)       => v.toLong.asInstanceOf[Object]
        case ("Double", v)     => v.toDouble.asInstanceOf[Object]
        case ("Boolean", v)    => v.toBoolean.asInstanceOf[Object]
        case ("Date", v)       => str2date(v).asInstanceOf[Object]
        case ("UUID", v)       => java.util.UUID.fromString(v).asInstanceOf[Object]
        case ("URI", v)        => new java.net.URI(v).asInstanceOf[Object]
        case ("BigInt", v)     => new java.math.BigInteger(v).asInstanceOf[Object]
        case ("BigDecimal", v) =>
          val v1 = if (v.contains(".")) v else s"$v.0"
          new java.math.BigDecimal(v1).asInstanceOf[Object]
        case _                 => throw MoleculeException(s"Unexpected input pair to cast: ($tpe, $v)")
      }
    }

    def datomArgs: Seq[Any] = index match {
      case "EAVT" => args match {
        case IndexArgs(-1L, "", "", "", -1L, -1L, _, _) => Nil
        case IndexArgs(e, "", "", "", -1L, -1L, _, _)   => Seq(e)
        case IndexArgs(e, a, "", "", -1L, -1L, _, _)    => Seq(e, read(a))
        case IndexArgs(e, a, v, tpe, -1L, -1L, _, _)    => Seq(e, read(a), castTpeV(tpe, v))
        case IndexArgs(e, a, v, tpe, t, -1L, _, _)      => Seq(e, read(a), castTpeV(tpe, v), t)
        case IndexArgs(e, a, v, tpe, -1L, inst, _, _)   => Seq(e, read(a), castTpeV(tpe, v), new Date(inst))
        case other                                      => throw MoleculeException("Unexpected IndexArgs: " + other)
      }
      case "AEVT" => args match {
        case IndexArgs(-1L, "", "", "", -1L, -1L, _, _) => Nil
        case IndexArgs(-1L, a, "", "", -1L, -1L, _, _)  => Seq(read(a))
        case IndexArgs(e, a, "", "", -1L, -1L, _, _)    => Seq(read(a), e)
        case IndexArgs(e, a, v, tpe, -1L, -1L, _, _)    => Seq(read(a), e, castTpeV(tpe, v))
        case IndexArgs(e, a, v, tpe, t, -1L, _, _)      => Seq(read(a), e, castTpeV(tpe, v), t)
        case IndexArgs(e, a, v, tpe, -1L, inst, _, _)   => Seq(read(a), e, castTpeV(tpe, v), new Date(inst))
        case other                                      => throw MoleculeException("Unexpected IndexArgs: " + other)
      }
      case "AVET" => args match {
        case IndexArgs(-1L, "", "", "", -1L, -1L, _, _) => Nil
        case IndexArgs(-1L, a, "", "", -1L, -1L, _, _)  => Seq(read(a))
        case IndexArgs(-1L, a, v, tpe, -1L, -1L, _, _)  => Seq(read(a), castTpeV(tpe, v))
        case IndexArgs(e, a, v, tpe, -1L, -1L, _, _)    => Seq(read(a), castTpeV(tpe, v), e)
        case IndexArgs(e, a, v, tpe, t, -1L, _, _)      => Seq(read(a), castTpeV(tpe, v), e, t)
        case IndexArgs(e, a, v, tpe, -1L, inst, _, _)   => Seq(read(a), castTpeV(tpe, v), e, new Date(inst))
        case other                                      => throw MoleculeException("Unexpected IndexArgs: " + other)
      }
      case "VAET" => args match {
        case IndexArgs(-1L, "", "", "", -1L, -1L, _, _) => Nil
        case IndexArgs(-1L, "", v, tpe, -1L, -1L, _, _) => Seq(castTpeV(tpe, v))
        case IndexArgs(-1L, a, v, tpe, -1L, -1L, _, _)  => Seq(castTpeV(tpe, v), read(a))
        case IndexArgs(e, a, v, tpe, -1L, -1L, _, _)    => Seq(castTpeV(tpe, v), read(a), e)
        case IndexArgs(e, a, v, tpe, t, -1L, _, _)      => Seq(castTpeV(tpe, v), read(a), e, t)
        case IndexArgs(e, a, v, tpe, -1L, inst, _, _)   => Seq(castTpeV(tpe, v), read(a), e, new Date(inst))
        case other                                      => throw MoleculeException("Unexpected IndexArgs: " + other)
      }
      case other  => throw MoleculeException("Unexpected index name: " + other)
    }

    try {
      for {
        conn <- getConn(connProxy)
        packed <- {
          val adhocDb: DatomicDb = conn.db
          lazy val attrMap = conn.connProxy.attrMap ++ Seq(":db/txInstant" -> (1, "Date"))

          def peerDatomElement2packed(
            tOpt: Option[Long],
            attr: String
          ): (StringBuffer, PeerDatom) => Future[StringBuffer] = attr match {
            case "e"                   => (sb: StringBuffer, d: PeerDatom) => Future(add(sb, d.e.toString))
            case "a"                   => (sb: StringBuffer, d: PeerDatom) =>
              Future {
                add(sb, adhocDb.getDatomicDb.asInstanceOf[PeerDb].ident(d.a).toString)
                end(sb)
              }
            case "v"                   => (sb: StringBuffer, d: PeerDatom) =>
              Future {
                val a         = adhocDb.getDatomicDb.asInstanceOf[PeerDb].ident(d.a).toString
                val (_, tpe)  = attrMap.getOrElse(a,
                  throw MoleculeException(s"Unexpected attribute `$a` not found in attrMap.")
                )
                val tpePrefix = tpe + " " * (10 - tpe.length)
                tpe match {
                  case "String" => add(sb, tpePrefix + d.v.toString); end(sb)
                  case "Date"   => add(sb, tpePrefix + date2str(d.v.asInstanceOf[Date]))
                  case _        => add(sb, tpePrefix + d.v.toString)
                }
              }
            case "t" if tOpt.isDefined => (sb: StringBuffer, _: PeerDatom) => Future(add(sb, tOpt.get.toString))
            case "t"                   => (sb: StringBuffer, d: PeerDatom) => Future(add(sb, toT(d.tx).toString))
            case "tx"                  => (sb: StringBuffer, d: PeerDatom) => Future(add(sb, d.tx.toString))
            case "txInstant"           => (sb: StringBuffer, d: PeerDatom) =>
              adhocDb.entity(conn, d.tx).rawValue(":db/txInstant").map { v =>
                add(sb, date2str(v.asInstanceOf[Date]))
              }
            case "op"                  => (sb: StringBuffer, d: PeerDatom) => Future(add(sb, d.added.toString))
            case x                     => throw MoleculeException("Unexpected PeerDatom element: " + x)
          }

          def clientDatomElement2packed(
            tOpt: Option[Long],
            attr: String
          ): (StringBuffer, ClientDatom) => Future[StringBuffer] = attr match {
            case "e"                   => (sb: StringBuffer, d: ClientDatom) => Future(add(sb, d.e.toString))
            case "a"                   => (sb: StringBuffer, d: ClientDatom) =>
              Future {
                add(sb, adhocDb.getDatomicDb.asInstanceOf[ClientDb].ident(d.a).toString)
                end(sb)
              }
            case "v"                   => (sb: StringBuffer, d: ClientDatom) =>
              Future {
                val a         = adhocDb.getDatomicDb.asInstanceOf[ClientDb].ident(d.a).toString
                val (_, tpe)  = attrMap.getOrElse(a,
                  throw MoleculeException(s"Unexpected attribute `$a` not found in attrMap.")
                )
                val tpePrefix = tpe + " " * (10 - tpe.length)
                tpe match {
                  case "String" => add(sb, tpePrefix + d.v.toString); end(sb)
                  case "Date"   => add(sb, tpePrefix + date2str(d.v.asInstanceOf[Date]))
                  case _        => add(sb, tpePrefix + d.v.toString)
                }
              }
            case "t" if tOpt.isDefined => (sb: StringBuffer, _: ClientDatom) => Future(add(sb, tOpt.get.toString))
            case "t"                   => (sb: StringBuffer, d: ClientDatom) => Future(add(sb, toT(d.tx).toString))
            case "tx"                  => (sb: StringBuffer, d: ClientDatom) => Future(add(sb, d.tx.toString))
            case "txInstant"           => (sb: StringBuffer, d: ClientDatom) =>
              adhocDb.entity(conn, d.tx).rawValue(":db/txInstant").map { v =>
                add(sb, date2str(v.asInstanceOf[Date]))
              }
            case "op"                  => (sb: StringBuffer, d: ClientDatom) => Future(add(sb, d.added.toString))
            case x                     => throw MoleculeException("Unexpected ClientDatom element: " + x)
          }

          def getPeerDatom2packed(tOpt: Option[Long]): (StringBuffer, PeerDatom) => Future[StringBuffer] = attrs.length match {
            case 1 =>
              val x1 = peerDatomElement2packed(tOpt, attrs.head)
              (sb: StringBuffer, d: PeerDatom) =>
                for {
                  sb1 <- x1(sb, d)
                } yield sb1

            case 2 =>
              val x1 = peerDatomElement2packed(tOpt, attrs.head)
              val x2 = peerDatomElement2packed(tOpt, attrs(1))
              (sb: StringBuffer, d: PeerDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                } yield sb2

            case 3 =>
              val x1 = peerDatomElement2packed(tOpt, attrs.head)
              val x2 = peerDatomElement2packed(tOpt, attrs(1))
              val x3 = peerDatomElement2packed(tOpt, attrs(2))
              (sb: StringBuffer, d: PeerDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                  sb3 <- x3(sb2, d)
                } yield sb3

            case 4 =>
              val x1 = peerDatomElement2packed(tOpt, attrs.head)
              val x2 = peerDatomElement2packed(tOpt, attrs(1))
              val x3 = peerDatomElement2packed(tOpt, attrs(2))
              val x4 = peerDatomElement2packed(tOpt, attrs(3))
              (sb: StringBuffer, d: PeerDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                  sb3 <- x3(sb2, d)
                  sb4 <- x4(sb3, d)
                } yield sb4

            case 5 =>
              val x1 = peerDatomElement2packed(tOpt, attrs.head)
              val x2 = peerDatomElement2packed(tOpt, attrs(1))
              val x3 = peerDatomElement2packed(tOpt, attrs(2))
              val x4 = peerDatomElement2packed(tOpt, attrs(3))
              val x5 = peerDatomElement2packed(tOpt, attrs(4))
              (sb: StringBuffer, d: PeerDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                  sb3 <- x3(sb2, d)
                  sb4 <- x4(sb3, d)
                  sb5 <- x5(sb4, d)
                } yield sb5

            case 6 =>
              val x1 = peerDatomElement2packed(tOpt, attrs.head)
              val x2 = peerDatomElement2packed(tOpt, attrs(1))
              val x3 = peerDatomElement2packed(tOpt, attrs(2))
              val x4 = peerDatomElement2packed(tOpt, attrs(3))
              val x5 = peerDatomElement2packed(tOpt, attrs(4))
              val x6 = peerDatomElement2packed(tOpt, attrs(5))
              (sb: StringBuffer, d: PeerDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                  sb3 <- x3(sb2, d)
                  sb4 <- x4(sb3, d)
                  sb5 <- x5(sb4, d)
                  sb6 <- x6(sb5, d)
                } yield sb6

            case 7 =>
              val x1 = peerDatomElement2packed(tOpt, attrs.head)
              val x2 = peerDatomElement2packed(tOpt, attrs(1))
              val x3 = peerDatomElement2packed(tOpt, attrs(2))
              val x4 = peerDatomElement2packed(tOpt, attrs(3))
              val x5 = peerDatomElement2packed(tOpt, attrs(4))
              val x6 = peerDatomElement2packed(tOpt, attrs(5))
              val x7 = peerDatomElement2packed(tOpt, attrs(6))
              (sb: StringBuffer, d: PeerDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                  sb3 <- x3(sb2, d)
                  sb4 <- x4(sb3, d)
                  sb5 <- x5(sb4, d)
                  sb6 <- x6(sb5, d)
                  sb7 <- x7(sb6, d)
                } yield sb7
          }

          def getClientDatom2packed(tOpt: Option[Long]): (StringBuffer, ClientDatom) => Future[StringBuffer] = attrs.length match {
            case 1 =>
              val x1 = clientDatomElement2packed(tOpt, attrs.head)
              (sb: StringBuffer, d: ClientDatom) =>
                for {
                  sb1 <- x1(sb, d)
                } yield sb1

            case 2 =>
              val x1 = clientDatomElement2packed(tOpt, attrs.head)
              val x2 = clientDatomElement2packed(tOpt, attrs(1))
              (sb: StringBuffer, d: ClientDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                } yield sb2

            case 3 =>
              val x1 = clientDatomElement2packed(tOpt, attrs.head)
              val x2 = clientDatomElement2packed(tOpt, attrs(1))
              val x3 = clientDatomElement2packed(tOpt, attrs(2))
              (sb: StringBuffer, d: ClientDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                  sb3 <- x3(sb2, d)
                } yield sb3

            case 4 =>
              val x1 = clientDatomElement2packed(tOpt, attrs.head)
              val x2 = clientDatomElement2packed(tOpt, attrs(1))
              val x3 = clientDatomElement2packed(tOpt, attrs(2))
              val x4 = clientDatomElement2packed(tOpt, attrs(3))
              (sb: StringBuffer, d: ClientDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                  sb3 <- x3(sb2, d)
                  sb4 <- x4(sb3, d)
                } yield sb4

            case 5 =>
              val x1 = clientDatomElement2packed(tOpt, attrs.head)
              val x2 = clientDatomElement2packed(tOpt, attrs(1))
              val x3 = clientDatomElement2packed(tOpt, attrs(2))
              val x4 = clientDatomElement2packed(tOpt, attrs(3))
              val x5 = clientDatomElement2packed(tOpt, attrs(4))
              (sb: StringBuffer, d: ClientDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                  sb3 <- x3(sb2, d)
                  sb4 <- x4(sb3, d)
                  sb5 <- x5(sb4, d)
                } yield sb5

            case 6 =>
              val x1 = clientDatomElement2packed(tOpt, attrs.head)
              val x2 = clientDatomElement2packed(tOpt, attrs(1))
              val x3 = clientDatomElement2packed(tOpt, attrs(2))
              val x4 = clientDatomElement2packed(tOpt, attrs(3))
              val x5 = clientDatomElement2packed(tOpt, attrs(4))
              val x6 = clientDatomElement2packed(tOpt, attrs(5))
              (sb: StringBuffer, d: ClientDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                  sb3 <- x3(sb2, d)
                  sb4 <- x4(sb3, d)
                  sb5 <- x5(sb4, d)
                  sb6 <- x6(sb5, d)
                } yield sb6

            case 7 =>
              val x1 = clientDatomElement2packed(tOpt, attrs.head)
              val x2 = clientDatomElement2packed(tOpt, attrs(1))
              val x3 = clientDatomElement2packed(tOpt, attrs(2))
              val x4 = clientDatomElement2packed(tOpt, attrs(3))
              val x5 = clientDatomElement2packed(tOpt, attrs(4))
              val x6 = clientDatomElement2packed(tOpt, attrs(5))
              val x7 = clientDatomElement2packed(tOpt, attrs(6))
              (sb: StringBuffer, d: ClientDatom) =>
                for {
                  sb1 <- x1(sb, d)
                  sb2 <- x2(sb1, d)
                  sb3 <- x3(sb2, d)
                  sb4 <- x4(sb3, d)
                  sb5 <- x5(sb4, d)
                  sb6 <- x6(sb5, d)
                  sb7 <- x7(sb6, d)
                } yield sb7
          }

          // Pack Datoms
          val sbFut = api match {
            case "datoms" =>
              adhocDb match {
                case adhocDb: DatomicDb_Peer   =>
                  val datomicIndex = index match {
                    case "EAVT" => datomic.Database.EAVT
                    case "AEVT" => datomic.Database.AEVT
                    case "AVET" => datomic.Database.AVET
                    case "VAET" => datomic.Database.VAET
                  }
                  val datom2packed = getPeerDatom2packed(None)
                  adhocDb.datoms(datomicIndex, datomArgs: _*).flatMap { datoms =>
                    datoms.asScala.foldLeft(Future(new StringBuffer())) {
                      case (sbFut, datom) => sbFut.flatMap(sb => datom2packed(sb, datom))
                    }
                  }
                case adhocDb: DatomicDb_Client =>
                  val datomicIndex = index match {
                    case "EAVT" => ":eavt"
                    case "AEVT" => ":aevt"
                    case "AVET" => ":avet"
                    case "VAET" => ":vaet"
                  }
                  val datom2packed = getClientDatom2packed(None)
                  adhocDb.datoms(datomicIndex, datomArgs).flatMap { datoms =>
                    datoms.iterator().asScala.foldLeft(Future(new StringBuffer())) {
                      case (sbFut, datom) => sbFut.flatMap(sb => datom2packed(sb, datom))
                    }
                  }
              }

            case "indexRange" =>
              adhocDb match {
                case adhocDb: DatomicDb_Peer   =>
                  val datom2packed = getPeerDatom2packed(None)
                  val startValue   = if (args.v.isEmpty) null else castTpeV(args.tpe, args.v)
                  val endValue     = if (args.v2.isEmpty) null else castTpeV(args.tpe2, args.v2)
                  adhocDb.indexRange(args.a, startValue, endValue).flatMap { datoms =>
                    datoms.asScala.foldLeft(Future(new StringBuffer())) {
                      case (sbFut, datom) => sbFut.flatMap(sb => datom2packed(sb, datom))
                    }
                  }
                case adhocDb: DatomicDb_Client =>
                  val datom2packed = getClientDatom2packed(None)
                  val startValue   = if (args.v.isEmpty) None else Some(castTpeV(args.tpe, args.v))
                  val endValue     = if (args.v2.isEmpty) None else Some(castTpeV(args.tpe2, args.v2))
                  adhocDb.indexRange(args.a, startValue, endValue).flatMap { datoms =>
                    datoms.iterator().asScala.foldLeft(Future(new StringBuffer())) {
                      case (sbFut, datom) => sbFut.flatMap(sb => datom2packed(sb, datom))
                    }
                  }
              }

            case "txRange" =>
              // Loop transactions
              conn match {
                case conn: Conn_Peer =>
                  val from  = if (args.v.isEmpty) null else castTpeV(args.tpe, args.v)
                  val until = if (args.v2.isEmpty) null else castTpeV(args.tpe2, args.v2)
                  conn.peerConn.log.txRange(from, until).asScala.foldLeft(Future(new StringBuffer())) {
                    case (sbFut, txMap) =>
                      // Flatten transaction datoms to uniform tuples return type
                      val datom2packed = getPeerDatom2packed(Some(txMap.get(datomic.Log.T).asInstanceOf[Long]))
                      txMap.get(datomic.Log.DATA).asInstanceOf[jList[PeerDatom]].asScala.foldLeft(sbFut) {
                        case (sbFut, datom) => sbFut.flatMap(sb => datom2packed(sb, datom))
                      }
                  }

                case conn: Conn_Client =>
                  val from  = if (args.v.isEmpty) None else Some(castTpeV(args.tpe, args.v))
                  val until = if (args.v2.isEmpty) None else Some(castTpeV(args.tpe2, args.v2))
                  conn.clientConn.txRange(from, until).foldLeft(Future(new StringBuffer())) {
                    case (sbFut, (t, datoms)) =>
                      // Flatten transaction datoms to uniform tuples return type
                      val datom2packed: (StringBuffer, ClientDatom) => Future[StringBuffer] = getClientDatom2packed(Some(t))
                      datoms.foldLeft(sbFut) {
                        case (sbFut, datom) => sbFut.flatMap(sb => datom2packed(sb, datom))
                      }
                  }
              }
          }

          sbFut.map(_.toString)
        }
      } yield {
        println("-------------------------------" + packed)
        packed
      }
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }
  }


  // Presuming a datalog query returning rows of single values.
  // Card-many attributes should therefore not be returned as Sets.
  def getAttrValues(
    connProxy: ConnProxy,
    datalogQuery: String,
    card: Int,
    tpe: String
  ): Future[List[String]] = {
    for {
      conn <- getConn(connProxy)
      rows0 <- conn.qRaw(conn.db, datalogQuery, Nil)
    } yield {
      val cast = if (tpe == "Date" && card != 3)
        (v: Any) => date2strLocal(v.asInstanceOf[Date])
      else
        (v: Any) => v.toString
      var vs   = List.empty[String]
      rows0.forEach(row => vs = vs :+ cast(row.get(0)))
      vs
    }
  }

  def entityAttrKeys(
    connProxy: ConnProxy,
    eid: Long
  ): Future[List[String]] = {
    val datalogQuery = s"[:find ?a1 :where [$eid ?a _][?a :db/ident ?a1]]"
    var list         = List.empty[String]
    for {
      conn <- getConn(connProxy)
      rows <- conn.qRaw(conn.db, datalogQuery, Nil)
    } yield {
      rows.forEach { row =>
        list = row.get(0).toString :: list
      }
      list.sorted
    }
  }


  def t(connProxy: ConnProxy): Future[Long] = {
    for {
      conn <- getConn(connProxy)
      t <- conn.db.t
    } yield t
  }

  def tx(connProxy: ConnProxy): Future[Long] = {
    for {
      conn <- getConn(connProxy)
      tx <- conn.db.tx
    } yield tx
  }

  def txInstant(connProxy: ConnProxy): Future[Date] = {
    for {
      conn <- getConn(connProxy)
      txInstant <- conn.db.txInstant
    } yield txInstant
  }


  def retract(
    connProxy: ConnProxy,
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): Future[TxReport] = {
    println(stmtsEdn)
    for {
      conn <- getConn(connProxy)
      javaStmts = getJavaStmts(stmtsEdn, uriAttrs)
      txReport <- conn.transactRaw(javaStmts)
    } yield TxReportRPC(
      txReport.eids, txReport.t, txReport.tx, txReport.inst, txReport.toString
    )
  }


  def touchMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String] = {
    getDatomicEntity(connProxy, eid).flatMap(_.touchMax(maxDepth)).map(entityMap2packed2)
  }

  def touchQuotedMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String] = {
    getDatomicEntity(connProxy, eid).flatMap(_.touchQuotedMax(maxDepth))
  }

  def touchListMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String] = {
    getDatomicEntity(connProxy, eid).flatMap(_.touchListMax(maxDepth)).map(entityList2packed2)
  }

  def touchListQuotedMax(connProxy: ConnProxy, eid: Long, maxDepth: Int): Future[String] = {
    getDatomicEntity(connProxy, eid).flatMap(_.touchListQuotedMax(maxDepth))
  }

  def asMap(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String] = {
    getDatomicEntity(connProxy, eid).flatMap(_.asMap(depth, maxDepth)).map(entityMap2packed2)
  }

  def asList(connProxy: ConnProxy, eid: Long, depth: Int, maxDepth: Int): Future[String] = {
    getDatomicEntity(connProxy, eid).flatMap(_.asList(depth, maxDepth)).map(entityList2packed2)
  }

  private def getDatomicEntity(connProxy: ConnProxy, eid: Any): Future[DatomicEntity] = {
    getConn(connProxy).map(conn => conn.db.entity(conn, eid))
  }

  def sortList(connProxy: ConnProxy, eid: Long, l: String): Future[String] = ???


  // Connection pool ---------------------------------------------

  private val connectionPool = mutable.HashMap.empty[String, Future[Conn]]

  def clearConnPool(): Future[Unit] = Future {
    connectionPool.clear()
    //    println("Connection pool cleared")
  }

  private def getFreshConn(connProxy: ConnProxy): Future[Conn] = connProxy match {
    case proxy@DatomicPeerProxy(protocol, dbIdentifier, schema, _, _, _, _, _) =>
      protocol match {
        case "mem"             => Datomic_Peer.recreateDbFromEdn(proxy, schema)
        case `datomicProtocol` => Datomic_Peer.connect(proxy, protocol, dbIdentifier)
        case other             => throw new RuntimeException(
          s"\nCan't serve Peer protocol `$other` since the current project is built with " +
            s"datomic `$datomicProtocol`. Please change the build setup or your Conn_Js protocol"
        )
      }

    case proxy@DatomicDevLocalProxy(protocol, system, storageDir, dbName, schema, _, _, _, _, _) =>
      val devLocal = Datomic_DevLocal(system, storageDir)
      protocol match {
        case "mem"             => devLocal.recreateDbFromEdn(schema, proxy)
        case `datomicProtocol` => devLocal.connect(dbName, proxy)
        case other             => throw new RuntimeException(
          s"\nCan't serve DevLocal protocol `$other` since the current project is built with " +
            s"datomic `$datomicProtocol`. Please change the build setup or your Conn_Js protocol"
        )
      }

    case proxy@DatomicPeerServerProxy(accessKey, secret, endpoint, dbName, _, _, _, _, _, _) =>
      Datomic_PeerServer(accessKey, secret, endpoint).connect(dbName, proxy)
  }

  private def getConn(
    connProxy: ConnProxy
  ): Future[Conn] = {
    val futConn             = connectionPool.getOrElse(connProxy.uuid, getFreshConn(connProxy))
    val futConnTimeAdjusted = futConn.map { conn =>
      conn.updateAdhocDbView(connProxy.adhocDbView)
      conn.updateTestDbView(connProxy.testDbView, connProxy.testDbStatus)
      conn
    }
    connectionPool(connProxy.uuid) = futConnTimeAdjusted
    futConnTimeAdjusted
  }


  // Helpers -------------------------------------------------

  def getJavaStmts(
    stmtsEdn: String,
    uriAttrs: Set[String]
  ): jList[AnyRef] = {
    val stmts = readAll(new StringReader(stmtsEdn)).get(0).asInstanceOf[jList[AnyRef]]
    if (uriAttrs.isEmpty) {
      stmts
    } else {
      def uri(s: AnyRef): AnyRef = readString(s"""#=(new java.net.URI "$s")""")
      val stmtsSize = stmts.size()
      val newStmts  = new util.ArrayList[jList[_]](stmtsSize)
      stmts.forEach { stmtRaw =>
        val stmt = stmtRaw.asInstanceOf[jList[AnyRef]]
        if (uriAttrs.contains(stmt.get(2).toString)) {
          val uriStmt = stmt.get(0).toString match {
            case ":db/add"     => list(stmt.get(0), stmt.get(1), stmt.get(2), uri(stmt.get(3)))
            case ":db/retract" => list(stmt.get(0), stmt.get(1), stmt.get(2), uri(stmt.get(3)))
            case ":db.fn/cas"  => list(stmt.get(0), stmt.get(1), stmt.get(2), uri(stmt.get(3)), uri(stmt.get(4)))
            case _             => stmt
          }
          newStmts.add(uriStmt)
        } else {
          newStmts.add(stmt)
        }
      }
      Collections.unmodifiableList(newStmts)
    }
  }

  def qTime(queryTime: Long): String = {
    val indents = 5 - queryTime.toString.length
    " " * indents + thousands(queryTime) + " ms"
  }
}
