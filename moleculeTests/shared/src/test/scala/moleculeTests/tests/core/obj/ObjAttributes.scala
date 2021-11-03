package moleculeTests.tests.core.obj

import molecule.core.util.Helpers
import molecule.datomic.api.in1_out3._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object ObjAttributes extends AsyncTestSuite with Helpers {

  lazy val tests = Tests {

    "Empty result set" - core { implicit conn =>
      Ns.str.getObjs.map(_ ==> Nil)
    }

    "Card one" - core { implicit conn =>
      for {
        // Insert single value for one cardinality-1 attribute
        refId <- Ref1.int1(1).save.map(_.eid)
        _ <- Ns.str insert "a"
        _ <- Ns.int insert 1
        _ <- Ns.long insert 1L
        _ <- Ns.double insert 1.1
        _ <- Ns.bool insert true
        _ <- Ns.date insert date1
        _ <- Ns.uuid insert uuid1
        _ <- Ns.uri insert uri1
        _ <- Ns.bigInt insert bigInt1
        _ <- Ns.bigDec insert bigDec1
        _ <- Ns.enumm insert enum1
        _ <- Ns.ref1(refId).save

        // Get all objects
        _ <- Ns.str.getObjs.map(_.head.str ==> "a")
        // Convenience method to get head of object list
        _ <- Ns.str.getObj.map(_.str ==> "a")

        _ <- Ns.int.getObj.map(_.int ==> 1)
        _ <- Ns.long.getObj.map(_.long ==> 1L)
        _ <- Ns.double.getObj.map(_.double ==> 1.1)
        _ <- Ns.bool.getObj.map(_.bool ==> true)
        _ <- Ns.date.getObj.map(_.date ==> date1)
        _ <- Ns.uuid.getObj.map(_.uuid ==> uuid1)
        _ <- Ns.uri.getObj.map(_.uri ==> uri1)
        _ <- Ns.bigInt.getObj.map(_.bigInt ==> bigInt1)
        _ <- Ns.bigDec.getObj.map(_.bigDec ==> bigDec1)
        _ <- Ns.enumm.getObj.map(_.enumm ==> enum1)
        _ <- Ns.ref1.getObj.map(_.ref1 ==> refId)
      } yield ()
    }


    "Card many" - core { implicit conn =>
      for {
        List(refId1, refId2) <- Ref1.int1.insert(1, 2).map(_.eids)
        _ <- Ns.strs insert Set("", "a", "b")
        _ <- Ns.ints insert Set(1, 2)
        _ <- Ns.longs insert Set(1L, 2L)
        _ <- Ns.doubles insert Set(1.1, 2.2)
        _ <- Ns.bools insert Set(true, false)
        _ <- Ns.dates insert Set(date1, date2)
        _ <- Ns.uuids insert Set(uuid1)
        _ <- Ns.uris insert Set(uri1, uri2)
        _ <- Ns.bigInts insert Set(bigInt1, bigInt2)
        _ <- Ns.bigDecs insert Set(bigDec1, bigDec2)
        _ <- Ns.enums insert Set(enum1, enum2)
        _ <- Ns.refs1(refId1, refId2).save

        _ <- Ns.strs.getObj.map(_.strs ==> Set("", "a", "b"))
        _ <- Ns.ints.getObj.map(_.ints ==> Set(1, 2))
        _ <- Ns.longs.getObj.map(_.longs ==> Set(1L, 2L))
        _ <- Ns.doubles.getObj.map(_.doubles ==> Set(1.1, 2.2))
        _ <- Ns.bools.getObj.map(_.bools ==> Set(true, false))
        _ <- Ns.dates.getObj.map(_.dates ==> Set(date1, date2))
        _ <- Ns.uuids.getObj.map(_.uuids ==> Set(uuid1))
        _ <- Ns.uris.getObj.map(_.uris ==> Set(uri1, uri2))
        _ <- Ns.bigInts.getObj.map(_.bigInts ==> Set(bigInt1, bigInt2))
        _ <- Ns.enums.getObj.map(_.enums ==> Set(enum1, enum2))
        _ <- Ns.refs1.getObj.map(_.refs1 ==> Set(refId1, refId2))
      } yield ()
    }


    "Card map" - core { implicit conn =>
      for {
        _ <- Ns.strMap insert Map("a" -> "A", "b" -> "B")
        _ <- Ns.intMap insert Map("a" -> 1, "b" -> 2)
        _ <- Ns.longMap insert Map("a" -> 1L, "b" -> 2L)
        _ <- Ns.doubleMap insert Map("a" -> 1.1, "b" -> 2.2)
        _ <- Ns.boolMap insert Map("a" -> true, "b" -> false)
        _ <- Ns.dateMap insert Map("a" -> date1, "b" -> date2)
        _ <- Ns.uuidMap insert Map("a" -> uuid1, "b" -> uuid2)
        _ <- Ns.uriMap insert Map("a" -> uri1, "b" -> uri2)
        _ <- Ns.bigIntMap insert Map("a" -> bigInt1, "b" -> bigInt2)
        _ <- Ns.bigDecMap insert Map("a" -> bigDec1, "b" -> bigDec2)

        _ <- Ns.strMap.getObj.map(_.strMap ==>  Map("a" -> "A", "b" -> "B"))
        _ <- Ns.intMap.getObj.map(_.intMap ==>  Map("a" -> 1, "b" -> 2))
        _ <- Ns.longMap.getObj.map(_.longMap ==>  Map("a" -> 1L, "b" -> 2L))
        _ <- Ns.doubleMap.getObj.map(_.doubleMap ==>  Map("a" -> 1.1, "b" -> 2.2))
        _ <- Ns.boolMap.getObj.map(_.boolMap ==>  Map("a" -> true, "b" -> false))
        _ <- Ns.dateMap.getObj.map(_.dateMap ==>  Map("a" -> date1, "b" -> date2))
        _ <- Ns.uuidMap.getObj.map(_.uuidMap ==>  Map("a" -> uuid1, "b" -> uuid2))
        _ <- Ns.uriMap.getObj.map(_.uriMap ==>  Map("a" -> uri1, "b" -> uri2))
        _ <- Ns.bigIntMap.getObj.map(_.bigIntMap ==>  Map("a" -> bigInt1, "b" -> bigInt2))
        _ <- Ns.bigDecMap.getObj.map(_.bigDecMap ==>  Map("a" -> bigDec1, "b" -> bigDec2))
      } yield ()
    }


    "Card one optional" - core { implicit conn =>
      for {
        refId <- Ref1.int1(1).save.map(_.eid)
        _ <- Ns.int.str$ insert List((1, None), (1, Some("a")))
        _ <- Ns.long.int$ insert List((2L, None), (2L, Some(20)))
        _ <- Ns.int.long$ insert List((3, None), (3, Some(1L)))
        _ <- Ns.int.double$ insert List((4, None), (4, Some(1.1)))
        _ <- Ns.int.bool$ insert List((5, None), (5, Some(true)))
        _ <- Ns.int.date$ insert List((6, None), (6, Some(date1)))
        _ <- Ns.int.uuid$ insert List((7, None), (7, Some(uuid1)))
        _ <- Ns.int.uri$ insert List((8, None), (8, Some(uri1)))
        _ <- Ns.int.bigInt$ insert List((9, None), (9, Some(bigInt1)))
        _ <- Ns.int.bigDec$ insert List((10, None), (10, Some(bigDec1)))
        _ <- Ns.int.enumm$ insert List((11, None), (11, Some(enum1)))
        _ <- Ns.int.ref1$ insert List((12, None), (12, Some(refId)))


        _ <- Ns.int(1).str$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 1
          o1.str$ ==> None
          o2.int ==> 1
          o2.str$ ==> Some("a")
        }
        _ <- Ns.int(1).str.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 1
          o.str ==> "a"
        }

        _ <- Ns.long(2L).int$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.long ==> 2L
          o1.int$ ==> None
          o2.long ==> 2L
          o2.int$ ==> Some(20)
        }
        _ <- Ns.long(2L).int.getObjs.map { oo =>
          val o = oo.head
          o.long ==> 2L
          o.int ==> 20
        }

        _ <- Ns.int(3).long$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 3
          o1.long$ ==> None
          o2.int ==> 3
          o2.long$ ==> Some(1L)
        }
        _ <- Ns.int(3).long.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 3
          o.long ==> 1L
        }

        _ <- Ns.int(4).double$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 4
          o1.double$ ==> None
          o2.int ==> 4
          o2.double$ ==> Some(1.1)
        }
        _ <- Ns.int(4).double.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 4
          o.double ==> 1.1
        }

        _ <- Ns.int(5).bool$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 5
          o1.bool$ ==> None
          o2.int ==> 5
          o2.bool$ ==> Some(true)
        }
        _ <- Ns.int(5).bool.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 5
          o.bool ==> true
        }

        _ <- Ns.int(6).date$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 6
          o1.date$ ==> None
          o2.int ==> 6
          o2.date$ ==> Some(date1)
        }
        _ <- Ns.int(6).date.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 6
          o.date ==> date1
        }

        _ <- Ns.int(7).uuid$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 7
          o1.uuid$ ==> None
          o2.int ==> 7
          o2.uuid$ ==> Some(uuid1)
        }
        _ <- Ns.int(7).uuid.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 7
          o.uuid ==> uuid1
        }

        _ <- Ns.int(8).uri$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 8
          o1.uri$ ==> None
          o2.int ==> 8
          o2.uri$ ==> Some(uri1)
        }
        _ <- Ns.int(8).uri.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 8
          o.uri ==> uri1
        }

        _ <- Ns.int(9).bigInt$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 9
          o1.bigInt$ ==> None
          o2.int ==> 9
          o2.bigInt$ ==> Some(bigInt1)
        }
        _ <- Ns.int(9).bigInt.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 9
          o.bigInt ==> bigInt1
        }

        _ <- Ns.int(10).bigDec$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 10
          o1.bigDec$ ==> None
          o2.int ==> 10
          o2.bigDec$ ==> Some(bigDec1)
        }
        _ <- Ns.int(10).bigDec.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 10
          o.bigDec ==> bigDec1
        }

        _ <- Ns.int(11).enumm$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 11
          o1.enumm$ ==> None
          o2.int ==> 11
          o2.enumm$ ==> Some(enum1)
        }
        _ <- Ns.int(11).enumm.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 11
          o.enumm ==> enum1
        }

        _ <- Ns.int(12).ref1$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 12
          o1.ref1$ ==> None
          o2.int ==> 12
          o2.ref1$ ==> Some(refId)
        }
        _ <- Ns.int(12).ref1.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 12
          o.ref1 ==> refId
        }
      } yield ()
    }


    "Card many optional" - core { implicit conn =>
      for {
        List(refId1, refId2) <- Ref1.int1.insert(1, 2).map(_.eids)
        _ <- Ns.int.strs$ insert List((1, None), (1, Some(Set("a", "b"))))
        _ <- Ns.int.ints$ insert List((2, None), (2, Some(Set(1, 2))))
        _ <- Ns.int.longs$ insert List((3, None), (3, Some(Set(1L, 2L))))
        _ <- Ns.int.doubles$ insert List((4, None), (4, Some(Set(1.1, 2.2))))
        _ <- Ns.int.bools$ insert List((5, None), (5, Some(Set(true, false))))
        _ <- Ns.int.dates$ insert List((6, None), (6, Some(Set(date1, date2))))
        _ <- Ns.int.uuids$ insert List((7, None), (7, Some(Set(uuid1))))
        _ <- Ns.int.uris$ insert List((8, None), (8, Some(Set(uri1, uri2))))
        _ <- Ns.int.bigInts$ insert List((9, None), (9, Some(Set(bigInt1, bigInt2))))
        _ <- Ns.int.bigDecs$ insert List((10, None), (10, Some(Set(bigDec1, bigDec2))))
        _ <- Ns.int.enums$ insert List((11, None), (11, Some(Set(enum1, enum2))))
        _ <- Ns.int.refs1$ insert List((12, None), (12, Some(Set(refId1, refId2))))


        _ <- Ns.int(1).strs$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 1
          o1.strs$ ==> None
          o2.int ==> 1
          o2.strs$ ==> Some(Set("a", "b"))
        }
        _ <- Ns.int(1).strs.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 1
          o.strs ==> Set("a", "b")
        }

        _ <- Ns.int(2).ints$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 2
          o1.ints$ ==> None
          o2.int ==> 2
          o2.ints$ ==> Some(Set(1, 2))
        }
        _ <- Ns.int(2).ints.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 2
          o.ints ==> Set(1, 2)
        }

        _ <- Ns.int(3).longs$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 3
          o1.longs$ ==> None
          o2.int ==> 3
          o2.longs$ ==> Some(Set(1L, 2L))
        }
        _ <- Ns.int(3).longs.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 3
          o.longs ==> Set(1L, 2L)
        }

        _ <- Ns.int(4).doubles$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 4
          o1.doubles$ ==> None
          o2.int ==> 4
          o2.doubles$ ==> Some(Set(1.1, 2.2))
        }
        _ <- Ns.int(4).doubles.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 4
          o.doubles ==> Set(1.1, 2.2)
        }

        _ <- Ns.int(5).bools$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 5
          o1.bools$ ==> None
          o2.int ==> 5
          o2.bools$ ==> Some(Set(true, false))
        }
        _ <- Ns.int(5).bools.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 5
          o.bools ==> Set(true, false)
        }

        _ <- Ns.int(6).dates$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 6
          o1.dates$ ==> None
          o2.int ==> 6
          o2.dates$ ==> Some(Set(date1, date2))
        }
        _ <- Ns.int(6).dates.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 6
          o.dates ==> Set(date1, date2)
        }

        _ <- Ns.int(7).uuids$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 7
          o1.uuids$ ==> None
          o2.int ==> 7
          o2.uuids$ ==> Some(Set(uuid1))
        }
        _ <- Ns.int(7).uuids.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 7
          o.uuids ==> Set(uuid1)
        }

        _ <- Ns.int(8).uris$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 8
          o1.uris$ ==> None
          o2.int ==> 8
          o2.uris$ ==> Some(Set(uri1, uri2))
        }
        _ <- Ns.int(8).uris.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 8
          o.uris ==> Set(uri1, uri2)
        }

        _ <- Ns.int(9).bigInts$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 9
          o1.bigInts$ ==> None
          o2.int ==> 9
          o2.bigInts$ ==> Some(Set(bigInt1, bigInt2))
        }
        _ <- Ns.int(9).bigInts.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 9
          o.bigInts ==> Set(bigInt1, bigInt2)
        }

        _ <- Ns.int(10).bigDecs$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 10
          o1.bigDecs$ ==> None
          o2.int ==> 10
          o2.bigDecs$ ==> Some(Set(bigDec1, bigDec2))
        }
        _ <- Ns.int(10).bigDecs.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 10
          o.bigDecs ==> Set(bigDec1, bigDec2)
        }

        _ <- Ns.int(11).enums$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 11
          o1.enums$ ==> None
          o2.int ==> 11
          o2.enums$ ==> Some(Set(enum1, enum2))
        }
        _ <- Ns.int(11).enums.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 11
          o.enums ==> Set(enum1, enum2)
        }

        _ <- Ns.int(12).refs1$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 12
          o1.refs1$ ==> None
          o2.int ==> 12
          o2.refs1$ ==> Some(Set(refId1, refId2))
        }
        _ <- Ns.int(12).refs1.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 12
          o.refs1 ==> Set(refId1, refId2)
        }
      } yield ()
    }


    "Card map optional" - core { implicit conn =>
      for {
        _ <- Ns.int.strMap$ insert List((1, None), (1, Some(Map("a" -> "A", "b" -> "B"))))
        _ <- Ns.int.intMap$ insert List((2, None), (2, Some(Map("a" -> 1, "b" -> 2))))
        _ <- Ns.int.longMap$ insert List((3, None), (3, Some(Map("a" -> 1L, "b" -> 2L))))
        _ <- Ns.int.doubleMap$ insert List((4, None), (4, Some(Map("a" -> 1.1, "b" -> 2.2))))
        _ <- Ns.int.boolMap$ insert List((5, None), (5, Some(Map("a" -> true, "b" -> false))))
        _ <- Ns.int.dateMap$ insert List((6, None), (6, Some(Map("a" -> date1, "b" -> date2))))
        _ <- Ns.int.uuidMap$ insert List((7, None), (7, Some(Map("a" -> uuid1, "b" -> uuid2))))
        _ <- Ns.int.uriMap$ insert List((8, None), (8, Some(Map("a" -> uri1, "b" -> uri2))))
        _ <- Ns.int.bigIntMap$ insert List((9, None), (9, Some(Map("a" -> bigInt1, "b" -> bigInt2))))
        _ <- Ns.int.bigDecMap$ insert List((10, None), (10, Some(Map("a" -> bigDec1, "b" -> bigDec2))))


        _ <- Ns.int(1).strMap$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 1
          o1.strMap$ ==> None
          o2.int ==> 1
          o2.strMap$ ==> Some(Map("a" -> "A", "b" -> "B"))
        }
        _ <- Ns.int(1).strMap.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 1
          o.strMap ==> Map("a" -> "A", "b" -> "B")
        }

        _ <- Ns.int(2).intMap$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 2
          o1.intMap$ ==> None
          o2.int ==> 2
          o2.intMap$ ==> Some(Map("a" -> 1, "b" -> 2))
        }
        _ <- Ns.int(2).intMap.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 2
          o.intMap ==> Map("a" -> 1, "b" -> 2)
        }

        _ <- Ns.int(3).longMap$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 3
          o1.longMap$ ==> None
          o2.int ==> 3
          o2.longMap$ ==> Some(Map("a" -> 1L, "b" -> 2L))
        }
        _ <- Ns.int(3).longMap.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 3
          o.longMap ==> Map("a" -> 1L, "b" -> 2L)
        }

        _ <- Ns.int(4).doubleMap$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 4
          o1.doubleMap$ ==> None
          o2.int ==> 4
          o2.doubleMap$ ==> Some(Map("a" -> 1.1, "b" -> 2.2))
        }
        _ <- Ns.int(4).doubleMap.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 4
          o.doubleMap ==> Map("a" -> 1.1, "b" -> 2.2)
        }

        _ <- Ns.int(5).boolMap$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 5
          o1.boolMap$ ==> None
          o2.int ==> 5
          o2.boolMap$ ==> Some(Map("a" -> true, "b" -> false))
        }
        _ <- Ns.int(5).boolMap.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 5
          o.boolMap ==> Map("a" -> true, "b" -> false)
        }

        _ <- Ns.int(6).dateMap$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 6
          o1.dateMap$ ==> None
          o2.int ==> 6
          o2.dateMap$ ==> Some(Map("a" -> date1, "b" -> date2))
        }
        _ <- Ns.int(6).dateMap.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 6
          o.dateMap ==> Map("a" -> date1, "b" -> date2)
        }

        _ <- Ns.int(7).uuidMap$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 7
          o1.uuidMap$ ==> None
          o2.int ==> 7
          o2.uuidMap$ ==> Some(Map("a" -> uuid1, "b" -> uuid2))
        }
        _ <- Ns.int(7).uuidMap.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 7
          o.uuidMap ==> Map("a" -> uuid1, "b" -> uuid2)
        }

        _ <- Ns.int(8).uriMap$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 8
          o1.uriMap$ ==> None
          o2.int ==> 8
          o2.uriMap$ ==> Some(Map("a" -> uri1, "b" -> uri2))
        }
        _ <- Ns.int(8).uriMap.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 8
          o.uriMap ==> Map("a" -> uri1, "b" -> uri2)
        }

        _ <- Ns.int(9).bigIntMap$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 9
          o1.bigIntMap$ ==> None
          o2.int ==> 9
          o2.bigIntMap$ ==> Some(Map("a" -> bigInt1, "b" -> bigInt2))
        }
        _ <- Ns.int(9).bigIntMap.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 9
          o.bigIntMap ==> Map("a" -> bigInt1, "b" -> bigInt2)
        }

        _ <- Ns.int(10).bigDecMap$.getObjs.map { oo =>
          val List(o1, o2) = oo
          o1.int ==> 10
          o1.bigDecMap$ ==> None
          o2.int ==> 10
          o2.bigDecMap$ ==> Some(Map("a" -> bigDec1, "b" -> bigDec2))
        }
        _ <- Ns.int(10).bigDecMap.getObjs.map { oo =>
          val o = oo.head
          o.int ==> 10
          o.bigDecMap ==> Map("a" -> bigDec1, "b" -> bigDec2)
        }
      } yield ()
    }
  }
}
