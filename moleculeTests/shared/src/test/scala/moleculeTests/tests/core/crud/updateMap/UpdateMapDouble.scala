package moleculeTests.tests.core.crud.updateMap

import molecule.core.util.testing.expectCompileError
import moleculeTests.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateMapDouble extends AsyncTestSuite {

  lazy val tests = Tests {

    "Mapped values" - {

      "assert" - core { implicit conn =>
        for {
          tx <- Ns.doubleMap("str1" -> 1.0).save
          eid = tx.eid

          // Add pair
          _ <- Ns(eid).doubleMap.assert("str2" -> 3.0).update
          _ <- Ns.doubleMap.get.map(_.head ==> Map("str1" -> 1.0, "str2" -> 3.0))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).doubleMap.assert("str2" -> 2.0).update
          _ <- Ns.doubleMap.get.map(_.head ==> Map("str1" -> 1.0, "str2" -> 2.0))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).doubleMap.assert("str3" -> 3.0, "str4" -> 4.0).update
          _ <- Ns.doubleMap.get.map(_.head ==> Map("str1" -> 1.0, "str2" -> 2.0, "str3" -> 3.0, "str4" -> 4.0))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).doubleMap.assert(Seq("str4" -> 4.0, "str5" -> 5.0)).update
          _ <- Ns.doubleMap.get.map(_.head ==> Map("str1" -> 1.0, "str2" -> 2.0, "str3" -> 3.0, "str4" -> 4.0, "str5" -> 5.0))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).doubleMap.assert(Seq[(String, Double)]()).update
          _ <- Ns.doubleMap.get.map(_.head ==> Map("str1" -> 1.0, "str2" -> 2.0, "str3" -> 3.0, "str4" -> 4.0, "str5" -> 5.0))


          // Can't add pairs with duplicate keys

                // vararg
                _ = compileError(
                  """Ns(eid).doubleMap.assert("str1" -> 1.0, "str1" -> 2.0).update""").check(
                  "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
                    "\nstr1 -> 1.0" +
                    "\nstr1 -> 2.0")

                // Seq
                _ = compileError(
                  """Ns(eid).doubleMap.assert(Seq("str1" -> 1.0, "str1" -> 2.0)).update""").check(
                  "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
                    "\nstr1 -> 1.0" +
                    "\nstr1 -> 2.0")
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx <- Ns.doubleMap("str1" -> 1.0, "str2" -> 2.0, "str3" -> 3.0, "str4" -> 4.0, "str5" -> 5.0, "str6" -> 6.0).save
          eid = tx.eid

          // Replace value
          _ <- Ns(eid).doubleMap.replace("str6" -> 8.0).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0, "str2" -> 2.0, "str3" -> 3.0, "str4" -> 4.0, "str5" -> 5.0, "str6" -> 8.0))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).doubleMap.replace("str5" -> 8.0).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0, "str2" -> 2.0, "str3" -> 3.0, "str4" -> 4.0, "str5" -> 8.0, "str6" -> 8.0))

          // Replace multiple values (vararg)
          _ <- Ns(eid).doubleMap.replace("str3" -> 6.0, "str4" -> 7.0).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0, "str2" -> 2.0, "str3" -> 6.0, "str4" -> 7.0, "str5" -> 8.0, "str6" -> 8.0))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).doubleMap.replace("str3" -> 6.0, "str4" -> 7.0).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0, "str2" -> 2.0, "str3" -> 6.0, "str4" -> 7.0, "str5" -> 8.0, "str6" -> 8.0))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).doubleMap.replace(Seq("str2" -> 5.0)).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0, "str2" -> 5.0, "str3" -> 6.0, "str4" -> 7.0, "str5" -> 8.0, "str6" -> 8.0))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).doubleMap.replace(Seq[(String, Double)]()).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0, "str2" -> 5.0, "str3" -> 6.0, "str4" -> 7.0, "str5" -> 8.0, "str6" -> 8.0))


          // Can't replace pairs with duplicate keys

                _ = compileError(
                  """Ns(eid).doubleMap.replace("str1" -> 1.0, "str1" -> 2.0).update""").check(
                  "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
                    "\nstr1 -> 1.0" +
                    "\nstr1 -> 2.0")

                _ = compileError(
                  """Ns(eid).doubleMap.replace(Seq("str1" -> 1.0, "str1" -> 2.0)).update""").check(
                  "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
                    "\nstr1 -> 1.0" +
                    "\nstr1 -> 2.0")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx <- Ns.doubleMap("str1" -> 1.0, "str2" -> 2.0, "str3" -> 3.0, "str4" -> 4.0, "str5" -> 5.0, "str6" -> 6.0).save
          eid = tx.eid

          // Remove pair by key
          _ <- Ns(eid).doubleMap.retract("str6").update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0, "str2" -> 2.0, "str3" -> 3.0, "str4" -> 4.0, "str5" -> 5.0))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).doubleMap.retract("str7").update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0, "str2" -> 2.0, "str3" -> 3.0, "str4" -> 4.0, "str5" -> 5.0))

         // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).doubleMap.retract("str5", "str5").update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0, "str2" -> 2.0, "str3" -> 3.0, "str4" -> 4.0))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).doubleMap.retract("str3", "str4").update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0, "str2" -> 2.0))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).doubleMap.retract(Seq("str2")).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).doubleMap.retract(Seq[String]()).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx <- Ns.doubleMap("str1" -> 1.0, "str2" -> 2.0).save
          eid = tx.eid

          // Apply value (replaces all current values!)
          _ <- Ns(eid).doubleMap("str1" -> 1.0).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str1" -> 1.0))

          // Apply multiple values (vararg)
          _ <- Ns(eid).doubleMap("str2" -> 2.0, "str3" -> 3.0).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str2" -> 2.0, "str3" -> 3.0))

          // Apply Map of values
          _ <- Ns(eid).doubleMap(Seq("str4" -> 4.0)).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List("str4" -> 4.0))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).doubleMap(Seq[(String, Double)]()).update
          _ <- Ns.doubleMap.get === List()


          _ <- Ns(eid).doubleMap(Seq("str1" -> 1.0, "str2" -> 2.0)).update

          // Delete all (apply no values)
          _ <- Ns(eid).doubleMap().update
          _ <- Ns.doubleMap.get === List()


          // Can't apply pairs with duplicate keys

          //      // vararg
          //      (Ns(eid).doubleMap("str1" -> 1.0, "str1" -> 2.0).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
          //        "\nstr1 -> 1.0" +
          //        "\nstr1 -> 2.0"
          //
          //      // Seq
          //      (Ns(eid).doubleMap(Seq("str1" -> 1.0, "str1" -> 2.0)).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
          //        "\nstr1 -> 1.0" +
          //        "\nstr1 -> 2.0"
        } yield ()
      }
    }

    "Mapped variables" - {

      "assert" - core { implicit conn =>
        for {
          tx <- Ns.doubleMap(str1 -> double1).save
          eid = tx.eid

          // Add pair
          _ <- Ns(eid).doubleMap.assert(str2 -> double3).update
          _ <- Ns.doubleMap.get.map(_.head ==> Map(str1 -> double1, str2 -> double3))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).doubleMap.assert(str2 -> double2).update
          _ <- Ns.doubleMap.get.map(_.head ==> Map(str1 -> double1, str2 -> double2))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).doubleMap.assert(str3 -> double3, str4 -> double4).update
          _ <- Ns.doubleMap.get.map(_.head ==> Map(str1 -> double1, str2 -> double2, str3 -> double3, str4 -> double4))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).doubleMap.assert(Seq(str4 -> double4, str5 -> double5)).update
          _ <- Ns.doubleMap.get.map(_.head ==> Map(str1 -> double1, str2 -> double2, str3 -> double3, str4 -> double4, str5 -> double5))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).doubleMap.assert(Seq[(String, Double)]()).update
          _ <- Ns.doubleMap.get.map(_.head ==> Map(str1 -> double1, str2 -> double2, str3 -> double3, str4 -> double4, str5 -> double5))


          // Can't add pairs with duplicate keys

                // vararg
                _ = compileError(
                  """Ns(eid).doubleMap.assert(str1 -> double1, str1 -> double2).update""").check(
                  "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
                    "\n__ident__str1 -> __ident__double1" +
                    "\n__ident__str1 -> __ident__double2")

                // Seq
                _ = compileError(
                  """Ns(eid).doubleMap.assert(Seq(str1 -> double1, str1 -> double2)).update""").check(
                  "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
                    "\n__ident__str1 -> __ident__double1" +
                    "\n__ident__str1 -> __ident__double2")

          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          str1x = str1

          // vararg
          //      (Ns(eid).doubleMap.assert(str1 -> double1, str1x -> double2).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
          //        "\na -> 1.0" +
          //        "\na -> 2.0"
          //
          //      // Seq
          //      (Ns(eid).doubleMap.assert(Seq(str1 -> double1, str1x -> double2)).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
          //        "\na -> 1.0" +
          //        "\na -> 2.0"
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx <- Ns.doubleMap(str1 -> double1, str2 -> double2, str3 -> double3, str4 -> double4, str5 -> double5, str6 -> double6).save
          eid = tx.eid

          // Replace value
          _ <- Ns(eid).doubleMap.replace(str6 -> double8).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1, str2 -> double2, str3 -> double3, str4 -> double4, str5 -> double5, str6 -> double8))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).doubleMap.replace(str5 -> double8).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1, str2 -> double2, str3 -> double3, str4 -> double4, str5 -> double8, str6 -> double8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).doubleMap.replace(str3 -> double6, str4 -> double7).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1, str2 -> double2, str3 -> double6, str4 -> double7, str5 -> double8, str6 -> double8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).doubleMap.replace(str3 -> double6, str4 -> double7).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1, str2 -> double2, str3 -> double6, str4 -> double7, str5 -> double8, str6 -> double8))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).doubleMap.replace(Seq(str2 -> double5)).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1, str2 -> double5, str3 -> double6, str4 -> double7, str5 -> double8, str6 -> double8))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).doubleMap.replace(Seq[(String, Double)]()).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1, str2 -> double5, str3 -> double6, str4 -> double7, str5 -> double8, str6 -> double8))


          // Can't replace pairs with duplicate keys

                _ = compileError(
                  """Ns(eid).doubleMap.replace(str1 -> double1, str1 -> double2).update""").check(
                  "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
                    "\n__ident__str1 -> __ident__double1" +
                    "\n__ident__str1 -> __ident__double2")

                _ = compileError(
                  """Ns(eid).doubleMap.replace(Seq(str1 -> double1, str1 -> double2)).update""").check(
                  "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
                    "\n__ident__str1 -> __ident__double1" +
                    "\n__ident__str1 -> __ident__double2")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx <- Ns.doubleMap(str1 -> double1, str2 -> double2, str3 -> double3, str4 -> double4, str5 -> double5, str6 -> double6).save
          eid = tx.eid

          // Remove pair by key
          _ <- Ns(eid).doubleMap.retract(str6).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1, str2 -> double2, str3 -> double3, str4 -> double4, str5 -> double5))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).doubleMap.retract(str7).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1, str2 -> double2, str3 -> double3, str4 -> double4, str5 -> double5))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).doubleMap.retract(str5, str5).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1, str2 -> double2, str3 -> double3, str4 -> double4))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).doubleMap.retract(str3, str4).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1, str2 -> double2))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).doubleMap.retract(Seq(str2)).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).doubleMap.retract(Seq[String]()).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx <- Ns.doubleMap(str1 -> double1, str2 -> double2).save
          eid = tx.eid

          // Apply value (replaces all current values!)
          _ <- Ns(eid).doubleMap(str1 -> double1).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str1 -> double1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).doubleMap(str2 -> double2, str3 -> double3).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str2 -> double2, str3 -> double3))

          // Apply Map of values
          _ <- Ns(eid).doubleMap(Seq(str4 -> double4)).update
          _ <- Ns.doubleMap.get.map(_.head.toList.sorted ==> List(str4 -> double4))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).doubleMap(Seq[(String, Double)]()).update
          _ <- Ns.doubleMap.get === List()


          _ <- Ns(eid).doubleMap(Seq(str1 -> double1, str2 -> double2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).doubleMap().update
          _ <- Ns.doubleMap.get === List()


          // Can't apply pairs with duplicate keys

          //      // vararg
          //      (Ns(eid).doubleMap(str1 -> double1, str1 -> double2).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
          //        "\na -> 1.0" +
          //        "\na -> 2.0"
          //
          //      // Seq
          //      (Ns(eid).doubleMap(Seq(str1 -> double1, str1 -> double2)).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/doubleMap`:" +
          //        "\na -> 1.0" +
          //        "\na -> 2.0"
        } yield ()
      }
    }
  }
}