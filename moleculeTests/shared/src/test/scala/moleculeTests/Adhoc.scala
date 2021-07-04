package moleculeTests

import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Adhoc extends AsyncTestSuite {

  lazy val tests = Tests {

    //    "adhoc" - bidirectional { implicit conn =>
    "adhoc" - core { implicit conn =>
      for {
        //        _ <- Future(1 ==> 1)
        _ <- Future(2 ==> 2)

        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.ints2) insert List(("a", 1, List(Set(2, 3))))

        _ <- m(Ns.str.Ref1.int1.Refs2 * Ref2.ints2).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a",
            |        "Ref1": {
            |          "int1": 1,
            |          "Refs2": [
            |            {
            |              "ints2": [
            |                3,
            |                2
            |              ]
            |            }
            |          ]
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

      } yield ()
    }


  }
}
