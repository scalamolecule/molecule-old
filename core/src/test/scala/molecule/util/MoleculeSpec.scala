package molecule.util
import datomic.Connection
import molecule.ast.model._
import molecule.ast.query._
import molecule.ast.transaction._
import molecule.DatomicFacade
import molecule.dsl.schemaDSL._
import molecule.in.InputMolecule
import molecule.out.OutputMolecule
import molecule.transform.{Model2Transaction, Query2String}
import org.specs2.mutable._


trait MoleculeSpec extends Specification with DatomicFacade {

  def typed[T](t: => T) {}

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def formatTx(tx: Seq[Statement]) = {
    val longestAction = tx.map(stmt => stmt.action.length).max
    val longestAttr = tx.map(stmt => stmt.a.toString.length).max
    val longestValue = tx.map(stmt => stmt.v.toString.length).max

    // Increment temporary ids in a controlled way so that we can test
    val ids = tx.foldLeft(Map[String, String]()) { case (ids, stmt) =>
      val rawId = stmt.e.toString
      rawId match {
        case r"#db/id\[:db.part/user -\d{7}\]" =>
          if (ids.contains(rawId)) ids else ids + (rawId -> ("#db/id[:db.part/user -" + (1000001 + ids.size) + "]"))
        case other                             => ids + (other.toString -> other.toString)
      }
    }
    val tx2 = tx.map { stmt =>
      val newId = ids.getOrElse(stmt.e.toString, sys.error("missing stmt id"))
      val newValue = ids.getOrElse(stmt.v.toString, stmt.v.toString)
      List(
        stmt.action + " " * (longestAction - stmt.action.toString.length),
        newId,
        stmt.a + " " * (longestAttr - stmt.a.toString.length),
        newValue + " " * (longestValue - newValue.length)
      )
    }

    tx2.map(l => l.mkString("List(  ", ",   ", "  )")).mkString("List(\n  ", "\n  ", "\n)")
  }

  def formatInputs(query: Query) = {
    val rules = if (query.in.rules.isEmpty) ""
    else {
      val p = (expr: QueryExpr) => Query2String(query).p(expr)
      query.in.rules map p mkString("[", "\n     ", "]")
    }
    val first = if (query.in.rules.isEmpty) Seq("datomic.db.Db@xxx") else Seq("datomic.db.Db@xxx", rules)
    val allInputs = first ++ inputs(query)
    if (allInputs.size == 1)
      ""
    else
      "\n\nINPUTS:" + allInputs.zipWithIndex.map(e => (e._2 + 1) + " " + e._1).mkString("\nList(\n  ", "\n  ", "\n)")
  }

  implicit class dsl2model2query2string(molecule: OutputMolecule)(implicit conn: Connection) {
    def -->(model: Model) = new {
      molecule._model === model

      // Molecule -> query
      def -->(query: Query) = new {
        molecule._query === query
        def -->(queryString: String) = {
          query.pretty(30) + formatInputs(query) === queryString
        }
      }

      // Input molecule + insert data
      def -->(data: Seq[Seq[Any]]) = new {
        def -->(txString: String) = {
          val tx = Model2Transaction(conn, model, data).tx
          formatTx(tx) === txString
        }
      }
    }

    def -->(queryString: String) = {
      molecule._query.pretty(30) + formatInputs(molecule._query) === queryString
    }

    def -->(data: Seq[Seq[Any]]) = new {
      def -->(txString: String) = {
        val tx = Model2Transaction(conn, molecule._model, data).tx
        formatTx(tx) === txString
      }
    }
  }

  implicit class inputDsl2model2query2string(inputMolecule: InputMolecule) {
    def -->(model: Model) = new {
      inputMolecule.model === model
      def -->(query: Query) = new {
        inputMolecule.query === query
        def -->(queryString: String) = {
          query.pretty(30) === queryString
        }
      }
    }

    def -->(queryString: String) = {
      inputMolecule.query.pretty(30) === queryString
    }
  }

  implicit class updateDsl2model2txString(updateMolecule: Update)(implicit conn: Connection) {
    def -->(model: Model) = new {
      Model(updateMolecule.elements) === model
      def -->(txString: String) = {
        val tx = Model2Transaction(conn, model, Seq(), updateMolecule.ids).tx
        formatTx(tx) === txString
      }
    }

    def -->(txString: String) = {
      val t = Model2Transaction
      val tx = Model2Transaction(conn, Model(updateMolecule.elements), Seq(), updateMolecule.ids).tx
      formatTx(tx) === txString
    }
  }

  def testInsertMolecule(insertMolecule: Insert, ids: Seq[Long] = Seq())(implicit conn: Connection) = new {
    def -->(model: Model) = new {
      Model(insertMolecule.elements) === model
      def -->(txString: String) = new {
        val tx = Model2Transaction(conn, model, Seq(), ids).tx
        formatTx(tx) === txString
      }
    }

    def -->(txString: String) = {
      val t = Model2Transaction
      val tx = Model2Transaction(conn, Model(insertMolecule.elements), Seq(), ids).tx
      formatTx(tx) === txString
    }
  }
}