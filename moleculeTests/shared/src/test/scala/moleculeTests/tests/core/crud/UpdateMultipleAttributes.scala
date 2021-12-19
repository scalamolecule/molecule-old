package moleculeTests.tests.core.crud

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._


object UpdateMultipleAttributes extends AsyncTestSuite {

  lazy val tests = Tests {

    "Updating same/new values" - {

      "both new" - core { implicit conn =>
        for {
          eid <- Ns.int(1).str("a").save.map(_.eid)

          // Both int and str asserted
          _ <- Ns(eid).int(2).str("b").update

          _ <- Ns.int.get.map(_ ==> List(2))
          _ <- Ns.str.get.map(_ ==> List("b"))
          _ <- Ns.e.int.str.get.map(_.head ==> (eid, 2, "b"))
        } yield ()
      }

      "first new, last same" - core { implicit conn =>
        for {
          eid <- Ns.int(1).str("a").save.map(_.eid)

          // Only int asserted
          _ <- Ns(eid).int(2).str("a").update

          _ <- Ns.int.get.map(_ ==> List(2))
          _ <- Ns.str.get.map(_ ==> List("a"))
          _ <- Ns.e.int.str.get.map(_.head ==> (eid, 2, "a"))
        } yield ()
      }

      "first same, last new" - core { implicit conn =>
        for {
          eid <- Ns.int(1).str("a").save.map(_.eid)

          // Only str asserted
          _ <- Ns(eid).int(1).str("b").update

          _ <- Ns.int.get.map(_ ==> List(1))
          _ <- Ns.str.get.map(_ ==> List("b"))
          _ <- Ns.e.int.str.get.map(_.head ==> (eid, 1, "b"))
        } yield ()
      }

      "both same" - core { implicit conn =>
        for {
          eid <- Ns.int(1).str("a").save.map(_.eid)

          // No facts asserted!
          _ <- Ns(eid).int(1).str("a").update

          _ <- Ns.int.get.map(_ ==> List(1))
          _ <- Ns.str.get.map(_ ==> List("a"))
          _ <- Ns.e.int.str.get.map(_.head ==> (eid, 1, "a"))
        } yield ()
      }

      "2 new, 1 same" - core { implicit conn =>
        for {
          eid <- Ns.int(1).str("a").bool(true).save.map(_.eid)

          _ <- Ns(eid).int(2).str("b").bool(true).update

          _ <- Ns.int.get.map(_ ==> List(2))
          _ <- Ns.str.get.map(_ ==> List("b"))
          _ <- Ns.bool.get.map(_ ==> List(true))
          _ <- Ns.e.int.str.bool.get.map(_.head ==> (eid, 2, "b", true))
        } yield ()
      }

      "2 same, 1 new" - core { implicit conn =>
        for {
          eid <- Ns.int(1).str("a").bool(true).save.map(_.eid)

          _ <- Ns(eid).int(1).str("a").bool(false).update

          _ <- Ns.int.get.map(_ ==> List(1))
          _ <- Ns.str.get.map(_ ==> List("a"))
          _ <- Ns.bool.get.map(_ ==> List(false))
          _ <- Ns.e.int.str.bool.get.map(_.head ==> (eid, 1, "a", false))
        } yield ()
      }
    }

    "Optional values - update or retract" - core { implicit conn =>
      for {
        eid <- Ns.int(1).str("a").save.map(_.eid)

        // Both values updated
        _ <- Ns(eid).int(2).str$(Some("b")).update
        _ <- Ns.int.str$.get.map(_.head ==> (2, Some("b")))

        // str retracted
        _ <- Ns(eid).int(3).str$(None).update
        _ <- Ns.int.str$.get.map(_.head ==> (3, None))

        // Reversing positions

        _ <- Ns(eid).str$(Some("d")).int(4).update
        _ <- Ns.str$.int.get.map(_.head ==> (Some("d"), 4))

        _ <- Ns(eid).str$(None).int(5).update
        _ <- Ns.str$.int.get.map(_.head ==> (None, 5))
      } yield ()
    }
  }
}