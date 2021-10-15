package moleculeTests

import datomic.Util
import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import molecule.datomic.api.in3_out12._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global


object Adhoc extends AsyncTestSuite with Helpers {

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "adhoc" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn
        //        _ <- Ns.int.apply(1).asc1.str.desc2.get
        //        _ <- Ns.int.not(1).asc1.str.desc2.get


        // Initial data
        _ <- Ns.int(1).save
        _ <- Ns.int.get.map(_ ==> List(1))

        // Add raw transactional data
        // (Scala integers are internally stored as Longs)
//        _ <- conn.transactRaw(Util.list(
//          Util.map(Util.read(":Ns/int"), 2L.asInstanceOf[AnyRef]),
//          Util.map(Util.read(":Ns/int"), 3L.asInstanceOf[AnyRef])
//        ).asInstanceOf[java.util.List[AnyRef]])
//        _ <- futConn.map(_.transactRaw(Util.list(
        _ <- futConn.flatMap(_.transactRaw(Util.list(
          Util.map(Util.read(":Ns/int"), 2L.asInstanceOf[AnyRef]),
          Util.map(Util.read(":Ns/int"), 3L.asInstanceOf[AnyRef])
        ).asInstanceOf[java.util.List[AnyRef]]))

        // Raw data has been added
        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))







//        // Live state
//        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
//
//        // Use current state as a test db "branch"
//        _ <- conn.testDbAsOfNow
//
//        // Test state is currently same as live state
//        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
//
//        // Test operations:
//
//        // Save
//        _ <- Ns.int(4).save
//        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3, 4))
//
//        // Insert
//        _ <- Ns.int insert List(5, 6)
//        _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3, 4, 5, 6))
//
//        // Update
//        _ <- Ns(e2).int(0).update
//        _ <- Ns.int.get.map(_.sorted ==> List(0, 1, 3, 4, 5, 6))
//
//        // Retract
//        _ <- e3.retract
//        _ <- Ns.int.get.map(_.sorted ==> List(0, 1, 4, 5, 6))
//
//        // Live state unchanged
//        _ = conn.useLiveDb()
//        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))


      } yield ()
    }
    /*
    moleculeTests.tests.core.json.JsonRef
    moleculeTests.jvm.core.transaction.TxRaw
    moleculeTests.tests.core.txMetaData.MetaUpdate
    moleculeTests.tests.core.generic.Schema_AttrOptions
    moleculeTests.tests.core.bidirectionals.edgeOther.EdgeManyOtherSave
    moleculeTests.tests.core.json.JsonNested
    moleculeTests.tests.core.expression.Fulltext_
    moleculeTests.tests.core.bidirectionals.edgeOther.EdgeOneOtherUpdateProps
    moleculeTests.tests.examples.datomic.dayOfDatomic.ProductsAndOrders
    moleculeTests.tests.core.runtime.EntityMap
    moleculeTests.tests.core.generic.Index_VAET
    moleculeTests.tests.examples.datomic.dayOfDatomic.Provenance
    moleculeTests.tests.core.generic.Schema_Attr
    moleculeTests.tests.core.bidirectionals.edgeOther.EdgeManyOtherUpdateProps
    moleculeTests.tests.core.composite.CompositeRef
    moleculeTests.tests.examples.datomic.seattle.SeattleTests
    moleculeTests.tests.core.generic.LogTest
    moleculeTests.tests.core.generic.Index
    moleculeTests.tests.core.composite.CompositeArities
    moleculeTests.tests.core.generic.Index_EAVT
    moleculeTests.tests.core.obj.ObjGeneric
    moleculeTests.tests.core.crud.insert.InsertRelated
    moleculeTests.tests.core.generic.Index_AVET
    moleculeTests.tests.core.time.TestDbSince
    moleculeTests.tests.core.txMetaData.MetaRetract
    moleculeTests.tests.core.runtime.EntityList
    moleculeTests.tests.core.generic.Index_AEVT
    moleculeTests.tests.core.time.TestDbWith
    moleculeTests.tests.core.generic.Datom_v
    moleculeTests.tests.core.time.TestDbAsOf
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
    //    "adhoc" - bidirectional { implicit conn =>
    //      import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
    //
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //
    //
    //      } yield ()
    //    }
  }
}
