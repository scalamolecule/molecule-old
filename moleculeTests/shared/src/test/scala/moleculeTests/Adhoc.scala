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


        _ <- Ns.str.ints.bool.insert("foo", Set(1, 2, 3), true)
        _ <- Ns.str.ints.bool.get.map(_ ==> List(("foo", Set(1, 2, 3), true)))


//        _ <- m(Ns.str.Refs1 * Ref1.str1.int1$) insert List(
//          ("a", List(("a1", Some(11)))),
//          ("b", List(("b1", None))))
//
//        // Now there's a ref from entity with "b" to entity with "b1"
//        _ <- m(Ns.str.Refs1 * Ref1.str1.int1$).get.map(_.sortBy(_._1) ==> List(
//          ("a", List(("a1", Some(11)))),
//          ("b", List(("b1", None)))))

//        _ <- m(Ns.str.Refs1 * (Ref1.str1$.int1.Refs2 * Ref2.str2.int2$)) insert List(
//          ("a", List(
//            (None, 11, List(
//              ("a2", Some(12)))))),
//          ("b", List(
//            (Some("b1"), 21, List(
//              ("b2", None))))))

//        _ <- m(Ns.str.Refs1 * (Ref1.str1$.int1.Refs2 * Ref2.str2.int2$)).get.map(_.sortBy(_._1) ==> List(
//          ("a", List(
//            (None, 11, List(
//              ("a2", Some(12)))))),
//          ("b", List(
//            (Some("b1"), 21, List(
//              ("b2", None)))))))

      } yield ()
    }
  }
}
