package molecule.examples.dayOfDatomic.schema

import molecule.util.dsl.schemaDefinition._

@InOut(3, 6)
trait DbDefinition {

  trait Db {
    val valueType = oneString
    val txInstant = oneDate
  }
}