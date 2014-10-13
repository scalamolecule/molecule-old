package molecule.schemas

import java.util.Date
import molecule.dsl.schemaDSL._
import molecule.out._

object Db extends Db_0

trait Db {
//  class txInstant[NS] extends OneLong[NS]
}

trait Db_0 extends Db with Molecule_0[Db_0, Db_1] {
//  val txInstant: txInstant[Db_1[Date], Nothing] with Db_1[Date] = ???
}

trait Db_1[T1] extends Db with Molecule_1[Db_1, Db_2, T1]{
//  val txInstant: txInstant[Db_2[T1, Date], Nothing] with Db_2[T1, Date]
}
trait Db_2[T1, T2] extends Db with Molecule_2[Db_2, Nothing, T1, T2]
