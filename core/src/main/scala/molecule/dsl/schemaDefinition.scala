package molecule.dsl

import scala.annotation.StaticAnnotation


object schemaDefinition {

  // Annotation for arities of molecule inputs and outputs
  class InOut(inputArity: Int = 3, outputArity: Int = 8) extends StaticAnnotation

  sealed trait anyAttr {
    def doc(s: String) = this
    lazy val noHistory = this
  }

  // String
  object oneString extends anyAttr {
    lazy val fullTextSearch = this
    lazy val uniqueValue    = this
    lazy val uniqueIdentity = this
  }
  object manyStrings extends anyAttr {
    lazy val fullTextSearch = this
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
    // Requiring at least 2 values
    def apply(v1: Symbol, v2: Symbol, vs: Symbol*) = this
  }
  object oneEnum extends enum
  object manyEnums extends enum

  // Ref
  trait One[Ref]
  object oneRef {
    def apply[NS] = 7
  }

  // todo?
  // BigInt
  // BigDec
  // Bytes
}