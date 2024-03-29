package moleculeTests.tests.core.bidirectionals.edgeSelf

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out9._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object EdgeOneSelfUpdateProps extends AsyncTestSuite {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Long]] = {
    for {
      love <- Quality.name("Love").save.map(_.eid)
      List(patience, humor) <- Quality.name.insert("Patience", "Humor").map(_.eids)
      List(ann, annBen, _, _) <- Person.name("Ann")
        .Loves
        .weight(7)
        .howWeMet("atWork")
        .commonInterests("Food", "Walking", "Travelling")
        .commonLicences("climbing", "flying")
        .commonScores(Seq("golf" -> 7, "baseball" -> 9))
        .coreQuality(love)
        .inCommon(Seq(patience, humor))
        .Person.name("Ben")
        .save.map(_.eids)
    } yield {
      Seq(love, patience, humor, ann, annBen)
    }
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "base data" - bidirectional { implicit conn =>
      for {
        Seq(love, patience, humor, ann, annBen) <- testData

        // All edge properties have been saved in both directions
        _ <- Person.name.Loves
          .weight
          .howWeMet
          .commonInterests
          .commonLicences
          .commonScores
          .CoreQuality.name
          ._Loves.Person.name
          ._Loves.InCommon.*(Quality.name)
          .get.map(_ ==> List(
          ("Ann"
            , 7
            , "atWork"
            , Set("Food", "Walking", "Travelling")
            , Set("climbing", "flying")
            , Map("baseball" -> 9, "golf" -> 7)
            , "Love"
            , "Ben"
            , List("Patience", "Humor")
          ),
          ("Ben"
            , 7
            , "atWork"
            , Set("Food", "Walking", "Travelling")
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
          Seq(love, patience, humor, ann, annBen) <- testData

          // Updating edge properties from the base entity is not allowed
          _ <- Person(ann).Loves.howWeMet("inSchool").update
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> s"[update_edgeComplete]  Can't update edge `Loves` " +
              s"of base entity `Person` without knowing which target entity the edge is pointing too. " +
              s"Please update the edge itself, like `Loves(<edgeId>).edgeProperty(<new value>).update`."
          }

          // Instead update the edge entity itself:

          // Current weight value
          _ <- Person.name("Ann" or "Ben").a1.Loves.weight.get.map(_ ==> List(
            ("Ann", 7),
            ("Ben", 7)
          ))

          // Apply new value
          _ <- Loves(annBen).weight(2).update
          _ <- Person.name("Ann" or "Ben").a1.Loves.weight.get.map(_ ==> List(
            ("Ann", 2),
            ("Ben", 2)
          ))

          // Retract edge
          _ <- Loves(annBen).weight().update
          _ <- Person.name("Ann" or "Ben").Loves.weight.get.map(_ ==> List())
        } yield ()
      }

      "enum" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annBen) <- testData

          // Current howWeMet enum value
          _ <- Person.name("Ann" or "Ben").a1.Loves.howWeMet.get.map(_ ==> List(
            ("Ann", "atWork"),
            ("Ben", "atWork")
          ))

          // Apply new enum value
          _ <- Loves(annBen).howWeMet("throughFriend").update
          _ <- Person.name("Ann" or "Ben").a1.Loves.howWeMet.get.map(_ ==> List(
            ("Ann", "throughFriend"),
            ("Ben", "throughFriend")
          ))

          // Retract enum value
          _ <- Loves(annBen).howWeMet().update
          _ <- Person.name("Ann" or "Ben").Loves.howWeMet.get.map(_ ==> List())
        } yield ()
      }

      "ref" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, ann, annBen) <- testData

          // Current value
          _ <- Person.name("Ann" or "Ben").a1.Loves.CoreQuality.name.get.map(_ ==> List(
            ("Ann", "Love"),
            ("Ben", "Love")
          ))

          // We can't update across namespaces
          _ <- Loves(annBen).CoreQuality.name("Compassion").update
            .map(_ ==> "Unexpected success").recover { case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't span multiple namespaces like `Quality`."
          }

          // Instead we can either update the referenced entity or replace the reference to another existing Quality entity

          // 1. Update referenced value

          _ <- Quality(love).name("Compassion").update

          // Same reference, new value
          _ <- Person.name("Ann" or "Ben").a1.Loves.CoreQuality.name.get.map(_ ==> List(
            ("Ann", "Compassion"),
            ("Ben", "Compassion")
          ))


          // 2. Update reference

          trust <- Quality.name("Trust").save.map(_.eid)
          _ <- Loves(annBen).coreQuality(trust).update

          // New reference/value
          _ <- Person.name("Ann" or "Ben").a1.Loves.CoreQuality.name.get.map(_ ==> List(
            ("Ann", "Trust"),
            ("Ben", "Trust")
          ))

          // Retract reference
          _ <- Loves(annBen).coreQuality().update
          _ <- Loves(annBen).CoreQuality.name.get.map(_ ==> List())
        } yield ()
      }
    }


    "Card-many" - {

      "values" - bidirectional { implicit conn =>
        val commonInterestsOf = m(Person.name(?).a1.Loves.commonInterests)
        for {
          Seq(love, patience, humor, ann, annBen) <- testData

          // Current values
          _ <- commonInterestsOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", Set("Food", "Walking", "Travelling")),
            ("Ben", Set("Food", "Walking", "Travelling"))
          ))

          // Replace
          _ <- Loves(annBen).commonInterests.replace("Food" -> "Cuisine").update
          _ <- commonInterestsOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", Set("Cuisine", "Walking", "Travelling")),
            ("Ben", Set("Cuisine", "Walking", "Travelling"))
          ))

          // Remove
          _ <- Loves(annBen).commonInterests.retract("Travelling").update
          _ <- commonInterestsOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", Set("Cuisine", "Walking")),
            ("Ben", Set("Cuisine", "Walking"))
          ))

          // Add
          _ <- Loves(annBen).commonInterests.assert("Meditating").update
          _ <- commonInterestsOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", Set("Cuisine", "Walking", "Meditating")),
            ("Ben", Set("Cuisine", "Walking", "Meditating"))
          ))

          // Apply new values
          _ <- Loves(annBen).commonInterests("Running", "Cycling").update
          _ <- commonInterestsOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", Set("Running", "Cycling")),
            ("Ben", Set("Running", "Cycling"))
          ))

          // Retract all
          _ <- Loves(annBen).commonInterests().update
          _ <- commonInterestsOf("Ann" or "Ben").get.map(_ ==> List())
        } yield ()
      }

      "enums" - bidirectional { implicit conn =>
        val commonLicencesOf = m(Person.name(?).a1.Loves.commonLicences)
        for {
          Seq(love, patience, humor, ann, annBen) <- testData

          // Current enum values
          _ <- commonLicencesOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", Set("climbing", "flying")),
            ("Ben", Set("climbing", "flying"))
          ))

          // Replace
          _ <- Loves(annBen).commonLicences.replace("flying" -> "diving").update
          _ <- commonLicencesOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", Set("climbing", "diving")),
            ("Ben", Set("climbing", "diving"))
          ))

          // Remove
          _ <- Loves(annBen).commonLicences.retract("climbing").update
          _ <- commonLicencesOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", Set("diving")),
            ("Ben", Set("diving"))
          ))

          // Add
          _ <- Loves(annBen).commonLicences.assert("parachuting").update
          _ <- commonLicencesOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", Set("diving", "parachuting")),
            ("Ben", Set("diving", "parachuting"))
          ))

          // Apply new values
          _ <- Loves(annBen).commonLicences("climbing", "flying").update
          _ <- commonLicencesOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", Set("climbing", "flying")),
            ("Ben", Set("climbing", "flying"))
          ))

          // Retract all
          _ <- Loves(annBen).commonLicences().update
          _ <- commonLicencesOf("Ann" or "Ben").get.map(_ ==> List())
        } yield ()
      }

      "refs" - bidirectional { implicit conn =>
        val inCommonOf = m(Person.name(?).a1.Loves.InCommon.*(Quality.name.a1))
        for {
          Seq(love, patience, humor, ann, annBen) <- testData

          // Current value
          _ <- inCommonOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", List("Humor", "Patience")),
            ("Ben", List("Humor", "Patience"))
          ))

          // As with card-one references we have two choices to change referenced value(s)

          // 1. Update referenced value(s)

          _ <- Quality(patience).name("Waiting ability").update
          _ <- Quality(humor).name("Funny").update

          // Same references, new value(s)
          _ <- inCommonOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", List("Funny", "Waiting ability")),
            ("Ben", List("Funny", "Waiting ability"))
          ))


          // 2. Update reference(s)

          sporty <- Quality.name("Sporty").save.map(_.eid)

          // replace
          _ <- Loves(annBen).inCommon.replace(humor -> sporty).update
          _ <- inCommonOf("Ann" or "Ben").get.map(_ ==> List(
              ("Ann", List("Sporty", "Waiting ability")),
              ("Ben", List("Sporty", "Waiting ability"))
            ))

          // remove
          _ <- Loves(annBen).inCommon.retract(patience).update
          _ <- inCommonOf("Ann" or "Ben").get.map(_ ==> List(
            ("Ann", List("Sporty")),
            ("Ben", List("Sporty"))
          ))

          // add
          _ <- Loves(annBen).inCommon.assert(patience).update
          _ <- inCommonOf("Ann" or "Ben").get.map(_ ==> List(
              ("Ann", List("Sporty", "Waiting ability")),
              ("Ben", List("Sporty", "Waiting ability"))
            ))

          // Apply new values
          _ <- Loves(annBen).inCommon(sporty, humor).update
          _ <- inCommonOf("Ann" or "Ben").get.map(_ ==> List(
              ("Ann", List("Funny", "Sporty")),
              ("Ben", List("Funny", "Sporty"))
            ))

          // Retract all references
          _ <- Loves(annBen).inCommon().update
          _ <- inCommonOf("Ann" or "Ben").get.map(_ ==> List())

        } yield ()
      }
    }


    "Map" - bidirectional { implicit conn =>
      val commonScoresOf = m(Person.name(?).a1.Loves.commonScores)
      for {
        Seq(love, patience, humor, ann, annBen) <- testData

        // Current values
        _ <- commonScoresOf("Ann" or "Ben").get.map(_ ==> List(
          ("Ann", Map("baseball" -> 9, "golf" -> 7)),
          ("Ben", Map("baseball" -> 9, "golf" -> 7))
        ))

        // Replace values by key
        _ <- Loves(annBen).commonScores.replace("baseball" -> 8, "golf" -> 6).update
        _ <- commonScoresOf("Ann" or "Ben").get.map(_ ==> List(
          ("Ann", Map("baseball" -> 8, "golf" -> 6)),
          ("Ben", Map("baseball" -> 8, "golf" -> 6))
        ))

        // Remove by key
        _ <- Loves(annBen).commonScores.retract("golf").update
        _ <- commonScoresOf("Ann" or "Ben").get.map(_ ==> List(
          ("Ann", Map("baseball" -> 8)),
          ("Ben", Map("baseball" -> 8))
        ))

        // Add
        _ <- Loves(annBen).commonScores.assert("parachuting" -> 4).update
        _ <- commonScoresOf("Ann" or "Ben").get.map(_ ==> List(
          ("Ann", Map("baseball" -> 8, "parachuting" -> 4)),
          ("Ben", Map("baseball" -> 8, "parachuting" -> 4))
        ))

        // Apply new values (replacing all current values!)
        _ <- Loves(annBen).commonScores("volleyball" -> 4, "handball" -> 5).update
        _ <- commonScoresOf("Ann" or "Ben").get.map(_ ==> List(
          ("Ann", Map("volleyball" -> 4, "handball" -> 5)),
          ("Ben", Map("volleyball" -> 4, "handball" -> 5))
        ))

        // Delete all
        _ <- Loves(annBen).commonScores().update
        _ <- commonScoresOf("Ann" or "Ben").get.map(_ ==> List())
      } yield ()
    }
  }
}
