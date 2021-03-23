package molecule.core.transform

import molecule.datomic.base.ast.transactionModel.Statement

trait Model2Statements {

  val stmtsModel: Seq[Statement]

  def insertStmts(dataRows: Seq[Seq[Any]]): Seq[Seq[Statement]]

  def saveStmts(): Seq[Statement]

  def updateStmts(): Seq[Statement]
}
