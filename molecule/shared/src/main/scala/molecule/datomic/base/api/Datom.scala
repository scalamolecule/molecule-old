package molecule.datomic.base.api

case class Datom(
  e: Long,
  a: Int,
  v: Any,
  tx: Long,
  added: Boolean
)