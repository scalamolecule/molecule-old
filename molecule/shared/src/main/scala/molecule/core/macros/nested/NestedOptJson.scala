package molecule.core.macros.nested

import java.util.{List => jList, Collection => jCollection}
import molecule.core.api.Molecule_0
import molecule.core.ast.elements.Composite
import molecule.core.macros.attrResolvers.JsonBase
import molecule.datomic.base.facade.Conn
import scala.concurrent.{ExecutionContext, Future}

/** Builder classes of various arity of nested JSON. */
trait NestedOptJson[Obj, Tpl] extends JsonBase { self: Molecule_0[Obj, Tpl] =>

  final override def getJson(n: Int)(implicit futConn: Future[Conn], ec: ExecutionContext): Future[String] = {
    _inputThrowable.fold(
      for {
        conn <- futConn
        rows0 <- if (conn.isJsPlatform)
          conn.queryFlatJs(_query, -1, indexes, qr2list)
        else
          conn.query(_model, _query)
      } yield {
        val rows = rows0.asInstanceOf[jCollection[jList[AnyRef]]]
        val count = rows.size
        val sb    = new StringBuffer()
        var next  = false

        if (count == 0) {
          // empty result set

        } else if (n == -1) {
          rows.forEach { row =>
            if (next) sb.append(",") else next = true
            row2json(sb, row)
          }
          sb.append("\n    ")

        } else {
          val it = rows.iterator()
          var i  = 0
          while (it.hasNext && i < n) {
            if (next) sb.append(",") else next = true
            row2json(sb, it.next)
            i += 1
          }
          sb.append("\n    ")
        }

        _model.elements.head match {
          case _: Composite =>
            s"""{
               |  "data": {
               |    "composite": [${sb.toString()}]
               |  }
               |}""".stripMargin
          case _            =>
            s"""{
               |  "data": {
               |    "${firstNs(_model)}": [${sb.toString()}]
               |  }
               |}""".stripMargin
        }
      }
    )(Future.failed) // Pass on exception from input failure
  }
}