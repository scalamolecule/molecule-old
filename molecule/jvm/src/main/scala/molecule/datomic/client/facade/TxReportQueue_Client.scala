package molecule.datomic.client.facade

import java.util.concurrent.{BlockingQueue => jBlockingQueue}
import java.util.{LinkedList => jLinkedList, Map => jMap}
import datomicScala.client.api.sync.{TxReport => ClientTxReport}
import molecule.core.util.JavaConversions
import molecule.datomic.base.facade.{TxReport, TxReportQueue}
import scala.concurrent.duration._


case class TxReportQueue_Client(
  javaQueue: jBlockingQueue[jMap[_, _]]
) extends TxReportQueue with JavaConversions {


  def drain: List[TxReport] = {
    val txMaps = new jLinkedList[jMap[_, _]]
    javaQueue.drainTo(txMaps)
    val list = List.newBuilder[TxReport]
    txMaps.forEach(txMap => list +=  TxReport_Client(ClientTxReport(txMap)))
    list.result()
  }


  def drain(maxReports: Int): List[TxReport] = {
    val txMaps = new jLinkedList[jMap[_, _]]
    javaQueue.drainTo(txMaps, maxReports)
    val list = List.newBuilder[TxReport]
    txMaps.forEach(txMap => list +=  TxReport_Client(ClientTxReport(txMap)))
    list.result()
  }


  def poll(timeout: Duration): Option[TxReport] =
    Option(javaQueue.poll(timeout.toNanos, NANOSECONDS))
      .map(txMap => TxReport_Client(ClientTxReport(txMap)))


  def poll: Option[TxReport] =
    Option(javaQueue.poll()).map(txMap => TxReport_Client(ClientTxReport(txMap)))


  def take: TxReport = TxReport_Client(ClientTxReport(javaQueue.take()))


  def peek: Option[TxReport] =
    Option(javaQueue.peek()).map(txMap => TxReport_Client(ClientTxReport(txMap)))


  def isEmpty: Boolean = javaQueue.isEmpty


  def iterator: Iterator[TxReport] =
    javaQueue.iterator.asScala.map(txMap => TxReport_Client(ClientTxReport(txMap)))


  def size: Int = javaQueue.size()
}

