package moleculeTests.dataModels.examples.datomic.dayOfDatomic.dataModel

import molecule.core.data.model._

@InOut(0, 5)
object AggregatesDataModel {

  trait Obj {
    val name       = oneString.index.doc("Name of a Solar System object.")
    val meanRadius = oneDouble.index.doc("Mean radius of an object.")
  }

  trait Data {
    val source = oneString.index.doc("Source of the data in a transaction.")
  }


  trait Monster {
    val name    = oneString
    val heads   = oneInt
  }
}