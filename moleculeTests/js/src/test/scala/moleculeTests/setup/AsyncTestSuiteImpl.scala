package moleculeTests.setup

import molecule.core.data.SchemaTransaction
import molecule.core.facade.Conn_Js
import molecule.core.marshalling.{DatomicInMemProxy, DatomicPeerProxy}
import molecule.datomic.base.facade.Conn
import moleculeBuildInfo.BuildInfo
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


trait AsyncTestSuiteImpl { self: AsyncTestSuite =>

  val isJsPlatform_ = true

  def inMem[T](
    test: Future[Conn] => T,
    schema: SchemaTransaction
  ): T = test(Future(Conn_Js(
    DatomicInMemProxy(
      schema.datomicPeer,
      schema.attrMap
    )
  )))

  def coreImpl[T](test: Future[Conn] => T): T = inMem(test, CoreTestSchema)
  def corePeerOnlyImpl[T](test: Future[Conn] => T): T = ().asInstanceOf[T] // Not used on js platform anyway
  def bidirectionalImpl[T](test: Future[Conn] => T): T = inMem(test, BidirectionalSchema)
  def partitionImpl[T](test: Future[Conn] => T): T = inMem(test, PartitionTestSchema)
  def nestedImpl[T](test: Future[Conn] => T): T = inMem(test, NestedSchema)
  def selfJoinImpl[T](test: Future[Conn] => T): T = inMem(test, SelfJoinSchema)
  def aggregateImpl[T](test: Future[Conn] => T): T = inMem(test, AggregatesSchema)
  def socialNewsImpl[T](test: Future[Conn] => T): T = inMem(test, SocialNewsSchema)
  def graphImpl[T](test: Future[Conn] => T): T = inMem(test, GraphSchema)
  def graph2Impl[T](test: Future[Conn] => T): T = inMem(test, Graph2Schema)
  def modernGraph1Impl[T](test: Future[Conn] => T): T = inMem(test, ModernGraph1Schema)
  def modernGraph2Impl[T](test: Future[Conn] => T): T = inMem(test, ModernGraph2Schema)
  def productsImpl[T](test: Future[Conn] => T): T = inMem(test, ProductsOrderSchema)
  def seattleImpl[T](test: Future[Conn] => T): T = inMem(test, SeattleSchema)

  def mbrainzImpl[T](test: Future[Conn] => Future[T]): Future[T] = {
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
    //            Datomic_Peer.connect("dev", "localhost:4334/mbrainz-1968-1973")
    test(
      Future(
        Conn_Js(
          DatomicPeerProxy(
            "dev",
            "localhost:4334/mbrainz-1968-1973",
            MBrainzSchema.datomicPeer,
            MBrainzSchema.attrMap
          )
        )
      )
    )
  }
}
