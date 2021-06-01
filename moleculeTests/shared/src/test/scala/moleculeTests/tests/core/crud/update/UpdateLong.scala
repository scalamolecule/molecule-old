package moleculeTests.tests.core.crud.update

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateLong extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one values" - {

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.long(2L).save.map(_.eid)

          // Apply value (retracts current value)
          _ <- Ns(eid).long(1L).update
          _ <- Ns.long.get.map(_.head ==> 1L)

          // Apply new value
          _ <- Ns(eid).long(2L).update
          _ <- Ns.long.get.map(_.head ==> 2L)

          // Delete value (apply no value)
          _ <- Ns(eid).long().update
          _ <- Ns.long.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).long(2L, 3L).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... long(2, 3)"
          }
        } yield ()
      }
    }


    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.long(long2).save.map(_.eid)

          // Apply value (retracts current value)
          _ <- Ns(eid).long(long1).update
          _ <- Ns.long.get.map(_.head ==> long1)

          // Apply new value
          _ <- Ns(eid).long(long2).update
          _ <- Ns.long.get.map(_.head ==> long2)

          // Delete value (apply no value)
          _ <- Ns(eid).long().update
          _ <- Ns.long.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).long(long2, long3).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... long($long2, $long3)"
          }
        } yield ()
      }
    }


    "Card-many values" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.longs(1L).save.map(_.eid)

          // Assert value
          _ <- Ns(eid).longs.assert(2L).update
          _ <- Ns.longs.get.map(_.head ==> Set(1L, 2L))

          // Assert existing value (no effect)
          _ <- Ns(eid).longs.assert(2L).update
          _ <- Ns.longs.get.map(_.head ==> Set(1L, 2L))

          // Assert multiple values (vararg)
          _ <- Ns(eid).longs.assert(3L, 4L).update
          _ <- Ns.longs.get.map(_.head ==> Set(1L, 2L, 3L, 4L))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).longs.assert(Seq(4L, 5L)).update
          _ <- Ns.longs.get.map(_.head ==> Set(1L, 2L, 3L, 4L, 5L))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).longs.assert(Seq[Long]()).update
          _ <- Ns.longs.get.map(_.head ==> Set(1L, 2L, 3L, 4L, 5L))


          // Reset
          _ <- Ns(eid).longs().update

          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns(eid).longs.assert(1L, 2L, 2L).update
          _ <- Ns.longs.get.map(_.head ==> Set(1L, 2L))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.longs(1L, 2L, 3L, 4L, 5L, 6L).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).longs.replace(6L -> 8L).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L, 2L, 3L, 4L, 5L, 8L))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).longs.replace(5L -> 8L).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L, 2L, 3L, 4L, 8L))

          // Replace multiple values (vararg)
          _ <- Ns(eid).longs.replace(3L -> 6L, 4L -> 7L).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L, 2L, 6L, 7L, 8L))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).longs.replace(42L -> 9L).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L, 2L, 6L, 7L, 8L, 9L))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).longs.replace(Seq(2L -> 5L)).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L, 5L, 6L, 7L, 8L, 9L))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).longs.replace(Seq[(Long, Long)]()).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L, 5L, 6L, 7L, 8L, 9L))


          // Can't replace duplicate values

          _ = compileError(            """Ns(eid).longs.replace(7L -> 8L, 8L -> 8L).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/longs`:" +
              "\n8")

          _ = compileError(            """Ns(eid).longs.replace(Seq(7L -> 8L, 8L -> 8L)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/longs`:" +
              "\n8")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.longs(1L, 2L, 3L, 4L, 5L, 6L).save.map(_.eid)

          // Retract value
          _ <- Ns(eid).longs.retract(6L).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L, 2L, 3L, 4L, 5L))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).longs.retract(7L).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L, 2L, 3L, 4L, 5L))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).longs.retract(5L, 5L).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L, 2L, 3L, 4L))

          // Retract multiple values (vararg)
          _ <- Ns(eid).longs.retract(3L, 4L).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L, 2L))

          // Retract Seq of values
          _ <- Ns(eid).longs.retract(Seq(2L)).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L))

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).longs.retract(Seq[Long]()).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.longs(2L, 3L).save.map(_.eid)

          // Apply value (retracts all current values!)
          _ <- Ns(eid).longs(1L).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(1L))

          // Apply multiple values (vararg)
          _ <- Ns(eid).longs(2L, 3L).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(2L, 3L))

          // Apply Seq of values
          _ <- Ns(eid).longs(Set(4L)).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(4L))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).longs(Set[Long]()).update
          _ <- Ns.longs.get.map(_ ==> List())

          _ <- Ns(eid).longs(Set(1L, 2L)).update

          // Delete all (apply no values)
          _ <- Ns(eid).longs().update
          _ <- Ns.longs.get.map(_ ==> List())


          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns(eid).longs(1L, 2L, 2L).update
          _ <- Ns.longs.get.map(_.head ==> Set(1L, 2L))
        } yield ()
      }
    }


    "Card-many variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.longs(long1).save.map(_.eid)

          // Assert value
          _ <- Ns(eid).longs.assert(long2).update
          _ <- Ns.longs.get.map(_.head ==> Set(long1, long2))

          // Assert existing value (no effect)
          _ <- Ns(eid).longs.assert(long2).update
          _ <- Ns.longs.get.map(_.head ==> Set(long1, long2))

          // Assert multiple values
          _ <- Ns(eid).longs.assert(long3, long4).update
          _ <- Ns.longs.get.map(_.head ==> Set(long1, long2, long3, long4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).longs.assert(Seq(long4, long5)).update
          _ <- Ns.longs.get.map(_.head ==> Set(long1, long2, long3, long4, long5))

          // Assert Seq of values as variable (existing values unaffected)
          values = Seq(long6, long7)
          _ <- Ns(eid).longs.assert(values).update
          _ <- Ns.longs.get.map(_.head ==> Set(long1, long2, long3, long4, long5, long6, long7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).longs.assert(Seq[Long]()).update
          _ <- Ns.longs.get.map(_.head ==> Set(long1, long2, long3, long4, long5, long6, long7))


          // Reset
          _ <- Ns(eid).longs().update

          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).longs.assert(long1, long2, long2).update
          _ <- Ns.longs.get.map(_.head ==> Set(long1, long2))

          // Equal values are coalesced (at runtime)
          other3 = long3
          _ <- Ns(eid).longs.assert(long2, long3, other3).update
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.longs(long1, long2, long3, long4, long5, long6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).longs.replace(long6 -> long8).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1, long2, long3, long4, long5, long8))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).longs.replace(long5 -> long8).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1, long2, long3, long4, long8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).longs.replace(long3 -> long6, long4 -> long7).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1, long2, long6, long7, long8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).longs.replace(42L -> long9).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1, long2, long6, long7, long8, long9))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).longs.replace(Seq(long2 -> long5)).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1, long5, long6, long7, long8, long9))

          // Replace with Seq of oldValue->newValue pairs as variable
          values = Seq(long1 -> long4)
          _ <- Ns(eid).longs.replace(values).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long4, long5, long6, long7, long8, long9))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).longs.replace(Seq[(Long, Long)]()).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long4, long5, long6, long7, long8, long9))


          // Can't replace duplicate values

          _ = compileError(            """Ns(eid).longs.replace(long7 -> long8, long8 -> long8).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/longs`:" +
              "\n__ident__long8")

          _ = compileError(            """Ns(eid).longs.replace(Seq(long7 -> long8, long8 -> long8)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/longs`:" +
              "\n__ident__long8")


          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          other8 = 8L

          _ <- Ns(eid).longs.replace(long7 -> long8, long8 -> other8).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/longs`:" +
                "\n8"
          }

          // Conflicting new values
          _ <- Ns(eid).longs.replace(Seq(long7 -> long8, long8 -> other8)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/longs`:" +
                "\n8"
          }
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.longs(long1, long2, long3, long4, long5, long6).save.map(_.eid)

          // Retract value
          _ <- Ns(eid).longs.retract(long6).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1, long2, long3, long4, long5))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).longs.retract(long7).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1, long2, long3, long4, long5))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).longs.retract(long5, long5).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1, long2, long3, long4))

          // Retract multiple values (vararg)
          _ <- Ns(eid).longs.retract(long3, long4).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1, long2))

          // Retract Seq of values
          _ <- Ns(eid).longs.retract(Seq(long2)).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1))

          // Retract Seq of values as variable
          values = Seq(long1)
          _ <- Ns(eid).longs.retract(values).update
          _ <- Ns.longs.get.map(_ ==> List())

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).longs(long1).update
          _ <- Ns(eid).longs.retract(Seq[Long]()).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.longs(long2, long3).save.map(_.eid)

          // Apply value (retracts all current values!)
          _ <- Ns(eid).longs(long1).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).longs(long2, long3).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long2, long3))

          // Apply Seq of values
          _ <- Ns(eid).longs(Set(long4)).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long4))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).longs(Set[Long]()).update
          _ <- Ns.longs.get.map(_ ==> List())

          // Apply Seq of values as variable
          values = Set(long1, long2)
          _ <- Ns(eid).longs(values).update
          _ <- Ns.longs.get.map(_.head.toList.sorted ==> List(long1, long2))

          // Ref attributes are treated the same way
          _ <- Ns(eid).refs1(values).update

          // Delete all (apply no values)
          _ <- Ns(eid).longs().update
          _ <- Ns.longs.get.map(_ ==> List())


          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).longs(long1, long2, long2).update
          _ <- Ns.longs.get.map(_.head ==> Set(long1, long2))

          // Equal values are coalesced (at runtime)
          other3 = long3
          _ <- Ns(eid).longs(long2, long3, other3).update
          _ <- Ns.longs.get.map(_.head ==> Set(long2, long3))
        } yield ()
      }
    }
  }
}
