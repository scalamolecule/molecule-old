package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.util.{Collections, Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import scala.collection.mutable.ListBuffer

object Adhoc extends AsyncTestSuite {

  lazy val tests = Tests {

    //    "adhoc" - bidirectional { implicit conn =>
    "adhoc" - core { implicit conn =>

      for {
        _ <- Future(1 ==> 1)
        //        _ <- Future(2 ==> 2)

        _ <- m(Ref1.str1.Nss * Ns.int.ref1$) insert List(
          ("A", List((1, Some(42L)), (2, None))),
          ("B", List())
        )

        _ <- m(Ref1.str1.Nss *? Ns.int.ref1).get.map(_.sortBy(_._1) ==> List(
          ("A", List((1, 42L))),
          ("B", List())
        ))

      } yield ()
    }
  }
}
