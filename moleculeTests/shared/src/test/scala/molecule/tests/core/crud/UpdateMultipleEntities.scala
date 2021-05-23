package molecule.tests.core.crud

import molecule.datomic.api.out3._
import molecule.datomic.base.util.SystemPeer
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import molecule.tests.core.base.dsl.CoreTest._
import scala.concurrent.ExecutionContext.Implicits.global


object UpdateMultipleEntities extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card-one values" - {

      "apply" - core { implicit conn =>
        for {

          tx <- Ns.str.int insert List(
            ("a", 1),
            ("b", 2),
            ("c", 3),
            ("d", 4)
          )
          List(a, b, c, d) = tx.eids

          // Apply value to card-one attribute of multiple entities (retracts current values)
          _ <- Ns(a, b).int(5).update
          _ <- Ns.str.int.get.map(_.sorted ==> List(
            ("a", 5),
            ("b", 5),
            ("c", 3),
            ("d", 4)
          ))

          // Entity ids as Seq
          bc = Seq(b, c)
          _ <- Ns(bc).int(6).update
          _ <- Ns.str.int.get.map(_.sorted ==> List(
            ("a", 5),
            ("b", 6),
            ("c", 6),
            ("d", 4)
          ))

          // Apply empty value to card-one attribute of multiple entities (delete values)
          _ <- Ns(c, d).int().update
          _ <- Ns.str.int$.get.map(_.sorted ==> List(
            ("a", Some(5)),
            ("b", Some(6)),
            ("c", None),
            ("d", None)
          ))
        } yield ()
      }
    }


    "Card-one variables" - {

      "apply" - core { implicit conn =>
        for {
          tx <- Ns.str.int insert List(
            ("a", int1),
            ("b", int2),
            ("c", int3),
            ("d", int4)
          )
          List(a, b, c, d) = tx.eids

          // Apply value (retracts current values)
          _ <- Ns(a, b).int(int5).update
          _ <- Ns.str.int.get.map(_.sorted ==> List(
            ("a", int5),
            ("b", int5),
            ("c", int3),
            ("d", int4)
          ))

          // Apply empty value (delete values)
          _ <- Ns(b, d).int().update
          _ <- Ns.str.int$.get.map(_.sorted ==> List(
            ("a", Some(int5)),
            ("b", None),
            ("c", Some(int3)),
            ("d", None)
          ))
        } yield ()
      }
    }


    "Card-many values" - {

      "assert" - core { implicit conn =>
        for {
          tx <- Ns.str.ints insert List(
            ("a", Set(1)),
            ("b", Set(2)),
            ("c", Set(3)),
            ("d", Set(4))
          )
          List(a, b, c, d) = tx.eids

          // Add value
          _ <- Ns(a, b).ints.assert(5).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(1, 5)),
            ("b", Set(2, 5)),
            ("c", Set(3)),
            ("d", Set(4))
          ))

          // Add possibly existing value
          _ <- Ns(b, c).ints.assert(2).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(1, 5)),
            ("b", Set(2, 5)), // <-- 2 not added again
            ("c", Set(2, 3)), // <-- 2 added
            ("d", Set(4))
          ))

          // Add multiple values
          _ <- Ns(a, d).ints.assert(6, 7).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(7, 1, 6, 5)),
            ("b", Set(2, 5)),
            ("c", Set(3, 2)),
            ("d", Set(7, 4, 6))
          ))

          // Add empty Seq of values (no effect)
          _ <- Ns(a, c).ints.assert(Seq[Int]()).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(7, 1, 6, 5)),
            ("b", Set(2, 5)),
            ("c", Set(3, 2)),
            ("d", Set(7, 4, 6))
          ))
        } yield ()
      }

      "replace" - core { implicit conn =>
        for {
          tx <- Ns.str.ints insert List(
            ("a", Set(1, 2, 3)),
            ("b", Set(1, 2, 3)),
            ("c", Set(1, 2, 3)),
            ("d", Set(1, 2, 3))
          )
          List(a, b, c, d) = tx.eids

          // Replace values
          _ <- Ns(a, b).ints.replace(3 -> 4).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(1, 2, 4)), // 3 -> 4
            ("b", Set(1, 2, 4)), // 3 -> 4
            ("c", Set(1, 2, 3)),
            ("d", Set(1, 2, 3))
          ))

          // Replacing value to existing value simply retracts it
          _ <- Ns(b, c).ints.replace(2 -> 3).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(1, 2, 4)),
            ("b", Set(1, 3, 4)), // 2 -> 3
            ("c", Set(1, 3)), // 2 retracted
            ("d", Set(1, 2, 3))
          ))

          // Replace multiple values (vararg)
          _ <- Ns(a, d).ints.replace(1 -> 5, 2 -> 6).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(5, 6, 4)), // 1 -> 5, 2 -> 6
            ("b", Set(1, 3, 4)),
            ("c", Set(1, 3)),
            ("d", Set(5, 6, 3)) // 1 -> 5, 2 -> 6
          ))

          // Missing old values have no effect. The new value is inserted (upsert semantics)
          _ <- Ns(a, d).ints.replace(42 -> 7).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(7, 4, 6, 5)), // 7 added
            ("b", Set(1, 3, 4)),
            ("c", Set(1, 3)),
            ("d", Set(7, 6, 3, 5)) // 7 added
          ))
        } yield ()
      }

      "retract" - core { implicit conn =>
        for {
          tx <- Ns.str.ints insert List(
            ("a", Set(1, 2, 3)),
            ("b", Set(1, 2, 3)),
            ("c", Set(1, 2, 3)),
            ("d", Set(1, 2, 3))
          )
          List(a, b, c, d) = tx.eids


          // Remove values
          _ <- Ns(a, b).ints.retract(1).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(2, 3)),
            ("b", Set(2, 3)),
            ("c", Set(1, 2, 3)),
            ("d", Set(1, 2, 3))
          ))

          // Removing non-existing value has no effect
          _ <- Ns(a, b).ints.retract(7).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(2, 3)),
            ("b", Set(2, 3)),
            ("c", Set(1, 2, 3)),
            ("d", Set(1, 2, 3))
          ))

          // Removing duplicate values removes the distinct value
          _ <- Ns(a, b).ints.retract(2, 2).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(3)),
            ("b", Set(3)),
            ("c", Set(1, 2, 3)),
            ("d", Set(1, 2, 3))
          ))

          // Remove multiple values
          _ <- Ns(c, d).ints.retract(2, 3).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(3)),
            ("b", Set(3)),
            ("c", Set(1)),
            ("d", Set(1))
          ))

          // Removing empty Seq of values has no effect
          _ <- Ns(c, d).ints.retract(Seq[Int]()).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(3)),
            ("b", Set(3)),
            ("c", Set(1)),
            ("d", Set(1))
          ))
        } yield ()
      }

      "apply" - core { implicit conn =>
        for {
          tx <- Ns.str.ints insert List(
            ("a", Set(1, 2, 3)),
            ("b", Set(1, 2, 3)),
            ("c", Set(1, 2, 3)),
            ("d", Set(1, 2, 3))
          )
          List(a, b, c, d) = tx.eids


          // Apply value (retracts all current values!)
          _ <- Ns(a, b).ints(1).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(1)),
            ("b", Set(1)),
            ("c", Set(1, 2, 3)),
            ("d", Set(1, 2, 3))
          ))

          // Apply multiple values
          _ <- Ns(b, c).ints(2, 3).update
          _ <- Ns.str.ints.get.map(_.sortBy(_._1) ==> List(
            ("a", Set(1)),
            ("b", Set(2, 3)),
            ("c", Set(2, 3)),
            ("d", Set(1, 2, 3))
          ))

          // Apply empty Seq of values (retracts all values!)
          _ <- Ns(c, d).ints(Set[Int]()).update
          _ <- Ns.str.ints$.get.map(_.sortBy(_._1) ==> List(
            ("a", Some(Set(1))),
            ("b", Some(Set(2, 3))),
            ("c", None),
            ("d", None)
          ))

          // Apply nothing (retracts all values!)
          _ <- Ns(b, c).ints().update
          _ <- Ns.str.ints$.get.map(_.sortBy(_._1) ==> List(
            ("a", Some(Set(1))),
            ("b", None),
            ("c", None),
            ("d", None)
          ))
        } yield ()
      }
    }

    "Optional values" - core { implicit conn =>
      for {
        tx <- Ns.str.int insert List(
          ("a", 1),
          ("b", 2),
          ("c", 3),
          ("d", 4)
        )
        List(a, b, c, d) = tx.eids

        // Apply Some(value) to card-one attribute of multiple entities (retracts current values)
        _ <- Ns(a, b).str("e").int$(Some(5)).update
        _ <- Ns.e.str.int.get.map(_.sorted ==> List(
          (a, "e", 5),
          (b, "e", 5),
          (c, "c", 3),
          (d, "d", 4)
        ))

        // Apply None to card-one attribute of multiple entities (delete values)
        _ <- Ns(c, d).str("f").int$(None).update
        _ <- Ns.e.str.int$.get.map(_.sorted ==> List(
          (a, "e", Some(5)),
          (b, "e", Some(5)),
          (c, "f", None),
          (d, "f", None)
        ))

        // Reversing positions

        _ <- Ns(a, b).int$(Some(6)).str("g").update
        _ <- Ns.e.str.int$.get.map(_.sorted ==> List(
          (a, "g", Some(6)),
          (b, "g", Some(6)),
          (c, "f", None),
          (d, "f", None)
        ))

        _ <- Ns(b, c).int$(None).str("h").update
        _ <- Ns.e.str.int$.get.map(_.sorted ==> List(
          (a, "g", Some(6)),
          (b, "h", None),
          (c, "h", None),
          (d, "f", None)
        ))
      } yield ()
    }
  }
}