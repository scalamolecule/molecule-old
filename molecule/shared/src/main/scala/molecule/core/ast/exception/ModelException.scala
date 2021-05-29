package molecule.core.ast.exception

import molecule.core.exceptions.MoleculeException

case class ModelException(msg: String) extends RuntimeException(msg)
