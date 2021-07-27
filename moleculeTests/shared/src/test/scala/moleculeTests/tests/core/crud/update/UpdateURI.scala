package moleculeTests.tests.core.crud.update

import java.net.URI
import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateURI extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.uri(uri2).save.map(_.eid)

          // Apply value (retracts current value)
          _ <- Ns(eid).uri(uri1).update
          _ <- Ns.uri.get.map(_.head ==> uri1)

          // Apply new value
          _ <- Ns(eid).uri(uri2).update
          _ <- Ns.uri.get.map(_.head ==> uri2)

          // Delete value (apply no value)
          _ <- Ns(eid).uri().update
          _ <- Ns.uri.get.map(_ ==> List())


          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).uri(uri2, uri3).update.recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... uri($uri2, $uri3)"
          }
        } yield ()
      }
    }

    "Card-many variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.uris(uri1).save.map(_.eid)

          // Assert value
          _ <- Ns(eid).uris.assert(uri2).update
          _ <- Ns.uris.get.map(_.head ==> Set(uri1, uri2))

          // Assert existing value (no effect)
          _ <- Ns(eid).uris.assert(uri2).update
          _ <- Ns.uris.get.map(_.head ==> Set(uri1, uri2))

          // Assert multiple values
          _ <- Ns(eid).uris.assert(uri3, uri4).update
          _ <- Ns.uris.get.map(_.head ==> Set(uri1, uri2, uri3, uri4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).uris.assert(Seq(uri4, uri5)).update
          _ <- Ns.uris.get.map(_.head ==> Set(uri1, uri2, uri3, uri4, uri5))

          // Assert Seq of values as variable (existing values unaffected)
          values = Seq(uri6, uri7)
          _ <- Ns(eid).uris.assert(values).update
          _ <- Ns.uris.get.map(_.head ==> Set(uri1, uri2, uri3, uri4, uri5, uri6, uri7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).uris.assert(Seq[URI]()).update
          _ <- Ns.uris.get.map(_.head ==> Set(uri1, uri2, uri3, uri4, uri5, uri6, uri7))


          // Reset
          _ <- Ns(eid).uris().update

          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).uris.assert(uri1, uri2, uri2).update
          _ <- Ns.uris.get.map(_.head ==> Set(uri1, uri2))

          // Equal values are coalesced (at runtime)
          other3 = uri3
          _ <- Ns(eid).uris.assert(uri2, uri3, other3).update
          _ <- Ns.uris.get.map(_.head ==> Set(uri3, uri2, uri1))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.uris(uri1, uri2, uri3, uri4, uri5, uri6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).uris.replace(uri6 -> uri8).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1, uri2, uri3, uri4, uri5, uri8))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).uris.replace(uri5 -> uri8).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1, uri2, uri3, uri4, uri8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).uris.replace(uri3 -> uri6, uri4 -> uri7).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1, uri2, uri6, uri7, uri8))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).uris.replace(Seq(uri2 -> uri5)).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1, uri5, uri6, uri7, uri8))
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1, uri5, uri6, uri7, uri8))

          // Replace with Seq of oldValue->newValue pairs as variable
          values = Seq(uri1 -> uri4)
          _ <- Ns(eid).uris.replace(values).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri4, uri5, uri6, uri7, uri8))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).uris.replace(Seq[(URI, URI)]()).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri4, uri5, uri6, uri7, uri8))


          // Can't replace duplicate values

          _ = expectCompileError("""Ns(eid).uris.replace(uri7 -> uri8, uri8 -> uri8).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/uris`:" +
              "\n__ident__uri8")

          _ = expectCompileError("""Ns(eid).uris.replace(Seq(uri7 -> uri8, uri8 -> uri8)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/uris`:" +
              "\n__ident__uri8")


          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          other8 = uri8

          _ <- Ns(eid).uris.replace(uri7 -> uri8, uri8 -> other8).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/uris`:" +
                "\nuri8"
          }

          // Conflicting new values
          _ <- Ns(eid).uris.replace(Seq(uri7 -> uri8, uri8 -> other8)).update.recover {
            case Model2TransactionException(err) =>
              err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/uris`:" +
                "\nuri8"
          }
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.uris(uri1, uri2, uri3, uri4, uri5, uri6).save.map(_.eid)

          // Retract value
          _ <- Ns(eid).uris.retract(uri6).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1, uri2, uri3, uri4, uri5))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).uris.retract(uri7).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1, uri2, uri3, uri4, uri5))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).uris.retract(uri5, uri5).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1, uri2, uri3, uri4))

          // Retract multiple values (vararg)
          _ <- Ns(eid).uris.retract(uri3, uri4).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1, uri2))

          // Retract Seq of values
          _ <- Ns(eid).uris.retract(Seq(uri2)).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1))

          // Retract Seq of values as variable
          values = Seq(uri1)
          _ <- Ns(eid).uris.retract(values).update
          _ <- Ns.uris.get.map(_ ==> List())

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).uris(uri1).update
          _ <- Ns(eid).uris.retract(Seq[URI]()).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.uris(uri2, uri3).save.map(_.eid)

          // Apply value (retracts all current values!)
          _ <- Ns(eid).uris(uri1).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).uris(uri2, uri3).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri2, uri3))

          // Apply Seq of values
          _ <- Ns(eid).uris(Set(uri4)).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri4))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).uris(Set[URI]()).update
          _ <- Ns.uris.get.map(_ ==> List())

          // Apply Seq of values as variable
          values = Set(uri1, uri2)
          _ <- Ns(eid).uris(values).update
          _ <- Ns.uris.get.map(_.head.toList.sorted ==> List(uri1, uri2))

          // Delete all (apply no values)
          _ <- Ns(eid).uris().update
          _ <- Ns.uris.get.map(_ ==> List())


          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).uris(uri1, uri2, uri2).update
          _ <- Ns.uris.get.map(_.head ==> Set(uri1, uri2))

          // Equal values are coalesced (at runtime)
          other3 = uri3
          _ <- Ns(eid).uris(uri2, uri3, other3).update
          _ <- Ns.uris.get.map(_.head ==> Set(uri2, uri3))
        } yield ()
      }
    }
  }
}
