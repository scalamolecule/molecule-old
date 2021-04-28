package molecule

import java.io.StringReader
import java.net.URI
import java.util
import java.util.Collections
import clojure.lang.Keyword
import datomicClient.ClojureBridge
import datomicJava.client.api.async.AsyncDatomic.require
import molecule.core.transform.ModelTransformer
import molecule.core.util.JavaUtil
import molecule.datomic.api.out3._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.marshalling.DatomicRpc.readString
import molecule.datomic.peer.facade.Datomic_Peer._
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.core.schemaDef.schema.PartitionTestSchema
import org.specs2.mutable.Specification

//class AdHocTest extends molecule.setup.TestSpec with Helpers {
class AdHocTestJvm extends Specification with ClojureBridge with JavaUtil {


  "core" >> {
    import molecule.tests.core.base.dsl.CoreTest._
    implicit val conn: Conn = recreateDbFrom(CoreTestSchema)


    require("clojure.core.async")
    require("cognitect.anomalies")
    require("datomic.client.api.async")

    //    readString("""{:ns/uri #=(new java.net.URI "https://www.datomic.com/details.html")}""") === 7

    val edn =
      """[
        |[:db/add #db/id[:db.part/user -1000001] :Ns/str "a"]
        |[:db/add #db/id[:db.part/user -1000001] :Ns/uri https://www.datomic.com/details.html]
        |]""".stripMargin

    val edn1 =
      """[
        |[:db/add #db/id[:db.part/user -1000001] :Ns/str "a"]
        |[:db/add #db/id[:db.part/user -1000001] :Ns/uri #=(new java.net.URI "https://www.datomic.com/details.html")]
        |]""".stripMargin


    //    val reader = new StringReader(edn)

    //    import java.io.BufferedReader
    //    val bufReader = new BufferedReader(reader)
    //
    import _root_.datomic.Util._
    //
    //    bufReader.lines().forEach(line =>
    //      println("@ " + line)
    //    )

    import java.util.{List => jList}
    //
    //
    //    println(readAll(new StringReader(edn)))
    //    println(readAll(reader).getClass)
    //
    //
    //    val all = readAll(reader)

    val uriAttrs = Set(":Ns/uri")


    readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]].forEach { l =>
      println(l)
      println(l.asInstanceOf[jList[_]].get(2).getClass)
    }
    println("-------")
    //    println(readString("""#=(new java.net.URI "https://www.datomic.com/details.html")"""))
    //    println(readString("""#=(new java.net.URI "https://www.datomic.com/details.html")""").getClass)


    val javaList = {
      val stmts = readAll(new StringReader(edn)).get(0).asInstanceOf[jList[AnyRef]]
      if (uriAttrs.isEmpty) stmts else {
        val stmtsSize = stmts.size()
        val newStmts   = new util.ArrayList[jList[_]](stmtsSize)
        var i         = 0
        stmts.forEach { stmtRaw =>
          val stmt = stmtRaw.asInstanceOf[jList[AnyRef]]
          if (uriAttrs.contains(stmt.get(2).toString)) {
            val uri = readString(s"""#=(new java.net.URI "${stmt.get(3)}")""")
            val uriStmt = Util.list(stmt.get(0), stmt.get(1), stmt.get(2), uri)
            newStmts.add(uriStmt)
          } else {
            newStmts.add(stmt)
          }
          i += 1
        }
        Collections.unmodifiableList(newStmts)
      }
    }

    javaList.forEach { stmt =>
      println(stmt)
      println(stmt.asInstanceOf[jList[_]].get(3).getClass)
      println(stmt.asInstanceOf[jList[_]].get(3).isInstanceOf[URI])
    }



    //    readAll(new StringReader("""[:db/add #db/id[:db.part/user -1000001] :Ns/uri  #=(new java.net.URI "https://www.datomic.com/details.html")]""")) === 7

    //    readAll()


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
