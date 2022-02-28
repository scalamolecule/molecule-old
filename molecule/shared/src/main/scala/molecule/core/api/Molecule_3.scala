package molecule.core.api

import molecule.core.api.Molecule_0._
import molecule.core.api.exception.Molecule_3_Exception
import molecule.core.ast.elements._
import molecule.datomic.base.ast.query._
import molecule.datomic.base.facade.Conn
import scala.concurrent.Future
import scala.util.control.NonFatal


/** Shared interfaces of input molecules awaiting 3 inputs.
 * {{{
 * // Input molecule awaiting 3 inputs for `profession`, `age` and `score`
 * val profAgeScore = m(Person.name.profession_(?).age_(?).score_(?))
 * for {
 *   // Sample data set
 *   Person.name.profession.age.score insert List(
 *     ("Ann", "doctor", 37, 1.0),
 *     ("Ben", "teacher", 37, 1.0),
 *     ("Joe", "teacher", 32, 1.0),
 *     ("Liz", "teacher", 28, 2.0)
 *   )
 *
 *   // A. Triples of input .................................
 *
 *   // One triple as params
 *   _ <- profAgeScore.apply("doctor", 37, 1.0).get.map(_ ==> List("Ann"))
 *
 *   // One or more triples
 *   _ <- profAgeScore.apply(("doctor", 37, 1.0)).get.map(_ ==> List("Ann"))
 *   _ <- profAgeScore.apply(("doctor", 37, 1.0), ("teacher", 37, 1.0)).get.map(_.sorted ==> List("Ann", "Ben"))
 *
 *   // One or more logical triples
 *   // [triple-expression] or [triple-expression] or ...
 *   _ <- profAgeScore.apply(("doctor" and 37 and 1.0) or ("teacher" and 32 and 1.0)).get.map(_.sorted ==> List("Ann", "Joe"))
 *
 *   // List of triples
 *   _ <- profAgeScore.apply(Seq(("doctor", 37, 1.0))).get.map(_ ==> List("Ann"))
 *   _ <- profAgeScore.apply(Seq(("doctor", 37, 1.0), ("teacher", 37, 1.0))).get.map(_.sorted ==> List("Ann", "Ben"))
 *
 *
 *   // B. 3 groups of input, one for each input attribute .................................
 *
 *   // Three expressions
 *   // [profession-expression] and [age-expression] and [score-expression]
 *   _ <- profAgeScore.apply("doctor" and 37 and 1.0).get.map(_ ==> List("Ann"))
 *   _ <- profAgeScore.apply(("doctor" or "teacher") and 37 and 1.0).get.map(_.sorted ==> List("Ann", "Ben"))
 *   _ <- profAgeScore.apply(("doctor" or "teacher") and (37 or 32) and 1.0).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
 *   _ <- profAgeScore.apply(("doctor" or "teacher") and (37 or 32) and (1.0 or 2.0)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
 *
 *   // Three lists
 *   _ <- profAgeScore.apply(Seq("doctor"), Seq(37), Seq(1.0)).get.map(_ ==> List("Ann"))
 *   _ <- profAgeScore.apply(Seq("doctor", "teacher"), Seq(37), Seq(1.0)).get.map(_.sorted ==> List("Ann", "Ben"))
 * } yield ()
 * }}}
 *
 * @tparam I1 Type of input matching first attribute with `?` marker (profession: String)
 * @tparam I2 Type of input matching second attribute with `?` marker (age: Int)
 * @tparam I3 Type of input matching third attribute with `?` marker (score: Double)
 */
abstract class Molecule_3[Obj, I1, I2, I3](
  model: Model,
  queryData: (Query, String, Option[Throwable])
) extends InputMolecule(model, queryData) {

  protected def resolveOr3(or: Or3[I1, I2, I3]): Either[Throwable, Seq[(I1, I2, I3)]] = {
    def traverse(expr: Or3[I1, I2, I3]): Either[Throwable, Seq[(I1, I2, I3)]] = expr match {
      case Or3(
      And3(TermValue(a1), TermValue(a2), TermValue(a3)),
      And3(TermValue(b1), TermValue(b2), TermValue(b3)),
      or3: Or3[I1, I2, I3]
      ) => traverse(or3).map(Seq((a1, a2, a3), (b1, b2, b3)) ++ _)

      case Or3(
      And3(TermValue(a1), TermValue(a2), TermValue(a3)),
      And3(TermValue(b1), TermValue(b2), TermValue(b3)),
      And3(TermValue(c1), TermValue(c2), TermValue(c3))
      ) => Right(Seq((a1, a2, a3), (b1, b2, b3), (c1, c2, c3)))

      case _ => Left(Molecule_3_Exception(s"Unexpected Or3 expression: " + expr))
    }
    traverse(or).map(_.distinct)
  }

  protected def resolveAnd3(and3: And3[I1, I2, I3]): Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])] = {
    and3 match {
      case And3(TermValue(v1), TermValue(v2), TermValue(v3)) => Right((Seq(v1), Seq(v2), Seq(v3)))
      case And3(or1: Or[I1], TermValue(v2), TermValue(v3))   => resolveOr(or1).map((_, Seq(v2), Seq(v3)))
      case And3(TermValue(v1), or2: Or[I2], TermValue(v3))   => resolveOr(or2).map((Seq(v1), _, Seq(v3)))
      case And3(TermValue(v1), TermValue(v2), or3: Or[I3])   => resolveOr(or3).map((Seq(v1), Seq(v2), _))
      case And3(or1: Or[I1], or2: Or[I2], TermValue(v3))     =>
        for {
          a <- resolveOr(or1)
          b <- resolveOr(or2)
        } yield (a, b, Seq(v3))

      case And3(or1: Or[I1], TermValue(v2), or3: Or[I3]) =>
        for {
          a <- resolveOr(or1)
          c <- resolveOr(or3)
        } yield (a, Seq(v2), c)

      case And3(TermValue(v1), or2: Or[I2], or3: Or[I3]) =>
        for {
          b <- resolveOr(or2)
          c <- resolveOr(or3)
        } yield (Seq(v1), b, c)

      case And3(or1: Or[I1], or2: Or[I2], or3: Or[I3]) =>
        for {
          a <- resolveOr(or1)
          b <- resolveOr(or2)
          c <- resolveOr(or3)
        } yield (a, b, c)

      case _ => Left(throw Molecule_3_Exception(s"Unexpected And3 expression: " + and3))
    }
  }

  // Triples
  protected def bindValues(query: Query, inputTriples: Seq[(I1, I2, I3)]): Either[Throwable, Query] = try {
    val ph1@Placeholder(e1@Var(ex1), kw1@KW(ns1, attr1, _), v1@Var(w1), tpe1, enumPrefix1) = query.i.inputs.head
    val ph2@Placeholder(e2@Var(ex2), kw2@KW(ns2, attr2, _), v2@Var(w2), tpe2, enumPrefix2) = query.i.inputs(1)
    val ph3@Placeholder(e3@Var(ex3), kw3@KW(ns3, attr3, _), v3@Var(w3), tpe3, enumPrefix3) = query.i.inputs(2)

    val (v1_, v2_, v3_)                = (w1.filter(_.isLetter), w2.filter(_.isLetter), w3.filter(_.isLetter))
    val (isTacit1, isTacit2, isTacit3) = (isTacit(ns1, attr1), isTacit(ns2, attr2), isTacit(ns3, attr3))
    val (isExpr1, isExpr2, isExpr3)    = (isExpression(ns1, attr1), isExpression(ns2, attr2), isExpression(ns3, attr3))
    val hasExpression                  = isExpr1 || isExpr2 || isExpr3
    val es                             = List(e1, e2, e3).distinct // same or different namespaces


    // Discard placeholders
    val q0 = query.copy(i = In(Nil, query.i.rules, query.i.ds))

    val query2 = inputTriples.distinct match {

      case Nil if !isTacit1 || !isTacit2 || !isTacit3 =>
        throw Molecule_3_Exception("Can only apply empty list of pairs (Nil) to two tacit attributes")

      case Nil =>
        // Both input attributes to be non-asserted
        val newClauses1 = addNilClause(query.wh.clauses, e1, kw1, v1)
        val newClauses2 = addNilClause(newClauses1, e2, kw2, v2)
        val newClauses3 = addNilClause(newClauses2, e3, kw3, v3)
        q0.copy(wh = Where(newClauses3))

      case Seq((arg1, arg2, arg3)) =>
        val q1 = resolveInput(q0, ph1, Seq(arg1), unifyRule = true)
        val q2 = resolveInput(q1, ph2, Seq(arg2), unifyRule = true)
        val q3 = resolveInput(q2, ph3, Seq(arg3), unifyRule = true)
        q3

      case triples if hasExpression => throw Molecule_3_Exception(
        "Can't apply multiple triples to input attributes with one or more expressions (<, >, <=, >=, !=)")

      case triples =>
        def resolve2[A, B](query: Query,
                           ph1: Placeholder, ph2: Placeholder,
                           es: Seq[Var],
                           ex1: String, ex2: String,
                           enumPrefix1: Option[String], enumPrefix2: Option[String],
                           kw1: KW, kw2: KW,
                           v1: Var, v2: Var,
                           v1_ : String, v2_ : String,
                           isTacit1: Boolean, isTacit2: Boolean,
                           pairs: Seq[(A, B)]
                          ): Query = {
          val rule                    = RuleInvocation("rule2", es)
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
            throw Molecule_3_Exception(s"Couldn't find clauses in query that match placeholders:" +
              s"\nplaceholder 1: " + ph1 +
              s"\nplaceholder 2: " + ph2 +
              s"\nnew clauses  :\n  " + resolvedClauses.mkString("\n  ") +
              s"\n" + query
            )

          val rules = pairs.map { case (arg1, arg2) =>
            Rule("rule2", es, valueClauses(ex1, kw1, enumPrefix1, tpe1, arg1) ++ valueClauses(ex2, kw2, enumPrefix2, tpe2, arg2))
          }
          query.copy(i = In(Nil, query.i.rules ++ rules, query.i.ds), wh = Where(newClauses))
        }

        triples.head match {

          // 2 input attributes share rule

          case (set: Set[_], _, _) if set.size <= 1 || isExpr1 => {
            val (args, pairs) = triples.map { case (arg1, arg2, arg3) => (arg1, (arg2, arg3)) }.unzip
            val q1            = resolveInput(q0, ph1, args)
            resolve2(q1, ph2, ph3, Seq(e2, e3).distinct, ex2, ex3, enumPrefix2, enumPrefix3, kw2, kw3, v2, v3, v2_, v3_, isTacit2, isTacit3, pairs)
          }
          case (_, set: Set[_], _) if set.size <= 1 || isExpr2 => {
            val (args, pairs) = triples.map { case (arg1, arg2, arg3) => (arg2, (arg1, arg3)) }.unzip
            val q1            = resolveInput(q0, ph2, args)
            resolve2(q1, ph1, ph3, Seq(e1, e3).distinct, ex1, ex3, enumPrefix1, enumPrefix3, kw1, kw3, v1, v3, v1_, v3_, isTacit1, isTacit3, pairs)
          }
          case (_, _, set: Set[_]) if set.size <= 1 || isExpr3 => {
            val (args, pairs) = triples.map { case (arg1, arg2, arg3) => (arg3, (arg1, arg2)) }.unzip
            val q1            = resolveInput(q0, ph3, args)
            resolve2(q1, ph1, ph2, Seq(e1, e2).distinct, ex1, ex2, enumPrefix1, enumPrefix2, kw1, kw2, v1, v2, v1_, v2_, isTacit1, isTacit2, pairs)
          }

          // 3 input attributes share rule
          case _ =>
            // Add rule invocation clause to first input attribute clause (only 1 rule invocation needed)
            val rule                    = RuleInvocation("rule1", es)
            val ident1                  = Var(v1_ + 1)
            val ident2                  = Var(v2_ + 1)
            val ident3                  = Var(v3_ + 1)
            val (done, resolvedClauses) = query.wh.clauses.foldLeft(0, Seq.empty[Seq[Clause]]) {
              case ((n, acc), cl@DataClause(_, _, KW("db", "ident", _), `ident1`, _, _)) if isTacit1 => (n, acc)
              case ((_, acc), cl@DataClause(_, _, _, `v1`, _, _)) if isTacit1                        => (1, acc :+ Seq(rule))
              case ((n, acc), cl@DataClause(_, _, `kw1`, Var(v), _, _)) if isTacit1 && v == v1_      => (n, acc)
              case ((_, acc), cl@DataClause(_, _, _, `v1`, _, _))                    => (1, acc :+ Seq(cl, rule))
              case ((_, acc), fn@FunctClause(_, List(_, `v1`), _))                   => (1, acc :+ Seq(fn, rule))
              case ((_, acc), fn@FunctClause(_, List(_, _, `v1`), _))                => (1, acc :+ Seq(fn, rule))
              case ((_, acc), fn@FunctClause(_, _, ScalarBinding(`v1`))) if isTacit1 => (1, acc :+ Seq(rule))
              case ((_, acc), fn@FunctClause(_, _, ScalarBinding(`v1`)))             => (1, acc :+ Seq(fn, rule))

              case ((n, acc), cl@DataClause(_, _, KW("db", "ident", _), `ident2`, _, _)) if isTacit2 => (n, acc)
              case ((n, acc), cl@DataClause(_, _, _, `v2`, _, _)) if isTacit2                        => (n + 1, acc)
              case ((n, acc), cl@DataClause(_, _, _, `v2`, _, _))                                    => (n + 1, acc :+ Seq(cl))
              case ((n, acc), cl@DataClause(_, _, `kw2`, Var(v), _, _)) if isTacit2 && v == v2_ => (n, acc)
              case ((n, acc), fn@FunctClause(_, List(_, `v2`), _))                              => (n + 1, acc :+ Seq(fn))
              case ((n, acc), fn@FunctClause(_, List(_, _, `v2`), _))                           => (n + 1, acc :+ Seq(fn))
              case ((n, acc), fn@FunctClause(_, _, ScalarBinding(`v2`))) if isTacit2            => (n + 1, acc)
              case ((n, acc), fn@FunctClause(_, _, ScalarBinding(`v2`)))                        => (n + 1, acc :+ Seq(fn))

              case ((n, acc), cl@DataClause(_, _, KW("db", "ident", _), `ident3`, _, _)) if isTacit2 => (n, acc)
              case ((n, acc), cl@DataClause(_, _, _, `v3`, _, _)) if isTacit3                        => (n + 1, acc)
              case ((n, acc), cl@DataClause(_, _, _, `v3`, _, _))                                    => (n + 1, acc :+ Seq(cl))
              case ((n, acc), cl@DataClause(_, _, `kw3`, Var(v), _, _)) if isTacit3 && v == v3_ => (n, acc)
              case ((n, acc), fn@FunctClause(_, List(_, `v3`), _))                              => (n + 1, acc :+ Seq(fn))
              case ((n, acc), fn@FunctClause(_, List(_, _, `v3`), _))                           => (n + 1, acc :+ Seq(fn))
              case ((n, acc), fn@FunctClause(_, _, ScalarBinding(`v3`))) if isTacit3            => (n + 1, acc)
              case ((n, acc), fn@FunctClause(_, _, ScalarBinding(`v3`)))                        => (n + 1, acc :+ Seq(fn))

              case ((n, acc), otherClause) => (n, acc :+ Seq(otherClause))
            }

            val newClauses = if (done == 3) resolvedClauses.flatten else
              throw Molecule_3_Exception(s"Couldn't find clauses in query that match placeholders:" +
                s"\nplaceholder 1: " + ph1 +
                s"\nplaceholder 2: " + ph2 +
                s"\nplaceholder 3: " + ph3 +
                s"\nnew clauses  :\n  " + resolvedClauses.mkString("\n  ") +
                s"\n" + query
              )

            val rules = triples.map { case (arg1, arg2, arg3) =>
              Rule("rule1", es,
                valueClauses(ex1, kw1, enumPrefix1, tpe1, arg1) ++
                  valueClauses(ex2, kw2, enumPrefix2, tpe2, arg2) ++
                  valueClauses(ex3, kw3, enumPrefix3, tpe3, arg3))
            }
            q0.copy(i = In(Nil, query.i.rules ++ rules, query.i.ds), wh = Where(newClauses))
        }
    }
    Right(query2)
  } catch {
    case NonFatal(exc) => Left(exc)
  }


  protected def bindSeqs(query: Query, inputRaw1: Seq[I1], inputRaw2: Seq[I2], inputRaw3: Seq[I3]): Either[Throwable, Query] = try {
    val List(
    ph1@Placeholder(_, KW(ns1, attr1, _), _, tpe1, _),
    ph2@Placeholder(_, KW(ns2, attr2, _), _, tpe2, _),
    ph3@Placeholder(_, KW(ns3, attr3, _), _, tpe3, _)) = query.i.inputs

    val input1 = inputRaw1.distinct
    val input2 = inputRaw2.distinct
    val input3 = inputRaw3.distinct

    def resolve[T](
      query: Query,
      ph: Placeholder,
      input: Seq[T],
      tpe: String,
      ruleName: String,
      tacit: Boolean,
      expr: Boolean
    ): Query = {
      val Placeholder(_, KW(nsFull, attr, _), _, _, _) = ph
      input match {
        case Nil if !tacit =>
          throw Molecule_3_Exception(s"Can only apply empty list (Nil) to a tacit input attribute. Please make input attr tacit: `$attr` --> `${attr}_`")

        case in if expr && in.size > 1 =>
          throw Molecule_3_Exception(s"Can't apply multiple values to input attribute `:$nsFull/$attr` having expression (<, >, <=, >=, !=)")

        case in =>
          resolveInput(query, ph, in, ruleName)
      }
    }

    // Discard placeholders
    val q0 = query.copy(i = In(Seq(), query.i.rules, query.i.ds))

    // Resolve inputs
    val q1 = resolve(q0, ph1, input1, tpe1, "rule1", isTacit(ns1, attr1), isExpression(ns1, attr1))
    val q2 = resolve(q1, ph2, input2, tpe2, "rule2", isTacit(ns2, attr2), isExpression(ns2, attr2))
    val q3 = resolve(q2, ph3, input3, tpe3, "rule3", isTacit(ns3, attr3), isExpression(ns3, attr3))
    Right(q3)
  } catch {
    case NonFatal(exc) => Left(exc)
  }


  /** Resolve input molecule by applying 3 input values as individual args
   * {{{
   * // Input molecule awaiting 3 inputs for `profession`, `age` and `score`
   * val profAgeScore = m(Person.name.profession_(?).age_(?).score_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age.score insert List(
   *     ("Ann", "doctor", 37, 1.0),
   *     ("Ben", "teacher", 37, 1.0),
   *     ("Joe", "teacher", 32, 1.0),
   *     ("Liz", "teacher", 28, 2.0)
   *   )
   *
   *   // Apply 3 input values (1 triple)
   *   _ <- profAgeScore.apply("doctor", 37, 1.0).get.map(_ ==> List("Ann"))
   * } yield ()
   * }}}
   *
   * @param i1   Input value matching first input attribute (profession: String)
   * @param i2   Input value matching second input attribute (age: Int)
   * @param i3   Input value matching third input attribute (score: Double)
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(i1: I1, i2: I2, i3: I3)(implicit conn: Future[Conn]): Molecule


  /** Resolve input molecule by applying one or more value triples
   *
   * {{{
   * // Input molecule awaiting 3 inputs for `profession`, `age` and `score`
   * val profAgeScore = m(Person.name.profession_(?).age_(?).score_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age.score insert List(
   *     ("Ann", "doctor", 37, 1.0),
   *     ("Ben", "teacher", 37, 1.0),
   *     ("Joe", "teacher", 32, 1.0),
   *     ("Liz", "teacher", 28, 2.0)
   *   )
   *
   *   // Apply one or more value triples, each matching all 3 input attributes
   *   _ <- profAgeScore.apply(("doctor", 37, 1.0)).get.map(_ ==> List("Ann"))
   *   _ <- profAgeScore.apply(("doctor", 37, 1.0), ("teacher", 37, 1.0)).get.map(_.sorted ==> List("Ann", "Ben"))
   * } yield ()
   * }}}
   *
   * @param tpl  First triple of values matching the 3 input attributes
   * @param tpls Optional more triples of values matching both input attributes
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)(implicit conn: Future[Conn]): Molecule


  /** Resolve input molecule by applying one or more triples of expressions, each matching the 3 input attributes
   *
   * {{{
   * // Input molecule awaiting 3 inputs for `profession`, `age` and `score`
   * val profAgeScore = m(Person.name.profession_(?).age_(?).score_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age.score insert List(
   *     ("Ann", "doctor", 37, 1.0),
   *     ("Ben", "teacher", 37, 1.0),
   *     ("Joe", "teacher", 32, 1.0),
   *     ("Liz", "teacher", 28, 2.0)
   *   )
   *
   *   // Apply two or more triple expressions, each matching all 3 input attributes
   *   // [profession/age/score-expression] or [profession/age/score-expression] or ...
   *   _ <- profAgeScore.apply(("doctor" and 37 and 1.0) or ("teacher" and 32 and 1.0)).get
   *     .map(_.sorted ==> List("Ann", "Joe"))
   * } yield ()
   * }}}
   *
   * @param or   Two or more tuple3 expressions separated by `or`
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(or: Or3[I1, I2, I3])(implicit conn: Future[Conn]): Molecule


  /** Resolve input molecule by applying Seq of value triples
   * {{{
   * // Input molecule awaiting 3 inputs for `profession`, `age` and `score`
   * val profAgeScore = m(Person.name.profession_(?).age_(?).score_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age.score insert List(
   *     ("Ann", "doctor", 37, 1.0),
   *     ("Ben", "teacher", 37, 1.0),
   *     ("Joe", "teacher", 32, 1.0),
   *     ("Liz", "teacher", 28, 2.0)
   *   )
   *
   *   // Apply Seq of one or more value triples, each matching all 3 input attributes
   *   _ <- profAgeScore.apply(Seq(("doctor", 37, 1.0))).get.map(_ ==> List("Ann"))
   *   _ <- profAgeScore.apply(Seq(("doctor", 37, 1.0), ("teacher", 37, 1.0))).get.map(_.sorted ==> List("Ann", "Ben"))
   * } yield ()
   * }}}
   *
   * @param ins  Seq of value triples, each matching the 3 input attributes
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(ins: Seq[(I1, I2, I3)])(implicit conn: Future[Conn]): Molecule


  /** Resolve input molecule by applying 3 groups of expressions, one for each of the 3 input attributes
   *
   * {{{
   * // Input molecule awaiting 3 inputs for `profession`, `age` and `score`
   * val profAgeScore = m(Person.name.profession_(?).age_(?).score_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age.score insert List(
   *     ("Ann", "doctor", 37, 1.0),
   *     ("Ben", "teacher", 37, 1.0),
   *     ("Joe", "teacher", 32, 1.0),
   *     ("Liz", "teacher", 28, 2.0)
   *   )
   *
   *   // Apply 3 expressions, one for each input attribute
   *   // [profession-expression] and [age-expression] and [score-expression]
   *   _ <- profAgeScore.apply("doctor" and 37 and 1.0).get.map(_ ==> List("Ann"))
   *   _ <- profAgeScore.apply(("doctor" or "teacher") and 37 and 1.0).get
   *     .map(_.sorted ==> List("Ann", "Ben"))
   *
   *   _ <- profAgeScore.apply(("doctor" or "teacher") and (37 or 32) and 1.0).get
   *     .map(_.sorted ==> List("Ann", "Ben", "Joe"))
   *
   *   _ <- profAgeScore.apply(("doctor" or "teacher") and (37 or 32) and (1.0 or 2.0)).get
   *     .map(_.sorted ==> List("Ann", "Ben", "Joe"))
   * } yield ()
   * }}}
   *
   * @param and  First input expr `and` second input expr `and` third input expr
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(and: And3[I1, I2, I3])(implicit conn: Future[Conn]): Molecule


  /** Resolve input molecule by applying 3 groups of values, one for each of the 3 input attributes
   *
   * {{{
   * // Input molecule awaiting 3 inputs for `profession`, `age` and `score`
   * val profAgeScore = m(Person.name.profession_(?).age_(?).score_(?))
   * for {
   *   // Sample data set
   *   Person.name.profession.age.score insert List(
   *     ("Ann", "doctor", 37, 1.0),
   *     ("Ben", "teacher", 37, 1.0),
   *     ("Joe", "teacher", 32, 1.0),
   *     ("Liz", "teacher", 28, 2.0)
   *   )
   *
   *   // Apply 3 Seq of values, each matching one of the input attributes
   *   _ <- proOfAge(Seq("doctor"), Seq(37)).get.map(_ ==> List("Ann"))
   *   _ <- proOfAge(Seq("doctor", "teacher"), Seq(37, 32)).get.map(_.sorted ==> List("Ann", "Ben", "Joe"))
   *
   *   // Number of arguments in each Seq don't have to match but can be asymmetric
   *   _ <- profAgeScore.apply(Seq("doctor"), Seq(37), Seq(1.0)).get.map(_ ==> List("Ann"))
   *   _ <- profAgeScore.apply(Seq("doctor", "teacher"), Seq(37), Seq(1.0)).get.map(_.sorted ==> List("Ann", "Ben"))
   * } yield ()
   * }}}
   *
   * @param in1  Seq of values matching first input attribute (professions: Seq[String])
   * @param in2  Seq of values matching second input attribute (ages: Seq[Int])
   * @param in3  Seq of values matching third input attribute (scores: Seq[Double])
   * @param conn Implicit [[molecule.datomic.base.facade.Conn Conn]] in scope
   * @return Resolved molecule that can be queried
   */
  def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule
}


/** Implementations of input molecules awaiting 3 inputs, output arity 1-22 */
object Molecule_3 {

  abstract class Molecule_3_01[Obj, I1, I2, I3, A](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_01[Obj, A] // generated by macro

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_01[Obj, A] // generated by macro
  }

  abstract class Molecule_3_02[Obj, I1, I2, I3, A, B](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B]
  }

  abstract class Molecule_3_03[Obj, I1, I2, I3, A, B, C](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C]
  }

  abstract class Molecule_3_04[Obj, I1, I2, I3, A, B, C, D](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D]
  }

  abstract class Molecule_3_05[Obj, I1, I2, I3, A, B, C, D, E](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E]
  }

  abstract class Molecule_3_06[Obj, I1, I2, I3, A, B, C, D, E, F](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F]
  }

  abstract class Molecule_3_07[Obj, I1, I2, I3, A, B, C, D, E, F, G](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G]
  }

  abstract class Molecule_3_08[Obj, I1, I2, I3, A, B, C, D, E, F, G, H](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H]
  }

  abstract class Molecule_3_09[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I]
  }

  abstract class Molecule_3_10[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J]
  }

  abstract class Molecule_3_11[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K]
  }

  abstract class Molecule_3_12[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L]
  }

  abstract class Molecule_3_13[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M]
  }

  abstract class Molecule_3_14[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  }

  abstract class Molecule_3_15[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  }

  abstract class Molecule_3_16[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  }

  abstract class Molecule_3_17[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  }

  abstract class Molecule_3_18[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  }

  abstract class Molecule_3_19[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  }

  abstract class Molecule_3_20[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  }

  abstract class Molecule_3_21[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  }

  abstract class Molecule_3_22[Obj, I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_3[Obj, I1, I2, I3](model, queryData) {
    def apply(i1: I1, i2: I2, i3: I3)                  (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(Right(Seq((i1, i2, i3))))
    def apply(tpl: (I1, I2, I3), tpls: (I1, I2, I3)*)  (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(Right(tpl +: tpls))
    def apply(or: Or3[I1, I2, I3])                     (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(resolveOr3(or))
    def apply(ins: Seq[(I1, I2, I3)])                  (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(Right(ins))
    protected def outMoleculeValues(args: Either[Throwable, Seq[(I1, I2, I3)]])(implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]

    def apply(and: And3[I1, I2, I3])                   (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeSeqs(resolveAnd3(and))
    def apply(in1: Seq[I1], in2: Seq[I2], in3: Seq[I3])(implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeSeqs(Right((in1, in2, in3)))
    protected def outMoleculeSeqs(args: Either[Throwable, (Seq[I1], Seq[I2], Seq[I3])])(implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]
  }
}
