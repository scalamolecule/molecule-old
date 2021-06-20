package molecule.core

import molecule.core.ast.elements.Model
import molecule.datomic.base.ast.query.Query

/** Exceptions thrown by Molecule. */
package object exceptions {


  case class MoleculeException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)

  case class MoleculeCompileException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)


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
