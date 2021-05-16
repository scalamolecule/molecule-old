package molecule

import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.util.UUID
import boopickle.Default._
import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy, DbException}
import molecule.datomic.api.in1_out13._
import molecule.setup.core.CoreData
import molecule.tests.core.base.dsl.CoreTest._
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.examples.datomic.dayOfDatomic.schema.GraphSchema
import org.scalajs.dom
import org.scalajs.dom.ext.{Ajax, AjaxException}
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.Date
import scala.scalajs.js.timers._
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}
import scala.util.{Failure, Success}
import scala.util.control.Exception

object AdhocTestJs extends TestSuite with CoreData {


  lazy val tests = Tests {

        implicit val exPickler = exceptionPickler


    test("core") {
      implicit val conn = Conn_Js(DatomicInMemProxy(CoreTestSchema.datomicPeer))

      def pong(i: Int): Future[Int] = {
        (for {
          either <- conn.moleculeRpc.pong(i)
        } yield either).flatMap {
          case Right(res) => Future.successful(res)
          case Left(err)  => Future.failed(err)
        }
      }
      def pang(i: Int): Future[Either[Throwable, Int]] = {
        conn.moleculeRpc.pong(i)
      }

      //      for {
      //        _ <- pong(1).map(_ ==> 100)
      //        _ <- pong(4).recover(_ ==> DbException("XXY"))
      //        _ <- pong(2).map(_ ==> 200)
      //      } yield ()


      implicit class testFutureEither[T](fut: Future[T]) {
        def ===(expectedValue: T): Future[Unit] = fut.map(_ ==> expectedValue)
      }


      for {
        //        _ <- Ns.int(1).saveAsync
        //        _ <- Ns.int.getAsync.map(_ ==> List(1))
        //        _ <- Ns.int.getAsync === List(1)

        // Insert single value for one cardinality-1 attribute
//        _ <- Ns.str insertAsync str1
//        _ <- Ns.int insertAsync int1
//        _ <- Ns.long insertAsync long1
//        _ <- Ns.float insertAsync 1.10007f
//        _ <- Ns.float insertAsync float1
//        _ <- Ns.double insertAsync 1.1
        _ <- Ns.double insertAsync 1.1f.toDouble
//        _ <- Ns.double insertAsync double1
//        _ <- Ns.bool insertAsync bool1
//        _ <- Ns.date insertAsync date1
//        _ <- Ns.uuid insertAsync uuid1
//        _ <- Ns.uri insertAsync uri1
//        _ <- Ns.enum insertAsync enum1
//
//        // Calling `get` on explicit molecule
//        _ <- m(Ns.str).getAsync === List(str1)

//        // Calling `getAsync` on implicit molecule
//        _ <- Ns.str.getAsync === List(str1)
//        _ <- Ns.str.getAsync(1) === List(str1)
//
//        // Get one value (RuntimeException if no value)
//        _ <- Ns.str.getAsync === List(str1)
//        _ <- Ns.int.getAsync === List(int1)
//        _ <- Ns.long.getAsync === List(long1)
//        _ <- Ns.float.getAsync === List(float1)
//        _ <- Ns.double.getAsync === List(double1)
//        _ <- Ns.bool.getAsync === List(bool1)
//        _ <- Ns.date.getAsync === List(date1)
//        _ <- Ns.uuid.getAsync === List(uuid1)
//        _ <- Ns.uri.getAsync === List(uri1)
//        _ <- Ns.enum.getAsync === List(enum1)
      } yield ()


      //      pong(1)
      //        .flatMap { r => r ==> 100
      //
      //          pong(2)
      //        }.flatMap { r => r ==> 200
      //
      //      }


      //      for {
      //        Right(i) <- pang(4)
      //      } yield {
      //        i ==> 100
      //      }
      //
      //      for {
      //        either <- pang(1)
      //      } yield {
      //        either match {
      //          case Right(res) => res ==> 100
      //          case Left(re)   => re.getMessage ==> "XXY"
      //        }
      //      }


      //      val right: Future[Either[String, Int]] = Future(Right(7))
      //      val left: Future[Either[String, Int]] = Future(Left("x"))
      //
      //      for{
      //        a <- right
      //        i <- a
      //        x <- left
      //
      //      } yield {
      //        val j: Int = i
      //        i
      //        val y: Int = x
      //      }

      //
      ////      val exs: Seq[Throwable] = Seq(
      ////        new NullPointerException("Noooo!"),
      ////        new IllegalArgumentException("Your argument is not valid"),
      ////        new ArrayIndexOutOfBoundsException("There's no such index as 42 here!")
      ////      )
      ////      val bb = Pickle.intoBytes(exs)
      ////      val e  = Unpickle[Seq[Throwable]].fromBytes(bb)
      ////      assert(e.zip(exs).forall(x => x._1.getMessage == x._2.getMessage && x._1.getClass == x._2.getClass))
      //
      //
      //      val e1:Throwable   = new IllegalArgumentException("XXY")
      //      val p : ByteBuffer = Pickle.intoBytes(e1)
      //
      ////      val p2 = TypedArrayBuffer.wrap(p.asInstanceOf[ArrayBuffer])
      //
      //      println("p: " + p)
      //      println("p: " + p.get(0))
      //      println("p: " + p.get(1))
      //      println("p: " + p.get(2))
      //      println("p: " + p.get(3))
      //      println("p: " + p.get(4))
      ////      println("p: " + p.get(5))
      ////      println("p: " + p2.asCharBuffer())
      ////      println("p: " + p2.asCharBuffer().array().mkString("Array(", ", ", ")"))
      ////      println("p: " + p2.array().mkString("Array(", ", ", ")"))
      //      val e2  = Unpickle.apply[Throwable].fromBytes(p)
      //      e2.getMessage ==> e1.getMessage
      //      e2.getClass ==> e1.getClass
      //
      //      println(e1.getStackTrace.mkString("Array(\n", "\n", "\n)"))


    }
  }




  //    test("Simple hyperedge") {
  //      import molecule.tests.examples.datomic.dayOfDatomic.dsl.Graph._
  //      implicit val conn = Conn_Js(DatomicInMemProxy(GraphSchema.datomicPeer))
  //
  //      // User 1 Roles in Group 2
  //      User.name_("User1")
  //        .RoleInGroup.Group.name_("Group2")
  //        ._RoleInGroup.Role.name.inspectGet
  //
  //    }
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
