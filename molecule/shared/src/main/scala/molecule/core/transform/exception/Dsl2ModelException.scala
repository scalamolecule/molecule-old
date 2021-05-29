package molecule.core.transform.exception

import molecule.core.exceptions.MoleculeException

case class Dsl2ModelException(msg: String) extends RuntimeException(msg)
