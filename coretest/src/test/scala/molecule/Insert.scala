package molecule

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}
import shapeless.HNil

class Insert extends CoreSpec {


  "1 attribute" in new CoreSetup {

    // The `insert` method performs the compile time analysis of the molecule
    // The `apply` method inserts the type-inferred data at runtime
    Ns.str.insert.apply("a")

    // We can enter data for one attribute in 4 different ways:

    // 1. Comma-separated list
    Ns.str insert "b"
    Ns.str.insert("c", "d")

    // 2. List of values
    Ns.str insert List("e")
    Ns.str insert List("f", "g")

    // 3. Arity-1 HList
    Ns.str.insert("h" :: HNil)

    // 4. List of Arity-1 HLists
    Ns.str insert List("i" :: HNil)
    Ns.str insert List("j" :: HNil, "k" :: HNil)

    // All values inserted
    Ns.str.get.sorted === List("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k")


    Ns.int.insert(1)
    Ns.int.insert(2, 3)
    Ns.int.insert(List(4))
    Ns.int.insert(List(5, 6))
    Ns.int.insert(7 :: HNil)
    Ns.int.insert(List(8 :: HNil))
    Ns.int.insert(List(9 :: HNil, 10 :: HNil))
    Ns.int.get.sorted === List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)


    Ns.long.insert(1L)
    Ns.long.insert(2L, 3L)
    Ns.long.insert(List(4L))
    Ns.long.insert(List(5L, 6L))
    Ns.long.insert(7L :: HNil)
    Ns.long.insert(List(8L :: HNil))
    Ns.long.insert(List(9L :: HNil, 10L :: HNil))
    Ns.long.get.sorted === List(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)


    Ns.float.insert(1.0f)
    Ns.float.insert(2.0f, 3.0f)
    Ns.float.insert(List(4.0f))
    Ns.float.insert(List(5.0f, 6.0f))
    Ns.float.insert(7.0f :: HNil)
    Ns.float.insert(List(8.0f :: HNil))
    Ns.float.insert(List(9.0f :: HNil, 10.0f :: HNil))
    Ns.float.get.sorted === List(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f)


    Ns.double.insert(1.0)
    Ns.double.insert(2.0, 3.0)
    Ns.double.insert(List(4.0))
    Ns.double.insert(List(5.0, 6.0))
    Ns.double.insert(7.0 :: HNil)
    Ns.double.insert(List(8.0 :: HNil))
    Ns.double.insert(List(9.0 :: HNil, 10.0 :: HNil))
    Ns.double.get.sorted === List(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)


    Ns.bool.insert(true)
    Ns.bool.insert(true, false)
    Ns.bool.insert(List(true))
    Ns.bool.insert(List(true, false))
    Ns.bool.insert(true :: HNil)
    Ns.bool.insert(List(true :: HNil))
    Ns.bool.insert(List(true :: HNil, false :: HNil))
    // Unique values coalesced
    Ns.bool.get.sorted === List(false, true)


    Ns.date.insert(date1)
    Ns.date.insert(date2, date3)
    Ns.date.insert(List(date4))
    Ns.date.insert(List(date3, date4))
    Ns.date.insert(date1 :: HNil)
    Ns.date.insert(List(date2 :: HNil))
    Ns.date.insert(List(date3 :: HNil, date4 :: HNil))
    // Unique values coalesced
    Ns.date.get.sorted === List(date1, date2, date3, date4)


    Ns.uuid.insert(uuid1)
    Ns.uuid.insert(uuid2, uuid3)
    Ns.uuid.insert(List(uuid4))
    Ns.uuid.insert(List(uuid3, uuid4))
    Ns.uuid.insert(uuid1 :: HNil)
    Ns.uuid.insert(List(uuid2 :: HNil))
    Ns.uuid.insert(List(uuid3 :: HNil, uuid4 :: HNil))
    // Unique values coalesced
    Ns.uuid.get.sortBy(_.toString) === List(uuid1, uuid2, uuid3, uuid4)


    //    Ns.uri.insert(uri1)
    //    Ns.uri.insert(uri2, uri3)
    //    Ns.uri.insert(List(uri4))
    //    Ns.uri.insert(List(uri3, uri4))
    //    Ns.uri.insert(uri1 :: HNil)
    //    Ns.uri.insert(List(uri2 :: HNil))
    //    Ns.uri.insert(List(uri3 :: HNil, uri4 :: HNil))
    //    // Unique values coalesced
    //    Ns.uri.get.sorted === List(uri1, uri2, uri3, uri4)


    Ns.enum.insert("enum1")
    Ns.enum.insert("enum2", "enum3")
    Ns.enum.insert(List("enum4"))
    Ns.enum.insert(List("enum3", "enum4"))
    Ns.enum.insert(enum1 :: HNil)
    Ns.enum.insert(List(enum2 :: HNil))
    Ns.enum.insert(List(enum3 :: HNil, enum4 :: HNil))
    // Unique values coalesced
    Ns.enum.get.sorted === List(enum1, enum2, enum3, enum4)
  }


  "Add molecule to insert" in new CoreSetup {

    Ns.str("a").add

    Ns.str.get === List("a")

  }


  "Insert Molecule (2-step insertion)" in new CoreSetup {

    // 1. Define Insert Molecule
    val strInsertMolecule = Ns.str("a").insert

    // 2. Apply value to Insert Molecule
    strInsertMolecule("a")

    Ns.str.get === List("a")

  }



  "Missing values / inconsistent data set" in new CoreSetup {

    Ns.str.insert.apply(null.asInstanceOf[String])
    Ns.str.get === List()

    //    Ns.str.insert.apply("a")
    //    Ns.str.insert.apply(List("a"))
    //    Ns.str.insert.apply("a" :: HNil)
    //    Ns.str.insert.apply(List("a" :: HNil))
    //
    //    Ns.str.int.insert.apply(List(("b", 1)))
    //    Ns.str.int.insert.apply(List(("b", null.asInstanceOf[Int])))
    //    Ns.str.get === List("b")
    //    Ns.int.get === List()

    //    Ns.str.int.bool.insert.apply(List(("b", null.asInstanceOf[Int], true)))
    Ns.str.int.bool.insert.apply(List((null, null.asInstanceOf[Int], true)))
    //    Ns.str.int.bool.insert.apply(List((null, 7, true)))
    Ns.str.get === List()
    Ns.int.get === List()
    Ns.bool.get === List(true)
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