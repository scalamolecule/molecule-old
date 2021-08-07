package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.util.{Collections, Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.ast.elements._
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query

object Adhoc extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {

    "adhoc" - core { implicit conn =>


      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed

        //        _ <- m(Ref1.str1.Nss * Ns.int.str$) insert List(
        //          ("A", List((1, Some("a")), (2, None))),
        //          ("B", List())
        //        )
        //
        //        _ <- m(Ref1.str1.Nss * Ns.int.str$).get.map(_ ==> List(
        //          ("A", List((1, Some("a")), (2, None)))
        //        ))
        //        _ <- m(Ref1.str1.Nss * Ns.int.str).get.map(_ ==> List(
        //          ("A", List((1, "a")))
        //        ))
        //        _ <- m(Ref1.str1.Nss * Ns.int.str_).get.map(_ ==> List(
        //          ("A", List(1))
        //        ))

        //        _ <- m(Ref1.str1.Nss *? Ns.int.str$).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((1, Some("a")), (2, None))),
        //          ("B", List())
        //        ))
        //        _ <- m(Ref1.str1.Nss *? Ns.int.str).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((1, "a"))),
        //          ("B", List())
        //        ))
        //        _ <- m(Ref1.str1.Nss *? Ns.int.str_).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List(1)),
        //          ("B", List())
        //        ))


//        _ <- Ns.bool.Refs1.*(Ref1.str1).Tx(Ref3.int3_(1)) insert List(
//          (true, List("a", "b")),
//          (false, Nil)
//        )
////
////        _ <- Ns.bool.Refs1.*(Ref1.str1).Tx(Ref3.int3).get.map(_ ==> List(
////          (true, List("a", "b"), 1)
////        ))
//
//        _ <- Ns.bool.Refs1.*?(Ref1.str1).Tx(Ref3.int3).inspectGet
//        _ <- Ns.bool.Refs1.*?(Ref1.str1).Tx(Ref3.int3).get.map(_ ==> List(
//          (true, List("a", "b"), 1),
//          (false, Nil, 1)
//        ))

//        _ <- Ns.str.Refs1.*(Ref1.int1$.str1).Tx(Ref3.int3_(1)) insert List(
//
//          ("E", List())
//        )


        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3_(1)) insert List(
          ("A", List((Some(11), Some(12), "a"))),
          ("B", List((Some(13), None, "b"))),
          ("C", List((None, Some(14), "c"))),
          ("D", List((None, None, "d"))),
          ("E", List())
        )

        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).inspectGet
        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).get.map(_.sortBy(_._1) ==> List(
          ("A", List((Some(11), Some(12), "a")), 1),
          ("B", List((Some(13), None, "b")), 1),
          ("C", List((None, Some(14), "c")), 1),
          ("D", List((None, None, "d")), 1),
          ("E", List(), 1)
        ))


      } yield ()
    }






    //    "adhoc" - products { implicit conn =>
    //      import moleculeTests.tests.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
    //
    //      for {
    //
    //      } yield ()
    //    }


    //    "query" - mbrainz { implicit conn =>
    //      import moleculeTests.tests.examples.datomic.mbrainz.dsl.MBrainz._
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
