//package moleculeTests.tests.core.ref.nested
//
//import moleculeTests.tests.core.ref.dsl.Nested._
//import molecule.datomic.api.out3._
//import moleculeTests.setup.AsyncTestSuite
//import utest._
//import scala.concurrent.ExecutionContext.Implicits.global
//
//object NestedBranches extends AsyncTestSuite {
//
//    def branchA[T](leafs: Seq[T]): (Int, String, Seq[T]) = (1, "a", leafs)
//    def branchB[T](leafs: Seq[T]): (Int, String, Seq[T]) = (2, "b", leafs)
//    val leaf  = List((10, "a"))
//    val leafs = List((20, "b"), (21, "bb"))
//
//lazy val tests = Tests {
//
//"1 level deep" - nested {   implicit conn =>
//    val nested = m(
//      Ns0.i0.s0.R1.*(
//        Ns1.i1.s1))
//
//    val data = List(
//      List(branchA(leaf)),
//      List(branchA(leafs)),
//      List(branchA(leaf), branchB(leafs)),
//      List(branchA(leafs), branchB(leaf)),
//    )
//      for {
//    data.foreach { d =>
//      conn.testDbWith(nested.getInsertStmts(d))
//      nested.get.map(_ ==> d)
//    }
//    } yield ()
//    }
//
//"2 levels deep" - nested {   implicit conn =>
//      for {
//    val nested = m(
//      Ns0.i0.s0.R1.*(
//        Ns1.i1.s1.R2.*(
//          Ns2.i2.s2)))
//
//    val data = List(
//      List(branchA(List(branchA(leaf)))),
//      List(branchA(List(branchA(leafs)))),
//      List(branchA(List(branchA(leaf), branchB(leafs)))),
//      List(branchA(List(branchA(leafs), branchB(leaf)))),
//      List(
//        branchA(List(branchB(leaf))),
//        branchB(List(branchA(leafs))),
//      ),
//      List(
//        branchA(List(branchB(leaf))),
//        branchB(List(branchA(leafs))),
//      ),
//      List(
//        branchA(List(branchA(leaf), branchB(leaf))),
//        branchB(List(branchA(leafs), branchB(leafs))),
//      ),
//      List(
//        branchA(List(branchA(leaf), branchB(leafs))),
//        branchB(List(branchA(leafs), branchB(leaf))),
//      ),
//    )
//    data.foreach { d =>
//      println(d)
//      conn.testDbWith(nested.getInsertStmts(d))
//      nested.get.map(_ ==> d)
//    }
//    ok
//    } yield ()
//    }
//
//"3 levels deep" - nested {   implicit conn =>
//      for {
//    val nested = m(
//      Ns0.i0.s0.R1.*(
//        Ns1.i1.s1.R2.*(
//          Ns2.i2.s2.R3.*(
//            Ns3.i3.s3))))
//
//    val data = List(
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leaf)
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leafs)
//          ))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leaf), branchB(leaf)
//          ))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leaf), branchB(leafs)
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leafs), branchB(leafs)
//          ))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leaf)
//          )),
//          branchA(List(
//            branchB(leaf)
//          )),
//        ))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leaf)
//          )),
//          branchA(List(
//            branchB(leafs)
//          )),
//        ))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leafs)
//          )),
//          branchA(List(
//            branchB(leafs)
//          )),
//        ))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leaf), branchB(leaf)
//          )),
//          branchA(List(
//            branchB(leaf), branchA(leaf)
//          )),
//        ))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leaf), branchB(leaf)
//          )),
//          branchA(List(
//            branchB(leaf), branchA(leafs)
//          )),
//        ))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leaf), branchB(leafs)
//          )),
//          branchA(List(
//            branchB(leaf), branchA(leafs)
//          )),
//        ))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leaf))))),
//        branchA(List(
//          branchB(List(
//            branchA(leaf)))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(leaf))))),
//        branchA(List(
//          branchB(List(
//            branchA(leaf))))),
//        branchA(List(
//          branchB(List(
//            branchA(leaf)))))),
//    )
//    data.foreach { d =>
//      println(d)
//      conn.testDbWith(nested.getInsertStmts(d))
//      nested.get.map(_ ==> d)
//    }
//    ok
//    } yield ()
//    }
//
//"4 levels deep" - nested {   implicit conn =>
//      for {
//    val nested = m(
//      Ns0.i0.s0.R1.*(
//        Ns1.i1.s1.R2.*(
//          Ns2.i2.s2.R3.*(
//            Ns3.i3.s3.R4.*(
//              Ns4.i4.s4)))))
//
//    val data = List(
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leafs)
//            ))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf), branchA(leaf)
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf), branchA(leafs)
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leafs), branchA(leafs)
//            ))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf))),
//            branchB(List(
//              branchA(leaf)))
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf))),
//            branchB(List(
//              branchA(leafs)))
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leafs))),
//            branchB(List(
//              branchA(leafs)))
//          ))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf), branchA(leaf))),
//            branchB(List(
//              branchA(leafs)))
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf), branchA(leafs))),
//            branchB(List(
//              branchA(leafs)))
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leafs), branchA(leafs))),
//            branchB(List(
//              branchA(leafs)))
//          ))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf))),
//            branchB(List(
//              branchA(leaf), branchB(leaf)))
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf))),
//            branchB(List(
//              branchA(leaf), branchB(leafs)))
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leafs))),
//            branchB(List(
//              branchA(leaf), branchB(leaf)))
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leafs))),
//            branchB(List(
//              branchA(leaf), branchB(leafs)))
//          ))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf), branchA(leaf))),
//            branchB(List(
//              branchA(leaf), branchB(leaf)))
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf), branchA(leafs))),
//            branchB(List(
//              branchA(leaf), branchB(leaf)))
//          ))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf), branchA(leafs))),
//            branchB(List(
//              branchA(leaf), branchB(leafs)))
//          ))))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            )))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            )))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            )))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            ))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            )))),
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            )))),
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            )))),
//          branchB(List(
//            branchA(List(
//              branchB(leaf)
//            ))))))),
//    )
//    data.foreach { d =>
//      println(d)
//      conn.testDbWith(nested.getInsertStmts(d))
//      nested.get.map(_ ==> d)
//    }
//    ok
//    } yield ()
//    }
//
//"5 levels deep" - nested {   implicit conn =>
//      for {
//    val nested = m(
//      Ns0.i0.s0.R1.*(
//        Ns1.i1.s1.R2.*(
//          Ns2.i2.s2.R3.*(
//            Ns3.i3.s3.R4.*(
//              Ns4.i4.s4.R5.*(
//                Ns5.i5.s5))))))
//
//    val data = List(
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leafs)
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf), branchA(leaf)
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf), branchA(leafs)
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leafs), branchA(leafs)
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf))),
//              branchA(List(
//                branchB(leaf)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf))),
//              branchA(List(
//                branchB(leafs)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leafs))),
//              branchA(List(
//                branchB(leafs)))
//            ))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf))),
//              branchA(List(
//                branchB(leaf), branchA(leaf)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf))),
//              branchA(List(
//                branchB(leaf), branchA(leafs)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf))),
//              branchA(List(
//                branchB(leafs), branchA(leafs)))
//            ))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leafs))),
//              branchA(List(
//                branchB(leaf), branchA(leaf)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leafs))),
//              branchA(List(
//                branchB(leaf), branchA(leafs)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leafs))),
//              branchA(List(
//                branchB(leafs), branchA(leafs)))
//            ))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf), branchB(leaf))),
//              branchA(List(
//                branchB(leaf)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf), branchB(leafs))),
//              branchA(List(
//                branchB(leaf)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leafs), branchB(leafs))),
//              branchA(List(
//                branchB(leaf)))
//            ))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf), branchB(leaf))),
//              branchA(List(
//                branchB(leafs)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf), branchB(leafs))),
//              branchA(List(
//                branchB(leafs)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leafs), branchB(leafs))),
//              branchA(List(
//                branchB(leafs)))
//            ))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf), branchB(leaf))),
//              branchA(List(
//                branchB(leaf), branchA(leaf)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf), branchB(leaf))),
//              branchA(List(
//                branchB(leaf), branchA(leafs)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf), branchB(leaf))),
//              branchA(List(
//                branchB(leafs), branchA(leafs)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf), branchB(leafs))),
//              branchA(List(
//                branchB(leafs), branchA(leafs)))
//            ))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leafs), branchB(leafs))),
//              branchA(List(
//                branchB(leafs), branchA(leafs)))
//            ))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              )))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              )))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              )))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              )))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              )))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              )))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              )))),
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              )))),
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              )))),
//            branchA(List(
//              branchB(List(
//                branchA(leaf)
//              ))))))))),
//    )
//
//    data.foreach { d =>
//      println(d)
//      conn.testDbWith(nested.getInsertStmts(d))
//      nested.get.map(_ ==> d)
//    }
//    ok
//    } yield ()
//    }
//
//"6 levels deep" - nested {   implicit conn =>
//      for {
//    val nested = m(
//      Ns0.i0.s0.R1.*(
//        Ns1.i1.s1.R2.*(
//          Ns2.i2.s2.R3.*(
//            Ns3.i3.s3.R4.*(
//              Ns4.i4.s4.R5.*(
//                Ns5.i5.s5.R6.*(
//                  Ns6.i6.s6)))))))
//
//    val data = List(
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf), branchB(leaf)
//                ))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf), branchB(leafs)
//                ))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs), branchB(leafs)
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)
//                )),
//                branchB(List(
//                  branchA(leaf)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)
//                )),
//                branchB(List(
//                  branchA(leafs)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)
//                )),
//                branchB(List(
//                  branchA(leafs)
//                ))
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)
//                )),
//                branchB(List(
//                  branchA(leaf), branchB(leaf)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)
//                )),
//                branchB(List(
//                  branchA(leaf), branchB(leafs)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)
//                )),
//                branchB(List(
//                  branchA(leafs), branchB(leafs)
//                ))
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)
//                )),
//                branchB(List(
//                  branchA(leaf), branchB(leaf)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)
//                )),
//                branchB(List(
//                  branchA(leaf), branchB(leafs)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)
//                )),
//                branchB(List(
//                  branchA(leafs), branchB(leafs)
//                ))
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf), branchA(leaf)
//                )),
//                branchB(List(
//                  branchA(leaf)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf), branchA(leafs)
//                )),
//                branchB(List(
//                  branchA(leaf)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs), branchA(leafs)
//                )),
//                branchB(List(
//                  branchA(leaf)
//                ))
//              ))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf), branchA(leaf)
//                )),
//                branchB(List(
//                  branchA(leafs)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf), branchA(leafs)
//                )),
//                branchB(List(
//                  branchA(leafs)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs), branchA(leafs)
//                )),
//                branchB(List(
//                  branchA(leafs)
//                ))
//              ))))))))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf), branchA(leaf)
//                )),
//                branchB(List(
//                  branchA(leaf), branchB(leaf)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf), branchA(leafs)
//                )),
//                branchB(List(
//                  branchA(leaf), branchB(leafs)
//                ))
//              ))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs), branchA(leafs))),
//                branchB(List(
//                  branchA(leafs), branchB(leafs)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs))))))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)))))))))))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))),
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))),
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))),
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))))),
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs))))))),
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))),
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))),
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))),
//              branchB(List(
//                branchA(List(
//                  branchB(leaf)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leaf))))),
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(leafs))))),
//              branchB(List(
//                branchA(List(
//                  branchB(leafs)))))))))))),
//    )
//
//    data.foreach { d =>
//      println(d)
//      conn.testDbWith(nested.getInsertStmts(d))
//      nested.get.map(_ ==> d)
//    }
//    ok
//    } yield ()
//    }
//
//"7 levels deep" - nested {   implicit conn =>
//      for {
//    val nested = m(
//      Ns0.i0.s0.R1.*(
//        Ns1.i1.s1.R2.*(
//          Ns2.i2.s2.R3.*(
//            Ns3.i3.s3.R4.*(
//              Ns4.i4.s4.R5.*(
//                Ns5.i5.s5.R6.*(
//                  Ns6.i6.s6.R7.*(
//                    Ns7.i7.s7))))))))
//
//    val data = List(
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf), branchB(leaf)
//                  ))))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf), branchB(leafs)
//                  ))))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs), branchB(leafs)
//                  ))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))),
//                  branchB(List(
//                    branchA(leaf)))
//                ))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))),
//                  branchB(List(
//                    branchA(leafs)))
//                ))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs))),
//                  branchB(List(
//                    branchA(leafs)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))),
//                  branchB(List(
//                    branchA(leaf), branchA(leaf)))
//                ))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))),
//                  branchB(List(
//                    branchA(leaf), branchA(leafs)))
//                ))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))),
//                  branchB(List(
//                    branchA(leafs), branchA(leafs)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs))),
//                  branchB(List(
//                    branchA(leaf), branchA(leaf)))
//                ))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs))),
//                  branchB(List(
//                    branchA(leaf), branchA(leafs)))
//                ))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs))),
//                  branchB(List(
//                    branchA(leafs), branchA(leafs)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf), branchB(leaf))),
//                  branchB(List(
//                    branchA(leaf)))
//                ))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf), branchB(leafs))),
//                  branchB(List(
//                    branchA(leaf)))
//                ))))))))))),
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs), branchB(leafs))),
//                  branchB(List(
//                    branchA(leaf)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf), branchB(leaf))),
//                  branchB(List(
//                    branchA(leafs)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf), branchB(leafs))),
//                  branchB(List(
//                    branchA(leafs)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs), branchB(leafs))),
//                  branchB(List(
//                    branchA(leafs)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf), branchB(leaf))),
//                  branchB(List(
//                    branchA(leaf), branchB(leaf)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf), branchB(leaf))),
//                  branchB(List(
//                    branchA(leaf), branchB(leafs)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf), branchB(leaf))),
//                  branchB(List(
//                    branchA(leafs), branchB(leafs)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf), branchB(leafs))),
//                  branchB(List(
//                    branchA(leafs), branchB(leafs)))
//                ))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs), branchB(leafs))),
//                  branchB(List(
//                    branchA(leafs), branchB(leafs)))
//                ))))))))))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf)))))))))))))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs))))))))))))),
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf)))))))))))))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs))))))))))),
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))),
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))),
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))),
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))))),
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs))))))))),
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))),
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))))),
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs))))))),
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))),
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leaf))))),
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//      List(
//        branchA(List(
//          branchB(List(
//            branchA(List(
//              branchB(List(
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs))))),
//                branchA(List(
//                  branchB(List(
//                    branchA(leafs)))))))))))))),
//
//    )
//
//    data.foreach { d =>
//      println(d)
//      conn.testDbWith(nested.getInsertStmts(d))
//      nested.get.map(_ ==> d)
//    }
//    ok
//  }
//  }
//}