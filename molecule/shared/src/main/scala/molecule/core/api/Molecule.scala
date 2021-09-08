package molecule.core.api

import molecule.core.ast.elements.Model
import molecule.datomic.base.ast.query.Query

/** Base Molecule interface. */
abstract class Molecule(
  private val model: Model,
  private val queryData: (Query, String, Option[Throwable])
) {

  /** Internal Model representation of a molecule.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datalog query
    *
    * @group internal
    **/
  val _model: Model = model

  /** Internal [[molecule.datomic.base.ast.query.Query Query]] representation of molecule.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datalog query
    *
    * @group internal
    **/
  val _query: Query = queryData._1

  /** Datalog query.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datalog query
    *
    * @group internal
    **/
  val _datalog: String = queryData._2


  val _inputThrowable: Option[Throwable] = queryData._3
}
