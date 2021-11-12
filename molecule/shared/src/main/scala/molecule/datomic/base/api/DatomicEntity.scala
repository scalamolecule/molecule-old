package molecule.datomic.base.api

import molecule.core.api.Molecule
import molecule.core.ast.elements.{Model, TxMetaData}
import molecule.core.ops.VerifyModel
import molecule.datomic.base.ast.transactionModel.{RetractEntity, Statement}
import molecule.datomic.base.facade.TxReport
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait DatomicEntity {


  /** Retract single entity using entity id.
   * <br><br>
   * Given the implicit conversion of Long's in molecule.datomic.base.api.EntityOps to an
   * [[molecule.datomic.base.api.DatomicEntity Entity]] we can
   * can call `retract` on an entity id directly:
   * {{{
   * for {
   *   // Get entity id of Ben
   *   benId <- Person.e.name_("Ben").get.map(_.eid)
   *
   *   // Retract Ben entity
   *   _ <- benId.retract
   *
   *   _ <- Person.name.get.map(_ ==> Nil)
   * } yield ()
   * }}}
   *
   * To retract single entity id with tx meta data, use<br>
   * `eid.Tx(MyMetaData.action("my meta data")).retract`
   * <br><br>
   * To retract multiple entities (with or without tx meta data), use<br>
   * `retract(eids, txMetaDataMolecules*)` in molecule.datomic.base.api.EntityOps.
   *
   * @group retract
   * @param ec Implicit [[ExecutionContext]]
   * @return [[molecule.datomic.base.facade.TxReport]] with result of retraction
   */
  def retract(implicit ec: ExecutionContext): Future[TxReport]


  /** Retract single entity using entity id and tx meta data.
   * <br><br>
   * Given the implicit conversion of Long's in molecule.datomic.base.api.EntityOps to an
   * [[molecule.datomic.base.api.DatomicEntity Entity]] we can
   * can call `retract` on an entity id directly:
   * {{{
   * for {
   *   eid <- Ns.int(1).save.map(_.eid)
   *
   *   // Retract entity with tx meta data
   *   tx <- eid.retract(Ref2.str2("meta2"), Ref1.str1("meta1")).map(_.tx)
   *
   *   // What was retracted and with what tx meta data
   *   _ <- Ns.e.int.tx.op_(false).Tx(Ref2.str2 + Ref1.str1).getHistory.map(_ ==> List(
   *     (eid, 1, tx, "meta2", "meta1")
   *   ))
   * } yield ()
   * }}}
   *
   * To retract single entity id with tx meta data, use<br>
   * `eid.Tx(MyMetaData.action("my meta data")).retract`
   * <br><br>
   * To retract multiple entities (with or without tx meta data), use<br>
   * `retract(eids, txMetaDataMolecules*)` in molecule.datomic.base.api.EntityOps.
   *
   * @group retract
   * @param txMeta1 tx meta data molecule
   * @param txMetaMore Optional additional tx meta data molecules
   * @param ec Implicit [[ExecutionContext]]
   * @return [[molecule.datomic.base.facade.TxReport]] with result of retraction
   */
  def retract(txMeta1: Molecule, txMetaMore: Molecule*)(implicit ec: ExecutionContext): Future[TxReport]


  /** Get entity retraction transaction data without affecting the database.
   * <br><br>
   * Call `getRetractStmts` to retrieve the generated transaction data of the method `retract` on an entity
   * {{{
   * for {
   *   // Get entity id of Ben
   *   benId <- Person.e.name_("Ben").get.map(_.eid)
   *
   *   // Retraction transaction data
   *   _ <- benId.getRetractStmts.map(_ ==> List(RetractEntity(17592186045453)))
   * } yield ()
   * }}}
   *
   * @group retract
   * @param ec Implicit [[ExecutionContext]]
   * @return Future[Seq[Statement]]
   * */
  def getRetractStmts(implicit ec: ExecutionContext): Future[Seq[Statement]]


  /** Get entity retraction transaction data without affecting the database.
   * <br><br>
   * Call `getRetractStmts` to retrieve the generated transaction data of the method `retract` on an entity
   * {{{
   * for {
   *   // Get entity id of Ben
   *   benId <- Person.e.name_("Ben").get.map(_.eid)
   *
   *   // Retraction transaction data
   *   _ <- benId.getRetractStmts().map(_ ==> List(
   *     RetractEntity(17592186045453)
   *   ))
   * } yield ()
   * }}}
   *
   * @group retract
   * @param txMeta1 tx meta data molecule
   * @param txMetaMore Optional additional tx meta data molecules
   * @param ec Implicit [[ExecutionContext]]
   * @return Future[Seq[Statement]]
   * */
  def getRetractStmts(txMeta1: Molecule, txMetaMore: Molecule*)(implicit ec: ExecutionContext): Future[Seq[Statement]]

  /** Inspect entity transaction data of method `retract` without affecting the database.
   * {{{
   * for {
   *   // Inspect retraction of an entity
   *   _ <- eid.inspectRetract
   * } yield ()
   * }}}
   * This will print generated Datomic transaction statements in a readable format to output:
   * {{{
   * ## 1 ## Inspect `retract` on entity
   * =============================================================================
   * list(
   *   RetractEntity(17592186045453))
   * =============================================================================
   * }}}
   *
   * @group retract
   * @param ec Implicit [[ExecutionContext]]
   * @return Unit (prints data to console)
   */
  def inspectRetract(implicit ec: ExecutionContext): Future[Unit]


  /** Inspect entity transaction data of method `retract` with tx meta data without affecting the database.
   * {{{
   * for {
   *   // Inspect retraction of an entity
   *   _ <- eid.inspectRetract(Ref2.str2("meta2") + Ref1.str1("meta1"))
   * } yield ()
   * }}}
   * This will print generated Datomic transaction statements in a readable format to output:
   * {{{
   * ## 1 ## Inspect `retract` on entity with tx meta data
   * =============================================================================
   * list(
   *   RetractEntity(17592186045453),
   *   Add(datomic.tx,:Ref2/str2,meta2,Card(1)),
   *   Add(datomic.tx,:Ref1/str1,meta1,Card(1)))
   =============================================================================
   * }}}
   *
   * @group retract
   * @param txMeta1 tx meta data molecule
   * @param txMetaMore Optional additional tx meta data molecules
   * @param ec Implicit [[ExecutionContext]]
   * @return Unit (prints data to console)
   */
  def inspectRetract(txMeta1: Molecule, txMetaMore: Molecule*)(implicit ec: ExecutionContext): Future[Unit]


  // Entity api -------------------------------------------------

  def attrs(implicit ec: ExecutionContext): Future[List[String]]

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
   * for {
   *   benId <- Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").map(_.eid)
   *
   *   _ <- benId.graph.map(_ ==-< Map(
   *     ":db/id" -> 17592186045445L,
   *     ":Person/age" -> 42,
   *     ":Person/address" -> Map(
   *       ":db/id" -> 17592186045446L,
   *       ":Address/street" -> "Hollywood Rd"),
   *     ":Person/name" -> "Ben"
   *   ))
   * } yield ()
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
   * for {
   *   benId <- Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").map(_.eid)
   *
   *   // 2 levels returned
   *   _ <- benId.graphDepth(2).map(_ ==> Map(
   *     ":db/id" -> 17592186045445L,
   *     ":Person/age" -> 42,
   *     ":Person/address" -> Map(
   *       ":db/id" -> 17592186045446L,
   *       ":Address/street" -> "Hollywood Rd"),
   *     ":Person/name" -> "Ben"
   *   ))
   *
   *   // 1 level returned
   *   _ <- benId.graphDepth(1).map(_ ==> Map(
   *     ":db/id" -> 17592186045445L,
   *     ":Person/age" -> 42,
   *     ":Person/address" -> 17592186045446L // Only reference returned
   *     ":Person/name" -> "Ben"
   *   ))
   * } yield ()
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
   * for {
   *   benId <- Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").map(_.eid)
   *
   *   _ <- benId.graphCode.map(_ ==>
   *     """Map(
   *       |  ":db/id" -> 17592186045445L,
   *       |  ":Person/age" -> 42,
   *       |  ":Person/address" -> Map(
   *       |    ":db/id" -> 17592186045446L,
   *       |    ":Address/street" -> "Hollywood Rd"),
   *       |  ":Person/name" -> "Ben")""".stripMargin
   *   ))
   * } yield ()
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
   * for {
   *   benId <- Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").map(_.eid)
   *
   *   // 2 levels (in this case all levels) returned
   *   _ <- benId.graphCodeDepth(2).map(_ ==>
   *     """Map(
   *       |  ":db/id" -> 17592186045445L,
   *       |  ":Person/age" -> 42,
   *       |  ":Person/address" -> Map(
   *       |    ":db/id" -> 17592186045446L,
   *       |    ":Address/street" -> "Hollywood Rd"),
   *       |  ":Person/name" -> "Ben")""".stripMargin
   *   ))
   *
   *   // 1 level returned
   *   // Note that only reference to Address entity on level 2 is returned
   *   _ <- benId.graphCodeDepth(1).map(_ ==>
   *     """Map(
   *       |  ":db/id" -> 17592186045445L,
   *       |  ":Person/age" -> 42,
   *       |  ":Person/address" -> 17592186045446L,
   *       |  ":Person/name" -> "Ben")""".stripMargin
   *   ))
   * } yield ()
   * }}}
   *
   * @group entityGraph
   * @return String
   */
  def inspectGraphDepth(maxDepth: Int)(implicit ec: ExecutionContext): Future[Unit]


  def graphCode(maxDepth: Int)(implicit ec: ExecutionContext): Future[String]

  // Internal --------------------------------------------------

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

}
