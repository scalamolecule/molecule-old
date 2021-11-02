package moleculeTests.tests.core.bidirectionals.self

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out3._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object OneSelf extends AsyncTestSuite {

  val spouses = m(Person.name.Spouse.name)

  lazy val tests = Tests {

    "Save new" - bidirectional { implicit conn =>
      for {
        // Save Adam, Lisa and bidirectional references between them
        List(adam, lisa) <- Person.name("Adam").Spouse.name("Lisa").save.map(_.eids)

        // Reference is bidirectional - both point to each other
        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
          ("Adam", "Lisa"),
          // Reverse reference:
          ("Lisa", "Adam")
        ))

        _ <- Person(adam).Spouse.name.get.map(_.head ==> "Lisa")
        _ <- Person(lisa).Spouse.name.get.map(_.head ==> "Adam")

        // Forth and back should bring us to the initial value (given Adam cardinality one ref)
        _ <- Person(adam).Spouse.name_.Spouse.name.get.map(_.head ==> "Adam")
        _ <- Person(lisa).Spouse.name_.Spouse.name.get.map(_.head ==> "Lisa")
      } yield ()
    }

    "Save id" - bidirectional { implicit conn =>
      for {
        lisa <- Person.name.insert("Lisa").map(_.eid)

        // Save Adam with bidirectional ref to existing  Lisa
        adam <- Person.name("Adam").spouse(lisa).save.map(_.eid)

        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
          ("Adam", "Lisa"),
          ("Lisa", "Adam")
        ))

        // Saving reference to generic `e` not allowed.
        // (instead apply ref to ref attribute as shown above)
        _ <- Person.name("Adam").Spouse.e(lisa).save
          .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
          err ==> s"[noGenerics]  Generic elements `e`, `a`, `v`, `ns`, `tx`, `t`, `txInstant` and `op` " +
            s"not allowed in save molecules. Found `e($lisa)`"
        }
      } yield ()
    }

    "Insert new" - bidirectional { implicit conn =>
      for {
        // Insert 2 pairs of bidirectionally referenced entities
        _ <- Person.name.Spouse.name insert List(
          ("Adam", "Lisa"),
          ("John", "Nina")
        )

        // Bidirectional references have been inserted
        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
          ("Adam", "Lisa"),
          ("John", "Nina"),
          // Reverse references:
          ("Lisa", "Adam"),
          ("Nina", "John")
        ))
      } yield ()
    }

    "Insert id" - bidirectional { implicit conn =>
      for {
        List(lisa, nina) <- Person.name insert List("Lisa", "Nina") map (_.eids)

        // Insert 2 new entities and pair them with existing entities
        _ <- Person.name.spouse insert List(
          ("Adam", lisa),
          ("John", nina)
        )

        // Bidirectional references have been inserted
        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
          ("Adam", "Lisa"),
          ("John", "Nina"),
          ("Lisa", "Adam"),
          ("Nina", "John")
        ))
      } yield ()
    }


    "Update new" - {

      "creating ref to new" - bidirectional { implicit conn =>
        for {
          adam <- Person.name.insert("Adam").map(_.eid)

          // Update Adam with creation of Lisa and bidirectional reference between Adam and Lisa
          _ <- Person(adam).Spouse.name("Lisa").update

          _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
            ("Adam", "Lisa"),
            ("Lisa", "Adam")
          ))
        } yield ()
      }

      "replacing ref to new" - bidirectional { implicit conn =>
        for {
          List(adam, lisa) <- Person.name("Adam").Spouse.name("Lisa").save.map(_.eids)

          // Bidirectional references created
          _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
            ("Adam", "Lisa"),
            ("Lisa", "Adam")
          ))

          // Update Adam with creation of Nina and replacing bidirectional reference with Lisa to created Nina
          _ <- Person(adam).Spouse.name("Nina").update

          // Bidirectional references to Lisa have been replaced with refs to/from Nina
          _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
            ("Adam", "Nina"),
            ("Nina", "Adam")
          ))
        } yield ()
      }
    }


    "Update id" - {

      "creating ref to existing" - bidirectional { implicit conn =>
        for {
          // Adam and Lisa not married yet
          adam <- Person.name.insert("Adam").map(_.eid)
          lisa <- Person.name.insert("Lisa").map(_.eid)

          // Update Adam with creation of bidirectional reference to existing Lisa
          _ <- Person(adam).spouse(lisa).update

          _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
            ("Adam", "Lisa"),
            ("Lisa", "Adam")
          ))

          // Referencing the same id is not allowed
          _ <- Person(adam).spouse(adam).update
            .map(_ ==> "Unexpected success").recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:biSelfRef]  Current entity and referenced entity ids can't be the same."
          }
        } yield ()
      }

      "replacing ref to other existing" - bidirectional { implicit conn =>
        for {
          List(adam, lisa) <- Person.name("Adam").Spouse.name("Lisa").save.map(_.eids)

          // Bidirectional references created
          _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
            ("Adam", "Lisa"),
            ("Lisa", "Adam")
          ))

          // Update Adam, replacing bidirectional reference with Lisa to existing Nina
          nina <- Person.name.insert("Nina").map(_.eid)
          _ <- Person(adam).spouse(nina).update

          // Bidirectional references to Lisa have been replaced with refs to/from Nina
          _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
            ("Adam", "Nina"),
            ("Nina", "Adam")
          ))
        } yield ()
      }
    }


    "Update removing reference" - bidirectional { implicit conn =>
      for {
        List(adam, lisa) <- Person.name("Adam").Spouse.name("Lisa").save.map(_.eids)

        // Bidirectional references created
        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
          ("Adam", "Lisa"),
          ("Lisa", "Adam")
        ))

        // Retract ref between them by applying no value
        _ <- Person(adam).spouse().update

        // Bidirectional references retracted
        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List())
      } yield ()
    }

    "Retract" - bidirectional { implicit conn =>
      for {
        adam <- Person.name.insert("Adam").map(_.eid)

        // Create and reference Lisa to Adam
        lisa <- Person(adam).Spouse.name("Lisa").update.map(_.eid)

        _ <- Person(adam).Spouse.name.get.map(_ ==> List("Lisa"))
        _ <- Person(lisa).Spouse.name.get.map(_ ==> List("Adam"))

        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
          ("Adam", "Lisa"),
          ("Lisa", "Adam")
        ))

        // Retract Adam and all references to/from Adam
        _ <- adam.retract

        // Lisa remains and both references retracted
        _ <- Person.name.get.map(_ ==> List("Lisa"))
        _ <- Person(adam).Spouse.name.get.map(_ ==> List())
        _ <- Person(lisa).Spouse.name.get.map(_ ==> List())
        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List())
      } yield ()
    }
  }
}
