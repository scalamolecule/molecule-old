package moleculeTests.tests.core.crud.insert

import molecule.datomic.api.out10._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Insert extends AsyncTestSuite {

  lazy val tests = Tests {

    "Single attribute" - {

      "Card one" - core { implicit conn =>
        for {
          // The `insert` method performs the compile time analysis of the molecule
          // The `apply` method inserts the type-inferred data at runtime
          _ <- Ns.str.insert.apply("a")

          // We can enter data for one attribute in 4 different ways:

          // 1. Comma-separated list
          _ <- Ns.str insert "b"
          _ <- Ns.str.insert("c", "d")

          // 2. List of values
          _ <- Ns.str insert List("e")
          _ <- Ns.str insert List("f", "g")

          // All values inserted
          _ <- Ns.str.get.map(_.sorted ==> List("a", "b", "c", "d", "e", "f", "g"))


          _ <- Ns.int.insert(1)
          _ <- Ns.int.insert(2, 3)
          _ <- Ns.int.insert(List(4))
          _ <- Ns.int.insert(List(5, 1))
          // Unique values coalesced
          _ <- Ns.int.get.map(_.sorted ==> List(1, 2, 3, 4, 5))


          _ <- Ns.long.insert(1L)
          _ <- Ns.long.insert(2L, 3L)
          _ <- Ns.long.insert(List(4L))
          _ <- Ns.long.insert(List(5L, 1L))
          // Unique values coalesced
          _ <- Ns.long.get.map(_.sorted ==> List(1L, 2L, 3L, 4L, 5L))


          _ <- Ns.double.insert(1.0)
          _ <- Ns.double.insert(2.0, 3.0)
          _ <- Ns.double.insert(List(4.0))
          _ <- Ns.double.insert(List(5.0, 1.0))
          // Unique values coalesced
          _ <- Ns.double.get.map(_.sorted ==> List(1.0, 2.0, 3.0, 4.0, 5.0))


          _ <- Ns.bool.insert(true)
          _ <- Ns.bool.insert(true, false)
          _ <- Ns.bool.insert(List(true))
          _ <- Ns.bool.insert(List(true, false))
          // Unique values coalesced
          _ <- Ns.bool.get.map(_.sorted ==> List(false, true))


          _ <- Ns.date.insert(date1)
          _ <- Ns.date.insert(date2, date3)
          _ <- Ns.date.insert(List(date4))
          _ <- Ns.date.insert(List(date5, date1))
          // Unique values coalesced
          _ <- Ns.date.get.map(_.sorted ==> List(date1, date2, date3, date4, date5))


          _ <- Ns.uuid.insert(uuid1)
          _ <- Ns.uuid.insert(uuid2, uuid3)
          _ <- Ns.uuid.insert(List(uuid4))
          _ <- Ns.uuid.insert(List(uuid5, uuid1))
          // Unique values coalesced
          _ <- Ns.uuid.get.map(_.sortBy(_.toString) ==> List(uuid1, uuid2, uuid3, uuid4, uuid5))


          _ <- Ns.uri.insert(uri1)
          _ <- Ns.uri.insert(uri2, uri3)
          _ <- Ns.uri.insert(List(uri4))
          _ <- Ns.uri.insert(List(uri5, uri1))
          // Unique values coalesced
          _ <- Ns.uri.get.map(_.sorted ==> List(uri1, uri2, uri3, uri4, uri5))


          _ <- Ns.enumm.insert("enum1")
          _ <- Ns.enumm.insert("enum2", "enum3")
          _ <- Ns.enumm.insert(List("enum4"))
          _ <- Ns.enumm.insert(List("enum5", "enum1"))
          // Unique values coalesced
          _ <- Ns.enumm.get.map(_.sorted ==> List(enum1, enum2, enum3, enum4, enum5))
        } yield ()
      }

      "Card many" - core { implicit conn =>
        for {
          // The `insert` method performs the compile time analysis of the molecule
          // The `apply` method inserts the type-inferred data at runtime
          _ <- Ns.strs.insert.apply(Set("a"))

          // We can enter data for one attribute in 3 different ways:

          // 1. Set of values
          _ <- Ns.strs insert Set("b")
          _ <- Ns.strs insert Set("c", "d")

          // 2. Comma-separated list of sets of values
          _ <- Ns.strs.insert(Set("e"), Set("f"))

          // 3. List of sets of values
          _ <- Ns.strs insert List(Set("g"))
          _ <- Ns.strs insert List(Set("h", "i"))
          _ <- Ns.strs insert List(Set("j"), Set("k"))

          // All values inserted
          _ <- Ns.strs.get.map(_.head.toSeq.sorted ==> List("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"))


          _ <- Ns.ints.insert(Set(1))
          _ <- Ns.ints.insert(Set(2, 3))
          _ <- Ns.ints.insert(Set(4), Set(5))
          _ <- Ns.ints.insert(List(Set(6)))
          _ <- Ns.ints.insert(List(Set(7, 8)))
          _ <- Ns.ints.insert(List(Set(9), Set(10)))
          _ <- Ns.ints.get.map(_.head.toSeq.sorted ==> List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))

          _ <- Ns.longs.insert(Set(1L))
          _ <- Ns.longs.insert(Set(2L, 3L))
          _ <- Ns.longs.insert(Set(4L), Set(5L))
          _ <- Ns.longs.insert(List(Set(6L)))
          _ <- Ns.longs.insert(List(Set(7L, 8L)))
          _ <- Ns.longs.insert(List(Set(9L), Set(10L)))
          _ <- Ns.longs.get.map(_.head.toSeq.sorted ==> List(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L))

          _ <- Ns.doubles.insert(Set(1.0))
          _ <- Ns.doubles.insert(Set(2.0, 3.0))
          _ <- Ns.doubles.insert(Set(4.0), Set(5.0))
          _ <- Ns.doubles.insert(List(Set(6.0)))
          _ <- Ns.doubles.insert(List(Set(7.0, 8.0)))
          _ <- Ns.doubles.insert(List(Set(9.0), Set(10.0)))
          _ <- Ns.doubles.get.map(_.head.toSeq.sorted ==> List(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0))

          _ <- Ns.dates.insert(Set(date1))
          _ <- Ns.dates.insert(Set(date2, date3))
          _ <- Ns.dates.insert(Set(date4), Set(date5))
          _ <- Ns.dates.insert(List(Set(date6)))
          _ <- Ns.dates.insert(List(Set(date7, date8)))
          _ <- Ns.dates.insert(List(Set(date9), Set(date10)))
          _ <- Ns.dates.get.map(_.head.toSeq.sorted ==> List(date1, date2, date3, date4, date5, date6, date7, date8, date9, date10))

          _ <- Ns.uuids.insert(Set(uuid1))
          _ <- Ns.uuids.insert(Set(uuid2, uuid3))
          _ <- Ns.uuids.insert(Set(uuid4), Set(uuid5))
          _ <- Ns.uuids.insert(List(Set(uuid6)))
          _ <- Ns.uuids.insert(List(Set(uuid7, uuid8)))
          _ <- Ns.uuids.insert(List(Set(uuid9), Set(uuid10)))
          _ <- Ns.uuids.get.map(_.head.toSeq.sortBy(_.toString) ==> List(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6, uuid7, uuid8, uuid9, uuid10))

          _ <- Ns.uris.insert(Set(uri1))
          _ <- Ns.uris.insert(Set(uri2, uri3))
          _ <- Ns.uris.insert(Set(uri4), Set(uri5))
          _ <- Ns.uris.insert(List(Set(uri6)))
          _ <- Ns.uris.insert(List(Set(uri7, uri8)))
          _ <- Ns.uris.insert(List(Set(uri9), Set(uri10)))
          _ <- Ns.uris.get.map(_.head.toSeq.sortBy(_.toString) ==> List(uri1, uri10, uri2, uri3, uri4, uri5, uri6, uri7, uri8, uri9))

          _ <- Ns.enums.insert(Set(enum1))
          _ <- Ns.enums.insert(Set(enum2, enum3))
          _ <- Ns.enums.insert(Set(enum4), Set(enum5))
          _ <- Ns.enums.insert(List(Set(enum6)))
          _ <- Ns.enums.insert(List(Set(enum7, enum8)))
          _ <- Ns.enums.insert(List(Set(enum9), Set(enum0)))
          _ <- Ns.enums.get.map(_.head.toSeq.sorted ==> List(enum0, enum1, enum2, enum3, enum4, enum5, enum6, enum7, enum8, enum9))
        } yield ()
      }
    }


    "Multiple attributes" - {

      "Card one" - core { implicit conn =>
        for {
          // Insert 3 entities as tuples of values
          // Note that values are typechecked against the attribute types of the molecule
          _ <- Ns.str.int.long.double.bool.date.uuid.uri.enumm insert List(
            (" ", 0, 0L, 0.0, false, date0, uuid0, uri0, "enum0"),
            ("a", 1, 1L, 1.0, true, date1, uuid1, uri1, "enum1"),
            ("b", 2, 2L, 2.0, false, date2, uuid2, uri2, "enum2")
          )

          _ <- Ns.str.int.long.double.bool.date.uuid.uri.enumm.get.map(_.sortBy(_._1) ==> List(
            (" ", 0, 0L, 0.0, false, date0, uuid0, uri0, "enum0"),
            ("a", 1, 1L, 1.0, true, date1, uuid1, uri1, "enum1"),
            ("b", 2, 2L, 2.0, false, date2, uuid2, uri2, "enum2")
          ))
        } yield ()
      }

      "Card many" - core { implicit conn =>
        for {
          // Insert 3 entities as tuples of values
          // Note that values are typechecked against the attribute types of the molecule
          _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums insert List(
            (Set("a", "b"),
              Set(1, 2),
              Set(1L, 2L),
              Set(1.0, 2.0),
              Set(date1, date2),
              Set(uuid1, uuid2),
              Set(uri1, uri2),
              Set("enum1", "enum2")),
            (Set("c", "d"),
              Set(3, 4),
              Set(3L, 4L),
              Set(3.0, 4.0),
              Set(date3, date4),
              Set(uuid3, uuid4),
              Set(uri3, uri4),
              Set("enum3", "enum4"))
          )

          // Unique values coalesced
          _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.get.map(_.head ==> (
            Set("d", "a", "b", "c"),
            Set(1, 4, 3, 2),
            Set(1L, 4L, 3L, 2L),
            Set(2.0, 4.0, 1.0, 3.0),
            Set(date4, date1, date2, date3),
            Set(uuid3, uuid2, uuid4, uuid1),
            Set(uri1, uri2, uri3, uri4),
            Set("enum4", "enum1", "enum3", "enum2")))
        } yield ()
      }
    }


    "Optional values" - {

      "Card one attrs" - core { implicit conn =>
        for {
          _ <- Ns.int.str$ insert List(
            (1, None),
            (2, Some("a"))
          )

          _ <- Ns.int.str$.get.map(_ ==> List(
            (1, None),
            (2, Some("a"))
          ))

          _ <- Ns.int.str.get.map(_ ==> List(
            (2, "a")
          ))
        } yield ()
      }

      "Card many attrs" - core { implicit conn =>
        for {
          _ <- Ns.int.strs$ insert List(
            (1, None),
            (2, Some(Set("a", "b")))
          )

          _ <- Ns.int.strs$.get.map(_ ==> List(
            (1, None),
            (2, Some(Set("a", "b")))
          ))

          _ <- Ns.int.strs.get.map(_ ==> List(
            (2, Set("a", "b"))
          ))

          // Alternatively we can apply typed empty Sets

          _ <- Ns.date.longs insert List(
            (date1, Set[Long]()),
            (date2, Set(20L, 21L))
          )

          _ <- Ns.date.longs$.get.map(_.sortBy(_._1) ==> List(
            (date1, None),
            (date2, Some(Set(20L, 21L))),
          ))

          _ <- Ns.date.longs.get.map(_ ==> List(
            (date2, Set(20L, 21L))
          ))
        } yield ()
      }
    }


    ">22 inserts/upserts" - core { implicit conn =>
      for {
        // If we need to insert more than 22 facts for a single namespace
        // we can start asserting those 22 facts, then use the returned eid
        // to continue (here shown with only 1 fact):

        // Insert maximum of 22 facts
        eid <- Ns.str.insert("a").map(_.eid)

        // Use entity id to continue adding more values
        _ <- Ns.e.int.insert(eid, 42)

        // Only a single entity has been created
        _ <- Ns.e.str.int.get.map(_ ==> List(
          (eid, "a", 42)
        ))

        // Optional attribute after `e` works too
        _ <- Ns.e.long$.bool.insert(eid, None, true)

        // Only a single entity has been created
        _ <- Ns.e.str.int.long$.bool.get.map(_ ==> List(
          (eid, "a", 42, None, true)
        ))
      } yield ()
    }
  }
}