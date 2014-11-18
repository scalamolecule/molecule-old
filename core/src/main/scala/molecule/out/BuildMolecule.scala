package molecule.out
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.QueryOps._
import molecule.ops.TreeOps
import molecule.transform._
//import scala.collection.JavaConverters._
import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.whitebox.Context

trait BuildMolecule[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = Debug("BuildMolecule", 1, 60, false)
  type KeepQueryOpsWhenFormatting = KeepQueryOps


  def basics(dsl: c.Expr[NS]) = {
    val model0 = Dsl2Model(c)(dsl)

    implicit class Regex(sc: StringContext) {
      def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
    }
    val p = dsl.tree.pos
    val dslTailCode = p.source.lineToString(p.line - 1).substring(p.column)
    val checkCorrectModel = dslTailCode match {
      // todo: lift into quasiquotes and check against resolved `model`
      case r".*[\.|\s]*add.*"    => "check add..."
      case r".*[\.|\s]*insert.*" => "check insert..."
      case r".*[\.|\s]*update.*" => "check update..."
      case _                     => "other..."
    }

    //    x(30, dsl.tree, model0, checkCorrectModel)

    def mapIdentifiers(elements: Seq[Element], identifiers: Seq[(String, Tree)] = Seq()): Seq[(String, Tree)] = {
      val newIdentifiers = (elements collect {
        case atom@Atom(_, _, _, _, Eq(Seq(ident: String)), _) if ident.startsWith("__ident__") => Seq(ident -> q"${TermName(ident.substring(9))}")
        case meta@Meta(_, _, _, Eq(Seq(ident: String))) if ident.startsWith("__ident__")       => Seq(ident -> q"${TermName(ident.substring(9))}")
        case Group(_, nestedElements)                                                          => mapIdentifiers(nestedElements, identifiers)
      }).flatten
      (identifiers ++ newIdentifiers).distinct
    }
    val identifiers = mapIdentifiers(model0.elements).toMap

    q"""
      import molecule.ast.model._
      import molecule.ast.query._
      import molecule.ops.QueryOps._
      import molecule.out._
      import molecule.transform.Model2Query
      import molecule.transform.Model2Transaction._
      import scala.collection.JavaConversions._
      import scala.collection.JavaConverters._
      import datomic.Connection
      import shapeless._

      def resolveIdentifiers(elements: Seq[Element]): Seq[Element] = elements map {
        case atom@Atom(_, _, _, _, Eq(Seq(ident: String)), _) if ident.startsWith("__ident__") => atom.copy(value = Eq(Seq($identifiers.get(ident).get)))
        case meta@Meta(_, _, _,    Eq(Seq(ident: String)))    if ident.startsWith("__ident__") => meta.copy(value = Eq(Seq($identifiers.get(ident).get)))
        case Group(ns, nestedElements)                                                         => Group(ns, resolveIdentifiers(nestedElements))
        case other                                                                             => other
      }
      val model = Model(resolveIdentifiers($model0.elements))

      val query = Model2Query(model)

      def debugMolecule(conn: Connection): Unit = {
        val rows = try {
          results(query, conn)
        } catch {
          case e: Throwable => sys.error(e.toString)
        }
        sys.error(
          "\n--------------------------------------------------------------------------\n" +
          ${show(dsl.tree)} + "\n\n" +
          model + "\n\n" +
          query + "\n\n" +
          query.datalog + "\n\n" +
          (if (query.i.rules.isEmpty) "" else query.i.rules.mkString("RULES: [\n ", "\n ", "\n]\n\n")) +
          "OUTPUTS:\n" + rows.toList.zipWithIndex.map(r => (r._2 + 1) + "  " + r._1).mkString("\n") +
          "\n--------------------------------------------------------------------------\n"
        )
      }
    """
  }

  def from0attr(dsl: c.Expr[NS]) = {
    expr( q"""
      ..${
      basics(dsl)
    }
      new Molecule0(model, query) {
        def debug(implicit conn: Connection): Unit = debugMolecule(conn)
      }
    """)
  }

  def from1attr(dsl: c.Expr[NS], A: Type) = {
    val cast = (data: Tree) => if (A <:< typeOf[Set[_]])
      q"$data.get(0).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$A]"
    else
      q"$data.get(0).asInstanceOf[$A]"

    val hlist = (data: Tree) => if (A <:< typeOf[Set[_]])
      q"$data.get(0).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$A] :: HNil"
    else
      q"$data.get(0).asInstanceOf[$A] :: HNil"

    expr( q"""
      ..${
      basics(dsl)
    }
      new Molecule1[$A](model, query) {
        def get(implicit conn: Connection): Seq[$A]         = results(query, conn).toList.map(data => ${
      cast(q"data")
    })
        def hl(implicit conn: Connection) : Seq[$A :: HNil] = results(query, conn).toList.map(data => ${
      hlist(q"data")
    })
        def debug(implicit conn: Connection): Unit          = debugMolecule(conn)
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
      ..${
      basics(dsl)
    }
      new $MoleculeTpe[..$OutTypes](model, query) {
        def get(implicit conn: Connection): Seq[(..$OutTypes)] = results(query, conn).toList.map(data => (..${
      tplValues(q"data")
    }))
        def hl(implicit conn: Connection) : Seq[$HListType]    = results(query, conn).toList.map(data => ${
      hlist(q"data")
    })
        def debug(implicit conn: Connection): Unit             = debugMolecule(conn)
      }
    """)
  }
}


object BuildMolecule {
  def build(c0: Context) = new {val c: c0.type = c0} with BuildMolecule[c0.type]

  // Molecule implementations

  def from0attr[Ns0: c.WeakTypeTag, Ns1[_], In1_0[_], In1_1[_, _]]
  (c: Context)(dsl: c.Expr[Molecule_0[Ns0, Ns1, In1_0, In1_1]])
    (implicit ev1: c.WeakTypeTag[Ns1[_]], ev2: c.WeakTypeTag[In1_0[_]], ev3: c.WeakTypeTag[In1_1[_, _]])
  : c.Expr[Molecule0] =
    build(c).from0attr(dsl)


  def from1attr[Ns1[_], Ns2[_, _], In1_1[_, _], In1_2[_, _, _],
  A: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_1[Ns1, Ns2, In1_1, In1_2, A]])
    (implicit ev1: c.WeakTypeTag[Ns1[_]], ev2: c.WeakTypeTag[Ns2[_, _]], ev3: c.WeakTypeTag[In1_1[_, _]], ev4: c.WeakTypeTag[In1_2[_, _, _]])
  : c.Expr[Molecule1[A]] =
    build(c).from1attr(dsl, c.weakTypeOf[A])


  def from2attr[Ns2[_, _], Ns3[_, _, _], In1_2[_, _, _], In1_3[_, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_2[Ns2, Ns3, In1_2, In1_3, A, B]])
    (implicit ev1: c.WeakTypeTag[Ns2[_, _]], ev2: c.WeakTypeTag[Ns3[_, _, _]], ev3: c.WeakTypeTag[In1_2[_, _, _]], ev4: c.WeakTypeTag[In1_3[_, _, _, _]])
  : c.Expr[Molecule2[A, B]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B])


  def from3attr[Ns3[_, _, _], Ns4[_, _, _, _], In1_3[_, _, _, _], In1_4[_, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_3[Ns3, Ns4, In1_3, In1_4, A, B, C]])
  : c.Expr[Molecule3[A, B, C]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C])


  def from4attr[Ns4[_, _, _, _], Ns5[_, _, _, _, _], In1_4[_, _, _, _, _], In1_5[_, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_4[Ns4, Ns5, In1_4, In1_5, A, B, C, D]])
    (implicit ev1: c.WeakTypeTag[Ns4[_, _, _, _]], ev2: c.WeakTypeTag[Ns5[_, _, _, _, _]], ev3: c.WeakTypeTag[In1_4[_, _, _, _, _]], ev4: c.WeakTypeTag[In1_5[_, _, _, _, _, _]])
  : c.Expr[Molecule4[A, B, C, D]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D])


  def from5attr[Ns5[_, _, _, _, _], Ns6[_, _, _, _, _, _], In1_5[_, _, _, _, _, _], In1_6[_, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_5[Ns5, Ns6, In1_5, In1_6, A, B, C, D, E]])
    (implicit ev1: c.WeakTypeTag[Ns5[_, _, _, _, _]], ev2: c.WeakTypeTag[Ns6[_, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_5[_, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_6[_, _, _, _, _, _, _]])
  : c.Expr[Molecule5[A, B, C, D, E]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E])


  def from6attr[Ns6[_, _, _, _, _, _], Ns7[_, _, _, _, _, _, _], In1_6[_, _, _, _, _, _, _], In1_7[_, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_6[Ns6, Ns7, In1_6, In1_7, A, B, C, D, E, F]])
    (implicit ev1: c.WeakTypeTag[Ns6[_, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns7[_, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_6[_, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_7[_, _, _, _, _, _, _, _]])
  : c.Expr[Molecule6[A, B, C, D, E, F]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F])


  def from7attr[Ns7[_, _, _, _, _, _, _], Ns8[_, _, _, _, _, _, _, _], In1_7[_, _, _, _, _, _, _, _], In1_8[_, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_7[Ns7, Ns8, In1_7, In1_8, A, B, C, D, E, F, G]])
    (implicit ev1: c.WeakTypeTag[Ns7[_, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns8[_, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_7[_, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_8[_, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule7[A, B, C, D, E, F, G]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G])


  def from8attr[Ns8[_, _, _, _, _, _, _, _], Ns9[_, _, _, _, _, _, _, _, _], In1_8[_, _, _, _, _, _, _, _, _], In1_9[_, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_8[Ns8, Ns9, In1_8, In1_9, A, B, C, D, E, F, G, H]])
    (implicit ev1: c.WeakTypeTag[Ns8[_, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns9[_, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_8[_, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_9[_, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule8[A, B, C, D, E, F, G, H]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H])


  def from9attr[Ns9[_, _, _, _, _, _, _, _, _], Ns10[_, _, _, _, _, _, _, _, _, _], In1_9[_, _, _, _, _, _, _, _, _, _], In1_10[_, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_9[Ns9, Ns10, In1_9, In1_10, A, B, C, D, E, F, G, H, I]])
    (implicit ev1: c.WeakTypeTag[Ns9[_, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns10[_, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_9[_, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_10[_, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule9[A, B, C, D, E, F, G, H, I]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I])


  def from10attr[Ns10[_, _, _, _, _, _, _, _, _, _], Ns11[_, _, _, _, _, _, _, _, _, _, _], In1_10[_, _, _, _, _, _, _, _, _, _, _], In1_11[_, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_10[Ns10, Ns11, In1_10, In1_11, A, B, C, D, E, F, G, H, I, J]])
    (implicit ev1: c.WeakTypeTag[Ns10[_, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns11[_, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_10[_, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_11[_, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule10[A, B, C, D, E, F, G, H, I, J]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J])

  def from11attr[Ns11[_, _, _, _, _, _, _, _, _, _, _], Ns12[_, _, _, _, _, _, _, _, _, _, _, _], In1_11[_, _, _, _, _, _, _, _, _, _, _, _], In1_12[_, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_11[Ns11, Ns12, In1_11, In1_12, A, B, C, D, E, F, G, H, I, J, K]])
    (implicit ev1: c.WeakTypeTag[Ns11[_, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns12[_, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_11[_, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_12[_, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule11[A, B, C, D, E, F, G, H, I, J, K]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K])


  def from12attr[Ns12[_, _, _, _, _, _, _, _, _, _, _, _], Ns13[_, _, _, _, _, _, _, _, _, _, _, _, _], In1_12[_, _, _, _, _, _, _, _, _, _, _, _, _], In1_13[_, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_12[Ns12, Ns13, In1_12, In1_13, A, B, C, D, E, F, G, H, I, J, K, L]])
    (implicit ev1: c.WeakTypeTag[Ns12[_, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns13[_, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_12[_, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_13[_, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule12[A, B, C, D, E, F, G, H, I, J, K, L]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L])


  def from13attr[Ns13[_, _, _, _, _, _, _, _, _, _, _, _, _], Ns14[_, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_13[_, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_14[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_13[Ns13, Ns14, In1_13, In1_14, A, B, C, D, E, F, G, H, I, J, K, L, M]])
    (implicit ev1: c.WeakTypeTag[Ns13[_, _, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns14[_, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_13[_, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_14[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M])


  def from14attr[Ns14[_, _, _, _, _, _, _, _, _, _, _, _, _, _], Ns15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_14[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_14[Ns14, Ns15, In1_14, In1_15, A, B, C, D, E, F, G, H, I, J, K, L, M, N]])
    (implicit ev1: c.WeakTypeTag[Ns14[_, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_14[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N])


  def from15attr[Ns15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _], Ns16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_15[Ns15, Ns16, In1_15, In1_16, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]])
    (implicit ev1: c.WeakTypeTag[Ns15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O])


  def from16attr[Ns16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], Ns17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_16[Ns16, Ns17, In1_16, In1_17, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]])
    (implicit ev1: c.WeakTypeTag[Ns16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P])


  def from17attr[Ns17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], Ns18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_17[Ns17, Ns18, In1_17, In1_18, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]])
    (implicit ev1: c.WeakTypeTag[Ns17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q])


  def from18attr[Ns18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], Ns19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_18[Ns18, Ns19, In1_18, In1_19, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]])
    (implicit ev1: c.WeakTypeTag[Ns18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R])


  def from19attr[Ns19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], Ns20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_19[Ns19, Ns20, In1_19, In1_20, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]])
    (implicit ev1: c.WeakTypeTag[Ns19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S])


  def from20attr[Ns20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], Ns21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_20[Ns20, Ns21, In1_20, In1_21, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]])
    (implicit ev1: c.WeakTypeTag[Ns20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T])


  def from21attr[Ns21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], Ns22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_21[Ns21, Ns22, In1_21, In1_22, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]])
    (implicit ev1: c.WeakTypeTag[Ns21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U])


  def from22attr[Ns22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], Ns23[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _], In1_23[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _],
  A: c.WeakTypeTag, B: c.WeakTypeTag, C: c.WeakTypeTag, D: c.WeakTypeTag, E: c.WeakTypeTag, F: c.WeakTypeTag, G: c.WeakTypeTag, H: c.WeakTypeTag, I: c.WeakTypeTag, J: c.WeakTypeTag, K: c.WeakTypeTag, L: c.WeakTypeTag, M: c.WeakTypeTag, N: c.WeakTypeTag, O: c.WeakTypeTag, P: c.WeakTypeTag, Q: c.WeakTypeTag, R: c.WeakTypeTag, S: c.WeakTypeTag, T: c.WeakTypeTag, U: c.WeakTypeTag, V: c.WeakTypeTag]
  (c: Context)(dsl: c.Expr[Molecule_22[Ns22, Ns23, In1_22, In1_23, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]])
    (implicit ev1: c.WeakTypeTag[Ns22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev2: c.WeakTypeTag[Ns23[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev3: c.WeakTypeTag[In1_22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]], ev4: c.WeakTypeTag[In1_23[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]])
  : c.Expr[Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]] =
    build(c).fromXattrs(dsl, c.weakTypeOf[A], c.weakTypeOf[B], c.weakTypeOf[C], c.weakTypeOf[D], c.weakTypeOf[E], c.weakTypeOf[F], c.weakTypeOf[G], c.weakTypeOf[H], c.weakTypeOf[I], c.weakTypeOf[J], c.weakTypeOf[K], c.weakTypeOf[L], c.weakTypeOf[M], c.weakTypeOf[N], c.weakTypeOf[O], c.weakTypeOf[P], c.weakTypeOf[Q], c.weakTypeOf[R], c.weakTypeOf[S], c.weakTypeOf[T], c.weakTypeOf[U], c.weakTypeOf[V])
}