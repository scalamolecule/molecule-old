///*
// * AUTO-GENERATED CODE - DON'T CHANGE!
// *
// * Manual changes to this file will likely break molecules!
// * Instead, change the molecule definition files and recompile your project with `sbt compile`.
// */
//package molecule.examples.seattle.dsl.seattle
//import molecule._
//import molecule.dsl.schemaDSL._
//import molecule.in._
//import molecule.out._
//import java.util.Date
//
//
//object Community extends Community_0 {
//  def apply(e: Long)       : Community_0      = ???
//  def apply(c: count.type) : Community_1[Int] = ???
//}
//
//trait Community  {
//  class name        [Ns] extends OneString [Ns] with FulltextSearch[Ns] { self: Ns => }
//  class url         [Ns] extends OneString [Ns]                         { self: Ns => }
//  class category    [Ns] extends ManyString[Ns] with FulltextSearch[Ns] { self: Ns => }
//  class orgtype     [Ns] extends OneEnum   [Ns] { self: Ns => private lazy val community, commercial, nonprofit, personal = EnumValue }
//  class `type`      [Ns] extends OneEnum   [Ns] { self: Ns => private lazy val email_list, twitter, facebook_page, blog, website, wiki, myspace, ning = EnumValue }
//  class neighborhood[Ns] extends OneRefAttr[Ns]                         { self: Ns => }
//}
//
//trait Community_0 extends Community with Molecule_0 {
//  val name          : name        [Community_1[String]]      with Community_1[String]      = ???
//  val url           : url         [Community_1[String]]      with Community_1[String]      = ???
//  val category      : category    [Community_1[Set[String]]] with Community_1[Set[String]] = ???
//  val orgtype       : orgtype     [Community_1[String]]      with Community_1[String]      = ???
//  val `type`        : `type`      [Community_1[String]]      with Community_1[String]      = ???
//  val neighborhood  : neighborhood[Community_1[Long]]        with Community_1[Long]        = ???
//
//  val name_         : name        [Community_0]              with Community_0              = ???
//  val url_          : url         [Community_0]              with Community_0              = ???
//  val category_     : category    [Community_0]              with Community_0              = ???
//  val orgtype_      : orgtype     [Community_0]              with Community_0              = ???
//  val type_         : `type`      [Community_0]              with Community_0              = ???
//  val neighborhood_ : neighborhood[Community_0]              with Community_0              = ???
//
//  val e             : e           [Community_1[Long]]        with Community_1[Long]        = ???
//  val a             : a           [Community_1[String]]      with Community_1[String]      = ???
//  val v             : v           [Community_1[Any]]         with Community_1[Any]         = ???
//  val ns            : ns          [Community_1[String]]      with Community_1[String]      = ???
//  val txInstant     : txInstant   [Community_1[Date]]        with Community_1[Date]        = ???
//  val txT           : txT         [Community_1[Long]]        with Community_1[Long]        = ???
//  val txAdded       : txAdded     [Community_1[Boolean]]     with Community_1[Boolean]     = ???
//
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_0 = ???
//}
//
//trait Community_1[A] extends Community with Molecule_1[A] {
//  val name          : name        [Community_2[A, String]]      with Community_2[A, String]      = ???
//  val url           : url         [Community_2[A, String]]      with Community_2[A, String]      = ???
//  val category      : category    [Community_2[A, Set[String]]] with Community_2[A, Set[String]] = ???
//  val orgtype       : orgtype     [Community_2[A, String]]      with Community_2[A, String]      = ???
//  val `type`        : `type`      [Community_2[A, String]]      with Community_2[A, String]      = ???
//  val neighborhood  : neighborhood[Community_2[A, Long]]        with Community_2[A, Long]        = ???
//
//  val name_         : name        [Community_1[A]]              with Community_1[A]              = ???
//  val url_          : url         [Community_1[A]]              with Community_1[A]              = ???
//  val category_     : category    [Community_1[A]]              with Community_1[A]              = ???
//  val orgtype_      : orgtype     [Community_1[A]]              with Community_1[A]              = ???
//  val type_         : `type`      [Community_1[A]]              with Community_1[A]              = ???
//  val neighborhood_ : neighborhood[Community_1[A]]              with Community_1[A]              = ???
//
//  val e             : e           [Community_2[A, Long]]        with Community_2[A, Long]        = ???
//  val a             : a           [Community_2[A, String]]      with Community_2[A, String]      = ???
//  val v             : v           [Community_2[A, Any]]         with Community_2[A, Any]         = ???
//  val ns            : ns          [Community_2[A, String]]      with Community_2[A, String]      = ???
//  val txInstant     : txInstant   [Community_2[A, Date]]        with Community_2[A, Date]        = ???
//  val txT           : txT         [Community_2[A, Long]]        with Community_2[A, Long]        = ???
//  val txAdded       : txAdded     [Community_2[A, Boolean]]     with Community_2[A, Boolean]     = ???
//
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_1[A] = ???
//  def apply(in: ?.type)    : Community_In_1_1[A, A] = ???
//  def <(in: ?.type)        : Community_In_1_1[A, A] = ???
//  def contains(in: ?.type) : Community_In_1_1[A, A] = ???
//  def apply(m: maybe.type) : Community_1[A]   = ???
//  def apply(c: count.type) : Community_1[Int] = ???
//}
//
//trait Community_2[A, B] extends Community with Molecule_2[A, B] {
//  val name          : name        [Community_3[A, B, String]]      with Community_3[A, B, String]      = ???
//  val url           : url         [Community_3[A, B, String]]      with Community_3[A, B, String]      = ???
//  val category      : category    [Community_3[A, B, Set[String]]] with Community_3[A, B, Set[String]] = ???
//  val orgtype       : orgtype     [Community_3[A, B, String]]      with Community_3[A, B, String]      = ???
//  val `type`        : `type`      [Community_3[A, B, String]]      with Community_3[A, B, String]      = ???
//  val neighborhood  : neighborhood[Community_3[A, B, Long]]        with Community_3[A, B, Long]        = ???
//
//  val name_         : name        [Community_2[A, B]]              with Community_2[A, B]              = ???
//  val url_          : url         [Community_2[A, B]]              with Community_2[A, B]              = ???
//  val category_     : category    [Community_2[A, B]]              with Community_2[A, B]              = ???
//  val orgtype_      : orgtype     [Community_2[A, B]]              with Community_2[A, B]              = ???
//  val type_         : `type`      [Community_2[A, B]]              with Community_2[A, B]              = ???
//  val neighborhood_ : neighborhood[Community_2[A, B]]              with Community_2[A, B]              = ???
//
//  val e             : e           [Community_3[A, B, Long]]        with Community_3[A, B, Long]        = ???
//  val a             : a           [Community_3[A, B, String]]      with Community_3[A, B, String]      = ???
//  val v             : v           [Community_3[A, B, Any]]         with Community_3[A, B, Any]         = ???
//  val ns            : ns          [Community_3[A, B, String]]      with Community_3[A, B, String]      = ???
//  val txInstant     : txInstant   [Community_3[A, B, Date]]        with Community_3[A, B, Date]        = ???
//  val txT           : txT         [Community_3[A, B, Long]]        with Community_3[A, B, Long]        = ???
//  val txAdded       : txAdded     [Community_3[A, B, Boolean]]     with Community_3[A, B, Boolean]     = ???
//
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_2[A, B] = ???
//  def apply(in: ?.type)    : Community_In_1_2[B, A, B] = ???
//  def <(in: ?.type)        : Community_In_1_2[B, A, B] = ???
//  def contains(in: ?.type) : Community_In_1_2[B, A, B] = ???
//  def apply(m: maybe.type) : Community_2[A, B]   = ???
//  def apply(c: count.type) : Community_2[A, Int] = ???
//}
//
//trait Community_3[A, B, C] extends Community with Molecule_3[A, B, C] {
//  val name          : name        [Community_4[A, B, C, String]]      with Community_4[A, B, C, String]      = ???
//  val url           : url         [Community_4[A, B, C, String]]      with Community_4[A, B, C, String]      = ???
//  val category      : category    [Community_4[A, B, C, Set[String]]] with Community_4[A, B, C, Set[String]] = ???
//  val orgtype       : orgtype     [Community_4[A, B, C, String]]      with Community_4[A, B, C, String]      = ???
//  val `type`        : `type`      [Community_4[A, B, C, String]]      with Community_4[A, B, C, String]      = ???
//  val neighborhood  : neighborhood[Community_4[A, B, C, Long]]        with Community_4[A, B, C, Long]        = ???
//
//  val name_         : name        [Community_3[A, B, C]]              with Community_3[A, B, C]              = ???
//  val url_          : url         [Community_3[A, B, C]]              with Community_3[A, B, C]              = ???
//  val category_     : category    [Community_3[A, B, C]]              with Community_3[A, B, C]              = ???
//  val orgtype_      : orgtype     [Community_3[A, B, C]]              with Community_3[A, B, C]              = ???
//  val type_         : `type`      [Community_3[A, B, C]]              with Community_3[A, B, C]              = ???
//  val neighborhood_ : neighborhood[Community_3[A, B, C]]              with Community_3[A, B, C]              = ???
//
//  val e             : e           [Community_4[A, B, C, Long]]        with Community_4[A, B, C, Long]        = ???
//  val a             : a           [Community_4[A, B, C, String]]      with Community_4[A, B, C, String]      = ???
//  val v             : v           [Community_4[A, B, C, Any]]         with Community_4[A, B, C, Any]         = ???
//  val ns            : ns          [Community_4[A, B, C, String]]      with Community_4[A, B, C, String]      = ???
//  val txInstant     : txInstant   [Community_4[A, B, C, Date]]        with Community_4[A, B, C, Date]        = ???
//  val txT           : txT         [Community_4[A, B, C, Long]]        with Community_4[A, B, C, Long]        = ???
//  val txAdded       : txAdded     [Community_4[A, B, C, Boolean]]     with Community_4[A, B, C, Boolean]     = ???
//
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_3[A, B, C] = ???
//  def apply(in: ?.type)    : Community_In_1_3[C, A, B, C] = ???
//  def <(in: ?.type)        : Community_In_1_3[C, A, B, C] = ???
//  def contains(in: ?.type) : Community_In_1_3[C, A, B, C] = ???
//  def apply(m: maybe.type) : Community_3[A, B, C]   = ???
//  def apply(c: count.type) : Community_3[A, B, Int] = ???
//}
//
//trait Community_4[A, B, C, D] extends Community with Molecule_4[A, B, C, D] {
//  val name          : name        [Community_5[A, B, C, D, String]]      with Community_5[A, B, C, D, String]      = ???
//  val url           : url         [Community_5[A, B, C, D, String]]      with Community_5[A, B, C, D, String]      = ???
//  val category      : category    [Community_5[A, B, C, D, Set[String]]] with Community_5[A, B, C, D, Set[String]] = ???
//  val orgtype       : orgtype     [Community_5[A, B, C, D, String]]      with Community_5[A, B, C, D, String]      = ???
//  val `type`        : `type`      [Community_5[A, B, C, D, String]]      with Community_5[A, B, C, D, String]      = ???
//  val neighborhood  : neighborhood[Community_5[A, B, C, D, Long]]        with Community_5[A, B, C, D, Long]        = ???
//
//  val name_         : name        [Community_4[A, B, C, D]]              with Community_4[A, B, C, D]              = ???
//  val url_          : url         [Community_4[A, B, C, D]]              with Community_4[A, B, C, D]              = ???
//  val category_     : category    [Community_4[A, B, C, D]]              with Community_4[A, B, C, D]              = ???
//  val orgtype_      : orgtype     [Community_4[A, B, C, D]]              with Community_4[A, B, C, D]              = ???
//  val type_         : `type`      [Community_4[A, B, C, D]]              with Community_4[A, B, C, D]              = ???
//  val neighborhood_ : neighborhood[Community_4[A, B, C, D]]              with Community_4[A, B, C, D]              = ???
//
//  val e             : e           [Community_5[A, B, C, D, Long]]        with Community_5[A, B, C, D, Long]        = ???
//  val a             : a           [Community_5[A, B, C, D, String]]      with Community_5[A, B, C, D, String]      = ???
//  val v             : v           [Community_5[A, B, C, D, Any]]         with Community_5[A, B, C, D, Any]         = ???
//  val ns            : ns          [Community_5[A, B, C, D, String]]      with Community_5[A, B, C, D, String]      = ???
//  val txInstant     : txInstant   [Community_5[A, B, C, D, Date]]        with Community_5[A, B, C, D, Date]        = ???
//  val txT           : txT         [Community_5[A, B, C, D, Long]]        with Community_5[A, B, C, D, Long]        = ???
//  val txAdded       : txAdded     [Community_5[A, B, C, D, Boolean]]     with Community_5[A, B, C, D, Boolean]     = ???
//
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_4[A, B, C, D] = ???
//  def apply(in: ?.type)    : Community_In_1_4[D, A, B, C, D] = ???
//  def <(in: ?.type)        : Community_In_1_4[D, A, B, C, D] = ???
//  def contains(in: ?.type) : Community_In_1_4[D, A, B, C, D] = ???
//  def apply(m: maybe.type) : Community_4[A, B, C, D]   = ???
//  def apply(c: count.type) : Community_4[A, B, C, Int] = ???
//}
//
//trait Community_5[A, B, C, D, E] extends Community with Molecule_5[A, B, C, D, E] {
//  val name          : name        [Community_6[A, B, C, D, E, String]]      with Community_6[A, B, C, D, E, String]      = ???
//  val url           : url         [Community_6[A, B, C, D, E, String]]      with Community_6[A, B, C, D, E, String]      = ???
//  val category      : category    [Community_6[A, B, C, D, E, Set[String]]] with Community_6[A, B, C, D, E, Set[String]] = ???
//  val orgtype       : orgtype     [Community_6[A, B, C, D, E, String]]      with Community_6[A, B, C, D, E, String]      = ???
//  val `type`        : `type`      [Community_6[A, B, C, D, E, String]]      with Community_6[A, B, C, D, E, String]      = ???
//  val neighborhood  : neighborhood[Community_6[A, B, C, D, E, Long]]        with Community_6[A, B, C, D, E, Long]        = ???
//
//  val name_         : name        [Community_5[A, B, C, D, E]]              with Community_5[A, B, C, D, E]              = ???
//  val url_          : url         [Community_5[A, B, C, D, E]]              with Community_5[A, B, C, D, E]              = ???
//  val category_     : category    [Community_5[A, B, C, D, E]]              with Community_5[A, B, C, D, E]              = ???
//  val orgtype_      : orgtype     [Community_5[A, B, C, D, E]]              with Community_5[A, B, C, D, E]              = ???
//  val type_         : `type`      [Community_5[A, B, C, D, E]]              with Community_5[A, B, C, D, E]              = ???
//  val neighborhood_ : neighborhood[Community_5[A, B, C, D, E]]              with Community_5[A, B, C, D, E]              = ???
//
//  val e             : e           [Community_6[A, B, C, D, E, Long]]        with Community_6[A, B, C, D, E, Long]        = ???
//  val a             : a           [Community_6[A, B, C, D, E, String]]      with Community_6[A, B, C, D, E, String]      = ???
//  val v             : v           [Community_6[A, B, C, D, E, Any]]         with Community_6[A, B, C, D, E, Any]         = ???
//  val ns            : ns          [Community_6[A, B, C, D, E, String]]      with Community_6[A, B, C, D, E, String]      = ???
//  val txInstant     : txInstant   [Community_6[A, B, C, D, E, Date]]        with Community_6[A, B, C, D, E, Date]        = ???
//  val txT           : txT         [Community_6[A, B, C, D, E, Long]]        with Community_6[A, B, C, D, E, Long]        = ???
//  val txAdded       : txAdded     [Community_6[A, B, C, D, E, Boolean]]     with Community_6[A, B, C, D, E, Boolean]     = ???
//
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_5[A, B, C, D, E] = ???
//  def apply(in: ?.type)    : Community_In_1_5[E, A, B, C, D, E] = ???
//  def <(in: ?.type)        : Community_In_1_5[E, A, B, C, D, E] = ???
//  def contains(in: ?.type) : Community_In_1_5[E, A, B, C, D, E] = ???
//  def apply(m: maybe.type) : Community_5[A, B, C, D, E]   = ???
//  def apply(c: count.type) : Community_5[A, B, C, D, Int] = ???
//}
//
//trait Community_6[A, B, C, D, E, F] extends Community with Molecule_6[A, B, C, D, E, F] {
//  val name          : name        [Community_7[A, B, C, D, E, F, String]]      with Community_7[A, B, C, D, E, F, String]      = ???
//  val url           : url         [Community_7[A, B, C, D, E, F, String]]      with Community_7[A, B, C, D, E, F, String]      = ???
//  val category      : category    [Community_7[A, B, C, D, E, F, Set[String]]] with Community_7[A, B, C, D, E, F, Set[String]] = ???
//  val orgtype       : orgtype     [Community_7[A, B, C, D, E, F, String]]      with Community_7[A, B, C, D, E, F, String]      = ???
//  val `type`        : `type`      [Community_7[A, B, C, D, E, F, String]]      with Community_7[A, B, C, D, E, F, String]      = ???
//  val neighborhood  : neighborhood[Community_7[A, B, C, D, E, F, Long]]        with Community_7[A, B, C, D, E, F, Long]        = ???
//
//  val name_         : name        [Community_6[A, B, C, D, E, F]]              with Community_6[A, B, C, D, E, F]              = ???
//  val url_          : url         [Community_6[A, B, C, D, E, F]]              with Community_6[A, B, C, D, E, F]              = ???
//  val category_     : category    [Community_6[A, B, C, D, E, F]]              with Community_6[A, B, C, D, E, F]              = ???
//  val orgtype_      : orgtype     [Community_6[A, B, C, D, E, F]]              with Community_6[A, B, C, D, E, F]              = ???
//  val type_         : `type`      [Community_6[A, B, C, D, E, F]]              with Community_6[A, B, C, D, E, F]              = ???
//  val neighborhood_ : neighborhood[Community_6[A, B, C, D, E, F]]              with Community_6[A, B, C, D, E, F]              = ???
//
//  val e             : e           [Community_7[A, B, C, D, E, F, Long]]        with Community_7[A, B, C, D, E, F, Long]        = ???
//  val a             : a           [Community_7[A, B, C, D, E, F, String]]      with Community_7[A, B, C, D, E, F, String]      = ???
//  val v             : v           [Community_7[A, B, C, D, E, F, Any]]         with Community_7[A, B, C, D, E, F, Any]         = ???
//  val ns            : ns          [Community_7[A, B, C, D, E, F, String]]      with Community_7[A, B, C, D, E, F, String]      = ???
//  val txInstant     : txInstant   [Community_7[A, B, C, D, E, F, Date]]        with Community_7[A, B, C, D, E, F, Date]        = ???
//  val txT           : txT         [Community_7[A, B, C, D, E, F, Long]]        with Community_7[A, B, C, D, E, F, Long]        = ???
//  val txAdded       : txAdded     [Community_7[A, B, C, D, E, F, Boolean]]     with Community_7[A, B, C, D, E, F, Boolean]     = ???
//
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_6[A, B, C, D, E, F] = ???
//  def apply(in: ?.type)    : Community_In_1_6[F, A, B, C, D, E, F] = ???
//  def <(in: ?.type)        : Community_In_1_6[F, A, B, C, D, E, F] = ???
//  def contains(in: ?.type) : Community_In_1_6[F, A, B, C, D, E, F] = ???
//  def apply(m: maybe.type) : Community_6[A, B, C, D, E, F]   = ???
//  def apply(c: count.type) : Community_6[A, B, C, D, E, Int] = ???
//}
//
//trait Community_7[A, B, C, D, E, F, G] extends Community with Molecule_7[A, B, C, D, E, F, G] {
//  val name          : name        [Community_8[A, B, C, D, E, F, G, String]]      with Community_8[A, B, C, D, E, F, G, String]      = ???
//  val url           : url         [Community_8[A, B, C, D, E, F, G, String]]      with Community_8[A, B, C, D, E, F, G, String]      = ???
//  val category      : category    [Community_8[A, B, C, D, E, F, G, Set[String]]] with Community_8[A, B, C, D, E, F, G, Set[String]] = ???
//  val orgtype       : orgtype     [Community_8[A, B, C, D, E, F, G, String]]      with Community_8[A, B, C, D, E, F, G, String]      = ???
//  val `type`        : `type`      [Community_8[A, B, C, D, E, F, G, String]]      with Community_8[A, B, C, D, E, F, G, String]      = ???
//  val neighborhood  : neighborhood[Community_8[A, B, C, D, E, F, G, Long]]        with Community_8[A, B, C, D, E, F, G, Long]        = ???
//
//  val name_         : name        [Community_7[A, B, C, D, E, F, G]]              with Community_7[A, B, C, D, E, F, G]              = ???
//  val url_          : url         [Community_7[A, B, C, D, E, F, G]]              with Community_7[A, B, C, D, E, F, G]              = ???
//  val category_     : category    [Community_7[A, B, C, D, E, F, G]]              with Community_7[A, B, C, D, E, F, G]              = ???
//  val orgtype_      : orgtype     [Community_7[A, B, C, D, E, F, G]]              with Community_7[A, B, C, D, E, F, G]              = ???
//  val type_         : `type`      [Community_7[A, B, C, D, E, F, G]]              with Community_7[A, B, C, D, E, F, G]              = ???
//  val neighborhood_ : neighborhood[Community_7[A, B, C, D, E, F, G]]              with Community_7[A, B, C, D, E, F, G]              = ???
//
//  val e             : e           [Community_8[A, B, C, D, E, F, G, Long]]        with Community_8[A, B, C, D, E, F, G, Long]        = ???
//  val a             : a           [Community_8[A, B, C, D, E, F, G, String]]      with Community_8[A, B, C, D, E, F, G, String]      = ???
//  val v             : v           [Community_8[A, B, C, D, E, F, G, Any]]         with Community_8[A, B, C, D, E, F, G, Any]         = ???
//  val ns            : ns          [Community_8[A, B, C, D, E, F, G, String]]      with Community_8[A, B, C, D, E, F, G, String]      = ???
//  val txInstant     : txInstant   [Community_8[A, B, C, D, E, F, G, Date]]        with Community_8[A, B, C, D, E, F, G, Date]        = ???
//  val txT           : txT         [Community_8[A, B, C, D, E, F, G, Long]]        with Community_8[A, B, C, D, E, F, G, Long]        = ???
//  val txAdded       : txAdded     [Community_8[A, B, C, D, E, F, G, Boolean]]     with Community_8[A, B, C, D, E, F, G, Boolean]     = ???
//
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_7[A, B, C, D, E, F, G] = ???
//  def apply(in: ?.type)    : Community_In_1_7[G, A, B, C, D, E, F, G] = ???
//  def <(in: ?.type)        : Community_In_1_7[G, A, B, C, D, E, F, G] = ???
//  def contains(in: ?.type) : Community_In_1_7[G, A, B, C, D, E, F, G] = ???
//  def apply(m: maybe.type) : Community_7[A, B, C, D, E, F, G]   = ???
//  def apply(c: count.type) : Community_7[A, B, C, D, E, F, Int] = ???
//}
//
//trait Community_8[A, B, C, D, E, F, G, H] extends Community with Molecule_8[A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 1 input *******************************/
//
// trait Community_In_1_0[I1] extends Community with In_1_0[I1] {
//  lazy val name          : name        [Community_In_1_1[I1, String]]      with Community_In_1_1[I1, String]      = ???
//  lazy val url           : url         [Community_In_1_1[I1, String]]      with Community_In_1_1[I1, String]      = ???
//  lazy val category      : category    [Community_In_1_1[I1, Set[String]]] with Community_In_1_1[I1, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_1_1[I1, String]]      with Community_In_1_1[I1, String]      = ???
//  lazy val `type`        : `type`      [Community_In_1_1[I1, String]]      with Community_In_1_1[I1, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_1_1[I1, Long]]        with Community_In_1_1[I1, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_1_0[I1]] with Community_In_1_0[I1]              = ???
//  lazy val url_          :  url         [Community_In_1_0[I1]] with Community_In_1_0[I1]              = ???
//  lazy val category_     :  category    [Community_In_1_0[I1]] with Community_In_1_0[I1]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_1_0[I1]] with Community_In_1_0[I1]              = ???
//  lazy val type_         :  `type`      [Community_In_1_0[I1]] with Community_In_1_0[I1]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_1_0[I1]] with Community_In_1_0[I1]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_1_0[I1] = ???
//}
//
// trait Community_In_1_1[I1, A] extends Community with In_1_1[I1, A] {
//  lazy val name          : name        [Community_In_1_2[I1, A, String]]      with Community_In_1_2[I1, A, String]      = ???
//  lazy val url           : url         [Community_In_1_2[I1, A, String]]      with Community_In_1_2[I1, A, String]      = ???
//  lazy val category      : category    [Community_In_1_2[I1, A, Set[String]]] with Community_In_1_2[I1, A, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_1_2[I1, A, String]]      with Community_In_1_2[I1, A, String]      = ???
//  lazy val `type`        : `type`      [Community_In_1_2[I1, A, String]]      with Community_In_1_2[I1, A, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_1_2[I1, A, Long]]        with Community_In_1_2[I1, A, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_1_1[I1, A]] with Community_In_1_1[I1, A]              = ???
//  lazy val url_          :  url         [Community_In_1_1[I1, A]] with Community_In_1_1[I1, A]              = ???
//  lazy val category_     :  category    [Community_In_1_1[I1, A]] with Community_In_1_1[I1, A]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_1_1[I1, A]] with Community_In_1_1[I1, A]              = ???
//  lazy val type_         :  `type`      [Community_In_1_1[I1, A]] with Community_In_1_1[I1, A]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_1_1[I1, A]] with Community_In_1_1[I1, A]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_1_1[I1, A] = ???
//  def apply(in: ?.type)    : Community_In_2_1[I1, A, A] {} = ???
//  def <(in: ?.type)        : Community_In_2_1[I1, A, A] {} = ???
//  def contains(in: ?.type) : Community_In_2_1[I1, A, A] {} = ???
//  def apply(m: maybe.type) : Community_In_1_1[I1, A] {} = ???
//  def apply(c: count.type) : Community_In_1_1[I1, Int] {} = ???
//}
//
// trait Community_In_1_2[I1, A, B] extends Community with In_1_2[I1, A, B] {
//  lazy val name          : name        [Community_In_1_3[I1, A, B, String]]      with Community_In_1_3[I1, A, B, String]      = ???
//  lazy val url           : url         [Community_In_1_3[I1, A, B, String]]      with Community_In_1_3[I1, A, B, String]      = ???
//  lazy val category      : category    [Community_In_1_3[I1, A, B, Set[String]]] with Community_In_1_3[I1, A, B, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_1_3[I1, A, B, String]]      with Community_In_1_3[I1, A, B, String]      = ???
//  lazy val `type`        : `type`      [Community_In_1_3[I1, A, B, String]]      with Community_In_1_3[I1, A, B, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_1_3[I1, A, B, Long]]        with Community_In_1_3[I1, A, B, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_1_2[I1, A, B]] with Community_In_1_2[I1, A, B]              = ???
//  lazy val url_          :  url         [Community_In_1_2[I1, A, B]] with Community_In_1_2[I1, A, B]              = ???
//  lazy val category_     :  category    [Community_In_1_2[I1, A, B]] with Community_In_1_2[I1, A, B]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_1_2[I1, A, B]] with Community_In_1_2[I1, A, B]              = ???
//  lazy val type_         :  `type`      [Community_In_1_2[I1, A, B]] with Community_In_1_2[I1, A, B]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_1_2[I1, A, B]] with Community_In_1_2[I1, A, B]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_1_2[I1, A, B] = ???
//  def apply(in: ?.type)    : Community_In_2_2[I1, B, A, B] {} = ???
//  def <(in: ?.type)        : Community_In_2_2[I1, B, A, B] {} = ???
//  def contains(in: ?.type) : Community_In_2_2[I1, B, A, B] {} = ???
//  def apply(m: maybe.type) : Community_In_1_2[I1, A, B] {} = ???
//  def apply(c: count.type) : Community_In_1_2[I1, A, Int] {} = ???
//}
//
// trait Community_In_1_3[I1, A, B, C] extends Community with In_1_3[I1, A, B, C] {
//  lazy val name          : name        [Community_In_1_4[I1, A, B, C, String]]      with Community_In_1_4[I1, A, B, C, String]      = ???
//  lazy val url           : url         [Community_In_1_4[I1, A, B, C, String]]      with Community_In_1_4[I1, A, B, C, String]      = ???
//  lazy val category      : category    [Community_In_1_4[I1, A, B, C, Set[String]]] with Community_In_1_4[I1, A, B, C, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_1_4[I1, A, B, C, String]]      with Community_In_1_4[I1, A, B, C, String]      = ???
//  lazy val `type`        : `type`      [Community_In_1_4[I1, A, B, C, String]]      with Community_In_1_4[I1, A, B, C, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_1_4[I1, A, B, C, Long]]        with Community_In_1_4[I1, A, B, C, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_1_3[I1, A, B, C]] with Community_In_1_3[I1, A, B, C]              = ???
//  lazy val url_          :  url         [Community_In_1_3[I1, A, B, C]] with Community_In_1_3[I1, A, B, C]              = ???
//  lazy val category_     :  category    [Community_In_1_3[I1, A, B, C]] with Community_In_1_3[I1, A, B, C]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_1_3[I1, A, B, C]] with Community_In_1_3[I1, A, B, C]              = ???
//  lazy val type_         :  `type`      [Community_In_1_3[I1, A, B, C]] with Community_In_1_3[I1, A, B, C]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_1_3[I1, A, B, C]] with Community_In_1_3[I1, A, B, C]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_1_3[I1, A, B, C] = ???
//  def apply(in: ?.type)    : Community_In_2_3[I1, C, A, B, C] {} = ???
//  def <(in: ?.type)        : Community_In_2_3[I1, C, A, B, C] {} = ???
//  def contains(in: ?.type) : Community_In_2_3[I1, C, A, B, C] {} = ???
//  def apply(m: maybe.type) : Community_In_1_3[I1, A, B, C] {} = ???
//  def apply(c: count.type) : Community_In_1_3[I1, A, B, Int] {} = ???
//}
//
// trait Community_In_1_4[I1, A, B, C, D] extends Community with In_1_4[I1, A, B, C, D] {
//  lazy val name          : name        [Community_In_1_5[I1, A, B, C, D, String]]      with Community_In_1_5[I1, A, B, C, D, String]      = ???
//  lazy val url           : url         [Community_In_1_5[I1, A, B, C, D, String]]      with Community_In_1_5[I1, A, B, C, D, String]      = ???
//  lazy val category      : category    [Community_In_1_5[I1, A, B, C, D, Set[String]]] with Community_In_1_5[I1, A, B, C, D, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_1_5[I1, A, B, C, D, String]]      with Community_In_1_5[I1, A, B, C, D, String]      = ???
//  lazy val `type`        : `type`      [Community_In_1_5[I1, A, B, C, D, String]]      with Community_In_1_5[I1, A, B, C, D, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_1_5[I1, A, B, C, D, Long]]        with Community_In_1_5[I1, A, B, C, D, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_1_4[I1, A, B, C, D]] with Community_In_1_4[I1, A, B, C, D]              = ???
//  lazy val url_          :  url         [Community_In_1_4[I1, A, B, C, D]] with Community_In_1_4[I1, A, B, C, D]              = ???
//  lazy val category_     :  category    [Community_In_1_4[I1, A, B, C, D]] with Community_In_1_4[I1, A, B, C, D]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_1_4[I1, A, B, C, D]] with Community_In_1_4[I1, A, B, C, D]              = ???
//  lazy val type_         :  `type`      [Community_In_1_4[I1, A, B, C, D]] with Community_In_1_4[I1, A, B, C, D]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_1_4[I1, A, B, C, D]] with Community_In_1_4[I1, A, B, C, D]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_1_4[I1, A, B, C, D] = ???
//  def apply(in: ?.type)    : Community_In_2_4[I1, D, A, B, C, D] {} = ???
//  def <(in: ?.type)        : Community_In_2_4[I1, D, A, B, C, D] {} = ???
//  def contains(in: ?.type) : Community_In_2_4[I1, D, A, B, C, D] {} = ???
//  def apply(m: maybe.type) : Community_In_1_4[I1, A, B, C, D] {} = ???
//  def apply(c: count.type) : Community_In_1_4[I1, A, B, C, Int] {} = ???
//}
//
// trait Community_In_1_5[I1, A, B, C, D, E] extends Community with In_1_5[I1, A, B, C, D, E] {
//  lazy val name          : name        [Community_In_1_6[I1, A, B, C, D, E, String]]      with Community_In_1_6[I1, A, B, C, D, E, String]      = ???
//  lazy val url           : url         [Community_In_1_6[I1, A, B, C, D, E, String]]      with Community_In_1_6[I1, A, B, C, D, E, String]      = ???
//  lazy val category      : category    [Community_In_1_6[I1, A, B, C, D, E, Set[String]]] with Community_In_1_6[I1, A, B, C, D, E, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_1_6[I1, A, B, C, D, E, String]]      with Community_In_1_6[I1, A, B, C, D, E, String]      = ???
//  lazy val `type`        : `type`      [Community_In_1_6[I1, A, B, C, D, E, String]]      with Community_In_1_6[I1, A, B, C, D, E, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_1_6[I1, A, B, C, D, E, Long]]        with Community_In_1_6[I1, A, B, C, D, E, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_1_5[I1, A, B, C, D, E]] with Community_In_1_5[I1, A, B, C, D, E]              = ???
//  lazy val url_          :  url         [Community_In_1_5[I1, A, B, C, D, E]] with Community_In_1_5[I1, A, B, C, D, E]              = ???
//  lazy val category_     :  category    [Community_In_1_5[I1, A, B, C, D, E]] with Community_In_1_5[I1, A, B, C, D, E]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_1_5[I1, A, B, C, D, E]] with Community_In_1_5[I1, A, B, C, D, E]              = ???
//  lazy val type_         :  `type`      [Community_In_1_5[I1, A, B, C, D, E]] with Community_In_1_5[I1, A, B, C, D, E]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_1_5[I1, A, B, C, D, E]] with Community_In_1_5[I1, A, B, C, D, E]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_1_5[I1, A, B, C, D, E] = ???
//  def apply(in: ?.type)    : Community_In_2_5[I1, E, A, B, C, D, E] {} = ???
//  def <(in: ?.type)        : Community_In_2_5[I1, E, A, B, C, D, E] {} = ???
//  def contains(in: ?.type) : Community_In_2_5[I1, E, A, B, C, D, E] {} = ???
//  def apply(m: maybe.type) : Community_In_1_5[I1, A, B, C, D, E] {} = ???
//  def apply(c: count.type) : Community_In_1_5[I1, A, B, C, D, Int] {} = ???
//}
//
// trait Community_In_1_6[I1, A, B, C, D, E, F] extends Community with In_1_6[I1, A, B, C, D, E, F] {
//  lazy val name          : name        [Community_In_1_7[I1, A, B, C, D, E, F, String]]      with Community_In_1_7[I1, A, B, C, D, E, F, String]      = ???
//  lazy val url           : url         [Community_In_1_7[I1, A, B, C, D, E, F, String]]      with Community_In_1_7[I1, A, B, C, D, E, F, String]      = ???
//  lazy val category      : category    [Community_In_1_7[I1, A, B, C, D, E, F, Set[String]]] with Community_In_1_7[I1, A, B, C, D, E, F, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_1_7[I1, A, B, C, D, E, F, String]]      with Community_In_1_7[I1, A, B, C, D, E, F, String]      = ???
//  lazy val `type`        : `type`      [Community_In_1_7[I1, A, B, C, D, E, F, String]]      with Community_In_1_7[I1, A, B, C, D, E, F, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_1_7[I1, A, B, C, D, E, F, Long]]        with Community_In_1_7[I1, A, B, C, D, E, F, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_1_6[I1, A, B, C, D, E, F]] with Community_In_1_6[I1, A, B, C, D, E, F]              = ???
//  lazy val url_          :  url         [Community_In_1_6[I1, A, B, C, D, E, F]] with Community_In_1_6[I1, A, B, C, D, E, F]              = ???
//  lazy val category_     :  category    [Community_In_1_6[I1, A, B, C, D, E, F]] with Community_In_1_6[I1, A, B, C, D, E, F]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_1_6[I1, A, B, C, D, E, F]] with Community_In_1_6[I1, A, B, C, D, E, F]              = ???
//  lazy val type_         :  `type`      [Community_In_1_6[I1, A, B, C, D, E, F]] with Community_In_1_6[I1, A, B, C, D, E, F]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_1_6[I1, A, B, C, D, E, F]] with Community_In_1_6[I1, A, B, C, D, E, F]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_1_6[I1, A, B, C, D, E, F] = ???
//  def apply(in: ?.type)    : Community_In_2_6[I1, F, A, B, C, D, E, F] {} = ???
//  def <(in: ?.type)        : Community_In_2_6[I1, F, A, B, C, D, E, F] {} = ???
//  def contains(in: ?.type) : Community_In_2_6[I1, F, A, B, C, D, E, F] {} = ???
//  def apply(m: maybe.type) : Community_In_1_6[I1, A, B, C, D, E, F] {} = ???
//  def apply(c: count.type) : Community_In_1_6[I1, A, B, C, D, E, Int] {} = ???
//}
//
// trait Community_In_1_7[I1, A, B, C, D, E, F, G] extends Community with In_1_7[I1, A, B, C, D, E, F, G] {
//  lazy val name          : name        [Community_In_1_8[I1, A, B, C, D, E, F, G, String]]      with Community_In_1_8[I1, A, B, C, D, E, F, G, String]      = ???
//  lazy val url           : url         [Community_In_1_8[I1, A, B, C, D, E, F, G, String]]      with Community_In_1_8[I1, A, B, C, D, E, F, G, String]      = ???
//  lazy val category      : category    [Community_In_1_8[I1, A, B, C, D, E, F, G, Set[String]]] with Community_In_1_8[I1, A, B, C, D, E, F, G, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_1_8[I1, A, B, C, D, E, F, G, String]]      with Community_In_1_8[I1, A, B, C, D, E, F, G, String]      = ???
//  lazy val `type`        : `type`      [Community_In_1_8[I1, A, B, C, D, E, F, G, String]]      with Community_In_1_8[I1, A, B, C, D, E, F, G, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_1_8[I1, A, B, C, D, E, F, G, Long]]        with Community_In_1_8[I1, A, B, C, D, E, F, G, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_1_7[I1, A, B, C, D, E, F, G]] with Community_In_1_7[I1, A, B, C, D, E, F, G]              = ???
//  lazy val url_          :  url         [Community_In_1_7[I1, A, B, C, D, E, F, G]] with Community_In_1_7[I1, A, B, C, D, E, F, G]              = ???
//  lazy val category_     :  category    [Community_In_1_7[I1, A, B, C, D, E, F, G]] with Community_In_1_7[I1, A, B, C, D, E, F, G]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_1_7[I1, A, B, C, D, E, F, G]] with Community_In_1_7[I1, A, B, C, D, E, F, G]              = ???
//  lazy val type_         :  `type`      [Community_In_1_7[I1, A, B, C, D, E, F, G]] with Community_In_1_7[I1, A, B, C, D, E, F, G]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_1_7[I1, A, B, C, D, E, F, G]] with Community_In_1_7[I1, A, B, C, D, E, F, G]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_1_7[I1, A, B, C, D, E, F, G] = ???
//  def apply(in: ?.type)    : Community_In_2_7[I1, G, A, B, C, D, E, F, G] {} = ???
//  def <(in: ?.type)        : Community_In_2_7[I1, G, A, B, C, D, E, F, G] {} = ???
//  def contains(in: ?.type) : Community_In_2_7[I1, G, A, B, C, D, E, F, G] {} = ???
//  def apply(m: maybe.type) : Community_In_1_7[I1, A, B, C, D, E, F, G] {} = ???
//  def apply(c: count.type) : Community_In_1_7[I1, A, B, C, D, E, F, Int] {} = ???
//}
//
// trait Community_In_1_8[I1, A, B, C, D, E, F, G, H] extends Community with In_1_8[I1, A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 2 inputs *******************************/
//
// trait Community_In_2_0[I1, I2] extends Community with In_2_0[I1, I2] {
//  lazy val name          : name        [Community_In_2_1[I1, I2, String]]      with Community_In_2_1[I1, I2, String]      = ???
//  lazy val url           : url         [Community_In_2_1[I1, I2, String]]      with Community_In_2_1[I1, I2, String]      = ???
//  lazy val category      : category    [Community_In_2_1[I1, I2, Set[String]]] with Community_In_2_1[I1, I2, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_2_1[I1, I2, String]]      with Community_In_2_1[I1, I2, String]      = ???
//  lazy val `type`        : `type`      [Community_In_2_1[I1, I2, String]]      with Community_In_2_1[I1, I2, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_2_1[I1, I2, Long]]        with Community_In_2_1[I1, I2, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_2_0[I1, I2]] with Community_In_2_0[I1, I2]              = ???
//  lazy val url_          :  url         [Community_In_2_0[I1, I2]] with Community_In_2_0[I1, I2]              = ???
//  lazy val category_     :  category    [Community_In_2_0[I1, I2]] with Community_In_2_0[I1, I2]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_2_0[I1, I2]] with Community_In_2_0[I1, I2]              = ???
//  lazy val type_         :  `type`      [Community_In_2_0[I1, I2]] with Community_In_2_0[I1, I2]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_2_0[I1, I2]] with Community_In_2_0[I1, I2]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_2_0[I1, I2] = ???
//}
//
// trait Community_In_2_1[I1, I2, A] extends Community with In_2_1[I1, I2, A] {
//  lazy val name          : name        [Community_In_2_2[I1, I2, A, String]]      with Community_In_2_2[I1, I2, A, String]      = ???
//  lazy val url           : url         [Community_In_2_2[I1, I2, A, String]]      with Community_In_2_2[I1, I2, A, String]      = ???
//  lazy val category      : category    [Community_In_2_2[I1, I2, A, Set[String]]] with Community_In_2_2[I1, I2, A, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_2_2[I1, I2, A, String]]      with Community_In_2_2[I1, I2, A, String]      = ???
//  lazy val `type`        : `type`      [Community_In_2_2[I1, I2, A, String]]      with Community_In_2_2[I1, I2, A, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_2_2[I1, I2, A, Long]]        with Community_In_2_2[I1, I2, A, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_2_1[I1, I2, A]] with Community_In_2_1[I1, I2, A]              = ???
//  lazy val url_          :  url         [Community_In_2_1[I1, I2, A]] with Community_In_2_1[I1, I2, A]              = ???
//  lazy val category_     :  category    [Community_In_2_1[I1, I2, A]] with Community_In_2_1[I1, I2, A]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_2_1[I1, I2, A]] with Community_In_2_1[I1, I2, A]              = ???
//  lazy val type_         :  `type`      [Community_In_2_1[I1, I2, A]] with Community_In_2_1[I1, I2, A]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_2_1[I1, I2, A]] with Community_In_2_1[I1, I2, A]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_2_1[I1, I2, A] = ???
//  def apply(in: ?.type)    : Community_In_3_1[I1, I2, A, A] {} = ???
//  def <(in: ?.type)        : Community_In_3_1[I1, I2, A, A] {} = ???
//  def contains(in: ?.type) : Community_In_3_1[I1, I2, A, A] {} = ???
//  def apply(m: maybe.type) : Community_In_2_1[I1, I2, A] {} = ???
//  def apply(c: count.type) : Community_In_2_1[I1, I2, Int] {} = ???
//}
//
// trait Community_In_2_2[I1, I2, A, B] extends Community with In_2_2[I1, I2, A, B] {
//  lazy val name          : name        [Community_In_2_3[I1, I2, A, B, String]]      with Community_In_2_3[I1, I2, A, B, String]      = ???
//  lazy val url           : url         [Community_In_2_3[I1, I2, A, B, String]]      with Community_In_2_3[I1, I2, A, B, String]      = ???
//  lazy val category      : category    [Community_In_2_3[I1, I2, A, B, Set[String]]] with Community_In_2_3[I1, I2, A, B, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_2_3[I1, I2, A, B, String]]      with Community_In_2_3[I1, I2, A, B, String]      = ???
//  lazy val `type`        : `type`      [Community_In_2_3[I1, I2, A, B, String]]      with Community_In_2_3[I1, I2, A, B, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_2_3[I1, I2, A, B, Long]]        with Community_In_2_3[I1, I2, A, B, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_2_2[I1, I2, A, B]] with Community_In_2_2[I1, I2, A, B]              = ???
//  lazy val url_          :  url         [Community_In_2_2[I1, I2, A, B]] with Community_In_2_2[I1, I2, A, B]              = ???
//  lazy val category_     :  category    [Community_In_2_2[I1, I2, A, B]] with Community_In_2_2[I1, I2, A, B]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_2_2[I1, I2, A, B]] with Community_In_2_2[I1, I2, A, B]              = ???
//  lazy val type_         :  `type`      [Community_In_2_2[I1, I2, A, B]] with Community_In_2_2[I1, I2, A, B]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_2_2[I1, I2, A, B]] with Community_In_2_2[I1, I2, A, B]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_2_2[I1, I2, A, B] = ???
//  def apply(in: ?.type)    : Community_In_3_2[I1, I2, B, A, B] {} = ???
//  def <(in: ?.type)        : Community_In_3_2[I1, I2, B, A, B] {} = ???
//  def contains(in: ?.type) : Community_In_3_2[I1, I2, B, A, B] {} = ???
//  def apply(m: maybe.type) : Community_In_2_2[I1, I2, A, B] {} = ???
//  def apply(c: count.type) : Community_In_2_2[I1, I2, A, Int] {} = ???
//}
//
// trait Community_In_2_3[I1, I2, A, B, C] extends Community with In_2_3[I1, I2, A, B, C] {
//  lazy val name          : name        [Community_In_2_4[I1, I2, A, B, C, String]]      with Community_In_2_4[I1, I2, A, B, C, String]      = ???
//  lazy val url           : url         [Community_In_2_4[I1, I2, A, B, C, String]]      with Community_In_2_4[I1, I2, A, B, C, String]      = ???
//  lazy val category      : category    [Community_In_2_4[I1, I2, A, B, C, Set[String]]] with Community_In_2_4[I1, I2, A, B, C, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_2_4[I1, I2, A, B, C, String]]      with Community_In_2_4[I1, I2, A, B, C, String]      = ???
//  lazy val `type`        : `type`      [Community_In_2_4[I1, I2, A, B, C, String]]      with Community_In_2_4[I1, I2, A, B, C, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_2_4[I1, I2, A, B, C, Long]]        with Community_In_2_4[I1, I2, A, B, C, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_2_3[I1, I2, A, B, C]] with Community_In_2_3[I1, I2, A, B, C]              = ???
//  lazy val url_          :  url         [Community_In_2_3[I1, I2, A, B, C]] with Community_In_2_3[I1, I2, A, B, C]              = ???
//  lazy val category_     :  category    [Community_In_2_3[I1, I2, A, B, C]] with Community_In_2_3[I1, I2, A, B, C]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_2_3[I1, I2, A, B, C]] with Community_In_2_3[I1, I2, A, B, C]              = ???
//  lazy val type_         :  `type`      [Community_In_2_3[I1, I2, A, B, C]] with Community_In_2_3[I1, I2, A, B, C]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_2_3[I1, I2, A, B, C]] with Community_In_2_3[I1, I2, A, B, C]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_2_3[I1, I2, A, B, C] = ???
//  def apply(in: ?.type)    : Community_In_3_3[I1, I2, C, A, B, C] {} = ???
//  def <(in: ?.type)        : Community_In_3_3[I1, I2, C, A, B, C] {} = ???
//  def contains(in: ?.type) : Community_In_3_3[I1, I2, C, A, B, C] {} = ???
//  def apply(m: maybe.type) : Community_In_2_3[I1, I2, A, B, C] {} = ???
//  def apply(c: count.type) : Community_In_2_3[I1, I2, A, B, Int] {} = ???
//}
//
// trait Community_In_2_4[I1, I2, A, B, C, D] extends Community with In_2_4[I1, I2, A, B, C, D] {
//  lazy val name          : name        [Community_In_2_5[I1, I2, A, B, C, D, String]]      with Community_In_2_5[I1, I2, A, B, C, D, String]      = ???
//  lazy val url           : url         [Community_In_2_5[I1, I2, A, B, C, D, String]]      with Community_In_2_5[I1, I2, A, B, C, D, String]      = ???
//  lazy val category      : category    [Community_In_2_5[I1, I2, A, B, C, D, Set[String]]] with Community_In_2_5[I1, I2, A, B, C, D, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_2_5[I1, I2, A, B, C, D, String]]      with Community_In_2_5[I1, I2, A, B, C, D, String]      = ???
//  lazy val `type`        : `type`      [Community_In_2_5[I1, I2, A, B, C, D, String]]      with Community_In_2_5[I1, I2, A, B, C, D, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_2_5[I1, I2, A, B, C, D, Long]]        with Community_In_2_5[I1, I2, A, B, C, D, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_2_4[I1, I2, A, B, C, D]] with Community_In_2_4[I1, I2, A, B, C, D]              = ???
//  lazy val url_          :  url         [Community_In_2_4[I1, I2, A, B, C, D]] with Community_In_2_4[I1, I2, A, B, C, D]              = ???
//  lazy val category_     :  category    [Community_In_2_4[I1, I2, A, B, C, D]] with Community_In_2_4[I1, I2, A, B, C, D]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_2_4[I1, I2, A, B, C, D]] with Community_In_2_4[I1, I2, A, B, C, D]              = ???
//  lazy val type_         :  `type`      [Community_In_2_4[I1, I2, A, B, C, D]] with Community_In_2_4[I1, I2, A, B, C, D]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_2_4[I1, I2, A, B, C, D]] with Community_In_2_4[I1, I2, A, B, C, D]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_2_4[I1, I2, A, B, C, D] = ???
//  def apply(in: ?.type)    : Community_In_3_4[I1, I2, D, A, B, C, D] {} = ???
//  def <(in: ?.type)        : Community_In_3_4[I1, I2, D, A, B, C, D] {} = ???
//  def contains(in: ?.type) : Community_In_3_4[I1, I2, D, A, B, C, D] {} = ???
//  def apply(m: maybe.type) : Community_In_2_4[I1, I2, A, B, C, D] {} = ???
//  def apply(c: count.type) : Community_In_2_4[I1, I2, A, B, C, Int] {} = ???
//}
//
// trait Community_In_2_5[I1, I2, A, B, C, D, E] extends Community with In_2_5[I1, I2, A, B, C, D, E] {
//  lazy val name          : name        [Community_In_2_6[I1, I2, A, B, C, D, E, String]]      with Community_In_2_6[I1, I2, A, B, C, D, E, String]      = ???
//  lazy val url           : url         [Community_In_2_6[I1, I2, A, B, C, D, E, String]]      with Community_In_2_6[I1, I2, A, B, C, D, E, String]      = ???
//  lazy val category      : category    [Community_In_2_6[I1, I2, A, B, C, D, E, Set[String]]] with Community_In_2_6[I1, I2, A, B, C, D, E, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_2_6[I1, I2, A, B, C, D, E, String]]      with Community_In_2_6[I1, I2, A, B, C, D, E, String]      = ???
//  lazy val `type`        : `type`      [Community_In_2_6[I1, I2, A, B, C, D, E, String]]      with Community_In_2_6[I1, I2, A, B, C, D, E, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_2_6[I1, I2, A, B, C, D, E, Long]]        with Community_In_2_6[I1, I2, A, B, C, D, E, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_2_5[I1, I2, A, B, C, D, E]] with Community_In_2_5[I1, I2, A, B, C, D, E]              = ???
//  lazy val url_          :  url         [Community_In_2_5[I1, I2, A, B, C, D, E]] with Community_In_2_5[I1, I2, A, B, C, D, E]              = ???
//  lazy val category_     :  category    [Community_In_2_5[I1, I2, A, B, C, D, E]] with Community_In_2_5[I1, I2, A, B, C, D, E]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_2_5[I1, I2, A, B, C, D, E]] with Community_In_2_5[I1, I2, A, B, C, D, E]              = ???
//  lazy val type_         :  `type`      [Community_In_2_5[I1, I2, A, B, C, D, E]] with Community_In_2_5[I1, I2, A, B, C, D, E]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_2_5[I1, I2, A, B, C, D, E]] with Community_In_2_5[I1, I2, A, B, C, D, E]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_2_5[I1, I2, A, B, C, D, E] = ???
//  def apply(in: ?.type)    : Community_In_3_5[I1, I2, E, A, B, C, D, E] {} = ???
//  def <(in: ?.type)        : Community_In_3_5[I1, I2, E, A, B, C, D, E] {} = ???
//  def contains(in: ?.type) : Community_In_3_5[I1, I2, E, A, B, C, D, E] {} = ???
//  def apply(m: maybe.type) : Community_In_2_5[I1, I2, A, B, C, D, E] {} = ???
//  def apply(c: count.type) : Community_In_2_5[I1, I2, A, B, C, D, Int] {} = ???
//}
//
// trait Community_In_2_6[I1, I2, A, B, C, D, E, F] extends Community with In_2_6[I1, I2, A, B, C, D, E, F] {
//  lazy val name          : name        [Community_In_2_7[I1, I2, A, B, C, D, E, F, String]]      with Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      = ???
//  lazy val url           : url         [Community_In_2_7[I1, I2, A, B, C, D, E, F, String]]      with Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      = ???
//  lazy val category      : category    [Community_In_2_7[I1, I2, A, B, C, D, E, F, Set[String]]] with Community_In_2_7[I1, I2, A, B, C, D, E, F, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_2_7[I1, I2, A, B, C, D, E, F, String]]      with Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      = ???
//  lazy val `type`        : `type`      [Community_In_2_7[I1, I2, A, B, C, D, E, F, String]]      with Community_In_2_7[I1, I2, A, B, C, D, E, F, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_2_7[I1, I2, A, B, C, D, E, F, Long]]        with Community_In_2_7[I1, I2, A, B, C, D, E, F, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_2_6[I1, I2, A, B, C, D, E, F]] with Community_In_2_6[I1, I2, A, B, C, D, E, F]              = ???
//  lazy val url_          :  url         [Community_In_2_6[I1, I2, A, B, C, D, E, F]] with Community_In_2_6[I1, I2, A, B, C, D, E, F]              = ???
//  lazy val category_     :  category    [Community_In_2_6[I1, I2, A, B, C, D, E, F]] with Community_In_2_6[I1, I2, A, B, C, D, E, F]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_2_6[I1, I2, A, B, C, D, E, F]] with Community_In_2_6[I1, I2, A, B, C, D, E, F]              = ???
//  lazy val type_         :  `type`      [Community_In_2_6[I1, I2, A, B, C, D, E, F]] with Community_In_2_6[I1, I2, A, B, C, D, E, F]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_2_6[I1, I2, A, B, C, D, E, F]] with Community_In_2_6[I1, I2, A, B, C, D, E, F]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_2_6[I1, I2, A, B, C, D, E, F] = ???
//  def apply(in: ?.type)    : Community_In_3_6[I1, I2, F, A, B, C, D, E, F] {} = ???
//  def <(in: ?.type)        : Community_In_3_6[I1, I2, F, A, B, C, D, E, F] {} = ???
//  def contains(in: ?.type) : Community_In_3_6[I1, I2, F, A, B, C, D, E, F] {} = ???
//  def apply(m: maybe.type) : Community_In_2_6[I1, I2, A, B, C, D, E, F] {} = ???
//  def apply(c: count.type) : Community_In_2_6[I1, I2, A, B, C, D, E, Int] {} = ???
//}
//
// trait Community_In_2_7[I1, I2, A, B, C, D, E, F, G] extends Community with In_2_7[I1, I2, A, B, C, D, E, F, G] {
//  lazy val name          : name        [Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]]      with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      = ???
//  lazy val url           : url         [Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]]      with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      = ???
//  lazy val category      : category    [Community_In_2_8[I1, I2, A, B, C, D, E, F, G, Set[String]]] with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]]      with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      = ???
//  lazy val `type`        : `type`      [Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]]      with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]]        with Community_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_2_7[I1, I2, A, B, C, D, E, F, G]] with Community_In_2_7[I1, I2, A, B, C, D, E, F, G]              = ???
//  lazy val url_          :  url         [Community_In_2_7[I1, I2, A, B, C, D, E, F, G]] with Community_In_2_7[I1, I2, A, B, C, D, E, F, G]              = ???
//  lazy val category_     :  category    [Community_In_2_7[I1, I2, A, B, C, D, E, F, G]] with Community_In_2_7[I1, I2, A, B, C, D, E, F, G]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_2_7[I1, I2, A, B, C, D, E, F, G]] with Community_In_2_7[I1, I2, A, B, C, D, E, F, G]              = ???
//  lazy val type_         :  `type`      [Community_In_2_7[I1, I2, A, B, C, D, E, F, G]] with Community_In_2_7[I1, I2, A, B, C, D, E, F, G]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_2_7[I1, I2, A, B, C, D, E, F, G]] with Community_In_2_7[I1, I2, A, B, C, D, E, F, G]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_2_7[I1, I2, A, B, C, D, E, F, G] = ???
//  def apply(in: ?.type)    : Community_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {} = ???
//  def <(in: ?.type)        : Community_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {} = ???
//  def contains(in: ?.type) : Community_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {} = ???
//  def apply(m: maybe.type) : Community_In_2_7[I1, I2, A, B, C, D, E, F, G] {} = ???
//  def apply(c: count.type) : Community_In_2_7[I1, I2, A, B, C, D, E, F, Int] {} = ???
//}
//
// trait Community_In_2_8[I1, I2, A, B, C, D, E, F, G, H] extends Community with In_2_8[I1, I2, A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 3 inputs *******************************/
//
// trait Community_In_3_0[I1, I2, I3] extends Community with In_3_0[I1, I2, I3] {
//  lazy val name          : name        [Community_In_3_1[I1, I2, I3, String]]      with Community_In_3_1[I1, I2, I3, String]      = ???
//  lazy val url           : url         [Community_In_3_1[I1, I2, I3, String]]      with Community_In_3_1[I1, I2, I3, String]      = ???
//  lazy val category      : category    [Community_In_3_1[I1, I2, I3, Set[String]]] with Community_In_3_1[I1, I2, I3, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_3_1[I1, I2, I3, String]]      with Community_In_3_1[I1, I2, I3, String]      = ???
//  lazy val `type`        : `type`      [Community_In_3_1[I1, I2, I3, String]]      with Community_In_3_1[I1, I2, I3, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_3_1[I1, I2, I3, Long]]        with Community_In_3_1[I1, I2, I3, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_3_0[I1, I2, I3]] with Community_In_3_0[I1, I2, I3]              = ???
//  lazy val url_          :  url         [Community_In_3_0[I1, I2, I3]] with Community_In_3_0[I1, I2, I3]              = ???
//  lazy val category_     :  category    [Community_In_3_0[I1, I2, I3]] with Community_In_3_0[I1, I2, I3]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_3_0[I1, I2, I3]] with Community_In_3_0[I1, I2, I3]              = ???
//  lazy val type_         :  `type`      [Community_In_3_0[I1, I2, I3]] with Community_In_3_0[I1, I2, I3]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_3_0[I1, I2, I3]] with Community_In_3_0[I1, I2, I3]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_3_0[I1, I2, I3] = ???
//}
//
// trait Community_In_3_1[I1, I2, I3, A] extends Community with In_3_1[I1, I2, I3, A] {
//  lazy val name          : name        [Community_In_3_2[I1, I2, I3, A, String]]      with Community_In_3_2[I1, I2, I3, A, String]      = ???
//  lazy val url           : url         [Community_In_3_2[I1, I2, I3, A, String]]      with Community_In_3_2[I1, I2, I3, A, String]      = ???
//  lazy val category      : category    [Community_In_3_2[I1, I2, I3, A, Set[String]]] with Community_In_3_2[I1, I2, I3, A, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_3_2[I1, I2, I3, A, String]]      with Community_In_3_2[I1, I2, I3, A, String]      = ???
//  lazy val `type`        : `type`      [Community_In_3_2[I1, I2, I3, A, String]]      with Community_In_3_2[I1, I2, I3, A, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_3_2[I1, I2, I3, A, Long]]        with Community_In_3_2[I1, I2, I3, A, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_3_1[I1, I2, I3, A]] with Community_In_3_1[I1, I2, I3, A]              = ???
//  lazy val url_          :  url         [Community_In_3_1[I1, I2, I3, A]] with Community_In_3_1[I1, I2, I3, A]              = ???
//  lazy val category_     :  category    [Community_In_3_1[I1, I2, I3, A]] with Community_In_3_1[I1, I2, I3, A]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_3_1[I1, I2, I3, A]] with Community_In_3_1[I1, I2, I3, A]              = ???
//  lazy val type_         :  `type`      [Community_In_3_1[I1, I2, I3, A]] with Community_In_3_1[I1, I2, I3, A]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_3_1[I1, I2, I3, A]] with Community_In_3_1[I1, I2, I3, A]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_3_1[I1, I2, I3, A] = ???
//  def apply(m: maybe.type) : Community_In_3_1[I1, I2, I3, A] {} = ???
//  def apply(c: count.type) : Community_In_3_1[I1, I2, I3, Int] {} = ???
//}
//
// trait Community_In_3_2[I1, I2, I3, A, B] extends Community with In_3_2[I1, I2, I3, A, B] {
//  lazy val name          : name        [Community_In_3_3[I1, I2, I3, A, B, String]]      with Community_In_3_3[I1, I2, I3, A, B, String]      = ???
//  lazy val url           : url         [Community_In_3_3[I1, I2, I3, A, B, String]]      with Community_In_3_3[I1, I2, I3, A, B, String]      = ???
//  lazy val category      : category    [Community_In_3_3[I1, I2, I3, A, B, Set[String]]] with Community_In_3_3[I1, I2, I3, A, B, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_3_3[I1, I2, I3, A, B, String]]      with Community_In_3_3[I1, I2, I3, A, B, String]      = ???
//  lazy val `type`        : `type`      [Community_In_3_3[I1, I2, I3, A, B, String]]      with Community_In_3_3[I1, I2, I3, A, B, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_3_3[I1, I2, I3, A, B, Long]]        with Community_In_3_3[I1, I2, I3, A, B, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_3_2[I1, I2, I3, A, B]] with Community_In_3_2[I1, I2, I3, A, B]              = ???
//  lazy val url_          :  url         [Community_In_3_2[I1, I2, I3, A, B]] with Community_In_3_2[I1, I2, I3, A, B]              = ???
//  lazy val category_     :  category    [Community_In_3_2[I1, I2, I3, A, B]] with Community_In_3_2[I1, I2, I3, A, B]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_3_2[I1, I2, I3, A, B]] with Community_In_3_2[I1, I2, I3, A, B]              = ???
//  lazy val type_         :  `type`      [Community_In_3_2[I1, I2, I3, A, B]] with Community_In_3_2[I1, I2, I3, A, B]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_3_2[I1, I2, I3, A, B]] with Community_In_3_2[I1, I2, I3, A, B]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_3_2[I1, I2, I3, A, B] = ???
//  def apply(m: maybe.type) : Community_In_3_2[I1, I2, I3, A, B] {} = ???
//  def apply(c: count.type) : Community_In_3_2[I1, I2, I3, A, Int] {} = ???
//}
//
// trait Community_In_3_3[I1, I2, I3, A, B, C] extends Community with In_3_3[I1, I2, I3, A, B, C] {
//  lazy val name          : name        [Community_In_3_4[I1, I2, I3, A, B, C, String]]      with Community_In_3_4[I1, I2, I3, A, B, C, String]      = ???
//  lazy val url           : url         [Community_In_3_4[I1, I2, I3, A, B, C, String]]      with Community_In_3_4[I1, I2, I3, A, B, C, String]      = ???
//  lazy val category      : category    [Community_In_3_4[I1, I2, I3, A, B, C, Set[String]]] with Community_In_3_4[I1, I2, I3, A, B, C, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_3_4[I1, I2, I3, A, B, C, String]]      with Community_In_3_4[I1, I2, I3, A, B, C, String]      = ???
//  lazy val `type`        : `type`      [Community_In_3_4[I1, I2, I3, A, B, C, String]]      with Community_In_3_4[I1, I2, I3, A, B, C, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_3_4[I1, I2, I3, A, B, C, Long]]        with Community_In_3_4[I1, I2, I3, A, B, C, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_3_3[I1, I2, I3, A, B, C]] with Community_In_3_3[I1, I2, I3, A, B, C]              = ???
//  lazy val url_          :  url         [Community_In_3_3[I1, I2, I3, A, B, C]] with Community_In_3_3[I1, I2, I3, A, B, C]              = ???
//  lazy val category_     :  category    [Community_In_3_3[I1, I2, I3, A, B, C]] with Community_In_3_3[I1, I2, I3, A, B, C]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_3_3[I1, I2, I3, A, B, C]] with Community_In_3_3[I1, I2, I3, A, B, C]              = ???
//  lazy val type_         :  `type`      [Community_In_3_3[I1, I2, I3, A, B, C]] with Community_In_3_3[I1, I2, I3, A, B, C]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_3_3[I1, I2, I3, A, B, C]] with Community_In_3_3[I1, I2, I3, A, B, C]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_3_3[I1, I2, I3, A, B, C] = ???
//  def apply(m: maybe.type) : Community_In_3_3[I1, I2, I3, A, B, C] {} = ???
//  def apply(c: count.type) : Community_In_3_3[I1, I2, I3, A, B, Int] {} = ???
//}
//
// trait Community_In_3_4[I1, I2, I3, A, B, C, D] extends Community with In_3_4[I1, I2, I3, A, B, C, D] {
//  lazy val name          : name        [Community_In_3_5[I1, I2, I3, A, B, C, D, String]]      with Community_In_3_5[I1, I2, I3, A, B, C, D, String]      = ???
//  lazy val url           : url         [Community_In_3_5[I1, I2, I3, A, B, C, D, String]]      with Community_In_3_5[I1, I2, I3, A, B, C, D, String]      = ???
//  lazy val category      : category    [Community_In_3_5[I1, I2, I3, A, B, C, D, Set[String]]] with Community_In_3_5[I1, I2, I3, A, B, C, D, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_3_5[I1, I2, I3, A, B, C, D, String]]      with Community_In_3_5[I1, I2, I3, A, B, C, D, String]      = ???
//  lazy val `type`        : `type`      [Community_In_3_5[I1, I2, I3, A, B, C, D, String]]      with Community_In_3_5[I1, I2, I3, A, B, C, D, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_3_5[I1, I2, I3, A, B, C, D, Long]]        with Community_In_3_5[I1, I2, I3, A, B, C, D, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_3_4[I1, I2, I3, A, B, C, D]] with Community_In_3_4[I1, I2, I3, A, B, C, D]              = ???
//  lazy val url_          :  url         [Community_In_3_4[I1, I2, I3, A, B, C, D]] with Community_In_3_4[I1, I2, I3, A, B, C, D]              = ???
//  lazy val category_     :  category    [Community_In_3_4[I1, I2, I3, A, B, C, D]] with Community_In_3_4[I1, I2, I3, A, B, C, D]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_3_4[I1, I2, I3, A, B, C, D]] with Community_In_3_4[I1, I2, I3, A, B, C, D]              = ???
//  lazy val type_         :  `type`      [Community_In_3_4[I1, I2, I3, A, B, C, D]] with Community_In_3_4[I1, I2, I3, A, B, C, D]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_3_4[I1, I2, I3, A, B, C, D]] with Community_In_3_4[I1, I2, I3, A, B, C, D]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_3_4[I1, I2, I3, A, B, C, D] = ???
//  def apply(m: maybe.type) : Community_In_3_4[I1, I2, I3, A, B, C, D] {} = ???
//  def apply(c: count.type) : Community_In_3_4[I1, I2, I3, A, B, C, Int] {} = ???
//}
//
// trait Community_In_3_5[I1, I2, I3, A, B, C, D, E] extends Community with In_3_5[I1, I2, I3, A, B, C, D, E] {
//  lazy val name          : name        [Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]]      with Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      = ???
//  lazy val url           : url         [Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]]      with Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      = ???
//  lazy val category      : category    [Community_In_3_6[I1, I2, I3, A, B, C, D, E, Set[String]]] with Community_In_3_6[I1, I2, I3, A, B, C, D, E, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]]      with Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      = ???
//  lazy val `type`        : `type`      [Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]]      with Community_In_3_6[I1, I2, I3, A, B, C, D, E, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_3_6[I1, I2, I3, A, B, C, D, E, Long]]        with Community_In_3_6[I1, I2, I3, A, B, C, D, E, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_3_5[I1, I2, I3, A, B, C, D, E]] with Community_In_3_5[I1, I2, I3, A, B, C, D, E]              = ???
//  lazy val url_          :  url         [Community_In_3_5[I1, I2, I3, A, B, C, D, E]] with Community_In_3_5[I1, I2, I3, A, B, C, D, E]              = ???
//  lazy val category_     :  category    [Community_In_3_5[I1, I2, I3, A, B, C, D, E]] with Community_In_3_5[I1, I2, I3, A, B, C, D, E]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_3_5[I1, I2, I3, A, B, C, D, E]] with Community_In_3_5[I1, I2, I3, A, B, C, D, E]              = ???
//  lazy val type_         :  `type`      [Community_In_3_5[I1, I2, I3, A, B, C, D, E]] with Community_In_3_5[I1, I2, I3, A, B, C, D, E]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_3_5[I1, I2, I3, A, B, C, D, E]] with Community_In_3_5[I1, I2, I3, A, B, C, D, E]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_3_5[I1, I2, I3, A, B, C, D, E] = ???
//  def apply(m: maybe.type) : Community_In_3_5[I1, I2, I3, A, B, C, D, E] {} = ???
//  def apply(c: count.type) : Community_In_3_5[I1, I2, I3, A, B, C, D, Int] {} = ???
//}
//
// trait Community_In_3_6[I1, I2, I3, A, B, C, D, E, F] extends Community with In_3_6[I1, I2, I3, A, B, C, D, E, F] {
//  lazy val name          : name        [Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]]      with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      = ???
//  lazy val url           : url         [Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]]      with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      = ???
//  lazy val category      : category    [Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, Set[String]]] with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]]      with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      = ???
//  lazy val `type`        : `type`      [Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]]      with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]]        with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]] with Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]              = ???
//  lazy val url_          :  url         [Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]] with Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]              = ???
//  lazy val category_     :  category    [Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]] with Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]] with Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]              = ???
//  lazy val type_         :  `type`      [Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]] with Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]] with Community_In_3_6[I1, I2, I3, A, B, C, D, E, F]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_3_6[I1, I2, I3, A, B, C, D, E, F] = ???
//  def apply(m: maybe.type) : Community_In_3_6[I1, I2, I3, A, B, C, D, E, F] {} = ???
//  def apply(c: count.type) : Community_In_3_6[I1, I2, I3, A, B, C, D, E, Int] {} = ???
//}
//
// trait Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G] extends Community with In_3_7[I1, I2, I3, A, B, C, D, E, F, G] {
//  lazy val name          : name        [Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]]      with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      = ???
//  lazy val url           : url         [Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]]      with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      = ???
//  lazy val category      : category    [Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Set[String]]] with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Set[String]] = ???
//  lazy val orgtype       : orgtype     [Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]]      with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      = ???
//  lazy val `type`        : `type`      [Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]]      with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]      = ???
//  lazy val neighborhood  : neighborhood[Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]]        with Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]        = ???
//
//  lazy val name_         :  name        [Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]] with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]              = ???
//  lazy val url_          :  url         [Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]] with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]              = ???
//  lazy val category_     :  category    [Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]] with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]              = ???
//  lazy val orgtype_      :  orgtype     [Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]] with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]              = ???
//  lazy val type_         :  `type`      [Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]] with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]              = ???
//  lazy val neighborhood_ :  neighborhood[Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]] with Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]              = ???
//  def Neighborhood : OneRef[Community, Neighborhood] with Neighborhood_In_3_7[I1, I2, I3, A, B, C, D, E, F, G] = ???
//  def apply(m: maybe.type) : Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, G] {} = ???
//  def apply(c: count.type) : Community_In_3_7[I1, I2, I3, A, B, C, D, E, F, Int] {} = ???
//}
//
// trait Community_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] extends Community with In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]
