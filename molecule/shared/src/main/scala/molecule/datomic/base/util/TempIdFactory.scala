package molecule.datomic.base.util

import java.util.concurrent.atomic.AtomicInteger

object TempIdFactory {
  private val baseValue  = -1
  private val nextTempId = new AtomicInteger(baseValue)

  def reset(): Unit = nextTempId.set(baseValue)

  def next: Int = nextTempId.getAndDecrement()

  def cur: Int = nextTempId.get()
}
