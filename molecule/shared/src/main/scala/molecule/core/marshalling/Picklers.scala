package molecule.core.marshalling

import boopickle.Default.compositePickler
import molecule.datomic.base.facade.TxReport
import playing.sloth.Serializations

trait Picklers extends Serializations{

  implicit val txReportPickler = compositePickler[TxReport]
}
