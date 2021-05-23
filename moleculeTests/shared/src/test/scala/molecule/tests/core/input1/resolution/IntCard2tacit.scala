package molecule.tests.core.input1.resolution

import molecule.datomic.base.ast.query._
import molecule.core.exceptions.MoleculeException
import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.in1_out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}


object IntCard2tacit extends AsyncTestSuite {

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
      val inputMolecule = m(Ns.int.ints_(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get === List(int6)

        _ <- inputMolecule(List(Set[Int]())).get === List(int6)

        _ <- inputMolecule(List(Set(int1))).get === List(int1)
        _ <- inputMolecule(List(Set(int2))).get === List(int1, int2)
        _ <- inputMolecule(List(Set(int3))).get === List(int2, int3)

        _ <- inputMolecule(List(Set(int1, int1))).get === List(int1)
        _ <- inputMolecule(List(Set(int1, int2))).get === List(int1)
        _ <- inputMolecule(List(Set(int1, int3))).get === Nil
        _ <- inputMolecule(List(Set(int2, int3))).get === List(int2)
        _ <- inputMolecule(List(Set(int4, int5))).get === List(int4, int5)

        _ <- inputMolecule(List(Set(int1, int2), Set[Int]())).get === List(int1)
        _ <- inputMolecule(List(Set(int1), Set(int1))).get === List(int1)
        _ <- inputMolecule(List(Set(int1), Set(int2))).get === List(int1, int2)
        _ <- inputMolecule(List(Set(int1), Set(int3))).get === List(int1, int2, int3)
        _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get === List(int1, int2, int3)
        _ <- inputMolecule(List(Set(int1), Set(int2, int3))).get === List(int1, int2)
        _ <- inputMolecule(List(Set(int1), Set(int2), Set(int3))).get === List(int1, int2, int3)
        _ <- inputMolecule(List(Set(int1, int2), Set(int3, int4))).get === List(int1, int3)
      } yield ()
    }

    "!=" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints_.not(?)) // or m(Ns.int.ints_.!=(?))
      for {
        _ <- manyData
        _ <- Ns.int.ints insert List(
          (int1, Set(int1, int2, int3)),
          (int2, Set(int2, int3, int4)),
          (int3, Set(int3, int4, int5))
        )

        _ <- inputMolecule(Nil).get === List(int1, int2, int3)
        _ <- inputMolecule(List(Set[Int]())).get === List(int1, int2, int3)

        _ <- inputMolecule(List(Set(int1))).get === List(int2, int3)
        _ <- inputMolecule(List(Set(int2))).get === List(int3)
        _ <- inputMolecule(List(Set(int3))).get === Nil

        _ <- inputMolecule(List(Set(int1, int2))).get === List(int2, int3)
        _ <- inputMolecule(List(Set(int1, int3))).get === List(int2, int3)
        _ <- inputMolecule(List(Set(int2, int3))).get === List(int3)

        _ <- inputMolecule(List(Set(int1), Set(int1))).get === List(int2, int3)
        _ <- inputMolecule(List(Set(int1), Set(int2))).get === List(int3)
        _ <- inputMolecule(List(Set(int1), Set(int3))).get === Nil
        _ <- inputMolecule(List(Set(int1), Set(int4))).get === Nil

        _ <- inputMolecule(List(Set(int1, int2), Set(int3))).get === Nil
        _ <- inputMolecule(List(Set(int1, int2), Set(int2, int3))).get === List(int3)
        _ <- inputMolecule(List(Set(int1, int2), Set(int4, int5))).get === List(int2)
      } yield ()
    }

    ">" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints_.>(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get === List(int1, int2, int3, int4, int5)
        _ <- inputMolecule(List(Set[Int]())).get === List(int1, int2, int3, int4, int5)

        // (int3, int4), (int4, int5), (int4, int5, int6)
        _ <- inputMolecule(List(Set(int2))).get === List(int2, int3, int4, int5)

        //(_ <- inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."


        //(_ <- inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."
      } yield ()
    }

    ">=" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints_.>=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get === List(int1, int2, int3, int4, int5)
        _ <- inputMolecule(List(Set[Int]())).get === List(int1, int2, int3, int4, int5)

        // (int2, int4), (int3, int4), (int4, int5), (int4, int5, int6)
        _ <- inputMolecule(List(Set(int2))).get === List(int1, int2, int3, int4, int5)
        //(_ <- inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."


        //(_ <- inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."
      } yield ()
    }

    "<" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints_.<(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get === List(int1, int2, int3, int4, int5)
        _ <- inputMolecule(List(Set[Int]())).get === List(int1, int2, int3, int4, int5)

        _ <- inputMolecule(List(Set(int2))).get === List(int1)
        //(_ <- inputMolecule(List(Set(int2, int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."


        //(_ <- inputMolecule(List(Set(int2), Set(int3))).get must throwA[MoleculeException])
        // .message === "Got the exception molecule.core.exceptions.package$MoleculeException: " +
        // "Can't apply multiple values to comparison function."
      } yield ()
    }

    "<=" - core { implicit conn =>
      val inputMolecule = m(Ns.int.ints_.<=(?))
      for {
        _ <- manyData
        _ <- inputMolecule(Nil).get === List(int1, int2, int3, int4, int5)
        _ <- inputMolecule(List(Set[Int]())).get === List(int1, int2, int3, int4, int5)
        _ <- inputMolecule(List(Set(int2))).get === List(int1, int2)

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
