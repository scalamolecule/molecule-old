package moleculeTests.tests.db.datomic.generic

import molecule.core.data.model._
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.datomic.api.out13._
import moleculeTests.setup.AsyncTestSuite
import utest._


object SchemaChange_Partition extends AsyncTestSuite {

  lazy val tests = Tests {

    "New" - empty { implicit futConn =>
      for {
        t1 <- transact(schema {
          object part1 {
            trait Foo {
              val int = oneInt
            }
          }
        }).map(_.last.t)

        // When partitions are used, the schema is transacted in two steps:
        // t0: partition is installed
        // t1: attributes are installed

        // Schema has 1 partition
        _ <- Schema.part.a.get.map(_ ==> List(
          ("part1", ":part1_Foo/int")
        ))
        _ <- Schema.t.part.a.getHistory.map(_ ==> List(
          (t1, "part1", ":part1_Foo/int"),
        ))

        // Transact data model with added partition having multiple namespaces
        t3 <- transact(schema {
          object part1 {
            trait Foo {
              val int = oneInt
            }
          }
          object part2 {
            trait Bar {
              val str = oneString
            }
            trait Baz {
              val long = oneLong
            }
          }
        }).map(_.last.t)

        // Schema now has 2 partitions
        _ <- Schema.part.a1.a.get.map(_ ==> List(
          ("part1", ":part1_Foo/int"),
          ("part2", ":part2_Bar/str"),
          ("part2", ":part2_Baz/long"),
        ))
        _ <- Schema.t.part.a.getHistory.map(_ ==> List(
          (t1, "part1", ":part1_Foo/int"),
          (t3, "part2", ":part2_Bar/str"),
          (t3, "part2", ":part2_Baz/long"),
        ))
      } yield ()
    }


    "Rename" - empty { implicit futConn =>
      for {
        conn <- futConn

        t1 <- transact(schema {
          object part1 {
            trait Foo {
              val int = oneInt
              val str = oneString
            }
            trait Bar {
              val long = oneLong
            }
          }
        }).map(_.last.t)

        _ <- Schema.part.ns.d1.a.get.map(_ ==> List(
          ("part1", "Foo", ":part1_Foo/int"),
          ("part1", "Foo", ":part1_Foo/str"),
          ("part1", "Bar", ":part1_Bar/long"),
        ))

        // Renaming a partition means changing the namespace prefix of all of its attributes in eachj namespace

        // 1. Call changeNamespaceName to change the schema definition in the database
        t2 <- conn.changePartitionName("part1", "part2").map(_.t)
        // 2. Update partition name in the data model
        //      object part2 {
        //        trait Foo {
        //          val int = oneInt
        //          val str = oneString
        //        }
        //        trait Bar {
        //          val str = oneString
        //        }
        //      }
        // 3. Change all uses of the old namespace `Foo` to `Bar` in your code.
        // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).

        // Foo attributes have been renamed to belong to part2

        _ <- Schema.part.ns.d1.a.get.map(_ ==> List(
          ("part2", "Foo", ":part2_Foo/int"),
          ("part2", "Foo", ":part2_Foo/str"),
          ("part2", "Bar", ":part2_Bar/long"),
        ))

        // See name changes with getHistory
        // Note that t, part nad ns refer to the current values
        _ <- Schema.t.part.ns.a.ident.getHistory.map(_ ==> List(
          (t1, "part2", "Foo", ":part2_Foo/int", ":part1_Foo/int"),
          (t1, "part2", "Foo", ":part2_Foo/str", ":part1_Foo/str"),
          (t1, "part2", "Bar", ":part2_Bar/long", ":part1_Bar/long"),

          (t2, "part2", "Foo", ":part2_Foo/int", ":part2_Foo/int"),
          (t2, "part2", "Foo", ":part2_Foo/str", ":part2_Foo/str"),
          (t2, "part2", "Bar", ":part2_Bar/long", ":part2_Bar/long"),
        ))
      } yield ()
    }


    "Retire" - empty { implicit futConn =>
      // Namespaces can be "retired" which means that all their attributes are marked
      // with a `-` prefix to exclude them from current Schema query results.
      for {
        conn <- futConn

        // Initial data model
        t1 <- transact(schema {
          object part1 {
            trait Foo {
              val int = oneInt
              val str = oneString
            }
            trait Bar {
              val long = oneLong
            }
          }
          object part2 {
            trait Baz {
              val bool = oneBoolean
            }
          }
        }).map(_.last.t)

        _ <- Schema.t.a.a1.get.map(_ ==> List(
          (t1, ":part1_Bar/long"),
          (t1, ":part1_Foo/int"),
          (t1, ":part1_Foo/str"),
          (t1, ":part2_Baz/bool"),
        ))

        // Checks

        // Invalid partition name
        _ <- conn.retirePartition("Part1")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Invalid partition name `Part1`. Expecting partition name in the format `[a-z][a-zA-Z0-9]+`"
          }

        // Unknown partition
        _ <- conn.retirePartition("part3")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Couldn't find partition `part3`."
          }

        // Attribute with data asserted can't be retired
        e <- conn.transact("""[[:db/add "-1" :part1_Foo/int 1]]""").map(_.eid)
        _ <- conn.retirePartition("part1")
          .map(_ ==> "Unexpected success")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Can't retire attribute `:part1_Foo/int` having 1 value asserted. " +
              "Please retract value before retiring attribute."
          }

        // Retract data asserted with int attribute
        _ <- conn.transact(s"[[:db/retract $e :part1_Foo/int 1]]")


        // Now we can retire the partition - in 5 steps:

        // 1. Call retireAttr to retire all attributes of the partition in the database.
        t5 <- conn.retirePartition("part1").map(_.t)
        // 2. Remove partition `part1` from the data model.
        //        object part2 {
        //          trait Baz {
        //            val bool = oneBoolean
        //          }
        //        }
        // 3. Remove all uses of attributes from the old partition `part1` in your code.
        // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).


        // Attributes in `part2` partition are no longer available
        _ <- Schema.t.part.ns.attr.a.get.map(_ ==> List(
          (t1, "part2", "Baz", "bool", ":part2_Baz/bool"),
        ))

        // Retired attributes are simply marked with a `-` prefix to exclude them from current Schema queries.
        _ <- Schema.t.part.ns.attr.a.ident.getHistory.map(_ ==> List(
          (t1, "part1", "Foo", "int", ":-part1_Foo/int", ":part1_Foo/int"),
          (t1, "part1", "Foo", "str", ":-part1_Foo/str", ":part1_Foo/str"),
          (t1, "part1", "Bar", "long", ":-part1_Bar/long", ":part1_Bar/long"),
          (t1, "part2", "Baz", "bool", ":part2_Baz/bool", ":part2_Baz/bool"),

          // Partition part1 retired
          (t5, "part1", "Foo", "int", ":-part1_Foo/int", ":-part1_Foo/int"),
          (t5, "part1", "Foo", "str", ":-part1_Foo/str", ":-part1_Foo/str"),
          (t5, "part1", "Bar", "long", ":-part1_Bar/long", ":-part1_Bar/long"),
        ))
      } yield ()
    }
  }
}