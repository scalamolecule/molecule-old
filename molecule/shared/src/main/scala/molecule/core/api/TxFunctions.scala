package molecule.core.api

import java.util.concurrent.{ExecutionException => ExecutionExc}
import molecule.core.ast.elements.{Composite, Model, TxMetaData}
import molecule.core.exceptions.TxFnException
import molecule.core.macros.TxFunctionCall
import molecule.core.util.{Helpers, JavaUtil}
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.util.control.NonFatal


/** Transactional methods for bundled transactions and tx functions
  *
  * @groupname bundled Bundled transactions
  * @groupdesc bundled Multiple molecule operations in one atomic transaction.
  * @groupprio bundled 1
  * @groupname txfn Transaction functions
  * @groupdesc txfn Atomic transaction logic with access to tx database value.
  * @groupprio txfn 2
  */
trait TxFunctions {

  /** Asynchronously transact tx function invocation
    * <br><br>
    * Macro that takes a tx function invocation itself as its argument. The tx function is analyzed
    * by the macro and the necessary transaction preparations done at compile time.
    * <br><br>
    * At runtime, the returned statements from the tx function is asynchronously transacted as
    * one atomic transaction using Datomic's asynchronous API.
    * {{{
    * Await.result(
    *   transactAsync(transfer(fromAccount, toAccount, 20)) map { txReport =>
    *     Account(fromAccount).balance.get.map(_.head ==> 80) // (could be asynchronous too)
    *     Account(toAccount).balance.get.map(_.head ==> 720)
    *   },
    *   2.seconds
    * )
    * }}}
    * Additional transaction meta data can be added
    * {{{
    * Await.result(
    *   transactAsync(
    *     transfer(fromAccount, toAccount, 20),
    *     Person.name("John"),
    *     UseCase.name("Scheduled transfer")) map { txReport =>
    *       Account(fromAccount).balance
    *       .Tx(Person.name_("John"))
    *       .Tx(UseCase.name_("Scheduled transfer"))
    *       .get.map(_.head ==> 80) // (could be asynchronous too)
    *   },
    *   2.seconds
    * )
    * }}}
    *
    * @group txfn
    * @param txFnCall    Tx function invocation
    * @param txMolecules Optional tx meta data molecules
    * @return Future with [[molecule.datomic.base.facade.TxReport TxReport]] with result of transaction
    */
  def transactFn(
    txFnCall: Future[Seq[Statement]],
    txMolecules: Molecule*
  )(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = macro TxFunctionCall.txFnCall


  /** Inspect tx function invocation
    * <br><br>
    * Print transaction statements to output of a tx function invocation
    * without affecting the live database.
    * {{{
    * // Print inspect info for tx function invocation
    * inspectTransact(transfer(fromAccount, toAccount, 20))
    *
    * // Prints produced tx statements to output:
    * /*
    * ## 1 ## TxReport
    * ========================================================================
    * 1          ArrayBuffer(
    *   1          List(
    *     1          :db/add       17592186045445       :Account/balance    80        Card(1))
    *   2          List(
    *     1          :db/add       17592186045447       :Account/balance    720       Card(1)))
    * ------------------------------------------------
    * 2          List(
    *   1    1     added: true ,   t: 13194139534345,   e: 13194139534345,   a: 50,   v: Thu Nov 22 16:23:09 CET 2018
    *
    *   2    2     added: true ,   t: 13194139534345,   e: 17592186045445,   a: 64,   v: 80
    *        3     added: false,  -t: 13194139534345,  -e: 17592186045445,  -a: 64,  -v: 100
    *
    *   3    4     added: true ,   t: 13194139534345,   e: 17592186045447,   a: 64,   v: 720
    *        5     added: false,  -t: 13194139534345,  -e: 17592186045447,  -a: 64,  -v: 700)
    * ========================================================================
    * */
    * }}}
    *
    * @group txfn
    * @param txFnCall    Tx function invocation
    * @param txMolecules Optional tx meta data molecules
    */
  def inspectTransactFn(
    txFnCall: Future[Seq[Statement]],
    txMolecules: Molecule*
  )(implicit conn: Future[Conn], ec: ExecutionContext): Future[Unit] = macro TxFunctionCall.inspectTxFnCall


}


object TxFunctions extends Helpers with JavaUtil {

  import Util._

  def excMissingScalaJar(e: Throwable): String =
    s"""The Datomic transactor needs any dependencies in transactor
       |functions to be available on its classpath. Please copy the scala-library jar to the Datomic transactor lib.
       |Supposing $$DATOMIC_HOME points to your Datomic distribution folder you can likely run this command (on a mac):
       |cp ~/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-<scala-version>.jar $$DATOMIC_HOME/lib/
       |You might also want to copy the molecule library:
       |cp ~/.ivy2/cache/org.scalamolecule/molecule_2.13/<project-version>/jars/molecule_2.13.jar $$DATOMIC_HOME/lib/
       |You need to then restart the Datomic transactor.
       |${e.getMessage}""".stripMargin

  def excMissingMoleculeClass(e: Throwable): String =
    s"""The Datomic transactor needs any dependencies in transactor
       |functions to be available on its classpath. Please copy the Molecule library jar to the Datomic transactor lib.
       |Supposing $$DATOMIC_HOME points to your Datomic distribution folder you can likely run this command (on a mac):
       |cp ~/.ivy2/cache/org.scalamolecule/molecule_2.13/<project-version>/jars/molecule_2.13.jar $$DATOMIC_HOME/lib/
       |You need to then restart the Datomic transactor.
       |${e.getMessage}""".stripMargin

  def excMissingMoleculeMethod(e: Throwable): String = {
    val err    = e.getMessage
    val method = err.substring(29, err.indexOf("("))
    s"""The Datomic transactor needs all used Scala transaction methods to be in its scope.
       |The Scala transaction method `$method` is not found by the transactor. So you may need to restart
       |the transactor (if using classpath) or make an updated jar with all transaction functions for the transactor lib.
       |${e.getMessage}""".stripMargin
  }

  def txFnException(e: Throwable): String = "Unexpected error when invoking transaction function:\n" + e.getCause

  val redundant = "molecule.core.exceptions.TxFnException: ".length


  /** Invoke transaction function call synchronously (blocks)
    *
    * See also non-blocking asynchronous implementation
    * */
  def txFnCall(
    txFnDatomic: String,
    txMolecules: Seq[Molecule],
    args: Any*
  )(implicit futConn: Future[Conn], ec: ExecutionContext): Future[TxReport] = try {
    for {
      conn <- futConn

      // Install transaction function if not installed yet
      txFns <- conn.db.pull("[*]", s":$txFnDatomic")
      _ <- if (txFns.size() == 1) {
        // Only id returned - tx function needs to be installed in db
        conn.transact(conn.buildTxFnInstall(txFnDatomic, args))
      } else Future.unit

      txMetaStmts <- if (txMolecules.nonEmpty) {
        val txElements = txMolecules.flatMap { mol =>
          mol._model.elements.flatMap {
            case Composite(elements) => elements
            case element             => Seq(element)
          }
        }
        val txModel    = Model(Seq(TxMetaData(txElements)))
        conn.model2stmts(txModel).saveStmts
      } else Future(Nil)

      res <- {
        // Build raw function call for Datomic using untyped function
        val txFnInvocationClauses = list(
          list(s":$txFnDatomic" +: txMetaStmts +: args.map(_.asInstanceOf[AnyRef]): _*)
        )

        // Invoke transaction function and retrieve result
        conn.transact(txFnInvocationClauses)
      }
    } yield res
  } catch {
    case e: ExecutionExc => e.getMessage match {
      case msg if msg.startsWith("java.lang.NoClassDefFoundError: scala") =>
        Future.failed(TxFnException(excMissingScalaJar(e)))

      case msg if msg.startsWith("java.lang.NoClassDefFoundError: molecule") =>
        Future.failed(TxFnException(excMissingMoleculeClass(e)))

      case msg if msg.startsWith("java.lang.NoSuchMethodError: molecule") =>
        Future.failed(TxFnException(excMissingMoleculeMethod(e)))

      case _ =>
        Future.failed(TxFnException(e.getMessage.drop(redundant)))
    }

    case NonFatal(e) =>
      Future.failed(TxFnException(e.getMessage.drop(redundant)))
  }


  def inspectTxFnCall(
    txFn: String,
    txMolecules: Seq[Molecule],
    args: Any*
  )(implicit conn: Future[Conn], ec: ExecutionContext): Future[Unit] = {
    for{
      // Use temporary branch of db to not changing any live data
      _ <- conn.map(_.testDbWith())
      // Print tx report to console
      _ <- txFnCall(txFn, txMolecules, args: _*).map(_.inspect)
      // Go back to live db
      _ <- conn.map(_.useLiveDb())
    } yield ()
  }
}

