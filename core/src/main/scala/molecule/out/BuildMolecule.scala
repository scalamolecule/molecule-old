package molecule.out
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.QueryOps._
import molecule.ops.TreeOps
import molecule.transform._
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait BuildMolecule[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = debug("BuildMolecule", 1, 60, false)
  type KeepQueryOpsWhenFormatting = KeepQueryOps

  val imports = q"""
      import molecule.out._
      import molecule.ast.query._
      import molecule.ast.model._
      import molecule.transform.Model2Transaction._
      import molecule.transform.Model2Query
      import molecule.DatomicFacade._
      import shapeless._
      import scala.collection.JavaConversions._
      import scala.collection.JavaConverters._
      import datomic.Connection
      """

  def basics(dsl: c.Expr[NS]) = {
    val model0 = Dsl2Model(c)(dsl)
    val identifiers = (model0.elements collect {
      case atom@Atom(_, _, _, _, Eq(Seq(ident)), _) if ident.toString.startsWith("__ident__") =>
        ident -> q"${TermName(ident.toString.substring(9))}"
    }).toMap
    //    x(12, model0)
    q"""
      ..$imports
      val model = Model($model0.elements.map {
        case atom@Atom(_, _, _, _, value, _) => value match {
          case Eq(Seq(ident)) if ident.toString.startsWith("__ident__") =>
            atom.copy(value = Eq(Seq($identifiers.get(ident.toString).get)))
          case _ => atom
        }
        case other => other
      })
      val query = Model2Query(model)
      val entityQuery = query.copy(find = Find(Seq(Var("ent", "Long"))))
    """
  }

  def from0attr(dsl: c.Expr[NS]) = {
    expr( q"""
      ..${basics(dsl)}
      new Molecule0(model, query) {
        def ids: Seq[Long] = entityIds(entityQuery)
      }
    """)
  }

  def from0attrNode(dsl: c.Expr[NS]) = {
    expr( q"""
      ..${basics(dsl)}
      new NodeMolecule0(model, query) {
        def ids: Seq[Long] = entityIds(entityQuery)

        override def insertAndConnectTo(otherEid: Long)(implicit conn: Connection): Long = {
          val currentNs = curNs(model.elements.head)
          upsert(conn, model :+ Node(currentNs, otherEid)).last
        }
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
      new Molecule1[$A](model, query) {
        def ids: Seq[Long] = entityIds(entityQuery)
        def get(implicit conn: Connection): Seq[$A] = results(_query, conn).toList.map(data => ${cast(q"data")})
      }
    """)
  }

  def from1attrNode(dsl: c.Expr[NS], A: Type) = {
    val cast = (data: Tree) => if (A <:< typeOf[Set[_]])
      q"$data.get(0).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$A]"
    else
      q"$data.get(0).asInstanceOf[$A]"

    expr( q"""
      ..${basics(dsl)}
      new NodeMolecule1[$A](model, query) {
        def ids: Seq[Long] = entityIds(entityQuery)
        def get(implicit conn: Connection): Seq[$A] = results(_query, conn).toList.map(data => ${cast(q"data")})

        override def insertAndConnectTo(otherEid: Long)(implicit conn: Connection): Long = {
          val currentNs = curNs(model.elements.head)
          upsert(conn, model :+ Node(currentNs, otherEid)).last
        }
      }
    """)
  }

  def fromXattrs(dsl: c.Expr[NS], OutTypes: Type*) = {
    val tplValues = (data: Tree) => OutTypes.zipWithIndex.map {
      case (t, i) if t <:< typeOf[Set[_]] => q"$data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t]"
      case (t, i)                         => q"$data.get($i).asInstanceOf[$t]"
    }
    val HListType = OutTypes.foldRight(tq"HNil": Tree)((t, tpe) => tq"::[$t, $tpe]")
    val hlist = (data: Tree) => OutTypes.zipWithIndex.foldRight(q"shapeless.HList()": Tree) {
      case ((t, i), hl) if t <:< typeOf[Set[_]] => q"$hl.::($data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t])"
      case ((t, i), hl)                         => q"$hl.::($data.get($i).asInstanceOf[$t])"
    }
    val MoleculeTpe = molecule_o(OutTypes.size)

    expr( q"""
      ..${basics(dsl)}
      new $MoleculeTpe[..$OutTypes](model, query) {
        def ids: Seq[Long] = entityIds(entityQuery)
        def tpls(implicit conn: Connection): Seq[(..$OutTypes)] = results(_query, conn).toList.map(data => (..${tplValues(q"data")}))
        def hls(implicit conn: Connection): Seq[$HListType]     = results(_query, conn).toList.map(data => ${hlist(q"data")})
      }
    """)
  }

  def fromXattrsNode(dsl: c.Expr[NS], OutTypes: Type*) = {
    val tplValues = (data: Tree) => OutTypes.zipWithIndex.map {
      case (t, i) if t <:< typeOf[Set[_]] => q"$data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t]"
      case (t, i)                         => q"$data.get($i).asInstanceOf[$t]"
    }
    val HListType = OutTypes.foldRight(tq"HNil": Tree)((t, tpe) => tq"::[$t, $tpe]")
    val hlist = (data: Tree) => OutTypes.zipWithIndex.foldRight(q"shapeless.HList()": Tree) {
      case ((t, i), hl) if t <:< typeOf[Set[_]] => q"$hl.::($data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t])"
      case ((t, i), hl)                         => q"$hl.::($data.get($i).asInstanceOf[$t])"
    }
    val NodeMoleculeTpe = nodeMolecule_o(OutTypes.size)

    expr( q"""
      ..${basics(dsl)}
      new $NodeMoleculeTpe[..$OutTypes](model, query) {
        def ids: Seq[Long] = entityIds(entityQuery)
        def tpls(implicit conn: Connection): Seq[(..$OutTypes)] = results(_query, conn).toList.map(data => (..${tplValues(q"data")}))
        def hls(implicit conn: Connection): Seq[$HListType]     = results(_query, conn).toList.map(data => ${hlist(q"data")})
      }
    """)
  }
}

object BuildMolecule {
  def build(c0: Context) = new {val c: c0.type = c0} with BuildMolecule[c0.type]

  // Molecule implementations

  def from0attr
  (c: Context)(dsl: c.Expr[Molecule_0])
  : c.Expr[Molecule0] =
    build(c).from0attr(dsl)

  def from1attr[A: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_1[A]])
  : c.Expr[Molecule1[A]] =
    build(c).from1attr(dsl, c.weakTypeOf[A])

  def from2attr[A: c.WeakTypeTag, B: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_2[A, B]])
  : c.Expr[Molecule2[A, B]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B])

  def from3attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_3[A, B, C]])
  : c.Expr[Molecule3[A, B, C]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])

  def from4attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_4[A, B, C, D]])
  : c.Expr[Molecule4[A, B, C, D]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D])

  def from5attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_5[A, B, C, D, E]])
  : c.Expr[Molecule5[A, B, C, D, E]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E])

  def from6attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_6[A, B, C, D, E, F]])
  : c.Expr[Molecule6[A, B, C, D, E, F]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F])

  def from7attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_7[A, B, C, D, E, F, G]])
  : c.Expr[Molecule7[A, B, C, D, E, F, G]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G])

  def from8attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_8[A, B, C, D, E, F, G, H]])
  : c.Expr[Molecule8[A, B, C, D, E, F, G, H]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H])

  def from9attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_9[A, B, C, D, E, F, G, H, I]])
  : c.Expr[Molecule9[A, B, C, D, E, F, G, H, I]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I])

  def from10attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_10[A, B, C, D, E, F, G, H, I, J]])
  : c.Expr[Molecule10[A, B, C, D, E, F, G, H, I, J]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J])

  def from11attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_11[A, B, C, D, E, F, G, H, I, J, K]])
  : c.Expr[Molecule11[A, B, C, D, E, F, G, H, I, J, K]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K])

  def from12attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_12[A, B, C, D, E, F, G, H, I, J, K, L]])
  : c.Expr[Molecule12[A, B, C, D, E, F, G, H, I, J, K, L]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L])

  def from13attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, M]])
  : c.Expr[Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M])

  def from14attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]])
  : c.Expr[Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N])

  def from15attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]])
  : c.Expr[Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O])

  def from16attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]])
  : c.Expr[Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P])

  def from17attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]])
  : c.Expr[Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q])

  def from18attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]])
  : c.Expr[Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R])

  def from19attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]])
  : c.Expr[Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S])

  def from20attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]])
  : c.Expr[Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T])

  def from21attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]])
  : c.Expr[Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U])

  def from22attr[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag, V: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]])
  : c.Expr[Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U], c.weakTypeOf[V])


  // Node Molecule implementations

  def from0attrNode
  (c: Context)(dsl: c.Expr[NodeMolecule_0])
  : c.Expr[NodeMolecule0] =
    build(c).from0attrNode(dsl)

  def from1attrNode[A: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_1[A]])
  : c.Expr[NodeMolecule1[A]] =
    build(c).from1attrNode(dsl, c.weakTypeOf[A])

  def from2attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_2[A, B]])
  : c.Expr[NodeMolecule2[A, B]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B])

  def from3attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_3[A, B, C]])
  : c.Expr[NodeMolecule3[A, B, C]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])

  def from4attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_4[A, B, C, D]])
  : c.Expr[NodeMolecule4[A, B, C, D]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D])

  def from5attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_5[A, B, C, D, E]])
  : c.Expr[NodeMolecule5[A, B, C, D, E]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E])

  def from6attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_6[A, B, C, D, E, F]])
  : c.Expr[NodeMolecule6[A, B, C, D, E, F]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F])

  def from7attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_7[A, B, C, D, E, F, G]])
  : c.Expr[NodeMolecule7[A, B, C, D, E, F, G]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G])

  def from8attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_8[A, B, C, D, E, F, G, H]])
  : c.Expr[NodeMolecule8[A, B, C, D, E, F, G, H]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H])

  def from9attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_9[A, B, C, D, E, F, G, H, I]])
  : c.Expr[NodeMolecule9[A, B, C, D, E, F, G, H, I]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I])

  def from10attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_10[A, B, C, D, E, F, G, H, I, J]])
  : c.Expr[NodeMolecule10[A, B, C, D, E, F, G, H, I, J]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J])

  def from11attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_11[A, B, C, D, E, F, G, H, I, J, K]])
  : c.Expr[NodeMolecule11[A, B, C, D, E, F, G, H, I, J, K]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K])

  def from12attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_12[A, B, C, D, E, F, G, H, I, J, K, L]])
  : c.Expr[NodeMolecule12[A, B, C, D, E, F, G, H, I, J, K, L]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L])

  def from13attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_13[A, B, C, D, E, F, G, H, I, J, K, L, M]])
  : c.Expr[NodeMolecule13[A, B, C, D, E, F, G, H, I, J, K, L, M]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M])

  def from14attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]])
  : c.Expr[NodeMolecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N])

  def from15attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]])
  : c.Expr[NodeMolecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O])

  def from16attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]])
  : c.Expr[NodeMolecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P])

  def from17attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]])
  : c.Expr[NodeMolecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q])

  def from18attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]])
  : c.Expr[NodeMolecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R])

  def from19attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]])
  : c.Expr[NodeMolecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S])

  def from20attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]])
  : c.Expr[NodeMolecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T])

  def from21attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]])
  : c.Expr[NodeMolecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U])

  def from22attrNode[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag, V: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[NodeMolecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]])
  : c.Expr[NodeMolecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]] =
    build(c).fromXattrsNode(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U], c.weakTypeOf[V])

}