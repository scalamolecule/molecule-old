package molecule.datomic.peer.facade

import java.lang.{Boolean => jBoolean, Long => jLong}
import java.util.{Collections, Comparator, Date, Collection => jCollection, List => jList}
import java.{lang => jl, util => ju}
import datomic.Connection.DB_AFTER
import datomic.Peer._
import datomic._
import molecule.core.ast.elements._
import molecule.core.exceptions._
import molecule.core.marshalling._
import molecule.datomic.base.api.DatomicEntity
import molecule.datomic.base.ast.dbView._
import molecule.datomic.base.ast.query.Query
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade._
import molecule.datomic.base.marshalling.DatomicRpc
import molecule.datomic.base.transform.Query2String
import molecule.datomic.base.util.QueryOpsClojure
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.control.NonFatal


/** Facade to Datomic connection for peer api.
 * */
trait QueryIndex { self: Conn_Peer =>


  // Datoms API providing direct access to indexes
  private[molecule] final override def indexQuery(
    model: Model
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = Future {
    try {
      val (api, index, args) = model.elements.head match {
        case Generic("EAVT", _, _, value, _) =>
          ("datoms", datomic.Database.EAVT, value match {
            case NoValue                   => Seq()
            case Eq(Seq(e))                => Seq(e)
            case Eq(Seq(e, a))             => Seq(e, a)
            case Eq(Seq(e, a, v))          => Seq(e, a, v)
            case Eq(Seq(e, a, v, d: Date)) => Seq(e, a, v, d)
            case Eq(Seq(e, a, v, t))       => Seq(e, a, v, t)
            case v                         => throw MoleculeException("Unexpected EAVT value: " + v)
          })

        case Generic("AEVT", _, _, value, _) =>
          ("datoms", datomic.Database.AEVT, value match {
            case NoValue                   => Seq()
            case EntValue                  => Seq()
            case Eq(Seq(a))                => Seq(a)
            case Eq(Seq(a, e))             => Seq(a, e)
            case Eq(Seq(a, e, v))          => Seq(a, e, v)
            case Eq(Seq(a, e, v, d: Date)) => Seq(a, e, v, d)
            case Eq(Seq(a, e, v, t))       => Seq(a, e, v, t)
            case v                         => throw MoleculeException("Unexpected AEVT value: " + v)
          })

        case Generic("AVET", attr, _, value, _) =>
          attr match {
            case "range" =>
              ("indexRange", "", value match {
                case Eq(Seq(a, None, None))  => Seq(a, null, null)
                case Eq(Seq(a, from, None))  => Seq(a, from, null)
                case Eq(Seq(a, None, until)) => Seq(a, null, until)
                case Eq(Seq(a, from, until)) =>
                  if (from.getClass != until.getClass)
                    throw MoleculeException("Please supply range arguments of same type as attribute.")
                  Seq(a, from, until)
                case v                       => throw MoleculeException("Unexpected AVET range value: " + v)
              })
            case _       =>
              ("datoms", datomic.Database.AVET, value match {
                case NoValue                   => Seq()
                case Eq(Seq(a))                => Seq(a)
                case Eq(Seq(a, v))             => Seq(a, v)
                case Eq(Seq(a, v, e))          => Seq(a, v, e)
                case Eq(Seq(a, v, e, d: Date)) => Seq(a, v, e, d)
                case Eq(Seq(a, v, e, t))       => Seq(a, v, e, t)
                case v                         => throw MoleculeException("Unexpected AVET datoms value: " + v)
              })
          }

        case Generic("VAET", _, _, value, _) =>
          ("datoms", datomic.Database.VAET, value match {
            case NoValue                   => Seq()
            case Eq(Seq(v))                => Seq(v)
            case Eq(Seq(v, a))             => Seq(v, a)
            case Eq(Seq(v, a, e))          => Seq(v, a, e)
            case Eq(Seq(v, a, e, d: Date)) => Seq(v, a, e, d)
            case Eq(Seq(v, a, e, t))       => Seq(v, a, e, t)
            case v                         => throw MoleculeException("Unexpected VAET value: " + v)
          })

        case Generic("Log", _, _, value, _) =>
          def err(v: Any) = throw MoleculeException(
            s"Args to Log can only be t, tx or txInstant of type Int/Long/Date. Found `$v` of type " + v.getClass)

          ("txRange", "", value match {
            case Eq(Seq(a, b)) =>
              // Get valid from/until values
              val from  = a match {
                case None                              => null
                case from@(_: Int | _: Long | _: Date) => from
                case other                             => err(other)
              }
              val until = b match {
                case None                               => null
                case until@(_: Int | _: Long | _: Date) => until
                case other                              => err(other)
              }
              Seq(from, until)


            case Eq(Seq(from)) => from match {
              case None                              => Seq(null, null)
              case from@(_: Int | _: Long | _: Date) => Seq(from, null)
              case other                             => err(other)
            }

            // All !!
            case Eq(Nil) => Seq(null, null)

            case Eq(other) => err(other)

            case v => throw MoleculeException("Unexpected Log value: " + v)
          })

        case other => throw MoleculeException(
          s"Only Index queries accepted (EAVT, AEVT, AVET, VAET, Log). Found `$other`"
        )
      }

      // Important with stable db (when mixing filters with getHistory)!
      val stableDb = db

      def datomElement(
        tOpt: Option[Long],
        attr: String
      )(implicit ec: ExecutionContext): Datom => Future[Any] = attr match {
        case "e"                   => (d: Datom) => Future(d.e)
        case "a"                   => (d: Datom) =>
          stableDb.map(_.getDatomicDb.asInstanceOf[Database].ident(d.a).toString)
        case "v"                   => (d: Datom) => Future(d.v)
        case "t" if tOpt.isDefined => (_: Datom) => Future(tOpt.get)
        case "t"                   => (d: Datom) => Future(toT(d.tx))
        case "tx"                  => (d: Datom) => Future(d.tx)
        case "txInstant"           => (d: Datom) =>
          stableDb.flatMap(db =>
            db.entity(this, d.tx).rawValue(":db/txInstant").map(_.asInstanceOf[Date])
          )
        case "op"                  => (d: Datom) => Future(d.added)
        case a                     => throw MoleculeException("Unexpected generic attribute: " + a)
      }

      val attrs: Seq[String] = model.elements.collect {
        case Generic(_, attr, _, _, _)
          if attr != "args_" && attr != "range" => attr
      }

      def datom2row(
        tOpt: Option[Long]
      )(implicit ec: ExecutionContext): Datom => Future[jList[AnyRef]] = attrs.length match {
        case 1 =>
          val x1 = datomElement(tOpt, attrs.head)
          (d: Datom) =>
            for {
              v1 <- x1(d)
            } yield list(
              v1.asInstanceOf[AnyRef]
            )

        case 2 =>
          val x1 = datomElement(tOpt, attrs.head)
          val x2 = datomElement(tOpt, attrs(1))
          (d: Datom) =>
            for {
              v1 <- x1(d)
              v2 <- x2(d)
            } yield list(
              v1.asInstanceOf[AnyRef],
              v2.asInstanceOf[AnyRef]
            )

        case 3 =>
          val x1 = datomElement(tOpt, attrs.head)
          val x2 = datomElement(tOpt, attrs(1))
          val x3 = datomElement(tOpt, attrs(2))
          (d: Datom) =>
            for {
              v1 <- x1(d)
              v2 <- x2(d)
              v3 <- x3(d)
            } yield list(
              v1.asInstanceOf[AnyRef],
              v2.asInstanceOf[AnyRef],
              v3.asInstanceOf[AnyRef]
            )


        case 4 =>
          val x1 = datomElement(tOpt, attrs.head)
          val x2 = datomElement(tOpt, attrs(1))
          val x3 = datomElement(tOpt, attrs(2))
          val x4 = datomElement(tOpt, attrs(3))
          (d: Datom) =>
            for {
              v1 <- x1(d)
              v2 <- x2(d)
              v3 <- x3(d)
              v4 <- x4(d)
            } yield list(
              v1.asInstanceOf[AnyRef],
              v2.asInstanceOf[AnyRef],
              v3.asInstanceOf[AnyRef],
              v4.asInstanceOf[AnyRef]
            )

        case 5 =>
          val x1 = datomElement(tOpt, attrs.head)
          val x2 = datomElement(tOpt, attrs(1))
          val x3 = datomElement(tOpt, attrs(2))
          val x4 = datomElement(tOpt, attrs(3))
          val x5 = datomElement(tOpt, attrs(4))
          (d: Datom) =>
            for {
              v1 <- x1(d)
              v2 <- x2(d)
              v3 <- x3(d)
              v4 <- x4(d)
              v5 <- x5(d)
            } yield list(
              v1.asInstanceOf[AnyRef],
              v2.asInstanceOf[AnyRef],
              v3.asInstanceOf[AnyRef],
              v4.asInstanceOf[AnyRef],
              v5.asInstanceOf[AnyRef]
            )

        case 6 =>
          val x1 = datomElement(tOpt, attrs.head)
          val x2 = datomElement(tOpt, attrs(1))
          val x3 = datomElement(tOpt, attrs(2))
          val x4 = datomElement(tOpt, attrs(3))
          val x5 = datomElement(tOpt, attrs(4))
          val x6 = datomElement(tOpt, attrs(5))
          (d: Datom) =>
            for {
              v1 <- x1(d)
              v2 <- x2(d)
              v3 <- x3(d)
              v4 <- x4(d)
              v5 <- x5(d)
              v6 <- x6(d)
            } yield list(
              v1.asInstanceOf[AnyRef],
              v2.asInstanceOf[AnyRef],
              v3.asInstanceOf[AnyRef],
              v4.asInstanceOf[AnyRef],
              v5.asInstanceOf[AnyRef],
              v6.asInstanceOf[AnyRef]
            )

        case 7 =>
          val x1 = datomElement(tOpt, attrs.head)
          val x2 = datomElement(tOpt, attrs(1))
          val x3 = datomElement(tOpt, attrs(2))
          val x4 = datomElement(tOpt, attrs(3))
          val x5 = datomElement(tOpt, attrs(4))
          val x6 = datomElement(tOpt, attrs(5))
          val x7 = datomElement(tOpt, attrs(6))
          (d: Datom) =>
            for {
              v1 <- x1(d)
              v2 <- x2(d)
              v3 <- x3(d)
              v4 <- x4(d)
              v5 <- x5(d)
              v6 <- x6(d)
              v7 <- x7(d)
            } yield list(
              v1.asInstanceOf[AnyRef],
              v2.asInstanceOf[AnyRef],
              v3.asInstanceOf[AnyRef],
              v4.asInstanceOf[AnyRef],
              v5.asInstanceOf[AnyRef],
              v6.asInstanceOf[AnyRef],
              v7.asInstanceOf[AnyRef]
            )
      }


      // Convert Datoms to standard list of rows so that we can use the same Molecule query API
      var rows = List.empty[Future[jList[AnyRef]]]
      val res  = api match {
        case "datoms" =>
          val datom2row_ = datom2row(None)
          stableDb.flatMap(db =>
            db.asInstanceOf[DatomicDb_Peer].datoms(index, args: _*).flatMap { datoms =>
              datoms.forEach(datom =>
                rows = rows :+ datom2row_(datom)
              )
              Future.sequence(rows).map(_.asJavaCollection)
            }
          )

        case "indexRange" =>
          val datom2row_ = datom2row(None)
          val attrId     = args.head.toString
          val startValue = args(1)
          val endValue   = args(2)
          stableDb.flatMap(db =>
            db.asInstanceOf[DatomicDb_Peer].indexRange(attrId, startValue, endValue).flatMap { datoms =>
              datoms.forEach(datom =>
                rows = rows :+ datom2row_(datom)
              )
              Future.sequence(rows).map(_.asJavaCollection)
            }
          )

        case "txRange" =>
          // Loop transactions
          peerConn.log.txRange(args.head, args(1)).forEach { txMap =>
            // Flatten transaction datoms to uniform tuples return type
            val datom2row_ = datom2row(Some(txMap.get(datomic.Log.T).asInstanceOf[Long]))
            txMap.get(datomic.Log.DATA).asInstanceOf[jList[Datom]].forEach(datom =>
              rows = rows :+ datom2row_(datom)
            )
          }
          Future.sequence(rows).map(_.asJavaCollection)
      }
      res
    } catch {
      case NonFatal(ex) => Future.failed(ex)
    }
  }.flatten
}


