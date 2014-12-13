package molecule.examples.dayOfDatomic.schema

import molecule.dsl.schemaDefinition._

@InOut(2, 4)
trait Graph2Definition {

  trait User {
    val name        = oneString.uniqueIdentity
    val roleInGroup = many[RoleInGroup]
  }

  trait RoleInGroup {
    val name  = oneString
    val roles = many[Role]
    val group = one[Group]
  }

  trait Group {
    val name  = oneString.uniqueIdentity
    val roles = many[Role]
  }

  trait Role {
    val name = oneString.uniqueIdentity
  }
}