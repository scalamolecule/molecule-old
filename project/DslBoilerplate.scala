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
        case r"trait (\w*)$ns\s*extends\s*Tree\s*\{"        => acc :+ (ns -> Seq(Seq("ManyRef", "tree_", "Set[Long]", 2, ns, "IsComponent")))
        case r"trait (\w*)$ns \{"                           => acc :+ (ns -> Seq())
        case r"val\s*(\`?)$q1(\w*)$a(\`?)$q2\s*\=\s*(.*)$s" => {
          val attr = q1 + a + q2
          //        case r"trait (\w*)$ns\s*extends\s*SubComponentOf2\[(\w*)$a,\s*(\w*)$b\]\s*\{" => acc :+ (ns -> Seq(Seq("ManyRef", "tree_", "Set[Long]", 2, "PARENT", "IsComponent", Seq(a, b))))

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
            case r"\.fullTextSearch(.*)$rest"    => extract(rest, elements :+ "FulltextSearch[Ns]")
            case r"\.uniqueValue(.*)$rest"       => extract(rest, elements :+ "UniqueValue")
            case r"\.uniqueIdentity(.*)$rest"    => extract(rest, elements :+ "UniqueIdentity")
            case r"\.indexed(.*)$rest"           => extract(rest, elements :+ "Indexed")
            case r"\.subComponents(.*)$rest"     => extract(rest, elements :+ "IsComponent")
            case r"\.subComponent(.*)$rest"      => extract(rest, elements :+ "IsComponent")
            case r"\.noHistory(.*)$rest"         => extract(rest, elements :+ "NoHistory")

            case ""         => elements
            case unexpected => sys.error(s"Unexpected attribute code in ${defFile.getName}:\n" + unexpected)
          }
          val ns = acc.last._1
          val prevAttrs = acc.last._2
          val attrDef = extract(s)
          acc.init :+ (ns -> (prevAttrs :+ attrDef))
        }
        case "}"                                            => acc
        case unexpected                                     => sys.error(s"Unexpected code in ${defFile.getName}:\n" + unexpected)
      }
    }
    (path, domain, inArity, outArity, nsDefs)
  }

  def collectBackRefs(ns: String, otherNsDefs: Seq[(String, Seq[Seq[Any]])]) = {
    otherNsDefs.foldLeft(Seq[Seq[Any]]()) { case (backRefs, (otherNs, otherAttrs)) =>
      otherAttrs.foldLeft(backRefs) { case (backRefs2, otherAttr) =>
        val (cat, refAttr, card, refNs) = (otherAttr(0).toString, otherAttr(1).toString, otherAttr(3), otherAttr(4).toString)
        if ((cat == "OneRef" || cat == "ManyRef") && refNs == ns)
          backRefs2 :+ Seq(s"BackRef", refAttr, "Set[Long]", card, otherNs)
        else
          backRefs2
      }
    }
  }

  def addBackReferences(nsDefs: Seq[(String, Seq[Seq[Any]])]) = nsDefs.foldLeft(Seq[(String, Seq[Seq[Any]])]()) { case (nsDef2, nsDef) =>
    val (ns, attrs) = nsDef
    val otherNsDefs = nsDefs.filter(_._1 != ns)
    val backRefs: Seq[Seq[Any]] = collectBackRefs(ns, otherNsDefs)
    nsDef2 :+ (ns -> (attrs ++ backRefs))
  }

  def handleNamespace(srcManaged: File, path: String, domain: String, inArity: Int, outArity: Int, nsDef: (String, Seq[Seq[Any]]), nsArities: Map[String, Int]) = {

    // Namespace ......................

    val Ns1 = nsDef._1
    val ns1 = firstLow(Ns1)


    // Attributes .....................

    val attrs0 = nsDef._2.map { d =>
      val (cat: String, attr: String, tpe: String, card: Int, ext: String, defs: List[_]) = (d(0), d(1), d(2), d(3), d(4), d.drop(5))
      val baseType = tpe.toString match {
        case r"Set\[([\w\.]*)$t\]" => t
        case t: String             => t
      }
      (cat, attr, tpe, baseType, card, ext, defs)
    }
    val attrs1 = attrs0.filter(a => a._2 != "tree_" && a._1 != "BackRef")
    val treeAttrs = attrs0.filter(a => a._2 == "tree_")
    val (l1, l2) = ("_parent".length + ns1.length, "Long".length)

    val (longestAttr, longestAttrClean, longestType, longestBaseType) = if (attrs1.isEmpty)
      (l2, l2, l2, l2)
    else {
      val (a, b, c, d) = (
        attrs1.map(_._2.toString.length).max,
        attrs1.map(_._2.toString.replace("`", "").length).max,
        attrs1.map(_._3.toString.length).max,
        attrs1.map(_._4.toString.length).max)
      if (treeAttrs.nonEmpty)
        (if (a > l1) a else l1, if (b > l1) b else l1, if (c > l2) c else l2, if (d > l2) d else l2)
      else
        (if (a > l2) a else l2, if (b > l2) b else l2, if (c > l2) c else l2, if (d > l2) d else l2)
    }

    val entityIdDef = ("attr", "eid", "Long", "Long", 1, "OneLong(ns1, ns2)", List())

    def padS(longest: Int, str: String) = pad(longest, str.length)
    def pad(longest: Int, shorter: Int) = if (longest > shorter) " " * (longest - shorter) else ""

    val attrs = attrs1.map { case (cat, attr, tpe, baseType, card, ext, defs) =>
      val attrClean = attr.replace("`", "")
      val padAttr = padS(longestAttr, attr)
      val padAttrClean = padS(longestAttrClean, attrClean)
      val padType = padS(longestType, tpe)
      val padBaseType = padS(longestBaseType, baseType)
      (cat, attr, attrClean, tpe, baseType, card, ext, defs.map(_.toString), padAttr, padAttrClean, padType, padBaseType)
    }

    val valueAttrs = attrs.filter(a => a._1 == "attr" || a._1 == "enum")
    val refAttrs = attrs.filter(a => (a._1 == "OneRef" || a._1 == "ManyRef") && a._2 != "tree_")
    val backRefAttrs = attrs0.filter(a => a._1 == "BackRef")


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

      val internalStmts = treeAttrs.map {
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

      val attrStmts = valueAttrs.map { case (cat, attr0, attrClean, tpe, baseType, card, ext, defs, _, _, _, _) =>
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

    // Todo. This is messy. Need to make an AST instead of working with string tuples
    val attrs2 = attrs.map {
      case ("OneRef", b, c, d, e, f, g, h, i, j, k, l)  => ("OneRef", b, c, d, e, f, g, h, i, j, k, l, "OneRefAttr")
      case ("ManyRef", b, c, d, e, f, g, h, i, j, k, l) => ("ManyRef", b, c, d, e, f, g, h, i, j, k, l, "ManyRefAttr")
      case (a, b, c, d, e, f, g, h, i, j, k, l)         => (a, b, c, d, e, f, g, h, i, j, k, l, g)
    }
    val longestSchemaType = {
      val max = attrs2.map(_._13.length).max
      if (max < "ManyString".length) "ManyString".length else max
    }
    val longestDefs = {
      val max = attrs2.filter(_._1 != "enum").map(_._8.mkString(" with ").length).max
      if (max > 0) " with ".length + max else 0
    }

    val pad1 = pad(longestDefs, 0)
    val pad2 = pad(longestAttr, 4)
    val pad3 = pad(longestSchemaType, 10)

    val genericClasses = Seq(
      s"class eid $pad2[Ns] extends OneLong   $pad3[Ns]$pad1 { self: Ns => }",
      s"class attr$pad2[Ns] extends ManyString$pad3[Ns]$pad1 { self: Ns => }",
      s"class ns  $pad2[Ns] extends ManyString$pad3[Ns]$pad1 { self: Ns => }"
    ).mkString("\n  ").trim

    val attrClasses = attrs2.map { case (cat, attr, _, tpe, baseType, _, ext, defs, padAttr, padAttrClean, _, _, ext2) =>
      val pad1 = pad(longestSchemaType, ext2.length)
      val ext3 = ext2 + pad1 + s"[Ns]"
      val defsLength = if (cat != "enum" && defs.nonEmpty) " with ".length + defs.mkString(" with ").length else 0
      val (extensions, enumValues, pad2) = cat match {
        case "enum" => (ext3, s"private lazy val ${defs.mkString(", ")} = EnumValue ", "")
        case _      => ((ext3 +: defs).mkString(" with "), "", pad(longestDefs, defsLength))
      }
      //      println(s"$ns1, $attr, $ext, $longestSchemaType, $ext2, $defs - $longestDefs - $defsLength")
      s"class $attr$padAttr[Ns] extends $extensions$pad2 { self: Ns => $enumValues}"
    }.mkString("\n  ").trim


    // Arity classes ######################################################################################

    val inRange = 0 to inArity
    val outRange = 0 to outArity
    val imp = if (attrs.isEmpty) Seq() else Seq(s"import $Ns1._")

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
            val curIn = s"${Ns1}_In_${in + 1}_${out - 1}[$curTypes]"
            val nextInTypes = (newInTypes ++ OutTypes).mkString(", ")
            val nextIn = s"${Ns1}_In_${in + 1}_$out[$nextInTypes]"
            Seq( s"""
               |  def apply(in: ?.type)    : $curIn = ???
               |  def apply(in: ?!.type)   : $nextIn = ???
               |  def <(in: ?.type)        : $nextIn = ???
               |  def contains(in: ?.type) : $nextIn = ???
               |  """.stripMargin.trim)
          } else Seq()

          val extraMethods = if (out > 0) {
            val nextOutTypes = OutTypes.mkString(", ")
            val nextOut = s"${Ns1}_$out[$nextOutTypes]"
            val nextOutIntTypes = (OutTypes.init :+ "Int").mkString(", ")
            val nextOutInt = s"${Ns1}_$out[$nextOutIntTypes]"
            Seq( s"""
               |  def apply(m: maybe.type) : $nextOut   = ???
               |  def apply(c: count.type) : $nextOutInt = ???
               |  """.stripMargin.trim)
          } else Seq()

          val outBody = {

            val apply = if (in + out == 0 && attrs1.nonEmpty) {
              val (_, attr, tpeFirst, _, _, _, _) = attrs1.head
              Seq( s"""|  def apply(eid: Long)     : ${Ns1}_0      = ???
                       |  def apply(c: count.type) : ${Ns1}_1[Int] = ???""".stripMargin.trim)
            } else Seq()

            val (attrVals, attrVals_) = attrs.map { case (cat, attr, attrClean, tpe, _, _, defs, _, padAttr, padAttrClean, padType, _) =>
              val nextTypes = (OutTypes :+ tpe) mkString ", "
              val nextNS = s"${Ns1}_${out + 1}[$nextTypes]"
              val thisTypes = OutTypes mkString ", "
              val thisNS = if (out == 0) s"${Ns1}_0" else s"${Ns1}_$out[$thisTypes]"

              val padLongestType = pad(longestType, 0)
              (s"val $attr  $padAttr: $attr$padAttr[$nextNS]$padType with $nextNS$padType = ???",
                s"val ${attrClean}_ $padAttrClean: $attr$padAttr[$thisNS]  $padLongestType with $thisNS  $padLongestType = ???")
            }.unzip

            val genericVals = {
              def nextNS(tpe: String) = s"${Ns1}_${out + 1}[${(OutTypes :+ tpe) mkString ", "}]"
              val thisNS = if (out == 0) s"${Ns1}_0" else s"${Ns1}_$out[${OutTypes mkString ", "}]"
              val pad4 = pad(longestAttr, 4)
              val pad7 = pad(longestAttr, 7)
              val padType = " " * (longestType - (if (longestType > 6) longestType - 6 else 0))
              val padType2 = " " * (2 + longestType + (longestType - (if (longestType > 6) longestType - 6 else 0)))
              val padT4 = pad(longestType, 4)
              val padT6 = pad(longestType, 6)
              val padT = pad(longestType, 0)
              val commonVals = Seq(
                "",
                s"val eid   $pad4: eid $pad4[${nextNS("Long")}]$padT4 with ${nextNS("Long")}$padT4 = ???",
                s"val attr  $pad4: attr$pad4[${nextNS("String")}]$padT6 with ${nextNS("String")}$padT6 = ???",
                s"val ns    $pad4: ns  $pad4[${nextNS("String")}]$padT6 with ${nextNS("String")}$padT6 = ???",
                s"val eid_  $pad4: eid $pad4[$thisNS]  $padT with $thisNS  $padT = ???",
                s"val attr_ $pad4: attr$pad4[$thisNS]  $padT with $thisNS  $padT = ???",
                s"val ns_   $pad4: ns  $pad4[$thisNS]  $padT with $thisNS  $padT = ???"
              )
              val treeVals = if (treeAttrs.nonEmpty) Seq(
                "",
                s"val _parent$Ns1  : _parent$pad7[${nextNS("Long")}]$padT4 with ${nextNS("Long")}$padT4 = ???",
                s"val _parent${Ns1}_ : _parent$pad7[$thisNS]  $padT with $thisNS  $padT = ???",
                "",
                s"def _Parent$Ns1  : BackRef[$Ns1, $Ns1] with $thisNS = ???"
                //                s"val _parent  $pad7: _parent$pad7[${nextNS("Long")}]$padT4 with ${nextNS("Long")}$padT4 = ???",
                //                s"val _parent_ $pad7: _parent$pad7[$thisNS]  $padT with $thisNS  $padT = ???",
                //                s"def Parent$Ns1 : BackRef[$Ns1, $Ns1] with $thisNS = ???"
              )
              else Seq()

              commonVals ++ treeVals
            }

            val refCode = refAttrs.foldLeft(Seq("")) { case (acc, (cat, attr, _, _, _, _, ref, _, _, _, _, _)) =>
              val refs: Seq[String] = if (cat == "ManyRef") {
                // Nested entities
                // Offer max arity equaling the number of attributes in the referenced namespace
                // List(List(T1), List(T1, T2), List(T1, T2, T3), etc.. )
                val refTypeLists: Seq[Seq[String]] = (1 to nsArities.get(ref).get).scanLeft(Seq[String]()) { case (types, i) => types :+ ("T" + i)}.tail
                val maxPad = refTypeLists.last.length * 4 - 2
                val refPad = " " * (s" []($ref: ${ref}_X[])".length + maxPad * 2)
                val refPad2 = " " * (Ns1.length - ref.length + 7 + refTypeLists.last.length * 4)
                val refNs: String = s"def ${attr.capitalize}$refPad: $cat[$Ns1, $ref] with ${ref}_$out$TraitTypes$refPad2 = ???"
                val backRefs = refTypeLists.map { refTypeList =>
                  val types = refTypeList.mkString(", ")
                  val pad = " " * (maxPad - types.length)
                  val pad2 = " " * (if (refTypeList.length == 1) 2 else 0)
                  val refTypes = if (refTypeList.size == 1) s"Seq[${refTypeList.head}]" else s"Seq[(${refTypeList.mkString(", ")})]"
                  val allTypes = if (out == 0) refTypes else TraitTypes.init.tail + ", " + refTypes
                  val name = s"def $attr[$types]$pad(${firstLow(ref)}: ${ref}_${refTypeList.size}[$types])$pad : ManyRef[$Ns1, $ref] with ${Ns1}_${out + 1}[$allTypes]$pad$pad2 = ???"
                  name
                }
                refNs +: backRefs
              } else {
                // Link to referenced cardinality-one namespace
                Seq(s"def ${attr.capitalize} : $cat[$Ns1, $ref] with ${ref}_$out$TraitTypes = ???")
              }
              acc ++ refs
            } //.tail

            val backRefCode = backRefAttrs.foldLeft(Seq("")) { case (acc, (cat, backAttr, _, baseType, card, backRef, _)) =>
              val backRefs: Seq[String] = {
                // Nested entities
                // Offer max arity equaling the number of attributes in the referenced namespace
                // List(List(T1), List(T1, T2), List(T1, T2, T3), etc.. )
                val backRefTypeLists: Seq[Seq[String]] = (1 to nsArities.get(backRef).get).scanLeft(Seq[String]()) { case (types, i) => types :+ ("T" + i)}.tail
                val maxPad = backRefTypeLists.last.length * 4 - 2
                val backRefPad = " " * (s" []($backRef: ${backRef}_X[])".length + maxPad * 2 - backRef.size)
                val backRefPad2 = " " * (Ns1.length - backRef.length + 7 + backRefTypeLists.last.length * 4)
                val backRefPostPad = " " * (maxPad - 2)
                val backRefNs: String = s"\n  def _${backAttr.capitalize}$backRef$backRefPad: BackRef[$backRef, $Ns1] with ${backRef}_$out$TraitTypes$backRefPad2 = ???"
                val nextBackRefTypes = (OutTypes :+ "Set[Long]") mkString ", "
                val backRefIds: String = s"def _$backAttr$backRef$backRefPad: BackRef[$backRef, $Ns1] with ${Ns1}_${out + 1}[$nextBackRefTypes]$backRefPostPad = ???"
                val backRefGroup = backRefTypeLists.map { backRefTypeList =>
                  val types = backRefTypeList.mkString(", ")
                  val pad = " " * ((backRefTypeLists.last.length - backRefTypeList.length) * 4)
                  val pad2 = " " * (if (backRefTypeList.length == 1) 2 else 0)
                  val backRefTypes = if (backRefTypeList.size == 1) s"Seq[${backRefTypeList.head}]" else s"Seq[(${backRefTypeList.mkString(", ")})]"
                  val allTypes = if (out == 0) backRefTypes else TraitTypes.init.tail + ", " + backRefTypes
                  val name = s"def _$backAttr[$types]$pad(${firstLow(backRef)}: ${backRef}_${backRefTypeList.size}[$types])$pad : BackRef[$backRef, $Ns1] with ${Ns1}_${out + 1}[$allTypes]$pad$pad2 = ???"
                  name
                }
                backRefNs +: backRefIds +: backRefGroup
              }
              acc ++ backRefs
            }.tail

            if (out == outArity)
              "\n\n"
            else
              (imp ++ apply ++ attrVals ++ attrVals_ ++ genericVals ++ refCode ++ inputMethods ++ extraMethods ++ backRefCode).mkString("{\n  ", "\n  ", "\n}\n")
          }
          //          val Tree = if (treeAttrs.isEmpty) "" else " with Tree"
          //          Some(s"trait ${Ns1}_$out$TraitTypes extends $Ns1 with Molecule_$out$TraitTypes$Tree $outBody")
          Some(s"trait ${Ns1}_$out$TraitTypes extends $Ns1 with Molecule_$out$TraitTypes $outBody")

        } else {

          // In_i_o ================================================================================================

          val InOutTypes = (InTypes ++ OutTypes) mkString ", "

          val s = if (in > 1) "s" else ""
          val header = if (in > 0 && out == 0) s"/********* Input molecules awaiting $in input$s *******************************/\n\n" else ""


          val (attrCode, attrCode_) = attrs.map { case (cat, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, padType, padBaseType) =>
            val nextNS = s"${Ns1}_In_${in}_${out + 1}[$InOutTypes, $tpe]"
            val thisNS = s"${Ns1}_In_${in}_$out[$InOutTypes]"
            (s"lazy val $attr  $padAttr= new $attr$padAttr[$nextNS]$padType with $nextNS",
              s"lazy val ${attrClean}_ $padAttrClean= new $attr$padAttr[$thisNS] with $thisNS")
          }.unzip

          val refCode = refAttrs.foldLeft(Seq("")) { case (acc, (cat, attr, attrClean, tpe, refType, _, ref, defs, padAttr, _, padType, _)) =>
            val refs: Seq[String] = if (cat == "ManyRef") {
              // Nested entities
              // Offer max arity equaling the number of attributes in the referenced namespace
              // List(List(T1), List(T1, T2), List(T1, T2, T3), etc.. )
              val refTypeLists: Seq[Seq[String]] = (1 to nsArities.get(ref).get).scanLeft(Seq[String]()) { case (types, i) => types :+ ("T" + i)}.tail
              val maxPad = refTypeLists.last.length * 4 - 2
              val refPad = " " * (s" []($ref: ${ref}_X[])".length + maxPad * 2)
              val refNs: String = s"def ${attr.capitalize}$refPad = new $cat[$Ns1, $ref] with ${ref}_In_${in}_$out[$InOutTypes]"
              val named = refTypeLists.map { refTypeList =>
                val types = refTypeList.mkString(", ")
                val pad = " " * (maxPad - types.length - 2)
                val refTypes = if (refTypeList.size == 1) s"Seq[${refTypeList.head}]" else s"Seq[(${refTypeList.mkString(", ")})]"
                val allTypes = if (out == 0) (InTypes :+ refTypes).mkString(", ") else InOutTypes + ", " + refTypes
                val name = s"def $attr[$types]$pad(${firstLow(ref)}: ${ref}_${refTypeList.size}[$types])$pad = new ManyRef[$Ns1, $ref] with ${Ns1}_In_${in}_${out + 1}[$allTypes]"
                name
              }
              refNs +: named
            } else {
              // Link to referenced cardinality-one namespace
              Seq(s"def ${attr.capitalize} = new $cat[$Ns1, $ref] with ${ref}_In_${in}_$out[$InOutTypes]")
            }
            acc ++ refs
          }.tail

          val backRefCode = backRefAttrs.foldLeft(Seq("")) { case (acc, (cat, backAttr, _, baseType, card, backRef, _)) =>
            val backRefs: Seq[String] = {
              // Nested entities
              // Offer max arity equaling the number of attributes in the referenced namespace
              // List(List(T1), List(T1, T2), List(T1, T2, T3), etc.. )
              val backRefTypeLists: Seq[Seq[String]] = (1 to nsArities.get(backRef).get).scanLeft(Seq[String]()) { case (types, i) => types :+ ("T" + i)}.tail
              val maxPad = backRefTypeLists.last.length * 4 - 2
              val backRefPad = " " * (s" []($backRef: ${backRef}_X[])".length + maxPad * 2 - backRef.size)
              val backRefNs: String = s"\n  def _${backAttr.capitalize}$backRef$backRefPad= new BackRef[$backRef, $Ns1] with ${backRef}_In_${in}_$out[$InOutTypes]"
              val nextBackRefTypes = (InTypes ++ OutTypes :+ "Set[Long]") mkString ", "
              val backRefIds: String = s"def _$backAttr$backRef$backRefPad= new BackRef[$backRef, $Ns1] with ${Ns1}_In_${in}_${out + 1}[$nextBackRefTypes]"
              val named = backRefTypeLists.map { backRefTypeList =>
                val types = backRefTypeList.mkString(", ")
                val pad = " " * (maxPad - types.length)
                val backRefTypes = if (backRefTypeList.size == 1) s"Seq[${backRefTypeList.head}]" else s"Seq[(${backRefTypeList.mkString(", ")})]"
                val allTypes = if (out == 0) (InTypes :+ backRefTypes).mkString(", ") else InOutTypes + ", " + backRefTypes
                val name = s"def _$backAttr[$types]$pad(${firstLow(backRef)}: ${backRef}_${backRefTypeList.size}[$types])$pad = new BackRef[$backRef, $Ns1] with ${Ns1}_In_${in}_${out + 1}[$allTypes]"
                name
              }
              backRefNs +: backRefIds +: named
            }
            acc ++ backRefs
          }.tail

          val inputMethods = if (out > 0 && in < inArity) {
            val newInTypes = if (OutTypes.isEmpty) InTypes else InTypes :+ OutTypes.last
            val curTypes = (if (OutTypes.isEmpty) newInTypes else newInTypes ++ OutTypes.init).mkString(", ")
            val curInOut = s"${Ns1}_In_${in + 1}_${out - 1}[$curTypes] {}"
            val nextTypes = (newInTypes ++ OutTypes).mkString(", ")
            val nextInOut = s"${Ns1}_In_${in + 1}_$out[$nextTypes] {}"
            Seq( s"""def apply(in: ?.type)    = new $curInOut
                  |  def apply(in: ?!.type)   = new $nextInOut
                  |  def <(in: ?.type)        = new $nextInOut
                  |  def contains(in: ?.type) = new $nextInOut""".stripMargin)
          } else Seq()

          val extraMethods = if (out > 0) {
            val nextOutTypes = (InTypes ++ OutTypes).mkString(", ")
            val nextOut = s"${Ns1}_In_${in}_$out[$nextOutTypes] {}"
            val nextOutIntTypes = (InTypes ++ OutTypes.init :+ "Int").mkString(", ")
            val nextOutInt = s"${Ns1}_In_${in}_$out[$nextOutIntTypes] {}"
            Seq( s"""def apply(m: maybe.type) = new $nextOut
                  |  def apply(c: count.type) = new $nextOutInt""".stripMargin)
          } else Seq()

          val traitBody = if (out == outArity)
            "\n\n"
          else
            (imp ++ attrCode ++ attrCode_ ++ refCode ++ inputMethods ++ extraMethods ++ backRefCode).mkString("{\n  ", "\n  ", "\n}\n")

          Some(s"$header trait ${Ns1}_In_${in}_$out[$InOutTypes] extends $Ns1 with In_${in}_$out[$InOutTypes] $traitBody")
        }
      }
    }.mkString("\n").trim

    //    val domainNs = domain.capitalize + " : " + Ns1 + (if (internalAttrs.isEmpty) "" else " (Sub implementation)")
    //    val connImport = if (treeAttrs.nonEmpty) "\nimport datomic.Connection" else ""
    val extendsTree = if (treeAttrs.nonEmpty) "extends Tree" else ""
    val inImport = if (inArity > 0) "\nimport molecule.in._" else ""
    val body = s"""|/*
                   | * AUTO-GENERATED CODE - DO NOT CHANGE!
                   | *
                   | * Manual changes to this file will likely break molecules!
                   | * Instead, change the molecule definition files and recompile your project with `sbt compile`.
                   | */
                   |package $path.dsl.$domain
                   |import molecule._
                   |import molecule.dsl.schemaDSL._$inImport
                   |import molecule.out._
                   |
                   |
                   |trait $Ns1 $extendsTree
                   |
                   |object $Ns1 extends $Ns1 with ${Ns1}_0 {
                   |  $attrClasses
                   |
                   |  $genericClasses
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
      sub = if (nsStmts.count(_.count(_._2.endsWith("tree_")) > 0) > 0) " (Tree)" else ""
      attrStmts <- nsStmts
      (key, value) <- attrStmts
    } yield (ns, key, value, sub)

    val (_, _, stmtString) = keyValues.foldLeft(("", "", "")) { case ((ns1, key1, acc), (ns, key, value, sub)) =>
      val line = "-" * (65 - (ns.length + sub.length))
      key match {
        case "<id>" if ns1 == ""      => (ns, key, acc + s"""|// $ns$sub $line
                                                             |
                                                             |    Util.map(":db/id"                , Peer.tempid(":db.part/db")""".stripMargin)
        case "<id>" if ns1 != ns      => (ns, key, acc + s"""|),
                                                             |
                                                             |
                                                             |    // $ns$sub $line
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

      var p = ""

      // Loop definition files in each domain directory
      val xx = definitionFiles.flatMap { definitionFile =>
        val (path, domain, inArity, outArity, nsDefs) = extractNsDefinitions(definitionFile)
        val nsArities = nsDefs.map(ns => ns._1 -> (ns._2.size + 1 - ns._2.map(attr => if (attr(1) == "tree_") 1 else 0).sum)).toMap
        val nsDefs2 = addBackReferences(nsDefs)

        p += s"\n------- $domain -------"
        nsDefs2.foreach { case (ns, nsDef) => p += nsDef.mkString(s"\n$ns\n - ", "\n - ", "")}

        // Loop namespaces in each definition file
        val (schemaStmts, dslFiles) = nsDefs2.map(handleNamespace(srcManaged, path, domain, inArity, outArity, _, nsArities)).unzip

        // Create schema file
        val domainFileName = definitionFile.name.replace("Definition.scala", "")
        val schemaFile = mkSchema(srcManaged, path, domainFileName, schemaStmts)

        dslFiles :+ schemaFile
      }

      //      println(p)

      xx
    }
  }
}