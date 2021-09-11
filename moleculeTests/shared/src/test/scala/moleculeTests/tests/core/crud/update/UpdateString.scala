package moleculeTests.tests.core.crud.update

import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateString extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one values" - {

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.str("b").save.map(_.eid)

          // Apply value (retracts current value)
          _ <- Ns(eid).str("a").update
          _ <- Ns.str.get.map(_.head ==> "a")

          // Apply new value
          _ <- Ns(eid).str("b").update
          _ <- Ns.str.get.map(_.head ==> "b")

          // Delete value (apply no value)
          _ <- Ns(eid).str().update
          _ <- Ns.str.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).str("b", "c").update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... str(b, c)"
          }
        } yield ()
      }
    }


    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.str(str2).save.map(_.eid)

          // Apply value (retracts current value)
          _ <- Ns(eid).str(str1).update
          _ <- Ns.str.get.map(_.head ==> str1)

          // Apply new value
          _ <- Ns(eid).str(str2).update
          _ <- Ns.str.get.map(_.head ==> str2)

          // Delete value (apply no value)
          _ <- Ns(eid).str().update
          _ <- Ns.str.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).str(str2, str3).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... str($str2, $str3)"
          }
        } yield ()
      }
    }


    "Card-many values" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.strs("a").save.map(_.eid)

          // Assert value
          _ <- Ns(eid).strs.assert("b").update
          _ <- Ns.strs.get.map(_.head ==> Set("a", "b"))

          // Assert existing value (no effect)
          _ <- Ns(eid).strs.assert("b").update
          _ <- Ns.strs.get.map(_.head ==> Set("a", "b"))

          // Assert multiple values (vararg)
          _ <- Ns(eid).strs.assert("c", "d").update
          _ <- Ns.strs.get.map(_.head ==> Set("a", "b", "c", "d"))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).strs.assert(Seq("d", "e")).update
          _ <- Ns.strs.get.map(_.head ==> Set("a", "b", "c", "d", "e"))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).strs.assert(Seq[String]()).update
          _ <- Ns.strs.get.map(_.head ==> Set("a", "b", "c", "d", "e"))


          // Reset
          _ <- Ns(eid).strs().update

          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns(eid).strs.assert("a", "b", "b").update
          _ <- Ns.strs.get.map(_.head ==> Set("a", "b"))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.strs("a", "b", "c", "d", "e", "f").save.map(_.eid)

          // Replace value
          _ <- Ns(eid).strs.replace("f" -> "h").update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a", "b", "c", "d", "e", "h"))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).strs.replace("e" -> "h").update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a", "b", "c", "d", "h"))

          // Replace multiple values (vararg)
          _ <- Ns(eid).strs.replace("c" -> "f", "d" -> "g").update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a", "b", "f", "g", "h"))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).strs.replace("x" -> "i").update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a", "b", "f", "g", "h", "i"))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).strs.replace(Seq("b" -> "e")).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a", "e", "f", "g", "h", "i"))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).strs.replace(Seq[(String, String)]()).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a", "e", "f", "g", "h", "i"))


          // Can't replace duplicate values

          _ = expectCompileError("""Ns(eid).strs.replace("g" -> "h", "h" -> "h").update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/strs`:" +
              "\nh")

          _ = expectCompileError("""Ns(eid).strs.replace(Seq("g" -> "h", "h" -> "h")).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/strs`:" +
              "\nh")
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.strs("a", "b", "c", "d", "e", "f").save.map(_.eid)

          // Retract value
          _ <- Ns(eid).strs.retract("f").update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a", "b", "c", "d", "e"))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).strs.retract("g").update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a", "b", "c", "d", "e"))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).strs.retract("e", "e").update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a", "b", "c", "d"))

          // Retract multiple values (vararg)
          _ <- Ns(eid).strs.retract("c", "d").update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a", "b"))

          // Retract Seq of values
          _ <- Ns(eid).strs.retract(Seq("b")).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a"))

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).strs.retract(Seq[String]()).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a"))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.strs("b", "c").save.map(_.eid)

          // Apply value (retracts all current values!)
          _ <- Ns(eid).strs("a").update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("a"))

          // Apply multiple values (vararg)
          _ <- Ns(eid).strs("b", "c").update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("b", "c"))

          // Apply Seq of values
          _ <- Ns(eid).strs(Set("d")).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List("d"))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).strs(Set[String]()).update
          _ <- Ns.strs.get.map(_ ==> List())

          _ <- Ns(eid).strs(Set("a", "b")).update

          // Delete all (apply no values)
          _ <- Ns(eid).strs().update
          _ <- Ns.strs.get.map(_ ==> List())


          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns(eid).strs("a", "b", "b").update
          _ <- Ns.strs.get.map(_.head ==> Set("a", "b"))
        } yield ()
      }
    }


    "Card-many variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.strs(str1).save.map(_.eid)

          // Assert value
          _ <- Ns(eid).strs.assert(str2).update
          _ <- Ns.strs.get.map(_.head ==> Set(str1, str2))

          // Assert existing value (no effect)
          _ <- Ns(eid).strs.assert(str2).update
          _ <- Ns.strs.get.map(_.head ==> Set(str1, str2))

          // Assert multiple values
          _ <- Ns(eid).strs.assert(str3, str4).update
          _ <- Ns.strs.get.map(_.head ==> Set(str1, str2, str3, str4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).strs.assert(Seq(str4, str5)).update
          _ <- Ns.strs.get.map(_.head ==> Set(str1, str2, str3, str4, str5))

          // Assert Seq of values as variable (existing values unaffected)
          values = Seq(str6, str7)
          _ <- Ns(eid).strs.assert(values).update
          _ <- Ns.strs.get.map(_.head ==> Set(str1, str2, str3, str4, str5, str6, str7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).strs.assert(Seq[String]()).update
          _ <- Ns.strs.get.map(_.head ==> Set(str1, str2, str3, str4, str5, str6, str7))


          // Reset
          _ <- Ns(eid).strs().update

          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).strs.assert(str1, str2, str2).update
          _ <- Ns.strs.get.map(_.head ==> Set(str1, str2))

          // Equal values are coalesced (at runtime)
          other3 = str3
          _ <- Ns(eid).strs.assert(str2, str3, other3).update
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.strs(str1, str2, str3, str4, str5, str6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).strs.replace(str6 -> str8).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1, str2, str3, str4, str5, str8))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).strs.replace(str5 -> str8).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1, str2, str3, str4, str8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).strs.replace(str3 -> str6, str4 -> str7).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1, str2, str6, str7, str8))

          // Missing old value has no effect. The new value is inserted (upsert semantics)
          _ <- Ns(eid).strs.replace("x" -> str9).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1, str2, str6, str7, str8, str9))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).strs.replace(Seq(str2 -> str5)).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1, str5, str6, str7, str8, str9))

          // Replace with Seq of oldValue->newValue pairs as variable
          values = Seq(str1 -> str4)
          _ <- Ns(eid).strs.replace(values).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str4, str5, str6, str7, str8, str9))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).strs.replace(Seq[(String, String)]()).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str4, str5, str6, str7, str8, str9))


          // Can't replace duplicate values

          _ = expectCompileError("""Ns(eid).strs.replace(str7 -> str8, str8 -> str8).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/strs`:" +
              "\n__ident__str8")

          _ = expectCompileError("""Ns(eid).strs.replace(Seq(str7 -> str8, str8 -> str8)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/strs`:" +
              "\n__ident__str8")


          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          other8 = str8

          _ <- Ns(eid).strs.replace(str7 -> str8, str8 -> other8).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/strs`:" +
                "\nh"
          }

          // Conflicting new values
          _ <- Ns(eid).strs.replace(Seq(str7 -> str8, str8 -> other8)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/strs`:" +
                "\nh"
          }
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.strs(str1, str2, str3, str4, str5, str6).save.map(_.eid)

          // Retract value
          _ <- Ns(eid).strs.retract(str6).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1, str2, str3, str4, str5))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).strs.retract(str7).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1, str2, str3, str4, str5))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).strs.retract(str5, str5).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1, str2, str3, str4))

          // Retract multiple values (vararg)
          _ <- Ns(eid).strs.retract(str3, str4).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1, str2))

          // Retract Seq of values
          _ <- Ns(eid).strs.retract(Seq(str2)).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1))

          // Retract Seq of values as variable
          values = Seq(str1)
          _ <- Ns(eid).strs.retract(values).update
          _ <- Ns.strs.get.map(_ ==> List())

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).strs(str1).update
          _ <- Ns(eid).strs.retract(Seq[String]()).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.strs(str2, str3).save.map(_.eid)

          // Apply value (retracts all current values!)
          _ <- Ns(eid).strs(str1).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).strs(str2, str3).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str2, str3))

          // Apply Seq of values
          _ <- Ns(eid).strs(Set(str4)).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str4))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).strs(Set[String]()).update
          _ <- Ns.strs.get.map(_ ==> List())

          // Apply Seq of values as variable
          values = Set(str1, str2)
          _ <- Ns(eid).strs(values).update
          _ <- Ns.strs.get.map(_.head.toList.sorted ==> List(str1, str2))

          // Delete all (apply no values)
          _ <- Ns(eid).strs().update
          _ <- Ns.strs.get.map(_ ==> List())


          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).strs(str1, str2, str2).update
          _ <- Ns.strs.get.map(_.head ==> Set(str1, str2))

          // Equal values are coalesced (at runtime)
          other3 = str3
          _ <- Ns(eid).strs(str2, str3, other3).update
          _ <- Ns.strs.get.map(_.head ==> Set(str2, str3))
        } yield ()
      }
    }
  }
}