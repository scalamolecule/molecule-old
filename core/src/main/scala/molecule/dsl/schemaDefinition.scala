package molecule.dsl
import scala.annotation.StaticAnnotation


object schemaDefinition {

  // Annotation for arities of molecule inputs and outputs
  class InOut(inputArity: Int = 3, outputArity: Int = 8) extends StaticAnnotation

  // Todo: Use phantom types to build valid combinations (checkout how Rogue does it)


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
  object mapString extends mapString
  trait mapString extends scalarAttr[mapString] {
    val fullTextSearch: mapString = ???
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

  object mapInt extends mapInt
  trait mapInt extends number[mapInt] with scalarAttr[mapInt]

  // Long
  object oneLong extends oneLong
  trait oneLong extends number[oneLong] with scalarAttr[oneLong]

  object manyLong extends manyLong
  trait manyLong extends number[manyLong] with scalarAttr[manyLong]

  object mapLong extends mapLong
  trait mapLong extends number[mapLong] with scalarAttr[mapLong]

  // Float
  object oneFloat extends oneFloat
  trait oneFloat extends number[oneFloat] with scalarAttr[oneFloat]

  object manyFloat extends manyFloat
  trait manyFloat extends number[manyFloat] with scalarAttr[manyFloat]

  object mapFloat extends mapFloat
  trait mapFloat extends number[mapFloat] with scalarAttr[mapFloat]

  // Double
  object oneDouble extends oneDouble
  trait oneDouble extends number[oneDouble] with scalarAttr[oneDouble]

  object manyDouble extends manyDouble
  trait manyDouble extends number[manyDouble] with scalarAttr[manyDouble]

  object mapDouble extends mapDouble
  trait mapDouble extends number[mapDouble] with scalarAttr[mapDouble]

  // Boolean
  object oneBoolean extends oneBoolean
  trait oneBoolean extends scalarAttr[oneBoolean]

  object manyBoolean extends manyBoolean
  trait manyBoolean extends scalarAttr[manyBoolean]

  object mapBoolean extends mapBoolean
  trait mapBoolean extends scalarAttr[mapBoolean]

  // Date
  object oneDate extends oneDate
  trait oneDate extends scalarAttr[oneDate]

  object manyDate extends manyDate
  trait manyDate extends scalarAttr[manyDate]

  object mapDate extends mapDate
  trait mapDate extends scalarAttr[mapDate]

  // UUID
  object oneUUID extends oneUUID
  trait oneUUID extends scalarAttr[oneUUID]

  object manyUUID extends manyUUID
  trait manyUUID extends scalarAttr[manyUUID]

  object mapUUID extends mapUUID
  trait mapUUID extends scalarAttr[mapUUID]

  // URI
  object oneURI extends oneURI
  trait oneURI extends scalarAttr[oneURI]

  object manyURI extends manyURI
  trait manyURI extends scalarAttr[manyURI]

  object mapURI extends mapURI
  trait mapURI extends scalarAttr[mapURI]

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
    def doc(s: String) = this
    lazy val subComponent = this
  }

  object many {
    def apply[NS] = this
    def doc(s: String) = this
    lazy val subComponents = this
  }
}

