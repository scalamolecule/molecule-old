package molecule.dsl
import scala.annotation.StaticAnnotation


object schemaDefinition {

  // Annotation for arities of molecule inputs and outputs
  class InOut(inputArity: Int = 3, outputArity: Int = 8) extends StaticAnnotation

  // Todo: Use phantom types to build valid combinations (checkout how Rogue does it)


  // Entity that can be attached as a subcomponent to another entity
  trait SubComponent
  trait SubComponentOf2[A, B] extends SubComponent
  trait SubComponentOf3[A, B, C] extends SubComponent
  trait SubComponentOf4[A, B, C, D] extends SubComponent
  // etc...

  private[molecule] sealed trait anyAttr {
    def doc(s: String) = this
    lazy val indexed   = this
    lazy val noHistory = this
  }

  // String
  object oneString extends anyAttr {
    lazy val fullTextSearch = this
    lazy val uniqueValue    = this
    lazy val uniqueIdentity = this
  }
  object manyString extends anyAttr {
    lazy val fullTextSearch = this
  }

  // Todo: Specialized attributes with constraints
  object oneEmail extends anyAttr {
    lazy val fullTextSearch = this
    lazy val uniqueValue    = this
    lazy val uniqueIdentity = this
  }

  // Int
  object oneInt extends anyAttr
  object manyInt extends anyAttr

  // Long
  object oneLong extends anyAttr
  object manyLong extends anyAttr

  // Float
  object oneFloat extends anyAttr
  object manyFloat extends anyAttr

  // Double
  object oneDouble extends anyAttr
  object manyDouble extends anyAttr

  // Boolean
  object oneBoolean extends anyAttr

  // Date
  object oneDate extends anyAttr
  object manyDate extends anyAttr

  // UUID
  object oneUUID extends anyAttr
  object manyUUID extends anyAttr

  // URI
  object oneURI extends anyAttr
  object manyURI extends anyAttr


  // Enum
  private[molecule] trait enum extends anyAttr {
    // Require at least 2 enum values (any use case for only 1 enum??)
    def apply(e1: Symbol, e2: Symbol, es: Symbol*) = this
  }
  object oneEnum extends enum
  object manyEnum extends enum


  // Ref
  object one {
    def apply[NS1] = this
    lazy val component = this
  }

  object many {
    def apply[NS1] = this
    lazy val components = this
  }
}

// todo?
// BigInt
// BigDec
// Bytes