package molecule.core.marshalling

/** Client db connection.
  *
  * Used to cary information enabling marshalling on both client and server side.
  *
  * Make a similar subclass of ConnProxy like this one in order to use an
  * alternative moleculeRpc implementation.
  *
  * @param dbProxy0  Db coordinates to access db on server side
  */
case class Conn_Js(dbProxy0: DbProxy) extends ConnProxy {

  val isJsPlatform: Boolean = true

  override lazy val dbProxy: DbProxy = dbProxy0

  override lazy val moleculeRpc: MoleculeRpc = MoleculeWebClient.moleculeRpc

}