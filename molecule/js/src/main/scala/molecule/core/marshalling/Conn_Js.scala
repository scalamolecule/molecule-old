package molecule.core.marshalling

import boopickle.Default.compositePickler
import molecule.core.data.SchemaTransaction
import molecule.core.ops.ColOps
import molecule.core.util.Helpers
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.transform.Query2String
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

/** Client db connection.
  *
  * Used to cary information enabling marshalling on both client and server side.
  *
  * Make a similar subclass of ConnProxy like this one in order to use an
  * alternative moleculeRpc implementation.
  *
  * @param dbProxy0 Db coordinates to access db on server side
  */
trait Conn_Js extends ConnProxy with ColOps with Helpers {

  val isJsPlatform: Boolean = true

  override lazy val moleculeRpc: MoleculeRpc = MoleculeWebClient.moleculeRpc


  private[molecule] override def qAsync[Tpl](
    query: Query,
    n: Int,
    indexes: List[(Int, Int, Int, Int)],
    qr2tpl: QueryResult => Int => Tpl
  )(implicit ec: ExecutionContext): Future[List[Tpl]] = {
    condense(
      Future {
        val q2s          = Query2String(query)
        val datalogQuery = q2s.multiLine(60)
        val p            = q2s.p
        val rules        = if (query.i.rules.isEmpty) Nil else Seq("[" + (query.i.rules map p mkString " ") + "]")
        val (l, ll, lll) = marshallInputs(query)
        // Fetch QueryResult with Ajax call via typed Sloth wire
        moleculeRpc.queryAsync(dbProxy, datalogQuery, rules, l, ll, lll, n, indexes)
          .recover { exc => Left(exc) }
          .map {
            case Right(qr) =>
              val maxRows    = if (n == -1) qr.maxRows else n
              val tplsBuffer = new ListBuffer[Tpl]
              val columns    = qr2tpl(qr) // macro generated extractor
              var rowIndex   = 0
              while (rowIndex < maxRows) {
                tplsBuffer += columns(rowIndex)
                rowIndex += 1
              }
              Right(tplsBuffer.toList)

            case Left(err) => Left(err) // error from QueryExecutor
          }
      }.flatten
    )
  }
}

object Conn_Js {
  def apply(dbProxy0: DbProxy): Conn_Js = new Conn_Js {
    override lazy val dbProxy = dbProxy0
  }

  def inMem(schemaTransaction: SchemaTransaction): Conn_Js = apply(
    DatomicInMemProxy(
      schemaTransaction.datomicPeer,
      schemaTransaction.attrMap
    )
  )
}