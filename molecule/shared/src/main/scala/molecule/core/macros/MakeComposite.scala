package molecule.core.macros

import molecule.core.macros.attrResolverTrees.LambdaCastAggr
import molecule.core.ops.{Liftables, TreeOps}
import molecule.core.transform.Dsl2Model
import molecule.datomic.base.transform.Model2Query
import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox

/** Macro to make composite molecules. */
class MakeComposite(val c: blackbox.Context) extends Base {

  import c.universe._

  //    private lazy val xx = InspectMacro("MakeComposite", 1, 9, mkError = true)
//    private lazy val xx = InspectMacro("MakeComposite", 1, 9)
  private lazy val xx = InspectMacro("MakeComposite", 9, 8)


  private[this] final def generateCompositeMolecule(dsl: Tree, ObjType: Type, TplTypes: Type*): Tree = {
    val (
      genericImports, model0,
      typess, castss,
      obj, indexes,
      nestedRefs, hasVariables, txMetaCompositesCount,
      postTypes, postCasts, postJsons,
      isNestedOpt,
      nestedOptRefIndexes, nestedOptTacitIndexes
      )                = getModel(dsl)
    val imports        = getImports(genericImports)
    val OutMoleculeTpe = molecule_o(TplTypes.size)
    val outMolecule    = TypeName(c.freshName("compositeOutMolecule$"))

    val transformers = if (isJsPlatform) {
      q"""
          final override protected def packed2tpl(vs: Iterator[String]): (..$TplTypes) =
            ${packed2tpl(typess, postTypes, indexes, true, txMetaCompositesCount)}

          final override protected def packed2obj(vs: Iterator[String]): $ObjType = ???
          final override protected def packed2json(vs: Iterator[String]): String = ???

          final override lazy val indexes: Indexes = $indexes
       """
    } else {
      q"""
          final override def row2tpl(row: jList[AnyRef]): (..$TplTypes) = ${tplComposite(castss, txMetaCompositesCount)}
          final override def row2obj(row: jList[AnyRef]): $ObjType = ${objFlat(obj)._1}
          final override def row2json(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ${jsonFlat(obj)._1}
        """
    }

    val t = if (hasVariables) {
      q"""
        ..$imports
        private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
        final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel)) {
          ..$transformers
        }
        new $outMolecule
      """
    } else {
      q"""
        ..$imports
        final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)}) {
          ..$transformers
        }
        new $outMolecule
      """
    }
    xx(6
      , model0
      , t
      , packed2tpl(typess, postTypes, indexes, true, txMetaCompositesCount)
      , tplComposite(castss, txMetaCompositesCount)
    )
    t
  }

  // Composite molecules ....................................................

  final def from01tuples[Obj: W, T1: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1])
  final def from02tuples[Obj: W, T1: W, T2: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2])
  final def from03tuples[Obj: W, T1: W, T2: W, T3: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3])
  final def from04tuples[Obj: W, T1: W, T2: W, T3: W, T4: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4])
  final def from05tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5])
  final def from06tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6])
  final def from07tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7])
  final def from08tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8])
  final def from09tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9])
  final def from10tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10])
  final def from11tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11])
  final def from12tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12])
  final def from13tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13])
  final def from14tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14])
  final def from15tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15])
  final def from16tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16])
  final def from17tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17])
  final def from18tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18])
  final def from19tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19])
  final def from20tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20])
  final def from21tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W, T21: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20], w[T21])
  final def from22tuples[Obj: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W, T21: W, T22: W](dsl: Tree): Tree = generateCompositeMolecule(dsl, w[Obj], w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20], w[T21], w[T22])
}