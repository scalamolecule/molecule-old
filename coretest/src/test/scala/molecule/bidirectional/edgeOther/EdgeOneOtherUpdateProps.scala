//package molecule.bidirectional.edgeOther
//
//import molecule._
//import molecule.bidirectional.dsl.bidirectional._
//import molecule.bidirectional.schema.BidirectionalSchema
//import molecule.util._
//import org.specs2.specification.Scope
//
//class EdgeOneOtherUpdateProps extends MoleculeSpec {
//
//  class Setup extends Scope with DatomicFacade {
//    implicit val conn = recreateDbFrom(BidirectionalSchema)
//
//    val love                    = living_Quality.name("Love").save.eid
//    val List(patience, humor)   = living_Quality.name.insert("Patience", "Humor").eids
//    val List(ann, annBen, _, _) = living_Person.name("Ann")
//      .Favorite
//      .weight(7)
//      .howWeMet("atWork")
//      .commonInterests("Food", "Walking", "Travelling")
//      .commonLicences("climbing", "flying")
//      .commonScores(Seq("golf" -> 7, "baseball" -> 9))
//      .coreQuality(love)
//      .inCommon(Seq(patience, humor))
//      .Person.name("Ben")
//      .save.eids
//
//    // All edge properties have been saved in both directions
//    living_Person.name.Favorite
//      .weight
//      .howWeMet
//      .commonInterests
//      .commonLicences
//      .commonScores
//      .CoreQuality.name._Favorite
//      .InCommon.*(living_Quality.name)._Favorite
//      .Person.name
//      .get === List(
//      ("Ann"
//        , 7
//        , "atWork"
//        , Set("Food", "Walking", "Travelling")
//        , Set("climbing", "flying")
//        , Map("baseball" -> 9, "golf" -> 7)
//        , "Love"
//        , List("Patience", "Humor")
//        , "Ben"),
//      ("Ben"
//        , 7
//        , "atWork"
//        , Set("Food", "Walking", "Travelling")
//        , Set("climbing", "flying")
//        , Map("baseball" -> 9, "golf" -> 7)
//        , "Love"
//        , List("Patience", "Humor")
//        , "Ann")
//    )
//  }
//
//
//  "Card-one" >> {
//
//    "value" in new Setup {
//
//      // Updating edge properties from the base entity is not allowed
//      (living_Person(ann).Favorite.howWeMet("inSchool").update must throwA[IllegalArgumentException])
//        .message === "Got the exception java.lang.IllegalArgumentException: " +
//        s"[molecule.api.CheckModel.save_edgeCompleteª]  Can't update edge `living_Favorite` " +
//        s"of base entity `living_Person` without knowing which target entity the edge is pointing too. " +
//        s"Please update the edge itself, like `living_Favorite(<edgeId>).edgeProperty(<new value>).update`."
//
//      // Instead update the edge entity itself:
//
//      // Current weight value
//      living_Person.name("Ann" or "Ben").Favorite.weight.get.sortBy(_._1) === List(
//        ("Ann", 7),
//        ("Ben", 7)
//      )
//
//      // Apply new value
//      living_Favorite(annBen).weight(2).update
//      living_Person.name("Ann" or "Ben").Favorite.weight.get.sortBy(_._1) === List(
//        ("Ann", 2),
//        ("Ben", 2)
//      )
//
//      // Retract value
//      living_Favorite(annBen).weight().update
//      living_Person.name("Ann" or "Ben").Favorite.weight.get === List()
//    }
//
//
//    "enum" in new Setup {
//
//      // Current howWeMet enum value
//      living_Person.name("Ann" or "Ben").Favorite.howWeMet.get.sortBy(_._1) === List(
//        ("Ann", "atWork"),
//        ("Ben", "atWork")
//      )
//
//      // Apply new enum value
//      living_Favorite(annBen).howWeMet("throughFriend").update
//      living_Person.name("Ann" or "Ben").Favorite.howWeMet.get.sortBy(_._1) === List(
//        ("Ann", "throughFriend"),
//        ("Ben", "throughFriend")
//      )
//
//      // Retract enum value
//      living_Favorite(annBen).howWeMet().update
//      living_Person.name("Ann" or "Ben").Favorite.howWeMet.get === List()
//    }
//
//
//    "ref" in new Setup {
//
//      // Current value
//      living_Person.name("Ann" or "Ben").Favorite.CoreQuality.name.get.sortBy(_._1) === List(
//        ("Ann", "Love"),
//        ("Ben", "Love")
//      )
//
//      // We can't update across namespaces
//      (living_Favorite(annBen).CoreQuality.name("Compassion").update must throwA[IllegalArgumentException])
//        .message === "Got the exception java.lang.IllegalArgumentException: " +
//        s"[molecule.api.CheckModel.update_onlyOneNs]  Update molecules can't span multiple namespaces like `living_Quality`."
//
//      // Instead we can either update the referenced entity or replace the reference to another existing Quality entity
//
//      // 1. Update referenced value
//
//      living_Quality(love).name("Compassion").update
//
//      // Same reference, new value
//      living_Person.name("Ann" or "Ben").Favorite.CoreQuality.name.get.sortBy(_._1) === List(
//        ("Ann", "Compassion"),
//        ("Ben", "Compassion")
//      )
//
//
//      // 2. Update reference
//
//      val trust = living_Quality.name("Trust").save.eid
//      living_Favorite(annBen).coreQuality(trust).update
//
//      // New reference/value
//      living_Person.name("Ann" or "Ben").Favorite.CoreQuality.name.get.sortBy(_._1) === List(
//        ("Ann", "Trust"),
//        ("Ben", "Trust")
//      )
//
//      // Retract reference
//      living_Favorite(annBen).coreQuality().update
//      living_Favorite(annBen).CoreQuality.name.get === List()
//    }
//  }
//
//
//  "Card-many" >> {
//
//    "values" in new Setup {
//
//      // Current values
//      living_Person.name("Ann" or "Ben").Favorite.commonInterests.get.sortBy(_._1) === List(
//        ("Ann", Set("Food", "Walking", "Travelling")),
//        ("Ben", Set("Food", "Walking", "Travelling"))
//      )
//
//      // Replace
//      living_Favorite(annBen).commonInterests.replace("Food" -> "Cuisine").update
//      living_Person.name("Ann" or "Ben").Favorite.commonInterests.get.sortBy(_._1) === List(
//        ("Ann", Set("Cuisine", "Walking", "Travelling")),
//        ("Ben", Set("Cuisine", "Walking", "Travelling"))
//      )
//
//      // Remove
//      living_Favorite(annBen).commonInterests.remove("Travelling").update
//      living_Person.name("Ann" or "Ben").Favorite.commonInterests.get.sortBy(_._1) === List(
//        ("Ann", Set("Cuisine", "Walking")),
//        ("Ben", Set("Cuisine", "Walking"))
//      )
//
//      // Add
//      living_Favorite(annBen).commonInterests.add("Meditating").update
//      living_Person.name("Ann" or "Ben").Favorite.commonInterests.get.sortBy(_._1) === List(
//        ("Ann", Set("Cuisine", "Walking", "Meditating")),
//        ("Ben", Set("Cuisine", "Walking", "Meditating"))
//      )
//
//      // Apply new values
//      living_Favorite(annBen).commonInterests("Running", "Cycling").update
//      living_Person.name("Ann" or "Ben").Favorite.commonInterests.get.sortBy(_._1) === List(
//        ("Ann", Set("Running", "Cycling")),
//        ("Ben", Set("Running", "Cycling"))
//      )
//
//      // Retract all
//      living_Favorite(annBen).commonInterests().update
//      living_Person.name("Ann" or "Ben").Favorite.commonInterests.get === List()
//    }
//
//
//    "enums" in new Setup {
//
//      // Current enum values
//      living_Person.name("Ann" or "Ben").Favorite.commonLicences.get.sortBy(_._1) === List(
//        ("Ann", Set("climbing", "flying")),
//        ("Ben", Set("climbing", "flying"))
//      )
//
//      // Replace
//      living_Favorite(annBen).commonLicences.replace("flying" -> "diving").update
//      living_Person.name("Ann" or "Ben").Favorite.commonLicences.get.sortBy(_._1) === List(
//        ("Ann", Set("climbing", "diving")),
//        ("Ben", Set("climbing", "diving"))
//      )
//
//      // Remove
//      living_Favorite(annBen).commonLicences.remove("climbing").update
//      living_Person.name("Ann" or "Ben").Favorite.commonLicences.get.sortBy(_._1) === List(
//        ("Ann", Set("diving")),
//        ("Ben", Set("diving"))
//      )
//
//      // Add
//      living_Favorite(annBen).commonLicences.add("parachuting").update
//      living_Person.name("Ann" or "Ben").Favorite.commonLicences.get.sortBy(_._1) === List(
//        ("Ann", Set("diving", "parachuting")),
//        ("Ben", Set("diving", "parachuting"))
//      )
//
//      // Apply new values
//      living_Favorite(annBen).commonLicences("climbing", "flying").update
//      living_Person.name("Ann" or "Ben").Favorite.commonLicences.get.sortBy(_._1) === List(
//        ("Ann", Set("climbing", "flying")),
//        ("Ben", Set("climbing", "flying"))
//      )
//
//      // Retract all
//      living_Favorite(annBen).commonLicences().update
//      living_Person.name("Ann" or "Ben").Favorite.commonLicences.get === List()
//    }
//
//
//    "refs" in new Setup {
//
//      // Current value
//      living_Person.name("Ann" or "Ben").Favorite.InCommon.*(living_Quality.name).get.sortBy(_._1) === List(
//        ("Ann", List("Patience", "Humor")),
//        ("Ben", List("Patience", "Humor"))
//      )
//
//      // As with card-one references we have two choices to change referenced value(s)
//
//      // 1. Update referenced value(s)
//
//      living_Quality(patience).name("Waiting ability").update
//      living_Quality(humor).name("Funny").update
//
//      // Same references, new value(s)
//      living_Person.name("Ann" or "Ben").Favorite.InCommon.*(living_Quality.name).get.sortBy(_._1) === List(
//        ("Ann", List("Waiting ability", "Funny")),
//        ("Ben", List("Waiting ability", "Funny"))
//      )
//
//
//      // 2. Update reference(s)
//
//      val sporty = living_Quality.name("Sporty").save.eid
//
//      // replace
//      living_Favorite(annBen).inCommon.replace(humor -> sporty).update
//      living_Person.name("Ann" or "Ben").Favorite.InCommon.*(living_Quality.name).get.sortBy(_._1) === List(
//        ("Ann", List("Waiting ability", "Sporty")),
//        ("Ben", List("Waiting ability", "Sporty"))
//      )
//
//      // remove
//      living_Favorite(annBen).inCommon.remove(patience).update
//      living_Person.name("Ann" or "Ben").Favorite.InCommon.*(living_Quality.name).get.sortBy(_._1) === List(
//        ("Ann", List("Sporty")),
//        ("Ben", List("Sporty"))
//      )
//
//      // add
//      living_Favorite(annBen).inCommon.add(patience).update
//      living_Person.name("Ann" or "Ben").Favorite.InCommon.*(living_Quality.name).get.sortBy(_._1) === List(
//        ("Ann", List("Waiting ability", "Sporty")),
//        ("Ben", List("Waiting ability", "Sporty"))
//      )
//
//      // Apply new values
//      living_Favorite(annBen).inCommon(sporty, humor).update
//      living_Person.name("Ann" or "Ben").Favorite.InCommon.*(living_Quality.name).get.sortBy(_._1) === List(
//        ("Ann", List("Funny", "Sporty")),
//        ("Ben", List("Funny", "Sporty"))
//      )
//
//      // Retract all references
//      living_Favorite(annBen).inCommon().update
//      living_Person.name("Ann" or "Ben").Favorite.InCommon.*(living_Quality.name).get === List()
//    }
//  }
//
//
//  "Map" in new Setup {
//
//    // Current values
//    living_Person.name("Ann" or "Ben").Favorite.commonScores.get.sortBy(_._1) === List(
//      ("Ann", Map("baseball" -> 9, "golf" -> 7)),
//      ("Ben", Map("baseball" -> 9, "golf" -> 7))
//    )
//
//    // Replace values by key
//    living_Favorite(annBen).commonScores.replace("baseball" -> 8, "golf" -> 6).update
//    living_Person.name("Ann" or "Ben").Favorite.commonScores.get.sortBy(_._1) === List(
//      ("Ann", Map("baseball" -> 8, "golf" -> 6)),
//      ("Ben", Map("baseball" -> 8, "golf" -> 6))
//    )
//
//    // Remove by key
//    living_Favorite(annBen).commonScores.remove("golf").update
//    living_Person.name("Ann" or "Ben").Favorite.commonScores.get.sortBy(_._1) === List(
//      ("Ann", Map("baseball" -> 8)),
//      ("Ben", Map("baseball" -> 8))
//    )
//
//    // Add
//    living_Favorite(annBen).commonScores.add("parachuting" -> 4).update
//    living_Person.name("Ann" or "Ben").Favorite.commonScores.get.sortBy(_._1) === List(
//      ("Ann", Map("baseball" -> 8, "parachuting" -> 4)),
//      ("Ben", Map("baseball" -> 8, "parachuting" -> 4))
//    )
//
//    // Apply new values (replacing all current values!)
//    living_Favorite(annBen).commonScores("volleball" -> 4, "handball" -> 5).update
//    living_Person.name("Ann" or "Ben").Favorite.commonScores.get.sortBy(_._1) === List(
//      ("Ann", Map("volleball" -> 4, "handball" -> 5)),
//      ("Ben", Map("volleball" -> 4, "handball" -> 5))
//    )
//
//    // Delete all
//    living_Favorite(annBen).commonScores().update
//    living_Person.name("Ann" or "Ben").Favorite.commonScores.get === List()
//  }
//}
