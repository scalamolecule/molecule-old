package moleculeTests.tests.core.crud

import molecule.datomic.api.out10._
import molecule.datomic.base.api.Datom
import molecule.datomic.base.util.SystemPeer
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object TxReport extends AsyncTestSuite {
  val txInstAttrId = 50

  // Attribute ids for dev-local 1 higher
  val d = if (system == SystemPeer) 0 else 1

  lazy val tests = Tests {

    "Card one attrs" - core { implicit conn =>
      for {
        txReport <- Ns.str.int.long.double.bool.date.uuid.uri.bigInt.bigDec insert List(
          ("a", 1, 2L, 3.3, true, date1, uuid1, uri1, bigInt4, bigDec5)
        )
        e = txReport.eid
        tx = txReport.tx
        txInstant = txReport.txInstant

        _ = txReport.txData ==> List(
          Datom(tx, txInstAttrId, txInstant, tx, true),
          Datom(e, 72 + d, "a", tx, true),
          Datom(e, 73 + d, 1, tx, true),
          Datom(e, 74 + d, 2L, tx, true),
          Datom(e, 75 + d, 3.3, tx, true),
          Datom(e, 76 + d, true, tx, true),
          Datom(e, 77 + d, date1, tx, true),
          Datom(e, 78 + d, uuid1, tx, true),
          Datom(e, 79 + d, uri1, tx, true),
          Datom(e, 80 + d, bigInt4, tx, true),
          Datom(e, 81 + d, bigDec5, tx, true)
        )
      } yield ()
    }


    "Card many attrs" - core { implicit conn =>
      for {
        txReport <- Ns.strs.ints.longs.doubles.bools.dates.uuids.uris.bigInts.bigDecs insert List(
          (
            Set("a", "b"),
            Set(1, 2),
            Set(2L, 3L),
            Set(3.3, 4.4),
            Set(true, false),
            Set(date1, date2),
            Set(uuid1, uuid2),
            Set(uri1, uri2),
            Set(bigInt4, bigInt5),
            Set(bigDec5, bigDec6),
          )
        )
        e = txReport.eid
        tx = txReport.tx
        txInstant = txReport.txInstant

        _ = txReport.txData ==> List(
          Datom(tx, txInstAttrId, txInstant, tx, true),
          Datom(e, 86 + d, "a", tx, true),
          Datom(e, 86 + d, "b", tx, true),
          Datom(e, 87 + d, 1, tx, true),
          Datom(e, 87 + d, 2, tx, true),
          Datom(e, 88 + d, 2L, tx, true),
          Datom(e, 88 + d, 3L, tx, true),
          Datom(e, 89 + d, 3.3, tx, true),
          Datom(e, 89 + d, 4.4, tx, true),
          Datom(e, 90 + d, true, tx, true),
          Datom(e, 90 + d, false, tx, true),
          Datom(e, 91 + d, date1, tx, true),
          Datom(e, 91 + d, date2, tx, true),
          Datom(e, 92 + d, uuid1, tx, true),
          Datom(e, 92 + d, uuid2, tx, true),
          Datom(e, 93 + d, uri1, tx, true),
          Datom(e, 93 + d, uri2, tx, true),
          Datom(e, 94 + d, bigInt4, tx, true),
          Datom(e, 94 + d, bigInt5, tx, true),
          Datom(e, 95 + d, bigDec5, tx, true),
          Datom(e, 95 + d, bigDec6, tx, true)
        )
      } yield ()
    }


    "Card map attrs" - core { implicit conn =>
      for {
        txReport <- Ns.strMap.intMap insert List(
          (
            Map("en" -> "Hi there", "da" -> "Hejsa"),
            Map("en" -> 10, "da" -> 30)
          )
        )
        e = txReport.eid
        tx = txReport.tx
        txInstant = txReport.txInstant

        _ = txReport.txData ==> List(
          Datom(tx, txInstAttrId, txInstant, tx, true),
          Datom(e, 100 + d, "en@Hi there", tx, true),
          Datom(e, 100 + d, "da@Hejsa", tx, true),
          Datom(e, 101 + d, "en@10", tx, true),
          Datom(e, 101 + d, "da@30", tx, true),
        )
      } yield ()
    }
  }
}
