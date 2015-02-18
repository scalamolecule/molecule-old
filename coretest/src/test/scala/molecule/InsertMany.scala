package molecule

import java.net.URI
import java.util.{Date, UUID}

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}
import shapeless._

class InsertMany extends CoreSpec {


  "1 attribute" in new CoreSetup {

    // The `insert` method performs the compile time analysis of the molecule
    // The `apply` method inserts the type-inferred data at runtime
    Ns.strs.insert.apply(Set("a"))

    // We can enter data for one attribute in 4 different ways:

    // 1. Set of values
    Ns.strs insert Set("b")
    Ns.strs insert Set("c", "d")

    // 2. Comma-separated list of sets of values
    Ns.strs.insert(Set("e"), Set("f"))

    // 3. List of sets of values
    Ns.strs insert List(Set("g"))
    Ns.strs insert List(Set("h", "i"))
    Ns.strs insert List(Set("j"), Set("k"))

    // 4. Arity-1 HList
    Ns.strs insert Set("l") :: HNil
    Ns.strs insert Set("m", "n") :: HNil

    // 5. List of Arity-1 HLists
    Ns.strs insert List(Set("o") :: HNil)
    Ns.strs insert List(Set("p", "q") :: HNil)
    Ns.strs insert List(Set("r") :: HNil, Set("s") :: HNil)

    // All values inserted
    Ns.strs.one.toSeq.sorted === List("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s")


    Ns.ints.insert(Set(1))
    Ns.ints.insert(Set(2, 3))
    Ns.ints.insert(Set(4), Set(5))
    Ns.ints.insert(List(Set(6)))
    Ns.ints.insert(List(Set(7, 8)))
    Ns.ints.insert(List(Set(9), Set(10)))

    Ns.ints.insert(Set(11) :: HNil)
    Ns.ints.insert(Set(12, 13) :: HNil)
    Ns.ints.insert(List(Set(14) :: HNil))
    Ns.ints.insert(List(Set(15, 16) :: HNil))
    Ns.ints.insert(List(Set(17) :: HNil, Set(18) :: HNil))

    Ns.ints.one.toSeq.sorted === List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)


    Ns.longs.insert(Set(1L))
    Ns.longs.insert(Set(2L, 3L))
    Ns.longs.insert(Set(4L), Set(5L))
    Ns.longs.insert(List(Set(6L)))
    Ns.longs.insert(List(Set(7L, 8L)))
    Ns.longs.insert(List(Set(9L), Set(10L)))

    Ns.longs.insert(Set(11L) :: HNil)
    Ns.longs.insert(Set(12L, 13L) :: HNil)
    Ns.longs.insert(List(Set(14L) :: HNil))
    Ns.longs.insert(List(Set(15L, 16L) :: HNil))
    Ns.longs.insert(List(Set(17L) :: HNil, Set(18L) :: HNil))

    Ns.longs.one.toSeq.sorted === List(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L)


    Ns.floats.insert(Set(1.0f))
    Ns.floats.insert(Set(2.0f, 3.0f))
    Ns.floats.insert(Set(4.0f), Set(5.0f))
    Ns.floats.insert(List(Set(6.0f)))
    Ns.floats.insert(List(Set(7.0f, 8.0f)))
    Ns.floats.insert(List(Set(9.0f), Set(10.0f)))

    Ns.floats.insert(Set(11.0f) :: HNil)
    Ns.floats.insert(Set(12.0f, 13.0f) :: HNil)
    Ns.floats.insert(List(Set(14.0f) :: HNil))
    Ns.floats.insert(List(Set(15.0f, 16.0f) :: HNil))
    Ns.floats.insert(List(Set(17.0f) :: HNil, Set(18.0f) :: HNil))

    Ns.floats.one.toSeq.sorted === List(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f, 11.0f, 12.0f, 13.0f, 14.0f, 15.0f, 16.0f, 17.0f, 18.0f)


    Ns.doubles.insert(Set(1.0))
    Ns.doubles.insert(Set(2.0, 3.0))
    Ns.doubles.insert(Set(4.0), Set(5.0))
    Ns.doubles.insert(List(Set(6.0)))
    Ns.doubles.insert(List(Set(7.0, 8.0)))
    Ns.doubles.insert(List(Set(9.0), Set(10.0)))

    Ns.doubles.insert(Set(11.0) :: HNil)
    Ns.doubles.insert(Set(12.0, 13.0) :: HNil)
    Ns.doubles.insert(List(Set(14.0) :: HNil))
    Ns.doubles.insert(List(Set(15.0, 16.0) :: HNil))
    Ns.doubles.insert(List(Set(17.0) :: HNil, Set(18.0) :: HNil))

    Ns.doubles.one.toSeq.sorted === List(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0)


    Ns.dates.insert(Set(date1))
    Ns.dates.insert(Set(date2, date3))
    Ns.dates.insert(Set(date4), Set(date5))
    Ns.dates.insert(List(Set(date6)))
    Ns.dates.insert(List(Set(date7, date8)))
    Ns.dates.insert(List(Set(date9), Set(date10)))

    Ns.dates.insert(Set(date11) :: HNil)
    Ns.dates.insert(Set(date12, date13) :: HNil)
    Ns.dates.insert(List(Set(date14) :: HNil))
    Ns.dates.insert(List(Set(date15, date16) :: HNil))
    Ns.dates.insert(List(Set(date17) :: HNil, Set(date18) :: HNil))

    Ns.dates.one.toSeq.sorted === List(date1, date2, date3, date4, date5, date6, date7, date8, date9, date10, date11, date12, date13, date14, date15, date16, date17, date18)


    Ns.uuids.insert(Set(uuid1))
    Ns.uuids.insert(Set(uuid2, uuid3))
    Ns.uuids.insert(Set(uuid4), Set(uuid5))
    Ns.uuids.insert(List(Set(uuid6)))
    Ns.uuids.insert(List(Set(uuid7, uuid8)))
    Ns.uuids.insert(List(Set(uuid9), Set(uuid10)))

    Ns.uuids.insert(Set(uuid11) :: HNil)
    Ns.uuids.insert(Set(uuid12, uuid13) :: HNil)
    Ns.uuids.insert(List(Set(uuid14) :: HNil))
    Ns.uuids.insert(List(Set(uuid15, uuid16) :: HNil))
    Ns.uuids.insert(List(Set(uuid17) :: HNil, Set(uuid18) :: HNil))

    Ns.uuids.one.toSeq.sortBy(_.toString) === List(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6, uuid7, uuid8, uuid9, uuid10, uuid11, uuid12, uuid13, uuid14, uuid15, uuid16, uuid17, uuid18)


    Ns.uris.insert(Set(uri1))
    Ns.uris.insert(Set(uri2, uri3))
    Ns.uris.insert(Set(uri4), Set(uri5))
    Ns.uris.insert(List(Set(uri6)))
    Ns.uris.insert(List(Set(uri7, uri8)))
    Ns.uris.insert(List(Set(uri9), Set(uri10)))

    Ns.uris.insert(Set(uri11) :: HNil)
    Ns.uris.insert(Set(uri12, uri13) :: HNil)
    Ns.uris.insert(List(Set(uri14) :: HNil))
    Ns.uris.insert(List(Set(uri15, uri16) :: HNil))
    Ns.uris.insert(List(Set(uri17) :: HNil, Set(uri18) :: HNil))

    Ns.uris.one.toSeq.sortBy(_.toString) === List(uri1, uri2, uri3, uri4, uri5, uri6, uri7, uri8, uri9, uri10, uri11, uri12, uri13, uri14, uri15, uri16, uri17, uri18)


    Ns.enums.insert(Set(enum1))
    Ns.enums.insert(Set(enum2, enum3))
    Ns.enums.insert(Set(enum4), Set(enum5))
    Ns.enums.insert(List(Set(enum6)))
    Ns.enums.insert(List(Set(enum7, enum8)))
    Ns.enums.insert(List(Set(enum9), Set(enum0)))

    Ns.enums.insert(Set(enum1) :: HNil)
    Ns.enums.insert(Set(enum2, enum3) :: HNil)
    Ns.enums.insert(List(Set(enum4) :: HNil))
    Ns.enums.insert(List(Set(enum5, enum6) :: HNil))
    Ns.enums.insert(List(Set(enum7) :: HNil, Set(enum8) :: HNil))

    Ns.enums.one.toSeq.sorted === List(enum0, enum1, enum2, enum3, enum4, enum5, enum6, enum7, enum8, enum9)
  }


  //  "Data-molecule, 1 attr" in new CoreSetup {
  //
  //    // Construct a "Data-Molecule" with an attribute value and add it to the database
  //
  //    Ns.str("a").add
  //    Ns.int(1).add
  //    Ns.long(1L).add
  //    Ns.float(1.0f).add
  //    Ns.double(1.0).add
  //    Ns.bool(true).add
  //    Ns.date(date1).add
  //    Ns.uuid(uuid1).add
  //    Ns.uri(uri1).add
  //    Ns.enum("enum1").add
  //
  //    Ns.str.one === "a"
  //    Ns.int.one === 1
  //    Ns.long.one === 1L
  //    Ns.float.one === 1.0f
  //    Ns.double.one === 1.0
  //    Ns.bool.one === true
  //    Ns.date.one === date1
  //    Ns.uuid.one === uuid1
  //    Ns.uri.one === uri1
  //    Ns.enum.one === enum1
  //  }
  //
  //
  //  "Data-molecule, n attrs" in new CoreSetup {
  //
  //    // Construct a "Data-Molecule" with multiple attributes populated with data and add it to the database
  //
  //    Ns.str("a").int(1).long(1L).float(1.0f).double(1.0).bool(true).date(date1).uuid(uuid1).uri(uri1).enum("enum1").add
  //
  //    Ns.str.int.long.float.double.bool.date.uuid.uri.enum.one ===(
  //      "a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1")
  //  }
  //
  //
  //  " Insert-Molecule n attributes" in new CoreSetup {
  //
  //    // Insert 3 entities as tuples of values
  //    // Note that values are typechecked against the attribute types of the molecule
  //    Ns.str.int.long.float.double.bool.date.uuid.uri.enum insert List(
  //      (" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
  //      ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
  //      ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
  //    )
  //
  //    Ns.str.int.long.float.double.bool.date.uuid.uri.enum.get.sortBy(_._1) === List(
  //      (" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
  //      ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
  //      ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
  //    )
  //  }
  //
  //
  //  " Insert-Molecule (2-step insertion)" in new CoreSetup {
  //
  //    // 1. Define "Insert-Molecule"
  //    val insertStr = Ns.str.insert
  //
  //    // 2. Re-use Insert-Molecule to insert values
  //    insertStr("a")
  //    insertStr("b")
  //    insertStr("c")
  //
  //    Ns.str.get.sorted === List("a", "b", "c")
  //
  //
  //    val insertAll = Ns.str.int.long.float.double.bool.date.uuid.uri.enum.insert
  //
  //    insertAll(" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0")
  //    insertAll(List(
  //      ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
  //      ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
  //    ))
  //
  //    Ns.str.int.long.float.double.bool.date.uuid.uri.enum.get.sortBy(_._1) === List(
  //      (" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
  //      ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
  //      ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
  //    )
  //  }
  //
  //
  //  " Insert inconsistent data sets" in new CoreSetup {
  //
  //    // If we have an inconsistent data set we can use typed `null` as
  //    // a placeholder for a missing value. When Molecule encounters a null
  //    // value it won't assert a fact about that attribute (simply skipping it is
  //    // different from for instance in SQL where a NULL value could be inserted).
  //
  //    Ns.str.int.long.float.double.bool.date.uuid.uri.enum insert List(
  //      (null.asInstanceOf[String], 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
  //      ("a", null.asInstanceOf[Int], 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
  //      ("b", 2, null.asInstanceOf[Long], 2.0f, 2.0, false, date2, uuid2, uri2, "enum2"),
  //      ("c", 3, 3L, null.asInstanceOf[Float], 3.0, true, date3, uuid3, uri3, "enum3"),
  //      ("d", 4, 4L, 4.0f, null.asInstanceOf[Double], false, date4, uuid4, uri4, "enum4"),
  //      ("e", 5, 5L, 5.0f, 5.0, null.asInstanceOf[Boolean], date5, uuid5, uri5, "enum5"),
  //      ("f", 6, 6L, 6.0f, 6.0, false, null.asInstanceOf[Date], uuid6, uri6, "enum6"),
  //      ("g", 7, 7L, 7.0f, 7.0, true, date7, null.asInstanceOf[UUID], uri7, "enum7"),
  //      ("h", 8, 8L, 8.0f, 8.0, false, date8, uuid8, null.asInstanceOf[URI], "enum8"),
  //      ("i", 9, 9L, 9.0f, 9.0, true, date9, uuid9, uri9, null.asInstanceOf[String])
  //    )
  //
  //    // Null values haven't been asserted:
  //
  //    // View created entities:
  //
  //    // Without str
  //    Ns.int.long.float.double.bool.date.uuid.uri.enum.one ===(0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0")
  //    // Without int
  //    Ns.str.long.float.double.bool.date.uuid.uri.enum.one ===("a", 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1")
  //    // Without long
  //    Ns.str.int.float.double.bool.date.uuid.uri.enum.one ===("b", 2, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
  //    // Without float
  //    Ns.str.int.long.double.bool.date.uuid.uri.enum.one ===("c", 3, 3L, 3.0, true, date3, uuid3, uri3, "enum3")
  //    // Without double
  //    Ns.str.int.long.float.bool.date.uuid.uri.enum.one ===("d", 4, 4L, 4.0f, false, date4, uuid4, uri4, "enum4")
  //    // Without bool
  //    Ns.str.int.long.float.double.date.uuid.uri.enum.one ===("e", 5, 5L, 5.0f, 5.0, date5, uuid5, uri5, "enum5")
  //    // Without date
  //    Ns.str.int.long.float.double.bool.uuid.uri.enum.one ===("f", 6, 6L, 6.0f, 6.0, false, uuid6, uri6, "enum6")
  //    // Without uuid
  //    Ns.str.int.long.float.double.bool.date.uri.enum.one ===("g", 7, 7L, 7.0f, 7.0, true, date7, uri7, "enum7")
  //    // Without uri
  //    Ns.str.int.long.float.double.bool.date.uuid.enum.one ===("h", 8, 8L, 8.0f, 8.0, false, date8, uuid8, "enum8")
  //    // Without enum
  //    Ns.str.int.long.float.double.bool.date.uuid.uri.one ===("i", 9, 9L, 9.0f, 9.0, true, date9, uuid9, uri9)
  //
  //
  //    // View attributes:
  //
  //    // No value " "
  //    Ns.str.get.sorted === List("a", "b", "c", "d", "e", "f", "g", "h", "i")
  //    // No value 1
  //    Ns.int.get.sorted === List(0, 2, 3, 4, 5, 6, 7, 8, 9)
  //    // No value 2L
  //    Ns.long.get.sorted === List(0L, 1L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)
  //    // No value 3.0f
  //    Ns.float.get.sorted === List(0.0f, 1.0f, 2.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f)
  //    // No value 4.0
  //    Ns.double.get.sorted === List(0.0, 1.0, 2.0, 3.0, 5.0, 6.0, 7.0, 8.0, 9.0)
  //    // (Coalesced values)
  //    Ns.bool.get.sorted === List(false, true)
  //    // No value date6
  //    Ns.date.get.sorted === List(date0, date1, date2, date3, date4, date5, date7, date8, date9)
  //    // No value uuid7
  //    Ns.uuid.get.sortBy(_.toString) === List(uuid0, uuid1, uuid2, uuid3, uuid4, uuid5, uuid6, uuid8, uuid9)
  //    // No value uri8
  //    Ns.uri.get.sortBy(_.toString) === List(uri0, uri1, uri2, uri3, uri4, uri5, uri6, uri7, uri9)
  //    // No value enum9
  //    Ns.enum.get.sorted === List(enum0, enum1, enum2, enum3, enum4, enum5, enum6, enum7, enum8)
  //  }
}