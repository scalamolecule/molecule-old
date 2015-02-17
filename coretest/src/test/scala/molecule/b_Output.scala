//package molecule
//package semantics.tuple
//
//import molecule.dsl.coreTest._
//
//
//class b_Output extends CoreSpec {
//
//  "Applying a value" in new CoreSetup {
//
//    Ns.str.int insert List(("John", 37), ("Lisa", 28), ("Ben", 28))
//
//
//    // Require str and int - output str and int
//    Ns.str.int.get === List(("Ben", 28), ("John", 37), ("Lisa", 28))
//
//    // Require str and int - output str
//    Ns.str.int_.get === List("Ben", "John", "Lisa")
//
//    // Require str - output str
//    Ns.str.get === List("Ben", "John", "Lisa")
//
//
//
//
//    // Won't compile
//    // Ns.str_.int_.get === List()
//  }
//
//}