package molecule.datomic.base.marshalling


/** Simple commands to build packed data as a line-delimited text.
  *
  * Simple special unicode markers are used to structure the data on the server side:
  *
  * ◄  End of multi-line string / None
  * ►  Continue
  * ◄◄ Empty nested data
  *
  * On the client side, the markers enables unpacking the data correctly.
  *
  * OBS: If any of the two special unicode characters are part of a processed text, the packing will fail.
  *
  * https://en.wikipedia.org/wiki/List_of_Unicode_characters
  * ◄ is 0x25C4.toChar
  * ► is 0x25BA.toChar
  */
trait PackBase {

  val sb = new StringBuffer()

  def add(s: String): Unit = {
    sb.append("\n")
    sb.append(s)
    ()
  }

  def end(): Unit = {
    // End of string / None
    sb.append("\n◄")
    ()
  }

  def next(): Unit = {
    // End of string/multi-value
    sb.append("\n►")
    ()
  }

  def nil(): Unit = {
    // Empty sub data
    sb.append("\n◄◄")
    ()
  }
}