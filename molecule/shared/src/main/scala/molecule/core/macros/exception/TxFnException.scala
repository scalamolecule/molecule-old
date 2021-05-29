package molecule.core.macros.exception

import molecule.core.exceptions.MoleculeException

case class TxFnException(msg: String) extends RuntimeException(msg)
