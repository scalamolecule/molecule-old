package moleculeTests.dataModels.examples.datomic.dayOfDatomic.dataModel

import molecule.core.data.model._

@InOut(3, 6)
object DbDataModel {

  trait Db {
    val valueType = oneString
  }
}