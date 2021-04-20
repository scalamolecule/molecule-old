package molecule

import molecule.core.marshalling.{ClientConn, DatomicInMemProxy, DatomicPeerProxy}
import molecule.datomic.api.out3._
import molecule.tests.core.base.dsl.CoreTest._
import molecule.tests.core.base.schema.CoreTestSchema
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object InMem extends TestSuite {

  implicit val conn = ClientConn(DatomicInMemProxy(CoreTestSchema.datomicPeer))

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

    test("Empty result set") {
      Ns.str.int.getAsync2.isEmpty
    }

//    test("Empty result set") {
//      Ns.int(1).saveAsync
//      Ns.str.int.getAsync2.isEmpty
//    }
  }
}
