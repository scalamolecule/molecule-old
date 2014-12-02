package molecule
package in
import molecule.out._
import java.util.Date
import molecule.dsl.schemaDSL.NS
import molecule.dsl.schemaDSL._
import scala.language.higherKinds


trait In_1_0[In1_0[_], In1_1[_,_], In2_0[_,_], In2_1[_,_,_], I1] extends NS {
  val e          : In1_1[I1, Long   ] with OneLong   [In1_1[I1, Long   ], In2_1[Long   , I1, Long   ]] = ???
  val a          : In1_1[I1, String ] with OneString [In1_1[I1, String ], In2_1[String , I1, String ]] = ???
  val v          : In1_1[I1, Any    ] with OneAny    [In1_1[I1, Any    ], In2_1[Any    , I1, Any    ]] = ???
  val ns         : In1_1[I1, String ] with OneString [In1_1[I1, String ], In2_1[String , I1, String ]] = ???
  val txInstant  : In1_1[I1, Date   ] with OneDate   [In1_1[I1, Date   ], In2_1[Date   , I1, Date   ]] = ???
  val txT        : In1_1[I1, Long   ] with OneLong   [In1_1[I1, Long   ], In2_1[Long   , I1, Long   ]] = ???
  val txAdded    : In1_1[I1, Boolean] with OneBoolean[In1_1[I1, Boolean], In2_1[Boolean, I1, Boolean]] = ???

  val e_         : In1_0[I1] with OneLong   [In1_0[I1], In2_0[Long   , Long   ]] = ???
  val a_         : In1_0[I1] with OneString [In1_0[I1], In2_0[String , String ]] = ???
  val v_         : In1_0[I1] with OneAny    [In1_0[I1], In2_0[Any    , Any    ]] = ???
  val ns_        : In1_0[I1] with OneString [In1_0[I1], In2_0[String , String ]] = ???
  val txInstant_ : In1_0[I1] with OneDate   [In1_0[I1], In2_0[Date   , Date   ]] = ???
  val txT_       : In1_0[I1] with OneLong   [In1_0[I1], In2_0[Long   , Long   ]] = ???
  val txAdded_   : In1_0[I1] with OneBoolean[In1_0[I1], In2_0[Boolean, Boolean]] = ???

  // If we supply 2 or more tx attributes we return a generic molecule
  // This means that you can't continue expanding the molecule from the initial namespace anymore, so you'll
  // want to have the tx data defined in the end of the molecule
  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_0[I1]                                                 = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_1[I1, a]                                              = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_2[Any, Any, Any, Any, I1, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_3[Any, Any, Any, Any, I1, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_4[Any, Any, Any, Any, I1, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_5[Any, Any, Any, Any, I1, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_6[Any, Any, Any, Any, I1, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_7[Any, Any, Any, Any, I1, a, b, c, d, e, f, g] = ???
}

trait In_1_1[In1_1[_,_], In1_2[_,_,_], In2_1[_,_,_], In2_2[_,_,_,_], I1, A] extends NS {
  val e         : In1_2[I1, A, Long   ] with OneLong   [In1_2[I1, A, Long   ], In2_2[Long   , I1, A, Long   ]] = ???
  val a         : In1_2[I1, A, String ] with OneString [In1_2[I1, A, String ], In2_2[String , I1, A, String ]] = ???
  val v         : In1_2[I1, A, Any    ] with OneAny    [In1_2[I1, A, Any    ], In2_2[Any    , I1, A, Any    ]] = ???
  val ns        : In1_2[I1, A, String ] with OneString [In1_2[I1, A, String ], In2_2[String , I1, A, String ]] = ???
  val txInstant : In1_2[I1, A, Date   ] with OneDate   [In1_2[I1, A, Date   ], In2_2[Date   , I1, A, Date   ]] = ???
  val txT       : In1_2[I1, A, Long   ] with OneLong   [In1_2[I1, A, Long   ], In2_2[Long   , I1, A, Long   ]] = ???
  val txAdded   : In1_2[I1, A, Boolean] with OneBoolean[In1_2[I1, A, Boolean], In2_2[Boolean, I1, A, Boolean]] = ???

  val e_          : In1_1[I1, Long   ] with OneLong   [In1_1[I1, Long   ], In2_1[Long   , I1, Long   ]] = ???
  val a_          : In1_1[I1, String ] with OneString [In1_1[I1, String ], In2_1[String , I1, String ]] = ???
  val v_          : In1_1[I1, Any    ] with OneAny    [In1_1[I1, Any    ], In2_1[Any    , I1, Any    ]] = ???
  val ns_         : In1_1[I1, String ] with OneString [In1_1[I1, String ], In2_1[String , I1, String ]] = ???
  val txInstant_  : In1_1[I1, Date   ] with OneDate   [In1_1[I1, Date   ], In2_1[Date   , I1, Date   ]] = ???
  val txT_        : In1_1[I1, Long   ] with OneLong   [In1_1[I1, Long   ], In2_1[Long   , I1, Long   ]] = ???
  val txAdded_    : In1_1[I1, Boolean] with OneBoolean[In1_1[I1, Boolean], In2_1[Boolean, I1, Boolean]] = ???

  def apply(v: min)     : In1_1[I1, A] = ???
  def apply(v: max)     : In1_1[I1, A] = ???
  def apply(v: rand)    : In1_1[I1, A] = ???
  def apply(v: sample)  : In1_1[I1, A] = ???

  def apply(v: mins)    : In1_1[I1, Vector[A]] = ???
  def apply(v: maxs)    : In1_1[I1, Vector[A]] = ???
  def apply(v: distinct): In1_1[I1, Vector[A]] = ???
  def apply(v: rands)   : In1_1[I1, Stream[A]] = ???
  def apply(v: samples) : In1_1[I1, Vector[A]] = ???

  def apply(v: count)        : In1_1[I1, Long] = ???
  def apply(v: countDistinct): In1_1[I1, Long] = ???
  def apply(v: sum)          : In1_1[I1, Double] = ???
  def apply(v: avg)          : In1_1[I1, Double] = ???
  def apply(v: median)       : In1_1[I1, Double] = ???
  def apply(v: variance)     : In1_1[I1, Double] = ???
  def apply(v: stddev)       : In1_1[I1, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_1[I1, A]                                                 = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_2[I1, A, a]                                              = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_3[Any, Any, Any, Any, I1, A, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_4[Any, Any, Any, Any, I1, A, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_5[Any, Any, Any, Any, I1, A, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_6[Any, Any, Any, Any, I1, A, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_7[Any, Any, Any, Any, I1, A, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_8[Any, Any, Any, Any, I1, A, a, b, c, d, e, f, g] = ???
}

trait In_1_2[In1_2[_,_,_], In1_3[_,_,_,_], In2_2[_,_,_,_], In2_3[_,_,_,_,_], I1, A, B] extends NS {
  val e         : In1_3[I1, A, B, Long   ] with OneLong   [In1_3[I1, A, B, Long   ], In2_3[Long   , I1, A, B, Long   ]] = ???
  val a         : In1_3[I1, A, B, String ] with OneString [In1_3[I1, A, B, String ], In2_3[String , I1, A, B, String ]] = ???
  val v         : In1_3[I1, A, B, Any    ] with OneAny    [In1_3[I1, A, B, Any    ], In2_3[Any    , I1, A, B, Any    ]] = ???
  val ns        : In1_3[I1, A, B, String ] with OneString [In1_3[I1, A, B, String ], In2_3[String , I1, A, B, String ]] = ???
  val txInstant : In1_3[I1, A, B, Date   ] with OneDate   [In1_3[I1, A, B, Date   ], In2_3[Date   , I1, A, B, Date   ]] = ???
  val txT       : In1_3[I1, A, B, Long   ] with OneLong   [In1_3[I1, A, B, Long   ], In2_3[Long   , I1, A, B, Long   ]] = ???
  val txAdded   : In1_3[I1, A, B, Boolean] with OneBoolean[In1_3[I1, A, B, Boolean], In2_3[Boolean, I1, A, B, Boolean]] = ???

  val e_         : In1_2[I1, A, Long   ] with OneLong   [In1_2[I1, A, Long   ], In2_2[Long   , I1, A, Long   ]] = ???
  val a_         : In1_2[I1, A, String ] with OneString [In1_2[I1, A, String ], In2_2[String , I1, A, String ]] = ???
  val v_         : In1_2[I1, A, Any    ] with OneAny    [In1_2[I1, A, Any    ], In2_2[Any    , I1, A, Any    ]] = ???
  val ns_        : In1_2[I1, A, String ] with OneString [In1_2[I1, A, String ], In2_2[String , I1, A, String ]] = ???
  val txInstant_ : In1_2[I1, A, Date   ] with OneDate   [In1_2[I1, A, Date   ], In2_2[Date   , I1, A, Date   ]] = ???
  val txT_       : In1_2[I1, A, Long   ] with OneLong   [In1_2[I1, A, Long   ], In2_2[Long   , I1, A, Long   ]] = ???
  val txAdded_   : In1_2[I1, A, Boolean] with OneBoolean[In1_2[I1, A, Boolean], In2_2[Boolean, I1, A, Boolean]] = ???

  def apply(v: min)     : In1_2[I1, A, B] = ???
  def apply(v: max)     : In1_2[I1, A, B] = ???
  def apply(v: rand)    : In1_2[I1, A, B] = ???
  def apply(v: sample)  : In1_2[I1, A, B] = ???

  def apply(v: mins)    : In1_2[I1, A, Vector[B]] = ???
  def apply(v: maxs)    : In1_2[I1, A, Vector[B]] = ???
  def apply(v: distinct): In1_2[I1, A, Vector[B]] = ???
  def apply(v: rands)   : In1_2[I1, A, Stream[B]] = ???
  def apply(v: samples) : In1_2[I1, A, Vector[B]] = ???

  def apply(v: count)        : In1_2[I1, A, Long] = ???
  def apply(v: countDistinct): In1_2[I1, A, Long] = ???
  def apply(v: sum)          : In1_2[I1, A, Double] = ???
  def apply(v: avg)          : In1_2[I1, A, Double] = ???
  def apply(v: median)       : In1_2[I1, A, Double] = ???
  def apply(v: variance)     : In1_2[I1, A, Double] = ???
  def apply(v: stddev)       : In1_2[I1, A, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_2[I1, A, B]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_3[I1, A, B, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_4[Any, Any, Any, Any, I1, A, B, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_5[Any, Any, Any, Any, I1, A, B, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_6[Any, Any, Any, Any, I1, A, B, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_7[Any, Any, Any, Any, I1, A, B, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_8[Any, Any, Any, Any, I1, A, B, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_9[Any, Any, Any, Any, I1, A, B, a, b, c, d, e, f, g] = ???
}

trait In_1_3[In1_3[_,_,_,_], In1_4[_,_,_,_,_], In2_3[_,_,_,_,_], In2_4[_,_,_,_,_,_], I1, A, B, C] extends NS {
  val e         : In1_4[I1, A, B, C, Long   ] with OneLong   [In1_4[I1, A, B, C, Long   ], In2_4[Long   , I1, A, B, C, Long   ]] = ???
  val a         : In1_4[I1, A, B, C, String ] with OneString [In1_4[I1, A, B, C, String ], In2_4[String , I1, A, B, C, String ]] = ???
  val v         : In1_4[I1, A, B, C, Any    ] with OneAny    [In1_4[I1, A, B, C, Any    ], In2_4[Any    , I1, A, B, C, Any    ]] = ???
  val ns        : In1_4[I1, A, B, C, String ] with OneString [In1_4[I1, A, B, C, String ], In2_4[String , I1, A, B, C, String ]] = ???
  val txInstant : In1_4[I1, A, B, C, Date   ] with OneDate   [In1_4[I1, A, B, C, Date   ], In2_4[Date   , I1, A, B, C, Date   ]] = ???
  val txT       : In1_4[I1, A, B, C, Long   ] with OneLong   [In1_4[I1, A, B, C, Long   ], In2_4[Long   , I1, A, B, C, Long   ]] = ???
  val txAdded   : In1_4[I1, A, B, C, Boolean] with OneBoolean[In1_4[I1, A, B, C, Boolean], In2_4[Boolean, I1, A, B, C, Boolean]] = ???

  val e_         : In1_3[I1, A, B, Long   ] with OneLong   [In1_3[I1, A, B, Long   ], In2_3[Long   , I1, A, B, Long   ]] = ???
  val a_         : In1_3[I1, A, B, String ] with OneString [In1_3[I1, A, B, String ], In2_3[String , I1, A, B, String ]] = ???
  val v_         : In1_3[I1, A, B, Any    ] with OneAny    [In1_3[I1, A, B, Any    ], In2_3[Any    , I1, A, B, Any    ]] = ???
  val ns_        : In1_3[I1, A, B, String ] with OneString [In1_3[I1, A, B, String ], In2_3[String , I1, A, B, String ]] = ???
  val txInstant_ : In1_3[I1, A, B, Date   ] with OneDate   [In1_3[I1, A, B, Date   ], In2_3[Date   , I1, A, B, Date   ]] = ???
  val txT_       : In1_3[I1, A, B, Long   ] with OneLong   [In1_3[I1, A, B, Long   ], In2_3[Long   , I1, A, B, Long   ]] = ???
  val txAdded_   : In1_3[I1, A, B, Boolean] with OneBoolean[In1_3[I1, A, B, Boolean], In2_3[Boolean, I1, A, B, Boolean]] = ???

  def apply(v: min)     : In1_3[I1, A, B, C] = ???
  def apply(v: max)     : In1_3[I1, A, B, C] = ???
  def apply(v: rand)    : In1_3[I1, A, B, C] = ???
  def apply(v: sample)  : In1_3[I1, A, B, C] = ???

  def apply(v: mins)    : In1_3[I1, A, B, Vector[C]] = ???
  def apply(v: maxs)    : In1_3[I1, A, B, Vector[C]] = ???
  def apply(v: distinct): In1_3[I1, A, B, Vector[C]] = ???
  def apply(v: rands)   : In1_3[I1, A, B, Stream[C]] = ???
  def apply(v: samples) : In1_3[I1, A, B, Vector[C]] = ???

  def apply(v: count)        : In1_3[I1, A, B, Long] = ???
  def apply(v: countDistinct): In1_3[I1, A, B, Long] = ???
  def apply(v: sum)          : In1_3[I1, A, B, Double] = ???
  def apply(v: avg)          : In1_3[I1, A, B, Double] = ???
  def apply(v: median)       : In1_3[I1, A, B, Double] = ???
  def apply(v: variance)     : In1_3[I1, A, B, Double] = ???
  def apply(v: stddev)       : In1_3[I1, A, B, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_3 [I1, A, B, C]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_4 [I1, A, B, C, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_5 [Any, Any, Any, Any, I1, A, B, C, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_6 [Any, Any, Any, Any, I1, A, B, C, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_7 [Any, Any, Any, Any, I1, A, B, C, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_8 [Any, Any, Any, Any, I1, A, B, C, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_9 [Any, Any, Any, Any, I1, A, B, C, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_10[Any, Any, Any, Any, I1, A, B, C, a, b, c, d, e, f, g] = ???
}

trait In_1_4[In1_4[_,_,_,_,_], In1_5[_,_,_,_,_,_], In2_4[_,_,_,_,_,_], In2_5[_,_,_,_,_,_,_], I1, A, B, C, D] extends NS {
  val e         : In1_5[I1, A, B, C, D, Long   ] with OneLong   [In1_5[I1, A, B, C, D, Long   ], In2_5[Long   , I1, A, B, C, D, Long   ]] = ???
  val a         : In1_5[I1, A, B, C, D, String ] with OneString [In1_5[I1, A, B, C, D, String ], In2_5[String , I1, A, B, C, D, String ]] = ???
  val v         : In1_5[I1, A, B, C, D, Any    ] with OneAny    [In1_5[I1, A, B, C, D, Any    ], In2_5[Any    , I1, A, B, C, D, Any    ]] = ???
  val ns        : In1_5[I1, A, B, C, D, String ] with OneString [In1_5[I1, A, B, C, D, String ], In2_5[String , I1, A, B, C, D, String ]] = ???
  val txInstant : In1_5[I1, A, B, C, D, Date   ] with OneDate   [In1_5[I1, A, B, C, D, Date   ], In2_5[Date   , I1, A, B, C, D, Date   ]] = ???
  val txT       : In1_5[I1, A, B, C, D, Long   ] with OneLong   [In1_5[I1, A, B, C, D, Long   ], In2_5[Long   , I1, A, B, C, D, Long   ]] = ???
  val txAdded   : In1_5[I1, A, B, C, D, Boolean] with OneBoolean[In1_5[I1, A, B, C, D, Boolean], In2_5[Boolean, I1, A, B, C, D, Boolean]] = ???

  val e_         : In1_4[I1, A, B, C, Long   ] with OneLong   [In1_4[I1, A, B, C, Long   ], In2_4[Long   , I1, A, B, C, Long   ]] = ???
  val a_         : In1_4[I1, A, B, C, String ] with OneString [In1_4[I1, A, B, C, String ], In2_4[String , I1, A, B, C, String ]] = ???
  val v_         : In1_4[I1, A, B, C, Any    ] with OneAny    [In1_4[I1, A, B, C, Any    ], In2_4[Any    , I1, A, B, C, Any    ]] = ???
  val ns_        : In1_4[I1, A, B, C, String ] with OneString [In1_4[I1, A, B, C, String ], In2_4[String , I1, A, B, C, String ]] = ???
  val txInstant_ : In1_4[I1, A, B, C, Date   ] with OneDate   [In1_4[I1, A, B, C, Date   ], In2_4[Date   , I1, A, B, C, Date   ]] = ???
  val txT_       : In1_4[I1, A, B, C, Long   ] with OneLong   [In1_4[I1, A, B, C, Long   ], In2_4[Long   , I1, A, B, C, Long   ]] = ???
  val txAdded_   : In1_4[I1, A, B, C, Boolean] with OneBoolean[In1_4[I1, A, B, C, Boolean], In2_4[Boolean, I1, A, B, C, Boolean]] = ???

  def apply(v: min)     : In1_4[I1, A, B, C, D] = ???
  def apply(v: max)     : In1_4[I1, A, B, C, D] = ???
  def apply(v: rand)    : In1_4[I1, A, B, C, D] = ???
  def apply(v: sample)  : In1_4[I1, A, B, C, D] = ???

  def apply(v: mins)    : In1_4[I1, A, B, C, Vector[D]] = ???
  def apply(v: maxs)    : In1_4[I1, A, B, C, Vector[D]] = ???
  def apply(v: distinct): In1_4[I1, A, B, C, Vector[D]] = ???
  def apply(v: rands)   : In1_4[I1, A, B, C, Stream[D]] = ???
  def apply(v: samples) : In1_4[I1, A, B, C, Vector[D]] = ???

  def apply(v: count)        : In1_4[I1, A, B, C, Long] = ???
  def apply(v: countDistinct): In1_4[I1, A, B, C, Long] = ???
  def apply(v: sum)          : In1_4[I1, A, B, C, Double] = ???
  def apply(v: avg)          : In1_4[I1, A, B, C, Double] = ???
  def apply(v: median)       : In1_4[I1, A, B, C, Double] = ???
  def apply(v: variance)     : In1_4[I1, A, B, C, Double] = ???
  def apply(v: stddev)       : In1_4[I1, A, B, C, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_4 [I1, A, B, C, D]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_5 [I1, A, B, C, D, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_6 [Any, Any, Any, Any, I1, A, B, C, D, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_7 [Any, Any, Any, Any, I1, A, B, C, D, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_8 [Any, Any, Any, Any, I1, A, B, C, D, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_9 [Any, Any, Any, Any, I1, A, B, C, D, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_10[Any, Any, Any, Any, I1, A, B, C, D, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_11[Any, Any, Any, Any, I1, A, B, C, D, a, b, c, d, e, f, g] = ???
}

trait In_1_5[In1_5[_,_,_,_,_,_], In1_6[_,_,_,_,_,_,_], In2_5[_,_,_,_,_,_,_], In2_6[_,_,_,_,_,_,_,_], I1, A, B, C, D, E] extends NS {
  val e         : In1_6[I1, A, B, C, D, E, Long   ] with OneLong   [In1_6[I1, A, B, C, D, E, Long   ], In2_6[Long   , I1, A, B, C, D, E, Long   ]] = ???
  val a         : In1_6[I1, A, B, C, D, E, String ] with OneString [In1_6[I1, A, B, C, D, E, String ], In2_6[String , I1, A, B, C, D, E, String ]] = ???
  val v         : In1_6[I1, A, B, C, D, E, Any    ] with OneAny    [In1_6[I1, A, B, C, D, E, Any    ], In2_6[Any    , I1, A, B, C, D, E, Any    ]] = ???
  val ns        : In1_6[I1, A, B, C, D, E, String ] with OneString [In1_6[I1, A, B, C, D, E, String ], In2_6[String , I1, A, B, C, D, E, String ]] = ???
  val txInstant : In1_6[I1, A, B, C, D, E, Date   ] with OneDate   [In1_6[I1, A, B, C, D, E, Date   ], In2_6[Date   , I1, A, B, C, D, E, Date   ]] = ???
  val txT       : In1_6[I1, A, B, C, D, E, Long   ] with OneLong   [In1_6[I1, A, B, C, D, E, Long   ], In2_6[Long   , I1, A, B, C, D, E, Long   ]] = ???
  val txAdded   : In1_6[I1, A, B, C, D, E, Boolean] with OneBoolean[In1_6[I1, A, B, C, D, E, Boolean], In2_6[Boolean, I1, A, B, C, D, E, Boolean]] = ???

  val e_         : In1_5[I1, A, B, C, D, Long   ] with OneLong   [In1_5[I1, A, B, C, D, Long   ], In2_5[Long   , I1, A, B, C, D, Long   ]] = ???
  val a_         : In1_5[I1, A, B, C, D, String ] with OneString [In1_5[I1, A, B, C, D, String ], In2_5[String , I1, A, B, C, D, String ]] = ???
  val v_         : In1_5[I1, A, B, C, D, Any    ] with OneAny    [In1_5[I1, A, B, C, D, Any    ], In2_5[Any    , I1, A, B, C, D, Any    ]] = ???
  val ns_        : In1_5[I1, A, B, C, D, String ] with OneString [In1_5[I1, A, B, C, D, String ], In2_5[String , I1, A, B, C, D, String ]] = ???
  val txInstant_ : In1_5[I1, A, B, C, D, Date   ] with OneDate   [In1_5[I1, A, B, C, D, Date   ], In2_5[Date   , I1, A, B, C, D, Date   ]] = ???
  val txT_       : In1_5[I1, A, B, C, D, Long   ] with OneLong   [In1_5[I1, A, B, C, D, Long   ], In2_5[Long   , I1, A, B, C, D, Long   ]] = ???
  val txAdded_   : In1_5[I1, A, B, C, D, Boolean] with OneBoolean[In1_5[I1, A, B, C, D, Boolean], In2_5[Boolean, I1, A, B, C, D, Boolean]] = ???

  def apply(v: min)     : In1_5[I1, A, B, C, D, E] = ???
  def apply(v: max)     : In1_5[I1, A, B, C, D, E] = ???
  def apply(v: rand)    : In1_5[I1, A, B, C, D, E] = ???
  def apply(v: sample)  : In1_5[I1, A, B, C, D, E] = ???

  def apply(v: mins)    : In1_5[I1, A, B, C, D, Vector[E]] = ???
  def apply(v: maxs)    : In1_5[I1, A, B, C, D, Vector[E]] = ???
  def apply(v: distinct): In1_5[I1, A, B, C, D, Vector[E]] = ???
  def apply(v: rands)   : In1_5[I1, A, B, C, D, Stream[E]] = ???
  def apply(v: samples) : In1_5[I1, A, B, C, D, Vector[E]] = ???

  def apply(v: count)        : In1_5[I1, A, B, C, D, Long] = ???
  def apply(v: countDistinct): In1_5[I1, A, B, C, D, Long] = ???
  def apply(v: sum)          : In1_5[I1, A, B, C, D, Double] = ???
  def apply(v: avg)          : In1_5[I1, A, B, C, D, Double] = ???
  def apply(v: median)       : In1_5[I1, A, B, C, D, Double] = ???
  def apply(v: variance)     : In1_5[I1, A, B, C, D, Double] = ???
  def apply(v: stddev)       : In1_5[I1, A, B, C, D, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_5 [I1, A, B, C, D, E]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_6 [I1, A, B, C, D, E, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_7 [Any, Any, Any, Any, I1, A, B, C, D, E, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_8 [Any, Any, Any, Any, I1, A, B, C, D, E, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_9 [Any, Any, Any, Any, I1, A, B, C, D, E, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_10[Any, Any, Any, Any, I1, A, B, C, D, E, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_11[Any, Any, Any, Any, I1, A, B, C, D, E, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_12[Any, Any, Any, Any, I1, A, B, C, D, E, a, b, c, d, e, f, g] = ???
}

trait In_1_6[In1_6[_,_,_,_,_,_,_], In1_7[_,_,_,_,_,_,_,_], In2_6[_,_,_,_,_,_,_,_], In2_7[_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F] extends NS {
  val e         : In1_7[I1, A, B, C, D, E, F, Long   ] with OneLong   [In1_7[I1, A, B, C, D, E, F, Long   ], In2_7[Long   , I1, A, B, C, D, E, F, Long   ]] = ???
  val a         : In1_7[I1, A, B, C, D, E, F, String ] with OneString [In1_7[I1, A, B, C, D, E, F, String ], In2_7[String , I1, A, B, C, D, E, F, String ]] = ???
  val v         : In1_7[I1, A, B, C, D, E, F, Any    ] with OneAny    [In1_7[I1, A, B, C, D, E, F, Any    ], In2_7[Any    , I1, A, B, C, D, E, F, Any    ]] = ???
  val ns        : In1_7[I1, A, B, C, D, E, F, String ] with OneString [In1_7[I1, A, B, C, D, E, F, String ], In2_7[String , I1, A, B, C, D, E, F, String ]] = ???
  val txInstant : In1_7[I1, A, B, C, D, E, F, Date   ] with OneDate   [In1_7[I1, A, B, C, D, E, F, Date   ], In2_7[Date   , I1, A, B, C, D, E, F, Date   ]] = ???
  val txT       : In1_7[I1, A, B, C, D, E, F, Long   ] with OneLong   [In1_7[I1, A, B, C, D, E, F, Long   ], In2_7[Long   , I1, A, B, C, D, E, F, Long   ]] = ???
  val txAdded   : In1_7[I1, A, B, C, D, E, F, Boolean] with OneBoolean[In1_7[I1, A, B, C, D, E, F, Boolean], In2_7[Boolean, I1, A, B, C, D, E, F, Boolean]] = ???

  val e_         : In1_6[I1, A, B, C, D, E, Long   ] with OneLong   [In1_6[I1, A, B, C, D, E, Long   ], In2_6[Long   , I1, A, B, C, D, E, Long   ]] = ???
  val a_         : In1_6[I1, A, B, C, D, E, String ] with OneString [In1_6[I1, A, B, C, D, E, String ], In2_6[String , I1, A, B, C, D, E, String ]] = ???
  val v_         : In1_6[I1, A, B, C, D, E, Any    ] with OneAny    [In1_6[I1, A, B, C, D, E, Any    ], In2_6[Any    , I1, A, B, C, D, E, Any    ]] = ???
  val ns_        : In1_6[I1, A, B, C, D, E, String ] with OneString [In1_6[I1, A, B, C, D, E, String ], In2_6[String , I1, A, B, C, D, E, String ]] = ???
  val txInstant_ : In1_6[I1, A, B, C, D, E, Date   ] with OneDate   [In1_6[I1, A, B, C, D, E, Date   ], In2_6[Date   , I1, A, B, C, D, E, Date   ]] = ???
  val txT_       : In1_6[I1, A, B, C, D, E, Long   ] with OneLong   [In1_6[I1, A, B, C, D, E, Long   ], In2_6[Long   , I1, A, B, C, D, E, Long   ]] = ???
  val txAdded_   : In1_6[I1, A, B, C, D, E, Boolean] with OneBoolean[In1_6[I1, A, B, C, D, E, Boolean], In2_6[Boolean, I1, A, B, C, D, E, Boolean]] = ???

  def apply(v: min)     : In1_6[I1, A, B, C, D, E, F] = ???
  def apply(v: max)     : In1_6[I1, A, B, C, D, E, F] = ???
  def apply(v: rand)    : In1_6[I1, A, B, C, D, E, F] = ???
  def apply(v: sample)  : In1_6[I1, A, B, C, D, E, F] = ???

  def apply(v: mins)    : In1_6[I1, A, B, C, D, E, Vector[F]] = ???
  def apply(v: maxs)    : In1_6[I1, A, B, C, D, E, Vector[F]] = ???
  def apply(v: distinct): In1_6[I1, A, B, C, D, E, Vector[F]] = ???
  def apply(v: rands)   : In1_6[I1, A, B, C, D, E, Stream[F]] = ???
  def apply(v: samples) : In1_6[I1, A, B, C, D, E, Vector[F]] = ???

  def apply(v: count)        : In1_6[I1, A, B, C, D, E, Long] = ???
  def apply(v: countDistinct): In1_6[I1, A, B, C, D, E, Long] = ???
  def apply(v: sum)          : In1_6[I1, A, B, C, D, E, Double] = ???
  def apply(v: avg)          : In1_6[I1, A, B, C, D, E, Double] = ???
  def apply(v: median)       : In1_6[I1, A, B, C, D, E, Double] = ???
  def apply(v: variance)     : In1_6[I1, A, B, C, D, E, Double] = ???
  def apply(v: stddev)       : In1_6[I1, A, B, C, D, E, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_6 [I1, A, B, C, D, E, F]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_7 [I1, A, B, C, D, E, F, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_8 [Any, Any, Any, Any, I1, A, B, C, D, E, F, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_9 [Any, Any, Any, Any, I1, A, B, C, D, E, F, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_10[Any, Any, Any, Any, I1, A, B, C, D, E, F, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_11[Any, Any, Any, Any, I1, A, B, C, D, E, F, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_12[Any, Any, Any, Any, I1, A, B, C, D, E, F, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_13[Any, Any, Any, Any, I1, A, B, C, D, E, F, a, b, c, d, e, f, g] = ???
}

trait In_1_7[In1_7[_,_,_,_,_,_,_,_], In1_8[_,_,_,_,_,_,_,_,_], In2_7[_,_,_,_,_,_,_,_,_], In2_8[_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G] extends NS {
  val e         : In1_8[I1, A, B, C, D, E, F, G, Long   ] with OneLong   [In1_8[I1, A, B, C, D, E, F, G, Long   ], In2_8[Long   , I1, A, B, C, D, E, F, G, Long   ]] = ???
  val a         : In1_8[I1, A, B, C, D, E, F, G, String ] with OneString [In1_8[I1, A, B, C, D, E, F, G, String ], In2_8[String , I1, A, B, C, D, E, F, G, String ]] = ???
  val v         : In1_8[I1, A, B, C, D, E, F, G, Any    ] with OneAny    [In1_8[I1, A, B, C, D, E, F, G, Any    ], In2_8[Any    , I1, A, B, C, D, E, F, G, Any    ]] = ???
  val ns        : In1_8[I1, A, B, C, D, E, F, G, String ] with OneString [In1_8[I1, A, B, C, D, E, F, G, String ], In2_8[String , I1, A, B, C, D, E, F, G, String ]] = ???
  val txInstant : In1_8[I1, A, B, C, D, E, F, G, Date   ] with OneDate   [In1_8[I1, A, B, C, D, E, F, G, Date   ], In2_8[Date   , I1, A, B, C, D, E, F, G, Date   ]] = ???
  val txT       : In1_8[I1, A, B, C, D, E, F, G, Long   ] with OneLong   [In1_8[I1, A, B, C, D, E, F, G, Long   ], In2_8[Long   , I1, A, B, C, D, E, F, G, Long   ]] = ???
  val txAdded   : In1_8[I1, A, B, C, D, E, F, G, Boolean] with OneBoolean[In1_8[I1, A, B, C, D, E, F, G, Boolean], In2_8[Boolean, I1, A, B, C, D, E, F, G, Boolean]] = ???

  val e_         : In1_7[I1, A, B, C, D, E, F, Long   ] with OneLong   [In1_7[I1, A, B, C, D, E, F, Long   ], In2_7[Long   , I1, A, B, C, D, E, F, Long   ]] = ???
  val a_         : In1_7[I1, A, B, C, D, E, F, String ] with OneString [In1_7[I1, A, B, C, D, E, F, String ], In2_7[String , I1, A, B, C, D, E, F, String ]] = ???
  val v_         : In1_7[I1, A, B, C, D, E, F, Any    ] with OneAny    [In1_7[I1, A, B, C, D, E, F, Any    ], In2_7[Any    , I1, A, B, C, D, E, F, Any    ]] = ???
  val ns_        : In1_7[I1, A, B, C, D, E, F, String ] with OneString [In1_7[I1, A, B, C, D, E, F, String ], In2_7[String , I1, A, B, C, D, E, F, String ]] = ???
  val txInstant_ : In1_7[I1, A, B, C, D, E, F, Date   ] with OneDate   [In1_7[I1, A, B, C, D, E, F, Date   ], In2_7[Date   , I1, A, B, C, D, E, F, Date   ]] = ???
  val txT_       : In1_7[I1, A, B, C, D, E, F, Long   ] with OneLong   [In1_7[I1, A, B, C, D, E, F, Long   ], In2_7[Long   , I1, A, B, C, D, E, F, Long   ]] = ???
  val txAdded_   : In1_7[I1, A, B, C, D, E, F, Boolean] with OneBoolean[In1_7[I1, A, B, C, D, E, F, Boolean], In2_7[Boolean, I1, A, B, C, D, E, F, Boolean]] = ???

  def apply(v: min)     : In1_7[I1, A, B, C, D, E, F, G] = ???
  def apply(v: max)     : In1_7[I1, A, B, C, D, E, F, G] = ???
  def apply(v: rand)    : In1_7[I1, A, B, C, D, E, F, G] = ???
  def apply(v: sample)  : In1_7[I1, A, B, C, D, E, F, G] = ???

  def apply(v: mins)    : In1_7[I1, A, B, C, D, E, F, Vector[G]] = ???
  def apply(v: maxs)    : In1_7[I1, A, B, C, D, E, F, Vector[G]] = ???
  def apply(v: distinct): In1_7[I1, A, B, C, D, E, F, Vector[G]] = ???
  def apply(v: rands)   : In1_7[I1, A, B, C, D, E, F, Stream[G]] = ???
  def apply(v: samples) : In1_7[I1, A, B, C, D, E, F, Vector[G]] = ???

  def apply(v: count)        : In1_7[I1, A, B, C, D, E, F, Long] = ???
  def apply(v: countDistinct): In1_7[I1, A, B, C, D, E, F, Long] = ???
  def apply(v: sum)          : In1_7[I1, A, B, C, D, E, F, Double] = ???
  def apply(v: avg)          : In1_7[I1, A, B, C, D, E, F, Double] = ???
  def apply(v: median)       : In1_7[I1, A, B, C, D, E, F, Double] = ???
  def apply(v: variance)     : In1_7[I1, A, B, C, D, E, F, Double] = ???
  def apply(v: stddev)       : In1_7[I1, A, B, C, D, E, F, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_7 [I1, A, B, C, D, E, F, G]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_8 [I1, A, B, C, D, E, F, G, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_9 [Any, Any, Any, Any, I1, A, B, C, D, E, F, G, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_10[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_11[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_12[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_13[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_14[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, a, b, c, d, e, f, g] = ???
}

trait In_1_8[In1_8[_,_,_,_,_,_,_,_,_], In1_9[_,_,_,_,_,_,_,_,_,_], In2_8[_,_,_,_,_,_,_,_,_,_], In2_9[_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H] extends NS {
  val e         : In1_9[I1, A, B, C, D, E, F, G, H, Long   ] with OneLong   [In1_9[I1, A, B, C, D, E, F, G, H, Long   ], In2_9[Long   , I1, A, B, C, D, E, F, G, H, Long   ]] = ???
  val a         : In1_9[I1, A, B, C, D, E, F, G, H, String ] with OneString [In1_9[I1, A, B, C, D, E, F, G, H, String ], In2_9[String , I1, A, B, C, D, E, F, G, H, String ]] = ???
  val v         : In1_9[I1, A, B, C, D, E, F, G, H, Any    ] with OneAny    [In1_9[I1, A, B, C, D, E, F, G, H, Any    ], In2_9[Any    , I1, A, B, C, D, E, F, G, H, Any    ]] = ???
  val ns        : In1_9[I1, A, B, C, D, E, F, G, H, String ] with OneString [In1_9[I1, A, B, C, D, E, F, G, H, String ], In2_9[String , I1, A, B, C, D, E, F, G, H, String ]] = ???
  val txInstant : In1_9[I1, A, B, C, D, E, F, G, H, Date   ] with OneDate   [In1_9[I1, A, B, C, D, E, F, G, H, Date   ], In2_9[Date   , I1, A, B, C, D, E, F, G, H, Date   ]] = ???
  val txT       : In1_9[I1, A, B, C, D, E, F, G, H, Long   ] with OneLong   [In1_9[I1, A, B, C, D, E, F, G, H, Long   ], In2_9[Long   , I1, A, B, C, D, E, F, G, H, Long   ]] = ???
  val txAdded   : In1_9[I1, A, B, C, D, E, F, G, H, Boolean] with OneBoolean[In1_9[I1, A, B, C, D, E, F, G, H, Boolean], In2_9[Boolean, I1, A, B, C, D, E, F, G, H, Boolean]] = ???

  val e_         : In1_8[I1, A, B, C, D, E, F, G, Long   ] with OneLong   [In1_8[I1, A, B, C, D, E, F, G, Long   ], In2_8[Long   , I1, A, B, C, D, E, F, G, Long   ]] = ???
  val a_         : In1_8[I1, A, B, C, D, E, F, G, String ] with OneString [In1_8[I1, A, B, C, D, E, F, G, String ], In2_8[String , I1, A, B, C, D, E, F, G, String ]] = ???
  val v_         : In1_8[I1, A, B, C, D, E, F, G, Any    ] with OneAny    [In1_8[I1, A, B, C, D, E, F, G, Any    ], In2_8[Any    , I1, A, B, C, D, E, F, G, Any    ]] = ???
  val ns_        : In1_8[I1, A, B, C, D, E, F, G, String ] with OneString [In1_8[I1, A, B, C, D, E, F, G, String ], In2_8[String , I1, A, B, C, D, E, F, G, String ]] = ???
  val txInstant_ : In1_8[I1, A, B, C, D, E, F, G, Date   ] with OneDate   [In1_8[I1, A, B, C, D, E, F, G, Date   ], In2_8[Date   , I1, A, B, C, D, E, F, G, Date   ]] = ???
  val txT_       : In1_8[I1, A, B, C, D, E, F, G, Long   ] with OneLong   [In1_8[I1, A, B, C, D, E, F, G, Long   ], In2_8[Long   , I1, A, B, C, D, E, F, G, Long   ]] = ???
  val txAdded_   : In1_8[I1, A, B, C, D, E, F, G, Boolean] with OneBoolean[In1_8[I1, A, B, C, D, E, F, G, Boolean], In2_8[Boolean, I1, A, B, C, D, E, F, G, Boolean]] = ???

  def apply(v: min)     : In1_8[I1, A, B, C, D, E, F, G, H] = ???
  def apply(v: max)     : In1_8[I1, A, B, C, D, E, F, G, H] = ???
  def apply(v: rand)    : In1_8[I1, A, B, C, D, E, F, G, H] = ???
  def apply(v: sample)  : In1_8[I1, A, B, C, D, E, F, G, H] = ???

  def apply(v: mins)    : In1_8[I1, A, B, C, D, E, F, G, Vector[H]] = ???
  def apply(v: maxs)    : In1_8[I1, A, B, C, D, E, F, G, Vector[H]] = ???
  def apply(v: distinct): In1_8[I1, A, B, C, D, E, F, G, Vector[H]] = ???
  def apply(v: rands)   : In1_8[I1, A, B, C, D, E, F, G, Stream[H]] = ???
  def apply(v: samples) : In1_8[I1, A, B, C, D, E, F, G, Vector[H]] = ???

  def apply(v: count)        : In1_8[I1, A, B, C, D, E, F, G, Long] = ???
  def apply(v: countDistinct): In1_8[I1, A, B, C, D, E, F, G, Long] = ???
  def apply(v: sum)          : In1_8[I1, A, B, C, D, E, F, G, Double] = ???
  def apply(v: avg)          : In1_8[I1, A, B, C, D, E, F, G, Double] = ???
  def apply(v: median)       : In1_8[I1, A, B, C, D, E, F, G, Double] = ???
  def apply(v: variance)     : In1_8[I1, A, B, C, D, E, F, G, Double] = ???
  def apply(v: stddev)       : In1_8[I1, A, B, C, D, E, F, G, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_8 [I1, A, B, C, D, E, F, G, H]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_9 [I1, A, B, C, D, E, F, G, H, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_10[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_11[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_12[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_13[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_14[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_15[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, a, b, c, d, e, f, g] = ???
}

trait In_1_9[In1_9[_,_,_,_,_,_,_,_,_,_], In1_10[_,_,_,_,_,_,_,_,_,_,_], In2_9[_,_,_,_,_,_,_,_,_,_,_], In2_10[_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I] extends NS {
  val e         : In1_10[I1, A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [In1_10[I1, A, B, C, D, E, F, G, H, I, Long   ], In2_10[Long   , I1, A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val a         : In1_10[I1, A, B, C, D, E, F, G, H, I, String ] with OneString [In1_10[I1, A, B, C, D, E, F, G, H, I, String ], In2_10[String , I1, A, B, C, D, E, F, G, H, I, String ]] = ???
  val v         : In1_10[I1, A, B, C, D, E, F, G, H, I, Any    ] with OneAny    [In1_10[I1, A, B, C, D, E, F, G, H, I, Any    ], In2_10[Any    , I1, A, B, C, D, E, F, G, H, I, Any    ]] = ???
  val ns        : In1_10[I1, A, B, C, D, E, F, G, H, I, String ] with OneString [In1_10[I1, A, B, C, D, E, F, G, H, I, String ], In2_10[String , I1, A, B, C, D, E, F, G, H, I, String ]] = ???
  val txInstant : In1_10[I1, A, B, C, D, E, F, G, H, I, Date   ] with OneDate   [In1_10[I1, A, B, C, D, E, F, G, H, I, Date   ], In2_10[Date   , I1, A, B, C, D, E, F, G, H, I, Date   ]] = ???
  val txT       : In1_10[I1, A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [In1_10[I1, A, B, C, D, E, F, G, H, I, Long   ], In2_10[Long   , I1, A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val txAdded   : In1_10[I1, A, B, C, D, E, F, G, H, I, Boolean] with OneBoolean[In1_10[I1, A, B, C, D, E, F, G, H, I, Boolean], In2_10[Boolean, I1, A, B, C, D, E, F, G, H, I, Boolean]] = ???

  val e_         : In1_9[I1, A, B, C, D, E, F, G, H, Long   ] with OneLong   [In1_9[I1, A, B, C, D, E, F, G, H, Long   ], In2_9[Long   , I1, A, B, C, D, E, F, G, H, Long   ]] = ???
  val a_         : In1_9[I1, A, B, C, D, E, F, G, H, String ] with OneString [In1_9[I1, A, B, C, D, E, F, G, H, String ], In2_9[String , I1, A, B, C, D, E, F, G, H, String ]] = ???
  val v_         : In1_9[I1, A, B, C, D, E, F, G, H, Any    ] with OneAny    [In1_9[I1, A, B, C, D, E, F, G, H, Any    ], In2_9[Any    , I1, A, B, C, D, E, F, G, H, Any    ]] = ???
  val ns_        : In1_9[I1, A, B, C, D, E, F, G, H, String ] with OneString [In1_9[I1, A, B, C, D, E, F, G, H, String ], In2_9[String , I1, A, B, C, D, E, F, G, H, String ]] = ???
  val txInstant_ : In1_9[I1, A, B, C, D, E, F, G, H, Date   ] with OneDate   [In1_9[I1, A, B, C, D, E, F, G, H, Date   ], In2_9[Date   , I1, A, B, C, D, E, F, G, H, Date   ]] = ???
  val txT_       : In1_9[I1, A, B, C, D, E, F, G, H, Long   ] with OneLong   [In1_9[I1, A, B, C, D, E, F, G, H, Long   ], In2_9[Long   , I1, A, B, C, D, E, F, G, H, Long   ]] = ???
  val txAdded_   : In1_9[I1, A, B, C, D, E, F, G, H, Boolean] with OneBoolean[In1_9[I1, A, B, C, D, E, F, G, H, Boolean], In2_9[Boolean, I1, A, B, C, D, E, F, G, H, Boolean]] = ???

  def apply(v: min)     : In1_9[I1, A, B, C, D, E, F, G, H, I] = ???
  def apply(v: max)     : In1_9[I1, A, B, C, D, E, F, G, H, I] = ???
  def apply(v: rand)    : In1_9[I1, A, B, C, D, E, F, G, H, I] = ???
  def apply(v: sample)  : In1_9[I1, A, B, C, D, E, F, G, H, I] = ???

  def apply(v: mins)    : In1_9[I1, A, B, C, D, E, F, G, H, Vector[I]] = ???
  def apply(v: maxs)    : In1_9[I1, A, B, C, D, E, F, G, H, Vector[I]] = ???
  def apply(v: distinct): In1_9[I1, A, B, C, D, E, F, G, H, Vector[I]] = ???
  def apply(v: rands)   : In1_9[I1, A, B, C, D, E, F, G, H, Stream[I]] = ???
  def apply(v: samples) : In1_9[I1, A, B, C, D, E, F, G, H, Vector[I]] = ???

  def apply(v: count)        : In1_9[I1, A, B, C, D, E, F, G, H, Long] = ???
  def apply(v: countDistinct): In1_9[I1, A, B, C, D, E, F, G, H, Long] = ???
  def apply(v: sum)          : In1_9[I1, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: avg)          : In1_9[I1, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: median)       : In1_9[I1, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: variance)     : In1_9[I1, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: stddev)       : In1_9[I1, A, B, C, D, E, F, G, H, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_9 [I1, A, B, C, D, E, F, G, H, I]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_10[I1, A, B, C, D, E, F, G, H, I, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_11[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_12[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_13[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_14[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_15[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_16[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, a, b, c, d, e, f, g] = ???
}

  trait In_1_10[In1_10[_,_,_,_,_,_,_,_,_,_,_], In1_11[_,_,_,_,_,_,_,_,_,_,_,_], In2_10[_,_,_,_,_,_,_,_,_,_,_,_], In2_11[_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J] extends NS {
    val e         : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [In1_11[I1, A, B, C, D, E, F, G, H, I, J, Long   ], In2_11[Long   , I1, A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
    val a         : In1_11[I1, A, B, C, D, E, F, G, H, I, J, String ] with OneString [In1_11[I1, A, B, C, D, E, F, G, H, I, J, String ], In2_11[String , I1, A, B, C, D, E, F, G, H, I, J, String ]] = ???
    val v         : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Any    ] with OneAny    [In1_11[I1, A, B, C, D, E, F, G, H, I, J, Any    ], In2_11[Any    , I1, A, B, C, D, E, F, G, H, I, J, Any    ]] = ???
    val ns        : In1_11[I1, A, B, C, D, E, F, G, H, I, J, String ] with OneString [In1_11[I1, A, B, C, D, E, F, G, H, I, J, String ], In2_11[String , I1, A, B, C, D, E, F, G, H, I, J, String ]] = ???
    val txInstant : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Date   ] with OneDate   [In1_11[I1, A, B, C, D, E, F, G, H, I, J, Date   ], In2_11[Date   , I1, A, B, C, D, E, F, G, H, I, J, Date   ]] = ???
    val txT       : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [In1_11[I1, A, B, C, D, E, F, G, H, I, J, Long   ], In2_11[Long   , I1, A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
    val txAdded   : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Boolean] with OneBoolean[In1_11[I1, A, B, C, D, E, F, G, H, I, J, Boolean], In2_11[Boolean, I1, A, B, C, D, E, F, G, H, I, J, Boolean]] = ???

  val e_         : In1_10[I1, A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [In1_10[I1, A, B, C, D, E, F, G, H, I, Long   ], In2_10[Long   , I1, A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val a_         : In1_10[I1, A, B, C, D, E, F, G, H, I, String ] with OneString [In1_10[I1, A, B, C, D, E, F, G, H, I, String ], In2_10[String , I1, A, B, C, D, E, F, G, H, I, String ]] = ???
  val v_         : In1_10[I1, A, B, C, D, E, F, G, H, I, Any    ] with OneAny    [In1_10[I1, A, B, C, D, E, F, G, H, I, Any    ], In2_10[Any    , I1, A, B, C, D, E, F, G, H, I, Any    ]] = ???
  val ns_        : In1_10[I1, A, B, C, D, E, F, G, H, I, String ] with OneString [In1_10[I1, A, B, C, D, E, F, G, H, I, String ], In2_10[String , I1, A, B, C, D, E, F, G, H, I, String ]] = ???
  val txInstant_ : In1_10[I1, A, B, C, D, E, F, G, H, I, Date   ] with OneDate   [In1_10[I1, A, B, C, D, E, F, G, H, I, Date   ], In2_10[Date   , I1, A, B, C, D, E, F, G, H, I, Date   ]] = ???
  val txT_       : In1_10[I1, A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [In1_10[I1, A, B, C, D, E, F, G, H, I, Long   ], In2_10[Long   , I1, A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val txAdded_   : In1_10[I1, A, B, C, D, E, F, G, H, I, Boolean] with OneBoolean[In1_10[I1, A, B, C, D, E, F, G, H, I, Boolean], In2_10[Boolean, I1, A, B, C, D, E, F, G, H, I, Boolean]] = ???

    def apply(v: min)     : In1_10[I1, A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: max)     : In1_10[I1, A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: rand)    : In1_10[I1, A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: sample)  : In1_10[I1, A, B, C, D, E, F, G, H, I, J] = ???

    def apply(v: mins)    : In1_10[I1, A, B, C, D, E, F, G, H, I, Vector[J]] = ???
    def apply(v: maxs)    : In1_10[I1, A, B, C, D, E, F, G, H, I, Vector[J]] = ???
    def apply(v: distinct): In1_10[I1, A, B, C, D, E, F, G, H, I, Vector[J]] = ???
    def apply(v: rands)   : In1_10[I1, A, B, C, D, E, F, G, H, I, Stream[J]] = ???
    def apply(v: samples) : In1_10[I1, A, B, C, D, E, F, G, H, I, Vector[J]] = ???

    def apply(v: count)        : In1_10[I1, A, B, C, D, E, F, G, H, I, Long] = ???
    def apply(v: countDistinct): In1_10[I1, A, B, C, D, E, F, G, H, I, Long] = ???
    def apply(v: sum)          : In1_10[I1, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: avg)          : In1_10[I1, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: median)       : In1_10[I1, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: variance)     : In1_10[I1, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: stddev)       : In1_10[I1, A, B, C, D, E, F, G, H, I, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_10[I1, A, B, C, D, E, F, G, H, I, J]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_11[I1, A, B, C, D, E, F, G, H, I, J, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_12[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_13[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_14[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_15[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_16[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_17[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f, g] = ???
}

trait In_1_11[In1_11[_,_,_,_,_,_,_,_,_,_,_,_], In1_12[_,_,_,_,_,_,_,_,_,_,_,_,_], In2_11[_,_,_,_,_,_,_,_,_,_,_,_,_], In2_12[_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K] extends NS {
  val e         : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long   ], In2_12[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val a         : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String ], In2_12[String , I1, A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val v         : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Any    ] with OneAny    [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Any    ], In2_12[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, Any    ]] = ???
  val ns        : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String ], In2_12[String , I1, A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val txInstant : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Date   ] with OneDate   [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Date   ], In2_12[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, Date   ]] = ???
  val txT       : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long   ], In2_12[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val txAdded   : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Boolean] with OneBoolean[In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Boolean], In2_12[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, Boolean]] = ???

  val e_         : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [In1_11[I1, A, B, C, D, E, F, G, H, I, J, Long   ], In2_11[Long   , I1, A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
  val a_         : In1_11[I1, A, B, C, D, E, F, G, H, I, J, String ] with OneString [In1_11[I1, A, B, C, D, E, F, G, H, I, J, String ], In2_11[String , I1, A, B, C, D, E, F, G, H, I, J, String ]] = ???
  val v_         : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Any    ] with OneAny    [In1_11[I1, A, B, C, D, E, F, G, H, I, J, Any    ], In2_11[Any    , I1, A, B, C, D, E, F, G, H, I, J, Any    ]] = ???
  val ns_        : In1_11[I1, A, B, C, D, E, F, G, H, I, J, String ] with OneString [In1_11[I1, A, B, C, D, E, F, G, H, I, J, String ], In2_11[String , I1, A, B, C, D, E, F, G, H, I, J, String ]] = ???
  val txInstant_ : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Date   ] with OneDate   [In1_11[I1, A, B, C, D, E, F, G, H, I, J, Date   ], In2_11[Date   , I1, A, B, C, D, E, F, G, H, I, J, Date   ]] = ???
  val txT_       : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [In1_11[I1, A, B, C, D, E, F, G, H, I, J, Long   ], In2_11[Long   , I1, A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
  val txAdded_   : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Boolean] with OneBoolean[In1_11[I1, A, B, C, D, E, F, G, H, I, J, Boolean], In2_11[Boolean, I1, A, B, C, D, E, F, G, H, I, J, Boolean]] = ???

  def apply(v: min)     : In1_11[I1, A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: max)     : In1_11[I1, A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: rand)    : In1_11[I1, A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: sample)  : In1_11[I1, A, B, C, D, E, F, G, H, I, J, K] = ???

  def apply(v: mins)    : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???
  def apply(v: maxs)    : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???
  def apply(v: distinct): In1_11[I1, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???
  def apply(v: rands)   : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Stream[K]] = ???
  def apply(v: samples) : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???

  def apply(v: count)        : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Long] = ???
  def apply(v: countDistinct): In1_11[I1, A, B, C, D, E, F, G, H, I, J, Long] = ???
  def apply(v: sum)          : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: avg)          : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: median)       : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: variance)     : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: stddev)       : In1_11[I1, A, B, C, D, E, F, G, H, I, J, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_11[I1, A, B, C, D, E, F, G, H, I, J, K]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_13[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_14[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_15[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_16[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_17[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_18[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f, g] = ???
}

trait In_1_12[In1_12[_,_,_,_,_,_,_,_,_,_,_,_,_], In1_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_12[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L] extends NS {
  val e         : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In2_13[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val a         : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String ], In2_13[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val v         : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Any    ] with OneAny    [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Any    ], In2_13[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, Any    ]] = ???
  val ns        : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String ], In2_13[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val txInstant : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Date   ] with OneDate   [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Date   ], In2_13[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, Date   ]] = ???
  val txT       : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In2_13[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val txAdded   : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Boolean] with OneBoolean[In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Boolean], In2_13[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, Boolean]] = ???

  val e_         : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long   ], In2_12[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val a_         : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String ], In2_12[String , I1, A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val v_         : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Any    ] with OneAny    [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Any    ], In2_12[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, Any    ]] = ???
  val ns_        : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String ], In2_12[String , I1, A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val txInstant_ : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Date   ] with OneDate   [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Date   ], In2_12[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, Date   ]] = ???
  val txT_       : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long   ], In2_12[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val txAdded_   : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Boolean] with OneBoolean[In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Boolean], In2_12[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, Boolean]] = ???

  def apply(v: min)     : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: max)     : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: rand)    : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: sample)  : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L] = ???

  def apply(v: mins)    : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???
  def apply(v: maxs)    : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???
  def apply(v: distinct): In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???
  def apply(v: rands)   : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Stream[L]] = ???
  def apply(v: samples) : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???

  def apply(v: count)        : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long] = ???
  def apply(v: countDistinct): In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long] = ???
  def apply(v: sum)          : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: avg)          : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: median)       : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: variance)     : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: stddev)       : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Double] = ???


  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_14[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_15[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_16[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_17[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_18[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_19[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f, g] = ???
}

trait In_1_13[In1_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS {
  val e         : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In2_14[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val a         : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In2_14[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val v         : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ] with OneAny    [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ], In2_14[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ]] = ???
  val ns        : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In2_14[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val txInstant : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ] with OneDate   [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ], In2_14[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ]] = ???
  val txT       : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In2_14[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val txAdded   : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean] with OneBoolean[In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean], In2_14[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean]] = ???

  val e_         : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In2_13[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val a_         : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String ], In2_13[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val v_         : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Any    ] with OneAny    [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Any    ], In2_13[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, Any    ]] = ???
  val ns_        : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String ], In2_13[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val txInstant_ : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Date   ] with OneDate   [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Date   ], In2_13[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, Date   ]] = ???
  val txT_       : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In2_13[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val txAdded_   : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Boolean] with OneBoolean[In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Boolean], In2_13[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, Boolean]] = ???

  def apply(v: min)     : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: max)     : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: rand)    : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: sample)  : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???

  def apply(v: mins)    : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???
  def apply(v: maxs)    : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???
  def apply(v: distinct): In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???
  def apply(v: rands)   : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Stream[M]] = ???
  def apply(v: samples) : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???

  def apply(v: count)        : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long] = ???
  def apply(v: countDistinct): In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long] = ???
  def apply(v: sum)          : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: avg)          : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: median)       : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: variance)     : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: stddev)       : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_15[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_16[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_17[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_18[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_19[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_20[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f, g] = ???
}

trait In_1_14[In1_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS {
  val e         : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In2_15[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val a         : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In2_15[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val v         : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ] with OneAny    [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ], In2_15[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ]] = ???
  val ns        : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In2_15[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val txInstant : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ] with OneDate   [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ], In2_15[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ]] = ???
  val txT       : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In2_15[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val txAdded   : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean] with OneBoolean[In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean], In2_15[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean]] = ???

  val e_         : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In2_14[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val a_         : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In2_14[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val v_         : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ] with OneAny    [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ], In2_14[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ]] = ???
  val ns_        : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In2_14[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val txInstant_ : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ] with OneDate   [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ], In2_14[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ]] = ???
  val txT_       : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In2_14[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val txAdded_   : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean] with OneBoolean[In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean], In2_14[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean]] = ???

  def apply(v: min)     : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: max)     : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: rand)    : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: sample)  : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???

  def apply(v: mins)    : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???
  def apply(v: maxs)    : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???
  def apply(v: distinct): In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???
  def apply(v: rands)   : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Stream[N]] = ???
  def apply(v: samples) : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???

  def apply(v: count)        : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long] = ???
  def apply(v: countDistinct): In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long] = ???
  def apply(v: sum)          : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: avg)          : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: median)       : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: variance)     : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: stddev)       : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_16[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_17[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_18[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_19[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_20[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_21[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f, g] = ???
}

trait In_1_15[In1_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS {
  val e         : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In2_16[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val a         : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In2_16[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val v         : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ] with OneAny    [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ], In2_16[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ]] = ???
  val ns        : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In2_16[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val txInstant : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ] with OneDate   [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ], In2_16[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ]] = ???
  val txT       : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In2_16[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val txAdded   : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean] with OneBoolean[In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean], In2_16[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean]] = ???

  val e_         : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In2_15[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val a_         : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In2_15[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val v_         : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ] with OneAny    [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ], In2_15[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ]] = ???
  val ns_        : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In2_15[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val txInstant_ : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ] with OneDate   [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ], In2_15[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ]] = ???
  val txT_       : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In2_15[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val txAdded_   : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean] with OneBoolean[In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean], In2_15[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean]] = ???

  def apply(v: min)     : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: max)     : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: rand)    : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: sample)  : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???

  def apply(v: mins)    : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???
  def apply(v: maxs)    : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???
  def apply(v: distinct): In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???
  def apply(v: rands)   : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Stream[O]] = ???
  def apply(v: samples) : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???

  def apply(v: count)        : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long] = ???
  def apply(v: countDistinct): In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long] = ???
  def apply(v: sum)          : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: avg)          : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: median)       : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: variance)     : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: stddev)       : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_17[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_18[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_19[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_20[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_21[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in1_7[_,_,_,_,_,_,_,_], in1_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in1_7, in1_8, a, b, c, d, e, f, g]): In_1_22[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f, g] = ???
}

trait In_1_16[In1_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS {
  val e         : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In2_17[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val a         : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In2_17[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val v         : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ] with OneAny    [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ], In2_17[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ]] = ???
  val ns        : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In2_17[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val txInstant : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ] with OneDate   [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ], In2_17[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ]] = ???
  val txT       : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In2_17[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val txAdded   : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean] with OneBoolean[In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean], In2_17[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean]] = ???

  val e_         : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In2_16[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val a_         : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In2_16[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val v_         : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ] with OneAny    [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ], In2_16[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ]] = ???
  val ns_        : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In2_16[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val txInstant_ : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ] with OneDate   [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ], In2_16[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ]] = ???
  val txT_       : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In2_16[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val txAdded_   : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean] with OneBoolean[In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean], In2_16[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean]] = ???

  def apply(v: min)     : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: max)     : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: rand)    : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: sample)  : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???

  def apply(v: mins)    : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???
  def apply(v: maxs)    : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???
  def apply(v: distinct): In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???
  def apply(v: rands)   : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Stream[P]] = ???
  def apply(v: samples) : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???

  def apply(v: count)        : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long] = ???
  def apply(v: countDistinct): In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long] = ???
  def apply(v: sum)          : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: avg)          : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: median)       : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: variance)     : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: stddev)       : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]                                    = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a]                                 = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_18[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_19[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_20[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d]       = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_21[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e]    = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in1_6[_,_,_,_,_,_,_]  , in1_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in1_6, in1_7, a, b, c, d, e, f])   : In_1_22[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e, f] = ???
}

trait In_1_17[In1_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS {
  val e         : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In2_18[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val a         : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In2_18[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val v         : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ] with OneAny    [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ], In2_18[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ]] = ???
  val ns        : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In2_18[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val txInstant : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ] with OneDate   [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ], In2_18[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ]] = ???
  val txT       : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In2_18[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val txAdded   : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean] with OneBoolean[In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean], In2_18[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean]] = ???

  val e_         : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In2_17[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val a_         : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In2_17[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val v_         : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ] with OneAny    [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ], In2_17[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ]] = ???
  val ns_        : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In2_17[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val txInstant_ : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ] with OneDate   [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ], In2_17[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ]] = ???
  val txT_       : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In2_17[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val txAdded_   : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean] with OneBoolean[In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean], In2_17[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean]] = ???

  def apply(v: min)     : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: max)     : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: rand)    : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: sample)  : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???

  def apply(v: mins)    : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???
  def apply(v: maxs)    : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???
  def apply(v: distinct): In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???
  def apply(v: rands)   : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Stream[Q]] = ???
  def apply(v: samples) : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???

  def apply(v: count)        : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long] = ???
  def apply(v: countDistinct): In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long] = ???
  def apply(v: sum)          : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: avg)          : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: median)       : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: variance)     : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: stddev)       : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_19[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_20[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_21[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d]       = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in1_5[_,_,_,_,_,_]    , in1_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in1_5, in1_6, a, b, c, d, e])      : In_1_22[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d, e]    = ???
}

trait In_1_18[In1_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS {
  val e         : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In2_19[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val a         : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In2_19[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val v         : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ] with OneAny    [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ], In2_19[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ]] = ???
  val ns        : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In2_19[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val txInstant : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ] with OneDate   [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ], In2_19[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ]] = ???
  val txT       : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In2_19[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val txAdded   : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean] with OneBoolean[In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean], In2_19[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean]] = ???

  val e_         : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In2_18[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val a_         : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In2_18[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val v_         : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ] with OneAny    [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ], In2_18[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ]] = ???
  val ns_        : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In2_18[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val txInstant_ : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ] with OneDate   [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ], In2_18[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ]] = ???
  val txT_       : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In2_18[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val txAdded_   : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean] with OneBoolean[In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean], In2_18[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean]] = ???

  def apply(v: min)     : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: max)     : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: rand)    : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: sample)  : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???

  def apply(v: mins)    : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???
  def apply(v: maxs)    : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???
  def apply(v: distinct): In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???
  def apply(v: rands)   : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Stream[R]] = ???
  def apply(v: samples) : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???

  def apply(v: count)        : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long] = ???
  def apply(v: countDistinct): In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long] = ???
  def apply(v: sum)          : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: avg)          : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: median)       : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: variance)     : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: stddev)       : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_20[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_21[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in1_4[_,_,_,_,_]      , in1_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in1_4, in1_5, a, b, c, d])         : In_1_22[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c, d]       = ???
}

trait In_1_19[In1_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS {
  val e         : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In2_20[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val a         : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In2_20[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val v         : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ] with OneAny    [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ], In2_20[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ]] = ???
  val ns        : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In2_20[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val txInstant : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ] with OneDate   [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ], In2_20[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ]] = ???
  val txT       : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In2_20[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val txAdded   : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean] with OneBoolean[In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean], In2_20[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean]] = ???

  val e_         : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In2_19[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val a_         : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In2_19[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val v_         : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ] with OneAny    [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ], In2_19[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ]] = ???
  val ns_        : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In2_19[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val txInstant_ : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ] with OneDate   [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ], In2_19[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ]] = ???
  val txT_       : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In2_19[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val txAdded_   : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean] with OneBoolean[In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean], In2_19[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean]] = ???

  def apply(v: min)     : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: max)     : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: rand)    : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: sample)  : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???

  def apply(v: mins)    : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???
  def apply(v: maxs)    : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???
  def apply(v: distinct): In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???
  def apply(v: rands)   : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Stream[S]] = ???
  def apply(v: samples) : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???

  def apply(v: count)        : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long] = ???
  def apply(v: countDistinct): In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long] = ???
  def apply(v: sum)          : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: avg)          : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: median)       : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: variance)     : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: stddev)       : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_21[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in1_3[_,_,_,_]        , in1_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in1_3, in1_4, a, b, c])            : In_1_22[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b, c]          = ???
}

trait In_1_20[In1_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS {
  val e         : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In2_21[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val a         : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In2_21[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val v         : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ] with OneAny    [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ], In2_21[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ]] = ???
  val ns        : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In2_21[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val txInstant : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ] with OneDate   [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ], In2_21[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ]] = ???
  val txT       : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In2_21[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val txAdded   : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean] with OneBoolean[In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean], In2_21[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean]] = ???

  val e_         : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In2_20[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val a_         : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In2_20[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val v_         : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ] with OneAny    [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ], In2_20[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ]] = ???
  val ns_        : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In2_20[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val txInstant_ : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ] with OneDate   [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ], In2_20[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ]] = ???
  val txT_       : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In2_20[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val txAdded_   : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean] with OneBoolean[In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean], In2_20[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean]] = ???

  def apply(v: min)     : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: max)     : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: rand)    : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: sample)  : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???

  def apply(v: mins)    : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???
  def apply(v: maxs)    : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???
  def apply(v: distinct): In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???
  def apply(v: rands)   : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Stream[T]] = ???
  def apply(v: samples) : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???

  def apply(v: count)        : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long] = ???
  def apply(v: countDistinct): In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long] = ???
  def apply(v: sum)          : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: avg)          : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: median)       : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: variance)     : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: stddev)       : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in1_2[_,_,_]          , in1_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in1_2, in1_3, a, b])               : In_1_22[Any, Any, Any, Any, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a, b]             = ???
}

trait In_1_21[In1_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In1_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS {
  val e         : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], In2_22[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ]] = ???
  val a         : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], In2_22[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ]] = ???
  val v         : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], In2_22[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ]] = ???
  val ns        : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], In2_22[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ]] = ???
  val txInstant : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], In2_22[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ]] = ???
  val txT       : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], In2_22[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ]] = ???
  val txAdded   : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], In2_22[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean]] = ???

  val e_         : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In2_21[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val a_         : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In2_21[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val v_         : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ] with OneAny    [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ], In2_21[Any    , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ]] = ???
  val ns_        : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In2_21[String , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val txInstant_ : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ] with OneDate   [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ], In2_21[Date   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ]] = ???
  val txT_       : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In2_21[Long   , I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val txAdded_   : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean] with OneBoolean[In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean], In2_21[Boolean, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean]] = ???

  def apply(v: min)     : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: max)     : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: rand)    : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: sample)  : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???

  def apply(v: mins)    : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???
  def apply(v: maxs)    : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???
  def apply(v: distinct): In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???
  def apply(v: rands)   : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Stream[U]] = ???
  def apply(v: samples) : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???

  def apply(v: count)        : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long] = ???
  def apply(v: countDistinct): In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long] = ???
  def apply(v: sum)          : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: avg)          : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: median)       : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: variance)     : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: stddev)       : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in1_1[_,_]            , in1_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in1_1, in1_2, a])                  : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, a]                                           = ???
}

trait In_1_22[In1_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], P24[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], P25[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS {
  val e_         : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val a_         : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val v_         : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], Nothing] = ???
  val ns_        : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val txInstant_ : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], Nothing] = ???
  val txT_       : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val txAdded_   : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], Nothing] = ???

  def tx[ns0               , ns1[_]              , in1_0[_]              , in1_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in1_0, in1_1])                     : In1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]                                              = ???
}
