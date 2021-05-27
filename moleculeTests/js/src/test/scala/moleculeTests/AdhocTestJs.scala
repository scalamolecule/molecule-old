package moleculeTests

import boopickle.Default._
import molecule.datomic.api.in1_out13._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._

object AdhocTestJs extends AsyncTestSuite {

  println("")

  lazy val tests = Tests {

    "hej" - core { implicit conn =>

//      m(Ns.int)
//      m(Ns.int(count))

    }

  }
}
