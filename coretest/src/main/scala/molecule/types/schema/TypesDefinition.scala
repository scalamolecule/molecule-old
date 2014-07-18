package molecule.types.schema
import molecule.dsl.schemaDefinition._

@InOut(0, 22)
trait TypesDefinition {

  trait One {
    val str    = oneString
    val int    = oneInt
    val long   = oneLong
    val float  = oneFloat
    val double = oneDouble
    val bool   = oneBoolean
    val date   = oneDate
    val uuid   = oneUUID
    val uri    = oneURI
    val enum   = oneEnum('enum1, 'enum2)
    //  val ref    = one[NS2]
  }

  trait Many {
    val strM    = manyString
    val intM    = manyInt
    val longM   = manyLong
    val floatM  = manyFloat
    val doubleM = manyDouble
    val dateM   = manyDate
    val uuidM   = manyUUID
    val uriM    = manyURI
    val enumM   = manyEnum('enum1, 'enum2)
    //  val ref    = many[NS2]
  }
}