package molecule

import java.util
import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy}
import molecule.datomic.api.out3._
import molecule.datomic.base.facade.Conn
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.core.schemaDef.schema.PartitionTestSchema
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object InMem extends TestSuite {


  implicit class testFutureEither[T](eitherFuture: Future[Either[String, T]]) {
    def ===(expectedValue: T): Future[Unit] = eitherFuture.map {
      case Right(realValue) => realValue ==> expectedValue
      case Left(realValue)  => realValue ==> expectedValue
    }
    def isEmpty: Future[Unit] = eitherFuture.map {
      case Right(realValue) => realValue ==> "Empty result set"
      case Left(realValue)  => realValue ==> "Empty result set"
    }
  }


  lazy val tests = Tests {

//    test("Empty result set") {
//      Ns.str.int.getAsync2.isEmpty
//    }

    test("Empty result set") {
      import molecule.tests.core.schemaDef.dsl.PartitionTest._
      implicit val conn = Conn_Js(DatomicInMemProxy(PartitionTestSchema.datomicPeer))

      m(lit_Book.title("yeah")).inspectSave
      //      lit_Book.title("yeah").save

      //      gen_Person.name

      val edn =
        """[
          |[:db/add #db/id[:gen] :gen_Person/name "ben"]
          |[:db/add #db/id[:lit] :lit_Book/title "yeah"]
          |[:db/add #db/id[:lit] :lit_Book/author 42]
          |]
          |""".stripMargin


      val tx1 = conn.transact(edn)
      println(tx1)


      lit_Book.title.get.head ==> "yeah"
    }


    test("Empty result set") {
      import molecule.tests.core.base.dsl.CoreTest._
      implicit val conn = Conn_Js(DatomicInMemProxy(CoreTestSchema.datomicPeer))

      val edn =
        """[
          |{
          |
          |}
          |]
          |""".stripMargin


      Ns.int(1).saveAsync2.map{res =>
        println("@@@")
//        1 ==> 3
        res ==> 7
      }
//      Ns.int(1).saveAsync2.map{
//        case Right(txReport) => txReport.eids.size ==> 2
//      }
//      Ns.str.int.getAsync2.isEmpty

    }
  }
}
