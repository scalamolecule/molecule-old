package molecule.examples.seattle.schema
import molecule.dsl.schemaDefinition
import schemaDefinition._


@InOut(3, 8)
trait Community {
  val name         = oneString.fullTextSearch
  val url          = oneString.fullTextSearch
  val category     = manyStrings.fullTextSearch
  val orgtype      = oneEnum('community, 'commercial, 'nonprofit, 'personal)
  val `type`       = oneEnum('email_list, 'twitter, 'facebook_page, 'blog, 'website, 'wiki, 'myspace, 'ning)
  val neighborhood = oneRef[Neighborhood]
}

trait Neighborhood {
  val name     = oneString.fullTextSearch.uniqueIdentity
  val district = oneRef[District]
}

trait District {
  val name   = oneString.fullTextSearch.uniqueIdentity
  val region = oneEnum('n, 'ne, 'e, 'se, 's, 'sw, 'w, 'nw)
}