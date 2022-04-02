package molecule.datomic.base.facade

import java.io.{Reader, StringReader}
import java.net.URI
import java.util.{Collections, Date, Collection => jCollection, List => jList, Map => jMap}
import clojure.lang.{PersistentArrayMap, PersistentVector}
import com.cognitect.transit.impl.URIImpl
import datomic.Util.{read, readAll}
import datomic.{Peer, Util}
import molecule.core.ast.elements._
import molecule.core.dto.SchemaAttr
import molecule.core.exceptions.MoleculeException
import molecule.core.ops.ModelOps
import molecule.core.util.{Helpers, JavaConversions}
import molecule.datomic.base.ast.transactionModel.{Cas, Enum, RetractEntity, Statement, TempId}
import molecule.datomic.base.ops.QueryOps.txBase
import molecule.datomic.base.transform.Model2Query
import molecule.datomic.base.util.Inspect
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait Conn_Jvm extends Conn with JavaConversions with Helpers with ModelOps with SchemaOps {

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

  def changeAttrName(curName: String, newName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    changeAttrName_(this, curName, newName)

  def retireAttr(attrName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    retireAttr_(this, attrName)

  def changeNamespaceName(curName: String, newName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    changeNamespaceName_(this, curName, newName)

  def retireNamespace(nsName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    retireNamespace_(this, nsName)

  def changePartitionName(curName: String, newName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    changePartitionName_(this, curName, newName)

  def retirePartition(partName: String)(implicit ec: ExecutionContext): Future[TxReport] =
    retirePartition_(this, partName)

  def retractSchemaOption(attr: String, option: String)(implicit ec: ExecutionContext): Future[TxReport] =
    retractSchemaOption_(this, attr, option)

  def getEnumHistory(implicit ec: ExecutionContext): Future[List[(String, Int, Long, Date, String, Boolean)]] =
    getEnumHistory_(this)

  def retractEnum(enumString: String)(implicit ec: ExecutionContext): Future[TxReport] =
    retractEnum_(this, enumString)


  private[molecule] def historyQuery(query: String, inputs: Seq[jList[AnyRef]] = Nil)
                                    (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]]

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

  private[molecule] final override def jvmSchemaHistoryQuery(
    model: Model
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    val schemaAttrs = model2schemaAttrs(model)
    val queryString = Model2Query(model, schemaHistory0 = true, optimize = false)._2
    QuerySchemaHistory(this).fetchSchemaHistory(schemaAttrs, queryString)
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
