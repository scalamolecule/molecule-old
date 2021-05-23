package molecule.tests.core.input1.resolution

import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import molecule.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object IntCard2 extends AsyncTestSuite {

  def manyData(implicit conn: Conn, ec: ExecutionContext): Future[TxReport] = {
    Ns.int.ints$ insert List(
      (int1, Some(Set(int1, int2))),
      (int2, Some(Set(int2, int3))),
      (int3, Some(Set(int3, int4))),
      (int4, Some(Set(int4, int5))),
      (int5, Some(Set(int4, int5, int6))),
      (int6, None)
    )
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Eq" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get === Nil

        // AND semantics when applying int1 Set of values matching input attribute values of int1 entity

        _ <- inputMolecule(List(Set[Int]())).get === Nil
        _ <- inputMolecule(List(Set(int1))).get === List((int1, Set(int1, int2)))
        _ <- inputMolecule(List(Set(int2))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)))
        _ <- inputMolecule(List(Set(int3))).get === List((int2, Set(int3, int2)), (int3, Set(int4, int3)))

        _ <- inputMolecule(List(Set(int1, int1))).get === List((int1, Set(int1, int2)))
        _ <- inputMolecule(List(Set(int1, int2))).get === List((int1, Set(int1, int2)))
        _ <- inputMolecule(List(Set(int1, int3))).get === Nil
        _ <- inputMolecule(List(Set(int2, int3))).get === List((int2, Set(int3, int2)))
        _ <- inputMolecule(List(Set(int4, int5))).get === List((int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

        _ <- inputMolecule(List(Set(int1), Set(int1))).get === List((int1, Set(int1, int2)))
        _ <- inputMolecule(List(Set(int1), Set(int2))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)))
        _ <- inputMolecule(List(Set(int1), Set(int3))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
        _ <- inputMolecule(List(Set(int1), Set(int2, int3))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)))
        _ <- inputMolecule(List(Set(int1), Set(int2), Set(int3))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))

        _ <- inputMolecule(List(Set(int1, int2), Set[Int]())).get === List((int1, Set(int1, int2)))
        _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)))
        _ <- inputMolecule(List(Set(int1, int2), Set(int3, int4))).get === List((int1, Set(int1, int2)), (int3, Set(int4, int3)))
      } yield ()
    }

    "!=" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints.not(?)) // or m(Ns.int.ints.!=(?))
      val all           = List(
        (1, Set(int1, int2, int3)),
        (2, Set(int2, int3, int4)),
        (3, Set(int3, int4, int5))
      )
      for {
        _ <- manyData
        _ <- Ns.int.ints insert all

        _ <- inputMolecule(Nil).get === all
        _ <- inputMolecule(List(Set[Int]())).get === all

        _ <- inputMolecule(List(Set(int1))).get === List(
          // (1, Set(int1, int2, int3)),  // int1 match
          (2, Set(int2, int3, int4)),
          (3, Set(int3, int4, int5))
        )
        _ <- inputMolecule(List(Set(int2))).get === List(
          // (1, Set(int1, int2, int3)),  // int2 match
          // (2, Set(int2, int3, int4)),  // int2 match
          (3, Set(int3, int4, int5))
        )
        _ <- inputMolecule(List(Set(int3))).get === Nil // int3 match all

        _ <- inputMolecule(List(Set(int1, int2))).get === List(
          // (1, Set(int1, int2, int3)),  // int1 AND int2 match
          (2, Set(int2, int3, int4)),
          (3, Set(int3, int4, int5))
        )

        _ <- inputMolecule(List(Set(int1, int3))).get === List(
          // (1, Set(int1, int2, int3)),  // int1 AND int3 match
          (2, Set(int2, int3, int4)),
          (3, Set(int3, int4, int5))
        )

        _ <- inputMolecule(List(Set(int2, int3))).get === List(
          (3, Set(int3, int4, int5))
        )

        _ <- inputMolecule(List(Set(int1), Set(int1))).get === List(
          // (1, Set(int1, int2, int3)),  // int1 match
          (2, Set(int2, int3, int4)),
          (3, Set(int3, int4, int5))
        )

        _ <- inputMolecule(List(Set(int1), Set(int2))).get === List(
          // (1, Set(int1, int2, int3)),  // int1 match, int2 match
          // (2, Set(int2, int3, int4)),  // int2 match
          (3, Set(int3, int4, int5))
        )

        _ <- inputMolecule(List(Set(int1), Set(int3))).get === Nil
        _ <- inputMolecule(List(Set(int1), Set(int4))).get === Nil
        _ <- inputMolecule(List(Set(int2), Set(int3))).get === Nil

        _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get === Nil
        _ <- inputMolecule(List(Set(int1, int2), Set(int2, int3))).get === List(
          (3, Set(int3, int4, int5))
        )
      } yield ()
    }

    ">" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints.>(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))
        _ <- inputMolecule(List(Set[Int]())).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

        // (int3, int4), (int4, int5), (int4, int5, int6)
        _ <- inputMolecule(List(Set(int2))).get === List((int2, Set(int3)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

        //(_ <- inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."


        //(_ <- inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."
      } yield ()
    }

    ">=" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints.>=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))
        _ <- inputMolecule(List(Set[Int]())).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

        // (int2, int4), (int3, int4), (int4, int5), (int4, int5, int6)
        _ <- inputMolecule(List(Set(int2))).get === List((int1, Set(int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))

        //(_ <- inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."


        //(_ <- inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."
      } yield ()
    }

    "<" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints.<(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))
        _ <- inputMolecule(List(Set[Int]())).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))
        _ <- inputMolecule(List(Set(int2))).get === List((int1, Set(int1)))

        //(_ <- inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."


        //(_ <- inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."
      } yield ()
    }

    "<=" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints.<=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))
        _ <- inputMolecule(List(Set[Int]())).get === List((int1, Set(int1, int2)), (int2, Set(int3, int2)), (int3, Set(int4, int3)), (int4, Set(int4, int5)), (int5, Set(int4, int6, int5)))
        _ <- inputMolecule(List(Set(int2))).get === List((int1, Set(int1, int2)), (int2, Set(int2)))
        //(_ <- inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."


        //(_ <- inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."
      } yield ()
    }
  }
}