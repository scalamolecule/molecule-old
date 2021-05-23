package molecule.datomic.base.api


import java.net.URI
import java.util.{Date, UUID}
import clojure.lang.{Keyword, PersistentArrayMap}
import datomicClient.anomaly.Fault
import molecule.core.ast.Molecule
import molecule.core.ast.elements.{Model, TxMetaData}
import molecule.core.ops.VerifyModel
import molecule.core.util.{Helpers, Quoted}
import molecule.datomic.base.ast.transactionModel
import molecule.datomic.base.ast.transactionModel.{RetractEntity, Statement}
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.existentials
import scala.util.control.NonFatal


abstract class DatomicEntityImpl(conn: Conn, eid: Any) extends DatomicEntity with Quoted with Helpers {

  // Get ================================================================

  lazy val mapOneLevel: Future[Map[String, Any]] = {
    conn.q(s"[:find ?a1 ?v :where [$eid ?a ?v][?a :db/ident ?a1]]")
      .map(l => (l.head.toString, l(1)))
      .toMap + (":db/id" -> eid)
  }

  def entityMap(implicit ec: ExecutionContext): Future[Map[String, Any]] = {
    val res = try {
      var buildMap = Map.empty[String, Any]
      conn.db.pull("[*]", eid).forEach {
        case (k, v) => buildMap = buildMap + (k.toString -> v)
      }
      buildMap
    } catch {
      // Fetch top level only for cyclic graphs
      case _: StackOverflowError                                    =>
        mapOneLevel
      case Fault("java.lang.StackOverflowError with empty message") =>
        mapOneLevel

      case e: Throwable => throw e
    }
    res
  }

  def keySet(implicit ec: ExecutionContext): Future[Set[String]]

  def keys(implicit ec: ExecutionContext): Future[List[String]]

  def rawValue(key: String)(implicit ec: ExecutionContext): Future[Any]

  def apply[T](key: String)(implicit ec: ExecutionContext): Future[Option[T]] = {
    try {
      val rawV = rawValue(key)
      rawV match {
        case None    => None
        case Some(v) => Some(v.asInstanceOf[T])
        case null    => Option.empty[T]

        case results: clojure.lang.PersistentHashSet =>
          results.toArray.apply(0) match {
            case _: datomic.Entity =>
              var list = List.empty[Long]
              results.forEach(e =>
                list = e.asInstanceOf[datomic.Entity].get(":db/id").asInstanceOf[Long] :: list
              )
              Some(list.sorted.asInstanceOf[T])

            case _ =>
              var list = List.empty[Any]
              results.forEach(v1 =>
                list = list :+ toScala(key, Some(v1))
              )
              Some(list.asInstanceOf[T])
          }

        case result =>
          Some(toScala(key, Some(result)).asInstanceOf[T])
      }
    } catch {
      case _: NoSuchElementException => Option.empty[T]
    }
  }


  def apply(kw1: String, kw2: String, kws: String*)(implicit ec: ExecutionContext): Future[List[Option[Any]]] = {
    (kw1 +: kw2 +: kws.toList) map apply[Any]
  }


  // Retract =========================================================

  def retract(implicit ec: ExecutionContext): Future[TxReport] = {
    conn.transact(getRetractStmts)
  }

  def getRetractStmts(implicit ec: ExecutionContext): Future[List[RetractEntity]] = {
    Future(List(RetractEntity(eid)))
  }

  def inspectRetract(implicit ec: ExecutionContext): Future[Unit] = {
    getRetractStmts.map { stmts =>
      conn.inspect("Inspect `retract` on entity", 1)(1, stmts)
    }
  }

  def Tx(txMeta: Molecule)(implicit ec: ExecutionContext): RetractMolecule = RetractMoleculeImpl(txMeta)

  case class RetractMoleculeImpl(txMeta: Molecule)(implicit ec: ExecutionContext) extends RetractMolecule {
    private val stmts: Future[Seq[Statement]] = try {
      val retractStmts = Seq(RetractEntity(eid))
      val model        = Model(Seq(TxMetaData(txMeta._model.elements)))
      VerifyModel(model, "save") // can throw exception
      conn.modelTransformerAsync(model).saveStmts.map(txMetaStmts => retractStmts ++ txMetaStmts)
    } catch {
      case NonFatal(exc) => Future.failed(exc)
    }

    // todo: check that failed future propagates correctly
    def retract(implicit ec: ExecutionContext): Future[TxReport] = {
      conn.transact(stmts)
    }

    def inspectRetract: Unit = {
      conn.inspect("Inspect `retract` on entity with tx meta data", 1)(1, stmts)
    }
  }

  // Touch - traverse entity attributes ========================================

  def touch(implicit ec: ExecutionContext): Future[Map[String, Any]] = asMap(1, 5)

  def touchMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] = asMap(1, maxDepth)

  def touchQuoted(implicit ec: ExecutionContext): Future[String] = Future(quote(asMap(1, 5)))

  def touchQuotedMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] = Future(quote(asMap(1, maxDepth)))


  def touchList(implicit ec: ExecutionContext): Future[List[(String, Any)]] = asList(1, 5)

  def touchListMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[List[(String, Any)]] = asList(1, maxDepth)

  def touchListQuoted(implicit ec: ExecutionContext): Future[String] = Future(quote(asList(1, 5)))

  def touchListQuotedMax(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] = Future(quote(asList(1, maxDepth)))


  private[molecule] def toScala(
    key: String,
    vOpt: Option[Any],
    depth: Int = 1,
    maxDepth: Int = 5,
    tpe: String = "Map"
  ): Any


  protected def asMap(depth: Int, maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] = Future {
    val builder    = Map.newBuilder[String, Any]
    val keysSorted = keys.sortWith((x, y) => x.toLowerCase < y.toLowerCase)
    if (keysSorted.head != ":db/id")
      builder += ":db/id" -> rawValue(":db/id")
    keysSorted.foreach { key =>
      val scalaValue  = toScala(key, None, depth, maxDepth)
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
            indexedRefMaps.sortBy(_._1).map(_._2)

          case _ => l
        }
        case other     => other
      }
      builder += (key -> sortedValue)
    }
    builder.result()
  }


  protected def asList(depth: Int, maxDepth: Int)(implicit ec: ExecutionContext): Future[List[(String, Any)]] = Future {
    val builder    = List.newBuilder[(String, Any)]
    val keys2      = keys
    val keysSorted = keys2.sortWith((x, y) => x.toLowerCase < y.toLowerCase)
    if (keysSorted.head != ":db/id")
      builder += ":db/id" -> rawValue(":db/id")
    keysSorted.foreach { key =>
      val scalaValue  = toScala(key, None, depth, maxDepth, "List")
      val sortedValue = scalaValue match {
        case l: Seq[_] => l.head match {
          case l0: Seq[_] => l0.head match {
            case pair: (_, _) => // Now we now we have a Seq of Seq with pairs
              // Make typed Seq
              val typedSeq: Seq[Seq[(String, Any)]] = l.collect {
                case l1: Seq[_] => l1.collect {
                  case (k: String, v) => (k, v)
                }
              }
              if (typedSeq.head.map(_._1).contains(":db/id")) {
                // We now know we have :db/id's to sort on
                val indexedRefLists: Seq[(Long, Seq[(String, Any)])] = typedSeq.map {
                  subSeq => subSeq.toMap.apply(":db/id").asInstanceOf[Long] -> subSeq
                }
                // Sort sub Seq's by :db/id
                indexedRefLists.sortBy(_._1).map(_._2)
              } else {
                typedSeq
              }
          }
          case _          => l
        }
        case other     => other
      }
      builder += (key -> sortedValue)
    }
    builder.result()
  }


  lazy protected val ident = Keyword.intern("db", "ident")

  def sortList(l: List[Any])(implicit ec: ExecutionContext): Future[List[Any]] = Future {
    l.head match {
      case _: String => l.asInstanceOf[List[String]].sorted
      case _: Long   => l.asInstanceOf[List[Long]].sorted
      //    case _: Float                => l.asInstanceOf[List[Float]].sorted
      case _: Double               => l.asInstanceOf[List[Double]].sorted
      case _: Boolean              => l.asInstanceOf[List[Boolean]].sorted
      case _: Date                 => l.asInstanceOf[List[Date]].sorted
      case _: UUID                 => l.asInstanceOf[List[UUID]].sorted
      case _: URI                  => l.asInstanceOf[List[URI]].sorted
      case _: java.math.BigInteger => l.asInstanceOf[List[java.math.BigInteger]].map(BigInt(_)).sorted
      case _: java.math.BigDecimal => l.asInstanceOf[List[java.math.BigDecimal]].map(BigDecimal(_)).sorted
      case _: BigInt               => l.asInstanceOf[List[BigInt]].sorted
      case _: BigDecimal           => l.asInstanceOf[List[BigDecimal]].sorted

      case m: PersistentArrayMap if m.containsKey(ident) =>
        l.asInstanceOf[List[PersistentArrayMap]].map(pam =>
          pam.get(ident).toString
        ).sorted

      case _ => l
    }
  }
}