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
  val q: Query
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