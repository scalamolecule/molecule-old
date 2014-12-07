package molecule
package in

import molecule.out._
import java.util.Date
import molecule.dsl.schemaDSL.NS
import molecule.dsl.schemaDSL._
import scala.language.higherKinds


trait In_2_0[In2_0[_,_], In2_1[_,_,_], In3_0[_,_,_], In3_1[_,_,_,_], I1, I2] extends NS {
  val e          : In2_1[I1, I2, Long   ] with OneLong   [In2_1[I1, I2, Long   ], In3_1[Long   , I1, I2, Long   ]] = ???
  val a          : In2_1[I1, I2, String ] with OneString [In2_1[I1, I2, String ], In3_1[String , I1, I2, String ]] = ???
  val v          : In2_1[I1, I2, Any    ] with OneAny    [In2_1[I1, I2, Any    ], In3_1[Any    , I1, I2, Any    ]] = ???
  val ns         : In2_1[I1, I2, String ] with OneString [In2_1[I1, I2, String ], In3_1[String , I1, I2, String ]] = ???
  val txInstant  : In2_1[I1, I2, Date   ] with OneDate   [In2_1[I1, I2, Date   ], In3_1[Date   , I1, I2, Date   ]] = ???
  val txT        : In2_1[I1, I2, Long   ] with OneLong   [In2_1[I1, I2, Long   ], In3_1[Long   , I1, I2, Long   ]] = ???
  val txAdded    : In2_1[I1, I2, Boolean] with OneBoolean[In2_1[I1, I2, Boolean], In3_1[Boolean, I1, I2, Boolean]] = ???

  val e_         : In2_0[I1, I2] with OneLong   [In2_0[I1, I2], In3_0[Long   , I1, Long   ]] = ???
  val a_         : In2_0[I1, I2] with OneString [In2_0[I1, I2], In3_0[String , I1, String ]] = ???
  val v_         : In2_0[I1, I2] with OneAny    [In2_0[I1, I2], In3_0[Any    , I1, Any    ]] = ???
  val ns_        : In2_0[I1, I2] with OneString [In2_0[I1, I2], In3_0[String , I1, String ]] = ???
  val txInstant_ : In2_0[I1, I2] with OneDate   [In2_0[I1, I2], In3_0[Date   , I1, Date   ]] = ???
  val txT_       : In2_0[I1, I2] with OneLong   [In2_0[I1, I2], In3_0[Long   , I1, Long   ]] = ???
  val txAdded_   : In2_0[I1, I2] with OneBoolean[In2_0[I1, I2], In3_0[Boolean, I1, Boolean]] = ???

  // If we supply 2 or more tx attributes we return a generic molecule
  // This means that you can't continue expanding the molecule from the initial namespace anymore, so you'll
  // want to have the tx data defined in the end of the molecule
  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_0[I1, I2]                                                 = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_1[I1, I2, a]                                              = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_2[Any, Any, Any, Any, I1, I2, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_3[Any, Any, Any, Any, I1, I2, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_4[Any, Any, Any, Any, I1, I2, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_5[Any, Any, Any, Any, I1, I2, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_6[Any, Any, Any, Any, I1, I2, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_7[Any, Any, Any, Any, I1, I2, a, b, c, d, e, f, g] = ???
}

trait In_2_1[In2_1[_,_,_], In2_2[_,_,_,_], In3_1[_,_,_,_], In3_2[_,_,_,_,_], I1, I2, A] extends NS {
  val e         : In2_2[I1, I2, A, Long   ] with OneLong   [In2_2[I1, I2, A, Long   ], In3_2[Long   , I1, I2, A,Long   ]] = ???
  val a         : In2_2[I1, I2, A, String ] with OneString [In2_2[I1, I2, A, String ], In3_2[String , I1, I2, A,String ]] = ???
  val v         : In2_2[I1, I2, A, Any    ] with OneAny    [In2_2[I1, I2, A, Any    ], In3_2[Any    , I1, I2, A,Any    ]] = ???
  val ns        : In2_2[I1, I2, A, String ] with OneString [In2_2[I1, I2, A, String ], In3_2[String , I1, I2, A,String ]] = ???
  val txInstant : In2_2[I1, I2, A, Date   ] with OneDate   [In2_2[I1, I2, A, Date   ], In3_2[Date   , I1, I2, A,Date   ]] = ???
  val txT       : In2_2[I1, I2, A, Long   ] with OneLong   [In2_2[I1, I2, A, Long   ], In3_2[Long   , I1, I2, A,Long   ]] = ???
  val txAdded   : In2_2[I1, I2, A, Boolean] with OneBoolean[In2_2[I1, I2, A, Boolean], In3_2[Boolean, I1, I2, A, Boolean]] = ???

  val e_          : In2_1[I1, I2, A] with OneLong   [In2_1[I1, I2, A], In3_1[Long   , I1, I2, A]] = ???
  val a_          : In2_1[I1, I2, A] with OneString [In2_1[I1, I2, A], In3_1[String , I1, I2, A]] = ???
  val v_          : In2_1[I1, I2, A] with OneAny    [In2_1[I1, I2, A], In3_1[Any    , I1, I2, A]] = ???
  val ns_         : In2_1[I1, I2, A] with OneString [In2_1[I1, I2, A], In3_1[String , I1, I2, A]] = ???
  val txInstant_  : In2_1[I1, I2, A] with OneDate   [In2_1[I1, I2, A], In3_1[Date   , I1, I2, A]] = ???
  val txT_        : In2_1[I1, I2, A] with OneLong   [In2_1[I1, I2, A], In3_1[Long   , I1, I2, A]] = ???
  val txAdded_    : In2_1[I1, I2, A] with OneBoolean[In2_1[I1, I2, A], In3_1[Boolean, I1, I2, A]] = ???

  def apply(v: min)     : In2_1[I1, I2, A] = ???
  def apply(v: max)     : In2_1[I1, I2, A] = ???
  def apply(v: rand)    : In2_1[I1, I2, A] = ???
  def apply(v: sample)  : In2_1[I1, I2, A] = ???

  def apply(v: mins)    : In2_1[I1, I2, Vector[A]] = ???
  def apply(v: maxs)    : In2_1[I1, I2, Vector[A]] = ???
  def apply(v: distinct): In2_1[I1, I2, Vector[A]] = ???
  def apply(v: rands)   : In2_1[I1, I2, Stream[A]] = ???
  def apply(v: samples) : In2_1[I1, I2, Vector[A]] = ???

  def apply(v: count)        : In2_1[I1, I2, Int] = ???
  def apply(v: countDistinct): In2_1[I1, I2, Int] = ???
  def apply(v: sum)          : In2_1[I1, I2, Double] = ???
  def apply(v: avg)          : In2_1[I1, I2, Double] = ???
  def apply(v: median)       : In2_1[I1, I2, Double] = ???
  def apply(v: variance)     : In2_1[I1, I2, Double] = ???
  def apply(v: stddev)       : In2_1[I1, I2, Double] = ???

  val length:  In2_1[I1, I2, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_1[I1, I2, A]                                                 = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_2[I1, I2, A, a]                                              = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_3[Any, Any, Any, Any, I1, I2, A, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_4[Any, Any, Any, Any, I1, I2, A, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_5[Any, Any, Any, Any, I1, I2, A, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_6[Any, Any, Any, Any, I1, I2, A, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_7[Any, Any, Any, Any, I1, I2, A, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_8[Any, Any, Any, Any, I1, I2, A, a, b, c, d, e, f, g] = ???
}

trait In_2_2[In2_2[_,_,_,_], In2_3[_,_,_,_,_], In3_2[_,_,_,_,_], In3_3[_,_,_,_,_,_], I1, I2, A, B] extends NS {
  val e         : In2_3[I1, I2, A, B, Long   ] with OneLong   [In2_3[I1, I2, A, B, Long   ], In3_3[Long   , I1, I2, A, B, Long   ]] = ???
  val a         : In2_3[I1, I2, A, B, String ] with OneString [In2_3[I1, I2, A, B, String ], In3_3[String , I1, I2, A, B, String ]] = ???
  val v         : In2_3[I1, I2, A, B, Any    ] with OneAny    [In2_3[I1, I2, A, B, Any    ], In3_3[Any    , I1, I2, A, B, Any    ]] = ???
  val ns        : In2_3[I1, I2, A, B, String ] with OneString [In2_3[I1, I2, A, B, String ], In3_3[String , I1, I2, A, B, String ]] = ???
  val txInstant : In2_3[I1, I2, A, B, Date   ] with OneDate   [In2_3[I1, I2, A, B, Date   ], In3_3[Date   , I1, I2, A, B, Date   ]] = ???
  val txT       : In2_3[I1, I2, A, B, Long   ] with OneLong   [In2_3[I1, I2, A, B, Long   ], In3_3[Long   , I1, I2, A, B, Long   ]] = ???
  val txAdded   : In2_3[I1, I2, A, B, Boolean] with OneBoolean[In2_3[I1, I2, A, B, Boolean], In3_3[Boolean, I1, I2, A, B, Boolean]] = ???

  val e_         : In2_2[I1, I2, A, B] with OneLong   [In2_2[I1, I2, A, B], In3_2[Long   , I1, I2, A, B]] = ???
  val a_         : In2_2[I1, I2, A, B] with OneString [In2_2[I1, I2, A, B], In3_2[String , I1, I2, A, B]] = ???
  val v_         : In2_2[I1, I2, A, B] with OneAny    [In2_2[I1, I2, A, B], In3_2[Any    , I1, I2, A, B]] = ???
  val ns_        : In2_2[I1, I2, A, B] with OneString [In2_2[I1, I2, A, B], In3_2[String , I1, I2, A, B]] = ???
  val txInstant_ : In2_2[I1, I2, A, B] with OneDate   [In2_2[I1, I2, A, B], In3_2[Date   , I1, I2, A, B]] = ???
  val txT_       : In2_2[I1, I2, A, B] with OneLong   [In2_2[I1, I2, A, B], In3_2[Long   , I1, I2, A, B]] = ???
  val txAdded_   : In2_2[I1, I2, A, B] with OneBoolean[In2_2[I1, I2, A, B], In3_2[Boolean, I1, I2, A, B]] = ???

  def apply(v: min)     : In2_2[I1, I2, A, B] = ???
  def apply(v: max)     : In2_2[I1, I2, A, B] = ???
  def apply(v: rand)    : In2_2[I1, I2, A, B] = ???
  def apply(v: sample)  : In2_2[I1, I2, A, B] = ???

  def apply(v: mins)    : In2_2[I1, I2, A, Vector[B]] = ???
  def apply(v: maxs)    : In2_2[I1, I2, A, Vector[B]] = ???
  def apply(v: distinct): In2_2[I1, I2, A, Vector[B]] = ???
  def apply(v: rands)   : In2_2[I1, I2, A, Stream[B]] = ???
  def apply(v: samples) : In2_2[I1, I2, A, Vector[B]] = ???

  def apply(v: count)        : In2_2[I1, I2, A, Int] = ???
  def apply(v: countDistinct): In2_2[I1, I2, A, Int] = ???
  def apply(v: sum)          : In2_2[I1, I2, A, Double] = ???
  def apply(v: avg)          : In2_2[I1, I2, A, Double] = ???
  def apply(v: median)       : In2_2[I1, I2, A, Double] = ???
  def apply(v: variance)     : In2_2[I1, I2, A, Double] = ???
  def apply(v: stddev)       : In2_2[I1, I2, A, Double] = ???

  val length:  In2_2[I1, I2, A, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_2[I1, I2, A, B]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_3[I1, I2, A, B, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_4[Any, Any, Any, Any, I1, I2, A, B, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_5[Any, Any, Any, Any, I1, I2, A, B, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_6[Any, Any, Any, Any, I1, I2, A, B, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_7[Any, Any, Any, Any, I1, I2, A, B, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_8[Any, Any, Any, Any, I1, I2, A, B, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_9[Any, Any, Any, Any, I1, I2, A, B, a, b, c, d, e, f, g] = ???
}

trait In_2_3[In2_3[_,_,_,_,_], In2_4[_,_,_,_,_,_], In3_3[_,_,_,_,_,_], In3_4[_,_,_,_,_,_,_], I1, I2, A, B, C] extends NS {
  val e         : In2_4[I1, I2, A, B, C, Long   ] with OneLong   [In2_4[I1, I2, A, B, C, Long   ], In3_4[Long   , I1, I2, A, B, C, Long   ]] = ???
  val a         : In2_4[I1, I2, A, B, C, String ] with OneString [In2_4[I1, I2, A, B, C, String ], In3_4[String , I1, I2, A, B, C, String ]] = ???
  val v         : In2_4[I1, I2, A, B, C, Any    ] with OneAny    [In2_4[I1, I2, A, B, C, Any    ], In3_4[Any    , I1, I2, A, B, C, Any    ]] = ???
  val ns        : In2_4[I1, I2, A, B, C, String ] with OneString [In2_4[I1, I2, A, B, C, String ], In3_4[String , I1, I2, A, B, C, String ]] = ???
  val txInstant : In2_4[I1, I2, A, B, C, Date   ] with OneDate   [In2_4[I1, I2, A, B, C, Date   ], In3_4[Date   , I1, I2, A, B, C, Date   ]] = ???
  val txT       : In2_4[I1, I2, A, B, C, Long   ] with OneLong   [In2_4[I1, I2, A, B, C, Long   ], In3_4[Long   , I1, I2, A, B, C, Long   ]] = ???
  val txAdded   : In2_4[I1, I2, A, B, C, Boolean] with OneBoolean[In2_4[I1, I2, A, B, C, Boolean], In3_4[Boolean, I1, I2, A, B, C, Boolean]] = ???

  val e_         : In2_3[I1, I2, A, B, C] with OneLong   [In2_3[I1, I2, A, B, C], In3_3[Long   , I1, I2, A, B, C]] = ???
  val a_         : In2_3[I1, I2, A, B, C] with OneString [In2_3[I1, I2, A, B, C], In3_3[String , I1, I2, A, B, C]] = ???
  val v_         : In2_3[I1, I2, A, B, C] with OneAny    [In2_3[I1, I2, A, B, C], In3_3[Any    , I1, I2, A, B, C]] = ???
  val ns_        : In2_3[I1, I2, A, B, C] with OneString [In2_3[I1, I2, A, B, C], In3_3[String , I1, I2, A, B, C]] = ???
  val txInstant_ : In2_3[I1, I2, A, B, C] with OneDate   [In2_3[I1, I2, A, B, C], In3_3[Date   , I1, I2, A, B, C]] = ???
  val txT_       : In2_3[I1, I2, A, B, C] with OneLong   [In2_3[I1, I2, A, B, C], In3_3[Long   , I1, I2, A, B, C]] = ???
  val txAdded_   : In2_3[I1, I2, A, B, C] with OneBoolean[In2_3[I1, I2, A, B, C], In3_3[Boolean, I1, I2, A, B, C]] = ???

  def apply(v: min)     : In2_3[I1, I2, A, B, C] = ???
  def apply(v: max)     : In2_3[I1, I2, A, B, C] = ???
  def apply(v: rand)    : In2_3[I1, I2, A, B, C] = ???
  def apply(v: sample)  : In2_3[I1, I2, A, B, C] = ???

  def apply(v: mins)    : In2_3[I1, I2, A, B, Vector[C]] = ???
  def apply(v: maxs)    : In2_3[I1, I2, A, B, Vector[C]] = ???
  def apply(v: distinct): In2_3[I1, I2, A, B, Vector[C]] = ???
  def apply(v: rands)   : In2_3[I1, I2, A, B, Stream[C]] = ???
  def apply(v: samples) : In2_3[I1, I2, A, B, Vector[C]] = ???

  def apply(v: count)        : In2_3[I1, I2, A, B, Int] = ???
  def apply(v: countDistinct): In2_3[I1, I2, A, B, Int] = ???
  def apply(v: sum)          : In2_3[I1, I2, A, B, Double] = ???
  def apply(v: avg)          : In2_3[I1, I2, A, B, Double] = ???
  def apply(v: median)       : In2_3[I1, I2, A, B, Double] = ???
  def apply(v: variance)     : In2_3[I1, I2, A, B, Double] = ???
  def apply(v: stddev)       : In2_3[I1, I2, A, B, Double] = ???

  val length:  In2_3[I1, I2, A, B, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_3 [I1, I2, A, B, C]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_4 [I1, I2, A, B, C, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_5 [Any, Any, Any, Any, I1, I2, A, B, C, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_6 [Any, Any, Any, Any, I1, I2, A, B, C, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_7 [Any, Any, Any, Any, I1, I2, A, B, C, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_8 [Any, Any, Any, Any, I1, I2, A, B, C, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_9 [Any, Any, Any, Any, I1, I2, A, B, C, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_10[Any, Any, Any, Any, I1, I2, A, B, C, a, b, c, d, e, f, g] = ???
}

trait In_2_4[In2_4[_,_,_,_,_,_], In2_5[_,_,_,_,_,_,_], In3_4[_,_,_,_,_,_,_], In3_5[_,_,_,_,_,_,_,_], I1, I2, A, B, C, D] extends NS {
  val e         : In2_5[I1, I2, A, B, C, D, Long   ] with OneLong   [In2_5[I1, I2, A, B, C, D, Long   ], In3_5[Long   , I1, I2, A, B, C, D, Long   ]] = ???
  val a         : In2_5[I1, I2, A, B, C, D, String ] with OneString [In2_5[I1, I2, A, B, C, D, String ], In3_5[String , I1, I2, A, B, C, D, String ]] = ???
  val v         : In2_5[I1, I2, A, B, C, D, Any    ] with OneAny    [In2_5[I1, I2, A, B, C, D, Any    ], In3_5[Any    , I1, I2, A, B, C, D, Any    ]] = ???
  val ns        : In2_5[I1, I2, A, B, C, D, String ] with OneString [In2_5[I1, I2, A, B, C, D, String ], In3_5[String , I1, I2, A, B, C, D, String ]] = ???
  val txInstant : In2_5[I1, I2, A, B, C, D, Date   ] with OneDate   [In2_5[I1, I2, A, B, C, D, Date   ], In3_5[Date   , I1, I2, A, B, C, D, Date   ]] = ???
  val txT       : In2_5[I1, I2, A, B, C, D, Long   ] with OneLong   [In2_5[I1, I2, A, B, C, D, Long   ], In3_5[Long   , I1, I2, A, B, C, D, Long   ]] = ???
  val txAdded   : In2_5[I1, I2, A, B, C, D, Boolean] with OneBoolean[In2_5[I1, I2, A, B, C, D, Boolean], In3_5[Boolean, I1, I2, A, B, C, D, Boolean]] = ???

  val e_         : In2_4[I1, I2, A, B, C, D] with OneLong   [In2_4[I1, I2, A, B, C, D], In3_4[Long   , I1, I2, A, B, C, D]] = ???
  val a_         : In2_4[I1, I2, A, B, C, D] with OneString [In2_4[I1, I2, A, B, C, D], In3_4[String , I1, I2, A, B, C, D]] = ???
  val v_         : In2_4[I1, I2, A, B, C, D] with OneAny    [In2_4[I1, I2, A, B, C, D], In3_4[Any    , I1, I2, A, B, C, D]] = ???
  val ns_        : In2_4[I1, I2, A, B, C, D] with OneString [In2_4[I1, I2, A, B, C, D], In3_4[String , I1, I2, A, B, C, D]] = ???
  val txInstant_ : In2_4[I1, I2, A, B, C, D] with OneDate   [In2_4[I1, I2, A, B, C, D], In3_4[Date   , I1, I2, A, B, C, D]] = ???
  val txT_       : In2_4[I1, I2, A, B, C, D] with OneLong   [In2_4[I1, I2, A, B, C, D], In3_4[Long   , I1, I2, A, B, C, D]] = ???
  val txAdded_   : In2_4[I1, I2, A, B, C, D] with OneBoolean[In2_4[I1, I2, A, B, C, D], In3_4[Boolean, I1, I2, A, B, C, D]] = ???

  def apply(v: min)     : In2_4[I1, I2, A, B, C, D] = ???
  def apply(v: max)     : In2_4[I1, I2, A, B, C, D] = ???
  def apply(v: rand)    : In2_4[I1, I2, A, B, C, D] = ???
  def apply(v: sample)  : In2_4[I1, I2, A, B, C, D] = ???

  def apply(v: mins)    : In2_4[I1, I2, A, B, C, Vector[D]] = ???
  def apply(v: maxs)    : In2_4[I1, I2, A, B, C, Vector[D]] = ???
  def apply(v: distinct): In2_4[I1, I2, A, B, C, Vector[D]] = ???
  def apply(v: rands)   : In2_4[I1, I2, A, B, C, Stream[D]] = ???
  def apply(v: samples) : In2_4[I1, I2, A, B, C, Vector[D]] = ???

  def apply(v: count)        : In2_4[I1, I2, A, B, C, Int] = ???
  def apply(v: countDistinct): In2_4[I1, I2, A, B, C, Int] = ???
  def apply(v: sum)          : In2_4[I1, I2, A, B, C, Double] = ???
  def apply(v: avg)          : In2_4[I1, I2, A, B, C, Double] = ???
  def apply(v: median)       : In2_4[I1, I2, A, B, C, Double] = ???
  def apply(v: variance)     : In2_4[I1, I2, A, B, C, Double] = ???
  def apply(v: stddev)       : In2_4[I1, I2, A, B, C, Double] = ???

  val length:  In2_4[I1, I2, A, B, C, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_4 [I1, I2, A, B, C, D]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_5 [I1, I2, A, B, C, D, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_6 [Any, Any, Any, Any, I1, I2, A, B, C, D, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_7 [Any, Any, Any, Any, I1, I2, A, B, C, D, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_8 [Any, Any, Any, Any, I1, I2, A, B, C, D, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_9 [Any, Any, Any, Any, I1, I2, A, B, C, D, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_10[Any, Any, Any, Any, I1, I2, A, B, C, D, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_11[Any, Any, Any, Any, I1, I2, A, B, C, D, a, b, c, d, e, f, g] = ???
}

trait In_2_5[In2_5[_,_,_,_,_,_,_], In2_6[_,_,_,_,_,_,_,_], In3_5[_,_,_,_,_,_,_,_], In3_6[_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E] extends NS {
  val e         : In2_6[I1, I2, A, B, C, D, E, Long   ] with OneLong   [In2_6[I1, I2, A, B, C, D, E, Long   ], In3_6[Long   , I1, I2, A, B, C, D, E, Long   ]] = ???
  val a         : In2_6[I1, I2, A, B, C, D, E, String ] with OneString [In2_6[I1, I2, A, B, C, D, E, String ], In3_6[String , I1, I2, A, B, C, D, E, String ]] = ???
  val v         : In2_6[I1, I2, A, B, C, D, E, Any    ] with OneAny    [In2_6[I1, I2, A, B, C, D, E, Any    ], In3_6[Any    , I1, I2, A, B, C, D, E, Any    ]] = ???
  val ns        : In2_6[I1, I2, A, B, C, D, E, String ] with OneString [In2_6[I1, I2, A, B, C, D, E, String ], In3_6[String , I1, I2, A, B, C, D, E, String ]] = ???
  val txInstant : In2_6[I1, I2, A, B, C, D, E, Date   ] with OneDate   [In2_6[I1, I2, A, B, C, D, E, Date   ], In3_6[Date   , I1, I2, A, B, C, D, E, Date   ]] = ???
  val txT       : In2_6[I1, I2, A, B, C, D, E, Long   ] with OneLong   [In2_6[I1, I2, A, B, C, D, E, Long   ], In3_6[Long   , I1, I2, A, B, C, D, E, Long   ]] = ???
  val txAdded   : In2_6[I1, I2, A, B, C, D, E, Boolean] with OneBoolean[In2_6[I1, I2, A, B, C, D, E, Boolean], In3_6[Boolean, I1, I2, A, B, C, D, E, Boolean]] = ???

  val e_         : In2_5[I1, I2, A, B, C, D, E] with OneLong   [In2_5[I1, I2, A, B, C, D, E], In3_5[Long   , I1, I2, A, B, C, D, E]] = ???
  val a_         : In2_5[I1, I2, A, B, C, D, E] with OneString [In2_5[I1, I2, A, B, C, D, E], In3_5[String , I1, I2, A, B, C, D, E]] = ???
  val v_         : In2_5[I1, I2, A, B, C, D, E] with OneAny    [In2_5[I1, I2, A, B, C, D, E], In3_5[Any    , I1, I2, A, B, C, D, E]] = ???
  val ns_        : In2_5[I1, I2, A, B, C, D, E] with OneString [In2_5[I1, I2, A, B, C, D, E], In3_5[String , I1, I2, A, B, C, D, E]] = ???
  val txInstant_ : In2_5[I1, I2, A, B, C, D, E] with OneDate   [In2_5[I1, I2, A, B, C, D, E], In3_5[Date   , I1, I2, A, B, C, D, E]] = ???
  val txT_       : In2_5[I1, I2, A, B, C, D, E] with OneLong   [In2_5[I1, I2, A, B, C, D, E], In3_5[Long   , I1, I2, A, B, C, D, E]] = ???
  val txAdded_   : In2_5[I1, I2, A, B, C, D, E] with OneBoolean[In2_5[I1, I2, A, B, C, D, E], In3_5[Boolean, I1, I2, A, B, C, D, E]] = ???

  def apply(v: min)     : In2_5[I1, I2, A, B, C, D, E] = ???
  def apply(v: max)     : In2_5[I1, I2, A, B, C, D, E] = ???
  def apply(v: rand)    : In2_5[I1, I2, A, B, C, D, E] = ???
  def apply(v: sample)  : In2_5[I1, I2, A, B, C, D, E] = ???

  def apply(v: mins)    : In2_5[I1, I2, A, B, C, D, Vector[E]] = ???
  def apply(v: maxs)    : In2_5[I1, I2, A, B, C, D, Vector[E]] = ???
  def apply(v: distinct): In2_5[I1, I2, A, B, C, D, Vector[E]] = ???
  def apply(v: rands)   : In2_5[I1, I2, A, B, C, D, Stream[E]] = ???
  def apply(v: samples) : In2_5[I1, I2, A, B, C, D, Vector[E]] = ???

  def apply(v: count)        : In2_5[I1, I2, A, B, C, D, Int] = ???
  def apply(v: countDistinct): In2_5[I1, I2, A, B, C, D, Int] = ???
  def apply(v: sum)          : In2_5[I1, I2, A, B, C, D, Double] = ???
  def apply(v: avg)          : In2_5[I1, I2, A, B, C, D, Double] = ???
  def apply(v: median)       : In2_5[I1, I2, A, B, C, D, Double] = ???
  def apply(v: variance)     : In2_5[I1, I2, A, B, C, D, Double] = ???
  def apply(v: stddev)       : In2_5[I1, I2, A, B, C, D, Double] = ???

  val length:  In2_5[I1, I2, A, B, C, D, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_5 [I1, I2, A, B, C, D, E]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_6 [I1, I2, A, B, C, D, E, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_7 [Any, Any, Any, Any, I1, I2, A, B, C, D, E, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_8 [Any, Any, Any, Any, I1, I2, A, B, C, D, E, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_9 [Any, Any, Any, Any, I1, I2, A, B, C, D, E, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_10[Any, Any, Any, Any, I1, I2, A, B, C, D, E, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_11[Any, Any, Any, Any, I1, I2, A, B, C, D, E, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_12[Any, Any, Any, Any, I1, I2, A, B, C, D, E, a, b, c, d, e, f, g] = ???
}

trait In_2_6[In2_6[_,_,_,_,_,_,_,_], In2_7[_,_,_,_,_,_,_,_,_], In3_6[_,_,_,_,_,_,_,_,_], In3_7[_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F] extends NS {
  val e         : In2_7[I1, I2, A, B, C, D, E, F, Long   ] with OneLong   [In2_7[I1, I2, A, B, C, D, E, F, Long   ], In3_7[Long   , I1, I2, A, B, C, D, E, F, Long   ]] = ???
  val a         : In2_7[I1, I2, A, B, C, D, E, F, String ] with OneString [In2_7[I1, I2, A, B, C, D, E, F, String ], In3_7[String , I1, I2, A, B, C, D, E, F, String ]] = ???
  val v         : In2_7[I1, I2, A, B, C, D, E, F, Any    ] with OneAny    [In2_7[I1, I2, A, B, C, D, E, F, Any    ], In3_7[Any    , I1, I2, A, B, C, D, E, F, Any    ]] = ???
  val ns        : In2_7[I1, I2, A, B, C, D, E, F, String ] with OneString [In2_7[I1, I2, A, B, C, D, E, F, String ], In3_7[String , I1, I2, A, B, C, D, E, F, String ]] = ???
  val txInstant : In2_7[I1, I2, A, B, C, D, E, F, Date   ] with OneDate   [In2_7[I1, I2, A, B, C, D, E, F, Date   ], In3_7[Date   , I1, I2, A, B, C, D, E, F, Date   ]] = ???
  val txT       : In2_7[I1, I2, A, B, C, D, E, F, Long   ] with OneLong   [In2_7[I1, I2, A, B, C, D, E, F, Long   ], In3_7[Long   , I1, I2, A, B, C, D, E, F, Long   ]] = ???
  val txAdded   : In2_7[I1, I2, A, B, C, D, E, F, Boolean] with OneBoolean[In2_7[I1, I2, A, B, C, D, E, F, Boolean], In3_7[Boolean, I1, I2, A, B, C, D, E, F, Boolean]] = ???

  val e_         : In2_6[I1, I2, A, B, C, D, E, F] with OneLong   [In2_6[I1, I2, A, B, C, D, E, F], In3_6[Long   , I1, I2, A, B, C, D, E, F]] = ???
  val a_         : In2_6[I1, I2, A, B, C, D, E, F] with OneString [In2_6[I1, I2, A, B, C, D, E, F], In3_6[String , I1, I2, A, B, C, D, E, F]] = ???
  val v_         : In2_6[I1, I2, A, B, C, D, E, F] with OneAny    [In2_6[I1, I2, A, B, C, D, E, F], In3_6[Any    , I1, I2, A, B, C, D, E, F]] = ???
  val ns_        : In2_6[I1, I2, A, B, C, D, E, F] with OneString [In2_6[I1, I2, A, B, C, D, E, F], In3_6[String , I1, I2, A, B, C, D, E, F]] = ???
  val txInstant_ : In2_6[I1, I2, A, B, C, D, E, F] with OneDate   [In2_6[I1, I2, A, B, C, D, E, F], In3_6[Date   , I1, I2, A, B, C, D, E, F]] = ???
  val txT_       : In2_6[I1, I2, A, B, C, D, E, F] with OneLong   [In2_6[I1, I2, A, B, C, D, E, F], In3_6[Long   , I1, I2, A, B, C, D, E, F]] = ???
  val txAdded_   : In2_6[I1, I2, A, B, C, D, E, F] with OneBoolean[In2_6[I1, I2, A, B, C, D, E, F], In3_6[Boolean, I1, I2, A, B, C, D, E, F]] = ???

  def apply(v: min)     : In2_6[I1, I2, A, B, C, D, E, F] = ???
  def apply(v: max)     : In2_6[I1, I2, A, B, C, D, E, F] = ???
  def apply(v: rand)    : In2_6[I1, I2, A, B, C, D, E, F] = ???
  def apply(v: sample)  : In2_6[I1, I2, A, B, C, D, E, F] = ???

  def apply(v: mins)    : In2_6[I1, I2, A, B, C, D, E, Vector[F]] = ???
  def apply(v: maxs)    : In2_6[I1, I2, A, B, C, D, E, Vector[F]] = ???
  def apply(v: distinct): In2_6[I1, I2, A, B, C, D, E, Vector[F]] = ???
  def apply(v: rands)   : In2_6[I1, I2, A, B, C, D, E, Stream[F]] = ???
  def apply(v: samples) : In2_6[I1, I2, A, B, C, D, E, Vector[F]] = ???

  def apply(v: count)        : In2_6[I1, I2, A, B, C, D, E, Int] = ???
  def apply(v: countDistinct): In2_6[I1, I2, A, B, C, D, E, Int] = ???
  def apply(v: sum)          : In2_6[I1, I2, A, B, C, D, E, Double] = ???
  def apply(v: avg)          : In2_6[I1, I2, A, B, C, D, E, Double] = ???
  def apply(v: median)       : In2_6[I1, I2, A, B, C, D, E, Double] = ???
  def apply(v: variance)     : In2_6[I1, I2, A, B, C, D, E, Double] = ???
  def apply(v: stddev)       : In2_6[I1, I2, A, B, C, D, E, Double] = ???

  val length:  In2_6[I1, I2, A, B, C, D, E, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_6 [I1, I2, A, B, C, D, E, F]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_7 [I1, I2, A, B, C, D, E, F, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_8 [Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_9 [Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_10[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_11[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_12[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_13[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, a, b, c, d, e, f, g] = ???
}

trait In_2_7[In2_7[_,_,_,_,_,_,_,_,_], In2_8[_,_,_,_,_,_,_,_,_,_], In3_7[_,_,_,_,_,_,_,_,_,_], In3_8[_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G] extends NS {
  val e         : In2_8[I1, I2, A, B, C, D, E, F, G, Long   ] with OneLong   [In2_8[I1, I2, A, B, C, D, E, F, G, Long   ], In3_8[Long   , I1, I2, A, B, C, D, E, F, G, Long   ]] = ???
  val a         : In2_8[I1, I2, A, B, C, D, E, F, G, String ] with OneString [In2_8[I1, I2, A, B, C, D, E, F, G, String ], In3_8[String , I1, I2, A, B, C, D, E, F, G, String ]] = ???
  val v         : In2_8[I1, I2, A, B, C, D, E, F, G, Any    ] with OneAny    [In2_8[I1, I2, A, B, C, D, E, F, G, Any    ], In3_8[Any    , I1, I2, A, B, C, D, E, F, G, Any    ]] = ???
  val ns        : In2_8[I1, I2, A, B, C, D, E, F, G, String ] with OneString [In2_8[I1, I2, A, B, C, D, E, F, G, String ], In3_8[String , I1, I2, A, B, C, D, E, F, G, String ]] = ???
  val txInstant : In2_8[I1, I2, A, B, C, D, E, F, G, Date   ] with OneDate   [In2_8[I1, I2, A, B, C, D, E, F, G, Date   ], In3_8[Date   , I1, I2, A, B, C, D, E, F, G, Date   ]] = ???
  val txT       : In2_8[I1, I2, A, B, C, D, E, F, G, Long   ] with OneLong   [In2_8[I1, I2, A, B, C, D, E, F, G, Long   ], In3_8[Long   , I1, I2, A, B, C, D, E, F, G, Long   ]] = ???
  val txAdded   : In2_8[I1, I2, A, B, C, D, E, F, G, Boolean] with OneBoolean[In2_8[I1, I2, A, B, C, D, E, F, G, Boolean], In3_8[Boolean, I1, I2, A, B, C, D, E, F, G, Boolean]] = ???

  val e_         : In2_7[I1, I2, A, B, C, D, E, F, G] with OneLong   [In2_7[I1, I2, A, B, C, D, E, F, G], In3_7[Long   , I1, I2, A, B, C, D, E, F, G]] = ???
  val a_         : In2_7[I1, I2, A, B, C, D, E, F, G] with OneString [In2_7[I1, I2, A, B, C, D, E, F, G], In3_7[String , I1, I2, A, B, C, D, E, F, G]] = ???
  val v_         : In2_7[I1, I2, A, B, C, D, E, F, G] with OneAny    [In2_7[I1, I2, A, B, C, D, E, F, G], In3_7[Any    , I1, I2, A, B, C, D, E, F, G]] = ???
  val ns_        : In2_7[I1, I2, A, B, C, D, E, F, G] with OneString [In2_7[I1, I2, A, B, C, D, E, F, G], In3_7[String , I1, I2, A, B, C, D, E, F, G]] = ???
  val txInstant_ : In2_7[I1, I2, A, B, C, D, E, F, G] with OneDate   [In2_7[I1, I2, A, B, C, D, E, F, G], In3_7[Date   , I1, I2, A, B, C, D, E, F, G]] = ???
  val txT_       : In2_7[I1, I2, A, B, C, D, E, F, G] with OneLong   [In2_7[I1, I2, A, B, C, D, E, F, G], In3_7[Long   , I1, I2, A, B, C, D, E, F, G]] = ???
  val txAdded_   : In2_7[I1, I2, A, B, C, D, E, F, G] with OneBoolean[In2_7[I1, I2, A, B, C, D, E, F, G], In3_7[Boolean, I1, I2, A, B, C, D, E, F, G]] = ???

  def apply(v: min)     : In2_7[I1, I2, A, B, C, D, E, F, G] = ???
  def apply(v: max)     : In2_7[I1, I2, A, B, C, D, E, F, G] = ???
  def apply(v: rand)    : In2_7[I1, I2, A, B, C, D, E, F, G] = ???
  def apply(v: sample)  : In2_7[I1, I2, A, B, C, D, E, F, G] = ???

  def apply(v: mins)    : In2_7[I1, I2, A, B, C, D, E, F, Vector[G]] = ???
  def apply(v: maxs)    : In2_7[I1, I2, A, B, C, D, E, F, Vector[G]] = ???
  def apply(v: distinct): In2_7[I1, I2, A, B, C, D, E, F, Vector[G]] = ???
  def apply(v: rands)   : In2_7[I1, I2, A, B, C, D, E, F, Stream[G]] = ???
  def apply(v: samples) : In2_7[I1, I2, A, B, C, D, E, F, Vector[G]] = ???

  def apply(v: count)        : In2_7[I1, I2, A, B, C, D, E, F, Int] = ???
  def apply(v: countDistinct): In2_7[I1, I2, A, B, C, D, E, F, Int] = ???
  def apply(v: sum)          : In2_7[I1, I2, A, B, C, D, E, F, Double] = ???
  def apply(v: avg)          : In2_7[I1, I2, A, B, C, D, E, F, Double] = ???
  def apply(v: median)       : In2_7[I1, I2, A, B, C, D, E, F, Double] = ???
  def apply(v: variance)     : In2_7[I1, I2, A, B, C, D, E, F, Double] = ???
  def apply(v: stddev)       : In2_7[I1, I2, A, B, C, D, E, F, Double] = ???

  val length:  In2_7[I1, I2, A, B, C, D, E, F, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_7 [I1, I2, A, B, C, D, E, F, G]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_8 [I1, I2, A, B, C, D, E, F, G, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_9 [Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_10[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_11[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_12[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_13[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_14[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, a, b, c, d, e, f, g] = ???
}

trait In_2_8[In2_8[_,_,_,_,_,_,_,_,_,_], In2_9[_,_,_,_,_,_,_,_,_,_,_], In3_8[_,_,_,_,_,_,_,_,_,_,_], In3_9[_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H] extends NS {
  val e         : In2_9[I1, I2, A, B, C, D, E, F, G, H, Long   ] with OneLong   [In2_9[I1, I2, A, B, C, D, E, F, G, H, Long   ], In3_9[Long   , I1, I2, A, B, C, D, E, F, G, H, Long   ]] = ???
  val a         : In2_9[I1, I2, A, B, C, D, E, F, G, H, String ] with OneString [In2_9[I1, I2, A, B, C, D, E, F, G, H, String ], In3_9[String , I1, I2, A, B, C, D, E, F, G, H, String ]] = ???
  val v         : In2_9[I1, I2, A, B, C, D, E, F, G, H, Any    ] with OneAny    [In2_9[I1, I2, A, B, C, D, E, F, G, H, Any    ], In3_9[Any    , I1, I2, A, B, C, D, E, F, G, H, Any    ]] = ???
  val ns        : In2_9[I1, I2, A, B, C, D, E, F, G, H, String ] with OneString [In2_9[I1, I2, A, B, C, D, E, F, G, H, String ], In3_9[String , I1, I2, A, B, C, D, E, F, G, H, String ]] = ???
  val txInstant : In2_9[I1, I2, A, B, C, D, E, F, G, H, Date   ] with OneDate   [In2_9[I1, I2, A, B, C, D, E, F, G, H, Date   ], In3_9[Date   , I1, I2, A, B, C, D, E, F, G, H, Date   ]] = ???
  val txT       : In2_9[I1, I2, A, B, C, D, E, F, G, H, Long   ] with OneLong   [In2_9[I1, I2, A, B, C, D, E, F, G, H, Long   ], In3_9[Long   , I1, I2, A, B, C, D, E, F, G, H, Long   ]] = ???
  val txAdded   : In2_9[I1, I2, A, B, C, D, E, F, G, H, Boolean] with OneBoolean[In2_9[I1, I2, A, B, C, D, E, F, G, H, Boolean], In3_9[Boolean, I1, I2, A, B, C, D, E, F, G, H, Boolean]] = ???

  val e_         : In2_8[I1, I2, A, B, C, D, E, F, G, H] with OneLong   [In2_8[I1, I2, A, B, C, D, E, F, G, H], In3_8[Long   , I1, I2, A, B, C, D, E, F, G, H]] = ???
  val a_         : In2_8[I1, I2, A, B, C, D, E, F, G, H] with OneString [In2_8[I1, I2, A, B, C, D, E, F, G, H], In3_8[String , I1, I2, A, B, C, D, E, F, G, H]] = ???
  val v_         : In2_8[I1, I2, A, B, C, D, E, F, G, H] with OneAny    [In2_8[I1, I2, A, B, C, D, E, F, G, H], In3_8[Any    , I1, I2, A, B, C, D, E, F, G, H]] = ???
  val ns_        : In2_8[I1, I2, A, B, C, D, E, F, G, H] with OneString [In2_8[I1, I2, A, B, C, D, E, F, G, H], In3_8[String , I1, I2, A, B, C, D, E, F, G, H]] = ???
  val txInstant_ : In2_8[I1, I2, A, B, C, D, E, F, G, H] with OneDate   [In2_8[I1, I2, A, B, C, D, E, F, G, H], In3_8[Date   , I1, I2, A, B, C, D, E, F, G, H]] = ???
  val txT_       : In2_8[I1, I2, A, B, C, D, E, F, G, H] with OneLong   [In2_8[I1, I2, A, B, C, D, E, F, G, H], In3_8[Long   , I1, I2, A, B, C, D, E, F, G, H]] = ???
  val txAdded_   : In2_8[I1, I2, A, B, C, D, E, F, G, H] with OneBoolean[In2_8[I1, I2, A, B, C, D, E, F, G, H], In3_8[Boolean, I1, I2, A, B, C, D, E, F, G, H]] = ???

  def apply(v: min)     : In2_8[I1, I2, A, B, C, D, E, F, G, H] = ???
  def apply(v: max)     : In2_8[I1, I2, A, B, C, D, E, F, G, H] = ???
  def apply(v: rand)    : In2_8[I1, I2, A, B, C, D, E, F, G, H] = ???
  def apply(v: sample)  : In2_8[I1, I2, A, B, C, D, E, F, G, H] = ???

  def apply(v: mins)    : In2_8[I1, I2, A, B, C, D, E, F, G, Vector[H]] = ???
  def apply(v: maxs)    : In2_8[I1, I2, A, B, C, D, E, F, G, Vector[H]] = ???
  def apply(v: distinct): In2_8[I1, I2, A, B, C, D, E, F, G, Vector[H]] = ???
  def apply(v: rands)   : In2_8[I1, I2, A, B, C, D, E, F, G, Stream[H]] = ???
  def apply(v: samples) : In2_8[I1, I2, A, B, C, D, E, F, G, Vector[H]] = ???

  def apply(v: count)        : In2_8[I1, I2, A, B, C, D, E, F, G, Int] = ???
  def apply(v: countDistinct): In2_8[I1, I2, A, B, C, D, E, F, G, Int] = ???
  def apply(v: sum)          : In2_8[I1, I2, A, B, C, D, E, F, G, Double] = ???
  def apply(v: avg)          : In2_8[I1, I2, A, B, C, D, E, F, G, Double] = ???
  def apply(v: median)       : In2_8[I1, I2, A, B, C, D, E, F, G, Double] = ???
  def apply(v: variance)     : In2_8[I1, I2, A, B, C, D, E, F, G, Double] = ???
  def apply(v: stddev)       : In2_8[I1, I2, A, B, C, D, E, F, G, Double] = ???

  val length:  In2_8[I1, I2, A, B, C, D, E, F, G, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_8 [I1, I2, A, B, C, D, E, F, G, H]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_9 [I1, I2, A, B, C, D, E, F, G, H, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_10[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_11[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_12[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_13[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_14[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_15[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, a, b, c, d, e, f, g] = ???
}

trait In_2_9[In2_9[_,_,_,_,_,_,_,_,_,_,_], In2_10[_,_,_,_,_,_,_,_,_,_,_,_], In3_9[_,_,_,_,_,_,_,_,_,_,_,_], In3_10[_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I] extends NS {
  val e         : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Long   ], In3_10[Long   , I1, I2, A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val a         : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, String ] with OneString [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, String ], In3_10[String , I1, I2, A, B, C, D, E, F, G, H, I, String ]] = ???
  val v         : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Any    ] with OneAny    [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Any    ], In3_10[Any    , I1, I2, A, B, C, D, E, F, G, H, I, Any    ]] = ???
  val ns        : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, String ] with OneString [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, String ], In3_10[String , I1, I2, A, B, C, D, E, F, G, H, I, String ]] = ???
  val txInstant : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Date   ] with OneDate   [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Date   ], In3_10[Date   , I1, I2, A, B, C, D, E, F, G, H, I, Date   ]] = ???
  val txT       : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Long   ], In3_10[Long   , I1, I2, A, B, C, D, E, F, G, H, I, Long   ]] = ???
  val txAdded   : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Boolean] with OneBoolean[In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Boolean], In3_10[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, Boolean]] = ???

  val e_         : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] with OneLong   [In2_9[I1, I2, A, B, C, D, E, F, G, H, I], In3_9[Long   , I1, I2, A, B, C, D, E, F, G, H, I]] = ???
  val a_         : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] with OneString [In2_9[I1, I2, A, B, C, D, E, F, G, H, I], In3_9[String , I1, I2, A, B, C, D, E, F, G, H, I]] = ???
  val v_         : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] with OneAny    [In2_9[I1, I2, A, B, C, D, E, F, G, H, I], In3_9[Any    , I1, I2, A, B, C, D, E, F, G, H, I]] = ???
  val ns_        : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] with OneString [In2_9[I1, I2, A, B, C, D, E, F, G, H, I], In3_9[String , I1, I2, A, B, C, D, E, F, G, H, I]] = ???
  val txInstant_ : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] with OneDate   [In2_9[I1, I2, A, B, C, D, E, F, G, H, I], In3_9[Date   , I1, I2, A, B, C, D, E, F, G, H, I]] = ???
  val txT_       : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] with OneLong   [In2_9[I1, I2, A, B, C, D, E, F, G, H, I], In3_9[Long   , I1, I2, A, B, C, D, E, F, G, H, I]] = ???
  val txAdded_   : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] with OneBoolean[In2_9[I1, I2, A, B, C, D, E, F, G, H, I], In3_9[Boolean, I1, I2, A, B, C, D, E, F, G, H, I]] = ???

  def apply(v: min)     : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] = ???
  def apply(v: max)     : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] = ???
  def apply(v: rand)    : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] = ???
  def apply(v: sample)  : In2_9[I1, I2, A, B, C, D, E, F, G, H, I] = ???

  def apply(v: mins)    : In2_9[I1, I2, A, B, C, D, E, F, G, H, Vector[I]] = ???
  def apply(v: maxs)    : In2_9[I1, I2, A, B, C, D, E, F, G, H, Vector[I]] = ???
  def apply(v: distinct): In2_9[I1, I2, A, B, C, D, E, F, G, H, Vector[I]] = ???
  def apply(v: rands)   : In2_9[I1, I2, A, B, C, D, E, F, G, H, Stream[I]] = ???
  def apply(v: samples) : In2_9[I1, I2, A, B, C, D, E, F, G, H, Vector[I]] = ???

  def apply(v: count)        : In2_9[I1, I2, A, B, C, D, E, F, G, H, Int] = ???
  def apply(v: countDistinct): In2_9[I1, I2, A, B, C, D, E, F, G, H, Int] = ???
  def apply(v: sum)          : In2_9[I1, I2, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: avg)          : In2_9[I1, I2, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: median)       : In2_9[I1, I2, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: variance)     : In2_9[I1, I2, A, B, C, D, E, F, G, H, Double] = ???
  def apply(v: stddev)       : In2_9[I1, I2, A, B, C, D, E, F, G, H, Double] = ???

  val length:  In2_9[I1, I2, A, B, C, D, E, F, G, H, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_9 [I1, I2, A, B, C, D, E, F, G, H, I]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_11[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_12[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_13[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_14[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_15[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_16[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, a, b, c, d, e, f, g] = ???
}

  trait In_2_10[In2_10[_,_,_,_,_,_,_,_,_,_,_,_], In2_11[_,_,_,_,_,_,_,_,_,_,_,_,_], In3_10[_,_,_,_,_,_,_,_,_,_,_,_,_], In3_11[_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J] extends NS {
    val e         : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Long   ], In3_11[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
    val a         : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String ] with OneString [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String ], In3_11[String , I1, I2, A, B, C, D, E, F, G, H, I, J, String ]] = ???
    val v         : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Any    ] with OneAny    [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Any    ], In3_11[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, Any    ]] = ???
    val ns        : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String ] with OneString [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String ], In3_11[String , I1, I2, A, B, C, D, E, F, G, H, I, J, String ]] = ???
    val txInstant : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Date   ] with OneDate   [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Date   ], In3_11[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, Date   ]] = ???
    val txT       : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Long   ], In3_11[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, Long   ]] = ???
    val txAdded   : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Boolean] with OneBoolean[In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Boolean], In3_11[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, Boolean]] = ???

  val e_         : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] with OneLong   [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J], In3_10[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J]] = ???
  val a_         : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] with OneString [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J], In3_10[String , I1, I2, A, B, C, D, E, F, G, H, I, J]] = ???
  val v_         : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] with OneAny    [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J], In3_10[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J]] = ???
  val ns_        : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] with OneString [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J], In3_10[String , I1, I2, A, B, C, D, E, F, G, H, I, J]] = ???
  val txInstant_ : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] with OneDate   [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J], In3_10[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J]] = ???
  val txT_       : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] with OneLong   [In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J], In3_10[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J]] = ???
  val txAdded_   : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] with OneBoolean[In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J], In3_10[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J]] = ???

    def apply(v: min)     : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: max)     : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: rand)    : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] = ???
    def apply(v: sample)  : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] = ???

    def apply(v: mins)    : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Vector[J]] = ???
    def apply(v: maxs)    : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Vector[J]] = ???
    def apply(v: distinct): In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Vector[J]] = ???
    def apply(v: rands)   : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Stream[J]] = ???
    def apply(v: samples) : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Vector[J]] = ???

    def apply(v: count)        : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Int] = ???
    def apply(v: countDistinct): In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Int] = ???
    def apply(v: sum)          : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: avg)          : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: median)       : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: variance)     : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Double] = ???
    def apply(v: stddev)       : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Double] = ???

  val length:  In2_10[I1, I2, A, B, C, D, E, F, G, H, I, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_10[I1, I2, A, B, C, D, E, F, G, H, I, J]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_12[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_13[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_14[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_15[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_16[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_17[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f, g] = ???
}

trait In_2_11[In2_11[_,_,_,_,_,_,_,_,_,_,_,_,_], In2_12[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_11[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_12[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K] extends NS {
  val e         : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Long   ], In3_12[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val a         : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String ], In3_12[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val v         : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Any    ] with OneAny    [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Any    ], In3_12[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, Any    ]] = ???
  val ns        : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String ], In3_12[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, String ]] = ???
  val txInstant : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Date   ] with OneDate   [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Date   ], In3_12[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, Date   ]] = ???
  val txT       : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Long   ], In3_12[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, Long   ]] = ???
  val txAdded   : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Boolean] with OneBoolean[In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Boolean], In3_12[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, Boolean]] = ???

  val e_         : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] with OneLong   [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K], In3_11[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K]] = ???
  val a_         : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] with OneString [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K], In3_11[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K]] = ???
  val v_         : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] with OneAny    [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K], In3_11[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K]] = ???
  val ns_        : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] with OneString [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K], In3_11[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K]] = ???
  val txInstant_ : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] with OneDate   [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K], In3_11[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K]] = ???
  val txT_       : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] with OneLong   [In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K], In3_11[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K]] = ???
  val txAdded_   : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] with OneBoolean[In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K], In3_11[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K]] = ???

  def apply(v: min)     : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: max)     : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: rand)    : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] = ???
  def apply(v: sample)  : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] = ???

  def apply(v: mins)    : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???
  def apply(v: maxs)    : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???
  def apply(v: distinct): In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???
  def apply(v: rands)   : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Stream[K]] = ???
  def apply(v: samples) : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Vector[K]] = ???

  def apply(v: count)        : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Int] = ???
  def apply(v: countDistinct): In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Int] = ???
  def apply(v: sum)          : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: avg)          : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: median)       : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: variance)     : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Double] = ???
  def apply(v: stddev)       : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Double] = ???

  val length:  In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_13[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_14[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_15[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_16[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_17[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_18[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f, g] = ???
}

trait In_2_12[In2_12[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_12[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] extends NS {
  val e         : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In3_13[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val a         : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String ], In3_13[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val v         : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Any    ] with OneAny    [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Any    ], In3_13[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Any    ]] = ???
  val ns        : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String ], In3_13[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String ]] = ???
  val txInstant : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Date   ] with OneDate   [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Date   ], In3_13[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Date   ]] = ???
  val txT       : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Long   ], In3_13[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Long   ]] = ???
  val txAdded   : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Boolean] with OneBoolean[In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Boolean], In3_13[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Boolean]] = ???

  val e_         : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] with OneLong   [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L], In3_12[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]] = ???
  val a_         : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] with OneString [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L], In3_12[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]] = ???
  val v_         : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] with OneAny    [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L], In3_12[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]] = ???
  val ns_        : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] with OneString [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L], In3_12[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]] = ???
  val txInstant_ : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] with OneDate   [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L], In3_12[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]] = ???
  val txT_       : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] with OneLong   [In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L], In3_12[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]] = ???
  val txAdded_   : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] with OneBoolean[In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L], In3_12[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]] = ???

  def apply(v: min)     : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: max)     : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: rand)    : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] = ???
  def apply(v: sample)  : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] = ???

  def apply(v: mins)    : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???
  def apply(v: maxs)    : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???
  def apply(v: distinct): In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???
  def apply(v: rands)   : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Stream[L]] = ???
  def apply(v: samples) : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Vector[L]] = ???

  def apply(v: count)        : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Int] = ???
  def apply(v: countDistinct): In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Int] = ???
  def apply(v: sum)          : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: avg)          : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: median)       : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: variance)     : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Double] = ???
  def apply(v: stddev)       : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Double] = ???

  val length:  In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Int] = ???


  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_14[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_15[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_16[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_17[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_18[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_19[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f, g] = ???
}

trait In_2_13[In2_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_13[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS {
  val e         : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In3_14[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val a         : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In3_14[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val v         : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ] with OneAny    [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ], In3_14[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ]] = ???
  val ns        : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String ], In3_14[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String ]] = ???
  val txInstant : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ] with OneDate   [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ], In3_14[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ]] = ???
  val txT       : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], In3_14[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ]] = ???
  val txAdded   : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean] with OneBoolean[In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean], In3_14[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean]] = ???

  val e_         : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] with OneLong   [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M], In3_13[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]] = ???
  val a_         : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] with OneString [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M], In3_13[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]] = ???
  val v_         : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] with OneAny    [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M], In3_13[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]] = ???
  val ns_        : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] with OneString [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M], In3_13[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]] = ???
  val txInstant_ : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] with OneDate   [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M], In3_13[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]] = ???
  val txT_       : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] with OneLong   [In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M], In3_13[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]] = ???
  val txAdded_   : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] with OneBoolean[In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M], In3_13[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]] = ???

  def apply(v: min)     : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: max)     : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: rand)    : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???
  def apply(v: sample)  : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] = ???

  def apply(v: mins)    : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???
  def apply(v: maxs)    : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???
  def apply(v: distinct): In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???
  def apply(v: rands)   : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Stream[M]] = ???
  def apply(v: samples) : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Vector[M]] = ???

  def apply(v: count)        : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Int] = ???
  def apply(v: countDistinct): In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Int] = ???
  def apply(v: sum)          : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: avg)          : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: median)       : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: variance)     : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???
  def apply(v: stddev)       : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Double] = ???

  val length:  In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_15[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_16[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_17[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_18[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_19[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_20[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f, g] = ???
}

trait In_2_14[In2_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS {
  val e         : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In3_15[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val a         : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In3_15[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val v         : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ] with OneAny    [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ], In3_15[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ]] = ???
  val ns        : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], In3_15[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ]] = ???
  val txInstant : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ] with OneDate   [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ], In3_15[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ]] = ???
  val txT       : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], In3_15[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ]] = ???
  val txAdded   : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean] with OneBoolean[In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean], In3_15[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean]] = ???

  val e_         : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] with OneLong   [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N], In3_14[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]] = ???
  val a_         : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] with OneString [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N], In3_14[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]] = ???
  val v_         : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] with OneAny    [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N], In3_14[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]] = ???
  val ns_        : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] with OneString [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N], In3_14[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]] = ???
  val txInstant_ : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] with OneDate   [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N], In3_14[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]] = ???
  val txT_       : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] with OneLong   [In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N], In3_14[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]] = ???
  val txAdded_   : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] with OneBoolean[In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N], In3_14[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]] = ???

  def apply(v: min)     : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: max)     : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: rand)    : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???
  def apply(v: sample)  : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = ???

  def apply(v: mins)    : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???
  def apply(v: maxs)    : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???
  def apply(v: distinct): In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???
  def apply(v: rands)   : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Stream[N]] = ???
  def apply(v: samples) : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Vector[N]] = ???

  def apply(v: count)        : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Int] = ???
  def apply(v: countDistinct): In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Int] = ???
  def apply(v: sum)          : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: avg)          : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: median)       : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: variance)     : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???
  def apply(v: stddev)       : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Double] = ???

  val length:  In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_16[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_17[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_18[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_19[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_20[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_21[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f, g] = ???
}

trait In_2_15[In2_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS {
  val e         : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In3_16[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val a         : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In3_16[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val v         : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ] with OneAny    [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ], In3_16[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ]] = ???
  val ns        : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], In3_16[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ]] = ???
  val txInstant : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ] with OneDate   [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ], In3_16[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ]] = ???
  val txT       : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], In3_16[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ]] = ???
  val txAdded   : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean] with OneBoolean[In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean], In3_16[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean]] = ???

  val e_         : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] with OneLong   [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O], In3_15[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] = ???
  val a_         : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] with OneString [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O], In3_15[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] = ???
  val v_         : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] with OneAny    [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O], In3_15[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] = ???
  val ns_        : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] with OneString [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O], In3_15[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] = ???
  val txInstant_ : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] with OneDate   [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O], In3_15[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] = ???
  val txT_       : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] with OneLong   [In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O], In3_15[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] = ???
  val txAdded_   : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] with OneBoolean[In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O], In3_15[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] = ???

  def apply(v: min)     : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: max)     : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: rand)    : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???
  def apply(v: sample)  : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = ???

  def apply(v: mins)    : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???
  def apply(v: maxs)    : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???
  def apply(v: distinct): In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???
  def apply(v: rands)   : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Stream[O]] = ???
  def apply(v: samples) : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Vector[O]] = ???

  def apply(v: count)        : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Int] = ???
  def apply(v: countDistinct): In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Int] = ???
  def apply(v: sum)          : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: avg)          : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: median)       : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: variance)     : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???
  def apply(v: stddev)       : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Double] = ???

  val length:  In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]                                       = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a]                                    = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_17[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b]                = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_18[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c]             = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_19[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_20[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_21[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], in2_7[_,_,_,_,_,_,_,_], in2_8[_,_,_,_,_,_,_,_,_], a, b, c, d, e, f, g] (m7: Molecule_7[ns7, ns8, in2_7, in2_8, a, b, c, d, e, f, g]): In_2_22[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f, g] = ???
}

trait In_2_16[In2_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS {
  val e         : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In3_17[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val a         : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In3_17[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val v         : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ] with OneAny    [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ], In3_17[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ]] = ???
  val ns        : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], In3_17[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ]] = ???
  val txInstant : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ] with OneDate   [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ], In3_17[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ]] = ???
  val txT       : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], In3_17[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ]] = ???
  val txAdded   : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean] with OneBoolean[In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean], In3_17[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean]] = ???

  val e_         : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] with OneLong   [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P], In3_16[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] = ???
  val a_         : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] with OneString [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P], In3_16[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] = ???
  val v_         : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] with OneAny    [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P], In3_16[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] = ???
  val ns_        : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] with OneString [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P], In3_16[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] = ???
  val txInstant_ : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] with OneDate   [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P], In3_16[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] = ???
  val txT_       : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] with OneLong   [In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P], In3_16[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] = ???
  val txAdded_   : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] with OneBoolean[In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P], In3_16[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] = ???

  def apply(v: min)     : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: max)     : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: rand)    : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???
  def apply(v: sample)  : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = ???

  def apply(v: mins)    : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???
  def apply(v: maxs)    : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???
  def apply(v: distinct): In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???
  def apply(v: rands)   : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Stream[P]] = ???
  def apply(v: samples) : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Vector[P]] = ???

  def apply(v: count)        : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Int] = ???
  def apply(v: countDistinct): In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Int] = ???
  def apply(v: sum)          : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: avg)          : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: median)       : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: variance)     : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???
  def apply(v: stddev)       : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Double] = ???

  val length:  In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]                                    = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a]                                 = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_18[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_19[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_20[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d]       = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_21[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e]    = ???
  def tx[ns6[_,_,_,_,_,_]  , ns7[_,_,_,_,_,_,_]  , in2_6[_,_,_,_,_,_,_]  , in2_7[_,_,_,_,_,_,_,_]  , a, b, c, d, e, f   ] (m6: Molecule_6[ns6, ns7, in2_6, in2_7, a, b, c, d, e, f])   : In_2_22[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e, f] = ???
}

trait In_2_17[In2_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS {
  val e         : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In3_18[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val a         : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In3_18[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val v         : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ] with OneAny    [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ], In3_18[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ]] = ???
  val ns        : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], In3_18[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ]] = ???
  val txInstant : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ] with OneDate   [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ], In3_18[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ]] = ???
  val txT       : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], In3_18[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ]] = ???
  val txAdded   : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean] with OneBoolean[In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean], In3_18[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean]] = ???

  val e_         : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] with OneLong   [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q], In3_17[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] = ???
  val a_         : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] with OneString [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q], In3_17[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] = ???
  val v_         : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] with OneAny    [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q], In3_17[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] = ???
  val ns_        : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] with OneString [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q], In3_17[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] = ???
  val txInstant_ : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] with OneDate   [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q], In3_17[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] = ???
  val txT_       : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] with OneLong   [In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q], In3_17[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] = ???
  val txAdded_   : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] with OneBoolean[In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q], In3_17[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] = ???

  def apply(v: min)     : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: max)     : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: rand)    : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???
  def apply(v: sample)  : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = ???

  def apply(v: mins)    : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???
  def apply(v: maxs)    : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???
  def apply(v: distinct): In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???
  def apply(v: rands)   : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Stream[Q]] = ???
  def apply(v: samples) : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Vector[Q]] = ???

  def apply(v: count)        : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Int] = ???
  def apply(v: countDistinct): In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Int] = ???
  def apply(v: sum)          : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: avg)          : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: median)       : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: variance)     : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???
  def apply(v: stddev)       : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Double] = ???

  val length:  In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_19[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_20[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_21[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d]       = ???
  def tx[ns5[_,_,_,_,_]    , ns6[_,_,_,_,_,_]    , in2_5[_,_,_,_,_,_]    , in2_6[_,_,_,_,_,_,_]    , a, b, c, d, e      ] (m5: Molecule_5[ns5, ns6, in2_5, in2_6, a, b, c, d, e])      : In_2_22[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d, e]    = ???
}

trait In_2_18[In2_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS {
  val e         : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In3_19[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val a         : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In3_19[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val v         : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ] with OneAny    [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ], In3_19[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ]] = ???
  val ns        : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], In3_19[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ]] = ???
  val txInstant : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ] with OneDate   [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ], In3_19[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ]] = ???
  val txT       : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], In3_19[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ]] = ???
  val txAdded   : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean] with OneBoolean[In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean], In3_19[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean]] = ???

  val e_         : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] with OneLong   [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R], In3_18[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] = ???
  val a_         : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] with OneString [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R], In3_18[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] = ???
  val v_         : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] with OneAny    [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R], In3_18[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] = ???
  val ns_        : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] with OneString [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R], In3_18[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] = ???
  val txInstant_ : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] with OneDate   [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R], In3_18[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] = ???
  val txT_       : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] with OneLong   [In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R], In3_18[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] = ???
  val txAdded_   : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] with OneBoolean[In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R], In3_18[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] = ???

  def apply(v: min)     : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: max)     : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: rand)    : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???
  def apply(v: sample)  : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = ???

  def apply(v: mins)    : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???
  def apply(v: maxs)    : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???
  def apply(v: distinct): In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???
  def apply(v: rands)   : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Stream[R]] = ???
  def apply(v: samples) : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Vector[R]] = ???

  def apply(v: count)        : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Int] = ???
  def apply(v: countDistinct): In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Int] = ???
  def apply(v: sum)          : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: avg)          : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: median)       : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: variance)     : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???
  def apply(v: stddev)       : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Double] = ???

  val length:  In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_20[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_21[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c]          = ???
  def tx[ns4[_,_,_,_]      , ns5[_,_,_,_,_]      , in2_4[_,_,_,_,_]      , in2_5[_,_,_,_,_,_]      , a, b, c, d         ] (m4: Molecule_4[ns4, ns5, in2_4, in2_5, a, b, c, d])         : In_2_22[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c, d]       = ???
}

trait In_2_19[In2_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS {
  val e         : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In3_20[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val a         : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In3_20[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val v         : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ] with OneAny    [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ], In3_20[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ]] = ???
  val ns        : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], In3_20[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ]] = ???
  val txInstant : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ] with OneDate   [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ], In3_20[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ]] = ???
  val txT       : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], In3_20[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ]] = ???
  val txAdded   : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean] with OneBoolean[In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean], In3_20[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean]] = ???

  val e_         : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] with OneLong   [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S], In3_19[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] = ???
  val a_         : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] with OneString [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S], In3_19[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] = ???
  val v_         : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] with OneAny    [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S], In3_19[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] = ???
  val ns_        : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] with OneString [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S], In3_19[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] = ???
  val txInstant_ : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] with OneDate   [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S], In3_19[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] = ???
  val txT_       : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] with OneLong   [In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S], In3_19[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] = ???
  val txAdded_   : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] with OneBoolean[In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S], In3_19[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] = ???

  def apply(v: min)     : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: max)     : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: rand)    : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???
  def apply(v: sample)  : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = ???

  def apply(v: mins)    : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???
  def apply(v: maxs)    : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???
  def apply(v: distinct): In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???
  def apply(v: rands)   : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Stream[S]] = ???
  def apply(v: samples) : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Vector[S]] = ???

  def apply(v: count)        : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Int] = ???
  def apply(v: countDistinct): In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Int] = ???
  def apply(v: sum)          : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: avg)          : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: median)       : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: variance)     : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???
  def apply(v: stddev)       : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Double] = ???

  val length:  In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_21[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b]             = ???
  def tx[ns3[_,_,_]        , ns4[_,_,_,_]        , in2_3[_,_,_,_]        , in2_4[_,_,_,_,_]        , a, b, c            ] (m3: Molecule_3[ns3, ns4, in2_3, in2_4, a, b, c])            : In_2_22[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b, c]          = ???
}

trait In_2_20[In2_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS {
  val e         : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In3_21[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val a         : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In3_21[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val v         : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ] with OneAny    [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ], In3_21[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ]] = ???
  val ns        : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], In3_21[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ]] = ???
  val txInstant : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ] with OneDate   [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ], In3_21[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ]] = ???
  val txT       : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], In3_21[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ]] = ???
  val txAdded   : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean] with OneBoolean[In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean], In3_21[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean]] = ???

  val e_         : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] with OneLong   [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T], In3_20[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] = ???
  val a_         : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] with OneString [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T], In3_20[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] = ???
  val v_         : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] with OneAny    [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T], In3_20[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] = ???
  val ns_        : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] with OneString [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T], In3_20[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] = ???
  val txInstant_ : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] with OneDate   [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T], In3_20[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] = ???
  val txT_       : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] with OneLong   [In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T], In3_20[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] = ???
  val txAdded_   : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] with OneBoolean[In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T], In3_20[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] = ???

  def apply(v: min)     : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: max)     : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: rand)    : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???
  def apply(v: sample)  : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = ???

  def apply(v: mins)    : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???
  def apply(v: maxs)    : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???
  def apply(v: distinct): In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???
  def apply(v: rands)   : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Stream[T]] = ???
  def apply(v: samples) : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Vector[T]] = ???

  def apply(v: count)        : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Int] = ???
  def apply(v: countDistinct): In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Int] = ???
  def apply(v: sum)          : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: avg)          : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: median)       : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: variance)     : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???
  def apply(v: stddev)       : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Double] = ???

  val length:  In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a]                                           = ???
  def tx[ns2[_,_]          , ns3[_,_,_]          , in2_2[_,_,_]          , in2_3[_,_,_,_]          , a, b               ] (m2: Molecule_2[ns2, ns3, in2_2, in2_3, a, b])               : In_2_22[Any, Any, Any, Any, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a, b]             = ???
}

trait In_2_21[In2_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In2_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS {
  val e         : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], In3_22[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ]] = ???
  val a         : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], In3_22[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ]] = ???
  val v         : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], In3_22[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ]] = ???
  val ns        : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], In3_22[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ]] = ???
  val txInstant : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], In3_22[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ]] = ???
  val txT       : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], In3_22[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ]] = ???
  val txAdded   : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], In3_22[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean]] = ???

  val e_         : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] with OneLong   [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U], In3_21[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] = ???
  val a_         : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] with OneString [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U], In3_21[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] = ???
  val v_         : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] with OneAny    [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U], In3_21[Any    , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] = ???
  val ns_        : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] with OneString [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U], In3_21[String , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] = ???
  val txInstant_ : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] with OneDate   [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U], In3_21[Date   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] = ???
  val txT_       : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] with OneLong   [In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U], In3_21[Long   , I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] = ???
  val txAdded_   : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] with OneBoolean[In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U], In3_21[Boolean, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] = ???

  def apply(v: min)     : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: max)     : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: rand)    : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???
  def apply(v: sample)  : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = ???

  def apply(v: mins)    : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???
  def apply(v: maxs)    : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???
  def apply(v: distinct): In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???
  def apply(v: rands)   : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Stream[U]] = ???
  def apply(v: samples) : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Vector[U]] = ???

  def apply(v: count)        : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Int] = ???
  def apply(v: countDistinct): In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Int] = ???
  def apply(v: sum)          : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: avg)          : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: median)       : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: variance)     : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???
  def apply(v: stddev)       : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Double] = ???

  val length:  In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Int] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]                                              = ???
  def tx[ns1[_]            , ns2[_,_]            , in2_1[_,_]            , in2_2[_,_,_]            , a                  ] (m1: Molecule_1[ns1, ns2, in2_1, in2_2, a])                  : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, a]                                           = ???
}

trait In_2_22[In2_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], P25[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], In3_22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], P26[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS {
  val e_         : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val a_         : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val v_         : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], Nothing] = ???
  val ns_        : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val txInstant_ : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], Nothing] = ???
  val txT_       : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val txAdded_   : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], Nothing] = ???

  def tx[ns0               , ns1[_]              , in2_0[_]              , in2_1[_,_]                                   ] (m0: Molecule_0[ns0, ns1, in2_0, in2_1])                     : In2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]                                              = ???
}
