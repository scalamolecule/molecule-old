package moleculeTests.setup

import molecule.core.data.SchemaTransaction
import molecule.core.facade.Conn_Js
import molecule.core.marshalling.{DatomicDevLocalProxy, DatomicPeerProxy, DatomicPeerServerProxy}
import molecule.core.util.EmptySchema
import molecule.core.util.Executor._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeBuildInfo.BuildInfo
import moleculeBuildInfo.BuildInfo.datomicHome
import moleculeTests.dataModels.core.base.schema.CoreTestSchema
import moleculeTests.dataModels.core.bidirectionals.schema.BidirectionalSchema
import moleculeTests.dataModels.core.ref.schema.{NestedSchema, SelfJoinSchema}
import moleculeTests.dataModels.core.schemaDef.schema.PartitionTestSchema
import moleculeTests.dataModels.examples.datomic.dayOfDatomic.schema._
import moleculeTests.dataModels.examples.datomic.mbrainz.schema.MBrainzSchema
import moleculeTests.dataModels.examples.datomic.seattle.schema.SeattleSchema
import moleculeTests.dataModels.examples.gremlin.gettingStarted.schema.{ModernGraph1Schema, ModernGraph2Schema}
import scala.concurrent.Future


trait AsyncTestSuiteImpl { self: AsyncTestSuite =>

  lazy val isJsPlatform_ = true
  lazy val protocol_     = BuildInfo.datomicProtocol
  lazy val useFree_      = BuildInfo.datomicUseFree

  def inMem[T](
    test: Future[Conn] => T,
    schema: SchemaTransaction,
    peerServerDb: String
  ): T = {
    val (peerSchema, clientSchema, nsMap, attrMap) = (schema.datomicPeer, schema.datomicClient, schema.nsMap, schema.attrMap)

    val proxy = system match {
      case SystemPeer       => DatomicPeerProxy("mem", "", peerSchema, nsMap, attrMap)
      case SystemDevLocal   => DatomicDevLocalProxy("mem", "datomic-samples-temp", datomicHome, "", clientSchema, nsMap, attrMap)
      case SystemPeerServer => DatomicPeerServerProxy("k", "s", "localhost:8998", peerServerDb, clientSchema, nsMap, attrMap)
    }
    test(Future(Conn_Js(proxy, "localhost", 8080)))
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
    val proxy = system match {
      case SystemPeer =>
        DatomicPeerProxy(
          "dev",
          "localhost:4334/mbrainz-1968-1973",
          MBrainzSchema.datomicPeer, MBrainzSchema.nsMap, MBrainzSchema.attrMap
        )

      case SystemDevLocal =>
        DatomicDevLocalProxy(
          "dev",
          "datomic-samples",
          datomicHome,
          "mbrainz-subset",
          MBrainzSchema.datomicClient, MBrainzSchema.nsMap, MBrainzSchema.attrMap
        )

      case SystemPeerServer =>
        DatomicPeerServerProxy(
          "k", "s", "localhost:8998",
          "mbrainz-1968-1973",
          MBrainzSchema.datomicClient, MBrainzSchema.nsMap, MBrainzSchema.attrMap
        )
    }
    test(Future(Conn_Js(proxy, "localhost", 8080)))
  }
}
