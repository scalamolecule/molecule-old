package molecule.setup

import molecule.datomic.base.facade.Conn
import molecule.datomic.peer.facade.Datomic_Peer
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.core.bidirectionals.schema.BidirectionalSchema
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.core.bidirectionals.schema.BidirectionalSchema
import molecule.tests.core.ref.schema.{NestedSchema, SelfJoinSchema}
import molecule.tests.core.schemaDef.schema.PartitionTestSchema
import molecule.tests.examples.datomic.dayOfDatomic.schema._
import molecule.tests.examples.datomic.mbrainz.schema.{MBrainzSchema, MBrainzSchemaLowerToUpper}
import molecule.tests.examples.datomic.seattle.schema.SeattleSchema
import molecule.tests.examples.gremlin.gettingStarted.schema.{ModernGraph1Schema, ModernGraph2Schema}


trait AsyncTestSuiteImpl {

  def coreImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(CoreTestSchema))

  def bidirectionalImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(BidirectionalSchema))
  def partitionImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(PartitionTestSchema))
  def nestedImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(NestedSchema))
  def selfJoinImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(SelfJoinSchema))
  def aggregateImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(AggregatesSchema))
  def socialNewsImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(SocialNewsSchema))
  def graphImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(GraphSchema))
  def graph2Impl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(Graph2Schema))
  def modernGraph1Impl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(ModernGraph1Schema))
  def modernGraph2Impl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(ModernGraph2Schema))
  def productsImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(ProductsOrderSchema))
  def seattleImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(SeattleSchema))
  def mbrainzImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(MBrainzSchema))
}
