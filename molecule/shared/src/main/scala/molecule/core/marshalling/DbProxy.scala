package molecule.core.marshalling

import java.util.UUID


sealed trait DbProxy {
  val uuid: String
  val attrMap = Map.empty[String, (Int, String)]
}

case class DatomicInMemProxy(
  edns: Seq[String],
  override val attrMap: Map[String, (Int, String)],
  uuid: String = UUID.randomUUID().toString
) extends DbProxy

case class DatomicPeerProxy(
  protocol: String,
  dbIdentifier: String,
  edns: Seq[String] = Nil,
  uuid: String = UUID.randomUUID().toString
) extends DbProxy

case class DatomicDevLocalProxy(
  system: String,
  storageDir: String,
  dbName: String,
  edns: Seq[String] = Nil,
  uuid: String = UUID.randomUUID().toString
) extends DbProxy

case class DatomicPeerServerProxy(
  accessKey: String,
  secret: String,
  endpoint: String,
  dbName: String,
  uuid: String = UUID.randomUUID().toString
) extends DbProxy
