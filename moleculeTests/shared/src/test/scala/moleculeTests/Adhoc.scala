package moleculeTests

import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.core.util.testing.TimerPrint
import molecule.core.util.{Helpers, JavaUtil}
import molecule.datomic.api.in1_out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.pagination.cursor.CursorPaginationTpl_forward.x
import utest._
import scala.annotation.{nowarn, tailrec}
import scala.math.abs

object Adhoc extends AsyncTestSuite with Helpers with JavaUtil {
  val x = ""


  @nowarn lazy val tests = Tests {

    "core" - core { implicit futConn =>
      for {
        conn <- futConn

        //        _ <- Ns.str.int insert List(
        //          ("a", 1),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 1),
        //          ("e", 1),
        //        )
        //
        //        // Order of non-sorted attributes is non-deterministic
        //        // (but will likely not vary in-between internal indexing jobs)
        //        _ <- Ns.str.int.get.map(_ ==> List(
        //          ("b", 1),
        //          ("a", 1),
        //          ("e", 1),
        //          ("d", 1),
        //          ("c", 1),
        //        ))
        //
        //        c <- Ns.str.int.a1.get(2, x).map { case (List(("b", 1), ("a", 1)), c, 3) => c }
        //        c <- Ns.str.int.a1.get(2, c).map { case (List(("e", 1), ("d", 1)), c, 1) => c }
        //
        //        _ <- Ns.str("a1").int(1).save
        //        _ <- Ns.str.int.get.map(_ ==> List(
        //          ("b", 1),
        //          ("a", 1),
        //          ("a1", 1),
        //          ("e", 1),
        //          ("d", 1),
        //          ("c", 1),
        //        ))
        //
        //        _ <- Ns.str.int.a1.get(2, c).map { case (List(("c", 1)), _, 0) => () }
        //
        //        _ <- Ns.str.int insert List(
        //          ("a", 1),
        //          ("ba", 1),
        //          ("c", 1),
        //        )
        //
        //        // Order of non-sorted attributes is non-deterministic
        //        // (but will likely not vary in-between internal indexing jobs)
        //        _ <- Ns.str.int.get.map(_ ==> List(
        //          ("a", 1),
        //          ("ba", 1),
        //          ("c", 1),
        //        ))
        //
        //        c <- Ns.str.int.a1.get(2, x).map { case (List(("a", 1), ("ba", 1)), c, 1) => c }
        //
        //        _ <- Ns.str("bb").int(1).save
        //        _ <- Ns.str.int.get.map(_ ==> List(
        //          ("a", 1),
        //          ("ba", 1), // cursor row
        //          ("bb", 1),
        //          ("c", 1),
        //        ))
        //
        //        // Page 2 including new bb row since it was after cursor row
        //        _ <- Ns.str.int.a1.get(2, c).map { case (List(("bb", 1), ("c", 1)), _, 0) => () }


        //        d <- Ns.str.int insert List(
        //          ("a", 1),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 1),
        //          ("e", 2),
        //          ("f", 2),
        //        ) map (_.eids(3))
        //
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("b", 1),
        //          ("a", 1),
        //          ("d", 1),
        //          ("c", 1),
        //
        //          ("f", 2),
        //          ("e", 2),
        //        ))
        //
        //        c <- Ns.str.int.a1.get(3, x).map { case (List(("b", 1), ("a", 1), ("d", 1)), c, 3) => c }
        //
        //        _ <- d.retract
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("b", 1),
        //          ("a", 1), // new last row of previous page
        //          ("c", 1),
        //
        //          ("f", 2),
        //          ("e", 2),
        //        ))
        //
        //        // Page 2 excluding new ba row since it was before cursor row
        //        _ <- Ns.str.int.a1.get(3, c).map {
        //          case (List(("c", 1), ("f", 2), ("e", 2)), _, 0) => ()
        //        }


        //        d <- Ns.str.int insert List(
        //          ("a", 1),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 1),
        //          ("e", 2),
        //        ) map (_.eids(3))
        //
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("b", 1),
        //          ("a", 1),
        //          ("d", 1),
        //          ("c", 1),
        //
        //          ("e", 2),
        //        ))
        //
        //        c <- Ns.str.int.a1.get(3, x).map { case (List(("b", 1), ("a", 1), ("d", 1)), c, 2) => c }
        //
        //        _ <- d.retract
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("b", 1),
        //          ("a", 1), // new last row of previous page
        //          ("c", 1),
        //
        //          ("e", 2),
        //        ))
        //
        //        // Page 2 excluding new ba row since it was before cursor row
        //        _ <- Ns.str.int.a1.get(3, c).map {
        //          case (List(("c", 1), ("e", 2)), _, 0) => ()
        //        }


        //        d <- Ns.str.int insert List(
        //          ("a", 1),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 1),
        //        ) map (_.eids(3))
        //
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("b", 1),
        //          ("a", 1),
        //          ("d", 1),
        //          ("c", 1),
        //        ))
        //
        //        c <- Ns.str.int.a1.get(3, x).map { case (List(("b", 1), ("a", 1), ("d", 1)), c, 1) => c }
        //
        //        _ <- d.retract
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("b", 1),
        //          ("a", 1), // new last row of previous page
        //          ("c", 1),
        //        ))
        //
        //        // Page 2 excluding new ba row since it was before cursor row
        //        _ <- Ns.str.int.a1.get(3, c).map {
        //          case (List(("c", 1)), _, 0) => ()
        //        }


        //        // Retract row after cursor
        //        c1 <- Ns.str.int insert List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 2),
        //        ) map (_.eids(2))
        //
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 2),
        //        ))
        //
        //        c <- Ns.str.int.a1.get(2, x).map { case (List(("a", 0), ("b", 1)), c, 2) => c }
        //
        //        _ <- c1.retract
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("d", 2),
        //        ))
        //
        //        // Page 2 excluding new ba row since it was before cursor row
        //        _ <- Ns.str.int.a1.get(2, c).map {
        //          case (List(("d", 2)), _, 0) => ()
        //        }


        //        b <- Ns.str.int insert List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 1),
        //          ("e", 2),
        //        ) map (_.eids(1))
        //
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("d", 1),
        //          ("c", 1),
        //          ("e", 2),
        //        ))
        //
        //        c <- Ns.str.int.a1.get(2, x).map { case (List(("a", 0), ("b", 1)), c, 3) => c }
        //
        //        _ <- b.retract
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("a", 0),
        //          ("d", 1),
        //          ("c", 1),
        //          ("e", 2),
        //        ))
        //
        //        // Page 2 excluding new ba row since it was before cursor row
        //        _ <- Ns.str.int.a1.get(2, c).map {
        //          case (List(("d", 1), ("c", 1)), _, 1) => ()
        //        }
        //


        //        b <- Ns.str.int insert List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 2),
        //        ) map (_.eids(1))
        //
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 2),
        //        ))
        //
        //        c <- Ns.str.int.a1.get(2, x).map { case (List(("a", 0), ("b", 1)), c, 2) => c }
        //
        //        _ <- b.retract
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("a", 0),
        //          ("c", 1),
        //          ("d", 2),
        //        ))
        //
        //        // Page 2 excluding new ba row since it was before cursor row
        //        _ <- Ns.str.int.a1.get(2, c).map {
        //          case (List(("c", 1), ("d", 2)), _, 0) => ()
        //        }


        //        c <- Ns.str.int insert List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 2),
        //        ) map (_.eids(1))
        //
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 2),
        //        ))
        //
        //        c1 <- Ns.str.int.a1.get(2, x).map { case (List(("a", 0), ("b", 1)), c, 2) => c }
        //
        //        _ <- c.retract
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("a", 0),
        //          ("c", 1),
        //          ("d", 2),
        //        ))
        //
        //        // Page 2 excluding new ba row since it was before cursor row
        //        _ <- Ns.str.int.a1.get(2, c1).map {
        //          case (List(("d", 2)), _, 0) => ()
        //        }


        //        c1 <- Ns.str.int insert List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 2),
        //        ) map (_.eids(2))
        //
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("c", 1),
        //          ("d", 2),
        //        ))
        //
        //        c <- Ns.str.int.a1.get(3, x).map { case (List(("a", 0), ("b", 1), ("c", 1)), c, 1) => c }
        //
        //        _ <- c1.retract
        //        _ <- Ns.str.int.a1.get.map(_ ==> List(
        //          ("a", 0),
        //          ("b", 1),
        //          ("d", 2),
        //        ))
        //
        //        // Page 2 excluding new ba row since it was before cursor row
        //        _ <- Ns.str.int.a1.get(3, c).map {
        //          case (List(("d", 2)), _, 0) => ()
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
