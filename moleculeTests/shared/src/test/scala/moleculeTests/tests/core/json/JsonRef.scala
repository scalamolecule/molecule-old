package moleculeTests.tests.core.json

import molecule.datomic.api.out11._
import molecule.datomic.base.util.SystemPeerServer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object JsonRef extends AsyncTestSuite {

  lazy val tests = Tests {

    "Ref / ref attr" - core { implicit conn =>
      for {
        // Creating 3 entities referencing 3 other entities
        tx <- Ns.str.Ref1.str1 insert List(
          ("a0", "a1"),
          ("b0", "b1"),
          ("c0", "c1")
        )
        List(a0, a1, b0, b1, c0, c1) = tx.eids

        // Get attribute values from 2 namespaces
        // Namespace references like `Ref1` starts with Capital letter
        _ <- Ns.str.Ref1.str1.getJson.map(_ ==>
          """{
            |  "data": {
            |    "Ns": [
            |      {
            |        "str": "a0"
            |        "Ref1": {
            |          "str1": "a1"
            |        }
            |      },
            |      {
            |        "str": "b0"
            |        "Ref1": {
            |          "str1": "b1"
            |        }
            |      },
            |      {
            |        "str": "c0"
            |        "Ref1": {
            |          "str1": "c1"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)


        // We can also retrieve the referenced entity id
        // Referenced entity id `ref1` starts with lower case letter
        _ <- Ns.str.ref1.getJson.map(_ ==>
          s"""{
             |  "data": {
             |    "Ns": [
             |      {
             |        "str": "a0"
             |        "ref1": $a1
             |      },
             |      {
             |        "str": "b0"
             |        "ref1": $b1
             |      },
             |      {
             |        "str": "c0"
             |        "ref1": $c1
             |      }
             |    ]
             |  }
             |}""".stripMargin)
      } yield ()
    }


    "ref/backref" - core { implicit conn =>
      for {
        _ <- Ns.int(0).str("a")
          .Ref1.int1(1).str1("b")
          .Refs2.int2(22)._Ref1
          .Ref2.int2(2).str2("c")._Ref1._Ns
          .Refs1.int1(11)
          .save

        _ <- Ns.int.Ref1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
        }
        _ <- Ns.int.Ref1.int1._Ns.str.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.str ==> "a"
        }
        _ <- Ns.int.Ref1.int1._Ns.Refs1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Refs1.int1 ==> 11
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.str1 ==> "b"
        }
        _ <- Ns.int.Ref1.Ref2.int2._Ref1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.int1 ==> 1
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.Refs2.int2.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.Refs2.int2 ==> 22
        }
        _ <- Ns.int.Ref1.Ref2.int2._Ref1.Refs2.int2.getObj.map { o =>
          o.int ==> 0
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.Refs2.int2 ==> 22
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1._Ns.str.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.str1 ==> "b"
          o.str ==> "a"
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1._Ns.str.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.str ==> "a"
        }
        _ <- Ns.int.Ref1.Ref2.int2._Ref1._Ns.str.getObj.map { o =>
          o.int ==> 0
          o.Ref1.Ref2.int2 ==> 2
          o.str ==> "a"
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1.str1._Ns.Refs1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.str1 ==> "b"
          o.Refs1.int1 ==> 11
        }
        _ <- Ns.int.Ref1.int1.Ref2.int2._Ref1._Ns.Refs1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Refs1.int1 ==> 11
        }
        _ <- Ns.int.Ref1.Ref2.int2._Ref1._Ns.Refs1.int1.getObj.map { o =>
          o.int ==> 0
          o.Ref1.Ref2.int2 ==> 2
          o.Refs1.int1 ==> 11
        }
        _ <- Ns.Ref1.Ref2.int2._Ref1._Ns.Refs1.int1.getObj.map { o =>
          o.Ref1.Ref2.int2 ==> 2
          o.Refs1.int1 ==> 11
        }
      } yield ()
    }
  }

}
