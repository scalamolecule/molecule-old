package moleculeTests.tests.db.datomic.composite

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._

object CompositeSave extends AsyncTestSuite {

  lazy val tests = Tests {

    "Save" - core { implicit conn =>
      for {
        // 1 + 1
        _ <- (Ns.int(1) + Ref2.int2(11)).save
        _ <- (Ns.int(1) + Ref2.int2(11)).get.map(_.head ==> (1, 11))

        // n + 1
        _ <- (Ns.int(2).str("b") + Ref2.int2(22)).save
        _ <- (Ns.int(2).str("b") + Ref2.int2(22)).get.map(_.head ==> ((2, "b"), 22))

        // 1 + n
        _ <- (Ns.int(3) + Ref2.int2(33).str2("cc")).save
        _ <- (Ns.int(3) + Ref2.int2(33).str2("cc")).get.map(_.head ==> (3, (33, "cc")))

        // n + n
        _ <- (Ns.int(4).str("d") + Ref2.int2(44).str2("dd")).save
        _ <- (Ns.int(4).str("d") + Ref2.int2(44).str2("dd")).get.map(_.head ==> ((4, "d"), (44, "dd")))

        // All sub-molecules share the same entity id
        e5 <- (Ns.int(5).Ref1.int1(55) + Ref2.int2(555)).save.map(_.eid)
        _ <- Ns(e5).int.Ref1.int1.get.map(_.head ==> (5, 55))
        _ <- Ref2(e5).int2.get.map(_.head ==> 555)
        _ <- (Ns(e5).int.Ref1.int1 + Ref2.int2).get.map(_.head ==> ((5, 55), 555))

        // Sub-molecules can point straight to other entities (without any attributes of their own)
        e6 <- (Ns.Ref1.int1(6) + Ref2.int2(66)).save.map(_.eid)
        _ <- Ns(e6).Ref1.int1.get.map(_.head ==> 6)
        _ <- Ref2(e6).int2.get.map(_.head ==> 66)
        _ <- (Ns(e6).Ref1.int1 + Ref2.int2).get.map(_.head ==> (6, 66))
      } yield ()
    }
  }
}