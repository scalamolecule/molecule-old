package moleculeTests.tests.core.crud.updateMap

import java.util.Date
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateMapDate extends AsyncTestSuite {

  lazy val tests = Tests {

    "Mapped variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.dateMap(str1 -> date1).save.map(_.eid)

          // Add pair
          _ <- Ns(eid).dateMap.assert(str2 -> date3).update
          _ <- Ns.dateMap.get.map(_.head ==> Map(str1 -> date1, str2 -> date3))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).dateMap.assert(str2 -> date2).update
          _ <- Ns.dateMap.get.map(_.head ==> Map(str1 -> date1, str2 -> date2))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).dateMap.assert(str3 -> date3, str4 -> date4).update
          _ <- Ns.dateMap.get.map(_.head ==> Map(str1 -> date1, str2 -> date2, str3 -> date3, str4 -> date4))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).dateMap.assert(Seq(str4 -> date4, str5 -> date5)).update
          _ <- Ns.dateMap.get.map(_.head ==> Map(str1 -> date1, str2 -> date2, str3 -> date3, str4 -> date4, str5 -> date5))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).dateMap.assert(Seq[(String, Date)]()).update
          _ <- Ns.dateMap.get.map(_.head ==> Map(str1 -> date1, str2 -> date2, str3 -> date3, str4 -> date4, str5 -> date5))


          // Can't add pairs with duplicate keys

          // vararg
          _ = expectCompileError("""Ns(eid).dateMap.assert(str1 -> date1, str1 -> date2).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/dateMap`:" +
              "\n__ident__str1 -> __ident__date1" +
              "\n__ident__str1 -> __ident__date2")

          // Seq
          _ = expectCompileError("""Ns(eid).dateMap.assert(Seq(str1 -> date1, str1 -> date2)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/dateMap`:" +
              "\n__ident__str1 -> __ident__date1" +
              "\n__ident__str1 -> __ident__date2")

          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          str1x = str1

          // vararg
          _ <- Ns(eid).dateMap.assert(str1 -> date1, str1x -> date2).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/dateMap`:" +
                "\na -> " + date1 +
                "\na -> " + date2
          }


          // Seq
          _ <- Ns(eid).dateMap.assert(Seq(str1 -> date1, str1x -> date2)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/dateMap`:" +
                "\na -> " + date1 +
                "\na -> " + date2
          }
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.dateMap(str1 -> date1, str2 -> date2, str3 -> date3, str4 -> date4, str5 -> date5, str6 -> date6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).dateMap.replace(str6 -> date8).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1, str2 -> date2, str3 -> date3, str4 -> date4, str5 -> date5, str6 -> date8))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).dateMap.replace(str5 -> date8).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1, str2 -> date2, str3 -> date3, str4 -> date4, str5 -> date8, str6 -> date8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).dateMap.replace(str3 -> date6, str4 -> date7).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1, str2 -> date2, str3 -> date6, str4 -> date7, str5 -> date8, str6 -> date8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).dateMap.replace(str3 -> date6, str4 -> date7).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1, str2 -> date2, str3 -> date6, str4 -> date7, str5 -> date8, str6 -> date8))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).dateMap.replace(Seq(str2 -> date5)).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1, str2 -> date5, str3 -> date6, str4 -> date7, str5 -> date8, str6 -> date8))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).dateMap.replace(Seq[(String, Date)]()).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1, str2 -> date5, str3 -> date6, str4 -> date7, str5 -> date8, str6 -> date8))


          // Can't replace pairs with duplicate keys

          _ = expectCompileError("""Ns(eid).dateMap.replace(str1 -> date1, str1 -> date2).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/dateMap`:" +
              "\n__ident__str1 -> __ident__date1" +
              "\n__ident__str1 -> __ident__date2")

          _ = expectCompileError("""Ns(eid).dateMap.replace(Seq(str1 -> date1, str1 -> date2)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/dateMap`:" +
              "\n__ident__str1 -> __ident__date1" +
              "\n__ident__str1 -> __ident__date2")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.dateMap(str1 -> date1, str2 -> date2, str3 -> date3, str4 -> date4, str5 -> date5, str6 -> date6).save.map(_.eid)

          // Remove pair by key
          _ <- Ns(eid).dateMap.retract(str6).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1, str2 -> date2, str3 -> date3, str4 -> date4, str5 -> date5))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).dateMap.retract(str7).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1, str2 -> date2, str3 -> date3, str4 -> date4, str5 -> date5))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).dateMap.retract(str5, str5).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1, str2 -> date2, str3 -> date3, str4 -> date4))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).dateMap.retract(str3, str4).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1, str2 -> date2))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).dateMap.retract(Seq(str2)).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).dateMap.retract(Seq[String]()).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.dateMap(str1 -> date1, str2 -> date2).save.map(_.eid)

          // Apply value (replaces all current values!)
          _ <- Ns(eid).dateMap(str1 -> date1).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str1 -> date1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).dateMap(str2 -> date2, str3 -> date3).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str2 -> date2, str3 -> date3))

          // Apply Map of values
          _ <- Ns(eid).dateMap(Seq(str4 -> date4)).update
          _ <- Ns.dateMap.get.map(_.head.toList.sorted ==> List(str4 -> date4))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).dateMap(Seq[(String, Date)]()).update
          _ <- Ns.dateMap.get.map(_ ==> List())


          _ <- Ns(eid).dateMap(Seq(str1 -> date1, str2 -> date2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).dateMap().update
          _ <- Ns.dateMap.get.map(_ ==> List())


          // Can't apply pairs with duplicate keys

          // vararg
          _ <- Ns(eid).dateMap(str1 -> date1, str1 -> date2).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/dateMap`:" +
                "\na -> " + date1 +
                "\na -> " + date2
          }


          // Seq
          _ <- Ns(eid).dateMap(Seq(str1 -> date1, str1 -> date2)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/dateMap`:" +
                "\na -> " + date1 +
                "\na -> " + date2
          }
        } yield ()
      }
    }
  }
}
