package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.lang.{Long => jLong}
import java.util.{Collections, Date, UUID, ArrayList => jArrayList, Comparator => jComparator, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.ast.elements._
import molecule.core.marshalling.unpack.UnpackTypes
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import moleculeTests.tests.core.base.schema.CoreTestSchema
import scala.util.control.NonFatal


object Adhoc extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {

    "core" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn


        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1$) insert List(
          (10, List((1, Some(Set("a", "b"))), (2, None))),
          (20, List())
        )

//        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1$).get.map(_ ==> List(
//          (10, List((1, Some(Set("a", "b"))), (2, None))),
//        ))
//        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1).get.map(_ ==> List(
//          (10, List((1, Set("a", "b")))),
//        ))
//        _ <- m(Ns.int.Refs1 * Ref1.int1.strs1_).get.map(_ ==> List(
//          (10, List(1)),
//        ))
//
//        _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1$).get.map(_.sortBy(_._1) ==> List(
//          (10, List((1, Some(Set("a", "b"))), (2, None))),
//          (20, List())
//        ))
//
//        _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1).get.map(_.sortBy(_._1) ==> List(
//          (10, List((1, Set("a", "b")))),
//          (20, List())
//        ))

        _ <- m(Ns.int.Refs1 *? Ref1.int1.strs1_).get.map(_.sortBy(_._1) ==> List(
          (10, List(1)),
          (20, List())
        ))

      } yield ()
    }


    //    "adhoc" - products { implicit conn =>
    //      import moleculeTests.tests.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
    //
    //      for {
    //
    //      } yield ()
    //    }


    //    "mbrainz" - mbrainz { implicit conn =>
    //      import moleculeTests.tests.examples.datomic.mbrainz.dsl.MBrainz._
    //      val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
    //
    //      for {
    //        ledZeppelin <- Artist.e.gid_(ledZeppelinUUID).get
    //        mccartney <- Artist.e.gid_(UUID.fromString("ba550d0e-adac-4864-b88b-407cab5e76af")).get
    //        darkSideOfTheMoon <- Release.e.gid_(UUID.fromString("24824319-9bb8-3d1e-a2c5-b8b864dafd1b")).get
    //        dylanHarrisonSessions <- Release.e.gid_(UUID.fromString("67bbc160-ac45-4caf-baae-a7e9f5180429")).get
    //        concertForBangladesh <- Release.e.gid_(UUID.fromString("f3bdff34-9a85-4adc-a014-922eef9cdaa5")).get
    //        dylanHarrisonCd <- Release(dylanHarrisonSessions).media.get
    //        ghostRiders <- Release(dylanHarrisonSessions).Media.Tracks.e.position_(11).get
    //        gb <- Country.e.name_("United Kingdom").get
    //        georgeHarrison <- Artist.e.name_("George Harrison").get
    //        bobDylan <- Artist.e.name_("Bob Dylan").get
    //
    //
    //      } yield ()
    //    }


    //    "adhoc" - bidirectional { implicit conn =>
    //    for {
    //      _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //    } yield ()
    //  }
  }
}
