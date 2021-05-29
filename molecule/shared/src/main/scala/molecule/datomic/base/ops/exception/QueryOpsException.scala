package molecule.datomic.base.ops.exception

import molecule.core.exceptions.MoleculeException

case class QueryOpsException(msg: String) extends RuntimeException(msg)
