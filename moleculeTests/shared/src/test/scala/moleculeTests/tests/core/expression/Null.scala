package moleculeTests.tests.core.expression

import molecule.datomic.api.in1_out2._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object Null extends AsyncTestSuite {

  lazy val tests = Tests {

    "Card one" - core { implicit conn =>
      for {
        _ <- Ns.str.int$ insert List(
          ("a", Some(1)),
          ("b", Some(2)),
          ("c", Some(3)),
          ("d", None)
        )

        // Apply empty value to match entities with non-asserted attributes (null)
        _ <- Ns.str.int_().get.map(_ ==> List("d"))

        // Same as applying empty Iterables
        _ <- Ns.str.int_(Nil).get.map(_ ==> List("d"))
        _ <- Ns.str.int_(List()).get.map(_ ==> List("d"))

        // Applying empty value to mandatory attribute is contradictive and matches no entities.
        _ <- Ns.str.int().get.map(_ ==> Nil)

        // Applying possibly empty list as variable simply yields empty result set
        emptyList = Nil
        _ <- Ns.int(emptyList).get.map(_ ==> Nil)

        // Apply Nil to tacit attribute of input molecule
        _ <- m(Ns.str.int_(?)).apply(Nil).get.map(_ ==> List("d"))

        // Apply Nil to mandatory attribute of input molecule never matches any entities
        _ <- m(Ns.str.int(?)).apply(Nil).get.map(_ ==> Nil)
      } yield ()
    }

    "Card many" - core { implicit conn =>
      for {
        _ <- Ns.int.ints$ insert List(
          (10, Some(Set(1, 2))),
          (20, Some(Set(2, 3))),
          (30, Some(Set(3, 4))),
          (40, None)
        )

        // Apply empty value to match entities with non-asserted attributes (null)
        _ <- Ns.int.ints_().get.map(_ ==> List(40))

        // Same as applying empty Iterables
        _ <- Ns.int.ints_(Nil).get.map(_ ==> List(40))
        _ <- Ns.int.ints_(List()).get.map(_ ==> List(40))

        // Applying empty value to mandatory attribute is contradictive and never matches entities.
        _ <- Ns.int.ints().get.map(_ ==> Nil)

        // Applying possibly empty list as variable simply yields empty result set
        emptyList = Nil
        _ <- Ns.ints(emptyList).get.map(_ ==> Nil)

        // Apply Nil to tacit attribute of input molecule
        _ <- m(Ns.int.ints_(?)).apply(Nil).get.map(_ ==> List(40))

        // Apply Nil to mandatory attribute of input molecule never matches any entities
        _ <- m(Ns.int.ints(?)).apply(Nil).get.map(_ ==> Nil)
      } yield ()
    }
  }
}
