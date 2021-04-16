package molecule
//import boopickle.Default.Pickle

import molecule.core.marshalling.{ConnProxy, DatomicPeerProxy}
import molecule.datomic.api.out3._
import molecule.tests.examples.datomic.mbrainz.dsl.MBrainz._
import molecule.tests.examples.datomic.mbrainz.schema.MBrainzSchema
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AdHocTestJs extends TestSuite {

  implicit val conn = ConnProxy(
    MBrainzSchema,
    DatomicPeerProxy("dev", "localhost:4334/mbrainz-1968-1973")
  )

  implicit class testFutureEither[T](eitherFuture: Future[Either[String, T]]) {
    def ===>(expectedValue: T): Future[Unit] = eitherFuture.map {
      case Right(realValue) => realValue ==> expectedValue
      case Left(realValue)  => realValue ==> expectedValue
    }
  }


  val tests = Tests {
    test("client query") {
      Artist.name.endYear.getAsync2(2) ===> List(
        ("Dunn and McCashen", 1968), ("Brüder Grimm", 1863)
      )
    }
  }
}
