package molecule.dsl
import molecule.dsl.schemaDSL._
import molecule.out.{Molecule_0, Molecule_1}

object DbSchema {

  object Db extends Db_0 {
    class txInstant[NS, NS2](ns: NS, ns2: NS2) extends OneLong(ns, ns2)
  }

  trait Db_0 extends Molecule_0 {
    import molecule.dsl.DbSchema.Db._
//    lazy val txInstant = new txInstant(this, new Db_1[java.util.Date] {}) with Db_1[java.util.Date]
  }

  trait Db_1[T1] extends Molecule_1[T1]
}