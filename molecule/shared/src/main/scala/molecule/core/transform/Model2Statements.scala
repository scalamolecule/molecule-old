package molecule.core.transform

import molecule.datomic.base.ast.transactionModel.Statement

trait Model2Statements {

  val stmtsModel: Seq[Statement]

  def insertStmts(dataRows: Iterable[Seq[Any]]): Seq[Statement]

  def saveStmts(): Seq[Statement]

  def updateStmts(): Seq[Statement]
}
