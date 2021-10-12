package moleculeTests.setup

import java.util.UUID
import molecule.core.data.SchemaTransaction
import molecule.core.marshalling.{DatomicDevLocalProxy, DatomicInMemProxy, DatomicPeerProxy, DatomicPeerServerProxy}
import molecule.datomic.api.out1._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import molecule.datomic.client.facade.{Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeBuildInfo.BuildInfo.datomicHome
import moleculeTests.dataModels.core.base.schema.CoreTestSchema
import moleculeTests.dataModels.core.bidirectionals.schema.BidirectionalSchema
import moleculeTests.dataModels.core.ref.schema.{NestedSchema, SelfJoinSchema}
import moleculeTests.dataModels.core.schemaDef.schema.PartitionTestSchema
import moleculeTests.dataModels.examples.datomic.dayOfDatomic.schema._
import moleculeTests.dataModels.examples.datomic.mbrainz.schema.{MBrainzSchema, MBrainzSchemaLowerToUpper}
import moleculeTests.dataModels.examples.datomic.seattle.schema.SeattleSchema
import moleculeTests.dataModels.examples.gremlin.gettingStarted.schema.{ModernGraph1Schema, ModernGraph2Schema}
import scala.concurrent.Future


trait AsyncTestSuiteImpl { self: AsyncTestSuite =>

  val isJsPlatform_ = false

  def inMem(schema: SchemaTransaction, db: String): Future[Conn] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    system match {
      case SystemPeer => Datomic_Peer.recreateDbFrom(
        schema,
        connProxy = DatomicInMemProxy(schema.datomicPeer, schema.attrMap)
      )

      case SystemPeerServer => CleanPeerServer.getCleanPeerServerConn(
        Datomic_PeerServer("k", "s", "localhost:8998"), db, schema,
        DatomicPeerServerProxy("k", "s", "localhost:8998", db, schema.datomicPeer, schema.attrMap)
      )

      case SystemDevLocal =>
        // Unique random db names to avoid overlapping asynchronous db calls to same-name dbs
        val db = UUID.randomUUID().toString
        Datomic_DevLocal("datomic-samples", datomicHome).recreateDbFrom(
          schema, db,
          DatomicDevLocalProxy("datomic-samples", datomicHome, db, schema.datomicPeer, schema.attrMap)
        )

//        Datomic_DevLocal("datomic-samples", datomicHome).recreateDbFrom(
//          schema, db,
//          DatomicDevLocalProxy("datomic-samples", datomicHome, db, schema.datomicPeer, schema.attrMap)
//        )
    }
  }

  def coreImpl[T](test: Future[Conn] => T): T = test(inMem(CoreTestSchema, "m_coretests"))

  def corePeerOnlyImpl[T](test: Future[Conn] => T): T = {
    if (system == SystemPeer)
      test(inMem(CoreTestSchema, "m_coretests"))
    else
      ().asInstanceOf[T] // No testing done, but shows success in output
  }

  def bidirectionalImpl[T](test: Future[Conn] => T): T = test(inMem(BidirectionalSchema, "m_bidirectional"))
  def partitionImpl[T](test: Future[Conn] => T): T = test(inMem(PartitionTestSchema, "m_partitions"))
  def nestedImpl[T](test: Future[Conn] => T): T = test(inMem(NestedSchema, "m_nested"))
  def selfJoinImpl[T](test: Future[Conn] => T): T = test(inMem(SelfJoinSchema, "m_selfjoin"))
  def aggregateImpl[T](test: Future[Conn] => T): T = test(inMem(AggregatesSchema, "m_aggregates"))
  def socialNewsImpl[T](test: Future[Conn] => T): T = test(inMem(SocialNewsSchema, "m_socialNews"))
  def graphImpl[T](test: Future[Conn] => T): T = test(inMem(GraphSchema, "m_graph"))
  def graph2Impl[T](test: Future[Conn] => T): T = test(inMem(Graph2Schema, "m_graph2"))
  def modernGraph1Impl[T](test: Future[Conn] => T): T = test(inMem(ModernGraph1Schema, "m_modernGraph1"))
  def modernGraph2Impl[T](test: Future[Conn] => T): T = test(inMem(ModernGraph2Schema, "m_modernGraph2"))
  def productsImpl[T](test: Future[Conn] => T): T = test(inMem(ProductsOrderSchema, "m_productsOrder"))
  def seattleImpl[T](test: Future[Conn] => T): T = test(inMem(SeattleSchema, "m_seattle"))

  // Connecting to existing MBrainz database without recreating it
  def mbrainzImpl[T](test: Future[Conn] => T): Future[T] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val futConn: Future[Conn] = system match {
      case SystemPeer =>
        val protocol     = "dev" // or "free" if running free transactor
        val dbIdentifier = "localhost:4334/mbrainz-1968-1973"
        Datomic_Peer.connect(
          protocol,
          dbIdentifier,
          DatomicPeerProxy(protocol, dbIdentifier, MBrainzSchema.datomicPeer, MBrainzSchema.attrMap)
        )

      case SystemPeerServer =>
        val dbName = "mbrainz-1968-1973"
        Datomic_PeerServer("k", "s", "localhost:8998").connect(
          dbName,
          DatomicPeerServerProxy("k", "s", "localhost:8998", dbName, MBrainzSchema.datomicPeer, MBrainzSchema.attrMap)
        )

      case SystemDevLocal =>
        val dbName = "mbrainz-subset"
        Datomic_DevLocal("datomic-samples", datomicHome).connect(
          dbName,
          DatomicDevLocalProxy("datomic-samples", datomicHome, dbName, MBrainzSchema.datomicPeer, MBrainzSchema.attrMap)
        )
    }
    for {
      conn <- futConn
      _ <- Schema.a(":Artist/name").get.collect {
        case Nil =>
          // Add uppercase-namespaced attribute names so that we can access the externally
          // transacted lowercase names with uppercase names of the molecule code.
          println("Adding uppercase namespace names to MBrainz database..")
          conn.transact(MBrainzSchemaLowerToUpper.edn)
      }
    } yield test(futConn)
  }
}
