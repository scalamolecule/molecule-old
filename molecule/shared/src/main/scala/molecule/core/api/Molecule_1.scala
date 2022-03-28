package molecule.core.api

import java.util.Date
import molecule.core.api.Molecule_0._
import molecule.core.api.exception.Molecule_1_Exception
import molecule.core.ast.elements._
import molecule.core.util.fns
import molecule.datomic.base.ast.query._
import molecule.datomic.base.facade.Conn
import scala.concurrent.Future
import scala.util.control.NonFatal


/** Shared interfaces of input molecules awaiting 1 input.
 * {{{
 * // Input molecule awaiting 1 input for `name`
 * val ageOfPersons = m(Person.name_(?).age)
 * for {
 *   // Sample data set
 *   _ <- Person.name.age insert List(
 *     ("Joe", 42),
 *     ("Liz", 34)
 *   )
 *
 *   // Resolve input molecule with name input in various ways
 *   _ <- ageOfPersons("Joe").get.map(_ ==> List(42))
 *   _ <- ageOfPersons("Joe", "Liz").get.map(_ ==> List(42, 34))
 *   _ <- ageOfPersons("Joe" or "Liz").get.map(_ ==> List(42, 34))
 *   _ <- ageOfPersons(Seq("Joe", "Liz")).get.map(_ ==> List(42, 34))
 * } yield ()
 * }}}
 *
 * @tparam I1 Type of input matching attribute with `?` marker
 */
abstract class Molecule_1[Obj, I1](
  model: Model,
  queryData: (Query, String, Option[Throwable])
) extends InputMolecule(model, queryData) {


  protected def bindValues(query: Query, inputs0: Seq[I1]): Either[Throwable, Query] = try {
    val inputs = inputs0.distinct
    val q2     = query.i.inputs.size match {
      case 2 => // Mapped attributes
        val inVars                                         = query.i.inputs.collect { case Placeholder(_, _, v, _, _) => v }
        val Placeholder(_, KW(nsFull, attr, _), _, tpe, _) = query.i.inputs.head

        val values = inputs.flatMap {
          case map: Map[_, _] =>
            map.head._2 match {
              case _: Date => map.toSeq.map {
                case (k, v: Date) => Seq(k, fns.date2str(v)) // compare standardized date format
                case (k, v)       => Seq(k, v)
              }
              case _       => map.toSeq.map {
                case (k, v) => Seq(k, v)
              }
            }
          case other          => throw Molecule_1_Exception(s"Unexpected input for mapped attribute `:$nsFull/$attr`: " + other)
        }

        val tpeDateStr = if (isJsPlatform && tpe == "Date") "String" else tpe
        query.copy(i = In(Seq(InVar(RelationBinding(inVars), tpeDateStr, values)), query.i.rules, query.i.ds))

      case 1 => // Card-one/many/mapK
        val List(ph: Placeholder) = query.i.inputs
        // Remove placeholder
        val q1                    = query.copy(i = In(Nil, query.i.rules, query.i.ds))
        resolveInput(q1, ph, inputs)
    }
    Right(q2)
  } catch {
    case NonFatal(exc) => Left(exc)
  }


  /** Apply one or more input values to resolve input molecule.
   * {{{
   * // Input molecule awaiting name input
   * val ageOfPersons = Person.name_(?).age
   * for {
   *   // Apply single value
   *   _ <- ageOfPersons("Joe").get.map(_ ==> List(42))
   * } yield ()
   * }}}
   * Querying the resolved molecule will match all entities having `name` set to the value(s) applied.
   *
   * @note Only distinct values are matched.
   * @return Resolved molecule that can be queried
   */
  def apply(arg: I1)(implicit conn: Future[Conn]): Molecule


  /** Apply one or more input values to resolve input molecule.
   * {{{
   * // Input molecule awaiting name input
   * val ageOfPersons = Person.name_(?).age
   * for {
   *   // Apply two or more values as vararg
   *   _ <- ageOfPersons("Joe", "Liz").get.map(_ ==> List(42, 34))
   * } yield ()
   * }}}
   * Querying the resolved molecule will match all entities having `name` set to the value(s) applied.
   *
   * @note Only distinct values are matched.
   * @return Resolved molecule that can be queried
   */
  def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule


  /** Apply OR expression of input values to resolve input molecule.
   * <br><br>
   * Input value type matches attribute having `?` marker.
   * {{{
   * // Input molecule awaiting name input
   * val ageOfPersons = Person.name_(?).age
   * for {
   *   // Apply OR expression of two or more input values
   *   _ <- ageOfPersons("Joe" or "Liz").get.map(_ ==> List(42, 34))
   * } yield ()
   * }}}
   * Querying the resolved molecule will match all entities having `name` set to the values applied.
   *
   * @note Only distinct values are matched.
   * @return Resolved molecule that can be queried
   */
  def apply(or: Or[I1])(implicit conn: Future[Conn]): Molecule


  /** Apply Seq of input values with OR semantics to resolve input molecule.
   * <br><br>
   * Resolve input molecule by applying a Set of values that the attribute is expected to have (OR semantics).
   * {{{
   * // Input molecule awaiting name input
   * val ageOfPersons = Person.name_(?).age
   * for {
   *   // Apply Seq of one or more input value(s)
   *   _ <- ageOfPersons(Seq("Joe", "Liz")).get.map(_ ==> List(42, 34))
   * } yield ()
   * }}}
   * Querying the resolved molecule will match all entities having `name` set to the value(s) applied.
   *
   * @note Only distinct values are matched.
   * @return Resolved molecule that can be queried
   */
  def apply(args: Seq[I1])(implicit conn: Future[Conn]): Molecule
}


/** Implementations of input molecules awaiting 1 input, output arity 1-22 */
object Molecule_1 {

  abstract class Molecule_1_01[Obj, I1, A](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(resolveOr(or)) // can be a Left(exception)
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_01[Obj, A] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_01[Obj, A] // generated by macro
  }

  abstract class Molecule_1_02[Obj, I1, A, B](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_02[Obj, A, B]
  }

  abstract class Molecule_1_03[Obj, I1, A, B, C](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_03[Obj, A, B, C]
  }

  abstract class Molecule_1_04[Obj, I1, A, B, C, D](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_04[Obj, A, B, C, D]
  }

  abstract class Molecule_1_05[Obj, I1, A, B, C, D, E](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_05[Obj, A, B, C, D, E]
  }

  abstract class Molecule_1_06[Obj, I1, A, B, C, D, E, F](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_06[Obj, A, B, C, D, E, F]
  }

  abstract class Molecule_1_07[Obj, I1, A, B, C, D, E, F, G](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_07[Obj, A, B, C, D, E, F, G]
  }

  abstract class Molecule_1_08[Obj, I1, A, B, C, D, E, F, G, H](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_08[Obj, A, B, C, D, E, F, G, H]
  }

  abstract class Molecule_1_09[Obj, I1, A, B, C, D, E, F, G, H, I](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_09[Obj, A, B, C, D, E, F, G, H, I]
  }

  abstract class Molecule_1_10[Obj, I1, A, B, C, D, E, F, G, H, I, J](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_10[Obj, A, B, C, D, E, F, G, H, I, J]
  }

  abstract class Molecule_1_11[Obj, I1, A, B, C, D, E, F, G, H, I, J, K](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_11[Obj, A, B, C, D, E, F, G, H, I, J, K]
  }

  abstract class Molecule_1_12[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_12[Obj, A, B, C, D, E, F, G, H, I, J, K, L]
  }

  abstract class Molecule_1_13[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L, M](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_13[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M]
  }

  abstract class Molecule_1_14[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_14[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  }

  abstract class Molecule_1_15[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_15[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  }

  abstract class Molecule_1_16[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_16[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  }

  abstract class Molecule_1_17[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_17[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  }

  abstract class Molecule_1_18[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_18[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  }

  abstract class Molecule_1_19[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_19[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  }

  abstract class Molecule_1_20[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_20[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  }

  abstract class Molecule_1_21[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_21[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  }

  abstract class Molecule_1_22[Obj, I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](model: Model, queryData: (Query, String, Option[Throwable])) extends Molecule_1[Obj, I1](model, queryData) {
    def apply(arg: I1)                         (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(Right(List(arg)))
    def apply(arg: I1, arg2: I1, moreArgs: I1*)(implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(Right(arg +: arg2 +: moreArgs))
    def apply(or: Or[I1])                      (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(resolveOr(or))
    def apply(args: Seq[I1])                   (implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = outMoleculeValues(Right(args))
    protected def outMoleculeValues(args: Either[Throwable, Seq[I1]])(implicit conn: Future[Conn]): Molecule_0_22[Obj, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]
  }
}
