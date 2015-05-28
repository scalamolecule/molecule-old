package molecule
import java.net.URI
import java.util.{Date, UUID}

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}

class EntityAPI extends CoreSpec {


  class OneSetup extends CoreSetup {

    val List(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11) = Ns
      .str.int.long.float.double.bool.date.uuid.uri.enum insert List(
      ("a", 1, long1, float1, double1, bool1, date1, uuid1, uri1, enum1),
      (null.asInstanceOf[String], 2, long1, float1, double1, bool1, date1, uuid1, uri1, enum1),
      ("c", null.asInstanceOf[Int], long1, float1, double1, bool1, date1, uuid1, uri1, enum1),
      ("d", 4, null.asInstanceOf[Long], float1, double1, bool1, date1, uuid1, uri1, enum1),
      ("e", 5, long1, null.asInstanceOf[Float], double1, bool1, date1, uuid1, uri1, enum1),
      ("f", 6, long1, float1, null.asInstanceOf[Double], bool1, date1, uuid1, uri1, enum1),
      ("g", 7, long1, float1, double1, null.asInstanceOf[Boolean], date1, uuid1, uri1, enum1),
      ("h", 8, long1, float1, double1, bool1, null.asInstanceOf[Date], uuid1, uri1, enum1),
      ("i", 9, long1, float1, double1, bool1, date1, null.asInstanceOf[UUID], uri1, enum1),
      ("j", 10, long1, float1, double1, bool1, date1, uuid1, null.asInstanceOf[URI], enum1),
      ("k", 11, long1, float1, double1, bool1, date1, uuid1, uri1, null.asInstanceOf[String])
    ) eids
  }

  class ManySetup extends CoreSetup {

    val List(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11) = Ns
      .int.strs.ints.longs.floats.doubles.bools.dates.uuids.uris.enums insert List(
      (1, Set("a"), Set(1), Set(long1), Set(float1), Set(double1), Set(bool1), Set(date1), Set(uuid1), Set(uri1), Set(enum1)),
      (2, null, Set(2), Set(long1), Set(float1), Set(double1), Set(bool1), Set(date1), Set(uuid1), Set(uri1), Set(enum1)),
      (3, Set("c"), null, Set(long1), Set(float1), Set(double1), Set(bool1), Set(date1), Set(uuid1), Set(uri1), Set(enum1)),
      (4, Set("d"), Set(4), null, Set(float1), Set(double1), Set(bool1), Set(date1), Set(uuid1), Set(uri1), Set(enum1)),
      (5, Set("e"), Set(5), Set(long1), null, Set(double1), Set(bool1), Set(date1), Set(uuid1), Set(uri1), Set(enum1)),
      (6, Set("f"), Set(6), Set(long1), Set(float1), null, Set(bool1), Set(date1), Set(uuid1), Set(uri1), Set(enum1)),
      (7, Set("g"), Set(7), Set(long1), Set(float1), Set(double1), null, Set(date1), Set(uuid1), Set(uri1), Set(enum1)),
      (8, Set("h"), Set(8), Set(long1), Set(float1), Set(double1), Set(bool1), null, Set(uuid1), Set(uri1), Set(enum1)),
      (9, Set("i"), Set(9), Set(long1), Set(float1), Set(double1), Set(bool1), Set(date1), null, Set(uri1), Set(enum1)),
      (10, Set("j"), Set(10), Set(long1), Set(float1), Set(double1), Set(bool1), Set(date1), Set(uuid1), null, Set(enum1)),
      (11, Set("k"), Set(11), Set(long1), Set(float1), Set(double1), Set(bool1), Set(date1), Set(uuid1), Set(uri1), null)
    ) eids
  }

  "`getTyped` - Optional typed card one values from entity id" in new OneSetup {

    import Ns._
    type O[T] = Option[T]

    val test1 : (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e1(str, int, long, float, double, bool, date, uuid, uri, enum)
    val test2 : (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e2(str, int, long, float, double, bool, date, uuid, uri, enum)
    val test3 : (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e3(str, int, long, float, double, bool, date, uuid, uri, enum)
    val test4 : (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e4(str, int, long, float, double, bool, date, uuid, uri, enum)
    val test5 : (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e5(str, int, long, float, double, bool, date, uuid, uri, enum)
    val test6 : (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e6(str, int, long, float, double, bool, date, uuid, uri, enum)
    val test7 : (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e7(str, int, long, float, double, bool, date, uuid, uri, enum)
    val test8 : (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e8(str, int, long, float, double, bool, date, uuid, uri, enum)
    val test9 : (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e9(str, int, long, float, double, bool, date, uuid, uri, enum)
    val test10: (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e10(str, int, long, float, double, bool, date, uuid, uri, enum)
    val test11: (O[String], O[Int], O[Long], O[Float], O[Double], O[Boolean], O[Date], O[UUID], O[URI], O[String]) = e11(str, int, long, float, double, bool, date, uuid, uri, enum)

    // All present
    test1 ===(Some("a"), Some(1), Some(1L), Some(1f), Some(1.0), Some(true), Some(date1), Some(uuid1), Some(uri1), Some(":ns.enum/enum1"))

    // 1 None in each result
    test2 ===(None, Some(2), Some(1L), Some(1f), Some(1.0), Some(true), Some(date1), Some(uuid1), Some(uri1), Some(":ns.enum/enum1"))
    test3 ===(Some("c"), None, Some(1L), Some(1f), Some(1.0), Some(true), Some(date1), Some(uuid1), Some(uri1), Some(":ns.enum/enum1"))
    test4 ===(Some("d"), Some(4), None, Some(1f), Some(1.0), Some(true), Some(date1), Some(uuid1), Some(uri1), Some(":ns.enum/enum1"))
    test5 ===(Some("e"), Some(5), Some(1L), None, Some(1.0), Some(true), Some(date1), Some(uuid1), Some(uri1), Some(":ns.enum/enum1"))
    test6 ===(Some("f"), Some(6), Some(1L), Some(1f), None, Some(true), Some(date1), Some(uuid1), Some(uri1), Some(":ns.enum/enum1"))
    test7 ===(Some("g"), Some(7), Some(1L), Some(1f), Some(1.0), None, Some(date1), Some(uuid1), Some(uri1), Some(":ns.enum/enum1"))
    test8 ===(Some("h"), Some(8), Some(1L), Some(1f), Some(1.0), Some(true), None, Some(uuid1), Some(uri1), Some(":ns.enum/enum1"))
    test9 ===(Some("i"), Some(9), Some(1L), Some(1f), Some(1.0), Some(true), Some(date1), None, Some(uri1), Some(":ns.enum/enum1"))
    test10 ===(Some("j"), Some(10), Some(1L), Some(1f), Some(1.0), Some(true), Some(date1), Some(uuid1), None, Some(":ns.enum/enum1"))
    test11 ===(Some("k"), Some(11), Some(1L), Some(1f), Some(1.0), Some(true), Some(date1), Some(uuid1), Some(uri1), None)


    // entity 2: no str asserted
    // entity 3: no int asserted
    Ns.str.<=("d").int.get.sortBy(_._1) === List(
      ("a", 1),
      ("d", 4))

    Ns.str.int.<=(4).get.sortBy(_._1) === List(
      ("a", 1),
      ("d", 4))
  }


  "`getTyped` - Optional typed card many values from entity id" in new ManySetup {

    import Ns._
    type O[T] = Option[T]

    val test1 : (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e1(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)
    val test2 : (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e2(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)
    val test3 : (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e3(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)
    val test4 : (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e4(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)
    val test5 : (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e5(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)
    val test6 : (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e6(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)
    val test7 : (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e7(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)
    val test8 : (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e8(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)
    val test9 : (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e9(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)
    val test10: (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e10(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)
    val test11: (O[Set[String]], O[Set[Int]], O[Set[Long]], O[Set[Float]], O[Set[Double]], O[Set[Boolean]], O[Set[Date]], O[Set[UUID]], O[Set[URI]], O[Set[String]]) = e11(strs, ints, longs, floats, doubles, bools, dates, uuids, uris, enums)

    // All present
    test1 ===(Some(Set("a")), Some(Set(1)), Some(Set(1L)), Some(Set(1f)), Some(Set(1.0)), Some(Set(true)), Some(Set(date1)), Some(Set(uuid1)), Some(Set(uri1)), Some(Set(":ns.enums/enum1")))

    // 1 None in each result
    test2 ===(None, Some(Set(2)), Some(Set(1L)), Some(Set(1f)), Some(Set(1.0)), Some(Set(true)), Some(Set(date1)), Some(Set(uuid1)), Some(Set(uri1)), Some(Set(":ns.enums/enum1")))
    test3 ===(Some(Set("c")), None, Some(Set(1L)), Some(Set(1f)), Some(Set(1.0)), Some(Set(true)), Some(Set(date1)), Some(Set(uuid1)), Some(Set(uri1)), Some(Set(":ns.enums/enum1")))
    test4 ===(Some(Set("d")), Some(Set(4)), None, Some(Set(1f)), Some(Set(1.0)), Some(Set(true)), Some(Set(date1)), Some(Set(uuid1)), Some(Set(uri1)), Some(Set(":ns.enums/enum1")))
    test5 ===(Some(Set("e")), Some(Set(5)), Some(Set(1L)), None, Some(Set(1.0)), Some(Set(true)), Some(Set(date1)), Some(Set(uuid1)), Some(Set(uri1)), Some(Set(":ns.enums/enum1")))
    test6 ===(Some(Set("f")), Some(Set(6)), Some(Set(1L)), Some(Set(1f)), None, Some(Set(true)), Some(Set(date1)), Some(Set(uuid1)), Some(Set(uri1)), Some(Set(":ns.enums/enum1")))
    test7 ===(Some(Set("g")), Some(Set(7)), Some(Set(1L)), Some(Set(1f)), Some(Set(1.0)), None, Some(Set(date1)), Some(Set(uuid1)), Some(Set(uri1)), Some(Set(":ns.enums/enum1")))
    test8 ===(Some(Set("h")), Some(Set(8)), Some(Set(1L)), Some(Set(1f)), Some(Set(1.0)), Some(Set(true)), None, Some(Set(uuid1)), Some(Set(uri1)), Some(Set(":ns.enums/enum1")))
    test9 ===(Some(Set("i")), Some(Set(9)), Some(Set(1L)), Some(Set(1f)), Some(Set(1.0)), Some(Set(true)), Some(Set(date1)), None, Some(Set(uri1)), Some(Set(":ns.enums/enum1")))
    test10 ===(Some(Set("j")), Some(Set(10)), Some(Set(1L)), Some(Set(1f)), Some(Set(1.0)), Some(Set(true)), Some(Set(date1)), Some(Set(uuid1)), None, Some(Set(":ns.enums/enum1")))
    test11 ===(Some(Set("k")), Some(Set(11)), Some(Set(1L)), Some(Set(1f)), Some(Set(1.0)), Some(Set(true)), Some(Set(date1)), Some(Set(uuid1)), Some(Set(uri1)), None)

    // entity 2: no strs asserted
    // entity 3: no ints asserted
    // (group by card one int)
    Ns.int.<=(4).strs.ints.get.sortBy(_._1) === List(
      (1, Set("a"), Set(1)),
      (4, Set("d"), Set(4)))

    Ns.int.strs.ints.get.filter(_._2.forall(_ <= "d")).sortBy(_._1) === List(
      (1, Set("a"), Set(1)),
      (4, Set("d"), Set(4)))

    Ns.int.strs.ints.get.filter(_._3.forall(_ <= 4)).sortBy(_._1) === List(
      (1, Set("a"), Set(1)),
      (4, Set("d"), Set(4)))
  }


  "`maybe` - Optional additional typed values merged into result tuples" in new OneSetup {

    import Ns._

    // Get optional values
    Ns.int.<=(4).maybe(str) === List(
      (1, Some("a")),
      (2, None),
      (4, Some("d")))

    Ns.int.<=(4).maybe(str, long) === List(
      (1, Some("a"), Some(1L)),
      (2, None, Some(1L)),
      (4, Some("d"), None))

    Ns.int.<=(4).str.maybe(long) === List(
      (1, "a", Some(1)),
      (4, "d", None))
  }
}
