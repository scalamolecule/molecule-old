package molecule
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import molecule.ast.model._
import molecule.ast.schemaDSL._
import molecule.ops.QueryOps._
import molecule.ops.TreeOps
import molecule.transform._

trait BuildMolecule[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = debug("BuildMolecule", 1, 60, false)
  type KeepQueryOpsWhenFormatting = KeepQueryOps

  val imports = q"""
      import molecule.ast.query._
      import molecule.ast.model._
      import molecule.transform.Model2Transaction._
      import molecule.transform.Model2Query
      import molecule.db.DatomicFacade._
      import shapeless._
      import scala.collection.JavaConversions._
      import scala.collection.JavaConverters._
      import datomic.Connection
      """

  def basics(dsl: c.Expr[NS]) = {
    val model0 = Dsl2Model(c)(dsl)
    val identifiers = (model0.elements collect {
      case atom@Atom(_, _, _, _, Eq(Seq(ident)), _) if ident.startsWith("__ident__") =>
        ident -> q"${TermName(ident.substring(9))}"
    }).toMap
    q"""
      ..$imports
      val model = Model($model0.elements.map {
        case atom@Atom(_, _, _, _, value, _) => value match {
          case Eq(Seq(ident)) if ident.startsWith("__ident__") =>
            atom.copy(value = Eq(Seq($identifiers.get(ident).get.toString)))
          case _ => atom
        }
        case ref => ref
      })
      val query = Model2Query(model)
      val entityQuery = query.copy(find = Find(Seq(Var("ent", "Long"))))
    """
  }

  def from0attr(dsl: c.Expr[NS]) = {
    expr( q"""
      ..${basics(dsl)}
      new Molecule(model, query) with Out0 {
        def ids: Seq[Long] = entityIds(entityQuery)
      }
    """)
  }

  def from1attr(dsl: c.Expr[NS], A: Type) = {
    val cast = (data: Tree) => if (A <:< typeOf[Set[_]])
      q"$data.get(0).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$A]"
    else
      q"$data.get(0).asInstanceOf[$A]"

    expr( q"""
      ..${basics(dsl)}
      new Molecule(model, query) with Out1[$A] {
        def ids: Seq[Long] = entityIds(entityQuery)
        def get(implicit conn: Connection): Seq[$A] = results(q, conn).toList.map(data => ${cast(q"data")})
      }
    """)
  }

  def fromXattrs(dsl: c.Expr[NS], outTypes: Type*) = {
    val tplValues = (data: Tree) => outTypes.zipWithIndex.map {
      case (t, i) if t <:< typeOf[Set[_]] => q"$data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t]"
      case (t, i)                         => q"$data.get($i).asInstanceOf[$t]"
    }
    val hlType = outTypes.foldRight(tq"HNil": Tree)((t, tpe) => tq"::[$t, $tpe]")
    val hlist = (data: Tree) => outTypes.zipWithIndex.foldRight(q"shapeless.HList()": Tree) {
      case ((t, i), hl) if t <:< typeOf[Set[_]] => q"$hl.::($data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t])"
      case ((t, i), hl)                         => q"$hl.::($data.get($i).asInstanceOf[$t])"
    }
    expr( q"""
      ..${basics(dsl)}
      new Molecule(model, query) with ${dataX(outTypes)} {
        def ids: Seq[Long] = entityIds(entityQuery)
        def tpls(implicit conn: Connection): Seq[(..$outTypes)] = results(q, conn).toList.map(data => (..${tplValues(q"data")}))
        def hls(implicit conn: Connection): Seq[$hlType]        = results(q, conn).toList.map(data => ${hlist(q"data")})
      }
    """)
  }
}

object BuildMolecule {
  def build(c0: Context) = new {val c: c0.type = c0} with BuildMolecule[c0.type]

  def from0attr
  (c: Context)(dsl: c.Expr[Out_0])
  : c.Expr[Molecule with Out0] =
    build(c).from0attr(dsl)

  def from1attr[A: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_1[A]])
  : c.Expr[Molecule with Out1[A]] =
    build(c).from1attr(dsl, c.weakTypeOf[A])

  def from2attr[A: c.WeakTypeTag, B: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_2[A, B]])
  : c.Expr[Molecule with Out2[A, B]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B])

  def from3attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_3[A, B, C]])
  : c.Expr[Molecule with Out3[A, B, C]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])

  def from4attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_4[A, B, C, D]])
  : c.Expr[Molecule with Out4[A, B, C, D]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D])

  def from5attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_5[A, B, C, D, E]])
  : c.Expr[Molecule with Out5[A, B, C, D, E]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E])

  def from6attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_6[A, B, C, D, E, F]])
  : c.Expr[Molecule with Out6[A, B, C, D, E, F]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F])

  def from7attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_7[A, B, C, D, E, F, G]])
  : c.Expr[Molecule with Out7[A, B, C, D, E, F, G]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G])

  def from8attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_8[A, B, C, D, E, F, G, H]])
  : c.Expr[Molecule with Out8[A, B, C, D, E, F, G, H]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H])

  // todo: more arities...
}