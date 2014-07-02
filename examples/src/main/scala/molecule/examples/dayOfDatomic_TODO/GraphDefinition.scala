//package molecule.examples.dayOfDatomic
//import scala.language.postfixOps
//import molecule.ast.schemaDefinition
//import molecule.dsl.DefinitionDSL
//object GraphDefinition extends DefinitionDSL {
//  import schemaDefinition._
//  // lazy could add an apply method so that you can say
//  // User("John")
//  // Therefore: only 1 lazy val allowed!
//  trait User {
//    lazy val name = string ~ uniqueIdentity
//    // Hyper-edge involving 3 entities
//    val Groups = Many[Group] withOptional Many[Role]
//  }
//  trait Group {
//    lazy val name = string ~ uniqueIdentity
//    val Roles = Many[Role]
//  }
//  trait Role {
//    lazy val name = string ~ uniqueIdentity
//  }
//}