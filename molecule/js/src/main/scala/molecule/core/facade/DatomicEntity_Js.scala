package molecule.core.facade

import molecule.core.api.Molecule
import molecule.core.ast.elements.{Model, TxMetaData}
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.DbProxy
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.core.ops.VerifyModel
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.transactionModel
import molecule.datomic.base.ast.transactionModel.RetractEntity
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

case class DatomicEntity_Js(conn: Conn, dbProxy: DbProxy, eid: Any) extends DatomicEntity {
  val rpc = conn.rpc

  def ??? : Nothing = throw MoleculeException("Unexpected method call on JS side in DatomicEntity_Js")


  def mapOneLevel(implicit ec: ExecutionContext): Future[Map[String, Any]] = ???

  def entityMap(implicit ec: ExecutionContext): Future[Map[String, Any]] = ???

  def keySet(implicit ec: ExecutionContext): Future[Set[String]] = ???

  def keys(implicit ec: ExecutionContext): Future[List[String]] = ???

  def rawValue(key: String)(implicit ec: ExecutionContext): Future[Any] = ???

  def apply[T](key: String)(implicit ec: ExecutionContext): Future[Option[T]] = ???

  def apply(kw1: String, kw2: String, kws: String*)(implicit ec: ExecutionContext): Future[List[Option[Any]]] = ???


  def retract(implicit ec: ExecutionContext): Future[TxReport] = {
    eid match {
      case eid: Long => rpc.transact(conn.dbProxy, Stmts2Edn(List(RetractEntity(eid)), conn))
      case _         => throw MoleculeException(s"Unexpected eid $eid of type " + eid.getClass)
    }
  }

  def retract(txMeta: Molecule)
             (implicit ec: ExecutionContext): Future[TxReport] = try {
    val retractStmts = Seq(RetractEntity(eid))
    val txMetaModel        = Model(Seq(TxMetaData(txMeta._model.elements)))
    VerifyModel(txMetaModel, "save") // can throw exception
    eid match {
      case eid: Long =>
      for{
        txMetaStmts <- conn.modelTransformer(txMetaModel).saveStmts
        txReport <- rpc.transact(conn.dbProxy, Stmts2Edn(retractStmts ++ txMetaStmts, conn))
      }  yield txReport

      case _         => throw MoleculeException(s"Unexpected eid $eid of type " + eid.getClass)
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def getRetractStmts(implicit ec: ExecutionContext): Future[List[transactionModel.RetractEntity]] = {
    Future(List(RetractEntity(eid)))
  }


  def inspectRetract(implicit ec: ExecutionContext): Future[Unit] = {
    getRetractStmts.map { stmts =>
      conn.inspect("Inspect `retract` on entity", 1)(1, stmts)
    }
  }

  def inspectRetract(txMeta: Molecule)(implicit ec: ExecutionContext): Future[Unit] = try {
    val retractStmts = Seq(RetractEntity(eid))
    val model        = Model(Seq(TxMetaData(txMeta._model.elements)))
    VerifyModel(model, "save") // can throw exception
    conn.modelTransformer(model).saveStmts.map(txMetaStmts =>
      conn.inspect(
        "Inspect `retract` on entity with tx meta data", 1)(
        1, retractStmts ++ txMetaStmts
      )
    )
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }


  def touch(implicit ec: ExecutionContext): Future[Map[String, Any]] = ???

  def touchMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] = ???

  def touchQuoted(implicit ec: ExecutionContext): Future[String] = ???

  def touchQuotedMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] = ???

  def touchList(implicit ec: ExecutionContext): Future[List[(String, Any)]] = ???

  def touchListMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[List[(String, Any)]] = ???

  def touchListQuoted(implicit ec: ExecutionContext): Future[String] = ???

  def touchListQuotedMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] = ???

  def asMap(depth: Int, maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] = ???

  def asList(depth: Int, maxDepth: Int)(implicit ec: ExecutionContext): Future[List[(String, Any)]] = ???

  def sortList(l: List[Any])(implicit ec: ExecutionContext): Future[List[Any]] = ???
}
