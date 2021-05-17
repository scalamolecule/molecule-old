package molecule.core.data

import molecule.datomic.base.ast.metaSchema.{MetaNs, MetaSchema}


/** Generated schema transaction data interface
  * */
trait SchemaTransaction {

  /** Auto-generated edn data to transact Datomic Peer schema.
    *
    * One edn text string defines namespaces/attributes, two define partitions
    * and namespaces/attributes.
    * */
  val datomicPeer: Seq[String]


  /** Auto-generated edn data to transact Datomic Client schema.
    *
    * The `datomicClient` schema is a reduced Peer schema where attributes of
    * type `bytes` are filtered out since this is not an available attribute type
    * for Client. Likewise, index and fulltext options are also filtered out.
    *
    * One edn text string defines namespaces/attributes, two define partitions
    * and namespaces/attributes.
    * */
  val datomicClient: Seq[String]


  /** Auto-generated MetaSchema
    * */
  val metaSchema: MetaSchema


  /** Auto-generated map of Ns -> MetaNs
    * */
  val nsMap: Map[String, MetaNs]


  /** Auto-generated map of Attr -> (card, type)
    * */
  val attrMap: Map[String, (Int, String)]
}