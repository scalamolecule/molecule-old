package molecule.js.examples.datomic.mbrainz

import molecule.core.marshalling.{Conn_Js, DatomicPeerProxy}
import molecule.datomic.api.out3._
import molecule.tests.examples.datomic.mbrainz.dsl.MBrainz._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MBrainz extends TestSuite {

  implicit val conn = Conn_Js(DatomicPeerProxy("dev", "localhost:4334/mbrainz-1968-1973"))

//  implicit class testFutureEither[T](fut: Future[T])(implicit ex: ExecutionContext) {
  implicit class testFutureEither[T](fut: Future[T]) {
    def ===(expectedValue: T): Future[Unit] = fut.map(_ ==> expectedValue)
  }

  val tests = Tests {

    test("String-Int") {
      Artist.name.endYear.getAsync(2) === List(
        ("Dunn and McCashen", 1968),
        ("Brüder Grimm", 1863)
      )
    }


    test("Int-String") {
      Artist.endYear.name.getAsync.collect {
        case v => v.sorted.take(2) ==> List(
          (1672, "Heinrich Schütz"),
          (1741, "Antonio Vivaldi")
          //          (1980, "Bill Evans")
        )
      }
      Artist.endYear.name.getAsync(2) === List(
        (1976, "The Peddlers"),
        (1978, "Ralfi Pagán")
        //          (1980, "Bill Evans")
      )
    }

    test("String-String") {
      Artist.name.sortName.getAsync(2) === List(
        ("Rolf Lundqvist & Arbete & fritid", "Lundqvist, Rolf & Arbete & fritid"),
        ("Rusty York", "York, Rusty")
      )
    }

    //        test("String-String") {
    //          implicit val conn = ConnProxy(DatomicPeerProxy("mem", schema = Some(CoreTestSchema)))
    //          Artist.name.sortName.getAsync2(2) === List(
    //            ("Rolf Lundqvist & Arbete & fritid", "Lundqvist, Rolf & Arbete & fritid"),
    //            ("Rusty York", "York, Rusty")
    //          )
    //        }
  }
}
