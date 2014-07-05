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
        def get(implicit conn: Connection): Seq[$A] = results(_query, conn).toList.map(data => ${cast(q"data")})
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
        def tpls(implicit conn: Connection): Seq[(..$outTypes)] = results(_query, conn).toList.map(data => (..${tplValues(q"data")}))
        def hls(implicit conn: Connection): Seq[$hlType]        = results(_query, conn).toList.map(data => ${hlist(q"data")})
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

  def from9attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_9[A, B, C, D, E, F, G, H, I]])
  : c.Expr[Molecule with Out9[A, B, C, D, E, F, G, H, I]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I])

  def from10attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_10[A, B, C, D, E, F, G, H, I, J]])
  : c.Expr[Molecule with Out10[A, B, C, D, E, F, G, H, I, J]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J])

  def from11attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_11[A, B, C, D, E, F, G, H, I, J, K]])
  : c.Expr[Molecule with Out11[A, B, C, D, E, F, G, H, I, J, K]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K])

  def from12attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_12[A, B, C, D, E, F, G, H, I, J, K, L]])
  : c.Expr[Molecule with Out12[A, B, C, D, E, F, G, H, I, J, K, L]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L])

  def from13attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M]])
  : c.Expr[Molecule with Out13[A, B, C, D, E, F, G, H, I, J, K, L, M]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M])

  def from14attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]])
  : c.Expr[Molecule with Out14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N])

  def from15attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]])
  : c.Expr[Molecule with Out15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O])

  def from16attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]])
  : c.Expr[Molecule with Out16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P])

  def from17attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]])
  : c.Expr[Molecule with Out17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q])

  def from18attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]])
  : c.Expr[Molecule with Out18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R])

  def from19attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]])
  : c.Expr[Molecule with Out19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S])

  def from20attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]])
  : c.Expr[Molecule with Out20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T])

  def from21attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]])
  : c.Expr[Molecule with Out21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U])

  def from22attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag, V: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]])
  : c.Expr[Molecule with Out22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U], c.weakTypeOf[V])

}