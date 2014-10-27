package molecule.examples.seattle.schema
import molecule.dsl.schemaDefinition._

@InOut(3, 8)
trait SeattleDefinition {

  trait Community {
    val name         = oneString.fullTextSearch
    val url          = oneString
    val category     = manyString.fullTextSearch
    val orgtype      = oneEnum('community, 'commercial, 'nonprofit, 'personal)
    val `type`       = oneEnum('email_list, 'twitter, 'facebook_page, 'blog, 'website, 'wiki, 'myspace, 'ning)
    val neighborhood = one[Neighborhood]
  }

  trait Neighborhood {
    val name     = oneString.fullTextSearch.uniqueIdentity
    val district = one[District]
  }

  trait District {
    val name   = oneString.fullTextSearch.uniqueIdentity
    val region = oneEnum('n, 'ne, 'e, 'se, 's, 'sw, 'w, 'nw)
  }
}