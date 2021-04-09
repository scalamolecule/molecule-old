package molecule.core.marshalling

case class Col(
  colIndex: Int,
  related: Int,
  nsAlias: String,
  nsFull: String,
  attr: String,
  attrType: String,
  colType: String,
  card: Int,
  opt: Boolean = false,
  enums: Seq[String] = Nil,
  aggrType: String = "",
  attrExpr: String = "",
  sortDir: String = "",
  sortPos: Int = 0,
  filterExpr: String = "",
  kind: String = ""
)
//  extends HelpersAdmin {
//
//  override def toString = {
//    s"""Col($colIndex, $related, "$nsAlias", "$nsFull", "$attr", "$attrType", "$colType", $card, $opt, ${seq(enums)}, "$aggrType", "$attrExpr", "$sortDir", $sortPos, "$filterExpr", "$kind")"""
//  }
//}