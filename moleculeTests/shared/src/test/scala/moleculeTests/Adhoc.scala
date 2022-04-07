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

        //        _ <- Ns.int(1).save
        //        _ <- Ns.int.get.map(_ ==> List(1))

        r1 <- Ns.int.insert(1, 3, 5, 7)

        // Use empty string as cursor for initial fetch of page 1
        cursor1 = ""

        cursor2 <- Ns.int.a1.get(2, cursor1).map { case (_, nextCursor2, page1) =>
          page1 ==> List(1, 3)
          nextCursor2
        }

        // Data added before next page is fetched
        r2 <- Ns.int(2).save

        _ <- Ns.int.getSince(r1.t).map(_ ==> List(2))

        // Continue from where we were regardless of added 2
        _ <- Ns.int.a1.get(2, cursor2).map { case (_, nextCursor3, page2) =>
          page2 ==> List(5, 7)
          nextCursor3
        }

        // Offset pagination would have shown 3 again since it sorts anew and would skip 1 and 2
        _ <- Ns.int.a1.get(2, 2).map(_._1 ==> List(3, 5))

        // If data is added after the cursor point, it is included in the following fetch
        _ <- Ns.int(6).save
        _ <- Ns.int.a1.get(2, cursor2).map { case (_, nextCursor3, page2) =>
          page2 ==> List(5, 6)
          nextCursor3
        }

      } yield ()
    }

    //    "Rename" - empty { implicit futConn =>
    //      for {
    //        conn <- futConn
    //        // Initial data model
    //        _ <- transact(schema {
    //          trait Foo {
    //            val int = oneInt
    //          }
    //        })
    //
    //        // Add int data
    //        _ <- conn.transact("""[[:db/add "-1" :Foo/int 1]]""")
    //        _ <- conn.query("[:find ?b :where [?a :Foo/int ?b]]").map(_ ==> List(List(1)))
    //      } yield ()
    //    }
  }
}
