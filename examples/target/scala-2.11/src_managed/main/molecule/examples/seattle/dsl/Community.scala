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

trait Community_Out_8[A, B, C, D, E, F, G, H] extends Out_8[A, B, C, D, E, F, G, H] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_9[A, B, C, D, E, F, G, H, Long]        {}) with Community_Out_9[A, B, C, D, E, F, G, H, Long]        {}
  lazy val name     = new name     (this, new Community_Out_9[A, B, C, D, E, F, G, H, String]      {}) with Community_Out_9[A, B, C, D, E, F, G, H, String]      {}
  lazy val url      = new url      (this, new Community_Out_9[A, B, C, D, E, F, G, H, String]      {}) with Community_Out_9[A, B, C, D, E, F, G, H, String]      {}
  lazy val category = new category (this, new Community_Out_9[A, B, C, D, E, F, G, H, Set[String]] {}) with Community_Out_9[A, B, C, D, E, F, G, H, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_9[A, B, C, D, E, F, G, H, String]      {}) with Community_Out_9[A, B, C, D, E, F, G, H, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_9[A, B, C, D, E, F, G, H, String]      {}) with Community_Out_9[A, B, C, D, E, F, G, H, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_8[A, B, C, D, E, F, G, H]
  def apply(in: ?.type)    = new Community_In_1_7[H, A, B, C, D, E, F, G] {}
  def apply(in: ?!.type)   = new Community_In_1_8[H, A, B, C, D, E, F, G, H] {}
  def <(in: ?.type)        = new Community_In_1_8[H, A, B, C, D, E, F, G, H] {}
  def contains(in: ?.type) = new Community_In_1_8[H, A, B, C, D, E, F, G, H] {}
  def apply(m: maybe.type) = new Community_Out_8[A, B, C, D, E, F, G, H] {}
}

trait Community_Out_9[A, B, C, D, E, F, G, H, I] extends Out_9[A, B, C, D, E, F, G, H, I] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_10[A, B, C, D, E, F, G, H, I, Long]        {}) with Community_Out_10[A, B, C, D, E, F, G, H, I, Long]        {}
  lazy val name     = new name     (this, new Community_Out_10[A, B, C, D, E, F, G, H, I, String]      {}) with Community_Out_10[A, B, C, D, E, F, G, H, I, String]      {}
  lazy val url      = new url      (this, new Community_Out_10[A, B, C, D, E, F, G, H, I, String]      {}) with Community_Out_10[A, B, C, D, E, F, G, H, I, String]      {}
  lazy val category = new category (this, new Community_Out_10[A, B, C, D, E, F, G, H, I, Set[String]] {}) with Community_Out_10[A, B, C, D, E, F, G, H, I, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_10[A, B, C, D, E, F, G, H, I, String]      {}) with Community_Out_10[A, B, C, D, E, F, G, H, I, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_10[A, B, C, D, E, F, G, H, I, String]      {}) with Community_Out_10[A, B, C, D, E, F, G, H, I, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_9[A, B, C, D, E, F, G, H, I]
  def apply(in: ?.type)    = new Community_In_1_8[I, A, B, C, D, E, F, G, H] {}
  def apply(in: ?!.type)   = new Community_In_1_9[I, A, B, C, D, E, F, G, H, I] {}
  def <(in: ?.type)        = new Community_In_1_9[I, A, B, C, D, E, F, G, H, I] {}
  def contains(in: ?.type) = new Community_In_1_9[I, A, B, C, D, E, F, G, H, I] {}
  def apply(m: maybe.type) = new Community_Out_9[A, B, C, D, E, F, G, H, I] {}
}

trait Community_Out_10[A, B, C, D, E, F, G, H, I, J] extends Out_10[A, B, C, D, E, F, G, H, I, J] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_11[A, B, C, D, E, F, G, H, I, J, Long]        {}) with Community_Out_11[A, B, C, D, E, F, G, H, I, J, Long]        {}
  lazy val name     = new name     (this, new Community_Out_11[A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_Out_11[A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val url      = new url      (this, new Community_Out_11[A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_Out_11[A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val category = new category (this, new Community_Out_11[A, B, C, D, E, F, G, H, I, J, Set[String]] {}) with Community_Out_11[A, B, C, D, E, F, G, H, I, J, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_11[A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_Out_11[A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_11[A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_Out_11[A, B, C, D, E, F, G, H, I, J, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_10[A, B, C, D, E, F, G, H, I, J]
  def apply(in: ?.type)    = new Community_In_1_9[J, A, B, C, D, E, F, G, H, I] {}
  def apply(in: ?!.type)   = new Community_In_1_10[J, A, B, C, D, E, F, G, H, I, J] {}
  def <(in: ?.type)        = new Community_In_1_10[J, A, B, C, D, E, F, G, H, I, J] {}
  def contains(in: ?.type) = new Community_In_1_10[J, A, B, C, D, E, F, G, H, I, J] {}
  def apply(m: maybe.type) = new Community_Out_10[A, B, C, D, E, F, G, H, I, J] {}
}

trait Community_Out_11[A, B, C, D, E, F, G, H, I, J, K] extends Out_11[A, B, C, D, E, F, G, H, I, J, K] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, Long]        {}) with Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, Long]        {}
  lazy val name     = new name     (this, new Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val url      = new url      (this, new Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val category = new category (this, new Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, Set[String]] {}) with Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_11[A, B, C, D, E, F, G, H, I, J, K]
  def apply(in: ?.type)    = new Community_In_1_10[K, A, B, C, D, E, F, G, H, I, J] {}
  def apply(in: ?!.type)   = new Community_In_1_11[K, A, B, C, D, E, F, G, H, I, J, K] {}
  def <(in: ?.type)        = new Community_In_1_11[K, A, B, C, D, E, F, G, H, I, J, K] {}
  def contains(in: ?.type) = new Community_In_1_11[K, A, B, C, D, E, F, G, H, I, J, K] {}
  def apply(m: maybe.type) = new Community_Out_11[A, B, C, D, E, F, G, H, I, J, K] {}
}

trait Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, L] extends Out_12[A, B, C, D, E, F, G, H, I, J, K, L] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, Long]        {}) with Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, Long]        {}
  lazy val name     = new name     (this, new Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val url      = new url      (this, new Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val category = new category (this, new Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, Set[String]] {}) with Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_12[A, B, C, D, E, F, G, H, I, J, K, L]
  def apply(in: ?.type)    = new Community_In_1_11[L, A, B, C, D, E, F, G, H, I, J, K] {}
  def apply(in: ?!.type)   = new Community_In_1_12[L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def <(in: ?.type)        = new Community_In_1_12[L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def contains(in: ?.type) = new Community_In_1_12[L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def apply(m: maybe.type) = new Community_Out_12[A, B, C, D, E, F, G, H, I, J, K, L] {}
}

trait Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M] extends Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long]        {}) with Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Long]        {}
  lazy val name     = new name     (this, new Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val url      = new url      (this, new Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val category = new category (this, new Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Set[String]] {}) with Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M]
  def apply(in: ?.type)    = new Community_In_1_12[M, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def apply(in: ?!.type)   = new Community_In_1_13[M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def <(in: ?.type)        = new Community_In_1_13[M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def contains(in: ?.type) = new Community_In_1_13[M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def apply(m: maybe.type) = new Community_Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M] {}
}

trait Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]        {}) with Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]        {}
  lazy val name     = new name     (this, new Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val url      = new url      (this, new Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val category = new category (this, new Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Set[String]] {}) with Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  def apply(in: ?.type)    = new Community_In_1_13[N, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def apply(in: ?!.type)   = new Community_In_1_14[N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def <(in: ?.type)        = new Community_In_1_14[N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def contains(in: ?.type) = new Community_In_1_14[N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def apply(m: maybe.type) = new Community_Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
}

trait Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]        {}) with Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]        {}
  lazy val name     = new name     (this, new Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val url      = new url      (this, new Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val category = new category (this, new Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Set[String]] {}) with Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  def apply(in: ?.type)    = new Community_In_1_14[O, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def apply(in: ?!.type)   = new Community_In_1_15[O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def <(in: ?.type)        = new Community_In_1_15[O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def contains(in: ?.type) = new Community_In_1_15[O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def apply(m: maybe.type) = new Community_Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
}

trait Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]        {}) with Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]        {}
  lazy val name     = new name     (this, new Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val url      = new url      (this, new Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val category = new category (this, new Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Set[String]] {}) with Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  def apply(in: ?.type)    = new Community_In_1_15[P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def apply(in: ?!.type)   = new Community_In_1_16[P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def <(in: ?.type)        = new Community_In_1_16[P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def contains(in: ?.type) = new Community_In_1_16[P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def apply(m: maybe.type) = new Community_Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
}

trait Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]        {}) with Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]        {}
  lazy val name     = new name     (this, new Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val url      = new url      (this, new Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val category = new category (this, new Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Set[String]] {}) with Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  def apply(in: ?.type)    = new Community_In_1_16[Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def apply(in: ?!.type)   = new Community_In_1_17[Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def <(in: ?.type)        = new Community_In_1_17[Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def contains(in: ?.type) = new Community_In_1_17[Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def apply(m: maybe.type) = new Community_Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
}

trait Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]        {}) with Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]        {}
  lazy val name     = new name     (this, new Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val url      = new url      (this, new Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val category = new category (this, new Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Set[String]] {}) with Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  def apply(in: ?.type)    = new Community_In_1_17[R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def apply(in: ?!.type)   = new Community_In_1_18[R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def <(in: ?.type)        = new Community_In_1_18[R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def contains(in: ?.type) = new Community_In_1_18[R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def apply(m: maybe.type) = new Community_Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
}

trait Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]        {}) with Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]        {}
  lazy val name     = new name     (this, new Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val url      = new url      (this, new Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val category = new category (this, new Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Set[String]] {}) with Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  def apply(in: ?.type)    = new Community_In_1_18[S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def apply(in: ?!.type)   = new Community_In_1_19[S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def <(in: ?.type)        = new Community_In_1_19[S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def contains(in: ?.type) = new Community_In_1_19[S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def apply(m: maybe.type) = new Community_Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
}

trait Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]        {}) with Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]        {}
  lazy val name     = new name     (this, new Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val url      = new url      (this, new Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val category = new category (this, new Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Set[String]] {}) with Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  def apply(in: ?.type)    = new Community_In_1_19[T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def apply(in: ?!.type)   = new Community_In_1_20[T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def <(in: ?.type)        = new Community_In_1_20[T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def contains(in: ?.type) = new Community_In_1_20[T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def apply(m: maybe.type) = new Community_Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
}

trait Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {
  import Community._
  lazy val eid      = new eid      (this, new Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]        {}) with Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]        {}
  lazy val name     = new name     (this, new Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val url      = new url      (this, new Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val category = new category (this, new Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Set[String]] {}) with Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val `type`   = new `type`   (this, new Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  def Neighborhood = new OneRef with Neighborhood_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  def apply(in: ?.type)    = new Community_In_1_20[U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def apply(in: ?!.type)   = new Community_In_1_21[U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def <(in: ?.type)        = new Community_In_1_21[U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def contains(in: ?.type) = new Community_In_1_21[U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def apply(m: maybe.type) = new Community_Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
}

trait Community_Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] 


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

trait Community_In_1_8[I1, A, B, C, D, E, F, G, H] extends In_1_8[I1, A, B, C, D, E, F, G, H] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_9[I1, A, B, C, D, E, F, G, H, Long]        {}) with Community_In_1_9[I1, A, B, C, D, E, F, G, H, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_9[I1, A, B, C, D, E, F, G, H, String]      {}) with Community_In_1_9[I1, A, B, C, D, E, F, G, H, String]      {}
  lazy val url      = new url      (this, new Community_In_1_9[I1, A, B, C, D, E, F, G, H, String]      {}) with Community_In_1_9[I1, A, B, C, D, E, F, G, H, String]      {}
  lazy val category = new category (this, new Community_In_1_9[I1, A, B, C, D, E, F, G, H, Set[String]] {}) with Community_In_1_9[I1, A, B, C, D, E, F, G, H, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_9[I1, A, B, C, D, E, F, G, H, String]      {}) with Community_In_1_9[I1, A, B, C, D, E, F, G, H, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_9[I1, A, B, C, D, E, F, G, H, String]      {}) with Community_In_1_9[I1, A, B, C, D, E, F, G, H, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_8[I1, A, B, C, D, E, F, G, H]
  def apply(in: ?.type)    = new Community_In_2_7[I1, H, A, B, C, D, E, F, G] {}
  def apply(in: ?!.type)   = new Community_In_2_8[I1, H, A, B, C, D, E, F, G, H] {}
  def <(in: ?.type)        = new Community_In_2_8[I1, H, A, B, C, D, E, F, G, H] {}
  def contains(in: ?.type) = new Community_In_2_8[I1, H, A, B, C, D, E, F, G, H] {}
}

trait Community_In_1_9[I1, A, B, C, D, E, F, G, H, I] extends In_1_9[I1, A, B, C, D, E, F, G, H, I] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, Long]        {}) with Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, String]      {}
  lazy val url      = new url      (this, new Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, String]      {}
  lazy val category = new category (this, new Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, Set[String]] {}) with Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_9[I1, A, B, C, D, E, F, G, H, I]
  def apply(in: ?.type)    = new Community_In_2_8[I1, I, A, B, C, D, E, F, G, H] {}
  def apply(in: ?!.type)   = new Community_In_2_9[I1, I, A, B, C, D, E, F, G, H, I] {}
  def <(in: ?.type)        = new Community_In_2_9[I1, I, A, B, C, D, E, F, G, H, I] {}
  def contains(in: ?.type) = new Community_In_2_9[I1, I, A, B, C, D, E, F, G, H, I] {}
}

trait Community_In_1_10[I1, A, B, C, D, E, F, G, H, I, J] extends In_1_10[I1, A, B, C, D, E, F, G, H, I, J] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, Long]        {}) with Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val url      = new url      (this, new Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val category = new category (this, new Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, Set[String]] {}) with Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_10[I1, A, B, C, D, E, F, G, H, I, J]
  def apply(in: ?.type)    = new Community_In_2_9[I1, J, A, B, C, D, E, F, G, H, I] {}
  def apply(in: ?!.type)   = new Community_In_2_10[I1, J, A, B, C, D, E, F, G, H, I, J] {}
  def <(in: ?.type)        = new Community_In_2_10[I1, J, A, B, C, D, E, F, G, H, I, J] {}
  def contains(in: ?.type) = new Community_In_2_10[I1, J, A, B, C, D, E, F, G, H, I, J] {}
}

trait Community_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, K] extends In_1_11[I1, A, B, C, D, E, F, G, H, I, J, K] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long]        {}) with Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val url      = new url      (this, new Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val category = new category (this, new Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Set[String]] {}) with Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_11[I1, A, B, C, D, E, F, G, H, I, J, K]
  def apply(in: ?.type)    = new Community_In_2_10[I1, K, A, B, C, D, E, F, G, H, I, J] {}
  def apply(in: ?!.type)   = new Community_In_2_11[I1, K, A, B, C, D, E, F, G, H, I, J, K] {}
  def <(in: ?.type)        = new Community_In_2_11[I1, K, A, B, C, D, E, F, G, H, I, J, K] {}
  def contains(in: ?.type) = new Community_In_2_11[I1, K, A, B, C, D, E, F, G, H, I, J, K] {}
}

trait Community_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L] extends In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long]        {}) with Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val url      = new url      (this, new Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val category = new category (this, new Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Set[String]] {}) with Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L]
  def apply(in: ?.type)    = new Community_In_2_11[I1, L, A, B, C, D, E, F, G, H, I, J, K] {}
  def apply(in: ?!.type)   = new Community_In_2_12[I1, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def <(in: ?.type)        = new Community_In_2_12[I1, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def contains(in: ?.type) = new Community_In_2_12[I1, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
}

trait Community_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M] extends In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]        {}) with Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val url      = new url      (this, new Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val category = new category (this, new Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Set[String]] {}) with Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M]
  def apply(in: ?.type)    = new Community_In_2_12[I1, M, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def apply(in: ?!.type)   = new Community_In_2_13[I1, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def <(in: ?.type)        = new Community_In_2_13[I1, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def contains(in: ?.type) = new Community_In_2_13[I1, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
}

trait Community_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]        {}) with Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val url      = new url      (this, new Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val category = new category (this, new Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Set[String]] {}) with Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  def apply(in: ?.type)    = new Community_In_2_13[I1, N, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def apply(in: ?!.type)   = new Community_In_2_14[I1, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def <(in: ?.type)        = new Community_In_2_14[I1, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def contains(in: ?.type) = new Community_In_2_14[I1, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
}

trait Community_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]        {}) with Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val url      = new url      (this, new Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val category = new category (this, new Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Set[String]] {}) with Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  def apply(in: ?.type)    = new Community_In_2_14[I1, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def apply(in: ?!.type)   = new Community_In_2_15[I1, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def <(in: ?.type)        = new Community_In_2_15[I1, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def contains(in: ?.type) = new Community_In_2_15[I1, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
}

trait Community_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]        {}) with Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val url      = new url      (this, new Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val category = new category (this, new Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Set[String]] {}) with Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  def apply(in: ?.type)    = new Community_In_2_15[I1, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def apply(in: ?!.type)   = new Community_In_2_16[I1, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def <(in: ?.type)        = new Community_In_2_16[I1, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def contains(in: ?.type) = new Community_In_2_16[I1, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
}

trait Community_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]        {}) with Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val url      = new url      (this, new Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val category = new category (this, new Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Set[String]] {}) with Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  def apply(in: ?.type)    = new Community_In_2_16[I1, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def apply(in: ?!.type)   = new Community_In_2_17[I1, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def <(in: ?.type)        = new Community_In_2_17[I1, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def contains(in: ?.type) = new Community_In_2_17[I1, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
}

trait Community_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]        {}) with Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val url      = new url      (this, new Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val category = new category (this, new Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Set[String]] {}) with Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  def apply(in: ?.type)    = new Community_In_2_17[I1, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def apply(in: ?!.type)   = new Community_In_2_18[I1, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def <(in: ?.type)        = new Community_In_2_18[I1, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def contains(in: ?.type) = new Community_In_2_18[I1, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
}

trait Community_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]        {}) with Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val url      = new url      (this, new Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val category = new category (this, new Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Set[String]] {}) with Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  def apply(in: ?.type)    = new Community_In_2_18[I1, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def apply(in: ?!.type)   = new Community_In_2_19[I1, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def <(in: ?.type)        = new Community_In_2_19[I1, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def contains(in: ?.type) = new Community_In_2_19[I1, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
}

trait Community_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]        {}) with Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val url      = new url      (this, new Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val category = new category (this, new Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Set[String]] {}) with Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  def apply(in: ?.type)    = new Community_In_2_19[I1, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def apply(in: ?!.type)   = new Community_In_2_20[I1, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def <(in: ?.type)        = new Community_In_2_20[I1, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def contains(in: ?.type) = new Community_In_2_20[I1, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
}

trait Community_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]        {}) with Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]        {}
  lazy val name     = new name     (this, new Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val url      = new url      (this, new Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val category = new category (this, new Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Set[String]] {}) with Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  def apply(in: ?.type)    = new Community_In_2_20[I1, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def apply(in: ?!.type)   = new Community_In_2_21[I1, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def <(in: ?.type)        = new Community_In_2_21[I1, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def contains(in: ?.type) = new Community_In_2_21[I1, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
}

trait Community_In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] 


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

trait Community_In_2_8[I1, I2, A, B, C, D, E, F, G, H] extends In_2_8[I1, I2, A, B, C, D, E, F, G, H] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, Long]        {}) with Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, String]      {}) with Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, String]      {}
  lazy val url      = new url      (this, new Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, String]      {}) with Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, String]      {}
  lazy val category = new category (this, new Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, Set[String]] {}) with Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, String]      {}) with Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, String]      {}) with Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_8[I1, I2, A, B, C, D, E, F, G, H]
  def apply(in: ?.type)    = new Community_In_3_7[I1, I2, H, A, B, C, D, E, F, G] {}
  def apply(in: ?!.type)   = new Community_In_3_8[I1, I2, H, A, B, C, D, E, F, G, H] {}
  def <(in: ?.type)        = new Community_In_3_8[I1, I2, H, A, B, C, D, E, F, G, H] {}
  def contains(in: ?.type) = new Community_In_3_8[I1, I2, H, A, B, C, D, E, F, G, H] {}
}

trait Community_In_2_9[I1, I2, A, B, C, D, E, F, G, H, I] extends In_2_9[I1, I2, A, B, C, D, E, F, G, H, I] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, Long]        {}) with Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, String]      {}
  lazy val url      = new url      (this, new Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, String]      {}
  lazy val category = new category (this, new Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, Set[String]] {}) with Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_9[I1, I2, A, B, C, D, E, F, G, H, I]
  def apply(in: ?.type)    = new Community_In_3_8[I1, I2, I, A, B, C, D, E, F, G, H] {}
  def apply(in: ?!.type)   = new Community_In_3_9[I1, I2, I, A, B, C, D, E, F, G, H, I] {}
  def <(in: ?.type)        = new Community_In_3_9[I1, I2, I, A, B, C, D, E, F, G, H, I] {}
  def contains(in: ?.type) = new Community_In_3_9[I1, I2, I, A, B, C, D, E, F, G, H, I] {}
}

trait Community_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] extends In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Long]        {}) with Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val url      = new url      (this, new Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val category = new category (this, new Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Set[String]] {}) with Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J]
  def apply(in: ?.type)    = new Community_In_3_9[I1, I2, J, A, B, C, D, E, F, G, H, I] {}
  def apply(in: ?!.type)   = new Community_In_3_10[I1, I2, J, A, B, C, D, E, F, G, H, I, J] {}
  def <(in: ?.type)        = new Community_In_3_10[I1, I2, J, A, B, C, D, E, F, G, H, I, J] {}
  def contains(in: ?.type) = new Community_In_3_10[I1, I2, J, A, B, C, D, E, F, G, H, I, J] {}
}

trait Community_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] extends In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Long]        {}) with Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val url      = new url      (this, new Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val category = new category (this, new Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Set[String]] {}) with Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K]
  def apply(in: ?.type)    = new Community_In_3_10[I1, I2, K, A, B, C, D, E, F, G, H, I, J] {}
  def apply(in: ?!.type)   = new Community_In_3_11[I1, I2, K, A, B, C, D, E, F, G, H, I, J, K] {}
  def <(in: ?.type)        = new Community_In_3_11[I1, I2, K, A, B, C, D, E, F, G, H, I, J, K] {}
  def contains(in: ?.type) = new Community_In_3_11[I1, I2, K, A, B, C, D, E, F, G, H, I, J, K] {}
}

trait Community_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] extends In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Long]        {}) with Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val url      = new url      (this, new Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val category = new category (this, new Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Set[String]] {}) with Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]
  def apply(in: ?.type)    = new Community_In_3_11[I1, I2, L, A, B, C, D, E, F, G, H, I, J, K] {}
  def apply(in: ?!.type)   = new Community_In_3_12[I1, I2, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def <(in: ?.type)        = new Community_In_3_12[I1, I2, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def contains(in: ?.type) = new Community_In_3_12[I1, I2, L, A, B, C, D, E, F, G, H, I, J, K, L] {}
}

trait Community_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] extends In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]        {}) with Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val url      = new url      (this, new Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val category = new category (this, new Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Set[String]] {}) with Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]
  def apply(in: ?.type)    = new Community_In_3_12[I1, I2, M, A, B, C, D, E, F, G, H, I, J, K, L] {}
  def apply(in: ?!.type)   = new Community_In_3_13[I1, I2, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def <(in: ?.type)        = new Community_In_3_13[I1, I2, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def contains(in: ?.type) = new Community_In_3_13[I1, I2, M, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
}

trait Community_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]        {}) with Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val url      = new url      (this, new Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val category = new category (this, new Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Set[String]] {}) with Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  def apply(in: ?.type)    = new Community_In_3_13[I1, I2, N, A, B, C, D, E, F, G, H, I, J, K, L, M] {}
  def apply(in: ?!.type)   = new Community_In_3_14[I1, I2, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def <(in: ?.type)        = new Community_In_3_14[I1, I2, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def contains(in: ?.type) = new Community_In_3_14[I1, I2, N, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
}

trait Community_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]        {}) with Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val url      = new url      (this, new Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val category = new category (this, new Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Set[String]] {}) with Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  def apply(in: ?.type)    = new Community_In_3_14[I1, I2, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {}
  def apply(in: ?!.type)   = new Community_In_3_15[I1, I2, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def <(in: ?.type)        = new Community_In_3_15[I1, I2, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def contains(in: ?.type) = new Community_In_3_15[I1, I2, O, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
}

trait Community_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]        {}) with Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val url      = new url      (this, new Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val category = new category (this, new Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Set[String]] {}) with Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  def apply(in: ?.type)    = new Community_In_3_15[I1, I2, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {}
  def apply(in: ?!.type)   = new Community_In_3_16[I1, I2, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def <(in: ?.type)        = new Community_In_3_16[I1, I2, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def contains(in: ?.type) = new Community_In_3_16[I1, I2, P, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
}

trait Community_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]        {}) with Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val url      = new url      (this, new Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val category = new category (this, new Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Set[String]] {}) with Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  def apply(in: ?.type)    = new Community_In_3_16[I1, I2, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {}
  def apply(in: ?!.type)   = new Community_In_3_17[I1, I2, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def <(in: ?.type)        = new Community_In_3_17[I1, I2, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def contains(in: ?.type) = new Community_In_3_17[I1, I2, Q, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
}

trait Community_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]        {}) with Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val url      = new url      (this, new Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val category = new category (this, new Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Set[String]] {}) with Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  def apply(in: ?.type)    = new Community_In_3_17[I1, I2, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {}
  def apply(in: ?!.type)   = new Community_In_3_18[I1, I2, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def <(in: ?.type)        = new Community_In_3_18[I1, I2, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def contains(in: ?.type) = new Community_In_3_18[I1, I2, R, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
}

trait Community_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]        {}) with Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val url      = new url      (this, new Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val category = new category (this, new Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Set[String]] {}) with Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  def apply(in: ?.type)    = new Community_In_3_18[I1, I2, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {}
  def apply(in: ?!.type)   = new Community_In_3_19[I1, I2, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def <(in: ?.type)        = new Community_In_3_19[I1, I2, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def contains(in: ?.type) = new Community_In_3_19[I1, I2, S, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
}

trait Community_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]        {}) with Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val url      = new url      (this, new Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val category = new category (this, new Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Set[String]] {}) with Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  def apply(in: ?.type)    = new Community_In_3_19[I1, I2, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {}
  def apply(in: ?!.type)   = new Community_In_3_20[I1, I2, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def <(in: ?.type)        = new Community_In_3_20[I1, I2, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def contains(in: ?.type) = new Community_In_3_20[I1, I2, T, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
}

trait Community_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]        {}) with Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]        {}
  lazy val name     = new name     (this, new Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val url      = new url      (this, new Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val category = new category (this, new Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Set[String]] {}) with Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  def apply(in: ?.type)    = new Community_In_3_20[I1, I2, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {}
  def apply(in: ?!.type)   = new Community_In_3_21[I1, I2, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def <(in: ?.type)        = new Community_In_3_21[I1, I2, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
  def contains(in: ?.type) = new Community_In_3_21[I1, I2, U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {}
}

trait Community_In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] 


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

trait Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] extends In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long]        {}) with Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String]      {}) with Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String]      {}
  lazy val url      = new url      (this, new Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String]      {}) with Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String]      {}
  lazy val category = new category (this, new Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Set[String]] {}) with Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String]      {}) with Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String]      {}) with Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]
}

trait Community_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I] extends In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long]        {}) with Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String]      {}
  lazy val url      = new url      (this, new Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String]      {}
  lazy val category = new category (this, new Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Set[String]] {}) with Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String]      {}) with Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I]
}

trait Community_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J] extends In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long]        {}) with Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val url      = new url      (this, new Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val category = new category (this, new Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Set[String]] {}) with Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String]      {}) with Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J]
}

trait Community_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] extends In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long]        {}) with Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val url      = new url      (this, new Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val category = new category (this, new Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Set[String]] {}) with Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String]      {}) with Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K]
}

trait Community_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] extends In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long]        {}) with Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val url      = new url      (this, new Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val category = new category (this, new Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Set[String]] {}) with Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}) with Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L]
}

trait Community_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] extends In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]        {}) with Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val url      = new url      (this, new Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val category = new category (this, new Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Set[String]] {}) with Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}) with Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M]
}

trait Community_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]        {}) with Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val url      = new url      (this, new Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val category = new category (this, new Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Set[String]] {}) with Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}) with Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
}

trait Community_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]        {}) with Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val url      = new url      (this, new Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val category = new category (this, new Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Set[String]] {}) with Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}) with Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
}

trait Community_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]        {}) with Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val url      = new url      (this, new Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val category = new category (this, new Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Set[String]] {}) with Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}) with Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
}

trait Community_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]        {}) with Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val url      = new url      (this, new Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val category = new category (this, new Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Set[String]] {}) with Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}) with Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
}

trait Community_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]        {}) with Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val url      = new url      (this, new Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val category = new category (this, new Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Set[String]] {}) with Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}) with Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
}

trait Community_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]        {}) with Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val url      = new url      (this, new Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val category = new category (this, new Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Set[String]] {}) with Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}) with Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
}

trait Community_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]        {}) with Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val url      = new url      (this, new Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val category = new category (this, new Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Set[String]] {}) with Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}) with Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
}

trait Community_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] {
  import Community._
  lazy val eid      = new eid      (this, new Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]        {}) with Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Long]        {}
  lazy val name     = new name     (this, new Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val url      = new url      (this, new Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val category = new category (this, new Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Set[String]] {}) with Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Set[String]] {}
  lazy val orgtype  = new orgtype  (this, new Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  lazy val `type`   = new `type`   (this, new Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}) with Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String]      {}
  def Neighborhood = new OneRef with Neighborhood_In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
}

trait Community_In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]
                  