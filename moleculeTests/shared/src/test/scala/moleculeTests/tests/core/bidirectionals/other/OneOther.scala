package moleculeTests.tests.core.bidirectionals.other

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out3._
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object OneOther extends AsyncTestSuite {

  val personPet    = m(Person.name.Pet.name)
  val animalMaster = m(Animal.name.Master.name)

  lazy val tests = Tests {

    "Save new" - bidirectional { implicit conn =>
      for {
        // Save Ben, Rex and bidirectional references between them
        tx <- Person.name("Ben").Pet.name("Rex").save
        List(ben, rex) = tx.eids

        // Reference in both directions saved
        // Since we use different names for the ref attributes on each end,
        // we query from each end. We could as well have had the same attr name.
        _ <- Person(ben).Pet.name.get.map(_.head ==> "Rex")
        _ <- Animal(rex).Master.name.get.map(_.head ==> "Ben")

        // Traversing forth and back should bring us to the initial value
        _ <- Person(ben).Pet.name_.Master.name.get.map(_.head ==> "Ben")
        _ <- Animal(rex).Master.name_.Pet.name.get.map(_.head ==> "Rex")
      } yield ()
    }

    "Save new in reverse" - bidirectional { implicit conn =>
      for {
        // Building from the other end gives the same result

        // Save Ben, Rex and bidirectional references between them
        tx <- Animal.name("Rex").Master.name("Ben").save
        List(rex, ben) = tx.eids

        // Reference in both directions saved
        // Since we use different names for the ref attributes on each end,
        // we query from each end. We could as well have had the same attr name.
        _ <- Animal(rex).Master.name.get.map(_.head ==> "Ben")
        _ <- Person(ben).Pet.name.get.map(_.head ==> "Rex")

        // Traversing forth and back should bring us to the initial value
        _ <- Animal(rex).Master.Pet.name.get.map(_.head ==> "Rex")
        _ <- Person(ben).Pet.Master.name.get.map(_.head ==> "Ben")
      } yield ()
    }

    "Save id" - bidirectional { implicit conn =>
      for {
        tx1 <- Animal.name.insert("Rex")
        rex = tx1.eid

        // Save Ben with bidirectional ref to existing Rex
        tx2 <- Person.name("Ben").pet(rex).save
        ben = tx2.eid

        _ <- animalMaster.get.map(_ ==> List(
          ("Rex", "Ben")
        ))
        _ <- personPet.get.map(_ ==> List(
          ("Ben", "Rex")
        ))

        // Saving reference to generic `e` not allowed.
        // (instead apply ref to ref attribute as shown above)
        _ <- Person.name("Ben").Pet.e(rex).save.recover { case VerifyModelException(err) =>
          err ==> s"[noGenerics]  Generic elements `e`, `a`, `v`, `ns`, `tx`, `t`, `txInstant` and `op` " +
            s"not allowed in save molecules. Found `e($rex)`"
        }
      } yield ()
    }

    "Insert new" - bidirectional { implicit conn =>
      for {
        // Insert 2 pairs of bidirectionally referenced entities
        _ <- Person.name.Pet.name insert List(
          ("Ben", "Rex"),
          ("Kim", "Zip")
        )

        // Bidirectional references have been inserted
        _ <- personPet.get.map(_.sorted ==> List(
          ("Ben", "Rex"),
          ("Kim", "Zip")
        ))
        _ <- animalMaster.get.map(_.sorted ==> List(
          ("Rex", "Ben"),
          ("Zip", "Kim")
        ))
      } yield ()
    }

    "Insert id" - bidirectional { implicit conn =>
      for {
        tx <- Animal.name insert List("Rex", "Zip")
        List(rex, zip) = tx.eids

        // Insert 2 new entities and pair them with existing entities
        _ <- Person.name.pet insert List(
          ("Ben", rex),
          ("Kim", zip)
        )

        // Bidirectional references have been inserted
        _ <- personPet.get.map(_.sorted ==> List(
          ("Ben", "Rex"),
          ("Kim", "Zip")
        ))
        _ <- animalMaster.get.map(_.sorted ==> List(
          ("Rex", "Ben"),
          ("Zip", "Kim")
        ))
      } yield ()
    }

    "Update new" - {

      "creating ref to new" - bidirectional { implicit conn =>
        for {
          tx <- Person.name.insert("Ben")
          ben = tx.eid
          _ <- Person(ben).Pet.name("Rex").update

          _ <- personPet.get.map(_.sorted ==> List(
            ("Ben", "Rex")
          ))
          _ <- animalMaster.get.map(_.sorted ==> List(
            ("Rex", "Ben")
          ))

          // Insert from other end

          tx2 <- Animal.name.insert("Zip")
          zip = tx2.eid
          _ <- Animal(zip).Master.name("Kim").update

          _ <- personPet.get.map(_.sorted ==> List(
            ("Ben", "Rex"),
            ("Kim", "Zip")
          ))
          _ <- animalMaster.get.map(_.sorted ==> List(
            ("Rex", "Ben"),
            ("Zip", "Kim")
          ))
        } yield ()
      }

      "replacing ref to new" - bidirectional { implicit conn =>
        for {
          tx <- Person.name("Ben").Pet.name("Rex").save
          List(ben, rex) = tx.eids

          // Bidirectional references created
          _ <- personPet.get.map(_.sorted ==> List(
            ("Ben", "Rex")
          ))
          _ <- animalMaster.get.map(_.sorted ==> List(
            ("Rex", "Ben")
          ))

          // Update Ben with creation of Zip and replacing bidirectional reference with Rex to created Zip
          _ <- Person(ben).Pet.name("Guz").update

          // Bidirectional references to Rex have been replaced with refs to/from Zip
          _ <- personPet.get.map(_.sorted ==> List(
            ("Ben", "Guz")
          ))
          _ <- animalMaster.get.map(_.map(p => Seq(p._1, p._2).sorted) ==> List(
            Seq("Ben", "Guz")
          ))
        } yield ()
      }
    }

    "Update id" - {

      "creating ref to existing" - bidirectional { implicit conn =>
        for {
          // Ben haven't got Rex yet
          tx1 <- Person.name.insert("Ben")
          tx2 <- Animal.name.insert("Rex")
          ben = tx1.eid
          rex = tx2.eid

          // Update Ben with creation of bidirectional reference to existing Rex
          _ <- Person(ben).pet(rex).update

          _ <- personPet.get.map(_.sorted ==> List(
            ("Ben", "Rex")
          ))
          _ <- animalMaster.get.map(_.sorted ==> List(
            ("Rex", "Ben")
          ))

          // Referencing the same id is not allowed
          _ <- Person(ben).pet(ben).update.recover { case Model2TransactionException(err) =>
            err ==> "[valueStmts:biSelfRef]  Current entity and referenced entity ids can't be the same."
          }
        } yield ()
      }

      "replacing ref to other existing" - bidirectional { implicit conn =>
        for {
          tx1 <- Person.name("Ben").Pet.name("Rex").save
          List(ben, rex) = tx1.eids

          // Bidirectional references created
          _ <- personPet.get.map(_.sorted ==> List(
            ("Ben", "Rex")
          ))
          _ <- animalMaster.get.map(_.sorted ==> List(
            ("Rex", "Ben")
          ))

          // Update Ben, replacing bidirectional reference with Rex to existing Zip
          tx2 <- Animal.name.insert("Zip")
          zip = tx2.eid
          _ <- Person(ben).pet(zip).update

          // Bidirectional references to Rex have been replaced with refs to/from Zip
          _ <- personPet.get.map(_.sorted ==> List(
            ("Ben", "Zip")
          ))
          _ <- animalMaster.get.map(_.sorted ==> List(
            ("Zip", "Ben")
          ))
        } yield ()
      }
    }


    "Update removing reference" - bidirectional { implicit conn =>
      for {
        tx <- Person.name("Ben").Pet.name("Rex").save
        List(ben, rex) = tx.eids

        // Bidirectional references created
        _ <- personPet.get.map(_.sorted ==> List(
          ("Ben", "Rex")
        ))
        _ <- animalMaster.get.map(_.sorted ==> List(
          ("Rex", "Ben")
        ))

        // Retract ref between them by applying no value
        _ <- Person(ben).pet().update

        // Bidirectional references retracted
        _ <- personPet.get.map(_.sorted ==> List())
      } yield ()
    }

    "Retract" - bidirectional { implicit conn =>
      for {
        tx1 <- Person.name.insert("Ben")
        ben = tx1.eid

        // Create and reference Rex to Ben
        tx2 <- Person(ben).Pet.name("Rex").update
        rex = tx2.eid

        _ <- personPet.get.map(_.sorted ==> List(
          ("Ben", "Rex")
        ))
        _ <- animalMaster.get.map(_.sorted ==> List(
          ("Rex", "Ben")
        ))

        // Retract Rex and all references to/from Ben
        _ <- rex.retract

        // Ben remains and both references retracted
        _ <- Person.name.get.map(_ ==> List("Ben"))
        _ <- Animal.name.get.map(_ ==> List())

        _ <- Person(ben).Pet.name.get.map(_ ==> List())
        _ <- Animal(rex).Master.name.get.map(_ ==> List())

        _ <- personPet.get.map(_.sorted ==> List())
        _ <- animalMaster.get.map(_.sorted ==> List())
      } yield ()
    }
  }
}
