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
object SchemaChange_Options extends AsyncTestSuite {

  lazy val tests = Tests {

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