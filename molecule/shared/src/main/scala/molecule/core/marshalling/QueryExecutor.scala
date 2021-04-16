package molecule.core.marshalling

import scala.concurrent.Future

trait QueryExecutor {

  def query(
    proxyDb: DbProxy,
    datalogQuery: String,
    rules: Seq[String],
    l: Seq[(Int, (String, String))],
    ll: Seq[(Int, Seq[(String, String)])],
    lll: Seq[(Int, Seq[Seq[(String, String)]])],
    maxRows: Int,
    cols: Seq[Column]
  ): Future[Either[String, QueryResult]]
}
