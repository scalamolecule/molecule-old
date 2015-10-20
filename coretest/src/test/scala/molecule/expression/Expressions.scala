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

}