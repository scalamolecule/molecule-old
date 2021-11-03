package moleculeTests.tests.core.expression

import java.net.URI
import molecule.datomic.api.out2._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Negation extends Base {

  lazy val tests = Tests {

    "Card one" - core { implicit conn =>
      for {
        _ <- oneData

        _ <- Ns.str.not("").get.map(_.sorted ==> List(" ", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.not(" ").get.map(_.sorted ==> List("", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.not(",").get.map(_.sorted ==> List("", " ", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.not(".").get.map(_.sorted ==> List("", " ", ",", "?", "A", "B", "a", "b"))
        _ <- Ns.str.not("?").get.map(_.sorted ==> List("", " ", ",", ".", "A", "B", "a", "b"))
        _ <- Ns.str.not("A").get.map(_.sorted ==> List("", " ", ",", ".", "?", "B", "a", "b"))
        _ <- Ns.str.not("B").get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "a", "b"))
        _ <- Ns.str.not("a").get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "B", "b"))
        _ <- Ns.str.not("b").get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "B", "a"))
        _ <- Ns.str.not("C").get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.not("c").get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "B", "a", "b"))

        // Same as
        // Intellij shows error although it is fine
        _ <- Ns.str.!=("").get.map(_.sorted ==> List(" ", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.!=(" ").get.map(_.sorted ==> List("", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.!=(",").get.map(_.sorted ==> List("", " ", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.!=(".").get.map(_.sorted ==> List("", " ", ",", "?", "A", "B", "a", "b"))
        _ <- Ns.str.!=("?").get.map(_.sorted ==> List("", " ", ",", ".", "A", "B", "a", "b"))
        _ <- Ns.str.!=("A").get.map(_.sorted ==> List("", " ", ",", ".", "?", "B", "a", "b"))
        _ <- Ns.str.!=("B").get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "a", "b"))
        _ <- Ns.str.!=("a").get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "B", "b"))
        _ <- Ns.str.!=("b").get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "B", "a"))
        _ <- Ns.str.!=("C").get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.!=("c").get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "B", "a", "b"))

        _ <- Ns.str.not(str1).get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "B", "b"))
        _ <- Ns.str.!=(str1).get.map(_.sorted ==> List("", " ", ",", ".", "?", "A", "B", "b"))

        // Negate multiple values ("NOR"-logic: not 'a NOR 'b NOR 'c...)
        _ <- Ns.str.not("", " ").get.map(_.sorted ==> List(",", ".", "?", "A", "B", "a", "b"))
        _ <- Ns.str.not("", " ", ",", ".", "?").get.map(_.sorted ==> List("A", "B", "a", "b"))
        _ <- Ns.str.not("", " ", ",", ".", "?", "A", "B").get.map(_.sorted ==> List("a", "b"))
        _ <- Ns.str.not("", " ", ",", ".", "?", "A", "B", "a", "b").get.map(_.sorted ==> List())


        _ <- Ns.int.not(7).get.map(_.sorted ==> List(-2, -1, 0, 1, 2))
        _ <- Ns.int.not(1).get.map(_.sorted ==> List(-2, -1, 0, 2))
        _ <- Ns.int.not(-1, 0, 1).get.map(_.sorted ==> List(-2, 2))
        _ <- Ns.int.not(Seq(-1, 0, 1)).get.map(_.sorted ==> List(-2, 2))
        _ <- Ns.int.not(int1).get.map(_.sorted ==> List(-2, -1, 0, 2))
        _ <- Ns.int.not(int1, int2).get.map(_.sorted ==> List(-2, -1, 0))
        _ <- Ns.int.not(Seq(int1, int2)).get.map(_.sorted ==> List(-2, -1, 0))
        ints = Seq(int1, int2)
        _ <- Ns.int.not(ints).get.map(_.sorted ==> List(-2, -1, 0))

        // Same as
        // Intellij shows error although it is fine
        _ <- Ns.int.!=(7).get.map(_.sorted ==> List(-2, -1, 0, 1, 2))
        _ <- Ns.int.!=(1).get.map(_.sorted ==> List(-2, -1, 0, 2))
        _ <- Ns.int.!=(-1, 0, 1).get.map(_.sorted ==> List(-2, 2))
        _ <- Ns.int.!=(Seq(-1, 0, 1)).get.map(_.sorted ==> List(-2, 2))
        _ <- Ns.int.!=(int1).get.map(_.sorted ==> List(-2, -1, 0, 2))
        _ <- Ns.int.!=(int1, int2).get.map(_.sorted ==> List(-2, -1, 0))
        _ <- Ns.int.!=(Seq(int1, int2)).get.map(_.sorted ==> List(-2, -1, 0))
        _ <- Ns.int.!=(ints).get.map(_.sorted ==> List(-2, -1, 0))


        _ <- Ns.long.not(7L).get.map(_.sorted ==> List(-2L, -1L, 0L, 1L, 2L))
        _ <- Ns.long.not(1L).get.map(_.sorted ==> List(-2L, -1L, 0L, 2L))
        _ <- Ns.long.not(-1L, 0L, 1L).get.map(_.sorted ==> List(-2L, 2L))
        _ <- Ns.long.not(Seq(-1L, 0L, 1L)).get.map(_.sorted ==> List(-2L, 2L))
        _ <- Ns.long.not(long1).get.map(_.sorted ==> List(-2L, -1L, 0L, 2L))
        _ <- Ns.long.not(long1, long1).get.map(_.sorted ==> List(-2L, -1L, 0L, 2L))
        _ <- Ns.long.not(Seq(long1, long2)).get.map(_.sorted ==> List(-2L, -1L, 0L))
        longs = Seq(long1, long2)
        _ <- Ns.long.not(longs).get.map(_.sorted ==> List(-2L, -1L, 0L))


        _ <- Ns.double.not(7.0).get.map(_.sorted ==> List(-2.0, -1.0, 0.0, 1.0, 2.0))
        _ <- Ns.double.not(1.0).get.map(_.sorted ==> List(-2.0, -1.0, 0.0, 2.0))
        _ <- Ns.double.not(-1.0, 0.0, 1.0).get.map(_.sorted ==> List(-2.0, 2.0))
        _ <- Ns.double.not(Seq(-1.0, 0.0, 1.0)).get.map(_.sorted ==> List(-2.0, 2.0))
        _ <- Ns.double.not(double1).get.map(_.sorted ==> List(-2.0, -1.0, 0.0, 2.0))
        _ <- Ns.double.not(double1, double2).get.map(_.sorted ==> List(-2.0, -1.0, 0.0))
        _ <- Ns.double.not(Seq(double1, double2)).get.map(_.sorted ==> List(-2.0, -1.0, 0.0))
        doubles = Seq(double1, double2)
        _ <- Ns.double.not(doubles).get.map(_.sorted ==> List(-2.0, -1.0, 0.0))


        _ <- Ns.bool.not(true).get.map(_ ==> List(false))
        _ <- Ns.bool.not(false).get.map(_ ==> List(true))
        // Multiple boolean values not so useful...
        _ <- Ns.bool.not(false, true).get.map(_ ==> List())


        _ <- Ns.date.not(date3).get.map(_.sorted ==> List(date0, date1, date2))
        _ <- Ns.date.not(date0).get.map(_.sorted ==> List(date1, date2))
        _ <- Ns.date.not(date0, date1).get.map(_.sorted ==> List(date2))
        _ <- Ns.date.not(Seq(date0, date1)).get.map(_.sorted ==> List(date2))
        dates = Seq(date0, date1)
        _ <- Ns.date.not(dates).get.map(_.sorted ==> List(date2))


        _ <- Ns.uuid.not(uuid3).get.map(_.sortBy(_.toString) ==> List(uuid0, uuid1, uuid2))
        _ <- Ns.uuid.not(uuid0).get.map(_.sortBy(_.toString) ==> List(uuid1, uuid2))
        _ <- Ns.uuid.not(uuid0, uuid1).get.map(_.sortBy(_.toString) ==> List(uuid2))
        _ <- Ns.uuid.not(Seq(uuid0, uuid1)).get.map(_.sortBy(_.toString) ==> List(uuid2))
        uuids = Seq(uuid0, uuid1)
        _ <- Ns.uuid.not(uuids).get.map(_.sortBy(_.toString) ==> List(uuid2))


        _ <- Ns.uri.not(uri3).get.map(_.sortBy(_.toString) ==> List(uri0, uri1, uri2))
        _ <- Ns.uri.not(uri0).get.map(_.sortBy(_.toString) ==> List(uri1, uri2))
        _ <- Ns.uri.not(uri0, uri1).get.map(_.sortBy(_.toString) ==> List(uri2))
        _ <- Ns.uri.not(Seq(uri0, uri1)).get.map(_.sortBy(_.toString) ==> List(uri2))
        uris = Seq(uri0, uri1)
        _ <- Ns.uri.not(uris).get.map(_.sortBy(_.toString) ==> List(uri2))


        _ <- Ns.enumm.not("enum0").get.map(_.sorted ==> List(enum1, enum2))
        _ <- Ns.enumm.not("enum0", "enum1").get.map(_.sorted ==> List(enum2))
        _ <- Ns.enumm.not(Seq("enum0", "enum1")).get.map(_.sorted ==> List(enum2))
        _ <- Ns.enumm.not(enum0).get.map(_.sorted ==> List(enum1, enum2))
        _ <- Ns.enumm.not(enum0, enum1).get.map(_.sorted ==> List(enum2))
        _ <- Ns.enumm.not(Seq(enum0, enum1)).get.map(_.sorted ==> List(enum2))
        enums = Seq(enum0, enum1)
        _ <- Ns.enumm.not(enums).get.map(_.sorted ==> List(enum2))


        _ <- Ns.bigInt.not(bigInt3).get.map(_.sortBy(_.toString) ==> List(bigInt0, bigInt1, bigInt2))
        _ <- Ns.bigInt.not(bigInt0).get.map(_.sortBy(_.toString) ==> List(bigInt1, bigInt2))
        _ <- Ns.bigInt.not(bigInt0, bigInt1).get.map(_.sortBy(_.toString) ==> List(bigInt2))
        _ <- Ns.bigInt.not(Seq(bigInt0, bigInt1)).get.map(_.sortBy(_.toString) ==> List(bigInt2))
        bigInts = Seq(bigInt0, bigInt1)
        _ <- Ns.bigInt.not(bigInts).get.map(_.sortBy(_.toString) ==> List(bigInt2))


        _ <- Ns.bigDec.not(bigDec3).get.map(_.sortBy(_.toString) ==> List(bigDec0, bigDec1, bigDec2))
        _ <- Ns.bigDec.not(bigDec0).get.map(_.sortBy(_.toString) ==> List(bigDec1, bigDec2))
        _ <- Ns.bigDec.not(bigDec0, bigDec1).get.map(_.sortBy(_.toString) ==> List(bigDec2))
        _ <- Ns.bigDec.not(Seq(bigDec0, bigDec1)).get.map(_.sortBy(_.toString) ==> List(bigDec2))
        bigDecs = Seq(bigDec0, bigDec1)
        _ <- Ns.bigDec.not(bigDecs).get.map(_.sortBy(_.toString) ==> List(bigDec2))
      } yield ()
    }

    "Quoting" - core { implicit conn =>
      for {
        _ <- Ns.int(1).str("""Hi "Ann"""").save

        _ <- Ns.str.not("""Hi "Ben"""").get.map(_ ==> List("""Hi "Ann""""))

        str2: String = """Hi "Ben""""
        _ <- Ns.str.not(str2).get.map(_ ==> List("""Hi "Ann""""))

        _ <- Ns.int.str_.not("""Hi "Ben"""").get.map(_ ==> List(1))
        _ <- Ns.int.str_.not(str2).get.map(_ ==> List(1))
      } yield ()
    }

    "Card many - coalesce 1 attr" - core { implicit conn =>
      for {
        _ <- manyData

        // Negation of a single cardinality-many attribute value is rather useless since it
        // will just return the coalesced set of values minus the excluded value
        _ <- Ns.strs.not("b").get.map(_ ==> List(Set("d", "a", "ba", "c")))

        // We could group by another attribute but that still leave us with filtered sets
        _ <- Ns.str.strs.not("a").get.map(_ ==> List(("str1", Set("b")), ("str2", Set("b", "c")), ("str3", Set("d", "ba"))))
        _ <- Ns.str.strs.not("b").get.map(_ ==> List(("str1", Set("a")), ("str2", Set("c")), ("str3", Set("d", "ba"))))
        _ <- Ns.str.strs.not("c").get.map(_ ==> List(("str1", Set("a", "b")), ("str2", Set("b")), ("str3", Set("d", "ba"))))
        _ <- Ns.str.strs.not("d").get.map(_ ==> List(("str1", Set("a", "b")), ("str2", Set("b", "c")), ("str3", Set("ba"))))

        // What we probably want to do is to filter out full sets having the negation value:
        _ <- Ns.str.strs.get.map(_.filterNot(_._2.contains("a")) ==> List(("str2", Set("b", "c")), ("str3", Set("d", "ba"))))
        _ <- Ns.str.strs.get.map(_.filterNot(_._2.contains("b")) ==> List(("str3", Set("d", "ba"))))
        _ <- Ns.str.strs.get.map(_.filterNot(_._2.contains("c")) ==> List(("str1", Set("a", "b")), ("str3", Set("d", "ba"))))
        _ <- Ns.str.strs.get.map(_.filterNot(_._2.contains("d")) ==> List(("str1", Set("a", "b")), ("str2", Set("b", "c"))))
      } yield ()
    }

    "Card many" - core { implicit conn =>
      val all = List(
        (1, Set(1, 2, 3)),
        (2, Set(2, 3, 4)),
        (3, Set(3, 4, 5))
      )

      for {
        _ <- Ns.int.ints insert all

        _ <- Ns.int.ints.not(Nil).get.map(_ ==> all)
        _ <- Ns.int.ints.not(Set[Int]()).get.map(_ ==> all)

        _ <- Ns.int.ints.not(1).get.map(_ ==> List(
          (1, Set(2, 3)),
          (2, Set(2, 3, 4)),
          (3, Set(3, 4, 5))
        ))
        // Same as
        _ <- Ns.int.ints.not(List(1)).get.map(_ ==> List(
          (1, Set(2, 3)),
          (2, Set(2, 3, 4)),
          (3, Set(3, 4, 5))
        ))

        // Set semantics omit the whole set with one or more matching values
        _ <- Ns.int.ints.not(Set(1)).get.map(_ ==> List(
          // (1, Set(1, 2, 3)),  // 1 match
          (2, Set(2, 3, 4)),
          (3, Set(3, 4, 5))
        ))
        // Same as
        _ <- Ns.int.ints.not(List(Set(1))).get.map(_ ==> List(
          (2, Set(2, 3, 4)),
          (3, Set(3, 4, 5))
        ))

        _ <- Ns.int.ints.not(2).get.map(_ ==> List(
          (1, Set(1, 3)),
          (2, Set(3, 4)),
          (3, Set(3, 4, 5))
        ))
        _ <- Ns.int.ints.not(Set(2)).get.map(_ ==> List(
          // (1, Set(1, 2, 3)),  // 2 match
          // (2, Set(2, 3, 4)),  // 2 match
          (3, Set(3, 4, 5))
        ))

        _ <- Ns.int.ints.not(3).get.map(_ ==> List(
          (1, Set(1, 2)),
          (2, Set(4, 2)),
          (3, Set(4, 5))
        ))
        _ <- Ns.int.ints.not(Set(3)).get.map(_ ==> Nil) // 3 match all


        _ <- Ns.int.ints.not(1, 2).get.map(_ ==> List(
          (1, Set(3)),
          (2, Set(3, 4)),
          (3, Set(3, 4, 5))
        ))
        // Same as
        _ <- Ns.int.ints.not(List(1, 2)).get.map(_ ==> List(
          (1, Set(3)),
          (2, Set(3, 4)),
          (3, Set(3, 4, 5))
        ))

        _ <- Ns.int.ints.not(Set(1), Set(2)).get.map(_ ==> List(
          // (1, Set(1, 2, 3)),  // 1 match, 2 match
          // (2, Set(2, 3, 4)),  // 2 match
          (3, Set(3, 4, 5))
        ))
        // Multiple values in a Set matches matches set-wise
        _ <- Ns.int.ints.not(Set(1, 2)).get.map(_ ==> List(
          // (1, Set(1, 2, 3)),  // 1 AND 2 match
          (2, Set(2, 3, 4)),
          (3, Set(3, 4, 5))
        ))


        _ <- Ns.int.ints.not(1, 3).get.map(_ ==> List(
          (1, Set(2)),
          (2, Set(2, 4)),
          (3, Set(4, 5))
        ))
        _ <- Ns.int.ints.not(Set(1), Set(3)).get.map(_ ==> Nil) // 3 match all
        _ <- Ns.int.ints.not(Set(1, 3)).get.map(_ ==> List(
          // (1, Set(1, 2, 3)),  // 1 AND 3 match
          (2, Set(2, 3, 4)),
          (3, Set(3, 4, 5))
        ))


        _ <- Ns.int.ints.not(1, 2, 3).get.map(_ ==> List(
          // (1, Set(1, 2, 3)),  // 1 match, 2 match, 3 match
          (2, Set(4)),
          (3, Set(4, 5))
        ))
        _ <- Ns.int.ints.not(Set(1), Set(2), Set(3)).get.map(_ ==> Nil) // 3 match all
        _ <- Ns.int.ints.not(Set(1, 2, 3)).get.map(_ ==> List(
          // (1, Set(1, 2, 3)),  // 1 AND 2 AND 3 match
          (2, Set(2, 3, 4)),
          (3, Set(3, 4, 5))
        ))


        _ <- Ns.int.ints.not(Set(1, 2), Set(1)).get.map(_ ==> List(
          (2, Set(2, 3, 4)),
          (3, Set(3, 4, 5))
        ))
        _ <- Ns.int.ints.not(Set(1, 2), Set(2)).get.map(_ ==> List(
          (3, Set(3, 4, 5))
        ))
        _ <- Ns.int.ints.not(Set(1, 2), Set(3)).get.map(_ ==> Nil)
        _ <- Ns.int.ints.not(Set(1, 2), Set(4)).get.map(_ ==> Nil)
        _ <- Ns.int.ints.not(Set(1, 2), Set(5)).get.map(_ ==> List(
          (2, Set(2, 3, 4)),
        ))

        _ <- Ns.int.ints.not(Set(1, 2), Set(2, 3)).get.map(_ ==> List(
          (3, Set(3, 4, 5))
        ))
        _ <- Ns.int.ints.not(Set(1, 2), Set(4, 5)).get.map(_ ==> List(
          (2, Set(2, 3, 4))
        ))


        // Variables/values can be mixed

        l2 = List((2, Set(2, 3, 4)))

        _ <- Ns.int.ints.not(Set(1, 2), Set(4, 5)).get.map(_ ==> l2)
        _ <- Ns.int.ints.not(Set(1, int2), Set(int4, 5)).get.map(_ ==> l2)

        set45 = Set(4, 5)
        _ <- Ns.int.ints.not(Set(1, int2), set45).get.map(_ ==> l2)

        // Seq of Sets
        _ <- Ns.int.ints.not(List(Set(1, int2), set45)).get.map(_ ==> l2)

        sets = List(Set(1, int2), set45)
        _ <- Ns.int.ints.not(sets).get.map(_ ==> l2)
      } yield ()
    }

    "Card many, enum" - core { implicit conn =>
      val all = List(
        (1, Set("enum1", "enum2", "enum3")),
        (2, Set("enum2", "enum3", "enum4")),
        (3, Set("enum3", "enum4", "enum5"))
      )

      for {
        _ <- Ns.int.enums insert all

        _ <- Ns.int.enums.not(Nil).get.map(_ ==> all)
        _ <- Ns.int.enums.not(Set[String]()).get.map(_ ==> all)

        _ <- Ns.int.enums.not("enum1").get.map(_ ==> List(
          (1, Set("enum2", "enum3")),
          (2, Set("enum2", "enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))
        // Same as
        _ <- Ns.int.enums.not(List("enum1")).get.map(_ ==> List(
          (1, Set("enum2", "enum3")),
          (2, Set("enum2", "enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))

        // Set semantics omit the whole set with one or more matching values
        _ <- Ns.int.enums.not(Set("enum1")).get.map(_ ==> List(
          // (1, Set("enum1", "enum2", "enum3")),  // "enum1" match
          (2, Set("enum2", "enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))
        // Same as
        _ <- Ns.int.enums.not(List(Set("enum1"))).get.map(_ ==> List(
          (2, Set("enum2", "enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))

        _ <- Ns.int.enums.not("enum2").get.map(_ ==> List(
          (1, Set("enum1", "enum3")),
          (2, Set("enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))
        _ <- Ns.int.enums.not(Set("enum2")).get.map(_ ==> List(
          // (1, Set("enum1", "enum2", "enum3")),  // "enum2" match
          // (2, Set("enum2", "enum3", "enum4")),  // "enum2" match
          (3, Set("enum3", "enum4", "enum5"))
        ))

        _ <- Ns.int.enums.not("enum3").get.map(_ ==> List(
          (1, Set("enum1", "enum2")),
          (2, Set("enum4", "enum2")),
          (3, Set("enum4", "enum5"))
        ))
        _ <- Ns.int.enums.not(Set("enum3")).get.map(_ ==> Nil) // "enum3" match all


        _ <- Ns.int.enums.not("enum1", "enum2").get.map(_ ==> List(
          (1, Set("enum3")),
          (2, Set("enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))
        // Same as
        _ <- Ns.int.enums.not(List("enum1", "enum2")).get.map(_ ==> List(
          (1, Set("enum3")),
          (2, Set("enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))

        _ <- Ns.int.enums.not(Set("enum1"), Set("enum2")).get.map(_ ==> List(
          // (1, Set("enum1", "enum2", "enum3")),  // "enum1" match, "enum2" match
          // (2, Set("enum2", "enum3", "enum4")),  // "enum2" match
          (3, Set("enum3", "enum4", "enum5"))
        ))
        // Multiple values in a Set matches matches set-wise
        _ <- Ns.int.enums.not(Set("enum1", "enum2")).get.map(_ ==> List(
          // (1, Set("enum1", "enum2", "enum3")),  // "enum1" AND "enum2" match
          (2, Set("enum2", "enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))


        _ <- Ns.int.enums.not("enum1", "enum3").get.map(_ ==> List(
          (1, Set("enum2")),
          (2, Set("enum2", "enum4")),
          (3, Set("enum4", "enum5"))
        ))
        _ <- Ns.int.enums.not(Set("enum1"), Set("enum3")).get.map(_ ==> Nil) // "enum3" match all
        _ <- Ns.int.enums.not(Set("enum1", "enum3")).get.map(_ ==> List(
          // (1, Set("enum1", "enum2", "enum3")),  // "enum1" AND "enum3" match
          (2, Set("enum2", "enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))


        _ <- Ns.int.enums.not("enum1", "enum2", "enum3").get.map(_ ==> List(
          // (1, Set("enum1", "enum2", "enum3")),  // "enum1" match, "enum2" match, "enum3" match
          (2, Set("enum4")),
          (3, Set("enum4", "enum5"))
        ))
        _ <- Ns.int.enums.not(Set("enum1"), Set("enum2"), Set("enum3")).get.map(_ ==> Nil) // "enum3" match all
        _ <- Ns.int.enums.not(Set("enum1", "enum2", "enum3")).get.map(_ ==> List(
          // (1, Set("enum1", "enum2", "enum3")),  // "enum1" AND "enum2" AND "enum3" match
          (2, Set("enum2", "enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))


        _ <- Ns.int.enums.not(Set("enum1", "enum2"), Set("enum1")).get.map(_ ==> List(
          (2, Set("enum2", "enum3", "enum4")),
          (3, Set("enum3", "enum4", "enum5"))
        ))
        _ <- Ns.int.enums.not(Set("enum1", "enum2"), Set("enum2")).get.map(_ ==> List(
          (3, Set("enum3", "enum4", "enum5"))
        ))
        _ <- Ns.int.enums.not(Set("enum1", "enum2"), Set("enum3")).get.map(_ ==> Nil)
        _ <- Ns.int.enums.not(Set("enum1", "enum2"), Set("enum4")).get.map(_ ==> Nil)
        _ <- Ns.int.enums.not(Set("enum1", "enum2"), Set("enum5")).get.map(_ ==> List(
          (2, Set("enum2", "enum3", "enum4")),
        ))

        _ <- Ns.int.enums.not(Set("enum1", "enum2"), Set("enum2", "enum3")).get.map(_ ==> List(
          (3, Set("enum3", "enum4", "enum5"))
        ))
        _ <- Ns.int.enums.not(Set("enum1", "enum2"), Set("enum4", "enum5")).get.map(_ ==> List(
          (2, Set("enum2", "enum3", "enum4"))
        ))


        // Variables/values can be mixed

        l2 = List((2, Set("enum2", "enum3", "enum4")))

        _ <- Ns.int.enums.not(Set("enum1", "enum2"), Set("enum4", "enum5")).get.map(_ ==> l2)
        _ <- Ns.int.enums.not(Set("enum1", enum2), Set(enum4, "enum5")).get.map(_ ==> l2)

        set45 = Set("enum4", "enum5")
        _ <- Ns.int.enums.not(Set("enum1", enum2), set45).get.map(_ ==> l2)

        // Seq of Sets
        _ <- Ns.int.enums.not(List(Set("enum1", enum2), set45)).get.map(_ ==> l2)

        sets = List(Set("enum1", enum2), set45)
        _ <- Ns.int.enums.not(sets).get.map(_ ==> l2)
      } yield ()
    }

    "Card many, uri" - core { implicit conn =>
      val all = List(
        (1, Set(uri1, uri2, uri3)),
        (2, Set(uri2, uri3, uri4)),
        (3, Set(uri3, uri4, uri5))
      )

      for {
        _ <- Ns.int.uris insert all

        _ <- Ns.int.uris.not(Nil).get.map(_ ==> all)
        _ <- Ns.int.uris.not(Set[URI]()).get.map(_ ==> all)

        _ <- Ns.int.uris.not(uri1).get.map(_ ==> List(
          (1, Set(uri2, uri3)),
          (2, Set(uri2, uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))
        // Same as
        _ <- Ns.int.uris.not(List(uri1)).get.map(_ ==> List(
          (1, Set(uri2, uri3)),
          (2, Set(uri2, uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))

        // Set semantics omit the whole set with one or more matching values
        _ <- Ns.int.uris.not(Set(uri1)).get.map(_ ==> List(
          // (1, Set(uri1, uri2, uri3)),  // uri1 match
          (2, Set(uri2, uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))
        // Same as
        _ <- Ns.int.uris.not(List(Set(uri1))).get.map(_ ==> List(
          (2, Set(uri2, uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))

        _ <- Ns.int.uris.not(uri2).get.map(_ ==> List(
          (1, Set(uri1, uri3)),
          (2, Set(uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))
        _ <- Ns.int.uris.not(Set(uri2)).get.map(_ ==> List(
          // (1, Set(uri1, uri2, uri3)),  // uri2 match
          // (2, Set(uri2, uri3, uri4)),  // uri2 match
          (3, Set(uri3, uri4, uri5))
        ))

        _ <- Ns.int.uris.not(uri3).get.map(_ ==> List(
          (1, Set(uri1, uri2)),
          (2, Set(uri4, uri2)),
          (3, Set(uri4, uri5))
        ))
        _ <- Ns.int.uris.not(Set(uri3)).get.map(_ ==> Nil) // uri3 match all


        _ <- Ns.int.uris.not(uri1, uri2).get.map(_ ==> List(
          (1, Set(uri3)),
          (2, Set(uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))
        // Same as
        _ <- Ns.int.uris.not(List(uri1, uri2)).get.map(_ ==> List(
          (1, Set(uri3)),
          (2, Set(uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))

        _ <- Ns.int.uris.not(Set(uri1), Set(uri2)).get.map(_ ==> List(
          // (1, Set(uri1, uri2, uri3)),  // uri1 match, uri2 match
          // (2, Set(uri2, uri3, uri4)),  // uri2 match
          (3, Set(uri3, uri4, uri5))
        ))
        // Multiple values in a Set matches matches set-wise
        _ <- Ns.int.uris.not(Set(uri1, uri2)).get.map(_ ==> List(
          // (1, Set(uri1, uri2, uri3)),  // uri1 AND uri2 match
          (2, Set(uri2, uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))


        _ <- Ns.int.uris.not(uri1, uri3).get.map(_ ==> List(
          (1, Set(uri2)),
          (2, Set(uri2, uri4)),
          (3, Set(uri4, uri5))
        ))
        _ <- Ns.int.uris.not(Set(uri1), Set(uri3)).get.map(_ ==> Nil) // uri3 match all
        _ <- Ns.int.uris.not(Set(uri1, uri3)).get.map(_ ==> List(
          // (1, Set(uri1, uri2, uri3)),  // uri1 AND uri3 match
          (2, Set(uri2, uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))


        _ <- Ns.int.uris.not(uri1, uri2, uri3).get.map(_ ==> List(
          // (1, Set(uri1, uri2, uri3)),  // uri1 match, uri2 match, uri3 match
          (2, Set(uri4)),
          (3, Set(uri4, uri5))
        ))
        _ <- Ns.int.uris.not(Set(uri1), Set(uri2), Set(uri3)).get.map(_ ==> Nil) // uri3 match all
        _ <- Ns.int.uris.not(Set(uri1, uri2, uri3)).get.map(_ ==> List(
          // (1, Set(uri1, uri2, uri3)),  // uri1 AND uri2 AND uri3 match
          (2, Set(uri2, uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))


        _ <- Ns.int.uris.not(Set(uri1, uri2), Set(uri1)).get.map(_ ==> List(
          (2, Set(uri2, uri3, uri4)),
          (3, Set(uri3, uri4, uri5))
        ))
        _ <- Ns.int.uris.not(Set(uri1, uri2), Set(uri2)).get.map(_ ==> List(
          (3, Set(uri3, uri4, uri5))
        ))
        _ <- Ns.int.uris.not(Set(uri1, uri2), Set(uri3)).get.map(_ ==> Nil)
        _ <- Ns.int.uris.not(Set(uri1, uri2), Set(uri4)).get.map(_ ==> Nil)
        _ <- Ns.int.uris.not(Set(uri1, uri2), Set(uri5)).get.map(_ ==> List(
          (2, Set(uri2, uri3, uri4)),
        ))

        _ <- Ns.int.uris.not(Set(uri1, uri2), Set(uri2, uri3)).get.map(_ ==> List(
          (3, Set(uri3, uri4, uri5))
        ))
        _ <- Ns.int.uris.not(Set(uri1, uri2), Set(uri4, uri5)).get.map(_ ==> List(
          (2, Set(uri2, uri3, uri4))
        ))
      } yield ()
    }
  }
}