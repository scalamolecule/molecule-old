package moleculeTests.tests.core.bidirectionals.self

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out3._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object OneSelf extends AsyncTestSuite {

  val spouses = m(Person.name.Spouse.name)

  lazy val tests = Tests {

    "Save new" - bidirectional { implicit conn =>
      for {
        // Save Adam, Lisa and bidirectional references between them
        tx <- Person.name("Adam").Spouse.name("Lisa").save
        List(adam, lisa) = tx.eids

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
        tx <- Person.name.insert("Lisa")
        lisa = tx.eid

        // Save Adam with bidirectional ref to existing  Lisa
        tx2 <- Person.name("Adam").spouse(lisa).save
        adam = tx2.eid

        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
          ("Adam", "Lisa"),
          ("Lisa", "Adam")
        ))

        // Saving reference to generic `e` not allowed.
        // (instead apply ref to ref attribute as shown above)
        _ <- Person.name("Adam").Spouse.e(lisa).save.recover { case VerifyModelException(err) =>
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
        tx <- Person.name insert List("Lisa", "Nina")
        List(lisa, nina) = tx.eids

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
          tx <- Person.name.insert("Adam")
          adam = tx.eid

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
          tx <- Person.name("Adam").Spouse.name("Lisa").save
          List(adam, lisa) = tx.eids

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
          tx1 <- Person.name.insert("Adam")
          tx2 <- Person.name.insert("Lisa")
          adam = tx1.eid
          lisa = tx2.eid

          // Update Adam with creation of bidirectional reference to existing Lisa
          _ <- Person(adam).spouse(lisa).update

          _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
            ("Adam", "Lisa"),
            ("Lisa", "Adam")
          ))

          // Referencing the same id is not allowed
          _ <- Person(adam).spouse(adam).update.recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:biSelfRef]  Current entity and referenced entity ids can't be the same."
          }
        } yield ()
      }

      "replacing ref to other existing" - bidirectional { implicit conn =>
        for {
          tx <- Person.name("Adam").Spouse.name("Lisa").save
          List(adam, lisa) = tx.eids

          // Bidirectional references created
          _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
            ("Adam", "Lisa"),
            ("Lisa", "Adam")
          ))

          // Update Adam, replacing bidirectional reference with Lisa to existing Nina
          tx <- Person.name.insert("Nina")
          nina = tx.eid
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
        tx <- Person.name("Adam").Spouse.name("Lisa").save
        List(adam, lisa) = tx.eids

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
        tx <- Person.name.insert("Adam")
        adam = tx.eid

        // Create and reference Lisa to Adam
        tx2 <- Person(adam).Spouse.name("Lisa").update
        lisa = tx2.eid

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