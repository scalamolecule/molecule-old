package moleculeTests.tests.core.crud

import molecule.datomic.api.out4._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import moleculeTests.tests.core.base.dsl.CoreTest._
import scala.concurrent.ExecutionContext.Implicits.global


object UpdateMultipleAttributes extends AsyncTestSuite {

  lazy val tests = Tests {

    "Updating same/new values" - {

      "both new" - core { implicit conn =>
        for {
          tx <- Ns.int(1).str("a").save
          eid = tx.eid

          // Both int and str asserted
          _ <- Ns(eid).int(2).str("b").update

          _ <- Ns.int.get === List(2)
          _ <- Ns.str.get === List("b")
          _ <- Ns.e.int.str.get.map(_.head ==> (eid, 2, "b"))
        } yield ()
      }

      "first new, last same" - core { implicit conn =>
        for {
          tx <- Ns.int(1).str("a").save
          eid = tx.eid

          // Only int asserted
          _ <- Ns(eid).int(2).str("a").update

          _ <- Ns.int.get === List(2)
          _ <- Ns.str.get === List("a")
          _ <- Ns.e.int.str.get.map(_.head ==> (eid, 2, "a"))
        } yield ()
      }

      "first same, last new" - core { implicit conn =>
        for {
          tx <- Ns.int(1).str("a").save
          eid = tx.eid

          // Only str asserted
          _ <- Ns(eid).int(1).str("b").update

          _ <- Ns.int.get === List(1)
          _ <- Ns.str.get === List("b")
          _ <- Ns.e.int.str.get.map(_.head ==> (eid, 1, "b"))
        } yield ()
      }

      "both same" - core { implicit conn =>
        for {
          tx <- Ns.int(1).str("a").save
          eid = tx.eid

          // No facts asserted!
          _ <- Ns(eid).int(1).str("a").update

          _ <- Ns.int.get === List(1)
          _ <- Ns.str.get === List("a")
          _ <- Ns.e.int.str.get.map(_.head ==> (eid, 1, "a"))
        } yield ()
      }

      "2 new, 1 same" - core { implicit conn =>
        for {
          tx <- Ns.int(1).str("a").bool(true).save
          eid = tx.eid

          _ <- Ns(eid).int(2).str("b").bool(true).update

          _ <- Ns.int.get === List(2)
          _ <- Ns.str.get === List("b")
          _ <- Ns.bool.get === List(true)
          _ <- Ns.e.int.str.bool.get.map(_.head ==> (eid, 2, "b", true))
        } yield ()
      }

      "2 same, 1 new" - core { implicit conn =>
        for {
          tx <- Ns.int(1).str("a").bool(true).save
          eid = tx.eid

          _ <- Ns(eid).int(1).str("a").bool(false).update

          _ <- Ns.int.get === List(1)
          _ <- Ns.str.get === List("a")
          _ <- Ns.bool.get === List(false)
          _ <- Ns.e.int.str.bool.get.map(_.head ==> (eid, 1, "a", false))
        } yield ()
      }
    }

    "Optional values - update or retract" - core { implicit conn =>
      for {
        tx <- Ns.int(1).str("a").save
        eid = tx.eid

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