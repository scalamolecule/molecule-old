package molecule.datomic.base.facade

import scala.concurrent.duration._

/** A transaction report queue associated with a connection.
 *
 * The molecule TxReportQueue is a wrapper of the
 * [[java.util.concurrent.BlockingQueue BlockingQueue]] returned by a Datomic
 * Peer Connection.
 *
 * This queue may be safely consumed from more than one thread.
 * Note that the queue does not block producers, and will consume
 * memory until you consume the elements from it.
 *
 * A common use is to listen for incoming transactions and do something with them:
 * {{{
 *   val conn = Conn_Peer(...)
 *   val queue = conn.txReportQueue
 *   // Run TxReportQueue operations in separate thread
 *   Future {
 *     while (true) {
 *       try {
 *         // `take` pops off the head tx report of the queue. It blocks until new
 *         // reports of transactions are received.
 *         val headTxReport = queue.take
 *         val datomsInTx = headTxReport.txData
 *         // do stuff with datoms...
 *       } catch {
 *         case _: InterruptedException => println("interrupted")
 *       }
 *     }
 *   }
 *
 *   // Remove the queue associated with the connection when finished listening.
 *   conn.removeTxReportQueue()
 * }}}
 *
 * @see [[https://docs.datomic.com/on-prem/javadoc/datomic/Connection.html#txReportQueue-- datomic.Connection.txReportQueue()]]
 */
trait TxReportQueue {

  /** Removes all available transaction reports from
   * this queue and returns them as a list.
   *
   * This operation may be more efficient than repeatedly
   * polling this queue.
   *
   * @return a list of all available tranaction reports.
   */
  def drain: List[TxReport]


  /** Removes at most the given number of available
   * transaction reports from this queue and returns
   * them as a list.
   *
   * This operation may be more efficient than repeatedly
   * polling this queue.
   *
   * @param  maxReports  the maximum number of reports to transfer.
   * @return a list of all available tranaction reports.
   */
  def drain(maxReports: Int): List[TxReport]


  /** Retrieves and removes the head of this queue,
   * waiting up to the specified wait time
   * if necessary for an element to become available.
   *
   * Throws [[http://docs.oracle.com/javase/7/docs/api/java/lang/InterruptedException.html InterruptedException]]  if interrupted while waiting.
   *
   * @param  timeout  the duration of time to wait before giving up.
   * @return the head of this queue, or `None` if the specified
   *     waiting time elapses before an element is available.
   */
  def poll(timeout: Duration): Option[TxReport]


  /** Retrieves and removes the head of this queue,
   * or returns `None` if this queue is empty.
   *
   * @return the head of this queue, or `None`
   *     if this queue is empty.
   */
  def poll: Option[TxReport]


  /** Retrieves and removes the head of this queue,
   * waiting if necessary until an element becomes available.
   *
   * Throws [[http://docs.oracle.com/javase/7/docs/api/java/lang/InterruptedException.html InterruptedException]]  if interrupted while waiting.
   *
   * @return the head of this queue.
   */
  def take: TxReport


  /** Retrieves, but does not remove, the head of this queue,
   * or `None` if this queue is empty.
   *
   * @return the head of this queue, or `None` if this
   *     queue is empty.
   */
  def peek: Option[TxReport]


  /** Returns `true` if this queue contains no transaction reports.
   *
   * @return `true` if this queue contains no transaction reports.
   */
  def isEmpty: Boolean


  /** Returns an iterator over the transaction reports in
   * this queue.
   *
   * @return an `Iterator` over the transaction reports
   *     in this queue.
   */
  def iterator: Iterator[TxReport]


  /** Returns the number of transaction reports in the queue.
   *
   * @return the number of transaction reports in the queue.
   */
  def size: Int
}
