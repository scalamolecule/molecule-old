package molecule.out
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.QueryOps._
import molecule.ops.TreeOps
import molecule.transform._
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.language.higherKinds

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

//  def from0attrSub(dsl: c.Expr[NS]) = {
//    expr( q"""
//      ..${basics(dsl)}
//      new SubMolecule0(model, query) {
//        def ids: Seq[Long] = entityIds(entityQuery)
//
//        override def insertAndConnectTo(parentEid: Long)(implicit conn: Connection): Long = {
//          val currentNs = curNs(model.elements.head)
//          upsert(conn, model :+ SubComponent(currentNs, parentEid)).last
//        }
//      }
//    """)
//  }

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

//  def from1attrSub(dsl: c.Expr[NS], A: Type) = {
//    val cast = (data: Tree) => if (A <:< typeOf[Set[_]])
//      q"$data.get(0).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$A]"
//    else
//      q"$data.get(0).asInstanceOf[$A]"
//
//    expr( q"""
//      ..${basics(dsl)}
//      new SubMolecule1[$A](model, query) {
//        def ids: Seq[Long] = entityIds(entityQuery)
//        def get(implicit conn: Connection): Seq[$A] = results(_query, conn).toList.map(data => ${cast(q"data")})
//
//        override def insertAndConnectTo(parentEid: Long)(implicit conn: Connection): Long = {
//          val currentNs = curNs(model.elements.head)
//          upsert(conn, model :+ SubComponent(currentNs, parentEid)).last
//        }
//      }
//    """)
//  }

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

//  def fromXattrsSub(dsl: c.Expr[NS], OutTypes: Type*) = {
//    val tplValues = (data: Tree) => OutTypes.zipWithIndex.map {
//      case (t, i) if t <:< typeOf[Set[_]] => q"$data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t]"
//      case (t, i)                         => q"$data.get($i).asInstanceOf[$t]"
//    }
//    val HListType = OutTypes.foldRight(tq"HNil": Tree)((t, tpe) => tq"::[$t, $tpe]")
//    val hlist = (data: Tree) => OutTypes.zipWithIndex.foldRight(q"shapeless.HList()": Tree) {
//      case ((t, i), hl) if t <:< typeOf[Set[_]] => q"$hl.::($data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t])"
//      case ((t, i), hl)                         => q"$hl.::($data.get($i).asInstanceOf[$t])"
//    }
//    val SubMoleculeTpe = nodeMolecule_o(OutTypes.size)
//
//    expr( q"""
//      ..${basics(dsl)}
//      new $SubMoleculeTpe[..$OutTypes](model, query) {
//        def ids: Seq[Long] = entityIds(entityQuery)
//        def tpls(implicit conn: Connection): Seq[(..$OutTypes)] = results(_query, conn).toList.map(data => (..${tplValues(q"data")}))
//        def hls(implicit conn: Connection): Seq[$HListType]     = results(_query, conn).toList.map(data => ${hlist(q"data")})
//
//        override def insertAndConnectTo(parentEid: Long)(implicit conn: Connection): Long = {
//          val currentNs = curNs(model.elements.head)
//          upsert(conn, model :+ SubComponent(currentNs, parentEid)).last
//        }
//      }
//    """)
//  }
}


object BuildMolecule {
  def build(c0: Context) = new {val c: c0.type = c0} with BuildMolecule[c0.type]

  // Molecule implementations

  def from0attr[Ns1: c.WeakTypeTag, Ns2[_]]
  (c: Context)(dsl: c.Expr[Molecule_0[Ns1, Ns2]])
    (implicit ev1: c.WeakTypeTag[Ns2[_]])
  : c.Expr[Molecule0] =
    build(c).from0attr(dsl)

  def from1attr[Ns1[_],  Ns2[_, _], A: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_1[Ns1, Ns2, A]])
    (implicit ev1: c.WeakTypeTag[Ns1[_]], ev2: c.WeakTypeTag[Ns2[_,_]])
  : c.Expr[Molecule1[A]] =
    build(c).from1attr(dsl, c.weakTypeOf[A])

  def from2attr[Ns2[_,_], Ns3[_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_2[Ns2, Ns3, A, B]])
    (implicit ev1: c.WeakTypeTag[Ns2[_,_]], ev2: c.WeakTypeTag[Ns3[_,_,_]])
  : c.Expr[Molecule2[A, B]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B])

  def from3attr[Ns3[_,_,_], Ns4[_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_3[Ns3, Ns4, A, B, C]])
    (implicit ev1: c.WeakTypeTag[Ns3[_,_,_]], ev2: c.WeakTypeTag[Ns4[_,_,_,_]])
  : c.Expr[Molecule3[A, B, C]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])

  def from4attr[Ns4[_,_,_,_], Ns5[_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_4[Ns4, Ns5, A, B, C, D]])
    (implicit ev1: c.WeakTypeTag[Ns4[_,_,_,_]], ev2: c.WeakTypeTag[Ns5[_,_,_,_,_]])
  : c.Expr[Molecule4[A, B, C, D]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D])

  def from5attr[Ns5[_,_,_,_,_], Ns6[_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_5[Ns5, Ns6, A, B, C, D, E]])
    (implicit ev1: c.WeakTypeTag[Ns5[_,_,_,_,_]], ev2: c.WeakTypeTag[Ns6[_,_,_,_,_,_]])
  : c.Expr[Molecule5[A, B, C, D, E]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E])

  def from6attr[Ns6[_,_,_,_,_,_], Ns7[_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_6[Ns6, Ns7, A, B, C, D, E, F]])
    (implicit ev1: c.WeakTypeTag[Ns6[_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns7[_,_,_,_,_,_,_]])
  : c.Expr[Molecule6[A, B, C, D, E, F]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F])

  def from7attr[Ns7[_,_,_,_,_,_,_], Ns8[_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_7[Ns7, Ns8, A, B, C, D, E, F, G]])
    (implicit ev1: c.WeakTypeTag[Ns7[_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns8[_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule7[A, B, C, D, E, F, G]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G])

  def from8attr[Ns8[_,_,_,_,_,_,_,_], Ns9[_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_8[Ns8, Ns9, A, B, C, D, E, F, G, H]])
    (implicit ev1: c.WeakTypeTag[Ns8[_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns9[_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule8[A, B, C, D, E, F, G, H]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H])

  def from9attr[Ns9[_,_,_,_,_,_,_,_,_], Ns10[_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_9[Ns9, Ns10, A, B, C, D, E, F, G, H, I]])
    (implicit ev1: c.WeakTypeTag[Ns9[_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns10[_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule9[A, B, C, D, E, F, G, H, I]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I])

  def from10attr[Ns10[_,_,_,_,_,_,_,_,_,_], Ns11[_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_10[Ns10, Ns11, A, B, C, D, E, F, G, H, I, J]])
    (implicit ev1: c.WeakTypeTag[Ns10[_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns11[_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule10[A, B, C, D, E, F, G, H, I, J]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J])

  def from11attr[Ns11[_,_,_,_,_,_,_,_,_,_,_], Ns12[_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_11[Ns11, Ns12, A, B, C, D, E, F, G, H, I, J, K]])
    (implicit ev1: c.WeakTypeTag[Ns11[_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns12[_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule11[A, B, C, D, E, F, G, H, I, J, K]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K])

  def from12attr[Ns12[_,_,_,_,_,_,_,_,_,_,_,_], Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_12[Ns12, Ns13, A, B, C, D, E, F, G, H, I, J, K, L]])
    (implicit ev1: c.WeakTypeTag[Ns12[_,_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule12[A, B, C, D, E, F, G, H, I, J, K, L]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L])

  def from13attr[Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_], Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_13[Ns13, Ns14, A, B, C, D, E, F, G, H, I, J, K, L, M]])
    (implicit ev1: c.WeakTypeTag[Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M])

  def from14attr[Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_14[Ns14, Ns15, A, B, C, D, E, F, G, H, I, J, K, L, M, N]])
    (implicit ev1: c.WeakTypeTag[Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N])

  def from15attr[Ns15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_15[Ns15, Ns16, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]])
    (implicit ev1: c.WeakTypeTag[Ns15[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O])

  def from16attr[Ns16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_16[Ns16, Ns17, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]])
    (implicit ev1: c.WeakTypeTag[Ns16[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P])

  def from17attr[Ns17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_17[Ns17, Ns18, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]])
    (implicit ev1: c.WeakTypeTag[Ns17[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q])

  def from18attr[Ns18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_18[Ns18, Ns19, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]])
    (implicit ev1: c.WeakTypeTag[Ns18[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R])

  def from19attr[Ns19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_19[Ns19, Ns20, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]])
    (implicit ev1: c.WeakTypeTag[Ns19[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S])

  def from20attr[Ns20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_20[Ns20, Ns21, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]])
    (implicit ev1: c.WeakTypeTag[Ns20[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T])

  def from21attr[Ns21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Ns22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_21[Ns21, Ns22, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]])
    (implicit ev1: c.WeakTypeTag[Ns21[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]], ev2: c.WeakTypeTag[Ns22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U])

  def from22attr[Ns22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], Nothing, A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag, V: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_22[Ns22, Nothing, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]])
    (implicit ev1: c.WeakTypeTag[Ns22[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]])
  : c.Expr[Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U], c.weakTypeOf[V])


//  // Sub Molecule implementations
//
//  def from0attrSub
//  (c: Context)(dsl: c.Expr[SubMolecule_0])
//  : c.Expr[SubMolecule0] =
//    build(c).from0attrSub(dsl)
//
//  def from1attrSub[A: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_1[A]])
//  : c.Expr[SubMolecule1[A]] =
//    build(c).from1attrSub(dsl, c.weakTypeOf[A])
//
//  def from2attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_2[A, B]])
//  : c.Expr[SubMolecule2[A, B]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B])
//
//  def from3attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_3[A, B, C]])
//  : c.Expr[SubMolecule3[A, B, C]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])
//
//  def from4attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_4[A, B, C, D]])
//  : c.Expr[SubMolecule4[A, B, C, D]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D])
//
//  def from5attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_5[A, B, C, D, E]])
//  : c.Expr[SubMolecule5[A, B, C, D, E]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E])
//
//  def from6attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_6[A, B, C, D, E, F]])
//  : c.Expr[SubMolecule6[A, B, C, D, E, F]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F])
//
//  def from7attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_7[A, B, C, D, E, F, G]])
//  : c.Expr[SubMolecule7[A, B, C, D, E, F, G]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G])
//
//  def from8attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_8[A, B, C, D, E, F, G, H]])
//  : c.Expr[SubMolecule8[A, B, C, D, E, F, G, H]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H])
//
//  def from9attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_9[A, B, C, D, E, F, G, H, I]])
//  : c.Expr[SubMolecule9[A, B, C, D, E, F, G, H, I]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I])
//
//  def from10attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_10[A, B, C, D, E, F, G, H, I, J]])
//  : c.Expr[SubMolecule10[A, B, C, D, E, F, G, H, I, J]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J])
//
//  def from11attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_11[A, B, C, D, E, F, G, H, I, J, K]])
//  : c.Expr[SubMolecule11[A, B, C, D, E, F, G, H, I, J, K]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K])
//
//  def from12attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_12[A, B, C, D, E, F, G, H, I, J, K, L]])
//  : c.Expr[SubMolecule12[A, B, C, D, E, F, G, H, I, J, K, L]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L])
//
//  def from13attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_13[A, B, C, D, E, F, G, H, I, J, K, L, M]])
//  : c.Expr[SubMolecule13[A, B, C, D, E, F, G, H, I, J, K, L, M]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M])
//
//  def from14attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]])
//  : c.Expr[SubMolecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N])
//
//  def from15attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]])
//  : c.Expr[SubMolecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O])
//
//  def from16attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]])
//  : c.Expr[SubMolecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P])
//
//  def from17attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]])
//  : c.Expr[SubMolecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q])
//
//  def from18attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]])
//  : c.Expr[SubMolecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R])
//
//  def from19attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]])
//  : c.Expr[SubMolecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S])
//
//  def from20attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]])
//  : c.Expr[SubMolecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T])
//
//  def from21attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]])
//  : c.Expr[SubMolecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U])
//
//  def from22attrSub[A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag, V: c.WeakTypeTag]
//  (c: Context)(dsl: c.Expr[SubMolecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]])
//  : c.Expr[SubMolecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]] =
//    build(c).fromXattrsSub(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U], c.weakTypeOf[V])

}