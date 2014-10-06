package molecule.dsl
import scala.annotation.StaticAnnotation


object schemaDefinition {

  // Annotation for arities of molecule inputs and outputs
  class InOut(inputArity: Int = 3, outputArity: Int = 8) extends StaticAnnotation

  // Todo: Use phantom types to build valid combinations (checkout how Rogue does it)


  //  trait PropertyEdge[From, To]
  //  trait SubComponentOf2[From, To]

  // Adjacency List Model
  // See https://github.com/tinkerpop/gremlin/wiki/Tree-Pattern
  trait Tree

  // See http://docs.neo4j.org/chunked/stable/cypher-cookbook-hyperedges.html
  trait HyperEdge


  private[molecule] sealed trait scalarAttr[T] {self: T =>
    def doc(s: String) = self
    lazy val indexed   = self
    lazy val noHistory = self
  }


  // String
  object oneString extends oneString
  trait oneString extends scalarAttr[oneString] {
    lazy val fullTextSearch = this
    lazy val uniqueValue    = this
    lazy val uniqueIdentity = this
  }
  object manyString extends manyString
  trait manyString extends scalarAttr[manyString] {
    lazy val fullTextSearch = this
  }

  // Todo: Specialized attributes with constraints
  object oneEmail extends oneEmail
  trait oneEmail extends scalarAttr[oneEmail] {
    lazy val fullTextSearch = this
    lazy val uniqueValue    = this
    lazy val uniqueIdentity = this
  }

  // Int
  object oneInt extends oneInt
  trait oneInt extends scalarAttr[oneInt]

  object manyInt extends manyInt
  trait manyInt extends scalarAttr[manyInt]

  // Long
  object oneLong extends oneLong
  trait oneLong extends scalarAttr[oneLong]

  object manyLong extends manyLong
  trait manyLong extends scalarAttr[manyLong]

  // Float
  object oneFloat extends oneFloat
  trait oneFloat extends scalarAttr[oneFloat]

  object manyFloat extends manyFloat
  trait manyFloat extends scalarAttr[manyFloat]

  // Double
  object oneDouble extends oneDouble
  trait oneDouble extends scalarAttr[oneDouble]

  object manyDouble extends manyDouble
  trait manyDouble extends scalarAttr[manyDouble]

  // Boolean
  object oneBoolean extends oneBoolean
  trait oneBoolean extends scalarAttr[oneBoolean]

  // Date
  object oneDate extends oneDate
  trait oneDate extends scalarAttr[oneDate]

  object manyDate extends manyDate
  trait manyDate extends scalarAttr[manyDate]

  // UUID
  object oneUUID extends oneUUID
  trait oneUUID extends scalarAttr[oneUUID]

  object manyUUID extends manyUUID
  trait manyUUID extends scalarAttr[manyUUID]

  // URI
  object oneURI extends oneURI
  trait oneURI extends scalarAttr[oneURI]

  object manyURI extends manyURI
  trait manyURI extends scalarAttr[manyURI]

  // todo?
  // BigInt
  // BigDec
  // Bytes


  // Enum
  private[molecule] trait enum extends scalarAttr[enum] {
    // Require at least 2 enum values (any use case for only 1 enum??)
    def apply(e1: Symbol, e2: Symbol, es: Symbol*) = this
  }
  object oneEnum extends enum
  object manyEnum extends enum


  // Ref
  object one {
    def apply[NS] = this
    lazy val subComponent = this
    lazy val tree         = this
    def ~(other: one.type) = this
    def ~(other: many.type) = this
  }
  object many {
    def apply[NS1] = this
    //    def apply[NS1, NS2] = this
    lazy val subComponents = this
    lazy val trees         = this
    def withOptional(other: many.type) = this
    def ~(other: one.type) = this
    def ~(other: many.type) = this
  }

  // Hyper edge
  object hyper {
    def apply[NS, NS2] = this
  }
}

