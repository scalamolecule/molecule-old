package moleculeTests

import molecule.core.data.model.oneInt
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.core.util.{Helpers, JavaUtil}
import molecule.datomic.api.in1_out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.db.datomic.generic.SchemaChange_Attr.{empty, transact}
import utest._

object Adhoc extends AsyncTestSuite with Helpers with JavaUtil {


  lazy val tests = Tests {

    //    "core" - core { implicit futConn =>
    //
    //        println("core....")
    //      for {
    //        conn <- futConn
    //
    //        _ <- Ns.int(1).save
    //        _ <- Ns.int.get.map(_ ==> List(1))
    //
    ///*
    //testOnly moleculeTests.tests.db.datomic.generic.SchemaChange_AttrOptions
    //testOnly moleculeTests.tests.db.datomic.generic.SchemaChange_Namespace
    //testOnly moleculeTests.tests.db.datomic.generic.SchemaChange_Attr
    //testOnly moleculeTests.tests.db.datomic.generic.SchemaChange_Partition
    // */
    //
    //
    //      } yield ()
    //    }

    "Rename" - empty { implicit futConn =>
      for {
        conn <- futConn

        t0 <- transact(schema {
          trait Foo {
            val int = oneInt
          }
        }).map(_.last.t)

        _ <- Schema.t.a.get.map(_ ==> List(
          (t0, ":Foo/int"),
        ))

        // Rename an attribute in 5 steps:

        // 1. Call changeAttrName to change the schema definition in the database.
        t1 <- conn.changeAttrName(":Foo/int", ":Foo/int2").map(_.t)
        // 2. Update name of attribute in the data model
        //      trait Foo {
        //        val int2 = oneInt
        //      }
        // 3. Change all uses of int to int2 in your code.
        // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).

        // int has correctly been renamed to int2
        // Note that `t` refers to the transaction where the attribute entity was originally added
        _ <- Schema.t.a.get.map(_ ==> List(
          (t0, ":Foo/int2"),
        ))


        // See name changes with getHistory
        // Note that `a` refers to the current attribute name
        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
          (t0, ":Foo/int2", ":Foo/int"),
          (t1, ":Foo/int2", ":Foo/int2"),
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

        // 1. Add the attribute again to the data model.
        // 2. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 3. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).

        // For testing purpose, we transact the schema of our updated data model here
        t2 <- transact(schema {
          trait Foo {
            val int  = oneInt
            val int2 = oneInt
          }
        }).map(_.last.t)

        // Now both attributes exist
        _ <- Schema.t.a1.a.attr.get.map(_ ==> List(
          (t0, ":Foo/int2", "int2"), // originally `int`
          (t2, ":Foo/int", "int"), //   repurposed `int`
        ))

        // Check name changes with getHistory
        // Note that `a` refers to the current attribute name
        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
          (t0, ":Foo/int2", ":Foo/int"), //  int created
          (t1, ":Foo/int2", ":Foo/int2"), // int -> int2
          (t2, ":Foo/int", ":Foo/int"), //   repurposed new int
        ))
      } yield ()
    }
  }
}
