package molecule.core.marshalling

import java.util.{UUID, List => jList}
import molecule.core.api.Molecule
import molecule.core.ast.elements.Model
import molecule.core.marshalling.attrIndexes.Indexes
import molecule.core.util.Helpers
import molecule.datomic.base.ast.query.Query


/** Marshalling methods
  *
  * Methods are implemented by macros for either JS or JVM platform
  */
abstract class Marshalling[Obj, Tpl](
  model: Model,
  queryData: (Query, Option[Query], Query, Option[Query], Option[Throwable])
) extends Molecule(model, queryData) with Helpers {

  protected lazy val indexes     : Indexes = ???
  protected lazy val isNestedOpt : Boolean = false
  protected lazy val nestedLevels: Int     = 0

  // jvm
  protected def row2tpl(row: jList[AnyRef]): Tpl = ???
  protected def row2obj(row: jList[AnyRef]): Obj = ???
  protected def row2json(sb: StringBuffer, row: jList[AnyRef]): StringBuffer = ???

  // js
  protected def packed2tpl(vs: Iterator[String]): Tpl = ???
  protected def packed2obj(vs: Iterator[String]): Obj = ???
  protected def packed2json(vs: Iterator[String]): String = ???
}
