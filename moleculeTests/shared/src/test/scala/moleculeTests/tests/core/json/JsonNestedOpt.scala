package moleculeTests.tests.core.json

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.out11._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object JsonNestedOpt extends AsyncTestSuite {

  lazy val tests = Tests {

    // todo: implement!

    "Optional nested not implemented" - core { implicit conn =>
//      m(Ns.int.str.Refs1 *? Ref1.int1).getJson.recover { case MoleculeException(err, _) =>
//        err ==> "Optional nested data as json not implemented"
//      }

      1 ==> 1
    }
  }
}