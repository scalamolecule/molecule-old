package moleculeTests.tests.core.api

import java.util.{Collection => jCollection, List => jList}
import molecule.datomic.api.out1._
import moleculeTests.tests.core.base.dsl.CoreTest._
import molecule.core.util.JavaUtil
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Get extends AsyncTestSuite with JavaUtil {

  lazy val tests = Tests {

    "Sync" - core { implicit conn =>
      for {
        _ <- Ns.int insert List(1, 2, 3)

        _ <- Ns.int.get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.getArray.map(_ ==> Array(1, 2, 3))
        _ <- Ns.int.getIterable.map(_.iterator.toList ==> Iterator(1, 2, 3).toList)

        // Raw output has different implementations but same interface
        _ <- if (system == SystemPeer) {
          for {
            // Interface
            _ <- Ns.int.getRaw.map(_.isInstanceOf[jCollection[jList[AnyRef]]] ==> true)
            // Implementation: HashSet of PersistentVector of Object
            _ <- Ns.int.getRaw.map(_.getClass.getName ==> "java.util.HashSet")
            _ <- Ns.int.getRaw.map(_.iterator.next.getClass.getName ==> "clojure.lang.PersistentVector")
            res <- Ns.int.getRaw.map(_.toString ==> "[[1], [2], [3]]")
          } yield res
        } else {
          for {
            // Interface
            _ <- Ns.int.getRaw.map(_.isInstanceOf[jCollection[jList[AnyRef]]] ==> true)
            // Implementation: PersistentVector of PersistentVector of Object
            _ <- Ns.int.getRaw.map(_.getClass.getName ==> "clojure.lang.PersistentVector")
            _ <- Ns.int.getRaw.map(_.iterator.next.getClass.getName ==> "clojure.lang.PersistentVector")
            res <- Ns.int.getRaw.map(_.toString ==> "[[1] [2] [3]]")
          } yield res
        }
        _ <- Ns.int.getRaw.map(_.ints ==> List(1, 2, 3))

        _ <- Ns.int.get(2).map(_ ==> List(1, 2))
        _ <- Ns.int.getArray(2).map(_ ==> Array(1, 2))
        _ <- Ns.int.getRaw(2).map(_.ints ==> List(1, 2))
      } yield ()
    }
  }
}