package moleculeTests.tests.db.datomic.generic

import molecule.core.data.model._
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor.global
import molecule.datomic.api.out13._
import molecule.datomic.base.util.SystemPeer
import moleculeTests.dataModels.core.base.dataModel.CoreTestDataModel.{Ns, Ref1}
import moleculeTests.setup.AsyncTestSuite
import utest._


object SchemaChange_AttrOptions extends AsyncTestSuite {

  lazy val tests = Tests {

    "valueType" - empty { implicit futConn =>
      for {
        // Initial Int attribute
        t0 <- transact(schema {
          trait Foo {
            val str = oneString
          }
        }).map(_.last.t)

        _ <- Schema.t.a.valueType.get.map(_ ==> List(
          (t0, ":Foo/str", "string"),
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

        t1 <- transact(schema {
          trait Foo {
            val str  = oneString.doc("doc")
            val bool = oneBoolean
          }
        }).map(_.last.t)


        // Optional value type definitions
        _ <- Schema.t.a.valueType$.doc$.getHistory.map(_ ==> List(
          (t0, ":Foo/str", Some("string"), None),
          (t1, ":Foo/str", None, Some("doc")),
          (t1, ":Foo/bool", Some("boolean"), None),
        ))

        // Value type definitions only
        _ <- Schema.t.a.valueType.doc$.getHistory.map(_ ==> List(
          (t0, ":Foo/str", "string", None),
          (t1, ":Foo/bool", "boolean", None),
        ))
        _ <- Schema.t.a.valueType_.doc$.getHistory.map(_ ==> List(
          (t0, ":Foo/str", None),
          (t1, ":Foo/bool", None),
        ))

        // Exclude value type
        _ <- Schema.t.a.valueType$(None).doc$.getHistory.map(_ ==> List(
          (t1, ":Foo/str", None, Some("doc")),
        ))
        _ <- Schema.t.a.valueType_(None).doc$.getHistory.map(_ ==> List(
          (t1, ":Foo/str", Some("doc")),
        ))

        // Specific value type
        _ <- Schema.t.a.valueType("string").getHistory.map(_ ==> List(
          (t0, ":Foo/str", "string")
        ))
        _ <- Schema.t.a.valueType$(Some("string")).getHistory.map(_ ==> List(
          (t0, ":Foo/str", Some("string"))
        ))
        _ <- Schema.t.a.valueType_("string").getHistory.map(_ ==> List(
          (t0, ":Foo/str")
        ))

        // Value types except boolean
        _ <- Schema.t.a.valueType.not("boolean").getHistory.map(_ ==> List(
          (t0, ":Foo/str", "string")
        ))
        _ <- Schema.t.a.valueType_.not("boolean").getHistory.map(_ ==> List(
          (t0, ":Foo/str")
        ))

        // Variables can be applied too
        string = "string"
        some = Some("string")
        none = None
        _ <- Schema.t.a.valueType(string).getHistory.map(_ ==> List(
          (t0, ":Foo/str", "string")
        ))
        _ <- Schema.t.a.valueType_(string).getHistory.map(_ ==> List(
          (t0, ":Foo/str")
        ))
        _ <- Schema.t.a.valueType$(some).getHistory.map(_ ==> List(
          (t0, ":Foo/str", Some("string"))
        ))
        _ <- Schema.t.a.valueType$(none).getHistory.map(_ ==> List(
          (t1, ":Foo/str", None)
        ))
      } yield ()
    }
    //
    //
    //    "valueType, types" - empty { implicit futConn =>
    //      for {
    //        _ <- transact(schema {
    //          trait Foo {
    //            val str    = oneString
    //            val int    = oneInt
    //            val long   = oneLong
    //            val double = oneDouble
    //            val bool   = oneBoolean
    //            val date   = oneDate
    //            val uuid   = oneUUID
    //            val uri    = oneURI
    //            val bigInt = oneBigInt
    //            val bigDec = oneBigDecimal
    //            val enumm  = oneEnum("enum1", "enum2")
    //            val bar    = one[Bar]
    //
    //            val strs    = manyString
    //            val ints    = manyInt
    //            val longs   = manyLong
    //            val doubles = manyDouble
    //            val bools   = manyBoolean
    //            val dates   = manyDate
    //            val uuids   = manyUUID
    //            val uris    = manyURI
    //            val bigInts = manyBigInt
    //            val bigDecs = manyBigDecimal
    //            val enums   = manyEnum("enums1", "enums2")
    //            val bars    = many[Bar]
    //
    //            val strMap    = mapString
    //            val intMap    = mapInt
    //            val longMap   = mapLong
    //            val doubleMap = mapDouble
    //            val boolMap   = mapBoolean
    //            val dateMap   = mapDate
    //            val uuidMap   = mapUUID
    //            val uriMap    = mapURI
    //            val bigIntMap = mapBigInt
    //            val bigDecMap = mapBigDecimal
    //          }
    //          trait Bar {
    //            //            val baz = oneInt
    //          }
    //        }).map(_.last.t)
    //
    //        _ <- Schema.attrId.a1.cardinality.attr.valueType
    //          .getHistory.map(_.map(r => (r._2, r._3, r._4)) ==> List(
    //          ("one", "str", "string"),
    //          ("one", "int", "long"), // Scala Int's are saved internally as Long's in Datomic
    //          ("one", "long", "long"),
    //          ("one", "double", "double"),
    //          ("one", "bool", "boolean"),
    //          ("one", "date", "instant"),
    //          ("one", "uuid", "uuid"),
    //          ("one", "uri", "uri"),
    //          ("one", "bigInt", "bigint"),
    //          ("one", "bigDec", "bigdec"),
    //          ("one", "enumm", "ref"),
    //          ("one", "bar", "ref"),
    //
    //          ("many", "strs", "string"),
    //          ("many", "ints", "long"),
    //          ("many", "longs", "long"),
    //          ("many", "doubles", "double"),
    //          ("many", "bools", "boolean"),
    //          ("many", "dates", "instant"),
    //          ("many", "uuids", "uuid"),
    //          ("many", "uris", "uri"),
    //          ("many", "bigInts", "bigint"),
    //          ("many", "bigDecs", "bigdec"),
    //          ("many", "enums", "ref"),
    //          ("many", "bars", "ref"),
    //
    //          // Molecule map attributes are saved internally as cardinality-many
    //          // string values ("<key>@<value>") in Datomic
    //          ("many", "strMap", "string"),
    //          ("many", "intMap", "string"),
    //          ("many", "longMap", "string"),
    //          ("many", "doubleMap", "string"),
    //          ("many", "boolMap", "string"),
    //          ("many", "dateMap", "string"),
    //          ("many", "uuidMap", "string"),
    //          ("many", "uriMap", "string"),
    //          ("many", "bigIntMap", "string"),
    //          ("many", "bigDecMap", "string")
    //        ))
    //      } yield ()
    //    }


    //    "cardinality" - {
    //
    //      "one to many" - empty { implicit futConn =>
    //        for {
    //          conn <- futConn
    //
    //          // Card-one attribute
    //          t0 <- transact(schema {
    //            trait Foo {
    //              val int = oneInt
    //            }
    //          }).map(_.last.t)
    //
    //          _ <- Schema.t.a.cardinality.get.map(_ ==> List(
    //            (t0, ":Foo/int", "one"),
    //          ))
    //
    //          // Change cardinality to "many"
    //          t1 <- transact(schema {
    //            trait Foo {
    //              val int = manyInt
    //            }
    //          }).map(_.last.t)
    //
    //          // Current cardinalities
    //          _ <- Schema.t.a.cardinality.get.map(_ ==> List(
    //            (t0, ":Foo/int", "many"),
    //          ))
    //
    //          // Cardinality history
    //          _ <- Schema.t.a.cardinality.getHistory.map(_ ==> List(
    //            (t0, ":Foo/int", "one"),
    //            (t1, ":Foo/int", "many"),
    //          ))
    //
    //          // Specific cardinality
    //          _ <- Schema.t.a.cardinality("one").getHistory.map(_ ==> List(
    //            (t0, ":Foo/int", "one"),
    //          ))
    //          _ <- Schema.t.a.cardinality_("one").getHistory.map(_ ==> List(
    //            (t0, ":Foo/int"),
    //          ))
    //
    //          // Cardinalities except "many"
    //          _ <- Schema.t.a.cardinality.not("many").getHistory.map(_ ==> List(
    //            (t0, ":Foo/int", "one"),
    //          ))
    //          _ <- Schema.t.a.cardinality_.not("many").getHistory.map(_ ==> List(
    //            (t0, ":Foo/int"),
    //          ))
    //
    //          // Add some non-cardinality schema change
    //          t4 <- transact(schema {
    //            trait Foo {
    //              val int = manyInt.doc("hello")
    //            }
    //          }).map(_.last.t)
    //
    //          // Optional cardinality definitions
    //          _ <- Schema.t.a.cardinality$.doc$.getHistory.map(_ ==> List(
    //            (t0, ":Foo/int", Some("one"), None),
    //            (t1, ":Foo/int", Some("many"), None),
    //            (t4, ":Foo/int", None, Some("hello")), // no cardinality change when doc was added
    //          ))
    //
    //          // Cardinality definitions only
    //          _ <- Schema.t.a.cardinality.doc$.getHistory.map(_ ==> List(
    //            (t0, ":Foo/int", Some("one"), None),
    //            (t1, ":Foo/int", Some("many"), None),
    //          ))
    //
    //          // Exclude cardinality
    //          _ <- Schema.t.a.cardinality$(None).getHistory.map(_ ==> List(
    //            (t4, ":Foo/int", None)
    //          ))
    //          _ <- Schema.t.a.cardinality_(None).getHistory.map(_ ==> List(
    //            (t4, ":Foo/int")
    //          ))
    //
    //          // Specific cardinality
    //          _ <- Schema.t.a.cardinality("one").getHistory.map(_ ==> List(
    //            (t0, ":Foo/int", "one")
    //          ))
    //          _ <- Schema.t.a.cardinality$(Some("one")).getHistory.map(_ ==> List(
    //            (t0, ":Foo/int", Some("one"))
    //          ))
    //          _ <- Schema.t.a.cardinality_("one").getHistory.map(_ ==> List(
    //            (t0, ":Foo/int")
    //          ))
    //
    //          // Cardinalities except "many"
    //          _ <- Schema.t.a.cardinality.not("many").getHistory.map(_ ==> List(
    //            (t0, ":Foo/int", "one")
    //          ))
    //          _ <- Schema.t.a.cardinality_.not("many").getHistory.map(_ ==> List(
    //            (t0, ":Foo/int")
    //          ))
    //
    //        } yield ()
    //      }

    //      "many to one" - empty { implicit futConn =>
    //        for {
    //          conn <- futConn
    //
    //          // Card-one attribute
    //          t <- transact(schema {
    //            trait Foo {
    //              val bar = manyInt
    //              val baz = manyInt
    //            }
    //          }).map(_.last.t)
    //
    //          _ <- Schema.t.a.a1.cardinality.get.map(_ ==> List(
    //            (t, ":Foo/bar", "many"),
    //            (t, ":Foo/baz", "many"),
    //          ))
    //
    //          // Add 1 value for bar and 2 values for baz
    //          r <- conn.transact(
    //            """[
    //              |  [:db/add "-1" :Foo/bar 1]
    //              |  [:db/add "-2" :Foo/baz 2]
    //              |  [:db/add "-2" :Foo/baz 3]
    //              |]""".stripMargin)
    //          tx = r.tx
    //          List(e1, e2) = r.eids
    //
    //          _ <- conn.query("[:find (distinct ?b) :where [?a :Foo/bar ?b]]")
    //            .map(_ ==> List(List(Set(1))))
    //
    //          _ <- conn.query("[:find (distinct ?b) :where [?a :Foo/baz ?b]]")
    //            .map(_ ==> List(List(Set(2, 3))))
    //
    //          // Change cardinality to one for `bar` - ok if attribute has only
    //          // 1 value for any entity
    //          t4 <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt
    //              val baz = manyInt
    //            }
    //          }).map(_.last.t)
    //
    //          // baz now has cardinality one
    //          _ <- Schema.t.a.a1.cardinality.get.map(_ ==> List(
    //            (t, ":Foo/bar", "one"),
    //            (t, ":Foo/baz", "many"),
    //          ))
    //
    //          // Change cardinality to one for `baz` - rejected if attribute has multiple
    //          // values for any entity
    //          _ <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt
    //              val baz = oneInt
    //            }
    //          }).map(_ ==> "Unexpected success")
    //            .recover { case MoleculeException(msg, _) =>
    //              if (system == SystemPeer)
    //                msg ==> ":db.error/invalid-alter-attribute Error: " +
    //                  "{:db/error :db.error/cardinality-violation, " +
    //                  ":datoms [" +
    //                  s"#datom[$e2 73 2 $tx true] " +
    //                  s"#datom[$e2 73 3 $tx true]]}"
    //              else {
    //                msg ==> "Error: {" +
    //                  ":db/error :db.error/cardinality-violation, " +
    //                  ":datoms [" +
    //                  s"#datom[$e2 74 2 $tx true] " +
    //                  s"#datom[$e2 74 3 $tx true]]}"
    //              }
    //            }
    //
    //          // Retract values so that `baz` has only one value per entity
    //          _ <- conn.transact(s"[[:db/retract $e2 :Foo/baz 3]]")
    //
    //          _ <- conn.query("[:find (distinct ?b) :where [?a :Foo/baz ?b]]")
    //            .map(_ ==> List(List(Set(2))))
    //
    //          // Now we can change baz to cardinality one
    //          t6 <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt
    //              val baz = oneInt
    //            }
    //          }).map(_.last.t)
    //
    //          // Both attributes now has cardinality one
    //          _ <- Schema.t.a.a1.cardinality.get.map(_ ==> List(
    //            (t, ":Foo/bar", "one"),
    //            (t, ":Foo/baz", "one"),
    //          ))
    //
    //          // History of changes
    //          _ <- Schema.t.a.cardinality.getHistory.map(_ ==> List(
    //            (t, ":Foo/bar", "many"),
    //            (t, ":Foo/baz", "many"),
    //
    //            (t4, ":Foo/bar", "one"),
    //
    //            (t6, ":Foo/baz", "one"),
    //          ))
    //        } yield ()
    //      }
    //    }
    //
    //
    //    "doc" - empty { implicit futConn =>
    //      // :db/doc specifies a documentation string.
    //
    //      for {
    //        conn <- futConn
    //
    //        // Initial attribute without documentation string
    //        t <- transact(schema {
    //          trait Foo {
    //            val int = oneInt
    //          }
    //        }).map(_.last.t)
    //
    //        _ <- Schema.t.a.doc$.get.map(_ ==> List(
    //          (t, ":Foo/int", None),
    //        ))
    //
    //        // Add documentation string
    //        t1 <- transact(schema {
    //          trait Foo {
    //            val int = oneInt.doc("blah")
    //          }
    //        }).map(_.last.t)
    //
    //        _ <- Schema.t.a.doc$.get.map(_ ==> List(
    //          (t, ":Foo/int", Some("blah")),
    //        ))
    //        _ <- Schema.t.a.doc$.getHistory.map(_ ==> List(
    //          (t, ":Foo/int", None),
    //          (t1, ":Foo/int", Some("blah")),
    //        ))
    //
    //        // Update documentation string
    //        t2 <- transact(schema {
    //          trait Foo {
    //            val int = oneInt.doc("blah blah")
    //          }
    //        }).map(_.last.t)
    //
    //        _ <- Schema.t.a.doc$.get.map(_ ==> List(
    //          (t, ":Foo/int", Some("blah blah")),
    //        ))
    //        _ <- Schema.t.a.doc$.getHistory.map(_ ==> List(
    //          (t, ":Foo/int", None),
    //          (t1, ":Foo/int", Some("blah")),
    //          (t2, ":Foo/int", Some("blah blah")),
    //        ))
    //
    //        // Retract documentation string
    //        t3 <- conn.retractSchemaOption(":Foo/int", "doc").map(_.t)
    //        _ <- transact(schema {
    //          trait Foo {
    //            val int = oneInt
    //          }
    //        })
    //
    //        _ <- Schema.t.a.doc$.get.map(_ ==> List(
    //          (t, ":Foo/int", None),
    //        ))
    //        _ <- Schema.t.a.doc$.getHistory.map(_ ==> List(
    //          (t, ":Foo/int", None),
    //          (t1, ":Foo/int", Some("blah")),
    //          (t2, ":Foo/int", Some("blah blah")),
    //          (t3, ":Foo/int", None),
    //        ))
    //
    //        // retractSchemaOption checks
    //
    //        // Invalid attribute name
    //        _ <- conn.retractSchemaOption("Foo/int", "doc")
    //          .map(_ ==> "Unexpected success")
    //          .recover { case MoleculeException(msg, _) =>
    //            msg ==> "Invalid attribute name `Foo/int`. " +
    //              "Expecting attribute name in the format `:<Ns>/<attr>` or `:<part_Ns>/<attr>`"
    //          }
    //
    //        // Invalid option name
    //        _ <- conn.retractSchemaOption(":Foo/int", "docs")
    //          .map(_ ==> "Unexpected success")
    //          .recover { case MoleculeException(msg, _) =>
    //            msg ==> "Can't retract option 'docs' for attribute `:Foo/int`. " +
    //              "Only the following options can be retracted: " +
    //              "doc, unique, isComponent, noHistory, index."
    //          }
    //
    //        // Non-existing attribute
    //        _ <- conn.retractSchemaOption(":Foo/bar", "doc")
    //          .map(_ ==> "Unexpected success")
    //          .recover { case MoleculeException(msg, _) =>
    //            msg ==> "Couldn't find attribute `:Foo/bar` in the database."
    //          }
    //
    //        // Option has no value
    //        _ <- conn.retractSchemaOption(":Foo/int", "doc")
    //          .map(_ ==> "Unexpected success")
    //          .recover { case MoleculeException(msg, _) =>
    //            msg ==> "'doc' option of attribute :Foo/int has no value."
    //          }
    //      } yield ()
    //    }
    //
    //
    //    "unique" - {
    //      /*
    //      :db/unique - specifies a uniqueness constraint for the values of an attribute.
    //      Setting an attribute :db/unique also implies :db/index. The values allowed
    //      for :db/unique are:
    //
    //      :db.unique/value - only one entity can have a given value for this attribute.
    //        Attempts to assert a duplicate value for the same attribute for a different
    //        entity id will fail. More documentation on unique values is available here.
    //
    //      :db.unique/identity - only one entity can have a given value for this attribute
    //        and "upsert" is enabled; attempts to insert a duplicate value for a temporary
    //        entity id will cause all attributes associated with that temporary id to be
    //        merged with the entity already in the database. More documentation on unique
    //        identities is available here.
    //
    //      :db/unique defaults to nil.
    //
    //      See:
    //      https://docs.datomic.com/on-prem/schema/schema.html#operational-schema-attributes
    //      https://docs.datomic.com/on-prem/schema/identity.html#unique-values
    //      https://docs.datomic.com/on-prem/schema/identity.html#unique-identities
    //       */
    //
    //      "value" - empty { implicit futConn =>
    //        for {
    //          conn <- futConn
    //
    //          // Initial attribute
    //          //          t <- transact(schema {
    //          //            trait Foo {
    //          //              val bar = oneInt
    //          //            }
    //          //          }).map(_.last.t)
    //          (t, tx0) <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt
    //            }
    //          }).map(res => (res.last.t, res.head.tx))
    //
    //          _ <- Schema.t.a.unique$.get.map(_ ==> List(
    //            (t, ":Foo/bar", None),
    //          ))
    //
    //          // Add two entities with duplicate data for :Foo/bar
    //          txr <- conn.transact(
    //            """[
    //              |  [:db/add "-1" :Foo/bar 1]
    //              |  [:db/add "-2" :Foo/bar 1]
    //              |]""".stripMargin)
    //          tx = txr.tx
    //          List(e1, e2) = txr.eids
    //
    //          // :Foo/bar has duplicate values
    //          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
    //            .map(res =>
    //              if (system == SystemPeer)
    //                res ==> List(
    //                  List(e2, 1),
    //                  List(e1, 1),
    //                )
    //              else
    //                res ==> List(
    //                  List(e1, 1),
    //                  List(e2, 1),
    //                )
    //            )
    //
    //          // Enforcing unique values on bar having duplicate values will be rejected
    //          _ <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt.uniqueValue
    //            }
    //          }).map(_ ==> "Unexpected success")
    //            .recover { case MoleculeException(msg, _) =>
    //              if (system == SystemPeer)
    //                msg ==> ":db.error/invalid-alter-attribute Error: {" +
    //                  ":db/error :db.error/unique-violation, " +
    //                  ":datoms [" +
    //                  s"#datom[$e1 72 1 $tx true] " +
    //                  s"#datom[$e2 72 1 $tx true]]}"
    //              else
    //                msg ==> "Error: {:db/error :db.error/unique-violation, " +
    //                  ":datoms [" +
    //                  s"#datom[$e1 73 1 $tx true] " +
    //                  s"#datom[$e2 73 1 $tx true]]}"
    //            }
    //
    //          // Remove duplicate values of :Foo/bar
    //          _ <- conn.transact(s"[[:db/retract $e2 :Foo/bar 1]]")
    //
    //          // `bar` now has no duplicate values
    //          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
    //            .map(_ ==> List(
    //              List(e1, 1)
    //            ))
    //
    //          // Now we can add the unique value constraint on :Foo/bar
    //          t5 <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt.uniqueValue
    //            }
    //          }).map(_.last.t)
    //
    //          _ <- Schema.t.a.unique$.get.map(_ ==> List(
    //            (t, ":Foo/bar", Some("value")),
    //          ))
    //          _ <- Schema.t.a.unique$.getHistory.map(_ ==> List(
    //            (t, ":Foo/bar", None),
    //            (t5, ":Foo/bar", Some("value")),
    //          ))
    //
    //          // Now we are not allowed to add duplicate values.
    //          // This is where unique value/identity differ:
    //          // uniqueIdentity would allow upserting the value
    //          // (and no entity would be created since the value already exists)
    //          _ <- conn.transact("""[[:db/add "-3" :Foo/bar 1]]""")
    //            .map(_ ==> "Unexpected success")
    //            .recover { case MoleculeException(msg, _) =>
    //              if (system == SystemPeer)
    //                msg.take(51) ==> ":db.error/unique-conflict Unique conflict: :Foo/bar"
    //              else
    //                msg.take(25) ==> "Unique conflict: :Foo/bar"
    //            }
    //
    //          // If we remove the constraint we can add duplicate values again
    //          t6 <- conn.retractSchemaOption(":Foo/bar", "unique").map(_.t)
    //
    //          // Note that simply removing `uniqueValue` from the attribute definition
    //          // will not retract the constraint
    //          _ <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt
    //            }
    //          })
    //
    //          _ <- Schema.t.a.unique$.get.map(_ ==> List(
    //            (t, ":Foo/bar", None),
    //          ))
    //          _ <- Schema.t.a.unique$.getHistory.map(_ ==> List(
    //            (t, ":Foo/bar", None),
    //            (t5, ":Foo/bar", Some("value")),
    //            (t6, ":Foo/bar", None),
    //          ))
    //
    //          e3 <- conn.transact("""[[:db/add "-3" :Foo/bar 1]]""").map(_.eid)
    //
    //          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
    //            .map(res =>
    //              if (system == SystemPeer)
    //                res ==> List(
    //                  List(e3, 1),
    //                  List(e1, 1),
    //                )
    //              else
    //                res ==> List(
    //                  List(e1, 1),
    //                  List(e3, 1),
    //                )
    //            )
    //
    //        } yield ()
    //      }
    //
    //
    //      "identity" - empty { implicit futConn =>
    //        for {
    //          conn <- futConn
    //
    //          // Initial attribute
    //          t <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt
    //            }
    //          }).map(_.last.t)
    //
    //          _ <- Schema.t.a.unique$.get.map(_ ==> List(
    //            (t, ":Foo/bar", None),
    //          ))
    //
    //          // Add two entities with duplicate data for :Foo/bar
    //          txr <- conn.transact(
    //            """[
    //              |  [:db/add "-1" :Foo/bar 1]
    //              |  [:db/add "-2" :Foo/bar 1]
    //              |]""".stripMargin)
    //          tx = txr.tx
    //          List(e1, e2) = txr.eids
    //
    //          // `bar` has duplicate values
    //          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
    //            .map(res =>
    //              if (system == SystemPeer)
    //                res ==> List(
    //                  List(e2, 1),
    //                  List(e1, 1),
    //                )
    //              else
    //                res ==> List(
    //                  List(e1, 1),
    //                  List(e2, 1),
    //                )
    //            )
    //
    //          // Enforcing unique values on :Foo/bar having duplicate values will be rejected
    //          _ <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt.uniqueIdentity
    //            }
    //          }).map(_ ==> "Unexpected success")
    //            .recover { case MoleculeException(msg, _) =>
    //              if (system == SystemPeer)
    //                msg ==> ":db.error/invalid-alter-attribute Error: {" +
    //                  ":db/error :db.error/unique-violation, " +
    //                  ":datoms [" +
    //                  s"#datom[$e1 72 1 $tx true] " +
    //                  s"#datom[$e2 72 1 $tx true]]}"
    //              else
    //                msg ==> "Error: {:db/error :db.error/unique-violation, " +
    //                  ":datoms [" +
    //                  s"#datom[$e1 73 1 $tx true] " +
    //                  s"#datom[$e2 73 1 $tx true]]}"
    //            }
    //
    //          // Remove duplicate values of :Foo/bar
    //          _ <- conn.transact(s"[[:db/retract $e2 :Foo/bar 1]]")
    //
    //          // :Foo/bar now has no duplicate values
    //          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
    //            .map(_ ==> List(
    //              List(e1, 1)
    //            ))
    //
    //          // Now we can add the unique identity constraint on :Foo/bar
    //          t5 <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt.uniqueIdentity
    //            }
    //          }).map(_.last.t)
    //
    //          _ <- Schema.t.a.unique$.get.map(_ ==> List(
    //            (t, ":Foo/bar", Some("identity")),
    //          ))
    //          _ <- Schema.t.a.unique$.getHistory.map(_ ==> List(
    //            (t, ":Foo/bar", None),
    //            (t5, ":Foo/bar", Some("identity")),
    //          ))
    //
    //          // Since entity e1 already has `1` asserted and "upsert" behaviour is
    //          // enabled with unique identity, no new entity is created when trying to assert
    //          // `1` again.
    //          // This is where unique value/identity differ:
    //          // uniqueValue would reject transacting the duplicate value
    //          _ <- conn.transact("""[[:db/add "-3" :Foo/bar 1]]""").map(_.eids ==> Nil)
    //
    //          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
    //            .map(_ ==> List(
    //              List(e1, 1),
    //            ))
    //
    //          // If we remove the constraint we can add duplicate values again
    //          t7 <- conn.retractSchemaOption(":Foo/bar", "unique").map(_.t)
    //          // Note that simply removing `uniqueIdentity` from the attribute definition
    //          // will not retract the constraint
    //
    //          _ <- transact(schema {
    //            trait Foo {
    //              val bar = oneInt
    //            }
    //          })
    //
    //          _ <- Schema.t.a.unique$.get.map(_ ==> List(
    //            (t, ":Foo/bar", None),
    //          ))
    //          _ <- Schema.t.a.unique$.getHistory.map(_ ==> List(
    //            (t, ":Foo/bar", None),
    //            (t5, ":Foo/bar", Some("identity")),
    //            (t7, ":Foo/bar", None),
    //          ))
    //
    //          e3 <- conn.transact("""[[:db/add "-3" :Foo/bar 1]]""").map(_.eid)
    //
    //          _ <- conn.query("[:find ?a ?b :where [?a :Foo/bar ?b]]")
    //            .map(res =>
    //              if (system == SystemPeer)
    //                res ==> List(
    //                  List(e3, 1),
    //                  List(e1, 1),
    //                )
    //              else
    //                res ==> List(
    //                  List(e1, 1),
    //                  List(e3, 1),
    //                )
    //            )
    //        } yield ()
    //      }
    //    }
    //
    //
    //    "isComponent" - empty { implicit futConn =>
    //      /*
    //      :db/isComponent specifies a boolean value indicating that an attribute
    //      whose type is :db.type/ref refers to a subcomponent of the entity to which
    //      the attribute is applied. When you retract an entity with :db.fn/retractEntity,
    //      all subcomponents are also retracted. When you touch an entity, all its
    //      subcomponent entities are touched recursively. Defaults to false.
    //       */
    //      for {
    //        conn <- futConn
    //
    //        // Non-component ref
    //        t <- transact(schema {
    //          trait Foo {
    //            val bar = one[Bar]
    //          }
    //          trait Bar {
    //            val int = oneInt
    //          }
    //        }).map(_.last.t)
    //
    //        _ <- Schema.t.a.isComponent$.get.map(_ ==> List(
    //          (t, ":Foo/bar", None),
    //          (t, ":Bar/int", None),
    //        ))
    //
    //        // Add ref data
    //        List(e, r) <- conn.transact(
    //          """[
    //            |  [:db/add "-1" :Foo/bar "-2"]
    //            |  [:db/add "-2" :Bar/int 1]
    //            |]""".stripMargin).map(_.eids)
    //
    //        // When retracting a non-component ref, the referenced data is not affected
    //        _ <- conn.transact(s"[[:db/retractEntity $e]]")
    //        _ <- conn.query("[:find ?b :where [?a :Bar/int ?b]]")
    //          .map(_ ==> List(List(1)))
    //
    //
    //        // Now make the bar reference a component reference
    //        t5 <- transact(schema {
    //          trait Foo {
    //            val bar = one[Bar].isComponent
    //          }
    //          trait Bar {
    //            val int = oneInt
    //          }
    //        }).map(_.last.t)
    //        _ <- Schema.t.a.isComponent$.get.map(_ ==> List(
    //          (t, ":Foo/bar", Some(true)),
    //          (t, ":Bar/int", None),
    //        ))
    //
    //        // Add component ref to :Bar/int again
    //        e2 <- conn.transact(s"""[[:db/add "-3" :Foo/bar $r]]""").map(_.eid)
    //
    //        // When retracting a component ref, the referenced data is now retracted too
    //        _ <- conn.transact(s"[[:db/retractEntity $e2]]")
    //        _ <- conn.query("[:find ?b :where [?a :Bar/int ?b]]").map(_ ==> Nil)
    //
    //
    //        // Make :Foo/bar a non-component ref again
    //        t9 <- conn.retractSchemaOption(":Foo/bar", "isComponent").map(_.t)
    //        _ <- Schema.t.a.isComponent$.get.map(_ ==> List(
    //          (t, ":Foo/bar", None),
    //          (t, ":Bar/int", None),
    //        ))
    //
    //        // Add non-component ref to :Bar/int again
    //        List(e3, _) <- conn.transact(
    //          """[
    //            |  [:db/add "-1" :Foo/bar "-2"]
    //            |  [:db/add "-2" :Bar/int 3]
    //            |]""".stripMargin).map(_.eids)
    //
    //        // When retracting a non-component ref, the referenced data is not affected
    //        // (as in the beginning of the test)
    //        _ <- conn.transact(s"[[:db/retractEntity $e3]]")
    //        _ <- conn.query("[:find ?b :where [?a :Bar/int ?b]]")
    //          .map(_ ==> List(List(3)))
    //
    //        _ <- Schema.t.a.isComponent$.getHistory.map(_ ==> List(
    //          (t, ":Foo/bar", None),
    //          (t, ":Bar/int", None),
    //          (t5, ":Foo/bar", Some(true)),
    //          (t9, ":Foo/bar", None),
    //        ))
    //      } yield ()
    //    }
    //
    //
    //    "noHistory" - empty { implicit futConn =>
    //      /*
    //      :db/noHistory specifies a boolean value indicating whether past values of
    //      an attribute should not be retained. Defaults to false.
    //
    //      The purpose of :db/noHistory is to conserve storage, not to make semantic
    //      guarantees about removing information. The effect of :db/noHistory happens
    //      in the background, and some amount of history may be visible even for
    //      attributes with :db/noHistory set to true.
    //
    //      Therefore we simply show below how to turn the option on and off.
    //       */
    //      for {
    //        conn <- futConn
    //
    //        // Initial attribute with history recorded as default
    //        t <- transact(schema {
    //          trait Foo {
    //            val int = oneInt
    //          }
    //        }).map(_.last.t)
    //
    //        _ <- Schema.t.a.noHistory$.get.map(_ ==> List(
    //          (t, ":Foo/int", None),
    //        ))
    //
    //        // Stop retaining values for Foo/int
    //        t1 <- transact(schema {
    //          trait Foo {
    //            val int = oneInt.noHistory
    //          }
    //        }).map(_.last.t)
    //
    //        _ <- Schema.t.a.noHistory$.get.map(_ ==> List(
    //          (t, ":Foo/int", Some(true)),
    //        ))
    //
    //        // Re-retain values for Foo/int
    //        t2 <- conn.retractSchemaOption(":Foo/int", "noHistory").map(_.t)
    //
    //        _ <- Schema.t.a.noHistory$.get.map(_ ==> List(
    //          (t, ":Foo/int", None),
    //        ))
    //        _ <- Schema.t.a.noHistory$.getHistory.map(_ ==> List(
    //          (t, ":Foo/int", None),
    //          (t1, ":Foo/int", Some(true)),
    //          (t2, ":Foo/int", None),
    //        ))
    //      } yield ()
    //    }
    //
    //
    //    "index, Peer" - empty { implicit futConn =>
    //      if (system == SystemPeer) {
    //        /*
    //        :db/index specifies a boolean value indicating that an index should be
    //        generated for this attribute. Defaults to false.
    //
    //        In Molecule the indexing is turned on by default.
    //        Like with the noHistory option we simply show how to turn it on and off.
    //         */
    //        for {
    //          conn <- futConn
    //
    //          // In Molecule indexing on the Peer is turned on by default without having to
    //          // add `index` to the attribute definition
    //          t <- transact(schema {
    //            trait Foo {
    //              val int = oneInt
    //            }
    //          }).map(_.last.t)
    //
    //          _ <- Schema.t.a.index$.get.map(_ ==> List(
    //            (t, ":Foo/int", Some(true))
    //          ))
    //
    //          // Stop indexing values for Foo/int
    //          t1 <- conn.retractSchemaOption(":Foo/int", "index").map(_.t)
    //
    //          _ <- Schema.t.a.noHistory$.get.map(_ ==> List(
    //            (t, ":Foo/int", None),
    //          ))
    //
    //          // Index attribute values again
    //          t2 <- transact(schema {
    //            trait Foo {
    //              val int = oneInt.index
    //            }
    //          }).map(_.last.t)
    //
    //          _ <- Schema.t.a.index$.get.map(_ ==> List(
    //            (t, ":Foo/int", Some(true)),
    //          ))
    //          _ <- Schema.t.a.index$.getHistory.map(_ ==> List(
    //            (t, ":Foo/int", Some(true)),
    //            (t1, ":Foo/int", None),
    //            (t2, ":Foo/int", Some(true)),
    //          ))
    //        } yield ()
    //      }
    //    }
    //
    //
    //    "index, Client" - empty { implicit futConn =>
    //      if (system != SystemPeer) {
    //        /*
    //        All attributes are indexed by default on the Client and there is no indexing choice.
    //         */
    //        for {
    //          t <- transact(schema {
    //            trait Foo {
    //              val int = oneInt
    //            }
    //          }).map(_.last.t)
    //
    //          // Querying for the index option will return None since no index option is asserted
    //          _ <- Schema.t.a.index$.get.map(res =>
    //            res ==> List((t, ":Foo/int", None))
    //          )
    //        } yield ()
    //      }
    //    }
    //
    //    "fulltext (Peer only)" - empty { implicit futConn =>
    //      if (system == SystemPeer) {
    //        /*
    //      :db/fulltext specifies a boolean value indicating that an eventually
    //      consistent fulltext search index should be generated for the attribute.
    //      Defaults to false.
    //
    //      The fulltext option cannot be altered which means that an attribute has to
    //      be defined with the initial definition of the attribute to allow fulltext searches.
    //       */
    //        for {
    //          conn <- futConn
    //
    //          // Initial attribute definition with/without fulltext search indexing
    //          t <- transact(schema {
    //            trait Foo {
    //              val str1 = oneString.fulltext
    //              val str2 = oneString
    //            }
    //          }).map(_.last.t)
    //
    //          _ <- Schema.t.a.fulltext$.get.map(_ ==> List(
    //            (t, ":Foo/str1", Some(true)),
    //            (t, ":Foo/str2", None),
    //          ))
    //
    //          _ <- conn.transact(
    //            s"""[
    //               |[:db/add "-1" :Foo/str1 "hello world"]
    //               |[:db/add "-2" :Foo/str2 "hello world"]
    //               |]""".stripMargin)
    //
    //          // Do fulltext search
    //          _ <- conn.query(
    //            """[:find ?b :where [(fulltext $ :Foo/str1 "hello") [[ ?a ?b ]]]]"""
    //          ).map(_ ==> List(List("hello world")))
    //
    //          // Fulltext search on attribute without fulltext indexing returns empty result
    //          _ <- conn.query(
    //            """[:find ?b :where [(fulltext $ :Foo/str2 "hello") [[ ?a ?b ]]]]"""
    //          ).map(_ ==> Nil)
    //
    //
    //          // Attempts at retracting the fulltext option return a failed Future with an exception
    //          _ <- conn.retractSchemaOption(":Foo/str1", "fulltext")
    //            .map(_ ==> "Unexpected success")
    //            .recover { case MoleculeException(msg, _) =>
    //              msg ==> "Can't retract option 'fulltext' for attribute `:Foo/str1`. " +
    //                "Only the following options can be retracted: " +
    //                "doc, unique, isComponent, noHistory, index."
    //            }
    //
    //          // Trying to add fulltext indexing to an existing attribute will return
    //          // a failed Future with an exception
    //          _ <- transact(schema {
    //            trait Foo {
    //              val str1 = oneString.fulltext
    //              val str2 = oneString.fulltext
    //            }
    //          }).map(_ ==> "Unexpected success")
    //            .recover { case MoleculeException(msg, _) =>
    //              msg ==> ":db.error/invalid-alter-attribute Error: {" +
    //                ":db/error :db.error/unsupported-alter-schema, " +
    //                ":entity :Foo/str2, " +
    //                ":attribute :db/fulltext, " +
    //                ":from :disabled, " +
    //                ":to true}"
    //            }
    //
    //          // No change of fulltext options
    //          _ <- Schema.t.a.fulltext$.get.map(_ ==> List(
    //            (t, ":Foo/str1", Some(true)),
    //            (t, ":Foo/str2", None),
    //          ))
    //        } yield ()
    //      }
    //    }
    //
    //    "enumm" - empty { implicit futConn =>
    //      /*
    //      Since `enum` is a reserved keyword in Scala 3, we use `Schema.enumm` to get
    //      defined enum values.
    //
    //      An enum attribute definition expects at least 1 enum value.
    //       */
    //      for {
    //        conn <- futConn
    //
    //        // Initial schema
    //        (t, tx0, d0) <- transact(schema {
    //          trait Foo {
    //            val bar = oneEnum("bar1")
    //          }
    //        }).map(res => (res.head.t, res.head.tx, res.head.txInstant))
    //
    //        _ <- Schema.t.a.enumm.get.map(_ ==> List(
    //          (t, ":Foo/bar", "bar1"),
    //        ))
    //
    //        // Add one more enum
    //        (t2, tx2, d2) <- transact(schema {
    //          trait Foo {
    //            val bar = oneEnum("bar1", "bar2")
    //          }
    //        }).map(res => (res.head.t, res.head.tx, res.head.txInstant))
    //
    //        _ <- Schema.t.a.enumm.a1.get.map(_ ==> List(
    //          (t, ":Foo/bar", "bar1"),
    //          (t, ":Foo/bar", "bar2"), // Note that `t` points to the initial transaction
    //        ))
    //
    //        // Schema.enumm.getHistory not supported
    //        _ <- Schema.t.a.enumm.getHistory
    //          .map(_ ==> "Unexpected success")
    //          .recover { case MoleculeException(msg, _) =>
    //            msg ==> "Retrieving historical enum values with `Schema` is not supported since they " +
    //              "are entities having their own timeline independently from schema attributes. " +
    //              "Instead, please call `conn.getEnumHistory` to retrieve historical enum values."
    //          }
    //
    //        // Instead, use getEnumHistory
    //        _ <- conn.getEnumHistory.map(_ ==> List(
    //          (":Foo/bar", t, tx0, d0, "bar1", true),
    //          (":Foo/bar", t2, tx2, d2, "bar2", true),
    //        ))
    //
    //        // Just omitting an enum value in the data model doesn't retract it
    //        _ <- transact(schema {
    //          trait Foo {
    //            val bar = oneEnum("bar2")
    //          }
    //        })
    //        // bar1 still exists
    //        _ <- Schema.t.a.enumm.a1.get.map(_ ==> List(
    //          (t, ":Foo/bar", "bar1"),
    //          (t, ":Foo/bar", "bar2"),
    //        ))
    //
    //        // Instead, use conn.retractEnum to retract an enum value
    //        // (each enum value is a referenced entity that we can retract)
    //        (t5, tx5, d5) <- conn.retractEnum(":Foo.bar/bar1")
    //          .map(res => (res.t, res.tx, res.txInstant))
    //
    //        // Now bar1 is not available and we can compile and transact our updated data model
    //        _ <- Schema.t.a.enumm.get.map(_ ==> List(
    //          (t, ":Foo/bar", "bar2"),
    //        ))
    //
    //        _ <- transact(schema {
    //          trait Foo {
    //            val bar = oneEnum("bar2")
    //          }
    //        })
    //
    //        _ <- conn.getEnumHistory.map(_ ==> List(
    //          (":Foo/bar", t, tx0, d0, "bar1", true),
    //          (":Foo/bar", t2, tx2, d2, "bar2", true),
    //          (":Foo/bar", t5, tx5, d5, "bar1", false), // bar1 retracted in tx5
    //        ))
    //      } yield ()
    //    }
  }
}