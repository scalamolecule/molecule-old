package molecule.dsl

import java.net.URI
import java.util.{UUID, Date => jDate}
import datomic.Connection
import molecule.DatomicFacade
import molecule.ast.model._

object schemaDSL {

  // todo
  trait Partition

  trait NS {
    // Entity id (internal Datomic id)
    class eid[Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends ValueAttr[Ns1, Ns2, Long]
  }


  trait Ref[Ns1, Ns2]
  trait OneRef[Ns1, Ns2] extends Ref[Ns1, Ns2]
  trait ManyRef[Ns1, Ns2] extends Ref[Ns1, Ns2]

  trait Attr {
    type NS1
    type NS2
    val ns1: NS1
    val ns2: NS2
  }

  trait RefAttr[Ns1, Ns2, T] extends Attr {
    type NS1 = Ns1
    type NS2 = Ns2
  }
  abstract class OneRefAttr[Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends RefAttr[Ns1, Ns2, Long] with OneRef[Ns1, Ns2] {
    def apply(value: Long): NS1 = ns1
  }
  abstract class ManyRefAttr[Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends RefAttr[Ns1, Ns2, Long] with ManyRef[Ns1, Ns2] {
    def apply(value: Long): NS1 = ns1
  }

  sealed trait ValueAttr[Ns1, Ns2, T] extends Attr {
    type NS1 = Ns1
    type NS2 = Ns2

    def apply(value: Exp1[T]): NS1 = ns1
    def find(value: Exp1[T]): NS1 = ns1
    def ! (value: T*): NS2 = ns2
    def maybe = ns2
    def < (value: T): NS2 = ns2
    def eq(value: T): NS2 = ns2
  }


  // One-cardinality
  trait One[Ns1, Ns2, T] extends ValueAttr[Ns1, Ns2, T] {
    def apply(value: T*): NS1 = ns1
  }
  abstract class OneString [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends One[Ns1, Ns2, String]
  abstract class OneInt    [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends One[Ns1, Ns2, Int]
  abstract class OneLong   [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends One[Ns1, Ns2, Long]
  abstract class OneFloat  [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends One[Ns1, Ns2, Float]
  abstract class OneDouble [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends One[Ns1, Ns2, Double]
  abstract class OneBoolean[Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends One[Ns1, Ns2, Boolean]
  abstract class OneDate   [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends One[Ns1, Ns2, jDate]
  abstract class OneUUID   [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends One[Ns1, Ns2, UUID]
  abstract class OneURI    [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends One[Ns1, Ns2, URI]

  // Many-cardinality
  trait Many[Ns1, Ns2, T, U] extends ValueAttr[Ns1, Ns2, T] {
    def apply(value: U*): NS1 = ns1
    def apply(h: (U, U), t: (U, U)*): NS1 = ns1
    def add(data: U): NS1 = ns1
    def remove(values: U*): NS1 = ns1
//    def apply(): NS1 = ns1
  }
  abstract class ManyString[Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends Many[Ns1, Ns2, Set[String], String]
  abstract class ManyInt   [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends Many[Ns1, Ns2, Set[Int], Int]
  abstract class ManyLong  [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends Many[Ns1, Ns2, Set[Long], Long]
  abstract class ManyFloat [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends Many[Ns1, Ns2, Set[Float], Float]
  abstract class ManyDouble[Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends Many[Ns1, Ns2, Set[Double], Double]
  abstract class ManyDate  [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends Many[Ns1, Ns2, Set[jDate], jDate]
  abstract class ManyUUID  [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends Many[Ns1, Ns2, Set[UUID], UUID]
  abstract class ManyURI   [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends Many[Ns1, Ns2, Set[URI], URI]

  // Enums
  trait Enum
  abstract class OneEnum  [Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends One [Ns1, Ns2, String] with Enum
  abstract class ManyEnums[Ns1, Ns2](val ns1: Ns1, val ns2: Ns2) extends Many[Ns1, Ns2, String, String] with Enum
  object EnumValue

  // Attribute options
  case class Doc(msg: String)
  trait UniqueValue
  trait UniqueIdentity
  trait Indexed
  trait FulltextSearch {
    self: Attr =>
    def contains(that: String): NS2 = ns2
  }
  trait IsComponent
  trait NoHistory

  //  trait Mandatory
  abstract class Insert(val elements: Seq[Element]) extends DatomicFacade {
    def save(implicit conn: Connection): Seq[Long] = upsert(conn, Model(elements))
  }
  abstract class Update(val elements: Seq[Element], val ids: Seq[Long]) extends DatomicFacade {
    def save(implicit conn: Connection): Seq[Long] = upsert(conn, Model(elements), Seq(), ids)
  }
  abstract class Retract(elements: Seq[Element]) extends DatomicFacade
  abstract class Entity(elements: Seq[Element]) extends DatomicFacade

}