package sbtmolecule.dsl

object Template {

  def apply(
    ns: String,
    pkg: String,
    domain: String,
    body: String,
    extraImports: Seq[String],
  ): String = {
    val imports = (Seq(
      "java.util.Date",
      "scala.language.higherKinds",
    ) ++ extraImports).sorted.mkString("import ", "\nimport ", "")

    s"""/*
       |* AUTO-GENERATED Molecule DSL for namespace `$ns`
       |*
       |* To change:
       |* 1. Edit data model in $pkg.dataModel/${domain}DataModel
       |* 2. `sbt clean compile`
       |* 3. Re-compile project in IDE
       |*/
       |package $pkg.$domain
       |
       |$imports
       |
       |$body
       |""".stripMargin
  }
}
