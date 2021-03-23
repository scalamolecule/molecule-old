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
}