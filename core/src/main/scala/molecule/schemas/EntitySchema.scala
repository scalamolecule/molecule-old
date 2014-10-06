package molecule.schemas

import java.util.Date
import molecule.dsl.schemaDSL._
import molecule.out._

object Entity extends Entity_0

trait Entity {
  class txInstant[NS] extends OneLong[NS]
}

trait Entity_0 extends Entity with Molecule_0 {
  val txInstant: txInstant[Entity_1[Date]] with Entity_1[Date] = ???
}

trait Entity_1[T1] extends Entity with Molecule_1[T1]{
  val txInstant: txInstant[Entity_2[T1, Date]] with Entity_2[T1, Date]
}
trait Entity_2[T1, T2] extends Entity with Molecule_2[T1, T2]
