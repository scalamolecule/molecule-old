package molecule.datomic.client.facade

import datomic.Peer
import datomic.Util.read
import datomicScala.client.api.async.AsyncDatomic
import datomicScala.client.api.sync.Datomic
import molecule.core.data.SchemaTransaction
import molecule.datomic.base.facade.TxReport
import molecule.datomic.base.facade.exception.DatomicFacadeException
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal
//import scala.concurrent.ExecutionContext.Implicits.global


/** Datomic facade for cloud/dev-local.
  *
  * @param system     folder where samples reside
  * @param storageDir absolute path to where "system" is
  *                   overrides :storage-dir in ~/.datomic/dev-local.edn
  */
case class Datomic_DevLocal(system: String, storageDir: String = "")
  extends Datomic_Client(
    Datomic.clientDevLocal(system, storageDir),
    AsyncDatomic.clientDevLocal(system, storageDir)
  ) {


  /** Retrieve DevLocal Client connection
    *
    * @param dbName
    * @return
    */
  def connect(dbName: String)
             (implicit ec: ExecutionContext): Future[Conn_Client] = {
    Future(Conn_Client(client, clientAsync, dbName))
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
  )(implicit ec: ExecutionContext): Future[Boolean] = Future{
    client.createDatabase(dbName, timeout)
  }

  def deleteDatabase(
    dbName: String,
    timeout: Int = 0
  )(implicit ec: ExecutionContext): Future[Boolean] = Future{
    client.deleteDatabase(dbName, timeout)
  }

  def renameDatabase(
    dbIdentifier: String,
    newDbName: String,
    protocol: String = "mem"
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
    * @param schema Auto-generated YourDomainSchema Transaction object<br>
    *               (in package yourdomain.schema of generated source jar)
    * @param dbName Database name
    * @return [[molecule.datomic.base.facade.Conn Conn]]
    */
  def recreateDbFrom(
    schema: SchemaTransaction,
    dbName: String
  )(implicit ec: ExecutionContext): Future[Conn_Client] = {
    for {
      _ <- deleteDatabase(dbName)
      _ <- createDatabase(dbName)
      conn <- connect(dbName)
      _ <- Future.sequence(schema.datomicClient.map(edn => conn.transact(edn)))
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
    * @param schemaData java.util.List of java.util.Maps of key/values defining a Datomic schema
    * @param dbName     Optional String identifier of database (default empty string creates a randomUUID)
    * @return [[molecule.datomic.base.facade.Conn Conn]]
    */
  def recreateDbFromRaw(
    schemaData: java.util.List[_],
    dbName: String
  )(implicit ec: ExecutionContext): Future[Conn_Client] = {
    for {
      _ <- deleteDatabase(dbName)
      _ <- createDatabase(dbName)
      conn <- connect(dbName)
      _ <- conn.transactRaw(schemaData)
    } yield conn
  }


//  def checkNotLambda: Any => Boolean = {
//    val index    = read(":db/index")
//    val fulltext = read(":db/fulltext")
//    (k: Any) => k == index || k == fulltext
//  }
}

