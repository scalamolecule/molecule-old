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
        case r"trait (\w*)$ns \{"                           => acc :+ (ns -> Seq())
        case r"val\s*(\`?)$q1(\w*)$a(\`?)$q2\s*\=\s*(.*)$s" => {
          val attr = q1 + a + q2

          def extract(str: String, elements: Seq[Any] = Seq()): Seq[Any] = str match {

            // One
            case r"oneString(.*)$rest"  => extract(rest, Seq("attr", attr, "String", 1, "OneString(ns, ns2)"))
            case r"oneByte(.*)$rest"    => extract(rest, Seq("attr", attr, "Byte", 1, "OneByte(ns, ns2)"))
            case r"oneShort(.*)$rest"   => extract(rest, Seq("attr", attr, "Short", 1, "OneShort(ns, ns2)"))
            case r"oneInt(.*)$rest"     => extract(rest, Seq("attr", attr, "Int", 1, "OneInt(ns, ns2)"))
            case r"oneLong(.*)$rest"    => extract(rest, Seq("attr", attr, "Long", 1, "OneLong(ns, ns2)"))
            case r"oneFloat(.*)$rest"   => extract(rest, Seq("attr", attr, "Float", 1, "OneFloat(ns, ns2)"))
            case r"oneDouble(.*)$rest"  => extract(rest, Seq("attr", attr, "Double", 1, "OneDouble(ns, ns2)"))
            case r"oneBoolean(.*)$rest" => extract(rest, Seq("attr", attr, "Boolean", 1, "OneBoolean(ns, ns2)"))
            case r"oneDate(.*)$rest"    => extract(rest, Seq("attr", attr, "java.util.Date", 1, "OneDate(ns, ns2)"))
            case r"oneUUID(.*)$rest"    => extract(rest, Seq("attr", attr, "java.util.UUID", 1, "OneUUID(ns, ns2)"))
            case r"oneURI(.*)$rest"     => extract(rest, Seq("attr", attr, "java.net.URI", 1, "OneURI(ns, ns2)"))

            // Many
            case r"manyString(.*)$rest" => extract(rest, Seq("attr", attr, "Set[String]", 2, "ManyString(ns, ns2)"))
            case r"manyInt(.*)$rest"    => extract(rest, Seq("attr", attr, "Set[Int]", 2, "ManyInt(ns, ns2)"))
            case r"manyLong(.*)$rest"   => extract(rest, Seq("attr", attr, "Set[Long]", 2, "ManyLong(ns, ns2)"))
            case r"manyFloat(.*)$rest"  => extract(rest, Seq("attr", attr, "Set[Float]", 2, "ManyFloat(ns, ns2)"))
            case r"manyDouble(.*)$rest" => extract(rest, Seq("attr", attr, "Set[Double]", 2, "ManyDouble(ns, ns2)"))
            case r"manyDate(.*)$rest"   => extract(rest, Seq("attr", attr, "Set[java.util.Date]", 2, "ManyDate(ns, ns2)"))
            case r"manyUUID(.*)$rest"   => extract(rest, Seq("attr", attr, "Set[java.util.UUID]", 2, "ManyUUID(ns, ns2)"))
            case r"manyURI(.*)$rest"    => extract(rest, Seq("attr", attr, "Set[java.net.URI]", 2, "ManyURI(ns, ns2)"))

            // Enums
            case r"oneEnum\((.*)$enums\)"  => Seq("enum", attr, "String", 1, "OneEnum(ns, ns2)") ++ enums.replaceAll("'", "").split(",").toList.map(_.trim)
            case r"manyEnum\((.*)$enums\)" => Seq("enum", attr, "Set[String]", 2, "ManyEnums(ns, ns2)") ++ enums.replaceAll("'", "").split(",").toList.map(_.trim)

            // Refs
            case r"one\[(.*)$ref\](.*)$rest"  => extract(rest, Seq("ref", attr, "OneRef", 1, ref))
            case r"many\[(.*)$ref\](.*)$rest" => extract(rest, Seq("ref", attr, "ManyRef", 2, ref))

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
          val updatedAttrs = prevAttrs :+ attrDef
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
    case r"OneRef"             => "Long"
    case r"ManyRef"            => "Long"
    case t: String             => t
  }

  def handleNamespace(srcManaged: File, path: String, domain: String, inArity: Int, outArity: Int, nsDef: (String, Seq[Seq[Any]])) = {

    // Namespace ......................

    val Ns = nsDef._1
    val ns = firstLow(Ns)
    //    val attrDefs = nsDef._2


    // Attributes .....................

    //    val attrs0 = attrDefs.filter(a => a.head == "attr" || a.head == "enum").map { d =>
    //      val (cat: String, attr: String, tpe: String, card: Int, defs: List[_]) = (d.head, d.tail.head, d.tail.tail.head, d.tail.tail.tail.head, d.tail.tail.tail.tail)
    val attrs0 = nsDef._2.map { d =>
      val (cat: String, attr: String, tpe: String, card: Int, ext: String, defs: List[_]) = (d(0), d(1), d(2), d(3), d(4), d.drop(5))
      (cat, attr, tpe, baseType(tpe), card, ext, defs)
    }

    val (longestAttr, longestAttrClean, longestType, longestBaseType) = if (attrs0.isEmpty)
      (3, 3, 4, 4) // eid: Long
    else (
      attrs0.map(_._2.toString.length).max,
      attrs0.map(_._2.toString.replace("`", "").length).max,
      attrs0.map(_._3.toString.length).max,
      attrs0.map(_._4.toString.length).max)

    val entityIdDef = ("attr", "eid", "Long", "Long", 1, "OneLong(ns, ns2)", List())

    val attrs = (entityIdDef +: attrs0).map { case (cat, attr, tpe, baseType, card, ext, defs) =>
      val cleanType = if (cat == "ref") "Long" else tpe
      val attrClean = attr.replace("`", "")
      val padAttr = " " * (longestAttr - attr.length)
      val padType = " " * (longestType - cleanType.length)
      val padBaseType = " " * (longestBaseType - baseType.length)
      val padAttrClean = " " * (longestAttrClean - attrClean.length)
      (cat, attr, attrClean, tpe, baseType, card, ext, defs.map(_.toString), padAttr, padAttrClean, padType, padBaseType)
    }

    val valueAttrs = attrs.filter(a => a._1 == "attr" || a._1 == "enum")
    val refAttrs = attrs.filter(a => a._1 == "ref")


    //    // Refs ...........................
    //
    //    val refs0 = attrDefs.filter(_.head == "ref").map { d =>
    //      val (attr: String, refType: String, ref: String, defs: List[_]) = (d.tail.head, d.tail.tail.head, d.tail.tail.tail.head, d.tail.tail.tail.tail)
    //      (attr, refType, ref, defs)
    //    }
    //    val refs = refs0.map { case (attr, refType, ref, defs) =>
    //      val padAttr = " " * (longestAttr - attr.length)
    //      val padType = " " * (longestType - refType.length)
    //
    //
    //      ("ref", attr, attrClean, _, refType, _, ref, defs, padAttr, _, padType, _)
    //      (attr.capitalize, refType, ref, defs, padAttr, padType)
    //    }


    // Schema stmts ######################################################################################

    def optional(defs: List[_]) = defs.collect {
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

      val attrTxs = valueAttrs.tail.map { case (cat, attr0, attrClean, tpe, baseType, card, ext, defs, _, _, _, _) =>
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
        val required = Seq(
          "<id>" -> ":db.part/db",
          ":db/ident" -> s":$ns/$attr",
          ":db/valueType" -> s":db.type/$valueType",
          ":db/cardinality" -> (if (card == 1) ":db.cardinality/one" else ":db.cardinality/many"))

        val enumStmts = if (cat == "enum") defs.flatMap(enum => Seq("<enum>" -> s":$ns.$attr/$enum")) else Nil
        (required ++ optional(defs) :+ installStmt) ++ enumStmts
      }

      val refTxs = {
        //        refs.map { case ((attr0, refType, ref, defs, _, _)) =>
        refAttrs.map { case (_, attr, attrClean, _, refType, _, ref, defs, padAttr, _, padType, _) =>
          val attr = firstLow(attrClean)
          val required = Seq(
            "<id>" -> ":db.part/db",
            ":db/ident" -> s":$ns/$attr",
            ":db/valueType" -> ":db.type/ref",
            ":db/cardinality" -> (if (refType == "OneRef") ":db.cardinality/one" else ":db.cardinality/many"))

          required ++ optional(defs) :+ installStmt
        }
      }
      (Ns, attrTxs ++ refTxs)
    }


    // Base classes ######################################################################################


    // NS =======================================================================================

    val attrClasses = attrs.tail.map { case (cat, attr, _, tpe, baseType, _, ext, defs, padAttr, padAttrClean, _, _) =>
      val (extensions, enumValues) = cat match {
        case "enum" => (ext, s"private lazy val ${defs.mkString(", ")} = EnumValue")
        case "ref"  => (tpe, "")
        case _      => ((ext +: defs).mkString(" with "), "")
      }
      val oldNew = s"def apply(data: oldNew[$baseType]) = ${Ns}_Update()"
      val baseElements = Seq(enumValues, oldNew) map (_.trim) filter (_.nonEmpty) mkString "\n    "
      s"""class $attr[NS, NS2](ns: NS, ns2: NS2) extends $extensions {
       |    $baseElements
       |  }
       |""".stripMargin
    }.mkString("\n  ").trim



    // NS_Insert =======================================================================================

    val insertAttrs1 = valueAttrs.map { case (cat, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, _, padBaseType) =>
      val enumPrefix = if (cat == "enum") s""", Some(":$ns.$attrClean/")""" else ""
      if (card == 1)
        s"""lazy val $attr $padAttr= (data: $baseType$padBaseType) => _insert(Seq(data), $card, "$attrClean"$padAttrClean, "$tpe"$padBaseType$enumPrefix)"""
      else
        s"lazy val $attr $padAttr= ${attr.replace("`", "")}_"
    } mkString "\n  "

    val insertAttrs2 = valueAttrs.filter(_._6 == 2).map { case (cat, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, padType, _) =>
      val pad = " " * (baseType.length - 2)
      val enumPrefix = if (cat == "enum") s""", Some(":$ns.$attrClean/")""" else ""
      s"""private[$domain] object ${attrClean}_ {
       |    def apply(h: $baseType, t: $baseType*) = _insert(h +: t.toList, $card, "$attrClean"$padAttrClean, "$tpe"$enumPrefix)
       |    def apply(data: Seq[$baseType])$pad = _insert(data,          $card, "$attrClean"$padAttrClean, "$tpe"$enumPrefix)
       |  }""".stripMargin
    } mkString "\n  "

    //    val insertRefs = refs.map { case (attr, _, ref, defs, padAttr, _) =>
    val insertRefs = refAttrs.map { case (_, attr, attrClean, _, refType, _, ref, defs, padAttr, _, padType, _) =>
      s"def ${attr.capitalize} = ${ref}_Insert(elements)"
    } mkString "\n  "

    val inserts = Seq(insertAttrs1, insertAttrs2, insertRefs) map (_.trim) filter (_.nonEmpty) mkString "\n\n  "



    // NS_Update =======================================================================================

    val updateAttrs1 = valueAttrs.map { case (_, attr, attrClean, _, _, _, _, _, padAttr, padAttrClean, _, _) =>
      s"lazy val $attr $padAttr= ${attrClean}_"
    } mkString "\n  "

    val updateAttrs2 = valueAttrs.map { case (cat, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, padType, padBaseType) =>
      val enumPrefix = if (cat == "enum") s""", Some(":$ns.$attrClean/")""" else ""
      if (card == 1) {
        val pad = " " * longestBaseType
        s"""private[$domain] object ${attrClean}_ {
         |    def apply(data: $baseType) $padBaseType= _assertNewFact(Seq(data), $card, "$attrClean", "$tpe"$enumPrefix)
         |    def apply()$pad       = _retract(                 $card, "$attrClean")
         |  }""".stripMargin
      } else {
        val pad3 = " " * (baseType.length * 3)
        val pad4 = " " * (baseType.length * 4)
        s"""private[$domain] object ${attrClean}_ {
         |    def apply(h: ($baseType, $baseType), t: ($baseType, $baseType)*) = _swap(h +: t.toList            , "$attrClean", "$tpe"$enumPrefix)
         |    def remove(values: $baseType*) $pad3       = _removeElements(Seq(values: _*), "$attrClean", "$tpe"$enumPrefix)
         |    def add(data: $baseType) $pad3             = _assertNewFact(Seq(data),     2, "$attrClean", "$tpe"$enumPrefix)
         |    def apply() $pad4                 = _retract(                     $card, "$attrClean")
         |  }""".stripMargin
      }
    } mkString "\n  "

    val updateRefs = refAttrs.map { case (_, attr, attrClean, _, refType, _, ref, defs, padAttr, _, padType, _) =>
      //    val updateRefs = refs.map { case (_, attr, _, refType, ref, _, defs, padAttr, _, padType) =>
      s"def ${attr.capitalize} = ${ref}_Update(elements)"
    } mkString "\n  "

    val updates = Seq(updateAttrs1, updateAttrs2, updateRefs) map (_.trim) filter (_.nonEmpty) mkString "\n\n  "



    // NS_Retract =======================================================================================

    val retractAttrs1 = valueAttrs.map { case (_, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, _, padBaseType) =>
      if (card == 1)
        s"""lazy val $attr $padAttr= _retract($card, "$attrClean"$padAttrClean, "$tpe"$padBaseType)"""
      else
        s"lazy val $attr $padAttr= ${attrClean}_"
    } mkString "\n  "

    val retractAttrs2 = valueAttrs.filter(_._6 == 2).map { case (_, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, padType, padBaseType) =>
      val pad = " " * (baseType.length - 2)
      s"""private[$domain] object ${attrClean}_ {
       |    def apply() $padBaseType                     = _retract($card, "$attrClean", "$tpe")
       |    def apply(h: $baseType, t: $baseType*) = _retract($card, "$attrClean", "$tpe")
       |    def apply(data: Seq[$baseType]) $pad= _retract($card, "$attrClean", "$tpe")
       |  }""".stripMargin
    } mkString "\n  "

    val retractRefs = refAttrs.map { case (_, attr, attrClean, _, refType, _, ref, defs, padAttr, _, padType, _) =>
      //    val retractRefs = refs.map { case  (_, attr, _, refType, ref, _, defs, padAttr, _, padType) =>
      s"def ${attr.capitalize} = ${ref}_Retract(elements)"
    } mkString "\n  "

    val retracts = Seq(retractAttrs1, retractAttrs2, retractRefs) map (_.trim) filter (_.nonEmpty) mkString "\n\n  "



    // NS_Entity =======================================================================================

    val entities1 = valueAttrs.map { case (_, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, _, padBaseType) =>
      s"""lazy val $attr $padAttr= _get($card, "$attrClean"$padAttrClean, "$tpe"$padBaseType)"""
    } mkString "\n  "

    val entityRefs = refAttrs.map { case (_, attr, attrClean, _, refType, _, ref, defs, padAttr, _, padType, _) =>
      //    val entityRefs = refs.map { case  (_, attr, _, refType, ref, _, defs, padAttr, _, padType) =>
      s"def ${attr.capitalize} = ${ref}_Entity(elements)"
    } mkString "\n  "

    val entities = Seq(entities1, entityRefs) map (_.trim) filter (_.nonEmpty) mkString "\n\n  "



    // Arity classes ######################################################################################

    val inRange = 0 to inArity
    val outRange = 0 to outArity
    val imp = if (attrs.size == 1) Seq() else Seq(s"import $Ns._")

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

            val defaults = if (in + out == 0 && attrs0.nonEmpty) {
              val (_, attr, tpeFirst, _, _, _, _) = attrs0.head
//              val nameFirst = attr.capitalize
//              Seq( s"""|
//                       |  @default
//                       |  def apply($attr: $tpeFirst) = new $attr(this, new ${Ns}_Out_1[$tpeFirst] {}) with ${Ns}_Out_1[$tpeFirst]
//                       |  def apply(id: Long) = ${Ns}_Entity()
//                       |
//                       |  def update(id: Long) = ${Ns}_Update(Seq(), Seq(id))
//                       |  def update(ids: Seq[Long]) = ${Ns}_Update(Seq(), ids)
//                       |""".stripMargin)
                            Seq( s"""def apply(eid: Long)        = ${Ns}_Entity()
                                  |  def update(eid: Long)       = ${Ns}_Update(Seq(), Seq(eid))
                                  |  def update(eids: Seq[Long]) = ${Ns}_Update(Seq(), eids)
                                  |""".stripMargin)
            } else Seq()

            val attrCode = attrs.map { case (cat, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, padType, padBaseType) =>
              val cleanType = if (cat == "ref") "Long" else tpe
              val nextTypes = (OutTypes :+ cleanType) mkString ", "
              val nextNS = s"${Ns}_Out_${out + 1}[$nextTypes]$padType"
              s"lazy val $attr $padAttr= new $attr $padAttr(this, new $nextNS {}) with $nextNS {}"
            }

            val refCode = refAttrs.foldLeft(Seq("")) { case (acc, (_, attr, attrClean, tpe, refType, _, ref, defs, padAttr, _, padType, _)) =>
              //            val refCode = refs.foldLeft(Seq("")) { case (acc,  (_, attr, _, refType, ref, _, defs, padAttr, _, padType)) =>
              acc :+ s"def ${attr.capitalize} = new $tpe with ${ref}_Out_$out$TraitTypes"
            }.tail

            if (out == outArity)
              "\n\n"
            else
              (imp ++ defaults ++ attrCode ++ refCode ++ inputMethods).mkString("{\n  ", "\n  ", "\n}\n")
          }

          Some(s"trait ${Ns}_Out_$out$TraitTypes extends Out_$out$TraitTypes $outBody")

        } else {

          // In_i_o ================================================================================================

          val InOutTypes = (InTypes ++ OutTypes) mkString ", "

          val s = if (in > 1) "s" else ""
          val header = if (in > 0 && out == 0) s"/********* Input molecules awaiting $in input$s *******************************/\n\n" else ""

          val InTraits = s"trait ${Ns}_In_${in}_$out[$InOutTypes] extends In_${in}_$out[$InOutTypes]"

          val attrCode = attrs.map { case (cat, attr, attrClean, tpe, baseType, card, _, _, padAttr, padAttrClean, padType, padBaseType) =>
            val cleanType = if (cat == "ref") "Long" else tpe
            val nextTypes = Seq(InOutTypes, cleanType) mkString ", "
            val nextNS = s"${Ns}_In_${in}_${out + 1}[$nextTypes]$padType"
            s"lazy val $attr $padAttr= new $attr $padAttr(this, new $nextNS {}) with $nextNS {}"
          }

          val refCode = refAttrs.foldLeft(Seq("")) { case (acc, (_, attr, attrClean, tpe, refType, _, ref, defs, padAttr, _, padType, _)) =>
            //          val refCode = refs.foldLeft(Seq("")) { case (acc,  (_, attr, _, refType, ref, _, defs, padAttr, _, padType)) =>
            acc :+ s"def ${attr.capitalize} = new $tpe with ${ref}_In_${in}_$out[$InOutTypes]"
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
            (imp ++ attrCode ++ refCode ++ inputMethods).mkString("{\n  ", "\n  ", "\n}\n")

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
                   |package $path.dsl.$domain
                   |import molecule._
                   |import molecule.ast.model._
                   |import molecule.dsl.schemaDSL._
                   |import molecule.in._
                   |import molecule.out._
                   |
                   |object $Ns extends ${Ns}_Out_0 {
                   |  $attrClasses
                   |
                   |  def insert = ${Ns}_Insert()
                   |}
                   |
                   |// Todo: The entity api is not yet implemented in Molecule
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
                   |    ${Ns}_Insert(elements :+ Atom("$ns", attr, tpe, card, Eq(data), enumPrefix))
                   |}
                   |
                   |case class ${Ns}_Update(override val elements: Seq[Element] = Seq(), override val ids: Seq[Long] = Seq()) extends Update(elements, ids) {
                   |  $updates
                   |
                   |  private def _assertNewFact(data: Seq[Any], card: Int, attr: String, tpe: String, enumPrefix: Option[String] = None) =
                   |    ${Ns}_Update(elements :+ Atom("$ns", attr, tpe, card, Eq(data), enumPrefix), ids)
                   |
                   |  private def _swap(oldNew: Seq[(Any, Any)], attr: String, tpe: String, enumPrefix: Option[String] = None) =
                   |    ${Ns}_Update(elements :+ Atom("$ns", attr, tpe, 2, Replace(oldNew.toMap), enumPrefix), ids)
                   |
                   |  private def _removeElements(values: Seq[Any], attr: String, tpe: String, enumPrefix: Option[String] = None) =
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
                   |    ${Ns}_Retract(elements :+ Atom("$ns", attr, tpe, card, Eq(data)))
                   |}
                   |
                   |//********* Output molecules *******************************/
                   |
                   | $boilerplateClasses
                  """.stripMargin

    val dslFile: File = path.split('.').toList.foldLeft(srcManaged)((file, pkg) => file / pkg) / "dsl" / domain / s"$Ns.scala"
    IO.write(dslFile, body)
    (schemaStmts, dslFile)
  }

  def mkSchema(srcManaged: File, path: String, domain: String, schemaStmts: Seq[(String, Seq[Seq[(String, String)]])]) = {

    val keyValues = for {
      (ns, nsStmts) <- schemaStmts
      attrStmts <- nsStmts
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
      }
    }

    val body = s"""|/*
                   | * AUTO-GENERATED CODE - DO NOT CHANGE!
                   | *
                   | * Manual changes to this file will likely break schema creations!
                   | * Instead, change the molecule definition files and recompile your project with `sbt compile`
                   | */
                   |package $path.schema
                   |import molecule.ast.Transaction
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

        // Loop namespaces in each definition file
        val (schemaStmts, dslFiles) = nsDefs.map(handleNamespace(srcManaged, path, domain, inArity, outArity, _)).unzip

        // Create schema file
        val domainFileName = definitionFile.name.replace("Definition.scala", "")
        val schemaFile = mkSchema(srcManaged, path, domainFileName, schemaStmts)

        dslFiles :+ schemaFile
      }
    }
  }
}