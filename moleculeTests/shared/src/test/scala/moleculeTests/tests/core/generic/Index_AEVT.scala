package moleculeTests.tests.core.generic

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out5._
import molecule.datomic.base.util.SystemPeerServer
import utest._
import scala.concurrent.Future

/**
  * The AEVT index provides efficient access to all values for a given attribute,
  * comparable to traditional column access style.
  */
object Index_AEVT extends Base {

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Args" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5), _, _) <- testData

        // Attribute :Ns/int's entities, values and transactions
        _ <- AEVT(":Ns/int").e.v.t.get.map(_.sortBy(_._3) ==> List(
          (e1, 2, t3),
          (e2, 5, t5)
        ))

        // Entities having :Ns/int asserted
        _ <- AEVT(":Ns/int").e.get.map(_.sorted ==> List(e1, e2).sorted)

        // All values of attribute :Ns/int
        _ <- AEVT(":Ns/int").v.get.map(_.map(_.toString.toInt).sorted ==> List(2, 5))

        // All transactions where attribute :Ns/int is asserted
        _ <- AEVT(":Ns/int").t.get.map(_.sorted ==> List(t3, t5))


        // Attribute :Ns/int of entity e1's value and transaction
        _ <- AEVT(":Ns/int", e1).e.v.t.get.map(_ ==> List(
          (e1, 2, t3)
        ))

        // Attribute :Ns/int of entity e1 with value 2's transaction
        _ <- AEVT(":Ns/int", e1, 2).e.v.t.get.map(_ ==> List(
          (e1, 2, t3)
        ))

        // Attribute :Ns/int of entity e1 with value 2 in transaction t3
        _ <- AEVT(":Ns/int", e1, 2, t3).e.v.t.get.map(_ ==> List(
          (e1, 2, t3)
        ))

        // Attribute :Ns/int's historic entities, values and transactions
        _ <- if (system != SystemPeerServer)
          AEVT(":Ns/int").e.v.t.op.getHistory.map(_.sortBy(p => (p._3, p._4)) ==> List(
            (e1, 1, t1, true),
            (e1, 1, t3, false),
            (e1, 2, t3, true),
            (e2, 4, t4, true),
            (e2, 4, t5, false),
            (e2, 5, t5, true),
          ))
        else Future.unit
      } yield ()
    }

    "Only mandatory datom args" - core { implicit conn =>
      // Applying values to Index attributes not allowed
      expectCompileError("""m(AEVT(":Ns/int").a.e.v(42).t)""",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "AEVT index attributes not allowed to have values applied.\n" +
          "AEVT index only accepts datom arguments: `AEVT(<a/e/v/t>)`.")
    }
  }
}