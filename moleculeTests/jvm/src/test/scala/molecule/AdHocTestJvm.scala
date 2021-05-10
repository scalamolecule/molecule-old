package molecule

//import _root_.datomic.Peer
//import datomicClient.ClojureBridge
//import molecule.core.util.JavaUtil
//import molecule.core.util.testing.MoleculeTestHelper
//import molecule.setup.core.CoreData
//import org.specs2.mutable.Specification
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future

//import java.util.Date

//import datomicClient.ClojureBridge

import molecule.core.util.{Helpers, JavaUtil}
import molecule.core.util.testing.MoleculeTestHelper
import molecule.datomic.api.in1_out13._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.datomic.peer.facade.Datomic_Peer._
import molecule.setup.core.CoreData
import molecule.tests.core.base.schema.CoreTestSchema
import org.specs2.mutable.Specification
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import _root_.datomic.Peer
import scala.concurrent.duration._


//class AdHocTestJvm extends molecule.setup.TestSpec with Helpers {
class AdHocTestJvm extends Specification
  //  with ClojureBridge
  with JavaUtil with MoleculeTestHelper with CoreData {


  "core" >> {
    //    implicit val conn: Conn = recreateDbFrom(CoreTestSchema)


    //    val tx1 = Ns.int(1).save
    //    val e   = tx1.eid
    //    val tx2 = Ns(e).int(2).update

    //    require("clojure.core.async")
    //    require("datomic.Peer")

    //    Await.result(
    //      Future {
    //        _root_.datomic.Peer.q("[:find ?n :where [(+ 1 1) ?n]]")
    //      },
    //      5.seconds
    //    ).iterator().next.get(0) === 2

//    await(Future(Peer.q("[:find ?n :where [(molecule.util.fns/- 2 1) ?n]]"))) === 5
//    Future(Peer.q("[:find ?n :where [(ground 1) ?n] [(molecule.util.fns/- 2 1) ?n]]"))



    await(Future(Peer.q("[:find ?n :where [(+ 1 1) ?n]]"))) === 2
    //        await(Future(Peer.q("[:find ?n :where [(+ 1 1) ?n]]")))
    //        Peer.q("[:find ?n :where [(+ 1 1) ?n]]").iterator().next.get(0) === 2
    //        await(Future(Peer.q("[:find ?n :where [(+ 1 1) ?n]]"))).iterator().next.get(0) === 2
    //    await(Future(Peer.q("[:find ?n :where [(+ 1 1) ?n]]"))).iterator().next.get(0) === 2

    //    await(Future(
    //      _root_.datomic.Peer.q(
    //        """[:find  ?v
    //          | :in    $ ?a
    //          | :where [(+ ?a 1) ?v]
    //          |]""".stripMargin, conn.db.getDatomicDb, 1.asInstanceOf[AnyRef]
    //      )
    //    ))
    //

    //    await(Future(
    //      _root_.datomic.Peer.q(
    //        """[:find  ?c ?c_t ?c_op
    //          | :in    [?a ...]
    //          | :where [?a :Ns/int ?c ?c_tx ?c_op]
    //          |        [(- ?c_tx 13194139533312) ?c_t]
    //          |]""".stripMargin, conn.db.getDatomicDb, Util.list(42)
    //      )
    //    ))

    //    await(Future(
    //      conn.q(
    //        """[:find  ?c ?c_t ?c_op
    //          | :in    $ [?a ...]
    //          | :where [?a :Ns/int ?c ?c_tx ?c_op]
    //          |        [(- ?c_tx 13194139533312) ?c_t]
    //          |]""".stripMargin, Seq(42)
    //      )
    //    ))
    //    await(Future(Ns(42).int.t.op.inspectGetHistory))
    //    await(Future(Ns(42).int.t.op.getObjListHistory))

    //      .sortBy(o => (o.t, o.op)).map(o => Vector(o.int, o.t, o.op)) === List(
    //      Vector(1, tx1.t, true),
    //      Vector(1, tx2.t, false),
    //      Vector(2, tx2.t, true)
    //    )

    //    Ns(e).int.t.op.getObjListHistory.sortBy(o => (o.t, o.op)).map(o => Vector(o.int, o.t, o.op)) === List(
    //      Vector(1, tx1.t, true),
    //      Vector(1, tx2.t, false),
    //      Vector(2, tx2.t, true)
    //    )

    //    await(Ns(e).int.t.op.getAsyncObjListHistory).sortBy(o => (o.t, o.op)).map(o => Vector(o.int, o.t, o.op)) === List(
    //      Vector(1, tx1.t, true),
    //      Vector(1, tx2.t, false),
    //      Vector(2, tx2.t, true)
    //    )


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
