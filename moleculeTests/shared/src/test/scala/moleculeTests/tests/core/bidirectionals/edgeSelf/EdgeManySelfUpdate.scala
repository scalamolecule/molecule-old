package moleculeTests.tests.core.bidirectionals.edgeSelf

import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.api.in1_out3._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.bidirectionals.dsl.Bidirectional._
import utest._
import scala.concurrent.{ExecutionContext, Future}

object EdgeManySelfUpdate extends AsyncTestSuite {

  val knownBy = m(Person.name_(?).Knows.*(Knows.weight.Person.name))

  def testData(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Long]] = {
    for {
      tx1 <- Person.name("Ann").save

      // Separate edges
      tx2 <- Knows.weight.Person.name.insert(List(
        (2, "Ben"),
        (3, "Don"),
        (4, "Gil"),
        (5, "Han"),
        (6, "Joe"),
        (7, "Ron"),
        (8, "Tom")
      ))
    } yield tx1.eid +: tx2.eids.grouped(3).map(_.head).toSeq
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "add edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // vararg
        _ <- Person(ann).knows.assert(knowsBen, knowsDon).update

        // Seq
        _ <- Person(ann).knows.assert(Seq(knowsGil)).update

        // Empty list of edges has no effect
        _ <- Person(ann).knows.assert(Seq()).update

        _ <- knownBy("Ann").get.map(_ ==> List(List((2, "Ben"), (3, "Don"), (4, "Gil"))))
        _ <- knownBy("Ben").get.map(_ ==> List(List((2, "Ann"))))
        _ <- knownBy("Don").get.map(_ ==> List(List((3, "Ann"))))
        _ <- knownBy("Gil").get.map(_ ==> List(List((4, "Ann"))))
      } yield ()
    }


    "replace edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // current friends
        _ <- Person(ann).knows.assert(knowsBen, knowsDon, knowsGil, knowsTom).update

        _ <- knownBy("Ann").get.map(_ ==> List(List((2, "Ben"), (3, "Don"), (4, "Gil"), (8, "Tom"))))
        _ <- knownBy("Ben").get.map(_ ==> List(List((2, "Ann"))))
        _ <- knownBy("Don").get.map(_ ==> List(List((3, "Ann"))))
        _ <- knownBy("Gil").get.map(_ ==> List(List((4, "Ann"))))
        _ <- knownBy("Tom").get.map(_ ==> List(List((8, "Ann"))))

        // Replace who Ann knows
        _ <- Person(ann).knows.replace(knowsBen -> knowsHan, knowsDon -> knowsJoe).update
        _ <- Person(ann).knows.replace(Seq(knowsGil -> knowsRon)).update

        // All friends have been replaced
        _ <- knownBy("Ann").get.map(_ ==> List(List((5, "Han"), (6, "Joe"), (7, "Ron"), (8, "Tom"))))
        _ <- knownBy("Han").get.map(_ ==> List(List((5, "Ann"))))
        _ <- knownBy("Joe").get.map(_ ==> List(List((6, "Ann"))))
        _ <- knownBy("Ron").get.map(_ ==> List(List((7, "Ann"))))
        _ <- knownBy("Tom").get.map(_ ==> List(List((8, "Ann")))) // Hasn't been replace by empty Seq

        // Replace with empty Seq has no effect
        _ <- Person(ann).knows.replace(Seq()).update
        _ <- knownBy("Ann").get.map(_ ==> List(List((5, "Han"), (6, "Joe"), (7, "Ron"), (8, "Tom"))))
      } yield ()
    }


    "remove edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // current friends
        _ <- Person(ann).knows.assert(knowsBen, knowsDon, knowsGil, knowsTom).update

        _ <- knownBy("Ann").get.map(_ ==> List(List((2, "Ben"), (3, "Don"), (4, "Gil"), (8, "Tom"))))
        _ <- knownBy("Ben").get.map(_ ==> List(List((2, "Ann"))))
        _ <- knownBy("Don").get.map(_ ==> List(List((3, "Ann"))))
        _ <- knownBy("Gil").get.map(_ ==> List(List((4, "Ann"))))
        _ <- knownBy("Tom").get.map(_ ==> List(List((8, "Ann"))))


        // Remove who Ann knows
        _ <- Person(ann).knows.retract(knowsBen, knowsDon).update

        // All friends have been replaced
        _ <- knownBy("Ann").get.map(_ ==> List(List((4, "Gil"), (8, "Tom"))))
        _ <- knownBy("Ben").get.map(_ ==> List())
        _ <- knownBy("Don").get.map(_ ==> List())
        _ <- knownBy("Gil").get.map(_ ==> List(List((4, "Ann"))))
        _ <- knownBy("Tom").get.map(_ ==> List(List((8, "Ann"))))

        // Remove Seq of edges
        _ <- Person(ann).knows.retract(Seq(knowsGil)).update

        // All friends have been replaced
        _ <- knownBy("Ann").get.map(_ ==> List(List((8, "Tom"))))
        _ <- knownBy("Ben").get.map(_ ==> List())
        _ <- knownBy("Don").get.map(_ ==> List())
        _ <- knownBy("Gil").get.map(_ ==> List())
        _ <- knownBy("Tom").get.map(_ ==> List(List((8, "Ann"))))

        // Remove empty Seq of edges has no effect
        _ <- Person(ann).knows.retract(Seq()).update

        // All friends have been replaced
        _ <- knownBy("Ann").get.map(_ ==> List(List((8, "Tom"))))
      } yield ()
    }


    "apply edges" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // current friends
        _ <- Person(ann).knows.assert(knowsBen, knowsDon, knowsGil).update

        _ <- knownBy("Ann").get.map(_ ==> List(List((2, "Ben"), (3, "Don"), (4, "Gil"))))
        _ <- knownBy("Ben").get.map(_ ==> List(List((2, "Ann"))))
        _ <- knownBy("Don").get.map(_ ==> List(List((3, "Ann"))))
        _ <- knownBy("Gil").get.map(_ ==> List(List((4, "Ann"))))
        _ <- knownBy("Tom").get.map(_ ==> List())


        // State who Ann knows now
        _ <- Person(ann).knows(knowsBen, knowsTom).update

        // Ben remains, Tom added
        _ <- knownBy("Ann").get.map(_ ==> List(List((2, "Ben"), (8, "Tom"))))
        _ <- knownBy("Ben").get.map(_ ==> List(List((2, "Ann"))))
        _ <- knownBy("Don").get.map(_ ==> List())
        _ <- knownBy("Gil").get.map(_ ==> List())
        _ <- knownBy("Tom").get.map(_ ==> List(List((8, "Ann"))))

        // Apply Seq of edges
        _ <- Person(ann).knows(Seq(knowsHan)).update

        _ <- knownBy("Ann").get.map(_ ==> List(List((5, "Han"))))
        _ <- knownBy("Ben").get.map(_ ==> List())
        _ <- knownBy("Don").get.map(_ ==> List())
        _ <- knownBy("Gil").get.map(_ ==> List())
        _ <- knownBy("Han").get.map(_ ==> List(List((5, "Ann"))))
        _ <- knownBy("Tom").get.map(_ ==> List())

        // Applying empty Seq retracts all edges from Ann!
        _ <- Person(ann).knows(Seq()).update

        _ <- knownBy("Ann").get.map(_ ==> List())
        _ <- knownBy("Han").get.map(_ ==> List())


        // Applying no values removes all!

        _ <- Person(ann).Knows.weight(7).Person.name("Bob").update
        _ <- knownBy("Ann").get.map(_ ==> List(List((7, "Bob"))))

        _ <- Person(ann).knows().update
        _ <- knownBy("Ann").get.map(_ ==> List())
      } yield ()
    }


    "retract edge" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // current friends
        _ <- Person(ann).knows.assert(knowsBen, knowsDon).update

        _ <- knownBy("Ann").get.map(_ ==> List(List((2, "Ben"), (3, "Don"))))
        _ <- knownBy("Ben").get.map(_ ==> List(List((2, "Ann"))))
        _ <- knownBy("Don").get.map(_ ==> List(List((3, "Ann"))))

        // Retract single edge
        _ <- knowsBen.map(_.retract)

        _ <- knownBy("Ann").get.map(_ ==> List(List((3, "Don"))))
        _ <- knownBy("Ben").get.map(_ ==> List())
        _ <- knownBy("Don").get.map(_ ==> List(List((3, "Ann"))))
      } yield ()
    }


    "retract base/target entity" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // current friends
        _ <- Person(ann).knows.assert(knowsBen, knowsDon).update

        _ <- knownBy("Ann").get.map(_ ==> List(List((2, "Ben"), (3, "Don"))))
        _ <- knownBy("Ben").get.map(_ ==> List(List((2, "Ann"))))
        _ <- knownBy("Don").get.map(_ ==> List(List((3, "Ann"))))

        // Retract base entity
        _ <- ann.map(_.retract)

        // All knowing to/from Ann retracted
        _ <- knownBy("Ann").get.map(_ ==> List())
        _ <- knownBy("Ben").get.map(_ ==> List())
        _ <- knownBy("Don").get.map(_ ==> List())
      } yield ()
    }


    "no nested in update molecules" - bidirectional { implicit conn =>
      for {
        Seq(ann, knowsBen, knowsDon, knowsGil, knowsHan, knowsJoe, knowsRon, knowsTom) <- testData

        // Can't update multiple values of cardinality-one attribute `name`
        _ <- Person(ann).Knows.weight(7).Person.name("Joe", "Liz").update.recover {
          case VerifyModelException(err) =>
            err ==> s"[noConflictingCardOneValues]  Can't update multiple values for cardinality-one attribute:" +
              "\n  Person ... name(Joe, Liz)"
        }

        // As with save molecules nesting is not allowed in update molecules
        _ <- Person(ann).Knows.*(Knows.weight(4)).name("Joe").update.recover {
          case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't have nested data structures like `Knows`."
        }

        _ <- Person(ann).Knows.*(Knows.weight(4)).e(42L).update.recover {
          case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't have nested data structures like `Knows`."
        }

        _ <- Person(ann).Knows.*(Knows.weight(4).Person.name("Joe")).update.recover {
          case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't have nested data structures like `Knows`."
        }

        _ <- Person(ann).Knows.*(Knows.weight(4).person(42L)).update.recover {
          case VerifyModelException(err) =>
            err ==> s"[update_onlyOneNs]  Update molecules can't have nested data structures like `Knows`."
        }

        // Note that an edge always have only one target entity.
        // So we can't add multiple (won't compile)
        // Person(ann).Knows.weight(6).person(42L, 43L).update

        // Each edge has only 1 target entity so we can't use nested structures on the target namespace
        // (Person.name("Ben").Knows.weight(7).Person.*(Person.name("Joe")).update
        //                                                   ^ nesting of edge target namespace not available
      } yield ()
    }
  }
}
