package moleculeTests.tests.core.expression

import molecule.datomic.api.out2._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._


object Comparison extends Base {

  lazy val tests = Tests {

    "Card one" - core { implicit conn =>
      for {
        _ <- oneData

        _ <- Ns.str.<("").a1.get.map(_ ==> List())
        _ <- Ns.str.<(" ").a1.get.map(_ ==> List(""))
        _ <- Ns.str.<(",").a1.get.map(_ ==> List("", " "))
        _ <- Ns.str.<(".").a1.get.map(_ ==> List("", " ", ","))
        _ <- Ns.str.<("?").a1.get.map(_ ==> List("", " ", ",", "."))
        _ <- Ns.str.<("A").a1.get.map(_ ==> List("", " ", ",", ".", "?"))
        _ <- Ns.str.<("B").a1.get.map(_ ==> List("", " ", ",", ".", "?", "A"))
        _ <- Ns.str.<("a").a1.get.map(_ ==> List("", " ", ",", ".", "?", "A", "B"))
        _ <- Ns.str.<("b").a1.get.map(_ ==> List("", " ", ",", ".", "?", "A", "B", "a"))
        _ <- Ns.str.<("d").a1.get.map(_ ==> List("", " ", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.<(str1).a1.get.map(_ ==> List("", " ", ",", ".", "?", "A", "B"))

        _ <- Ns.str.>("").a1.get.map(_ ==> List(" ", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.>(" ").a1.get.map(_ ==> List(",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.>(",").a1.get.map(_ ==> List(".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.>(".").a1.get.map(_ ==> List("?", "A", "B", "a", "b"))
        _ <- Ns.str.>("?").a1.get.map(_ ==> List("A", "B", "a", "b"))
        _ <- Ns.str.>("A").a1.get.map(_ ==> List("B", "a", "b"))
        _ <- Ns.str.>("B").a1.get.map(_ ==> List("a", "b"))
        _ <- Ns.str.>("C").a1.get.map(_ ==> List("a", "b"))
        _ <- Ns.str.>("a").a1.get.map(_ ==> List("b"))
        _ <- Ns.str.>("b").a1.get.map(_ ==> List())
        _ <- Ns.str.>(str1).a1.get.map(_ ==> List("b"))

        _ <- Ns.str.<=("").a1.get.map(_ ==> List(""))
        _ <- Ns.str.<=(" ").a1.get.map(_ ==> List("", " "))
        _ <- Ns.str.<=(",").a1.get.map(_ ==> List("", " ", ","))
        _ <- Ns.str.<=(".").a1.get.map(_ ==> List("", " ", ",", "."))
        _ <- Ns.str.<=("?").a1.get.map(_ ==> List("", " ", ",", ".", "?"))
        _ <- Ns.str.<=("A").a1.get.map(_ ==> List("", " ", ",", ".", "?", "A"))
        _ <- Ns.str.<=("B").a1.get.map(_ ==> List("", " ", ",", ".", "?", "A", "B"))
        _ <- Ns.str.<=("a").a1.get.map(_ ==> List("", " ", ",", ".", "?", "A", "B", "a"))
        _ <- Ns.str.<=("b").a1.get.map(_ ==> List("", " ", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.<=(str1).a1.get.map(_ ==> List("", " ", ",", ".", "?", "A", "B", "a"))

        _ <- Ns.str.>=("").a1.get.map(_ ==> List("", " ", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.>=(" ").a1.get.map(_ ==> List(" ", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.>=(",").a1.get.map(_ ==> List(",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.>=(".").a1.get.map(_ ==> List(".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.>=("?").a1.get.map(_ ==> List("?", "A", "B", "a", "b"))
        _ <- Ns.str.>=("A").a1.get.map(_ ==> List("A", "B", "a", "b"))
        _ <- Ns.str.>=("B").a1.get.map(_ ==> List("B", "a", "b"))
        _ <- Ns.str.>=("a").a1.get.map(_ ==> List("a", "b"))
        _ <- Ns.str.>=("b").a1.get.map(_ ==> List("b"))
        _ <- Ns.str.>=("c").a1.get.map(_ ==> List())
        _ <- Ns.str.>=(str1).a1.get.map(_ ==> List("a", "b"))


        _ <- Ns.int.<(-2).a1.get.map(_ ==> List())
        _ <- Ns.int.<(0).a1.get.map(_ ==> List(-2, -1))
        _ <- Ns.int.<(2).a1.get.map(_ ==> List(-2, -1, 0, 1))
        _ <- Ns.int.<(int1).a1.get.map(_ ==> List(-2, -1, 0))

        _ <- Ns.int.>(2).a1.get.map(_ ==> List())
        _ <- Ns.int.>(0).a1.get.map(_ ==> List(1, 2))
        _ <- Ns.int.>(-2).a1.get.map(_ ==> List(-1, 0, 1, 2))
        _ <- Ns.int.>(int1).a1.get.map(_ ==> List(2))

        _ <- Ns.int.<=(-2).a1.get.map(_ ==> List(-2))
        _ <- Ns.int.<=(0).a1.get.map(_ ==> List(-2, -1, 0))
        _ <- Ns.int.<=(2).a1.get.map(_ ==> List(-2, -1, 0, 1, 2))
        _ <- Ns.int.<=(int1).a1.get.map(_ ==> List(-2, -1, 0, 1))

        _ <- Ns.int.>=(2).a1.get.map(_ ==> List(2))
        _ <- Ns.int.>=(0).a1.get.map(_ ==> List(0, 1, 2))
        _ <- Ns.int.>=(-2).a1.get.map(_ ==> List(-2, -1, 0, 1, 2))
        _ <- Ns.int.>=(int1).a1.get.map(_ ==> List(1, 2))


        _ <- Ns.long.<(-2L).a1.get.map(_ ==> List())
        _ <- Ns.long.<(0L).a1.get.map(_ ==> List(-2L, -1L))
        _ <- Ns.long.<(2L).a1.get.map(_ ==> List(-2L, -1L, 0L, 1L))
        _ <- Ns.long.<(long1).a1.get.map(_ ==> List(-2L, -1L, 0L))

        _ <- Ns.long.>(2L).a1.get.map(_ ==> List())
        _ <- Ns.long.>(0L).a1.get.map(_ ==> List(1L, 2L))
        _ <- Ns.long.>(-2L).a1.get.map(_ ==> List(-1L, 0L, 1L, 2L))
        _ <- Ns.long.>(long1).a1.get.map(_ ==> List(2L))

        _ <- Ns.long.<=(-2L).a1.get.map(_ ==> List(-2L))
        _ <- Ns.long.<=(0L).a1.get.map(_ ==> List(-2L, -1L, 0L))
        _ <- Ns.long.<=(2L).a1.get.map(_ ==> List(-2L, -1L, 0L, 1L, 2L))
        _ <- Ns.long.<=(long1).a1.get.map(_ ==> List(-2L, -1L, 0L, 1L))

        _ <- Ns.long.>=(2L).a1.get.map(_ ==> List(2L))
        _ <- Ns.long.>=(0L).a1.get.map(_ ==> List(0L, 1L, 2L))
        _ <- Ns.long.>=(-2L).a1.get.map(_ ==> List(-2L, -1L, 0L, 1L, 2L))
        _ <- Ns.long.>=(long1).a1.get.map(_ ==> List(1L, 2L))


        _ <- Ns.double.<(-2.0).a1.get.map(_ ==> List())
        _ <- Ns.double.<(0.0).a1.get.map(_ ==> List(-2.0, -1.0))
        _ <- Ns.double.<(2.0).a1.get.map(_ ==> List(-2.0, -1.0, 0.0, 1.0))
        _ <- Ns.double.<(double1).a1.get.map(_ ==> List(-2.0, -1.0, 0.0))

        _ <- Ns.double.>(2.0).a1.get.map(_ ==> List())
        _ <- Ns.double.>(0.0).a1.get.map(_ ==> List(1.0, 2.0))
        _ <- Ns.double.>(-2.0).a1.get.map(_ ==> List(-1.0, 0.0, 1.0, 2.0))
        _ <- Ns.double.>(double1).a1.get.map(_ ==> List(2.0))

        _ <- Ns.double.<=(-2.0).a1.get.map(_ ==> List(-2.0))
        _ <- Ns.double.<=(0.0).a1.get.map(_ ==> List(-2.0, -1.0, 0.0))
        _ <- Ns.double.<=(2.0).a1.get.map(_ ==> List(-2.0, -1.0, 0.0, 1.0, 2.0))
        _ <- Ns.double.<=(double1).a1.get.map(_ ==> List(-2.0, -1.0, 0.0, 1.0))

        _ <- Ns.double.>=(2.0).a1.get.map(_ ==> List(2.0))
        _ <- Ns.double.>=(0.0).a1.get.map(_ ==> List(0.0, 1.0, 2.0))
        _ <- Ns.double.>=(-2.0).a1.get.map(_ ==> List(-2.0, -1.0, 0.0, 1.0, 2.0))
        _ <- Ns.double.>=(double1).a1.get.map(_ ==> List(1.0, 2.0))


        _ <- Ns.bool.<(true).a1.get.map(_ ==> List(false))
        _ <- Ns.bool.<(false).a1.get.map(_ ==> List())
        _ <- Ns.bool.<(bool0).a1.get.map(_ ==> List())

        _ <- Ns.bool.>(true).a1.get.map(_ ==> List())
        _ <- Ns.bool.>(false).a1.get.map(_ ==> List(true))
        _ <- Ns.bool.>(bool0).a1.get.map(_ ==> List(true))

        _ <- Ns.bool.<=(true).a1.get.map(_ ==> List(false, true))
        _ <- Ns.bool.<=(false).a1.get.map(_ ==> List(false))
        _ <- Ns.bool.<=(bool0).a1.get.map(_ ==> List(false))

        _ <- Ns.bool.>=(true).a1.get.map(_ ==> List(true))
        _ <- Ns.bool.>=(false).a1.get.map(_ ==> List(false, true))
        _ <- Ns.bool.>=(bool0).a1.get.map(_ ==> List(false, true))


        _ <- Ns.date.<(date1).a1.get.map(_ ==> List(date0))
        _ <- Ns.date.<(date2).a1.get.map(_ ==> List(date0, date1))

        _ <- Ns.date.>(date1).a1.get.map(_ ==> List(date2))
        _ <- Ns.date.>(date0).a1.get.map(_ ==> List(date1, date2))

        _ <- Ns.date.<=(date1).a1.get.map(_ ==> List(date0, date1))
        _ <- Ns.date.<=(date2).a1.get.map(_ ==> List(date0, date1, date2))

        _ <- Ns.date.>=(date1).a1.get.map(_ ==> List(date1, date2))
        _ <- Ns.date.>=(date0).a1.get.map(_ ==> List(date0, date1, date2))


        // Comparison of random UUIDs omitted...
        // Comparison of URIs not implemented


        _ <- Ns.enumm.<("enum1").a1.get.map(_ ==> List("enum0"))
        _ <- Ns.enumm.>("enum1").a1.get.map(_ ==> List("enum2"))
        _ <- Ns.enumm.<=("enum1").a1.get.map(_ ==> List("enum0", "enum1"))
        _ <- Ns.enumm.>=("enum1").a1.get.map(_ ==> List("enum1", "enum2"))

        _ <- Ns.enumm.<(enum1).a1.get.map(_ ==> List("enum0"))
        _ <- Ns.enumm.>(enum1).a1.get.map(_ ==> List("enum2"))
        _ <- Ns.enumm.<=(enum1).a1.get.map(_ ==> List("enum0", "enum1"))
        _ <- Ns.enumm.>=(enum1).a1.get.map(_ ==> List("enum1", "enum2"))


        _ <- Ns.bigInt.<(bigInt1).a1.get.map(_ ==> List(bigInt0))
        _ <- Ns.bigInt.<(bigInt2).a1.get.map(_ ==> List(bigInt0, bigInt1))

        _ <- Ns.bigInt.>(bigInt1).a1.get.map(_ ==> List(bigInt2))
        _ <- Ns.bigInt.>(bigInt0).a1.get.map(_ ==> List(bigInt1, bigInt2))

        _ <- Ns.bigInt.<=(bigInt1).a1.get.map(_ ==> List(bigInt0, bigInt1))
        _ <- Ns.bigInt.<=(bigInt2).a1.get.map(_ ==> List(bigInt0, bigInt1, bigInt2))

        _ <- Ns.bigInt.>=(bigInt1).a1.get.map(_ ==> List(bigInt1, bigInt2))
        _ <- Ns.bigInt.>=(bigInt0).a1.get.map(_ ==> List(bigInt0, bigInt1, bigInt2))


        _ <- Ns.bigDec.<(bigDec1).a1.get.map(_ ==> List(bigDec0))
        _ <- Ns.bigDec.<(bigDec2).a1.get.map(_ ==> List(bigDec0, bigDec1))

        _ <- Ns.bigDec.>(bigDec1).a1.get.map(_ ==> List(bigDec2))
        _ <- Ns.bigDec.>(bigDec0).a1.get.map(_ ==> List(bigDec1, bigDec2))

        _ <- Ns.bigDec.<=(bigDec1).a1.get.map(_ ==> List(bigDec0, bigDec1))
        _ <- Ns.bigDec.<=(bigDec2).a1.get.map(_ ==> List(bigDec0, bigDec1, bigDec2))

        _ <- Ns.bigDec.>=(bigDec1).a1.get.map(_ ==> List(bigDec1, bigDec2))
        _ <- Ns.bigDec.>=(bigDec0).a1.get.map(_ ==> List(bigDec0, bigDec1, bigDec2))
      } yield ()
    }


    "Quoting" - core { implicit conn =>
      for {
        _ <- Ns.int(1).str("""Hi "Ann"""").save

        _ <- Ns.str.>(""""H"""").get.map(_ ==> List("""Hi "Ann""""))
        _ <- Ns.str.>=(""""H"""").get.map(_ ==> List("""Hi "Ann""""))
        _ <- Ns.str.<=(""""H"""").get.map(_ ==> Nil)
        _ <- Ns.str.<(""""H"""").get.map(_ ==> Nil)

        letter = "H"
        _ <- Ns.str.>(letter).get.map(_ ==> List("""Hi "Ann""""))
        _ <- Ns.str.>=(letter).get.map(_ ==> List("""Hi "Ann""""))
        _ <- Ns.str.<=(letter).get.map(_ ==> Nil)
        _ <- Ns.str.<(letter).get.map(_ ==> Nil)

        _ <- Ns.int.str_.>(""""H"""").get.map(_ ==> List(1))
        _ <- Ns.int.str_.>=(""""H"""").get.map(_ ==> List(1))
        _ <- Ns.int.str_.<=(""""H"""").get.map(_ ==> Nil)
        _ <- Ns.int.str_.<(""""H"""").get.map(_ ==> Nil)

        _ <- Ns.int.str_.>(letter).get.map(_ ==> List(1))
        _ <- Ns.int.str_.>=(letter).get.map(_ ==> List(1))
        _ <- Ns.int.str_.<=(letter).get.map(_ ==> Nil)
        _ <- Ns.int.str_.<(letter).get.map(_ ==> Nil)
      } yield ()
    }


    "Range" - core { implicit conn =>
      for {
        _ <- oneData

        _ <- Ns.int.insert(1, 2, 3, 4)

        _ <- Ns.int_.>(1).int.<(4).get.map(_ ==> List(2, 3))
        _ <- Ns.int_.>(1).int.<=(4).get.map(_ ==> List(2, 3, 4))
        _ <- Ns.int_.>=(1).int.<(4).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int_.>=(1).int.<=(4).get.map(_ ==> List(1, 2, 3, 4))


        _ <- Ns.int_.<(4).int.>(1).get.map(_ ==> List(2, 3))
        _ <- Ns.int_.<=(4).int.>(1).get.map(_ ==> List(2, 3, 4))
        _ <- Ns.int_.<(4).int.>=(1).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int_.<=(4).int.>=(1).get.map(_ ==> List(1, 2, 3, 4))
      } yield ()
    }


    "Card many" - core { implicit conn =>
      for {
        _ <- manyData

        // String

        // Note how 'ba' is "greater than" 'b'
        // (See java.util.String.compareTo)
        _ <- Ns.str.strs.>("b").get.map(_ ==> List(
          ("str2", Set("c")),
          ("str3", Set("d", "ba"))
        ))
        _ <- Ns.str.strs.>=("b").get.map(_ ==> List(
          ("str1", Set("b")),
          ("str2", Set("b", "c")),
          ("str3", Set("d", "ba"))
        ))
        _ <- Ns.str.strs.<=("b").get.map(_ ==> List(
          ("str1", Set("a", "b")),
          ("str2", Set("b"))
        ))
        _ <- Ns.str.strs.<("b").get.map(_ ==> List(
          ("str1", Set("a"))
        ))
        _ <- Ns.str.a1.strs_.>("b").get.map(_ ==> List("str2", "str3"))
        _ <- Ns.str.a1.strs_.>=("b").get.map(_ ==> List("str1", "str2", "str3"))
        _ <- Ns.str.a1.strs_.<=("b").get.map(_ ==> List("str1", "str2"))
        _ <- Ns.str.a1.strs_.<("b").get.map(_ ==> List("str1"))


        // Int

        _ <- Ns.int.ints.>(2).get.map(_ ==> List(
          (2, Set(3)),
          (3, Set(4))
        ))
        _ <- Ns.int.ints.>=(2).get.map(_ ==> List(
          (1, Set(2)),
          (2, Set(2, 3)),
          (3, Set(4, 2))
        ))
        _ <- Ns.int.ints.<=(2).get.map(_ ==> List(
          (1, Set(1, 2)),
          (2, Set(2)),
          (3, Set(2))
        ))
        _ <- Ns.int.ints.<(2).get.map(_ ==> List(
          (1, Set(1))
        ))
        _ <- Ns.int.a1.ints_.>(2).get.map(_ ==> List(2, 3))
        _ <- Ns.int.a1.ints_.>=(2).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.a1.ints_.<=(2).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.a1.ints_.<(2).get.map(_ ==> List(1))


        // Date

        _ <- Ns.date.dates.>(date2).get.map(_ ==> List(
          (date2, Set(date3)),
          (date3, Set(date4))
        ))
        _ <- Ns.date.dates.>=(date2).get.map(_ ==> List(
          (date1, Set(date2)),
          (date2, Set(date2, date3)),
          (date3, Set(date4, date2))
        ))
        _ <- Ns.date.dates.<=(date2).get.map(_ ==> List(
          (date1, Set(date1, date2)),
          (date2, Set(date2)),
          (date3, Set(date2))
        ))
        _ <- Ns.date.dates.<(date2).get.map(_ ==> List(
          (date1, Set(date1))
        ))
        _ <- Ns.date.a1.dates_.>(date2).get.map(_ ==> List(date2, date3))
        _ <- Ns.date.a1.dates_.>=(date2).get.map(_ ==> List(date1, date2, date3))
        _ <- Ns.date.a1.dates_.<=(date2).get.map(_ ==> List(date1, date2, date3))
        _ <- Ns.date.a1.dates_.<(date2).get.map(_ ==> List(date1))


        // UUID (comparisons not of much relevance - only works here because we sorted the values)

        _ <- Ns.uuid.uuids.>(uuid2).get.map(_ ==> List(
          (uuid2, Set(uuid3)),
          (uuid3, Set(uuid4))
        ))
        _ <- Ns.uuid.uuids.>=(uuid2).get.map(_ ==> List(
          (uuid1, Set(uuid2)),
          (uuid2, Set(uuid2, uuid3)),
          (uuid3, Set(uuid4, uuid2))
        ))
        _ <- Ns.uuid.uuids.<=(uuid2).get.map(_ ==> List(
          (uuid1, Set(uuid1, uuid2)),
          (uuid2, Set(uuid2)),
          (uuid3, Set(uuid2))
        ))
        _ <- Ns.uuid.uuids.<(uuid2).get.map(_ ==> List(
          (uuid1, Set(uuid1))
        ))
        _ <- Ns.uuid.a1.uuids_.>(uuid2).get.map(_ ==> List(uuid2, uuid3))
        _ <- Ns.uuid.a1.uuids_.>=(uuid2).get.map(_ ==> List(uuid1, uuid2, uuid3))
        _ <- Ns.uuid.a1.uuids_.<=(uuid2).get.map(_ ==> List(uuid1, uuid2, uuid3))
        _ <- Ns.uuid.a1.uuids_.<(uuid2).get.map(_ ==> List(uuid1))


        _ <- Ns.bigInt.bigInts.>(bigInt2).get.map(_ ==> List(
          (bigInt2, Set(bigInt3)),
          (bigInt3, Set(bigInt4))
        ))
        _ <- Ns.bigInt.bigInts.>=(bigInt2).get.map(_ ==> List(
          (bigInt1, Set(bigInt2)),
          (bigInt2, Set(bigInt2, bigInt3)),
          (bigInt3, Set(bigInt4, bigInt2))
        ))
        _ <- Ns.bigInt.bigInts.<=(bigInt2).get.map(_ ==> List(
          (bigInt1, Set(bigInt1, bigInt2)),
          (bigInt2, Set(bigInt2)),
          (bigInt3, Set(bigInt2))
        ))
        _ <- Ns.bigInt.bigInts.<(bigInt2).get.map(_ ==> List(
          (bigInt1, Set(bigInt1))
        ))
        _ <- Ns.bigInt.a1.bigInts_.>(bigInt2).get.map(_ ==> List(bigInt2, bigInt3))
        _ <- Ns.bigInt.a1.bigInts_.>=(bigInt2).get.map(_ ==> List(bigInt1, bigInt2, bigInt3))
        _ <- Ns.bigInt.a1.bigInts_.<=(bigInt2).get.map(_ ==> List(bigInt1, bigInt2, bigInt3))
        _ <- Ns.bigInt.a1.bigInts_.<(bigInt2).get.map(_ ==> List(bigInt1))


        _ <- Ns.bigDec.bigDecs.>(bigDec2).get.map(_ ==> List(
          (bigDec2, Set(bigDec3)),
          (bigDec3, Set(bigDec4))
        ))
        _ <- Ns.bigDec.bigDecs.>=(bigDec2).get.map(_ ==> List(
          (bigDec1, Set(bigDec2)),
          (bigDec2, Set(bigDec2, bigDec3)),
          (bigDec3, Set(bigDec4, bigDec2))
        ))
        _ <- Ns.bigDec.bigDecs.<=(bigDec2).get.map(_ ==> List(
          (bigDec1, Set(bigDec1, bigDec2)),
          (bigDec2, Set(bigDec2)),
          (bigDec3, Set(bigDec2))
        ))
        _ <- Ns.bigDec.bigDecs.<(bigDec2).get.map(_ ==> List(
          (bigDec1, Set(bigDec1))
        ))
        _ <- Ns.bigDec.a1.bigDecs_.>(bigDec2).get.map(_ ==> List(bigDec2, bigDec3))
        _ <- Ns.bigDec.a1.bigDecs_.>=(bigDec2).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))
        _ <- Ns.bigDec.a1.bigDecs_.<=(bigDec2).get.map(_ ==> List(bigDec1, bigDec2, bigDec3))
        _ <- Ns.bigDec.a1.bigDecs_.<(bigDec2).get.map(_ ==> List(bigDec1))
      } yield ()
    }
  }
}