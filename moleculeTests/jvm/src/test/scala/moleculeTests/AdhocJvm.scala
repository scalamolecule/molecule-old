package moleculeTests

import molecule.core.data.model.{oneInt, oneString}
import molecule.core.macros.GetTransactSchema.schema
import molecule.core.util.Executor._
import molecule.core.util.{Helpers, JavaConversions}
import molecule.datomic.api.in1_out15._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import utest._

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

    "adhocJvm" - empty { implicit futConn =>
      for {
        conn <- futConn
        //        t0 <- db.basisT



        _ <- transact(schema {
          trait Foo {
            val int: oneInt = oneInt
          }
        })

        _ <- Schema.a.valueType.cardinality.get.map(_ ==> List(
          (":Foo/int", "long", "one"),
        ))

        _ <- transact(schema {
          trait Foo {
            val int = oneInt
            val str = oneString
          }
        })

        _ <- Schema.a.valueType.cardinality.get.map(_ ==> List(
          (":Foo/int", "long", "one"),
          (":Foo/str", "string", "one"),
        ))


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
        _ <- Schema.tx.a1.id.a2.a.ident.valueType.cardinality.doc$.unique$.isComponent$.noHistory$.index$.fulltext$.inspectGet
        //        _ <- Schema.tx.a1.id.a2.a.ident$.valueType$.cardinality$.doc$.unique$.isComponent$.noHistory$.index$.fulltext$.inspectGet
        //                _ <- Schema.tx.a1.id.a2.a.ident$.tpe$.card$.doc$.unique$.isComponent$.noHistory$.index$.fulltext$.getHistory
        //        _ <- Schema.a.inspectGet
        //        _ <- Schema.a.uniqueValue.inspectGet

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
        //        _ <- conn.query(
        //          """[:find  ?a ?attr
        //            | :where [_ :db.install/attribute ?id ?tx]
        //            |        [(>= ?tx 13194139534312)]
        //            |        [?id :db/ident ?idIdent]
        //            |        [(namespace ?idIdent) ?nsFull]
        //            |        [(name ?idIdent) ?attr]
        //            |        [(.contains ^String ?nsFull "_") ?isPart]
        //            |        [(.split ^String ?nsFull "_") ?nsParts]
        //            |        [(first ?nsParts) ?part0]
        //            |        [(if ?isPart ?part0 "db.part/user") ?part]
        //            |        [(last ?nsParts) ?ns]
        //            |        [(str ?idIdent) ?a]
        //            |        ]""".stripMargin
        //        ).map { r =>
        //          r.foreach(println)
        //          r
        //        }

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
