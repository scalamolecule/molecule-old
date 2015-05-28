package molecule
import java.util.{Collection => jCollection, Date, List => jList, Map => jMap, UUID}

import datomic._
import molecule.util.Debug

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.language.{existentials, higherKinds}

case class EntityFacade(entity: datomic.Entity, conn: Connection, id: Object) {
  private val x = Debug("EntityFacade", 1, 99, false, 3)


  // Retraction ......................................................................

  def retract = conn.transact(Util.list(Util.list(":db.fn/retractEntity", id))).get()


  // Touch ...........................................................................

  def touch: Map[String, Any] = toMap

  // Format touch output for tests...
  def touch2: Map[_, _] = toMap.map(p => s"""\n"${p._1}"""" -> formatEntity(p._2)).toMap
  def formatEntity(value: Any): Any = value match {
    case s: String               => s""""$s""""
    case l: Long                 => if (l > Int.MaxValue) s"${l}L" else l // presuming we used Int... - how to get Int from touch?
    case l: Seq[_]               => l map formatEntity
    case m: Map[_, _]            => "\n" + m.map(p => s""""${p._1}"""" -> formatEntity(p._2))
    case (s: String, value: Any) => s""""$s"""" -> formatEntity(value)
    case other                   => other
  }

  def toMap: Map[String, Any] = {
    val builder = Map.newBuilder[String, Any]
    val iter = entity.keySet.toList.sorted.asJava.iterator()

    // Add id also
    builder += ":db/id" -> entity.get(":db/id")
    while (iter.hasNext) {
      val key = iter.next()
      builder += (key -> toScala(entity.get(key)))
    }
    builder.result().toList.map {
      case (s: String, refs: List[_]) => refs.head match {
        case ref1: Map[_, _] => ref1.head match {
          case (attr: String, id: Long) => {
            // Presuming we now have a map of referenced entities we can sort the referenced maps by ref ids
            val indexedRefMaps: List[(Long, Map[String, Any])] = refs.map {
              case ref2: Map[_, _] => ref2.head match {
                case (attr: String, id: Long) => {
                  id -> ref2.asInstanceOf[Map[String, Any]]
                }
              }
            }
            s -> indexedRefMaps.sortBy(_._1).map(_._2)
          }
        }
        case other           => s -> refs
      }
      case other                      => other
    }.toMap
  }


  // Entity api from ValueAttribute (typed) .................................................................

  import dsl.schemaDSL._
  type W[Out] = ValueAttr[_, _, _, Out]
  type Op[T] = Option[T]

  // Single value
  def apply[A](attr: W[A]): Option[A] = attr match {
    case oneInt: OneInt[_, _]     => entity.get(attr._kw) match {
      case null   => None
      case oneInt => Some(toScala(oneInt).asInstanceOf[Long].toInt.asInstanceOf[A])
    }
    case oneFloat: OneFloat[_, _] => entity.get(attr._kw) match {
      case null     => None
      case oneFloat => Some(toScala(oneFloat).asInstanceOf[Double].toFloat.asInstanceOf[A])
    }
    case otherOne: One[_, _, _]   => entity.get(attr._kw) match {
      case null     => None
      case oneValue => Some(toScala(oneValue).asInstanceOf[A])
    }
    case many: Many[_, _, _, _]   => entity.get(attr._kw) match {
      case null                                    => None
      case results: clojure.lang.PersistentHashSet => results.head match {
        case ent: datomic.Entity => Some(results.toList.map(_.asInstanceOf[datomic.Entity].get(":db/id").asInstanceOf[Long]).sorted.asInstanceOf[A])
        case manySet             => Some((results.toList map toScala).toSet.asInstanceOf[A])
      }
      case shouldWeEverGetHere                     => Some(toScala(shouldWeEverGetHere).asInstanceOf[A])
    }
  }

  // Tuples, arity 2-22
  def apply[A, B](a: W[A], b: W[B]): (Op[A], Op[B]) = (apply(a), apply(b))
  def apply[A, B, C](a: W[A], b: W[B], c: W[C]): (Op[A], Op[B], Op[C]) = (apply(a), apply(b), apply(c))
  def apply[A, B, C, D](a: W[A], b: W[B], c: W[C], d: W[D]): (Op[A], Op[B], Op[C], Op[D]) = (apply(a), apply(b), apply(c), apply(d))
  def apply[A, B, C, D, E](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E]): (Op[A], Op[B], Op[C], Op[D], Op[E]) = (apply(a), apply(b), apply(c), apply(d), apply(e))
  def apply[A, B, C, D, E, F](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f))
  def apply[A, B, C, D, E, F, G](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g))
  def apply[A, B, C, D, E, F, G, H](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h))
  def apply[A, B, C, D, E, F, G, H, I](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i))
  def apply[A, B, C, D, E, F, G, H, I, J](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j))
  def apply[A, B, C, D, E, F, G, H, I, J, K](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L, M](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L], m: W[M]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L], Op[M]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l), apply(m))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L, M, N](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L], m: W[M], n: W[N]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L], Op[M], Op[N]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l), apply(m), apply(n))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L], m: W[M], n: W[N], o: W[O]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L], Op[M], Op[N], Op[O]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l), apply(m), apply(n), apply(o))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L], m: W[M], n: W[N], o: W[O], p: W[P]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L], Op[M], Op[N], Op[O], Op[P]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l), apply(m), apply(n), apply(o), apply(p))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L], m: W[M], n: W[N], o: W[O], p: W[P], q: W[Q]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L], Op[M], Op[N], Op[O], Op[P], Op[Q]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l), apply(m), apply(n), apply(o), apply(p), apply(q))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L], m: W[M], n: W[N], o: W[O], p: W[P], q: W[Q], r: W[R]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L], Op[M], Op[N], Op[O], Op[P], Op[Q], Op[R]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l), apply(m), apply(n), apply(o), apply(p), apply(q), apply(r))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L], m: W[M], n: W[N], o: W[O], p: W[P], q: W[Q], r: W[R], s: W[S]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L], Op[M], Op[N], Op[O], Op[P], Op[Q], Op[R], Op[S]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l), apply(m), apply(n), apply(o), apply(p), apply(q), apply(r), apply(s))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L], m: W[M], n: W[N], o: W[O], p: W[P], q: W[Q], r: W[R], s: W[S], t: W[T]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L], Op[M], Op[N], Op[O], Op[P], Op[Q], Op[R], Op[S], Op[T]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l), apply(m), apply(n), apply(o), apply(p), apply(q), apply(r), apply(s), apply(t))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L], m: W[M], n: W[N], o: W[O], p: W[P], q: W[Q], r: W[R], s: W[S], t: W[T], u: W[U]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L], Op[M], Op[N], Op[O], Op[P], Op[Q], Op[R], Op[S], Op[T], Op[U]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l), apply(m), apply(n), apply(o), apply(p), apply(q), apply(r), apply(s), apply(t), apply(u))
  def apply[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](a: W[A], b: W[B], c: W[C], d: W[D], e: W[E], f: W[F], g: W[G], h: W[H], i: W[I], j: W[J], k: W[K], l: W[L], m: W[M], n: W[N], o: W[O], p: W[P], q: W[Q], r: W[R], s: W[S], t: W[T], u: W[U], v: W[V]): (Op[A], Op[B], Op[C], Op[D], Op[E], Op[F], Op[G], Op[H], Op[I], Op[J], Op[K], Op[L], Op[M], Op[N], Op[O], Op[P], Op[Q], Op[R], Op[S], Op[T], Op[U], Op[V]) = (apply(a), apply(b), apply(c), apply(d), apply(e), apply(f), apply(g), apply(h), apply(i), apply(j), apply(k), apply(l), apply(m), apply(n), apply(o), apply(p), apply(q), apply(r), apply(s), apply(t), apply(u), apply(v))

  //  def andMaybe[A](attr: Z[A]): Option[A] =


  // Entity api from string (typed) .................................................................


  def getTyped[T](kw: String): Option[T] = entity.get(kw) match {
    case null                                    => None
    case results: clojure.lang.PersistentHashSet => results.head match {
      case ent: datomic.Entity => Some(results.toList.map(_.asInstanceOf[datomic.Entity].get(":db/id").asInstanceOf[Long]).sorted.asInstanceOf[T])
      case manyValue           => Some((results.toList map toScala).toSet.asInstanceOf[T])
    }
    case result => Some(toScala(result).asInstanceOf[T])
  }

  // Entity api from string (untyped) .................................................................

  def apply(kw1: String, kw2: String, kwx: String*): Seq[Option[Any]] = {
    (kw1 +: kw2 +: kwx.toList) map apply
  }

  def apply(kw: String): Option[Any] = entity.get(kw) match {
    case null                                    => None
    case results: clojure.lang.PersistentHashSet => results.head match {
      case ent: datomic.Entity => Some(results.toList.map(_.asInstanceOf[datomic.Entity].get(":db/id").asInstanceOf[Long]).sorted)
      case manyValue           => Some((results.toList map toScala).toSet)
    }
    case result                                  => Some(toScala(result))
  }


  // Conversions ..........................................................................

  private[molecule] def toScala(v: Any): Any = v match {
    // :db.type/string
    case s: java.lang.String => s
    // :db.type/boolean
    case b: java.lang.Boolean => b: Boolean
    // :db.type/long
    case l: java.lang.Long => l: Long
    // attribute id
    case i: java.lang.Integer => i.toLong: Long
    // :db.type/float
    case f: java.lang.Float => f: Float
    // :db.type/double
    case d: java.lang.Double => d: Double
    // :db.type/bigint
    case bi: java.math.BigInteger => BigInt(bi)
    // :db.type/bigdec
    case bd: java.math.BigDecimal => BigDecimal(bd)
    // :db.type/instant
    case d: Date => d
    // :db.type/uuid
    case u: UUID => u
    // :db.type/uri
    case u: java.net.URI => u
    // :db.type/keyword
    case kw: clojure.lang.Keyword => kw.toString // Clojure Keywords not used in Molecule
    // :db.type/bytes
    case bytes: Array[Byte] => bytes
    // an entity map
    case e: datomic.Entity => new EntityFacade(e, conn, e.get(":db/id")).toMap

    // :db.type/keyword
    case set: clojure.lang.PersistentHashSet =>
      set.toList map toScala

    // a collection
    case coll: java.util.Collection[_] =>
      new Iterable[Any] {
        override def iterator = new Iterator[Any] {
          private val jIter = coll.iterator.asInstanceOf[java.util.Iterator[AnyRef]]
          override def hasNext = jIter.hasNext
          override def next() = toScala(jIter.next())
        }
        override def isEmpty = coll.isEmpty
        override def size = coll.size
        override def toString = coll.toString
      }
    // otherwise
    case v => throw new RuntimeException("[DatomicFacade:Convert:toScala] Unexpected Datalog type to convert: " + v.getClass.toString)
  }
}
