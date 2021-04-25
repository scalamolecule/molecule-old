package molecule.datomic.base.ast

import molecule.core.ast.elements._
import molecule.core.util.JavaUtil


/** Datomic transaction representation and operations. */
object transactionModel extends JavaUtil {

  /** Transaction statement. */
  sealed trait Statement {
    val action: String
    val e     : Any
    val a     : String
    val v     : Any
    val gv    : GenericValue
    val oldV: Any = null
  }

  private def eid(e: Any): String = {
    val e1  = if (e.isInstanceOf[Long]) s"${e}L" else e.toString
    val pad = " " * (8 - e1.length)
    e1 + pad
  }

  case class Add(e: Any, a: String, v: Any, gv: GenericValue) extends Statement {
    val action = ":db/add"

    override def toString: String = {
      val pad = " " * (25 - a.length)
      if (v.isInstanceOf[AbstractValue])
        s"""List(":db/add",     ${eid(e)}, "$a", $pad $v, $gv)"""
      else
        s"""list(":db/add",     ${eid(e)}, "$a", $pad $v)"""
    }
  }

  // Todo: Implement in updates?
  // Current Add's let Datomic automatically create a retract datom for each new update.
  case class Cas(e: Any, a: String, override val oldV: Any, v: Any, gv: GenericValue) extends Statement {
    val action = ":db.fn/cas"
  }

  case class Retract(e: Any, a: String, v: Any, gv: GenericValue = NoValue) extends Statement {
    val action = ":db/retract"

    override def toString: String = {
      val pad = " " * (25 - a.length)
      if (v.isInstanceOf[AbstractValue])
        s"""List(":db/retract", ${eid(e)}, "$a", $pad $v, $gv)"""
      else
        s"""list(":db/retract", ${eid(e)}, "$a", $pad $v)"""
    }
  }

  case class RetractEntity(e: Any) extends Statement {
    val action = ":db/retractEntity"
    val a      = ""
    val v      = ""
    val gv     = NoValue

    override def toString: String = {
      s"""list(":db/retractEntity", ${eid(e)})"""
    }
  }

  case class TempId(part: String, i: Int) {
    override def toString: String = s"""TempId("$part", $i)"""
  }

  trait AbstractValue
  case class Eid(id: Long) extends AbstractValue
  case class Eids(ids: Seq[Any]) extends AbstractValue
  case class Prefix(s: String) extends AbstractValue
  case class Values(vs: Any, prefix: Option[String] = None) extends AbstractValue
}
