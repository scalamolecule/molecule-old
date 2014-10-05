package molecule.out
import molecule.dsl.schemaDSL.NS


trait Molecule_0 extends NS {
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
  def tx                     (m0: Molecule_0)                     : Molecule_1[A]
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_2[A, a]
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_3[A, a, b]
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_4[A, a, b, c]
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_5[A, a, b, c, d]
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_6[A, a, b, c, d, e]
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_7[A, a, b, c, d, e, f]
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_8[A, a, b, c, d, e, f, g]
}

trait Molecule_2[A, B] extends NS {
  def tx                     (m0: Molecule_0)                     : Molecule_2[A, B]
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_3[A, B, a]
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_4[A, B, a, b]
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_5[A, B, a, b, c]
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_6[A, B, a, b, c, d]
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_7[A, B, a, b, c, d, e]
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_8[A, B, a, b, c, d, e, f]
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_9[A, B, a, b, c, d, e, f, g]
}

trait Molecule_3[A, B, C] extends NS {
  def tx                     (m0: Molecule_0)                     : Molecule_3[A, B, C]
  def tx[a]                  (m1: Molecule_1[a])                  : Molecule_4[A, B, C, a]
  def tx[a, b]               (m2: Molecule_2[a, b])               : Molecule_5[A, B, C, a, b]
  def tx[a, b, c]            (m3: Molecule_3[a, b, c])            : Molecule_6[A, B, C, a, b, c]
  def tx[a, b, c, d]         (m4: Molecule_4[a, b, c, d])         : Molecule_7[A, B, C, a, b, c, d]
  def tx[a, b, c, d, e]      (m5: Molecule_5[a, b, c, d, e])      : Molecule_8[A, B, C, a, b, c, d, e]
  def tx[a, b, c, d, e, f]   (m6: Molecule_6[a, b, c, d, e, f])   : Molecule_9[A, B, C, a, b, c, d, e, f]
  def tx[a, b, c, d, e, f, g](m7: Molecule_7[a, b, c, d, e, f, g]): Molecule_10[A, B, C, a, b, c, d, e, f, g]
}

trait Molecule_4[A, B, C, D] extends NS
trait Molecule_5[A, B, C, D, E] extends NS
trait Molecule_6[A, B, C, D, E, F] extends NS
trait Molecule_7[A, B, C, D, E, F, G] extends NS
trait Molecule_8[A, B, C, D, E, F, G, H] extends NS
trait Molecule_9[A, B, C, D, E, F, G, H, I] extends NS
trait Molecule_10[A, B, C, D, E, F, G, H, I, J] extends NS
trait Molecule_11[A, B, C, D, E, F, G, H, I, J, K] extends NS
trait Molecule_12[A, B, C, D, E, F, G, H, I, J, K, L] extends NS
trait Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS
trait Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS
trait Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS
trait Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS
trait Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS
trait Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS
trait Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS
trait Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS
trait Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS
trait Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS