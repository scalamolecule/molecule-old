package molecule.core.api

import java.util.concurrent.{ExecutionException => ExecutionExc}
import molecule.core.ast.elements.{Composite, Model, TxMetaData}
import molecule.core.exceptions.{MoleculeException, TxFnException}
import molecule.core.macros.ResolveTxFnCall
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

  /** Invoke tx function
   * <br><br>
   * Macro that takes a tx function invocation itself as its argument. The tx function is analyzed
   * by the macro and the necessary transaction preparations done at compile time.
   * <br><br>
   * At runtime, the returned statements from the tx function transacted as
   * one atomic transaction using Datomic's asynchronous API.
   * {{{
   * for {
   *   // Transact transfer
   *   _ <- transactFn(
   *     transfer(fromAccount, toAccount, 20)
   *   )
   *
   *   // Amount has been transferred safely
   *   _ <- Account(fromAccount).balance.get.map(_.head ==> 80)
   *   _ <- Account(toAccount).balance.get.map(_.head ==> 720)
   * } yield ()
   * }}}
   * Additional transaction meta data can be added
   * {{{
   * for {
   *   _ <- transactFn(
   *     transfer(fromAccount, toAccount, 20),
   *
   *     // Tx meta data that John made the transfer
   *     Person.name("John"),
   *
   *     // Tx meta data that the transfer was part of use case "Scheduled transfer)
   *     UseCase.name("Scheduled transfer")
   *   )
   *
   *   _ <- Account(fromAccount).balance
   *     .Tx(Person.name_("John") + UseCase.name_("Scheduled transfer"))
   *     .get.map(_.head ==> 80)
   * } yield ()
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
  )(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = macro ResolveTxFnCall.resolveTxFnCall

  /** Inspect tx function invocation
   * <br><br>
   * Print transaction statements to output of a tx function invocation
   * without affecting the live database.
   * {{{
   * for {
   *   // Print inspect info for tx function invocation
   *   _ <- inspectTransact(transfer(fromAccount, toAccount, 20))
   * } yield ()
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
  )(implicit conn: Future[Conn], ec: ExecutionContext): Future[Unit] = macro ResolveTxFnCall.resolveInspectTxFnCall
}


object TxFunctions extends Helpers with JavaUtil {

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


  /** Invoke transaction function call */
  def txFnCall(
    classpathTxFn: String,
    txMolecules: Seq[Molecule],
    args: Any*
  )(implicit futConn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    (for {
      conn <- futConn

      // Install database function in db that invokes the classpath tx function
      _ <- conn.transact(conn.buildTxFnInvoker(classpathTxFn, args))

      txMetaStmts <- if (txMolecules.nonEmpty) {
        val txElements = txMolecules.flatMap { mol =>
          mol._model.elements.flatMap {
            case Composite(elements) => elements
            case element             => Seq(element)
          }
        }
        val txModel    = Model(Seq(TxMetaData(txElements)))
        conn.model2stmts(txModel).saveStmts.map(stmts => conn.stmts2java(stmts))
      } else Future(javaList())

      // Invoke transaction function to retrieve result
      res <- conn.transact(javaList(
        javaList(s":${classpathTxFn}_invoker" +: txMetaStmts +: args.map(_.asInstanceOf[AnyRef]): _*)
      ))
    } yield res).recoverWith {
      case e@MoleculeException(msg, _) => msg match {
        case msg if msg.startsWith("scala") =>
          Future.failed(TxFnException(excMissingScalaJar(e)))

        case msg if msg.startsWith("java.lang.NoClassDefFoundError: molecule") =>
          Future.failed(TxFnException(excMissingMoleculeClass(e)))

        case msg if msg.startsWith("java.lang.NoSuchMethodError: molecule") =>
          Future.failed(TxFnException(excMissingMoleculeMethod(e)))

        case _ => Future.failed(TxFnException(e.getMessage))
      }

      // Wrap other exceptions in a TxFnException to enable pattern matching
      case NonFatal(e) => Future.failed(TxFnException(e.getMessage))
    }
  }


  def inspectTxFnCall(
    txFn: String,
    txMolecules: Seq[Molecule],
    args: Any*
  )(implicit conn: Future[Conn], ec: ExecutionContext): Future[Unit] = {
    for {
      // Use temporary branch of db to not changing any live data
      _ <- conn.map(_.testDbWith())
      // Print tx report to console
      _ <- txFnCall(txFn, txMolecules, args: _*).map(_.inspect)
      // Go back to live db
      _ <- conn.map(_.useLiveDb())
    } yield ()
  }
}

