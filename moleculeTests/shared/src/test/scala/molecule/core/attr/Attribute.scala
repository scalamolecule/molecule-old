package molecule.core.attr

import molecule.datomic.api.out11._
import molecule.tests.core.base.dsl.CoreTest._
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Attribute extends AsyncTestSuite {

  lazy val tests = Tests {

    "Single cardinality-1 attribute - one entity" - core { implicit conn =>
      for {
        // insertAsync single value for one cardinality-1 attribute
        _ <- Ns.str insertAsync str1
        _ <- Ns.int insertAsync int1
        _ <- Ns.long insertAsync long1
        _ <- Ns.double insertAsync double1
        _ <- Ns.bool insertAsync bool1
        _ <- Ns.date insertAsync date1
        _ <- Ns.uuid insertAsync uuid1
        _ <- Ns.uri insertAsync uri1
        _ <- Ns.enum insertAsync enum1

        // Get one value (RuntimeException if no value)
        _ <- Ns.str.getAsync === List(str1)
        _ <- Ns.int.getAsync === List(int1)
        _ <- Ns.long.getAsync === List(long1)
        _ <- Ns.double.getAsync === List(double1)
        _ <- Ns.bool.getAsync === List(bool1)
        _ <- Ns.date.getAsync === List(date1)
        _ <- Ns.uuid.getAsync === List(uuid1)
        _ <- Ns.uri.getAsync === List(uri1)
        _ <- Ns.enum.getAsync === List(enum1)
      } yield ()
    }

    "Single cardinality-2 attribute - one entity" - core { implicit conn =>
      for {
        // insertAsync single Set of value(s) for one cardinality-2 attribute
        _ <- Ns.strs insertAsync strs1 // Set("str1")
        _ <- Ns.ints insertAsync ints1
        _ <- Ns.longs insertAsync longs1
        _ <- Ns.doubles insertAsync doubles1
        // Set of boolean values doesn't make sense
        _ <- Ns.dates insertAsync dates1
        _ <- Ns.uuids insertAsync uuids1
        _ <- Ns.uris insertAsync uris1
        _ <- Ns.enums insertAsync enums1

        // Get one value (RuntimeException if no value)
        _ <- Ns.strs.getAsync === List(strs1)
        _ <- Ns.ints.getAsync === List(ints1)
        _ <- Ns.longs.getAsync === List(longs1)
        _ <- Ns.doubles.getAsync === List(doubles1)
        // No Set of boolean values
        _ <- Ns.dates.getAsync === List(dates1)
        _ <- Ns.uuids.getAsync === List(uuids1)
        _ <- Ns.uris.getAsync === List(uris1)
        _ <- Ns.enums.getAsync === List(enums1)
      } yield ()
    }


    "Single cardinality-1 attribute - multiple entities" - core { implicit conn =>
      for {
        // insertAsync multiple values for one cardinality-1 attribute
        _ <- Ns.str insertAsync List(str1, str2)
        _ <- Ns.int insertAsync List(int1, int2)
        _ <- Ns.long insertAsync List(long1, long2)
        _ <- Ns.double insertAsync List(double1, double2)
        _ <- Ns.bool insertAsync List(bool1, bool2)
        _ <- Ns.date insertAsync List(date1, date2)
        _ <- Ns.uuid insertAsync List(uuid1, uuid2)
        _ <- Ns.uri insertAsync List(uri1, uri2)
        _ <- Ns.enum insertAsync List(enum1, enum2)

        // Get attribute values as tuples (order not guaranteed)
        _ <- Ns.str.getAsync === List(str1, str2)
        _ <- Ns.int.getAsync === List(int1, int2)
        _ <- Ns.long.getAsync === List(long1, long2)
        _ <- Ns.double.getAsync === List(double1, double2)
        _ <- Ns.bool.getAsync === List(bool2, bool1)
        _ <- Ns.date.getAsync.map(_.sorted ==> List(date1, date2))
        _ <- Ns.uuid.getAsync.map(_.sortBy(_.toString) ==> List(uuid1, uuid2))
        _ <- Ns.uri.getAsync === List(uri1, uri2)
        _ <- Ns.enum.getAsync === List(enum2, enum1)
      } yield ()
    }

    "Single cardinality-2 attribute - multiple entities" - core { implicit conn =>
      for {
        // insertAsync multiple values for one cardinality-2 attribute
        _ <- Ns.strs insertAsync List(strs1, strs2) // List(Set("str1"), Set("str1", "str2"))
        _ <- Ns.ints insertAsync List(ints1, ints2)
        _ <- Ns.longs insertAsync List(longs1, longs2)
        _ <- Ns.doubles insertAsync List(doubles1, doubles2)
        // No Set of boolean values
        _ <- Ns.dates insertAsync List(dates1, dates2)
        _ <- Ns.uuids insertAsync List(uuids1, uuids2)
        _ <- Ns.uris insertAsync List(uris1, uris2)
        _ <- Ns.enums insertAsync List(enums1, enums2)

        // Retrieving Sets only will retrieve one Set of distinct values for each attribute
        _ <- Ns.strs.getAsync === List(strs2) // List(Set("str1", "str2"))
        _ <- Ns.ints.getAsync === List(ints2)
        _ <- Ns.longs.getAsync === List(longs2)
        _ <- Ns.doubles.getAsync === List(doubles2)
        // No Set of boolean values
        _ <- Ns.dates.getAsync === List(dates2)
        _ <- Ns.uuids.getAsync === List(uuids2)
        _ <- Ns.uris.getAsync === List(uris2)
        _ <- Ns.enums.getAsync === List(enums2)
      } yield ()
    }


    "Multiple cardinality-1 attributes - one entity" - core { implicit conn =>
      for {
        // insertAsync single molecule with comma-separated values
        _ <- Ns.str.int.long.double.bool.date.uuid.uri.enum.insertAsync(
          str1, int1, long1, double1, bool1, date1, uuid1, uri1, enum1
        )

        // Get single molecule as tuple of values
        _ <- Ns.str.int.long.double.bool.date.uuid.uri.enum.getAsync === List(
          (str1, int1, long1, double1, bool1, date1, uuid1, uri1, enum1)
        )
      } yield ()
    }

    "Multiple cardinality-2 attributes - one entity" - core { implicit conn =>
      for {
        // insertAsync single molecule with comma-separated Sets of values
        _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.insertAsync(
          strs1, ints1, longs1, doubles1, dates1, uuids1, uris1, enums1
        )

        // Get single molecule as tuple of Sets of values
        _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.getAsync === List(
          (strs1, ints1, longs1, doubles1, dates1, uuids1, uris1, enums1)
        )
      } yield ()
    }


    "Multiple cardinality-1 attributes - multiple entities" - core { implicit conn =>
      for {
        // insertAsync two molecules with tuples of values
        _ <- Ns.str.int.long.double.date.uuid.uri.enum insertAsync List(
          (str1, int1, long1, double1, date1, uuid1, uri1, enum1),
          (str2, int2, long2, double2, date2, uuid2, uri2, enum2)
        )

        // Get two molecules as tuples of values
        _ <- Ns.str.int.long.double.date.uuid.uri.enum.getAsync.map(_.sortBy(_._1) ==> List(
          (str1, int1, long1, double1, date1, uuid1, uri1, enum1),
          (str2, int2, long2, double2, date2, uuid2, uri2, enum2)
        ))
      } yield ()
    }

    "Multiple cardinality-2 attributes - multiple entities" - core { implicit conn =>
      for {
        // insertAsync two molecules with tuples of Sets of values
        _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums insertAsync List(
          (strs1, ints1, longs1, doubles1, dates1, uuids1, uris1, enums1),
          (strs2, ints2, longs2, doubles2, dates2, uuids2, uris2, enums2)
        )

        // Retrieving Sets only will retrieve one Set of distinct values for each attribute
        _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.getAsync === List(
          (strs2, ints2, longs2, doubles2, dates2, uuids2, uris2, enums2)
        )
      } yield ()
    }


    "Mixed cardinality attributes - one entity" - core { implicit conn =>
      for {
        _ <- Ns.str.ints.bool.insertAsync("foo", Set(1, 2, 3), true)
        _ <- Ns.str.ints.bool.getAsync === List(("foo", Set(1, 2, 3), true))
      } yield ()
    }

    "Mixed cardinality attributes - multiple entities" - core { implicit conn =>
      for {
        _ <- Ns.str.ints.bool insertAsync List(
          ("foo", Set(1, 2), true),
          ("bar", Set(2, 3, 4), false)
        )

        // (order not guaranteed)
        _ <- Ns.str.ints.bool.getAsync === List(
          ("bar", Set(2, 3, 4), false),
          ("foo", Set(1, 2), true)
        )
      } yield ()
    }
  }
}