package molecule.dsl

import java.net.URI
//import java.util.{UUID, Date => jDate}
import java.util.{UUID, Date}
import datomic.Connection
import molecule.DatomicFacade
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
    class e         [Ns] extends OneLong    [Ns] { self: Ns => }
    class a         [Ns] extends OneString  [Ns] { self: Ns => }
    class v         [Ns] extends OneAny     [Ns] { self: Ns => }
    class ns        [Ns] extends OneString  [Ns] { self: Ns => }
    class txInstant [Ns] extends OneDate    [Ns] { self: Ns => }
    class txT       [Ns] extends OneLong    [Ns] { self: Ns => }
    class txAdded   [Ns] extends OneBoolean [Ns] { self: Ns => }
  }

  trait Ref[Ns1, Ns2]
  trait OneRef[Ns1, Ns2] extends Ref[Ns1, Ns2]
  trait ManyRef[Ns1, Ns2] extends Ref[Ns1, Ns2]
  trait BackRef[BackRefNS, ThisNs] extends Ref[BackRefNS, ThisNs]
  trait ChildRef[Ns1] extends Ref[Ns1, Ns1]
  trait HyperRef[Ns1] extends Ref[Ns1, Ns1]

  trait Attr

  trait RefAttr[Ns1, T] extends Attr
  trait OneRefAttr[Ns] extends RefAttr[Ns,  Long] {self: Ns =>
    def apply(value: Long) = self
  }
  trait ManyRefAttr[Ns] extends RefAttr[Ns,  Long] {self: Ns =>
    def apply(value: Long*) = self
    def add(value: Long) = self
    def remove(values: Long*) = self
  }

  sealed trait ValueAttr[Ns, T] extends Attr {self: Ns =>
//  sealed trait ValueAttr[T] extends Attr {self =>
//    type NS = Ns

    // Unchanged arity
    def apply(expr: Exp1[T]) = self
//    def find(expr: Exp1[T]) = self
//    def apply(anyValue: Any => Any) = self

    // Increase arity
    def ! (value: T*) = self
    def ! (value: Exp1[T]) = self
    def maybe = self
    def < (value: T) = self
    def eq(value: T) = self
//    def contains(value: T) = self
  }


  // One-cardinality
//  trait One[T] extends ValueAttr[T] { self: M =>
//  trait One[Ns, T]  { self: NS =>
//  trait One[Ns <: NS, T]  { self: Ns =>
  trait One[Ns, T] extends ValueAttr[Ns, T] { self: Ns =>
//  trait One[Ns, T]  { self: Ns =>

//    def apply(expr: Exp1[T]) = self


    //    def apply(values: T*) = self
//    def apply(one: T, more: T*) = self
    def apply() = self
    def apply(values: Seq[T]) = self
  }
  trait OneString  [Ns] extends One[Ns, String]  {self: Ns =>}
  trait OneInt     [Ns] extends One[Ns, Int]     {self: Ns =>}
  trait OneLong    [Ns] extends One[Ns, Long]    {self: Ns =>}
  trait OneFloat   [Ns] extends One[Ns, Float]   {self: Ns =>}
  trait OneDouble  [Ns] extends One[Ns, Double]  {self: Ns =>}
  trait OneBoolean [Ns] extends One[Ns, Boolean] {self: Ns =>}
  trait OneDate    [Ns] extends One[Ns, Date]    {self: Ns =>}
  trait OneUUID    [Ns] extends One[Ns, UUID]    {self: Ns =>}
  trait OneURI     [Ns] extends One[Ns, URI]     {self: Ns =>}

  trait OneAny     [Ns] extends One[Ns, Any]     {self: Ns =>}

  // Many-cardinality
  trait Many[Ns, S, T] extends ValueAttr[Ns, T] { self: Ns =>
    def apply(value: T*) = self
//    def apply(one: T, more: T*) = self
//    def apply() = self

//    def apply(values: Seq[T]) = self
    def apply(oldNew: (T, T), oldNewMore: (T, T)*) = self
    //    def apply(h: Seq[(T, T)]) = self
    def add(value: T) = self
    def remove(values: T*) = self
  }
  trait ManyString [Ns] extends Many[Ns, Set[String], String] {self: Ns =>}
  trait ManyInt    [Ns] extends Many[Ns, Set[Int], Int]       {self: Ns =>}
  trait ManyLong   [Ns] extends Many[Ns, Set[Long], Long]     {self: Ns =>}
  trait ManyFloat  [Ns] extends Many[Ns, Set[Float], Float]   {self: Ns =>}
  trait ManyDouble [Ns] extends Many[Ns, Set[Double], Double] {self: Ns =>}
  trait ManyDate   [Ns] extends Many[Ns, Set[Date], Date]     {self: Ns =>}
  trait ManyUUID   [Ns] extends Many[Ns, Set[UUID], UUID]     {self: Ns =>}
  trait ManyURI    [Ns] extends Many[Ns, Set[URI], URI]       {self: Ns =>}

  // Enums
  trait Enum
  trait OneEnum  [Ns] extends One [Ns, String]         with Enum {self: Ns =>}
  trait ManyEnums[Ns] extends Many[Ns, String, String] with Enum {self: Ns =>}
  object EnumValue

  // Attribute options
  case class Doc(msg: String)
  trait UniqueValue
  trait UniqueIdentity
  trait Indexed
  trait FulltextSearch[Ns] {
    self: Ns =>
    def contains(that: String) = self
  }
//  trait FulltextSearch {
//    self: Attr =>
//    def contains(that: String) = self
//  }
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