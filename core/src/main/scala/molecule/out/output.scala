package molecule.out
import java.util.{Date => jDate}
import datomic.{Connection => Cnx}
import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.query.Query
import molecule.dsl.schemaDSL.NS
import shapeless.{::, HNil}


// Output models

trait Out_0 extends NS
trait Out_1[A] extends NS
trait Out_2[A, B] extends NS
trait Out_3[A, B, C] extends NS
trait Out_4[A, B, C, D] extends NS
trait Out_5[A, B, C, D, E] extends NS
trait Out_6[A, B, C, D, E, F] extends NS
trait Out_7[A, B, C, D, E, F, G] extends NS
trait Out_8[A, B, C, D, E, F, G, H] extends NS
trait Out_9[A, B, C, D, E, F, G, H, I] extends NS
trait Out_10[A, B, C, D, E, F, G, H, I, J] extends NS
trait Out_11[A, B, C, D, E, F, G, H, I, J, K] extends NS
trait Out_12[A, B, C, D, E, F, G, H, I, J, K, L] extends NS
trait Out_13[A, B, C, D, E, F, G, H, I, J, K, L, M] extends NS
trait Out_14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends NS
trait Out_15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends NS
trait Out_16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends NS
trait Out_17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends NS
trait Out_18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends NS
trait Out_19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends NS
trait Out_20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends NS
trait Out_21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends NS
trait Out_22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends NS


// Output molecules
trait OutputMolecule extends DatomicFacade {
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

  protected case class nodeActions(conn: Cnx, _model: Model, data: Seq[Seq[Any]]) {
    // Maybe misleading to think of child/parent since associations are multidirectional...
    def childOf (parentId: Long): Long = {
      val currentNs = curNs(_model.elements.head)
      upsert(conn, _model :+ Node(currentNs, parentId), data).last
    }

    // Using this one as it makes no presumptions of direction of the relationship
    def -- (parentId: Long): Long = childOf(parentId)

    // Allows reversed notation:
    // parentId +: Comment(stu, "blah 1")
    // Not so intuitive though that it's the new child id that is returned...
    def +: (parentId: Long): Long = childOf(parentId)
  }
}

abstract class OutputMolecule0(val _model: Model, val _query: Query) extends OutputMolecule

abstract class OutputMolecule1[A](val _model: Model, val _query: Query) extends OutputMolecule {
  def get(implicit conn: Cnx): Seq[A]
  def take(n: Int)(implicit conn: Cnx): Seq[A] = get(conn).take(n)

  def insert(a: A)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a)))
  def insert(a: A, a2: A, ax: A*)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, (Seq(a, a2) ++ ax.toSeq).map(Seq(_)))
  def insert(data: Seq[A])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(Seq(_)))
}

abstract class OutputMolecule2[A, B](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: HNil] = hls.take(n)

  object node {
    def apply(a: A, b: B)(implicit conn: Cnx) = nodeActions(conn, _model, Seq(Seq(a, b)))
    def apply(data: A :: B :: HNil)(implicit conn: Cnx) = nodeActions(conn, _model, Seq(data.toList))
    def apply(data: Seq[(A, B)], hack: Int = 42)(implicit conn: Cnx) = nodeActions(conn, _model, data.map(d => Seq(d._1, d._2)))
    def apply(data: Seq[A :: B :: HNil])(implicit conn: Cnx) = nodeActions(conn, _model, data.map(_.toList))
  }

  def insert(a: A, b: B)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b)))
  def insert(data: A :: B :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B)], hack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2)))
  def insert(data: Seq[A :: B :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule3[A, B, C](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C)] = tpls(conn).take(n)
  def hls(implicit conn: Cnx): Seq[A :: B :: C :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c)))
  def insert(data: A :: B :: C :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3)))
  def insert(data: Seq[A :: B :: C :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule4[A, B, C, D](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D)] = tpls(conn).take(n)
  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d)))
  def insert(data: A :: B :: C :: D :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4)))
  def insert(data: Seq[A :: B :: C :: D :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule5[A, B, C, D, E](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E)] = tpls(conn).take(n)
  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e)))
  def insert(data: A :: B :: C :: D :: E :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
  def insert(data: Seq[A :: B :: C :: D :: E :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule6[A, B, C, D, E, F](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F)] = tpls(conn).take(n)
  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f)))
  def insert(data: A :: B :: C :: D :: E :: F :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule7[A, B, C, D, E, F, G](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G)] = tpls(conn).take(n)
  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule8[A, B, C, D, E, F, G, H](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H)] = tpls(conn).take(n)
  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule9[A, B, C, D, E, F, G, H, I](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I)] = tpls(conn).take(n)
  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule10[A, B, C, D, E, F, G, H, I, J](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule11[A, B, C, D, E, F, G, H, I, J, K](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule12[A, B, C, D, E, F, G, H, I, J, K, L](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule13[A, B, C, D, E, F, G, H, I, J, K, L, M](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule14[A, B, C, D, E, F, G, H, I, J, K, L, M, N](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}

abstract class OutputMolecule22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](val _model: Model, val _query: Query) extends OutputMolecule {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, Seq(data.toList))
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil])(implicit conn: Cnx): Seq[Long] = upsert(conn, _model, data.map(_.toList))
}
