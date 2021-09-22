package moleculeTests.tests.core.generic

import molecule.datomic.api.out3._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Schema_Attr extends AsyncTestSuite {

  // Differing counts and ids for different systems
  val List(attrCount, a1, a2, a3, card1count, card2count) = system match {
    //    case SystemPeer       => List(68, 106, 108, 109, 30, 38)
    case SystemPeer       => List(69, 106, 108, 109, 30, 39)
    case SystemDevLocal   => List(71, 105, 107, 108, 31, 40)
    case SystemPeerServer => List(71, 104, 106, 107, 31, 40)
  }

  lazy val tests = Tests {

    "id" - core { implicit conn =>
      for {
        _ <- Schema.id.get.map(_.size ==> attrCount)

        _ <- Schema.id.get(5).map(_ ==> List(97, 98, 99, 100, 101))

        _ <- Schema.id(97).get(5).map(_ ==> List(97))
        _ <- Schema.id(97, 98).get(5).map(_ ==> List(97, 98))

        _ <- Schema.id.not(97).get.map(_.size ==> attrCount - 1)
        _ <- Schema.id.not(97, 98).get.map(_.size ==> attrCount - 2)

        _ <- Schema.id.get.map(_.length ==> attrCount)

        // Since all attributes have an id, a tacit `id_` makes no difference
        _ <- Schema.id_.attr.get.map(_.size ==> attrCount)
        _ <- Schema.id_.attr.get(3).map(_ ==> List("double", "str1", "uri"))

        // We can though filter by one or more tacit attribute ids
        _ <- Schema.id_(a1).attr.get.map(_ ==> List("uuidMap"))
        _ <- Schema.id_(a2, a3).attr.get.map(_ ==> List("bigIntMap", "bigDecMap"))

        _ <- Schema.id_.not(a1).attr.get.map(_.size ==> attrCount - 1)
        _ <- Schema.id_.not(a2, a3).attr.get.map(_.size ==> attrCount - 2)
      } yield ()
    }


    "a" - core { implicit conn =>
      for {
        _ <- Schema.a.get.map(_.size ==> attrCount)
        _ <- Schema.a.get(3).map(_ ==> List(":Ns/double", ":Ns/doubleMap", ":Ref2/ints2"))

        _ <- Schema.a(":Ns/str").get.map(_ ==> List(":Ns/str"))
        _ <- Schema.a(":Ns/str", ":Ns/int").get.map(_ ==> List(":Ns/int", ":Ns/str"))

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
      // Default `db.part/user` partition name returned when no custom partitions are defined
      Schema.part.get.map(_ ==> List("db.part/user"))

      // Note that when no custom partitions are defined, namespaces are not prefixed with any partition name
    }


    "nsFull" - core { implicit conn =>
      for {
        // `nsfull` always starts with lowercase letter as used in Datomic queries
        // - when partitions are defined: concatenates `part` + `ns`
        // - when partitions are not defined: `ns` starting with lower case letter

        _ <- Schema.nsFull.get.map(_ ==> List("Ns", "Ref4", "Ref2", "Ref3", "Ref1"))

        _ <- Schema.nsFull("Ref1").get.map(_ ==> List("Ref1"))
        _ <- Schema.nsFull("Ref1", "Ref2").get.map(_ ==> List("Ref2", "Ref1"))

        _ <- Schema.nsFull.not("Ref1").get.map(_.sorted ==> List("Ns", "Ref2", "Ref3", "Ref4"))
        _ <- Schema.nsFull.not("Ref1", "Ref2").get.map(_.sorted ==> List("Ns", "Ref3", "Ref4"))

        _ <- Schema.nsFull.get.map(_.length ==> 5)


        // Since all attributes have a namespace, a tacit `nsFull_` makes no difference
        _ <- Schema.nsFull_.attr.get.map(_.size ==> attrCount)

        // We can though filter by one or more tacit namespace names
        _ <- Schema.nsFull_("Ref1").attr.get.map(_.sorted ==> List(
          "enum1", "enums1", "int1", "intMap1", "ints1", "nss",
          "ref2", "refSub2", "refs2", "refsSub2", "str1", "strs1"
        ))

        // Attributes in namespace "Ref1" or "Ref2"
        _ <- Schema.nsFull_("Ref1", "Ref2").attr.get.map(_.sorted ==> List(
          "enum1", "enum2", "enums1", "int1", "int2", "intMap1", "ints1",
          "ints2", "nss", "ref2", "ref3", "refSub2", "refs2", "refs3", "refsSub2",
          "str1", "str2", "strs1", "strs2"
        ))

        // Negate tacit namespace name
        _ <- Schema.nsFull_.not("Ns").attr.get.map(_.sorted ==> List(
          "enum1", "enum2", "enum3", "enum4", "enums1",
          "int1", "int2", "int3", "int4", "intMap1",
          "ints1", "ints2", "ints3", "ints4",
          "nss",
          "ref2", "ref3", "ref4", "refSub2",
          "refs2", "refs3", "refs4", "refsSub2",
          "str1", "str2", "str3", "str4",
          "strs1", "strs2", "strs3", "strs4"
        ))
        _ <- Schema.nsFull_.not("Ns", "Ref2").attr.get.map(_.sorted ==> List(
          "enum1", "enum3", "enum4", "enums1",
          "int1", "int3", "int4", "intMap1",
          "ints1", "ints3", "ints4",
          "nss",
          "ref2", "ref4", "refSub2",
          "refs2", "refs4", "refsSub2",
          "str1", "str3", "str4",
          "strs1", "strs3", "strs4"
        ))
      } yield ()
    }


    "ns" - core { implicit conn =>
      for {
        _ <- Schema.ns.get.map(_ ==> List("Ns", "Ref4", "Ref2", "Ref3", "Ref1"))

        _ <- Schema.nsFull.get.map(_ ==> List("Ns", "Ref4", "Ref2", "Ref3", "Ref1"))

        _ <- Schema.ns("Ref1").get.map(_ ==> List("Ref1"))
        _ <- Schema.ns("Ref1", "Ref2").get.map(_ ==> List("Ref2", "Ref1"))

        _ <- Schema.ns.not("Ref1").get.map(_.sorted ==> List("Ns", "Ref2", "Ref3", "Ref4"))
        _ <- Schema.ns.not("Ref1", "Ref2").get.map(_.sorted ==> List("Ns", "Ref3", "Ref4"))


        // Since all attributes have a namespace, a tacit `ns_` makes no difference
        _ <- Schema.ns_.attr.get.map(_.size ==> attrCount)

        // We can though filter by one or more tacit namespace names
        _ <- Schema.ns_("Ref1").attr.get.map(_.sorted ==> List(
          "enum1", "enums1", "int1", "intMap1", "ints1", "nss",
          "ref2", "refSub2", "refs2", "refsSub2", "str1", "strs1"
        ))

        // Attributes in namespace "Ref1" or "Ref2"
        _ <- Schema.ns_("Ref1", "Ref2").attr.get.map(_.sorted ==> List(
          "enum1", "enum2", "enums1",
          "int1", "int2", "intMap1",
          "ints1", "ints2",
          "nss",
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
          "nss",
          "ref2", "ref3", "ref4", "refSub2",
          "refs2", "refs3", "refs4", "refsSub2",
          "str1", "str2", "str3", "str4",
          "strs1", "strs2", "strs3", "strs4"
        ))
        _ <- Schema.ns_.not("Ns", "Ref2").attr.get.map(_.sorted ==> List(
          "enum1", "enum3", "enum4", "enums1",
          "int1", "int3", "int4", "intMap1",
          "ints1", "ints3", "ints4",
          "nss",
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
          Schema.attr.get(5).map(_ ==> List("double", "str1", "uri", "dates", "enum"))
        else
          Schema.attr.get(5).map(_ ==> List("double", "str1", "uri", "dates"))

        _ <- Schema.attr("str").get.map(_ ==> List("str"))
        _ <- Schema.attr("str", "int").get.map(_ ==> List("str", "int"))

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

        _ <- Schema.tpe("string").get.map(_ ==> List("string"))
        _ <- Schema.tpe("string", "long").get.map(_ ==> List("string", "long"))

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
        _ <- Schema.card.get.map(_ ==> List("one", "many"))

        _ <- Schema.card("one").get.map(_ ==> List("one"))
        _ <- Schema.card("one", "many").get.map(_ ==> List("one", "many"))

        _ <- Schema.card.not("one").get.map(_ ==> List("many"))
        _ <- Schema.card.not("one", "many").get.map(_ ==> Nil)


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

        _ <- Schema.a.enum("enum0").get.map(_ ==> List(
          (":Ns/enums", "enum0"),
          (":Ns/enum", "enum0")
        ))

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


    "t, tx, txInstant" - core { implicit futConn =>
      // Peer and dev-local schema transaction was last transaction
      if (system != SystemPeerServer) {
        for {
          conn <- futConn
          t <- conn.db.t
          tx <- conn.db.tx
          txInstant <- conn.db.txInstant

          // Schema transaction time t
          _ <- Schema.t.get.map(_ ==> List(t))

          // Schema transaction entity id
          _ <- Schema.tx.get.map(_ ==> List(tx))

          // Schema transaction wall clock time
          _ <- Schema.txInstant.get.map(_ ==> List(txInstant))
        } yield ()
      }
    }
  }
}