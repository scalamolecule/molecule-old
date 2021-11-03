package molecule.datomic.base.ast
import java.util.{Date, List => jList}

private[molecule] object dbView {

  sealed trait PointInTime
  case class TxDate(d: Date) extends PointInTime
  case class TxLong(t: Long) extends PointInTime

  sealed trait DbView
  case class AsOf(tx: PointInTime) extends DbView
  case class Since(tx: PointInTime) extends DbView
  case class With(stmtsEdn: String, uriAttrs: Set[String] = Set.empty[String]) extends DbView
  case object History extends DbView
  case class Sync(t: Long) extends DbView
  case class SyncIndex(t: Long) extends DbView
  case class SyncSchema(t: Long) extends DbView
  case class SyncExcise(t: Long) extends DbView
}
