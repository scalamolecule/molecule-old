//package moleculeTests
//
////import _root_.datomic.Peer
////import datomicClient.ClojureBridge
////import molecule.core.util.JavaUtil
////import molecule.core.util.testing.MoleculeTestHelper
////import moleculeTests.setup.core.CoreData
////import org.specs2.mutable.Specification
////import scala.concurrent.ExecutionContext.Implicits.global
////import scala.concurrent.Future
//
////import java.util.Date
//
////import datomicClient.ClojureBridge
//
//import java.util.concurrent.Executors.newFixedThreadPool
//import datomicClient.ClojureBridge
//import molecule.core.api.Molecule_1
//import molecule.core.dsl.base
//import molecule.core.util.{Helpers, JavaUtil}
//import molecule.core.util.testing.{MoleculeTestHelper, TimerPrint}
//import molecule.datomic.api.in1_out13._
//import molecule.datomic.api.in1_out4.m
//import molecule.datomic.base.facade.{Conn, TxReport}
//import molecule.datomic.base.marshalling.DatomicRpc.{getCachedQueryExecutor, qTime, unmarshallInputs}
//import molecule.datomic.peer.facade.Datomic_Peer._
//import moleculeTests.setup.core.CoreData
//import moleculeTests.dataModels.core.base.dsl.CoreTest._
//import moleculeTests.dataModels.core.base.schema.CoreTestSchema
//import org.specs2.concurrent.ExecutionEnv
//import org.specs2.mutable.Specification
//import scala.concurrent.{ExecutionContext, Promise}
//import scala.util.control.NonFatal
////import scala.concurrent.ExecutionContext
////import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.{Await, Future}
//import _root_.datomic.Peer
////import scala.concurrent.ExecutionContext.fromExecutorService
//import scala.concurrent.duration._
//
//
//class AdHocTestJvm extends molecule.setup.TestSpec with Helpers {
//  //class AdHocTestJvm extends Specification
//  //  with ClojureBridge with JavaUtil with MoleculeTestHelper with CoreData {
//
//  import scala.concurrent.Promise
//  import scala.concurrent.Future
//  import scala.concurrent.ExecutionContext.Implicits.global
//  import scala.util.control.NonFatal
//
////  def f(i: Int): Future[Int] = {
////    Future {
////      if (i == 0)
////        throw new IllegalArgumentException("auch")
////      i
////    }
////  }
////
////  def f3(i: Int): Future[Int] = {
////    val p = Promise[Int]()
////    try {
////      p.success {
////        if (i == 0) {
////          throw new IllegalArgumentException("auch")
////        }
////        i
////      }
////    } catch {
////      case NonFatal(exc) => p.failure(exc)
////    }
////    p.future
////  }
//
//  //  def f2(i: Int): Future[Int] = {
//  //    Future {
//  //      try {
//  //        if (i == 0)
//  //          throw new IllegalArgumentException("auch")
//  //        i
//  //      } catch {
//  //        case NonFatal(exc) => Future.failed(exc)
//  //      }
//  //    }
//  //  }
////
////    "core" >> {
////      implicit val conn: Conn = recreateDbFrom(CoreTestSchema)
////
////  //    1 === 2
////
////      val in: Molecule_1.Molecule_1_01[base.Init with Ns_int, Int, Int] = m(Ns.int(?))
////
////      in(5).getAsync
////
////
////      Ns.uri(uri1).inspectSave
////      Ns.uri(uri1).save
////      Ns.uri.get.map(_ ==> List(uri1))
////
////
////
////
////
////  //    ok
////    }
//
//  //  Peer.shutdown(true)
//
//
//  //  _root_.datomic.Peer.shutdown(true)
//  //    defaultExecutorService.shutdownNow()
//
//  //
//  //  "Simple hyperedge" in new GraphSetup {
//  //
//  //    import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.Graph._
//  //
//  //    // User 1 Roles in Group 2
//  //    User.name_("User1")
//  //      .RoleInGroup.Group.name_("Group2")
//  //      ._RoleInGroup.Role.name.inspectGet
//  //
//  //
//  //    ok
//  //  }
//  //
//  //      "adhoc" in new BidirectionalSetup {
//  //        import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
//  //
//  //      }
//  //
//  //
//  //    "self-join" >> {
//  //      import moleculeTests.dataModels.core.ref.dsl.SelfJoin._
//  //      implicit val conn: Conn = Datomic_Peer.recreateDbFrom(SelfJoinSchema)
//  //
//  //
//  //
//  //
//  //      ok
//  //    }
//  //
//  //  "bidirectional" >> {
//  //    import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
//  //    implicit val conn: Conn = recreateDbFrom(BidirectionalSchema)
//  //
//  //
//  //    ok
//  //  }
//  //
//  //  "socialNews" >> {
//  //    import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.SocialNews._
//  //    implicit val conn: Conn = recreateDbFrom(SocialNewsSchema)
//  //
//  //
//  //    ok
//  //  }
//  //
//  //
//  //
//  //
//  //
//  //
//  //  "adhoc" in new CoreSetup {
//  //
//  //    import moleculeTests.dataModels.core.base.dsl.CoreTest._
//  //
//  //    Ns.str.bool.inspectGet
//  ////    Ns.str.bool.inspectGet
//  ////    m(Ns.str.bool).inspectGet
//  //  }
//  //
//  //    "Insert resolves to correct partitions" in new PartitionSetup {
//  //  "Insert resolves to correct partitions" >> {
//  //    import moleculeTests.dataModels.core.schemaDef.dsl.PartitionTest._
//  //    implicit val conn: Conn = recreateDbFrom(PartitionTestSchema)
//  //
//  //    m(lit_Book.title("yeah")).inspectSave
//  //    //      lit_Book.title("yeah").save
//  //
//  //    //      gen_Person.name
//  //
//  //    val edn =
//  //      """[
//  //        |  [:db/add #db/id[:gen] :gen_Person/name "ben"]
//  //        |  [:db/add #db/id[:lit] :lit_Book/title "yeah"]
//  //        |  [:db/add #db/id[:lit] :lit_Book/author 42]
//  //        |]
//  //        |""".stripMargin
//  //
//  //    import _root_.datomic.Util._
//  //
//  //
//  //    val jav: util.List[_] = list(
//  //      list(":db/add", "-1", ":lit_Book/title", "yeah")
//  //      //        list(":db/add", tempId, ":lit_Book/title", "yeah")
//  //      //        list(":db/add", "#dxxxb/ixd[:lit -1000025]", ":lit_Book/title", "yeah")
//  //      //        list(":db/add", _root_.datomic.Peer.tempid(read(":gen")), ":gen_Person/name", "ben"),
//  //      //        list(":db/add", _root_.datomic.Peer.tempid(read(":lit")), ":lit_Book/title", "yeah"),
//  //      //        list(":db/add", _root_.datomic.Peer.tempid(read(":lit")), ":lit_Book/author", 42),
//  //    )
//  //
//  //    val tx1 = conn.transact(edn)
//  //    println(tx1)
//  //    val tx2 = conn.transactRaw(jav)
//  //    println(tx2)
//  //
//  //
//  //    lit_Book.title.get.map(_.head ==> "yeah")
//  //
//  //    ok
//  //  }
//  //
//  //
//  //    "Insert resolves to correct partitions" in new ModernGraph2Setup {
//  //
//  //      import moleculeTests.dataModels.examples.gremlin.gettingStarted.dsl.ModernGraph2._
//  //
//  //      m(Person.name.Knows * Person.name).inspectGet
//  //    }
//  //
//  //
//  //    "txCount" >> {
//  //
//  //      import molecule.core.util.testing.TxCount._
//  //      implicit val conn: Conn = Datomic_Peer.recreateDbFrom(TxCountSchema)
//  //      TxCount.db("x").basisT(42L).save
//  //
//  //      ok
//  //    }
//  //
//  //
//  //
//  //  "A first query" in new SeattleSetup {
//  //    import moleculeTests.dataModels.examples.datomic.seattle.dsl.Seattle._
//  //
//  //    // A Community-name molecule
//  //    val communities = m(Community. e.name_)
//  //
//  //    // We have 150 communities
//  //    communities.get.map(_.size ==> 150)
//  //  }
//  //
//  //  "adhoc" in new BidirectionalSetup {
//  //import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
//  //
//  //  }
//  //
//  //  "adhoc" in new PartitionSetup {
//  //
//  //  }
//  //
//  //
//  //  "example adhoc" in new SeattleSetup {
//  //
//  //
//  //    //    ok
//  //  }
//  //
//  //
//  //  "example adhoc" in new SocialNewsSetup {
//  //
//  //
//  //
//  //    ok
//  //  }
//  //
//  //    "example adhoc" in new MBrainzSetup {
//  //
//  //
//  //
//  //      ok
//  //    }
//  //    val l1 = List(100, 2, 3)
//  //
//  //    def f(acc: Seq[Int], i: Int): Future[Seq[Int]] = {
//  //      Thread.sleep(i)
//  //      val fut = Future {
//  //        println("blocking " + i)
//  //        i * 10
//  //      }
//  //      fut.map(v => acc :+ v)
//  //    }
//  //
//  //    val n1 = System.currentTimeMillis()
//  //    val l2 = l1.foldLeft(Future(Seq.empty[Int])) {
//  //      case (acc, i) =>
//  //        println("run " + i)
//  //        acc.flatMap(l => f(l, i))
//  //    }
//  //    val n2 = System.currentTimeMillis()
//  //    println("future created in " + (n2 - n1) + " ms")
//  //
//  //    val l3 = await(l2)
//  //    val n3 = System.currentTimeMillis()
//  //    println("await took " + (n3 - n2) + " ms")
//  //
//  //    l3 === List(1000, 20, 30)
//}
