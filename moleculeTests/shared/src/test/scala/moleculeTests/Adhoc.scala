package moleculeTests

import java.util.UUID
import molecule.core.exceptions.MoleculeException
import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.DateHandling
import molecule.datomic.api.in3_out11._
import molecule.datomic.api.out11.m
import molecule.datomic.base.util.SystemPeerServer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import moleculeTests.tests.core.json.JsonAttributes.{bigDec1, bigDec2, bigInt1, bigInt2, date1, date2, enum1, enum2, system, uri1, uri2, uuid1, uuid2}
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object Adhoc extends AsyncTestSuite {


  lazy val tests = Tests {

    //    "adhoc" - bidirectional { implicit conn =>
    "adhoc" - core { implicit conn =>


      for {
        _ <- Future(1 ==> 1)



//        // Creating 3 entities referencing 3 other entities
//        tx <- Ns.str.Ref1.str1 insert List(
//          ("a0", "a1"),
//          ("b0", "b1"),
//          ("c0", "c1")
//        )
//        List(a0, a1, b0, b1, c0, c1) = tx.eids

        // Get attribute values from 2 namespaces
        // Namespace references like `Ref1` starts with Capital letter
        _ <- Ns.str.Ref1.str1.get

//        _ <- Ns.str.Ref1.str1.getJson.map(_ ==>
//          """{
//            |  "data": {
//            |    "Ns": [
//            |      {
//            |        "str": "a0"
//            |        "Ref1": {
//            |          "str1": "a1"
//            |        }
//            |      },
//            |      {
//            |        "str": "b0"
//            |        "Ref1": {
//            |          "str1": "b1"
//            |        }
//            |      },
//            |      {
//            |        "str": "c0"
//            |        "Ref1": {
//            |          "str1": "c1"
//            |        }
//            |      }
//            |    ]
//            |  }
//            |}""".stripMargin)
//
//
//        // We can also retrieve the referenced entity id
//        // Referenced entity id `ref1` starts with lower case letter
//        _ <- Ns.str.ref1.getJson.map(_ ==>
//          s"""{
//             |  "data": {
//             |    "Ns": [
//             |      {
//             |        "str": "a0"
//             |        "ref1": $a1
//             |      },
//             |      {
//             |        "str": "b0"
//             |        "ref1": $b1
//             |      },
//             |      {
//             |        "str": "c0"
//             |        "ref1": $c1
//             |      }
//             |    ]
//             |  }
//             |}""".stripMargin)

        /*
{
"ref1", "Ref1"), Var("c"), Empty, NoBinding), DataClause(ImplDS, Var("c"), KW("Ref1", "str1", ""), Var("d"), Empty, NoBinding)))), scala.None, scala.None)) {
    def <init>() = {
      super.<init>();
      ()
    };
    final override def row2tpl(row: java.util.List[AnyRef]): scala.Tuple2[String, String] = scala.Tuple2(castOne[String](row, 0), castOne[String](row, 1));
    final override def row2obj(row: java.util.List[AnyRef]): molecule.core.dsl.base.Init with moleculeTests.tests.core.base.dsl.CoreTest.Ns_str with moleculeTests.tests.core.base.dsl.CoreTest.Ns__Ref1[molecule.core.dsl.base.Init with moleculeTests.tests.core.base.dsl.CoreTest.Ref1_str1] = {
      final class $anon extends Init with Ns_str with Ns__Ref1[Init with Ref1_str1] {
        def <init>() = {
          super.<init>();
          ()
        };
        final override lazy val str: String = castOne[String](row, 0);
        final override def Ref1: Init with Ref1_str1 = {
          final class $anon extends Init with Ref1_str1 {
            def <init>() = {
              super.<init>();
              ()
            };
            final override lazy val str1: String = castOne[String](row, 1)
          };
          new $anon()
        }
      };
      new $anon()
    };
    final override def row2json(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = {
      jsonOneQuoted(sb, "str", row, 0);
      sb.append(",\n        ");
      jsonOneQuoted(sb, "ref1.str1", row, 1)
    }
  };
  new outMolecule$macro$1()
}
------------------------------------------------
Obj("", "", 0, List(
  Prop("Ns_str", "str", "String", <cast-lambda>, None),
  Obj("Ns__Ref1", "Ref1", 1, List(
    Prop("Ref1_str1", "str1", "String", <cast-lambda>, None)))))
======================================================

 */





//                _ <- (Ns.int.str.Refs1 * Ref1.int1) insert List(
//                  (1, "a", List(10, 11)),
//                  (2, "b", List(20, 21))
//                )
//        //
        //        _ <- m(Ns.int.str.Refs1 * Ref1.int1).getJson.map(_ ==>
        //          """{
        //            |  "data": {
        //            |    "Ns": [
        //            |      {
        //            |        "int": 1,
        //            |        "str": "a",
        //            |        "Refs1": [
        //            |          {
        //            |            "int1": 10
        //            |          },
        //            |          {
        //            |            "int1": 11
        //            |          }
        //            |        ]
        //            |      },
        //            |      {
        //            |        "int": 2,
        //            |        "str": "b",
        //            |        "Refs1": [
        //            |          {
        //            |            "int1": 20
        //            |          },
        //            |          {
        //            |            "int1": 21
        //            |          }
        //            |        ]
        //            |      }
        //            |    ]
        //            |  }
        //            |}""".stripMargin)


        //        _ <- m(Ref1.int1.Nss * Ns.int.str$) insert List(
        //          (1, List((1, Some("a")), (2, None))),
        //          (2, List())
        //        )

        //        _ <- m(Ref1.int1.Nss * Ns.int.str$).get.map(_ ==> List(
        //          (1, List((1, Some("a")), (2, None)))
        //        ))
        //        _ <- m(Ref1.int1.Nss * Ns.int.str).get.map(_ ==> List(
        //          (1, List((1, "a")))
        //        ))
        //        _ <- m(Ref1.int1.Nss * Ns.int.str_).get.map(_ ==> List(
        //          (1, List(1))
        //        ))

        //        // Optional nested
        //        _ <- m(Ref1.int1.Nss *? Ns.int.str$).get.map(_.sortBy(_._1) ==> List(
        //          (1, List((1, Some("a")), (2, None))),
        //          (2, List())
        //        ))
        //        _ <- m(Ref1.int1.Nss *? Ns.int.str).get.map(_.sortBy(_._1) ==> List(
        //          (1, List((1, "a"))),
        //          (2, List())
        //        ))

        //        _ <- m(Ns.str.Refs1 * Ref1.str1.int1$) insert List(
        //          ("a", List(("a1", Some(11)))),
        //          ("b", List(("b1", None))))


        //        _ <- m(Ns.int.Refs1 * Ref1.int1) insert List(
        //          (1, List(1, 2)),
        //          (2, List(2, 3)),
        //          (3, List())
        //        )
        //
        //        _ <- m(Ns.int.Refs1 * Ref1.int1.>(1)).get.map(_.sortBy(_._1) ==> List(
        //          (1, List(2)),
        //          (2, List(2, 3))
        //        ))
        //
        //        // Now there's a ref from entity with "b" to entity with "b1"
        //        _ <- m(Ns.str.Refs1 * Ref1.str1.int1$).get.map(_.sortBy(_._1) ==> List(
        //          ("a", List(("a1", Some(11)))),
        //          ("b", List(("b1", None)))))

        //        _ <- m(Ns.str.Refs1 * (Ref1.str1$.int1.Refs2 * Ref2.str2.int2$)) insert List(
        //          ("a", List(
        //            (None, 11, List(
        //              ("a2", Some(12)))))),
        //          ("b", List(
        //            (Some("b1"), 21, List(
        //              ("b2", None))))))

        //        _ <- m(Ns.str.Refs1 * (Ref1.str1$.int1.Refs2 * Ref2.str2.int2$)).get.map(_.sortBy(_._1) ==> List(
        //          ("a", List(
        //            (None, 11, List(
        //              ("a2", Some(12)))))),
        //          ("b", List(
        //            (Some("b1"), 21, List(
        //              ("b2", None)))))))

      } yield ()
    }


    //    "String" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.str$) insert List(
    //          (1, List((1, Some("a")), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.str$).get.map(_ ==> List(
    //          (1, List((1, Some("a")), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.str).get.map(_ ==> List(
    //          (1, List((1, "a")))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.str_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        // Optional nested
    //        _ <- m(Ref1.int1.Nss *? Ns.int.str$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some("a")), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.str).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, "a"))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.str_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }
    //
    //    "Enum" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.enum$) insert List(
    //          (1, List((1, Some("enum1")), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.enum$).get.map(_ ==> List(
    //          (1, List((1, Some("enum1")), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.enum).get.map(_ ==> List(
    //          (1, List((1, "enum1")))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.enum_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        _ <- m(Ref1.int1.Nss *? Ns.int.enum$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some("enum1")), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.enum).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, "enum1"))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.enum_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }
    //
    //    "Long" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.long$) insert List(
    //          (1, List((1, Some(10L)), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.long$).get.map(_ ==> List(
    //          (1, List((1, Some(10L)), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.long).get.map(_ ==> List(
    //          (1, List((1, 10L)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.long_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        // Optional nested
    //        _ <- m(Ref1.int1.Nss *? Ns.int.long$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some(10L)), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.long).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, 10L))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.long_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }
    //
    //    "ref" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.ref1$) insert List(
    //          (1, List((1, Some(42L)), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.ref1$).get.map(_ ==> List(
    //          (1, List((1, Some(42L)), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.ref1).get.map(_ ==> List(
    //          (1, List((1, 42L)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.ref1_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        _ <- m(Ref1.int1.Nss *? Ns.int.ref1$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some(42L)), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.ref1).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, 42L))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.ref1_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }
    //
    //    "Double" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.double$) insert List(
    //          (1, List((1, Some(1.1)), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.double$).get.map(_ ==> List(
    //          (1, List((1, Some(1.1)), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.double).get.map(_ ==> List(
    //          (1, List((1, 1.1)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.double_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        _ <- m(Ref1.int1.Nss *? Ns.int.double$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some(1.1)), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.double).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, 1.1))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.double_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }
    //
    //    "Boolean" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.bool$) insert List(
    //          (1, List((1, Some(true)), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.bool$).get.map(_ ==> List(
    //          (1, List((1, Some(true)), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.bool).get.map(_ ==> List(
    //          (1, List((1, true)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.bool_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        _ <- m(Ref1.int1.Nss *? Ns.int.bool$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some(true)), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.bool).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, true))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.bool_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }
    //
    //    "Date" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.date$) insert List(
    //          (1, List((1, Some(date1)), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.date$).get.map(_ ==> List(
    //          (1, List((1, Some(date1)), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.date).get.map(_ ==> List(
    //          (1, List((1, date1)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.date_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        _ <- m(Ref1.int1.Nss *? Ns.int.date$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some(date1)), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.date).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, date1))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.date_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }
    //
    //    "UUID" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.uuid$) insert List(
    //          (1, List((1, Some(uuid1)), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.uuid$).get.map(_ ==> List(
    //          (1, List((1, Some(uuid1)), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.uuid).get.map(_ ==> List(
    //          (1, List((1, uuid1)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.uuid_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        _ <- m(Ref1.int1.Nss *? Ns.int.uuid$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some(uuid1)), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.uuid).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, uuid1))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.uuid_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }
    //
    //    "URI" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.uri$) insert List(
    //          (1, List((1, Some(uri1)), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.uri$).get.map(_ ==> List(
    //          (1, List((1, Some(uri1)), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.uri).get.map(_ ==> List(
    //          (1, List((1, uri1)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.uri_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        _ <- m(Ref1.int1.Nss *? Ns.int.uri$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some(uri1)), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.uri).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, uri1))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.uri_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }
    //
    //    "BigInt" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.bigInt$) insert List(
    //          (1, List((1, Some(bigInt1)), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.bigInt$).get.map(_ ==> List(
    //          (1, List((1, Some(bigInt1)), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.bigInt).get.map(_ ==> List(
    //          (1, List((1, bigInt1)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.bigInt_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        _ <- m(Ref1.int1.Nss *? Ns.int.bigInt$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some(bigInt1)), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.bigInt).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, bigInt1))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.bigInt_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }
    //
    //    "BigDecimal" - core { implicit conn =>
    //      for {
    //        _ <- m(Ref1.int1.Nss * Ns.int.bigDec$) insert List(
    //          (1, List((1, Some(bigDec1)), (2, None))),
    //          (2, List())
    //        )
    //
    //        _ <- m(Ref1.int1.Nss * Ns.int.bigDec$).get.map(_ ==> List(
    //          (1, List((1, Some(bigDec1)), (2, None)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.bigDec).get.map(_ ==> List(
    //          (1, List((1, bigDec1)))
    //        ))
    //        _ <- m(Ref1.int1.Nss * Ns.int.bigDec_).get.map(_ ==> List(
    //          (1, List(1))
    //        ))
    //
    //        _ <- m(Ref1.int1.Nss *? Ns.int.bigDec$).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, Some(bigDec1)), (2, None))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.bigDec).get.map(_.sortBy(_._1) ==> List(
    //          (1, List((1, bigDec1))),
    //          (2, List())
    //        ))
    //        _ <- m(Ref1.int1.Nss *? Ns.int.bigDec_).get.map(_.sortBy(_._1) ==> List(
    //          (1, List(1)),
    //          (2, List())
    //        ))
    //      } yield ()
    //    }


  }
}
