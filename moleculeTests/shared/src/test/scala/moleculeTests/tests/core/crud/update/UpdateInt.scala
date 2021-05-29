package moleculeTests.tests.core.crud.update

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object UpdateInt extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one values" - {

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.int(2).save
          eid = tx1.eid

          // Apply value (retracts current value)
          _ <- Ns(eid).int(1).update
          _ <- Ns.int.get.map(_.head ==> 1)

          // Apply new value
          _ <- Ns(eid).int(2).update
          _ <- Ns.int.get.map(_.head ==> 2)

          // Apply empty value (retract `int` datom)
          _ <- Ns(eid).int().update
          _ <- Ns.int.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).int.update.recover { case VerifyModelException(err) =>
            err ==> "[onlyAtomsWithValue]  Update molecule can only have attributes with some value(s) applied/added/replaced etc."
          }

          _ <- Ns(eid).int(2, 3).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... int(2, 3)"
          }
        } yield ()
      }
    }

    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.int(int2).save
          eid = tx1.eid

          // Apply value (retracts current value)
          _ <- Ns(eid).int(int1).update
          _ <- Ns.int.get.map(_.head ==> int1)

          // Apply new value
          _ <- Ns(eid).int(int2).update
          _ <- Ns.int.get.map(_.head ==> int2)

          // Delete value (apply empty value)
          _ <- Ns(eid).int().update
          _ <- Ns.int.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).int(int2, int3).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... int($int2, $int3)"
          }
        } yield ()
      }
    }


    "Card-many values" - {

      "assert" - core { implicit conn =>
        for {
          tx1 <- Ns.ints(1).save
          eid = tx1.eid

          // Assert value
          _ <- Ns(eid).ints.assert(2).update
          _ <- Ns.ints.get.map(_.head ==> Set(1, 2))

          // Assert existing value (no effect)
          _ <- Ns(eid).ints.assert(2).update
          _ <- Ns.ints.get.map(_.head ==> Set(1, 2))

          // Assert multiple values (vararg)
          _ <- Ns(eid).ints.assert(3, 4).update
          _ <- Ns.ints.get.map(_.head ==> Set(1, 2, 3, 4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).ints.assert(Seq(4, 5)).update
          _ <- Ns.ints.get.map(_.head ==> Set(1, 2, 3, 4, 5))

          // Assert Set of values
          _ <- Ns(eid).ints.assert(Set(6)).update
          _ <- Ns.ints.get.map(_.head ==> Set(1, 2, 3, 4, 5, 6))

          // Assert Iterable of values
          _ <- Ns(eid).ints.assert(Iterable(7)).update
          _ <- Ns.ints.get.map(_.head ==> Set(1, 2, 3, 4, 5, 6, 7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).ints.assert(Seq[Int]()).update
          _ <- Ns.ints.get.map(_.head ==> Set(1, 2, 3, 4, 5, 6, 7))


          // Reset
          _ <- Ns(eid).ints().update

          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns(eid).ints.assert(1, 2, 2).update
          _ <- Ns.ints.get.map(_.head ==> Set(1, 2))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx1 <- Ns.ints(1, 2, 3, 4, 5, 6).save
          eid = tx1.eid

          // Replace value
          _ <- Ns(eid).ints.replace(6 -> 8).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1, 2, 3, 4, 5, 8))

          // Replacing value to existing value simply retracts it
          _ <- Ns(eid).ints.replace(5 -> 8).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1, 2, 3, 4, 8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).ints.replace(3 -> 6, 4 -> 7).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1, 2, 6, 7, 8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).ints.replace(42 -> 9).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1, 2, 6, 7, 8, 9))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).ints.replace(Seq(2 -> 5)).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1, 5, 6, 7, 8, 9))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).ints.replace(Seq[(Int, Int)]()).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1, 5, 6, 7, 8, 9))


          // Can't replace duplicate values

          _ = compileError(            """Ns(eid).ints.replace(7 -> 8, 8 -> 8).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/ints`:" +
              "\n8")

          _ = compileError(            """Ns(eid).ints.replace(Seq(7 -> 8, 8 -> 8)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/ints`:" +
              "\n8")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx1 <- Ns.ints(1, 2, 3, 4, 5, 6).save
          eid = tx1.eid

          // Retract value
          _ <- Ns(eid).ints.retract(6).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1, 2, 3, 4, 5))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).ints.retract(7).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1, 2, 3, 4, 5))

          // Retracting duplicate values removes the distinct value
          _ <- Ns(eid).ints.retract(5, 5).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1, 2, 3, 4))

          // Retract multiple values (vararg)
          _ <- Ns(eid).ints.retract(3, 4).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1, 2))

          // Retract Seq of values
          _ <- Ns(eid).ints.retract(Seq(2)).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1))

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).ints.retract(Seq[Int]()).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.ints(2, 3).save
          eid = tx1.eid

          // Apply value (retracts all current values!)
          _ <- Ns(eid).ints(1).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).ints(2, 3).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(2, 3))

          // Apply Seq of values
          _ <- Ns(eid).ints(Set(4)).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(4))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).ints(Seq[Int]()).update
          _ <- Ns.ints.get.map(_ ==> List())


          _ <- Ns(eid).ints(Set(1, 2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).ints().update
          _ <- Ns.ints.get.map(_ ==> List())


          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns(eid).ints(1, 2, 2).update
          _ <- Ns.ints.get.map(_.head ==> Set(1, 2))
        } yield ()
      }
    }


    "Card-many variables" - {

      "assert" - core { implicit conn =>
        for {
          tx1 <- Ns.ints(int1).save
          eid = tx1.eid

          // Assert value
          _ <- Ns(eid).ints.assert(int2).update
          _ <- Ns.ints.get.map(_.head ==> Set(int1, int2))

          // Assert existing value (no effect)
          _ <- Ns(eid).ints.assert(int2).update
          _ <- Ns.ints.get.map(_.head ==> Set(int1, int2))

          // Assert multiple values
          _ <- Ns(eid).ints.assert(int3, int4).update
          _ <- Ns.ints.get.map(_.head ==> Set(int1, int2, int3, int4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).ints.assert(Seq(int4, int5)).update
          _ <- Ns.ints.get.map(_.head ==> Set(int1, int2, int3, int4, int5))

          // Assert Seq of values as variable (existing values unaffected)
          values = Seq(int6, int7)
          _ <- Ns(eid).ints.assert(values).update
          _ <- Ns.ints.get.map(_.head ==> Set(int1, int2, int3, int4, int5, int6, int7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).ints.assert(Seq[Int]()).update
          _ <- Ns.ints.get.map(_.head ==> Set(int1, int2, int3, int4, int5, int6, int7))


          // Reset
          _ <- Ns(eid).ints().update

          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).ints.assert(int1, int2, int2).update
          _ <- Ns.ints.get.map(_.head ==> Set(int1, int2))

          // Equal values are coalesced (at runtime)
          other3 = int3
          _ <- Ns(eid).ints.assert(int2, int3, other3).update
          _ <- Ns.ints.get.map(_.head ==> Set(int3, int2, int1))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx1 <- Ns.ints(int1, int2, int3, int4, int5, int6).save
          eid = tx1.eid

          // Replace value
          _ <- Ns(eid).ints.replace(int6 -> int8).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1, int2, int3, int4, int5, int8))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).ints.replace(int5 -> int8).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1, int2, int3, int4, int8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).ints.replace(int3 -> int6, int4 -> int7).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1, int2, int6, int7, int8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).ints.replace(42 -> int9).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1, int2, int6, int7, int8, int9))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).ints.replace(Seq(int2 -> int5)).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1, int5, int6, int7, int8, int9))

          // Replace with Seq of oldValue->newValue pairs as variable
          values = Seq(int1 -> int4)
          _ <- Ns(eid).ints.replace(values).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int4, int5, int6, int7, int8, int9))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).ints.replace(Seq[(Int, Int)]()).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int4, int5, int6, int7, int8, int9))


          // Can't replace duplicate values

          _ = compileError(            """Ns(eid).ints.replace(int7 -> int8, int8 -> int8).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/ints`:" +
              "\n__ident__int8")

          _ = compileError(            """Ns(eid).ints.replace(Seq(int7 -> int8, int8 -> int8)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/ints`:" +
              "\n__ident__int8")


          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          other8 = 8

          _ <- Ns(eid).ints.replace(int7 -> int8, int8 -> other8).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/ints`:" +
                "\n8"
          }

          // Conflicting new values
          _ <- Ns(eid).ints.replace(Seq(int7 -> int8, int8 -> other8)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/ints`:" +
                "\n8"
          }
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx1 <- Ns.ints(int1, int2, int3, int4, int5, int6).save
          eid = tx1.eid

          // Retract value
          _ <- Ns(eid).ints.retract(int6).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1, int2, int3, int4, int5))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).ints.retract(int7).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1, int2, int3, int4, int5))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).ints.retract(int5, int5).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1, int2, int3, int4))

          // Retract multiple values (vararg)
          _ <- Ns(eid).ints.retract(int3, int4).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1, int2))

          // Retract Seq of values
          _ <- Ns(eid).ints.retract(Seq(int2)).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1))

          // Retract Seq of values as variable
          values = Seq(int1)
          _ <- Ns(eid).ints.retract(values).update
          _ <- Ns.ints.get.map(_ ==> List())

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).ints(int1).update
          _ <- Ns(eid).ints.retract(Seq[Int]()).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.ints(int2, int3).save
          eid = tx1.eid

          // Apply value (retracts all current values!)
          _ <- Ns(eid).ints(int1).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).ints(int2, int3).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int2, int3))

          // Apply Seq of values
          _ <- Ns(eid).ints(Set(int4)).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int4))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).ints(Set[Int]()).update
          _ <- Ns.ints.get.map(_ ==> List())

          // Apply Seq of values as variable
          values = Set(int1, int2)
          _ <- Ns(eid).ints(values).update
          _ <- Ns.ints.get.map(_.head.toList.sorted ==> List(int1, int2))

          // Delete all (apply no values)
          _ <- Ns(eid).ints().update
          _ <- Ns.ints.get.map(_ ==> List())


          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).ints(int1, int2, int2).update
          _ <- Ns.ints.get.map(_.head ==> Set(int1, int2))

          // Equal values are coalesced (at runtime)
          other3 = int3
          _ <- Ns(eid).ints(int2, int3, other3).update
          _ <- Ns.ints.get.map(_.head ==> Set(int2, int3))
        } yield ()
      }
    }
  }
}
