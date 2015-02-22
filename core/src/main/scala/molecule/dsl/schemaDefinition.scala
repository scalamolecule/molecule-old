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
//  trait Tree

  // See http://docs.neo4j.org/chunked/stable/cypher-cookbook-hyperedges.html
//  trait HyperEdge


  private[molecule] sealed trait scalarAttr[T] {
    def doc(s: String): T = ???
    val indexed  : T = ???
    val noHistory: T = ???
    val uniqueValue   : oneString = ???
    val uniqueIdentity: oneString = ???
  }


  // String
  object oneString extends oneString
  trait oneString extends scalarAttr[oneString] {
    val fullTextSearch: oneString = ???
  }
  object manyString extends manyString
  trait manyString extends scalarAttr[manyString] {
    val fullTextSearch: manyString = ???
  }

  // Todo: Specialized attributes with constraints
  object oneEmail extends oneEmail
  trait oneEmail extends oneString

  // Number .....................
  trait number[T] extends scalarAttr[T] {
    // One by one
    val count   : T = ???
    val avg     : T = ???
    val median  : T = ???
    val variance: T = ???
    val stddev  : T = ???

    // All
    val aggregates: T = ???
  }

  // Int
  object oneInt extends oneInt
  trait oneInt extends number[oneInt] with scalarAttr[oneInt]

  object manyInt extends manyInt
  trait manyInt extends number[manyInt] with scalarAttr[manyInt]

  // Long
  object oneLong extends oneLong
  trait oneLong extends number[oneLong] with scalarAttr[oneLong]

  object manyLong extends manyLong
  trait manyLong extends number[manyLong] with scalarAttr[manyLong]

  // Float
  object oneFloat extends oneFloat
  trait oneFloat extends number[oneFloat] with scalarAttr[oneFloat]

  object manyFloat extends manyFloat
  trait manyFloat extends number[manyFloat] with scalarAttr[manyFloat]

  // Double
  object oneDouble extends oneDouble
  trait oneDouble extends number[oneDouble] with scalarAttr[oneDouble]

  object manyDouble extends manyDouble
  trait manyDouble extends number[manyDouble] with scalarAttr[manyDouble]

  // Boolean
  object oneBoolean extends oneBoolean
  trait oneBoolean extends scalarAttr[oneBoolean]

  object manyBoolean extends manyBoolean
  trait manyBoolean extends scalarAttr[manyBoolean]

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
  object _one {
    def apply[NS] = this
    lazy val subComponent = this
    lazy val _subComponent = this
    lazy val tree         = this
    def ~(other: one.type) = this
    def ~(other: many.type) = this
  }
  object one {
    def apply[NS] = this
//    def apply[NS, Ns2] = this
    lazy val subComponent = this
    lazy val _subComponent = this
    lazy val tree         = this
    def ~(other: one.type) = this
    def ~(other: many.type) = this
  }

  object _many {
    def apply[NS1] = this
//    def apply[Ns1, Ns2] = this
    def _many[Ns2] = this

    lazy val _subComponents = this
    lazy val subComponents = this
    lazy val trees         = this
    def withOptional(other: many.type) = this
    def ~(other: one.type) = this
    def ~(other: many.type) = this
  }
  object many {
    def apply[NS1] = this
    lazy val _subComponents = this
    lazy val subComponents = this
    lazy val trees         = this
    def withOptional(other: many.type) = this
    def ~(other: one.type) = this
    def ~(other: many.type) = this
  }

  // Hyper edge
//  object hyper {
//    def apply[NS, NS2] = this
//  }
}

