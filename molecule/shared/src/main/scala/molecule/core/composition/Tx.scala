package molecule.core.composition

import molecule.core.dsl.base.Init
import scala.language.higherKinds

/** Transaction meta data on molecule.
 * <br><br>
 * `Tx` takes a transaction meta data molecule with attributes having the transaction id as their entity id.
 * {{{
 * for {
 *   // Save molecule with transaction data
 *   _ <- Person.name("Ben").Tx(MyMetaData.action("add member")).save
 *
 *   // Query for data with transaction meta data - "which persons became members"
 *   _ <- Person.name.Tx(MyMetaData.action_("add member")).get.map(_ ==> List("Ben"))
 * } yield ()
 * }}}
 */
trait Tx

trait Tx_[props] {
  def Tx: props = ???
}