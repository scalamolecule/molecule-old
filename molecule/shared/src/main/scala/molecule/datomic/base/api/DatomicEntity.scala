package molecule.datomic.base.api

import molecule.core.api.Molecule
import molecule.core.ast.elements.{Model, TxMetaData}
import molecule.core.ops.VerifyModel
import molecule.datomic.base.ast.transactionModel.RetractEntity
import molecule.datomic.base.facade.TxReport
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait DatomicEntity {

  private[molecule] def rawValue(
    kw: String
  )(implicit ec: ExecutionContext): Future[Any] = ???

  private[molecule] def asMap(
    depth: Int,
    maxDepth: Int
  )(implicit ec: ExecutionContext): Future[Map[String, Any]] = ???

  private[molecule] def asList(
    depth: Int,
    maxDepth: Int
  )(implicit ec: ExecutionContext): Future[List[(String, Any)]] = ???


  def attrs(implicit ec: ExecutionContext): Future[List[String]]

  /** Asynchronously retract single entity using entity id.
    * <br><br>
    * Given the implicit conversion of Long's in molecule.datomic.base.api.EntityOps to an [[molecule.datomic.base.api.DatomicEntity Entity]] we can
    * can call `retractAsync` on an entity id directly:
    * {{{
    *   // Get entity id of Ben
    *   val benId = Person.e.name_("Ben").get.head
    *
    *   // Retract Ben entity asynchronously
    *   benId.retract.map { tx =>
    *     // ..ben was retracted
    *   }
    * }}}
    *
    * To retract single entity id with tx meta data, use<br>
    * `eid.Tx(MyMetaData.action("my meta data")).retract`
    * <br><br>
    * To retract multiple entities (with or without tx meta data), use<br>
    * `retract(eids, txMetaDataMolecules*)` in molecule.datomic.base.api.EntityOps.
    *
    * @group retract
    * @return [[molecule.datomic.base.facade.TxReport]] with result of retraction
    */
  def retract(implicit ec: ExecutionContext): Future[TxReport]

  def retract(txMeta: Molecule)(implicit ec: ExecutionContext): Future[TxReport]

  /** Get entity retraction transaction data without affecting the database.
    * <br><br>
    * Call `getRetractTx` to retrieve the generated transaction data of the method `retract` on an entity
    * {{{
    *   // Get entity id of Ben
    *   val benId = Person.e.name_("Ben").get.head
    *
    *   // Retraction transaction data
    *   benId.getRetractTx.map(_ ==> List(List(RetractEntity(17592186045445))))
    * }}}
    *
    * @group retract
    * @return List[Retractentity[Long]]
    * */
  def getRetractStmts(implicit ec: ExecutionContext): Future[List[RetractEntity]]

  /** Inspect entity transaction data of method `retract` without affecting the database.
    * {{{
    *   // Inspect retraction of an entity
    *   eid.inspectRetract
    * }}}
    * This will print generated Datomic transaction statements in a readable format to output:
    * {{{
    *   ## 1 ## Inspect `retract` on entity
    *   ========================================================================
    *   1          List(
    *     1          List(
    *       1          :db/retractEntity   17592186045445))
    *   ========================================================================
    * }}}
    *
    * @group retract
    */
  def inspectRetract(implicit ec: ExecutionContext): Future[Unit]

  def inspectRetract(txMeta: Molecule)(implicit ec: ExecutionContext): Future[Unit]


  // Entity api ....................................

  def apply[T](attr: String)(implicit ec: ExecutionContext): Future[Option[T]]

  def apply(attr1: String, attr2: String, moreAttrs: String*)
           (implicit ec: ExecutionContext): Future[List[Option[Any]]]

  /** Get entity graph as Map.
    * <br><br>
    * If entity has reference(s) to other entities it can be a nested graph. Default max levels retrieved is 5.
    *
    *  - Keys of returned Map are namespaced names of attributes
    *  - Values of returned Map are untyped attribute values. For references to other entities,
    *    the value is a Map itself of the referenced entity attributes, etc.
    * {{{
    *   val benId = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eid
    *
    *   benId.graph === Map(
    *     ":db/id" -> 17592186045445L,
    *     ":Person/age" -> 42,
    *     ":Person/address" -> Map(
    *       ":db/id" -> 17592186045446L,
    *       ":Address/street" -> "Hollywood Rd"),
    *     ":Person/name" -> "Ben"
    *   )
    * }}}
    *
    * @group entityGraph
    * @return Map[key: String, value: Any] where value can be a primitive or another nested Map of the entity graph
    */
  def graph(implicit ec: ExecutionContext): Future[Map[String, Any]]

  /** Get entity graph to some depth as Map.
    *
    *  - Keys of returned Map are namespaced names of attributes
    *  - Values of returned Map are untyped attribute values. For references to other entities,
    *    the value is a Map itself of the referenced entity attributes, etc.
    *
    * {{{
    *   val benId = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eid
    *
    *   // 2 levels returned
    *   benId.graphDepth(2).map(_ ==> Map(
    *     ":db/id" -> 17592186045445L,
    *     ":Person/age" -> 42,
    *     ":Person/address" -> Map(
    *       ":db/id" -> 17592186045446L,
    *       ":Address/street" -> "Hollywood Rd"),
    *     ":Person/name" -> "Ben"
    *   )
    *
    *   // 1 level returned
    *   benId.graphDepth(1).map(_ ==> Map(
    *     ":db/id" -> 17592186045445L,
    *     ":Person/age" -> 42,
    *     ":Person/address" -> 17592186045446L // Only reference returned
    *     ":Person/name" -> "Ben"
    *   )
    * }}}
    *
    * @group entityGraph
    * @return Map[key: String, value: Any] where value can be a primitive or another nested Map of the entity graph
    */
  def graphDepth(maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]]

  /** Get entity graph as Map-string (for presentation).
    * <br><br>
    * To show the entity graph, this method quotes all text strings so that you can paste the whole graph
    * into any presentation.
    * <br><br>
    * If the entity has reference(s) to other entities it can be a nested graph. Default max levels retrieved is 5.
    *
    *  - Keys of returned Map are namespaced names of attributes
    *  - Values of returned Map are untyped attribute values. For references to other entities,
    *    the value is a Map itself of the referenced entity attributes, etc.
    *
    * {{{
    *   val benId = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eid
    *
    *   benId.graphCode ===
    *     """Map(
    *       |  ":db/id" -> 17592186045445L,
    *       |  ":Person/age" -> 42,
    *       |  ":Person/address" -> Map(
    *       |    ":db/id" -> 17592186045446L,
    *       |    ":Address/street" -> "Hollywood Rd"),
    *       |  ":Person/name" -> "Ben")""".stripMargin
    * }}}
    *
    * @group entityGraph
    * @return String
    */
  def inspectGraph(implicit ec: ExecutionContext): Future[Unit]

  /** Get entity graph to some depth as Map-string (for presentation).
    * <br><br>
    * To show the entity graph, this method quotes all text strings so that you can paste the whole graph
    * into any presentation.
    *
    *  - Keys of returned Map are namespaced names of attributes
    *  - Values of returned Map are untyped attribute values. For references to other entities,
    *    the value is a Map itself of the referenced entity attributes, etc.
    *
    * {{{
    *   val benId = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eid
    *
    *   // 2 levels (in this case all levels) returned
    *   benId.graphCodeDepth(2) ===
    *     """Map(
    *       |  ":db/id" -> 17592186045445L,
    *       |  ":Person/age" -> 42,
    *       |  ":Person/address" -> Map(
    *       |    ":db/id" -> 17592186045446L,
    *       |    ":Address/street" -> "Hollywood Rd"),
    *       |  ":Person/name" -> "Ben")""".stripMargin
    *
    *   // 1 level returned
    *   // Note that only reference to Address entity on level 2 is returned
    *   benId.graphCodeDepth(1) ===
    *     """Map(
    *       |  ":db/id" -> 17592186045445L,
    *       |  ":Person/age" -> 42,
    *       |  ":Person/address" -> 17592186045446L,
    *       |  ":Person/name" -> "Ben")""".stripMargin
    * }}}
    *
    * @group entityGraph
    * @return String
    */
  def inspectGraphDepth(maxDepth: Int)(implicit ec: ExecutionContext): Future[Unit]

  private[molecule] def graphCode(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] = ???

}
