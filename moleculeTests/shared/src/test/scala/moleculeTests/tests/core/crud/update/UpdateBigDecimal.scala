package moleculeTests.tests.core.crud.update

import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateBigDecimal extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.bigDec(bigDec2).save.map(_.eid)

          // Apply value (retracts current value)
          _ <- Ns(eid).bigDec(bigDec1).update
          _ <- Ns.bigDec.get.map(_.head ==> bigDec1)

          // Apply new value
          _ <- Ns(eid).bigDec(bigDec2).update
          _ <- Ns.bigDec.get.map(_.head ==> bigDec2)

          // Delete value (apply no value)
          _ <- Ns(eid).bigDec().update
          _ <- Ns.bigDec.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).bigDec(bigDec2, bigDec3).update
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... bigDec(2.0, 3.0)"
          }
        } yield ()
      }
    }

    "Card-many variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.bigDecs(bigDec1).save.map(_.eid)

          // Assert value
          _ <- Ns(eid).bigDecs.assert(bigDec2).update
          _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec2, bigDec1))

          // Assert existing value (no effect)
          _ <- Ns(eid).bigDecs.assert(bigDec2).update
          _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec1, bigDec2))

          // Assert multiple values
          _ <- Ns(eid).bigDecs.assert(bigDec3, bigDec4).update
          _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec1, bigDec2, bigDec3, bigDec4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).bigDecs.assert(Seq(bigDec4, bigDec5)).update
          _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

          // Assert Seq of values as variable (existing values unaffected)
          values = Seq(bigDec6, bigDec7)
          _ <- Ns(eid).bigDecs.assert(values).update
          _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6, bigDec7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).bigDecs.assert(Seq[BigDecimal]()).update
          _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6, bigDec7))


          // Reset
          _ <- Ns(eid).bigDecs().update

          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).bigDecs.assert(bigDec1, bigDec2, bigDec2).update
          _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec1, bigDec2))

          // Equal values are coalesced (at runtime)
          other3 = bigDec3
          _ <- Ns(eid).bigDecs.assert(bigDec2, bigDec3, other3).update
          _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec3, bigDec2, bigDec1))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.bigDecs(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).bigDecs.replace(bigDec6 -> bigDec8).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec8))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).bigDecs.replace(bigDec5 -> bigDec8).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).bigDecs.replace(bigDec3 -> bigDec6, bigDec4 -> bigDec7).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1, bigDec2, bigDec6, bigDec7, bigDec8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          bigDec42 = BigDecimal(42)
          _ <- Ns(eid).bigDecs.replace(bigDec42 -> bigDec9).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1, bigDec2, bigDec6, bigDec7, bigDec8, bigDec9))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).bigDecs.replace(Seq(bigDec2 -> bigDec5)).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1, bigDec5, bigDec6, bigDec7, bigDec8, bigDec9))

          // Replace with Seq of oldValue->newValue pairs as variable
          values = Seq(bigDec1 -> bigDec4)
          _ <- Ns(eid).bigDecs.replace(values).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec4, bigDec5, bigDec6, bigDec7, bigDec8, bigDec9))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).bigDecs.replace(Seq[(BigDecimal, BigDecimal)]()).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec4, bigDec5, bigDec6, bigDec7, bigDec8, bigDec9))


          // Can't replace duplicate values

          _ = expectCompileError("""Ns(eid).bigDecs.replace(bigDec7 -> bigDec8, bigDec8 -> bigDec8).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/bigDecs`:" +
              "\n__ident__bigDec8")

          _ = expectCompileError("""Ns(eid).bigDecs.replace(Seq(bigDec7 -> bigDec8, bigDec8 -> bigDec8)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/bigDecs`:" +
              "\n__ident__bigDec8")

          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          other8 = bigDec8

          _ <- Ns(eid).bigDecs.replace(bigDec7 -> bigDec8, bigDec8 -> other8).update
            .map(_ ==> "Unexpected success").recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/bigDecs`:" +
              "\n8.0"
          }

          // Conflicting new values
          _ <- Ns(eid).bigDecs.replace(Seq(bigDec7 -> bigDec8, bigDec8 -> other8)).update
            .map(_ ==> "Unexpected success").recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/bigDecs`:" +
              "\n8.0"
          }
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.bigDecs(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5, bigDec6).save.map(_.eid)

          // Retract value
          _ <- Ns(eid).bigDecs.retract(bigDec6).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).bigDecs.retract(bigDec7).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4, bigDec5))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).bigDecs.retract(bigDec5, bigDec5).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1, bigDec2, bigDec3, bigDec4))

          // Retract multiple values (vararg)
          _ <- Ns(eid).bigDecs.retract(bigDec3, bigDec4).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1, bigDec2))

          // Retract Seq of values
          _ <- Ns(eid).bigDecs.retract(Seq(bigDec2)).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1))

          // Retract Seq of values as variable
          values = Seq(bigDec1)
          _ <- Ns(eid).bigDecs.retract(values).update
          _ <- Ns.bigDecs.get.map(_ ==> List())

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).bigDecs(bigDec1).update
          _ <- Ns(eid).bigDecs.retract(Seq[BigDecimal]()).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.bigDecs(bigDec2, bigDec3).save.map(_.eid)

          // Apply value (retracts all current values!)
          _ <- Ns(eid).bigDecs(bigDec1).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).bigDecs(bigDec2, bigDec3).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec2, bigDec3))

          // Apply Seq of values
          _ <- Ns(eid).bigDecs(Set(bigDec4)).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec4))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).bigDecs(Set[BigDecimal]()).update
          _ <- Ns.bigDecs.get.map(_ ==> List())

          // Apply Seq of values as variable
          values = Set(bigDec1, bigDec2)
          _ <- Ns(eid).bigDecs(values).update
          _ <- Ns.bigDecs.get.map(_.head.toList.sorted ==> List(bigDec1, bigDec2))

          // Delete all (apply no values)
          _ <- Ns(eid).bigDecs().update
          _ <- Ns.bigDecs.get.map(_ ==> List())


          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).bigDecs(bigDec1, bigDec2, bigDec2).update
          _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec1, bigDec2))

          // Equal values are coalesced (at runtime)
          other3 = bigDec3
          _ <- Ns(eid).bigDecs(bigDec2, bigDec3, other3).update
          _ <- Ns.bigDecs.get.map(_.head ==> Set(bigDec2, bigDec3))
        } yield ()
      }
    }
  }
}
