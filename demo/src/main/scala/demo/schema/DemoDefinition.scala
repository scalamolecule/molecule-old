package demo.schema
import molecule.dsl.schemaDefinition._


@InOut(0, 3)
trait DemoDefinition {

  trait Person {
    val name     = oneString.fullTextSearch
    val age      = oneInt
    val gender   = oneEnum('male, 'female)
  }
}