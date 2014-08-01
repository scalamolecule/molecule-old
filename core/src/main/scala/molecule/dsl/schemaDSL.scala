package molecule.dsl

import datomic.Connection
import molecule.ast.model._
import molecule.DatomicFacade
import scala.annotation.StaticAnnotation

object schemaDSL {

  // todo
  trait Partition

  // Annotation to mark default attribute of a namespace
  class default extends StaticAnnotation

  trait NS {
    // Entity id. If no other attribute is supplied, the default attribute is used to find it
    class eid[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Attr {
      type NS = Ns
      type NS2 = Ns2
    }
  }

  trait Ref[Ns1, Ns2]
  abstract class OneRef[Ns1, Ns2] extends Ref[Ns1, Ns2]
  abstract class ManyRef[Ns1, Ns2] extends Ref[Ns1, Ns2]

  trait Attr {
    type NS
    type NS2
    val ns : NS
    val ns2: NS2
  }

  sealed trait ValueAttr[Ns, Ns2, T] extends Attr {
    type NS = Ns
    type NS2 = Ns2

    def apply(value: Exp1[T]): NS = ns
    def find(value: Exp1[T]): NS = ns
    def !(value: T*): NS2 = ns2
    def maybe = ns2
    def <(value: T): NS2 = ns2
    def eq(value: T): NS2 = ns2
  }

  // One-cardinality
  trait One[Ns, Ns2, T] extends ValueAttr[Ns, Ns2, T] {
    def apply(value: T*): NS = ns
  }
  abstract class OneString[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, String]
  abstract class OneInt[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, Int]
  abstract class OneLong[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, Long]
  abstract class OneFloat[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, Float]
  abstract class OneDouble[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, Double]
  abstract class OneBoolean[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, Boolean]
  abstract class OneDate[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, java.util.Date]
  abstract class OneUUID[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, java.util.UUID]
  abstract class OneURI[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, java.net.URI]

  // Many-cardinality
  trait Many[Ns, Ns2, T] extends ValueAttr[Ns, Ns2, T]

  abstract class ManyString[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, Set[String]] {
    def apply(value: String*): NS = ns
  }
  abstract class ManyInt[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, Set[Int]] {
    def apply(value: Int*): NS = ns
  }
  abstract class ManyLong[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, Set[Long]] {
    def apply(value: Long*): NS = ns
  }
  abstract class ManyFloat[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, Set[Float]] {
    def apply(value: Float*): NS = ns
  }
  abstract class ManyDouble[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, Set[Double]] {
    def apply(value: Double*): NS = ns
  }
  abstract class ManyDate[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, Set[java.util.Date]] {
    def apply(value: java.util.Date*): NS = ns
  }
  abstract class ManyUUID[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, Set[java.util.UUID]] {
    def apply(value: java.util.UUID*): NS = ns
  }
  abstract class ManyURI[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, Set[java.net.URI]] {
    def apply(value: java.net.URI*): NS = ns
  }

  // Enums
  trait Enum
  abstract class OneEnum[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, String] with Enum
  abstract class ManyEnums[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, String] with Enum
  object EnumValue

  // Attribute options
  case class Doc(msg: String)
  trait UniqueValue
  trait UniqueIdentity
  trait Indexed
  trait FulltextSearch {self: Attr =>
    def contains(that: String): NS2 = ns2
  }
  trait IsComponent
  trait NoHistory

  //  trait Mandatory
  abstract class Insert(val elements: Seq[Element]) extends DatomicFacade {
    def save(implicit conn: Connection): Seq[Long] = upsertMolecule(conn, Model(elements))
  }
  abstract class Update(val elements: Seq[Element], val ids: Seq[Long]) extends DatomicFacade {
    def save(implicit conn: Connection): Seq[Long] = upsertMolecule(conn, Model(elements), ids)
  }
  abstract class Retract(elements: Seq[Element]) extends DatomicFacade
  abstract class Entity(elements: Seq[Element]) extends DatomicFacade

}