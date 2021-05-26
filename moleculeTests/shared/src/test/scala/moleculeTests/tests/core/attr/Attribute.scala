package moleculeTests.tests.core.attr

import molecule.datomic.api.out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Attribute extends AsyncTestSuite {

  lazy val tests = Tests {

    "Single cardinality-1 attribute - one entity" - core { implicit conn =>
      for {
        // Insert single value for one cardinality-1 attribute
        _ <- Ns.str insert str1
        _ <- Ns.int insert int1
        _ <- Ns.long insert long1
        _ <- Ns.double insert double1
        _ <- Ns.bool insert bool1
        _ <- Ns.date insert date1
        _ <- Ns.uuid insert uuid1
        _ <- Ns.uri insert uri1
        _ <- Ns.enum insert enum1

        // Get one value (RuntimeException if no value)
        _ <- Ns.str.get === List(str1)
        _ <- Ns.int.get === List(int1)
        _ <- Ns.long.get === List(long1)
        _ <- Ns.double.get === List(double1)
        _ <- Ns.bool.get === List(bool1)
        _ <- Ns.date.get === List(date1)
        _ <- Ns.uuid.get === List(uuid1)
        _ <- Ns.uri.get === List(uri1)
        _ <- Ns.enum.get === List(enum1)
      } yield ()
    }


    "Single cardinality-2 attribute - one entity" - core { implicit conn =>
      for {
        // Insert single Set of value(s) for one cardinality-2 attribute
        _ <- Ns.strs insert strs1 // Set("str1")
        _ <- Ns.ints insert ints1
        _ <- Ns.longs insert longs1
        _ <- Ns.doubles insert doubles1
        // Set of boolean values doesn't make sense
        _ <- Ns.dates insert dates1
        _ <- Ns.uuids insert uuids1
        _ <- Ns.uris insert uris1
        _ <- Ns.enums insert enums1

        // Get one value (RuntimeException if no value)
        _ <- Ns.strs.get === List(strs1)
        _ <- Ns.ints.get === List(ints1)
        _ <- Ns.longs.get === List(longs1)
        _ <- Ns.doubles.get === List(doubles1)
        // No Set of boolean values
        _ <- Ns.dates.get.map(_.head ==> dates1)
        _ <- Ns.uuids.get.map(_.head ==> uuids1)
        _ <- Ns.uris.get.map(_.head ==> uris1)
        _ <- Ns.enums.get.map(_.head ==> enums1)
      } yield ()
    }


    "Single cardinality-1 attribute - multiple entities" - core { implicit conn =>
      for {
        // Insert multiple values for one cardinality-1 attribute
        _ <- Ns.str insert List(str1, str2)
        _ <- Ns.int insert List(int1, int2)
        _ <- Ns.long insert List(long1, long2)
        _ <- Ns.double insert List(double1, double2)
        _ <- Ns.bool insert List(bool1, bool2)
        _ <- Ns.date insert List(date1, date2)
        _ <- Ns.uuid insert List(uuid1, uuid2)
        _ <- Ns.uri insert List(uri1, uri2)
        _ <- Ns.enum insert List(enum1, enum2)

        // Get attribute values as tuples (order not guaranteed)
        _ <- Ns.str.get === List(str1, str2)
        _ <- Ns.int.get === List(int1, int2)
        _ <- Ns.long.get === List(long1, long2)
        _ <- Ns.double.get === List(double1, double2)
        _ <- Ns.bool.get === List(bool2, bool1)
        _ <- Ns.date.get.map(_.sorted ==> List(date1, date2))
        _ <- Ns.uuid.get.map(_.sortBy(_.toString) ==> List(uuid1, uuid2))
        _ <- Ns.uri.get === List(uri1, uri2)
        _ <- Ns.enum.get === List(enum2, enum1)
      } yield ()
    }


    "Single cardinality-2 attribute - multiple entities" - core { implicit conn =>
      for {
        // Insert multiple values for one cardinality-2 attribute
        _ <- Ns.strs insert List(strs1, strs2) // List(Set("str1"), Set("str1", "str2"))
        _ <- Ns.ints insert List(ints1, ints2)
        _ <- Ns.longs insert List(longs1, longs2)
        _ <- Ns.doubles insert List(doubles1, doubles2)
        // No Set of boolean values
        _ <- Ns.dates insert List(dates1, dates2)
        _ <- Ns.uuids insert List(uuids1, uuids2)
        _ <- Ns.uris insert List(uris1, uris2)
        _ <- Ns.enums insert List(enums1, enums2)

        // Retrieving Sets only will retrieve one Set of distinct values for each attribute
        _ <- Ns.strs.get === List(strs2) // List(Set("str1", "str2"))
        _ <- Ns.ints.get === List(ints2)
        _ <- Ns.longs.get === List(longs2)
        _ <- Ns.doubles.get === List(doubles2)
        // No Set of boolean values
        _ <- Ns.dates.get === List(dates2)
        _ <- Ns.uuids.get === List(uuids2)
        _ <- Ns.uris.get === List(uris2)
        _ <- Ns.enums.get === List(enums2)
      } yield ()
    }


    "Multiple cardinality-1 attributes - one entity" - core { implicit conn =>
      for {
        // Insert single molecule with comma-separated values
        _ <- Ns.str.int.long.double.bool.date.uuid.uri.enum.insert(
          str1, int1, long1, double1, bool1, date1, uuid1, uri1, enum1)

        // Get single molecule as tuple of values
        _ <- Ns.str.int.long.double.bool.date.uuid.uri.enum.get === List(
          (str1, int1, long1, double1, bool1, date1, uuid1, uri1, enum1)
        )
      } yield ()
    }


    "Multiple cardinality-2 attributes - one entity" - core { implicit conn =>
      for {
        // Insert single molecule with comma-separated Sets of values
        _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.insert(
          strs1, ints1, longs1, doubles1, dates1, uuids1, uris1, enums1)

        // Get single molecule as tuple of Sets of values
        _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.get === List(
          (strs1, ints1, longs1, doubles1, dates1, uuids1, uris1, enums1)
        )
      } yield ()
    }


    "Multiple cardinality-1 attributes - multiple entities" - core { implicit conn =>
      for {
        // Insert two molecules with tuples of values
        _ <- Ns.str.int.long.double.date.uuid.uri.enum insert List(
          (str1, int1, long1, double1, date1, uuid1, uri1, enum1),
          (str2, int2, long2, double2, date2, uuid2, uri2, enum2)
        )

        // Get two molecules as tuples of values
        _ <- Ns.str.int.long.double.date.uuid.uri.enum.get.map(_.sortBy(_._1) ==> List(
          (str1, int1, long1, double1, date1, uuid1, uri1, enum1),
          (str2, int2, long2, double2, date2, uuid2, uri2, enum2)
        ))
      } yield ()
    }


    "Multiple cardinality-2 attributes - multiple entities" - core { implicit conn =>
      for {
        // Insert two molecules with tuples of Sets of values
        _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums insert List(
          (strs1, ints1, longs1, doubles1, dates1, uuids1, uris1, enums1),
          (strs2, ints2, longs2, doubles2, dates2, uuids2, uris2, enums2)
        )

        // Retrieving Sets only will retrieve one Set of distinct values for each attribute
        _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.get === List(
          (strs2, ints2, longs2, doubles2, dates2, uuids2, uris2, enums2)
        )
      } yield ()
    }


    "Mixed cardinality attributes - one entity" - core { implicit conn =>
      for {
        _ <- Ns.str.ints.bool.insert("foo", Set(1, 2, 3), true)
        _ <- Ns.str.ints.bool.get === List(("foo", Set(1, 2, 3), true))
      } yield ()
    }


    "Mixed cardinality attributes - multiple entities" - core { implicit conn =>
      for {
        _ <- Ns.str.ints.bool insert List(
          ("foo", Set(1, 2), true),
          ("bar", Set(2, 3, 4), false)
        )

        // (order not guaranteed)
        _ <- Ns.str.ints.bool.get === List(
          ("bar", Set(2, 3, 4), false),
          ("foo", Set(1, 2), true)
        )
      } yield ()
    }
  }
}