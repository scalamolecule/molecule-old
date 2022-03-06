package moleculeTests.tests.db.datomic.generic

import molecule.core.data.model._
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out13._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.exception.Model2QueryException
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.Future


object SchemaChange_Attr extends AsyncTestSuite {

  lazy val tests = Tests {

    "New" - empty { implicit futConn =>
      for {
        // Schema has no attributes yet
        _ <- Schema.t.a.valueType.cardinality.get.map(_ ==> Nil)

        // Initial data model
        r0 <- transact(schema {
          trait Foo {
            val int = oneInt
          }
        }).map(_.last)
        (t0, tx0, d0) = (r0.t, r0.tx, r0.txInstant)

        // Schema now has 1 attribute
        _ <- Schema.t.a.get.map(_ ==> List(
          (t0, ":Foo/int"),
        ))


        // Add an attribute in 3 steps:

        // 1. Add new attribute to data model.
        //    (Previously transacted schema attributes are automatically ignored by Datomic. So we can safely transact the
        //     whole schema anytime we want).
        // 2. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 3. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).

        // For testing purpose, we transact the schema of our updated data model here
        r1 <- transact(schema {
          trait Foo {
            val int = oneInt
            val str = manyString
          }
        }).map(_.last)
        (t1, tx1, d1) = (r1.t, r1.tx, r1.txInstant)

        // Schema has 2 attributes
        (attrId0, attrId1) <- Schema.t.a1.attrId.a.ns.attr.valueType.cardinality.get.map { res =>
          val (attrId0, attrId1) = (res.head._2, res(1)._2)
          res ==> List(
            (t0, attrId0, ":Foo/int", "Foo", "int", "long", "one"),
            (t1, attrId1, ":Foo/str", "Foo", "str", "string", "many"),
          )
          (attrId0, attrId1)
        }

        // Since we have only added to the schema in this case, `getHistory` will show the same as `get`
        _ <- Schema.t.a1.attrId.a.ns.attr.valueType.cardinality.getHistory.map(_ ==> List(
          (t0, attrId0, ":Foo/int", "Foo", "int", "long", "one"),
          (t1, attrId1, ":Foo/str", "Foo", "str", "string", "many"),
        ))


        // Schema history attribute expressions

        _ <- Schema.a.t.getHistory.map(_ ==> List((":Foo/int", t0), (":Foo/str", t1)))
        _ <- Schema.a.t(t0).getHistory.map(_ ==> List((":Foo/int", t0)))
        _ <- Schema.a.t(t0, t1).getHistory.map(_ ==> List((":Foo/int", t0), (":Foo/str", t1)))
        _ <- Schema.a.t(Seq(t0)).getHistory.map(_ ==> List((":Foo/int", t0)))
        _ <- Schema.a.t(Seq(t0, t1)).getHistory.map(_ ==> List((":Foo/int", t0), (":Foo/str", t1)))
        _ <- Schema.a.t.not(t0).getHistory.map(_ ==> List((":Foo/str", t1)))
        _ <- Schema.a.t.not(t0, t1).getHistory.map(_ ==> Nil)
        _ <- Schema.a.t.not(Seq(t0)).getHistory.map(_ ==> List((":Foo/str", t1)))
        _ <- Schema.a.t.not(Seq(t0, t1)).getHistory.map(_ ==> Nil)
        _ <- Schema.a.t.>(t0).getHistory.map(_ ==> List((":Foo/str", t1)))
        _ <- Schema.a.t.>=(t0).getHistory.map(_ ==> List((":Foo/int", t0), (":Foo/str", t1)))
        _ <- Schema.a.t.<(t1).getHistory.map(_ ==> List((":Foo/int", t0)))
        _ <- Schema.a.t.<=(t1).getHistory.map(_ ==> List((":Foo/int", t0), (":Foo/str", t1)))

        _ <- Schema.a.tx(tx0).getHistory.map(_ ==> List((":Foo/int", tx0)))
        _ <- Schema.a.tx(tx0, tx1).getHistory.map(_ ==> List((":Foo/int", tx0), (":Foo/str", tx1)))
        _ <- Schema.a.tx(Seq(tx0)).getHistory.map(_ ==> List((":Foo/int", tx0)))
        _ <- Schema.a.tx(Seq(tx0, tx1)).getHistory.map(_ ==> List((":Foo/int", tx0), (":Foo/str", tx1)))
        _ <- Schema.a.tx.not(tx0).getHistory.map(_ ==> List((":Foo/str", tx1)))
        _ <- Schema.a.tx.not(tx0, tx1).getHistory.map(_ ==> Nil)
        _ <- Schema.a.tx.not(Seq(tx0)).getHistory.map(_ ==> List((":Foo/str", tx1)))
        _ <- Schema.a.tx.not(Seq(tx0, tx1)).getHistory.map(_ ==> Nil)
        _ <- Schema.a.tx.>(tx0).getHistory.map(_ ==> List((":Foo/str", tx1)))
        _ <- Schema.a.tx.>=(tx0).getHistory.map(_ ==> List((":Foo/int", tx0), (":Foo/str", tx1)))
        _ <- Schema.a.tx.<(tx1).getHistory.map(_ ==> List((":Foo/int", tx0)))
        _ <- Schema.a.tx.<=(tx1).getHistory.map(_ ==> List((":Foo/int", tx0), (":Foo/str", tx1)))

        _ <- Schema.a.txInstant(d0).getHistory.map(_ ==> List((":Foo/int", d0)))
        _ <- Schema.a.txInstant(d0, d1).getHistory.map(_ ==> List((":Foo/int", d0), (":Foo/str", d1)))
        _ <- Schema.a.txInstant(Seq(d0)).getHistory.map(_ ==> List((":Foo/int", d0)))
        _ <- Schema.a.txInstant(Seq(d0, d1)).getHistory.map(_ ==> List((":Foo/int", d0), (":Foo/str", d1)))
        _ <- Schema.a.txInstant.not(d0).getHistory.map(_ ==> List((":Foo/str", d1)))
        _ <- Schema.a.txInstant.not(d0, d1).getHistory.map(_ ==> Nil)
        _ <- Schema.a.txInstant.not(Seq(d0)).getHistory.map(_ ==> List((":Foo/str", d1)))
        _ <- Schema.a.txInstant.not(Seq(d0, d1)).getHistory.map(_ ==> Nil)
        _ <- Schema.a.txInstant.>(d0).getHistory.map(_ ==> List((":Foo/str", d1)))
        _ <- Schema.a.txInstant.>=(d0).getHistory.map(_ ==> List((":Foo/int", d0), (":Foo/str", d1)))
        _ <- Schema.a.txInstant.<(d1).getHistory.map(_ ==> List((":Foo/int", d0)))
        _ <- Schema.a.txInstant.<=(d1).getHistory.map(_ ==> List((":Foo/int", d0), (":Foo/str", d1)))

        _ <- Schema.a.attrId(attrId0).getHistory.map(_ ==> List((":Foo/int", attrId0)))
        _ <- Schema.a.attrId(attrId0, attrId1).getHistory.map(_ ==> List((":Foo/int", attrId0), (":Foo/str", attrId1)))
        _ <- Schema.a.attrId(Seq(attrId0)).getHistory.map(_ ==> List((":Foo/int", attrId0)))
        _ <- Schema.a.attrId(Seq(attrId0, attrId1)).getHistory.map(_ ==> List((":Foo/int", attrId0), (":Foo/str", attrId1)))
        _ <- Schema.a.attrId.not(attrId0).getHistory.map(_ ==> List((":Foo/str", attrId1)))
        _ <- Schema.a.attrId.not(attrId0, attrId1).getHistory.map(_ ==> Nil)
        _ <- Schema.a.attrId.not(Seq(attrId0)).getHistory.map(_ ==> List((":Foo/str", attrId1)))
        _ <- Schema.a.attrId.not(Seq(attrId0, attrId1)).getHistory.map(_ ==> Nil)
        _ <- Schema.a.attrId.>(attrId0).getHistory.map(_ ==> List((":Foo/str", attrId1)))
        _ <- Schema.a.attrId.>=(attrId0).getHistory.map(_ ==> List((":Foo/int", attrId0), (":Foo/str", attrId1)))
        _ <- Schema.a.attrId.<(attrId1).getHistory.map(_ ==> List((":Foo/int", attrId0)))
        _ <- Schema.a.attrId.<=(attrId1).getHistory.map(_ ==> List((":Foo/int", attrId0), (":Foo/str", attrId1)))

        _ <- Schema.a.attr("int").getHistory.map(_ ==> List((":Foo/int", "int")))
        _ <- Schema.a.attr("int", "str").getHistory.map(_ ==> List((":Foo/int", "int"), (":Foo/str", "str")))
        _ <- Schema.a.attr(Seq("int")).getHistory.map(_ ==> List((":Foo/int", "int")))
        _ <- Schema.a.attr(Seq("int", "str")).getHistory.map(_ ==> List((":Foo/int", "int"), (":Foo/str", "str")))
        _ <- Schema.a.attr.not("int").getHistory.map(_ ==> List((":Foo/str", "str")))
        _ <- Schema.a.attr.not("int", "str").getHistory.map(_ ==> Nil)
        _ <- Schema.a.attr.not(Seq("int")).getHistory.map(_ ==> List((":Foo/str", "str")))
        _ <- Schema.a.attr.not(Seq("int", "str")).getHistory.map(_ ==> Nil)

        // Schema history attributes with text values are not allowed to use range expressions
        _ <- Schema.a.attr.>("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `attr`: Gt("int")"""
          }

        _ <- Schema.a.attr.>=("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `attr`: Ge("int")"""
          }

        _ <- Schema.a.attr.<("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `attr`: Lt("int")"""
          }

        _ <- Schema.a.attr.<=("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `attr`: Le("int")"""
          }

        _ <- Schema.attr.a(":Foo/int").getHistory.map(_ ==> List(("int", ":Foo/int")))
        _ <- Schema.attr.a(":Foo/int", ":Foo/str").getHistory.map(_ ==> List(("int", ":Foo/int"), ("str", ":Foo/str")))
        _ <- Schema.attr.a(Seq(":Foo/int")).getHistory.map(_ ==> List(("int", ":Foo/int")))
        _ <- Schema.attr.a(Seq(":Foo/int", ":Foo/str")).getHistory.map(_ ==> List(("int", ":Foo/int"), ("str", ":Foo/str")))
        _ <- Schema.attr.a.not(":Foo/int").getHistory.map(_ ==> List(("str", ":Foo/str")))
        _ <- Schema.attr.a.not(":Foo/int", ":Foo/str").getHistory.map(_ ==> Nil)
        _ <- Schema.attr.a.not(Seq(":Foo/int")).getHistory.map(_ ==> List(("str", ":Foo/str")))
        _ <- Schema.attr.a.not(Seq(":Foo/int", ":Foo/str")).getHistory.map(_ ==> Nil)

        // Schema history attributes with text values are not allowed to use range expressions
        _ <- Schema.attr.a.>("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `a`: Gt("int")"""
          }

        _ <- Schema.attr.a.>=("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `a`: Ge("int")"""
          }

        _ <- Schema.attr.a.<("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `a`: Lt("int")"""
          }

        _ <- Schema.attr.a.<=("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `a`: Le("int")"""
          }

        // Since all attribute definitions have a t/tx/txInstant/attrId/attr value,
        // it doesn't make much sense to ask for the tacit presence (but we can)
        _ <- Schema.a.t_.getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.tx_.getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.txInstant_.getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.attrId_.getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.attr_.getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.attr.a_.getHistory.map(_ ==> List("int", "str"))

        // Tacit Schema attributes on the other hand are valuable for filtering
        // where we don't need to return the value
        _ <- Schema.a.t_(t0).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.t_(t0, t1).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.t_(Seq(t0)).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.t_(Seq(t0, t1)).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.t_.not(t0).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.t_.not(t0, t1).getHistory.map(_ ==> Nil)
        _ <- Schema.a.t_.not(Seq(t0)).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.t_.not(Seq(t0, t1)).getHistory.map(_ ==> Nil)
        _ <- Schema.a.t_.>(t0).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.t_.>=(t0).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.t_.<(t1).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.t_.<=(t1).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))

        _ <- Schema.a.tx_(tx0).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.tx_(tx0, tx1).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.tx_(Seq(tx0)).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.tx_(Seq(tx0, tx1)).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.tx_.not(tx0).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.tx_.not(tx0, tx1).getHistory.map(_ ==> Nil)
        _ <- Schema.a.tx_.not(Seq(tx0)).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.tx_.not(Seq(tx0, tx1)).getHistory.map(_ ==> Nil)
        _ <- Schema.a.tx_.>(tx0).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.tx_.>=(tx0).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.tx_.<(tx1).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.tx_.<=(tx1).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))

        _ <- Schema.a.txInstant_(d0).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.txInstant_(d0, d1).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.txInstant_(Seq(d0)).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.txInstant_(Seq(d0, d1)).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.txInstant_.not(d0).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.txInstant_.not(d0, d1).getHistory.map(_ ==> Nil)
        _ <- Schema.a.txInstant_.not(Seq(d0)).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.txInstant_.not(Seq(d0, d1)).getHistory.map(_ ==> Nil)
        _ <- Schema.a.txInstant_.>(d0).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.txInstant_.>=(d0).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.txInstant_.<(d1).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.txInstant_.<=(d1).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))

        _ <- Schema.a.attrId_(attrId0).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.attrId_(attrId0, attrId1).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.attrId_(Seq(attrId0)).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.attrId_(Seq(attrId0, attrId1)).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.attrId_.not(attrId0).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.attrId_.not(attrId0, attrId1).getHistory.map(_ ==> Nil)
        _ <- Schema.a.attrId_.not(Seq(attrId0)).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.attrId_.not(Seq(attrId0, attrId1)).getHistory.map(_ ==> Nil)
        _ <- Schema.a.attrId_.>(attrId0).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.attrId_.>=(attrId0).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.attrId_.<(attrId1).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.attrId_.<=(attrId1).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))

        _ <- Schema.a.attr_("int").getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.attr_("int", "str").getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.attr_(Seq("int")).getHistory.map(_ ==> List(":Foo/int"))
        _ <- Schema.a.attr_(Seq("int", "str")).getHistory.map(_ ==> List(":Foo/int", ":Foo/str"))
        _ <- Schema.a.attr_.not("int").getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.attr_.not("int", "str").getHistory.map(_ ==> Nil)
        _ <- Schema.a.attr_.not(Seq("int")).getHistory.map(_ ==> List(":Foo/str"))
        _ <- Schema.a.attr_.not(Seq("int", "str")).getHistory.map(_ ==> Nil)

        // Schema history attributes with text values are not allowed to use range expressions
        _ <- Schema.a.attr_.>("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `attr_`: Gt("int")"""
          }

        _ <- Schema.a.attr_.>=("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `attr_`: Ge("int")"""
          }

        _ <- Schema.a.attr_.<("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `attr_`: Lt("int")"""
          }

        _ <- Schema.a.attr_.<=("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `attr_`: Le("int")"""
          }

        _ <- Schema.attr.a_(":Foo/int").getHistory.map(_ ==> List("int"))
        _ <- Schema.attr.a_(":Foo/int", ":Foo/str").getHistory.map(_ ==> List("int", "str"))
        _ <- Schema.attr.a_(Seq(":Foo/int")).getHistory.map(_ ==> List("int"))
        _ <- Schema.attr.a_(Seq(":Foo/int", ":Foo/str")).getHistory.map(_ ==> List("int", "str"))
        _ <- Schema.attr.a_.not(":Foo/int").getHistory.map(_ ==> List("str"))
        _ <- Schema.attr.a_.not(":Foo/int", ":Foo/str").getHistory.map(_ ==> Nil)
        _ <- Schema.attr.a_.not(Seq(":Foo/int")).getHistory.map(_ ==> List("str"))
        _ <- Schema.attr.a_.not(Seq(":Foo/int", ":Foo/str")).getHistory.map(_ ==> Nil)

        // Schema history attributes with text values are not allowed to use range expressions
        _ <- Schema.attr.a_.>("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `a_`: Gt("int")"""
          }

        _ <- Schema.attr.a_.>=("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `a_`: Ge("int")"""
          }

        _ <- Schema.attr.a_.<("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `a_`: Lt("int")"""
          }

        _ <- Schema.attr.a_.<=("int").getHistory
          .map(_ ==> "Unexpected success")
          .recover { case Model2QueryException(msg) =>
            msg ==> """Unsupported expression for basic schema history attribute `a_`: Le("int")"""
          }
      } yield ()
    }


    "Rename" - empty { implicit futConn =>
      for {
        conn <- futConn

        // Initial data model
        t0 <- transact(schema {
          trait Foo {
            val int = oneInt
          }
        }).map(_.last.t)

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


    "Rename, checking data" - empty { implicit futConn =>
      for {
        conn <- futConn

        // Initial data model
        _ <- transact(schema {
          trait Foo {
            val int = oneInt
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
            val int  = oneInt
            val int2 = oneInt
          }
        })

        // `int` is now its own attribute and no longer points to the same attribute as `int2`
        _ <- conn.query("[:find ?b :where [?a :Foo/int ?b]]").map(_ ==> Nil)
        _ <- conn.query("[:find ?b :where [?a :Foo/int2 ?b]]").map(_ ==> List(List(1)))

      } yield ()
    }


    "Move attribute to other ns" - empty { implicit futConn =>
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

        // Move/Rename an attribute in 5 steps:

        // 1. Call changeAttrName to change the schema definition in the database
        t1 <- conn.changeAttrName(":Foo/int", ":Bar/int").map(_.t)
        // 2. Update name of attribute in the data model
        //      trait Foo {
        //        val str = oneString
        //      }
        //
        //      trait Bar {
        //        val int  = oneInt
        //        val long = oneLong
        //      }
        // 3. Change all uses of int to int2 in your code.
        // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).

        // int has correctly been moved to the Bar namespace
        _ <- Schema.a.get.map(_ ==> List(
          ":Foo/str",
          ":Bar/int",
          ":Bar/long",
        ))

        // See name changes with getHistory
        // Note that `a` refers to the current attribute name
        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
          (t0, ":Bar/int", ":Foo/int"),
          (t0, ":Foo/str", ":Foo/str"),
          (t0, ":Bar/long", ":Bar/long"),
          (t1, ":Bar/int", ":Bar/int"),
        ))
      } yield ()
    }


    "Retire" - empty { implicit futConn =>
      // Attributes can be "retired" which means that they are marked with a `-` prefix to exclude them from
      // current Schema query results.
      for {
        conn <- futConn

        // Initial data model
        t0 <- transact(schema {
          trait Foo {
            val int = oneInt
            val str = oneString
          }
        }).map(_.last.t)

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
        t4 <- conn.retireAttr(":Foo/int").map(_.t)
        // 2. Remove attribute definition in the data model.
        // 3. Remove all uses of `int` in your code.
        // 4. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 5. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).

        // For testing purpose, we transact the schema of our new data model here
        _ <- transact(schema {
          trait Foo {
            val str = oneString
          }
        })

        // int attribute is no longer available (current view filters out retired attributes)
        _ <- Schema.t.a.get.map(_ ==> List(
          (t0, ":Foo/str"),
        ))

        // Retired attributes are simply marked with a `-` prefix to exclude them from current Schema queries.
        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
          (t0, ":-Foo/int", ":Foo/int"), // :Foo/int created (`a` shows current retired attribute ident `:-Foo/int`)
          (t0, ":Foo/str", ":Foo/str"),
          (t4, ":-Foo/int", ":-Foo/int"), // :Foo/int retired
        ))


        // Un-retire attribute in 5 steps:

        // 1. Call changeAttrName to remove prefix used to mark it as retired
        t6 <- conn.changeAttrName(":-Foo/int", ":Foo/int").map(_.t)
        // 2. Add `int` definition again to the data model.
        // 3. Run `sbt compile -Dmolecule=true` to re-generate boilerplate code.
        // 4. For a live database, call `Datomic_Peer.transactSchema(YourSchema, protocol, yourDbName)`
        //    to transact the updated schema (depending on which system you use).

        // For testing purpose, we transact the schema of our new data model here
        _ <- transact(schema {
          trait Foo {
            val int = oneInt
            val str = oneString
          }
        })

        _ <- Schema.t.a.get.map(_ ==> List(
          (t0, ":Foo/int"),
          (t0, ":Foo/str"),
        ))

        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
          (t0, ":Foo/int", ":Foo/int"),
          (t0, ":Foo/str", ":Foo/str"),
          (t4, ":Foo/int", ":-Foo/int"), // :Foo/int retired
          (t6, ":Foo/int", ":Foo/int"), //  :Foo/int un-retired
        ))

        // When was :Foo/int retired? At t4
        _ <- Schema.t.a.ident(":-Foo/int").getHistory.map(_ ==> List(
          (t4, ":Foo/int", ":-Foo/int"),
        ))

        // Has :Foo/int been re-instated (asserted multiple times)? Yes
        _ <- Schema.t.a.ident(":Foo/int").getHistory.map(_ ==> List(
          (t0, ":Foo/int", ":Foo/int"),
          (t6, ":Foo/int", ":Foo/int"),
        ))

        // We could also simply have updated our data model as above without first calling
        // changeAttrName. The only difference would then be that a new attribute named `:Foo/int`
        // would be created and the old retired `:-Foo/int` would remain. This wouldn't affect our
        // code since the retired attribute wouldn't be available in our code anyway.
      } yield ()
    }
  }
}