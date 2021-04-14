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

  val tests = Tests {
    test("first") {
      Artist.name.endYear.getAsync2(2).flatMap {
        case Right(res) => Future(res ==> List(("Ben", 42), ("Ann", 37)))
        case Left(err)  => Future(err ==> 7)
      }
    }
  }
}
