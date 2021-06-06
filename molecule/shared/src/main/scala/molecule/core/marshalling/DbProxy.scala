package molecule.core.marshalling

import java.util.UUID
import molecule.datomic.base.ast.dbView.{AsOf, DbView, History, TxLong}


sealed trait DbProxy {
  val uuid      : String
  val schemaPeer: Seq[String]
  val testDbView: Option[DbView]
  val adhocDbView: Option[DbView]
  val attrMap = Map.empty[String, (Int, String)]
}

case class DatomicInMemProxy(
  schemaPeer: Seq[String],
  override val attrMap: Map[String, (Int, String)],
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends DbProxy

case class DatomicPeerProxy(
  protocol: String,
  dbIdentifier: String,
  schemaPeer: Seq[String] = Nil,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends DbProxy

case class DatomicDevLocalProxy(
  system: String,
  storageDir: String,
  dbName: String,
  schemaPeer: Seq[String] = Nil,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends DbProxy

case class DatomicPeerServerProxy(
  accessKey: String,
  secret: String,
  endpoint: String,
  dbName: String,
  schemaPeer: Seq[String] = Nil,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends DbProxy
