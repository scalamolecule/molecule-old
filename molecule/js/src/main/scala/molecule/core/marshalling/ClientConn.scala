package molecule.core.marshalling

import java.io.Reader
import java.util
import java.util.Date
import molecule.core.ast.elements.Model
import molecule.core.transform.Model2Statements
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.tempDb.TempDb
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.facade.{Conn, DatomicDb, TxReport}
import scala.concurrent.{ExecutionContext, Future}

/** Client db connection.
  *
  * Used to cary information enabling marshalling on both client and server side.
  *
  * Make a similar subclass of ConnProxy like this one in order to use an
  * alternative moleculeRpc implementation.
  *
  * @param dbProxy0  Db coordinates to access db on server side
  */
case class ClientConn(dbProxy0: DbProxy) extends ConnProxy {

  override lazy val dbProxy: DbProxy = dbProxy0

  override lazy val moleculeRpc: MoleculeRpc = MoleculeWebClient.moleculeRpc

}