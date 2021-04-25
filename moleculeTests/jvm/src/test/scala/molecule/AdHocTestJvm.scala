package molecule

import java.util
import molecule.datomic.api.out3._
import molecule.datomic.base.facade.Conn
import molecule.datomic.peer.facade.Datomic_Peer._
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.core.schemaDef.schema.PartitionTestSchema
import org.specs2.mutable.Specification

//class AdHocTest extends molecule.setup.TestSpec with Helpers {
class AdHocTestJvm extends Specification {


  "core" >> {
    import molecule.tests.core.base.dsl.CoreTest._
    implicit val conn: Conn = recreateDbFrom(CoreTestSchema)



    ok
  }
  //
  //  "Simple hyperedge" in new GraphSetup {
  //
  //    import molecule.tests.examples.datomic.dayOfDatomic.dsl.Graph._
  //
  //    // User 1 Roles in Group 2
  //    User.name_("User1")
  //      .RoleInGroup.Group.name_("Group2")
  //      ._RoleInGroup.Role.name.inspectGet
  //
  //
  //    ok
  //  }
  //
  //    "adhoc" in new BidirectionalSetup {
  //      import molecule.tests.core.bidirectionals.dsl.Bidirectional._
  //      Person.name("Ann").Buddies.e(gus)
  //    }

  //    "self-join" >> {
  //      import molecule.tests.core.ref.dsl.SelfJoin._
  //      implicit val conn: Conn = Datomic_Peer.recreateDbFrom(SelfJoinSchema)
  //
  //
  //
  //
  //      ok
  //    }

  //      "bidirectional" >> {
  //        import molecule.tests.core.bidirectionals.dsl.Bidirectional._
  //        implicit val conn: Conn = Datomic_Peer.recreateDbFrom(BidirectionalSchema)
  //
  //
  //        ok
  //      }
  //
  //
  //
  //
  //
  //
  //  "adhoc" in new CoreSetup {
  //
  //    import molecule.tests.core.base.dsl.CoreTest._
  //
  //    Ns.str.bool.inspectGet
  ////    Ns.str.bool.inspectGet
  ////    m(Ns.str.bool).inspectGet
  //  }
  //
  //    "Insert resolves to correct partitions" in new PartitionSetup {
//  "Insert resolves to correct partitions" >> {
//    import molecule.tests.core.schemaDef.dsl.PartitionTest._
//    implicit val conn: Conn = recreateDbFrom(PartitionTestSchema)
//
//    m(lit_Book.title("yeah")).inspectSave
//    //      lit_Book.title("yeah").save
//
//    //      gen_Person.name
//
//    val edn =
//      """[
//        |  [:db/add #db/id[:gen] :gen_Person/name "ben"]
//        |  [:db/add #db/id[:lit] :lit_Book/title "yeah"]
//        |  [:db/add #db/id[:lit] :lit_Book/author 42]
//        |]
//        |""".stripMargin
//
//    import _root_.datomic.Util._
//
//
//    val jav: util.List[_] = list(
//      list(":db/add", "-1", ":lit_Book/title", "yeah")
//      //        list(":db/add", tempId, ":lit_Book/title", "yeah")
//      //        list(":db/add", "#dxxxb/ixd[:lit -1000025]", ":lit_Book/title", "yeah")
//      //        list(":db/add", _root_.datomic.Peer.tempid(read(":gen")), ":gen_Person/name", "ben"),
//      //        list(":db/add", _root_.datomic.Peer.tempid(read(":lit")), ":lit_Book/title", "yeah"),
//      //        list(":db/add", _root_.datomic.Peer.tempid(read(":lit")), ":lit_Book/author", 42),
//    )
//
//    val tx1 = conn.transact(edn)
//    println(tx1)
//    val tx2 = conn.transactRaw(jav)
//    println(tx2)
//
//
//    lit_Book.title.get.head === "yeah"
//
//    ok
//  }
  //
  //
  //    "Insert resolves to correct partitions" in new ModernGraph2Setup {
  //
  //      import molecule.tests.examples.gremlin.gettingStarted.dsl.ModernGraph2._
  //
  //      m(Person.name.Knows * Person.name).inspectGet
  //    }
  //
  //
  //    "txCount" >> {
  //
  //      import molecule.core.util.testing.TxCount._
  //      implicit val conn: Conn = Datomic_Peer.recreateDbFrom(TxCountSchema)
  //      TxCount.db("x").basisT(42L).save
  //
  //      ok
  //    }
  //
  //
  //
  //  "A first query" in new SeattleSetup {
  //    import molecule.tests.examples.datomic.seattle.dsl.Seattle._
  //
  //    // A Community-name molecule
  //    val communities = m(Community. e.name_)
  //
  //    // We have 150 communities
  //    communities.get.size === 150
  //  }
  //
  //  "adhoc" in new BidirectionalSetup {
  //import molecule.tests.core.bidirectionals.dsl.Bidirectional._
  //
  //  }
  //
  //  "adhoc" in new PartitionSetup {
  //
  //  }
  //
  //
  //  "example adhoc" in new SeattleSetup {
  //
  //
  //    //    ok
  //  }
  //
  //
  //  "example adhoc" in new SocialNewsSetup {
  //
  //
  //
  //    ok
  //  }
  //
  //    "example adhoc" in new MBrainzSetup {
  //
  //
  //
  //      ok
  //    }
}
