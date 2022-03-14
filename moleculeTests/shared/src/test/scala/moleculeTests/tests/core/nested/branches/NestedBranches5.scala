package moleculeTests.tests.core.nested.branches

import molecule.core.util.Executor._
import molecule.datomic.api.out3._
import moleculeTests.dataModels.core.ref.dsl.Nested._
import utest._
import scala.concurrent.Future


object NestedBranches5 extends Base {

  lazy val tests = Tests {

    "5 levels deep" - nested { implicit futConn =>
      val nested = m(
        Ns0.i0.a1.s0.R1.*(
          Ns1.i1.a1.s1.R2.*(
            Ns2.i2.a1.s2.R3.*(
              Ns3.i3.a1.s3.R4.*(
                Ns4.i4.a1.s4.R5.*(
                  Ns5.i5.a1.s5))))))

      val trees = List(
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                ))))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leafs)
                ))))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf), branchA(leaf)
                ))))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf), branchA(leafs)
                ))))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leafs), branchA(leafs)
                ))))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf))),
                branchA(List(
                  branchB(leaf)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf))),
                branchA(List(
                  branchB(leafs)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leafs))),
                branchA(List(
                  branchB(leafs)))
              ))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf))),
                branchA(List(
                  branchB(leaf), branchA(leaf)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf))),
                branchA(List(
                  branchB(leaf), branchA(leafs)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf))),
                branchA(List(
                  branchB(leafs), branchA(leafs)))
              ))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leafs))),
                branchA(List(
                  branchB(leaf), branchA(leaf)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leafs))),
                branchA(List(
                  branchB(leaf), branchA(leafs)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leafs))),
                branchA(List(
                  branchB(leafs), branchA(leafs)))
              ))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf), branchB(leaf))),
                branchA(List(
                  branchB(leaf)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf), branchB(leafs))),
                branchA(List(
                  branchB(leaf)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leafs), branchB(leafs))),
                branchA(List(
                  branchB(leaf)))
              ))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf), branchB(leaf))),
                branchA(List(
                  branchB(leafs)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf), branchB(leafs))),
                branchA(List(
                  branchB(leafs)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leafs), branchB(leafs))),
                branchA(List(
                  branchB(leafs)))
              ))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf), branchB(leaf))),
                branchA(List(
                  branchB(leaf), branchA(leaf)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf), branchB(leaf))),
                branchA(List(
                  branchB(leaf), branchA(leafs)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf), branchB(leaf))),
                branchA(List(
                  branchB(leafs), branchA(leafs)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf), branchB(leafs))),
                branchA(List(
                  branchB(leafs), branchA(leafs)))
              ))))))),
        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leafs), branchB(leafs))),
                branchA(List(
                  branchB(leafs), branchA(leafs)))
              ))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                )))))))),
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                ))))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                )))))))),
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                )))))))),
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                ))))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                )))))),
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                ))))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                )))))),
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                )))))),
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                ))))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                )))),
              branchA(List(
                branchB(List(
                  branchA(leaf)
                ))))))))),

        List(
          branchA(List(
            branchB(List(
              branchA(List(
                branchB(List(
                  branchA(leaf)
                )))),
              branchA(List(
                branchB(List(
                  branchA(leaf)
                )))),
              branchA(List(
                branchB(List(
                  branchA(leaf)
                ))))))))),
      )

      for {
        conn <- futConn
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