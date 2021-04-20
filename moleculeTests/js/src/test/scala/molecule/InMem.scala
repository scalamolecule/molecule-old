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
    def left(expectedValue: String): Future[Unit] = eitherFuture.map {
      case Right(realValue) => realValue ==> expectedValue
      case Left(realValue)  => realValue ==> expectedValue
    }
  }


  lazy val tests = Tests {

    test("String-Int") {
      Ns.str.int.getAsync2 left "Empty result set"
    }
  }
}
