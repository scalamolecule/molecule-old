package molecule.datomic.base.facade

import java.io.{Reader, StringReader}
import java.util.{Collections, Date, Collection => jCollection, List => jList}
import datomic.Peer.function
import datomic.Util.{list, read, readAll}
import datomic.{Peer, Util}
import molecule.datomic.base.ast.tempDb.TempDb
import molecule.datomic.base.ast.transactionModel.{Cas, Enum, RetractEntity, Statement, TempId}
import molecule.datomic.base.util.Inspect
import scala.concurrent.{ExecutionContext, Future}

trait Conn_Datomic extends Conn {

  val isJsPlatform: Boolean = false

  // Temporary db for ad-hoc queries against time variation dbs
  // (takes precedence over test db)
  protected var _adhocDb: Option[TempDb] = None

  protected def cleanFrom(nextTimePoint: Any): Unit

  def testDbAsOf(txR: TxReport): Unit = cleanFrom(txR.t + 1)

  def testDbAsOf(tOrTx: Long): Unit = cleanFrom(tOrTx + 1)

  def testDbAsOf(d: Date): Unit = {
    cleanFrom(new Date(d.toInstant.plusMillis(1).toEpochMilli))
  }


  def transact(stmtsReader: Reader, scalaStmts: Seq[Statement]): TxReport =
    transactRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]], scalaStmts)

  def transact(edn: String, scalaStmts: Seq[Statement]): TxReport =
    transactRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]], scalaStmts)

  def transact(stmtsReader: Reader): TxReport =
    transactRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]])

  def transact(edn: String): TxReport =
    transactRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]])

  def transact(scalaStmts: Seq[Statement]): TxReport =
    transactRaw(stmts2java(scalaStmts), scalaStmts)


  def transactAsync(stmtsReader: Reader, scalaStmts: Seq[Statement])
                   (implicit ec: ExecutionContext): Future[Either[String, TxReport]] =
    transactAsyncRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]], scalaStmts)

  def transactAsync(edn: String, scalaStmts: Seq[Statement])
                   (implicit ec: ExecutionContext): Future[Either[String, TxReport]] =
    transactAsyncRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]], scalaStmts)

  def transactAsync(stmtsReader: Reader)
                   (implicit ec: ExecutionContext): Future[Either[String, TxReport]] =
    transactAsyncRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]])

  def transactAsync(edn: String)
                   (implicit ec: ExecutionContext): Future[Either[String, TxReport]] =
    transactAsyncRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]])

  def transactAsync(scalaStmts: Seq[Statement])
                   (implicit ec: ExecutionContext): Future[Either[String, TxReport]] =
    transactAsyncRaw(stmts2java(scalaStmts), scalaStmts)


  override def getAttrValuesAsync(
    datalogQuery: String,
    card: Int,
    tpe: String
  )(implicit ec: ExecutionContext): Future[List[String]] = Future(
    q(datalogQuery).map(_.head.toString)
  )

  def q(query: String, inputs: Any*): List[List[AnyRef]] =
    q(db, query, inputs.toSeq)

  def qRaw(query: String, inputs: Any*): jCollection[jList[AnyRef]] =
    qRaw(db, query, inputs)


  def buildTxFnInstall(txFn: String, args: Seq[Any]): jList[_] = {
    val params = args.indices.map(i => ('a' + i).toChar.toString)
    Util.list(Util.map(
      read(":db/ident"), read(s":$txFn"),
      read(":db/fn"), function(Util.map(
        read(":lang"), "java",
        read(":params"), list(read("txDb") +: read("txMetaData") +: params.map(read): _*),
        read(":code"), s"return $txFn(txDb, txMetaData, ${params.mkString(", ")});"
      ))
    ))
  }


  def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = {
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
    }).asInstanceOf[AnyRef]

    def value(v: Any): AnyRef = (v match {
      case i: Int             => i.toLong
      case f: Float           => f.toDouble
      case TempId(part, i)    => getTempId(part, i)
      case Enum(prefix, enum) => prefix + enum
      case bigInt: BigInt     => bigInt.bigInteger
      case bigDec: BigDecimal => bigDec.bigDecimal
      case other              => other
    }).asInstanceOf[AnyRef]

    val list: jList[jList[_]] = new java.util.ArrayList[jList[_]](stmts.length)
    stmts.foreach {
      case s: RetractEntity =>
        list.add(Util.list(s.action, s.e.asInstanceOf[AnyRef]))
      case s: Cas           =>
        list.add(Util.list(s.action, s.e.asInstanceOf[AnyRef], s.a, value(s.oldV), value(s.v)))
      case s                =>
        list.add(Util.list(s.action, eid(s.e), s.a, value(s.v)))
    }
    Collections.unmodifiableList(list)
  }

  def inspect(
    clazz: String,
    threshold: Int,
    max: Int = 9999,
    showStackTrace: Boolean = false,
    maxLevel: Int = 99,
    showBi: Boolean = false
  )(id: Int, params: Any*): Unit =
    Inspect(clazz, threshold, max, showStackTrace, maxLevel, showBi)(id, params: _*)
}