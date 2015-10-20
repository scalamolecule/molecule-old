package molecule
import java.util.Date
import java.util.UUID._
import java.net.URI
import datomic.Peer
import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}

class Comparison extends Expressions {

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

    // Comparing cardinality-many attribute values is performed
    // against the coalesced set of all (!) values:
    Ns.strs.>("b").get === List(Set("d", "c"))

    // Often we probably instead want to _group by another attribute_ and
    // then compare each set of cardinality-many values:
    Ns.str.strs.get.filter(_._2.forall(_ > "a")) === List(("str2", Set("b", "c")), ("str3", Set("d", "b")))
    // etc...
  }
}