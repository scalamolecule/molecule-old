package moleculeTests.tests.db.datomic.generic

import molecule.core.data.model._
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.datomic.api.out7._
import moleculeTests.setup.AsyncTestSuite
import utest._


object SchemaChange_Namespace extends AsyncTestSuite {

  lazy val tests = Tests {

    "Add" - empty { implicit futConn =>
      for {
        t1 <- transact(schema {
          trait Foo {
            val int = oneInt
          }
        }).map(_.last.t)

        // Transact updated data model with added namespace having multiple attributes
        t2 <- transact(schema {
          trait Foo {
            val int  = oneInt
            val bool = oneBoolean
          }
          trait Bar {
            val str  = oneString
            val long = oneLong
          }
        }).map(_.last.t)

        // Current namespace definitions
        _ <- Schema.t.ns.a.d1.get.map(_ ==> List(
          (t1, "Foo", ":Foo/int"),
          (t2, "Foo", ":Foo/bool"),
          (t2, "Bar", ":Bar/str"),
          (t2, "Bar", ":Bar/long"),
        ))

        // Namespace definition history
        // Since we have only added to the schema in this case, `getHistory` will show the same as `get`
        _ <- Schema.t.ns.a.getHistory.map(_ ==> List(
          (t1, "Foo", ":Foo/int"),
          (t2, "Foo", ":Foo/bool"),
          (t2, "Bar", ":Bar/str"),
          (t2, "Bar", ":Bar/long"),
        ))

        // Specific namespace(s)
        _ <- Schema.t.ns("Foo", "Bar").a.getHistory.map(_ ==> List(
          (t1, "Foo", ":Foo/int"),
          (t2, "Foo", ":Foo/bool"),
          (t2, "Bar", ":Bar/str"),
          (t2, "Bar", ":Bar/long"),
        ))
        _ <- Schema.t.ns(Seq("Foo", "Bar")).a.getHistory.map(_ ==> List(
          (t1, "Foo", ":Foo/int"),
          (t2, "Foo", ":Foo/bool"),
          (t2, "Bar", ":Bar/str"),
          (t2, "Bar", ":Bar/long"),
        ))
        _ <- Schema.t.ns("Foo").a.getHistory.map(_ ==> List(
          (t1, "Foo", ":Foo/int"),
          (t2, "Foo", ":Foo/bool"),
        ))
        _ <- Schema.t.ns(Seq("Bar")).a.getHistory.map(_ ==> List(
          (t2, "Bar", ":Bar/str"),
          (t2, "Bar", ":Bar/long"),
        ))
        _ <- Schema.t.ns_("Foo").a.getHistory.map(_ ==> List(
          (t1, ":Foo/int"),
          (t2, ":Foo/bool"),
        ))
        _ <- Schema.t.ns_(Seq("Bar")).a.getHistory.map(_ ==> List(
          (t2, ":Bar/str"),
          (t2, ":Bar/long"),
        ))

        // Excluding namespace(s)
        _ <- Schema.t.ns.not("Bar").a.getHistory.map(_ ==> List(
          (t1, "Foo", ":Foo/int"),
          (t2, "Foo", ":Foo/bool"),
        ))
        _ <- Schema.t.ns.not(Seq("Foo")).a.getHistory.map(_ ==> List(
          (t2, "Bar", ":Bar/str"),
          (t2, "Bar", ":Bar/long"),
        ))
        _ <- Schema.t.ns_.not("Bar").a.getHistory.map(_ ==> List(
          (t1, ":Foo/int"),
          (t2, ":Foo/bool"),
        ))
        _ <- Schema.t.ns_.not(Seq("Foo")).a.getHistory.map(_ ==> List(
          (t2, ":Bar/str"),
          (t2, ":Bar/long"),
        ))
        _ <- Schema.t.ns.not("Bar", "Foo").a.getHistory.map(_ ==> Nil)
        _ <- Schema.t.ns.not(Seq("Bar", "Foo")).a.getHistory.map(_ ==> Nil)
      } yield ()
    }


    "Rename" - empty { implicit futConn =>
      for {
        conn <- futConn

        t1 <- transact(schema {
          trait Foo {
            val int = oneInt
            val str = oneString
          }
          trait Baz {
            val boo = oneBoolean
          }
        }).map(_.last.t)

        // Renaming a namespace means changing the namespace prefix of all of its attributes

        // 1. Call changeNamespaceName to change the schema definition in the database
        t2 <- conn.changeNamespaceName("Foo", "Bar").map(_.t)
        // 2. Update namespace name in the data model
        //      trait Bar {
        //        val int = oneInt
        //        val str = oneString
        //      }
        // 3. Change all uses of the old namespace `Foo` to `Bar` in your code.
        // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).

        // Foo attributes have been renamed to belong to Bar
        // Note that `t` refers to the transaction where the attribute entity was originally added
        _ <- Schema.t.a1.ns.a.a2.ident.get.map(_ ==> List(
          (t1, "Bar", ":Bar/int", ":Bar/int"),
          (t1, "Bar", ":Bar/str", ":Bar/str"),
          (t1, "Baz", ":Baz/boo", ":Baz/boo"),
        ))

        // See name changes with getHistory
        // Note that ns/a refers to the current namespace/attribute name
        _ <- Schema.t.ns.a.ident.getHistory.map(_ ==> List(
          (t1, "Bar", ":Bar/int", ":Foo/int"),
          (t1, "Bar", ":Bar/str", ":Foo/str"),
          (t1, "Baz", ":Baz/boo", ":Baz/boo"),
          (t2, "Bar", ":Bar/int", ":Bar/int"),
          (t2, "Bar", ":Bar/str", ":Bar/str"),
        ))

        // Specific namespace(s)
        _ <- Schema.t.ns("Bar", "Baz").a.ident.getHistory.map(_ ==> List(
          (t1, "Bar", ":Bar/int", ":Foo/int"),
          (t1, "Bar", ":Bar/str", ":Foo/str"),
          (t1, "Baz", ":Baz/boo", ":Baz/boo"),
          (t2, "Bar", ":Bar/int", ":Bar/int"),
          (t2, "Bar", ":Bar/str", ":Bar/str"),
        ))
        _ <- Schema.t.ns(Seq("Bar", "Baz")).a.ident.getHistory.map(_ ==> List(
          (t1, "Bar", ":Bar/int", ":Foo/int"),
          (t1, "Bar", ":Bar/str", ":Foo/str"),
          (t1, "Baz", ":Baz/boo", ":Baz/boo"),
          (t2, "Bar", ":Bar/int", ":Bar/int"),
          (t2, "Bar", ":Bar/str", ":Bar/str"),
        ))
        _ <- Schema.t.ns("Bar").a.ident.getHistory.map(_ ==> List(
          (t1, "Bar", ":Bar/int", ":Foo/int"),
          (t1, "Bar", ":Bar/str", ":Foo/str"),
          (t2, "Bar", ":Bar/int", ":Bar/int"),
          (t2, "Bar", ":Bar/str", ":Bar/str"),
        ))
        _ <- Schema.t.ns(Seq("Baz")).a.ident.getHistory.map(_ ==> List(
          (t1, "Baz", ":Baz/boo", ":Baz/boo"),
        ))

        // Excluding namespace(s)
        _ <- Schema.t.ns.not("Baz").a.ident.getHistory.map(_ ==> List(
          (t1, "Bar", ":Bar/int", ":Foo/int"),
          (t1, "Bar", ":Bar/str", ":Foo/str"),
          (t2, "Bar", ":Bar/int", ":Bar/int"),
          (t2, "Bar", ":Bar/str", ":Bar/str"),
        ))
        _ <- Schema.t.ns.not(Seq("Bar")).a.ident.getHistory.map(_ ==> List(
          (t1, "Baz", ":Baz/boo", ":Baz/boo"),
        ))
        _ <- Schema.t.ns.not("Bar", "Baz").a.ident.getHistory.map(_ ==> Nil)
        _ <- Schema.t.ns.not(Seq("Bar", "Baz")).a.ident.getHistory.map(_ ==> Nil)
      } yield ()
    }


    "Retire" - empty { implicit futConn =>
      // Namespaces can be "retired" which means that all their attributes are marked
      // with a `-` prefix to exclude them from current Schema query results.
      for {
        conn <- futConn

        // Initial data model
        t1 <- transact(schema {
          trait Foo {
            val int = oneInt
            val str = oneString
          }
          trait Bar {
            val long = oneLong
          }
        }).map(_.last.t)

        _ <- Schema.t.a.d1.get.map(_ ==> List(
          (t1, ":Foo/str"),
          (t1, ":Foo/int"),
          (t1, ":Bar/long"),
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
        _ <- Schema.t.ns.a.get.map(_ ==> List(
          (t1, "Bar", ":Bar/long"),
        ))

        // Retired attributes are simply marked with a `-` prefix to exclude them from current Schema queries.
        _ <- Schema.t.ns.a.ident.getHistory.map(_ ==> List(
          (t1, "Foo", ":-Foo/int", ":Foo/int"), // :Foo/int created (`a` shows current retired attribute ident `:-Foo/int`)
          (t1, "Foo", ":-Foo/str", ":Foo/str"), // :Foo/str created (`a` shows current retired attribute ident `:-Foo/str`)
          (t1, "Bar", ":Bar/long", ":Bar/long"),

          // Namespace Foo retired
          (t4, "Foo", ":-Foo/int", ":-Foo/int"), // :Foo/int retired
          (t4, "Foo", ":-Foo/str", ":-Foo/str"), // :Foo/str retired
        ))

        // Specific namespace(s)
        _ <- Schema.t.ns("Foo", "Bar").a.ident.getHistory.map(_ ==> List(
          (t1, "Foo", ":-Foo/int", ":Foo/int"),
          (t1, "Foo", ":-Foo/str", ":Foo/str"),
          (t1, "Bar", ":Bar/long", ":Bar/long"),
          (t4, "Foo", ":-Foo/int", ":-Foo/int"),
          (t4, "Foo", ":-Foo/str", ":-Foo/str"),
        ))
        _ <- Schema.t.ns(Seq("Foo", "Bar")).a.ident.getHistory.map(_ ==> List(
          (t1, "Foo", ":-Foo/int", ":Foo/int"),
          (t1, "Foo", ":-Foo/str", ":Foo/str"),
          (t1, "Bar", ":Bar/long", ":Bar/long"),
          (t4, "Foo", ":-Foo/int", ":-Foo/int"),
          (t4, "Foo", ":-Foo/str", ":-Foo/str"),
        ))
        _ <- Schema.t.ns("Foo").a.ident.getHistory.map(_ ==> List(
          (t1, "Foo", ":-Foo/int", ":Foo/int"),
          (t1, "Foo", ":-Foo/str", ":Foo/str"),
          (t4, "Foo", ":-Foo/int", ":-Foo/int"),
          (t4, "Foo", ":-Foo/str", ":-Foo/str"),
        ))
        _ <- Schema.t.ns(Seq("Bar")).a.ident.getHistory.map(_ ==> List(
          (t1, "Bar", ":Bar/long", ":Bar/long"),
        ))
        _ <- Schema.t.ns_("Foo").a.ident.getHistory.map(_ ==> List(
          (t1, ":-Foo/int", ":Foo/int"),
          (t1, ":-Foo/str", ":Foo/str"),
          (t4, ":-Foo/int", ":-Foo/int"),
          (t4, ":-Foo/str", ":-Foo/str"),
        ))
        _ <- Schema.t.ns_(Seq("Bar")).a.ident.getHistory.map(_ ==> List(
          (t1, ":Bar/long", ":Bar/long"),
        ))

        // Excluding namespace(s)
        _ <- Schema.t.ns.not("Bar").a.ident.getHistory.map(_ ==> List(
          (t1, "Foo", ":-Foo/int", ":Foo/int"),
          (t1, "Foo", ":-Foo/str", ":Foo/str"),
          (t4, "Foo", ":-Foo/int", ":-Foo/int"),
          (t4, "Foo", ":-Foo/str", ":-Foo/str"),
        ))
        _ <- Schema.t.ns.not(Seq("Foo")).a.ident.getHistory.map(_ ==> List(
          (t1, "Bar", ":Bar/long", ":Bar/long"),
        ))
        _ <- Schema.t.ns_.not("Bar").a.ident.getHistory.map(_ ==> List(
          (t1, ":-Foo/int", ":Foo/int"),
          (t1, ":-Foo/str", ":Foo/str"),
          (t4, ":-Foo/int", ":-Foo/int"),
          (t4, ":-Foo/str", ":-Foo/str"),
        ))
        _ <- Schema.t.ns_.not(Seq("Foo")).a.ident.getHistory.map(_ ==> List(
          (t1, ":Bar/long", ":Bar/long"),
        ))
        _ <- Schema.t.ns.not("Bar", "Foo").a.ident.getHistory.map(_ ==> Nil)
        _ <- Schema.t.ns.not(Seq("Bar", "Foo")).a.ident.getHistory.map(_ ==> Nil)

      } yield ()
    }
  }
}