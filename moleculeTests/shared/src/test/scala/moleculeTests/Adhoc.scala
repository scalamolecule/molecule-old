package moleculeTests

import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.core.util.testing.TimerPrint
import molecule.core.util.{Helpers, JavaUtil}
import molecule.datomic.api.in1_out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.pagination.cursor.CursorPaginationTpl.x
import utest._
import scala.annotation.{nowarn, tailrec}
import scala.math.abs

object Adhoc extends AsyncTestSuite with Helpers with JavaUtil {
  val x = ""


  @nowarn lazy val tests = Tests {

    "Local logic" - selfJoin { implicit conn =>
      import moleculeTests.dataModels.core.ref.dsl.SelfJoin._

      for {
        _ <- Person.name("Ben").age(23).save

        // Dynamic objects needs explicit molecule created with `m`
        person <- m(Person.name.age).getDynObjs { person =>
          // Local business logic using the molecule object properties
          def nextAge: Int = person.age + 1
        }.map(_.head)

        _ = {
          person.name ==> "Ben"
          person.age ==> 23
          person.nextAge ==> 24
        }

      } yield ()
    }


    "core" - core { implicit futConn =>
      for {
        conn <- futConn



      } yield ()
    }



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
