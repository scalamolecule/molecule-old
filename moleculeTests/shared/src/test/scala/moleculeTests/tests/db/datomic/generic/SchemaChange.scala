package moleculeTests.tests.db.datomic.generic

import molecule.core.data.model.{oneInt, oneString}
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out13._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.db.datomic.generic.SchemaHistory.transact
import utest._
import scala.concurrent.Future

/*
Changing your schema ...
 */
object SchemaChange extends AsyncTestSuite {

  lazy val tests = Tests {

    "Attribute" - {

      "New" - empty { implicit futConn =>
        for {
          // Schema has no attributes yet
          _ <- Schema.t.a.valueType.cardinality.get.map(_ ==> Nil)

          // Initial data model
          _ <- transact(schema {
            trait Foo {
              val int = oneInt
            }
          })

          // Schema now has 1 attribute
          _ <- Schema.t.a.get.map(_ ==> List(
            (1000, ":Foo/int"),
          ))


          // Add an attribute in 3 steps:

          // 1. Add new attribute to data model.
          //    (Previously transacted schema attributes are automatically ignored by Datomic. So we can safely transact the
          //     whole schema anytime we want).
          // 2. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
          // 3. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbNae)`
          //    to transact the updated schema (depending on which system you use).

          // For testing purpose, we transact the schema of our updated data model here
          _ <- transact(schema {
            trait Foo {
              val int = oneInt
              val str = oneString
            }
          })

          // Schema has 2 attributes
          _ <- Schema.t.a.valueType.cardinality.get.map(_ ==> List(
            (1000, ":Foo/int", "long", "one"),
            (1001, ":Foo/str", "string", "one"),
          ))

          // Since we have just added to the schema, `getHistory` will show the same as `get`
          _ <- Schema.t.a.valueType.cardinality.getHistory.map(_ ==> List(
            (1000, ":Foo/int", "long", "one"),
            (1001, ":Foo/str", "string", "one"),
          ))
        } yield ()
      }


      "Rename" - empty { implicit futConn =>
        for {
          conn <- futConn

          // Initial data model
          _ <- transact(schema {
            trait Foo {
              val int = oneInt
            }
          })

          // Rename an attribute in 5 steps:

          // 1. Call changeAttrName to change the schema definition in the database
          _ <- conn.changeAttrName(":Foo/int", ":Foo/int2")
          // 2. Update name of attribute in the data model
          // 3. Prepare for generating new boilerplate code by changing old uses of `int` to `int2` in your code
          //    (will not compile until you have re-generated the molecule boilerplate code)
          // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
          // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbNae)`
          //    to transact the updated schema (depending on which system you use).

          // For testing purpose, we transact the schema of our updated data model here
          _ <- transact(schema {
            trait Foo {
              val int2 = oneInt.noHistory
            }
          })

          // int has correctly been renamed to int2
          // Note that `t` refers to the transaction where the attribute was originally added
          _ <- Schema.t.a.get.map(_ ==> List(
            (1000, ":Foo/int2"),
          ))

          // See name changes with getHistory
          // Note that `a` refers to the current attribute name
          _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
            (1000, ":Foo/int2", ":Foo/int"),
            (1001, ":Foo/int2", ":Foo/int2"),
          ))


          // Checks .........................................

          // We can't just change an attribute name by changing its name in our data model since there's
          // no way to know that `int2` was earlier `int` (at least with multiple attributes).
          // Doing so would just create a new `int2` and `int` would still exist.
          //          _ <- transact(schema {
          //            trait Foo {
          //              val int2 = oneInt
          //            }
          //          })

          // Invalid attribute name
          _ <- conn.changeAttrName(":Foo/int", "Foo/int2")
            .map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              msg ==> "Invalid attribute name `Foo/int2`. " +
                "Expecting attribute name in the format `:<Ns>/<attr>` or `:<part_Ns>/<attr>`"
            }

          // Non-existing current attribute
          _ <- conn.changeAttrName(":Foo/bar", ":Foo/int2")
            .map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              msg ==> "Couldn't find attribute `:Foo/bar` in the database."
            }


          // Repurpose attribute .......................

          // If we later want to use :Foo/int again for another purpose we can do the following 3 steps:

          // 1. Add the attribute again to the data model
          // 2. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
          // 3. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbNae)`
          //    to transact the updated schema (depending on which system you use).

          // For testing purpose, we transact the schema of our updated data model here
          _ <- transact(schema {
            trait Foo {
              val int  = oneInt.noHistory
              val int2 = oneInt.noHistory
            }
          })

          // Now both attributes exist
          _ <- Schema.t.a.get.map(_ ==> List(
            (1000, ":Foo/int2"),
            (1003, ":Foo/int"),
          ))

          // Check name changes with getHistory
          // Note that `a` refers to the current attribute name
          _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
            (1000, ":Foo/int2", ":Foo/int"),
            (1001, ":Foo/int2", ":Foo/int2"),
          ))
        } yield ()
      }


      "Rename, checking data" - empty { implicit futConn =>
        for {
          conn <- futConn

          // Initial data model
          _ <- transact(schema {
            trait Foo {
              val int = oneInt.noHistory
            }
          })

          // Add int data
          _ <- conn.transact("""[[:db/add "-1" :Foo/int 1]]""")
          _ <- conn.query("[:find ?b :where [?a :Foo/int ?b]]").map(_ ==> List(List(1)))

          // int -> int2
          _ <- conn.changeAttrName(":Foo/int", ":Foo/int2")

          // int and int2 still points to the same attribute. This doesn't affect your code since only the `int2`
          // attribute will be available for your molecules after you have re-compiled the updated data model.
          _ <- conn.query("[:find ?b :where [?a :Foo/int ?b]]").map(_ ==> List(List(1)))
          _ <- conn.query("[:find ?b :where [?a :Foo/int2 ?b]]").map(_ ==> List(List(1)))


          // Repurpose attribute name .............................

          _ <- transact(schema {
            trait Foo {
              val int  = oneInt.noHistory
              val int2 = oneInt.noHistory
            }
          })

          // `int` is now its own attribute and no longer points to the same attribute as `int2`
          _ <- conn.query("[:find ?b :where [?a :Foo/int ?b]]").map(_ ==> Nil)
          _ <- conn.query("[:find ?b :where [?a :Foo/int2 ?b]]").map(_ ==> List(List(1)))

        } yield ()
      }


      "Retire" - empty { implicit futConn =>
        // Attributes can be "retired" which means that they are marked with a `-` prefix to exclude them from
        // current Schema query results.
        for {
          conn <- futConn

          // Initial data model
          _ <- transact(schema {
            trait Foo {
              val int = oneInt
              val str = oneString
            }
          })

          // int data
          e <- conn.transact("""[[:db/add "-1" :Foo/int 1]]""").map(_.eid)
          _ <- conn.query("[:find  ?b :where [?a :Foo/int ?b]]").map(_ ==> List(List(1)))

          // Checks

          // Invalid attribute name
          _ <- conn.retireAttr("int")
            .map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              msg ==> "Invalid attribute name `int`. " +
                "Expecting attribute name in the format `:<Ns>/<attr>` or `:<part_Ns>/<attr>`"
            }

          // Non-existing attribute
          _ <- conn.retireAttr(":Foo/bar")
            .map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              msg ==> "Couldn't find attribute `:Foo/bar` in the database."
            }

          // Attribute with data asserted can't be retired
          _ <- conn.retireAttr(":Foo/int")
            .map(_ ==> "Unexpected success")
            .recover { case MoleculeException(msg, _) =>
              msg ==> "Can't retire attribute `:Foo/int` having 1 value asserted. " +
                "Please retract value before retiring attribute."
            }

          // Retract data asserted with int attribute
          _ <- conn.transact(s"[[:db/retract $e :Foo/int 1]]")


          // Now we can retire the attribute - in 5 steps:

          // 1. Call retireAttr to retire the attribute in the database.
          _ <- conn.retireAttr(":Foo/int")
          // 2. Remove attribute definition in the data model.
          // 3. Prepare for generating new boilerplate code by removing old uses of `int` in your code.
          // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
          // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbNae)`
          //    to transact the updated schema (depending on which system you use).

          // For testing purpose, we transact the schema of our new data model here
          _ <- transact(schema {
            trait Foo {
              val str = oneString
            }
          })

          // int attribute is no longer available (current view filters out retired attributes)
          _ <- Schema.t.a.get.map(_ ==> List(
            (1000, ":Foo/str"),
          ))

          // Retired attributes are simply marked with a `-` prefix to exclude them from
          // current Schema queries as shown above.
          _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
            (1000, ":-Foo/int", ":Foo/int"),
            (1000, ":Foo/str", ":Foo/str"),
            (1004, ":-Foo/int", ":-Foo/int"), // :Foo/int retired
          ))


          // Un-retire attribute in 5 steps:

          // 1. Call changeAttrName to remove prefix used to mark it as retired
          _ <- conn.changeAttrName(":-Foo/int", ":Foo/int")
          // 2. Remove attribute definition in the data model.
          // 3. Prepare for generating new boilerplate code by removing old uses of `int` in your code.
          // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
          // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbNae)`
          //    to transact the updated schema (depending on which system you use).

          // For testing purpose, we transact the schema of our new data model here
          _ <- transact(schema {
            trait Foo {
              val int = oneInt
              val str = oneString
            }
          })

          _ <- Schema.t.a.get.map(_ ==> List(
            (1000, ":Foo/int"),
            (1000, ":Foo/str"),
          ))

          _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
            (1000, ":Foo/int", ":Foo/int"),
            (1000, ":Foo/str", ":Foo/str"),
            (1004, ":Foo/int", ":-Foo/int"), // :Foo/int retired
            (1006, ":Foo/int", ":Foo/int"), //  :Foo/int un-retired
          ))

          // We could also simply have updated our data model as above without first calling
          // changeAttrName. The only difference would then be that a new attribute named `:Foo/int`
          // would be created and the old retired `:-Foo/int` would remain. This wouldn't affect our
          // code since the retired attribute wouldn't be available in our code anyway.
        } yield ()
      }
    }


    "Cardinality" - {

      "one to many" - empty { implicit futConn =>
        for {
          _ <- transact(schema {
            trait Foo {
              val int = oneInt
            }
          })

          // Mandatory schema attributes
          _ <- Schema.t.a.valueType.cardinality.get.map(_ ==> List(
            (1000, ":Foo/int", "long", "one"),
          ))

          _ <- transact(schema {
            trait Foo {
              val int = oneInt
              val str = oneString
            }
          })

          // Mandatory schema attributes
          _ <- Schema.t.a.valueType.cardinality.get.map(_ ==> List(
            (1000, ":Foo/int", "long", "one"),
            (1001, ":Foo/str", "string", "one"),
          ))
        } yield ()
      }
    }

    "breaking" - {

      "Rename attribute" - empty { implicit futConn =>
        for {
          conn <- futConn

          _ <- transact(schema {
            trait Foo {
              val int = oneInt
            }
          })
          _ <- Schema.a.ident.get.map(_ ==> List(
            (":Foo/int", ":Foo/int"),
          ))

          //        _ <- conn.changeAttrName(":Foo/intX", ":Foo/int2")
          //          .map(_ ==> "Unexpected succes")
          //          .recover { case MoleculeException(msg, _) =>
          //            msg ==> "Couldn't find current attribute ident `:Foo/intX` in the database. " +
          //              s"Please check the supplied current ident in order to change the name."
          //          }
          //
          //        _ <- conn.changeAttrName(":Foo/int", ":Foo/int2")
          //        _ <- Schema.a.ident.get.map(_ ==> List(
          //          (":Foo/int2", ":Foo/int2"),
          //        ))
          //
          //        _ <- conn.changeAttrName(":Foo/int2", ":Bar/hello")
          //        _ <- Schema.a.ident.get.map(_ ==> List(
          //          (":Bar/hello", ":Bar/hello"),
          //        ))
          //
          //        _ = println("-----------------------")
          //        _ = Peer.q(
          //          """[:find  ?tx ?op ?a ?ident ?action ?v
          //            | :in    $ %
          //            | :where [:db.part/db :db.install/attribute ?a]
          //            |        [(datomic.api/ident $ ?a) ?ident]
          //            |        (entity-at ?a ?tx ?t ?inst ?op ?action ?v)
          //            |        [(>= ?tx 13194139534312)]
          //            |]""".stripMargin,
          //          db.getDatomicDb.asInstanceOf[Database].history(),
          //          """[
          //            |  [
          //            |    (entity-at [?e] ?tx ?t ?inst ?op ?actionIdent ?v)
          //            |    [?e ?actionAttr ?v ?tx ?op]
          //            |    [?actionAttr :db/ident ?actionIdent]
          //            |    [(datomic.api/tx->t ?tx) ?t]
          //            |    [?tx :db/txInstant ?inst]
          //            |  ]
          //            |]""".stripMargin
          //        ).asScala.toSeq.map { row0 =>
          //          val l = row0.asScala
          //          (l.head.toString.toLong, l(1).toString, l(2).toString.toLong, l(3).toString, l(4).toString, l(5).toString)
          //        }
          //          .sortBy(r => (r._1, r._3))
          //          .foreach { r =>
          //            if (r._1 != last) {
          //              println("")
          //            }
          //            last = r._1
          //            println(r)
          //          }
          //
          //        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
          //          (1000, ":Bar/hello", ":Foo/int"),
          //          (1001, ":Bar/hello", ":Foo/int2"),
          //          (1004, ":Bar/hello", ":Bar/hello"),
          //        ))
          //
          //        _ <- Schema.t.a.ident$.getHistory.map(_ ==> List(
          //          (1000, ":Bar/hello", ":Foo/int"),
          //          (1002, ":Bar/hello", ":Foo/int2"),
          //          (1004, ":Bar/hello", ":Bar/hello"),
          //        ))

        } yield ()
      }

      "Change cardinality" - empty { implicit futConn =>
        for {
          _ <- transact(schema {
            trait Foo {
              val int = oneInt
            }
          })

          // Mandatory schema attributes
          _ <- Schema.t.a.valueType.cardinality.get.map(_ ==> List(
            (1000, ":Foo/int", "long", "one"),
          ))

          _ <- transact(schema {
            trait Foo {
              val int = oneInt
              val str = oneString
            }
          })

          // Mandatory schema attributes
          _ <- Schema.t.a.valueType.cardinality.get.map(_ ==> List(
            (1000, ":Foo/int", "long", "one"),
            (1001, ":Foo/str", "string", "one"),
          ))
        } yield ()
      }


      "ident 4" - empty { implicit futConn =>
        for {
          _ <- transact(schema {
            trait Foo {
              val int = oneInt
            }
          })

          // Mandatory schema attributes
          _ <- Schema.t.a.valueType.cardinality.get.map(_ ==> List(
            (1000, ":Foo/int", "long", "one"),
          ))

          _ <- transact(schema {
            trait Foo {
              val int = oneInt
              val str = oneString
            }
          })

          // Mandatory schema attributes
          _ <- Schema.t.a.valueType.cardinality.get.map(_ ==> List(
            (1000, ":Foo/int", "long", "one"),
            (1001, ":Foo/str", "string", "one"),
          ))
        } yield ()
      }
    }
  }
}