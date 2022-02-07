package moleculeTests

import clojure.lang.Keyword
import datomic.{Database, Peer, Util}
import molecule.core.data.model._
import molecule.core.dsl.attributes.Attr
import molecule.core.exceptions.MoleculeException
import molecule.core.generic.Schema.Schema_a
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.core.util.{Helpers, JavaConversions}
import molecule.datomic.api
import molecule.datomic.api.in1_out15._
import molecule.datomic.base.api.Datom
import molecule.datomic.base.facade.TxReport
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.dataModels.core.base.dataModel.CoreTestDataModel.Ns
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}

object AdhocJvm extends AsyncTestSuite with Helpers with JavaConversions {

  var last = 0L


  // Differing counts and ids for different systems
  val List(attrCount, a1, a2, a3, card1count, card2count) = (system, protocol) match {
    case (SystemPeer, "free")  => List(69, 97, 99, 100, 30, 39)
    case (SystemPeer, _)       => List(69, 106, 108, 109, 30, 39)
    case (SystemDevLocal, _)   => List(69, 107, 109, 110, 30, 39)
    case (SystemPeerServer, _) => List(71, 104, 106, 107, 31, 40)
  }


  lazy val tests = Tests {

    //    "adhocJvm" - core { implicit futConn =>
    "adhocJvm" - empty { implicit futConn =>
      for {
        conn <- futConn


        _ <- transact(schema {
          trait Foo {
            val int = oneInt.noHistory.uniqueIdentity.doc("hej")
            //            val str = oneString
          }
        })

        //        _ <- Schema.a.ident.get.map(_ ==> List(
        //          (":Foo/int", ":Foo/int"),
        //        ))

        _ <- conn.changeAttrName(":Foo/intX", ":Foo/int2")
          .map(_ ==> "Unexpected succes")
          .recover { case MoleculeException(msg, _) =>
            msg ==> "Couldn't find current attribute ident `:Foo/intX` in the database. " +
              s"Please check the supplied current ident in order to change the name."
          }

        _ <- conn.changeAttrName(":Foo/int", ":Foo/int2")
        //        _ <- Schema.a.ident.get.map(_ ==> List(
        //          (":Foo/int2", ":Foo/int2"),
        //        ))


        //        _ <- transact(schema {
        //          trait Foo {
        //            val str = manyString
        //            val long    = oneLong
        //            val double  = oneDouble
        //            val bool    = oneBoolean
        //            val date    = oneDate
        //            val uuid    = oneUUID
        //            val uri     = oneURI
        //            val bigInt  = oneBigInt
        //            val bigDec  = oneBigDecimal
        //            val parent  = one[Baz]
        //          }
        //          trait Baz {
        //            val xx = oneInt
        //          }
        //        })

        _ <- conn.changeAttrName(":Foo/int2", ":Bar/hello")
        //        _ <- Schema.a.ident.get.map(_ ==> List(
        //          (":Bar/hello", ":Bar/hello"),
        //        ))

        // ok that we can work on old ident?
        _ <- conn.transact("""[[:db/add :Foo/int :db/noHistory false]]""".stripMargin)
        //        (1003,72,:Foo/int2,45,:db/noHistory,false,true)
        //        (1003,72,:Foo/int2,45,:db/noHistory,true,false)

        // or retracting (but see no reason for doing that
        //        _ <- conn.transact(
        //          """[[:db/retract :Foo/int2 :db/noHistory true]
        //            | [:db/add :db.part/db :db.alter/attribute :Foo/int2]]""".stripMargin
        //        )
        //        (1003,72,:Foo/int2,45,:db/noHistory,true,false)


        db <- conn.db

//        _ = {
//          val res  = Peer.q(
//            """[:find  ?t ?a
//              |        (pull ?valueType_pull [{:db/valueType [:db/ident]}])
//              | :where [_ :db.install/attribute ?attrId ?tx]
//              |        [?attrId :db/ident ?idIdent]
//              |        [(namespace ?idIdent) ?nsFull]
//              |        [(.matches ^String ?nsFull "(db|db.alter|db.excise|db.install|db.part|db.sys|fressian|db.entity|db.attr|-.*)") ?sys]
//              |        [(= ?sys false)]
//              |        [(- ?tx 13194139533312) ?t]
//              |        [(str ?idIdent) ?a]
//              |        [(identity ?attrId) ?valueType_pull]]""".stripMargin,
//            db.getDatomicDb
//          )
//          val res3 = res.iterator().next().get(2)
//          println("1  " + res3)
//          println("1  " + Util.map(
//            Keyword.intern("db", "valueType"),
//            Util.map(
//              Keyword.intern("db", "ident"),
//              Keyword.intern("db.type", "long")
//            )
//          ))
////          println("2  " + res3.getClass)
////          println("3  " + res3.asInstanceOf[java.util.Map[_,_]])
////          println("4  " + res3.asInstanceOf[java.util.Map[_,_]].size())
////          println("5  " + res3.asInstanceOf[java.util.Map[_,_]].values().iterator().next())
////          println("6  " + res3.asInstanceOf[java.util.Map[_,_]].values().iterator().next().getClass)
////          println("5  " + res3.asInstanceOf[java.util.Map[_,_]].values().iterator().next().asInstanceOf[])
//        }
//        _ = println("-----------------------")


        _ = Peer.q(
          """[:find  ?attrId ?attrIdent
            |        ?schemaId ?schemaIdent ?schemaValue ?tx ?t ?txInst ?op
            | :where [:db.part/db :db.install/attribute ?attrId]
            |        [(datomic.api/ident $ ?attrId) ?attrIdent]
            |        [?attrId ?schemaId ?schemaValue ?tx ?op]
            |        [(datomic.api/ident $ ?schemaId) ?schemaIdent]
            |        [(datomic.api/tx->t ?tx) ?t]
            |        [(>= ?t 1000)]
            |        [?tx :db/txInstant ?txInst]
            |]""".stripMargin,
          //                      |        [?attrId :db/ident ?attrIdent]
          //                      |        [?schemaId :db/ident ?schemaIdent]
          db.getDatomicDb.asInstanceOf[Database].history()
          //          """[:find  ?tx ?t ?txInst ?op
          //            |        ?attrId ?attrIdent
          //            |        ?schemaId ?schemaIdent ?schemaValue
          //            | :where [:db.part/db :db.install/attribute ?attrId]
          //            |        [?attrId :db/ident ?attrIdent]
          //            |        [?attrId ?schemaId ?schemaValue ?tx ?op]
          //            |        [?schemaId :db/ident ?schemaIdent]
          //            |        [(>= ?tx 13194139534312)]
          //            |        [(datomic.api/tx->t ?tx) ?t]
          //            |        [?tx :db/txInstant ?txInst]
          //            |]""".stripMargin,
          //                    """[:find  ?tx ?t ?op ?a ?ident ?action ?v
          //                      | :in    $ %
          //                      | :where [:db.part/db :db.install/attribute ?a]
          //                      |        [(datomic.api/ident $ ?a) ?ident]
          //                      |        (entity-at ?a ?tx ?t ?inst ?op ?action ?v)
          //                      |        [(>= ?tx 13194139534312)]
          //                      |]""".stripMargin,
          //          db.getDatomicDb.asInstanceOf[Database].history(),
          //                    """[
          //                      |  [
          //                      |    (entity-at [?e] ?tx ?t ?inst ?op ?action ?v)
          //                      |    [?e ?actionAttr ?v ?tx ?op]
          //                      |    [?actionAttr :db/ident ?action]
          //                      |    [(datomic.api/tx->t ?tx) ?t]
          //                      |    [?tx :db/txInstant ?inst]
          //                      |  ]
          //                      |]""".stripMargin

          //          """[:find ?tx ?t ?op ?a ?ident ?action ?v
          //            | :in $ %
          //            | :where
          //            |   [:db.part/db :db.install/attribute ?a]
          //            |   [(datomic.api/ident $ ?a) ?ident]
          //            |   (or
          //            |       (tpe-at ?a ?tx ?t ?inst ?op ?action ?v)
          //            |       (card-at ?a ?tx ?t ?inst ?op ?action ?v)
          //            |       (entity-at ?a ?tx ?t ?inst ?op ?action ?v)
          //            |       )
          //            |   [(> ?tx 13194139534312)]
          //            |]""".stripMargin,
          //          db.getDatomicDb.asInstanceOf[Database].history(),
          //          """[
          //            |  [
          //            |    (tpe-at [?e] ?tx ?t ?inst ?op ?actionIdent ?v)
          //            |    [?e :db/valueType ?tpeId ?tx ?op]
          //            |    [:db/valueType :db/ident ?actionIdent]
          //            |    [?tpeId :db/ident ?tpeKw]
          //            |    [(name ?tpeKw) ?v]
          //            |    [(datomic.api/tx->t ?tx) ?t]
          //            |    [?tx :db/txInstant ?inst]
          //            |  ]
          //            |  [
          //            |    (card-at [?e] ?tx ?t ?inst ?op ?actionIdent ?v)
          //            |    [?e :db/cardinality ?cardId ?tx ?op]
          //            |    [:db/cardinality :db/ident ?actionIdent]
          //            |    [?cardId :db/ident ?cardKw]
          //            |    [(name ?cardKw) ?v]
          //            |    [(datomic.api/tx->t ?tx) ?t]
          //            |    [?tx :db/txInstant ?inst]
          //            |  ]
          //            |  [
          //            |    (entity-at [?e] ?tx ?t ?inst ?op ?actionIdent ?v)
          //            |    [?e ?actionAttr ?v ?tx ?op]
          //            |    [?actionAttr :db/ident ?actionIdent]
          //            |    [(datomic.api/tx->t ?tx) ?t]
          //            |    [?tx :db/txInstant ?inst]
          //            |  ]
          //            |]""".stripMargin
        ).asScala.toSeq.map { row0 =>
          //                    println(row0)
          val l = row0.asScala
          (
            l(6).toString.toLong, // t
            l(0).toString.toLong, // attrId
            l(1).toString, // attrIdent
            l(2).toString.toLong, // schemaId
            l(3).toString, // schemaIdent
            l(4).toString, // schemaValue
            l(8).toString, // op

            //            l(5).toString.toLong, // tx
            //            l(7).toString,        // txInstant

            //            l(0).toString.toLong, // attrId
            //            l(1).toString,        // attrIdent
            //            l(2).toString.toLong, // schemaId
            //            l(3).toString,        // schemaIdent
            //            l(4).toString,        // schemaValue
            //            l(5).toString.toLong, // tx
            //            l(6).toString.toLong, // t
            //            l(7).toString,        // txInstant
            //            l(8).toString,        // op
          )
        }
          .sortBy(r => (r._1, r._2, r._4))
          .foreach { r =>
            if (r._1 != last) {
              println("")
            }
            last = r._1
            println(r)
          }


        //        _ = println("-----------------------")
        //        _ <- Schema.t.part.nsFull.ns.attr.a.inspectGet

        //        _ <- Schema.t.a.ident.valueType.cardinality.noHistory$.get.map(_ ==> List(
        //          (1001, ":Bar/hello", ":Bar/hello", "long", "one", Some(false)),
        //          (1003, ":Foo/str", ":Foo/str", "string", "many", None)
        //        ))
        //        _ <- Schema.t.a.ident.valueType.cardinality.noHistory.get.map(_ ==> List(
        //          (1001, ":Bar/hello", ":Bar/hello", "long", "one", false)
        //        ))

        //                _ <- Schema
        //                  .tx.t.txInstant
        //                  .attrId.a
        //                  .ident$.valueType$.cardinality$
        //                  .doc$.unique$.isComponent$.noHistory$.index$.fulltext$.getHistory.map(_ ==> List(
        //                  (1001, ":Bar/hello", Some(":Foo/int"), Some("long"), Some("one"), Some(true)),
        //                  (1002, ":Bar/hello", Some(":Foo/int2"), None, None, None),
        //                  (1003, ":Bar/hello", Some(":Bar/hello"), None, None, None),
        //                  (1004, ":Bar/hello", None, None, None, Some(false)),
        //                ))

//        _ <- Schema.t.a.valueType.inspectGet
//        _ <- Schema.t.a.valueType$.inspectGet
        //        _ <- Schema.t.a.valueType$.get.map(_ ==> List(
        //          (1001, ":Bar/hello", Some("long")),
        ////          (1003, ":Bar/hello", Some(":Bar/hello")),
        //        ))


        _ <- Schema.t.a.valueType$.getHistory.map(_ ==> List(
          (1001, ":Bar/hello", Some("long")),
          (1002, ":Bar/hello", None),
          (1003, ":Bar/hello", None),
          (1004, ":Bar/hello", None),
        ))
        //        _ <- Schema.t.a.ident$.getHistory.map(_ ==> List(
        //          (1001, ":Bar/hello", Some(":Foo/int")),
        //          (1002, ":Bar/hello", Some(":Foo/int2")),
        //          (1003, ":Bar/hello", Some(":Bar/hello")),
        //        ))

        //        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
        //          (1001, ":Bar/hello", ":Foo/int"),
        //          (1002, ":Bar/hello", ":Foo/int2"),
        //          (1003, ":Bar/hello", ":Bar/hello"),
        //        ))
        //
        //        _ <- Schema.t.a.attr.ident.getHistory.map(_ ==> List(
        //          (1001, ":Bar/hello", "hello", ":Foo/int"),
        //          (1002, ":Bar/hello", "hello", ":Foo/int2"),
        //          (1003, ":Bar/hello", "hello", ":Bar/hello"),
        //        ))
        //        _ <- Schema.t.a.ident$.valueType$.cardinality$.noHistory$.getHistory.map(_ ==> List(
        //          (1001, ":Bar/hello", Some(":Foo/int"), Some("long"), Some("one"), Some(true)),
        //          (1002, ":Bar/hello", Some(":Foo/int2"), None, None, None),
        //          (1003, ":Bar/hello", Some(":Bar/hello"), None, None, None),
        //          (1004, ":Bar/hello", None, None, None, Some(false)),
        //        ))


        /*
0   attrId
1   a
2   part
3   nsFull
4   ns
5   attr
6   enumm
7   ident
8   valueType
9   cardinality
10  doc
11  unique
12  isComponent
13  noHistory
14  index
15  fulltext
16  t
17  tx
18  txInstant






(13194139534313,1001,true,72,:Bar/hello,:db/index,true)
(13194139534313,1001,true,72,:Bar/hello,:db/cardinality,35)
(13194139534313,1001,true,72,:Bar/hello,:db/noHistory,true)
(13194139534313,1001,true,72,:Bar/hello,:db/ident,:Foo/int)
(13194139534313,1001,true,72,:Bar/hello,:db/valueType,22)

(13194139534314,1002,false,72,:Bar/hello,:db/ident,:Foo/int)
(13194139534314,1002,true,72,:Bar/hello,:db/ident,:Foo/int2)

(13194139534315,1003,false,72,:Bar/hello,:db/ident,:Foo/int2)
(13194139534315,1003,true,72,:Bar/hello,:db/ident,:Bar/hello)

(13194139534316,1004,false,72,:Bar/hello,:db/noHistory,true)
         */

        //                _ = println("-----------------------")
        //        _ = Peer.q(
        //          """[:find ?tx ?t ?op ?a ?ident ?action ?v
        //            | :in $ %
        //            | :where
        //            |   (or [:db.part/db :db.install/attribute ?a]
        //            |       [:db.part/db :db.alter/attribute ?a])
        //            |   [(datomic.api/ident $ ?a) ?ident]
        //            |   (or (entity-at ?a ?tx ?t ?inst ?op ?action ?v)
        //            |       (value-at ?a ?tx ?t ?inst ?op ?action ?v))
        //            |   [(> ?tx 13194139534312)]
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
        //            |  [
        //            |    (value-at [?e] ?tx ?t ?inst ?op ?action ?v)
        //            |    [?v ?actionAttr ?e ?tx ?op]
        //            |    [?actionAttr :db/ident ?action]
        //            |    [(datomic.api/tx->t ?tx) ?t]
        //            |    [?tx :db/txInstant ?inst]
        //            |  ]
        //            |]""".stripMargin
        //        ).asScala.toSeq.map { row0 =>
        //          val l = row0.asScala
        //          (l.head.toString.toLong, l(1).toString.toLong, l(2).toString, l(3).toString, l(4).toString, l(5).toString, l(6).toString)
        //        }
        //          .sortBy(r => (r._1, r._5, r._3))
        //          .foreach { r =>
        //            if (r._1 != last) {
        //              println("")
        //            }
        //            last = r._1
        //            println(r)
        //          }


        //        _ = println("-----------------------")
        //        _ <- SchemaLog.t.attrId.schemaAttr.schemaValue.op.inspectGet

        //        _ = println("-----------------------")
        //        _ <- Log(None, None).t.e.a.v.op.inspectGet

        //        _ <- Schema.t.a.ident.inspectGetHistory
        //        _ = println("-----------------------")
        //
        //        _ <- Schema.t.a.ident.getHistory.map(_ ==> List(
        //          (1001, ":Bar/hello", ":Foo/int"),
        //          (1002, ":Bar/hello", ":Foo/int2"),
        //          (1003, ":Bar/hello", ":Bar/hello"),
        //        ))
        //
        //        _ <- Schema.t.a.ident$.getHistory.map(_ ==> List(
        //          (1000, ":Bar/hello", ":Foo/int"),
        //          (1002, ":Bar/hello", ":Foo/int2"),
        //          (1003, ":Bar/hello", ":Bar/hello"),
        //        ))
        //
        //        _ <- Schema.t.a.ident$.getHistory.map(_ ==> List(
        //          (1000, ":Bar/hello", ":Foo/int"),
        //          (1002, ":Bar/hello", ":Foo/int2"),
        //          (1003, ":Bar/hello", ":Bar/hello"),
        //        ))


        //        txr <- conn.transact(
        //          """[
        //            |  {:db/ident         :Ns/aaaaaaaaaaaaaaaa
        //            |   :db/valueType     :db.type/long
        //            |   :db/cardinality   :db.cardinality/one
        //            |   :db/index         true
        //            |  }
        //            |]""".stripMargin
        //        )
        //        t1 = txr.t
        //
        //        txr2 <- conn.transact(
        //          """[
        //            |  {:db/id     :Ns/str
        //            |   :db/doc    "text 2"
        //            |   :db/unique :db.unique/value
        //            | }
        //            |]""".stripMargin
        //        )
        //        t2 = txr2.t
        //
        //
        //        _ <- Ns.int(1).str("a").save
        //
        //
        //        _ <- conn.transact(
        //          """[
        //            |  {
        //            |    :db/id        :Ns/int
        //            |    :db/ident     :Ns/int2
        //            |    :db/noHistory true
        //            |  }
        //            |]""".stripMargin
        //        )
        //        _ <- conn.transact(
        //          """[[:db/retract :Ref2/ints2 :db/noHistory true]
        //            | [:db/add :db.part/db :db.alter/attribute :Ref2/ints2]]""".stripMargin
        //        )
        //
        //        db <- conn.db
        //
        //
        //

        //        _ = println("-----------------------")
        //        _ <- conn.query(
        //          """[:find  ?a
        //            |        (pull ?uniqueIdentity_pull [{:db/unique [:db/ident]}])
        //            | :in    $ [?attr ...]
        //            | :where [_ :db.install/attribute ?id ?tx]
        //            |        [(>= ?tx 13194139534312)]
        //            |        [?id :db/ident ?idIdent]
        //            |        [(name ?idIdent) ?attr]
        //            |        [(str ?idIdent) ?a]
        //            |        [(identity ?id) ?uniqueIdentity_pull]]""".stripMargin, Seq("str", "str2")
        //        ).map { r =>
        //          r.foreach(println)
        //          r
        //        }
        //        _ = println("-----------------------")
        //                _ <- conn.query(
        //                  """[:find  ?a ?attr
        //                    | :where [_ :db.install/attribute ?id ?tx]
        //                    |        [(>= ?tx 13194139534312)]
        //                    |        [?id :db/ident ?idIdent]
        //                    |        [(namespace ?idIdent) ?nsFull]
        //                    |        [(name ?idIdent) ?attr]
        //                    |        [(.contains ^String ?nsFull "_") ?isPart]
        //                    |        [(.split ^String ?nsFull "_") ?nsParts]
        //                    |        [(first ?nsParts) ?part0]
        //                    |        [(if ?isPart ?part0 "db.part/user") ?part]
        //                    |        [(last ?nsParts) ?ns]
        //                    |        [(str ?idIdent) ?a]
        //                    |        ]""".stripMargin
        //                ).map { r =>
        //                  r.foreach(println)
        //                  r
        //                }

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


        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //        _ <- Schema.a.uniqueIdentity$.inspectGet
        //        _ <- Schema.tx.doc.inspectGetHistory
        //        _ <- Schema.tx.a.inspectGetHistory
        //        _ <- Schema.doc.op.inspectGetHistory

        //        _ <- Schema.tx.doc.op.inspectGet
        //        _ <- Schema.tx.doc.op.inspectGetHistory

        //        _ <- Log(Some(t1)).e.a.v.tx.op.inspectGet
        //
        //        _ <- Ns.tx.op.e.a(":db/ident", ":db/doc", ":db/noHistory", ":Ns/int", ":Ns/int2").v.inspectGetHistory
        //        //        _ <- Ns.tx.op.e.a.v.inspectGetHistory
        //
        //        _ <- Ns.int.get.map(_ ==> List(1))

        //        tx = r.tx
        //        e = r.eid
        //        r2 <- Ns(e).int(2).update
        //        tx2 = r2.tx
        //        //        _ <- Ns.int.t.op.str.t.inspectGetHistory
        //        _ <- Ns.int.tx.a1.op.a2.inspectGetHistory
        //
        //        // todo: sort history/time views too when inspecting...
        //        _ <- Ns(e).a.v.tx.op.inspectGetHistory
        //        _ <- Ns(e).a.a2.v.tx.a1.op.a3.getHistory.map(_ ==> List(
        //          (":Ns/int", 1, tx, true),
        //          (":Ns/str", "a", tx, true),
        //          (":Ns/int", 1, tx2, false),
        //          (":Ns/int", 2, tx2, true),
        //        ))
        //        _ <- Ns.int.tx.op.getHistory.map(_ ==> 7)

        //        _ <- conn.query(
        //          """[:find  ?a ?uniqueValue
        //            | :where [_ :db.install/attribute ?id ?tx]
        //            |        [?id :db/ident ?idIdent]
        //            |        [(namespace ?idIdent) ?nsFull]
        //            |        [(.matches ^String ?nsFull "^(db|db.alter|db.excise|db.install|db.part|db.sys|fressian|db.entity|db.attr|:?-.*)") ?sys]
        //            |        [(= ?sys false)]
        //            |        [(.contains ^String ?nsFull "_") ?isPart]
        //            |        [(.split ^String ?nsFull "_") ?nsParts]
        //            |        [(first ?nsParts) ?part0]
        //            |        [(if ?isPart ?part0 "db.part/user") ?part]
        //            |        [(last ?nsParts) ?ns]
        //            |        [(str ?idIdent) ?a]
        //            |        [?id :db/unique :db.unique/value]
        //            |        [(ground true) ?uniqueValue]
        //            |        ]""".stripMargin
        //        ).map { r =>
        //          r.foreach(println)
        //          r
        //        }
        //
        //        _ = println("-----------------------")
        //        _ <- conn.query(
        //          """[:find  ?a ?uniqueIdentity
        //            | :where [_ :db.install/attribute ?id ?tx]
        //            |        [?id :db/ident ?idIdent]
        //            |        [(namespace ?idIdent) ?nsFull]
        //            |        [(.matches ^String ?nsFull "^(db|db.alter|db.excise|db.install|db.part|db.sys|fressian|db.entity|db.attr|:?-.*)") ?sys]
        //            |        [(= ?sys false)]
        //            |        [(.contains ^String ?nsFull "_") ?isPart]
        //            |        [(.split ^String ?nsFull "_") ?nsParts]
        //            |        [(first ?nsParts) ?part0]
        //            |        [(if ?isPart ?part0 "db.part/user") ?part]
        //            |        [(last ?nsParts) ?ns]
        //            |        [(str ?idIdent) ?a]
        //            |        [?id :db/unique :db.unique/identity]
        //            |        [(ground true) ?uniqueIdentity]
        //            |        ]""".stripMargin
        //        ).map { r =>
        //          r.foreach(println)
        //          r
        //        }


        //        _ = println("-----------------------")
        //        _ <- conn.query(
        //          """[:find  ?tx ?tx2 ?doc ?doc_op
        //            | :where [_ :db.install/attribute ?id ?tx]
        //            |        [?id :db/doc ?doc ?tx2 ?doc_op]
        //            |        [?id :db/ident ?idIdent]
        //            |        [(namespace ?idIdent) ?nsFull]
        //            |        [(.matches ^String ?nsFull "^(db|db.alter|db.excise|db.install|db.part|db.sys|fressian|db.entity|db.attr|:?-.*)") ?sys]
        //            |        [(= ?sys false)]
        //            |        [(.contains ^String ?nsFull "_") ?isPart]
        //            |        [(.split ^String ?nsFull "_") ?nsParts]
        //            |        [(first ?nsParts) ?part0]
        //            |        [(if ?isPart ?part0 "db.part/user") ?part]
        //            |        [(last ?nsParts) ?ns]
        //            |        ]""".stripMargin
        //        ).map { r =>
        //          r.foreach(println)
        //          r
        //        }


        //        _ = println("-----------------------")
        //        _ <- conn.query(
        //          //          """[:find ?v ?id ?tx
        //          //            | :in $ [?actions ...]
        //          //            | :where [_ ?actions ?id ?tx]
        //          //            |        [?id :db/doc ?v]]""".stripMargin, Seq(":db.alter/attribute")
        //          //          """[:find ?tx ?v
        //          """[:find ?tx ?idIdent ?v
        //            | :in $ [?actions ...] [?options ...]
        //            | :where [_ ?actions ?id ?tx]
        //            |        [?id :db/ident ?idIdent]
        //            |        [?id ?options ?v]
        //            | ]""".stripMargin, Seq(":db.install/attribute", ":db.alter/attribute"), Seq(":db/doc")
        //          //            | :where [_ ?actions ?v ?tx]]""".stripMargin, Seq(":db/doc")
        //          //            |        [?id :db/ident ?idIdent]]""".stripMargin, Seq(":db.alter/attribute", ":db/doc")
        //        ).map { r =>
        //          r.map(l => (l.head.toString.toLong, l(1).toString, l.last.toString)).sortBy(r => (r._1, r._2))
        //            .takeRight(20).foreach(println)
        //          r
        //        }


        //        _ = println("-----------------------")
        //        _ <- conn.query(
        //          """[:find ?tx ?op ?action ?ident ?v ?id
        //            | :in $ [?action ...]
        //            | :where [?id ?action ?v ?tx ?op]
        //            |        [?id :db/ident ?ident]
        //            |        [(> ?tx 13194139534312)]
        //            | ]""".stripMargin, Seq(
        ////            ":db.install/attribute",
        ////            ":db.alter/attribute",
        //            ":db/ident",
        //            ":db/doc",
        //            ":db/noHistory",
        //          )
        //        ).map { r =>
        //          r.map(l => (l.head.toString.toLong, l(1).toString, l(2).toString, l(3).toString, l(4).toString, l(5).toString.toLong))
        //            .sortBy(r => (r._1, r._6))
        //            //            .takeRight(20)
        //            .foreach { r =>
        //              if (r._1 != last) {
        //                println("")
        //              }
        //              last = r._1
        //              println(r)
        //            }
        //          r
        //        }

        //        _ = println("-----------------------")
        //        _ = Peer.q(
        ////          """[:find ?tx ?op ?action ?ident ?v ?id
        //          """[:find ?tx ?op ?action ?ident ?v ?id
        //            | :in $ [?action ...]
        //            | :where [?id ?action ?v ?tx ?op]
        //            |        [?id :db/ident ?ident]
        //            |        [(> ?tx 13194139534312)]
        //            | ]""".stripMargin,
        ////            |        [(!= ?ident ?v)]
        //          db.getDatomicDb.asInstanceOf[Database].history(),
        //          Util.list(
        //            ":db.install/attribute",
        //            ":db.alter/attribute",
        //            ":db/ident",
        //            ":db/doc",
        //            ":db/noHistory",
        //          )
        //        ).asScala.toSeq.map { row0 =>
        //          val l = row0.asScala
        //          (l.head.toString.toLong, l(1).toString, l(2).toString, l(3).toString, l(4).toString, l(5).toString.toLong)
        //        }.sortBy(r => (r._1, r._6))
        //          .foreach { r =>
        //            if (r._1 != last) {
        //              println("")
        //            }
        //            last = r._1
        //            println(r)
        //          }
        //
        //        _ = println("-----------------------")
        //        _ = Peer.q(
        ////          """[:find ?tx ?a ?ident ?v ?op
        ////          """[:find ?tx ?a ?ident ?t ?inst ?op
        ////          """[:find ?tx ?op ?ident ?inst ?v
        //          """[:find ?tx ?op ?a ?ident
        //            |            :in $ %
        //            |            :where
        //            |            (or [:db.part/db :db.install/attribute ?a]
        //            |                [:db.part/db :db.alter/attribute ?a])
        //            |            [(datomic.api/ident $ ?a) ?ident]
        //            |            (or (entity-at ?a ?tx ?t ?inst ?op)
        //            |                (value-at ?a ?tx ?t ?inst ?op))
        //            |            [(> ?tx 13194139534312)]
        //            |                ]""".stripMargin,
        //          db.getDatomicDb.asInstanceOf[Database].history(),
        //          """[
        //            |  [
        //            |    (entity-at [?e] ?tx ?t ?inst ?op)
        //            |    [?e _ _ ?tx ?op]
        //            |    [(datomic.api/tx->t ?tx) ?t]
        //            |    [?tx :db/txInstant ?inst]
        //            |  ]
        //            |  [
        //            |    (value-at [?e] ?tx ?t ?inst ?op)
        //            |    [_ _ ?e ?tx ?op]
        //            |    [(datomic.api/tx->t ?tx) ?t]
        //            |    [?tx :db/txInstant ?inst]
        //            |  ]
        //            |]""".stripMargin
        //        ).asScala.toSeq.map { row0 =>
        //          val l = row0.asScala
        ////          (l.head.toString.toLong, l(1).toString, l(2).toString)
        ////          (l.head.toString.toLong, l(1).toString.toLong, l(2).toString, l(3).toString)
        //          (l.head.toString.toLong, l(1).toString, l(2).toString, l(3).toString)
        ////          (l.head.toString.toLong, l(1).toString, l(2).toString, l(3).toString, l(4).toString)
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
        //        _ = println("-----------------------")
        //        _ = Peer.q(
        //          //          """[:find ?tx ?a ?ident ?v ?op
        //          //          """[:find ?tx ?a ?ident ?t ?inst ?op
        //          //          """[:find ?tx ?op ?ident ?inst ?v
        //          """[:find ?tx ?op ?a ?ident ?action ?v
        //            |            :in $ %
        //            |            :where
        //            |            (or [:db.part/db :db.install/attribute ?a]
        //            |                [:db.part/db :db.alter/attribute ?a])
        //            |            [(datomic.api/ident $ ?a) ?ident]
        //            |            (or (entity-at ?a ?tx ?t ?inst ?op ?action ?v)
        //            |                (value-at ?a ?tx ?t ?inst ?op ?action ?v))
        //            |            [(> ?tx 13194139534312)]
        //            |                ]""".stripMargin,
        //          db.getDatomicDb.asInstanceOf[Database].history(),
        //          """[
        //            |  [
        //            |    (entity-at [?e] ?tx ?t ?inst ?op ?actionIdent ?x)
        //            |    [?e ?actionAttr ?v ?tx ?op]
        //            |    [?actionAttr :db/ident ?actionIdent]
        //            |    [(str "---------------- " ?v) ?x]
        //            |    [(datomic.api/tx->t ?tx) ?t]
        //            |    [?tx :db/txInstant ?inst]
        //            |  ]
        //            |  [
        //            |    (value-at [?e] ?tx ?t ?inst ?op ?actionIdent ?v)
        //            |    [_ ?actionAttr ?e ?tx ?op]
        //            |    [(identity ?e) ?v]
        //            |    [?actionAttr :db/ident ?actionIdent]
        //            |    [(datomic.api/tx->t ?tx) ?t]
        //            |    [?tx :db/txInstant ?inst]
        //            |  ]
        //            |]""".stripMargin
        //        ).asScala.toSeq.map { row0 =>
        //          val l = row0.asScala
        //          //          (l.head.toString.toLong, l(1).toString, l(2).toString)
        //          //          (l.head.toString.toLong, l(1).toString.toLong, l(2).toString, l(3).toString)
        //          //          (l.head.toString.toLong, l(1).toString, l(2).toString, l(3).toString)
        //          //          (l.head.toString.toLong, l(1).toString, l(2).toString, l(3).toString, l(4).toString)
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
        //
        //
        //        _ = println("-----------------------")
        //        _ = Peer.q(
        //          """[:find ?tx ?op ?a ?ident ?action ?v
        //            |            :in $ %
        //            |            :where
        //            |            (or [:db.part/db :db.install/attribute ?a]
        //            |                [:db.part/db :db.alter/attribute ?a])
        //            |            [(datomic.api/ident $ ?a) ?ident]
        //            |            (entity-at ?a ?tx ?t ?inst ?op ?action ?v)
        //            |            [(> ?tx 13194139534312)]
        //            |                ]""".stripMargin,
        //          db.getDatomicDb.asInstanceOf[Database].history(),
        //          """[
        //            |  [
        //            |    (entity-at [?e] ?tx ?t ?inst ?op ?actionIdent ?x)
        //            |    [?e ?actionAttr ?v ?tx ?op]
        //            |    [?actionAttr :db/ident ?actionIdent]
        //            |    [(str "---------------- " ?v) ?x]
        //            |    [(datomic.api/tx->t ?tx) ?t]
        //            |    [?tx :db/txInstant ?inst]
        //            |  ]
        //            |  [
        //            |    (value-at [?e] ?tx ?t ?inst ?op ?actionIdent ?v)
        //            |    [_ ?actionAttr ?e ?tx ?op]
        //            |    [(identity ?e) ?v]
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

        //                _ = println("-----------------------")
        //                _ <- conn.query(
        //                  """[:find  ?tx ?a
        //                    |        (pull ?doc_pull [(limit :db/doc nil)])
        //                    | :where [_ :db.install/attribute ?id ?tx]
        //                    |        [?id :db/ident ?idIdent]
        //                    |        [(namespace ?idIdent) ?nsFull]
        //                    |        [(.matches ^String ?nsFull "^(db|db.alter|db.excise|db.install|db.part|db.sys|fressian|db.entity|db.attr|:?-.*)") ?sys]
        //                    |        [(= ?sys false)]
        //                    |        [(.contains ^String ?nsFull "_") ?isPart]
        //                    |        [(.split ^String ?nsFull "_") ?nsParts]
        //                    |        [(first ?nsParts) ?part0]
        //                    |        [(if ?isPart ?part0 "db.part/user") ?part]
        //                    |        [(last ?nsParts) ?ns]
        //                    |        [(.compareTo ^Long ?tx 13194139534312) ?tx_10]
        //                    |        [(> ?tx_10 0)]
        //                    |        [(str ?idIdent) ?a]
        //                    |        [(identity ?id) ?doc_pull]]""".stripMargin
        //                ).map { r =>
        //                  r.foreach(println)
        //                  r
        //                }
        //
        //        _ <- Schema.tx.>(13194139534312L).a1.a.doc$.inspectGet
        //        _ <- Schema.tx.>(13194139534312L).a1.a.doc$.inspectGetHistory


        //        _ = println("-----------------------")
        //        _ = Peer.q(
        //          """[:find ?tx ?op ?a ?ident ?action ?v
        //            |            :in $ %
        //            |            :where
        //            |            [:db.part/db :db.install/attribute ?a]
        //            |            [(datomic.api/ident $ ?a) ?ident]
        //            |            (entity-at ?a ?tx ?t ?inst ?op ?action ?v)
        //            |            [(>= ?tx 13194139534312)]
        //            |                ]""".stripMargin,
        //          db.getDatomicDb.asInstanceOf[Database].history(),
        //          """[
        //            |  [
        //            |    (opt-doc-at [?e] ?doc ?tx ?t ?inst ?op)
        //            |    [?e :db/doc ?doc ?tx ?op]
        //            |    [(datomic.api/tx->t ?tx) ?t]
        //            |    [?tx :db/txInstant ?inst]
        //            |  ]
        //            |  [
        //            |    (opt-empty [?e] ?doc ?tx ?t ?inst ?op)
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


        //        _ = println("-----------------------")
        //        _ <- Schema.tx.>(13194139534312L).a1.a.doc$.get.map{r =>
        //          println(r)
        //          r
        //        }
        //
        //        _ = println("-----------------------")
        //        _ <- Schema.tx.>(13194139534312L).a1.doc.get.map{r =>
        //          println(r)
        //          r
        //        }

        //        _ = println("-----------------------")
        //        _ = Peer.q(
        //          //          """[:find ?tx ?a ?ident ?v ?op
        //          //          """[:find ?tx ?a ?ident ?t ?inst ?op
        //          //          """[:find ?tx ?op ?ident ?inst ?v
        //          """[:find ?tx ?op ?a ?ident ?action ?v
        //            |            :in $ %
        //            |            :where
        //            |            (or [:db.part/db :db.install/attribute ?a]
        //            |                [:db.part/db :db.alter/attribute ?a])
        //            |            [(datomic.api/ident $ ?a) ?ident]
        //            |            (entity-at ?a ?tx ?t ?inst ?op ?action ?v)
        //            |            [(> ?tx 13194139534312)]
        //            |                ]""".stripMargin,
        //          db.getDatomicDb.asInstanceOf[Database].history(),
        //          """[
        //            |  [
        //            |    (entity-at [?e] ?tx ?t ?inst ?op ?actionIdent ?x)
        //            |    [?e ?actionAttr ?v ?tx ?op]
        //            |    [?actionAttr :db/ident ?actionIdent]
        //            |    [(str "---------------- " ?v) ?x]
        //            |    [(datomic.api/tx->t ?tx) ?t]
        //            |    [?tx :db/txInstant ?inst]
        //            |  ]
        //            |  [
        //            |    (value-at [?e] ?tx ?t ?inst ?op ?actionIdent ?v)
        //            |    [_ ?actionAttr ?e ?tx ?op]
        //            |    [(identity ?e) ?v]
        //            |    [?actionAttr :db/ident ?actionIdent]
        //            |    [(datomic.api/tx->t ?tx) ?t]
        //            |    [?tx :db/txInstant ?inst]
        //            |  ]
        //            |]""".stripMargin
        //        ).asScala.toSeq.map { row0 =>
        //          val l = row0.asScala
        //          //          (l.head.toString.toLong, l(1).toString, l(2).toString)
        //          //          (l.head.toString.toLong, l(1).toString.toLong, l(2).toString, l(3).toString)
        //          //          (l.head.toString.toLong, l(1).toString, l(2).toString, l(3).toString)
        //          //          (l.head.toString.toLong, l(1).toString, l(2).toString, l(3).toString, l(4).toString)
        //          (l.head.toString.toLong, l(1).toString, l(2).toString, l(3).toString, l(4).toString, l(5).toString)
        //        }
        //          .sortBy(r => (r._1, r._3))
        //          .foreach { r =>
        //            if (r._1 != last) {
        //              println("")
        //            }
        //            last = r._1
        //            println(r)
        //          }

        //          map { r =>
        //          r.map(l => (l.head.toString.toLong, l(1).toString, l(2).toString, l(3).toString, l(4).toString, l(5).toString))
        //            .sortBy(r => (r._1, r._5))
        //            //            .takeRight(20)
        //            .foreach(println)
        //          r
        //        }

        //        _ = println("-----------------------")
        //        _ <- conn.query(
        //          """[:find ?tx ?idIdent ?id
        //            | :in $ [?actions ...]
        //            | :where [_ ?actions ?id ?tx]
        //            |        [?id :db/ident ?idIdent]]""".stripMargin, Seq(":db.install/attribute")
        //          //            |        [?id :db/ident ?idIdent]]""".stripMargin, Seq(13)
        //        ).map { r =>
        //          r.map(l => (l.head.toString, l(1).toString, l.last.toString.toLong)).sortBy(_._2).foreach(println)
        //          r
        //        }

        //        db <- conn.db
        //
        //        _ = {
        //          val res = Peer.q(
        //            """[:find ?idIdent ?id
        //              | :in $ [?actions ...]
        //              | :where [_ ?actions ?id ?tx]
        //              |        [?id :db/ident ?idIdent]]""".stripMargin, db.getDatomicDb, Util.list(13)
        //          )
        ////          val res = Peer.q(
        ////            """[:find ?idIdent ?id
        ////              | :in $ ?actions
        ////              | :where [_ ?actions ?id ?tx]
        ////              |        [?id :db/ident ?idIdent]]""".stripMargin, db.getDatomicDb, 13
        //////              |        [?id :db/ident ?idIdent]]""".stripMargin, db.getDatomicDb, ":db.install/attribute"
        ////          )
        //          res.forEach(row => println(row))
        //        }


        //        _ <- conn.query(
        //          """[:find  ?idIdent ?id
        //            | :where [?id :db/ident ?idIdent]]""".stripMargin
        //        ).map { r =>
        //          println("-----------------")
        //          r.map(l => (l.head.toString, l.last.toString)).sortBy(_._1).foreach(println)
        //
        //          println("-----------------")
        //          r.map(l => (l.last.toString.toLong, l.head.toString)).sortBy(_._1).foreach(println)
        //
        //          r
        //        }

      } yield ()

    }


    //    "adhoc" - products { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
    //
    //      for {
    //
    //      } yield ()
    //    }


    //    "mbrainz" - mbrainz { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.mbrainz.dsl.MBrainz._
    //      val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
    //
    //      for {
    //        ledZeppelin <- Artist.e.gid_(ledZeppelinUUID).get
    //        mccartney <- Artist.e.gid_(UUID.fromString("ba550d0e-adac-4864-b88b-407cab5e76af")).get
    //        darkSideOfTheMoon <- Release.e.gid_(UUID.fromString("24824319-9bb8-3d1e-a2c5-b8b864dafd1b")).get
    //        dylanHarrisonSessions <- Release.e.gid_(UUID.fromString("67bbc160-ac45-4caf-baae-a7e9f5180429")).get
    //        concertForBangladesh <- Release.e.gid_(UUID.fromString("f3bdff34-9a85-4adc-a014-922eef9cdaa5")).get
    //        dylanHarrisonCd <- Release(dylanHarrisonSessions).media.get
    //        ghosotRiders <- Release(dylanHarrisonSessions).Media.Tracks.e.position_(11).get
    //        gb <- Country.e.name_("United Kingdom").get
    //        georgeHarrison <- Artist.e.name_("George Harrison").get
    //        bobDylan <- Artist.e.name_("Bob Dylan").get
    //
    //
    //      } yield ()
    //    }

    //
    //
    //    "adhoc" - bidirectional { implicit conn =>
    //      import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
    //
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //
    //
    //
    //      } yield ()
    //    }

  }
}
