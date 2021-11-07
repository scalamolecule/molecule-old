package molecule.datomic.peer.facade


import java.util.concurrent.{BlockingQueue => jBlockingQueue}
import java.util.{LinkedList => jLinkedList, Map => jMap}
import molecule.core.util.JavaConversions
import molecule.datomic.base.facade.{TxReport, TxReportQueue}
import scala.concurrent.duration._


case class TxReportQueue_Peer(
  javaQueue: jBlockingQueue[jMap[_, _]]
) extends TxReportQueue with JavaConversions {

  def drain: List[TxReport] = {
    val txMaps = new jLinkedList[jMap[_, _]]
    javaQueue.drainTo(txMaps)
    val list = List.newBuilder[TxReport]
    txMaps.forEach(txMap => list += TxReport_Peer(txMap))
    list.result()
  }


  def drain(maxReports: Int): List[TxReport] = {
    val txMaps = new jLinkedList[jMap[_, _]]
    javaQueue.drainTo(txMaps, maxReports)
    val list = List.newBuilder[TxReport]
    txMaps.forEach(txMap => list += TxReport_Peer(txMap))
    list.result()
  }


  def poll(timeout: Duration): Option[TxReport] =
    Option(javaQueue.poll(timeout.toNanos, NANOSECONDS)).map(TxReport_Peer(_))


  def poll: Option[TxReport] = Option(javaQueue.poll()).map(TxReport_Peer(_))


  def take: TxReport = TxReport_Peer(javaQueue.take())


  def peek: Option[TxReport] = Option(javaQueue.peek()).map(TxReport_Peer(_))


  def isEmpty: Boolean = javaQueue.isEmpty


  def iterator: Iterator[TxReport] =
    javaQueue.iterator.asScala.map(TxReport_Peer(_))


  def size: Int = javaQueue.size()
}

