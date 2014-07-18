//package molecule.examples.dayOfDatomic
//
//import scala.language.postfixOps
//import molecule.ast.schemaDefinition
//import molecule.dsl.DefinitionDSL
//object AggregatesDefinition extends DefinitionDSL {
//  import schemaDefinition._
//
//  trait Obj {
//    lazy val name = string ~ indexed
//    val meanRadius = double ~ indexed
//  }
//  trait Data {
//    lazy val source = string ~ uniqueIdentity
//  }
//}