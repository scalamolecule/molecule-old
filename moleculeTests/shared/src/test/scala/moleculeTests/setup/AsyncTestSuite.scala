package moleculeTests.setup

import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{System, SystemPeer}
import moleculeTests.setup.core.CoreData
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait AsyncTestSuite extends TestSuite with CoreData
  // Platform-specific JS/JVM implementations
  with AsyncTestSuiteImpl {

  var system: System = SystemPeer

  def core[T](func: Future[Conn] => T): T = coreImpl(func)
  def bidirectional[T](func: Future[Conn] => T): T = bidirectionalImpl(func)
  def partition[T](func: Future[Conn] => T): T = partitionImpl(func)
  def nested[T](func: Future[Conn] => T): T = nestedImpl(func)
  def selfJoin[T](func: Future[Conn] => T): T = selfJoinImpl(func)
  def aggregate[T](func: Future[Conn] => T): T = aggregateImpl(func)
  def socialNews[T](func: Future[Conn] => T): T = socialNewsImpl(func)
  def graph[T](func: Future[Conn] => T): T = graphImpl(func)
  def graph2[T](func: Future[Conn] => T): T = graph2Impl(func)
  def modernGraph1[T](func: Future[Conn] => T): T = modernGraph1Impl(func)
  def modernGraph2[T](func: Future[Conn] => T): T = modernGraph2Impl(func)
  def products[T](func: Future[Conn] => T): T = productsImpl(func)
  def seattle[T](func: Future[Conn] => T): T = seattleImpl(func)
  def mbrainz[T](func: Future[Conn] => T): T = mbrainzImpl(func)

  implicit class testMappedFuture[T](fut: Future[T]) {
    def ===(expectedValue: T): Future[Unit] = fut.map(_ ==> expectedValue)
  }
}
