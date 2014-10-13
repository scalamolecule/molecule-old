package molecule.out
import java.util.Date
import molecule.dsl.schemaDSL.NS
import molecule.dsl.schemaDSL._
import scala.language.higherKinds


trait Molecule_0[Ns0, Ns1[_]] extends NS {
  val e          : Ns1[Long   ] with OneLong   [Ns1[Long   ], Nothing] = ???
  val a          : Ns1[String ] with OneString [Ns1[String ], Nothing] = ???
  val v          : Ns1[Any    ] with OneAny    [Ns1[Any    ], Nothing] = ???
  val ns         : Ns1[String ] with OneString [Ns1[String ], Nothing] = ???
  val txInstant  : Ns1[Date   ] with OneDate   [Ns1[Date   ], Nothing] = ???
  val txT        : Ns1[Long   ] with OneLong   [Ns1[Long   ], Nothing] = ???
  val txAdded    : Ns1[Boolean] with OneBoolean[Ns1[Boolean], Nothing] = ???

  val e_         : Ns0 with OneLong   [Ns0, Nothing] = ???
  val a_         : Ns0 with OneString [Ns0, Nothing] = ???
  val v_         : Ns0 with OneAny    [Ns0, Nothing] = ???
  val ns_        : Ns0 with OneString [Ns0, Nothing] = ???
  val txInstant_ : Ns0 with OneDate   [Ns0, Nothing] = ???
  val txT_       : Ns0 with OneLong   [Ns0, Nothing] = ???
  val txAdded_   : Ns0 with OneBoolean[Ns0, Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns0                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns1[a]                                    = ???

  // If we supply 2 or more tx attributes we return a generic molecule
  // This means that you can't continue expanding the molecule from the initial namespace anymore, so you'll
  // want to have the tx data defined in the end of the molecule
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_2[Any, Any, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_3[Any, Any, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_4[Any, Any, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_5[Any, Any, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_6[Any, Any, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_7[Any, Any, a, b, c, d, e, f, g] = ???
}

trait Molecule_1[Ns1[_], Ns2[_,_], A] extends NS {
  val e         : Ns2[A, Long   ] with OneLong   [Ns2[A, Long   ], Nothing] = ???
  val a         : Ns2[A, String ] with OneString [Ns2[A, String ], Nothing] = ???
  val v         : Ns2[A, Any    ] with OneAny    [Ns2[A, Any    ], Nothing] = ???
  val ns        : Ns2[A, String ] with OneString [Ns2[A, String ], Nothing] = ???
  val txInstant : Ns2[A, Date   ] with OneDate   [Ns2[A, Date   ], Nothing] = ???
  val txT       : Ns2[A, Long   ] with OneLong   [Ns2[A, Long   ], Nothing] = ???
  val txAdded   : Ns2[A, Boolean] with OneBoolean[Ns2[A, Boolean], Nothing] = ???

  val e_          : Ns1[Long   ] with OneLong   [Ns1[Long   ], Nothing] = ???
  val a_          : Ns1[String ] with OneString [Ns1[String ], Nothing] = ???
  val v_          : Ns1[Any    ] with OneAny    [Ns1[Any    ], Nothing] = ???
  val ns_         : Ns1[String ] with OneString [Ns1[String ], Nothing] = ???
  val txInstant_  : Ns1[Date   ] with OneDate   [Ns1[Date   ], Nothing] = ???
  val txT_        : Ns1[Long   ] with OneLong   [Ns1[Long   ], Nothing] = ???
  val txAdded_    : Ns1[Boolean] with OneBoolean[Ns1[Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns1[A]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns2[A, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_3[Any, Any, A, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_4[Any, Any, A, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_5[Any, Any, A, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_6[Any, Any, A, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_7[Any, Any, A, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_8[Any, Any, A, a, b, c, d, e, f, g] = ???
}

trait Molecule_2[Ns2[_,_], Ns3[_,_,_], A, B] extends NS {
  val e         : Ns3[A, B, Long   ] with OneLong   [Ns3[A, B, Long   ], Nothing] = ???
  val a         : Ns3[A, B, String ] with OneString [Ns3[A, B, String ], Nothing] = ???
  val v         : Ns3[A, B, Any    ] with OneAny    [Ns3[A, B, Any    ], Nothing] = ???
  val ns        : Ns3[A, B, String ] with OneString [Ns3[A, B, String ], Nothing] = ???
  val txInstant : Ns3[A, B, Date   ] with OneDate   [Ns3[A, B, Date   ], Nothing] = ???
  val txT       : Ns3[A, B, Long   ] with OneLong   [Ns3[A, B, Long   ], Nothing] = ???
  val txAdded   : Ns3[A, B, Boolean] with OneBoolean[Ns3[A, B, Boolean], Nothing] = ???

  val e_         : Ns2[A, Long   ] with OneLong   [Ns2[A, Long   ], Nothing] = ???
  val a_         : Ns2[A, String ] with OneString [Ns2[A, String ], Nothing] = ???
  val v_         : Ns2[A, Any    ] with OneAny    [Ns2[A, Any    ], Nothing] = ???
  val ns_        : Ns2[A, String ] with OneString [Ns2[A, String ], Nothing] = ???
  val txInstant_ : Ns2[A, Date   ] with OneDate   [Ns2[A, Date   ], Nothing] = ???
  val txT_       : Ns2[A, Long   ] with OneLong   [Ns2[A, Long   ], Nothing] = ???
  val txAdded_   : Ns2[A, Boolean] with OneBoolean[Ns2[A, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns2[A, B]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns3[A, B, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_4[Any, Any, A, B, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_5[Any, Any, A, B, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_6[Any, Any, A, B, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_7[Any, Any, A, B, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_8[Any, Any, A, B, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_9[Any, Any, A, B, a, b, c, d, e, f, g] = ???
}

trait Molecule_3[Ns3[_,_,_], Ns4[_,_,_,_], A, B, C] extends NS {
  val e         : Ns4[A, B, C, Long   ] with OneLong   [Ns4[A, B, C, Long   ], Nothing] = ???
  val a         : Ns4[A, B, C, String ] with OneString [Ns4[A, B, C, String ], Nothing] = ???
  val v         : Ns4[A, B, C, Any    ] with OneAny    [Ns4[A, B, C, Any    ], Nothing] = ???
  val ns        : Ns4[A, B, C, String ] with OneString [Ns4[A, B, C, String ], Nothing] = ???
  val txInstant : Ns4[A, B, C, Date   ] with OneDate   [Ns4[A, B, C, Date   ], Nothing] = ???
  val txT       : Ns4[A, B, C, Long   ] with OneLong   [Ns4[A, B, C, Long   ], Nothing] = ???
  val txAdded   : Ns4[A, B, C, Boolean] with OneBoolean[Ns4[A, B, C, Boolean], Nothing] = ???

  val e_         : Ns3[A, B, Long   ] with OneLong   [Ns3[A, B, Long   ], Nothing] = ???
  val a_         : Ns3[A, B, String ] with OneString [Ns3[A, B, String ], Nothing] = ???
  val v_         : Ns3[A, B, Any    ] with OneAny    [Ns3[A, B, Any    ], Nothing] = ???
  val ns_        : Ns3[A, B, String ] with OneString [Ns3[A, B, String ], Nothing] = ???
  val txInstant_ : Ns3[A, B, Date   ] with OneDate   [Ns3[A, B, Date   ], Nothing] = ???
  val txT_       : Ns3[A, B, Long   ] with OneLong   [Ns3[A, B, Long   ], Nothing] = ???
  val txAdded_   : Ns3[A, B, Boolean] with OneBoolean[Ns3[A, B, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns3 [A, B, C]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns4 [A, B, C, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_5 [Any, Any, A, B, C, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_6 [Any, Any, A, B, C, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_7 [Any, Any, A, B, C, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_8 [Any, Any, A, B, C, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_9 [Any, Any, A, B, C, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_10[Any, Any, A, B, C, a, b, c, d, e, f, g] = ???
}

trait Molecule_4[Ns4[_,_,_,_], Ns5[_,_,_,_,_], A, B, C, D] extends NS {
  val e         : Ns5[A, B, C, D, Long   ] with OneLong   [Ns5[A, B, C, D, Long   ], Nothing] = ???
  val a         : Ns5[A, B, C, D, String ] with OneString [Ns5[A, B, C, D, String ], Nothing] = ???
  val v         : Ns5[A, B, C, D, Any    ] with OneAny    [Ns5[A, B, C, D, Any    ], Nothing] = ???
  val ns        : Ns5[A, B, C, D, String ] with OneString [Ns5[A, B, C, D, String ], Nothing] = ???
  val txInstant : Ns5[A, B, C, D, Date   ] with OneDate   [Ns5[A, B, C, D, Date   ], Nothing] = ???
  val txT       : Ns5[A, B, C, D, Long   ] with OneLong   [Ns5[A, B, C, D, Long   ], Nothing] = ???
  val txAdded   : Ns5[A, B, C, D, Boolean] with OneBoolean[Ns5[A, B, C, D, Boolean], Nothing] = ???

  val e_         : Ns4[A, B, C, Long   ] with OneLong   [Ns4[A, B, C, Long   ], Nothing] = ???
  val a_         : Ns4[A, B, C, String ] with OneString [Ns4[A, B, C, String ], Nothing] = ???
  val v_         : Ns4[A, B, C, Any    ] with OneAny    [Ns4[A, B, C, Any    ], Nothing] = ???
  val ns_        : Ns4[A, B, C, String ] with OneString [Ns4[A, B, C, String ], Nothing] = ???
  val txInstant_ : Ns4[A, B, C, Date   ] with OneDate   [Ns4[A, B, C, Date   ], Nothing] = ???
  val txT_       : Ns4[A, B, C, Long   ] with OneLong   [Ns4[A, B, C, Long   ], Nothing] = ???
  val txAdded_   : Ns4[A, B, C, Boolean] with OneBoolean[Ns4[A, B, C, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns4 [A, B, C, D]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns5 [A, B, C, D, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_6 [Any, Any, A, B, C, D, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_7 [Any, Any, A, B, C, D, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_8 [Any, Any, A, B, C, D, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_9 [Any, Any, A, B, C, D, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_10[Any, Any, A, B, C, D, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_11[Any, Any, A, B, C, D, a, b, c, d, e, f, g] = ???
}

trait Molecule_5[Ns5[_,_,_,_,_], Ns6[_,_,_,_,_,_], A, B, C, D, E] extends NS {
  val e         : Ns6[A, B, C, D, E, Long   ] with OneLong   [Ns6[A, B, C, D, E, Long   ], Nothing] = ???
  val a         : Ns6[A, B, C, D, E, String ] with OneString [Ns6[A, B, C, D, E, String ], Nothing] = ???
  val v         : Ns6[A, B, C, D, E, Any    ] with OneAny    [Ns6[A, B, C, D, E, Any    ], Nothing] = ???
  val ns        : Ns6[A, B, C, D, E, String ] with OneString [Ns6[A, B, C, D, E, String ], Nothing] = ???
  val txInstant : Ns6[A, B, C, D, E, Date   ] with OneDate   [Ns6[A, B, C, D, E, Date   ], Nothing] = ???
  val txT       : Ns6[A, B, C, D, E, Long   ] with OneLong   [Ns6[A, B, C, D, E, Long   ], Nothing] = ???
  val txAdded   : Ns6[A, B, C, D, E, Boolean] with OneBoolean[Ns6[A, B, C, D, E, Boolean], Nothing] = ???

  val e_         : Ns5[A, B, C, D, Long   ] with OneLong   [Ns5[A, B, C, D, Long   ], Nothing] = ???
  val a_         : Ns5[A, B, C, D, String ] with OneString [Ns5[A, B, C, D, String ], Nothing] = ???
  val v_         : Ns5[A, B, C, D, Any    ] with OneAny    [Ns5[A, B, C, D, Any    ], Nothing] = ???
  val ns_        : Ns5[A, B, C, D, String ] with OneString [Ns5[A, B, C, D, String ], Nothing] = ???
  val txInstant_ : Ns5[A, B, C, D, Date   ] with OneDate   [Ns5[A, B, C, D, Date   ], Nothing] = ???
  val txT_       : Ns5[A, B, C, D, Long   ] with OneLong   [Ns5[A, B, C, D, Long   ], Nothing] = ???
  val txAdded_   : Ns5[A, B, C, D, Boolean] with OneBoolean[Ns5[A, B, C, D, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns5 [A, B, C, D, E]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns6 [A, B, C, D, E, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_7 [Any, Any, A, B, C, D, E, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_8 [Any, Any, A, B, C, D, E, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_9 [Any, Any, A, B, C, D, E, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_10[Any, Any, A, B, C, D, E, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_11[Any, Any, A, B, C, D, E, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_12[Any, Any, A, B, C, D, E, a, b, c, d, e, f, g] = ???
}

trait Molecule_6[Ns6[_,_,_,_,_,_], Ns7[_,_,_,_,_,_,_], A, B, C, D, E, F] extends NS {
  val e         : Ns7[A, B, C, D, E, F, Long   ] with OneLong   [Ns7[A, B, C, D, E, F, Long   ], Nothing] = ???
  val a         : Ns7[A, B, C, D, E, F, String ] with OneString [Ns7[A, B, C, D, E, F, String ], Nothing] = ???
  val v         : Ns7[A, B, C, D, E, F, Any    ] with OneAny    [Ns7[A, B, C, D, E, F, Any    ], Nothing] = ???
  val ns        : Ns7[A, B, C, D, E, F, String ] with OneString [Ns7[A, B, C, D, E, F, String ], Nothing] = ???
  val txInstant : Ns7[A, B, C, D, E, F, Date   ] with OneDate   [Ns7[A, B, C, D, E, F, Date   ], Nothing] = ???
  val txT       : Ns7[A, B, C, D, E, F, Long   ] with OneLong   [Ns7[A, B, C, D, E, F, Long   ], Nothing] = ???
  val txAdded   : Ns7[A, B, C, D, E, F, Boolean] with OneBoolean[Ns7[A, B, C, D, E, F, Boolean], Nothing] = ???

  val e_         : Ns6[A, B, C, D, E, Long   ] with OneLong   [Ns6[A, B, C, D, E, Long   ], Nothing] = ???
  val a_         : Ns6[A, B, C, D, E, String ] with OneString [Ns6[A, B, C, D, E, String ], Nothing] = ???
  val v_         : Ns6[A, B, C, D, E, Any    ] with OneAny    [Ns6[A, B, C, D, E, Any    ], Nothing] = ???
  val ns_        : Ns6[A, B, C, D, E, String ] with OneString [Ns6[A, B, C, D, E, String ], Nothing] = ???
  val txInstant_ : Ns6[A, B, C, D, E, Date   ] with OneDate   [Ns6[A, B, C, D, E, Date   ], Nothing] = ???
  val txT_       : Ns6[A, B, C, D, E, Long   ] with OneLong   [Ns6[A, B, C, D, E, Long   ], Nothing] = ???
  val txAdded_   : Ns6[A, B, C, D, E, Boolean] with OneBoolean[Ns6[A, B, C, D, E, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns6 [A, B, C, D, E, F]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns7 [A, B, C, D, E, F, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_8 [Any, Any, A, B, C, D, E, F, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_9 [Any, Any, A, B, C, D, E, F, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_10[Any, Any, A, B, C, D, E, F, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_11[Any, Any, A, B, C, D, E, F, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_12[Any, Any, A, B, C, D, E, F, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_13[Any, Any, A, B, C, D, E, F, a, b, c, d, e, f, g] = ???
}

trait Molecule_7[Ns7[_,_,_,_,_,_,_], Ns8[_,_,_,_,_,_,_,_], A, B, C, D, E, F, G] extends NS {
  val e         : Ns8[A, B, C, D, E, F, G, Long   ] with OneLong   [Ns8[A, B, C, D, E, F, G, Long   ], Nothing] = ???
  val a         : Ns8[A, B, C, D, E, F, G, String ] with OneString [Ns8[A, B, C, D, E, F, G, String ], Nothing] = ???
  val v         : Ns8[A, B, C, D, E, F, G, Any    ] with OneAny    [Ns8[A, B, C, D, E, F, G, Any    ], Nothing] = ???
  val ns        : Ns8[A, B, C, D, E, F, G, String ] with OneString [Ns8[A, B, C, D, E, F, G, String ], Nothing] = ???
  val txInstant : Ns8[A, B, C, D, E, F, G, Date   ] with OneDate   [Ns8[A, B, C, D, E, F, G, Date   ], Nothing] = ???
  val txT       : Ns8[A, B, C, D, E, F, G, Long   ] with OneLong   [Ns8[A, B, C, D, E, F, G, Long   ], Nothing] = ???
  val txAdded   : Ns8[A, B, C, D, E, F, G, Boolean] with OneBoolean[Ns8[A, B, C, D, E, F, G, Boolean], Nothing] = ???

  val e_         : Ns7[A, B, C, D, E, F, Long   ] with OneLong   [Ns7[A, B, C, D, E, F, Long   ], Nothing] = ???
  val a_         : Ns7[A, B, C, D, E, F, String ] with OneString [Ns7[A, B, C, D, E, F, String ], Nothing] = ???
  val v_         : Ns7[A, B, C, D, E, F, Any    ] with OneAny    [Ns7[A, B, C, D, E, F, Any    ], Nothing] = ???
  val ns_        : Ns7[A, B, C, D, E, F, String ] with OneString [Ns7[A, B, C, D, E, F, String ], Nothing] = ???
  val txInstant_ : Ns7[A, B, C, D, E, F, Date   ] with OneDate   [Ns7[A, B, C, D, E, F, Date   ], Nothing] = ???
  val txT_       : Ns7[A, B, C, D, E, F, Long   ] with OneLong   [Ns7[A, B, C, D, E, F, Long   ], Nothing] = ???
  val txAdded_   : Ns7[A, B, C, D, E, F, Boolean] with OneBoolean[Ns7[A, B, C, D, E, F, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns7 [A, B, C, D, E, F, G]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns8 [A, B, C, D, E, F, G, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_9 [Any, Any, A, B, C, D, E, F, G, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_10[Any, Any, A, B, C, D, E, F, G, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_11[Any, Any, A, B, C, D, E, F, G, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_12[Any, Any, A, B, C, D, E, F, G, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_13[Any, Any, A, B, C, D, E, F, G, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_14[Any, Any, A, B, C, D, E, F, G, a, b, c, d, e, f, g] = ???
}

trait Molecule_8[Ns8[_,_,_,_,_,_,_,_], Ns9[_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H] extends NS {
  val e         : Ns9[A, B, C, D, E, F, G, H, Long   ] with OneLong   [Ns9[A, B, C, D, E, F, G, H, Long   ], Nothing] = ???
  val a         : Ns9[A, B, C, D, E, F, G, H, String ] with OneString [Ns9[A, B, C, D, E, F, G, H, String ], Nothing] = ???
  val v         : Ns9[A, B, C, D, E, F, G, H, Any    ] with OneAny    [Ns9[A, B, C, D, E, F, G, H, Any    ], Nothing] = ???
  val ns        : Ns9[A, B, C, D, E, F, G, H, String ] with OneString [Ns9[A, B, C, D, E, F, G, H, String ], Nothing] = ???
  val txInstant : Ns9[A, B, C, D, E, F, G, H, Date   ] with OneDate   [Ns9[A, B, C, D, E, F, G, H, Date   ], Nothing] = ???
  val txT       : Ns9[A, B, C, D, E, F, G, H, Long   ] with OneLong   [Ns9[A, B, C, D, E, F, G, H, Long   ], Nothing] = ???
  val txAdded   : Ns9[A, B, C, D, E, F, G, H, Boolean] with OneBoolean[Ns9[A, B, C, D, E, F, G, H, Boolean], Nothing] = ???

  val e_         : Ns8[A, B, C, D, E, F, G, Long   ] with OneLong   [Ns8[A, B, C, D, E, F, G, Long   ], Nothing] = ???
  val a_         : Ns8[A, B, C, D, E, F, G, String ] with OneString [Ns8[A, B, C, D, E, F, G, String ], Nothing] = ???
  val v_         : Ns8[A, B, C, D, E, F, G, Any    ] with OneAny    [Ns8[A, B, C, D, E, F, G, Any    ], Nothing] = ???
  val ns_        : Ns8[A, B, C, D, E, F, G, String ] with OneString [Ns8[A, B, C, D, E, F, G, String ], Nothing] = ???
  val txInstant_ : Ns8[A, B, C, D, E, F, G, Date   ] with OneDate   [Ns8[A, B, C, D, E, F, G, Date   ], Nothing] = ???
  val txT_       : Ns8[A, B, C, D, E, F, G, Long   ] with OneLong   [Ns8[A, B, C, D, E, F, G, Long   ], Nothing] = ???
  val txAdded_   : Ns8[A, B, C, D, E, F, G, Boolean] with OneBoolean[Ns8[A, B, C, D, E, F, G, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns8 [A, B, C, D, E, F, G, H]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns9 [A, B, C, D, E, F, G, H, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_10[Any, Any, A, B, C, D, E, F, G, H, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_11[Any, Any, A, B, C, D, E, F, G, H, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_12[Any, Any, A, B, C, D, E, F, G, H, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_13[Any, Any, A, B, C, D, E, F, G, H, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_14[Any, Any, A, B, C, D, E, F, G, H, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_15[Any, Any, A, B, C, D, E, F, G, H, a, b, c, d, e, f, g] = ???
}

trait Molecule_9[Ns9[_,_,_,_,_,_,_,_,_], Ns10[_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I] extends NS {
  val e         : Ns10[A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [Ns10[A, B, C, D, E, F, G, H, I, Long   ], Nothing] = ???
  val a         : Ns10[A, B, C, D, E, F, G, H, I, String ] with OneString [Ns10[A, B, C, D, E, F, G, H, I, String ], Nothing] = ???
  val v         : Ns10[A, B, C, D, E, F, G, H, I, Any    ] with OneAny    [Ns10[A, B, C, D, E, F, G, H, I, Any    ], Nothing] = ???
  val ns        : Ns10[A, B, C, D, E, F, G, H, I, String ] with OneString [Ns10[A, B, C, D, E, F, G, H, I, String ], Nothing] = ???
  val txInstant : Ns10[A, B, C, D, E, F, G, H, I, Date   ] with OneDate   [Ns10[A, B, C, D, E, F, G, H, I, Date   ], Nothing] = ???
  val txT       : Ns10[A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [Ns10[A, B, C, D, E, F, G, H, I, Long   ], Nothing] = ???
  val txAdded   : Ns10[A, B, C, D, E, F, G, H, I, Boolean] with OneBoolean[Ns10[A, B, C, D, E, F, G, H, I, Boolean], Nothing] = ???

  val e_         : Ns9[A, B, C, D, E, F, G, H, Long   ] with OneLong   [Ns9[A, B, C, D, E, F, G, H, Long   ], Nothing] = ???
  val a_         : Ns9[A, B, C, D, E, F, G, H, String ] with OneString [Ns9[A, B, C, D, E, F, G, H, String ], Nothing] = ???
  val v_         : Ns9[A, B, C, D, E, F, G, H, Any    ] with OneAny    [Ns9[A, B, C, D, E, F, G, H, Any    ], Nothing] = ???
  val ns_        : Ns9[A, B, C, D, E, F, G, H, String ] with OneString [Ns9[A, B, C, D, E, F, G, H, String ], Nothing] = ???
  val txInstant_ : Ns9[A, B, C, D, E, F, G, H, Date   ] with OneDate   [Ns9[A, B, C, D, E, F, G, H, Date   ], Nothing] = ???
  val txT_       : Ns9[A, B, C, D, E, F, G, H, Long   ] with OneLong   [Ns9[A, B, C, D, E, F, G, H, Long   ], Nothing] = ???
  val txAdded_   : Ns9[A, B, C, D, E, F, G, H, Boolean] with OneBoolean[Ns9[A, B, C, D, E, F, G, H, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns9 [A, B, C, D, E, F, G, H, I]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns10[A, B, C, D, E, F, G, H, I, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_11[Any, Any, A, B, C, D, E, F, G, H, I, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_12[Any, Any, A, B, C, D, E, F, G, H, I, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_13[Any, Any, A, B, C, D, E, F, G, H, I, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_14[Any, Any, A, B, C, D, E, F, G, H, I, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_15[Any, Any, A, B, C, D, E, F, G, H, I, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_16[Any, Any, A, B, C, D, E, F, G, H, I, a, b, c, d, e, f, g] = ???
}

  trait Molecule_10[Ns10[_,_,_,_,_,_,_,_,_,_], Ns11[_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J] extends NS {
    val e         : Ns11[A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [Ns11[A, B, C, D, E, F, G, H, I, J, Long   ], Nothing] = ???
    val a         : Ns11[A, B, C, D, E, F, G, H, I, J, String ] with OneString [Ns11[A, B, C, D, E, F, G, H, I, J, String ], Nothing] = ???
    val v         : Ns11[A, B, C, D, E, F, G, H, I, J, Any    ] with OneAny    [Ns11[A, B, C, D, E, F, G, H, I, J, Any    ], Nothing] = ???
    val ns        : Ns11[A, B, C, D, E, F, G, H, I, J, String ] with OneString [Ns11[A, B, C, D, E, F, G, H, I, J, String ], Nothing] = ???
    val txInstant : Ns11[A, B, C, D, E, F, G, H, I, J, Date   ] with OneDate   [Ns11[A, B, C, D, E, F, G, H, I, J, Date   ], Nothing] = ???
    val txT       : Ns11[A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [Ns11[A, B, C, D, E, F, G, H, I, J, Long   ], Nothing] = ???
    val txAdded   : Ns11[A, B, C, D, E, F, G, H, I, J, Boolean] with OneBoolean[Ns11[A, B, C, D, E, F, G, H, I, J, Boolean], Nothing] = ???

  val e_         : Ns10[A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [Ns10[A, B, C, D, E, F, G, H, I, Long   ], Nothing] = ???
  val a_         : Ns10[A, B, C, D, E, F, G, H, I, String ] with OneString [Ns10[A, B, C, D, E, F, G, H, I, String ], Nothing] = ???
  val v_         : Ns10[A, B, C, D, E, F, G, H, I, Any    ] with OneAny    [Ns10[A, B, C, D, E, F, G, H, I, Any    ], Nothing] = ???
  val ns_        : Ns10[A, B, C, D, E, F, G, H, I, String ] with OneString [Ns10[A, B, C, D, E, F, G, H, I, String ], Nothing] = ???
  val txInstant_ : Ns10[A, B, C, D, E, F, G, H, I, Date   ] with OneDate   [Ns10[A, B, C, D, E, F, G, H, I, Date   ], Nothing] = ???
  val txT_       : Ns10[A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [Ns10[A, B, C, D, E, F, G, H, I, Long   ], Nothing] = ???
  val txAdded_   : Ns10[A, B, C, D, E, F, G, H, I, Boolean] with OneBoolean[Ns10[A, B, C, D, E, F, G, H, I, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns10[A, B, C, D, E, F, G, H, I, J]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns11[A, B, C, D, E, F, G, H, I, J, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_12[Any, Any, A, B, C, D, E, F, G, H, I, J, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_13[Any, Any, A, B, C, D, E, F, G, H, I, J, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_14[Any, Any, A, B, C, D, E, F, G, H, I, J, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_15[Any, Any, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_16[Any, Any, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_17[Any, Any, A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f, g] = ???
}

trait Molecule_11[Ns11[_,_,_,_,_,_,_,_,_,_,_], Ns12[_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K] extends NS {
  val e         : Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ], Nothing] = ???
  val a         : Ns12[A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [Ns12[A, B, C, D, E, F, G, H, I, J, K, String ], Nothing] = ???
  val v         : Ns12[A, B, C, D, E, F, G, H, I, J, K, Any    ] with OneAny    [Ns12[A, B, C, D, E, F, G, H, I, J, K, Any    ], Nothing] = ???
  val ns        : Ns12[A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [Ns12[A, B, C, D, E, F, G, H, I, J, K, String ], Nothing] = ???
  val txInstant : Ns12[A, B, C, D, E, F, G, H, I, J, K, Date   ] with OneDate   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Date   ], Nothing] = ???
  val txT       : Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ], Nothing] = ???
  val txAdded   : Ns12[A, B, C, D, E, F, G, H, I, J, K, Boolean] with OneBoolean[Ns12[A, B, C, D, E, F, G, H, I, J, K, Boolean], Nothing] = ???

  val e_         : Ns11[A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [Ns11[A, B, C, D, E, F, G, H, I, J, Long   ], Nothing] = ???
  val a_         : Ns11[A, B, C, D, E, F, G, H, I, J, String ] with OneString [Ns11[A, B, C, D, E, F, G, H, I, J, String ], Nothing] = ???
  val v_         : Ns11[A, B, C, D, E, F, G, H, I, J, Any    ] with OneAny    [Ns11[A, B, C, D, E, F, G, H, I, J, Any    ], Nothing] = ???
  val ns_        : Ns11[A, B, C, D, E, F, G, H, I, J, String ] with OneString [Ns11[A, B, C, D, E, F, G, H, I, J, String ], Nothing] = ???
  val txInstant_ : Ns11[A, B, C, D, E, F, G, H, I, J, Date   ] with OneDate   [Ns11[A, B, C, D, E, F, G, H, I, J, Date   ], Nothing] = ???
  val txT_       : Ns11[A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [Ns11[A, B, C, D, E, F, G, H, I, J, Long   ], Nothing] = ???
  val txAdded_   : Ns11[A, B, C, D, E, F, G, H, I, J, Boolean] with OneBoolean[Ns11[A, B, C, D, E, F, G, H, I, J, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns11[A, B, C, D, E, F, G, H, I, J, K]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns12[A, B, C, D, E, F, G, H, I, J, K, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_13[Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_14[Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_15[Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_16[Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_17[Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_18[Any, Any, A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f, g] = ???
}

trait Molecule_12[Ns12[_,_,_,_,_,_,_,_,_,_,_,_], Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L] extends NS {
  val e         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ], Nothing] = ???
  val a         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ], Nothing] = ???
  val v         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Any    ] with OneAny    [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Any    ], Nothing] = ???
  val ns        : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ], Nothing] = ???
  val txInstant : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Date   ] with OneDate   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Date   ], Nothing] = ???
  val txT       : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ], Nothing] = ???
  val txAdded   : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Boolean] with OneBoolean[Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Boolean], Nothing] = ???

  val e_         : Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ], Nothing] = ???
  val a_         : Ns12[A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [Ns12[A, B, C, D, E, F, G, H, I, J, K, String ], Nothing] = ???
  val v_         : Ns12[A, B, C, D, E, F, G, H, I, J, K, Any    ] with OneAny    [Ns12[A, B, C, D, E, F, G, H, I, J, K, Any    ], Nothing] = ???
  val ns_        : Ns12[A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [Ns12[A, B, C, D, E, F, G, H, I, J, K, String ], Nothing] = ???
  val txInstant_ : Ns12[A, B, C, D, E, F, G, H, I, J, K, Date   ] with OneDate   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Date   ], Nothing] = ???
  val txT_       : Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [Ns12[A, B, C, D, E, F, G, H, I, J, K, Long   ], Nothing] = ???
  val txAdded_   : Ns12[A, B, C, D, E, F, G, H, I, J, K, Boolean] with OneBoolean[Ns12[A, B, C, D, E, F, G, H, I, J, K, Boolean], Nothing] = ???


  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns12[A, B, C, D, E, F, G, H, I, J, K, L]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_14[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_15[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_16[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_17[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_18[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_19[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f, g] = ???
}

trait Molecule_13[Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_], Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS {
  val e         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], Nothing] = ???
  val a         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ], Nothing] = ???
  val v         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ] with OneAny    [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ], Nothing] = ???
  val ns        : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ], Nothing] = ???
  val txInstant : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ] with OneDate   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ], Nothing] = ???
  val txT       : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], Nothing] = ???
  val txAdded   : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean] with OneBoolean[Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean], Nothing] = ???

  val e_         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ], Nothing] = ???
  val a_         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ], Nothing] = ???
  val v_         : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Any    ] with OneAny    [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Any    ], Nothing] = ???
  val ns_        : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, String ], Nothing] = ???
  val txInstant_ : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Date   ] with OneDate   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Date   ], Nothing] = ???
  val txT_       : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ], Nothing] = ???
  val txAdded_   : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Boolean] with OneBoolean[Ns13[A, B, C, D, E, F, G, H, I, J, K, L, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns13[A, B, C, D, E, F, G, H, I, J, K, L, M]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_15[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_16[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_17[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_18[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_19[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_20[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f, g] = ???
}

trait Molecule_14[Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS {
  val e         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], Nothing] = ???
  val a         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], Nothing] = ???
  val v         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ] with OneAny    [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ], Nothing] = ???
  val ns        : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], Nothing] = ???
  val txInstant : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ] with OneDate   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ], Nothing] = ???
  val txT       : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], Nothing] = ???
  val txAdded   : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean] with OneBoolean[Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean], Nothing] = ???

  val e_         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], Nothing] = ???
  val a_         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ], Nothing] = ???
  val v_         : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ] with OneAny    [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ], Nothing] = ???
  val ns_        : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ], Nothing] = ???
  val txInstant_ : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ] with OneDate   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ], Nothing] = ???
  val txT_       : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], Nothing] = ???
  val txAdded_   : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean] with OneBoolean[Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_16[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_17[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_18[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_19[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_20[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_21[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f, g] = ???
}

trait Molecule_15[Ns15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS {
  val e         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], Nothing] = ???
  val a         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], Nothing] = ???
  val v         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ] with OneAny    [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ], Nothing] = ???
  val ns        : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], Nothing] = ???
  val txInstant : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ] with OneDate   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ], Nothing] = ???
  val txT       : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], Nothing] = ???
  val txAdded   : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean] with OneBoolean[Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean], Nothing] = ???

  val e_         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], Nothing] = ???
  val a_         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], Nothing] = ???
  val v_         : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ] with OneAny    [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ], Nothing] = ???
  val ns_        : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], Nothing] = ???
  val txInstant_ : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ] with OneDate   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ], Nothing] = ???
  val txT_       : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], Nothing] = ???
  val txAdded_   : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean] with OneBoolean[Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean], Nothing] = ???

  def tx[ns0, ns1[_]]                                                  (m0: Molecule_0[ns0, ns1])                     : Ns15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]                                       = ???
  def tx[ns1[_], ns2[_,_], a]                                          (m1: Molecule_1[ns1, ns2, a])                  : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a]                                    = ???
  def tx[ns2[_,_], ns3[_,_,_], a, b]                                   (m2: Molecule_2[ns2, ns3, a, b])               : Molecule_17[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b]                = ???
  def tx[ns3[_,_,_], ns4[_,_,_,_], a, b, c]                            (m3: Molecule_3[ns3, ns4, a, b, c])            : Molecule_18[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c]             = ???
  def tx[ns4[_,_,_,_], ns5[_,_,_,_,_], a, b, c, d]                     (m4: Molecule_4[ns4, ns5, a, b, c, d])         : Molecule_19[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d]          = ???
  def tx[ns5[_,_,_,_,_], ns6[_,_,_,_,_,_], a, b, c, d, e]              (m5: Molecule_5[ns5, ns6, a, b, c, d, e])      : Molecule_20[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e]       = ???
  def tx[ns6[_,_,_,_,_,_], ns7[_,_,_,_,_,_,_], a, b, c, d, e, f]       (m6: Molecule_6[ns6, ns7, a, b, c, d, e, f])   : Molecule_21[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f]    = ???
  def tx[ns7[_,_,_,_,_,_,_], ns8[_,_,_,_,_,_,_,_], a, b, c, d, e, f, g](m7: Molecule_7[ns7, ns8, a, b, c, d, e, f, g]): Molecule_22[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f, g] = ???
}

trait Molecule_16[Ns16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS {
  val e         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], Nothing] = ???
  val a         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], Nothing] = ???
  val v         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ] with OneAny    [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ], Nothing] = ???
  val ns        : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], Nothing] = ???
  val txInstant : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ] with OneDate   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ], Nothing] = ???
  val txT       : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], Nothing] = ???
  val txAdded   : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean] with OneBoolean[Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean], Nothing] = ???

  val e_         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], Nothing] = ???
  val a_         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], Nothing] = ???
  val v_         : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ] with OneAny    [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ], Nothing] = ???
  val ns_        : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], Nothing] = ???
  val txInstant_ : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ] with OneDate   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ], Nothing] = ???
  val txT_       : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], Nothing] = ???
  val txAdded_   : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean] with OneBoolean[Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean], Nothing] = ???

  def tx[ns1, ns2]                  (m0: Molecule_0[Any, Any])                  : Ns16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]                                    = ???
  def tx[ns1, ns2, a]               (m1: Molecule_1[Any, Any, a])               : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a]                                 = ???
  def tx[ns1, ns2, a, b]            (m2: Molecule_2[Any, Any, a, b])            : Molecule_18[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b]             = ???
  def tx[ns1, ns2, a, b, c]         (m3: Molecule_3[Any, Any, a, b, c])         : Molecule_19[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c]          = ???
  def tx[ns1, ns2, a, b, c, d]      (m4: Molecule_4[Any, Any, a, b, c, d])      : Molecule_20[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d]       = ???
  def tx[ns1, ns2, a, b, c, d, e]   (m5: Molecule_5[Any, Any, a, b, c, d, e])   : Molecule_21[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e]    = ???
  def tx[ns1, ns2, a, b, c, d, e, f](m6: Molecule_6[Any, Any, a, b, c, d, e, f]): Molecule_22[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e, f] = ???
}

trait Molecule_17[Ns17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS {
  val e         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], Nothing] = ???
  val a         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], Nothing] = ???
  val v         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ] with OneAny    [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ], Nothing] = ???
  val ns        : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], Nothing] = ???
  val txInstant : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ] with OneDate   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ], Nothing] = ???
  val txT       : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], Nothing] = ???
  val txAdded   : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean] with OneBoolean[Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean], Nothing] = ???

  val e_         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], Nothing] = ???
  val a_         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], Nothing] = ???
  val v_         : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ] with OneAny    [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ], Nothing] = ???
  val ns_        : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], Nothing] = ???
  val txInstant_ : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ] with OneDate   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ], Nothing] = ???
  val txT_       : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], Nothing] = ???
  val txAdded_   : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean] with OneBoolean[Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean], Nothing] = ???

  def tx[ns1, ns2]               (m0: Molecule_0[Any, Any])               : Ns17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]                                 = ???
  def tx[ns1, ns2, a]            (m1: Molecule_1[Any, Any, a])            : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a]                              = ???
  def tx[ns1, ns2, a, b]         (m2: Molecule_2[Any, Any, a, b])         : Molecule_19[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b]          = ???
  def tx[ns1, ns2, a, b, c]      (m3: Molecule_3[Any, Any, a, b, c])      : Molecule_20[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c]       = ???
  def tx[ns1, ns2, a, b, c, d]   (m4: Molecule_4[Any, Any, a, b, c, d])   : Molecule_21[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d]    = ???
  def tx[ns1, ns2, a, b, c, d, e](m5: Molecule_5[Any, Any, a, b, c, d, e]): Molecule_22[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d, e] = ???
}

trait Molecule_18[Ns18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS {
  val e         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], Nothing] = ???
  val a         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], Nothing] = ???
  val v         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ] with OneAny    [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ], Nothing] = ???
  val ns        : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], Nothing] = ???
  val txInstant : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ] with OneDate   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ], Nothing] = ???
  val txT       : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], Nothing] = ???
  val txAdded   : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean] with OneBoolean[Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean], Nothing] = ???

  val e_         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], Nothing] = ???
  val a_         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], Nothing] = ???
  val v_         : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ] with OneAny    [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ], Nothing] = ???
  val ns_        : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], Nothing] = ???
  val txInstant_ : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ] with OneDate   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ], Nothing] = ???
  val txT_       : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], Nothing] = ???
  val txAdded_   : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean] with OneBoolean[Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean], Nothing] = ???

  def tx[ns1, ns2]            (m0: Molecule_0[Any, Any])            : Ns18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]                              = ???
  def tx[ns1, ns2, a]         (m1: Molecule_1[Any, Any, a])         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a]                           = ???
  def tx[ns1, ns2, a, b]      (m2: Molecule_2[Any, Any, a, b])      : Molecule_20[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b]       = ???
  def tx[ns1, ns2, a, b, c]   (m3: Molecule_3[Any, Any, a, b, c])   : Molecule_21[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c]    = ???
  def tx[ns1, ns2, a, b, c, d](m4: Molecule_4[Any, Any, a, b, c, d]): Molecule_22[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c, d] = ???
}

trait Molecule_19[Ns19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS {
  val e         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], Nothing] = ???
  val a         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], Nothing] = ???
  val v         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ] with OneAny    [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ], Nothing] = ???
  val ns        : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], Nothing] = ???
  val txInstant : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ] with OneDate   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ], Nothing] = ???
  val txT       : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], Nothing] = ???
  val txAdded   : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean] with OneBoolean[Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean], Nothing] = ???

  val e_         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], Nothing] = ???
  val a_         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], Nothing] = ???
  val v_         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ] with OneAny    [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ], Nothing] = ???
  val ns_        : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], Nothing] = ???
  val txInstant_ : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ] with OneDate   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ], Nothing] = ???
  val txT_       : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], Nothing] = ???
  val txAdded_   : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean] with OneBoolean[Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean], Nothing] = ???

  def tx[ns1, ns2]         (m0: Molecule_0[Any, Any])         : Ns19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]                           = ???
  def tx[ns1, ns2, a]      (m1: Molecule_1[Any, Any, a])      : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a]                        = ???
  def tx[ns1, ns2, a, b]   (m2: Molecule_2[Any, Any, a, b])   : Molecule_21[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b]    = ???
  def tx[ns1, ns2, a, b, c](m3: Molecule_3[Any, Any, a, b, c]): Molecule_22[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b, c] = ???
}

trait Molecule_20[Ns20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS {
  val e         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], Nothing] = ???
  val a         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], Nothing] = ???
  val v         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ] with OneAny    [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ], Nothing] = ???
  val ns        : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], Nothing] = ???
  val txInstant : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ] with OneDate   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ], Nothing] = ???
  val txT       : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], Nothing] = ???
  val txAdded   : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean] with OneBoolean[Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean], Nothing] = ???

  val e_         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], Nothing] = ???
  val a_         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], Nothing] = ???
  val v_         : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ] with OneAny    [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ], Nothing] = ???
  val ns_        : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], Nothing] = ???
  val txInstant_ : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ] with OneDate   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ], Nothing] = ???
  val txT_       : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], Nothing] = ???
  val txAdded_   : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean] with OneBoolean[Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean], Nothing] = ???

  def tx[ns1, ns2]      (m0: Molecule_0[Any, Any])      : Ns20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]                        = ???
  def tx[ns1, ns2, a]   (m1: Molecule_1[Any, Any, a])   : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a]                     = ???
  def tx[ns1, ns2, a, b](m2: Molecule_2[Any, Any, a, b]): Molecule_22[Any, Any, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a, b] = ???
}

trait Molecule_21[Ns21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS {
  val e         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val a         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val v         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], Nothing] = ???
  val ns        : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val txInstant : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], Nothing] = ???
  val txT       : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val txAdded   : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], Nothing] = ???

  val e_         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], Nothing] = ???
  val a_         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], Nothing] = ???
  val v_         : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ] with OneAny    [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ], Nothing] = ???
  val ns_        : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], Nothing] = ???
  val txInstant_ : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ] with OneDate   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ], Nothing] = ???
  val txT_       : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], Nothing] = ???
  val txAdded_   : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean] with OneBoolean[Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean], Nothing] = ???

  def tx[ns1, ns2]   (m0: Molecule_0[Any, Any])   : Ns21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]    = ???
  def tx[ns1, ns2, a](m1: Molecule_1[Any, Any, a]): Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, a] = ???
}

trait Molecule_22[Ns22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Nothing, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS {
  val e_         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val a_         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val v_         : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], Nothing] = ???
  val ns_        : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val txInstant_ : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], Nothing] = ???
  val txT_       : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val txAdded_   : Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[Ns22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], Nothing] = ???
}
