package molecule.core

import molecule.core.ast.elements.Model
import molecule.datomic.base.ast.query.{Query, QueryExpr}

/** Exceptions thrown by Molecule. */
package object exceptions {


  case class MoleculeException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
    def this(message: String) = {
      this(message, null)
    }
  }

  case class MoleculeCompileException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
    def this(message: String) = {
      this(message, null)
    }
  }


  /** Query exception */
  case class QueryException(ex: Throwable, model: Model, query: Query) extends RuntimeException(
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
