package moleculeTests.tests.db.datomic.txMetaData

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in3_out10._
import molecule.datomic.base.ops.QueryOps
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object MetaInsert extends AsyncTestSuite {

  lazy val tests = Tests {

    "Insert 1 or more" - core { implicit conn =>
      for {
        // Can't add transaction meta data along other data of molecule
        _ <- Ns.int.Tx(Ns.str).insert(0, "a")
          .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
          err ==> s"[onlyTacitTxAttrs]  For inserts, tx meta data can only be applied to tacit attributes, like: `Ns.str_(<metadata>)`"
        }

        // Can't both apply meta data to tx attribute and insert meta data
        _ <- Ns.int.Tx(Ns.str("a")).insert(List((0, "b")))
          .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
          err ==> s"[onlyTacitTxAttrs]  For inserts, tx meta data can only be applied to tacit attributes, like: `Ns.str_(<metadata>)`"
        }

        // Can't both apply meta data to optional tx attribute and insert meta data
        _ <- Ns.int.Tx(Ns.str$(Some("a"))).insert(List((0, Some("b"))))
          .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
          err ==> s"[onlyTacitTxAttrs]  For inserts, tx meta data can only be applied to tacit attributes, like: `Ns.str_(<metadata>)`"
        }

        // Apply tx meta data to tacit tx attributes:
        _ <- Ns.int.Tx(Ns.str_("a")).insert(0)

        // Data without tx meta data
        _ <- Ns.int.insert(1)

        // Base data having tx meta data
        _ <- Ns.int.Tx(Ns.str_).get.map(_ ==> List(0))

        // All base data
        _ <- Ns.int.get.map(_ ==> List(1, 0))

        // Transaction molecules don't have to be related to the base molecule
        _ <- Ns.int.Tx(Ref1.str1_("b")).insert(2)
        _ <- Ns.int.Tx(Ref2.str2_("c")).insert(3)

        _ <- Ns.int.Tx(Ref1.str1_).get.map(_ ==> List(2))
        _ <- Ns.int.Tx(Ref2.str2_).get.map(_ ==> List(3))

        // Base data with tx meta data
        _ <- Ns.int.Tx(Ns.str).get.map(_ ==> List((0, "a")))
        _ <- Ns.int.Tx(Ref1.str1).get.map(_ ==> List((2, "b")))
        _ <- Ns.int.Tx(Ref2.str2).get.map(_ ==> List((3, "c")))
      } yield ()
    }

    "Large tx meta data" - core { implicit conn =>
      for {
        _ <- Ns.str.Tx(Ns
          .int_(int1)
          .long_(long1)
          .double_(double1)
          .bool_(bool1)
          .date_(date1)
          .uuid_(uuid1)
        ).insert("With tx meta data")

        // Add data without tx meta data
        _ <- Ns.str.insert("Without tx meta data")

        // Data with and without tx meta data created
        _ <- Ns.str.get.map(_ ==> List(
          "With tx meta data",
          "Without tx meta data"
        ))

        // Use transaction meta data to filter
        _ <- Ns.str.Tx(Ns.int_(int1)).get.map(_ ==> List("With tx meta data"))
        _ <- Ns.str.Tx(Ns.long_(long1)).get.map(_ ==> List("With tx meta data"))
        _ <- Ns.str.Tx(Ns.double_(double1)).get.map(_ ==> List("With tx meta data"))
        _ <- Ns.str.Tx(Ns.bool_(bool1)).get.map(_ ==> List("With tx meta data"))
        _ <- Ns.str.Tx(Ns.date_(date1)).get.map(_ ==> List("With tx meta data"))
        _ <- Ns.str.Tx(Ns.uuid_(uuid1)).get.map(_ ==> List("With tx meta data"))

        // All tx meta data present
        _ <- Ns.str.Tx(Ns
          .int_(int1)
          .long_(long1)
          .double_(double1)
          .bool_(bool1)
          .date_(date1)
          .uuid_(uuid1)
        ).get.map(_ ==> List("With tx meta data"))
      } yield ()
    }


    "Insert tx meta ref with multiple attrs" - core { implicit conn =>
      for {
        _ <- Ns.int.Tx(Ns.str_("a").Ref1.str1_("b").int1_(7)) insert List(1, 2, 3)

        _ <- Ns.int.a1.Tx(Ns.str.Ref1.str1.int1).get.map(_ ==> List(
          (1, "a", "b", 7),
          (2, "a", "b", 7),
          (3, "a", "b", 7)
        ))
      } yield ()
    }


    "Insert multiple tx meta refs with multiple attrs" - core { implicit conn =>
      for {
        _ <- Ns.int.Tx(Ns.str_("a").Ref1.str1_("b").int1_(7).Ref2.str2_("c").int2_(8)) insert List(1, 2, 3)

        _ <- Ns.int.a1.Tx(Ns.str.Ref1.str1.int1.Ref2.str2.int2).get.map(_ ==> List(
          (1, "a", "b", 7, "c", 8),
          (2, "a", "b", 7, "c", 8),
          (3, "a", "b", 7, "c", 8)
        ))
      } yield ()
    }
  }
}