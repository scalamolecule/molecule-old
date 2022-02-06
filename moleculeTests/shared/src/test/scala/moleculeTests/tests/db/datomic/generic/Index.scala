package moleculeTests.tests.db.datomic.generic

import molecule.core.util.Executor._
import molecule.datomic.api.out5._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer}
import utest._
import scala.concurrent.Future


object Index extends Base {

  lazy val tests = Tests {

    "All Datoms of Index" - core { implicit conn =>
      for {
        _ <- testData

        _ <- system match {
          case SystemPeer =>
            for {
              _ <- EAVT.a.get.map(_.size ==> 648 + (if (useFree) -43 else 0))
              _ <- AEVT.a.get.map(_.size ==> 648 + (if (useFree) -43 else 0))
              _ <- VAET.a.get.map(_.size ==> 316 + (if (useFree) -26 else 0))
              _ <- AVET.a.get.map(_.size ==> 203 + (if (useFree) -15 else 0))
            } yield ()

          case SystemDevLocal =>
            for {
              _ <- EAVT.a.get.map(_.size ==> 531)
              _ <- AEVT.a.get.map(_.size ==> 531)
              _ <- VAET.a.get.map(_.size ==> 293)
              _ <- AVET.a.get.map(_.size ==> 531)
            } yield ()

          case _ => Future.unit // Peer Server (growing across tests, so we can't test deterministically here)
        }
      } yield ()
    }

  }
}