package moleculeTests.setup

import molecule.core.marshalling.Conn_Js
import molecule.datomic.base.facade.Conn
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

  def coreImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(CoreTestSchema))
  def bidirectionalImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(BidirectionalSchema))
  def partitionImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(PartitionTestSchema))
  def nestedImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(NestedSchema))
  def selfJoinImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(SelfJoinSchema))
  def aggregateImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(AggregatesSchema))
  def socialNewsImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(SocialNewsSchema))
  def graphImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(GraphSchema))
  def graph2Impl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(Graph2Schema))
  def modernGraph1Impl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(ModernGraph1Schema))
  def modernGraph2Impl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(ModernGraph2Schema))
  def productsImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(ProductsOrderSchema))
  def seattleImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(SeattleSchema))
  def mbrainzImpl[T](func: Future[Conn] => T): T = func(Conn_Js.inMem(MBrainzSchema))
}
