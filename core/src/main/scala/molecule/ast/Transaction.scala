package molecule.ast
import datomic.Util


object transaction {

  //  case class tx(rows: Seq[Row])

//  case class tx(stmts: Seq[Statement])

  sealed trait Statement {
    val action: String
    val e: Object
    val a: String
    val v: Any
    //    val t: Any
    def toJava = Util.list(action, e, a.asInstanceOf[Object], v.asInstanceOf[Object])
  }
  case class Add(e: Object, a: String, v: Any) extends Statement {
    val action = ":db/add"
  }
  case class Retract(e: Object, a: String, v: Any) extends Statement {
    val action = ":db/retract"
  }
}
