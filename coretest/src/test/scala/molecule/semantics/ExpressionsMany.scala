package molecule
package semantics
import molecule.semantics.dsl.coreTest._

class ExpressionsMany extends CoreSpec {

  class ManySetup extends CoreSetup {

    val (a, b, c, d) = ("a", "b", "c", "d")

    // We pair cardinality many attribute values with card-one's too to be able to group by cardinality one values
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


  "Match one value" in new ManySetup {

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


  "Match multiple values" in new ManySetup {

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


  "Exclude 1 or more values" in new ManySetup {

    // Negation of a cardinality-many attribute value is rather useless since it
    // will just return the coalesced set minus the excluded value
    Ns.strs.not("b").get === List(Set("d", "a", "c"))

    // What we probably want to do instead is to group by another attribute and
    // then filter out the sets having the value to exclude:
    Ns.str.strs.get.filter(!_._2.contains("a")) === List(("str2", Set("b", "c")), ("str3", Set("d", "b")))
    Ns.str.strs.get.filter(!_._2.contains("b")) === List()
    Ns.str.strs.get.filter(!_._2.contains("c")) === List(("str1", Set("a", "b")), ("str3", Set("d", "b")))
    Ns.str.strs.get.filter(!_._2.contains("d")) === List(("str1", Set("a", "b")), ("str2", Set("b", "c")))
  }

  "Compare values" in new ManySetup {

    ok
  }
  //
  //  "Search text" in new ManySetup {
  //
  //    ok
  //  }


  "Group by other attribute" in new ManySetup {

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

  //
  //  "Group by other attribute value" in new ManySetup {
  //
  //    // We could also match the card-one value to get the corresponding card-many set of values
  //    Ns.str("str1").strs.get === List(("str1", Set("a", "b")))
  //    Ns.str("str2").strs.get === List(("str2", Set("b", "c")))
  //    Ns.str("str3").strs.get === List(("str3", Set("b", "d")))
  //
  //
  //    //    Ns.longs(2).get === List(Set(1, 2), Set(2, 3), Set(2, 4))
  //
  //
  //    //    Ns.int(1).get === List(1)
  //    //    Ns.int(0).get === List(0)
  //    //    Ns.int(-1).get === List(-1)
  //    //    Ns.int(int1).get === List(1)
  //    //
  //    //
  //    //    Ns.long(1L).get === List(1L)
  //    //    Ns.long(0L).get === List(0L)
  //    //    Ns.long(-1L).get === List(-1L)
  //    //    Ns.long(long1).get === List(1L)
  //    //
  //    //    // We can also apply Int values to a Long Attribute
  //    //    Ns.long(1).get === List(1)
  //    //
  //    //    Ns.float(1f).get === List(1f)
  //    //    Ns.float(0f).get === List(0f)
  //    //    Ns.float(-1f).get === List(-1f)
  //    //    Ns.float(float1).get === List(1f)
  //    //
  //    //    Ns.double(1.0).get === List(1.0)
  //    //    Ns.double(0.0).get === List(0.0)
  //    //    Ns.double(-1.0).get === List(-1.0)
  //    //    Ns.double(double1).get === List(1.0)
  //    //
  //    //    Ns.bool(true).get === List(true)
  //    //    Ns.bool(false).get === List(false)
  //    //    Ns.bool(bool1).get === List(true)
  //    //
  //    //    Ns.date(date1).get === List(date1)
  //    //    Ns.date(date2).get === List(date2)
  //    //
  //    //    Ns.uuid(uuid1).get === List(uuid1)
  //    //    Ns.uuid(uuid2).get === List(uuid2)
  //    //
  //    ////    Ns.uri.debug
  //    ////    Ns.uri(uri1).debug
  //    ////    Ns.uri(uri1).get === List(uri1)
  //    ////    Ns.uri(uri2).get === List(uri2)
  //    //
  //    //    Ns.enum("enum1").get === List("enum1")
  //    //    Ns.enum("enum2").get === List("enum2")
  //    //    Ns.enum(enum2).get === List("enum2")
  //
  //    // Applying a non-existing enum value ("enum3") won't compile!
  //    // Ns.enum("enum3").get === List("enum3")
  //  }
}
//import datomic._
//Peer.q(
//  s"""
//     |[:find  (distinct ?b)
//     |:where [?a :ns/strs "b"]
//     |     [?a :ns/strs ?b]]
//     """.stripMargin, conn.db) === 42
