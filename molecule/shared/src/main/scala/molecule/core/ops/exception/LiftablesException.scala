package molecule.core.ops.exception

import molecule.core.exceptions.MoleculeException

case class LiftablesException(msg: String) extends RuntimeException(msg)
