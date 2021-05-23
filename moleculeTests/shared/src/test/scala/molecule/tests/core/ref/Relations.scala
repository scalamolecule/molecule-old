package molecule.tests.core.ref

import molecule.core.util.testing.expectCompileError
import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out4._
import molecule.core.ops.exception.VerifyModelException
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object Relations extends AsyncTestSuite {

  lazy val tests = Tests {

    "One-to-One" - core { implicit conn =>
      for {
        // Creating 3 entities referencing 3 other entities
        tx <- Ns.str.Ref1.str1 insert List(
          ("a0", "a1"),
          ("b0", "b1"),
          ("c0", "c1")
        )
        List(a0, a1, b0, b1, c0, c1) = tx.eids

        // Get attribute values from 2 namespaces
        // Namespace references like `Ref1` starts with Capital letter
        _ <- Ns.str.Ref1.str1.get.map(_.sorted ==> List(
          ("a0", "a1"),
          ("b0", "b1"),
          ("c0", "c1")
        ))

        // We can also retrieve the referenced entity id
        // Referenced entity id `ref1` starts with lower case letter
        _ <- Ns.str.ref1.get.map(_.sorted ==> List(
          ("a0", a1),
          ("b0", b1),
          ("c0", c1)))
      } yield ()
    }

    "Referenced entity ids" - core { implicit conn =>
      for {
        tx <- Ref1.str1 insert List("father1", "father2", "father3")
        List(father1, father2, father3) = tx.eids

        // We can insert ref entity ids
        _ <- Ns.str.ref1 insert List(
          ("kid1", father1),
          ("kid2", father2),
          ("kid3", father3)
        )

        // Get attribute values from 2 namespaces
        // Namespace references like `Ref1` starts with Capital letter
        _ <- Ns.str.Ref1.str1.get.map(_.sorted ==> List(
          ("kid1", "father1"),
          ("kid2", "father2"),
          ("kid3", "father3")
        ))

        // We can also retrieve the referenced entity id
        // Referenced entity id `ref1` starts with lower case letter
        _ <- Ns.str.ref1.get.map(_.sorted ==> List(
          ("kid1", father1),
          ("kid2", father2),
          ("kid3", father3)))
      } yield ()
    }

    "Referenced entity ids" - core { implicit conn =>
      for {
        tx <- Ns.str("a").save
        id = tx.eid

        //    // Avoid mixing update/save semantics
        //    (Ns(id).Refs1.int1(1).save must throwA[VerifyModelException])
        //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
        //      "[unexpectedAppliedId]  Applying an eid is only allowed for updates."
        //
        //    // Updating across namespaces not allowed
        //
        //    (Ns(id).Refs1.int1(1).update must throwA[VerifyModelException])
        //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
        //      "[update_onlyOneNs]  Update molecules can't span multiple namespaces like `Ref1`."
      } yield ()
    }

    "Enum" - core { implicit conn =>
      for {
        _ <- Ns.str.enum insert List(("a", "enum0"))
        _ <- Ns.str.enum.get === List(("a", "enum0"))
      } yield ()
    }

    "Ref enum after ref" - core { implicit conn =>
      for {
        _ <- Ns.str.Ref1.enum1 insert List(("b", "enum10"))
        _ <- Ns.str.Ref1.enum1.get === List(("b", "enum10"))
      } yield ()
    }

    "Ref enum after attr" - core { implicit conn =>
      for {
        _ <- Ns.str.Ref1.int1.enum1 insert List(("c", 11, "enum11"))
        _ <- Ns.str.Ref1.int1.enum1.get === List(("c", 11, "enum11"))
      } yield ()
    }

    "BackRef" - core { implicit conn =>
      for {
        _ <- Ns.str.Ref1.str1._Ns.Refs1.int1 insert List(
          ("a", "a1", 1),
          ("b", "b1", 2))

        _ <- Ns.str.Ref1.str1._Ns.Refs1.int1.get === List(
          ("a", "a1", 1),
          ("b", "b1", 2))

        _ <- Ns.str.Refs1.int1._Ns.Ref1.str1.get === List(
          ("a", 1, "a1"),
          ("b", 2, "b1"))
      } yield ()
    }

    "BackRef, 2 levels" - core { implicit conn =>
      for {
        _ <- Ns.str.Ref1.str1.Ref2.str2._Ref1._Ns.Refs1.int1 insert List(
          ("a", "a1", "a2", 1),
          ("b", "b1", "b2", 2))

        _ <- Ns.str.Ref1.str1.Ref2.str2._Ref1._Ns.Refs1.int1.get === List(
          ("a", "a1", "a2", 1),
          ("b", "b1", "b2", 2))
      } yield ()
    }

    "Back ref, Adjacent" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1) insert List(("book", "John", "Marc"))
        _ <- m(Ns.str.Ref1.str1._Ns.Refs1.str1).get === List(("book", "John", "Marc"))
        _ <- m(Ns.str.Ref1.str1._Ns.Refs1 * Ref1.str1).get === List(("book", "John", List("Marc")))
      } yield ()
    }

    "Adjacent ref without attribute" - core { implicit conn =>
      for {
        _ <- Ns.str.Ref1.int1.Ref2.int2 insert List(
          ("a", 11, 12),
          ("b", 21, 22))

        // We can jump from namespace to namespace in queries
        _ <- Ns.str.Ref1.Ref2.int2.get === List(
          ("b", 22),
          ("a", 12))
      } yield ()
    }

    "Expecting mandatory attributes after card-one ref" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Ref1.int1) insert List(
          ("a", 1),
          ("b", 2))

        // Ok to ask for an optional referenced value
        _ <- m(Ns.str.Ref1.int1$).get.map(_.sortBy(_._1) ==> List(
          ("a", Some(1)),
          ("b", Some(2))))

        //    // But in insert molecules we don't want to create referenced orphan entities
        //    (m(Ns.str.Ref1.int1$).insert must throwA[VerifyModelException])
        //      .message === "Got the exception molecule.core.ops.exception.VerifyModelException: " +
        //      "[missingAttrInStartEnd]  Missing mandatory attributes of last namespace."
      } yield ()
    }

    "Aggregates one" - core { implicit conn =>
      for {
        _ <- m(Ns.str.ref1) insert List(
          ("a", 1L),
          ("b", 2L))

        _ <- m(Ns.str.ref1(count)).get === List(
          ("a", 1),
          ("b", 1))
      } yield ()
    }

    "Aggregates many" - core { implicit conn =>
      for {
        _ <- m(Ns.str.refs1) insert List(
          ("a", Set(1L)),
          ("b", Set(2L, 3L)))

        _ <- m(Ns.str.refs1(count)).get === List(
          ("a", 1),
          ("b", 2))
      } yield ()
    }

    "Self-refs" - core { implicit conn =>
      for {
        // OBS: not considered "Self-joins" in this context
        _ <- m(Ns.str.Parent.str) insert List(("child", "parent"))
        _ <- m(Ns.str.Parent.str).get === List(("child", "parent"))

        _ <- m(Ns.str.Parents * Ns.str) insert List(("child", List("parent1", "parent2")))
        _ <- m(Ns.str.Parents * Ns.str).get === List(("child", List("parent1", "parent2")))
        _ <- m(Ns.str.Parents.str).get === List(("child", "parent1"), ("child", "parent2"))
      } yield ()
    }

    "Many attribute + ref" - core { implicit conn =>
      for {
        _ <- m(Ns.str.Refs1.*(Ref1.int1)) insert List(("a", List(1, 2)))

        //    expectCompileError(
        //      "m(Ns.str.refs1.Refs1.int1)",
        //      "molecule.core.ops.exception.VerifyRawModelException: Instead of getting the ref id with `refs1` please get it via the referenced namespace: `Refs1.e ...`")
        //
        //    expectCompileError(
        //      "m(Ns.refs1.str.Refs1.int1)",
        //      "molecule.core.ops.exception.VerifyRawModelException: Instead of getting the ref id with `refs1` please get it via the referenced namespace: `Refs1.e ...`")
      } yield ()
    }

    //  "Molecule has to end with attribute" - {
    //    "Ending with ref" - core { implicit conn =>
    //      expectCompileError(
    //        "m(Ns.str.Ref1)",
    //        "molecule.core.ops.exception.VerifyRawModelException: Molecule not allowed to end with a reference. Please add one or more attribute to the reference.")
    //    }
    //
    //"Ending with refs" - core {   implicit conn =>
    //      expectCompileError(
    //        "m(Ns.str.Refs1)",
    //                "molecule.core.ops.exception.VerifyRawModelException: Molecule not allowed to end with a reference. Please add one or more attribute to the reference.")
    //    }
  }
}