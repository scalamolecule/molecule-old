package moleculeTests.dataModels.examples.gremlin.gettingStarted.dataModel

import molecule.core.data.model._

@InOut(0, 5)
object ModernGraph1DataModel {

  trait Person {
    val name = oneString
    val age  = oneInt

    // Normal (uni-directional) reference
    val software = many[Software]

    // Bidirectional self-reference
    val friends = manyBi[Person]
  }

  trait Software {
    val name = oneString
    val lang = oneString
  }
}