package molecule.datomic.base.api


import java.net.URI
import java.util.{Date, UUID}
import clojure.lang.{Keyword, PersistentArrayMap}
import molecule.core.ast.Molecule
import molecule.core.ast.elements.{Model, TxMetaData}
import molecule.core.ops.VerifyModel
import molecule.core.util.Quoted
import molecule.datomic.base.ast.transactionModel.RetractEntity
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.language.existentials


abstract class DatomicEntityImpl(conn: Conn, eid: Any) extends DatomicEntity with Quoted {

  // Get ================================================================

  def keySet: Set[String]

  def keys: List[String]

  def rawValue(key: String): Any

  def apply[T](key: String): Option[T] = {
    try {
      val rawV = rawValue(key)
      rawV match {
        case None    => None
        case Some(v) => Some(v.asInstanceOf[T])
        case null    => Option.empty[T]

        case results: clojure.lang.PersistentHashSet =>
          results.asScala.head match {
            case _: datomic.Entity =>
              Some(results.asScala.toList
                .map(_.asInstanceOf[datomic.Entity].get(":db/id").asInstanceOf[Long])
                .sorted.asInstanceOf[T])

            case _ =>
              Some(results.asScala.toList.map(v1 =>
                toScala(key, Some(v1))).asInstanceOf[T]
              )
          }

        case result =>
          Some(toScala(key, Some(result)).asInstanceOf[T])
      }
    } catch {
      case _: NoSuchElementException => Option.empty[T]
    }
  }


  def apply(kw1: String, kw2: String, kws: String*): List[Option[Any]] = {
    (kw1 +: kw2 +: kws.toList) map apply[Any]
  }


  // Retract =========================================================

  def retract: TxReport = conn.transact(getRetractStmts)

  def retractAsync(implicit ec: ExecutionContext): Future[TxReport] =
    conn.transactAsync(getRetractStmts)

  def getRetractStmts: List[RetractEntity] = List(RetractEntity(eid))

  def inspectRetract: Unit = conn.inspect("Inspect `retract` on entity", 1)(1, getRetractStmts)

  def Tx(txMeta: Molecule): RetractMolecule = RetractMoleculeImpl(txMeta)

  case class RetractMoleculeImpl(txMeta: Molecule) extends RetractMolecule {
    private val retractStmts = Seq(RetractEntity(eid))

    private val _model = Model(Seq(TxMetaData(txMeta._model.elements)))
    VerifyModel(_model, "save")
    private val txMetaStmts = conn.modelTransformer(_model).saveStmts

    private val stmts = retractStmts ++ txMetaStmts

    def retract: TxReport = conn.transact(stmts)

    def retractAsync(implicit ec: ExecutionContext): Future[TxReport] =
      conn.transactAsync(stmts)

    def inspectRetract: Unit =
      conn.inspect("Inspect `retract` on entity with tx meta data", 1)(1, stmts)
  }

  // Touch - traverse entity attributes ========================================

  def touch: Map[String, Any] = asMap(1, 5)

  def touchMax(maxDepth: Int): Map[String, Any] = asMap(1, maxDepth)

  def touchQuoted: String = quote(asMap(1, 5))

  def touchQuotedMax(maxDepth: Int): String = quote(asMap(1, maxDepth))


  def touchList: List[(String, Any)] = asList(1, 5)

  def touchListMax(maxDepth: Int): List[(String, Any)] = asList(1, maxDepth)

  def touchListQuoted: String = quote(asList(1, 5))

  def touchListQuotedMax(maxDepth: Int): String = quote(asList(1, maxDepth))


  private[molecule] def toScala(
    key: String,
    vOpt: Option[Any],
    depth: Int = 1,
    maxDepth: Int = 5,
    tpe: String = "Map"
  ): Any


  protected def asMap(depth: Int, maxDepth: Int): Map[String, Any] = {
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


  protected def asList(depth: Int, maxDepth: Int): List[(String, Any)] = {
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

  def sortList(l: List[Any]): List[Any] = l.head match {
    case _: String               => l.asInstanceOf[List[String]].sorted
    case _: Long                 => l.asInstanceOf[List[Long]].sorted
    case _: Float                => l.asInstanceOf[List[Float]].sorted
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