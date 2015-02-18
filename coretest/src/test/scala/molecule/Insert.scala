package molecule

import java.net.URI
import java.util.{Date, UUID}

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}
import shapeless._

class Insert extends CoreSpec {


  "1 attribute" in new CoreSetup {

    // The `insert` method performs the compile time analysis of the molecule
    // The `apply` method inserts the type-inferred data at runtime
    Ns.str.insert.apply("a")

    // We can enter data for one attribute in 4 different ways:

    // 1. Comma-separated list
    Ns.str insert "b"
    Ns.str.insert("c", "d")

    // 2. List of values
    Ns.str insert List("e")
    Ns.str insert List("f", "g")

    // 3. Arity-1 HList
    Ns.str.insert("h" :: HNil)

    // 4. List of Arity-1 HLists
    Ns.str insert List("i" :: HNil)
    Ns.str insert List("j" :: HNil, "k" :: HNil)

    // All values inserted
    Ns.str.get.sorted === List("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k")


    Ns.int.insert(1)
    Ns.int.insert(2, 3)
    Ns.int.insert(List(4))
    Ns.int.insert(List(5, 6))
    Ns.int.insert(7 :: HNil)
    Ns.int.insert(List(8 :: HNil))
    Ns.int.insert(List(9 :: HNil, 10 :: HNil))
    Ns.int.get.sorted === List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)


    Ns.long.insert(1L)
    Ns.long.insert(2L, 3L)
    Ns.long.insert(List(4L))
    Ns.long.insert(List(5L, 6L))
    Ns.long.insert(7L :: HNil)
    Ns.long.insert(List(8L :: HNil))
    Ns.long.insert(List(9L :: HNil, 10L :: HNil))
    Ns.long.get.sorted === List(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)


    Ns.float.insert(1.0f)
    Ns.float.insert(2.0f, 3.0f)
    Ns.float.insert(List(4.0f))
    Ns.float.insert(List(5.0f, 6.0f))
    Ns.float.insert(7.0f :: HNil)
    Ns.float.insert(List(8.0f :: HNil))
    Ns.float.insert(List(9.0f :: HNil, 10.0f :: HNil))
    Ns.float.get.sorted === List(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f)


    Ns.double.insert(1.0)
    Ns.double.insert(2.0, 3.0)
    Ns.double.insert(List(4.0))
    Ns.double.insert(List(5.0, 6.0))
    Ns.double.insert(7.0 :: HNil)
    Ns.double.insert(List(8.0 :: HNil))
    Ns.double.insert(List(9.0 :: HNil, 10.0 :: HNil))
    Ns.double.get.sorted === List(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)


    Ns.bool.insert(true)
    Ns.bool.insert(true, false)
    Ns.bool.insert(List(true))
    Ns.bool.insert(List(true, false))
    Ns.bool.insert(true :: HNil)
    Ns.bool.insert(List(true :: HNil))
    Ns.bool.insert(List(true :: HNil, false :: HNil))
    // Unique values coalesced
    Ns.bool.get.sorted === List(false, true)


    Ns.date.insert(date1)
    Ns.date.insert(date2, date3)
    Ns.date.insert(List(date4))
    Ns.date.insert(List(date3, date4))
    Ns.date.insert(date1 :: HNil)
    Ns.date.insert(List(date2 :: HNil))
    Ns.date.insert(List(date3 :: HNil, date4 :: HNil))
    // Unique values coalesced
    Ns.date.get.sorted === List(date1, date2, date3, date4)


    Ns.uuid.insert(uuid1)
    Ns.uuid.insert(uuid2, uuid3)
    Ns.uuid.insert(List(uuid4))
    Ns.uuid.insert(List(uuid3, uuid4))
    Ns.uuid.insert(uuid1 :: HNil)
    Ns.uuid.insert(List(uuid2 :: HNil))
    Ns.uuid.insert(List(uuid3 :: HNil, uuid4 :: HNil))
    // Unique values coalesced
    Ns.uuid.get.sortBy(_.toString) === List(uuid1, uuid2, uuid3, uuid4)


    Ns.uri.insert(uri1)
    Ns.uri.insert(uri2, uri3)
    Ns.uri.insert(List(uri4))
    Ns.uri.insert(List(uri3, uri4))
    Ns.uri.insert(uri1 :: HNil)
    Ns.uri.insert(List(uri2 :: HNil))
    Ns.uri.insert(List(uri3 :: HNil, uri4 :: HNil))
    // Unique values coalesced
    Ns.uri.get.sorted === List(uri1, uri2, uri3, uri4)


    Ns.enum.insert("enum1")
    Ns.enum.insert("enum2", "enum3")
    Ns.enum.insert(List("enum4"))
    Ns.enum.insert(List("enum3", "enum4"))
    Ns.enum.insert(enum1 :: HNil)
    Ns.enum.insert(List(enum2 :: HNil))
    Ns.enum.insert(List(enum3 :: HNil, enum4 :: HNil))
    // Unique values coalesced
    Ns.enum.get.sorted === List(enum1, enum2, enum3, enum4)
  }


  "Data-molecule, 1 attr" in new CoreSetup {

    // Construct a "Data-Molecule" with an attribute value and add it to the database

    Ns.str("a").add
    Ns.int(1).add
    Ns.long(1L).add
    Ns.float(1.0f).add
    Ns.double(1.0).add
    Ns.bool(true).add
    Ns.date(date1).add
    Ns.uuid(uuid1).add
    Ns.uri(uri1).add
    Ns.enum("enum1").add

    Ns.str.one === "a"
    Ns.int.one === 1
    Ns.long.one === 1L
    Ns.float.one === 1.0f
    Ns.double.one === 1.0
    Ns.bool.one === true
    Ns.date.one === date1
    Ns.uuid.one === uuid1
    Ns.uri.one === uri1
    Ns.enum.one === enum1
  }


  "Data-molecule, n attrs" in new CoreSetup {

    // Construct a "Data-Molecule" with multiple attributes populated with data and add it to the database

    Ns.str("a").int(1).long(1L).float(1.0f).double(1.0).bool(true).date(date1).uuid(uuid1).uri(uri1).enum("enum1").add

    Ns.str.int.long.float.double.bool.date.uuid.uri.enum.one ===(
      "a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1")
  }


  " Insert-Molecule n attributes" in new CoreSetup {

    // Insert 3 entities as tuples of values
    // Note that values are typechecked against the attribute types of the molecule
    Ns.str.int.long.float.double.bool.date.uuid.uri.enum insert List(
      (" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
      ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
      ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
    )

    Ns.str.int.long.float.double.bool.date.uuid.uri.enum.get.sortBy(_._1) === List(
      (" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
      ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
      ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
    )
  }


  " Insert-Molecule (2-step insertion)" in new CoreSetup {

    // 1. Define "Insert-Molecule"
    val insertStr = Ns.str.insert

    // 2. Re-use Insert-Molecule to insert values
    insertStr("a")
    insertStr("b")
    insertStr("c")

    Ns.str.get.sorted === List("a", "b", "c")


    val insertAll = Ns.str.int.long.float.double.bool.date.uuid.uri.enum.insert

    insertAll(" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0")
    insertAll(List(
      ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
      ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
    ))

    Ns.str.int.long.float.double.bool.date.uuid.uri.enum.get.sortBy(_._1) === List(
      (" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
      ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
      ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
    )
  }


  " Insert inconsistent data sets" in new CoreSetup {

    // If we have an inconsistent data set we can use typed `null` as
    // a placeholder for a missing value. When Molecule encounters a null
    // value it won't assert a fact about that attribute (simply skipping it is
    // different from for instance in SQL where a NULL value could be inserted).

    Ns.str.int.long.float.double.bool.date.uuid.uri.enum insert List(
      (null.asInstanceOf[String], 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
      ("a", null.asInstanceOf[Int], 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
      ("b", 2, null.asInstanceOf[Long], 2.0f, 2.0, false, date2, uuid2, uri2, "enum2"),
      ("c", 3, 3L, null.asInstanceOf[Float], 3.0, true, date3, uuid3, uri3, "enum3"),
      ("d", 4, 4L, 4.0f, null.asInstanceOf[Double], false, date4, uuid4, uri4, "enum4"),
      ("e", 5, 5L, 5.0f, 5.0, null.asInstanceOf[Boolean], date5, uuid5, uri5, "enum5"),
      ("f", 6, 6L, 6.0f, 6.0, false, null.asInstanceOf[Date], uuid6, uri6, "enum6"),
      ("g", 7, 7L, 7.0f, 7.0, true, date7, null.asInstanceOf[UUID], uri7, "enum7"),
      ("h", 8, 8L, 8.0f, 8.0, false, date8, uuid8, null.asInstanceOf[URI], "enum8"),
      ("i", 9, 9L, 9.0f, 9.0, true, date9, uuid9, uri9, null.asInstanceOf[String])
    )

    // Null values haven't been asserted:

    // View created entities:

    // Without str
    Ns.int.long.float.double.bool.date.uuid.uri.enum.one ===(0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0")
    // Without int
    Ns.str.long.float.double.bool.date.uuid.uri.enum.one ===("a", 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1")
    // Without long
    Ns.str.int.float.double.bool.date.uuid.uri.enum.one ===("b", 2, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
    // Without float
    Ns.str.int.long.double.bool.date.uuid.uri.enum.one ===("c", 3, 3L, 3.0, true, date3, uuid3, uri3, "enum3")
    // Without double
    Ns.str.int.long.float.bool.date.uuid.uri.enum.one ===("d", 4, 4L, 4.0f, false, date4, uuid4, uri4, "enum4")
    // Without bool
    Ns.str.int.long.float.double.date.uuid.uri.enum.one ===("e", 5, 5L, 5.0f, 5.0, date5, uuid5, uri5, "enum5")
    // Without date
    Ns.str.int.long.float.double.bool.uuid.uri.enum.one ===("f", 6, 6L, 6.0f, 6.0, false, uuid6, uri6, "enum6")
    // Without uuid
    Ns.str.int.long.float.double.bool.date.uri.enum.one ===("g", 7, 7L, 7.0f, 7.0, true, date7, uri7, "enum7")
    // Without uri
    Ns.str.int.long.float.double.bool.date.uuid.enum.one ===("h", 8, 8L, 8.0f, 8.0, false, date8, uuid8, "enum8")
    // Without enum
    Ns.str.int.long.float.double.bool.date.uuid.uri.one ===("i", 9, 9L, 9.0f, 9.0, true, date9, uuid9, uri9)


    // View attributes:

    // No value " "
    Ns.str.get.sorted === List("a", "b", "c", "d", "e", "f", "g", "h", "i")
    // No value 1
    Ns.int.get.sorted === List(0, 2, 3, 4, 5, 6, 7, 8, 9)
    // No value 2L
    Ns.long.get.sorted === List(0L, 1L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)
    // No value 3.0f
    Ns.float.get.sorted === List(0.0f, 1.0f, 2.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f)
    // No value 4.0
    Ns.double.get.sorted === List(0.0, 1.0, 2.0, 3.0, 5.0, 6.0, 7.0, 8.0, 9.0)
    // (Coalesced values)
    Ns.bool.get.sorted === List(false, true)
    // No value date6
    Ns.date.get.sorted === List(date0, date1, date2, date3, date4, date5, date7, date8, date9)
    // No value uuid7
    Ns.uuid.get.sortBy(_.toString) === List(uuid0, uuid1, uuid2, uuid3, uuid4, uuid5, uuid6, uuid8, uuid9)
    // No value uri8
    Ns.uri.get.sortBy(_.toString) === List(uri0, uri1, uri2, uri3, uri4, uri5, uri6, uri7, uri9)
    // No value enum9
    Ns.enum.get.sorted === List(enum0, enum1, enum2, enum3, enum4, enum5, enum6, enum7, enum8)
  }
}