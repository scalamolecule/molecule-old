package molecule.datomic.peer.facade

import molecule.datomic.base.api.Datom
import molecule.datomic.base.util.Convert


class Datom_Peer(datom: datomic.Datom) extends Datom(
  datom.e.asInstanceOf[Long],
  datom.a.toString.toInt,
  Convert.datomValue2Scala(datom.v),
  datom.tx.asInstanceOf[Long],
  datom.added,
)
