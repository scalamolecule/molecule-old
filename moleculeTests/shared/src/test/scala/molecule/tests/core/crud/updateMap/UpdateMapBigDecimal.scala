package molecule.tests.core.crud.updateMap

import molecule.core.util.testing.expectCompileError
import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateMapBigDecimal extends AsyncTestSuite {

  lazy val tests = Tests {

    "Mapped variables" - {

      "assert" - core { implicit conn =>
        for {
          tx <- Ns.bigDecMap(str1 -> bigDec1).save
          eid = tx.eid

          // Add pair
          _ <- Ns(eid).bigDecMap.assert(str2 -> bigDec3).update
          _ <- Ns.bigDecMap.get.map(_.head ==> Map(str1 -> bigDec1, str2 -> bigDec3))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).bigDecMap.assert(str2 -> bigDec2).update
          _ <- Ns.bigDecMap.get.map(_.head ==> Map(str1 -> bigDec1, str2 -> bigDec2))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).bigDecMap.assert(str3 -> bigDec3, str4 -> bigDec4).update
          _ <- Ns.bigDecMap.get.map(_.head ==> Map(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec3, str4 -> bigDec4))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).bigDecMap.assert(Seq(str4 -> bigDec4, str5 -> bigDec5)).update
          _ <- Ns.bigDecMap.get.map(_.head ==> Map(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec3, str4 -> bigDec4, str5 -> bigDec5))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).bigDecMap.assert(Seq[(String, BigDecimal)]()).update
          _ <- Ns.bigDecMap.get.map(_.head ==> Map(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec3, str4 -> bigDec4, str5 -> bigDec5))


          // Can't add pairs with duplicate keys

          //      // vararg
          //      expectCompileError(
          //        """Ns(eid).bigDecMap.assert(str1 -> bigDec1, str1 -> bigDec2).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/bigDecMap`:" +
          //          "\n__ident__str1 -> __ident__bigDec1" +
          //          "\n__ident__str1 -> __ident__bigDec2")
          //
          //      // Seq
          //      expectCompileError(
          //        """Ns(eid).bigDecMap.assert(Seq(str1 -> bigDec1, str1 -> bigDec2)).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/bigDecMap`:" +
          //          "\n__ident__str1 -> __ident__bigDec1" +
          //          "\n__ident__str1 -> __ident__bigDec2")

          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          str1x = str1

          //      // vararg
          //      (Ns(eid).bigDecMap.assert(str1 -> bigDec1, str1x -> bigDec2).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/bigDecMap`:" +
          //        "\na -> " + bigDec1 +
          //        "\na -> " + bigDec2
          //
          //
          //      // Seq
          //      (Ns(eid).bigDecMap.assert(Seq(str1 -> bigDec1, str1x -> bigDec2)).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/bigDecMap`:" +
          //        "\na -> " + bigDec1 +
          //        "\na -> " + bigDec2
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx <- Ns.bigDecMap(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec3, str4 -> bigDec4, str5 -> bigDec5, str6 -> bigDec6).save
          eid = tx.eid

          // Replace value
          _ <- Ns(eid).bigDecMap.replace(str6 -> bigDec8).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec3, str4 -> bigDec4, str5 -> bigDec5, str6 -> bigDec8))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).bigDecMap.replace(str5 -> bigDec8).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec3, str4 -> bigDec4, str5 -> bigDec8, str6 -> bigDec8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).bigDecMap.replace(str3 -> bigDec6, str4 -> bigDec7).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec6, str4 -> bigDec7, str5 -> bigDec8, str6 -> bigDec8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).bigDecMap.replace(str3 -> bigDec6, str4 -> bigDec7).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec6, str4 -> bigDec7, str5 -> bigDec8, str6 -> bigDec8))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).bigDecMap.replace(Seq(str2 -> bigDec5)).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1, str2 -> bigDec5, str3 -> bigDec6, str4 -> bigDec7, str5 -> bigDec8, str6 -> bigDec8))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).bigDecMap.replace(Seq[(String, BigDecimal)]()).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1, str2 -> bigDec5, str3 -> bigDec6, str4 -> bigDec7, str5 -> bigDec8, str6 -> bigDec8))


          // Can't replace pairs with duplicate keys

          //      expectCompileError(
          //        """Ns(eid).bigDecMap.replace(str1 -> bigDec1, str1 -> bigDec2).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/bigDecMap`:" +
          //          "\n__ident__str1 -> __ident__bigDec1" +
          //          "\n__ident__str1 -> __ident__bigDec2")
          //
          //      expectCompileError(
          //        """Ns(eid).bigDecMap.replace(Seq(str1 -> bigDec1, str1 -> bigDec2)).update""",
          //        "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/bigDecMap`:" +
          //          "\n__ident__str1 -> __ident__bigDec1" +
          //          "\n__ident__str1 -> __ident__bigDec2")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx <- Ns.bigDecMap(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec3, str4 -> bigDec4, str5 -> bigDec5, str6 -> bigDec6).save
          eid = tx.eid

          // Remove pair by key
          _ <- Ns(eid).bigDecMap.retract(str6).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec3, str4 -> bigDec4, str5 -> bigDec5))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).bigDecMap.retract(str7).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec3, str4 -> bigDec4, str5 -> bigDec5))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).bigDecMap.retract(str5, str5).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1, str2 -> bigDec2, str3 -> bigDec3, str4 -> bigDec4))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).bigDecMap.retract(str3, str4).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1, str2 -> bigDec2))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).bigDecMap.retract(Seq(str2)).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).bigDecMap.retract(Seq[String]()).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx <- Ns.bigDecMap(str1 -> bigDec1, str2 -> bigDec2).save
          eid = tx.eid

          // Apply value (replaces all current values!)
          _ <- Ns(eid).bigDecMap(str1 -> bigDec1).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigDec1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).bigDecMap(str2 -> bigDec2, str3 -> bigDec3).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str2 -> bigDec2, str3 -> bigDec3))

          // Apply Map of values
          _ <- Ns(eid).bigDecMap(Seq(str4 -> bigDec4)).update
          _ <- Ns.bigDecMap.get.map(_.head.toList.sortBy(_._1) ==> List(str4 -> bigDec4))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).bigDecMap(Seq[(String, BigDecimal)]()).update
          _ <- Ns.bigDecMap.get === List()


          _ <- Ns(eid).bigDecMap(Seq(str1 -> bigDec1, str2 -> bigDec2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).bigDecMap().update
          _ <- Ns.bigDecMap.get === List()


          // Can't apply pairs with duplicate keys

          //      (Ns(eid).bigDecMap(str1 -> bigDec1, str1 -> bigDec2).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/bigDecMap`:" +
          //        "\na -> " + bigDec1 +
          //        "\na -> " + bigDec2
          //
          //      (Ns(eid).bigDecMap(Seq(str1 -> bigDec1, str1 -> bigDec2)).update must throwA[Model2TransactionException])
          //        .message === "Got the exception molecule.datomic.base.transform.exception.Model2TransactionException: " +
          //        "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/bigDecMap`:" +
          //        "\na -> " + bigDec1 +
          //        "\na -> " + bigDec2
        } yield ()
      }
    }
  }
}
