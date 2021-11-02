package moleculeTests.tests.core.crud.update

import java.util.UUID
import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out1._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object UpdateUUID extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.uuid(uuid2).save.map(_.eid)

          // Apply value (retracts current value)
          _ <- Ns(eid).uuid(uuid1).update
          _ <- Ns.uuid.get.map(_.head ==> uuid1)

          // Apply new value
          _ <- Ns(eid).uuid(uuid2).update
          _ <- Ns.uuid.get.map(_.head ==> uuid2)

          // Delete value (apply no value)
          _ <- Ns(eid).uuid().update
          _ <- Ns.uuid.get.map(_ ==> List())

          // Applying multiple values to card-one attribute not allowed

          _ <- Ns(eid).uuid(uuid2, uuid3).update
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              s"\n  Ns ... uuid($uuid2, $uuid3)"
          }
        } yield ()
      }
    }


    "Card-many variables" - {

      "assert" - core { implicit conn =>
        for {
          eid <- Ns.uuids(uuid1).save.map(_.eid)

          // Assert value
          _ <- Ns(eid).uuids.assert(uuid2).update
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid1, uuid2))

          // Assert existing value (no effect)
          _ <- Ns(eid).uuids.assert(uuid2).update
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid1, uuid2))

          // Assert multiple values
          _ <- Ns(eid).uuids.assert(uuid3, uuid4).update
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid1, uuid2, uuid3, uuid4))

          // Assert Seq of values (existing values unaffected)
          _ <- Ns(eid).uuids.assert(Seq(uuid4, uuid5)).update
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid1, uuid2, uuid3, uuid4, uuid5))

          // Assert Seq of values as variable (existing values unaffected)
          values = Seq(uuid6, uuid7)
          _ <- Ns(eid).uuids.assert(values).update
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6, uuid7))

          // Assert empty Seq of values (no effect)
          _ <- Ns(eid).uuids.assert(Seq[UUID]()).update
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6, uuid7))


          // Reset
          _ <- Ns(eid).uuids().update

          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).uuids.assert(uuid1, uuid2, uuid2).update
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid1, uuid2))

          // Equal values are coalesced (at runtime)
          other3 = uuid3
          _ <- Ns(eid).uuids.assert(uuid2, uuid3, other3).update
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid3, uuid2, uuid1))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          eid <- Ns.uuids(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6).save.map(_.eid)

          // Replace value
          _ <- Ns(eid).uuids.replace(uuid6 -> uuid8).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid1, uuid2, uuid3, uuid4, uuid5, uuid8))

          // Replace value to existing value simply retracts it
          _ <- Ns(eid).uuids.replace(uuid5 -> uuid8).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid1, uuid2, uuid3, uuid4, uuid8))

          // Replace multiple values (vararg)
          _ <- Ns(eid).uuids.replace(uuid3 -> uuid6, uuid4 -> uuid7).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid1, uuid2, uuid6, uuid7, uuid8))

          // Replace with Seq of oldValue->newValue pairs
          _ <- Ns(eid).uuids.replace(Seq(uuid2 -> uuid5)).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid1, uuid5, uuid6, uuid7, uuid8))

          // Replace with Seq of oldValue->newValue pairs as variable
          values = Seq(uuid1 -> uuid4)
          _ <- Ns(eid).uuids.replace(values).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid4, uuid5, uuid6, uuid7, uuid8))

          // Replacing with empty Seq of oldValue->newValue pairs has no effect
          _ <- Ns(eid).uuids.replace(Seq[(UUID, UUID)]()).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid4, uuid5, uuid6, uuid7, uuid8))


          // Can't replace duplicate values

          _ = expectCompileError("""Ns(eid).uuids.replace(uuid7 -> uuid8, uuid8 -> uuid8).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/uuids`:" +
              "\n__ident__uuid8")

          _ = expectCompileError("""Ns(eid).uuids.replace(Seq(uuid7 -> uuid8, uuid8 -> uuid8)).update""",
            "molecule.core.ops.exception.VerifyRawModelException: Can't replace with duplicate values of attribute `:Ns/uuids`:" +
              "\n__ident__uuid8")


          // If duplicate values are added with non-equally-named variables we can still catch them at runtime
          other8 = uuid8

          _ <- Ns(eid).uuids.replace(uuid7 -> uuid8, uuid8 -> other8).update
            .map(_ ==> "Unexpected success").recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/uuids`:" +
              "\n" + uuid8
          }

          // Conflicting new values
          _ <- Ns(eid).uuids.replace(Seq(uuid7 -> uuid8, uuid8 -> other8)).update
            .map(_ ==> "Unexpected success").recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:default]  Can't replace with duplicate new values of attribute `:Ns/uuids`:" +
              "\n" + uuid8
          }
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          eid <- Ns.uuids(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6).save.map(_.eid)

          // Retract value
          _ <- Ns(eid).uuids.retract(uuid6).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

          // Retracting non-existing value has no effect
          _ <- Ns(eid).uuids.retract(uuid7).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))

          // Retracting duplicate values removes the distinc value
          _ <- Ns(eid).uuids.retract(uuid5, uuid5).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid1, uuid2, uuid3, uuid4))

          // Retract multiple values (vararg)
          _ <- Ns(eid).uuids.retract(uuid3, uuid4).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid1, uuid2))

          // Retract Seq of values
          _ <- Ns(eid).uuids.retract(Seq(uuid2)).update
          _ <- Ns.uuids.get.map(_.head.toList ==> List(uuid1))

          // Retract Seq of values as variable
          values = Seq(uuid1)
          _ <- Ns(eid).uuids.retract(values).update
          _ <- Ns.uuids.get.map(_ ==> List())

          // Retracting empty Seq of values has no effect
          _ <- Ns(eid).uuids(uuid1).update
          _ <- Ns(eid).uuids.retract(Seq[UUID]()).update
          _ <- Ns.uuids.get.map(_.head.toList ==> List(uuid1))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          eid <- Ns.uuids(uuid2, uuid3).save.map(_.eid)

          // Apply value (retracts all current values!)
          _ <- Ns(eid).uuids(uuid1).update
          _ <- Ns.uuids.get.map(_.head.toList ==> List(uuid1))

          // Apply multiple values (vararg)
          _ <- Ns(eid).uuids(uuid2, uuid3).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid2, uuid3))

          // Apply Seq of values
          _ <- Ns(eid).uuids(Set(uuid4)).update
          _ <- Ns.uuids.get.map(_.head.toList ==> List(uuid4))

          // Apply empty Seq of values (retracting all values!)
          _ <- Ns(eid).uuids(Set[UUID]()).update
          _ <- Ns.uuids.get.map(_ ==> List())

          // Apply Seq of values as variable
          values = Set(uuid1, uuid2)
          _ <- Ns(eid).uuids(values).update
          _ <- Ns.uuids.get.map(_.head.toList.sortBy(_.toString) ==> List(uuid1, uuid2))

          // Delete all (apply no values)
          _ <- Ns(eid).uuids().update
          _ <- Ns.uuids.get.map(_ ==> List())


          // Redundant duplicate values are discarded

          // Equally named variables are coalesced (at compile time)
          _ <- Ns(eid).uuids(uuid1, uuid2, uuid2).update
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid1, uuid2))

          // Equal values are coalesced (at runtime)
          other3 = uuid3
          _ <- Ns(eid).uuids(uuid2, uuid3, other3).update
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid2, uuid3))
        } yield ()
      }
    }
  }
}
