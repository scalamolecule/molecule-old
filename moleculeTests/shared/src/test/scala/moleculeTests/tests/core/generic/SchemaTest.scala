package moleculeTests.tests.core.generic

import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out3._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object SchemaTest extends AsyncTestSuite {

  // Differing counts and ids for different systems
  val List(attrCount, a1, a2, a3, card1count, card2count) = system match {
    case SystemPeer       => List(68, 106, 108, 109, 30, 38)
    case SystemDevLocal   => List(71, 105, 107, 108, 31, 40)
    case SystemPeerServer => List(71, 104, 106, 107, 31, 40)
  }

  lazy val tests = Tests {

    "Partition schema values" - {

      "part" - partition { implicit conn =>
        for {
          _ <- Schema.part.get === List("lit", "gen")

          _ <- Schema.part("gen").get === List("gen")
          _ <- Schema.part("gen", "lit").get === List("lit", "gen")

          _ <- Schema.part.not("gen").get === List("lit")
          _ <- Schema.part.not("gen", "lit").get === Nil

          // All Schema attributes can be compared. But maybe not that useful,
          // so this is only tested here:
          _ <- Schema.part.>("gen").get === List("lit")
          _ <- Schema.part.>=("gen").get === List("lit", "gen")
          _ <- Schema.part.<=("gen").get === List("gen")
          _ <- Schema.part.<("gen").get === Nil

          _ <- Schema.part.get.map(_.length ==> 2)

          // Since all attributes have an attribute name, a tacit `part_` makes no difference
          _ <- Schema.ns.get.map(_.sorted ==> List("Book", "Person", "Profession"))
          _ <- Schema.part_.ns.get.map(_.sorted ==> List("Book", "Person", "Profession"))

          // We can though filter by one or more tacit attribute names
          _ <- Schema.part_("gen").ns.get.map(_.sorted ==> List("Person", "Profession"))
          _ <- Schema.part_("gen").ns.attr.get.map(_.sorted ==> List(
            ("Person", "gender"),
            ("Person", "name"),
            ("Person", "professions"),
            ("Profession", "name"),
          ))

          // Namespaces of partitions named "gen" or "lit"
          _ <- Schema.part_("gen", "lit").ns.get.map(_.sorted ==> List("Book", "Person", "Profession"))

          // Negate tacit partition
          _ <- Schema.part_.not("lit").ns.get.map(_.sorted ==> List("Person", "Profession"))
          _ <- Schema.part_.not("gen", "lit").ns.get === Nil
        } yield ()
      }


      "nsFull" - partition { implicit conn =>
        for {
          // Partition-prefixed namespaces
          _ <- Schema.nsFull.get.map(_.sorted ==> List("gen_Person", "gen_Profession", "lit_Book"))
          // Namespaces without partition prefix
          _ <- Schema.ns.get.map(_.sorted ==> List("Book", "Person", "Profession"))

          _ <- Schema.nsFull("gen_Profession").get === List("gen_Profession")
          _ <- Schema.nsFull("gen_Profession", "lit_Book").get.map(_.sorted ==> List("gen_Profession", "lit_Book"))

          _ <- Schema.nsFull.not("gen_Profession").get.map(_.sorted ==> List("gen_Person", "lit_Book"))
          _ <- Schema.nsFull.not("gen_Profession", "lit_Book").get === List("gen_Person")

          _ <- Schema.nsFull.get.map(_.length ==> 3)

          // Since all attributes have a namespace, a tacit `nsFull_` makes no difference
          _ <- Schema.a.get.map(_.size ==> 9)
          _ <- Schema.nsFull_.a.get.map(_.size ==> 9)

          // We can though filter by one or more tacit namespace names
          _ <- Schema.nsFull_("gen_Profession").attr.get.map(_.sorted ==> List("name"))
          _ <- Schema.nsFull_("gen_Person").attr.get.map(_.sorted.sorted ==> List("gender", "name", "professions"))

          // Attributes in namespace "Ref1" or "gen_Person"
          _ <- Schema.nsFull_("gen_Profession", "gen_Person").attr.get.map(_.sorted ==> List(
            // Note that duplicate attribute `name`s have coalesced in the result Set
            "gender", "name", "professions"
          ))

          // Negate tacit namespace name
          _ <- Schema.nsFull_.not("lit_Book").attr.get.map(_.sorted ==> List(
            "gender", "name", "professions"
          ))
          _ <- Schema.nsFull_.not("lit_Book", "gen_Person").attr.get.map(_.sorted ==> List(
            "name"
          ))

          // Enum
          _ <- Schema.part_("gen").ns_("Person").attr_("gender").enum.get === List("female", "male")

          // All enums grouped by attribute
          _ <- Schema.a.enum.get.map(_.groupBy(_._1).map(g => g._1 -> g._2.map(_._2).sorted) ==> Map(
            ":gen_Person/gender" -> List("female", "male"),
            ":lit_Book/cat" -> List("bad", "good")
          ))
        } yield ()
      }
    }


    "Schema attributes" - {

      "id" - core { implicit conn =>
        for {
          _ <- Schema.id.get.map(_.size ==> attrCount)

          _ <- Schema.id.get(5) === List(97, 98, 99, 100, 101)

          _ <- Schema.id(97).get(5) === List(97)
          _ <- Schema.id(97, 98).get(5) === List(97, 98)

          _ <- Schema.id.not(97).get.map(_.size ==> attrCount - 1)
          _ <- Schema.id.not(97, 98).get.map(_.size ==> attrCount - 2)

          _ <- Schema.id.get.map(_.length ==> attrCount)

          // Since all attributes have an id, a tacit `id_` makes no difference
          _ <- Schema.id_.attr.get.map(_.size ==> attrCount)
          _ <- Schema.id_.attr.get(3) === List("double", "str1", "uri")

          // We can though filter by one or more tacit attribute ids
          _ <- Schema.id_(a1).attr.get === List("uuidMap")
          _ <- Schema.id_(a2, a3).attr.get === List("bigIntMap", "bigDecMap")

          _ <- Schema.id_.not(a1).attr.get.map(_.size ==> attrCount - 1)
          _ <- Schema.id_.not(a2, a3).attr.get.map(_.size ==> attrCount - 2)
        } yield ()
      }


      "a" - core { implicit conn =>
        for {
          _ <- Schema.a.get.map(_.size ==> attrCount)
          _ <- Schema.a.get(3) === List(":Ns/double", ":Ns/doubleMap", ":Ref2/ints2")

          _ <- Schema.a(":Ns/str").get === List(":Ns/str")
          _ <- Schema.a(":Ns/str", ":Ns/int").get === List(":Ns/int", ":Ns/str")

          _ <- Schema.a.not(":Ns/str").get.map(_.size ==> attrCount - 1)
          _ <- Schema.a.not(":Ns/str", ":Ns/int").get.map(_.size ==> attrCount - 2)

          // Since all attributes have an `ident`, a tacit `ident_` makes no difference
          _ <- Schema.a_.ns.get.map(_.size ==> 5)

          // We can though filter by one or more tacit ident names
          _ <- Schema.a_(":Ns/str").ns.get.map(_.sorted ==> List("Ns"))

          // Namespaces with attributes ident ":Ns/str" or ":Ref1/str1"
          _ <- Schema.a_(":Ns/str", ":Ref1/str1").ns.get.map(_.sorted ==> List("Ns", "Ref1"))

          // Negate tacit ident value
          // Note though that since other attributes than `str` exist, the namespace is still returned
          _ <- Schema.a_.not(":Ref2/int2").ns.get.map(_.sorted ==> List("Ns", "Ref1", "Ref2", "Ref3", "Ref4"))

          // If we exclude all attributes in a namespace, it won't be returned
          _ <- Schema.a_.not(
            ":Ref2/str2",
            ":Ref2/int2",
            ":Ref2/enum2",
            ":Ref2/strs2",
            ":Ref2/ints2",
            ":Ref2/ref3",
            ":Ref2/refs3",
          ).ns.get.map(_.sorted ==> List("Ns", "Ref1", "Ref3", "Ref4"))
        } yield ()
      }


      "part when not defined" - core { implicit conn =>
        for {

          // Default `db.part/user` partition name returned when no custom partitions are defined
          _ <- Schema.part.get === List("db.part/user")

          // Note that when no custom partitions are defined, namespaces are not prefixed with any partition name
        } yield ()
      }


      "nsFull" - core { implicit conn =>
        for {
          // `nsfull` always starts with lowercase letter as used in Datomic queries
          // - when partitions are defined: concatenates `part` + `ns`
          // - when partitions are not defined: `ns` starting with lower case letter

          _ <- Schema.nsFull.get === List("Ns", "Ref4", "Ref2", "Ref3", "Ref1")

          _ <- Schema.nsFull("Ref1").get === List("Ref1")
          _ <- Schema.nsFull("Ref1", "Ref2").get === List("Ref2", "Ref1")

          _ <- Schema.nsFull.not("Ref1").get.map(_.sorted ==> List("Ns", "Ref2", "Ref3", "Ref4"))
          _ <- Schema.nsFull.not("Ref1", "Ref2").get.map(_.sorted ==> List("Ns", "Ref3", "Ref4"))

          _ <- Schema.nsFull.get.map(_.length ==> 5)


          // Since all attributes have a namespace, a tacit `nsFull_` makes no difference
          _ <- Schema.nsFull_.attr.get.map(_.size ==> attrCount)

          // We can though filter by one or more tacit namespace names
          _ <- Schema.nsFull_("Ref1").attr.get.map(_.sorted ==> List(
            "enum1", "enums1", "int1", "intMap1", "ints1",
            "ref2", "refSub2", "refs2", "refsSub2", "str1", "strs1"
          ))

          // Attributes in namespace "Ref1" or "Ref2"
          _ <- Schema.nsFull_("Ref1", "Ref2").attr.get.map(_.sorted ==> List(
            "enum1", "enum2", "enums1", "int1", "int2", "intMap1", "ints1",
            "ints2", "ref2", "ref3", "refSub2", "refs2", "refs3", "refsSub2",
            "str1", "str2", "strs1", "strs2"
          ))

          // Negate tacit namespace name
          _ <- Schema.nsFull_.not("Ns").attr.get.map(_.sorted ==> List(
            "enum1", "enum2", "enum3", "enum4", "enums1",
            "int1", "int2", "int3", "int4", "intMap1",
            "ints1", "ints2", "ints3", "ints4",
            "ref2", "ref3", "ref4", "refSub2",
            "refs2", "refs3", "refs4", "refsSub2",
            "str1", "str2", "str3", "str4",
            "strs1", "strs2", "strs3", "strs4"
          ))
          _ <- Schema.nsFull_.not("Ns", "Ref2").attr.get.map(_.sorted ==> List(
            "enum1", "enum3", "enum4", "enums1",
            "int1", "int3", "int4", "intMap1",
            "ints1", "ints3", "ints4",
            "ref2", "ref4", "refSub2",
            "refs2", "refs4", "refsSub2",
            "str1", "str3", "str4",
            "strs1", "strs3", "strs4"
          ))
        } yield ()
      }


      "ns" - core { implicit conn =>
        for {
          _ <- Schema.ns.get === List("Ns", "Ref4", "Ref2", "Ref3", "Ref1")

          _ <- Schema.nsFull.get === List("Ns", "Ref4", "Ref2", "Ref3", "Ref1")

          _ <- Schema.ns("Ref1").get === List("Ref1")
          _ <- Schema.ns("Ref1", "Ref2").get === List("Ref2", "Ref1")

          _ <- Schema.ns.not("Ref1").get.map(_.sorted ==> List("Ns", "Ref2", "Ref3", "Ref4"))
          _ <- Schema.ns.not("Ref1", "Ref2").get.map(_.sorted ==> List("Ns", "Ref3", "Ref4"))


          // Since all attributes have a namespace, a tacit `ns_` makes no difference
          _ <- Schema.ns_.attr.get.map(_.size ==> attrCount)

          // We can though filter by one or more tacit namespace names
          _ <- Schema.ns_("Ref1").attr.get.map(_.sorted ==> List(
            "enum1", "enums1", "int1", "intMap1", "ints1",
            "ref2", "refSub2", "refs2", "refsSub2", "str1", "strs1"
          ))

          // Attributes in namespace "Ref1" or "Ref2"
          _ <- Schema.ns_("Ref1", "Ref2").attr.get.map(_.sorted ==> List(
            "enum1", "enum2", "enums1",
            "int1", "int2", "intMap1",
            "ints1", "ints2",
            "ref2", "ref3", "refSub2",
            "refs2", "refs3", "refsSub2",
            "str1", "str2",
            "strs1", "strs2"
          ))

          // Negate tacit namespace name
          _ <- Schema.ns_.not("Ns").attr.get.map(_.sorted ==> List(
            "enum1", "enum2", "enum3", "enum4", "enums1",
            "int1", "int2", "int3", "int4", "intMap1",
            "ints1", "ints2", "ints3", "ints4",
            "ref2", "ref3", "ref4", "refSub2",
            "refs2", "refs3", "refs4", "refsSub2",
            "str1", "str2", "str3", "str4",
            "strs1", "strs2", "strs3", "strs4"
          ))
          _ <- Schema.ns_.not("Ns", "Ref2").attr.get.map(_.sorted ==> List(
            "enum1", "enum3", "enum4", "enums1",
            "int1", "int3", "int4", "intMap1",
            "ints1", "ints3", "ints4",
            "ref2", "ref4", "refSub2",
            "refs2", "refs4", "refsSub2",
            "str1", "str3", "str4",
            "strs1", "strs3", "strs4"
          ))
        } yield ()
      }


      "attr" - core { implicit conn =>
        for {
          _ <- Schema.attr.get.map(_.size ==> attrCount)
          _ <- if (system == SystemPeer)
            Schema.attr.get(5) === List("double", "str1", "uri", "dates", "enum")
          else
            Schema.attr.get(5) === List("double", "str1", "uri", "dates")

          _ <- Schema.attr("str").get === List("str")
          _ <- Schema.attr("str", "int").get === List("str", "int")

          _ <- Schema.attr.not("str").get.map(_.size ==> attrCount - 1)
          _ <- Schema.attr.not("str", "int").get.map(_.size ==> attrCount - 2)

          _ <- Schema.attr.get.map(_.length ==> attrCount)


          // Since all attributes have an attribute name, a tacit `a_` makes no difference
          _ <- Schema.attr_.ns.get.map(_.size ==> 5)

          // We can though filter by one or more tacit attribute names
          _ <- Schema.attr_("str").ns.get.map(_.sorted ==> List("Ns"))

          // Namespaces with attributes named "str" or "str1"
          _ <- Schema.attr_("str", "str1").ns.get.map(_.sorted ==> List("Ns", "Ref1"))

          // Negate tacit attribute name
          // Note though that since other attributes than `str` exist, the namespace is still returned
          _ <- Schema.attr_.not("int2").ns.get.map(_.sorted ==> List("Ns", "Ref1", "Ref2", "Ref3", "Ref4"))

          // If we exclude all attributes in a namespace, it won't be returned
          _ <- Schema.attr_.not(
            "str2",
            "int2",
            "enum2",
            "strs2",
            "ints2",
            "ref3",
            "refs3",
          ).ns.get.map(_.sorted ==> List("Ns", "Ref1", "Ref3", "Ref4"))
        } yield ()
      }


      "tpe" - core { implicit conn =>
        for {

          // Datomic types of schema attributes
          // Note that attributes defined being of Scala type
          // - `Integer` are internally saved as type `long` in Datomic
          // Molecule transparently converts back and forth so that application code only have to consider the Scala type.

          _ <- if (system == SystemPeer)
            Schema.tpe.get.map(_.sorted ==> List(
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
          else
            Schema.tpe.get.map(_.sorted ==> List(
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

          _ <- Schema.tpe("string").get === List("string")
          _ <- Schema.tpe("string", "long").get === List("string", "long")

          _ <- if (system == SystemPeer) {
            for {
              _ <- Schema.tpe.not("instant").get.map(_.sorted ==> List(
                "bigdec",
                "bigint",
                "boolean",
                "double",
                "long",
                "ref",
                "string",
                "uri",
                "uuid",
              ))
              res <- Schema.tpe.not("instant", "boolean").get.map(_.sorted ==> List(
                "bigdec",
                "bigint",
                "double",
                "long",
                "ref",
                "string",
                "uri",
                "uuid",
              ))
            } yield res
          } else {
            for {
              // Client without bytes type
              _ <- Schema.tpe.not("instant").get.map(_.sorted ==> List(
                "bigdec",
                "bigint",
                "boolean",
                "double",
                "long",
                "ref",
                "string",
                "uri",
                "uuid",
              ))
              res <- Schema.tpe.not("instant", "boolean").get.map(_.sorted ==> List(
                "bigdec",
                "bigint",
                "double",
                "long",
                "ref",
                "string",
                "uri",
                "uuid",
              ))
            } yield res
          }

          // Since all attributes have a value type, a tacit `tpe_` makes no difference
          _ <- Schema.ns.get.map(_.size ==> 5)
          _ <- Schema.tpe_.ns.get.map(_.size ==> 5)

          // We can though filter by one or more tacit value types
          _ <- Schema.tpe_("string").ns.get.map(_.sorted ==> List(
            "Ns", "Ref1", "Ref2", "Ref3", "Ref4"))
          // Only namespace `ns` has attributes of type Boolean
          _ <- Schema.tpe_("boolean").ns.get.map(_.sorted ==> List("Ns"))

          // Namespaces with attributes of type string or long
          _ <- Schema.tpe_("string", "long").ns.get.map(_.sorted ==> List(
            "Ns", "Ref1", "Ref2", "Ref3", "Ref4"))

          // Negate tacit attribute type
          // Note though that since other attributes have other types, the namespace is still returned
          _ <- Schema.tpe_.not("string").ns.get.map(_.sorted ==> List(
            "Ns", "Ref1", "Ref2", "Ref3", "Ref4"))

          // If we exclude all attribute types in a namespace, it won't be returned
          _ <- Schema.tpe_.not("string", "long", "ref").ns.get.map(_.sorted ==> List("Ns"))
        } yield ()
      }


      "card" - core { implicit conn =>
        for {
          _ <- Schema.card.get === List("one", "many")

          _ <- Schema.card("one").get === List("one")
          _ <- Schema.card("one", "many").get === List("one", "many")

          _ <- Schema.card.not("one").get === List("many")
          _ <- Schema.card.not("one", "many").get === Nil


          // Since all attributes have a cardinality, a tacit `card_` makes no difference
          _ <- Schema.a.get.map(_.size ==> attrCount)
          _ <- Schema.card_.a.get.map(_.size ==> attrCount)

          // We can though filter by cardinality
          _ <- Schema.card_("one").a.get.map(_.size ==> card1count)
          _ <- Schema.card_("many").a.get.map(_.size ==> card2count)

          // Attributes of cardinality one or many, well that's all
          _ <- Schema.card_("one", "many").a.get.map(_.size ==> attrCount)

          // Negate tacit namespace name
          _ <- Schema.card_.not("one").a.get.map(_.size ==> card2count) // many
          _ <- Schema.card_.not("many").a.get.map(_.size ==> card1count) // one
          _ <- Schema.card_.not("one", "many").a.get.map(_.size ==> 0)
        } yield ()
      }
    }


    "Schema attribute options" - {

      "doc" - core { implicit conn =>
        for {
          // 2 core attributes are documented
          _ <- Schema.doc.get === List(
            "Card one String attribute",
            "Card one Int attribute"
          )
          // See what attributes is
          _ <- Schema.a.doc.get === List(
            (":Ns/str", "Card one String attribute"),
            (":Ns/int", "Card one Int attribute")
          )

          // Filtering by a complete `doc_` is probably not that useful
          _ <- Schema.doc("Card one Int attribute").get === List(
            "Card one Int attribute")
          // .. likely the same for negation
          _ <- Schema.doc.not("Card one String attribute").get === List(
            "Card one Int attribute")

          // Docs only searchable with Peer (requires fulltext search)
          _ <- if (system == SystemPeer) {
            for {

              // Instead, use fulltext search for a whole word in doc texts
              _ <- Schema.doc.contains("Int").get === List(
                "Card one Int attribute"
              )
              res <- Schema.doc.contains("attribute").get === List(
                "Card one String attribute",
                "Card one Int attribute"
              )
                      // Fulltext search for multiple words not allowed
                      _ = compileError(
                        """m(Schema.doc.contains("Int", "String"))""").check(
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
          _ <- Schema.doc_("Card one Int attribute").a.get === List(":Ns/int")
          // .. likely the same for negation
          _ <- Schema.doc_.not("Card one Int attribute").a.get === List(":Ns/str")


          // Docs only searchable with Peer (requires fulltext search)
          _ <- if (system == SystemPeer) {
            for {
              // Tacit fulltext search in doc texts
              _ <- Schema.doc_.contains("Int").a.get === List(":Ns/int")
              res <- Schema.doc_.contains("one").a.get === List(":Ns/int", ":Ns/str")
            } yield res
          } else Future.unit

          // Get optional attribute doc text with `doc$`
          _ <- Schema.attr_("bool", "str").a.doc$.get === List(
            (":Ns/str", Some("Card one String attribute")),
            (":Ns/bool", None),
          )

          // Filter by applying optional attribute doc text string
          someDocText1 = Some("Card one String attribute")
          someDocText2 = None
          _ <- Schema.attr_("bool", "str").a.doc$(someDocText1).get === List(
            (":Ns/str", Some("Card one String attribute"))
          )
          _ <- Schema.attr_("bool", "str").a.doc$(someDocText2).get === List(
            (":Ns/bool", None),
          )
        } yield ()
      }


      "index" - core { implicit conn =>
        if (system == SystemPeer) {
          for {
            // Index option only available in Peer

            // All attributes are indexed
            _ <- Schema.index.get === List(true) // no false
            _ <- Schema.a.index.get.map(_.size ==> attrCount)

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
            _ <- Schema.attr_("bool", "str").a.index$.get === List(
              (":Ns/str", Some(true)),
              (":Ns/bool", Some(true)),
            )

            // Filter by applying optional attribute indexing status
            some = Some(true)
            _ <- Schema.attr_("bool", "str").a.index$(some).get === List(
              (":Ns/bool", Some(true)),
              (":Ns/str", Some(true)),
            )
            _ <- Schema.attr_("bool", "str").a.index$(Some(true)).get === List(
              (":Ns/bool", Some(true)),
              (":Ns/str", Some(true)),
            )

            none = None
            _ <- Schema.attr_("bool", "str").a.index$(none).get === Nil
            _ <- Schema.attr_("bool", "str").a.index$(None).get === Nil
          } yield ()
        }
      }


      "unique" - core { implicit conn =>
        for {
          // Unique options
          _ <- Schema.unique.get === List("identity", "value")

          // Unique options
          _ <- Schema.a.unique.get === List(
            (":Ref2/str2", "identity"),
            (":Ref2/int2", "value"),
          )

          _ <- Schema.a.unique("identity").get === List((":Ref2/str2", "identity"))
          _ <- Schema.a.unique("value").get === List((":Ref2/int2", "value"))

          _ <- Schema.a.unique.not("identity").get === List((":Ref2/int2", "value"))
          _ <- Schema.a.unique.not("value").get === List((":Ref2/str2", "identity"))

          // Filter attributes by tacit `unique_` option
          _ <- Schema.unique_.a.get === List(":Ref2/int2", ":Ref2/str2")

          _ <- Schema.unique_("identity").a.get === List(":Ref2/str2")
          _ <- Schema.unique_.not("value").a.get === List(":Ref2/str2")

          // Get optional attribute indexing status with `index$`
          _ <- Schema.attr_("str", "str2", "int2").a.unique$.get.map(_.sorted ==> List(
            (":Ns/str", None),
            (":Ref2/int2", Some("value")),
            (":Ref2/str2", Some("identity")),
          ))

          // Filter by applying optional attribute uniqueness status

          some1 = Some("identity")
          _ <- Schema.attr_("str", "str2", "int2").a.unique$(some1).get === List(
            (":Ref2/str2", Some("identity"))
          )

          some2 = Some("value")
          _ <- Schema.attr_("str", "str2", "int2").a.unique$(some2).get === List(
            (":Ref2/int2", Some("value"))
          )

          none = None
          _ <- Schema.attr_("str", "str2", "int2").a.unique$(none).get === List(
            (":Ns/str", None)
          )

          // Number of non-unique attributes
          _ <- Schema.a.unique$(None).get.map(_.size ==> attrCount - 2)
        } yield ()
      }


      "fulltext" - core { implicit conn =>
        // Fulltext option only available in Peer
        if (system == SystemPeer) {
          for {
            // Fulltext options
            _ <- Schema.fulltext.get === List(true) // no false

            // Count attribute fulltext statuses (only true)
            _ <- Schema.fulltext.get.map(_.length ==> 1)

            // Attributes with fulltext search
            _ <- Schema.a.fulltext.get.map(_.sortBy(_._1) ==> List(
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
            _ <- Schema.fulltext_.a.get.map(_.sorted ==> List(
              ":Ns/str",
              ":Ns/strMap",
              ":Ns/strs",
              ":Ref1/str1",
              ":Ref2/str2"
            ))
            _ <- Schema.fulltext_(true).a.get.map(_.sorted ==> List(
              ":Ns/str",
              ":Ns/strMap",
              ":Ns/strs",
              ":Ref1/str1",
              ":Ref2/str2"
            ))
            _ <- Schema.fulltext_.not(false).a.get.map(_.sorted ==> List(
              ":Ns/str",
              ":Ns/strMap",
              ":Ns/strs",
              ":Ref1/str1",
              ":Ref2/str2"
            ))

            // Get optional attribute fulltext status with `fulltext$`
            _ <- Schema.attr_("bool", "str").a.fulltext$.get === List(
              (":Ns/str", Some(true)),
              (":Ns/bool", None),
            )

            // Filter by applying optional attribute fulltext search status
            some = Some(true)
            _ <- Schema.attr_("bool", "str").a.fulltext$(some).get === List(
              (":Ns/str", Some(true)))
            _ <- Schema.attr_("bool", "str").a.fulltext$(Some(true)).get === List(
              (":Ns/str", Some(true)))

            none = None
            _ <- Schema.attr_("bool", "str").a.fulltext$(none).get === List(
              (":Ns/bool", None))
            _ <- Schema.attr_("bool", "str").a.fulltext$(None).get === List(
              (":Ns/bool", None))

            // Number of attributes without fulltext search
            _ <- Schema.a.fulltext$(None).get.map(_.size ==> attrCount - 5)
          } yield ()
        }
      }


      "isComponent" - core { implicit conn =>
        for {
          // Component status options - either true or non-asserted
          _ <- Schema.isComponent.get === List(true) // no false
          _ <- Schema.isComponent.get.map(_.length ==> 1)

          // Component attributes
          _ <- Schema.a.isComponent.get === List(
            (":Ns/refSub1", true),
            (":Ns/refsSub1", true),
            (":Ref1/refsSub2", true),
            (":Ref1/refSub2", true),
          )

          _ <- Schema.a.isComponent(true).get.map(_.size ==> 4)
          // Option is either true or non-asserted (nil/None), never false
          _ <- Schema.a.isComponent(false).get.map(_.size ==> 0)

          _ <- Schema.a.isComponent.not(true).get.map(_.size ==> 0)
          _ <- Schema.a.isComponent.not(false).get.map(_.size ==> 4)

          // Filter attributes with tacit `isComponent_` option
          _ <- Schema.isComponent_.a.get === List(
            ":Ns/refsSub1",
            ":Ref1/refSub2",
            ":Ref1/refsSub2",
            ":Ns/refSub1",
          )
          _ <- Schema.isComponent_(true).a.get === List(
            ":Ns/refsSub1",
            ":Ref1/refSub2",
            ":Ref1/refsSub2",
            ":Ns/refSub1",
          )
          _ <- Schema.isComponent_.not(false).a.get === List(
            ":Ns/refsSub1",
            ":Ref1/refSub2",
            ":Ref1/refsSub2",
            ":Ns/refSub1",
          )

          // Get optional attribute component status with `isComponent$`
          _ <- Schema.attr_("bool", "refSub1").a.isComponent$.get.map(_.sorted ==> List(
            (":Ns/bool", None),
            (":Ns/refSub1", Some(true)),
          ))

          // Filter by applying optional attribute component status
          some = Some(true)
          _ <- Schema.attr_("bool", "refSub1").a.isComponent$(some).get === List(
            (":Ns/refSub1", Some(true)))

          _ <- Schema.attr_("bool", "refSub1").a.isComponent$(Some(true)).get === List(
            (":Ns/refSub1", Some(true)))

          none = None
          _ <- Schema.attr_("bool", "refSub1").a.isComponent$(none).get === List(
            (":Ns/bool", None))

          _ <- Schema.attr_("bool", "refSub1").a.isComponent$(None).get === List(
            (":Ns/bool", None))

          // Number of non-component attributes
          _ <- Schema.a.isComponent$(None).get.map(_.size ==> attrCount - 4)
        } yield ()
      }


      "noHistory" - core { implicit conn =>
        for {
          // No-history status options - either true or non-asserted
          _ <- Schema.noHistory.get === List(true) // no false
          _ <- Schema.noHistory.get.map(_.length ==> 1)

          // No-history attributes
          _ <- Schema.a.noHistory.get === List(
            (":Ref2/ints2", true)
          )

          _ <- Schema.a.noHistory(true).get.map(_.size ==> 1)
          // Option is either true or non-asserted (nil/None), never false
          _ <- Schema.a.noHistory(false).get.map(_.size ==> 0)

          _ <- Schema.a.noHistory.not(true).get.map(_.size ==> 0)
          _ <- Schema.a.noHistory.not(false).get.map(_.size ==> 1)


          // Filter attributes with tacit `noHistory_` option
          _ <- Schema.noHistory_.a.get === List(":Ref2/ints2")
          _ <- Schema.noHistory_(true).a.get === List(":Ref2/ints2")
          _ <- Schema.noHistory_.not(false).a.get === List(":Ref2/ints2")


          // Get optional attribute no-history status with `noHistory$`
          _ <- Schema.attr_("bool", "ints2").a.noHistory$.get.map(_.sorted ==> List(
            (":Ns/bool", None),
            (":Ref2/ints2", Some(true)),
          ))

          // Filter by applying optional attribute no-history status
          some = Some(true)
          _ <- Schema.attr_("bool", "ints2").a.noHistory$(some).get === List(
            (":Ref2/ints2", Some(true)))

          _ <- Schema.attr_("bool", "ints2").a.noHistory$(Some(true)).get === List(
            (":Ref2/ints2", Some(true)))

          none = None
          _ <- Schema.attr_("bool", "ints2").a.noHistory$(none).get === List(
            (":Ns/bool", None))

          _ <- Schema.attr_("bool", "ints2").a.noHistory$(None).get === List(
            (":Ns/bool", None))

          // Number of non-component attributes
          _ <- Schema.a.noHistory$(None).get.map(_.size ==> attrCount - 1)
        } yield ()
      }
    }

    "enum" - core { implicit conn =>
      for {

        // Attribute/enum values in namespace `ref2`
        _ <- Schema.ns_("Ref2").attr.enum.get.map(_.sorted ==> List(
          ("enum2", "enum20"),
          ("enum2", "enum21"),
          ("enum2", "enum22"),
        ))

        // All enums grouped by ident
        _ <- Schema.a.enum.get.map(_.groupBy(_._1).map(g => g._1 -> g._2.map(_._2).sorted)
          .toList.sortBy(_._1) ==> List(
          ":Ns/enum" -> List("enum0", "enum1", "enum2", "enum3", "enum4",
            "enum5", "enum6", "enum7", "enum8", "enum9"),
          ":Ns/enums" -> List("enum0", "enum1", "enum2", "enum3", "enum4",
            "enum5", "enum6", "enum7", "enum8", "enum9"),
          ":Ref1/enum1" -> List("enum10", "enum11", "enum12"),
          ":Ref1/enums1" -> List("enum10", "enum11", "enum12"),
          ":Ref2/enum2" -> List("enum20", "enum21", "enum22"),
          ":Ref3/enum3" -> List("enum30", "enum31", "enum32"),
          ":Ref4/enum4" -> List("enum40", "enum41", "enum42"),
        ))

        // Enums of a specific attribute
        _ <- Schema.a_(":Ns/enum").enum.get.map(_.sorted ==> List(
          "enum0", "enum1", "enum2", "enum3", "enum4",
          "enum5", "enum6", "enum7", "enum8", "enum9"
        ))

        _ <- Schema.a.enum("enum0").get === List(
          (":Ns/enums", "enum0"),
          (":Ns/enum", "enum0")
        )

        _ <- Schema.a.enum("enum0", "enum1").get.map(_.sortBy(r => (r._1, r._2)) ==> List(
          (":Ns/enum", "enum0"),
          (":Ns/enum", "enum1"),
          (":Ns/enums", "enum0"),
          (":Ns/enums", "enum1")
        ))

        // Enums per namespace
        _ <- Schema.ns.enum.get.map(_.groupBy(_._1)
          .map { case (k, v) => k -> v.length }.toList
          .sortBy(_._1) ==> List(
          ("Ns", 10),
          ("Ref1", 3),
          ("Ref2", 3),
          ("Ref3", 3),
          ("Ref4", 3),
        ))

        // Enums per namespace per attribute
        _ <- Schema.ns.attr.enum.get.map(_.groupBy { case (n, a, _) => (n, a) }
          .map { case (pair, vs) => (pair._1, pair._2, vs.length) }.toList
          .sortBy(t => (t._1, t._2)) ==> List(
          ("Ns", "enum", 10),
          ("Ns", "enums", 10),
          ("Ref1", "enum1", 3),
          ("Ref1", "enums1", 3),
          ("Ref2", "enum2", 3),
          ("Ref3", "enum3", 3),
          ("Ref4", "enum4", 3),
        ))

        // Attributes with some enum value
        _ <- Schema.a.enum_("enum0").get.map(_.sorted ==> List(
          ":Ns/enum", ":Ns/enums"))
        _ <- Schema.a.enum_("enum0", "enum1").get.map(_.sorted ==> List(
          ":Ns/enum", ":Ns/enums"))

        // Excluding one enum value will still match the other values
        _ <- Schema.a.enum_.not("enum0").get.map(_.sorted ==> List(
          ":Ns/enum",
          ":Ns/enums",
          ":Ref1/enum1",
          ":Ref1/enums1",
          ":Ref2/enum2",
          ":Ref3/enum3",
          ":Ref4/enum4",
        ))

        // If we exclude all enum values of an attribute it won't be returned
        _ <- Schema.a.enum_.not("enum10", "enum11", "enum12").get.map(_.sorted ==> List(
          ":Ns/enum",
          ":Ns/enums",
          ":Ref2/enum2",
          ":Ref3/enum3",
          ":Ref4/enum4",
        ))
      } yield ()
    }


    "t, tx, txInstant" - core { implicit conn =>
      // Peer and dev-local schema transaction was last transaction
      if (system != SystemPeerServer) {
        for {
          conn2 <- conn
          t <- conn2.db.t
          tx <- conn2.db.tx
          txInstant <- conn2.db.txInstant

          // Schema transaction time t
          _ <- Schema.t.get === List(t)

          // Schema transaction entity id
          _ <- Schema.tx.get === List(tx)

          // Schema transaction wall clock time
          _ <- Schema.txInstant.get === List(txInstant)
        } yield ()
      }
    }
  }
}