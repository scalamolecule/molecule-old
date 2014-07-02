import molecule.ast.model._
import scala.language.experimental.macros
import scala.language.implicitConversions

package object molecule {

  // Output molecules

  implicit def m(dsl: Out_0): Molecule with Out0 = macro BuildMolecule.from0attr
  implicit def m[A](dsl: Out_1[A]): Molecule with Out1[A] = macro BuildMolecule.from1attr[A]
  implicit def m[A, B](dsl: Out_2[A, B]): Molecule with Out2[A, B] = macro BuildMolecule.from2attr[A, B]
  implicit def m[A, B, C](dsl: Out_3[A, B, C]): Molecule with Out3[A, B, C] = macro BuildMolecule.from3attr[A, B, C]
  implicit def m[A, B, C, D](dsl: Out_4[A, B, C, D]): Molecule with Out4[A, B, C, D] = macro BuildMolecule.from4attr[A, B, C, D]
  implicit def m[A, B, C, D, E](dsl: Out_5[A, B, C, D, E]): Molecule with Out5[A, B, C, D, E] = macro BuildMolecule.from5attr[A, B, C, D, E]
  implicit def m[A, B, C, D, E, F](dsl: Out_6[A, B, C, D, E, F]): Molecule with Out6[A, B, C, D, E, F] = macro BuildMolecule.from6attr[A, B, C, D, E, F]
  implicit def m[A, B, C, D, E, F, G](dsl: Out_7[A, B, C, D, E, F, G]): Molecule with Out7[A, B, C, D, E, F, G] = macro BuildMolecule.from7attr[A, B, C, D, E, F, G]
  implicit def m[A, B, C, D, E, F, G, H](dsl: Out_8[A, B, C, D, E, F, G, H]): Molecule with Out8[A, B, C, D, E, F, G, H] = macro BuildMolecule.from8attr[A, B, C, D, E, F, G, H]
  //  implicit def m[A, B, C, D, E, F, G, H, I](dsl: Out_9[A, B, C, D, E, F, G, H, I]): Molecule with Out8[A, B, C, D, E, F, G, H, I] = macro BuildMolecule.from9attr[A, B, C, D, E, F, G, H, I]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J](dsl: Out_10[A, B, C, D, E, F, G, H, I, J]): Molecule with Out10[A, B, C, D, E, F, G, H, I, J] = macro BuildMolecule.from10attr[A, B, C, D, E, F, G, H,I, J]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K](dsl: Out_11[A, B, C, D, E, F, G, H, I, J, K]): Molecule with Out11[A, B, C, D, E, F, G, H, I, J, K] = macro BuildMolecule.from11attr[A, B, C, D, E, F, G, H,I, J, K]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L](dsl: Out_12[A, B, C, D, E, F, G, H, I, J, K, L]): Molecule with Out12[A, B, C, D, E, F, G, H, I, J, K, L] = macro BuildMolecule.from12attr[A, B, C, D, E, F, G, H,I, J, K, L]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule with Out13[A, B, C, D, E, F, G, H, I, J, K, L, M] = macro BuildMolecule.from13attr[A, B, C, D, E, F, G, H,I, J, K, L, M]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule with Out14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro BuildMolecule.from14attr[A, B, C, D, E, F, G, H,I, J, K, L, M, N]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): Molecule with Out15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro BuildMolecule.from15attr[A, B, C, D, E, F, G, H,I, J, K, L, M, N, O]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](dsl: Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): Molecule with Out16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro BuildMolecule.from16attr[A, B, C, D, E, F, G, H,I, J, K, L, M, N, O, P]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](dsl: Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): Molecule with Out17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro BuildMolecule.from17attr[A, B, C, D, E, F, G, H,I, J, K, L, M, N, O, P, Q]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](dsl: Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): Molecule with Out18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro BuildMolecule.from18attr[A, B, C, D, E, F, G, H,I, J, K, L, M, N, O, P, Q, R]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](dsl: Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]): Molecule with Out19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = macro BuildMolecule.from19attr[A, B, C, D, E, F, G, H,I, J, K, L, M, N, O, P, Q, R, S]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](dsl: Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]): Molecule with Out20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = macro BuildMolecule.from20attr[A, B, C, D, E, F, G, H,I, J, K, L, M, N, O, P, Q, R, S, T]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](dsl: Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]): Molecule with Out21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = macro BuildMolecule.from21attr[A, B, C, D, E, F, G, H,I, J, K, L, M, N, O, P, Q, R, S, T, U]
  //  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](dsl: Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]): Molecule with Out22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = macro BuildMolecule.from22attr[A, B, C, D, E, F, G, H,I, J, K, L, M, N, O, P, Q, R, S, T, U, V]


  // Input molecules awaiting 1 input

  implicit def m[I1](inputDsl: In_1_0[I1]): InputMolecule_1_0[I1] = macro BuildInputMolecule.await_1_0[I1]
  implicit def m[I1, A](inputDsl: In_1_1[I1, A]): InputMolecule_1_1[I1, A] = macro BuildInputMolecule.await_1_1[I1, A]
  implicit def m[I1, A, B](inputDsl: In_1_2[I1, A, B]): InputMolecule_1_2[I1, A, B] = macro BuildInputMolecule.await_1_2[I1, A, B]
  implicit def m[I1, A, B, C](inputDsl: In_1_3[I1, A, B, C]): InputMolecule_1_3[I1, A, B, C] = macro BuildInputMolecule.await_1_3[I1, A, B, C]


  // Input molecules awaiting 2 inputs

  implicit def m[I1, I2](inputDsl: In_2_0[I1, I2]): InputMolecule_2_0[I1, I2] = macro BuildInputMolecule.await_2_0[I1, I2]
  implicit def m[I1, I2, A](inputDsl: In_2_1[I1, I2, A]): InputMolecule_2_1[I1, I2, A] = macro BuildInputMolecule.await_2_1[I1, I2, A]
  implicit def m[I1, I2, A, B](inputDsl: In_2_2[I1, I2, A, B]): InputMolecule_2_2[I1, I2, A, B] = macro BuildInputMolecule.await_2_2[I1, I2, A, B]
  implicit def m[I1, I2, A, B, C](inputDsl: In_2_3[I1, I2, A, B, C]): InputMolecule_2_3[I1, I2, A, B, C] = macro BuildInputMolecule.await_2_3[I1, I2, A, B, C]


  // Input molecules awaiting 3 inputs

  implicit def m[I1, I2, I3](inputDsl: In_3_0[I1, I2, I3]): InputMolecule_3_0[I1, I2, I3] = macro BuildInputMolecule.await_3_0[I1, I2, I3]
  implicit def m[I1, I2, I3, A](inputDsl: In_3_1[I1, I2, I3, A]): InputMolecule_3_1[I1, I2, I3, A] = macro BuildInputMolecule.await_3_1[I1, I2, I3, A]
  implicit def m[I1, I2, I3, A, B](inputDsl: In_3_2[I1, I2, I3, A, B]): InputMolecule_3_2[I1, I2, I3, A, B] = macro BuildInputMolecule.await_3_2[I1, I2, I3, A, B]
  implicit def m[I1, I2, I3, A, B, C](inputDsl: In_3_3[I1, I2, I3, A, B, C]): InputMolecule_3_3[I1, I2, I3, A, B, C] = macro BuildInputMolecule.await_3_3[I1, I2, I3, A, B, C]


  // Input in expressions
  implicit def string2Model(s: String) = TermValue(s)
  implicit def int2Model(i: Int) = TermValue(i)
  implicit def long2Model(l: Long) = TermValue(l)
  implicit def tuple2Model[A, B](tpl: (A, B)) = TermValue(tpl)

  // Markers
  object ?
  object ?!
  object maybe

  case class oldNew[T](from: T, to: T)

  // Todo - more update options...
  //  implicit class updater[T](from: T) {
  //    def ?->(to: T) = oldNew(from, to)
  //  }
  //
  //  import datomic.{Connection => Cnx}
  //
  //  implicit class selectionFromMolecule(selection: Out_0) {
  //    def update(selection: Out_0, data: Out_0*)(implicit conn: Cnx): Boolean = ???
  //  }
  //  implicit class selectionFromIds(ids: Seq[Long]) {
  //    def update(selection: Out_0, data: Out_0*)(implicit conn: Cnx): Boolean = ???
  //  }
}
