package moleculeTests.tests.db.datomic.time.domain

import molecule.datomic.api.out2._
import molecule.datomic.base.facade.{Conn, TxReport}
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


// Example domain class doing CRUD operations on the database
// set with the connection object at runtime

object Crud {

  def create(numbers: Int*)(implicit conn: Future[Conn]): Future[TxReport] = {
    Ns.int insert numbers
  }

  def read(implicit conn: Future[Conn]): Future[List[Int]] = {
    Ns.int.get.map(_.sorted)
  }

  def update(pair: (Int, Int))(implicit conn: Future[Conn]): Future[TxReport] = {
    val (oldNumber, newNumber) = pair
    for {
      eid <- Ns.e.int_(oldNumber).get.map(_.headOption.getOrElse {
        throw new IllegalArgumentException(s"Old number ($oldNumber) doesn't exist in db.")
      })
      res <- Ns(eid).int(newNumber).update
    } yield res
  }

  def delete(numbers: Int*)(implicit futConn: Future[Conn]): Future[TxReport] = {
    for {
      ids <- Ns.e.int_(numbers).get
      res <- Ns(ids).int().update
    } yield res
  }
}