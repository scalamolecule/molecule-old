package molecule.datomic.base.facade

import java.io.{Reader, StringReader}
import java.util.{Collections, Date, Collection => jCollection, List => jList}
import datomic.Peer.function
import datomic.Util.{list, read, readAll}
import datomic.{Peer, Util}
import molecule.datomic.base.ast.tempDb.{TempDb, With}
import molecule.datomic.base.ast.transactionModel.{Cas, Enum, RetractEntity, Statement, TempId}
import molecule.datomic.base.util.Inspect
import scala.concurrent.{ExecutionContext, Future}

trait Conn_Datomic extends Conn {

  val isJsPlatform: Boolean = false

  // Temporary db for ad-hoc queries against time variation dbs
  // (takes precedence over test db)
  protected var _adhocDb: Option[TempDb] = None

  // Reset datoms of in-mem with-db from next timePoint after as-of t until end
  protected def cleanFrom(nextTimePoint: Any)(implicit ec: ExecutionContext): Future[Unit]



  def testDbAsOf(t: Long)(implicit ec: ExecutionContext): Future[Unit] = cleanFrom(t + 1)

  def testDbAsOf(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] = cleanFrom(txR.t + 1)

  def testDbAsOf(d: Date)(implicit ec: ExecutionContext): Future[Unit] = {
    cleanFrom(new Date(d.toInstant.plusMillis(1).toEpochMilli))
  }


  def transact(stmtsReader: Reader, scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] =
    transactRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]], scalaStmts)

  def transact(edn: String, scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] =
    transactRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]], scalaStmts)

  def transact(stmtsReader: Reader)
              (implicit ec: ExecutionContext): Future[TxReport] =
    transactRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]])

  def transact(edn: String)
              (implicit ec: ExecutionContext): Future[TxReport] =
    transactRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]])

  def transact(scalaStmts: Future[Seq[Statement]])
              (implicit ec: ExecutionContext): Future[TxReport] = {
    scalaStmts.flatMap { stmts =>
      transactRaw(stmts2java(stmts), scalaStmts)
    }
  }


  override def jsGetAttrValues(
    datalogQuery: String,
    card: Int,
    tpe: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    q(datalogQuery).map(_.map(_.head.toString))


  def q(query: String, inputs: Any*)
       (implicit ec: ExecutionContext): Future[List[List[AnyRef]]] =
    q(db, query, inputs.toSeq)

  def qRaw(query: String, inputs: Any*)
          (implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] =
    qRaw(db, query, inputs)


  private[molecule] def buildTxFnInstall(txFn: String, args: Seq[Any]): jList[_] = {
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
      case i: Int => i.toLong
      //      case f: Float           => f.toDouble
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
