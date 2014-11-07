package molecule
package out
import java.util.Date
import molecule.dsl.schemaDSL._
import scala.language.higherKinds


trait Molecule_0[Ns0, Ns1[_], In1_0[_], In1_1[_, _]] extends NS0 {

//  val e          : Ns1[Long   ] with OneLong   [Ns1[Long   ], In1_1[Long   , Long   ]] with Nested1[Ns1] = ???
  val e          : Ns1[Long   ] with OneLong   [Ns1[Long   ], In1_1[Long   , Long   ]] = ???
  val a          : Ns1[String ] with OneString [Ns1[String ], In1_1[String , String ]] = ???
  val v          : Ns1[Any    ] with OneAny    [Ns1[Any    ], In1_1[Any    , Any    ]] = ???
  val ns         : Ns1[String ] with OneString [Ns1[String ], In1_1[String , String ]] = ???
  val txInstant  : Ns1[Date   ] with OneDate   [Ns1[Date   ], In1_1[Date   , Date   ]] = ???
  val txT        : Ns1[Long   ] with OneLong   [Ns1[Long   ], In1_1[Long   , Long   ]] = ???
  val txAdded    : Ns1[Boolean] with OneBoolean[Ns1[Boolean], In1_1[Boolean, Boolean]] = ???

  val e_         : Ns0 with OneLong   [Ns0, In1_0[Long   ]] = ???
  val a_         : Ns0 with OneString [Ns0, In1_0[String ]] = ???
  val v_         : Ns0 with OneAny    [Ns0, In1_0[Any    ]] = ???
  val ns_        : Ns0 with OneString [Ns0, In1_0[String ]] = ???
  val txInstant_ : Ns0 with OneDate   [Ns0, In1_0[Date   ]] = ???
  val txT_       : Ns0 with OneLong   [Ns0, In1_0[Long   ]] = ???
  val txAdded_   : Ns0 with OneBoolean[Ns0, In1_0[Boolean]] = ???


  // Build on from entity id
//  def apply(e: Long): Ns0 = ???


  // Count entities
//  def apply(v: count): Ns1[Int] = ???


  // If only 1 or 2 transaction attributes are supplied, we can continue building on the original molecule
  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns0                                                 = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns1[a]                                              = ???

  // If we supply 2 or more tx attributes we return a generic molecule
  // This means that you can't continue expanding the molecule from the initial namespace anymore, so you'll
  // want to have the tx data defined in the end of the molecule
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_2[Any, Any, Any, Any, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_3[Any, Any, Any, Any, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_4[Any, Any, Any, Any, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_5[Any, Any, Any, Any, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_6[Any, Any, Any, Any, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_7[Any, Any, Any, Any, a, b, c, d, e, f, g] = ???


//  def ~[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns0                                                 = ???
//  def ~[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns1[a]                                              = ???
//  def ~[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_2[Any, Any, Any, Any, a, b]                = ???
//  def ~[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_3[Any, Any, Any, Any, a, b, c]             = ???
//  def ~[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_4[Any, Any, Any, Any, a, b, c, d]          = ???
//  def ~[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_5[Any, Any, Any, Any, a, b, c, d, e]       = ???
//  def ~[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_6[Any, Any, Any, Any, a, b, c, d, e, f]    = ???
//  def ~[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_7[Any, Any, Any, Any, a, b, c, d, e, f, g] = ???


  def apply[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns0                                                 = ???
  def apply[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns1[a]                                              = ???
//  def apply[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_2[Any, Any, Any, Any, a, b]                = ???
//  def apply[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_3[Any, Any, Any, Any, a, b, c]             = ???
//  def apply[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_4[Any, Any, Any, Any, a, b, c, d]          = ???
//  def apply[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_5[Any, Any, Any, Any, a, b, c, d, e]       = ???
//  def apply[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_6[Any, Any, Any, Any, a, b, c, d, e, f]    = ???
//  def apply[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_7[Any, Any, Any, Any, a, b, c, d, e, f, g] = ???
}


trait Molecule_1[Ns1[_], Ns2[_,_], In1_1[_,_], In1_2[_,_,_], A] extends NS1[A] with Nested2[Ns2, A] {

  val e         : Ns2[A, Long   ] with OneLong   [Ns2[A, Long   ], In1_2[Long   , A, Long   ]] = ???
  val a         : Ns2[A, String ] with OneString [Ns2[A, String ], In1_2[String , A, String ]] = ???
  val v         : Ns2[A, Any    ] with OneAny    [Ns2[A, Any    ], In1_2[Any    , A, Any    ]] = ???
  val ns        : Ns2[A, String ] with OneString [Ns2[A, String ], In1_2[String , A, String ]] = ???
  val txInstant : Ns2[A, Date   ] with OneDate   [Ns2[A, Date   ], In1_2[Date   , A, Date   ]] = ???
  val txT       : Ns2[A, Long   ] with OneLong   [Ns2[A, Long   ], In1_2[Long   , A, Long   ]] = ???
  val txAdded   : Ns2[A, Boolean] with OneBoolean[Ns2[A, Boolean], In1_2[Boolean, A, Boolean]] = ???

  val e_          : Ns1[Long   ] with OneLong   [Ns1[Long   ], In1_1[Long   , Long   ]] = ???
  val a_          : Ns1[String ] with OneString [Ns1[String ], In1_1[String , String ]] = ???
  val v_          : Ns1[Any    ] with OneAny    [Ns1[Any    ], In1_1[Any    , Any    ]] = ???
  val ns_         : Ns1[String ] with OneString [Ns1[String ], In1_1[String , String ]] = ???
  val txInstant_  : Ns1[Date   ] with OneDate   [Ns1[Date   ], In1_1[Date   , Date   ]] = ???
  val txT_        : Ns1[Long   ] with OneLong   [Ns1[Long   ], In1_1[Long   , Long   ]] = ???
  val txAdded_    : Ns1[Boolean] with OneBoolean[Ns1[Boolean], In1_1[Boolean, Boolean]] = ???

  def apply(v: min)     : Ns1[A] = ???
  def apply(v: max)     : Ns1[A] = ???
  def apply(v: rand)    : Ns1[A] = ???
  def apply(v: sample)  : Ns1[A] = ???

  def apply(v: mins)    : Ns1[Seq[A]] = ???
  def apply(v: maxs)    : Ns1[Seq[A]] = ???
  def apply(v: distinct): Ns1[Seq[A]] = ???
  def apply(v: rands)   : Ns1[Seq[A]] = ???
  def apply(v: samples) : Ns1[Seq[A]] = ???

  def apply(v: count)        : Ns1[Int] = ???
  def apply(v: countDistinct): Ns1[Int] = ???
  def apply(v: sum)          : Ns1[Double] = ???
  def apply(v: avg)          : Ns1[Double] = ???
  def apply(v: median)       : Ns1[Double] = ???
  def apply(v: variance)     : Ns1[Double] = ???
  def apply(v: stddev)       : Ns1[Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns1[A]                                                 = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns2[A, a]                                              = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_3[Any, Any, Any, Any, A, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_4[Any, Any, Any, Any, A, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_5[Any, Any, Any, Any, A, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_6[Any, Any, Any, Any, A, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_7[Any, Any, Any, Any, A, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_8[Any, Any, Any, Any, A, a, b, c, d, e, f, g] = ???

//  def ~[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns1[A]                                                 = ???
//  def ~[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns2[A, a]                                              = ???
//  def ~[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_3[Any, Any, Any, Any, A, a, b]                = ???
//  def ~[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_4[Any, Any, Any, Any, A, a, b, c]             = ???
//  def ~[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_5[Any, Any, Any, Any, A, a, b, c, d]          = ???
//  def ~[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_6[Any, Any, Any, Any, A, a, b, c, d, e]       = ???
//  def ~[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_7[Any, Any, Any, Any, A, a, b, c, d, e, f]    = ???
//  def ~[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_8[Any, Any, Any, Any, A, a, b, c, d, e, f, g] = ???

  def apply[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns1[A]                                                 = ???
  def apply[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns2[A, a]                                              = ???
//  def apply[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_3[Any, Any, Any, Any, A, a, b]                = ???
//  def apply[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_4[Any, Any, Any, Any, A, a, b, c]             = ???
//  def apply[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_5[Any, Any, Any, Any, A, a, b, c, d]          = ???
//  def apply[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_6[Any, Any, Any, Any, A, a, b, c, d, e]       = ???
//  def apply[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_7[Any, Any, Any, Any, A, a, b, c, d, e, f]    = ???
//  def apply[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_8[Any, Any, Any, Any, A, a, b, c, d, e, f, g] = ???
}


trait Molecule_2[Ns2[_,_], Ns3[_,_,_], In1_2[_,_,_], In1_3[_,_,_,_], A, B] extends NS2[A, B] {

  val e         : Ns3[A, B, Long   ] with OneLong   [Ns3[A, B, Long   ], In1_3[Long   , A, B, Long   ]] = ???
  val a         : Ns3[A, B, String ] with OneString [Ns3[A, B, String ], In1_3[String , A, B, String ]] = ???
  val v         : Ns3[A, B, Any    ] with OneAny    [Ns3[A, B, Any    ], In1_3[Any    , A, B, Any    ]] = ???
  val ns        : Ns3[A, B, String ] with OneString [Ns3[A, B, String ], In1_3[String , A, B, String ]] = ???
  val txInstant : Ns3[A, B, Date   ] with OneDate   [Ns3[A, B, Date   ], In1_3[Date   , A, B, Date   ]] = ???
  val txT       : Ns3[A, B, Long   ] with OneLong   [Ns3[A, B, Long   ], In1_3[Long   , A, B, Long   ]] = ???
  val txAdded   : Ns3[A, B, Boolean] with OneBoolean[Ns3[A, B, Boolean], In1_3[Boolean, A, B, Boolean]] = ???

  val e_         : Ns2[A, Long   ] with OneLong   [Ns2[A, Long   ], In1_2[Long   , A, Long   ]] = ???
  val a_         : Ns2[A, String ] with OneString [Ns2[A, String ], In1_2[String , A, String ]] = ???
  val v_         : Ns2[A, Any    ] with OneAny    [Ns2[A, Any    ], In1_2[Any    , A, Any    ]] = ???
  val ns_        : Ns2[A, String ] with OneString [Ns2[A, String ], In1_2[String , A, String ]] = ???
  val txInstant_ : Ns2[A, Date   ] with OneDate   [Ns2[A, Date   ], In1_2[Date   , A, Date   ]] = ???
  val txT_       : Ns2[A, Long   ] with OneLong   [Ns2[A, Long   ], In1_2[Long   , A, Long   ]] = ???
  val txAdded_   : Ns2[A, Boolean] with OneBoolean[Ns2[A, Boolean], In1_2[Boolean, A, Boolean]] = ???

  def apply(v: min)     : Ns2[A, B] = ???
  def apply(v: max)     : Ns2[A, B] = ???
  def apply(v: rand)    : Ns2[A, B] = ???
  def apply(v: sample)  : Ns2[A, B] = ???

  def apply(v: mins)    : Ns2[A, Seq[B]] = ???
  def apply(v: maxs)    : Ns2[A, Seq[B]] = ???
  def apply(v: distinct): Ns2[A, Seq[B]] = ???
  def apply(v: rands)   : Ns2[A, Seq[B]] = ???
  def apply(v: samples) : Ns2[A, Seq[B]] = ???

  def apply(v: count)        : Ns2[A, Int] = ???
  def apply(v: countDistinct): Ns2[A, Int] = ???
  def apply(v: sum)          : Ns2[A, Double] = ???
  def apply(v: avg)          : Ns2[A, Double] = ???
  def apply(v: median)       : Ns2[A, Double] = ???
  def apply(v: variance)     : Ns2[A, Double] = ???
  def apply(v: stddev)       : Ns2[A, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns2[A, B]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns3[A, B, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_4[Any, Any, Any, Any, A, B, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_5[Any, Any, Any, Any, A, B, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_6[Any, Any, Any, Any, A, B, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_7[Any, Any, Any, Any, A, B, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_8[Any, Any, Any, Any, A, B, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_9[Any, Any, Any, Any, A, B, a, b, c, d, e, f, g] = ???

//  def ~[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns2[A, B]                                       = ???
//  def ~[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns3[A, B, a]                                    = ???
//  def ~[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_4[Any, Any, Any, Any, A, B, a, b]                = ???
//  def ~[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_5[Any, Any, Any, Any, A, B, a, b, c]             = ???
//  def ~[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_6[Any, Any, Any, Any, A, B, a, b, c, d]          = ???
//  def ~[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_7[Any, Any, Any, Any, A, B, a, b, c, d, e]       = ???
//  def ~[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_8[Any, Any, Any, Any, A, B, a, b, c, d, e, f]    = ???
//  def ~[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_9[Any, Any, Any, Any, A, B, a, b, c, d, e, f, g] = ???

  def apply[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns2[A, B]                                       = ???
  def apply[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns3[A, B, a]                                    = ???
//  def apply[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_4[Any, Any, Any, Any, A, B, a, b]                = ???
//  def apply[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_5[Any, Any, Any, Any, A, B, a, b, c]             = ???
//  def apply[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_6[Any, Any, Any, Any, A, B, a, b, c, d]          = ???
//  def apply[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_7[Any, Any, Any, Any, A, B, a, b, c, d, e]       = ???
//  def apply[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_8[Any, Any, Any, Any, A, B, a, b, c, d, e, f]    = ???
//  def apply[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_9[Any, Any, Any, Any, A, B, a, b, c, d, e, f, g] = ???
}


trait Molecule_3[Ns3[_,_,_], Ns4[_,_,_,_], In1_3[_,_,_,_], In1_4[_,_,_,_,_], A, B, C] extends NS3[A, B, C] {

  val e         : Ns4[A, B, C, Long   ] with OneLong   [Ns4[A, B, C, Long   ], In1_4[Long   , A, B, C, Long   ]] = ???
  val a         : Ns4[A, B, C, String ] with OneString [Ns4[A, B, C, String ], In1_4[String , A, B, C, String ]] = ???
  val v         : Ns4[A, B, C, Any    ] with OneAny    [Ns4[A, B, C, Any    ], In1_4[Any    , A, B, C, Any    ]] = ???
  val ns        : Ns4[A, B, C, String ] with OneString [Ns4[A, B, C, String ], In1_4[String , A, B, C, String ]] = ???
  val txInstant : Ns4[A, B, C, Date   ] with OneDate   [Ns4[A, B, C, Date   ], In1_4[Date   , A, B, C, Date   ]] = ???
  val txT       : Ns4[A, B, C, Long   ] with OneLong   [Ns4[A, B, C, Long   ], In1_4[Long   , A, B, C, Long   ]] = ???
  val txAdded   : Ns4[A, B, C, Boolean] with OneBoolean[Ns4[A, B, C, Boolean], In1_4[Boolean, A, B, C, Boolean]] = ???

  val e_         : Ns3[A, B, Long   ] with OneLong   [Ns3[A, B, Long   ], In1_3[Long   , A, B, Long   ]] = ???
  val a_         : Ns3[A, B, String ] with OneString [Ns3[A, B, String ], In1_3[String , A, B, String ]] = ???
  val v_         : Ns3[A, B, Any    ] with OneAny    [Ns3[A, B, Any    ], In1_3[Any    , A, B, Any    ]] = ???
  val ns_        : Ns3[A, B, String ] with OneString [Ns3[A, B, String ], In1_3[String , A, B, String ]] = ???
  val txInstant_ : Ns3[A, B, Date   ] with OneDate   [Ns3[A, B, Date   ], In1_3[Date   , A, B, Date   ]] = ???
  val txT_       : Ns3[A, B, Long   ] with OneLong   [Ns3[A, B, Long   ], In1_3[Long   , A, B, Long   ]] = ???
  val txAdded_   : Ns3[A, B, Boolean] with OneBoolean[Ns3[A, B, Boolean], In1_3[Boolean, A, B, Boolean]] = ???

  def apply(v: min)     : Ns3[A, B, C] = ???
  def apply(v: max)     : Ns3[A, B, C] = ???
  def apply(v: rand)    : Ns3[A, B, C] = ???
  def apply(v: sample)  : Ns3[A, B, C] = ???

  def apply(v: mins)    : Ns3[A, B, Seq[C]] = ???
  def apply(v: maxs)    : Ns3[A, B, Seq[C]] = ???
  def apply(v: distinct): Ns3[A, B, Seq[C]] = ???
  def apply(v: rands)   : Ns3[A, B, Seq[C]] = ???
  def apply(v: samples) : Ns3[A, B, Seq[C]] = ???

  def apply(v: count)        : Ns3[A, B, Int] = ???
  def apply(v: countDistinct): Ns3[A, B, Int] = ???
  def apply(v: sum)          : Ns3[A, B, Double] = ???
  def apply(v: avg)          : Ns3[A, B, Double] = ???
  def apply(v: median)       : Ns3[A, B, Double] = ???
  def apply(v: variance)     : Ns3[A, B, Double] = ???
  def apply(v: stddev)       : Ns3[A, B, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns3 [A, B, C]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns4 [A, B, C, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_5 [Any, Any, Any, Any, A, B, C, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_6 [Any, Any, Any, Any, A, B, C, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_7 [Any, Any, Any, Any, A, B, C, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_8 [Any, Any, Any, Any, A, B, C, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_9 [Any, Any, Any, Any, A, B, C, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_10[Any, Any, Any, Any, A, B, C, a, b, c, d, e, f, g] = ???
}


trait Molecule_4[Ns4[_,_,_,_], Ns5[_,_,_,_,_], In1_4[_,_,_,_,_], In1_5[_,_,_,_,_,_], A, B, C, D] extends NS4[A, B, C, D] {

  val e         : Ns5[A, B, C, D, Long   ] with OneLong   [Ns5[A, B, C, D, Long   ], In1_5[Long   , A, B, C, D, Long   ]] = ???
  val a         : Ns5[A, B, C, D, String ] with OneString [Ns5[A, B, C, D, String ], In1_5[String , A, B, C, D, String ]] = ???
  val v         : Ns5[A, B, C, D, Any    ] with OneAny    [Ns5[A, B, C, D, Any    ], In1_5[Any    , A, B, C, D, Any    ]] = ???
  val ns        : Ns5[A, B, C, D, String ] with OneString [Ns5[A, B, C, D, String ], In1_5[String , A, B, C, D, String ]] = ???
  val txInstant : Ns5[A, B, C, D, Date   ] with OneDate   [Ns5[A, B, C, D, Date   ], In1_5[Date   , A, B, C, D, Date   ]] = ???
  val txT       : Ns5[A, B, C, D, Long   ] with OneLong   [Ns5[A, B, C, D, Long   ], In1_5[Long   , A, B, C, D, Long   ]] = ???
  val txAdded   : Ns5[A, B, C, D, Boolean] with OneBoolean[Ns5[A, B, C, D, Boolean], In1_5[Boolean, A, B, C, D, Boolean]] = ???

  val e_         : Ns4[A, B, C, Long   ] with OneLong   [Ns4[A, B, C, Long   ], In1_4[Long   , A, B, C, Long   ]] = ???
  val a_         : Ns4[A, B, C, String ] with OneString [Ns4[A, B, C, String ], In1_4[String , A, B, C, String ]] = ???
  val v_         : Ns4[A, B, C, Any    ] with OneAny    [Ns4[A, B, C, Any    ], In1_4[Any    , A, B, C, Any    ]] = ???
  val ns_        : Ns4[A, B, C, String ] with OneString [Ns4[A, B, C, String ], In1_4[String , A, B, C, String ]] = ???
  val txInstant_ : Ns4[A, B, C, Date   ] with OneDate   [Ns4[A, B, C, Date   ], In1_4[Date   , A, B, C, Date   ]] = ???
  val txT_       : Ns4[A, B, C, Long   ] with OneLong   [Ns4[A, B, C, Long   ], In1_4[Long   , A, B, C, Long   ]] = ???
  val txAdded_   : Ns4[A, B, C, Boolean] with OneBoolean[Ns4[A, B, C, Boolean], In1_4[Boolean, A, B, C, Boolean]] = ???

  def apply(v: min)     : Ns4[A, B, C, D] = ???
  def apply(v: max)     : Ns4[A, B, C, D] = ???
  def apply(v: rand)    : Ns4[A, B, C, D] = ???
  def apply(v: sample)  : Ns4[A, B, C, D] = ???

  def apply(v: mins)    : Ns4[A, B, C, Seq[D]] = ???
  def apply(v: maxs)    : Ns4[A, B, C, Seq[D]] = ???
  def apply(v: distinct): Ns4[A, B, C, Seq[D]] = ???
  def apply(v: rands)   : Ns4[A, B, C, Seq[D]] = ???
  def apply(v: samples) : Ns4[A, B, C, Seq[D]] = ???

  def apply(v: count)        : Ns4[A, B, C, Int] = ???
  def apply(v: countDistinct): Ns4[A, B, C, Int] = ???
  def apply(v: sum)          : Ns4[A, B, C, Double] = ???
  def apply(v: avg)          : Ns4[A, B, C, Double] = ???
  def apply(v: median)       : Ns4[A, B, C, Double] = ???
  def apply(v: variance)     : Ns4[A, B, C, Double] = ???
  def apply(v: stddev)       : Ns4[A, B, C, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns4 [A, B, C, D]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns5 [A, B, C, D, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_6 [Any, Any, Any, Any, A, B, C, D, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_7 [Any, Any, Any, Any, A, B, C, D, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_8 [Any, Any, Any, Any, A, B, C, D, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_9 [Any, Any, Any, Any, A, B, C, D, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_10[Any, Any, Any, Any, A, B, C, D, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_11[Any, Any, Any, Any, A, B, C, D, a, b, c, d, e, f, g] = ???
}


trait Molecule_5[Ns5[_,_,_,_,_], Ns6[_,_,_,_,_,_], In1_5[_,_,_,_,_,_], In1_6[_,_,_,_,_,_,_], A, B, C, D, E] extends NS5[A, B, C, D, E] {

  val e         : Ns6[A, B, C, D, E, Long   ] with OneLong   [Ns6[A, B, C, D, E, Long   ], In1_6[Long   , A, B, C, D, E, Long   ]] = ???
  val a         : Ns6[A, B, C, D, E, String ] with OneString [Ns6[A, B, C, D, E, String ], In1_6[String , A, B, C, D, E, String ]] = ???
  val v         : Ns6[A, B, C, D, E, Any    ] with OneAny    [Ns6[A, B, C, D, E, Any    ], In1_6[Any    , A, B, C, D, E, Any    ]] = ???
  val ns        : Ns6[A, B, C, D, E, String ] with OneString [Ns6[A, B, C, D, E, String ], In1_6[String , A, B, C, D, E, String ]] = ???
  val txInstant : Ns6[A, B, C, D, E, Date   ] with OneDate   [Ns6[A, B, C, D, E, Date   ], In1_6[Date   , A, B, C, D, E, Date   ]] = ???
  val txT       : Ns6[A, B, C, D, E, Long   ] with OneLong   [Ns6[A, B, C, D, E, Long   ], In1_6[Long   , A, B, C, D, E, Long   ]] = ???
  val txAdded   : Ns6[A, B, C, D, E, Boolean] with OneBoolean[Ns6[A, B, C, D, E, Boolean], In1_6[Boolean, A, B, C, D, E, Boolean]] = ???

  val e_         : Ns5[A, B, C, D, Long   ] with OneLong   [Ns5[A, B, C, D, Long   ], In1_5[Long   , A, B, C, D, Long   ]] = ???
  val a_         : Ns5[A, B, C, D, String ] with OneString [Ns5[A, B, C, D, String ], In1_5[String , A, B, C, D, String ]] = ???
  val v_         : Ns5[A, B, C, D, Any    ] with OneAny    [Ns5[A, B, C, D, Any    ], In1_5[Any    , A, B, C, D, Any    ]] = ???
  val ns_        : Ns5[A, B, C, D, String ] with OneString [Ns5[A, B, C, D, String ], In1_5[String , A, B, C, D, String ]] = ???
  val txInstant_ : Ns5[A, B, C, D, Date   ] with OneDate   [Ns5[A, B, C, D, Date   ], In1_5[Date   , A, B, C, D, Date   ]] = ???
  val txT_       : Ns5[A, B, C, D, Long   ] with OneLong   [Ns5[A, B, C, D, Long   ], In1_5[Long   , A, B, C, D, Long   ]] = ???
  val txAdded_   : Ns5[A, B, C, D, Boolean] with OneBoolean[Ns5[A, B, C, D, Boolean], In1_5[Boolean, A, B, C, D, Boolean]] = ???

  def apply(v: min)     : Ns5[A, B, C, D, E] = ???
  def apply(v: max)     : Ns5[A, B, C, D, E] = ???
  def apply(v: rand)    : Ns5[A, B, C, D, E] = ???
  def apply(v: sample)  : Ns5[A, B, C, D, E] = ???

  def apply(v: mins)    : Ns5[A, B, C, D, Seq[E]] = ???
  def apply(v: maxs)    : Ns5[A, B, C, D, Seq[E]] = ???
  def apply(v: distinct): Ns5[A, B, C, D, Seq[E]] = ???
  def apply(v: rands)   : Ns5[A, B, C, D, Seq[E]] = ???
  def apply(v: samples) : Ns5[A, B, C, D, Seq[E]] = ???

  def apply(v: count)        : Ns5[A, B, C, D, Int] = ???
  def apply(v: countDistinct): Ns5[A, B, C, D, Int] = ???
  def apply(v: sum)          : Ns5[A, B, C, D, Double] = ???
  def apply(v: avg)          : Ns5[A, B, C, D, Double] = ???
  def apply(v: median)       : Ns5[A, B, C, D, Double] = ???
  def apply(v: variance)     : Ns5[A, B, C, D, Double] = ???
  def apply(v: stddev)       : Ns5[A, B, C, D, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns5 [A, B, C, D, E]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns6 [A, B, C, D, E, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_7 [Any, Any, Any, Any, A, B, C, D, E, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_8 [Any, Any, Any, Any, A, B, C, D, E, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_9 [Any, Any, Any, Any, A, B, C, D, E, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_10[Any, Any, Any, Any, A, B, C, D, E, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_11[Any, Any, Any, Any, A, B, C, D, E, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_12[Any, Any, Any, Any, A, B, C, D, E, a, b, c, d, e, f, g] = ???
}


trait Molecule_6[Ns6[_,_,_,_,_,_], Ns7[_,_,_,_,_,_,_], In1_6[_,_,_,_,_,_,_], In1_7[_,_,_,_,_,_,_,_], A, B, C, D, E, F] extends NS6[A, B, C, D, E, F] {

  val e         : Ns7[A, B, C, D, E, F, Long   ] with OneLong   [Ns7[A, B, C, D, E, F, Long   ], In1_7[Long   , A, B, C, D, E, F, Long   ]] = ???
  val a         : Ns7[A, B, C, D, E, F, String ] with OneString [Ns7[A, B, C, D, E, F, String ], In1_7[String , A, B, C, D, E, F, String ]] = ???
  val v         : Ns7[A, B, C, D, E, F, Any    ] with OneAny    [Ns7[A, B, C, D, E, F, Any    ], In1_7[Any    , A, B, C, D, E, F, Any    ]] = ???
  val ns        : Ns7[A, B, C, D, E, F, String ] with OneString [Ns7[A, B, C, D, E, F, String ], In1_7[String , A, B, C, D, E, F, String ]] = ???
  val txInstant : Ns7[A, B, C, D, E, F, Date   ] with OneDate   [Ns7[A, B, C, D, E, F, Date   ], In1_7[Date   , A, B, C, D, E, F, Date   ]] = ???
  val txT       : Ns7[A, B, C, D, E, F, Long   ] with OneLong   [Ns7[A, B, C, D, E, F, Long   ], In1_7[Long   , A, B, C, D, E, F, Long   ]] = ???
  val txAdded   : Ns7[A, B, C, D, E, F, Boolean] with OneBoolean[Ns7[A, B, C, D, E, F, Boolean], In1_7[Boolean, A, B, C, D, E, F, Boolean]] = ???

  val e_         : Ns6[A, B, C, D, E, Long   ] with OneLong   [Ns6[A, B, C, D, E, Long   ], In1_6[Long   , A, B, C, D, E, Long   ]] = ???
  val a_         : Ns6[A, B, C, D, E, String ] with OneString [Ns6[A, B, C, D, E, String ], In1_6[String , A, B, C, D, E, String ]] = ???
  val v_         : Ns6[A, B, C, D, E, Any    ] with OneAny    [Ns6[A, B, C, D, E, Any    ], In1_6[Any    , A, B, C, D, E, Any    ]] = ???
  val ns_        : Ns6[A, B, C, D, E, String ] with OneString [Ns6[A, B, C, D, E, String ], In1_6[String , A, B, C, D, E, String ]] = ???
  val txInstant_ : Ns6[A, B, C, D, E, Date   ] with OneDate   [Ns6[A, B, C, D, E, Date   ], In1_6[Date   , A, B, C, D, E, Date   ]] = ???
  val txT_       : Ns6[A, B, C, D, E, Long   ] with OneLong   [Ns6[A, B, C, D, E, Long   ], In1_6[Long   , A, B, C, D, E, Long   ]] = ???
  val txAdded_   : Ns6[A, B, C, D, E, Boolean] with OneBoolean[Ns6[A, B, C, D, E, Boolean], In1_6[Boolean, A, B, C, D, E, Boolean]] = ???

  def apply(v: min)     : Ns6[A, B, C, D, E, F] = ???
  def apply(v: max)     : Ns6[A, B, C, D, E, F] = ???
  def apply(v: rand)    : Ns6[A, B, C, D, E, F] = ???
  def apply(v: sample)  : Ns6[A, B, C, D, E, F] = ???

  def apply(v: mins)    : Ns6[A, B, C, D, E, Seq[F]] = ???
  def apply(v: maxs)    : Ns6[A, B, C, D, E, Seq[F]] = ???
  def apply(v: distinct): Ns6[A, B, C, D, E, Seq[F]] = ???
  def apply(v: rands)   : Ns6[A, B, C, D, E, Seq[F]] = ???
  def apply(v: samples) : Ns6[A, B, C, D, E, Seq[F]] = ???

  def apply(v: count)        : Ns6[A, B, C, D, E, Int] = ???
  def apply(v: countDistinct): Ns6[A, B, C, D, E, Int] = ???
  def apply(v: sum)          : Ns6[A, B, C, D, E, Double] = ???
  def apply(v: avg)          : Ns6[A, B, C, D, E, Double] = ???
  def apply(v: median)       : Ns6[A, B, C, D, E, Double] = ???
  def apply(v: variance)     : Ns6[A, B, C, D, E, Double] = ???
  def apply(v: stddev)       : Ns6[A, B, C, D, E, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns6 [A, B, C, D, E, F]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns7 [A, B, C, D, E, F, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_8 [Any, Any, Any, Any, A, B, C, D, E, F, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_9 [Any, Any, Any, Any, A, B, C, D, E, F, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_10[Any, Any, Any, Any, A, B, C, D, E, F, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_11[Any, Any, Any, Any, A, B, C, D, E, F, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_12[Any, Any, Any, Any, A, B, C, D, E, F, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_13[Any, Any, Any, Any, A, B, C, D, E, F, a, b, c, d, e, f, g] = ???
}


trait Molecule_7[Ns7[_,_,_,_,_,_,_], Ns8[_,_,_,_,_,_,_,_], In1_7[_,_,_,_,_,_,_,_], In1_8[_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G] extends NS7[A, B, C, D, E, F, G] {

  val e         : Ns8[A, B, C, D, E, F, G, Long   ] with OneLong   [Ns8[A, B, C, D, E, F, G, Long   ], In1_8[Long   , A, B, C, D, E, F, G, Long   ]] = ???
  val a         : Ns8[A, B, C, D, E, F, G, String ] with OneString [Ns8[A, B, C, D, E, F, G, String ], In1_8[String , A, B, C, D, E, F, G, String ]] = ???
  val v         : Ns8[A, B, C, D, E, F, G, Any    ] with OneAny    [Ns8[A, B, C, D, E, F, G, Any    ], In1_8[Any    , A, B, C, D, E, F, G, Any    ]] = ???
  val ns        : Ns8[A, B, C, D, E, F, G, String ] with OneString [Ns8[A, B, C, D, E, F, G, String ], In1_8[String , A, B, C, D, E, F, G, String ]] = ???
  val txInstant : Ns8[A, B, C, D, E, F, G, Date   ] with OneDate   [Ns8[A, B, C, D, E, F, G, Date   ], In1_8[Date   , A, B, C, D, E, F, G, Date   ]] = ???
  val txT       : Ns8[A, B, C, D, E, F, G, Long   ] with OneLong   [Ns8[A, B, C, D, E, F, G, Long   ], In1_8[Long   , A, B, C, D, E, F, G, Long   ]] = ???
  val txAdded   : Ns8[A, B, C, D, E, F, G, Boolean] with OneBoolean[Ns8[A, B, C, D, E, F, G, Boolean], In1_8[Boolean, A, B, C, D, E, F, G, Boolean]] = ???

  val e_         : Ns7[A, B, C, D, E, F, Long   ] with OneLong   [Ns7[A, B, C, D, E, F, Long   ], In1_7[Long   , A, B, C, D, E, F, Long   ]] = ???
  val a_         : Ns7[A, B, C, D, E, F, String ] with OneString [Ns7[A, B, C, D, E, F, String ], In1_7[String , A, B, C, D, E, F, String ]] = ???
  val v_         : Ns7[A, B, C, D, E, F, Any    ] with OneAny    [Ns7[A, B, C, D, E, F, Any    ], In1_7[Any    , A, B, C, D, E, F, Any    ]] = ???
  val ns_        : Ns7[A, B, C, D, E, F, String ] with OneString [Ns7[A, B, C, D, E, F, String ], In1_7[String , A, B, C, D, E, F, String ]] = ???
  val txInstant_ : Ns7[A, B, C, D, E, F, Date   ] with OneDate   [Ns7[A, B, C, D, E, F, Date   ], In1_7[Date   , A, B, C, D, E, F, Date   ]] = ???
  val txT_       : Ns7[A, B, C, D, E, F, Long   ] with OneLong   [Ns7[A, B, C, D, E, F, Long   ], In1_7[Long   , A, B, C, D, E, F, Long   ]] = ???
  val txAdded_   : Ns7[A, B, C, D, E, F, Boolean] with OneBoolean[Ns7[A, B, C, D, E, F, Boolean], In1_7[Boolean, A, B, C, D, E, F, Boolean]] = ???

  def apply(v: min)     : Ns7[A, B, C, D, E, F, G] = ???
  def apply(v: max)     : Ns7[A, B, C, D, E, F, G] = ???
  def apply(v: rand)    : Ns7[A, B, C, D, E, F, G] = ???
  def apply(v: sample)  : Ns7[A, B, C, D, E, F, G] = ???

  def apply(v: mins)    : Ns7[A, B, C, D, E, F, Seq[G]] = ???
  def apply(v: maxs)    : Ns7[A, B, C, D, E, F, Seq[G]] = ???
  def apply(v: distinct): Ns7[A, B, C, D, E, F, Seq[G]] = ???
  def apply(v: rands)   : Ns7[A, B, C, D, E, F, Seq[G]] = ???
  def apply(v: samples) : Ns7[A, B, C, D, E, F, Seq[G]] = ???

  def apply(v: count)        : Ns7[A, B, C, D, E, F, Int] = ???
  def apply(v: countDistinct): Ns7[A, B, C, D, E, F, Int] = ???
  def apply(v: sum)          : Ns7[A, B, C, D, E, F, Double] = ???
  def apply(v: avg)          : Ns7[A, B, C, D, E, F, Double] = ???
  def apply(v: median)       : Ns7[A, B, C, D, E, F, Double] = ???
  def apply(v: variance)     : Ns7[A, B, C, D, E, F, Double] = ???
  def apply(v: stddev)       : Ns7[A, B, C, D, E, F, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns7 [A, B, C, D, E, F, G]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns8 [A, B, C, D, E, F, G, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_9 [Any, Any, Any, Any, A, B, C, D, E, F, G, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_10[Any, Any, Any, Any, A, B, C, D, E, F, G, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_11[Any, Any, Any, Any, A, B, C, D, E, F, G, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_12[Any, Any, Any, Any, A, B, C, D, E, F, G, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_13[Any, Any, Any, Any, A, B, C, D, E, F, G, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_14[Any, Any, Any, Any, A, B, C, D, E, F, G, a, b, c, d, e, f, g] = ???
}


trait Molecule_8[Ns8[_,_,_,_,_,_,_,_], Ns9[_,_,_,_,_,_,_,_,_], In1_8[_,_,_,_,_,_,_,_,_], In1_9[_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H] extends NS8[A, B, C, D, E, F, G, H] {

  val e         : Ns9[A, B, C, D, E, F, G, H, Long   ] with OneLong   [Ns9[A, B, C, D, E, F, G, H, Long   ], In1_9[Long   , A, B, C, D, E, F, G, H, Long   ]] = ???
  val a         : Ns9[A, B, C, D, E, F, G, H, String ] with OneString [Ns9[A, B, C, D, E, F, G, H, String ], In1_9[String , A, B, C, D, E, F, G, H, String ]] = ???
  val v         : Ns9[A, B, C, D, E, F, G, H, Any    ] with OneAny    [Ns9[A, B, C, D, E, F, G, H, Any    ], In1_9[Any    , A, B, C, D, E, F, G, H, Any    ]] = ???
  val ns        : Ns9[A, B, C, D, E, F, G, H, String ] with OneString [Ns9[A, B, C, D, E, F, G, H, String ], In1_9[String , A, B, C, D, E, F, G, H, String ]] = ???
  val txInstant : Ns9[A, B, C, D, E, F, G, H, Date   ] with OneDate   [Ns9[A, B, C, D, E, F, G, H, Date   ], In1_9[Date   , A, B, C, D, E, F, G, H, Date   ]] = ???
  val txT       : Ns9[A, B, C, D, E, F, G, H, Long   ] with OneLong   [Ns9[A, B, C, D, E, F, G, H, Long   ], In1_9[Long   , A, B, C, D, E, F, G, H, Long   ]] = ???
  val txAdded   : Ns9[A, B, C, D, E, F, G, H, Boolean] with OneBoolean[Ns9[A, B, C, D, E, F, G, H, Boolean], In1_9[Boolean, A, B, C, D, E, F, G, H, Boolean]] = ???

  val e_         : Ns8[A, B, C, D, E, F, G, Long   ] with OneLong   [Ns8[A, B, C, D, E, F, G, Long   ], In1_8[Long   , A, B, C, D, E, F, G, Long   ]] = ???
  val a_         : Ns8[A, B, C, D, E, F, G, String ] with OneString [Ns8[A, B, C, D, E, F, G, String ], In1_8[String , A, B, C, D, E, F, G, String ]] = ???
  val v_         : Ns8[A, B, C, D, E, F, G, Any    ] with OneAny    [Ns8[A, B, C, D, E, F, G, Any    ], In1_8[Any    , A, B, C, D, E, F, G, Any    ]] = ???
  val ns_        : Ns8[A, B, C, D, E, F, G, String ] with OneString [Ns8[A, B, C, D, E, F, G, String ], In1_8[String , A, B, C, D, E, F, G, String ]] = ???
  val txInstant_ : Ns8[A, B, C, D, E, F, G, Date   ] with OneDate   [Ns8[A, B, C, D, E, F, G, Date   ], In1_8[Date   , A, B, C, D, E, F, G, Date   ]] = ???
  val txT_       : Ns8[A, B, C, D, E, F, G, Long   ] with OneLong   [Ns8[A, B, C, D, E, F, G, Long   ], In1_8[Long   , A, B, C, D, E, F, G, Long   ]] = ???
  val txAdded_   : Ns8[A, B, C, D, E, F, G, Boolean] with OneBoolean[Ns8[A, B, C, D, E, F, G, Boolean], In1_8[Boolean, A, B, C, D, E, F, G, Boolean]] = ???

  def apply(v: min)     : Ns8[A, B, C, D, E, F, G, H] = ???
  def apply(v: max)     : Ns8[A, B, C, D, E, F, G, H] = ???
  def apply(v: rand)    : Ns8[A, B, C, D, E, F, G, H] = ???
  def apply(v: sample)  : Ns8[A, B, C, D, E, F, G, H] = ???

  def apply(v: mins)    : Ns8[A, B, C, D, E, F, G, Seq[H]] = ???
  def apply(v: maxs)    : Ns8[A, B, C, D, E, F, G, Seq[H]] = ???
  def apply(v: distinct): Ns8[A, B, C, D, E, F, G, Seq[H]] = ???
  def apply(v: rands)   : Ns8[A, B, C, D, E, F, G, Seq[H]] = ???
  def apply(v: samples) : Ns8[A, B, C, D, E, F, G, Seq[H]] = ???

  def apply(v: count)        : Ns8[A, B, C, D, E, F, G, Int] = ???
  def apply(v: countDistinct): Ns8[A, B, C, D, E, F, G, Int] = ???
  def apply(v: sum)          : Ns8[A, B, C, D, E, F, G, Double] = ???
  def apply(v: avg)          : Ns8[A, B, C, D, E, F, G, Double] = ???
  def apply(v: median)       : Ns8[A, B, C, D, E, F, G, Double] = ???
  def apply(v: variance)     : Ns8[A, B, C, D, E, F, G, Double] = ???
  def apply(v: stddev)       : Ns8[A, B, C, D, E, F, G, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns8 [A, B, C, D, E, F, G, H]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns9 [A, B, C, D, E, F, G, H, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_10[Any, Any, Any, Any, A, B, C, D, E, F, G, H, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_11[Any, Any, Any, Any, A, B, C, D, E, F, G, H, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_12[Any, Any, Any, Any, A, B, C, D, E, F, G, H, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_13[Any, Any, Any, Any, A, B, C, D, E, F, G, H, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_14[Any, Any, Any, Any, A, B, C, D, E, F, G, H, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_15[Any, Any, Any, Any, A, B, C, D, E, F, G, H, a, b, c, d, e, f, g] = ???
}


trait Molecule_9[Ns9[_,_,_,_,_,_,_,_,_], Ns10[_,_,_,_,_,_,_,_,_,_], In1_9[_,_,_,_,_,_,_,_,_,_], In1_10[_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I] extends NS9[A, B, C, D, E, F, G, H, I] {

  val e         : Ns10[A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [Ns10[A, B, C, D, E, F, G, H, I, Long   ], In1_10[Long   , A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val a         : Ns10[A, B, C, D, E, F, G, H, I, String ] with OneString [Ns10[A, B, C, D, E, F, G, H, I, String ], In1_10[String , A, B, C, D, E, F, G, H, I, String ]] = ???
  val v         : Ns10[A, B, C, D, E, F, G, H, I, Any    ] with OneAny    [Ns10[A, B, C, D, E, F, G, H, I, Any    ], In1_10[Any    , A, B, C, D, E, F, G, H, I, Any    ]] = ???
  val ns        : Ns10[A, B, C, D, E, F, G, H, I, String ] with OneString [Ns10[A, B, C, D, E, F, G, H, I, String ], In1_10[String , A, B, C, D, E, F, G, H, I, String ]] = ???
  val txInstant : Ns10[A, B, C, D, E, F, G, H, I, Date   ] with OneDate   [Ns10[A, B, C, D, E, F, G, H, I, Date   ], In1_10[Date   , A, B, C, D, E, F, G, H, I, Date   ]] = ???
  val txT       : Ns10[A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [Ns10[A, B, C, D, E, F, G, H, I, Long   ], In1_10[Long   , A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val txAdded   : Ns10[A, B, C, D, E, F, G, H, I, Boolean] with OneBoolean[Ns10[A, B, C, D, E, F, G, H, I, Boolean], In1_10[Boolean, A, B, C, D, E, F, G, H, I, Boolean]] = ???

  val e_         : Ns9[A, B, C, D, E, F, G, H, Long   ] with OneLong   [Ns9[A, B, C, D, E, F, G, H, Long   ], In1_9[Long   , A, B, C, D, E, F, G, H, Long   ]] = ???
  val a_         : Ns9[A, B, C, D, E, F, G, H, String ] with OneString [Ns9[A, B, C, D, E, F, G, H, String ], In1_9[String , A, B, C, D, E, F, G, H, String ]] = ???
  val v_         : Ns9[A, B, C, D, E, F, G, H, Any    ] with OneAny    [Ns9[A, B, C, D, E, F, G, H, Any    ], In1_9[Any    , A, B, C, D, E, F, G, H, Any    ]] = ???
  val ns_        : Ns9[A, B, C, D, E, F, G, H, String ] with OneString [Ns9[A, B, C, D, E, F, G, H, String ], In1_9[String , A, B, C, D, E, F, G, H, String ]] = ???
  val txInstant_ : Ns9[A, B, C, D, E, F, G, H, Date   ] with OneDate   [Ns9[A, B, C, D, E, F, G, H, Date   ], In1_9[Date   , A, B, C, D, E, F, G, H, Date   ]] = ???
  val txT_       : Ns9[A, B, C, D, E, F, G, H, Long   ] with OneLong   [Ns9[A, B, C, D, E, F, G, H, Long   ], In1_9[Long   , A, B, C, D, E, F, G, H, Long   ]] = ???
  val txAdded_   : Ns9[A, B, C, D, E, F, G, H, Boolean] with OneBoolean[Ns9[A, B, C, D, E, F, G, H, Boolean], In1_9[Boolean, A, B, C, D, E, F, G, H, Boolean]] = ???

  def apply(v: min)     : Ns9[A, B, C, D, E, F, G, H, I] = ???
  def apply(v: max)     : Ns9[A, B, C, D, E, F, G, H, I] = ???
  def apply(v: rand)    : Ns9[A, B, C, D, E, F, G, H, I] = ???
  def apply(v: sample)  : Ns9[A, B, C, D, E, F, G, H, I] = ???

  def apply(v: mins)    : Ns9[A, B, C, D, E, F, G, H, Seq[I]] = ???
  def apply(v: maxs)    : Ns9[A, B, C, D, E, F, G, H, Seq[I]] = ???
  def apply(v: distinct): Ns9[A, B, C, D, E, F, G, H, Seq[I]] = ???
  def apply(v: rands)   : Ns9[A, B, C, D, E, F, G, H, Seq[I]] = ???
  def apply(v: samples) : Ns9[A, B, C, D, E, F, G, H, Seq[I]] = ???

  def apply(v: count)        : Ns9[A, B, C, D, E, F, G, H, Int] = ???
  def apply(v: countDistinct): Ns9[A, B, C, D, E, F, G, H, Int] = ???
  def apply(v: sum)          : Ns9[A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: avg)          : Ns9[A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: median)       : Ns9[A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: variance)     : Ns9[A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: stddev)       : Ns9[A, B, C, D, E, F, G, H, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns9 [A, B, C, D, E, F, G, H, I]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns10[A, B, C, D, E, F, G, H, I, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_11[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_12[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_13[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_14[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_15[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_16[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, a, b, c, d, e, f, g] = ???
}


  trait Molecule_10[Ns10[_,_,_,_,_,_,_,_,_,_], Ns11[_,_,_,_,_,_,_,_,_,_,_], In1_10[_,_,_,_,_,_,_,_,_,_,_], In1_11[_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J] extends NS10[A, B, C, D, E, F, G, H, I, J] {

    val e         : Ns11[A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [Ns11[A, B, C, D, E, F, G, H, I, J, Long   ], In1_11[Long   , A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
    val a         : Ns11[A, B, C, D, E, F, G, H, I, J, String ] with OneString [Ns11[A, B, C, D, E, F, G, H, I, J, String ], In1_11[String , A, B, C, D, E, F, G, H, I, J, String ]] = ???
    val v         : Ns11[A, B, C, D, E, F, G, H, I, J, Any    ] with OneAny    [Ns11[A, B, C, D, E, F, G, H, I, J, Any    ], In1_11[Any    , A, B, C, D, E, F, G, H, I, J, Any    ]] = ???
    val ns        : Ns11[A, B, C, D, E, F, G, H, I, J, String ] with OneString [Ns11[A, B, C, D, E, F, G, H, I, J, String ], In1_11[String , A, B, C, D, E, F, G, H, I, J, String ]] = ???
    val txInstant : Ns11[A, B, C, D, E, F, G, H, I, J, Date   ] with OneDate   [Ns11[A, B, C, D, E, F, G, H, I, J, Date   ], In1_11[Date   , A, B, C, D, E, F, G, H, I, J, Date   ]] = ???
    val txT       : Ns11[A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [Ns11[A, B, C, D, E, F, G, H, I, J, Long   ], In1_11[Long   , A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
    val txAdded   : Ns11[A, B, C, D, E, F, G, H, I, J, Boolean] with OneBoolean[Ns11[A, B, C, D, E, F, G, H, I, J, Boolean], In1_11[Boolean, A, B, C, D, E, F, G, H, I, J, Boolean]] = ???

  val e_         : Ns10[A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [Ns10[A, B, C, D, E, F, G, H, I, Long   ], In1_10[Long   , A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val a_         : Ns10[A, B, C, D, E, F, G, H, I, String ] with OneString [Ns10[A, B, C, D, E, F, G, H, I, String ], In1_10[String , A, B, C, D, E, F, G, H, I, String ]] = ???
  val v_         : Ns10[A, B, C, D, E, F, G, H, I, Any    ] with OneAny    [Ns10[A, B, C, D, E, F, G, H, I, Any    ], In1_10[Any    , A, B, C, D, E, F, G, H, I, Any    ]] = ???
  val ns_        : Ns10[A, B, C, D, E, F, G, H, I, String ] with OneString [Ns10[A, B, C, D, E, F, G, H, I, String ], In1_10[String , A, B, C, D, E, F, G, H, I, String ]] = ???
  val txInstant_ : Ns10[A, B, C, D, E, F, G, H, I, Date   ] with OneDate   [Ns10[A, B, C, D, E, F, G, H, I, Date   ], In1_10[Date   , A, B, C, D, E, F, G, H, I, Date   ]] = ???
  val txT_       : Ns10[A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [Ns10[A, B, C, D, E, F, G, H, I, Long   ], In1_10[Long   , A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val txAdded_   : Ns10[A, B, C, D, E, F, G, H, I, Boolean] with OneBoolean[Ns10[A, B, C, D, E, F, G, H, I, Boolean], In1_10[Boolean, A, B, C, D, E, F, G, H, I, Boolean]] = ???

    def apply(v: min)     : Ns10[A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: max)     : Ns10[A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: rand)    : Ns10[A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: sample)  : Ns10[A, B, C, D, E, F, G, H, I, J] = ???

    def apply(v: mins)    : Ns10[A, B, C, D, E, F, G, H, I, Seq[J]] = ???
    def apply(v: maxs)    : Ns10[A, B, C, D, E, F, G, H, I, Seq[J]] = ???
    def apply(v: distinct): Ns10[A, B, C, D, E, F, G, H, I, Seq[J]] = ???
    def apply(v: rands)   : Ns10[A, B, C, D, E, F, G, H, I, Seq[J]] = ???
    def apply(v: samples) : Ns10[A, B, C, D, E, F, G, H, I, Seq[J]] = ???

    def apply(v: count)        : Ns10[A, B, C, D, E, F, G, H, I, Int] = ???
    def apply(v: countDistinct): Ns10[A, B, C, D, E, F, G, H, I, Int] = ???
    def apply(v: sum)          : Ns10[A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: avg)          : Ns10[A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: median)       : Ns10[A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: variance)     : Ns10[A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: stddev)       : Ns10[A, B, C, D, E, F, G, H, I, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns10[A, B, C, D, E, F, G, H, I, J]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns11[A, B, C, D, E, F, G, H, I, J, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_12[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_13[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_14[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_15[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_16[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_17[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f, g] = ???
}


trait Molecule_11[Ns11[_,_,_,_,_,_,_,_,_,_,_], Ns12[_,_,_,_,_,_,_,_,_,_,_,_], In1_11[_,_,_,_,_,_,_,_,_,_,_,_], In1_12[_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K] extends NS11[A, B, C, D, E, F, G, H, I, J, K] {

  val e         : Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ], In1_12[Long   , A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val a         : Ns12[A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [Ns12[A, B, C, D, E, F, G, H, I, J, K, String ], In1_12[String , A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val v         : Ns12[A, B, C, D, E, F, G, H, I, J, K, Any    ] with OneAny    [Ns12[A, B, C, D, E, F, G, H, I, J, K, Any    ], In1_12[Any    , A, B, C, D, E, F, G, H, I, J, K, Any    ]] = ???
  val ns        : Ns12[A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [Ns12[A, B, C, D, E, F, G, H, I, J, K, String ], In1_12[String , A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val txInstant : Ns12[A, B, C, D, E, F, G, H, I, J, K, Date   ] with OneDate   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Date   ], In1_12[Date   , A, B, C, D, E, F, G, H, I, J, K, Date   ]] = ???
  val txT       : Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ], In1_12[Long   , A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val txAdded   : Ns12[A, B, C, D, E, F, G, H, I, J, K, Boolean] with OneBoolean[Ns12[A, B, C, D, E, F, G, H, I, J, K, Boolean], In1_12[Boolean, A, B, C, D, E, F, G, H, I, J, K, Boolean]] = ???

  val e_         : Ns11[A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [Ns11[A, B, C, D, E, F, G, H, I, J, Long   ], In1_11[Long   , A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
  val a_         : Ns11[A, B, C, D, E, F, G, H, I, J, String ] with OneString [Ns11[A, B, C, D, E, F, G, H, I, J, String ], In1_11[String , A, B, C, D, E, F, G, H, I, J, String ]] = ???
  val v_         : Ns11[A, B, C, D, E, F, G, H, I, J, Any    ] with OneAny    [Ns11[A, B, C, D, E, F, G, H, I, J, Any    ], In1_11[Any    , A, B, C, D, E, F, G, H, I, J, Any    ]] = ???
  val ns_        : Ns11[A, B, C, D, E, F, G, H, I, J, String ] with OneString [Ns11[A, B, C, D, E, F, G, H, I, J, String ], In1_11[String , A, B, C, D, E, F, G, H, I, J, String ]] = ???
  val txInstant_ : Ns11[A, B, C, D, E, F, G, H, I, J, Date   ] with OneDate   [Ns11[A, B, C, D, E, F, G, H, I, J, Date   ], In1_11[Date   , A, B, C, D, E, F, G, H, I, J, Date   ]] = ???
  val txT_       : Ns11[A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [Ns11[A, B, C, D, E, F, G, H, I, J, Long   ], In1_11[Long   , A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
  val txAdded_   : Ns11[A, B, C, D, E, F, G, H, I, J, Boolean] with OneBoolean[Ns11[A, B, C, D, E, F, G, H, I, J, Boolean], In1_11[Boolean, A, B, C, D, E, F, G, H, I, J, Boolean]] = ???

  def apply(v: min)     : Ns11[A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: max)     : Ns11[A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: rand)    : Ns11[A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: sample)  : Ns11[A, B, C, D, E, F, G, H, I, J, K] = ???

  def apply(v: mins)    : Ns11[A, B, C, D, E, F, G, H, I, J, Seq[K]] = ???
  def apply(v: maxs)    : Ns11[A, B, C, D, E, F, G, H, I, J, Seq[K]] = ???
  def apply(v: distinct): Ns11[A, B, C, D, E, F, G, H, I, J, Seq[K]] = ???
  def apply(v: rands)   : Ns11[A, B, C, D, E, F, G, H, I, J, Seq[K]] = ???
  def apply(v: samples) : Ns11[A, B, C, D, E, F, G, H, I, J, Seq[K]] = ???

  def apply(v: count)        : Ns11[A, B, C, D, E, F, G, H, I, J, Int] = ???
  def apply(v: countDistinct): Ns11[A, B, C, D, E, F, G, H, I, J, Int] = ???
  def apply(v: sum)          : Ns11[A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: avg)          : Ns11[A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: median)       : Ns11[A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: variance)     : Ns11[A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: stddev)       : Ns11[A, B, C, D, E, F, G, H, I, J, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns11[A, B, C, D, E, F, G, H, I, J, K]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns12[A, B, C, D, E, F, G, H, I, J, K, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_13[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_14[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_15[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_16[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_17[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_18[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f, g] = ???
}


trait Molecule_12[Ns12[_,_,_,_,_,_,_,_,_,_,_,_], Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_], In1_12[_,_,_,_,_,_,_,_,_,_,_,_,_], In1_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L] extends NS12[A, B, C, D, E, F, G, H, I, J, K, L] {

  val e         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In1_13[Long   , A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val a         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ], In1_13[String , A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val v         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Any    ] with OneAny    [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Any    ], In1_13[Any    , A, B, C, D, E, F, G, H, I, J, K, L, Any    ]] = ???
  val ns        : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ], In1_13[String , A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val txInstant : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Date   ] with OneDate   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Date   ], In1_13[Date   , A, B, C, D, E, F, G, H, I, J, K, L, Date   ]] = ???
  val txT       : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In1_13[Long   , A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val txAdded   : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Boolean] with OneBoolean[Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Boolean], In1_13[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, Boolean]] = ???

  val e_         : Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ], In1_12[Long   , A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val a_         : Ns12[A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [Ns12[A, B, C, D, E, F, G, H, I, J, K, String ], In1_12[String , A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val v_         : Ns12[A, B, C, D, E, F, G, H, I, J, K, Any    ] with OneAny    [Ns12[A, B, C, D, E, F, G, H, I, J, K, Any    ], In1_12[Any    , A, B, C, D, E, F, G, H, I, J, K, Any    ]] = ???
  val ns_        : Ns12[A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [Ns12[A, B, C, D, E, F, G, H, I, J, K, String ], In1_12[String , A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val txInstant_ : Ns12[A, B, C, D, E, F, G, H, I, J, K, Date   ] with OneDate   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Date   ], In1_12[Date   , A, B, C, D, E, F, G, H, I, J, K, Date   ]] = ???
  val txT_       : Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ], In1_12[Long   , A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val txAdded_   : Ns12[A, B, C, D, E, F, G, H, I, J, K, Boolean] with OneBoolean[Ns12[A, B, C, D, E, F, G, H, I, J, K, Boolean], In1_12[Boolean, A, B, C, D, E, F, G, H, I, J, K, Boolean]] = ???

  def apply(v: min)     : Ns12[A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: max)     : Ns12[A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: rand)    : Ns12[A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: sample)  : Ns12[A, B, C, D, E, F, G, H, I, J, K, L] = ???

  def apply(v: mins)    : Ns12[A, B, C, D, E, F, G, H, I, J, K, Seq[L]] = ???
  def apply(v: maxs)    : Ns12[A, B, C, D, E, F, G, H, I, J, K, Seq[L]] = ???
  def apply(v: distinct): Ns12[A, B, C, D, E, F, G, H, I, J, K, Seq[L]] = ???
  def apply(v: rands)   : Ns12[A, B, C, D, E, F, G, H, I, J, K, Seq[L]] = ???
  def apply(v: samples) : Ns12[A, B, C, D, E, F, G, H, I, J, K, Seq[L]] = ???

  def apply(v: count)        : Ns12[A, B, C, D, E, F, G, H, I, J, K, Int] = ???
  def apply(v: countDistinct): Ns12[A, B, C, D, E, F, G, H, I, J, K, Int] = ???
  def apply(v: sum)          : Ns12[A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: avg)          : Ns12[A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: median)       : Ns12[A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: variance)     : Ns12[A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: stddev)       : Ns12[A, B, C, D, E, F, G, H, I, J, K, Double] = ???


  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns12[A, B, C, D, E, F, G, H, I, J, K, L]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_14[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_15[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_16[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_17[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_18[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_19[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f, g] = ???
}


trait Molecule_13[Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_], Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS13[A, B, C, D, E, F, G, H, I, J, K, L, M] {

  val e         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In1_14[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val a         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In1_14[String , A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val v         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ] with OneAny    [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ], In1_14[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ]] = ???
  val ns        : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In1_14[String , A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val txInstant : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ] with OneDate   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ], In1_14[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ]] = ???
  val txT       : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In1_14[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val txAdded   : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean] with OneBoolean[Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean], In1_14[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean]] = ???

  val e_         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In1_13[Long   , A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val a_         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ], In1_13[String , A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val v_         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Any    ] with OneAny    [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Any    ], In1_13[Any    , A, B, C, D, E, F, G, H, I, J, K, L, Any    ]] = ???
  val ns_        : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ], In1_13[String , A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val txInstant_ : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Date   ] with OneDate   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Date   ], In1_13[Date   , A, B, C, D, E, F, G, H, I, J, K, L, Date   ]] = ???
  val txT_       : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In1_13[Long   , A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val txAdded_   : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Boolean] with OneBoolean[Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Boolean], In1_13[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, Boolean]] = ???

  def apply(v: min)     : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: max)     : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: rand)    : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: sample)  : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, M] = ???

  def apply(v: mins)    : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Seq[M]] = ???
  def apply(v: maxs)    : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Seq[M]] = ???
  def apply(v: distinct): Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Seq[M]] = ???
  def apply(v: rands)   : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Seq[M]] = ???
  def apply(v: samples) : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Seq[M]] = ???

  def apply(v: count)        : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Int] = ???
  def apply(v: countDistinct): Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Int] = ???
  def apply(v: sum)          : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: avg)          : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: median)       : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: variance)     : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: stddev)       : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, M]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_15[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_16[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_17[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_18[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_19[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_20[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f, g] = ???
}


trait Molecule_14[Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] {

  val e         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In1_15[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val a         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In1_15[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val v         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ] with OneAny    [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ], In1_15[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ]] = ???
  val ns        : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In1_15[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val txInstant : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ] with OneDate   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ], In1_15[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ]] = ???
  val txT       : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In1_15[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val txAdded   : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean] with OneBoolean[Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean], In1_15[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean]] = ???

  val e_         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In1_14[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val a_         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In1_14[String , A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val v_         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ] with OneAny    [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ], In1_14[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ]] = ???
  val ns_        : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In1_14[String , A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val txInstant_ : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ] with OneDate   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ], In1_14[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ]] = ???
  val txT_       : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In1_14[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val txAdded_   : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean] with OneBoolean[Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean], In1_14[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean]] = ???

  def apply(v: min)     : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: max)     : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: rand)    : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: sample)  : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???

  def apply(v: mins)    : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Seq[N]] = ???
  def apply(v: maxs)    : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Seq[N]] = ???
  def apply(v: distinct): Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Seq[N]] = ???
  def apply(v: rands)   : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Seq[N]] = ???
  def apply(v: samples) : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Seq[N]] = ???

  def apply(v: count)        : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Int] = ???
  def apply(v: countDistinct): Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Int] = ???
  def apply(v: sum)          : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: avg)          : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: median)       : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: variance)     : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: stddev)       : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_16[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_17[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_18[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_19[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_20[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_21[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f, g] = ???
}


trait Molecule_15[Ns15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {

  val e         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In1_16[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val a         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In1_16[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val v         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ] with OneAny    [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ], In1_16[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ]] = ???
  val ns        : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In1_16[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val txInstant : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ] with OneDate   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ], In1_16[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ]] = ???
  val txT       : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In1_16[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val txAdded   : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean] with OneBoolean[Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean], In1_16[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean]] = ???

  val e_         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In1_15[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val a_         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In1_15[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val v_         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ] with OneAny    [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ], In1_15[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ]] = ???
  val ns_        : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In1_15[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val txInstant_ : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ] with OneDate   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ], In1_15[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ]] = ???
  val txT_       : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In1_15[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val txAdded_   : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean] with OneBoolean[Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean], In1_15[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean]] = ???

  def apply(v: min)     : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: max)     : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: rand)    : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: sample)  : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???

  def apply(v: mins)    : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Seq[O]] = ???
  def apply(v: maxs)    : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Seq[O]] = ???
  def apply(v: distinct): Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Seq[O]] = ???
  def apply(v: rands)   : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Seq[O]] = ???
  def apply(v: samples) : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Seq[O]] = ???

  def apply(v: count)        : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Int] = ???
  def apply(v: countDistinct): Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Int] = ???
  def apply(v: sum)          : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: avg)          : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: median)       : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: variance)     : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: stddev)       : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]                                                 = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a]                                              = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_17[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_18[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_19[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_20[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_21[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): Molecule_22[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f, g] = ???
}


trait Molecule_16[Ns16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {

  val e         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In1_17[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val a         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In1_17[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val v         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ] with OneAny    [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ], In1_17[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ]] = ???
  val ns        : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In1_17[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val txInstant : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ] with OneDate   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ], In1_17[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ]] = ???
  val txT       : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In1_17[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val txAdded   : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean] with OneBoolean[Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean], In1_17[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean]] = ???

  val e_         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In1_16[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val a_         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In1_16[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val v_         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ] with OneAny    [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ], In1_16[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ]] = ???
  val ns_        : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In1_16[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val txInstant_ : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ] with OneDate   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ], In1_16[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ]] = ???
  val txT_       : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In1_16[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val txAdded_   : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean] with OneBoolean[Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean], In1_16[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean]] = ???

  def apply(v: min)     : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: max)     : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: rand)    : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: sample)  : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???

  def apply(v: mins)    : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Seq[P]] = ???
  def apply(v: maxs)    : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Seq[P]] = ???
  def apply(v: distinct): Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Seq[P]] = ???
  def apply(v: rands)   : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Seq[P]] = ???
  def apply(v: samples) : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Seq[P]] = ???

  def apply(v: count)        : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Int] = ???
  def apply(v: countDistinct): Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Int] = ???
  def apply(v: sum)          : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: avg)          : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: median)       : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: variance)     : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: stddev)       : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]                                    = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a]                                 = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_18[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_19[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_20[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d]       = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_21[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e]    = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : Molecule_22[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e, f] = ???
}


trait Molecule_17[Ns17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {

  val e         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In1_18[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val a         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In1_18[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val v         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ] with OneAny    [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ], In1_18[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ]] = ???
  val ns        : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In1_18[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val txInstant : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ] with OneDate   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ], In1_18[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ]] = ???
  val txT       : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In1_18[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val txAdded   : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean] with OneBoolean[Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean], In1_18[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean]] = ???

  val e_         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In1_17[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val a_         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In1_17[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val v_         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ] with OneAny    [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ], In1_17[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ]] = ???
  val ns_        : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In1_17[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val txInstant_ : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ] with OneDate   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ], In1_17[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ]] = ???
  val txT_       : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In1_17[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val txAdded_   : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean] with OneBoolean[Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean], In1_17[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean]] = ???

  def apply(v: min)     : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: max)     : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: rand)    : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: sample)  : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???

  def apply(v: mins)    : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Seq[Q]] = ???
  def apply(v: maxs)    : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Seq[Q]] = ???
  def apply(v: distinct): Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Seq[Q]] = ???
  def apply(v: rands)   : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Seq[Q]] = ???
  def apply(v: samples) : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Seq[Q]] = ???

  def apply(v: count)        : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Int] = ???
  def apply(v: countDistinct): Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Int] = ???
  def apply(v: sum)          : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: avg)          : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: median)       : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: variance)     : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: stddev)       : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_19[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_20[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_21[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d]       = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : Molecule_22[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d, e]    = ???
}


trait Molecule_18[Ns18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {

  val e         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In1_19[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val a         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In1_19[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val v         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ] with OneAny    [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ], In1_19[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ]] = ???
  val ns        : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In1_19[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val txInstant : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ] with OneDate   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ], In1_19[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ]] = ???
  val txT       : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In1_19[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val txAdded   : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean] with OneBoolean[Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean], In1_19[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean]] = ???

  val e_         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In1_18[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val a_         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In1_18[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val v_         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ] with OneAny    [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ], In1_18[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ]] = ???
  val ns_        : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In1_18[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val txInstant_ : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ] with OneDate   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ], In1_18[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ]] = ???
  val txT_       : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In1_18[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val txAdded_   : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean] with OneBoolean[Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean], In1_18[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean]] = ???

  def apply(v: min)     : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: max)     : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: rand)    : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: sample)  : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???

  def apply(v: mins)    : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Seq[R]] = ???
  def apply(v: maxs)    : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Seq[R]] = ???
  def apply(v: distinct): Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Seq[R]] = ???
  def apply(v: rands)   : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Seq[R]] = ???
  def apply(v: samples) : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Seq[R]] = ???

  def apply(v: count)        : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Int] = ???
  def apply(v: countDistinct): Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Int] = ???
  def apply(v: sum)          : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: avg)          : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: median)       : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: variance)     : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: stddev)       : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_20[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_21[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : Molecule_22[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c, d]       = ???
}


trait Molecule_19[Ns19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {

  val e         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In1_20[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val a         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In1_20[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val v         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ] with OneAny    [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ], In1_20[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ]] = ???
  val ns        : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In1_20[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val txInstant : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ] with OneDate   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ], In1_20[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ]] = ???
  val txT       : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In1_20[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val txAdded   : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean] with OneBoolean[Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean], In1_20[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean]] = ???

  val e_         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In1_19[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val a_         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In1_19[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val v_         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ] with OneAny    [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ], In1_19[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ]] = ???
  val ns_        : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In1_19[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val txInstant_ : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ] with OneDate   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ], In1_19[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ]] = ???
  val txT_       : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In1_19[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val txAdded_   : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean] with OneBoolean[Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean], In1_19[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean]] = ???

  def apply(v: min)     : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: max)     : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: rand)    : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: sample)  : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???

  def apply(v: mins)    : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Seq[S]] = ???
  def apply(v: maxs)    : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Seq[S]] = ???
  def apply(v: distinct): Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Seq[S]] = ???
  def apply(v: rands)   : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Seq[S]] = ???
  def apply(v: samples) : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Seq[S]] = ???

  def apply(v: count)        : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Int] = ???
  def apply(v: countDistinct): Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Int] = ???
  def apply(v: sum)          : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: avg)          : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: median)       : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: variance)     : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: stddev)       : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_21[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : Molecule_22[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b, c]          = ???
}


trait Molecule_20[Ns20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {

  val e         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In1_21[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val a         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In1_21[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val v         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ] with OneAny    [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ], In1_21[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ]] = ???
  val ns        : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In1_21[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val txInstant : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ] with OneDate   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ], In1_21[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ]] = ???
  val txT       : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In1_21[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val txAdded   : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean] with OneBoolean[Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean], In1_21[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean]] = ???

  val e_         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In1_20[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val a_         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In1_20[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val v_         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ] with OneAny    [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ], In1_20[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ]] = ???
  val ns_        : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In1_20[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val txInstant_ : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ] with OneDate   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ], In1_20[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ]] = ???
  val txT_       : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In1_20[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val txAdded_   : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean] with OneBoolean[Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean], In1_20[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean]] = ???

  def apply(v: min)     : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: max)     : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: rand)    : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: sample)  : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???

  def apply(v: mins)    : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Seq[T]] = ???
  def apply(v: maxs)    : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Seq[T]] = ???
  def apply(v: distinct): Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Seq[T]] = ???
  def apply(v: rands)   : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Seq[T]] = ???
  def apply(v: samples) : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Seq[T]] = ???

  def apply(v: count)        : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Int] = ???
  def apply(v: countDistinct): Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Int] = ???
  def apply(v: sum)          : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: avg)          : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: median)       : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: variance)     : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: stddev)       : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : Molecule_22[Any, Any, Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a, b]             = ???
}


trait Molecule_21[Ns21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {

  val e         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], In1_22[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ]] = ???
  val a         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], In1_22[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ]] = ???
  val v         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], In1_22[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ]] = ???
  val ns        : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], In1_22[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ]] = ???
  val txInstant : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], In1_22[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ]] = ???
  val txT       : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], In1_22[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ]] = ???
  val txAdded   : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], In1_22[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean]] = ???

  val e_         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In1_21[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val a_         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In1_21[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val v_         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ] with OneAny    [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ], In1_21[Any    , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ]] = ???
  val ns_        : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In1_21[String , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val txInstant_ : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ] with OneDate   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ], In1_21[Date   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ]] = ???
  val txT_       : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In1_21[Long   , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val txAdded_   : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean] with OneBoolean[Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean], In1_21[Boolean, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean]] = ???

  def apply(v: min)     : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: max)     : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: rand)    : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: sample)  : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???

  def apply(v: mins)    : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Seq[U]] = ???
  def apply(v: maxs)    : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Seq[U]] = ???
  def apply(v: distinct): Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Seq[U]] = ???
  def apply(v: rands)   : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Seq[U]] = ???
  def apply(v: samples) : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Seq[U]] = ???

  def apply(v: count)        : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Int] = ???
  def apply(v: countDistinct): Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Int] = ???
  def apply(v: sum)          : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: avg)          : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: median)       : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: variance)     : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: stddev)       : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, a]                                           = ???
}


trait Molecule_22[Ns22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], P23[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], P24[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] {

  val e_         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val a_         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val v_         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], Nothing] = ???
  val ns_        : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val txInstant_ : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], Nothing] = ???
  val txT_       : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val txAdded_   : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], Nothing] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]                                              = ???
}

