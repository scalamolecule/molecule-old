//package moleculeTests.setup
//
//import molecule.datomic.base.ast.transactionModel._
//import molecule.core.data.SchemaTransaction
//import molecule.core.dsl.attributes
//import molecule.core.util.testing
//import molecule.core.util.testing.TxCount._
//import molecule.core.util.testing.TxCount.schema._
//import molecule.datomic.api.out3._
//import molecule.datomic.client.facade.{Conn_Client, Datomic_PeerServer}
//import molecule.datomic.peer.facade.Datomic_Peer
//import moleculeBuildInfo.BuildInfo.datomicProtocol
//import scala.collection.mutable
////import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.{ExecutionContext, Future}
//
//// Hack to empty a database on a Peer Server since we can't recreate it without
//// restarting the Peer Server which is not an option running big test suites.
//
//object CleanPeerServer {
//
//  def getCleanPeerServerConn(
//    peerServer: Datomic_PeerServer,
//    dbIdentifier: String,
//    schema: SchemaTransaction,
//    basisT0: Long
//  )(implicit ec: ExecutionContext): Future[(Conn_Client, Long)] = {
//    var basisT = basisT0
//    for {
//      dataConn <- peerServer.connect(dbIdentifier)
//      txCountConn <- peerServer.connect("m_txCount")
//      t <- dataConn.db.t
//      log = dataConn.clientConn.txRange(Some(1000), Some(1002))
//
//    } yield {
//      // Uncomment and run, if m_txCount database hasn't been created yet
//      //    Datomic_Peer.recreateDbFrom(
//      //      TxCountSchema,
//      //      datomicProtocol, // has to match transactor: 'free' or 'dev' (pro)
//      //      "localhost:4334/m_txCount"
//      //    )
//
//      if (log.isEmpty) {
//        println("Installing Peer Server schema...")
//        //      if (schema.partitions.size() > 0)
//        //        dataConn.transact(peerServer.allowedClientDefinitions(schema.partitions))
//        //      dataConn.transact(peerServer.allowedClientDefinitions(schema.namespaces))
//        schema.datomicClient.foreach { edn =>
//          dataConn.transact(edn).map { _ =>
//            basisT = t + 1
//            TxCountSchema.datomicClient.foreach { edn =>
//              txCountConn.transact(edn)
//              TxCount.db(dbIdentifier).basisT(basisT).save(Future(txCountConn), ec)
//            }
//          }
//        }
//
//      } else {
//
//        for{
//          res <- TxCount.db_(dbIdentifier).basisT.get(Future(txCountConn), ec)
//          basisT = res.head
//          cleanupRetractions = mutable.Set.empty[Statement]
//          res2 <- Log(Some(basisT)).e.a.v.get(Future(dataConn), ec)
//          _ = {
//            res2.foreach {
//              case (_, ":db/txInstant", _)            => // Can't change tx time
//              case (_, ":TxCount/basisT", _)          => // Keep basisT
//              case (tx, a, v) if tx < 17000000000000L => cleanupRetractions += Retract(tx, a, v)
//              case (e, _, _)                          => cleanupRetractions += RetractEntity(e)
//            }
//          }
//
//
//
//        } yield {
//          val retractionCount = cleanupRetractions.size
//          if (retractionCount > 1000)
//            throw new RuntimeException(s"Unexpectedly gathered $retractionCount retractions...")
//
//          if (retractionCount > 1000)
//            throw new RuntimeException(s"Unexpectedly gathered $retractionCount retractions...")
//
//          if (retractionCount > 0) {
//            val txd = dataConn.transact(cleanupRetractions.toSeq)
//            //            println(txd)
//
//            val tx = TxCount.e.db_(dbIdentifier).get(txCountConn).head
//            basisT = dataConn.db.t + 1
//            TxCount(tx).basisT(basisT).update(txCountConn)
//            //            println(s"basisT: $tx  $basisT  " + TxCount.db(dbIdentifier).basisT.get(txCountConn).head)
//          } else {
//            //            println("No rectractions necessary (could be a reload) ...")
//          }
//        }
//        }
//
//
//
////        basisT = TxCount.db_(dbIdentifier).basisT.get(txCountConn).head
//        //          println(s"basisT: $dbIdentifier  " + basisT)
//
//        // Since we can't recreate a Peer Server from here, we simply retract
//        // all datoms created in previous tests.
////        val cleanupRetractions = mutable.Set.empty[Statement]
////        Log(Some(basisT)).e.a.v.get(dataConn).foreach {
////          case (_, ":db/txInstant", _)            => // Can't change tx time
////          case (_, ":TxCount/basisT", _)          => // Keep basisT
////          case (tx, a, v) if tx < 17000000000000L => cleanupRetractions += Retract(tx, a, v)
////          case (e, _, _)                          => cleanupRetractions += RetractEntity(e)
////        }
////        val retractionCount = cleanupRetractions.size
//
//        //          Log(Some(basisT)).t.op.e.a.v.get(dataConn) foreach println
//        //          println("-------------------")
//        //          cleanupRetractions.toSeq.sortBy(_.e.toString.toLong) foreach println
//        //          println("-------------------")
//        //          println(s"Retraction count: $retractionCount")
//
////        if (retractionCount > 1000)
////          throw new RuntimeException(s"Unexpectedly gathered $retractionCount retractions...")
////
////        if (retractionCount > 0) {
////          val txd = dataConn.transact(cleanupRetractions.toSeq)
////          //            println(txd)
////
////          val tx = TxCount.e.db_(dbIdentifier).get(txCountConn).head
////          basisT = dataConn.db.t + 1
////          TxCount(tx).basisT(basisT).update(txCountConn)
////          //            println(s"basisT: $tx  $basisT  " + TxCount.db(dbIdentifier).basisT.get(txCountConn).head)
////        } else {
////          //            println("No rectractions necessary (could be a reload) ...")
////        }
//      }
//      (dataConn, basisT)
//    }
//  }
//}
