package molecule.datomic.base.marshalling.pack

trait PackBase {

  val sb = new StringBuffer()

  def add(s: String): Unit = {
    sb.append("\n")
    sb.append(s)
    ()
  }
  def end(): Unit = {
    // mark end of string/multi-value
    sb.append("\n◄")
    ()
  }
  def nil(): Unit = {
    // mark end of string/multi-value
    sb.append("\n◄◄")
    ()
  }
  def rowSeparator(): Unit = {
    // mark end of string/multi-value
    sb.append("\n-----------")
    ()
  }
}
