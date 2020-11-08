package molecule.coretests.transaction

import molecule.datomic.peer.api._
import molecule.ast.transactionModel.Statement
import molecule.coretests.util.CoreSpec
import molecule.coretests.util.dsl.coreTest._
import molecule.facade.Conn
import molecule.macros.TxFns
import molecule.macros.exception.TxFnException
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/* Example tx functions for tests
*
* To run tx functions, the transactor needs to have access to them. This is achieved
* by 2 means:
*
* 1. Annotating an object that contains some tx function(s) with `@TxFns`. This is a Scala
* annotation macro that creates a twin method for each tx function that satisfy the transactor:
*   a) `db` as its first parameter (used by the transactor to inject the db value)
*   b) All params as temporary params of type Object / AnyRef
*   c) Casting of temporary params to typed params according to original tx function.
*   d) Injection of zero or more tx meta data molecule statements to be saved with the transaction.
*      (tx meta data molecules are added when transacting with `transact(<txFn>, <txMetaData>*)`.
*
* 2. Preparing the transactor environment:
*
* LOCAL DEV / IN-MEMORY
* No need to prepare anything since transaction functions defined within the project
* will be available on the classpath for the Transactor managed by the Peer.
* OBS: Note that when running test from sbt
*
* STARTER PRO / PRO
* Set Datomic classpath variable to where your tx functions are before starting the transactor
* > export DATOMIC_EXT_CLASSPATH=/Users/mg/molecule/molecule/coretests/target/scala-2.13/test-classes/
* > bin/transactor ...
*
* FREE
* The Free version can't set the classpath variable so we need to provide the tx functions manually
* by making a jar of our classes, move it to the transactor lib folder and start the transactor:
* > cd ~/molecule/molecule/coretests/target/scala-2.13/test-classes  [path to your compiled classes]
* > jar cvf scala-fns.jar .
* > mv ~/molecule/molecule/coretests/target/scala-2.13/test-classes/scala-fns.jar DATOMIC_HOME/lib/
* > bin/transactor ...
* */
@TxFns
object txFns {

  // Example from Stu's presentation:
  // https://www.youtube.com/watch?v=8fY687k7DMA
  def inc(e: Long, amount: Int)(implicit conn: Conn): Seq[Seq[Statement]] = {
    // Current value is looked up in db
    val curBalance = Ns(e).int.get.headOption.getOrElse(0)
    val newBalance = curBalance + amount
    // Atomic update guaranteeing that the balance/pre-requisites haven't changed
    // between the balance check and saving the new value.
    Ns(e).int(newBalance).getUpdateTx
  }


  // Constraint check before multiple updates
  def transfer(from: Long, to: Long, amount: Int)(implicit conn: Conn): Seq[Seq[Statement]] = {

    // Validate sufficient funds in from-account
    val curFromBalance = Ns(from).int.get.headOption.getOrElse(0)

    if (curFromBalance < amount)
      // Throw exception to abort the whole transaction
      throw new TxFnException(s"Can't transfer $amount from account $from having a balance of only $curFromBalance.")

    // Calculate new balances
    val newFromBalance = curFromBalance - amount
    val newToBalance = Ns(to).int.get.headOption.getOrElse(0) + amount

    // Update accounts
    Ns(from).int(newFromBalance).getUpdateTx ++ Ns(to).int(newToBalance).getUpdateTx
  }


  // "Sub" tx fn - can be used on its own or in other tx functions
  def withdraw(from: Long, amount: Int)(implicit conn: Conn): Seq[Seq[Statement]] = {
    val curFromBalance = Ns(from).int.get.headOption.getOrElse(0)
    if (curFromBalance < amount)
      throw new TxFnException(s"Can't transfer $amount from account $from having a balance of only $curFromBalance.")

    val newFromBalance = curFromBalance - amount
    Ns(from).int(newFromBalance).getUpdateTx
  }


  // "Sub" tx fn - can be used on its own or in other tx functions
  def deposit(to: Long, amount: Int)(implicit conn: Conn): Seq[Seq[Statement]] = {
    val newToBalance = Ns(to).int.get.headOption.getOrElse(0) + amount
    Ns(to).int(newToBalance).getUpdateTx
  }


  // Compose tx function by calling other tx functions
  def transferComposed(from: Long, to: Long, amount: Int)(implicit conn: Conn): Seq[Seq[Statement]] = {
    // This tx function guarantees atomicity when calling multiple sub tx functions.
    // If they were called independently outside a tx function, atomicity wouldn't be guaranteed.
    withdraw(from, amount) ++ deposit(to, amount)
  }


  // Constructor enforcing validation rules against db
  def addUser(username: String, age: Int)(implicit conn: Conn): Seq[Seq[Statement]] = {
    // Checking that username hasn't been taken. Since this check runs inside
    // the transaction we are guaranteed Atomicity. The requested
    // username couldn't have been created between the check and saving.
    val userCheck = Ns.str(username).get
    if (userCheck.nonEmpty)
      throw new TxFnException(s"Username `$username` is already taken. Please choose another username.")

    // Constraint check not dependent on db call - this could be lifted out of the tx function to
    // save transactor workload. But the trade-off is that validation encapsulation is broken and
    // responsibility to check transfered to application code.
    if (age < 18)
      throw new TxFnException(s"Users have to be at least 18 years old to register.")

    // Save valid User
    Ns.str(username).int(age).getSaveTx
  }


  // Constructor enforcing validation rules against db
  def addUserPartiallyChecked(username: String, age: Int)(implicit conn: Conn): Seq[Seq[Statement]] = {
    // Checking that username hasn't been taken. Since this check runs inside
    // the transaction we are guaranteed Atomicity. The requested
    // username couldn't have been created between the check and saving.
    val userCheck = Ns.str(username).get
    if (userCheck.nonEmpty)
      throw new TxFnException(s"Username `$username` is already taken. Please choose another username.")

    // (Relying on age validation outside tx function)

    // Save valid User
    Ns.str(username).int(age).getSaveTx
  }
}


class TxFunctions extends CoreSpec {

  import txFns._

  "Synchronous / Asynchronous" in new CoreSetup {

    // Transaction functions can be invoked both synchronously and asynchronously
    // with `transact(...)` or transactAsync(...)`

    // Example from https://www.youtube.com/watch?v=8fY687k7DMA
    // Existing data
    val e = Ns.int(100).save.eid

    // Synchronous, blocking tx fn call
    transact(inc(e, 10))

    // Schema.is updated
    Ns.int.get.head === 110

    // Asynchronous, non-blocking tx fn call (uses Datomic's asynchronous api)
    Await.result(
      transactAsync(inc(e, 15)) map { txReport =>
        Ns.int.get.head === 125
      },
      2.seconds
    )

    // All examples can be synchronous/asynchronous. For brevity,
    // sync calls are used in the following tests
  }


  "Atomic constraints" in new CoreSetup {

    val fromAccount  = Ns.int(100).save.eid
    val toAccount    = Ns.int(700).save.eid
    val tooBigAmount = 200

    (transact(transfer(fromAccount, toAccount, tooBigAmount)) must throwA[TxFnException])
      .message === s"Got the exception molecule.macros.exception.TxFnException: " +
      s"Can't transfer 200 from account $fromAccount having a balance of only 100."

    // Live data unchanged
    Ns(fromAccount).int.get.head === 100
    Ns(toAccount).int.get.head === 700

    val okAmount = 20
    // Atomicity is guaranteed:
    // Balance check and saving of new values within same transaction boundary
    transact(transfer(fromAccount, toAccount, okAmount))

    // Live data changed
    Ns(fromAccount).int.get.head === 80
    Ns(toAccount).int.get.head === 720
  }


  "Composing multiple tx functions" in new CoreSetup {

    // (identical effect as in previous test)

    val fromAccount  = Ns.int(100).save.eid
    val toAccount    = Ns.int(700).save.eid
    val tooBigAmount = 200

    (transact(transferComposed(fromAccount, toAccount, tooBigAmount)) must throwA[TxFnException])
      .message === s"Got the exception molecule.macros.exception.TxFnException: " +
      s"Can't transfer 200 from account $fromAccount having a balance of only 100."

    // Live data unchanged
    Ns(fromAccount).int.get.head === 100
    Ns(toAccount).int.get.head === 700

    val okAmount = 20
    // `transferComposed` calls two sub tx functions and still guarantees atomicity
    transact(transferComposed(fromAccount, toAccount, okAmount))

    // Live data changed
    Ns(fromAccount).int.get.head === 80
    Ns(toAccount).int.get.head === 720
  }

  /*
  Tthe two following tests fail when running in sbt and succedd when running in IDE

  [error]  org.codehaus.commons.compiler.CompileException:
  File org.codehaus.commons.compiler.jdk.SimpleCompiler$1[simplecompiler], Line 1, Column 0:
  package datomic does not exist (compiler.err.doesnt.exist) (TxMethods.scala:320)

  `datomic` doesn't seem to be on the classpath and might be related some of those issues:


   */

  "Tx fn + 1 tx meta data molecule (OBS: fails with sbt, but succeeds with IDE!)" in new CoreSetup {

    val fromAccount = Ns.int(100).save.eid
    val toAccount   = Ns.int(700).save.eid
    val amount      = 20

    // Add 1 tx meta data molecule to tx function transaction
    transact(transfer(fromAccount, toAccount, amount), Ref2.str2("Tx meta data..."))

    // Live data changed and tx meta data added
    // Both account entities share the same transaction meta data
    Ns(fromAccount).int.Tx(Ref2.str2).get.head === (80, "Tx meta data...")
    Ns(toAccount).int.Tx(Ref2.str2).get.head === (720, "Tx meta data...")
  }


  "Tx fn + 2 tx meta data molecules (OBS: fails with sbt, but succeeds with IDE!)" in new CoreSetup {

    val fromAccount = Ns.int(100).save.eid
    val toAccount   = Ns.int(700).save.eid
    val amount      = 20

    // Add 2 tx meta data molecules to tx function transaction
    transact(transfer(fromAccount, toAccount, amount), Ref2.str2("Tx meta data..."), Ref1.int1(12345))

    // Live data changed
    Ns(fromAccount).int.get.head === 80
    Ns(toAccount).int.get.head === 720

    // Both account entities share the same transaction meta data
    Ns(fromAccount).int.Tx(Ref2.str2).Tx(Ref1.int1).get.head === (80, "Tx meta data...", 12345)
    Ns(toAccount).int.Tx(Ref2.str2).Tx(Ref1.int1).get.head === (720, "Tx meta data...", 12345)

    // Partial tx meta data queries
    Ns(fromAccount).int.Tx(Ref2.str2).get.head === (80, "Tx meta data...")
    Ns(toAccount).int.Tx(Ref1.int1).get.head === (720, 12345)
  }


  "Constructor" in new CoreSetup {

    // Use tx function as a data constructor enforcing some integrity checks

    // Existing user
    Ns.str("Ben").int(28).save

    (transact(addUser("Ben", 22)) must throwA[TxFnException])
      .message === "Got the exception molecule.macros.exception.TxFnException: " +
      "Username `Ben` is already taken. Please choose another username."

    // Age validation in tx fn
    (transact(addUser("Liz", 17)) must throwA[TxFnException])
      .message === "Got the exception molecule.macros.exception.TxFnException: " +
      "Users have to be at least 18 years old to register."

    // Successful User construction with all validation rules satisfied
    // Construction is atomic - only a fully valid User can be saved.
    transact(addUser("Ann", 28))
    Ns.str("Ann").int(28).get.size === 1
  }


  "Constructor with partial validation" in new CoreSetup {

    // Use tx function as a data constructor enforcing some integrity checks inside the
    // tx fun and some validation outside the tx function.

    // Existing user
    Ns.str("Ben").int(28).save

    (transact(addUserPartiallyChecked("Ben", 22)) must throwA[TxFnException])
      .message === "Got the exception molecule.macros.exception.TxFnException: " +
      "Username `Ben` is already taken. Please choose another username."

    // Age validation outside tx fn
    // - saves transactor workload
    // - gives up on encapsulating validation atomically
    // An unvalid User could be saved if application code doesn't check the age for instance:
    transact(addUserPartiallyChecked("Liz", 17))

    // Under-age Liz is wrongly saved
    Ns.str("Liz").int(17).get.size === 1

    // If age is validated in application code, we can of course construct a valid User
    transact(addUserPartiallyChecked("Ann", 28))
    Ns.str("Ann").int(28).get.size === 1

    // If validation not dependent on db lookup is taken care of in application code, it can
    // save workload on the transactor to move it outside tx functions.
  }


  "Debugging tx fn call" in new CoreSetup {

    val e = Ns.int(100).save.eid

    // Print potential result of tx fn call to `with` branch of current db without affecting live db.
    debugTransact(inc(e, 10))
    // 1st group: Empty list of additional statements since only the tx fn was called.
    //            If calling tx fn with molecule having attributes, the attribute statements will show here
    // 2nd group: tx timestamp
    //            Addition of new value 110
    //            Retraction of old value 100
    /*
      ## 1 ## TxReport
      ========================================================================
      1          List(
      )
      ------------------------------------------------
      2          List(
        1    1     added: true ,   t: 13194139534344,   e: 13194139534344,   a: 50,   v: Wed Nov 07 10:48:56 CET 2018

        2    2     added: true ,   t: 13194139534344,   e: 17592186045445,   a: 64,   v: 110
             3     added: false,  -t: 13194139534344,  -e: 17592186045445,  -a: 64,  -v: 100)
      ========================================================================
    */

    // Live data unchanged
    Ns.int.get.head === 100


    // Invoke tx function
    val txReport = transact(inc(e, 10))

    // Print debug info from result of tx fn execution (will show the same as `debugTransact(...)`
    txReport.debug

    // Live data has been changed
    Ns.int.get.head === 110
  }
}
