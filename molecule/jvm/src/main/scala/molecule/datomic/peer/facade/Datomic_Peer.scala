package molecule.datomic.peer.facade

import java.util.UUID.randomUUID
import datomic.Peer
import molecule.core.data.SchemaTransaction
import molecule.core.marshalling.{ConnProxy, DatomicInMemProxy}
import molecule.core.util.JavaConversions
import scala.concurrent.{ExecutionContext, Future}


/** Facade to Datomic Peer with selected methods.
  *
  * @groupname database  Database operations
  * @groupprio 10
  * */
trait Datomic_Peer extends JavaConversions {

  private val inMemConnProxy: ConnProxy = DatomicInMemProxy(Nil, Map.empty[String, (Int, String)])

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
    dbIdentifier: String = "",
    connProxy: ConnProxy = inMemConnProxy
  )(implicit ec: ExecutionContext): Future[Conn_Peer] = Future {
    val id = if (dbIdentifier == "") randomUUID().toString else dbIdentifier
    Conn_Peer(s"datomic:$protocol://$id", connProxy)
  }

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
    host: String = ""
  )(implicit ec: ExecutionContext): Future[List[String]] = Future {
    Peer.getDatabaseNames(s"datomic:$protocol://$host*").asScala.toList
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
    dbIdentifier: String = ""
  )(implicit ec: ExecutionContext): Future[Boolean] = Future {
    Peer.createDatabase(s"datomic:$protocol://$dbIdentifier")
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
    dbIdentifier: String = "localhost:4334/"
  )(implicit ec: ExecutionContext): Future[Boolean] = Future {
    Peer.deleteDatabase(s"datomic:$protocol://$dbIdentifier")
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
  )(implicit ec: ExecutionContext): Future[Boolean] = Future {
    Peer.renameDatabase(s"datomic:$protocol://$dbIdentifier", newDbName)
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
    connProxy: ConnProxy = inMemConnProxy
  )(implicit ec: ExecutionContext): Future[Conn_Peer] = {
    recreateDbFromEdn(schema.datomicPeer, protocol, dbIdentifier, connProxy)
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
    * @param edns
    * @param protocol
    * @param dbIdentifier
    * @return
    */
  def recreateDbFromEdn(
    edns: Seq[String],
    protocol: String = "mem",
    dbIdentifier: String = "",
    connProxy: ConnProxy = inMemConnProxy
  )(implicit ec: ExecutionContext): Future[Conn_Peer] = {
    val id = if (dbIdentifier == "") randomUUID().toString else dbIdentifier
    for {
      _ <- deleteDatabase(protocol, id)
      _ <- createDatabase(protocol, id)
      conn <- connect(protocol, id, connProxy)
      _ <- Future.sequence(edns.map(edn => conn.transact(edn)))
    } yield conn
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
    connProxy: ConnProxy = inMemConnProxy
  )(implicit ec: ExecutionContext): Future[Conn_Peer] = {
    val id = if (dbIdentifier == "") randomUUID().toString else dbIdentifier
    for {
      _ <- deleteDatabase(protocol, id)
      _ <- createDatabase(protocol, id)
      conn <- connect(protocol, id, connProxy)
      _ <- conn.transactRaw(schemaData)
    } yield conn
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
    connProxy: ConnProxy = inMemConnProxy
  )(implicit ec: ExecutionContext): Future[Conn_Peer] = {
    val id = if (dbIdentifier == "") randomUUID().toString else dbIdentifier
    for {
      conn <- connect(protocol, id, connProxy)
      _ <- Future.sequence(schema.datomicPeer.map(edn => conn.transact(edn)))
    } yield conn
  }
}

object Datomic_Peer extends Datomic_Peer

