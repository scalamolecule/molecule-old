package molecule.datomic.base.transform.exception

import molecule.core.exceptions.MoleculeException

case class Model2QueryException(msg: String) extends RuntimeException(msg)
