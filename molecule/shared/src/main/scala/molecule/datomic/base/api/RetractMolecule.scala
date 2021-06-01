//package molecule.datomic.base.api
//
//import molecule.datomic.base.facade.TxReport
//import scala.concurrent.{ExecutionContext, Future}
//
//trait RetractMolecule {
//
//  /** Perform retraction of entity with added transaction meta data against database.
//    *
//    * @return [[molecule.datomic.base.facade.TxReport TxReport]] with result of transaction
//    */
////  def retract: TxReport
//
//  /** Perform asynchronous retraction of entity with added transaction meta data against database.
//    *
//    * @return Future[molecule.facade.TxReport] with result of transaction
//    */
//  def retract(implicit ec: ExecutionContext): Future[TxReport]
//
//  /** Inspect entity retraction with transaction meta data.
//    * {{{
//    *   eid.Tx(MyMetaData.action("moved away")).inspectRetract
//    * }}}
//    * This will print generated Datomic transaction statements in a readable format to output:
//    * {{{
//    *   ## 1 ## Inspect `retract` on entity with tx meta data
//    *   ========================================================================
//    *   1          List(
//    *     1          List(
//    *       1          :db/retractEntity   17592186045445
//    *       2          :db/add   #db/id[:db.part/tx -1000100]  :myMetaData/action   moved away   Card(1)))
//    *   ========================================================================
//    * }}}
//    */
//  def inspectRetract: Unit
//}
