package molecule.datomic.peer.facade

import java.util.UUID.randomUUID
import datomic.Peer
import molecule.core.data.SchemaTransaction
import molecule.datomic.base.facade.exception.DatomicFacadeException
import scala.jdk.CollectionConverters._

/** Facade to Datomic Peer with selected methods.
  *
  * @groupname database  Database operations
  * @groupprio 10
  * */
trait Datomic_Peer {

  /** Get database names
    *
    * Datomic uses a URI to identify which databases to find and is comprised of
    * a protocol and a host identifier with an added asterisk '*'.
    *
    * Here are some examples of protocol/dbIdentifier settings for various
    * systems on a local machine:
    *
    * | Peer        | protocol | host            | Comment                        |
    * | ---         | :---:    | ---             | ---                            |
    * | In-memory   | mem      |                 |                                |
    * | Free        | free     | localhost:4334/ | With a running Free transactor |
    * | Starter/Pro | dev      | localhost:4334/ | With a running Pro transactor  |
    *
    * Host could also be remote. For more info on the db URI syntax,
    *
    * @see https://docs.datomic.com/on-prem/javadoc/datomic/Peer.html#connect-java.lang.Object-
    * @param protocol
    * @param host
    * @return List of database names
    */
  def getDatabaseNames(
    protocol: String = "mem",
    host: String = ""): List[String] = try {
    Peer.getDatabaseNames(s"datomic:$protocol://$host*").asScala.toList
  } catch {
    case e: Throwable => throw new DatomicFacadeException(e.toString)
  }

  /** Create database
    *
    * Datomic uses a URI to identify a database and is comprised of a protocol
    * and a database identifier.
    *
    * Here are some examples of protocol/dbIdentifier settings for various
    * systems on a local machine:
    *
    * | Peer        | protocol | db identifier         | Comment                        |
    * | ---         | :---:    | ---                   | ---                            |
    * | In-memory   | mem      | dbName                |                                |
    * | Free        | free     | localhost:4334/dbName | With a running Free transactor |
    * | Starter/Pro | dev      | localhost:4334/dbName | With a running Pro transactor  |
    *
    * Host could also be remote. For more info on the db URI syntax,
    *
    * @see https://docs.datomic.com/on-prem/javadoc/datomic/Peer.html#connect-java.lang.Object-
    * @param protocol
    * @param dbIdentifier
    * @return True on success
    */
  def createDatabase(
    protocol: String = "mem",
    dbIdentifier: String = ""): Boolean = try {
    Peer.createDatabase(s"datomic:$protocol://$dbIdentifier")
  } catch {
    case e: Throwable => throw new DatomicFacadeException(e.toString)
  }

  /** Delete database
    *
    * Datomic uses a URI to identify a database and is comprised of a protocol
    * and a database identifier.
    *
    * Here are some examples of protocol/dbIdentifier settings for various
    * systems on a local machine:
    *
    * | Peer        | protocol | db identifier         | Comment                        |
    * | ---         | :---:    | ---                   | ---                            |
    * | In-memory   | mem      | dbName                |                                |
    * | Free        | free     | localhost:4334/dbName | With a running Free transactor |
    * | Starter/Pro | dev      | localhost:4334/dbName | With a running Pro transactor  |
    *
    * Host could also be remote. For more info on the db URI syntax,
    *
    * @see https://docs.datomic.com/on-prem/javadoc/datomic/Peer.html#connect-java.lang.Object-
    * @param protocol
    * @param dbIdentifier
    * @return True on success
    */
  def deleteDatabase(
    protocol: String = "mem",
    dbIdentifier: String = "localhost:4334/"): Boolean = try {
    Peer.deleteDatabase(s"datomic:$protocol://$dbIdentifier")
  } catch {
    case e: Throwable => throw new DatomicFacadeException(e.toString)
  }

  /** Rename database
    *
    * Datomic uses a URI to identify a database and is comprised of a protocol
    * and a database identifier.
    *
    * Here are some examples of protocol/dbIdentifier settings for various
    * systems on a local machine:
    *
    * | Peer        | protocol | db identifier         | new db name | Comment                        |
    * | ---         | :---:    | ---                   | ---         | ---                            |
    * | In-memory   | mem      | dbName                | newDbName   |                                |
    * | Free        | free     | localhost:4334/dbName | newDbName   | With a running Free transactor |
    * | Starter/Pro | dev      | localhost:4334/dbName | newDbName   | With a running Pro transactor  |
    *
    * Host could also be remote. For more info on the db URI syntax,
    *
    * @see https://docs.datomic.com/on-prem/javadoc/datomic/Peer.html#connect-java.lang.Object-
    * @param protocol
    * @param dbIdentifier
    * @return True on success
    */
  def renameDatabase(
    protocol: String = "mem",
    dbIdentifier: String = "localhost:4334/",
    newDbName: String,
  ): Boolean = try {
    Peer.renameDatabase(s"datomic:$protocol://$dbIdentifier", newDbName)
  } catch {
    case e: Throwable => throw new DatomicFacadeException(e.toString)
  }


  /** Connect to Peer database
    *
    * Datomic uses a URI to identify a database and is comprised of a protocol
    * and a database identifier.
    *
    * Here are some examples of protocol/dbIdentifier settings for various
    * systems on a local machine:
    *
    * | Peer        | protocol | db identifier         | new db name | Comment                        |
    * | ---         | :---:    | ---                   | ---         | ---                            |
    * | In-memory   | mem      | dbName                | newDbName   |                                |
    * | Free        | free     | localhost:4334/dbName | newDbName   | With a running Free transactor |
    * | Starter/Pro | dev      | localhost:4334/dbName | newDbName   | With a running Pro transactor  |
    *
    * Host could also be remote. For more info on the db URI syntax,
    *
    * @see https://docs.datomic.com/on-prem/javadoc/datomic/Peer.html#connect-java.lang.Object-
    * @param protocol
    * @param dbIdentifier
    * @return
    */
  def connect(
    protocol: String = "mem",
    dbIdentifier: String = ""
  ): Conn_Peer = try {
    val id = if (dbIdentifier == "") randomUUID().toString else dbIdentifier
    Conn_Peer(Peer.connect(s"datomic:$protocol://$id"))
  } catch {
    case e: Throwable => throw new DatomicFacadeException(e.toString)
  }


  /** Deletes existing database (!) and creates a new empty db with schema from Schema Transaction file.
    * <br><br>
    * A typical development cycle in the initial stages of creating the db schema:
    *
    *  1. Edit schema definition file
    *  1. `sbt compile` to update boilerplate code in generated jars
    *  1. Obtain a fresh connection to new empty db with updated schema:<br>
    *     `implicit val conn = recreateDbFrom(YourDomainSchema)`
    *
    * @group database
    * @param schema       Auto-generated YourDomainSchema Transaction object<br>
    *                     (in package yourdomain.schema of generated source jar)
    * @param dbIdentifier Optional String identifier to name database (default empty string creates a randomUUID)
    * @param protocol     Datomic protocol. Defaults to "mem" for in-memory database.
    * @return [[molecule.datomic.base.facade.Conn Conn]]
    */
  def recreateDbFrom(
    schema: SchemaTransaction,
    protocol: String = "mem",
    dbIdentifier: String = "",
  ): Conn_Peer = {
    val id = if (dbIdentifier == "") randomUUID().toString else dbIdentifier
    try {
      deleteDatabase(protocol, id)
      createDatabase(protocol, id)
      val conn = connect(protocol, id)
      schema.datomicPeer.foreach(edn => conn.transact(edn))
      conn
    } catch {
      case e: Throwable => throw new DatomicFacadeException(e.toString)
    }
  }

  /** Deletes existing database (!) and creates a new empty db with schema from schema data structure.
    * <br><br>
    * Schema data structure is a java List of Map's of key/value pairs defining the schema.
    * <br><br>
    * Can be an [[https://github.com/edn-format/edn EDN]] file like the [[https://github.com/Datomic/mbrainz-sample/blob/master/schema.edn mbrainz example]].
    *
    * @see [[https://docs.datomic.com/on-prem/data-structure-literals.html]]
    * @group database
    * @param schemaData   java.util.List of java.util.Maps of key/values defining a Datomic schema
    * @param dbIdentifier Optional String identifier of database (default empty string creates a randomUUID)
    * @see [[https://docs.datomic.com/on-prem/javadoc/datomic/Peer.html#connect-java.lang.Object-]]
    * @param protocol Datomic protocol. Defaults to "mem" for in-memory database.
    * @return [[molecule.datomic.base.facade.Conn Conn]]
    */
  def recreateDbFromRaw(
    schemaData: java.util.List[_],
    protocol: String = "mem",
    dbIdentifier: String = "",
  ): Conn_Peer = {
    val id = if (dbIdentifier == "") randomUUID().toString else dbIdentifier
    try {
      deleteDatabase(protocol, id)
      createDatabase(protocol, id)
      val conn = connect(protocol, id)
      conn.transact(schemaData)
      conn
    } catch {
      case e: Throwable => throw new DatomicFacadeException(e.toString)
    }
  }

  /** Transact schema from generated schema transaction data.
    *
    * @group database
    * @param schema sbt-plugin auto-generated Transaction file path.to.schema.YourDomainSchema
    * @param dbIdentifier
    * @param protocol
    * @return
    */
  def transactSchema(
    schema: SchemaTransaction,
    protocol: String = "mem",
    dbIdentifier: String = "",
  ): Conn_Peer = try {
    val id   = if (dbIdentifier == "") randomUUID().toString else dbIdentifier
    val conn = connect(protocol, id)
    schema.datomicPeer.foreach(edn => conn.transact(edn))
    conn
  } catch {
    case e: Throwable => throw new DatomicFacadeException(e.toString)
  }
}

object Datomic_Peer extends Datomic_Peer

