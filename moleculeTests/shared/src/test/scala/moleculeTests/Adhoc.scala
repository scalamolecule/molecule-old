package moleculeTests

import java.util.UUID
import molecule.core.exceptions.MoleculeException
import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.DateHandling
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object Adhoc extends AsyncTestSuite {


  lazy val tests = Tests {

    //    "adhoc" - bidirectional { implicit conn =>
    "adhoc" - core { implicit conn =>


      for {

//        _ <- m(Ns.int.Refs1 * Ref1.int1) insert List(
//          (1, List(1, 2))
//        )

        _ <- m(Ns.int.Refs1.int1).get

        _ <- m(Ns.int.Refs1 * Ref1.int1).get
//                .map(_ ==> List(
//          (1, List(1, 2))
//        ))



//        _ <- m(Ns.str.Refs1 * Ref1.str1.int1$) insert List(
//          ("a", List(("a1", Some(11)))),
//          ("b", List(("b1", None))))
//
//        // Now there's a ref from entity with "b" to entity with "b1"
//        _ <- m(Ns.str.Refs1 * Ref1.str1.int1$).get.map(_.sortBy(_._1) ==> List(
//          ("a", List(("a1", Some(11)))),
//          ("b", List(("b1", None)))))

      } yield ()
    }
  }
}
