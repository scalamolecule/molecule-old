package molecule
package semantics
import java.util.Date

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

    Ns.date.dates insert List(
      (date1, Set(date1, date2)),
      (date2, Set(date2, date3)),
      (date3, Set(date2, date4)))

    Ns.uuid.uuids insert List(
      (uuid1, Set(uuid1, uuid2)),
      (uuid2, Set(uuid2, uuid3)),
      (uuid3, Set(uuid2, uuid4)))

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

    // Multiple sets are coalesced to one set with unique values
    Ns.strs("a", "c").get === List(Set("a", "b", "c")) // Set("a", "b") + Set("b", "c")
    Ns.strs("a" or "c").get === List(Set("a", "b", "c"))
    Ns.strs(Set("a", "c")).get === List(Set("a", "b", "c"))

    // Using variables
    Ns.strs(Set(a, c)).get === List(Set("a", "b", "c"))
    val set = Set(a, c)
    Ns.strs(set).get === List(Set("a", "b", "c"))

  }


  "Group by other attribute" in new ManySetup {

    // Card-many values can group by card-one attributes
    Ns.str.strs("a").get === List(("str1", Set("a", "b")))
    Ns.str.strs("b").get === List(("str1", Set("a", "b")), ("str2", Set("b", "c")), ("str3", Set("d", "b")))
    Ns.str.strs("c").get === List(("str2", Set("b", "c")))
    Ns.str.strs("d").get === List(("str3", Set("d", "b")))
    Ns.str.strs(d).get === List(("str3", Set("d", "b")))

  }


  "Group and search by other attribute" in new ManySetup {

    // We could also match the card-one value to get the corresponding card-many set of values
    Ns.str("str3").strs.get === List(("str3", Set("d", "b")))


    Ns.ints(2).get === List(Set(1, 2), Set(2, 3), Set(2, 4))


    //    Ns.int(1).get === List(1)
    //    Ns.int(0).get === List(0)
    //    Ns.int(-1).get === List(-1)
    //    Ns.int(int1).get === List(1)
    //
    //
    //    Ns.long(1L).get === List(1L)
    //    Ns.long(0L).get === List(0L)
    //    Ns.long(-1L).get === List(-1L)
    //    Ns.long(long1).get === List(1L)
    //
    //    // We can also apply Int values to a Long Attribute
    //    Ns.long(1).get === List(1)
    //
    //    Ns.float(1f).get === List(1f)
    //    Ns.float(0f).get === List(0f)
    //    Ns.float(-1f).get === List(-1f)
    //    Ns.float(float1).get === List(1f)
    //
    //    Ns.double(1.0).get === List(1.0)
    //    Ns.double(0.0).get === List(0.0)
    //    Ns.double(-1.0).get === List(-1.0)
    //    Ns.double(double1).get === List(1.0)
    //
    //    Ns.bool(true).get === List(true)
    //    Ns.bool(false).get === List(false)
    //    Ns.bool(bool1).get === List(true)
    //
    //    Ns.uuid(date1).get === List(date1)
    //    Ns.date(date2).get === List(date2)
    //
    //    Ns.uuid(uuid1).get === List(uuid1)
    //    Ns.uuid(uuid2).get === List(uuid2)
    //
    ////    Ns.uri.debug
    ////    Ns.uri(uri1).debug
    ////    Ns.uri(uri1).get === List(uri1)
    ////    Ns.uri(uri2).get === List(uri2)
    //
    //    Ns.enum("enum1").get === List("enum1")
    //    Ns.enum("enum2").get === List("enum2")
    //    Ns.enum(enum2).get === List("enum2")

    // Applying a non-existing enum value ("enum3") won't compile!
    // Ns.enum("enum3").get === List("enum3")
  }
}
//import datomic._
//Peer.q(
//  s"""
//     |[:find  (distinct ?b)
//     |:where [?a :ns/strs "b"]
//     |     [?a :ns/strs ?b]]
//     """.stripMargin, conn.db) === 42
