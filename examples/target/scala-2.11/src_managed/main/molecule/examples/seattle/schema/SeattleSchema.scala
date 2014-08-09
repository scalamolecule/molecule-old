/*
 * AUTO-GENERATED CODE - DO NOT CHANGE!
 *
 * Manual changes to this file will likely break schema creations!
 * Instead, change the molecule definition files and recompile your project with `sbt compile`
 */
package molecule.examples.seattle.schema
import molecule.dsl.Transaction
import datomic.{Util, Peer}

object SeattleSchema extends Transaction {

  lazy val tx = Util.list(

    // Community --------------------------------------------------------

    Util.map(":db/id"                , Peer.tempid(":db.part/db"),
             ":db/ident"             , ":community/name",
             ":db/valueType"         , ":db.type/string",
             ":db/cardinality"       , ":db.cardinality/one",
             ":db/fulltext"          , true.asInstanceOf[Object],
             ":db.install/_attribute", ":db.part/db"),

    Util.map(":db/id"                , Peer.tempid(":db.part/db"),
             ":db/ident"             , ":community/url",
             ":db/valueType"         , ":db.type/string",
             ":db/cardinality"       , ":db.cardinality/one",
             ":db.install/_attribute", ":db.part/db"),

    Util.map(":db/id"                , Peer.tempid(":db.part/db"),
             ":db/ident"             , ":community/category",
             ":db/valueType"         , ":db.type/string",
             ":db/cardinality"       , ":db.cardinality/many",
             ":db/fulltext"          , true.asInstanceOf[Object],
             ":db.install/_attribute", ":db.part/db"),

    Util.map(":db/id"                , Peer.tempid(":db.part/db"),
             ":db/ident"             , ":community/orgtype",
             ":db/valueType"         , ":db.type/ref",
             ":db/cardinality"       , ":db.cardinality/one",
             ":db.install/_attribute", ":db.part/db"),

    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.orgtype/community"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.orgtype/commercial"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.orgtype/nonprofit"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.orgtype/personal"),

    Util.map(":db/id"                , Peer.tempid(":db.part/db"),
             ":db/ident"             , ":community/type",
             ":db/valueType"         , ":db.type/ref",
             ":db/cardinality"       , ":db.cardinality/one",
             ":db.install/_attribute", ":db.part/db"),

    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.type/email_list"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.type/twitter"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.type/facebook_page"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.type/blog"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.type/website"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.type/wiki"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.type/myspace"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":community.type/ning"),

    Util.map(":db/id"                , Peer.tempid(":db.part/db"),
             ":db/ident"             , ":community/neighborhood",
             ":db/valueType"         , ":db.type/ref",
             ":db/cardinality"       , ":db.cardinality/many",
             ":db.install/_attribute", ":db.part/db"),


    // Neighborhood -----------------------------------------------------

    Util.map(":db/id"                , Peer.tempid(":db.part/db"),
             ":db/ident"             , ":neighborhood/name",
             ":db/valueType"         , ":db.type/string",
             ":db/cardinality"       , ":db.cardinality/one",
             ":db/fulltext"          , true.asInstanceOf[Object],
             ":db/unique"            , ":db.unique/identity",
             ":db.install/_attribute", ":db.part/db"),

    Util.map(":db/id"                , Peer.tempid(":db.part/db"),
             ":db/ident"             , ":neighborhood/district",
             ":db/valueType"         , ":db.type/ref",
             ":db/cardinality"       , ":db.cardinality/many",
             ":db.install/_attribute", ":db.part/db"),


    // District ---------------------------------------------------------

    Util.map(":db/id"                , Peer.tempid(":db.part/db"),
             ":db/ident"             , ":district/name",
             ":db/valueType"         , ":db.type/string",
             ":db/cardinality"       , ":db.cardinality/one",
             ":db/fulltext"          , true.asInstanceOf[Object],
             ":db/unique"            , ":db.unique/identity",
             ":db.install/_attribute", ":db.part/db"),

    Util.map(":db/id"                , Peer.tempid(":db.part/db"),
             ":db/ident"             , ":district/region",
             ":db/valueType"         , ":db.type/ref",
             ":db/cardinality"       , ":db.cardinality/one",
             ":db.install/_attribute", ":db.part/db"),

    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":district.region/n"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":district.region/ne"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":district.region/e"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":district.region/se"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":district.region/s"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":district.region/sw"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":district.region/w"),
    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":district.region/nw")
  )
}