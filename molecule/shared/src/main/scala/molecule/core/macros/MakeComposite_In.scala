package molecule.core.macros

import molecule.core.ops.{Liftables, TreeOps}
import molecule.core.transform.Dsl2Model
import molecule.datomic.base.transform.Model2Query
import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox

/** Macro to make composite input molecules. */
class MakeComposite_In(val c: blackbox.Context) extends MakeBase {

  import c.universe._

  //   private lazy val xx = InspectMacro("MakeComposite_In", 9, mkError = true)
  private lazy val xx = InspectMacro("MakeComposite_In", 90)

  private[this] final def generateComposite_In_Molecule(dsl: Tree, ObjType: Type, InTypes: Type*)(OutTypes: Type*): Tree = {
    val (genericImports, model0, _, castss, obj, _, hasVariables, txMetas, _, _, _, _) = getModel(dsl)

    val imports          = getImports(genericImports)
    val InputMoleculeTpe = inputMolecule_i_o(InTypes.size, OutTypes.size)
    val OutMoleculeTpe   = molecule_o(OutTypes.size)
    val inputMolecule    = TypeName(c.freshName("compositeInputMolecule$"))
    val outMolecule      = TypeName(c.freshName("compositeOutMolecule$"))


    def getApplyValues(outMoleculeClass: Tree) = {
      q"""
        def outMoleculeValues(args0: Either[Throwable, Seq[(..$InTypes)]])
                             (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$OutTypes] = {
          val queryData: (Query, String, Option[Throwable]) = args0 match {
            case Right(args) =>
              bindValues(_query, args) match {
                case Right(boundQuery) => (boundQuery, Query2String(boundQuery).multiLine(60), None)
                case Left(exc)         => (_query, "", Some(exc))
              }
            case Left(exc)   => (_query, "", Some(exc))
          }
          $outMoleculeClass
          new $outMolecule
        }
      """
    }

    def getApplySeq(outMoleculeClass: Tree) = InTypes match {
      case Seq(it1) => EmptyTree // no extra

      case Seq(it1, it2) =>
        q"""
          def outMoleculeSeqs(args0: Either[Throwable, (Seq[$it1], Seq[$it2])])
                             (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$OutTypes] = {
            val queryData: (Query, String, Option[Throwable]) = args0 match {
              case Right(args) =>
                bindSeqs(_query, args._1, args._2) match {
                  case Right(boundQuery) => (boundQuery, Query2String(boundQuery).multiLine(60), None)
                  case Left(exc)         => (_query, "", Some(exc))
                }
              case Left(exc)   => (_query, "", Some(exc))
            }
            $outMoleculeClass
            new $outMolecule
          }
        """

      case Seq(it1, it2, it3) =>
        q"""
          def outMoleculeSeqs(args0: Either[Throwable, (Seq[$it1], Seq[$it2], Seq[$it3])])
                             (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$OutTypes] = {
            val queryData: (Query, String, Option[Throwable]) = args0 match {
              case Right(args) =>
                bindSeqs(_query, args._1, args._2, args._3) match {
                  case Right(boundQuery) => (boundQuery, Query2String(boundQuery).multiLine(60), None)
                  case Left(exc)         => (_query, "", Some(exc))
                }
              case Left(exc)   => (_query, "", Some(exc))
            }
            $outMoleculeClass
            new $outMolecule
          }
        """
    }

    val outMoleculeClass = if (isJsPlatform) {
      val jsTpl = Some(if (OutTypes.length == 1) q"Tuple1(packed2tpl(vs))" else q"packed2tpl(vs)")
      q"""
        final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes](_model, queryData) {
          final override def packed2tpl(vs: Iterator[String]): (..$OutTypes) = ${packed2tplComposite(obj, txMetas)}
          final override def packed2obj(vs: Iterator[String]): $ObjType = ${objTree(obj, jsTpl)}
          final override def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ${packed2jsonFlat(obj, txMetas)}
          final override def obj: nodes.Obj = $obj
        }
      """
    } else {
      val casts = if (txMetas == 0) {
        q"(..${compositeCasts(castss)})"
      } else {
        val ordinaryComposites = castss.take(castss.length - txMetas)
        val txMetaComposites   = castss.takeRight(txMetas)
        val firstComposites    = ordinaryComposites.init
        val lastComposite      = ordinaryComposites.last
        val lastOffset         = firstComposites.flatten.length
        val metaOffset         = ordinaryComposites.flatten.length
        val first              = compositeCasts(firstComposites)
        val txCasts            = compositeCasts(txMetaComposites, metaOffset)
        val last               = topLevel(List(lastComposite), lastOffset) ++ txCasts
        (first, last) match {
          case (Nil, last)   => q"(..$last)"
          case (first, Nil)  => q"(..$first)"
          case (first, last) => q"(..$first, (..$last))"
        }
      }
      q"""
        final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes](_model, queryData) {
          final override def row2tpl(row: jList[AnyRef]): (..$OutTypes) = $casts
          final override def row2obj(row: jList[AnyRef]): $ObjType = ${objTree(obj)}
          final override def row2json(row: jList[AnyRef], sb: StringBuffer): StringBuffer = ${jsonFlat(obj)}
        }
      """
    }

    val inputMoleculeClass = if (hasVariables) {
      val identifiers = mapIdentifiers(model0.elements).toMap
      q"""
        private val _resolvedModel: Model = resolveIdentifiers($model0, $identifiers)
        final class $inputMolecule extends $InputMoleculeTpe[$ObjType, ..$InTypes, ..$OutTypes](
          _resolvedModel, Model2Query(_resolvedModel)
        ) {
          val isJsPlatform = $isJsPlatform
          ${getApplyValues(outMoleculeClass)}
          ${getApplySeq(outMoleculeClass)}
        }
      """
    } else {
      q"""
        final class $inputMolecule extends $InputMoleculeTpe[$ObjType, ..$InTypes, ..$OutTypes](
          $model0, ${Model2Query(model0)}
        ) {
          val isJsPlatform = $isJsPlatform
          ${getApplyValues(outMoleculeClass)}
          ${getApplySeq(outMoleculeClass)}
        }
      """
    }

    val tree =
      q"""
        ..$imports
        ..$inputMoleculeClass
        new $inputMolecule
      """
    xx(9, tree)
    tree
  }


  final def await_1_01[Obj: W, I1: W, T1: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1])
  final def await_1_02[Obj: W, I1: W, T1: W, T2: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2])
  final def await_1_03[Obj: W, I1: W, T1: W, T2: W, T3: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3])
  final def await_1_04[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4])
  final def await_1_05[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5])
  final def await_1_06[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6])
  final def await_1_07[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7])
  final def await_1_08[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8])
  final def await_1_09[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9])
  final def await_1_10[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10])
  final def await_1_11[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11])
  final def await_1_12[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12])
  final def await_1_13[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13])
  final def await_1_14[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14])
  final def await_1_15[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15])
  final def await_1_16[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16])
  final def await_1_17[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17])
  final def await_1_18[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18])
  final def await_1_19[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19])
  final def await_1_20[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20])
  final def await_1_21[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W, T21: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20], w[T21])
  final def await_1_22[Obj: W, I1: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W, T21: W, T22: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20], w[T21], w[T22])

  final def await_2_01[Obj: W, I1: W, I2: W, T1: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1])
  final def await_2_02[Obj: W, I1: W, I2: W, T1: W, T2: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2])
  final def await_2_03[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3])
  final def await_2_04[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4])
  final def await_2_05[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5])
  final def await_2_06[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6])
  final def await_2_07[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7])
  final def await_2_08[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8])
  final def await_2_09[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9])
  final def await_2_10[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10])
  final def await_2_11[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11])
  final def await_2_12[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12])
  final def await_2_13[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13])
  final def await_2_14[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14])
  final def await_2_15[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15])
  final def await_2_16[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16])
  final def await_2_17[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17])
  final def await_2_18[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18])
  final def await_2_19[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19])
  final def await_2_20[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20])
  final def await_2_21[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W, T21: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20], w[T21])
  final def await_2_22[Obj: W, I1: W, I2: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W, T21: W, T22: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20], w[T21], w[T22])

  final def await_3_01[Obj: W, I1: W, I2: W, I3: W, T1: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1])
  final def await_3_02[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2])
  final def await_3_03[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3])
  final def await_3_04[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4])
  final def await_3_05[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5])
  final def await_3_06[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6])
  final def await_3_07[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7])
  final def await_3_08[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8])
  final def await_3_09[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9])
  final def await_3_10[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10])
  final def await_3_11[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11])
  final def await_3_12[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12])
  final def await_3_13[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13])
  final def await_3_14[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14])
  final def await_3_15[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15])
  final def await_3_16[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16])
  final def await_3_17[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17])
  final def await_3_18[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18])
  final def await_3_19[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19])
  final def await_3_20[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20])
  final def await_3_21[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W, T21: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20], w[T21])
  final def await_3_22[Obj: W, I1: W, I2: W, I3: W, T1: W, T2: W, T3: W, T4: W, T5: W, T6: W, T7: W, T8: W, T9: W, T10: W, T11: W, T12: W, T13: W, T14: W, T15: W, T16: W, T17: W, T18: W, T19: W, T20: W, T21: W, T22: W](dsl: Tree): Tree = generateComposite_In_Molecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[T1], w[T2], w[T3], w[T4], w[T5], w[T6], w[T7], w[T8], w[T9], w[T10], w[T11], w[T12], w[T13], w[T14], w[T15], w[T16], w[T17], w[T18], w[T19], w[T20], w[T21], w[T22])
}