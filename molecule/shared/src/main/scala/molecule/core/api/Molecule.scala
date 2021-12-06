package molecule.core.api

import molecule.core.ast.elements.Model
import molecule.datomic.base.ast.query.Query

/** Base Molecule interface. */
trait Molecule {

  /** Internal Model representation of a molecule.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datalog query
    *
    * @group internal
    **/
  def _model: Model

  /** Internal [[molecule.datomic.base.ast.query.Query Query]] representation of molecule.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datalog query
    *
    * @group internal
    **/
  def _query: Query

  /** Datalog query.
    * <br><br>
    * Molecule transforms custom boilerplate DSL constructs to Datomic queries in 3 steps:
    * <br><br>
    * Custom DSL molecule --> Model --> Query --> Datalog query
    *
    * @group internal
    **/
  def _datalog: String


  def _inputThrowable: Option[Throwable]
}
