package molecule.tests.js

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy}
import molecule.datomic.api.in1_out14._
import molecule.datomic.base.facade.TxReportProxy
import molecule.tests.core.base.dsl.CoreTest._
import molecule.tests.core.base.schema.CoreTestSchema
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object ValueTypes extends TestSuite {

  def inMemConn: Conn_Js = Conn_Js(
    DatomicInMemProxy(CoreTestSchema.datomicPeer, UUID.randomUUID().toString)
  )

  val date1   = new Date(0)
  val uuid1   = UUID.randomUUID()
  val uri1    = new URI("uri1")
  val bigInt1 = BigInt(1)
  val bigDec1 = BigDecimal(1.1)

  type Transact = Future[Either[String, TxReportProxy]]
  type Query[T] = Future[Either[String, List[T]]]

  def saveGet[T](save: => Transact, get: => Query[T], v: T)(implicit conn: Conn_Js) = {
    save.flatMap {
      case Right(txReportProxy) =>
        get.map {
          res => res ==> Right(List(v))
        }
      case Left(txErr)          =>
        println(txErr)
        throw new RuntimeException(txErr)
    }
  }

  implicit val conn = inMemConn
  Log.a.clearCache

  

  lazy val tests = Tests {

    test("card one") {
      implicit val conn = inMemConn
      test("str   ")(saveGet(Ns.str("a").saveAsync2, Ns.str.getAsync2, "a"))
      test("int   ")(saveGet(Ns.int(1).saveAsync2, Ns.int.getAsync2, 1))
      test("float ")(saveGet(Ns.float(1.1f).saveAsync2, Ns.float.getAsync2, 1.1f))
      test("long  ")(saveGet(Ns.long(2L).saveAsync2, Ns.long.getAsync2, 2L))
      test("double")(saveGet(Ns.double(2.2).saveAsync2, Ns.double.getAsync2, 2.2))
      test("bool  ")(saveGet(Ns.bool(true).saveAsync2, Ns.bool.getAsync2, true))
      test("date  ")(saveGet(Ns.date(date1).saveAsync2, Ns.date.getAsync2, date1))
      test("uuid  ")(saveGet(Ns.uuid(uuid1).saveAsync2, Ns.uuid.getAsync2, uuid1))
      test("uri   ")(saveGet(Ns.uri(uri1).saveAsync2, Ns.uri.getAsync2, uri1))
      test("bigInt")(saveGet(Ns.bigInt(bigInt1).saveAsync2, Ns.bigInt.getAsync2, bigInt1))
      test("bigDec")(saveGet(Ns.bigDec(bigDec1).saveAsync2, Ns.bigDec.getAsync2, bigDec1))
      test("enum  ")(saveGet(Ns.enum("enum1").saveAsync2, Ns.enum.getAsync2, "enum1"))
    }


    //    test("card one") {
    //      implicit val conn = inMemConn
    //      for {
    //        Right(tx) <- Ns
    //          .str("a")
    //          .int(1)
    //          .float(1.1f)
    //          .long(1L)
    //          .double(1.1)
    //          .bool(true)
    //          .date(date1)
    //          .uuid(uuid1)
    //          .uri(uri1)
    //          //          .bigInt(bigInt1)
    //          //          .bigDec(bigDec1)
    //          //          .enum("enum1")
    //          .saveAsync2
    //        res1 <- Ns.str.getAsync2
    //        res2 <- Ns.int.getAsync2
    //        res3 <- Ns.float.getAsync2
    //        res4 <- Ns.long.getAsync2
    //        res5 <- Ns.double.getAsync2
    //        res6 <- Ns.bool.getAsync2
    //        res7 <- Ns.date.getAsync2
    //        res8 <- Ns.uuid.getAsync2
    //        res9 <- Ns.uri.getAsync2
    //        //        res10 <- Ns.bigInt.getAsync2
    //        //        res11 <- Ns.bigDec.getAsync2
    //        //        res12 <- Ns.enum.getAsync2
    //      } yield {
    //        res1 ==> Right(List("a"))
    //        res2 ==> Right(List(1))
    //        res3 ==> Right(List(1.1f))
    //        res4 ==> Right(List(1L))
    //        res5 ==> Right(List(1.1))
    //        res6 ==> Right(List(true))
    //        res7 ==> Right(List(date1))
    //        res8 ==> Right(List(uuid1))
    //        res9 ==> Right(List(uri1))
    //        //        res10 ==> Right(List(bigInt1))
    //        //        res11 ==> Right(List(bigDec1))
    //        //        res12 ==> Right(List("enum1"))
    //      }
    //    }
    //
    //    test("save and query 2") {
    //      implicit val conn = inMemConn
    //      for {
    //        Right(tx) <- Ns.int(2).str("b").saveAsync2
    //        Right(res1) <- Ns.e.int.getAsync2
    //        Right(res2) <- Ns.str.getAsync2
    //      } yield {
    //        res1 ==> List((tx.eid, 2))
    //        res2 ==> List("b")
    //      }
    //        }
    //    test("save and query 2") {
    //      implicit val conn = inMemConn
    //      Ns.uri(uri1).saveAsync2.flatMap {
    //        case Right(res) =>
    //          Ns.uri.getAsync2.map {
    //            case Right(res) =>
    //              res ==> List(uri1)
    //          }
    //      }
    //    }

  }
}
