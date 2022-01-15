package moleculeTests.tests.db.datomic.generic

import molecule.datomic.api.out5._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer}
import utest._
import scala.concurrent.Future


object Index extends Base {

  val freeAdj = if (useFree) -43 else 0

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "All Datoms of Index" - core { implicit conn =>
      for {
        _ <- testData

        _ <- system match {
          case SystemPeer =>
            for {
              _ <- EAVT.a.get.map(_.size ==> 684 + (if (useFree) -43 else 0))
              _ <- AEVT.a.get.map(_.size ==> 684 + (if (useFree) -43 else 0))
              _ <- VAET.a.get.map(_.size ==> 334 + (if (useFree) -26 else 0))
              r <- AVET.a.get.map(_.size ==> 215 + (if (useFree) -15 else 0))
              //              _ <- EAVT.a.get.map(_.size ==> 679)
              //              _ <- AEVT.a.get.map(_.size ==> 679)
              //              _ <- VAET.a.get.map(_.size ==> 331)
              //              r <- AVET.a.get.map(_.size ==> 214)
            } yield r

          case SystemDevLocal =>
            for {
              _ <- EAVT.a.get.map(_.size ==> 561)
              _ <- AEVT.a.get.map(_.size ==> 561)
              _ <- VAET.a.get.map(_.size ==> 311)
              r <- AVET.a.get.map(_.size ==> 561)
            } yield r

          case _ => Future.unit // Peer Server (growing across tests, so we can't test deterministically here)
        }
      } yield ()
    }

  }
}