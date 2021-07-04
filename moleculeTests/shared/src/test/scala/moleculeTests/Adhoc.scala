package moleculeTests

import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import moleculeTests.tests.core.json.JsonNestedTypes.{date1, enum1, str1, uri1, uuid1}
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

        _ <- Ns.str.Refs1.*(Ref1.int1).insert(str1, Seq(1))

//        _ <- Ns.str.Refs1.*(Ref1.int1).get.map(_ ==> 7)
        _ <- Ns.str.Refs1.*(Ref1.int1).getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": "a", "Ns.refs1": [
            |   {"Ref1.int1": 1}]}
            |]""".stripMargin)


      } yield ()
    }


  }
}
