package molecule.setup

import molecule.datomic.base.facade.Conn
import molecule.core.marshalling.Conn_Js
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.core.bidirectionals.schema.BidirectionalSchema
import molecule.core.data.SchemaTransaction
import molecule.core.util.testing.MoleculeTestHelper
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{System, SystemDevLocal, SystemPeer, SystemPeerServer}
import molecule.setup.core.CoreData
import molecule.setup.examples.datomic.dayOfDatomic.SocialNewsData
import molecule.setup.examples.datomic.seattle.SeattleLoader
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.core.bidirectionals.schema.BidirectionalSchema
import molecule.tests.core.ref.schema.{NestedSchema, SelfJoinSchema}
import molecule.tests.core.schemaDef.schema.PartitionTestSchema
import molecule.tests.examples.datomic.dayOfDatomic.schema._
import molecule.tests.examples.datomic.mbrainz.schema.{MBrainzSchema, MBrainzSchemaLowerToUpper}
import molecule.tests.examples.datomic.seattle.schema.SeattleSchema
import molecule.tests.examples.gremlin.gettingStarted.schema.{ModernGraph1Schema, ModernGraph2Schema}
import moleculeBuildInfo.BuildInfo._
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import org.specs2.specification.core.{Fragments, Text}


trait AsyncTestSuiteImpl {

  def coreImpl[T](func: Conn => T): T = func(Conn_Js.inMem(CoreTestSchema))
  def bidirectionalImpl[T](func: Conn => T): T = func(Conn_Js.inMem(BidirectionalSchema))
  def partitionImpl[T](func: Conn => T): T = func(Conn_Js.inMem(PartitionTestSchema))
  def nestedImpl[T](func: Conn => T): T = func(Conn_Js.inMem(NestedSchema))
  def selfJoinImpl[T](func: Conn => T): T = func(Conn_Js.inMem(SelfJoinSchema))
  def aggregateImpl[T](func: Conn => T): T = func(Conn_Js.inMem(AggregatesSchema))
  def socialNewsImpl[T](func: Conn => T): T = func(Conn_Js.inMem(SocialNewsSchema))
  def graphImpl[T](func: Conn => T): T = func(Conn_Js.inMem(GraphSchema))
  def graph2Impl[T](func: Conn => T): T = func(Conn_Js.inMem(Graph2Schema))
  def modernGraph1Impl[T](func: Conn => T): T = func(Conn_Js.inMem(ModernGraph1Schema))
  def modernGraph2Impl[T](func: Conn => T): T = func(Conn_Js.inMem(ModernGraph2Schema))
  def productsImpl[T](func: Conn => T): T = func(Conn_Js.inMem(ProductsOrderSchema))
  def seattleImpl[T](func: Conn => T): T = func(Conn_Js.inMem(SeattleSchema))
  def mbrainzImpl[T](func: Conn => T): T = func(Conn_Js.inMem(MBrainzSchema))
}
