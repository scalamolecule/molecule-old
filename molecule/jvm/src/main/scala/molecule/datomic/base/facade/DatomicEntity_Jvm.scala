package molecule.datomic.base.facade

import java.util.{Collection => jCollection}
import molecule.core.api.Molecule
import molecule.core.ast.elements.{Model, TxMetaData}
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.Serializations
import molecule.core.marshalling.unpackers.Packed2EntityMap
import molecule.core.ops.VerifyModel
import molecule.core.util.{Helpers, JavaConversions, Quoted}
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.transactionModel.RetractEntity
import molecule.datomic.base.util.Inspect
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}


abstract class DatomicEntity_Jvm(conn: Conn, eid: Any) extends Packed2EntityMap(conn)
  with DatomicEntity with Quoted with Helpers with Serializations with JavaConversions {


  final override def apply[T](attr: String)(implicit ec: ExecutionContext): Future[Option[T]] = {
    attrDefinitions.get(attr).fold(
      Future.failed[Option[T]](MoleculeException(s"Attribute `$attr` not found in schema."))
    ) { case (card, tpe) =>
      rawValue(attr).flatMap {
        case None                    => Future(None)
        case Some(v: List[_])        => Future(Some(v.toSet.asInstanceOf[T]))
        case Some(v)                 => Future(Some(v.asInstanceOf[T]))
        case null                    => Future(Option.empty[T])
        case results: jCollection[_] =>
          results.toArray.apply(0) match {
            case _: datomic.Entity =>
              var list = List.empty[Future[Long]]
              results.forEach(e =>
                list = Future(e.asInstanceOf[datomic.Entity].get(":db/id").asInstanceOf[Long]) :: list
              )
              Future.sequence(list).map(l => Some(l.toSet.asInstanceOf[T]))

            case _ =>
              var list = List.empty[Future[Any]]
              results.forEach(v1 => list = list :+ toScala(attr, Some(v1)))
              Future.sequence(list).map { v =>
                getTypedValue(card, tpe, v) match {
                  case Success(v)   =>
                    Some(v.asInstanceOf[T])
                  case Failure(exc) => throw exc
                }
              }
          }

        case result => toScala(attr, Some(result)).map(v => Some(v.asInstanceOf[T]))
      }.recover {
        case _: NoSuchElementException => Option.empty[T]
      }
    }
  }

  final override def apply(attr1: String, attr2: String, moreAttrs: String*)
                          (implicit ec: ExecutionContext): Future[List[Option[Any]]] = {
    Future.sequence((attr1 +: attr2 +: moreAttrs.toList) map apply)
  }


  def retract(implicit ec: ExecutionContext): Future[TxReport] = {
    conn.transact(getRetractStmts)
  }

  def retract(txMeta: Molecule)
             (implicit ec: ExecutionContext): Future[TxReport] = try {
    val retractStmts = Seq(RetractEntity(eid))
    val model        = Model(Seq(TxMetaData(txMeta._model.elements)))
    VerifyModel(model, "save") // can throw exception
    conn.transact(
      conn.model2stmts(model).saveStmts.map(txMetaStmts => retractStmts ++ txMetaStmts)
    )
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def getRetractStmts(implicit ec: ExecutionContext): Future[List[RetractEntity]] = {
    Future(List(RetractEntity(eid)))
  }

  def inspectRetract(txMeta: Molecule)
                    (implicit ec: ExecutionContext): Future[Unit] = try {
    val retractStmts = Seq(RetractEntity(eid))
    val model        = Model(Seq(TxMetaData(txMeta._model.elements)))
    VerifyModel(model, "save") // can throw exception
    conn.model2stmts(model).saveStmts.map(txMetaStmts =>
      Inspect("Inspect `retract` on entity with tx meta data", 1)(1, retractStmts ++ txMetaStmts)
    )
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def inspectRetract(implicit ec: ExecutionContext): Future[Unit] = {
    getRetractStmts.map { stmts =>
      Inspect("Inspect `retract` on entity", 1)(1, stmts)
    }
  }


  // Entity graph -------------------------------------------------

  def graph(implicit ec: ExecutionContext): Future[Map[String, Any]] =
    asMap(1, 5)

  def graphDepth(maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] =
    asMap(1, maxDepth)

  def inspectGraph(implicit ec: ExecutionContext): Future[Unit] =
    graphCode(5).map(println)

  def inspectGraphDepth(maxDepth: Int)(implicit ec: ExecutionContext): Future[Unit] =
    graphCode(maxDepth).map(println)

  private[molecule] final override def graphCode(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] =
    asMap(1, maxDepth).map(quote(_))


  private[molecule] def toScala(
    attr: String,
    vOpt: Option[Any],
    depth: Int = 1,
    maxDepth: Int = 5,
    tpe: String = "Map"
  )(implicit ec: ExecutionContext): Future[Any]


  private[molecule] final override def asMap(
    depth: Int,
    maxDepth: Int
  )(implicit ec: ExecutionContext): Future[Map[String, Any]] = {
    attrs.flatMap {
      case Nil   => Future.failed(MoleculeException(s"Entity id $eid not found in database."))
      case attrs =>
        val attrsSorted  = attrs.filterNot(_ == ":db/id").sortWith((x, y) => x.toLowerCase < y.toLowerCase)
        val valueFutures = attrsSorted.map { attr =>
          val (_, tpe) = attrDefinitions.getOrElse(attr, (0, ""))
          toScala(attr, None, depth, maxDepth).map { scalaValue =>
            val sortedValue = scalaValue match {
              case l: Seq[_] => l.head match {
                case m1: Map[_, _]
                  if m1.asInstanceOf[Map[String, Any]].isDefinedAt(":db/id") =>
                  val indexedRefMaps: Seq[(Long, Map[String, Any])] = l.map {
                    case m2: Map[_, _] =>
                      m2.asInstanceOf[Map[String, Any]]
                        .apply(":db/id").asInstanceOf[Long] ->
                        m2.asInstanceOf[Map[String, Any]]
                  }
                  indexedRefMaps.map(_._2).toSet

                case _ => l
              }

              case ident: String if tpe == "ref" =>
                // Failed Future for ref ids interpreted as ident
                throw new NumberFormatException(s"""For input string: "$ident"""")

              case v => v
            }
            attr -> sortedValue
          }
        }
        // Id first
        Future.sequence(rawValue(":db/id").map(":db/id" -> _) +: valueFutures).map(_.toMap)
    }
  }

  private[molecule] final override def asList(
    depth: Int,
    maxDepth: Int
  )(implicit ec: ExecutionContext): Future[List[(String, Any)]] = {
    attrs.flatMap {
      case Nil   => Future.failed(MoleculeException(s"Entity id $eid not found in database."))
      case attrs =>
        val attrsSorted  = attrs.filterNot(_ == ":db/id").sortWith((x, y) => x.toLowerCase < y.toLowerCase)
        val valueFutures = attrsSorted.map { attr =>
          toScala(attr, None, depth, maxDepth, "List").map { scalaValue =>
            val sortedValue = scalaValue match {
              case l: Seq[_] => l.head match {
                case l0: Seq[_] => l0.head match {
                  case _: (_, _) => // Now we now we have a Seq of Seq with pairs
                    // Make typed Seq
                    val typedSeq: Seq[Seq[(String, Any)]] = l.collect {
                      case l1: Seq[_] => l1.collect {
                        case (k: String, v) =>
                          (k, v)
                      }
                    }
                    if (typedSeq.head.map(_._1).contains(":db/id")) {
                      // We now know we have :db/id's to sort on
                      val indexedRefLists: Seq[(Long, Seq[(String, Any)])] = typedSeq.map {
                        subSeq => subSeq.toMap.apply(":db/id").asInstanceOf[Long] -> subSeq
                      }
                      // Sort sub Seq's by :db/id
                      indexedRefLists.sortBy(_._1).map(_._2).toSet

                    } else {
                      typedSeq.toSet
                    }

                  case other =>
                    // Propagates to failed Future
                    throw new Exception("Expected pairs of values. Found: " + other)
                }

                case _ => l
              }

              case v => v
            }
            attr -> sortedValue
          }
        }
        // Id first
        Future.sequence(rawValue(":db/id").map(":db/id" -> _) +: valueFutures)
    }
  }
}