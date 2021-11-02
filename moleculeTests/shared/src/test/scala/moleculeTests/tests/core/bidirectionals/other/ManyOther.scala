package moleculeTests.tests.core.bidirectionals.other

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object ManyOther extends AsyncTestSuite {

  val animalBuddiesOf = m(Person.name_(?).Buddies.name)
  val personBuddiesOf = m(Animal.name_(?).Buddies.name)

  lazy val tests = Tests {

    "Save" - {

      "1 new" - bidirectional { implicit conn =>
        for {
          // Save Ann, Gus and bidirectional references between them
          _ <- Person.name("Ann").Buddies.name("Gus").save

          // Reference is bidirectional - both point to each other
          _ <- Person.name.Buddies.name.get.map(_ ==> List(
            ("Ann", "Gus")
          ))
          _ <- Animal.name.Buddies.name.get.map(_ ==> List(
            ("Gus", "Ann")
          ))

          // We can now use a uniform query from both ends:
          // Ann and Gus are buddies with each other
          _ <- animalBuddiesOf("Ann").get.map(_ ==> List("Gus"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))

          // Forth and back should bring os to the starting point
          _ <- Person.name_("Ann").Buddies.Buddies.name.get.map(_ ==> List("Ann"))
          _ <- Animal.name_("Gus").Buddies.Buddies.name.get.map(_ ==> List("Gus"))
        } yield ()
      }

      "n new" - bidirectional { implicit conn =>
        for {
          // Can't save multiple values to cardinality-one attribute
          // It could become unwieldy if different referenced attributes had different number of
          // values (arities) - how many related entities should be created then?
          _ <- Person.name("Ann").Buddies.name("Gus", "Leo").save
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't save multiple values for cardinality-one attribute:" +
              "\n  Animal ... name(Gus, Leo)"
          }

          // We can save a single value though...
          _ <- Person.name("Ann").Buddies.name("Leo").save

          _ <- Person.name.Buddies.name.get.map(_ ==> List(
            ("Ann", "Leo")
          ))
          _ <- Animal.name.Buddies.name.get.map(_ ==> List(
            ("Leo", "Ann")
          ))

          // Ann and Gus are buddies with each other
          _ <- animalBuddiesOf("Ann").get.map(_ ==> List("Leo"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))

          // Can't `save` nested data structures - use nested `insert` instead for that (see tests further down)
          _ <- Person.name("Ann").Buddies.*(Animal.name("Gus")).save
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> s"[noNested]  Nested data structures not allowed in save molecules"
          }

          // So, we can't create multiple referenced entities in one go with the `save` command.
          // Use `insert` for this or save existing entity ids (see below).
        } yield ()
      }

      "1 existing" - bidirectional { implicit conn =>
        for {
          gus <- Animal.name.insert("Gus").map(_.eid)

          // Save Ann with bidirectional ref to existing Gus
          _ <- Person.name("Ann").buddies(gus).save

          // Ann and Gus are buddies with each other
          _ <- Person.name.Buddies.name.get.map(_ ==> List(
            ("Ann", "Gus")
          ))
          _ <- Animal.name.Buddies.name.get.map(_ ==> List(
            ("Gus", "Ann")
          ))

          // Saving reference to generic `e` not allowed.
          // (instead apply ref to ref attribute as shown above)
          _ <- Person.name("Ann").Buddies.e(gus).save
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> s"[noGenerics]  Generic elements `e`, `a`, `v`, `ns`, `tx`, `t`, `txInstant` and `op` " +
              s"not allowed in save molecules. Found `e($gus)`"
          }
        } yield ()
      }

      "n existing" - bidirectional { implicit conn =>
        for {
          gusLeo <- Animal.name.insert("Gus", "Leo").map(_.eids)

          // Save Ann with bidirectional ref to existing Gus and Leo
          _ <- Person.name("Ann").buddies(gusLeo).save

          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))
        } yield ()
      }
    }


    "Insert" - {

      "1 new" - bidirectional { implicit conn =>
        for {
          // Insert two bidirectionally connected entities
          _ <- Person.name.Buddies.name.insert("Ann", "Gus")

          // Bidirectional references have been inserted
          _ <- animalBuddiesOf("Ann").get.map(_ ==> List("Gus"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "1 existing" - bidirectional { implicit conn =>
        for {
          gus <- Animal.name("Gus").save.map(_.eid)

          // Insert Ann with bidirectional ref to existing Gus
          _ <- Person.name.buddies.insert("Ann", Set(gus))

          // Bidirectional references have been inserted
          _ <- animalBuddiesOf("Ann").get.map(_ ==> List("Gus"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "multiple new" - bidirectional { implicit conn =>
        for {
          // Insert 2 pairs of entities with bidirectional references between them
          _ <- Person.name.Buddies.name insert List(
            ("Ann", "Gus"),
            ("Bob", "Leo")
          )

          // Bidirectional references have been inserted
          _ <- animalBuddiesOf("Ann").get.map(_ ==> List("Gus"))
          _ <- animalBuddiesOf("Bob").get.map(_ ==> List("Leo"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Bob"))
        } yield ()
      }

      "multiple existing" - bidirectional { implicit conn =>
        for {
          List(gus, leo) <- Animal.name.insert("Gus", "Leo").map(_.eids)

          // Insert 2 entities with bidirectional refs to existing entities
          _ <- Person.name.buddies insert List(
            ("Ann", Set(gus)),
            ("Bob", Set(leo))
          )

          // Bidirectional references have been inserted
          _ <- animalBuddiesOf("Ann").get.map(_ ==> List("Gus"))
          _ <- animalBuddiesOf("Bob").get.map(_ ==> List("Leo"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Bob"))
        } yield ()
      }

      "nested new" - bidirectional { implicit conn =>
        for {
          // Insert molecules allow nested data structures. So we can conveniently
          // insert 2 entities each connected to 2 target entites
          _ <- Person.name.Buddies.*(Animal.name) insert List(
            ("Ann", List("Gus", "Leo")),
            ("Bob", List("Rex", "Zip"))
          )

          // Bidirectional references have been inserted
          _ <- Person.name.Buddies.*(Animal.name).get.map(_.sortBy(_._1) ==> List(
            ("Ann", List("Gus", "Leo")),
            ("Bob", List("Rex", "Zip"))
          ))
          _ <- Animal.name.Buddies.*(Person.name).get.map(_.sortBy(_._1) ==> List(
            ("Gus", List("Ann")),
            ("Leo", List("Ann")),
            ("Rex", List("Bob")),
            ("Zip", List("Bob"))
          ))
        } yield ()
      }

      "nested existing" - bidirectional { implicit conn =>
        for {
          List(gus, leo, rex) <- Animal.name insert List("Gus", "Leo", "Rex") map (_.eids)

          // Insert 2 Persons and connect them with existing Persons
          _ <- Person.name.buddies insert List(
            ("Ann", Set(gus, leo)),
            ("Bob", Set(gus, rex))
          )

          // Bidirectional references have been inserted - not how Gus got 2 (reverse) friendships
          _ <- Person.name.Buddies.*(Animal.name).get.map(_.sortBy(_._1) ==> List(
            ("Ann", List("Gus", "Leo")),
            ("Bob", List("Gus", "Rex"))
          ))
          _ <- Animal.name.Buddies.*(Person.name).get.map(_.sortBy(_._1) ==> List(
            ("Gus", List("Ann", "Bob")),
            ("Leo", List("Ann")),
            ("Rex", List("Bob"))
          ))
        } yield ()
      }
    }


    "Update" - {

      "assert" - bidirectional { implicit conn =>
        for {
          ann <- Person.name("Ann").save.map(_.eid)
          List(gus, leo, rex, zip) <- Animal.name.insert("Gus", "Leo", "Rex", "Zip").map(_.eids)

          // Add buddieships in various ways

          // Single reference
          _ <- Person(ann).buddies.assert(gus).update
          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus"))

          // Multiple references (vararg)
          _ <- Person(ann).buddies.assert(leo, rex).update
          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo", "Rex"))

          // Set of references
          _ <- Person(ann).buddies.assert(Seq(zip)).update
          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo", "Rex", "Zip"))

          // Buddieships have been added in both directions
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Rex").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Zip").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "retract" - bidirectional { implicit conn =>
        for {
          // Insert Ann and buddies
          List(ann, gus, leo, rex, zip, zup) <- Person.name.Buddies.*(Animal.name) insert List(
            ("Ann", List("Gus", "Leo", "Rex", "Zip", "Zup"))
          ) map (_.eids)

          // Buddieships have been inserted in both directions
          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo", "Rex", "Zip", "Zup"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Rex").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Zip").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Zup").get.map(_ ==> List("Ann"))

          // Remove some buddieships in various ways

          // Single reference
          _ <- Person(ann).buddies.retract(gus).update

          // Multiple references (vararg)
          _ <- Person(ann).buddies.retract(leo, rex).update

          // Set of references
          _ <- Person(ann).buddies.retract(Seq(zip)).update

          // Correct buddieships have been removed in both directions
          _ <- animalBuddiesOf("Ann").get.map(_ ==> List("Zup"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List())
          _ <- personBuddiesOf("Leo").get.map(_ ==> List())
          _ <- personBuddiesOf("Rex").get.map(_ ==> List())
          _ <- personBuddiesOf("Zip").get.map(_ ==> List())
          _ <- personBuddiesOf("Zup").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "replace 1" - bidirectional { implicit conn =>
        for {
          List(ann, gus, leo) <- Person.name.Buddies.*(Animal.name).insert("Ann", List("Gus", "Leo")).map(_.eids)
          rex <- Animal.name("Rex").save.map(_.eid)

          _ <- animalBuddiesOf("Ann").get.map(_ ==> List("Leo", "Gus"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Rex").get.map(_ ==> List())

          // Ann replaces Gus with Rex
          _ <- Person(ann).buddies.replace(gus -> rex).update

          // Ann now buddies with Rex instead of Gus
          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Leo", "Rex"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List())
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Rex").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "replace multiple" - bidirectional { implicit conn =>
        for {
          List(ann, gus, leo) <- Person.name.Buddies.*(Animal.name).insert("Ann", List("Gus", "Leo")).map(_.eids)
          List(rex, zip) <- Animal.name.insert("Rex", "Zip").map(_.eids)

          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Rex").get.map(_ ==> List())
          _ <- personBuddiesOf("Zip").get.map(_ ==> List())

          // Ann replaces Gus and Leo with Rex and Zip
          _ <- Person(ann).buddies.replace(gus -> rex, leo -> zip).update

          // Ann is now buddies with Rex and Zip instead of Gus and Leo
          // Gus and Leo are no longer buddies with Ann either
          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Rex", "Zip"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List())
          _ <- personBuddiesOf("Leo").get.map(_ ==> List())
          _ <- personBuddiesOf("Rex").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Zip").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "replace all with 1" - bidirectional { implicit conn =>
        for {
          List(ann, gus, leo) <- Person.name.Buddies.*(Animal.name) insert List(("Ann", List("Gus", "Leo"))) map (_.eids)
          rex <- Animal.name.insert("Rex").map(_.eid)

          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Rex").get.map(_ ==> List())

          // Applying value(s) replaces all existing values!

          // Rex is the new friend
          _ <- Person(ann).buddies(rex).update

          // Leo and Ann no longer buddies
          _ <- animalBuddiesOf("Ann").get.map(_ ==> List("Rex"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List())
          _ <- personBuddiesOf("Leo").get.map(_ ==> List())
          _ <- personBuddiesOf("Rex").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "replace all with multiple (apply varargs)" - bidirectional { implicit conn =>
        for {
          List(ann, gus, leo) <- Person.name.Buddies.*(Animal.name) insert List(("Ann", List("Gus", "Leo"))) map (_.eids)
          rex <- Animal.name.insert("Rex").map(_.eid)

          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Rex").get.map(_ ==> List())

          // Ann now has Gus and Rex as buddies
          _ <- Person(ann).buddies(gus, rex).update

          // Ann and Rex new buddies
          // Ann and Leo no longer buddies
          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Rex"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List())
          _ <- personBuddiesOf("Rex").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "replace all with multiple (apply Set)" - bidirectional { implicit conn =>
        for {
          List(ann, gus, leo) <- Person.name.Buddies.*(Animal.name) insert List(("Ann", List("Gus", "Leo"))) map (_.eids)
          rex <- Animal.name.insert("Rex").map(_.eid)

          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Rex").get.map(_ ==> List())

          // Ann now has Gus and Rex as buddies
          _ <- Person(ann).buddies(Seq(gus, rex)).update

          // Ann and Rex new buddies
          // Ann and Leo no longer buddies
          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Rex"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List())
          _ <- personBuddiesOf("Rex").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "remove all (apply no values)" - bidirectional { implicit conn =>
        for {
          List(ann, gus, leo) <- Person.name.Buddies.*(Animal.name) insert List(("Ann", List("Gus", "Leo"))) map (_.eids)

          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))

          // Ann has no buddies any longer
          _ <- Person(ann).buddies().update

          // Ann's buddieships replaced with no buddieships (in both directions)
          _ <- animalBuddiesOf("Ann").get.map(_ ==> List())
          _ <- personBuddiesOf("Gus").get.map(_ ==> List())
          _ <- personBuddiesOf("Leo").get.map(_ ==> List())
        } yield ()
      }

      "remove all (apply empty Set)" - bidirectional { implicit conn =>
        for {
          List(ann, gus, leo) <- Person.name.Buddies.*(Animal.name) insert List(("Ann", List("Gus", "Leo"))) map (_.eids)

          _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo"))
          _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
          _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))

          // Ann has no buddies any longer
          noBuddies = Seq.empty[Long]
          _ <- Person(ann).buddies(noBuddies).update

          // Ann's buddieships replaced with no buddieships (in both directions)
          _ <- animalBuddiesOf("Ann").get.map(_ ==> List())
          _ <- personBuddiesOf("Gus").get.map(_ ==> List())
          _ <- personBuddiesOf("Leo").get.map(_ ==> List())

        } yield ()
      }
    }


    "Retract" - bidirectional { implicit conn =>
      for {
        List(ann, gus, leo) <- Person.name.Buddies.*(Animal.name) insert List(("Ann", List("Gus", "Leo"))) map (_.eids)

        _ <- animalBuddiesOf("Ann").get.map(_.sorted ==> List("Gus", "Leo"))
        _ <- personBuddiesOf("Gus").get.map(_ ==> List("Ann"))
        _ <- personBuddiesOf("Leo").get.map(_ ==> List("Ann"))

        // Retract Leo and his relationship to Ann
        _ <- leo.retract

        // Leo is gone
        _ <- Person.name.get.map(_ ==> List("Ann"))
        _ <- Animal.name.get.map(_ ==> List("Gus"))

        // Ann and Gus remain buddies
        _ <- Person(ann).Buddies.name.get.map(_ ==> List("Gus"))
        _ <- Animal(gus).Buddies.name.get.map(_ ==> List("Ann"))
        _ <- Animal(leo).Buddies.name.get.map(_ ==> List())
      } yield ()
    }
  }
}
