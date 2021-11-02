package moleculeTests.tests.db.datomic.generic

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out5._
import utest._


object Index_VAET extends Base {

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Args" - core { implicit conn =>
      for {
        (_, (tx4, e2, t4, d4, tx5, t5, d5),
        (tx6, t6, d6, e3, tx7, t7, d7),
        (t8, e4, t9, t10, t11, tx12, t12, d12, tx13, t13, d13)) <- testData

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
      expectCompileError("m(VAET(42L).v.a.e(77L).t)",
        "molecule.core.transform.exception.Dsl2ModelException: " +
          "VAET index attributes not allowed to have values applied.\n" +
          "VAET index only accepts datom arguments: `VAET(<v/a/e/t>)`.")
    }
  }
}