package molecule.setup

import molecule.datomic.base.facade.Conn
import molecule.setup.core.CoreData
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait AsyncTestSuite extends TestSuite with CoreData
  // Platform-specific JS/JVM implementations
  with AsyncTestSuiteImpl
{

  def core[T](func: Conn => T): T = coreImpl(func)

  implicit class testMappedFuture[T](fut: Future[T]) {
    def ===(expectedValue: T): Future[Unit] = fut.map(_ ==> expectedValue)
  }
}
