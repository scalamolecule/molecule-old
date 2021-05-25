package molecule.datomic.base.facade

import java.net.URI
import java.util.{Collection => jCollection, Map => jMap}
import clojure.lang.{PersistentArrayMap, PersistentVector}
import com.cognitect.transit.impl.URIImpl
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

/** Base class for Datomic connection facade.
  *
  */
trait Conn_Datomic213 extends Conn_Datomic {

  def q(
    db: DatomicDb,
    query: String,
    inputs: Seq[Any]
  )(implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = {
    qRaw(db, query, inputs).map { raw =>
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
}
