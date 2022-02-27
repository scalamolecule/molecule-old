package moleculeTests.tests.core.pagination

import molecule.core.util.Executor._
import molecule.datomic.api.in1_out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.testing.expectCompileError
import moleculeTests.tests.core.pagination.OffsetPagination.core


object SortAttrs extends AsyncTestSuite {


  lazy val tests = Tests {

    "Types" - core { implicit conn =>
      for {
        _ <- Ns.str.insert(str3, str1, str2)
        _ <- Ns.int.insert(int3, int1, int2)
        _ <- Ns.long.insert(long3, long1, long2)
        _ <- Ns.double.insert(double3, double1, double2)
        _ <- Ns.bool.insert(true, false)
        _ <- Ns.date.insert(date3, date1, date2)
        _ <- Ns.uuid.insert(uuid3, uuid1, uuid2)
        _ <- Ns.uri.insert(uri3, uri1, uri2)
        _ <- Ns.bigInt.insert(bigInt3, bigInt1, bigInt2)
        _ <- Ns.bigDec.insert(bigDec3, bigDec1, bigDec2)
        _ <- Ns.enumm.insert(enum3, enum1, enum2)
        _ <- Ns.ref1.insert(r3, r1, r2)

        // Ascending
        _ <- Ns.str.a1.get.map(_ ==> List(str1, str2, str3))
        _ <- Ns.int.a1.get.map(_ ==> List(int1, int2, int3))
        _ <- Ns.long.a1.get.map(_ ==> List(long1, long2, long3))
        _ <- Ns.double.a1.get.map(_ ==> List(double1, double2, double3))
        _ <- Ns.bool.a1.get.map(_ ==> List(false, true)) // false before true
        _ <- Ns.date.a1.get.map(_ ==> List(date1, date2, date3))
        _ <- Ns.uuid.a1.get.map(_ ==> List(uuid1, uuid2, uuid3))
        _ <- Ns.uri.a1.get.map(_ ==> List(uri1, uri2, uri3))
        _ <- Ns.bigInt.a1.get.map(_ ==> List(bigInt1, bigInt2, bigInt3))
        _ <- Ns.bigDec.a1.get.map(_ ==> List(bigDec1, bigDec2, bigDec3))
        _ <- Ns.enumm.a1.get.map(_ ==> List(enum1, enum2, enum3))
        _ <- Ns.ref1.a1.get.map(_ ==> List(r1, r2, r3))

        // Descending
        _ <- Ns.str.d1.get.map(_ ==> List(str3, str2, str1))
        _ <- Ns.int.d1.get.map(_ ==> List(int3, int2, int1))
        _ <- Ns.long.d1.get.map(_ ==> List(long3, long2, long1))
        _ <- Ns.double.d1.get.map(_ ==> List(double3, double2, double1))
        _ <- Ns.bool.d1.get.map(_ ==> List(true, false))
        _ <- Ns.date.d1.get.map(_ ==> List(date3, date2, date1))
        _ <- Ns.uuid.d1.get.map(_ ==> List(uuid3, uuid2, uuid1))
        _ <- Ns.uri.d1.get.map(_ ==> List(uri3, uri2, uri1))
        _ <- Ns.bigInt.d1.get.map(_ ==> List(bigInt3, bigInt2, bigInt1))
        _ <- Ns.bigDec.d1.get.map(_ ==> List(bigDec3, bigDec2, bigDec1))
        _ <- Ns.enumm.d1.get.map(_ ==> List(enum3, enum2, enum1))
        _ <- Ns.ref1.d1.get.map(_ ==> List(r3, r2, r1))
      } yield ()
    }


    "Optional" - core { implicit conn =>
      for {
        _ <- Ns.int.str$ insert List((1, Some(str2)), (1, None), (1, Some(str1)))
        _ <- Ns.long.int$ insert List((20, Some(21)), (20, None), (20, Some(22)))
        _ <- Ns.int.long$ insert List((3, Some(long2)), (3, None), (3, Some(long1)))
        _ <- Ns.int.double$ insert List((4, Some(double2)), (4, None), (4, Some(double1)))
        _ <- Ns.int.bool$ insert List((5, Some(true)), (5, None), (5, Some(false)))
        _ <- Ns.int.date$ insert List((6, Some(date2)), (6, None), (6, Some(date1)))
        _ <- Ns.int.uuid$ insert List((7, Some(uuid2)), (7, None), (7, Some(uuid1)))
        _ <- Ns.int.uri$ insert List((8, Some(uri2)), (8, None), (8, Some(uri1)))
        _ <- Ns.int.bigInt$ insert List((9, Some(bigInt2)), (9, None), (9, Some(bigInt1)))
        _ <- Ns.int.bigDec$ insert List((10, Some(bigDec2)), (10, None), (10, Some(bigDec1)))
        _ <- Ns.int.enumm$ insert List((11, Some(enum2)), (11, None), (11, Some(enum1)))
        _ <- Ns.int.ref1$ insert List((12, Some(r2)), (12, None), (12, Some(r1)))

        // Ascending
        _ <- Ns.int(1).str$.a1.get.map(_ ==> List((1, None), (1, Some(str1)), (1, Some(str2))))
        _ <- Ns.long(20).int$.a1.get.map(_ ==> List((20, None), (20, Some(21)), (20, Some(22))))
        _ <- Ns.int(3).long$.a1.get.map(_ ==> List((3, None), (3, Some(long1)), (3, Some(long2))))
        _ <- Ns.int(4).double$.a1.get.map(_ ==> List((4, None), (4, Some(double1)), (4, Some(double2))))
        _ <- Ns.int(5).bool$.a1.get.map(_ ==> List((5, None), (5, Some(false)), (5, Some(true))))
        _ <- Ns.int(6).date$.a1.get.map(_ ==> List((6, None), (6, Some(date1)), (6, Some(date2))))
        _ <- Ns.int(7).uuid$.a1.get.map(_ ==> List((7, None), (7, Some(uuid1)), (7, Some(uuid2))))
        _ <- Ns.int(8).uri$.a1.get.map(_ ==> List((8, None), (8, Some(uri1)), (8, Some(uri2))))
        _ <- Ns.int(9).bigInt$.a1.get.map(_ ==> List((9, None), (9, Some(bigInt1)), (9, Some(bigInt2))))
        _ <- Ns.int(10).bigDec$.a1.get.map(_ ==> List((10, None), (10, Some(bigDec1)), (10, Some(bigDec2))))
        _ <- Ns.int(11).enumm$.a1.get.map(_ ==> List((11, None), (11, Some(enum1)), (11, Some(enum2))))
        _ <- Ns.int(12).ref1$.a1.get.map(_ ==> List((12, None), (12, Some(r1)), (12, Some(r2))))

        // Descending
        _ <- Ns.int(1).str$.d1.get.map(_ ==> List((1, Some(str2)), (1, Some(str1)), (1, None)))
        _ <- Ns.long(20).int$.d1.get.map(_ ==> List((20, Some(22)), (20, Some(21)), (20, None)))
        _ <- Ns.int(3).long$.d1.get.map(_ ==> List((3, Some(long2)), (3, Some(long1)), (3, None)))
        _ <- Ns.int(4).double$.d1.get.map(_ ==> List((4, Some(double2)), (4, Some(double1)), (4, None)))
        _ <- Ns.int(5).bool$.d1.get.map(_ ==> List((5, Some(true)), (5, Some(false)), (5, None)))
        _ <- Ns.int(6).date$.d1.get.map(_ ==> List((6, Some(date2)), (6, Some(date1)), (6, None)))
        _ <- Ns.int(7).uuid$.d1.get.map(_ ==> List((7, Some(uuid2)), (7, Some(uuid1)), (7, None)))
        _ <- Ns.int(8).uri$.d1.get.map(_ ==> List((8, Some(uri2)), (8, Some(uri1)), (8, None)))
        _ <- Ns.int(9).bigInt$.d1.get.map(_ ==> List((9, Some(bigInt2)), (9, Some(bigInt1)), (9, None)))
        _ <- Ns.int(10).bigDec$.d1.get.map(_ ==> List((10, Some(bigDec2)), (10, Some(bigDec1)), (10, None)))
        _ <- Ns.int(11).enumm$.d1.get.map(_ ==> List((11, Some(enum2)), (11, Some(enum1)), (11, None)))
        _ <- Ns.int(12).ref1$.d1.get.map(_ ==> List((12, Some(r2)), (12, Some(r1)), (12, None)))
      } yield ()
    }


    "Aggregates" - core { implicit conn =>
      for {
        _ <- Ns.int.str insert List(
          (1, "a"),
          (2, "b"),
          (3, "a"),
          (4, "c"),
        )

        // min
        _ <- Ns.int(min).a1.str.get.map(_ ==> List(
          (1, "a"),
          (2, "b"),
          (4, "c"),
        ))
        _ <- Ns.int(min).d1.str.get.map(_ ==> List(
          (4, "c"),
          (2, "b"),
          (1, "a"),
        ))
        _ = expectCompileError("m(Ns.int(min(2)).a1)",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "Can't sort list of values from attributes with aggregate min/max/rand/sample with applied limit. " +
            "Found: int(min(2)).a1"
        )

        // max
        _ <- Ns.int(max).a1.str.get.map(_ ==> List(
          (2, "b"),
          (3, "a"),
          (4, "c"),
        ))
        _ <- Ns.int(max).d1.str.get.map(_ ==> List(
          (4, "c"),
          (3, "a"),
          (2, "b"),
        ))
        _ = expectCompileError("m(Ns.int(max(2)).a1)",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "Can't sort list of values from attributes with aggregate min/max/rand/sample with applied limit. " +
            "Found: int(max(2)).a1"
        )

        // rand
        _ = expectCompileError("m(Ns.int(rand).a1)",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "Sorting by random/sample values not supported. Found: int(rand).a1"
        )
        _ = expectCompileError("m(Ns.int(rand(2)).a1)",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "Can't sort list of values from attributes with aggregate min/max/rand/sample with applied limit. " +
            "Found: int(rand(2)).a1"
        )

        // sample
        _ = expectCompileError("m(Ns.int(sample).a1)",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "Sorting by random/sample values not supported. Found: int(sample).a1"
        )
        _ = expectCompileError("m(Ns.int(sample(2)).a1)",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "Can't sort list of values from attributes with aggregate min/max/rand/sample with applied limit. " +
            "Found: int(sample(2)).a1"
        )

        // distinct
        _ = expectCompileError("m(Ns.int(distinct).a1)",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "Can't sort list of distinct values. Found: int(distinct).a1"
        )
        
        // sum
        _ <- Ns.int(sum).a1.str.a2.get.map(_ ==> List(
          (2, "b"),
          (4, "a"),
          (4, "c"),
        ))
        _ <- Ns.int(sum).d1.str.d2.get.map(_ ==> List(
          (4, "c"),
          (4, "a"),
          (2, "b"),
        ))

        // median
        _ <- Ns.int(median).a1.str.a2.get.map(_ ==> List(
          (2, "a"),
          (2, "b"),
          (4, "c"),
        ))
        _ <- Ns.int(median).d1.str.d2.get.map(_ ==> List(
          (4, "c"),
          (2, "b"),
          (2, "a"),
        ))

        // count
        _ <- Ns.str(count).a1.str.a2.get.map(_ ==> List(
          (1, "b"),
          (1, "c"),
          (2, "a"),
        ))
        _ <- Ns.str(count).d1.str.a2.get.map(_ ==> List(
          (2, "a"),
          (1, "b"),
          (1, "c"),
        ))

        // countDistinct
        _ <- Ns.str(countDistinct).a1.str.a2.get.map(_ ==> List(
          (1, "a"),
          (1, "b"),
          (1, "c"),
        ))
        _ <- Ns.str(countDistinct).d1.str.d2.get.map(_ ==> List(
          (1, "c"),
          (1, "b"),
          (1, "a"),
        ))

        // avg
        _ <- Ns.int(avg).a1.str.a2.get.map(_ ==> List(
          (2.0, "a"),
          (2.0, "b"),
          (4.0, "c"),
        ))
        _ <- Ns.int(avg).d1.str.d2.get.map(_ ==> List(
          (4.0, "c"),
          (2.0, "b"),
          (2.0, "a"),
        ))

        // variance
        _ <- Ns.int(variance).a1.str.a2.get.map(_ ==> List(
          (0.0, "b"),
          (0.0, "c"),
          (1.0, "a"),
        ))
        _ <- Ns.int(variance).d1.str.d2.get.map(_ ==> List(
          (1.0, "a"),
          (0.0, "c"),
          (0.0, "b"),
        ))

        // stddev
        _ <- Ns.int(stddev).a1.str.a2.get.map(_ ==> List(
          (0.0, "b"),
          (0.0, "c"),
          (1.0, "a"),
        ))
        _ <- Ns.int(stddev).d1.str.d2.get.map(_ ==> List(
          (1.0, "a"),
          (0.0, "c"),
          (0.0, "b"),
        ))
      } yield ()
    }


    "Variables" - core { implicit conn =>
      for {
        _ <- Ns.str.insert(str3, str1, str2)
        _ <- Ns.str(str1, str2).a1.get.map(_ ==> List(str1, str2))
        _ <- Ns.str(str1, str2).d1.get.map(_ ==> List(str2, str1))
      } yield ()
    }


    "Input" - core { implicit conn =>
      for {
        _ <- Ns.str.insert(str3, str1, str2)
        _ <- m(Ns.str(?).a1)(Seq(str1, str2)).get.map(_ ==> List(str1, str2))
        _ <- m(Ns.str(?).d1)(Seq(str1, str2)).get.map(_ ==> List(str2, str1))
      } yield ()
    }


    "Tacit can't sort" - core { implicit conn =>
      expectCompileError("m(Ns.str.int_.a1)",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "Can't sort by tacit values. Found: int_.a1"
      )
    }

    "Can't apply operations on sort marker" - core { implicit conn =>
      expectCompileError("m(Ns.int.a1(max))",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "Sort marker `a1` can't have any operation applied."
      )
    }


    "2 sort markers" - core { implicit conn =>
      for {
        _ <- Ns.int.long insert List(
          (2, 3),
          (1, 4),
          (1, 3),
        )

        _ <- Ns.int.a1.long.a2.get.map(_ ==> List(
          (1, 3),
          (1, 4),
          (2, 3),
        ))
        _ <- Ns.int.a1.long.d2.get.map(_ ==> List(
          (1, 4),
          (1, 3),
          (2, 3),
        ))
        _ <- Ns.int.d1.long.a2.get.map(_ ==> List(
          (2, 3),
          (1, 3),
          (1, 4),
        ))
        _ <- Ns.int.d1.long.d2.get.map(_ ==> List(
          (2, 3),
          (1, 4),
          (1, 3),
        ))

        _ <- Ns.int.a2.long.a1.get.map(_ ==> List(
          (1, 3),
          (2, 3),
          (1, 4),
        ))
        _ <- Ns.int.a2.long.d1.get.map(_ ==> List(
          (1, 4),
          (1, 3),
          (2, 3),
        ))
        _ <- Ns.int.d2.long.a1.get.map(_ ==> List(
          (2, 3),
          (1, 3),
          (1, 4),
        ))
        _ <- Ns.int.d2.long.d1.get.map(_ ==> List(
          (1, 4),
          (2, 3),
          (1, 3),
        ))
      } yield ()
    }


    "All 5 sort markers" - core { implicit conn =>
      for {
        _ <- Ns.bool.str.uri.int.long insert List(
          (false, "b", uri4, 8, 16),
          (true, "a", uri2, 3, 5),
          (true, "b", uri4, 7, 13),
          (false, "a", uri1, 2, 3),
          (true, "a", uri1, 1, 2),
          (true, "a", uri2, 4, 7),
          (false, "b", uri4, 8, 15),
          (true, "a", uri2, 4, 8),
          (false, "b", uri4, 7, 13),
          (false, "b", uri3, 5, 9),
          (true, "b", uri3, 6, 12),
          (false, "a", uri1, 2, 4),
          (true, "a", uri1, 2, 4),
          (true, "b", uri4, 8, 15),
          (true, "a", uri1, 1, 1),
          (false, "b", uri3, 6, 11),
          (false, "b", uri4, 7, 14),
          (false, "a", uri1, 1, 1),
          (false, "a", uri2, 3, 6),
          (true, "a", uri2, 3, 6),
          (true, "b", uri4, 8, 16),
          (false, "b", uri3, 6, 12),
          (false, "a", uri2, 4, 7),
          (false, "a", uri1, 1, 2),
          (true, "b", uri3, 5, 9),
          (false, "a", uri2, 4, 8),
          (true, "b", uri3, 6, 11),
          (true, "b", uri4, 7, 14),
          (true, "b", uri3, 5, 10),
          (false, "b", uri3, 5, 10),
          (false, "a", uri2, 3, 5),
          (true, "a", uri1, 2, 3),
        )


        // Ascending
        _ <- Ns.bool.a1.str.a2.uri.a3.int.a4.long.a5.get.map(_ ==> List(
          (false, "a", uri1, 1, 1),
          (false, "a", uri1, 1, 2),
          (false, "a", uri1, 2, 3),
          (false, "a", uri1, 2, 4),
          (false, "a", uri2, 3, 5),
          (false, "a", uri2, 3, 6),
          (false, "a", uri2, 4, 7),
          (false, "a", uri2, 4, 8),
          (false, "b", uri3, 5, 9),
          (false, "b", uri3, 5, 10),
          (false, "b", uri3, 6, 11),
          (false, "b", uri3, 6, 12),
          (false, "b", uri4, 7, 13),
          (false, "b", uri4, 7, 14),
          (false, "b", uri4, 8, 15),
          (false, "b", uri4, 8, 16),
          (true, "a", uri1, 1, 1),
          (true, "a", uri1, 1, 2),
          (true, "a", uri1, 2, 3),
          (true, "a", uri1, 2, 4),
          (true, "a", uri2, 3, 5),
          (true, "a", uri2, 3, 6),
          (true, "a", uri2, 4, 7),
          (true, "a", uri2, 4, 8),
          (true, "b", uri3, 5, 9),
          (true, "b", uri3, 5, 10),
          (true, "b", uri3, 6, 11),
          (true, "b", uri3, 6, 12),
          (true, "b", uri4, 7, 13),
          (true, "b", uri4, 7, 14),
          (true, "b", uri4, 8, 15),
          (true, "b", uri4, 8, 16),
        ))

        // Descending
        _ <- Ns.bool.d1.str.d2.uri.d3.int.d4.long.d5.get.map(_ ==> List(
          (true, "b", uri4, 8, 16),
          (true, "b", uri4, 8, 15),
          (true, "b", uri4, 7, 14),
          (true, "b", uri4, 7, 13),
          (true, "b", uri3, 6, 12),
          (true, "b", uri3, 6, 11),
          (true, "b", uri3, 5, 10),
          (true, "b", uri3, 5, 9),
          (true, "a", uri2, 4, 8),
          (true, "a", uri2, 4, 7),
          (true, "a", uri2, 3, 6),
          (true, "a", uri2, 3, 5),
          (true, "a", uri1, 2, 4),
          (true, "a", uri1, 2, 3),
          (true, "a", uri1, 1, 2),
          (true, "a", uri1, 1, 1),
          (false, "b", uri4, 8, 16),
          (false, "b", uri4, 8, 15),
          (false, "b", uri4, 7, 14),
          (false, "b", uri4, 7, 13),
          (false, "b", uri3, 6, 12),
          (false, "b", uri3, 6, 11),
          (false, "b", uri3, 5, 10),
          (false, "b", uri3, 5, 9),
          (false, "a", uri2, 4, 8),
          (false, "a", uri2, 4, 7),
          (false, "a", uri2, 3, 6),
          (false, "a", uri2, 3, 5),
          (false, "a", uri1, 2, 4),
          (false, "a", uri1, 2, 3),
          (false, "a", uri1, 1, 2),
          (false, "a", uri1, 1, 1),
        ))

        // Mixed order
        _ <- Ns.bool.a3.str.d1.uri.d4.int.a2.long.d5.get.map(_ ==> List(
          (false, "b", uri3, 5, 10),
          (false, "b", uri3, 5, 9),
          (true, "b", uri3, 5, 10),
          (true, "b", uri3, 5, 9),

          (false, "b", uri3, 6, 12),
          (false, "b", uri3, 6, 11),
          (true, "b", uri3, 6, 12),
          (true, "b", uri3, 6, 11),

          (false, "b", uri4, 7, 14),
          (false, "b", uri4, 7, 13),
          (true, "b", uri4, 7, 14),
          (true, "b", uri4, 7, 13),

          (false, "b", uri4, 8, 16),
          (false, "b", uri4, 8, 15),
          (true, "b", uri4, 8, 16),
          (true, "b", uri4, 8, 15),


          (false, "a", uri1, 1, 2),
          (false, "a", uri1, 1, 1),
          (true, "a", uri1, 1, 2),
          (true, "a", uri1, 1, 1),

          (false, "a", uri1, 2, 4),
          (false, "a", uri1, 2, 3),
          (true, "a", uri1, 2, 4),
          (true, "a", uri1, 2, 3),

          (false, "a", uri2, 3, 6),
          (false, "a", uri2, 3, 5),
          (true, "a", uri2, 3, 6),
          (true, "a", uri2, 3, 5),

          (false, "a", uri2, 4, 8),
          (false, "a", uri2, 4, 7),
          (true, "a", uri2, 4, 8),
          (true, "a", uri2, 4, 7),
        ))
      } yield ()
    }
  }
}
