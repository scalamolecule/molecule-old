package molecule.core.marshalling

sealed trait DbProxy

case class DatomicInMemProxy(
  edns: Seq[String],
  dbIdentifier: String = ""
) extends DbProxy

case class DatomicPeerProxy(
  protocol: String,
  dbIdentifier: String,
  edns: Seq[String]
) extends DbProxy

case class DatomicDevLocalProxy(
  system: String,
  storageDir: String,
  dbName: String,
  edns: Seq[String]
) extends DbProxy

case class DatomicPeerServerProxy(
  accessKey: String,
  secret: String,
  endpoint: String,
  dbName: String
) extends DbProxy
