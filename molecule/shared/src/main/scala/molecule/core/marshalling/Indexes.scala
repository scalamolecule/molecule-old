package molecule.core.marshalling

import boopickle.Default._

sealed trait IndexContainer

case class Indexes(
  attr: String,
  colIndex: Int,
  castIndex: Int,
  arrayType: Int,
  arrayIndex: Int,
  post: Boolean = false,
  nested: List[IndexContainer] = Nil
) extends IndexContainer

object IndexContainer {
  implicit val indexesPickler = compositePickler[IndexContainer]
  indexesPickler.addConcreteType[Indexes]
}
