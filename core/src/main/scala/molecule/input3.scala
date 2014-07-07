package molecule
import datomic.Connection
import molecule.ast.model._
import molecule.ast.query._
import molecule.dsl.schemaDSL
import schemaDSL.NS


// 3 inputs X outputs
trait In_3_0[I1, I2, I3] extends NS
trait In_3_1[I1, I2, I3, A] extends NS
trait In_3_2[I1, I2, I3, A, B] extends NS
trait In_3_3[I1, I2, I3, A, B, C] extends NS
trait In_3_4[I1, I2, I3, A, B, C, D] extends NS
trait In_3_5[I1, I2, I3, A, B, C, D, E] extends NS
trait In_3_6[I1, I2, I3, A, B, C, D, E, F] extends NS
trait In_3_7[I1, I2, I3, A, B, C, D, E, F, G] extends NS
trait In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] extends NS
trait In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I] extends NS
trait In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J] extends NS
trait In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] extends NS
trait In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] extends NS
trait In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS
trait In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS
trait In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS
trait In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS
trait In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS
trait In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS
trait In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS
trait In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS
trait In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS
trait In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS



trait InputMolecule_3[I1, I2, I3] extends InputMolecule {

  // Todo
  //  def resolveAnd(and: And3[(I1, I2, I3)])(implicit conn: Connection): Seq[(I1, I2, I3)] = {
  //    def recurse(expr: And3[(I1, I2, I3)]): Seq[(I1, I2, I3)] = expr match {
  //      case And3(TermValue(v1), TermValue(v2), TermValue(v3))   => Seq((v1, v2, v3))
  //      case And3(and1: And3[I1, I2, I3], and2: And3[(I1, I2, I3)]) => recurse(and1) ++ recurse(and2)
  //    }
  //    recurse(and)
  //  }
  //
  //  def resolveOr(or: Or[(I1, I2, I3)])(implicit conn: Connection): Seq[(I1, I2, I3)] = {
  //    def recurse(expr: Or[(I1, I2, I3)]): Seq[(I1, I2, I3)] = expr match {
  //      case Or(or1: Or[(I1, I2, I3)], And3(t4: TermValue[I1, I2, I3], t5: TermValue[I2], t6: TermValue[I3])) =>
  //        recurse(or1) :+ (t4.term, t5.term, t6.term)
  //      case Or(
  //      And3(t1: TermValue[I1, I2, I3], t2: TermValue[I2], t3: TermValue[I3]),
  //      And3(t4: TermValue[I1, I2, I3], t5: TermValue[I2], t6: TermValue[I3]))                                =>
  //        Seq((t1.term, t2.term, t3.term), (t4.term, t5.term, t6.term))
  //    }
  //    recurse(or)
  //  }


  def bindValues(inputTuples: Seq[(I1, I2, I3)]) = {
    val (vars, p1 :: p2 :: p3 :: Nil) = varsAndPrefixes.unzip
    val values = inputTuples.map(tpl => Seq(p1 + tpl._1, p2 + tpl._2, p3 + tpl._3))
    val query1 = query.copy(in = In(Seq(InVar(RelationBinding(vars), values))))
    val entityQuery = query.copy(find = Find(Seq(Var("ent", "Long"))))
    (query1, entityQuery)
  }
}

abstract class InputMolecule_3_0[I1, I2, I3](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out0
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out0 = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out0 = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_1[I1, I2, I3, A](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out1[A]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out1[A] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out1[A] = apply(head +: tail)(conn)
  // todo?
  //  def apply(and: And3[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out1[A] = apply(resolveAnd(and))(conn)
  //  def apply(or: Or[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out1[A] = apply(Seq((i1, i2, i3)))(conn)
}

abstract class InputMolecule_3_2[I1, I2, I3, A, B](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out2[A, B]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out2[A, B] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out2[A, B] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_3[I1, I2, I3, A, B, C](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out3[A, B, C]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out3[A, B, C] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out3[A, B, C] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_4[I1, I2, I3, A, B, C, D](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out4[A, B, C, D]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out4[A, B, C, D] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out4[A, B, C, D] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_5[I1, I2, I3, A, B, C, D, E](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out5[A, B, C, D, E]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out5[A, B, C, D, E] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out5[A, B, C, D, E] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_6[I1, I2, I3, A, B, C, D, E, F](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out6[A, B, C, D, E, F]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out6[A, B, C, D, E, F] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out6[A, B, C, D, E, F] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_7[I1, I2, I3, A, B, C, D, E, F, G](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out7[A, B, C, D, E, F, G]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out7[A, B, C, D, E, F, G] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out7[A, B, C, D, E, F, G] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_8[I1, I2, I3, A, B, C, D, E, F, G, H](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out8[A, B, C, D, E, F, G, H]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out8[A, B, C, D, E, F, G, H] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out8[A, B, C, D, E, F, G, H] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out9[A, B, C, D, E, F, G, H, I]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out9[A, B, C, D, E, F, G, H, I] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out9[A, B, C, D, E, F, G, H, I] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out10[A, B, C, D, E, F, G, H, I, J]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out10[A, B, C, D, E, F, G, H, I, J] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out10[A, B, C, D, E, F, G, H, I, J] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out11[A, B, C, D, E, F, G, H, I, J, K]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out11[A, B, C, D, E, F, G, H, I, J, K] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out11[A, B, C, D, E, F, G, H, I, J, K] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out12[A, B, C, D, E, F, G, H, I, J, K, L]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out12[A, B, C, D, E, F, G, H, I, J, K, L] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out12[A, B, C, D, E, F, G, H, I, J, K, L] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out13[A, B, C, D, E, F, G, H, I, J, K, L, M]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out13[A, B, C, D, E, F, G, H, I, J, K, L, M] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out13[A, B, C, D, E, F, G, H, I, J, K, L, M] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = apply(head +: tail)(conn)
}

abstract class InputMolecule_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](val model: Model, val query: Query) extends InputMolecule_3[I1, I2, I3] {
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Connection): Molecule with Out22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Connection): Molecule with Out22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = apply(Seq((i1, i2, i3)))(conn)
  def apply(head: (I1, I2, I3), tail: (I1, I2, I3)*)(implicit conn: Connection): Molecule with Out22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = apply(head +: tail)(conn)
}