package moleculeTests.tests.db.datomic.entity

import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.out4._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Retrieve attribute values by applying the namespace-prefixed attribute name to an entity id.
 */
object EntityAttr extends AsyncTestSuite {

  lazy val tests = Tests {

    "Single attribute" - core { implicit conn =>
      for {
        List(e, ref) <- Ns
          .str("single value")
          .ints(Set(1,2,3))
          .boolMap(Map("a" -> true, "b" -> false))
          .Ref1.str1("ref value")
          .save.map(_.eids)

        // Cardinality-one value
        _ <- e.apply(":Ns/str").map(_ ==> Some("single value"))
        // Same as
        _ <- e(":Ns/str").map(_ ==> Some("single value"))

        // Cardinality-many value
        _ <- e(":Ns/ints").map(_ ==> Some(Set(1, 2, 3)))

        // Cardinality-map value
        _ <- e(":Ns/boolMap").map(_ ==> Some(Map("a" -> true, "b" -> false)))

        // Ref attribute `:Ns/ref1` of entity `e` fetches entity map of attr/value pairs
        _ <- e(":Ns/ref1").map(_ ==> Some(
          Map(
            ":db/id" -> ref,
            ":Ref1/str1" -> "ref value"
          )
        ))

        // Attribute `:Ref1/str1` of entity `ref`
        _ <- ref(":Ref1/str1").map(_ ==> Some("ref value"))

        // Reverse lookup with back-ref (having prepended underscore) - entities pointing to `ref`
        _ <- ref(":Ns/_ref1").map(_ ==> Some(Set(e)))

        // Existing attribute that has no asserted value returns None
        _ <- e(":Ns/long").map(_ ==> None)

        // Non-existing attribute returns failed Future with exception
        _ <- e(":Ns/non-existing-attribute").recover { case MoleculeException(err, _) =>
          err ==> "Attribute `:Ns/non-existing-attribute` not found in schema."
        }
      } yield ()
    }


    "Multiple attributes" - core { implicit conn =>
      for {
        List(e, ref) <- Ns
          .str("single value")
          .ints(Set(1, 2, 3))
          .boolMap(Map("a" -> true, "b" -> false))
          .Ref1.str1("ref value")
          .save.map(_.eids)

        // Apply multiple attribute names to a single entity
        _ <- e(
          ":Ns/str",
          ":Ns/ints",
          ":Ns/boolMap",
          ":Ns/long", // (no value asserted)
          ":Ns/ref1"
        ).map(_ ==> List(
          Some("single value"),
          Some(Set(1, 2, 3)),
          Some(Map("a" -> true, "b" -> false)),
          None, // (:Ns/long not asserted)
          Some(
            Map(
              ":db/id" -> ref,
              ":Ref1/str1" -> "ref value"
            )
          )
        ))
      } yield ()
    }
  }
}
