package molecule
import datomic.Connection
import molecule.ast.model._
import molecule.ast.query._
import molecule.dsl.schemaDSL
import schemaDSL.NS


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

abstract class InputMolecule_1_4[I1, A, B, C, D](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out4[A, B, C, D]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out4[A, B, C, D] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out4[A, B, C, D] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_5[I1, A, B, C, D, E](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out5[A, B, C, D, E]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out5[A, B, C, D, E] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out5[A, B, C, D, E] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_6[I1, A, B, C, D, E, F](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out6[A, B, C, D, E, F]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out6[A, B, C, D, E, F] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out6[A, B, C, D, E, F] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_7[I1, A, B, C, D, E, F, G](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out7[A, B, C, D, E, F, G]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out7[A, B, C, D, E, F, G] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out7[A, B, C, D, E, F, G] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_8[I1, A, B, C, D, E, F, G, H](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out8[A, B, C, D, E, F, G, H]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out8[A, B, C, D, E, F, G, H] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out8[A, B, C, D, E, F, G, H] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_9[I1, A, B, C, D, E, F, G, H, I](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out9[A, B, C, D, E, F, G, H, I]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out9[A, B, C, D, E, F, G, H, I] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out9[A, B, C, D, E, F, G, H, I] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_10[I1, A, B, C, D, E, F, G, H, I, J](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out10[A, B, C, D, E, F, G, H, I, J]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out10[A, B, C, D, E, F, G, H, I, J] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out10[A, B, C, D, E, F, G, H, I, J] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_11[I1, A, B, C, D, E, F, G, H, I, J, K](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out11[A, B, C, D, E, F, G, H, I, J, K]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out11[A, B, C, D, E, F, G, H, I, J, K] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out11[A, B, C, D, E, F, G, H, I, J, K] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out12[A, B, C, D, E, F, G, H, I, J, K, L]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out12[A, B, C, D, E, F, G, H, I, J, K, L] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out12[A, B, C, D, E, F, G, H, I, J, K, L] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out13[A, B, C, D, E, F, G, H, I, J, K, L, M]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out13[A, B, C, D, E, F, G, H, I, J, K, L, M] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out13[A, B, C, D, E, F, G, H, I, J, K, L, M] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](val model: Model, val query: Query) extends InputMolecule_1[I1] {
  def apply(ins: Seq[I1])(implicit conn: Connection): Molecule with Out22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]
  def apply(or: Or[I1])(implicit conn: Connection): Molecule with Out22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule with Out22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = apply(head +: tail)(conn)
}
