package molecule
import datomic.{Connection => Cnx}
import molecule.ast.model._
import molecule.ast.query.Query
import molecule.ast.schemaDSL.NS
import molecule.db.DatomicFacade._
import shapeless.{::, HNil}

// Output molecule interfaces

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


// Output molecule implementations

trait Out {
  val _model: Model
  val _query: Query
}

trait Out0 extends Out

trait Out1[A] extends Out {
  def get(implicit conn: Cnx): Seq[A]
  def take(n: Int)(implicit conn: Cnx): Seq[A] = get(conn).take(n)

  def insert(a: A)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a))
  def insert(a: A, a2: A, ax: A*)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, Seq(a +: a2 +: ax))
  def insert(data: Seq[A])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, Seq(data))
}

trait Out2[A, B] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: HNil] = hls.take(n)

  def insert(a: A, b: B)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b))
  def insert(data: A :: B :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2)))
  def insert(data: Seq[A :: B :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out3[A, B, C] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c))
  def insert(data: A :: B :: C :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3)))
  def insert(data: Seq[A :: B :: C :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out4[A, B, C, D] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d))
  def insert(data: A :: B :: C :: D :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4)))
  def insert(data: Seq[A :: B :: C :: D :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out5[A, B, C, D, E] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e))
  def insert(data: A :: B :: C :: D :: E :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5)))
  def insert(data: Seq[A :: B :: C :: D :: E :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out6[A, B, C, D, E, F] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f))
  def insert(data: A :: B :: C :: D :: E :: F :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out7[A, B, C, D, E, F, G] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out8[A, B, C, D, E, F, G, H] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out9[A, B, C, D, E, F, G, H, I] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out10[A, B, C, D, E, F, G, H, I, J] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out11[A, B, C, D, E, F, G, H, I, J, K] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out12[A, B, C, D, E, F, G, H, I, J, K, L] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out13[A, B, C, D, E, F, G, H, I, J, K, L, M] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l, m))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out14[A, B, C, D, E, F, G, H, I, J, K, L, M, N] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out15[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out16[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out17[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out18[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out19[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out20[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out21[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}

trait Out22[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] extends Out {
  def tpls(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)]
  def tpl(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)] = tpls(conn).take(n)
  def take(n: Int)(implicit conn: Cnx): Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)] = tpls(conn).take(n)

  def hls(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil]
  def hl(n: Int)(implicit conn: Cnx): Seq[A :: B :: C :: D :: E :: F :: G :: H :: HNil] = hls.take(n)

  def insert(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, Seq(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v))
  def insert(data: A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil)(implicit conn: Cnx): Seq[Long] = insertOne(conn, _model, data.toList)
  def insert(data: Seq[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)], overloadHack: Int = 42)(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(d => Seq(d._1, d._2, d._3, d._4, d._5, d._6, d._7, d._8, d._9, d._10, d._11, d._12, d._13, d._14, d._15, d._16, d._17, d._18, d._19, d._20, d._21, d._22)))
  def insert(data: Seq[A :: B :: C :: D :: E :: F :: G :: H :: I :: J :: K :: L :: M :: N :: O :: P :: Q :: R :: S :: T :: U :: V :: HNil])(implicit conn: Cnx): Seq[Long] = insertMany(conn, _model, data.map(_.toList))
}