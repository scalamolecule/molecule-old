//package molecule
//package semantics.tuple
//import molecule.dsl.coreTest._
//
//
//class Expressions extends CoreSpec {
//
//  class ExpressionSetup extends CoreSetup {
//    Ns.str.int insert List(("John", 37), ("Lisa", 28), ("Ben", 28))
//  }
//
//  "Applying a value" in new ExpressionSetup {
//
//    Ns.str_("John").int.get === List(37)
//    Ns.str("John").int.get === List(("John", 37))
//
//    Ns.str.int_(28).get === List("Ben", "Lisa")
//    Ns.str.int(28).get === List(("Ben", 28), ("Lisa", 28))
//  }
//
//  //  "Applying a value (parameterized)" in new ExpressionSetup {
//  //    val ageOf = m(Ns.str_(?).int)
//  //    ageOf("John").one === 37
//  //
//  //    val personByName = m(Ns.str(?).int)
//  //    personByName("John").one ===("John", 37)
//  //
//  //    val nameOfAge = m(Ns.str.int_(?))
//  //    nameOfAge(37).get === List("John")
//  //    nameOfAge(28).get === List("Lisa", "Ben")
//  //    nameOfAge(10).get === List()
//  //
//  //    val personOfAge = m(Ns.str.int(?))
//  //    personOfAge(37).get === List(("John", 37))
//  //    personOfAge(28).get === List(("Lisa", 28), ("Ben", 28))
//  //    personOfAge(10).get === List()
//  //  }
//  //
//  //  "Applying a Set of values" in new ExpressionSetup {
//  //
//  //  }
//  //
//  //  "Applying a value" in new ExpressionSetup {
//  //    Ns.str("Lisa" or "Ben").int.get === List(("Lisa", 28), ("Ben", 28))
//  //    Ns.str_("Lisa" or "Ben").int.get === List(28, 28)
//  //
//  //    Ns.str.int(28 or 37).get === List(("John", 37), ("Lisa", 28), ("Ben", 28))
//  //    Ns.str.int_(28 or 37).get === List("John", "Lisa", "Ben")
//  //  }
//  //
//  ////  "Fulltext search" in new ExpressionSetup {}
//  //
//  //  "Compare String" in new ExpressionSetup {
//  //    //
//  //    Ns.str.<("C").one === "Ben"
//  //    // same as
//  //    m(Ns.str < "C").one === "Ben"
//  //
//  //    Ns.str.<("C").one === "Ben"
//  //  }
//
//  //  "OR expressions" in new ExpressionSetup {}
//
//  //  "AND expressions" in new ExpressionSetup {}
//}