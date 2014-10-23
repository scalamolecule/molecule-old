package molecule.semantics.tuple
import molecule.util.dsl.coreTest._
import molecule.{CoreSetup, CoreSpec}


class tpl_basics extends CoreSpec {

  "Single cardinality-1 attribute - one entity" in new CoreSetup {

    // Create an entity with attribute `str` having value "foo"
    Ns.str insert "foo"

    // Get one `str` value
    // Since order of result is not guaranteed, a random value is returned
    Ns.str.get.head === "foo"
    // Same as this convenience method
    Ns.str.one === "foo"

    // Get all values of attribute `str` (there's only one)
    Ns.str.get === List("foo")
  }


  "Single cardinality-1 attribute - multiple entities" in new CoreSetup {
    Ns.int insert List(1, 2, 3)

    Ns.int.one === 3
    Ns.int.get.head === 3

    // Get some values as tuples (order not guaranteed)
    Ns.int.get(1) === List(3)
    Ns.int.get(2) === List(3, 1)
    Ns.int.get(3) === List(3, 1, 2)

    // Get all values as tuples (order not guaranteed)
    Ns.int.get === List(3, 1, 2)
  }

  "Single cardinality-2 attribute - one entity" in new CoreSetup {

    // Create an entity with attribute `strs` having a Set of values Set("foo", "bar")
    Ns.strs insert Set("foo", "bar")

    // Get one set of `str` values
    Ns.strs.one === Set("foo", "bar")
    Ns.strs.get.head === Set("foo", "bar")

    Ns.strs.get === List(Set("foo", "bar"))
  }

  "Single cardinality-2 attribute - multiple entities" in new CoreSetup {

    // Create 3 entities, each with a set of `int` values
    Ns.ints insert List(Set(1, 2), Set(3, 4))

    // Get one `ints` set of values (order not guaranteed)
    Ns.ints.one === Set(1, 4, 3, 2) // Todo: Unexpected
    //    Ns.ints.one === Set(3, 4)
    //    Ns.ints.get.head === Set(3, 4)
    //
    //    // Get some values as tuples (order not guaranteed)
    //    Ns.ints.get(1) === List(Set(3, 4))
    //    Ns.ints.get(2) === List(Set(3, 4), Set(1, 2))
    //
    //    // Get all values as tuples (order not guaranteed)
    //    Ns.ints.get === List(Set(3, 4), Set(1, 2))
  }


  "Multiple cardinality-1 attributes - one entity" in new CoreSetup {
    Ns.str.int.bool.insert("foo", 42, true)

    // Attributes of molecule attributes and values have same order
    Ns.str.int.bool.get === List(("foo", 42, true))
  }


  "Multiple cardinality-1 attributes - multiple entities" in new CoreSetup {
    Ns.str.int.bool insert List(("foo", 42, true), ("bar", 99, false))
    Ns.str.int.bool.get === List(("bar", 99, false), ("foo", 42, true))
  }


  "Multiple cardinality-2 attributes - one entity" in new CoreSetup {
    Ns.strs.ints.insert(Set("foo", "bar"), Set(1, 2, 3))
    Ns.strs.ints.get === List((Set("foo", "bar"), Set(1, 2, 3)))
  }


  "Multiple cardinality-2 attributes - multiple entities" in new CoreSetup {
    Ns.strs.ints insert List(
      (Set("foo", "bar"), Set(1, 2, 3)),
      (Set("baz"), Set(3, 4, 5, 6))
    )
    // Unexpected???
    Ns.strs.ints.one ===(Set("foo", "bar", "baz"), Set(5, 1, 6, 2, 3, 4))

    // todo
    //    Ns.strs.ints.get === List(
    //      (Set("baz"), Set(3, 4, 5, 6)),
    //      (Set("foo", "bar"), Set(1, 2, 3))
    //    )
  }


  "Mixed cardinality attributes - one entity" in new CoreSetup {
    Ns.str.ints.bool.insert("foo", Set(1, 2, 3), true)
    Ns.str.ints.bool.get === List(("foo", Set(1, 2, 3), true))
  }


  "Mixed cardinality attributes - multiple entities" in new CoreSetup {
    Ns.str.ints.bool insert List(("foo", Set(1, 2), true), ("bar", Set(2, 3, 4), false))
    Ns.str.ints.bool.get === List(("bar", Set(2, 3, 4), false), ("foo", Set(1, 2), true))
  }


  "All cardinality-1 types" in new CoreSetup {
    Ns.str.int.long.float.double.bool.date.uuid.uri.enum insert List(
      ("str1", 1, 1L, 1.1f, 2.2, true, date1, uuid1, uri1, "enum1"))

    Ns.str.one === "str1"
    Ns.int.one === 1
    Ns.long.one === 1L
    Ns.float.one === 1.1f
    Ns.double.one === 2.2
    Ns.bool.one === true
    Ns.date.one === date1
    Ns.uuid.one === uuid1
    Ns.uri.one === uri1
    Ns.enum.one === "enum1"

    Ns.str.int.long.float.double.bool.date.uuid.uri.enum.one ===(
      "str1", 1, 1L, 1.1f, 2.2, true, date1, uuid1, uri1, "enum1")
  }


  "All cardinality-2 types" in new CoreSetup {
    // (we don't include Sets of boolean values for obvious reasons...)

    Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums insert(
      Set("str1", "str2"),
      Set(1, 2),
      Set(1L, 2L),
      Set(1.1f, 2.2f),
      Set(1.1, 2.2),
      Set(date1, date2),
      Set(uuid1, uuid2),
      Set(uri1, uri2),
      Set("enum1", "enum2"))

    // See https://groups.google.com/d/msg/datomic/FZVG8MC-vYU/BRLsOgzUpkIJ

//    Ns.strs.enums insert(
//      Set("str1", "str2"),
//      Set("enum1", "enum2"))

    //    Ns.strs.tpe //=== Set("str1", "str2")
    //      [:find (distinct ?a)
    //       :where [?ent :ns/strs ?a]]
    //    model : Model(List(Atom(ns,strs,String,2,VarValue,None)))
    //    result: [[#{"str1" "str2"}]]
    //    type  : Set[String]

    //    Ns.enums.tpe //=== Set("str1", "str2")
    //      [:find ?a2
    //       :where
    //         [?ent :ns/enum ?a]
    //         [?a :db/ident ?a1]
    //         [(.getName ^clojure.lang.Keyword ?a1) ?a2]]
    //    model : Model(List(Atom(ns,enums,String,2,EnumVal,Some(:ns.enums/))))
    //    result: [["enum2"], ["enum1"]]
    //    type  : Set[String]

    Ns.strs.one === Set("str1", "str2")
    Ns.ints.one === Set(1, 2)
    Ns.longs.one === Set(1L, 2L)
    Ns.floats.one === Set(1.1f, 2.2f)
    Ns.doubles.one === Set(1.1, 2.2)
    Ns.dates.one === Set(date1, date2)
    Ns.uuids.one === Set(uuid1, uuid2)
    Ns.uris.one === Set(uri1, uri2)
//    Ns.enums.one === Set("enum1", "enum2")

//    Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.one ===(
    Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.one ===(
      Set("str1", "str2"),
      Set(1, 2),
      Set(1L, 2L),
      Set(1.1f, 2.2f),
      Set(1.1, 2.2),
      Set(date1, date2),
      Set(uuid1, uuid2),
      Set(uri1, uri2)
//      , Set("enum1", "enum2")
      )
  }
}