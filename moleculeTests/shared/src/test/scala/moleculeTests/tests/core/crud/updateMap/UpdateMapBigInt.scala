package moleculeTests.tests.core.crud.updateMap

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateMapBigInt extends AsyncTestSuite {

  lazy val tests = Tests {

    "Mapped variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.bigIntMap(str1 -> bigInt1).save.map(_.eid)

          // Add pair
          _ <- Ns(eid).bigIntMap.assert(str2 -> bigInt3).update
          _ <- Ns.bigIntMap.get.map(_.head ==> Map(str1 -> bigInt1, str2 -> bigInt3))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).bigIntMap.assert(str2 -> bigInt2).update
          _ <- Ns.bigIntMap.get.map(_.head ==> Map(str1 -> bigInt1, str2 -> bigInt2))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).bigIntMap.assert(str3 -> bigInt3, str4 -> bigInt4).update
          _ <- Ns.bigIntMap.get.map(_.head ==> Map(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt3, str4 -> bigInt4))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).bigIntMap.assert(Seq(str4 -> bigInt4, str5 -> bigInt5)).update
          _ <- Ns.bigIntMap.get.map(_.head ==> Map(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt3, str4 -> bigInt4, str5 -> bigInt5))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).bigIntMap.assert(Seq[(String, BigInt)]()).update
          _ <- Ns.bigIntMap.get.map(_.head ==> Map(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt3, str4 -> bigInt4, str5 -> bigInt5))


          // Can't add pairs with duplicate keys

          // vararg
          _ = expectCompileError("""Ns(eid).bigIntMap.assert(str1 -> bigInt1, str1 -> bigInt2).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/bigIntMap`:" +
              "\n__ident__str1 -> __ident__bigInt1" +
              "\n__ident__str1 -> __ident__bigInt2")

          // Seq
          _ = expectCompileError("""Ns(eid).bigIntMap.assert(Seq(str1 -> bigInt1, str1 -> bigInt2)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/bigIntMap`:" +
              "\n__ident__str1 -> __ident__bigInt1" +
              "\n__ident__str1 -> __ident__bigInt2")

          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          str1x = str1

          // vararg
          _ <- Ns(eid).bigIntMap.assert(str1 -> bigInt1, str1x -> bigInt2).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/bigIntMap`:" +
                "\na -> " + bigInt1 +
                "\na -> " + bigInt2
          }


          // Seq
          _ <- Ns(eid).bigIntMap.assert(Seq(str1 -> bigInt1, str1x -> bigInt2)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/bigIntMap`:" +
                "\na -> " + bigInt1 +
                "\na -> " + bigInt2
          }
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.bigIntMap(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt3, str4 -> bigInt4, str5 -> bigInt5, str6 -> bigInt6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).bigIntMap.replace(str6 -> bigInt8).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt3, str4 -> bigInt4, str5 -> bigInt5, str6 -> bigInt8))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).bigIntMap.replace(str5 -> bigInt8).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt3, str4 -> bigInt4, str5 -> bigInt8, str6 -> bigInt8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).bigIntMap.replace(str3 -> bigInt6, str4 -> bigInt7).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt6, str4 -> bigInt7, str5 -> bigInt8, str6 -> bigInt8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).bigIntMap.replace(str3 -> bigInt6, str4 -> bigInt7).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt6, str4 -> bigInt7, str5 -> bigInt8, str6 -> bigInt8))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).bigIntMap.replace(Seq(str2 -> bigInt5)).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1, str2 -> bigInt5, str3 -> bigInt6, str4 -> bigInt7, str5 -> bigInt8, str6 -> bigInt8))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).bigIntMap.replace(Seq[(String, BigInt)]()).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1, str2 -> bigInt5, str3 -> bigInt6, str4 -> bigInt7, str5 -> bigInt8, str6 -> bigInt8))


          // Can't replace pairs with duplicate keys

          _ = expectCompileError("""Ns(eid).bigIntMap.replace(str1 -> bigInt1, str1 -> bigInt2).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/bigIntMap`:" +
              "\n__ident__str1 -> __ident__bigInt1" +
              "\n__ident__str1 -> __ident__bigInt2")

          _ = expectCompileError("""Ns(eid).bigIntMap.replace(Seq(str1 -> bigInt1, str1 -> bigInt2)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/bigIntMap`:" +
              "\n__ident__str1 -> __ident__bigInt1" +
              "\n__ident__str1 -> __ident__bigInt2")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.bigIntMap(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt3, str4 -> bigInt4, str5 -> bigInt5, str6 -> bigInt6).save.map(_.eid)

          // Remove pair by key
          _ <- Ns(eid).bigIntMap.retract(str6).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt3, str4 -> bigInt4, str5 -> bigInt5))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).bigIntMap.retract(str7).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt3, str4 -> bigInt4, str5 -> bigInt5))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).bigIntMap.retract(str5, str5).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1, str2 -> bigInt2, str3 -> bigInt3, str4 -> bigInt4))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).bigIntMap.retract(str3, str4).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1, str2 -> bigInt2))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).bigIntMap.retract(Seq(str2)).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).bigIntMap.retract(Seq[String]()).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.bigIntMap(str1 -> bigInt1, str2 -> bigInt2).save.map(_.eid)

          // Apply value (replaces all current values!)
          _ <- Ns(eid).bigIntMap(str1 -> bigInt1).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str1 -> bigInt1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).bigIntMap(str2 -> bigInt2, str3 -> bigInt3).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str2 -> bigInt2, str3 -> bigInt3))

          // Apply Map of values
          _ <- Ns(eid).bigIntMap(Seq(str4 -> bigInt4)).update
          _ <- Ns.bigIntMap.get.map(_.head.toList.sortBy(_._1) ==> List(str4 -> bigInt4))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).bigIntMap(Seq[(String, BigInt)]()).update
          _ <- Ns.bigIntMap.get.map(_ ==> List())


          _ <- Ns(eid).bigIntMap(Seq(str1 -> bigInt1, str2 -> bigInt2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).bigIntMap().update
          _ <- Ns.bigIntMap.get.map(_ ==> List())


          // Can't apply pairs with duplicate keys

          _ <- Ns(eid).bigIntMap(str1 -> bigInt1, str1 -> bigInt2).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/bigIntMap`:" +
                "\na -> " + bigInt1 +
                "\na -> " + bigInt2
          }

          _ <- Ns(eid).bigIntMap(Seq(str1 -> bigInt1, str1 -> bigInt2)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/bigIntMap`:" +
                "\na -> " + bigInt1 +
                "\na -> " + bigInt2
          }
        } yield ()
      }
    }
  }
}
