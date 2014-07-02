package molecule
package db
import molecule.ast.schemaDSL._

object DbSchema {

  object Db extends Db_0 {
    class txInstant[NS, NS2](ns: NS, ns2: NS2) extends OneLong(ns, ns2)
  }

  trait Db_0 extends Out_0 {
    import Db._
    lazy val txInstant = new txInstant(this, new Db_1[java.util.Date] {}) with Db_1[java.util.Date]
  }

  trait Db_1[T1] extends Out_1[T1]
}