//* AUTO-GENERATED CODE - DO NOT CHANGE!
//*
//* Manual changes to this file will likely break molecules!
//* Instead, change the molecule definition files and recompile your project with `sbt compile`.
//*/
//package molecule.examples.seattle.dsl.seattle
//import molecule._
//import molecule.dsl.schemaDSL._
//import molecule.examples.seattle.dsl.seattle.{Neighborhood_0, Neighborhood}
//import molecule.in._
//import molecule.out._
//
//
//trait Community {
//
// class name[Ns]         extends OneString[Ns]  with FulltextSearch[Ns] {self: Ns =>}
////  trait name[Ns <: NS]         extends OneString[Community_1[String]]  with FulltextSearch[Ns]
////  trait name         extends One[Community_1[String], String]  with FulltextSearch[Community_1[String]]
////  trait name         extends OneString[Ns]  with FulltextSearch
//}
//
//object Community extends Community with Community_0 {
////  trait name[Ns]         extends OneString[Ns]  with FulltextSearch
// class url          extends OneString
// class category     extends ManyString with FulltextSearch
// class orgtype      extends OneEnum    { private lazy val community, commercial, nonprofit, personal = EnumValue }
// class `type`       extends OneEnum    { private lazy val email_list, twitter, facebook_page, blog, website, wiki, myspace, ning = EnumValue }
// class neighborhood extends OneRefAttr
//}
//
//trait Community_0 extends Community with Molecule_0 {
////  import Community._
// lazy val eid           = new eid          with Community_1[Long]
////  lazy val name          = new  Community_1[String] with name[Community_1[String]]
////  lazy val name          = new  Community_1[String] with One[Community_1[String], String] with FulltextSearch[Community_1[String]]
////  lazy val name          = new  Community_1[String] with OneString[Community_1[String]] with FulltextSearch[Community_1[String]]
// lazy val name          = new name[Community_1[String]]         with Community_1[String]
// lazy val url           = new url          with Community_1[String]
// lazy val category      = new category     with Community_1[Set[String]]
// lazy val orgtype       = new orgtype      with Community_1[String]
// lazy val `type`        = new `type`       with Community_1[String]
// lazy val neighborhood  = new neighborhood with Community_1[Long]
// lazy val eid_          = new eid          with Community_0
// lazy val name_         = new name         with Community_0
// lazy val url_          = new url          with Community_0
// lazy val category_     = new category     with Community_0
// lazy val orgtype_      = new orgtype      with Community_0
// lazy val type_       = new `type`       with Community_0
// lazy val neighborhood_ = new neighborhood with Community_0
// def Neighborhood = new OneRef[Community, Neighborhood] with Neighborhood_0
//}
//
//trait Community_1[A] extends Community with Molecule_1[A] {