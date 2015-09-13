package molecule.part.schema
import molecule.dsl.schemaDefinition._

@InOut(0, 4)
object PartitionTestDefinition {

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
      // To avoid attr/partition name clashes we can prepend the definition object name
      // (in case we would have needed an attribute named `gen` for instance)
      val editor = one[PartitionTestDefinition.gen.Person]
      val cat    = oneEnum('good, 'bad)
    }
  }
}