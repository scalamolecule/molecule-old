package molecule.core.api

import molecule.core.api.Molecule_0._
import molecule.core.api.exception.Molecule_2_Exception
import molecule.core.ast.elements._
import molecule.datomic.base.ast.query._
import molecule.datomic.base.facade.Conn
import scala.concurrent.Future
import scala.util.control.NonFatal


/** Shared interfaces of input molecules awaiting 2 inputs.
 * {{{
 * // Input molecule awaiting 2 inputs for `profession` and `age`
 * val profAge = m(Person.name.profession_(?).age_(?))
 * for {
 *   // Sample data set
 *   _ <- Person.name.profession.age insert List(
 *     ("Ann", "doctor", 37),
 *     ("Ben", "teacher", 37),
 *     ("Joe", "teacher", 32),
 *     ("Liz", "teacher", 28)
 *   )
 *
 *   // A. Pairs of input .................................
 *
 *   // One pair as params
 *   _ <- profAge("doctor", 37).get.map(_ ==> List("Ann"))
 *
 *   // One or more pairs
 *   _ <- profAge(("doctor", 37)).get.map(_ ==> List("Ann"))
 *   _ <- profAge(("doctor", 37), ("teacher", 37)).get.map(_.sorted ==> List("Ann", "Ben"))
 *
 *   // One or more logical pairs
 *   // [pair-expression] or [pair-expression] or ...
 *   _ <- profAge(("doctor" and 37) or ("teacher" and 32)).get.map(_.sorted ==> List("Ann", "Joe"))
 *   _ <- profAge(Seq(("doctor", 37), ("teacher", 37))).get.map(_.sorted ==> List("Ann", "Ben"))
 *
 *   // List of pairs
 *   _ <- profAge(Seq(("doctor", 37))).get.map(_ ==> List("Ann"))
 *
 *
 *   // B. 2 groups of input, one for each input attribute .................................
 *
 *   // Two expressions
 *   // [profession-expression] and [age-expression]
 *   _ <- profAge("doctor" and 37).get.map(_ ==> List("Ann"))
 *   _ <- profAge(("doctor" or "teacher") and 37).get.map(_.sorted ==> List("Ann", "Ben"))
 *   _ <- profAge(("doctor" or "teacher") and (32 or 28)).get.map(_.sorted ==> List("Joe", "Liz"))
 *
 *   // Two Lists
 *   _ <- profAge(Seq("doctor"), Seq(37)).get.map(_ ==> List("Ann"))
 *   _ <- profAge(Seq("doctor", "teacher"), Seq(37)).get.map(_.sorted ==> List("Ann", "Ben"))
 *   _ <- profAge(Seq("teacher"), Seq(37, 32)).get.map(_.sorted ==> List("Ben", "Joe"))
 *   _ <- profAge(Seq("doctor", "teacher"), Seq(37, 32)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
 * } yield ()
 * }}}
 *
 * @tparam I1 Type of input matching first attribute with `?` marker (profession: String)
 * @tparam I2 Type of input matching second attribute with `?` marker (age: Int)
 */
abstract class Molecule_2[Obj, I1, I2](
  model: Model,
  queryData: (Query, String, Option[Throwable])
) extends InputMolecule(model, queryData) {

  protected def resolveOr2(or: Or2[I1, I2]): Either[Throwable, Seq[(I1, I2)]] = {
    def traverse(expr: Or2[I1, I2]): Either[Throwable, Seq[(I1, I2)]] = expr match {
      case Or2(And2(TermValue(a1), TermValue(a2)), And2(TermValue(b1), TermValue(b2))) => Right(Seq((a1, a2), (b1, b2)))
      case Or2(And2(TermValue(a1), TermValue(a2)), or2: Or2[I1, I2])                   => traverse(or2).map((a1, a2) +: _)
      case Or2(or1: Or2[I1, I2], And2(TermValue(b1), TermValue(b2)))                   => traverse(or1).map(_ :+ (b1, b2))
      case Or2(or1: Or2[I1, I2], or2: Or2[I1, I2])                                     => traverse(or1).flatMap(a => traverse(or2).map(b => a ++ b))
      case _                                                                           => Left(Molecule_2_Exception(s"Unexpected Or2 expression: " + expr))
    }
    traverse(or).map(_.distinct)
  }

  protected def resolveAnd2(and2: And2[I1, I2]): Either[Throwable, (Seq[I1], Seq[I2])] = {
    and2 match {
      case And2(TermValue(v1), TermValue(v2)) => Right((Seq(v1), Seq(v2)))
      case And2(or1: Or[I1], TermValue(v2))   => resolveOr(or1).map((_, Seq(v2)))
      case And2(TermValue(v1), or2: Or[I2])   => resolveOr(or2).map((Seq(v1), _))
      case And2(or1: Or[I1], or2: Or[I2])     => resolveOr(or1).flatMap(a => resolveOr(or2).map(b => (a, b)))
      case _                                  => Left(Molecule_2_Exception(s"Unexpected And2 expression: " + and2))
    }
  }


  // Pairs
  protected def bindValues(query: Query, inputTuples: Seq[(I1, I2)]): Either[Throwable, Query] = try {
    val ph1@Placeholder(e1@Var(ex1), kw1@KW(ns1, attr1, _), v1@Var(w1), tpe1, enumPrefix1) = query.i.inputs.head
    val ph2@Placeholder(e2@Var(ex2), kw2@KW(ns2, attr2, _), v2@Var(w2), tpe2, enumPrefix2) = query.i.inputs(1)

    val (v1_, v2_)           = (w1.filter(_.isLetter), w2.filter(_.isLetter))
    val (isTacit1, isTacit2) = (isTacit(ns1, attr1), isTacit(ns2, attr2))
    val (isExpr1, isExpr2)   = (isExpression(ns1, attr1), isExpression(ns2, attr2))
    val hasExpression        = isExpr1 || isExpr2
    val es                   = List(e1, e2).distinct // same or different namespaces

    // Discard placeholders
    val q0 = query.copy(i = In(Nil, query.i.rules, query.i.ds))

    val query2 = inputTuples.distinct match {

      case Nil if !isTacit1 || !isTacit2 =>
        throw Molecule_2_Exception("Can only apply empty list of pairs (Nil) to two tacit attributes")

      case Nil =>
        // Both input attributes to be non-asserted
        val newClauses1 = addNilClause(query.wh.clauses, e1, kw1, v1)
        val newClauses2 = addNilClause(newClauses1, e2, kw2, v2)

        // Remove placeholders
        q0.copy(wh = Where(newClauses2))

      case Seq((arg1, arg2)) =>
        val q1 = resolveInput(q0, ph1, Seq(arg1), unifyRule = true)
        val q2 = resolveInput(q1, ph2, Seq(arg2), unifyRule = true)
        q2

      case pairs if hasExpression => throw Molecule_2_Exception(
        "Can't apply multiple pairs to input attributes with one or more expressions (<, >, <=, >=, !=)")

      case pairs =>
        // Add rule invocation clause to first input attribute clause (only 1 rule invocation needed)
        val rule                    = RuleInvocation("rule1", es)
        val ident1                  = Var(v1_ + 1)
        val ident2                  = Var(v2_ + 1)
        val (done, resolvedClauses) = query.wh.clauses.foldLeft(0, Seq.empty[Seq[Clause]]) {
          case ((n, acc), cl@DataClause(_, _, KW("db", "ident", _), `ident1`, _, _)) if isTacit1 => (n, acc)
          case ((_, acc), cl@DataClause(_, _, _, `v1`, _, _)) if isTacit1                        => (1, acc :+ Seq(rule))
          case ((_, acc), cl@DataClause(_, _, _, `v1`, _, _))                                    => (1, acc :+ Seq(cl, rule))
          case ((n, acc), cl@DataClause(_, _, `kw1`, Var(v), _, _)) if isTacit1 && v == v1_ => (n, acc)
          case ((_, acc), fn@FunctClause(_, List(_, `v1`), _))                              => (1, acc :+ Seq(fn, rule))
          case ((_, acc), fn@FunctClause(_, List(_, _, `v1`), _))                           => (1, acc :+ Seq(fn, rule))
          case ((_, acc), fn@FunctClause(_, _, ScalarBinding(`v1`))) if isTacit1            => (1, acc :+ Seq(rule))
          case ((_, acc), fn@FunctClause(_, _, ScalarBinding(`v1`)))                        => (1, acc :+ Seq(fn, rule))

          case ((n, acc), cl@DataClause(_, _, KW("db", "ident", _), `ident2`, _, _)) if isTacit2 => (n, acc)
          case ((n, acc), cl@DataClause(_, _, _, `v2`, _, _)) if isTacit2                        => (n + 1, acc)
          case ((n, acc), cl@DataClause(_, _, _, `v2`, _, _))                                    => (n + 1, acc :+ Seq(cl))
          case ((n, acc), cl@DataClause(_, _, `kw2`, Var(v), _, _)) if isTacit2 && v == v2_ => (n, acc)
          case ((n, acc), fn@FunctClause(_, List(_, `v2`), _))                              => (n + 1, acc :+ Seq(fn))
          case ((n, acc), fn@FunctClause(_, List(_, _, `v2`), _))                           => (n + 1, acc :+ Seq(fn))
          case ((n, acc), fn@FunctClause(_, _, ScalarBinding(`v2`))) if isTacit2            => (n + 1, acc)
          case ((n, acc), fn@FunctClause(_, _, ScalarBinding(`v2`)))                        => (n + 1, acc :+ Seq(fn))
          case ((n, acc), otherClause)                                                      => (n, acc :+ Seq(otherClause))
        }

        val newClauses = if (done == 2) resolvedClauses.flatten else
          throw Molecule_2_Exception(s"Couldn't find clauses in query that match placeholders:" +
            s"\nplaceholder 1: " + ph1 +
            s"\nplaceholder 2: " + ph2 +
            s"\nnew clauses  :\n  " + resolvedClauses.mkString("\n  ") +
            s"\n" + query
          )

        val rules = pairs.map { case (arg1, arg2) =>
          Rule("rule1", es,
            valueClauses(ex1, kw1, enumPrefix1, tpe1, arg1) ++
              valueClauses(ex2, kw2, enumPrefix2, tpe2, arg2))
        }
        q0.copy(i = In(Nil, query.i.rules ++ rules, query.i.ds), wh = Where(newClauses))
    }
    Right(query2)
  } catch {
    case NonFatal(exc) => Left(exc)
  }


  protected def bindSeqs(query: Query, inputRaw1: Seq[I1], inputRaw2: Seq[I2]): Either[Throwable, Query] = try {
    val List(
    ph1@Placeholder(_, KW(nsFull1, attr1, _), _, tpe1, _),
    ph2@Placeholder(_, KW(nsFull2, attr2, _), _, tpe2, _)) = query.i.inputs

    val input1 = inputRaw1.distinct
    val input2 = inputRaw2.distinct

    def resolve[T](
      query: Query,
      ph: Placeholder,
      input: Seq[T],
      ruleName: String,
      tacit: Boolean,
      expr: Boolean
    ): Query = {
      val Placeholder(_, KW(nsFull, attr, _), _, _, _) = ph
      input match {
        case Nil if !tacit =>
          throw Molecule_2_Exception(s"Can only apply empty list (Nil) to a tacit input attribute. Please make input attr tacit: `$attr` --> `${attr}_`")

        case in if expr && in.size > 1 =>
          throw Molecule_2_Exception(s"Can't apply multiple values to input attribute `:$nsFull/$attr` having expression (<, >, <=, >=, !=)")

        case in =>
          resolveInput(query, ph, in, ruleName)
      }
    }

    // Discard placeholders
    val q0 = query.copy(i = In(Seq(), query.i.rules, query.i.ds))

    // Resolve inputs
    val q1 = resolve(q0, ph1, input1, "rule1", isTacit(nsFull1, attr1), isExpression(nsFull1, attr1))
    val q2 = resolve(q1, ph2, input2, "rule2", isTacit(nsFull2, attr2), isExpression(nsFull2, attr2))
    Right(q2)
  } catch {
    case NonFatal(exc) => Left(exc)
  }


  // Paired inputs -----------------------------------------------------------------------------

  /** Resolve input molecule by applying 2 input values as individual args
   * {{{
   * // Input molecule awaiting 2 inputs for `profession` and `age`
   * val profAge = m(Person.name.profession_(?).age_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age insert List(
   *     ("Ann", "doctor", 37),
   *     ("Ben", "teacher", 37),
   *     ("Joe", "teacher", 32),
   *     ("Liz", "teacher", 28)
   *   )
   *
   *   // Apply 2 input values
   *   _ <- profAge("doctor", 37).get.map(_ ==> List("Ann"))
   * } yield ()
   * }}}
   *
   * @param i1   Input value matching first input attribute (profession: String)
   * @param i2   Input value matching second input attribute (age: Int)
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(i1: I1, i2: I2)(implicit conn: Future[Conn]): Molecule


  /** Resolve input molecule by applying one or more pairs of expressions, each matching both input attributes
   *
   * {{{
   * // Input molecule awaiting 2 inputs for `profession` and `age`
   * val profAge = m(Person.name.profession_(?).age_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age insert List(
   *     ("Ann", "doctor", 37),
   *     ("Ben", "teacher", 37),
   *     ("Joe", "teacher", 32),
   *     ("Liz", "teacher", 28)
   *   )
   *
   *   // Apply two or more pair expressions, each matching both input attributes
   *   // [profession/age-expression] or [profession/age-expression] or ...
   *   _ <- profAge("doctor" and 37).get.map(_.sorted ==> List("Ann"))
   *   _ <- profAge(("doctor" and 37) or ("teacher" and 32)).get.map(_.sorted ==> List("Ann", "Joe"))
   * } yield ()
   * }}}
   *
   * @param or   Two or more pair-wise expressions separated by `or`
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(or: Or2[I1, I2])(implicit conn: Future[Conn]): Molecule


  /** Resolve input molecule by applying one or more value pairs
   *
   * {{{
   * // Input molecule awaiting 2 inputs for `profession` and `age`
   * val profAge = m(Person.name.profession_(?).age_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age insert List(
   *     ("Ann", "doctor", 37),
   *     ("Ben", "teacher", 37),
   *     ("Joe", "teacher", 32),
   *     ("Liz", "teacher", 28)
   *   )
   *
   *   // Apply one or more value pairs, each matching both input attributes
   *   _ <- profAge(("doctor", 37)).get.map(_ ==> List("Ann"))
   *   _ <- profAge(("doctor", 37), ("teacher", 37)).get.map(_.sorted ==> List("Ann", "Ben"))
   * } yield ()
   * }}}
   *
   * @param tpl  First pair of values matching both input attributes
   * @param tpls Optional more pairs of values matching both input attributes
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule


  /** Resolve input molecule by applying Seq of value pairs
   * {{{
   * // Input molecule awaiting 2 inputs for `profession` and `age`
   * val profAge = m(Person.name.profession_(?).age_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age insert List(
   *     ("Ann", "doctor", 37),
   *     ("Ben", "teacher", 37),
   *     ("Joe", "teacher", 32),
   *     ("Liz", "teacher", 28)
   *   )
   *
   *   // Apply Seq of one or more value pairs, each matching both input attributes
   *   _ <- profAge(Seq(("doctor", 37))).get.map(_ ==> List("Ann"))
   *   _ <- profAge(Seq(("doctor", 37), ("teacher", 37))).get.map(_.sorted ==> List("Ann", "Ben"))
   * } yield ()
   * }}}
   *
   * @param ins  Seq of value pairs, each matching both input attributes
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(ins: Seq[(I1, I2)])(implicit conn: Future[Conn]): Molecule


  // Separate inputs -----------------------------------------------------------------------------

  /** Resolve input molecule by applying 2 expressions, one for each input attribute
   *
   * {{{
   * // Input molecule awaiting 2 inputs for `profession` and `age`
   * val profAge = m(Person.name.profession_(?).age_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age insert List(
   *     ("Ann", "doctor", 37),
   *     ("Ben", "teacher", 37),
   *     ("Joe", "teacher", 32),
   *     ("Liz", "teacher", 28)
   *   )
   *
   *   // Apply 2 expressions, one for each input attribute
   *   // [profession-expression] and [age-expression]
   *   _ <- profAge("doctor" and 37).get.map(_ ==> List("Ann"))
   *   _ <- profAge(("doctor" or "teacher") and 37).get.map(_.sorted ==> List("Ann", "Ben"))
   *   _ <- profAge(("doctor" or "teacher") and (32 or 28)).get.map(_.sorted ==> List("Joe", "Liz"))
   * } yield ()
   * }}}
   *
   * @param and  First input expr `and` second input expr
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(and: And2[I1, I2])(implicit conn: Future[Conn]): Molecule


  /** Resolve input molecule by applying 2 Seq of values, one for each input attribute
   *
   * {{{
   * // Input molecule awaiting 2 inputs for profession and age
   * val profAge = m(Person.name.profession_(?).age_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age insert List(
   *     ("Ann", "doctor", 37),
   *     ("Ben", "teacher", 37),
   *     ("Joe", "teacher", 32),
   *     ("Liz", "teacher", 28)
   *   )
   *
   *   // Apply 2 Seq of values, each matching one of the input attributes
   *   _ <- profAge(Seq("doctor"), Seq(37)).get.map(_ ==> List("Ann"))
   *   _ <- profAge(Seq("doctor", "teacher"), Seq(37, 32)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
   *
   *   // Number of arguments in each Seq don't have to match but can be asymmetric
   *   _ <- profAge(Seq("doctor", "teacher"), Seq(37)).get.map(_.sorted ==> List("Ann", "Ben"))
   *   _ <- profAge(Seq("teacher"), Seq(37, 32)).get.map(_.sorted ==> List("Ben", "Joe"))
   * } yield ()
   * }}}
   *
   * @param in1  Seq of values matching first input attribute (professions: Seq[String])
   * @param in2  Seq of values matching second input attribute (ages: Seq[Int])
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(in1: Seq[I1], in2: Seq[I2])(implicit conn: Future[Conn]): Molecule
}


/** Implementations of input molecules awaiting 2 inputs, output arity 1-22 */
object Molecule_2 {

  abstract class Molecule_2_01[Obj, I1, I2, A](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(resolveOr2(or)) // can be Left(exception)
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_01[Obj, A] // generated by macro

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_01[Obj, A] // generated by macro
  }

  abstract class Molecule_2_02[Obj, I1, I2, A, B](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B]
  }

  abstract class Molecule_2_03[Obj, I1, I2, A, B, C](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C]
  }

  abstract class Molecule_2_04[Obj, I1, I2, A, B, C, D](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D]
  }

  abstract class Molecule_2_05[Obj, I1, I2, A, B, C, D, E](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E]
  }

  abstract class Molecule_2_06[Obj, I1, I2, A, B, C, D, E, F](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F]
  }

  abstract class Molecule_2_07[Obj, I1, I2, A, B, C, D, E, F, G](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G]
  }

  abstract class Molecule_2_08[Obj, I1, I2, A, B, C, D, E, F, G, H](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H]
  }

  abstract class Molecule_2_09[Obj, I1, I2, A, B, C, D, E, F, G, H, I](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I]
  }

  abstract class Molecule_2_10[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J]
  }

  abstract class Molecule_2_11[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K]
  }

  abstract class Molecule_2_12[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L]
  }

  abstract class Molecule_2_13[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M]
  }

  abstract class Molecule_2_14[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  }

  abstract class Molecule_2_15[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  }

  abstract class Molecule_2_16[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  }

  abstract class Molecule_2_17[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  }

  abstract class Molecule_2_18[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  }

  abstract class Molecule_2_19[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  }

  abstract class Molecule_2_20[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  }

  abstract class Molecule_2_21[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  }

  abstract class Molecule_2_22[Obj, I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_2[Obj, I1, I2](model, queryData) {
    def apply(i1: I1, i2: I2)                (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(Right(Seq((i1, i2))))
    def apply(tpl: (I1, I2), tpls: (I1, I2)*)(implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or2[I1, I2])               (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(resolveOr2(or))
    def apply(ins: Seq[(I1, I2)])            (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2)]])(implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]

    def apply(and: And2[I1, I2])             (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeSeqs(resolveAnd2(and))
    def apply(in1: Seq[I1], in2: Seq[I2])    (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeSeqs(Right((in1, in2)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2])])(implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]
  }
}
