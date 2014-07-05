/*
 * AUTO-GENERATED CODE - DO NOT CHANGE!
 *
 * Manual changes to this file will likely break molecules!
 * Instead, change the molecule definition files and recompile your project with `sbt compile`.
 */
package molecule.examples.seattle.dsl
import molecule._
import ast.schemaDSL._
import ast.model._

object Neighborhood extends Neighborhood_Out_0 {
  class name[NS, NS2](ns: NS, ns2: NS2) extends OneString(ns, ns2) with FulltextSearch with UniqueIdentity {
    def apply(data: oldNew[String]) = Neighborhood_Update()
  }

  def insert = Neighborhood_Insert()
}

case class Neighborhood_Entity(elements: Seq[Element] = Seq()) extends Entity(elements) {
  lazy val eid  = _get(1, "eid" , "Long"  )
  lazy val name = _get(1, "name", "String")

  def District = District_Entity(elements)

  private def _get(card: Int, attr: String, tpe: String) =
    Neighborhood_Entity(elements :+ Atom("neighborhood", attr, tpe, card, NoValue))
}

case class Neighborhood_Insert(override val elements: Seq[Element] = Seq()) extends Insert(elements) {
  lazy val eid  = (data: Long  ) => _insert(Seq(data), 1, "eid" , "Long"  )
  lazy val name = (data: String) => _insert(Seq(data), 1, "name", "String")

  def District = District_Insert(elements)

  private def _insert(data: Seq[Any], card: Int, attr: String, tpe: String, enumPrefix: Option[String] = None) =
    Neighborhood_Insert(elements :+ Atom("neighborhood", attr, tpe, card, Eq(data.map(_.toString)), enumPrefix))
}

case class Neighborhood_Update(override val elements: Seq[Element] = Seq(), override val ids: Seq[Long] = Seq()) extends Update(elements, ids) {
  lazy val eid  = eid_
  lazy val name = name_

  private[molecule] object eid_ {
    def apply(data: Long)   = _assertNewFact(Seq(data), 1, "eid", "Long")
    def apply()             = _retract(                 1, "eid")
  }
  private[molecule] object name_ {
    def apply(data: String) = _assertNewFact(Seq(data), 1, "name", "String")
    def apply()             = _retract(                 1, "name")
  }

  def District = District_Update(elements)

  private def _assertNewFact(data: Seq[Any], card: Int, attr: String, tpe: String, enumPrefix: Option[String] = None) =
    Neighborhood_Update(elements :+ Atom("neighborhood", attr, tpe, card, Eq(data.map(_.toString)), enumPrefix), ids)

  private def _swap(oldNew: Seq[(String, String)], attr: String, tpe: String, enumPrefix: Option[String] = None) =
    Neighborhood_Update(elements :+ Atom("neighborhood", attr, tpe, 2, Replace(oldNew.toMap), enumPrefix), ids)

  private def _removeElements(values: Seq[String], attr: String, tpe: String, enumPrefix: Option[String] = None) =
    Neighborhood_Update(elements :+ Atom("neighborhood", attr, tpe, 2, Remove(values), enumPrefix), ids)

  private def _retract(card: Int, attr: String) =
    Neighborhood_Update(elements :+ Atom("neighborhood", attr, "", card, Remove(Seq())), ids)
}

case class Neighborhood_Retract(elements: Seq[Element] = Seq()) extends Retract(elements) {
  lazy val eid  = _retract(1, "eid" , "Long"  )
  lazy val name = _retract(1, "name", "String")

  def District = District_Retract(elements)

  private def _retract(card: Int, attr: String, tpe: String, data: Seq[Any] = Seq()) =
    Neighborhood_Retract(elements :+ Atom("neighborhood", attr, tpe, card, Eq(data.map(_.toString))))
}

//********* Output molecules *******************************/

 trait Neighborhood_Out_0 extends Out_0 {
  import Neighborhood._
  
  @default
  def apply(name: String) = new name(this, new Neighborhood_Out_1[String] {}) with Neighborhood_Out_1[String]
  def apply(id: Long) = Neighborhood_Entity()

  def update(id: Long) = Neighborhood_Update(Seq(), Seq(id))
  def update(ids: Seq[Long]) = Neighborhood_Update(Seq(), ids)

  lazy val eid  = new eid  (this, new Neighborhood_Out_1[Long]   {}) with Neighborhood_Out_1[Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_1[String] {}) with Neighborhood_Out_1[String] {}
  def District = new OneRef with District_Out_0
}

trait Neighborhood_Out_1[A] extends Out_1[A] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_2[A, Long]   {}) with Neighborhood_Out_2[A, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_2[A, String] {}) with Neighborhood_Out_2[A, String] {}
  def District = new OneRef with District_Out_1[A]
  def apply(in: ?.type)    = new Neighborhood_In_1_0[A] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_1[A, A] {}
  def <(in: ?.type)        = new Neighborhood_In_1_1[A, A] {}
  def contains(in: ?.type) = new Neighborhood_In_1_1[A, A] {}
  def apply(m: maybe.type) = new Neighborhood_Out_1[A] {}
}

trait Neighborhood_Out_2[A, B] extends Out_2[A, B] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_3[A, B, Long]   {}) with Neighborhood_Out_3[A, B, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_3[A, B, String] {}) with Neighborhood_Out_3[A, B, String] {}
  def District = new OneRef with District_Out_2[A, B]
  def apply(in: ?.type)    = new Neighborhood_In_1_1[B, A] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_2[B, A, B] {}
  def <(in: ?.type)        = new Neighborhood_In_1_2[B, A, B] {}
  def contains(in: ?.type) = new Neighborhood_In_1_2[B, A, B] {}
  def apply(m: maybe.type) = new Neighborhood_Out_2[A, B] {}
}

trait Neighborhood_Out_3[A, B, C] extends Out_3[A, B, C] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_4[A, B, C, Long]   {}) with Neighborhood_Out_4[A, B, C, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_4[A, B, C, String] {}) with Neighborhood_Out_4[A, B, C, String] {}
  def District = new OneRef with District_Out_3[A, B, C]
  def apply(in: ?.type)    = new Neighborhood_In_1_2[C, A, B] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_3[C, A, B, C] {}
  def <(in: ?.type)        = new Neighborhood_In_1_3[C, A, B, C] {}
  def contains(in: ?.type) = new Neighborhood_In_1_3[C, A, B, C] {}
  def apply(m: maybe.type) = new Neighborhood_Out_3[A, B, C] {}
}

trait Neighborhood_Out_4[A, B, C, D] extends Out_4[A, B, C, D] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_5[A, B, C, D, Long]   {}) with Neighborhood_Out_5[A, B, C, D, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_5[A, B, C, D, String] {}) with Neighborhood_Out_5[A, B, C, D, String] {}
  def District = new OneRef with District_Out_4[A, B, C, D]
  def apply(in: ?.type)    = new Neighborhood_In_1_3[D, A, B, C] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_4[D, A, B, C, D] {}
  def <(in: ?.type)        = new Neighborhood_In_1_4[D, A, B, C, D] {}
  def contains(in: ?.type) = new Neighborhood_In_1_4[D, A, B, C, D] {}
  def apply(m: maybe.type) = new Neighborhood_Out_4[A, B, C, D] {}
}

trait Neighborhood_Out_5[A, B, C, D, E] extends Out_5[A, B, C, D, E] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_6[A, B, C, D, E, Long]   {}) with Neighborhood_Out_6[A, B, C, D, E, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_6[A, B, C, D, E, String] {}) with Neighborhood_Out_6[A, B, C, D, E, String] {}
  def District = new OneRef with District_Out_5[A, B, C, D, E]
  def apply(in: ?.type)    = new Neighborhood_In_1_4[E, A, B, C, D] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_5[E, A, B, C, D, E] {}
  def <(in: ?.type)        = new Neighborhood_In_1_5[E, A, B, C, D, E] {}
  def contains(in: ?.type) = new Neighborhood_In_1_5[E, A, B, C, D, E] {}
  def apply(m: maybe.type) = new Neighborhood_Out_5[A, B, C, D, E] {}
}

trait Neighborhood_Out_6[A, B, C, D, E, F] extends Out_6[A, B, C, D, E, F] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_7[A, B, C, D, E, F, Long]   {}) with Neighborhood_Out_7[A, B, C, D, E, F, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_7[A, B, C, D, E, F, String] {}) with Neighborhood_Out_7[A, B, C, D, E, F, String] {}
  def District = new OneRef with District_Out_6[A, B, C, D, E, F]
  def apply(in: ?.type)    = new Neighborhood_In_1_5[F, A, B, C, D, E] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_6[F, A, B, C, D, E, F] {}
  def <(in: ?.type)        = new Neighborhood_In_1_6[F, A, B, C, D, E, F] {}
  def contains(in: ?.type) = new Neighborhood_In_1_6[F, A, B, C, D, E, F] {}
  def apply(m: maybe.type) = new Neighborhood_Out_6[A, B, C, D, E, F] {}
}

trait Neighborhood_Out_7[A, B, C, D, E, F, G] extends Out_7[A, B, C, D, E, F, G] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_8[A, B, C, D, E, F, G, Long]   {}) with Neighborhood_Out_8[A, B, C, D, E, F, G, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_8[A, B, C, D, E, F, G, String] {}) with Neighborhood_Out_8[A, B, C, D, E, F, G, String] {}
  def District = new OneRef with District_Out_7[A, B, C, D, E, F, G]
  def apply(in: ?.type)    = new Neighborhood_In_1_6[G, A, B, C, D, E, F] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_7[G, A, B, C, D, E, F, G] {}
  def <(in: ?.type)        = new Neighborhood_In_1_7[G, A, B, C, D, E, F, G] {}
  def contains(in: ?.type) = new Neighborhood_In_1_7[G, A, B, C, D, E, F, G] {}
  def apply(m: maybe.type) = new Neighborhood_Out_7[A, B, C, D, E, F, G] {}
}

trait Neighborhood_Out_8[A, B, C, D, E, F, G, H] extends Out_8[A, B, C, D, E, F, G, H] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_9[A, B, C, D, E, F, G, H, Long]   {}) with Neighborhood_Out_9[A, B, C, D, E, F, G, H, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_9[A, B, C, D, E, F, G, H, String] {}) with Neighborhood_Out_9[A, B, C, D, E, F, G, H, String] {}
  def District = new OneRef with District_Out_8[A, B, C, D, E, F, G, H]
  def apply(in: ?.type)    = new Neighborhood_In_1_7[H, A, B, C, D, E, F, G] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_8[H, A, B, C, D, E, F, G, H] {}
  def <(in: ?.type)        = new Neighborhood_In_1_8[H, A, B, C, D, E, F, G, H] {}
  def contains(in: ?.type) = new Neighborhood_In_1_8[H, A, B, C, D, E, F, G, H] {}
  def apply(m: maybe.type) = new Neighborhood_Out_8[A, B, C, D, E, F, G, H] {}
}

trait Neighborhood_Out_9[A, B, C, D, E, F, G, H, I] extends Out_9[A, B, C, D, E, F, G, H, I] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_10[A, B, C, D, E, F, G, H, I, Long]   {}) with Neighborhood_Out_10[A, B, C, D, E, F, G, H, I, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_10[A, B, C, D, E, F, G, H, I, String] {}) with Neighborhood_Out_10[A, B, C, D, E, F, G, H, I, String] {}
  def District = new OneRef with District_Out_9[A, B, C, D, E, F, G, H, I]
  def apply(in: ?.type)    = new Neighborhood_In_1_8[I, A, B, C, D, E, F, G, H] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_9[I, A, B, C, D, E, F, G, H, I] {}
  def <(in: ?.type)        = new Neighborhood_In_1_9[I, A, B, C, D, E, F, G, H, I] {}
  def contains(in: ?.type) = new Neighborhood_In_1_9[I, A, B, C, D, E, F, G, H, I] {}
  def apply(m: maybe.type) = new Neighborhood_Out_9[A, B, C, D, E, F, G, H, I] {}
}

trait Neighborhood_Out_10[A, B, C, D, E, F, G, H, I, J] extends Out_10[A, B, C, D, E, F, G, H, I, J] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_11[A, B, C, D, E, F, G, H, I, J, Long]   {}) with Neighborhood_Out_11[A, B, C, D, E, F, G, H, I, J, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_11[A, B, C, D, E, F, G, H, I, J, String] {}) with Neighborhood_Out_11[A, B, C, D, E, F, G, H, I, J, String] {}
  def District = new OneRef with District_Out_10[A, B, C, D, E, F, G, H, I, J]
  def apply(in: ?.type)    = new Neighborhood_In_1_9[J, A, B, C, D, E, F, G, H, I] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_10[J, A, B, C, D, E, F, G, H, I, J] {}
  def <(in: ?.type)        = new Neighborhood_In_1_10[J, A, B, C, D, E, F, G, H, I, J] {}
  def contains(in: ?.type) = new Neighborhood_In_1_10[J, A, B, C, D, E, F, G, H, I, J] {}
  def apply(m: maybe.type) = new Neighborhood_Out_10[A, B, C, D, E, F, G, H, I, J] {}
}

trait Neighborhood_Out_11[A, B, C, D, E, F, G, H, I, J, K] extends Out_11[A, B, C, D, E, F, G, H, I, J, K] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_12[A, B, C, D, E, F, G, H, I, J, K, Long]   {}) with Neighborhood_Out_12[A, B, C, D, E, F, G, H, I, J, K, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_12[A, B, C, D, E, F, G, H, I, J, K, String] {}) with Neighborhood_Out_12[A, B, C, D, E, F, G, H, I, J, K, String] {}
  def District = new OneRef with District_Out_11[A, B, C, D, E, F, G, H, I, J, K]
  def apply(in: ?.type)    = new Neighborhood_In_1_10[K, A, B, C, D, E, F, G, H, I, J] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_11[K, A, B, C, D, E, F, G, H, I, J, K] {}
  def <(in: ?.type)        = new Neighborhood_In_1_11[K, A, B, C, D, E, F, G, H, I, J, K] {}
  def contains(in: ?.type) = new Neighborhood_In_1_11[K, A, B, C, D, E, F, G, H, I, J, K] {}
  def apply(m: maybe.type) = new Neighborhood_Out_11[A, B, C, D, E, F, G, H, I, J, K] {}
}

trait Neighborhood_Out_12[A, B, C, D, E, F, G, H, I, J, K, L] extends Out_12[A, B, C, D, E, F, G, H, I, J, K, L] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, Long]   {}) with Neighborhood_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, String] {}) with Neighborhood_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, String] {}
  def District = new OneRef with District_Out_12[A, B, C, D, E, F, G, H, I, J, K, L]
  def apply(in: ?.type)    = new Neighborhood_In_1_11[L, A, B, C, D, E, F, G, H, I, J, K] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_12[L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def <(in: ?.type)        = new Neighborhood_In_1_12[L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def contains(in: ?.type) = new Neighborhood_In_1_12[L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def apply(m: maybe.type) = new Neighborhood_Out_12[A, B, C, D, E, F, G, H, I, J, K, L] {}
}

trait Neighborhood_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M] extends Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long]   {}) with Neighborhood_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String] {}) with Neighborhood_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String] {}
  def District = new OneRef with District_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M]
  def apply(in: ?.type)    = new Neighborhood_In_1_12[M, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_13[M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def <(in: ?.type)        = new Neighborhood_In_1_13[M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def contains(in: ?.type) = new Neighborhood_In_1_13[M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def apply(m: maybe.type) = new Neighborhood_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M] {}
}

trait Neighborhood_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]   {}) with Neighborhood_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String] {}) with Neighborhood_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String] {}
  def District = new OneRef with District_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  def apply(in: ?.type)    = new Neighborhood_In_1_13[N, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_14[N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def <(in: ?.type)        = new Neighborhood_In_1_14[N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def contains(in: ?.type) = new Neighborhood_In_1_14[N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def apply(m: maybe.type) = new Neighborhood_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
}

trait Neighborhood_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]   {}) with Neighborhood_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String] {}) with Neighborhood_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String] {}
  def District = new OneRef with District_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  def apply(in: ?.type)    = new Neighborhood_In_1_14[O, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_15[O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def <(in: ?.type)        = new Neighborhood_In_1_15[O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def contains(in: ?.type) = new Neighborhood_In_1_15[O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def apply(m: maybe.type) = new Neighborhood_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
}

trait Neighborhood_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]   {}) with Neighborhood_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String] {}) with Neighborhood_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String] {}
  def District = new OneRef with District_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  def apply(in: ?.type)    = new Neighborhood_In_1_15[P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_16[P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def <(in: ?.type)        = new Neighborhood_In_1_16[P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def contains(in: ?.type) = new Neighborhood_In_1_16[P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def apply(m: maybe.type) = new Neighborhood_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
}

trait Neighborhood_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]   {}) with Neighborhood_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String] {}) with Neighborhood_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String] {}
  def District = new OneRef with District_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  def apply(in: ?.type)    = new Neighborhood_In_1_16[Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_17[Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def <(in: ?.type)        = new Neighborhood_In_1_17[Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def contains(in: ?.type) = new Neighborhood_In_1_17[Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def apply(m: maybe.type) = new Neighborhood_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
}

trait Neighborhood_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]   {}) with Neighborhood_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String] {}) with Neighborhood_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String] {}
  def District = new OneRef with District_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  def apply(in: ?.type)    = new Neighborhood_In_1_17[R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_18[R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def <(in: ?.type)        = new Neighborhood_In_1_18[R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def contains(in: ?.type) = new Neighborhood_In_1_18[R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def apply(m: maybe.type) = new Neighborhood_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
}

trait Neighborhood_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]   {}) with Neighborhood_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String] {}) with Neighborhood_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String] {}
  def District = new OneRef with District_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  def apply(in: ?.type)    = new Neighborhood_In_1_18[S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_19[S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def <(in: ?.type)        = new Neighborhood_In_1_19[S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def contains(in: ?.type) = new Neighborhood_In_1_19[S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def apply(m: maybe.type) = new Neighborhood_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
}

trait Neighborhood_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]   {}) with Neighborhood_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String] {}) with Neighborhood_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String] {}
  def District = new OneRef with District_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  def apply(in: ?.type)    = new Neighborhood_In_1_19[T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_20[T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def <(in: ?.type)        = new Neighborhood_In_1_20[T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def contains(in: ?.type) = new Neighborhood_In_1_20[T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def apply(m: maybe.type) = new Neighborhood_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
}

trait Neighborhood_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]   {}) with Neighborhood_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]   {}
  lazy val name = new name (this, new Neighborhood_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String] {}) with Neighborhood_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String] {}
  def District = new OneRef with District_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  def apply(in: ?.type)    = new Neighborhood_In_1_20[U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def apply(in: ?!.type)   = new Neighborhood_In_1_21[U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def <(in: ?.type)        = new Neighborhood_In_1_21[U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def contains(in: ?.type) = new Neighborhood_In_1_21[U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def apply(m: maybe.type) = new Neighborhood_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
}

trait Neighborhood_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] 


/********* Input molecules awaiting 1 input *******************************/

trait Neighborhood_In_1_0[I1] extends In_1_0[I1] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_1[I1, Long]   {}) with Neighborhood_In_1_1[I1, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_1[I1, String] {}) with Neighborhood_In_1_1[I1, String] {}
  def District = new OneRef with District_In_1_0[I1]
}

trait Neighborhood_In_1_1[I1, A] extends In_1_1[I1, A] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_2[I1, A, Long]   {}) with Neighborhood_In_1_2[I1, A, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_2[I1, A, String] {}) with Neighborhood_In_1_2[I1, A, String] {}
  def District = new OneRef with District_In_1_1[I1, A]
  def apply(in: ?.type)    = new Neighborhood_In_2_0[I1, A] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_1[I1, A, A] {}
  def <(in: ?.type)        = new Neighborhood_In_2_1[I1, A, A] {}
  def contains(in: ?.type) = new Neighborhood_In_2_1[I1, A, A] {}
}

trait Neighborhood_In_1_2[I1, A, B] extends In_1_2[I1, A, B] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_3[I1, A, B, Long]   {}) with Neighborhood_In_1_3[I1, A, B, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_3[I1, A, B, String] {}) with Neighborhood_In_1_3[I1, A, B, String] {}
  def District = new OneRef with District_In_1_2[I1, A, B]
  def apply(in: ?.type)    = new Neighborhood_In_2_1[I1, B, A] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_2[I1, B, A, B] {}
  def <(in: ?.type)        = new Neighborhood_In_2_2[I1, B, A, B] {}
  def contains(in: ?.type) = new Neighborhood_In_2_2[I1, B, A, B] {}
}

trait Neighborhood_In_1_3[I1, A, B, C] extends In_1_3[I1, A, B, C] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_4[I1, A, B, C, Long]   {}) with Neighborhood_In_1_4[I1, A, B, C, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_4[I1, A, B, C, String] {}) with Neighborhood_In_1_4[I1, A, B, C, String] {}
  def District = new OneRef with District_In_1_3[I1, A, B, C]
  def apply(in: ?.type)    = new Neighborhood_In_2_2[I1, C, A, B] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_3[I1, C, A, B, C] {}
  def <(in: ?.type)        = new Neighborhood_In_2_3[I1, C, A, B, C] {}
  def contains(in: ?.type) = new Neighborhood_In_2_3[I1, C, A, B, C] {}
}

trait Neighborhood_In_1_4[I1, A, B, C, D] extends In_1_4[I1, A, B, C, D] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_5[I1, A, B, C, D, Long]   {}) with Neighborhood_In_1_5[I1, A, B, C, D, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_5[I1, A, B, C, D, String] {}) with Neighborhood_In_1_5[I1, A, B, C, D, String] {}
  def District = new OneRef with District_In_1_4[I1, A, B, C, D]
  def apply(in: ?.type)    = new Neighborhood_In_2_3[I1, D, A, B, C] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_4[I1, D, A, B, C, D] {}
  def <(in: ?.type)        = new Neighborhood_In_2_4[I1, D, A, B, C, D] {}
  def contains(in: ?.type) = new Neighborhood_In_2_4[I1, D, A, B, C, D] {}
}

trait Neighborhood_In_1_5[I1, A, B, C, D, E] extends In_1_5[I1, A, B, C, D, E] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_6[I1, A, B, C, D, E, Long]   {}) with Neighborhood_In_1_6[I1, A, B, C, D, E, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_6[I1, A, B, C, D, E, String] {}) with Neighborhood_In_1_6[I1, A, B, C, D, E, String] {}
  def District = new OneRef with District_In_1_5[I1, A, B, C, D, E]
  def apply(in: ?.type)    = new Neighborhood_In_2_4[I1, E, A, B, C, D] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_5[I1, E, A, B, C, D, E] {}
  def <(in: ?.type)        = new Neighborhood_In_2_5[I1, E, A, B, C, D, E] {}
  def contains(in: ?.type) = new Neighborhood_In_2_5[I1, E, A, B, C, D, E] {}
}

trait Neighborhood_In_1_6[I1, A, B, C, D, E, F] extends In_1_6[I1, A, B, C, D, E, F] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_7[I1, A, B, C, D, E, F, Long]   {}) with Neighborhood_In_1_7[I1, A, B, C, D, E, F, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_7[I1, A, B, C, D, E, F, String] {}) with Neighborhood_In_1_7[I1, A, B, C, D, E, F, String] {}
  def District = new OneRef with District_In_1_6[I1, A, B, C, D, E, F]
  def apply(in: ?.type)    = new Neighborhood_In_2_5[I1, F, A, B, C, D, E] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_6[I1, F, A, B, C, D, E, F] {}
  def <(in: ?.type)        = new Neighborhood_In_2_6[I1, F, A, B, C, D, E, F] {}
  def contains(in: ?.type) = new Neighborhood_In_2_6[I1, F, A, B, C, D, E, F] {}
}

trait Neighborhood_In_1_7[I1, A, B, C, D, E, F, G] extends In_1_7[I1, A, B, C, D, E, F, G] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_8[I1, A, B, C, D, E, F, G, Long]   {}) with Neighborhood_In_1_8[I1, A, B, C, D, E, F, G, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_8[I1, A, B, C, D, E, F, G, String] {}) with Neighborhood_In_1_8[I1, A, B, C, D, E, F, G, String] {}
  def District = new OneRef with District_In_1_7[I1, A, B, C, D, E, F, G]
  def apply(in: ?.type)    = new Neighborhood_In_2_6[I1, G, A, B, C, D, E, F] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_7[I1, G, A, B, C, D, E, F, G] {}
  def <(in: ?.type)        = new Neighborhood_In_2_7[I1, G, A, B, C, D, E, F, G] {}
  def contains(in: ?.type) = new Neighborhood_In_2_7[I1, G, A, B, C, D, E, F, G] {}
}

trait Neighborhood_In_1_8[I1, A, B, C, D, E, F, G, H] extends In_1_8[I1, A, B, C, D, E, F, G, H] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_9[I1, A, B, C, D, E, F, G, H, Long]   {}) with Neighborhood_In_1_9[I1, A, B, C, D, E, F, G, H, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_9[I1, A, B, C, D, E, F, G, H, String] {}) with Neighborhood_In_1_9[I1, A, B, C, D, E, F, G, H, String] {}
  def District = new OneRef with District_In_1_8[I1, A, B, C, D, E, F, G, H]
  def apply(in: ?.type)    = new Neighborhood_In_2_7[I1, H, A, B, C, D, E, F, G] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_8[I1, H, A, B, C, D, E, F, G, H] {}
  def <(in: ?.type)        = new Neighborhood_In_2_8[I1, H, A, B, C, D, E, F, G, H] {}
  def contains(in: ?.type) = new Neighborhood_In_2_8[I1, H, A, B, C, D, E, F, G, H] {}
}

trait Neighborhood_In_1_9[I1, A, B, C, D, E, F, G, H, I] extends In_1_9[I1, A, B, C, D, E, F, G, H, I] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_10[I1, A, B, C, D, E, F, G, H, I, Long]   {}) with Neighborhood_In_1_10[I1, A, B, C, D, E, F, G, H, I, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_10[I1, A, B, C, D, E, F, G, H, I, String] {}) with Neighborhood_In_1_10[I1, A, B, C, D, E, F, G, H, I, String] {}
  def District = new OneRef with District_In_1_9[I1, A, B, C, D, E, F, G, H, I]
  def apply(in: ?.type)    = new Neighborhood_In_2_8[I1, I, A, B, C, D, E, F, G, H] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_9[I1, I, A, B, C, D, E, F, G, H, I] {}
  def <(in: ?.type)        = new Neighborhood_In_2_9[I1, I, A, B, C, D, E, F, G, H, I] {}
  def contains(in: ?.type) = new Neighborhood_In_2_9[I1, I, A, B, C, D, E, F, G, H, I] {}
}

trait Neighborhood_In_1_10[I1, A, B, C, D, E, F, G, H, I, J] extends In_1_10[I1, A, B, C, D, E, F, G, H, I, J] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, Long]   {}) with Neighborhood_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, String] {}) with Neighborhood_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, String] {}
  def District = new OneRef with District_In_1_10[I1, A, B, C, D, E, F, G, H, I, J]
  def apply(in: ?.type)    = new Neighborhood_In_2_9[I1, J, A, B, C, D, E, F, G, H, I] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_10[I1, J, A, B, C, D, E, F, G, H, I, J] {}
  def <(in: ?.type)        = new Neighborhood_In_2_10[I1, J, A, B, C, D, E, F, G, H, I, J] {}
  def contains(in: ?.type) = new Neighborhood_In_2_10[I1, J, A, B, C, D, E, F, G, H, I, J] {}
}

trait Neighborhood_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, K] extends In_1_11[I1, A, B, C, D, E, F, G, H, I, J, K] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long]   {}) with Neighborhood_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String] {}) with Neighborhood_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String] {}
  def District = new OneRef with District_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, K]
  def apply(in: ?.type)    = new Neighborhood_In_2_10[I1, K, A, B, C, D, E, F, G, H, I, J] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_11[I1, K, A, B, C, D, E, F, G, H, I, J, K] {}
  def <(in: ?.type)        = new Neighborhood_In_2_11[I1, K, A, B, C, D, E, F, G, H, I, J, K] {}
  def contains(in: ?.type) = new Neighborhood_In_2_11[I1, K, A, B, C, D, E, F, G, H, I, J, K] {}
}

trait Neighborhood_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L] extends In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long]   {}) with Neighborhood_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String] {}) with Neighborhood_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String] {}
  def District = new OneRef with District_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L]
  def apply(in: ?.type)    = new Neighborhood_In_2_11[I1, L, A, B, C, D, E, F, G, H, I, J, K] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_12[I1, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def <(in: ?.type)        = new Neighborhood_In_2_12[I1, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def contains(in: ?.type) = new Neighborhood_In_2_12[I1, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
}

trait Neighborhood_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M] extends In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]   {}) with Neighborhood_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String] {}) with Neighborhood_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String] {}
  def District = new OneRef with District_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M]
  def apply(in: ?.type)    = new Neighborhood_In_2_12[I1, M, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_13[I1, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def <(in: ?.type)        = new Neighborhood_In_2_13[I1, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def contains(in: ?.type) = new Neighborhood_In_2_13[I1, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
}

trait Neighborhood_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]   {}) with Neighborhood_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String] {}) with Neighborhood_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String] {}
  def District = new OneRef with District_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  def apply(in: ?.type)    = new Neighborhood_In_2_13[I1, N, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_14[I1, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def <(in: ?.type)        = new Neighborhood_In_2_14[I1, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def contains(in: ?.type) = new Neighborhood_In_2_14[I1, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
}

trait Neighborhood_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]   {}) with Neighborhood_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String] {}) with Neighborhood_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String] {}
  def District = new OneRef with District_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  def apply(in: ?.type)    = new Neighborhood_In_2_14[I1, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_15[I1, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def <(in: ?.type)        = new Neighborhood_In_2_15[I1, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def contains(in: ?.type) = new Neighborhood_In_2_15[I1, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
}

trait Neighborhood_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]   {}) with Neighborhood_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String] {}) with Neighborhood_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String] {}
  def District = new OneRef with District_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  def apply(in: ?.type)    = new Neighborhood_In_2_15[I1, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_16[I1, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def <(in: ?.type)        = new Neighborhood_In_2_16[I1, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def contains(in: ?.type) = new Neighborhood_In_2_16[I1, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
}

trait Neighborhood_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]   {}) with Neighborhood_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String] {}) with Neighborhood_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String] {}
  def District = new OneRef with District_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  def apply(in: ?.type)    = new Neighborhood_In_2_16[I1, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_17[I1, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def <(in: ?.type)        = new Neighborhood_In_2_17[I1, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def contains(in: ?.type) = new Neighborhood_In_2_17[I1, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
}

trait Neighborhood_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]   {}) with Neighborhood_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String] {}) with Neighborhood_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String] {}
  def District = new OneRef with District_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  def apply(in: ?.type)    = new Neighborhood_In_2_17[I1, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_18[I1, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def <(in: ?.type)        = new Neighborhood_In_2_18[I1, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def contains(in: ?.type) = new Neighborhood_In_2_18[I1, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
}

trait Neighborhood_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]   {}) with Neighborhood_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String] {}) with Neighborhood_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String] {}
  def District = new OneRef with District_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  def apply(in: ?.type)    = new Neighborhood_In_2_18[I1, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_19[I1, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def <(in: ?.type)        = new Neighborhood_In_2_19[I1, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def contains(in: ?.type) = new Neighborhood_In_2_19[I1, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
}

trait Neighborhood_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]   {}) with Neighborhood_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String] {}) with Neighborhood_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String] {}
  def District = new OneRef with District_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  def apply(in: ?.type)    = new Neighborhood_In_2_19[I1, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_20[I1, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def <(in: ?.type)        = new Neighborhood_In_2_20[I1, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def contains(in: ?.type) = new Neighborhood_In_2_20[I1, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
}

trait Neighborhood_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]   {}) with Neighborhood_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String] {}) with Neighborhood_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String] {}
  def District = new OneRef with District_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  def apply(in: ?.type)    = new Neighborhood_In_2_20[I1, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def apply(in: ?!.type)   = new Neighborhood_In_2_21[I1, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def <(in: ?.type)        = new Neighborhood_In_2_21[I1, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def contains(in: ?.type) = new Neighborhood_In_2_21[I1, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
}

trait Neighborhood_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] 


/********* Input molecules awaiting 2 inputs *******************************/

trait Neighborhood_In_2_0[I1, I2] extends In_2_0[I1, I2] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_1[I1, I2, Long]   {}) with Neighborhood_In_2_1[I1, I2, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_1[I1, I2, String] {}) with Neighborhood_In_2_1[I1, I2, String] {}
  def District = new OneRef with District_In_2_0[I1, I2]
}

trait Neighborhood_In_2_1[I1, I2, A] extends In_2_1[I1, I2, A] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_2[I1, I2, A, Long]   {}) with Neighborhood_In_2_2[I1, I2, A, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_2[I1, I2, A, String] {}) with Neighborhood_In_2_2[I1, I2, A, String] {}
  def District = new OneRef with District_In_2_1[I1, I2, A]
  def apply(in: ?.type)    = new Neighborhood_In_3_0[I1, I2, A] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_1[I1, I2, A, A] {}
  def <(in: ?.type)        = new Neighborhood_In_3_1[I1, I2, A, A] {}
  def contains(in: ?.type) = new Neighborhood_In_3_1[I1, I2, A, A] {}
}

trait Neighborhood_In_2_2[I1, I2, A, B] extends In_2_2[I1, I2, A, B] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_3[I1, I2, A, B, Long]   {}) with Neighborhood_In_2_3[I1, I2, A, B, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_3[I1, I2, A, B, String] {}) with Neighborhood_In_2_3[I1, I2, A, B, String] {}
  def District = new OneRef with District_In_2_2[I1, I2, A, B]
  def apply(in: ?.type)    = new Neighborhood_In_3_1[I1, I2, B, A] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_2[I1, I2, B, A, B] {}
  def <(in: ?.type)        = new Neighborhood_In_3_2[I1, I2, B, A, B] {}
  def contains(in: ?.type) = new Neighborhood_In_3_2[I1, I2, B, A, B] {}
}

trait Neighborhood_In_2_3[I1, I2, A, B, C] extends In_2_3[I1, I2, A, B, C] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_4[I1, I2, A, B, C, Long]   {}) with Neighborhood_In_2_4[I1, I2, A, B, C, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_4[I1, I2, A, B, C, String] {}) with Neighborhood_In_2_4[I1, I2, A, B, C, String] {}
  def District = new OneRef with District_In_2_3[I1, I2, A, B, C]
  def apply(in: ?.type)    = new Neighborhood_In_3_2[I1, I2, C, A, B] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_3[I1, I2, C, A, B, C] {}
  def <(in: ?.type)        = new Neighborhood_In_3_3[I1, I2, C, A, B, C] {}
  def contains(in: ?.type) = new Neighborhood_In_3_3[I1, I2, C, A, B, C] {}
}

trait Neighborhood_In_2_4[I1, I2, A, B, C, D] extends In_2_4[I1, I2, A, B, C, D] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_5[I1, I2, A, B, C, D, Long]   {}) with Neighborhood_In_2_5[I1, I2, A, B, C, D, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_5[I1, I2, A, B, C, D, String] {}) with Neighborhood_In_2_5[I1, I2, A, B, C, D, String] {}
  def District = new OneRef with District_In_2_4[I1, I2, A, B, C, D]
  def apply(in: ?.type)    = new Neighborhood_In_3_3[I1, I2, D, A, B, C] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_4[I1, I2, D, A, B, C, D] {}
  def <(in: ?.type)        = new Neighborhood_In_3_4[I1, I2, D, A, B, C, D] {}
  def contains(in: ?.type) = new Neighborhood_In_3_4[I1, I2, D, A, B, C, D] {}
}

trait Neighborhood_In_2_5[I1, I2, A, B, C, D, E] extends In_2_5[I1, I2, A, B, C, D, E] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_6[I1, I2, A, B, C, D, E, Long]   {}) with Neighborhood_In_2_6[I1, I2, A, B, C, D, E, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_6[I1, I2, A, B, C, D, E, String] {}) with Neighborhood_In_2_6[I1, I2, A, B, C, D, E, String] {}
  def District = new OneRef with District_In_2_5[I1, I2, A, B, C, D, E]
  def apply(in: ?.type)    = new Neighborhood_In_3_4[I1, I2, E, A, B, C, D] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_5[I1, I2, E, A, B, C, D, E] {}
  def <(in: ?.type)        = new Neighborhood_In_3_5[I1, I2, E, A, B, C, D, E] {}
  def contains(in: ?.type) = new Neighborhood_In_3_5[I1, I2, E, A, B, C, D, E] {}
}

trait Neighborhood_In_2_6[I1, I2, A, B, C, D, E, F] extends In_2_6[I1, I2, A, B, C, D, E, F] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_7[I1, I2, A, B, C, D, E, F, Long]   {}) with Neighborhood_In_2_7[I1, I2, A, B, C, D, E, F, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_7[I1, I2, A, B, C, D, E, F, String] {}) with Neighborhood_In_2_7[I1, I2, A, B, C, D, E, F, String] {}
  def District = new OneRef with District_In_2_6[I1, I2, A, B, C, D, E, F]
  def apply(in: ?.type)    = new Neighborhood_In_3_5[I1, I2, F, A, B, C, D, E] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
  def <(in: ?.type)        = new Neighborhood_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
  def contains(in: ?.type) = new Neighborhood_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
}

trait Neighborhood_In_2_7[I1, I2, A, B, C, D, E, F, G] extends In_2_7[I1, I2, A, B, C, D, E, F, G] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]   {}) with Neighborhood_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}) with Neighborhood_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}
  def District = new OneRef with District_In_2_7[I1, I2, A, B, C, D, E, F, G]
  def apply(in: ?.type)    = new Neighborhood_In_3_6[I1, I2, G, A, B, C, D, E, F] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
  def <(in: ?.type)        = new Neighborhood_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
  def contains(in: ?.type) = new Neighborhood_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
}

trait Neighborhood_In_2_8[I1, I2, A, B, C, D, E, F, G, H] extends In_2_8[I1, I2, A, B, C, D, E, F, G, H] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_9[I1, I2, A, B, C, D, E, F, G, H, Long]   {}) with Neighborhood_In_2_9[I1, I2, A, B, C, D, E, F, G, H, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_9[I1, I2, A, B, C, D, E, F, G, H, String] {}) with Neighborhood_In_2_9[I1, I2, A, B, C, D, E, F, G, H, String] {}
  def District = new OneRef with District_In_2_8[I1, I2, A, B, C, D, E, F, G, H]
  def apply(in: ?.type)    = new Neighborhood_In_3_7[I1, I2, H, A, B, C, D, E, F, G] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_8[I1, I2, H, A, B, C, D, E, F, G, H] {}
  def <(in: ?.type)        = new Neighborhood_In_3_8[I1, I2, H, A, B, C, D, E, F, G, H] {}
  def contains(in: ?.type) = new Neighborhood_In_3_8[I1, I2, H, A, B, C, D, E, F, G, H] {}
}

trait Neighborhood_In_2_9[I1, I2, A, B, C, D, E, F, G, H, I] extends In_2_9[I1, I2, A, B, C, D, E, F, G, H, I] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, Long]   {}) with Neighborhood_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, String] {}) with Neighborhood_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, String] {}
  def District = new OneRef with District_In_2_9[I1, I2, A, B, C, D, E, F, G, H, I]
  def apply(in: ?.type)    = new Neighborhood_In_3_8[I1, I2, I, A, B, C, D, E, F, G, H] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_9[I1, I2, I, A, B, C, D, E, F, G, H, I] {}
  def <(in: ?.type)        = new Neighborhood_In_3_9[I1, I2, I, A, B, C, D, E, F, G, H, I] {}
  def contains(in: ?.type) = new Neighborhood_In_3_9[I1, I2, I, A, B, C, D, E, F, G, H, I] {}
}

trait Neighborhood_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] extends In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Long]   {}) with Neighborhood_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String] {}) with Neighborhood_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String] {}
  def District = new OneRef with District_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J]
  def apply(in: ?.type)    = new Neighborhood_In_3_9[I1, I2, J, A, B, C, D, E, F, G, H, I] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_10[I1, I2, J, A, B, C, D, E, F, G, H, I, J] {}
  def <(in: ?.type)        = new Neighborhood_In_3_10[I1, I2, J, A, B, C, D, E, F, G, H, I, J] {}
  def contains(in: ?.type) = new Neighborhood_In_3_10[I1, I2, J, A, B, C, D, E, F, G, H, I, J] {}
}

trait Neighborhood_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] extends In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Long]   {}) with Neighborhood_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String] {}) with Neighborhood_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String] {}
  def District = new OneRef with District_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K]
  def apply(in: ?.type)    = new Neighborhood_In_3_10[I1, I2, K, A, B, C, D, E, F, G, H, I, J] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_11[I1, I2, K, A, B, C, D, E, F, G, H, I, J, K] {}
  def <(in: ?.type)        = new Neighborhood_In_3_11[I1, I2, K, A, B, C, D, E, F, G, H, I, J, K] {}
  def contains(in: ?.type) = new Neighborhood_In_3_11[I1, I2, K, A, B, C, D, E, F, G, H, I, J, K] {}
}

trait Neighborhood_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] extends In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Long]   {}) with Neighborhood_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String] {}) with Neighborhood_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String] {}
  def District = new OneRef with District_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]
  def apply(in: ?.type)    = new Neighborhood_In_3_11[I1, I2, L, A, B, C, D, E, F, G, H, I, J, K] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_12[I1, I2, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def <(in: ?.type)        = new Neighborhood_In_3_12[I1, I2, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def contains(in: ?.type) = new Neighborhood_In_3_12[I1, I2, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
}

trait Neighborhood_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] extends In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]   {}) with Neighborhood_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String] {}) with Neighborhood_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String] {}
  def District = new OneRef with District_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]
  def apply(in: ?.type)    = new Neighborhood_In_3_12[I1, I2, M, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_13[I1, I2, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def <(in: ?.type)        = new Neighborhood_In_3_13[I1, I2, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def contains(in: ?.type) = new Neighborhood_In_3_13[I1, I2, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
}

trait Neighborhood_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]   {}) with Neighborhood_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String] {}) with Neighborhood_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String] {}
  def District = new OneRef with District_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  def apply(in: ?.type)    = new Neighborhood_In_3_13[I1, I2, N, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_14[I1, I2, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def <(in: ?.type)        = new Neighborhood_In_3_14[I1, I2, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def contains(in: ?.type) = new Neighborhood_In_3_14[I1, I2, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
}

trait Neighborhood_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]   {}) with Neighborhood_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String] {}) with Neighborhood_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String] {}
  def District = new OneRef with District_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  def apply(in: ?.type)    = new Neighborhood_In_3_14[I1, I2, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_15[I1, I2, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def <(in: ?.type)        = new Neighborhood_In_3_15[I1, I2, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def contains(in: ?.type) = new Neighborhood_In_3_15[I1, I2, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
}

trait Neighborhood_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]   {}) with Neighborhood_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String] {}) with Neighborhood_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String] {}
  def District = new OneRef with District_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  def apply(in: ?.type)    = new Neighborhood_In_3_15[I1, I2, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_16[I1, I2, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def <(in: ?.type)        = new Neighborhood_In_3_16[I1, I2, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def contains(in: ?.type) = new Neighborhood_In_3_16[I1, I2, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
}

trait Neighborhood_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]   {}) with Neighborhood_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String] {}) with Neighborhood_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String] {}
  def District = new OneRef with District_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  def apply(in: ?.type)    = new Neighborhood_In_3_16[I1, I2, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_17[I1, I2, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def <(in: ?.type)        = new Neighborhood_In_3_17[I1, I2, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def contains(in: ?.type) = new Neighborhood_In_3_17[I1, I2, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
}

trait Neighborhood_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]   {}) with Neighborhood_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String] {}) with Neighborhood_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String] {}
  def District = new OneRef with District_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  def apply(in: ?.type)    = new Neighborhood_In_3_17[I1, I2, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_18[I1, I2, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def <(in: ?.type)        = new Neighborhood_In_3_18[I1, I2, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def contains(in: ?.type) = new Neighborhood_In_3_18[I1, I2, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
}

trait Neighborhood_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]   {}) with Neighborhood_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String] {}) with Neighborhood_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String] {}
  def District = new OneRef with District_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  def apply(in: ?.type)    = new Neighborhood_In_3_18[I1, I2, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_19[I1, I2, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def <(in: ?.type)        = new Neighborhood_In_3_19[I1, I2, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def contains(in: ?.type) = new Neighborhood_In_3_19[I1, I2, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
}

trait Neighborhood_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]   {}) with Neighborhood_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String] {}) with Neighborhood_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String] {}
  def District = new OneRef with District_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  def apply(in: ?.type)    = new Neighborhood_In_3_19[I1, I2, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_20[I1, I2, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def <(in: ?.type)        = new Neighborhood_In_3_20[I1, I2, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def contains(in: ?.type) = new Neighborhood_In_3_20[I1, I2, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
}

trait Neighborhood_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]   {}) with Neighborhood_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String] {}) with Neighborhood_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String] {}
  def District = new OneRef with District_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  def apply(in: ?.type)    = new Neighborhood_In_3_20[I1, I2, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def apply(in: ?!.type)   = new Neighborhood_In_3_21[I1, I2, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def <(in: ?.type)        = new Neighborhood_In_3_21[I1, I2, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def contains(in: ?.type) = new Neighborhood_In_3_21[I1, I2, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
}

trait Neighborhood_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] 


/********* Input molecules awaiting 3 inputs *******************************/

trait Neighborhood_In_3_0[I1, I2, I3] extends In_3_0[I1, I2, I3] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_1[I1, I2, I3, Long]   {}) with Neighborhood_In_3_1[I1, I2, I3, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_1[I1, I2, I3, String] {}) with Neighborhood_In_3_1[I1, I2, I3, String] {}
  def District = new OneRef with District_In_3_0[I1, I2, I3]
}

trait Neighborhood_In_3_1[I1, I2, I3, A] extends In_3_1[I1, I2, I3, A] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_2[I1, I2, I3, A, Long]   {}) with Neighborhood_In_3_2[I1, I2, I3, A, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_2[I1, I2, I3, A, String] {}) with Neighborhood_In_3_2[I1, I2, I3, A, String] {}
  def District = new OneRef with District_In_3_1[I1, I2, I3, A]
}

trait Neighborhood_In_3_2[I1, I2, I3, A, B] extends In_3_2[I1, I2, I3, A, B] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_3[I1, I2, I3, A, B, Long]   {}) with Neighborhood_In_3_3[I1, I2, I3, A, B, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_3[I1, I2, I3, A, B, String] {}) with Neighborhood_In_3_3[I1, I2, I3, A, B, String] {}
  def District = new OneRef with District_In_3_2[I1, I2, I3, A, B]
}

trait Neighborhood_In_3_3[I1, I2, I3, A, B, C] extends In_3_3[I1, I2, I3, A, B, C] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_4[I1, I2, I3, A, B, C, Long]   {}) with Neighborhood_In_3_4[I1, I2, I3, A, B, C, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_4[I1, I2, I3, A, B, C, String] {}) with Neighborhood_In_3_4[I1, I2, I3, A, B, C, String] {}
  def District = new OneRef with District_In_3_3[I1, I2, I3, A, B, C]
}

trait Neighborhood_In_3_4[I1, I2, I3, A, B, C, D] extends In_3_4[I1, I2, I3, A, B, C, D] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_5[I1, I2, I3, A, B, C, D, Long]   {}) with Neighborhood_In_3_5[I1, I2, I3, A, B, C, D, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_5[I1, I2, I3, A, B, C, D, String] {}) with Neighborhood_In_3_5[I1, I2, I3, A, B, C, D, String] {}
  def District = new OneRef with District_In_3_4[I1, I2, I3, A, B, C, D]
}

trait Neighborhood_In_3_5[I1, I2, I3, A, B, C, D, E] extends In_3_5[I1, I2, I3, A, B, C, D, E] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_6[I1, I2, I3, A, B, C, D, E, Long]   {}) with Neighborhood_In_3_6[I1, I2, I3, A, B, C, D, E, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}) with Neighborhood_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}
  def District = new OneRef with District_In_3_5[I1, I2, I3, A, B, C, D, E]
}

trait Neighborhood_In_3_6[I1, I2, I3, A, B, C, D, E, F] extends In_3_6[I1, I2, I3, A, B, C, D, E, F] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]   {}) with Neighborhood_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}) with Neighborhood_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}
  def District = new OneRef with District_In_3_6[I1, I2, I3, A, B, C, D, E, F]
}

trait Neighborhood_In_3_7[I1, I2, I3, A, B, C, D, E, F, G] extends In_3_7[I1, I2, I3, A, B, C, D, E, F, G] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]   {}) with Neighborhood_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}) with Neighborhood_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}
  def District = new OneRef with District_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]
}

trait Neighborhood_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] extends In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long]   {}) with Neighborhood_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String] {}) with Neighborhood_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String] {}
  def District = new OneRef with District_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]
}

trait Neighborhood_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I] extends In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long]   {}) with Neighborhood_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String] {}) with Neighborhood_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String] {}
  def District = new OneRef with District_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I]
}

trait Neighborhood_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J] extends In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long]   {}) with Neighborhood_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String] {}) with Neighborhood_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String] {}
  def District = new OneRef with District_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J]
}

trait Neighborhood_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] extends In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long]   {}) with Neighborhood_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String] {}) with Neighborhood_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String] {}
  def District = new OneRef with District_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K]
}

trait Neighborhood_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] extends In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long]   {}) with Neighborhood_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String] {}) with Neighborhood_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String] {}
  def District = new OneRef with District_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L]
}

trait Neighborhood_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] extends In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]   {}) with Neighborhood_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String] {}) with Neighborhood_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String] {}
  def District = new OneRef with District_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M]
}

trait Neighborhood_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]   {}) with Neighborhood_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String] {}) with Neighborhood_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String] {}
  def District = new OneRef with District_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
}

trait Neighborhood_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]   {}) with Neighborhood_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String] {}) with Neighborhood_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String] {}
  def District = new OneRef with District_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
}

trait Neighborhood_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]   {}) with Neighborhood_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String] {}) with Neighborhood_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String] {}
  def District = new OneRef with District_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
}

trait Neighborhood_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]   {}) with Neighborhood_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String] {}) with Neighborhood_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String] {}
  def District = new OneRef with District_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
}

trait Neighborhood_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]   {}) with Neighborhood_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String] {}) with Neighborhood_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String] {}
  def District = new OneRef with District_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
}

trait Neighborhood_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]   {}) with Neighborhood_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String] {}) with Neighborhood_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String] {}
  def District = new OneRef with District_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
}

trait Neighborhood_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]   {}) with Neighborhood_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String] {}) with Neighborhood_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String] {}
  def District = new OneRef with District_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
}

trait Neighborhood_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {
  import Neighborhood._
  lazy val eid  = new eid  (this, new Neighborhood_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]   {}) with Neighborhood_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]   {}
  lazy val name = new name (this, new Neighborhood_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String] {}) with Neighborhood_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String] {}
  def District = new OneRef with District_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
}

trait Neighborhood_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]
                  