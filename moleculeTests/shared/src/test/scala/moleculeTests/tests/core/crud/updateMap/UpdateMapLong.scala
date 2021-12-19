package moleculeTests.tests.core.crud.updateMap

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object UpdateMapLong extends AsyncTestSuite {

  lazy val tests = Tests {

    "Mapped values" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.longMap("str1" -> 1L).save.map(_.eid)

          // Add pair
          _ <- Ns(eid).longMap.assert("str2" -> 3L).update
          _ <- Ns.longMap.get.map(_.head ==> Map("str1" -> 1L, "str2" -> 3L))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).longMap.assert("str2" -> 2L).update
          _ <- Ns.longMap.get.map(_.head ==> Map("str1" -> 1L, "str2" -> 2L))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).longMap.assert("str3" -> 3L, "str4" -> 4L).update
          _ <- Ns.longMap.get.map(_.head ==> Map("str1" -> 1L, "str2" -> 2L, "str3" -> 3L, "str4" -> 4L))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).longMap.assert(Seq("str4" -> 4L, "str5" -> 5L)).update
          _ <- Ns.longMap.get.map(_.head ==> Map("str1" -> 1L, "str2" -> 2L, "str3" -> 3L, "str4" -> 4L, "str5" -> 5L))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).longMap.assert(Seq[(String, Long)]()).update
          _ <- Ns.longMap.get.map(_.head ==> Map("str1" -> 1L, "str2" -> 2L, "str3" -> 3L, "str4" -> 4L, "str5" -> 5L))


          // Can't add pairs with duplicate keys

          // vararg
          _ = expectCompileError("""Ns(eid).longMap.assert("str1" -> 1L, "str1" -> 2L).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
              "\nstr1 -> 1" +
              "\nstr1 -> 2")

          // Seq
          _ = expectCompileError("""Ns(eid).longMap.assert(Seq("str1" -> 1L, "str1" -> 2L)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
              "\nstr1 -> 1" +
              "\nstr1 -> 2")
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.longMap("str1" -> 1L, "str2" -> 2L, "str3" -> 3L, "str4" -> 4L, "str5" -> 5L, "str6" -> 6L).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).longMap.replace("str6" -> 8L).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L, "str2" -> 2L, "str3" -> 3L, "str4" -> 4L, "str5" -> 5L, "str6" -> 8L))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).longMap.replace("str5" -> 8L).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L, "str2" -> 2L, "str3" -> 3L, "str4" -> 4L, "str5" -> 8L, "str6" -> 8L))

          // Replace multiple values (vararg)
          _ <- Ns(eid).longMap.replace("str3" -> 6L, "str4" -> 7L).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L, "str2" -> 2L, "str3" -> 6L, "str4" -> 7L, "str5" -> 8L, "str6" -> 8L))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).longMap.replace("str3" -> 6L, "str4" -> 7L).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L, "str2" -> 2L, "str3" -> 6L, "str4" -> 7L, "str5" -> 8L, "str6" -> 8L))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).longMap.replace(Seq("str2" -> 5L)).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L, "str2" -> 5L, "str3" -> 6L, "str4" -> 7L, "str5" -> 8L, "str6" -> 8L))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).longMap.replace(Seq[(String, Long)]()).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L, "str2" -> 5L, "str3" -> 6L, "str4" -> 7L, "str5" -> 8L, "str6" -> 8L))


          // Can't replace pairs with duplicate keys

          _ = expectCompileError("""Ns(eid).longMap.replace("str1" -> 1L, "str1" -> 2L).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
              "\nstr1 -> 1" +
              "\nstr1 -> 2")

          _ = expectCompileError("""Ns(eid).longMap.replace(Seq("str1" -> 1L, "str1" -> 2L)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
              "\nstr1 -> 1" +
              "\nstr1 -> 2")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.longMap("str1" -> 1L, "str2" -> 2L, "str3" -> 3L, "str4" -> 4L, "str5" -> 5L, "str6" -> 6L).save.map(_.eid)

          // Remove pair by key
          _ <- Ns(eid).longMap.retract("str6").update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L, "str2" -> 2L, "str3" -> 3L, "str4" -> 4L, "str5" -> 5L))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).longMap.retract("str7").update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L, "str2" -> 2L, "str3" -> 3L, "str4" -> 4L, "str5" -> 5L))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).longMap.retract("str5", "str5").update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L, "str2" -> 2L, "str3" -> 3L, "str4" -> 4L))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).longMap.retract("str3", "str4").update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L, "str2" -> 2L))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).longMap.retract(Seq("str2")).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).longMap.retract(Seq[String]()).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.longMap("str1" -> 1L, "str2" -> 2L).save.map(_.eid)

          // Apply value (replaces all current values!)
          _ <- Ns(eid).longMap("str1" -> 1L).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str1" -> 1L))

          // Apply multiple values (vararg)
          _ <- Ns(eid).longMap("str2" -> 2L, "str3" -> 3L).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str2" -> 2L, "str3" -> 3L))

          // Apply Map of values
          _ <- Ns(eid).longMap(Seq("str4" -> 4L)).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List("str4" -> 4L))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).longMap(Seq[(String, Long)]()).update
          _ <- Ns.longMap.get.map(_ ==> List())


          _ <- Ns(eid).longMap(Seq("str1" -> 1L, "str2" -> 2L)).update

          // Delete all (apply no values)
          _ <- Ns(eid).longMap().update
          _ <- Ns.longMap.get.map(_ ==> List())


          // Can't apply pairs with duplicate keys

          _ <- Ns(eid).longMap("str1" -> 1L, "str1" -> 2L).update
            .map(_ ==> "Unexpected success").recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
                "\nstr1 -> 1" +
                "\nstr1 -> 2"
          }

          _ <- Ns(eid).longMap(Seq("str1" -> 1L, "str1" -> 2L)).update
            .map(_ ==> "Unexpected success").recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
                "\nstr1 -> 1" +
                "\nstr1 -> 2"
          }
        } yield ()
      }
    }


    "Mapped variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.longMap(str1 -> long1).save.map(_.eid)

          // Add pair
          _ <- Ns(eid).longMap.assert(str2 -> long3).update
          _ <- Ns.longMap.get.map(_.head ==> Map(str1 -> long1, str2 -> long3))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).longMap.assert(str2 -> long2).update
          _ <- Ns.longMap.get.map(_.head ==> Map(str1 -> long1, str2 -> long2))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).longMap.assert(str3 -> long3, str4 -> long4).update
          _ <- Ns.longMap.get.map(_.head ==> Map(str1 -> long1, str2 -> long2, str3 -> long3, str4 -> long4))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).longMap.assert(Seq(str4 -> long4, str5 -> long5)).update
          _ <- Ns.longMap.get.map(_.head ==> Map(str1 -> long1, str2 -> long2, str3 -> long3, str4 -> long4, str5 -> long5))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).longMap.assert(Seq[(String, Long)]()).update
          _ <- Ns.longMap.get.map(_.head ==> Map(str1 -> long1, str2 -> long2, str3 -> long3, str4 -> long4, str5 -> long5))


          // Can't add pairs with duplicate keys

          // vararg
          _ = expectCompileError("""Ns(eid).longMap.assert(str1 -> long1, str1 -> long2).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
              "\n__ident__str1 -> __ident__long1" +
              "\n__ident__str1 -> __ident__long2")

          // Seq
          _ = expectCompileError("""Ns(eid).longMap.assert(Seq(str1 -> long1, str1 -> long2)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
              "\n__ident__str1 -> __ident__long1" +
              "\n__ident__str1 -> __ident__long2")

          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          str1x = str1

          // vararg
          _ <- Ns(eid).longMap.assert(str1 -> long1, str1x -> long2).update
            .map(_ ==> "Unexpected success").recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
                "\na -> 1" +
                "\na -> 2"
          }

          // Seq
          _ <- Ns(eid).longMap.assert(Seq(str1 -> long1, str1x -> long2)).update
            .map(_ ==> "Unexpected success").recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
                "\na -> 1" +
                "\na -> 2"
          }
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.longMap(str1 -> long1, str2 -> long2, str3 -> long3, str4 -> long4, str5 -> long5, str6 -> long6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).longMap.replace(str6 -> long8).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1, str2 -> long2, str3 -> long3, str4 -> long4, str5 -> long5, str6 -> long8))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).longMap.replace(str5 -> long8).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1, str2 -> long2, str3 -> long3, str4 -> long4, str5 -> long8, str6 -> long8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).longMap.replace(str3 -> long6, str4 -> long7).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1, str2 -> long2, str3 -> long6, str4 -> long7, str5 -> long8, str6 -> long8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).longMap.replace(str3 -> long6, str4 -> long7).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1, str2 -> long2, str3 -> long6, str4 -> long7, str5 -> long8, str6 -> long8))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).longMap.replace(Seq(str2 -> long5)).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1, str2 -> long5, str3 -> long6, str4 -> long7, str5 -> long8, str6 -> long8))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).longMap.replace(Seq[(String, Long)]()).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1, str2 -> long5, str3 -> long6, str4 -> long7, str5 -> long8, str6 -> long8))


          // Can't replace pairs with duplicate keys

          _ = expectCompileError("""Ns(eid).longMap.replace(str1 -> long1, str1 -> long2).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
              "\n__ident__str1 -> __ident__long1" +
              "\n__ident__str1 -> __ident__long2")

          _ = expectCompileError("""Ns(eid).longMap.replace(Seq(str1 -> long1, str1 -> long2)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
              "\n__ident__str1 -> __ident__long1" +
              "\n__ident__str1 -> __ident__long2")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.longMap(str1 -> long1, str2 -> long2, str3 -> long3, str4 -> long4, str5 -> long5, str6 -> long6).save.map(_.eid)

          // Remove pair by key
          _ <- Ns(eid).longMap.retract(str6).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1, str2 -> long2, str3 -> long3, str4 -> long4, str5 -> long5))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).longMap.retract(str7).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1, str2 -> long2, str3 -> long3, str4 -> long4, str5 -> long5))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).longMap.retract(str5, str5).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1, str2 -> long2, str3 -> long3, str4 -> long4))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).longMap.retract(str3, str4).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1, str2 -> long2))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).longMap.retract(Seq(str2)).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).longMap.retract(Seq[String]()).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.longMap(str1 -> long1, str2 -> long2).save.map(_.eid)

          // Apply value (replaces all current values!)
          _ <- Ns(eid).longMap(str1 -> long1).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str1 -> long1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).longMap(str2 -> long2, str3 -> long3).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str2 -> long2, str3 -> long3))

          // Apply Map of values
          _ <- Ns(eid).longMap(Seq(str4 -> long4)).update
          _ <- Ns.longMap.get.map(_.head.toList.sorted ==> List(str4 -> long4))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).longMap(Seq[(String, Long)]()).update
          _ <- Ns.longMap.get.map(_ ==> List())


          _ <- Ns(eid).longMap(Seq(str1 -> long1, str2 -> long2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).longMap().update
          _ <- Ns.longMap.get.map(_ ==> List())


          // Can't apply pairs with duplicate keys

          _ <- Ns(eid).longMap(str1 -> long1, str1 -> long2).update
            .map(_ ==> "Unexpected success").recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
                "\na -> 1" +
                "\na -> 2"
          }

          _ <- Ns(eid).longMap(Seq(str1 -> long1, str1 -> long2)).update
            .map(_ ==> "Unexpected success").recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/longMap`:" +
                "\na -> 1" +
                "\na -> 2"
          }
        } yield ()
      }
    }
  }
}