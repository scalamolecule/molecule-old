package moleculeTests.tests.db.datomic.partitions

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.schemaDef.dsl.PartitionTest._
import utest._
import molecule.core.util.Executor._
import scala.language.reflectiveCalls


object PartitionTests extends AsyncTestSuite {

  lazy val tests = Tests {

    "Save resolves to correct partitions" - partition { implicit conn =>
      lit_Book.title("A good book").cat("good").Author.name("Marc").gender("male").inspectSave
      /*
        ## 1 ## output.Molecule.inspectSave
        ================================================================================================================
        Model(
          Atom("lit_Book", "title", "String", 1, Eq(Seq("A good book")), None, Seq(), Seq())
          Atom("lit_Book", "cat", "String", 1, Eq(Seq("good")), Some(":lit_Book.cat/"), Seq(), Seq())
          Bond("lit_Book", "author", "gen_Person", 1, Seq())
          Atom("gen_Person", "name", "String", 1, Eq(Seq("Marc")), None, Seq(), Seq())
          Atom("gen_Person", "gender", "String", 1, Eq(Seq("male")), Some(":gen_Person.gender/"), Seq(), Seq()))
        ----------------------------------------------------------------------------------------------------------------
        List(
          List(":db/add",     __tempId, ":lit_Book/title",            Values(Eq(Seq("A good book")),None), Card(1)),
          List(":db/add",     e       , ":lit_Book/cat",              Values(Eq(Seq("good")),Some(:lit_Book.cat/)), Card(1)),
          list(":db/add",     e       , ":lit_Book/author",           :gen_Person),
          List(":db/add",     v       , ":gen_Person/name",           Values(Eq(Seq("Marc")),None), Card(1)),
          List(":db/add",     e       , ":gen_Person/gender",         Values(Eq(Seq("male")),Some(:gen_Person.gender/)), Card(1)))
        ----------------------------------------------------------------------------------------------------------------
        list(
          list(":db/add",     #db/id[:lit -1000025], ":lit_Book/title",            A good book),
          list(":db/add",     #db/id[:lit -1000025], ":lit_Book/cat",              :lit_Book.cat/good),
          list(":db/add",     #db/id[:lit -1000025], ":lit_Book/author",           #db/id[:gen -1000026]),
          list(":db/add",     #db/id[:gen -1000026], ":gen_Person/name",           Marc),
          list(":db/add",     #db/id[:gen -1000026], ":gen_Person/gender",         :gen_Person.gender/male))
        ================================================================================================================
       */
    }

    "Nested 2 levels" - partition { implicit conn =>
      for {
        _ <- m(lit_Book.title.Reviewers.name.Professions * gen_Profession.name) insert List(("book", "Jan", List("Musician")))

        _ <- m(lit_Book.title.Reviewers.name.Professions * gen_Profession.name).get.map(_ ==> List(("book", "Jan", List("Musician"))))
        // Same as
        _ <- m(lit_Book.title.Reviewers.Professions * gen_Profession.name).get.map(_ ==> List(("book", List("Musician"))))
      } yield ()
    }

    "Nested 2 levels without intermediary attribute values" - partition { implicit conn =>
      for {
        _ <- m(lit_Book.title.Reviewers.Professions * gen_Profession.name) insert List(("book", List("Hacker", "Magician")))

        _ <- m(lit_Book.title.Reviewers * gen_Person.Professions.name).get.map(_.map(r => (r._1, r._2.sorted)) ==>
          List(("book", List("Hacker", "Magician"))))
        // Same as
        _ <- m(lit_Book.title.Reviewers.Professions * gen_Profession.name).get.map(_.map(r => (r._1, r._2.sorted)) ==>
          List(("book", List("Hacker", "Magician"))))
      } yield ()
    }

    "Nested 2 levels + many reference" - partition { implicit conn =>
      for {
        _ <- m(lit_Book.title.Reviewers * gen_Person.Professions.name) insert List(("book", List("Hacker", "Magician")))

        _ <- m(lit_Book.title.Reviewers * gen_Person.Professions.name).get.map(_ ==> List(("book", List("Hacker", "Magician"))))
        // Same as
        _ <- m(lit_Book.title.Reviewers.Professions * gen_Profession.name).get.map(_ ==> List(("book", List("Hacker", "Magician"))))
      } yield ()
    }

    "Back ref" - {

      "Back only" - partition { implicit conn =>
        for {
          _ <- lit_Book.title("A good book").cat("good").Author.name("Marc").save
          _ <- lit_Book.title.Author.name._lit_Book.cat.get.map(_.head ==> ("A good book", "Marc", "good"))
        } yield ()
      }

      "Adjacent" - partition { implicit conn =>
        for {
          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers.name) insert List(("book", "John", "Marc"))

          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers.name).get.map(_ ==> List(("book", "John", "Marc")))
          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers * gen_Person.name).get.map(_ ==> List(("book", "John", List("Marc"))))
        } yield ()
      }

      "Nested" - partition { implicit conn =>
        for {
          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers * gen_Person.name) insert List(("book", "John", List("Marc")))

          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers.name).get.map(_ ==> List(("book", "John", "Marc")))
          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers * gen_Person.name).get.map(_ ==> List(("book", "John", List("Marc"))))
        } yield ()
      }

      "Nested + adjacent" - partition { implicit conn =>
        for {
          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers * gen_Person.name.Professions.name) insert List(("book", "John", List(("Marc", "Musician"))))

          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers.name.Professions.name).get.map(_ ==> List(("book", "John", "Marc", "Musician")))
          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers * gen_Person.name.Professions.name).get.map(_ ==> List(("book", "John", List(("Marc", "Musician")))))
        } yield ()
      }

      "Nested + nested" - partition { implicit conn =>
        for {
          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers * (gen_Person.name.Professions * gen_Profession.name)) insert List(("book", "John", List(("Marc", List("Musician")))))

          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers.name.Professions.name).get.map(_ ==> List(("book", "John", "Marc", "Musician")))
          _ <- m(lit_Book.title.Author.name._lit_Book.Reviewers * (gen_Person.name.Professions * gen_Profession.name)).get.map(_ ==> List(("book", "John", List(("Marc", List("Musician"))))))
        } yield ()
      }
    }
  }
}