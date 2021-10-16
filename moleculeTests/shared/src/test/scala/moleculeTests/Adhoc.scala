package moleculeTests

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

        List(e1, r1, r2) <- Ns.str.Refs1.*(Ref1.int1).insert(List(
          ("a", List(1, 2))
        )).map(_.eids)

        _ <- e1.touchListMax(1).map(_ ==> List(
          ":db/id" -> e1,
          ":Ns/refs1" -> List(r1, r2),
          ":Ns/str" -> "a"
        ))

      } yield ()
    }
    /*
[error] 	moleculeTests.tests.core.bidirectionals.edgeOther.EdgeManyOtherSave
[error] 	moleculeTests.tests.core.bidirectionals.edgeOther.EdgeManyOtherUpdateProps

[error] 	moleculeTests.tests.core.json.JsonRef
[error] 	moleculeTests.tests.core.json.JsonNested
[error] 	moleculeTests.tests.core.json.JsonAttributes

[error] 	moleculeTests.tests.core.expression.Fulltext_

[error] 	moleculeTests.tests.core.composite.CompositeRef
[error] 	moleculeTests.tests.core.composite.CompositeArities

[error] 	moleculeTests.tests.core.obj.ObjGeneric

[error] 	moleculeTests.tests.core.crud.insert.InsertRelated

[error] 	moleculeTests.tests.core.txMetaData.MetaUpdate
[error] 	moleculeTests.tests.core.txMetaData.MetaRetract

[error] 	moleculeTests.tests.examples.datomic.dayOfDatomic.ProductsAndOrders
[error] 	moleculeTests.tests.examples.datomic.seattle.SeattleTests
[error] 	moleculeTests.tests.examples.datomic.dayOfDatomic.Provenance

[error] 	moleculeTests.tests.core.runtime.EntityMap
[error] 	moleculeTests.tests.core.runtime.EntityList


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
