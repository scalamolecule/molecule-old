package molecule
package part
import java.util.UUID._

import datomic.Peer
import molecule._
import molecule.ast.model.{Bond, Eq, Atom, Model}
import molecule.part.dsl.partitionTest._
import molecule.part.schema.PartitionTestSchema
import molecule.util.dsl.coreTest.{Ref1, Ns}
import molecule.util.schema.CoreTestSchema
import molecule.util.{CoreSpec, CoreSetup, MoleculeSpec}
import org.specs2.specification.Scope
import scala.language.reflectiveCalls

class PartitionSetup extends Scope with DatomicFacade {
  implicit val conn = recreateDbFrom(PartitionTestSchema)
}

class Partition extends MoleculeSpec {

  "Insert resolves to correct partitions" in new PartitionSetup {
    testInsertMolecule(
      lit_Book.title("A good book").cat("good").Author.name("Marc").gender("male")
    ) -->
      Model(List(
        Atom("lit_Book", "title", "String", 1, Eq(List("A good book")), None, List()),
        Atom("lit_Book", "cat", "String", 1, Eq(List("good")), Some(":lit_Book.cat/"), List()),
        Bond("lit_Book", "author", "gen_Person", 1),
        Atom("gen_Person", "name", "String", 1, Eq(List("Marc")), None, List()),
        Atom("gen_Person", "gender", "String", 1, Eq(List("male")), Some(":gen_Person.gender/"), List()))) -->
      //           action          temp id             attribute            value
      """List(
        |  List(  :db/add,   #db/id[:lit -1000001],   :lit_Book/title   ,   A good book              )
        |  List(  :db/add,   #db/id[:lit -1000001],   :lit_Book/cat     ,   :lit_Book.cat/good       )
        |  List(  :db/add,   #db/id[:lit -1000001],   :lit_Book/author  ,   #db/id[:gen -1000002]    )
        |  List(  :db/add,   #db/id[:gen -1000002],   :gen_Person/name  ,   Marc                     )
        |  List(  :db/add,   #db/id[:gen -1000002],   :gen_Person/gender,   :gen_Person.gender/male  )
        |)""".stripMargin
  }


  "Nested 2 levels" in new PartitionSetup {
    m(lit_Book.title.Reviewers.name.Professions * gen_Profession.name) insert List(("book", "Jan", List("Musician")))

    m(lit_Book.title.Reviewers.name.Professions * gen_Profession.name).get === List(("book", "Jan", List("Musician")))
    m(lit_Book.title.Reviewers.Professions * gen_Profession.name).get === List(("book", List("Musician")))
  }

  "Nested 2 levels missing middle attribute values" in new PartitionSetup {
    (m(lit_Book.title.Reviewers.Professions * gen_Profession.name).insert must throwA[RuntimeException]).message === "Got the exception java.lang.RuntimeException: " +
      "[output.Molecule:modelCheck (5)] Namespace `Reviewers` in insert molecule has no mandatory attributes. Please add at least one."

    (m(lit_Book.title.Reviewers * gen_Person.Professions.name).insert must throwA[RuntimeException]).message === "Got the exception java.lang.RuntimeException: " +
      "[output.Molecule:modelCheck (2)] Namespace `gen_Person` in insert molecule has no mandatory attributes. Please add at least one."
  }

  "No transitives in inserts" in new PartitionSetup {
    // Todo: more transitive examples in own file
    (m(lit_Book.title.Author.name.name).insert must throwA[RuntimeException]).message === "Got the exception java.lang.RuntimeException: " +
      "[output.Molecule:modelCheck (6)] Can't insert transitive attribute values (repeated attributes)."
  }


  "Back ref" >> {

    "Back only" in new PartitionSetup {
      lit_Book.title("A good book").cat("good").Author.name("Marc").add
      lit_Book.title.Author.name._Book.cat.one ===("A good book", "Marc", "good")
    }

    "Adjacent" in new PartitionSetup {
      m(lit_Book.title.Author.name._Book.Reviewers.name) insert List(("book", "John", "Marc"))

      m(lit_Book.title.Author.name._Book.Reviewers.name).get === List(("book", "John", "Marc"))
      m(lit_Book.title.Author.name._Book.Reviewers * gen_Person.name).get === List(("book", "John", List("Marc")))
    }

    "Nested" in new PartitionSetup {
      m(lit_Book.title.Author.name._Book.Reviewers * gen_Person.name) insert List(("book", "John", List("Marc")))

      m(lit_Book.title.Author.name._Book.Reviewers.name).get === List(("book", "John", "Marc"))
      m(lit_Book.title.Author.name._Book.Reviewers * gen_Person.name).get === List(("book", "John", List("Marc")))
    }

    "Nested + adjacent" in new PartitionSetup {
      m(lit_Book.title.Author.name._Book.Reviewers * gen_Person.name.Professions.name) insert List(("book", "John", List(("Marc", "Musician"))))

      m(lit_Book.title.Author.name._Book.Reviewers.name.Professions.name).get === List(("book", "John", "Marc", "Musician"))
      m(lit_Book.title.Author.name._Book.Reviewers * gen_Person.name.Professions.name).get === List(("book", "John", List(("Marc", "Musician"))))
    }

    "Nested + nested" in new PartitionSetup {
      m(lit_Book.title.Author.name._Book.Reviewers * (gen_Person.name.Professions * gen_Profession.name)) insert List(("book", "John", List(("Marc", List("Musician")))))

      m(lit_Book.title.Author.name._Book.Reviewers.name.Professions.name).get === List(("book", "John", "Marc", "Musician"))
      m(lit_Book.title.Author.name._Book.Reviewers * (gen_Person.name.Professions * gen_Profession.name)).get === List(("book", "John", List(("Marc", List("Musician")))))
    }
  }
}