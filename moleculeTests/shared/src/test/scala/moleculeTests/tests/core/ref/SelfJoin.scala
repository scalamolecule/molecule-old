package moleculeTests.tests.core.ref

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out8._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.ref.dsl.SelfJoin._
import utest._
import scala.concurrent.{ExecutionContext, Future}

object SelfJoin extends AsyncTestSuite {

  def data(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    m(Person.age.name.Likes * Score.beverage.rating) insert List(
      (23, "Joe", List(("Coffee", 3), ("Cola", 2), ("Pepsi", 3))),
      (25, "Ben", List(("Coffee", 2), ("Tea", 3))),
      (23, "Liz", List(("Coffee", 1), ("Tea", 3), ("Pepsi", 1))))
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "AND, unify attributes" - selfJoin { implicit conn =>
      for {
        _ <- data

        // Normally we ask for values accross attributes like
        // attr1 AND attr2 AND etc as in
        // age==23 AND name AND rating==Pepsi
        _ <- Person.age_(23).name.Likes.beverage_("Pepsi").get.map(_ ==> List("Liz", "Joe"))

        // But when we need to compare values of the same attribute
        // across entities we need self-joiPerson.

        // Here's an example of a self-join where we take pairs of entities
        // where one is 23 years old and the other 25 years old and then see
        // which of those pairs have a shared preferred beverage. We say that
        // we "unify" by the beverage attribute value (Likes.beverage) - the values
        // that the two entities have in common.

        // What beverages do pairs of 23- AND 25-year-olds like in common?
        // (unifying on Likes.beverage)
        _ <- Person.age_(23 and 25).Likes.beverage.get.map(_ ==> List("Coffee", "Tea"))

        // Does 23- and 25-years-old have some common beverage ratings?
        // (unifying on Likes.rating)
        _ <- Person.age_(23 and 25).Likes.rating.get.map(_ ==> List(2, 3))

        // Any 23- and 25-year-olds with the same name? (no)
        // (unifying on Person.name)
        _ <- Person.age_(23 and 25).name.get.map(_ ==> List())


        // Which beverages do Joe AND Liz both like?
        // (unifying on Likes.beverage)
        _ <- Person.name_("Joe" and "Liz").Likes.beverage.get.map(_ ==> List("Pepsi", "Coffee"))

        // Do Joe AND Liz have some common ratings?
        // (unifying on Likes.rating)
        _ <- Person.name_("Joe" and "Liz").Likes.rating.get.map(_ ==> List(3))

        // Do Joe AND Liz have a shared age?
        // (unifying on Person.age)
        _ <- Person.name_("Joe" and "Liz").age.get.map(_ ==> List(23))


        // Who likes both Coffee AND Tea?
        // (unifying on Person.name)
        _ <- Person.name.Likes.beverage_("Coffee" and "Tea").get.map(_ ==> List("Ben", "Liz"))

        // What ages have those who like both Coffe and Tea?
        // (unifying on Person.age)
        _ <- Person.age.Likes.beverage_("Coffee" and "Tea").get.map(_ ==> List(23, 25))

        // What shared ratings do Coffee and Tea have?
        // (unifying on Score.rating)
        _ <- Score.beverage_("Coffee" and "Tea").rating.get.map(_ ==> List(3))


        // Who rated both 2 AND 3?
        // (unifying on Person.name)
        _ <- Person.name.Likes.rating_(2 and 3).get.map(_ ==> List("Ben", "Joe"))

        // What ages have those who rated both 2 AND 3?
        // (unifying on Person.age)
        _ <- Person.age.Likes.rating_(2 and 3).get.map(_ ==> List(23, 25))

        // Which beverages are rated 2 AND 3?
        // (unifying on Likes.beverage)
        _ <- Score.rating_(2 and 3).beverage.get.map(_ ==> List("Coffee"))


        // Unifying by 2 attributes

        // Which 23- AND 25-year-olds with the same name like the same beverage? (none)
        // (unifying on Person.name AND Likes.beverage)
        _ <- Person.age_(23 and 25).name.Likes.beverage.get.map(_ ==> List())

        // Do Joe and Liz share age and beverage preferences? (yes)
        // (unifying on Person.age AND Likes.beverage)
        _ <- Person.age.name_("Joe" and "Liz").Likes.beverage.get.map(_ ==> List(
          (23, "Coffee"),
          (23, "Pepsi")))


        // Multiple ANDs
        _ <- Person.name_("Joe" and "Ben" and "Liz").Likes.beverage.get.map(_ ==> List("Coffee"))
      } yield ()
    }

    "AND, unify attribute maps" - selfJoin { implicit conn =>
      for {
        _ <- data

        // age - name(in various languages) - beverage - rating
        _ <- m(Person.age.nameL.Likes * Score.beverage.rating) insert List(
          (23, Map("en" -> "Joe", "da" -> "Jonas"), List(("Coffee", 3), ("Cola", 2), ("Pepsi", 3))),
          (25, Map("en" -> "Ben", "es" -> "Benito"), List(("Coffee", 2), ("Tea", 3))),
          (23, Map("en" -> "Liz", "da" -> "Lis"), List(("Coffee", 1), ("Tea", 3), ("Pepsi", 1))))


        // Whkat beverages do 23- AND 25-year-olds like in common?
        // (unifying on Likes.beverage)
        _ <- Person.age_(23 and 25).Likes.beverage.get.map(_ ==> List("Coffee", "Tea"))

        // Does 23- and 25-years-old have some common beverage ratings?
        // (unifying on Likes.rating)
        _ <- Person.age_(23 and 25).Likes.rating.get.map(_ ==> List(2, 3))

        // Any 23- and 25-year-olds with the same name? (no)
        // (unifying on Person.nameL)
        _ <- Person.age_(23 and 25).nameL.get.map(_ ==> List())


        // Which beverages do Joe AND Liz both like?
        // (unifying on Likes.beverage)
        // Given the names are saved in an attribute map we can retrieve
        // all combinations of names in different languages.
        // Note that this `and` notation is to only create a self-join
        // - not to retrieve multiple values from one strMap!
        _ <- Person.nameL_("Joe" and "Liz").Likes.beverage.get.map(_ ==> List("Pepsi", "Coffee"))
        _ <- Person.nameL_("Joe" and "Lis").Likes.beverage.get.map(_ ==> List("Pepsi", "Coffee"))
        _ <- Person.nameL_("Jonas" and "Liz").Likes.beverage.get.map(_ ==> List("Pepsi", "Coffee"))
        _ <- Person.nameL_("Jonas" and "Lis").Likes.beverage.get.map(_ ==> List("Pepsi", "Coffee"))

        // Do Joe AND Liz have some common ratings?
        // (unifying on Likes.rating)
        _ <- Person.nameL_("Joe" and "Liz").Likes.rating.get.map(_ ==> List(3))
        _ <- Person.nameL_("Joe" and "Lis").Likes.rating.get.map(_ ==> List(3))
        _ <- Person.nameL_("Jonas" and "Liz").Likes.rating.get.map(_ ==> List(3))
        _ <- Person.nameL_("Jonas" and "Lis").Likes.rating.get.map(_ ==> List(3))

        // Do Joe AND Liz have a shared age?
        // (unifying on Person.age)
        _ <- Person.nameL_("Joe" and "Liz").age.get.map(_ ==> List(23))
        _ <- Person.nameL_("Joe" and "Lis").age.get.map(_ ==> List(23))
        _ <- Person.nameL_("Jonas" and "Liz").age.get.map(_ ==> List(23))
        _ <- Person.nameL_("Jonas" and "Lis").age.get.map(_ ==> List(23))


        // Who likes both Coffee AND Tea?
        // (unifying on Person.nameL)
        _ <- Person.nameL.Likes.beverage_("Coffee" and "Tea").get.map(_ ==> List(
          Map(
            "en" -> "Liz",
            "da" -> "Lis",
            "es" -> "Benito"
          )
        ))
        // Since the merged Map of results contains multiple pairs
        // with the same key, the pairs coalesce to a smaller set:
        //    Map(
        //      "en" -> "Ben", // Same key - this one went
        //      "en" -> "Liz", // Same key
        //      "da" -> "Lis",
        //      "es" -> "Benito"
        //    )
        // Instead we could unify also by the entity id and then filter it out
        // to get a more useful result:
        _ <- Person.e.nameL.Likes.beverage_("Coffee" and "Tea").get.map(_.map(_._2) ==> List(
          Map("en" -> "Ben", "es" -> "Benito"),
          Map("en" -> "Liz", "da" -> "Lis")
        ))

        // What ages have those who like both Coffe and Tea?
        // (unifying on Person.age)
        _ <- Person.age.Likes.beverage_("Coffee" and "Tea").get.map(_ ==> List(23, 25))

        // What shared ratings do Coffee and Tea have?
        // (unifying on Score.rating)
        _ <- Score.beverage_("Coffee" and "Tea").rating.get.map(_ ==> List(3))


        // Who rated both 2 AND 3?
        // (unifying on Person.nameL)
        // Using entity id againg to get uncoalesced strMap values
        _ <- Person.e.nameL.Likes.rating_(2 and 3).get.map(_.map(_._2) ==> List(
          Map("en" -> "Joe", "da" -> "Jonas"),
          Map("en" -> "Ben", "es" -> "Benito")
        ))

        // What ages have those who rated both 2 AND 3?
        // (unifying on Person.age)
        _ <- Person.age.Likes.rating_(2 and 3).get.map(_ ==> List(23, 25))

        // Which beverages are rated 2 AND 3?
        // (unifying on Likes.beverage)
        _ <- Score.rating_(2 and 3).beverage.get.map(_ ==> List("Coffee"))
      } yield ()
    }

    "AND, unify keyed attribute maps" - selfJoin { implicit conn =>
      for {
        _ <- data

        // age - name(in various languages) - beverage - rating
        _ <- m(Person.age.nameL.Likes * Score.beverage.rating) insert List(
          (23, Map("en" -> "Joe", "da" -> "Jonas"), List(("Coffee", 3), ("Cola", 2), ("Pepsi", 3))),
          (25, Map("en" -> "Ben", "es" -> "Benito"), List(("Coffee", 2), ("Tea", 3))),
          (23, Map("en" -> "Liz", "da" -> "Lis"), List(("Coffee", 1), ("Tea", 3), ("Pepsi", 1))))

        // Who likes both Coffee AND Tea?
        // Unifying on Person.nameLK("en")
        _ <- Person.nameLK("en").Likes.beverage_("Coffee" and "Tea").get.map(_ ==> List("Ben", "Liz"))

        // Who rated both 2 AND 3?
        // Unifying on Person.nameLK("en")
        _ <- Person.nameLK("en").Likes.rating_(2 and 3).get.map(_ ==> List("Ben", "Joe"))
      } yield ()
    }

    "Explicit self-join" - selfJoin { implicit conn =>
      for {
        _ <- data

        // All the examples above use the AND notation to construct
        // simple self-joins to Person. Any of them could be re-written to use
        // a more powerful and expressive Self-notation:

        _ <- Person.age_(23 and 25).Likes.beverage.get.map(_ ==> List("Coffee", "Tea"))
        // ..can be re-written to:
        _ <- Person.age_(23).Likes.beverage._Person.Self
          .age_(25).Likes.beverage_(unify).get.map(_ ==> List("Coffee", "Tea"))

        // Let's walk through that one...

        // First we ask for a tacit age of 23 being asserted with one person (entity).
        // After asking for the beverage value (Likes.beverage) of the first person we
        // "go back" with `_Person` to the initial namespace `Person` and then say that we
        // want to make a self-join with `Self` to start defining another person/entity.
        // We want the other person to be 25 years old.
        // When we define the beverage value for the other person we tell molecule
        // to "unify" that value with the equivalent beverage value of the
        // first person.

        // This second notation gives us freedom to fetch more values that
        // shouldn't be unified. Say for instance that we want to know the names
        // of 23-/25-year-olds sharing a beverage preference:

        _ <- Person.age_(23).name.a2.Likes.beverage.a1._Person.Self
          .age_(25).name.Likes.beverage_(unify).get.map(_ ==> List(
          ("Joe", "Coffee", "Ben"),
          ("Liz", "Coffee", "Ben"),
          ("Liz", "Tea", "Ben")
        ))
        // Now we also fetch the name of beverage which is not being unified between the two entities.

        // Let's add the ratings too
        _ <- Person.age_(23).name.a2.Likes.rating.beverage.a1._Person.Self
          .age_(25).name.Likes.beverage_(unify).rating.get.map(_ ==> List(
          ("Joe", 3, "Coffee", "Ben", 2),
          ("Liz", 1, "Coffee", "Ben", 2),
          ("Liz", 3, "Tea", "Ben", 3)
        ))

        // We can arrange the attributes in the previous molecule in other orders too:
        _ <- Person.age_(23).name.a2.Likes.rating.beverage.a1._Person.Self
          .age_(25).name.Likes.rating.beverage_(unify).get.map(_ ==> List(
          ("Joe", 3, "Coffee", "Ben", 2),
          ("Liz", 1, "Coffee", "Ben", 2),
          ("Liz", 3, "Tea", "Ben", 3)
        ))
        // or
        _ <- Person.age_(23).name.a2.Likes.beverage.a1.rating._Person.Self
          .age_(25).name.Likes.beverage_(unify).rating.get.map(_ ==> List(
          ("Joe", "Coffee", 3, "Ben", 2),
          ("Liz", "Coffee", 1, "Ben", 2),
          ("Liz", "Tea", 3, "Ben", 3)
        ))
        // or
        _ <- Person.age_(23).name.a2.Likes.beverage.a1.rating._Person.Self
          .age_(25).name.Likes.rating.beverage_(unify).get.map(_ ==> List(
          ("Joe", "Coffee", 3, "Ben", 2),
          ("Liz", "Coffee", 1, "Ben", 2),
          ("Liz", "Tea", 3, "Ben", 3)
        ))

        // Only higher rated beverages
        _ <- Person.age_(23).name.a2.Likes.rating.>(1).beverage.a1._Person.Self
          .age_(25).name.Likes.rating.>(1).beverage_(unify).get.map(_ ==> List(
          ("Joe", 3, "Coffee", "Ben", 2),
          ("Liz", 3, "Tea", "Ben", 3)
        ))

        // Only highest rated beverages
        _ <- Person.age_(23).name.Likes.rating(3).beverage._Person.Self
          .age_(25).name.Likes.rating(3).beverage_(unify).get.map(_ ==> List(
          ("Liz", 3, "Tea", "Ben", 3)
        ))

        // Common beverage of 23-year-old with low rating and 25-year-old with high rating
        _ <- Person.age_(23).name.Likes.rating(1).beverage._Person.Self
          .age_(25).name.Likes.rating(2).beverage_(unify).get.map(_ ==> List(
          ("Liz", 1, "Coffee", "Ben", 2)
        ))

        // Any 23- and 25-year-olds wanting to drink tea together?
        _ <- Person.age_(23).name.Likes.beverage_("Tea")._Person.Self
          .age_(25).name.Likes.beverage_("Tea").get.map(_ ==> List(("Liz", "Ben")))

        // Any 23-year old Tea drinker and a 25-year-old Coffee drinker?
        _ <- Person.age_(23).name.Likes.beverage_("Tea")._Person.Self
          .age_(25).name.Likes.beverage_("Coffee").get.map(_ ==> List(("Liz", "Ben")))

        // Any pair of young persons drinking respectively Tea and Coffee?
        _ <- Person.age_.<(24).name.a1.Likes.beverage_("Tea")._Person.Self
          .age_.<(24).name.a2.Likes.beverage_("Coffee").get.map(_ ==> List(
          ("Liz", "Joe"),
          ("Liz", "Liz")
        ))

        // Since Liz is under 24 and drinks both Tea and Coffee she
        // shows up as two persons (one drinking Tea, the other Coffee).
        // We can filter the result to only get different persons:
        _ <- Person.e.age_.<(24).name.Likes.beverage_("Tea")._Person.Self
          .e.age_.<(24).name.Likes.beverage_("Coffee").get.map(
          _.filter(r => r._1 != r._3).map(r => (r._2, r._4)) ==> List(
            ("Liz", "Joe")
          ))

        // Unifying attributes should be tacit
        // Grab the value from the first attribute that it unifies with (if needed)

        _ = expectCompileError("m(Person.age_(23).Likes.beverage._Person.Self.age_(25).Likes.beverage(unify))",
          "molecule.core.transform.exception.Dsl2ModelException: Can only unify on tacit attributes. Please add underscore to attribute: `beverage_(unify)`")
      } yield ()
    }

    "Multiple explicit self-joins" - selfJoin { implicit conn =>
      for {
        _ <- data
        _ <- Person.name_("Joe" and "Ben" and "Liz").Likes.beverage.get.map(_ ==> List("Coffee"))

        // Beverages liked by all 3 different people
        _ <- Person
          .name_("Joe").Likes.beverage._Person.Self
          .name_("Ben").Likes.beverage_(unify)._Person.Self
          .name_("Liz").Likes.beverage_(unify).get.map(_ ==> List("Coffee"))
      } yield ()
    }
  }
}