package molecule.core.macros

import molecule.datomic.base.transform.Model2Query
import scala.language.higherKinds
import scala.reflect.macros.blackbox

class MakeMolecule(val c: blackbox.Context) extends MakeBase {

  import c.universe._

//  private lazy val xy = InspectMacro("MakeMolecule", 6, mkError = true)
  private lazy val xx = InspectMacro("MakeMolecule", 60)


  private[this] final def generateMolecule(dsl: Tree, ObjType: Type, OutTypes: Type*): Tree = {
    val (
      genericImports, model,
      typess, castss,
      obj,
      nestedRefs, hasVariables, txMetas,
      postJsons,
      isOptNested,
      refIndexes, tacitIndexes,
      doSort
      ) = getModel(dsl)

    val imports         = getImports(genericImports)
    val OutMoleculeTpe  = molecule_o(OutTypes.size)
    val outMoleculeName = c.freshName("outMolecule$")
    val outMolecule     = TypeName(outMoleculeName)
    lazy val levels = castss.size - txMetas
    lazy val jsTpl  = Some(if (OutTypes.length == 1) q"Tuple1(packed2tpl(vs))" else q"packed2tpl(vs)")
    lazy val jvmTpl = Some(if (OutTypes.length == 1) q"Tuple1(row2tpl(row))" else q"row2tpl(row)")


    def mkFlat = {
      val transformers = if (isJsPlatform) {
        q"""
          final override def packed2tpl(vs: Iterator[String]): (..$OutTypes) = ${packed2tplFlat(obj, txMetas)}
          final override def packed2obj(vs: Iterator[String]): $ObjType = ${objTree(obj, jsTpl)}
          final override def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ${packed2jsonFlat(obj, txMetas)}
          final override def obj: nodes.Obj = $obj
         """
      } else {
        q"""
          final override def row2tpl(row: jList[AnyRef]): (..$OutTypes) = ${tplFlat(castss, txMetas)}
          final override def row2obj(row: jList[AnyRef]): $ObjType = ${objTree(obj)}
          final override def row2json(row: jList[AnyRef], sb: StringBuffer): StringBuffer = ${jsonFlat(obj)}
        """
      }


      if (hasVariables) {
        val identifiers = mapIdentifiers(model.elements).toMap
        q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model, $identifiers)
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes](_resolvedModel, Model2Query(_resolvedModel)) {
            ..$transformers
            ..${compare(model, doSort)}
          }
        """
      } else {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes]($model, ${Model2Query(model)}) {
            ..$transformers
            ..${compare(model, doSort)}
          }
        """
      }
    }


    def mkOptNested = {
      val transformers = if (isJsPlatform) {
        q"""
          final override def packed2tpl(vs: Iterator[String]): (..$OutTypes) = ${packed2tplNested(typess, obj, txMetas)}
          final override def packed2obj(vs: Iterator[String]): $ObjType = ${objTree(obj, jsTpl)}
          final override def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ${packed2jsonNested(levels, obj, txMetas)}
          final override def obj: nodes.Obj = $obj
          final override def isOptNested: Boolean = true
          final override def refIndexes  : List[List[Int]] = $refIndexes
          final override def tacitIndexes: List[List[Int]] = $tacitIndexes
        """
      } else {
        val (compareImpl, orderings) = compareOptNested(model, doSort)
        q"""
          final override def row2tpl(row: jList[AnyRef]): (..$OutTypes) =
            ${tplOptNested(obj, refIndexes, tacitIndexes, orderings = orderings)}.asInstanceOf[(..$OutTypes)]

          final override def row2obj(row: jList[AnyRef]): $ObjType = ${objTree(obj, jvmTpl)}

          final override def row2json(row: jList[AnyRef], sb: StringBuffer): StringBuffer =
            ${jsonOptNested(obj, refIndexes, tacitIndexes)}

          ..$compareImpl
        """
      }

      if (hasVariables) {
        val identifiers = mapIdentifiers(model.elements).toMap
        q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model, $identifiers)
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes](_resolvedModel, Model2Query(_resolvedModel)) {
            ..$transformers
          }
        """
      } else {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes]($model, ${Model2Query(model)}) {
            ..$transformers
          }
        """
      }
    }


    def mkNested = if (isJsPlatform) {
      val transformers =
        q"""
          final override def packed2tpl(vs: Iterator[String]): (..$OutTypes) = ${packed2tplNested(typess, obj, txMetas)}
          final override def packed2obj(vs: Iterator[String]): $ObjType = ${objTree(obj, jsTpl)}
          final override def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ${packed2jsonNested(levels, obj, txMetas)}
          final override def obj: nodes.Obj = $obj
          final override def nestedLevels: Int = ${levels - 1}
        """
      if (hasVariables) {
        val identifiers = mapIdentifiers(model.elements).toMap
        q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model, $identifiers)
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes](_resolvedModel, Model2Query(_resolvedModel)) {
            ..$transformers
          }
        """
      } else {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes]($model, ${Model2Query(model)}) {
            ..$transformers
          }
        """
      }

    } else {

      val nestedTupleClass = tq"${nestedTupleClassX(levels)}"
      val nestedJsonClass  = tq"${nestedJsonClassX(levels)}"
      val tpl              = Some(if (OutTypes.length == 1) q"Tuple1(tpl0)" else q"tpl0") // todo: ever only 1 when nested?
      val transformers     =
        q"""
          ..${buildTplNested(castss, typess, OutTypes, txMetas).get}
          ..${buildJsonNested(obj, nestedRefs, txMetas, postJsons).get}
          final override def outerTpl2obj(tpl0: (..$OutTypes)): $ObjType = ${objTree(obj, tpl)}
          final override def nestedLevels: Int = ${levels - 1}
          ..${compareNested(model, levels, doSort)}
         """

      if (hasVariables) {
        val identifiers = mapIdentifiers(model.elements).toMap
        q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model, $identifiers)
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes](_resolvedModel, Model2Query(_resolvedModel))
            with $nestedTupleClass[$ObjType, (..$OutTypes)]
            with $nestedJsonClass[$ObjType, (..$OutTypes)] {
            ..$transformers
          }
        """
      } else {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes]($model, ${Model2Query(model)})
            with $nestedTupleClass[$ObjType, (..$OutTypes)]
            with $nestedJsonClass[$ObjType, (..$OutTypes)] {
            ..$transformers
          }
        """
      }
    }

    val outMoleculeClass = if (levels == 1)
      mkFlat
    else if (isOptNested)
      mkOptNested
    else
      mkNested

    val tree =
      q"""
        {
          ..$imports
          ..$outMoleculeClass
          new $outMolecule
        }
      """

    xx(6, levels, obj, tree)
    tree
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
