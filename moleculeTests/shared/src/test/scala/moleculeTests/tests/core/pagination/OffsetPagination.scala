package moleculeTests.tests.core.pagination

import molecule.core.exceptions.MoleculeException
import molecule.core.util.Executor._
import molecule.datomic.api.out5._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


object OffsetPagination extends AsyncTestSuite {

  lazy val tests = Tests {

    "From start/end" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        // From start ...............

        // Positive limit returns data from start
        _ <- Ns.int.get(2).map(_ ==> List(1, 2))

        // Limit+offset returns data + total row count (for calculating number of expected pages)
        _ <- Ns.int.get(2, 0).map(_ ==> (List(1, 2), 3))

        // Offset/skip rows
        _ <- Ns.int.get(2, 1).map(_ ==> (List(2, 3), 3))
        _ <- Ns.int.get(2, 2).map(_ ==> (List(3), 3))
        _ <- Ns.int.get(2, 3).map(_ ==> (Nil, 3))
        _ <- Ns.int.get(2, 4).map(_ ==> (Nil, 3))


        // From end ...............

        // Negative limit returns data from end
        _ <- Ns.int.get(-2).map(_ ==> List(2, 3))
        _ <- Ns.int.get(-2, 0).map(_ ==> (List(2, 3), 3))

        // Offset/skip rows from back
        _ <- Ns.int.get(-2, 1).map(_ ==> (List(1, 2), 3))
        _ <- Ns.int.get(-2, 2).map(_ ==> (List(1), 3))
        _ <- Ns.int.get(-2, 3).map(_ ==> (Nil, 3))
        _ <- Ns.int.get(-2, 4).map(_ ==> (Nil, 3))


        // Wrong limit/offset ...............

        _ <- Ns.int.a1.get(0, 10)
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Limit cannot be 0. Please use a positive number to get rows " +
              "from start, or a negative number to get rows from end."
          }

        _ <- Ns.int.a1.get(20, -10)
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Offset has to be >= 0. Found: -10"
          }
      } yield ()
    }


    "Flat" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        _ <- Ns.int.a1.get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.a1.get(2).map(_ ==> List(1, 2))

        _ <- Ns.int.a1.get(2, 0).map(_ ==> (List(1, 2), 3))
        _ <- Ns.int.a1.get(2, 2).map(_ ==> (List(3), 3))

        _ <- Ns.int.d1.get(2, 0).map(_ ==> (List(3, 2), 3))
        _ <- Ns.int.d1.get(2, 2).map(_ ==> (List(1), 3))

        // offset beyond totalCount. Row count still returned
        _ <- Ns.int.get(2, 4).map(_ ==> (Nil, 3))

        // Empty result set
        _ <- Ns.str.get(2, 4).map(_ ==> (Nil, 0))


        _ <- Ns.int.a1.getObjs.collect { case List(o1, o2, o3) =>
          o1.int ==> 1
          o2.int ==> 2
          o3.int ==> 3
        }
        _ <- Ns.int.a1.getObjs(2).collect { case List(o1, o2) =>
          o1.int ==> 1
          o2.int ==> 2
        }

        _ <- Ns.int.a1.getObjs(2, 0).collect { case (List(o1, o2), totalCount) =>
          totalCount ==> 3
          o1.int ==> 1
          o2.int ==> 2
        }
        _ <- Ns.int.a1.getObjs(2, 2).collect { case (List(o1), totalCount) =>
          totalCount ==> 3
          o1.int ==> 3
        }

        _ <- Ns.int.d1.getObjs(2, 0).collect { case (List(o1, o2), totalCount) =>
          totalCount ==> 3
          o1.int ==> 3
          o2.int ==> 2
        }
        _ <- Ns.int.d1.getObjs(2, 2).collect { case (List(o1), totalCount) =>
          totalCount ==> 3
          o1.int ==> 1
        }
        _ <- Ns.int.getObjs(2, 4).map(_ ==> (Nil, 3))
        _ <- Ns.str.getObjs(2, 4).map(_ ==> (Nil, 0))


        _ <- Ns.int.a1.getJson.map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 3,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1
            |      },
            |      {
            |        "int": 2
            |      },
            |      {
            |        "int": 3
            |      }
            |    ]
            |  }
            |}""".stripMargin)
        _ <- Ns.int.a1.getJson(2).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1
            |      },
            |      {
            |        "int": 2
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.getJson(2, 0).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1
            |      },
            |      {
            |        "int": 2
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.getJson(2, 2).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 2,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        _ <- Ns.int.d1.getJson(2, 0).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 3
            |      },
            |      {
            |        "int": 2
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.d1.getJson(2, 2).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 2,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.getJson(2, 4).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)

        _ <- Ns.str.getJson(2, 4).map(_ ==>
          """{
            |  "totalCount": 0,
            |  "limit"     : 2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Nested - tpl" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
        )

        // From start, asc .......................

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(2).map(_ ==> List(
          (1, List(11, 12)),
          (2, List(21, 22)),
        ))
        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, 0).map(_ ==> (
          List(
            (1, List(11, 12)),
            (2, List(21, 22)),
          ),
          3
        ))
        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, 2).map(_ ==> (
          List(
            (3, List(31, 32)),
          ),
          3
        ))
        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(2, 4).map(_ ==> (Nil, 3))
        _ <- Ns.str.a1.Refs1.*(Ref1.int1).get(2, 4).map(_ ==> (Nil, 0))


        // From start, desc .......................

        _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(2).map(_ ==> List(
          (3, List(31, 32)),
          (2, List(21, 22)),
        ))
        _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, 0).map(_ ==> (
          List(
            (3, List(31, 32)),
            (2, List(21, 22)),
          ),
          3
        ))
        _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, 2).map(_ ==> (
          List(
            (1, List(11, 12)),
          ),
          3
        ))
        _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(2, 4).map(_ ==> (Nil, 3))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1).get(2, 4).map(_ ==> (Nil, 0))


        // From end, asc .......................

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2).map(_ ==> List(
          (2, List(21, 22)),
          (3, List(31, 32)),
        ))
        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, 0).map(_ ==> (
          List(
            (2, List(21, 22)),
            (3, List(31, 32)),
          ),
          3
        ))
        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, 2).map(_ ==> (
          List(
            (1, List(11, 12)),
          ),
          3
        ))
        _ <- Ns.int.a1.Refs1.*(Ref1.int1).get(-2, 4).map(_ ==> (Nil, 3))
        _ <- Ns.str.a1.Refs1.*(Ref1.int1).get(-2, 4).map(_ ==> (Nil, 0))


        // From end, desc .......................

        _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2).map(_ ==> List(
          (2, List(21, 22)),
          (1, List(11, 12)),
        ))
        _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, 0).map(_ ==> (
          List(
            (2, List(21, 22)),
            (1, List(11, 12)),
          ),
          3
        ))
        _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, 2).map(_ ==> (
          List(
            (3, List(31, 32)),
          ),
          3
        ))
        _ <- Ns.int.d1.Refs1.*(Ref1.int1).get(-2, 4).map(_ ==> (Nil, 3))
        _ <- Ns.str.d1.Refs1.*(Ref1.int1).get(-2, 4).map(_ ==> (Nil, 0))
      } yield ()
    }


    "Nested - obj" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
        )

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getObjs.collect { case List(o1, o2, o3) =>
          o1.int ==> 1
          o1.Refs1.head.int1 ==> 11
          o1.Refs1.last.int1 ==> 12
          o2.int ==> 2
          o2.Refs1.head.int1 ==> 21
          o2.Refs1.last.int1 ==> 22
          o3.int ==> 3
          o3.Refs1.head.int1 ==> 31
          o3.Refs1.last.int1 ==> 32
        }

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getObjs(2).collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.Refs1.head.int1 ==> 11
          o1.Refs1.last.int1 ==> 12
          o2.int ==> 2
          o2.Refs1.head.int1 ==> 21
          o2.Refs1.last.int1 ==> 22
        }

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getObjs(2, 0).collect { case (List(o1, o2), totalCount) =>
          totalCount ==> 3
          o1.int ==> 1
          o1.Refs1.head.int1 ==> 11
          o1.Refs1.last.int1 ==> 12
          o2.int ==> 2
          o2.Refs1.head.int1 ==> 21
          o2.Refs1.last.int1 ==> 22
        }
        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getObjs(2, 2).collect { case (List(o1), totalCount) =>
          totalCount ==> 3
          o1.int ==> 3
          o1.Refs1.head.int1 ==> 31
          o1.Refs1.last.int1 ==> 32
        }
        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getObjs(2, 4).collect { case (data, totalCount) =>
          totalCount ==> 3
          data ==> Nil
        }
        _ <- Ns.str.a1.Refs1.*(Ref1.int1).getObjs(2, 4).collect { case (data, totalCount) =>
          totalCount ==> 0
          data ==> Nil
        }
      } yield ()
    }


    "nested - json" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
        )

        // From start, asc .......................

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(2).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      },
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
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(2, 0).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      },
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
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(2, 2).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 2,
            |  "data": {
            |    "Ns": [
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

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(2, 4).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)

        _ <- Ns.str.a1.Refs1.*(Ref1.int1).getJson(2, 4).map(_ ==>
          """{
            |  "totalCount": 0,
            |  "limit"     : 2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)


        // From start, desc .......................

        _ <- Ns.int.d1.Refs1.*(Ref1.int1).getJson(2).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
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
            |      },
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
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.d1.Refs1.*(Ref1.int1).getJson(2, 0).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
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
            |      },
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
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.d1.Refs1.*(Ref1.int1).getJson(2, 2).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 2,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.d1.Refs1.*(Ref1.int1).getJson(2, 4).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : 2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)

        _ <- Ns.str.d1.Refs1.*(Ref1.int1).getJson(2, 4).map(_ ==>
          """{
            |  "totalCount": 0,
            |  "limit"     : 2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)


        // From end, asc .......................

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

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(-2, 0).map(_ ==>
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

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(-2, 2).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : -2,
            |  "offset"    : 2,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.Refs1.*(Ref1.int1).getJson(-2, 4).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : -2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)

        _ <- Ns.str.a1.Refs1.*(Ref1.int1).getJson(-2, 4).map(_ ==>
          """{
            |  "totalCount": 0,
            |  "limit"     : -2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)


        // From end, desc .......................

        _ <- Ns.int.d1.Refs1.*(Ref1.int1).getJson(-2).map(_ ==>
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
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.d1.Refs1.*(Ref1.int1).getJson(-2, 0).map(_ ==>
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
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.d1.Refs1.*(Ref1.int1).getJson(-2, 2).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : -2,
            |  "offset"    : 2,
            |  "data": {
            |    "Ns": [
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

        _ <- Ns.int.d1.Refs1.*(Ref1.int1).getJson(-2, 4).map(_ ==>
          """{
            |  "totalCount": 3,
            |  "limit"     : -2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)

        _ <- Ns.str.d1.Refs1.*(Ref1.int1).getJson(-2, 4).map(_ ==>
          """{
            |  "totalCount": 0,
            |  "limit"     : -2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)

      } yield ()
    }


    "Optional nested - tpl" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, Nil),
        )

        // All
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get.map(_ ==> List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, Nil),
        ))


        // From start, asc .......................

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get(2).map(_ ==> List(
          (1, List(11, 12)),
          (2, List(21, 22)),
        ))
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get(2, 0).map(_ ==> (
          List(
            (1, List(11, 12)),
            (2, List(21, 22)),
          ),
          4
        ))
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get(2, 2).map(_ ==> (
          List(
            (3, List(31, 32)),
            (4, Nil),
          ),
          4
        ))
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get(2, 4).map(_ ==> (Nil, 4))
        _ <- Ns.str.a1.Refs1.*?(Ref1.int1).get(2, 4).map(_ ==> (Nil, 0))


        // From start, desc .......................

        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).get(2).map(_ ==> List(
          (4, Nil),
          (3, List(31, 32)),
        ))
        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).get(2, 0).map(_ ==> (
          List(
            (4, Nil),
            (3, List(31, 32)),
          ),
          4
        ))
        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).get(2, 2).map(_ ==> (
          List(
            (2, List(21, 22)),
            (1, List(11, 12)),
          ),
          4
        ))
        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).get(2, 4).map(_ ==> (Nil, 4))
        _ <- Ns.str.d1.Refs1.*?(Ref1.int1).get(2, 4).map(_ ==> (Nil, 0))


        // From end, asc .......................

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get(-2).map(_ ==> List(
          (3, List(31, 32)),
          (4, Nil),
        ))
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get(-2, 0).map(_ ==> (
          List(
            (3, List(31, 32)),
            (4, Nil),
          ),
          4
        ))
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get(-2, 2).map(_ ==> (
          List(
            (1, List(11, 12)),
            (2, List(21, 22)),
          ),
          4
        ))
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).get(-2, 4).map(_ ==> (Nil, 4))
        _ <- Ns.str.a1.Refs1.*?(Ref1.int1).get(-2, 4).map(_ ==> (Nil, 0))


        // From end, desc .......................

        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).get(-2).map(_ ==> List(
          (2, List(21, 22)),
          (1, List(11, 12)),
        ))
        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).get(-2, 0).map(_ ==> (
          List(
            (2, List(21, 22)),
            (1, List(11, 12)),
          ),
          4
        ))
        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).get(-2, 2).map(_ ==> (
          List(
            (4, Nil),
            (3, List(31, 32)),
          ),
          4
        ))
        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).get(-2, 4).map(_ ==> (Nil, 4))
        _ <- Ns.str.d1.Refs1.*?(Ref1.int1).get(-2, 4).map(_ ==> (Nil, 0))
      } yield ()
    }


    "Optional nested - obj" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, Nil),
        )

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getObjs.collect { case List(o1, o2, o3, o4) =>
          o1.int ==> 1
          o1.Refs1.head.int1 ==> 11
          o1.Refs1.last.int1 ==> 12
          o2.int ==> 2
          o2.Refs1.head.int1 ==> 21
          o2.Refs1.last.int1 ==> 22
          o3.int ==> 3
          o3.Refs1.head.int1 ==> 31
          o3.Refs1.last.int1 ==> 32
          o4.int ==> 4
          o4.Refs1 ==> Nil
        }
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getObjs(2).collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.Refs1.head.int1 ==> 11
          o1.Refs1.last.int1 ==> 12
          o2.int ==> 2
          o2.Refs1.head.int1 ==> 21
          o2.Refs1.last.int1 ==> 22
        }

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getObjs(2, 0).collect { case (List(o1, o2), totalCount) =>
          totalCount ==> 4
          o1.int ==> 1
          o1.Refs1.head.int1 ==> 11
          o1.Refs1.last.int1 ==> 12
          o2.int ==> 2
          o2.Refs1.head.int1 ==> 21
          o2.Refs1.last.int1 ==> 22
        }
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getObjs(2, 2).collect { case (List(o1, o2), totalCount) =>
          totalCount ==> 4
          o1.int ==> 3
          o1.Refs1.head.int1 ==> 31
          o1.Refs1.last.int1 ==> 32
          o2.int ==> 4
          o2.Refs1 ==> Nil
        }
        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getObjs(2, 4).collect { case (data, totalCount) =>
          totalCount ==> 4
          data ==> Nil
        }
        _ <- Ns.str.a1.Refs1.*?(Ref1.int1).getObjs(2, 4).collect { case (data, totalCount) =>
          totalCount ==> 0
          data ==> Nil
        }
      } yield ()
    }

    "Optional nested - json" - core { implicit conn =>
      for {
        _ <- Ns.int.Refs1.*(Ref1.int1) insert List(
          (1, List(11, 12)),
          (2, List(21, 22)),
          (3, List(31, 32)),
          (4, Nil),
        )

        // From start, asc .......................

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getJson(2).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      },
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
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getJson(2, 0).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      },
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
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getJson(2, 2).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : 2,
            |  "offset"    : 2,
            |  "data": {
            |    "Ns": [
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
            |      },
            |      {
            |        "int": 4,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getJson(2, 4).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : 2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)

        _ <- Ns.str.a1.Refs1.*?(Ref1.int1).getJson(2, 4).map(_ ==>
          """{
            |  "totalCount": 0,
            |  "limit"     : 2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)


        // From start, desc .......................

        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).getJson(2).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "Refs1": []
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

        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).getJson(2, 0).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : 2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "Refs1": []
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

        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).getJson(2, 2).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : 2,
            |  "offset"    : 2,
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
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).getJson(2, 4).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : 2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)

        _ <- Ns.str.d1.Refs1.*?(Ref1.int1).getJson(2, 4).map(_ ==>
          """{
            |  "totalCount": 0,
            |  "limit"     : 2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)


        // From end, asc .......................

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getJson(-2).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : -2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
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
            |      },
            |      {
            |        "int": 4,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getJson(-2, 0).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : -2,
            |  "offset"    : 0,
            |  "data": {
            |    "Ns": [
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
            |      },
            |      {
            |        "int": 4,
            |        "Refs1": []
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getJson(-2, 2).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : -2,
            |  "offset"    : 2,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      },
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
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.a1.Refs1.*?(Ref1.int1).getJson(-2, 4).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : -2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)

        _ <- Ns.str.a1.Refs1.*?(Ref1.int1).getJson(-2, 4).map(_ ==>
          """{
            |  "totalCount": 0,
            |  "limit"     : -2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)


        // From end, desc .......................

        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).getJson(-2).map(_ ==>
          """{
            |  "totalCount": 4,
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
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).getJson(-2, 0).map(_ ==>
          """{
            |  "totalCount": 4,
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
            |        "int": 1,
            |        "Refs1": [
            |          {
            |            "int1": 11
            |          },
            |          {
            |            "int1": 12
            |          }
            |        ]
            |      }
            |    ]
            |  }
            |}""".stripMargin)

        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).getJson(-2, 2).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : -2,
            |  "offset"    : 2,
            |  "data": {
            |    "Ns": [
            |      {
            |        "int": 4,
            |        "Refs1": []
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

        _ <- Ns.int.d1.Refs1.*?(Ref1.int1).getJson(-2, 4).map(_ ==>
          """{
            |  "totalCount": 4,
            |  "limit"     : -2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)

        _ <- Ns.str.d1.Refs1.*?(Ref1.int1).getJson(-2, 4).map(_ ==>
          """{
            |  "totalCount": 0,
            |  "limit"     : -2,
            |  "offset"    : 4,
            |  "data": {
            |    "Ns": []
            |  }
            |}""".stripMargin)
      } yield ()
    }


    "Aggregate type changes" - core { implicit conn =>
      for {
        _ <- Ns.str.insert("a", "a", "b", "c")

        // Empty result set returned for offset exceeding total count
        _ <- Ns.str.str(count).get.map(_ ==> List(("a", 2), ("b", 1), ("c", 1)))
      } yield ()
    }


    // General problems with offset pagination (for any db system):

    "Re-seen data" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 3, 5)

        _ <- Ns.int.a1.get(2, 0).map(_._1 ==> List(1, 3))

        // Data added before next page is fetched
        _ <- Ns.int(2).save

        // 3 is shown again!
        _ <- Ns.int.a1.get(2, 2).map(_._1 ==> List(3, 5))
      } yield ()
    }

    "Skipped data" - core { implicit conn =>
      for {

        eids <- Ns.int.insert(1, 2, 3, 4).map(_.eids)

        _ <- Ns.int.a1.get(2, 0).map(_._1 ==> List(1, 2))

        // First row (1) retracted before next page is fetched
        _ <- eids.head.retract

        // 3 is never shown!
        _ <- Ns.int.a1.get(2, 2).map(_._1 ==> List(4))
      } yield ()
    }
  }
}
