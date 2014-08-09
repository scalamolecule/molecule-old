import sbt._

// Generate molecule dsl from definition files

object DslBoilerplate {

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }
  def firstLow(str: Any) = str.toString.head.toLower + str.toString.tail

  def extractNsDefinitions(defFile: File) = {
    val raw = IO.readLines(defFile) filterNot (_.isEmpty) map (_.trim)
    val path: String = raw.collectFirst {
      case r"package (.*)$p\..*" => p
    }.getOrElse {
      sys.error("Found no package statement in definition file")
    }

    // Input/output arities
    val (inArity, outArity) = raw collect {
      case r"@InOut\((\d+)$in, (\d+)$out\)" => (in.toString.toInt, out.toString.toInt) match {
        case (inN: Int, _) if inN < 0 || inN > 3     => sys.error(s"Input arity in '${defFile.getName}' was $in. It should be between 0-3")
        case (_, outN: Int) if outN < 1 || outN > 22 => sys.error(s"Output arity of '${defFile.getName}' was $out. It should be between 1-22")
        case (inN: Int, outN: Int)                   => (inN, outN)
      }
    } match {
      case Nil           => sys.error(
        """Please annotate the first namespace definition with '@InOut(inArity, outArity)' where:
          |inArity is a number between 1-3 for how many inputs molecules of this schema can await
          |outArity is a number between 1-22 for how many output attributes molecules of this schema can have""".stripMargin)
      case h :: t :: Nil => sys.error(
        """
          |Only the first namespace should be annotated with @InOut since all namespaces in a schema will need
          |to share the same arities to be able to carry over type information uniformly across namespaces.""".stripMargin)
      case annotations   => annotations.head
    }

    // Domain
    val domain = raw collect {
      case r"trait (.*)${name}Definition"      => name
      case r"trait (.*)${name}Definition \{"   => name
      case r"trait (.*)${name}Definition \{\}" => name
    } match {
      case Nil                      => sys.error("Couldn't find definition trait <domain>Definition in " + defFile.getName)
      case l: List[_] if l.size > 1 => sys.error(s"Only one definition trait per definition file allowed. Found ${l.size}:" + l.mkString("\n - ", "Definition\n - ", "Definition"))
      case domainNameList           => firstLow(domainNameList.head)
    }

    // Collect namespace definitions
    val nsDefs: Seq[(String, Seq[Seq[Any]])] = raw.foldLeft(Seq[(String, Seq[Seq[Any]])]()) {
      case (acc, l) => l match {
        case r"\/\/.*" /* comments allowed */               => acc
        case r"package (.*)$path\.[\w]*"                    => acc
        case "import molecule.dsl.schemaDefinition._"       => acc
        case r"@InOut\(\d, \d+\)"                           => acc
        case r"trait (.*)${name}Definition \{"              => acc
        case r"trait (\w*)$ns extends (\w*)$nsExtra \{"     => acc :+ (ns -> Seq(Seq(nsExtra)))
        case r"trait (\w*)$ns \{"                           => acc :+ (ns -> Seq())
        case r"val\s*(\`?)$q1(\w*)$a(\`?)$q2\s*\=\s*(.*)$s" => {
          val attr = q1 + a + q2

          def extract(str: String, elements: Seq[Any] = Seq()): Seq[Any] = str match {

            // One
            case r"oneString(.*)$rest"  => extract(rest, Seq("attr", attr, "String", 1, "OneString"))
            case r"oneByte(.*)$rest"    => extract(rest, Seq("attr", attr, "Byte", 1, "OneByte"))
            case r"oneShort(.*)$rest"   => extract(rest, Seq("attr", attr, "Short", 1, "OneShort"))
            case r"oneInt(.*)$rest"     => extract(rest, Seq("attr", attr, "Int", 1, "OneInt"))
            case r"oneLong(.*)$rest"    => extract(rest, Seq("attr", attr, "Long", 1, "OneLong"))
            case r"oneFloat(.*)$rest"   => extract(rest, Seq("attr", attr, "Float", 1, "OneFloat"))
            case r"oneDouble(.*)$rest"  => extract(rest, Seq("attr", attr, "Double", 1, "OneDouble"))
            case r"oneBoolean(.*)$rest" => extract(rest, Seq("attr", attr, "Boolean", 1, "OneBoolean"))
            case r"oneDate(.*)$rest"    => extract(rest, Seq("attr", attr, "java.util.Date", 1, "OneDate"))
            case r"oneUUID(.*)$rest"    => extract(rest, Seq("attr", attr, "java.util.UUID", 1, "OneUUID"))
            case r"oneURI(.*)$rest"     => extract(rest, Seq("attr", attr, "java.net.URI", 1, "OneURI"))

            // Many
            case r"manyString(.*)$rest" => extract(rest, Seq("attr", attr, "Set[String]", 2, "ManyString"))
            case r"manyInt(.*)$rest"    => extract(rest, Seq("attr", attr, "Set[Int]", 2, "ManyInt"))
            case r"manyLong(.*)$rest"   => extract(rest, Seq("attr", attr, "Set[Long]", 2, "ManyLong"))
            case r"manyFloat(.*)$rest"  => extract(rest, Seq("attr", attr, "Set[Float]", 2, "ManyFloat"))
            case r"manyDouble(.*)$rest" => extract(rest, Seq("attr", attr, "Set[Double]", 2, "ManyDouble"))
            case r"manyDate(.*)$rest"   => extract(rest, Seq("attr", attr, "Set[java.util.Date]", 2, "ManyDate"))
            case r"manyUUID(.*)$rest"   => extract(rest, Seq("attr", attr, "Set[java.util.UUID]", 2, "ManyUUID"))
            case r"manyURI(.*)$rest"    => extract(rest, Seq("attr", attr, "Set[java.net.URI]", 2, "ManyURI"))

            // Enums
            case r"oneEnum\((.*)$enums\)"  => Seq("enum", attr, "String", 1, "OneEnum") ++ enums.replaceAll("'", "").split(",").toList.map(_.trim)
            case r"manyEnum\((.*)$enums\)" => Seq("enum", attr, "Set[String]", 2, "ManyEnums") ++ enums.replaceAll("'", "").split(",").toList.map(_.trim)

            // Refs
            case r"one\[(.*)$ref\](.*)$rest"  => extract(rest, Seq("OneRef", attr, "Long", 1, ref))
            case r"many\[(.*)$ref\](.*)$rest" => extract(rest, Seq("ManyRef", attr, "Set[Long]", 2, ref))

            // Options
            case r"\.doc\(((\w|\s)*)$msg\)$rest" => extract(rest, elements :+ s"Doc: $msg")
            case r"\.fullTextSearch(.*)$rest"    => extract(rest, elements :+ "FulltextSearch")
            case r"\.uniqueValue(.*)$rest"       => extract(rest, elements :+ "UniqueValue")
            case r"\.uniqueIdentity(.*)$rest"    => extract(rest, elements :+ "UniqueIdentity")
            case r"\.indexed(.*)$rest"           => extract(rest, elements :+ "Indexed")
            case r"\.components(.*)$rest"        => extract(rest, elements :+ "IsComponent")
            case r"\.component(.*)$rest"         => extract(rest, elements :+ "IsComponent")
            case r"\.noHistory(.*)$rest"         => extract(rest, elements :+ "NoHistory")

            case ""         => elements
            case unexpected => sys.error(s"Unexpected attribute code in ${defFile.getName}:\n" + unexpected)
          }
          val ns = acc.last._1
          val prevAttrs = acc.last._2
          val attrDef = extract(s)

          val updatedAttrs = if (prevAttrs.nonEmpty && prevAttrs.head.nonEmpty && prevAttrs.head.size == 1) {
            val extraAttr = prevAttrs.head.head match {
              case "Node"     => Seq("ManyRef", "tree_", "Set[Long]", 2, "PARENT", "IsComponent")
              case unexpected => sys.error(s"Unexpected namespace extension in ${defFile.getName}:" + unexpected)
            }
            prevAttrs.tail ++ Seq(extraAttr, attrDef)
          } else {
            prevAttrs :+ attrDef
          }
          acc.init :+ (ns -> updatedAttrs)
        }
        case "}"                                            => acc
        case unexpected                                     => sys.error(s"Unexpected code in ${defFile.getName}:\n" + unexpected)
      }
    }
    (path, domain, inArity, outArity, nsDefs)
  }

  def baseType(tpe: Any): String = tpe.toString match {
    case r"Set\[([\w\.]*)$t\]" => t
    case t: String             => t
  }

  def handleNamespace(srcManaged: File, path: String, domain: String, inArity: Int, outArity: Int, nsDef: (String, Seq[Seq[Any]]), nsArities: Map[String, Int]) = {

    // Namespace ......................

    val Ns1 = nsDef._1
    val ns1 = firstLow(Ns1)


    // Attributes .....................

    val attrs0 = nsDef._2.map { d =>
      val (cat: String, attr: String, tpe: String, card: Int, ext: String, defs: List[_]) = (d(0), d(1), d(2), d(3), d(4), d.drop(5))
      (cat, attr, tpe, baseType(tpe), card, ext, defs)
    }
    val attrs1 = attrs0.filter(a => a._2 != "tree_")

    val (longestAttr, longestAttrClean, longestType, longestBaseType) = if (attrs1.isEmpty)
      (3, 3, 4, 4) // eid: Long
    else (
      attrs1.map(_._2.toString.length).max,
      attrs1.map(_._2.toString.replace("`", "").length).max,
      attrs1.map(_._3.toString.length).max,
      attrs1.map(_._4.toString.length).max)

    val entityIdDef = ("attr", "eid", "Long", "Long", 1, "OneLong(ns1, ns2)", List())

    val attrs = (entityIdDef +: attrs1).map { case (cat, attr, tpe, baseType, card, ext, defs) =>
      val attrClean = attr.replace("`", "")
      val padAttr = " " * (longestAttr - attr.length)
      val padType = " " * (longestType - tpe.length)
      val padBaseType = " " * (longestBaseType - baseType.length)
      val padAttrClean = " " * (longestAttrClean - attrClean.length)
      (cat, attr, attrClean, tpe, baseType, card, ext, defs.map(_.toString), padAttr, padAttrClean, padType, padBaseType)
    }

    val internalAttrs = attrs0.filter(a => a._2 == "tree_")
    val valueAttrs = attrs.filter(a => a._1 == "attr" || a._1 == "enum")
    val refAttrs = attrs.filter(a => (a._1 == "OneRef" || a._1 == "ManyRef") && a._2 != "tree_")


    // Schema stmts ######################################################################################

    def optionalStmts(defs: List[_]) = defs.collect {
      case doc: String if doc.startsWith("Doc: ") => ":db/doc" -> doc.drop(5)
      case "FulltextSearch"                       => ":db/fulltext" -> "true.asInstanceOf[Object]"
      case "UniqueValue"                          => ":db/unique" -> ":db.unique/value"
      case "UniqueIdentity"                       => ":db/unique" -> ":db.unique/identity"
      case "Indexed"                              => ":db/index" -> "<true>"
      case "IsComponent"                          => ":db/isComponent" -> "<true>"
      case "NoHistory"                            => ":db/noHistory" -> "<true>"
    }.distinct

    val schemaStmts: (String, Seq[Seq[(String, String)]]) = {
      val installStmt = ":db.install/_attribute" -> ":db.part/db"

      val internalStmts = internalAttrs.map {
        case (cat, attr, _, baseType, card, ref, defs) => {
          val valueType = cat match {
            case "ManyRef"  => "ref"
            case unexpected => sys.error("[schemaStmts] Unexpected internal attribute type: " + unexpected)
          }
          val requiredStmts = Seq(
            "<id>" -> ":db.part/db",
            ":db/ident" -> s":$ns1/$attr",
            ":db/valueType" -> s":db.type/$valueType",
            ":db/cardinality" -> (if (card == 1) ":db.cardinality/one" else ":db.cardinality/many"))

          requiredStmts ++ optionalStmts(defs) :+ installStmt
        }
      }

      val attrStmts = valueAttrs.tail.map { case (cat, attr0, attrClean, tpe, baseType, card, ext, defs, _, _, _, _) =>
        val attr = attr0.replace("`", "")
        val valueType = if (cat == "enum") "ref"
        else baseType match {
          case "Int"            => "long"
          case "Date"           => "instant"
          case "java.util.Date" => "instant"
          case "java.util.UUID" => "uuid"
          case "java.net.URI"   => "uri"
          case other            => other.toLowerCase
        }
        val requiredStmts = Seq(
          "<id>" -> ":db.part/db",
          ":db/ident" -> s":$ns1/$attr",
          ":db/valueType" -> s":db.type/$valueType",
          ":db/cardinality" -> (if (card == 1) ":db.cardinality/one" else ":db.cardinality/many"))

        val enumStmts = if (cat == "enum") defs.flatMap(enum => Seq("<enum>" -> s":$ns1.$attr/$enum")) else Nil
        (requiredStmts ++ optionalStmts(defs) :+ installStmt) ++ enumStmts
      }

      val refStmts = attrs.filter(a => a._1 == "OneRef" || a._1 == "ManyRef").map {
        case (_, attr, attrClean, _, refType, _, ref, defs, padAttr, _, padType, _) => {
          val attr = firstLow(attrClean)
          val requiredStmts = Seq(
            "<id>" -> ":db.part/db",
            ":db/ident" -> s":$ns1/$attr",
            ":db/valueType" -> ":db.type/ref",
            ":db/cardinality" -> (if (refType == "OneRef") ":db.cardinality/one" else ":db.cardinality/many"))

          requiredStmts ++ optionalStmts(defs) :+ installStmt
        }
      }

      (Ns1, internalStmts ++ attrStmts ++ refStmts)
    }


    // Base classes ######################################################################################


    // NS =======================================================================================

    // I know, I know, this is clumsy
    val attrs2 = attrs.tail.map {
      case ("OneRef", b, c, d, e, f, g, h, i, j, k, l)  => ("OneRef", b, c, d, e, f, g, h, i, j, k, l, "OneRefAttr")
      case ("ManyRef", b, c, d, e, f, g, h, i, j, k, l) => ("ManyRef", b, c, d, e, f, g, h, i, j, k, l, "ManyRefAttr")
      case (a, b, c, d, e, f, g, h, i, j, k, l)         => (a, b, c, d, e, f, g, h, i, j, k, l, g)
    }
    val longestSchemaType = attrs2.map(_._13.length).max
    val attrClasses = attrs2.map { case (cat, attr, _, tpe, baseType, _, ext, defs, padAttr, padAttrClean, _, _, ext2) =>
      val pad = " " * (longestSchemaType - ext2.length)
      val ext3 = ext2 + pad + " (ns1, ns2)"

      val (extensions, enumValues) = cat match {
//        case "enum"    => (ext3, s"{\n    private lazy val ${defs.mkString(", ")} = EnumValue\n  }")
        case "enum"    => (ext3, s"{ private lazy val ${defs.mkString(", ")} = EnumValue }")
        case "OneRef"  => (ext3, "")
        case "ManyRef" => (ext3, "")
        case _         => ((ext3 +: defs).mkString(" with "), "")
      }
      s"class $attr $padAttr[Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends $extensions $enumValues"
    }.mkString("\n  ").trim


    // Arity classes ######################################################################################

    val inRange = 0 to inArity
    val outRange = 0 to outArity
    val imp = if (attrs.size == 1) Seq() else Seq(s"import $Ns1._")

    val boilerplateClasses = inRange.flatMap { in =>
      outRange.flatMap { out =>

        val InTypes = (0 until in) map (n => "I" + (n + 1))
        val OutTypes = (0 until out) map (n => (n + 'A').toChar)

        if (in == 0) {

          // Molecule_x ================================================================================================

          val TraitTypes = if (out == 0) "" else s"[${OutTypes mkString ", "}]"

          val inputMethods = if (out > 0 && in < inArity) {
            val newInTypes = if (OutTypes.isEmpty) InTypes else InTypes :+ OutTypes.last

            val curTypes = (if (OutTypes.isEmpty) newInTypes else newInTypes ++ OutTypes.init).mkString(", ")
            val curOut = s"${Ns1}_${out - 1}[$curTypes] {}"
            val curIn = s"${Ns1}_In_${in + 1}_${out - 1}[$curTypes] {}"

            val nextInTypes = (newInTypes ++ OutTypes).mkString(", ")
            val nextIn = s"${Ns1}_In_${in + 1}_$out[$nextInTypes] {}"

            val nextOutTypes = OutTypes.mkString(", ")
            val nextOut = s"${Ns1}_$out[$nextOutTypes] {}"

            val nextOutIntTypes = (OutTypes.init :+ "Int").mkString(", ")
            val nextOutInt = s"${Ns1}_$out[$nextOutIntTypes] {}"

            Seq( s"""
                       |  def apply(in: ?.type)    = new $curIn
                       |  def apply(in: ?!.type)   = new $nextIn
                       |  def <(in: ?.type)        = new $nextIn
                       |  def contains(in: ?.type) = new $nextIn
                       |  def apply(m: maybe.type) = new $nextOut
                       |  def apply(c: count.type) = new $nextOutInt
                       |  """.stripMargin.trim)
          } else Seq()

          val outBody = {

            val defaults = if (in + out == 0 && attrs1.nonEmpty) {
              val (_, attr, tpeFirst, _, _, _, _) = attrs1.head
              Seq("def apply(eid: Long) = this")
            } else Seq()

            val attrCode = attrs.map { case (cat, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, padType, padBaseType) =>
              val nextTypes = (OutTypes :+ tpe) mkString ", "
              val nextNS = s"${Ns1}_${out + 1}[$nextTypes]$padType"
              s"lazy val $attr $padAttr= new $attr $padAttr(this, new $nextNS {}) with $nextNS {}"
            }

            val refCode = refAttrs.foldLeft(Seq("")) { case (acc, (cat, attr, attrClean, tpe, refType, _, ref, defs, padAttr, _, padType, _)) =>
              val refs: Seq[String] = if (cat == "ManyRef") {
                // Nested entities
                // Offer max arity equaling the number of attributes in the referenced namespace
                // List(List(T1), List(T1, T2), List(T1, T2, T3), etc.. )
                val refTypeLists: Seq[Seq[String]] = (1 to nsArities.get(ref).get).scanLeft(Seq[String]()) { case (types, i) => types :+ ("T" + i)}.tail
                val maxPad = refTypeLists.last.length * 4 - 2
                val refPad = " " * (s" []($attr: ${ref}_X[])".length + maxPad * 2)
                val refNs: String = s"def ${attr.capitalize}$refPad= new $cat[$Ns1, $ref] with ${ref}_$out$TraitTypes"

                val (named, stars) = refTypeLists.map { refTypeList =>
                  val types = refTypeList.mkString(", ")
                  val pad = " " * (maxPad - types.length)
                  val padStar = pad + (" " * (attr.length - 1))
                  val refTypes = if (refTypeList.size == 1) s"Seq[${refTypeList.head}]" else s"Seq[(${refTypeList.mkString(", ")})]"
                  val allTypes = if (out == 0) refTypes else TraitTypes.init.tail + ", " + refTypes
                  val name = s"def $attr[$types]$pad($attr: ${ref}_${refTypeList.size}[$types])$pad = new ManyRef[$Ns1, $ref] with ${Ns1}_${out + 1}[$allTypes]"
                  val star = s"def *[$types]$padStar($attr: ${ref}_${refTypeList.size}[$types])$pad = new ManyRef[$Ns1, $ref] with ${Ns1}_${out + 1}[$allTypes]"
                  (name, star)
                }.unzip
                refNs +: (named ++ stars)
              } else {
                // Link to referenced cardinality-one namespace
                Seq(s"def ${attr.capitalize} = new $cat[$Ns1, $ref] with ${ref}_$out$TraitTypes")
              }
              acc ++ refs
            }.tail

            if (out == outArity)
              "\n\n"
            else
              (imp ++ defaults ++ attrCode ++ refCode ++ inputMethods).mkString("{\n  ", "\n  ", "\n}\n")
          }
          val Node = if (internalAttrs.isEmpty) "" else "Node"
          Some(s"trait ${Ns1}_$out$TraitTypes extends ${Node}Molecule_$out$TraitTypes $outBody")

        } else {

          // In_i_o ================================================================================================

          val InOutTypes = (InTypes ++ OutTypes) mkString ", "

          val s = if (in > 1) "s" else ""
          val header = if (in > 0 && out == 0) s"/********* Input molecules awaiting $in input$s *******************************/\n\n" else ""


          val attrCode = attrs.map { case (cat, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, padType, padBaseType) =>
            val nextTypes = Seq(InOutTypes, tpe) mkString ", "
            val nextNS = s"${Ns1}_In_${in}_${out + 1}[$nextTypes]$padType"
            s"lazy val $attr $padAttr= new $attr $padAttr(this, new $nextNS {}) with $nextNS {}"
          }

          val refCode = refAttrs.foldLeft(Seq("")) { case (acc, (cat, attr, attrClean, tpe, refType, _, ref, defs, padAttr, _, padType, _)) =>
            val refs: Seq[String] = if (cat == "ManyRef") {
              // Nested entities
              // Offer max arity equaling the number of attributes in the referenced namespace
              // List(List(T1), List(T1, T2), List(T1, T2, T3), etc.. )
              val refTypeLists: Seq[Seq[String]] = (1 to nsArities.get(ref).get).scanLeft(Seq[String]()) { case (types, i) => types :+ ("T" + i)}.tail
              val maxPad = refTypeLists.last.length * 4 - 2
              val refPad = " " * (s" []($attr: ${ref}_X[])".length + maxPad * 2)
              val refNs: String = s"def ${attr.capitalize}$refPad = new $cat[$Ns1, $ref] with ${ref}_In_${in}_$out[$InOutTypes]"

              val (named, stars) = refTypeLists.map { refTypeList =>
                val types = refTypeList.mkString(", ")
                val pad = " " * (maxPad - types.length - 2)
                val padStar = pad + (" " * (attr.length - 1))
                val refTypes = if (refTypeList.size == 1) s"Seq[${refTypeList.head}]" else s"Seq[(${refTypeList.mkString(", ")})]"
                val allTypes = if (out == 0) (InTypes :+ refTypes).mkString(", ") else InOutTypes + ", " + refTypes
                val name = s"def $attr[$types]$pad($attr: ${ref}_${refTypeList.size}[$types])$pad = new ManyRef[$Ns1, $ref] with ${Ns1}_In_${in}_${out + 1}[$allTypes]"
                val star = s"def *[$types]$padStar($attr: ${ref}_${refTypeList.size}[$types])$pad = new ManyRef[$Ns1, $ref] with ${Ns1}_In_${in}_${out + 1}[$allTypes]"
                (name, star)
              }.unzip
              refNs +: (named ++ stars)
            } else {
              // Link to referenced cardinality-one namespace
              Seq(s"def ${attr.capitalize} = new $cat[$Ns1, $ref] with ${ref}_In_${in}_$out[$InOutTypes]")
            }
            acc ++ refs
          }.tail

          val inputMethods = if (out > 0 && in < inArity) {
            val newInTypes = if (OutTypes.isEmpty) InTypes else InTypes :+ OutTypes.last

            val curTypes = (if (OutTypes.isEmpty) newInTypes else newInTypes ++ OutTypes.init).mkString(", ")
            val curNS = s"${Ns1}_In_${in + 1}_${out - 1}[$curTypes] {}"

            val nextTypes = (newInTypes ++ OutTypes).mkString(", ")
            val nextNS = s"${Ns1}_In_${in + 1}_$out[$nextTypes] {}"

            Seq( s"""def apply(in: ?.type)    = new $curNS
                  |  def apply(in: ?!.type)   = new $nextNS
                  |  def <(in: ?.type)        = new $nextNS
                  |  def contains(in: ?.type) = new $nextNS""".stripMargin)
          } else Seq()

          val traitBody = if (out == outArity)
            "\n\n"
          else
            (imp ++ attrCode ++ refCode ++ inputMethods).mkString("{\n  ", "\n  ", "\n}\n")

          Some(s"$header trait ${Ns1}_In_${in}_$out[$InOutTypes] extends In_${in}_$out[$InOutTypes] $traitBody")
        }
      }
    }.mkString("\n").trim

    val domainNs = domain.capitalize + " : " + Ns1 + (if (internalAttrs.isEmpty) "" else " (Node implementation)")
    val connImport = if (internalAttrs.nonEmpty) "\nimport datomic.Connection" else ""
    val body = s"""|/*
                   | * AUTO-GENERATED CODE - DO NOT CHANGE!
                   | *
                   | * Manual changes to this file will likely break molecules!
                   | * Instead, change the molecule definition files and recompile your project with `sbt compile`.
                   | */
                   |package $path.dsl.$domain
                   |import molecule._
                   |import molecule.dsl.schemaDSL._
                   |import molecule.in._
                   |import molecule.out._$connImport
                   |
                   |
                   |trait $Ns1
                   |
                   |object $Ns1 extends $Ns1 with ${Ns1}_0 {
                   |  $attrClasses
                   |}
                   |
                   |$boilerplateClasses
                  """.stripMargin

    val dslFile: File = path.split('.').toList.foldLeft(srcManaged)((file, pkg) => file / pkg) / "dsl" / domain / s"$Ns1.scala"
    IO.write(dslFile, body)
    (schemaStmts, dslFile)
  }

  def mkSchema(srcManaged: File, path: String, domain: String, schemaStmts: Seq[(String, Seq[Seq[(String, String)]])]) = {
    val keyValues = for {
      (ns, nsStmts) <- schemaStmts
      node = if (nsStmts.count(_.count(_._2.endsWith("tree_")) > 0) > 0) " (Node)" else ""
      attrStmts <- nsStmts
      (key, value) <- attrStmts
    } yield (ns, key, value, node)

    val (_, _, stmtString) = keyValues.foldLeft(("", "", "")) { case ((ns1, key1, acc), (ns, key, value, node)) =>
      val line = "-" * (65 - (ns.length + node.length))
      key match {
        case "<id>" if ns1 == ""      => (ns, key, acc + s"""|// $ns$node $line
                                                             |
                                                             |    Util.map(":db/id"                , Peer.tempid(":db.part/db")""".stripMargin)
        case "<id>" if ns1 != ns      => (ns, key, acc + s"""|),
                                                             |
                                                             |
                                                             |    // $ns$node $line
                                                             |
                                                             |    Util.map(":db/id"                , Peer.tempid(":db.part/db")""".stripMargin)
        case "<id>"                   => (ns1, key, acc + s"""),\n\n    Util.map(":db/id"                , Peer.tempid(":db.part/db")""".stripMargin)
        case ":db/ident"              => (ns1, key, acc + s""",\n             ":db/ident"             , "$value"""")
        case ":db/valueType"          => (ns1, key, acc + s""",\n             ":db/valueType"         , "$value"""")
        case ":db/cardinality"        => (ns1, key, acc + s""",\n             ":db/cardinality"       , "$value"""")
        case ":db/doc"                => (ns1, key, acc + s""",\n             ":db/doc"               , true.asInstanceOf[Object]""")
        case ":db/fulltext"           => (ns1, key, acc + s""",\n             ":db/fulltext"          , true.asInstanceOf[Object]""")
        case ":db/unique"             => (ns1, key, acc + s""",\n             ":db/unique"            , "$value"""")
        case ":db/index"              => (ns1, key, acc + s""",\n             ":db/index"             , true.asInstanceOf[Object]""")
        case ":db/isComponent"        => (ns1, key, acc + s""",\n             ":db/isComponent"       , true.asInstanceOf[Object]""")
        case ":db/noHistory"          => (ns1, key, acc + s""",\n             ":db/noHistory"         , true.asInstanceOf[Object]""")
        case ":db.install/_attribute" => (ns1, key, acc + s""",\n             ":db.install/_attribute", "$value"""")

        case "<enum>" if key1 != key => (ns1, key, acc +
          s"""),\n\n    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", "$value"""".stripMargin)
        case "<enum>"                => (ns1, key, acc +
          s"""),\n    Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", "$value"""".stripMargin)
        case unexpected              => sys.error("[mkSchema] Unexpected attribute key: " + unexpected)
      }
    }

    val body = s"""|/*
                   | * AUTO-GENERATED CODE - DO NOT CHANGE!
                   | *
                   | * Manual changes to this file will likely break schema creations!
                   | * Instead, change the molecule definition files and recompile your project with `sbt compile`
                   | */
                   |package $path.schema
                   |import molecule.dsl.Transaction
                   |import datomic.{Util, Peer}
                   |
                   |object ${domain}Schema extends Transaction {
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
      val definitionFiles = IO.listFiles(new File(domainDir) / "schema").filter(f => f.isFile && f.name.endsWith("Definition.scala"))
      assert(definitionFiles.size > 0, "Found no definition files in path: " + domainDir)

      // Loop definition files in each domain directory
      definitionFiles.flatMap { definitionFile =>
        val (path, domain, inArity, outArity, nsDefs) = extractNsDefinitions(definitionFile)
        val nsArities = nsDefs.map(ns => ns._1 -> ns._2.size).toMap

        // Loop namespaces in each definition file
        val (schemaStmts, dslFiles) = nsDefs.map(handleNamespace(srcManaged, path, domain, inArity, outArity, _, nsArities)).unzip

        // Create schema file
        val domainFileName = definitionFile.name.replace("Definition.scala", "")
        val schemaFile = mkSchema(srcManaged, path, domainFileName, schemaStmts)

        dslFiles :+ schemaFile
      }
    }
  }
}