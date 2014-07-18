import molecule.ast.model._
import molecule.in._
import molecule.out._
import scala.language.experimental.macros
import scala.language.implicitConversions
import datomic.Connection


package object molecule {

  // Output molecules

  implicit def m(dsl: Out_0): OutputMolecule0 = macro BuildOutputMolecule.from0attr
  implicit def m[A](dsl: Out_1[A]): OutputMolecule1[A] = macro BuildOutputMolecule.from1attr[A]
  implicit def m[A, B](dsl: Out_2[A, B]): OutputMolecule2[A, B] = macro BuildOutputMolecule.from2attr[A, B]
  implicit def m[A, B, C](dsl: Out_3[A, B, C]): OutputMolecule3[A, B, C] = macro BuildOutputMolecule.from3attr[A, B, C]
  implicit def m[A, B, C, D](dsl: Out_4[A, B, C, D]): OutputMolecule4[A, B, C, D] = macro BuildOutputMolecule.from4attr[A, B, C, D]
  implicit def m[A, B, C, D, E](dsl: Out_5[A, B, C, D, E]): OutputMolecule5[A, B, C, D, E] = macro BuildOutputMolecule.from5attr[A, B, C, D, E]
  implicit def m[A, B, C, D, E, F](dsl: Out_6[A, B, C, D, E, F]): OutputMolecule6[A, B, C, D, E, F] = macro BuildOutputMolecule.from6attr[A, B, C, D, E, F]
  implicit def m[A, B, C, D, E, F, G](dsl: Out_7[A, B, C, D, E, F, G]): OutputMolecule7[A, B, C, D, E, F, G] = macro BuildOutputMolecule.from7attr[A, B, C, D, E, F, G]
  implicit def m[A, B, C, D, E, F, G, H](dsl: Out_8[A, B, C, D, E, F, G, H]): OutputMolecule8[A, B, C, D, E, F, G, H] = macro BuildOutputMolecule.from8attr[A, B, C, D, E, F, G, H]
  implicit def m[A, B, C, D, E, F, G, H, I](dsl: Out_9[A, B, C, D, E, F, G, H, I]): OutputMolecule9[A, B, C, D, E, F, G, H, I] = macro BuildOutputMolecule.from9attr[A, B, C, D, E, F, G, H, I]
  implicit def m[A, B, C, D, E, F, G, H, I, J](dsl: Out_10[A, B, C, D, E, F, G, H, I, J]): OutputMolecule10[A, B, C, D, E, F, G, H, I, J] = macro BuildOutputMolecule.from10attr[A, B, C, D, E, F, G, H, I, J]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K](dsl: Out_11[A, B, C, D, E, F, G, H, I, J, K]): OutputMolecule11[A, B, C, D, E, F, G, H, I, J, K] = macro BuildOutputMolecule.from11attr[A, B, C, D, E, F, G, H, I, J, K]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L](dsl: Out_12[A, B, C, D, E, F, G, H, I, J, K, L]): OutputMolecule12[A, B, C, D, E, F, G, H, I, J, K, L] = macro BuildOutputMolecule.from12attr[A, B, C, D, E, F, G, H, I, J, K, L]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M]): OutputMolecule13[A, B, C, D, E, F, G, H, I, J, K, L, M] = macro BuildOutputMolecule.from13attr[A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]): OutputMolecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro BuildOutputMolecule.from14attr[A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): OutputMolecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro BuildOutputMolecule.from15attr[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](dsl: Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): OutputMolecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro BuildOutputMolecule.from16attr[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](dsl: Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): OutputMolecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro BuildOutputMolecule.from17attr[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](dsl: Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): OutputMolecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro BuildOutputMolecule.from18attr[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](dsl: Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]): OutputMolecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = macro BuildOutputMolecule.from19attr[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](dsl: Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]): OutputMolecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = macro BuildOutputMolecule.from20attr[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](dsl: Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]): OutputMolecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = macro BuildOutputMolecule.from21attr[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  implicit def m[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](dsl: Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]): OutputMolecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = macro BuildOutputMolecule.from22attr[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]


  // Input molecules awaiting 1 input

  implicit def m[I1](inputDsl: In_1_0[I1]): InputMolecule_1_0[I1] = macro BuildInputMolecule.await_1_0[I1]
  implicit def m[I1, A](inputDsl: In_1_1[I1, A]): InputMolecule_1_1[I1, A] = macro BuildInputMolecule.await_1_1[I1, A]
  implicit def m[I1, A, B](inputDsl: In_1_2[I1, A, B]): InputMolecule_1_2[I1, A, B] = macro BuildInputMolecule.await_1_2[I1, A, B]
  implicit def m[I1, A, B, C](inputDsl: In_1_3[I1, A, B, C]): InputMolecule_1_3[I1, A, B, C] = macro BuildInputMolecule.await_1_3[I1, A, B, C]
  implicit def m[I1, A, B, C, D](inputDsl: In_1_4[I1, A, B, C, D]): InputMolecule_1_4[I1, A, B, C, D] = macro BuildInputMolecule.await_1_4[I1, A, B, C, D]
  implicit def m[I1, A, B, C, D, E](inputDsl: In_1_5[I1, A, B, C, D, E]): InputMolecule_1_5[I1, A, B, C, D, E] = macro BuildInputMolecule.await_1_5[I1, A, B, C, D, E]
  implicit def m[I1, A, B, C, D, E, F](inputDsl: In_1_6[I1, A, B, C, D, E, F]): InputMolecule_1_6[I1, A, B, C, D, E, F] = macro BuildInputMolecule.await_1_6[I1, A, B, C, D, E, F]
  implicit def m[I1, A, B, C, D, E, F, G](inputDsl: In_1_7[I1, A, B, C, D, E, F, G]): InputMolecule_1_7[I1, A, B, C, D, E, F, G] = macro BuildInputMolecule.await_1_7[I1, A, B, C, D, E, F, G]
  implicit def m[I1, A, B, C, D, E, F, G, H](inputDsl: In_1_8[I1, A, B, C, D, E, F, G, H]): InputMolecule_1_8[I1, A, B, C, D, E, F, G, H] = macro BuildInputMolecule.await_1_8[I1, A, B, C, D, E, F, G, H]
  implicit def m[I1, A, B, C, D, E, F, G, H, I](inputDsl: In_1_9[I1, A, B, C, D, E, F, G, H, I]): InputMolecule_1_9[I1, A, B, C, D, E, F, G, H, I] = macro BuildInputMolecule.await_1_9[I1, A, B, C, D, E, F, G, H, I]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J](inputDsl: In_1_10[I1, A, B, C, D, E, F, G, H, I, J]): InputMolecule_1_10[I1, A, B, C, D, E, F, G, H, I, J] = macro BuildInputMolecule.await_1_10[I1, A, B, C, D, E, F, G, H, I, J]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K](inputDsl: In_1_11[I1, A, B, C, D, E, F, G, H, I, J, K]): InputMolecule_1_11[I1, A, B, C, D, E, F, G, H, I, J, K] = macro BuildInputMolecule.await_1_11[I1, A, B, C, D, E, F, G, H, I, J, K]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L](inputDsl: In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L]): InputMolecule_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L] = macro BuildInputMolecule.await_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L, M](inputDsl: In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M]): InputMolecule_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro BuildInputMolecule.await_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N](inputDsl: In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): InputMolecule_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro BuildInputMolecule.await_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](inputDsl: In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): InputMolecule_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro BuildInputMolecule.await_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](inputDsl: In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): InputMolecule_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro BuildInputMolecule.await_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](inputDsl: In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): InputMolecule_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro BuildInputMolecule.await_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](inputDsl: In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): InputMolecule_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro BuildInputMolecule.await_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](inputDsl: In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]): InputMolecule_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = macro BuildInputMolecule.await_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](inputDsl: In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]): InputMolecule_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = macro BuildInputMolecule.await_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](inputDsl: In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]): InputMolecule_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = macro BuildInputMolecule.await_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  implicit def m[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](inputDsl: In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]): InputMolecule_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = macro BuildInputMolecule.await_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]


  // Input molecules awaiting 2 inputs

  implicit def m[I1, I2](inputDsl: In_2_0[I1, I2]): InputMolecule_2_0[I1, I2] = macro BuildInputMolecule.await_2_0[I1, I2]
  implicit def m[I1, I2, A](inputDsl: In_2_1[I1, I2, A]): InputMolecule_2_1[I1, I2, A] = macro BuildInputMolecule.await_2_1[I1, I2, A]
  implicit def m[I1, I2, A, B](inputDsl: In_2_2[I1, I2, A, B]): InputMolecule_2_2[I1, I2, A, B] = macro BuildInputMolecule.await_2_2[I1, I2, A, B]
  implicit def m[I1, I2, A, B, C](inputDsl: In_2_3[I1, I2, A, B, C]): InputMolecule_2_3[I1, I2, A, B, C] = macro BuildInputMolecule.await_2_3[I1, I2, A, B, C]
  implicit def m[I1, I2, A, B, C, D](inputDsl: In_2_4[I1, I2, A, B, C, D]): InputMolecule_2_4[I1, I2, A, B, C, D] = macro BuildInputMolecule.await_2_4[I1, I2, A, B, C, D]
  implicit def m[I1, I2, A, B, C, D, E](inputDsl: In_2_5[I1, I2, A, B, C, D, E]): InputMolecule_2_5[I1, I2, A, B, C, D, E] = macro BuildInputMolecule.await_2_5[I1, I2, A, B, C, D, E]
  implicit def m[I1, I2, A, B, C, D, E, F](inputDsl: In_2_6[I1, I2, A, B, C, D, E, F]): InputMolecule_2_6[I1, I2, A, B, C, D, E, F] = macro BuildInputMolecule.await_2_6[I1, I2, A, B, C, D, E, F]
  implicit def m[I1, I2, A, B, C, D, E, F, G](inputDsl: In_2_7[I1, I2, A, B, C, D, E, F, G]): InputMolecule_2_7[I1, I2, A, B, C, D, E, F, G] = macro BuildInputMolecule.await_2_7[I1, I2, A, B, C, D, E, F, G]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H](inputDsl: In_2_8[I1, I2, A, B, C, D, E, F, G, H]): InputMolecule_2_8[I1, I2, A, B, C, D, E, F, G, H] = macro BuildInputMolecule.await_2_8[I1, I2, A, B, C, D, E, F, G, H]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I](inputDsl: In_2_9[I1, I2, A, B, C, D, E, F, G, H, I]): InputMolecule_2_9[I1, I2, A, B, C, D, E, F, G, H, I] = macro BuildInputMolecule.await_2_9[I1, I2, A, B, C, D, E, F, G, H, I]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J](inputDsl: In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J]): InputMolecule_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] = macro BuildInputMolecule.await_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K](inputDsl: In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K]): InputMolecule_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] = macro BuildInputMolecule.await_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L](inputDsl: In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]): InputMolecule_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] = macro BuildInputMolecule.await_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M](inputDsl: In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]): InputMolecule_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro BuildInputMolecule.await_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N](inputDsl: In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): InputMolecule_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro BuildInputMolecule.await_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](inputDsl: In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): InputMolecule_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro BuildInputMolecule.await_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](inputDsl: In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): InputMolecule_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro BuildInputMolecule.await_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](inputDsl: In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): InputMolecule_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro BuildInputMolecule.await_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](inputDsl: In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): InputMolecule_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro BuildInputMolecule.await_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](inputDsl: In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]): InputMolecule_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = macro BuildInputMolecule.await_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](inputDsl: In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]): InputMolecule_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = macro BuildInputMolecule.await_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](inputDsl: In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]): InputMolecule_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = macro BuildInputMolecule.await_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  implicit def m[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](inputDsl: In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]): InputMolecule_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = macro BuildInputMolecule.await_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]


  // Input molecules awaiting 3 inputs

  implicit def m[I1, I2, I3](inputDsl: In_3_0[I1, I2, I3]): InputMolecule_3_0[I1, I2, I3] = macro BuildInputMolecule.await_3_0[I1, I2, I3]
  implicit def m[I1, I2, I3, A](inputDsl: In_3_1[I1, I2, I3, A]): InputMolecule_3_1[I1, I2, I3, A] = macro BuildInputMolecule.await_3_1[I1, I2, I3, A]
  implicit def m[I1, I2, I3, A, B](inputDsl: In_3_2[I1, I2, I3, A, B]): InputMolecule_3_2[I1, I2, I3, A, B] = macro BuildInputMolecule.await_3_2[I1, I2, I3, A, B]
  implicit def m[I1, I2, I3, A, B, C](inputDsl: In_3_3[I1, I2, I3, A, B, C]): InputMolecule_3_3[I1, I2, I3, A, B, C] = macro BuildInputMolecule.await_3_3[I1, I2, I3, A, B, C]
  implicit def m[I1, I2, I3, A, B, C, D](inputDsl: In_3_4[I1, I2, I3, A, B, C, D]): InputMolecule_3_4[I1, I2, I3, A, B, C, D] = macro BuildInputMolecule.await_3_4[I1, I2, I3, A, B, C, D]
  implicit def m[I1, I2, I3, A, B, C, D, E](inputDsl: In_3_5[I1, I2, I3, A, B, C, D, E]): InputMolecule_3_5[I1, I2, I3, A, B, C, D, E] = macro BuildInputMolecule.await_3_5[I1, I2, I3, A, B, C, D, E]
  implicit def m[I1, I2, I3, A, B, C, D, E, F](inputDsl: In_3_6[I1, I2, I3, A, B, C, D, E, F]): InputMolecule_3_6[I1, I2, I3, A, B, C, D, E, F] = macro BuildInputMolecule.await_3_6[I1, I2, I3, A, B, C, D, E, F]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G](inputDsl: In_3_7[I1, I2, I3, A, B, C, D, E, F, G]): InputMolecule_3_7[I1, I2, I3, A, B, C, D, E, F, G] = macro BuildInputMolecule.await_3_7[I1, I2, I3, A, B, C, D, E, F, G]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H](inputDsl: In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]): InputMolecule_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] = macro BuildInputMolecule.await_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I](inputDsl: In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I]): InputMolecule_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I] = macro BuildInputMolecule.await_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J](inputDsl: In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J]): InputMolecule_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J] = macro BuildInputMolecule.await_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K](inputDsl: In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K]): InputMolecule_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] = macro BuildInputMolecule.await_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L](inputDsl: In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L]): InputMolecule_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] = macro BuildInputMolecule.await_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M](inputDsl: In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M]): InputMolecule_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro BuildInputMolecule.await_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N](inputDsl: In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): InputMolecule_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro BuildInputMolecule.await_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](inputDsl: In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): InputMolecule_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro BuildInputMolecule.await_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](inputDsl: In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): InputMolecule_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro BuildInputMolecule.await_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](inputDsl: In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): InputMolecule_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro BuildInputMolecule.await_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](inputDsl: In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): InputMolecule_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro BuildInputMolecule.await_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](inputDsl: In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]): InputMolecule_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = macro BuildInputMolecule.await_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](inputDsl: In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]): InputMolecule_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = macro BuildInputMolecule.await_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](inputDsl: In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]): InputMolecule_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = macro BuildInputMolecule.await_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  implicit def m[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](inputDsl: In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]): InputMolecule_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = macro BuildInputMolecule.await_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]


  // Input in expressions
  implicit def string2Model(s: String) = TermValue(s)
  implicit def int2Model(i: Int) = TermValue(i)
  implicit def long2Model(l: Long) = TermValue(l)
  implicit def tuple2Model[A, B](tpl: (A, B)) = TermValue(tpl)

  // Entity api
  implicit def long2EntityId(id: Long)(implicit conn: Connection) = conn.db.entity(id)

  // Markers
  object ?
  object ?!
  object maybe
  case class oldNew[T](from: T, to: T)
}
