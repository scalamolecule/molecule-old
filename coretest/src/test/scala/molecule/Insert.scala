//package molecule
//
//import java.net.URI
//import java.util.{Date, UUID}
//
//import molecule.util.dsl.coreTest._
//import molecule.util.{CoreSetup, CoreSpec}
//import shapeless._
//
//class Insert extends CoreSpec {
//
//
//  "Single attribute" >> {
//
//    "Card one" in new CoreSetup {
//
//      // The `insert` method performs the compile time analysis of the molecule
//      // The `apply` method inserts the type-inferred data at runtime
//      Ns.str.insert.apply("a")
//
//      // We can enter data for one attribute in 4 different ways:
//
//      // 1. Comma-separated list
//      Ns.str insert "b"
//      Ns.str.insert("c", "d")
//
//      // 2. List of values
//      Ns.str insert List("e")
//      Ns.str insert List("f", "g")
//
//      // 3. Arity-1 HList
//      Ns.str.insert("h" :: HNil)
//
//      // 4. List of Arity-1 HLists
//      Ns.str insert List("i" :: HNil)
//      Ns.str insert List("j" :: HNil, "k" :: HNil)
//
//      // All values inserted
//      Ns.str.get.sorted === List("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k")
//
//
//      Ns.int.insert(1)
//      Ns.int.insert(2, 3)
//      Ns.int.insert(List(4))
//      Ns.int.insert(List(5, 6))
//      Ns.int.insert(7 :: HNil)
//      Ns.int.insert(List(8 :: HNil))
//      Ns.int.insert(List(9 :: HNil, 10 :: HNil))
//      Ns.int.get.sorted === List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//
//
//      Ns.long.insert(1L)
//      Ns.long.insert(2L, 3L)
//      Ns.long.insert(List(4L))
//      Ns.long.insert(List(5L, 6L))
//      Ns.long.insert(7L :: HNil)
//      Ns.long.insert(List(8L :: HNil))
//      Ns.long.insert(List(9L :: HNil, 10L :: HNil))
//      Ns.long.get.sorted === List(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)
//
//
//      Ns.float.insert(1.0f)
//      Ns.float.insert(2.0f, 3.0f)
//      Ns.float.insert(List(4.0f))
//      Ns.float.insert(List(5.0f, 6.0f))
//      Ns.float.insert(7.0f :: HNil)
//      Ns.float.insert(List(8.0f :: HNil))
//      Ns.float.insert(List(9.0f :: HNil, 10.0f :: HNil))
//      Ns.float.get.sorted === List(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f)
//
//
//      Ns.double.insert(1.0)
//      Ns.double.insert(2.0, 3.0)
//      Ns.double.insert(List(4.0))
//      Ns.double.insert(List(5.0, 6.0))
//      Ns.double.insert(7.0 :: HNil)
//      Ns.double.insert(List(8.0 :: HNil))
//      Ns.double.insert(List(9.0 :: HNil, 10.0 :: HNil))
//      Ns.double.get.sorted === List(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
//
//
//      Ns.bool.insert(true)
//      Ns.bool.insert(true, false)
//      Ns.bool.insert(List(true))
//      Ns.bool.insert(List(true, false))
//      Ns.bool.insert(true :: HNil)
//      Ns.bool.insert(List(true :: HNil))
//      Ns.bool.insert(List(true :: HNil, false :: HNil))
//      // Unique values coalesced
//      Ns.bool.get.sorted === List(false, true)
//
//
//      Ns.date.insert(date1)
//      Ns.date.insert(date2, date3)
//      Ns.date.insert(List(date4))
//      Ns.date.insert(List(date3, date4))
//      Ns.date.insert(date1 :: HNil)
//      Ns.date.insert(List(date2 :: HNil))
//      Ns.date.insert(List(date3 :: HNil, date4 :: HNil))
//      // Unique values coalesced
//      Ns.date.get.sorted === List(date1, date2, date3, date4)
//
//
//      Ns.uuid.insert(uuid1)
//      Ns.uuid.insert(uuid2, uuid3)
//      Ns.uuid.insert(List(uuid4))
//      Ns.uuid.insert(List(uuid3, uuid4))
//      Ns.uuid.insert(uuid1 :: HNil)
//      Ns.uuid.insert(List(uuid2 :: HNil))
//      Ns.uuid.insert(List(uuid3 :: HNil, uuid4 :: HNil))
//      // Unique values coalesced
//      Ns.uuid.get.sortBy(_.toString) === List(uuid1, uuid2, uuid3, uuid4)
//
//
//      Ns.uri.insert(uri1)
//      Ns.uri.insert(uri2, uri3)
//      Ns.uri.insert(List(uri4))
//      Ns.uri.insert(List(uri3, uri4))
//      Ns.uri.insert(uri1 :: HNil)
//      Ns.uri.insert(List(uri2 :: HNil))
//      Ns.uri.insert(List(uri3 :: HNil, uri4 :: HNil))
//      // Unique values coalesced
//      Ns.uri.get.sorted === List(uri1, uri2, uri3, uri4)
//
//
//      Ns.enum.insert("enum1")
//      Ns.enum.insert("enum2", "enum3")
//      Ns.enum.insert(List("enum4"))
//      Ns.enum.insert(List("enum3", "enum4"))
//      Ns.enum.insert(enum1 :: HNil)
//      Ns.enum.insert(List(enum2 :: HNil))
//      Ns.enum.insert(List(enum3 :: HNil, enum4 :: HNil))
//      // Unique values coalesced
//      Ns.enum.get.sorted === List(enum1, enum2, enum3, enum4)
//    }
//
//
//    "Card many" in new CoreSetup {
//
//      // The `insert` method performs the compile time analysis of the molecule
//      // The `apply` method inserts the type-inferred data at runtime
//      Ns.strs.insert.apply(Set("a"))
//
//      // We can enter data for one attribute in 4 different ways:
//
//      // 1. Set of values
//      Ns.strs insert Set("b")
//      Ns.strs insert Set("c", "d")
//
//      // 2. Comma-separated list of sets of values
//      Ns.strs.insert(Set("e"), Set("f"))
//
//      // 3. List of sets of values
//      Ns.strs insert List(Set("g"))
//      Ns.strs insert List(Set("h", "i"))
//      Ns.strs insert List(Set("j"), Set("k"))
//
//      // 4. Arity-1 HList
//      Ns.strs insert Set("l") :: HNil
//      Ns.strs insert Set("m", "n") :: HNil
//
//      // 5. List of Arity-1 HLists
//      Ns.strs insert List(Set("o") :: HNil)
//      Ns.strs insert List(Set("p", "q") :: HNil)
//      Ns.strs insert List(Set("r") :: HNil, Set("s") :: HNil)
//
//      // All values inserted
//      Ns.strs.one.toSeq.sorted === List("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s")
//
//
//      Ns.ints.insert(Set(1))
//      Ns.ints.insert(Set(2, 3))
//      Ns.ints.insert(Set(4), Set(5))
//      Ns.ints.insert(List(Set(6)))
//      Ns.ints.insert(List(Set(7, 8)))
//      Ns.ints.insert(List(Set(9), Set(10)))
//
//      Ns.ints.insert(Set(11) :: HNil)
//      Ns.ints.insert(Set(12, 13) :: HNil)
//      Ns.ints.insert(List(Set(14) :: HNil))
//      Ns.ints.insert(List(Set(15, 16) :: HNil))
//      Ns.ints.insert(List(Set(17) :: HNil, Set(18) :: HNil))
//
//      Ns.ints.one.toSeq.sorted === List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)
//
//
//      Ns.longs.insert(Set(1L))
//      Ns.longs.insert(Set(2L, 3L))
//      Ns.longs.insert(Set(4L), Set(5L))
//      Ns.longs.insert(List(Set(6L)))
//      Ns.longs.insert(List(Set(7L, 8L)))
//      Ns.longs.insert(List(Set(9L), Set(10L)))
//
//      Ns.longs.insert(Set(11L) :: HNil)
//      Ns.longs.insert(Set(12L, 13L) :: HNil)
//      Ns.longs.insert(List(Set(14L) :: HNil))
//      Ns.longs.insert(List(Set(15L, 16L) :: HNil))
//      Ns.longs.insert(List(Set(17L) :: HNil, Set(18L) :: HNil))
//
//      Ns.longs.one.toSeq.sorted === List(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L)
//
//
//      Ns.floats.insert(Set(1.0f))
//      Ns.floats.insert(Set(2.0f, 3.0f))
//      Ns.floats.insert(Set(4.0f), Set(5.0f))
//      Ns.floats.insert(List(Set(6.0f)))
//      Ns.floats.insert(List(Set(7.0f, 8.0f)))
//      Ns.floats.insert(List(Set(9.0f), Set(10.0f)))
//
//      Ns.floats.insert(Set(11.0f) :: HNil)
//      Ns.floats.insert(Set(12.0f, 13.0f) :: HNil)
//      Ns.floats.insert(List(Set(14.0f) :: HNil))
//      Ns.floats.insert(List(Set(15.0f, 16.0f) :: HNil))
//      Ns.floats.insert(List(Set(17.0f) :: HNil, Set(18.0f) :: HNil))
//
//      Ns.floats.one.toSeq.sorted === List(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f, 11.0f, 12.0f, 13.0f, 14.0f, 15.0f, 16.0f, 17.0f, 18.0f)
//
//
//      Ns.doubles.insert(Set(1.0))
//      Ns.doubles.insert(Set(2.0, 3.0))
//      Ns.doubles.insert(Set(4.0), Set(5.0))
//      Ns.doubles.insert(List(Set(6.0)))
//      Ns.doubles.insert(List(Set(7.0, 8.0)))
//      Ns.doubles.insert(List(Set(9.0), Set(10.0)))
//
//      Ns.doubles.insert(Set(11.0) :: HNil)
//      Ns.doubles.insert(Set(12.0, 13.0) :: HNil)
//      Ns.doubles.insert(List(Set(14.0) :: HNil))
//      Ns.doubles.insert(List(Set(15.0, 16.0) :: HNil))
//      Ns.doubles.insert(List(Set(17.0) :: HNil, Set(18.0) :: HNil))
//
//      Ns.doubles.one.toSeq.sorted === List(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0)
//
//
//      Ns.dates.insert(Set(date1))
//      Ns.dates.insert(Set(date2, date3))
//      Ns.dates.insert(Set(date4), Set(date5))
//      Ns.dates.insert(List(Set(date6)))
//      Ns.dates.insert(List(Set(date7, date8)))
//      Ns.dates.insert(List(Set(date9), Set(date10)))
//
//      Ns.dates.insert(Set(date11) :: HNil)
//      Ns.dates.insert(Set(date12, date13) :: HNil)
//      Ns.dates.insert(List(Set(date14) :: HNil))
//      Ns.dates.insert(List(Set(date15, date16) :: HNil))
//      Ns.dates.insert(List(Set(date17) :: HNil, Set(date18) :: HNil))
//
//      Ns.dates.one.toSeq.sorted === List(date1, date2, date3, date4, date5, date6, date7, date8, date9, date10, date11, date12, date13, date14, date15, date16, date17, date18)
//
//
//      Ns.uuids.insert(Set(uuid1))
//      Ns.uuids.insert(Set(uuid2, uuid3))
//      Ns.uuids.insert(Set(uuid4), Set(uuid5))
//      Ns.uuids.insert(List(Set(uuid6)))
//      Ns.uuids.insert(List(Set(uuid7, uuid8)))
//      Ns.uuids.insert(List(Set(uuid9), Set(uuid10)))
//
//      Ns.uuids.insert(Set(uuid11) :: HNil)
//      Ns.uuids.insert(Set(uuid12, uuid13) :: HNil)
//      Ns.uuids.insert(List(Set(uuid14) :: HNil))
//      Ns.uuids.insert(List(Set(uuid15, uuid16) :: HNil))
//      Ns.uuids.insert(List(Set(uuid17) :: HNil, Set(uuid18) :: HNil))
//
//      Ns.uuids.one.toSeq.sortBy(_.toString) === List(uuid1, uuid2, uuid3, uuid4, uuid5, uuid6, uuid7, uuid8, uuid9, uuid10, uuid11, uuid12, uuid13, uuid14, uuid15, uuid16, uuid17, uuid18)
//
//
//      Ns.uris.insert(Set(uri1))
//      Ns.uris.insert(Set(uri2, uri3))
//      Ns.uris.insert(Set(uri4), Set(uri5))
//      Ns.uris.insert(List(Set(uri6)))
//      Ns.uris.insert(List(Set(uri7, uri8)))
//      Ns.uris.insert(List(Set(uri9), Set(uri10)))
//
//      Ns.uris.insert(Set(uri11) :: HNil)
//      Ns.uris.insert(Set(uri12, uri13) :: HNil)
//      Ns.uris.insert(List(Set(uri14) :: HNil))
//      Ns.uris.insert(List(Set(uri15, uri16) :: HNil))
//      Ns.uris.insert(List(Set(uri17) :: HNil, Set(uri18) :: HNil))
//
//      Ns.uris.one.toSeq.sortBy(_.toString) === List(uri1, uri10, uri11, uri12, uri13, uri14, uri15, uri16, uri17, uri18, uri2, uri3, uri4, uri5, uri6, uri7, uri8, uri9)
//
//
//      Ns.enums.insert(Set(enum1))
//      Ns.enums.insert(Set(enum2, enum3))
//      Ns.enums.insert(Set(enum4), Set(enum5))
//      Ns.enums.insert(List(Set(enum6)))
//      Ns.enums.insert(List(Set(enum7, enum8)))
//      Ns.enums.insert(List(Set(enum9), Set(enum0)))
//
//      Ns.enums.insert(Set(enum1) :: HNil)
//      Ns.enums.insert(Set(enum2, enum3) :: HNil)
//      Ns.enums.insert(List(Set(enum4) :: HNil))
//      Ns.enums.insert(List(Set(enum5, enum6) :: HNil))
//      Ns.enums.insert(List(Set(enum7) :: HNil, Set(enum8) :: HNil))
//
//      Ns.enums.one.toSeq.sorted === List(enum0, enum1, enum2, enum3, enum4, enum5, enum6, enum7, enum8, enum9)
//    }
//  }
//
//
//  "Multiple attributes" >> {
//
//    "Card one" in new CoreSetup {
//
//      // Insert 3 entities as tuples of values
//      // Note that values are typechecked against the attribute types of the molecule
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum insert List(
//        (" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
//        ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
//        ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
//      )
//
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.get.sortBy(_._1) === List(
//        (" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
//        ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
//        ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
//      )
//    }
//
//    "Card many" in new CoreSetup {
//
//      // Insert 3 entities as tuples of values
//      // Note that values are typechecked against the attribute types of the molecule
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums insert List(
//        (Set("a", "b"),
//          Set(1, 2),
//          Set(1L, 2L),
//          Set(1.0f, 2.0f),
//          Set(1.0, 2.0),
//          Set(date1, date2),
//          Set(uuid1, uuid2),
//          Set(uri1, uri2),
//          Set("enum1", "enum2")),
//        (Set("c", "d"),
//          Set(3, 4),
//          Set(3L, 4L),
//          Set(3.0f, 4.0f),
//          Set(3.0, 4.0),
//          Set(date3, date4),
//          Set(uuid3, uuid4),
//          Set(uri3, uri4),
//          Set("enum3", "enum4"))
//      )
//
//      // Unique values coalesced
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.one ===(
//        Set("d", "a", "b", "c"),
//        Set(1, 4, 3, 2),
//        Set(1L, 4L, 3L, 2L),
//        Set(2.0f, 4.0f, 1.0f, 3.0f),
//        Set(2.0, 4.0, 1.0, 3.0),
//        Set(date4, date1, date2, date3),
//        Set(uuid3, uuid2, uuid4, uuid1),
//        Set(uri1, uri2, uri3, uri4),
//        Set("enum4", "enum1", "enum3", "enum2"))
//    }
//  }
//
//
//  "Relationships" >> {
//
//    "Basics" in new CoreSetup {
//
//      // Asserting a fact in the `Ref1` namespace is the same as creating
//      // one in the `Ns` namespace (no references between the two are made):
//
//      val a0 = Ns.str.insert("a0").id
//      a0.touch === Map(
//        ":db/id" -> 17592186045478L,
//        ":ns/str" -> "a0")
//
//      val b0 = Ref1.str.insert("b0").id
//      b0.touch === Map(
//        ":db/id" -> 17592186045480L,
//        ":ref1/str" -> "b0")
//
//
//      // If we start from `Ns` and use `Ref1` as a reference to the
//      // `Ref1` namespace then a `:ns/ref1` reference we will also be
//      // created to link from `Ns` to `Ref1`. Note how no other attributes
//      // are asserted in the `Ns` namespace.
//
//      val b1 = Ns.Ref1.str.insert("b1").id
//      b1.touch === Map(
//        ":db/id" -> 17592186045482L, // base entity id
//        ":ns/ref1" -> Map(// ref to "ref1" namespace
//          ":db/id" -> 17592186045483L, // ref entity id
//          ":ref1/str" -> "b1"))
//      // ref attribute value
//
//
//      // If we also assert a fact in `Ns` we will get an entity with
//      // a :ns/str assertion ("a0") of namespace `Ns` and a reference to an entity
//      // with another :ref1/str assertion ("b1") in namespace `Ref1`:
//
//      val a0b1 = Ns.str.Ref1.str.insert("a0", "b1").id
//      a0b1.touch === Map(
//        ":db/id" -> 17592186045485L,
//        ":ns/str" -> "a0",
//        ":ns/ref1" -> Map(
//          ":db/id" -> 17592186045486L,
//          ":ref1/str" -> "b1"))
//
//
//      // We can expand our graph one level deeper
//
//      val a0b1c2 = Ns.str.Ref1.str.Ref2.str.insert("a0", "b1", "c2").id
//      a0b1c2.touch === Map(
//        ":db/id" -> 17592186045488L,
//        ":ns/ref1" -> Map(
//          ":db/id" -> 17592186045489L,
//          ":ref1/ref2" -> Map(
//            ":db/id" -> 17592186045490L,
//            ":ref2/str" -> "c2"),
//          ":ref1/str" -> "b1"),
//        ":ns/str" -> "a0")
//
//
//      // We don't have to assert values in all namespaces
//
//      val a0c2 = Ns.str.Ref1.Ref2.str.insert("a0", "c2").id
//      a0c2.touch === Map(
//        ":db/id" -> 17592186045492L,
//        ":ns/ref1" -> Map(
//          ":db/id" -> 17592186045493L,
//          ":ref1/ref2" -> Map(
//            ":db/id" -> 17592186045494L,
//            ":ref2/str" -> "c2")),
//        ":ns/str" -> "a0")
//
//      val c2 = Ns.Ref1.Ref2.str.insert("c2").id
//      c2.touch === Map(
//        ":db/id" -> 17592186045496L,
//        ":ns/ref1" -> Map(
//          ":db/id" -> 17592186045497L,
//          ":ref1/ref2" -> Map(
//            ":db/id" -> 17592186045498L,
//            ":ref2/str" -> "c2")))
//    }
//
//    "Multiple values across namespaces" in new CoreSetup {
//
//      Ns.str.int.Ref1.str.int.Ref2.str.int.insert("a0", 0, "b1", 1, "c2", 2)
//      Ns.str.int.Ref1.str.int.Ref2.str.int.one ===("a0", 0, "b1", 1, "c2", 2)
//
//      Ns.strs.ints.Ref1.strs.ints.Ref2.strs.ints.insert(Set("a0"), Set(0), Set("b1"), Set(1), Set("c2"), Set(2))
//      Ns.strs.ints.Ref1.strs.ints.Ref2.strs.ints.one ===(Set("a0"), Set(0), Set("b1"), Set(1), Set("c2"), Set(2))
//
//      // Address example
//      val address = Ns.str.Ref1.int.str.Ref2.str.insert("273 Broadway", 10700, "New York", "USA").id
//      address.touch === Map(
//        ":db/id" -> 17592186045486L,
//        ":ns/ref1" -> Map(
//          ":db/id" -> 17592186045487L,
//          ":ref1/int" -> 10700,
//          ":ref1/ref2" -> Map(":db/id" -> 17592186045488L, ":ref2/str" -> "USA"),
//          ":ref1/str" -> "New York"),
//        ":ns/str" -> "273 Broadway")
//    }
//
//    "Card many references" in new CoreSetup {
//
//      // Even with a cardinality-many reference we can still make
//      // a single reference
//      val id = Ns.Refs1.str.insert("r").id
//      id.touch === Map(
//        ":db/id" -> 17592186045478L,
//        ":ns/refs1" -> List(// <-- notice we have a list of references now (with one ref here)
//          Map(":db/id" -> 17592186045479L, ":ref1/str" -> "r")))
//
//
//      // Note that applying multiple values creates multiple base entities with a
//      // reference to each new `:ref1/str` assertion, so that we get the following:
//
//      val List(id1, ref1, id2, ref2) = Ns.Refs1.str.insert.apply("r", "s").ids
//      id1.touch === Map(
//        ":db/id" -> id1,
//        ":ns/refs1" -> List(
//          Map(":db/id" -> ref1, ":ref1/str" -> "r")))
//      id2.touch === Map(
//        ":db/id" -> id2,
//        ":ns/refs1" -> List(
//          Map(":db/id" -> ref2, ":ref1/str" -> "s")))
//    }
//
//    "Sub-molecules" in new CoreSetup {
//
//      // If we want to create two references from the same base entity we
//      // can us "group" notation `*` after our cardinality-many reference
//      // and then define what sub-attributes we want to add.
//
//      // Note that the "sub-molecule" we apply is treated as a single type
//      // - when more than 1 attribute, like a tuple.
//
//      Ns.Refs1.*(Ref1.str).insert(List("r1", "r2")).id.touch === Map(
//        ":db/id" -> 17592186045478L,
//        ":ns/refs1" -> List(
//          Map(":db/id" -> 17592186045479L, ":ref1/str" -> "r1"),
//          Map(":db/id" -> 17592186045480L, ":ref1/str" -> "r2")))
//
//      // Like the classical order/products example
//      // Note how our "sub-molecule" `Ref1.int.str` is regarded as
//      // one type `Seq[(Int, String)]` by the outer molecule
//
//      val order = m(Ns.str.Refs1 * Ref1.int.str).insert("order", List((4, "product1"), (7, "product2"))).id
//      order.touch === Map(
//        ":db/id" -> 17592186045482L,
//        ":ns/refs1" -> List(
//          Map(":db/id" -> 17592186045483L, ":ref1/int" -> 4, ":ref1/str" -> "product1"),
//          Map(":db/id" -> 17592186045484L, ":ref1/int" -> 7, ":ref1/str" -> "product2")),
//        ":ns/str" -> "order")
//    }
//  }
//
//
//  "Data-molecule" >> {
//
//    "Card one attr" in new CoreSetup {
//
//      // Construct a "Data-Molecule" with an attribute value and add it to the database
//
//      Ns.str("a").add
//      Ns.int(1).add
//      Ns.long(1L).add
//      Ns.float(1.0f).add
//      Ns.double(1.0).add
//      Ns.bool(true).add
//      Ns.date(date1).add
//      Ns.uuid(uuid1).add
//      Ns.uri(uri1).add
//      Ns.enum("enum1").add
//
//      Ns.str.one === "a"
//      Ns.int.one === 1
//      Ns.long.one === 1L
//      Ns.float.one === 1.0f
//      Ns.double.one === 1.0
//      Ns.bool.one === true
//      Ns.date.one === date1
//      Ns.uuid.one === uuid1
//      Ns.uri.one === uri1
//      Ns.enum.one === enum1
//    }
//
//    "Card many attr" in new CoreSetup {
//
//      // Construct a "Data-Molecule" with multiple attributes populated with data and add it to the database
//
//      Ns.strs("a", "b")
//        .ints(1, 2)
//        .longs(1L, 2L)
//        .floats(1.0f, 2.0f)
//        .doubles(1.0, 2.0)
//        .dates(date1, date2)
//        .uuids(uuid1, uuid2)
//        .uris(uri1, uri2)
//        .enums("enum1", "enum2").add
//
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.one ===(
//        Set("a", "b"),
//        Set(1, 2),
//        Set(1L, 2L),
//        Set(1.0f, 2.0f),
//        Set(1.0, 2.0),
//        Set(date1, date2),
//        Set(uuid1, uuid2),
//        Set(uri1, uri2),
//        Set("enum1", "enum2"))
//    }
//
//    "Card one attrs" in new CoreSetup {
//
//      // Construct a "Data-Molecule" with multiple attributes populated with data and add it to the database
//
//      Ns.str("a").int(1).long(1L).float(1.0f).double(1.0).bool(true).date(date1).uuid(uuid1).uri(uri1).enum("enum1").add
//
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.one ===(
//        "a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1")
//    }
//
//    "Card many attrs" in new CoreSetup {
//
//      // Construct a "Data-Molecule" with an attribute value and add it to the database
//
//      Ns.strs("a").add
//      Ns.strs("b", "c").add
//      Ns.strs.one === Set("a", "b", "c")
//
//      Ns.ints(1).add
//      Ns.ints(2, 3).add
//      Ns.ints.one === Set(1, 2, 3)
//
//      Ns.longs(1L).add
//      Ns.longs(2L, 3L).add
//      Ns.longs.one === Set(1L, 2L, 3L)
//
//      Ns.floats(1.0f).add
//      Ns.floats(2.0f, 3.0f).add
//      Ns.floats.one === Set(1.0f, 2.0f, 3.0f)
//
//      Ns.doubles(1.0).add
//      Ns.doubles(2.0, 3.0).add
//      Ns.doubles.one === Set(1.0, 2.0, 3.0)
//
//      // Ns.bools not implemented...
//
//      Ns.dates(date1).add
//      Ns.dates(date2, date3).add
//      Ns.dates.one === Set(date1, date2, date3)
//
//      Ns.uuids(uuid1).add
//      Ns.uuids(uuid2, uuid3).add
//      Ns.uuids.one === Set(uuid1, uuid2, uuid3)
//
//      Ns.uris(uri1).add
//      Ns.uris(uri2, uri3).add
//      Ns.uris.one === Set(uri1, uri2, uri3)
//
//      Ns.enums("enum1").add
//      Ns.enums("enum2", "enum3").add
//      Ns.enums.one === Set("enum1", "enum2", "enum3")
//    }
//
//    "Relationships" in new CoreSetup {
//
//      val address = Ns.str("273 Broadway").Ref1.int(10700).str("New York").Ref2.str("USA").add.id
//      address.touch === Map(
//        ":db/id" -> 17592186045478L,
//        ":ns/ref1" -> Map(
//          ":db/id" -> 17592186045479L,
//          ":ref1/int" -> 10700,
//          ":ref1/ref2" -> Map(":db/id" -> 17592186045480L, ":ref2/str" -> "USA"),
//          ":ref1/str" -> "New York"),
//        ":ns/str" -> "273 Broadway")
//
//      Ns.str.Ref1.int.str.Ref2.str.one ===("273 Broadway", 10700, "New York", "USA")
//    }
//  }
//
//
//  " Insert-molecule (2-step insertion)" >> {
//
//    "Card one" in new CoreSetup {
//
//      // 1. Define "Insert-molecule"
//      val insertStr = Ns.str.insert
//
//      // 2. Re-use Insert-molecule to insert values
//      insertStr("a")
//      insertStr("b")
//      insertStr("c")
//
//      Ns.str.get.sorted === List("a", "b", "c")
//
//
//      val insertAll = Ns.str.int.long.float.double.bool.date.uuid.uri.enum.insert
//
//      insertAll(" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0")
//      insertAll(List(
//        ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
//        ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
//      ))
//
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.get.sortBy(_._1) === List(
//        (" ", 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
//        ("a", 1, 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
//        ("b", 2, 2L, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
//      )
//    }
//
//
//    "Card many" in new CoreSetup {
//
//      // 1. Define "Insert-molecule"
//      val insertStrs = Ns.strs.insert
//
//      // 2. Re-use Insert-molecule to insert values
//      insertStrs(Set("a"))
//      insertStrs(Set("b", "c"))
//
//      Ns.strs.one === List("a", "b", "c")
//
//
//      val insertAlls = Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.insert
//
//      insertAlls(Set(" "), Set(0), Set(0L), Set(0.0f), Set(0.0), Set(date0), Set(uuid0), Set(uri0), Set("enum0"))
//      insertAlls(List(
//        (Set("a"), Set(1), Set(1L), Set(1.0f), Set(1.0), Set(date1), Set(uuid1), Set(uri1), Set("enum1")),
//        (Set("b"), Set(2), Set(2L), Set(2.0f), Set(2.0), Set(date2), Set(uuid2), Set(uri2), Set("enum2"))
//      ))
//
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.one ===(
//        Set("a", "b", " "),
//        Set(0, 1, 2),
//        Set(0L, 1L, 2L),
//        Set(0.0f, 2.0f, 1.0f),
//        Set(0.0, 2.0, 1.0),
//        Set(date0, date1, date2),
//        Set(uuid0, uuid1, uuid2),
//        Set(uri0, uri1, uri2),
//        Set("enum1", "enum0", "enum2"))
//    }
//
//    "Relationships" in new CoreSetup {
//
//      // 1. Define Input-molecule
//      val insertAddress = Ns.str.Ref1.int.str.Ref2.str.insert
//
//      // 2. Insert data usint Input-molecule as template
//      insertAddress("273 Broadway", 10700, "New York", "USA")
//      insertAddress("2054, 5th Ave", 10800, "New York", "USA")
//
//      Ns.str.Ref1.int.str.Ref2.str.get === List(
//        ("273 Broadway", 10700, "New York", "USA"),
//        ("2054, 5th Ave", 10800, "New York", "USA")
//      )
//    }
//  }
//
//
//  "Missing values" >> {
//
//    "Card one attrs" in new CoreSetup {
//
//      // If we have an inconsistent data set we can use typed `null` as
//      // a placeholder for a missing value. When Molecule encounters a null
//      // value it won't assert a fact about that attribute (simply skipping it is
//      // different from for instance in SQL where a NULL value could be inserted).
//
//      // to avoid implicit conversion collisions we need to cast our `null`s:
//
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum insert List(
//        (null.asInstanceOf[String], 0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0"),
//        ("a", null.asInstanceOf[Int], 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1"),
//        ("b", 2, null.asInstanceOf[Long], 2.0f, 2.0, false, date2, uuid2, uri2, "enum2"),
//        ("c", 3, 3L, null.asInstanceOf[Float], 3.0, true, date3, uuid3, uri3, "enum3"),
//        ("d", 4, 4L, 4.0f, null.asInstanceOf[Double], false, date4, uuid4, uri4, "enum4"),
//        ("e", 5, 5L, 5.0f, 5.0, null.asInstanceOf[Boolean], date5, uuid5, uri5, "enum5"),
//        ("f", 6, 6L, 6.0f, 6.0, false, null.asInstanceOf[Date], uuid6, uri6, "enum6"),
//        ("g", 7, 7L, 7.0f, 7.0, true, date7, null.asInstanceOf[UUID], uri7, "enum7"),
//        ("h", 8, 8L, 8.0f, 8.0, false, date8, uuid8, null.asInstanceOf[URI], "enum8"),
//        ("i", 9, 9L, 9.0f, 9.0, true, date9, uuid9, uri9, null.asInstanceOf[String])
//      )
//
//      // Null values haven't been asserted:
//
//      // View created entities:
//
//      // Without str
//      Ns.int.long.float.double.bool.date.uuid.uri.enum.one ===(0, 0L, 0.0f, 0.0, false, date0, uuid0, uri0, "enum0")
//      // Without int
//      Ns.str.long.float.double.bool.date.uuid.uri.enum.one ===("a", 1L, 1.0f, 1.0, true, date1, uuid1, uri1, "enum1")
//      // Without long
//      Ns.str.int.float.double.bool.date.uuid.uri.enum.one ===("b", 2, 2.0f, 2.0, false, date2, uuid2, uri2, "enum2")
//      // Without float
//      Ns.str.int.long.double.bool.date.uuid.uri.enum.one ===("c", 3, 3L, 3.0, true, date3, uuid3, uri3, "enum3")
//      // Without double
//      Ns.str.int.long.float.bool.date.uuid.uri.enum.one ===("d", 4, 4L, 4.0f, false, date4, uuid4, uri4, "enum4")
//      // Without bool
//      Ns.str.int.long.float.double.date.uuid.uri.enum.one ===("e", 5, 5L, 5.0f, 5.0, date5, uuid5, uri5, "enum5")
//      // Without date
//      Ns.str.int.long.float.double.bool.uuid.uri.enum.one ===("f", 6, 6L, 6.0f, 6.0, false, uuid6, uri6, "enum6")
//      // Without uuid
//      Ns.str.int.long.float.double.bool.date.uri.enum.one ===("g", 7, 7L, 7.0f, 7.0, true, date7, uri7, "enum7")
//      // Without uri
//      Ns.str.int.long.float.double.bool.date.uuid.enum.one ===("h", 8, 8L, 8.0f, 8.0, false, date8, uuid8, "enum8")
//      // Without enum
//      Ns.str.int.long.float.double.bool.date.uuid.uri.one ===("i", 9, 9L, 9.0f, 9.0, true, date9, uuid9, uri9)
//
//
//      // View attributes:
//
//      // No value " "
//      Ns.str.get.sorted === List("a", "b", "c", "d", "e", "f", "g", "h", "i")
//      // No value 1
//      Ns.int.get.sorted === List(0, 2, 3, 4, 5, 6, 7, 8, 9)
//      // No value 2L
//      Ns.long.get.sorted === List(0L, 1L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)
//      // No value 3.0f
//      Ns.float.get.sorted === List(0.0f, 1.0f, 2.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f)
//      // No value 4.0
//      Ns.double.get.sorted === List(0.0, 1.0, 2.0, 3.0, 5.0, 6.0, 7.0, 8.0, 9.0)
//      // (Coalesced values)
//      Ns.bool.get.sorted === List(false, true)
//      // No value date6
//      Ns.date.get.sorted === List(date0, date1, date2, date3, date4, date5, date7, date8, date9)
//      // No value uuid7
//      Ns.uuid.get.sortBy(_.toString) === List(uuid0, uuid1, uuid2, uuid3, uuid4, uuid5, uuid6, uuid8, uuid9)
//      // No value uri8
//      Ns.uri.get.sortBy(_.toString) === List(uri0, uri1, uri2, uri3, uri4, uri5, uri6, uri7, uri9)
//      // No value enum9
//      Ns.enum.get.sorted === List(enum0, enum1, enum2, enum3, enum4, enum5, enum6, enum7, enum8)
//    }
//
//
//    "Card many attrs" in new CoreSetup {
//
//      // If we have an inconsistent data set we can use typed `null` as
//      // a placeholder for a missing value. When Molecule encounters a null
//      // value it won't assert a fact about that attribute (simply skipping it is
//      // different from for instance in SQL where a NULL value could be inserted).
//
//      // On contrary to cardinality-one attributes, cardinality-many attributes don't collide
//      // with implicit conversions so we don't need to cast `null`s here:
//
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums insert List(
//        (null, Set(0), Set(0L), Set(0.0f), Set(0.0), Set(date0), Set(uuid0), Set(uri0), Set("enum0")),
//        (Set("a"), null, Set(1L), Set(1.0f), Set(1.0), Set(date1), Set(uuid1), Set(uri1), Set("enum1")),
//        (Set("b"), Set(2), null, Set(2.0f), Set(2.0), Set(date2), Set(uuid2), Set(uri2), Set("enum2")),
//        (Set("c"), Set(3), Set(3L), null, Set(3.0), Set(date3), Set(uuid3), Set(uri3), Set("enum3")),
//        (Set("d"), Set(4), Set(4L), Set(4.0f), null, Set(date4), Set(uuid4), Set(uri4), Set("enum4")),
//        (Set("e"), Set(5), Set(5L), Set(5.0f), Set(5.0), null, Set(uuid5), Set(uri5), Set("enum5")),
//        (Set("f"), Set(6), Set(6L), Set(6.0f), Set(6.0), Set(date6), null, Set(uri6), Set("enum6")),
//        (Set("g"), Set(7), Set(7L), Set(7.0f), Set(7.0), Set(date7), Set(uuid7), null, Set("enum7")),
//        (Set("h"), Set(8), Set(8L), Set(8.0f), Set(8.0), Set(date8), Set(uuid8), Set(uri8), null)
//      )
//
//      // Null values haven't been asserted:
//
//      // View created entities:
//
//      // Without str
//      Ns.ints.longs.floats.doubles.dates.uuids.uris.enums.one ===(Set(0), Set(0L), Set(0.0f), Set(0.0), Set(date0), Set(uuid0), Set(uri0), Set("enum0"))
//      // Without int
//      Ns.strs.longs.floats.doubles.dates.uuids.uris.enums.one ===(Set("a"), Set(1L), Set(1.0f), Set(1.0), Set(date1), Set(uuid1), Set(uri1), Set("enum1"))
//      // Without long
//      Ns.strs.ints.floats.doubles.dates.uuids.uris.enums.one ===(Set("b"), Set(2), Set(2.0f), Set(2.0), Set(date2), Set(uuid2), Set(uri2), Set("enum2"))
//      // Without float
//      Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.one ===(Set("c"), Set(3), Set(3L), Set(3.0), Set(date3), Set(uuid3), Set(uri3), Set("enum3"))
//      // Without double
//      Ns.strs.ints.longs.floats.dates.uuids.uris.enums.one ===(Set("d"), Set(4), Set(4L), Set(4.0f), Set(date4), Set(uuid4), Set(uri4), Set("enum4"))
//      // Without date
//      Ns.strs.ints.longs.floats.doubles.uuids.uris.enums.one ===(Set("e"), Set(5), Set(5L), Set(5.0f), Set(5.0), Set(uuid5), Set(uri5), Set("enum5"))
//      // Without uuid
//      Ns.strs.ints.longs.floats.doubles.dates.uris.enums.one ===(Set("f"), Set(6), Set(6L), Set(6.0f), Set(6.0), Set(date6), Set(uri6), Set("enum6"))
//      // Without uri
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.enums.one ===(Set("g"), Set(7), Set(7L), Set(7.0f), Set(7.0), Set(date7), Set(uuid7), Set("enum7"))
//      // Without enum
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.one ===(Set("h"), Set(8), Set(8L), Set(8.0f), Set(8.0), Set(date8), Set(uuid8), Set(uri8))
//
//
//      // View attributes:
//
//      // No value " "
//      Ns.strs.one === Set("a", "b", "c", "d", "e", "f", "g", "h")
//      // No value 1
//      Ns.ints.one === Set(0, 2, 3, 4, 5, 6, 7, 8)
//      // No value 2L
//      Ns.longs.one === Set(0L, 1L, 3L, 4L, 5L, 6L, 7L, 8L)
//      // No value 3.0f
//      Ns.floats.one === Set(0.0f, 1.0f, 2.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f)
//      // No value 4.0
//      Ns.doubles.one === Set(0.0, 1.0, 2.0, 3.0, 5.0, 6.0, 7.0, 8.0)
//      // No value date5
//      Ns.dates.one === Set(date0, date7, date2, date6, date3, date4, date8, date1)
//      // No value uuid6
//      Ns.uuids.one === Set(uuid1, uuid2, uuid3, uuid4, uuid5, uuid7, uuid8, uuid0)
//      // No value uri7
//      Ns.uris.one === Set(uri2, uri5, uri1, uri0, uri6, uri4, uri3, uri8)
//      // No value enum8
//      Ns.enums.one === Set(enum0, enum1, enum2, enum3, enum4, enum5, enum6, enum7)
//    }
//
//    "Relationships" in new CoreSetup {
//
//      Ns.str.Ref1.int.str.Ref2.str insert List(
//        (null, 10700, "New York", "USA"),
//        ("2054, 5th Ave", 10800, "New York", null),
//        ("Gotaplatsen 1", 41256, null, "Sweden")
//      )
//
//      Ns.Ref1.int.str.Ref2.str.one ===(10700, "New York", "USA")
//      Ns.str.Ref1.int.str.one ===("2054, 5th Ave", 10800, "New York")
//      Ns.str.Ref1.int.Ref2.str.one ===("Gotaplatsen 1", 41256, "Sweden")
//    }
//  }
//}