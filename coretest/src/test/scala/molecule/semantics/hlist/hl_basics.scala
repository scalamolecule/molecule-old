//package molecule.semantics.hlist
//import molecule.dsl.coreTest._
//import molecule.{CoreSetup, CoreSpec}
//import shapeless._
//
//
//class hl_basics extends CoreSpec {
//
//  "Single cardinality-1 attribute - one entity" in new CoreSetup {
//
//    // Create an entity with attribute `str` having value "foo"
//    Ns.str insert "foo" :: HNil
//
//    // Get one `str` value
//    // Since order of result is not guaranteed, a random value is returned
//    Ns.str.hl.head === "foo" :: HNil
//
//    // Get all values of attribute `str` (there's only one)
//    Ns.str.hl === List("foo" :: HNil)
//  }
//
//
//  "Single cardinality-1 attribute - multiple entities" in new CoreSetup {
//    Ns.int insert List(1 :: HNil, 2 :: HNil, 3 :: HNil)
//
//    // Get single value encoded in an HList
//    Ns.int.hl.head === 3 :: HNil
//
//    // Get some values as HLists (order not guaranteed)
//    Ns.int.hl(1) === List(3 :: HNil)
//    Ns.int.hl(2) === List(3 :: HNil, 1 :: HNil)
//    Ns.int.hl(3) === List(3 :: HNil, 1 :: HNil, 2 :: HNil)
//
//    // Get all values as HLists (order not guaranteed)
//    Ns.int.hl === List(3 :: HNil, 1 :: HNil, 2 :: HNil)
//  }
//
//
//  "Single cardinality-2 attribute - one entity" in new CoreSetup {
//    Ns.strs insert Set("foo", "bar") :: HNil
//    Ns.strs.hl === List(Set("foo", "bar") :: HNil)
//  }
//
//
//  "Single cardinality-2 attribute - multiple entities" in new CoreSetup {
//
//    // Create 3 entities, each with a set of `int` values
//    Ns.ints insert List(Set(1, 2) :: HNil, Set(3, 4) :: HNil)
//
//    // Get one `ints` set of values (order not guaranteed)
//    Ns.ints.one === Set(1, 4, 3, 2) // Todo: Unexpected
//
//    //    // Get single value encoded in an HList
//    //    Ns.ints.hl.head === Set(3, 4) :: HNil
//    //
//    //    // Get some values as HLists (order not guaranteed)
//    //    Ns.ints.hl(1) === List(Set(3, 4) :: HNil)
//    //    Ns.ints.hl(2) === List(Set(3, 4) :: HNil, Set(1, 2) :: HNil)
//    //
//    //    // Get all values as HLists (order not guaranteed)
//    //    Ns.ints.hl === List(Set(3, 4) :: HNil, Set(1, 2) :: HNil)
//  }
//
//
//  "Multiple cardinality-1 attributes - one entity" in new CoreSetup {
//    Ns.str.int.bool insert "foo" :: 42 :: true :: HNil
//    Ns.str.int.bool.hl === List("foo" :: 42 :: true :: HNil)
//  }
//
//
//  "Multiple cardinality-1 attributes - multiple entities" in new CoreSetup {
//    Ns.str.int.bool insert List("foo" :: 42 :: true :: HNil, "bar" :: 99 :: false :: HNil)
//    Ns.str.int.bool.hl === List("bar" :: 99 :: false :: HNil, "foo" :: 42 :: true :: HNil)
//  }
//
//
//  "Multiple cardinality-2 attributes - one entity" in new CoreSetup {
//    Ns.strs.ints insert Set("foo", "bar") :: Set(1, 2, 3) :: HNil
//    Ns.strs.ints.hl === List(Set("foo", "bar") :: Set(1, 2, 3) :: HNil)
//  }
//
//
//  "Multiple cardinality-2 attributes - multiple entities" in new CoreSetup {
//    Ns.strs.ints insert List(
//      Set("foo", "bar") :: Set(1, 2, 3) :: HNil,
//      Set("baz") :: Set(3, 4, 5, 6) :: HNil
//    )
//    // Unexpected???
//    Ns.strs.ints.one ===(Set("foo", "bar", "baz"), Set(5, 1, 6, 2, 3, 4))
//    //    Ns.strs.ints.hl === List(
//    //      Set("baz") :: Set(3, 4, 5, 6) :: HNil,
//    //      Set("foo", "bar") :: Set(1, 2, 3) :: HNil
//    //    )
//  }
//
//
//  "Mixed cardinality attributes - one entity (Using HLists" in new CoreSetup {
//    Ns.str.ints.bool insert "foo" :: Set(1, 2, 3) :: true :: HNil
//    Ns.str.ints.bool.hl === List("foo" :: Set(1, 2, 3) :: true :: HNil)
//  }
//
//
//  "Mixed cardinality attributes - multiple entities" in new CoreSetup {
//    Ns.str.ints.bool insert List("foo" :: Set(1, 2) :: true :: HNil, "bar" :: Set(2, 3, 4) :: false :: HNil)
//    Ns.str.ints.bool.hl === List("bar" :: Set(2, 3, 4) :: false :: HNil, "foo" :: Set(1, 2) :: true :: HNil)
//  }
//
//
//  "All cardinality-1 types" in new CoreSetup {
//    Ns.str.int.long.float.double.bool.date.uuid.uri.enum insert List(
//      "str1" :: 1 :: 1L :: 1.1f :: 2.2 :: true :: date1 :: uuid1 :: uri1 :: "enum1" :: HNil)
//
//    Ns.str.one === "str1"
//    Ns.int.one === 1
//    Ns.long.one === 1L
//    Ns.float.one === 1.1f
//    Ns.double.one === 2.2
//    Ns.bool.one === true
//    Ns.date.one === date1
//    Ns.uuid.one === uuid1
//    Ns.uri.one === uri1
//    Ns.enum.one === "enum1"
//
//    Ns.str.int.long.float.double.bool.date.uuid.uri.enum.hl.head === (
//      "str1" :: 1 :: 1L :: 1.1f :: 2.2 :: true :: date1 :: uuid1 :: uri1 :: "enum1" :: HNil)
//  }
//
//
//  "All cardinality-2 types" in new CoreSetup {
//    // (we don't include Sets of boolean values for obvious reasons...)
//
//    //    Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums insert(
//    Ns.strs.ints.longs.floats.doubles.dates.uuids.uris insert (
//      Set("str1", "str2")
//        :: Set(1, 2)
//        :: Set(1L, 2L)
//        :: Set(1.1f, 2.2f)
//        :: Set(1.1, 2.2)
//        :: Set(date1, date2)
//        :: Set(uuid1, uuid2)
//        :: Set(uri1, uri2)
//        //        :: Set("enum1", "enum2")
//        :: HNil)
//
//
//    Ns.strs.one === Set("str1", "str2")
//    Ns.ints.one === Set(1, 2)
//    Ns.longs.one === Set(1L, 2L)
//    Ns.floats.one === Set(1.1f, 2.2f)
//    Ns.doubles.one === Set(1.1, 2.2)
//    Ns.dates.one === Set(date1, date2)
//    Ns.uuids.one === Set(uuid1, uuid2)
//    Ns.uris.one === Set(uri1, uri2)
//    //    Ns.enums.one === Set("enum1", "enum2")
//
//    //    Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.one === (
//    Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.hl.head === (
//      Set("str1", "str2")
//        :: Set(1, 2)
//        :: Set(1L, 2L)
//        :: Set(1.1f, 2.2f)
//        :: Set(1.1, 2.2)
//        :: Set(date1, date2)
//        :: Set(uuid1, uuid2)
//        :: Set(uri1, uri2)
//        //        :: Set("enum1", "enum2")
//        :: HNil)
//  }
//}