package molecule

import _root_.datomic.Peer
import molecule.core.util.Helpers
import molecule.datomic.api.out3._
import molecule.datomic.base.facade.Conn
import molecule.datomic.peer.facade.Datomic_Peer
import molecule.datomic.peer.facade.Datomic_Peer._
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.examples.datomic.mbrainz.schema.{MBrainzSchema, MBrainzSchemaLowerToUpper}
import org.specs2.mutable.Specification
import scala.jdk.CollectionConverters._

//class AdHocTest extends molecule.setup.TestSpec with Helpers {
class AdHocTestJvm extends Specification {
  //
  //  trait C {
  //    def c = 8
  //  }
  //  object C extends C
  //
  //
  //  trait A extends C {
  //    val x = c
  //  }
  //  object A extends C
  //
  //
  //  object B {
  //    import C._
  //}
  //
  //  object D {
  //    import B._
  //
  //    val a2 = c
  //  }
  //
  //
  "core" >> {
    import molecule.tests.core.base.dsl.CoreTest._

    implicit val conn: Conn = recreateDbFrom(CoreTestSchema)

    println(Peer.tempid(_root_.datomic.Util.read(":db.part/user")))
    println(Peer.tempid(_root_.datomic.Util.read(":db.part/user")).getClass)
    println(Peer.tempid(_root_.datomic.Util.read(":db.part/user")).isInstanceOf[java.util.Map[_,_]])
    println("-------")
    println(_root_.datomic.Util.read(":db.part/user"))
    println(_root_.datomic.Util.read(":db.part/user").getClass)





    m(Ns.str.int)
//    xx(Ns.int.bool).inspectGet
    //    Ns.int.get.head === 0

//    println(Platform.bar)


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
  //  "Insert resolves to correct partitions" in new PartitionSetup {
  //    import molecule.tests.core.schemaDef.dsl.PartitionTest._
  //    m(lit_Book.title.Author.name._lit_Book.Reviewers * gen_Person.name).inspectGet
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
