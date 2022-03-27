package moleculeTests.tests.core.obj

import molecule.core.util.Helpers
import molecule.datomic.api.in1_out22._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object ObjGeneric extends AsyncTestSuite with Helpers {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      txR <- Ns.int(1).save
      e = txR.eid
      t = txR.t
      tx = txR.tx
      txInstant = txR.txInstant

      txR2 <- Ns(e).int(2).update
      t2 = txR2.t
      tx2 = txR2.tx
      txInstant2 = txR2.txInstant

      txR3 <- Ns(e).str("a").update
      t3 = txR3.t
      tx3 = txR3.tx
      txInstant3 = txR3.txInstant
    } yield {
      (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3)
    }
  }


  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "Datom" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData

        // Current datom values
        _ <- Ns(e).int.e.a.v.t.tx.txInstant.getObj.map { datom =>
          datom.int ==> 2
          datom.e ==> e
          datom.a ==> ":Ns/int"
          datom.v ==> 2
          datom.t ==> t2 // last transaction involving `int`
          datom.tx ==> tx2
          datom.txInstant ==> txInstant2
        }

        _ <- Ns(e).str.e.a.v.t.tx.txInstant.getObj.map { datom =>
          datom.str ==> "a"
          datom.e ==> e
          datom.a ==> ":Ns/str"
          datom.v ==> "a"
          datom.t ==> t3 // last transaction involving `str`
          datom.tx ==> tx3
          datom.txInstant ==> txInstant3
        }
      } yield ()
    }

    "Log" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData

        // Transaction t until t2 (not inclusive) - meaning only the first transaction
        _ <- Log(Some(t), Some(t2)).e.a.v.t.tx.txInstant.op.getObjs.collect { case List(txDatom, intDatom) =>
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
        intAttrId = (system, protocol) match {
          case (SystemPeer, "free") => 64
          case (SystemPeer, _)      => 73
          case (_, _)               => 74
        }
        intIndex = if (system == SystemPeer) Some(true) else None
        _ <- Schema
          .part.attrId.a(":Ns/int").nsFull.ns.attr.valueType.cardinality.doc
          .index$.unique$.fulltext$.isComponent$.noHistory$.t.tx.txInstant.getObj.map { schema =>
          schema.attrId ==> intAttrId
          schema.a ==> ":Ns/int"
          schema.part ==> "db.part/user"
          schema.nsFull ==> "Ns"
          schema.ns ==> "Ns"
          schema.attr ==> "int"
          schema.valueType ==> "long"
          schema.cardinality ==> "one"
          schema.doc ==> "Card one Int attribute"
          schema.index$ ==> intIndex
          schema.unique$ ==> None
          schema.fulltext$ ==> None
          schema.isComponent$ ==> None
          schema.noHistory$ ==> None
          schema.t // t of creation transaction
          schema.tx // tx of creation transaction
          schema.txInstant // txInstant of creation transaction
        }
      } yield ()
    }

    "EAVT" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData
        // Entity `e`
        _ <- EAVT(e).e.a.v.t.tx.txInstant.op.getObjs.collect { case List(datom1, datom2) =>
          datom1.e ==> e
          datom1.a ==> ":Ns/str"
          datom1.v ==> "a"
          datom1.t ==> t3 // last transaction involving `str`
          datom1.tx ==> tx3
          datom1.txInstant ==> txInstant3
          datom1.op ==> true

          datom2.e ==> e
          datom2.a ==> ":Ns/int"
          datom2.v ==> 2
          datom2.t ==> t2 // last transaction involving `int`
          datom2.tx ==> tx2
          datom2.txInstant ==> txInstant2
          datom2.op ==> true
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
        txInstant4 = txR4.txInstant

        // :Ns/int attribute
        _ <- AEVT(":Ns/int").e.a.v.t.a1.tx.txInstant.op.getObjs.collect { case datoms =>
          val List(datom1, datom2) = datoms
          datom1.e ==> e
          datom1.a ==> ":Ns/int"
          datom1.v ==> 2
          datom1.t ==> t2
          datom1.tx ==> tx2
          datom1.txInstant ==> txInstant2
          datom1.op ==> true

          datom2.e ==> e4
          datom2.a ==> ":Ns/int"
          datom2.v ==> 4
          datom2.t ==> t4
          datom2.tx ==> tx4
          datom2.txInstant ==> txInstant4
          datom2.op ==> true
        }
      } yield ()
    }

    "AVET" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData
        // :Ns/int / value 2

        _ <- AVET(":Ns/int", 2).e.a.v.t.tx.txInstant.op.getObj.map { datom =>
          datom.e ==> e
          datom.a ==> ":Ns/int"
          datom.v ==> 2
          datom.t ==> t2
          datom.tx ==> tx2
          datom.txInstant ==> txInstant2
          datom.op ==> true
        }
      } yield ()
    }

    "VAET" - core { implicit conn =>
      for {
        (e, t, tx, txInstant, t2, tx2, txInstant2, t3, tx3, txInstant3) <- testData
        ref <- Ref1.int1(11).save.map(_.eid)

        txR4 <- Ns(e).ref1(ref).update
        t4 = txR4.t
        tx4 = txR4.tx
        txInstant4 = txR4.txInstant

        // Reference
        _ <- VAET(ref).e.a.v.t.tx.txInstant.op.getObj.map { datom =>
          datom.e ==> e
          datom.a ==> ":Ns/ref1"
          datom.v ==> ref
          datom.t ==> t4
          datom.tx ==> tx4
          datom.txInstant ==> txInstant4
          datom.op ==> true
        }
      } yield ()
    }
  }
}