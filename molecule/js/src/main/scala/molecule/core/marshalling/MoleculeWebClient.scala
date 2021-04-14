package molecule.core.marshalling

import boopickle.Default._
import playing.sloth.WebClient

/** RPC via Sloth ajax web client to server
  *
  * Will only be called from client side (macros only implement the js side).
  *
  */
object MoleculeWebClient extends WebClient {

  /** Wire handle
    *
    */
  val moleculeWire: QueryExecutor =
    clientAjax(s"http://localhost:8080/ajax").wire[QueryExecutor]

}