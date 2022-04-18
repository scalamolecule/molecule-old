package moleculeTests

import molecule.core.macros.GetTransactSchema.schema
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



      } yield ()
    }
/*

 */

    //    "Rename" - empty { implicit futConn =>
    //      for {
    //        conn <- futConn
    //
    //        //            // Initial data model
    //        //            _ <- transact(schema {
    //        //              trait Foo {
    //        //                val int = oneInt
    //        //              }
    //        //            })
    //
    //        s = schema {
    //          trait Foo {
    //            val value    = oneInt.uniqueValue
    //            val identity = oneInt.uniqueIdentity
    //          }
    //        }
    //        _ = println(s.nsMap)
    //        _ = println(s)
    //
    //        // Add int data
    //        //            _ <- conn.transact("""[[:db/add "-1" :Foo/int 1]]""")
    //        //            _ <- conn.query("[:find ?b :where [?a :Foo/int ?b]]").map(_ ==> List(List(1)))
    //      } yield ()
    //    }
  }
}
