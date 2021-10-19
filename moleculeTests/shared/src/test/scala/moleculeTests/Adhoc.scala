package moleculeTests

import molecule.core.util.Helpers
import molecule.datomic.api.in3_out12._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global


object Adhoc extends AsyncTestSuite with Helpers {

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "adhoc" - core { implicit futConn =>
      import moleculeTests.dataModels.core.base.dsl.CoreTest._
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn
        //        _ <- Ns.int.apply(1).asc1.str.desc2.get
        //        _ <- Ns.int.not(1).asc1.str.desc2.get

//        _ <- Ns.int(1).save
//        _ <- Ns.int.get.map(_ ==> List(1))

//        txR1 <- Ns.int.Tx(Ref2.str2_("a")) insert List(1, 2, 3)
//        tx1 = txR1.tx
//        t1 = txR1.t
//        List(e1, e2, e3) = txR1.eids

        txR1 <- Ns.int.Tx(Ns.str_("a").Ref1.int1_(7)) insert List(1, 2, 3)
        List(e1, e2, e3) = txR1.eids

      } yield ()
    }
/*

[error] 	moleculeTests.tests.core.bidirectionals.edgeOther.EdgeManyOtherInsert
[error] 	moleculeTests.tests.core.bidirectionals.edgeSelf.EdgeOneSelfInsert
[error] 	moleculeTests.tests.examples.gremlin.gettingStarted.Friends2
[error] 	moleculeTests.tests.core.bidirectionals.edgeSelf.EdgeOneSelfSave
[error] 	moleculeTests.tests.core.bidirectionals.edgeOther.EdgeManyOtherSave
[error] 	moleculeTests.tests.examples.datomic.dayOfDatomic.QueryTour
[error] 	moleculeTests.tests.examples.datomic.dayOfDatomic.Provenance
[error] 	moleculeTests.tests.core.nested.NestedRef
[error] 	moleculeTests.tests.core.bidirectionals.edgeOther.EdgeManyOtherUpdateProps
[error] 	moleculeTests.tests.core.bidirectionals.edgeOther.EdgeOneOtherInsert
[error] 	moleculeTests.tests.core.composite.CompositeArities
[error] 	moleculeTests.tests.core.bidirectionals.edgeSelf.EdgeManySelfInsert
[error] 	moleculeTests.tests.core.txMetaData.MetaRetract
 */
    //    "adhoc2" - core { implicit futConn =>
    //
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //        conn <- futConn
    ////        _ <- Ns.int.apply(1).asc1.str.desc2.get
    ////        _ <- Ns.int.not(1).asc1.str.desc2.get
    //
    //        tx <- Ns.int(2).save
    ////        _ = println("tx2: " + tx)
    //        _ <- Ns.int.get.map(_ ==> List(2))
    //      } yield ()
    //    }

    //    "core2" - core { implicit futConn =>
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //        conn <- futConn
    //
    //
    //      } yield ()
    //    }
    //
    //    "adhoc" - products { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
    //
    //      for {
    //
    //      } yield ()
    //    }
    //

    //        "adhoc" - socialNews { implicit conn =>
    //          import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.SocialNews._
    //
    //          for {
    //
    //
    //          } yield ()
    //        }
    //
    //
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
    //        ghostRiders <- Release(dylanHarrisonSessions).Media.Tracks.e.position_(11).get
    //        gb <- Country.e.name_("United Kingdom").get
    //        georgeHarrison <- Artist.e.name_("George Harrison").get
    //        bobDylan <- Artist.e.name_("Bob Dylan").get
    //
    //
    //      } yield ()
    //    }
    //
    //
    //        "adhoc" - bidirectional { implicit conn =>
    //          import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
    //
    //          for {
    //            _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //
    //          } yield ()
    //        }
    //
    //

    //    "adhoc" - seattle { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.seattle.dsl.Seattle._
    //
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //        _ <- Community.name.url.tpe.orgtype$.category$.Neighborhood.name.District.name.region$ insert seattleData
    //
    //
    //
    //      } yield ()
    //    }
  }
}
