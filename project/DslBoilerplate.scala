import sbt._

// Generate molecule dsl from definition files

object DslBoilerplate {

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def extractNsDefinitions(defFile: File) = {
    val raw = IO.readLines(defFile) filterNot (_.isEmpty) map (_.trim)
    val path: String = raw.collectFirst {
      case r"package (.*)$p\..*" => p
    }.getOrElse {
      sys.error("Found no package statement in definition file")
    }

    val nsDefs: Seq[(String, Seq[Seq[Any]])] = raw.foldLeft(Seq("ns" -> Seq(Seq("attr definitions...": Any)))) {
      case (acc, l) => l match {
        case r"package (.*)$path\.[\w]*"                    => acc
        case "import molecule.ast.definition._"             => acc
        case r"trait (\w*)$ns \{"                           => acc :+ (ns -> Seq())
        case r"val\s*(\`?)$q1(\w*)$a(\`?)$q2\s*\=\s*(.*)$s" => {
          val attr = q1 + a + q2
          def extract(str: String, elements: Seq[Any] = Seq()): Seq[Any] = str match {
            case r"oneString(.*)$rest"        => extract(rest, Seq("attr", attr, "String", 1, "OneString(ns, ns2)"))
            case r"manyStrings(.*)$rest"      => extract(rest, Seq("attr", attr, "Set[String]", 2, "ManyString(ns, ns2)"))
            case r"oneInt(.*)$rest"           => extract(rest, Seq("attr", attr, "Int", 1, "OneInt(ns, ns2)"))
            case r"oneEnum\((.*)$enums\)"     => Seq("enum", attr, "String", 1, "OneEnum(ns, ns2)") ++ enums.replaceAll("'", "").split(",").toList.map(_.trim)
            case r"manyEnums\((.*)$enums\)"   => Seq("enum", attr, "String", 2, "ManyEnums(ns, ns2)") ++ enums.replaceAll("'", "").split(",").toList.map(_.trim)
            case r"\.fullTextSearch(.*)$rest" => extract(rest, elements :+ "FulltextSearch")
            case r"\.uniqueIdentity(.*)$rest" => extract(rest, elements :+ "UniqueIdentity")
            case r"oneRef\[(\w*)$ref\]"       => Seq("ref", attr, "OneRef", ref)
            case ""                           => elements
            case unexpected                   => sys.error("Unexpected attribute code in definition file:\n" + unexpected)
          }
          val ns = acc.last._1
          val prevAttrs = acc.last._2
          val attrDef = extract(s)
          val updatedAttrs = prevAttrs :+ attrDef
          acc.init :+ (ns -> updatedAttrs)
        }
        case "}"                                            => acc
        case unexpected                                     => sys.error("Unexpected code in definition file:\n" + unexpected)
      }
    }.tail
    (path, nsDefs)
  }

  def handleNamespace(srcManaged: File, path: String, nsDef: (String, Seq[Seq[Any]])) = {

    // NS meta data.........................................
    val Ns = nsDef._1
    val ns = Ns.toLowerCase
    val attrDefs = nsDef._2
    val attrs0 = attrDefs.filter(a => a.head == "attr" || a.head == "enum").map { d =>
      val (cat, attr, tpe, card, defs) = (d.head, d.tail.head, d.tail.tail.head, d.tail.tail.tail.head, d.tail.tail.tail.tail)
      val baseType = tpe match {
        case r"Set\[(\w*)$t\]" => "" + t
        case t                 => "" + t
      }
      (cat, attr, tpe, baseType, card, defs)
    }
    val rawBaseTypes = attrs0.map(a => a._3 match {
      case r"Set\[(\w*)$t\]" => t
      case t: String         => t
    })
    val longestAttr = attrs0.map(_._2.toString.length).max
    val longestAttrClean = attrs0.map(_._2.toString.replace("`", "").length).max
    val longestType = attrs0.map(_._3.toString.length).max
    val longestBaseType = rawBaseTypes.map(_.toString.length).max
    val enumAttrCount = attrs0.count(_._1 == "enum")
    val entityIdDef = ("attr", "eid", "Long", "Long", 1, List("OneLong(ns, ns2)"))
    val attrs = (entityIdDef +: attrs0).map {
      case (cat: String, attr: String, tpe: String, baseType: String, card: Int, defs: List[_]) => {
        val attrClean = attr.replace("`", "")
        val padAttr = " " * (longestAttr - attr.length)
        val padAttrClean = " " * (longestAttrClean - attrClean.length)
        val padType = " " * (longestType - tpe.length)
        val padBaseType = " " * (longestBaseType - baseType.length)
        (cat, attr, attrClean, tpe, baseType, card, defs.map(_.toString), padAttr, padAttrClean, padType, padBaseType)
      }
    }
    val refs0 = attrDefs.filter(_.head == "ref").map(d => (d.tail.head, d.tail.tail.head, d.tail.tail.tail.head))
    val refs = refs0.map { case (attr: String, refType: String, ref: String) =>
      val padAttr = " " * (longestAttr - attr.length)
      val padType = " " * (longestType - refType.length)
      (attr, refType, ref, padAttr, padType)
    }


    // Schema stmts ######################################################################################

    val schemaStmts: (String, Seq[Seq[(String, String)]]) = (Ns, attrs.tail.map { case (cat, attr0, attrClean, tpe, baseType, card, defs, _, _, _, _) =>
      val attr = attr0.replace("`", "")
      val valueType = if (cat == "enum") "ref"
      else baseType match {
        case "Int" => "bigint"
        case other => other.toLowerCase
      }
      val attr1 = Seq(
        "<id>" -> ":db.part/db",
        ":db/ident" -> s":$ns/$attr",
        ":db/valueType" -> s":db.type/$valueType",
        ":db/cardinality" -> (if (card == 1) ":db.cardinality/one" else ":db.cardinality/many"))


      val attr2 = defs.foldLeft(attr1) {
        case (acc, "FulltextSearch") => acc :+ (":db/fulltext" -> "true")
        case (acc, "UniqueIdentity") => acc :+ (":db/unique" -> ":db.unique/identity")
        case (acc, _)                => acc
      }

      val installStmt = ":db.install/_attribute" -> ":db.part/db"

      if (cat == "enum")
        (attr2 :+ installStmt) ++ defs.tail.flatMap(enum => Seq("<enum>" -> s":$ns.$attr/$enum"))
      else
        attr2 :+ installStmt
    } ++ {
      refs.map { case ((attr0, refType, ref, _, _)) =>
        val attr = attr0.replace("`", "")
        Seq(
          "<id>" -> ":db.part/db",
          ":db/ident" -> s":$ns/$attr",
          ":db/valueType" -> ":db.type/ref",
          ":db/cardinality" -> (if (refType == "OneRef") ":db.cardinality/one" else ":db.cardinality/many"),
          ":db.install/_attribute" -> ":db.part/db"
        )
      }
    }
      )
    //    println("\n======= " + Ns + " =======\n")
    //    println(schemaStmts._2.map(_.mkString("\n")).mkString("\n\n"))


    // Base classes ######################################################################################

    // NS ------------------------------------------------------------------

    val attrClasses = attrs.tail.map { case (cat, attr, _, _, baseType, _, defs, padAttr, padAttrClean, _, _) =>
      val (extensions, enumValues) = if (cat == "enum")
        (defs.head, s"private lazy val ${defs.tail.mkString(", ")} = EnumValue")
      else
        (defs.mkString(" with "), "")
      val oldNew = s"def apply(data: oldNew[$baseType]) = ${Ns}_Update()"
      val baseElements = Seq(enumValues, oldNew) map (_.trim) filter (_.nonEmpty) mkString "\n    "
      s"""class $attr[NS, NS2](ns: NS, ns2: NS2) extends $extensions {
             |    $baseElements
             |  }
             |""".stripMargin
    }.mkString("\n  ").trim


    // NS_Insert ------------------------------------------------------------------

    val insertAttrs1 = attrs.map { case (cat, attr, attrClean, tpe, baseType, card, _, padAttr, padAttrClean, _, padBaseType) =>
      val enumPrefix = if (cat == "enum") s""", Some(":$ns.$attrClean/")""" else ""
      if (card == 1)
        s"""lazy val $attr $padAttr= (data: $baseType$padBaseType) => _insert(Seq(data), $card, "$attrClean"$padAttrClean, "$tpe"$padBaseType$enumPrefix)"""
      else
        s"lazy val $attr $padAttr= ${attr.replace("`", "")}_"
    } mkString "\n  "

    val insertAttrs2 = attrs.filter(_._6 == 2).map { case (cat, attr, attrClean, tpe, baseType, card, _, padAttr, padAttrClean, padType, _) =>
      val pad = " " * (baseType.length - 2)
      val enumPrefix = if (cat == "enum") s""", Some(":$ns.$attrClean/")""" else ""
      s"""private[molecule] object ${attrClean}_ {
               |    def apply(h: $baseType, t: $baseType*) = _insert(h +: t.toList, $card, "$attrClean"$padAttrClean, "$tpe"$enumPrefix)
               |    def apply(data: Seq[$baseType])$pad = _insert(data,          $card, "$attrClean"$padAttrClean, "$tpe"$enumPrefix)
               |  }""".stripMargin
    } mkString "\n  "

    val insertRefs = refs.map { case (_, _, ref, padAttr, _) =>
      s"def $ref$padAttr = ${ref}_Insert(elements)"
    } mkString "\n  "

    val inserts = Seq(insertAttrs1, insertAttrs2, insertRefs) map (_.trim) filter (_.nonEmpty) mkString "\n\n  "


    // NS_Update ------------------------------------------------------------------

    val updateAttrs1 = attrs.map { case (_, attr, attrClean, _, _, _, _, padAttr, padAttrClean, _, _) =>
      s"lazy val $attr $padAttr= ${attrClean}_"
    } mkString "\n  "

    val updateAttrs2 = attrs.map { case (cat, attr, attrClean, tpe, baseType, card, _, padAttr, padAttrClean, padType, padBaseType) =>
      val enumPrefix = if (cat == "enum") s""", Some(":$ns.$attrClean/")""" else ""
      if (card == 1) {
        val pad = " " * longestBaseType
        s"""private[molecule] object ${attrClean}_ {
                 |    def apply(data: $baseType) $padBaseType= _assertNewFact(Seq(data), $card, "$attrClean", "$tpe"$enumPrefix)
                 |    def apply()$pad       = _retract(                 $card, "$attrClean")
                 |  }""".stripMargin
      } else {
        val pad = " " * (baseType.length * 4)
        s"""private[molecule] object ${attrClean}_ {
                 |    def add(data: $baseType) $pad       = _assertNewFact(Seq(data),     2, "category", "Set[String]")
                 |    def apply(h: ($baseType, $baseType), t: ($baseType, $baseType)*) = _swap(h +: t.toList            , "$attrClean", "$tpe"$enumPrefix)
                 |    def remove(values: $baseType*) $pad = _removeElements(Seq(values: _*), "category", "Set[String]")
                 |    def apply() $padBaseType $pad                = _retract(                     $card, "$attrClean")
                 |  }""".stripMargin
      }
    } mkString "\n  "

    val updateRefs = refs.map { case (_, _, ref, padAttr, _) =>
      s"def $ref$padAttr = ${ref}_Update(elements)"
    } mkString "\n  "

    val updates = Seq(updateAttrs1, updateAttrs2, updateRefs) map (_.trim) filter (_.nonEmpty) mkString "\n\n  "


    // NS_Retract ------------------------------------------------------------------

    val retractAttrs1 = attrs.map { case (_, attr, attrClean, tpe, baseType, card, _, padAttr, padAttrClean, _, padBaseType) =>
      if (card == 1)
        s"""lazy val $attr $padAttr= _retract($card, "$attrClean"$padAttrClean, "$tpe"$padBaseType)"""
      else
        s"lazy val $attr $padAttr= ${attrClean}_"
    } mkString "\n  "

    val retractAttrs2 = attrs.filter(_._6 == 2).map { case (_, attr, attrClean, tpe, baseType, card, _, padAttr, padAttrClean, padType, padBaseType) =>
      val pad = " " * (baseType.length - 2)
      s"""private[molecule] object ${attrClean}_ {
               |    def apply() $padBaseType                     = _retract($card, "$attrClean", "$tpe")
               |    def apply(h: $baseType, t: $baseType*) = _retract($card, "$attrClean", "$tpe")
               |    def apply(data: Seq[$baseType]) $pad= _retract($card, "$attrClean", "$tpe")
               |  }""".stripMargin
    } mkString "\n  "

    val retractRefs = refs.map { case (_, _, ref, padAttr, _) =>
      s"def $ref$padAttr = ${ref}_Retract(elements)"
    } mkString "\n  "

    val retracts = Seq(retractAttrs1, retractAttrs2, retractRefs) map (_.trim) filter (_.nonEmpty) mkString "\n\n  "


    // NS_Entity ------------------------------------------------------------------

    val entities1 = attrs.map { case (_, attr, attrClean, tpe, baseType, card, _, padAttr, padAttrClean, _, padBaseType) =>
      s"""lazy val $attr $padAttr= _get($card, "$attrClean"$padAttrClean, "$tpe"$padBaseType)"""
    } mkString "\n  "

    val entityRefs = refs.map { case (_, _, ref, padAttr, _) =>
      s"def $ref$padAttr = ${ref}_Entity(elements)"
    } mkString "\n  "

    val entities = Seq(entities1, entityRefs) map (_.trim) filter (_.nonEmpty) mkString "\n\n  "



    // Arity classes ######################################################################################

    val inArity = 3
    val outArity = 8
    val inRange = 0 to inArity
    val outRange = 0 to outArity

    val boilerplateClasses = inRange.flatMap { in =>
      outRange.flatMap { out =>

        val InTypes = (0 until in) map (n => "I" + (n + 1))
        val OutTypes = (0 until out) map (n => (n + 'A').toChar)

        if (in == 0) {

          // Out_o ================================================================================================

          val TraitTypes = if (out == 0) "" else s"[${OutTypes mkString ", "}]"


          val inputMethods = if (out > 0 && in < inArity) {
            val newInTypes = if (OutTypes.isEmpty) InTypes else InTypes :+ OutTypes.last

            val curTypes = (if (OutTypes.isEmpty) newInTypes else newInTypes ++ OutTypes.init).mkString(", ")
            val curIn = s"${Ns}_In_${in + 1}_${out - 1}[$curTypes] {}"

            val nextInTypes = (newInTypes ++ OutTypes).mkString(", ")
            val nextIn = s"${Ns}_In_${in + 1}_$out[$nextInTypes] {}"

            val nextOutTypes = OutTypes.mkString(", ")
            val nextOut = s"${Ns}_Out_$out[$nextOutTypes] {}"

            Seq( s"""
                        |  def apply(in: ?.type)    = new $curIn
                        |  def apply(in: ?!.type)   = new $nextIn
                        |  def <(in: ?.type)        = new $nextIn
                        |  def contains(in: ?.type) = new $nextIn
                        |  def apply(m: maybe.type) = new $nextOut
                        |  """.stripMargin.trim)
          } else Seq()


          val outBody = {

            val (_, nameFirst, tpeFirst, _, _, _) = attrs0.head
            val defaults = if (in + out == 0)
              Seq( s"""|
                        |  @default
                        |  def apply($nameFirst: $tpeFirst) = new $nameFirst(this, new ${Ns}_Out_1[$tpeFirst] {}) with ${Ns}_Out_1[$tpeFirst]
                        |  def apply(id: Long) = ${Ns}_Entity()
                        |
                        |  def update(id: Long) = ${Ns}_Update(Seq(), Seq(id))
                        |  def update(ids: Seq[Long]) = ${Ns}_Update(Seq(), ids)
                        |""".stripMargin)
            else Seq()

            val attrCode = attrs.map { case (_, attr, attrClean, tpe, baseType, card, _, padAttr, padAttrClean, padType, padBaseType) =>
              val nextTypes = (OutTypes :+ tpe) mkString ", "
              val nextNS = s"${Ns}_Out_${out + 1}[$nextTypes]$padType"
              s"lazy val $attr $padAttr= new $attr$padAttr (this, new $nextNS {}) with $nextNS {}"
            }

            val refCode = refs.foldLeft(Seq("")) { case (acc, (attr, refType, ref, padAttr, padType)) =>
              acc :+ s"def $ref $padAttr= new $refType with ${ref}_Out_$out$TraitTypes"
            }.tail

            if (out == outArity)
              "\n\n"
            else
              (Seq(s"import $Ns._") ++ defaults ++ attrCode ++ refCode ++ inputMethods).mkString("{\n  ", "\n  ", "\n}\n")
          }

          Some(s"trait ${Ns}_Out_$out$TraitTypes extends Out_$out$TraitTypes $outBody")

        } else {

          // In_i_o ================================================================================================

          val InOutTypes = (InTypes ++ OutTypes) mkString ", "

          val s = if (in > 1) "s" else ""
          val header = if (in > 0 && out == 0) s"/********* Input molecules awaiting $in input$s *******************************/\n\n" else ""

          val InTraits = s"trait ${Ns}_In_${in}_$out[$InOutTypes] extends In_${in}_$out[$InOutTypes]"

          val attrCode = attrs.map { case (_, attr, attrClean, tpe, baseType, card, _, padAttr, padAttrClean, padType, padBaseType) =>
            val nextTypes = Seq(InOutTypes, tpe) mkString ", "
            val nextNS = s"${Ns}_In_${in}_${out + 1}[$nextTypes]$padType"
            s"lazy val $attr $padAttr= new $attr$padAttr (this, new $nextNS {}) with $nextNS {}"
          }

          val refCode = refs.foldLeft(Seq("")) { case (acc, (attr, refType, ref, padAttr, padType)) =>
            acc :+ s"def $ref $padAttr= new $refType with ${ref}_In_${in}_$out[$InOutTypes]"
          }.tail

          val inputMethods = if (out > 0 && in < inArity) {
            val newInTypes = if (OutTypes.isEmpty) InTypes else InTypes :+ OutTypes.last

            val curTypes = (if (OutTypes.isEmpty) newInTypes else newInTypes ++ OutTypes.init).mkString(", ")
            val curNS = s"${Ns}_In_${in + 1}_${out - 1}[$curTypes] {}"

            val nextTypes = (newInTypes ++ OutTypes).mkString(", ")
            val nextNS = s"${Ns}_In_${in + 1}_$out[$nextTypes] {}"

            Seq( s"""def apply(in: ?.type)    = new $curNS
                        |  def apply(in: ?!.type)   = new $nextNS
                        |  def <(in: ?.type)        = new $nextNS
                        |  def contains(in: ?.type) = new $nextNS""".stripMargin)
          } else Seq()

          val traitBody = if (out == outArity)
            "\n\n"
          else
            (Seq(s"import $Ns._") ++ attrCode ++ refCode ++ inputMethods).mkString("{\n  ", "\n  ", "\n}\n")

          Some(s"$header$InTraits $traitBody")
        }
      }
    }.mkString("\n").trim

    val body = s"""|/*
                   | * AUTO-GENERATED CODE - DO NOT CHANGE!
                   | *
                   | * Manual changes to this file will likely break molecules!
                   | * Instead, change the molecule definition files and recompile your project with `sbt compile`.
                   | */
                   |package $path.dsl
                   |import molecule._
                   |import ast.schemaDSL._
                   |import ast.model._
                   |
                   |object $Ns extends ${Ns}_Out_0 {
                   |  $attrClasses
                   |
                   |  def insert = ${Ns}_Insert()
                   |}
                   |
                   |case class ${Ns}_Entity(elements: Seq[Element] = Seq()) extends Entity(elements) {
                   |  $entities
                   |
                   |  private def _get(card: Int, attr: String, tpe: String) =
                   |    ${Ns}_Entity(elements :+ Atom("$ns", attr, tpe, card, NoValue))
                   |}
                   |
                   |case class ${Ns}_Insert(override val elements: Seq[Element] = Seq()) extends Insert(elements) {
                   |  $inserts
                   |
                   |  private def _insert(data: Seq[Any], card: Int, attr: String, tpe: String, enumPrefix: Option[String] = None) =
                   |    ${Ns}_Insert(elements :+ Atom("$ns", attr, tpe, card, Eq(data.map(_.toString)), enumPrefix))
                   |}
                   |
                   |case class ${Ns}_Update(override val elements: Seq[Element] = Seq(), override val ids: Seq[Long] = Seq()) extends Update(elements, ids) {
                   |  $updates
                   |
                   |  private def _assertNewFact(data: Seq[Any], card: Int, attr: String, tpe: String, enumPrefix: Option[String] = None) =
                   |    ${Ns}_Update(elements :+ Atom("$ns", attr, tpe, card, Eq(data.map(_.toString)), enumPrefix), ids)
                   |
                   |  private def _swap(oldNew: Seq[(String, String)], attr: String, tpe: String, enumPrefix: Option[String] = None) =
                   |    ${Ns}_Update(elements :+ Atom("$ns", attr, tpe, 2, Replace(oldNew.toMap), enumPrefix), ids)
                   |
                   |  private def _removeElements(values: Seq[String], attr: String, tpe: String, enumPrefix: Option[String] = None) =
                   |    ${Ns}_Update(elements :+ Atom("$ns", attr, tpe, 2, Remove(values), enumPrefix), ids)
                   |
                   |  private def _retract(card: Int, attr: String) =
                   |    ${Ns}_Update(elements :+ Atom("$ns", attr, "", card, Remove(Seq())), ids)
                   |}
                   |
                   |case class ${Ns}_Retract(elements: Seq[Element] = Seq()) extends Retract(elements) {
                   |  $retracts
                   |
                   |  private def _retract(card: Int, attr: String, tpe: String, data: Seq[Any] = Seq()) =
                   |    ${Ns}_Retract(elements :+ Atom("$ns", attr, tpe, card, Eq(data.map(_.toString))))
                   |}
                   |
                   |//********* Output molecules *******************************/
                   |
                   | $boilerplateClasses
                  """.stripMargin

    val dslFile: File = path.split('.').toList.foldLeft(srcManaged)((file, pkg) => file / pkg) / "dsl" / s"$Ns.scala"
    IO.write(dslFile, body)
    (schemaStmts, dslFile)
  }

  def mkSchema(srcManaged: File, path: String, domain: String, schemaStmts: Seq[(String, Seq[Seq[(String, String)]])]) = {

    val keyValues = for {
      (ns, nsStmts) <- schemaStmts
      attrStmts <- nsStmts
      longest = 34
      (key, value) <- attrStmts
    } yield (ns, key, value)

    val (_, _, stmtString) = keyValues.foldLeft(("", "", "")) { case ((ns1, key1, acc), (ns, key, value)) =>
      key match {
        case "<id>" if ns1 == ""      => (ns, key, acc + s"""|// $ns ----------------------------------------------------------
                                                     |
                                                     |    Util.map(":db/id"                , Peer.tempid(":db.part/db")""".stripMargin)
        case "<id>" if ns1 != ns      => (ns, key, acc + s"""|),
                                                     |
                                                     |
                                                     |    // $ns ----------------------------------------------------------
                                                     |
                                                     |    Util.map(":db/id"                , Peer.tempid(":db.part/db")""".stripMargin)
        case "<id>"                   => (ns1, key, acc + s"""),\n\n    Util.map(":db/id"                , Peer.tempid(":db.part/db")""".stripMargin)
        case ":db/ident"              => (ns1, key, acc + s""",\n             ":db/ident"             , "$value"""")
        case ":db/valueType"          => (ns1, key, acc + s""",\n             ":db/valueType"         , "$value"""")
        case ":db/cardinality"        => (ns1, key, acc + s""",\n             ":db/cardinality"       , "$value"""")
        case ":db/fulltext"           => (ns1, key, acc + s""",\n             ":db/fulltext"          , true.asInstanceOf[Object]""")
        case ":db/unique"             => (ns1, key, acc + s""",\n             ":db/unique"            , "$value"""")
        case ":db.install/_attribute" => (ns1, key, acc + s""",\n             ":db.install/_attribute", "$value"""")

        case "<enum>" if key1 != key => (ns1, key, acc +
          s"""),\n\n    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", "$value"""".stripMargin)
        case "<enum>"                => (ns1, key, acc +
          s"""),\n    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", "$value"""".stripMargin)
      }
    }

    val body = s"""|/*
                   | * AUTO-GENERATED CODE - DO NOT CHANGE!
                   | *
                   | * Manual changes to this file will likely break schema creations!
                   | * Instead, change the molecule definition files and recompile your project with `sbt compile`
                   | */
                   |package $path.schema
                   |import molecule.db.Schema
                   |import datomic.{Util, Peer}
                   |
                   |object ${domain}Schema extends Schema {
                   |
                   |  lazy val tx = Util.list(
                   |
                   |    ${stmtString.trim})
                   |  )
                   |}""".stripMargin

    val schemaFile: File = path.split('.').toList.foldLeft(srcManaged)((file, pkg) => file / pkg) / "schema" / s"${domain}Schema.scala"
    IO.write(schemaFile, body)
    schemaFile
  }

  def generate(srcManaged: File, domainDirs: Seq[String]): Seq[File] = {

    // Loop domain directories
    domainDirs flatMap { domainDir =>
      val definitionFiles = IO.listFiles(new File(domainDir) / "definition").filter(f => f.isFile && f.name.endsWith("Definition.scala"))
      assert(definitionFiles.size > 0, "Found no definition files in path: " + domainDir)

      // Loop definition files in each domain directory
      definitionFiles.flatMap { definitionFile =>
        val (path, nsDefs) = extractNsDefinitions(definitionFile)

        // Loop namespaces in each definition file
        val (schemaStmts, dslFiles) = nsDefs.map(handleNamespace(srcManaged, path, _)).unzip

        // Create schema file
        val domain = definitionFile.name.replace("Definition.scala", "")
        val schemaFile = mkSchema(srcManaged, path, domain, schemaStmts)

        dslFiles :+ schemaFile
      }
    }
  }
}