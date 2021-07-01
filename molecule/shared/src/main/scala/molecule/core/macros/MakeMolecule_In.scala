package molecule.core.macros

import molecule.core.macros.lambdaTrees.LambdaCastAggr
import molecule.core.ops.{Liftables, TreeOps}
import molecule.core.transform.Dsl2Model
import molecule.datomic.base.transform.Model2Query
import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox


/** Macro to make input molecules. */
class MakeMolecule_In(val c: blackbox.Context) extends Base {

  import c.universe._

  //   val z = InspectMacro("MakeMolecule_In", 1, 8, mkError = true)
   val z = InspectMacro("MakeMolecule_In", 9, 8)
  //      override val z = InspectMacro("MakeMolecule_In", 1, 8)

  private[this] final def generateInputMolecule(dsl: Tree, ObjType: Type, InTypes: Type*)(TplTypes: Type*): Tree = {
    val (
      genericImports, model0,
      typess, castss, jsonss,
      indexes, obj,
      nestedRefs, hasVariables, txMetaCompositesCount,
      postTypes, postCasts, postJsons,
      isOptNested,
      optNestedRefIndexes, optNestedTacitIndexes
      )                  = getModel(dsl)
    val imports          = getImports(genericImports)
    val InputMoleculeTpe = inputMolecule_i_o(InTypes.size, TplTypes.size)
    val OutMoleculeTpe   = molecule_o(TplTypes.size)
    val inputMolecule    = TypeName(c.freshName("inputMolecule$"))
    val outMolecule      = TypeName(c.freshName("outMolecule$"))
    val flat             = castss.size == 1

    // Methods for applying separate lists of input
    val applySeqs = InTypes match {
      case Seq(it1) => q"" // no extra

      case Seq(it1, it2) =>
        val (i1, i2)                     = (TermName(s"in1"), TermName(s"in2"))
        val (t1, t2)                     = (tq"Seq[$it1]", tq"Seq[$it2]")
        val (inParams, inTerm1, inTerm2) = (Seq(q"$i1: $t1", q"$i2: $t2"), i1, i2)
        if (flat) {
          q"""
            def outMoleculeSeqs(args0: Either[Throwable, (Seq[$it1], Seq[$it2])])
                               (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$TplTypes] = {
              val queryData: (Query, Option[Query], Query, Option[Query], Option[Throwable]) = args0 match {
                case Right(args) => bindSeqs(_rawQuery, args._1, args._2) match {
                  case Right(boundRawQuery) => (QueryOptimizer(boundRawQuery), None, boundRawQuery, None, None)
                  case Left(exc)            => (_rawQuery, None, _rawQuery, None, Some(exc))
                }
                case Left(exc)   => (_rawQuery, None, _rawQuery, None, Some(exc))
              }
              final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_model, queryData) {
                final override def row2tpl(row: java.util.List[AnyRef]): (..$TplTypes) = (..${topLevel(castss)})
                final override def row2obj(row: java.util.List[AnyRef]): $ObjType = ${objCode(obj)._1}
              }
              new $outMolecule
            }
          """
        } else {
          q"""
            def outMoleculeSeqs(args0: Either[Throwable, (Seq[$it1], Seq[$it2])])
                               (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$TplTypes] = {
              val queryDataNested: (Query, Option[Query], Query, Option[Query], Option[Throwable]) = args0 match {
                case Right(args) => bindSeqs(_rawQuery, args._1, args._2) match {
                  case Right(boundRawQuery) => bindSeqs(_rawNestedQuery.get, args._1, args._2) match {
                    case Right(boundRawNestedQuery) => (
                      QueryOptimizer(boundRawQuery),
                      Some(QueryOptimizer(boundRawNestedQuery)),
                      boundRawQuery,
                      Some(boundRawNestedQuery),
                      None
                    )
                    case Left(exc)                  => (_rawQuery, None, _rawQuery, None, Some(exc))
                  }
                  case Left(exc)            => (_rawQuery, None, _rawQuery, None, Some(exc))
                }
                case Left(exc)   => (_rawQuery, None, _rawQuery, None, Some(exc))
              }
              final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_model, queryDataNested)
                with ${nestedJsonClassX(castss.size)}[$ObjType, (..$TplTypes)]
              {
                ..${resolveNestedTupleMethods(castss, typess, TplTypes, postTypes, postCasts).get}
                ..${resolveNestedJsonMethods(obj, jsonss, nestedRefs, postJsons).get}
              }
              new $outMolecule
            }
          """
        }

      case Seq(it1, it2, it3) =>
        val (i1, i2, i3)                          = (TermName(s"in1"), TermName(s"in2"), TermName(s"in3"))
        val (t1, t2, t3)                          = (tq"Seq[$it1]", tq"Seq[$it2]", tq"Seq[$it3]")
        val (inParams, inTerm1, inTerm2, inTerm3) = (Seq(q"$i1: $t1", q"$i2: $t2", q"$i3: $t3"), i1, i2, i3)
        if (flat) {
          q"""
            def outMoleculeSeqs(args0: Either[Throwable, (Seq[$it1], Seq[$it2], Seq[$it3])])
                               (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$TplTypes] = {
              val queryData: (Query, Option[Query], Query, Option[Query], Option[Throwable]) = args0 match {
                case Right(args) => bindSeqs(_rawQuery, args._1, args._2, args._3) match {
                  case Right(boundRawQuery) => (QueryOptimizer(boundRawQuery), None, boundRawQuery, None, None)
                  case Left(exc)            => (_rawQuery, None, _rawQuery, None, Some(exc))
                }
                case Left(exc)   => (_rawQuery, None, _rawQuery, None, Some(exc))
              }
              final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_model, queryData) {
                final override def row2tpl(row: java.util.List[AnyRef]): (..$TplTypes) = (..${topLevel(castss)})
                final override def row2obj(row: java.util.List[AnyRef]): $ObjType = ${objCode(obj)._1}
              }
              new $outMolecule
            }
          """
        } else {
          q"""
            def outMoleculeSeqs(args0: Either[Throwable, (Seq[$it1], Seq[$it2], Seq[$it3])])
                               (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$TplTypes] = {
              val queryDataNested: (Query, Option[Query], Query, Option[Query], Option[Throwable]) = args0 match {
                case Right(args) => bindSeqs(_rawQuery, args._1, args._2, args._3) match {
                  case Right(boundRawQuery) => bindSeqs(_rawNestedQuery.get, args._1, args._2, args._3) match {
                    case Right(boundRawNestedQuery) => (
                      QueryOptimizer(boundRawQuery),
                      Some(QueryOptimizer(boundRawNestedQuery)),
                      boundRawQuery,
                      Some(boundRawNestedQuery),
                      None
                    )
                    case Left(exc)                  => (_rawQuery, None, _rawQuery, None, Some(exc))
                  }
                  case Left(exc)            => (_rawQuery, None, _rawQuery, None, Some(exc))
                }
                case Left(exc)   => (_rawQuery, None, _rawQuery, None, Some(exc))
              }
              final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_model, queryDataNested)
                with ${nestedJsonClassX(castss.size)}[$ObjType, (..$TplTypes)] {
                ..${resolveNestedTupleMethods(castss, typess, TplTypes, postTypes, postCasts).get}
                ..${resolveNestedJsonMethods(obj, jsonss, nestedRefs, postJsons).get}
              }
              new $outMolecule
            }
          """
        }
    }

    if (flat) {
      val typers = if (isJsPlatform) {
        val (arrays, lookups0) = indexes.map {
          // Generic `v` of type Any needs to be cast on JS side
          case (colIndex, 11, arrayType, arrayIndex) =>
            (dataArrays(arrayType)(colIndex, arrayIndex), q"castV(${TermName("a" + colIndex)}(i))")

          case (colIndex, castIndex, arrayType, arrayIndex) =>
            (dataArrays(arrayType)(colIndex, arrayIndex), q"${TermName("a" + colIndex)}(i)")
        }.unzip

        val lookups = if (txMetaCompositesCount > 0) {
          // Treat tx meta data as composite
          val first = topLevelLookups(List(castss.head), lookups0)
          val last  = compositeLookups(castss.tail, lookups0, castss.head.length)
          q"(..$first, ..$last)"
        } else {
          q"(..$lookups0)"
        }
        z(1, txMetaCompositesCount, castss, lookups)
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
          q"(..${topLevel(List(castss.head))}, ..${compositeCasts(castss.tail, castss.head.length)})"
        } else {
          q"(..${topLevel(castss)})"
        }
        q"""
          final override def row2tpl(row: java.util.List[AnyRef]): (..$TplTypes) = $casts
          final override def row2obj(row: java.util.List[AnyRef]): $ObjType = ${objCode(obj)._1}
        """
      }

      if (hasVariables) {
        q"""
          ..$imports
          private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
          final class $inputMolecule extends $InputMoleculeTpe[$ObjType, ..$InTypes, ..$TplTypes](
            _resolvedModel, Model2Query(_resolvedModel)
          ) {
            def outMoleculeValues(args0: Either[Throwable, Seq[(..$InTypes)]])
                                 (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$TplTypes] = {
              val queryData: (Query, Option[Query], Query, Option[Query], Option[Throwable]) = args0 match {
                case Left(exc)   => (_rawQuery, None, _rawQuery, None, Some(exc))
                case Right(args) => bindValues(_rawQuery, args) match {
                  case Right(boundRawQuery) => (QueryOptimizer(boundRawQuery), None, boundRawQuery, None, None)
                  case Left(exc)            => (_rawQuery, None, _rawQuery, None, Some(exc))
                }
              }
              final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_model, queryData) {
                ..$typers
              }
              new $outMolecule
            }
            $applySeqs
          }
          new $inputMolecule
        """
      } else {
        val t =
          q"""
            ..$imports
            final class $inputMolecule extends $InputMoleculeTpe[$ObjType, ..$InTypes, ..$TplTypes](
              $model0, ${Model2Query(model0)}
            ) {
              def outMoleculeValues(args0: Either[Throwable, Seq[(..$InTypes)]])
                                   (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$TplTypes] = {
                val queryData: (Query, Option[Query], Query, Option[Query], Option[Throwable]) = args0 match {
                  case Left(exc)   => (_rawQuery, None, _rawQuery, None, Some(exc))
                  case Right(args) => bindValues(_rawQuery, args) match {
                    case Right(boundRawQuery) => (QueryOptimizer(boundRawQuery), None, boundRawQuery, None, None)
                    case Left(exc)            => (_rawQuery, None, _rawQuery, None, Some(exc))
                  }
                }
                final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_model, queryData) {
                  ..$typers
                }
                new $outMolecule
              }
              $applySeqs
            }
            new $inputMolecule
          """

        z(1
          //          , model0
          //          , obj
          , t
        )


        t
      }

    } else {

      if (hasVariables) {
        q"""
          ..$imports
          private val _resolvedModel: Model = resolveIdentifiers($model0, ${mapIdentifiers(model0.elements).toMap})
          final class $inputMolecule extends $InputMoleculeTpe[$ObjType, ..$InTypes, ..$TplTypes](
            _resolvedModel, Model2Query(_resolvedModel)
          ) {
            def outMoleculeValues(args0: Either[Throwable, Seq[(..$InTypes)]])
                                 (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$TplTypes] = {
              val queryDataNested: (Query, Option[Query], Query, Option[Query], Option[Throwable]) = args0 match {
                case Left(exc)   => (_rawQuery, None, _rawQuery, None, Some(exc))
                case Right(args) => bindValues(_rawQuery, args) match {
                  case Right(boundRawQuery) => bindValues(_rawNestedQuery.get, args) match {
                    case Right(boundRawNestedQuery) => (
                      QueryOptimizer(boundRawQuery),
                      Some(QueryOptimizer(boundRawNestedQuery)),
                      boundRawQuery,
                      Some(boundRawNestedQuery),
                      None
                    )
                    case Left(exc)                  => (_rawQuery, None, _rawQuery, None, Some(exc))
                  }
                  case Left(exc)            => (_rawQuery, None, _rawQuery, None, Some(exc))
                }
              }
              final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_model, queryDataNested)
                with ${nestedJsonClassX(castss.size)}[$ObjType, (..$TplTypes)] {
                ..${resolveNestedTupleMethods(castss, typess, TplTypes, postTypes, postCasts).get}
                ..${resolveNestedJsonMethods(obj, jsonss, nestedRefs, postJsons).get}
              }
              new $outMolecule
            }
            $applySeqs
          }
          new $inputMolecule
        """
      } else {
        q"""
          ..$imports
          final class $inputMolecule extends $InputMoleculeTpe[$ObjType, ..$InTypes, ..$TplTypes](
            $model0, ${Model2Query(model0)}
          ) {
            def outMoleculeValues(args0: Either[Throwable, Seq[(..$InTypes)]])
                                 (implicit conn: Future[Conn]): $OutMoleculeTpe[$ObjType, ..$TplTypes] = {
              val queryDataNested: (Query, Option[Query], Query, Option[Query], Option[Throwable]) = args0 match {
                case Left(exc)   => (_rawQuery, None, _rawQuery, None, Some(exc))
                case Right(args) => bindValues(_rawQuery, args) match {
                  case Right(boundRawQuery) => bindValues(_rawNestedQuery.get, args) match {
                    case Right(boundRawNestedQuery) => (
                      QueryOptimizer(boundRawQuery),
                      Some(QueryOptimizer(boundRawNestedQuery)),
                      boundRawQuery,
                      Some(boundRawNestedQuery),
                      None
                    )
                    case Left(exc)                  => (_rawQuery, None, _rawQuery, None, Some(exc))
                  }
                  case Left(exc)            => (_rawQuery, None, _rawQuery, None, Some(exc))
                }
              }
              final class $outMolecule extends $OutMoleculeTpe[$ObjType, ..$TplTypes](_model, queryDataNested)
                with ${nestedJsonClassX(castss.size)}[$ObjType, (..$TplTypes)] {
                ..${resolveNestedTupleMethods(castss, typess, TplTypes, postTypes, postCasts).get}
                ..${resolveNestedJsonMethods(obj, jsonss, nestedRefs, postJsons).get}
              }
              new $outMolecule
            }
            $applySeqs
          }
          new $inputMolecule
        """
      }
    }
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