package molecule.datomic.client.facade

import molecule.datomic.base.api.Datom
import molecule.datomic.base.util.Convert
import datomicScala.client.api.{Datom => ClientDatom}


class Datom_Client(datom: ClientDatom) extends Datom(
  datom.e,
  datom.a.toString.toInt,
  Convert.datomValue2Scala(datom.v),
  datom.tx,
  datom.added,
)