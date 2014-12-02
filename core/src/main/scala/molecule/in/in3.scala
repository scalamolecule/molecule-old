package molecule
package in

import molecule.out._
import java.util.Date
import molecule.dsl.schemaDSL.NS
import molecule.dsl.schemaDSL._
import scala.language.higherKinds


trait In_3_0[In3_0[_,_,_], In3_1[_,_,_,_], In4_0[_,_,_,_], In4_1[_,_,_,_,_], I1, I2, I3] extends NS {
  val e          : In3_1[I1, I2, I3, Long   ] with OneLong   [In3_1[I1, I2, I3, Long   ], In4_1[Long   , I1, I2, I3, Long   ]] = ???
  val a          : In3_1[I1, I2, I3, String ] with OneString [In3_1[I1, I2, I3, String ], In4_1[String , I1, I2, I3, String ]] = ???
  val v          : In3_1[I1, I2, I3, Any    ] with OneAny    [In3_1[I1, I2, I3, Any    ], In4_1[Any    , I1, I2, I3, Any    ]] = ???
  val ns         : In3_1[I1, I2, I3, String ] with OneString [In3_1[I1, I2, I3, String ], In4_1[String , I1, I2, I3, String ]] = ???
  val txInstant  : In3_1[I1, I2, I3, Date   ] with OneDate   [In3_1[I1, I2, I3, Date   ], In4_1[Date   , I1, I2, I3, Date   ]] = ???
  val txT        : In3_1[I1, I2, I3, Long   ] with OneLong   [In3_1[I1, I2, I3, Long   ], In4_1[Long   , I1, I2, I3, Long   ]] = ???
  val txAdded    : In3_1[I1, I2, I3, Boolean] with OneBoolean[In3_1[I1, I2, I3, Boolean], In4_1[Boolean, I1, I2, I3, Boolean]] = ???

  val e_         : In3_0[I1, I2, I3] with OneLong   [In3_0[I1, I2, I3], In4_0[Long   , I1, I2, Long   ]] = ???
  val a_         : In3_0[I1, I2, I3] with OneString [In3_0[I1, I2, I3], In4_0[String , I1, I2, String ]] = ???
  val v_         : In3_0[I1, I2, I3] with OneAny    [In3_0[I1, I2, I3], In4_0[Any    , I1, I2, Any    ]] = ???
  val ns_        : In3_0[I1, I2, I3] with OneString [In3_0[I1, I2, I3], In4_0[String , I1, I2, String ]] = ???
  val txInstant_ : In3_0[I1, I2, I3] with OneDate   [In3_0[I1, I2, I3], In4_0[Date   , I1, I2, Date   ]] = ???
  val txT_       : In3_0[I1, I2, I3] with OneLong   [In3_0[I1, I2, I3], In4_0[Long   , I1, I2, Long   ]] = ???
  val txAdded_   : In3_0[I1, I2, I3] with OneBoolean[In3_0[I1, I2, I3], In4_0[Boolean, I1, I2, Boolean]] = ???

  // If we supply 2 or more tx attributes we return a generic molecule
  // This means that you can't continue expanding the molecule from the initial namespace anymore, so you'll
  // want to have the tx data defined in the end of the molecule
  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_0[I1, I2, I3]                                                 = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_1[I1, I2, I3, a]                                              = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_2[Any, Any, Any, Any, I1, I2, I3, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_3[Any, Any, Any, Any, I1, I2, I3, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_4[Any, Any, Any, Any, I1, I2, I3, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_5[Any, Any, Any, Any, I1, I2, I3, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_6[Any, Any, Any, Any, I1, I2, I3, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_7[Any, Any, Any, Any, I1, I2, I3, a, b, c, d, e, f, g] = ???
}

trait In_3_1[In3_1[_,_,_,_], In3_2[_,_,_,_,_], In4_1[_,_,_,_,_], In4_2[_,_,_,_,_,_], I1, I2, I3, A] extends NS {
  val e         : In3_2[I1, I2, I3, A, Long   ] with OneLong   [In3_2[I1, I2, I3, A, Long   ], In4_2[Long   , I1, I2, I3, A,Long   ]] = ???
  val a         : In3_2[I1, I2, I3, A, String ] with OneString [In3_2[I1, I2, I3, A, String ], In4_2[String , I1, I2, I3, A,String ]] = ???
  val v         : In3_2[I1, I2, I3, A, Any    ] with OneAny    [In3_2[I1, I2, I3, A, Any    ], In4_2[Any    , I1, I2, I3, A,Any    ]] = ???
  val ns        : In3_2[I1, I2, I3, A, String ] with OneString [In3_2[I1, I2, I3, A, String ], In4_2[String , I1, I2, I3, A,String ]] = ???
  val txInstant : In3_2[I1, I2, I3, A, Date   ] with OneDate   [In3_2[I1, I2, I3, A, Date   ], In4_2[Date   , I1, I2, I3, A,Date   ]] = ???
  val txT       : In3_2[I1, I2, I3, A, Long   ] with OneLong   [In3_2[I1, I2, I3, A, Long   ], In4_2[Long   , I1, I2, I3, A,Long   ]] = ???
  val txAdded   : In3_2[I1, I2, I3, A, Boolean] with OneBoolean[In3_2[I1, I2, I3, A, Boolean], In4_2[Boolean, I1, I2, I3, A, Boolean]] = ???

  val e_          : In3_1[I1, I2, I3, Long   ] with OneLong   [In3_1[I1, I2, I3, Long   ], In4_1[Long   , I1, I2, I3, Long   ]] = ???
  val a_          : In3_1[I1, I2, I3, String ] with OneString [In3_1[I1, I2, I3, String ], In4_1[String , I1, I2, I3, String ]] = ???
  val v_          : In3_1[I1, I2, I3, Any    ] with OneAny    [In3_1[I1, I2, I3, Any    ], In4_1[Any    , I1, I2, I3, Any    ]] = ???
  val ns_         : In3_1[I1, I2, I3, String ] with OneString [In3_1[I1, I2, I3, String ], In4_1[String , I1, I2, I3, String ]] = ???
  val txInstant_  : In3_1[I1, I2, I3, Date   ] with OneDate   [In3_1[I1, I2, I3, Date   ], In4_1[Date   , I1, I2, I3, Date   ]] = ???
  val txT_        : In3_1[I1, I2, I3, Long   ] with OneLong   [In3_1[I1, I2, I3, Long   ], In4_1[Long   , I1, I2, I3, Long   ]] = ???
  val txAdded_    : In3_1[I1, I2, I3, Boolean] with OneBoolean[In3_1[I1, I2, I3, Boolean], In4_1[Boolean, I1, I2, I3, Boolean]] = ???

  def apply(v: min)     : In3_1[I1, I2, I3, A] = ???
  def apply(v: max)     : In3_1[I1, I2, I3, A] = ???
  def apply(v: rand)    : In3_1[I1, I2, I3, A] = ???
  def apply(v: sample)  : In3_1[I1, I2, I3, A] = ???

  def apply(v: mins)    : In3_1[I1, I2, I3, Vector[A]] = ???
  def apply(v: maxs)    : In3_1[I1, I2, I3, Vector[A]] = ???
  def apply(v: distinct): In3_1[I1, I2, I3, Vector[A]] = ???
  def apply(v: rands)   : In3_1[I1, I2, I3, Stream[A]] = ???
  def apply(v: samples) : In3_1[I1, I2, I3, Vector[A]] = ???

  def apply(v: count)        : In3_1[I1, I2, I3, Long] = ???
  def apply(v: countDistinct): In3_1[I1, I2, I3, Long] = ???
  def apply(v: sum)          : In3_1[I1, I2, I3, Double] = ???
  def apply(v: avg)          : In3_1[I1, I2, I3, Double] = ???
  def apply(v: median)       : In3_1[I1, I2, I3, Double] = ???
  def apply(v: variance)     : In3_1[I1, I2, I3, Double] = ???
  def apply(v: stddev)       : In3_1[I1, I2, I3, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_1[I1, I2, I3, A]                                                 = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_2[I1, I2, I3, A, a]                                              = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_3[Any, Any, Any, Any, I1, I2, I3, A, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_4[Any, Any, Any, Any, I1, I2, I3, A, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_5[Any, Any, Any, Any, I1, I2, I3, A, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_6[Any, Any, Any, Any, I1, I2, I3, A, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_7[Any, Any, Any, Any, I1, I2, I3, A, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_8[Any, Any, Any, Any, I1, I2, I3, A, a, b, c, d, e, f, g] = ???
}

trait In_3_2[In3_2[_,_,_,_,_], In3_3[_,_,_,_,_,_], In4_2[_,_,_,_,_,_], In4_3[_,_,_,_,_,_,_], I1, I2, I3, A, B] extends NS {
  val e         : In3_3[I1, I2, I3, A, B, Long   ] with OneLong   [In3_3[I1, I2, I3, A, B, Long   ], In4_3[Long   , I1, I2, I3, A, B, Long   ]] = ???
  val a         : In3_3[I1, I2, I3, A, B, String ] with OneString [In3_3[I1, I2, I3, A, B, String ], In4_3[String , I1, I2, I3, A, B, String ]] = ???
  val v         : In3_3[I1, I2, I3, A, B, Any    ] with OneAny    [In3_3[I1, I2, I3, A, B, Any    ], In4_3[Any    , I1, I2, I3, A, B, Any    ]] = ???
  val ns        : In3_3[I1, I2, I3, A, B, String ] with OneString [In3_3[I1, I2, I3, A, B, String ], In4_3[String , I1, I2, I3, A, B, String ]] = ???
  val txInstant : In3_3[I1, I2, I3, A, B, Date   ] with OneDate   [In3_3[I1, I2, I3, A, B, Date   ], In4_3[Date   , I1, I2, I3, A, B, Date   ]] = ???
  val txT       : In3_3[I1, I2, I3, A, B, Long   ] with OneLong   [In3_3[I1, I2, I3, A, B, Long   ], In4_3[Long   , I1, I2, I3, A, B, Long   ]] = ???
  val txAdded   : In3_3[I1, I2, I3, A, B, Boolean] with OneBoolean[In3_3[I1, I2, I3, A, B, Boolean], In4_3[Boolean, I1, I2, I3, A, B, Boolean]] = ???

  val e_         : In3_2[I1, I2, I3, A, Long   ] with OneLong   [In3_2[I1, I2, I3, A, Long   ], In4_2[Long   , I1, I2, I3, A, Long   ]] = ???
  val a_         : In3_2[I1, I2, I3, A, String ] with OneString [In3_2[I1, I2, I3, A, String ], In4_2[String , I1, I2, I3, A, String ]] = ???
  val v_         : In3_2[I1, I2, I3, A, Any    ] with OneAny    [In3_2[I1, I2, I3, A, Any    ], In4_2[Any    , I1, I2, I3, A, Any    ]] = ???
  val ns_        : In3_2[I1, I2, I3, A, String ] with OneString [In3_2[I1, I2, I3, A, String ], In4_2[String , I1, I2, I3, A, String ]] = ???
  val txInstant_ : In3_2[I1, I2, I3, A, Date   ] with OneDate   [In3_2[I1, I2, I3, A, Date   ], In4_2[Date   , I1, I2, I3, A, Date   ]] = ???
  val txT_       : In3_2[I1, I2, I3, A, Long   ] with OneLong   [In3_2[I1, I2, I3, A, Long   ], In4_2[Long   , I1, I2, I3, A, Long   ]] = ???
  val txAdded_   : In3_2[I1, I2, I3, A, Boolean] with OneBoolean[In3_2[I1, I2, I3, A, Boolean], In4_2[Boolean, I1, I2, I3, A, Boolean]] = ???

  def apply(v: min)     : In3_2[I1, I2, I3, A, B] = ???
  def apply(v: max)     : In3_2[I1, I2, I3, A, B] = ???
  def apply(v: rand)    : In3_2[I1, I2, I3, A, B] = ???
  def apply(v: sample)  : In3_2[I1, I2, I3, A, B] = ???

  def apply(v: mins)    : In3_2[I1, I2, I3, A, Vector[B]] = ???
  def apply(v: maxs)    : In3_2[I1, I2, I3, A, Vector[B]] = ???
  def apply(v: distinct): In3_2[I1, I2, I3, A, Vector[B]] = ???
  def apply(v: rands)   : In3_2[I1, I2, I3, A, Stream[B]] = ???
  def apply(v: samples) : In3_2[I1, I2, I3, A, Vector[B]] = ???

  def apply(v: count)        : In3_2[I1, I2, I3, A, Long] = ???
  def apply(v: countDistinct): In3_2[I1, I2, I3, A, Long] = ???
  def apply(v: sum)          : In3_2[I1, I2, I3, A, Double] = ???
  def apply(v: avg)          : In3_2[I1, I2, I3, A, Double] = ???
  def apply(v: median)       : In3_2[I1, I2, I3, A, Double] = ???
  def apply(v: variance)     : In3_2[I1, I2, I3, A, Double] = ???
  def apply(v: stddev)       : In3_2[I1, I2, I3, A, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_2[I1, I2, I3, A, B]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_3[I1, I2, I3, A, B, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_4[Any, Any, Any, Any, I1, I2, I3, A, B, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_5[Any, Any, Any, Any, I1, I2, I3, A, B, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_6[Any, Any, Any, Any, I1, I2, I3, A, B, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_7[Any, Any, Any, Any, I1, I2, I3, A, B, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_8[Any, Any, Any, Any, I1, I2, I3, A, B, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_9[Any, Any, Any, Any, I1, I2, I3, A, B, a, b, c, d, e, f, g] = ???
}

trait In_3_3[In3_3[_,_,_,_,_,_], In3_4[_,_,_,_,_,_,_], In4_3[_,_,_,_,_,_,_], In4_4[_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C] extends NS {
  val e         : In3_4[I1, I2, I3, A, B, C, Long   ] with OneLong   [In3_4[I1, I2, I3, A, B, C, Long   ], In4_4[Long   , I1, I2, I3, A, B, C, Long   ]] = ???
  val a         : In3_4[I1, I2, I3, A, B, C, String ] with OneString [In3_4[I1, I2, I3, A, B, C, String ], In4_4[String , I1, I2, I3, A, B, C, String ]] = ???
  val v         : In3_4[I1, I2, I3, A, B, C, Any    ] with OneAny    [In3_4[I1, I2, I3, A, B, C, Any    ], In4_4[Any    , I1, I2, I3, A, B, C, Any    ]] = ???
  val ns        : In3_4[I1, I2, I3, A, B, C, String ] with OneString [In3_4[I1, I2, I3, A, B, C, String ], In4_4[String , I1, I2, I3, A, B, C, String ]] = ???
  val txInstant : In3_4[I1, I2, I3, A, B, C, Date   ] with OneDate   [In3_4[I1, I2, I3, A, B, C, Date   ], In4_4[Date   , I1, I2, I3, A, B, C, Date   ]] = ???
  val txT       : In3_4[I1, I2, I3, A, B, C, Long   ] with OneLong   [In3_4[I1, I2, I3, A, B, C, Long   ], In4_4[Long   , I1, I2, I3, A, B, C, Long   ]] = ???
  val txAdded   : In3_4[I1, I2, I3, A, B, C, Boolean] with OneBoolean[In3_4[I1, I2, I3, A, B, C, Boolean], In4_4[Boolean, I1, I2, I3, A, B, C, Boolean]] = ???

  val e_         : In3_3[I1, I2, I3, A, B, Long   ] with OneLong   [In3_3[I1, I2, I3, A, B, Long   ], In4_3[Long   , I1, I2, I3, A, B, Long   ]] = ???
  val a_         : In3_3[I1, I2, I3, A, B, String ] with OneString [In3_3[I1, I2, I3, A, B, String ], In4_3[String , I1, I2, I3, A, B, String ]] = ???
  val v_         : In3_3[I1, I2, I3, A, B, Any    ] with OneAny    [In3_3[I1, I2, I3, A, B, Any    ], In4_3[Any    , I1, I2, I3, A, B, Any    ]] = ???
  val ns_        : In3_3[I1, I2, I3, A, B, String ] with OneString [In3_3[I1, I2, I3, A, B, String ], In4_3[String , I1, I2, I3, A, B, String ]] = ???
  val txInstant_ : In3_3[I1, I2, I3, A, B, Date   ] with OneDate   [In3_3[I1, I2, I3, A, B, Date   ], In4_3[Date   , I1, I2, I3, A, B, Date   ]] = ???
  val txT_       : In3_3[I1, I2, I3, A, B, Long   ] with OneLong   [In3_3[I1, I2, I3, A, B, Long   ], In4_3[Long   , I1, I2, I3, A, B, Long   ]] = ???
  val txAdded_   : In3_3[I1, I2, I3, A, B, Boolean] with OneBoolean[In3_3[I1, I2, I3, A, B, Boolean], In4_3[Boolean, I1, I2, I3, A, B, Boolean]] = ???

  def apply(v: min)     : In3_3[I1, I2, I3, A, B, C] = ???
  def apply(v: max)     : In3_3[I1, I2, I3, A, B, C] = ???
  def apply(v: rand)    : In3_3[I1, I2, I3, A, B, C] = ???
  def apply(v: sample)  : In3_3[I1, I2, I3, A, B, C] = ???

  def apply(v: mins)    : In3_3[I1, I2, I3, A, B, Vector[C]] = ???
  def apply(v: maxs)    : In3_3[I1, I2, I3, A, B, Vector[C]] = ???
  def apply(v: distinct): In3_3[I1, I2, I3, A, B, Vector[C]] = ???
  def apply(v: rands)   : In3_3[I1, I2, I3, A, B, Stream[C]] = ???
  def apply(v: samples) : In3_3[I1, I2, I3, A, B, Vector[C]] = ???

  def apply(v: count)        : In3_3[I1, I2, I3, A, B, Long] = ???
  def apply(v: countDistinct): In3_3[I1, I2, I3, A, B, Long] = ???
  def apply(v: sum)          : In3_3[I1, I2, I3, A, B, Double] = ???
  def apply(v: avg)          : In3_3[I1, I2, I3, A, B, Double] = ???
  def apply(v: median)       : In3_3[I1, I2, I3, A, B, Double] = ???
  def apply(v: variance)     : In3_3[I1, I2, I3, A, B, Double] = ???
  def apply(v: stddev)       : In3_3[I1, I2, I3, A, B, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_3 [I1, I2, I3, A, B, C]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_4 [I1, I2, I3, A, B, C, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_5 [Any, Any, Any, Any, I1, I2, I3, A, B, C, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_6 [Any, Any, Any, Any, I1, I2, I3, A, B, C, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_7 [Any, Any, Any, Any, I1, I2, I3, A, B, C, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_8 [Any, Any, Any, Any, I1, I2, I3, A, B, C, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_9 [Any, Any, Any, Any, I1, I2, I3, A, B, C, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_10[Any, Any, Any, Any, I1, I2, I3, A, B, C, a, b, c, d, e, f, g] = ???
}

trait In_3_4[In3_4[_,_,_,_,_,_,_], In3_5[_,_,_,_,_,_,_,_], In4_4[_,_,_,_,_,_,_,_], In4_5[_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D] extends NS {
  val e         : In3_5[I1, I2, I3, A, B, C, D, Long   ] with OneLong   [In3_5[I1, I2, I3, A, B, C, D, Long   ], In4_5[Long   , I1, I2, I3, A, B, C, D, Long   ]] = ???
  val a         : In3_5[I1, I2, I3, A, B, C, D, String ] with OneString [In3_5[I1, I2, I3, A, B, C, D, String ], In4_5[String , I1, I2, I3, A, B, C, D, String ]] = ???
  val v         : In3_5[I1, I2, I3, A, B, C, D, Any    ] with OneAny    [In3_5[I1, I2, I3, A, B, C, D, Any    ], In4_5[Any    , I1, I2, I3, A, B, C, D, Any    ]] = ???
  val ns        : In3_5[I1, I2, I3, A, B, C, D, String ] with OneString [In3_5[I1, I2, I3, A, B, C, D, String ], In4_5[String , I1, I2, I3, A, B, C, D, String ]] = ???
  val txInstant : In3_5[I1, I2, I3, A, B, C, D, Date   ] with OneDate   [In3_5[I1, I2, I3, A, B, C, D, Date   ], In4_5[Date   , I1, I2, I3, A, B, C, D, Date   ]] = ???
  val txT       : In3_5[I1, I2, I3, A, B, C, D, Long   ] with OneLong   [In3_5[I1, I2, I3, A, B, C, D, Long   ], In4_5[Long   , I1, I2, I3, A, B, C, D, Long   ]] = ???
  val txAdded   : In3_5[I1, I2, I3, A, B, C, D, Boolean] with OneBoolean[In3_5[I1, I2, I3, A, B, C, D, Boolean], In4_5[Boolean, I1, I2, I3, A, B, C, D, Boolean]] = ???

  val e_         : In3_4[I1, I2, I3, A, B, C, Long   ] with OneLong   [In3_4[I1, I2, I3, A, B, C, Long   ], In4_4[Long   , I1, I2, I3, A, B, C, Long   ]] = ???
  val a_         : In3_4[I1, I2, I3, A, B, C, String ] with OneString [In3_4[I1, I2, I3, A, B, C, String ], In4_4[String , I1, I2, I3, A, B, C, String ]] = ???
  val v_         : In3_4[I1, I2, I3, A, B, C, Any    ] with OneAny    [In3_4[I1, I2, I3, A, B, C, Any    ], In4_4[Any    , I1, I2, I3, A, B, C, Any    ]] = ???
  val ns_        : In3_4[I1, I2, I3, A, B, C, String ] with OneString [In3_4[I1, I2, I3, A, B, C, String ], In4_4[String , I1, I2, I3, A, B, C, String ]] = ???
  val txInstant_ : In3_4[I1, I2, I3, A, B, C, Date   ] with OneDate   [In3_4[I1, I2, I3, A, B, C, Date   ], In4_4[Date   , I1, I2, I3, A, B, C, Date   ]] = ???
  val txT_       : In3_4[I1, I2, I3, A, B, C, Long   ] with OneLong   [In3_4[I1, I2, I3, A, B, C, Long   ], In4_4[Long   , I1, I2, I3, A, B, C, Long   ]] = ???
  val txAdded_   : In3_4[I1, I2, I3, A, B, C, Boolean] with OneBoolean[In3_4[I1, I2, I3, A, B, C, Boolean], In4_4[Boolean, I1, I2, I3, A, B, C, Boolean]] = ???

  def apply(v: min)     : In3_4[I1, I2, I3, A, B, C, D] = ???
  def apply(v: max)     : In3_4[I1, I2, I3, A, B, C, D] = ???
  def apply(v: rand)    : In3_4[I1, I2, I3, A, B, C, D] = ???
  def apply(v: sample)  : In3_4[I1, I2, I3, A, B, C, D] = ???

  def apply(v: mins)    : In3_4[I1, I2, I3, A, B, C, Vector[D]] = ???
  def apply(v: maxs)    : In3_4[I1, I2, I3, A, B, C, Vector[D]] = ???
  def apply(v: distinct): In3_4[I1, I2, I3, A, B, C, Vector[D]] = ???
  def apply(v: rands)   : In3_4[I1, I2, I3, A, B, C, Stream[D]] = ???
  def apply(v: samples) : In3_4[I1, I2, I3, A, B, C, Vector[D]] = ???

  def apply(v: count)        : In3_4[I1, I2, I3, A, B, C, Long] = ???
  def apply(v: countDistinct): In3_4[I1, I2, I3, A, B, C, Long] = ???
  def apply(v: sum)          : In3_4[I1, I2, I3, A, B, C, Double] = ???
  def apply(v: avg)          : In3_4[I1, I2, I3, A, B, C, Double] = ???
  def apply(v: median)       : In3_4[I1, I2, I3, A, B, C, Double] = ???
  def apply(v: variance)     : In3_4[I1, I2, I3, A, B, C, Double] = ???
  def apply(v: stddev)       : In3_4[I1, I2, I3, A, B, C, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_4 [I1, I2, I3, A, B, C, D]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_5 [I1, I2, I3, A, B, C, D, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_6 [Any, Any, Any, Any, I1, I2, I3, A, B, C, D, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_7 [Any, Any, Any, Any, I1, I2, I3, A, B, C, D, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_8 [Any, Any, Any, Any, I1, I2, I3, A, B, C, D, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_9 [Any, Any, Any, Any, I1, I2, I3, A, B, C, D, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_10[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_11[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, a, b, c, d, e, f, g] = ???
}

trait In_3_5[In3_5[_,_,_,_,_,_,_,_], In3_6[_,_,_,_,_,_,_,_,_], In4_5[_,_,_,_,_,_,_,_,_], In4_6[_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E] extends NS {
  val e         : In3_6[I1, I2, I3, A, B, C, D, E, Long   ] with OneLong   [In3_6[I1, I2, I3, A, B, C, D, E, Long   ], In4_6[Long   , I1, I2, I3, A, B, C, D, E, Long   ]] = ???
  val a         : In3_6[I1, I2, I3, A, B, C, D, E, String ] with OneString [In3_6[I1, I2, I3, A, B, C, D, E, String ], In4_6[String , I1, I2, I3, A, B, C, D, E, String ]] = ???
  val v         : In3_6[I1, I2, I3, A, B, C, D, E, Any    ] with OneAny    [In3_6[I1, I2, I3, A, B, C, D, E, Any    ], In4_6[Any    , I1, I2, I3, A, B, C, D, E, Any    ]] = ???
  val ns        : In3_6[I1, I2, I3, A, B, C, D, E, String ] with OneString [In3_6[I1, I2, I3, A, B, C, D, E, String ], In4_6[String , I1, I2, I3, A, B, C, D, E, String ]] = ???
  val txInstant : In3_6[I1, I2, I3, A, B, C, D, E, Date   ] with OneDate   [In3_6[I1, I2, I3, A, B, C, D, E, Date   ], In4_6[Date   , I1, I2, I3, A, B, C, D, E, Date   ]] = ???
  val txT       : In3_6[I1, I2, I3, A, B, C, D, E, Long   ] with OneLong   [In3_6[I1, I2, I3, A, B, C, D, E, Long   ], In4_6[Long   , I1, I2, I3, A, B, C, D, E, Long   ]] = ???
  val txAdded   : In3_6[I1, I2, I3, A, B, C, D, E, Boolean] with OneBoolean[In3_6[I1, I2, I3, A, B, C, D, E, Boolean], In4_6[Boolean, I1, I2, I3, A, B, C, D, E, Boolean]] = ???

  val e_         : In3_5[I1, I2, I3, A, B, C, D, Long   ] with OneLong   [In3_5[I1, I2, I3, A, B, C, D, Long   ], In4_5[Long   , I1, I2, I3, A, B, C, D, Long   ]] = ???
  val a_         : In3_5[I1, I2, I3, A, B, C, D, String ] with OneString [In3_5[I1, I2, I3, A, B, C, D, String ], In4_5[String , I1, I2, I3, A, B, C, D, String ]] = ???
  val v_         : In3_5[I1, I2, I3, A, B, C, D, Any    ] with OneAny    [In3_5[I1, I2, I3, A, B, C, D, Any    ], In4_5[Any    , I1, I2, I3, A, B, C, D, Any    ]] = ???
  val ns_        : In3_5[I1, I2, I3, A, B, C, D, String ] with OneString [In3_5[I1, I2, I3, A, B, C, D, String ], In4_5[String , I1, I2, I3, A, B, C, D, String ]] = ???
  val txInstant_ : In3_5[I1, I2, I3, A, B, C, D, Date   ] with OneDate   [In3_5[I1, I2, I3, A, B, C, D, Date   ], In4_5[Date   , I1, I2, I3, A, B, C, D, Date   ]] = ???
  val txT_       : In3_5[I1, I2, I3, A, B, C, D, Long   ] with OneLong   [In3_5[I1, I2, I3, A, B, C, D, Long   ], In4_5[Long   , I1, I2, I3, A, B, C, D, Long   ]] = ???
  val txAdded_   : In3_5[I1, I2, I3, A, B, C, D, Boolean] with OneBoolean[In3_5[I1, I2, I3, A, B, C, D, Boolean], In4_5[Boolean, I1, I2, I3, A, B, C, D, Boolean]] = ???

  def apply(v: min)     : In3_5[I1, I2, I3, A, B, C, D, E] = ???
  def apply(v: max)     : In3_5[I1, I2, I3, A, B, C, D, E] = ???
  def apply(v: rand)    : In3_5[I1, I2, I3, A, B, C, D, E] = ???
  def apply(v: sample)  : In3_5[I1, I2, I3, A, B, C, D, E] = ???

  def apply(v: mins)    : In3_5[I1, I2, I3, A, B, C, D, Vector[E]] = ???
  def apply(v: maxs)    : In3_5[I1, I2, I3, A, B, C, D, Vector[E]] = ???
  def apply(v: distinct): In3_5[I1, I2, I3, A, B, C, D, Vector[E]] = ???
  def apply(v: rands)   : In3_5[I1, I2, I3, A, B, C, D, Stream[E]] = ???
  def apply(v: samples) : In3_5[I1, I2, I3, A, B, C, D, Vector[E]] = ???

  def apply(v: count)        : In3_5[I1, I2, I3, A, B, C, D, Long] = ???
  def apply(v: countDistinct): In3_5[I1, I2, I3, A, B, C, D, Long] = ???
  def apply(v: sum)          : In3_5[I1, I2, I3, A, B, C, D, Double] = ???
  def apply(v: avg)          : In3_5[I1, I2, I3, A, B, C, D, Double] = ???
  def apply(v: median)       : In3_5[I1, I2, I3, A, B, C, D, Double] = ???
  def apply(v: variance)     : In3_5[I1, I2, I3, A, B, C, D, Double] = ???
  def apply(v: stddev)       : In3_5[I1, I2, I3, A, B, C, D, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_5 [I1, I2, I3, A, B, C, D, E]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_6 [I1, I2, I3, A, B, C, D, E, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_7 [Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_8 [Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_9 [Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_10[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_11[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_12[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, a, b, c, d, e, f, g] = ???
}

trait In_3_6[In3_6[_,_,_,_,_,_,_,_,_], In3_7[_,_,_,_,_,_,_,_,_,_], In4_6[_,_,_,_,_,_,_,_,_,_], In4_7[_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F] extends NS {
  val e         : In3_7[I1, I2, I3, A, B, C, D, E, F, Long   ] with OneLong   [In3_7[I1, I2, I3, A, B, C, D, E, F, Long   ], In4_7[Long   , I1, I2, I3, A, B, C, D, E, F, Long   ]] = ???
  val a         : In3_7[I1, I2, I3, A, B, C, D, E, F, String ] with OneString [In3_7[I1, I2, I3, A, B, C, D, E, F, String ], In4_7[String , I1, I2, I3, A, B, C, D, E, F, String ]] = ???
  val v         : In3_7[I1, I2, I3, A, B, C, D, E, F, Any    ] with OneAny    [In3_7[I1, I2, I3, A, B, C, D, E, F, Any    ], In4_7[Any    , I1, I2, I3, A, B, C, D, E, F, Any    ]] = ???
  val ns        : In3_7[I1, I2, I3, A, B, C, D, E, F, String ] with OneString [In3_7[I1, I2, I3, A, B, C, D, E, F, String ], In4_7[String , I1, I2, I3, A, B, C, D, E, F, String ]] = ???
  val txInstant : In3_7[I1, I2, I3, A, B, C, D, E, F, Date   ] with OneDate   [In3_7[I1, I2, I3, A, B, C, D, E, F, Date   ], In4_7[Date   , I1, I2, I3, A, B, C, D, E, F, Date   ]] = ???
  val txT       : In3_7[I1, I2, I3, A, B, C, D, E, F, Long   ] with OneLong   [In3_7[I1, I2, I3, A, B, C, D, E, F, Long   ], In4_7[Long   , I1, I2, I3, A, B, C, D, E, F, Long   ]] = ???
  val txAdded   : In3_7[I1, I2, I3, A, B, C, D, E, F, Boolean] with OneBoolean[In3_7[I1, I2, I3, A, B, C, D, E, F, Boolean], In4_7[Boolean, I1, I2, I3, A, B, C, D, E, F, Boolean]] = ???

  val e_         : In3_6[I1, I2, I3, A, B, C, D, E, Long   ] with OneLong   [In3_6[I1, I2, I3, A, B, C, D, E, Long   ], In4_6[Long   , I1, I2, I3, A, B, C, D, E, Long   ]] = ???
  val a_         : In3_6[I1, I2, I3, A, B, C, D, E, String ] with OneString [In3_6[I1, I2, I3, A, B, C, D, E, String ], In4_6[String , I1, I2, I3, A, B, C, D, E, String ]] = ???
  val v_         : In3_6[I1, I2, I3, A, B, C, D, E, Any    ] with OneAny    [In3_6[I1, I2, I3, A, B, C, D, E, Any    ], In4_6[Any    , I1, I2, I3, A, B, C, D, E, Any    ]] = ???
  val ns_        : In3_6[I1, I2, I3, A, B, C, D, E, String ] with OneString [In3_6[I1, I2, I3, A, B, C, D, E, String ], In4_6[String , I1, I2, I3, A, B, C, D, E, String ]] = ???
  val txInstant_ : In3_6[I1, I2, I3, A, B, C, D, E, Date   ] with OneDate   [In3_6[I1, I2, I3, A, B, C, D, E, Date   ], In4_6[Date   , I1, I2, I3, A, B, C, D, E, Date   ]] = ???
  val txT_       : In3_6[I1, I2, I3, A, B, C, D, E, Long   ] with OneLong   [In3_6[I1, I2, I3, A, B, C, D, E, Long   ], In4_6[Long   , I1, I2, I3, A, B, C, D, E, Long   ]] = ???
  val txAdded_   : In3_6[I1, I2, I3, A, B, C, D, E, Boolean] with OneBoolean[In3_6[I1, I2, I3, A, B, C, D, E, Boolean], In4_6[Boolean, I1, I2, I3, A, B, C, D, E, Boolean]] = ???

  def apply(v: min)     : In3_6[I1, I2, I3, A, B, C, D, E, F] = ???
  def apply(v: max)     : In3_6[I1, I2, I3, A, B, C, D, E, F] = ???
  def apply(v: rand)    : In3_6[I1, I2, I3, A, B, C, D, E, F] = ???
  def apply(v: sample)  : In3_6[I1, I2, I3, A, B, C, D, E, F] = ???

  def apply(v: mins)    : In3_6[I1, I2, I3, A, B, C, D, E, Vector[F]] = ???
  def apply(v: maxs)    : In3_6[I1, I2, I3, A, B, C, D, E, Vector[F]] = ???
  def apply(v: distinct): In3_6[I1, I2, I3, A, B, C, D, E, Vector[F]] = ???
  def apply(v: rands)   : In3_6[I1, I2, I3, A, B, C, D, E, Stream[F]] = ???
  def apply(v: samples) : In3_6[I1, I2, I3, A, B, C, D, E, Vector[F]] = ???

  def apply(v: count)        : In3_6[I1, I2, I3, A, B, C, D, E, Long] = ???
  def apply(v: countDistinct): In3_6[I1, I2, I3, A, B, C, D, E, Long] = ???
  def apply(v: sum)          : In3_6[I1, I2, I3, A, B, C, D, E, Double] = ???
  def apply(v: avg)          : In3_6[I1, I2, I3, A, B, C, D, E, Double] = ???
  def apply(v: median)       : In3_6[I1, I2, I3, A, B, C, D, E, Double] = ???
  def apply(v: variance)     : In3_6[I1, I2, I3, A, B, C, D, E, Double] = ???
  def apply(v: stddev)       : In3_6[I1, I2, I3, A, B, C, D, E, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_6 [I1, I2, I3, A, B, C, D, E, F]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_7 [I1, I2, I3, A, B, C, D, E, F, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_8 [Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_9 [Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_10[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_11[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_12[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_13[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, a, b, c, d, e, f, g] = ???
}

trait In_3_7[In3_7[_,_,_,_,_,_,_,_,_,_], In3_8[_,_,_,_,_,_,_,_,_,_,_], In4_7[_,_,_,_,_,_,_,_,_,_,_], In4_8[_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G] extends NS {
  val e         : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Long   ] with OneLong   [In3_8[I1, I2, I3, A, B, C, D, E, F, G, Long   ], In4_8[Long   , I1, I2, I3, A, B, C, D, E, F, G, Long   ]] = ???
  val a         : In3_8[I1, I2, I3, A, B, C, D, E, F, G, String ] with OneString [In3_8[I1, I2, I3, A, B, C, D, E, F, G, String ], In4_8[String , I1, I2, I3, A, B, C, D, E, F, G, String ]] = ???
  val v         : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Any    ] with OneAny    [In3_8[I1, I2, I3, A, B, C, D, E, F, G, Any    ], In4_8[Any    , I1, I2, I3, A, B, C, D, E, F, G, Any    ]] = ???
  val ns        : In3_8[I1, I2, I3, A, B, C, D, E, F, G, String ] with OneString [In3_8[I1, I2, I3, A, B, C, D, E, F, G, String ], In4_8[String , I1, I2, I3, A, B, C, D, E, F, G, String ]] = ???
  val txInstant : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Date   ] with OneDate   [In3_8[I1, I2, I3, A, B, C, D, E, F, G, Date   ], In4_8[Date   , I1, I2, I3, A, B, C, D, E, F, G, Date   ]] = ???
  val txT       : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Long   ] with OneLong   [In3_8[I1, I2, I3, A, B, C, D, E, F, G, Long   ], In4_8[Long   , I1, I2, I3, A, B, C, D, E, F, G, Long   ]] = ???
  val txAdded   : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Boolean] with OneBoolean[In3_8[I1, I2, I3, A, B, C, D, E, F, G, Boolean], In4_8[Boolean, I1, I2, I3, A, B, C, D, E, F, G, Boolean]] = ???

  val e_         : In3_7[I1, I2, I3, A, B, C, D, E, F, Long   ] with OneLong   [In3_7[I1, I2, I3, A, B, C, D, E, F, Long   ], In4_7[Long   , I1, I2, I3, A, B, C, D, E, F, Long   ]] = ???
  val a_         : In3_7[I1, I2, I3, A, B, C, D, E, F, String ] with OneString [In3_7[I1, I2, I3, A, B, C, D, E, F, String ], In4_7[String , I1, I2, I3, A, B, C, D, E, F, String ]] = ???
  val v_         : In3_7[I1, I2, I3, A, B, C, D, E, F, Any    ] with OneAny    [In3_7[I1, I2, I3, A, B, C, D, E, F, Any    ], In4_7[Any    , I1, I2, I3, A, B, C, D, E, F, Any    ]] = ???
  val ns_        : In3_7[I1, I2, I3, A, B, C, D, E, F, String ] with OneString [In3_7[I1, I2, I3, A, B, C, D, E, F, String ], In4_7[String , I1, I2, I3, A, B, C, D, E, F, String ]] = ???
  val txInstant_ : In3_7[I1, I2, I3, A, B, C, D, E, F, Date   ] with OneDate   [In3_7[I1, I2, I3, A, B, C, D, E, F, Date   ], In4_7[Date   , I1, I2, I3, A, B, C, D, E, F, Date   ]] = ???
  val txT_       : In3_7[I1, I2, I3, A, B, C, D, E, F, Long   ] with OneLong   [In3_7[I1, I2, I3, A, B, C, D, E, F, Long   ], In4_7[Long   , I1, I2, I3, A, B, C, D, E, F, Long   ]] = ???
  val txAdded_   : In3_7[I1, I2, I3, A, B, C, D, E, F, Boolean] with OneBoolean[In3_7[I1, I2, I3, A, B, C, D, E, F, Boolean], In4_7[Boolean, I1, I2, I3, A, B, C, D, E, F, Boolean]] = ???

  def apply(v: min)     : In3_7[I1, I2, I3, A, B, C, D, E, F, G] = ???
  def apply(v: max)     : In3_7[I1, I2, I3, A, B, C, D, E, F, G] = ???
  def apply(v: rand)    : In3_7[I1, I2, I3, A, B, C, D, E, F, G] = ???
  def apply(v: sample)  : In3_7[I1, I2, I3, A, B, C, D, E, F, G] = ???

  def apply(v: mins)    : In3_7[I1, I2, I3, A, B, C, D, E, F, Vector[G]] = ???
  def apply(v: maxs)    : In3_7[I1, I2, I3, A, B, C, D, E, F, Vector[G]] = ???
  def apply(v: distinct): In3_7[I1, I2, I3, A, B, C, D, E, F, Vector[G]] = ???
  def apply(v: rands)   : In3_7[I1, I2, I3, A, B, C, D, E, F, Stream[G]] = ???
  def apply(v: samples) : In3_7[I1, I2, I3, A, B, C, D, E, F, Vector[G]] = ???

  def apply(v: count)        : In3_7[I1, I2, I3, A, B, C, D, E, F, Long] = ???
  def apply(v: countDistinct): In3_7[I1, I2, I3, A, B, C, D, E, F, Long] = ???
  def apply(v: sum)          : In3_7[I1, I2, I3, A, B, C, D, E, F, Double] = ???
  def apply(v: avg)          : In3_7[I1, I2, I3, A, B, C, D, E, F, Double] = ???
  def apply(v: median)       : In3_7[I1, I2, I3, A, B, C, D, E, F, Double] = ???
  def apply(v: variance)     : In3_7[I1, I2, I3, A, B, C, D, E, F, Double] = ???
  def apply(v: stddev)       : In3_7[I1, I2, I3, A, B, C, D, E, F, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_7 [I1, I2, I3, A, B, C, D, E, F, G]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_8 [I1, I2, I3, A, B, C, D, E, F, G, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_9 [Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_10[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_11[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_12[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_13[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_14[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, a, b, c, d, e, f, g] = ???
}

trait In_3_8[In3_8[_,_,_,_,_,_,_,_,_,_,_], In3_9[_,_,_,_,_,_,_,_,_,_,_,_], In4_8[_,_,_,_,_,_,_,_,_,_,_,_], In4_9[_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H] extends NS {
  val e         : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long   ] with OneLong   [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long   ], In4_9[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, Long   ]] = ???
  val a         : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String ] with OneString [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String ], In4_9[String , I1, I2, I3, A, B, C, D, E, F, G, H, String ]] = ???
  val v         : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Any    ] with OneAny    [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Any    ], In4_9[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, Any    ]] = ???
  val ns        : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String ] with OneString [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String ], In4_9[String , I1, I2, I3, A, B, C, D, E, F, G, H, String ]] = ???
  val txInstant : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Date   ] with OneDate   [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Date   ], In4_9[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, Date   ]] = ???
  val txT       : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long   ] with OneLong   [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long   ], In4_9[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, Long   ]] = ???
  val txAdded   : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Boolean] with OneBoolean[In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Boolean], In4_9[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, Boolean]] = ???

  val e_         : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Long   ] with OneLong   [In3_8[I1, I2, I3, A, B, C, D, E, F, G, Long   ], In4_8[Long   , I1, I2, I3, A, B, C, D, E, F, G, Long   ]] = ???
  val a_         : In3_8[I1, I2, I3, A, B, C, D, E, F, G, String ] with OneString [In3_8[I1, I2, I3, A, B, C, D, E, F, G, String ], In4_8[String , I1, I2, I3, A, B, C, D, E, F, G, String ]] = ???
  val v_         : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Any    ] with OneAny    [In3_8[I1, I2, I3, A, B, C, D, E, F, G, Any    ], In4_8[Any    , I1, I2, I3, A, B, C, D, E, F, G, Any    ]] = ???
  val ns_        : In3_8[I1, I2, I3, A, B, C, D, E, F, G, String ] with OneString [In3_8[I1, I2, I3, A, B, C, D, E, F, G, String ], In4_8[String , I1, I2, I3, A, B, C, D, E, F, G, String ]] = ???
  val txInstant_ : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Date   ] with OneDate   [In3_8[I1, I2, I3, A, B, C, D, E, F, G, Date   ], In4_8[Date   , I1, I2, I3, A, B, C, D, E, F, G, Date   ]] = ???
  val txT_       : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Long   ] with OneLong   [In3_8[I1, I2, I3, A, B, C, D, E, F, G, Long   ], In4_8[Long   , I1, I2, I3, A, B, C, D, E, F, G, Long   ]] = ???
  val txAdded_   : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Boolean] with OneBoolean[In3_8[I1, I2, I3, A, B, C, D, E, F, G, Boolean], In4_8[Boolean, I1, I2, I3, A, B, C, D, E, F, G, Boolean]] = ???

  def apply(v: min)     : In3_8[I1, I2, I3, A, B, C, D, E, F, G, H] = ???
  def apply(v: max)     : In3_8[I1, I2, I3, A, B, C, D, E, F, G, H] = ???
  def apply(v: rand)    : In3_8[I1, I2, I3, A, B, C, D, E, F, G, H] = ???
  def apply(v: sample)  : In3_8[I1, I2, I3, A, B, C, D, E, F, G, H] = ???

  def apply(v: mins)    : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Vector[H]] = ???
  def apply(v: maxs)    : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Vector[H]] = ???
  def apply(v: distinct): In3_8[I1, I2, I3, A, B, C, D, E, F, G, Vector[H]] = ???
  def apply(v: rands)   : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Stream[H]] = ???
  def apply(v: samples) : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Vector[H]] = ???

  def apply(v: count)        : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Long] = ???
  def apply(v: countDistinct): In3_8[I1, I2, I3, A, B, C, D, E, F, G, Long] = ???
  def apply(v: sum)          : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Double] = ???
  def apply(v: avg)          : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Double] = ???
  def apply(v: median)       : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Double] = ???
  def apply(v: variance)     : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Double] = ???
  def apply(v: stddev)       : In3_8[I1, I2, I3, A, B, C, D, E, F, G, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_8 [I1, I2, I3, A, B, C, D, E, F, G, H]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_9 [I1, I2, I3, A, B, C, D, E, F, G, H, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_10[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_11[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_12[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_13[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_14[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_15[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, a, b, c, d, e, f, g] = ???
}

trait In_3_9[In3_9[_,_,_,_,_,_,_,_,_,_,_,_], In3_10[_,_,_,_,_,_,_,_,_,_,_,_,_], In4_9[_,_,_,_,_,_,_,_,_,_,_,_,_], In4_10[_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I] extends NS {
  val e         : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ], In4_10[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val a         : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String ] with OneString [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String ], In4_10[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, String ]] = ???
  val v         : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Any    ] with OneAny    [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Any    ], In4_10[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, Any    ]] = ???
  val ns        : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String ] with OneString [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String ], In4_10[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, String ]] = ???
  val txInstant : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Date   ] with OneDate   [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Date   ], In4_10[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, Date   ]] = ???
  val txT       : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ], In4_10[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val txAdded   : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Boolean] with OneBoolean[In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Boolean], In4_10[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, Boolean]] = ???

  val e_         : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long   ] with OneLong   [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long   ], In4_9[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, Long   ]] = ???
  val a_         : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String ] with OneString [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String ], In4_9[String , I1, I2, I3, A, B, C, D, E, F, G, H, String ]] = ???
  val v_         : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Any    ] with OneAny    [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Any    ], In4_9[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, Any    ]] = ???
  val ns_        : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String ] with OneString [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String ], In4_9[String , I1, I2, I3, A, B, C, D, E, F, G, H, String ]] = ???
  val txInstant_ : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Date   ] with OneDate   [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Date   ], In4_9[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, Date   ]] = ???
  val txT_       : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long   ] with OneLong   [In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long   ], In4_9[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, Long   ]] = ???
  val txAdded_   : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Boolean] with OneBoolean[In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Boolean], In4_9[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, Boolean]] = ???

  def apply(v: min)     : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I] = ???
  def apply(v: max)     : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I] = ???
  def apply(v: rand)    : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I] = ???
  def apply(v: sample)  : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I] = ???

  def apply(v: mins)    : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Vector[I]] = ???
  def apply(v: maxs)    : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Vector[I]] = ???
  def apply(v: distinct): In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Vector[I]] = ???
  def apply(v: rands)   : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Stream[I]] = ???
  def apply(v: samples) : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Vector[I]] = ???

  def apply(v: count)        : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long] = ???
  def apply(v: countDistinct): In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long] = ???
  def apply(v: sum)          : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: avg)          : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: median)       : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: variance)     : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: stddev)       : In3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_9 [I1, I2, I3, A, B, C, D, E, F, G, H, I]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_11[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_12[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_13[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_14[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_15[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_16[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, a, b, c, d, e, f, g] = ???
}

  trait In_3_10[In3_10[_,_,_,_,_,_,_,_,_,_,_,_,_], In3_11[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_10[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_11[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J] extends NS {
    val e         : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ], In4_11[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
    val a         : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ] with OneString [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ], In4_11[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ]] = ???
    val v         : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Any    ] with OneAny    [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Any    ], In4_11[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Any    ]] = ???
    val ns        : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ] with OneString [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ], In4_11[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ]] = ???
    val txInstant : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Date   ] with OneDate   [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Date   ], In4_11[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Date   ]] = ???
    val txT       : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ], In4_11[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
    val txAdded   : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Boolean] with OneBoolean[In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Boolean], In4_11[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Boolean]] = ???

  val e_         : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ], In4_10[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val a_         : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String ] with OneString [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String ], In4_10[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, String ]] = ???
  val v_         : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Any    ] with OneAny    [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Any    ], In4_10[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, Any    ]] = ???
  val ns_        : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String ] with OneString [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String ], In4_10[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, String ]] = ???
  val txInstant_ : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Date   ] with OneDate   [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Date   ], In4_10[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, Date   ]] = ???
  val txT_       : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ], In4_10[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val txAdded_   : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Boolean] with OneBoolean[In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Boolean], In4_10[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, Boolean]] = ???

    def apply(v: min)     : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: max)     : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: rand)    : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: sample)  : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J] = ???

    def apply(v: mins)    : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Vector[J]] = ???
    def apply(v: maxs)    : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Vector[J]] = ???
    def apply(v: distinct): In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Vector[J]] = ???
    def apply(v: rands)   : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Stream[J]] = ???
    def apply(v: samples) : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Vector[J]] = ???

    def apply(v: count)        : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long] = ???
    def apply(v: countDistinct): In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long] = ???
    def apply(v: sum)          : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: avg)          : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: median)       : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: variance)     : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: stddev)       : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_12[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_13[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_14[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_15[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_16[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_17[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f, g] = ???
}

trait In_3_11[In3_11[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_12[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_11[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_12[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] extends NS {
  val e         : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ], In4_12[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val a         : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ], In4_12[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val v         : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Any    ] with OneAny    [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Any    ], In4_12[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Any    ]] = ???
  val ns        : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ], In4_12[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val txInstant : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Date   ] with OneDate   [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Date   ], In4_12[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Date   ]] = ???
  val txT       : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ], In4_12[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val txAdded   : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Boolean] with OneBoolean[In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Boolean], In4_12[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Boolean]] = ???

  val e_         : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ], In4_11[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
  val a_         : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ] with OneString [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ], In4_11[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ]] = ???
  val v_         : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Any    ] with OneAny    [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Any    ], In4_11[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Any    ]] = ???
  val ns_        : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ] with OneString [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ], In4_11[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String ]] = ???
  val txInstant_ : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Date   ] with OneDate   [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Date   ], In4_11[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Date   ]] = ???
  val txT_       : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ], In4_11[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
  val txAdded_   : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Boolean] with OneBoolean[In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Boolean], In4_11[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Boolean]] = ???

  def apply(v: min)     : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: max)     : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: rand)    : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: sample)  : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] = ???

  def apply(v: mins)    : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???
  def apply(v: maxs)    : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???
  def apply(v: distinct): In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???
  def apply(v: rands)   : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Stream[K]] = ???
  def apply(v: samples) : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???

  def apply(v: count)        : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long] = ???
  def apply(v: countDistinct): In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long] = ???
  def apply(v: sum)          : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: avg)          : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: median)       : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: variance)     : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: stddev)       : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_13[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_14[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_15[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_16[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_17[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_18[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f, g] = ???
}

trait In_3_12[In3_12[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_12[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] extends NS {
  val e         : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In4_13[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val a         : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ], In4_13[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val v         : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Any    ] with OneAny    [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Any    ], In4_13[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Any    ]] = ???
  val ns        : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ], In4_13[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val txInstant : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Date   ] with OneDate   [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Date   ], In4_13[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Date   ]] = ???
  val txT       : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In4_13[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val txAdded   : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Boolean] with OneBoolean[In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Boolean], In4_13[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Boolean]] = ???

  val e_         : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ], In4_12[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val a_         : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ], In4_12[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val v_         : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Any    ] with OneAny    [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Any    ], In4_12[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Any    ]] = ???
  val ns_        : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ], In4_12[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val txInstant_ : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Date   ] with OneDate   [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Date   ], In4_12[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Date   ]] = ???
  val txT_       : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ], In4_12[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val txAdded_   : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Boolean] with OneBoolean[In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Boolean], In4_12[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Boolean]] = ???

  def apply(v: min)     : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: max)     : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: rand)    : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: sample)  : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] = ???

  def apply(v: mins)    : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???
  def apply(v: maxs)    : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???
  def apply(v: distinct): In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???
  def apply(v: rands)   : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Stream[L]] = ???
  def apply(v: samples) : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???

  def apply(v: count)        : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long] = ???
  def apply(v: countDistinct): In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long] = ???
  def apply(v: sum)          : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: avg)          : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: median)       : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: variance)     : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: stddev)       : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Double] = ???


  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_14[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_15[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_16[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_17[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_18[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_19[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f, g] = ???
}

trait In_3_13[In3_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS {
  val e         : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In4_14[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val a         : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In4_14[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val v         : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ] with OneAny    [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ], In4_14[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ]] = ???
  val ns        : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In4_14[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val txInstant : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ] with OneDate   [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ], In4_14[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ]] = ???
  val txT       : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In4_14[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val txAdded   : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean] with OneBoolean[In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean], In4_14[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean]] = ???

  val e_         : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In4_13[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val a_         : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ], In4_13[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val v_         : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Any    ] with OneAny    [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Any    ], In4_13[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Any    ]] = ???
  val ns_        : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ], In4_13[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val txInstant_ : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Date   ] with OneDate   [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Date   ], In4_13[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Date   ]] = ???
  val txT_       : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In4_13[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val txAdded_   : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Boolean] with OneBoolean[In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Boolean], In4_13[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Boolean]] = ???

  def apply(v: min)     : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: max)     : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: rand)    : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: sample)  : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???

  def apply(v: mins)    : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???
  def apply(v: maxs)    : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???
  def apply(v: distinct): In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???
  def apply(v: rands)   : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Stream[M]] = ???
  def apply(v: samples) : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???

  def apply(v: count)        : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long] = ???
  def apply(v: countDistinct): In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long] = ???
  def apply(v: sum)          : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: avg)          : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: median)       : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: variance)     : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: stddev)       : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_15[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_16[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_17[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_18[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_19[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_20[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f, g] = ???
}

trait In_3_14[In3_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS {
  val e         : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In4_15[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val a         : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In4_15[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val v         : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ] with OneAny    [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ], In4_15[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ]] = ???
  val ns        : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In4_15[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val txInstant : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ] with OneDate   [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ], In4_15[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ]] = ???
  val txT       : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In4_15[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val txAdded   : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean] with OneBoolean[In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean], In4_15[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean]] = ???

  val e_         : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In4_14[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val a_         : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In4_14[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val v_         : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ] with OneAny    [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ], In4_14[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ]] = ???
  val ns_        : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In4_14[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val txInstant_ : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ] with OneDate   [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ], In4_14[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ]] = ???
  val txT_       : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In4_14[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val txAdded_   : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean] with OneBoolean[In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean], In4_14[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean]] = ???

  def apply(v: min)     : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: max)     : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: rand)    : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: sample)  : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???

  def apply(v: mins)    : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???
  def apply(v: maxs)    : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???
  def apply(v: distinct): In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???
  def apply(v: rands)   : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Stream[N]] = ???
  def apply(v: samples) : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???

  def apply(v: count)        : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long] = ???
  def apply(v: countDistinct): In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long] = ???
  def apply(v: sum)          : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: avg)          : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: median)       : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: variance)     : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: stddev)       : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_16[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_17[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_18[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_19[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_20[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_21[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f, g] = ???
}

trait In_3_15[In3_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS {
  val e         : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In4_16[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val a         : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In4_16[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val v         : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ] with OneAny    [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ], In4_16[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ]] = ???
  val ns        : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In4_16[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val txInstant : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ] with OneDate   [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ], In4_16[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ]] = ???
  val txT       : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In4_16[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val txAdded   : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean] with OneBoolean[In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean], In4_16[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean]] = ???

  val e_         : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In4_15[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val a_         : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In4_15[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val v_         : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ] with OneAny    [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ], In4_15[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ]] = ???
  val ns_        : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In4_15[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val txInstant_ : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ] with OneDate   [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ], In4_15[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ]] = ???
  val txT_       : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In4_15[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val txAdded_   : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean] with OneBoolean[In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean], In4_15[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean]] = ???

  def apply(v: min)     : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: max)     : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: rand)    : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: sample)  : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???

  def apply(v: mins)    : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???
  def apply(v: maxs)    : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???
  def apply(v: distinct): In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???
  def apply(v: rands)   : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Stream[O]] = ???
  def apply(v: samples) : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???

  def apply(v: count)        : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long] = ???
  def apply(v: countDistinct): In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long] = ???
  def apply(v: sum)          : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: avg)          : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: median)       : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: variance)     : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: stddev)       : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_17[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_18[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_19[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_20[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_21[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in3_7[_,_,_,_,_,_,_,_], in3_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in3_7, in3_8, a, b, c, d, e, f, g]): In_3_22[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f, g] = ???
}

trait In_3_16[In3_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS {
  val e         : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In4_17[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val a         : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In4_17[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val v         : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ] with OneAny    [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ], In4_17[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ]] = ???
  val ns        : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In4_17[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val txInstant : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ] with OneDate   [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ], In4_17[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ]] = ???
  val txT       : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In4_17[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val txAdded   : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean] with OneBoolean[In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean], In4_17[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean]] = ???

  val e_         : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In4_16[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val a_         : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In4_16[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val v_         : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ] with OneAny    [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ], In4_16[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ]] = ???
  val ns_        : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In4_16[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val txInstant_ : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ] with OneDate   [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ], In4_16[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ]] = ???
  val txT_       : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In4_16[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val txAdded_   : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean] with OneBoolean[In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean], In4_16[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean]] = ???

  def apply(v: min)     : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: max)     : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: rand)    : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: sample)  : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???

  def apply(v: mins)    : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???
  def apply(v: maxs)    : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???
  def apply(v: distinct): In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???
  def apply(v: rands)   : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Stream[P]] = ???
  def apply(v: samples) : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???

  def apply(v: count)        : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long] = ???
  def apply(v: countDistinct): In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long] = ???
  def apply(v: sum)          : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: avg)          : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: median)       : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: variance)     : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: stddev)       : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]                                    = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a]                                 = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_18[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_19[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_20[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d]       = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_21[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e]    = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in3_6[_,_,_,_,_,_,_]  , in3_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in3_6, in3_7, a, b, c, d, e, f])   : In_3_22[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e, f] = ???
}

trait In_3_17[In3_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS {
  val e         : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In4_18[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val a         : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In4_18[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val v         : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ] with OneAny    [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ], In4_18[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ]] = ???
  val ns        : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In4_18[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val txInstant : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ] with OneDate   [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ], In4_18[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ]] = ???
  val txT       : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In4_18[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val txAdded   : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean] with OneBoolean[In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean], In4_18[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean]] = ???

  val e_         : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In4_17[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val a_         : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In4_17[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val v_         : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ] with OneAny    [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ], In4_17[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ]] = ???
  val ns_        : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In4_17[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val txInstant_ : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ] with OneDate   [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ], In4_17[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ]] = ???
  val txT_       : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In4_17[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val txAdded_   : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean] with OneBoolean[In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean], In4_17[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean]] = ???

  def apply(v: min)     : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: max)     : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: rand)    : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: sample)  : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???

  def apply(v: mins)    : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???
  def apply(v: maxs)    : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???
  def apply(v: distinct): In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???
  def apply(v: rands)   : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Stream[Q]] = ???
  def apply(v: samples) : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???

  def apply(v: count)        : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long] = ???
  def apply(v: countDistinct): In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long] = ???
  def apply(v: sum)          : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: avg)          : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: median)       : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: variance)     : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: stddev)       : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_19[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_20[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_21[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d]       = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in3_5[_,_,_,_,_,_]    , in3_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in3_5, in3_6, a, b, c, d, e])      : In_3_22[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d, e]    = ???
}

trait In_3_18[In3_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS {
  val e         : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In4_19[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val a         : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In4_19[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val v         : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ] with OneAny    [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ], In4_19[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ]] = ???
  val ns        : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In4_19[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val txInstant : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ] with OneDate   [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ], In4_19[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ]] = ???
  val txT       : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In4_19[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val txAdded   : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean] with OneBoolean[In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean], In4_19[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean]] = ???

  val e_         : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In4_18[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val a_         : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In4_18[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val v_         : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ] with OneAny    [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ], In4_18[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ]] = ???
  val ns_        : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In4_18[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val txInstant_ : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ] with OneDate   [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ], In4_18[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ]] = ???
  val txT_       : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In4_18[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val txAdded_   : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean] with OneBoolean[In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean], In4_18[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean]] = ???

  def apply(v: min)     : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: max)     : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: rand)    : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: sample)  : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???

  def apply(v: mins)    : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???
  def apply(v: maxs)    : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???
  def apply(v: distinct): In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???
  def apply(v: rands)   : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Stream[R]] = ???
  def apply(v: samples) : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???

  def apply(v: count)        : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long] = ???
  def apply(v: countDistinct): In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long] = ???
  def apply(v: sum)          : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: avg)          : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: median)       : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: variance)     : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: stddev)       : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_20[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_21[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in3_4[_,_,_,_,_]      , in3_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in3_4, in3_5, a, b, c, d])         : In_3_22[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c, d]       = ???
}

trait In_3_19[In3_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS {
  val e         : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In4_20[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val a         : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In4_20[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val v         : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ] with OneAny    [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ], In4_20[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ]] = ???
  val ns        : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In4_20[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val txInstant : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ] with OneDate   [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ], In4_20[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ]] = ???
  val txT       : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In4_20[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val txAdded   : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean] with OneBoolean[In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean], In4_20[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean]] = ???

  val e_         : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In4_19[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val a_         : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In4_19[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val v_         : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ] with OneAny    [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ], In4_19[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ]] = ???
  val ns_        : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In4_19[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val txInstant_ : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ] with OneDate   [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ], In4_19[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ]] = ???
  val txT_       : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In4_19[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val txAdded_   : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean] with OneBoolean[In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean], In4_19[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean]] = ???

  def apply(v: min)     : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: max)     : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: rand)    : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: sample)  : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???

  def apply(v: mins)    : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???
  def apply(v: maxs)    : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???
  def apply(v: distinct): In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???
  def apply(v: rands)   : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Stream[S]] = ???
  def apply(v: samples) : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???

  def apply(v: count)        : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long] = ???
  def apply(v: countDistinct): In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long] = ???
  def apply(v: sum)          : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: avg)          : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: median)       : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: variance)     : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: stddev)       : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_21[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in3_3[_,_,_,_]        , in3_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in3_3, in3_4, a, b, c])            : In_3_22[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b, c]          = ???
}

trait In_3_20[In3_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS {
  val e         : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In4_21[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val a         : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In4_21[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val v         : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ] with OneAny    [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ], In4_21[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ]] = ???
  val ns        : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In4_21[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val txInstant : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ] with OneDate   [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ], In4_21[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ]] = ???
  val txT       : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In4_21[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val txAdded   : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean] with OneBoolean[In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean], In4_21[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean]] = ???

  val e_         : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In4_20[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val a_         : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In4_20[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val v_         : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ] with OneAny    [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ], In4_20[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ]] = ???
  val ns_        : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In4_20[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val txInstant_ : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ] with OneDate   [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ], In4_20[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ]] = ???
  val txT_       : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In4_20[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val txAdded_   : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean] with OneBoolean[In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean], In4_20[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean]] = ???

  def apply(v: min)     : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: max)     : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: rand)    : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: sample)  : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???

  def apply(v: mins)    : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???
  def apply(v: maxs)    : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???
  def apply(v: distinct): In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???
  def apply(v: rands)   : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Stream[T]] = ???
  def apply(v: samples) : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???

  def apply(v: count)        : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long] = ???
  def apply(v: countDistinct): In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long] = ???
  def apply(v: sum)          : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: avg)          : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: median)       : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: variance)     : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: stddev)       : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in3_2[_,_,_]          , in3_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in3_2, in3_3, a, b])               : In_3_22[Any, Any, Any, Any, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a, b]             = ???
}

trait In_3_21[In3_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS {
  val e         : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], In4_22[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ]] = ???
  val a         : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], In4_22[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ]] = ???
  val v         : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], In4_22[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ]] = ???
  val ns        : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], In4_22[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ]] = ???
  val txInstant : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], In4_22[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ]] = ???
  val txT       : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], In4_22[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ]] = ???
  val txAdded   : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], In4_22[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean]] = ???

  val e_         : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In4_21[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val a_         : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In4_21[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val v_         : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ] with OneAny    [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ], In4_21[Any    , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ]] = ???
  val ns_        : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In4_21[String , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val txInstant_ : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ] with OneDate   [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ], In4_21[Date   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ]] = ???
  val txT_       : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In4_21[Long   , I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val txAdded_   : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean] with OneBoolean[In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean], In4_21[Boolean, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean]] = ???

  def apply(v: min)     : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: max)     : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: rand)    : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: sample)  : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???

  def apply(v: mins)    : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???
  def apply(v: maxs)    : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???
  def apply(v: distinct): In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???
  def apply(v: rands)   : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Stream[U]] = ???
  def apply(v: samples) : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???

  def apply(v: count)        : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long] = ???
  def apply(v: countDistinct): In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long] = ???
  def apply(v: sum)          : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: avg)          : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: median)       : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: variance)     : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: stddev)       : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in3_1[_,_]            , in3_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in3_1, in3_2, a])                  : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, a]                                           = ???
}

trait In_3_22[In3_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], P26[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In4_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], P27[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS {
  val e_         : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val a_         : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val v_         : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], Nothing] = ???
  val ns_        : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val txInstant_ : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], Nothing] = ???
  val txT_       : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val txAdded_   : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], Nothing] = ???

  def tx[ns0               , ns1[_]              , in3_0[_]              , in3_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in3_0, in3_1])                     : In3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]                                              = ???
}
