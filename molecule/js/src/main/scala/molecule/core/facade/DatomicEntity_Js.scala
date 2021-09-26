package molecule.core.facade

import molecule.core.api.Molecule
import molecule.core.ast.elements.{Model, TxMetaData}
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.core.marshalling.unpackers.Packed2EntityMap
import molecule.core.ops.VerifyModel
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.transactionModel.RetractEntity
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

case class DatomicEntity_Js(conn: Conn, eidAny: Any) extends Packed2EntityMap(conn) with DatomicEntity {
  val rpc = conn.rpc

  private def withEid[T](body: Long => Future[T]): Future[T] = try {
    eidAny match {
      case eidLong: Long => body(eidLong)
      case _             => throw MoleculeException(s"Expected Long eid. Found `$eidAny` of type " + eidAny.getClass)
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retract(implicit ec: ExecutionContext): Future[TxReport] = withEid(eid =>
    rpc.transact(conn.connProxy, Stmts2Edn(List(RetractEntity(eid)), conn))
  )


  def retract(txMeta: Molecule)
             (implicit ec: ExecutionContext): Future[TxReport] = withEid { eid =>
    val retractStmts = Seq(RetractEntity(eid))
    val txMetaModel  = Model(Seq(TxMetaData(txMeta._model.elements)))
    try {
      VerifyModel(txMetaModel, "save") // can throw exception
      for {
        txMetaStmts <- conn.model2stmts(txMetaModel).saveStmts
        txReport <- rpc.transact(conn.connProxy, Stmts2Edn(retractStmts ++ txMetaStmts, conn))
      } yield txReport
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }
  }

  def getRetractStmts(implicit ec: ExecutionContext): Future[List[RetractEntity]] = withEid(eid =>
    Future(List(RetractEntity(eid)))
  )


  def inspectRetract(implicit ec: ExecutionContext): Future[Unit] = {
    getRetractStmts.map { stmts =>
      conn.inspect("Inspect `retract` on entity", 1)(1, stmts)
    }
  }

  def inspectRetract(txMeta: Molecule)(implicit ec: ExecutionContext): Future[Unit] = withEid { eid =>
    val retractStmts = Seq(RetractEntity(eid))
    val model        = Model(Seq(TxMetaData(txMeta._model.elements)))
    try {
      VerifyModel(model, "save") // can throw exception
      conn.model2stmts(model).saveStmts.map(txMetaStmts =>
        conn.inspect("Inspect `retract` on entity with tx meta data", 1)(1, retractStmts ++ txMetaStmts)
      )
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }
  }


  def touch(implicit ec: ExecutionContext): Future[Map[String, Any]] = withEid(eid =>
    rpc.touchMax(conn.connProxy, eid, 5).map(packed2entityMap)
  )

  def touchMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] = withEid(eid =>
    rpc.touchMax(conn.connProxy, eid, maxDepth).map(packed2entityMap)
  )

  def touchQuoted(implicit ec: ExecutionContext): Future[String] = withEid(eid =>
    rpc.touchQuotedMax(conn.connProxy, eid, 5)
  )

  def touchQuotedMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] = withEid(eid =>
    rpc.touchQuotedMax(conn.connProxy, eid, maxDepth)
  )

  def touchList(implicit ec: ExecutionContext): Future[List[(String, Any)]] = withEid(eid =>
    rpc.touchListMax(conn.connProxy, eid, 5).map(packed2entityList)
  )

  def touchListMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[List[(String, Any)]] = withEid(eid =>
    rpc.touchListMax(conn.connProxy, eid, maxDepth).map(packed2entityList)
  )

  def touchListQuoted(implicit ec: ExecutionContext): Future[String] = withEid(eid =>
    rpc.touchListQuotedMax(conn.connProxy, eid, 5)
  )

  def touchListQuotedMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] = withEid(eid =>
    rpc.touchListQuotedMax(conn.connProxy, eid, maxDepth)
  )

  def asMap(depth: Int, maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] = withEid(eid =>
    rpc.asMap(conn.connProxy, eid, depth, maxDepth).map(packed2entityMap)
  )

  def asList(depth: Int, maxDepth: Int)(implicit ec: ExecutionContext): Future[List[(String, Any)]] = withEid(eid =>
    rpc.asList(conn.connProxy, eid, depth, maxDepth).map(packed2entityList)
  )
}
