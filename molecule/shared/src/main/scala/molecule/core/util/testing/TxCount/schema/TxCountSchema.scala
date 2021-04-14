/*
* AUTO-GENERATED Datomic Schema generation boilerplate code
*
* To change:
* 1. edit data model file in `app.dataModel/`
* 2. `sbt compile` in terminal
* 3. Refresh and re-compile project in IDE
*/
package molecule.core.util.testing.TxCount.schema

import molecule.core.data.SchemaTransaction
import molecule.datomic.base.ast.metaSchema.{MetaAttr, MetaNs, MetaPart, MetaSchema}

object TxCountSchema extends SchemaTransaction {

  lazy val datomicPeer = Seq(
    """
     [
       ;; TxCount -------------------------------------------

       {:db/ident         :TxCount/db
        :db/valueType     :db.type/string
        :db/cardinality   :db.cardinality/one
        :db/doc           "Database name"
        :db/index         true}

       {:db/ident         :TxCount/basisT
        :db/valueType     :db.type/long
        :db/cardinality   :db.cardinality/one
        :db/doc           "Datomic basis T"
        :db/index         true}
     ]""")


  lazy val datomicClient = Seq(
    """
     [
       ;; TxCount -------------------------------------------

       {:db/ident         :TxCount/db
        :db/valueType     :db.type/string
        :db/cardinality   :db.cardinality/one
        :db/doc           "Database name"}

       {:db/ident         :TxCount/basisT
        :db/valueType     :db.type/long
        :db/cardinality   :db.cardinality/one
        :db/doc           "Datomic basis T"}
     ]""")


  lazy val metaSchema = MetaSchema(Seq(
    MetaPart(0, "db.part/user", None, None, Seq(
      MetaNs(0, "TxCount", "TxCount", None, None, Seq(
        MetaAttr(0, "db"    , 1, "String", Seq(), None, Seq(), None, None, None, None, None, Nil),
        MetaAttr(1, "basisT", 1, "Long"  , Seq(), None, Seq(), None, None, None, None, None, Nil)))))))


  lazy val nsMap = Map(
    "TxCount" -> MetaNs(0, "TxCount", "TxCount", None, None, Seq(
      MetaAttr(0, "db"    , 1, "String", Seq(), None, Seq(), None, None, None, None, None, Nil),
      MetaAttr(1, "basisT", 1, "Long"  , Seq(), None, Seq(), None, None, None, None, None, Nil))))
}