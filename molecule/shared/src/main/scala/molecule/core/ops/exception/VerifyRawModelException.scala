package molecule.core.ops.exception

import molecule.core.exceptions.MoleculeException

case class VerifyRawModelException(msg: String) extends RuntimeException(msg)
