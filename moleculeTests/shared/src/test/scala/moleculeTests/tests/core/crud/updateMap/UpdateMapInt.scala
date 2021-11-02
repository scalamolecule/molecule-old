package moleculeTests.tests.core.crud.updateMap

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateMapInt extends AsyncTestSuite {

  lazy val tests = Tests {

    "Mapped values" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.intMap("str1" -> 1).save.map(_.eid)

          // Add pair
          _ <- Ns(eid).intMap.assert("str2" -> 3).update
          _ <- Ns.intMap.get.map(_.head ==> Map("str1" -> 1, "str2" -> 3))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).intMap.assert("str2" -> 2).update
          _ <- Ns.intMap.get.map(_.head ==> Map("str1" -> 1, "str2" -> 2))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).intMap.assert("str3" -> 3, "str4" -> 4).update
          _ <- Ns.intMap.get.map(_.head ==> Map("str1" -> 1, "str2" -> 2, "str3" -> 3, "str4" -> 4))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).intMap.assert(Seq("str4" -> 4, "str5" -> 5)).update
          _ <- Ns.intMap.get.map(_.head ==> Map("str1" -> 1, "str2" -> 2, "str3" -> 3, "str4" -> 4, "str5" -> 5))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).intMap.assert(Seq[(String, Int)]()).update
          _ <- Ns.intMap.get.map(_.head ==> Map("str1" -> 1, "str2" -> 2, "str3" -> 3, "str4" -> 4, "str5" -> 5))


          // Can't add pairs with duplicate keys

          // vararg
          _ = expectCompileError("""Ns(eid).intMap.assert("str1" -> 1, "str1" -> 2).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
              "\nstr1 -> 1" +
              "\nstr1 -> 2")

          // Seq
          _ = expectCompileError("""Ns(eid).intMap.assert(Seq("str1" -> 1, "str1" -> 2)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
              "\nstr1 -> 1" +
              "\nstr1 -> 2")
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.intMap("str1" -> 1, "str2" -> 2, "str3" -> 3, "str4" -> 4, "str5" -> 5, "str6" -> 6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).intMap.replace("str6" -> 8).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1, "str2" -> 2, "str3" -> 3, "str4" -> 4, "str5" -> 5, "str6" -> 8))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).intMap.replace("str5" -> 8).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1, "str2" -> 2, "str3" -> 3, "str4" -> 4, "str5" -> 8, "str6" -> 8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).intMap.replace("str3" -> 6, "str4" -> 7).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1, "str2" -> 2, "str3" -> 6, "str4" -> 7, "str5" -> 8, "str6" -> 8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).intMap.replace("str3" -> 6, "str4" -> 7).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1, "str2" -> 2, "str3" -> 6, "str4" -> 7, "str5" -> 8, "str6" -> 8))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).intMap.replace(Seq("str2" -> 5)).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1, "str2" -> 5, "str3" -> 6, "str4" -> 7, "str5" -> 8, "str6" -> 8))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).intMap.replace(Seq[(String, Int)]()).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1, "str2" -> 5, "str3" -> 6, "str4" -> 7, "str5" -> 8, "str6" -> 8))


          // Can't replace pairs with duplicate keys

          _ = expectCompileError("""Ns(eid).intMap.replace("str1" -> 1, "str1" -> 2).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
              "\nstr1 -> 1" +
              "\nstr1 -> 2")

          _ = expectCompileError("""Ns(eid).intMap.replace(Seq("str1" -> 1, "str1" -> 2)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
              "\nstr1 -> 1" +
              "\nstr1 -> 2")

          _ <- Ns(eid).intMap.replace("unknownKey" -> 42).update
            .map(_ ==> "Unexpected success").recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  " +
              "Can't replace non-existing keys of map attribute `:Ns/intMap`:" +
              "\nunknownKey" +
              "\nYou might want to apply the values instead to replace all current values?"
          }
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.intMap("str1" -> 1, "str2" -> 2, "str3" -> 3, "str4" -> 4, "str5" -> 5, "str6" -> 6).save.map(_.eid)

          // Remove pair by key
          _ <- Ns(eid).intMap.retract("str6").update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1, "str2" -> 2, "str3" -> 3, "str4" -> 4, "str5" -> 5))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).intMap.retract("str7").update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1, "str2" -> 2, "str3" -> 3, "str4" -> 4, "str5" -> 5))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).intMap.retract("str5", "str5").update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1, "str2" -> 2, "str3" -> 3, "str4" -> 4))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).intMap.retract("str3", "str4").update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1, "str2" -> 2))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).intMap.retract(Seq("str2")).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).intMap.retract(Seq[String]()).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.intMap("str1" -> 1, "str2" -> 2).save.map(_.eid)

          // Apply value (replaces all current values!)
          _ <- Ns(eid).intMap("str1" -> 1).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str1" -> 1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).intMap("str2" -> 2, "str3" -> 3).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str2" -> 2, "str3" -> 3))

          // Apply Map of values
          _ <- Ns(eid).intMap(Seq("str4" -> 4)).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List("str4" -> 4))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).intMap(Seq[(String, Int)]()).update
          _ <- Ns.intMap.get.map(_ ==> List())


          _ <- Ns(eid).intMap(Seq("str1" -> 1, "str2" -> 2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).intMap().update
          _ <- Ns.intMap.get.map(_ ==> List())


          // Can't apply pairs with duplicate keys

          // vararg
          _ <- Ns(eid).intMap("str1" -> 1, "str1" -> 2).update
            .map(_ ==> "Unexpected success").recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
              "\nstr1 -> 1" +
              "\nstr1 -> 2"
          }

          // Seq
          _ <- Ns(eid).intMap(Seq("str1" -> 1, "str1" -> 2)).update
            .map(_ ==> "Unexpected success").recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
              "\nstr1 -> 1" +
              "\nstr1 -> 2"
          }
        } yield ()
      }
    }

    "Mapped variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.intMap(str1 -> int1).save.map(_.eid)

          // Add pair
          _ <- Ns(eid).intMap.assert(str2 -> int3).update
          _ <- Ns.intMap.get.map(_.head ==> Map(str1 -> int1, str2 -> int3))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).intMap.assert(str2 -> int2).update
          _ <- Ns.intMap.get.map(_.head ==> Map(str1 -> int1, str2 -> int2))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).intMap.assert(str3 -> int3, str4 -> int4).update
          _ <- Ns.intMap.get.map(_.head ==> Map(str1 -> int1, str2 -> int2, str3 -> int3, str4 -> int4))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).intMap.assert(Seq(str4 -> int4, str5 -> int5)).update
          _ <- Ns.intMap.get.map(_.head ==> Map(str1 -> int1, str2 -> int2, str3 -> int3, str4 -> int4, str5 -> int5))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).intMap.assert(Seq[(String, Int)]()).update
          _ <- Ns.intMap.get.map(_.head ==> Map(str1 -> int1, str2 -> int2, str3 -> int3, str4 -> int4, str5 -> int5))


          // Can't add pairs with duplicate keys

          // vararg
          _ = expectCompileError("""Ns(eid).intMap.assert(str1 -> int1, str1 -> int2).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
              "\n__ident__str1 -> __ident__int1" +
              "\n__ident__str1 -> __ident__int2")

          // Seq
          _ = expectCompileError("""Ns(eid).intMap.assert(Seq(str1 -> int1, str1 -> int2)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
              "\n__ident__str1 -> __ident__int1" +
              "\n__ident__str1 -> __ident__int2")

          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          str1x = str1

          // vararg
          _ <- Ns(eid).intMap.assert(str1 -> int1, str1x -> int2).update
            .map(_ ==> "Unexpected success").recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
                "\na -> 1" +
                "\na -> 2"
          }


          // Seq
          _ <- Ns(eid).intMap.assert(Seq(str1 -> int1, str1x -> int2)).update
            .map(_ ==> "Unexpected success").recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
                "\na -> 1" +
                "\na -> 2"
          }
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.intMap(str1 -> int1, str2 -> int2, str3 -> int3, str4 -> int4, str5 -> int5, str6 -> int6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).intMap.replace(str6 -> int8).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1, str2 -> int2, str3 -> int3, str4 -> int4, str5 -> int5, str6 -> int8))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).intMap.replace(str5 -> int8).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1, str2 -> int2, str3 -> int3, str4 -> int4, str5 -> int8, str6 -> int8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).intMap.replace(str3 -> int6, str4 -> int7).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1, str2 -> int2, str3 -> int6, str4 -> int7, str5 -> int8, str6 -> int8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).intMap.replace(str3 -> int6, str4 -> int7).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1, str2 -> int2, str3 -> int6, str4 -> int7, str5 -> int8, str6 -> int8))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).intMap.replace(Seq(str2 -> int5)).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1, str2 -> int5, str3 -> int6, str4 -> int7, str5 -> int8, str6 -> int8))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).intMap.replace(Seq[(String, Int)]()).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1, str2 -> int5, str3 -> int6, str4 -> int7, str5 -> int8, str6 -> int8))


          // Can't replace pairs with duplicate keys

          _ = expectCompileError("""Ns(eid).intMap.replace(str1 -> int1, str1 -> int2).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
              "\n__ident__str1 -> __ident__int1" +
              "\n__ident__str1 -> __ident__int2")

          _ = expectCompileError("""Ns(eid).intMap.replace(Seq(str1 -> int1, str1 -> int2)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
              "\n__ident__str1 -> __ident__int1" +
              "\n__ident__str1 -> __ident__int2")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.intMap(str1 -> int1, str2 -> int2, str3 -> int3, str4 -> int4, str5 -> int5, str6 -> int6).save.map(_.eid)

          // Remove pair by key
          _ <- Ns(eid).intMap.retract(str6).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1, str2 -> int2, str3 -> int3, str4 -> int4, str5 -> int5))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).intMap.retract(str7).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1, str2 -> int2, str3 -> int3, str4 -> int4, str5 -> int5))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).intMap.retract(str5, str5).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1, str2 -> int2, str3 -> int3, str4 -> int4))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).intMap.retract(str3, str4).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1, str2 -> int2))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).intMap.retract(Seq(str2)).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).intMap.retract(Seq[String]()).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.intMap(str1 -> int1, str2 -> int2).save.map(_.eid)

          // Apply value (replaces all current values!)
          _ <- Ns(eid).intMap(str1 -> int1).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str1 -> int1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).intMap(str2 -> int2, str3 -> int3).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str2 -> int2, str3 -> int3))

          // Apply Map of values
          _ <- Ns(eid).intMap(Seq(str4 -> int4)).update
          _ <- Ns.intMap.get.map(_.head.toList.sorted ==> List(str4 -> int4))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).intMap(Seq[(String, Int)]()).update
          _ <- Ns.intMap.get.map(_ ==> List())


          _ <- Ns(eid).intMap(Seq(str1 -> int1, str2 -> int2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).intMap().update
          _ <- Ns.intMap.get.map(_ ==> List())


          // Can't apply pairs with duplicate keys

          // vararg
          _ <- Ns(eid).intMap(str1 -> int1, str1 -> int2).update
            .map(_ ==> "Unexpected success").recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
                "\na -> 1" +
                "\na -> 2"
          }

          // Seq
          _ <- Ns(eid).intMap(Seq(str1 -> int1, str1 -> int2)).update
            .map(_ ==> "Unexpected success").recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/intMap`:" +
                "\na -> 1" +
                "\na -> 2"
          }
        } yield ()
      }
    }
  }
}