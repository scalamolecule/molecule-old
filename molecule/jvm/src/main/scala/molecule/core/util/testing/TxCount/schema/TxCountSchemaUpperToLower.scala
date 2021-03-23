/*
* AUTO-GENERATED Datomic Schema exchange boilerplate code
*
* To change:
* 1. edit schema definition file in `app.schema/`
* 2. `sbt compile` in terminal
* 3. Refresh and re-compile project in IDE
*/
package molecule.core.util.testing.TxCount.schema

object TxCountSchemaUpperToLower {

  lazy val edn =
    """
      ;; TxCount -------------------------------------------

      { :db/id :TxCount/db       :db/ident :txCount/db     }
      { :db/id :TxCount/basisT   :db/ident :txCount/basisT }
    """

}