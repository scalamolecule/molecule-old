//package molecule.core.marshalling
//
//import boopickle.Default._
//
///** RPC via Sloth ajax web client to server
//  *
//  * Will only be called from client side.
//  *
//  */
//private[molecule] object MoleculeWebClient extends WebClient {
//
//  val rpc: MoleculeRpc = moleculeAjax("localhost", 8080).wire[MoleculeRpc]
//  //  val rpc: MoleculeRpc = moleculeWs("ws://localhost:8080/ws").wire[MoleculeRpc]
//}