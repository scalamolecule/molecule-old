package molecule.core.pagination

import java.lang.{Long => jLong}
import java.net.URI
import java.util
import java.util.{Base64, Date, UUID, List => jList}
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.marshalling.Marshalling
import molecule.datomic.base.ast.dbView.{AsOf, TxLong}
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.Model2Query
import scala.annotation.{nowarn, tailrec}
import scala.concurrent.{ExecutionContext, Future}


trait OffsetPagination[Obj, Tpl] { self: Marshalling[Obj, Tpl] =>

  final protected def allFlatRows2selectedFlatRows(
    sortedRows: util.ArrayList[jList[AnyRef]],
    limit: Int,
    offset: Int
  ): (util.ArrayList[jList[AnyRef]], Int) = {
    var topRowIndex   = -1
    var curId : jLong = 0
    var prevId: jLong = 0
    if (limit == 0) {
      // All rows (offset is 0)
      sortedRows.forEach { row =>
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        prevId = curId
      }
      return (sortedRows, topRowIndex + 1)
    }

    val totalCount         = sortedRows.size
    val selectedRows       = new util.ArrayList[jList[AnyRef]]()
    var row: jList[AnyRef] = null
    var continue           = true

    if (limit > 0) {
      // From start ..........
      val until = offset + limit
      var i     = 0
      while (continue && i != totalCount) {
        row = sortedRows.get(i)
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        if (topRowIndex >= offset && topRowIndex < until) {
          // append row
          selectedRows.add(row)
        } else if (topRowIndex == until) {
          continue = false
        }
        prevId = curId
        i += 1
      }
      (selectedRows, topRowIndex + 1)

    } else {
      // From end ..........
      val until = offset - limit // (limit is negative)
      var i     = totalCount - 1
      while (continue && i != -1) {
        row = sortedRows.get(i)
        curId = row.get(0).asInstanceOf[jLong]
        if (curId != prevId) {
          topRowIndex += 1
        }
        if (topRowIndex >= offset && topRowIndex < until) {
          // prepend row
          selectedRows.add(0, row)
        } else if (topRowIndex == until) {
          continue = false
        }
        prevId = curId
        i -= 1
      }
      (selectedRows, topRowIndex + 1)
    }
  }
}
