package moleculeTests

import molecule.datomic.api.in3_out11.m
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




        _ <- Ref2.int2 + Ns.int insert Seq(
          // Two rows of data
          (1, 11),
          (2, 22)
        )

        _ <- m(Ref2.int2 + Ns.int).getJson.map(_ ==>
          """{
            |  "data": {
            |    "composite": [
            |      {
            |        "Ref2": {
            |          "int2": 1
            |        },
            |        "Ns": {
            |          "int": 11
            |        }
            |      },
            |      {
            |        "Ref2": {
            |          "int2": 2
            |        },
            |        "Ns": {
            |          "int": 22
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)


      } yield ()
    }


  }
}
