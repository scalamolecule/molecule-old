package moleculeTests.jvm.db.datomic

import molecule.core.data.model._
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import molecule.datomic.base.transform.exception.Model2QueryException
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import utest._


object SchemaChange_AttrOptions extends AsyncTestSuite {

  lazy val tests = Tests {

    "valueType" - empty { implicit futConn =>
      for {
        // Initial Int attribute
        t1 <- transact(schema {
          trait Foo {
            val str = oneString
          }
        }).map(_.last.t)

        _ <- Schema.t.a.valueType.get.map(_ ==> List(
          (t1, ":Foo/str", "string"),
        ))

        // Can't change value type
        _ <- transact(schema {
          trait Foo {
            val str = oneBoolean
          }
        }).map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            if (system == SystemPeer)
              msg ==> ":db.error/invalid-alter-attribute Error: {" +
                ":db/error :db.error/unsupported-alter-schema, " +
                ":entity :Foo/str, " +
                ":attribute :db/valueType, " +
                ":from :db.type/string, " +
                ":to :db.type/boolean}"
            else
              msg ==> "Error: {:db/error :db.error/unsupported-alter-schema, " +
                ":entity :Foo/str, " +
                ":attribute :db/valueType, " +
                ":from :db.type/string, " +
                ":to :db.type/boolean}"
          }

        // Track both initial creation and later changes with optional schema attributes

        t2 <- transact(schema {
          trait Foo {
            val str = oneString.doc("doc")
            val boo = oneBoolean
          }
        }).map(_.last.t)

        // valueType definition history
        _ <- Schema.t.a.valueType$.doc$.getHistory.map(_ ==> List(
          (t1, ":Foo/str", Some("string"), None),
          (t2, ":Foo/str", None, Some("doc")),
          (t2, ":Foo/boo", Some("boolean"), None),
        ))

        // Value type definitions only
        _ <- Schema.t.a.valueType.doc$.getHistory.map(_ ==> List(
          (t1, ":Foo/str", "string", None),
          (t2, ":Foo/boo", "boolean", None),
        ))
        _ <- Schema.t.a.valueType_.doc$.getHistory.map(_ ==> List(
          (t1, ":Foo/str", None),
          (t2, ":Foo/boo", None),
        ))

        // Exclude value type
        _ <- Schema.t.a.valueType$(None).doc$.getHistory.map(_ ==> List(
          (t2, ":Foo/str", None, Some("doc")),
        ))
        _ <- Schema.t.a.valueType_(None).doc$.getHistory.map(_ ==> List(
          (t2, ":Foo/str", Some("doc")),
        ))

        // Specific value type
        _ <- Schema.t.a.valueType("string").getHistory.map(_ ==> List(
          (t1, ":Foo/str", "string"),
        ))
        _ <- Schema.t.a.valueType$(Some("string")).getHistory.map(_ ==> List(
          (t1, ":Foo/str", Some("string")),
        ))
        _ <- Schema.t.a.valueType_("string").getHistory.map(_ ==> List(
          (t1, ":Foo/str"),
        ))

        // Value types except boolean
        _ <- Schema.t.a.valueType.not("boolean").getHistory.map(_ ==> List(
          (t1, ":Foo/str", "string"),
        ))
        _ <- Schema.t.a.valueType_.not("boolean").getHistory.map(_ ==> List(
          (t1, ":Foo/str"),
        ))
        // Value types except long
        _ <- Schema.t.a.valueType.not("long").getHistory.map(_ ==> List(
          (t1, ":Foo/str", "string"),
          (t2, ":Foo/boo", "boolean"),
        ))
        _ <- Schema.t.a.valueType_.not("long").getHistory.map(_ ==> List(
          (t1, ":Foo/str"),
          (t2, ":Foo/boo"),
        ))

        // Variables can be applied too
        string = "string"
        some = Some("string")
        none = None
        _ <- Schema.t.a.valueType(string).getHistory.map(_ ==> List(
          (t1, ":Foo/str", "string"),
        ))
        _ <- Schema.t.a.valueType_(string).getHistory.map(_ ==> List(
          (t1, ":Foo/str"),
        ))
        _ <- Schema.t.a.valueType$(some).getHistory.map(_ ==> List(
          (t1, ":Foo/str", Some("string")),
        ))
        _ <- Schema.t.a.valueType$(none).getHistory.map(_ ==> List(
          (t2, ":Foo/str", None),
        ))

        // Unsupported expressions for schema history
        _ <- Schema.t.a.valueType(count).getHistory
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Unsupported expression for schema history attribute `valueType`: " +
              """Generic("Schema", "valueType", "schema", Fn("count", None), "")"""
          }
      } yield ()
    }


    "valueType, types" - empty { implicit futConn =>
      for {
        _ <- transact(schema {
          trait Foo {
            val str    = oneString
            val int    = oneInt
            val long   = oneLong
            val double = oneDouble
            val bool   = oneBoolean
            val date   = oneDate
            val uuid   = oneUUID
            val uri    = oneURI
            val bigInt = oneBigInt
            val bigDec = oneBigDecimal
            val enumm  = oneEnum("enum1", "enum2")
            val bar    = one[Bar]

            val strs    = manyString
            val ints    = manyInt
            val longs   = manyLong
            val doubles = manyDouble
            val bools   = manyBoolean
            val dates   = manyDate
            val uuids   = manyUUID
            val uris    = manyURI
            val bigInts = manyBigInt
            val bigDecs = manyBigDecimal
            val enums   = manyEnum("enums1", "enums2")
            val bars    = many[Bar]

            val strMap    = mapString
            val intMap    = mapInt
            val longMap   = mapLong
            val doubleMap = mapDouble
            val boolMap   = mapBoolean
            val dateMap   = mapDate
            val uuidMap   = mapUUID
            val uriMap    = mapURI
            val bigIntMap = mapBigInt
            val bigDecMap = mapBigDecimal
          }
          trait Bar {
            // (no attributes defined)
          }
        })

        _ <- Schema.attr_("str").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("int").valueType.getHistory.map(_ ==> List("long"))
        _ <- Schema.attr_("long").valueType.getHistory.map(_ ==> List("long"))
        _ <- Schema.attr_("double").valueType.getHistory.map(_ ==> List("double"))
        _ <- Schema.attr_("bool").valueType.getHistory.map(_ ==> List("boolean"))
        _ <- Schema.attr_("date").valueType.getHistory.map(_ ==> List("instant"))
        _ <- Schema.attr_("uuid").valueType.getHistory.map(_ ==> List("uuid"))
        _ <- Schema.attr_("uri").valueType.getHistory.map(_ ==> List("uri"))
        _ <- Schema.attr_("bigInt").valueType.getHistory.map(_ ==> List("bigint"))
        _ <- Schema.attr_("bigDec").valueType.getHistory.map(_ ==> List("bigdec"))
        _ <- Schema.attr_("enumm").valueType.getHistory.map(_ ==> List("ref"))
        _ <- Schema.attr_("bar").valueType.getHistory.map(_ ==> List("ref"))

        _ <- Schema.attr_("strs").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("ints").valueType.getHistory.map(_ ==> List("long"))
        _ <- Schema.attr_("longs").valueType.getHistory.map(_ ==> List("long"))
        _ <- Schema.attr_("doubles").valueType.getHistory.map(_ ==> List("double"))
        _ <- Schema.attr_("bools").valueType.getHistory.map(_ ==> List("boolean"))
        _ <- Schema.attr_("dates").valueType.getHistory.map(_ ==> List("instant"))
        _ <- Schema.attr_("uuids").valueType.getHistory.map(_ ==> List("uuid"))
        _ <- Schema.attr_("uris").valueType.getHistory.map(_ ==> List("uri"))
        _ <- Schema.attr_("bigInts").valueType.getHistory.map(_ ==> List("bigint"))
        _ <- Schema.attr_("bigDecs").valueType.getHistory.map(_ ==> List("bigdec"))
        _ <- Schema.attr_("enums").valueType.getHistory.map(_ ==> List("ref"))
        _ <- Schema.attr_("bars").valueType.getHistory.map(_ ==> List("ref"))

        _ <- Schema.attr_("strMap").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("intMap").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("longMap").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("doubleMap").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("boolMap").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("dateMap").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("uuidMap").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("uriMap").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("bigIntMap").valueType.getHistory.map(_ ==> List("string"))
        _ <- Schema.attr_("bigDecMap").valueType.getHistory.map(_ ==> List("string"))
      } yield ()
    }


    "cardinality" - {

      "one to many" - empty { implicit futConn =>
        for {
          conn <- futConn

          // Card-one attribute
          t1 <- transact(schema {
            trait Foo {
              val int = oneInt
            }
          }).map(_.last.t)

          _ <- Schema.t.a.cardinality.get.map(_ ==> List(
            (t1, ":Foo/int", "one"),
          ))

          // Add single value to card-one attribute
          e <- conn.transact("""[[:db/add "-1" :Foo/int 1]]""").map(_.eid)
          _ <- conn.query("[:find ?e ?v :where [?e :Foo/int ?v]]")
            .map(_ ==> List(List(e, 1)))

          // Adding a new value to the same entity retracts the old one, confirming cardinality-one
          _ <- conn.transact(s"[[:db/add $e :Foo/int 2]]")
          _ <- conn.query(s"[:find ?e ?v :where [$e :Foo/int ?v][(ground $e) ?e]]")
            .map(_ ==> List(List(e, 2)))

          // Change cardinality to "many"
          t4 <- transact(schema {
            trait Foo {
              val int = manyInt
            }
          }).map(_.last.t)


          // Adding a new value to the same entity adds it when the attribute is cardinality-many
          _ <- conn.transact(s"[[:db/add $e :Foo/int 3]]")

          // Multiple values per entity
          _ <- conn.query(s"[:find ?e (distinct ?v) :where [$e :Foo/int ?v][(ground $e) ?e]]")
            .map(_ ==> List(List(e, Set(2, 3))))

          // Current cardinalities
          _ <- Schema.t.a.cardinality.get.map(_ ==> List(
            (t1, ":Foo/int", "many"),
          ))

          // cardinality definition history
          _ <- Schema.t.a.cardinality.getHistory.map(_ ==> List(
            (t1, ":Foo/int", "one"),
            (t4, ":Foo/int", "many"),
          ))

          // Specific cardinality
          _ <- Schema.t.a.cardinality("one").getHistory.map(_ ==> List(
            (t1, ":Foo/int", "one"),
          ))
          _ <- Schema.t.a.cardinality_("one").getHistory.map(_ ==> List(
            (t1, ":Foo/int"),
          ))

          // Cardinalities except "many"
          _ <- Schema.t.a.cardinality.not("many").getHistory.map(_ ==> List(
            (t1, ":Foo/int", "one"),
          ))
          _ <- Schema.t.a.cardinality_.not("many").getHistory.map(_ ==> List(
            (t1, ":Foo/int"),
          ))

          // Add some non-cardinality schema change
          t6 <- transact(schema {
            trait Foo {
              val int = manyInt.doc("hello")
            }
          }).map(_.last.t)

          // cardinality definition history
          _ <- Schema.t.a.cardinality$.doc$.getHistory.map(_ ==> List(
            (t1, ":Foo/int", Some("one"), None),
            (t4, ":Foo/int", Some("many"), None),
            (t6, ":Foo/int", None, Some("hello")), // no cardinality change when doc was added
          ))

          // Cardinality definitions only
          _ <- Schema.t.a.cardinality.getHistory.map(_ ==> List(
            (t1, ":Foo/int", "one"),
            (t4, ":Foo/int", "many"),
          ))
          _ <- Schema.t.a.cardinality_.getHistory.map(_ ==> List(
            (t1, ":Foo/int"),
            (t4, ":Foo/int"),
          ))

          // Exclude cardinality
          _ <- Schema.t.a.cardinality$(None).getHistory.map(_ ==> List(
            (t6, ":Foo/int", None)
          ))
          _ <- Schema.t.a.cardinality_(None).getHistory.map(_ ==> List(
            (t6, ":Foo/int")
          ))

          // Specific cardinality
          _ <- Schema.t.a.cardinality("one").getHistory.map(_ ==> List(
            (t1, ":Foo/int", "one")
          ))
          _ <- Schema.t.a.cardinality$(Some("one")).getHistory.map(_ ==> List(
            (t1, ":Foo/int", Some("one"))
          ))
          _ <- Schema.t.a.cardinality_("one").getHistory.map(_ ==> List(
            (t1, ":Foo/int")
          ))

          // Cardinalities except "many"
          _ <- Schema.t.a.cardinality.not("many").getHistory.map(_ ==> List(
            (t1, ":Foo/int", "one")
          ))
          _ <- Schema.t.a.cardinality_.not("many").getHistory.map(_ ==> List(
            (t1, ":Foo/int")
          ))
        } yield ()
      }

      "many to one" - empty { implicit futConn =>
        for {
          conn <- futConn

          // Card-one attribute
          t1 <- transact(schema {
            trait Foo {
              val bar = manyInt
              val baz = manyInt
            }
          }).map(_.last.t)

          _ <- Schema.t.a.a1.cardinality.get.map(_ ==> List(
            (t1, ":Foo/bar", "many"),
            (t1, ":Foo/baz", "many"),
          ))

          // Add 1 value for bar and 2 values for baz
          txr2 <- conn.transact(
            """[
              |  [:db/add "-1" :Foo/bar 1]
              |  [:db/add "-2" :Foo/baz 2]
              |  [:db/add "-2" :Foo/baz 3]
              |]""".stripMargin)
          tx2 = txr2.tx
          List(e1, e2) = txr2.eids

          _ <- conn.query("[:find (distinct ?b) :where [?a :Foo/bar ?b]]")
            .map(_ ==> List(List(Set(1))))

          _ <- conn.query("[:find (distinct ?b) :where [?a :Foo/baz ?b]]")
            .map(_ ==> List(List(Set(2, 3))))

          // Change cardinality to one for `bar` - ok if attribute has only
          // 1 value for any entity
          t4 <- transact(schema {
            trait Foo {
              val bar = oneInt
              val baz = manyInt
            }
          }).map(_.last.t)

          // baz now has cardinality one
          _ <- Schema.t.a.a1.cardinality.get.map(_ ==> List(
            (t1, ":Foo/bar", "one"),
            (t1, ":Foo/baz", "many"),
          ))

          // Change cardinality to one for `baz` - rejected if attribute has multiple
          // values for any entity
          _ <- transact(schema {
            trait Foo {
              val bar = oneInt
              val baz = oneInt
            }
          }).map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              if (system == SystemPeer)
                msg ==> ":db.error/invalid-alter-attribute Error: " +
                  "{:db/error :db.error/cardinality-violation, " +
                  ":datoms [" +
                  s"#datom[$e2 73 2 $tx2 true] " +
                  s"#datom[$e2 73 3 $tx2 true]]}"
              else {
                msg ==> "Error: {" +
                  ":db/error :db.error/cardinality-violation, " +
                  ":datoms [" +
                  s"#datom[$e2 74 2 $tx2 true] " +
                  s"#datom[$e2 74 3 $tx2 true]]}"
              }
            }

          // Retract values so that `baz` has only one value per entity
          _ <- conn.transact(s"[[:db/retract $e2 :Foo/baz 3]]")

          _ <- conn.query("[:find (distinct ?b) :where [?a :Foo/baz ?b]]")
            .map(_ ==> List(List(Set(2))))

          // Now we can change baz to cardinality one (and add a non-cardinality definition)
          t6 <- transact(schema {
            trait Foo {
              val bar = oneInt
              val baz = oneInt
            }
          }).map(_.last.t)

          // Both attributes now has cardinality one
          _ <- Schema.t.a.a1.cardinality.get.map(_ ==> List(
            (t1, ":Foo/bar", "one"),
            (t1, ":Foo/baz", "one"),
          ))

          // cardinality definition history
          _ <- Schema.t.a.cardinality.getHistory.map(_ ==> List(
            (t1, ":Foo/bar", "many"),
            (t1, ":Foo/baz", "many"),
            (t4, ":Foo/bar", "one"),
            (t6, ":Foo/baz", "one"),
          ))

          // Add some non-cardinality schema change
          t7 <- transact(schema {
            trait Foo {
              val bar = oneInt
              val baz = oneInt.doc("hello")
            }
          }).map(_.last.t)

          // cardinality definition history
          _ <- Schema.t.a.cardinality$.doc$.getHistory.map(_ ==> List(
            (t1, ":Foo/bar", Some("many"), None),
            (t1, ":Foo/baz", Some("many"), None),
            (t4, ":Foo/bar", Some("one"), None),
            (t6, ":Foo/baz", Some("one"), None),
            (t7, ":Foo/baz", None, Some("hello")),
          ))

          // Cardinality definitions only
          _ <- Schema.t.a.cardinality.getHistory.map(_ ==> List(
            (t1, ":Foo/bar", "many"),
            (t1, ":Foo/baz", "many"),
            (t4, ":Foo/bar", "one"),
            (t6, ":Foo/baz", "one"),
          ))
          _ <- Schema.t.a.cardinality_.getHistory.map(_ ==> List(
            (t1, ":Foo/bar"),
            (t1, ":Foo/baz"),
            (t4, ":Foo/bar"),
            (t6, ":Foo/baz"),
          ))

          // Exclude cardinality
          _ <- Schema.t.a.cardinality$(None).getHistory.map(_ ==> List(
            (t7, ":Foo/baz", None),
          ))
          _ <- Schema.t.a.cardinality_(None).getHistory.map(_ ==> List(
            (t7, ":Foo/baz"),
          ))

          // Specific cardinality
          _ <- Schema.t.a.cardinality("one").getHistory.map(_ ==> List(
            (t4, ":Foo/bar", "one"),
            (t6, ":Foo/baz", "one"),
          ))
          _ <- Schema.t.a.cardinality$(Some("one")).getHistory.map(_ ==> List(
            (t4, ":Foo/bar", Some("one")),
            (t6, ":Foo/baz", Some("one")),
          ))
          _ <- Schema.t.a.cardinality_("one").getHistory.map(_ ==> List(
            (t4, ":Foo/bar"),
            (t6, ":Foo/baz"),
          ))

          // Cardinalities except "many"
          _ <- Schema.t.a.cardinality.not("many").getHistory.map(_ ==> List(
            (t4, ":Foo/bar", "one"),
            (t6, ":Foo/baz", "one"),
          ))
          _ <- Schema.t.a.cardinality_.not("many").getHistory.map(_ ==> List(
            (t4, ":Foo/bar"),
            (t6, ":Foo/baz"),
          ))
        } yield ()
      }
    }


    "doc" - empty { implicit futConn =>
      // :db/doc specifies a documentation string for an attribute definition.

      for {
        conn <- futConn

        // Initial attribute without documentation string
        t1 <- transact(schema {
          trait Foo {
            val int = oneInt
          }
        }).map(_.last.t)

        _ <- Schema.t.a.doc$.get.map(_ ==> List(
          (t1, ":Foo/int", None),
        ))

        // Add documentation string
        t2 <- transact(schema {
          trait Foo {
            val int = oneInt.doc("hello")
          }
        }).map(_.last.t)

        _ <- Schema.t.a.doc$.get.map(_ ==> List(
          (t1, ":Foo/int", Some("hello")),
        ))
        _ <- Schema.t.a.doc$.getHistory.map(_ ==> List(
          (t1, ":Foo/int", None),
          (t2, ":Foo/int", Some("hello")),
        ))

        // Update documentation string
        t3 <- transact(schema {
          trait Foo {
            val int = oneInt.doc("hello world")
          }
        }).map(_.last.t)

        _ <- Schema.t.a.doc$.get.map(_ ==> List(
          (t1, ":Foo/int", Some("hello world")),
        ))
        _ <- Schema.t.a.doc$.getHistory.map(_ ==> List(
          (t1, ":Foo/int", None),
          (t2, ":Foo/int", Some("hello")),
          (t3, ":Foo/int", Some("hello world")),
        ))

        // Retract documentation string
        t4 <- conn.retractSchemaOption(":Foo/int", "doc").map(_.t)
        _ <- transact(schema {
          trait Foo {
            val int = oneInt
          }
        })

        // Current doc definition
        _ <- Schema.t.a.doc$.get.map(_ ==> List(
          (t1, ":Foo/int", None),
        ))

        // doc definition history
        _ <- Schema.t.a.doc$.getHistory.map(_ ==> List(
          (t1, ":Foo/int", None),
          (t2, ":Foo/int", Some("hello")),
          (t3, ":Foo/int", Some("hello world")),
          (t4, ":Foo/int", None),
        ))

        // doc definitions only
        _ <- Schema.t.a.doc.getHistory.map(_ ==> List(
          (t2, ":Foo/int", "hello"),
          (t3, ":Foo/int", "hello world"),
        ))
        _ <- Schema.t.a.doc_.getHistory.map(_ ==> List(
          (t2, ":Foo/int"),
          (t3, ":Foo/int"),
        ))

        // Exclude doc value
        _ <- Schema.t.a.doc$(None).getHistory.map(_ ==> List(
          (t1, ":Foo/int", None),
          (t4, ":Foo/int", None),
        ))
        _ <- Schema.t.a.doc_(None).getHistory.map(_ ==> List(
          (t1, ":Foo/int"),
          (t4, ":Foo/int"),
        ))

        // Specific doc text
        _ <- Schema.t.a.doc("hello").getHistory.map(_ ==> List(
          (t2, ":Foo/int", "hello"),
        ))
        _ <- Schema.t.a.doc$(Some("hello")).getHistory.map(_ ==> List(
          (t2, ":Foo/int", Some("hello")),
        ))
        _ <- Schema.t.a.doc_("hello").getHistory.map(_ ==> List(
          (t2, ":Foo/int"),
        ))

        // docs texts except "hello world"
        _ <- Schema.t.a.doc.not("hello world").getHistory.map(_ ==> List(
          (t2, ":Foo/int", "hello"),
        ))
        _ <- Schema.t.a.doc_.not("hello world").getHistory.map(_ ==> List(
          (t2, ":Foo/int"),
        ))

        // fulltext search not supported with doc texts when querying for schema history
        _ <- Schema.t.a.doc.contains("hello").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Unsupported expression for schema history attribute `doc`: " +
              """Generic("Schema", "doc", "schema", Fulltext(List(hello)), "")"""
          }

        // retractSchemaOption checks

        // Invalid attribute name
        _ <- conn.retractSchemaOption("Foo/int", "doc")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Invalid attribute name `Foo/int`. " +
              "Expecting attribute name in the format `:<Ns>/<attr>` or `:<part_Ns>/<attr>`"
          }

        // Invalid option name
        _ <- conn.retractSchemaOption(":Foo/int", "docs")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Can't retract option 'docs' for attribute `:Foo/int`. " +
              "Only the following options can be retracted: " +
              "doc, unique, isComponent, noHistory, index."
          }

        // Non-existing attribute
        _ <- conn.retractSchemaOption(":Foo/bar", "doc")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Couldn't find attribute `:Foo/bar` in the database."
          }

        // Option has no value
        _ <- conn.retractSchemaOption(":Foo/int", "doc")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "'doc' option of attribute :Foo/int has no value."
          }
      } yield ()
    }


    "unique" - {
      /*
      :db/unique - specifies a uniqueness constraint for the values of an attribute.
      Setting an attribute :db/unique also implies :db/index. The values allowed
      for :db/unique are:

      :db.unique/value - only one entity can have a given value for this attribute.
        Attempts to assert a duplicate value for the same attribute for a different
        entity id will fail. More documentation on unique values is available here.

      :db.unique/identity - only one entity can have a given value for this attribute
        and "upsert" is enabled; attempts to insert a duplicate value for a temporary
        entity id will cause all attributes associated with that temporary id to be
        merged with the entity already in the database. More documentation on unique
        identities is available here.

      :db/unique defaults to nil.

      See:
      https://docs.datomic.com/on-prem/schema/schema.html#operational-schema-attributes
      https://docs.datomic.com/on-prem/schema/identity.html#unique-values
      https://docs.datomic.com/on-prem/schema/identity.html#unique-identities
       */

      "value" - empty { implicit futConn =>
        for {
          conn <- futConn

          // Initial attribute
          t1 <- transact(schema {
            trait Foo {
              val bar = oneInt
            }
          }).map(_.last.t)

          _ <- Schema.t.a.unique$.get.map(_ ==> List(
            (t1, ":Foo/bar", None),
          ))

          // Add two entities with duplicate data for :Foo/bar
          txr2 <- conn.transact(
            """[
              |  [:db/add "-1" :Foo/bar 1]
              |  [:db/add "-2" :Foo/bar 1]
              |]""".stripMargin)
          tx2 = txr2.tx
          List(e1, e2) = txr2.eids

          // :Foo/bar has duplicate values
          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
            .map(res =>
              if (system == SystemPeer)
                res ==> List(
                  List(e2, 1),
                  List(e1, 1),
                )
              else
                res ==> List(
                  List(e1, 1),
                  List(e2, 1),
                )
            )

          // Enforcing unique values on bar having duplicate values will be rejected
          _ <- transact(schema {
            trait Foo {
              val bar = oneInt.uniqueValue
            }
          }).map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              if (system == SystemPeer)
                msg ==> ":db.error/invalid-alter-attribute Error: {" +
                  ":db/error :db.error/unique-violation, " +
                  ":datoms [" +
                  s"#datom[$e1 72 1 $tx2 true] " +
                  s"#datom[$e2 72 1 $tx2 true]]}"
              else
                msg ==> "Error: {:db/error :db.error/unique-violation, " +
                  ":datoms [" +
                  s"#datom[$e1 73 1 $tx2 true] " +
                  s"#datom[$e2 73 1 $tx2 true]]}"
            }

          // Remove duplicate values of :Foo/bar
          _ <- conn.transact(s"[[:db/retract $e2 :Foo/bar 1]]")

          // `bar` now has no duplicate values
          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
            .map(_ ==> List(
              List(e1, 1)
            ))

          // Now we can add the unique value constraint on :Foo/bar
          t5 <- transact(schema {
            trait Foo {
              val bar = oneInt.uniqueValue
            }
          }).map(_.last.t)

          _ <- Schema.t.a.unique$.get.map(_ ==> List(
            (t1, ":Foo/bar", Some("value")),
          ))
          _ <- Schema.t.a.unique$.getHistory.map(_ ==> List(
            (t1, ":Foo/bar", None),
            (t5, ":Foo/bar", Some("value")),
          ))

          // Now we are not allowed to add duplicate values.
          // This is where unique value/identity differ:
          // uniqueIdentity would allow upserting the value
          // (and no entity would be created since the value already exists)
          _ <- conn.transact("""[[:db/add "-3" :Foo/bar 1]]""")
            .map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              if (system == SystemPeer)
                msg.take(51) ==> ":db.error/unique-conflict Unique conflict: :Foo/bar"
              else
                msg.take(25) ==> "Unique conflict: :Foo/bar"
            }

          // If we remove the constraint we can add duplicate values again
          t6 <- conn.retractSchemaOption(":Foo/bar", "unique").map(_.t)

          // Note that simply removing `uniqueValue` from the attribute definition
          // will not retract the constraint
          _ <- transact(schema {
            trait Foo {
              val bar = oneInt
            }
          })

          _ <- Schema.t.a.unique$.get.map(_ ==> List(
            (t1, ":Foo/bar", None),
          ))

          // unique definition history
          _ <- Schema.t.a.unique$.getHistory.map(_ ==> List(
            (t1, ":Foo/bar", None),
            (t5, ":Foo/bar", Some("value")),
            (t6, ":Foo/bar", None),
          ))

          e3 <- conn.transact("""[[:db/add "-3" :Foo/bar 1]]""").map(_.eid)

          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
            .map(res =>
              if (system == SystemPeer)
                res ==> List(
                  List(e3, 1),
                  List(e1, 1),
                )
              else
                res ==> List(
                  List(e1, 1),
                  List(e3, 1),
                )
            )

          // Unique value definitions only
          _ <- Schema.t.a.unique.getHistory.map(_ ==> List(
            (t5, ":Foo/bar", "value"),
          ))
          _ <- Schema.t.a.unique_.getHistory.map(_ ==> List(
            (t5, ":Foo/bar"),
          ))

          // Exclude unique values
          _ <- Schema.t.a.unique$(None).getHistory.map(_ ==> List(
            (t1, ":Foo/bar", None),
            (t6, ":Foo/bar", None),
          ))
          _ <- Schema.t.a.unique_(None).getHistory.map(_ ==> List(
            (t1, ":Foo/bar"),
            (t6, ":Foo/bar"),
          ))

          // Specific unique value
          _ <- Schema.t.a.unique("value").getHistory.map(_ ==> List(
            (t5, ":Foo/bar", "value"),
          ))
          _ <- Schema.t.a.unique$(Some("value")).getHistory.map(_ ==> List(
            (t5, ":Foo/bar", Some("value")),
          ))
          _ <- Schema.t.a.unique_("value").getHistory.map(_ ==> List(
            (t5, ":Foo/bar"),
          ))

          // Unique values except "identity"
          _ <- Schema.t.a.unique.not("identity").getHistory.map(_ ==> List(
            (t5, ":Foo/bar", "value"),
          ))
          _ <- Schema.t.a.unique_.not("identity").getHistory.map(_ ==> List(
            (t5, ":Foo/bar"),
          ))

        } yield ()
      }

      "identity" - empty { implicit futConn =>
        for {
          conn <- futConn

          // Initial attribute
          t1 <- transact(schema {
            trait Foo {
              val bar = oneInt
            }
          }).map(_.last.t)

          _ <- Schema.t.a.unique$.get.map(_ ==> List(
            (t1, ":Foo/bar", None),
          ))

          // Add two entities with duplicate data for :Foo/bar
          txr2 <- conn.transact(
            """[
              |  [:db/add "-1" :Foo/bar 1]
              |  [:db/add "-2" :Foo/bar 1]
              |]""".stripMargin)
          tx2 = txr2.tx
          List(e1, e2) = txr2.eids

          // `bar` has duplicate values
          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
            .map(res =>
              if (system == SystemPeer)
                res ==> List(
                  List(e2, 1),
                  List(e1, 1),
                )
              else
                res ==> List(
                  List(e1, 1),
                  List(e2, 1),
                )
            )

          // Enforcing unique values on :Foo/bar having duplicate values will be rejected
          _ <- transact(schema {
            trait Foo {
              val bar = oneInt.uniqueIdentity
            }
          }).map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              if (system == SystemPeer)
                msg ==> ":db.error/invalid-alter-attribute Error: {" +
                  ":db/error :db.error/unique-violation, " +
                  ":datoms [" +
                  s"#datom[$e1 72 1 $tx2 true] " +
                  s"#datom[$e2 72 1 $tx2 true]]}"
              else
                msg ==> "Error: {:db/error :db.error/unique-violation, " +
                  ":datoms [" +
                  s"#datom[$e1 73 1 $tx2 true] " +
                  s"#datom[$e2 73 1 $tx2 true]]}"
            }

          // Remove duplicate values of :Foo/bar
          _ <- conn.transact(s"[[:db/retract $e2 :Foo/bar 1]]")

          // :Foo/bar now has no duplicate values
          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
            .map(_ ==> List(
              List(e1, 1)
            ))

          // Now we can add the unique identity constraint on :Foo/bar
          t5 <- transact(schema {
            trait Foo {
              val bar = oneInt.uniqueIdentity
            }
          }).map(_.last.t)

          _ <- Schema.t.a.unique$.get.map(_ ==> List(
            (t1, ":Foo/bar", Some("identity")),
          ))
          _ <- Schema.t.a.unique$.getHistory.map(_ ==> List(
            (t1, ":Foo/bar", None),
            (t5, ":Foo/bar", Some("identity")),
          ))

          // Since entity e1 already has `1` asserted and "upsert" behaviour is
          // enabled with unique identity, no new entity is created when trying to assert
          // `1` again.
          // This is where unique value/identity differ:
          // uniqueValue would reject transacting the redundant value
          _ <- conn.transact("""[[:db/add "-3" :Foo/bar 1]]""").map(_.eids ==> Nil)

          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
            .map(_ ==> List(
              List(e1, 1),
            ))

          // If we remove the constraint we can add duplicate values again
          t7 <- conn.retractSchemaOption(":Foo/bar", "unique").map(_.t)
          // Note that simply removing `uniqueIdentity` from the attribute definition
          // will not retract the constraint

          _ <- transact(schema {
            trait Foo {
              val bar = oneInt
            }
          })

          _ <- Schema.t.a.unique$.get.map(_ ==> List(
            (t1, ":Foo/bar", None),
          ))

          // unique definition history
          _ <- Schema.t.a.unique$.getHistory.map(_ ==> List(
            (t1, ":Foo/bar", None),
            (t5, ":Foo/bar", Some("identity")),
            (t7, ":Foo/bar", None),
          ))

          e3 <- conn.transact("""[[:db/add "-3" :Foo/bar 1]]""").map(_.eid)

          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
            .map(res =>
              if (system == SystemPeer)
                res ==> List(
                  List(e3, 1),
                  List(e1, 1),
                )
              else
                res ==> List(
                  List(e1, 1),
                  List(e3, 1),
                )
            )

          // Unique value definitions only
          _ <- Schema.t.a.unique.getHistory.map(_ ==> List(
            (t5, ":Foo/bar", "identity"),
          ))
          _ <- Schema.t.a.unique_.getHistory.map(_ ==> List(
            (t5, ":Foo/bar"),
          ))

          // Exclude unique values
          _ <- Schema.t.a.unique$(None).getHistory.map(_ ==> List(
            (t1, ":Foo/bar", None),
            (t7, ":Foo/bar", None),
          ))
          _ <- Schema.t.a.unique_(None).getHistory.map(_ ==> List(
            (t1, ":Foo/bar"),
            (t7, ":Foo/bar"),
          ))

          // Specific unique value
          _ <- Schema.t.a.unique("identity").getHistory.map(_ ==> List(
            (t5, ":Foo/bar", "identity"),
          ))
          _ <- Schema.t.a.unique$(Some("identity")).getHistory.map(_ ==> List(
            (t5, ":Foo/bar", Some("identity")),
          ))
          _ <- Schema.t.a.unique_("identity").getHistory.map(_ ==> List(
            (t5, ":Foo/bar"),
          ))

          // Unique values except "value"
          _ <- Schema.t.a.unique.not("value").getHistory.map(_ ==> List(
            (t5, ":Foo/bar", "identity"),
          ))
          _ <- Schema.t.a.unique_.not("value").getHistory.map(_ ==> List(
            (t5, ":Foo/bar"),
          ))
        } yield ()
      }
    }


    "isComponent" - empty { implicit futConn =>
      /*
      :db/isComponent specifies a boolean value indicating that an attribute
      whose type is :db.type/ref refers to a subcomponent of the entity to which
      the attribute is applied. When you retract an entity with :db.fn/retractEntity,
      all subcomponents are also retracted. When you touch an entity, all its
      subcomponent entities are touched recursively. Defaults to false.
       */
      for {
        conn <- futConn

        // Non-component ref
        t1 <- transact(schema {
          trait Foo {
            val bar = one[Bar]
          }
          trait Bar {
            val int = oneInt
          }
        }).map(_.last.t)

        _ <- Schema.t.a.isComponent$.get.map(_ ==> List(
          (t1, ":Foo/bar", None),
          (t1, ":Bar/int", None),
        ))

        // Add ref data
        List(e, ref) <- conn.transact(
          """[
            |  [:db/add "-1" :Foo/bar "-2"]
            |  [:db/add "-2" :Bar/int 1]
            |]""".stripMargin).map(_.eids)

        // When retracting a non-component ref, the referenced data is not affected
        _ <- conn.transact(s"[[:db/retractEntity $e]]")
        _ <- conn.query("[:find ?b :where [?a :Bar/int ?b]]")
          .map(_ ==> List(List(1)))

        // Now make the bar reference a component reference
        t5 <- transact(schema {
          trait Foo {
            val bar = one[Bar].isComponent
          }
          trait Bar {
            val int = oneInt
          }
        }).map(_.last.t)
        _ <- Schema.t.a.isComponent$.get.map(_ ==> List(
          (t1, ":Foo/bar", Some(true)),
          (t1, ":Bar/int", None),
        ))

        // Add component ref to :Bar/int again
        e2 <- conn.transact(s"""[[:db/add "-3" :Foo/bar $ref]]""").map(_.eid)

        // When retracting a component ref, the referenced data is now retracted too
        _ <- conn.transact(s"[[:db/retractEntity $e2]]")
        _ <- conn.query("[:find ?b :where [?a :Bar/int ?b]]").map(_ ==> Nil)

        // Make :Foo/bar a non-component ref again
        t9 <- conn.retractSchemaOption(":Foo/bar", "isComponent").map(_.t)
        _ <- Schema.t.a.isComponent$.get.map(_ ==> List(
          (t1, ":Foo/bar", None),
          (t1, ":Bar/int", None),
        ))

        // Add non-component ref to :Bar/int again
        List(e3, _) <- conn.transact(
          """[
            |  [:db/add "-1" :Foo/bar "-2"]
            |  [:db/add "-2" :Bar/int 3]
            |]""".stripMargin).map(_.eids)

        // When retracting a non-component ref, the referenced data is not affected
        // (as in the beginning of the test)
        _ <- conn.transact(s"[[:db/retractEntity $e3]]")
        _ <- conn.query("[:find ?b :where [?a :Bar/int ?b]]")
          .map(_ ==> List(List(3)))

        // isComponent definition history
        _ <- Schema.t.a.isComponent$.getHistory.map(_ ==> List(
          (t1, ":Foo/bar", None),
          (t1, ":Bar/int", None),
          (t5, ":Foo/bar", Some(true)),
          (t9, ":Foo/bar", None),
        ))

        // isComponent definitions only
        _ <- Schema.t.a.isComponent.getHistory.map(_ ==> List(
          (t5, ":Foo/bar", true),
        ))
        _ <- Schema.t.a.isComponent_.getHistory.map(_ ==> List(
          (t5, ":Foo/bar"),
        ))

        // Exclude isComponent definitions
        _ <- Schema.t.a.isComponent$(None).getHistory.map(_ ==> List(
          (t1, ":Foo/bar", None),
          (t1, ":Bar/int", None),
          (t9, ":Foo/bar", None),
        ))
        _ <- Schema.t.a.isComponent_(None).getHistory.map(_ ==> List(
          (t1, ":Foo/bar"),
          (t1, ":Bar/int"),
          (t9, ":Foo/bar"),
        ))

        // Specific isComponent value
        _ <- Schema.t.a.isComponent(true).getHistory.map(_ ==> List(
          (t5, ":Foo/bar", true),
        ))
        _ <- Schema.t.a.isComponent$(Some(true)).getHistory.map(_ ==> List(
          (t5, ":Foo/bar", Some(true)),
        ))
        _ <- Schema.t.a.isComponent_(true).getHistory.map(_ ==> List(
          (t5, ":Foo/bar"),
        ))

        // isComponent values except "false"
        _ <- Schema.t.a.isComponent.not(false).getHistory.map(_ ==> List(
          (t5, ":Foo/bar", true),
        ))
        _ <- Schema.t.a.isComponent_.not(false).getHistory.map(_ ==> List(
          (t5, ":Foo/bar"),
        ))
      } yield ()
    }


    "noHistory" - empty { implicit futConn =>
      /*
      :db/noHistory specifies a boolean value indicating whether past values of
      an attribute should not be retained. Defaults to false.

      The purpose of :db/noHistory is to conserve storage, not to make semantic
      guarantees about removing information. The effect of :db/noHistory happens
      in the background, and some amount of history may be visible even for
      attributes with :db/noHistory set to true.

      Therefore we simply show below how to turn the option on and off.
       */
      for {
        conn <- futConn

        // Initial attribute with history recorded as default
        t1 <- transact(schema {
          trait Foo {
            val int = oneInt
          }
        }).map(_.last.t)

        _ <- Schema.t.a.noHistory$.get.map(_ ==> List(
          (t1, ":Foo/int", None),
        ))

        // Stop retaining values for Foo/int
        t2 <- transact(schema {
          trait Foo {
            val int = oneInt.noHistory
          }
        }).map(_.last.t)

        _ <- Schema.t.a.noHistory$.get.map(_ ==> List(
          (t1, ":Foo/int", Some(true)),
        ))

        // Re-retain values for Foo/int
        t3 <- conn.retractSchemaOption(":Foo/int", "noHistory").map(_.t)

        _ <- Schema.t.a.noHistory$.get.map(_ ==> List(
          (t1, ":Foo/int", None),
        ))

        // noHistory definition history
        _ <- Schema.t.a.noHistory$.getHistory.map(_ ==> List(
          (t1, ":Foo/int", None),
          (t2, ":Foo/int", Some(true)),
          (t3, ":Foo/int", None),
        ))

        // noHistory definitions only
        _ <- Schema.t.a.noHistory.getHistory.map(_ ==> List(
          (t2, ":Foo/int", true),
        ))
        _ <- Schema.t.a.noHistory_.getHistory.map(_ ==> List(
          (t2, ":Foo/int"),
        ))

        // Exclude noHistory definitions
        _ <- Schema.t.a.noHistory$(None).getHistory.map(_ ==> List(
          (t1, ":Foo/int", None),
          (t3, ":Foo/int", None),
        ))
        _ <- Schema.t.a.noHistory_(None).getHistory.map(_ ==> List(
          (t1, ":Foo/int"),
          (t3, ":Foo/int"),
        ))

        // Specific noHistory value
        _ <- Schema.t.a.noHistory(true).getHistory.map(_ ==> List(
          (t2, ":Foo/int", true),
        ))
        _ <- Schema.t.a.noHistory$(Some(true)).getHistory.map(_ ==> List(
          (t2, ":Foo/int", Some(true)),
        ))
        _ <- Schema.t.a.noHistory_(true).getHistory.map(_ ==> List(
          (t2, ":Foo/int"),
        ))

        // noHistory values except "false"
        _ <- Schema.t.a.noHistory.not(false).getHistory.map(_ ==> List(
          (t2, ":Foo/int", true),
        ))
        _ <- Schema.t.a.noHistory_.not(false).getHistory.map(_ ==> List(
          (t2, ":Foo/int"),
        ))
      } yield ()
    }


    "index, Peer" - empty { implicit futConn =>
      if (system == SystemPeer) {
        /*
        :db/index specifies a boolean value indicating that an index should be
        generated for this attribute. Defaults to false.

        In Molecule the indexing is turned on by default.
        Like with the noHistory option we simply show how to turn it on and off.
         */
        for {
          conn <- futConn

          // In Molecule indexing on the Peer is turned on by default without having to
          // add `index` to the attribute definition
          t1 <- transact(schema {
            trait Foo {
              val int = oneInt
            }
          }).map(_.last.t)

          _ <- Schema.t.a.index$.get.map(_ ==> List(
            (t1, ":Foo/int", Some(true))
          ))

          // Stop indexing values for Foo/int
          t2 <- conn.retractSchemaOption(":Foo/int", "index").map(_.t)

          _ <- Schema.t.a.noHistory$.get.map(_ ==> List(
            (t1, ":Foo/int", None),
          ))

          // Index attribute values again
          t3 <- transact(schema {
            trait Foo {
              val int = oneInt.index
            }
          }).map(_.last.t)

          _ <- Schema.t.a.index$.get.map(_ ==> List(
            (t1, ":Foo/int", Some(true)),
          ))

          // index definition history
          _ <- Schema.t.a.index$.getHistory.map(_ ==> List(
            (t1, ":Foo/int", Some(true)),
            (t2, ":Foo/int", None),
            (t3, ":Foo/int", Some(true)),
          ))

          // index definitions only
          _ <- Schema.t.a.index.getHistory.map(_ ==> List(
            (t1, ":Foo/int", true),
            (t3, ":Foo/int", true),
          ))
          _ <- Schema.t.a.index_.getHistory.map(_ ==> List(
            (t1, ":Foo/int"),
            (t3, ":Foo/int"),
          ))

          // Exclude index definitions
          _ <- Schema.t.a.index$(None).getHistory.map(_ ==> List(
            (t2, ":Foo/int", None),
          ))
          _ <- Schema.t.a.index_(None).getHistory.map(_ ==> List(
            (t2, ":Foo/int"),
          ))

          // Specific index value
          _ <- Schema.t.a.index(true).getHistory.map(_ ==> List(
            (t1, ":Foo/int", true),
            (t3, ":Foo/int", true),
          ))
          _ <- Schema.t.a.index$(Some(true)).getHistory.map(_ ==> List(
            (t1, ":Foo/int", Some(true)),
            (t3, ":Foo/int", Some(true)),
          ))
          _ <- Schema.t.a.index_(true).getHistory.map(_ ==> List(
            (t1, ":Foo/int"),
            (t3, ":Foo/int"),
          ))

          // index values except "false"
          _ <- Schema.t.a.index.not(false).getHistory.map(_ ==> List(
            (t1, ":Foo/int", true),
            (t3, ":Foo/int", true),
          ))
          _ <- Schema.t.a.index_.not(false).getHistory.map(_ ==> List(
            (t1, ":Foo/int"),
            (t3, ":Foo/int"),
          ))
        } yield ()
      }
    }


    "index, Client" - empty { implicit futConn =>
      if (system != SystemPeer) {
        /*
        All attributes are indexed by default on the Client and there is no indexing choice.
         */
        for {
          t <- transact(schema {
            trait Foo {
              val int = oneInt
            }
          }).map(_.last.t)

          // Querying for the index option will return None since no index option is asserted
          _ <- Schema.t.a.index$.get.map(res =>
            res ==> List((t, ":Foo/int", None))
          )
        } yield ()
      }
    }

    "fulltext (Peer only)" - empty { implicit futConn =>
      if (system == SystemPeer) {
        /*
      :db/fulltext specifies a boolean value indicating that an eventually
      consistent fulltext search index should be generated for the attribute.
      Defaults to false.

      The fulltext option cannot be altered which means that an attribute has to
      be defined with the initial definition of the attribute to allow fulltext searches.
       */
        for {
          conn <- futConn

          // Initial attribute definition with/without fulltext search indexing
          t <- transact(schema {
            trait Foo {
              val str1 = oneString.fulltext
              val str2 = oneString
            }
          }).map(_.last.t)

          _ <- Schema.t.a.fulltext$.get.map(_ ==> List(
            (t, ":Foo/str1", Some(true)),
            (t, ":Foo/str2", None),
          ))

          _ <- conn.transact(
            s"""[
               |[:db/add "-1" :Foo/str1 "hello world"]
               |[:db/add "-2" :Foo/str2 "hello world"]
               |]""".stripMargin)

          // Do fulltext search
          _ <- conn.query(
            """[:find ?b :where [(fulltext $ :Foo/str1 "hello") [[ ?a ?b ]]]]"""
          ).map(_ ==> List(List("hello world")))

          // Fulltext search on attribute without fulltext indexing returns empty result
          _ <- conn.query(
            """[:find ?b :where [(fulltext $ :Foo/str2 "hello") [[ ?a ?b ]]]]"""
          ).map(_ ==> Nil)


          // Attempts at retracting the fulltext option return a failed Future with an exception
          _ <- conn.retractSchemaOption(":Foo/str1", "fulltext")
            .map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              msg ==> "Can't retract option 'fulltext' for attribute `:Foo/str1`. " +
                "Only the following options can be retracted: " +
                "doc, unique, isComponent, noHistory, index."
            }

          // Trying to add fulltext indexing to an existing attribute will return
          // a failed Future with an exception
          _ <- transact(schema {
            trait Foo {
              val str1 = oneString.fulltext
              val str2 = oneString.fulltext
            }
          }).map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              msg ==> ":db.error/invalid-alter-attribute Error: {" +
                ":db/error :db.error/unsupported-alter-schema, " +
                ":entity :Foo/str2, " +
                ":attribute :db/fulltext, " +
                ":from :disabled, " +
                ":to true}"
            }

          // fulltext definition history (only initialization)
          _ <- Schema.t.a.fulltext$.get.map(_ ==> List(
            (t, ":Foo/str1", Some(true)),
            (t, ":Foo/str2", None),
          ))

          // fulltext definitions only
          _ <- Schema.t.a.fulltext.getHistory.map(_ ==> List(
            (t, ":Foo/str1", true),
          ))
          _ <- Schema.t.a.fulltext_.getHistory.map(_ ==> List(
            (t, ":Foo/str1"),
          ))

          // Exclude fulltext definitions
          _ <- Schema.t.a.fulltext$(None).getHistory.map(_ ==> List(
            (t, ":Foo/str2", None),
          ))
          _ <- Schema.t.a.fulltext_(None).getHistory.map(_ ==> List(
            (t, ":Foo/str2"),
          ))

          // Specific fulltext value
          _ <- Schema.t.a.fulltext(true).getHistory.map(_ ==> List(
            (t, ":Foo/str1", true),
          ))
          _ <- Schema.t.a.fulltext$(Some(true)).getHistory.map(_ ==> List(
            (t, ":Foo/str1", Some(true)),
          ))
          _ <- Schema.t.a.fulltext_(true).getHistory.map(_ ==> List(
            (t, ":Foo/str1"),
          ))

          // fulltext values except "false"
          _ <- Schema.t.a.fulltext.not(false).getHistory.map(_ ==> List(
            (t, ":Foo/str1", true),
          ))
          _ <- Schema.t.a.fulltext_.not(false).getHistory.map(_ ==> List(
            (t, ":Foo/str1"),
          ))
        } yield ()
      }
    }


    "enumm" - empty { implicit futConn =>
      /*
      Since `enum` is a reserved keyword in Scala 3, we use `Schema.enumm` to get
      defined enum values.

      An enum attribute definition expects at least 1 enum value.
       */
      for {
        conn <- futConn

        // Initial schema
        (t1, tx1, d1) <- transact(schema {
          trait Foo {
            val bar = oneEnum("bar1")
          }
        }).map(res => (res.head.t, res.head.tx, res.head.txInstant))

        _ <- Schema.t.a.enumm.get.map(_ ==> List(
          (t1, ":Foo/bar", "bar1"),
        ))

        // Add one more enum
        (t2, tx2, d2) <- transact(schema {
          trait Foo {
            val bar = oneEnum("bar1", "bar2")
          }
        }).map(res => (res.head.t, res.head.tx, res.head.txInstant))

        _ <- Schema.t.a.enumm.a1.get.map(_ ==> List(
          (t1, ":Foo/bar", "bar1"),
          (t1, ":Foo/bar", "bar2"), // Note that `t` points to the initial transaction
        ))

        // Schema.enumm.getHistory not supported
        _ <- Schema.t.a.enumm.getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> "Retrieving historical enum values with `Schema` is not supported since they " +
              "are entities having their own timeline independently from schema attributes. " +
              "Instead, please call `conn.getEnumHistory` to retrieve historical enum values."
          }

        // Instead, use getEnumHistory
        // Returns (a, t, tx, txInstant, enum, op) tuples
        _ <- conn.getEnumHistory.map(_ ==> List(
          (":Foo/bar", t1, tx1, d1, "bar1", true),
          (":Foo/bar", t2, tx2, d2, "bar2", true),
        ))

        // Just omitting an enum value in the data model doesn't retract it
        _ <- transact(schema {
          trait Foo {
            val bar = oneEnum("bar2")
          }
        })
        // bar1 still exists
        _ <- Schema.t.a.enumm.a1.get.map(_ ==> List(
          (t1, ":Foo/bar", "bar1"),
          (t1, ":Foo/bar", "bar2"),
        ))

        // Instead, use conn.retractEnum to retract an enum value
        // (each enum value is a referenced entity that we can retract)
        (t5, tx5, d5) <- conn.retractEnum(":Foo.bar/bar1")
          .map(res => (res.t, res.tx, res.txInstant))

        // Now bar1 is not available and we can compile and transact our updated data model
        _ <- Schema.t.a.enumm.get.map(_ ==> List(
          (t1, ":Foo/bar", "bar2"),
        ))

        _ <- transact(schema {
          trait Foo {
            val bar = oneEnum("bar2")
          }
        })

        // enum history retrieved with `getEnumHistory`
        // Returns (a, t, tx, txInstant, enum, op) tuples
        _ <- conn.getEnumHistory.map(_ ==> List(
          (":Foo/bar", t1, tx1, d1, "bar1", true),
          (":Foo/bar", t2, tx2, d2, "bar2", true),
          (":Foo/bar", t5, tx5, d5, "bar1", false), // bar1 retracted in tx5
        ))
      } yield ()
    }
  }
}