package moleculeTests.tests.core.crud.update

import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateBigInt extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.bigInt(bigInt2).save.map(_.eid)

          // Apply value (retracts current value)
          _ <- Ns(eid).bigInt(bigInt1).update
          _ <- Ns.bigInt.get.map(_.head ==> bigInt1)

          // Apply new value
          _ <- Ns(eid).bigInt(bigInt2).update
          _ <- Ns.bigInt.get.map(_.head ==> bigInt2)

          // Delete value (apply no value)
          _ <- Ns(eid).bigInt().update
          _ <- Ns.bigInt.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).bigInt(bigInt2, bigInt3).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... bigInt($bigInt2, $bigInt3)"
          }
        } yield ()
      }
    }

    "Card-many variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.bigInts(bigInt1).save.map(_.eid)

          // Assert value
          _ <- Ns(eid).bigInts.assert(bigInt2).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt1, bigInt2))

          // Assert existing value (no effect)
          _ <- Ns(eid).bigInts.assert(bigInt2).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt1, bigInt2))

          // Assert multiple values
          _ <- Ns(eid).bigInts.assert(bigInt3, bigInt4).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt1, bigInt2, bigInt3, bigInt4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).bigInts.assert(Seq(bigInt4, bigInt5)).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

          // Assert Seq of values as variable (existing values unaffected)
          values = Seq(bigInt6, bigInt7)
          _ <- Ns(eid).bigInts.assert(values).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6, bigInt7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).bigInts.assert(Seq[BigInt]()).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6, bigInt7))


          // Reset
          _ <- Ns(eid).bigInts().update

          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).bigInts.assert(bigInt1, bigInt2, bigInt2).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt1, bigInt2))

          // Equal values are coalesced (at runtime)
          other3 = bigInt3
          _ <- Ns(eid).bigInts.assert(bigInt2, bigInt3, other3).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt3, bigInt2, bigInt1))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.bigInts(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6).save.map(_.eid)

          // Replace value
          - <- Ns(eid).bigInts.replace(bigInt6 -> bigInt8).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt8))

          // Replace value to existing value simply retracts it
          - <- Ns(eid).bigInts.replace(bigInt5 -> bigInt8).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt8))

          // Replace multiple values (vararg)
          - <- Ns(eid).bigInts.replace(bigInt3 -> bigInt6, bigInt4 -> bigInt7).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1, bigInt2, bigInt6, bigInt7, bigInt8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          bigInt42 = BigInt(42)
          - <- Ns(eid).bigInts.replace(bigInt42 -> bigInt9).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1, bigInt2, bigInt6, bigInt7, bigInt8, bigInt9))

          // Replace with Seq of oldValue->newValue pairs
          - <- Ns(eid).bigInts.replace(Seq(bigInt2 -> bigInt5)).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1, bigInt5, bigInt6, bigInt7, bigInt8, bigInt9))

          // Replace with Seq of oldValue->newValue pairs as variable
          values = Seq(bigInt1 -> bigInt4)
          - <- Ns(eid).bigInts.replace(values).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt4, bigInt5, bigInt6, bigInt7, bigInt8, bigInt9))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          - <- Ns(eid).bigInts.replace(Seq[(BigInt, BigInt)]()).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt4, bigInt5, bigInt6, bigInt7, bigInt8, bigInt9))


          // Can't replace duplicate values

          _ = expectCompileError("""Ns(eid).bigInts.replace(bigInt7 -> bigInt8, bigInt8 -> bigInt8).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/bigInts`:" +
              "\n__ident__bigInt8")

          _ = expectCompileError("""Ns(eid).bigInts.replace(Seq(bigInt7 -> bigInt8, bigInt8 -> bigInt8)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/bigInts`:" +
              "\n__ident__bigInt8")


          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          other8 = bigInt8

          _ <- Ns(eid).bigInts.replace(bigInt7 -> bigInt8, bigInt8 -> other8).update.recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/bigInts`:" +
              "\n8"
          }

          // Conflicting new values
          _ <- Ns(eid).bigInts.replace(Seq(bigInt7 -> bigInt8, bigInt8 -> other8)).update.recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/bigInts`:" +
              "\n8"
          }
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.bigInts(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5, bigInt6).save.map(_.eid)

          // Retract value
          _ <- Ns(eid).bigInts.retract(bigInt6).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).bigInts.retract(bigInt7).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4, bigInt5))

          // Retracting duplicate values removes the distinct value
          _ <- Ns(eid).bigInts.retract(bigInt5, bigInt5).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1, bigInt2, bigInt3, bigInt4))

          // Retract multiple values (vararg)
          _ <- Ns(eid).bigInts.retract(bigInt3, bigInt4).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1, bigInt2))

          // Retract Seq of values
          _ <- Ns(eid).bigInts.retract(Seq(bigInt2)).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1))

          // Retract Seq of values as variable
          values = Seq(bigInt1)
          _ <- Ns(eid).bigInts.retract(values).update
          _ <- Ns.bigInts.get.map(_ ==> List())

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).bigInts(bigInt1).update
          _ <- Ns(eid).bigInts.retract(Seq[BigInt]()).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.bigInts(bigInt2, bigInt3).save.map(_.eid)

          // Apply value (retracts all current values!)
          _ <- Ns(eid).bigInts(bigInt1).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).bigInts(bigInt2, bigInt3).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt2, bigInt3))

          // Apply Seq of values
          _ <- Ns(eid).bigInts(Set(bigInt4)).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt4))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).bigInts(Set[BigInt]()).update
          _ <- Ns.bigInts.get.map(_ ==> List())

          // Apply Seq of values as variable
          values = Set(bigInt1, bigInt2)
          _ <- Ns(eid).bigInts(values).update
          _ <- Ns.bigInts.get.map(_.head.toList.sorted ==> List(bigInt1, bigInt2))

          // Delete all (apply no values)
          _ <- Ns(eid).bigInts().update
          _ <- Ns.bigInts.get.map(_ ==> List())


          _ <- Ns(eid).bigInts(bigInt1, bigInt2, bigInt2).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt1, bigInt2))

          other3 = bigInt3
          _ <- Ns(eid).bigInts(bigInt2, bigInt3, other3).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt2, bigInt3))


          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).bigInts(bigInt1, bigInt2, bigInt2).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt1, bigInt2))

          // Equal values are coalesced (at runtime)
          _ <- Ns(eid).bigInts(bigInt2, bigInt3, other3).update
          _ <- Ns.bigInts.get.map(_.head ==> Set(bigInt2, bigInt3))
        } yield ()
      }
    }
  }
}
