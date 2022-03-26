package moleculeTests.tests.core.nested.branches

import molecule.core.util.Executor._
import molecule.datomic.api.out3._
import moleculeTests.dataModels.core.ref.dsl.Nested._
import utest._
import scala.concurrent.Future

object NestedBranches1 extends Base {

  lazy val tests = Tests {

    "1 level deep" - nested { implicit futConn =>
      val nested = m(
        Ns0.i0.a1.s0.R1.*(
          Ns1.i1.a1.s1))

      val trees = List(
        List(branchA(leaf)),
        List(branchA(leafs)),
        List(branchA(leaf), branchB(leafs)),
        List(branchA(leafs), branchB(leaf)),
      )

      for {
        conn <- futConn
        _ <- Ns0.i0.get // Make sure to recreate db on js side from proxy settings before testing `with`

        _ <- {
          var result = Future(())
          trees.foreach { tree =>
            result = for {
              _ <- result
              _ <- conn.testDbWith(nested.getInsertStmts(tree))
              r <- nested.get.map(_ ==> sort(tree))
            } yield r
          }
          result
        }
      } yield ()
    }
  }
}