package moleculeTests

import java.util
import molecule.datomic.api.in3_out12._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.lang.{RuntimeException, Long => jLong}
import java.net.URI
import java.util.{Collections, Date, UUID, ArrayList => jArrayList, Comparator => jComparator, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.ast.elements._
import molecule.core.exceptions.{MoleculeException, TxFnException}
import molecule.core.marshalling.unpackAttr.String2cast
import molecule.core.marshalling.unpackers.Packed2EntityMap
import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.Helpers
import molecule.datomic.base.ast.query.{KW, Placeholder, Query}
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.datomic.base.transform.exception.Model2TransactionException
import moleculeTests.Adhoc.{bigDec1, bigInt1, bool1, date1, double1, enum1, int1, long1, r1, uri1, uuid1}
//import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import scala.util.control.NonFatal


object Adhoc extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "adhoc" - core { implicit futConn =>

      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn



        e1 <- Ns.str.int.long.double.bool.date.uuid.uri.bigInt.bigDec.enum.ref1.insert(
          str1, int1, long1, double1, bool1, date1, uuid1, uri1, bigInt1, bigDec1, enum1, r1
        ).map(_.eid)

//        // All attribute assertions with value "a" of entity e1
//        _ <- Ns(e1).a.v.get.map(_.sortBy(_._1) ==> List(
//          (":Ns/bigDec", bigDec1),
//          (":Ns/bigInt", bigInt1),
//          (":Ns/bool", bool1),
//          (":Ns/date", date1),
//          (":Ns/double", double1),
//          (":Ns/enum", enum1),
//          (":Ns/int", int1),
//          (":Ns/long", long1),
//          (":Ns/ref1", r1),
//          (":Ns/str", str1),
//          (":Ns/uri", uri1),
//          (":Ns/uuid", uuid1),
//        ))
//
//        List((:Ns/bigDec,1.0), (:Ns/bigInt,1), (:Ns/bool,true), (:Ns/date,Sun Jul 01 00:00:00 GMT 2001), (:Ns/double,1), (:Ns/enum,17592186045418), (:Ns/int,1), (:Ns/long,1), (:Ns/ref1,17194139534366), (:Ns/str,a), (:Ns/uri,uri1), (:Ns/uuid,053efffc-3748-4b53-ac07-dc2972e6e562))
//        List((:Ns/bigDec,1), (:Ns/bigInt,1), (:Ns/bool,true), (:Ns/date,Sun Jul 01 00:00:00 GMT 2001), (:Ns/double,1), (:Ns/enum,enum1), (:Ns/int,1), (:Ns/long,1), (:Ns/ref1,17194139534366), (:Ns/str,a), (:Ns/uri,uri1), (:Ns/uuid,053efffc-3748-4b53-ac07-dc2972e6e562))

//        _ <- Ns(e1).a.v(int1).inspectGet
//        _ <- Ns(e1).a.v(long1).inspectGet
//        _ <- Ns(e1).a.v(double1).inspectGet
//        _ <- Ns(e1).a.v(bigInt1).inspectGet
//
//        // Ints are saved as Longs in the database, and will also match BigInt's within Int/Long range
        wholeNumbers = List(
          (":Ns/bigInt", bigInt1),
          (":Ns/long", long1),
          (":Ns/int", int1),
        )
//        _ <- Ns(e1).a.v(int1).get.map(_ ==> wholeNumbers)
        _ <- Ns(e1).a.v(long1).get.map(_ ==> wholeNumbers)
//        _ <- Ns(e1).a.v(bigInt1).get.map(_ ==> wholeNumbers)


//        _ <- Ns(e1).a.v(str1).get.map(_ ==> List((":Ns/str", str1)))
//        _ <- Ns(e1).a.v(double1).get.map(_ ==> List((":Ns/double", double1)))









//        _ <- Ns(e1).a.v(bool1).get.map(_ ==> List((":Ns/bool", bool1)))
//        _ <- Ns(e1).a.v(date1).get.map(_ ==> List((":Ns/date", date1)))
//        _ <- Ns(e1).a.v(uuid1).get.map(_ ==> List((":Ns/uuid", uuid1)))
//        _ <- Ns(e1).a.v(uri1).get.map(_ ==> List((":Ns/uri", uri1)))
//        _ <- Ns(e1).a.v(bigDec1).get.map(_ ==> List((":Ns/bigDec", bigDec1)))
//        _ <- Ns(e1).a.v(enum1).get.map(_ ==> List((":Ns/enum", enum1)))
//        _ <- Ns(e1).a.v(r1).get.map(_ ==> List((":Ns/ref1", r1)))
//
//        _ <- Ns(e1).a.v("a").get.map(_ ==> List((":Ns/str", str1)))
//        _ <- Ns(e1).a.v(1).get.map(_ ==> List((":Ns/int", int1)))
//        _ <- Ns(e1).a.v(1L).get.map(_ ==> List((":Ns/long", long1)))
//        _ <- Ns(e1).a.v(1.0).get.map(_ ==> List((":Ns/double", double1)))
//        _ <- Ns(e1).a.v(true).get.map(_ ==> List((":Ns/bool", bool1)))
//        _ <- Ns(e1).a.v(BigInt(1)).get.map(_ ==> List((":Ns/bigInt", bigInt1)))
//        _ <- Ns(e1).a.v(BigDecimal(1.0)).get.map(_ ==> List((":Ns/bigDec", bigDec1)))
//        _ <- Ns(e1).a.v("enum1").get.map(_ ==> List((":Ns/enum", enum1)))
//        _ <- Ns(e1).a.v(17194139534366L).get.map(_ ==> List((":Ns/ref1", r1)))



//        e2 <- Ns.strs.ints.longs.doubles.bools.dates.uuids.uris.bigInts.bigDecs.enums.refs1.insert(
//          strs1, ints1, longs1, doubles1, bools1, dates1, uuids1, uris1, bigInts1, bigDecs1, enums1, rs1
//        ).map(_.eid)
//
//        // All attribute assertions with value "a" of entity e1
//        _ <- Ns(e2).a.v.get.map(_.sortBy(_._1) ==> List(
//          (":Ns/bigDecs", bigDecs1),
//          (":Ns/bigInts", bigInts1),
//          (":Ns/bools", bools1),
//          (":Ns/dates", dates1),
//          (":Ns/doubles", doubles1),
//          (":Ns/enums", enums1),
//          (":Ns/ints", ints1),
//          (":Ns/longs", longs1),
//          (":Ns/refs1", rs1),
//          (":Ns/strs", strs1),
//          (":Ns/uris", uris1),
//          (":Ns/uuids", uuids1),
//        ))
//
//        _ <- Ns(e2).a.v(str1).get.map(_ ==> List((":Ns/strs", strs1)))
//        _ <- Ns(e2).a.v(int1).get.map(_ ==> List((":Ns/ints", ints1)))
//        _ <- Ns(e2).a.v(long1).get.map(_ ==> List((":Ns/longs", longs1)))
//        _ <- Ns(e2).a.v(double1).get.map(_ ==> List((":Ns/doubles", doubles1)))
//        _ <- Ns(e2).a.v(bool1).get.map(_ ==> List((":Ns/bools", bools1)))
//        _ <- Ns(e2).a.v(date1).get.map(_ ==> List((":Ns/dates", dates1)))
//        _ <- Ns(e2).a.v(uuid1).get.map(_ ==> List((":Ns/uuids", uuids1)))
//        _ <- Ns(e2).a.v(uri1).get.map(_ ==> List((":Ns/uris", uris1)))
//        _ <- Ns(e2).a.v(bigInt1).get.map(_ ==> List((":Ns/bigInts", bigInts1)))
//        _ <- Ns(e2).a.v(bigDec1).get.map(_ ==> List((":Ns/bigDecs", bigDecs1)))
//        _ <- Ns(e2).a.v(enum1).get.map(_ ==> List((":Ns/enums", enums1)))
//        _ <- Ns(e2).a.v(r1).get.map(_ ==> List((":Ns/refs1", rs1)))
//
//        _ <- Ns(e2).a.v("a").get.map(_ ==> List((":Ns/strs", strs1)))
//        _ <- Ns(e2).a.v(1).get.map(_ ==> List((":Ns/ints", ints1)))
//        _ <- Ns(e2).a.v(1L).get.map(_ ==> List((":Ns/longs", longs1)))
//        _ <- Ns(e2).a.v(1.0).get.map(_ ==> List((":Ns/doubles", doubles1)))
//        _ <- Ns(e2).a.v(true).get.map(_ ==> List((":Ns/bools", bools1)))
//        _ <- Ns(e2).a.v(BigInt(1)).get.map(_ ==> List((":Ns/bigInts", bigInts1)))
//        _ <- Ns(e2).a.v(BigDecimal(1.0)).get.map(_ ==> List((":Ns/bigDecs", bigDecs1)))
//        _ <- Ns(e2).a.v("enum1").get.map(_ ==> List((":Ns/enums", enums1)))
//        _ <- Ns(e2).a.v(17194139534366L).get.map(_ ==> List((":Ns/refs1", rs1)))
//
//
//
//        e3 <- Ns.bigDecMap.bigIntMap.boolMap.dateMap.doubleMap.intMap.longMap.strMap.uriMap.uuidMap.insert(
//          Map("key" -> bigDec1),
//          Map("key" -> bigInt1),
//          Map("key" -> bool1),
//          Map("key" -> date1),
//          Map("key" -> double1),
//          Map("key" -> int1),
//          Map("key" -> long1),
//          Map("key" -> str1),
//          Map("key" -> uri1),
//          Map("key" -> uuid1),
//        ).map(_.eid)
//
//        // All attribute assertions with value "a" of entity e1
//        _ <- Ns(e3).a.v.get.map(_.sortBy(_._1) ==> List(
//          (":Ns/bigDecMap", Map("key" -> bigDec1)),
//          (":Ns/bigIntMap", Map("key" -> bigInt1)),
//          (":Ns/boolMap", Map("key" -> bool1)),
//          (":Ns/dateMap", Map("key" -> date1)),
//          (":Ns/doubleMap", Map("key" -> double1)),
//          (":Ns/intMap", Map("key" -> int1)),
//          (":Ns/longMap", Map("key" -> long1)),
//          (":Ns/strMap", Map("key" -> str1)),
//          (":Ns/uriMap", Map("key" -> uri1)),
//          (":Ns/uuidMap", Map("key" -> uuid1)),
//        ))
//
//        _ <- Ns(e3).a.v(bigInt1).get.map(_ ==> List((":Ns/bigIntMap", Map("key" -> bigDec1))))
//        _ <- Ns(e3).a.v(bigDec1).get.map(_ ==> List((":Ns/bigDecMap", Map("key" -> bigInt1))))
//        _ <- Ns(e3).a.v(bool1).get.map(_ ==> List((":Ns/boolMap", Map("key" -> bool1))))
//        _ <- Ns(e3).a.v(date1).get.map(_ ==> List((":Ns/dateMap", Map("key" -> date1))))
//        _ <- Ns(e3).a.v(double1).get.map(_ ==> List((":Ns/doubleMap", Map("key" -> double1))))
//        _ <- Ns(e3).a.v(int1).get.map(_ ==> List((":Ns/intMap", Map("key" -> int1))))
//        _ <- Ns(e3).a.v(long1).get.map(_ ==> List((":Ns/longMap", Map("key" -> long1))))
//        _ <- Ns(e3).a.v(str1).get.map(_ ==> List((":Ns/strMap", Map("key" -> str1))))
//        _ <- Ns(e3).a.v(uri1).get.map(_ ==> List((":Ns/uriMap", Map("key" -> uri1))))
//        _ <- Ns(e3).a.v(uuid1).get.map(_ ==> List((":Ns/uuidMap", Map("key" -> uuid1))))
//
//        _ <- Ns(e3).a.v(BigDecimal(1.0)).get.map(_ ==> List((":Ns/bigDecMap", Map("key" -> bigDec1))))
//        _ <- Ns(e3).a.v(BigInt(1)).get.map(_ ==> List((":Ns/bigIntMap", Map("key" -> bigInt1))))
//        _ <- Ns(e3).a.v(true).get.map(_ ==> List((":Ns/boolMap", Map("key" -> bool1))))
//        _ <- Ns(e3).a.v(1.0).get.map(_ ==> List((":Ns/doubleMap", Map("key" -> double1))))
//        _ <- Ns(e3).a.v(1).get.map(_ ==> List((":Ns/intMap", Map("key" -> int1))))
//        _ <- Ns(e3).a.v(1L).get.map(_ ==> List((":Ns/longMap", Map("key" -> long1))))
//        _ <- Ns(e3).a.v("a").get.map(_ ==> List((":Ns/strMap", Map("key" -> str1))))









        //        // All attribute assertions with value "a" of entity e1 at t2
        //        _ <- Ns(e1).a.v("a").t(t2).op.getHistory.map(_ ==> List(
        //          (":Ns/str",
        // "a", t2, false)
        //        ))
        //
        //        // All attribute assertions with value "a" of entity e1
        //        _ <- Ns(e1).a.v(2).t.op.getHistory.map(_ ==> List(
        //          (":Ns/int", 2, t3, true)
        //        ))


      } yield ()
    }




    //    "core2" - core { implicit futConn =>
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //        conn <- futConn
    //
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
    //
    //
    //    "adhoc" - bidirectional { implicit conn =>
    //      import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
    //
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //
    //      } yield ()
    //    }
  }
}
