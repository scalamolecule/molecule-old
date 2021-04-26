package molecule.core.marshalling

import molecule.datomic.base.ast.transactionModel._

object Stmts2Edn {

  val buf = new StringBuilder()

  def apply(stmts: Seq[Statement]): String = {
    buf.clear()
    stmts.foreach { stmt =>
      buf.append("\n")
      buf.append(addStmt(stmt))
    }
    s"[${buf.toString}\n]"
  }

  val addStmt = (stmt: Statement) => stmt match {
    case Add(e, a, v, _)       => s"[:db/add ${eid(e)} $a ${value(v)}]"
    case Retract(e, a, v, _)   => s"[:db/retract ${eid(e)} $a ${value(v)}]"
    case RetractEntity(e)      => s"[:db/retractEntity ${eid(e)}]"
    case Cas(e, a, oldV, v, _) => s"[:db.fn/cas ${eid(e)} $a ${value(v)}]"
  }

  def eid(e: Any): String = e match {
    case TempId(part, i) => s"#db/id[$part $i]"
    case "datomic.tx"    => "datomic.tx"
    case e               => s"$e"
  }


  def value(v: Any): String = v match {
    case TempId(part, i)    => s"#db/id[$part $i]"
    case i: Int             => s"$i"
    case f: Float           => s"$f"
    case bigInt: BigInt     => s"$bigInt"
    case bigDec: BigDecimal => s"$bigDec"
    case other              => s"$other"
  }
}
