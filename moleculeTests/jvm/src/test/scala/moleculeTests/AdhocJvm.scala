package moleculeTests

import molecule.core.util.Executor._
import molecule.core.util.Helpers
import molecule.core.util.testing.expectCompileError
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import molecule.datomic.api.out5._
import moleculeTests.setup.AsyncTestSuite
import utest._


object AdhocJvm extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {

    "adhocJvm" - core { implicit futConn =>

      for {
        conn <- futConn
        //        _ <- Ns.a.apply(min(2)).get

        _ <- Ns.str.insert(
          "a", "a",
          "b", "b", "b", "b", "b", "b", "b", "b", "b", "b",
        )
        _ <- Ns.str(count).d1.str.a2.inspectGet
        _ <- Ns.str(count).d1.str.a2.get.map(_ ==> List(
          (10, "b"),
          (2, "a"),
        ))

      } yield ()

    }


    //    "adhoc" - products { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
    //
    //      for {
    //
    //      } yield ()
    //    }


    //    "mbrainz" - mbrainz { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.mbrainz.dsl.MBrainz._
    //      val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
    //
    //      for {
    //        ledZeppelin <- Artist.e.gid_(ledZeppelinUUID).get
    //        mccartney <- Artist.e.gid_(UUID.fromString("ba550d0e-adac-4864-b88b-407cab5e76af")).get
    //        darkSideOfTheMoon <- Release.e.gid_(UUID.fromString("24824319-9bb8-3d1e-a2c5-b8b864dafd1b")).get
    //        dylanHarrisonSessions <- Release.e.gid_(UUID.fromString("67bbc160-ac45-4caf-baae-a7e9f5180429")).get
    //        concertForBangladesh <- Release.e.gid_(UUID.fromString("f3bdff34-9a85-4adc-a014-922eef9cdaa5")).get
    //        dylanHarrisonCd <- Release(dylanHarrisonSessions).media.get
    //        ghosotRiders <- Release(dylanHarrisonSessions).Media.Tracks.e.position_(11).get
    //        gb <- Country.e.name_("United Kingdom").get
    //        georgeHarrison <- Artist.e.name_("George Harrison").get
    //        bobDylan <- Artist.e.name_("Bob Dylan").get
    //
    //
    //      } yield ()
    //    }

    //
    //
    //    "adhoc" - bidirectional { implicit conn =>
    //      import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
    //
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //
    //
    //
    //      } yield ()
    //    }

  }
}
