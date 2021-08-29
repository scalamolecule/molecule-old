package moleculeTests.tests.core.bidirectionals.edgeOther

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.out9._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.{ExecutionContext, Future}

object EdgeOneOtherUpdateProps extends AsyncTestSuite {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Long]] = {
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
          .Animal.name
          ._Favorite.InCommon.*(Quality.name)
          .get.map(_ ==> List(
          ("Ann"
            , 7
            , "atWork"
            , Set("Food", "Travelling", "Walking")
            , Set("climbing", "flying")
            , Map("baseball" -> 9, "golf" -> 7)
            , "Love"
            , "Rex"
            , List("Patience", "Humor")
          )
        ))

        // Animal -> Person
        _ <- Animal.name
          .Favorite
          .weight
          .howWeMet
          .commonInterests
          .commonLicences
          .commonScores
          .CoreQuality.name._Favorite
          .Person.name
          ._Favorite.InCommon.*(Quality.name)
          .get.map(_ ==> List(
          ("Rex"
            , 7
            , "atWork"
            , Set("Food", "Travelling", "Walking")
            , Set("climbing", "flying")
            , Map("baseball" -> 9, "golf" -> 7)
            , "Love"
            , "Ann"
            , List("Patience", "Humor")
          )
        ))
      } yield ()
    }

    "Card-one" - {

      "value" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData

          // Updating edge properties from the base entity is not allowed
          _ <- Person(ann).Favorite.howWeMet("inSchool").update.recover { case VerifyModelException(err) =>
            err ==> s"[update_edgeComplete]  Can't update edge `Favorite` " +
              s"of base entity `Person` without knowing which target entity the edge is pointing too. " +
              s"Please update the edge itself, like `Favorite(<edgeId>).edgeProperty(<new value>).update`."
          }

          // Instead update the edge entity itself:

          // Current weight value
          _ <- Person.name_("Ann").Favorite.weight.Animal.name.get.map(_ ==> List((7, "Rex")))
          _ <- Animal.name_("Rex").Favorite.weight.Person.name.get.map(_ ==> List((7, "Ann")))

          // Apply new value
          _ <- Favorite(annRex).weight(2).update
          _ <- Person.name_("Ann").Favorite.weight.Animal.name.get.map(_ ==> List((2, "Rex")))
          _ <- Animal.name_("Rex").Favorite.weight.Person.name.get.map(_ ==> List((2, "Ann")))

          // Retract value
          _ <- Favorite(annRex).weight().update
          _ <- Person.name("Ann").Favorite.weight.get.map(_ ==> List())
          _ <- Animal.name("Rex").Favorite.weight.get.map(_ ==> List())
        } yield ()
      }

      "enum" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData

          // Current howWeMet enum value
          _ <- Person.name_("Ann").Favorite.howWeMet.Animal.name.get.map(_ ==> List(("atWork", "Rex")))
          _ <- Animal.name_("Rex").Favorite.howWeMet.Person.name.get.map(_ ==> List(("atWork", "Ann")))

          // Apply new enum value
          _ <- Favorite(annRex).howWeMet("throughFriend").update
          _ <- Person.name_("Ann").Favorite.howWeMet.Animal.name.get.map(_ ==> List(("throughFriend", "Rex")))
          _ <- Animal.name_("Rex").Favorite.howWeMet.Person.name.get.map(_ ==> List(("throughFriend", "Ann")))

          // Retract enum value
          _ <- Favorite(annRex).howWeMet().update
          _ <- Person.name("Ann").Favorite.howWeMet.get.map(_ ==> List())
          _ <- Animal.name("Rex").Favorite.howWeMet.get.map(_ ==> List())
        } yield ()
      }

      "ref" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData

          // Current value
          _ <- Person.name_("Ann").Favorite.CoreQuality.name._Favorite.Animal.name.get.map(_ ==> List(("Love", "Rex")))
          _ <- Animal.name_("Rex").Favorite.CoreQuality.name._Favorite.Person.name.get.map(_ ==> List(("Love", "Ann")))

          // We can't update across namespaces
          _ <- Favorite(annRex).CoreQuality.name("Compassion").update.recover { case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't span multiple namespaces like `Quality`."
          }

          // Instead we can either update the referenced entity or replace the reference to another existing Quality entity

          // 1. Update referenced value

          _ <- Quality(love).name("Compassion").update

          // Same reference, new value
          _ <- Person.name_("Ann").Favorite.CoreQuality.name._Favorite.Animal.name.get.map(_ ==> List(("Compassion", "Rex")))
          _ <- Animal.name_("Rex").Favorite.CoreQuality.name._Favorite.Person.name.get.map(_ ==> List(("Compassion", "Ann")))


          // 2. Update reference

          tx <- Quality.name("Trust").save
          trust = tx.eid
          _ <- Favorite(annRex).coreQuality(trust).update

          // New reference/value
          _ <- Person.name_("Ann").Favorite.CoreQuality.name._Favorite.Animal.name.get.map(_ ==> List(("Trust", "Rex")))
          _ <- Animal.name_("Rex").Favorite.CoreQuality.name._Favorite.Person.name.get.map(_ ==> List(("Trust", "Ann")))


          // Retract reference
          _ <- Favorite(annRex).coreQuality().update
          _ <- Favorite(annRex).CoreQuality.name.get.map(_ ==> List())
        } yield ()
      }
    }


    "Card-many" - {

      "values" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData
          // Current values
          _ <- Person.name_("Ann").Favorite.commonInterests.Animal.name.get.map(_ ==> List((Set("Food", "Travelling", "Walking"), "Rex")))
          _ <- Animal.name_("Rex").Favorite.commonInterests.Person.name.get.map(_ ==> List((Set("Food", "Travelling", "Walking"), "Ann")))

          // Replace
          _ <- Favorite(annRex).commonInterests.replace("Food" -> "Cuisine").update
          _ <- Person.name_("Ann").Favorite.commonInterests.Animal.name.get.map(_ ==> List((Set("Travelling", "Walking", "Cuisine"), "Rex")))
          _ <- Animal.name_("Rex").Favorite.commonInterests.Person.name.get.map(_ ==> List((Set("Travelling", "Walking", "Cuisine"), "Ann")))

          // Remove
          _ <- Favorite(annRex).commonInterests.retract("Travelling").update
          _ <- Person.name_("Ann").Favorite.commonInterests.Animal.name.get.map(_ ==> List((Set("Walking", "Cuisine"), "Rex")))
          _ <- Animal.name_("Rex").Favorite.commonInterests.Person.name.get.map(_ ==> List((Set("Walking", "Cuisine"), "Ann")))

          // Add
          _ <- Favorite(annRex).commonInterests.assert("Meditating").update
          _ <- Person.name_("Ann").Favorite.commonInterests.Animal.name.get.map(_ ==> List((Set("Walking", "Cuisine", "Meditating"), "Rex")))
          _ <- Animal.name_("Rex").Favorite.commonInterests.Person.name.get.map(_ ==> List((Set("Walking", "Cuisine", "Meditating"), "Ann")))

          // Apply new values
          _ <- Favorite(annRex).commonInterests("Running", "Cycling").update
          _ <- Person.name_("Ann").Favorite.commonInterests.Animal.name.get.map(_ ==> List((Set("Running", "Cycling"), "Rex")))
          _ <- Animal.name_("Rex").Favorite.commonInterests.Person.name.get.map(_ ==> List((Set("Running", "Cycling"), "Ann")))

          // Retract all
          _ <- Favorite(annRex).commonInterests().update
          _ <- Person.name_("Ann").Favorite.commonInterests.get.map(_ ==> List())
          _ <- Animal.name_("Rex").Favorite.commonInterests.get.map(_ ==> List())
        } yield ()
      }

      "enums" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData

          // Current enum values
          _ <- Person.name_("Ann").Favorite.commonLicences.Animal.name.get.map(_ ==> List((Set("climbing", "flying"), "Rex")))
          _ <- Animal.name_("Rex").Favorite.commonLicences.Person.name.get.map(_ ==> List((Set("climbing", "flying"), "Ann")))

          // Replace
          _ <- Favorite(annRex).commonLicences.replace("flying" -> "diving").update
          _ <- Person.name_("Ann").Favorite.commonLicences.Animal.name.get.map(_ ==> List((Set("climbing", "diving"), "Rex")))
          _ <- Animal.name_("Rex").Favorite.commonLicences.Person.name.get.map(_ ==> List((Set("climbing", "diving"), "Ann")))

          // Remove
          _ <- Favorite(annRex).commonLicences.retract("climbing").update
          _ <- Person.name_("Ann").Favorite.commonLicences.Animal.name.get.map(_ ==> List((Set("diving"), "Rex")))
          _ <- Animal.name_("Rex").Favorite.commonLicences.Person.name.get.map(_ ==> List((Set("diving"), "Ann")))

          // Add
          _ <- Favorite(annRex).commonLicences.assert("parachuting").update
          _ <- Person.name_("Ann").Favorite.commonLicences.Animal.name.get.map(_ ==> List((Set("diving", "parachuting"), "Rex")))
          _ <- Animal.name_("Rex").Favorite.commonLicences.Person.name.get.map(_ ==> List((Set("diving", "parachuting"), "Ann")))

          // Apply new values
          _ <- Favorite(annRex).commonLicences("climbing", "flying").update
          _ <- Person.name_("Ann").Favorite.commonLicences.Animal.name.get.map(_ ==> List((Set("climbing", "flying"), "Rex")))
          _ <- Animal.name_("Rex").Favorite.commonLicences.Person.name.get.map(_ ==> List((Set("climbing", "flying"), "Ann")))

          // Retract all
          _ <- Favorite(annRex).commonLicences().update
          _ <- Person.name_("Ann").Favorite.commonLicences.get.map(_ ==> List())
          _ <- Animal.name_("Rex").Favorite.commonLicences.get.map(_ ==> List())
          _ <- Person.name("Ann" or "Rex").Favorite.commonLicences.get.map(_ ==> List())
        } yield ()
      }

      "refs" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annRex) <- testData

          // Current value
          _ <- Person.name_("Ann").Favorite.Animal.name._Favorite.InCommon.*(Quality.name).get.map(_ ==> List(("Rex", Seq("Patience", "Humor"))))
          _ <- Animal.name_("Rex").Favorite.Person.name._Favorite.InCommon.*(Quality.name).get.map(_ ==> List(("Ann", Seq("Patience", "Humor"))))

          // As with card-one references we have two choices to change referenced value(s)

          // 1. Update referenced value(s)

          _ <- Quality(patience).name("Waiting ability").update
          _ <- Quality(humor).name("Funny").update

          // Same references, new value(s)
          _ <- Person.name_("Ann").Favorite.Animal.name._Favorite.InCommon.*(Quality.name).get.map(_ ==> List(("Rex", Seq("Waiting ability", "Funny"))))
          _ <- Animal.name_("Rex").Favorite.Person.name._Favorite.InCommon.*(Quality.name).get.map(_ ==> List(("Ann", Seq("Waiting ability", "Funny"))))


          // 2. Update reference(s)

          tx <- Quality.name("Sporty").save
          sporty = tx.eid

          // replace
          _ <- Favorite(annRex).inCommon.replace(humor -> sporty).update
          _ <- Person.name_("Ann").Favorite.Animal.name._Favorite.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Rex", Seq("Waiting ability", "Sporty")))
          )

          _ <- Animal.name_("Rex").Favorite.Person.name._Favorite.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Ann", Seq("Waiting ability", "Sporty")))
          )

          // remove
          _ <- Favorite(annRex).inCommon.retract(patience).update
          _ <- Person.name_("Ann").Favorite.Animal.name._Favorite.InCommon.*(Quality.name).get.map(_ ==> List(("Rex", Seq("Sporty"))))
          _ <- Animal.name_("Rex").Favorite.Person.name._Favorite.InCommon.*(Quality.name).get.map(_ ==> List(("Ann", Seq("Sporty"))))

          // add
          _ <- Favorite(annRex).inCommon.assert(patience).update
          _ <- Person.name_("Ann").Favorite.Animal.name._Favorite.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Rex", Seq("Waiting ability", "Sporty")))
          )

          _ <- Animal.name_("Rex").Favorite.Person.name._Favorite.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Ann", Seq("Waiting ability", "Sporty")))
          )

          // Apply new values
          _ <- Favorite(annRex).inCommon(sporty, humor).update
          _ <- Person.name_("Ann").Favorite.Animal.name._Favorite.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Rex", Seq("Funny", "Sporty")))
          )

          _ <- Animal.name_("Rex").Favorite.Person.name._Favorite.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Ann", Seq("Funny", "Sporty")))
          )

          // Retract all references
          _ <- Favorite(annRex).inCommon().update
          _ <- Person.name_("Ann").Favorite.Animal.name._Favorite.InCommon.*(Quality.name).get.map(_ ==> List())
          _ <- Animal.name_("Rex").Favorite.Person.name._Favorite.InCommon.*(Quality.name).get.map(_ ==> List())

        } yield ()
      }
    }


    "Map" - bidirectional { implicit conn =>
      for {
        Seq(love, patience, humor, ann, annRex) <- testData

        // Current values
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get.map(_ ==> List((Map("baseball" -> 9, "golf" -> 7), "Rex")))
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get.map(_ ==> List((Map("baseball" -> 9, "golf" -> 7), "Ann")))

        // Replace values by key
        _ <- Favorite(annRex).commonScores.replace("baseball" -> 8, "golf" -> 6).update
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get.map(_ ==> List((Map("baseball" -> 8, "golf" -> 6), "Rex")))
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get.map(_ ==> List((Map("baseball" -> 8, "golf" -> 6), "Ann")))

        // Remove by key
        _ <- Favorite(annRex).commonScores.retract("golf").update
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get.map(_ ==> List((Map("baseball" -> 8), "Rex")))
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get.map(_ ==> List((Map("baseball" -> 8), "Ann")))

        // Add
        _ <- Favorite(annRex).commonScores.assert("parachuting" -> 4).update
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get.map(_ ==> List((Map("baseball" -> 8, "parachuting" -> 4), "Rex")))
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get.map(_ ==> List((Map("baseball" -> 8, "parachuting" -> 4), "Ann")))

        // Apply new values (replacing all current values!)
        _ <- Favorite(annRex).commonScores("volleball" -> 4, "handball" -> 5).update
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get.map(_ ==> List((Map("volleball" -> 4, "handball" -> 5), "Rex")))
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get.map(_ ==> List((Map("volleball" -> 4, "handball" -> 5), "Ann")))

        // Delete all
        _ <- Favorite(annRex).commonScores().update
        _ <- Person.name_("Ann").Favorite.commonScores.Animal.name.get.map(_ ==> List())
        _ <- Animal.name_("Rex").Favorite.commonScores.Person.name.get.map(_ ==> List())
      } yield ()
    }
  }
}
