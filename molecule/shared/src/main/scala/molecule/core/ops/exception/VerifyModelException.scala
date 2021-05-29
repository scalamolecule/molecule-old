package molecule.core.ops.exception

import molecule.core.exceptions.MoleculeException

case class VerifyModelException(msg: String) extends RuntimeException(msg)
