package molecule.core

import molecule.core.ast.elements.Model
import molecule.datomic.base.ast.query.{Query, QueryExpr}

/** Exceptions thrown by Molecule. */
package object exceptions {


  class MoleculeException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
    def this(message: String) = {
      this(message, null)
    }
  }

  class MoleculeCompileException(message: String, cause: Throwable) extends MoleculeException(message, cause) {
    def this(message: String) = {
      this(message, null)
    }
  }


  /** Query exception */
  class QueryException(ex: Throwable, model: Model, query: Query) extends MoleculeException(
    s"""
       |#############################################################################
       |Query failed with cause: $ex
       |
       |$model
       |
       |$query
       |
       |${query.datalog}
       |#############################################################################
   """.stripMargin, ex
  )
}
