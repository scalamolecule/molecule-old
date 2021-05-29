package moleculeTests.tests.core.crud.updateMap

import java.net.URI
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateMapURI extends AsyncTestSuite {

  lazy val tests = Tests {

    "Mapped variables" - {

      "assert" - core { implicit conn =>
        for {
          tx <- Ns.uriMap(str1 -> uri1).save
          eid = tx.eid

          // Add pair
          _ <- Ns(eid).uriMap.assert(str2 -> uri3).update
          _ <- Ns.uriMap.get.map(_.head ==> Map(str1 -> uri1, str2 -> uri3))

          // Add pair at existing key - replaces the value (not the key)
          _ <- Ns(eid).uriMap.assert(str2 -> uri2).update
          _ <- Ns.uriMap.get.map(_.head ==> Map(str1 -> uri1, str2 -> uri2))

          // Add multiple pairs (vararg)
          _ <- Ns(eid).uriMap.assert(str3 -> uri3, str4 -> uri4).update
          _ <- Ns.uriMap.get.map(_.head ==> Map(str1 -> uri1, str2 -> uri2, str3 -> uri3, str4 -> uri4))

          // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
          _ <- Ns(eid).uriMap.assert(Seq(str4 -> uri4, str5 -> uri5)).update
          _ <- Ns.uriMap.get.map(_.head ==> Map(str1 -> uri1, str2 -> uri2, str3 -> uri3, str4 -> uri4, str5 -> uri5))

          // Add empty Map of pair (no effect)
          _ <- Ns(eid).uriMap.assert(Seq[(String, URI)]()).update
          _ <- Ns.uriMap.get.map(_.head ==> Map(str1 -> uri1, str2 -> uri2, str3 -> uri3, str4 -> uri4, str5 -> uri5))


          // Can't add pairs with duplicate keys

          // vararg
          _ = compileError(            """Ns(eid).uriMap.assert(str1 -> uri1, str1 -> uri2).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/uriMap`:" +
              "\n__ident__str1 -> __ident__uri1" +
              "\n__ident__str1 -> __ident__uri2")

          // Seq
          _ = compileError(            """Ns(eid).uriMap.assert(Seq(str1 -> uri1, str1 -> uri2)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't assert multiple key/value pairs with the same key for attribute `:Ns/uriMap`:" +
              "\n__ident__str1 -> __ident__uri1" +
              "\n__ident__str1 -> __ident__uri2")

          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          str1x = str1

          // vararg
          _ <- Ns(eid).uriMap.assert(str1 -> uri1, str1x -> uri2).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/uriMap`:" +
                "\na -> " + uri1 +
                "\na -> " + uri2
          }


          // Seq
          _ <- Ns(eid).uriMap.assert(Seq(str1 -> uri1, str1x -> uri2)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't assert multiple key/value pairs with the same key for attribute `:Ns/uriMap`:" +
                "\na -> " + uri1 +
                "\na -> " + uri2
          }
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx <- Ns.uriMap(str1 -> uri1, str2 -> uri2, str3 -> uri3, str4 -> uri4, str5 -> uri5, str6 -> uri6).save
          eid = tx.eid

          // Replace value
          _ <- Ns(eid).uriMap.replace(str6 -> uri8).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1, str2 -> uri2, str3 -> uri3, str4 -> uri4, str5 -> uri5, str6 -> uri8))

          // Replace value to existing value at another key is ok
          _ <- Ns(eid).uriMap.replace(str5 -> uri8).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1, str2 -> uri2, str3 -> uri3, str4 -> uri4, str5 -> uri8, str6 -> uri8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).uriMap.replace(str3 -> uri6, str4 -> uri7).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1, str2 -> uri2, str3 -> uri6, str4 -> uri7, str5 -> uri8, str6 -> uri8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).uriMap.replace(str3 -> uri6, str4 -> uri7).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1, str2 -> uri2, str3 -> uri6, str4 -> uri7, str5 -> uri8, str6 -> uri8))

          // Replace with Seq of key/newValue pairs
          _ <- Ns(eid).uriMap.replace(Seq(str2 -> uri5)).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1, str2 -> uri5, str3 -> uri6, str4 -> uri7, str5 -> uri8, str6 -> uri8))

          // Replacing with empty Seq of key/newValue mapped values has no effect
          _ <- Ns(eid).uriMap.replace(Seq[(String, URI)]()).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1, str2 -> uri5, str3 -> uri6, str4 -> uri7, str5 -> uri8, str6 -> uri8))


          // Can't replace pairs with duplicate keys

          _ = compileError(            """Ns(eid).uriMap.replace(str1 -> uri1, str1 -> uri2).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/uriMap`:" +
              "\n__ident__str1 -> __ident__uri1" +
              "\n__ident__str1 -> __ident__uri2")

          _ = compileError(            """Ns(eid).uriMap.replace(Seq(str1 -> uri1, str1 -> uri2)).update""").check("",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace multiple key/value pairs with the same key for attribute `:Ns/uriMap`:" +
              "\n__ident__str1 -> __ident__uri1" +
              "\n__ident__str1 -> __ident__uri2")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx <- Ns.uriMap(str1 -> uri1, str2 -> uri2, str3 -> uri3, str4 -> uri4, str5 -> uri5, str6 -> uri6).save
          eid = tx.eid

          // Remove pair by key
          _ <- Ns(eid).uriMap.retract(str6).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1, str2 -> uri2, str3 -> uri3, str4 -> uri4, str5 -> uri5))

          // Removing pair by non-existing key has no effect
          _ <- Ns(eid).uriMap.retract(str7).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1, str2 -> uri2, str3 -> uri3, str4 -> uri4, str5 -> uri5))

          // Removing duplicate keys removes the distinct key
          _ <- Ns(eid).uriMap.retract(str5, str5).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1, str2 -> uri2, str3 -> uri3, str4 -> uri4))

          // Remove pairs by multiple keys (vararg)
          _ <- Ns(eid).uriMap.retract(str3, str4).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1, str2 -> uri2))

          // Remove pairs by Seq of keys
          _ <- Ns(eid).uriMap.retract(Seq(str2)).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1))

          // Removing pairs by empty Seq of keys has no effect
          _ <- Ns(eid).uriMap.retract(Seq[String]()).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx <- Ns.uriMap(str1 -> uri1, str2 -> uri2).save
          eid = tx.eid

          // Apply value (replaces all current values!)
          _ <- Ns(eid).uriMap(str1 -> uri1).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str1 -> uri1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).uriMap(str2 -> uri2, str3 -> uri3).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str2 -> uri2, str3 -> uri3))

          // Apply Map of values
          _ <- Ns(eid).uriMap(Seq(str4 -> uri4)).update
          _ <- Ns.uriMap.get.map(_.head.toList.sorted ==> List(str4 -> uri4))

          // Apply empty Map of values (retracting all values!)
          _ <- Ns(eid).uriMap(Seq[(String, URI)]()).update
          _ <- Ns.uriMap.get.map(_ ==> List())


          _ <- Ns(eid).uriMap(Seq(str1 -> uri1, str2 -> uri2)).update

          // Delete all (apply no values)
          _ <- Ns(eid).uriMap().update
          _ <- Ns.uriMap.get.map(_ ==> List())


          // Can't apply pairs with duplicate keys

          _ <- Ns(eid).uriMap(str1 -> uri1, str1 -> uri2).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/uriMap`:" +
                "\na -> " + uri1 +
                "\na -> " + uri2
          }

          _ <- Ns(eid).uriMap(Seq(str1 -> uri1, str1 -> uri2)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:Ns/uriMap`:" +
                "\na -> " + uri1 +
                "\na -> " + uri2
          }
        } yield ()
      }
    }
  }
}
