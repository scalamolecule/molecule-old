package moleculeTests.tests.core.time.domain

import moleculeTests.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out1._
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// Example domain class getting and manipulating the db with molecules
// Molecules need an implicit conn object at runtime
case class Counter(eid: Long) {

  // Live value
  def value(implicit conn: Future[Conn]): Future[Int] = {
    Ns(eid).int.get.map(_.head)
  }

  // Mutate domain data
  def incr(implicit conn: Future[Conn]): Future[Int] = {
    for {
      // get current value
      cur <- value
      next = cur + 10
      // save next
      _ <- Ns(eid).int(next).update
    } yield next
  }
}
