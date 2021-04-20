package molecule.core.marshalling

import boopickle.Default._
import playing.sloth.WebClient

/** RPC via Sloth ajax web client to server
  *
  * Will only be called from client side.
  *
  */
object MoleculeWebClient extends WebClient {

  /** Wire handle
    *
    */
  val moleculeRpc: MoleculeRpc = clientAjax(s"http://localhost:8080/ajax").wire[MoleculeRpc]

}