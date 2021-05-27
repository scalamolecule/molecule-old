package moleculeTests.jvm.examples.datomic.seattle

import java.io.FileReader
import java.util.{List => jList}
import datomic.Util
import molecule.datomic.api.in2_out8._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.setup.examples.seattle.SeattleData
import moleculeTests.tests.examples.datomic.seattle.dsl.Seattle._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object SeattleTests extends AsyncTestSuite with SeattleData {

  def loadData(implicit conn: Future[Conn], ec: ExecutionContext) = {
    Community.name.url.tpe.orgtype$.category$.Neighborhood.name.District.name.region$ insert seattleData
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Working with time" - seattle { implicit conn =>
      for {
        _ <- loadData

        r1 <- Schema.t.get
        schemaTxT = r1.head
        r2 <- Community.name_.t.get
        dataTxT = r2.head

        // Take all Community entities
        communities = m(Community.e.name_)

        // Revisiting the past

        _ <- communities.getAsOf(schemaTxT).map(_.size ==> 0)
        _ <- communities.getAsOf(dataTxT).map(_.size ==> 150)

        _ <- communities.getSince(schemaTxT).map(_.size ==> 150)
        _ <- communities.getSince(dataTxT).map(_.size ==> 0)

        // Imagining the future
        data_rdr2 = new FileReader("moleculeTests/jvm/resources/tests/examples/seattle/seattle-data2upper.dtm")
        newDataTx = Util.readAll(data_rdr2).get(0).asInstanceOf[jList[jList[_]]]

        // future db
        _ <- communities.getWith(newDataTx).map(_.size ==> 258)

        // existing db
        _ <- communities.get.map(_.size ==> 150)

        // transact
        _ <- conn.map(_.transactRaw(newDataTx))

        // updated db
        _ <- communities.get.map(_.size ==> 258)

        // number of new transactions
        _ <- communities.getSince(dataTxT).map(_.size ==> 108)
      } yield ()
    }
  }
}