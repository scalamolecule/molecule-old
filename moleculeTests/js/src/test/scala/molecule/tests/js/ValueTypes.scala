package molecule.tests.js

import java.util.UUID
import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy}
import molecule.datomic.api.in1_out14._
import molecule.datomic.base.facade.TxReportProxy
import molecule.setup.core.CoreData
import molecule.tests.core.base.dsl.CoreTest._
import molecule.tests.core.base.schema.CoreTestSchema
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object ValueTypes extends TestSuite with CoreData {

  def inMemConn: Conn_Js = Conn_Js(
    DatomicInMemProxy(CoreTestSchema.datomicPeer, UUID.randomUUID().toString)
  )

  def saveGet[T](
    save: => Future[Either[String, TxReportProxy]],
    get: => Future[Either[String, List[T]]],
    v: T
  )(implicit conn: Conn_Js): Future[Unit] = {
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

    test("for comprehension") {
      implicit val conn = inMemConn
      for {
        Right(tx) <- Ns.int(2).str("b").saveAsync2
        Right(res1) <- Ns.e.int.getAsync2
        Right(res2) <- Ns.e.str.getAsync2
      } yield {
        res1 ==> List((tx.eid, 2))
        res2 ==> List((tx.eid, "b"))
      }
    }
  }
}
