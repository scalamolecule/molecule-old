package molecule.core.marshalling

case class IndexArgs(
  e: Long = -1L,
  a: String = "",
  v: String = "", // index `v` or indexRange `from`
  tpe: String = "",
  t: Long = -1L, // t or tx
  inst: Long = -1L, // Date.getTime
  v2: String = "", // `until` value
  tpe2: String = "" // `until` type
)
