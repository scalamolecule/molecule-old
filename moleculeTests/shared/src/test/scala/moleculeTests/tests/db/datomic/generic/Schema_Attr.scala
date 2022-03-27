package moleculeTests.tests.db.datomic.generic

import molecule.core.util.Executor._
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out3._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import utest._


object Schema_Attr extends AsyncTestSuite {

  // Differing counts and ids for different systems
  val List(attrCount, uuidMapId, bigIntMapId, bigDecMapId, card1count, card2count) = (system, protocol) match {
    case (SystemPeer, "free")  => List(63, 97, 99, 100, 28, 35)
    case (SystemPeer, _)       => List(63, 106, 108, 109, 28, 35)
    case (SystemDevLocal, _)   => List(63, 107, 109, 110, 28, 35)
    case (SystemPeerServer, _) => List(65, 104, 106, 107, 29, 36)
  }

  lazy val tests = Tests {

    "id" - core { implicit conn =>
      for {
        _ <- Schema.attrId.get(5).map(_ ==> List(97, 98, 99, 100, 101))

        _ <- Schema.attrId(count).get.map(_.head ==> attrCount)

        _ <- Schema.attrId(97).get(5).map(_ ==> List(97))
        _ <- Schema.attrId(97, 98).get(5).map(_ ==> List(97, 98))

        _ <- Schema.attrId.not(97).get.map(_.size ==> attrCount - 1)
        _ <- Schema.attrId.not(97, 98).get.map(_.size ==> attrCount - 2)

        _ <- Schema.attrId_(uuidMapId).attr.get.map(_ ==> List("uuidMap"))
        _ <- Schema.attrId_(bigIntMapId, bigDecMapId).attr.get.map(_ ==> List("bigIntMap", "bigDecMap"))

        _ <- Schema.attrId_.not(uuidMapId).attr.get.map(_.size ==> attrCount - 1)
        _ <- Schema.attrId_.not(bigIntMapId, bigDecMapId).attr.get.map(_.size ==> attrCount - 2)
      } yield ()
    }


    "a" - core { implicit conn =>
      for {
        _ <- Schema.a(count).get.map(_.head ==> attrCount)
        _ <- Schema.ns_("Ref1").a(count).get.map(_ ==> List(12))

        _ <- Schema.a.get(3).map(_ ==> List(":Ns/double", ":Ns/doubleMap", ":Ref2/ints2"))

        _ <- Schema.a(":Ns/str").get.map(_ ==> List(":Ns/str"))
        _ <- Schema.a(":Ns/str", ":Ns/int").get.map(_ ==> List(":Ns/int", ":Ns/str"))

        _ <- Schema.a.not(":Ns/str").get.map(_.size ==> attrCount - 1)
        _ <- Schema.a.not(":Ns/str", ":Ns/int").get.map(_.size ==> attrCount - 2)

        _ <- Schema.a_(":Ns/str").ns.a1.get.map(_ ==> List("Ns"))
        _ <- Schema.a_(":Ns/str", ":Ref1/str1").ns.a1.get.map(_ ==> List("Ns", "Ref1"))

        // Negate tacit ident value
        // Note though that since other attributes than `str` exist, the namespace is still returned
        _ <- Schema.a_.not(":Ref2/int2").ns.a1.get.map(_ ==> List("Ns", "Ref1", "Ref2", "Ref3", "Ref4"))

        // If we exclude all attributes in a namespace, it won't be returned
        _ <- Schema.a_.not(
          ":Ref2/str2",
          ":Ref2/int2",
          ":Ref2/enum2",
          ":Ref2/strs2",
          ":Ref2/ints2",
          ":Ref2/ref3",
          ":Ref2/refs3",
        ).ns.a1.get.map(_ ==> List("Ns", "Ref1", "Ref3", "Ref4"))

      } yield ()
    }


    "part (default when not defined)" - core { implicit conn =>
      for {
        // Default `db.part/user` partition name returned when no custom partitions are defined
        _ <- Schema.part.get.map(_ ==> List("db.part/user"))
        _ <- Schema.part(count).get.map(_.head ==> 1)
      } yield ()
    }

    "part" - partition { implicit conn =>
      for {
        _ <- Schema.part.a1.get.map(_ ==> List("gen", "lit"))
        _ <- Schema.part(count).get.map(_.head ==> 2)
      } yield ()
    }


    "nsFull" - core { implicit conn =>
      for {
        _ <- Schema.nsFull.get.map(_ ==> List("Ns", "Ref4", "Ref2", "Ref3", "Ref1"))

        _ <- Schema.nsFull(count).get.map(_.head ==> 5)

        _ <- Schema.nsFull("Ref1").get.map(_ ==> List("Ref1"))
        _ <- Schema.nsFull("Ref1", "Ref2").get.map(_ ==> List("Ref2", "Ref1"))

        _ <- Schema.nsFull.not("Ref1").a1.get.map(_ ==> List("Ns", "Ref2", "Ref3", "Ref4"))
        _ <- Schema.nsFull.not("Ref1", "Ref2").a1.get.map(_ ==> List("Ns", "Ref3", "Ref4"))

        // Since all attributes have a namespace, a tacit `nsFull_` makes no difference
        _ <- Schema.nsFull_.attr.get.map(_.size ==> attrCount)

        // We can though filter by one or more tacit namespace names
        _ <- Schema.nsFull_("Ref1").attr.a1.get.map(_ ==> List(
          "enum1", "enums1", "int1", "intMap1", "ints1", "nss",
          "ref2", "refSub2", "refs2", "refsSub2", "str1", "strs1"
        ))

        // Attributes in namespace "Ref1" or "Ref2"
        _ <- Schema.nsFull_("Ref1", "Ref2").attr.a1.get.map(_ ==> List(
          "enum1", "enum2", "enums1", "int1", "int2", "intMap1", "ints1",
          "ints2", "nss", "ref2", "ref3", "refSub2", "refs2", "refs3", "refsSub2",
          "str1", "str2", "strs1", "strs2"
        ))

        // Negate tacit namespace name
        _ <- Schema.nsFull_.not("Ns").attr.a1.get.map(_ ==> List(
          "enum1", "enum2", "enums1",
          "int1", "int2", "int3", "int4", "intMap1",
          "ints1", "ints2",
          "nss",
          "ref2", "ref3", "ref4", "refSub2",
          "refs2", "refs3", "refs4", "refsSub2",
          "str1", "str2", "str3", "str4",
          "strs1", "strs2"
        ))
        _ <- Schema.nsFull_.not("Ns", "Ref2").attr.a1.get.map(_ ==> List(
          "enum1", "enums1",
          "int1", "int3", "int4", "intMap1",
          "ints1",
          "nss",
          "ref2", "ref4", "refSub2",
          "refs2", "refs4", "refsSub2",
          "str1", "str3", "str4",
          "strs1"
        ))
      } yield ()
    }


    "ns" - core { implicit conn =>
      for {
        _ <- Schema.ns.a1.get.map(_ ==> List("Ns", "Ref4", "Ref2", "Ref3", "Ref1"))

        _ <- Schema.ns(count).get.map(_.head ==> 5)

        _ <- Schema.ns("Ref1").get.map(_ ==> List("Ref1"))
        _ <- Schema.ns("Ref1", "Ref2").get.map(_ ==> List("Ref2", "Ref1"))

        _ <- Schema.ns.not("Ref1").a1.get.map(_ ==> List("Ns", "Ref2", "Ref3", "Ref4"))
        _ <- Schema.ns.not("Ref1", "Ref2").a1.get.map(_ ==> List("Ns", "Ref3", "Ref4"))

        _ <- Schema.ns_("Ref1").attr.a1.get.map(_ ==> List(
          "enum1", "enums1", "int1", "intMap1", "ints1", "nss",
          "ref2", "refSub2", "refs2", "refsSub2", "str1", "strs1"
        ))

        // Attributes in namespace "Ref1" or "Ref2"
        _ <- Schema.ns_("Ref1", "Ref2").attr.a1.get.map(_ ==> List(
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
        _ <- Schema.ns_.not("Ns").attr.a1.get.map(_ ==> List(
          "enum1", "enum2", "enums1",
          "int1", "int2", "int3", "int4", "intMap1",
          "ints1", "ints2",
          "nss",
          "ref2", "ref3", "ref4", "refSub2",
          "refs2", "refs3", "refs4", "refsSub2",
          "str1", "str2", "str3", "str4",
          "strs1", "strs2"
        ))
        _ <- Schema.ns_.not("Ns", "Ref2").attr.a1.get.map(_ ==> List(
          "enum1", "enums1",
          "int1", "int3", "int4", "intMap1",
          "ints1",
          "nss",
          "ref2", "ref4", "refSub2",
          "refs2", "refs4", "refsSub2",
          "str1", "str3", "str4",
          "strs1"
        ))
      } yield ()
    }


    "attr" - core { implicit conn =>
      for {
        _ <- Schema.attr.get(5).map(_ ==> List("double", "str1", "uri", "dates", "int"))

        _ <- Schema.attr(count).get.map(_.head ==> attrCount)

        _ <- Schema.attr("str").get.map(_ ==> List("str"))
        _ <- Schema.attr("str", "int").get.map(_ ==> List("str", "int"))

        _ <- Schema.attr.not("str").get.map(_.size ==> attrCount - 1)
        _ <- Schema.attr.not("str", "int").get.map(_.size ==> attrCount - 2)

        _ <- Schema.attr_("str").ns.a1.get.map(_ ==> List("Ns"))
        _ <- Schema.attr_("str", "str1").ns.a1.get.map(_ ==> List("Ns", "Ref1"))

        // Negate tacit attribute name
        // Note though that since other attributes than `str` exist, the namespace is still returned
        _ <- Schema.attr_.not("int2").ns.a1.get.map(_ ==> List("Ns", "Ref1", "Ref2", "Ref3", "Ref4"))

        // If we exclude all attributes in a namespace, it won't be returned
        _ <- Schema.attr_.not(
          "str2",
          "int2",
          "enum2",
          "strs2",
          "ints2",
          "ref3",
          "refs3",
        ).ns.a1.get.map(_ ==> List("Ns", "Ref1", "Ref3", "Ref4"))
      } yield ()
    }


    "enum" - core { implicit conn =>
      for {
        // Count all enum values
        _ <- Schema.enumm(count).get.map(_.head ==> 16)

        // Count enum values per namespace
        _ <- Schema.ns.a1.enumm(count).get.map(_ ==> List(
          ("Ns", 10),
          ("Ref1", 3),
          ("Ref2", 3),
        ))

        // Attribute/enum values in namespace `ref2`
        _ <- Schema.ns_("Ref2").attr.enumm.a1.get.map(_ ==> List(
          ("enum2", "enum20"),
          ("enum2", "enum21"),
          ("enum2", "enum22"),
        ))

        // All enums grouped by ident
        _ <- Schema.a.enumm.get.map(_.groupBy(_._1).map(g => g._1 -> g._2.map(_._2).sorted)
          .toList.sortBy(_._1) ==> List(
          ":Ns/enumm" -> List(
            "enum0", "enum1", "enum2", "enum3", "enum4", "enum5", "enum6", "enum7", "enum8", "enum9"),
          ":Ns/enums" -> List(
            "enum0", "enum1", "enum2", "enum3", "enum4", "enum5", "enum6", "enum7", "enum8", "enum9"),
          ":Ref1/enum1" -> List("enum10", "enum11", "enum12"),
          ":Ref1/enums1" -> List("enum10", "enum11", "enum12"),
          ":Ref2/enum2" -> List("enum20", "enum21", "enum22")
        ))

        // Enums of a specific attribute
        _ <- Schema.a_(":Ns/enumm").enumm.a1.get.map(_ ==> List(
          "enum0", "enum1", "enum2", "enum3", "enum4",
          "enum5", "enum6", "enum7", "enum8", "enum9"
        ))

        _ <- Schema.a.enumm("enum0").get.map(_ ==> List(
          (":Ns/enums", "enum0"),
          (":Ns/enumm", "enum0")
        ))

        _ <- Schema.a.a1.enumm("enum0", "enum1").a2.get.map(_ ==> List(
          (":Ns/enumm", "enum0"),
          (":Ns/enumm", "enum1"),
          (":Ns/enums", "enum0"),
          (":Ns/enums", "enum1")
        ))

        // Enums per namespace
        _ <- Schema.ns.enumm.get.map(_.groupBy(_._1)
          .map { case (k, v) => k -> v.length }.toList
          .sortBy(_._1) ==> List(
          ("Ns", 10),
          ("Ref1", 3),
          ("Ref2", 3),
        ))

        // Enums per namespace per attribute
        _ <- Schema.ns.attr.enumm.get.map(_.groupBy { case (n, a, _) => (n, a) }
          .map { case (pair, vs) => (pair._1, pair._2, vs.length) }.toList
          .sortBy(t => (t._1, t._2)) ==> List(
          ("Ns", "enumm", 10),
          ("Ns", "enums", 10),
          ("Ref1", "enum1", 3),
          ("Ref1", "enums1", 3),
          ("Ref2", "enum2", 3),
        ))

        // Attributes with some enum value
        _ <- Schema.a.a1.enumm_("enum0").get.map(_ ==> List(
          ":Ns/enumm", ":Ns/enums"))
        _ <- Schema.a.a1.enumm_("enum0", "enum1").get.map(_ ==> List(
          ":Ns/enumm", ":Ns/enums"))

        // Excluding one enum value will still match the other values
        _ <- Schema.a.a1.enumm_.not("enum0").get.map(_ ==> List(
          ":Ns/enumm",
          ":Ns/enums",
          ":Ref1/enum1",
          ":Ref1/enums1",
          ":Ref2/enum2",
        ))

        // If we exclude all enum values of an attribute it won't be returned
        _ <- Schema.a.a1.enumm_.not("enum10", "enum11", "enum12").get.map(_ ==> List(
          ":Ns/enumm",
          ":Ns/enums",
          ":Ref2/enum2",
        ))
      } yield ()
    }


    "valueType" - core { implicit conn =>
      for {
        // Datomic types of schema attributes
        // Note that attributes defined being of Scala type
        // - `Integer` are internally saved as type `long` in Datomic
        // Molecule transparently converts back and forth so that application code only have to consider the Scala type.

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

        _ <- Schema.ns.a1.valueType(count).get.map(_ ==> List(
          ("Ns", 10),
          ("Ref1", 3),
          ("Ref2", 3),
          ("Ref3", 3),
          ("Ref4", 2),
        ))

        _ <- Schema.valueType("string").get.map(_ ==> List("string"))
        _ <- Schema.valueType("string", "long").get.map(_ ==> List("string", "long"))

        _ <- Schema.valueType.not("instant").a1.get.map(_ ==> List(
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
        _ <- Schema.valueType.not("instant", "boolean").a1.get.map(_ ==> List(
          "bigdec",
          "bigint",
          "double",
          "long",
          "ref",
          "string",
          "uri",
          "uuid",
        ))

        // We can though filter by one or more tacit value types
        _ <- Schema.valueType_("string").ns.a1.get.map(_ ==> List(
          "Ns", "Ref1", "Ref2", "Ref3", "Ref4"))
        // Only namespace `ns` has attributes of type Boolean
        _ <- Schema.valueType_("boolean").ns.a1.get.map(_ ==> List("Ns"))

        // Namespaces with attributes of type string or long
        _ <- Schema.valueType_("string", "long").ns.a1.get.map(_ ==> List(
          "Ns", "Ref1", "Ref2", "Ref3", "Ref4"))

        // Negate tacit attribute type
        // Note though that since other attributes have other types, the namespace is still returned
        _ <- Schema.valueType_.not("string").ns.a1.get.map(_ ==> List(
          "Ns", "Ref1", "Ref2", "Ref3", "Ref4"))

        // If we exclude all attribute types in a namespace, it won't be returned
        _ <- Schema.valueType_.not("string", "long", "ref").ns.a1.get.map(_ ==> List("Ns"))

      } yield ()
    }


    "cardinality" - core { implicit conn =>
      for {
        _ <- Schema.cardinality.get.map(_ ==> List("one", "many"))

        _ <- Schema.cardinality(count).get.map(_.head ==> 2)

        _ <- Schema.cardinality("one").get.map(_ ==> List("one"))
        _ <- Schema.cardinality("one", "many").get.map(_ ==> List("one", "many"))

        _ <- Schema.cardinality.not("one").get.map(_ ==> List("many"))
        _ <- Schema.cardinality.not("one", "many").get.map(_ ==> Nil)

        _ <- Schema.cardinality_("one").a.get.map(_.size ==> card1count)
        _ <- Schema.cardinality_("many").a.get.map(_.size ==> card2count)

        // Attributes of cardinality one or many, well that's all
        _ <- Schema.cardinality_("one", "many").a.get.map(_.size ==> attrCount)

        // Negate tacit namespace name
        _ <- Schema.cardinality_.not("one").a.get.map(_.size ==> card2count) // many
        _ <- Schema.cardinality_.not("many").a.get.map(_.size ==> card1count) // one
        _ <- Schema.cardinality_.not("one", "many").a.get.map(_.size ==> 0)
      } yield ()
    }


    "t, tx, txInstant" - core { implicit futConn =>
      // Peer and dev-local schema transaction was last transaction
      if (system != SystemPeerServer) {
        for {
          conn <- futConn
          db <- conn.db
          t <- db.basisT
          // Fetch schema transaction tx and date
          (tx, txInstant) <- Log(Some(t)).tx.txInstant.get.map(_.head)

          // Schema transaction time t
          _ <- Schema.t.get.map(_ ==> List(t))

          // Schema transaction entity id
          _ <- Schema.tx.get.map(_ ==> List(tx))

          // Schema transaction wall clock time
          _ <- Schema.txInstant.get.map(_ ==> List(txInstant))

          // All schema is transacted in one transaction
          _ <- Schema.t(count).get.map(_.head ==> 1)
          _ <- Schema.tx(count).get.map(_.head ==> 1)
          _ <- Schema.txInstant(count).get.map(_.head ==> 1)
        } yield ()
      }
    }
  }
}