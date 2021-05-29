package molecule.core.api

import molecule.core.ast.elements.Model
import molecule.datomic.base.ast.query.Query

/** Base Molecule interface. */
abstract class Molecule(
  private val model: Model,
  private val queryData: (Query, Option[Query], Query, Option[Query], Option[Throwable])
) {

  /** Internal Model representation of a molecule.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datomic query string
    *
    * @group internal
    **/
  val _model: Model = model

  /** Internal [[molecule.datomic.base.ast.query.Query Query]] representation of molecule.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datomic query string
    *
    * @group internal
    **/
  val _query: Query = queryData._1

  /** Internal optional [[molecule.datomic.base.ast.query.Query Query]] representation of nested molecule with added entity search for each level.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datomic query string
    *
    * @group internal
    **/
  val _nestedQuery: Option[Query] = queryData._2

  /** Internal un-optimized [[molecule.datomic.base.ast.query.Query Query]] representation molecule.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datomic query string
    *
    * @group internal
    **/
  val _rawQuery: Query = queryData._3

  /** Internal un-optimized optional [[molecule.datomic.base.ast.query.Query Query]] representation of nested molecule with added entity search for each level.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datomic query string
    *
    * @group internal
    **/
  val _rawNestedQuery: Option[Query] = queryData._4



  val _inputThrowable: Option[Throwable] = queryData._5
}
