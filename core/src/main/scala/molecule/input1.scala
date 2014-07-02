package molecule
import datomic.Connection
import molecule.ast.model._
import molecule.ast.query._
import molecule.ast.schemaDSL.NS


// 1 input X outputs
trait In_1_0[I1] extends NS
trait In_1_1[I1, A] extends NS
trait In_1_2[I1, A, B] extends NS
trait In_1_3[I1, A, B, C] extends NS
trait In_1_4[I1, A, B, C, D] extends NS
trait In_1_5[I1, A, B, C, D, E] extends NS
trait In_1_6[I1, A, B, C, D, E, F] extends NS
trait In_1_7[I1, A, B, C, D, E, F, G] extends NS
trait In_1_8[I1, A, B, C, D, E, F, G, H] extends NS
trait In_1_9[I1, A, B, C, D, E, F, G, H, I] extends NS
trait In_1_10[I1, A, B, C, D, E, F, G, H, I, J] extends NS
trait In_1_11[I1, A, B, C, D, E, F, G, H, I, J, K] extends NS
trait In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L] extends NS
trait In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS
trait In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS
trait In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS
trait In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS
trait In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS
trait In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS
trait In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS
trait In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS
trait In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS
trait In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS



trait InputMolecule_1[I1] extends InputMolecule {

  def bindValues(in1: Seq[I1]) = {
    val (vars, p1 :: Nil) = varsAndPrefixes.unzip
    val values = getValues(p1, in1)
    val query1 = if (values.size > 1)
      query.copy(in = In(Seq(InVar(CollectionBinding(vars.head), Seq(values)))))
    else
      query.copy(in = In(Seq(InVar(ScalarBinding(vars.head), Seq(Seq(values.head))))))

    val entityQuery = query1.copy(find = Find(Seq(Var("ent", "Long"))))
    (query1, entityQuery)
  }
}

abstract class InputMolecule_1_0[I1](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out0
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out0 = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out0 = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_1[I1, A](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out1[A]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out1[A] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out1[A] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_2[I1, A, B](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out2[A, B]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out2[A, B] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out2[A, B] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_3[I1, A, B, C](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out3[A, B, C]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out3[A, B, C] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out3[A, B, C] = apply(head +: tail)(conn)
}
