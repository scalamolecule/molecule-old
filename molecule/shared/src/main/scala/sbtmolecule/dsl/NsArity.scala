package sbtmolecule.dsl

import sbtmolecule.ast.schemaModel._

case class NsArity(
  model: Model,
  namespace: Namespace,
  in: Int,
  out: Int,
  genericPkg: String
) extends Formatting(model, namespace, in, out, genericPkg = genericPkg) {

  val pkg           = if (isGeneric) genericPkg else model.pkg + ".dsl"
  val NS            = s"NS_${in}_${nn(out)}[o0, p0${`, I1, A`}]"
  val NSsp          = s"NS_${in}_${nn(out)}             [o0, p0${`, I1, A`}]"
  val txNs          = s"Tx_${in}_${nn(out)}             [o0, p0${`, I1, A`}]"
  val compositeInit = s"CompositeInit_${in}_${nn(out)}  [o0, p0${`, I1, A`}]"

  // Collect traits for each level for this namespace/arity
  val levelTraits = (0 to maxLevel).map(level =>
    NsArityLevel(model, namespace, in, out, level, genericPkg).get
  ).mkString("\n\n")

  val (imports, body) = if (isGeneric) {
    (
      Seq(
        "molecule.core.dsl.base._"
      ),
      s"""trait $ns_0_0[o0[_], p0${`, I1, A`}] extends $ns with $NS
         |
         |$levelTraits
     """.stripMargin
    )

  } else {
    val nested = if (out < 22)
      Seq(s"molecule.core.composition.nested_$in.nested_${in}_${nn(out)}._")
    else Nil
    (
      Seq(
        "java.net.URI",
        "java.util.UUID",
        s"molecule.core.composition.CompositeInit_$in.CompositeInit_${in}_${nn(out)}",
        s"molecule.core.composition.Tx_$in.Tx_${in}_${nn(out)}",
        "molecule.core.dsl.attributes._",
        "molecule.core.dsl.base._",
        s"molecule.core.expression.aggregates.aggr_${nn(in + out)}._",
        "molecule.core.generic.Datom._",
      ) ++ nested,
      s"""trait $ns_0_0[o0[_], p0${`, I1, A`}] extends _$ns_
         |  with $NSsp
         |  with $compositeInit
         |  with $txNs
         |
         |$levelTraits
     """.stripMargin)
  }

  def get: String = Template(ns, pkg, model.domain, body, imports)
}
