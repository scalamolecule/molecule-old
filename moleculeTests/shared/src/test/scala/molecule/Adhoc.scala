package molecule

import boopickle.Default._
import molecule.datomic.base.facade.Conn
import molecule.setup.AsyncTestSuite
import utest._
import molecule.datomic.api.out11._
import molecule.setup.AsyncTestSuite
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.ExecutionContext.Implicits.global

object Adhoc extends AsyncTestSuite {

  lazy val tests = Tests {

    "hej" - core { implicit conn =>
      for {
        // insertAsync single value for one cardinality-1 attribute
        _ <- Ref1.int1 insertAsync int1
        _ <- Ref1.int1.getAsync === List(int1)
      } yield ()
    }



  }
}
