package moleculeTests

import molecule.core.util.Executor._
import molecule.core.util.{Helpers, JavaUtil}
import molecule.datomic.api.in1_out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._

object Adhoc extends AsyncTestSuite with Helpers with JavaUtil {


  lazy val tests = Tests {

    "core" - core { implicit futConn =>

      for {
        conn <- futConn




/*
testOnly moleculeTests.tests.db.datomic.generic.SchemaChange_AttrOptions
testOnly moleculeTests.tests.db.datomic.generic.SchemaChange_Namespace
testOnly moleculeTests.tests.db.datomic.generic.SchemaChange_Attr
testOnly moleculeTests.tests.db.datomic.generic.SchemaChange_Partition
 */


      } yield ()
    }
  }
}
