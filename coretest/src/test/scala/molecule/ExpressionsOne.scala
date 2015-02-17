package molecule
import java.util.Date
import java.util.UUID._
import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}

class ExpressionsOne extends CoreSpec {

  class OneSetup extends CoreSetup {
    Ns.str insert List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.int insert List(-2, -1, 0, 1, 2)
    Ns.long insert List(-2L, -1L, 0L, 1L, 2L)
    Ns.float insert List(-2f, -1f, 0f, 1f, 2f)
    Ns.double insert List(-2.0, -1.0, 0.0, 1.0, 2.0)
    Ns.bool insert List(true, false)
    Ns.date insert List(date0, date1, date2)
    Ns.uuid insert List(uuid0, uuid1, uuid2)
    Ns.uri insert List(uri1, uri2)
    Ns.uri insert List(uri0, uri1, uri2)
    Ns.enum insert List("enum0", "enum1", "enum2")
  }


  "Match one value" in new OneSetup {

    Ns.str("").get === List("") // same as `Ns.str.apply("").get`
    Ns.str(" ").get === List(" ")
    Ns.str(",").get === List(",")
    Ns.str(".").get === List(".")
    Ns.str("?").get === List("?")
    Ns.str("A").get === List("A")
    Ns.str("B").get === List("B")
    Ns.str("a").get === List("a")
    Ns.str("b").get === List("b")

    // Apply value assigned to a variable
    Ns.str(str1).get === List("a")

    Ns.int(1).get === List(1)
    Ns.int(0).get === List(0)
    Ns.int(-1).get === List(-1)
    Ns.int(int1).get === List(1)


    Ns.long(1L).get === List(1L)
    Ns.long(0L).get === List(0L)
    Ns.long(-1L).get === List(-1L)
    Ns.long(long1).get === List(1L)

    // We can also apply Int values to a Long Attribute (using Scala's implicit conversions?)
    Ns.long(1).get === List(1)


    Ns.float(1f).get === List(1f)
    Ns.float(0f).get === List(0f)
    Ns.float(-1f).get === List(-1f)
    Ns.float(float1).get === List(1f)


    Ns.double(1.0).get === List(1.0)
    Ns.double(0.0).get === List(0.0)
    Ns.double(-1.0).get === List(-1.0)
    Ns.double(double1).get === List(1.0)


    Ns.bool(true).get === List(true)
    Ns.bool(false).get === List(false)
    Ns.bool(bool1).get === List(true)


    val now = new Date()
    Ns.date(now).get === List()
    Ns.date(date1).get === List(date1)
    Ns.date(date2).get === List(date2)
    // Todo Can we allow runtime constructs for compile time build-up?
    //    Ns.date(new Date()).get === List()


    Ns.uuid(uuid1).get === List(uuid1)
    Ns.uuid(uuid2).get === List(uuid2)


    //    import datomic._
    //    Peer.q(s"""[:find ?a ?b :where [?a :ns/uri ?b]]""", conn.db) === 76
    // [[17592186045436 #<URI uri1>], [17592186045437 #<URI uri2>]]

    // Both give empty result set
    //        Peer.q( s"""[:find ?b :where [17592186045493 :ns/uri ?b]]""", conn.db) === 42
    //        Peer.q( s"""[:find ?a :where [?a :ns/uri #java.net.URI["uri1"]]]""", conn.db) === 42
    //        Peer.q( s"""[:find ?a :where [?a :ns/uri (java.net.URI. "uri1")]]""", conn.db) === 42

    //    Ns.uri.debug
    //    Ns.uri(uri1).debug
    //    Ns.uri(uri1).get === List(uri1)
    //    Ns.uri(uri2).get === List(uri2)

    Ns.enum("enum1").get === List("enum1")
    Ns.enum("enum2").get === List("enum2")
    Ns.enum(enum2).get === List("enum2")

    // Applying a non-existing enum value ("enum3") won't compile!
    // Ns.enum("enum3").get === List("enum3")
  }


  "Match multiple values" in new OneSetup {

    // 3 ways of applying the same OR-semantics:

    // 1. `or`-separated values
    Ns.str("c" or "a").get === List("a")

    // 2. Comma-separated values
    Ns.str("c", "a").get === List("a")

    // 3. List of values
    Ns.str(List("c", "a")).get.sorted === List("a")


    // order of values not relevant
    Ns.str("a", "c").get === List("a")
    Ns.str("a" or "c").get === List("a")
    Ns.str(List("a", "c")).get === List("a")

    // No limit on number of applied values
    Ns.str("a", "b", "c", "d").get.sorted === List("a", "b")
    Ns.str("a" or "b" or "c" or "d").get.sorted === List("a", "b")
    Ns.str(List("a", "b", "c", "d")).get.sorted === List("a", "b")

    // Applying same value returns single value
    Ns.str("a", "a").get === List("a")
    Ns.str("a" or "a").get === List("a")

    // Multiple search strings in a list are treated with OR-semantics
    Ns.str(List("a", "a")).get === List("a")

    // Applying non-matching values returns empty result
    Ns.str("c", "d").get === List()
    Ns.str("c" or "d").get === List()
    Ns.str(List("c", "d")).get === List()

    // Empty/space character values treated as strings too
    Ns.str("", "    ").get === List("")
    Ns.str("", " ").get.sorted === List("", " ")

    // We can apply values assigned to variables
    Ns.str(str1 or str2).get.sorted === List("a", "b")
    Ns.str(str1, str2).get.sorted === List("a", "b")
    Ns.str(List(str1, str2)).get.sorted === List("a", "b")
    val strList = List(str1, str2)
    Ns.str(strList).get.sorted === List("a", "b")

    // We can mix variables and static values
    Ns.str(str1 or "b").get.sorted === List("a", "b")
    Ns.str("a", str2).get.sorted === List("a", "b")
    Ns.str(List(str1, "b")).get.sorted === List("a", "b")
    val strListMixed = List("a", str2)
    Ns.str(strListMixed).get.sorted === List("a", "b")


    Ns.int(-1, 0, 1).get.sorted === List(-1, 0, 1)
    Ns.int(0, 1, 2).get.sorted === List(0, 1, 2)
    Ns.int(1, 2, 3).get.sorted === List(1, 2)
    Ns.int(2, 3, 4).get.sorted === List(2)
    Ns.int(3, 4, 5).get.sorted === List()
    Ns.int(int1, int2).get.sorted === List(1, 2)
    Ns.int(int1 or int2).get.sorted === List(1, 2)
    Ns.int(List(int1, int2)).get.sorted === List(1, 2)
    val intList = List(int1, int2)
    Ns.int(intList).get.sorted === List(1, 2)


    Ns.long(-1L, 0L, 1L).get.sorted === List(-1L, 0L, 1L)
    Ns.long(0L, 1L, 2L).get.sorted === List(0L, 1L, 2L)
    Ns.long(1L, 2L, 3L).get.sorted === List(1L, 2L)
    Ns.long(2L, 3L, 4L).get.sorted === List(2L)
    Ns.long(3L, 4L, 5L).get.sorted === List()
    Ns.long(long1, long2).get.sorted === List(1L, 2L)
    Ns.long(long1 or long2).get.sorted === List(1L, 2L)
    Ns.long(List(long1, long2)).get.sorted === List(1L, 2L)
    val longList = List(long1, long2)
    Ns.long(longList).get.sorted === List(1L, 2L)


    Ns.float(-1f, 0f, 1f).get.sorted === List(-1f, 0f, 1f)
    Ns.float(0f, 1f, 2f).get.sorted === List(0f, 1f, 2f)
    Ns.float(1f, 2f, 3f).get.sorted === List(1f, 2f)
    Ns.float(2f, 3f, 4f).get.sorted === List(2f)
    Ns.float(3f, 4f, 5f).get.sorted === List()
    Ns.float(float1, float2).get.sorted === List(1f, 2f)
    Ns.float(float1 or float2).get.sorted === List(1f, 2f)
    Ns.float(List(float1, float2)).get.sorted === List(1f, 2f)
    val floatList = List(float1, float2)
    Ns.float(floatList).get.sorted === List(1.0f, 2.0f)


    Ns.double(-1.0, 0.0, 1.0).get.sorted === List(-1.0, 0.0, 1.0)
    Ns.double(0.0, 1.0, 2.0).get.sorted === List(0.0, 1.0, 2.0)
    Ns.double(1.0, 2.0, 3.0).get.sorted === List(1.0, 2.0)
    Ns.double(2.0, 3.0, 4.0).get.sorted === List(2.0)
    Ns.double(3.0, 4.0, 5.0).get.sorted === List()
    Ns.double(double1, double2).get.sorted === List(1.0, 2.0)
    Ns.double(double1 or double2).get.sorted === List(1.0, 2.0)
    Ns.double(List(double1, double2)).get.sorted === List(1.0, 2.0)
    val doubleList = List(double1, double2)
    Ns.double(doubleList).get.sorted === List(1.0, 2.0)


    // Weird case though to apply OR-semantics to Boolean attribute...
    Ns.bool(true or false).get === List(false, true)
    Ns.bool(true, false).get === List(false, true)
    Ns.bool(bool0, bool1).get === List(false, true)
    Ns.bool(bool0 or bool1).get === List(false, true)
    Ns.bool(List(bool0, bool1)).get === List(false, true)
    val boolList = List(bool1, bool2)
    Ns.bool(boolList).get.sorted === List(false, true)


    val now = new java.util.Date()
    Ns.date(date1, now).get === List(date1)
    Ns.date(date1, date2).get.sorted === List(date1, date2)
    Ns.date(date1 or date2).get.sorted === List(date1, date2)
    Ns.date(List(date1, date2)).get.sorted === List(date1, date2)
    val dateList = List(date1, date2)
    Ns.date(dateList).get.sorted === List(date1, date2)


    Ns.uuid(uuid1, uuid2).get.sortBy(_.toString) === List(uuid1, uuid2)
    Ns.uuid(uuid1 or uuid2).get.sortBy(_.toString) === List(uuid1, uuid2)
    Ns.uuid(List(uuid1, uuid2)).get.sortBy(_.toString) === List(uuid1, uuid2)
    val uuidList = List(uuid1, uuid2)
    Ns.uuid(uuidList).get.sorted === List(uuid1, uuid2)


    //    Ns.uri(uri1, uri2).get.sortBy(_.toString) === List(uri1, uri2)
    //    Ns.uri(uri1 or uri2).get.sortBy(_.toString) === List(uri1, uri2)
    //    Ns.uri(List(uri1, uri2)).get.sortBy(_.toString) === List(uri1, uri2)
    //    val uriList = List(uri1, uri2)
    //    Ns.uri(uriList).get.sorted === List(uri1, uri2)


    Ns.enum("enum1", "enum2").get.sorted === List("enum1", "enum2")
    Ns.enum(enum1, enum2).get.sorted === List("enum1", "enum2")
    Ns.enum(enum1 or enum2).get.sorted === List("enum1", "enum2")
    Ns.enum(List(enum1, enum2)).get.sorted === List("enum1", "enum2")
    val enumList = List(enum1, enum2)
    Ns.enum(enumList).get.sorted === List(enum1, enum2)
  }


  "Exclude 1 or more values" in new OneSetup {

    Ns.str.not("").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.not(" ").get.sorted === List("", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.not(",").get.sorted === List("", " ", ".", "?", "A", "B", "a", "b")
    Ns.str.not(".").get.sorted === List("", " ", ",", "?", "A", "B", "a", "b")
    Ns.str.not("?").get.sorted === List("", " ", ",", ".", "A", "B", "a", "b")
    Ns.str.not("A").get.sorted === List("", " ", ",", ".", "?", "B", "a", "b")
    Ns.str.not("B").get.sorted === List("", " ", ",", ".", "?", "A", "a", "b")
    Ns.str.not("a").get.sorted === List("", " ", ",", ".", "?", "A", "B", "b")
    Ns.str.not("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
    Ns.str.not("C").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.not("c").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")

    // Same as
    Ns.str.!=("").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.!=(" ").get.sorted === List("", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.!=(",").get.sorted === List("", " ", ".", "?", "A", "B", "a", "b")
    Ns.str.!=(".").get.sorted === List("", " ", ",", "?", "A", "B", "a", "b")
    Ns.str.!=("?").get.sorted === List("", " ", ",", ".", "A", "B", "a", "b")
    Ns.str.!=("A").get.sorted === List("", " ", ",", ".", "?", "B", "a", "b")
    Ns.str.!=("B").get.sorted === List("", " ", ",", ".", "?", "A", "a", "b")
    Ns.str.!=("a").get.sorted === List("", " ", ",", ".", "?", "A", "B", "b")
    Ns.str.!=("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
    Ns.str.!=("C").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.!=("c").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")

    Ns.str.not(str1).get.sorted === List("", " ", ",", ".", "?", "A", "B", "b")
    Ns.str.!=(str1).get.sorted === List("", " ", ",", ".", "?", "A", "B", "b")

    // Negate multiple values ("NOR"-logic: not 'a AND not 'b AND ...)
    Ns.str.not("", " ").get.sorted === List(",", ".", "?", "A", "B", "a", "b")
    Ns.str.not("", " ", ",", ".", "?").get.sorted === List("A", "B", "a", "b")
    Ns.str.not("", " ", ",", ".", "?", "A", "B").get.sorted === List("a", "b")
    Ns.str.not("", " ", ",", ".", "?", "A", "B", "a", "b").get.sorted === List()


    Ns.int.not(7).get.sorted === List(-2, -1, 0, 1, 2)
    Ns.int.not(1).get.sorted === List(-2, -1, 0, 2)
    Ns.int.not(-1, 0, 1).get.sorted === List(-2, 2)
    Ns.int.not(int1).get.sorted === List(-2, -1, 0, 2)
    Ns.int.not(int1, int2).get.sorted === List(-2, -1, 0)


    Ns.long.not(7).get.sorted === List(-2, -1, 0, 1, 2)
    Ns.long.not(1).get.sorted === List(-2, -1, 0, 2)
    Ns.long.not(-1, 0, 1).get.sorted === List(-2, 2)
    Ns.long.not(long1).get.sorted === List(-2, -1, 0, 2)
    Ns.long.not(long1, long1).get.sorted === List(-2, -1, 0, 2)


    Ns.float.not(7).get.sorted === List(-2, -1, 0, 1, 2)
    Ns.float.not(1).get.sorted === List(-2, -1, 0, 2)
    Ns.float.not(-1, 0, 1).get.sorted === List(-2, 2)
    Ns.float.not(float1).get.sorted === List(-2, -1, 0, 2)
    Ns.float.not(float1, float1).get.sorted === List(-2, -1, 0, 2)
    Ns.float.not(float1, float2).get.sorted === List(-2, -1, 0)


    Ns.double.not(7).get.sorted === List(-2, -1, 0, 1, 2)
    Ns.double.not(1).get.sorted === List(-2, -1, 0, 2)
    Ns.double.not(-1, 0, 1).get.sorted === List(-2, 2)
    Ns.double.not(double1).get.sorted === List(-2, -1, 0, 2)
    Ns.double.not(double1, double2).get.sorted === List(-2, -1, 0)


    Ns.bool.not(true).get === List(false)
    Ns.bool.not(false).get === List(true)
    Ns.bool.not(false, true).get === List()


    val now = new Date()
    Ns.date.not(now).get.sorted === List(date0, date1, date2)
    Ns.date.not(date0).get.sorted === List(date1, date2)
    Ns.date.not(date0, date1).get.sorted === List(date2)
    Ns.date.not(date0, date1, date2).get.sorted === List()


    val uuid3 = randomUUID()
    Ns.uuid.not(uuid3).get.sortBy(_.toString) === List(uuid0, uuid1, uuid2)
    Ns.uuid.not(uuid0).get.sortBy(_.toString) === List(uuid1, uuid2)
    Ns.uuid.not(uuid0, uuid1).get.sortBy(_.toString) === List(uuid2)
    Ns.uuid.not(uuid0, uuid1, uuid2).get.sortBy(_.toString) === List()

    // todo
    //    Ns.uri.not(new URI("other")).get.sortBy(_.toString) === List(uri0, uri1, uri2)
    //    Ns.uri.not(uri1).get.sortBy(_.toString) === List(uri0, uri2)

    Ns.enum.not("enum0").get.sorted === List(enum1, enum2)
    Ns.enum.not("enum0", "enum1").get.sorted === List(enum2)
    Ns.enum.not("enum0", "enum1", "enum2").get.sorted === List()
    Ns.enum.not(enum0).get.sorted === List(enum1, enum2)
    Ns.enum.not(enum0, enum1).get.sorted === List(enum2)
  }


  "Compare values" in new OneSetup {

    Ns.str.<("").get.sorted === List()
    Ns.str.<(" ").get.sorted === List("")
    Ns.str.<(",").get.sorted === List("", " ")
    Ns.str.<(".").get.sorted === List("", " ", ",")
    Ns.str.<("?").get.sorted === List("", " ", ",", ".")
    Ns.str.<("A").get.sorted === List("", " ", ",", ".", "?")
    Ns.str.<("B").get.sorted === List("", " ", ",", ".", "?", "A")
    Ns.str.<("a").get.sorted === List("", " ", ",", ".", "?", "A", "B")
    Ns.str.<("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
    Ns.str.<("d").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.<(str1).get.sorted === List("", " ", ",", ".", "?", "A", "B")

    Ns.str.>("").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.>(" ").get.sorted === List(",", ".", "?", "A", "B", "a", "b")
    Ns.str.>(",").get.sorted === List(".", "?", "A", "B", "a", "b")
    Ns.str.>(".").get.sorted === List("?", "A", "B", "a", "b")
    Ns.str.>("?").get.sorted === List("A", "B", "a", "b")
    Ns.str.>("A").get.sorted === List("B", "a", "b")
    Ns.str.>("B").get.sorted === List("a", "b")
    Ns.str.>("C").get.sorted === List("a", "b")
    Ns.str.>("a").get.sorted === List("b")
    Ns.str.>("b").get.sorted === List()
    Ns.str.>(str1).get.sorted === List("b")

    Ns.str.<=("").get.sorted === List("")
    Ns.str.<=(" ").get.sorted === List("", " ")
    Ns.str.<=(",").get.sorted === List("", " ", ",")
    Ns.str.<=(".").get.sorted === List("", " ", ",", ".")
    Ns.str.<=("?").get.sorted === List("", " ", ",", ".", "?")
    Ns.str.<=("A").get.sorted === List("", " ", ",", ".", "?", "A")
    Ns.str.<=("B").get.sorted === List("", " ", ",", ".", "?", "A", "B")
    Ns.str.<=("a").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
    Ns.str.<=("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.<=(str1).get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")

    Ns.str.>=("").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.>=(" ").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.>=(",").get.sorted === List(",", ".", "?", "A", "B", "a", "b")
    Ns.str.>=(".").get.sorted === List(".", "?", "A", "B", "a", "b")
    Ns.str.>=("?").get.sorted === List("?", "A", "B", "a", "b")
    Ns.str.>=("A").get.sorted === List("A", "B", "a", "b")
    Ns.str.>=("B").get.sorted === List("B", "a", "b")
    Ns.str.>=("a").get.sorted === List("a", "b")
    Ns.str.>=("b").get.sorted === List("b")
    Ns.str.>=("c").get.sorted === List()
    Ns.str.>=(str1).get.sorted === List("a", "b")


    Ns.int.<(-2).get.sorted === List()
    Ns.int.<(0).get.sorted === List(-2, -1)
    Ns.int.<(2).get.sorted === List(-2, -1, 0, 1)
    Ns.int.<(int1).get.sorted === List(-2, -1, 0)

    Ns.int.>(2).get.sorted === List()
    Ns.int.>(0).get.sorted === List(1, 2)
    Ns.int.>(-2).get.sorted === List(-1, 0, 1, 2)
    Ns.int.>(int1).get.sorted === List(2)

    Ns.int.<=(-2).get.sorted === List(-2)
    Ns.int.<=(0).get.sorted === List(-2, -1, 0)
    Ns.int.<=(2).get.sorted === List(-2, -1, 0, 1, 2)
    Ns.int.<=(int1).get.sorted === List(-2, -1, 0, 1)

    Ns.int.>=(2).get.sorted === List(2)
    Ns.int.>=(0).get.sorted === List(0, 1, 2)
    Ns.int.>=(-2).get.sorted === List(-2, -1, 0, 1, 2)
    Ns.int.>=(int1).get.sorted === List(1, 2)


    Ns.long.<(-2L).get.sorted === List()
    Ns.long.<(0L).get.sorted === List(-2L, -1L)
    Ns.long.<(2L).get.sorted === List(-2L, -1L, 0L, 1L)
    Ns.long.<(long1).get.sorted === List(-2L, -1L, 0L)

    Ns.long.>(2L).get.sorted === List()
    Ns.long.>(0L).get.sorted === List(1L, 2L)
    Ns.long.>(-2L).get.sorted === List(-1L, 0L, 1L, 2L)
    Ns.long.>(long1).get.sorted === List(2L)

    Ns.long.<=(-2L).get.sorted === List(-2L)
    Ns.long.<=(0L).get.sorted === List(-2L, -1L, 0L)
    Ns.long.<=(2L).get.sorted === List(-2L, -1L, 0L, 1L, 2L)
    Ns.long.<=(long1).get.sorted === List(-2L, -1L, 0L, 1L)

    Ns.long.>=(2L).get.sorted === List(2L)
    Ns.long.>=(0L).get.sorted === List(0L, 1L, 2L)
    Ns.long.>=(-2L).get.sorted === List(-2L, -1L, 0L, 1L, 2L)
    Ns.long.>=(long1).get.sorted === List(1L, 2L)


    Ns.float.<(-2f).get.sorted === List()
    Ns.float.<(0f).get.sorted === List(-2f, -1f)
    Ns.float.<(2f).get.sorted === List(-2f, -1f, 0f, 1f)
    Ns.float.<(float1).get.sorted === List(-2f, -1f, 0f)

    Ns.float.>(2f).get.sorted === List()
    Ns.float.>(0f).get.sorted === List(1f, 2f)
    Ns.float.>(-2f).get.sorted === List(-1f, 0f, 1f, 2f)
    Ns.float.>(float1).get.sorted === List(2f)

    Ns.float.<=(-2f).get.sorted === List(-2f)
    Ns.float.<=(0f).get.sorted === List(-2f, -1f, 0f)
    Ns.float.<=(2f).get.sorted === List(-2f, -1f, 0f, 1f, 2f)
    Ns.float.<=(float1).get.sorted === List(-2f, -1f, 0f, 1f)

    Ns.float.>=(2f).get.sorted === List(2f)
    Ns.float.>=(0f).get.sorted === List(0f, 1f, 2f)
    Ns.float.>=(float1).get.sorted === List(1f, 2f)


    Ns.double.<(-2.0).get.sorted === List()
    Ns.double.<(0.0).get.sorted === List(-2.0, -1.0)
    Ns.double.<(2.0).get.sorted === List(-2.0, -1.0, 0.0, 1.0)
    Ns.double.<(double1).get.sorted === List(-2.0, -1.0, 0.0)

    Ns.double.>(2.0).get.sorted === List()
    Ns.double.>(0.0).get.sorted === List(1.0, 2.0)
    Ns.double.>(-2.0).get.sorted === List(-1.0, 0.0, 1.0, 2.0)
    Ns.double.>(double1).get.sorted === List(2.0)

    Ns.double.<=(-2.0).get.sorted === List(-2.0)
    Ns.double.<=(0.0).get.sorted === List(-2.0, -1.0, 0.0)
    Ns.double.<=(2.0).get.sorted === List(-2.0, -1.0, 0.0, 1.0, 2.0)
    Ns.double.<=(double1).get.sorted === List(-2.0, -1.0, 0.0, 1.0)

    Ns.double.>=(2.0).get.sorted === List(2.0)
    Ns.double.>=(0.0).get.sorted === List(0.0, 1.0, 2.0)
    Ns.double.>=(-2.0).get.sorted === List(-2.0, -1.0, 0.0, 1.0, 2.0)
    Ns.double.>=(double1).get.sorted === List(1.0, 2.0)


    Ns.bool.<(true).get.sorted === List(false)
    Ns.bool.<(false).get.sorted === List()
    Ns.bool.<(bool0).get.sorted === List()

    Ns.bool.>(true).get.sorted === List()
    Ns.bool.>(false).get.sorted === List(true)
    Ns.bool.>(bool0).get.sorted === List(true)

    Ns.bool.<=(true).get.sorted === List(false, true)
    Ns.bool.<=(false).get.sorted === List(false)
    Ns.bool.<=(bool0).get.sorted === List(false)

    Ns.bool.>=(true).get.sorted === List(true)
    Ns.bool.>=(false).get.sorted === List(false, true)
    Ns.bool.>=(bool0).get.sorted === List(false, true)


    Ns.date.<(date1).get.sorted === List(date0)
    Ns.date.<(date2).get.sorted === List(date0, date1)

    Ns.date.>(date1).get.sorted === List(date2)
    Ns.date.>(date0).get.sorted === List(date1, date2)

    Ns.date.<=(date1).get.sorted === List(date0, date1)
    Ns.date.<=(date2).get.sorted === List(date0, date1, date2)

    Ns.date.>=(date1).get.sorted === List(date1, date2)
    Ns.date.>=(date0).get.sorted === List(date0, date1, date2)


    // Comparison of random UUIDs omitted...


    // todo
    //    Ns.uri.<(uri1).get.sorted === List(uri0)
    //    Ns.uri.>(uri1).get.sorted === List(uri2)
    //    Ns.uri.<=(uri1).get.sorted === List(uri0, uri1)
    //    Ns.uri.>=(uri1).get.sorted === List(uri1, uri2)


    Ns.enum.<("enum1").get.sorted === List("enum0")
    Ns.enum.>("enum1").get.sorted === List("enum2")
    Ns.enum.<=("enum1").get.sorted === List("enum0", "enum1")
    Ns.enum.>=("enum1").get.sorted === List("enum1", "enum2")

    Ns.enum.<(enum1).get.sorted === List("enum0")
    Ns.enum.>(enum1).get.sorted === List("enum2")
    Ns.enum.<=(enum1).get.sorted === List("enum0", "enum1")
    Ns.enum.>=(enum1).get.sorted === List("enum1", "enum2")
  }


  "Search text" in new CoreSetup {

    Ns.str insert List("The quick fox jumps", "Ten slow monkeys")

    // Trivial words like "The" not indexed
    Ns.str.contains("The").get === List()
    Ns.str.contains("Ten").get === List("Ten slow monkeys")

    // Only full words counted
    Ns.str.contains("jumps").get === List("The quick fox jumps")
    Ns.str.contains("jump").get === List()

    // Empty spaces ignored
    Ns.str.contains("slow ").get === List("Ten slow monkeys")
    Ns.str.contains(" slow").get === List("Ten slow monkeys")
    Ns.str.contains(" slow ").get === List("Ten slow monkeys")
    Ns.str.contains("  slow  ").get === List("Ten slow monkeys")

    // Words are searched individually - order and spaces ignored
    Ns.str.contains("slow     monkeys").get === List("Ten slow monkeys")
    Ns.str.contains("monkeys slow").get === List("Ten slow monkeys")
    Ns.str.contains("monkeys quick").get === List("Ten slow monkeys", "The quick fox jumps")
    Ns.str.contains("quick monkeys").get === List("Ten slow monkeys", "The quick fox jumps")
  }
}