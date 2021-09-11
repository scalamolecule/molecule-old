package moleculeTests.tests.core.crud.insert

import molecule.datomic.api.out6._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object InsertRelated extends AsyncTestSuite {

  lazy val tests = Tests {

    "Basics" - core { implicit conn =>
      for {
        // Asserting a fact in the `Ref1` namespace is the same as creating
        // one in the `Ns` namespace (no references between the two are made):

        tx1 <- Ns.str.insert("a0")
        a0 = tx1.eid
        _ <- a0.touch.map(_ ==> Map(
          ":db/id" -> a0,
          ":Ns/str" -> "a0"))

        tx2 <- Ref1.str1.insert("b0")
        b0 = tx2.eid
        _ <- b0.touch.map(_ ==> Map(
          ":db/id" -> b0,
          ":Ref1/str1" -> "b0"))

        // If we also assert a fact in `Ns` we will get an entity with
        // a :Ns/str assertion ("a0") of namespace `Ns` and a reference to an entity
        // with another :Ref1/str assertion ("b1") in namespace `Ref1`:

        tx3 <- Ns.str.Ref1.str1.insert("a0", "b1")
        List(a0ref, b1ref) = tx3.eids
        _ <- a0ref.touch.map(_ ==> Map(
          ":db/id" -> a0ref,
          ":Ns/str" -> "a0",
          ":Ns/ref1" -> Map(
            ":db/id" -> b1ref,
            ":Ref1/str1" -> "b1")
        ))


        // We can expand our graph one level deeper
        tx4 <- Ns.str.Ref1.str1.Ref2.str2.insert("a0", "b1", "c2")
        List(a0refs, b1ref1, c2ref2) = tx4.eids
        _ <- a0refs.touch.map(_ ==> Map(
          ":db/id" -> a0refs,
          ":Ns/ref1" -> Map(
            ":db/id" -> b1ref1,
            ":Ref1/ref2" -> Map(
              ":db/id" -> c2ref2,
              ":Ref2/str2" -> "c2"),
            ":Ref1/str1" -> "b1"),
          ":Ns/str" -> "a0"
        ))


        // We can limit the depth of the retrieved graph

        _ <- a0refs.touchMax(3).map(_ ==> Map(
          ":db/id" -> a0refs,
          ":Ns/ref1" -> Map(
            ":db/id" -> b1ref1,
            ":Ref1/ref2" -> Map(
              ":db/id" -> c2ref2,
              ":Ref2/str2" -> "c2"),
            ":Ref1/str1" -> "b1"),
          ":Ns/str" -> "a0"
        ))

        _ <- a0refs.touchMax(2).map(_ ==> Map(
          ":db/id" -> a0refs,
          ":Ns/ref1" -> Map(
            ":db/id" -> b1ref1,
            ":Ref1/ref2" -> c2ref2,
            ":Ref1/str1" -> "b1"),
          ":Ns/str" -> "a0"
        ))

        _ <- a0refs.touchMax(1).map(_ ==> Map(
          ":db/id" -> a0refs,
          ":Ns/ref1" -> b1ref1,
          ":Ns/str" -> "a0"
        ))

        // Use `touchQ` to generate a quoted graph that you can paste into your tests
        _ <- a0refs.touchQuotedMax(1).map(_ ==>
          s"""Map(
             |  ":db/id" -> ${a0refs}L,
             |  ":Ns/ref1" -> ${b1ref1}L,
             |  ":Ns/str" -> "a0")""".stripMargin)
      } yield ()
    }

    "Multiple values across namespaces" - core { implicit conn =>
      for {
        _ <- Ns.str.int.Ref1.str1.int1.Ref2.str2.int2.insert("a0", 0, "b1", 1, "c2", 2)
        _ <- Ns.str.int.Ref1.str1.int1.Ref2.str2.int2.get.map(_.head ==> ("a0", 0, "b1", 1, "c2", 2))

        _ <- Ns.strs.ints.Ref1.strs1.ints1.Ref2.strs2.ints2.insert(Set("a0"), Set(0), Set("b1"), Set(1), Set("c2"), Set(2))
        _ <- Ns.strs.ints.Ref1.strs1.ints1.Ref2.strs2.ints2.get.map(_.head ==> (Set("a0"), Set(0), Set("b1"), Set(1), Set("c2"), Set(2)))

        // Address example
        tx1 <- Ns.str.Ref1.int1.str1.Ref2.str2.insert("273 Broadway", 10700, "New York", "USA")
        List(addressE, streetE, countryE) = tx1.eids
        _ <- addressE.touch.map(_ ==> Map(
          ":db/id" -> addressE,
          ":Ns/ref1" -> Map(
            ":db/id" -> streetE,
            ":Ref1/int1" -> 10700,
            ":Ref1/ref2" -> Map(":db/id" -> countryE, ":Ref2/str2" -> "USA"),
            ":Ref1/str1" -> "New York"),
          ":Ns/str" -> "273 Broadway"))

        // We can even create chains of relationships without having intermediate attribute values
        _ <- Ns.str.Ref1.Ref2.int2.insert("a", 1)
        _ <- Ns.str.Ref1.Ref2.int2.get.map(_.head ==> ("a", 1))
      } yield ()
    }

    "Optional values" - core { implicit conn =>
      for {
        _ <- Ns.str.Ref1.str1$.Ref2.int2 insert List(
          ("a", Some("aa"), 1),
          ("b", None, 2)
        )

        _ <- Ns.str.Ref1.str1$.Ref2.int2.get.map(_.sortBy(_._1) ==> List(
          ("a", Some("aa"), 1),
          ("b", None, 2),
        ))
        _ <- Ns.str.Ref1.str1.Ref2.int2.get.map(_ ==> List(
          ("a", "aa", 1)
        ))
      } yield ()
    }

    "Card many references" - core { implicit conn =>
      for {
        tx1 <- Ns.int.Refs1.str1.insert(42, "r")
        List(base, ref) = tx1.eids
        _ <- base.touch.map(_ ==> Map(
          ":db/id" -> base,
          ":Ns/refs1" -> List( // <-- notice we have a list of references now (with one ref here)
            Map(":db/id" -> ref, ":Ref1/str1" -> "r")),
          ":Ns/int" -> 42
        ))


        // Note that applying multiple values creates multiple base entities with a
        // reference to each new `:Ref1/str` assertion, so that we get the following:

        tx2 <- Ns.int.Refs1.str1.insert.apply(Seq((1, "r"), (2, "s")))
        List(id1, ref1, id2, ref2) = tx2.eids
        _ <- id1.touch.map(_ ==> Map(
          ":db/id" -> id1,
          ":Ns/refs1" -> List(
            Map(":db/id" -> ref1, ":Ref1/str1" -> "r")),
          ":Ns/int" -> 1
        ))
        _ <- id2.touch.map(_ ==> Map(
          ":db/id" -> id2,
          ":Ns/refs1" -> List(
            Map(":db/id" -> ref2, ":Ref1/str1" -> "s")),
          ":Ns/int" -> 2
        ))
      } yield ()
    }
  }
}