package molecule.setup

import molecule.datomic.base.facade.Conn
import molecule.datomic.base.util.{System, SystemPeer}
import molecule.setup.core.CoreData
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait AsyncTestSuite extends TestSuite with CoreData
  // Platform-specific JS/JVM implementations
  with AsyncTestSuiteImpl {

  var system: System = SystemPeer

  def core[T](func: Conn => T): T = coreImpl(func)
  def bidirectional[T](func: Conn => T): T = bidirectionalImpl(func)
  def partition[T](func: Conn => T): T = partitionImpl(func)
  def nested[T](func: Conn => T): T = nestedImpl(func)
  def selfJoin[T](func: Conn => T): T = selfJoinImpl(func)
  def aggregate[T](func: Conn => T): T = aggregateImpl(func)
  def socialNews[T](func: Conn => T): T = socialNewsImpl(func)
  def graph[T](func: Conn => T): T = graphImpl(func)
  def graph2[T](func: Conn => T): T = graph2Impl(func)
  def modernGraph1[T](func: Conn => T): T = modernGraph1Impl(func)
  def modernGraph2[T](func: Conn => T): T = modernGraph2Impl(func)
  def products[T](func: Conn => T): T = productsImpl(func)
  def seattle[T](func: Conn => T): T = seattleImpl(func)
  def mbrainz[T](func: Conn => T): T = mbrainzImpl(func)

  implicit class testMappedFuture[T](fut: Future[T]) {
    def ===(expectedValue: T): Future[Unit] = fut.map(_ ==> expectedValue)
  }
}
