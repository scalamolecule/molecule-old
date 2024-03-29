package moleculeTests.tests.db.datomic.generic

import molecule.datomic.api.out3._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._


object Schema_Partition extends AsyncTestSuite {


  lazy val tests = Tests {

    "part" - partition { implicit conn =>
      for {
        _ <- Schema.part.get.map(_ ==> List("lit", "gen"))

        _ <- Schema.part(count).get.map(_.head ==> 2)

        _ <- Schema.part("gen").get.map(_ ==> List("gen"))
        _ <- Schema.part("gen", "lit").get.map(_ ==> List("lit", "gen"))

        _ <- Schema.part.not("gen").get.map(_ ==> List("lit"))
        _ <- Schema.part.not("gen", "lit").get.map(_ ==> Nil)

        // All Schema attributes can be compared. But maybe not that useful,
        // so this is only tested here:
        _ <- Schema.part.>("gen").get.map(_ ==> List("lit"))
        _ <- Schema.part.>=("gen").get.map(_ ==> List("lit", "gen"))
        _ <- Schema.part.<=("gen").get.map(_ ==> List("gen"))
        _ <- Schema.part.<("gen").get.map(_ ==> Nil)

        _ <- Schema.part.get.map(_.length ==> 2)

        // Since all attributes have an attribute name, a tacit `part_` makes no difference
        _ <- Schema.ns.a1.get.map(_ ==> List("Book", "Person", "Profession"))
        _ <- Schema.part_.ns.a1.get.map(_ ==> List("Book", "Person", "Profession"))

        // We can though filter by one or more tacit attribute names
        _ <- Schema.part_("gen").ns.a1.get.map(_ ==> List("Person", "Profession"))
        _ <- Schema.part_("gen").ns.a1.attr.a2.get.map(_ ==> List(
          ("Person", "gender"),
          ("Person", "name"),
          ("Person", "professions"),
          ("Profession", "name"),
        ))

        // Namespaces of partitions named "gen" or "lit"
        _ <- Schema.part_("gen", "lit").ns.a1.get.map(_ ==> List("Book", "Person", "Profession"))

        // Negate tacit partition
        _ <- Schema.part_.not("lit").ns.a1.get.map(_ ==> List("Person", "Profession"))
        _ <- Schema.part_.not("gen", "lit").ns.get.map(_ ==> Nil)
      } yield ()
    }


    "nsFull" - partition { implicit conn =>
      for {
        // Partition-prefixed namespaces
        _ <- Schema.nsFull.a1.get.map(_ ==> List("gen_Person", "gen_Profession", "lit_Book"))

        _ <- Schema.nsFull(count).get.map(_.head ==> 3)
        _ <- Schema.part_("gen").nsFull(count).get.map(_.head ==> 2)

        // Namespaces without partition prefix
        _ <- Schema.ns.a1.get.map(_ ==> List("Book", "Person", "Profession"))

        _ <- Schema.nsFull("gen_Profession").get.map(_ ==> List("gen_Profession"))
        _ <- Schema.nsFull("gen_Profession", "lit_Book").a1.get.map(_ ==> List("gen_Profession", "lit_Book"))

        _ <- Schema.nsFull.not("gen_Profession").a1.get.map(_ ==> List("gen_Person", "lit_Book"))
        _ <- Schema.nsFull.not("gen_Profession", "lit_Book").get.map(_ ==> List("gen_Person"))

        _ <- Schema.nsFull.get.map(_.length ==> 3)

        // Since all attributes have a namespace, a tacit `nsFull_` makes no difference
        _ <- Schema.a.get.map(_.size ==> 9)
        _ <- Schema.nsFull_.a.get.map(_.size ==> 9)

        // We can though filter by one or more tacit namespace names
        _ <- Schema.nsFull_("gen_Profession").attr.a1.get.map(_ ==> List("name"))
        _ <- Schema.nsFull_("gen_Person").attr.a1.get.map(_ ==> List("gender", "name", "professions"))

        // Attributes in namespace "Ref1" or "gen_Person"
        _ <- Schema.nsFull_("gen_Profession", "gen_Person").attr.a1.get.map(_ ==> List(
          // Note that duplicate attribute `name`s have coalesced in the result Set
          "gender", "name", "professions"
        ))

        // Negate tacit namespace name
        _ <- Schema.nsFull_.not("lit_Book").attr.a1.get.map(_ ==> List(
          "gender", "name", "professions"
        ))
        _ <- Schema.nsFull_.not("lit_Book", "gen_Person").attr.a1.get.map(_ ==> List(
          "name"
        ))

        // Enum
        _ <- Schema.part_("gen").ns_("Person").attr_("gender").enumm.get.map(_ ==> List("female", "male"))

        // All enums grouped by attribute
        _ <- Schema.a.enumm.get.map(_.groupBy(_._1).map(g => g._1 -> g._2.map(_._2).sorted) ==> Map(
          ":gen_Person/gender" -> List("female", "male"),
          ":lit_Book/cat" -> List("bad", "good")
        ))
      } yield ()
    }
  }
}