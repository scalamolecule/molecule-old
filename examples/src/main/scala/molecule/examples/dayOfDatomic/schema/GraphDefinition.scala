package molecule.examples.dayOfDatomic.schema

import molecule.util.dsl.schemaDefinition._

@InOut(0, 4)
trait GraphDefinition {

  trait User {
    val name        = oneString.uniqueIdentity
    val roleInGroup = one[RoleInGroup]
  }

  trait RoleInGroup extends HyperEdge {
    val role  = one[Role]
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