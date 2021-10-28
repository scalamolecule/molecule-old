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
import scala.util.{Failure, Success}
import scala.util.control.NonFatal

case class DatomicEntity_Js(conn: Conn, eidAny: Any) extends Packed2EntityMap(conn) with DatomicEntity {
  val rpc = conn.rpc


  final def retract(implicit ec: ExecutionContext): Future[TxReport] = withEid(eid =>
    rpc.transact(conn.connProxy, Stmts2Edn(List(RetractEntity(eid)), conn))
  )

  final def retract(txMeta: Molecule)
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

  final def getRetractStmts(implicit ec: ExecutionContext): Future[List[RetractEntity]] = withEid(eid =>
    Future(List(RetractEntity(eid)))
  )


  final def inspectRetract(implicit ec: ExecutionContext): Future[Unit] = {
    getRetractStmts.map { stmts =>
      conn.inspect("Inspect `retract` on entity", 1)(1, stmts)
    }
  }

  final def inspectRetract(txMeta: Molecule)(implicit ec: ExecutionContext): Future[Unit] = withEid { eid =>
    val retractStmts = Seq(RetractEntity(eid))
    val model        = Model(Seq(TxMetaData(txMeta._model.elements)))
    try {
      VerifyModel(model, "save") // can throw exception
      conn.model2stmts(model).saveStmts.map(txMetaStmts =>
        conn.inspect(
          "Inspect `retract` on entity with tx meta data", 1
        )(1, retractStmts ++ txMetaStmts)
      )
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }
  }


  private[molecule] final def rawValue(attr: String)(implicit ec: ExecutionContext): Future[Any] = withEid(eid =>
    rpc.rawValue(conn.connProxy, eid, attr).map(packed => packed2entityList(packed).head._2)
  )

  final def attrs(implicit ec: ExecutionContext): Future[List[String]] = withEid(eid =>
    rpc.attrs(conn.connProxy, eid)
  )

  final def apply[T](attr: String)(implicit ec: ExecutionContext): Future[Option[T]] = withEid { eid =>
    rpc.apply(conn.connProxy, eid, attr).flatMap { packed =>
      try {
        val typedValue = attrDefinitions.get(attr).fold(
          throw MoleculeException(s"Attribute `$attr` not found in schema.")
        ) {
          case (card, tpe) => packed match {
            case "" => getTypedNone(tpe).asInstanceOf[Option[T]]
            case _  => tpe match {
              case "ref" if !attr.contains("/_") => Some(packed2entityMap(packed).asInstanceOf[T])
              case _                             =>
                getTypedValue(card, tpe, packed2entityList(packed)(1)._2) match {
                  case Success(v)   => Some(v.asInstanceOf[T])
                  case Failure(exc) => throw exc
                }
            }
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
            case (card, tpe) => packed match {
              case "" => getTypedNone(tpe)
              case _  => tpe match {
                case "ref" if !attr.contains("/_") => Some(packed2entityMap(packed))
                case _                             =>
                  getTypedValue(card, tpe, packed2entityList(packed)(1)._2) match {
                    case Success(v)   => Some(v)
                    case Failure(exc) => throw exc
                  }
              }
            }
          }
        }
        Future(typedValues)
      } catch {
        case NonFatal(exc) => Future.failed(exc)
      }
    }
  }

  final def touch(implicit ec: ExecutionContext): Future[Map[String, Any]] = withEid(eid =>
    rpc.touchMax(conn.connProxy, eid, 5).map(packed2entityMap)
  )

  final def touchMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] = withEid(eid =>
    rpc.touchMax(conn.connProxy, eid, maxDepth).map(packed2entityMap)
  )

  final def touchQuoted(implicit ec: ExecutionContext): Future[String] = withEid(eid =>
    rpc.touchQuotedMax(conn.connProxy, eid, 5)
  )

  final def touchQuotedMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] = withEid(eid =>
    rpc.touchQuotedMax(conn.connProxy, eid, maxDepth)
  )

  final def touchList(implicit ec: ExecutionContext): Future[List[(String, Any)]] = withEid(eid =>
    rpc.touchListMax(conn.connProxy, eid, 5).map(packed2entityList)
  )

  final def touchListMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[List[(String, Any)]] = withEid(eid =>
    rpc.touchListMax(conn.connProxy, eid, maxDepth).map(packed2entityList)
  )

  final def touchListQuoted(implicit ec: ExecutionContext): Future[String] = withEid(eid =>
    rpc.touchListQuotedMax(conn.connProxy, eid, 5)
  )

  final def touchListQuotedMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] = withEid(eid =>
    rpc.touchListQuotedMax(conn.connProxy, eid, maxDepth)
  )

  final def asMap(depth: Int, maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] = withEid(eid =>
    rpc.asMap(conn.connProxy, eid, depth, maxDepth).map(packed2entityMap)
  )

  final def asList(depth: Int, maxDepth: Int)(implicit ec: ExecutionContext): Future[List[(String, Any)]] = withEid(eid =>
    rpc.asList(conn.connProxy, eid, depth, maxDepth).map(packed2entityList)
  )


  private final def withEid[T](body: Long => Future[T]): Future[T] = try {
    eidAny match {
      case eidLong: Long => body(eidLong)
      case _             => throw MoleculeException(s"Expected Long eid. Found `$eidAny` of type " + eidAny.getClass)
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }
}
