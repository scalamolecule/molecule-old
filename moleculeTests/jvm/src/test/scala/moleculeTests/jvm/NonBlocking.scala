package moleculeTests.jvm

import java.util.Date
import molecule.datomic.api.in1_out4._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps


/** Check that transaction calls are non-blocking.
 *
 * Calling 1000 transactions in under 1 second should confirm that we are not blocking.
 *
 * See instructions in examples/README_pro.md to setup testing mbrainz
 * */
object NonBlocking extends AsyncTestSuite {

  def run[T](id: String, max: Int, test: Int => T)
            (implicit ex: ExecutionContext, conn: Future[Conn]): Long = {
    val start = (new Date).getTime // using Date to allow js platform test too
    (1 to max).foreach(test)
    val ms = (new Date).getTime - start
    println(s"$id: $ms ms")
    ms
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Transactor: save-update-retract" - mbrainz { implicit conn =>
      import moleculeTests.dataModels.examples.datomic.mbrainz.dsl.MBrainz._
      Future(
        run("Transactor Update", 1000, (n: Int) => {
          val testName = "test" + n
          for {
            e <- Artist.name("test").save.map(_.eid)
            _ <- Artist(e).name(testName).update
            _ <- e.retract
          } yield ()
        }
        ) < 1000 ==> true
      )
    }

    "Transactor: insert-retract" - mbrainz { implicit conn =>
      import moleculeTests.dataModels.examples.datomic.mbrainz.dsl.MBrainz._
      Future(
        run("Transactor Insert", 1000, (n: Int) =>
          for {
            eids <- Artist.name.insert("foo" + n, "bar" + n).map(_.eids)
            _ <- retract(eids)
          } yield ()
        ) < 1000 ==> true
      )
    }


    "In-mem: save-update-retract" - core { implicit conn =>
      import moleculeTests.dataModels.core.base.dsl.CoreTest._
      Future(
        run("In-mem Update", 1000, (n: Int) => {
          val testName = "test" + n
          for {
            e <- Ns.str("test").save.map(_.eid)
            _ <- Ns(e).str(testName).update
            _ <- e.retract
          } yield ()
        }
        ) < 1000 ==> true
      )
    }

    "In-mem: insert-retract" - core { implicit conn =>
      import moleculeTests.dataModels.core.base.dsl.CoreTest._
      Future(
        run("In-mem Insert", 1000, (n: Int) =>
          for {
            eids <- Ns.str.insert("foo" + n, "bar" + n).map(_.eids)
            _ <- retract(eids)
          } yield ()
        ) < 1000 ==> true
      )
    }
  }
}