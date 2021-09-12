package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.lang.{RuntimeException, Long => jLong}
import java.util.{Collections, Date, UUID, ArrayList => jArrayList, Comparator => jComparator, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.ast.elements._
import molecule.core.exceptions.{MoleculeException, TxFnException}
import molecule.core.marshalling.unpackAttr.String2cast
import molecule.core.marshalling.unpackers.Packed2EntityMap
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import moleculeTests.dataModels.core.base.schema.CoreTestSchema
import scala.util.control.NonFatal


object Adhoc extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {

    "core" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn

        _ <- Ref2.int2.str2$ + Ref1.int1.str1$ + Ns.int.str$ insert Seq(
          ((1, Some("a")), (11, Some("aa")), (111, Some("aaa"))),
          ((2, Some("b")), (22, Some("bb")), (222, None)),
          ((3, Some("c")), (33, None), (333, None)),
          ((4, None), (44, None), (444, None)),
        )

//        // Same namespace
//
//        _ <- m(Ref2.int2.str2).get.map(_.sorted ==> List(
//          (1, "a"),
//          (2, "b"),
//          (3, "c")
//        ))
//        // When 1 + 1 attribute, this outcome will be the same
//        _ <- m(Ref2.int2 + Ref2.str2).get.map(_.sorted ==> List(
//          (1, "a"),
//          (2, "b"),
//          (3, "c")
//        ))
//
//        _ <- m(Ref2.int2).get.map(_.sorted ==> List(1, 2, 3, 4))

        _ <- m(Ref2.int2.str2 + Ref1.int1).get.map(_.sorted ==> List(
          ((1, "a"), 11),
          ((2, "b"), 22),
          ((3, "c"), 33),
        ))
        _ <- m(Ref2.int2 + Ref2.str2_).get.map(_.sorted ==> List(1, 2, 3))
//        // Order irrelevant
//        _ <- m(Ref2.str2_ + Ref2.int2).get.map(_.sorted ==> List(1, 2, 3))
//
//        _ <- m(Ref1.int1 + Ref1.str1_).get.map(_.sorted ==> List(11, 22))
//
//        _ <- m(Ns.int + Ns.str_).get.map(_ ==> List(111))
//
//
//        // 2 namespaces, 1 tacit
//
//        _ <- m(Ref2.int2 + Ref1.str1_).get.map(_.sorted ==> List(1, 2))
//        _ <- m(Ref2.int2 + Ns.str_).get.map(_ ==> List(1))
//        _ <- m(Ref1.int1 + Ns.str_).get.map(_ ==> List(11))
//
//
//        // 3 namespaces, 2 tacits
//
//        _ <- m(Ref2.int2 + Ref1.str1_ + Ns.str_).get.map(_ ==> List(1))
//        _ <- m(Ref2.str2_ + Ref1.int1 + Ns.str_).get.map(_ ==> List(11))
//        _ <- m(Ref2.str2_ + Ref1.str1_ + Ns.int).get.map(_.sorted ==> List(111, 222))
//
//
//        // 3 namespaces, 3 tacits, 4 composite parts (to test second `+` method)
//
//        _ <- m(Ref2.int2 + Ref1.str1_ + Ns.int_ + Ns.str_).get.map(_ ==> List(1))
//        _ <- m(Ref2.str2_ + Ref1.int1 + Ns.int_ + Ns.str_).get.map(_ ==> List(11))
//        _ <- m(Ref2.str2_ + Ref1.str1_ + Ns.int + Ns.str_).get.map(_ ==> List(111))
//        _ <- m(Ref2.str2_ + Ref1.str1_ + Ns.int_ + Ns.str).get.map(_ ==> List("aaa"))


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


    //    "adhoc" - bidirectional { implicit conn =>
    //    for {
    //      _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //    } yield ()
    //  }
  }
}
