package molecule.datomic.base.facade

import java.util.Date
import molecule.datomic.base.api.Datom

/** Facade to Datomic transaction report with convenience methods to access tx data. */
trait TxReport {


  /** Transaction time t. */
  def t: Long

  /** Transaction entity id (Long). */
  def tx: Long

  /** Transaction instant (Date). */
  def txInstant: Date

  /** List of affected entity ids from transaction */
  def eids: List[Long]

  /** Convenience method to get single affected entity id from transaction.
   *
   * Often useful when you know only one entity was affected:
   * {{{
   *   val benId = Person.name("Ben").eid
   *
   *   // We could have said
   *   val lizId = Person.name("Liz").eids.head
   * }}}
   *
   * @return
   */
  def eid: Long = eids.head

  def txData: List[Datom]

  /** Print TxReport */
  def inspect: Unit
}
