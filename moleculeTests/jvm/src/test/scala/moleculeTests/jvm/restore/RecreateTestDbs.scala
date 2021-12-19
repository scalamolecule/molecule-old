package moleculeTests.jvm.restore

import molecule.core.util.testing.TxCount.schema.TxCountSchema
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeBuildInfo.BuildInfo.datomicProtocol
import moleculeTests.dataModels.core.base.schema.CoreTestSchema
import moleculeTests.dataModels.core.bidirectionals.schema.BidirectionalSchema
import moleculeTests.dataModels.core.ref.schema.NestedSchema
import moleculeTests.dataModels.core.schemaDef.schema.PartitionTestSchema
import moleculeTests.dataModels.examples.datomic.dayOfDatomic.schema._
import moleculeTests.dataModels.examples.datomic.seattle.schema.SeattleSchema
import moleculeTests.dataModels.examples.gremlin.gettingStarted.schema.{ModernGraph1Schema, ModernGraph2Schema}
import moleculeTests.jvm.NonBlocking.{mbrainz, run}
import moleculeTests.setup.AsyncTestSuite
import molecule.core.util.Executor._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import utest._
import molecule.core.util.Executor._


  // If running as App
//object RecreateTestDbs extends App {
//  Await.result(Future.sequence(
//    List(
//      "m_txCount" -> TxCountSchema,
//      "m_coretests" -> CoreTestSchema,
//      "m_bidirectional" -> BidirectionalSchema,
//      "m_partitions" -> PartitionTestSchema,
//      "m_nested" -> NestedSchema,
//      "m_aggregates" -> AggregatesSchema,
//      "m_socialNews" -> SocialNewsSchema,
//      "m_graph" -> GraphSchema,
//      "m_graph2" -> Graph2Schema,
//      "m_modernGraph1" -> ModernGraph1Schema,
//      "m_modernGraph2" -> ModernGraph2Schema,
//      "m_productsOrder" -> ProductsOrderSchema,
//      "m_seattle" -> SeattleSchema,
//    ).map {
//      case (db, schema) =>
//        Datomic_Peer
//          .recreateDbFrom(schema, datomicProtocol, "localhost:4334/" + db)
//          .map(_ => println("Created database: " + db))
//    }), 1.minute)
// }


// Uncomment to run (to avoid running in run-all testing)
//object RecreateTestDbs extends AsyncTestSuite {
object RecreateTestDbs {

  lazy val tests = Tests {

    "Create Peer Server test databases" - {
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
      ).map {
        case (db, schema) =>
          Datomic_Peer
            .recreateDbFrom(schema, datomicProtocol, "localhost:4334/" + db)
            .map(_ => println("Created database: " + db))
      }
    }
  }
}