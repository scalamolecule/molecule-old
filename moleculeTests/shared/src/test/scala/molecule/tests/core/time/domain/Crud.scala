package molecule.tests.core.time.domain

import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


// Example domain class doing CRUD operations on the database
// set with the connection object at runtime

object Crud {

  def create(numbers: Int*)(implicit conn: Conn): Future[TxReport] = {
    Ns.int insert numbers
  }

  def read(implicit conn: Conn): Future[List[Int]] = {
    Ns.int.get.map(_.sorted)
  }

  def update(pair: (Int, Int))(implicit conn: Conn): Future[Unit] = {
    val (oldNumber, newNumber) = pair
    for {
      e <- Ns.e.int_(oldNumber).get.map(_.headOption.getOrElse {
        throw new IllegalArgumentException(s"Old number ($oldNumber) doesn't exist in db.")
      })
      _ <- Ns(e).int(newNumber).update
    } yield ()
  }

  def delete(numbers: Int*)(implicit conn: Conn): Future[Unit] = Future {
    numbers.foreach(i =>
      // Find entity
      Ns.e.int_(i).get.map(_.foreach(e =>
        // Retract attribute value
        Ns(e).int().update
      )
      )
    )
  }
}