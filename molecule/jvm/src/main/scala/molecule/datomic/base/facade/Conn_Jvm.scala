package molecule.datomic.base.facade

import java.io.{Reader, StringReader}
import java.net.URI
import java.util.{Collections, Date, Collection => jCollection, List => jList, Map => jMap}
import clojure.lang.{PersistentArrayMap, PersistentVector}
import com.cognitect.transit.impl.URIImpl
import datomic.Util.{read, readAll}
import datomic.{Peer, Util}
import molecule.core.exceptions.MoleculeException
import molecule.core.util.{Helpers, JavaConversions}
import molecule.datomic.base.ast.transactionModel.{Cas, Enum, RetractEntity, Statement, TempId}
import molecule.datomic.base.util.Inspect
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait Conn_Jvm extends Conn with JavaConversions with Helpers {

  // Molecule api --------------------------------------------------------------

  final def testDbAsOf(t: Long)(implicit ec: ExecutionContext): Future[Unit] =
    cleanFrom(t + 1)

  final def testDbAsOf(txR: TxReport)(implicit ec: ExecutionContext): Future[Unit] =
    cleanFrom(txR.t + 1)

  final def testDbAsOf(d: Date)(implicit ec: ExecutionContext): Future[Unit] = {
    cleanFrom(new Date(d.toInstant.plusMillis(1).toEpochMilli))
  }


  // Datomic facade ------------------------------------------------------------

  final def transact(edn: String)
                    (implicit ec: ExecutionContext): Future[TxReport] =
    transact(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]])


  final override def transact(stmtsReader: Reader)
                             (implicit ec: ExecutionContext): Future[TxReport] =
    transact(readAll(stmtsReader).get(0).asInstanceOf[jList[_]])


  final override def transact(javaStmts: jList[_])(implicit ec: ExecutionContext): Future[TxReport] =
    transactRaw(javaStmts, Future.successful(Seq.empty[Statement]))


  // Schema change -------------------------------------------------------------

  private def attrExists(ident: String)(implicit ec: ExecutionContext): Future[Boolean] = query(
    s"[:find (count ?id) :where [?id :db/ident $ident]]"
  ).map { res =>
    if (res == List(List(1))) true else throw MoleculeException(
      s"Couldn't find attribute `$ident` in the database."
    )
  }

  private def attrHasNoData(ident: String)(implicit ec: ExecutionContext): Future[Unit] = {
    query(s"[:find (count ?v) :where [_ $ident ?v]]").map(data =>
      if (data.nonEmpty) {
        val count  = data.head.head.toString.toInt
        val values = if (count == 1) "value" else "values"
        throw MoleculeException(
          s"Can't retire attribute `$ident` having $count $values asserted. " +
            s"Please retract $values before retiring attribute."
        )
      }
    ).recoverWith { case exc =>
      exc.getMessage match {
        case r".*Unable to resolve entity: :.+/(.+)$attr.*" => Future.failed(
          MoleculeException(s"Couldn't find attribute `$ident` in the database schema.")
        )
        case _                                              => Future.failed(exc)
      }
    }
  }

  def changeAttrName(curName: String, newName: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val (curIdent, newIdent) = (okIdent(curName), okIdent(newName))
    attrExists(curIdent).flatMap(_ => transact(s"[{:db/id $curIdent :db/ident $newIdent}]"))
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def changeNamespaceName(curName: String, newName: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val (curNs, newNs) = (okNsName(curName), okNsName(newName))
    connProxy.nsMap.get(curNs).fold[Future[TxReport]](
      Future.failed(MoleculeException(s"Couldn't find namespace `$curNs`."))
    ) { nsMap =>
      val attrNameChanges = nsMap.attrs.map { metaAttr =>
        val attr = metaAttr.name
        s"{:db/id :$curNs/$attr :db/ident :$newNs/$attr}"
      }.mkString("")
      transact(s"[$attrNameChanges]")
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retireAttr(name: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val ident        = okIdent(name)
    val retiredIdent = ident.replace(":", ":-")
    for {
      _ <- attrExists(ident)
      _ <- attrHasNoData(ident)
      txReport <- transact(s"[{:db/id $ident :db/ident $retiredIdent}]")
    } yield txReport
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retireNamespace(name: String)(implicit ec: ExecutionContext): Future[TxReport] = try {
    val ns = okNsName(name)
    connProxy.nsMap.get(ns).fold[Future[TxReport]](
      Future.failed(MoleculeException(s"Couldn't find namespace `$ns`."))
    ) { nsMap =>
      for {
        _ <- Future.sequence(nsMap.attrs.map(metaAttr => attrHasNoData(s":$ns/${metaAttr.name}")))
        txReport <- {
          // Mark all attribute names in namespace with `-` prefix
          val attrNameChanges = nsMap.attrs.map { metaAttr =>
            val attr = metaAttr.name
            s"{:db/id :$ns/$attr :db/ident :-$ns/$attr}"
          }.mkString("")
          transact(s"[$attrNameChanges]")
        }
      } yield txReport
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }

  def retirePartition(name: String)(implicit ec: ExecutionContext): Future[TxReport] = ???


  // Query ---------------------------------------------------------------------

  final override def query(datalogQuery: String, inputs: Any*)
                          (implicit ec: ExecutionContext): Future[List[List[AnyRef]]] = {
    val javaInputs = inputs.toSeq.map {
      case l: Iterable[_] => Util.list(
        l.map {
          case l2: Iterable[_] => Util.list(l2.map(_.asInstanceOf[AnyRef]).toSeq: _*)
          case v               => v.asInstanceOf[AnyRef]
        }.toSeq: _*
      )
      case v              => v.asInstanceOf[AnyRef]
    }
    rawQuery(datalogQuery, javaInputs).map { raw =>
      if (
        raw.isInstanceOf[PersistentVector]
          && !raw.asInstanceOf[PersistentVector].isEmpty
          && raw.asInstanceOf[PersistentVector].nth(0).isInstanceOf[PersistentArrayMap]
      ) {
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


  // Internal ------------------------------------------------------------------

  private[molecule] val isJsPlatform: Boolean = false

  private[molecule] final override def transact(edn: String, scalaStmts: Future[Seq[Statement]])
                                               (implicit ec: ExecutionContext): Future[TxReport] =
    transactRaw(readAll(new StringReader(edn)).get(0).asInstanceOf[jList[_]], scalaStmts)


  private[molecule] final override def transact(stmtsReader: Reader, scalaStmts: Future[Seq[Statement]])
                                               (implicit ec: ExecutionContext): Future[TxReport] =
    transactRaw(readAll(stmtsReader).get(0).asInstanceOf[jList[_]], scalaStmts)


  private[molecule] final def transact(scalaStmts: Future[Seq[Statement]])
                                      (implicit ec: ExecutionContext): Future[TxReport] = scalaStmts.flatMap { stmts =>
    transactRaw(stmts2java(stmts), scalaStmts)
  }


  private[molecule] final override def inspect(
    header: String,
    threshold: Int,
    max: Int = 9999,
    showStackTrace: Boolean = false,
    maxLevel: Int = 99,
    showBi: Boolean = false
  )(id: Int, params: Any*): Unit =
    Inspect(header, threshold, max, showStackTrace, maxLevel, showBi)(id, params: _*)


  // Needs to be public since tx functions use id
  final override def stmts2java(stmts: Seq[Statement]): jList[jList[_]] = {
    var tempIds = Map.empty[Int, AnyRef]

    def getTempId(part: String, i: Int): AnyRef = tempIds.getOrElse(i, {
      val tempId = Peer.tempid(read(part))
      tempIds = tempIds + (i -> tempId)
      tempId
    })

    def eid(e: Any): AnyRef = (e match {
      case l: Long         => l
      case TempId(part, i) => getTempId(part, i)
      case "datomic.tx"    => "datomic.tx"
      case other           => throw new Exception("Unexpected entity id: " + other)
    }).asInstanceOf[AnyRef]

    def value(v: Any): AnyRef = (v match {
      case i: Int => i.toLong
      //      case f: Float           => f.toDouble
      case TempId(part, i)    => getTempId(part, i)
      case Enum(prefix, enum) => read(prefix + enum)
      case bigInt: BigInt     => bigInt.bigInteger
      case bigDec: BigDecimal => bigDec.bigDecimal
      case other              => other
    }).asInstanceOf[AnyRef]

    val list: jList[jList[_]] = new java.util.ArrayList[jList[_]](stmts.length)
    stmts.foreach {
      case s: RetractEntity =>
        list.add(Util.list(read(s.action), s.e.asInstanceOf[AnyRef]))
      case s: Cas           =>
        list.add(Util.list(read(s.action), s.e.asInstanceOf[AnyRef], read(s.a), value(s.oldV), value(s.v)))
      case s                =>
        list.add(Util.list(read(s.action), eid(s.e), read(s.a), value(s.v)))
    }
    Collections.unmodifiableList(list)
  }

  private[molecule] final def getAttrValues(
    datalogQuery: String,
    card: Int,
    tpe: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    query(datalogQuery).map(_.map(_.head.toString))


  private[molecule] final def getEntityAttrKeys(
    datalogQuery: String
  )(implicit ec: ExecutionContext): Future[List[String]] =
    query(datalogQuery).map(rows => rows.map(_.head.toString))

  // Reset datoms of in-mem with-db from next timePoint after as-of t until end
  protected def cleanFrom(nextTimePoint: Any)(implicit ec: ExecutionContext): Future[Unit]

}
