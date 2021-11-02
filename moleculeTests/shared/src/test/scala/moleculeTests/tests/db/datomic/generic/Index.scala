package moleculeTests.tests.db.datomic.generic

import molecule.datomic.api.out5._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer}
import utest._
import scala.concurrent.Future


object Index extends Base {


  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "All Datoms of Index" - core { implicit conn =>
      for {
        _ <- testData

        _ <- system match {
          case SystemPeer =>
            for {
              _ <- EAVT.a.get.map(_.size ==> 684)
              _ <- AEVT.a.get.map(_.size ==> 684)
              _ <- VAET.a.get.map(_.size ==> 334)
              r <- AVET.a.get.map(_.size ==> 215)
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