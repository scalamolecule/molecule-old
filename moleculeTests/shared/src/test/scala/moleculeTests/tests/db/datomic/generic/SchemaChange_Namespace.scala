package moleculeTests.tests.db.datomic.generic

import molecule.core.data.model._
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.datomic.api.out13._
import moleculeTests.setup.AsyncTestSuite
import utest._

/*
Changing your schema ...
 */
object SchemaChange_Namespace extends AsyncTestSuite {

  lazy val tests = Tests {

    "New" - empty { implicit futConn =>
      for {
        _ <- transact(schema {
          trait Foo {
            val int = oneInt
          }
        })

        // Transact updated data model
        _ <- transact(schema {
          trait Foo {
            val int = oneInt
          }
          trait Bar {
            val str = oneString
          }
        })

        // Schema has 2 namespaces
        _ <- Schema.ns.a.get.map(_ ==> List(
          ("Foo", ":Foo/int"),
          ("Bar", ":Bar/str"),
        ))
      } yield ()
    }


    "Rename" - empty { implicit futConn =>
      for {
        conn <- futConn

        _ <- transact(schema {
          trait Foo {
            val int = oneInt
            val str = oneString
          }
        })

        // Renaming a namespace means changing the namespace prefix of all of its attributes

        // 1. Call changeNamespaceName to change the schema definition in the database
        _ <- conn.changeNamespaceName("Foo", "Bar")
        // 2. Update namespace name in the data model
        //      trait Bar {
        //        val int  = oneInt
        //        val str = oneString
        //      }
        // 3. Change all uses of the old namespace `Foo` to `Bar` in your code.
        // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbNae)`
        //    to transact the updated schema (depending on which system you use).

        // Foo attributes have been renamed to belong to Bar
        _ <- Schema.a.get.map(_ ==> List(
          ":Bar/str",
          ":Bar/int",
        ))

        // See name changes with getHistory
        // Note that `a` refers to the current attribute name
        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
          (1000, ":Bar/int", ":Foo/int"),
          (1000, ":Bar/str", ":Foo/str"),
          (1001, ":Bar/int", ":Bar/int"),
          (1001, ":Bar/str", ":Bar/str"),
        ))
      } yield ()
    }


    "Retire" - empty { implicit futConn =>
      // Namespaces can be "retired" which means that all their attributes are marked
      // with a `-` prefix to exclude them from current Schema query results.
      for {
        conn <- futConn

        // Initial data model
        _ <- transact(schema {
          trait Foo {
            val int = oneInt
            val str = oneString
          }
          trait Bar {
            val long = oneLong
          }
        })
        _ <- Schema.t.a.get.map(_ ==> List(
          (1000, ":Foo/int"),
          (1000, ":Foo/str"),
          (1000, ":Bar/long"),
        ))

        // Checks

        // Invalid attribute name
        _ <- conn.retireNamespace(":Foo")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Invalid namespace name `:Foo`. Expecting namespace name in the format `[a-zA-Z][a-zA-Z0-9_]+`"
          }

        // Attribute with data asserted can't be retired
        e <- conn.transact("""[[:db/add "-1" :Foo/int 1]]""").map(_.eid)
        _ <- conn.retireNamespace("Foo")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Can't retire attribute `:Foo/int` having 1 value asserted. " +
              "Please retract value before retiring attribute."
          }

        // Retract data asserted with int attribute
        _ <- conn.transact(s"[[:db/retract $e :Foo/int 1]]")


        // Now we can retire the attribute - in 5 steps:

        // 1. Call retireAttr to retire the attribute in the database.
        _ <- conn.retireNamespace("Foo")
        // 2. Remove namespace `Foo` from the data model.
        //      trait Bar {
        //        val long = oneLong
        //      }
        // 3. Change all uses of the old namespace `Foo` to `Bar` in your code.
        // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbNae)`
        //    to transact the updated schema (depending on which system you use).


        // Foo attributes are no longer available
        _ <- Schema.t.a.get.map(_ ==> List(
          (1000, ":Bar/long"),
        ))

        // Retired attributes are simply marked with a `-` prefix to exclude them from
        // current Schema queries as shown above.
        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
          (1000, ":-Foo/int", ":Foo/int"), // :Foo/int created (`a` shows current retired attribute ident `:-Foo/int`)
          (1000, ":-Foo/str", ":Foo/str"), // :Foo/str created (`a` shows current retired attribute ident `:-Foo/str`)
          (1000, ":Bar/long", ":Bar/long"),
          (1004, ":-Foo/int", ":-Foo/int"), // :Foo/int retired
          (1004, ":-Foo/str", ":-Foo/str"), // :Foo/str retired
        ))


        // Data model out of sync with database schema
        outOfSyncSchema = schema {
          trait Foo {
            val int  = oneInt
            val strX = oneString
          }
        }
        _ <- conn.updateConnProxy(outOfSyncSchema).retireNamespace("Foo")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Couldn't find attribute `:Foo/strX` in the database schema."
          }
      } yield ()
    }
  }
}