/*
* AUTO-GENERATED Datomic Schema exchange boilerplate code
*
* To change:
* 1. edit schema definition file in `app.schema/`
* 2. `sbt compile` in terminal
* 3. Refresh and re-compile project in IDE
*/
package molecule.core.util.testing.TxCount.schema

object TxCountSchemaLowerToUpper {

  lazy val edn =
    """
      ;; TxCount -------------------------------------------

      { :db/id :txCount/db       :db/ident :TxCount/db     }
      { :db/id :txCount/basisT   :db/ident :TxCount/basisT }
    """
}