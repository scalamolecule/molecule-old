package moleculeTests.setup

import molecule.core.data.SchemaTransaction
import molecule.core.marshalling.DatomicInMemProxy
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.SystemPeer
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeBuildInfo.BuildInfo
import moleculeTests.dataModels.core.base.schema.CoreTestSchema
import moleculeTests.dataModels.core.bidirectionals.schema.BidirectionalSchema
import moleculeTests.dataModels.core.ref.schema.{NestedSchema, SelfJoinSchema}
import moleculeTests.dataModels.core.schemaDef.schema.PartitionTestSchema
import moleculeTests.dataModels.examples.datomic.dayOfDatomic.schema._
import moleculeTests.dataModels.examples.datomic.seattle.schema.SeattleSchema
import moleculeTests.dataModels.examples.gremlin.gettingStarted.schema.{ModernGraph1Schema, ModernGraph2Schema}
import moleculeTests.jvm.core.transaction.TxFunction.system
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait AsyncTestSuiteImpl {

  val isJsPlatform_ = false

  def inMem(schema: SchemaTransaction): Future[Conn] = {
    Datomic_Peer.recreateDbFrom(
      schema,
      connProxy = DatomicInMemProxy(
        schema.datomicPeer,
        schema.attrMap
      )
    )
  }

  def coreImpl[T](test: Future[Conn] => T): T = test(inMem(CoreTestSchema))

  def coreTxFnImpl[T](test: Future[Conn] => T): T = {
    if (system == SystemPeer)
      test(inMem(CoreTestSchema))
    else
      ().asInstanceOf[T]
  }

  def bidirectionalImpl[T](test: Future[Conn] => T): T = test(inMem(BidirectionalSchema))
  def partitionImpl[T](test: Future[Conn] => T): T = test(inMem(PartitionTestSchema))
  def nestedImpl[T](test: Future[Conn] => T): T = test(inMem(NestedSchema))
  def selfJoinImpl[T](test: Future[Conn] => T): T = test(inMem(SelfJoinSchema))
  def aggregateImpl[T](test: Future[Conn] => T): T = test(inMem(AggregatesSchema))
  def socialNewsImpl[T](test: Future[Conn] => T): T = test(inMem(SocialNewsSchema))
  def graphImpl[T](test: Future[Conn] => T): T = test(inMem(GraphSchema))
  def graph2Impl[T](test: Future[Conn] => T): T = test(inMem(Graph2Schema))
  def modernGraph1Impl[T](test: Future[Conn] => T): T = test(inMem(ModernGraph1Schema))
  def modernGraph2Impl[T](test: Future[Conn] => T): T = test(inMem(ModernGraph2Schema))
  def productsImpl[T](test: Future[Conn] => T): T = test(inMem(ProductsOrderSchema))
  def seattleImpl[T](test: Future[Conn] => T): T = test(inMem(SeattleSchema))

  def mbrainzImpl[T](test: Future[Conn] => T): T = {
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
    test(
      Datomic_Peer.connect("dev", "localhost:4334/mbrainz-1968-1973")
    )
  }
}
