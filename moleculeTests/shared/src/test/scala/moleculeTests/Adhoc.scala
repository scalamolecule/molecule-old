package moleculeTests

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.exceptions.MoleculeException
import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.Helpers
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.in3_out12._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.Future
import scala.util.{Failure, Success}
//import scala.concurrent.ExecutionContext.Implicits.global


object Adhoc extends AsyncTestSuite with Helpers {

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global


    "core" - core { implicit futConn =>
      import moleculeTests.dataModels.core.base.dsl.CoreTest._
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn
        //        _ <- Ns.int.apply(1).asc1.str.desc2.get
        //        _ <- Ns.int.not(1).asc1.str.desc2.get

        e <- Ns.int(1).save.map(_.eid)
        _ <- Ns.int.get.map(_ ==> List(1))
         _ = {
           conn
         }


      } yield ()
    }


    //    "products" - products { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
    //
    //      for {
    //
    ////        List(o1, l1, c1) <- Order.orderid.LineItems * (
    ////          LineItem.quantity.Comments * Comment.text) insert List(
    ////          (23, List((2, List("second"))))
    ////        ) map (_.eids)
    ////
    ////        _ <- o1.graph.map(_ ==>
    ////          Map(
    ////            ":db/id" -> o1,
    ////            ":Order/lineItems" ->
    ////              Set(
    ////                Map(
    ////                  ":db/id" -> l1,
    ////                  ":LineItem/comments" ->
    ////                    Set(
    ////                      Map(
    ////                        ":db/id" -> c1,
    ////                        ":Comment/text" -> "second"
    ////                      ),
    ////                    ),
    ////                  ":LineItem/quantity" -> 2,
    ////                )
    ////              ),
    ////            ":Order/orderid" -> 23
    ////          )
    ////        )
    //
    //
    //      } yield ()
    //    }

    //
    //    "socialNews" - socialNews { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.SocialNews._
    //      val ecURL = "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html"
    //      for {
    //        txr1 <- Story.title.url insert List(
    //          ("Teach Yourself Programming in Ten Years", "http://norvig.com/21-days.html"),
    //          ("Clojure Rationale", "http://clojure.org/rationale"),
    //          ("Beating the Averages", "http://www.paulgraham.com/avg.html")
    //        )
    //
    //        List(s1, s2, s3) = txr1.eids
    //        tx1 = txr1.tx
    //
    //        // Add Users
    //        txR2 <- User.firstName.lastName.email insert List(
    //          ("Stu", "Halloway", "stuarthalloway@datomic.com"),
    //          ("Ed", "Itor", "editor@example.com")
    //        )
    //        List(stu, ed) = txR2.eids
    //
    //        // Add comments
    //        // Input Molecule act as a template to insert data
    //        addComment = Parent.e.Comment.author.text.insert
    //
    //        // Insert Stu's first comment to story 1 and return the id of this comment
    //        // (Parent s1 is a Story)
    //        txR1 <- addComment(s1, stu, "blah 1")
    //        c1 = txR1.eid
    //
    //        // Ed's Comment to Stu's first Comment
    //        // (Parent c1 is a Comment)
    //        txr2 <- addComment(c1, ed, "blah 2")
    //        c2 = txr2.eid
    //
    //        // More sub-comments
    //        txR3 <- addComment(c2, stu, "blah 3")
    //        c3 = txR3.eid
    //        txR4 <- addComment(c3, ed, "blah 4")
    //        c4 = txR4.eid
    //
    //        // Story 2 comments
    //        txR5 <- addComment(s2, ed, "blah 5")
    //        c5 = txR5.eid
    //        txR6 <- addComment(c5, stu, "blah 6")
    //        c6 = txR6.eid
    //
    //        // Story 3 comments
    //        txR7 <- addComment(s3, ed, "blah 7")
    //        c7 = txR7.eid
    //        txR8 <- addComment(c7, stu, "blah 8")
    //        c8 = txR8.eid
    //
    //        // Stu comments on his own comment
    //        txR9 <- addComment(c8, stu, "blah 9")
    //        c9 = txR9.eid
    //
    //        // Story 2 again - a second thread of comments. This time Stu starts
    //        txR10 <- addComment(s2, stu, "blah 10")
    //        c10 = txR10.eid
    //        txR11 <- addComment(c10, ed, "blah 11")
    //        c11 = txR11.eid
    //
    //        txR12 <- addComment(c11, stu, "blah 12")
    //        c12 = txR12.eid
    //
    //
    //
    //      } yield ()
    //    }
    //
    //
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
    //            "bidirectional" - bidirectional { implicit conn =>
    //              import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
    //
    //              for {
    //                _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //
    //                List(ann, annLovesBen, benLovesAnn, ben) <-
    //                  Person.name("Ann").Loves.weight(7).Person.name("Ben").save.map(_.eids)
    //
    ////                // Bidirectional property edges have been saved
    ////                _ <- Person.name.Loves.weight.Person.name.get.map(_.sorted ==> List(
    ////                  ("Ann", 7, "Ben"),
    ////                  // Reverse edge:
    ////                  ("Ben", 7, "Ann")
    ////                ))
    ////
    ////                _ <- ann.graphDepth(1).map(_ ==> Map(
    ////                  ":db/id" -> ann,
    ////                  ":Person/loves" -> annLovesBen,
    ////                  ":Person/name" -> "Ann"
    ////                ))
    ////
    ////                _ <- ben.graphDepth(1).map(_ ==> Map(
    ////                  ":db/id" -> ben,
    ////                  ":Person/loves" -> benLovesAnn,
    ////                  ":Person/name" -> "Ben"
    ////                ))
    //
    //                _ <- ann.graphDepth(2).map(_ ==> Map(
    //                  ":db/id" -> ann,
    //                  ":Person/loves" -> Map(
    //                    ":db/id" -> annLovesBen,
    //                    ":Loves/person" -> ben,
    //                    ":Loves/weight" -> 7,
    //                    ":molecule_Meta/otherEdge" -> benLovesAnn),
    //                  ":Person/name" -> "Ann"
    //                ))
    //
    //              } yield ()
    //            }


    /*



    Map(:db/id -> 87960930222204, :Person/loves -> HashMap(:Loves/person -> 87960930222207, :Person/name -> Ann, :db/id -> 87960930222205, :molecule_Meta/otherEdge -> 87960930222206, :Loves/weight -> 7)) !=
    Map(:db/id -> 87960930222204, :Person/loves ->     Map(:db/id -> 87960930222205, :Loves/person -> 87960930222207, :Loves/weight -> 7, :molecule_Meta/otherEdge -> 87960930222206), :Person/name -> Ann)
     */


    //    "seattle" - seattle { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.seattle.dsl.Seattle._
    //
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //        _ <- Community.name.url.tpe.orgtype$.category$.Neighborhood.name.District.name.region$ insert seattleData
    //
    //
    //
    //      } yield ()
    //    }
  }
}
