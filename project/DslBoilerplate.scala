import java.io.File
import sbt._

// Generate molecule dsl from definition files


object DslBoilerplate {

  // Definition AST .......................................

  case class Definition(pkg: String, imports: Seq[String], in: Int, out: Int, domain: String, nss: Seq[Namespace]) {
    def addAttr(attr: Attr) = {
      val previousNs = nss.init
      val lastNs = nss.last
      copy(nss = previousNs :+ lastNs.copy(attrs = lastNs.attrs :+ attr))
    }
  }

  case class Namespace(ns: String, opt: Option[Extension] = None, attrs: Seq[Attr] = Seq())

  sealed trait Extension
  case class Tree() extends Extension {
    override def toString = " (Tree)"
  }
  case class HyperEdge(edges: Seq[String]) extends Extension {
    override def toString = s" (HyperEdge to ${edges.mkString(", ")})"
  }

  sealed trait Attr {
    val attr     : String
    val attrClean: String
    val clazz    : String
    val tpe      : String
  }
  case class Val(attr: String, attrClean: String, clazz: String, tpe: String, baseTpe: String, datomicTpe: String, options: Seq[Optional] = Seq()) extends Attr
  case class Enum(attr: String, attrClean: String, clazz: String, tpe: String, baseTpe: String, enums: Seq[String]) extends Attr
  case class Ref(attr: String, attrClean: String, clazz: String, clazz2: String, tpe: String, baseTpe: String, refNs: String) extends Attr
  case class BackRef(attr: String, attrClean: String, clazz: String, clazz2: String, tpe: String, baseTpe: String, backRef: String) extends Attr

  case class Optional(datomicKeyValue: String, clazz: String)


  // Helpers ..........................................

  def padS(longest: Int, str: String) = pad(longest, str.length)
  def pad(longest: Int, shorter: Int) = if (longest > shorter) " " * (longest - shorter) else ""
  def firstLow(str: Any) = str.toString.head.toLower + str.toString.tail
  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }


  // Parse ..........................................

  def parse(defFile: File) = {
    val raw = IO.readLines(defFile) filterNot (_.isEmpty) map (_.trim)

    // Check package statement
    val path: String = raw.collectFirst {
      case r"package (.*)$p\..*" => p
    }.getOrElse {
      sys.error("Found no package statement in definition file")
    }

    // Check input/output arities
    val (inArity, outArity) = raw collect {
      case r"@InOut\((\d+)$in, (\d+)$out\)" => (in.toString.toInt, out.toString.toInt) match {
        case (i: Int, _) if i < 0 || i > 3  => sys.error(s"Input arity in '${defFile.getName}' was $in. It should be between 0-3")
        case (_, o: Int) if o < 1 || o > 22 => sys.error(s"Output arity of '${defFile.getName}' was $out. It should be between 1-22")
        case (i: Int, o: Int)               => (i, o)
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

    // Check domain name
    val domain = raw collect {
      case r"trait (.*)${name}Definition"      => name
      case r"trait (.*)${name}Definition \{"   => name
      case r"trait (.*)${name}Definition \{\}" => name
    } match {
      case Nil                      => sys.error("Couldn't find definition trait <domain>Definition in " + defFile.getName)
      case l: List[_] if l.size > 1 => sys.error(s"Only one definition trait per definition file allowed. Found ${l.size}:" + l.mkString("\n - ", "Definition\n - ", "Definition"))
      case domainNameList           => firstLow(domainNameList.head)
    }


    def parseOptions(str: String, acc: Seq[Optional] = Seq()): Seq[Optional] = str match {
      case r"\.doc\((.*)$msg\)(.*)$str" => parseOptions(str, acc :+ Optional( s"""":db/doc"               , $msg""", ""))
      case r"\.fullTextSearch(.*)$str"  => parseOptions(str, acc :+ Optional( """":db/fulltext"          , true.asInstanceOf[Object]""", "FulltextSearch[Ns, In]"))
      case r"\.uniqueValue(.*)$str"     => parseOptions(str, acc :+ Optional( """":db/unique"            , ":db.unique/value"""", "UniqueValue"))
      case r"\.uniqueIdentity(.*)$str"  => parseOptions(str, acc :+ Optional( """":db/unique"            , ":db.unique/identity"""", "UniqueIdentity"))
      case r"\.indexed(.*)$str"         => parseOptions(str, acc :+ Optional( """":db/index"             , true.asInstanceOf[Object]""", "Indexed"))
      case r"\.subComponents(.*)$str"   => parseOptions(str, acc :+ Optional( """":db/isComponent"       , true.asInstanceOf[Object]""", "IsComponent"))
      case r"\.subComponent(.*)$str"    => parseOptions(str, acc :+ Optional( """":db/isComponent"       , true.asInstanceOf[Object]""", "IsComponent"))
      case r"\.noHistory(.*)$str"       => parseOptions(str, acc :+ Optional( """":db/noHistory"         , true.asInstanceOf[Object]""", "NoHistory"))
      case ""                           => acc
      case unexpected                   => sys.error(s"Unexpected options code in ${defFile.getName}:\n" + unexpected)
    }

    def parseAttr(attr: String, attrClean: String, str: String) = str match {
      case r"oneString(.*)$str"  => Val(attr, attrClean, "OneString", "String", "", "string", parseOptions(str))
      case r"oneByte(.*)$str"    => Val(attr, attrClean, "OneByte", "Byte", "", "byte", parseOptions(str))
      case r"oneShort(.*)$str"   => Val(attr, attrClean, "OneShort", "Short", "", "short", parseOptions(str))
      case r"oneInt(.*)$str"     => Val(attr, attrClean, "OneInt", "Int", "", "long", parseOptions(str))
      case r"oneLong(.*)$str"    => Val(attr, attrClean, "OneLong", "Long", "", "long", parseOptions(str))
      case r"oneFloat(.*)$str"   => Val(attr, attrClean, "OneFloat", "Float", "", "float", parseOptions(str))
      case r"oneDouble(.*)$str"  => Val(attr, attrClean, "OneDouble", "Double", "", "double", parseOptions(str))
      case r"oneBoolean(.*)$str" => Val(attr, attrClean, "OneBoolean", "Boolean", "", "boolean", parseOptions(str))
      case r"oneDate(.*)$str"    => Val(attr, attrClean, "OneDate", "java.util.Date", "", "instant", parseOptions(str))
      case r"oneUUID(.*)$str"    => Val(attr, attrClean, "OneUUID", "java.util.UUID", "", "uuid", parseOptions(str))
      case r"oneURI(.*)$str"     => Val(attr, attrClean, "OneURI", "java.net.URI", "", "uri", parseOptions(str))

      case r"manyString(.*)$str" => Val(attr, attrClean, "ManyString", "Set[String]", "String", "string", parseOptions(str))
      case r"manyInt(.*)$str"    => Val(attr, attrClean, "ManyInt", "Set[Int]", "Int", "long", parseOptions(str))
      case r"manyLong(.*)$str"   => Val(attr, attrClean, "ManyLong", "Set[Long]", "Long", "long", parseOptions(str))
      case r"manyFloat(.*)$str"  => Val(attr, attrClean, "ManyFloat", "Set[Float]", "Float", "float", parseOptions(str))
      case r"manyDouble(.*)$str" => Val(attr, attrClean, "ManyDouble", "Set[Double]", "Double", "double", parseOptions(str))
      case r"manyDate(.*)$str"   => Val(attr, attrClean, "ManyDate", "Set[java.util.Date]", "java.util.Date", "instant", parseOptions(str))
      case r"manyUUID(.*)$str"   => Val(attr, attrClean, "ManyUUID", "Set[java.util.UUID]", "java.util.UUID", "uuid", parseOptions(str))
      case r"manyURI(.*)$str"    => Val(attr, attrClean, "ManyURI", "Set[java.net.URI]", "java.net.URI", "uri", parseOptions(str))

      case r"oneEnum\((.*)$enums\)"  => Enum(attr, attrClean, "OneEnum", "String", "", enums.replaceAll("'", "").split(",").toList.map(_.trim))
      case r"manyEnum\((.*)$enums\)" => Enum(attr, attrClean, "ManyEnums", "Set[String]", "String", enums.replaceAll("'", "").split(",").toList.map(_.trim))

      case r"one\[(.*)$ref\](.*)$str"  => Ref(attr, attrClean, "OneRefAttr", "OneRef", "Long", "", ref)
      case r"many\[(.*)$ref\](.*)$str" => Ref(attr, attrClean, "ManyRefAttr", "ManyRef", "Set[Long]", "Long", ref)
      case unexpected                  => sys.error(s"Unexpected attribute code in ${defFile.getName}:\n" + unexpected)
    }

    val definition: Definition = raw.foldLeft(Definition("", Seq(), -1, -1, "", Seq())) {
      case (d, line) => line match {
        case r"\/\/.*" /* comments allowed */                 => d
        case r"package (.*)$path\.[\w]*"                      => d.copy(pkg = path)
        case "import molecule.dsl.schemaDefinition._"         => d
        case r"@InOut\((\d+)$inS, (\d+)$outS\)"               => d.copy(in = inS.toString.toInt, out = outS.toString.toInt)
        case r"trait (.*)${dmn}Definition \{"                 => d.copy(domain = dmn)
        case r"trait (\w*)$ns\s*extends\s*Tree\s*\{"          => d.copy(nss = d.nss :+ Namespace(ns, Some(Tree())))
        case r"trait (\w*)$ns\s*extends\s*HyperEdge\s*\{"     => d.copy(nss = d.nss :+ Namespace(ns, Some(HyperEdge(Seq()))))
        case r"trait (\w*)$ns\s*\{"                           => d.copy(nss = d.nss :+ Namespace(ns))
        case r"val\s*(\`?)$q1(\w*)$a(\`?)$q2\s*\=\s*(.*)$str" => d.addAttr(parseAttr(q1 + a + q2, a, str))
        case "}"                                              => d
        case unexpected                                       => sys.error(s"Unexpected definition code in ${defFile.getName}:\n" + unexpected)
      }
    }

    //    // Only allow refs in hyper edge definitions
    //    val checkHyperEdges = definition.nss.map { ns =>
    //      ns.opt match {
    //        case Some(hyperEdge: HyperEdge) =>
    //          ns.attrs.map {
    //            case ref@Ref(_, _, _, _, _, _, _) => ref
    //            case unexpected                   =>
    //              sys.error(s"Unexpected attribute in hyper edge definition `${ns.ns}` (only refs allowed):\n" + unexpected)
    //          }
    //        case _                          => ns
    //      }
    //    }
    definition
  }

  def resolve(definition: Definition) = {
    // Add backrefs to hyper edge in affected namespaces
    val newNss = definition.nss.foldLeft(definition.nss) { case (nss2, ns) =>
      ns.opt match {
        case Some(hyperEdge: HyperEdge) =>
          // Gather refs of hyper edge
          val refs = ns.attrs.map {
            case ref@Ref(_, _, _, _, _, _, refNs) => refNs
            case unexpected                       =>
              sys.error(s"Unexpected attribute in hyper edge definition `${ns.ns}` (only refs allowed):\n" + unexpected)
          }
          nss2.map {
            // Namespace is a backref to hyper edge (could be many)
            case ns2 if refs.contains(ns2.ns) =>
              val attrs2 = ns2.attrs :+ BackRef("_" + firstLow(ns.ns), "_" + firstLow(ns.ns), "ManyRefAttr", "ManyRef", "Set[Long]", "Long", ns.ns)
              ns2.copy(attrs = attrs2)
            case ns2 if ns2.ns == ns.ns       => ns2.copy(opt = Some(HyperEdge(refs)))
            case ns2                          => ns2
          }
        case _                          => nss2
      }
    }
    definition.copy(nss = newNss)
  }

  // Generate ..........................................

  def schemaBody(d: Definition) = {

    def attrStmts(ns: String, a: Attr) = {
      val ident = s"""":db/ident"             , ":${firstLow(ns)}/${a.attrClean}""""
      def tpe(t: String) = s"""":db/valueType"         , ":db.type/$t""""
      def card(c: String) = s"""":db/cardinality"       , ":db.cardinality/$c""""
      val stmts = a match {
        case Val(_, _, clazz, _, _, t, options) if clazz.take(3) == "One" => Seq(tpe(t), card("one")) ++ options.map(_.datomicKeyValue)
        case Val(_, _, _, _, _, t, options)                               => Seq(tpe(t), card("many")) ++ options.map(_.datomicKeyValue)
        case a: Attr if a.clazz.take(3) == "One"                          => Seq(tpe("ref"), card("one"))
        case a: Attr                                                      => Seq(tpe("ref"), card("many"))
        case unexpected                                                   => sys.error(s"Unexpected attribute statement:\n" + unexpected)
      }
      val all = (ident +: stmts) ++ Seq(
        """":db/id"                , Peer.tempid(":db.part/db")""",
        """":db.install/_attribute", ":db.part/db""""
      )
      s"Util.map(${all.mkString(",\n             ")})"
    }

    def enums(ns: String, a: String, es: Seq[String]) = es.map(e =>
      s"""Util.map(":db/id", Peer.tempid(":db.part/user"), ":db/ident", ":$ns.$a/$e")""").mkString(",\n    ")

    val stmts = d.nss map { ns =>
      val exts = ns.opt.getOrElse("").toString
      val header = "\n    // " + ns.ns + exts + " " + ("-" * (65 - (ns.ns.length + exts.length)))
      val attrs = ns.attrs.flatMap { a =>
        val attr = attrStmts(ns.ns, a)
        a match {
          case e: Enum => Seq(attr, enums(ns.ns, a.attrClean, e.enums))
          case _       => Seq(attr)
        }
      }
      header + "\n\n    " + attrs.mkString(",\n\n    ")
    }

    s"""|/*
        | * AUTO-GENERATED CODE - DON'T CHANGE!
        | *
        | * Manual changes to this file will likely break schema creations!
        | * Instead, change the molecule definition files and recompile your project with `sbt compile`
        | */
        |package ${d.pkg}.schema
        |import molecule.dsl.Transaction
        |import datomic.{Util, Peer}
        |
        |object ${d.domain}Schema extends Transaction {
        |
        |  lazy val tx = Util.list(
        |    ${stmts.mkString(",\n    ")}
        |  )
        |}""".stripMargin
  }

  def nsTrait(namesp: Namespace, in: Int, out: Int, maxIn: Int, maxOut: Int) = {
    val (ns, option, attrs) = (namesp.ns, namesp.opt, namesp.attrs)
    val InTypes = (0 until in) map (n => "I" + (n + 1))
    val OutTypes = (0 until out) map (n => (n + 'A').toChar.toString)
    val pad1 = (s: String) => padS(attrs.map(_.attr.length).max, s)
    val pad2 = (s: String) => padS(attrs.map(_.tpe.length).max, s)

    val (attrVals, attrVals_) = attrs.map { a =>
      val (attr, attrClean, tpe) = (a.attr, a.attrClean, a.tpe)
      val (p1, p2, p3) = (padS(attrs.map(_.attr.length).max, attr), padS(attrs.map(_.attr.length).max, attrClean), padS(attrs.map(_.tpe.length).max, tpe))

      val (nextNS, thisNS) = (in, out) match {
        case (0, 0) => (
          s"${ns}_1[$tpe$p3]",
          s"${ns}_0")

        case (0, o) => (
          s"${ns}_${o + 1}[${(OutTypes :+ tpe) mkString ", "}$p3]",
          s"${ns}_$o[${OutTypes mkString ", "}]")

        case (i, o) => (
          s"${ns}_In_${i}_${o + 1}[${(InTypes ++ OutTypes :+ tpe) mkString ", "}$p3]",
          s"${ns}_In_${i}_$o[${(InTypes ++ OutTypes) mkString ", "}]")
      }

      val (nextIn, thisIn) = if (maxIn == 0 || in == maxIn) ("Nothing", "Nothing")
      else (in, out) match {
        case (0, 0) => (
          s"${ns}_In_1_1[$tpe$p3, $tpe$p3]",
          s"${ns}_In_1_0[$tpe$p3]")

        case (0, o) => (
          s"${ns}_In_1_${o + 1}[$tpe$p3, ${(OutTypes :+ tpe) mkString ", "}$p3]",
          s"${ns}_In_1_$o[$tpe$p3, ${OutTypes mkString ", "}]")

        case (i, 0) => (
          s"${ns}_In_${i + 1}_1[${(InTypes :+ tpe) mkString ", "}$p3, $tpe$p3]",
          s"${ns}_In_${i + 1}_0[${(InTypes :+ tpe) mkString ", "}$p3]")

        case (i, o) => (
          s"${ns}_In_${i + 1}_${o + 1}[${(InTypes :+ tpe) mkString ", "}$p3, ${(OutTypes :+ tpe) mkString ", "}$p3]",
          s"${ns}_In_${i + 1}_$o[${(InTypes :+ tpe) mkString ", "}$p3, ${OutTypes mkString ", "}]")
      }

      (s"val $attr  $p1: $attr$p1[$nextNS, $nextIn] with $nextNS = ???",
        s"val ${attrClean}_ $p2: $attr$p1[$thisNS, $thisIn] with $thisNS = ???")
    }.unzip

    val refCode = attrs.foldLeft(Seq("")) {
      case (acc, Ref(attr, _, _, clazz, _, _, refNs)) =>
        val ref = (in, out) match {
          case (0, 0) => s"${refNs}_0"
          case (0, o) => s"${refNs}_$o[${OutTypes mkString ", "}]"
          case (i, o) => s"${refNs}_In_${i}_$o[${(InTypes ++ OutTypes) mkString ", "}]"
        }
        acc :+ s"def ${attr.capitalize} : $clazz[$ns, $refNs] with $ref = ???"

      case (acc, BackRef(_, _, _, clazz, _, _, backRef)) =>
        val ref = (in, out) match {
          case (0, 0) => s"${backRef}_0"
          case (0, o) => s"${backRef}_$o[${OutTypes mkString ", "}]"
          case (i, o) => s"${backRef}_In_${i}_$o[${(InTypes ++ OutTypes) mkString ", "}]"
        }
        acc :+ s"def _$backRef : $clazz[$ns, $backRef] with $ref = ???"

      case (acc, _) => acc
    }

    val optional = option match {
      case Some(tree: Tree) =>
        val thisNS = if (out == 0) s"${ns}_0" else s"${ns}_$out[${OutTypes mkString ", "}]"
        Seq(s"def $ns : ChildRef[$ns] with $thisNS = ???")
      case _                => Nil
    }

    //    val inputMethods = if (out > 0 && in < maxIn) {
    //      val nextIn = (in, out) match {
    //        case (0, o) => s"${ns}_In_1_$out[${(OutTypes.last +: OutTypes) mkString ", "}]"
    //        case (i, o) => s"${ns}_In_${in + 1}_$out[${((InTypes :+ OutTypes.last) ++ OutTypes) mkString ", "}]"
    //      }
    //      Seq(
    //        s"def apply(in: ?.type)    : $nextIn = ???",
    //        s"def <(in: ?.type)        : $nextIn = ???",
    //        s"def contains(in: ?.type) : $nextIn = ???")
    //    } else Nil

    //    val extraMethods = if (out > 0) {
    //      val (nextOut, nextOutInt) = (in, out) match {
    //        case (0, o) => (s"${ns}_$out[${OutTypes mkString ", "}]", s"${ns}_$out[${(OutTypes.init :+ "Int") mkString ", "}]")
    //        case (i, o) => (s"${ns}_In_${i}_$out[${(InTypes ++ OutTypes) mkString ", "}]", s"${ns}_In_${i}_$out[${(InTypes ++ OutTypes.init :+ "Int") mkString ", "}]")
    //      }
    //      Seq(
    //        s"def apply(m: maybe.type) : $nextOut   = ???",
    //        s"def apply(c: count.type) : $nextOutInt = ???")
    //
    //    } else Nil



    (in, out) match {
      // First output trait
      case (0, 0) =>
        val (thisIn, nextIn) = if (maxIn == 0 || in == maxIn) ("Nothing", "Nothing") else (s"${ns}_In_1_0", s"${ns}_In_1_1")
        s"""trait ${ns}_0 extends $ns with Molecule_0[${ns}_0, ${ns}_1, $thisIn, $nextIn] {
           |  ${(attrVals ++ Seq("") ++ attrVals_ ++ refCode ++ optional).mkString("\n  ").trim}
           |}
         """.stripMargin

      // Last output trait
      case (0, o) if o == maxOut =>
        val thisIn = if (maxIn == 0 || in == maxIn) "Nothing" else s"${ns}_In_1_$o"
        val types = OutTypes mkString ", "
        s"""trait ${ns}_$o[$types] extends $ns with Molecule_$o[${ns}_$o, Nothing, $thisIn, Nothing, $types] {
           |  ${(attrVals_ ++ refCode ++ optional).mkString("\n  ").trim}
           |}""".stripMargin

      // Other output traits
      case (0, o) =>
        val (thisIn, nextIn) = if (maxIn == 0 || in == maxIn) ("Nothing", "Nothing") else (s"${ns}_In_1_$o", s"${ns}_In_1_${o + 1}")
        val types = OutTypes mkString ", "
        s"""trait ${ns}_$o[$types] extends $ns with Molecule_$o[${ns}_$o, ${ns}_${o + 1}, $thisIn, $nextIn, $types] {
           |  ${(attrVals ++ Seq("") ++ attrVals_ ++ refCode ++ optional).mkString("\n  ").trim}
           |}
         """.stripMargin

      // First input trait
      case (i, 0) =>
        val s = if (in > 1) "s" else ""
        val types = InTypes mkString ", "
        s"""
           |/********* Input molecules awaiting $i input$s *******************************/
           |
           |trait ${ns}_In_${i}_0[$types] extends $ns with In_${i}_0[$types] {
           |  ${(attrVals ++ Seq("") ++ attrVals_ ++ refCode ++ optional).mkString("\n  ").trim}
           |}
         """.stripMargin

      // Last input trait
      case (i, o) if o == maxOut =>
        val types = (InTypes ++ OutTypes) mkString ", "
        s"""trait ${ns}_In_${i}_$o[$types] extends $ns with In_${i}_$o[$types] {
           |  ${(attrVals_ ++ refCode ++ optional).mkString("\n  ").trim}
           |}""".stripMargin

      // Other input traits
      case (i, o) =>
        val types = (InTypes ++ OutTypes) mkString ", "
        s"""trait ${ns}_In_${i}_$o[$types] extends $ns with In_${i}_$o[$types] {
           |  ${(attrVals ++ Seq("") ++ attrVals_ ++ refCode ++ optional).mkString("\n  ").trim}
           |}
         """.stripMargin
    }
  }

  def namespaceBody(d: Definition, namespace: Namespace) = {
    val inArity = d.in
    val outArity = d.out
    val Ns = namespace.ns
    val attrs = namespace.attrs

    val extension = namespace.opt match {
      case Some(t: Tree)      => "extends Tree "
      case Some(h: HyperEdge) => "extends HyperEdge "
      case None               => ""
    }

    val p1 = (s: String) => padS(attrs.map(_.attr.length).max, s)
    val p2 = (s: String) => padS(attrs.map(_.clazz.length).max, s)
    val attrClasses = attrs.map {
      case Val(attr, _, clazz, _, _, _, options) =>
        val extensions = if (options.isEmpty) "" else " with " + options.filter(_.clazz.nonEmpty).map(_.clazz).mkString(" with ")
        s"class $attr${p1(attr)}[Ns, In] extends $clazz${p2(clazz)}[Ns, In]$extensions"

      case Enum(attr, _, clazz, _, _, enums) =>
        val enumValues = s"private lazy val ${enums.mkString(", ")} = EnumValue "
        s"class $attr${p1(attr)}[Ns, In] extends $clazz${p2(clazz)}[Ns, In] { $enumValues }"

      case Ref(attr, _, clazz, _, _, _, _) =>
        s"class $attr${p1(attr)}[Ns, In] extends $clazz${p2(clazz)}[Ns, In]"

      case BackRef(attr, _, clazz, _, _, _, _) =>
        s"class $attr${p1(attr)}[Ns, In] extends $clazz${p2(clazz)}[Ns, In]"

    }.mkString("\n  ").trim

    val nsTraits = (for {
      in <- 0 to inArity
      out <- 0 to outArity
    } yield nsTrait(namespace, in, out, inArity, outArity)).mkString("\n")

    val inImport = if (inArity > 0) "\nimport molecule.in._" else ""
    val javaImports0 = attrs.collect {
      case Val(_, _, _, tpe, _, _, options) if tpe.take(4) == "java" => tpe
    }.distinct
    val javaImports = if(javaImports0.isEmpty) "" else javaImports0.mkString(s"\nimport ", "\nimport ", "")

    s"""|/*
        | * AUTO-GENERATED CODE - DON'T CHANGE!
        | *
        | * Manual changes to this file will likely break molecules!
        | * Instead, change the molecule definition files and recompile your project with `sbt compile`.
        | */
        |package ${d.pkg}.dsl.${firstLow(d.domain)}
        |import molecule._
        |import molecule.dsl.schemaDSL._$inImport
        |import molecule.out._$javaImports
        |
        |
        |object $Ns extends ${Ns}_0 {
        |  def apply(e: Long)       : ${Ns}_0      = ???
        |  def apply(c: count.type) : ${Ns}_1[Int] = ???
        |}
        |
        |trait $Ns $extension{
        |  $attrClasses
        |}
        |
        |$nsTraits""".stripMargin
  }

  def generate(srcManaged: File, domainDirs: Seq[String]): Seq[File] = {
    // Loop domain directories
    val files = domainDirs flatMap { domainDir =>
      val definitionFiles = IO.listFiles(new File(domainDir) / "schema").filter(f => f.isFile && f.name.endsWith("Definition.scala"))
      assert(definitionFiles.size > 0, "Found no definition files in path: " + domainDir)

      // Loop definition files in each domain directory
      definitionFiles flatMap { definitionFile =>
        val d0 = parse(definitionFile)
        val d = resolve(d0)

        // Write schema file
        val schemaFile: File = d.pkg.split('.').toList.foldLeft(srcManaged)((file, pkg) => file / pkg) / "schema" / s"${d.domain}Schema.scala"
        IO.write(schemaFile, schemaBody(d))

        // Write namespace files
        val namespaceFiles = d.nss.map { ns =>
          val nsFile: File = d.pkg.split('.').toList.foldLeft(srcManaged)((file, pkg) => file / pkg) / "dsl" / firstLow(d.domain) / s"${ns.ns}.scala"
          val nsBody = namespaceBody(d, ns)
          IO.write(nsFile, nsBody)
          nsFile
        }

        schemaFile +: namespaceFiles
      }
    }
    files
  }
}