package moleculeTests.tests.core.crud.update

import java.util.Date
import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateDate extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          tx1 <- Ns.date(date2).save
          eid = tx1.eid

          // Apply value (retracts current value)
          _ <- Ns(eid).date(date1).update
          _ <- Ns.date.get.map(_.head ==> date1)

          // Apply new value
          _ <- Ns(eid).date(date2).update
          _ <- Ns.date.get.map(_.head ==> date2)

          // Delete value (apply no value)
          _ <- Ns(eid).date().update
          _ <- Ns.date.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).date(date2, date3).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... date($date2, $date3)"
          }
        } yield ()
      }
    }


    "Card-many variables" - {

      "assert" - core { implicit conn =>
        for {
          tx1 <- Ns.dates(date1).save
          eid = tx1.eid

          // Assert value
          _ <- Ns(eid).dates.assert(date2).update
          _ <- Ns.dates.get.map(_.head ==> Set(date1, date2))

          // Assert existing value (no effect)
          _ <- Ns(eid).dates.assert(date2).update
          _ <- Ns.dates.get.map(_.head ==> Set(date1, date2))

          // Assert multiple values
          _ <- Ns(eid).dates.assert(date3, date4).update
          _ <- Ns.dates.get.map(_.head ==> Set(date1, date2, date3, date4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).dates.assert(Seq(date4, date5)).update
          _ <- Ns.dates.get.map(_.head ==> Set(date1, date2, date3, date4, date5))

          // Assert Seq of values as variable (existing values unaffected)
          values = Seq(date6, date7)
          _ <- Ns(eid).dates.assert(values).update
          _ <- Ns.dates.get.map(_.head ==> Set(date1, date2, date3, date4, date5, date6, date7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).dates.assert(Seq[Date]()).update
          _ <- Ns.dates.get.map(_.head ==> Set(date1, date2, date3, date4, date5, date6, date7))


          // Reset
          _ <- Ns(eid).dates().update

          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).dates.assert(date1, date2, date2).update
          _ <- Ns.dates.get.map(_.head ==> Set(date1, date2))

          // Equal values are coalesced (at runtime)
          other3 = date3
          _ <- Ns(eid).dates.assert(date2, date3, other3).update
          _ <- Ns.dates.get.map(_.head ==> Set(date3, date2, date1))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx1 <- Ns.dates(date1, date2, date3, date4, date5, date6).save
          eid = tx1.eid

          // Replace value
          _ <- Ns(eid).dates.replace(date6 -> date8).update
          _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1, date2, date3, date4, date5, date8))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).dates.replace(date5 -> date8).update
          _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1, date2, date3, date4, date8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).dates.replace(date3 -> date6, date4 -> date7).update
          _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1, date2, date6, date7, date8))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).dates.replace(Seq(date2 -> date5)).update
          _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1, date5, date6, date7, date8))

          // Replace with Seq of oldValue->newValue pairs as variable
          values = Seq(date1 -> date4)
          _ <- Ns(eid).dates.replace(values).update
          _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date4, date5, date6, date7, date8))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).dates.replace(Seq[(Date, Date)]()).update
          _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date4, date5, date6, date7, date8))


          // Can't replace duplicate values

          _ = compileError(            """Ns(eid).dates.replace(date7 -> date8, date8 -> date8).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/dates`:" +
              "\n__ident__date8")

          _ = compileError(            """Ns(eid).dates.replace(Seq(date7 -> date8, date8 -> date8)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/dates`:" +
              "\n__ident__date8")


          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          other8 = date8

          _ <- Ns(eid).dates.replace(date7 -> date8, date8 -> other8).update.recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/dates`:" +
              "\n" + date8
          }

          // Conflicting new values
          _ <- Ns(eid).dates.replace(Seq(date7 -> date8, date8 -> other8)).update.recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/dates`:" +
              "\n" + date8
          }
        } yield ()
      }
    }


    "retract" - core { implicit conn =>
      for {
        tx1 <- Ns.dates(date1, date2, date3, date4, date5, date6).save
        eid = tx1.eid

        // Retract value
        _ <- Ns(eid).dates.retract(date6).update
        _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1, date2, date3, date4, date5))

        // Retracting non-existing value has no effect
        _ <- Ns(eid).dates.retract(date7).update
        _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1, date2, date3, date4, date5))

        // Retracting duplicate values removes the distinc value
        _ <- Ns(eid).dates.retract(date5, date5).update
        _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1, date2, date3, date4))

        // Retract multiple values (vararg)
        _ <- Ns(eid).dates.retract(date3, date4).update
        _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1, date2))

        // Retract Seq of values
        _ <- Ns(eid).dates.retract(Seq(date2)).update
        _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1))

        // Retract Seq of values as variable
        values = Seq(date1)
        _ <- Ns(eid).dates.retract(values).update
        _ <- Ns.dates.get.map(_ ==> List())

        // Retracting empty Seq of values has no effect
        _ <- Ns(eid).dates(date1).update
        _ <- Ns(eid).dates.retract(Seq[Date]()).update
        _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1))
      } yield ()
    }


    "apply" - core { implicit conn =>
      for {
        tx1 <- Ns.dates(date2, date3).save
        eid = tx1.eid

        // Apply value (retracts all current values!)
        _ <- Ns(eid).dates(date1).update
        _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1))

        // Apply multiple values (vararg)
        _ <- Ns(eid).dates(date2, date3).update
        _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date2, date3))

        // Apply Seq of values
        _ <- Ns(eid).dates(Set(date4)).update
        _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date4))

        // Apply empty Seq of values (retracting all values!)
        _ <- Ns(eid).dates(Set[Date]()).update
        _ <- Ns.dates.get.map(_ ==> List())

        // Apply Seq of values as variable
        values = Set(date1, date2)
        _ <- Ns(eid).dates(values).update
        _ <- Ns.dates.get.map(_.head.toList.sorted ==> List(date1, date2))

        // Delete all (apply no values)
        _ <- Ns(eid).dates().update
        _ <- Ns.dates.get.map(_ ==> List())


        // Redundant duplicate values are discarded

        // Equally named variables are coalesced (at compile time)
        _ <- Ns(eid).dates(date1, date2, date2).update
        _ <- Ns.dates.get.map(_.head ==> Set(date1, date2))

        // Equal values are coalesced (at runtime)
        other3 = date3
        _ <- Ns(eid).dates(date2, date3, other3).update
        _ <- Ns.dates.get.map(_.head ==> Set(date2, date3))
      } yield ()
    }
  }
}

