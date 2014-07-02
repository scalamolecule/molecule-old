package molecule
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import molecule.ast.model._
import molecule.ast.query._
import molecule.ast.schemaDSL._
import molecule.ops.QueryOps._
import molecule.ops.TreeOps
import molecule.transform._

trait BuildInputMolecule[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = debug("BuildInputMolecule", 1, 60)
  type KeepQueryOpsWhenFormatting = KeepQueryOps

  val imports = q"""
      import molecule.ast.query._
      import molecule.ast.model._
      import molecule.transform.Model2Transaction._
      import molecule.db.DatomicFacade._
      import shapeless._
      import scala.collection.JavaConversions._
      import scala.collection.JavaConverters._
      import datomic.Connection
      """

  def await_in_x_out_0(inputDsl: Expr[NS], inTypes: Type*) = {
    val model = Dsl2Model(c)(inputDsl)
    val query = Model2Query(model)
    val InputMolecule_i_o = inputMolecule_i_o(inTypes.size, 1)
    val inputTypes = if (inTypes.length == 1) tq"Seq[..$inTypes]" else tq"Seq[(..$inTypes)]"
    expr( q"""
      ..$imports
      new $InputMolecule_i_o[..$inTypes]($model, $query) {
        def apply(values: $inputTypes)(implicit conn: Connection): Molecule with Out0 = {
          val (query1, entityQuery) = bindValues(values)
          new Molecule(model, query1) with Out0 {
            def ids: Seq[Long] = entityIds(entityQuery)(conn)
          }
        }
      }
      """)
  }

  def await_in_x_out_1(inputDsl: Expr[NS], A: Type, inTypes: Type*) = {
    val model = Dsl2Model(c)(inputDsl)
    val query = Model2Query(model)
    val InputMolecule_i_o = inputMolecule_i_o(inTypes.size, 1)

    val cast = (data: Tree) => if (A <:< typeOf[Set[_]])
      q"$data.get(0).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$A]"
    else
      q"$data.get(0).asInstanceOf[$A]"

    val inputTypes = if (inTypes.length == 1) tq"Seq[..$inTypes]" else tq"Seq[(..$inTypes)]"

    val bindValues2 = if (inTypes.size > 1) {
      val (inTerms, inParams) = inTypes.zipWithIndex.map { case (t, i) =>
        val inTerm = TermName(s"in$i")
        val inType = tq"Seq[$t]"
        (inTerm, q"$inTerm: $inType")
      }.unzip
      q"""
        def apply(..$inParams)(implicit conn: Connection): Molecule with Out1[$A] = {
          val (query1, entityQuery) = bindValues2(..$inTerms)
          new Molecule(model, query1) with Out1[$A] {
            def ids: Seq[Long] = entityIds(entityQuery)
            def get(implicit conn: Connection): Seq[$A] = results(q, conn).toList.map(data => ${cast(q"data")})
          }
        }
      """
    } else q""

    expr( q"""
      ..$imports
      new $InputMolecule_i_o[..$inTypes, $A]($model, $query) {
        def apply(values: $inputTypes)(implicit conn: Connection): Molecule with Out1[$A] = {
          val (query1, entityQuery) = bindValues(values)
          new Molecule(model, query1) with Out1[$A] {
            def ids: Seq[Long] = entityIds(entityQuery)(conn)
            def get(implicit conn: Connection): Seq[$A] = results(q, conn).toList.map(data => ${cast(q"data")})
          }
        }
        $bindValues2
      }
      """)
  }

  def await(inputDsl: Expr[NS], inTypes: Type*)(outTypes: Type*) = {
    val model = Dsl2Model(c)(inputDsl)
    val query = Model2Query(model)
    val entityQuery = query.copy(find = Find(Seq(Var("ent", "Long"))))
    val InputMolecule_i_o = inputMolecule_i_o(inTypes.size, outTypes.size)
    val OutX = dataX(outTypes)

    val tplValues = (data: Tree) => outTypes.zipWithIndex.map {
      case (t, i) if t <:< typeOf[Set[_]] => q"$data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t]"
      case (t, i)                         => q"$data.get($i).asInstanceOf[$t]"
    }

    val hlType = outTypes.foldRight(tq"HNil": Tree)((t, tpe) => tq"::[$t, $tpe]")
    val hlist = (data: Tree) => outTypes.zipWithIndex.foldRight(q"shapeless.HList()": Tree) {
      case ((t, i), hl) if t <:< typeOf[Set[_]] => q"$hl.::($data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t])"
      case ((t, i), hl)                         => q"$hl.::($data.get($i).asInstanceOf[$t])"
    }

    val bindValues2 = if (inTypes.size > 1) {
      val (inTerms, inParams) = inTypes.zipWithIndex.map { case (t, i) =>
        val inTerm = TermName(s"in$i")
        val inType = tq"Seq[$t]"
        (inTerm, q"$inTerm: $inType")
      }.unzip
      q"""
        def apply(..$inParams)(implicit conn: Connection): Molecule with $OutX = {
          val (query1, entityQuery) = bindValues2(..$inTerms)
          new Molecule(model, query1) with $OutX {
            override def ids: Seq[Long] = entityIds(entityQuery)
            def tpls(implicit conn: Connection): Seq[(..$outTypes)] = results(q, conn).toList.map(data => (..${tplValues(q"data")}))
            def hls(implicit conn: Connection): Seq[$hlType]        = results(q, conn).toList.map(data => ${hlist(q"data")})
          }
        }
      """
    } else q""

    expr( q"""
      ..$imports
      new $InputMolecule_i_o[..$inTypes, ..$outTypes]($model, $query) {
        def apply(values: Seq[(..$inTypes)])(implicit conn: Connection): Molecule with $OutX = {
          val (query1, entityQuery) = bindValues(values)
          new Molecule(model, query1) with $OutX {
            override def ids: Seq[Long] = entityIds($entityQuery)
            def tpls(implicit conn: Connection): Seq[(..$outTypes)] = results(q, conn).toList.map(data => (..${tplValues(q"data")}))
            def hls(implicit conn: Connection): Seq[$hlType]        = results(q, conn).toList.map(data => ${hlist(q"data")})
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

  // todo ..more arities


  // 2 inputs - multiple outputs

  def await_2_2[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_2[I1, I2, A, B]])
  : c.Expr[InputMolecule_2_2[I1, I2, A, B]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B])

  def await_2_3[I1: c.WeakTypeTag, I2: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_2_3[I1, I2, A, B, C]])
  : c.Expr[InputMolecule_2_3[I1, I2, A, B, C]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])

  // todo ..more arities


  // 3 inputs - multiple outputs

  def await_3_2[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_2[I1, I2, I3, A, B]])
  : c.Expr[InputMolecule_3_2[I1, I2, I3, A, B]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B])

  def await_3_3[I1: c.WeakTypeTag, I2: c.WeakTypeTag, I3: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
  (c: Context)(inputDsl: c.Expr[In_3_3[I1, I2, I3, A, B, C]])
  : c.Expr[InputMolecule_3_3[I1, I2, I3, A, B, C]] =
    inst(c).await(inputDsl, c.weakTypeOf[I1], c.weakTypeOf[I2], c.weakTypeOf[I3])(c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])

  // todo ..more arities
}


