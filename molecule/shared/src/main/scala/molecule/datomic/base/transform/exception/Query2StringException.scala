package molecule.datomic.base.transform.exception

import molecule.core.exceptions.MoleculeException

case class Query2StringException(msg: String) extends RuntimeException(msg)
