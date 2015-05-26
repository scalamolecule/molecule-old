package molecule
import java.util.Date
import java.util.UUID._
import java.net.URI
import datomic.Peer
import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}

class Expressions extends CoreSpec {

  class OneSetup extends CoreSetup {
    Ns.str insert List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.int insert List(-2, -1, 0, 1, 2)
    Ns.long insert List(-2L, -1L, 0L, 1L, 2L)
    Ns.float insert List(-2f, -1f, 0f, 1f, 2f)
    Ns.double insert List(-2.0, -1.0, 0.0, 1.0, 2.0)
    Ns.bool insert List(true, false)
    Ns.date insert List(date0, date1, date2)
    Ns.uuid insert List(uuid0, uuid1, uuid2)
    Ns.uri insert List(uri0, uri1, uri2)
    Ns.enum insert List("enum0", "enum1", "enum2")
  }

  class ManySetup extends CoreSetup {

    val (a, b, c, d) = ("a", "b", "c", "d")

    // We pair cardinality many attribute values with card-one's too to be able to group by cardinality one values
    //    Ns.str.strs.debug
    Ns.str.strs insert List(
      ("str1", Set("a", "b")),
      ("str2", Set("b", "c")),
      ("str3", Set("b", "d")))

    Ns.int.ints insert List(
      (1, Set(1, 2)),
      (2, Set(2, 3)),
      (3, Set(2, 4)))

    Ns.long.longs insert List(
      (1L, Set(1L, 2L)),
      (2L, Set(2L, 3L)),
      (3L, Set(2L, 4L)))

    Ns.float.floats insert List(
      (1.0f, Set(1.0f, 2.0f)),
      (2.0f, Set(2.0f, 3.0f)),
      (3.0f, Set(2.0f, 4.0f)))

    Ns.double.doubles insert List(
      (1.0, Set(1.0, 2.0)),
      (2.0, Set(2.0, 3.0)),
      (3.0, Set(2.0, 4.0)))

    // Set of boolean values not relevant

    Ns.date.dates insert List(
      (date1, Set(date1, date2)),
      (date2, Set(date2, date3)),
      (date3, Set(date2, date4)))

    Ns.uuid.uuids insert List(
      (uuid1, Set(uuid1, uuid2)),
      (uuid2, Set(uuid2, uuid3)),
      (uuid3, Set(uuid2, uuid4)))

    //    Ns.uri.uris insert List(
    //      (uri1, Set(uri1, uri2)),
    //      (uri2, Set(uri2, uri3)),
    //      (uri3, Set(uri2, uri4)))

    Ns.enum.enums insert List(
      ("enum1", Set("enum1", "enum2")),
      ("enum2", Set("enum2", "enum3")),
      ("enum3", Set("enum2", "enum4")))
  }


  "Equality - 1 value" >> {

    "Card one" in new OneSetup {

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

      // Todo Allow runtime constructs
      //    Ns.date(new Date()).get === List()


      Ns.uuid(uuid1).get === List(uuid1)
      Ns.uuid(uuid2).get === List(uuid2)


      Ns.uri(uri1).get === List(uri1)
      Ns.uri(uri2).get === List(uri2)


      Ns.enum("enum1").get === List("enum1")
      Ns.enum("enum2").get === List("enum2")
      Ns.enum(enum2).get === List("enum2")

      // Applying a non-existing enum value ("enum3") won't compile!
      // Ns.enum("enum3").get === List("enum3")
    }

    "Card many" in new ManySetup {

      // Coalesce all sets having a value into one set (no order guaranteed)
      Ns.strs("a").get === List(Set("b", "a"))

      // "b" is in all 3 entities, so all values are coalesced to one set
      Ns.strs("b").get === List(Set("d", "a", "b", "c"))
      Ns.strs("c").get === List(Set("c", "b"))
      Ns.strs("d").get === List(Set("d", "b"))

      // Using value assigned to variable
      Ns.strs(d).get === List(Set("d", "b"))


      Ns.ints(1).get === List(Set(1, 2))
      Ns.ints(2).get === List(Set(1, 4, 3, 2))
      Ns.ints(3).get === List(Set(3, 2))
      Ns.ints(4).get === List(Set(4, 2))
      Ns.ints(int1).get === List(Set(1, 2))


      Ns.longs(1).get === List(Set(1, 2))
      Ns.longs(2).get === List(Set(1, 4, 3, 2))
      Ns.longs(3).get === List(Set(3, 2))
      Ns.longs(4).get === List(Set(4, 2))
      Ns.longs(long1).get === List(Set(1, 2))


      Ns.floats(1).get === List(Set(1, 2))
      Ns.floats(2).get === List(Set(1, 4, 3, 2))
      Ns.floats(3).get === List(Set(3, 2))
      Ns.floats(4).get === List(Set(4, 2))
      Ns.floats(float1).get === List(Set(1, 2))


      Ns.doubles(1).get === List(Set(1, 2))
      Ns.doubles(2).get === List(Set(1, 4, 3, 2))
      Ns.doubles(3).get === List(Set(3, 2))
      Ns.doubles(4).get === List(Set(4, 2))
      Ns.doubles(double1).get === List(Set(1, 2))


      // Cardinality-many attribute for boolean values not implemented (doesn't make sense)


      Ns.dates(date1).get === List(Set(date1, date2))
      Ns.dates(date2).get === List(Set(date1, date4, date3, date2))
      Ns.dates(date3).get === List(Set(date3, date2))
      Ns.dates(date4).get === List(Set(date4, date2))


      Ns.uuids(uuid1).get === List(Set(uuid1, uuid2))
      Ns.uuids(uuid2).get === List(Set(uuid1, uuid4, uuid3, uuid2))
      Ns.uuids(uuid3).get === List(Set(uuid3, uuid2))
      Ns.uuids(uuid4).get === List(Set(uuid4, uuid2))


      //    Ns.uris(uri1).get === List(Set(uri1, uri2))
      //    Ns.uris(uri2).get === List(Set(uri1, uri4, uri3, uri2))
      //    Ns.uris(uri3).get === List(Set(uri3, uri2))
      //    Ns.uris(uri4).get === List(Set(uri4, uri2))


      Ns.enums("enum1").get === List(Set("enum1", "enum2"))
      Ns.enums("enum2").get === List(Set("enum1", "enum4", "enum3", "enum2"))
      Ns.enums(enum3).get === List(Set(enum3, enum2))
      Ns.enums(enum4).get === List(Set(enum4, enum2))
    }


    "Card many - group by other attribute" in new ManySetup {

      // Card-many values can group by card-one attributes
      Ns.str.strs("a").get === List(("str1", Set("a", "b")))
      Ns.str.strs("b").get === List(("str1", Set("a", "b")), ("str2", Set("b", "c")), ("str3", Set("d", "b")))
      Ns.str.strs("c").get === List(("str2", Set("b", "c")))
      Ns.str.strs("d").get === List(("str3", Set("d", "b")))
      Ns.str.strs(d).get === List(("str3", Set("d", "b")))


      Ns.int.ints(1).get === List((1, Set(1, 2)))
      Ns.int.ints(2).get === List((1, Set(1, 2)), (2, Set(3, 2)), (3, Set(4, 2)))
      Ns.int.ints(3).get === List((2, Set(3, 2)))
      Ns.int.ints(4).get === List((3, Set(4, 2)))
      Ns.int.ints(int3).get === List((2, Set(3, 2)))


      Ns.long.longs(1L).get === List((1L, Set(1L, 2L)))
      Ns.long.longs(2L).get === List((1L, Set(1L, 2L)), (2L, Set(3L, 2L)), (3L, Set(4L, 2L)))
      Ns.long.longs(3L).get === List((2L, Set(3L, 2L)))
      Ns.long.longs(4L).get === List((3L, Set(4L, 2L)))
      Ns.long.longs(long3).get === List((2L, Set(3L, 2L)))


      Ns.float.floats(1.0f).get === List((1.0f, Set(1.0f, 2.0f)))
      Ns.float.floats(2.0f).get === List((1.0f, Set(1.0f, 2.0f)), (2.0f, Set(3.0f, 2.0f)), (3.0f, Set(4.0f, 2.0f)))
      Ns.float.floats(3.0f).get === List((2.0f, Set(3.0f, 2.0f)))
      Ns.float.floats(4.0f).get === List((3.0f, Set(4.0f, 2.0f)))
      Ns.float.floats(float3).get === List((2.0f, Set(3.0f, 2.0f)))


      Ns.double.doubles(1.0).get === List((1.0, Set(1.0, 2.0)))
      Ns.double.doubles(2.0).get === List((1.0, Set(1.0, 2.0)), (2.0, Set(3.0, 2.0)), (3.0, Set(4.0, 2.0)))
      Ns.double.doubles(3.0).get === List((2.0, Set(3.0, 2.0)))
      Ns.double.doubles(4.0).get === List((3.0, Set(4.0, 2.0)))
      Ns.double.doubles(double3).get === List((2.0, Set(3.0, 2.0)))


      Ns.date.dates(date1).get === List((date1, Set(date1, date2)))
      Ns.date.dates(date2).get === List((date1, Set(date1, date2)), (date2, Set(date3, date2)), (date3, Set(date4, date2)))
      Ns.date.dates(date3).get === List((date2, Set(date3, date2)))
      Ns.date.dates(date4).get === List((date3, Set(date4, date2)))


      Ns.uuid.uuids(uuid1).get.sortBy(_.toString) === List((uuid1, Set(uuid1, uuid2)))
      Ns.uuid.uuids(uuid2).get.sortBy(_.toString) === List((uuid1, Set(uuid2, uuid1)), (uuid2, Set(uuid3, uuid2)), (uuid3, Set(uuid4, uuid2)))
      Ns.uuid.uuids(uuid3).get.sortBy(_.toString) === List((uuid2, Set(uuid2, uuid3)))
      Ns.uuid.uuids(uuid4).get.sortBy(_.toString) === List((uuid3, Set(uuid2, uuid4)))


      //    Ns.ur.urs(ur1).get === List((ur1, Set(ur1, ur2)))
      //    Ns.ur.urs(ur2).get === List((ur1, Set(ur1, ur2)), (ur2, Set(ur3, ur2)), (ur3, Set(ur4, ur2)))
      //    Ns.ur.urs(ur3).get === List((ur2, Set(ur3, ur2)))
      //    Ns.ur.urs(ur4).get === List((ur3, Set(ur4, ur2)))


      Ns.enum.enums("enum1").get === List(("enum1", Set("enum1", "enum2")))
      Ns.enum.enums("enum2").get === List(("enum1", Set("enum1", "enum2")), ("enum2", Set("enum3", "enum2")), ("enum3", Set("enum4", "enum2")))
      Ns.enum.enums(enum3).get === List((enum2, Set(enum3, enum2)))
      Ns.enum.enums(enum4).get === List((enum3, Set(enum4, enum2)))
    }


    "Card many - group by other attribute value" in new ManySetup {

      // We could also match the card-one value to get the corresponding card-many set of values
      Ns.str("str1").strs.get === List(("str1", Set("a", "b")))
      Ns.str("str2").strs.get === List(("str2", Set("b", "c")))
      Ns.str("str3").strs.get === List(("str3", Set("b", "d")))
      // etc...
    }

    "Relationships" in new OneSetup {
      ok
    }
  }


  "Equality - multiple values (OR-logic)" >> {

    "Card one" in new OneSetup {

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
      Ns.uuid(uuidList).get.sortBy(_.toString) === List(uuid1, uuid2)


      Ns.uri(uri1, uri2).get.sortBy(_.toString) === List(uri1, uri2)
      Ns.uri(uri1 or uri2).get.sortBy(_.toString) === List(uri1, uri2)
      Ns.uri(List(uri1, uri2)).get.sortBy(_.toString) === List(uri1, uri2)
      val uriList = List(uri1, uri2)
      Ns.uri(uriList).get.sortBy(_.toString) === List(uri1, uri2)


      Ns.enum("enum1", "enum2").get.sorted === List("enum1", "enum2")
      Ns.enum(enum1, enum2).get.sorted === List("enum1", "enum2")
      Ns.enum(enum1 or enum2).get.sorted === List("enum1", "enum2")
      Ns.enum(List(enum1, enum2)).get.sorted === List("enum1", "enum2")
      val enumList = List(enum1, enum2)
      Ns.enum(enumList).get.sorted === List(enum1, enum2)
    }

    "Card many" in new ManySetup {

      // Multiple sets are coalesced to one set with unique values:
      // Set("a", "b") + Set("b", "c") -> Set("a", "b", "c")

      // 3 ways of applying the same OR-semantics:

      // 1. `or`-separated values
      Ns.strs("a" or "c").get === List(Set("a", "b", "c"))

      // 2. Comma-separated values
      Ns.strs("a", "b").get === List(Set("d", "a", "b", "c"))
      Ns.strs("a", "c").get === List(Set("a", "b", "c"))
      Ns.strs("a", "d").get === List(Set("a", "b", "d"))

      // 3. Set of values (note that this differs from card-one attributes that use Seq/List)
      Ns.strs(Set("a", "c")).get === List(Set("a", "b", "c"))



      // Using variables
      Ns.strs(a or c).get === List(Set("a", "b", "c"))
      Ns.strs(a, c).get === List(Set("a", "b", "c"))
      Ns.strs(Set(a, c)).get === List(Set("a", "b", "c"))
      val strSet = Set(a, c)
      Ns.strs(strSet).get === List(Set("a", "b", "c"))

      // We can even supply multiple comma-separated Sets of search values
      Ns.strs.apply(Set(a, c), Set(d)).get === List(Set("d", "a", "b", "c"))


      Ns.ints(1 or 3).get === List(Set(1, 2, 3))
      Ns.ints(Set(1, 3)).get === List(Set(1, 2, 3))
      Ns.ints(1, 2).get === List(Set(1, 4, 3, 2))
      Ns.ints(1, 3).get === List(Set(1, 2, 3))
      Ns.ints(1, 4).get === List(Set(1, 2, 4))

      Ns.ints(int1 or int3).get === List(Set(1, 2, 3))
      Ns.ints(Set(int1, int3)).get === List(Set(1, 2, 3))
      Ns.ints(int1, int3).get === List(Set(1, 2, 3))
      val intSet = Set(int1, int3)
      Ns.ints(intSet).get === List(Set(1, 2, 3))


      Ns.longs(1L or 3L).get === List(Set(1L, 2L, 3L))
      Ns.longs(Set(1L, 3L)).get === List(Set(1L, 2L, 3L))
      Ns.longs(1L, 2L).get === List(Set(1L, 4L, 3L, 2L))
      Ns.longs(1L, 3L).get === List(Set(1L, 2L, 3L))
      Ns.longs(1L, 4L).get === List(Set(1L, 2L, 4L))

      Ns.longs(long1 or long3).get === List(Set(1L, 2L, 3L))
      Ns.longs(Set(long1, long3)).get === List(Set(1L, 2L, 3L))
      Ns.longs(long1, long3).get === List(Set(1L, 2L, 3L))
      val longSet = Set(long1, long3)
      Ns.longs(longSet).get === List(Set(1L, 2L, 3L))


      Ns.floats(1.0f or 3.0f).get === List(Set(1.0f, 2.0f, 3.0f))
      Ns.floats(Set(1.0f, 3.0f)).get === List(Set(1.0f, 2.0f, 3.0f))
      Ns.floats(1.0f, 2.0f).get === List(Set(1.0f, 4.0f, 3.0f, 2.0f))
      Ns.floats(1.0f, 3.0f).get === List(Set(1.0f, 2.0f, 3.0f))
      Ns.floats(1.0f, 4.0f).get === List(Set(1.0f, 2.0f, 4.0f))

      Ns.floats(float1 or float3).get === List(Set(1.0f, 2.0f, 3.0f))
      Ns.floats(Set(float1, float3)).get === List(Set(1.0f, 2.0f, 3.0f))
      Ns.floats(float1, float3).get === List(Set(1.0f, 2.0f, 3.0f))
      val floatSet = Set(float1, float3)
      Ns.floats(floatSet).get === List(Set(1.0f, 2.0f, 3.0f))


      Ns.doubles(1.0 or 3.0).get === List(Set(1.0, 2.0, 3.0))
      Ns.doubles(Set(1.0, 3.0)).get === List(Set(1.0, 2.0, 3.0))
      Ns.doubles(1.0, 2.0).get === List(Set(1.0, 4.0, 3.0, 2.0))
      Ns.doubles(1.0, 3.0).get === List(Set(1.0, 2.0, 3.0))
      Ns.doubles(1.0, 4.0).get === List(Set(1.0, 2.0, 4.0))

      Ns.doubles(double1 or double3).get === List(Set(1.0, 2.0, 3.0))
      Ns.doubles(Set(double1, double3)).get === List(Set(1.0, 2.0, 3.0))
      Ns.doubles(double1, double3).get === List(Set(1.0, 2.0, 3.0))
      val doubleSet = Set(double1, double3)
      Ns.doubles(doubleSet).get === List(Set(1.0, 2.0, 3.0))


      // Cardinality-many attribute for boolean values not implemented (doesn't make sense)


      Ns.dates(date1 or date3).get === List(Set(date1, date2, date3))
      Ns.dates(Set(date1, date3)).get === List(Set(date1, date2, date3))
      Ns.dates(date1, date2).get === List(Set(date1, date4, date3, date2))
      Ns.dates(date1, date3).get === List(Set(date1, date2, date3))
      Ns.dates(date1, date4).get === List(Set(date1, date2, date4))
      val dateSet = Set(date1, date3)
      Ns.dates(dateSet).get === List(Set(date1, date2, date3))


      //    Ns.uris(uri1 or uri2).get === List(Set(uri1, uri4, uri3, uri2))
      //    Ns.uris(Set(uri1, uri2)).get === List(Set(uri1, uri4, uri3, uri2))
      //    Ns.uris(uri1, uri2).get === List(Set(uri1, uri4, uri3, uri2))
      //    Ns.uris(uri1, uri3).get === List(Set(uri1, uri2, uri3))
      //    Ns.uris(uri1, uri4).get === List(Set(uri1, uri2, uri4))
      //    val uriSet = Set(uri1, uri3)
      //    Ns.uris(uriSet).get === List(Set(uri1, uri2, uri3))


      Ns.uuids(uuid1 or uuid3).get === List(Set(uuid1, uuid2, uuid3))
      Ns.uuids(Set(uuid1, uuid3)).get === List(Set(uuid1, uuid2, uuid3))
      Ns.uuids(uuid1, uuid2).get === List(Set(uuid1, uuid4, uuid3, uuid2))
      Ns.uuids(uuid1, uuid3).get === List(Set(uuid1, uuid2, uuid3))
      Ns.uuids(uuid1, uuid4).get === List(Set(uuid1, uuid2, uuid4))
      val uuidSet = Set(uuid1, uuid3)
      Ns.uuids(uuidSet).get === List(Set(uuid1, uuid2, uuid3))


      Ns.enums("enum1" or "enum3").get === List(Set("enum1", "enum3", "enum2"))
      Ns.enums(Set("enum1", "enum3")).get === List(Set("enum1", "enum3", "enum2"))
      Ns.enums(enum1, enum2).get === List(Set(enum1, enum4, enum3, enum2))
      Ns.enums(enum1, enum3).get === List(Set(enum1, enum2, enum3))
      Ns.enums(enum1, enum4).get === List(Set(enum1, enum2, enum4))
      val enumSet = Set(enum1, enum3)
      Ns.enums(enumSet).get === List(Set(enum1, enum2, enum3))
    }

    "Relationship" in new ManySetup {
      ok
    }
  }


  "Negation" >> {

    "Include entities with non-asserted datoms (null values)" in new OneSetup {
      Ns.str.int.long insert List(
        ("foo", 1, 1L),
        ("bar", 2, 2L),
        ("baz", 3, null.asInstanceOf[Long])
      )

      // "baz" not returned since that entity has no long datom asserted
      Ns.str.int_.long_.not(1L).get === List("bar")

      // Entities where long is not asserted (is null)
      Ns.str.int_.long(nil).get === List("baz")
    }

    "Exclude 1 or more card one values" in new OneSetup {

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

      // todo when we get a string representation #uri
      //    val uri = new URI("other")
      //    Ns.uri.not(uri).get.sortBy(_.toString) === List(uri0, uri1, uri2)
      //    Ns.uri.not(uri1).get.sortBy(_.toString) === List(uri0, uri2)

      Ns.enum.not("enum0").get.sorted === List(enum1, enum2)
      Ns.enum.not("enum0", "enum1").get.sorted === List(enum2)
      Ns.enum.not("enum0", "enum1", "enum2").get.sorted === List()
      Ns.enum.not(enum0).get.sorted === List(enum1, enum2)
      Ns.enum.not(enum0, enum1).get.sorted === List(enum2)
    }

    "Exclude 1 or more card many values" in new ManySetup {

      // Negation of a cardinality-many attribute value is rather useless since it
      // will just return the coalesced set minus the excluded value
      Ns.strs.not("b").get === List(Set("d", "a", "c"))

      // We could group by another attribute ut that still leave us with filtered sets
      //      Ns.str.strs.not("a").debug
      Ns.str.strs.not("a").get === List(("str1", Set("b")), ("str2", Set("b", "c")), ("str3", Set("d", "b")))
      Ns.str.strs.not("b").get === List(("str1", Set("a")), ("str2", Set("c")), ("str3", Set("d")))
      Ns.str.strs.not("c").get === List(("str1", Set("a", "b")), ("str2", Set("b")), ("str3", Set("d", "b")))
      Ns.str.strs.not("d").get === List(("str1", Set("a", "b")), ("str2", Set("b", "c")), ("str3", Set("b")))

      // What we probably want to do is to filter out full sets having the negation value:
      Ns.str.strs.get.filter(!_._2.contains("a")) === List(("str2", Set("b", "c")), ("str3", Set("d", "b")))
      Ns.str.strs.get.filter(!_._2.contains("b")) === List()
      Ns.str.strs.get.filter(!_._2.contains("c")) === List(("str1", Set("a", "b")), ("str3", Set("d", "b")))
      Ns.str.strs.get.filter(!_._2.contains("d")) === List(("str1", Set("a", "b")), ("str2", Set("b", "c")))
      // etc...
    }
  }


  "Comparison" >> {

    "Card one" in new OneSetup {

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


      // todo when we get a string representation #uri
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


    "Card many" in new ManySetup {

      // Comparing cardinality-many attribute values is also rather useless since
      // the comparison is performed against the coalesced set of values
      Ns.strs.>("b").get === List(Set("d", "c"))

      // Instead we probably want to group by another attribute and then
      // compare the values of each set of cardinality-many values:
      Ns.str.strs.get.filter(_._2.forall(_ > "a")) === List(("str2", Set("b", "c")), ("str3", Set("d", "b")))
      // etc...
    }
  }


  "Fulltext search" >> {

    "Card one" in new CoreSetup {

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

    "Card many" in new ManySetup {

      // Searching for text strings in cardinality-many attribute values is rather useless since the
      // coalesed set of values is searched and not the original sets of values
      Ns.strs.contains("c").get === List(Set("c"))

      // What we want is probably rather to group by another attribute to
      // find the cardinality-many sets of values matching the search string:
      Ns.str.strs.get.filter(_._2.contains("c")) === List(("str2", Set("b", "c")))
      // etc...
    }
  }
}

//import datomic._
//import java.net.URI
//    Peer.q( s"""[:find ?a ?b :where [?a :ns/uri ?b]]""", conn.db) === 76
//      [[ 17592186045436 #< URI uri1 >], [17592186045437 #< URI uri2 >]]
//
//    Both give empty result set
//    Peer.q( s"""[:find ?b :where [17592186045493 :ns/uri ?b]]""", conn.db) === 42

//        Peer.q( """[:find ?a :where [?a :ns/uri (ground (java.net.URI. "uri1")) ?uri]]""".stripMargin, conn.db) === 42

//    Peer.q(
//          """
//[:find  ?b
// :in    $ %
// :where [?a :ns/uri ?b]
//        (rule1 ?a)]
//          """.stripMargin, conn.db, """[
//       [(rule1 ?a) [(ground (java.net.URI. "uri1")) ?b][?a :ns/uri ?b]]
//       [(rule1 ?a) [(ground (java.net.URI. "uri2")) ?b][?a :ns/uri ?b]]
//                                                ]""") === 42
//    Peer.q(
//          """[:find  ?a
//              :where [(ground (java.net.URI. "uri1")) ?uri]
//                     [?a :ns/uri ?uri]]
//                     ]""".stripMargin, conn.db) === 42
//
//           Peer.q( """[:find ?a :where [?a :ns/uri #java.net.URI "uri1"]]""", conn.db) === 42
//
//import datomic._
//import java.net.URI
//    Peer.q( s"""[:find ?a ?b :where [?a :ns/uri ?b]]""", conn.db) === 76
//      [[ 17592186045436 #< URI uri1 >], [17592186045437 #< URI uri2 >]]
//
//    Both give empty result set
//    Peer.q( s"""[:find ?b :where [17592186045493 :ns/uri ?b]]""", conn.db) === 42
//
//        Peer.q( """[:find ?a :where [?a :ns/uri (ground (java.net.URI. "uri1")) ?uri]]""".stripMargin, conn.db) === 42
//        Peer.q(
//          """[:find  ?a
//              :where [(ground (java.net.URI. "uri1")) ?uri]
//                     [?a :ns/uri ?uri]]
//                     ]""".stripMargin, conn.db) === 42
//        Peer.q( """[:find ?a :where [?a :ns/uri #java.net.URI["uri1"]]]""", conn.db) === 42
//
//    Peer.q( s"""[:find ?a :where [?a :ns/uri (java.net.URI. "uri1")]]""", conn.db) === 42
//
//    Peer.q("[:find ?a :in $ ?b :where [?a :ns/uri ?b]]", conn.db, "(java.net.URI. \"uri1\")") === 42
//    Peer.q("[:find ?a :in $ ?b :where [?a :ns/uri ?b]]", conn.db, "uri1") === 42
//    Peer.q("[:find ?a :in $ ?b :where [?a :ns/uri ?b]]", conn.db, new URI("uri1")) === List(17592186045494L)
//    java.lang.UnsupportedOperationException: nth not supported on this type: IndexSet
//
//    java.lang.UnsupportedOperationException: nth not supported on this type: IndexSet
//
//    java.lang.UnsupportedOperationException: nth not supported on this type: IndexSet

//    Ns.uri.debug
//    Ns.uri(uri1).debug
//        Ns.uri.apply("uri1").get === List(uri1)
//        Ns.uri(uri1).debug