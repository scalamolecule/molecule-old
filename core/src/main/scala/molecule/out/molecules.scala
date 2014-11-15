package molecule.out
import java.util.{Date, List => jList}
//import datomic.{Connection => Cnx}
import datomic.Connection
import molecule.DatomicFacade
import molecule.Tx
import molecule.ast.model._
import molecule.ast.query.Query
//import molecule.dsl.schemaDSL.NS
import shapeless.{::, HNil}


trait Molecule extends DatomicFacade {
   val _model: Model
   val _query: Query

  override def toString: String = _query.toList
//  def p = _query.pretty
  def ids: Seq[Long]
  def size: Int = ids.size
  
  protected type lObj = java.util.List[Object]
  protected def asOf_   [M <: Molecule](thisMolecule: M, d: Date) = { dbOp = AsOf(txDate(d)); thisMolecule }
  protected def asOf_   [M <: Molecule](thisMolecule: M, l: Long) = { dbOp = AsOf(txLong(l)); thisMolecule }
  protected def asOf_   [M <: Molecule](thisMolecule: M, t: lObj) = { dbOp = AsOf(txlObj(t)); thisMolecule }
  protected def history_[M <: Molecule](thisMolecule: M)          = { dbOp = History        ; thisMolecule }

  def since(date: Date) = { dbOp = Since(date); this }
  def imagine(tx: lObj) = { dbOp = Imagine(tx); this }

  def add                   (implicit conn: Connection): Tx = save(conn, _model)
  def update(updateId: Long)(implicit conn: Connection): Tx = update(conn, _model, updateId)
  def update                (implicit conn: Connection): Tx = insert(conn, _model)

  def debug(implicit conn: Connection): Unit
}

abstract class Molecule0(val _model: Model, val _query: Query) extends Molecule

abstract class Molecule1[A](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[A]
  def get(n: Int)(implicit conn: Connection): Seq[A]    = get(conn).take(n)
  def one        (implicit conn: Connection): A         = get(conn).head
  def some       (implicit conn: Connection): Option[A] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: HNil] = hl.take(n)
  object insert {
//    def apply(a: A)                        (implicit conn: Connection): Tx = upsert(conn, _model, Seq(Seq(a)))
    def apply(a: A, ax: A*)         (implicit conn: Connection): Tx = insert(conn, _model, (a +: ax.toSeq).map(Seq(_)))
//    def apply(a: A, a2: A, ax: A*)         (implicit conn: Connection): Tx = upsert(conn, _model, (Seq(a, a2) ++ ax.toSeq).map(Seq(_)))
    def apply(data: A :: HNil)             (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[A], hack: Int = 42)(implicit conn: Connection): Tx = insert(conn, _model, data.map(Seq(_)))
    def apply(data: Seq[A :: HNil])        (implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule2[A, B](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B)                       (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b)))
    def apply(data: A :: B :: HNil)             (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B)], hack: Int = 42)(implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2)))
    def apply(data: Seq[A :: B :: HNil])        (implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule3[A, B, C](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C)                    (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c)))
    def apply(data: A :: B :: C :: HNil)           (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C)], hack: Int = 42)(implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3)))
    def apply(data: Seq[A :: B :: C :: HNil])      (implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule4[A, B, C, D](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D)                 (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d)))
    def apply(data: A :: B :: C :: D :: HNil)         (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D)], hack: Int = 42)(implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4)))
    def apply(data: Seq[A :: B :: C :: D :: HNil])    (implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule5[A, B, C, D, E](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E)              (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e)))
    def apply(data: A :: B :: C :: D :: E :: HNil)       (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E)], hack: Int = 42)(implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
    def apply(data: Seq[A :: B :: C :: D :: E :: HNil])  (implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule6[A, B, C, D, E, F](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F)           (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f)))
    def apply(data: A :: B :: C :: D :: E :: F :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F)], hack: Int = 42)(implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule7[A, B, C, D, E, F, G](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G)          (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G)], hack: Int = 42)  (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule8[A, B, C, D, E, F, G, H](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)         (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H)], hack: Int = 42)    (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule9[A, B, C, D, E, F, G, H, I](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)        (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I)], hack: Int = 42)      (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule10[A, B, C, D, E, F, G, H, I, J](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)       (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J)], hack: Int = 42)        (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule11[A, B, C, D, E, F, G, H, I, J, K](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)      (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K)], hack: Int = 42)          (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule12[A, B, C, D, E, F, G, H, I, J, K, L](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L)], hack: Int = 42)            (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule13[A, B, C, D, E, F, G, H, I, J, K, L, M](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L, M)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L, M)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)    (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)], hack: Int = 42)              (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L, M, N)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)   (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)], hack: Int = 42)                (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)  (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)], hack: Int = 42)                  (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P) (implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)], hack: Int = 42)                    (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil)     (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)], hack: Int = 42)                      (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil])(implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil)      (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)], hack: Int = 42)                         (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil]) (implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil)       (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)], hack: Int = 42)                            (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil])  (implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil]
  def hl(n: Int)(implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil)        (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)], hack: Int = 42)                               (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil])   (implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil)         (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)], hack: Int = 42)                                  (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil])    (implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}

abstract class Molecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](val _model: Model, val _query: Query) extends Molecule {
  def get        (implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)]
  def get(n: Int)(implicit conn: Connection): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)]    = get(conn).take(n)
  def one        (implicit conn: Connection): (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)         = get(conn).head
  def some       (implicit conn: Connection): Option[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)] = get(conn).headOption
  def hl         (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil]
  def hl(n: Int) (implicit conn: Connection): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil] = hl.take(n)
  object insert {
    def apply(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Connection): Tx = insert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
    def apply(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil)          (implicit conn: Connection): Tx = insert(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)], hack: Int = 42)                                     (implicit conn: Connection): Tx = insert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
    def apply(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil])     (implicit conn: Connection): Tx = insert(conn, _model, data.map(_.toList))
  }
  def asOf(d: Date) = asOf_(this, d)
  def asOf(l: Long) = asOf_(this, l)
  def asOf(t: lObj) = asOf_(this, t)
  def history       = history_(this)
}
