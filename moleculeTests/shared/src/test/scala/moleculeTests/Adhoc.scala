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

                _ <- Ns.int(1).save
                _ <- Ns.int.get.map(_ ==> List(1))


        _ = {
          conn
        }

//        _ <- Ns.str.int insert List(("a", 1), ("b", 2))
//
//        // Scala List of rows/Lists returned
//        _ <- conn.qRaw("[:find ?s :where [_ :Ns/str ?s]]").map(_ ==> List(
//          List("a"), // row 1
//          List("b"), // row 2
//        ))
//
//        _ <- conn.qRaw("[:find ?i :where [_ :Ns/int ?i]]").map(_ ==> List(List(1), List(2)))
//
//        _ <- conn.qRaw("[:find ?s ?i :where [?e :Ns/str ?s] [?e :Ns/int ?i]]").map(_ ==> List(
//          List("a", 1),
//          List("b", 2)
//        ))
//
//        _ <- conn.qRaw(
//          "[:find ?i :in $ ?s :where [?e :Ns/str ?s] [?e :Ns/int ?i]]",
//          "a"
//        ).map(_ ==> List(List(1)))
//
//        _ <- conn.qRaw(
//          "[:find ?s :in $ ?i :where [?e :Ns/str ?s] [?e :Ns/int ?i]]",
//          1
//        ).map(_ ==> List(List("a")))
//
//        _ <- conn.qRaw(
//          "[:find ?s ?i :in $ ?s ?i :where [?e :Ns/str ?s] [?e :Ns/int ?i]]",
//          "a", 1
//        ).map(_ ==> List(List("a", 1)))
//
//        _ <- conn.qRaw(
//          "[:find ?s ?i :in $ ?s ?i :where [?e :Ns/str ?s] [?e :Ns/int ?i]]",
//          "a", 42
//        ).map(_ ==> Nil)


      } yield ()
    }

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
