package moleculeTests.tests.core.generic

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.out5._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Index extends AsyncTestSuite {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      // Generally use `t` or `tx` to identify transaction and `txInstant` only to get
      // the wall clock time since Date's are a bit unreliable for precision.

      // First entity

      txR1 <- Ns.str("a").int(1).save
      tx1 = txR1.tx
      e1 = txR1.eid
      t1 = txR1.t
      d1 = txR1.inst

      txR2 <- Ns(e1).str("b").update
      tx2 = txR2.tx
      t2 = txR2.t
      d2 = txR2.inst

      txR3 <- Ns(e1).int(2).update
      tx3 = txR3.tx
      t3 = txR3.t
      d3 = txR3.inst


      // Second entity

      txR4 <- Ns.str("x").int(4).save
      tx4 = txR4.tx
      e2 = txR4.eid
      t4 = txR4.t
      d4 = txR4.inst

      txR5 <- Ns(e2).int(5).update
      tx5 = txR5.tx
      t5 = txR5.t
      d5 = txR5.inst


      // Relationship

      txR6 <- Ref1.str1("hello").save
      tx6 = txR6.tx
      t6 = txR6.t
      d6 = txR6.inst
      e3 = txR6.eid

      // e2 points to e3
      txR7 <- Ns(e2).ref1(e3).update
      tx7 = txR7.tx
      t7 = txR7.t
      d7 = txR7.inst


      // Cardinality-many attributes

      // 6, 7, 8
      txR8 <- Ns.ints(6, 7, 8).save
      t8 = txR8.t
      e4 = txR8.eid

      // 6, 70, 80
      txR9 <- Ns(e4).ints.replace(7 -> 70, 8 -> 80).update
      t9 = txR9.t

      // 70, 80
      txR10 <- Ns(e4).ints.retract(6).update
      t10 = txR10.t

      // 70, 80, 90
      txR11 <- Ns(e4).ints.assert(60).update
      t11 = txR11.t

      // e2 now points to e4
      txR12 <- Ns(e2).ref1(e4).update
      t12 = txR12.t

      // e1 also points to e4
      txR13 <- Ns(e2).refs1(e4).update
      t13 = txR13.t

      // Inline descriptions respectfully borrowed from the manual:
      // https://docs.datomic.com/on-prem/indexes.html
    } yield {
      (
        (tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5),
        (tx6, t6, d6, e3, tx7, t7, d7),
        (t8, e4, t9, t10, t11, t12, t13)
      )
    }
  }


  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "All Datoms of Index" - core { implicit conn =>
      for {
        _ <- testData

        _ <- system match {
          case SystemPeer =>
            for {
              _ <- EAVT.a.get.map(_.size ==> 679)
              _ <- AEVT.a.get.map(_.size ==> 679)
              _ <- VAET.a.get.map(_.size ==> 331)
              r <- AVET.a.get.map(_.size ==> 214)
            } yield r


          case SystemDevLocal =>
            for {
              _ <- EAVT.a.get.map(_.size ==> 569)
              _ <- AEVT.a.get.map(_.size ==> 569)
              _ <- VAET.a.get.map(_.size ==> 317)
              r <- AVET.a.get.map(_.size ==> 569)
            } yield r

          case _ => Future.unit // Peer Server (growing across tests, so we can't test deterministically here)
        }
      } yield ()
    }


    "EAVT" - {

      // The EAVT index provides efficient access to everything about a given entity.
      // Conceptually this is very similar to row access style in a SQL database, except
      // that entities can possess arbitrary attributes rather then being limited to
      // a predefined set of columns in a table.

      // Note that Datomic sorts the attribute id, not the name/ident,
      // so we don't get alphabetic order by attribute name. Attributes are though
      // still grouped together.


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
          (_, _, _, (t8, e4, t9, t10, t11, t12, t13)) <- testData

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
        compileError("m(EAVT(42L).e.a.v(500).t)").check("",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "EAVT index attributes not allowed to have values applied.\n" +
            "EAVT index only accepts datom arguments: `EAVT(<e/a/v/t>)`.")
      }
    }

    "AEVT" - {

      "Args" - core { implicit conn =>
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
          (tx4, e2, t4, d4, tx5, t5, d5), _, _) <- testData

          // The AEVT index provides efficient access to all values for a given attribute,
          // comparable to traditional column access style.

          // Attribute :Ns/int's entities, values and transactions
          _ <- AEVT(":Ns/int").e.v.t.get.map(_.sortBy(_._3) ==> List(
            (e1, 2, t3),
            (e2, 5, t5)
          ))

          // Entities having :Ns/int asserted
          _ <- AEVT(":Ns/int").e.get.map(_.sorted ==> List(e1, e2).sorted)

          // All values of attribute :Ns/int
          _ <- AEVT(":Ns/int").v.get.map(_.map(_.asInstanceOf[Long]).sorted ==> List(2, 5))

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
        compileError("""m(AEVT(":Ns/int").a.e.v(42).t)""").check("",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "AEVT index attributes not allowed to have values applied.\n" +
            "AEVT index only accepts datom arguments: `AEVT(<a/e/v/t>)`.")
      }
    }


    "AVET" - {

      "Basics" - core { implicit conn =>
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
          (tx4, e2, t4, d4, tx5, t5, d5), _, _) <- testData

          // The AVET index provides efficient access to particular combinations of attribute and value.

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
        compileError("""m(AVET(":Ns/int").a.v.e(77L).t)""").check("",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "AVET index attributes not allowed to have values applied.\n" +
            "AVET index only accepts datom arguments: `AVET(<a/v/e/t>)` or range arguments: `AVET.range(a, from, until)`.")
      }
    }


    "AVET Index range" - {

      "Basics" - core { implicit conn =>
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

      "Arg combinations" - core { implicit conn =>
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

      "Arg types" - core { implicit conn =>
        for {
          _ <- testData

          // Different range types throw an exception
          _ <- AVET.range(":Ns/int", Some(1), Some("y")).e.get.recover { case MoleculeException(err, _) =>
            err ==> "Please supply range arguments of same type as attribute."
          }

          // Two wrong types simply returns no result
          _ <- AVET.range(":Ns/int", Some("x"), Some("y")).e.get.map(_ ==> Nil)
        } yield ()
      }

      "Arg variables" - core { implicit conn =>
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

      "History" - core { implicit conn =>
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


    "VAET" - {

      "Args" - core { implicit conn =>
        for {
          (_, (tx4, e2, t4, d4, tx5, t5, d5),
          (tx6, t6, d6, e3, tx7, t7, d7),
          (t8, e4, t9, t10, t11, t12, t13)) <- testData

          // e2 no longer points to e3
          _ <- VAET(e3).a.e.t.get.map(_ ==> Nil)

          // e1 and e2 points to e4
          _ <- VAET(e4).a.e.t.get.map(_ ==> List(
            (":Ns/ref1", e2, t12),
            (":Ns/refs1", e2, t13)
          ))

          // e2 pointed to e3
          _ <- VAET(e3).a.e.t.op.getHistory.map(_ ==> List(
            (":Ns/ref1", e2, t12, false),
            (":Ns/ref1", e2, t7, true)
          ))

          // e1 and e2 now points to e4
          _ <- VAET(e4).a.e.t.op.getHistory.map(_ ==> List(
            (":Ns/ref1", e2, t12, true),
            (":Ns/refs1", e2, t13, true)
          ))
        } yield ()
      }

      "Only mandatory datom args" - core { implicit conn =>
        // Applying values to Index attributes not allowed
        compileError("m(VAET(42L).v.a.e(77L).t)").check("",
          "molecule.core.transform.exception.Dsl2ModelException: " +
            "VAET index attributes not allowed to have values applied.\n" +
            "VAET index only accepts datom arguments: `VAET(<v/a/e/t>)`.")
      }
    }
  }
}