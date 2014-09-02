package molecule.examples.dayOfDatomic.schema

import molecule.dsl.schemaDefinition._

// See: http://blog.datomic.com/2013/06/component-entities.html

@InOut(3, 8)
trait ProductsOrderDefinition {

  trait Order {
    val lineItems = many[LineItem].subComponents
  }

  trait LineItem {
    val product  = one[Product]
    val price    = oneDouble
    val quantity = oneInt
  }

  trait Product {
    val description = oneString.indexed
  }
}
