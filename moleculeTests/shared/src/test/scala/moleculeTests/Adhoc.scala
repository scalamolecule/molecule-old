package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.lang.{RuntimeException, Long => jLong}
import java.util.{Collections, Date, UUID, ArrayList => jArrayList, Comparator => jComparator, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import boopickle.Default._
import molecule.core.ast.elements._
import molecule.core.exceptions.{MoleculeException, TxFnException}
import molecule.core.marshalling.unpackAttr.String2cast
import molecule.core.marshalling.unpackers.Packed2EntityMap
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out3.transactFn
import molecule.datomic.api.out6.m
import molecule.datomic.base.transform.Model2Query
import moleculeTests.dataModels.core.base.schema.CoreTestSchema
import moleculeTests.tests.core.runtime.EntityList.{uri1, uri2, uuid1, uuid2}
import moleculeTests.tests.core.runtime.EntityMap.{bigDec1, bigDec2, bigInt1, bigInt2, date1, date2, enum1, enum2, uri1, uri2, uuid1, uuid2}
import scala.util.control.NonFatal


object Adhoc extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {

    "core" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn

        e <- Ns.ref1(1L).save.map(_.eid)
        _ <- e.touchList.map(_ ==> List(":db/id" -> e, ":Ns/ref1" -> ":db/add"))


//        e1 <- Ns.strMap("a" -> "aa", "b" -> "bb").save.map(_.eid)
//        _ <- e1.touch.map(_ ==> Map(":db/id" -> e1, ":Ns/strMap" -> List("a@aa", "b@bb")))
//
//        e2 <- Ns.intMap("a" -> 1, "b" -> 2).save.map(_.eid)
//        _ <- e2.touch.map(_ ==> Map(":db/id" -> e2, ":Ns/intMap" -> List("a@1", "b@2")))
//
//        e3 <- Ns.longMap("a" -> 1L, "b" -> 2L).save.map(_.eid)
//        _ <- e3.touch.map(_ ==> Map(":db/id" -> e3, ":Ns/longMap" -> List("a@1", "b@2")))
//
//        e4 <- Ns.doubleMap("a" -> 1.1, "b" -> 2.2).save.map(_.eid)
//        _ <- e4.touch.map(_ ==> Map(":db/id" -> e4, ":Ns/doubleMap" -> List("a@1.1", "b@2.2")))
//
//        e5 <- Ns.boolMap("a" -> true, "b" -> false).save.map(_.eid)
//        _ <- e5.touch.map(_ ==> Map(":db/id" -> e5, ":Ns/boolMap" -> List("a@true", "b@false")))
//
//        e6 <- Ns.dateMap("a" -> date1, "b" -> date2).save.map(_.eid)
//        _ <- e6.touch.map(_ ==> Map(":db/id" -> e6, ":Ns/dateMap" -> List("a@2001-07-01", "b@2002-01-01")))
//
//        e7 <- Ns.uuidMap("a" -> uuid1, "b" -> uuid2).save.map(_.eid)
//        _ <- e7.touch.map(_ ==> Map(":db/id" -> e7, ":Ns/uuidMap" -> List(s"a@$uuid1", s"b@$uuid2")))
//
//        e8 <- Ns.uriMap("a" -> uri1, "b" -> uri2).save.map(_.eid)
//        _ <- e8.touch.map(_ ==> Map(":db/id" -> e8, ":Ns/uriMap" -> List(s"a@$uri1", s"b@$uri2")))
//
//        e9 <- Ns.bigIntMap("a" -> bigInt1, "b" -> bigInt2).save.map(_.eid)
//        _ <- e9.touch.map(_ ==> Map(":db/id" -> e9, ":Ns/bigIntMap" -> List(s"a@1", s"b@2")))
//
//        e10 <- Ns.bigDecMap("a" -> bigDec1, "b" -> bigDec2).save.map(_.eid)
//        _ <- e10.touch.map(_ ==> Map(":db/id" -> e10, ":Ns/bigDecMap" -> List(s"a@1.0", s"b@2.0")))

      } yield ()
    }

//    "core2" - core { implicit futConn =>
//      for {
//        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
//        conn <- futConn
//
//
////        e1 <- Ns.strMap("a" -> "aa", "b" -> "bb").save.map(_.eid)
////        _ <- e1.touchList.map(_ ==> List(":db/id" -> e1, ":Ns/strMap" -> List("a@aa", "b@bb")))
////
////        e2 <- Ns.intMap("a" -> 1, "b" -> 2).save.map(_.eid)
////        _ <- e2.touchList.map(_ ==> List(":db/id" -> e2, ":Ns/intMap" -> List("a@1", "b@2")))
////
////        e3 <- Ns.longMap("a" -> 1L, "b" -> 2L).save.map(_.eid)
////        _ <- e3.touchList.map(_ ==> List(":db/id" -> e3, ":Ns/longMap" -> List("a@1", "b@2")))
////
////        e4 <- Ns.doubleMap("a" -> 1.1, "b" -> 2.2).save.map(_.eid)
////        _ <- e4.touchList.map(_ ==> List(":db/id" -> e4, ":Ns/doubleMap" -> List("a@1.1", "b@2.2")))
////
////        e5 <- Ns.boolMap("a" -> true, "b" -> false).save.map(_.eid)
////        _ <- e5.touchList.map(_ ==> List(":db/id" -> e5, ":Ns/boolMap" -> List("a@true", "b@false")))
////
////        e6 <- Ns.dateMap("a" -> date1, "b" -> date2).save.map(_.eid)
////        _ <- e6.touchList.map(_ ==> List(":db/id" -> e6, ":Ns/dateMap" -> List("a@2001-07-01", "b@2002-01-01")))
////
////        e7 <- Ns.uuidMap("a" -> uuid1, "b" -> uuid2).save.map(_.eid)
////        _ <- e7.touchList.map(_ ==> List(":db/id" -> e7, ":Ns/uuidMap" -> List(s"a@$uuid1", s"b@$uuid2")))
////
////        e8 <- Ns.uriMap("a" -> uri1, "b" -> uri2).save.map(_.eid)
////        _ <- e8.touchList.map(_ ==> List(":db/id" -> e8, ":Ns/uriMap" -> List(s"a@$uri1", s"b@$uri2")))
////
////        e9 <- Ns.bigIntMap("a" -> bigInt1, "b" -> bigInt2).save.map(_.eid)
////        _ <- e9.touchList.map(_ ==> List(":db/id" -> e9, ":Ns/bigIntMap" -> List(s"a@1", s"b@2")))
////
////        e10 <- Ns.bigDecMap("a" -> bigDec1, "b" -> bigDec2).save.map(_.eid)
////        _ <- e10.touchList.map(_ ==> List(":db/id" -> e10, ":Ns/bigDecMap" -> List(s"a@1.0", s"b@2.0")))
//
//      } yield ()
//    }

    //    "adhoc" - products { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
    //
    //      for {
    //
    //      } yield ()
    //    }


    //    "mbrainz" - mbrainz { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.mbrainz.dsl.MBrainz._
    //      val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
    //
    //      for {
    //        ledZeppelin <- Artist.e.gid_(ledZeppelinUUID).get
    //        mccartney <- Artist.e.gid_(UUID.fromString("ba550d0e-adac-4864-b88b-407cab5e76af")).get
    //        darkSideOfTheMoon <- Release.e.gid_(UUID.fromString("24824319-9bb8-3d1e-a2c5-b8b864dafd1b")).get
    //        dylanHarrisonSessions <- Release.e.gid_(UUID.fromString("67bbc160-ac45-4caf-baae-a7e9f5180429")).get
    //        concertForBangladesh <- Release.e.gid_(UUID.fromString("f3bdff34-9a85-4adc-a014-922eef9cdaa5")).get
    //        dylanHarrisonCd <- Release(dylanHarrisonSessions).media.get
    //        ghostRiders <- Release(dylanHarrisonSessions).Media.Tracks.e.position_(11).get
    //        gb <- Country.e.name_("United Kingdom").get
    //        georgeHarrison <- Artist.e.name_("George Harrison").get
    //        bobDylan <- Artist.e.name_("Bob Dylan").get
    //
    //
    //      } yield ()
    //    }


    //    "adhoc" - bidirectional { implicit conn =>
    //    for {
    //      _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //    } yield ()
    //  }
  }
}
