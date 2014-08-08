package molecule.out
import java.util.{Date => jDate}
import datomic.{Connection => Cnx}
import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.query.Query
import molecule.dsl.schemaDSL.NS
import shapeless.{::, HNil}


// Molecule arities

trait Molecule_0 extends NS
trait Molecule_1[A] extends NS
trait Molecule_2[A, B] extends NS
trait Molecule_3[A, B, C] extends NS
trait Molecule_4[A, B, C, D] extends NS
trait Molecule_5[A, B, C, D, E] extends NS
trait Molecule_6[A, B, C, D, E, F] extends NS
trait Molecule_7[A, B, C, D, E, F, G] extends NS
trait Molecule_8[A, B, C, D, E, F, G, H] extends NS
trait Molecule_9[A, B, C, D, E, F, G, H, I] extends NS
trait Molecule_10[A, B, C, D, E, F, G, H, I, J] extends NS
trait Molecule_11[A, B, C, D, E, F, G, H, I, J, K] extends NS
trait Molecule_12[A, B, C, D, E, F, G, H, I, J, K, L] extends NS
trait Molecule_13[A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS
trait Molecule_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS
trait Molecule_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS
trait Molecule_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS
trait Molecule_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS
trait Molecule_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS
trait Molecule_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS
trait Molecule_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS
trait Molecule_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS
trait Molecule_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS


// Molecule interfaces

trait Molecule extends DatomicFacade {
   val _model: Model
   val _query: Query

  override def toString: String = _query.toList
  def p = _query.pretty
  def ids: Seq[Long]
  def size: Int = ids.size

  // Kind of a hack...?
  def asOf(date: jDate) = { dbOp = AsOf(date); this }
  def since(date: jDate) = { dbOp = Since(date); this }
  def imagine(tx: java.util.List[Object]) = { dbOp = Imagine(tx); this }
}

abstract class Molecule0(val _model: Model, val _query: Query) extends Molecule {
//  def save2
  def insert(implicit conn: Cnx): Seq[Long] = upsert(conn, _model)
}

abstract class Molecule1[A](val _model: Model, val _query: Query) extends Molecule {
  def get         (implicit conn: Cnx): Seq[A]
  def take(n: Int)(implicit conn: Cnx): Seq[A] = get(conn).take(n)
  object insert {
    def apply(a: A)               (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a)))
    def apply(a: A, a2: A, ax: A*)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, (Seq(a, a2) ++ ax.toSeq).map(Seq(_)))
    def apply(data: Seq[A])       (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(Seq(_)))
  }
}

abstract class Molecule2[A, B](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B)                       (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b)))
    def apply(data: A :: B :: HNil)             (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B)], hack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2)))
    def apply(data: Seq[A :: B :: HNil])        (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule3[A, B, C](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C)                    (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c)))
    def apply(data: A :: B :: C :: HNil)           (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C)], hack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3)))
    def apply(data: Seq[A :: B :: C :: HNil])      (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule4[A, B, C, D](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D)                 (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d)))
    def apply(data: A :: B :: C :: D :: HNil)         (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D)], hack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    def apply(data: Seq[A :: B :: C :: D :: HNil])    (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule5[A, B, C, D, E](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E)              (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e)))
    def apply(data: A :: B :: C :: D :: E :: HNil)       (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E)], hack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    def apply(data: Seq[A :: B :: C :: D :: E :: HNil])  (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule6[A, B, C, D, E, F](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F)           (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f)))
    def apply(data: A :: B :: C :: D :: E :: F :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F)], hack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule7[A, B, C, D, E, F, G](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)          (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G)], hack: Int = 42)  (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule8[A, B, C, D, E, F, G, H](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)         (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H)], hack: Int = 42)    (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule9[A, B, C, D, E, F, G, H, I](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)        (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I)], hack: Int = 42)      (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule10[A, B, C, D, E, F, G, H, I, J](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)       (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J)], hack: Int = 42)        (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule11[A, B, C, D, E, F, G, H, I, J, K](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)      (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K)], hack: Int = 42)          (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule12[A, B, C, D, E, F, G, H, I, J, K, L](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L)], hack: Int = 42)            (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)    (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)], hack: Int = 42)              (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)   (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)], hack: Int = 42)                (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)  (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)], hack: Int = 42)                  (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P) (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)], hack: Int = 42)                    (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil)     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)], hack: Int = 42)                      (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil)      (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)], hack: Int = 42)                         (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil]) (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil)       (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)], hack: Int = 42)                            (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil])  (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil)        (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)], hack: Int = 42)                               (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil])   (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil)         (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)], hack: Int = 42)                                  (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil])    (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}

abstract class Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](val _model: Model, val _query: Query) extends Molecule {
  def tpls        (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)]
  def tpl(n: Int) (implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)] = tpls(conn).take(n)
  def hls         (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil]
  def hl(n: Int)  (implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil] = hls.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil)          (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)], hack: Int = 42)                                     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil])     (implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
  }
}
