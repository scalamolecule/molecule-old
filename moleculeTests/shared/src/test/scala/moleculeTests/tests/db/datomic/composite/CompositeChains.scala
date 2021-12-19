package moleculeTests.tests.db.datomic.composite

import molecule.datomic.api.out22._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object CompositeChains extends AsyncTestSuite {

  lazy val tests = Tests {

    "Arity 22+" - core { implicit conn =>
      for {
        // A single molecule can have up to 22 attributes
        _ <- Ns.str.int.long.double.bool.date.uuid.uri.enumm.ref1.refSub1.strs.ints.longs.doubles.bools.dates.uuids.uris.enums insert List(
          (str1, int1, long1, double1, bool1, date1, uuid1, uri1, enum1, r1, r2, strs1, ints1, longs1, doubles1, bools1, dates1, uuids1, uris1, enums1),
          (str2, int2, long2, double2, bool2, date2, uuid2, uri2, enum2, r3, r4, strs2, ints2, longs2, doubles2, bools2, dates2, uuids2, uris2, enums2)
        )

        _ <- Ns.str.int.long.double.bool.date.uuid.uri.enumm.ref1.refSub1.strs.ints.longs.doubles.bools.dates.uuids.uris.enums.get.map(_ ==> List(
          (str1, int1, long1, double1, bool1, date1, uuid1, uri1, enum1, r1, r2, strs1, ints1, longs1, doubles1, bools1, dates1, uuids1, uris1, enums1),
          (str2, int2, long2, double2, bool2, date2, uuid2, uri2, enum2, r3, r4, strs2, ints2, longs2, doubles2, bools2, dates2, uuids2, uris2, enums2)
        ))

        // Molecules needing more than 22 attributes can be composed as composites. Here we compose a composite molecule with 22 + 9 = 31 attributes in total:
        _ <- (Ns.str.int.long.double.bool.date.uuid.uri.enumm.ref1.refSub1.strs.ints.longs.doubles.bools.dates.uuids.uris.enums +
          Ref1.str1.int1.enum1.ref2.refSub2.strs1.ints1.refs2.refsSub2) insert List(
          (
            (str1, int1, long1, double1, bool1, date1, uuid1, uri1, enum1, r1, r2, strs1, ints1, longs0, doubles1, bools1, dates1, uuids1, uris1, enums1),
            (str2, int2, enum11, r3, r4, strs2, ints2, rs1, rs2)
          )
        )


        // Retrieving composite data as Lists of tuples of two sub tuples, first of arity 22, and second of arity 9
        _ <- (Ns.str.int.long.double.bool.date.uuid.uri.enumm.ref1.refSub1.strs.ints.longs.doubles.bools.dates.uuids.uris.enums +
          Ref1.str1.int1.enum1.ref2.refSub2.strs1.ints1.refs2.refsSub2).get.map(_ ==> List(
          (
            (str1, int1, long1, double1, bool1, date1, uuid1, uri1, enum1, r1, r2, strs1, ints1, longs0, doubles1, bools1, dates1, uuids1, uris1, enums1),
            (str2, int2, enum11, r3, r4, strs2, ints2, rs1, rs2)
          )
        ))
      } yield ()
    }

    "Split into 3" - core { implicit conn =>
      for {
        // Insert composite data with 3 sub-molecules (23 attributes in total)
        _ <- (Ns.bool.bools.date.dates.double.doubles.enumm.enums +
          Ns.int.ints.long.longs.ref1.refSub1 +
          Ns.str.strs.uri.uris.uuid.uuids.refs1) insert Seq(
          // Two rows with tuples of 3 sub-tuples that type-safely match the 3 sub-molecules above
          (
            (false, Set(false), date1, Set(date2, date3), 1.0, Set(2.0, 3.0), "enum1", Set("enum2", "enum3")),
            (1, Set(2, 3), 1L, Set(2L, 3L), r1, r2),
            ("a", Set("b", "c"), uri1, Set(uri2, uri3), uuid1, Set(uuid2), Set(42L))
          ),
          (
            (true, Set(true), date4, Set(date5, date6), 4.0, Set(5.0, 6.0), "enum4", Set("enum5", "enum6")),
            (4, Set(5, 6), 4L, Set(5L, 6L), r3, r4),
            ("d", Set("e", "f"), uri4, Set(uri5, uri6), uuid4, Set(uuid5), Set(43L))
          )
        )

        // Retrieve composite data with the same 3 sub-molecules
        _ <- (Ns.bool.bools.date.dates.double.doubles.enumm.enums +
          Ns.int.ints.long.longs.ref1.refSub1 +
          Ns.str.strs.uri.uris.uuid.uuids.refs1).get.map(_ ==> List(
          (
            (false, Set(false), date1, Set(date2, date3), 1.0, Set(2.0, 3.0), "enum1", Set("enum2", "enum3")),
            (1, Set(2, 3), 1L, Set(2L, 3L), r1, r2),
            ("a", Set("b", "c"), uri1, Set(uri2, uri3), uuid1, Set(uuid2), Set(42L))
          ),
          (
            (true, Set(true), date4, Set(date5, date6), 4.0, Set(5.0, 6.0), "enum4", Set("enum5", "enum6")),
            (4, Set(5, 6), 4L, Set(5L, 6L), r3, r4),
            ("d", Set("e", "f"), uri4, Set(uri5, uri6), uuid4, Set(uuid5), Set(43L))
          )
        ))

        // Subsets of the composite data can be retrieved too
        _ <- (Ns.bool.bools.date.dates +
          Ns.int.refSub1 +
          Ns.str.strs).get.map(_ ==> List(
          (
            (false, Set(false), date1, Set(date2, date3)),
            (1, r2),
            ("a", Set("b", "c"))
          ),
          (
            (true, Set(true), date4, Set(date5, date6)),
            (4, r4),
            ("d", Set("e", "f"))
          )
        ))

        // Since the subset uses less than 22 attributes, we can return single tuples
        // without the need for composite with sub-tuples:
        _ <- Ns.bool.bools.date.dates
          .int.refSub1
          .str.strs.get.map(_ ==> List(
          (
            false, Set(false), date1, Set(date2, date3),
            1, r2,
            "a", Set("b", "c")
          ),
          (
            true, Set(true), date4, Set(date5, date6),
            4, r4,
            "d", Set("e", "f")
          )
        ))
      } yield ()
    }

    // Etc... We can make composites of up to 22 molecules with each having up to 22 attributes
    // adding up to a whopping arity-484 attribute composite!

  }
}