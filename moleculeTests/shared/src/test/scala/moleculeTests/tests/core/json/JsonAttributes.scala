package moleculeTests.tests.core.json

import molecule.datomic.api.out11._
import molecule.datomic.base.util.SystemPeerServer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object JsonAttributes extends AsyncTestSuite {

  lazy val tests = Tests {

    "Empty result set" - core { implicit conn =>
      Ns.str.getJson.map(_ ==>
        """{
          |  "data": {
          |    "Ns": []
          |  }
          |}""".stripMargin)
    }

    "Card one" - core { implicit conn =>
      for {
        // Insert single value for one cardinality-1 attribute
        _ <- Ns.str insert str1
        _ <- Ns.int insert 1
        _ <- Ns.long insert 1L
        _ <- Ns.double insert 1.1
        _ <- Ns.bool insert true
        _ <- Ns.date insert date1
        _ <- Ns.uuid insert uuid1
        _ <- Ns.uri insert uri1
        _ <- Ns.bigInt insert bigInt1
        _ <- Ns.bigDec insert bigDec1
        _ <- Ns.enum insert enum1
        refId <- Ref1.int1(1).save.map(_.eid)
        _ <- Ns.ref1(refId).save

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

        _ <- Ns.date.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "date": "2001-07-01"
            |      }
            |    ]
            |  }
            |}""".stripMargin)

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

        _ <- Ns.bigDec.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "bigDec": 1.0
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.enum.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "enum": "enum1"
            |      }
            |    ]
            |  }
            |}""".stripMargin)

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


    "Card many" - core { implicit conn =>
      for {
        _ <- Ns.strs insert Set("", "a", "b")
        _ <- Ns.ints insert Set(1, 2)
        _ <- Ns.longs insert Set(1L, 2L)
        _ <- Ns.doubles insert Set(1.1, 2.2)
        _ <- Ns.bools insert Set(true, false)
        _ <- Ns.dates insert Set(date1, date2)
        _ <- Ns.uuids insert Set(uuid1)
        _ <- Ns.uris insert Set(uri1, uri2)
        _ <- Ns.bigInts insert Set(bigInt1, bigInt2)
        _ <- Ns.bigDecs insert Set(bigDec1, bigDec2)
        _ <- Ns.enums insert Set(enum1, enum2)
        List(refId1, refId2) <- Ref1.int1.insert(1, 2).map(_.eids)
        _ <- Ns.refs1(refId1, refId2).save

        _ <- Ns.strs.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "strs": [
            |          "",
            |          "a",
            |          "b"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

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

        _ <- Ns.dates.getJson.map(_ ==>
          """{
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

        _ <- Ns.uuids.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "uuids": [
             |          "$uuid1"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- if (system == SystemPeerServer) {
          Ns.uris.getJson.map(_ ==>
            s"""{
               |  "data": {
               |    "Ns": [
               |      {
               |        "uris": [
               |          "$uri2",
               |          "$uri1"
               |        ]
               |      }
               |    ]
               |  }
               |}""".stripMargin)
        } else {
          Ns.uris.getJson.map(_ ==>
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
        }

        _ <- Ns.bigInts.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "bigInts": [
            |          1,
            |          2
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.bigDecs.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "bigDecs": [
            |          2.0,
            |          1.0
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.enums.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "enums": [
            |          "enum1",
            |          "enum2"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

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


    "Card map" - core { implicit conn =>
      for {
        _ <- Ns.strMap insert Map("a" -> "A", "b" -> "B")
        _ <- Ns.intMap insert Map("a" -> 1, "b" -> 2)
        _ <- Ns.longMap insert Map("a" -> 1L, "b" -> 2L)
        _ <- Ns.doubleMap insert Map("a" -> 1.1, "b" -> 2.2)
        _ <- Ns.boolMap insert Map("a" -> true, "b" -> false)
        _ <- Ns.dateMap insert Map("a" -> date1, "b" -> date2)
        _ <- Ns.uuidMap insert Map("a" -> uuid1)
        _ <- Ns.uriMap insert Map("a" -> uri1, "b" -> uri2)
        _ <- Ns.bigIntMap insert Map("a" -> bigInt1, "b" -> bigInt2)
        _ <- Ns.bigDecMap insert Map("a" -> bigDec1, "b" -> bigDec2)

        _ <- Ns.strMap.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "strMap": {
            |          "b": "B",
            |          "a": "A"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.intMap.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "intMap": {
            |          "b": 2,
            |          "a": 1
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.longMap.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "longMap": {
            |          "b": 2,
            |          "a": 1
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.doubleMap.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "doubleMap": {
            |          "b": 2.2,
            |          "a": 1.1
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.boolMap.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "boolMap": {
            |          "b": false,
            |          "a": true
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.dateMap.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "dateMap": {
            |          "a": "2001-07-01",
            |          "b": "2002-01-01"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.uuidMap.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "uuidMap": {
             |          "a": "$uuid1"
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.uriMap.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "uriMap": {
             |          "b": "$uri2",
             |          "a": "$uri1"
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.bigIntMap.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "bigIntMap": {
            |          "b": 2,
            |          "a": 1
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.bigDecMap.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "bigDecMap": {
            |          "b": 2.0,
            |          "a": 1.0
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Card one optional" - core { implicit conn =>
      for {
        refId <- Ref1.int1(1).save.map(_.eid)
        _ <- Ns.int.str$ insert List((1, None), (1, Some("")), (1, Some("c")))
        _ <- Ns.long.int$ insert List((2, None), (2, Some(2)))
        _ <- Ns.int.long$ insert List((3, None), (3, Some(20L)))
        _ <- Ns.int.double$ insert List((4, None), (4, Some(2.2)))
        _ <- Ns.int.bool$ insert List((5, None), (5, Some(true)))
        _ <- Ns.int.date$ insert List((6, None), (6, Some(date2)))
        _ <- Ns.int.uuid$ insert List((7, None), (7, Some(uuid2)))
        _ <- Ns.int.uri$ insert List((8, None), (8, Some(uri2)))
        _ <- Ns.int.bigInt$ insert List((9, None), (9, Some(bigInt2)))
        _ <- Ns.int.bigDec$ insert List((10, None), (10, Some(bigDec2)))
        _ <- Ns.int.enum$ insert List((11, None), (11, Some(enum1)))
        _ <- Ns.int.ref1$ insert List((12, None), (12, Some(refId)))

        _ <- Ns.int(1).str$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "str$": null
            |      },
            |      {
            |        "int": 1,
            |        "str$": ""
            |      },
            |      {
            |        "int": 1,
            |        "str$": "c"
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.long(2L).int$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "long": 2,
            |        "int$": null
            |      },
            |      {
            |        "long": 2,
            |        "int$": 2
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(3).long$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "long$": null
            |      },
            |      {
            |        "int": 3,
            |        "long$": 20
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(4).double$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "double$": null
            |      },
            |      {
            |        "int": 4,
            |        "double$": 2.2
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(5).bool$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 5,
            |        "bool$": null
            |      },
            |      {
            |        "int": 5,
            |        "bool$": true
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(6).date$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "date$": null
            |      },
            |      {
            |        "int": 6,
            |        "date$": "2002-01-01"
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(7).uuid$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 7,
             |        "uuid$$": null
             |      },
             |      {
             |        "int": 7,
             |        "uuid$$": "$uuid2"
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(8).uri$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 8,
             |        "uri$$": null
             |      },
             |      {
             |        "int": 8,
             |        "uri$$": "$uri2"
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(9).bigInt$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 9,
             |        "bigInt$$": null
             |      },
             |      {
             |        "int": 9,
             |        "bigInt$$": 2
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(10).bigDec$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 10,
             |        "bigDec$$": null
             |      },
             |      {
             |        "int": 10,
             |        "bigDec$$": 2.0
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(11).enum$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 11,
             |        "enum$$": null
             |      },
             |      {
             |        "int": 11,
             |        "enum$$": "$enum1"
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(12).ref1$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 12,
             |        "ref1$$": null
             |      },
             |      {
             |        "int": 12,
             |        "ref1$$": $refId
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "Card many optional" - core { implicit conn =>
      for {
        refId <- Ref1.int1(1).save.map(_.eid)
        _ <- Ns.int.strs$ insert List((1, None), (1, Some(Set("", "b"))))
        _ <- Ns.int.ints$ insert List((2, None), (2, Some(Set(1, 2))))
        _ <- Ns.int.longs$ insert List((3, None), (3, Some(Set(1L, 2L))))
        _ <- Ns.int.doubles$ insert List((4, None), (4, Some(Set(1.1, 2.2))))
        _ <- Ns.int.bools$ insert List((5, None), (5, Some(Set(true, false))))
        _ <- Ns.int.dates$ insert List((6, None), (6, Some(Set(date1, date2))))
        _ <- Ns.int.uuids$ insert List((7, None), (7, Some(Set(uuid1, uuid2))))
        _ <- Ns.int.uris$ insert List((8, None), (8, Some(Set(uri1, uri2))))
        _ <- Ns.int.bigInts$ insert List((9, None), (9, Some(Set(bigInt1, bigInt2))))
        _ <- Ns.int.bigDecs$ insert List((10, None), (10, Some(Set(bigDec1, bigDec2))))
        _ <- Ns.int.enums$ insert List((11, None), (11, Some(Set(enum1, enum2))))
        _ <- Ns.int.refs1$ insert List((12, None), (12, Some(Set(refId))))

        _ <- Ns.int(1).strs$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "strs$": null
            |      },
            |      {
            |        "int": 1,
            |        "strs$": [
            |          "",
            |          "b"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(2).ints$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "ints$": null
            |      },
            |      {
            |        "int": 2,
            |        "ints$": [
            |          1,
            |          2
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(3).longs$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "longs$": null
            |      },
            |      {
            |        "int": 3,
            |        "longs$": [
            |          1,
            |          2
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(4).doubles$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "doubles$": null
            |      },
            |      {
            |        "int": 4,
            |        "doubles$": [
            |          1.1,
            |          2.2
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        // OBS!: Sets of booleans truncate to one value!
        _ <- Ns.int(5).bools$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 5,
            |        "bools$": null
            |      },
            |      {
            |        "int": 5,
            |        "bools$": [
            |          false,
            |          true
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(6).dates$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "dates$": null
            |      },
            |      {
            |        "int": 6,
            |        "dates$": [
            |          "2001-07-01",
            |          "2002-01-01"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(7).uuids$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 7,
             |        "uuids$$": null
             |      },
             |      {
             |        "int": 7,
             |        "uuids$$": [
             |          "$uuid1",
             |          "$uuid2"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(8).uris$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 8,
             |        "uris$$": null
             |      },
             |      {
             |        "int": 8,
             |        "uris$$": [
             |          "$uri1",
             |          "$uri2"
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(9).bigInts$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 9,
             |        "bigInts$$": null
             |      },
             |      {
             |        "int": 9,
             |        "bigInts$$": [
             |          1,
             |          2
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(10).bigDecs$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 10,
             |        "bigDecs$$": null
             |      },
             |      {
             |        "int": 10,
             |        "bigDecs$$": [
             |          1.0,
             |          2.0
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)


        _ <- Ns.int(11).enums$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 11,
            |        "enums$": null
            |      },
            |      {
            |        "int": 11,
            |        "enums$": [
            |          "enum1",
            |          "enum2"
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(12).refs1$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 12,
             |        "refs1$$": null
             |      },
             |      {
             |        "int": 12,
             |        "refs1$$": [
             |          $refId
             |        ]
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "Card map optional" - core { implicit conn =>
      for {
        _ <- Ns.int.strMap$ insert List((1, None), (1, Some(Map("a" -> "A", "b" -> "B"))))
        _ <- Ns.int.intMap$ insert List((2, None), (2, Some(Map("a" -> 1, "b" -> 2))))
        _ <- Ns.int.longMap$ insert List((3, None), (3, Some(Map("a" -> 1L, "b" -> 2L))))
        _ <- Ns.int.doubleMap$ insert List((4, None), (4, Some(Map("a" -> 1.1, "b" -> 2.2))))
        _ <- Ns.int.boolMap$ insert List((5, None), (5, Some(Map("a" -> true, "b" -> false))))
        _ <- Ns.int.dateMap$ insert List((6, None), (6, Some(Map("a" -> date1, "b" -> date2))))
        _ <- Ns.int.uuidMap$ insert List((7, None), (7, Some(Map("a" -> uuid1, "b" -> uuid2))))
        _ <- Ns.int.uriMap$ insert List((8, None), (8, Some(Map("a" -> uri1, "b" -> uri2))))
        _ <- Ns.int.bigIntMap$ insert List((9, None), (9, Some(Map("a" -> bigInt1, "b" -> bigInt2))))
        _ <- Ns.int.bigDecMap$ insert List((10, None), (10, Some(Map("a" -> bigDec1, "b" -> bigDec2))))

        _ <- Ns.int(1).strMap$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "strMap$": null
            |      },
            |      {
            |        "int": 1,
            |        "strMap$": {
            |          "a": "A",
            |          "b": "B"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(2).intMap$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 2,
            |        "intMap$": null
            |      },
            |      {
            |        "int": 2,
            |        "intMap$": {
            |          "a": 1,
            |          "b": 2
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(3).longMap$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3,
            |        "longMap$": null
            |      },
            |      {
            |        "int": 3,
            |        "longMap$": {
            |          "a": 1,
            |          "b": 2
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(4).doubleMap$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "doubleMap$": null
            |      },
            |      {
            |        "int": 4,
            |        "doubleMap$": {
            |          "a": 1.1,
            |          "b": 2.2
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(5).boolMap$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 5,
            |        "boolMap$": null
            |      },
            |      {
            |        "int": 5,
            |        "boolMap$": {
            |          "a": true,
            |          "b": false
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(6).dateMap$.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 6,
            |        "dateMap$": null
            |      },
            |      {
            |        "int": 6,
            |        "dateMap$": {
            |          "a": "2001-07-01",
            |          "b": "2002-01-01"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int(7).uuidMap$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 7,
             |        "uuidMap$$": null
             |      },
             |      {
             |        "int": 7,
             |        "uuidMap$$": {
             |          "a": "$uuid1",
             |          "b": "$uuid2"
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(8).uriMap$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 8,
             |        "uriMap$$": null
             |      },
             |      {
             |        "int": 8,
             |        "uriMap$$": {
             |          "a": "$uri1",
             |          "b": "$uri2"
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(9).bigIntMap$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 9,
             |        "bigIntMap$$": null
             |      },
             |      {
             |        "int": 9,
             |        "bigIntMap$$": {
             |          "a": 1,
             |          "b": 2
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin)

        _ <- Ns.int(10).bigDecMap$.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "int": 10,
             |        "bigDecMap$$": null
             |      },
             |      {
             |        "int": 10,
             |        "bigDecMap$$": {
             |          "a": 1.0,
             |          "b": 2.0
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }
  }
}