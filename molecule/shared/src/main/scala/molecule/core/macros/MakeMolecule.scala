package molecule.core.macros

import molecule.core.macros.trees.LambdaCastAggr
import molecule.core.ops.{Liftables, TreeOps}
import molecule.core.transform.Dsl2Model
import molecule.datomic.base.transform.Model2Query
import scala.language.higherKinds
import scala.reflect.macros.blackbox

class MakeMolecule(val c: blackbox.Context) extends Base {

  import c.universe._

  //  override val z = InspectMacro("MakeMolecule", 1, 8, mkError = true)
    override val z = InspectMacro("MakeMolecule", 2, 8)
//  override val z = InspectMacro("MakeMolecule", 9, 7)


  private[this] final def generateMolecule(dsl: Tree, ObjType: Type, TplTypes: Type*): Tree = {
    val (
      genericImports, model0,
      typess, castss, jsonss,
      indexes0, obj,
      nestedRefAttrs, hasVariables, txMetaCompositesCount,
      postTypes, postCasts, postJsons,
      isOptNested,
      optNestedRefIndexes, optNestedTacitIndexes
      ) = getModel(dsl)

    val imports              = getImports(genericImports)
    val OutMoleculeTpe: Tree = molecule_o(TplTypes.size)
    val outMolecule          = TypeName(c.freshName("outMolecule$"))

    lazy val tpl = if (TplTypes.length == 1)
      q"val tpl: Product = Tuple1(row2tpl(row))"
    else
      q"val tpl: Product = row2tpl(row)"

    def resolveIndexes(nestedLevels: Int) = {
      val indexes           = if (nestedLevels == 0) indexes0 else {
        val nestedIndexes = (0 until nestedLevels).toList.map(i => (i, 3, 2, i))
        val dataIndexes   = indexes0.map {
          case (colIndex, castIndex, 2, arrayIndex)         =>
            // One Long array type where nested eid indexes are transferred
            (colIndex + nestedLevels, castIndex, 2, arrayIndex + nestedLevels)
          case (colIndex, castIndex, arrayType, arrayIndex) =>
            (colIndex + nestedLevels, castIndex, arrayType, arrayIndex)
        }
        nestedIndexes ++ dataIndexes
      }
      val (arrays, lookups) = indexes.map {
        // Generic `v` of type Any needs to be cast on JS side
        case (colIndex, 11, arrayType, arrayIndex)        =>
          (dataArrays(arrayType)(colIndex, arrayIndex), q"castV(${TermName("a" + colIndex)}(i))")
        case (colIndex, castIndex, arrayType, arrayIndex) =>
          (dataArrays(arrayType)(colIndex, arrayIndex), q"${TermName("a" + colIndex)}(i)")
      }.unzip
      (indexes, arrays, lookups)
    }


    def mkFlat = {
      val typers = if (isJsPlatform) {
        val (indexes, arrays, lookups0) = resolveIndexes(0)

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
          final override lazy val indexes: List[(Int, Int, Int, Int)] = $indexes
        """

      } else {
        val casts = if (txMetaCompositesCount > 0) {
          // Treat tx meta data as composite
          val first = topLevel(List(castss.head))
          val last  = compositeCasts(castss.tail, castss.head.length)
          q"(..$first, ..$last)"
        } else {
          q"(..${topLevel(castss)})"
        }
        q"""
          final override def row2tpl(row: java.util.List[AnyRef]): (..$TplTypes) = $casts
          final override def row2obj(row: java.util.List[AnyRef]): $ObjType = ${objCode(obj)._1}
          final override def row2json(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = {..${topLevelJson(jsonss)}}
        """
      }

      if (hasVariables) {
        q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel)) {
            ..$typers
          }
        """
      } else {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)}) {
            ..$typers
          }
        """
      }
    }

    def mkOptNested = {
      if (hasVariables) {
        q"""
          final private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel)) {
            ..${castOptNestedRows(castss, TplTypes, optNestedRefIndexes, optNestedTacitIndexes)}
            final override def row2obj(row: java.util.List[AnyRef]): $ObjType = {
              $tpl
              ${objCode(obj, isNested = true)._1}
            }
          }
        """
      } else {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)}) {
            ..${castOptNestedRows(castss, TplTypes, optNestedRefIndexes, optNestedTacitIndexes)}
            final override def row2obj(row: java.util.List[AnyRef]): $ObjType = {
              $tpl
              ${objCode(obj, isNested = true)._1}
            }
          }
        """
      }
    }

    def mkNested = {
      val jsResolvers = if (isJsPlatform) {
        val (indexes, arrays, lookups) = resolveIndexes(castss.length)

        val setters = lookups.map(lookup => q"list.add($lookup.asInstanceOf[AnyRef])")
        q"""
          final override def qr2list(qr: QueryResult): Int => java.util.List[AnyRef] = {
            ..$arrays
            (i: Int) => {
              val list = new java.util.ArrayList[AnyRef](${setters.length})
              ..$setters
              list
            }
          }
          final override def qr2obj(qr: QueryResult): Int => $ObjType = ???
          final override lazy val indexes: List[(Int, Int, Int, Int)] = $indexes
        """
      } else q""

      val resolvers =
        q"""
          ..$jsResolvers
          ..${resolveNestedTupleMethods(castss, typess, TplTypes, postTypes, postCasts).get}
          ..${resolveNestedJsonMethods(jsonss, nestedRefAttrs, postJsons).get}
          final override def outerTpl2obj(tpl: (..$TplTypes)): $ObjType = {
            $tpl
            ${objCode(obj, isNested = true)._1}
          }
         """

      val nestedTupleClass = tq"${nestedJsonClassX(castss.size)}"
      val resolveModel     =
        q"final private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})"

      if (isJsPlatform) {
        if (hasVariables) {
          q"""
            $resolveModel
            final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel))
              with $nestedTupleClass[$ObjType, (..$TplTypes)] with TypedCastHelpers {
              ..$resolvers
            }
          """
        } else {
          q"""
            final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)})
              with $nestedTupleClass[$ObjType, (..$TplTypes)] with TypedCastHelpers {
              ..$resolvers
            }
          """
        }
      } else {
        if (hasVariables) {
          q"""
            $resolveModel
            final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_resolvedModel, Model2Query(_resolvedModel))
              with $nestedTupleClass[$ObjType, (..$TplTypes)] {
              ..$resolvers
            }
          """
        } else {
          q"""
            final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes]($model0, ${Model2Query(model0)})
              with $nestedTupleClass[$ObjType, (..$TplTypes)] {
              ..$resolvers
            }
          """
        }
      }
    }


    val moleculeClass = if (castss.size == 1 || txMetaCompositesCount > 0) mkFlat
    else if (isOptNested) mkOptNested
    else mkNested

    val t =
      q"""
        ..$imports
        ..$moleculeClass
        new $outMolecule
      """

    z(7, t, obj)
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
