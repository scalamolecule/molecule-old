package molecule
package expression
import java.util.Date
import java.util.UUID._
import java.net.URI
import datomic.Peer
import molecule.util.dsl.coreTest._
import molecule.util.expectCompileError

class Negation extends Base {

  "Include entities with non-asserted datoms (null values)" in new OneSetup {
    Ns.str.int.long$ insert List(
      ("foo", 1, Some(1L)),
      ("bar", 2, Some(2L)),
      ("baz", 3, None)
    )

    // "baz" not returned since that entity has no long datom asserted
    Ns.str.int_.long_.not(1L).get === List("bar")

    // Entities where long is not asserted (is null)
    Ns.str.int_.long_(nil).get === List("baz")

    // Entities with no ref1
    Ns.str.int_.ref1_(nil).get === List("bar", "foo", "baz")

    // Makes no sense to return null, so only tacet attributes are allowed
    expectCompileError(
      "m(Ns.str.int_.long(nil))",
      "[Dsl2Model:getValues] Please add underscore to attribute: `long_(nil)`")

    expectCompileError(
      "m(Ns.str.int_.ref1(nil))",
      "[Dsl2Model:getValues] Please add underscore to attribute: `ref1_(nil)`")
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

    // Negate multiple values ("NOR"-logic: not 'a NOR 'b NOR 'c...)
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

    // todo: when Datomic gets a string representation #uri
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
    Ns.strs.not("b").get === List(Set("d", "a", "ba", "c"))

    // We could group by another attribute but that still leave us with filtered sets
    Ns.str.strs.not("a").get === List(("str1", Set("b")), ("str2", Set("b", "c")), ("str3", Set("d", "ba")))
    Ns.str.strs.not("b").get === List(("str1", Set("a")), ("str2", Set("c")), ("str3", Set("d", "ba")))
    Ns.str.strs.not("c").get === List(("str1", Set("a", "b")), ("str2", Set("b")), ("str3", Set("d", "ba")))
    Ns.str.strs.not("d").get === List(("str1", Set("a", "b")), ("str2", Set("b", "c")), ("str3", Set("ba")))

    // What we probably want to do is to filter out full sets having the negation value:
    Ns.str.strs.get.filterNot(_._2.contains("a")) === List(("str2", Set("b", "c")), ("str3", Set("d", "ba")))
    Ns.str.strs.get.filterNot(_._2.contains("b")) === List(("str3", Set("d", "ba")))
    Ns.str.strs.get.filterNot(_._2.contains("c")) === List(("str1", Set("a", "b")), ("str3", Set("d", "ba")))
    Ns.str.strs.get.filterNot(_._2.contains("d")) === List(("str1", Set("a", "b")), ("str2", Set("b", "c")))
  }
}