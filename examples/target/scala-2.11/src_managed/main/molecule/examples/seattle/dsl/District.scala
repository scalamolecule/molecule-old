/*
 * AUTO-GENERATED CODE - DO NOT CHANGE!
 *
 * Manual changes to this file will likely break molecules!
 * Instead, change the molecule definition files and recompile your project with `sbt compile`.
 */
package molecule.examples.seattle.dsl
import molecule._
import ast.model._
import dsl.schemaDSL._

object District extends District_Out_0 {
  class name[NS, NS2](ns: NS, ns2: NS2) extends OneString(ns, ns2) with FulltextSearch with UniqueIdentity {
    def apply(data: oldNew[String]) = District_Update()
  }

  class region[NS, NS2](ns: NS, ns2: NS2) extends OneEnum(ns, ns2) {
    private lazy val n, ne, e, se, s, sw, w, nw = EnumValue
    def apply(data: oldNew[String]) = District_Update()
  }

  def insert = District_Insert()
}

case class District_Entity(elements: Seq[Element] = Seq()) extends Entity(elements) {
  lazy val eid    = _get(1, "eid"   , "Long"  )
  lazy val name   = _get(1, "name"  , "String")
  lazy val region = _get(1, "region", "String")

  private def _get(card: Int, attr: String, tpe: String) =
    District_Entity(elements :+ Atom("district", attr, tpe, card, NoValue))
}

case class District_Insert(override val elements: Seq[Element] = Seq()) extends Insert(elements) {
  lazy val eid    = (data: Long  ) => _insert(Seq(data), 1, "eid"   , "Long"  )
  lazy val name   = (data: String) => _insert(Seq(data), 1, "name"  , "String")
  lazy val region = (data: String) => _insert(Seq(data), 1, "region", "String", Some(":district.region/"))

  private def _insert(data: Seq[Any], card: Int, attr: String, tpe: String, enumPrefix: Option[String] = None) =
    District_Insert(elements :+ Atom("district", attr, tpe, card, Eq(data.map(_.toString)), enumPrefix))
}

case class District_Update(override val elements: Seq[Element] = Seq(), override val ids: Seq[Long] = Seq()) extends Update(elements, ids) {
  lazy val eid    = eid_
  lazy val name   = name_
  lazy val region = region_

  private[molecule] object eid_ {
    def apply(data: Long)   = _assertNewFact(Seq(data), 1, "eid", "Long")
    def apply()             = _retract(                 1, "eid")
  }
  private[molecule] object name_ {
    def apply(data: String) = _assertNewFact(Seq(data), 1, "name", "String")
    def apply()             = _retract(                 1, "name")
  }
  private[molecule] object region_ {
    def apply(data: String) = _assertNewFact(Seq(data), 1, "region", "String", Some(":district.region/"))
    def apply()             = _retract(                 1, "region")
  }

  private def _assertNewFact(data: Seq[Any], card: Int, attr: String, tpe: String, enumPrefix: Option[String] = None) =
    District_Update(elements :+ Atom("district", attr, tpe, card, Eq(data.map(_.toString)), enumPrefix), ids)

  private def _swap(oldNew: Seq[(String, String)], attr: String, tpe: String, enumPrefix: Option[String] = None) =
    District_Update(elements :+ Atom("district", attr, tpe, 2, Replace(oldNew.toMap), enumPrefix), ids)

  private def _removeElements(values: Seq[String], attr: String, tpe: String, enumPrefix: Option[String] = None) =
    District_Update(elements :+ Atom("district", attr, tpe, 2, Remove(values), enumPrefix), ids)

  private def _retract(card: Int, attr: String) =
    District_Update(elements :+ Atom("district", attr, "", card, Remove(Seq())), ids)
}

case class District_Retract(elements: Seq[Element] = Seq()) extends Retract(elements) {
  lazy val eid    = _retract(1, "eid"   , "Long"  )
  lazy val name   = _retract(1, "name"  , "String")
  lazy val region = _retract(1, "region", "String")

  private def _retract(card: Int, attr: String, tpe: String, data: Seq[Any] = Seq()) =
    District_Retract(elements :+ Atom("district", attr, tpe, card, Eq(data.map(_.toString))))
}

//********* Output molecules *******************************/

 trait District_Out_0 extends Out_0 {
  import District._
  
  @default
  def apply(name: String) = new name(this, new District_Out_1[String] {}) with District_Out_1[String]
  def apply(id: Long) = District_Entity()

  def update(id: Long) = District_Update(Seq(), Seq(id))
  def update(ids: Seq[Long]) = District_Update(Seq(), ids)

  lazy val eid    = new eid    (this, new District_Out_1[Long]   {}) with District_Out_1[Long]   {}
  lazy val name   = new name   (this, new District_Out_1[String] {}) with District_Out_1[String] {}
  lazy val region = new region (this, new District_Out_1[String] {}) with District_Out_1[String] {}
}

trait District_Out_1[A] extends Out_1[A] {
  import District._
  lazy val eid    = new eid    (this, new District_Out_2[A, Long]   {}) with District_Out_2[A, Long]   {}
  lazy val name   = new name   (this, new District_Out_2[A, String] {}) with District_Out_2[A, String] {}
  lazy val region = new region (this, new District_Out_2[A, String] {}) with District_Out_2[A, String] {}
  def apply(in: ?.type)    = new District_In_1_0[A] {}
  def apply(in: ?!.type)   = new District_In_1_1[A, A] {}
  def <(in: ?.type)        = new District_In_1_1[A, A] {}
  def contains(in: ?.type) = new District_In_1_1[A, A] {}
  def apply(m: maybe.type) = new District_Out_1[A] {}
}

trait District_Out_2[A, B] extends Out_2[A, B] {
  import District._
  lazy val eid    = new eid    (this, new District_Out_3[A, B, Long]   {}) with District_Out_3[A, B, Long]   {}
  lazy val name   = new name   (this, new District_Out_3[A, B, String] {}) with District_Out_3[A, B, String] {}
  lazy val region = new region (this, new District_Out_3[A, B, String] {}) with District_Out_3[A, B, String] {}
  def apply(in: ?.type)    = new District_In_1_1[B, A] {}
  def apply(in: ?!.type)   = new District_In_1_2[B, A, B] {}
  def <(in: ?.type)        = new District_In_1_2[B, A, B] {}
  def contains(in: ?.type) = new District_In_1_2[B, A, B] {}
  def apply(m: maybe.type) = new District_Out_2[A, B] {}
}

trait District_Out_3[A, B, C] extends Out_3[A, B, C] {
  import District._
  lazy val eid    = new eid    (this, new District_Out_4[A, B, C, Long]   {}) with District_Out_4[A, B, C, Long]   {}
  lazy val name   = new name   (this, new District_Out_4[A, B, C, String] {}) with District_Out_4[A, B, C, String] {}
  lazy val region = new region (this, new District_Out_4[A, B, C, String] {}) with District_Out_4[A, B, C, String] {}
  def apply(in: ?.type)    = new District_In_1_2[C, A, B] {}
  def apply(in: ?!.type)   = new District_In_1_3[C, A, B, C] {}
  def <(in: ?.type)        = new District_In_1_3[C, A, B, C] {}
  def contains(in: ?.type) = new District_In_1_3[C, A, B, C] {}
  def apply(m: maybe.type) = new District_Out_3[A, B, C] {}
}

trait District_Out_4[A, B, C, D] extends Out_4[A, B, C, D] {
  import District._
  lazy val eid    = new eid    (this, new District_Out_5[A, B, C, D, Long]   {}) with District_Out_5[A, B, C, D, Long]   {}
  lazy val name   = new name   (this, new District_Out_5[A, B, C, D, String] {}) with District_Out_5[A, B, C, D, String] {}
  lazy val region = new region (this, new District_Out_5[A, B, C, D, String] {}) with District_Out_5[A, B, C, D, String] {}
  def apply(in: ?.type)    = new District_In_1_3[D, A, B, C] {}
  def apply(in: ?!.type)   = new District_In_1_4[D, A, B, C, D] {}
  def <(in: ?.type)        = new District_In_1_4[D, A, B, C, D] {}
  def contains(in: ?.type) = new District_In_1_4[D, A, B, C, D] {}
  def apply(m: maybe.type) = new District_Out_4[A, B, C, D] {}
}

trait District_Out_5[A, B, C, D, E] extends Out_5[A, B, C, D, E] {
  import District._
  lazy val eid    = new eid    (this, new District_Out_6[A, B, C, D, E, Long]   {}) with District_Out_6[A, B, C, D, E, Long]   {}
  lazy val name   = new name   (this, new District_Out_6[A, B, C, D, E, String] {}) with District_Out_6[A, B, C, D, E, String] {}
  lazy val region = new region (this, new District_Out_6[A, B, C, D, E, String] {}) with District_Out_6[A, B, C, D, E, String] {}
  def apply(in: ?.type)    = new District_In_1_4[E, A, B, C, D] {}
  def apply(in: ?!.type)   = new District_In_1_5[E, A, B, C, D, E] {}
  def <(in: ?.type)        = new District_In_1_5[E, A, B, C, D, E] {}
  def contains(in: ?.type) = new District_In_1_5[E, A, B, C, D, E] {}
  def apply(m: maybe.type) = new District_Out_5[A, B, C, D, E] {}
}

trait District_Out_6[A, B, C, D, E, F] extends Out_6[A, B, C, D, E, F] {
  import District._
  lazy val eid    = new eid    (this, new District_Out_7[A, B, C, D, E, F, Long]   {}) with District_Out_7[A, B, C, D, E, F, Long]   {}
  lazy val name   = new name   (this, new District_Out_7[A, B, C, D, E, F, String] {}) with District_Out_7[A, B, C, D, E, F, String] {}
  lazy val region = new region (this, new District_Out_7[A, B, C, D, E, F, String] {}) with District_Out_7[A, B, C, D, E, F, String] {}
  def apply(in: ?.type)    = new District_In_1_5[F, A, B, C, D, E] {}
  def apply(in: ?!.type)   = new District_In_1_6[F, A, B, C, D, E, F] {}
  def <(in: ?.type)        = new District_In_1_6[F, A, B, C, D, E, F] {}
  def contains(in: ?.type) = new District_In_1_6[F, A, B, C, D, E, F] {}
  def apply(m: maybe.type) = new District_Out_6[A, B, C, D, E, F] {}
}

trait District_Out_7[A, B, C, D, E, F, G] extends Out_7[A, B, C, D, E, F, G] {
  import District._
  lazy val eid    = new eid    (this, new District_Out_8[A, B, C, D, E, F, G, Long]   {}) with District_Out_8[A, B, C, D, E, F, G, Long]   {}
  lazy val name   = new name   (this, new District_Out_8[A, B, C, D, E, F, G, String] {}) with District_Out_8[A, B, C, D, E, F, G, String] {}
  lazy val region = new region (this, new District_Out_8[A, B, C, D, E, F, G, String] {}) with District_Out_8[A, B, C, D, E, F, G, String] {}
  def apply(in: ?.type)    = new District_In_1_6[G, A, B, C, D, E, F] {}
  def apply(in: ?!.type)   = new District_In_1_7[G, A, B, C, D, E, F, G] {}
  def <(in: ?.type)        = new District_In_1_7[G, A, B, C, D, E, F, G] {}
  def contains(in: ?.type) = new District_In_1_7[G, A, B, C, D, E, F, G] {}
  def apply(m: maybe.type) = new District_Out_7[A, B, C, D, E, F, G] {}
}

trait District_Out_8[A, B, C, D, E, F, G, H] extends Out_8[A, B, C, D, E, F, G, H] 


/********* Input molecules awaiting 1 input *******************************/

trait District_In_1_0[I1] extends In_1_0[I1] {
  import District._
  lazy val eid    = new eid    (this, new District_In_1_1[I1, Long]   {}) with District_In_1_1[I1, Long]   {}
  lazy val name   = new name   (this, new District_In_1_1[I1, String] {}) with District_In_1_1[I1, String] {}
  lazy val region = new region (this, new District_In_1_1[I1, String] {}) with District_In_1_1[I1, String] {}
}

trait District_In_1_1[I1, A] extends In_1_1[I1, A] {
  import District._
  lazy val eid    = new eid    (this, new District_In_1_2[I1, A, Long]   {}) with District_In_1_2[I1, A, Long]   {}
  lazy val name   = new name   (this, new District_In_1_2[I1, A, String] {}) with District_In_1_2[I1, A, String] {}
  lazy val region = new region (this, new District_In_1_2[I1, A, String] {}) with District_In_1_2[I1, A, String] {}
  def apply(in: ?.type)    = new District_In_2_0[I1, A] {}
  def apply(in: ?!.type)   = new District_In_2_1[I1, A, A] {}
  def <(in: ?.type)        = new District_In_2_1[I1, A, A] {}
  def contains(in: ?.type) = new District_In_2_1[I1, A, A] {}
}

trait District_In_1_2[I1, A, B] extends In_1_2[I1, A, B] {
  import District._
  lazy val eid    = new eid    (this, new District_In_1_3[I1, A, B, Long]   {}) with District_In_1_3[I1, A, B, Long]   {}
  lazy val name   = new name   (this, new District_In_1_3[I1, A, B, String] {}) with District_In_1_3[I1, A, B, String] {}
  lazy val region = new region (this, new District_In_1_3[I1, A, B, String] {}) with District_In_1_3[I1, A, B, String] {}
  def apply(in: ?.type)    = new District_In_2_1[I1, B, A] {}
  def apply(in: ?!.type)   = new District_In_2_2[I1, B, A, B] {}
  def <(in: ?.type)        = new District_In_2_2[I1, B, A, B] {}
  def contains(in: ?.type) = new District_In_2_2[I1, B, A, B] {}
}

trait District_In_1_3[I1, A, B, C] extends In_1_3[I1, A, B, C] {
  import District._
  lazy val eid    = new eid    (this, new District_In_1_4[I1, A, B, C, Long]   {}) with District_In_1_4[I1, A, B, C, Long]   {}
  lazy val name   = new name   (this, new District_In_1_4[I1, A, B, C, String] {}) with District_In_1_4[I1, A, B, C, String] {}
  lazy val region = new region (this, new District_In_1_4[I1, A, B, C, String] {}) with District_In_1_4[I1, A, B, C, String] {}
  def apply(in: ?.type)    = new District_In_2_2[I1, C, A, B] {}
  def apply(in: ?!.type)   = new District_In_2_3[I1, C, A, B, C] {}
  def <(in: ?.type)        = new District_In_2_3[I1, C, A, B, C] {}
  def contains(in: ?.type) = new District_In_2_3[I1, C, A, B, C] {}
}

trait District_In_1_4[I1, A, B, C, D] extends In_1_4[I1, A, B, C, D] {
  import District._
  lazy val eid    = new eid    (this, new District_In_1_5[I1, A, B, C, D, Long]   {}) with District_In_1_5[I1, A, B, C, D, Long]   {}
  lazy val name   = new name   (this, new District_In_1_5[I1, A, B, C, D, String] {}) with District_In_1_5[I1, A, B, C, D, String] {}
  lazy val region = new region (this, new District_In_1_5[I1, A, B, C, D, String] {}) with District_In_1_5[I1, A, B, C, D, String] {}
  def apply(in: ?.type)    = new District_In_2_3[I1, D, A, B, C] {}
  def apply(in: ?!.type)   = new District_In_2_4[I1, D, A, B, C, D] {}
  def <(in: ?.type)        = new District_In_2_4[I1, D, A, B, C, D] {}
  def contains(in: ?.type) = new District_In_2_4[I1, D, A, B, C, D] {}
}

trait District_In_1_5[I1, A, B, C, D, E] extends In_1_5[I1, A, B, C, D, E] {
  import District._
  lazy val eid    = new eid    (this, new District_In_1_6[I1, A, B, C, D, E, Long]   {}) with District_In_1_6[I1, A, B, C, D, E, Long]   {}
  lazy val name   = new name   (this, new District_In_1_6[I1, A, B, C, D, E, String] {}) with District_In_1_6[I1, A, B, C, D, E, String] {}
  lazy val region = new region (this, new District_In_1_6[I1, A, B, C, D, E, String] {}) with District_In_1_6[I1, A, B, C, D, E, String] {}
  def apply(in: ?.type)    = new District_In_2_4[I1, E, A, B, C, D] {}
  def apply(in: ?!.type)   = new District_In_2_5[I1, E, A, B, C, D, E] {}
  def <(in: ?.type)        = new District_In_2_5[I1, E, A, B, C, D, E] {}
  def contains(in: ?.type) = new District_In_2_5[I1, E, A, B, C, D, E] {}
}

trait District_In_1_6[I1, A, B, C, D, E, F] extends In_1_6[I1, A, B, C, D, E, F] {
  import District._
  lazy val eid    = new eid    (this, new District_In_1_7[I1, A, B, C, D, E, F, Long]   {}) with District_In_1_7[I1, A, B, C, D, E, F, Long]   {}
  lazy val name   = new name   (this, new District_In_1_7[I1, A, B, C, D, E, F, String] {}) with District_In_1_7[I1, A, B, C, D, E, F, String] {}
  lazy val region = new region (this, new District_In_1_7[I1, A, B, C, D, E, F, String] {}) with District_In_1_7[I1, A, B, C, D, E, F, String] {}
  def apply(in: ?.type)    = new District_In_2_5[I1, F, A, B, C, D, E] {}
  def apply(in: ?!.type)   = new District_In_2_6[I1, F, A, B, C, D, E, F] {}
  def <(in: ?.type)        = new District_In_2_6[I1, F, A, B, C, D, E, F] {}
  def contains(in: ?.type) = new District_In_2_6[I1, F, A, B, C, D, E, F] {}
}

trait District_In_1_7[I1, A, B, C, D, E, F, G] extends In_1_7[I1, A, B, C, D, E, F, G] {
  import District._
  lazy val eid    = new eid    (this, new District_In_1_8[I1, A, B, C, D, E, F, G, Long]   {}) with District_In_1_8[I1, A, B, C, D, E, F, G, Long]   {}
  lazy val name   = new name   (this, new District_In_1_8[I1, A, B, C, D, E, F, G, String] {}) with District_In_1_8[I1, A, B, C, D, E, F, G, String] {}
  lazy val region = new region (this, new District_In_1_8[I1, A, B, C, D, E, F, G, String] {}) with District_In_1_8[I1, A, B, C, D, E, F, G, String] {}
  def apply(in: ?.type)    = new District_In_2_6[I1, G, A, B, C, D, E, F] {}
  def apply(in: ?!.type)   = new District_In_2_7[I1, G, A, B, C, D, E, F, G] {}
  def <(in: ?.type)        = new District_In_2_7[I1, G, A, B, C, D, E, F, G] {}
  def contains(in: ?.type) = new District_In_2_7[I1, G, A, B, C, D, E, F, G] {}
}

trait District_In_1_8[I1, A, B, C, D, E, F, G, H] extends In_1_8[I1, A, B, C, D, E, F, G, H] 


/********* Input molecules awaiting 2 inputs *******************************/

trait District_In_2_0[I1, I2] extends In_2_0[I1, I2] {
  import District._
  lazy val eid    = new eid    (this, new District_In_2_1[I1, I2, Long]   {}) with District_In_2_1[I1, I2, Long]   {}
  lazy val name   = new name   (this, new District_In_2_1[I1, I2, String] {}) with District_In_2_1[I1, I2, String] {}
  lazy val region = new region (this, new District_In_2_1[I1, I2, String] {}) with District_In_2_1[I1, I2, String] {}
}

trait District_In_2_1[I1, I2, A] extends In_2_1[I1, I2, A] {
  import District._
  lazy val eid    = new eid    (this, new District_In_2_2[I1, I2, A, Long]   {}) with District_In_2_2[I1, I2, A, Long]   {}
  lazy val name   = new name   (this, new District_In_2_2[I1, I2, A, String] {}) with District_In_2_2[I1, I2, A, String] {}
  lazy val region = new region (this, new District_In_2_2[I1, I2, A, String] {}) with District_In_2_2[I1, I2, A, String] {}
  def apply(in: ?.type)    = new District_In_3_0[I1, I2, A] {}
  def apply(in: ?!.type)   = new District_In_3_1[I1, I2, A, A] {}
  def <(in: ?.type)        = new District_In_3_1[I1, I2, A, A] {}
  def contains(in: ?.type) = new District_In_3_1[I1, I2, A, A] {}
}

trait District_In_2_2[I1, I2, A, B] extends In_2_2[I1, I2, A, B] {
  import District._
  lazy val eid    = new eid    (this, new District_In_2_3[I1, I2, A, B, Long]   {}) with District_In_2_3[I1, I2, A, B, Long]   {}
  lazy val name   = new name   (this, new District_In_2_3[I1, I2, A, B, String] {}) with District_In_2_3[I1, I2, A, B, String] {}
  lazy val region = new region (this, new District_In_2_3[I1, I2, A, B, String] {}) with District_In_2_3[I1, I2, A, B, String] {}
  def apply(in: ?.type)    = new District_In_3_1[I1, I2, B, A] {}
  def apply(in: ?!.type)   = new District_In_3_2[I1, I2, B, A, B] {}
  def <(in: ?.type)        = new District_In_3_2[I1, I2, B, A, B] {}
  def contains(in: ?.type) = new District_In_3_2[I1, I2, B, A, B] {}
}

trait District_In_2_3[I1, I2, A, B, C] extends In_2_3[I1, I2, A, B, C] {
  import District._
  lazy val eid    = new eid    (this, new District_In_2_4[I1, I2, A, B, C, Long]   {}) with District_In_2_4[I1, I2, A, B, C, Long]   {}
  lazy val name   = new name   (this, new District_In_2_4[I1, I2, A, B, C, String] {}) with District_In_2_4[I1, I2, A, B, C, String] {}
  lazy val region = new region (this, new District_In_2_4[I1, I2, A, B, C, String] {}) with District_In_2_4[I1, I2, A, B, C, String] {}
  def apply(in: ?.type)    = new District_In_3_2[I1, I2, C, A, B] {}
  def apply(in: ?!.type)   = new District_In_3_3[I1, I2, C, A, B, C] {}
  def <(in: ?.type)        = new District_In_3_3[I1, I2, C, A, B, C] {}
  def contains(in: ?.type) = new District_In_3_3[I1, I2, C, A, B, C] {}
}

trait District_In_2_4[I1, I2, A, B, C, D] extends In_2_4[I1, I2, A, B, C, D] {
  import District._
  lazy val eid    = new eid    (this, new District_In_2_5[I1, I2, A, B, C, D, Long]   {}) with District_In_2_5[I1, I2, A, B, C, D, Long]   {}
  lazy val name   = new name   (this, new District_In_2_5[I1, I2, A, B, C, D, String] {}) with District_In_2_5[I1, I2, A, B, C, D, String] {}
  lazy val region = new region (this, new District_In_2_5[I1, I2, A, B, C, D, String] {}) with District_In_2_5[I1, I2, A, B, C, D, String] {}
  def apply(in: ?.type)    = new District_In_3_3[I1, I2, D, A, B, C] {}
  def apply(in: ?!.type)   = new District_In_3_4[I1, I2, D, A, B, C, D] {}
  def <(in: ?.type)        = new District_In_3_4[I1, I2, D, A, B, C, D] {}
  def contains(in: ?.type) = new District_In_3_4[I1, I2, D, A, B, C, D] {}
}

trait District_In_2_5[I1, I2, A, B, C, D, E] extends In_2_5[I1, I2, A, B, C, D, E] {
  import District._
  lazy val eid    = new eid    (this, new District_In_2_6[I1, I2, A, B, C, D, E, Long]   {}) with District_In_2_6[I1, I2, A, B, C, D, E, Long]   {}
  lazy val name   = new name   (this, new District_In_2_6[I1, I2, A, B, C, D, E, String] {}) with District_In_2_6[I1, I2, A, B, C, D, E, String] {}
  lazy val region = new region (this, new District_In_2_6[I1, I2, A, B, C, D, E, String] {}) with District_In_2_6[I1, I2, A, B, C, D, E, String] {}
  def apply(in: ?.type)    = new District_In_3_4[I1, I2, E, A, B, C, D] {}
  def apply(in: ?!.type)   = new District_In_3_5[I1, I2, E, A, B, C, D, E] {}
  def <(in: ?.type)        = new District_In_3_5[I1, I2, E, A, B, C, D, E] {}
  def contains(in: ?.type) = new District_In_3_5[I1, I2, E, A, B, C, D, E] {}
}

trait District_In_2_6[I1, I2, A, B, C, D, E, F] extends In_2_6[I1, I2, A, B, C, D, E, F] {
  import District._
  lazy val eid    = new eid    (this, new District_In_2_7[I1, I2, A, B, C, D, E, F, Long]   {}) with District_In_2_7[I1, I2, A, B, C, D, E, F, Long]   {}
  lazy val name   = new name   (this, new District_In_2_7[I1, I2, A, B, C, D, E, F, String] {}) with District_In_2_7[I1, I2, A, B, C, D, E, F, String] {}
  lazy val region = new region (this, new District_In_2_7[I1, I2, A, B, C, D, E, F, String] {}) with District_In_2_7[I1, I2, A, B, C, D, E, F, String] {}
  def apply(in: ?.type)    = new District_In_3_5[I1, I2, F, A, B, C, D, E] {}
  def apply(in: ?!.type)   = new District_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
  def <(in: ?.type)        = new District_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
  def contains(in: ?.type) = new District_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
}

trait District_In_2_7[I1, I2, A, B, C, D, E, F, G] extends In_2_7[I1, I2, A, B, C, D, E, F, G] {
  import District._
  lazy val eid    = new eid    (this, new District_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]   {}) with District_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]   {}
  lazy val name   = new name   (this, new District_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}) with District_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}
  lazy val region = new region (this, new District_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}) with District_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}
  def apply(in: ?.type)    = new District_In_3_6[I1, I2, G, A, B, C, D, E, F] {}
  def apply(in: ?!.type)   = new District_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
  def <(in: ?.type)        = new District_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
  def contains(in: ?.type) = new District_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
}

trait District_In_2_8[I1, I2, A, B, C, D, E, F, G, H] extends In_2_8[I1, I2, A, B, C, D, E, F, G, H] 


/********* Input molecules awaiting 3 inputs *******************************/

trait District_In_3_0[I1, I2, I3] extends In_3_0[I1, I2, I3] {
  import District._
  lazy val eid    = new eid    (this, new District_In_3_1[I1, I2, I3, Long]   {}) with District_In_3_1[I1, I2, I3, Long]   {}
  lazy val name   = new name   (this, new District_In_3_1[I1, I2, I3, String] {}) with District_In_3_1[I1, I2, I3, String] {}
  lazy val region = new region (this, new District_In_3_1[I1, I2, I3, String] {}) with District_In_3_1[I1, I2, I3, String] {}
}

trait District_In_3_1[I1, I2, I3, A] extends In_3_1[I1, I2, I3, A] {
  import District._
  lazy val eid    = new eid    (this, new District_In_3_2[I1, I2, I3, A, Long]   {}) with District_In_3_2[I1, I2, I3, A, Long]   {}
  lazy val name   = new name   (this, new District_In_3_2[I1, I2, I3, A, String] {}) with District_In_3_2[I1, I2, I3, A, String] {}
  lazy val region = new region (this, new District_In_3_2[I1, I2, I3, A, String] {}) with District_In_3_2[I1, I2, I3, A, String] {}
}

trait District_In_3_2[I1, I2, I3, A, B] extends In_3_2[I1, I2, I3, A, B] {
  import District._
  lazy val eid    = new eid    (this, new District_In_3_3[I1, I2, I3, A, B, Long]   {}) with District_In_3_3[I1, I2, I3, A, B, Long]   {}
  lazy val name   = new name   (this, new District_In_3_3[I1, I2, I3, A, B, String] {}) with District_In_3_3[I1, I2, I3, A, B, String] {}
  lazy val region = new region (this, new District_In_3_3[I1, I2, I3, A, B, String] {}) with District_In_3_3[I1, I2, I3, A, B, String] {}
}

trait District_In_3_3[I1, I2, I3, A, B, C] extends In_3_3[I1, I2, I3, A, B, C] {
  import District._
  lazy val eid    = new eid    (this, new District_In_3_4[I1, I2, I3, A, B, C, Long]   {}) with District_In_3_4[I1, I2, I3, A, B, C, Long]   {}
  lazy val name   = new name   (this, new District_In_3_4[I1, I2, I3, A, B, C, String] {}) with District_In_3_4[I1, I2, I3, A, B, C, String] {}
  lazy val region = new region (this, new District_In_3_4[I1, I2, I3, A, B, C, String] {}) with District_In_3_4[I1, I2, I3, A, B, C, String] {}
}

trait District_In_3_4[I1, I2, I3, A, B, C, D] extends In_3_4[I1, I2, I3, A, B, C, D] {
  import District._
  lazy val eid    = new eid    (this, new District_In_3_5[I1, I2, I3, A, B, C, D, Long]   {}) with District_In_3_5[I1, I2, I3, A, B, C, D, Long]   {}
  lazy val name   = new name   (this, new District_In_3_5[I1, I2, I3, A, B, C, D, String] {}) with District_In_3_5[I1, I2, I3, A, B, C, D, String] {}
  lazy val region = new region (this, new District_In_3_5[I1, I2, I3, A, B, C, D, String] {}) with District_In_3_5[I1, I2, I3, A, B, C, D, String] {}
}

trait District_In_3_5[I1, I2, I3, A, B, C, D, E] extends In_3_5[I1, I2, I3, A, B, C, D, E] {
  import District._
  lazy val eid    = new eid    (this, new District_In_3_6[I1, I2, I3, A, B, C, D, E, Long]   {}) with District_In_3_6[I1, I2, I3, A, B, C, D, E, Long]   {}
  lazy val name   = new name   (this, new District_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}) with District_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}
  lazy val region = new region (this, new District_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}) with District_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}
}

trait District_In_3_6[I1, I2, I3, A, B, C, D, E, F] extends In_3_6[I1, I2, I3, A, B, C, D, E, F] {
  import District._
  lazy val eid    = new eid    (this, new District_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]   {}) with District_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]   {}
  lazy val name   = new name   (this, new District_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}) with District_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}
  lazy val region = new region (this, new District_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}) with District_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}
}

trait District_In_3_7[I1, I2, I3, A, B, C, D, E, F, G] extends In_3_7[I1, I2, I3, A, B, C, D, E, F, G] {
  import District._
  lazy val eid    = new eid    (this, new District_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]   {}) with District_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]   {}
  lazy val name   = new name   (this, new District_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}) with District_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}
  lazy val region = new region (this, new District_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}) with District_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}
}

trait District_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] extends In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]
                  