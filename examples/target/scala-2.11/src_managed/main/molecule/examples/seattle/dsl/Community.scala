/*
 * AUTO-GENERATED CODE - DO NOT CHANGE!
 *
 * Manual changes to this file will likely break molecules!
 * Instead, change the molecule definition files and recompile your project with `sbt compile`.
 */
package molecule.examples.seattle.dsl
import molecule._
import molecule.dsl.schemaDSL
import schemaDSL._
import ast.model._

object Community extends Community_Out_0 {
  class name[NS, NS2](ns: NS, ns2: NS2) extends OneString(ns, ns2) with FulltextSearch {
    def apply(data: oldNew[String]) = Community_Update()
  }

  class url[NS, NS2](ns: NS, ns2: NS2) extends OneString(ns, ns2) with FulltextSearch {
    def apply(data: oldNew[String]) = Community_Update()
  }

  class category[NS, NS2](ns: NS, ns2: NS2) extends ManyString(ns, ns2) with FulltextSearch {
    def apply(data: oldNew[String]) = Community_Update()
  }

  class orgtype[NS, NS2](ns: NS, ns2: NS2) extends OneEnum(ns, ns2) {
    private lazy val community, commercial, nonprofit, personal = EnumValue
    def apply(data: oldNew[String]) = Community_Update()
  }

  class `type`[NS, NS2](ns: NS, ns2: NS2) extends OneEnum(ns, ns2) {
    private lazy val email_list, twitter, facebook_page, blog, website, wiki, myspace, ning = EnumValue
    def apply(data: oldNew[String]) = Community_Update()
  }

  def insert = Community_Insert()
}

case class Community_Entity(elements: Seq[Element] = Seq()) extends Entity(elements) {
  lazy val eid      = _get(1, "eid"     , "Long"  )
  lazy val name     = _get(1, "name"    , "String")
  lazy val url      = _get(1, "url"     , "String")
  lazy val category = _get(2, "category", "Set[String]")
  lazy val orgtype  = _get(1, "orgtype" , "String")
  lazy val `type`   = _get(1, "type"    , "String")

  def Neighborhood = Neighborhood_Entity(elements)

  private def _get(card: Int, attr: String, tpe: String) =
    Community_Entity(elements :+ Atom("community", attr, tpe, card, NoValue))
}

case class Community_Insert(override val elements: Seq[Element] = Seq()) extends Insert(elements) {
  lazy val eid      = (data: Long  ) => _insert(Seq(data), 1, "eid"     , "Long"  )
  lazy val name     = (data: String) => _insert(Seq(data), 1, "name"    , "String")
  lazy val url      = (data: String) => _insert(Seq(data), 1, "url"     , "String")
  lazy val category = category_
  lazy val orgtype  = (data: String) => _insert(Seq(data), 1, "orgtype" , "String", Some(":community.orgtype/"))
  lazy val `type`   = (data: String) => _insert(Seq(data), 1, "type"    , "String", Some(":community.type/"))

  private[molecule] object category_ {
    def apply(h: String, t: String*) = _insert(h +: t.toList, 2, "category", "Set[String]")
    def apply(data: Seq[String])     = _insert(data,          2, "category", "Set[String]")
  }

  def Neighborhood = Neighborhood_Insert(elements)

  private def _insert(data: Seq[Any], card: Int, attr: String, tpe: String, enumPrefix: Option[String] = None) =
    Community_Insert(elements :+ Atom("community", attr, tpe, card, Eq(data.map(_.toString)), enumPrefix))
}

case class Community_Update(override val elements: Seq[Element] = Seq(), override val ids: Seq[Long] = Seq()) extends Update(elements, ids) {
  lazy val eid      = eid_
  lazy val name     = name_
  lazy val url      = url_
  lazy val category = category_
  lazy val orgtype  = orgtype_
  lazy val `type`   = type_

  private[molecule] object eid_ {
    def apply(data: Long)   = _assertNewFact(Seq(data), 1, "eid", "Long")
    def apply()             = _retract(                 1, "eid")
  }
  private[molecule] object name_ {
    def apply(data: String) = _assertNewFact(Seq(data), 1, "name", "String")
    def apply()             = _retract(                 1, "name")
  }
  private[molecule] object url_ {
    def apply(data: String) = _assertNewFact(Seq(data), 1, "url", "String")
    def apply()             = _retract(                 1, "url")
  }
  private[molecule] object category_ {
    def add(data: String)                                = _assertNewFact(Seq(data),     2, "category", "Set[String]")
    def apply(h: (String, String), t: (String, String)*) = _swap(h +: t.toList            , "category", "Set[String]")
    def remove(values: String*)                          = _removeElements(Seq(values: _*), "category", "Set[String]")
    def apply()                                          = _retract(                     2, "category")
  }
  private[molecule] object orgtype_ {
    def apply(data: String) = _assertNewFact(Seq(data), 1, "orgtype", "String", Some(":community.orgtype/"))
    def apply()             = _retract(                 1, "orgtype")
  }
  private[molecule] object type_ {
    def apply(data: String) = _assertNewFact(Seq(data), 1, "type", "String", Some(":community.type/"))
    def apply()             = _retract(                 1, "type")
  }

  def Neighborhood = Neighborhood_Update(elements)

  private def _assertNewFact(data: Seq[Any], card: Int, attr: String, tpe: String, enumPrefix: Option[String] = None) =
    Community_Update(elements :+ Atom("community", attr, tpe, card, Eq(data.map(_.toString)), enumPrefix), ids)

  private def _swap(oldNew: Seq[(String, String)], attr: String, tpe: String, enumPrefix: Option[String] = None) =
    Community_Update(elements :+ Atom("community", attr, tpe, 2, Replace(oldNew.toMap), enumPrefix), ids)

  private def _removeElements(values: Seq[String], attr: String, tpe: String, enumPrefix: Option[String] = None) =
    Community_Update(elements :+ Atom("community", attr, tpe, 2, Remove(values), enumPrefix), ids)

  private def _retract(card: Int, attr: String) =
    Community_Update(elements :+ Atom("community", attr, "", card, Remove(Seq())), ids)
}

case class Community_Retract(elements: Seq[Element] = Seq()) extends Retract(elements) {
  lazy val eid      = _retract(1, "eid"     , "Long"  )
  lazy val name     = _retract(1, "name"    , "String")
  lazy val url      = _retract(1, "url"     , "String")
  lazy val category = category_
  lazy val orgtype  = _retract(1, "orgtype" , "String")
  lazy val `type`   = _retract(1, "type"    , "String")

  private[molecule] object category_ {
    def apply()                      = _retract(2, "category", "Set[String]")
    def apply(h: String, t: String*) = _retract(2, "category", "Set[String]")
    def apply(data: Seq[String])     = _retract(2, "category", "Set[String]")
  }

  def Neighborhood = Neighborhood_Retract(elements)

  private def _retract(card: Int, attr: String, tpe: String, data: Seq[Any] = Seq()) =
    Community_Retract(elements :+ Atom("community", attr, tpe, card, Eq(data.map(_.toString))))
}

//********* Output molecules *******************************/

 trait Community_Out_0 extends Out_0 {
  import Community._
  
  @default
  def apply(name: String) = new name(this, new Community_Out_1[String] {}) with Community_Out_1[String]
  def apply(id: Long) = Community_Entity()

  def update(id: Long) = Community_Update(Seq(), Seq(id))
  def update(ids: Seq[Long]) = Community_Update(Seq(), ids)

  lazy val eid      = new eid      (this, new Community_Out_1[Long]        {}) with Community_Out_1[Long]        {}
  lazy val name     = new name     (this, new Community_Out_1[String]      {}) with Community_Out_1[String]      {}
  lazy val url      = new url      (this, new Community_Out_1[String]      {}) with Community_Out_1[String]      {}
  lazy val category = new category (this, new Community_Out_1[Set[String]] {}) with Community_Out_1[Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_1[String]      {}) with Community_Out_1[String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_1[String]      {}) with Community_Out_1[String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_0
}

trait Community_Out_1[A] extends Out_1[A] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_2[A, Long]        {}) with Community_Out_2[A, Long]        {}
  lazy val name     = new name     (this, new Community_Out_2[A, String]      {}) with Community_Out_2[A, String]      {}
  lazy val url      = new url      (this, new Community_Out_2[A, String]      {}) with Community_Out_2[A, String]      {}
  lazy val category = new category (this, new Community_Out_2[A, Set[String]] {}) with Community_Out_2[A, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_2[A, String]      {}) with Community_Out_2[A, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_2[A, String]      {}) with Community_Out_2[A, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_1[A]
  def apply(in: ?.type)    = new Community_In_1_0[A] {}
  def apply(in: ?!.type)   = new Community_In_1_1[A, A] {}
  def <(in: ?.type)        = new Community_In_1_1[A, A] {}
  def contains(in: ?.type) = new Community_In_1_1[A, A] {}
  def apply(m: maybe.type) = new Community_Out_1[A] {}
}

trait Community_Out_2[A, B] extends Out_2[A, B] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_3[A, B, Long]        {}) with Community_Out_3[A, B, Long]        {}
  lazy val name     = new name     (this, new Community_Out_3[A, B, String]      {}) with Community_Out_3[A, B, String]      {}
  lazy val url      = new url      (this, new Community_Out_3[A, B, String]      {}) with Community_Out_3[A, B, String]      {}
  lazy val category = new category (this, new Community_Out_3[A, B, Set[String]] {}) with Community_Out_3[A, B, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_3[A, B, String]      {}) with Community_Out_3[A, B, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_3[A, B, String]      {}) with Community_Out_3[A, B, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_2[A, B]
  def apply(in: ?.type)    = new Community_In_1_1[B, A] {}
  def apply(in: ?!.type)   = new Community_In_1_2[B, A, B] {}
  def <(in: ?.type)        = new Community_In_1_2[B, A, B] {}
  def contains(in: ?.type) = new Community_In_1_2[B, A, B] {}
  def apply(m: maybe.type) = new Community_Out_2[A, B] {}
}

trait Community_Out_3[A, B, C] extends Out_3[A, B, C] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_4[A, B, C, Long]        {}) with Community_Out_4[A, B, C, Long]        {}
  lazy val name     = new name     (this, new Community_Out_4[A, B, C, String]      {}) with Community_Out_4[A, B, C, String]      {}
  lazy val url      = new url      (this, new Community_Out_4[A, B, C, String]      {}) with Community_Out_4[A, B, C, String]      {}
  lazy val category = new category (this, new Community_Out_4[A, B, C, Set[String]] {}) with Community_Out_4[A, B, C, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_4[A, B, C, String]      {}) with Community_Out_4[A, B, C, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_4[A, B, C, String]      {}) with Community_Out_4[A, B, C, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_3[A, B, C]
  def apply(in: ?.type)    = new Community_In_1_2[C, A, B] {}
  def apply(in: ?!.type)   = new Community_In_1_3[C, A, B, C] {}
  def <(in: ?.type)        = new Community_In_1_3[C, A, B, C] {}
  def contains(in: ?.type) = new Community_In_1_3[C, A, B, C] {}
  def apply(m: maybe.type) = new Community_Out_3[A, B, C] {}
}

trait Community_Out_4[A, B, C, D] extends Out_4[A, B, C, D] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_5[A, B, C, D, Long]        {}) with Community_Out_5[A, B, C, D, Long]        {}
  lazy val name     = new name     (this, new Community_Out_5[A, B, C, D, String]      {}) with Community_Out_5[A, B, C, D, String]      {}
  lazy val url      = new url      (this, new Community_Out_5[A, B, C, D, String]      {}) with Community_Out_5[A, B, C, D, String]      {}
  lazy val category = new category (this, new Community_Out_5[A, B, C, D, Set[String]] {}) with Community_Out_5[A, B, C, D, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_5[A, B, C, D, String]      {}) with Community_Out_5[A, B, C, D, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_5[A, B, C, D, String]      {}) with Community_Out_5[A, B, C, D, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_4[A, B, C, D]
  def apply(in: ?.type)    = new Community_In_1_3[D, A, B, C] {}
  def apply(in: ?!.type)   = new Community_In_1_4[D, A, B, C, D] {}
  def <(in: ?.type)        = new Community_In_1_4[D, A, B, C, D] {}
  def contains(in: ?.type) = new Community_In_1_4[D, A, B, C, D] {}
  def apply(m: maybe.type) = new Community_Out_4[A, B, C, D] {}
}

trait Community_Out_5[A, B, C, D, E] extends Out_5[A, B, C, D, E] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_6[A, B, C, D, E, Long]        {}) with Community_Out_6[A, B, C, D, E, Long]        {}
  lazy val name     = new name     (this, new Community_Out_6[A, B, C, D, E, String]      {}) with Community_Out_6[A, B, C, D, E, String]      {}
  lazy val url      = new url      (this, new Community_Out_6[A, B, C, D, E, String]      {}) with Community_Out_6[A, B, C, D, E, String]      {}
  lazy val category = new category (this, new Community_Out_6[A, B, C, D, E, Set[String]] {}) with Community_Out_6[A, B, C, D, E, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_6[A, B, C, D, E, String]      {}) with Community_Out_6[A, B, C, D, E, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_6[A, B, C, D, E, String]      {}) with Community_Out_6[A, B, C, D, E, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_5[A, B, C, D, E]
  def apply(in: ?.type)    = new Community_In_1_4[E, A, B, C, D] {}
  def apply(in: ?!.type)   = new Community_In_1_5[E, A, B, C, D, E] {}
  def <(in: ?.type)        = new Community_In_1_5[E, A, B, C, D, E] {}
  def contains(in: ?.type) = new Community_In_1_5[E, A, B, C, D, E] {}
  def apply(m: maybe.type) = new Community_Out_5[A, B, C, D, E] {}
}

trait Community_Out_6[A, B, C, D, E, F] extends Out_6[A, B, C, D, E, F] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_7[A, B, C, D, E, F, Long]        {}) with Community_Out_7[A, B, C, D, E, F, Long]        {}
  lazy val name     = new name     (this, new Community_Out_7[A, B, C, D, E, F, String]      {}) with Community_Out_7[A, B, C, D, E, F, String]      {}
  lazy val url      = new url      (this, new Community_Out_7[A, B, C, D, E, F, String]      {}) with Community_Out_7[A, B, C, D, E, F, String]      {}
  lazy val category = new category (this, new Community_Out_7[A, B, C, D, E, F, Set[String]] {}) with Community_Out_7[A, B, C, D, E, F, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_7[A, B, C, D, E, F, String]      {}) with Community_Out_7[A, B, C, D, E, F, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_7[A, B, C, D, E, F, String]      {}) with Community_Out_7[A, B, C, D, E, F, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_6[A, B, C, D, E, F]
  def apply(in: ?.type)    = new Community_In_1_5[F, A, B, C, D, E] {}
  def apply(in: ?!.type)   = new Community_In_1_6[F, A, B, C, D, E, F] {}
  def <(in: ?.type)        = new Community_In_1_6[F, A, B, C, D, E, F] {}
  def contains(in: ?.type) = new Community_In_1_6[F, A, B, C, D, E, F] {}
  def apply(m: maybe.type) = new Community_Out_6[A, B, C, D, E, F] {}
}

trait Community_Out_7[A, B, C, D, E, F, G] extends Out_7[A, B, C, D, E, F, G] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_8[A, B, C, D, E, F, G, Long]        {}) with Community_Out_8[A, B, C, D, E, F, G, Long]        {}
  lazy val name     = new name     (this, new Community_Out_8[A, B, C, D, E, F, G, String]      {}) with Community_Out_8[A, B, C, D, E, F, G, String]      {}
  lazy val url      = new url      (this, new Community_Out_8[A, B, C, D, E, F, G, String]      {}) with Community_Out_8[A, B, C, D, E, F, G, String]      {}
  lazy val category = new category (this, new Community_Out_8[A, B, C, D, E, F, G, Set[String]] {}) with Community_Out_8[A, B, C, D, E, F, G, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_8[A, B, C, D, E, F, G, String]      {}) with Community_Out_8[A, B, C, D, E, F, G, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_8[A, B, C, D, E, F, G, String]      {}) with Community_Out_8[A, B, C, D, E, F, G, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_7[A, B, C, D, E, F, G]
  def apply(in: ?.type)    = new Community_In_1_6[G, A, B, C, D, E, F] {}
  def apply(in: ?!.type)   = new Community_In_1_7[G, A, B, C, D, E, F, G] {}
  def <(in: ?.type)        = new Community_In_1_7[G, A, B, C, D, E, F, G] {}
  def contains(in: ?.type) = new Community_In_1_7[G, A, B, C, D, E, F, G] {}
  def apply(m: maybe.type) = new Community_Out_7[A, B, C, D, E, F, G] {}
}

trait Community_Out_8[A, B, C, D, E, F, G, H] extends Out_8[A, B, C, D, E, F, G, H] 


/********* Input molecules awaiting 1 input *******************************/

trait Community_In_1_0[I1] extends In_1_0[I1] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_1[I1, Long]        {}) with Community_In_1_1[I1, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_1[I1, String]      {}) with Community_In_1_1[I1, String]      {}
  lazy val url      = new url      (this, new Community_In_1_1[I1, String]      {}) with Community_In_1_1[I1, String]      {}
  lazy val category = new category (this, new Community_In_1_1[I1, Set[String]] {}) with Community_In_1_1[I1, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_1[I1, String]      {}) with Community_In_1_1[I1, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_1[I1, String]      {}) with Community_In_1_1[I1, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_0[I1]
}

trait Community_In_1_1[I1, A] extends In_1_1[I1, A] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_2[I1, A, Long]        {}) with Community_In_1_2[I1, A, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_2[I1, A, String]      {}) with Community_In_1_2[I1, A, String]      {}
  lazy val url      = new url      (this, new Community_In_1_2[I1, A, String]      {}) with Community_In_1_2[I1, A, String]      {}
  lazy val category = new category (this, new Community_In_1_2[I1, A, Set[String]] {}) with Community_In_1_2[I1, A, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_2[I1, A, String]      {}) with Community_In_1_2[I1, A, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_2[I1, A, String]      {}) with Community_In_1_2[I1, A, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_1[I1, A]
  def apply(in: ?.type)    = new Community_In_2_0[I1, A] {}
  def apply(in: ?!.type)   = new Community_In_2_1[I1, A, A] {}
  def <(in: ?.type)        = new Community_In_2_1[I1, A, A] {}
  def contains(in: ?.type) = new Community_In_2_1[I1, A, A] {}
}

trait Community_In_1_2[I1, A, B] extends In_1_2[I1, A, B] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_3[I1, A, B, Long]        {}) with Community_In_1_3[I1, A, B, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_3[I1, A, B, String]      {}) with Community_In_1_3[I1, A, B, String]      {}
  lazy val url      = new url      (this, new Community_In_1_3[I1, A, B, String]      {}) with Community_In_1_3[I1, A, B, String]      {}
  lazy val category = new category (this, new Community_In_1_3[I1, A, B, Set[String]] {}) with Community_In_1_3[I1, A, B, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_3[I1, A, B, String]      {}) with Community_In_1_3[I1, A, B, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_3[I1, A, B, String]      {}) with Community_In_1_3[I1, A, B, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_2[I1, A, B]
  def apply(in: ?.type)    = new Community_In_2_1[I1, B, A] {}
  def apply(in: ?!.type)   = new Community_In_2_2[I1, B, A, B] {}
  def <(in: ?.type)        = new Community_In_2_2[I1, B, A, B] {}
  def contains(in: ?.type) = new Community_In_2_2[I1, B, A, B] {}
}

trait Community_In_1_3[I1, A, B, C] extends In_1_3[I1, A, B, C] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_4[I1, A, B, C, Long]        {}) with Community_In_1_4[I1, A, B, C, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_4[I1, A, B, C, String]      {}) with Community_In_1_4[I1, A, B, C, String]      {}
  lazy val url      = new url      (this, new Community_In_1_4[I1, A, B, C, String]      {}) with Community_In_1_4[I1, A, B, C, String]      {}
  lazy val category = new category (this, new Community_In_1_4[I1, A, B, C, Set[String]] {}) with Community_In_1_4[I1, A, B, C, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_4[I1, A, B, C, String]      {}) with Community_In_1_4[I1, A, B, C, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_4[I1, A, B, C, String]      {}) with Community_In_1_4[I1, A, B, C, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_3[I1, A, B, C]
  def apply(in: ?.type)    = new Community_In_2_2[I1, C, A, B] {}
  def apply(in: ?!.type)   = new Community_In_2_3[I1, C, A, B, C] {}
  def <(in: ?.type)        = new Community_In_2_3[I1, C, A, B, C] {}
  def contains(in: ?.type) = new Community_In_2_3[I1, C, A, B, C] {}
}

trait Community_In_1_4[I1, A, B, C, D] extends In_1_4[I1, A, B, C, D] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_5[I1, A, B, C, D, Long]        {}) with Community_In_1_5[I1, A, B, C, D, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_5[I1, A, B, C, D, String]      {}) with Community_In_1_5[I1, A, B, C, D, String]      {}
  lazy val url      = new url      (this, new Community_In_1_5[I1, A, B, C, D, String]      {}) with Community_In_1_5[I1, A, B, C, D, String]      {}
  lazy val category = new category (this, new Community_In_1_5[I1, A, B, C, D, Set[String]] {}) with Community_In_1_5[I1, A, B, C, D, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_5[I1, A, B, C, D, String]      {}) with Community_In_1_5[I1, A, B, C, D, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_5[I1, A, B, C, D, String]      {}) with Community_In_1_5[I1, A, B, C, D, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_4[I1, A, B, C, D]
  def apply(in: ?.type)    = new Community_In_2_3[I1, D, A, B, C] {}
  def apply(in: ?!.type)   = new Community_In_2_4[I1, D, A, B, C, D] {}
  def <(in: ?.type)        = new Community_In_2_4[I1, D, A, B, C, D] {}
  def contains(in: ?.type) = new Community_In_2_4[I1, D, A, B, C, D] {}
}

trait Community_In_1_5[I1, A, B, C, D, E] extends In_1_5[I1, A, B, C, D, E] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_6[I1, A, B, C, D, E, Long]        {}) with Community_In_1_6[I1, A, B, C, D, E, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_6[I1, A, B, C, D, E, String]      {}) with Community_In_1_6[I1, A, B, C, D, E, String]      {}
  lazy val url      = new url      (this, new Community_In_1_6[I1, A, B, C, D, E, String]      {}) with Community_In_1_6[I1, A, B, C, D, E, String]      {}
  lazy val category = new category (this, new Community_In_1_6[I1, A, B, C, D, E, Set[String]] {}) with Community_In_1_6[I1, A, B, C, D, E, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_6[I1, A, B, C, D, E, String]      {}) with Community_In_1_6[I1, A, B, C, D, E, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_6[I1, A, B, C, D, E, String]      {}) with Community_In_1_6[I1, A, B, C, D, E, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_5[I1, A, B, C, D, E]
  def apply(in: ?.type)    = new Community_In_2_4[I1, E, A, B, C, D] {}
  def apply(in: ?!.type)   = new Community_In_2_5[I1, E, A, B, C, D, E] {}
  def <(in: ?.type)        = new Community_In_2_5[I1, E, A, B, C, D, E] {}
  def contains(in: ?.type) = new Community_In_2_5[I1, E, A, B, C, D, E] {}
}

trait Community_In_1_6[I1, A, B, C, D, E, F] extends In_1_6[I1, A, B, C, D, E, F] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_7[I1, A, B, C, D, E, F, Long]        {}) with Community_In_1_7[I1, A, B, C, D, E, F, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_7[I1, A, B, C, D, E, F, String]      {}) with Community_In_1_7[I1, A, B, C, D, E, F, String]      {}
  lazy val url      = new url      (this, new Community_In_1_7[I1, A, B, C, D, E, F, String]      {}) with Community_In_1_7[I1, A, B, C, D, E, F, String]      {}
  lazy val category = new category (this, new Community_In_1_7[I1, A, B, C, D, E, F, Set[String]] {}) with Community_In_1_7[I1, A, B, C, D, E, F, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_7[I1, A, B, C, D, E, F, String]      {}) with Community_In_1_7[I1, A, B, C, D, E, F, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_7[I1, A, B, C, D, E, F, String]      {}) with Community_In_1_7[I1, A, B, C, D, E, F, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_6[I1, A, B, C, D, E, F]
  def apply(in: ?.type)    = new Community_In_2_5[I1, F, A, B, C, D, E] {}
  def apply(in: ?!.type)   = new Community_In_2_6[I1, F, A, B, C, D, E, F] {}
  def <(in: ?.type)        = new Community_In_2_6[I1, F, A, B, C, D, E, F] {}
  def contains(in: ?.type) = new Community_In_2_6[I1, F, A, B, C, D, E, F] {}
}

trait Community_In_1_7[I1, A, B, C, D, E, F, G] extends In_1_7[I1, A, B, C, D, E, F, G] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_8[I1, A, B, C, D, E, F, G, Long]        {}) with Community_In_1_8[I1, A, B, C, D, E, F, G, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_8[I1, A, B, C, D, E, F, G, String]      {}) with Community_In_1_8[I1, A, B, C, D, E, F, G, String]      {}
  lazy val url      = new url      (this, new Community_In_1_8[I1, A, B, C, D, E, F, G, String]      {}) with Community_In_1_8[I1, A, B, C, D, E, F, G, String]      {}
  lazy val category = new category (this, new Community_In_1_8[I1, A, B, C, D, E, F, G, Set[String]] {}) with Community_In_1_8[I1, A, B, C, D, E, F, G, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_8[I1, A, B, C, D, E, F, G, String]      {}) with Community_In_1_8[I1, A, B, C, D, E, F, G, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_8[I1, A, B, C, D, E, F, G, String]      {}) with Community_In_1_8[I1, A, B, C, D, E, F, G, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_7[I1, A, B, C, D, E, F, G]
  def apply(in: ?.type)    = new Community_In_2_6[I1, G, A, B, C, D, E, F] {}
  def apply(in: ?!.type)   = new Community_In_2_7[I1, G, A, B, C, D, E, F, G] {}
  def <(in: ?.type)        = new Community_In_2_7[I1, G, A, B, C, D, E, F, G] {}
  def contains(in: ?.type) = new Community_In_2_7[I1, G, A, B, C, D, E, F, G] {}
}

trait Community_In_1_8[I1, A, B, C, D, E, F, G, H] extends In_1_8[I1, A, B, C, D, E, F, G, H] 


/********* Input molecules awaiting 2 inputs *******************************/

trait Community_In_2_0[I1, I2] extends In_2_0[I1, I2] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_1[I1, I2, Long]        {}) with Community_In_2_1[I1, I2, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_1[I1, I2, String]      {}) with Community_In_2_1[I1, I2, String]      {}
  lazy val url      = new url      (this, new Community_In_2_1[I1, I2, String]      {}) with Community_In_2_1[I1, I2, String]      {}
  lazy val category = new category (this, new Community_In_2_1[I1, I2, Set[String]] {}) with Community_In_2_1[I1, I2, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_1[I1, I2, String]      {}) with Community_In_2_1[I1, I2, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_1[I1, I2, String]      {}) with Community_In_2_1[I1, I2, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_0[I1, I2]
}

trait Community_In_2_1[I1, I2, A] extends In_2_1[I1, I2, A] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_2[I1, I2, A, Long]        {}) with Community_In_2_2[I1, I2, A, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_2[I1, I2, A, String]      {}) with Community_In_2_2[I1, I2, A, String]      {}
  lazy val url      = new url      (this, new Community_In_2_2[I1, I2, A, String]      {}) with Community_In_2_2[I1, I2, A, String]      {}
  lazy val category = new category (this, new Community_In_2_2[I1, I2, A, Set[String]] {}) with Community_In_2_2[I1, I2, A, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_2[I1, I2, A, String]      {}) with Community_In_2_2[I1, I2, A, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_2[I1, I2, A, String]      {}) with Community_In_2_2[I1, I2, A, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_1[I1, I2, A]
  def apply(in: ?.type)    = new Community_In_3_0[I1, I2, A] {}
  def apply(in: ?!.type)   = new Community_In_3_1[I1, I2, A, A] {}
  def <(in: ?.type)        = new Community_In_3_1[I1, I2, A, A] {}
  def contains(in: ?.type) = new Community_In_3_1[I1, I2, A, A] {}
}

trait Community_In_2_2[I1, I2, A, B] extends In_2_2[I1, I2, A, B] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_3[I1, I2, A, B, Long]        {}) with Community_In_2_3[I1, I2, A, B, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_3[I1, I2, A, B, String]      {}) with Community_In_2_3[I1, I2, A, B, String]      {}
  lazy val url      = new url      (this, new Community_In_2_3[I1, I2, A, B, String]      {}) with Community_In_2_3[I1, I2, A, B, String]      {}
  lazy val category = new category (this, new Community_In_2_3[I1, I2, A, B, Set[String]] {}) with Community_In_2_3[I1, I2, A, B, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_3[I1, I2, A, B, String]      {}) with Community_In_2_3[I1, I2, A, B, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_3[I1, I2, A, B, String]      {}) with Community_In_2_3[I1, I2, A, B, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_2[I1, I2, A, B]
  def apply(in: ?.type)    = new Community_In_3_1[I1, I2, B, A] {}
  def apply(in: ?!.type)   = new Community_In_3_2[I1, I2, B, A, B] {}
  def <(in: ?.type)        = new Community_In_3_2[I1, I2, B, A, B] {}
  def contains(in: ?.type) = new Community_In_3_2[I1, I2, B, A, B] {}
}

trait Community_In_2_3[I1, I2, A, B, C] extends In_2_3[I1, I2, A, B, C] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_4[I1, I2, A, B, C, Long]        {}) with Community_In_2_4[I1, I2, A, B, C, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_4[I1, I2, A, B, C, String]      {}) with Community_In_2_4[I1, I2, A, B, C, String]      {}
  lazy val url      = new url      (this, new Community_In_2_4[I1, I2, A, B, C, String]      {}) with Community_In_2_4[I1, I2, A, B, C, String]      {}
  lazy val category = new category (this, new Community_In_2_4[I1, I2, A, B, C, Set[String]] {}) with Community_In_2_4[I1, I2, A, B, C, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_4[I1, I2, A, B, C, String]      {}) with Community_In_2_4[I1, I2, A, B, C, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_4[I1, I2, A, B, C, String]      {}) with Community_In_2_4[I1, I2, A, B, C, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_3[I1, I2, A, B, C]
  def apply(in: ?.type)    = new Community_In_3_2[I1, I2, C, A, B] {}
  def apply(in: ?!.type)   = new Community_In_3_3[I1, I2, C, A, B, C] {}
  def <(in: ?.type)        = new Community_In_3_3[I1, I2, C, A, B, C] {}
  def contains(in: ?.type) = new Community_In_3_3[I1, I2, C, A, B, C] {}
}

trait Community_In_2_4[I1, I2, A, B, C, D] extends In_2_4[I1, I2, A, B, C, D] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_5[I1, I2, A, B, C, D, Long]        {}) with Community_In_2_5[I1, I2, A, B, C, D, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_5[I1, I2, A, B, C, D, String]      {}) with Community_In_2_5[I1, I2, A, B, C, D, String]      {}
  lazy val url      = new url      (this, new Community_In_2_5[I1, I2, A, B, C, D, String]      {}) with Community_In_2_5[I1, I2, A, B, C, D, String]      {}
  lazy val category = new category (this, new Community_In_2_5[I1, I2, A, B, C, D, Set[String]] {}) with Community_In_2_5[I1, I2, A, B, C, D, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_5[I1, I2, A, B, C, D, String]      {}) with Community_In_2_5[I1, I2, A, B, C, D, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_5[I1, I2, A, B, C, D, String]      {}) with Community_In_2_5[I1, I2, A, B, C, D, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_4[I1, I2, A, B, C, D]
  def apply(in: ?.type)    = new Community_In_3_3[I1, I2, D, A, B, C] {}
  def apply(in: ?!.type)   = new Community_In_3_4[I1, I2, D, A, B, C, D] {}
  def <(in: ?.type)        = new Community_In_3_4[I1, I2, D, A, B, C, D] {}
  def contains(in: ?.type) = new Community_In_3_4[I1, I2, D, A, B, C, D] {}
}

trait Community_In_2_5[I1, I2, A, B, C, D, E] extends In_2_5[I1, I2, A, B, C, D, E] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_6[I1, I2, A, B, C, D, E, Long]        {}) with Community_In_2_6[I1, I2, A, B, C, D, E, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_6[I1, I2, A, B, C, D, E, String]      {}) with Community_In_2_6[I1, I2, A, B, C, D, E, String]      {}
  lazy val url      = new url      (this, new Community_In_2_6[I1, I2, A, B, C, D, E, String]      {}) with Community_In_2_6[I1, I2, A, B, C, D, E, String]      {}
  lazy val category = new category (this, new Community_In_2_6[I1, I2, A, B, C, D, E, Set[String]] {}) with Community_In_2_6[I1, I2, A, B, C, D, E, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_6[I1, I2, A, B, C, D, E, String]      {}) with Community_In_2_6[I1, I2, A, B, C, D, E, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_6[I1, I2, A, B, C, D, E, String]      {}) with Community_In_2_6[I1, I2, A, B, C, D, E, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_5[I1, I2, A, B, C, D, E]
  def apply(in: ?.type)    = new Community_In_3_4[I1, I2, E, A, B, C, D] {}
  def apply(in: ?!.type)   = new Community_In_3_5[I1, I2, E, A, B, C, D, E] {}
  def <(in: ?.type)        = new Community_In_3_5[I1, I2, E, A, B, C, D, E] {}
  def contains(in: ?.type) = new Community_In_3_5[I1, I2, E, A, B, C, D, E] {}
}

trait Community_In_2_6[I1, I2, A, B, C, D, E, F] extends In_2_6[I1, I2, A, B, C, D, E, F] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_7[I1, I2, A, B, C, D, E, F, Long]        {}) with Community_In_2_7[I1, I2, A, B, C, D, E, F, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      {}) with Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      {}
  lazy val url      = new url      (this, new Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      {}) with Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      {}
  lazy val category = new category (this, new Community_In_2_7[I1, I2, A, B, C, D, E, F, Set[String]] {}) with Community_In_2_7[I1, I2, A, B, C, D, E, F, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      {}) with Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      {}) with Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_6[I1, I2, A, B, C, D, E, F]
  def apply(in: ?.type)    = new Community_In_3_5[I1, I2, F, A, B, C, D, E] {}
  def apply(in: ?!.type)   = new Community_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
  def <(in: ?.type)        = new Community_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
  def contains(in: ?.type) = new Community_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
}

trait Community_In_2_7[I1, I2, A, B, C, D, E, F, G] extends In_2_7[I1, I2, A, B, C, D, E, F, G] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]        {}) with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      {}) with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      {}
  lazy val url      = new url      (this, new Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      {}) with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      {}
  lazy val category = new category (this, new Community_In_2_8[I1, I2, A, B, C, D, E, F, G, Set[String]] {}) with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      {}) with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      {}) with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_7[I1, I2, A, B, C, D, E, F, G]
  def apply(in: ?.type)    = new Community_In_3_6[I1, I2, G, A, B, C, D, E, F] {}
  def apply(in: ?!.type)   = new Community_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
  def <(in: ?.type)        = new Community_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
  def contains(in: ?.type) = new Community_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
}

trait Community_In_2_8[I1, I2, A, B, C, D, E, F, G, H] extends In_2_8[I1, I2, A, B, C, D, E, F, G, H] 


/********* Input molecules awaiting 3 inputs *******************************/

trait Community_In_3_0[I1, I2, I3] extends In_3_0[I1, I2, I3] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_1[I1, I2, I3, Long]        {}) with Community_In_3_1[I1, I2, I3, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_1[I1, I2, I3, String]      {}) with Community_In_3_1[I1, I2, I3, String]      {}
  lazy val url      = new url      (this, new Community_In_3_1[I1, I2, I3, String]      {}) with Community_In_3_1[I1, I2, I3, String]      {}
  lazy val category = new category (this, new Community_In_3_1[I1, I2, I3, Set[String]] {}) with Community_In_3_1[I1, I2, I3, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_1[I1, I2, I3, String]      {}) with Community_In_3_1[I1, I2, I3, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_1[I1, I2, I3, String]      {}) with Community_In_3_1[I1, I2, I3, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_0[I1, I2, I3]
}

trait Community_In_3_1[I1, I2, I3, A] extends In_3_1[I1, I2, I3, A] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_2[I1, I2, I3, A, Long]        {}) with Community_In_3_2[I1, I2, I3, A, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_2[I1, I2, I3, A, String]      {}) with Community_In_3_2[I1, I2, I3, A, String]      {}
  lazy val url      = new url      (this, new Community_In_3_2[I1, I2, I3, A, String]      {}) with Community_In_3_2[I1, I2, I3, A, String]      {}
  lazy val category = new category (this, new Community_In_3_2[I1, I2, I3, A, Set[String]] {}) with Community_In_3_2[I1, I2, I3, A, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_2[I1, I2, I3, A, String]      {}) with Community_In_3_2[I1, I2, I3, A, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_2[I1, I2, I3, A, String]      {}) with Community_In_3_2[I1, I2, I3, A, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_1[I1, I2, I3, A]
}

trait Community_In_3_2[I1, I2, I3, A, B] extends In_3_2[I1, I2, I3, A, B] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_3[I1, I2, I3, A, B, Long]        {}) with Community_In_3_3[I1, I2, I3, A, B, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_3[I1, I2, I3, A, B, String]      {}) with Community_In_3_3[I1, I2, I3, A, B, String]      {}
  lazy val url      = new url      (this, new Community_In_3_3[I1, I2, I3, A, B, String]      {}) with Community_In_3_3[I1, I2, I3, A, B, String]      {}
  lazy val category = new category (this, new Community_In_3_3[I1, I2, I3, A, B, Set[String]] {}) with Community_In_3_3[I1, I2, I3, A, B, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_3[I1, I2, I3, A, B, String]      {}) with Community_In_3_3[I1, I2, I3, A, B, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_3[I1, I2, I3, A, B, String]      {}) with Community_In_3_3[I1, I2, I3, A, B, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_2[I1, I2, I3, A, B]
}

trait Community_In_3_3[I1, I2, I3, A, B, C] extends In_3_3[I1, I2, I3, A, B, C] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_4[I1, I2, I3, A, B, C, Long]        {}) with Community_In_3_4[I1, I2, I3, A, B, C, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_4[I1, I2, I3, A, B, C, String]      {}) with Community_In_3_4[I1, I2, I3, A, B, C, String]      {}
  lazy val url      = new url      (this, new Community_In_3_4[I1, I2, I3, A, B, C, String]      {}) with Community_In_3_4[I1, I2, I3, A, B, C, String]      {}
  lazy val category = new category (this, new Community_In_3_4[I1, I2, I3, A, B, C, Set[String]] {}) with Community_In_3_4[I1, I2, I3, A, B, C, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_4[I1, I2, I3, A, B, C, String]      {}) with Community_In_3_4[I1, I2, I3, A, B, C, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_4[I1, I2, I3, A, B, C, String]      {}) with Community_In_3_4[I1, I2, I3, A, B, C, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_3[I1, I2, I3, A, B, C]
}

trait Community_In_3_4[I1, I2, I3, A, B, C, D] extends In_3_4[I1, I2, I3, A, B, C, D] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_5[I1, I2, I3, A, B, C, D, Long]        {}) with Community_In_3_5[I1, I2, I3, A, B, C, D, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_5[I1, I2, I3, A, B, C, D, String]      {}) with Community_In_3_5[I1, I2, I3, A, B, C, D, String]      {}
  lazy val url      = new url      (this, new Community_In_3_5[I1, I2, I3, A, B, C, D, String]      {}) with Community_In_3_5[I1, I2, I3, A, B, C, D, String]      {}
  lazy val category = new category (this, new Community_In_3_5[I1, I2, I3, A, B, C, D, Set[String]] {}) with Community_In_3_5[I1, I2, I3, A, B, C, D, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_5[I1, I2, I3, A, B, C, D, String]      {}) with Community_In_3_5[I1, I2, I3, A, B, C, D, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_5[I1, I2, I3, A, B, C, D, String]      {}) with Community_In_3_5[I1, I2, I3, A, B, C, D, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_4[I1, I2, I3, A, B, C, D]
}

trait Community_In_3_5[I1, I2, I3, A, B, C, D, E] extends In_3_5[I1, I2, I3, A, B, C, D, E] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_6[I1, I2, I3, A, B, C, D, E, Long]        {}) with Community_In_3_6[I1, I2, I3, A, B, C, D, E, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      {}) with Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      {}
  lazy val url      = new url      (this, new Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      {}) with Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      {}
  lazy val category = new category (this, new Community_In_3_6[I1, I2, I3, A, B, C, D, E, Set[String]] {}) with Community_In_3_6[I1, I2, I3, A, B, C, D, E, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      {}) with Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      {}) with Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_5[I1, I2, I3, A, B, C, D, E]
}

trait Community_In_3_6[I1, I2, I3, A, B, C, D, E, F] extends In_3_6[I1, I2, I3, A, B, C, D, E, F] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]        {}) with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      {}) with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      {}
  lazy val url      = new url      (this, new Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      {}) with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      {}
  lazy val category = new category (this, new Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, Set[String]] {}) with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      {}) with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      {}) with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_6[I1, I2, I3, A, B, C, D, E, F]
}

trait Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G] extends In_3_7[I1, I2, I3, A, B, C, D, E, F, G] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]        {}) with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      {}) with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      {}
  lazy val url      = new url      (this, new Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      {}) with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      {}
  lazy val category = new category (this, new Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Set[String]] {}) with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      {}) with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      {}) with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]
}

trait Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] extends In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]
                  