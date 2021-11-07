package moleculeTests.tests.core.crud

import molecule.datomic.api.out10._
import molecule.datomic.base.api.Datom
import moleculeTests.Adhoc.{bigDec5, bigDec6, bigInt4, bigInt5, date1, date2, uri1, uri2, uuid1, uuid2}
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object TxReport extends AsyncTestSuite {
  val txInstAttrId = 50

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
          Datom(e, 72, "a", tx, true),
          Datom(e, 73, 1, tx, true),
          Datom(e, 74, 2L, tx, true),
          Datom(e, 75, 3.3, tx, true),
          Datom(e, 76, true, tx, true),
          Datom(e, 77, date1, tx, true),
          Datom(e, 78, uuid1, tx, true),
          Datom(e, 79, uri1, tx, true),
          Datom(e, 80, bigInt4, tx, true),
          Datom(e, 81, bigDec5, tx, true)
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
          Datom(e, 86, "a", tx, true),
          Datom(e, 86, "b", tx, true),
          Datom(e, 87, 1, tx, true),
          Datom(e, 87, 2, tx, true),
          Datom(e, 88, 2L, tx, true),
          Datom(e, 88, 3L, tx, true),
          Datom(e, 89, 3.3, tx, true),
          Datom(e, 89, 4.4, tx, true),
          Datom(e, 90, true, tx, true),
          Datom(e, 90, false, tx, true),
          Datom(e, 91, date1, tx, true),
          Datom(e, 91, date2, tx, true),
          Datom(e, 92, uuid1, tx, true),
          Datom(e, 92, uuid2, tx, true),
          Datom(e, 93, uri1, tx, true),
          Datom(e, 93, uri2, tx, true),
          Datom(e, 94, bigInt4, tx, true),
          Datom(e, 94, bigInt5, tx, true),
          Datom(e, 95, bigDec5, tx, true),
          Datom(e, 95, bigDec6, tx, true)
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
          Datom(e, 100, "en@Hi there", tx, true),
          Datom(e, 100, "da@Hejsa", tx, true),
          Datom(e, 101, "en@10", tx, true),
          Datom(e, 101, "da@30", tx, true),
        )
      } yield ()
    }
  }
}
