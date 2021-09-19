package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
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
import molecule.datomic.api.out4.m
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.datomic.base.transform.exception.Model2TransactionException
//import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import scala.util.control.NonFatal


object Adhoc extends AsyncTestSuite with Helpers {

  val en    = "en"
  val da    = "da"
  val fr    = "fr"
  val it    = "it"
  val Hi    = "Hi"
  val He    = "He"
  val Bo    = "Bo"
  val regEx = "Hi|He"

  val i10 = 10
  val i20 = 20
  val i30 = 30

  def testData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    for {
//      _ <- Ns.int.strMap insert List(
//        (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
//        (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
//        (3, Map("en" -> "Hello", "da" -> "Hej")),
//        (4, Map("da" -> "Hej")),
//        (5, Map[String, String]())
//      )
      _ <- Ns.int.intMap insert List(
        (1, Map("en" -> 10, "da" -> 30)),
        (2, Map("en" -> 10, "da" -> 10, "fr" -> 20, "it" -> 30)),
        (3, Map("en" -> 30, "da" -> 30)),
        (4, Map("da" -> 30)),
        (5, Map[String, Int]())
      )
//      _ <- Ns.int.longMap insert List(
//        (1, Map("en" -> 10L, "da" -> 30L)),
//        (2, Map("en" -> 10L, "da" -> 10L, "fr" -> 20L, "it" -> 30L)),
//        (3, Map("en" -> 30L, "da" -> 30L)),
//        (4, Map("da" -> 30L)),
//        (5, Map[String, Long]())
//      )
      res <- Ns.int.doubleMap insert List(
        (1, Map("en" -> 10.0, "da" -> 30.0)),
        (2, Map("en" -> 10.0, "da" -> 10.0, "fr" -> 20.0, "it" -> 30.0)),
        (3, Map("en" -> 30.0, "da" -> 30.0)),
        (4, Map("da" -> 30.0)),
        (5, Map[String, Double]())
      )
//      _ <- Ns.int.boolMap insert List(
//        (1, Map("en" -> true, "da" -> false)),
//        (2, Map("en" -> true, "da" -> true, "fr" -> false, "it" -> false)),
//        (3, Map("en" -> false, "en" -> false)),
//        (4, Map("da" -> false)),
//        (5, Map[String, Boolean]())
//      )
//      _ <- Ns.int.dateMap insert List(
//        (1, Map("en" -> date1, "da" -> date3)),
//        (2, Map("en" -> date1, "da" -> date1, "fr" -> date2, "it" -> date3)),
//        (3, Map("en" -> date3, "da" -> date3)),
//        (4, Map("da" -> date3)),
//        (5, Map[String, Date]())
//      )
//      _ <- Ns.int.uuidMap insert List(
//        (1, Map("en" -> uuid1, "da" -> uuid3)),
//        (2, Map("en" -> uuid1, "da" -> uuid1, "fr" -> uuid2, "it" -> uuid3)),
//        (3, Map("en" -> uuid3, "da" -> uuid3)),
//        (4, Map("da" -> uuid3)),
//        (5, Map[String, UUID]())
//      )
//      res <- Ns.int.uriMap insert List(
//        (1, Map("en" -> uri1, "da" -> uri3)),
//        (2, Map("en" -> uri1, "da" -> uri1, "fr" -> uri2, "it" -> uri3)),
//        (3, Map("en" -> uri3, "da" -> uri3)),
//        (4, Map("da" -> uri3)),
//        (5, Map[String, URI]())
//      )
    } yield res
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "adhoc jvm" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn

        _ <- testData

        _ <- Ns.int.intMap.>(20).get.map(_ ==> List(
          (1, Map("da" -> 30)),
          (2, Map("it" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))

        _ <- Ns.int.doubleMap.>(20.0).get.map(_ ==> List(
          (1, Map("da" -> 30.0)),
          (2, Map("it" -> 30.0)),
          (3, Map("en" -> 30.0, "da" -> 30.0)),
          (4, Map("da" -> 30.0))
        ))
//        _ <- Ns.int.doubleMap.>=(20.0).get.map(_ ==> List(
//          (1, Map("da" -> 30.0)),
//          (2, Map("fr" -> 20.0, "it" -> 30.0)),
//          (3, Map("en" -> 30.0, "da" -> 30.0)),
//          (4, Map("da" -> 30.0))
//        ))
//        _ <- Ns.int.doubleMap.<=(20.0).get.map(_ ==> List(
//          (1, Map("en" -> 10.0)),
//          (2, Map("en" -> 10.0, "da" -> 10.0, "fr" -> 20.0))
//        ))
//        _ <- Ns.int.doubleMap.<(20.0).get.map(_ ==> List(
//          (1, Map("en" -> 10.0)),
//          (2, Map("en" -> 10.0, "da" -> 10.0))
//        ))
//
//        _ <- Ns.int.doubleMap.>(-10.0).get.map(_ ==> List(
//          (1, Map("en" -> 10.0, "da" -> 30.0)),
//          (2, Map("en" -> 10.0, "da" -> 10.0, "fr" -> 20.0, "it" -> 30.0)),
//          (3, Map("en" -> 30.0, "da" -> 30.0)),
//          (4, Map("da" -> 30.0))
//        ))
//
//        _ <- Ns.int.doubleMap_.>(20.0).get.map(_ ==> List(1, 2, 3, 4))
//        _ <- Ns.int.doubleMap_.>=(20.0).get.map(_ ==> List(1, 2, 3, 4))
//        _ <- Ns.int.doubleMap_.<=(20.0).get.map(_ ==> List(1, 2))
//        _ <- Ns.int.doubleMap_.<(20.0).get.map(_ ==> List(1, 2))
//
//
//        // Date
//
//        _ <- Ns.int.dateMap.>(date2).get.map(_ ==> List(
//          (1, Map("da" -> date3)),
//          (2, Map("it" -> date3)),
//          (3, Map("en" -> date3, "da" -> date3)),
//          (4, Map("da" -> date3))
//        ))
//
//        _ <- Ns.int.dateMap.>=(date2).get.map(_ ==> List(
//          (1, Map("da" -> date3)),
//          (2, Map("fr" -> date2, "it" -> date3)),
//          (3, Map("en" -> date3, "da" -> date3)),
//          (4, Map("da" -> date3))
//        ))
//        _ <- Ns.int.dateMap.<=(date2).get.map(_ ==> List(
//          (1, Map("en" -> date1)),
//          (2, Map("en" -> date1, "da" -> date1, "fr" -> date2))
//        ))
//        _ <- Ns.int.dateMap.<(date2).get.map(_ ==> List(
//          (1, Map("en" -> date1)),
//          (2, Map("en" -> date1, "da" -> date1))
//        ))
//
//        _ <- Ns.int.dateMap_.>(date2).get.map(_ ==> List(1, 2, 3, 4))
//        _ <- Ns.int.dateMap_.>=(date2).get.map(_ ==> List(1, 2, 3, 4))
//        _ <- Ns.int.dateMap_.<=(date2).get.map(_ ==> List(1, 2))
//        _ <- Ns.int.dateMap_.<(date2).get.map(_ ==> List(1, 2))


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
