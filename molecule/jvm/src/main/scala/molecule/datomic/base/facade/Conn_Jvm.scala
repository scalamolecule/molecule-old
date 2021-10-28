package molecule.datomic.base.facade

import java.io.{Reader, StringReader}
import java.net.URI
import java.util.{Collections, Date, Collection => jCollection, List => jList, Map => jMap}
import clojure.lang.{PersistentArrayMap, PersistentVector}
import com.cognitect.transit.impl.URIImpl
import datomic.Peer.function
import datomic.Util.{list, read, readAll}
import datomic.{Peer, Util}
import molecule.core.util.JavaConversions
import molecule.datomic.base.ast.transactionModel.{Cas, Enum, RetractEntity, Statement, TempId}
import molecule.datomic.base.util.Inspect
import scala.concurrent.{ExecutionContext, Future}

private[molecule] trait Conn_Jvm extends Conn with JavaConversions {

  // Reset datoms of in-mem with-db from next timePoint after as-of t until end
  protected def cleanFrom(nextTimePoint: Any)(implicit ec: ExecutionContext): Future[Unit]


  def testDbAsOf(t: Long)(implicit ec: ExecutionContext): Future[Unit] = cleanFrom(t + 1)

  def testDbAsOf(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = cleanFrom(txR.t + 1)

  def testDbAsOf(d: Date)(implicit ec: ExecutionContext): Future[Unit] = {
    cleanFrom(new Date(d.toInstant.plusMillis(1).toEpochMilli))
  }


  def transact(edn: String)
              (implicit ec: ExecutionContext): Future[TxReport] =
    transact(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]])

  override def transact(stmtsReader: Reader)
              (implicit ec: ExecutionContext): Future[TxReport] =
    transact(readAll(stmtsReader).get(0).asInstanceOf[jList[_]])

  override def transact(javaStmts: jList[_])(implicit ec: ExecutionContext): Future[TxReport] =
    transact(javaStmts, Future.successful(Seq.empty[Statement]))


  override def transact(edn: String, scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] =
    transact(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]], scalaStmts)

  override def transact(stmtsReader: Reader, scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] =
    transact(readAll(stmtsReader).get(0).asInstanceOf[jList[_]], scalaStmts)


  def transact(scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] = scalaStmts.flatMap { stmts =>
    transact(stmts2java(stmts), scalaStmts)
  }


  private[molecule] def getAttrValues(
    datalogQuery: String,
    card: Int,
    tpe: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    query(datalogQuery).map(_.map(_.head.toString))


  private[molecule] def getEntityAttrKeys(
    datalogQuery: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    query(datalogQuery).map(rows => rows.map(_.head.toString))


  override def query(datalogQuery: String, inputs: Any*)
           (implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = {
    rawQuery(datalogQuery, inputs.toSeq.asInstanceOf[Seq[AnyRef]]).map { raw =>
      if (raw.isInstanceOf[PersistentVector]
        && !raw.asInstanceOf[PersistentVector].isEmpty
        && raw.asInstanceOf[PersistentVector].nth(0).isInstanceOf[PersistentArrayMap]) {
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


  private[molecule] override def buildTxFnInstall(txFnDatomic: String, args: Seq[Any]): jList[_] = {
    val params = args.indices.map(i => ('a' + i).toChar.toString)
    Util.list(Util.map(
      read(":db/ident"), read(s":$txFnDatomic"),
      read(":db/fn"), function(Util.map(
        read(":lang"), "java",
        read(":params"), list(read("txDb") +: read("txMetaData") +: params.map(read): _*),
        read(":code"), s"return $txFnDatomic(txDb, txMetaData, ${params.mkString(", ")});"
      ))
    ))
  }


  override def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = {
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

  override def inspect(
    header: String,
    threshold: Int,
    max: Int = 9999,
    showStackTrace: Boolean = false,
    maxLevel: Int = 99,
    showBi: Boolean = false
  )(id: Int, params: Any*): Unit =
    Inspect(header, threshold, max, showStackTrace, maxLevel, showBi)(id, params: _*)
}
