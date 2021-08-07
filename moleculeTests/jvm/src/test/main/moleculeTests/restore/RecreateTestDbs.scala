package moleculeTests.restore

import molecule.core.util.testing.TxCount.schema.TxCountSchema
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeBuildInfo.BuildInfo.datomicProtocol
import moleculeTests.tests.core.base.schema.CoreTestSchema
import moleculeTests.tests.core.bidirectionals.schema.BidirectionalSchema
import moleculeTests.tests.core.ref.schema.NestedSchema
import moleculeTests.tests.core.schemaDef.schema.PartitionTestSchema
import moleculeTests.tests.examples.datomic.dayOfDatomic.schema._
import moleculeTests.tests.examples.datomic.seattle.schema.SeattleSchema
import moleculeTests.tests.examples.gremlin.gettingStarted.schema.{ModernGraph1Schema, ModernGraph2Schema}
import scala.concurrent.ExecutionContext.Implicits.global


object RecreateTestDbs extends App {

  List(
    "m_txCount" -> TxCountSchema,
    "m_coretests" -> CoreTestSchema,
    "m_bidirectional" -> BidirectionalSchema,
    "m_partitions" -> PartitionTestSchema,
    "m_nested" -> NestedSchema,
    "m_aggregates" -> AggregatesSchema,
    "m_socialNews" -> SocialNewsSchema,
    "m_graph" -> GraphSchema,
    "m_graph2" -> Graph2Schema,
    "m_modernGraph1" -> ModernGraph1Schema,
    "m_modernGraph2" -> ModernGraph2Schema,
    "m_productsOrder" -> ProductsOrderSchema,
    "m_seattle" -> SeattleSchema,
  ).foreach {
    case (db, schema) =>
      println("Recreating db " + db)
      Datomic_Peer.recreateDbFrom(schema, "localhost:4334/" + db, datomicProtocol)
  }
}