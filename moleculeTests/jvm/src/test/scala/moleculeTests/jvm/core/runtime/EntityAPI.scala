//package molecule.tests.core.runtime // only for internal molecule use
//
//import molecule.datomic.api.out3._
//import moleculeTests.setup.AsyncTestSuite
//import moleculeTests.dataModels.core.base.dsl.CoreTest._
//import utest._
//import scala.concurrent.ExecutionContext.Implicits.global
//
//object EntityAPI extends AsyncTestSuite {
//
//  lazy val tests = Tests {
//
//    "apply typed" - core { implicit conn =>
//      for {
//        List(eid, refId) <- Ns.str.int.Ref1.str1.insert("Ben", 42, "Hollywood Rd").map(_.eids)
//        strTyped: Option[String] <- eid.apply[String](":Ns/str")
//        untyped: Option[Any] <- eid(":Ns/str")
//
//        // Level 1
//        _ = strTyped ==> Some("Ben")
//        _ <- eid[Int](":Ns/int").map(_ ==> Some(42))
//
//        // Level 2
//        _ <- refId[String](":Ref1/str1").map(_ ==> Some("Hollywood Rd"))
//
//        // Non-existing attribute returns None
//        _ <- eid(":Ns/non-existing-attribute").map(_ ==> None)
//      } yield ()
//    }
//
//
//    "apply untyped" - core { implicit conn =>
//      for {
//        List(eid, refId) <- Ns.str.int.Ref1.str1.insert("Ben", 42, "Hollywood Rd").map(_.eids)
//
//        _ <- eid(":Ns/str", ":Ns/int", ":Ns/ref1").map(_ ==> List(
//          Some("Ben"),
//          Some(42),
//          Some(
//            Map(
//              ":db/id" -> refId,
//              ":Ref1/str1" -> "Hollywood Rd"
//            )
//          )
//        ))
//
//        // Type ascription is still unchecked since it is eliminated by erasure
//        // so we suppress compile warnings emitted
//        List(
//        optName: Option[String]@unchecked,
//        optAddress: Option[Map[String, Any]]@unchecked
//        ) <- eid(
//          ":Ns/str",
//          ":Ns/ref1"
//        )
//
//        name: String = optName.getOrElse("no name")
//
//        address: Map[String, Any]@unchecked = optAddress.getOrElse(Map.empty[String, Any])
//        street: String = address.getOrElse(":Ref1/str1", "no street").asInstanceOf[String]
//
//        _ = name ==> "Ben"
//        _ = street ==> "Hollywood Rd"
//      } yield ()
//    }
//
//    "Reverse lookup" - core { implicit conn =>
//      for {
//        r <- Ref1.int1(10).save.map(_.eid)
//
//        // 3 entities pointing to r
//        eids <- Ns.int.ref1.insert(List((1, r), (2, r), (3, r))).map(_.eids.sorted)
//
//        // get entities pointing to r
//        _ <- r[Long](":Ns/_ref1").map(_ ==> Some(eids))
//
//        // Alternatively we can get the entities type safely with a query
//        _ <- Ns.e.ref1_(r).get.map(_.sorted ==> eids)
//      } yield ()
//    }
//  }
//}
