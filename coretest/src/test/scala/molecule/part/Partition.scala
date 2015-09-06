package molecule.part
import java.util.UUID._

import datomic.Peer
import molecule.ast.model.{Bond, Eq, Atom, Model}
import molecule.part.dsl.partitionTest._
import molecule.part.schema.PartitionTestSchema
import molecule.util.MoleculeSpec
import scala.language.reflectiveCalls

class Partition extends MoleculeSpec {

  "Insert resolves to correct partitions" >> {

    implicit val conn = load(PartitionTestSchema)

    testInsertMolecule(
      lit_Book.title("A good book").cat("good").Author.name("Marc").gender("male")
    ) -->
      Model(List(
        Atom("lit_Book", "title", "String", 1, Eq(List("A good book")), None, List()),
        Atom("lit_Book", "cat", "String", 1, Eq(List("good")), Some(":lit_Book.cat/"), List()),
        Bond("lit_Book", "author", "gen_Person"),
        Atom("gen_Person", "name", "String", 1, Eq(List("Marc")), None, List()),
        Atom("gen_Person", "gender", "String", 1, Eq(List("male")), Some(":gen_Person.gender/"), List()))) -->
      //            action          temp id             attribute            value
      """List(
        |  List(  :db/add,   #db/id[:lit -1000001],   :lit_Book/title   ,   A good book              )
        |  List(  :db/add,   #db/id[:lit -1000001],   :lit_Book/cat     ,   :lit_Book.cat/good       )
        |  List(  :db/add,   #db/id[:lit -1000001],   :lit_Book/author  ,   #db/id[:gen -1000002]    )
        |  List(  :db/add,   #db/id[:gen -1000002],   :gen_Person/name  ,   Marc                     )
        |  List(  :db/add,   #db/id[:gen -1000002],   :gen_Person/gender,   :gen_Person.gender/male  )
        |)""".stripMargin

    //    ok
  }
}