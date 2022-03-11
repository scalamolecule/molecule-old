package moleculeTests

import clojure.lang.Keyword
import datomic.{Database, Peer, Util}
import molecule.core.data.model._
import molecule.core.dsl.attributes.Attr
import molecule.core.exceptions.MoleculeException
import molecule.core.generic.Schema.Schema_a
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.core.util.{Helpers, JavaConversions}
import molecule.datomic.api
import molecule.datomic.api.in1_out15._
import molecule.datomic.base.api.Datom
import molecule.datomic.base.ast.dbView.History
import molecule.datomic.base.facade.TxReport
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import molecule.datomic.peer.facade.Conn_Peer
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}

object AdhocJvm extends AsyncTestSuite with Helpers with JavaConversions {


  lazy val tests = Tests {

    "adhocJvm" - core { implicit futConn =>
      for {
        conn <- futConn

        _ <- Ns.str.Refs1.*(Ref1.int1) insert List(
          ("A", List(1, 2)),
          ("B", List(1, 2)),
          ("C", List()),
        )

        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).inspectGet
        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.a1).inspectGet

//        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.a1).get.map(_ ==> List(
//          ("A", List(1, 2)),
//          ("B", List(1, 2)),
//          ("C", List()),
//        ))
//        _ <- Ns.str.a1.Refs1.*?(Ref1.int1.d1).get.map(_ ==> List(
//          ("A", List(2, 1)),
//          ("B", List(2, 1)),
//          ("C", List()),
//        ))
//        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.a1).get.map(_ ==> List(
//          ("C", List()),
//          ("B", List(1, 2)),
//          ("A", List(1, 2)),
//        ))
//        _ <- Ns.str.d1.Refs1.*?(Ref1.int1.d1).get.map(_ ==> List(
//          ("C", List()),
//          ("B", List(2, 1)),
//          ("A", List(2, 1)),
//        ))



//        _ <- (Ns.str + Ref1.int1) insert List(
//          ("A", 1),
//          ("A", 2),
//          ("B", 1),
//          ("B", 2),
//        )
//        _ <- (Ns.str.a1 + Ref1.int1.a1).get.map(_ ==> List(
//          ("A", 1),
//          ("A", 2),
//          ("B", 1),
//          ("B", 2),
//        ))
//        _ <- (Ns.str.a1 + Ref1.int1.d1).get.map(_ ==> List(
//          ("A", 2),
//          ("A", 1),
//          ("B", 2),
//          ("B", 1),
//        ))
//        _ <- (Ns.str.d1 + Ref1.int1.a1).get.map(_ ==> List(
//          ("B", 1),
//          ("B", 2),
//          ("A", 1),
//          ("A", 2),
//        ))
//        _ <- (Ns.str.d1 + Ref1.int1.d1).get.map(_ ==> List(
//          ("B", 2),
//          ("B", 1),
//          ("A", 2),
//          ("A", 1),
//        ))













//        _ <- Ns.str.a1.Refs1.*(Ref1.int1.a1).getJson.map(_ ==> List(
//          ("B", List(2, 1)),
//          ("A", List(2, 1)),
//        ))


      } yield ()
    }

    //    "adhocJvm" - empty { implicit futConn =>
    //      for {
    //        conn <- futConn
    //
    //
    //      } yield ()
    //
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
