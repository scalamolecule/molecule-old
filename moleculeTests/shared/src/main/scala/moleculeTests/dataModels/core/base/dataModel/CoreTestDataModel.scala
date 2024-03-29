package moleculeTests.dataModels.core.base.dataModel

import molecule.core.data.model._

@InOut(3, 22)
object CoreTestDataModel {

  trait Ns {
    val str     = oneString.fulltext.doc("Card one String attribute")
    val int     = oneInt.doc("Card one Int attribute")
    val long    = oneLong
    val double  = oneDouble
    val bool    = oneBoolean
    val date    = oneDate
    val uuid    = oneUUID
    val uri     = oneURI
    val bigInt  = oneBigInt
    val bigDec  = oneBigDecimal
    val enumm   = oneEnum("enum0", "enum1", "enum2", "enum3", "enum4", "enum5", "enum6", "enum7", "enum8", "enum9")
    val parent  = one[Ns]
    val ref1    = one[Ref1]
    val refSub1 = one[Ref1].isComponent

    val strs     = manyString.fulltext
    val ints     = manyInt
    val longs    = manyLong
    val doubles  = manyDouble
    val bools    = manyBoolean
    val dates    = manyDate
    val uuids    = manyUUID
    val uris     = manyURI
    val bigInts  = manyBigInt
    val bigDecs  = manyBigDecimal
    val enums    = manyEnum("enum0", "enum1", "enum2", "enum3", "enum4", "enum5", "enum6", "enum7", "enum8", "enum9")
    val parents  = many[Ns]
    val refs1    = many[Ref1]
    val refsSub1 = many[Ref1].isComponent

    val strMap    = mapString.fulltext
    val intMap    = mapInt
    val longMap   = mapLong
    val doubleMap = mapDouble
    val boolMap   = mapBoolean
    val dateMap   = mapDate
    val uuidMap   = mapUUID
    val uriMap    = mapURI
    val bigIntMap = mapBigInt
    val bigDecMap = mapBigDecimal
  }

  trait Ref1 {
    val str1    = oneString.fulltext
    val int1    = oneInt
    val enum1   = oneEnum("enum10", "enum11", "enum12")
    val ref2    = one[Ref2]
    val refSub2 = one[Ref2].isComponent

    val strs1    = manyString
    val ints1    = manyInt
    val enums1   = manyEnum("enum10", "enum11", "enum12")
    val refs2    = many[Ref2]
    val refsSub2 = many[Ref2].isComponent

    val nss = many[Ns]
    val intMap1 = mapInt
  }

  trait Ref2 {
    val str2  = oneString.uniqueIdentity.fulltext
    val int2  = oneInt.uniqueValue
    val enum2 = oneEnum("enum20", "enum21", "enum22")
    val strs2 = manyString
    val ints2 = manyInt.noHistory
    val ref3  = one[Ref3]
    val refs3 = many[Ref3]
  }

  trait Ref3 {
    val str3  = oneString
    val int3  = oneInt
    val ref4  = one[Ref4]
    val refs4 = many[Ref4]
  }

  trait Ref4 {
    val str4  = oneString
    val int4  = oneInt
  }
}