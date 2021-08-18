package molecule.core.macros

import molecule.datomic.base.transform.Model2Query
import scala.language.higherKinds // necessary for scala 2.12
import scala.reflect.macros.blackbox

class MakeMolecule(val c: blackbox.Context) extends Base {

  import c.universe._

  //     private lazy val xx = InspectMacro("MakeMolecule", 1, 8, mkError = true)
  private lazy val xx = InspectMacro("MakeMolecule", 2, 8)
//    private lazy val xx = InspectMacro("MakeMolecule", 9, 7)


  private[this] final def generateMolecule(dsl: Tree, ObjType: Type, TplTypes: Type*): Tree = {
    val (
      genericImports, model0,
      typess, castss,
      obj, indexes,
      nestedRefs, hasVariables, txMetaCompositesCount,
      postTypes, postCasts, postJsons,
      isNestedOpt,
      nestedOptRefIndexes, nestedOptTacitIndexes
      ) = getModel(dsl)

    val imports              = getImports(genericImports)
    val OutMoleculeTpe: Tree = molecule_o(TplTypes.size)
    val outMolecule          = TypeName(c.freshName("outMolecule$"))
    lazy val levels               = castss.size

    def mkFlat = {
      val transformers = if (isJsPlatform) {
        //        val (indexes, arrays, lookups0) = resolveIndexesOLD(flatIndexes0, 0)
        val (arrays, lookups0) = resolveIndexes(indexes, 0)

        val lookups = if (txMetaCompositesCount > 0) {
          // Treat tx meta data as composite
          val first = topLevelLookups(List(castss.head), lookups0)
          val last  = compositeLookups(castss.tail, lookups0, castss.head.length)
          q"(..$first, ..$last)"
        } else {
          q"(..$lookups0)"
        }
        q"""
          final override def qr2tpl(qr: QueryResult): Int => (..$TplTypes) = {
            ..$arrays
            (i: Int) => $lookups
          }
          final override def qr2obj(qr: QueryResult): Int => $ObjType = ???
          final override lazy val indexes: Indexes = $indexes
        """
        //              q"""
        //                final override protected def packed2tpl(lines: Iterable[String]): (..$TplTypes) = ???
        //                final override protected def packed2obj(lines: Iterable[String]): $ObjType = ???
        //                final override protected def packed2json(lines: Iterable[String]): String = ???
        //               """

      } else {
        q"""
          final override def row2tpl(row: jList[AnyRef]): (..$TplTypes) = ${tplFlat(castss, txMetaCompositesCount)}
          final override def row2obj(row: jList[AnyRef]): $ObjType = ${objFlat(obj)._1}
          final override def row2json(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ${jsonFlat(obj)._1}
        """
      }

      //      val transformersX =
      //        q"""
      //          final override def row2tpl(row: jList[AnyRef]): (..$TplTypes) = ${tplFlat(castss, txMetaCompositesCount)}
      //          final override def row2obj(row: jList[AnyRef]): $ObjType = ${objFlat(obj)._1}
      //          final override def row2json(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ${jsonFlat(obj)._1}
      //        """

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


    def mkNestedOpt = {
      if (isJsPlatform) {
        val (arrays, lookups) = resolveIndexes(indexes, levels)

        val setters        = lookups.map(lookup => q"list.add($lookup.asInstanceOf[Any])")
        val jsTransformers =
//          q"""
//          final override def qr2list(qr: QueryResult): Int => jList[Any] = {
//            ..$arrays
//            (i: Int) => {
//              val list = new java.util.ArrayList[Any](${setters.length})
//              ..$setters
//              list
//            }
//          }
//          final override def qr2obj(qr: QueryResult): Int => $ObjType = ???
//
//          final override def packed2tpl(vs: Iterator[String]): (..$TplTypes) = ${packed2tpl(typess, postTypes, indexes)}
//          final override def packed2obj(vs: Iterator[String]): $ObjType = ???
//          final override def packed2json(vs: Iterator[String]): String = ???
//
//          final override lazy val indexes: Indexes = $indexes
//          final override lazy val isNestedOpt: Boolean = true
//        """
          q"""
          final override def packed2tpl(vs: Iterator[String]): (..$TplTypes) = ${packed2tpl(typess, postTypes, indexes)}
          final override def packed2obj(vs: Iterator[String]): $ObjType = ???
          final override def packed2json(vs: Iterator[String]): String = ???

          final override lazy val indexes: Indexes = $indexes
          final override lazy val isNestedOpt: Boolean = true
        """

        val transformers =
        //          q"""
        //          ..$jsTransformers
        //          ..${buildTplNested(castss, typess, TplTypes, postTypes, postCasts).get}
        //          final override def outerTpl2obj(tpl0: (..$TplTypes)): $ObjType = {
        //            $tpl
        //            ${objFlat(obj, isNestedOpt = true)._1}
        //          }
        //          ..${buildJsonNested(obj, nestedRefs, postJsons).get}
        //         """
        //          q"""
        //          ..$jsTransformers
        ////          ..{buildTplNested(castss, typess, TplTypes, postTypes, postCasts).get}
        //          final override def outerTpl2obj(tpl0: (..$TplTypes)): $ObjType = {
        //            $tpl
        //            ${objFlat(obj, isNestedOpt = true)._1}
        //          }
        ////          ..{buildJsonNested(obj, nestedRefs, postJsons).get}
        //         """
          q"""
          ..$jsTransformers
         """

        if (hasVariables) {
          q"""
            final private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
            final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel)) {
              ..$transformers
            }
          """
        } else {
          //          q"""
          //            final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)})
          //              with $jsNestedOptTupleClass[$ObjType, (..$TplTypes)]
          //              with $jsNestedOptJsonClass[$ObjType, (..$TplTypes)] {
          //              ..$transformers
          //            }
          //          """
          q"""
            final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)}) {
              ..$transformers
            }
          """
        }

      } else {
        // jvm platform


        lazy val tpl = if (TplTypes.length == 1)
          q"lazy val tpl: Product = Tuple1(row2tpl(row))"
        else
          q"lazy val tpl: Product = row2tpl(row)"

        val nestedOptJsonClass = tq"_root_.molecule.core.macros.nested.NestedOptJson"

        val transformers =
          q"""
          final override def row2tpl(row: jList[AnyRef]): (..$TplTypes) =
            ${tplNestedOpt(obj, nestedOptRefIndexes, nestedOptTacitIndexes)}.asInstanceOf[(..$TplTypes)]

          final override def row2obj(row: jList[AnyRef]): $ObjType = {
            $tpl
            ${objFlat(obj, isNestedOpt = true)._1}
          }

          final override def row2json(sb: StringBuffer, row: jList[AnyRef]): StringBuffer =
            ${jsonNestedOpt(obj, nestedOptRefIndexes, nestedOptTacitIndexes)}
        """

        if (hasVariables) {
          q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel))
            with $nestedOptJsonClass[$ObjType, (..$TplTypes)] {
            ..$transformers
          }
        """
        } else {
          q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)})
            with $nestedOptJsonClass[$ObjType, (..$TplTypes)] {
            ..$transformers
          }
        """
        }
      }
    }


    def mkNested = {
      val jsTransformers = if (isJsPlatform) {
        //        val (indexes, arrays, lookups) = resolveIndexes(flatIndexes0, castss.length)
        val (arrays, lookups) = resolveIndexes(indexes, levels)

        //        xx(3, indexes)

        val setters = lookups.map(lookup => q"list.add($lookup.asInstanceOf[AnyRef])")
//        q"""
//          final override def qr2list(qr: QueryResult): Int => jList[Any] = {
//            ..$arrays
//            (i: Int) => {
//              val list = new java.util.ArrayList[Any](${setters.length})
//              ..$setters
//              list
//            }
//          }
//          final override def qr2obj(qr: QueryResult): Int => $ObjType = ???
//
//          final override def packed2tpl(vs: Iterator[String]): (..$TplTypes) = ${packed2tpl(typess, postTypes, indexes)}
//          final override def packed2obj(vs: Iterator[String]): $ObjType = ???
//          final override def packed2json(vs: Iterator[String]): String = ???
//
//          final override lazy val indexes: Indexes = $indexes
//          final override lazy val nestedLevels: Int = ${levels - 1}
//        """
        q"""
          final override def packed2tpl(vs: Iterator[String]): (..$TplTypes) = ${packed2tpl(typess, postTypes, indexes)}
          final override def packed2obj(vs: Iterator[String]): $ObjType = ???
          final override def packed2json(vs: Iterator[String]): String = ???

          final override lazy val indexes: Indexes = $indexes
          final override lazy val nestedLevels: Int = ${levels - 1}
        """
      } else q""


      lazy val tpl = if (TplTypes.length == 1)
        q"lazy val tpl: Product = Tuple1(tpl0)"
      else
        q"lazy val tpl: Product = tpl0"

      val transformers =
//        q"""
//          ..$jsTransformers
//          ..${buildTplNested(castss, typess, TplTypes, postTypes, postCasts).get}
//          final override def outerTpl2obj(tpl0: (..$TplTypes)): $ObjType = {
//            $tpl
//            ${objFlat(obj, isNestedOpt = true)._1}
//          }
//          ..${buildJsonNested(obj, nestedRefs, postJsons).get}
//         """
        q"""
          ..$jsTransformers
         """

      val nestedTupleClass = tq"${nestedTupleClassX(levels)}"
      val nestedJsonClass  = tq"${nestedJsonClassX(levels)}"

      if (isJsPlatform) {
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
//          q"""
//            final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)})
//              with $nestedTupleClass[$ObjType, (..$TplTypes)]
//              with $nestedJsonClass[$ObjType, (..$TplTypes)] {
//              ..$transformers
//            }
//          """
          q"""
            final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)}) {
              ..$transformers
            }
          """
        }
      } else {
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
    }


    val moleculeClass = if (levels == 1 || txMetaCompositesCount > 0)
      mkFlat
    else if (isNestedOpt)
      mkNestedOpt
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
      , indexes
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
