package molecule.dsl

import java.net.URI
//import java.util.{UUID, Date => jDate}
import java.util.{UUID, Date}
import datomic.Connection
import molecule._
import molecule.ast.model._
import molecule.out.Molecule_1
import molecule.out.Molecule

object schemaDSL {

  // todo?
  trait Partition

  trait Tree {
    // Parent entity
//    class _parent[Ns] extends OneLong[Ns] {self: Ns =>}
  }
  trait HyperEdge

  trait NS {
    // Common attribute classes
    abstract class e         [Ns] extends OneLong    [Ns]
    abstract class a         [Ns] extends OneString  [Ns]
    abstract class v         [Ns] extends OneAny     [Ns]
    abstract class ns        [Ns] extends OneString  [Ns]
    abstract class txInstant [Ns] extends OneDate    [Ns]
    abstract class txT       [Ns] extends OneLong    [Ns]
    abstract class txAdded   [Ns] extends OneBoolean [Ns]
  }

  trait Ref[Ns1, Ns2]
  trait OneRef[Ns1, Ns2] extends Ref[Ns1, Ns2]
  trait ManyRef[Ns1, Ns2] extends Ref[Ns1, Ns2]
//  trait BackRef[BackRefNS, ThisNs] extends Ref[BackRefNS, ThisNs]
  trait ChildRef[Ns1] extends Ref[Ns1, Ns1]
  trait HyperRef[Ns1] extends Ref[Ns1, Ns1]

  trait Attr

  trait RefAttr[Ns1, T] extends Attr
  trait OneRefAttr[Ns] extends RefAttr[Ns,  Long] { //self: Ns =>
    def apply(value: Long): Ns = ???
  }
  trait ManyRefAttr[Ns] extends RefAttr[Ns,  Long] { //self: Ns =>
    def apply(value: Long*): Ns = ???
    def add(value: Long): Ns = ???
    def remove(values: Long*): Ns = ???
  }

//  sealed trait ValueAttr[Ns, T] extends Attr {self: Ns =>
  sealed trait ValueAttr[Ns, T] extends Attr {
    def apply(expr: Exp1[T]) : Ns = ???
    def maybe : Ns = ???
    def < (value: T) : Ns = ???
    def eq(value: T) : Ns = ???

  def apply(in: ?) : Ns = ???

    def apply(m: maybe): Ns = ???
  }


  // One-cardinality
//  trait One[Ns, T] extends ValueAttr[Ns, T] { self: Ns =>
  trait One[Ns, T] extends ValueAttr[Ns, T] {
    //    def apply(expr: Exp1[T]): Ns = ???
    //    def apply(values: T*): Ns = ???
    //    def apply(one: T, more: T*): Ns = ???

    // Request for no value!
    def apply(): Ns = ???
    def apply(values: Seq[T]) : Ns = ???

  }
  trait OneString  [Ns] extends One[Ns, String]
  trait OneInt     [Ns] extends One[Ns, Int]
  trait OneLong    [Ns] extends One[Ns, Long]
  trait OneFloat   [Ns] extends One[Ns, Float]
  trait OneDouble  [Ns] extends One[Ns, Double]
  trait OneBoolean [Ns] extends One[Ns, Boolean]
  trait OneDate    [Ns] extends One[Ns, Date]
  trait OneUUID    [Ns] extends One[Ns, UUID]
  trait OneURI     [Ns] extends One[Ns, URI]

  trait OneAny     [Ns] extends One[Ns, Any]

  // Many-cardinality
  trait Many[Ns, S, T] extends ValueAttr[Ns, T] { //self: Ns =>
    def apply(value: T*): Ns = ???
//    def apply(one: T, more: T*): Ns = ???
//    def apply(): Ns = ???

//    def apply(values: Seq[T]): Ns = ???
    def apply(oldNew: (T, T), oldNewMore: (T, T)*): Ns = ???
    //    def apply(h: Seq[(T, T)]): Ns = ???
    def add(value: T): Ns = ???
    def remove(values: T*): Ns = ???
  }
  trait ManyString [Ns] extends Many[Ns, Set[String], String]
  trait ManyInt    [Ns] extends Many[Ns, Set[Int], Int]
  trait ManyLong   [Ns] extends Many[Ns, Set[Long], Long]
  trait ManyFloat  [Ns] extends Many[Ns, Set[Float], Float]
  trait ManyDouble [Ns] extends Many[Ns, Set[Double], Double]
  trait ManyDate   [Ns] extends Many[Ns, Set[Date], Date]
  trait ManyUUID   [Ns] extends Many[Ns, Set[UUID], UUID]
  trait ManyURI    [Ns] extends Many[Ns, Set[URI], URI]

  // Enums
  trait Enum
  trait OneEnum  [Ns] extends One [Ns, String]         with Enum
  trait ManyEnums[Ns] extends Many[Ns, String, String] with Enum
  object EnumValue

  // Attribute options
  case class Doc(msg: String)
  trait UniqueValue
  trait UniqueIdentity
  trait Indexed
  trait FulltextSearch[Ns] { //self: Ns =>
    def contains(that: String): Ns = ???
  }
  trait IsComponent
  trait NoHistory

  //  trait Mandatory
  abstract class Insert(val elements: Seq[Element]) extends DatomicFacade {
    def save(implicit conn: Connection): Seq[Long] = upsert(conn, Model(elements)).ids
  }
  abstract class Update(val elements: Seq[Element], val ids: Seq[Long]) extends DatomicFacade {
    def save(implicit conn: Connection): Seq[Long] = upsert(conn, Model(elements), Seq(), ids).ids
  }
  abstract class Retract(elements: Seq[Element]) extends DatomicFacade
  abstract class Entity(elements: Seq[Element]) extends DatomicFacade

}