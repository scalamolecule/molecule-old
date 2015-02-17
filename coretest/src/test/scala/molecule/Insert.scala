package molecule

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}
import shapeless.HNil

class Insert extends CoreSpec {


//  class InsertSetup extends CoreSetup {
//    Ns.str.int insert List(
//      ("a", 1),
//      ("b", 2),
//      (null, 3),
//      ("d", null)
//    )
//
//  }


  "Populate molecule" in new CoreSetup {

//    Ns.str insert List(null)

    Ns.str.insert.apply("a")
    Ns.str.insert.apply(List("a"))
    Ns.str.insert.apply("a" :: HNil)
    Ns.str.insert.apply(List("a" :: HNil))

    Ns.str.int.insert.apply(List(("b", 1)))
//    Ns.str.int.insert.apply(List(("b", null.asInstanceOf[Int])))

//    Ns.str.int.insert.apply(null, 1)
//    Ns.str.int.insert.apply(List((null, 1)))
//    Ns.str.int.insert.apply(null :: 1 :: HNil)
//    Ns.str.int.insert.apply(List(null :: 1 :: HNil))
//
//
//    Ns.str.int insert List(
////      ("a", 1),
////      ("b", 2),
//      (null, 3)
////      ("d", null.asInstanceOf[Int])
//    )
//
//    Ns.str.int.get === List()

  }


//  "Populate molecule" in new InsertSetup {
//
//    Ns.str("str1").add
//
//  }
//
//  "Input molecule" in new InsertSetup {
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
//  "Input molecule" in new InsertSetup {
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

}