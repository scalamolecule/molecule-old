/*
* AUTO-GENERATED Datomic Schema generation boilerplate code
*
* To change:
* 1. edit data model file in `moleculeTests.dataModels.core.base.dataModel/`
* 2. `sbt compile` in terminal
* 3. Refresh and re-compile project in IDE
*/
package moleculeTests

import molecule.core.data.SchemaTransaction
import molecule.datomic.base.ast.metaSchema._

object EmptySchema extends SchemaTransaction {

  lazy val datomicPeer = Seq("[]")

  lazy val datomicClient = Seq("[]")

  lazy val metaSchema = MetaSchema(Nil)

  lazy val nsMap = Map()

  lazy val attrMap = Map()
}