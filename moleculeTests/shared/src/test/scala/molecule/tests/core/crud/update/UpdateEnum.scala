package molecule.tests.core.crud.update

import java.util.concurrent.ExecutionException
import datomicClient.anomaly.Incorrect
import datomicClient.anomaly.Incorrect
import molecule.core.util.testing.expectCompileError
import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.base.util.SystemPeer
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateEnum extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one values" - {

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.enum("enum2").save
          eid = tx1.eid

          // Apply value (retracts current value)
          _ <- Ns(eid).enum("enum1").update
          _ <- Ns.enum.get.map(_.head ==> "enum1")

          // Apply new value
          _ <- Ns(eid).enum("enum2").update
          _ <- Ns.enum.get.map(_.head ==> "enum2")

          // Delete value (apply no value)
          _ <- Ns(eid).enum().update
          _ <- Ns.enum.get === List()


          // Applying multiple values to card-one attribute not allowed

          //      (Ns(eid).enum("enum2", "enum3").update must throwA[VerifyModelException])
          //        .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
          //        "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
          //        s"\n  Ns ... enum(enum2, enum3)"
        } yield ()
      }
    }


    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.enum(enum2).save
          eid = tx1.eid

          // Apply value (retracts current value)
          _ <- Ns(eid).enum(enum1).update
          _ <- Ns.enum.get.map(_.head ==> enum1)

          // Apply new value
          _ <- Ns(eid).enum(enum2).update
          _ <- Ns.enum.get.map(_.head ==> enum2)

          // Delete value (apply no value)
          _ <- Ns(eid).enum().update
          _ <- Ns.enum.get === List()


          // Applying multiple values to card-one attribute not allowed

          //      (Ns(eid).enum(enum2, enum3).update must throwA[VerifyModelException])
          //        .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
          //        "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
          //        s"\n  Ns ... enum($enum2, $enum3)"
        } yield ()
      }
    }


    "Card-many values" - {

      "assert" - core { implicit conn =>
        for {
          tx1 <- Ns.enums("enum1").save
          eid = tx1.eid

          // Assert value
          _ <- Ns(eid).enums.assert("enum2").update
          _ <- Ns.enums.get.map(_.head ==> Set("enum1", "enum2"))

          // Assert existing value (no effect)
          _ <- Ns(eid).enums.assert("enum2").update
          _ <- Ns.enums.get.map(_.head ==> Set("enum1", "enum2"))

          // Assert multiple values (vararg)
          _ <- Ns(eid).enums.assert("enum3", "enum4").update
          _ <- Ns.enums.get.map(_.head ==> Set("enum1", "enum2", "enum3", "enum4"))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).enums.assert(Seq("enum4", "enum5")).update
          _ <- Ns.enums.get.map(_.head ==> Set("enum1", "enum2", "enum3", "enum4", "enum5"))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).enums.assert(Seq[String]()).update
          _ <- Ns.enums.get.map(_.head ==> Set("enum1", "enum2", "enum3", "enum4", "enum5"))


          // Reset
          _ <- Ns(eid).enums().update

          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns(eid).enums.assert("enum1", "enum2", "enum2").update
          _ <- Ns.enums.get.map(_.head ==> Set("enum1", "enum2"))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx1 <- Ns.enums("enum1", "enum2", "enum3", "enum4", "enum5", "enum6").save
          eid = tx1.eid

          // Replace value
          _ <- Ns(eid).enums.replace("enum6" -> "enum8").update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1", "enum2", "enum3", "enum4", "enum5", "enum8"))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).enums.replace("enum5" -> "enum8").update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1", "enum2", "enum3", "enum4", "enum8"))

          // Replace multiple values (vararg)
          _ <- Ns(eid).enums.replace("enum3" -> "enum6", "enum4" -> "enum7").update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1", "enum2", "enum6", "enum7", "enum8"))

          //      // Different exceptions for each system
          //      if (system == SystemPeer) {
          //        // Trying to use a non-existing enum not possible
          //        (Ns(eid).enums.replace("x" -> "enum9").update must throwA[ExecutionException])
          //          .message === "Got the exception java.util.concurrent.ExecutionException: " +
          //          "java.lang.IllegalArgumentException: :db.error/not-an-entity " +
          //          s"""Unable to resolve entity: :Ns.enums/x in datom [$eid ":Ns/enums" ":Ns.enums/x"]"""
          //      } else {
          //        // Trying to use a non-existing enum not possible
          //        (Ns(eid).enums.replace("x" -> "enum9").update must throwA[Incorrect])
          //          .message === "Got the exception datomicClient.anomaly.Incorrect: " +
          //          s"""Unable to resolve entity: :Ns.enums/x in datom [$eid ":Ns/enums" ":Ns.enums/x"]"""
          //      }

          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1", "enum2", "enum6", "enum7", "enum8"))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).enums.replace(Seq("enum2" -> "enum5")).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1", "enum5", "enum6", "enum7", "enum8"))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).enums.replace(Seq[(String, String)]()).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1", "enum5", "enum6", "enum7", "enum8"))


          // Can't replace duplicate values

          //      expectCompileError(
          //        """Ns(eid).enums.replace("enum7" -> "enum8", "enum8" -> "enum8").update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/enums`:" +
          //          "\nenum8")
          //
          //      expectCompileError(
          //        """Ns(eid).enums.replace(Seq("enum7" -> "enum8", "enum8" -> "enum8")).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/enums`:" +
          //          "\nenum8")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx1 <- Ns.enums("enum1", "enum2", "enum3", "enum4", "enum5", "enum6").save
          eid = tx1.eid

          // Retract value
          _ <- Ns(eid).enums.retract("enum6").update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1", "enum2", "enum3", "enum4", "enum5"))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).enums.retract("enum7").update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1", "enum2", "enum3", "enum4", "enum5"))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).enums.retract("enum5", "enum5").update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1", "enum2", "enum3", "enum4"))

          // Retract multiple values (vararg)
          _ <- Ns(eid).enums.retract("enum3", "enum4").update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1", "enum2"))

          // Retract Seq of values
          _ <- Ns(eid).enums.retract(Seq("enum2")).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1"))

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).enums.retract(Seq[String]()).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1"))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.enums("enum2", "enum3").save
          eid = tx1.eid

          // Apply value (retracts all current values!)
          _ <- Ns(eid).enums("enum1").update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum1"))

          // Apply multiple values (vararg)
          _ <- Ns(eid).enums("enum2", "enum3").update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum2", "enum3"))

          // Apply Seq of values
          _ <- Ns(eid).enums(Set("enum4")).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List("enum4"))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).enums(Set[String]()).update
          _ <- Ns.enums.get === List()


          _ <- Ns(eid).enums(Set("enum1", "enum2")).update

          // Delete all (apply no values)
          _ <- Ns(eid).enums().update
          _ <- Ns.enums.get === List()


          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns(eid).enums("enum1", "enum2", "enum2").update
          _ <- Ns.enums.get.map(_.head ==> Set("enum1", "enum2"))
        } yield ()
      }
    }

    "Card-many variables" - {

      "assert" - core { implicit conn =>
        for {
          tx1 <- Ns.enums(enum1).save
          eid = tx1.eid

          // Assert value
          _ <- Ns(eid).enums.assert(enum2).update
          _ <- Ns.enums.get.map(_.head ==> Set(enum1, enum2))

          // Assert existing value (no effect)
          _ <- Ns(eid).enums.assert(enum2).update
          _ <- Ns.enums.get.map(_.head ==> Set(enum1, enum2))

          // Assert multiple values
          _ <- Ns(eid).enums.assert(enum3, enum4).update
          _ <- Ns.enums.get.map(_.head ==> Set(enum1, enum2, enum3, enum4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).enums.assert(Seq(enum4, enum5)).update
          _ <- Ns.enums.get.map(_.head ==> Set(enum1, enum2, enum3, enum4, enum5))

          // Assert Seq of values as variable (existing values unaffected)
          values = Seq(enum6, enum7)
          _ <- Ns(eid).enums.assert(values).update
          _ <- Ns.enums.get.map(_.head ==> Set(enum1, enum2, enum3, enum4, enum5, enum6, enum7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).enums.assert(Seq[String]()).update
          _ <- Ns.enums.get.map(_.head ==> Set(enum1, enum2, enum3, enum4, enum5, enum6, enum7))


          // Reset
          _ <- Ns(eid).enums().update

          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).enums.assert(enum1, enum2, enum2).update
          _ <- Ns.enums.get.map(_.head ==> Set(enum1, enum2))

          // Equal values are coalesced (at runtime)
          other3 = enum3
          _ <- Ns(eid).enums.assert(enum2, enum3, other3).update
          _ <- Ns.enums.get.map(_.head ==> Set(enum3, enum2, enum1))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx1 <- Ns.enums(enum1, enum2, enum3, enum4, enum5, enum6).save
          eid = tx1.eid

          // Replace value
          _ <- Ns(eid).enums.replace(enum6 -> enum8).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1, enum2, enum3, enum4, enum5, enum8))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).enums.replace(enum5 -> enum8).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1, enum2, enum3, enum4, enum8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).enums.replace(enum3 -> enum6, enum4 -> enum7).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1, enum2, enum6, enum7, enum8))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).enums.replace(Seq(enum2 -> enum5)).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1, enum5, enum6, enum7, enum8))

          // Replace with Seq of oldValue->newValue pairs as variable
          values = Seq(enum1 -> enum4)
          _ <- Ns(eid).enums.replace(values).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum4, enum5, enum6, enum7, enum8))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).enums.replace(Seq[(String, String)]()).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum4, enum5, enum6, enum7, enum8))


          // Can't replace duplicate values

          //      expectCompileError(
          //        """Ns(eid).enums.replace(enum7 -> enum8, enum8 -> enum8).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/enums`:" +
          //          "\n__ident__enum8")
          //
          //      expectCompileError(
          //        """Ns(eid).enums.replace(Seq(enum7 -> enum8, enum8 -> enum8)).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/enums`:" +
          //          "\n__ident__enum8")


          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          other8 = enum8

          //      (Ns(eid).enums.replace(enum7 -> enum8, enum8 -> other8).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/enums`:" +
          //        "\nenum8"
          //
          //      // Conflicting new values
          //      (Ns(eid).enums.replace(Seq(enum7 -> enum8, enum8 -> other8)).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/enums`:" +
          //        "\nenum8"
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx1 <- Ns.enums(enum1, enum2, enum3, enum4, enum5, enum6).save
          eid = tx1.eid

          // Retract value
          _ <- Ns(eid).enums.retract(enum6).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).enums.retract(enum7).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1, enum2, enum3, enum4, enum5))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).enums.retract(enum5, enum5).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1, enum2, enum3, enum4))

          // Retract multiple values (vararg)
          _ <- Ns(eid).enums.retract(enum3, enum4).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1, enum2))

          // Retract Seq of values
          _ <- Ns(eid).enums.retract(Seq(enum2)).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1))

          // Retract Seq of values as variable
          values = Seq(enum1)
          _ <- Ns(eid).enums.retract(values).update
          _ <- Ns.enums.get === List()

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).enums(enum1).update
          _ <- Ns(eid).enums.retract(Seq[String]()).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.enums(enum2, enum3).save
          eid = tx1.eid

          // Apply value (retracts all current values!)
          _ <- Ns(eid).enums(enum1).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).enums(enum2, enum3).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum2, enum3))

          // Apply Seq of values
          _ <- Ns(eid).enums(Set(enum4)).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum4))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).enums(Set[String]()).update
          _ <- Ns.enums.get === List()

          // Apply Seq of values as variable
          values = Set(enum1, enum2)
          _ <- Ns(eid).enums(values).update
          _ <- Ns.enums.get.map(_.head.toList.sorted ==> List(enum1, enum2))

          // Delete all (apply no values)
          _ <- Ns(eid).enums().update
          _ <- Ns.enums.get === List()


          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).enums(enum1, enum2, enum2).update
          _ <- Ns.enums.get.map(_.head ==> Set(enum1, enum2))

          // Equal values are coalesced (at runtime)
          other3 = enum3
          _ <- Ns(eid).enums(enum2, enum3, other3).update
          _ <- Ns.enums.get.map(_.head ==> Set(enum2, enum3))
        } yield ()
      }
    }
  }
}
