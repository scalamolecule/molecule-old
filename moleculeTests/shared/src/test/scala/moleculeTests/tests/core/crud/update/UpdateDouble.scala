package moleculeTests.tests.core.crud.update

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateDouble extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one values" - {

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.double(2.0).save
          eid = tx1.eid

          // Apply value (retracts current value)
          _ <- Ns(eid).double(1.0).update
          _ <- Ns.double.get.map(_.head ==> 1.0)

          // Apply new value
          _ <- Ns(eid).double(2.0).update
          _ <- Ns.double.get.map(_.head ==> 2.0)

          // Delete value (apply no value)
          _ <- Ns(eid).double().update
          _ <- Ns.double.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).double(2.0, 3.0).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... double(2.0, 3.0)"
          }
        } yield ()
      }
    }


    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.double(double2).save
          eid = tx1.eid

          // Apply value (retracts current value)
          _ <- Ns(eid).double(double1).update
          _ <- Ns.double.get.map(_.head ==> double1)

          // Apply new value
          _ <- Ns(eid).double(double2).update
          _ <- Ns.double.get.map(_.head ==> double2)

          // Delete value (apply no value)
          _ <- Ns(eid).double().update
          _ <- Ns.double.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not alloweddouble(2.0, 3.0)

          _ <- Ns(eid).double(double2, double3).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... double($double2, $double3)"
          }
        } yield ()
      }
    }


    "Card-many values" - {

      "assert" - core { implicit conn =>
        for {
          tx1 <- Ns.doubles(1.0).save
          eid = tx1.eid

          // Assert value
          _ <- Ns(eid).doubles.assert(2.0).update
          _ <- Ns.doubles.get.map(_.head ==> Set(1.0, 2.0))

          // Assert existing value (no effect)
          _ <- Ns(eid).doubles.assert(2.0).update
          _ <- Ns.doubles.get.map(_.head ==> Set(1.0, 2.0))

          // Assert multiple values (vararg)
          _ <- Ns(eid).doubles.assert(3.0, 4.0).update
          _ <- Ns.doubles.get.map(_.head ==> Set(1.0, 2.0, 3.0, 4.0))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).doubles.assert(Seq(4.0, 5.0)).update
          _ <- Ns.doubles.get.map(_.head ==> Set(1.0, 2.0, 3.0, 4.0, 5.0))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).doubles.assert(Seq[Double]()).update
          _ <- Ns.doubles.get.map(_.head ==> Set(1.0, 2.0, 3.0, 4.0, 5.0))


          // Reset
          _ <- Ns(eid).doubles().update

          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns(eid).doubles.assert(1.0, 2.0, 2.0).update
          _ <- Ns.doubles.get.map(_.head ==> Set(1.0, 2.0))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx1 <- Ns.doubles(1.0, 2.0, 3.0, 4.0, 5.0, 6.0).save
          eid = tx1.eid

          // Replace value
          _ <- Ns(eid).doubles.replace(6.0 -> 8.0).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0, 2.0, 3.0, 4.0, 5.0, 8.0))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).doubles.replace(5.0 -> 8.0).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0, 2.0, 3.0, 4.0, 8.0))

          // Replace multiple values (vararg)
          _ <- Ns(eid).doubles.replace(3.0 -> 6.0, 4.0 -> 7.0).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0, 2.0, 6.0, 7.0, 8.0))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).doubles.replace(42.0 -> 9.0).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0, 2.0, 6.0, 7.0, 8.0, 9.0))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).doubles.replace(Seq(2.0 -> 5.0)).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0, 5.0, 6.0, 7.0, 8.0, 9.0))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).doubles.replace(Seq[(Double, Double)]()).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0, 5.0, 6.0, 7.0, 8.0, 9.0))


          // Can't replace duplicate values

          _ = compileError(            """Ns(eid).doubles.replace(7.0 -> 8.0, 8.0 -> 8.0).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/doubles`:" +
              "\n8.0")

          _ = compileError(            """Ns(eid).doubles.replace(Seq(7.0 -> 8.0, 8.0 -> 8.0)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/doubles`:" +
              "\n8.0")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx1 <- Ns.doubles(1.0, 2.0, 3.0, 4.0, 5.0, 6.0).save
          eid = tx1.eid

          // Retract value
          _ <- Ns(eid).doubles.retract(6.0).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0, 2.0, 3.0, 4.0, 5.0))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).doubles.retract(7.0).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0, 2.0, 3.0, 4.0, 5.0))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).doubles.retract(5.0, 5.0).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0, 2.0, 3.0, 4.0))

          // Retract multiple values (vararg)
          _ <- Ns(eid).doubles.retract(3.0, 4.0).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0, 2.0))

          // Retract Seq of values
          _ <- Ns(eid).doubles.retract(Seq(2.0)).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0))

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).doubles.retract(Seq[Double]()).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.doubles(2.0, 3.0).save
          eid = tx1.eid

          // Apply value (retracts all current values!)
          _ <- Ns(eid).doubles(1.0).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(1.0))

          // Apply multiple values (vararg)
          _ <- Ns(eid).doubles(2.0, 3.0).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(2.0, 3.0))

          // Apply Seq of values
          _ <- Ns(eid).doubles(Set(4.0)).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(4.0))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).doubles(Set[Double]()).update
          _ <- Ns.doubles.get.map(_ ==> List())


          _ <- Ns(eid).doubles(Set(1.0, 2.0)).update

          // Delete all (apply no values)
          _ <- Ns(eid).doubles().update
          _ <- Ns.doubles.get.map(_ ==> List())


          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns(eid).doubles(1.0, 2.0, 2.0).update
          _ <- Ns.doubles.get.map(_.head ==> Set(1.0, 2.0))
        } yield ()
      }
    }


    "Card-many variables" - {

      "assert" - core { implicit conn =>
        for {
          tx1 <- Ns.doubles(double1).save
          eid = tx1.eid

          // Assert value
          _ <- Ns(eid).doubles.assert(double2).update
          _ <- Ns.doubles.get.map(_.head ==> Set(double1, double2))

          // Assert existing value (no effect)
          _ <- Ns(eid).doubles.assert(double2).update
          _ <- Ns.doubles.get.map(_.head ==> Set(double1, double2))

          // Assert multiple values
          _ <- Ns(eid).doubles.assert(double3, double4).update
          _ <- Ns.doubles.get.map(_.head ==> Set(double1, double2, double3, double4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).doubles.assert(Seq(double4, double5)).update
          _ <- Ns.doubles.get.map(_.head ==> Set(double1, double2, double3, double4, double5))

          // Assert Seq of values as variable (existing values unaffected)
          values = Seq(double6, double7)
          _ <- Ns(eid).doubles.assert(values).update
          _ <- Ns.doubles.get.map(_.head ==> Set(double1, double2, double3, double4, double5, double6, double7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).doubles.assert(Seq[Double]()).update
          _ <- Ns.doubles.get.map(_.head ==> Set(double1, double2, double3, double4, double5, double6, double7))


          // Reset
          _ <- Ns(eid).doubles().update

          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).doubles.assert(double1, double2, double2).update
          _ <- Ns.doubles.get.map(_.head ==> Set(double1, double2))

          // Equal values are coalesced (at runtime)
          other3 = double3
          _ <- Ns(eid).doubles.assert(double2, double3, other3).update
          _ <- Ns.doubles.get.map(_.head ==> Set(double3, double2, double1))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx1 <- Ns.doubles(double1, double2, double3, double4, double5, double6).save
          eid = tx1.eid

          // Replace value
          _ <- Ns(eid).doubles.replace(double6 -> double8).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1, double2, double3, double4, double5, double8))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).doubles.replace(double5 -> double8).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1, double2, double3, double4, double8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).doubles.replace(double3 -> double6, double4 -> double7).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1, double2, double6, double7, double8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).doubles.replace(42.0 -> double9).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1, double2, double6, double7, double8, double9))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).doubles.replace(Seq(double2 -> double5)).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1, double5, double6, double7, double8, double9))

          // Replace with Seq of oldValue->newValue pairs as variable
          values = Seq(double1 -> double4)
          _ <- Ns(eid).doubles.replace(values).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double4, double5, double6, double7, double8, double9))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).doubles.replace(Seq[(Double, Double)]()).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double4, double5, double6, double7, double8, double9))


          // Can't replace duplicate values

          _ = compileError(            """Ns(eid).doubles.replace(double7 -> double8, double8 -> double8).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/doubles`:" +
              "\n__ident__double8")

          _ = compileError(            """Ns(eid).doubles.replace(Seq(double7 -> double8, double8 -> double8)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/doubles`:" +
              "\n__ident__double8")


          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          other8 = 8.0

          _ <- Ns(eid).doubles.replace(double7 -> double8, double8 -> other8).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/doubles`:" +
                "\n8.0"
          }

          // Conflicting new values
          _ <- Ns(eid).doubles.replace(Seq(double7 -> double8, double8 -> other8)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/doubles`:" +
                "\n8.0"
          }
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx1 <- Ns.doubles(double1, double2, double3, double4, double5, double6).save
          eid = tx1.eid

          // Retract value
          _ <- Ns(eid).doubles.retract(double6).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1, double2, double3, double4, double5))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).doubles.retract(double7).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1, double2, double3, double4, double5))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).doubles.retract(double5, double5).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1, double2, double3, double4))

          // Retract multiple values (vararg)
          _ <- Ns(eid).doubles.retract(double3, double4).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1, double2))

          // Retract Seq of values
          _ <- Ns(eid).doubles.retract(Seq(double2)).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1))

          // Retract Seq of values as variable
          values = Seq(double1)
          _ <- Ns(eid).doubles.retract(values).update
          _ <- Ns.doubles.get.map(_ ==> List())

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).doubles(double1).update
          _ <- Ns(eid).doubles.retract(Seq[Double]()).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.doubles(double2, double3).save
          eid = tx1.eid

          // Apply value (retracts all current values!)
          _ <- Ns(eid).doubles(double1).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).doubles(double2, double3).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double2, double3))

          // Apply Seq of values
          _ <- Ns(eid).doubles(Set(double4)).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double4))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).doubles(Set[Double]()).update
          _ <- Ns.doubles.get.map(_ ==> List())

          // Apply Seq of values as variable
          values = Set(double1, double2)
          _ <- Ns(eid).doubles(values).update
          _ <- Ns.doubles.get.map(_.head.toList.sorted ==> List(double1, double2))

          // Delete all (apply no values)
          _ <- Ns(eid).doubles().update
          _ <- Ns.doubles.get.map(_ ==> List())


          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).doubles(double1, double2, double2).update
          _ <- Ns.doubles.get.map(_.head ==> Set(double1, double2))

          // Equal values are coalesced (at runtime)
          other3 = double3
          _ <- Ns(eid).doubles(double2, double3, other3).update
          _ <- Ns.doubles.get.map(_.head ==> Set(double2, double3))
        } yield ()
      }
    }
  }
}
