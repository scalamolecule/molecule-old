//package molecule.semantics.tuple
//
//import molecule.dsl.coreTest._
//import molecule.{CoreSetup, CoreSpec}
//
//class b_Insert extends CoreSpec {
//
//  "Populate molecule" in new CoreSetup {
//
//    Ns.str("str1").add
//
//  }
//
//  "Input molecule" in new CoreSetup {
//
//    // 2 steps
//    val insertString = Ns.str.insert
//    insertString("str1")
//
//    // With variable
//    insertString(str1)
//
//  }
//
//  "Input molecule" in new CoreSetup {
//
//    // Insert single value for one cardinality-1 attribute
//
//    Ns.str insert "str1"
//    Ns.str.insert("str1")
//
//    // card 2
//    Ns.ints insert Set(1, 2)
//
//    // Use variable a value
//    val foo = "foo"
//    Ns.str insert foo
//    Ns.str.insert(foo)
//
//
//    // 2 steps
//    val insertString = Ns.str.insert
//    insertString("str1")
//
//    // With variable
//    insertString(foo)
//
//
//
//    // Insert several values
//
//    Ns.str.get === List()
//  }
//
//}