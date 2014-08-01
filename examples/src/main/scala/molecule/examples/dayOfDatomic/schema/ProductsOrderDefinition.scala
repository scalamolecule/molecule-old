package molecule.examples.dayOfDatomic.schema
import molecule.dsl.schemaDefinition._

// See: http://blog.datomic.com/2013/06/component-entities.html

@InOut(3, 8)
trait ProductsOrderDefinition {

  trait Order {
    val id        = oneString
    val lineItems = many[LineItem].components
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


//trait User {
//  val orders = many[Order]
//}
//
//trait OrderSystem {
//  val note = oneString
//}