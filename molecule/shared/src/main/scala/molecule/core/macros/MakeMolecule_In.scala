package molecule.core.macros

import molecule.core.ops.{Liftables, TreeOps}
import molecule.core.transform.Dsl2Model
import molecule.datomic.base.transform.Model2Query
import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox


/** Macro to make input molecules. */
class MakeMolecule_In(val c: blackbox.Context) extends MakeBase {

  import c.universe._

  //   private lazy val xx = InspectMacro("MakeMolecule_In", 7, mkError = true)
  private lazy val xx = InspectMacro("MakeMolecule_In", 70)

  private[this] final def generateInputMolecule(dsl: Tree, ObjType: Type, InTypes: Type*)(OutTypes: Type*): Tree = {
    val (
      genericImports, model,
      typess, castss,
      obj,
      nestedRefs, hasVariables, txMetas,
      postJsons,
      isOptNested,
      optNestedRefIndexes, optNestedTacitIndexes,
      doSort
      )                  = getModel(dsl)
    val imports          = getImports(genericImports)
    val InputMoleculeTpe = inputMolecule_i_o(InTypes.size, OutTypes.size)
    val OutMoleculeTpe   = molecule_o(OutTypes.size)
    val inputMolecule    = TypeName(c.freshName("inputMolecule$"))
    val outMolecule      = TypeName(c.freshName("outMolecule$"))
    lazy val flat             = castss.size == 1
    lazy val nestedTupleClass = tq"${nestedTupleClassX(castss.size)}"
    lazy val nestedJsonClass  = tq"${nestedJsonClassX(castss.size)}"
    lazy val levels           = castss.size - txMetas
    lazy val jsTpl            = Some(if (OutTypes.length == 1) q"Tuple1(packed2tpl(vs))" else q"packed2tpl(vs)")


    def getApplyValues(outMoleculeClass: Tree) = {
      q"""
        def outMoleculeValues(args0: Either[Throwable, Seq[(..$InTypes)]])
                             (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$OutTypes] = {
          val queryData: (Query, String, Option[Throwable]) = args0 match {
            case Right(args) =>
              bindValues(_query, args) match {
                case Right(boundQuery) =>
                  val optimizedQuery = QueryOptimizer(boundQuery)
                  (optimizedQuery, Query2String(optimizedQuery).multiLine(60), None)
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
                  case Right(boundQuery) =>
                    val optimizedQuery = QueryOptimizer(boundQuery)
                    (optimizedQuery, Query2String(optimizedQuery).multiLine(60), None)
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
                  case Right(boundQuery) =>
                    val optimizedQuery = QueryOptimizer(boundQuery)
                    (optimizedQuery, Query2String(optimizedQuery).multiLine(60), None)
                  case Left(exc)         => (_query, "", Some(exc))
                }
              case Left(exc)   => (_query, "", Some(exc))
            }
            $outMoleculeClass
            new $outMolecule
          }
        """
    }


    def getInputClass(outMoleculeClass: Tree) = if (hasVariables) {
      val identifiers = mapIdentifiers(model.elements).toMap
      q"""
        private val _resolvedModel: Model = resolveIdentifiers($model, $identifiers)
        final class $inputMolecule extends $InputMoleculeTpe[$ObjType, ..$InTypes, ..$OutTypes](
          _resolvedModel, Model2Query(_resolvedModel, optimize = false) // Optimize after binding input variables
        ) {
          val isJsPlatform = $isJsPlatform
          ${getApplyValues(outMoleculeClass)}
          ${getApplySeq(outMoleculeClass)}
        }
      """
    } else {
      q"""
        final class $inputMolecule extends $InputMoleculeTpe[$ObjType, ..$InTypes, ..$OutTypes](
          $model, ${Model2Query(model, optimize = false)}
        ) {
          val isJsPlatform = $isJsPlatform
          ${getApplyValues(outMoleculeClass)}
          ${getApplySeq(outMoleculeClass)}
        }
      """
    }


    def mkFlat = {
      val outMoleculeClass = if (isJsPlatform) {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes](_model, queryData) {
            final override def packed2tpl(vs: Iterator[String]): (..$OutTypes) = ${packed2tplFlat(obj, txMetas)}
            final override def packed2obj(vs: Iterator[String]): $ObjType = ${objTree(obj, jsTpl)}
            final override def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ${packed2jsonFlat(obj, txMetas)}
            final override def obj: nodes.Obj = $obj
            ..${sortCoordinatesFlat(model, doSort)}
          }
        """
      } else {
        val tplCasts = if (txMetas > 0) {
          // Treat tx meta data as composite
          q"(..${topLevel(List(castss.head))}, ..${compositeCasts(castss.tail, castss.head.length)})"
        } else {
          q"(..${topLevel(castss)})"
        }
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes](_model, queryData) {
            final override def row2tpl(row: jList[AnyRef]): (..$OutTypes) = $tplCasts
            final override def row2obj(row: jList[AnyRef]): $ObjType = ${objTree(obj)}
            final override def row2json(row: jList[AnyRef], sb: StringBuffer): StringBuffer = ${jsonFlat(obj)}
            ..${compareFlat(model, doSort)}
          }
        """
      }
      getInputClass(outMoleculeClass)
    }


    def mkNested = {
      val outMoleculeClass = if (isJsPlatform) {
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes](_model, queryData) {
            final override def packed2tpl(vs: Iterator[String]): (..$OutTypes) = ${packed2tplNested(typess, obj, txMetas)}
            final override def packed2obj(vs: Iterator[String]): $ObjType = ${objTree(obj, jsTpl)}
            final override def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ${packed2jsonNested(levels, obj, txMetas)}
            final override def obj: nodes.Obj = $obj
            final override def nestedLevels: Int = ${levels - 1}
            ..${sortCoordinatesNested(model, levels)}
          }
        """
      } else {
        val tpl = Some(if (OutTypes.length == 1) q"Tuple1(tpl0)" else q"tpl0")
        q"""
          final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$OutTypes](_model, queryData)
            with $nestedTupleClass[$ObjType, (..$OutTypes)]
            with $nestedJsonClass[$ObjType, (..$OutTypes)] {
            ..${buildTplNested(castss, typess, OutTypes, txMetas).get}
            ..${buildJsonNested(obj, nestedRefs, txMetas, postJsons).get}
            final override def outerTpl2obj(tpl0: (..$OutTypes)): $ObjType = ${objTree(obj, tpl)}
            final override def nestedLevels: Int = ${levels - 1}
            ..${compareNested(model, levels, doSort)}
          }
        """
      }
      getInputClass(outMoleculeClass)
    }

    val inputMoleculeClass = if (flat || txMetas > 0) mkFlat else mkNested

    val tree =
      q"""
        {
          ..$imports
          ..$inputMoleculeClass
          new $inputMolecule
        }
      """

    xx(7, obj, tree)
    tree
  }


  // Input molecules with 1 input and 1-22 outputs

  final def await_1_01[Obj: W, I1: W, A: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A])
  final def await_1_02[Obj: W, I1: W, A: W, B: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B])
  final def await_1_03[Obj: W, I1: W, A: W, B: W, C: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C])
  final def await_1_04[Obj: W, I1: W, A: W, B: W, C: W, D: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D])
  final def await_1_05[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E])
  final def await_1_06[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F])
  final def await_1_07[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G])
  final def await_1_08[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H])
  final def await_1_09[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I])
  final def await_1_10[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J])
  final def await_1_11[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K])
  final def await_1_12[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L])
  final def await_1_13[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M])
  final def await_1_14[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N])
  final def await_1_15[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O])
  final def await_1_16[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P])
  final def await_1_17[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q])
  final def await_1_18[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R])
  final def await_1_19[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S])
  final def await_1_20[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T])
  final def await_1_21[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W, U: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T], w[U])
  final def await_1_22[Obj: W, I1: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W, U: W, V: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T], w[U], w[V])


  // Input molecules with 2 inputs and 1-22 outputs

  final def await_2_01[Obj: W, I1: W, I2: W, A: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A])
  final def await_2_02[Obj: W, I1: W, I2: W, A: W, B: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B])
  final def await_2_03[Obj: W, I1: W, I2: W, A: W, B: W, C: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C])
  final def await_2_04[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D])
  final def await_2_05[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E])
  final def await_2_06[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F])
  final def await_2_07[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G])
  final def await_2_08[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H])
  final def await_2_09[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I])
  final def await_2_10[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J])
  final def await_2_11[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K])
  final def await_2_12[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L])
  final def await_2_13[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M])
  final def await_2_14[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N])
  final def await_2_15[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O])
  final def await_2_16[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P])
  final def await_2_17[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q])
  final def await_2_18[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R])
  final def await_2_19[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S])
  final def await_2_20[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T])
  final def await_2_21[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W, U: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T], w[U])
  final def await_2_22[Obj: W, I1: W, I2: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W, U: W, V: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T], w[U], w[V])


  // Input molecules with 3 inputs and 1-22 outputs

  final def await_3_01[Obj: W, I1: W, I2: W, I3: W, A: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A])
  final def await_3_02[Obj: W, I1: W, I2: W, I3: W, A: W, B: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B])
  final def await_3_03[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C])
  final def await_3_04[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D])
  final def await_3_05[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E])
  final def await_3_06[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F])
  final def await_3_07[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G])
  final def await_3_08[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H])
  final def await_3_09[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I])
  final def await_3_10[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J])
  final def await_3_11[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K])
  final def await_3_12[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L])
  final def await_3_13[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M])
  final def await_3_14[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N])
  final def await_3_15[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O])
  final def await_3_16[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P])
  final def await_3_17[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q])
  final def await_3_18[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R])
  final def await_3_19[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S])
  final def await_3_20[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T])
  final def await_3_21[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W, U: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T], w[U])
  final def await_3_22[Obj: W, I1: W, I2: W, I3: W, A: W, B: W, C: W, D: W, E: W, F: W, G: W, H: W, I: W, J: W, K: W, L: W, M: W, N: W, O: W, P: W, Q: W, R: W, S: W, T: W, U: W, V: W](dsl: Tree): Tree = generateInputMolecule(dsl, w[Obj], w[I1], w[I2], w[I3])(w[A], w[B], w[C], w[D], w[E], w[F], w[G], w[H], w[I], w[J], w[K], w[L], w[M], w[N], w[O], w[P], w[Q], w[R], w[S], w[T], w[U], w[V])
}