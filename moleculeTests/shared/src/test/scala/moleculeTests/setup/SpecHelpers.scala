//package moleculeTests.setup
//
//import molecule.core.api.InputMolecule
//import molecule.core.ast.Molecule
//import molecule.core.ast.elements.Model
//import molecule.core.transform.ModelTransformer
//import molecule.core.util.testing.MoleculeTestHelper
//import molecule.datomic.base.ast.query.Query
//import molecule.datomic.base.facade.Conn
//import org.specs2.mutable.Specification
//import scala.concurrent.Future
//
//
//trait SpecHelpers extends Specification with MoleculeTestHelper {
//
//
//  implicit class dsl2Model2query2string(molecule: Molecule)(implicit conn: Future[Conn]) {
//    def -->(model: Model) = new {
//      molecule._model === model
//
//      // Molecule -> query
//      def -->(query: Query) = new {
//        molecule._query.toString === query.toString
//        def -->(queryString: String) = {
//          query.datalog(30) + formatInputs(query) === queryString
//        }
//      }
//
//      // Input molecule + insert data
//      def -->(data: Seq[Seq[Any]]) = new {
//        def -->(txString: String) = {
//          val tx = ModelTransformer(conn, model).insertStmts(data)
//          formatTx(tx) === txString
//        }
//        // Inspect
//        def --->(txString: String) = {
//          val tx = ModelTransformer(conn, model).insertStmts(data)
//          tx foreach println
//          formatTx(tx) === txString
//        }
//      }
//    }
//
//    def -->(queryString: String) = {
//      molecule._query.datalog(30) + formatInputs(molecule._query) === queryString
//    }
//
//    def -->(data: Seq[Seq[Any]]) = new {
//      def -->(txString: String) = {
//        val tx = ModelTransformer(conn, molecule._model).insertStmts(data)
//        formatTx(tx) === txString
//      }
//    }
//  }
//
//  implicit class inputDsl2Model2query2string(inputMolecule: InputMolecule) {
//    def -->(model: Model) = new {
//      inputMolecule._model === model
//      def -->(query: Query) = new {
//        inputMolecule._query === query
//        def -->(queryString: String) = {
//          query.datalog(30) === queryString
//        }
//      }
//    }
//
//    def -->(queryString: String) = {
//      inputMolecule._query.datalog(30) === queryString
//    }
//  }
//}