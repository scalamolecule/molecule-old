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

        //        _ <- Ns.str insert "a"
        _ <- Ns.int insert 1
        //        _ <- Ns.long insert 1L
        //        _ <- Ns.double insert 1.1
        //        _ <- Ns.bool insert true
        //        _ <- Ns.date insert date1
        //        _ <- Ns.uuid insert uuid1
        //        _ <- Ns.uri insert uri1
        //        _ <- Ns.bigInt insert bigInt1
        //        _ <- Ns.bigDec insert bigDec1
        //        _ <- Ns.enumm insert "enum1"
        //        _ <- Ns.ref1 insert 42L

        //        _ <- conn.query("[:find ?v :where [_ :Ns/str ?v]]").map(_ ==> List(List("a")))
        _ <- conn.query("[:find ?v :where [_ :Ns/int ?v]]").map(_ ==> List(List(1)))
        //        _ <- conn.query("[:find ?v :where [_ :Ns/long ?v]]").map(_ ==> List(List(1L)))
        //        _ <- conn.query("[:find ?v :where [_ :Ns/double ?v]]").map(_ ==> List(List(1.1)))
        //        _ <- conn.query("[:find ?v :where [_ :Ns/bool ?v]]").map(_ ==> List(List(true)))
        //        _ <- conn.query("[:find ?v :where [_ :Ns/date ?v]]").map(_ ==> List(List(date1)))
        //        _ <- conn.query("[:find ?v :where [_ :Ns/uuid ?v]]").map(_ ==> List(List(uuid1)))
        //        _ <- conn.query("[:find ?v :where [_ :Ns/uri ?v]]").map(_ ==> List(List(uri1)))
        //        _ <- conn.query("[:find ?v :where [_ :Ns/bigInt ?v]]").map(_ ==> List(List(bigInt1)))
        //        _ <- conn.query("[:find ?v :where [_ :Ns/bigDec ?v]]").map(_ ==> List(List(bigDec1)))
        //        _ <- conn.query("[:find ?v :where [_ :Ns/enumm ?v]]").map(_ ==> List(List("enum1")))
        //        _ <- conn.query("[:find ?v :where [_ :Ns/ref1 ?v]]").map(_ ==> List(List(42L)))

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
