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

        _ <- Ref2.int2.insert(1, 2, 3, 4, 5)

        // Page 1 (empty cursor)
        cursor <- Ref2.int2.a1.get(-2, "").map { case (page, cursor, more) =>
          page ==> List(4, 5)
          more ==> 3
          cursor
        }

        // Page 2
        cursor <- Ref2.int2.a1.get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(2, 3)
          more ==> 1
          cursor
        }

        // Page 3, last page
        cursor <- Ref2.int2.a1.get(-2, cursor).map { case (page, cursor, more) =>
          page ==> List(1)
          more ==> 0 // currently no further rows
          cursor
        }

        // Page 2, go backwards with negative limit
        cursor <- Ref2.int2.a1.get(2, cursor).map { case (page, cursor, more) =>
          page ==> List(2, 3)
          more ==> 2
          cursor
        }

        // Page 1
        _ <- Ref2.int2.a1.get(2, cursor).map { case (page, _, more) =>
          page ==> List(4, 5)
          more ==> 0
        }


        //        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
        //          (1, List(11, 12)),
        //          (2, List(21, 22)),
        //          (3, List(31, 32)),
        //          (4, List(41, 42)),
        //          (5, List(51, 52)),
        //        )
        //
        //        // Page 1
        //        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, "").map { case (page, cursor, more) =>
        //          page ==> List(
        //            (1, List(11, 12)),
        //            (2, List(21, 22)),
        //          )
        //          more ==> 3
        //          cursor
        //        }
        //
        //        // Page 2
        //        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
        //          page ==> List(
        //            (3, List(31, 32)),
        //            (4, List(41, 42)),
        //          )
        //          more ==> 1
        //          cursor
        //        }
        //
        //        // Page 3
        //        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, cursor).map { case (page, cursor, more) =>
        //          page ==> List(
        //            (5, List(51, 52)),
        //          )
        //          more ==> 0
        //          cursor
        //        }
        //
        //        // Page 2
        //        cursor <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, cursor, more) =>
        //          page ==> List(
        //            (3, List(31, 32)),
        //            (4, List(41, 42)),
        //          )
        //          more ==> 2
        //          cursor
        //        }
        //
        //        // Page 1
        //        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, cursor).map { case (page, _, more) =>
        //          page ==> List(
        //            (1, List(11, 12)),
        //            (2, List(21, 22)),
        //          )
        //          more ==> 0
        //        }

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
