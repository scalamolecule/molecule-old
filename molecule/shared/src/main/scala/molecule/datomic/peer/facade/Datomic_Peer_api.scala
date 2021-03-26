package molecule.datomic.peer.facade

import molecule.core.data.SchemaTransaction

trait Datomic_Peer_api {

  def getDatabaseNames(
    protocol: String = "mem",
    host: String = "localhost:4334/"
  ): List[String]

  def createDatabase(dbIdentifier: String, protocol: String = "mem"): Boolean

  def recreateDbFrom(
    schema: SchemaTransaction,
    dbIdentifier: String = "",
    protocol: String = "mem"
  ): Conn_Peer_api
}
