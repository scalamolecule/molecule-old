package molecule.datomic.base.facade.exception

import molecule.core.exceptions.MoleculeException

case class DatomicFacadeException(msg: String) extends RuntimeException(msg)
