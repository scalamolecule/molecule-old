package moleculeTests.setup

import molecule.core.data.SchemaTransaction
import molecule.core.facade.Conn_Js
import molecule.core.marshalling.DatomicInMemProxy
import molecule.datomic.base.facade.Conn
import moleculeTests.dataModels.core.base.schema.CoreTestSchema
import moleculeTests.dataModels.core.bidirectionals.schema.BidirectionalSchema
import moleculeTests.dataModels.core.ref.schema.{NestedSchema, SelfJoinSchema}
import moleculeTests.dataModels.core.schemaDef.schema.PartitionTestSchema
import moleculeTests.dataModels.examples.datomic.dayOfDatomic.schema._
import moleculeTests.dataModels.examples.datomic.mbrainz.schema.MBrainzSchema
import moleculeTests.dataModels.examples.datomic.seattle.schema.SeattleSchema
import moleculeTests.dataModels.examples.gremlin.gettingStarted.schema.{ModernGraph1Schema, ModernGraph2Schema}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


trait AsyncTestSuiteImpl {

  val isJsPlatform_ = true

  def inMem(schema: SchemaTransaction): Future[Conn_Js] = Future(Conn_Js(
    DatomicInMemProxy(
      schema.datomicPeer,
      schema.attrMap
    )
  ))

  def coreImpl[T](func: Future[Conn] => T): T = func(inMem(CoreTestSchema))
  def bidirectionalImpl[T](func: Future[Conn] => T): T = func(inMem(BidirectionalSchema))
  def partitionImpl[T](func: Future[Conn] => T): T = func(inMem(PartitionTestSchema))
  def nestedImpl[T](func: Future[Conn] => T): T = func(inMem(NestedSchema))
  def selfJoinImpl[T](func: Future[Conn] => T): T = func(inMem(SelfJoinSchema))
  def aggregateImpl[T](func: Future[Conn] => T): T = func(inMem(AggregatesSchema))
  def socialNewsImpl[T](func: Future[Conn] => T): T = func(inMem(SocialNewsSchema))
  def graphImpl[T](func: Future[Conn] => T): T = func(inMem(GraphSchema))
  def graph2Impl[T](func: Future[Conn] => T): T = func(inMem(Graph2Schema))
  def modernGraph1Impl[T](func: Future[Conn] => T): T = func(inMem(ModernGraph1Schema))
  def modernGraph2Impl[T](func: Future[Conn] => T): T = func(inMem(ModernGraph2Schema))
  def productsImpl[T](func: Future[Conn] => T): T = func(inMem(ProductsOrderSchema))
  def seattleImpl[T](func: Future[Conn] => T): T = func(inMem(SeattleSchema))

  def mbrainzImpl[T](func: Future[Conn] => T): T = {
    func(inMem(MBrainzSchema))
  }

//  def mbrainzImpl[T](func: Future[Conn] => T): T = {
//    //    val dbName = if (system == SystemDevLocal)
//    //      "mbrainz-subset" // dev-local
//    //    else
//    //      "mbrainz-1968-1973" // peer and peer-server
//    //    implicit val conn = getConn(MBrainzSchema,
//    //      dbName,
//    //      false, // don't recreate db
//    //      "localhost:4334/mbrainz-1968-1973", // peer uri to transactor
//    //      //      "free" // if running free transactor
//    //      "dev" // if running pro transactor
//    //    )
//    //
//    //    import molecule.datomic.api.out1._
//    //
//    //    if (Schema.a(":Artist/name").get.isEmpty) {
//    //      // Add uppercase-namespaced attribute names so that we can access the externally
//    //      // transacted lowercase names with uppercase names of the molecule code.
//    //      println("Converting nss from lower to upper..")
//    //      conn.transact(MBrainzSchemaLowerToUpper.edn)
//    //    }
//    func(
//      Datomic_Peer.connect("dev", "localhost:4334/mbrainz-1968-1973")
//    )
//  }
}
