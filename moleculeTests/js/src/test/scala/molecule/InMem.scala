package molecule

import java.util.UUID
import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy}
import molecule.datomic.api.out3._
import molecule.tests.core.base.dsl.CoreTest._
import molecule.tests.core.base.schema.CoreTestSchema
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

  def inMemConn = {
    val c = Conn_Js(DatomicInMemProxy(CoreTestSchema.datomicPeer, UUID.randomUUID()))
    println(c.dbProxy.uuid + "  ")
    c
  }


  lazy val tests = Tests {

    test("save and query 1") {
      implicit val conn = inMemConn
      Ns.int(1).saveAsync2.flatMap {
        case Right(tx) =>
          Ns.e.int.getAsync2.map {
            case Right(res) => res ==> List((tx.eid, 1))
          }
      }
    }

    test("save and query 2") {
      implicit val conn = inMemConn
      for {
        Right(tx) <- Ns.int(2).saveAsync2
        Right(res) <- Ns.e.int.getAsync2
      } yield {
        res ==> List((tx.eid, 2))
      }
    }


    test("Empty result set") {
      implicit val conn = inMemConn
      Ns.str.int.getAsync2.isEmpty
    }
  }
}
