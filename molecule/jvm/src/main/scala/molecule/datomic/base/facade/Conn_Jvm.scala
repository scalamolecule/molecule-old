package molecule.datomic.base.facade

import java.io.{Reader, StringReader}
import java.net.URI
import java.util.{Collections, Date, Collection => jCollection, List => jList, Map => jMap}
import clojure.lang.{PersistentArrayMap, PersistentVector}
import com.cognitect.transit.impl.URIImpl
import datomic.Util.{read, readAll}
import datomic.{Database, Peer, Util}
import molecule.core.ast.elements._
import molecule.core.dto
import molecule.core.dto.SchemaAttr
import molecule.core.exceptions.MoleculeException
import molecule.core.util.{Helpers, JavaConversions}
import molecule.datomic.base.ast.transactionModel.{Cas, Enum, RetractEntity, Statement, TempId}
import molecule.datomic.base.util.Inspect
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait Conn_Jvm extends Conn with JavaConversions with Helpers with QuerySchemaHistory {

  // Molecule api --------------------------------------------------------------

  final def testDbAsOf(t: Long)(implicit ec: ExecutionContext): Future[Unit] =
    cleanFrom(t + 1)

  final def testDbAsOf(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] =
    cleanFrom(txR.t + 1)

  final def testDbAsOf(d: Date)(implicit ec: ExecutionContext): Future[Unit] = {
    cleanFrom(new Date(d.toInstant.plusMillis(1).toEpochMilli))
  }


  // Datomic shared Peer/Client api --------------------------------------------

  final def transact(edn: String)
                    (implicit ec: ExecutionContext): Future[TxReport] =
    transact(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]])


  final override def transact(stmtsReader: Reader)
                             (implicit ec: ExecutionContext): Future[TxReport] =
    transact(readAll(stmtsReader).get(0).asInstanceOf[jList[_]])


  final override def transact(javaStmts: jList[_])(implicit ec: ExecutionContext): Future[TxReport] =
    transactRaw(javaStmts, Future.successful(Seq.empty[Statement]))

  final override def query(datalogQuery: String, inputs: Any*)
                          (implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = {
    val javaInputs = inputs.toSeq.map {
      case l: Iterable[_] => Util.list(
        l.map {
          case l2: Iterable[_] => Util.list(l2.map(_.asInstanceOf[AnyRef]).toSeq: _*)
          case v               => v.asInstanceOf[AnyRef]
        }.toSeq: _*
      )
      case v              => v.asInstanceOf[AnyRef]
    }
    rawQuery(datalogQuery, javaInputs).map { raw =>
      if (
        raw.isInstanceOf[PersistentVector]
          && !raw.asInstanceOf[PersistentVector].isEmpty
          && raw.asInstanceOf[PersistentVector].nth(0).isInstanceOf[PersistentArrayMap]
      ) {
        raw.asInstanceOf[jCollection[jMap[_, _]]].asScala.toList.map { rows =>
          rows.asScala.toList.map { case (k, v) => k.toString -> v }
        }
      } else {
        raw.asScala.toList
          .map(_.asScala.toList
            .map {
              case set: clojure.lang.PersistentHashSet => set.asScala.toSet
              case uriImpl: URIImpl                    => new URI(uriImpl.toString)
              case bi: clojure.lang.BigInt             => BigInt(bi.toString)
              case other                               => other
            }
          )
      }
    }
  }


  // Schema --------------------------------------------------------------------

  private def attrExists(ident: String)(implicit ec: ExecutionContext): Future[Boolean] = query(
    s"[:find (count ?id) :where [?id :db/ident $ident]]"
  ).map { res =>
    if (res == List(List(1))) true else throw MoleculeException(
      s"Couldn't find attribute `$ident` in the database."
    )
  }

  private def attrHasNoData(ident: String)(implicit ec: ExecutionContext): Future[Unit] = {
    query(s"[:find (count ?v) :where [_ $ident ?v]]").map(data =>
      if (data.nonEmpty) {
        val count  = data.head.head.toString.toInt
        val values = if (count == 1) "value" else "values"
        throw MoleculeException(
          s"Can't retire attribute `$ident` having $count $values asserted. " +
            s"Please retract $values before retiring attribute."
        )
      }
    ).recoverWith { case exc =>
      exc.getMessage match {
        case r".*Unable to resolve entity: :.+/(.+)$attr.*" => Future.failed(
          MoleculeException(s"Couldn't find attribute `$ident` in the database schema.")
        )
        case _                                              => Future.failed(exc)
      }
    }
  }

  def changeAttrName(curName: String, newName: String)
                    (implicit ec: ExecutionContext): Future[TxReport] = try {
    val (curIdent, newIdent) = (okIdent(curName), okIdent(newName))
    attrExists(curIdent).flatMap(_ => transact(s"[{:db/id $curIdent :db/ident $newIdent}]"))
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retireAttr(attrName: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val ident        = okIdent(attrName)
    val retiredIdent = ident.replace(":", ":-")
    for {
      _ <- attrExists(ident)
      _ <- attrHasNoData(ident)
      txReport <- transact(s"[{:db/id $ident :db/ident $retiredIdent}]")
    } yield txReport
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }


  def changeNamespaceName(curName: String, newName: String)
                         (implicit ec: ExecutionContext): Future[TxReport] = try {
    val (curNs, newNs) = (okNamespaceName(curName), okNamespaceName(newName))
    connProxy.nsMap.get(curNs).fold[Future[TxReport]](
      Future.failed(MoleculeException(s"Couldn't find namespace `$curNs`."))
    ) { nsMap =>
      val attrNameChanges = nsMap.attrs.map { metaAttr =>
        val attr = metaAttr.name
        s"{:db/id :$curNs/$attr :db/ident :$newNs/$attr}"
      }.mkString("")
      transact(s"[$attrNameChanges]")
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retireNamespace(nsName: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val ns = okNamespaceName(nsName)
    connProxy.nsMap.get(ns).fold[Future[TxReport]](
      Future.failed(MoleculeException(s"Couldn't find namespace `$ns`."))
    ) { nsMap =>
      for {
        _ <- Future.sequence(nsMap.attrs.map(metaAttr => attrHasNoData(s":$ns/${metaAttr.name}")))
        txReport <- {
          // Prefix all attribute names in namespace with `-`
          val attrNameChanges = nsMap.attrs.map { metaAttr =>
            val attr = metaAttr.name
            s"{:db/id :$ns/$attr :db/ident :-$ns/$attr}"
          }.mkString("")
          transact(s"[$attrNameChanges]")
        }
      } yield txReport
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def changePartitionName(curName: String, newName: String)
                         (implicit ec: ExecutionContext): Future[TxReport] = try {
    val (curPart, newPart) = (okPartitionName(curName), okPartitionName(newName))
    val nss                = connProxy.nsMap.filter(_._1.startsWith(curPart)).toSeq
    if (nss.isEmpty) {
      throw MoleculeException(s"Couldn't find partition `$curPart`.")
    }
    val stmts = for {
      (_, nsMap) <- nss
      ns = nsMap.name
      metaAttr <- nsMap.attrs
      attr = metaAttr.name
    } yield s"{:db/id :${curPart}_$ns/$attr :db/ident :${newPart}_$ns/$attr}"
    transact(stmts.mkString("[", "", "]"))
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retirePartition(partName: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val part = okPartitionName(partName)
    val nss  = connProxy.nsMap.filter(_._1.startsWith(part)).toSeq
    if (nss.isEmpty) {
      throw MoleculeException(s"Couldn't find partition `$part`.")
    }
    val (nsAttr, stmts) = (for {
      (_, nsMap) <- nss
      ns = nsMap.name
      metaAttr <- nsMap.attrs
      attr = metaAttr.name
    } yield {
      // Prefix all attribute names in partition with `-`
      ((ns, attr), s"{:db/id :${part}_$ns/$attr :db/ident :-${part}_$ns/$attr}")
    }).unzip
    for {
      _ <- Future.sequence(nsAttr.map { case (ns, attr) => attrHasNoData(s":${part}_$ns/$attr") })
      txReport <- transact(stmts.mkString("[", "", "]"))
    } yield txReport
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retractSchemaOption(attr: String, option: String)
                         (implicit ec: ExecutionContext): Future[TxReport] = try {
    val ident   = okIdent(attr)
    val options = List("doc", "unique", "isComponent", "noHistory", "index")
    if (!options.contains(option)) {
      throw MoleculeException(
        s"Can't retract option '$option' for attribute `$attr`. " +
          s"Only the following options can be retracted: " + options.mkString(", ") + s"."
      )
    }
    for {
      _ <- attrExists(ident)
      res <- query(s"[:find ?v :where [$ident :db/$option ?v]]")
      txReport <- {
        if (res.isEmpty) {
          Future.failed(MoleculeException(s"'$option' option of attribute $attr has no value."))
        } else {
          val rawValue = res.head.head // Keyword (unique), String (doc) or Boolean (isComponent, noHistory, index)
          val curValue = option match {
            case "doc" => s""""$rawValue""""
            case _     => rawValue
          }
          transact(s"[[:db/retract $attr :db/$option $curValue]]")
        }
      }
    } yield txReport
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  protected def historyQuery(query: String, inputs: Seq[jList[AnyRef]] = Nil)
                            (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]]

  def getEnumHistory(implicit ec: ExecutionContext)
  : Future[List[(String, Int, Long, Date, String, Boolean)]] = {
    val enumQuery =
      """[:find  ?a
        |        ?enumT
        |        ?enumTx
        |        ?enumTxInst
        |        ?enum
        |        ?op
        | :in $ $dbCurrent
        | :where [:db.part/db :db.install/attribute ?attrId]
        |        [$dbCurrent ?attrId :db/ident ?attrIdent]
        |        [(name ?attrIdent) ?attr]
        |        [(str ?attrIdent) ?a]
        |        [(namespace ?attrIdent) ?nsFull0]
        |        [(if (= (subs ?nsFull0 0 1) "-") (subs ?nsFull0 1) ?nsFull0) ?nsFull]
        |        [(.matches ^String ?nsFull "^(db|db.alter|db.excise|db.install|db.part|db.sys|fressian|db.entity|db.attr|-.*)") ?sys]
        |        [(= ?sys false)]
        |        [_ :db/ident ?enumIdent ?enumTx ?op]
        |        [(namespace ?enumIdent) ?enumNs]
        |        [(str ?nsFull "." ?attr) ?enumSubNs]
        |        [(= ?enumSubNs ?enumNs)]
        |        [(name ?enumIdent) ?enum]
        |        [(datomic.api/tx->t ?enumTx) ?enumT]
        |        [?enumTx :db/txInstant ?enumTxInst]
        |]""".stripMargin

    historyQuery(enumQuery).map { enumRes =>
      val rows = List.newBuilder[(String, Int, Long, Date, String, Boolean)]
      enumRes.forEach { enumRow =>
        rows.+=((
          enumRow.get(0).asInstanceOf[String],
          enumRow.get(1).toString.toInt,
          enumRow.get(2).toString.toLong,
          enumRow.get(3).asInstanceOf[Date],
          enumRow.get(4).asInstanceOf[String],
          enumRow.get(5).asInstanceOf[Boolean],
        ))
      }
      rows.result().sortBy(r => (r._1, r._2, r._5, r._6))
    }
  }

  def retractEnum(enumString: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val enumIdent = okEnumIdent(enumString)
    transact(s"[[:db/retractEntity $enumIdent]]")
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }


  // Internal ------------------------------------------------------------------

  private[molecule] val isJsPlatform: Boolean = false

  private[molecule] final override def transact(
    edn: String,
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] =
    transactRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]], scalaStmts)


  private[molecule] final override def transact(
    stmtsReader: Reader,
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] =
    transactRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]], scalaStmts)


  private[molecule] final def transact(
    scalaStmts: Future[Seq[Statement]]
  )(implicit ec: ExecutionContext): Future[TxReport] = scalaStmts.flatMap { stmts =>
    transactRaw(stmts2java(stmts), scalaStmts)
  }

  private[molecule] final override def jvmSchemaHistoryQueryTpl(
    model: Model,
    queryString: String
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = try {
    val attrs = model.elements.collect { case g: Generic =>
      val attr           = g.attr
      val last           = attr.last
      val attrClean      = if (last == '_' || last == '$') attr.init else attr
      val (expr, inputs) = g match {
        case Generic(_, "txInstant" | "txInstant_", _, v, _) => v match {
          case NoValue        => ("", Nil)
          case Eq(args)       => ("=", args.map(arg => date2str(arg.asInstanceOf[Date])))
          case Neq(args)      => ("!=", args.map(arg => date2str(arg.asInstanceOf[Date])))
          case Gt(arg)        => (">", Seq(date2str(arg.asInstanceOf[Date])))
          case Ge(arg)        => (">=", Seq(date2str(arg.asInstanceOf[Date])))
          case Lt(arg)        => ("<", Seq(date2str(arg.asInstanceOf[Date])))
          case Le(arg)        => ("<=", Seq(date2str(arg.asInstanceOf[Date])))
          case Fn("count", _) => ("count", Nil)
          case Fulltext(arg)  => ("fulltext", Seq(date2str(arg.asInstanceOf[Date])))
          case other          => throw MoleculeException("Unexpected txInstant attribute value: " + other)
        }
        case Generic(_, _, _, NoValue, _)                    => ("", Nil)
        case Generic(_, _, _, Eq(args), _)                   => ("=", args.map(_.toString))
        case Generic(_, _, _, Neq(args), _)                  => ("!=", args.map(_.toString))
        case Generic(_, _, _, Gt(arg), _)                    => (">", Seq(arg.toString))
        case Generic(_, _, _, Ge(arg), _)                    => (">=", Seq(arg.toString))
        case Generic(_, _, _, Lt(arg), _)                    => ("<", Seq(arg.toString))
        case Generic(_, _, _, Le(arg), _)                    => ("<=", Seq(arg.toString))
        case Generic(_, _, _, Fn("count", _), _)             => ("count", Nil)
        case Generic(_, _, _, Fulltext(arg), _)              => ("fulltext", Seq(arg.toString))
        case other                                           =>
          throw MoleculeException("Unexpected generic schema history value: " + other)
      }
      SchemaAttr(attrClean, attr, expr, inputs)
    }
    fetchSchemaHistory(attrs, queryString)
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  private[molecule] final override def inspect(
    header: String,
    threshold: Int,
    max: Int = 9999,
    showStackTrace: Boolean = false,
    maxLevel: Int = 99,
    showBi: Boolean = false
  )(id: Int, params: Any*): Unit =
    Inspect(header, threshold, max, showStackTrace, maxLevel, showBi)(id, params: _*)


  // Needs to be public since tx functions use id
  final override def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = {
    var tempIds = Map.empty[Int, AnyRef]

    def getTempId(part: String, i: Int): AnyRef = tempIds.getOrElse(i, {
      val tempId = Peer.tempid(read(part))
      tempIds = tempIds + (i -> tempId)
      tempId
    })

    def eid(e: Any): AnyRef = (e match {
      case l: Long         => l
      case TempId(part, i) => getTempId(part, i)
      case "datomic.tx"    => "datomic.tx"
      case other           => throw new Exception("Unexpected entity id: " + other)
    }).asInstanceOf[AnyRef]

    def value(v: Any): AnyRef = (v match {
      case i: Int => i.toLong
      //      case f: Float           => f.toDouble
      case TempId(part, i)    => getTempId(part, i)
      case Enum(prefix, enum) => read(prefix + enum)
      case bigInt: BigInt     => bigInt.bigInteger
      case bigDec: BigDecimal => bigDec.bigDecimal
      case other              => other
    }).asInstanceOf[AnyRef]

    val list: jList[jList[_]] = new java.util.ArrayList[jList[_]](stmts.length)
    stmts.foreach {
      case s: RetractEntity =>
        list.add(Util.list(read(s.action), s.e.asInstanceOf[AnyRef]))
      case s: Cas           =>
        list.add(Util.list(read(s.action), s.e.asInstanceOf[AnyRef], read(s.a), value(s.oldV), value(s.v)))
      case s                =>
        list.add(Util.list(read(s.action), eid(s.e), read(s.a), value(s.v)))
    }
    Collections.unmodifiableList(list)
  }

  private[molecule] final def getAttrValues(
    datalogQuery: String,
    card: Int,
    tpe: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    query(datalogQuery).map(_.map(_.head.toString))


  private[molecule] final def getEntityAttrKeys(
    datalogQuery: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    query(datalogQuery).map(rows => rows.map(_.head.toString))

  // Reset datoms of in-mem with-db from next timePoint after as-of t until end
  protected def cleanFrom(nextTimePoint: Any)(implicit ec: ExecutionContext): Future[Unit]

}
