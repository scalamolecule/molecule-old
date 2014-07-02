package molecule.util
import datomic.Connection
import molecule.ast.model._
import molecule.ast.query._
import molecule.ast.schemaDSL._
import molecule.db.DatomicFacade
import molecule.transform.{Model2Transaction, Query2String}
import molecule.{InputMolecule, Molecule}
import org.specs2.mutable._


trait MoleculeSpec extends Specification with DatomicFacade {

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def formatTx(tx: Seq[Seq[Any]]) = {
    val longestAction = tx.map(stmt => stmt(0).toString.length).max
    val longestAttr = tx.map(stmt => stmt(2).toString.length).max
    val longestValue = tx.map(stmt => stmt(3).toString.length).max

    // Increment temporary ids in a controlled way so that we can test
    val ids = tx.foldLeft(Map[String, String]()) { case (ids, stmt) =>
      val rawId = stmt(1).toString
      rawId match {
        case r"#db/id\[:db.part/user -\d{7}\]" =>
          if (ids.contains(rawId)) ids else ids + (rawId -> ("#db/id[:db.part/user -" + (1000001 + ids.size) + "]"))
        case other                             => ids + (other.toString -> other.toString)
      }
    }
    val tx2 = tx.map { stmt =>
      val newId = ids.getOrElse(stmt(1).toString, sys.error("missing stmt id"))
      val newValue = ids.getOrElse(stmt(3).toString, stmt(3).toString)
      List(
        stmt(0) + " " * (longestAction - stmt(0).toString.length),
        newId,
        stmt(2) + " " * (longestAttr - stmt(2).toString.length),
        newValue + " " * (longestValue - newValue.length)
      )
    }

    tx2.map(l => l.mkString("List(  ", ",   ", "  )")).mkString("List(\n  ", "\n  ", "\n)")
  }

  def formatInputs(query: Query) = {
    val rules = if (query.in.rules.isEmpty) ""
    else {
      val p = (expr: QueryExpr) => Query2String(query).p(expr)
      query.in.rules map p mkString ("[", "\n     ", "]")
    }
    val first = if (query.in.rules.isEmpty) Seq("datomic.db.Db@xxx") else Seq("datomic.db.Db@xxx", rules)
    val allInputs = first ++ inputs(query)
    if (allInputs.size == 1)
      ""
    else
      "\n\nINPUTS:" + allInputs.zipWithIndex.map(e => (e._2 + 1) + " " + e._1).mkString("\nList(\n  ", "\n  ", "\n)")
  }

  implicit class dsl2model2query2string(molecule: Molecule)(implicit conn: Connection) {
    def -->(model: Model) = new {
      molecule._model === model

      // Molecule -> query
      def -->(query: Query) = new {
        molecule.q === query
        def -->(queryString: String) = {
          query.pretty(30) + formatInputs(query) === queryString
        }
      }

      // Input molecule + insert data
      def -->(data: Seq[Seq[Any]]) = new {
        def -->(txString: String) = {
          val tx = Model2Transaction(conn, model, data)
          formatTx(tx) === txString
        }
      }
    }

    def -->(queryString: String) = {
      molecule.q.pretty(30) + formatInputs(molecule.q) === queryString
    }

    def -->(data: Seq[Seq[Any]]) = new {
      def -->(txString: String) = {
        val tx = Model2Transaction(conn, molecule._model, data)
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
        val t = Model2Transaction
        val (attrs, args) = t.getNonEmptyAttrs(model).unzip
        val rawMolecules = t.chargeMolecules(attrs, Seq(args))
        val molecules = t.groupNamespaces(rawMolecules)
        val tx: Seq[Seq[Any]] = t.upsertTransaction(conn.db, molecules, updateMolecule.ids)
        formatTx(tx) === txString
      }
    }

    def -->(txString: String) = {
      val t = Model2Transaction
      val (attrs, args) = t.getNonEmptyAttrs(Model(updateMolecule.elements)).unzip
      val rawMolecules = t.chargeMolecules(attrs, Seq(args))
      val molecules = t.groupNamespaces(rawMolecules)
      val tx: Seq[Seq[Any]] = t.upsertTransaction(conn.db, molecules, updateMolecule.ids)
      formatTx(tx) === txString
    }
  }

  def testInsertMolecule(insertMolecule: Insert, ids: Seq[Long] = Seq())(implicit conn: Connection) = new {
    def -->(model: Model) = new {
      Model(insertMolecule.elements) === model
      def -->(txString: String) = new {
        val t                 = Model2Transaction
        val (attrs, args)     = t.getNonEmptyAttrs(model).unzip
        val rawMolecules      = t.chargeMolecules(attrs, Seq(args))
        val molecules         = t.groupNamespaces(rawMolecules)
        val tx: Seq[Seq[Any]] = t.upsertTransaction(conn.db, molecules, ids)
        formatTx(tx) === txString
      }
    }

    def -->(txString: String) = {
      val t = Model2Transaction
      val (attrs, args) = t.getNonEmptyAttrs(Model(insertMolecule.elements)).unzip
      val rawMolecules = t.chargeMolecules(attrs, Seq(args))
      val molecules = t.groupNamespaces(rawMolecules)
      val tx: Seq[Seq[Any]] = t.upsertTransaction(conn.db, molecules, ids)
      formatTx(tx) === txString
    }
  }
}