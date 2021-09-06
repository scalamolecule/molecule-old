package molecule.core.macros

import molecule.datomic.base.transform.Model2Query
import scala.language.higherKinds // necessary for scala 2.12
import scala.reflect.macros.blackbox

class MakeMolecule(val c: blackbox.Context) extends Base {

  import c.universe._

  //    private lazy val xx = InspectMacro("MakeMolecule", 1, 8, mkError = true)
  //      private lazy val xx = InspectMacro("MakeMolecule", 2, 8)
  private lazy val xx = InspectMacro("MakeMolecule", 9, 7)


  private[this] final def generateMolecule(dsl: Tree, ObjType: Type, TplTypes: Type*): Tree = {
    val (
      genericImports, model0,
      typess, castss,
      obj,
      nestedRefs, hasVariables, txMetas,
      postJsons,
      isOptNested,
      refIndexes, tacitIndexes
      ) = getModel(dsl)

    val imports              = getImports(genericImports)
    val OutMoleculeTpe: Tree = molecule_o(TplTypes.size)
    val outMolecule          = TypeName(c.freshName("outMolecule$"))
    lazy val levels = castss.size - txMetas
    lazy val jsTpl  = Some(if (TplTypes.length == 1) q"Tuple1(packed2tpl(vs))" else q"packed2tpl(vs)")
    lazy val jvmTpl = Some(if (TplTypes.length == 1) q"Tuple1(row2tpl(row))" else q"row2tpl(row)")


    def mkFlat = {
      val transformers = if (isJsPlatform) {
        q"""
          final override def packed2tpl(vs: Iterator[String]): (..$TplTypes) = ${packed2tplFlat(obj, txMetas)}
          final override def packed2obj(vs: Iterator[String]): $ObjType = ${objTree(obj, jsTpl)}
          final override def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ${packed2jsonFlat(obj, txMetas)}

          final override lazy val obj: nodes.Obj = $obj
         """
      } else {
        q"""
          final override def row2tpl(row: jList[AnyRef]): (..$TplTypes) = ${tplFlat(castss, txMetas)}
          final override def row2obj(row: jList[AnyRef]): $ObjType = ${objTree(obj)}
          final override def row2json(row: jList[AnyRef], sb: StringBuffer): StringBuffer = ${jsonFlat(obj)}
        """
      }

      if (hasVariables) {
        q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel)) {
            ..$transformers
          }
        """
      } else {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)}) {
            ..$transformers
          }
        """
      }
    }


    def mkOptNested = {
      val transformers = if (isJsPlatform) {
        q"""
          final override def packed2tpl(vs: Iterator[String]): (..$TplTypes) = ${packed2tplNested(typess, obj, txMetas)}
          final override def packed2obj(vs: Iterator[String]): $ObjType = ${objTree(obj, jsTpl)}
          final override def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ???

          final override lazy val obj: nodes.Obj = $obj
          final override lazy val isOptNested: Boolean = true
          final override lazy val refIndexes  : List[List[Int]] = $refIndexes
          final override lazy val tacitIndexes: List[List[Int]] = $tacitIndexes
        """
      } else {
        q"""
          final override def row2tpl(row: jList[AnyRef]): (..$TplTypes) =
            ${tplOptNested(obj, refIndexes, tacitIndexes)}.asInstanceOf[(..$TplTypes)]

          final override def row2obj(row: jList[AnyRef]): $ObjType = ${objTree(obj, jvmTpl)}

          final override def row2json(row: jList[AnyRef], sb: StringBuffer): StringBuffer =
            ${jsonOptNested(obj, refIndexes, tacitIndexes)}
        """
      }
      if (hasVariables) {
        q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel)) {
            ..$transformers
          }
        """
      } else {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)}) {
            ..$transformers
          }
        """
      }
    }


    def mkNested = if (isJsPlatform) {
      val transformers =
        q"""
          final override def packed2tpl(vs: Iterator[String]): (..$TplTypes) = ${packed2tplNested(typess, obj, txMetas)}
          final override def packed2obj(vs: Iterator[String]): $ObjType = ${objTree(obj, jsTpl)}
          final override def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ???

          final override lazy val obj: nodes.Obj = $obj
          final override lazy val nestedLevels: Int = ${levels - 1}
        """
      if (hasVariables) {
        q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel)) {
            ..$transformers
          }
        """
      } else {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)}) {
            ..$transformers
          }
        """
      }

    } else {
      // jvm platform

      val nestedTupleClass = tq"${nestedTupleClassX(levels)}"
      val nestedJsonClass  = tq"${nestedJsonClassX(levels)}"
      val tpl              = Some(if (TplTypes.length == 1) q"Tuple1(tpl0)" else q"tpl0") // todo: ever only 1 when nested?
      val transformers     =
        q"""
          ..${buildTplNested(castss, typess, TplTypes, txMetas).get}
          ..${buildJsonNested(obj, nestedRefs, txMetas, postJsons).get}
          final override def outerTpl2obj(tpl0: (..$TplTypes)): $ObjType = ${objTree(obj, tpl)}
         """

      if (hasVariables) {
        q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel))
            with $nestedTupleClass[$ObjType, (..$TplTypes)]
            with $nestedJsonClass[$ObjType, (..$TplTypes)] {
            ..$transformers
          }
        """
      } else {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)})
            with $nestedTupleClass[$ObjType, (..$TplTypes)]
            with $nestedJsonClass[$ObjType, (..$TplTypes)] {
            ..$transformers
          }
        """
      }
    }


    //    val moleculeClass = if (levels == 1 || txMetas > 0)
    val moleculeClass = if (levels == 1)
      mkFlat
    else if (isOptNested)
      mkOptNested
    else
      mkNested

    val t =
      q"""
        {
          ..$imports
          ..$moleculeClass
          new $outMolecule
        }
      """

    xx(7
      , obj
      //      , indexes
      , t
      //      , model0
      //      , Model2Query(model0)._1
      //      , Model2Query(model0)._2
      //      , Model2Query(model0)._3
      //      , Model2Query(model0)._4
      //      , obj
      //      , jsonss
    )
    t
  }

  final def from01attr[Obj: W, A: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A])
  final def from02attr[Obj: W, A: W, B: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B])
  final def from03attr[Obj: W, A: W, B: W, C: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C])
  final def from04attr[Obj: W, A: W, B: W, C: W, D: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D])
  final def from05attr[Obj: W, A: W, B: W, C: W, D: W, E: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E])
  final def from06attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F])
  final def from07attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G])
  final def from08attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H])
  final def from09attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I])
  final def from10attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J])
  final def from11attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K])
  final def from12attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L])
  final def from13attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M])
  final def from14attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N])
  final def from15attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O])
  final def from16attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P])
  final def from17attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q])
  final def from18attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R])
  final def from19attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S])
  final def from20attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T])
  final def from21attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W, U: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T], w[U])
  final def from22attr[Obj: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W, U: W, V: W](dsl: Tree): Tree = generateMolecule(dsl, w[Obj], w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T], w[U], w[V])
}
