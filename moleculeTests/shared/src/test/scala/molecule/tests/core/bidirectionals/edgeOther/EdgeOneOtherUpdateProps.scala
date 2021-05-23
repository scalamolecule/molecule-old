package molecule.tests.core.bidirectionals.edgeOther

import molecule.datomic.api.out9._
import molecule.datomic.base.facade.Conn
import molecule.setup.AsyncTestSuite
import molecule.tests.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.{ExecutionContext, Future}

object EdgeOneOtherUpdateProps extends AsyncTestSuite {

  def testData(implicit conn: Conn, ec: ExecutionContext): Future[Seq[Long]] = {
    for {
      tx1 <- Quality.name("Love").save
      love = tx1.eid

      tx2 <- Quality.name.insert("Patience", "Humor")
      List(patience, humor) = tx2.eids

      tx3 <- Person.name("Ann")
        .Favorite
        .weight(7)
        .howWeMet("atWork")
        .commonInterests("Food", "Walking", "Travelling")
        .commonLicences("climbing", "flying")
        .commonScores(Seq("golf" -> 7, "baseball" -> 9))
        .coreQuality(love)
        .inCommon(Seq(patience, humor))
        .Animal.name("Rex")
        .save
    } yield {
      val List(ann, annRex, _, _) = tx3.eids
      Seq(love, patience, humor, ann, annRex)
    }
  }


  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "base data" - bidirectional { implicit conn =>
      for {
        Seq(love, patience, humor, ann, annRex) <- testData

        // All edge properties have been inserted in both directions:

        // Person -> Animal
        _ <- Person.name
          .Favorite
          .weight
          .howWeMet
          .commonInterests
          .commonLicences
          .commonScores
          .CoreQuality.name._Favorite
          .InCommon.*(Quality.name)
          .Animal.name
          .get === List(
          ("Ann"
            , 7
            , "atWork"
            , Set("Food", "Travelling", "Walking")
            , Set("climbing", "flying")
            , Map("baseball" -> 9, "golf" -> 7)
            , "Love"
            , List("Patience", "Humor")
            , "Rex")
        )

        // Animal -> Person
        _ <- Animal.name
          .Favorite
          .weight
          .howWeMet
          .commonInterests
          .commonLicences
          .commonScores
          .CoreQuality.name._Favorite
          .InCommon.*(Quality.name)
          .Person.name
          .get === List(
          ("Rex"
            , 7
            , "atWork"
            , Set("Food", "Travelling", "Walking")
            , Set("climbing", "flying")
            , Map("baseball" -> 9, "golf" -> 7)
            , "Love"
            , List("Patience", "Humor")
            , "Ann")
        )
      } yield ()
    }

    "Card-one" - {

      "value" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData

          //      // Updating edge properties from the base entity is not allowed
          //      (Person(ann).Favorite.howWeMet("inSchool").update must throwA[VerifyModelException])
          //        .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
          //        s"[update_edgeComplete]  Can't update edge `Favorite` " +
          //        s"of base entity `Person` without knowing which target entity the edge is pointing too. " +
          //        s"Please update the edge itself, like `Favorite(<edgeId>).edgeProperty(<new value>).update`."

          // Instead update the edge entity itself:

          // Current weight value
          _ <- Person.name_("Ann").Favorite.weight.Animal.name.get === List((7, "Rex"))
          _ <- Animal.name_("Rex").Favorite.weight.Person.name.get === List((7, "Ann"))

          // Apply new value
          _ <- Favorite(annRex).weight(2).update
          _ <- Person.name_("Ann").Favorite.weight.Animal.name.get === List((2, "Rex"))
          _ <- Animal.name_("Rex").Favorite.weight.Person.name.get === List((2, "Ann"))

          // Retract value
          _ <- Favorite(annRex).weight().update
          _ <- Person.name("Ann").Favorite.weight.get === List()
          _ <- Animal.name("Rex").Favorite.weight.get === List()
        } yield ()
      }

      "enum" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData

          // Current howWeMet enum value
          _ <- Person.name_("Ann").Favorite.howWeMet.Animal.name.get === List(("atWork", "Rex"))
          _ <- Animal.name_("Rex").Favorite.howWeMet.Person.name.get === List(("atWork", "Ann"))

          // Apply new enum value
          _ <- Favorite(annRex).howWeMet("throughFriend").update
          _ <- Person.name_("Ann").Favorite.howWeMet.Animal.name.get === List(("throughFriend", "Rex"))
          _ <- Animal.name_("Rex").Favorite.howWeMet.Person.name.get === List(("throughFriend", "Ann"))

          // Retract enum value
          _ <- Favorite(annRex).howWeMet().update
          _ <- Person.name("Ann").Favorite.howWeMet.get === List()
          _ <- Animal.name("Rex").Favorite.howWeMet.get === List()
        } yield ()
      }

      "ref" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData

          // Current value
          _ <- Person.name_("Ann").Favorite.CoreQuality.name._Favorite.Animal.name.get === List(("Love", "Rex"))
          _ <- Animal.name_("Rex").Favorite.CoreQuality.name._Favorite.Person.name.get === List(("Love", "Ann"))

          //      // We can't update across namespaces
          //      (Favorite(annRex).CoreQuality.name("Compassion").update must throwA[VerifyModelException])
          //        .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
          //        s"[update_onlyOneNs]  Update molecules can't span multiple namespaces like `Quality`."

          // Instead we can either update the referenced entity or replace the reference to another existing Quality entity

          // 1. Update referenced value

          _ <- Quality(love).name("Compassion").update

          // Same reference, new value
          _ <- Person.name_("Ann").Favorite.CoreQuality.name._Favorite.Animal.name.get === List(("Compassion", "Rex"))
          _ <- Animal.name_("Rex").Favorite.CoreQuality.name._Favorite.Person.name.get === List(("Compassion", "Ann"))


          // 2. Update reference

          tx <- Quality.name("Trust").save
          trust = tx.eid
          _ <- Favorite(annRex).coreQuality(trust).update

          // New reference/value
          _ <- Person.name_("Ann").Favorite.CoreQuality.name._Favorite.Animal.name.get === List(("Trust", "Rex"))
          _ <- Animal.name_("Rex").Favorite.CoreQuality.name._Favorite.Person.name.get === List(("Trust", "Ann"))


          // Retract reference
          _ <- Favorite(annRex).coreQuality().update
          _ <- Favorite(annRex).CoreQuality.name.get === List()
        } yield ()
      }
    }


    "Card-many" - {

      "values" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData
          // Current values
          _ <- Person.name_("Ann").Favorite.commonInterests.Animal.name.get === List((Set("Food", "Travelling", "Walking"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.commonInterests.Person.name.get === List((Set("Food", "Travelling", "Walking"), "Ann"))

          // Replace
          _ <- Favorite(annRex).commonInterests.replace("Food" -> "Cuisine").update
          _ <- Person.name_("Ann").Favorite.commonInterests.Animal.name.get === List((Set("Travelling", "Walking", "Cuisine"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.commonInterests.Person.name.get === List((Set("Travelling", "Walking", "Cuisine"), "Ann"))

          // Remove
          _ <- Favorite(annRex).commonInterests.retract("Travelling").update
          _ <- Person.name_("Ann").Favorite.commonInterests.Animal.name.get === List((Set("Walking", "Cuisine"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.commonInterests.Person.name.get === List((Set("Walking", "Cuisine"), "Ann"))

          // Add
          _ <- Favorite(annRex).commonInterests.assert("Meditating").update
          _ <- Person.name_("Ann").Favorite.commonInterests.Animal.name.get === List((Set("Walking", "Cuisine", "Meditating"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.commonInterests.Person.name.get === List((Set("Walking", "Cuisine", "Meditating"), "Ann"))

          // Apply new values
          _ <- Favorite(annRex).commonInterests("Running", "Cycling").update
          _ <- Person.name_("Ann").Favorite.commonInterests.Animal.name.get === List((Set("Running", "Cycling"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.commonInterests.Person.name.get === List((Set("Running", "Cycling"), "Ann"))

          // Retract all
          _ <- Favorite(annRex).commonInterests().update
          _ <- Person.name_("Ann").Favorite.commonInterests.get === List()
          _ <- Animal.name_("Rex").Favorite.commonInterests.get === List()
        } yield ()
      }

      "enums" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData

          // Current enum values
          _ <- Person.name_("Ann").Favorite.commonLicences.Animal.name.get === List((Set("climbing", "flying"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.commonLicences.Person.name.get === List((Set("climbing", "flying"), "Ann"))

          // Replace
          _ <- Favorite(annRex).commonLicences.replace("flying" -> "diving").update
          _ <- Person.name_("Ann").Favorite.commonLicences.Animal.name.get === List((Set("climbing", "diving"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.commonLicences.Person.name.get === List((Set("climbing", "diving"), "Ann"))

          // Remove
          _ <- Favorite(annRex).commonLicences.retract("climbing").update
          _ <- Person.name_("Ann").Favorite.commonLicences.Animal.name.get === List((Set("diving"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.commonLicences.Person.name.get === List((Set("diving"), "Ann"))

          // Add
          _ <- Favorite(annRex).commonLicences.assert("parachuting").update
          _ <- Person.name_("Ann").Favorite.commonLicences.Animal.name.get === List((Set("diving", "parachuting"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.commonLicences.Person.name.get === List((Set("diving", "parachuting"), "Ann"))

          // Apply new values
          _ <- Favorite(annRex).commonLicences("climbing", "flying").update
          _ <- Person.name_("Ann").Favorite.commonLicences.Animal.name.get === List((Set("climbing", "flying"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.commonLicences.Person.name.get === List((Set("climbing", "flying"), "Ann"))

          // Retract all
          _ <- Favorite(annRex).commonLicences().update
          _ <- Person.name_("Ann").Favorite.commonLicences.get === List()
          _ <- Animal.name_("Rex").Favorite.commonLicences.get === List()
          _ <- Person.name("Ann" or "Rex").Favorite.commonLicences.get === List()
        } yield ()
      }

      "refs" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData

          // Current value
          _ <- Person.name_("Ann").Favorite.InCommon.*(Quality.name).Animal.name.get === List((Seq("Patience", "Humor"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.InCommon.*(Quality.name).Person.name.get === List((Seq("Patience", "Humor"), "Ann"))

          // As with card-one references we have two choices to change referenced value(s)

          // 1. Update referenced value(s)

          _ <- Quality(patience).name("Waiting ability").update
          _ <- Quality(humor).name("Funny").update

          // Same references, new value(s)
          _ <- Person.name_("Ann").Favorite.InCommon.*(Quality.name).Animal.name.get === List((Seq("Waiting ability", "Funny"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.InCommon.*(Quality.name).Person.name.get === List((Seq("Waiting ability", "Funny"), "Ann"))


          // 2. Update reference(s)

          tx <- Quality.name("Sporty").save
          sporty = tx.eid

          // replace
          _ <- Favorite(annRex).inCommon.replace(humor -> sporty).update
          _ <- Person.name_("Ann").Favorite.InCommon.*(Quality.name).Animal.name.get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List((Seq("Sporty", "Waiting ability"), "Rex"))
          )

          _ <- Animal.name_("Rex").Favorite.InCommon.*(Quality.name).Person.name.get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List((Seq("Sporty", "Waiting ability"), "Ann"))
          )

          // remove
          _ <- Favorite(annRex).inCommon.retract(patience).update
          _ <- Person.name_("Ann").Favorite.InCommon.*(Quality.name).Animal.name.get === List((Seq("Sporty"), "Rex"))
          _ <- Animal.name_("Rex").Favorite.InCommon.*(Quality.name).Person.name.get === List((Seq("Sporty"), "Ann"))

          // add
          _ <- Favorite(annRex).inCommon.assert(patience).update
          _ <- Person.name_("Ann").Favorite.InCommon.*(Quality.name).Animal.name.get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List((Seq("Sporty", "Waiting ability"), "Rex"))
          )

          _ <- Animal.name_("Rex").Favorite.InCommon.*(Quality.name).Person.name.get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List((Seq("Sporty", "Waiting ability"), "Ann"))
          )

          // Apply new values
          _ <- Favorite(annRex).inCommon(sporty, humor).update
          _ <- Person.name_("Ann").Favorite.InCommon.*(Quality.name).Animal.name.get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List((Seq("Funny", "Sporty"), "Rex"))
          )

          _ <- Animal.name_("Rex").Favorite.InCommon.*(Quality.name).Person.name.get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List((Seq("Funny", "Sporty"), "Ann"))
          )

          // Retract all references
          _ <- Favorite(annRex).inCommon().update
          _ <- Person.name_("Ann").Favorite.InCommon.*(Quality.name).Animal.name.get === List()
          _ <- Animal.name_("Rex").Favorite.InCommon.*(Quality.name).Person.name.get === List()

        } yield ()
      }
    }


    "Map" - bidirectional { implicit conn =>
      for {
        Seq(love, patience, humor, ann, annRex) <- testData

        // Current values
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get === List((Map("baseball" -> 9, "golf" -> 7), "Rex"))
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get === List((Map("baseball" -> 9, "golf" -> 7), "Ann"))

        // Replace values by key
        _ <- Favorite(annRex).commonScores.replace("baseball" -> 8, "golf" -> 6).update
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get === List((Map("baseball" -> 8, "golf" -> 6), "Rex"))
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get === List((Map("baseball" -> 8, "golf" -> 6), "Ann"))

        // Remove by key
        _ <- Favorite(annRex).commonScores.retract("golf").update
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get === List((Map("baseball" -> 8), "Rex"))
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get === List((Map("baseball" -> 8), "Ann"))

        // Add
        _ <- Favorite(annRex).commonScores.assert("parachuting" -> 4).update
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get === List((Map("baseball" -> 8, "parachuting" -> 4), "Rex"))
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get === List((Map("baseball" -> 8, "parachuting" -> 4), "Ann"))

        // Apply new values (replacing all current values!)
        _ <- Favorite(annRex).commonScores("volleball" -> 4, "handball" -> 5).update
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get === List((Map("volleball" -> 4, "handball" -> 5), "Rex"))
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get === List((Map("volleball" -> 4, "handball" -> 5), "Ann"))

        // Delete all
        _ <- Favorite(annRex).commonScores().update
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get === List()
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get === List()
      } yield ()
    }
  }
}
