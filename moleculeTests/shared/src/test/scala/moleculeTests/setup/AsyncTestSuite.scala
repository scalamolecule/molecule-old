package moleculeTests.setup

import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{System, SystemPeer}
import moleculeTests.setup.core.CoreData
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try


trait AsyncTestSuite extends TestSuite with CoreData
  // Platform-specific JS/JVM implementations
  with AsyncTestSuiteImpl {

  var system      : System  = SystemPeer
  val isJsPlatform: Boolean = isJsPlatform_


  def core[T](test: Future[Conn] => T): T = coreImpl(test)
  def coreTxFn[T](test: Future[Conn] => T): T = coreTxFnImpl(test)
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
  def mbrainz[T](test: Future[Conn] => T): T = mbrainzImpl(test)


  // create delays between transactions to allow dates to be separate by at least 1 ms
  def delay = (1 to 10000).sum

}
