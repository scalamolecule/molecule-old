package sbtmolecule.dsl

import sbtmolecule.ast.schemaModel._

case class NsArityLevel(
  model: Model,
  namespace: Namespace,
  in: Int,
  out: Int,
  level: Int,
  genericPkg: String
) extends Formatting(model, namespace, in, out, level, genericPkg) {

  def getRef(r: Ref) = {
    val Ref(_, _, clazz2, _, _, refNs, _, _, _, _)                                                                  = r
    val (ns_attr, ns_attrO, ns_attrK, ns__ref, attr, attrO, attr_, attrK, attrK_, tpe, tpO, baseTpe, ref, refNsPad) = formatted(r)

    val biDirectionals = r match {
      case Ref(_, _, _, _, _, refNs, _, bi, revRef, _) =>
        bi match {
          case Some("BiSelfRef_")     => Seq(s"BiSelfRef_")
          case Some("BiOtherRef_")    => Seq(s"BiOtherRef_ [${refNs}_$revRef]")
          case Some("BiEdgePropRef_") => Seq(s"BiEdgePropRef_")
          case Some("BiEdgeRef_")     => Seq(s"BiEdgeRef_  [${refNs}_$revRef]")
          case Some("BiTargetRef_")   => Seq(s"BiTargetRef_[${refNs}_$revRef]")
          case _                      => Nil
        }

      case _ => Nil
    }

    val cls          = if (clazz2 == "OneRef") "OneRef " else "ManyRef"
    val refNs_       = refNsPad(refNs)
    val refNs_0_0_L1 = refNs + "_" + in + "_" + out + "_L" + (level + 1) + padRefNs(refNs)
    val refNsDef     = refNs_0_0_L1 + "[" + `o0, p0` + s", $ns__ref, Init" + `, I1, A` + "]"
    val nestedDef    = if (clazz2 == "ManyRef" && out < maxOut) {
      val nsTypes = (in to 3).map {
        case i if i == 0 || i <= maxIn => ns + "_" + i + "_" + (out + 1) + "_L" + level
        case _                         => "Nothing"
      }.mkString(", ")
      Seq(s"Nested_${in}_${nn(out)}_L${level + 1}[${`o0, p0`}, $ns__ref${`, I1, A`}, $nsTypes]")
    } else {
      Nil
    }

    val extras    = biDirectionals ++ nestedDef
    val extrasStr = if (extras.isEmpty) "" else extras.mkString(" with ", " with ", "")

    s"final def $ref: $cls[$ns_[p0], $refNs_[p0]] with $refNsDef$extrasStr = ??? "
  }

  def getBackRef(br: BackRef) = {
    val backRefNs         = br.backRefNs
    val sp                = padBackRefs(backRefNs)
    val backRefNsPrefixed = "_" + backRefNs + sp
    val backRef_0_0_L0    = backRefNs + "_" + in + "_" + out + "_L" + (level - 1) + sp
    val concatLast        = level match {
      case 1 => "o0, p0 with o1[p1]"
      case 2 => "o0, p0, o1, p1 with o2[p2]"
      case 3 => "o0, p0, o1, p1, o2, p2 with o3[p3]"
      case 4 => "o0, p0, o1, p1, o2, p2, o3, p3 with o4[p4]"
      case 5 => "o0, p0, o1, p1, o2, p2, o3, p3, o4, p4 with o5[p5]"
      case 6 => "o0, p0, o1, p1, o2, p2, o3, p3, o4, p4, o5, p5 with o6[p6]"
      case 7 => "o0, p0, o1, p1, o2, p2, o3, p3, o4, p4, o5, p5, o6, p6 with o7[p7]"
    }
    s"final def $backRefNsPrefixed: $backRef_0_0_L0[$concatLast${`, I1, A`}] = ???"
  }


  def indexedFirst(opts: Seq[Optional]): Seq[String] = {
    val classes = opts.filter(_.clazz.nonEmpty).map(_.clazz)
    if (classes.contains("Indexed"))
      "Indexed" +: classes.filterNot(_ == "Indexed")
    else
      classes
  }

  val man = List.newBuilder[String]
  val opt = List.newBuilder[String]
  val tac = List.newBuilder[String]

  val manK       = List.newBuilder[String]
  val tacK       = List.newBuilder[String]
  val defRef     = List.newBuilder[String]
  val defBackRef = List.newBuilder[String]

  attrs.foreach {
    case a: Val =>
      val (ns_attr, ns_attrO, ns_attrK, ns__ref, attr, attrO, attr_, attrK, attrK_, tpe, tpO, baseTpe, ref, refNsPad) = formatted(a)
      if (out < maxOut) {
        man += s"final lazy val $attr : $Next[$attr, $ns_attr, $tpe] = ???"
        if (isGeneric) {
          if (isSchema && Seq("doc", "index", "unique", "fulltext", "isComponent", "noHistory", "enumm").contains(a.attr))
            opt += s"final lazy val $attrO : Next[$attrO, $ns_attrO, $tpO] = ???"
        } else {
          opt += s"final lazy val $attrO : $Next[$attrO, $ns_attrO, $tpO] = ???"
        }
      }
      if (isGeneric) {
        if (isDatom || isSchema)
          tac += s"final lazy val $attr_ : $Stay[$attr, $ns_attr, $tpe] = ???"
      } else {
        tac += s"final lazy val $attr_ : $Stay[$attr, $ns_attr, $tpe] = ???"
      }
      if (a.clazz.startsWith("Map")) {
        manK += s"final lazy val $attrK  : String => $Next[$attrK, $ns_attrK, $baseTpe] = ???"
        tacK += s"final lazy val $attrK_ : String => $Stay[$attrK, $ns_attrK, $baseTpe] = ???"
      }

    case a: Ref =>
      val (ns_attr, ns_attrO, ns_attrK, ns__ref, attr, attrO, attr_, attrK, attrK_, tpe, tpO, baseTpe, ref, refNsPad) = formatted(a)
      if (out < maxOut) {
        man += s"final lazy val $attr : $Next[$attr, $ns_attr, $tpe] = ???"
        opt += s"final lazy val $attrO : $Next[$attrO, $ns_attrO, $tpO] = ???"
      }
      tac += s"final lazy val $attr_ : $Stay[$attr, $ns_attr, $tpe] = ???"
      if (level < maxLevel)
        defRef += getRef(a)

    case a: Enum =>
      val (ns_attr, ns_attrO, ns_attrK, ns__ref, attr, attrO, attr_, attrK, attrK_, tpe, tpO, baseTpe, ref, refNsPad) = formatted(a)
      if (out < maxOut) {
        man += s"final lazy val $attr : $Next[$attr, $ns_attr, $tpe] = ???"
        opt += s"final lazy val $attrO : $Next[$attrO, $ns_attrO, $tpO] = ???"
      }
      tac += s"final lazy val $attr_ : $Stay[$attr, $ns_attr, $tpe] = ???"

    case br: BackRef =>
      if (level > 0)
        defBackRef += getBackRef(br)
  }

  // Next/Stay types --------------------------------------

  val next0 = if (out < maxOut) {
    if (isDatom)
      s"$Ns_0_1[${`o0, p0`} with Prop${`, I1, A`}, Tpe]"
    else
      s"$ns_0_1_L0[${`o0, p0`} with Prop${`, I1, A`}, Tpe]"
  } else "Nothing"

  val stay0 = if (isDatom)
    s"$Ns_0_0[${`o0, p0`}          ${`, I1, A`}     ]"
  else
    s"$ns_0_0_L0[${`o0, p0`}          ${`, I1, A`}     ]"

  val next1 = if (maxIn > 0 && in < maxIn && out < maxOut) {
    if (isDatom)
      s"$Ns_1_1[${`o0, p0`} with Prop${`, I1`}, Tpe${`, A`}, Tpe]"
    else
      s"$ns_1_1_L0[${`o0, p0`} with Prop${`, I1`}, Tpe${`, A`}, Tpe]"
  } else "Nothing"

  val stay1 = if (maxIn > 0 && in < maxIn) {
    if (isDatom)
      s"$Ns_1_0[${`o0, p0`}          ${`, I1`}, Tpe${`, A`}     ]"
    else
      s"$ns_1_0_L0[${`o0, p0`}          ${`, I1`}, Tpe${`, A`}     ]"
  } else "Nothing"

  val typeNext = if (out == maxOut) "" else
    s"type $Next[Attr[_, _], Prop, Tpe] = Attr[$next0, $next1] with $next0"

  val typeStay =
    s"type $Stay[Attr[_, _], Prop, Tpe] = Attr[$stay0, $stay1] with $stay0"


  // Body --------------------------------------

  val selfJoinDef = s"final def Self: $ns_0_0_L1[${`o0, p0`}, $ns_, Init${`, I1, A`}] with SelfJoin = ???"

  val datomNs = s"Datom_${in}_${out}_L$level[${`o0, p0`}${`, I1, A`}, $ns_0_0_L0, $ns_0_1_L0, $ns_1_0_L0, $ns_1_1_L0]"
  val baseNs  = ns_0_0 + "[" + `o0, p0 with o1[p1]` + `, I1, A` + "]"
  val spAggr  = if (out < 10) "  " else "   "
  val aggrNs  = if (out < maxOut) s"\n  with Aggr_${nn(in + out)}_L$level$spAggr[${`o0, p0`}${`, I1, A`}, ${ns}_${in}_${out}_L$level]" else ""

  val manKResult       = manK.result()
  val tacKResult       = tacK.result()
  val defRefResult     = defRef.result()
  val defBackRefResult = defBackRef.result()

  val attrsNext: Seq[String] = if (out < maxOut) {
    man.result() ++ Seq("") ++
      (if (manKResult.nonEmpty) manKResult ++ Seq("") else Nil) ++
      opt.result() ++ Seq("")
  } else Nil
  val attrKStay: Seq[String] = if (tacKResult.nonEmpty) Seq("") ++ tacKResult else Nil
  val refs     : Seq[String] = if (defRefResult.nonEmpty) Seq("") ++ defRefResult else Nil
  val backRefs : Seq[String] = if (defBackRefResult.nonEmpty) Seq("") ++ defBackRefResult else Nil
  val selfJoin : Seq[String] = if (out > 0 && level < 7 && !isGeneric) Seq("", selfJoinDef) else Nil

  val body = (attrsNext ++ tac.result() ++ attrKStay ++ refs ++ backRefs ++ selfJoin).mkString("\n  ").trim


  def get: String = if (isDatom)
    s"""trait $ns_0_0_L0[${`o0[_], p0`}${`, I1, A`}, $Ns_0_0_, $Ns_0_1_, $Ns_1_0_, $Ns_1_1_] extends $baseNs {
       |  $typeNext
       |  $typeStay
       |
       |  $body
       |}
       |""".stripMargin

  else if (isGeneric)
    s"""trait $ns_0_0_L0[${`o0[_], p0`}${`, I1, A`}] extends $baseNs {
       |  $typeNext
       |  $typeStay
       |
       |  $body
       |}
       |""".stripMargin

  else
    s"""trait $ns_0_0_L0[${`o0[_], p0`}${`, I1, A`}] extends $baseNs
       |  with $datomNs$aggrNs
       |{
       |  $typeNext
       |  $typeStay
       |
       |  $body
       |}
       |""".stripMargin
}
