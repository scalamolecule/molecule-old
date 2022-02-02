package molecule.core.macros

import molecule.core.data.SchemaTransaction
import molecule.core.ops.Liftables
import sbtmolecule.DataModelParser
import sbtmolecule.schema.SchemaTransactionCode
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

class TransactSchemaCode(val c: blackbox.Context) extends Liftables {

  import c.universe._

  def schema_impl(dataModelContent: Tree): Tree = {

    val pos    = dataModelContent.pos
    val source = pos.source.content
    val code   = if (pos.isRange) {
      // skip outer curly braces of macro call itself
      new String(source.slice(pos.start + 1, pos.start + pos.end - pos.start - 1)).trim
    } else ""

    val dataModel = {
      s"""package dummy.path.dataModel
         |
         |import molecule.core.data.model._
         |
         |@InOut(0, 1)
         |object DummyDataModel {
         |  $code
         |}""".stripMargin
    }
    val model     = DataModelParser("dummy", dataModel.split("\n").toList, true, "").parse
    val schema    = SchemaTransactionCode(model)

    //    c.error(c.enclosingPosition, schema.getCode)

    q"""{
      import molecule.core.data.SchemaTransaction
      import molecule.datomic.base.ast.metaSchema._

      object DummySchema extends SchemaTransaction {
        lazy val datomicPeer   = Seq(..${schema.datomicPeerValues})
        lazy val datomicClient = Seq(..${schema.datomicClientValues})
        lazy val metaSchema    = ${schema.metaSchema}
        lazy val nsMap         = ${schema.metaSchema.nsMapValues}
        lazy val attrMap       = ${schema.metaSchema.attrMapValues}
      }
      DummySchema
    }"""
  }
}

object GetTransactSchema {
  def schema(dataModelContent: Unit): SchemaTransaction = macro TransactSchemaCode.schema_impl
}