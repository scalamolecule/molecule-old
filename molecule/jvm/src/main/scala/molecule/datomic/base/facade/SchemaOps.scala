package molecule.datomic.base.facade

import java.util.Date
import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import molecule.datomic.base.ops.QueryOps.txBase
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait SchemaOps extends Helpers {

  private def attrExists(conn: Conn_Jvm, ident: String)
                        (implicit ec: ExecutionContext): Future[Boolean] = {
    conn.query(
      s"[:find (count ?id) :where [?id :db/ident $ident]]"
    ).map { res =>
      if (res == List(List(1))) true else throw MoleculeException(
        s"Couldn't find attribute `$ident` in the database."
      )
    }
  }

  private def attrHasNoData(conn: Conn_Jvm, ident: String)
                           (implicit ec: ExecutionContext): Future[Unit] = {
    conn.query(s"[:find (count ?v) :where [_ $ident ?v]]").map(data =>
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


  protected def changeAttrName_(conn: Conn_Jvm, curName: String, newName: String)
                     (implicit ec: ExecutionContext): Future[TxReport] = try {
    val (curIdent, newIdent) = (okIdent(curName), okIdent(newName))
    for {
      _ <- attrExists(conn, curIdent)
      txReport <- conn.transact(s"[{:db/id $curIdent :db/ident $newIdent}]")
    } yield txReport
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retireAttr_(conn: Conn_Jvm, attrName: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val ident        = okIdent(attrName)
    val retiredIdent = ident.replace(":", ":-")
    for {
      _ <- attrExists(conn, ident)
      _ <- attrHasNoData(conn, ident)
      txReport <- conn.transact(s"[{:db/id $ident :db/ident $retiredIdent}]")
    } yield txReport
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def changeNamespaceName_(conn: Conn_Jvm, curName: String, newName: String)
                          (implicit ec: ExecutionContext): Future[TxReport] = try {
    val (curNs, newNs) = (okNamespaceName(curName), okNamespaceName(newName))
    conn.connProxy.nsMap.get(curNs).fold[Future[TxReport]](
      Future.failed(MoleculeException(s"Couldn't find namespace `$curNs`."))
    ) { nsMap =>
      val attrNameChanges = nsMap.attrs.map { metaAttr =>
        val attr = metaAttr.name
        s"{:db/id :$curNs/$attr :db/ident :$newNs/$attr}"
      }.mkString("")
      conn.transact(s"[$attrNameChanges]")
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retireNamespace_(conn: Conn_Jvm, nsName: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val ns = okNamespaceName(nsName)
    conn.connProxy.nsMap.get(ns).fold[Future[TxReport]](
      Future.failed(MoleculeException(s"Couldn't find namespace `$ns`."))
    ) { nsMap =>
      for {
        _ <- Future.sequence(nsMap.attrs.map(metaAttr => attrHasNoData(conn, s":$ns/${metaAttr.name}")))
        txReport <- {
          // Prefix all attribute names in namespace with `-`
          val attrNameChanges = nsMap.attrs.map { metaAttr =>
            val attr = metaAttr.name
            s"{:db/id :$ns/$attr :db/ident :-$ns/$attr}"
          }.mkString("")
          conn.transact(s"[$attrNameChanges]")
        }
      } yield txReport
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def changePartitionName_(conn: Conn_Jvm, curName: String, newName: String)
                          (implicit ec: ExecutionContext): Future[TxReport] = try {
    val (curPart, newPart) = (okPartitionName(curName), okPartitionName(newName))
    val nss                = conn.connProxy.nsMap.filter(_._1.startsWith(curPart)).toSeq
    if (nss.isEmpty) {
      throw MoleculeException(s"Couldn't find partition `$curPart`.")
    }
    val stmts = for {
      (_, nsMap) <- nss
      ns = nsMap.name
      metaAttr <- nsMap.attrs
      attr = metaAttr.name
    } yield s"{:db/id :${curPart}_$ns/$attr :db/ident :${newPart}_$ns/$attr}"
    conn.transact(stmts.mkString("[", "", "]"))
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retirePartition_(conn: Conn_Jvm, partName: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val part = okPartitionName(partName)
    val nss  = conn.connProxy.nsMap.filter(_._1.startsWith(part)).toSeq
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
      _ <- Future.sequence(nsAttr.map { case (ns, attr) => attrHasNoData(conn, s":${part}_$ns/$attr") })
      txReport <- conn.transact(stmts.mkString("[", "", "]"))
    } yield txReport
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retractSchemaOption_(conn: Conn_Jvm, attr: String, option: String)
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
      _ <- attrExists(conn, ident)
      res <- conn.query(s"[:find ?v :where [$ident :db/$option ?v]]")
      txReport <- {
        if (res.isEmpty) {
          Future.failed(MoleculeException(s"'$option' option of attribute $attr has no value."))
        } else {
          val rawValue = res.head.head // Keyword (unique), String (doc) or Boolean (isComponent, noHistory, index)
          val curValue = option match {
            case "doc" => s""""$rawValue""""
            case _     => rawValue
          }
          conn.transact(s"[[:db/retract $attr :db/$option $curValue]]")
        }
      }
    } yield txReport
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }


  def getEnumHistory_(conn: Conn_Jvm)(implicit ec: ExecutionContext)
  : Future[List[(String, Int, Long, Date, String, Boolean)]] = {
    val enumQuery =
      s"""[:find  ?a
         |        ?enumT
         |        ?enumTx
         |        ?enumTxInst
         |        ?enum
         |        ?op
         | :in $$ $$dbCurrent
         | :where [:db.part/db :db.install/attribute ?attrId]
         |        [$$dbCurrent ?attrId :db/ident ?attrIdent]
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
         |        [(- ?enumTx $txBase) ?enumT]
         |        [?enumTx :db/txInstant ?enumTxInst]
         |]""".stripMargin

    conn.historyQuery(enumQuery).map { enumRes =>
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

  def retractEnum_(conn: Conn_Jvm, enumString: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val enumIdent = okEnumIdent(enumString)
    conn.transact(s"[[:db/retractEntity $enumIdent]]")
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }
}
