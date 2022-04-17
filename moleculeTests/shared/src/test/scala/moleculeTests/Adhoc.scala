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


        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, Nil),
        )

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(-2).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : -2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "Refs1": [
            |          {
            |            "int1": 21
            |          },
            |          {
            |            "int1": 22
            |          }
            |        ]
            |      },
            |      {
            |        "int": 3,
            |        "Refs1": [
            |          {
            |            "int1": 31
            |          },
            |          {
            |            "int1": 32
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

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
