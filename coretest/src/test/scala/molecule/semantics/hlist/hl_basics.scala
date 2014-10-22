//package molecule.semantics.hlist
//import molecule.util.dsl.coreTest._
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
//    Ns.ints.one === Set(1, 4, 3, 2) // Unexpected???
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
//  //  "Mixed cardinality attributes - multiple entities" in new CoreSetup {
//  //
//  //    // Create an entity with attribute `strs` having a Set of values Set("foo", "bar")
//  //    Ns.strs.ints.insert(Set("foo", "bar"), Set(1, 2, 3))
//  //
//  //    // Get one set of `str` values
//  //    Ns.strs.one === Set("foo", "bar")
//  //    Ns.strs.get.head === Set("foo", "bar")
//  //
//  //    // Get single set encoded in an HList
//  //    Ns.strs.hl.head === Set("foo", "bar") :: HNil
//  //
//  //    // Get all sets of values of attribute `str` (there's only one set)
//  //    Ns.strs.get === List(Set("foo", "bar"))
//  //    Ns.strs.hl === List(Set("foo", "bar") :: HNil)
//  //  }
//
//  //
//  //  "Single attribute, cardinality 1"  in new CoreSetup {
//  //
//  //    // Cardinality 1
//  //
//  //    // Insert 1 value
//  //    Ns.str insert "foo"
//  //
//  //    // Find 1 value
//  //    Ns.str.one === "foo"
//  //
//  //
//  //    // Cardinality 2
//  //
//  //    // Insert 1 Set of values
//  //    Ns.ints insert Set(1, 2, 3)
//  //
//  //    // Find 1 Set of values
//  //    Ns.ints.one === Set(1, 2, 3)
//  //
//  //
//  //    // Cardinality 1
//  //    Ns.str insert List(str0, str1, str2)
//  //
//  //    // Get all values of attribute (order is not guaranteed!)
//  //    Ns.str.get === List(str2, str0, str1)
//  //
//  //    // Get some values
//  //    Ns.str.get(2) === List(str2, str0)
//  //
//  //    // First value - convenience method retrieving single (random) value
//  //    Ns.str.one === str2
//  //  }
//  //
//  //  "Single attribute, cardinality 2"  in new CoreSetup {
//  //
//  //    // Cardinality 2 (Sets of values)
//  //    //    Ns.ints insert List(ints0, ints1, ints2, ints3)
//  //    //    Ns.ints insert List(ints1, ints2, ints3)
//  //    //    Ns.ints insert List(ints2, ints3)
//  //    //    Ns.ints insert List(ints2, ints1)
//  //    //    Ns.str.ints insert List((str1, ints2), (str2, ints3))
//  //    Ns.str.ints insert List((str1, Set(1, 2)), (str2, Set(3, 4)))
//  //    //    Ns.ints insert List(Set(1,2))
//  //
//  //    // Get all values of attribute (order is not guaranteed!)
//  //    //    Ns.ints.get2 === List(ints0, ints1, ints2, ints3)
//  //    //    Ns.ints.get2 === List(ints1, ints2, ints3)
//  //    //    Ns.ints.ids === List(ints1)
//  //    //    Ns.str.ints.get === List(ints1)
//  //    //    Ns.ints.get === List(ints1)
//  //    //    Ns.ints.get2 === List(ints1)
//  //    datomic._String) = Peer.q(s, conn.db)
//  //
//  //    //    q("[:find (distinct ?b) :where [?ent :ns/ints ?b] ]") === 23
//  //    val q1 = """[:find ?a (distinct ?b)
//  //               | :where
//  //               |   [?ent :ns/str ?a]
//  //               |   [?ent :ns/ints ?b]
//  //               |]""".stripMargin
//  //    val q2 = """[:find ?a ?b
//  //               | :where
//  //               |   [?ent :ns/str ?a]
//  //               |   [?ent :ns/ints ?b]
//  //               |]""".stripMargin
//  //    val q3 = """[:find (distinct ?b)
//  //               | :where
//  //               |   [?ent :ns/ints ?b]
//  //               |]""".stripMargin
//  //    val q5 = """[:find (distinct ?b)
//  //               | :with ?ent
//  //               | :where
//  //               |   [?ent :ns/ints ?b]
//  //               |]""".stripMargin
//  //    //            | :with ?ent
//  //    val q4 = """[:find ?a (distinct ?b)
//  //               | :where
//  //               |   [?ent :ns/str ?a]
//  //               |   [?ent :ns/ints ?b]
//  //               |]""".stripMargin
//  //    val q6 = """[:find (distinct ?b)
//  //               | :with ?ent
//  //               | :where
//  //               |   [?ent :ns/str ?a]
//  //               |   [?ent :ns/ints ?b]
//  //               |]""".stripMargin
//  //    //    println(q1)
//  //    println(q6)
//  //    q(q6) === 23
//  //    //    q(q2) === 23
//  //
//  //    //    q("[:find ?a (distinct ?b) :where [?ent :ns/str ?a] [?ent :ns/ints ?b] ]") === 23
//  //
//  //    //    // Get some values
//  //    //    Ns.ints.get(2) === List(ints0, ints1)
//  //    //
//  //    //    // First value - convenience method since ordering is not guaranteed
//  //    //    Ns.ints.first === ints0
//  //  }
//
//  //  "Multiple attributes output api" in new CoreSetup {
//  //
//  //    // Insert one-cardinality data
//  //    Ns.str.int insert List((str, int), (str0, int0), (str1, int1))
//  //
//  //    // Insert many-cardinality data (sets of values)
//  //    Ns.strs.ints insert List((strs, ints), (strs0, ints0), (strs1, ints1))
//  //
//  //    // Arity 1
//  //
//  //    // Get all values
//  //    // Result set has no ordering guarantee
//  //    Ns.str.get === List(str, str0, str1)
//  //
//  //    // Get some values
//  //    Ns.str.get(2) === List(str, str0)
//  //
//  //    // First value - convenience method since ordering is not guaranteed
//  //    Ns.str.first === str
//  //
//  //
//  //    // Arity 2
//  //
//  //    Ns.str.int.get === List((str0, int0), (str1, int1), (str, int))
//  //    Ns.str.int.get(2) === List((str0, int0))
//  //    Ns.str.int.first === List((str0, int0))
//  //
//  //    // etc .. to arity 22
//  //
//  //
//  ////    Ns.str.int insert List((str, int), (str0, int0), (str1, int1))
//  //  }
//  //
//  //  "Input == output" in new CoreSetup {
//  //
//  //    // Insert one-cardinality data
//  //    Ns.str.int.long.float.double.bool.date.uuid.uri.enum insert List(
//  //      (str, int, long, float, double, bool, date, uuid, uri, enum))
//  //
//  //    // Insert many-cardinality data (sets of values)
//  //    Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums insert(
//  //      strs, ints, longs, floats, doubles, dates, uuids, uris, enums)
//  //
//  //    Ns.str.first === str
//  //    Ns.int.first === int
//  //    Ns.long.first === long
//  //    Ns.float.first === float
//  //    Ns.double.first === double
//  //    Ns.bool.first === bool
//  //    Ns.date.first === date
//  //    Ns.uuid.first === uuid
//  //    Ns.uri.first === uri
//  //    Ns.enum.first === enum
//  //    Ns.enum.get === List(enum)
//  //
//  //    Ns.strs.first === strs
//  //    Ns.ints.first === ints
//  //    Ns.longs.first === longs
//  //    Ns.floats.first === floats
//  //    Ns.doubles.first === doubles
//  //    // No many-ref attributes with boolean values
//  //    Ns.dates.first === dates
//  //    Ns.uuids.first === uuids
//  //    Ns.uris.first === uris
//  //    Ns.enums.first === enums
//  //
//  //  }
//}