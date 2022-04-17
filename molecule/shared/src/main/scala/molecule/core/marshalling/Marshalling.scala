package molecule.core.marshalling

import java.util.{Comparator, List => jList}
import molecule.core.api.Molecule
import molecule.core.ast.elements.Model
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.ast.{SortCoordinate, nodes}
import molecule.core.util.Helpers
import molecule.datomic.base.ast.query.Query
import scala.concurrent.Future


/** Marshalling methods
 *
 * Methods are implemented by macros for either JS or JVM platform
 */
abstract class Marshalling[Obj, Tpl](model: Model, queryData: (Query, String, Option[Throwable]))
  extends Comparator[jList[AnyRef]] with Molecule with Helpers {

  def _model: Model = model
  def _query: Query = queryData._1
  def _datalog: String = queryData._2
  def _inputThrowable: Option[Throwable] = queryData._3


  protected def obj: nodes.Obj = ???
  protected def isOptNested: Boolean = false
  protected def nestedLevels: Int = 0
  protected def refIndexes: List[List[Int]] = List(List.empty[Int])
  protected def tacitIndexes: List[List[Int]] = List(List.empty[Int])
  protected def sortCoordinates: List[List[SortCoordinate]] = List.empty[List[SortCoordinate]]

  // Sorting
  protected def sortRows: Boolean = false
  def compare(a: jList[AnyRef], b: jList[AnyRef]): Int = ???

  // jvm
  protected def row2tpl(row: jList[AnyRef]): Tpl = ???
  protected def row2obj(row: jList[AnyRef]): Obj = ???
  protected def row2json(row: jList[AnyRef], sb: StringBuffer): StringBuffer = ???

  // js
  protected def packed2tpl(vs: Iterator[String]): Tpl = ???
  protected def packed2obj(vs: Iterator[String]): Obj = ???
  protected def packed2json(vs: Iterator[String], sb: StringBuffer): StringBuffer = ???


  def offsetException(offset: Int): Future[Nothing] = Future.failed(
    MoleculeException("Offset has to be >= 0. Found: " + offset))

  def limit0exception: Future[Nothing] = Future.failed(
    MoleculeException("Limit cannot be 0. " +
      "Please use a positive number to get rows from start, or a negative number to get rows from end."))
}
