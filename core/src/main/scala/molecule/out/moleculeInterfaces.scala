package molecule.out
import java.util.Date
import molecule.dsl.schemaDSL.NS
import molecule.dsl.schemaDSL._

trait Molecule_0 extends NS {
//  val e         : Molecule_1[Long   ] with OneLong   [Molecule_1[Long   ], Nothing] = ???
//  val a         : Molecule_1[String ] with OneString [Molecule_1[String ], Nothing] = ???
  val v         : Molecule_1[Any    ] with OneAny    [Molecule_1[Any    ], Nothing] = ???
  val ns        : Molecule_1[String ] with OneString [Molecule_1[String ], Nothing] = ???
  val txInstant : Molecule_1[Date   ] with OneDate   [Molecule_1[Date   ], Nothing] = ???
  val txT       : Molecule_1[Long   ] with OneLong   [Molecule_1[Long   ], Nothing] = ???
  val txAdded   : Molecule_1[Boolean] with OneBoolean[Molecule_1[Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_0                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_1[a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_2[a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_3[a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_4[a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_5[a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_6[a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_7[a, b, c, d, e, f, g] = ???
}

trait Molecule_1[A] extends NS {
//  val e         : Molecule_2[A, Long   ] with OneLong   [Molecule_2[A, Long   ], Nothing] = ???
//  val a         : Molecule_2[A, String ] with OneString [Molecule_2[A, String ], Nothing] = ???
  val v         : Molecule_2[A, Any    ] with OneAny    [Molecule_2[A, Any    ], Nothing] = ???
  val ns        : Molecule_2[A, String ] with OneString [Molecule_2[A, String ], Nothing] = ???
  val txInstant : Molecule_2[A, Date   ] with OneDate   [Molecule_2[A, Date   ], Nothing] = ???
  val txT       : Molecule_2[A, Long   ] with OneLong   [Molecule_2[A, Long   ], Nothing] = ???
  val txAdded   : Molecule_2[A, Boolean] with OneBoolean[Molecule_2[A, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_1[A]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_2[A, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_3[A, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_4[A, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_5[A, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_6[A, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_7[A, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_8[A, a, b, c, d, e, f, g] = ???
}

trait Molecule_2[A, B] extends NS {
//  val e         : Molecule_3[A, B, Long   ] with OneLong   [Molecule_3[A, B, Long   ], Nothing] = ???
//  val a         : Molecule_3[A, B, String ] with OneString [Molecule_3[A, B, String ], Nothing] = ???
  val v         : Molecule_3[A, B, Any    ] with OneAny    [Molecule_3[A, B, Any    ], Nothing] = ???
  val ns        : Molecule_3[A, B, String ] with OneString [Molecule_3[A, B, String ], Nothing] = ???
  val txInstant : Molecule_3[A, B, Date   ] with OneDate   [Molecule_3[A, B, Date   ], Nothing] = ???
  val txT       : Molecule_3[A, B, Long   ] with OneLong   [Molecule_3[A, B, Long   ], Nothing] = ???
  val txAdded   : Molecule_3[A, B, Boolean] with OneBoolean[Molecule_3[A, B, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_2[A, B]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_3[A, B, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_4[A, B, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_5[A, B, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_6[A, B, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_7[A, B, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_8[A, B, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_9[A, B, a, b, c, d, e, f, g] = ???
}

trait Molecule_3[A, B, C] extends NS {
//  val e         : Molecule_4[A, B, C, Long   ] with OneLong   [Molecule_4[A, B, C, Long   ], Nothing] = ???
//  val a         : Molecule_4[A, B, C, String ] with OneString [Molecule_4[A, B, C, String ], Nothing] = ???
  val v         : Molecule_4[A, B, C, Any    ] with OneAny    [Molecule_4[A, B, C, Any    ], Nothing] = ???
  val ns        : Molecule_4[A, B, C, String ] with OneString [Molecule_4[A, B, C, String ], Nothing] = ???
  val txInstant : Molecule_4[A, B, C, Date   ] with OneDate   [Molecule_4[A, B, C, Date   ], Nothing] = ???
  val txT       : Molecule_4[A, B, C, Long   ] with OneLong   [Molecule_4[A, B, C, Long   ], Nothing] = ???
  val txAdded   : Molecule_4[A, B, C, Boolean] with OneBoolean[Molecule_4[A, B, C, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_3 [A, B, C]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_4 [A, B, C, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_5 [A, B, C, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_6 [A, B, C, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_7 [A, B, C, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_8 [A, B, C, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_9 [A, B, C, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_10[A, B, C, a, b, c, d, e, f, g] = ???
}

trait Molecule_4[A, B, C, D] extends NS {
//  val e         : Molecule_5[A, B, C, D, Long   ] with OneLong   [Molecule_5[A, B, C, D, Long   ], Nothing] = ???
//  val a         : Molecule_5[A, B, C, D, String ] with OneString [Molecule_5[A, B, C, D, String ], Nothing] = ???
  val v         : Molecule_5[A, B, C, D, Any    ] with OneAny    [Molecule_5[A, B, C, D, Any    ], Nothing] = ???
  val ns        : Molecule_5[A, B, C, D, String ] with OneString [Molecule_5[A, B, C, D, String ], Nothing] = ???
  val txInstant : Molecule_5[A, B, C, D, Date   ] with OneDate   [Molecule_5[A, B, C, D, Date   ], Nothing] = ???
  val txT       : Molecule_5[A, B, C, D, Long   ] with OneLong   [Molecule_5[A, B, C, D, Long   ], Nothing] = ???
  val txAdded   : Molecule_5[A, B, C, D, Boolean] with OneBoolean[Molecule_5[A, B, C, D, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_4 [A, B, C, D]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_5 [A, B, C, D, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_6 [A, B, C, D, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_7 [A, B, C, D, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_8 [A, B, C, D, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_9 [A, B, C, D, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_10[A, B, C, D, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_11[A, B, C, D, a, b, c, d, e, f, g] = ???
}

trait Molecule_5[A, B, C, D, E] extends NS {
//  val e         : Molecule_6[A, B, C, D, E, Long   ] with OneLong   [Molecule_6[A, B, C, D, E, Long   ], Nothing] = ???
//  val a         : Molecule_6[A, B, C, D, E, String ] with OneString [Molecule_6[A, B, C, D, E, String ], Nothing] = ???
  val v         : Molecule_6[A, B, C, D, E, Any    ] with OneAny    [Molecule_6[A, B, C, D, E, Any    ], Nothing] = ???
  val ns        : Molecule_6[A, B, C, D, E, String ] with OneString [Molecule_6[A, B, C, D, E, String ], Nothing] = ???
  val txInstant : Molecule_6[A, B, C, D, E, Date   ] with OneDate   [Molecule_6[A, B, C, D, E, Date   ], Nothing] = ???
  val txT       : Molecule_6[A, B, C, D, E, Long   ] with OneLong   [Molecule_6[A, B, C, D, E, Long   ], Nothing] = ???
  val txAdded   : Molecule_6[A, B, C, D, E, Boolean] with OneBoolean[Molecule_6[A, B, C, D, E, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_5 [A, B, C, D, E]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_6 [A, B, C, D, E, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_7 [A, B, C, D, E, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_8 [A, B, C, D, E, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_9 [A, B, C, D, E, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_10[A, B, C, D, E, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_11[A, B, C, D, E, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_12[A, B, C, D, E, a, b, c, d, e, f, g] = ???
}

trait Molecule_6[A, B, C, D, E, F] extends NS {
//  val e         : Molecule_7[A, B, C, D, E, F, Long   ] with OneLong   [Molecule_7[A, B, C, D, E, F, Long   ], Nothing] = ???
//  val a         : Molecule_7[A, B, C, D, E, F, String ] with OneString [Molecule_7[A, B, C, D, E, F, String ], Nothing] = ???
  val v         : Molecule_7[A, B, C, D, E, F, Any    ] with OneAny    [Molecule_7[A, B, C, D, E, F, Any    ], Nothing] = ???
  val ns        : Molecule_7[A, B, C, D, E, F, String ] with OneString [Molecule_7[A, B, C, D, E, F, String ], Nothing] = ???
  val txInstant : Molecule_7[A, B, C, D, E, F, Date   ] with OneDate   [Molecule_7[A, B, C, D, E, F, Date   ], Nothing] = ???
  val txT       : Molecule_7[A, B, C, D, E, F, Long   ] with OneLong   [Molecule_7[A, B, C, D, E, F, Long   ], Nothing] = ???
  val txAdded   : Molecule_7[A, B, C, D, E, F, Boolean] with OneBoolean[Molecule_7[A, B, C, D, E, F, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_6 [A, B, C, D, E, F]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_7 [A, B, C, D, E, F, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_8 [A, B, C, D, E, F, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_9 [A, B, C, D, E, F, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_10[A, B, C, D, E, F, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_11[A, B, C, D, E, F, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_12[A, B, C, D, E, F, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_13[A, B, C, D, E, F, a, b, c, d, e, f, g] = ???
}

trait Molecule_7[A, B, C, D, E, F, G] extends NS {
//  val e         : Molecule_8[A, B, C, D, E, F, G, Long   ] with OneLong   [Molecule_8[A, B, C, D, E, F, G, Long   ], Nothing] = ???
//  val a         : Molecule_8[A, B, C, D, E, F, G, String ] with OneString [Molecule_8[A, B, C, D, E, F, G, String ], Nothing] = ???
  val v         : Molecule_8[A, B, C, D, E, F, G, Any    ] with OneAny    [Molecule_8[A, B, C, D, E, F, G, Any    ], Nothing] = ???
  val ns        : Molecule_8[A, B, C, D, E, F, G, String ] with OneString [Molecule_8[A, B, C, D, E, F, G, String ], Nothing] = ???
  val txInstant : Molecule_8[A, B, C, D, E, F, G, Date   ] with OneDate   [Molecule_8[A, B, C, D, E, F, G, Date   ], Nothing] = ???
  val txT       : Molecule_8[A, B, C, D, E, F, G, Long   ] with OneLong   [Molecule_8[A, B, C, D, E, F, G, Long   ], Nothing] = ???
  val txAdded   : Molecule_8[A, B, C, D, E, F, G, Boolean] with OneBoolean[Molecule_8[A, B, C, D, E, F, G, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_7 [A, B, C, D, E, F, G]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_8 [A, B, C, D, E, F, G, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_9 [A, B, C, D, E, F, G, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_10[A, B, C, D, E, F, G, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_11[A, B, C, D, E, F, G, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_12[A, B, C, D, E, F, G, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_13[A, B, C, D, E, F, G, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_14[A, B, C, D, E, F, G, a, b, c, d, e, f, g] = ???
}

trait Molecule_8[A, B, C, D, E, F, G, H] extends NS {
//  val e         : Molecule_9[A, B, C, D, E, F, G, H, Long   ] with OneLong   [Molecule_9[A, B, C, D, E, F, G, H, Long   ], Nothing] = ???
//  val a         : Molecule_9[A, B, C, D, E, F, G, H, String ] with OneString [Molecule_9[A, B, C, D, E, F, G, H, String ], Nothing] = ???
  val v         : Molecule_9[A, B, C, D, E, F, G, H, Any    ] with OneAny    [Molecule_9[A, B, C, D, E, F, G, H, Any    ], Nothing] = ???
  val ns        : Molecule_9[A, B, C, D, E, F, G, H, String ] with OneString [Molecule_9[A, B, C, D, E, F, G, H, String ], Nothing] = ???
  val txInstant : Molecule_9[A, B, C, D, E, F, G, H, Date   ] with OneDate   [Molecule_9[A, B, C, D, E, F, G, H, Date   ], Nothing] = ???
  val txT       : Molecule_9[A, B, C, D, E, F, G, H, Long   ] with OneLong   [Molecule_9[A, B, C, D, E, F, G, H, Long   ], Nothing] = ???
  val txAdded   : Molecule_9[A, B, C, D, E, F, G, H, Boolean] with OneBoolean[Molecule_9[A, B, C, D, E, F, G, H, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_8 [A, B, C, D, E, F, G, H]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_9 [A, B, C, D, E, F, G, H, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_10[A, B, C, D, E, F, G, H, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_11[A, B, C, D, E, F, G, H, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_12[A, B, C, D, E, F, G, H, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_13[A, B, C, D, E, F, G, H, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_14[A, B, C, D, E, F, G, H, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_15[A, B, C, D, E, F, G, H, a, b, c, d, e, f, g] = ???
}

trait Molecule_9[A, B, C, D, E, F, G, H, I] extends NS {
//  val e         : Molecule_10[A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [Molecule_10[A, B, C, D, E, F, G, H, I, Long   ], Nothing] = ???
//  val a         : Molecule_10[A, B, C, D, E, F, G, H, I, String ] with OneString [Molecule_10[A, B, C, D, E, F, G, H, I, String ], Nothing] = ???
  val v         : Molecule_10[A, B, C, D, E, F, G, H, I, Any    ] with OneAny    [Molecule_10[A, B, C, D, E, F, G, H, I, Any    ], Nothing] = ???
  val ns        : Molecule_10[A, B, C, D, E, F, G, H, I, String ] with OneString [Molecule_10[A, B, C, D, E, F, G, H, I, String ], Nothing] = ???
  val txInstant : Molecule_10[A, B, C, D, E, F, G, H, I, Date   ] with OneDate   [Molecule_10[A, B, C, D, E, F, G, H, I, Date   ], Nothing] = ???
  val txT       : Molecule_10[A, B, C, D, E, F, G, H, I, Long   ] with OneLong   [Molecule_10[A, B, C, D, E, F, G, H, I, Long   ], Nothing] = ???
  val txAdded   : Molecule_10[A, B, C, D, E, F, G, H, I, Boolean] with OneBoolean[Molecule_10[A, B, C, D, E, F, G, H, I, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_9 [A, B, C, D, E, F, G, H, I]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_10[A, B, C, D, E, F, G, H, I, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_11[A, B, C, D, E, F, G, H, I, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_12[A, B, C, D, E, F, G, H, I, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_13[A, B, C, D, E, F, G, H, I, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_14[A, B, C, D, E, F, G, H, I, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_15[A, B, C, D, E, F, G, H, I, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_16[A, B, C, D, E, F, G, H, I, a, b, c, d, e, f, g] = ???
}

trait Molecule_10[A, B, C, D, E, F, G, H, I, J] extends NS {
//  val e         : Molecule_11[A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [Molecule_11[A, B, C, D, E, F, G, H, I, J, Long   ], Nothing] = ???
//  val a         : Molecule_11[A, B, C, D, E, F, G, H, I, J, String ] with OneString [Molecule_11[A, B, C, D, E, F, G, H, I, J, String ], Nothing] = ???
  val v         : Molecule_11[A, B, C, D, E, F, G, H, I, J, Any    ] with OneAny    [Molecule_11[A, B, C, D, E, F, G, H, I, J, Any    ], Nothing] = ???
  val ns        : Molecule_11[A, B, C, D, E, F, G, H, I, J, String ] with OneString [Molecule_11[A, B, C, D, E, F, G, H, I, J, String ], Nothing] = ???
  val txInstant : Molecule_11[A, B, C, D, E, F, G, H, I, J, Date   ] with OneDate   [Molecule_11[A, B, C, D, E, F, G, H, I, J, Date   ], Nothing] = ???
  val txT       : Molecule_11[A, B, C, D, E, F, G, H, I, J, Long   ] with OneLong   [Molecule_11[A, B, C, D, E, F, G, H, I, J, Long   ], Nothing] = ???
  val txAdded   : Molecule_11[A, B, C, D, E, F, G, H, I, J, Boolean] with OneBoolean[Molecule_11[A, B, C, D, E, F, G, H, I, J, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_10[A, B, C, D, E, F, G, H, I, J]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_11[A, B, C, D, E, F, G, H, I, J, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_12[A, B, C, D, E, F, G, H, I, J, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_13[A, B, C, D, E, F, G, H, I, J, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_14[A, B, C, D, E, F, G, H, I, J, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_15[A, B, C, D, E, F, G, H, I, J, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_16[A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_17[A, B, C, D, E, F, G, H, I, J, a, b, c, d, e, f, g] = ???
}

trait Molecule_11[A, B, C, D, E, F, G, H, I, J, K] extends NS {
//  val e         : Molecule_12[A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [Molecule_12[A, B, C, D, E, F, G, H, I, J, K, Long   ], Nothing] = ???
//  val a         : Molecule_12[A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [Molecule_12[A, B, C, D, E, F, G, H, I, J, K, String ], Nothing] = ???
  val v         : Molecule_12[A, B, C, D, E, F, G, H, I, J, K, Any    ] with OneAny    [Molecule_12[A, B, C, D, E, F, G, H, I, J, K, Any    ], Nothing] = ???
  val ns        : Molecule_12[A, B, C, D, E, F, G, H, I, J, K, String ] with OneString [Molecule_12[A, B, C, D, E, F, G, H, I, J, K, String ], Nothing] = ???
  val txInstant : Molecule_12[A, B, C, D, E, F, G, H, I, J, K, Date   ] with OneDate   [Molecule_12[A, B, C, D, E, F, G, H, I, J, K, Date   ], Nothing] = ???
  val txT       : Molecule_12[A, B, C, D, E, F, G, H, I, J, K, Long   ] with OneLong   [Molecule_12[A, B, C, D, E, F, G, H, I, J, K, Long   ], Nothing] = ???
  val txAdded   : Molecule_12[A, B, C, D, E, F, G, H, I, J, K, Boolean] with OneBoolean[Molecule_12[A, B, C, D, E, F, G, H, I, J, K, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_11[A, B, C, D, E, F, G, H, I, J, K]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_12[A, B, C, D, E, F, G, H, I, J, K, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_13[A, B, C, D, E, F, G, H, I, J, K, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_18[A, B, C, D, E, F, G, H, I, J, K, a, b, c, d, e, f, g] = ???
}

trait Molecule_12[A, B, C, D, E, F, G, H, I, J, K, L] extends NS {
//  val e         : Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ], Nothing] = ???
//  val a         : Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, String ], Nothing] = ???
  val v         : Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, Any    ] with OneAny    [Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, Any    ], Nothing] = ???
  val ns        : Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, String ] with OneString [Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, String ], Nothing] = ???
  val txInstant : Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, Date   ] with OneDate   [Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, Date   ], Nothing] = ???
  val txT       : Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ] with OneLong   [Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, Long   ], Nothing] = ???
  val txAdded   : Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, Boolean] with OneBoolean[Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_12[A, B, C, D, E, F, G, H, I, J, K, L]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, a, b, c, d, e, f, g] = ???
}

trait Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS {
//  val e         : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], Nothing] = ???
//  val a         : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ], Nothing] = ???
  val v         : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ] with OneAny    [Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Any    ], Nothing] = ???
  val ns        : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ] with OneString [Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String ], Nothing] = ???
  val txInstant : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ] with OneDate   [Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Date   ], Nothing] = ???
  val txT       : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ] with OneLong   [Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long   ], Nothing] = ???
  val txAdded   : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean] with OneBoolean[Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, M]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, a, b, c, d, e, f, g] = ???
}

trait Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS {
//  val e         : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], Nothing] = ???
//  val a         : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], Nothing] = ???
  val v         : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ] with OneAny    [Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Any    ], Nothing] = ???
  val ns        : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ] with OneString [Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String ], Nothing] = ???
  val txInstant : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ] with OneDate   [Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Date   ], Nothing] = ???
  val txT       : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ] with OneLong   [Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long   ], Nothing] = ???
  val txAdded   : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean] with OneBoolean[Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, a, b, c, d, e, f, g] = ???
}

trait Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS {
//  val e         : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], Nothing] = ???
//  val a         : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], Nothing] = ???
  val v         : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ] with OneAny    [Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Any    ], Nothing] = ???
  val ns        : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ] with OneString [Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String ], Nothing] = ???
  val txInstant : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ] with OneDate   [Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Date   ], Nothing] = ???
  val txT       : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ] with OneLong   [Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long   ], Nothing] = ???
  val txAdded   : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean] with OneBoolean[Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Boolean], Nothing] = ???

  def tx                     (m0: Molecule_0)                     : Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]                      = ???
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a]                   = ???
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b]                = ???
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c]             = ???
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d]          = ???
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e]       = ???
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f]    = ???
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, a, b, c, d, e, f, g] = ???
}

trait Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS {
//  val e         : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], Nothing] = ???
//  val a         : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], Nothing] = ???
  val v         : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ] with OneAny    [Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Any    ], Nothing] = ???
  val ns        : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ] with OneString [Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String ], Nothing] = ???
  val txInstant : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ] with OneDate   [Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Date   ], Nothing] = ???
  val txT       : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ] with OneLong   [Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long   ], Nothing] = ???
  val txAdded   : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean] with OneBoolean[Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Boolean], Nothing] = ???

  def tx                  (m0: Molecule_0)                  : Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]                   = ???
  def tx[a]               (m1: Molecule_1[a])               : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a]                = ???
  def tx[a, b]            (m2: Molecule_2[a, b])            : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b]             = ???
  def tx[a, b, c]         (m3: Molecule_3[a, b, c])         : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c]          = ???
  def tx[a, b, c, d]      (m4: Molecule_4[a, b, c, d])      : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d]       = ???
  def tx[a, b, c, d, e]   (m5: Molecule_5[a, b, c, d, e])   : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e]    = ???
  def tx[a, b, c, d, e, f](m6: Molecule_6[a, b, c, d, e, f]): Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, a, b, c, d, e, f] = ???
}

trait Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS {
//  val e         : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], Nothing] = ???
//  val a         : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], Nothing] = ???
  val v         : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ] with OneAny    [Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Any    ], Nothing] = ???
  val ns        : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ] with OneString [Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String ], Nothing] = ???
  val txInstant : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ] with OneDate   [Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Date   ], Nothing] = ???
  val txT       : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ] with OneLong   [Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long   ], Nothing] = ???
  val txAdded   : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean] with OneBoolean[Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Boolean], Nothing] = ???

  def tx               (m0: Molecule_0)               : Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]                = ???
  def tx[a]            (m1: Molecule_1[a])            : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a]             = ???
  def tx[a, b]         (m2: Molecule_2[a, b])         : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b]          = ???
  def tx[a, b, c]      (m3: Molecule_3[a, b, c])      : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c]       = ???
  def tx[a, b, c, d]   (m4: Molecule_4[a, b, c, d])   : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d]    = ???
  def tx[a, b, c, d, e](m5: Molecule_5[a, b, c, d, e]): Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, a, b, c, d, e] = ???
}

trait Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS {
//  val e         : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], Nothing] = ???
//  val a         : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], Nothing] = ???
  val v         : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ] with OneAny    [Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Any    ], Nothing] = ???
  val ns        : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ] with OneString [Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String ], Nothing] = ???
  val txInstant : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ] with OneDate   [Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Date   ], Nothing] = ???
  val txT       : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ] with OneLong   [Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long   ], Nothing] = ???
  val txAdded   : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean] with OneBoolean[Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Boolean], Nothing] = ???

  def tx            (m0: Molecule_0)            : Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]             = ???
  def tx[a]         (m1: Molecule_1[a])         : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a]          = ???
  def tx[a, b]      (m2: Molecule_2[a, b])      : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b]       = ???
  def tx[a, b, c]   (m3: Molecule_3[a, b, c])   : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c]    = ???
  def tx[a, b, c, d](m4: Molecule_4[a, b, c, d]): Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, a, b, c, d] = ???
}

trait Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS {
//  val e         : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], Nothing] = ???
//  val a         : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], Nothing] = ???
  val v         : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ] with OneAny    [Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Any    ], Nothing] = ???
  val ns        : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ] with OneString [Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String ], Nothing] = ???
  val txInstant : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ] with OneDate   [Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Date   ], Nothing] = ???
  val txT       : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ] with OneLong   [Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long   ], Nothing] = ???
  val txAdded   : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean] with OneBoolean[Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Boolean], Nothing] = ???

  def tx         (m0: Molecule_0)         : Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]          = ???
  def tx[a]      (m1: Molecule_1[a])      : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a]       = ???
  def tx[a, b]   (m2: Molecule_2[a, b])   : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b]    = ???
  def tx[a, b, c](m3: Molecule_3[a, b, c]): Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, a, b, c] = ???
}

trait Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS {
//  val e         : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], Nothing] = ???
//  val a         : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], Nothing] = ???
  val v         : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ] with OneAny    [Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Any    ], Nothing] = ???
  val ns        : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ] with OneString [Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String ], Nothing] = ???
  val txInstant : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ] with OneDate   [Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Date   ], Nothing] = ???
  val txT       : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ] with OneLong   [Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long   ], Nothing] = ???
  val txAdded   : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean] with OneBoolean[Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Boolean], Nothing] = ???

  def tx      (m0: Molecule_0)      : Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]       = ???
  def tx[a]   (m1: Molecule_1[a])   : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a]    = ???
  def tx[a, b](m2: Molecule_2[a, b]): Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, a, b] = ???
}

trait Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS {
//  val e         : Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
//  val a         : Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val v         : Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ] with OneAny    [Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Any    ], Nothing] = ???
  val ns        : Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ] with OneString [Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String ], Nothing] = ???
  val txInstant : Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ] with OneDate   [Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Date   ], Nothing] = ???
  val txT       : Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ] with OneLong   [Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long   ], Nothing] = ???
  val txAdded   : Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean] with OneBoolean[Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Boolean], Nothing] = ???

  def tx   (m0: Molecule_0)   : Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]    = ???
  def tx[a](m1: Molecule_1[a]): Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, a] = ???
}

trait Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS
