package molecule.util.schema
import molecule.util.dsl.schemaDefinition._

@InOut(3, 22)
trait CoreTestDefinition {

  trait Ns {
    val str    = oneString
    val int    = oneInt
    val long   = oneLong
    val float  = oneFloat
    val double = oneDouble
    val bool   = oneBoolean
    val date   = oneDate
    val uuid   = oneUUID
    val uri    = oneURI
    val enum   = oneEnum('enum, 'enum0, 'enum1, 'enum2, 'enumM)
    val ref    = one[Ref1]

    val strs    = manyString
    val ints    = manyInt
    val longs   = manyLong
    val floats  = manyFloat
    val doubles = manyDouble
    val dates   = manyDate
    val uuids   = manyUUID
    val uris    = manyURI
    val enums   = manyEnum('enum, 'enum0, 'enum1, 'enum2, 'enumM)
    val refs    = many[Ref1]
  }

  trait Ref1 {
    val str    = oneString
    val int    = oneInt
    val long   = oneLong
    val float  = oneFloat
    val double = oneDouble
    val bool   = oneBoolean
    val date   = oneDate
    val uuid   = oneUUID
    val uri    = oneURI
    val enum   = oneEnum('enum, 'enum2)
    val ref    = one[Ref2]

    val strs    = manyString
    val ints    = manyInt
    val longs   = manyLong
    val floats  = manyFloat
    val doubles = manyDouble
    val dates   = manyDate
    val uuids   = manyUUID
    val uris    = manyURI
    val enums   = manyEnum('enum, 'enum2)
    val refs    = many[Ref2]
  }

  trait Ref2 {
    val str    = oneString
    val int    = oneInt
    val long   = oneLong
    val float  = oneFloat
    val double = oneDouble
    val bool   = oneBoolean
    val date   = oneDate
    val uuid   = oneUUID
    val uri    = oneURI
    val enum   = oneEnum('enum, 'enum2)

    val strs    = manyString
    val ints    = manyInt
    val longs   = manyLong
    val floats  = manyFloat
    val doubles = manyDouble
    val dates   = manyDate
    val uuids   = manyUUID
    val uris    = manyURI
    val enums   = manyEnum('enum, 'enum2)
  }
}