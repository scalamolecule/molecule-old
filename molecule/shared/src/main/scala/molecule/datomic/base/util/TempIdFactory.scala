package molecule.datomic.base.util

object TempIdFactory {
  private var nextTempId = -1000000

  def reset(): Unit = nextTempId = -1000000

  def next: Int = {
    nextTempId -= 1
    nextTempId
  }

  def cur: Int = nextTempId
}
