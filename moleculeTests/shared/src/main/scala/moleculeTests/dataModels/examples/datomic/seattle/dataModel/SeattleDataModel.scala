package moleculeTests.dataModels.examples.datomic.seattle.dataModel

import molecule.core.data.model._

@InOut(2, 8)
object SeattleDataModel {

  trait Community {
    val name         = oneString.fulltext.doc("A community's name")
    val url          = oneString.doc("A community's url")
    val category     = manyString.fulltext.doc("Community categories")
    val orgtype      = oneEnum("community", "commercial", "nonprofit", "personal").doc("Community organisation type")
    val tpe          = oneEnum("email_list", "twitter", "facebook_page", "blog", "website", "wiki", "myspace", "ning").alias("type").doc("Community type")
    val neighborhood = one[Neighborhood].doc("A community's neighborhood")
  }

  trait Neighborhood {
    val name     = oneString.doc("A unique neighborhood name")
    val district = one[District].doc("A neighborhood's district")
  }

  trait District {
    val name   = oneString.doc("A unique district name")
    val region = oneEnum("n", "ne", "e", "se", "s", "sw", "w", "nw").doc("A district region")
  }
}