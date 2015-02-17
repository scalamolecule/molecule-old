package molecule
import molecule.util.dsl.coreTest.Ns
import molecule.util.{CoreSetup, CoreSpec}


class Relations extends CoreSpec {


  class RelSetup extends CoreSetup {
    Ns.str.Ref1.str.Ref2.str insert List(
      ("a0", "a1", "a2"),
      ("b0", "b1", "b2"),
      ("c0", "c1", "c2"),
      // null values are simply not asserted (inserted)
//      (null, "d1", "d2"),
      ("e0", null, "e2"),
      ("f0", "f1", null))

  }


//  "Related attributes" in new RelSetup {
//
//    // Get attribute values from 2 namespaces
//    Ns.str.Ref1.str.get === List(
//      ("b0", "b1"),
//      ("c0", "c1"),
//      ("a0", "a1"))
//
//    // Get attribute values from all 3 namespaces
//    Ns.str.Ref1.str.Ref2.str.get === List(
//      ("b0", "b1", "b2"),
//      ("c0", "c1", "c2"),
//      ("a0", "a1", "a2")
//    )
//  }
//
//
//  "Implicit namespaces" in new RelSetup {
//
//    Ns.str.Ref1.Ref2.str.get === List(
//      ("a0", "a2"),
//      ("b0", "b2"),
//      ("c0", "c2"))
//
//    Ns.Ref1.str.Ref2.str.get === List(
//      ("b0", "b1", "b2"),
//      ("c0", "c1", "c2"),
//      ("a0", "a1", "a2"))
//  }

}