package sbtmolecule.dsl

import sbtmolecule.ast.schemaModel._

case class NsBase(
  model: Model,
  namespace: Namespace,
  genericPkg: String
) extends Formatting(model, namespace, genericPkg = genericPkg) {

  val tpeMan = List.newBuilder[String]
  val tpeOpt = List.newBuilder[String]

  val propTraitsMan = List.newBuilder[String]
  val propTraitsOpt = List.newBuilder[String]
  val propTraitsMap = List.newBuilder[String]
  val propTraitsRef = List.newBuilder[String]

  attrs.foreach {
    case a: Val =>
      val (ns_attr, ns_attrO, ns_attrK, ns__ref, attr, attrO, attr_, attrK, attrK_, tpe, tpO, baseTpe, ref, refNsPad) = formatted(a)

      val (clsMan, clsOpt) = nsData(a)
      val (opts, optsK)    = getExtras(a, a.bi)

      tpeMan += s"final class $attr [$Stay, $Next] extends $clsMan[$Stay, $Next]$opts"
      tpeOpt += s"final class $attrO[$Stay, $Next] extends $clsOpt[$Stay]$opts"

      propTraitsMan += s"trait $ns_attr  { lazy val $attr: $tpe = ??? }"
      propTraitsOpt += s"trait $ns_attrO { lazy val $attrO: $tpO = ??? }"

      if (a.clazz.startsWith("Map")) {
        val oneCls = "One" + baseTpe
        tpeMan += s"final class $attrK[$Stay, $Next] extends $oneCls [$Stay, $Next]$optsK"
        propTraitsMap += s"trait $ns_attrK { lazy val $attrK: $baseTpe = ??? }"
      }

    case a: Ref =>
      val (ns_attr, ns_attrO, ns_attrK, ns__ref, attr, attrO, attr_, attrK, attrK_, tpe, tpO, baseTpe, ref, refNsPad) = formatted(a)

      val (clsMan, clsOpt) = nsData(a)
      val (opts, optsK)    = getExtras(a, a.bi)

      tpeMan += s"final class $attr [$Stay, $Next] extends $clsMan[$Stay, $Next]$opts"
      tpeOpt += s"final class $attrO[$Stay, $Next] extends $clsOpt[$Stay]$opts"

      propTraitsMan += s"trait $ns_attr  { lazy val $attr: $tpe = ??? }"
      propTraitsOpt += s"trait $ns_attrO { lazy val $attrO: $tpO = ??? }"

      // Add underscore to distinguish it from ref attr (gives case warning)
      propTraitsRef += s"trait $ns__ref[props] { def $ref: props = ??? }"

    case a: Enum =>
      val (ns_attr, ns_attrO, ns_attrK, ns__ref, attr, attrO, attr_, attrK, attrK_, tpe, tpO, baseTpe, ref, refNsPad) = formatted(a)

      val (clsMan, clsOpt) = nsData(a)
      val (opts, optsK)    = getExtras(a, a.bi)

      val enumValues = s"private lazy val ${a.enums.mkString(", ")} = EnumValue"

      tpeMan += s"final class $attr [$Stay, $Next] extends $clsMan[$Stay, $Next]$opts { $enumValues }"
      tpeOpt += s"final class $attrO[$Stay, $Next] extends $clsOpt[$Stay]$opts { $enumValues }"

      propTraitsMan += s"trait $ns_attr  { lazy val $attr: $tpe = ??? }"
      propTraitsOpt += s"trait $ns_attrO { lazy val $attrO: $tpO = ??? }"

    case _ =>
  }

  val tpeClasses = (tpeMan.result() ++ Seq("") ++ tpeOpt.result()).mkString(("\n  "))

  val propTraits = {
    val propTraitsMapResult = propTraitsMap.result()
    val propTraitsRefResult = propTraitsRef.result()
    (
      propTraitsMan.result() ++
        Seq(
          "",
          "// Please note that `$` has been substituted with `_` only to allow packaging to jars.",
          "// To be interpreted as optional"
        ) ++ propTraitsOpt.result() ++
        (if (propTraitsMapResult.nonEmpty) Seq("") ++ propTraitsMapResult else Nil) ++
        (if (propTraitsRefResult.nonEmpty) Seq("") ++ propTraitsRefResult else Nil)
      ).mkString("\n").trim
  }


  val pkg = if (isGeneric) genericPkg else model.pkg + ".dsl"

  val inputEids = if (model.maxIn == 0) "" else
    s"\n  final override def apply(eids: qm)              : ${ns}_1_0_L0[$ns_, Init, Long] = ???"

  val (imports, body) = if (isDatom) {
    (
      Seq(
        "molecule.core.dsl.attributes._",
      ),
      s"""trait $ns {
         |  $tpeClasses
         |}
         |
         |trait $ns_[props] { def $ns: props = ??? }
         |
         |$propTraits
         |""".stripMargin
    )

  } else if (isGeneric) {
    (
      Seq(
        "molecule.core.dsl.attributes._",
        "molecule.core.generic.GenericNs"
      ),
      s"""trait $ns extends GenericNs {
         |  $tpeClasses
         |}
         |
         |trait $ns_[props] { def $ns: props = ??? }
         |
         |$propTraits
         |""".stripMargin
    )

  } else {
    (
      Seq(
        "java.net.URI",
        "java.util.UUID",
        "molecule.core.dsl.attributes._",
        "molecule.core.dsl.base._",
      ) ++ (if (model.maxIn > 0) Seq("molecule.core.expression.AttrExpressions.qm") else Nil),
      s"""object $ns extends ${ns}_0_0_L0[$ns_, Init] with FirstNS {
         |  final override def apply(eid: Long, eids: Long*): ${ns}_0_0_L0[$ns_, Init] = ???
         |  final override def apply(eids: Iterable[Long])  : ${ns}_0_0_L0[$ns_, Init] = ???$inputEids
         |}
         |
         |trait _$ns_ {
         |  $tpeClasses
         |}
         |
         |trait $ns_[props] { def $ns: props = ??? }
         |
         |$propTraits
         |""".stripMargin
    )
  }

  def get: String = Template(ns, pkg, model.domain, body, imports)
}
