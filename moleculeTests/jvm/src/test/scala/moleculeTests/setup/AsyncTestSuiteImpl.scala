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
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait AsyncTestSuiteImpl { self: AsyncTestSuite =>

  val isJsPlatform_ = false

  def inMem[T](
    test: Future[Conn] => T,
    schema: SchemaTransaction,
    db: String
  ): T = system match {
    case SystemPeer =>
      test(Datomic_Peer.recreateDbFrom(
        schema,
        connProxy = DatomicInMemProxy(schema.datomicPeer, schema.attrMap)
      ))

    case SystemDevLocal =>
      // Unique random db names to avoid overlapping asynchronous db calls to same-name dbs
      val db = UUID.randomUUID().toString
      test(Datomic_DevLocal("datomic-samples-temp", datomicHome).recreateDbFrom(
        schema, db,
        DatomicDevLocalProxy("datomic-samples-temp", datomicHome, db, schema.datomicPeer, schema.attrMap)
      ))

    case SystemPeerServer =>
      test(CleanPeerServer.getCleanPeerServerConn(
        Datomic_PeerServer("k", "s", "localhost:8998"), db, schema,
        DatomicPeerServerProxy("k", "s", "localhost:8998", db, schema.datomicPeer, schema.attrMap)
      ))
  }

  def coreImpl[T](test: Future[Conn] => T): T = inMem(test, CoreTestSchema, "m_coretests")
  def corePeerOnlyImpl[T](test: Future[Conn] => T): T = if (system == SystemPeer) coreImpl(test) else ().asInstanceOf[T]
  def bidirectionalImpl[T](test: Future[Conn] => T): T = inMem(test, BidirectionalSchema, "m_bidirectional")
  def partitionImpl[T](test: Future[Conn] => T): T = inMem(test, PartitionTestSchema, "m_partitions")
  def nestedImpl[T](test: Future[Conn] => T): T = inMem(test, NestedSchema, "m_nested")
  def selfJoinImpl[T](test: Future[Conn] => T): T = inMem(test, SelfJoinSchema, "m_selfjoin")
  def aggregateImpl[T](test: Future[Conn] => T): T = inMem(test, AggregatesSchema, "m_aggregates")
  def socialNewsImpl[T](test: Future[Conn] => T): T = inMem(test, SocialNewsSchema, "m_socialNews")
  def graphImpl[T](test: Future[Conn] => T): T = inMem(test, GraphSchema, "m_graph")
  def graph2Impl[T](test: Future[Conn] => T): T = inMem(test, Graph2Schema, "m_graph2")
  def modernGraph1Impl[T](test: Future[Conn] => T): T = inMem(test, ModernGraph1Schema, "m_modernGraph1")
  def modernGraph2Impl[T](test: Future[Conn] => T): T = inMem(test, ModernGraph2Schema, "m_modernGraph2")
  def productsImpl[T](test: Future[Conn] => T): T = inMem(test, ProductsOrderSchema, "m_productsOrder")
  def seattleImpl[T](test: Future[Conn] => T): T = inMem(test, SeattleSchema, "m_seattle")


  // Connecting to existing MBrainz database without recreating it
  def mbrainzImpl[T](test: Future[Conn] => Future[T]): Future[T] = {
    implicit val futConn: Future[Conn] = system match {
      case SystemPeer =>
        val protocol     = "dev" // or "free" if running free transactor
        val dbIdentifier = "localhost:4334/mbrainz-1968-1973"
        Datomic_Peer.connect(
          protocol, dbIdentifier,
          DatomicPeerProxy(protocol, dbIdentifier, MBrainzSchema.datomicPeer, MBrainzSchema.attrMap)
        )

      case SystemDevLocal =>
        val dbName = "mbrainz-subset"
        Datomic_DevLocal("datomic-samples", datomicHome).connect(
          dbName,
          DatomicDevLocalProxy("datomic-samples", datomicHome, dbName, MBrainzSchema.datomicPeer, MBrainzSchema.attrMap)
        )

      case SystemPeerServer =>
        val dbName = "mbrainz-1968-1973"
        Datomic_PeerServer("k", "s", "localhost:8998").connect(
          dbName,
          DatomicPeerServerProxy("k", "s", "localhost:8998", dbName, MBrainzSchema.datomicPeer, MBrainzSchema.attrMap)
        )
    }
    for {
      conn <- futConn
      upperCase <- Schema.a(":Artist/name").get
      _ <- if (upperCase.isEmpty) {
        // Add uppercase-namespaced attribute names so that we can access the externally
        // transacted lowercase names with uppercase names of the molecule code.
        println("Adding uppercase namespace names to MBrainz database..")
        conn.transact(MBrainzSchemaLowerToUpper.edn)
      } else Future.unit
      res <- test(futConn)
    } yield res
  }
}
