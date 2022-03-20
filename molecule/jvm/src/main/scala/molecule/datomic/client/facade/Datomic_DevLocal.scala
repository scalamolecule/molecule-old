package molecule.datomic.client.facade

import java.util.UUID.randomUUID
import datomic.Peer
import datomicScala.client.api.sync.Datomic
import molecule.core.data.SchemaTransaction
import molecule.core.marshalling.ast.{ConnProxy, DatomicDevLocalProxy}
import scala.concurrent.{ExecutionContext, Future}


/** Datomic facade for cloud/dev-local.
 *
 * @param system     folder where samples reside
 * @param storageDir absolute path to where "system" is
 *                   overrides :storage-dir in ~/.datomic/dev-local.edn
 */
case class Datomic_DevLocal(system: String, storageDir: String = "")
  extends Datomic_Client(Datomic.clientDevLocal(system, storageDir)) {

  /** Retrieve DevLocal Client connection
   *
   * @param dbName
   * @return
   */
  private[molecule] def connect(
    connProxy: ConnProxy,
    dbName: String
  )(implicit ec: ExecutionContext): Future[Conn_Client] = Future {
    Conn_Client(client, connProxy, dbName, system)
  }

  def connect(
    schema: SchemaTransaction,
    protocol: String,
    dbName: String,
  )(implicit ec: ExecutionContext): Future[Conn_Client] = Future {
    val connProxy = DatomicDevLocalProxy(
      protocol, system, storageDir, dbName, schema.datomicClient, schema.nsMap, schema.attrMap
    )
    Conn_Client(client, connProxy, dbName, system)
  }

  def getDatabaseNames(
    timeout: Int = 0,
    offset: Int = 0,
    limit: Int = 1000
  )(implicit ec: ExecutionContext): Future[List[String]] = Future {
    client.listDatabases(timeout, offset, limit).asScala.toList
  }

  def createDatabase(
    dbName: String,
    timeout: Int = 0
  )(implicit ec: ExecutionContext): Future[Boolean] = Future {
    client.createDatabase(dbName, timeout)
  }

  def deleteDatabase(
    dbName: String,
    timeout: Int = 0
  )(implicit ec: ExecutionContext): Future[Boolean] = Future {
    client.deleteDatabase(dbName, timeout)
  }

  def renameDatabase(
    dbIdentifier: String,
    newDbName: String,
    protocol: String = "mem"
  )(implicit ec: ExecutionContext): Future[Boolean] = Future {
    Peer.renameDatabase(s"datomic:$protocol://$dbIdentifier", newDbName)
  }


  /** Deletes existing database (!) and creates a new empty in-mem db with schema from Schema Transaction file.
   * <br><br>
   * A typical development cycle in the initial stages of creating the db schema:
   *
   *  1. Edit schema definition file
   *  1. `sbt compile` to update boilerplate code in generated jars
   *  1. Obtain a fresh connection to new empty db with updated schema:<br>
   *     `implicit val conn = recreateDbFrom(YourDomainSchema)`
   *
   * @group database
   * @param schema Auto-generated YourDomainSchema Transaction object<br>
   *               (in package yourdomain.schema of generated source jar)
   * @param dbName Database name
   * @return [[molecule.datomic.base.facade.Conn Conn]]
   */
  def recreateDbFrom(
    schema: SchemaTransaction,
    dbName: String = ""
  )(implicit ec: ExecutionContext): Future[Conn_Client] = {
    val connProxy = DatomicDevLocalProxy(
      "mem", system, storageDir, dbName, schema.datomicClient, schema.nsMap, schema.attrMap
    )
    recreateDbFromEdn(schema.datomicClient, connProxy, dbName)
  }


  def recreateDbFromEdn(
    edns: Seq[String],
    connProxy: ConnProxy,
    dbName0: String = ""
  )(implicit ec: ExecutionContext): Future[Conn_Client] = {
    val dbName = if (dbName0 == "") randomUUID().toString else dbName0
    for {
      _ <- deleteDatabase(dbName)
      _ <- createDatabase(dbName)
      conn <- connect(connProxy, dbName)
      // Ensure each transaction finishes before the next
      // partitions/attributes (or none for initial empty test dummy)
      _ <- if (edns.nonEmpty) conn.transact(edns.head) else Future.unit
      // attributes/aliases
      _ <- if (edns.size > 1) conn.transact(edns(1)) else Future.unit
      // aliases
      _ <- if (edns.size > 2) conn.transact(edns(2)) else Future.unit
    } yield {
      conn
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
   * @param schemaData java.util.List of java.util.Maps of key/values defining a Datomic schema
   * @param dbName     Optional String identifier of database (default empty string creates a randomUUID)
   * @return [[molecule.datomic.base.facade.Conn Conn]]
   */
  def recreateDbFromRaw(
    schemaData: java.util.List[_],
    connProxy: ConnProxy,
    dbName0: String = ""
  )(implicit ec: ExecutionContext): Future[Conn_Client] = {
    val dbName = if (dbName0 == "") randomUUID().toString else dbName0
    for {
      _ <- deleteDatabase(dbName)
      _ <- createDatabase(dbName)
      conn <- connect(connProxy, dbName)
      _ <- conn.transact(schemaData)
    } yield conn
  }
}

