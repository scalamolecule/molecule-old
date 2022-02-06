package molecule.datomic.peer.facade

import java.lang.{Boolean => jBoolean, Long => jLong}
import java.util.{Collections, Comparator, Collection => jCollection, List => jList}
import java.{util => ju}
import datomic._
import molecule.core.ast.elements._
import scala.concurrent.{ExecutionContext, Future}

/** Facade to Datomic connection for peer api.
 * */
trait QuerySchemaHistory { self: Conn_Peer =>


  private[molecule] def schemaHistoryQuery(model: Model)(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = {
    println("-------------------------------")
    println(model)
    println("-------------------------------")


    class SchemaComparator extends Comparator[jList[AnyRef]] {
      def compare(l1: jList[AnyRef], l2: jList[AnyRef]): Int = {
        var result = l1.get(0).asInstanceOf[jLong].compareTo(l2.get(0).asInstanceOf[jLong])
        if (result == 0) result = l1.get(4).asInstanceOf[jLong].compareTo(l2.get(4).asInstanceOf[jLong])
        if (result == 0) result = l1.get(6).asInstanceOf[jLong].compareTo(l2.get(6).asInstanceOf[jLong])
        result
      }
    }

    //    def setSchemaValue(schemaAttr: String) = schemaAttr match {
    //      case ":db/ident" =>
    //    }
    val length = model.elements.length
    //    val

    db.map { db =>
      val jColl = Peer.q(
        """[:find  ?tx ?t ?txInst ?op
          |        ?attrId ?attrIdent
          |        ?schemaId ?schemaIdent ?schemaValue
          | :where [:db.part/db :db.install/attribute ?attrId]
          |        [(datomic.api/ident $ ?attrId) ?attrIdent]
          |        [?attrId ?schemaId ?schemaValue ?tx ?op]
          |        [(datomic.api/ident $ ?schemaId) ?schemaIdent]
          |        [(datomic.api/tx->t ?tx) ?t]
          |        [(>= ?t 1000)]
          |        [?tx :db/txInstant ?txInst]
          |]""".stripMargin,
        //          |        [(>= ?tx 13194139534312)]
        //          |        [?attrId :db/ident ?attrIdent]
        db.getDatomicDb.asInstanceOf[Database].history()
      )

      // Sort by tx, attrId
      val rows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(jColl)
      Collections.sort(rows, new SchemaComparator())

      var next               = false
      var prevTx    : AnyRef = 0L.asInstanceOf[AnyRef]
      var tx        : AnyRef = 0L.asInstanceOf[AnyRef]
      var prevAttrId: AnyRef = 0L.asInstanceOf[AnyRef]
      var attrId    : AnyRef = 0L.asInstanceOf[AnyRef]
      var add                = true
      val coll               = new ju.ArrayList[jList[AnyRef]]()
      var list               = new ju.ArrayList[AnyRef](length)

      rows.forEach { row =>
        //        println(row)
        tx = row.get(0)
        add = row.get(3).asInstanceOf[jBoolean]
        attrId = row.get(4)

        if (tx != prevTx || attrId != prevAttrId) {
          prevTx = tx
          prevAttrId = attrId
          if (next) {
            // add finished previous row list
            coll.add(list)
          }
          next = true

          //          // Initiate row list
          //          list = new ju.ArrayList[AnyRef](length)
          //          list.add(tx)
          //          list.add(row.get(1))
          //          list.add(row.get(2))
          //          list.add(attrId)
          //          list.add(row.get(4))
          //          list.add(row.get(5))
          //          list.add(null)
          //          list.add(null)
          //          list.add(null)
          //          list.add(null)
          //          list.add(null)
          //          list.add(null)
          //          list.add(null)
          //          list.add(null)
          //
          //          row.get(7).toString match {
          //            case ":db/ident"       => list.set(5, row.get(8))
          //            case ":db/valueType"   => list.set(6, row.get(8))
          //            case ":db/cardinality" => list.set(7, row.get(8))
          //            case ":db/doc"         => list.set(8, row.get(8))
          //            case ":db/unique"      => list.set(9, row.get(8))
          //            case ":db/isComponent" => list.set(10, row.get(8))
          //            case ":db/noHistory"   => list.set(11, row.get(8))
          //            case ":db/index"       => list.set(12, row.get(8))
          //            case ":db/fulltext"    => list.set(13, row.get(8))
          //          }
          //          println("A " + list)

          //          // Initiate row list
          //          list = new ju.ArrayList[AnyRef](6)
          //          list.add(row.get(1))
          //          list.add(row.get(4))
          //          list.add(null)
          //          list.add(null)
          //          list.add(null)
          //          list.add(null)
          //
          //          row.get(7).toString match {
          //            case ":db/ident"       => list.set(2, row.get(8))
          //            case ":db/valueType"   => list.set(3, row.get(8))
          //            case ":db/cardinality" => list.set(4, row.get(8))
          //            case ":db/noHistory"   => list.set(5, row.get(8))
          //            case _                 =>
          //          }
          //          //          println("A " + list)
          //
          //        } else {
          //          row.get(7).toString match {
          //            case ":db/ident"       => list.set(2, row.get(8))
          //            case ":db/valueType"   => list.set(3, row.get(8))
          //            case ":db/cardinality" => list.set(4, row.get(8))
          //            case ":db/noHistory"   => list.set(5, row.get(8))
          //            case _                 =>
          //          }

          // Initiate row list
          list = new ju.ArrayList[AnyRef](3)
          list.add(row.get(1))
          list.add(row.get(5).toString)
          list.add(null)

          row.get(7).toString match {
            case ":db/ident" if add => list.set(2, row.get(8).toString)
            case _                  =>
          }
          //          println("A " + list)

        } else {
          row.get(7).toString match {
            case ":db/ident" if add => list.set(2, row.get(8).toString)
            case _                  =>
          }
          //          println("B " + list)
        }

      }
      // Add last row
      //      coll.add(list)
      val result = coll //.asInstanceOf[jCollection[jList[AnyRef]]]

      result.forEach(row => println(row))

      result

      //      rows

      //        .asScala.toSeq.map { row0 =>
      //        val l = row0.asScala
      //        (l.head.toString.toLong, l(1).toString.toLong, l(2).toString, l(3).toString, l(4).toString, l(5).toString, l(6).toString)
      //      }
      //        .sortBy(r => (r._1, r._5, r._3))
      //        .foreach { r =>
      //          if (r._1 != last) {
      //            println("")
      //          }
      //          last = r._1
      //          println(r)
      //        }

      //      rawQuery(
      //        """[:find  ?tx ?t ?op ?attrId ?attrIdent ?schemaIdent ?schemaValue ?inst
      //          | :in    $ %
      //          | :where [:db.part/db :db.install/attribute ?attrId]
      //          |        [(datomic.api/ident $ ?attrId) ?attrIdent]
      //          |        (entity-at ?attrId ?tx ?t ?inst ?op ?schemaIdent ?schemaValue)
      //          |        [(>= ?tx 13194139534312)]
      //          |]""".stripMargin,
      //        Seq(
      //          db.getDatomicDb, //.asInstanceOf[Database].history(),
      //          """[
      //            |  [
      //            |    (entity-at [?attrId] ?tx ?t ?inst ?op ?schemaIdent ?schemaValue)
      //            |    [?attrId ?actionAttr ?schemaValue ?tx ?op]
      //            |    [?actionAttr :db/ident ?schemaIdent]
      //            |    [(datomic.api/tx->t ?tx) ?t]
      //            |    [?tx :db/txInstant ?inst]
      //            |  ]
      //            |]""".stripMargin
      //        )
      //      ).flatMap { res =>
      //        println("-----------------------")
      //
      //        var tx = 0L
      //        res.forEach { row =>
      //          println(row)
      //
      //
      //        }
      //
      //        //      val grouped: List[(Long, List[List[AnyRef]])] = res.groupBy(_.head.toString.toLong).toList.sortBy(_._1)
      //        //      grouped.foreach { group =>
      //        //        println("")
      //        //        group._2.foreach(println)
      //        //      }
      //        Future.failed(MoleculeException("auch"))
      //        //          Future(res)
      //        //          null
      //      }.recoverWith {
      //        //          case NonFatal(exc) =>
      //        case exc: Exception =>
      //          println(exc)
      //          Future.failed(exc)
      //        //          exc
      //      }
    }
  }
}


