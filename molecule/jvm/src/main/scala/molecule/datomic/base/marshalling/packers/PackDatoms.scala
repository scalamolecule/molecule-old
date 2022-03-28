package molecule.datomic.base.marshalling.packers

import java.io.StringReader
import java.net.URI
import java.util
import java.util.{Collections, Date, UUID, List => jList}
import datomic.Peer.toT
import datomic.Util._
import datomic.{Util, Database => PeerDb, Datom => PeerDatom}
import datomicClient.ClojureBridge
import datomicScala.client.api.{Datom => ClientDatom}
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling._
import molecule.core.marshalling.ast._
import molecule.core.marshalling.ast.nodes.Obj
import molecule.core.util.testing.TimerPrint
import molecule.core.util.{DateHandling, Helpers, JavaConversions, Quoted}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.facade._
import molecule.datomic.base.marshalling.sorting.{SortDatoms_Peer, SortRows}
import molecule.datomic.base.marshalling._
import molecule.datomic.base.util.JavaHelpers
import molecule.datomic.client.facade.{Conn_Client, DatomicDb_Client, Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.{Conn_Peer, DatomicDb_Peer, Datomic_Peer}
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

case class PackDatoms(
  conn: Conn,
  adhocDb: DatomicDb,
  attrs: Seq[String],
  index: String,
  indexArgs: IndexArgs,
)(implicit ec: ExecutionContext) extends JavaHelpers with PackBase {

  lazy val attrMap = conn.connProxy.attrMap ++ Seq(":db/txInstant" -> (1, "Date"))

  def peerDatomElement2packed(
    tOpt: Option[Long],
    attr: String
  ): (StringBuffer, PeerDatom) => Future[StringBuffer] = {

    attr match {
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


  private def getIdent(conn: Conn, eid: Any): Future[String] = {
    conn.rawQuery(s"[:find ?idIdent :where [$eid :db/ident ?idIdent]]").map { rows =>
      if (rows.size() != 1)
        throw MoleculeException(s"Couldn't find attribute name of eid $eid in saved schema.")
      else
        rows.iterator().next().get(0).toString
    }
  }

  def clientDatomElement2packed(
    tOpt: Option[Long],
    attr: String
  ): (StringBuffer, ClientDatom) => Future[StringBuffer] = {
    attr match {
      case "e" => (sb: StringBuffer, d: ClientDatom) => Future(add(sb, d.e.toString))
      case "a" => (sb: StringBuffer, d: ClientDatom) =>
        getIdent(conn, d.a).map { attrName =>
          add(sb, attrName)
          end(sb)
        }

      case "v" => (sb: StringBuffer, d: ClientDatom) =>
        getIdent(conn, d.a).map { attrName =>
          val (_, tpe)  = attrMap.getOrElse(attrName,
            throw MoleculeException(s"Attribute name `$attrName` not found in attrMap.")
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


  def args: Seq[Any] = index match {
    case "EAVT" => indexArgs match {
      case IndexArgs(-1L, "", "", "", -1L, -1L, _, _) => Nil
      case IndexArgs(e, "", "", "", -1L, -1L, _, _)   => Seq(e)
      case IndexArgs(e, a, "", "", -1L, -1L, _, _)    => Seq(e, read(a))
      case IndexArgs(e, a, v, tpe, -1L, -1L, _, _)    => Seq(e, read(a), castTpeV(tpe, v))
      case IndexArgs(e, a, v, tpe, t, -1L, _, _)      => Seq(e, read(a), castTpeV(tpe, v), t)
      case IndexArgs(e, a, v, tpe, -1L, inst, _, _)   => Seq(e, read(a), castTpeV(tpe, v), new Date(inst))
      case other                                      => throw MoleculeException("Unexpected IndexArgs: " + other)
    }
    case "AEVT" => indexArgs match {
      case IndexArgs(-1L, "", "", "", -1L, -1L, _, _) => Nil
      case IndexArgs(-1L, a, "", "", -1L, -1L, _, _)  => Seq(read(a))
      case IndexArgs(e, a, "", "", -1L, -1L, _, _)    => Seq(read(a), e)
      case IndexArgs(e, a, v, tpe, -1L, -1L, _, _)    => Seq(read(a), e, castTpeV(tpe, v))
      case IndexArgs(e, a, v, tpe, t, -1L, _, _)      => Seq(read(a), e, castTpeV(tpe, v), t)
      case IndexArgs(e, a, v, tpe, -1L, inst, _, _)   => Seq(read(a), e, castTpeV(tpe, v), new Date(inst))
      case other                                      => throw MoleculeException("Unexpected IndexArgs: " + other)
    }
    case "AVET" => indexArgs match {
      case IndexArgs(-1L, "", "", "", -1L, -1L, _, _) => Nil
      case IndexArgs(-1L, a, "", "", -1L, -1L, _, _)  => Seq(read(a))
      case IndexArgs(-1L, a, v, tpe, -1L, -1L, _, _)  => Seq(read(a), castTpeV(tpe, v))
      case IndexArgs(e, a, v, tpe, -1L, -1L, _, _)    => Seq(read(a), castTpeV(tpe, v), e)
      case IndexArgs(e, a, v, tpe, t, -1L, _, _)      => Seq(read(a), castTpeV(tpe, v), e, t)
      case IndexArgs(e, a, v, tpe, -1L, inst, _, _)   => Seq(read(a), castTpeV(tpe, v), e, new Date(inst))
      case other                                      => throw MoleculeException("Unexpected IndexArgs: " + other)
    }
    case "VAET" => indexArgs match {
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
}
