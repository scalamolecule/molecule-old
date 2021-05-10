//package molecule.setup
//
//import java.util.UUID
//import molecule.core.data.SchemaTransaction
//import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy, DatomicPeerProxy}
//import molecule.core.util.testing.MoleculeTestHelper
//import molecule.datomic.base.facade.Conn
//import molecule.datomic.base.util.{System, SystemDevLocal, SystemPeer, SystemPeerServer}
//import molecule.setup.core.CoreData
//import molecule.setup.examples.datomic.dayOfDatomic.SocialNewsData
//import molecule.setup.examples.datomic.seattle.SeattleLoader
//import molecule.tests.core.base.schema.CoreTestSchema
//import molecule.tests.core.bidirectionals.schema.BidirectionalSchema
//import moleculeBuildInfo.BuildInfo._
//import org.specs2.mutable.Specification
//import org.specs2.specification.Scope
//import org.specs2.specification.core.{Fragments, Text}
//
//
//class TestSpec extends Specification with MoleculeTestHelper with CoreData {
//  sequential
//  var system    : System             = SystemPeer
////  var peer      : Datomic_Peer       = null
////  var peerServer: Datomic_PeerServer = null
////  var devLocal  : Datomic_DevLocal   = null
//  val heavyInputTesting              = false
//  var setupException                 = Option.empty[Throwable]
//  var basisT    : Long               = 0L
//  def basisTx: Long =  ??? //Peer.toTx(basisT).asInstanceOf[Long]
//
//  // What systems to test (can be a single, two or three systems in any order)
//  // Set this variable in a test to specify which system should run it
//  // 1: Peer   2: Peer-server   3: Dev-local
//  var tests = 1
//  def addSystem(fs: => Fragments, system: String) = fs.mapDescription {
//    case Text(t)    => Text(s"$system        $t")
//    case otherDescr => otherDescr
//  }
//  override def map(fs: => Fragments): Fragments = {
//    tests.toString.getBytes.map {
//      case 49 /* 1 */ => step(setupPeer()) ^ addSystem(fs, "peer       ")
//      //      case 50 /* 2 */ => step(setupPeerServer()) ^ addSystem(fs, "peer-server")
//      //      case 51 /* 3 */ => step(setupDevLocal()) ^ addSystem(fs, "dev-local  ")
//    }.reduce(_ ^ _)
//  }
//
//  def setupPeer(): Unit = {
//    system = SystemPeer
////    peer = Datomic_Peer
//  }
//  //  def setupPeerServer(): Unit = {
//  //    system = SystemPeerServer
//  //    try {
//  //      peerServer = Datomic_PeerServer("k", "s", "localhost:8998")
//  //    } catch {
//  //      case e: Throwable => setupException = Some(e)
//  //    }
//  //  }
//  //  def setupDevLocal(): Unit = {
//  //    system = SystemDevLocal
//  //    try {
//  //      devLocal = Datomic_DevLocal("datomic-samples", datomicHome)
//  //    } catch {
//  //      case e: Throwable => setupException = Some(e)
//  //    }
//  //  }
//  lazy val seattle = Seq(
//    // Attributes
//    """
//     [
//       ;; Community -----------------------------------------
//
//       {:db/ident         :Community/name
//        :db/valueType     :db.type/string
//        :db/cardinality   :db.cardinality/one
//        :db/fulltext      true
//        :db/doc           "A community's name"
//        :db/index         true}
//
//       {:db/ident         :Community/url
//        :db/valueType     :db.type/string
//        :db/cardinality   :db.cardinality/one
//        :db/doc           "A community's url"
//        :db/index         true}
//
//       {:db/ident         :Community/category
//        :db/valueType     :db.type/string
//        :db/cardinality   :db.cardinality/many
//        :db/fulltext      true
//        :db/doc           "Community categories"
//        :db/index         true}
//
//       {:db/ident         :Community/orgtype
//        :db/valueType     :db.type/ref
//        :db/cardinality   :db.cardinality/one
//        :db/doc           "Community organisation type"
//        :db/index         true}
//
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.orgtype/community]
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.orgtype/commercial]
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.orgtype/nonprofit]
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.orgtype/personal]
//
//       {:db/ident         :Community/type
//        :db/valueType     :db.type/ref
//        :db/cardinality   :db.cardinality/one
//        :db/doc           "Community type"
//        :db/index         true}
//
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.tpe/email_list]
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.tpe/twitter]
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.tpe/facebook_page]
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.tpe/blog]
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.tpe/website]
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.tpe/wiki]
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.tpe/myspace]
//       [:db/add #db/id[:db.part/user]  :db/ident :Community.tpe/ning]
//
//       {:db/ident         :Community/neighborhood
//        :db/valueType     :db.type/ref
//        :db/cardinality   :db.cardinality/one
//        :db/doc           "A community's neighborhood"
//        :db/index         true}
//
//
//       ;; Neighborhood --------------------------------------
//
//       {:db/ident         :Neighborhood/name
//        :db/valueType     :db.type/string
//        :db/cardinality   :db.cardinality/one
//        :db/doc           "A unique neighborhood name"
//        :db/index         true}
//
//       {:db/ident         :Neighborhood/district
//        :db/valueType     :db.type/ref
//        :db/cardinality   :db.cardinality/one
//        :db/doc           "A neighborhood's district"
//        :db/index         true}
//
//
//       ;; District ------------------------------------------
//
//       {:db/ident         :District/name
//        :db/valueType     :db.type/string
//        :db/cardinality   :db.cardinality/one
//        :db/doc           "A unique district name"
//        :db/index         true}
//
//       {:db/ident         :District/region
//        :db/valueType     :db.type/ref
//        :db/cardinality   :db.cardinality/one
//        :db/doc           "A district region"
//        :db/index         true}
//
//       [:db/add #db/id[:db.part/user]  :db/ident :District.region/n]
//       [:db/add #db/id[:db.part/user]  :db/ident :District.region/ne]
//       [:db/add #db/id[:db.part/user]  :db/ident :District.region/e]
//       [:db/add #db/id[:db.part/user]  :db/ident :District.region/se]
//       [:db/add #db/id[:db.part/user]  :db/ident :District.region/s]
//       [:db/add #db/id[:db.part/user]  :db/ident :District.region/sw]
//       [:db/add #db/id[:db.part/user]  :db/ident :District.region/w]
//       [:db/add #db/id[:db.part/user]  :db/ident :District.region/nw]
//     ]
//    """,
//
//    // Aliases
//    """
//     [
//       {:db/id     :Community/type
//        :db/ident  :Community/tpe}
//     ]
//    """
//  )
//
//  def getConn(
//    schema: SchemaTransaction,
//    db: String,
//    recreateDb: Boolean = true,
//    uri: String = "",
//    protocol: String = ""
//  ): Conn = {
//    // Throw potential setup error
//    setupException.fold(())(throw _)
//    system match {
//      case SystemPeer =>
////        if (db == "m_seattle")
////          peer.recreateDbFromEdn(seattle)
////        else
//          if (recreateDb)
//          Conn_Js(DatomicInMemProxy(schema.datomicPeer))
//        else
//          Conn_Js(DatomicPeerProxy(protocol, uri))
//
//
//      //      case SystemPeerServer =>
//      //        if (recreateDb) {
//      //          val (conn, newBasisT) = CleanPeerServer.getCleanPeerServerConn(
//      //            peerServer, db, schema, basisT
//      //          )
//      //          basisT = newBasisT
//      //          conn
//      //        } else
//      //          peerServer.connect(db)
//      //
//      //      case SystemDevLocal =>
//      //        if (recreateDb)
//      //          devLocal.recreateDbFrom(schema, db)
//      //        else
//      //          devLocal.connect(db)
//    }
//  }
//
//  // Entry points, core tests
//  class CoreSetup extends Scope {
//    implicit val conn: Conn = getConn(CoreTestSchema, "m_coretests")
//  }
//  class BidirectionalSetup extends Scope {
//    implicit val conn = getConn(BidirectionalSchema, "m_bidirectional")
//  }
//  class PartitionSetup extends SpecHelpers with Scope {
//    implicit val conn = getConn(PartitionTestSchema, "m_partitions")
//  }
//  class NestedSetup extends Scope {
//    implicit val conn = getConn(NestedSchema, "m_nested")
//  }
//  class SelfJoinSetup extends Scope {
//    implicit val conn = getConn(SelfJoinSchema, "m_selfjoin")
//  }
//
//  // Entry points, examples
//  class AggregateSetup extends Scope {
//    implicit val conn = getConn(AggregatesSchema, "m_aggregates")
//  }
//  class SocialNewsSetup extends SocialNewsData(
//    getConn(SocialNewsSchema, "m_socialNews")) with Scope
//
//  class GraphSetup extends Scope {
//    implicit val conn = getConn(GraphSchema, "m_graph")
//  }
//  class Graph2Setup extends Scope {
//    implicit val conn = getConn(Graph2Schema, "m_graph2")
//  }
//  class ModernGraph1Setup extends Scope {
//    implicit val conn = getConn(ModernGraph1Schema, "m_modernGraph1")
//  }
//  class ModernGraph2Setup extends Scope {
//    implicit val conn = getConn(ModernGraph2Schema, "m_modernGraph2")
//  }
//  class ProductsSetup extends Scope {
//    implicit val conn = getConn(ProductsOrderSchema, "m_productsOrder")
//  }
//  class SeattleSetup extends SeattleLoader(
//    getConn(SeattleSchema, "m_seattle")
//  ) with Scope
//
//  class MBrainzSetup extends Scope {
//    val dbName = if (system == SystemDevLocal)
//      "mbrainz-subset" // dev-local
//    else
//      "mbrainz-1968-1973" // peer and peer-server
//    implicit val conn = getConn(MBrainzSchema,
//      dbName,
//      false, // don't recreate db
//      "localhost:4334/mbrainz-1968-1973", // peer uri to transactor
//      //      "free" // if running free transactor
//      "dev" // if running pro transactor
//    )
//
//    import molecule.datomic.api.out1._
//
//    if (Schema.a(":Artist/name").get.isEmpty) {
//      // Add uppercase-namespaced attribute names so that we can access the externally
//      // transacted lowercase names with uppercase names of the molecule code.
//      println("Converting nss from lower to upper..")
//      conn.transact(MBrainzSchemaLowerToUpper.edn)
//    }
//  }
//}
