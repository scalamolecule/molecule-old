package moleculeTests.tests.core.build.flat

import molecule.datomic.api.out11._
import moleculeTests.Adhoc.{core, uuid1, uuid2}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

object Many extends AsyncTestSuite {

  lazy val tests = Tests {

    "String0" - core { implicit conn =>
      for {
        // (no data)

        _ <- Ns.strs.get.map(_ ==> Nil)

        _ <- Ns.strs.getObjs.map(_ ==> Nil)

        _ <- Ns.strs.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)
      } yield ()
    }

    "String1" - core { implicit conn =>
      for {
        _ <- Ns.strs insert Set("a")

        _ <- Ns.strs.get.map(_.head ==> Set("a"))

        // Get all objects
        _ <- Ns.strs.getObjs.map(_.head.strs ==> Set("a"))
        // Convenience method to get first object
        _ <- Ns.strs.getObj.map(_.strs ==> Set("a"))

        _ <- Ns.strs.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "strs": [
            |          "a"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }

    "String2" - core { implicit conn =>
      for {
        _ <- Ns.strs insert Set("a", "b")

        _ <- Ns.strs.get.map(_.head ==> Set("a", "b"))

        // Get all objects
        _ <- Ns.strs.getObjs.map(_.head.strs ==> Set("a", "b"))
        // Convenience method to get first object
        _ <- Ns.strs.getObj.map(_.strs ==> Set("a", "b"))

        _ <- Ns.strs.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "strs": [
            |          "a",
            |          "b"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Int" - core { implicit conn =>
      for {
        _ <- Ns.ints insert Set(1, 2)

        _ <- Ns.ints.get.map(_.head ==> Set(1, 2))

        _ <- Ns.ints.getObj.map(_.ints ==> Set(1, 2))

        _ <- Ns.ints.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "ints": [
            |          1,
            |          2
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Long" - core { implicit conn =>
      for {
        _ <- Ns.longs insert Set(1L, 2L)

        _ <- Ns.longs.get.map(_.head ==> Set(1L, 2L))

        _ <- Ns.longs.getObj.map(_.longs ==> Set(1L, 2L))

        _ <- Ns.longs.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "longs": [
            |          1,
            |          2
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Double" - core { implicit conn =>
      for {
        _ <- Ns.doubles insert Set(1.1, 2.2)

        _ <- Ns.doubles.get.map(_.head ==> Set(1.1, 2.2))

        _ <- Ns.doubles.getObj.map(_.doubles ==> Set(1.1, 2.2))

        _ <- Ns.doubles.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "doubles": [
            |          1.1,
            |          2.2
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Boolean" - core { implicit conn =>
      for {
        _ <- Ns.bools insert Set(true, false)

        _ <- Ns.bools.get.map(_.head ==> Set(true, false))

        _ <- Ns.bools.getObj.map(_.bools ==> Set(true, false))

        _ <- Ns.bools.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "bools": [
            |          true,
            |          false
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Date" - core { implicit conn =>
      for {
        _ <- Ns.dates insert Set(date1, date2)

        _ <- Ns.dates.get.map(_.head ==> Set(date1, date2))

        _ <- Ns.dates.getObj.map(_.dates ==> Set(date1, date2))

        _ <- Ns.dates.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "dates": [
             |          "2001-07-01",
             |          "2002-01-01"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "UUID" - core { implicit conn =>
      for {
        _ <- Ns.uuids insert Set(uuid1, uuid2)

        _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid1, uuid2))

        _ <- Ns.uuids.getObj.map(_.uuids.toList.sortBy(_.toString) ==> List(uuid1, uuid2))

        // Order of set elements is not guaranteed
        _ <- Ns.uuids.getJson.map(json =>
          try {
            json ==>
              s"""{
                 |  "data": {
                 |    "Ns": [
                 |      {
                 |        "uuids": [
                 |          "$uuid1",
                 |          "$uuid2"
                 |        ]
                 |      }
                 |    ]
                 |  }
                 |}""".stripMargin
          } catch {
            case NonFatal(e) =>
              json ==>
                s"""{
                   |  "data": {
                   |    "Ns": [
                   |      {
                   |        "uuids": [
                   |          "$uuid2",
                   |          "$uuid1"
                   |        ]
                   |      }
                   |    ]
                   |  }
                   |}""".stripMargin
          }
        )
      } yield ()
    }


    "URI" - core { implicit conn =>
      for {
        _ <- Ns.uris insert Set(uri1, uri2)

        _ <- Ns.uris.get.map(_.head ==> Set(uri1, uri2))

        _ <- Ns.uris.getObj.map(_.uris ==> Set(uri1, uri2))

        _ <- Ns.uris.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "uris": [
             |          "$uri1",
             |          "$uri2"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "BigInt" - core { implicit conn =>
      for {
        _ <- Ns.bigInts insert Set(bigInt1, bigInt2)

        _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt1, bigInt2))

        _ <- Ns.bigInts.getObj.map(_.bigInts ==> Set(bigInt1, bigInt2))

        _ <- Ns.bigInts.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "bigInts": [
             |          $bigInt1,
             |          $bigInt2
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "BigDecimal" - core { implicit conn =>
      for {
        _ <- Ns.bigDecs insert Set(bigDec1, bigDec2)

        _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec1, bigDec2))

        _ <- Ns.bigDecs.getObj.map(_.bigDecs ==> Set(bigDec1, bigDec2))

        _ <- Ns.bigDecs.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "bigDecs": [
             |          $bigDec2,
             |          $bigDec1
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "Enum" - core { implicit conn =>
      for {
        _ <- Ns.enums insert Set(enum1, enum2)

        _ <- Ns.enums.get.map(_.head ==> Set(enum1, enum2))

        _ <- Ns.enums.getObj.map(_.enums ==> Set(enum1, enum2))

        _ <- Ns.enums.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "enums": [
             |          "$enum1",
             |          "$enum2"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "Ref attr" - core { implicit conn =>
      for {
        List(refId1, refId2) <- Ref1.int1.insert(1, 2).map(_.eids)
        _ <- Ns.refs1(refId1, refId2).save

        _ <- Ns.refs1.get.map(_.head ==> Set(refId1, refId2))

        _ <- Ns.refs1.getObj.map(_.refs1 ==> Set(refId1, refId2))

        _ <- Ns.refs1.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "refs1": [
             |          $refId2,
             |          $refId1
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }
  }
}
