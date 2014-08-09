package molecule.out
//import java.util.{Date => jDate}
import datomic.{Connection => Cnx}
//import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.query.Query
import molecule.dsl.schemaDSL.NS
import shapeless.{::, HNil}


// Sub Molecule arities

trait SubMolecule_0 extends NS
trait SubMolecule_1[A] extends NS
trait SubMolecule_2[A, B] extends NS
trait SubMolecule_3[A, B, C] extends NS
trait SubMolecule_4[A, B, C, D] extends NS
trait SubMolecule_5[A, B, C, D, E] extends NS
trait SubMolecule_6[A, B, C, D, E, F] extends NS
trait SubMolecule_7[A, B, C, D, E, F, G] extends NS
trait SubMolecule_8[A, B, C, D, E, F, G, H] extends NS
trait SubMolecule_9[A, B, C, D, E, F, G, H, I] extends NS
trait SubMolecule_10[A, B, C, D, E, F, G, H, I, J] extends NS
trait SubMolecule_11[A, B, C, D, E, F, G, H, I, J, K] extends NS
trait SubMolecule_12[A, B, C, D, E, F, G, H, I, J, K, L] extends NS
trait SubMolecule_13[A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS
trait SubMolecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS
trait SubMolecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS
trait SubMolecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS
trait SubMolecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS
trait SubMolecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS
trait SubMolecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS
trait SubMolecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS
trait SubMolecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS
trait SubMolecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS


// Sub Molecule interfaces

trait SubMolecule {
  def insertAndConnectTo(parentEid: Long)(implicit conn: Cnx): Long = ???

//  val model: Model

//  override def insertAndConnectTo(parentEid: Long)(implicit conn: Connection): Long = {
//    val currentNs = curNs(model.elements.head)
//    upsert(conn, model :+ Sub(currentNs, parentEid)).last
//  }

//  def --(other: Molecule): Molecule = ???
}

abstract class SubMolecule0(_model: Model, _query: Query) extends Molecule0(_model, _query) with SubMolecule {
}

abstract class SubMolecule1[A](_model: Model, _query: Query) extends Molecule1[A](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A)               (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a)))
    def apply(a: A, a2: A, ax: A*)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, (Seq(a, a2) ++ ax.toSeq).map(Seq(_)))
    def apply(data: Seq[A])       (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(Seq(_)))
  }
}

abstract class SubMolecule2[A, B](_model: Model, _query: Query) extends Molecule2[A, B](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B)                       (parentEid: Long)(implicit conn: Cnx): Long = upsert(conn, _model, Seq(Seq(a, b))).last
    def apply(data: A :: B :: HNil)             (parentEid: Long)(implicit conn: Cnx): Long = upsert(conn, _model, Seq(data.toList)).last
    def apply(data: Seq[(A, B)], hack: Int = 42)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2)))
    def apply(data: Seq[A :: B :: HNil])        (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule3[A, B, C](_model: Model, _query: Query) extends Molecule3[A, B, C](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C)                    (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c)))
    def apply(data: A :: B :: C :: HNil)           (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C)], hack: Int = 42)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3)))
    def apply(data: Seq[A :: B :: C :: HNil])      (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule4[A, B, C, D](_model: Model, _query: Query) extends Molecule4[A, B, C, D](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D)                 (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d)))
    def apply(data: A :: B :: C :: D :: HNil)         (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D)], hack: Int = 42)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    def apply(data: Seq[A :: B :: C :: D :: HNil])    (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule5[A, B, C, D, E](_model: Model, _query: Query) extends Molecule5[A, B, C, D, E](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E)              (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e)))
    def apply(data: A :: B :: C :: D :: E :: HNil)       (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E)], hack: Int = 42)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    def apply(data: Seq[A :: B :: C :: D :: E :: HNil])  (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule6[A, B, C, D, E, F](_model: Model, _query: Query) extends Molecule6[A, B, C, D, E, F](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F)           (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f)))
    def apply(data: A :: B :: C :: D :: E :: F :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F)], hack: Int = 42)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule7[A, B, C, D, E, F, G](_model: Model, _query: Query) extends Molecule7[A, B, C, D, E, F, G](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)          (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G)], hack: Int = 42)  (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule8[A, B, C, D, E, F, G, H](_model: Model, _query: Query) extends Molecule8[A, B, C, D, E, F, G, H](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)         (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H)], hack: Int = 42)    (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule9[A, B, C, D, E, F, G, H, I](_model: Model, _query: Query) extends Molecule9[A, B, C, D, E, F, G, H, I](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)        (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I)], hack: Int = 42)      (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule10[A, B, C, D, E, F, G, H, I, J](_model: Model, _query: Query) extends Molecule10[A, B, C, D, E, F, G, H, I, J](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)       (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J)], hack: Int = 42)        (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule11[A, B, C, D, E, F, G, H, I, J, K](_model: Model, _query: Query) extends Molecule11[A, B, C, D, E, F, G, H, I, J, K](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)      (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K)], hack: Int = 42)          (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule12[A, B, C, D, E, F, G, H, I, J, K, L](_model: Model, _query: Query) extends Molecule12[A, B, C, D, E, F, G, H, I, J, K, L](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L)], hack: Int = 42)            (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule13[A, B, C, D, E, F, G, H, I, J, K, L, M](_model: Model, _query: Query) extends Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)    (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)], hack: Int = 42)              (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N](_model: Model, _query: Query) extends Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)   (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)], hack: Int = 42)                (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](_model: Model, _query: Query) extends Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)  (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)], hack: Int = 42)                  (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](_model: Model, _query: Query) extends Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P) (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)], hack: Int = 42)                    (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](_model: Model, _query: Query) extends Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil)     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)], hack: Int = 42)                      (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil])(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](_model: Model, _query: Query) extends Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil)      (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)], hack: Int = 42)                         (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil]) (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](_model: Model, _query: Query) extends Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil)       (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)], hack: Int = 42)                            (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil])  (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](_model: Model, _query: Query) extends Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil)        (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)], hack: Int = 42)                               (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil])   (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](_model: Model, _query: Query) extends Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil)         (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)], hack: Int = 42)                                  (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil])    (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class SubMolecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](_model: Model, _query: Query) extends Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](_model, _query) with SubMolecule {
  object insertSub {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil)          (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)], hack: Int = 42)                                     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil])     (parentEid: Long)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}
