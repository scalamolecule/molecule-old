package moleculeTests.tests.core.crud.insert

import moleculeTests.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out10._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object InsertInput extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - core { implicit conn =>
      // 1. Define "Insert-molecule"
      val insertStr = Ns.str.insert

      for {
        // 2. Re-use Insert-molecule to insert values
        _ <- insertStr("a")
        _ <- insertStr("b")
        _ <- insertStr("c")

        _ <- Ns.str.get.map(_.sorted ==> List("a", "b", "c"))


        insertAll = Ns.str.int.long.double.bool.date.uuid.uri.enum.insert

        // Var-arg for single entity
        _ <- insertAll(" ", 0, 0L, 0.0, false, date0, uuid0, uri0, "enum0")

        // List of tuples for multiple entities
        _ <- insertAll(List(
          ("a", 1, 1L, 1.0, true, date1, uuid1, uri1, "enum1"),
          ("b", 2, 2L, 2.0, false, date2, uuid2, uri2, "enum2")
        ))

        _ <- Ns.str.int.long.double.bool.date.uuid.uri.enum.get.map(_.sortBy(_._1) ==> List(
          (" ", 0, 0L, 0.0, false, date0, uuid0, uri0, "enum0"),
          ("a", 1, 1L, 1.0, true, date1, uuid1, uri1, "enum1"),
          ("b", 2, 2L, 2.0, false, date2, uuid2, uri2, "enum2")
        ))
      } yield ()
    }

    "Card many" - core { implicit conn =>

      // 1. Define "Insert-molecule"
      val insertStrs = Ns.strs.insert

      for {
        // 2. Re-use Insert-molecule to insert values
        _ <- insertStrs(Set("a"))
        _ <- insertStrs(Set("b", "c"))

        _ <- Ns.strs.get.map(_.head ==> Set("a", "b", "c"))


        insertAlls = Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.insert

        _ <- insertAlls(Set(" "), Set(0), Set(0L), Set(0.0), Set(date0), Set(uuid0), Set(uri0), Set("enum0"))
        _ <- insertAlls(List(
          (Set("a"), Set(1), Set(1L), Set(1.0), Set(date1), Set(uuid1), Set(uri1), Set("enum1")),
          (Set("b"), Set(2), Set(2L), Set(2.0), Set(date2), Set(uuid2), Set(uri2), Set("enum2"))
        ))

        _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.get.map(_.head ==> (
          Set("a", "b", " "),
          Set(0, 1, 2),
          Set(0L, 1L, 2L),
          Set(0.0, 2.0, 1.0),
          Set(date0, date1, date2),
          Set(uuid0, uuid1, uuid2),
          Set(uri0, uri1, uri2),
          Set("enum1", "enum0", "enum2")))
      } yield ()
    }

    "Relationships" - core { implicit conn =>

      // 1. Define Input-molecule
      val insertAddress = Ns.str.Ref1.int1.str1.Ref2.str2.insert

      for {
        // 2. Insert data using input molecule as template
        _ <- insertAddress("273 Broadway", 10700, "New York", "USA")
        _ <- insertAddress("2054, 5th Ave", 10800, "New York", "USA")

        _ <- Ns.str.Ref1.int1.str1.Ref2.str2.get === List(
          ("2054, 5th Ave", 10800, "New York", "USA"),
          ("273 Broadway", 10700, "New York", "USA")
        )
      } yield ()
    }
  }
}