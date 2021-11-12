package molecule.core.facade

import molecule.core.api.Molecule
import molecule.core.ast.elements.{Model, TxMetaData}
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.convert.Stmts2Edn
import molecule.core.marshalling.unpackers.Packed2EntityMap
import molecule.core.ops.VerifyModel
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.transactionModel.{RetractEntity, Statement}
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scala.util.control.NonFatal

case class DatomicEntity_Js(conn: Conn, eidAny: Any)
  extends Packed2EntityMap(conn) with DatomicEntity {

  val rpc = conn.rpc


  final def retract(implicit ec: ExecutionContext): Future[TxReport] = withEid(eid =>
    rpc.transact(conn.connProxy, Stmts2Edn(List(RetractEntity(eid)), conn))
  )

  final def retract(txMeta1: Molecule, txMetaMore: Molecule*)
                   (implicit ec: ExecutionContext): Future[TxReport] = withEid { eid =>
    val retractStmt     = RetractEntity(eid)
    val txMetaDataModel = Model((txMeta1 +: txMetaMore).map(m => TxMetaData(m._model.elements)))
    try {
      VerifyModel(txMetaDataModel, "save") // can throw exception
      for {
        txMetaStmts <- conn.model2stmts(txMetaDataModel).saveStmts
        txReport <- rpc.transact(conn.connProxy, Stmts2Edn(retractStmt +: txMetaStmts, conn))
      } yield txReport
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }
  }

  final def getRetractStmts(implicit ec: ExecutionContext): Future[Seq[Statement]] = withEid(eid =>
    Future(List(RetractEntity(eid)))
  )

  final def getRetractStmts(txMeta1: Molecule, txMetaMore: Molecule*)
                           (implicit ec: ExecutionContext): Future[Seq[Statement]] = withEid { eid =>
    val retractStmt     = RetractEntity(eid)
    val txMetaDataModel = Model((txMeta1 +: txMetaMore).map(m => TxMetaData(m._model.elements)))
    try {
      VerifyModel(txMetaDataModel, "save") // can throw exception
      conn.model2stmts(txMetaDataModel).saveStmts.map(txMetaStmts => retractStmt +: txMetaStmts)
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }
  }


  final def inspectRetract(implicit ec: ExecutionContext): Future[Unit] = {
    getRetractStmts.map { stmts =>
      conn.inspect("Inspect `retract` on entity", 1)(1, stmts)
    }
  }

  final def inspectRetract(txMeta1: Molecule, txMetaMore: Molecule*)
                          (implicit ec: ExecutionContext): Future[Unit] = withEid { eid =>
    val retractStmt     = RetractEntity(eid)
    val txMetaDataModel = Model((txMeta1 +: txMetaMore).map(m => TxMetaData(m._model.elements)))
    try {
      VerifyModel(txMetaDataModel, "save") // can throw exception
      conn.model2stmts(txMetaDataModel).saveStmts.map(txMetaStmts =>
        conn.inspect(
          "Inspect `retract` on entity with tx meta data", 1
        )(1, retractStmt +: txMetaStmts)
      )
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }
  }


  // Entity api -------------------------------------------------

  final def attrs(implicit ec: ExecutionContext): Future[List[String]] = withEid(eid =>
    rpc.attrs(conn.connProxy, eid)
  )

  final def apply[T](attr: String)(implicit ec: ExecutionContext): Future[Option[T]] = withEid { eid =>
    rpc.apply(conn.connProxy, eid, attr).flatMap { packed =>
      try {
        val typedValue = attrDefinitions.get(attr).fold(
          throw MoleculeException(s"Attribute `$attr` not found in schema.")
        ) {
          case (_, tpe) => packed match {
            case "" => getTypedNone(tpe).asInstanceOf[Option[T]]
            case _  => Some(packed2entityMap(packed).head._2.asInstanceOf[T])
          }
        }
        Future(typedValue)
      } catch {
        case NonFatal(exc) => Future.failed(exc)
      }
    }
  }

  final def apply(attr1: String, attr2: String, moreAttrs: String*)
                 (implicit ec: ExecutionContext): Future[List[Option[Any]]] = withEid { eid =>
    val attrs = (attr1 +: attr2 +: moreAttrs).toList
    rpc.apply(conn.connProxy, eid, attrs).flatMap { packs =>
      try {
        val typedValues = attrs.zip(packs).map { case (attr, packed) =>
          attrDefinitions.get(attr).fold(
            throw MoleculeException(s"Attribute `$attr` not found in schema.")
          ) {
            case (_, tpe) => packed match {
              case "" => getTypedNone(tpe)
              case _  => Some(packed2entityMap(packed).head._2)
            }
          }
        }
        Future(typedValues)
      } catch {
        case NonFatal(exc) => Future.failed(exc)
      }
    }
  }

  final def graph(implicit ec: ExecutionContext): Future[Map[String, Any]] = withEid(eid =>
    rpc.graphDepth(conn.connProxy, eid, 5).map(packed2entityMap)
  )

  final def graphDepth(maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] = withEid(eid =>
    rpc.graphDepth(conn.connProxy, eid, maxDepth).map(packed2entityMap)
  )

  final def inspectGraph(implicit ec: ExecutionContext): Future[Unit] = {
    graphCode(5).map(println)
  }

  final def inspectGraphDepth(maxDepth: Int)(implicit ec: ExecutionContext): Future[Unit] = {
    graphCode(maxDepth).map(println)
  }

  final def graphCode(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] = withEid(eid =>
    rpc.graphCode(conn.connProxy, eid, maxDepth)
  )


  // Internal --------------------------------------------------

  private final def withEid[T](body: Long => Future[T]): Future[T] = try {
    eidAny match {
      case eidLong: Long => body(eidLong)
      case _             => throw MoleculeException(s"Expected Long eid. Found `$eidAny` of type " + eidAny.getClass)
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }
}
