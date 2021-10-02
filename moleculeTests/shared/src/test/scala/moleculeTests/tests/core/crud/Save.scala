package moleculeTests.tests.core.crud

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.out9._
import moleculeTests.Adhoc.{bigDec1, bigInt1, date1, uri1, uuid1}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Save extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one attr" - core { implicit conn =>
      for {
        _ <- Ns.str("a").save
        _ <- Ns.int(1).save
        _ <- Ns.long(1L).save
        _ <- Ns.double(1.1).save
        _ <- Ns.bool(true).save
        _ <- Ns.date(date1).save
        _ <- Ns.uuid(uuid1).save
        _ <- Ns.uri(uri1).save
        _ <- Ns.bigInt(bigInt1).save
        _ <- Ns.bigDec(bigDec1).save
        _ <- Ns.enum("enum1").save

        _ <- Ns.str.get.map(_.head ==> "a")
        _ <- Ns.int.get.map(_.head ==> 1)
        _ <- Ns.long.get.map(_.head ==> 1L)
        _ <- Ns.double.get.map(_.head ==> 1.1)
        _ <- Ns.bool.get.map(_.head ==> true)
        _ <- Ns.date.get.map(_.head ==> date1)
        _ <- Ns.uuid.get.map(_.head ==> uuid1)
        _ <- Ns.uri.get.map(_.head ==> uri1)
        _ <- Ns.bigInt.get.map(_.head ==> bigInt1)
        _ <- Ns.bigDec.get.map(_.head ==> bigDec1)
        _ <- Ns.enum.get.map(_.head ==> "enum1")

        // Applying multiple values to card-one attr not allowed when saving

        _ <- Ns.str("a", "b").save.recover { case VerifyModelException(err) =>
          err ==> "[noConflictingCardOneValues]  Can't save multiple values for cardinality-one attribute:" +
            s"\n  Ns ... str(a, b)"
        }
      } yield ()
    }

    "Card many attr" - core { implicit conn =>
      for {
        _ <- Ns.strs("a", "b")
          .ints(1, 2)
          .longs(1L, 2L)
          .doubles(1.1, 2.2)
          .dates(date1, date2)
          .uuids(uuid1, uuid2)
          .uris(uri1, uri2)
          .enums("enum1", "enum2").save

        _ <- Ns.strs.ints.longs.doubles.dates.uuids.uris.enums.get.map(_.head ==> (
          Set("a", "b"),
          Set(1, 2),
          Set(1L, 2L),
          Set(1.1, 2.2),
          Set(date1, date2),
          Set(uuid1, uuid2),
          Set(uri1, uri2),
          Set("enum1", "enum2")))
      } yield ()
    }


    "Card many attrs" - {

      "Vararg, various types" - core { implicit conn =>
        for {
          _ <- Ns.strs("a").save
          _ <- Ns.strs("a", "b", "c").save
          _ <- Ns.strs.get.map(_.head ==> Set("a", "b", "c"))

          _ <- Ns.longs(1L).save
          _ <- Ns.longs(2L, 3L).save
          _ <- Ns.longs.get.map(_.head ==> Set(1L, 2L, 3L))

          _ <- Ns.doubles(1.1).save
          _ <- Ns.doubles(2.2, 3.3).save
          _ <- Ns.doubles.get.map(_.head ==> Set(1.1, 2.2, 3.3))

          // Ns.bools not implemented...

          _ <- Ns.dates(date1).save
          _ <- Ns.dates(date2, date3).save
          _ <- Ns.dates.get.map(_.head ==> Set(date1, date2, date3))

          _ <- Ns.uuids(uuid1).save
          _ <- Ns.uuids(uuid2, uuid3).save
          _ <- Ns.uuids.get.map(_.head ==> Set(uuid1, uuid2, uuid3))

          _ <- Ns.uris(uri1).save
          _ <- Ns.uris(uri2, uri3).save
          _ <- Ns.uris.get.map(_.head ==> Set(uri1, uri2, uri3))

          _ <- Ns.enums("enum1").save
          _ <- Ns.enums("enum2", "enum3").save
          _ <- Ns.enums.get.map(_.head ==> Set("enum1", "enum2", "enum3"))
        } yield ()
      }

      "Vararg" - core { implicit conn =>
        for {
          // Applying empty arg asserts nothing
          _ <- Ns.ints().save
          // Nothing asserted
          _ <- Ns.ints.get.map(_ ==> List())

          // Entity 1
          _ <- Ns.ints(1).save

          // Saving an applied empty arg doesn't affect other entities
          _ <- Ns.ints().save
          _ <- Ns.ints.get.map(_ ==> List(Set(1)))

          // Entity 2
          _ <- Ns.ints(2, 3).save
          // Values of all entities are unified
          _ <- Ns.ints.get.map(_.head ==> Set(1, 2, 3))

          // Redundant duplicate values are discarded (at compile time)
          _ <- Ns.int(1).ints(1, 2, 2).save
          _ <- Ns.int_(1).ints.get.map(_.head ==> Set(1, 2))
        } yield ()
      }

      "Vararg, as variables" - core { implicit conn =>
        for {
          _ <- Ns.ints(int1).save
          _ <- Ns.ints(int2, int3).save
          _ <- Ns.ints.get.map(_.head ==> Set(int1, int2, int3))

          // Redundant duplicate values (as variables) are discarded (at runtime when variable names for same value differ)
          other2 = 2
          _ <- Ns.int(int1).ints(int1, int2, other2).save
          _ <- Ns.int_(int1).ints.get.map(_.head ==> Set(1, 2))
        } yield ()
      }

      "Iterables" - core { implicit conn =>
        for {
          // Set
          _ <- Ns.int(1).ints(Set(1, 2)).save
          _ <- Ns.int_(1).ints.get.map(_.head ==> Set(1, 2))

          // List
          _ <- Ns.int(2).ints(List(1, 2)).save
          _ <- Ns.int_(2).ints.get.map(_.head ==> Set(1, 2))


          // Redundant duplicate values are discarded
          _ <- Ns.int(5).ints(List(1, 2, 2)).save
          _ <- Ns.int_(5).ints.get.map(_.head ==> Set(1, 2))
        } yield ()
      }

      "Iterables as variable" - core { implicit conn =>
        val set0   = Set.empty[Int]
        val set12  = Set(1, 2)
        val seq122 = Seq(1, 2, 2) // Seq with duplicate values
        val set23  = Set(2, 3)
        val set45  = Set(4, 5)

        for {
          // Empty Set - no facts asserted
          _ <- Ns.int(0).ints(set0).save
          // `int` was asserted!
          _ <- Ns.int(0).ints$.get.map(_ ==> List((0, None)))

          // 1 Set
          _ <- Ns.int(1).ints(set12).save
          _ <- Ns.int_(1).ints.get.map(_.head ==> Set(1, 2))

          // Multiple Sets
          // - mixing static/variable data
          // - empty sets ignored
          _ <- Ns.int(2).ints(Set(1, 2), set0, set45).save
          _ <- Ns.int_(2).ints.get.map(_.head ==> Set(1, 2, 4, 5))

          // Redundant duplicate values (as variables) are discarded
          other2 = 2
          _ <- Ns.int(int3).ints(List(int1, int2, other2)).save
          _ <- Ns.int_(int3).ints.get.map(_.head ==> Set(1, 2))

          _ <- Ns.int(int4).ints(List(int1, int2), List(other2, int3)).save
          _ <- Ns.int_(int4).ints.get.map(_.head ==> Set(1, 2, 3))
        } yield ()
      }
    }

    "Relationships" - core { implicit conn =>
      for {
        tx <- Ns.str("273 Broadway").Ref1.int1(10700).str1("New York").Ref2.str2("USA").save
        List(addressE, streetE, countyE) = tx.eids
        _ <- addressE.touch.map(_ ==> Map(
          ":db/id" -> addressE,
          ":Ns/ref1" -> Map(
            ":db/id" -> streetE,
            ":Ref1/int1" -> 10700,
            ":Ref1/ref2" -> Map(":db/id" -> countyE, ":Ref2/str2" -> "USA"),
            ":Ref1/str1" -> "New York"),
          ":Ns/str" -> "273 Broadway"))

        _ <- Ns.str.Ref1.int1.str1.Ref2.str2.get.map(_.head ==> ("273 Broadway", 10700, "New York", "USA"))
      } yield ()
    }

    "Optional card-one" - {

      "syntax overlaps" - core { implicit conn =>
        for {
          // Equal empty results
          _ <- Ns.str("a").save
          _ <- Ns.str.int_(Nil).get.map(_ ==> List("a"))
          _ <- Ns.str.int$(None).get.map(_.head ==> ("a", None))

          // Equal mandatory results
          _ <- Ns.str("b").int(2).save
          _ <- Ns.str.int(2).get.map(_.head ==> ("b", 2))
          _ <- Ns.str.int$(Some(2)).get.map(_.head ==> ("b", Some(2)))
        } yield ()
      }

      "data/variable" - core { implicit conn =>
        for {
          _ <- Ns.str("a").int$(Some(1)).save
          _ <- Ns.str("a").int$.get.map(_.head ==> ("a", Some(1)))

          some2 = Some(2)
          _ <- Ns.str("b").int$(some2).save
          _ <- Ns.str("b").int$.get.map(_.head ==> ("b", Some(2)))

          _ <- Ns.str("c").int$(None).save
          _ <- Ns.str("c").int$.get.map(_.head ==> ("c", None))

          none = None
          _ <- Ns.str("d").int$(none).save
          _ <- Ns.str("d").int$.get.map(_.head ==> ("d", None))
        } yield ()
      }

      "attributes" - core { implicit conn =>
        for {
          _ <- Ns.ints(1).str$(Some("a")).save
          _ <- Ns.ints(2).int$(Some(1)).save
          _ <- Ns.ints(3).long$(Some(1L)).save
          _ <- Ns.ints(5).double$(Some(1.1)).save
          _ <- Ns.ints(6).bool$(Some(true)).save
          _ <- Ns.ints(7).bigInt$(Some(bigInt1)).save
          _ <- Ns.ints(8).bigDec$(Some(bigDec1)).save
          _ <- Ns.ints(9).date$(Some(date1)).save
          _ <- Ns.ints(10).uuid$(Some(uuid1)).save
          _ <- Ns.ints(11).uri$(Some(uri1)).save
          _ <- Ns.ints(12).enum$(Some(enum1)).save
          _ <- Ns.ints(13).ref1$(Some(1L)).save

          _ <- Ns.ints(1).str$(Some("a")).get.map(_.head ==> (Set(1), Some("a")))
          _ <- Ns.ints(2).int$(Some(1)).get.map(_.head ==> (Set(2), Some(1)))
          _ <- Ns.ints(3).long$(Some(1L)).get.map(_.head ==> (Set(3), Some(1L)))
          _ <- Ns.ints(5).double$(Some(1.1)).get.map(_.head ==> (Set(5), Some(1.1)))
          _ <- Ns.ints(6).bool$(Some(true)).get.map(_.head ==> (Set(6), Some(true)))
          _ <- Ns.ints(7).bigInt$(Some(bigInt1)).get.map(_.head ==> (Set(7), Some(bigInt1)))
          _ <- Ns.ints(8).bigDec$(Some(bigDec1)).get.map(_.head ==> (Set(8), Some(bigDec1)))
          _ <- Ns.ints(9).date$(Some(date1)).get.map(_.head ==> (Set(9), Some(date1)))
          _ <- Ns.ints(10).uuid$(Some(uuid1)).get.map(_.head ==> (Set(10), Some(uuid1)))
          _ <- Ns.ints(11).uri$(Some(uri1)).get.map(_.head ==> (Set(11), Some(uri1)))
          _ <- Ns.ints(12).enum$(Some(enum1)).get.map(_.head ==> (Set(12), Some(enum1)))
          _ <- Ns.ints(13).ref1$(Some(1L)).get.map(_.head ==> (Set(13), Some(1L)))
        } yield ()
      }

      "ref" - core { implicit conn =>
        for {
          _ <- Ns.int(1).Ref1.str1("a").int1$.apply(Some(10)).save
          _ <- Ns.int(1).Ref1.str1.int1.get.map(_.head ==> (1, "a", 10))

          _ <- Ns.int(2).Ref1.str1("b").int1$.apply(None).save
          _ <- Ns.int(2).Ref1.str1.int1$.get.map(_.head ==> (2, "b", None))
        } yield ()
      }

      "backref" - core { implicit conn =>
        for {
          _ <- Ns.int(0).Ref1.int1(1).Ref2.str2("b")._Ref1.Refs2.int2(2).save

          _ <- Ns.int.Ref1.int1.Ref2.str2._Ref1.Refs2.int2.get.map(_ ==> List(
            (0, 1, "b", 2)
          ))
        } yield ()
      }
    }


    "Optional card-many" - {

      "syntax overlaps" - core { implicit conn =>
        for {
          // Equal empty results
          _ <- Ns.str("a").save
          _ <- Ns.str.ints_(Nil).get.map(_ ==> List("a"))
          _ <- Ns.str.ints$(None).get.map(_.head ==> ("a", None))

          // Equal mandatory results
          _ <- Ns.str("b").ints(2).save
          _ <- Ns.str.ints(2).get.map(_.head ==> ("b", Set(2)))
          _ <- Ns.str.ints(Set(2)).get.map(_.head ==> ("b", Set(2)))
          _ <- Ns.str.ints$(Some(Set(2))).get.map(_.head ==> ("b", Some(Set(2))))
        } yield ()
      }

      "data/variable" - core { implicit conn =>
        for {
          _ <- Ns.str("a").ints$.apply(Some(Set(1, 2))).save
          _ <- Ns.str("a").ints$.get.map(_.head ==> ("a", Some(Set(1, 2))))

          some2 = Some(Set(3, 4))
          _ <- Ns.str("b").ints$(some2).save
          _ <- Ns.str("b").ints$.get.map(_.head ==> ("b", Some(Set(3, 4))))

          _ <- Ns.str("c").ints$(None).save
          _ <- Ns.str("c").ints$.get.map(_.head ==> ("c", None))

          none = None
          _ <- Ns.str("d").ints$(none).save
          _ <- Ns.str("d").ints$.get.map(_.head ==> ("d", None))
        } yield ()
      }

      "attributes" - core { implicit conn =>
        for {
          _ <- Ns.int(11).strs$(Some(Set("a", "b"))).save
          _ <- Ns.int(12).ints$(Some(Set(1, 2))).save
          _ <- Ns.int(13).longs$(Some(Set(1L, 2L))).save
          _ <- Ns.int(15).doubles$(Some(Set(1.1, 2.2))).save
          _ <- Ns.int(16).bools$(Some(Set(true, false))).save
          _ <- Ns.int(17).bigInts$(Some(Set(bigInt1, bigInt2))).save
          _ <- Ns.int(18).bigDecs$(Some(Set(bigDec1, bigDec2))).save
          _ <- Ns.int(19).dates$(Some(Set(date1, date2))).save
          _ <- Ns.int(20).uuids$(Some(Set(uuid1, uuid2))).save
          _ <- Ns.int(21).uris$(Some(Set(uri1, uri2))).save
          _ <- Ns.int(22).enums$(Some(Set(enum1, enum2))).save
          _ <- Ns.int(23).refs1$(Some(Set(1L, 2L))).save

          _ <- Ns.int(11).strs$(Some(Set("a", "b"))).get.map(_.head ==> (11, Some(Set("a", "b"))))
          _ <- Ns.int(12).ints$(Some(Set(1, 2))).get.map(_.head ==> (12, Some(Set(1, 2))))
          _ <- Ns.int(13).longs$(Some(Set(1L, 2L))).get.map(_.head ==> (13, Some(Set(1L, 2L))))
          _ <- Ns.int(15).doubles$(Some(Set(1.1, 2.2))).get.map(_.head ==> (15, Some(Set(1.1, 2.2))))
          _ <- Ns.int(16).bools$(Some(Set(true, false))).get.map(_.head ==> (16, Some(Set(true, false))))
          _ <- Ns.int(17).bigInts$(Some(Set(bigInt1, bigInt2))).get.map(_.head ==> (17, Some(Set(bigInt1, bigInt2))))
          _ <- Ns.int(18).bigDecs$(Some(Set(bigDec1, bigDec2))).get.map(_.head ==> (18, Some(Set(bigDec1, bigDec2))))
          _ <- Ns.int(19).dates$(Some(Set(date1, date2))).get.map(_.head ==> (19, Some(Set(date1, date2))))
          _ <- Ns.int(20).uuids$(Some(Set(uuid1, uuid2))).get.map(_.head ==> (20, Some(Set(uuid1, uuid2))))
          _ <- Ns.int(21).uris$(Some(Set(uri1, uri2))).get.map(_.head ==> (21, Some(Set(uri1, uri2))))
          _ <- Ns.int(22).enums$(Some(Set(enum1, enum2))).get.map(_.head ==> (22, Some(Set(enum1, enum2))))
          _ <- Ns.int(23).refs1$(Some(Set(1L, 2L))).get.map(_.head ==> (23, Some(Set(1L, 2L))))
        } yield ()
      }
    }


    "Optional map attribute" - {

      "syntax overlaps" - core { implicit conn =>
        for {
          // Equal empty results
          _ <- Ns.str("a").save
          _ <- Ns.str.intMap_(Nil).get.map(_ ==> List("a"))
          _ <- Ns.str.intMap$(None).get.map(_.head ==> ("a", None))

          // Equal mandatory results
          _ <- Ns.str("b").intMap$(Some(Map("c" -> 2))).save
          _ <- Ns.str.intMapK("c").get.map(_.head ==> ("b", 2))
          _ <- Ns.str.intMap$(Some(Map("c" -> 2))).get.map(_.head ==> ("b", Some(Map("c" -> 2))))
        } yield ()
      }

      "data/variable" - core { implicit conn =>
        for {
          _ <- Ns.int(1).intMap$(Some(Map("a" -> 11, "b" -> 12))).save
          _ <- Ns.int(1).intMap.get.map(_.head ==> (1, Map("a" -> 11, "b" -> 12)))
          _ <- Ns.int(1).intMap$.get.map(_.head ==> (1, Some(Map("a" -> 11, "b" -> 12))))
          _ <- Ns.int.intMap$(Some(Map("a" -> 11, "b" -> 12))).get.map(_.head ==> (1, Some(Map("a" -> 11, "b" -> 12))))

          some2 = Some(Map("a" -> 21, "b" -> 22))
          _ <- Ns.int(2).intMap$.apply(some2).save
          _ <- Ns.int(2).intMap.get.map(_.head ==> (2, Map("a" -> 21, "b" -> 22)))
          _ <- Ns.int(2).intMap$.get.map(_.head ==> (2, Some(Map("a" -> 21, "b" -> 22))))
          _ <- Ns.int.intMap$(some2).get.map(_.head ==> (2, Some(Map("a" -> 21, "b" -> 22))))

          map3 = Map("a" -> 31, "b" -> 32)
          _ <- Ns.int(3).intMap$(Some(map3)).save
          _ <- Ns.int(3).intMap.get.map(_.head ==> (3, Map("a" -> 31, "b" -> 32)))
          _ <- Ns.int(3).intMap$.get.map(_.head ==> (3, Some(Map("a" -> 31, "b" -> 32))))
          _ <- Ns.int.intMap$(Some(map3)).get.map(_.head ==> (3, Some(Map("a" -> 31, "b" -> 32))))

          pair = "a" -> 41
          _ <- Ns.int(4).intMap$(Some(Map(pair, "b" -> 42))).save
          _ <- Ns.int(4).intMap.get.map(_.head ==> (4, Map("a" -> 41, "b" -> 42)))
          _ <- Ns.int(4).intMap$.get.map(_.head ==> (4, Some(Map("a" -> 41, "b" -> 42))))
          _ <- Ns.int.intMap$(Some(Map(pair, "b" -> 42))).get.map(_.head ==> (4, Some(Map("a" -> 41, "b" -> 42))))

          (a, i52) = ("a", 52)
          _ <- Ns.int(5).intMap$(Some(Map(a -> 51, "b" -> i52))).save
          _ <- Ns.int(5).intMap.get.map(_.head ==> (5, Map("a" -> 51, "b" -> 52)))
          _ <- Ns.int(5).intMap$.get.map(_.head ==> (5, Some(Map("a" -> 51, "b" -> 52))))
          _ <- Ns.int.intMap$(Some(Map(a -> 51, "b" -> i52))).get.map(_.head ==> (5, Some(Map("a" -> 51, "b" -> 52))))

          _ <- Ns.int(6).intMap$(None).save
          _ <- Ns.int(6).intMap.get.map(_ ==> Nil)
          _ <- Ns.int(6).intMap$.get.map(_.head ==> (6, None))
          _ <- Ns.int.intMap$(None).get.map(_.head ==> (6, None))

          none = None
          _ <- Ns.int(7).intMap$(none).save
          _ <- Ns.int(7).intMap.get.map(_ ==> Nil)
          _ <- Ns.int(7).intMap$.get.map(_.head ==> (7, None))
          _ <- Ns.int.intMap$(none).get.map(_.sortBy(_._1) ==> List((6, None), (7, None)))
        } yield ()
      }
    }

    "Nested data not allowed in save" - core { implicit conn =>
      Ns.int(0).Refs1.*(Ref1.int1(1)).save.recover { case VerifyModelException(err) =>
        err ==> "[noNested]  Nested data structures not allowed in save molecules"
      }
    }
  }
}