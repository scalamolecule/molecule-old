package molecule.core.util.testing

import molecule.datomic.base.ast.query._
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.ops.QueryOps._
import molecule.datomic.base.transform.Query2String
import molecule.core.util.{MoleculeSpecException, RegexMatching}
import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable}
import scala.language.postfixOps

trait MoleculeTestHelper extends RegexMatching {

  def typed[T](t: => T): Unit = {}

  def await[T](atMost: Duration)(awaitable: Awaitable[T]): T =
    Await.result(awaitable, atMost)

  def await[T](awaitable: Awaitable[T]): T =
    Await.result(awaitable, 10.seconds)


  def formatTx(tx: Seq[Statement]): String = {
    val longestAction = tx.map(stmt => stmt.action.length).max
    val longestAttr   = tx.map(stmt => stmt.a.length).max
    val longestValue  = tx.map(stmt => stmt.v.toString.length).max

    // Increment temporary ids in a controlled way so that we can test
    val ids: Map[String, String] = tx.foldLeft(Map.empty[String, String]) { case (idStmts, stmt) =>
      val rawId = stmt.e.toString
      rawId match {
        case r"""TempId\(":db.part/user", -\d{7}\)""" =>
          if (idStmts.contains(s"$rawId")) idStmts else idStmts ++ Map(s"$rawId" -> (s"""TempId(":db.part/user", -${1000001 + idStmts.size})"""))
        case r"#db/id\[:db.part/tx -\d{7}\]"          =>
          if (idStmts.contains(rawId)) idStmts else idStmts ++ Map(rawId -> ("#db/id[:db.part/tx   -" + (1000001 + idStmts.size) + "]"))
        case r"(\d{14,16})$id"                        =>
          if (idStmts.contains(rawId)) idStmts else idStmts ++ Map(rawId -> (id + "                "))
        case r"""Temp\(":(\w+)$part", -\d{7}\)"""     =>
          if (idStmts.contains(rawId)) idStmts else idStmts ++ Map(rawId -> (s"""Temp(":$part", -""" + (1000001 + idStmts.size) + ")"))
        case other                                    => idStmts ++ Map(other -> other)
      }
    }
    val tx2                      = tx.map { stmt =>
      val newId    = ids.getOrElse(stmt.e.toString, throw MoleculeSpecException("missing stmt id"))
      val newValue = ids.getOrElse(stmt.v.toString, stmt.v.toString)
      List(
        stmt.action + " " * (longestAction - stmt.action.toString.length),
        newId,
        stmt.a + " " * (longestAttr - stmt.a.toString.length),
        newValue + " " * (longestValue - newValue.length)
      )
    }

    tx2.map(l => l.mkString("List(", ",  ", ")")).mkString("List(\n  ", ",\n  ", "\n)")
  }

  def formatInputs(query: Query): String = {
    val rules     = if (query.i.rules.isEmpty) ""
    else {
      val p = Query2String(query).p
      query.i.rules.map(p).mkString("[", "\n     ", "]")
    }
    val first     = if (query.i.rules.isEmpty) Seq("datomic.db.Db@xxx") else Seq("datomic.db.Db@xxx", rules)
    val allInputs = first ++ query.inputs
    if (allInputs.size == 1)
      ""
    else
      "\n\nINPUTS:" + allInputs.zipWithIndex.map(e => s"${e._2 + 1} ${e._1}").mkString("\nList(\n  ", "\n  ", "\n)")
  }
}