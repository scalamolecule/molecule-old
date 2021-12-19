package moleculeTests.tests.db.datomic.generic

import molecule.core.exceptions.MoleculeException
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out5._
import molecule.datomic.base.util.SystemPeerServer
import utest._
import scala.concurrent.Future

/**
 * The AVET index provides efficient access to particular combinations of attribute and value.
 */
object Index_AVET extends Base {

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Basics" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5), _, _) <- testData

        // Which entities in what transactions have attribute :Ns/int asserted with value 2?
        _ <- AVET(":Ns/int", 2).e.t.get.map(_ ==> List(
          (e1, t3)
        ))

        _ <- AVET(":Ns/int", 2, e1).t.get.map(_ ==> List(t3))

        _ <- AVET(":Ns/int", 2, e1, t3).op.get.map(_ ==> List(true))

        // History of entities with attribute :Ns/int having value 4
        _ <- if (system != SystemPeerServer) {
          for {
            _ <- AVET(":Ns/int", 4).e.t.op.getHistory.map(_ ==> List(
              (e2, t5, false),
              (e2, t4, true)
            ))

            r <- AEVT(":Ns/int").v.e.t.op.getHistory.map(_.sortBy(p => (p._3, p._4)) ==> List(
              (1, e1, t1, true),
              (1, e1, t3, false),
              (2, e1, t3, true),
              (4, e2, t4, true),
              (4, e2, t5, false),
              (5, e2, t5, true),
            ))
          } yield ()
        } else Future.unit
      } yield ()
    }


    "Only mandatory datom args" - core { implicit conn =>
      // Applying values to Index attributes not allowed
      expectCompileError("""m(AVET(":Ns/int").a.v.e(77L).t)""",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "AVET index attributes not allowed to have values applied.\n" +
          "AVET index only accepts datom arguments: `AVET(<a/v/e/t>)` or range arguments: `AVET.range(a, from, until)`.")
    }


    // Index range ...................................................................................

    "Index range, Basics" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5), _, _) <- testData

        // Apply attribute name and `from` + `until` value range arguments

        // Datoms with attribute :Ns/int having a value between 2 until 6 (not included)
        _ <- AVET.range(":Ns/int", Some(2), Some(6)).v.e.t.get.map(_ ==> List(
          (2, e1, t3),
          (5, e2, t5)
        ))
      } yield ()
    }


    "Index range, Arg combinations" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5), _, _) <- testData

        // `until` arg 5 is not included
        _ <- AVET.range(":Ns/int", Some(2), Some(5)).e.get.map(_ ==> List(e1))

        // Both 2 and 5 matched
        _ <- AVET.range(":Ns/int", Some(2), Some(6)).e.get.map(_ ==> List(e1, e2))

        // Only 5 matched
        _ <- AVET.range(":Ns/int", Some(3), Some(6)).e.get.map(_ ==> List(e2))


        // 2 to end (2 included)
        _ <- AVET.range(":Ns/int", Some(2), None).e.get.map(_ ==> List(e1, e2))

        // 3 to end (2 not included)
        _ <- AVET.range(":Ns/int", Some(3), None).e.get.map(_ ==> List(e2))

        // 6 to end (2 and 5 not included)
        _ <- AVET.range(":Ns/int", Some(6), None).e.get.map(_ ==> Nil)


        // Start until 5 (5 not included)
        _ <- AVET.range(":Ns/int", None, Some(5)).e.get.map(_ ==> List(e1))

        // Start until 6 (5 included)
        _ <- AVET.range(":Ns/int", None, Some(6)).e.get.map(_ ==> List(e1, e2))

        // Start until 2 (2 and 5 not included)
        _ <- AVET.range(":Ns/int", None, Some(2)).e.get.map(_ ==> Nil)


        // Start - end
        // Molecule disallow returning from beginning to end (the whole database!)
        _ <- AVET.range(":Ns/int", None, None).e.get.map(_ ==> List(e1, e2))
      } yield ()
    }


    "Index range, Arg types" - core { implicit conn =>
      for {
        _ <- testData

        // Different range types throw an exception
        _ <- AVET.range(":Ns/int", Some(1), Some("y")).e.get
          .map(_ ==> "Unexpected success").recover { case MoleculeException(err, _) =>
          err ==> "Please supply range arguments of same type as attribute."
        }

        // Two wrong types simply return an empty result
        _ <- AVET.range(":Ns/int", Some("x"), Some("y")).e.get.map(_ ==> Nil)
      } yield ()
    }


    "Index range, Arg variables" - core { implicit conn =>
      // Args can be supplied as variables

      val attr = ":Ns/int"
      val one  = 1
      val six  = 6

      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5), _, _) <- testData

        // All variables
        _ <- AVET.range(attr, Some(one), Some(six)).e.get.map(_ ==> List(e1, e2))

        // Mixing static values and variables ok
        _ <- AVET.range(":Ns/int", Some(one), Some(6)).e.get.map(_ ==> List(e1, e2))

        // Optionals can be supplied as variables too
        from1 = Some(1)
        until6 = Some(6)
        end = None
        _ <- AVET.range(":Ns/int", from1, end).e.get.map(_ ==> List(e1, e2))
        _ <- AVET.range(":Ns/int", from1, until6).e.get.map(_ ==> List(e1, e2))
      } yield ()
    }


    "Index range, History" - core { implicit conn =>
      if (system != SystemPeerServer) {
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
          (tx4, e2, t4, d4, tx5, t5, d5), _, _) <- testData

          // Attribute :Ns/int values from 1 to end
          _ <- AVET.range(":Ns/int", Some(1), None).v.e.t.op.getHistory.map(_ ==> List(
            (1, e1, t3, false),
            (1, e1, t1, true),
            (2, e1, t3, true),
            (4, e2, t5, false),
            (4, e2, t4, true),
            (5, e2, t5, true)
          ))

          // Attribute :Ns/int values from 1 until 6
          _ <- AVET.range(":Ns/int", Some(1), Some(6)).v.e.t.op.getHistory.map(_ ==> List(
            (1, e1, t3, false),
            (1, e1, t1, true),
            (2, e1, t3, true),
            (4, e2, t5, false),
            (4, e2, t4, true),
            (5, e2, t5, true)
          ))

          // Attribute :Ns/int values from 1 until 5 (5 not included)
          _ <- AVET.range(":Ns/int", Some(1), Some(5)).v.e.t.op.getHistory.map(_ ==> List(
            (1, e1, t3, false),
            (1, e1, t1, true),
            (2, e1, t3, true),
            (4, e2, t5, false),
            (4, e2, t4, true)
          ))

          // Attribute :Ns/int values from 2 until 5 (1 and 5 not included)
          _ <- AVET.range(":Ns/int", Some(2), Some(5)).v.e.t.op.getHistory.map(_ ==> List(
            (2, e1, t3, true),
            (4, e2, t5, false),
            (4, e2, t4, true)
          ))

          // Attribute :Ns/int values from 3 until 5 (1, 2 and 5 not included)
          _ <- AVET.range(":Ns/int", Some(3), Some(5)).v.e.t.op.getHistory.map(_ ==> List(
            (4, e2, t5, false),
            (4, e2, t4, true)
          ))
        } yield ()
      }
    }
  }
}