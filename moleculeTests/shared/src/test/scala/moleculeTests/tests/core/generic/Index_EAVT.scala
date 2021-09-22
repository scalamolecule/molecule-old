package moleculeTests.tests.core.generic

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out5._
import molecule.datomic.base.util.SystemPeerServer
import utest._

/**
  * The EAVT index provides efficient access to everything about a given entity.
  * Conceptually this is very similar to row access style in a SQL database, except
  * that entities can possess arbitrary attributes rather then being limited to
  * a predefined set of columns in a table.
  *
  * Note that Datomic sorts the attribute id, not the name/ident,
  * so we don't get alphabetic order by attribute name. Attributes are though
  * still grouped together.
  */
object Index_EAVT extends Base {

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Current values" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3), _, _, _) <- testData

        // EAVT datom values of entity e1
        _ <- EAVT(e1).e.a.v.t.get.map(_.sortBy(_._4) ==> List(
          (e1, ":Ns/str", "b", t2),
          (e1, ":Ns/int", 2, t3),
        ))

        // Freely order generic attributes as you like
        _ <- EAVT(e1).t.v.e.a.get.map(_.sortBy(_._1) ==> List(
          (t2, "b", e1, ":Ns/str"),
          (t3, 2, e1, ":Ns/int")
        ))

        // No need to return the entity id that we filter by
        _ <- EAVT(e1).a.v.t.get.map(_.sortBy(_._3) ==> List(
          (":Ns/str", "b", t2),
          (":Ns/int", 2, t3)
        ))

        // Attributes of entity e1
        _ <- EAVT(e1).a.get.map(_.sorted ==> List(":Ns/int", ":Ns/str"))

        // Values of e1
        _ <- EAVT(e1).v.get.map(_ ==> List("b", 2))

        // Transaction Ts of e1
        _ <- EAVT(e1).t.get.map(_ ==> List(t2, t3))

        // Transaction entities of e1
        _ <- EAVT(e1).tx.get.map(_ ==> List(tx2, tx3))

        // Transaction times of e1
        _ <- EAVT(e1).txInstant.get.map(_ ==> List(d2, d3))

        // Operations of e1
        // Since the current database as of now will only have asserted values
        // this questions will always yield true and therefore not be interesting to query
        _ <- EAVT(e1).op.get.map(_ ==> List(true, true))
      } yield ()
    }


    "History values" - core { implicit conn =>
      if (system != SystemPeerServer) {
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3), _, _, _) <- testData

          // History of attribute values of entity e1
          // Generic attribute `op` is interesting when looking at the history database since
          // it tells wether datoms were asserted (true) or retracted (false).
          // NOTE that retracted datoms take precedence for the same EAV values, meaning that
          // the transaction value is sorted after the operation (so he index seems actually sorted by
          // all 5 datom elements EAVOpT and not only EAVT)
          _ <- EAVT(e1).e.a.v.t.op.getHistory.map(_.sortBy(p => (p._2, p._4, p._5)) ==> List(
            (e1, ":Ns/int", 1, t1, true),
            (e1, ":Ns/int", 1, t3, false),
            (e1, ":Ns/int", 2, t3, true),
            (e1, ":Ns/str", "a", t1, true),
            (e1, ":Ns/str", "a", t2, false),
            (e1, ":Ns/str", "b", t2, true),
          ))

          // History of attribute :Ns/int values of entity e1
          // or
          // "What values has attribute :Ns/int of entity e1 had over time?"
          // - 1 was asserted in transaction t1, retracted in t3, and new value 2 asserted in t3
          _ <- EAVT(e1, ":Ns/int").e.a.v.t.op.getHistory.map(_.sortBy(p => (p._4, p._5)) ==> List(
            (e1, ":Ns/int", 1, t1, true),
            (e1, ":Ns/int", 1, t3, false),
            (e1, ":Ns/int", 2, t3, true),
          ))

          // History of attribute :Ns/int value being 1 of entity e1
          // or
          // "What happened to entity e1's attribute :Ns/int value 1?"
          // - 1 was asserted in transaction t1 and then retracted in t3
          _ <- EAVT(e1, ":Ns/int", 1).e.a.v.t.op.getHistory.map(_.sortBy(p => (p._4, p._5)) ==> List(
            (e1, ":Ns/int", 1, t1, true),
            (e1, ":Ns/int", 1, t3, false),
          ))

          // History of attribute :Ns/int value being 1 of entity e1 in transaction t3
          // or
          // "Was entity e1's attribute :Ns/int value 1 in transaction t1 asserted or retracted?"
          // - 1 was asserted in transaction t1
          _ <- EAVT(e1, ":Ns/int", 1, t1).e.a.v.t.op.getHistory.map(_ ==> List(
            (e1, ":Ns/int", 1, t1, true)
          ))
        } yield ()
      }
    }


    "Card many" - core { implicit conn =>
      for {
        (_, _, _, (t8, e4, t9, t10, t11, tx12, t12, d12, tx13, t13, d13)) <- testData

        // Each value is asserted/retracted on its own
        _ <- EAVT(e4).a.v.t.get.map(_ ==> List(
          (":Ns/ints", 60, t11),
          (":Ns/ints", 70, t9),
          (":Ns/ints", 80, t9)
        ))

        _ <- EAVT(e4).a.v.t.op.getHistory.map(_ ==> List(
          (":Ns/ints", 6, t10, false),
          (":Ns/ints", 6, t8, true),

          (":Ns/ints", 7, t9, false),
          (":Ns/ints", 7, t8, true),

          (":Ns/ints", 8, t9, false),
          (":Ns/ints", 8, t8, true),

          (":Ns/ints", 60, t11, true),
          (":Ns/ints", 70, t9, true),
          (":Ns/ints", 80, t9, true),
        ))
      } yield ()
    }


    "Datom args" - core { implicit conn =>
      // Applying values to Index attributes not allowed
      expectCompileError("m(EAVT(42L).e.a.v(500).t)",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "EAVT index attributes not allowed to have values applied.\n" +
          "EAVT index only accepts datom arguments: `EAVT(<e/a/v/t>)`.")
    }
  }
}