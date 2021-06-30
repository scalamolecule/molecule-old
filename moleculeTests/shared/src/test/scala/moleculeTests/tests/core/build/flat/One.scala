package moleculeTests.tests.core.build.flat

import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object One extends AsyncTestSuite {

  lazy val tests = Tests {

    "String" - core { implicit conn =>
      for {
        _ <- Ns.str insert "a"

        _ <- Ns.str.get.map(_.head ==> "a")

        // Get all objects
        _ <- Ns.str.getObjs.map(_.head.str ==> "a")
        // Convenience method to get first object
        _ <- Ns.str.getObj.map(_.str ==> "a")

        _ <- Ns.str.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a"
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Int" - core { implicit conn =>
      for {
        _ <- Ns.int insert 1

        _ <- Ns.int.get.map(_.head ==> 1)

        _ <- Ns.int.getObj.map(_.int ==> 1)

        _ <- Ns.int.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Long" - core { implicit conn =>
      for {
        _ <- Ns.long insert 1L

        _ <- Ns.long.get.map(_.head ==> 1L)

        _ <- Ns.long.getObj.map(_.long ==> 1L)

        _ <- Ns.long.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 1
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Double" - core { implicit conn =>
      for {
        _ <- Ns.double insert 1.1

        _ <- Ns.double.get.map(_.head ==> 1.1)

        _ <- Ns.double.getObj.map(_.double ==> 1.1)

        _ <- Ns.double.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "double": 1.1
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Boolean" - core { implicit conn =>
      for {
        _ <- Ns.bool insert true

        _ <- Ns.bool.get.map(_.head ==> true)

        _ <- Ns.bool.getObj.map(_.bool ==> true)

        _ <- Ns.bool.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "bool": true
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Date" - core { implicit conn =>
      for {
        _ <- Ns.date insert date1

        _ <- Ns.date.get.map(_.head ==> date1)

        _ <- Ns.date.getObj.map(_.date ==> date1)

        _ <- Ns.date.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "date": "2001-07-01"
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "UUID" - core { implicit conn =>
      for {
        _ <- Ns.uuid insert uuid1

        _ <- Ns.uuid.get.map(_.head ==> uuid1)

        _ <- Ns.uuid.getObj.map(_.uuid ==> uuid1)

        _ <- Ns.uuid.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "uuid": "$uuid1"
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "URI" - core { implicit conn =>
      for {
        _ <- Ns.uri insert uri1

        _ <- Ns.uri.get.map(_.head ==> uri1)

        _ <- Ns.uri.getObj.map(_.uri ==> uri1)

        _ <- Ns.uri.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "uri": "$uri1"
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "BigInt" - core { implicit conn =>
      for {
        _ <- Ns.bigInt insert bigInt1

        _ <- Ns.bigInt.get.map(_.head ==> bigInt1)

        _ <- Ns.bigInt.getObj.map(_.bigInt ==> bigInt1)

        _ <- Ns.bigInt.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "bigInt": $bigInt1
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "BigDecimal" - core { implicit conn =>
      for {
        _ <- Ns.bigDec insert bigDec1

        _ <- Ns.bigDec.get.map(_.head ==> bigDec1)

        _ <- Ns.bigDec.getObj.map(_.bigDec ==> bigDec1)

        _ <- Ns.bigDec.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "bigDec": $bigDec1
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "Enum" - core { implicit conn =>
      for {
        _ <- Ns.enum insert enum1

        _ <- Ns.enum.get.map(_.head ==> enum1)

        _ <- Ns.enum.getObj.map(_.enum ==> enum1)

        _ <- Ns.enum.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "enum": "$enum1"
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "Ref attr" - core { implicit conn =>
      for {
        refId <- Ref1.int1(1).save.map(_.eid)
        _ <- Ns.ref1(refId).save

        _ <- Ns.ref1.get.map(_.head ==> refId)

        _ <- Ns.ref1.getObj.map(_.ref1 ==> refId)

        _ <- Ns.ref1.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "ref1": $refId
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }
  }
}
