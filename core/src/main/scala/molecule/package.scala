
import java.net.URI
import java.util.{Date, UUID}
import datomic._
import molecule.ast.model._
import molecule.in._
import molecule.out._
import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.language.higherKinds

package object molecule {

  // Generic interfaces

  implicit def m[Ns0, Ns1[_]](dsl: Molecule_0[Ns0, Ns1]): Molecule0 = macro BuildMolecule.from0attr[Ns0, Ns1]
  implicit def m[Ns1[_], Ns2[_,_], A](dsl: Molecule_1[Ns1, Ns2, A]): Molecule1[A] = macro BuildMolecule.from1attr[Ns1, Ns2, A]
  implicit def m[Ns2[_,_], Ns3[_,_,_], A, B](dsl: Molecule_2[Ns2, Ns3, A, B]): Molecule2[A, B] = macro BuildMolecule.from2attr[Ns2, Ns3, A, B]
  implicit def m[Ns3[_,_,_], Ns4[_,_,_,_], A, B, C](dsl: Molecule_3[Ns3, Ns4, A, B, C]): Molecule3[A, B, C] = macro BuildMolecule.from3attr[Ns3, Ns4, A, B, C]
  implicit def m[Ns4[_,_,_,_], Ns5[_,_,_,_,_], A, B, C, D](dsl: Molecule_4[Ns4, Ns5, A, B, C, D]): Molecule4[A, B, C, D] = macro BuildMolecule.from4attr[Ns4, Ns5, A, B, C, D]
  implicit def m[Ns5[_,_,_,_,_], Ns6[_,_,_,_,_,_], A, B, C, D, E](dsl: Molecule_5[Ns5, Ns6, A, B, C, D, E]): Molecule5[A, B, C, D, E] = macro BuildMolecule.from5attr[Ns5, Ns6, A, B, C, D, E]
  implicit def m[Ns6[_,_,_,_,_,_], Ns7[_,_,_,_,_,_,_], A, B, C, D, E, F](dsl: Molecule_6[Ns6, Ns7, A, B, C, D, E, F]): Molecule6[A, B, C, D, E, F] = macro BuildMolecule.from6attr[Ns6, Ns7, A, B, C, D, E, F]
  implicit def m[Ns7[_,_,_,_,_,_,_], Ns8[_,_,_,_,_,_,_,_], A, B, C, D, E, F, G](dsl: Molecule_7[Ns7, Ns8, A, B, C, D, E, F, G]): Molecule7[A, B, C, D, E, F, G] = macro BuildMolecule.from7attr[Ns7, Ns8, A, B, C, D, E, F, G]
  implicit def m[Ns8[_,_,_,_,_,_,_,_], Ns9[_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H](dsl: Molecule_8[Ns8, Ns9, A, B, C, D, E, F, G, H]): Molecule8[A, B, C, D, E, F, G, H] = macro BuildMolecule.from8attr[Ns8, Ns9, A, B, C, D, E, F, G, H]
  implicit def m[Ns9[_,_,_,_,_,_,_,_,_], Ns10[_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I](dsl: Molecule_9[Ns9, Ns10, A, B, C, D, E, F, G, H, I]): Molecule9[A, B, C, D, E, F, G, H, I] = macro BuildMolecule.from9attr[Ns9, Ns10, A, B, C, D, E, F, G, H, I]
  implicit def m[Ns10[_,_,_,_,_,_,_,_,_,_], Ns11[_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J](dsl: Molecule_10[Ns10, Ns11, A, B, C, D, E, F, G, H, I, J]): Molecule10[A, B, C, D, E, F, G, H, I, J] = macro BuildMolecule.from10attr[Ns10, Ns11, A, B, C, D, E, F, G, H, I, J]
  implicit def m[Ns11[_,_,_,_,_,_,_,_,_,_,_], Ns12[_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K](dsl: Molecule_11[Ns11, Ns12, A, B, C, D, E, F, G, H, I, J, K]): Molecule11[A, B, C, D, E, F, G, H, I, J, K] = macro BuildMolecule.from11attr[Ns11, Ns12, A, B, C, D, E, F, G, H, I, J, K]
  implicit def m[Ns12[_,_,_,_,_,_,_,_,_,_,_,_], Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L](dsl: Molecule_12[Ns12, Ns13, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule12[A, B, C, D, E, F, G, H, I, J, K, L] = macro BuildMolecule.from12attr[Ns12, Ns13, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit def m[Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_], Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: Molecule_13[Ns13, Ns14, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M] = macro BuildMolecule.from13attr[Ns13, Ns14, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit def m[Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: Molecule_14[Ns14, Ns15, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro BuildMolecule.from14attr[Ns14, Ns15, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit def m[Ns15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: Molecule_15[Ns15, Ns16, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro BuildMolecule.from15attr[Ns15, Ns16, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit def m[Ns16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](dsl: Molecule_16[Ns16, Ns17, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro BuildMolecule.from16attr[Ns16, Ns17, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit def m[Ns17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](dsl: Molecule_17[Ns17, Ns18, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro BuildMolecule.from17attr[Ns17, Ns18, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  implicit def m[Ns18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](dsl: Molecule_18[Ns18, Ns19, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro BuildMolecule.from18attr[Ns18, Ns19, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  implicit def m[Ns19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](dsl: Molecule_19[Ns19, Ns20, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]): Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = macro BuildMolecule.from19attr[Ns19, Ns20, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  implicit def m[Ns20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](dsl: Molecule_20[Ns20, Ns21, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]): Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = macro BuildMolecule.from20attr[Ns20, Ns21, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  implicit def m[Ns21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](dsl: Molecule_21[Ns21, Ns22, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]): Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = macro BuildMolecule.from21attr[Ns21, Ns22, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  implicit def m[Ns22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Nothing, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](dsl: Molecule_22[Ns22, Nothing, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]): Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = macro BuildMolecule.from22attr[Ns22, Nothing, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]


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


  // Attribute expressions

  implicit def string2Model (v: String)  = TermValue(v)
  implicit def int2Model    (v: Int)     = TermValue(v)
  implicit def long2Model   (v: Long)    = TermValue(v)
  implicit def float2Model  (v: Float)   = TermValue(v)
  implicit def double2Model (v: Double)  = TermValue(v)
  implicit def boolean2Model(v: Boolean) = TermValue(v)
  implicit def date2Model   (v: Date)    = TermValue(v)
  implicit def uuid2Model   (v: UUID)    = TermValue(v)
  implicit def uri2Model    (v: URI)     = TermValue(v)

  implicit def stringSet2Model (set: Set[String])  = TermValue(set)
  implicit def intSet2Model    (set: Set[Int])     = TermValue(set)
  implicit def longSet2Model   (set: Set[Long])    = TermValue(set)
  implicit def floatSet2Model  (set: Set[Float])   = TermValue(set)
  implicit def doubleSet2Model (set: Set[Double])  = TermValue(set)
  implicit def booleanSet2Model(set: Set[Boolean]) = TermValue(set)
  implicit def dateSet2Model   (set: Set[Date])    = TermValue(set)
  implicit def uuidSet2Model   (set: Set[UUID])    = TermValue(set)
  implicit def uriSet2Model    (set: Set[URI])     = TermValue(set)

//  implicit def contains2Model[T](c: contains[T]) = TermValue(c.value)
  implicit def tuple2Model[A, B](tpl: (A, B)) = TermValue(tpl)

  // Entity api
  implicit def long2Entity(id: Long)(implicit conn: Connection) = EntityFacade(conn.db.entity(id), conn, id.asInstanceOf[Object])


  // Markers

  trait ?
  object ? extends ?

  trait maybe
  object maybe extends maybe


  // Aggregates ==========================================================

  // Aggregate attribute (singular/multiple)

  trait max
  private[molecule] trait maxs
  object max extends max { def apply(i: Int): maxs = ???}

  trait min
  private[molecule] trait mins
  object min extends min {def apply(i: Int): mins = ???}

  trait distinct
  object distinct extends distinct

  trait rand
  private[molecule] trait rands
  object rand extends rand { def apply(i: Int): rands = ???}

  trait sample
  private[molecule] trait samples
  object sample extends sample { def apply(i: Int): samples = ???}


  // Aggregate calculation

  trait aggregate[A] {
    def apply[B](a: aggregate[B]): A = ???
  }

  trait count extends aggregate[count]
  object count extends count

  trait countDistinct extends aggregate[countDistinct]
  object countDistinct extends countDistinct

  trait sum extends aggregate[sum]
  object sum extends sum

  trait avg extends aggregate[avg]
  object avg extends avg

  trait median extends aggregate[median]
  object median extends median

  trait variance extends aggregate[variance]
  object variance extends variance

  trait stddev extends aggregate[stddev]
  object stddev extends stddev

//  case class contains[T](value: T)
//  case class oldNew[T](from: T, to: T)
}
