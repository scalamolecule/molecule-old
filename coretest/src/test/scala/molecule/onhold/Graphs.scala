//package molecule
//package semantics.tuple
//
//import molecule.dsl.coreTest._
//import molecule.{CoreSetup, CoreSpec}
//
//
//class Graphs extends CoreSpec {
//
//  "Applying a value" in new CoreSetup {
//
//    Ns.str.int insert List(("John", 37), ("Lisa", 28), ("Ben", 28))
//
//    val personWithAge = m(Ns.str.int(?))
//
//    personWithAge(37).get === List("John")
//    personWithAge(28).get === List("Lisa", "Ben")
//    personWithAge(10).get === List()
//  }
//
//
//}