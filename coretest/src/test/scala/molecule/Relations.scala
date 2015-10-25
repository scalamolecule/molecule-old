package molecule
import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}

import scala.collection.generic.SeqFactory


class Relations extends CoreSpec {

  "One-to-One" in new CoreSetup {

    val List(id1, ref1, id2, ref2, id3, ref3) = Ns.str.Ref1.str1 insert List(
      ("a0", "a1"),
      ("b0", "b1"),
      ("c0", "c1")) eids

    // Get attribute values from 2 namespaces
    // Namespace references like `Ref1` starts with Capital letter
    Ns.str.Ref1.str1.get === List(
      ("c0", "c1"),
      ("a0", "a1"),
      ("b0", "b1"))

    // We can also retrieve the referenced entity id
    // Referenced entity id `ref1` starts with lower case letter
    Ns.str.ref1.get === List(
      ("a0", ref1),
      ("b0", ref2),
      ("c0", ref3))
  }


  "Referenced entity ids" in new CoreSetup {

    val List(orderLine1, orderLine2, orderLine3) = Ref1.str1 insert List("item1", "item2", "item3") eids

    // We can insert ref entity ids
    Ns.str.ref1 insert List(
      ("a0", orderLine1),
      ("b0", orderLine2),
      ("c0", orderLine3))

    // Get attribute values from 2 namespaces
    // Namespace references like `Ref1` starts with Capital letter
    Ns.str.Ref1.str1.get === List(
      ("c0", "item3"),
      ("b0", "item2"),
      ("a0", "item1"))

    // We can also retrieve the referenced entity id
    // Referenced entity id `ref1` starts with lower case letter
    Ns.str.ref1.get === List(
      ("a0", orderLine1),
      ("b0", orderLine2),
      ("c0", orderLine3))
  }

  "Referenced entity ids" in new CoreSetup {
    val id     = Ns.str("a").add.eid
    val refId1 = Ns(id).Refs1.int1(1).add.eid
    val refId2 = Ref1(refId1).Ref2.int2(2).add.eid

    Ns(id).Refs1.e.Ref2.int2_(2).one === refId1
  }


  "Enum" in new CoreSetup {
    Ns.str.enum insert List(("a", "enum0"))

    Ns.str.enum.get === List(("a", "enum0"))
  }

  "Ref enum after ref" in new CoreSetup {
    Ns.str.Ref1.enum1 insert List(("b", "enum10"))
    Ns.str.Ref1.enum1.get === List(("b", "enum10"))
  }

  "Ref enum after attr" in new CoreSetup {
    Ns.str.Ref1.int1.enum1 insert List(("c", 11, "enum11"))
    Ns.str.Ref1.int1.enum1.get === List(("c", 11, "enum11"))
  }

  "Nested enum after ref" in new CoreSetup {
    m(Ns.str.Refs1 * Ref1.enum1) insert List(("d", List("enum11")))
    m(Ns.str.Refs1 * Ref1.enum1).get === List(("d", List("enum11")))
  }

  "Nested enum after attr" in new CoreSetup {
    m(Ns.str.Refs1 * Ref1.int1.enum1) insert List(("e", List((12, "enum12"))))
    m(Ns.str.Refs1 * Ref1.int1.enum1).get === List(("e", List((12, "enum12"))))
  }
}