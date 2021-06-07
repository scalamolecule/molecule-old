package moleculeTests.setup

import molecule.datomic.base.facade.Conn
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeTests.tests.core.base.schema.CoreTestSchema
import moleculeTests.tests.core.bidirectionals.schema.BidirectionalSchema
import moleculeTests.tests.core.ref.schema.{NestedSchema, SelfJoinSchema}
import moleculeTests.tests.core.schemaDef.schema.PartitionTestSchema
import moleculeTests.tests.examples.datomic.dayOfDatomic.schema._
import moleculeTests.tests.examples.datomic.mbrainz.schema.MBrainzSchema
import moleculeTests.tests.examples.datomic.seattle.schema.SeattleSchema
import moleculeTests.tests.examples.gremlin.gettingStarted.schema.{ModernGraph1Schema, ModernGraph2Schema}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait AsyncTestSuiteImpl {

  def coreImpl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(CoreTestSchema))
  def bidirectionalImpl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(BidirectionalSchema))
  def partitionImpl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(PartitionTestSchema))
  def nestedImpl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(NestedSchema))
  def selfJoinImpl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(SelfJoinSchema))
  def aggregateImpl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(AggregatesSchema))
  def socialNewsImpl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(SocialNewsSchema))
  def graphImpl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(GraphSchema))
  def graph2Impl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(Graph2Schema))
  def modernGraph1Impl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(ModernGraph1Schema))
  def modernGraph2Impl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(ModernGraph2Schema))
  def productsImpl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(ProductsOrderSchema))
  def seattleImpl[T](func: Future[Conn] => T): T = func(Datomic_Peer.recreateDbFrom(SeattleSchema))

  def mbrainzImpl[T](func: Future[Conn] => T): T = {
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
    func(
      Datomic_Peer.connect("dev", "localhost:4334/mbrainz-1968-1973")
    )
  }
}