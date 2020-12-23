package molecule.core.facade

import java.net.URI
import java.util.{Collection => jCollection, Map => jMap}
import clojure.lang.{PersistentArrayMap, PersistentVector}
import com.cognitect.transit.impl.URIImpl
import molecule.datomic.base.facade.{Conn, DatomicDb}
import scala.jdk.CollectionConverters._


trait Conn_Datomic extends Conn {

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
