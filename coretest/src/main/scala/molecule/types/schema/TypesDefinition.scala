package molecule.types.schema
import molecule.dsl.schemaDefinition._


@InOut(0, 22)
trait OneType {
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
  //  val ref    = oneRef[NS2]
}

