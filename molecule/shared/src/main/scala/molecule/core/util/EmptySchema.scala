package molecule.core.util

import molecule.core.data.SchemaTransaction
import molecule.datomic.base.ast.metaSchema.MetaSchema

// Dummy empty schema for testing from a clean slate
trait EmptySchema extends SchemaTransaction {

  lazy val datomicPeer = Nil

  lazy val datomicClient = Nil

  lazy val metaSchema = MetaSchema(Nil)

  lazy val nsMap = Map()

  lazy val attrMap = Map()
}

case object EmptySchema extends EmptySchema
