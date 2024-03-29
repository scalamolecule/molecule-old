package moleculeTests.setup

import molecule.core.data.SchemaTransaction
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.datomic.base.util.{System, SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.core.CoreData
import utest._
import utest.framework.Formatter
import scala.concurrent.Future
import molecule.core.util.Executor._

trait AsyncTestSuite extends TestSuite with CoreData
  // Platform-specific implementations (JS/JVM)
  with AsyncTestSuiteImpl {

  lazy val isJsPlatform: Boolean = isJsPlatform_
  lazy val protocol    : String  = protocol_
  lazy val useFree     : Boolean = useFree_

  lazy val system: System = {
    SystemPeer
    //        SystemDevLocal

    // Since we run asynchronous tests and can't recreate databases against the Peer Server,
    // we can only test reliably by restarting the Peer Server and test a single test at a time.
    //    SystemPeerServer
  }

  lazy val platformSystemProtocol = {
    val dbType = if (protocol == "mem") if (useFree) "(free)" else "(pro)" else ""
    (if (isJsPlatform) "JS" else "JVM") +
      (system match {
        case SystemPeer       => s" Peer $protocol $dbType"
        case SystemDevLocal   => s" DevLocal $protocol"
        case SystemPeerServer => s" PeerServer $protocol"
      })
  }

  override def utestFormatter: Formatter = new Formatter {
    override def formatIcon(success: Boolean): ufansi.Str = {
      formatResultColor(success)(
        (if (success) "+ " else "X ") + platformSystemProtocol
      )
    }
  }

  def empty[T](test: Future[Conn] => T): T = emptyImpl(test)
  def core[T](test: Future[Conn] => T): T = coreImpl(test)
  def corePeerOnly[T](test: Future[Conn] => T): T = corePeerOnlyImpl(test)
  def bidirectional[T](test: Future[Conn] => T): T = bidirectionalImpl(test)
  def partition[T](test: Future[Conn] => T): T = partitionImpl(test)
  def nested[T](test: Future[Conn] => T): T = nestedImpl(test)
  def selfJoin[T](test: Future[Conn] => T): T = selfJoinImpl(test)
  def aggregate[T](test: Future[Conn] => T): T = aggregateImpl(test)
  def socialNews[T](test: Future[Conn] => T): T = socialNewsImpl(test)
  def graph[T](test: Future[Conn] => T): T = graphImpl(test)
  def graph2[T](test: Future[Conn] => T): T = graph2Impl(test)
  def modernGraph1[T](test: Future[Conn] => T): T = modernGraph1Impl(test)
  def modernGraph2[T](test: Future[Conn] => T): T = modernGraph2Impl(test)
  def products[T](test: Future[Conn] => T): T = productsImpl(test)
  def seattle[T](test: Future[Conn] => T): T = seattleImpl(test)
  def mbrainz[T](test: Future[Conn] => Future[T]): Future[T] = mbrainzImpl(test)


  // At least 1 ms delay between transactions involving Dates to avoid overlapping
  // (can't use Thread.sleep(1000) on js platform)
  def delay = (1 to 50000).sum


  // Testing schema definitions
  def transact(schema: SchemaTransaction)(implicit futConn: Future[Conn]): Future[Seq[TxReport]] = {
    for {
      conn <- futConn
      _ = conn.updateConnProxy(schema)
      txrs <- system match {
        case SystemPeer       => Future.sequence(schema.datomicPeer.map(edn => conn.transact(edn)))
        case SystemDevLocal   => Future.sequence(schema.datomicClient.map(edn => conn.transact(edn)))
        case SystemPeerServer => Future.sequence(schema.datomicClient.map(edn => conn.transact(edn)))
      }
    } yield txrs
  }
}
