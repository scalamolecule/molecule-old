//package molecule
//package semantics
//import molecule.semantics.dsl.coreTest._
//
//class ExpressionsMany extends CoreSpec {
//
//  class Setup extends CoreSetup {
////    Ns.strs insert List("", " ", ",", ".", "?", "A", "B", "a", "b")
////    Ns.strs insert List(Set("", " "), Set(",", ".", "?"), Set("A", "B"), Set("a", "b"))
//    Ns.str.strs insert List(
//      ("hey", Set("a", "b")),
//      ("you", Set("b", "c")))
////    Ns.strs insert List(Set("A", "b"), Set("a", "b"))
//
////    Ns.int insert List(-2, -1, 0, 1, 2)
////    Ns.long insert List(-2L, -1L, 0L, 1L, 2L)
////    Ns.float insert List(-2f, -1f, 0f, 1f, 2f)
////    Ns.double insert List(-2.0, -1.0, 0.0, 1.0, 2.0)
////    Ns.bool insert List(true, false)
////    Ns.date insert List(date1, date2)
////    Ns.uuid insert List(uuid1, uuid2)
////    Ns.uri insert List(uri1, uri2)
////    Ns.enum insert List("enum1", "enum2")
//  }
////
////  "Apply value to cardinality-one attribute" in new BaseSetup {
////
////    Ns.str("").get === List("") // same as Ns.str.apply("").get
////    Ns.str(" ").get === List(" ")
////    Ns.str(",").get === List(",")
////    Ns.str(".").get === List(".")
////    Ns.str("?").get === List("?")
////    Ns.str("A").get === List("A")
////    Ns.str("B").get === List("B")
////    Ns.str("a").get === List("a")
////    Ns.str("b").get === List("b")
////
////    // Apply value assigned to variable
////    Ns.str(str0).get === List(" ")
////
////
////    Ns.int(1).get === List(1)
////    Ns.int(0).get === List(0)
////    Ns.int(-1).get === List(-1)
////    Ns.int(int1).get === List(1)
////
////
////    Ns.long(1L).get === List(1L)
////    Ns.long(0L).get === List(0L)
////    Ns.long(-1L).get === List(-1L)
////    Ns.long(long1).get === List(1L)
////
////    // We can also apply Int values to a Long Attribute
////    Ns.long(1).get === List(1)
////
////    Ns.float(1f).get === List(1f)
////    Ns.float(0f).get === List(0f)
////    Ns.float(-1f).get === List(-1f)
////    Ns.float(float1).get === List(1f)
////
////    Ns.double(1.0).get === List(1.0)
////    Ns.double(0.0).get === List(0.0)
////    Ns.double(-1.0).get === List(-1.0)
////    Ns.double(double1).get === List(1.0)
////
////    Ns.bool(true).get === List(true)
////    Ns.bool(false).get === List(false)
////    Ns.bool(bool1).get === List(true)
////
////    Ns.date(date1).get === List(date1)
////    Ns.date(date2).get === List(date2)
////
////    Ns.uuid(uuid1).get === List(uuid1)
////    Ns.uuid(uuid2).get === List(uuid2)
////
//////    Ns.uri.debug
//////    Ns.uri(uri1).debug
//////    Ns.uri(uri1).get === List(uri1)
//////    Ns.uri(uri2).get === List(uri2)
////
////    Ns.enum("enum1").get === List("enum1")
////    Ns.enum("enum2").get === List("enum2")
////    Ns.enum(enum2).get === List("enum2")
////
////    // Applying a non-existing enum value ("enum3") won't compile!
////    // Ns.enum("enum3").get === List("enum3")
////  }
////
////  "Apply multiple values (OR-semantics) to cardinality-one attribute" in new BaseSetup {
////
////    // 3 ways of applying (the same) OR-semantics:
////
////    // 1. `or`-separated expression
////    Ns.str("c" or "a").get === List("a")
////
////    // 2. Comma-separated values
////    Ns.str("c", "a").get === List("a")
////
////    // 3. List of values
////    Ns.str(List("c", "a")).get.sorted === List("a")
////
////
////    // order of values not relevant
////    Ns.str("a", "c").get === List("a")
////    Ns.str("a" or "c").get === List("a")
////    Ns.str(List("a", "c")).get === List("a")
////
////    // No limit on number of applied values
////    Ns.str("a", "b", "c", "d").get.sorted === List("a", "b")
////    Ns.str("a" or "b" or "c" or "d").get.sorted === List("a", "b")
////    Ns.str(List("a", "b", "c", "d")).get.sorted === List("a", "b")
////
////    // Applying same value returns single value
////    Ns.str("a", "a").get === List("a")
////    Ns.str("a" or "a").get === List("a")
////    Ns.str(List("a", "a")).get === List("a")
////
////    // Applying non-matching values returns empty result
////    Ns.str("c", "d").get === List()
////    Ns.str("c" or "d").get === List()
////    Ns.str(List("c", "d")).get === List()
////
////    // Empty/space character values treated as strings too
////    Ns.str("", "    ").get === List("")
////    Ns.str("", " ").get.sorted === List("", " ")
////
////    // We can apply values assigned to variables
////    Ns.str(str1 or str2).get.sorted === List("a", "b")
////    Ns.str(str1, str2).get.sorted === List("a", "b")
////    Ns.str(List(str1, str2)).get.sorted === List("a", "b")
////    val needles = List(str1, str2)
////    Ns.str(needles).get.sorted === List("a", "b")
////
////    Ns.int(-1, 0, 1).get.sorted === List(-1, 0, 1)
////    Ns.int(0, 1, 2).get.sorted === List(0, 1, 2)
////    Ns.int(1, 2, 3).get.sorted === List(1, 2)
////    Ns.int(2, 3, 4).get.sorted === List(2)
////    Ns.int(3, 4, 5).get.sorted === List()
////    Ns.int(int1, int2).get.sorted === List(1, 2)
////    Ns.int(int1 or int2).get.sorted === List(1, 2)
////    Ns.int(List(int1, int2)).get.sorted === List(1, 2)
////
////    Ns.long(-1L, 0L, 1L).get.sorted === List(-1L, 0L, 1L)
////    Ns.long(0L, 1L, 2L).get.sorted === List(0L, 1L, 2L)
////    Ns.long(1L, 2L, 3L).get.sorted === List(1L, 2L)
////    Ns.long(2L, 3L, 4L).get.sorted === List(2L)
////    Ns.long(3L, 4L, 5L).get.sorted === List()
////    Ns.long(long1, long2).get.sorted === List(1L, 2L)
////    Ns.long(long1 or long2).get.sorted === List(1L, 2L)
////    Ns.long(List(long1, long2)).get.sorted === List(1L, 2L)
////
////    Ns.float(-1f, 0f, 1f).get.sorted === List(-1f, 0f, 1f)
////    Ns.float(0f, 1f, 2f).get.sorted === List(0f, 1f, 2f)
////    Ns.float(1f, 2f, 3f).get.sorted === List(1f, 2f)
////    Ns.float(2f, 3f, 4f).get.sorted === List(2f)
////    Ns.float(3f, 4f, 5f).get.sorted === List()
////    Ns.float(float1, float2).get.sorted === List(1f, 2f)
////    Ns.float(float1 or float2).get.sorted === List(1f, 2f)
////    Ns.float(List(float1, float2)).get.sorted === List(1f, 2f)
////
////    Ns.double(-1.0, 0.0, 1.0).get.sorted === List(-1.0, 0.0, 1.0)
////    Ns.double(0.0, 1.0, 2.0).get.sorted === List(0.0, 1.0, 2.0)
////    Ns.double(1.0, 2.0, 3.0).get.sorted === List(1.0, 2.0)
////    Ns.double(2.0, 3.0, 4.0).get.sorted === List(2.0)
////    Ns.double(3.0, 4.0, 5.0).get.sorted === List()
////    Ns.double(double1, double2).get.sorted === List(1.0, 2.0)
////    Ns.double(double1 or double2).get.sorted === List(1.0, 2.0)
////    Ns.double(List(double1, double2)).get.sorted === List(1.0, 2.0)
////
////    // Weird case though to apply OR-semantics to Boolean attribute...
////    Ns.bool(true or false).get === List(false, true)
////    Ns.bool(true, false).get === List(false, true)
////    Ns.bool(bool0, bool1).get === List(false, true)
////    Ns.bool(bool0 or bool1).get === List(false, true)
////    Ns.bool(List(bool0, bool1)).get === List(false, true)
////
////    val now = new java.util.Date()
////    Ns.date(date1, now).get === List(date1)
////    Ns.date(date1, date2).get.sorted === List(date1, date2)
////    Ns.date(date1 or date2).get.sorted === List(date1, date2)
////    Ns.date(List(date1, date2)).get.sorted === List(date1, date2)
////
//////    Ns.uuid(uuid1, uuid2).get === List(uuid1, uuid2)
//////    Ns.uuid(uuid1 or uuid2).get === List(uuid1, uuid2)
//////    Ns.uuid(List(uuid1, uuid2)).get === List(uuid1, uuid2)
////
////    Ns.uuid(uuid1, uuid2).get.sortBy(_.toString) === List(uuid1, uuid2)
////    Ns.uuid(uuid1 or uuid2).get.sortBy(_.toString) === List(uuid1, uuid2)
////    Ns.uuid(List(uuid1, uuid2)).get.sortBy(_.toString) === List(uuid1, uuid2)
////
//////    Ns.uri.debug
//////    Ns.uri(uri1).debug
//////    Ns.uri(uri1).get === List(uri1)
//////    Ns.uri(uri2).get === List(uri2)
////
////    Ns.enum("enum1", "enum2").get.sorted === List("enum1", "enum2")
////    Ns.enum(enum1, enum2).get.sorted === List("enum1", "enum2")
////    Ns.enum(enum1 or enum2).get.sorted === List("enum1", "enum2")
////    Ns.enum(List(enum1, enum2)).get.sorted === List("enum1", "enum2")
////  }
//
//
//
//  "Apply value(s) to cardinality-many attributes" in new Setup {
////    Ns.str.strs.apply(List(("A", Set("A", "B")))).get === List(Set("", " ", ",", ".", "?", "A", "B", "a", "b"))
////    Ns.strs.apply("A").debug
//
//    import datomic._
//
////    Peer.q(s"""
////[:find  (distinct ?b)
////:where
////  [?a :ns/strs "b"]
////  [?a :ns/strs ?b]
////  ]
////           """, conn.db) === 26 // [[#{"a" "b" "c"}]]
//
////    Peer.q(s"""
////[:find  ?b
////:with ?a
////:where
////  [?a :ns/strs "b"]
////  [?a :ns/strs ?b]
////  ]
////           """, conn.db) === 27 // [["b"] ["c"] ["b"] ["a"]]
//
////    Peer.q(s"""
////[:find (distinct ?b)
////:with ?a
////:where
////  [?a :ns/strs "b"]
////  [?a :ns/strs ?b]
////  ]
////           """, conn.db) === 27 // [[#{"a" "b" "c"}]]
//
////    Peer.q(s"""
////[:find (distinct ?b)
////:where
////  [?a :ns/strs "b"]
////  [?a :ns/strs ?b]
////  ]
////           """, conn.db) === 27 // [[#{"a" "b" "c"}]]
//
////    Peer.q(s"""
////[:find ?c ?b
////:where
////  [?a :ns/str ?c]
////  [?a :ns/strs "b"]
////  [?a :ns/strs ?b]
////  ]
////           """, conn.db) === 28 // [["you" "b"], ["you" "c"], ["hey" "b"], ["hey" "a"]]
//
//    Peer.q(s"""
//[:find ?a (distinct ?b)
//:where
//  [?a :ns/str ?a]
//  [?a :ns/strs "b"]
//  [?a :ns/strs ?b]]
//           """, conn.db) === 29 // [["hey" #{"a" "b"}] ["you" #{"b" "c"}]]
//    Peer.q(s"""
//[:find ?c (distinct ?b)
//:in $$ ?b
//:where
//  [?a :ns/str ?c]
//  [?a :ns/strs ?b]
//  ]
//           """, conn.db, "b") === 29 // [["hey" #{"a" "b"}] ["you" #{"b" "c"}]]
////
////    Ns.strs.apply("A").get === List(Set("A", "b"))
////    Ns.strs.apply("B").get === List()
////    Ns.strs.apply("a").get === List(Set("a", "b"))
////    Ns.strs.apply("b").get === List(Set("A", "b"), Set("a", "b"))
////
////    Ns.strs.apply("A").get === List(Set("", " ", ",", ".", "?", "A", "B", "a", "b"))
////    Ns.strs.apply("A", "B").get === List(Set("", " ", ",", ".", "?", "A", "B", "a", "b"))
////    Ns.strs.apply(Set("A", "B")).get === List(Set("", " ", ",", ".", "?", "A", "B", "a", "b"))
//  }
//
////    import datomic._
////    Peer.q("[:find ?b :where [?a :ns/enum ?b]]", conn.db) === 7
//
//  "Range of strings" in new Setup {
//
//    Ns.str.<("").get.sorted === List()
//    Ns.str.<(" ").get.sorted === List("")
//    Ns.str.<(",").get.sorted === List("", " ")
//    Ns.str.<(".").get.sorted === List("", " ", ",")
//    Ns.str.<("?").get.sorted === List("", " ", ",", ".")
//    Ns.str.<("A").get.sorted === List("", " ", ",", ".", "?")
//    Ns.str.<("B").get.sorted === List("", " ", ",", ".", "?", "A")
//    Ns.str.<("a").get.sorted === List("", " ", ",", ".", "?", "A", "B")
//    Ns.str.<("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
//    Ns.str.<("d").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
//
//    Ns.str.>("").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
//    Ns.str.>(" ").get.sorted === List(",", ".", "?", "A", "B", "a", "b")
//    Ns.str.>(",").get.sorted === List(".", "?", "A", "B", "a", "b")
//    Ns.str.>(".").get.sorted === List("?", "A", "B", "a", "b")
//    Ns.str.>("?").get.sorted === List("A", "B", "a", "b")
//    Ns.str.>("A").get.sorted === List("B", "a", "b")
//    Ns.str.>("B").get.sorted === List("a", "b")
//    Ns.str.>("C").get.sorted === List("a", "b")
//    Ns.str.>("a").get.sorted === List("b")
//    Ns.str.>("b").get.sorted === List()
//
//
//    Ns.str.<=("").get.sorted === List("")
//    Ns.str.<=(" ").get.sorted === List("", " ")
//    Ns.str.<=(",").get.sorted === List("", " ", ",")
//    Ns.str.<=(".").get.sorted === List("", " ", ",", ".")
//    Ns.str.<=("?").get.sorted === List("", " ", ",", ".", "?")
//    Ns.str.<=("A").get.sorted === List("", " ", ",", ".", "?", "A")
//    Ns.str.<=("B").get.sorted === List("", " ", ",", ".", "?", "A", "B")
//    Ns.str.<=("a").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
//    Ns.str.<=("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
//
//    Ns.str.>=("").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
//    Ns.str.>=(" ").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
//    Ns.str.>=(",").get.sorted === List(",", ".", "?", "A", "B", "a", "b")
//    Ns.str.>=(".").get.sorted === List(".", "?", "A", "B", "a", "b")
//    Ns.str.>=("?").get.sorted === List("?", "A", "B", "a", "b")
//    Ns.str.>=("A").get.sorted === List("A", "B", "a", "b")
//    Ns.str.>=("B").get.sorted === List("B", "a", "b")
//    Ns.str.>=("a").get.sorted === List("a", "b")
//    Ns.str.>=("b").get.sorted === List("b")
//    Ns.str.>=("c").get.sorted === List()
//  }
//
//
//  "Negate string" in new Setup {
//
//    Ns.str.not("").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
//    Ns.str.not(" ").get.sorted === List("", ",", ".", "?", "A", "B", "a", "b")
//    Ns.str.not(",").get.sorted === List("", " ", ".", "?", "A", "B", "a", "b")
//    Ns.str.not(".").get.sorted === List("", " ", ",", "?", "A", "B", "a", "b")
//    Ns.str.not("?").get.sorted === List("", " ", ",", ".", "A", "B", "a", "b")
//    Ns.str.not("A").get.sorted === List("", " ", ",", ".", "?", "B", "a", "b")
//    Ns.str.not("B").get.sorted === List("", " ", ",", ".", "?", "A", "a", "b")
//    Ns.str.not("a").get.sorted === List("", " ", ",", ".", "?", "A", "B", "b")
//    Ns.str.not("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
//    Ns.str.not("C").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
//    Ns.str.not("c").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
//
//    Ns.str.!=("").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
//    Ns.str.!=(" ").get.sorted === List("", ",", ".", "?", "A", "B", "a", "b")
//    Ns.str.!=(",").get.sorted === List("", " ", ".", "?", "A", "B", "a", "b")
//    Ns.str.!=(".").get.sorted === List("", " ", ",", "?", "A", "B", "a", "b")
//    Ns.str.!=("?").get.sorted === List("", " ", ",", ".", "A", "B", "a", "b")
//    Ns.str.!=("A").get.sorted === List("", " ", ",", ".", "?", "B", "a", "b")
//    Ns.str.!=("B").get.sorted === List("", " ", ",", ".", "?", "A", "a", "b")
//    Ns.str.!=("a").get.sorted === List("", " ", ",", ".", "?", "A", "B", "b")
//    Ns.str.!=("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
//    Ns.str.!=("C").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
//    Ns.str.!=("c").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
//  }
//
//
//  "Search string (fulltext search)" in new CoreSetup {
//
//    Ns.str insert List("The quick fox jumps", "Ten slow monkeys")
//
//    // Trivial words like "The" not indexed
//    Ns.str.contains("The").get === List()
//    Ns.str.contains("Ten").get === List("Ten slow monkeys")
//
//    // Only full words counted
//    Ns.str.contains("jumps").get === List("The quick fox jumps")
//    Ns.str.contains("jump").get === List()
//
//    // Empty spaces ignored
//    Ns.str.contains("slow ").get === List("Ten slow monkeys")
//    Ns.str.contains(" slow").get === List("Ten slow monkeys")
//    Ns.str.contains(" slow ").get === List("Ten slow monkeys")
//    Ns.str.contains("  slow  ").get === List("Ten slow monkeys")
//
//    // Words are searched individually - order and spaces ignored
//    Ns.str.contains("slow     monkeys").get === List("Ten slow monkeys")
//    Ns.str.contains("monkeys slow").get === List("Ten slow monkeys")
//    Ns.str.contains("monkeys quick").get === List("Ten slow monkeys", "The quick fox jumps")
//    Ns.str.contains("quick monkeys").get === List("Ten slow monkeys", "The quick fox jumps")
//  }
//  //
//  //
//  //  "String logic" in new CoreSetup {
//  //    Ns.str.int insert List(("John", 37), ("Lisa", 28), ("Ben", 28))
//  //
//  //
//  //  }
//  //
//  //
//  //    class ExpressionSetup extends CoreSetup {
//  //      Ns.str.int insert List(("John", 37), ("Lisa", 28), ("Ben", 28))
//  //    }
//  //
//  //    "Applying a value" in new ExpressionSetup {
//  //
//  //      Ns.str_("John").int.get === List(37)
//  //      Ns.str("John").int.get === List(("John", 37))
//  //
//  //      Ns.str.int_(28).get === List("Ben", "Lisa")
//  //      Ns.str.int(28).get === List(("Ben", 28), ("Lisa", 28))
//  //    }
//  //  "Applying a value (parameterized)" in new ExpressionSetup {
//  //    val ageOf = m(Ns.str_(?).int)
//  //    ageOf("John").one === 37
//  //
//  //    val personByName = m(Ns.str(?).int)
//  //    personByName("John").one ===("John", 37)
//  //
//  //    val nameOfAge = m(Ns.str.int_(?))
//  //    nameOfAge(37).get === List("John")
//  //    nameOfAge(28).get === List("Lisa", "Ben")
//  //    nameOfAge(10).get === List()
//  //
//  //    val personOfAge = m(Ns.str.int(?))
//  //    personOfAge(37).get === List(("John", 37))
//  //    personOfAge(28).get === List(("Lisa", 28), ("Ben", 28))
//  //    personOfAge(10).get === List()
//  //  }
//  //
//  //  "Applying a Set of values" in new ExpressionSetup {
//  //
//  //  }
//  //
//  //  "Applying a value" in new ExpressionSetup {
//  //    Ns.str("Lisa" or "Ben").int.get === List(("Lisa", 28), ("Ben", 28))
//  //    Ns.str_("Lisa" or "Ben").int.get === List(28, 28)
//  //
//  //    Ns.str.int(28 or 37).get === List(("John", 37), ("Lisa", 28), ("Ben", 28))
//  //    Ns.str.int_(28 or 37).get === List("John", "Lisa", "Ben")
//  //  }
//  //
//  ////  "Fulltext search" in new ExpressionSetup {}
//  //
//  //  "Compare String" in new ExpressionSetup {
//  //    //
//  //    Ns.str.<("C").one === "Ben"
//  //    // same as
//  //    m(Ns.str < "C").one === "Ben"
//  //
//  //    Ns.str.<("C").one === "Ben"
//  //  }
//
//  //  "OR expressions" in new ExpressionSetup {}
//
//  //  "AND expressions" in new ExpressionSetup {}
//}
