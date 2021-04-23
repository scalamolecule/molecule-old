package molecule.datomic.base.api

import molecule.core.ast.Molecule
import molecule.datomic.base.ast.transactionModel.RetractEntity
import molecule.datomic.base.facade.TxReport
import scala.concurrent.{ExecutionContext, Future}

trait DatomicEntity {

  def keySet: Set[String]

  def keys: List[String]

  def rawValue(key: String): Any


  /** Get typed attribute value of entity.
    * <br><br>
    * Apply namespaced attribute name with a type parameter to return an optional typed value.
    * <br><br>
    * Note how referenced entities are returned as a Map so that we can continue traverse the entity graph.
    * {{{
    *   val List(benId, benAddressId) = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eids
    *
    *   // Level 1
    *   benId[String](":Person/name") === Some("Ben")
    *   benId[Int](":Person/age") === Some(42)
    *
    *   // Level 2
    *   val refMap = benId[Map[String, Any]](":Person/address").getOrElse(Map.empty[String, Any])
    *   benAddressId[String](":Address/street") === Some("Hollywood Rd")
    *
    *   // Non-asserted or non-existing attribute returns None
    *   benId[Int](":Person/non-existing-attribute") === None
    *   benId[Int](":Person/existing-but-non-asserted-attribute") === None
    * }}}
    *
    * @group entityApi
    * @param key Attribute: ":ns/attr"
    * @tparam T Type of attribute
    * @return Optional typed attribute value
    */
  def apply[T](key: String): Option[T]


  /** Get List of two or more unchecked/untyped attribute values of entity.
    * <br><br>
    * Apply two or more namespaced attribute names to return a List of unchecked/untyped optional attribute values.
    * <br><br>
    * Referenced entities can be cast to an `Option[Map[String, Any]]`.
    * {{{
    *   val List(benId, benAddressId) = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eids
    *
    *   // Type ascription is still unchecked since it is eliminated by erasure
    *   val List(
    *     optName: Option[String],
    *     optAge: Option[Int],
    *     optAddress: Option[Map[String, Any]]
    *   ) = benId(
    *     ":Person/name",
    *     ":Person/age",
    *     ":Person/address"
    *   )
    *
    *   val name: String = optName.getOrElse("no name")
    *
    *   // Type casting necessary to get right value type from Map[String, Any]
    *   val address: Map[String, Any] = optAddress.getOrElse(Map.empty[String, Any])
    *   val street: String = address.getOrElse(":Address/street", "no street").asInstanceOf[String]
    *
    *   name === "Ben"
    *   street === "Hollywood Rd"
    * }}}
    * Typed apply is likely more convenient if typed values are required.
    *
    * @group entityApi
    * @param kw1 First namespaced attribute name: ":[namespace with lowercase first letter]/[attribute name]"
    * @param kw2 Second namespaced attribute name
    * @param kws Further namespaced attribute names
    * @return List of optional unchecked/untyped attribute values
    */
  def apply(kw1: String, kw2: String, kws: String*): List[Option[Any]]


  /** Retract single entity using entity id.
    * <br><br>
    * Given the implicit conversion of Long's in molecule.datomic.base.api.EntityOps to an [[molecule.datomic.base.api.DatomicEntity Entity]] we can
    * can call `retract` on an entity id directly:
    * {{{
    *   // Get entity id of Ben
    *   val benId = Person.e.name_("Ben").get.head
    *
    *   // Retract Ben entity
    *   benId.retract
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
  def retract: TxReport

  /** Asynchronously retract single entity using entity id.
    * <br><br>
    * Given the implicit conversion of Long's in molecule.datomic.base.api.EntityOps to an [[molecule.datomic.base.api.DatomicEntity Entity]] we can
    * can call `retractAsync` on an entity id directly:
    * {{{
    *   // Get entity id of Ben
    *   val benId = Person.e.name_("Ben").get.head
    *
    *   // Retract Ben entity asynchronously
    *   benId.retractAsync.map { tx =>
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
  def retractAsync(implicit ec: ExecutionContext): Future[TxReport]

  /** Get entity retraction transaction data without affecting the database.
    * <br><br>
    * Call `getRetractTx` to retrieve the generated transaction data of the method `retract` on an entity
    * {{{
    *   // Get entity id of Ben
    *   val benId = Person.e.name_("Ben").get.head
    *
    *   // Retraction transaction data
    *   benId.getRetractTx === List(List(RetractEntity(17592186045445)))
    * }}}
    *
    * @group retract
    * @return List[Retractentity[Long]]
    * */
  def getRetractStmts: List[RetractEntity]
//  def getRetractStmts: List[List[RetractEntity]]

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
  def inspectRetract: Unit

  /** Entity retraction transaction meta data constructor.
    * <br><br>
    * Build on from entity with `Tx` and apply a transaction meta data molecule to
    * save transaction meta data with retraction of the entity.
    * {{{
    *   val benId = Person.name("Ben").Tx(MyMetaData.action("add member")).save.eid
    *
    *   // Retract entity with tx meta data
    *   benId.Tx(MyMetaData.action("moved away")).retract
    *
    *   // Query for Ben's history and why he was retracted
    *   Person(benId).name.t.op.Tx(MyMetaData.action).getHistory === List(
    *     ("Ben", 1028, true, "add member"), // Ben added as member
    *     ("Ben", 1030, false, "moved away") // Ben retracted since he moved away
    *   )
    * }}}
    *
    * @group tx
    * @param metaMolecule Transaction meta data molecule
    * @return [[RetractMolecule RetractMolecule]] - a simple wrapper for adding retraction tx meta data
    */
  def Tx(txMeta: Molecule): RetractMolecule

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
    *   benId.touch === Map(
    *     ":db/id" -> 17592186045445L,
    *     ":Person/age" -> 42,
    *     ":Person/address" -> Map(
    *       ":db/id" -> 17592186045446L,
    *       ":Address/street" -> "Hollywood Rd"),
    *     ":Person/name" -> "Ben"
    *   )
    * }}}
    *
    * @group touch
    * @return Map[key: String, value: Any] where value can be a primitive or another nested Map of the entity graph
    */
  def touch: Map[String, Any]

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
    *   benId.touchMax(2) === Map(
    *     ":db/id" -> 17592186045445L,
    *     ":Person/age" -> 42,
    *     ":Person/address" -> Map(
    *       ":db/id" -> 17592186045446L,
    *       ":Address/street" -> "Hollywood Rd"),
    *     ":Person/name" -> "Ben"
    *   )
    *
    *   // 1 level returned
    *   benId.touchMax(1) === Map(
    *     ":db/id" -> 17592186045445L,
    *     ":Person/age" -> 42,
    *     ":Person/address" -> 17592186045446L // Only reference returned
    *     ":Person/name" -> "Ben"
    *   )
    * }}}
    *
    * @group touch
    * @return Map[key: String, value: Any] where value can be a primitive or another nested Map of the entity graph
    */
  def touchMax(maxDepth: Int): Map[String, Any]

  /** Get entity graph as Map-string (for presentation).
    * <br><br>
    * To show the entity graph, this method quotes all text strings so that you can paste the whole graph
    * into any presentation. Pasting it into test code is less useful, since the order of key/value pairs in
    * a Map is not guaranteed. In that case, `touchListQuoted` is recommended since a List guarantees order.
    * <br><br>
    * If entity has reference(s) to other entities it can be a nested graph. Default max levels retrieved is 5.
    *
    *  - Keys of returned Map are namespaced names of attributes
    *  - Values of returned Map are untyped attribute values. For references to other entities,
    *    the value is a Map itself of the referenced entity attributes, etc.
    *
    * {{{
    *   val benId = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eid
    *
    *   benId.touchQuoted ===
    *     """Map(
    *       |  ":db/id" -> 17592186045445L,
    *       |  ":Person/age" -> 42,
    *       |  ":Person/address" -> Map(
    *       |    ":db/id" -> 17592186045446L,
    *       |    ":Address/street" -> "Hollywood Rd"),
    *       |  ":Person/name" -> "Ben")""".stripMargin
    * }}}
    *
    * @group touch
    * @return String
    */
  def touchQuoted: String

  /** Get entity graph to some depth as Map-string (for presentation).
    * <br><br>
    * To show the entity graph, this method quotes all text strings so that you can paste the whole graph
    * into any presentation. Pasting it into test code is less useful, since the order of key/value pairs in
    * a Map is not guaranteed. In that case, `touchListQuoted(maxLevel)` is recommended since a List guarantees order.
    *
    *  - Keys of returned Map are namespaced names of attributes
    *  - Values of returned Map are untyped attribute values. For references to other entities,
    *    the value is a Map itself of the referenced entity attributes, etc.
    *
    * {{{
    *   val benId = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eid
    *
    *   // 2 levels (in this case all levels) returned
    *   benId.touchQuotedMax(2) ===
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
    *   benId.touchQuotedMax(1) ===
    *     """Map(
    *       |  ":db/id" -> 17592186045445L,
    *       |  ":Person/age" -> 42,
    *       |  ":Person/address" -> 17592186045446L,
    *       |  ":Person/name" -> "Ben")""".stripMargin
    * }}}
    *
    * @group touch
    * @return String
    */
  def touchQuotedMax(maxDepth: Int): String


  /** Get entity graph as List.
    * <br><br>
    * If entity has reference(s) to other entities it can be a nested graph. Default max levels retrieved is 5.
    *
    *  - Keys of returned Map are namespaced names of attributes
    *  - Values of returned Map are untyped attribute values. For references to other entities,
    *    the value is a Map itself of the referenced entity attributes, etc.
    *
    * {{{
    *   val benId = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eid
    *
    *   benId.touchList === List(
    *     ":db/id" -> 17592186045445L,
    *     ":Person/age" -> 42,
    *     ":Person/address" -> List(
    *       ":db/id" -> 17592186045446L,
    *       ":Address/street" -> "Hollywood Rd"),
    *     ":Person/name" -> "Ben"
    *   )
    * }}}
    *
    * @group touch
    * @return List[(key: String, value: Any)] where value can be a primitive or another nested List of the entity graph
    */
  def touchList: List[(String, Any)]

  /** Get entity graph to some depth as List.
    *
    *  - Keys of returned Map are namespaced names of attributes
    *  - Values of returned Map are untyped attribute values. For references to other entities,
    *    the value is a Map itself of the referenced entity attributes, etc.
    *
    * {{{
    *   val benId = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eid
    *
    *   // 2 levels returned
    *   benId.touchListMax(2) === List(
    *     ":db/id" -> 17592186045445L,
    *     ":Person/age" -> 42,
    *     ":Person/address" -> List(
    *       ":db/id" -> 17592186045446L,
    *       ":Address/street" -> "Hollywood Rd"),
    *     ":Person/name" -> "Ben"
    *   )
    *
    *   // 1 level returned
    *   benId.touchListMax(1) === List(
    *     ":db/id" -> 17592186045445L,
    *     ":Person/age" -> 42,
    *     ":Person/address" -> 17592186045446L // Only reference returned
    *     ":Person/name" -> "Ben"
    *   )
    * }}}
    *
    * @todo remove overload hack (by avoiding implicit apply method of scala.collection.LinearSeqOptimized ?)
    * @group touch
    * @return List[(key: String, value: Any)] where value can be a primitive or another nested Map of the entity graph
    */
  def touchListMax(maxDepth: Int): List[(String, Any)]

  /** Get entity graph as List-string (for tests).
    * <br><br>
    * If entity has reference(s) to other entities it can be a nested graph. Default max levels retrieved is 5.
    *
    *  - Keys of returned Map are namespaced names of attributes
    *  - Values of returned Map are untyped attribute values. For references to other entities,
    *    the value is a Map itself of the referenced entity attributes, etc.
    *
    * {{{
    *   val benId = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eid
    *
    *   benId.touchListQuoted ===
    *     """List(
    *       |  ":db/id" -> 17592186045445L,
    *       |  ":Person/age" -> 42,
    *       |  ":Person/address" -> List(
    *       |    ":db/id" -> 17592186045446L,
    *       |    ":Address/street" -> "Hollywood Rd"),
    *       |  ":Person/name" -> "Ben")""",stripMargin
    * }}}
    *
    * @group touch
    * @return String
    */
  def touchListQuoted: String

  /** Get entity graph to some depth as List-string (for tests).
    *
    *  - Keys of returned Map are namespaced names of attributes
    *  - Values of returned Map are untyped attribute values. For references to other entities,
    *    the value is a Map itself of the referenced entity attributes, etc.
    *
    * {{{
    *   val benId = Person.name.age.Address.street.insert("Ben", 42, "Hollywood Rd").eid
    *
    *   // 2 levels (in this case all levels) returned
    *   benId.touchListQuotedMax(2) ===
    *     """List(
    *       |  ":db/id" -> 17592186045445L,
    *       |  ":Person/age" -> 42,
    *       |  ":Person/address" -> List(
    *       |    ":db/id" -> 17592186045446L,
    *       |    ":Address/street" -> "Hollywood Rd"),
    *       |  ":Person/name" -> "Ben")""",stripMargin
    *
    *   // 1 level returned
    *   // Note that only reference to Address entity on level 2 is returned
    *   benId.touchListQuotedMax(1) ===
    *     """List(
    *       |  ":db/id" -> 17592186045445L,
    *       |  ":Person/age" -> 42,
    *       |  ":Person/address" -> 17592186045446L,
    *       |  ":Person/name" -> "Ben")""",stripMargin
    * }}}
    *
    * @group touch
    * @return String
    */
  def touchListQuotedMax(maxDepth: Int): String

  protected def asMap(depth: Int, maxDepth: Int): Map[String, Any]

  protected def asList(depth: Int, maxDepth: Int): List[(String, Any)]

  def sortList(l: List[Any]): List[Any]
}
