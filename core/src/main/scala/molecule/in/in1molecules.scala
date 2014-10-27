package molecule.in
import datomic.Connection
import molecule.ast.model._
import molecule.ast.query._
import molecule.dsl.schemaDSL
import molecule.out._


// 1 input X outputs

trait InputMolecule_1[I1] extends InputMolecule {


  def bindValues1(in1: Seq[I1]) = {
    val (vars, Seq(p1)) = varsAndPrefixes.unzip
    val values = getValues(p1, in1)
    val query1 = if (values.size > 1)
      _query.copy(i = In(Seq(InVar(CollectionBinding(vars.head), Seq(values)))))
    else
      _query.copy(i = In(Seq(InVar(ScalarBinding(vars.head), Seq(Seq(values.head))))))


//    println("vars  : " + vars)
//    println("values: " + values)
//    println("------------------------------------------------ ")
//    println(_model)
//    println("XXXXXXXXXXXXXXXX")
//    println(_query)
//    println("------------------------------------------------ ")
//    println(_query.datalog)
//    println("XXXXXXXXXXXXXXXX")
//    println(query1)
//    println("------------------------------------------------ ")
//    println(query1.datalog)
//    println("------------------------------------------------ ")
//    println("RULES: " + (if (query1.i.rules.isEmpty) "none" else query1.i.rules.mkString("[\n ", "\n ", "\n]")))
//    println("------------------------------------------------ ")
//    println("INPUTS: " + values.zipWithIndex.map(e => "\n" + (e._2 + 1) + " " + e._1) + "\n")
//    println("XXXXXXXXXXXXXXXX")
    val entityQuery1 = query1.copy(f = Find(Seq(Var("ent", "Long"))))
    (query1, entityQuery1)
  }
  
  def inputValues1(in1: Seq[I1]) = {
    val (vars, Seq(p1)) = varsAndPrefixes.unzip
    val values = getValues(p1, in1)
    values.zipWithIndex.map(r => (r._2 + 1) + "  " + r._1).mkString("\n")
  }
}

abstract class InputMolecule_1_0[I1](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule0
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule0 = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule0 = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_1[I1, A](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule1[A]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule1[A] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule1[A] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_2[I1, A, B](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule2[A, B]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule2[A, B] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule2[A, B] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_3[I1, A, B, C](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule3[A, B, C]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule3[A, B, C] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule3[A, B, C] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_4[I1, A, B, C, D](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule4[A, B, C, D]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule4[A, B, C, D] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule4[A, B, C, D] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_5[I1, A, B, C, D, E](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule5[A, B, C, D, E]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule5[A, B, C, D, E] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule5[A, B, C, D, E] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_6[I1, A, B, C, D, E, F](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule6[A, B, C, D, E, F]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule6[A, B, C, D, E, F] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule6[A, B, C, D, E, F] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_7[I1, A, B, C, D, E, F, G](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule7[A, B, C, D, E, F, G]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule7[A, B, C, D, E, F, G] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule7[A, B, C, D, E, F, G] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_8[I1, A, B, C, D, E, F, G, H](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule8[A, B, C, D, E, F, G, H]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule8[A, B, C, D, E, F, G, H] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule8[A, B, C, D, E, F, G, H] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_9[I1, A, B, C, D, E, F, G, H, I](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule9[A, B, C, D, E, F, G, H, I]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule9[A, B, C, D, E, F, G, H, I] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule9[A, B, C, D, E, F, G, H, I] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_10[I1, A, B, C, D, E, F, G, H, I, J](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule10[A, B, C, D, E, F, G, H, I, J]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule10[A, B, C, D, E, F, G, H, I, J] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule10[A, B, C, D, E, F, G, H, I, J] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_11[I1, A, B, C, D, E, F, G, H, I, J, K](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule11[A, B, C, D, E, F, G, H, I, J, K]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule11[A, B, C, D, E, F, G, H, I, J, K] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule11[A, B, C, D, E, F, G, H, I, J, K] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule12[A, B, C, D, E, F, G, H, I, J, K, L]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule12[A, B, C, D, E, F, G, H, I, J, K, L] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule12[A, B, C, D, E, F, G, H, I, J, K, L] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = apply(head +: tail)(conn)
}

abstract class InputMolecule_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](val _model: Model, val _query: Query) extends InputMolecule_1[I1] {
  def apply(args: Seq[I1])      (implicit conn: Connection): Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]
  def apply(or: Or[I1])         (implicit conn: Connection): Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = apply(resolveOr(or))(conn)
  def apply(head: I1, tail: I1*)(implicit conn: Connection): Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = apply(head +: tail)(conn)
}
