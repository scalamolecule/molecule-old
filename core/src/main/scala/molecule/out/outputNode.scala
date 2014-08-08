package molecule.out
//import java.util.{Date => jDate}
import datomic.{Connection => Cnx}
//import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.query.Query
import molecule.dsl.schemaDSL.NS
import shapeless.{::, HNil}


// Node Molecule arities

trait NodeMolecule_0 extends NS
trait NodeMolecule_1[A] extends NS
trait NodeMolecule_2[A, B] extends NS
trait NodeMolecule_3[A, B, C] extends NS
trait NodeMolecule_4[A, B, C, D] extends NS
trait NodeMolecule_5[A, B, C, D, E] extends NS
trait NodeMolecule_6[A, B, C, D, E, F] extends NS
trait NodeMolecule_7[A, B, C, D, E, F, G] extends NS
trait NodeMolecule_8[A, B, C, D, E, F, G, H] extends NS
trait NodeMolecule_9[A, B, C, D, E, F, G, H, I] extends NS
trait NodeMolecule_10[A, B, C, D, E, F, G, H, I, J] extends NS
trait NodeMolecule_11[A, B, C, D, E, F, G, H, I, J, K] extends NS
trait NodeMolecule_12[A, B, C, D, E, F, G, H, I, J, K, L] extends NS
trait NodeMolecule_13[A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS
trait NodeMolecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS
trait NodeMolecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS
trait NodeMolecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS
trait NodeMolecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS
trait NodeMolecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS
trait NodeMolecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS
trait NodeMolecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS
trait NodeMolecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS
trait NodeMolecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS


// Node Molecule interfaces

trait NodeMolecule {
  def insertAndConnectTo(otherEid: Long)(implicit conn: Cnx): Long = ???
}

abstract class NodeMolecule0(_model: Model, _query: Query) extends Molecule0(_model, _query) with NodeMolecule {
//  def save2
//  def insert(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model)
}

abstract class NodeMolecule1[A](_model: Model, _query: Query) extends Molecule1[A](_model, _query) with NodeMolecule {

  object insertNode {
    def apply(a: A)               (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a)))
    def apply(a: A, a2: A, ax: A*)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, (Seq(a, a2) ++ ax.toSeq).map(Seq(_)))
    def apply(data: Seq[A])       (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(Seq(_)))
  }
}

abstract class NodeMolecule2[A, B](_model: Model, _query: Query) extends Molecule2[A, B](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B)                       (otherEid: Long)(implicit conn: Cnx): Long = upsert(conn, _model, Seq(Seq(a, b))).last
    def apply(data: A :: B :: HNil)             (otherEid: Long)(implicit conn: Cnx): Long = upsert(conn, _model, Seq(data.toList)).last
    def apply(data: Seq[(A, B)], hack: Int = 42)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2)))
    def apply(data: Seq[A :: B :: HNil])        (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule3[A, B, C](_model: Model, _query: Query) extends Molecule3[A, B, C](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C)                    (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c)))
    def apply(data: A :: B :: C :: HNil)           (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C)], hack: Int = 42)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3)))
    def apply(data: Seq[A :: B :: C :: HNil])      (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule4[A, B, C, D](_model: Model, _query: Query) extends Molecule4[A, B, C, D](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D)                 (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d)))
    def apply(data: A :: B :: C :: D :: HNil)         (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D)], hack: Int = 42)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    def apply(data: Seq[A :: B :: C :: D :: HNil])    (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule5[A, B, C, D, E](_model: Model, _query: Query) extends Molecule5[A, B, C, D, E](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E)              (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e)))
    def apply(data: A :: B :: C :: D :: E :: HNil)       (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E)], hack: Int = 42)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    def apply(data: Seq[A :: B :: C :: D :: E :: HNil])  (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule6[A, B, C, D, E, F](_model: Model, _query: Query) extends Molecule6[A, B, C, D, E, F](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F)           (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f)))
    def apply(data: A :: B :: C :: D :: E :: F :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F)], hack: Int = 42)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule7[A, B, C, D, E, F, G](_model: Model, _query: Query) extends Molecule7[A, B, C, D, E, F, G](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)          (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G)], hack: Int = 42)  (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule8[A, B, C, D, E, F, G, H](_model: Model, _query: Query) extends Molecule8[A, B, C, D, E, F, G, H](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)         (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H)], hack: Int = 42)    (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule9[A, B, C, D, E, F, G, H, I](_model: Model, _query: Query) extends Molecule9[A, B, C, D, E, F, G, H, I](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)        (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I)], hack: Int = 42)      (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule10[A, B, C, D, E, F, G, H, I, J](_model: Model, _query: Query) extends Molecule10[A, B, C, D, E, F, G, H, I, J](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)       (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J)], hack: Int = 42)        (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule11[A, B, C, D, E, F, G, H, I, J, K](_model: Model, _query: Query) extends Molecule11[A, B, C, D, E, F, G, H, I, J, K](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)      (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K)], hack: Int = 42)          (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule12[A, B, C, D, E, F, G, H, I, J, K, L](_model: Model, _query: Query) extends Molecule12[A, B, C, D, E, F, G, H, I, J, K, L](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L)], hack: Int = 42)            (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule13[A, B, C, D, E, F, G, H, I, J, K, L, M](_model: Model, _query: Query) extends Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)    (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)], hack: Int = 42)              (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N](_model: Model, _query: Query) extends Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)   (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)], hack: Int = 42)                (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](_model: Model, _query: Query) extends Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)  (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)], hack: Int = 42)                  (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](_model: Model, _query: Query) extends Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P) (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)], hack: Int = 42)                    (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](_model: Model, _query: Query) extends Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil)     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)], hack: Int = 42)                      (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil])(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](_model: Model, _query: Query) extends Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil)      (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)], hack: Int = 42)                         (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil]) (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](_model: Model, _query: Query) extends Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil)       (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)], hack: Int = 42)                            (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil])  (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](_model: Model, _query: Query) extends Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil)        (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)], hack: Int = 42)                               (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil])   (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](_model: Model, _query: Query) extends Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil)         (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)], hack: Int = 42)                                  (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil])    (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class NodeMolecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](_model: Model, _query: Query) extends Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](_model, _query) with NodeMolecule {
  object insertNode {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil)          (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)], hack: Int = 42)                                     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil])     (otherEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}
