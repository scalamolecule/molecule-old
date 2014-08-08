package molecule.in
import molecule.ast.query._
import molecule.dsl.schemaDSL._
import molecule.ops.QueryOps._
import molecule.ops.TreeOps
import molecule.transform._
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context


trait BuildInputMolecule[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = debug("BuildInputMolecule", 1, 60)
  type KeepQueryOpsWhenFormatting = KeepQueryOps

  val imports = q"""
      import molecule.in._
      import molecule.out._
      import molecule.ast.query._
      import molecule.ast.model._
      import molecule.transform.Model2Transaction._
      import molecule.DatomicFacade._
      import shapeless._
      import scala.collection.JavaConversions._
      import scala.collection.JavaConverters._
      import datomic.Connection
      """
  def await_in_x_out_0(inputDsl: c.Expr[NS], InTypes: Type*) = {
    val model = Dsl2Model(c)(inputDsl)
    val query = Model2Query(model)
    val InputMoleculeTpe = inputMolecule_i_o(InTypes.size, 1)
    val InputTypes = if (InTypes.length == 1) tq"Seq[..$InTypes]" else tq"Seq[(..$InTypes)]"
    expr( q"""
      ..$imports
      new $InputMoleculeTpe[..$InTypes]($model, $query) {
        def apply(values: $InputTypes)(implicit conn: Connection): Molecule0 = {
          val (query1, entityQuery) = bindValues(values)
          new Molecule0(model, query1) {
            def ids: Seq[Long] = entityIds(entityQuery)(conn)
          }
        }
      }
      """)
  }

  def await_in_x_out_1(inputDsl: c.Expr[NS], A: Type, InTypes: Type*) = {
    val model = Dsl2Model(c)(inputDsl)
    val query = Model2Query(model)
    val InputMoleculeTpe = inputMolecule_i_o(InTypes.size, 1)

    val cast = (data: Tree) => if (A <:< typeOf[Set[_]])
      q"$data.get(0).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$A]"
    else
      q"$data.get(0).asInstanceOf[$A]"

    val InputTypes = if (InTypes.length == 1) tq"Seq[..$InTypes]" else tq"Seq[(..$InTypes)]"

    val bindValues2 = if (InTypes.size > 1) {
      val (inTerms, inParams) = InTypes.zipWithIndex.map { case (t, i) =>
        val inTerm = TermName(s"in$i")
        val InType = tq"Seq[$t]"
        (inTerm, q"$inTerm: $InType")
      }.unzip
      q"""
        def apply(..$inParams)(implicit conn: Connection): Molecule1[$A] = {
          val (query1, entityQuery) = bindValues2(..$inTerms)
          new Molecule1[$A](model, query1) {
            def ids: Seq[Long] = entityIds(entityQuery)
            def get(implicit conn: Connection): Seq[$A] = results(_query, conn).toList.map(data => ${cast(q"data")})
          }
        }
      """
    } else q""

    expr( q"""
      ..$imports
      new $InputMoleculeTpe[..$InTypes, $A]($model, $query) {
        def apply(values: $InputTypes)(implicit conn: Connection): Molecule1[$A] = {
          val (query1, entityQuery) = bindValues(values)
          new Molecule1[$A](model, query1) {
            def ids: Seq[Long] = entityIds(entityQuery)(conn)
            def get(implicit conn: Connection): Seq[$A] = results(_query, conn).toList.map(data => ${cast(q"data")})
          }
        }
        $bindValues2
      }
      """)
  }

  def await(inputDsl: c.Expr[NS], InTypes: Type*)(OutTypes: Type*) = {
    val model = Dsl2Model(c)(inputDsl)
    val query = Model2Query(model)
    val entityQuery = query.copy(find = Find(Seq(Var("ent", "Long"))))
    val InputMoleculeTpe = inputMolecule_i_o(InTypes.size, OutTypes.size)
    val MoleculeTpe = molecule_o(OutTypes.size)
    val tplValues = (data: Tree) => OutTypes.zipWithIndex.map {
      case (t, i) if t <:< typeOf[Set[_]] => q"$data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t]"
      case (t, i)                         => q"$data.get($i).asInstanceOf[$t]"
    }
    val HListType = OutTypes.foldRight(tq"HNil": Tree)((t, tpe) => tq"::[$t, $tpe]")
    val hlist = (data: Tree) => OutTypes.zipWithIndex.foldRight(q"shapeless.HList()": Tree) {
      case ((t, i), hl) if t <:< typeOf[Set[_]] => q"$hl.::($data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t])"
      case ((t, i), hl)                         => q"$hl.::($data.get($i).asInstanceOf[$t])"
    }

    val bindValues2 = if (InTypes.size > 1) {
      val (inTerms, inParams) = InTypes.zipWithIndex.map { case (t, i) =>
        val inTerm = TermName(s"in$i")
        val inType = tq"Seq[$t]"
        (inTerm, q"$inTerm: $inType")
      }.unzip
      q"""
        def apply(..$inParams)(implicit conn: Connection): $MoleculeTpe[..$OutTypes] = {
          val (query1, entityQuery) = bindValues2(..$inTerms)
          new $MoleculeTpe[..$OutTypes](model, query1) {
            override def ids: Seq[Long] = entityIds(entityQuery)
            def tpls(implicit conn: Connection): Seq[(..$OutTypes)] = results(_query, conn).toList.map(data => (..${tplValues(q"data")}))
            def hls(implicit conn: Connection): Seq[$HListType]     = results(_query, conn).toList.map(data => ${hlist(q"data")})
          }
        }
      """
    } else q""

    expr( q"""
      ..$imports
      new $InputMoleculeTpe[..$InTypes, ..$OutTypes]($model, $query) {
        def apply(values: Seq[(..$InTypes)])(implicit conn: Connection): $MoleculeTpe[..$OutTypes] = {
          val (query1, entityQuery) = bindValues(values)
          new $MoleculeTpe[..$OutTypes](model, query1) {
            override def ids: Seq[Long] = entityIds($entityQuery)
            def tpls(implicit conn: Connection): Seq[(..$OutTypes)] = results(_query, conn).toList.map(data => (..${tplValues(q"data")}))
            def hls(implicit conn: Connection): Seq[$HListType]     = results(_query, conn).toList.map(data => ${hlist(q"data")})
          }
        }
        $bindValues2
      }
      """)
  }
}

object BuildInputMolecule {
  def inst(c0: Context) = new {val c: c0.type = c0} with BuildInputMolecule[c0.type]

  // Input molecules with 0 output (update templates)

  def await_1_0[I1: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_0[I1]])
  : c.Expr[InputMolecule_1_0[I1]] =
    inst(c).await_in_x_out_0(inputDsl, c.weakTypeOf[I1])

  def await_2_0[I1: c.WeakTypeTag, I2: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_0[I1, I2]])
  : c.Expr[InputMolecule_2_0[I1, I2]] =
    inst(c).await_in_x_out_0(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])

  def await_3_0[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_0[I1, I2, I3]])
  : c.Expr[InputMolecule_3_0[I1, I2, I3]] =
    inst(c).await_in_x_out_0(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])


  // Input molecules with 1 output (have simplified api)

  def await_1_1[I1: c.WeakTypeTag, A: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_1[I1, A]])
  : c.Expr[InputMolecule_1_1[I1, A]] =
    inst(c).await_in_x_out_1(inputDsl, c.weakTypeOf[A], c.weakTypeOf[I1])

  def await_2_1[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_1[I1, I2, A]])
  : c.Expr[InputMolecule_2_1[I1, I2, A]] =
    inst(c).await_in_x_out_1(inputDsl, c.weakTypeOf[A], c.weakTypeOf[I1], c.weakTypeOf[I2])

  def await_3_1[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_1[I1, I2, I3, A]])
  : c.Expr[InputMolecule_3_1[I1, I2, I3, A]] =
    inst(c).await_in_x_out_1(inputDsl, c.weakTypeOf[A], c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])


  // 1 input - multiple outputs

  def await_1_2[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_2[I1, A, B]])
  : c.Expr[InputMolecule_1_2[I1, A, B]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B])

  def await_1_3[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_3[I1, A, B, C]])
  : c.Expr[InputMolecule_1_3[I1, A, B, C]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])

  def await_1_4[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_4[I1, A, B, C, D]])
  : c.Expr[InputMolecule_1_4[I1, A, B, C, D]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D])

  def await_1_5[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_5[I1, A, B, C, D, E]])
  : c.Expr[InputMolecule_1_5[I1, A, B, C, D, E]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E])

  def await_1_6[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_6[I1, A, B, C, D, E, F]])
  : c.Expr[InputMolecule_1_6[I1, A, B, C, D, E, F]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F])

  def await_1_7[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_7[I1, A, B, C, D, E, F, G]])
  : c.Expr[InputMolecule_1_7[I1, A, B, C, D, E, F, G]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G])

  def await_1_8[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_8[I1, A, B, C, D, E, F, G, H]])
  : c.Expr[InputMolecule_1_8[I1, A, B, C, D, E, F, G, H]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H])

  def await_1_9[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_9[I1, A, B, C, D, E, F, G, H, I]])
  : c.Expr[InputMolecule_1_9[I1, A, B, C, D, E, F, G, H, I]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I])

  def await_1_10[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_10[I1, A, B, C, D, E, F, G, H, I, J]])
  : c.Expr[InputMolecule_1_10[I1, A, B, C, D, E, F, G, H, I, J]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J])

  def await_1_11[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_11[I1, A, B, C, D, E, F, G, H, I, J, K]])
  : c.Expr[InputMolecule_1_11[I1, A, B, C, D, E, F, G, H, I, J, K]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K])

  def await_1_12[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L]])
  : c.Expr[InputMolecule_1_12[I1, A, B, C, D, E, F, G, H, I, J, K, L]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L])

  def await_1_13[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M]])
  : c.Expr[InputMolecule_1_13[I1, A, B, C, D, E, F, G, H, I, J, K, L, M]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M])

  def await_1_14[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N]])
  : c.Expr[InputMolecule_1_14[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N])

  def await_1_15[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]])
  : c.Expr[InputMolecule_1_15[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O])

  def await_1_16[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]])
  : c.Expr[InputMolecule_1_16[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P])

  def await_1_17[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]])
  : c.Expr[InputMolecule_1_17[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q])

  def await_1_18[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]])
  : c.Expr[InputMolecule_1_18[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R])

  def await_1_19[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]])
  : c.Expr[InputMolecule_1_19[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S])

  def await_1_20[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]])
  : c.Expr[InputMolecule_1_20[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T])

  def await_1_21[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]])
  : c.Expr[InputMolecule_1_21[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U])

  def await_1_22[I1: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag, V: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]])
  : c.Expr[InputMolecule_1_22[I1, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U], c.weakTypeOf[V])


  // 2 inputs - multiple outputs

  def await_2_2[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_2[I1, I2, A, B]])
  : c.Expr[InputMolecule_2_2[I1, I2, A, B]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B])

  def await_2_3[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_3[I1, I2, A, B, C]])
  : c.Expr[InputMolecule_2_3[I1, I2, A, B, C]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])

  def await_2_4[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_4[I1, I2, A, B, C, D]])
  : c.Expr[InputMolecule_2_4[I1, I2, A, B, C, D]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D])

  def await_2_5[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_5[I1, I2, A, B, C, D, E]])
  : c.Expr[InputMolecule_2_5[I1, I2, A, B, C, D, E]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E])

  def await_2_6[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_6[I1, I2, A, B, C, D, E, F]])
  : c.Expr[InputMolecule_2_6[I1, I2, A, B, C, D, E, F]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F])

  def await_2_7[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_7[I1, I2, A, B, C, D, E, F, G]])
  : c.Expr[InputMolecule_2_7[I1, I2, A, B, C, D, E, F, G]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G])

  def await_2_8[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_8[I1, I2, A, B, C, D, E, F, G, H]])
  : c.Expr[InputMolecule_2_8[I1, I2, A, B, C, D, E, F, G, H]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H])

  def await_2_9[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_9[I1, I2, A, B, C, D, E, F, G, H, I]])
  : c.Expr[InputMolecule_2_9[I1, I2, A, B, C, D, E, F, G, H, I]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I])

  def await_2_10[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J]])
  : c.Expr[InputMolecule_2_10[I1, I2, A, B, C, D, E, F, G, H, I, J]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J])

  def await_2_11[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K]])
  : c.Expr[InputMolecule_2_11[I1, I2, A, B, C, D, E, F, G, H, I, J, K]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K])

  def await_2_12[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]])
  : c.Expr[InputMolecule_2_12[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L])

  def await_2_13[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]])
  : c.Expr[InputMolecule_2_13[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M])

  def await_2_14[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]])
  : c.Expr[InputMolecule_2_14[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N])

  def await_2_15[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]])
  : c.Expr[InputMolecule_2_15[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O])

  def await_2_16[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]])
  : c.Expr[InputMolecule_2_16[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P])

  def await_2_17[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]])
  : c.Expr[InputMolecule_2_17[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q])

  def await_2_18[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]])
  : c.Expr[InputMolecule_2_18[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R])

  def await_2_19[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]])
  : c.Expr[InputMolecule_2_19[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S])

  def await_2_20[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]])
  : c.Expr[InputMolecule_2_20[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T])

  def await_2_21[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]])
  : c.Expr[InputMolecule_2_21[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U])

  def await_2_22[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag, V: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]])
  : c.Expr[InputMolecule_2_22[I1, I2, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U], c.weakTypeOf[V])


  // 3 inputs - multiple outputs

  def await_3_2[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_2[I1, I2, I3, A, B]])
  : c.Expr[InputMolecule_3_2[I1, I2, I3, A, B]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B])

  def await_3_3[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_3[I1, I2, I3, A, B, C]])
  : c.Expr[InputMolecule_3_3[I1, I2, I3, A, B, C]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])

  def await_3_4[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_4[I1, I2, I3, A, B, C, D]])
  : c.Expr[InputMolecule_3_4[I1, I2, I3, A, B, C, D]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D])

  def await_3_5[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_5[I1, I2, I3, A, B, C, D, E]])
  : c.Expr[InputMolecule_3_5[I1, I2, I3, A, B, C, D, E]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E])

  def await_3_6[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_6[I1, I2, I3, A, B, C, D, E, F]])
  : c.Expr[InputMolecule_3_6[I1, I2, I3, A, B, C, D, E, F]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F])

  def await_3_7[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_7[I1, I2, I3, A, B, C, D, E, F, G]])
  : c.Expr[InputMolecule_3_7[I1, I2, I3, A, B, C, D, E, F, G]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G])

  def await_3_8[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]])
  : c.Expr[InputMolecule_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H])

  def await_3_9[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I]])
  : c.Expr[InputMolecule_3_9[I1, I2, I3, A, B, C, D, E, F, G, H, I]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I])

  def await_3_10[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J]])
  : c.Expr[InputMolecule_3_10[I1, I2, I3, A, B, C, D, E, F, G, H, I, J]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J])

  def await_3_11[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K]])
  : c.Expr[InputMolecule_3_11[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K])

  def await_3_12[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L]])
  : c.Expr[InputMolecule_3_12[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L])

  def await_3_13[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M]])
  : c.Expr[InputMolecule_3_13[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M])

  def await_3_14[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N]])
  : c.Expr[InputMolecule_3_14[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N])

  def await_3_15[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]])
  : c.Expr[InputMolecule_3_15[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O])

  def await_3_16[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]])
  : c.Expr[InputMolecule_3_16[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P])

  def await_3_17[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]])
  : c.Expr[InputMolecule_3_17[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q])

  def await_3_18[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]])
  : c.Expr[InputMolecule_3_18[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R])

  def await_3_19[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]])
  : c.Expr[InputMolecule_3_19[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S])

  def await_3_20[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]])
  : c.Expr[InputMolecule_3_20[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T])

  def await_3_21[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]])
  : c.Expr[InputMolecule_3_21[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U])

  def await_3_22[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag, V: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]])
  : c.Expr[InputMolecule_3_22[I1, I2, I3, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U], c.weakTypeOf[V])
}