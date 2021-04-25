package molecule.datomic.base.facade

import java.io.{Reader, StringReader}
import java.net.URI
import java.util.{Collection => jCollection, List => jList, Map => jMap}
import clojure.lang.{PersistentArrayMap, PersistentVector}
import com.cognitect.transit.impl.URIImpl
import datomic.Peer.function
import datomic.Util
import datomic.Util.{list, read, readAll}
import molecule.core.ast.elements.Model
import molecule.core.transform.ModelTransformer
import molecule.datomic.base.ast.tempDb.TempDb
import molecule.datomic.base.ast.transactionModel.Statement
import molecule.datomic.base.transform.Model2DatomicStmts
import molecule.datomic.base.util.Inspect
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/** Base class for Datomic connection facade
  *
  */
trait Conn_Datomic212 extends Conn_Datomic {

  def q(db: DatomicDb, query: String, inputs: Seq[Any]): List[List[AnyRef]] = {
    val raw = qRaw(db, query, inputs)
    if (raw.isInstanceOf[PersistentVector]
      && !raw.asInstanceOf[PersistentVector].isEmpty
      && raw.asInstanceOf[PersistentVector].nth(0).isInstanceOf[PersistentArrayMap]) {
      raw.asInstanceOf[jCollection[jMap[_, _]]].asScala.toList.map { rows =>
        rows.asScala.toList.map { case (k, v) => k.toString -> v }
      }
    } else {
      raw.asScala.toList
        .map(_.asScala.toList
          .map {
            case set: clojure.lang.PersistentHashSet => set.asScala.toSet
            case uriImpl: URIImpl                    => new URI(uriImpl.toString)
            case bi: clojure.lang.BigInt             => BigInt(bi.toString)
            case other                               => other
          }
        )
    }
  }
}
