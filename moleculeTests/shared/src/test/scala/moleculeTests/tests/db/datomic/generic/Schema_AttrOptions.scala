package moleculeTests.tests.db.datomic.generic

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out3._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._
import scala.concurrent.Future


object Schema_AttrOptions extends AsyncTestSuite {

  // Differing counts and ids for different systems
  val attrCount = system match {
    case SystemPeer       => 63
    case SystemDevLocal   => 63
    case SystemPeerServer => 65
  }


  lazy val tests = Tests {

    "doc" - core { implicit conn =>
      for {
        // 2 core attributes are documented
        _ <- Schema.doc.get.map(_ ==> List(
          "Card one String attribute",
          "Card one Int attribute"
        ))

        // See what attributes is
        _ <- Schema.a.doc.get.map(_ ==> List(
          (":Ns/str", "Card one String attribute"),
          (":Ns/int", "Card one Int attribute")
        ))

        // Two distinct doc comments
        _ <- Schema.doc(count).get.map(_.head ==> 2)
        // Two doc comments (duplicate comments would aggregate with the id added)
        _ <- Schema.attrId_.doc(count).get.map(_.head ==> 2)

        // Filtering by a complete `doc_` is probably not that useful
        _ <- Schema.doc("Card one Int attribute").get.map(_ ==> List(
          "Card one Int attribute"))
        // .. likely the same for negation
        _ <- Schema.doc.not("Card one String attribute").get.map(_ ==> List(
          "Card one Int attribute"))

        // Docs only searchable with Peer (requires fulltext search)
        _ <- if (system == SystemPeer) {
          for {
            // Instead, use fulltext search for a whole word in doc texts
            _ <- Schema.doc.contains("Int").get.map(_ ==> List(
              "Card one Int attribute"
            ))
            res <- Schema.doc.contains("attribute").get.map(_ ==> List(
              "Card one String attribute",
              "Card one Int attribute"
            ))
            // Fulltext search for multiple words not allowed
            _ = expectCompileError("""m(Schema.doc.contains("Int", "String"))""",
              "molecule.datomic.base.transform.exception.Model2QueryException: " +
                "Fulltext search can only be performed with 1 search phrase.")
          } yield res
        } else Future.unit

        // Use tacit `doc_` to filter documented attributes
        // All attributes
        _ <- Schema.a.get.map(_.size ==> attrCount)
        // Documented attributes
        _ <- Schema.doc_.a.get.map(_.size ==> 2)

        // Filtering by a complete tacit `doc_` text is probably not that useful
        _ <- Schema.doc_("Card one Int attribute").a.get.map(_ ==> List(":Ns/int"))
        // .. likely the same for negation
        _ <- Schema.doc_.not("Card one Int attribute").a.get.map(_ ==> List(":Ns/str"))


        // Docs only searchable with Peer (requires fulltext search)
        _ <- if (system == SystemPeer) {
          for {
            // Tacit fulltext search in doc texts
            _ <- Schema.doc_.contains("Int").a.get.map(_ ==> List(":Ns/int"))
            res <- Schema.doc_.contains("one").a.get.map(_ ==> List(":Ns/int", ":Ns/str"))
          } yield res
        } else Future.unit

        // Get optional attribute doc text with `doc$`
        _ <- Schema.attr_("bool", "str").a.doc$.get.map(_ ==> List(
          (":Ns/str", Some("Card one String attribute")),
          (":Ns/bool", None)
        ))

        // Filter by applying optional attribute doc text string
        someDocText1 = Some("Card one String attribute")
        someDocText2 = None
        _ <- Schema.attr_("bool", "str").a.doc$(someDocText1).get.map(_ ==> List(
          (":Ns/str", Some("Card one String attribute"))
        ))
        _ <- Schema.attr_("bool", "str").a.doc$(someDocText2).get.map(_ ==> List(
          (":Ns/bool", None),
        ))
      } yield ()
    }


    "unique" - core { implicit conn =>
      for {
        // Unique options
        _ <- Schema.unique.get.map(_ ==> List("identity", "value"))

        // Two unique option types
        _ <- Schema.unique(count).get.map(_.head ==> 2)

        // Unique options
        _ <- Schema.a.unique.get.map(_ ==> List(
          (":Ref2/str2", "identity"),
          (":Ref2/int2", "value")
        ))

        _ <- Schema.a.unique("identity").get.map(_ ==> List((":Ref2/str2", "identity")))
        _ <- Schema.a.unique("value").get.map(_ ==> List((":Ref2/int2", "value")))

        _ <- Schema.a.unique.not("identity").get.map(_ ==> List((":Ref2/int2", "value")))
        _ <- Schema.a.unique.not("value").get.map(_ ==> List((":Ref2/str2", "identity")))

        // Filter attributes by tacit `unique_` option
        _ <- Schema.unique_.a.get.map(_ ==> List(":Ref2/int2", ":Ref2/str2"))

        _ <- Schema.unique_("identity").a.get.map(_ ==> List(":Ref2/str2"))
        _ <- Schema.unique_.not("value").a.get.map(_ ==> List(":Ref2/str2"))

        // Get optional attribute indexing status with `index$`
        _ <- Schema.attr_("str", "str2", "int2").a.a1.unique$.get.map(_ ==> List(
          (":Ns/str", None),
          (":Ref2/int2", Some("value")),
          (":Ref2/str2", Some("identity"))
        ))

        // Filter by applying optional attribute uniqueness status
        _ <- Schema.attr_("str", "str2", "int2").a.unique$(Some("identity")).get.map(_ ==> List(
          (":Ref2/str2", Some("identity"))
        ))
        _ <- Schema.attr_("str", "str2", "int2").a.unique$(Some("value")).get.map(_ ==> List(
          (":Ref2/int2", Some("value"))
        ))
        _ <- Schema.attr_("str", "str2", "int2").a.unique$(None).get.map(_ ==> List(
          (":Ns/str", None)
        ))

        // Number of non-unique attributes
        _ <- Schema.unique(count).get.map(_.head ==> 2)
        _ <- Schema.a.unique$(None).get.map(_.size ==> attrCount - 2)
      } yield ()
    }


    "isComponent" - core { implicit conn =>
      for {
        // Component status options - either true or non-asserted
        _ <- Schema.isComponent.get.map(_ ==> List(true)) // no false

        _ <- Schema.isComponent(count).get.map(_.head ==> 1) // only true

        // Component attributes
        _ <- Schema.a.isComponent.get.map(_ ==> List(
          (":Ns/refSub1", true),
          (":Ns/refsSub1", true),
          (":Ref1/refsSub2", true),
          (":Ref1/refSub2", true)
        ))

        _ <- Schema.a.isComponent(true).get.map(_.size ==> 4)
        // Option is either true or non-asserted (nil/None), never false
        _ <- Schema.a.isComponent(false).get.map(_.size ==> 0)

        _ <- Schema.a.isComponent.not(true).get.map(_.size ==> 0)
        _ <- Schema.a.isComponent.not(false).get.map(_.size ==> 4)

        // Filter attributes with tacit `isComponent_` option
        _ <- Schema.isComponent_.a.get.map(_ ==> List(
          ":Ns/refsSub1",
          ":Ref1/refSub2",
          ":Ref1/refsSub2",
          ":Ns/refSub1"
        ))
        _ <- Schema.isComponent_(true).a.get.map(_ ==> List(
          ":Ns/refsSub1",
          ":Ref1/refSub2",
          ":Ref1/refsSub2",
          ":Ns/refSub1"
        ))
        _ <- Schema.isComponent_.not(false).a.get.map(_ ==> List(
          ":Ns/refsSub1",
          ":Ref1/refSub2",
          ":Ref1/refsSub2",
          ":Ns/refSub1"
        ))

        // Get optional attribute component status with `isComponent$`
        _ <- Schema.attr_("bool", "refSub1").a.a1.isComponent$.get.map(_ ==> List(
          (":Ns/bool", None),
          (":Ns/refSub1", Some(true))
        ))

        // Filter by applying optional attribute component status
        some = Some(true)
        _ <- Schema.attr_("bool", "refSub1").a.isComponent$(some).get.map(_ ==> List(
          (":Ns/refSub1", Some(true))))

        _ <- Schema.attr_("bool", "refSub1").a.isComponent$(Some(true)).get.map(_ ==> List(
          (":Ns/refSub1", Some(true))))

        none = None
        _ <- Schema.attr_("bool", "refSub1").a.isComponent$(none).get.map(_ ==> List(
          (":Ns/bool", None)))

        _ <- Schema.attr_("bool", "refSub1").a.isComponent$(None).get.map(_ ==> List(
          (":Ns/bool", None)))

        // Number of non-component attributes
        _ <- Schema.a.isComponent$(None).get.map(_.size ==> attrCount - 4)
      } yield ()
    }


    "noHistory" - core { implicit conn =>
      for {
        // No-history status options - either true or non-asserted
        _ <- Schema.noHistory.get.map(_ ==> List(true)) // no false

        _ <- Schema.noHistory(count).get.map(_.head ==> 1) // only true

        // No-history attributes
        _ <- Schema.a.noHistory.get.map(_ ==> List(
          (":Ref2/ints2", true))
        )

        _ <- Schema.a.noHistory(true).get.map(_.size ==> 1)
        // Option is either true or non-asserted (nil/None), never false
        _ <- Schema.a.noHistory(false).get.map(_.size ==> 0)

        _ <- Schema.a.noHistory.not(true).get.map(_.size ==> 0)
        _ <- Schema.a.noHistory.not(false).get.map(_.size ==> 1)


        // Filter attributes with tacit `noHistory_` option
        _ <- Schema.noHistory_.a.get.map(_ ==> List(":Ref2/ints2"))
        _ <- Schema.noHistory_(true).a.get.map(_ ==> List(":Ref2/ints2"))
        _ <- Schema.noHistory_.not(false).a.get.map(_ ==> List(":Ref2/ints2"))


        // Get optional attribute no-history status with `noHistory$`
        _ <- Schema.attr_("bool", "ints2").a.a1.noHistory$.get.map(_ ==> List(
          (":Ns/bool", None),
          (":Ref2/ints2", Some(true))
        ))

        // Filter by applying optional attribute no-history status
        some = Some(true)
        _ <- Schema.attr_("bool", "ints2").a.noHistory$(some).get.map(_ ==> List(
          (":Ref2/ints2", Some(true))))

        _ <- Schema.attr_("bool", "ints2").a.noHistory$(Some(true)).get.map(_ ==> List(
          (":Ref2/ints2", Some(true))))

        none = None
        _ <- Schema.attr_("bool", "ints2").a.noHistory$(none).get.map(_ ==> List(
          (":Ns/bool", None)))

        _ <- Schema.attr_("bool", "ints2").a.noHistory$(None).get.map(_ ==> List(
          (":Ns/bool", None)))

        // Number of non-component attributes
        _ <- Schema.a.noHistory$(None).get.map(_.size ==> attrCount - 1)
      } yield ()
    }


    "index" - core { implicit conn =>
      if (system == SystemPeer) {
        for {
          // Index option only available in Peer

          // All attributes are indexed
          _ <- Schema.index.get.map(_ ==> List(true)) // no false
          _ <- Schema.a.index.get.map(_.size ==> attrCount)

          _ <- Schema.index(count).get.map(_.head ==> 1) // only true

          _ <- Schema.a.index(true).get.map(_.size ==> attrCount)
          _ <- Schema.a.index(false).get.map(_.size ==> 0)

          _ <- Schema.a.index.not(true).get.map(_.size ==> 0)
          _ <- Schema.a.index.not(false).get.map(_.size ==> attrCount)

          // Count attribute indexing statuses (only true)
          _ <- Schema.index.get.map(_.length ==> 1)


          // Using tacit `index_` is not that useful since all attributes are indexed by default
          _ <- Schema.a.get.map(_.size ==> attrCount)
          _ <- Schema.index_.a.get.map(_.size ==> attrCount)

          _ <- Schema.index_(true).a.get.map(_.size ==> attrCount)
          _ <- Schema.index_.not(false).a.get.map(_.size ==> attrCount)


          // Get optional attribute indexing status with `index$`
          _ <- Schema.attr_("bool", "str").a.a1.index$.get.map(_ ==> List(
            (":Ns/bool", Some(true)),
            (":Ns/str", Some(true))
          ))

          // Filter by applying optional attribute indexing status
          some = Some(true)
          _ <- Schema.attr_("bool", "str").a.index$(some).get.map(_ ==> List(
            (":Ns/bool", Some(true)),
            (":Ns/str", Some(true))
          ))
          _ <- Schema.attr_("bool", "str").a.index$(Some(true)).get.map(_ ==> List(
            (":Ns/bool", Some(true)),
            (":Ns/str", Some(true))
          ))

          none = None
          _ <- Schema.attr_("bool", "str").a.index$(none).get.map(_ ==> Nil)
          _ <- Schema.attr_("bool", "str").a.index$(None).get.map(_ ==> Nil)
        } yield ()
      }
    }


    "fulltext" - core { implicit conn =>
      // Fulltext option only available in Peer
      if (system == SystemPeer) {
        for {
          // Fulltext options
          _ <- Schema.fulltext.get.map(_ ==> List(true)) // no false

          _ <- Schema.fulltext(count).get.map(_.head ==> 1) // only true

          // Count attribute fulltext statuses (only true)
          _ <- Schema.fulltext.get.map(_.length ==> 1)

          // Attributes with fulltext search
          _ <- Schema.a.a1.fulltext.get.map(_ ==> List(
            (":Ns/str", true),
            (":Ns/strMap", true),
            (":Ns/strs", true),
            (":Ref1/str1", true),
            (":Ref2/str2", true)
          ))

          _ <- Schema.a.fulltext(true).get.map(_.size ==> 5)
          // Option is either true or non-asserted (nil/None), never false
          _ <- Schema.a.fulltext(false).get.map(_.size ==> 0)

          _ <- Schema.a.fulltext.not(true).get.map(_.size ==> 0)
          _ <- Schema.a.fulltext.not(false).get.map(_.size ==> 5)


          // Filter attributes with tacit `fulltext_` option
          _ <- Schema.fulltext_.a.a1.get.map(_ ==> List(
            ":Ns/str",
            ":Ns/strMap",
            ":Ns/strs",
            ":Ref1/str1",
            ":Ref2/str2"
          ))
          _ <- Schema.fulltext_(true).a.a1.get.map(_ ==> List(
            ":Ns/str",
            ":Ns/strMap",
            ":Ns/strs",
            ":Ref1/str1",
            ":Ref2/str2"
          ))
          _ <- Schema.fulltext_.not(false).a.a1.get.map(_ ==> List(
            ":Ns/str",
            ":Ns/strMap",
            ":Ns/strs",
            ":Ref1/str1",
            ":Ref2/str2"
          ))

          // Get optional attribute fulltext status with `fulltext$`
          _ <- Schema.attr_("bool", "str").a.a1.fulltext$.get.map(_ ==> List(
            (":Ns/bool", None),
            (":Ns/str", Some(true))
          ))

          // Filter by applying optional attribute fulltext search status
          some = Some(true)
          _ <- Schema.attr_("bool", "str").a.fulltext$(some).get.map(_ ==> List(
            (":Ns/str", Some(true))))
          _ <- Schema.attr_("bool", "str").a.fulltext$(Some(true)).get.map(_ ==> List(
            (":Ns/str", Some(true))))

          none = None
          _ <- Schema.attr_("bool", "str").a.fulltext$(none).get.map(_ ==> List(
            (":Ns/bool", None)))
          _ <- Schema.attr_("bool", "str").a.fulltext$(None).get.map(_ ==> List(
            (":Ns/bool", None)))

          // Number of attributes without fulltext search
          _ <- Schema.a.fulltext$(None).get.map(_.size ==> attrCount - 5)
        } yield ()
      }
    }
  }
}