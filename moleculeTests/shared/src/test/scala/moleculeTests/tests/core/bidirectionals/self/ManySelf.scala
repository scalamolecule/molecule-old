package moleculeTests.tests.core.bidirectionals.self

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import molecule.core.util.Executor._


object ManySelf extends AsyncTestSuite {

  val friendsOf = m(Person.name_(?).Friends.name.a1)

  lazy val tests = Tests {

    "Save" - {

      "1 new" - bidirectional { implicit conn =>
        for {
          // Save Ann, Ben and bidirectional references between them
          _ <- Person.name("Ann").Friends.name("Ben").save

          // Reference is bidirectional - both point to each other
          _ <- Person.name.a1.Friends.name.get.map(_ ==> List(
            ("Ann", "Ben"),
            // Reverse reference:
            ("Ben", "Ann")
          ))

          // We can now use a uniform query from both ends:
          // Ann and Ben are friends with each other
          _ <- friendsOf("Ann").get.map(_ ==> List("Ben"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "n new" - bidirectional { implicit conn =>
        for {
          // Can't save multiple values to cardinality-one attribute
          // It could become unwieldy if different referenced attributes had different number of
          // values (arities) - how many related entities should be created then?
          _ <- Person.name("Ann").Friends.name("Ben", "Joe").save
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> "[noConflictingCardOneValues]  Can't save multiple values for cardinality-one attribute:" +
              "\n  Person ... name(Ben, Joe)"
          }

          // We can save a single value though...
          _ <- Person.name("Ann").Friends.name("Joe").save

          _ <- Person.name.Friends.name.get.map(_ ==> List(
            ("Ann", "Joe"),
            ("Joe", "Ann")
          ))

          // Ann and Ben are friends with each other
          _ <- friendsOf("Ann").get.map(_ ==> List("Joe"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))

          // Can't `save` nested data structures - use nested `insert` instead for that (see tests further down)
          _ <- Person.name("Ann").Friends.*(Person.name("Ben")).save
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> s"[noNested]  Nested data structures not allowed in save molecules"
          }

          // So, we can't create multiple referenced entities in one go with the `save` command.
          // Use `insert` for this or save existing entity ids (see below).
        } yield ()
      }

      "1 existing" - bidirectional { implicit conn =>
        for {
          ben <- Person.name.insert("Ben").map(_.eid)

          // Save Ann with bidirectional ref to existing Ben
          _ <- Person.name("Ann").friends(ben).save

          _ <- Person.name.a1.Friends.name.get.map(_ ==> List(
            ("Ann", "Ben"),
            ("Ben", "Ann")
          ))

          // Ann and Ben are friends with each other
          _ <- friendsOf("Ann").get.map(_ ==> List("Ben"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))

          // Saveing reference to generic `e` not allowed.
          // (instead apply ref to ref attribute as shown above)
          _ <- Person.name("Ann").Friends.e(ben).save
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> s"[noGenerics]  Generic elements `e`, `a`, `v`, `ns`, `tx`, `t`, `txInstant` and `op` " +
              s"not allowed in save molecules. Found `e($ben)`"
          }
        } yield ()
      }

      "n existing" - bidirectional { implicit conn =>
        for {
          benJoe <- Person.name.insert("Ben", "Joe").map(_.eids)

          // Save Ann with bidirectional ref to existing Ben and Joe
          _ <- Person.name("Ann").friends(benJoe).save

          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
        } yield ()
      }
    }

    "Insert" - {

      "1 new" - bidirectional { implicit conn =>
        for {
          // Insert two bidirectionally connected entities
          _ <- Person.name.Friends.name.insert("Ann", "Ben")

          // Bidirectional references have been inserted
          _ <- friendsOf("Ann").get.map(_ ==> List("Ben"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "1 existing" - bidirectional { implicit conn =>
        for {
          ben <- Person.name("Ben").save.map(_.eid)

          // Insert Ann with bidirectional ref to existing Ben
          _ <- Person.name.friends.insert("Ann", Set(ben))

          // Bidirectional references have been inserted
          _ <- friendsOf("Ann").get.map(_ ==> List("Ben"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "multiple new" - bidirectional { implicit conn =>
        for {
          // Insert 2 pairs of entities with bidirectional references between them
          _ <- Person.name.Friends.name insert List(
            ("Ann", "Joe"),
            ("Ben", "Tim")
          )

          // Bidirectional references have been inserted
          _ <- friendsOf("Ann").get.map(_ ==> List("Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Tim"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Tim").get.map(_ ==> List("Ben"))
        } yield ()
      }

      "multiple existing" - bidirectional { implicit conn =>
        for {
          List(joe, tim) <- Person.name.insert("Joe", "Tim").map(_.eids)

          // Insert 2 entities with bidirectional refs to existing entities
          _ <- Person.name.friends insert List(
            ("Ann", Set(joe)),
            ("Ben", Set(tim))
          )

          // Bidirectional references have been inserted
          _ <- friendsOf("Ann").get.map(_ ==> List("Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Tim"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Tim").get.map(_ ==> List("Ben"))
        } yield ()
      }

      "nested new" - bidirectional { implicit conn =>
        for {
          // Insert molecules allow nested data structures. So we can conveniently
          // insert 2 entities each connected to 2 target entites
          _ <- Person.name.Friends.*(Person.name) insert List(
            ("Ann", List("Ben", "Joe")),
            ("Don", List("Tim", "Tom"))
          )

          // Bidirectional references have been inserted
          _ <- Person.name.a1.Friends.*(Person.name.a1).get.map(_ ==> List(
            ("Ann", List("Ben", "Joe")),
            ("Ben", List("Ann")),
            ("Don", List("Tim", "Tom")),
            ("Joe", List("Ann")),
            ("Tim", List("Don")),
            ("Tom", List("Don"))
          ))
        } yield ()
      }

      "nested existing" - bidirectional { implicit conn =>
        for {
          List(ben, joe, tim) <- Person.name insert List("Ben", "Joe", "Tim") map (_.eids)

          // Insert 2 Persons and connect them with existing Persons
          _ <- Person.name.friends insert List(
            ("Ann", Set(ben, joe)),
            ("Don", Set(ben, tim))
          )

          // Bidirectional references have been inserted - not how Ben got 2 (reverse) friendships
          _ <- Person.name.a1.Friends.*(Person.name.a1).get.map(_ ==> List(
            ("Ann", List("Ben", "Joe")),
            ("Ben", List("Ann", "Don")),
            ("Don", List("Ben", "Tim")),
            ("Joe", List("Ann")),
            ("Tim", List("Don"))
          ))
        } yield ()
      }
    }

    "Update" - {

      "assert" - bidirectional { implicit conn =>
        for {
          List(ann, ben, joe, liz, tom) <- Person.name.insert("Ann", "Ben", "Joe", "Liz", "Tom").map(_.eids)

          // Add friendships in various ways

          // Single reference
          _ <- Person(ann).friends.assert(ben).update

          // Multiple references (vararg)
          _ <- Person(ann).friends.assert(joe, liz).update

          // Set of references
          _ <- Person(ann).friends.assert(Seq(tom)).update

          // Friendships have been added in both directions
          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe", "Liz", "Tom"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Liz").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Tom").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "retract" - bidirectional { implicit conn =>
        for {
          // Insert Ann and friends
          List(ann, ben, joe, liz, tom, ulf) <- Person.name.Friends.*(Person.name) insert List(
            ("Ann", List("Ben", "Joe", "Liz", "Tom", "Ulf"))
          ) map (_.eids)

          // Friendships have been inserted in both directions
          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe", "Liz", "Tom", "Ulf"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Liz").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Tom").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Ulf").get.map(_ ==> List("Ann"))

          // Remove some friendships in various ways

          // Single reference
          _ <- Person(ann).friends.retract(ben).update

          // Multiple references (vararg)
          _ <- Person(ann).friends.retract(joe, liz).update

          // Set of references
          _ <- Person(ann).friends.retract(Seq(tom)).update

          // Correct friendships have been removed in both directions
          _ <- friendsOf("Ann").get.map(_ ==> List("Ulf"))
          _ <- friendsOf("Ben").get.map(_ ==> List())
          _ <- friendsOf("Joe").get.map(_ ==> List())
          _ <- friendsOf("Liz").get.map(_ ==> List())
          _ <- friendsOf("Tom").get.map(_ ==> List())
          _ <- friendsOf("Ulf").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "replace 1" - bidirectional { implicit conn =>
        for {
          List(ann, ben, joe) <- Person.name.Friends.*(Person.name).insert("Ann", List("Ben", "Joe")).map(_.eids)
          tim <- Person.name("Tim").save.map(_.eid)

          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Tim").get.map(_ ==> List())

          // Ann replaces Ben with Tim
          _ <- Person(ann).friends.replace(ben -> tim).update

          // Ann now friends with Tim instead of Ben
          _ <- friendsOf("Ann").get.map(_ ==> List("Tim", "Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List())
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Tim").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "replace multiple" - bidirectional { implicit conn =>
        for {
          List(ann, ben, joe) <- Person.name.Friends.*(Person.name).insert("Ann", List("Ben", "Joe")).map(_.eids)
          List(tim, tom) <- Person.name.insert("Tim", "Tom").map(_.eids)

          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Tim").get.map(_ ==> List())
          _ <- friendsOf("Tom").get.map(_ ==> List())

          // Ann replaces Ben and Joe with Tim and Tom
          _ <- Person(ann).friends.replace(ben -> tim, joe -> tom).update

          // Ann is now friends with Tim and Tom instead of Ben and Joe
          // Ben and Joe are no longer friends with Ann either
          _ <- friendsOf("Ann").get.map(_ ==> List("Tom", "Tim"))
          _ <- friendsOf("Ben").get.map(_ ==> List())
          _ <- friendsOf("Joe").get.map(_ ==> List())
          _ <- friendsOf("Tim").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Tom").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "replace all with 1" - bidirectional { implicit conn =>
        for {
          List(ann, ben, joe) <- Person.name.Friends.*(Person.name) insert List(("Ann", List("Ben", "Joe"))) map (_.eids)
          liz <- Person.name.insert("Liz").map(_.eid)

          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Liz").get.map(_ ==> List())

          // Applying value(s) replaces all existing values!

          // Liz is the new friend
          _ <- Person(ann).friends(liz).update

          // Joe and Ann no longer friends
          _ <- friendsOf("Ann").get.map(_ ==> List("Liz"))
          _ <- friendsOf("Ben").get.map(_ ==> List())
          _ <- friendsOf("Joe").get.map(_ ==> List())
          _ <- friendsOf("Liz").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "replace all with multiple (apply varargs)" - bidirectional { implicit conn =>
        for {
          List(ann, ben, joe) <- Person.name.Friends.*(Person.name) insert List(("Ann", List("Ben", "Joe"))) map (_.eids)
          liz <- Person.name.insert("Liz").map(_.eid)

          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Liz").get.map(_ ==> List())

          // Ann now has Ben and Liz as friends
          _ <- Person(ann).friends(ben, liz).update

          // Ann and Liz new friends
          // Ann and Joe no longer friends
          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Liz"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List())
          _ <- friendsOf("Liz").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "replace all with multiple (apply Set)" - bidirectional { implicit conn =>
        for {
          List(ann, ben, joe) <- Person.name.Friends.*(Person.name) insert List(("Ann", List("Ben", "Joe"))) map (_.eids)
          liz <- Person.name.insert("Liz").map(_.eid)

          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Liz").get.map(_ ==> List())

          // Ann now has Ben and Liz as friends
          _ <- Person(ann).friends(Seq(ben, liz)).update

          // Ann and Liz new friends
          // Ann and Joe no longer friends
          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Liz"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List())
          _ <- friendsOf("Liz").get.map(_ ==> List("Ann"))
        } yield ()
      }

      "remove all (apply no values)" - bidirectional { implicit conn =>
        for {
          List(ann, ben, joe) <- Person.name.Friends.*(Person.name) insert List(("Ann", List("Ben", "Joe"))) map (_.eids)

          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))

          // Ann has no friends any longer
          _ <- Person(ann).friends().update

          // Ann's friendships replaced with no friendships (in both directions)
          _ <- friendsOf("Ann").get.map(_ ==> List())
          _ <- friendsOf("Ben").get.map(_ ==> List())
          _ <- friendsOf("Joe").get.map(_ ==> List())
        } yield ()
      }

      "remove all (apply empty Set)" - bidirectional { implicit conn =>
        for {
          List(ann, ben, joe) <- Person.name.Friends.*(Person.name) insert List(("Ann", List("Ben", "Joe"))) map (_.eids)

          _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe"))
          _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
          _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))

          // Ann has no friends any longer
          noFriends = Seq.empty[Long]
          _ <- Person(ann).friends(noFriends).update

          // Ann's friendships replaced with no friendships (in both directions)
          _ <- friendsOf("Ann").get.map(_ ==> List())
          _ <- friendsOf("Ben").get.map(_ ==> List())
          _ <- friendsOf("Joe").get.map(_ ==> List())
        } yield ()
      }
    }


    "Retract" - bidirectional { implicit conn =>
      for {
        List(ann, ben, joe) <- Person.name.Friends.*(Person.name) insert List(("Ann", List("Ben", "Joe"))) map (_.eids)

        _ <- friendsOf("Ann").get.map(_ ==> List("Ben", "Joe"))
        _ <- friendsOf("Ben").get.map(_ ==> List("Ann"))
        _ <- friendsOf("Joe").get.map(_ ==> List("Ann"))

        // Retract Ann and all her friendships
        _ <- ann.retract

        // Ann doesn't exist anymore
        _ <- Person.name("Ann").get.map(_ ==> List())
        _ <- friendsOf("Ann").get.map(_ ==> List())

        // Ben and Joe are no longer friends with Ann
        _ <- friendsOf("Ben").get.map(_ ==> List())
        _ <- friendsOf("Joe").get.map(_ ==> List())
      } yield ()
    }
  }
}
