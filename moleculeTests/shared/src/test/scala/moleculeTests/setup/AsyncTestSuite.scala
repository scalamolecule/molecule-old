package moleculeTests.setup

import java.util.concurrent.atomic.AtomicLong
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{System, SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.core.CoreData
import utest._
import utest.framework.Formatter
import scala.concurrent.{ExecutionContext, Future}


trait AsyncTestSuite extends TestSuite with CoreData
  // Platform-specific implementations (JS/JVM)
  with AsyncTestSuiteImpl {

  //  val system      : System  = SystemSelection.system
  val system      : System  = {
//        SystemPeer
    SystemDevLocal
    //    SystemPeerServer
  }
  val isJsPlatform: Boolean = isJsPlatform_

  val platformSystem = {
    (if (isJsPlatform) "JS" else "JVM") + (system match {
      case SystemPeer       => " Peer"
      case SystemDevLocal   => " DevLocal"
      case SystemPeerServer => " PeerServer"
    })
  }

  override def utestFormatter: Formatter = new Formatter {
    override def formatIcon(success: Boolean): ufansi.Str = {
      formatResultColor(success)(if (success) s"+ $platformSystem" else s"X $platformSystem")
    }
  }

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
  def delay = (1 to 10000).sum
}
