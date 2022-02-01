package moleculeTests.tests.core.pagination

import molecule.core.util.Executor._
import molecule.datomic.api.in1_out7._
import molecule.datomic.base.util.SystemPeerServer
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.Future

/*
To relate the results in this test, you might want to open the following two schema definitions:

moleculeTests.dataModels.core.base.dataModel.CoreTestDataModel
moleculeTests.dataModels.core.schemaDef.dataModel.PartitionTestDataModel
*/
object SortSchemaAttrs extends AsyncTestSuite {

  lazy val tests = Tests {

    "id" - core { implicit conn =>
      for {
        // Ordering by id shows the order of attributes defined in the Data Model
        _ <- Schema.id.a1.a.get(5).map(_ ==> List(
          (72, ":Ns/str"),
          (73, ":Ns/int"),
          (74, ":Ns/long"),
          (75, ":Ns/double"),
          (76, ":Ns/bool"),
        ))
        _ <- Schema.id.d1.a.get(5).map(_ ==> List(
          (140, ":Ref4/ints4"),
          (139, ":Ref4/strs4"),
          (138, ":Ref4/enum4"),
          (137, ":Ref4/int4"),
          (136, ":Ref4/str4"),
        ))

        // Sort by count of attribute id's in each namespace
        _ <- Schema.ns.a2.id(count).d1.get.map(_ ==> List(
          ("Ns", 38),
          ("Ref1", 12),
          ("Ref2", 7),
          ("Ref3", 7),
          ("Ref4", 5),
        ))
        _ <- Schema.ns.a2.id(count).a1.get.map(_ ==> List(
          ("Ref4", 5),
          ("Ref2", 7),
          ("Ref3", 7),
          ("Ref1", 12),
          ("Ns", 38),
        ))
      } yield ()
    }


    "a" - core { implicit conn =>
      for {
        _ <- Schema.ns_("Ref1").a.a1.get(3).map(_ ==> List(
          ":Ref1/enum1",
          ":Ref1/enums1",
          ":Ref1/int1",
        ))
        _ <- Schema.ns_("Ref1").a.d1.get(3).map(_ ==> List(
          ":Ref1/strs1",
          ":Ref1/str1",
          ":Ref1/refsSub2",
        ))

        // Sort by count of attributes in each namespace
        _ <- Schema.ns.a2.a(count).a1.get.map(_ ==> List(
          ("Ref4", 5),
          ("Ref2", 7),
          ("Ref3", 7),
          ("Ref1", 12),
          ("Ns", 38),
        ))
        _ <- Schema.ns.a2.a(count).d1.get.map(_ ==> List(
          ("Ns", 38),
          ("Ref1", 12),
          ("Ref2", 7),
          ("Ref3", 7),
          ("Ref4", 5),
        ))
      } yield ()
    }


    "part (default when not defined)" - core { implicit conn =>
      for {
        // Sorting a single default partition name not so useful
        _ <- Schema.part.get.map(_ ==> List("db.part/user"))

        // Sorting a single count of partition(s) not so useful
        _ <- Schema.part(count).a1.get.map(_ ==> List(1))
      } yield ()
    }

    "part (when defined)" - partition { implicit conn =>
      for {
        _ <- Schema.part.a1.get.map(_ ==> List("gen", "lit"))
        _ <- Schema.part.d1.get.map(_ ==> List("lit", "gen"))

        // Sorting a single count of partitions not so useful
        _ <- Schema.part(count).a1.get.map(_ ==> List(2))
      } yield ()
    }


    "nsFull" - partition { implicit conn =>
      for {
        _ <- Schema.nsFull.a1.get.map(_ ==> List("gen_Person", "gen_Profession", "lit_Book"))
        _ <- Schema.nsFull.d1.get.map(_ ==> List("lit_Book", "gen_Profession", "gen_Person"))

        // Sort by count of namespaces in each partition
        _ <- Schema.part.nsFull(count).a1.get.map(_ ==> List(
          ("lit", 1),
          ("gen", 2),
        ))
        _ <- Schema.part.nsFull(count).d1.get.map(_ ==> List(
          ("gen", 2),
          ("lit", 1),
        ))
      } yield ()
    }

    "ns" - partition { implicit conn =>
      for {
        _ <- Schema.ns.a1.get.map(_ ==> List("Book", "Person", "Profession"))
        _ <- Schema.ns.d1.get.map(_ ==> List("Profession", "Person", "Book"))

        // Sort by count of namespaces in each partition
        _ <- Schema.part.ns(count).a1.get.map(_ ==> List(
          ("lit", 1),
          ("gen", 2),
        ))
        _ <- Schema.part.ns(count).d1.get.map(_ ==> List(
          ("gen", 2),
          ("lit", 1),
        ))
      } yield ()
    }


    "attr" - core { implicit conn =>
      for {
        _ <- Schema.ns_("Ref1").attr.a1.get(3).map(_ ==> List(
          "enum1",
          "enums1",
          "int1"
        ))
        _ <- Schema.ns_("Ref1").attr.d1.get(3).map(_ ==> List(
          "strs1",
          "str1",
          "refsSub2",
        ))

        // Sort by count of attributes in each namespace
        _ <- Schema.ns.a2.attr(count).d1.get.map(_ ==> List(
          ("Ns", 38),
          ("Ref1", 12),
          ("Ref2", 7),
          ("Ref3", 7),
          ("Ref4", 5),
        ))
        _ <- Schema.ns.a2.attr(count).a1.get.map(_ ==> List(
          ("Ref4", 5),
          ("Ref2", 7),
          ("Ref3", 7),
          ("Ref1", 12),
          ("Ns", 38),
        ))
      } yield ()
    }


    "enum" - core { implicit conn =>
      for {
        _ <- Schema.ns_("Ref2").attr.enumm.a1.get.map(_ ==> List(
          ("enum2", "enum20"),
          ("enum2", "enum21"),
          ("enum2", "enum22"),
        ))
        _ <- Schema.ns_("Ref2").attr.enumm.d1.get.map(_ ==> List(
          ("enum2", "enum22"),
          ("enum2", "enum21"),
          ("enum2", "enum20"),
        ))

        _ <- Schema.a.a2.enumm(count).d1.get.map(_ ==> List(
          (":Ns/enumm", 10),
          (":Ns/enums", 10),
          (":Ref1/enum1", 3),
          (":Ref1/enums1", 3),
          (":Ref2/enum2", 3),
          (":Ref3/enum3", 3),
          (":Ref4/enum4", 3),
        ))
        _ <- Schema.a.a2.enumm(count).a1.get.map(_ ==> List(
          (":Ref1/enum1", 3),
          (":Ref1/enums1", 3),
          (":Ref2/enum2", 3),
          (":Ref3/enum3", 3),
          (":Ref4/enum4", 3),
          (":Ns/enumm", 10),
          (":Ns/enums", 10),
        ))
      } yield ()
    }


    "tpe" - core { implicit conn =>
      for {
        _ <- Schema.valueType.a1.get.map(_ ==> List(
          "bigdec",
          "bigint",
          "boolean",
          "double",
          "instant",
          "long",
          "ref",
          "string",
          "uri",
          "uuid",
        ))
        _ <- Schema.valueType.d1.get.map(_ ==> List(
          "uuid",
          "uri",
          "string",
          "ref",
          "long",
          "instant",
          "double",
          "boolean",
          "bigint",
          "bigdec",
        ))

        // Sort by count of types used in each namespace
        _ <- Schema.ns.a2.valueType(count).a1.get.map(_ ==> List(
          ("Ref1", 3),
          ("Ref2", 3),
          ("Ref3", 3),
          ("Ref4", 3),
          ("Ns", 10),
        ))
        _ <- Schema.ns.a2.valueType(count).d1.get.map(_ ==> List(
          ("Ns", 10),
          ("Ref1", 3),
          ("Ref2", 3),
          ("Ref3", 3),
          ("Ref4", 3),
        ))
      } yield ()
    }


    "card" - partition { implicit conn =>
      for {
        _ <- Schema.cardinality.a1.get.map(_ ==> List("many", "one"))
        _ <- Schema.cardinality.d1.get.map(_ ==> List("one", "many"))

        // Sort by count of cardinalities used in each namespace
        _ <- Schema.nsFull.a2.cardinality(count).a1.get.map(_ ==> List(
          ("gen_Profession", 1),
          ("gen_Person", 2),
          ("lit_Book", 2),
        ))
        _ <- Schema.nsFull.a2.cardinality(count).d1.get.map(_ ==> List(
          ("gen_Person", 2),
          ("lit_Book", 2),
          ("gen_Profession", 1),
        ))
      } yield ()
    }



    // Schema attribute options --------------------------------------------

    "doc" - core { implicit conn =>
      for {
        // Mandatory
        _ <- Schema.a.doc.a1.get.map(_ ==> List(
          (":Ns/int", "Card one Int attribute"),
          (":Ns/str", "Card one String attribute"),
        ))
        _ <- Schema.a.doc.d1.get.map(_ ==> List(
          (":Ns/str", "Card one String attribute"),
          (":Ns/int", "Card one Int attribute"),
        ))

        // Optional
        _ <- Schema.a.a2.doc$.d1.get(3).map(_ ==> List(
          (":Ns/str", Some("Card one String attribute")),
          (":Ns/int", Some("Card one Int attribute")),
          (":Ns/bigDec", None),
        ))
        _ <- Schema.a.d2.doc$.a1.get.map(_.takeRight(3) ==> List(
          (":Ns/bigDec", None),
          (":Ns/int", Some("Card one Int attribute")),
          (":Ns/str", Some("Card one String attribute")),
        ))

        // Count of attribute doc descriptions
        _ <- Schema.ns.doc(count).a1.get.map(_ ==> List(("Ns", 2)))
        _ <- Schema.ns.doc(count).d1.get.map(_ ==> List(("Ns", 2)))
      } yield ()
    }


    "unique" - core { implicit conn =>
      for {
        // Mandatory
        _ <- Schema.ns_("Ref2").cardinality_("one").a.a2.unique.a1.get.map(_ ==> List(
          (":Ref2/str2", "identity"),
          (":Ref2/int2", "value"),
        ))
        _ <- Schema.ns_("Ref2").cardinality_("one").a.a2.unique.d1.get.map(_ ==> List(
          (":Ref2/int2", "value"),
          (":Ref2/str2", "identity"),
        ))

        // Optional
        _ <- Schema.ns_("Ref2").cardinality_("one").a.a2.unique$.a1.get.map(_ ==> List(
          (":Ref2/enum2", None),
          (":Ref2/ref3", None),
          (":Ref2/str2", Some("identity")),
          (":Ref2/int2", Some("value")),
        ))
        _ <- Schema.ns_("Ref2").cardinality_("one").a.a2.unique$.d1.get.map(_ ==> List(
          (":Ref2/int2", Some("value")),
          (":Ref2/str2", Some("identity")),
          (":Ref2/enum2", None),
          (":Ref2/ref3", None),
        ))

        // Count of unique options (two values possible)
        _ <- Schema.ns.unique(count).a1.get.map(_ ==> List(("Ref2", 2)))
        _ <- Schema.ns.unique(count).d2.get.map(_ ==> List(("Ref2", 2)))
      } yield ()
    }


    "isComponent" - core { implicit conn =>
      for {
        // Mandatory (not much sense in sorting by one value: true)
        _ <- Schema.ns_("Ref1").cardinality_("one").a.a2.isComponent.a1.get.map(_ ==> List((":Ref1/refSub2", true)))
        _ <- Schema.ns_("Ref1").cardinality_("one").a.a2.isComponent.d1.get.map(_ ==> List((":Ref1/refSub2", true)))

        // Optional
        _ <- Schema.ns_("Ref1").cardinality_("one").a.a2.isComponent$.a1.get.map(_ ==> List(
          (":Ref1/enum1", None),
          (":Ref1/int1", None),
          (":Ref1/ref2", None),
          (":Ref1/str1", None),
          (":Ref1/refSub2", Some(true)),
        ))
        _ <- Schema.ns_("Ref1").cardinality_("one").a.a2.isComponent$.d1.get.map(_ ==> List(
          (":Ref1/refSub2", Some(true)),
          (":Ref1/enum1", None),
          (":Ref1/int1", None),
          (":Ref1/ref2", None),
          (":Ref1/str1", None),
        ))

        // Count of isComponent option
        _ <- Schema.ns.a2.isComponent(count).a1.get.map(_ ==> List(
          ("Ns", 1),
          ("Ref1", 1),
        ))
        _ <- Schema.ns.d2.isComponent(count).d1.get.map(_ ==> List(
          ("Ref1", 1),
          ("Ns", 1),
        ))

        // Count of component attributes in each namespace
        _ <- Schema.ns.a2.isComponent_.a(count).d1.get.map(_ ==> List(
          ("Ns", 2),
          ("Ref1", 2),
        ))
      } yield ()
    }


    "noHistory" - core { implicit conn =>
      for {
        // Mandatory (not much sense in sorting by one value)
        _ <- Schema.ns_("Ref2").cardinality_("many").a.a2.noHistory.a1.get.map(_ ==> List(
          (":Ref2/ints2", true),
        ))

        // Optional
        _ <- Schema.ns_("Ref2").cardinality_("many").a.a2.noHistory$.a1.get.map(_ ==> List(
          (":Ref2/refs3", None),
          (":Ref2/strs2", None),
          (":Ref2/ints2", Some(true)),
        ))
        _ <- Schema.ns_("Ref2").cardinality_("many").a.a2.noHistory$.d1.get.map(_ ==> List(
          (":Ref2/ints2", Some(true)),
          (":Ref2/refs3", None),
          (":Ref2/strs2", None),
        ))

        // Count of noHistory option
        _ <- Schema.ns.a2.noHistory(count).a1.get.map(_ ==> List(("Ref2", 1)))
        _ <- Schema.ns.d2.noHistory(count).d1.get.map(_ ==> List(("Ref2", 1)))

        // Count of no-history attributes in each namespace
        _ <- Schema.ns.a2.noHistory_.a(count).d1.get.map(_ ==> List(("Ref2", 1)))
      } yield ()
    }


    "index" - core { implicit conn =>
      for {
        // Mandatory
        _ <- Schema.a.a2.index.a1.get(3).map(_ ==> (
          if (isJsPlatform) {
            // No attributes indexed
            Nil
          } else {
            // All attributes indexed (not much sense in sorting by one value)
            List(
              (":Ns/bigDec", true),
              (":Ns/bigDecMap", true),
              (":Ns/bigDecs", true),
            )
          })
        )

        // Optional
        _ <- Schema.a.index$.a1.get(3).map(_ ==> (
          if (isJsPlatform) {
            // No attributes indexed
            List(
              (":Ns/str", None),
              (":Ns/int", None),
              (":Ns/long", None),
            )
          } else {
            // All attributes indexed
            List(
              (":Ns/str", Some(true)),
              (":Ns/int", Some(true)),
              (":Ns/long", Some(true)),
            )
          })
        )

        // Count of index options
        _ <- Schema.ns.a2.index(count).a1.get.map(_ ==> (
          if (isJsPlatform) {
            // No attributes indexed
            Nil
          } else {
            // All attributes indexed
            List(
              ("Ns", 1),
              ("Ref1", 1),
              ("Ref2", 1),
              ("Ref3", 1),
              ("Ref4", 1),
            )
          })
        )
        _ <- Schema.ns.d2.index(count).d1.get.map(_ ==> (
          if (isJsPlatform) {
            // No attributes indexed
            Nil
          } else {
            // All attributes indexed
            List(
              ("Ref4", 1),
              ("Ref3", 1),
              ("Ref2", 1),
              ("Ref1", 1),
              ("Ns", 1),
            )
          })
        )

        // Count of indexed attributes in each namespace
        _ <- Schema.ns.a2.index_.a(count).d1.get.map(_ ==> (
          if (isJsPlatform) {
            // No attributes indexed
            Nil
          } else {
            // All attributes indexed
            List(
              ("Ns", 38),
              ("Ref1", 12),
              ("Ref2", 7),
              ("Ref3", 7),
              ("Ref4", 5),
            )
          })
        )
      } yield ()
    }


    "fulltext" - core { implicit conn =>
      for {
        // Mandatory (not much sense in sorting by one possible value: true)
        _ <- Schema.ns_("Ref2").cardinality_("one").a.a2.fulltext.a1.get.map(_ ==> List((":Ref2/str2", true)))
        _ <- Schema.ns_("Ref2").cardinality_("one").a.a2.fulltext.d1.get.map(_ ==> List((":Ref2/str2", true)))

        // Optional
        _ <- Schema.ns_("Ref2").cardinality_("one").a.a2.fulltext$.a1.get.map(_ ==> List(
          (":Ref2/enum2", None),
          (":Ref2/int2", None),
          (":Ref2/ref3", None),
          (":Ref2/str2", Some(true)),
        ))
        _ <- Schema.ns_("Ref2").cardinality_("one").a.a2.fulltext$.d1.get.map(_ ==> List(
          (":Ref2/str2", Some(true)),
          (":Ref2/enum2", None),
          (":Ref2/int2", None),
          (":Ref2/ref3", None),
        ))

        // Count of fulltext option
        _ <- Schema.ns.a2.fulltext(count).a1.get.map(_ ==> List(
          ("Ns", 1),
          ("Ref1", 1),
          ("Ref2", 1),
        ))
        _ <- Schema.ns.d2.fulltext(count).d1.get.map(_ ==> List(
          ("Ref2", 1),
          ("Ref1", 1),
          ("Ns", 1),
        ))

        // Count of how many attributes in each namespace that has the fulltext option set
        _ <- Schema.ns.a2.fulltext_.a(count).d1.get.map(_ ==> List(
          ("Ns", 3),
          ("Ref1", 1),
          ("Ref2", 1),
        ))
      } yield ()
    }


    // Time coordinates of attribute installation -----------------------------------------------

    "t, tx, txInstant" - core { implicit futConn =>
      // Peer and dev-local schema transaction was last transaction
      if (system != SystemPeerServer) {
        for {
          conn <- futConn
          db <- conn.db
          basisT <- db.basisT

          // Fetch schema transaction time coordinates
          (t1, tx1, d1) <- Log(Some(basisT)).t.tx.txInstant.get.map(_.head)

          // Schema alteration
          (t2, tx2, d2) <- conn.transact(
            """[
              |  {:db/ident         :Ns/xx
              |   :db/valueType     :db.type/long
              |   :db/cardinality   :db.cardinality/one}
              |]""".stripMargin
          ).map(r => (r.t, r.tx, r.txInstant))

          // Attribute installation transactions
          _ <- Schema.t.a1.get.map(_ ==> List(t1, t2))
          _ <- Schema.t.d1.get.map(_ ==> List(t2, t1))
          _ <- Schema.tx.a1.get.map(_ ==> List(tx1, tx2))
          _ <- Schema.tx.d1.get.map(_ ==> List(tx2, tx1))
          _ <- Schema.txInstant.a1.get.map(_ ==> List(d1, d2))
          _ <- Schema.txInstant.d1.get.map(_ ==> List(d2, d1))


          // How many times have attributes been installed? (not much sense in sorting by one count value)
          _ <- Schema.t(count).a1.get.map(_.head ==> 2)
          _ <- Schema.tx(count).a1.get.map(_.head ==> 2)
          _ <- Schema.txInstant(count).a1.get.map(_.head ==> 2)
        } yield ()
      } else Future.unit
    }
  }
}
