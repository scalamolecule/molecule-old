package molecule.core.marshalling

import molecule.datomic.base.ast.transactionModel.Statement

object Stmts2Edn {

  val buf = new StringBuffer()

  def apply(stmts: Seq[Statement]): String = {
    stmts.foreach { stmt =>
      addStmt(stmt)
      buf.append("\n")
    }
    buf.toString
    s"""[
${buf.toString}
]"""
  }

  val addStmt = (stmt: Statement) => {

  }
}
