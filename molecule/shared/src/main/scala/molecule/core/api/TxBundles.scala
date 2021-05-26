package molecule.core.api

import molecule.core.util.Helpers
import molecule.datomic.base.ast.tempDb.With
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}

trait TxBundles extends Helpers {


  /** Asynchronously transact bundled transaction statements
    *
    * Supply transaction statements of one or more molecule actions to asynchronously
    * transact a single atomic transaction.
    * {{{
    *   transactBundle(
    *     e1.getRetractTx,
    *     Ns.int(4).getSaveTx,
    *     Ns.int.getInsertTx(List(5, 6)),
    *     Ns(e2).int(20).getUpdateTx
    *   ) map { bundleTx =>
    *     Ns.int.getAsync map { queryResult =>
    *       queryResult === List(3, 4, 5, 6, 20)
    *     }
    *   }
    * }}}
    *
    * @group bundled
    * @param stmtss [[molecule.datomic.base.ast.transactionModel.Statement Statement]]'s from multiple molecule operations
    * @param conn   Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return Future with [[molecule.datomic.base.facade.TxReport TxReport]] with result of transaction
    */
  def transactBundle(
    stmtss: Future[Seq[Statement]]*
  )(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    conn.flatMap(_.transact(Future.sequence(stmtss).map(_.flatten)))
  }


  /** Inspect transaction bundle statements
    * <br><br>
    * Add transaction statements from one or more molecule actions to `inspectTransact`
    * to see the bundled transaction statements.
    * {{{
    * inspectTransact(
    *   // retract
    *   e1.getRetractTx,
    *   // save
    *   Ns.int(4).getSaveTx,
    *   // insert
    *   Ns.int.getInsertTx(List(5, 6)),
    *   // update
    *   Ns(e2).int(20).getUpdateTx
    * )
    *
    * // Prints transaction data to output:
    * /*
    *   ## 1 ## TxReport
    *   ========================================================================
    *   1          ArrayBuffer(
    *     1          List(
    *       1          :db/retractEntity   17592186045445)
    *     2          List(
    *       1          :db/add       #db/id[:db.part/user -1000247]     :Ns/int          4           Card(1))
    *     3          List(
    *       1          :db/add       #db/id[:db.part/user -1000252]     :Ns/int          5           Card(1))
    *     4          List(
    *       1          :db/add       #db/id[:db.part/user -1000253]     :Ns/int          6           Card(1))
    *     5          List(
    *       1          :db/add       17592186045446                     :Ns/int          20          Card(1)))
    *   ------------------------------------------------
    *   2          List(
    *     1    1     added: true ,   t: 13194139534345,   e: 13194139534345,   a: 50,   v: Wed Nov 14 23:38:15 CET 2018
    *
    *     2    2     added: false,  -t: 13194139534345,  -e: 17592186045445,  -a: 64,  -v: 1
    *
    *     3    3     added: true ,   t: 13194139534345,   e: 17592186045450,   a: 64,   v: 4
    *
    *     4    4     added: true ,   t: 13194139534345,   e: 17592186045451,   a: 64,   v: 5
    *
    *     5    5     added: true ,   t: 13194139534345,   e: 17592186045452,   a: 64,   v: 6
    *
    *     6    6     added: true ,   t: 13194139534345,   e: 17592186045446,   a: 64,   v: 20
    *          7     added: false,  -t: 13194139534345,  -e: 17592186045446,  -a: 64,  -v: 2)
    *   ========================================================================
    * */
    * }}}
    *
    * @group bundled
    * @param stmtss [[molecule.datomic.base.ast.transactionModel.Statement Statement]]'s from multiple molecule operations
    * @param conn   Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    */
  def inspectTransactBundle(
    stmtss: Future[Seq[Statement]]*
  )(implicit conn: Future[Conn], ec: ExecutionContext): Future[Unit] = {
    for {
      conn <- conn
      // Use temporary branch of db to not changing any live data
      _ <- conn.testDbWith()

      // Print tx report to console
      _ <- conn.transact(Future.sequence(stmtss).map(_.flatten)).map(_.inspect)
    } yield conn.useLiveDb
  }
}
