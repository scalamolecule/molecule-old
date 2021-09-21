package moleculeTests.tests.core.expression

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out3._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}

object Aggregates extends AsyncTestSuite {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext): Future[Unit] = {
    for {
      // We pair cardinality many attribute values with card-one's
      // too to be able to group by cardinality one values
      _ <- Ns.str.strs insert List(
        ("str1", Set("a", "b")),
        ("str2", Set("b", "c")),
        ("str3", Set("ba", "d")),
        ("str4", Set.empty[String])
      )

      _ <- Ns.int.ints insert List(
        (1, Set(1, 2)),
        (2, Set(2, 3)),
        (3, Set(2, 4)))

      _ <- Ns.long.longs insert List(
        (1L, Set(1L, 2L)),
        (2L, Set(2L, 3L)),
        (3L, Set(2L, 4L)))

      _ <- Ns.double.doubles insert List(
        (1.1, Set(1.1, 2.2)),
        (2.2, Set(2.2, 3.3)),
        (3.3, Set(3.3, 4.4)))

      // Set of boolean values maybe not so useful
      _ <- Ns.bool.bools insert List(
        (false, Set(false)),
        (true, Set(false, true)))

      _ <- Ns.date.dates insert List(
        (date1, Set(date1, date2)),
        (date2, Set(date2, date3)),
        (date3, Set(date2, date4)))

      _ <- Ns.uuid.uuids insert List(
        (uuid1, Set(uuid1, uuid2)),
        (uuid2, Set(uuid2, uuid3)),
        (uuid3, Set(uuid2, uuid4)))

      _ <- Ns.uri.uris insert List(
        (uri1, Set(uri1, uri2)),
        (uri2, Set(uri2, uri3)),
        (uri3, Set(uri2, uri4)))

      _ <- Ns.enum.enums insert List(
        (enum1, Set(enum1, enum2)),
        (enum2, Set(enum2, enum3)),
        (enum3, Set(enum2, enum4)))

      _ <- Ns.bigInt.bigInts insert List(
        (bigInt1, Set(bigInt1, bigInt2)),
        (bigInt2, Set(bigInt2, bigInt3)),
        (bigInt3, Set(bigInt2, bigInt4)))

      _ <- Ns.bigDec.bigDecs insert List(
        (bigDec1, Set(bigDec1, bigDec2)),
        (bigDec2, Set(bigDec2, bigDec3)),
        (bigDec3, Set(bigDec2, bigDec4)))
    } yield ()
  }

  def round(value: Double, decimals: Int): Double = {
    val factor = scala.math.pow(10, decimals)
    (value * factor).round / factor.toDouble
  }


  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "sum" - core { implicit conn =>
      for {
        _ <- testData

        _ <- Ns.int(sum).get.map(_.head ==> (1 + 2 + 3))
        _ <- Ns.long(sum).get.map(_.head ==> 6L)

        //    // Peer server seems more picky with precision
        //    // For full precision, please use BigDecimal or save as Long and divide
        //    if (system == SystemPeerServer)
        //      round(Ns.double(sum).get.map(_.head, 6) ==> 6.6)
        //    //      Ns.double(sum).get.map(_.head ==> 6.6000000000000005)
        //    else
        //      _ <- Ns.double(sum).get.map(_.head ==> 6.6)

      } yield ()
    }

    "median" - core { implicit conn =>
      for {
        _ <- testData
        _ <- Ns.int(median).get.map(_.head ==> 2)
        _ <- Ns.long(median).get.map(_.head ==> 2L)
        _ <- Ns.double(median).get.map(_.head ==> 2.2)
      } yield ()
    }

    "avg" - core { implicit conn =>
      for {
        _ <- testData
        _ <- Ns.int(avg).get.map(_.head ==> (1 + 2 + 3) / 3)
        _ <- Ns.long(avg).get.map(_.head ==> 2)

        // Peer server seems more picky with precision
        // For full precision, please use BigDecimal or save as Long and divide
        //        if (system == SystemPeerServer)
        //        round (Ns.double(avg).get.map(_.head, 6) ==> 2.2)
        //        else
        //        _ <- Ns.double(avg).get.map(_.head ==> 2.1999999999999997)
      } yield ()
    }

    "variance" - core { implicit conn =>
      for {
        _ <- testData
        _ <- Ns.int(variance).get.map(_.head ==> 0.6666666666666666)
        _ <- Ns.long(variance).get.map(_.head ==> 0.6666666666666666)
        _ <- Ns.double(variance).get.map(_.head ==> 0.8066666666666665)
      } yield ()
    }

    "stddev" - core { implicit conn =>
      for {
        _ <- testData
        _ <- Ns.int(stddev).get.map(_.head ==> 0.816496580927726)
        _ <- Ns.long(stddev).get.map(_.head ==> 0.816496580927726)
        _ <- Ns.double(stddev).get.map(_.head ==> 0.8981462390204986)
      } yield ()
    }

    "min" - core { implicit conn =>
      for {
        _ <- testData

        // card one

        _ <- Ns.str(min).get.map(_.head ==> "str1")
        _ <- Ns.int(min).get.map(_.head ==> 1)
        _ <- Ns.long(min).get.map(_.head ==> 1L)
        _ <- Ns.double(min).get.map(_.head ==> 1.1)
        _ <- Ns.bool(min).get.map(_.head ==> false)
        _ <- Ns.date(min).get.map(_.head ==> date1)
        _ <- Ns.uuid(min).get.map(_.head ==> uuid1)
        _ <- Ns.uri(min).get.map(_.head ==> uri1)
        _ <- Ns.bigInt(min).get.map(_.head ==> bigInt1)
        _ <- Ns.bigDec(min).get.map(_.head ==> bigDec1)
        _ <- Ns.enum(min).get.map(_.head ==> enum1)

        _ <- Ns.str(min(2)).get.map(_.head ==> List("str1", "str2"))
        _ <- Ns.int(min(2)).get.map(_.head ==> List(1, 2))
        _ <- Ns.long(min(2)).get.map(_.head ==> List(1L, 2L))
        _ <- Ns.double(min(2)).get.map(_.head ==> List(1.1, 2.2))
        _ <- Ns.bool(min(2)).get.map(_.head ==> List(false, true))
        _ <- Ns.date(min(2)).get.map(_.head ==> List(date1, date2))
        _ <- Ns.uuid(min(2)).get.map(_.head ==> List(uuid1, uuid2))
        _ <- Ns.uri(min(2)).get.map(_.head ==> List(uri1, uri2))
        _ <- Ns.bigInt(min(2)).get.map(_.head ==> List(bigInt1, bigInt2))
        _ <- Ns.bigDec(min(2)).get.map(_.head ==> List(bigDec1, bigDec2))
        _ <- Ns.enum(min(2)).get.map(_.head ==> List(enum1, enum2))


        // card many

        _ <- Ns.strs(min).get.map(_.head ==> Set("a"))
        _ <- Ns.ints(min).get.map(_.head ==> Set(1))
        _ <- Ns.longs(min).get.map(_.head ==> Set(1L))
        _ <- Ns.doubles(min).get.map(_.head ==> Set(1.1))
        _ <- Ns.bools(min).get.map(_.head ==> Set(false))
        _ <- Ns.dates(min).get.map(_.head ==> Set(date1))
        _ <- Ns.uuids(min).get.map(_.head ==> Set(uuid1))
        _ <- Ns.uris(min).get.map(_.head ==> Set(uri1))
        _ <- Ns.bigInts(min).get.map(_.head ==> Set(bigInt1))
        _ <- Ns.bigDecs(min).get.map(_.head ==> Set(bigDec1))
        _ <- Ns.enums(min).get.map(_.head ==> Set(enum1))

        _ <- Ns.strs(min(2)).get.map(_.head ==> List(Set("a", "b")))
        _ <- Ns.ints(min(2)).get.map(_.head ==> List(Set(1, 2)))
        _ <- Ns.longs(min(2)).get.map(_.head ==> List(Set(1L, 2L)))
        _ <- Ns.doubles(min(2)).get.map(_.head ==> List(Set(1.1, 2.2)))
        _ <- Ns.bools(min(2)).get.map(_.head ==> List(Set(false, true)))
        _ <- Ns.dates(min(2)).get.map(_.head ==> List(Set(date1, date2)))
        _ <- Ns.uuids(min(2)).get.map(_.head ==> List(Set(uuid1, uuid2)))
        _ <- Ns.uris(min(2)).get.map(_.head ==> List(Set(uri1, uri2)))
        _ <- Ns.bigInts(min(2)).get.map(_.head ==> List(Set(bigInt1, bigInt2)))
        _ <- Ns.bigDecs(min(2)).get.map(_.head ==> List(Set(bigDec1, bigDec2)))
        _ <- Ns.enums(min(2)).get.map(_.head ==> List(Set(enum1, enum2)))


        // combinations

        _ <- Ns.int(min).ints.get.map(_ ==> List(
          (1, Set(1, 4, 3, 2))
        ))

        _ <- Ns.int(min(2)).ints.get.map(_ ==> List(
          (List(1, 1), Set(1, 4, 3, 2))
        ))


        _ <- Ns.int.ints(min).get.map(_ ==> List(
          (1, Set(1)),
          (2, Set(2)),
          (3, Set(2))
        ))

        _ <- Ns.int.ints(min(2)).get.map(_ ==> List(
          (1, List(Set(1, 2))),
          (2, List(Set(2, 3))),
          (3, List(Set(2, 4)))
        ))


        _ <- Ns.int(min).ints(min).get.map(_ ==> List(
          (1, Set(1))
        ))

        _ <- Ns.int(min(2)).ints(min).get.map(_ ==> List(
          (List(1, 1), Set(1))
        ))

        _ <- Ns.int(min).ints(min(2)).get.map(_ ==> List(
          (1, List(Set(1, 2)))
        ))

        _ <- Ns.int(min(2)).ints(min(2)).get.map(_ ==> List(
          (List(1, 1), List(Set(1, 2)))
        ))
      } yield ()
    }

    "max" - core { implicit conn =>
      for {
        _ <- testData

        // card one

        _ <- Ns.str(max).get.map(_.head ==> "str4")
        _ <- Ns.int(max).get.map(_.head ==> 3)
        _ <- Ns.long(max).get.map(_.head ==> 3L)
        _ <- Ns.double(max).get.map(_.head ==> 3.3)
        _ <- Ns.bool(max).get.map(_.head ==> true)
        _ <- Ns.date(max).get.map(_.head ==> date3)
        _ <- Ns.uuid(max).get.map(_.head ==> uuid3)
        _ <- Ns.uri(max).get.map(_.head ==> uri3)
        _ <- Ns.enum(max).get.map(_.head ==> enum3)
        _ <- Ns.bigInt(max).get.map(_.head ==> bigInt3)
        _ <- Ns.bigDec(max).get.map(_.head ==> bigDec3)

        _ <- Ns.str(max(2)).get.map(_.head ==> List("str4", "str3"))
        _ <- Ns.int(max(2)).get.map(_.head ==> List(3, 2))
        _ <- Ns.long(max(2)).get.map(_.head ==> List(3L, 2L))
        _ <- Ns.double(max(2)).get.map(_.head ==> List(3.3, 2.2))
        _ <- Ns.bool(max(2)).get.map(_.head ==> List(true, false))
        _ <- Ns.date(max(2)).get.map(_.head ==> List(date3, date2))
        _ <- Ns.uuid(max(2)).get.map(_.head ==> List(uuid3, uuid2))
        _ <- Ns.uri(max(2)).get.map(_.head ==> List(uri3, uri2))
        _ <- Ns.enum(max(2)).get.map(_.head ==> List(enum3, enum2))
        _ <- Ns.bigInt(max(2)).get.map(_.head ==> List(bigInt3, bigInt2))
        _ <- Ns.bigDec(max(2)).get.map(_.head ==> List(bigDec3, bigDec2))


        // card many

        _ <- Ns.strs(max).get.map(_.head ==> Set("d"))
        _ <- Ns.ints(max).get.map(_.head ==> Set(4))
        _ <- Ns.longs(max).get.map(_.head ==> Set(4L))
        _ <- Ns.doubles(max).get.map(_.head ==> Set(4.4))
        _ <- Ns.bools(max).get.map(_.head ==> Set(true))
        _ <- Ns.dates(max).get.map(_.head ==> Set(date4))
        _ <- Ns.uuids(max).get.map(_.head ==> Set(uuid4))
        _ <- Ns.uris(max).get.map(_.head ==> Set(uri4))
        _ <- Ns.enums(max).get.map(_.head ==> Set(enum4))
        _ <- Ns.bigInts(max).get.map(_.head ==> Set(bigInt4))
        _ <- Ns.bigDecs(max).get.map(_.head ==> Set(bigDec4))

        _ <- Ns.strs(max(2)).get.map(_.head ==> List(Set("d", "c")))
        _ <- Ns.ints(max(2)).get.map(_.head ==> List(Set(4, 3)))
        _ <- Ns.longs(max(2)).get.map(_.head ==> List(Set(4L, 3L)))
        _ <- Ns.doubles(max(2)).get.map(_.head ==> List(Set(4.4, 3.3)))
        _ <- Ns.bools(max(2)).get.map(_.head ==> List(Set(true, false)))
        _ <- Ns.dates(max(2)).get.map(_.head ==> List(Set(date4, date3)))
        _ <- Ns.uuids(max(2)).get.map(_.head ==> List(Set(uuid4, uuid3)))
        _ <- Ns.uris(max(2)).get.map(_.head ==> List(Set(uri4, uri3)))
        _ <- Ns.enums(max(2)).get.map(_.head ==> List(Set(enum4, enum3)))
        _ <- Ns.bigInts(max(2)).get.map(_.head ==> List(Set(bigInt4, bigInt3)))
        _ <- Ns.bigDecs(max(2)).get.map(_.head ==> List(Set(bigDec4, bigDec3)))


        // combinations

        _ <- Ns.int(max).ints.get.map(_ ==> List(
          (3, Set(1, 4, 3, 2))
        ))

        _ <- Ns.int(max(2)).ints.get.map(_ ==> List(
          (List(3, 3), Set(1, 4, 3, 2))
        ))


        _ <- Ns.int.ints(max).get.map(_ ==> List(
          (1, Set(2)),
          (2, Set(3)),
          (3, Set(4))
        ))

        _ <- Ns.int.ints(max(2)).get.map(_ ==> List(
          (1, List(Set(1, 2))),
          (2, List(Set(2, 3))),
          (3, List(Set(2, 4)))
        ))


        _ <- Ns.int(max).ints(max).get.map(_ ==> List(
          (3, Set(4))
        ))

        _ <- Ns.int(max(2)).ints(max).get.map(_ ==> List(
          (List(3, 3), Set(4))
        ))

        _ <- Ns.int(max).ints(max(2)).get.map(_ ==> List(
          (3, List(Set(4, 3)))
        ))

        _ <- Ns.int(max(2)).ints(max(2)).get.map(_ ==> List(
          (List(3, 3), List(Set(4, 3)))
        ))
      } yield ()
    }
    val strs    = Seq("str1", "str2", "str3", "str4")
    val ints    = Seq(1, 2, 3)
    val longs   = Seq(1L, 2L, 3L)
    val doubles = Seq(1.1, 2.2, 3.3)
    val bools   = Seq(false, true)
    val dates   = Seq(date1, date2, date3)
    val uuids   = Seq(uuid1, uuid2, uuid3)
    val uris    = Seq(uri1, uri2, uri3)
    val enums   = Seq(enum1, enum2, enum3)
    val bigInts = Seq(bigInt1, bigInt2, bigInt3)
    val bigDecs = Seq(bigDec1, bigDec2, bigDec3)


    "rand" - core { implicit conn =>
      for {
        _ <- testData

        _ <- Ns.str(rand).get.map(list => strs.contains(list.head) ==> true)
        _ <- Ns.int(rand).get.map(list => ints.contains(list.head) ==> true)
        _ <- Ns.long(rand).get.map(list => longs.contains(list.head) ==> true)
        _ <- Ns.double(rand).get.map(list => doubles.contains(list.head) ==> true)
        _ <- Ns.bool(rand).get.map(list => bools.contains(list.head) ==> true)
        _ <- Ns.date(rand).get.map(list => dates.contains(list.head) ==> true)
        _ <- Ns.uuid(rand).get.map(list => uuids.contains(list.head) ==> true)
        _ <- Ns.uri(rand).get.map(list => uris.contains(list.head) ==> true)
        _ <- Ns.enum(rand).get.map(list => enums.contains(list.head) ==> true)
        _ <- Ns.bigInt(rand).get.map(list => bigInts.contains(list.head) ==> true)
        _ <- Ns.bigDec(rand).get.map(list => bigDecs.contains(list.head) ==> true)

        _ <- Ns.str(rand(2)).get.map(list => strs.intersect(list.head).nonEmpty ==> true)
        _ <- Ns.int(rand(2)).get.map(list => ints.intersect(list.head).nonEmpty ==> true)
        _ <- Ns.long(rand(2)).get.map(list => longs.intersect(list.head).nonEmpty ==> true)
        _ <- Ns.double(rand(2)).get.map(list => doubles.intersect(list.head).nonEmpty ==> true)
        _ <- Ns.bool(rand(2)).get.map(list => bools.intersect(list.head).nonEmpty ==> true)
        _ <- Ns.date(rand(2)).get.map(list => dates.intersect(list.head).nonEmpty ==> true)
        _ <- Ns.uuid(rand(2)).get.map(list => uuids.intersect(list.head).nonEmpty ==> true)
        _ <- Ns.uri(rand(2)).get.map(list => uris.intersect(list.head).nonEmpty ==> true)
        _ <- Ns.enum(rand(2)).get.map(list => enums.intersect(list.head).nonEmpty ==> true)
        _ <- Ns.bigInt(rand(2)).get.map(list => bigInts.intersect(list.head).nonEmpty ==> true)
        _ <- Ns.bigDec(rand(2)).get.map(list => bigDecs.intersect(list.head).nonEmpty ==> true)
      } yield ()
    }

    "sample" - core { implicit conn =>
      for {
        _ <- testData

        _ <- Ns.str(sample).get.map(list => strs.contains(list.head) ==> true)
        _ <- Ns.int(sample).get.map(list => ints.contains(list.head) ==> true)
        _ <- Ns.long(sample).get.map(list => longs.contains(list.head) ==> true)
        _ <- Ns.double(sample).get.map(list => doubles.contains(list.head) ==> true)
        _ <- Ns.bool(sample).get.map(list => bools.contains(list.head) ==> true)
        _ <- Ns.date(sample).get.map(list => dates.contains(list.head) ==> true)
        _ <- Ns.uuid(sample).get.map(list => uuids.contains(list.head) ==> true)
        _ <- Ns.uri(sample).get.map(list => uris.contains(list.head) ==> true)
        _ <- Ns.enum(sample).get.map(list => enums.contains(list.head) ==> true)
        _ <- Ns.bigInt(sample).get.map(list => bigInts.contains(list.head) ==> true)
        _ <- Ns.bigDec(sample).get.map(list => bigDecs.contains(list.head) ==> true)

        _ <- Ns.str(sample(2)).get.map(list => strs.intersect(list.head).size ==> 2)
        _ <- Ns.int(sample(2)).get.map(list => ints.intersect(list.head).size ==> 2)
        _ <- Ns.long(sample(2)).get.map(list => longs.intersect(list.head).size ==> 2)
        _ <- Ns.double(sample(2)).get.map(list => doubles.intersect(list.head).size ==> 2)
        _ <- Ns.bool(sample(2)).get.map(list => bools.intersect(list.head).size ==> 2)
        _ <- Ns.date(sample(2)).get.map(list => dates.intersect(list.head).size ==> 2)
        _ <- Ns.uuid(sample(2)).get.map(list => uuids.intersect(list.head).size ==> 2)
        _ <- Ns.uri(sample(2)).get.map(list => uris.intersect(list.head).size ==> 2)
        _ <- Ns.enum(sample(2)).get.map(list => enums.intersect(list.head).size ==> 2)
        _ <- Ns.bigInt(sample(2)).get.map(list => bigInts.intersect(list.head).size ==> 2)
        _ <- Ns.bigDec(sample(2)).get.map(list => bigDecs.intersect(list.head).size ==> 2)
      } yield ()
    }

    "distinct" - core { implicit conn =>
      for {
        _ <- Ns.str.int insert List(
          ("a", 1),
          ("b", 2),
          ("b", 2),
          ("b", 3)
        )
        _ <- Ns.int(4).save


        _ <- Ns.str.int.get.map(_.sortBy(r => (r._1, r._2)) ==> List(
          ("a", 1),
          ("b", 2),
          ("b", 3)
        ))

        _ <- Ns.e.str.int.get.map(_.map(r => (r._2, r._3)).sortBy(r => (r._1, r._2)) ==> List(
          ("a", 1),
          ("b", 2),
          ("b", 2),
          ("b", 3)
        ))

        _ <- Ns.str.int(distinct).get.map(_.sortBy(_._1) ==> List(
          ("a", List(1)),
          ("b", List(3, 2)),
        ))

        _ <- Ns.int.str(distinct).get.map(_.sortBy(_._1) ==> List(
          (1, List("a")),
          (2, List("b")),
          (3, List("b"))
        ))

        _ <- Ns.str(distinct).int(distinct).get.map(_ ==> List(
          (List("a", "b"), List(1, 3, 2)),
        ))
      } yield ()
    }

    "count, countDistinct" - core { implicit conn =>
      for {
        _ <- Ns.str.int insert List(
          ("a", 1),
          ("b", 2),
          ("b", 2),
          ("b", 3)
        )
        _ <- Ns.int(4).save

        _ <- Ns.str.int(count).get.map(_.sorted ==> List(
          ("a", 1),
          ("b", 3)
        ))
        _ <- Ns.str.int(countDistinct).get.map(_.sorted ==> List(
          ("a", 1),
          ("b", 2)
        ))

        _ <- Ns.int.str(count).get.map(_.sorted ==> List(
          (1, 1),
          (2, 2),
          (3, 1)
        ))
        _ <- Ns.int.str(countDistinct).get.map(_.sorted ==> List(
          (1, 1),
          (2, 1),
          (3, 1)
        ))

        _ <- Ns.str(count).str(countDistinct).get.map(_ ==> List(
          (4, 2),
        ))

        // extra int without a str value was saved
        _ <- Ns.int(count).int(countDistinct).get.map(_ ==> List(
          (5, 4),
        ))

        _ <- Ns.int(count).get.map(_ ==> List(5))
        _ <- Ns.int(countDistinct).get.map(_ ==> List(4))

        _ <- Ns.str_(Nil).int.get.map(_ ==> List(4))
      } yield ()
    }

    "count, countDistinct with entity id" - core { implicit conn =>
      for {
        // card-one

        tx1 <- Ns.int(1).save
        tx2 <- Ns.int(2).save
        tx3 <- Ns.int(3).save
        e1 = tx1.eid
        e2 = tx2.eid
        e3 = tx3.eid

        _ <- Ns.e.int(count).get.map(_.sorted ==> List(
          (e1, 1),
          (e2, 1),
          (e3, 1)
        ).sorted)

        // card-many

        tx4 <- Ns.ints(Seq(1, 2)).save
        tx5 <- Ns.ints(3).save
        e4 = tx4.eid
        e5 = tx5.eid

        _ <- Ns.e.ints(count).get.map(_.sortBy(_._2) ==> List(
          (e5, 1),
          (e4, 2),
        ))
      } yield ()
    }

    "Map attributes can't use aggregates" - core { implicit conn =>
      expectCompileError("m(Ns.intMap(min))",
        "molecule.core.ops.exception.VerifyRawModelException: Only expression keywords `not` and `unify` can be applied to Map attributes."
      )

      expectCompileError("""m(Ns.intMapK("a")(min))""",
        "molecule.core.ops.exception.VerifyRawModelException: Only expression keywords `not` and `unify` can be applied to Map attributes."
      )
    }

    "Multiple aggregates of same attr" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 4)
        _ <- Ns.int(sum).int(avg).int(median).get.map(_.head ==> (7, 2.3333333333333335, 2))
      } yield ()
    }
  }
}