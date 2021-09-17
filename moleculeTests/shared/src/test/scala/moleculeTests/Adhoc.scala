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
import molecule.core.util.Helpers
import molecule.datomic.base.facade.{Conn, TxReport}
//import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import scala.util.control.NonFatal

object Adhoc extends AsyncTestSuite with Helpers  {


  def data(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      _ <- Ns.int.insert(1, 2, 3)
      _ <- Ns.double.insert(1.0, 2.0, 3.0)
      _ <- Ns.str.insert("a", "b", "c")
    } yield ()
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global


    "adhoc shared" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn

        _ <- data
        // For any property

        _ <- Ns.int(min).get.map(_ ==> List(1))
        _ <- Ns.int(min).getObj.map(_.int ==> 1)
//        _ <- Ns.int(max).getObj.map(_.int ==> 3)
//        _ <- Ns.int(rand).getObj.map(_.int) // 1, 2 or 3
//        _ <- Ns.int(sample).getObj.map(_.int) // 1, 2 or 3
//        _ <- Ns.int(median).getObj.map(_.int ==> 2)
//
//        _ <- Ns.double(min).getObj.map(_.double ==> 1.0)
//        _ <- Ns.double(max).getObj.map(_.double ==> 3.0)
//        _ <- Ns.double(rand).getObj.map(_.double) // 1.0, 2.0 or 3.0
//        _ <- Ns.double(sample).getObj.map(_.double) // 1.0, 2.0 or 3.0
//        _ <- Ns.double(median).getObj.map(_.double ==> 2.0)
//
//        _ <- Ns.str(min).getObj.map(_.str ==> "a")
//        _ <- Ns.str(max).getObj.map(_.str ==> "c")
//        _ <- Ns.str(rand).getObj.map(_.str) // a, b or c
//        _ <- Ns.str(sample).getObj.map(_.str) // a, b or c
//        _ <- Ns.str(median).getObj.map(_.str ==> "b")



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
