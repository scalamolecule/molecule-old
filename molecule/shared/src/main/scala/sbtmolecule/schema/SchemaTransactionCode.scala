package sbtmolecule.schema

import sbtmolecule.ast.schemaModel.{BackRef, DataModelException, DefAttr, Enum, Model, Namespace, Optional, Ref, Val}

case class SchemaTransactionCode(d: Model) extends MetaSchemaData {

  // Prepare schema for edge interlink meta data if a property edge is defined
  val (partitions: Seq[String], nss: Seq[Namespace]) = {
    val parts = d.nss.map(_.part).filterNot(_ == "db.part/user").distinct
    d.nss.collectFirst {
      case ns if ns.attrs.collectFirst {
        case Ref(_, _, _, _, _, _, _, Some("BiTargetRef_"), _, _) => true
      }.getOrElse(false) => {
        val moleculeMetaNs = Namespace("molecule", None, "molecule_Meta", None, None, Seq(
          Ref("otherEdge", "OneRefAttr", "OneRef", "Long", "", "molecule_Meta", Seq(
            Optional(":db/index         true", "Indexed"),
            // Is component so that retracts automatically retracts the other edge
            Optional(":db/isComponent   true", "IsComponent")
          ))))
        (parts :+ "molecule", d.nss :+ moleculeMetaNs)
      }
    } getOrElse(parts, d.nss)
  }


  val partitionDefinitions0: Seq[String] = if (partitions.isEmpty) {
    Nil
  } else {
    Seq(
      partitions.map { p =>
        s"""|{:db/id      "$p"
            |        :db/ident   :$p}
            |       [:db/add :db.part/db :db.install/partition "$p"]""".stripMargin
      }.mkString("\n     [\n       ", "\n\n       ", "\n     ]\n    ")
    )
  }

  val partitionDefinitions: Seq[String] = {
    partitionDefinitions0.map(part => "// Partitions\n    \"\"\"" + part + "\"\"\"")
  }

  var aliasIdents = Seq.empty[String]


  lazy val aliases0: Seq[String] = if (aliasIdents.isEmpty) Nil else Seq(
    aliasIdents.mkString("\n     [\n       ", "\n\n       ", "\n     ]\n    ")
  )
  lazy val aliases: Seq[String] = if (aliasIdents.isEmpty) Nil else Seq(
    "// Aliases\n    \"\"\"" + aliases0 + "\"\"\""
  )

  def attrStmts(ns: String, a: DefAttr, isClient: Boolean): String = {
    val origAttr = a.options.collectFirst {
      case Optional("alias", alias) => alias
    }.getOrElse(a.attr)
    val ident    = s""":db/ident         :$ns/$origAttr"""
    def tpe(t: String) = s""":db/valueType     :db.type/$t"""
    def card(c: String) = s""":db/cardinality   :db.cardinality/$c"""
    def opts(os: Seq[Optional]): Seq[String] = os.flatMap {
      case Optional("alias", alias)                                                     =>
        aliasIdents = aliasIdents :+
          s"""{:db/id     :$ns/$alias
             |        :db/ident  :$ns/${a.attr}}""".stripMargin
        Nil
      case Optional(":db/index         true" | ":db/fulltext      true", _) if isClient => Nil
      case Optional(datomicKeyValue, _)                                                 => Seq(datomicKeyValue)
    }
    val stmts = a match {
      case Val(_, clazz, _, _, t, options, _, _, _, _) if clazz.take(3) == "One" => Seq(tpe(t), card("one")) ++ opts(options)
      case Val(_, _, _, _, t, options, _, _, _, _)                               => Seq(tpe(t), card("many")) ++ opts(options)
      case a: DefAttr if a.clazz.take(3) == "One"                                => Seq(tpe("ref"), card("one")) ++ opts(a.options)
      case a: DefAttr                                                            => Seq(tpe("ref"), card("many")) ++ opts(a.options)
      case unexpected                                                            =>
        throw new DataModelException(s"Unexpected attribute statement:\n" + unexpected)
    }
    s"{${(ident +: stmts).mkString("\n        ")}}"
  }

  def enums(part: String, ns: String, a: String, es: Seq[String], alias: String = ""): String = es.map { e =>
    val attr = if (alias.isEmpty) a else alias
    s"""[:db/add #db/id[:$part]  :db/ident :$ns.$attr/$e]"""
  }.mkString("\n       ")


  def attributeDefinition0(isClient: Boolean): String = nss
    .filterNot(ns => ns.attrs.isEmpty || ns.attrs.forall(_.attr.startsWith("_"))) // No namespaces without attributes or only back refs
    .map { ns =>
      val exts   = ns.opt.getOrElse("").toString
      val header = ";; " + ns.ns + exts + " " + ("-" * (50 - (ns.ns.length + exts.length)))
      val attrs  = ns.attrs.flatMap {
        case Val(_, _, _, _, "bytes", _, _, _, _, _) if isClient => Nil
        case _: BackRef                                          => Nil
        case e: Enum                                             =>
          Seq(attrStmts(ns.ns, e, isClient), enums(ns.part, ns.ns, e.attr, e.enums, e.alias))
        case a                                                   =>
          Seq(attrStmts(ns.ns, a, isClient))
      }
      header + "\n\n       " + attrs.mkString("\n\n       ")
    }.mkString("\n     [\n       ", "\n\n\n       ", "\n     ]\n    ")

  def attributeDefinitions(isClient: Boolean): String = {
    "// Attributes\n    \"\"\"" + attributeDefinition0(isClient) + "\"\"\""
  }

  val datomicPeerValues = (partitionDefinitions0 :+ attributeDefinition0(false)) ++ aliases0
  val datomicClientValues = (partitionDefinitions0 :+ attributeDefinition0(true)) ++ aliases0

  val datomicPeer = (
    (partitionDefinitions :+ attributeDefinitions(false)) ++ aliases
    ).mkString("Seq(\n    ", ",\n\n    ", "\n  )")

  val datomicClient = (
    (partitionDefinitions :+ attributeDefinitions(true)) ++ aliases
    ).mkString("Seq(\n    ", ",\n\n    ", "\n  )")

  val metaSchema = getMetaSchema(d)

  def getCode: String =
    s"""|/*
        |* AUTO-GENERATED Datomic Schema generation boilerplate code
        |*
        |* To change:
        |* 1. edit data model file in `${d.pkg}.dataModel/`
        |* 2. `sbt compile` in terminal
        |* 3. Refresh and re-compile project in IDE
        |*/
        |package ${d.pkg}.schema
        |import molecule.core.data.SchemaTransaction
        |import molecule.datomic.base.ast.metaSchema._
        |
        |object ${d.domain}Schema extends SchemaTransaction {
        |
        |  lazy val datomicPeer = $datomicPeer
        |
        |
        |  lazy val datomicClient = $datomicClient
        |
        |
        |  lazy val metaSchema = ${metaSchema.toString}
        |
        |
        |  lazy val nsMap = ${metaSchema.nsMap}
        |
        |
        |  // Map of Attribute -> (card, type)
        |  lazy val attrMap: Map[String, (Int, String)] = ${metaSchema.attrMap}
        |}""".stripMargin
}
