package moleculeTests

import molecule.datomic.api.in3_out11._
import molecule.core.macros.rowAttr.{CastOptNested, CastTypes, JsonBase}
import molecule.core.marshalling.nodes.{Node, Obj, Prop}
import molecule.core.marshalling.unpackAttr.String2cast
import molecule.core.marshalling.unpackers.Packed2EntityMap
import molecule.core.util.Helpers
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object AdhocJs extends AsyncTestSuite with Helpers
  with String2cast with CastTypes with CastOptNested with JsonBase {


  lazy val tests = Tests {


    "adhoc js" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn







      } yield ()
    }
  }
}
