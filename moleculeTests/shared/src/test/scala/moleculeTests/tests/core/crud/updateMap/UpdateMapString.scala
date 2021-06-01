package moleculeTests.tests.core.crud.updateMap

import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateMapString extends AsyncTestSuite {

  lazy val tests = Tests {

    "Mapped values" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.strMap("str1" -> "a").save.map(_.eid)

          // Add pair
          _ <- Ns(eid).strMap.assert("str2" -> "c").update
          _ <- Ns.strMap.get.map(_.head ==> Map("str1" -> "a", "str2" -> "c"))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).strMap.assert("str2" -> "b").update
          _ <- Ns.strMap.get.map(_.head ==> Map("str1" -> "a", "str2" -> "b"))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).strMap.assert("str3" -> "c", "str4" -> "d").update
          _ <- Ns.strMap.get.map(_.head ==> Map("str1" -> "a", "str2" -> "b", "str3" -> "c", "str4" -> "d"))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).strMap.assert(Seq("str4" -> "d", "str5" -> "e")).update
          _ <- Ns.strMap.get.map(_.head ==> Map("str1" -> "a", "str2" -> "b", "str3" -> "c", "str4" -> "d", "str5" -> "e"))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).strMap.assert(Seq[(String, String)]()).update
          _ <- Ns.strMap.get.map(_.head ==> Map("str1" -> "a", "str2" -> "b", "str3" -> "c", "str4" -> "d", "str5" -> "e"))


          // Can't add pairs with duplicate keys

          // vararg
          _ = compileError(            """Ns(eid).strMap.assert("str1" -> "a", "str1" -> "b").update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
              "\nstr1 -> a" +
              "\nstr1 -> b")

          // Seq
          _ = compileError(            """Ns(eid).strMap.assert(Seq("str1" -> "a", "str1" -> "b")).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
              "\nstr1 -> a" +
              "\nstr1 -> b")
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.strMap("str1" -> "a", "str2" -> "b", "str3" -> "c", "str4" -> "d", "str5" -> "e", "str6" -> "f").save.map(_.eid)

          // Replace value
          _ <- Ns(eid).strMap.replace("str6" -> "h").update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a", "str2" -> "b", "str3" -> "c", "str4" -> "d", "str5" -> "e", "str6" -> "h"))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).strMap.replace("str5" -> "h").update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a", "str2" -> "b", "str3" -> "c", "str4" -> "d", "str5" -> "h", "str6" -> "h"))

          // Replace multiple values (vararg)
          _ <- Ns(eid).strMap.replace("str3" -> "f", "str4" -> "g").update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a", "str2" -> "b", "str3" -> "f", "str4" -> "g", "str5" -> "h", "str6" -> "h"))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).strMap.replace("str3" -> "f", "str4" -> "g").update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a", "str2" -> "b", "str3" -> "f", "str4" -> "g", "str5" -> "h", "str6" -> "h"))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).strMap.replace(Seq("str2" -> "e")).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a", "str2" -> "e", "str3" -> "f", "str4" -> "g", "str5" -> "h", "str6" -> "h"))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).strMap.replace(Seq[(String, String)]()).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a", "str2" -> "e", "str3" -> "f", "str4" -> "g", "str5" -> "h", "str6" -> "h"))


          // Can't replace pairs with duplicate keys

          _ = compileError(            """Ns(eid).strMap.replace("str1" -> "a", "str1" -> "b").update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
              "\nstr1 -> a" +
              "\nstr1 -> b")

          _ = compileError(            """Ns(eid).strMap.replace(Seq("str1" -> "a", "str1" -> "b")).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
              "\nstr1 -> a" +
              "\nstr1 -> b")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.strMap("str1" -> "a", "str2" -> "b", "str3" -> "c", "str4" -> "d", "str5" -> "e", "str6" -> "f").save.map(_.eid)

          // Remove pair by key
          _ <- Ns(eid).strMap.retract("str6").update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a", "str2" -> "b", "str3" -> "c", "str4" -> "d", "str5" -> "e"))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).strMap.retract("str7").update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a", "str2" -> "b", "str3" -> "c", "str4" -> "d", "str5" -> "e"))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).strMap.retract("str5", "str5").update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a", "str2" -> "b", "str3" -> "c", "str4" -> "d"))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).strMap.retract("str3", "str4").update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a", "str2" -> "b"))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).strMap.retract(Seq("str2")).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a"))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).strMap.retract(Seq[String]()).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a"))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.strMap("str1" -> "a", "str2" -> "b").save.map(_.eid)

          // Apply value (replaces all current values!)
          _ <- Ns(eid).strMap("str1" -> "a").update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str1" -> "a"))

          // Apply multiple values (vararg)
          _ <- Ns(eid).strMap("str2" -> "b", "str3" -> "c").update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str2" -> "b", "str3" -> "c"))

          // Apply Map of values
          _ <- Ns(eid).strMap(Seq("str4" -> "d")).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List("str4" -> "d"))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).strMap(Seq[(String, String)]()).update
          _ <- Ns.strMap.get.map(_ ==> List())


          _ <- Ns(eid).strMap(Seq("str1" -> "a", "str2" -> "b")).update

          // Delete all (apply no values)
          _ <- Ns(eid).strMap().update
          _ <- Ns.strMap.get.map(_ ==> List())


          // Can't apply pairs with duplicate keys

          // vararg
          _ <- Ns(eid).strMap("str1" -> "a", "str1" -> "b").update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
                "\nstr1 -> a" +
                "\nstr1 -> b"
          }

          // Seq
          _ <- Ns(eid).strMap(Seq("str1" -> "a", "str1" -> "b")).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
                "\nstr1 -> a" +
                "\nstr1 -> b"
          }
        } yield ()
      }
    }


    "Mapped variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.strMap(str1 -> str1).save.map(_.eid)

          // Add pair
          _ <- Ns(eid).strMap.assert(str2 -> str3).update
          _ <- Ns.strMap.get.map(_.head ==> Map(str1 -> str1, str2 -> str3))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).strMap.assert(str2 -> str2).update
          _ <- Ns.strMap.get.map(_.head ==> Map(str1 -> str1, str2 -> str2))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).strMap.assert(str3 -> str3, str4 -> str4).update
          _ <- Ns.strMap.get.map(_.head ==> Map(str1 -> str1, str2 -> str2, str3 -> str3, str4 -> str4))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).strMap.assert(Seq(str4 -> str4, str5 -> str5)).update
          _ <- Ns.strMap.get.map(_.head ==> Map(str1 -> str1, str2 -> str2, str3 -> str3, str4 -> str4, str5 -> str5))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).strMap.assert(Seq[(String, String)]()).update
          _ <- Ns.strMap.get.map(_.head ==> Map(str1 -> str1, str2 -> str2, str3 -> str3, str4 -> str4, str5 -> str5))


          // Can't add pairs with duplicate keys

          // vararg
          _ = compileError(            """Ns(eid).strMap.assert(str1 -> str1, str1 -> str2).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
              "\n__ident__str1 -> __ident__str1" +
              "\n__ident__str1 -> __ident__str2")

          // Seq
          _ = compileError(            """Ns(eid).strMap.assert(Seq(str1 -> str1, str1 -> str2)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
              "\n__ident__str1 -> __ident__str1" +
              "\n__ident__str1 -> __ident__str2")

          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          str1x = str1

          // vararg
          _ <- Ns(eid).strMap.assert(str1 -> str1, str1x -> str2).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
                "\na -> a" +
                "\na -> b"
          }

          // Seq
          _ <- Ns(eid).strMap.assert(Seq(str1 -> str1, str1x -> str2)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
                "\na -> a" +
                "\na -> b"
          }
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.strMap(str1 -> str1, str2 -> str2, str3 -> str3, str4 -> str4, str5 -> str5, str6 -> str6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).strMap.replace(str6 -> str8).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1, str2 -> str2, str3 -> str3, str4 -> str4, str5 -> str5, str6 -> str8))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).strMap.replace(str5 -> str8).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1, str2 -> str2, str3 -> str3, str4 -> str4, str5 -> str8, str6 -> str8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).strMap.replace(str3 -> str6, str4 -> str7).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1, str2 -> str2, str3 -> str6, str4 -> str7, str5 -> str8, str6 -> str8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).strMap.replace(str3 -> str6, str4 -> str7).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1, str2 -> str2, str3 -> str6, str4 -> str7, str5 -> str8, str6 -> str8))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).strMap.replace(Seq(str2 -> str5)).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1, str2 -> str5, str3 -> str6, str4 -> str7, str5 -> str8, str6 -> str8))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).strMap.replace(Seq[(String, String)]()).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1, str2 -> str5, str3 -> str6, str4 -> str7, str5 -> str8, str6 -> str8))


          // Can't replace pairs with duplicate keys

          _ = compileError(            """Ns(eid).strMap.replace(str1 -> str1, str1 -> str2).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
              "\n__ident__str1 -> __ident__str1" +
              "\n__ident__str1 -> __ident__str2")

          _ = compileError(            """Ns(eid).strMap.replace(Seq(str1 -> str1, str1 -> str2)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
              "\n__ident__str1 -> __ident__str1" +
              "\n__ident__str1 -> __ident__str2")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.strMap(str1 -> str1, str2 -> str2, str3 -> str3, str4 -> str4, str5 -> str5, str6 -> str6).save.map(_.eid)

          // Remove pair by key
          _ <- Ns(eid).strMap.retract(str6).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1, str2 -> str2, str3 -> str3, str4 -> str4, str5 -> str5))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).strMap.retract(str7).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1, str2 -> str2, str3 -> str3, str4 -> str4, str5 -> str5))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).strMap.retract(str5, str5).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1, str2 -> str2, str3 -> str3, str4 -> str4))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).strMap.retract(str3, str4).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1, str2 -> str2))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).strMap.retract(Seq(str2)).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).strMap.retract(Seq[String]()).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.strMap(str1 -> str1, str2 -> str2).save.map(_.eid)

          // Apply value (replaces all current values!)
          _ <- Ns(eid).strMap(str1 -> str1).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str1 -> str1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).strMap(str2 -> str2, str3 -> str3).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str2 -> str2, str3 -> str3))

          // Apply Map of values
          _ <- Ns(eid).strMap(Seq(str4 -> str4)).update
          _ <- Ns.strMap.get.map(_.head.toList.sorted ==> List(str4 -> str4))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).strMap(Seq[(String, String)]()).update
          _ <- Ns.strMap.get.map(_ ==> List())


          _ <- Ns(eid).strMap(Seq(str1 -> str1, str2 -> str2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).strMap().update
          _ <- Ns.strMap.get.map(_ ==> List())


          // Can't apply pairs with duplicate keys

          // vararg
          _ <- Ns(eid).strMap(str1 -> str1, str1 -> str2).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
                "\na -> a" +
                "\na -> b"
          }

          // Seq
          _ <- Ns(eid).strMap(Seq(str1 -> str1, str1 -> str2)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/strMap`:" +
                "\na -> a" +
                "\na -> b"
          }
        } yield ()
      }
    }
  }
}