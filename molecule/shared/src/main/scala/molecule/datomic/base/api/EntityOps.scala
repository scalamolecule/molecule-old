package molecule.datomic.base.api

import molecule.core.api.Molecule
import molecule.core.ast.elements.{Model, TxMetaData}
import molecule.core.ops.VerifyModel
import molecule.datomic.base.ast.transactionModel.{RetractEntity, Statement}
import molecule.datomic.base.facade.{Conn, TxReport}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/** Operations on multiple entities.
  *
  * @groupname entityOps    Entity operations
  * @groupprio 44
  */
trait EntityOps {

  /** Convenience conversion from entity id to DatomicEntity api
    *
    * @param id
    * @param conn
    * @param ec
    */
  implicit class long2DatomicEntity(
    id: Long
  )(implicit conn: Future[Conn], ec: ExecutionContext) extends DatomicEntity {
    private val datomicEntity: Future[DatomicEntity] = conn.flatMap(_.entity(id))

    def attrs(implicit ec: ExecutionContext): Future[List[String]] =
      datomicEntity.flatMap(_.attrs)

    def apply[T](attr: String)(implicit ec: ExecutionContext): Future[Option[T]] =
      datomicEntity.flatMap(_.apply(attr))

    def apply(attr1: String, attr2: String, moreAttrs: String*)
                      (implicit ec: ExecutionContext): Future[List[Option[Any]]] =
      datomicEntity.flatMap(_.apply(attr1, attr2, moreAttrs: _*))


    def retract(implicit ec: ExecutionContext): Future[TxReport] =
      datomicEntity.flatMap(_.retract)

    def retract(txMeta1: Molecule, txMetaMore: Molecule*)(implicit ec: ExecutionContext): Future[TxReport] =
      datomicEntity.flatMap(_.retract(txMeta1, txMetaMore: _*))

    def getRetractStmts(implicit ec: ExecutionContext): Future[Seq[Statement]] =
      datomicEntity.flatMap(_.getRetractStmts)

    def getRetractStmts(txMeta1: Molecule, txMetaMore: Molecule*)
                       (implicit ec: ExecutionContext): Future[Seq[Statement]] =
      datomicEntity.flatMap(_.getRetractStmts(txMeta1, txMetaMore: _*))

    def inspectRetract(implicit ec: ExecutionContext): Future[Unit] =
      datomicEntity.flatMap(_.inspectRetract)

    def inspectRetract(txMeta1: Molecule, txMetaMore: Molecule*)(implicit ec: ExecutionContext): Future[Unit] =
      datomicEntity.flatMap(_.inspectRetract(txMeta1, txMetaMore: _*))


    def graph(implicit ec: ExecutionContext): Future[Map[String, Any]] =
      datomicEntity.flatMap(_.graph)

    def graphDepth(maxDepth: Int)(implicit ec: ExecutionContext): Future[Map[String, Any]] =
      datomicEntity.flatMap(_.graphDepth(maxDepth))

    def inspectGraph(implicit ec: ExecutionContext): Future[Unit] =
      datomicEntity.flatMap(_.inspectGraph)

    def inspectGraphDepth(maxDepth: Int)(implicit ec: ExecutionContext): Future[Unit] =
      datomicEntity.flatMap(_.inspectGraphDepth(maxDepth))

    def graphCode(maxDepth: Int)(implicit ec: ExecutionContext): Future[String] =
      datomicEntity.flatMap(_.graphCode(maxDepth))
  }


  /** Retract multiple entities with optional transaction meta data.
    * <br><br>
    * 0 or more transaction meta data molecules can be asserted together with a
   * retraction of entities.
    * <br><br>
    * Here we asynchronously retract two comment entities with transaction meta data
   * asserting that the retraction was done by Ben Goodman:
    * {{{
    * retract(Seq(commentEid1, commentEid2), MetaData.user("Ben Goodman"))
    * }}}
    * We can then later see what comments Ben Goodman retracted (`op_(false)`):
    * {{{
    * Comment.e.text.op_(false).Tx(MetaData.user_("Ben Goodman")).getHistory.map(_ ==> List(
    *   (commentEid1, "I like this"),
    *   (commentEid2, "I hate this")
    * )
    * }}}
    *
    * @group entityOps
    * @param eids                Iterable of entity ids of type Long
    * @param txMetaDataMolecules Zero or more transaction meta data molecules
    * @param conn                Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return [[molecule.datomic.base.facade.TxReport TxReport]] with result of retract
    */
  def retract(
    eids: Iterable[Long],
    txMetaDataMolecules: Molecule*
  )(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    conn.flatMap { conn =>
      val retractStmts = Future(eids.toSeq.distinct map RetractEntity)
      if (txMetaDataMolecules.isEmpty) {
        conn.transact(retractStmts)
      } else {
        try {
          val txMetaDataModel = Model(txMetaDataMolecules.map(m => TxMetaData(m._model.elements)))
          VerifyModel(txMetaDataModel, "save") // can throw exception
          val txMetaDataStmts = conn.model2stmts(txMetaDataModel).saveStmts
          conn.transact(Future.sequence(Seq(retractStmts, txMetaDataStmts)).map(_.flatten))
        } catch {
          case NonFatal(exc) => Future.failed(exc)
        }
      }
    }
  }


  /** Inspect retracting multiple entities with optional transaction meta data.
    * <br><br>
    * Without affecting the database, a multiple entity retract action can be
   * inspected by calling the `inspectRetract` method.
    * <br><br>
    * Here we inspect a possible retraction of two comment entities with
   * transaction meta data asserting that the retraction was done by Ben Goodman:
    * {{{
    * inspectRetract(eids, Ref2.str2("meta2"), Ref1.str1("meta1"))
    * }}}
    * This will print inspecting info about the retraction to output (without affecting the database):
    * {{{
    * ## 1 ## molecule.core.Datomic.inspectRetract
    * =============================================================================
    * Model(
    *   TxMetaData(
    *     Atom("Ref2", "str2", "String", 1, Eq(Seq("meta2")), None, Seq(), Seq()))
    *   TxMetaData(
    *     Atom("Ref1", "str1", "String", 1, Eq(Seq("meta1")), None, Seq(), Seq())))
    * -----------------------------------------------------------------------------
    * List(
    *   Add(tx,:Ref2/str2,Values(Eq(Seq("meta2")),None),Card(1)),
    *   Add(tx,:Ref1/str1,Values(Eq(Seq("meta1")),None),Card(1)))
    * -----------------------------------------------------------------------------
    * List(
    *   list(
    *     RetractEntity(17592186045453),
    *     RetractEntity(17592186045454),
    *     Add(datomic.tx,:Ref2/str2,meta2,Card(1)),
    *     Add(datomic.tx,:Ref1/str1,meta1,Card(1))))
    * =============================================================================
    * }}}
    *
    * @group entityOps
    * @param eids                Iterable of entity ids of type Long
    * @param txMetaDataMolecules Zero or more transaction meta data molecules
    * @param conn                Implicit [[molecule.datomic.base.facade.Conn Conn]] value in scope
    * @return Unit (prints to output)
    */
  def inspectRetract(
    eids: Iterable[Long],
    txMetaDataMolecules: Molecule*
  )(implicit conn: Future[Conn], ec: ExecutionContext): Future[Unit] = {
    conn.flatMap { conn =>
      val retractStmts = eids.toSeq.distinct map RetractEntity

      val txMetaDataModel = if (txMetaDataMolecules.isEmpty) {
        Model(Nil)
      } else if (txMetaDataMolecules.size == 1) {
        val txMetaDataModel = Model(Seq(TxMetaData(txMetaDataMolecules.head._model.elements)))
        VerifyModel(txMetaDataModel, "save")
        txMetaDataModel
      } else {
        val txMetaDataModel = Model(
          txMetaDataMolecules.map(m => TxMetaData(m._model.elements))
        )
        VerifyModel(txMetaDataModel, "save")
        txMetaDataModel
      }

      val transformer = conn.model2stmts(txMetaDataModel)
      conn.model2stmts(txMetaDataModel).saveStmts.map { saveStmts =>
        val stmts = Seq(retractStmts ++ saveStmts)
        conn.inspect("molecule.core.Datomic.inspectRetract", 1)(1, txMetaDataModel, transformer.genericStmts, stmts)
      }
    }
  }
}
