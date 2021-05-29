package molecule.core.macros.exception

import molecule.core.exceptions.MoleculeException

case class NestedTuplesException(msg: String) extends RuntimeException(msg)
