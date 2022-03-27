package moleculeTests.tests.db.datomic.time

import molecule.core.ast.elements.Card
import molecule.datomic.api.out3._
import molecule.datomic.base.ast.transactionModel.{Add, RetractEntity}
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._

object GetWith extends AsyncTestSuite {

  lazy val tests = Tests {

    "getSaveStmts" - core { implicit conn =>
      for {
        _ <- Ns.str("a").int(1).save.map(_.eid)
        _ <- Ns.int.getWith(Ns.int(2).getSaveStmts).map(_ ==> List(1, 2))
        _ <- Ns.int.getWith(Ns.str("b").getSaveStmts).map(_ ==> List(1))
        _ <- Ns.int.getWith(Ns.str("b").int(2).getSaveStmts).map(_ ==> List(1, 2))
        _ <- Ns.str.getWith(Ns.str("b").int(2).getSaveStmts).map(_ ==> List("a", "b"))

        _ <- Ns.str$.int.a1.getWith(
          Ns.int(2).getSaveStmts
        ).map(_ ==> List((Some("a"), 1), (None, 2)))

        _ <- Ns.str.int.a1.getWith(
          Ns.str("b").int(2).getSaveStmts
        ).map(_ ==> List(("a", 1), ("b", 2)))

        // Current state unchanged
        _ <- Ns.str.int.get.map(_ ==> List(("a", 1)))
      } yield ()
    }


    "getInsertStmts" - core { implicit conn =>
      for {
        _ <- Ns.str("a").int(1).save.map(_.eid)
        _ <- Ns.int.getWith(Ns.int.getInsertStmts(2, 3)).map(_ ==> List(1, 2, 3))

        _ <- Ns.str.getWith(
          Ns.str$.int.getInsertStmts(Seq(
            (Some("b"), 2),
            (None, 3)
          ))
        ).map(_ ==> List("a", "b"))

        _ <- Ns.str$.int.a1.getWith(
          Ns.int.getInsertStmts(2, 3)
        ).map(_ ==> List(
          (Some("a"), 1),
          (None, 2),
          (None, 3)
        ))

        _ <- Ns.str$.int.a1.getWith(
          Ns.str$.int.getInsertStmts(Seq(
            (Some("b"), 2),
            (None, 3)
          ))
        ).map(_ ==> List(
          (Some("a"), 1),
          (Some("b"), 2),
          (None, 3)
        ))

        // Current state unchanged
        _ <- Ns.str.int.get.map(_ ==> List(("a", 1)))
      } yield ()
    }


    "getUpdateStmts" - core { implicit conn =>
      for {
        eid <- Ns.str("a").int(1).save.map(_.eid)
        _ <- Ns.int.getWith(Ns(eid).int(2).getUpdateStmts).map(_ ==> List(2))
        _ <- Ns.int.getWith(Ns(eid).str("b").int(2).getUpdateStmts).map(_ ==> List(2))
        _ <- Ns.str.getWith(Ns(eid).str("b").int(2).getUpdateStmts).map(_ ==> List("b"))
        _ <- Ns.str.int.getWith(Ns(eid).int(2).getUpdateStmts).map(_ ==> List(("a", 2)))
        _ <- Ns.str.int.getWith(Ns(eid).str("b").int(2).getUpdateStmts).map(_ ==> List(("b", 2)))

        // Current state unchanged
        _ <- Ns.str.int.get.map(_ ==> List(("a", 1)))
      } yield ()
    }


    "getRetractStmts" - core { implicit conn =>
      for {
        _ <- Ns.str("a").int(1).save
        eid2 <- Ns.str("b").int(2).save.map(_.eid)

        // Current state
        _ <- Ns.str.int.a1.get.map(_ ==> List(
          ("a", 1),
          ("b", 2)
        ))

        // Test retracting an entity id
        _ <- Ns.str.int.getWith(eid2.getRetractStmts).map(_ ==> List(
          ("a", 1)
        ))

        // Live state is unchanged
        _ <- Ns.str.int.a1.get.map(_ ==> List(
          ("a", 1),
          ("b", 2)
        ))
      } yield ()
    }


    "getRetractStmts with tx meta data" - core { implicit conn =>
      for {
        eid <- Ns.int(1).save.map(_.eid)

        // without tx meta data
        _ <- eid.getRetractStmts.map(_ ==> List(
          RetractEntity(eid)
        ))

        // with tx meta data
        _ <- eid.getRetractStmts(Ref2.str2("meta2") + Ref1.str1("meta1")).map(_ ==> List(
          RetractEntity(eid),
          Add("datomic.tx", ":Ref2/str2", "meta2", Card(1)),
          Add("datomic.tx", ":Ref1/str1", "meta1", Card(1))
        ))
      } yield ()
    }


    "Combination example" - core { implicit conn =>
      for {
        _ <- Ns.str("a").int(1).save.map(_.eid)

        // Clean initial live state
        _ <- Ns.e.str_.get.flatMap(retract(_))
        fred <- Ns.str("Fred").int(42).save.map(_.eid)

        // Current state
        _ <- Ns.str.int.get.map(_ ==> List(("Fred", 42)))

        _ <- Ns.str.int.getWith(
          Ns(fred).int(43).getUpdateStmts
        ).map(_ ==> List(("Fred", 43)))

        // Live value intact
        _ <- Ns.str.int.get.map(_ ==> List(("Fred", 42)))

        _ <- Ns.str.a1.int.getWith(
          Ns.str("John").int(44).getSaveStmts
        ).map(_ ==> List(
          ("Fred", 42), // live value
          ("John", 44) // insertion worked
        ))

        // Live value intact
        _ <- Ns.str.int.get.map(_ ==> List(("Fred", 42)))

        _ <- Ns.str.a1.int.getWith(
          Ns.str("John").int(44).getSaveStmts,
          Ns.str.int getInsertStmts List(
            ("Lisa", 23),
            ("Pete", 24)
          ),
          Ns(fred).int(43).getUpdateStmts
        ).map(_ ==> List(
          ("Fred", 43), // Updated
          ("John", 44), // Saved
          ("Lisa", 23), // Inserted
          ("Pete", 24) // Inserted
        ))

        saveJohn = Ns.str("John").int(44).getSaveStmts
        insertMembers = Ns.str.int getInsertStmts List(("Lisa", 23), ("Pete", 24))
        updateFred = Ns(fred).int(43).getUpdateStmts

        _ <- Ns.str.a1.int.getWith(
          saveJohn,
          insertMembers,
          updateFred
        ).map(_ ==> List(
          ("Fred", 43), // Updated
          ("John", 44), // Saved
          ("Lisa", 23), // Inserted
          ("Pete", 24) // Inserted
        ))
      } yield ()
    }
  }
}