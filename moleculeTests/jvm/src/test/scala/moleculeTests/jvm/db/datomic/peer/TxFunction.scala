package moleculeTests.jvm.db.datomic.peer

import molecule.core.exceptions.TxFnException
import molecule.core.macros.TxFns
import molecule.datomic.api.out3._
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.Conn
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.{ExecutionContext, Future}


/** Example tx functions for tests
  *
  * A transaction function allows its logic and database calls to be performed withing a single
  * transaction to guarantee transactional atomicity.
  *
  * Transaction functions are available for Peers on the jvm side only.
  *
  * To run tx functions, the transactor needs to have access to them. This is achieved
  * by 2 means:
  *
  * 1. Annotating an object that contains some tx function(s) with `@TxFns`. This is a Scala
  * annotation macro that creates a twin method for each tx function that satisfy the transactor:
  * a) `db` as its first parameter (used by the transactor to inject the db value)
  * b) All params as temporary params of type Object / AnyRef
  * c) Casting of temporary params to typed params according to original tx function.
  * d) Injection of zero or more tx meta data molecule statements to be saved with the transaction.
  * (tx meta data molecules are added when transacting with `transact(<txFn>, <txMetaData>*)`.
  *
  * 2. Preparing the transactor environment:
  *
  * LOCAL DEV / IN-MEMORY
  * No need to prepare anything since transaction functions defined within the project
  * will be available on the classpath for the Transactor managed by the Peer.
  *
  * STARTER PRO / PRO
  * Set Datomic classpath variable to where your tx functions are before starting the transactor
  * > export DATOMIC_EXT_CLASSPATH=/Users/mg/molecule/molecule/coretests/target/scala-2.13/test-classes/
  * > bin/transactor ...
  *
  * FREE
  * The Free version can't set the classpath variable so we need to provide the tx functions manually
  * by making a jar of our classes, move it to the transactor lib folder and start the transactor:
  * > cd <project-path>/molecule-tests/target/scala-2.13/test-classes  [path to your compiled classes]
  * > jar cvf scala-fns.jar .
  * > mv <project-path>/molecule-tests/target/scala-2.13/test-classes/scala-fns.jar DATOMIC_HOME/lib/
  * > bin/transactor ...
  * */
@TxFns
object TxFunctionExamples {

  // Example from Stu's presentation:
  // https://www.youtube.com/watch?v=8fY687k7DMA
  def inc(e: Long, amount: Int)
         (implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = {
    for {
      // Current value is looked up in db
      cur <- Ns(e).int.get
      curBalance = cur.headOption.getOrElse(0)
      newBalance = curBalance + amount

      // Atomic update guaranteeing that the balance/pre-requisites haven't changed
      // between the curBalance check and saving the new value.
      stmts <- Ns(e).int(newBalance).getUpdateStmts
    } yield stmts
  }


  // Constraint check before multiple updates
  def transfer(from: Long, to: Long, amount: Int)
              (implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = {
    for {
      // Validate sufficient funds in from-account
      curFromBalance <- Ns(from).int.get.map(_.headOption.getOrElse(0))

      _ = if (curFromBalance < amount)
      // Throw exception to abort the whole transaction
        throw TxFnException(
          s"Can't transfer $amount from account $from having a balance of only $curFromBalance."
        )

      // Calculate new balances
      newFromBalance = curFromBalance - amount
      newToBalance <- Ns(to).int.get.map(_.headOption.getOrElse(0) + amount)

      // Update accounts
      newFromStmts <- Ns(from).int(newFromBalance).getUpdateStmts
      newToStmts <- Ns(to).int(newToBalance).getUpdateStmts
    } yield newFromStmts ++ newToStmts
  }


  // "Sub" tx fn - can be used on its own or in other tx functions
  def withdraw(from: Long, amount: Int)
              (implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = {
    for {
      curFromBalance <- Ns(from).int.get.map(_.headOption.getOrElse(0))

      _ = if (curFromBalance < amount)
        throw TxFnException(
          s"Can't transfer $amount from account $from having a balance of only $curFromBalance.")

      newFromBalance = curFromBalance - amount
      stmts <- Ns(from).int(newFromBalance).getUpdateStmts
    } yield stmts
  }


  // "Sub" tx fn - can be used on its own or in other tx functions
  def deposit(to: Long, amount: Int)
             (implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = {
    for {
      newToBalance <- Ns(to).int.get.map(_.headOption.getOrElse(0) + amount)
      stmts <- Ns(to).int(newToBalance).getUpdateStmts
    } yield stmts
  }


  // Compose tx function by calling other tx functions
  def transferComposed(from: Long, to: Long, amount: Int)
                      (implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = {
    for {
      // This tx function guarantees atomicity when calling multiple sub tx functions.
      // If they were called independently outside a tx function, atomicity wouldn't be guaranteed.
      withdrawStmts <- withdraw(from, amount)
      depositStmts <- deposit(to, amount)
    } yield withdrawStmts ++ depositStmts
  }


  // Constructor enforcing validation rules against db
  def addUser(username: String, age: Int)
             (implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = {
    for {
      // Checking that username hasn't been taken. Since this check runs inside
      // the transaction we are guaranteed Atomicity. The requested
      // username couldn't have been created between the check and saving.
      userCheck <- Ns.str(username).get

      _ = if (userCheck.nonEmpty)
        throw TxFnException(s"Username `$username` is already taken. Please choose another username.")

      // Constraint check not dependent on db call - this could be lifted out of the tx function to
      // save transactor workload. But the trade-off is that validation encapsulation is broken and
      // responsibility to check transfered to application code.
      _ = if (age < 18)
        throw TxFnException(s"Users have to be at least 18 years old to register.")

      // Save valid User
      stmts <- Ns.str(username).int(age).getSaveStmts
    } yield stmts
  }


  // Constructor enforcing validation rules against db
  def addUserPartiallyChecked(username: String, age: Int)
                             (implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = {
    for {
      // Checking that username hasn't been taken. Since this check runs inside
      // the transaction we are guaranteed Atomicity. The requested
      // username couldn't have been created between the check and saving.
      userCheck <- Ns.str(username).get

      _ = if (userCheck.nonEmpty)
        throw TxFnException(s"Username `$username` is already taken. Please choose another username.")

      // (Relying on age validation outside tx function)

      // Save valid User
      stmts <- Ns.str(username).int(age).getSaveStmts
    } yield stmts
  }
}


object TxFunction extends AsyncTestSuite {

  import TxFunctionExamples._

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "Basic inc example" - corePeerOnly { implicit conn =>
      // Example from https://www.youtube.com/watch?v=8fY687k7DMA
      for {
        // Existing data
        eid <- Ns.int(100).save.map(_.eid)

        // Transaction function call
        _ <- transactFn(inc(eid, 10))

        // Value is updated
        _ <- Ns.int.get.map(_.head ==> 110)
      } yield ()
    }


    "Atomic constraints" - corePeerOnly { implicit conn =>
      for {
        fromAccount <- Ns.int(100).save.map(_.eid)
        toAccount <- Ns.int(700).save.map(_.eid)
        tooBigAmount = 200

        _ <- transactFn(transfer(fromAccount, toAccount, tooBigAmount))
          .map(_ ==> "Unexpected success").recover { case TxFnException(msg) =>
          msg ==> s"Can't transfer 200 from account $fromAccount having a balance of only 100."
        }

        // Live data unchanged
        _ <- Ns(fromAccount).int.get.map(_.head ==> 100)
        _ <- Ns(toAccount).int.get.map(_.head ==> 700)

        okAmount = 20

        // Atomicity is guaranteed:
        // Balance check and saving of new values within same transaction boundary
        _ <- transactFn(transfer(fromAccount, toAccount, okAmount))

        // Live data changed
        _ <- Ns(fromAccount).int.get.map(_.head ==> 80)
        _ <- Ns(toAccount).int.get.map(_.head ==> 720)
      } yield ()
    }


    "Composing multiple tx functions" - corePeerOnly { implicit conn =>
      for {
        // (identical effect as in previous test)

        fromAccount <- Ns.int(100).save.map(_.eid)
        toAccount <- Ns.int(700).save.map(_.eid)
        tooBigAmount = 200

        _ <- transactFn(transferComposed(fromAccount, toAccount, tooBigAmount))
          .map(_ ==> "Unexpected success").recover { case TxFnException(msg) =>
          msg ==> s"Can't transfer 200 from account $fromAccount having a balance of only 100."
        }

        // Live data unchanged
        _ <- Ns(fromAccount).int.get.map(_.head ==> 100)
        _ <- Ns(toAccount).int.get.map(_.head ==> 700)

        okAmount = 20
        // `transferComposed` calls two sub tx functions and still guarantees atomicity
        _ <- transactFn(transferComposed(fromAccount, toAccount, okAmount))

        // Live data changed
        _ <- Ns(fromAccount).int.get.map(_.head ==> 80)
        _ <- Ns(toAccount).int.get.map(_.head ==> 720)
      } yield ()
    }


    "Tx fn + 1 tx meta data molecule" - corePeerOnly { implicit conn =>
      for {
        fromAccount <- Ns.int(100).save.map(_.eid)
        toAccount <- Ns.int(700).save.map(_.eid)
        amount = 20

        // Add 1 tx meta data molecule to tx function transaction
        _ <- transactFn(transfer(fromAccount, toAccount, amount), Ref2.str2("Tx meta data..."))

        // Live data changed and tx meta data added
        // Both account entities share the same transaction meta data
        _ <- Ns(fromAccount).int.Tx(Ref2.str2).get.map(_.head ==> (80, "Tx meta data..."))
        _ <- Ns(toAccount).int.Tx(Ref2.str2).get.map(_.head ==> (720, "Tx meta data..."))
      } yield ()
    }


    "Tx fn + 2 tx meta data molecules" - corePeerOnly { implicit conn =>
      for {
        fromAccount <- Ns.int(100).save.map(_.eid)
        toAccount <- Ns.int(700).save.map(_.eid)
        amount = 20

        // Add 2 tx meta data molecules to tx function transaction
        _ <- transactFn(
          transfer(fromAccount, toAccount, amount),
          Ref2.str2("Tx meta data..."),
          Ref1.int1(12345)
        )

        // Live data changed
        _ <- Ns(fromAccount).int.get.map(_.head ==> 80)
        _ <- Ns(toAccount).int.get.map(_.head ==> 720)

        // Both account entities share the same transaction meta data
        _ <- Ns(fromAccount).int.Tx(Ref2.str2 + Ref1.int1).get.map(_.head ==> (80, "Tx meta data...", 12345))
        _ <- Ns(toAccount).int.Tx(Ref2.str2 + Ref1.int1).get.map(_.head ==> (720, "Tx meta data...", 12345))

        // Partial tx meta data queries
        _ <- Ns(fromAccount).int.Tx(Ref2.str2).get.map(_.head ==> (80, "Tx meta data..."))
        _ <- Ns(toAccount).int.Tx(Ref1.int1).get.map(_.head ==> (720, 12345))
      } yield ()
    }


    "Constructor" - corePeerOnly { implicit conn =>
      for {
        // Use tx function as a data constructor enforcing some integrity checks

        // Existing user
        _ <- Ns.str("Ben").int(28).save

        // User existence validation in transaction
        _ <- transactFn(addUser("Ben", 22))
          .map(_ ==> "Unexpected success").recover { case TxFnException(msg) =>
          msg ==> "Username `Ben` is already taken. Please choose another username."
        }

        // Age validation in tx fn
        _ <- transactFn(addUser("Liz", 17))
          .map(_ ==> "Unexpected success").recover { case TxFnException(msg) =>
          msg ==> "Users have to be at least 18 years old to register."
        }

        // Successful User construction with all validation rules satisfied
        // Construction is atomic - only a fully valid User can be saved.
        _ <- transactFn(addUser("Ann", 28))
        _ <- Ns.str("Ann").int(28).get.map(_.size ==> 1)
      } yield ()
    }


    "Constructor with partial validation" - corePeerOnly { implicit conn =>
      for {
        // Use tx function as a data constructor enforcing some integrity checks inside the
        // tx fun and some validation outside the tx function.

        // Existing user
        _ <- Ns.str("Ben").int(28).save

        _ <- transactFn(addUserPartiallyChecked("Ben", 22))
          .map(_ ==> "Unexpected success").recover { case TxFnException(msg) =>
          msg ==> "Username `Ben` is already taken. Please choose another username."
        }

        // Age validation outside tx fn
        // - saves transactor workload
        // - gives up on encapsulating validation atomically
        // An unvalid User could be saved if application code doesn't check the age for instance:
        _ <- transactFn(addUserPartiallyChecked("Liz", 17))

        // Under-age Liz is wrongly saved
        _ <- Ns.str("Liz").int(17).get.map(_.size ==> 1)

        // If age is validated in application code, we can of course construct a valid User
        _ <- transactFn(addUserPartiallyChecked("Ann", 28))
        _ <- Ns.str("Ann").int(28).get.map(_.size ==> 1)

        // If validation not dependent on db lookup is taken care of in application code, it can
        // save workload on the transactor to move it outside tx functions.
      } yield ()
    }


    "Inspecting tx fn call" - corePeerOnly { implicit conn =>
      for {
        eid <- Ns.int(100).save.map(_.eid)

        // Print potential result of tx fn call to `with` branch of current db without affecting live db.
        _ <- inspectTransactFn(inc(eid, 10))
        // 1st group: Empty list of additional statements since only the tx fn was called.
        //            If calling tx fn with molecule having attributes, the attribute statements will show here
        // 2nd group: tx timestamp
        //            Addition of new value 110
        //            Retraction of old value 100
        /*
          ## 1 ## TxReport
          ================================================================================================================
          List()
          ----------------------------------------------------------------------------------------------------------------
          List(
            1    1     added: true ,   t: 13194139534352,   e: 13194139534352,   a: 50,   v: Tue Jun 01 21:29:47 CEST 2021,

            2    2     added: true ,   t: 13194139534352,   e: 17592186045453,   a: 73,   v: 110,
                 3     added: false,  -t: 13194139534352,  -e: 17592186045453,  -a: 73,  -v: 100)
          ================================================================================================================
        */

        // Live data unchanged
        _ <- Ns.int.get.map(_.head ==> 100)

        // Invoke tx function
        txReport <- transactFn(inc(eid, 10))

        // Print inspect info from result of tx fn execution (will show the same as `inspectTransact(...)`
        _ = txReport.inspect

        // Live data has been changed
        _ <- Ns.int.get.map(_.head ==> 110)
      } yield ()
    }
  }
}