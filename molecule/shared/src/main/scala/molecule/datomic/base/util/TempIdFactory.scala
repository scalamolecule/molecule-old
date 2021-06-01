package molecule.datomic.base.util

import java.util.concurrent.atomic.AtomicInteger

object TempIdFactory {
  val baseValue  = 1
  val nextTempId = new AtomicInteger(baseValue)

  def reset(): Unit = nextTempId.set(baseValue)

  def next: Int = nextTempId.getAndIncrement()

  def cur: Int = nextTempId.get()
}
