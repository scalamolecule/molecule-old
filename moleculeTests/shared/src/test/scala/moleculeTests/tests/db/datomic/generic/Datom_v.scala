package moleculeTests.tests.db.datomic.generic

import molecule.datomic.api.out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._

/** Generic Datom value `v` resolution
  *
  */
object Datom_v extends AsyncTestSuite {

  val double11 = 1.1
  val bigDec11 = BigDecimal("1.1")


  lazy val tests = Tests {

    "Card 1" - core { implicit conn =>
      for {
        e1 <- Ns.str.int.long.double.bool.date.uri.uuid.bigInt.bigDec.ref1.insert(
          str1, int1, long1, double1, bool1, date1, uri1, uuid1, bigInt1, bigDec1, r1
        ).map(_.eid)

        e2 <- Ns.double.bigDec.insert(double11, bigDec11).map(_.eid)

        _ <- Ns(e1).a.a1.v.get.map(_ ==> List(
          (":Ns/bigDec", bigDec1),
          (":Ns/bigInt", bigInt1),
          (":Ns/bool", bool1),
          (":Ns/date", date1),
          (":Ns/double", double1),
          (":Ns/int", int1),
          (":Ns/long", long1),
          (":Ns/ref1", r1),
          (":Ns/str", str1),
          (":Ns/uri", uri1),
          (":Ns/uuid", uuid1),

          // Generic enum value would be the internal entity id of the enum ident which we don't have much use for
          // (":Ns/enum", 17592186045418L),
        ))

        _ <- Ns(e2).a.a1.v.get.map(_ ==> List(
          (":Ns/bigDec", bigDec11),
          (":Ns/double", double11),
        ))


        _ <- if (isJsPlatform) {

          // JS .....................

          val wholeNumberAndDoubles = List(
            (":Ns/bigInt", 1),
            (":Ns/long", 1),
            (":Ns/int", 1),
            (":Ns/double", 1)
          )

          for {
            // Int(1) and Long(1) also checks Double(1.0)
            _ <- Ns(e1).a.v(1).get.map(_ ==> wholeNumberAndDoubles)
            _ <- Ns(e1).a.v(int1).get.map(_ ==> wholeNumberAndDoubles)
            _ <- Ns(e1).a.v(1L).get.map(_ ==> wholeNumberAndDoubles)
            _ <- Ns(e1).a.v(long1).get.map(_ ==> wholeNumberAndDoubles)

            // BigInt(1) also checks BigDecimal(1.0) but not Double(1.0)
            _ <- Ns(e1).a.v(bigInt1).get.map(_ ==> List(
              (":Ns/bigInt", 1),
              (":Ns/long", 1),
              (":Ns/bigDec", 1.0),
              (":Ns/int", 1)
            ))

            // Single Long value in this case
            _ <- Ns(e1).a.v(r1).get.map(_ ==> List((":Ns/ref1", r1)))

            // Double, whole number matches other whole number types plus Double(1.0)
            _ <- Ns(e1).a.v(1.0).get.map(_ ==> wholeNumberAndDoubles)
            _ <- Ns(e1).a.v(double1).get.map(_ ==> wholeNumberAndDoubles)

            // Double with decimal
            _ <- Ns(e2).a.v(1.1).get.map(_ ==> List((":Ns/double", 1.1)))
            x <- Ns(e2).a.v(double11).get.map(_ ==> List((":Ns/double", 1.1)))
          } yield x

        } else {

          // JVM .....................

          val wholeNumbers = List(
            (":Ns/bigInt", 1),
            (":Ns/long", 1),
            (":Ns/int", 1)
          )

          for {
            // Int/Long conflated
            _ <- Ns(e1).a.v(1).get.map(_ ==> wholeNumbers)
            _ <- Ns(e1).a.v(int1).get.map(_ ==> wholeNumbers)

            _ <- Ns(e1).a.v(1L).get.map(_ ==> wholeNumbers)
            _ <- Ns(e1).a.v(long1).get.map(_ ==> wholeNumbers)
            _ <- Ns(e1).a.v(r1).get.map(_ ==> List((":Ns/ref1", r1)))

            // BigInt
            _ <- Ns(e1).a.v(bigInt1).get.map(_ ==> wholeNumbers)

            // Double
            _ <- Ns(e1).a.v(1.0).get.map(_ ==> List((":Ns/double", 1.0)))
            _ <- Ns(e1).a.v(double1).get.map(_ ==> List((":Ns/double", 1.0)))

            _ <- Ns(e2).a.v(1.1).get.map(_ ==> List((":Ns/double", 1.1)))
            x <- Ns(e2).a.v(double11).get.map(_ ==> List((":Ns/double", 1.1)))
          } yield x
        }

        // Shared ..........................

        // BigDecimal behaves equally on both platforms
        _ <- Ns(e1).a.v(bigDec1).get.map(_ ==> List((":Ns/bigDec", bigDec1)))
        _ <- Ns(e2).a.v(bigDec11).get.map(_ ==> List((":Ns/bigDec", bigDec11)))

        _ <- Ns(e1).a.v("a").get.map(_ ==> List((":Ns/str", str1)))
        _ <- Ns(e1).a.v(str1).get.map(_ ==> List((":Ns/str", str1)))

        _ <- Ns(e1).a.v(true).get.map(_ ==> List((":Ns/bool", bool1)))
        _ <- Ns(e1).a.v(bool1).get.map(_ ==> List((":Ns/bool", bool1)))

        _ <- Ns(e1).a.v(date1).get.map(_ ==> List((":Ns/date", date1)))

        _ <- Ns(e1).a.v(uri1).get.map(_ ==> List((":Ns/uri", uri1)))

        _ <- Ns(e1).a.v(uuid1).get.map(_ ==> List((":Ns/uuid", uuid1)))
      } yield ()
    }


    "Card 2" - core { implicit conn =>
      for {
        e <- Ns.strs.ints.longs.doubles.bools.dates.uris.uuids.bigInts.bigDecs.refs1.insert(
          strs2, ints2, longs2, Set(double1, double11), bools2, dates2, uris2, uuids2,
          bigInts2, Set(bigDec1, bigDec11), rs2
        ).map(_.eid)

        _ <- Ns(e).a.v.get.map(_.sortBy(res => (res._1, res._2.toString)) ==> List(
          (":Ns/bigDecs", bigDec1),
          (":Ns/bigDecs", bigDec11),
          (":Ns/bigInts", bigInt1),
          (":Ns/bigInts", bigInt2),
          (":Ns/bools", bool2),
          (":Ns/bools", bool1),
          (":Ns/dates", date1),
          (":Ns/dates", date2),
          (":Ns/doubles", double1),
          (":Ns/doubles", double11),
          (":Ns/ints", int1),
          (":Ns/ints", int2),
          (":Ns/longs", long1),
          (":Ns/longs", long2),
          (":Ns/refs1", r1),
          (":Ns/refs1", r2),
          (":Ns/strs", str1),
          (":Ns/strs", str2),
          (":Ns/uris", uri1),
          (":Ns/uris", uri2),
          (":Ns/uuids", uuid1),
          (":Ns/uuids", uuid2),
        ))


        _ <- if (isJsPlatform) {

          // JS .....................

          val wholeNumberAndDoubles = List(
            (":Ns/ints", 1),
            (":Ns/bigInts", 1),
            (":Ns/doubles", 1),
            (":Ns/longs", 1),
          )

          for {
            // Int/Long/BigInt
            _ <- Ns(e).a.v(1).get.map(_ ==> wholeNumberAndDoubles)
            _ <- Ns(e).a.v(int1).get.map(_ ==> wholeNumberAndDoubles)

            _ <- Ns(e).a.v(1L).get.map(_ ==> wholeNumberAndDoubles)
            _ <- Ns(e).a.v(long1).get.map(_ ==> wholeNumberAndDoubles)
            _ <- Ns(e).a.v(r1).get.map(_ ==> List((":Ns/refs1", r1)))

            _ <- Ns(e).a.v(bigInt1).get.map(_ ==> List(
              (":Ns/ints", 1),
              (":Ns/bigInts", 1),
              (":Ns/bigDecs", 1.0),
              (":Ns/longs", 1),
            ))

            // Double, whole number matches other whole number types plus Double(1.0)
            _ <- Ns(e).a.v(1.0).get.map(_ ==> wholeNumberAndDoubles)
            _ <- Ns(e).a.v(double1).get.map(_ ==> wholeNumberAndDoubles)

            // Double with decimal
            _ <- Ns(e).a.v(1.1).get.map(_ ==> List((":Ns/doubles", 1.1)))
            x <- Ns(e).a.v(double11).get.map(_ ==> List((":Ns/doubles", 1.1)))
          } yield x

        } else {

          // JVM .....................

          val wholeNumbers = List(
            (":Ns/ints", 1),
            (":Ns/bigInts", 1),
            (":Ns/longs", 1),
          )

          for {
            // Int/Long
            _ <- Ns(e).a.v(1).get.map(_ ==> wholeNumbers)
            _ <- Ns(e).a.v(int1).get.map(_ ==> wholeNumbers)

            _ <- Ns(e).a.v(1L).get.map(_ ==> wholeNumbers)
            _ <- Ns(e).a.v(long1).get.map(_ ==> wholeNumbers)
            _ <- Ns(e).a.v(r1).get.map(_ ==> List((":Ns/refs1", r1)))

            _ <- Ns(e).a.v(bigInt1).get.map(_ ==> wholeNumbers)

            // Double
            _ <- Ns(e).a.v(1.0).get.map(_ ==> List((":Ns/doubles", 1.0)))
            _ <- Ns(e).a.v(double1).get.map(_ ==> List((":Ns/doubles", 1.0)))

            _ <- Ns(e).a.v(1.1).get.map(_ ==> List((":Ns/doubles", 1.1)))
            x <- Ns(e).a.v(double11).get.map(_ ==> List((":Ns/doubles", 1.1)))
          } yield x
        }

        // Shared ..........................

        // BigDecimal behaves equally on both platforms
        _ <- Ns(e).a.v(bigDec1).get.map(_ ==> List((":Ns/bigDecs", bigDec1)))
        _ <- Ns(e).a.v(bigDec11).get.map(_ ==> List((":Ns/bigDecs", double11)))

        _ <- Ns(e).a.v("a").get.map(_ ==> List((":Ns/strs", str1)))
        _ <- Ns(e).a.v(str1).get.map(_ ==> List((":Ns/strs", str1)))

        _ <- Ns(e).a.v(true).get.map(_ ==> List((":Ns/bools", bool1)))
        _ <- Ns(e).a.v(bool1).get.map(_ ==> List((":Ns/bools", bool1)))

        _ <- Ns(e).a.v(date1).get.map(_ ==> List((":Ns/dates", date1)))

        _ <- Ns(e).a.v(uri1).get.map(_ ==> List((":Ns/uris", uri1)))

        _ <- Ns(e).a.v(uuid1).get.map(_ ==> List((":Ns/uuids", uuid1)))
      } yield ()
    }


    "Card 3" - core { implicit conn =>
      for {
        e <- Ns.strMap.intMap.longMap.doubleMap.boolMap.dateMap.uriMap.uuidMap.bigIntMap.bigDecMap.insert(
          Map("a" -> str1, "b" -> str2),
          Map("a" -> int1, "b" -> int2),
          Map("a" -> long1, "b" -> long2),
          Map("a" -> double1, "b" -> double11),
          Map("a" -> bool1, "b" -> bool2),
          Map("a" -> date1, "b" -> date2),
          Map("a" -> uri1, "b" -> uri2),
          Map("a" -> uuid1, "b" -> uuid2),
          Map("a" -> bigInt1, "b" -> bigInt2),
          Map("a" -> bigDec1, "b" -> bigDec11),
        ).map(_.eid)

        _ <- Ns(e).a.v.get.map(_.sortBy(res => (res._1, res._2.toString)) ==> List(
          (":Ns/bigDecMap", "a@1.0"),
          (":Ns/bigDecMap", "b@1.1"),
          (":Ns/bigIntMap", "a@1"),
          (":Ns/bigIntMap", "b@2"),
          (":Ns/boolMap", "a@true"),
          (":Ns/boolMap", "b@false"),
          (":Ns/dateMap", "a@2001-07-01"),
          (":Ns/dateMap", "b@2002-01-01"),
          (":Ns/doubleMap", "a@1.0"),
          (":Ns/doubleMap", "b@1.1"),
          (":Ns/intMap", "a@1"),
          (":Ns/intMap", "b@2"),
          (":Ns/longMap", "a@1"),
          (":Ns/longMap", "b@2"),
          (":Ns/strMap", "a@a"),
          (":Ns/strMap", "b@b"),
          (":Ns/uriMap", "a@uri1"),
          (":Ns/uriMap", "b@uri2"),
          (":Ns/uuidMap", s"a@$uuid1"),
          (":Ns/uuidMap", s"b@$uuid2"),
        ))

        // Int/Long/BigInt
        _ <- Ns(e).a.a1.v("a@1").get.map(_ ==> List(
          (":Ns/bigIntMap", "a@1"),
          (":Ns/intMap", "a@1"),
          (":Ns/longMap", "a@1")
        ))
        _ <- Ns(e).a.a1.v("b@2").get.map(_ ==> List(
          (":Ns/bigIntMap", "b@2"),
          (":Ns/intMap", "b@2"),
          (":Ns/longMap", "b@2")
        ))

        // Double/BigDecimal
        _ <- Ns(e).a.a1.v("a@1.0").get.map(_ ==> List(
          (":Ns/bigDecMap", "a@1.0"),
          (":Ns/doubleMap", "a@1.0")
        ))
        _ <- Ns(e).a.a1.v("b@1.1").get.map(_ ==> List(
          (":Ns/bigDecMap", "b@1.1"),
          (":Ns/doubleMap", "b@1.1")
        ))

        _ <- Ns(e).a.v("a@a").get.map(_ ==> List((":Ns/strMap", "a@a")))
        _ <- Ns(e).a.v("a@true").get.map(_ ==> List((":Ns/boolMap", "a@true")))
        _ <- Ns(e).a.v("a@2001-07-01").get.map(_ ==> List((":Ns/dateMap", "a@2001-07-01")))
        _ <- Ns(e).a.v("a@uri1").get.map(_ ==> List((":Ns/uriMap", "a@uri1")))

        // Applying a string interpolation is not allowed since it would need to be lifted by the macro and
        // we can't prepare for any type of expression.
        // Can't lift unexpected code:
        // [error] code : ("a@".+(Adhoc.this.uuid1): String)
        // _ <- Ns(e).a.v(s"a@$uuid1").get.map(_ ==> List((":Ns/uuidMap", uuid1)))

        // Solution is to assign the interpolated string to a variable and apply that:
        uuid1pair = s"a@$uuid1"
        _ <- Ns(e).a.v(uuid1pair).get.map(_ ==> List((":Ns/uuidMap", uuid1pair)))
      } yield ()
    }
  }
}