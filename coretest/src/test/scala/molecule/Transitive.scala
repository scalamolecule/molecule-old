package molecule
import molecule.util.CoreSpec
import molecule.util.dsl.coreTest._
import molecule.util.schema.CoreTestSchema

class Transitive extends CoreSpec {

  implicit val conn = load(CoreTestSchema)


  val List(r1, r2, r3, r4) = Ref1.int1 insert List(1, 2, 3, 4) eids

  Ns.int.refs1.refsSub1 insert List(
    (1, Set(r1, r2, r3), Set(r2, r3, r4)),
    (2, Set(r1), Set(r3, r4))
  )

//  "No Bonds" >> {
//    Ref1.int1(2).int1.debug === 7
//
//    Ref1.str1.int1.int1.debug === 7
//    Ref1.int1.str1.int1.debug === 7
//    Ref1.int1.int1.str1.debug === 7
//  }
//
//  "One Bond" >> {
//    Ns.Refs1.int1.int1.debug === 7
//
//    Ns.Refs1.str1.int1.int1.debug === 7
//    Ns.Refs1.int1.str1.int1.debug === 7
//    Ns.Refs1.int1.int1.str1.debug === 7
//  }
//
//  "Two Bond" >> {
//    Ns.Refs1.str1.int1._Ns.Refs1.int1.debug === 7
//    Ns.Refs1.int1.str1._Ns.Refs1.int1.debug === 7
//    Ns.Refs1.int1._Ns.Refs1.str1.int1.debug === 7
//    Ns.Refs1.int1._Ns.Refs1.int1.str1.debug === 7
//
//    Ns.Refs1.str1.int1._Ns.Refs1.int1.debug === 7
//    Ns.Refs1.int1.str1._Ns.Refs1.int1.debug === 7
//    Ns.Refs1.int1._Ns.Refs1.str1.int1.debug === 7
//    Ns.Refs1.int1._Ns.Refs1.int1.str1.debug === 7
//  }
}