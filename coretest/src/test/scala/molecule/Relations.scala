package molecule
import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}

import scala.collection.generic.SeqFactory


class Relations extends CoreSpec {


  class RelSetup extends CoreSetup {
    Ns.str.Ref1.str1.Ref2.str2 insert List(
      ("a0", "a1", "a2"),
      ("b0", "b1", "b2"),
      ("c0", "c1", "c2"),
      // null values are simply not asserted (inserted)
      //      (null, "d1", "d2"),
      ("e0", null, "e2"),
      ("f0", "f1", null))

  }


  "One-to-One" in new CoreSetup {

    val List(id1, ref1, id2, ref2, id3, ref3) = Ns.str.Ref1.str1 insert List(
      ("a0", "a1"),
      ("b0", "b1"),
      ("c0", "c1")) ids

    // Get attribute values from 2 namespaces
    // Namespace references like `Ref1` starts with Capital letter
    Ns.str.Ref1.str1.get === List(
      ("a0", "a1"),
      ("b0", "b1"),
      ("c0", "c1"))

    // We can also retrieve the referenced entity id
    // Referenced entity id `ref1` starts with lower case letter
    Ns.str.ref1.get === List(
      ("a0", ref1),
      ("b0", ref2),
      ("c0", ref3))
  }


  "Referenced entity ids" in new CoreSetup {

    val List(orderLine1, orderLine2, orderLine3) = Ref1.str1 insert List("item1", "item2", "item3") ids

    // We can insert ref entity ids
    Ns.str.ref1 insert List(
      ("a0", orderLine1),
      ("b0", orderLine2),
      ("c0", orderLine3))

    // Get attribute values from 2 namespaces
    // Namespace references like `Ref1` starts with Capital letter
    Ns.str.Ref1.str1.get === List(
      ("b0", "item2"),
      ("a0", "item1"),
      ("c0", "item3"))

    // We can also retrieve the referenced entity id
    // Referenced entity id `ref1` starts with lower case letter
    Ns.str.ref1.get === List(
      ("a0", orderLine1),
      ("b0", orderLine2),
      ("c0", orderLine3))
  }
  //
  //
  //  "Implicit namespaces" in new RelSetup {
  //
  //
  //    // Get attribute values from all 3 namespaces
  //    Ns.str.Ref1.str.Ref2.str.get === List(
  //      ("b0", "b1", "b2"),
  //      ("c0", "c1", "c2"),
  //      ("a0", "a1", "a2")
  //    )
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