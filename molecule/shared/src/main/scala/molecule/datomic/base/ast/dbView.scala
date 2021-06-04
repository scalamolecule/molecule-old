package molecule.datomic.base.ast
import java.util.{Date, List => jList}

private[molecule] object dbView {

  sealed trait PointInTime
  case class TxDate(d: Date) extends PointInTime
  case class TxLong(t: Long) extends PointInTime

  sealed trait DbView
  case class AsOf(tx: PointInTime) extends DbView
  case class Since(tx: PointInTime) extends DbView
//  case class With(tx: jList[jList[_]]) extends DbView
//  case class WithEdn(stmtsEdn: String, uriAttrs: Set[String]) extends DbView
  case class With(stmtsEdn: String, uriAttrs: Set[String] = Set.empty[String]) extends DbView
//  trait History extends DbView
  case object History extends DbView
}
