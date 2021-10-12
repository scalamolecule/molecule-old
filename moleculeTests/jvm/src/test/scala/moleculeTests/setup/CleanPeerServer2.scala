//package moleculeTests.setup
//
//import molecule.core.data.SchemaTransaction
//import molecule.core.util.testing.TxCount._
//import molecule.core.util.testing.TxCount.schema._
//import molecule.datomic.api.out3._
//import molecule.datomic.base.ast.transactionModel._
//import molecule.datomic.client.facade.{Conn_Client, Datomic_PeerServer}
//import scala.collection.mutable
//import scala.concurrent.duration.DurationInt
//import scala.concurrent.{Await, ExecutionContext, Future}
//
//// Hack to empty a database on a Peer Server since we can't recreate it without
//// restarting the Peer Server which is not an option running big test suites.
//
//object CleanPeerServer2 {
//
//  def await[T](fut: Future[T]) = Await.result(fut, 1.minute)
//
//  def getCleanPeerServerConn(
//    peerServer: Datomic_PeerServer,
//    dbIdentifier: String,
//    schema: SchemaTransaction,
//    basisT0: Long
//  )(implicit ec: ExecutionContext): (Conn_Client, Long) = {
//    var basisT         = basisT0
//    val futDataConn    = peerServer.connect(dbIdentifier)
//    val futTxCountConn = peerServer.connect("m_txCount")
//
//
//    val dataConn = await(futDataConn)
//    val log      = dataConn.clientConn.txRange(Some(1000), Some(1002))
//    //    if(log.isEmpty){
//    if (true) {
//      println("Installing fresh Peer Server schema...")
//      await(dataConn.transact(schema.datomicClient.head))
//      if (schema.datomicClient.size > 1)
//        await(dataConn.transact(schema.datomicClient(1)))
//      if (schema.datomicClient.size > 2)
//        await(dataConn.transact(schema.datomicClient(2)))
//
//      println(13)
//      basisT = 1001 // first custom t
//      val txCountConn = await(futTxCountConn)
//      await(txCountConn.transact(TxCountSchema.datomicClient.head)) // no partitions/aliases
//      println(14)
//      //          _ <- TxCount.db(dbIdentifier).basisT(basisT).save(futTxCountConn, ec)
//      await(TxCount.db(dbIdentifier).basisT(basisT).save(futTxCountConn, ec))
//      println(15)
//      val res = await(TxCount.db.basisT.get(futTxCountConn, ec))
//      println("txCount: " + res)
//      (dataConn, basisT)
//    } else {
//      null
//    }
//
//
//    //    for {
//    //      dataConn <- futDataConn
//    //      t <- dataConn.db.t
//    //      // Using synchronous api since we are within a Future here
//    //      log = dataConn.clientConn.txRange(Some(1000), Some(1002))
//    ////      log0 <- dataConn.clientConnAsync.flatMap {
//    ////        case Right(conn)       => conn.txRange(Some(1000), Some(1001)).flatMap {
//    ////          case Right(txMap)       =>
//    ////            println("13 " + t)
//    ////            txMap.foreach { case (t, datoms) => println(s"  t: $t  ${datoms.size}") }
//    ////            Future(txMap)
//    ////          case Left(txMapAnomaly) => Future.failed(txMapAnomaly)
//    ////        }
//    ////        case Left(connAnomaly) => Future.failed(connAnomaly)
//    ////      }
//    ////      _ = if (log.isEmpty) {
//    //      _ = if (true) {
//    //        println("Installing fresh Peer Server schema...")
//    //        for {
//    ////          _ <- Future.sequence(
//    ////            schema.datomicClient.map { edn =>
//    ////              dataConn.transact(edn)
//    ////            }
//    ////          )
//    ////          _ <- dataConn.transact(schema.datomicClient.head).recover(err => println("data err: " + err))
//    //          aa <- dataConn.transact(schema.datomicClient.head)
//    //          _ <- if(schema.datomicClient.size > 1) dataConn.transact(schema.datomicClient(1)) else Future.unit
//    //          _ <- if(schema.datomicClient.size > 2) dataConn.transact(schema.datomicClient(2)) else Future.unit
//    //          _ = println(13)
//    //          _ = basisT = 1001 // first custom t
//    //          txCountConn <- futTxCountConn
//    //          _ <- txCountConn.transact(TxCountSchema.datomicClient.head) // no partitions/aliases
//    //          _ = println(14)
//    ////          _ <- TxCount.db(dbIdentifier).basisT(basisT).save(futTxCountConn, ec)
//    //          _ <- TxCount.db(dbIdentifier).basisT(basisT).save(futTxCountConn, ec)
//    //          _ = println(15)
//    //          res <- TxCount.db.basisT.get(futTxCountConn, ec)
//    //          _ = println("txCount: " + res)
//    //        } yield ()
//    //
//    //      } else {
//    //        println(s"$dbIdentifier  $t  ${log.size}")
//    //
//    //        // Retract non-schema datoms in Peer Server database
//    //        for {
//    //          res <- TxCount.db.basisT.get(futTxCountConn, ec)
//    //          _ = println("txCount: " + res)
//    //          curBasisT <- TxCount.db_(dbIdentifier).basisT.get(futTxCountConn, ec)
//    //            .map(_.head)
//    //            .recover { err => println("err: " + err); 42L }
//    //          _ = basisT = curBasisT
//    //          _ = println("new basisT " + basisT)
//    //          aa <- TxCount.db.basisT.get(futTxCountConn, ec)
//    //          _ = println("aa " + aa)
//    //
//    //
//    //
//    //          cleanupRetractions = mutable.Set.empty[Statement]
//    //          _ <- Log(Some(basisT)).e.a.v.get(futDataConn, ec)
//    //            .recover { err => println("Log err: " + err) }
//    //          datoms <- Log(Some(basisT)).e.a.v.get(futDataConn, ec)
//    //          _ = {
//    //            println("found " + datoms.size + " datoms")
//    //            datoms.foreach {
//    //              case (_, ":db/txInstant", _)            => // Can't change tx time
//    //              case (_, ":TxCount/basisT", _)          => // Keep basisT
//    //              case (tx, a, v) if tx < 17000000000000L => cleanupRetractions += Retract(tx, a, v)
//    //              case (e, _, _)                          => cleanupRetractions += RetractEntity(e)
//    //            }
//    //          }
//    //
//    //        } yield {
//    //          val retractionCount = cleanupRetractions.size
//    //          if (retractionCount > 1000)
//    //            throw new RuntimeException(s"Unexpectedly gathered $retractionCount retractions...")
//    //
//    //          if (retractionCount > 0) {
//    //            for {
//    //              _ <- dataConn.transact(Future(cleanupRetractions.toSeq))
//    //              tx <- TxCount.e.db_(dbIdentifier).get(futTxCountConn, ec).map(_.head)
//    //              t2 <- dataConn.db.t
//    //              _ = basisT = t2 + 1
//    //              _ <- TxCount(tx).basisT(basisT).update(futTxCountConn, ec)
//    //            } yield ()
//    //          }
//    //        }
//    //      }
//    //    } yield {
//    //      (dataConn, basisT)
//    //    }
//  }
//}
