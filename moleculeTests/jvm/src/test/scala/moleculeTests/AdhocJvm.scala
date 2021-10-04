package moleculeTests

import java.util
import molecule.datomic.api.in3_out12._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.util.{Collections, Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.ast.elements._
import molecule.core.dsl.base.Init
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.rowAttr.{CastOptNested, CastTypes, JsonBase}
import molecule.datomic.base.marshalling._
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import moleculeTests.dataModels.core.base.schema.CoreTestSchema
import scala.util.control.NonFatal
import molecule.core.marshalling.nodes._
import molecule.core.marshalling.unpackAttr.String2cast
import molecule.core.marshalling.unpackers.Packed2EntityMap
import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.base.marshalling.packers.PackEntityMap
import moleculeTests.tests.core.generic.Datom.delay


object AdhocJvm extends AsyncTestSuite with Helpers
  with String2cast with CastTypes with CastOptNested with JsonBase
  with PackEntityMap {


  lazy val tests = Tests {

    "adhocJvm" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn



      } yield ()
    }



    /*
moleculeTests.tests.core.generic.Datom
moleculeTests.jvm.core.transaction.TxFunction
moleculeTests.tests.core.time.TestDbWith

object TxFunctionExamples extends scala.AnyRef {
  def <init>() = {
    super.<init>();
    ()
  };
  def inc(e: Long, amount: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = Ns(e).int.get.map(((cur) => {
  val curBalance = cur.headOption.getOrElse(0);
  val newBalance = curBalance.$plus(amount);
  scala.Tuple3(cur, curBalance, newBalance)
})).flatMap(((x$1) => x$1: @scala.unchecked match {
    case scala.Tuple3((cur @ _), (curBalance @ _), (newBalance @ _)) => Ns(e).int(newBalance).getUpdateStmts.map(((stmts) => stmts))
  }));
  def inc__txFnDatomic(txDb: AnyRef, txMetaStmtsRaw: AnyRef, e0: AnyRef, amount0: AnyRef): AnyRef = {
    import molecule.datomic.peer.facade.Conn_Peer;
    import molecule.datomic.base.ast.transactionModel.Statement;
    import scala.concurrent.duration.DurationInt;
    import scala.concurrent.ExecutionContext.Implicits.global;
    import scala.concurrent.{Await, Future};
    val conn: Conn = Conn_Peer(txDb);
    implicit val futConn: Future[Conn] = Future(conn);
    val e: Long = e0.asInstanceOf[Long];
    val amount: Int = amount0.asInstanceOf[Int];
    val futTxFnStmts: Future[Seq[Statement]] = Ns(e).int.get.map(((cur) => {
  val curBalance = cur.headOption.getOrElse(0);
  val newBalance = curBalance.$plus(amount);
  scala.Tuple3(cur, curBalance, newBalance)
})).flatMap(((x$1) => x$1: @scala.unchecked match {
      case scala.Tuple3((cur @ _), (curBalance @ _), (newBalance @ _)) => Ns(e).int(newBalance).getUpdateStmts.map(((stmts) => stmts))
    }));
    val txFnStmts = Await.result(futTxFnStmts, 1.minute);
    val txMetaStmts = txMetaStmtsRaw.asInstanceOf[Seq[Statement]];
    conn.stmts2java(txFnStmts.$plus$plus(txMetaStmts))
  }
}
======================================================
@TxFns


object TxFunctionExamples extends scala.AnyRef {
  def <init>() = {
    super.<init>();
    ()
  };
  def inc(e: Long, amount: Int)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = Ns(e).int.get.map(((cur) => {
  val curBalance = cur.headOption.getOrElse(0);
  val newBalance = curBalance.$plus(amount);
  scala.Tuple3(cur, curBalance, newBalance)
})).flatMap(((x$1) => x$1: @scala.unchecked match {
    case scala.Tuple3((cur @ _), (curBalance @ _), (newBalance @ _)) => Ns(e).int(newBalance).getUpdateStmts.map(((stmts) => stmts))
  }));
  def inc__txFnDatomic(txDb: AnyRef, txMetaStmtsRaw: AnyRef, e0: AnyRef, amount0: AnyRef): AnyRef = {
    import molecule.datomic.peer.facade.Conn_Peer;
    import molecule.datomic.base.ast.transactionModel.Statement;
    import scala.concurrent.duration.DurationInt;
    import scala.concurrent.ExecutionContext.Implicits.global;
    import scala.concurrent.{Await, Future};
    val conn: Conn = Conn_Peer(txDb);
    implicit val futConn: Future[Conn] = Future(conn);
    val e: Long = e0.asInstanceOf[Long];
    val amount: Int = amount0.asInstanceOf[Int];
    val futTxFnStmts: Future[Seq[Statement]] = Ns(e).int.get.map(((cur) => {
  val curBalance = cur.headOption.getOrElse(0);
  val newBalance = curBalance.$plus(amount);
  scala.Tuple3(cur, curBalance, newBalance)
})).flatMap(((x$1) => x$1: @scala.unchecked match {
      case scala.Tuple3((cur @ _), (curBalance @ _), (newBalance @ _)) => Ns(e).int(newBalance).getUpdateStmts.map(((stmts) => stmts))
    }));
    val txFnStmts = Await.result(futTxFnStmts, 1.minute);
    val txMetaStmts = txMetaStmtsRaw.asInstanceOf[Seq[Statement]];
    conn.stmts2java(txFnStmts.$plus$plus(txMetaStmts))
  }
}
======================================================
@TxFns

     */


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
