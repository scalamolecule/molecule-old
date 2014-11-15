package molecule.ast
import datomic.Util

object transaction {

  sealed trait Statement {
    val action: String
//    val e: Any
    val e: Object
    val a: String
    val v: Any
    //    val t: Any
//    def toJava = Util.list(action, e.asInstanceOf[Object], a.asInstanceOf[Object], v.asInstanceOf[Object])
    def toJava = Util.list(action, e, a.asInstanceOf[Object], v.asInstanceOf[Object])
  }
//  case class Add(e: Any, a: String, v: Any) extends Statement {
  case class Add(e: Object, a: String, v: Any) extends Statement {
    val action = ":db/add"
  }
//  case class Retract(e: Any, a: String, v: Any) extends Statement {
  case class Retract(e: Object, a: String, v: Any) extends Statement {
    val action = ":db/retract"
  }

  case class Prefix(s: String)
  case class Values(vs: Any, prefix: Option[String] = None)
}
