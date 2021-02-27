package molecule

import java.util.Date
import molecule.core.api.Molecule_1
import molecule.core.ast.elements._
import molecule.core.composition.Tx
import molecule.core.dsl.base.{Init, NS_0_01}
import molecule.core.dsl.{attributes, base}
import molecule.core.macros.ObjBuilder
import molecule.core.util.Helpers
import molecule.core.util.testing.TxCount.schema.TxCountSchema
import molecule.datomic.api.in3_out10._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.Model2Query
import molecule.datomic.peer.facade.Datomic_Peer
//import molecule.tests.core.bidirectionals.dsl.Bidirectional._
import molecule.tests.core.ref.dsl.SelfJoin._
import scala.reflect.runtime.universe._
//import molecule.tests.core.base.dsl.CoreTest._
import molecule.tests.core.base.schema.CoreTestSchema
//import molecule.tests.examples.gremlin.gettingStarted.dsl.ModernGraph2.Person
import org.specs2.mutable.Specification
import scala.language.experimental.macros


class ObjRef extends molecule.setup.TestSpec with Helpers {
  //class AdHocTest extends Specification {

  "ref/backref" in new CoreSetup {

    import molecule.tests.core.base.dsl.CoreTest._

//    implicit val conn: Conn = Datomic_Peer.recreateDbFrom(CoreTestSchema)

    //        Ns.int(0).str("a").Ref1.int1(1).str1("b").Ref2.int2(2).str2("c").save


//    Ns.int.Ref1.int1.getObj
//
//    Ns.int.Ref1.int1._Ns.str.getObj
//
//    Ns.int.Ref1.int1._Ns.Refs1.str1.getObj
//
//    Ns.int.Ref1.int1.Ref2.str2._Ref1.str1.getObj
//
//    Ns.int.Ref1.Ref2.str2._Ref1.str1.getObj
//
//    Ns.int.Ref1.int1.Ref2.str2._Ref1.Refs2.int2.getObj
//
//    Ns.int.Ref1.Ref2.str2._Ref1.Refs2.int2.getObj
//
//    Ns.int.Ref1.int1.Ref2.str2._Ref1.enum1._Ns.str.getObj
//
//    Ns.int.Ref1.int1.Ref2.str2._Ref1._Ns.str.getObj
//
//    Ns.int.Ref1.Ref2.str2._Ref1._Ns.str.getObj
//
//    Ns.int.Ref1.int1.Ref2.str2._Ref1.enum1._Ns.Refs1.str1.getObj
//
//    Ns.int.Ref1.int1.Ref2.str2._Ref1._Ns.Refs1.str1.getObj
//
//    Ns.int.Ref1.Ref2.str2._Ref1._Ns.Refs1.str1.getObj
//
//
//    // Only add ref if there are any props to ref
//    // (we can't be selective in the molecule type buildup, so the "empty refs" have to remain)
//    val o = Ns.int.Ref1.int1_.getObj
//    o.int
//    o.Ref1 // (no ref attributes though)

    //    Ns.int.strMapK("en").getObj
//
//        m(Ns.int.strMapK("en").apply(?))
//
//        //        Ns.int(0).str("a").Ref1.int1(1).str1("b").Ref2.int2(2).str2("c").save
//
//
//            val o = Person.name("Ann").Buddies.e(42L).getObj
//
//
//            Ns.int.strMapK("en").getObj
//
//            m(Ns.int.strMapK("en").apply(?))
//
//            Ns.int.int.get.head === (1, 1)
//
//
//            Ns.date.getObj
//            Ns.uuid.getObj
//            Ns.uri.getObj



    //        val o = m(Ns.int.str + Ref1.int1.str1 + Ref2.int2.str2.Ref3.int3).getObj
    //        m(Ns.int.str + Ref1.int1.str1 + Ref2.int2.str2).getObj
    //        m(Ns.int.str + Ref1.int1.str1).getObj
    //        m(Ns.int.str + Ref1.int1).getObj
    //        m(Ns.int.str + Ref1.int1_).getObj
    //
//
//    // Aggregate changes type - using dynamic prop name
//    Ns.str.insert("a", "b", "c")
//    val o = Ns.str.apply(min(2)).getObj
//    o.str_mins === List("a", "b")
//    //o.str === 6 // throws exception
//    //    o.xx === 7 // throws exception - only str_mins allowed in this case
//
//    // Successive self-join refs
//
//    // Beverages liked by all 3 different people
//    val o: base.Init
//      with Person_name
//      with Person__Likes[base.Init with Score_beverage]
//      with Person_age
//      with Person_[
//      base.Init
//        with Person_name
//        with Person__Likes[base.Init]
//        with Person_[
//        base.Init
//          with Person_name
//          with Person__Likes[base.Init]]] = Person.name("Joe").Likes.beverage._Person.age.Self
//      .name("Ben").Likes.beverage_(unify)._Person.Self
//      .name("Liz").Likes.beverage_(unify).getObj
//
//    o.name
//    o.age
//    o.Likes.beverage
//    o.Person.name
//    o.Person.Person.name

    //    val o = Ns.int.Tx.apply(Ref1.int1).getObj
    //
    //    o.int
    //    o.Tx.Ref1.int1

    //    val a: Molecule_0.Molecule_0_02[base.Init with Ns_int with Tx2_[Ref1_[base.Init with Ref1_int1]], Int, Int] = m(Ns.int.Tx2.apply(Ref1.int1))
    //    val a: Molecule_0.Molecule_0_02[base.Init with Ns_int with Tx_[Ref1_[base.Init with Ref1_int1]], Int, Int] = m(Ns.int.Tx.apply(Ref1.int1))
    //
    //    val b: DynamicProp with base.Init with Ns_int with Ns_Ref1_[base.Init with Ref1_str1] = Ns.int.Ref1.str1.getObj
    //
    //    val o: DynamicProp with base.Init with Ref1_str1 with Ref1_int1 with (
    //      (
    //        Ref1.str1[Ref1_0_1_L0[Ref1_, base.Init with Ref1_str1, String], Ref1_1_1_L0[Ref1_, base.Init with Ref1_str1, String, String]] with Ref1_0_1_L0[Ref1_, base.Init with Ref1_str1, String]
    //        )#int1[Ref1_0_2_L0[Ref1_, base.Init with Ref1_str1 with Ref1_int1, String, Int], Ref1_1_2_L0[Ref1_, base.Init with Ref1_str1 with Ref1_int1, Int, String, Int]]
    //        with Ref1_0_2_L0[Ref1_, base.Init with Ref1_str1 with Ref1_int1, String, Int]
    //      )#Tx_[Ns_[base.Init]] = m(Ref1.str1.int1.Tx(Ns.str_("Tx meta data"))).getObj

    //    val o = m(Ns.str_("Tx meta data") + Ref2.int2.str2).getObj
    //    val o: DynamicProp with base.Init with Ref1_str1 with Ref1_int1 with Tx_[Ns_[base.Init] with Ref2_[base.Init with Ref2_int2 with Ref2_str2]] = m(Ref1.str1.int1.Tx.apply(Ns.str_("Tx meta data") + Ref2.int2.str2)).getObj
//    val o = m(Ref1.str1.int1.Tx.apply(Ns.str_("Tx meta data") + Ref2.int2.str2)).getObj
//
//    o.int1
//    o.str1
//    o.Tx.Ns
//    o.Tx.Ref2.int2
//    o.Tx.Ref2.str2




//    m(Ns.int ++ Ns.float.str).insert(1, (2, "a"))
//    val o = m(Ns.int ++ Ns.float.str).getObj
//
//    o.Ns.int === 1
//    o.Ns.float === 2
//    o.Ns.str === "a"
//


//    m(Ns.int + Ns.float.str + Ref1.int1.str1).insert.apply(1, (2f, "a"), (3, "b"))
//
//    val o = m(Ns.int ++ Ns.float.str + Ref1.int1.str1).getObj
//    o.Ns.int === 1
//    o.Ns.float === 2f
//    o.Ns.str === "a"
//    o.Ref1.int1 === 3
//    o.Ref1.str1 === "b"
//
//    // Multiple same-name namespace composites need ++ to allow access to object interface
//    // Can't access object properties from same-name namespace composites
//    (m(Ns.int + Ns.float.str + Ref1.int1.str1).getObj must throwA[molecule.core.exceptions.MoleculeException])
//      .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
//      s"Please compose multiple same-name namespaces with `++` instead of `+` to access object properties."
//
//    // Multiple same-name namespace composites behaves equally for tuple output
//    m(Ns.int + Ns.float.str + Ref1.int1.str1).get.head === (1, (2f, "a"), (3, "b"))
//    m(Ns.int ++ Ns.float.str + Ref1.int1.str1).get.head === (1, (2f, "a"), (3, "b"))


    //    m(Ns.int.Ref1.int1)
    //    m(Ns.int.Ref1.int1.str1 + Ref2.int2)
    //    m(Ns.int.Ref1.int1.str1 + Ref1.int1)
    //    m(Ns.int.Ref1.int1.str1 + Ns.str)
    //    m(Ns.int.Ref1.int1.str1 ++ Ns.str)



//    m(Ns.int.Refs1 * Ref1.int1.str1) insert List(
//      (1, List((1, "a"), (2, "b")))
//    )
//
//    m(Ns.int.Refs1 * Ref1.int1.str1).get === List(
//      (1, List((1, "a"), (2, "b")))
//    )
//
//    val o = m(Ns.int.Refs1 * Ref1.int1.str1).getObj
//    o.int === 1
//    o.Refs1.head.int1 === 1
//    o.Refs1.head.str1 === "a"
//    o.Refs1.last.int1 === 2
//    o.Refs1.last.str1 === "b"




//    m(Ns.str.Refs1 * Ref1.int1$.Ref2.int2$.str2) insert List(
//      ("a", List((Some(11), Some(12), "aa"))),
//      ("b", List((Some(13), None, "bb"))),
//      ("c", List((None, Some(14), "cc"))),
//      ("d", List())
//    )
//
//    m(Ns.str.Refs1 *? Ref1.int1$.Ref2.int2$.str2).get
//      .sortBy(_._1) === List(
//      ("a", List((Some(11), Some(12), "aa"))),
//      ("b", List((Some(13), None, "bb"))),
//      ("c", List((None, Some(14), "cc"))),
//      ("d", List())
//    )
//
//    {
//      val oo = m(Ns.str.Refs1 *? Ref1.int1$.Ref2.int2$.str2).getObjList.sortBy(_.str)
//      oo.head.str === "a"
//      oo.head.Refs1.head.int1$ === Some(11)
//      oo.head.Refs1.head.Ref2.int2$ === Some(12)
//      oo.head.Refs1.head.Ref2.str2 === "aa"
//    }



//    m(Ns.apply(42L).str.Refs1.*(Ref1.int1))
//    m(Ns.apply(42L).Refs1.*(Ref1.int1))
//    m(Ns.str.Refs1.*(Ref1.Refs2.*(Ref2.int2)))

    // Successive nested not allowed (won't compile)
    //        m(Ns.float.Refs1.*(Ref1.int1.str1).str.RefsSub1.*(Ref1.ref2))



    // Composites + composite tx meta data
    //    m(Ref1.int1.+(Ref2.int2.Tx(Ref3.int3.+(Ref4.int4)))).save






















    //    val o = Ns.int.Tx.apply(Ref1.int1).getObj
    //
    //    o.int
    //    o.Tx.Ref1.int1

    //    val a: Molecule_0.Molecule_0_02[base.Init with Ns_int with Tx2_[Ref1_[base.Init with Ref1_int1]], Int, Int] = m(Ns.int.Tx2.apply(Ref1.int1))
    //    val a: Molecule_0.Molecule_0_02[base.Init with Ns_int with Tx_[Ref1_[base.Init with Ref1_int1]], Int, Int] = m(Ns.int.Tx.apply(Ref1.int1))
    //
    //    val b: DynamicProp with base.Init with Ns_int with Ns_Ref1_[base.Init with Ref1_str1] = Ns.int.Ref1.str1.getObj
    //
    //    val o: DynamicProp with base.Init with Ref1_str1 with Ref1_int1 with (
    //      (
    //        Ref1.str1[Ref1_0_1_L0[Ref1_, base.Init with Ref1_str1, String], Ref1_1_1_L0[Ref1_, base.Init with Ref1_str1, String, String]] with Ref1_0_1_L0[Ref1_, base.Init with Ref1_str1, String]
    //        )#int1[Ref1_0_2_L0[Ref1_, base.Init with Ref1_str1 with Ref1_int1, String, Int], Ref1_1_2_L0[Ref1_, base.Init with Ref1_str1 with Ref1_int1, Int, String, Int]]
    //        with Ref1_0_2_L0[Ref1_, base.Init with Ref1_str1 with Ref1_int1, String, Int]
    //      )#Tx_[Ns_[base.Init]] = m(Ref1.str1.int1.Tx(Ns.str_("Tx meta data"))).getObj

    //    val o = m(Ns.str_("Tx meta data") + Ref2.int2.str2).getObj
    //    val o: DynamicProp with base.Init with Ref1_str1 with Ref1_int1 with Tx_[Ns_[base.Init] with Ref2_[base.Init with Ref2_int2 with Ref2_str2]] = m(Ref1.str1.int1.Tx.apply(Ns.str_("Tx meta data") + Ref2.int2.str2)).getObj
//    val o = m(Ref1.str1.int1.Tx.apply(Ns.str_("Tx meta data") + Ref2.int2.str2)).getObj
//
//    o.int1
//    o.str1
//    o.Tx.Ns
//    o.Tx.Ref2.int2
//    o.Tx.Ref2.str2
    //
    //
    //
    //
    //
    //    val o = m(Ns.int.str.Ref1.int1).getObj
    //
    //    o.int === 0
    //    o.str === "x"
    //    o.Ref1.int1 === 1
    //
    //    val o1 = m(Ns.int).getObj
    //    o1.int === 0
    //    o1.str === "x"


    ////    val o1 = m(Ns.int.str).getObj
    ////    val o1 = m(Ns.int).getObj
    //
    //    val o2 = new Ns_int with Ns_str with Ns_Ref1_[Ref1_int1] {
    //      final override lazy val int = 0
    //      final override lazy val str = "x"
    //      final override def Ref1: Ref1_int1 = new Ref1_int1 {
    //        final override lazy val int1: Int = 1
    //      }
    //    }
    //
    //    val o3 = new Ns_int with Ns_str {
    //      final override lazy val int = 0
    //      final override lazy val str = "x"
    //    }
    //
    //    trait Ns_str        { lazy val str       : String                  = ??? }
    //    trait Ns_int        { lazy val int       : Int                     = ??? }
    //
    //
    //    showRaw(reify(new Ns_int with Ns_str {
    //      final override lazy val int = 0
    //      final override lazy val str = "x"
    //    }))

    //    import scala.reflect.runtime.universe._
    //    trait A { val a = 7 }
    //    trait B
    //    val AB = tq"A with B"
    //    val ab = q"new $AB"
    //
    //
    //    def build[T]: T = macro ObjBuilder.obj[T]
    //
    ////    type tt = A with B
    ////    trait X extends A with B
    //
    ////    build[A with B]
    //    val o  = build[A with B]


    //    build[A]


    //    RefinedType(
    //      List(
    //        RefinedType(
    //          List(
    //            RefinedType(
    //              List(
    //                TypeRef(SingleType(SingleType(SingleType(SingleType(ThisType("<root>"), molecule), molecule.core), molecule.core.dsl), molecule.core.dsl.base), molecule.core.dsl.base.Init, List()),
    //                TypeRef(ThisType(molecule.tests.core.base.dsl.CoreTest), molecule.tests.core.base.dsl.CoreTest.Ns_int, List())
    //              ),
    //              Scope()
    //            ),
    //            TypeRef(ThisType(molecule.tests.core.base.dsl.CoreTest), molecule.tests.core.base.dsl.CoreTest.Ns_str, List())),
    //          Scope()
    //        ),
    //        TypeRef(
    //          ThisType(molecule.tests.core.base.dsl.CoreTest), molecule.tests.core.base.dsl.CoreTest.Ns_Ref1_,
    //          List(
    //            RefinedType(
    //              List(
    //                TypeRef(
    //                  SingleType(SingleType(SingleType(SingleType(ThisType("<root>"), molecule), molecule.core), molecule.core.dsl), molecule.core.dsl.base), molecule.core.dsl.base.Init, List()),
    //                TypeRef(ThisType(molecule.tests.core.base.dsl.CoreTest), molecule.tests.core.base.dsl.CoreTest.Ref1_int1, List())),
    //              Scope()
    //            )
    //          )
    //        )
    //      ),
    //      Scope()
    //    )

    //    class obj() extends Nothing with molecule.tests.core.base.dsl.CoreTest.Ns_int with molecule.tests.core.base.dsl.CoreTest.Ns_str with molecule.tests.core.base.dsl.CoreTest.Ns_Ref1_[Nothing with molecule.tests.core.base.dsl.CoreTest.Ref1_int1] {
    //      override val int: Int = castOneInt(row, 0)
    //      override val str: String = castOne[String](row, 1)
    //    }
    //
    //    class obj()
    //
    //    val x = new obj()

    //    class obj extends Nothing with molecule.tests.core.base.dsl.CoreTest.Ns_int {
    //      final override lazy val int: Int = 0
    //    }
    //    val x = new obj()
    //
    //    x.int === 0


    //    o2.int === 0
    //    o2.str === "x"
    //    o2.Ref1.int1 === 1


    //    {
    //      val o = m(Ns.float.Refs1.*(Ref1.int1).RefsSub1.str1).getObjList.head
    //      o.float
    //      o.Refs1.head.int1
    //      o.RefsSub1.str1
    //    }
    //
    //        val o = m(Ns.float.Ref1.int1.Refs2.*(Ref2.int2)).getObjList.head
    //        o.float
    //        o.Ref1.int1
    //        o.Ref1.Refs2.head.int2
    //
    //    Ns.str.Ref1.str1._Ns.Refs1.int1.inspectInsert(
    //      List(
    //        ("a", "a1", 1),
    //        ("b", "b1", 2)
    //      )
    //    )

    ok
  }
  //
  //
  //  "A first query" in new SeattleSetup {
  //    import molecule.tests.examples.datomic.seattle.dsl.Seattle._
  //
  //    // A Community-name molecule
  //    val communities = m(Community. e.name_)
  //
  //    // We have 150 communities
  //    communities.get.size === 150
  //  }

  //  "adhoc" in new BidirectionalSetup {
  //import molecule.tests.core.bidirectionals.dsl.Bidirectional._
  //
  //  }

  //  "adhoc" in new PartitionSetup {

  //  }


  //  "example adhoc" in new SeattleSetup {
  //
  //
  //    //    ok
  //  }


  //  "example adhoc" in new SocialNewsSetup {
  //
  //
  //
  //    ok
  //  }
}
//