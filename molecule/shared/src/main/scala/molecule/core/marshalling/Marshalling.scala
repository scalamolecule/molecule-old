package molecule.core.marshalling

import java.util.{List => jList}
import molecule.core.api.Molecule
import molecule.core.ast.elements.Model
import molecule.core.util.Helpers
import molecule.datomic.base.ast.query.Query


/** Marshalling methods
 *
 * Methods are implemented by macros for either JS or JVM platform
 */
abstract class Marshalling[Obj, Tpl](model: Model, queryData: (Query, String, Option[Throwable]))
  extends Molecule with Helpers {

  def _model: Model = model
  def _query: Query = queryData._1
  def _datalog: String = queryData._2
  def _inputThrowable: Option[Throwable] = queryData._3


  protected def obj: nodes.Obj = ???
  protected def isOptNested: Boolean = false
  protected def nestedLevels: Int = 0
  protected def refIndexes: List[List[Int]] = List(List.empty[Int])
  protected def tacitIndexes: List[List[Int]] = List(List.empty[Int])

  // jvm
  protected def row2tpl(row: jList[AnyRef]): Tpl = ???
  protected def row2obj(row: jList[AnyRef]): Obj = ???
  protected def row2json(row: jList[AnyRef], sb: StringBuffer): StringBuffer = ???

  // js
  protected def packed2tpl(vs: Iterator[String]): Tpl = ???
  protected def packed2obj(vs: Iterator[String]): Obj = ???
  protected def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ???
}
