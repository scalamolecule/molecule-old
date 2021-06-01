package moleculeTests.tests.core.obj

import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import molecule.datomic.api.in1_out3._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Aggr extends AsyncTestSuite with Helpers {

  def data(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      _ <- Ns.int.insert(1, 2, 3)
      _ <- Ns.double.insert(1.0, 2.0, 3.0)
      _ <- Ns.str.insert("a", "b", "c")
    } yield ()
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global
    "min, max, rand, sample, median" - core { implicit conn =>
      for {
        _ <- data
        // For any property

        _ <- Ns.int(min).getObj.map(_.int ==> 1)
        _ <- Ns.int(max).getObj.map(_.int ==> 3)
        _ <- Ns.int(rand).getObj.map(_.int) // 1, 2 or 3
        _ <- Ns.int(sample).getObj.map(_.int) // 1, 2 or 3
        _ <- Ns.int(median).getObj.map(_.int ==> 2)

        _ <- Ns.double(min).getObj.map(_.double ==> 1.0)
        _ <- Ns.double(max).getObj.map(_.double ==> 3.0)
        _ <- Ns.double(rand).getObj.map(_.double) // 1.0, 2.0 or 3.0
        _ <- Ns.double(sample).getObj.map(_.double) // 1.0, 2.0 or 3.0
        _ <- Ns.double(median).getObj.map(_.double ==> 2.0)

        _ <- Ns.str(min).getObj.map(_.str ==> "a")
        _ <- Ns.str(max).getObj.map(_.str ==> "c")
        _ <- Ns.str(rand).getObj.map(_.str) // a, b or c
        _ <- Ns.str(sample).getObj.map(_.str) // a, b or c
        _ <- Ns.str(median).getObj.map(_.str ==> "b")
      } yield ()
    }

    "sum" - core { implicit conn =>
      for {
        _ <- data
        // For number properties

        _ <- Ns.int(sum).getObj.map(_.int ==> 6)
        _ <- Ns.double(sum).getObj.map(_.double ==> 6.0)

        _ = compileError("""m(Ns.str(sum))""").check("",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "Can't apply `sum` aggregate to non-number attribute `str` of type `String`.")
      } yield ()
    }

    "count, countDistinct" - core { implicit conn =>
      for {
        _ <- data
        // For Int properties

        _ <- Ns.int(count).getObj.map(_.int ==> 3)
        _ <- Ns.int(countDistinct).getObj.map(_.int ==> 3)

        _ <- Ns.double(count).getObj.map(_.double).recover { case MoleculeException(err, _) =>
          err ==> "Object property `double` not available since the aggregate changes its type to `Int`. " +
            "Please use tuple output instead to access aggregate value."
        }

        // Tuple output allowed
        _ <- Ns.double(count).get.map(_.head ==> 3)
        _ <- Ns.double(countDistinct).get.map(_.head ==> 3)
        _ <- Ns.str(count).get.map(_.head ==> 3)
        _ <- Ns.str(countDistinct).get.map(_.head ==> 3)
      } yield ()
    }

    "avg, variance, stddev" - core { implicit conn =>
      for {
        _ <- data
        // For Double properties

        _ <- Ns.double(avg).getObj.map(_.double ==> 2.0)
        _ <- Ns.double(variance).getObj.map(_.double ==> 0.6666666666666666)
        _ <- Ns.double(stddev).getObj.map(_.double ==> 0.816496580927726)

        _ <- Ns.int(avg).getObj.map(_.int).recover { case MoleculeException(err, _) =>
          err ==> "Object property `int` not available since the aggregate changes its type to `Double`. " +
            "Please use tuple output instead to access aggregate value."
        }

        // Tuple output allowed
        _ <- Ns.int(avg).get.map(_.head ==> 2.0)
        _ <- Ns.int(variance).get.map(_.head ==> 0.6666666666666666)
        _ <- Ns.int(stddev).get.map(_.head ==> 0.816496580927726)
      } yield ()
    }

    "Aggregates returning multiple values" - core { implicit conn =>
      for {
        _ <- data
        // Can be accessed as tuple data only

        _ <- Ns.int(min(2)).getObj.map(_.int).recover { case MoleculeException(err, _) =>
          err ==> "Object property `int` not available since the aggregate changes its type to `List[Int]`. " +
            "Please use tuple output instead to access aggregate value."
        }

        // Tuple output allowed
        _ <- Ns.int(min(2)).get.map(_.head.sorted ==> List(1, 2))
        _ <- Ns.int(max(2)).get.map(_.head.sorted ==> List(2, 3))
        _ <- Ns.int(distinct).get.map(_.head.sorted ==> List(1, 2, 3))
        _ <- Ns.int(rand(2)).get.map(_.head) // two of 1, 2 or 3
        _ <- Ns.int(sample(2)).get.map(_.head) // two of 1, 2 or 3

        _ <- Ns.str(min(2)).get.map(_.head.sorted ==> List("a", "b"))
        _ <- Ns.str(max(2)).get.map(_.head.sorted ==> List("b", "c"))
        _ <- Ns.str(distinct).get.map(_.head.sorted ==> List("a", "b", "c"))
        _ <- Ns.str(rand(2)).get.map(_.head) // two of a, b or c
        _ <- Ns.str(sample(2)).get.map(_.head) // two of a, b or c
      } yield ()
    }
  }
}
