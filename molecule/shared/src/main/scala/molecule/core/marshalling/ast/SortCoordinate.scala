package molecule.core.marshalling.ast

case class SortCoordinate(
  i: Int,
  asc: Boolean,
  attr: String,
  opt: Boolean,
  tpe: String,
  aggrFn: String = "",
  aggrLimit: Option[Int] = None
)
