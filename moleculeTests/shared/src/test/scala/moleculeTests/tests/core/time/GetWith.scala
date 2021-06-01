package moleculeTests.tests.core.time

import molecule.datomic.api.out2._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}

object GetWith extends AsyncTestSuite {

  def data(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      tx <- Ns.str("a").int(1).save
    } yield tx.eid
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "getSaveTx" - core { implicit conn =>
      for {
        _ <- data
        _ <- Ns.int.getWith(Ns.int(2).getSaveStmts).map(_ ==> List(1, 2))
        _ <- Ns.int.getWith(Ns.str("b").getSaveStmts).map(_ ==> List(1))
        _ <- Ns.int.getWith(Ns.str("b").int(2).getSaveStmts).map(_ ==> List(1, 2))
        _ <- Ns.str.getWith(Ns.str("b").int(2).getSaveStmts).map(_ ==> List("a", "b"))

        _ <- Ns.str$.int.getWith(
          Ns.int(2).getSaveStmts
        ).map(_.sortBy(_._2) ==> List((Some("a"), 1), (None, 2)))

        _ <- Ns.str.int.getWith(
          Ns.str("b").int(2).getSaveStmts
        ).map(_.sortBy(_._2) ==> List(("a", 1), ("b", 2)))

        // Current state unchanged
        _ <- Ns.str.int.get.map(_ ==> List(("a", 1)))
      } yield ()
    }


    "getInsertTx" - core { implicit conn =>
      for {
        - <- data
        _ <- Ns.int.getWith(Ns.int.getInsertStmts(2, 3)).map(_ ==> List(1, 2, 3))

        _ <- Ns.str.getWith(
          Ns.str$.int.getInsertStmts(Seq(
            (Some("b"), 2),
            (None, 3)
          ))
        ).map(_ ==> List("a", "b"))

        _ <- Ns.str$.int.getWith(
          Ns.int.getInsertStmts(2, 3)
        ).map(_.sortBy(_._2) ==> List(
          (Some("a"), 1),
          (None, 2),
          (None, 3)
        ))

        _ <- Ns.str$.int.getWith(
          Ns.str$.int.getInsertStmts(Seq(
            (Some("b"), 2),
            (None, 3)
          ))
        ).map(_.sortBy(_._2) ==> List(
          (Some("a"), 1),
          (Some("b"), 2),
          (None, 3)
        ))

        // Current state unchanged
        _ <- Ns.str.int.get.map(_ ==> List(("a", 1)))
      } yield ()
    }


    "getUpdateTx" - core { implicit conn =>
      for {
        eid <- data
        _ <- Ns.int.getWith(Ns(eid).int(2).getUpdateStmts).map(_ ==> List(2))
        _ <- Ns.int.getWith(Ns(eid).str("b").int(2).getUpdateStmts).map(_ ==> List(2))
        _ <- Ns.str.getWith(Ns(eid).str("b").int(2).getUpdateStmts).map(_ ==> List("b"))
        _ <- Ns.str.int.getWith(Ns(eid).int(2).getUpdateStmts).map(_ ==> List(("a", 2)))
        _ <- Ns.str.int.getWith(Ns(eid).str("b").int(2).getUpdateStmts).map(_ ==> List(("b", 2)))

        // Current state unchanged
        _ <- Ns.str.int.get.map(_ ==> List(("a", 1)))
      } yield ()
    }


    "getRetractTx" - core { implicit conn =>
      for {
        _ <- data
        tx <- Ns.str("b").int(2).save
        eid2 = tx.eid

        // Current state
        _ <- Ns.str.int.get.map(_.sortBy(_._2) ==> List(
          ("a", 1),
          ("b", 2)
        ))

        // Test retracting an entity id
        _ <- Ns.str.int.getWith(eid2.getRetractStmts).map(_ ==> List(
          ("a", 1)
        ))

        // Live state is unchanged
        _ <- Ns.str.int.get.map(_.sortBy(_._2) ==> List(
          ("a", 1),
          ("b", 2)
        ))
      } yield ()
    }


    "Combination example" - core { implicit conn =>
      for {
        _ <- data

        // Clean initial state
        _ <- Ns.e.str_.get.flatMap(retract(_))
        tx <- Ns.str("Fred").int(42).save
        fred = tx.eid

        // Current state
        _ <- Ns.str.int.get.map(_ ==> List(("Fred", 42)))

        _ <- Ns.str.int.getWith(
          Ns(fred).int(43).getUpdateStmts
        ).map(_ ==> List(("Fred", 43)))

        // Production value intact
        _ <- Ns.str.int.get.map(_ ==> List(("Fred", 42)))

        _ <- Ns.str.int.getWith(
          Ns.str("John").int(44).getSaveStmts
        ).map(_.sorted ==> List(
          ("Fred", 42), // production value
          ("John", 44) // insertion worked
        ))

        // Production value intact
        _ <- Ns.str.int.get.map(_ ==> List(("Fred", 42)))

        _ <- Ns.str.int.getWith(
          Ns.str("John").int(44).getSaveStmts,
          Ns.str.int getInsertStmts List(
            ("Lisa", 23),
            ("Pete", 24)
          ),
          Ns(fred).int(43).getUpdateStmts
        ).map(_.sorted ==> List(
          ("Fred", 43), // Updated
          ("John", 44), // Saved
          ("Lisa", 23), // Inserted
          ("Pete", 24) // Inserted
        ))

        saveJohn = Ns.str("John").int(44).getSaveStmts
        insertMembers = Ns.str.int getInsertStmts List(("Lisa", 23), ("Pete", 24))
        updateFred = Ns(fred).int(43).getUpdateStmts

        _ <- Ns.str.int.getWith(
          saveJohn,
          insertMembers,
          updateFred
        ).map(_.sorted ==> List(
          ("Fred", 43), // Updated
          ("John", 44), // Saved
          ("Lisa", 23), // Inserted
          ("Pete", 24) // Inserted
        ))
      } yield ()
    }
  }
}