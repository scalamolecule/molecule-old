package molecule.datomic.peer.facade

import autowire._
import boopickle.Default._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object Datomic_Peer {

  def getDatabaseNames(
    protocol: String = "mem",
    host: String = "localhost:4334/"
  ): Future[List[String]] =
    wire_Datomic_Peer().getDatabaseNames(protocol, host).call()

}
