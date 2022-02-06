package molecule.datomic.client.facade

import java.util.{Date, Collection => jCollection, List => jList}
import datomic.Peer._
import datomic._
import datomicScala.client.api.Datom
import molecule.core.ast.elements._
import molecule.core.exceptions._
import molecule.datomic.base.ast.transactionModel._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


/** Facade to Datomic connection for peer api.
 * */
trait QueryIndex { self: Conn_Client =>


  // Datoms API providing direct access to indexes
  private[molecule] final override def indexQuery(
    model: Model
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = Future {
    try {
      val (api, index, args) = model.elements.head match {
        case Generic("EAVT", _, _, value, _) =>
          ("datoms", ":eavt", value match {
            case NoValue                   => Seq()
            case Eq(Seq(e))                => Seq(e)
            case Eq(Seq(e, a))             => Seq(e, Util.read(a.toString))
            case Eq(Seq(e, a, v))          => Seq(e, Util.read(a.toString), v)
            case Eq(Seq(e, a, v, d: Date)) => Seq(e, Util.read(a.toString), v, d)
            case Eq(Seq(e, a, v, t))       => Seq(e, Util.read(a.toString), v, t)
            case v                         => throw MoleculeException("Unexpected EAVT value: " + v)
          })

        case Generic("AEVT", _, _, value, _) =>
          ("datoms", ":aevt", value match {
            case NoValue                   => Seq()
            case EntValue                  => Seq()
            case Eq(Seq(a))                => Seq(Util.read(a.toString))
            case Eq(Seq(a, e))             => Seq(Util.read(a.toString), e)
            case Eq(Seq(a, e, v))          => Seq(Util.read(a.toString), e, v)
            case Eq(Seq(a, e, v, d: Date)) => Seq(Util.read(a.toString), e, v, d)
            case Eq(Seq(a, e, v, t))       => Seq(Util.read(a.toString), e, v, t)
            case v                         => throw MoleculeException("Unexpected AEVT value: " + v)
          })

        case Generic("AVET", attr, _, value, _) =>
          attr match {
            case "range" =>
              ("indexRange", "", value match {
                case Eq(Seq(a, None, None))  => Seq(Util.read(a.toString), None, None)
                case Eq(Seq(a, from, None))  => Seq(Util.read(a.toString), Some(from), None)
                case Eq(Seq(a, None, until)) => Seq(Util.read(a.toString), None, Some(until))
                case Eq(Seq(a, from, until)) =>
                  if (from.getClass != until.getClass)
                    throw MoleculeException("Please supply range arguments of same type as attribute.")
                  Seq(Util.read(a.toString), Some(from), Some(until))
                case v                       => throw MoleculeException("Unexpected AVET range value: " + v)
              })
            case _       =>
              ("datoms", ":avet", value match {
                case NoValue                   => Seq()
                case Eq(Seq(a))                => Seq(Util.read(a.toString))
                case Eq(Seq(a, v))             => Seq(Util.read(a.toString), v)
                case Eq(Seq(a, v, e))          => Seq(Util.read(a.toString), v, e)
                case Eq(Seq(a, v, e, d: Date)) => Seq(Util.read(a.toString), v, e, d)
                case Eq(Seq(a, v, e, t))       => Seq(Util.read(a.toString), v, e, t)
                case v                         => throw MoleculeException("Unexpected AVET datoms value: " + v)
              })
          }

        case Generic("VAET", _, _, value, _) =>
          ("datoms", ":vaet", value match {
            case NoValue                   => Seq()
            case Eq(Seq(v))                => Seq(v)
            case Eq(Seq(v, a))             => Seq(v, Util.read(a.toString))
            case Eq(Seq(v, a, e))          => Seq(v, Util.read(a.toString), e)
            case Eq(Seq(v, a, e, d: Date)) => Seq(v, Util.read(a.toString), e, d)
            case Eq(Seq(v, a, e, t))       => Seq(v, Util.read(a.toString), e, t)
            case v                         => throw MoleculeException("Unexpected VAET value: " + v)
          })

        case Generic("Log", _, _, value, _) =>
          def err(v: Any) = throw MoleculeException(
            s"Args to Log can only be t, tx or txInstant of type Int/Long/Date. Found `$v` of type " + v.getClass)

          ("txRange", "", value match {
            case Eq(Seq(a, b)) =>
              // Get valid from/until values
              val from  = a match {
                case None                              => None
                case from@(_: Int | _: Long | _: Date) => Some(from)
                case other                             => err(other)
              }
              val until = b match {
                case None                               => None
                case until@(_: Int | _: Long | _: Date) => Some(until)
                case other                              => err(other)
              }
              Seq(from, until)


            case Eq(Seq(from)) => from match {
              case None                              => Seq(None, None)
              case from@(_: Int | _: Long | _: Date) => Seq(Some(from), None)
              case other                             => err(other)
            }

            // All !!
            case Eq(Nil) => Seq(None, None)

            case Eq(other) => err(other)

            case v => throw MoleculeException("Unexpected Log value: " + v)
          })

        case other => throw MoleculeException(
          s"Only Index queries accepted (EAVT, AEVT, AVET, VAET, Log). Found `$other`")
      }


      lazy val attrNames: Future[Array[String]] = {
        // Since the number of definitions is limited we can quickly collect them
        // for fast lookups in an array by index = attr id.
        // 5000 slots should satisfy any schema.
        val array = new Array[String](5000)
        rawQuery(
          """[:find  ?id ?idIdent
            | :where [_ :db.install/attribute ?id]
            |        [?id :db/ident ?idIdent]
            |        ]""".stripMargin).map { rows =>
          rows.forEach { row =>
            array(row.get(0).asInstanceOf[Long].toInt) = row.get(1).toString
          }
          array
        }
      }

      lazy val defaultDate = new Date(0)
      lazy val txInstant   = Util.read(":db/txInstant")

      def date(tx: Long): Future[Date] = {
        // We can't index all txInstants
        // Some initial transactions lack tx time it seems, so there we default
        // to time 0 (Thu Jan 01 01:00:00 CET 1970)
        db.flatMap(db => db.pull("[:db/txInstant]", tx).map {
          case null => defaultDate
          case res  => res.get(txInstant).asInstanceOf[Date]
        })
      }

      def datomElement(
        tOpt: Option[Long],
        attr: String
      )(implicit ec: ExecutionContext): Datom => Future[Any] = attr match {
        case "e"                   => (d: Datom) => Future(d.e)
        case "a"                   => (d: Datom) => attrNames.map(_ (d.a.toString.toInt))
        case "v"                   => (d: Datom) => Future(d.v)
        case "t" if tOpt.isDefined => (_: Datom) => Future(tOpt.get) // use provided t
        case "t"                   => (d: Datom) => Future(Peer.toT(d.tx))
        case "tx"                  => (d: Datom) => Future(d.tx)
        case "txInstant"           => (d: Datom) => date(d.tx)
        case "op"                  => (d: Datom) => Future(d.added)
        case a                     => throw MoleculeException("Unexpected generic attribute: " + a)
      }

      val attrs: Seq[String] = model.elements.collect {
        case Generic(_, attr, _, _, _)
          if attr != "args_" && attr != "range" => attr
      }

      def datom2row(tOpt: Option[Long]): Datom => Future[jList[AnyRef]] = attrs.length match {
        case 1 =>
          val x1 = datomElement(tOpt, attrs.head)
          (d: Datom) =>
            for {
              v1 <- x1(d)
            } yield list(
              v1.asInstanceOf[Object],
            )

        case 2 =>
          val x1 = datomElement(tOpt, attrs.head)
          val x2 = datomElement(tOpt, attrs(1))
          (d: Datom) =>
            for {
              v1 <- x1(d)
              v2 <- x2(d)
            } yield list(
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
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
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
              v3.asInstanceOf[Object],
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
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
              v3.asInstanceOf[Object],
              v4.asInstanceOf[Object],
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
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
              v3.asInstanceOf[Object],
              v4.asInstanceOf[Object],
              v5.asInstanceOf[Object],
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
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
              v3.asInstanceOf[Object],
              v4.asInstanceOf[Object],
              v5.asInstanceOf[Object],
              v6.asInstanceOf[Object],
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
              v1.asInstanceOf[Object],
              v2.asInstanceOf[Object],
              v3.asInstanceOf[Object],
              v4.asInstanceOf[Object],
              v5.asInstanceOf[Object],
              v6.asInstanceOf[Object],
              v7.asInstanceOf[Object]
            )
      }

      // Convert Datoms to standard list of rows so that we can use the same Molecule query API
      var rows = List.empty[Future[jList[AnyRef]]]
      api match {
        case "datoms" =>
          val datom2row_ = datom2row(None)
          db.flatMap(db =>
            db.asInstanceOf[DatomicDb_Client].datoms(index, args, limit = -1).flatMap { datoms =>
              datoms.forEach(datom =>
                rows = rows :+ datom2row_(datom)
              )
              Future.sequence(rows).map(_.asJavaCollection)
            }
          )

        case "indexRange" =>
          val datom2row_ = datom2row(None)
          val attrId     = args.head.toString
          val startValue = args(1).asInstanceOf[Option[Any]]
          val endValue   = args(2).asInstanceOf[Option[Any]]
          db.flatMap(db =>
            db.asInstanceOf[DatomicDb_Client].indexRange(attrId, startValue, endValue, limit = -1).flatMap { datoms =>
              datoms.forEach(datom =>
                rows = rows :+ datom2row_(datom)
              )
              Future.sequence(rows).map(_.asJavaCollection)
            }
          )

        case "txRange" =>
          val from  = args.head.asInstanceOf[Option[Any]]
          val until = args(1).asInstanceOf[Option[Any]]
          // Flatten transaction maps
          clientConn.txRangeArray(from, until, limit = -1).foreach {
            case (t, datoms) =>
              val datom2row_ = datom2row(Some(t))
              datoms.foreach(datom =>
                rows = rows :+ datom2row_(datom)
              )
          }
          Future.sequence(rows).map(_.asJavaCollection)
      }
    } catch {
      case NonFatal(ex) => Future.failed(ex)
    }
  }.flatten
}


