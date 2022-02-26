package moleculeTests.setup

import java.util.UUID.randomUUID
import molecule.core.data.SchemaTransaction
import molecule.core.util.EmptySchema
import molecule.core.util.Executor._
import molecule.datomic.api.out1._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import molecule.datomic.client.facade.{Datomic_DevLocal, Datomic_PeerServer}
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeBuildInfo.BuildInfo
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

  lazy val isJsPlatform_ = false
  lazy val protocol_     = BuildInfo.datomicProtocol
  lazy val useFree_      = BuildInfo.datomicUseFree

  def inMem[T](
    test: Future[Conn] => T,
    schemaTx: SchemaTransaction,
    db: String
  ): T = {
    val dbUri   = if (protocol_ == "mem") "" else {
      println(s"Re-creating live `$db` database...")
      "localhost:4334/" + randomUUID().toString
    }
    val futConn = system match {
      case SystemPeer       => Datomic_Peer.recreateDbFrom(schemaTx, protocol_, dbUri)
      case SystemDevLocal   => Datomic_DevLocal("datomic-samples-temp", datomicHome).recreateDbFrom(schemaTx)
      case SystemPeerServer => Datomic_PeerServer("k", "s", "localhost:8998").connect(schemaTx, db)
    }
    test(futConn)
  }

  def emptyImpl[T](test: Future[Conn] => T): T = inMem(test, EmptySchema, "")
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
        Datomic_Peer
          .connect(MBrainzSchema, "dev", "localhost:4334/mbrainz-1968-1973")

      case SystemDevLocal =>
        Datomic_DevLocal("datomic-samples", datomicHome)
          .connect(MBrainzSchema, "dev", "mbrainz-subset")

      case SystemPeerServer =>
        Datomic_PeerServer("k", "s", "localhost:8998")
          .connect(MBrainzSchema, "mbrainz-1968-1973")
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
