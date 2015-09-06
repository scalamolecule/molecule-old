package molecule.part.schema
import molecule.dsl.schemaDefinition._

@InOut(0, 4)
trait PartitionTestDefinition {

  object gen {
    trait Person {
      val name   = oneString
      val gender = oneEnum('male, 'female)
    }
  }

  object lit {
    trait Book {
      val title  = oneString
      val author = one[gen.Person]
      val cat    = oneEnum('good, 'bad)
    }
  }
}