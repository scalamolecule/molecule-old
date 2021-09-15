package moleculeTests.tests.core.bidirectionals.edgeOther

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.out9._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.{ExecutionContext, Future}

object EdgeManyOtherUpdateProps extends AsyncTestSuite {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Long]] = {
    for {
      tx1 <- Quality.name("Love").save
      love = tx1.eid
      tx2 <- Quality.name.insert("Patience", "Humor")
      List(patience, humor) = tx2.eids
      tx3 <- Animal.name.insert("Rex")
      rex = tx3.eid
      tx4 <- Person.name("Ann") // New entity
        .CloseTo
        .weight(7)
        .howWeMet("inSchool")
        .commonInterests("Food", "Walking", "Travelling")
        .commonLicences("climbing", "flying")
        .commonScores(Seq("golf" -> 7, "baseball" -> 9))
        .coreQuality(love)
        .inCommon(Seq(patience, humor))
        .animal(rex) // Saving reference to existing Person entity
        .save
    } yield {
      val List(ann, annRex, rexAnn) = tx4.eids
      Seq(love, patience, humor, rex, ann, annRex, rexAnn)
    }
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "base data" - bidirectional { implicit conn =>
      for {
        Seq(love, patience, humor, rex, ann, annRex, rexAnn) <- testData
        // All edge properties have been inserted in both directions:

        // Person -> Animal
        _ <- Person.name
          .CloseTo
          .weight
          .howWeMet
          .commonInterests
          .commonLicences
          .commonScores
          .CoreQuality.name._CloseTo
          .Animal.name
          ._CloseTo.InCommon.*(Quality.name)
          .get.map(_ ==> List(
          ("Ann"
            , 7
            , "inSchool"
            , Set("Food", "Walking", "Travelling")
            , Set("climbing", "flying")
            , Map("baseball" -> 9, "golf" -> 7)
            , "Love"
            , "Rex"
            , List("Patience", "Humor")
          )
        ))

        // Animal -> Person
        _ <- Animal.name
          .CloseTo
          .weight
          .howWeMet
          .commonInterests
          .commonLicences
          .commonScores
          .CoreQuality.name._CloseTo
          .Person.name
          ._CloseTo.InCommon.*(Quality.name)
          .get.map(_ ==> List(
          ("Rex"
            , 7
            , "inSchool"
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
          Seq(love, patience, humor, rex, ann, annRex, rexAnn) <- testData

          // Updating edge properties from the base entity is not allowed
          _ <- Person(ann).CloseTo.howWeMet("inSchool").update.recover { case VerifyModelException(err) =>
            err ==> s"[update_edgeComplete]  Can't update edge `CloseTo` " +
              s"of base entity `Person` without knowing which target entity the edge is pointing too. " +
              s"Please update the edge itself, like `CloseTo(<edgeId>).edgeProperty(<new value>).update`."
          }

          // Instead update the edge entity itself:

          // Current weight value
          _ <- Person.name_("Ann").CloseTo.weight.Animal.name.get.map(_ ==> List((7, "Rex")))
          _ <- Animal.name_("Rex").CloseTo.weight.Person.name.get.map(_ ==> List((7, "Ann")))

          // Apply new value
          _ <- CloseTo(annRex).weight(2).update
          _ <- Person.name_("Ann").CloseTo.weight.Animal.name.get.map(_ ==> List((2, "Rex")))
          _ <- Animal.name_("Rex").CloseTo.weight.Person.name.get.map(_ ==> List((2, "Ann")))

          // Retract value
          _ <- CloseTo(annRex).weight().update
          _ <- Person.name_("Ann").CloseTo.weight.Animal.name.get.map(_ ==> List())
          _ <- Animal.name_("Rex").CloseTo.weight.Person.name.get.map(_ ==> List())
        } yield ()
      }

      "enum" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, rex, ann, annRex, rexAnn) <- testData

          // Current howWeMet enum value
          _ <- Person.name_("Ann").CloseTo.howWeMet.Animal.name.get.map(_ ==> List(("inSchool", "Rex")))
          _ <- Animal.name_("Rex").CloseTo.howWeMet.Person.name.get.map(_ ==> List(("inSchool", "Ann")))

          // Apply new enum value
          _ <- CloseTo(annRex).howWeMet("throughFriend").update
          _ <- Person.name_("Ann").CloseTo.howWeMet.Animal.name.get.map(_ ==> List(("throughFriend", "Rex")))
          _ <- Animal.name_("Rex").CloseTo.howWeMet.Person.name.get.map(_ ==> List(("throughFriend", "Ann")))

          // Retract enum value
          _ <- CloseTo(annRex).howWeMet().update
          _ <- Person.name("Ann").CloseTo.howWeMet.get.map(_ ==> List())
          _ <- Animal.name("Rex").CloseTo.howWeMet.get.map(_ ==> List())
        } yield ()
      }

      "ref" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, rex, ann, annRex, rexAnn) <- testData

          // Current value
          _ <- Person.name_("Ann").CloseTo.CoreQuality.name._CloseTo.Animal.name.get.map(_ ==> List(("Love", "Rex")))
          _ <- Animal.name_("Rex").CloseTo.CoreQuality.name._CloseTo.Person.name.get.map(_ ==> List(("Love", "Ann")))

          // We can't update across namespaces
          _ <- CloseTo(annRex).CoreQuality.name("Compassion").update.recover { case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't span multiple namespaces like `Quality`."
          }

          // Instead we can either update the referenced entity or replace the reference to another existing Quality entity

          // 1. Update referenced value

          _ <- Quality(love).name("Compassion").update

          // Same reference, new value
          _ <- Person.name_("Ann").CloseTo.CoreQuality.name._CloseTo.Animal.name.get.map(_ ==> List(("Compassion", "Rex")))
          _ <- Animal.name_("Rex").CloseTo.CoreQuality.name._CloseTo.Person.name.get.map(_ ==> List(("Compassion", "Ann")))


          // 2. Update reference

          tx <- Quality.name("Trust").save
          trust = tx.eid
          _ <- CloseTo(annRex).coreQuality(trust).update

          // New reference/value
          _ <- Person.name_("Ann").CloseTo.CoreQuality.name._CloseTo.Animal.name.get.map(_ ==> List(("Trust", "Rex")))
          _ <- Animal.name_("Rex").CloseTo.CoreQuality.name._CloseTo.Person.name.get.map(_ ==> List(("Trust", "Ann")))


          // Retract reference
          _ <- CloseTo(annRex).coreQuality().update
          _ <- CloseTo(annRex).CoreQuality.name.get.map(_ ==> List())
        } yield ()
      }
    }


    "Card-many" - {

      "values" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, rex, ann, annRex, rexAnn) <- testData
          // Current values
          _ <- Person.name_("Ann").CloseTo.commonInterests.Animal.name.get.map(_ ==> List((Set("Food", "Travelling", "Walking"), "Rex")))
          _ <- Animal.name_("Rex").CloseTo.commonInterests.Person.name.get.map(_ ==> List((Set("Food", "Travelling", "Walking"), "Ann")))

          // Replace
          _ <- CloseTo(annRex).commonInterests.replace("Food" -> "Cuisine").update
          _ <- Person.name_("Ann").CloseTo.commonInterests.Animal.name.get.map(_ ==> List((Set("Travelling", "Walking", "Cuisine"), "Rex")))
          _ <- Animal.name_("Rex").CloseTo.commonInterests.Person.name.get.map(_ ==> List((Set("Travelling", "Walking", "Cuisine"), "Ann")))

          // Remove
          _ <- CloseTo(annRex).commonInterests.retract("Travelling").update
          _ <- Person.name_("Ann").CloseTo.commonInterests.Animal.name.get.map(_ ==> List((Set("Walking", "Cuisine"), "Rex")))
          _ <- Animal.name_("Rex").CloseTo.commonInterests.Person.name.get.map(_ ==> List((Set("Walking", "Cuisine"), "Ann")))

          // Add
          _ <- CloseTo(annRex).commonInterests.assert("Meditating").update
          _ <- Person.name_("Ann").CloseTo.commonInterests.Animal.name.get.map(_ ==> List((Set("Walking", "Cuisine", "Meditating"), "Rex")))
          _ <- Animal.name_("Rex").CloseTo.commonInterests.Person.name.get.map(_ ==> List((Set("Walking", "Cuisine", "Meditating"), "Ann")))

          // Apply new values
          _ <- CloseTo(annRex).commonInterests("Running", "Cycling").update
          _ <- Person.name_("Ann").CloseTo.commonInterests.Animal.name.get.map(_ ==> List((Set("Running", "Cycling"), "Rex")))
          _ <- Animal.name_("Rex").CloseTo.commonInterests.Person.name.get.map(_ ==> List((Set("Running", "Cycling"), "Ann")))

          // Retract all
          _ <- CloseTo(annRex).commonInterests().update
          _ <- Person.name_("Ann").CloseTo.commonInterests.get.map(_ ==> List())
          _ <- Animal.name_("Rex").CloseTo.commonInterests.get.map(_ ==> List())
        } yield ()
      }


      "enums" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, rex, ann, annRex, rexAnn) <- testData

          // Current enum values
          _ <- Person.name_("Ann").CloseTo.commonLicences.Animal.name.get.map(_ ==> List((Set("climbing", "flying"), "Rex")))
          _ <- Animal.name_("Rex").CloseTo.commonLicences.Person.name.get.map(_ ==> List((Set("climbing", "flying"), "Ann")))

          // Replace
          _ <- CloseTo(annRex).commonLicences.replace("flying" -> "diving").update
          _ <- Person.name_("Ann").CloseTo.commonLicences.Animal.name.get.map(_ ==> List((Set("climbing", "diving"), "Rex")))
          _ <- Animal.name_("Rex").CloseTo.commonLicences.Person.name.get.map(_ ==> List((Set("climbing", "diving"), "Ann")))

          // Remove
          _ <- CloseTo(annRex).commonLicences.retract("climbing").update
          _ <- Person.name_("Ann").CloseTo.commonLicences.Animal.name.get.map(_ ==> List((Set("diving"), "Rex")))
          _ <- Animal.name_("Rex").CloseTo.commonLicences.Person.name.get.map(_ ==> List((Set("diving"), "Ann")))

          // Add
          _ <- CloseTo(annRex).commonLicences.assert("parachuting").update
          _ <- Person.name_("Ann").CloseTo.commonLicences.Animal.name.get.map(_ ==> List((Set("diving", "parachuting"), "Rex")))
          _ <- Animal.name_("Rex").CloseTo.commonLicences.Person.name.get.map(_ ==> List((Set("diving", "parachuting"), "Ann")))

          // Apply new values
          _ <- CloseTo(annRex).commonLicences("climbing", "flying").update
          _ <- Person.name_("Ann").CloseTo.commonLicences.Animal.name.get.map(_ ==> List((Set("climbing", "flying"), "Rex")))
          _ <- Animal.name_("Rex").CloseTo.commonLicences.Person.name.get.map(_ ==> List((Set("climbing", "flying"), "Ann")))

          // Retract all
          _ <- CloseTo(annRex).commonLicences().update
          _ <- Person.name_("Ann").CloseTo.commonLicences.get.map(_ ==> List())
          _ <- Animal.name_("Rex").CloseTo.commonLicences.get.map(_ ==> List())
          _ <- Person.name("Ann" or "Rex").CloseTo.commonLicences.get.map(_ ==> List())
        } yield ()
      }


      "refs" - bidirectional { implicit conn =>
        for {
          Seq(love, patience, humor, rex, ann, annRex, rexAnn) <- testData

          // Current value
          _ <- Person.name_("Ann").CloseTo.Animal.name._CloseTo.InCommon.*(Quality.name).get.map(_ ==> List(("Rex", Seq("Patience", "Humor"))))
          _ <- Animal.name_("Rex").CloseTo.Person.name._CloseTo.InCommon.*(Quality.name).get.map(_ ==> List(("Ann", Seq("Patience", "Humor"))))

          // As with card-one references we have two choices to change referenced value(s)

          // 1. Update referenced value(s)

          _ <- Quality(patience).name("Waiting ability").update
          _ <- Quality(humor).name("Funny").update

          // Same references, new value(s)
          _ <- Person.name_("Ann").CloseTo.Animal.name._CloseTo.InCommon.*(Quality.name).get.map(_ ==> List(("Rex", Seq("Waiting ability", "Funny"))))
          _ <- Animal.name_("Rex").CloseTo.Person.name._CloseTo.InCommon.*(Quality.name).get.map(_ ==> List(("Ann", Seq("Waiting ability", "Funny"))))


          // 2. Update reference(s)

          tx <- Quality.name("Sporty").save
          sporty = tx.eid

          // replace
          _ <- CloseTo(annRex).inCommon.replace(humor -> sporty).update
          _ <- Person.name_("Ann").CloseTo.Animal.name._CloseTo.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Rex", Seq("Waiting ability", "Sporty")))
          )

          _ <- Animal.name_("Rex").CloseTo.Person.name._CloseTo.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Ann", Seq("Waiting ability", "Sporty")))
          )

          // remove
          _ <- CloseTo(annRex).inCommon.retract(patience).update
          _ <- Person.name_("Ann").CloseTo.Animal.name._CloseTo.InCommon.*(Quality.name).get.map(_ ==> List(("Rex", Seq("Sporty"))))
          _ <- Animal.name_("Rex").CloseTo.Person.name._CloseTo.InCommon.*(Quality.name).get.map(_ ==> List(("Ann", Seq("Sporty"))))

          // add
          _ <- CloseTo(annRex).inCommon.assert(patience).update
          _ <- Person.name_("Ann").CloseTo.Animal.name._CloseTo.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Rex", Seq("Waiting ability", "Sporty")))
          )

          _ <- Animal.name_("Rex").CloseTo.Person.name._CloseTo.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Ann", Seq("Waiting ability", "Sporty")))
          )

          // Apply new values
          _ <- CloseTo(annRex).inCommon(sporty, humor).update
          _ <- Person.name_("Ann").CloseTo.Animal.name._CloseTo.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Rex", Seq("Funny", "Sporty")))
          )

          _ <- Animal.name_("Rex").CloseTo.Person.name._CloseTo.InCommon.*(Quality.name).get.map(
            _.map(p => (p._1.sorted, p._2)) ==> List(("Ann", Seq("Funny", "Sporty")))
          )

          // Retract all references
          _ <- CloseTo(annRex).inCommon().update
          _ <- Person.name_("Ann").CloseTo.Animal.name._CloseTo.InCommon.*(Quality.name).get.map(_ ==> List())
          _ <- Animal.name_("Rex").CloseTo.Person.name._CloseTo.InCommon.*(Quality.name).get.map(_ ==> List())

        } yield ()
      }
    }


    "Map" - bidirectional { implicit conn =>
      for {
        Seq(love, patience, humor, rex, ann, annRex, rexAnn) <- testData

        // Current values
        _ <- Person.name_("Ann").CloseTo.commonScores.Animal.name.get.map(_ ==> List((Map("baseball" -> 9, "golf" -> 7), "Rex")))
        _ <- Animal.name_("Rex").CloseTo.commonScores.Person.name.get.map(_ ==> List((Map("baseball" -> 9, "golf" -> 7), "Ann")))

        // Replace values by key
        _ <- CloseTo(annRex).commonScores.replace("baseball" -> 8, "golf" -> 6).update
        _ <- Person.name_("Ann").CloseTo.commonScores.Animal.name.get.map(_ ==> List((Map("baseball" -> 8, "golf" -> 6), "Rex")))
        _ <- Animal.name_("Rex").CloseTo.commonScores.Person.name.get.map(_ ==> List((Map("baseball" -> 8, "golf" -> 6), "Ann")))

        // Remove by key
        _ <- CloseTo(annRex).commonScores.retract("golf").update
        _ <- Person.name_("Ann").CloseTo.commonScores.Animal.name.get.map(_ ==> List((Map("baseball" -> 8), "Rex")))
        _ <- Animal.name_("Rex").CloseTo.commonScores.Person.name.get.map(_ ==> List((Map("baseball" -> 8), "Ann")))

        // Add
        _ <- CloseTo(annRex).commonScores.assert("parachuting" -> 4).update
        _ <- Person.name_("Ann").CloseTo.commonScores.Animal.name.get.map(_ ==> List((Map("baseball" -> 8, "parachuting" -> 4), "Rex")))
        _ <- Animal.name_("Rex").CloseTo.commonScores.Person.name.get.map(_ ==> List((Map("baseball" -> 8, "parachuting" -> 4), "Ann")))

        // Apply new values (replacing all current values!)
        _ <- CloseTo(annRex).commonScores("volleyball" -> 4, "handball" -> 5).update
        _ <- Person.name_("Ann").CloseTo.commonScores.Animal.name.get.map(_ ==> List((Map("volleyball" -> 4, "handball" -> 5), "Rex")))
        _ <- Animal.name_("Rex").CloseTo.commonScores.Person.name.get.map(_ ==> List((Map("volleyball" -> 4, "handball" -> 5), "Ann")))

        // Delete all
        _ <- CloseTo(annRex).commonScores().update
        _ <- Person.name_("Ann").CloseTo.commonScores.Animal.name.get.map(_ ==> List())
        _ <- Animal.name_("Rex").CloseTo.commonScores.Person.name.get.map(_ ==> List())
      } yield ()
    }
  }
}
