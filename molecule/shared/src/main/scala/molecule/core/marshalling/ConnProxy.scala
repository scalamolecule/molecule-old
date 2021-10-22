package molecule.core.marshalling

import java.util.UUID
import molecule.datomic.base.ast.dbView.{AsOf, DbView, History, TxLong}


sealed trait ConnProxy {
  val uuid   : String
  val schema : Seq[String]
  val attrMap: Map[String, (Int, String)]

  /** 0 inactive
    * 1 active
    * -1 de-activate signal
    */
  val testDbStatus: Int
  val testDbView  : Option[DbView]
  val adhocDbView : Option[DbView]
}

case class DatomicPeerProxy(
  protocol: String,
  dbIdentifier: String,
  schema: Seq[String] = Nil,
  attrMap: Map[String, (Int, String)] = Map.empty[String, (Int, String)],
  testDbStatus: Int = 0,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends ConnProxy

case class DatomicDevLocalProxy(
  protocol: String,
  system: String,
  storageDir: String,
  dbName: String,
  schema: Seq[String] = Nil,
  attrMap: Map[String, (Int, String)] = Map.empty[String, (Int, String)],
  testDbStatus: Int = 0,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends ConnProxy

case class DatomicPeerServerProxy(
  accessKey: String,
  secret: String,
  endpoint: String,
  dbName: String,
  schema: Seq[String] = Nil,
  attrMap: Map[String, (Int, String)] = Map.empty[String, (Int, String)],
  testDbStatus: Int = 0,
  testDbView: Option[DbView] = None,
  adhocDbView: Option[DbView] = None,
  uuid: String = UUID.randomUUID().toString
) extends ConnProxy
