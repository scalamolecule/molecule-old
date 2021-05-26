package moleculeTests.tests.core.obj

import molecule.core.util.Helpers
import molecule.datomic.api.in1_out18._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object Generic_ extends AsyncTestSuite with Helpers {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      txR <- Ns.int(1).save
      e = txR.eid
      t = txR.t
      tx = txR.tx
      txInstant = txR.inst

      txR2 <- Ns(e).int(2).update
      t2 = txR2.t
      tx2 = txR2.tx
      txInstant2 = txR2.inst

      txR3 <- Ns(e).str("a").update
      t3 = txR3.t
      tx3 = txR3.tx
      txInstant3 = txR3.inst
    } yield {
      (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3)
    }
  }


  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Datom" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData

        // Current datom values
        _ <- Ns(e).int.e.a.v.t.tx.txInstant.getObj.map { d1 =>
          d1.int ==> 2
          d1.e ==> e
          d1.a ==> ":Ns/int"
          d1.v ==> 2
          d1.t ==> t2 // last transaction involving `int`
          d1.tx ==> tx2
          d1.txInstant ==> txInstant2
        }

        _ <- Ns(e).str.e.a.v.t.tx.txInstant.getObj.map { d2 =>
          d2.str ==> "a"
          d2.e ==> e
          d2.a ==> ":Ns/str"
          d2.v ==> "a"
          d2.t ==> t3 // last transaction involving `str`
          d2.tx ==> tx3
          d2.txInstant ==> txInstant3
        }
      } yield ()
    }

    "Log" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData

        // Transaction t until t2 (not inclusive) - meaning only the first transaction
        _ <- Log(Some(t), Some(t2)).e.a.v.t.tx.txInstant.op.getObjList.collect { case List(txDatom, intDatom) =>
          // Transaction datom
          txDatom.e ==> tx
          txDatom.a ==> ":db/txInstant"
          txDatom.v ==> txInstant
          txDatom.t ==> t
          txDatom.tx ==> tx
          txDatom.txInstant ==> txInstant
          txDatom.op ==> true

          intDatom.e ==> e
          intDatom.a ==> ":Ns/int"
          intDatom.v ==> 1
          intDatom.t ==> t
          intDatom.tx ==> tx
          intDatom.txInstant ==> txInstant
          intDatom.op ==> true
        }
      } yield ()
    }

    "Schema" - core { implicit conn =>
      for {
        _ <- testData

        // :Ns/int attribute
        _ <- Schema
          .part.id.a(":Ns/int").nsFull.ns.attr.tpe.card.doc
          .index$.unique$.fulltext$.isComponent$.noHistory$.enum$
          .t.tx.txInstant.getObj.map { o1 =>
          o1.part ==> "db.part/user"
          o1.id ==> 73
          o1.a ==> ":Ns/int"
          o1.nsFull ==> "Ns"
          o1.ns ==> "Ns"
          o1.attr ==> "int"
          o1.tpe ==> "long"
          o1.card ==> "one"
          o1.doc ==> "Card one Int attribute"
          o1.index$ ==> Some(true)
          o1.unique$ ==> None
          o1.fulltext$ ==> None
          o1.isComponent$ ==> None
          o1.noHistory$ ==> None
          o1.enum$ ==> None
          o1.t // t of creation transaction
          o1.tx // tx of creation transaction
          o1.txInstant // txInstant of creation transaction
        }
      } yield ()
    }

    "EAVT" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData
        // Entity `e`
        _ <- EAVT(e).e.a.v.t.tx.txInstant.op.getObjList.collect { case List(d1, d2) =>
          d1.e ==> e
          d1.a ==> ":Ns/str"
          d1.v ==> "a"
          d1.t ==> t3 // last transaction involving `str`
          d1.tx ==> tx3
          d1.txInstant ==> txInstant3
          d1.op ==> true

          d2.e ==> e
          d2.a ==> ":Ns/int"
          d2.v ==> 2
          d2.t ==> t2 // last transaction involving `int`
          d2.tx ==> tx2
          d2.txInstant ==> txInstant2
          d2.op ==> true
        }
      } yield ()
    }

    "AEVT" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData

        txR4 <- Ns.int(4).save
        e4 = txR4.eid
        t4 = txR4.t
        tx4 = txR4.tx
        txInstant4 = txR4.inst

        // :Ns/int attribute
        _ <- AEVT(":Ns/int").e.a.v.t.tx.txInstant.op.getObjList.collect { case List(d1, d2) =>
          d1.e ==> e
          d1.a ==> ":Ns/int"
          d1.v ==> 2
          d1.t ==> t2
          d1.tx ==> tx2
          d1.txInstant ==> txInstant2
          d1.op ==> true

          d2.e ==> e4
          d2.a ==> ":Ns/int"
          d2.v ==> 4
          d2.t ==> t4
          d2.tx ==> tx4
          d2.txInstant ==> txInstant4
          d2.op ==> true
        }
      } yield ()
    }

    "AVET" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData
        // :Ns/int / value 2

        _ <- AVET(":Ns/int", 2).e.a.v.t.tx.txInstant.op.getObj.map { d1 =>
          d1.e ==> e
          d1.a ==> ":Ns/int"
          d1.v ==> 2
          d1.t ==> t2
          d1.tx ==> tx2
          d1.txInstant ==> txInstant2
          d1.op ==> true
        }
      } yield ()
    }

    "VAET" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData
        tx <- Ref1.int1(11).save
        ref = tx.eid

        txR4 <- Ns(e).ref1(ref).update
        t4 = txR4.t
        tx4 = txR4.tx
        txInstant4 = txR4.inst

        // Reference
        _ <- VAET(ref).e.a.v.t.tx.txInstant.op.getObj.map { d1 =>
          d1.e ==> e
          d1.a ==> ":Ns/ref1"
          d1.v ==> ref
          d1.t ==> t4
          d1.tx ==> tx4
          d1.txInstant ==> txInstant4
          d1.op ==> true
        }
      } yield ()
    }
  }
}