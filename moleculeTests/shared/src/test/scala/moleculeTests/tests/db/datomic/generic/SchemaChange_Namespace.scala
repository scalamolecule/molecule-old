package moleculeTests.tests.db.datomic.generic

import molecule.core.data.model._
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.datomic.api.out13._
import moleculeTests.setup.AsyncTestSuite
import utest._


object SchemaChange_Namespace extends AsyncTestSuite {

  lazy val tests = Tests {

    "New" - empty { implicit futConn =>
      for {
        _ <- transact(schema {
          trait Foo {
            val int = oneInt
          }
        })

        // Transact updated data model with added namespace having multiple attributes
        _ <- transact(schema {
          trait Foo {
            val int = oneInt
          }
          trait Bar {
            val str  = oneString
            val long = oneLong
          }
        })

        // Schema has 2 namespaces
        _ <- Schema.ns.d1.a.d2.attr.get.map(_ ==> List(
          ("Foo", ":Foo/int", "int"),
          ("Bar", ":Bar/str", "str"),
          ("Bar", ":Bar/long", "long"),
        ))
      } yield ()
    }


    "Rename" - empty { implicit futConn =>
      for {
        conn <- futConn

        t0 <- transact(schema {
          trait Foo {
            val int = oneInt
            val str = oneString
          }
        }).map(_.last.t)

        // Renaming a namespace means changing the namespace prefix of all of its attributes

        // 1. Call changeNamespaceName to change the schema definition in the database
        t1 <- conn.changeNamespaceName("Foo", "Bar").map(_.t)
        // 2. Update namespace name in the data model
        //      trait Bar {
        //        val int  = oneInt
        //        val str = oneString
        //      }
        // 3. Change all uses of the old namespace `Foo` to `Bar` in your code.
        // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).

        // Foo attributes have been renamed to belong to Bar
        _ <- Schema.a.attr.get.map(_ ==> List(
          (":Bar/str", "str"),
          (":Bar/int", "int"),
        ))

        // See name changes with getHistory
        // Note that `a` refers to the current attribute name
        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
          (t0, ":Bar/int", ":Foo/int"),
          (t0, ":Bar/str", ":Foo/str"),
          (t1, ":Bar/int", ":Bar/int"),
          (t1, ":Bar/str", ":Bar/str"),
        ))
      } yield ()
    }


    "Retire" - empty { implicit futConn =>
      // Namespaces can be "retired" which means that all their attributes are marked
      // with a `-` prefix to exclude them from current Schema query results.
      for {
        conn <- futConn

        // Initial data model
        t0 <- transact(schema {
          trait Foo {
            val int = oneInt
            val str = oneString
          }
          trait Bar {
            val long = oneLong
          }
        }).map(_.last.t)

        _ <- Schema.t.a.d1.get.map(_ ==> List(
          (t0, ":Foo/str"),
          (t0, ":Foo/int"),
          (t0, ":Bar/long"),
        ))

        // Checks

        // Invalid namespace name
        _ <- conn.retireNamespace(":Foo")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Invalid namespace name `:Foo`. Expecting namespace name in the format `[a-zA-Z][a-zA-Z0-9_]+`"
          }

        // Unknown namespace
        _ <- conn.retireNamespace("Fooo")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Couldn't find namespace `Fooo`."
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


        // Now we can retire the namespace - in 5 steps:

        // 1. Call retireAttr to retire all attributes of the namespace in the database.
        t4 <- conn.retireNamespace("Foo").map(_.t)
        // 2. Remove namespace `Foo` from the data model.
        //      trait Bar {
        //        val long = oneLong
        //      }
        // 3. Remove all uses of attributes from the old namespace `Foo` in your code.
        // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).


        // Attributes in `Foo` namespace are no longer available
        _ <- Schema.t.a.get.map(_ ==> List(
          (t0, ":Bar/long"),
        ))

        // Retired attributes are simply marked with a `-` prefix to exclude them from current Schema queries.
        _ <- Schema.t.ns.attr.a.ident.getHistory.map(_ ==> List(
          (t0, "Foo", "int", ":-Foo/int", ":Foo/int"), // :Foo/int created (`a` shows current retired attribute ident `:-Foo/int`)
          (t0, "Foo", "str", ":-Foo/str", ":Foo/str"), // :Foo/str created (`a` shows current retired attribute ident `:-Foo/str`)
          (t0, "Bar", "long", ":Bar/long", ":Bar/long"),

          // Namespace Foo retired
          (t4, "Foo", "int", ":-Foo/int", ":-Foo/int"), // :Foo/int retired
          (t4, "Foo", "str", ":-Foo/str", ":-Foo/str"), // :Foo/str retired
        ))
      } yield ()
    }
  }
}