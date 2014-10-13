package molecule.schemas

import java.util.Date
import molecule.dsl.schemaDSL._
import molecule.out._

object Db extends Db_0

trait Db {
//  class txInstant[NS] extends OneLong[NS]
}

trait Db_0 extends Db with Molecule_0[Db_0, Db_1] {
  val valueType  : Db_1[String   ] with OneString   [Db_1[String   ], Nothing] = ???
}

trait Db_1[A] extends Db with Molecule_1[Db_1, Db_2, A]{
  val valueType   : Db_2[A, String] with OneString[Db_2[A, String], Nothing] = ???
}
trait Db_2[A, B] extends Db with Molecule_2[Db_2, Nothing, A, B] {
  val valueType   : Db_3[A, B, String] with OneString[Db_3[A, B, String], Nothing] = ???
}

trait Db_3[A, B, C] extends Db with Molecule_3[Db_3, Nothing, A, B, C]
