package moleculeTests

import molecule.core.macros.rowAttr.{CastOptNested, CastTypes, JsonBase}
import molecule.core.marshalling.unpackAttr.String2cast
import molecule.core.util.Helpers
import molecule.datomic.api.in3_out12._
import molecule.datomic.base.marshalling.packers.PackEntityGraph
import molecule.datomic.peer.facade.Conn_Peer
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object AdhocJvm extends AsyncTestSuite with Helpers
  with String2cast with CastTypes with CastOptNested with JsonBase
  with PackEntityGraph {


  lazy val tests = Tests {

    "adhocJvmPeer" - corePeerOnly { implicit futConn =>
      import moleculeTests.dataModels.core.base.dsl.CoreTest._
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn.map(_.asInstanceOf[Conn_Peer])
        //        _ <- Ns.int.apply(1).asc1.str.desc2.get
        //        _ <- Ns.int.not(1).asc1.str.desc2.get

        // Data before initialising the tx report queue
        _ <- Ns.int(0).save

        // Initialise tx report queue
        queue = conn.txReportQueue

        // The txReportQueue is empty when initialized
        _ = queue.isEmpty ==> true

        // Let queue listen in a separate thread
        _ = Future {
          while (true) {
            try {
              // Do stuff with tx report when it becomes available (blocks until then)
              println(queue.take)
            } catch {
              case _: InterruptedException => println("interrupted")
            }
          }
        }

        // Each transaction is added to the queue and printed in the above listener
        _ <- Ns.int(1).save
        _ <- Ns.int(2).save
        _ <- Ns.int(3).save

        /*
        Will print the 3 tx reports:

        TxReport {
          dbBefore  : datomic.db.Db@7dced5ae
          dbBefore.t: 1036
          dbAfter   : datomic.db.Db@d8bcefb3
          dbAfter.t : 1038
          txData    : [13194139534350  50    Sun Nov 07 17:57:06 CET 2021         13194139534350   true],
                      [17592186045455  73    1                                    13194139534350   true]
          tempIds   : {-9223350046623220403 17592186045455}
          eids      : List(17592186045455)
        }
        TxReport {
          dbBefore  : datomic.db.Db@d8bcefb3
          dbBefore.t: 1038
          dbAfter   : datomic.db.Db@84609abb
          dbAfter.t : 1040
          txData    : [13194139534352  50    Sun Nov 07 17:57:06 CET 2021         13194139534352   true],
                      [17592186045457  73    2                                    13194139534352   true]
          tempIds   : {-9223350046623220404 17592186045457}
          eids      : List(17592186045457)
        }
        TxReport {
          dbBefore  : datomic.db.Db@84609abb
          dbBefore.t: 1040
          dbAfter   : datomic.db.Db@b2cbb92e
          dbAfter.t : 1042
          txData    : [13194139534354  50    Sun Nov 07 17:57:06 CET 2021         13194139534354   true],
                      [17592186045459  73    3                                    13194139534354   true]
          tempIds   : {-9223350046623220405 17592186045459}
          eids      : List(17592186045459)
        }
         */

        // The last transaction has not been popped of the queue yet
        _ = queue.isEmpty ==> false

        // Data from before and after calling txReportQueue not affected
        _ <- Ns.int.get.map(_.sorted ==> List(0, 1, 2, 3))

        // Confirming that queue has been emptied
        _ = queue.isEmpty ==> true

        // Interrupt queue
        _ = conn.removeTxReportQueue()
      } yield ()
    }

//    "adhocJvm" - core { implicit futConn =>
//      for {
//        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
//        conn <- futConn
//
////        dd <- Log(Some(1000L)).e.a.v.get
////        _ = println(dd)
//
////        _ <- Schema.a.get.map(res => println(res.take(10)))
//
//        _ <- Ns.int(1).save
//
//        _ <- Ns.int.get.map(_ ==> List(1))
//
//
//      } yield ()
//    }

//    "adhocJvm2" - core { implicit futConn =>
//      for {
//        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
//        conn <- futConn
//
////        dd <- Log(Some(1000L)).e.a.v.get
////        _ = println(dd)
//        _ <- Ns.int(2).save
//        _ <- Ns.int.get.map(_ ==> List(2))
//
//
//      } yield ()
//    }




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
