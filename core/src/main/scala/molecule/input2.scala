package molecule
import datomic.Connection
import molecule.ast.model._
import molecule.ast.query._
import molecule.ast.schemaDSL.NS


// 2 inputs X outputs
trait In_2_0[I1, I2] extends NS
trait In_2_1[I1, I2, A] extends NS
trait In_2_2[I1, I2, A, B] extends NS
trait In_2_3[I1, I2, A, B, C] extends NS
trait In_2_4[I1, I2, A, B, C, D] extends NS
trait In_2_5[I1, I2, A, B, C, D, E] extends NS
trait In_2_6[I1, I2, A, B, C, D, E, F] extends NS
trait In_2_7[I1, I2, A, B, C, D, E, F, G] extends NS
trait In_2_8[I1, I2, A, B, C, D, E, F, G, H] extends NS
trait In_2_9[I1, I2, A, B, C, D, E, F, G, H, I] extends NS
trait In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] extends NS
trait In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] extends NS
trait In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] extends NS
trait In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS
trait In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS
trait In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS
trait In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS
trait In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS
trait In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS
trait In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS
trait In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS
trait In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS
trait In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS



trait InputMolecule_2[I1, I2] extends InputMolecule {

  def resolveAnd(and: And2[I1, I2])(implicit conn: Connection): (Seq[I1], Seq[I2]) = {
    def traverse(expr: And2[I1, I2]): (Seq[I1], Seq[I2]) = expr match {
      case And2(TermValue(v1), TermValue(v2)) => (Seq(v1), Seq(v2))
      case And2(or1: Or[I1], or2: Or[I2])     => (resolveOr(or1), resolveOr(or2))
    }
    traverse(and)
  }

  def resolveOr(or: Or2[I1, I2])(implicit conn: Connection): Seq[(I1, I2)] = {
    type V[T] = TermValue[T]
    def traverse(expr: Or2[I1, I2]): Seq[(I1, I2)] = expr match {
      case Or2(And2(TermValue(a1), TermValue(a2)), or2: Or2[I1, I2])                   => (a1, a2) +: traverse(or2)
      case Or2(And2(TermValue(a1), TermValue(a2)), And2(TermValue(b1), TermValue(b2))) => Seq((a1, a2), (b1, b2))
    }
    traverse(or)
  }

  def bindValues(inputTuples: Seq[(I1, I2)]) = {
    val (vars, p1 :: p2 :: Nil) = varsAndPrefixes.unzip
    val values = inputTuples.map(tpl => Seq(p1 + tpl._1, p2 + tpl._2))
    val query1 = query.copy(in = In(Seq(InVar(RelationBinding(vars), values))))
    val entityQuery = query.copy(find = Find(Seq(Var("ent", "Long"))))
    (query1, entityQuery)
  }

  def bindValues2(inputLists: (Seq[I1], Seq[I2])) = {
    // Extract placeholder info and discard placeholders
    val varsAndPrefixes = query.in.inputs.collect {
      case Placeholder(_, kw, t, enumPrefix, e) => (kw, t, enumPrefix.getOrElse(""), e)
    }
    val query1 = query.copy(in = In(Seq()))

    if (varsAndPrefixes.size != 2)
      sys.error(s"[InputMolecule_2] Query should expect exactly 2 inputs:\nQuery: ${query.pretty}")

    // Add rules for each list of inputs
    val query2 = inputLists.productIterator.toList.zip(varsAndPrefixes).foldLeft(query1) {
      case (q, (inputList: Seq[_], (kw, t, p, e))) => {
        // Add rule for each input value
        val ruleName = "rule" + (q.in.rules.map(_.name).distinct.size + 1)
        val newRules = inputList.foldLeft(q.in.rules) { case (rules, input) =>
          val dataClause = DataClause(e, kw, t, Val(p + input.toString))
          val rule = Rule(ruleName, Seq(Var(e)), Seq(dataClause))
          rules :+ rule
        }
        val newIn = q.in.copy(ds = (q.in.ds :+ DS).distinct, rules = newRules)
        val newWhere = Where(q.where.clauses :+ RuleInvocation(ruleName, Seq(Var(e))))
        q.copy(in = newIn, where = newWhere)
      }
    }
    val entityQuery = query2.copy(find = Find(Seq(Var("ent", "Long"))))
    (query2, entityQuery)
  }
}

abstract class InputMolecule_2_0[I1, I2](val model: Model, val query: Query) extends InputMolecule_2[I1, I2] {
  def apply(ins: Seq[(I1, I2)])(implicit conn: Connection): Molecule with Out0
  def apply(i1: I1, i2: I2)(implicit conn: Connection): Molecule with Out0 = apply(Seq((i1, i2)))(conn)

  def apply(and: And2[I1, I2])(implicit conn: Connection): Molecule with Out0 = and match {
    case And2(TermValue(v1), TermValue(v2)) => apply(Seq((v1, v2)))(conn)
    case And2(or1: Or[I1], or2: Or[I2])     => apply(resolveOr(or1), resolveOr(or2))(conn)
  }

  def apply(in1: Seq[I1], in2: Seq[I2])(implicit conn: Connection): Molecule with Out0

  def apply(or: Or2[I1, I2])(implicit conn: Connection): Molecule with Out0 = apply(resolveOr(or))(conn)
  def apply(head: (I1, I2), tail: (I1, I2)*)(implicit conn: Connection): Molecule with Out0 = apply(head +: tail)(conn)
}

abstract class InputMolecule_2_1[I1, I2, A](val model: Model, val query: Query) extends InputMolecule_2[I1, I2] {
  def apply(ins: Seq[(I1, I2)])(implicit conn: Connection): Molecule with Out1[A]
  def apply(i1: I1, i2: I2)(implicit conn: Connection): Molecule with Out1[A] = apply(Seq((i1, i2)))(conn)

  def apply(and: And2[I1, I2])(implicit conn: Connection): Molecule with Out1[A] = and match {
    case And2(TermValue(v1), TermValue(v2)) => apply(Seq((v1, v2)))(conn)
    case And2(or1: Or[I1], or2: Or[I2])     => apply(resolveOr(or1), resolveOr(or2))(conn)
  }

  def apply(in1: Seq[I1], in2: Seq[I2])(implicit conn: Connection): Molecule with Out1[A]

  def apply(or: Or2[I1, I2])(implicit conn: Connection): Molecule with Out1[A] = apply(resolveOr(or))(conn)
  def apply(head: (I1, I2), tail: (I1, I2)*)(implicit conn: Connection): Molecule with Out1[A] = apply(head +: tail)(conn)
}

abstract class InputMolecule_2_2[I1, I2, A, B](val model: Model, val query: Query) extends InputMolecule_2[I1, I2] {
  def apply(ins: Seq[(I1, I2)])(implicit conn: Connection): Molecule with Out2[A, B]
  def apply(i1: I1, i2: I2)(implicit conn: Connection): Molecule with Out2[A, B] = apply(Seq((i1, i2)))(conn)
  def apply(head: (I1, I2), tail: (I1, I2)*)(implicit conn: Connection): Molecule with Out2[A, B] = apply(head +: tail)(conn)

  def apply(in1: Seq[I1], in2: Seq[I2])(implicit conn: Connection): Molecule with Out2[A, B]
  def apply(and: And2[I1, I2])(implicit conn: Connection): Molecule with Out2[A, B] = {
    val (in1, in2) = resolveAnd(and)
    apply(in1, in2)(conn)
  }

  def apply(or: Or2[I1, I2])(implicit conn: Connection): Molecule with Out2[A, B] = apply(resolveOr(or))(conn)
}

abstract class InputMolecule_2_3[I1, I2, A, B, C](val model: Model, val query: Query) extends InputMolecule_2[I1, I2] {
  def apply(ins: Seq[(I1, I2)])(implicit conn: Connection): Molecule with Out3[A, B, C]
  def apply(i1: I1, i2: I2)(implicit conn: Connection): Molecule with Out3[A, B, C] = apply(Seq((i1, i2)))(conn)
  def apply(head: (I1, I2), tail: (I1, I2)*)(implicit conn: Connection): Molecule with Out3[A, B, C] = apply(head +: tail)(conn)

  def apply(in1: Seq[I1], in2: Seq[I2])(implicit conn: Connection): Molecule with Out3[A, B, C]

  def apply(and: And2[I1, I2])(implicit conn: Connection): Molecule with Out3[A, B, C] = {
    val (in1, in2) = resolveAnd(and)
    apply(in1, in2)(conn)
  }
  def apply(or: Or2[I1, I2])(implicit conn: Connection): Molecule with Out3[A, B, C] = apply(resolveOr(or))(conn)
}

abstract class InputMolecule_2_4[I1, I2, A, B, C, D](val model: Model, val query: Query) extends InputMolecule_2[I1, I2] {}

