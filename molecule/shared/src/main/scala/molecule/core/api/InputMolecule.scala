package molecule.core.api

import java.util.Date
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.util.fns
import molecule.datomic.base.ast.query._

/** Shared interface of all input molecules.
 * <br><br>
 * Input molecules are molecules that awaits one or more inputs at runtime. When input value is applied,
 * the input molecule is resolved and a standard molecule is returned that we can then call actions on.
 * <br><br>
 * Input molecule queries are cached by Datomic. So there is a runtime performance gain in using input molecules. Furthermore,
 * input molecules are a good fit for re-use for queries where only a few parameters change.
 * <br><br>
 * Input molecules can await 1, 2 or 3 inputs and are constructed by applying the `?` marker
 * to attributes. If one marker is applied, we get a [[molecule.core.api.Molecule_1 Molecule_1]], 2 inputs creates
 * an [[molecule.core.api.Molecule_2 Molecule_2]] and 3 an [[molecule.core.api.Molecule_3 Molecule_3]].
 * <br><br>
 * The three input molecule interfaces come in arity-versions corresponding to the number of non-?-marked attributes
 * in the input molecule. Let's see a simple example:
 * {{{
 * // Input molecule created at compile time. Awaits a name of type String
 * val ageOfPersons = m(Person.name_(?).age)
 * for {
 *   // Sample data
 *   _ <- Person.name.age insert List(
 *     ("Joe", 42),
 *     ("Liz", 34)
 *   )
 *
 *   // Resolved molecule. "Joe" input is matched against name attribute
 *   ageOfPersonsNamedJoe = ageOfPersons.apply("Joe")
 *
 *   // Calling action on resolved molecule.
 *   // (Only age is returned since name was marked as tacit with the underscore notation)
 *   _ <- ageOfPersonsNamedJoe.get.map(_ ==> List(42))
 *
 *   // Or we can re-use the input molecule straight away
 *   _ <- ageOfPersons("Liz").get.map(_ ==> List(34))
 * }
 * }}}
 */
private[molecule] abstract class InputMolecule(
  model: Model,
  queryData: (Query, String, Option[Throwable])
) extends Molecule {

  def _model: Model = model
  def _query: Query = queryData._1
  def _datalog: String = queryData._2
  def _inputThrowable: Option[Throwable] = queryData._3

  val isJsPlatform: Boolean

  protected def resolveOr[I1](or: Or[I1]): Either[Throwable, Seq[I1]] = {
    def traverse(or0: Or[I1]): Either[Throwable, Seq[I1]] = or0 match {
      case Or(TermValue(v1), TermValue(v2)) => Right(Seq(v1, v2))
      case Or(or1: Or[I1], TermValue(v2))   => traverse(or1).map(_ :+ v2)
      case Or(TermValue(v1), or2: Or[I1])   => traverse(or2).map(v1 +: _)
      case Or(or1: Or[I1], or2: Or[I1])     => traverse(or1).flatMap(a => traverse(or2).map(b => a ++ b))
      case _                                => Left(MoleculeException(s"Unexpected expression: " + or0, null))
    }
    traverse(or)
  }

  protected def varsAndPrefixes(query: Query): Seq[(Var, String)] = query.i.inputs.collect {
    case Placeholder(_, _, v, _, enumPrefix) => (v, enumPrefix.getOrElse(""))
  }

  protected def isTacit(nsFull: String, attr: String): Boolean = {
    val nsFull_                = nsFull + "_"
    val (attr_, attrK, attrK_) = (attr + "_", attr + "K", attr + "K_")
    def isTacit_(elements: Seq[Element], tacit0: Option[Boolean]): Option[Boolean] = elements.foldLeft(tacit0) {
      case (tacit, Generic(_, `attr`, `nsFull`, _, _))                                  => Some(true)
      case (tacit, Atom(`nsFull` | `nsFull_`, `attr_` | `attrK_`, _, _, _, _, _, _, _)) => Some(true)
      case (tacit, Atom(`nsFull_`, `attr` | `attrK`, _, _, _, _, _, _, _))              => Some(true)
      case (tacit, Atom(`nsFull`, `attr` | `attrK`, _, _, _, _, _, _, _))               => Some(false)
      case (tacit, Nested(_, elements2))                                                => isTacit_(elements2, tacit)
      case (tacit, Composite(elements2))                                                => isTacit_(elements2, tacit)
      case (tacit, TxMetaData(elements2))                                               => isTacit_(elements2, tacit)
      case (tacit, _)                                                                   => tacit
    }
    isTacit_(_model.elements, None) match {
      case Some(result) => result
      case None         => throw MoleculeException(s"Couldn't find atom of attribute `:$nsFull/$attr` in model:\n" + _model)
    }
  }

  protected def cardinality(nsFull: String, attr: String): Int = {
    val nsFull_                = nsFull + "_"
    val (attr_, attrK, attrK_) = (attr + "_", attr + "K", attr + "K_")
    def isTacit_(elements: Seq[Element], cardOpt0: Option[Int]): Option[Int] = elements.foldLeft(cardOpt0) {
      case (cardOpt, Generic(_, `attr`, `nsFull`, _, _))                                                        => Some(2)
      case (cardOpt, Atom(`nsFull` | `nsFull_`, `attr` | `attr_` | `attrK` | `attrK_`, _, card, _, _, _, _, _)) => Some(card)
      case (cardOpt, Nested(_, elements2))                                                                      => isTacit_(elements2, cardOpt)
      case (cardOpt, Composite(elements2))                                                                      => isTacit_(elements2, cardOpt)
      case (cardOpt, TxMetaData(elements2))                                                                     => isTacit_(elements2, cardOpt)
      case (cardOpt, e)                                                                                         => cardOpt
    }
    isTacit_(_model.elements, None) match {
      case Some(result) => result
      case None         => throw MoleculeException(s"Couldn't find atom of attribute `:$nsFull/$attr` in model:\n" + _model)
    }
  }

  protected def isExpression(nsFull: String, attr: String): Boolean = {
    val attr_ = attr + "_"
    def isExpression_(elements: Seq[Element], isExpression: Boolean): Boolean = elements.foldLeft(isExpression) {
      case (expr, Atom(`nsFull`, `attr` | `attr_`, _, _, Neq(_) | Lt(_) | Gt(_) | Le(_) | Ge(_) | Fulltext(_), _, _, _, _)) => true
      case (expr, Nested(_, elements2))                                                                                     => isExpression_(elements2, expr)
      case (expr, Composite(elements2))                                                                                     => isExpression_(elements2, expr)
      case (expr, TxMetaData(elements2))                                                                                    => isExpression_(elements2, expr)
      case (expr, _)                                                                                                        => expr
    }
    isExpression_(_model.elements, false)
  }

  protected def addNilClause(clauses: Seq[Clause], e: Var, kw: KW, v0: Var): Seq[Clause] = {
    val (found, _, newClauses) = clauses.foldLeft(false, "", Seq.empty[Clause]) {
      case ((found, v, acc), DataClause(_, `e`, `kw`, `v0`, _, _))   => (true, v, acc :+ NotClause(e, kw))
      case ((found, v, acc), DataClause(_, `e`, `kw`, Var(w), _, _)) => (true, w, acc :+ NotClause(e, kw))

      // Remove next function clauses related to main clause
      case ((found, v, acc), FunctClause(_, List(Var(w), _), ScalarBinding(Var(x)))) if w == v => (found, x, acc)
      case ((found, v, acc), FunctClause(_, List(Var(w), _), _)) if w == v                     => (found, w, acc)

      // Leave non-related clauses as is
      case ((found, v, acc), otherClause) => (found, v, acc :+ otherClause)
    }
    if (found) newClauses else {
      val KW(nsFull, attr, _) = kw
      throw MoleculeException(s"Couldn't find input attribute `:$nsFull/$attr` placeholder variable `$v0` to be null among clauses:\n" + clauses.mkString("\n"))
    }
  }

  protected def dataClause(e: String, kw: KW, enumPrefix: Option[String], arg: Any, i: Int): Seq[Clause] = arg match {
    case value: java.net.URI       =>
      val uriVar = Var(e + "_uri" + i)
      Seq(
        FunctClause(s"""ground (java.net.URI. "$value")""", Nil, ScalarBinding(uriVar)),
        DataClause(ImplDS, Var(e), kw, uriVar, Empty, NoBinding)
      )
    case _ if enumPrefix.isDefined => Seq(
      DataClause(ImplDS, Var(e), kw, KW(enumPrefix.get.init.tail, arg.toString), Empty, NoBinding)
    )
    case _                         => Seq(
      DataClause(ImplDS, Var(e), kw, Val(arg), Empty, NoBinding)
    )
  }

  protected def valueClauses[TT](e: String, kw: KW, enumPrefix: Option[String], tpe: String, args: TT): Seq[Clause] = args match {
    case set: Set[_] if set.isEmpty => Seq(NotClause(Var(e), kw))
    case set: Set[_]                =>
      if (isJsPlatform) {
        val patch = getWithJsDecimalPrefix(tpe, set.head)
        set.toSeq.zipWithIndex.flatMap {
          case (arg, i) => dataClause(e, kw, enumPrefix, patch(arg), i + 1)
        }
      } else {
        set.toSeq.zipWithIndex.flatMap {
          case (arg, i) => dataClause(e, kw, enumPrefix, arg, i + 1)
        }
      }
    case arg                        =>
      if (isJsPlatform) {
        val patch = getWithJsDecimalPrefix(tpe, arg)
        dataClause(e, kw, enumPrefix, patch(arg), 1)
      } else {
        dataClause(e, kw, enumPrefix, arg, 1)
      }
  }

  protected def deepNil(args: Seq[Any]) = args match {
    case Nil                                 => true
    case (seq: Seq[_]) :: Nil if seq.isEmpty => true
    case (set: Set[_]) :: Nil if set.isEmpty => true
    case _                                   => false
  }

  private def getAsString(tpe: String, value: Any): Any => String = value match {
    case _: String        => (v: Any) => v.toString
    case _: Int | _: Long => tpe match {
      case "Double"     => (v: Any) => s"$v.0"
      case "BigDecimal" => (v: Any) => s"$v.0"
      case _            => (v: Any) => v.toString
    }
    case _: Float         => tpe match {
      case "Double"     => (v: Any) => v.toString + (if (v.toString.contains(".")) "" else ".0")
      case "BigDecimal" => (v: Any) => v.toString + (if (v.toString.contains(".")) "M" else ".0")
      case _            => (v: Any) => v.toString
    }
    case _: Double        => tpe match {
      case "Double"     => (v: Any) => v.toString + (if (v.toString.contains(".")) "" else ".0")
      case "BigDecimal" => (v: Any) => v.toString + (if (v.toString.contains(".")) "" else ".0")
      case _            => (v: Any) => v.toString
    }
    case _: Date          => (v: Any) => fns.date2str(v.asInstanceOf[Date])
    case _: BigDecimal    => (v: Any) => v.toString + (if (v.toString.contains(".")) "" else ".0")
    case _                => (v: Any) => v.toString
  }

  private def getWithJsDecimalPrefix(tpe: String, value: Any): Any => Any = value match {
    case _: String        => (v: Any) => v
    case _: Int | _: Long => tpe match {
      case "Double"     => (v: Any) => "__n__" + v + ".0"
      case "BigDecimal" => (v: Any) => "__n__" + v + ".0M"
      case _            => (v: Any) => v
    }
    case _: Float         => tpe match {
      case "Double"     => (v: Any) => "__n__" + v + (if (v.toString.contains(".")) "" else ".0")
      case "BigDecimal" => (v: Any) => "__n__" + v + (if (v.toString.contains(".")) "M" else ".0M")
      case _            => (v: Any) => "__n__" + v
    }
    case _: Double        => tpe match {
      case "Double"     => (v: Any) => "__n__" + v + (if (v.toString.contains(".")) "" else ".0")
      case "BigDecimal" => (v: Any) => "__n__" + v + (if (v.toString.contains(".")) "M" else ".0M")
      case _            => (v: Any) => "__n__" + v
    }
    case _: BigDecimal    => (v: Any) => "__n__" + v + (if (v.toString.contains(".")) "M" else ".0M")
    case _                => (v: Any) => v
  }

  protected def resolveInput(
    query: Query,
    ph: Placeholder,
    inputs: Seq[Any],
    ruleName: String = "rule1",
    unifyRule: Boolean = false
  ): Query = {

    val Placeholder(e@Var(e_), kw@KW(nsFull, attr, _), v@Var(w), tpe, prefix) = ph

    val args: Seq[Any] = inputs.flatMap {
      case set: Set[_] if set.isEmpty => Nil
      case set: Set[_]                => if (isJsPlatform) {
        val patch = getWithJsDecimalPrefix(tpe, set.head)
        set.toSeq.map(patch)
      } else set.toSeq
      case arg                        => if (isJsPlatform) {
        val patch = getWithJsDecimalPrefix(tpe, arg)
        Seq(patch(arg))
      } else Seq(arg)
    }

    val argss: Seq[Seq[_]] = inputs.flatMap {
      case set: Set[_] if set.isEmpty => Nil
      case set: Set[_]                => if (isJsPlatform) {
        val prefix = getWithJsDecimalPrefix(tpe, set.head)
        Seq(set.toSeq.map(prefix))
      } else Seq(set.toSeq)
      case arg                        => if (isJsPlatform) {
        val prefix = getWithJsDecimalPrefix(tpe, arg)
        Seq(Seq(prefix(arg)))
      } else Seq(Seq(arg))
    }

    val argsJsString: Seq[Any] = inputs.flatMap {
      case set: Set[_] if set.isEmpty => Nil
      case set: Set[_]                => if (isJsPlatform) {
        val patch = getAsString(tpe, set.head)
        set.toSeq.map(patch)
      } else set.toSeq
      case arg                        => if (isJsPlatform) {
        val patch = getAsString(tpe, arg)
        Seq(patch(arg))
      } else Seq(arg)
    }

    val card = cardinality(nsFull, attr)

    if (card == 4) {
      // Mapped key attributes =========================================================================

      val argsJsString2 = if (isJsPlatform) argsJsString else argsJsString.map {
        case d: Date => fns.date2str(d) // compare standardized Date string format on jvm too
        case v       => v
      }

      // Compare Dates as standardized Strings
      val tpeDateStr = if (isJsPlatform && tpe == "Date") "String" else tpe
      if (inputs.size > 1) {
        query.copy(i = In(Seq(InVar(CollectionBinding(v), tpeDateStr, Seq(argsJsString2))), query.i.rules, query.i.ds))

      } else if (argss.nonEmpty && argss.head.size > 1) {
        val In(List(Placeholder(_, kw, v, _, _)), _, _) = query.i
        val (e, newClauses)                             = query.wh.clauses.foldLeft(null: QueryValue, Seq.empty[Clause]) {
          case ((_, acc), DataClause(_, e, _, `v`, _, _)) => (e, acc :+ RuleInvocation(ruleName, List(e)))
          case ((e, acc), other)                          => (e, acc :+ other)
        }
        val rules                                       = argss.map(value =>
          Rule(ruleName, List(e), List(DataClause(ImplDS, e, kw, Val(value), Empty, NoBinding)))
        )
        query.copy(i = In(Nil, rules, query.i.ds), wh = Where(newClauses))

      } else {
        query.copy(i = In(Seq(InVar(ScalarBinding(v), tpeDateStr, Seq(argsJsString2))), query.i.rules, query.i.ds))
      }

    } else {
      // Card-one/many attributes ======================================================================

      val v_ = w.filter(_.isLetter)
      def inGroup(v: String) = v.filter(_.isLetter) == v_ || v == v_ + "_casted"
      val tacit = isTacit(nsFull, attr)

      val (before, clauses, after) = query.wh.clauses.foldLeft(Seq.empty[Clause], Seq.empty[Clause], Seq.empty[Clause]) {
        case ((bef, cur, aft), cl@DataClause(_, `e`, `kw`, `v`, _, _))                  => (bef, cur :+ cl, aft)
        case ((bef, cur, aft), cl@DataClause(_, `e`, `kw`, Var(v), _, _)) if inGroup(v) => (bef, cur :+ cl, aft)
        case ((bef, cur, aft), cl@DataClause(_, Var(`v_`), _, _, _, _))             => (bef, cur :+ cl, aft)
        case ((bef, cur, aft), cl@FunctClause(_, List(_, `v`), _))                  => (bef, cur :+ cl, aft)
        case ((bef, cur, aft), cl@FunctClause(_, List(_, _, `v`), _))               => (bef, cur :+ cl, aft)
        case ((bef, cur, aft), cl@FunctClause(_, List(Var(v), _), _)) if inGroup(v) => (bef, cur :+ cl, aft)
        case ((bef, cur, aft), cl@FunctClause(_, List(Var(v)), _)) if inGroup(v)    => (bef, cur :+ cl, aft)
        case ((bef, Nil, aft), cl)                                                  => (bef :+ cl, Nil, aft)
        case ((bef, cur, aft), cl)                                                      => (bef, cur, aft :+ cl)
      }

      val nil = deepNil(args)
      val one = args.size == 1
      val uri = if (nil) false else tpe == "URI"

      val (newIns, newRules, newClauses): (Seq[Input], Seq[Rule], Seq[Clause]) = card match {

        // Applying entity ids to Namespace: `m(Ns(?).int).apply(42L)`
        case 2 if attr == "e_" => (Seq(InVar(CollectionBinding(v), tpe, Seq(argsJsString))), Nil, clauses)


        // Card-many enum attribute ...................................................................

        case 2 if prefix.isDefined => clauses match {

          // Neq(Seq(Qm))
          case Seq(enum, _, _, _, FunctClause("!=", _, _)) if nil && tacit  => (Nil, Nil, Seq(enum))
          case Seq(enum, ident, getName, _, FunctClause("!=", _, _)) if nil => (Nil, Nil, Seq(enum, ident, getName))
          case Seq(enum, ident, getName, _, FunctClause("!=", _, _))        => (Nil, Nil,
            Seq(enum, ident, getName) ++ argss.map(args =>
              NotClauses(
                args.map(arg => DataClause(ImplDS, e, kw, Val("__enum__" + prefix.get + arg), Empty, NoBinding))
              )
            )
          )

          // Gt(Qm), Ge(Qm), Lt(Qm), Le(Qm)
          case Seq(enum, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if nil && tacit  => (Nil, Nil, Seq(enum))
          case Seq(enum, ident, getName, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if nil => (Nil, Nil, Seq(enum, ident, getName))
          case cls@Seq(_, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if one          => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, cls)
          case Seq(_, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _))                     => throw MoleculeException("Can't apply multiple values to comparison function.")

          // Qm
          case _ if nil && tacit    => (Nil, Nil, Seq(FunctClause("missing?", Seq(DS(), e, kw), NoBinding)))
          case cls if nil           => (Seq(InVar(CollectionBinding(v), tpe, Seq(Nil))), Nil, cls)
          case Seq(enum, ident, fn) => (
            Nil,
            argss.map(args =>
              Rule(ruleName, Seq(e), args.map(arg =>
                DataClause(ImplDS, e, kw, Val("__enum__" + prefix.get + arg), Empty, NoBinding)
              ))),
            if (tacit) Seq(enum, RuleInvocation(ruleName, Seq(e))) else Seq(enum, ident, fn, RuleInvocation(ruleName, Seq(e)))
          )
        }


        // Card-many attribute ...................................................................

        case 2 => clauses match {

          // BigInt has extra cast function
          case Seq(_, FunctClause("biginteger", _, _), _, _, _) => {
            clauses match {
              // Neq(Seq(Qm))
              case Seq(dc, _, _, _, FunctClause("!=", _, _)) if nil => (Nil, Nil, Seq(dc))
              case Seq(dc, _, _, _, FunctClause("!=", _, _))        =>
                (Nil, Nil,
                  dc +: argss.map(args =>
                    NotClauses(args.map(arg =>
                      DataClause(ImplDS, e, kw, Val(arg), Empty, NoBinding))
                    )
                  )
                )

              // Gt(Qm), Ge(Qm), Lt(Qm), Le(Qm)
              case Seq(dc, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if nil    => (Nil, Nil, Seq(dc))
              case cls@Seq(_, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if one => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, cls)
              case Seq(_, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _))            => throw MoleculeException("Can't apply multiple values to comparison function.")
            }
          }

          // Neq(Seq(Qm))
          case Seq(dc, _, FunctClause("!=", _, _)) if nil => (Nil, Nil, Seq(dc))

          case Seq(dc, _, FunctClause("!=", _, _)) if uri => (Nil, Nil,
            dc +: argss.map { args =>
              val clauses = args.zipWithIndex.flatMap { case (arg, i) =>
                val uriVar = Var(w + "_uri" + (i + 1))
                Seq(
                  DataClause(ImplDS, e, kw, uriVar, Empty, NoBinding),
                  FunctClause(s"""ground (java.net.URI. "$arg")""", Seq(Empty), ScalarBinding(uriVar))
                )
              }
              NotJoinClauses(Seq(e), clauses)
            }
          )

          case Seq(dc, _, FunctClause("!=", _, _)) =>
            (Nil, Nil,
              dc +: argss.map(args =>
                NotClauses(args.map(arg =>
                  DataClause(ImplDS, e, kw, Val(arg), Empty, NoBinding))
                )
              )
            )

          // Gt(Qm), Ge(Qm), Lt(Qm), Le(Qm)
          case Seq(dc, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if nil    => (Nil, Nil, Seq(dc))
          case cls@Seq(_, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if one => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, cls)
          case Seq(_, _, FunctClause(">" | ">=" | "<" | "<=", _, _))            => throw MoleculeException("Can't apply multiple values to comparison function.")

          // Fulltext(Seq(Qm))
          case Seq(f@FunctClause("fulltext", _, _)) if nil => (Seq(InVar(CollectionBinding(v), tpe, Seq(Nil))), Nil, Seq(f))
          case Seq(f@FunctClause("fulltext", _, _))        => (
            Nil,
            argss.map(args =>
              Rule(ruleName, Seq(e), args.zipWithIndex.map { case (arg, i) =>
                FunctClause("fulltext", Seq(DS(), kw, Val(arg)), RelationBinding(List(e, Var(w + "_" + (i + 1)))))
              })),
            Seq(
              DataClause(ImplDS, e, kw, Var(v_), Empty, NoBinding),
              RuleInvocation(ruleName, Seq(e))
            )
          )

          // Qm
          case Seq(dc: DataClause) if nil && tacit => (Nil, Nil, Seq(FunctClause("missing?", Seq(DS(), e, kw), NoBinding)))
          case Seq(dc: DataClause) if nil          => (Seq(InVar(CollectionBinding(v), tpe, Seq(Nil))), Nil, Seq(dc))
          case Seq(dc: DataClause) if uri          => (
            Nil,
            argss.map(args =>
              Rule(ruleName, Seq(e), args.zipWithIndex.flatMap { case (arg, i) =>
                val uriVar = Var(w + "_uri" + (i + 1))
                Seq(
                  FunctClause(s"""ground (java.net.URI. "$arg")""", Seq(Empty), ScalarBinding(uriVar)),
                  DataClause(ImplDS, e, kw, uriVar, dc.tx, dc.op)
                )
              })),
            Seq(dc, RuleInvocation(ruleName, Seq(e)))
          )
          case Seq(dc: DataClause)                 =>
            (
              Nil,
              argss.map(args =>
                Rule(ruleName, Seq(e), args.map(arg =>
                  DataClause(ImplDS, e, kw, Val(arg), dc.tx, dc.op)))),
              Seq(dc, RuleInvocation(ruleName, Seq(e)))
            )
        }


        // Card-one enum attribute ...................................................................

        case 1 if prefix.isDefined => clauses match {

          // Neq(Seq(Qm))
          case Seq(enum, ident, getName, _, FunctClause("!=", _, _)) if nil && tacit                        => (Nil, Nil, Seq(enum))
          case Seq(enum, ident, getName, _, FunctClause("!=", _, _)) if nil                                 => (Nil, Nil, Seq(enum, ident, getName))
          case cls@Seq(_, _, _, _, FunctClause("!=", _, _)) if one                                          => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, cls)
          case Seq(enum, ident, getName, FunctClause(n, Seq(Var(v2), `v`), _), not@FunctClause("!=", _, _)) => (Nil, Nil,
            Seq(enum, ident, getName) ++ args.zipWithIndex.flatMap { case (arg, i) =>
              Seq(
                FunctClause(n, Seq(Var(v2), Val(arg)), ScalarBinding(Var(v2 + "_" + (i + 1)))),
                FunctClause("!=", Seq(Var(v2 + "_" + (i + 1)), Val(0)), NoBinding)
              )
            })

          // Gt(Qm), Ge(Qm), Lt(Qm), Le(Qm)
          case Seq(enum, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if nil && tacit  => (Nil, Nil, Seq(enum))
          case Seq(enum, ident, getName, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if nil => (Nil, Nil, Seq(enum, ident, getName))
          case cls@Seq(_, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if one          => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, cls)
          case Seq(_, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _))                     => throw MoleculeException("Can't apply multiple values to comparison function.")

          // Fulltext(Seq(Qm))
          case Seq(f@FunctClause("fulltext", _, _)) if nil => (Seq(InVar(CollectionBinding(v), tpe, Seq(Nil))), Nil, Seq(f))
          case Seq(f@FunctClause("fulltext", _, _)) if one => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, Seq(f))
          case Seq(f@FunctClause("fulltext", _, _))        => (Seq(InVar(CollectionBinding(v), tpe, Seq(argsJsString))), Nil, Seq(f))

          // Qm
          case cls if nil && tacit             => (Nil, Nil, Seq(FunctClause("missing?", Seq(DS(), e, kw), NoBinding)))
          case cls if nil                      => (Seq(InVar(CollectionBinding(v), tpe, Seq(Nil))), Nil, cls)
          case Seq(enum, _, _) if one && tacit => (Seq(InVar(ScalarBinding(Var(v_)), tpe, Seq(Seq("__enum__" + prefix.get + args.head)))), Nil, Seq(enum))
          case cls if one                      => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, cls)
          case Seq(enum, _, _) if tacit        => (Seq(InVar(CollectionBinding(Var(v_)), tpe, Seq(args.map(arg => "__enum__" + prefix.get + arg)))), Nil, Seq(enum))
          case cls                             => (Seq(InVar(CollectionBinding(v), tpe, Seq(argsJsString))), Nil, cls)
        }


        // Card-one attribute ...................................................................

        case 1 => clauses match {

          case Nil if nsFull == "ns" && attr == "?" => (Nil, Nil, Seq(FunctClause("=", Seq(v, Val(args.head)), NoBinding)))

          // BigInt has extra cast function
          case Seq(_, FunctClause("biginteger", _, _), _, _, _) => {
            clauses match {
              // Neq(Seq(Qm)) BigInt
              case Seq(dc, _, _, _, FunctClause("!=", _, _)) if nil =>
                (Nil, Nil, Seq(dc))

              case cls@Seq(_, _, _, _, FunctClause("!=", _, _)) if one =>
                (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, cls)

              case Seq(dc,
              FunctClause("biginteger", Seq(Var(v0)), ScalarBinding(Var(v0_casted))),
              FunctClause("biginteger", Seq(Var(v1)), ScalarBinding(Var(v1_casted))),
              FunctClause(fn, _, ScalarBinding(Var(v2))),
              FunctClause("!=", _, _)
              ) =>
                val bigIntNegations = args.zipWithIndex.flatMap { case (arg, i) =>
                  val suffix      = "_" + (i + 1)
                  val inputCasted = if (i == 0)
                    Seq(FunctClause("biginteger", Seq(Var(v0)), ScalarBinding(Var(v0_casted)))) else Nil

                  inputCasted ++ Seq(
                    FunctClause(s"biginteger $arg", Seq(), ScalarBinding(Var(v1_casted + suffix))),
                    FunctClause(fn, Seq(Var(v0_casted), Var(v1_casted + suffix)), ScalarBinding(Var(v2 + suffix))),
                    FunctClause("!=", Seq(Var(v2 + suffix), Val(0)), NoBinding)
                  )
                }
                (Nil, Nil, dc +: bigIntNegations)

              // Gt(Qm), Ge(Qm), Lt(Qm), Le(Qm) BigInt
              case Seq(dc, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if nil    => (Nil, Nil, Seq(dc))
              case cls@Seq(_, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if one => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, cls)
              case Seq(_, _, _, _, FunctClause(">" | ">=" | "<" | "<=", _, _))            => throw MoleculeException("Can't apply multiple values to comparison function.")
            }
          }

          // Neq(Seq(Qm))

          case Seq(dc, _, FunctClause("!=", _, _)) if nil => (Nil, Nil, Seq(dc))

          case cls@Seq(_, _, FunctClause("!=", _, _)) if one => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, cls)

          case Seq(dc, FunctClause(fn, Seq(v1, `v`), _), FunctClause("!=", _, _)) if uri =>
            (Nil, Nil,
              dc +: args.zipWithIndex.flatMap { case (arg, i) =>
                val (x, y) = (Var(v_ + "_" + (i + 1) + "a"), Var(v_ + "_" + (i + 1) + "b"))
                Seq(
                  FunctClause(s"""ground (java.net.URI. "$arg")""", Seq(Empty), ScalarBinding(x)),
                  FunctClause(fn, Seq(v1, x), ScalarBinding(y)),
                  FunctClause("!=", Seq(y, Val(0)), NoBinding)
                )
              }
            )

          case Seq(dc, FunctClause(fn, Seq(v1, `v`), ScalarBinding(Var(v2))), FunctClause("!=", _, _)) =>
            (Nil, Nil,
              dc +: args.zipWithIndex.flatMap {
                case (arg, i) =>
                  val vx = Var(v2 + "_" + (i + 1))
                  Seq(
                    FunctClause(fn, Seq(v1, Val(arg)), ScalarBinding(vx)),
                    FunctClause("!=", Seq(vx, Val(0)), NoBinding)
                  )
              }
            )

          // Gt(Qm), Ge(Qm), Lt(Qm), Le(Qm)
          case Seq(dc, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if nil    => (Nil, Nil, Seq(dc))
          case cls@Seq(_, _, FunctClause(">" | ">=" | "<" | "<=", _, _)) if one => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, cls)
          case Seq(_, _, FunctClause(">" | ">=" | "<" | "<=", _, _))            => throw MoleculeException("Can't apply multiple values to comparison function.")

          // Fulltext(Seq(Qm))
          case Seq(f@FunctClause("fulltext", _, _)) if nil => (Seq(InVar(CollectionBinding(v), tpe, Seq(Nil))), Nil, Seq(f))
          case Seq(f@FunctClause("fulltext", _, _)) if one => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, Seq(f))
          case Seq(f@FunctClause("fulltext", _, _))        => (Seq(InVar(CollectionBinding(v), tpe, Seq(argsJsString))), Nil, Seq(f))

          // Qm
          case _ if nil && tacit          => (Nil, Nil, Seq(FunctClause("missing?", Seq(DS(), e, kw), NoBinding)))
          case Seq(dc: DataClause) if nil => (Seq(InVar(CollectionBinding(v), tpe, Seq(Nil))), Nil, Seq(dc))
          case Seq(dc: DataClause) if one => (Seq(InVar(ScalarBinding(v), tpe, Seq(argsJsString))), Nil, Seq(dc))
          case Seq(dc: DataClause)        => (Seq(InVar(CollectionBinding(v), tpe, Seq(argsJsString))), Nil, Seq(dc))
        }
      }

      val (newRules2, newClauses2): (Seq[Rule], Seq[Clause]) = if (unifyRule && newRules.size == 1 && argss.size == 1) {
        val curRules    = query.i.rules.collect {
          case r@Rule(`ruleName`, _, _) => r
        }
        val newClauses0 = before ++ newClauses ++ after
        curRules.size match {
          case 0 => (newRules, newClauses0)
          case 1 =>
            // Collect rule clauses to be unified
            val (ruleVars, unifiedRules) = query.i.rules.foldLeft(Seq.empty[QueryValue], Seq.empty[Rule]) {
              case ((_, rules), Rule(`ruleName`, vars, cls)) =>
                (
                  (vars ++ newRules.head.vars).distinct,
                  rules :+ Rule(ruleName, (vars ++ newRules.head.vars).distinct, cls ++ newRules.head.clauses)
                )
              case ((vs, rules), other)                      => (vs, rules)
            }
            // Unify rule invocations
            val newClauses1: Seq[Clause] = newClauses0.foldRight(0, Seq.empty[Clause]) {
              case (RuleInvocation(`ruleName`, vars), (0, cls)) => (1, RuleInvocation(ruleName, (vars ++ ruleVars).distinct) +: cls)
              case (RuleInvocation(`ruleName`, _), (1, cls))    => (1, cls)
              case (cl, (done, cls))                            => (done, cl +: cls)
            }._2
            (unifiedRules, newClauses1)

          case unexpected => throw MoleculeException(s"Didn't expect $unexpected rules to be unified in query:\n" + query)
        }
      } else {
        (query.i.rules ++ newRules, before ++ newClauses ++ after)
      }

      query.copy(i = In(query.i.inputs ++ newIns, newRules2, query.i.ds), wh = Where(newClauses2))
    }
  }
}
