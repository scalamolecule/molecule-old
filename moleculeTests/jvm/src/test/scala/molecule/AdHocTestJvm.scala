package molecule

import java.io.StringReader
import java.net.URI
import java.util
import java.util.Collections
import clojure.lang.Keyword
import datomicClient.ClojureBridge
import datomicJava.client.api.async.AsyncDatomic.require
import molecule.core.ast.elements._
import molecule.core.transform.{ModelTransformer, ModelTransformerAsync}
import molecule.core.util.JavaUtil
import molecule.core.util.testing.MoleculeTestHelper
import molecule.datomic.api.in1_out13._
import molecule.datomic.base.ast.query._
import molecule.datomic.base.ast.transactionModel
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.marshalling.DatomicRpc.readString
import molecule.datomic.peer.facade.Datomic_Peer._
import molecule.setup.core.CoreData
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.core.bidirectionals.schema.BidirectionalSchema
import molecule.tests.core.schemaDef.schema.PartitionTestSchema
import molecule.tests.examples.datomic.dayOfDatomic.schema.SocialNewsSchema
import org.specs2.mutable.Specification
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


//class AdHocTest extends molecule.setup.TestSpec with Helpers {
class AdHocTestJvm extends Specification
  with ClojureBridge with JavaUtil with MoleculeTestHelper with CoreData {


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
  //
  //  "bidirectional" >> {
  //    import molecule.tests.core.bidirectionals.dsl.Bidirectional._
  //    implicit val conn: Conn = recreateDbFrom(BidirectionalSchema)
  //
  //
  //    ok
  //  }
  //
  //  "socialNews" >> {
  //    import molecule.tests.examples.datomic.dayOfDatomic.dsl.SocialNews._
  //    implicit val conn: Conn = recreateDbFrom(SocialNewsSchema)
  //
  //
  //    ok
  //  }
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
  //    val l1 = List(100, 2, 3)
  //
  //    def f(acc: Seq[Int], i: Int): Future[Seq[Int]] = {
  //      Thread.sleep(i)
  //      val fut = Future {
  //        println("blocking " + i)
  //        i * 10
  //      }
  //      fut.map(v => acc :+ v)
  //    }
  //
  //    val n1 = System.currentTimeMillis()
  //    val l2 = l1.foldLeft(Future(Seq.empty[Int])) {
  //      case (acc, i) =>
  //        println("run " + i)
  //        acc.flatMap(l => f(l, i))
  //    }
  //    val n2 = System.currentTimeMillis()
  //    println("future created in " + (n2 - n1) + " ms")
  //
  //    val l3 = await(l2)
  //    val n3 = System.currentTimeMillis()
  //    println("await took " + (n3 - n2) + " ms")
  //
  //    l3 === List(1000, 20, 30)
}
