package molecule
package ast
import datomic.Connection
import molecule.ast.model._
import molecule.db.DatomicFacade

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

  trait Ref
  abstract class OneRef extends Ref
  abstract class ManyRef extends Ref

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

  trait One[Ns, Ns2, T] extends ValueAttr[Ns, Ns2, T] {
    def apply(value: T*): NS = ns
  }

  trait Many[Ns, Ns2, T] extends ValueAttr[Ns, Ns2, T]


  //  sealed trait ValueType
  //  trait string extends ValueType
  //  trait boolean extends ValueType
  //  trait long extends ValueType
  //  trait bigint extends ValueType
  //  trait float extends ValueType
  //  trait double extends ValueType
  //  trait bigdec extends ValueType
  //  trait instant extends ValueType
  //  trait uuid extends ValueType
  //  trait uri extends ValueType
  abstract class OneString[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, String]
  abstract class OneInt[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, Int]
  abstract class OneLong[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, Long]

  abstract class ManyString[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, Set[String]] {
    def apply(value: String*): NS = ns
  }

  trait Enum
  abstract class OneEnum[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends One[Ns, Ns2, String] with Enum
  abstract class ManyEnums[Ns, Ns2](val ns: Ns, val ns2: Ns2) extends Many[Ns, Ns2, String] with Enum

  object EnumValue

  trait Doc
  trait FulltextSearch {self: Attr =>
    def contains(that: String): NS2 = ns2
  }
  trait Indexed
  trait NoHistory
  trait IsComponent
  trait Mandatory

  trait UniqueValue
  trait UniqueIdentity


  abstract class Insert(val elements: Seq[Element]) extends DatomicFacade {
    def save(implicit conn: Connection): Seq[Long] = upsertMolecule(conn, Model(elements))
  }
  abstract class Update(val elements: Seq[Element], val ids: Seq[Long]) extends DatomicFacade {
    def save(implicit conn: Connection): Seq[Long] = upsertMolecule(conn, Model(elements), ids)
  }
  abstract class Retract(elements: Seq[Element]) extends DatomicFacade
  abstract class Entity(elements: Seq[Element]) extends DatomicFacade

}