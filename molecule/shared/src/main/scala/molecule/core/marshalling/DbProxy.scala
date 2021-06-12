package molecule.core.marshalling

import java.util.UUID
import molecule.datomic.base.ast.dbView.{AsOf, DbView, History, TxLong}


sealed trait DbProxy {
  val uuid      : String
  val schemaPeer: Seq[String]
  val attrMap = Map.empty[String, (Int, String)]

  /** 0 inactive
    * 1 activate
    * 2 active
    * -1 de-activate
    */
  val testDbStatus: Int
  val testDbView  : Option[DbView]
  val adhocDbView: Option[DbView]
}

case class DatomicInMemProxy(
  schemaPeer: Seq[String],
  override val attrMap: Map[String, (Int, String)],
  testDbStatus: Int = 0,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends DbProxy

case class DatomicPeerProxy(
  protocol: String,
  dbIdentifier: String,
  schemaPeer: Seq[String] = Nil,
  testDbStatus: Int = 0,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends DbProxy

case class DatomicDevLocalProxy(
  system: String,
  storageDir: String,
  dbName: String,
  schemaPeer: Seq[String] = Nil,
  testDbStatus: Int = 0,
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
  testDbStatus: Int = 0,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends DbProxy
