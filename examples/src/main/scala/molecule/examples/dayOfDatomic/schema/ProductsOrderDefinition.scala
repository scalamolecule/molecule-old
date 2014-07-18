package molecule.examples.dayOfDatomic.schema
import molecule.dsl.schemaDefinition._

// See: http://blog.datomic.com/2013/06/component-entities.html

@InOut(3, 8)
trait ProductsOrderDefinition {

  trait User {
    val orders = many[Order]
  }

  trait Order {
    val lineItems = many[LineItem].components
  }

  trait LineItem {
    val quantity = oneInt
    val price    = oneDouble
    val product  = one[Product]
  }

  trait Product {
    val description = oneString.indexed
  }
}