package sbtmolecule.schema

import sbtmolecule.ast.schemaModel._

object SchemaTransactionLowerToUpper {

  // Generate ..........................................

  def apply(d: Model): String = {

    val attributeDefinitions: String = d.nss.filterNot(ns => ns.attrs.isEmpty || ns.attrs.forall(_.attr.startsWith("_"))).map {
      case Namespace(_, _, ns, _, opt, attrs) =>
        val exts                      = opt.getOrElse("").toString
        val header                    = ";; " + ns + exts + " " + ("-" * (50 - (ns.length + exts.length)))

        val (attrsBefore, attrsAfter) = attrs.flatMap { a =>
          val origAttr = a.options.collectFirst {
            case Optional("alias", alias) => alias
          }.getOrElse(a.attr)
          val attrStmt = (firstLow(ns) + "/" + origAttr, ns + "/" + a.attr)
          a match {
            case e: Enum    =>
              attrStmt +: e.enums.map(enum =>
                (firstLow(ns) + "." + origAttr + "/" + enum, ns + "." + a.attr + "/" + enum)
              )
            case _: BackRef => Nil
            case _          => Seq(attrStmt)
          }
        }.unzip

        val maxBefore = attrsBefore.map(_.length).max
        val maxAfter  = attrsAfter.map(_.length).max
        val stmts     = attrsBefore.zip(attrsAfter).map {
          case (attrBefore, attrAfter) =>
            val indent1 = " " * (maxBefore - attrBefore.length)
            val indent2 = " " * (maxAfter - attrAfter.length)
            s"""{ :db/id :$attrBefore$indent1   :db/ident :$attrAfter$indent2 }"""
        }
        header + "\n\n       " + stmts.mkString("\n       ")
    }.mkString("\n\n\n       ")


    s"""|/*
        |* AUTO-GENERATED Datomic Schema exchange boilerplate code
        |*
        |* To change:
        |* 1. edit schema definition file in `${d.pkg}.schema/`
        |* 2. `sbt compile` in terminal
        |* 3. Refresh and re-compile project in IDE
        |*/
        |package ${d.pkg}.schema
        |
        |object ${d.domain}SchemaLowerToUpper {
        |
        |  lazy val edn =
        |    \"\"\"
        |     [
        |       $attributeDefinitions
        |     ]
        |    \"\"\"
        |}""".stripMargin
  }
}
