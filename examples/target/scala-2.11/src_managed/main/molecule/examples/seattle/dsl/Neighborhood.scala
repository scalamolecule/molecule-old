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

trait Neighborhood_Out_8[A, B, C, D, E, F, G, H] extends Out_8[A, B, C, D, E, F, G, H] 


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

trait Neighborhood_In_1_8[I1, A, B, C, D, E, F, G, H] extends In_1_8[I1, A, B, C, D, E, F, G, H] 


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

trait Neighborhood_In_2_8[I1, I2, A, B, C, D, E, F, G, H] extends In_2_8[I1, I2, A, B, C, D, E, F, G, H] 


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

trait Neighborhood_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] extends In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]
                  