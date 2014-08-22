package molecule.dsl
import scala.annotation.StaticAnnotation


object schemaDefinition {

  // Annotation for arities of molecule inputs and outputs
  class InOut(inputArity: Int = 3, outputArity: Int = 8) extends StaticAnnotation

  // Todo: Use phantom types to build valid combinations (checkout how Rogue does it)


  trait PropertyEdge[From, To]
  trait SubComponentOf2[From, To]

  // Adjacency List Model
  // See https://github.com/tinkerpop/gremlin/wiki/Tree-Pattern
  trait Tree


  private[molecule] sealed trait scalarAttr {
    def doc(s: String) = this
    lazy val indexed   = this
    lazy val noHistory = this
  }


  // String
  object oneString extends scalarAttr {
    lazy val fullTextSearch = this
    lazy val uniqueValue    = this
    lazy val uniqueIdentity = this
  }
  object manyString extends scalarAttr {
    lazy val fullTextSearch = this
  }

  // Todo: Specialized attributes with constraints
  object oneEmail extends scalarAttr {
    lazy val fullTextSearch = this
    lazy val uniqueValue    = this
    lazy val uniqueIdentity = this
  }

  // Int
  object oneInt extends scalarAttr
  object manyInt extends scalarAttr

  // Long
  object oneLong extends scalarAttr
  object manyLong extends scalarAttr

  // Float
  object oneFloat extends scalarAttr
  object manyFloat extends scalarAttr

  // Double
  object oneDouble extends scalarAttr
  object manyDouble extends scalarAttr

  // Boolean
  object oneBoolean extends scalarAttr

  // Date
  object oneDate extends scalarAttr
  object manyDate extends scalarAttr

  // UUID
  object oneUUID extends scalarAttr
  object manyUUID extends scalarAttr

  // URI
  object oneURI extends scalarAttr
  object manyURI extends scalarAttr

  // todo?
  // BigInt
  // BigDec
  // Bytes


  // Enum
  private[molecule] trait enum extends scalarAttr {
    // Require at least 2 enum values (any use case for only 1 enum??)
    def apply(e1: Symbol, e2: Symbol, es: Symbol*) = this
  }
  object oneEnum extends enum
  object manyEnum extends enum


  // Ref
  object one {
    def apply[NS] = this
    lazy val subComponent = this
    lazy val tree = this
  }
  object many {
    def apply[NS1] = this
    lazy val subComponents = this
    lazy val trees = this
  }
}

