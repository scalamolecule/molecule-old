package molecule.core.ops.exception

import molecule.core.exceptions.MoleculeException

case class TreeOpsException(msg: String) extends RuntimeException(msg)
