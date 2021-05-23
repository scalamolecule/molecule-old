package molecule.tests.core.crud.updateMap

import molecule.core.util.testing.expectCompileError
import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateMapBoolean extends AsyncTestSuite {

  lazy val tests = Tests {

    "Mapped variables" - {

      "assert" - core { implicit conn =>
        for {
          tx <- Ns.boolMap(str1 -> bool1).save
          eid = tx.eid

          // Add pair
          _ <- Ns(eid).boolMap.assert(str2 -> bool3).update
          _ <- Ns.boolMap.get.map(_.head ==> Map(str1 -> bool1, str2 -> bool3))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).boolMap.assert(str2 -> bool2).update
          _ <- Ns.boolMap.get.map(_.head ==> Map(str1 -> bool1, str2 -> bool2))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).boolMap.assert(str3 -> bool3, str4 -> bool4).update
          _ <- Ns.boolMap.get.map(_.head ==> Map(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).boolMap.assert(Seq(str4 -> bool4, str5 -> bool5)).update
          _ <- Ns.boolMap.get.map(_.head ==> Map(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).boolMap.assert(Seq[(String, Boolean)]()).update
          _ <- Ns.boolMap.get.map(_.head ==> Map(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5))


          // Can't add pairs with duplicate keys

          //      // vararg
          //      expectCompileError(
          //        """Ns(eid).boolMap.assert(str1 -> bool1, str1 -> bool2).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/boolMap`:" +
          //          "\n__ident__str1 -> __ident__bool1" +
          //          "\n__ident__str1 -> __ident__bool2")
          //
          //      // Seq
          //      expectCompileError(
          //        """Ns(eid).boolMap.assert(Seq(str1 -> bool1, str1 -> bool2)).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/boolMap`:" +
          //          "\n__ident__str1 -> __ident__bool1" +
          //          "\n__ident__str1 -> __ident__bool2")

          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          str1x = str1

          //      // vararg
          //      (Ns(eid).boolMap.assert(str1 -> bool1, str1x -> bool2).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/boolMap`:" +
          //        "\na -> true" +
          //        "\na -> false"
          //
          //
          //      // Seq
          //      (Ns(eid).boolMap.assert(Seq(str1 -> bool1, str1x -> bool2)).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/boolMap`:" +
          //        "\na -> true" +
          //        "\na -> false"
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx <- Ns.boolMap(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5, str6 -> bool6).save
          eid = tx.eid

          // Replace value
          _ <- Ns(eid).boolMap.replace(str6 -> bool8).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5, str6 -> bool8))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).boolMap.replace(str5 -> bool8).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool8, str6 -> bool8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).boolMap.replace(str3 -> bool6, str4 -> bool7).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1, str2 -> bool2, str3 -> bool6, str4 -> bool7, str5 -> bool8, str6 -> bool8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).boolMap.replace(str3 -> bool6, str4 -> bool7).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1, str2 -> bool2, str3 -> bool6, str4 -> bool7, str5 -> bool8, str6 -> bool8))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).boolMap.replace(Seq(str2 -> bool5)).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1, str2 -> bool5, str3 -> bool6, str4 -> bool7, str5 -> bool8, str6 -> bool8))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).boolMap.replace(Seq[(String, Boolean)]()).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1, str2 -> bool5, str3 -> bool6, str4 -> bool7, str5 -> bool8, str6 -> bool8))


          // Can't replace pairs with duplicate keys

          //      expectCompileError(
          //        """Ns(eid).boolMap.replace(str1 -> bool1, str1 -> bool2).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/boolMap`:" +
          //          "\n__ident__str1 -> __ident__bool1" +
          //          "\n__ident__str1 -> __ident__bool2")
          //
          //      expectCompileError(
          //        """Ns(eid).boolMap.replace(Seq(str1 -> bool1, str1 -> bool2)).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/boolMap`:" +
          //          "\n__ident__str1 -> __ident__bool1" +
          //          "\n__ident__str1 -> __ident__bool2")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx <- Ns.boolMap(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5, str6 -> bool6).save
          eid = tx.eid

          // Remove pair by key
          _ <- Ns(eid).boolMap.retract(str6).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).boolMap.retract(str7).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).boolMap.retract(str5, str5).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).boolMap.retract(str3, str4).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1, str2 -> bool2))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).boolMap.retract(Seq(str2)).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).boolMap.retract(Seq[String]()).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx <- Ns.boolMap(str1 -> bool1, str2 -> bool2).save
          eid = tx.eid

          // Apply value (replaces all current values!)
          _ <- Ns(eid).boolMap(str1 -> bool1).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str1 -> bool1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).boolMap(str2 -> bool2, str3 -> bool3).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str2 -> bool2, str3 -> bool3))

          // Apply Map of values
          _ <- Ns(eid).boolMap(Seq(str4 -> bool4)).update
          _ <- Ns.boolMap.get.map(_.head.toList.sorted ==> List(str4 -> bool4))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).boolMap(Seq[(String, Boolean)]()).update
          _ <- Ns.boolMap.get === List()


          _ <- Ns(eid).boolMap(Seq(str1 -> bool1, str2 -> bool2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).boolMap().update
          _ <- Ns.boolMap.get === List()

          //      // Can't apply pairs with duplicate keys
          //      (Ns(eid).longMap(str1 -> long1, str1 -> long2).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
          //        "\na -> 1" +
          //        "\na -> 2"
          //
          //      (Ns(eid).longMap(Seq(str1 -> long1, str1 -> long2)).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
          //        "\na -> 1" +
          //        "\na -> 2"
        } yield ()
      }
    }
  }
}