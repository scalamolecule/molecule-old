package molecule

import java.util.Date
import molecule.core.api.Molecule_1
import molecule.core.ast.elements._
import molecule.core.composition.Tx
import molecule.core.dsl.{attributes, base}
import molecule.core.util.Helpers
import molecule.core.util.testing.TxCount.schema.TxCountSchema
import molecule.datomic.api.in3_out10._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.Model2Query
import molecule.datomic.peer.facade.Datomic_Peer
//import molecule.tests.core.base.dsl.CoreTest._
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.examples.gremlin.gettingStarted.dsl.ModernGraph2.Person
import org.specs2.mutable.Specification


class AdHocTest extends molecule.setup.TestSpec with Helpers {
  //class AdHocTest extends Specification {


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

  //  "adhoc" in new BidirectionalSetup {
  //
  //    import molecule.tests.core.bidirectionals.dsl.Bidirectional._
  //
  //
  //
  //  }
  //
  //
  //
  //
  //  "adhoc" in new CoreSetup {
  //    import molecule.tests.core.base.dsl.CoreTest._
  //

  //  }
  //
  //  "Insert resolves to correct partitions" in new PartitionSetup {
  //    import molecule.tests.core.schemaDef.dsl.PartitionTest._
  //    m(lit_Book.title.Author.name._lit_Book.Reviewers * gen_Person.name).inspectGet
  //  }
  //
  //
  //  "Insert resolves to correct partitions" in new ModernGraph2Setup {
  //
  //    import molecule.tests.examples.gremlin.gettingStarted.dsl.ModernGraph2._
  //
  //    m(Person.name.Knows * Person.name).inspectGet
  //  }


//  "adhoc" >> {
//
//    import molecule.core.util.testing.TxCount._
//    implicit val conn: Conn = Datomic_Peer.recreateDbFrom(TxCountSchema)
//    TxCount.db.Tx.apply(TxCount.db)
//
//  }

  "adhoc" >> {

    import molecule.tests.core.base.dsl.CoreTest._
    implicit val conn: Conn = Datomic_Peer.recreateDbFrom(CoreTestSchema)



    //    {
    //      val o = m(Ns.float.Refs1.*(Ref1.int1).RefsSub1.str1).getObjList.head
    //      o.float
    //      o.Refs1.head.int1
    //      o.RefsSub1.str1
    //    }
    //
    //        val o = m(Ns.float.Ref1.int1.Refs2.*(Ref2.int2)).getObjList.head
    //        o.float
    //        o.Ref1.int1
    //        o.Ref1.Refs2.head.int2
    //
    //    Ns.str.Ref1.str1._Ns.Refs1.int1.inspectInsert(
    //      List(
    //        ("a", "a1", 1),
    //        ("b", "b1", 2)
    //      )
    //    )

    ok
  }
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

  //  "adhoc" in new BidirectionalSetup {
  //import molecule.tests.core.bidirectionals.dsl.Bidirectional._
  //
  //  }

  //  "adhoc" in new PartitionSetup {

  //  }


  //  "example adhoc" in new SeattleSetup {
  //
  //
  //    //    ok
  //  }


  //  "example adhoc" in new SocialNewsSetup {
  //
  //
  //
  //    ok
  //  }
}
//