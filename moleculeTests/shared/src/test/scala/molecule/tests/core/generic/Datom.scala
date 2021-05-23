package molecule.tests.core.generic

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out6._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{SystemDevLocal, SystemPeerServer}
import molecule.setup.AsyncTestSuite
import utest._
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.{ExecutionContext, Future}

/** Generic Datom attribute interface
  *
  * - `e` Entity id (Long)
  * - `a` Full attribute name like ":Person/name" (String)
  * - `v` Value of Datoms (Any)
  * - `t` Transaction pointer (Long)
  * - `tx` Transaction entity id
  * - `txInstant` Transaction wall clock time
  * - `op` Assertion (true) / retraction (false) status
  */
object Datom extends AsyncTestSuite {

  def testData(implicit conn: Conn, ec: ExecutionContext) = {
    for {
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

      txR3 <- Ns(e1).int(3).update
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

      // Third entity, a ref
      txR6 <- Ref1.str1("hello").save
      r1 = txR6.eid
      tx6 = txR6.tx
      t6 = txR6.t
      d6 = txR6.inst

      txR7 <- Ns(e2).ref1(r1).update
      tx7 = txR7.tx
      t7 = txR7.t
      d7 = txR7.inst
    } yield {
      (
        (tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
        (tx4, e2, t4, d4, tx5, t5, d5),
        (r1, tx6, t6, d6, tx7, t7, d7)
      )
    }
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Entities" - {

      "1 entity" - core { implicit conn =>
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3), _, _) <- testData

          // Several generic attributes can be asserted with a common entity id
          // Molecules with an applied entity id and only generic attributes
          // matches the Datom values of the entity

          // Entity `e1` has 2 asserted datoms (and the entity id)
          //      _ <- e1.touch === Map(
          //        ":db/id" -> e1,
          //        ":Ns/int" -> 3,
          //        ":Ns/str" -> "b")

          // Entity id returned
          _ <- Ns.e(e1).get === List(e1)
          // Same as
          _ <- Ns(e1).e.get === List(e1)

          // Attribute names
          _ <- Ns(e1).a.get === List(":Ns/int", ":Ns/str")

          // Attribute values (of `Any` type)
          _ <- Ns(e1).v.get === List("b", 3)

          // Transaction entity id
          _ <- Ns(e1).tx.get.map(_.sorted ==> List(tx2, tx3))

          // Transaction time t
          _ <- Ns(e1).t.get.map(_.sorted ==> List(t2, t3))

          // Transaction wall clock time as Date
          _ <- Ns(e1).txInstant.get.map(_.sorted ==> List(d2, d3))

          // Transaction operation: true: assert, false: retract
          _ <- Ns(e1).op.get === List(true)

          // Core 5 Datom values (quintuplets)
          _ <- Ns(e1).e.a.v.tx.op.get.map(_.sortBy(_._4) ==> List(
            (e1, ":Ns/str", "b", tx2, true),
            (e1, ":Ns/int", 3, tx3, true),
          ))

          // Generic attributes can be added in any order
          _ <- Ns(e1).v.e.op.tx.a.get.map(_.sortBy(_._4) ==> List(
            ("b", e1, true, tx2, ":Ns/str"),
            (3, e1, true, tx3, ":Ns/int"),
          ))
        } yield ()
      }


      "n entities" - core { implicit conn =>
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
            (tx4, e2, t4, d4, tx5, t5, d5),
            (r1, tx6, t6, d6, tx7, t7, d7)) <- testData

          _ <- Ns(e1, e2).tx.e.a.v.get.map(_.sortBy(_._1) ==> List(
            (tx2, e1, ":Ns/str", "b"),
            (tx3, e1, ":Ns/int", 3),
            (tx4, e2, ":Ns/str", "x"),
            (tx5, e2, ":Ns/int", 5),
            (tx7, e2, ":Ns/ref1", r1)
          ))
        } yield ()
      }


      "History" - core { implicit conn =>
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
            (tx4, e2, t4, d4, tx5, t5, d5),
            (r1, tx6, t6, d6, tx7, t7, d7)) <- testData

          _ <- Ns(e1, e2).tx.e.a.v.op.getHistory.map(_.sortBy(t => (t._1, t._3, t._5)) ==> List(
            (tx1, e1, ":Ns/int", 1, true),
            (tx1, e1, ":Ns/str", "a", true),
            (tx2, e1, ":Ns/str", "a", false),
            (tx2, e1, ":Ns/str", "b", true),
            (tx3, e1, ":Ns/int", 1, false),
            (tx3, e1, ":Ns/int", 3, true),
            (tx4, e2, ":Ns/int", 4, true),
            (tx4, e2, ":Ns/str", "x", true),
            (tx5, e2, ":Ns/int", 4, false),
            (tx5, e2, ":Ns/int", 5, true),
            (tx7, e2, ":Ns/ref1", r1, true),
          ))
        } yield ()
      }
    }


    "Attributes" - {

      "1 Attribute" - core { implicit conn =>
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
            (tx4, e2, t4, d4, tx5, t5, d5), _) <- testData

          _ <- Ns.int.e.get.map(_.sortBy(_._1) ==> List((3, e1), (5, e2)))
          _ <- Ns.int.a.get === List((3, ":Ns/int"), (5, ":Ns/int"))
          _ <- Ns.int.v.get === List((3, 3), (5, 5))
          _ <- Ns.int.tx.get.map(_.sortBy(_._1) ==> List((3, tx3), (5, tx5)))
          _ <- Ns.int.t.get.map(_.sortBy(_._1) ==> List((3, t3), (5, t5)))
          _ <- Ns.int.txInstant.get.map(_.sortBy(_._1).toString ==> List((3, d3), (5, d5)).toString)
          _ <- Ns.int.op.get === List((5, true), (3, true))

          // Generic attributes after attribute with applied value
          _ <- Ns.int(5).e.get === List((5, e2))
          _ <- Ns.int(5).a.get === List((5, ":Ns/int"))
          _ <- Ns.int(5).v.get === List((5, 5))
          _ <- Ns.int(5).tx.get === List((5, tx5))
          _ <- Ns.int(5).t.get === List((5, t5))
          _ <- Ns.int(5).txInstant.get.map(_.toString ==> List((5, d5)).toString)
          _ <- Ns.int(5).op.get === List((5, true))

          // Generic attributes after attribute with applied operation
          _ <- Ns.int.<(4).e.get === List((3, e1))
          _ <- Ns.int.<(4).a.get === List((3, ":Ns/int"))
          _ <- Ns.int.<(4).v.get === List((3, 3))
          _ <- Ns.int.<(4).tx.get === List((3, tx3))
          _ <- Ns.int.<(4).t.get === List((3, t3))
          _ <- Ns.int.<(4).txInstant.get.map(_.toString ==> List((3, d3)).toString)
          _ <- Ns.int.<(4).op.get === List((3, true))

          // Generic attributes after attribute with applied aggregate keyword
          _ <- Ns.int(max).e.get.map(_.sortBy(_._1) ==> List((3, e1), (5, e2)))
          _ <- Ns.int(max).a.get === List((5, ":Ns/int"))
          _ <- Ns.int(max).v.get === List((3, 3), (5, 5))
          _ <- Ns.int(max).tx.get === List((3, tx3), (5, tx5))
          _ <- Ns.int(max).t.get === List((3, t3), (5, t5))
          _ <- Ns.int(max).txInstant.get.map(_.sortBy(_._1).toString ==> List((3, d3), (5, d5)).toString)
          _ <- Ns.int(max).op.get === List((5, true))
        } yield ()
      }


      "order of attribute types" - core { implicit conn =>
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
            (tx4, e2, t4, d4, tx5, t5, d5), _) <- testData


          // Generic entity id first is ok
          _ <- Ns.e.int.tx.get.map(_.sortBy(_._2) ==> List(
            (e1, 3, tx3),
            (e2, 5, tx5),
          ))

          //            // Other generic attributes not allowed before first attribute
          //            expectCompileError (
          //                                 "Ns.t.op.int.get",
          //                                 "molecule.core.ops.exception.VerifyRawModelException: " +
          //                                   "Can't add first attribute `int` after generic attributes (except `e` which is ok to have first). " +
          //                                   "Please add generic attributes `t`, `op` after `int`.")

          // Generic attributes after custom attribute ok
          _ <- Ns.int.tx.op.get.map(_.sortBy(_._2) ==> List(
            (3, tx3, true),
            (5, tx5, true),
          ))

          // Custom attributes after generic attributes ok as long
          // as at least one custom attr is before generic attributes
          _ <- Ns.int.op.str.get === List(
            (3, true, "b"),
            (5, true, "x")
          )
        } yield ()
      }


      "Full scan" - core { implicit conn =>
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
            (tx4, e2, t4, d4, tx5, t5, d5), _) <- testData


          // Generic attributes without an entity id applied or custom attributes added would
          // cause a full scan of the whole database and is not allowed
          //            expectCompileError (
          //                                 "Ns.e.a.v.t.get",
          //                                 "molecule.core.ops.exception.VerifyRawModelException: " +
          //                                   "Molecule with only generic attributes and no entity id(s) applied are not allowed since " +
          //                                   "it would cause a full scan of the whole database.")

          // Any filter will prevent a full scan

          _ <- Ns.e.a(":Ns/str").v.tx.get.map(_.sortBy(_._4) ==> List(
            (e1, ":Ns/str", "b", tx2),
            (e2, ":Ns/str", "x", tx4)
          ))

          // Count also involves full scan if no other attribute is present
          //            expectCompileError (
          //                                 "Ns.e(count).get",
          //                                 "molecule.core.ops.exception.VerifyRawModelException: " +
          //                                   "Molecule with only generic attributes and no entity id(s) applied are not allowed since " +
          //                                   "it would cause a full scan of the whole database.")

          // If some attribute is also asserted, we can use count
          _ <- Ns.e(count).int_.get === List(2)
        } yield ()
      }

      "Ref" - core { implicit conn =>
        for {
          (_, _, (r1, tx6, t6, d6, tx7, t7, d7)) <- testData

          // Generic attributes after ref id
          _ <- Ns.Ref1.e.get === List(r1)
          _ <- Ns.Ref1.a.get === List(":Ref1/str1")
          _ <- Ns.Ref1.v.get === List("hello")
          // `ref1` ref datom asserted in tx7
          _ <- Ns.Ref1.tx.get === List(tx7)
          _ <- Ns.Ref1.t.get === List(t7)
          _ <- Ns.Ref1.txInstant.get.map(_.toString ==> List(d7).toString)
          _ <- Ns.Ref1.op.get === List(true)


          _ <- Ns.int.Ref1.e.get === List((5, r1))
          _ <- Ns.int.Ref1.a.get === List((5, ":Ref1/str1"))
          _ <- Ns.int.Ref1.v.get === List((5, "hello"))
          // `ref1` ref datom asserted in tx7
          _ <- Ns.int.Ref1.tx.get === List((5, tx7))
          _ <- Ns.int.Ref1.t.get === List((5, t7))
          _ <- Ns.int.Ref1.txInstant.get.map(_.toString ==> List((5, d7)).toString)
          _ <- Ns.int.Ref1.op.get === List((5, true))


          _ <- Ns.int.Ref1.e.str1.get === List((5, r1, "hello"))
          _ <- Ns.int.Ref1.a.str1.get === List((5, ":Ref1/str1", "hello"))
          _ <- Ns.int.Ref1.v.str1.get === List((5, "hello", "hello"))
          // `ref1` ref datom asserted in tx7
          _ <- Ns.int.Ref1.tx.str1.get === List((5, tx7, "hello"))
          _ <- Ns.int.Ref1.t.str1.get === List((5, t7, "hello"))
          _ <- Ns.int.Ref1.txInstant.str1.get.map(_.toString ==> List((5, d7, "hello")).toString)
          _ <- Ns.int.Ref1.op.str1.get === List((5, true, "hello"))


          _ <- Ns.int.Ref1.str1.e.get === List((5, "hello", r1))
          _ <- Ns.int.Ref1.str1.a.get === List((5, "hello", ":Ref1/str1"))
          _ <- Ns.int.Ref1.str1.v.get === List((5, "hello", "hello"))
          // `str1` datom asserted in tx6
          _ <- Ns.int.Ref1.str1.tx.get === List((5, "hello", tx6))
          _ <- Ns.int.Ref1.str1.t.get === List((5, "hello", t6))
          _ <- Ns.int.Ref1.str1.txInstant.get.map(_.toString ==> List((5, "hello", d6)).toString)
          _ <- Ns.int.Ref1.str1.op.get === List((5, "hello", true))

          // generic attr before ref
          _ <- Ns.int.op.Ref1.str1.op.get === List((5, true, "hello", true))
        } yield ()
      }

      //        "Tacit" - core { implicit conn =>
      //          for {
      //            expectCompileError(
      //            """m(Ns.int.tx_)""",
      //            "molecule.core.transform.exception.Dsl2ModelException: " +
      //              "Tacit `tx_` can only be used with an applied value i.e. `tx_(<value>)`")
      //              ok
      //          } yield ()
      //        }

    }


    "Expressions, mandatory" - core { implicit conn =>
      if (system != SystemPeerServer) {
        for {
          ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
            (tx4, e2, t4, d4, tx5, t5, d5),
            (r1, tx6, t6, d6, tx7, t7, d7)) <- testData

          _ <- Ns.e(e1).get === List(e1)
          _ <- Ns.e(e1, e2).get.map(_.sorted ==> List(e1, e2).sorted)

          _ <- Ns.e.not(e1).get.map(_.sorted ==> List(e2, r1).sorted)
          _ <- Ns.e.not(e1, e2).get === List(r1)

          // Eids not deterministic with dev-local
          _ <- if (system != SystemDevLocal) {
            for {
              _ <- Ns.e.>(e1).get.map(_.sorted ==> List(e2, r1).sorted)
              _ <- Ns.e.>=(e1).get.map(_.sorted ==> List(e1, e2, r1).sorted)
              _ <- Ns.e.<=(e2).get.map(_.sorted ==> List(e1, e2).sorted)
              res <- Ns.e.<(e2).get === List(e1)
            } yield res
          } else Future()


          // Only `e` before first custom attribute is allowed
          _ <- Ns.e(count).int_.get === List(2)
          _ <- Ns.int_.e(count).get === List(2)

          _ <- Ns.a(":Ns/str").get === List(":Ns/str")
          _ <- Ns.a(":Ns/str", ":Ns/int").get === List(":Ns/int", ":Ns/str")
          _ <- Ns.a.not(":Ns/str").get.map(_.sorted ==> List(":Ns/int", ":Ns/ref1", ":Ref1/str1"))
          _ <- Ns.a.not(":Ns/str", ":Ns/ref1").get === List(":Ns/int", ":Ref1/str1")
          _ <- Ns.a.>(":Ns/str").get === List(":Ref1/str1")
          _ <- Ns.a.>=(":Ns/str").get === List(":Ref1/str1", ":Ns/str")
          _ <- Ns.a.<=(":Ns/str").get === List(":Ns/int", ":Ns/ref1", ":Ns/str")
          _ <- Ns.a.<(":Ns/str").get === List(":Ns/int", ":Ns/ref1")
          // Range of attribute names
          _ <- Ns.a_.>(":Ns/int").a.<=(":Ns/str").get === List(":Ns/ref1", ":Ns/str")

          _ <- Ns.int_.a(count).get === List(1)

          _ <- Ns.v(3).get === List(3)
          _ <- Ns.v("hello").get === List("hello")
          _ <- Ns.v("non-existing value").get === Nil
          _ <- Ns.v(3, "b").get === List("b", 3)
          _ <- Ns.v.not(3).get.map(_.sortBy(_.toString) ==> List(r1, 5, "b", "hello", "x").sortBy(_.toString))
          _ <- Ns.v.not(3, "b").get.map(_.sortBy(_.toString) ==> List(r1, 5, "hello", "x").sortBy(_.toString))
          //            expectCompileError(
          //              """m(Ns.v.>(3))""",
          //              "molecule.core.transform.exception.Dsl2ModelException: " +
          //                "Can't compare generic values being of different types. Found: v.>(3)")

          _ <- Ns.tx(tx3).get === List(tx3)
          _ <- Ns.tx(tx3, tx5).get.map(_.sorted ==> List(tx3, tx5))

          // Note that no current datoms remains from tx1
          _ <- Ns.tx.not(tx3).get.map(_.sorted ==> List(tx2, tx4, tx5, tx6, tx7))
          // If we ask the history database, tx1 will show up though
          _ <- Ns.tx.not(tx3).getHistory.map(_.sorted ==> List(tx1, tx2, tx4, tx5, tx6, tx7))

          _ <- Ns.tx.not(tx3, tx5).get.map(_.sorted ==> List(tx2, tx4, tx6, tx7))
          _ <- Ns.tx.>(tx3).get.map(_.sorted ==> List(tx4, tx5, tx6, tx7))
          _ <- Ns.tx.>=(tx3).get.map(_.sorted ==> List(tx3, tx4, tx5, tx6, tx7))
          _ <- Ns.tx.<=(tx3).get.map(_.sorted ==> List(tx2, tx3)) // excludes tx1 per explanation above)
          _ <- Ns.tx.<=(tx3).getHistory.map(_.sorted ==> List(tx1, tx2, tx3)) // includes tx1 too per explanation above
          _ <- Ns.tx.<(tx3).get === List(tx2)
          // Range of transaction entity ids
          _ <- Ns.tx_.>(tx2).tx.<=(tx4).get.map(_.sorted ==> List(tx3, tx4))
          _ <- Ns.int_.tx(count).get === List(2)


          _ <- Ns.t(t3).get === List(t3)
          _ <- Ns.t(t3, t5).get.map(_.sorted ==> List(t3, t5))
          _ <- Ns.t.not(t3).get.map(_.sorted ==> List(t2, t4, t5, t6, t7))
          _ <- Ns.t.not(t3, t5).get.map(_.sorted ==> List(t2, t4, t6, t7))
          _ <- Ns.t.>(t3).get.map(_.sorted ==> List(t4, t5, t6, t7))
          _ <- Ns.t.>=(t3).get.map(_.sorted ==> List(t3, t4, t5, t6, t7))
          _ <- Ns.t.<=(t3).get.map(_.sorted ==> List(t2, t3))
          _ <- Ns.t.<=(t3).getHistory.map(_.sorted ==> List(t1, t2, t3))

          _ <- Ns.t.<(t3).get === List(t2)
          // Range of transaction t's
          _ <- Ns.t_.>(t2).t.<=(t4).get.map(_.sorted ==> List(t3, t4))
          _ <- Ns.int_.t(count).get === List(2)

          // OBS: Avoid using date expressions for precision expressions!
          // Since the minimum fraction of Date is ms, it will be imprecise.
          // For precise expressions, use t or tx.
          _ <- Ns.txInstant(d2).get === List(d2)
          _ <- Ns.txInstant(d2, d3).get.map(_.sorted ==> List(d2, d3).sorted)
          _ <- Ns.txInstant.not(d2).get.map(_.sorted ==> List(d3, d4, d5, d6, d7).sorted)
          _ <- Ns.txInstant.not(d2, d3).get.map(_.sorted ==> List(d4, d5, d6, d7).sorted)
          _ <- Ns.txInstant.>(d3).get.map(_.sorted ==> List(d4, d5, d6, d7).sorted)
          _ <- Ns.txInstant.>=(d3).get.map(_.sorted ==> List(d3, d4, d5, d6, d7).sorted)
          _ <- Ns.txInstant.<=(d3).get.map(_.sorted ==> List(d2, d3).sorted)
          _ <- Ns.txInstant.<=(d3).getHistory.map(_.sorted ==> List(d1, d2, d3).sorted)
          _ <- Ns.txInstant.<(d3).get.map(_.sorted ==> List(d2))
          // Range of transaction entity ids
          _ <- Ns.txInstant_.>(d2).txInstant.<=(d4).get.map(_.sorted ==> List(d3, d4).sorted)
          _ <- Ns.int_.txInstant(count).get === List(2)

          // No current datoms are retracted
          _ <- Ns.op(true).get === List(true)
          _ <- Ns.op(true, false).get === List(true)
          _ <- Ns.op(false).get === Nil
          _ <- Ns.op(false).getHistory === List(false)

          _ <- Ns.op.not(true).get === Nil
          _ <- Ns.op.not(true, false).get === Nil

          // Comparing boolean values not that relevant, but hey, here we go:
          _ <- Ns.op.>(true).get === Nil
          _ <- Ns.op.>(true).getHistory === Nil
          _ <- Ns.op.>(false).get === List(true)
          _ <- Ns.op.>(false).getHistory === List(true)

          _ <- Ns.op.>=(true).get === List(true)
          _ <- Ns.op.>=(true).getHistory === List(true)
          _ <- Ns.op.>=(false).get === List(true)
          _ <- Ns.op.>=(false).getHistory === List(false, true)

          _ <- Ns.op.<=(true).get === List(true)
          _ <- Ns.op.<=(true).getHistory === List(false, true)
          _ <- Ns.op.<=(false).get === Nil
          _ <- Ns.op.<=(false).getHistory === List(false)

          _ <- Ns.op.<(true).get === Nil
          _ <- Ns.op.<(true).getHistory === List(false)
          _ <- Ns.op.<(false).get === Nil
          _ <- Ns.op.<(false).getHistory === Nil

          _ <- Ns.int_.op(count).get === List(1)

          // Generic attributes only allowed to aggregate `count`
          //          expectCompileError (
          //                               """m(Ns.int.t(max))""",
          //                               "molecule.core.transform.exception.Dsl2ModelException: " +
          //                                 "Generic attributes only allowed to aggregate `count`. Found: `max`")
          //                               ok
        } yield ()
      }
    }


    "Expressions, tacit" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3),
          (tx4, e2, t4, d4, tx5, t5, d5), _) <- testData

        _ <- Ns.int.a_(":Ns/int").get === List(3, 5)
        _ <- Ns.int.a_(":Ns/str").get === Nil
        _ <- Ns.int.a_(":Ns/str", ":Ns/int").get === List(3, 5)
        _ <- Ns.int.a_.not(":Ns/str").get === List(3, 5)
        _ <- Ns.int.a_.not(":Ns/str", ":Ns/int").get === Nil
        _ <- Ns.int.a_.>(":Ns/str").get === Nil
        _ <- Ns.int.a_.>=(":Ns/str").get === Nil
        _ <- Ns.int.a_.<=(":Ns/str").get === List(3, 5)
        _ <- Ns.int.a_.<(":Ns/str").get === List(3, 5)

        _ <- Ns.int.v_(3).get === List(3)
        // Value only relates to previous custom datom
        _ <- Ns.int.v_("b").get === Nil
        _ <- Ns.int.v_(3, "b").get === List(3)
        _ <- Ns.int.v_.not(3).get === List(5)
        _ <- Ns.int.v_.not(3, "b").get === List(5)
        //          expectCompileError (
        //                               """m(Ns.int.v_.<=(3))""",
        //                               "molecule.core.transform.exception.Dsl2ModelException: " +
        //                                 "Can't compare generic values being of different types. Found: v_.<=(3)")

        _ <- Ns.int.tx_(tx3).get === List(3)
        _ <- Ns.int.tx_(tx3, tx5).get === List(3, 5)
        _ <- Ns.int.tx_.not(tx3).get === List(5)
        _ <- Ns.int.tx_.not(tx3, tx5).get === Nil
        _ <- Ns.int.tx_.>(tx3).get === List(5)
        _ <- Ns.int.tx_.>=(tx3).get === List(3, 5)
        _ <- Ns.int.tx_.<=(tx3).get === List(3)
        _ <- Ns.int.tx_.<(tx3).get === Nil
        // Int values withing range of transaction entity ids
        _ <- Ns.int.tx_.>(tx2).tx_.<=(tx4).get === List(3)
        _ <- Ns.int.tx_.>(tx2).tx_.<=(tx5).get === List(3, 5)

        _ <- Ns.int.t_(t3).get === List(3)
        _ <- Ns.int.t_(t3, t5).get === List(3, 5)
        _ <- Ns.int.t_.not(t3).get === List(5)
        _ <- Ns.int.t_.not(t3, t5).get === Nil
        _ <- Ns.int.t_.>(t3).get === List(5)
        _ <- Ns.int.t_.>=(t3).get === List(3, 5)
        _ <- Ns.int.t_.<=(t3).get === List(3)
        _ <- Ns.int.t_.<(t3).get === Nil
        // Int values withing range of transaction t's
        _ <- Ns.int.t_.>(t2).t_.<=(t4).get === List(3)
        _ <- Ns.int.t_.>(t2).t_.<=(t5).get === List(3, 5)

        _ <- Ns.int.txInstant_(d3).get === List(3)
        _ <- Ns.int.txInstant_(d3, d5).get === List(3, 5)
        _ <- Ns.int.txInstant_.not(d3).get === List(5)
        _ <- Ns.int.txInstant_.not(d3, d5).get === Nil
        _ <- Ns.int.txInstant_.>(d3).get === List(5)
        _ <- Ns.int.txInstant_.>=(d3).get === List(3, 5)
        _ <- Ns.int.txInstant_.<=(d3).get === List(3)
        _ <- Ns.int.txInstant_.<(d3).get === Nil

        // Int values withing range of transaction dates
        _ <- Ns.int.txInstant_.>(d2).txInstant_.<=(d4).get === List(3)
        _ <- Ns.int.txInstant_.>(d2).txInstant_.<=(d5).get === List(3, 5)

        _ <- Ns.int.op_(true).get === List(3, 5)
        _ <- Ns.int.op_(true, false).get === List(3, 5)
        _ <- Ns.int.op_(false).get === Nil

        // Retracted numbers from history db!
        // Since we can't recreate the Peer Server db history is accumulating
        if (system != SystemPeerServer)
        _ <- Ns.int.op_(false).getHistory === List(1, 4)

        _ <- Ns.int.op_.not(true).get === Nil
        _ <- Ns.int.op_.not(false).get === List(3, 5)
        _ <- Ns.int.op_.not(true, false).get === Nil
        // Skipping pretty meaningless boolean comparisons
      } yield ()
    }


    "Multiple tx attributes" - core { implicit conn =>
      for {
        ((tx1, e1, t1, d1, tx2, t2, d2, tx3, t3, d3), _,_) <- testData

        // Tacit attributes can be followed by generic attributes
        _ <- Ns(e1).str_.tx.get.map(_.head ==> tx2)
        _ <- Ns(e1).int_.tx.get.map(_.head ==> tx3)

        _ <- Ns(e1).str_.tx.int_.tx.get.map(_.head ==> (tx2, tx3))
      } yield ()
    }

    //      "Optional tx data not allowed" - core { implicit conn =>
    //        for {
    //          expectCompileError(
    //          """m(Ns.int$.tx.str)""",
    //          "molecule.core.transform.exception.Dsl2ModelException: Optional attributes (`int$`) can't be followed by generic transaction attributes (`tx`).")
    //
    //            expectCompileError (
    //            """m(Ns.int$.t.str)""",
    //            "molecule.core.transform.exception.Dsl2ModelException: Optional attributes (`int$`) can't be followed by generic transaction attributes (`t`).")
    //
    //            expectCompileError (
    //            """m(Ns.int$.txInstant.str)""",
    //            "molecule.core.transform.exception.Dsl2ModelException: Optional attributes (`int$`) can't be followed by generic transaction attributes (`txInstant`).")
    //
    //            expectCompileError (
    //            """m(Ns.int$.op.str)""",
    //            "molecule.core.transform.exception.Dsl2ModelException: Optional attributes (`int$`) can't be followed by generic transaction attributes (`op`).")
    //        } yield()
    //      }
  }
}