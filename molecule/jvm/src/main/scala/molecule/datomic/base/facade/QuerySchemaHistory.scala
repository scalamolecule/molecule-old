package molecule.datomic.base.facade

import java.lang.{Boolean => jBoolean, Long => jLong}
import java.util
import java.util.{Collections, Comparator, Collection => jCollection, List => jList}
import molecule.core.dto.SchemaAttr
import molecule.core.marshalling.ast.DatomicPeerProxy
import molecule.core.util.{DateHandling, JavaUtil}
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

case class QuerySchemaHistory(conn: Conn_Jvm)(implicit ec: ExecutionContext) extends JavaUtil with DateHandling {

  private val sharedAttrs = List("t", "tx", "txInstant", "attrId", "a", "part", "nsFull", "ns", "attr")

  // Indexes of raw data
  private val t_           = 0
  private val tx_          = 1
  private val txInstant_   = 2
  private val op_          = 3
  private val attrId_      = 4
  private val a_           = 5
  private val part_        = 6
  private val nsFull_      = 7
  private val ns_          = 8
  private val attr_        = 9
  private val schemaId_    = 10
  private val schemaAttr_  = 11
  private val schemaValue_ = 12

  private class SchemaComparator extends Comparator[jList[AnyRef]] {
    def compare(l1: jList[AnyRef], l2: jList[AnyRef]): Int = {
      var result = l1.get(tx_).asInstanceOf[jLong].compareTo(l2.get(tx_).asInstanceOf[jLong])
      if (result == 0) result = l1.get(attrId_).asInstanceOf[jLong].compareTo(l2.get(attrId_).asInstanceOf[jLong])
      if (result == 0) result = l1.get(schemaId_).asInstanceOf[jLong].compareTo(l2.get(schemaId_).asInstanceOf[jLong])
      if (result == 0) result = l1.get(op_).asInstanceOf[jBoolean].compareTo(l2.get(op_).asInstanceOf[jBoolean])
      result
    }
  }

  private lazy val id2str = {
    conn.connProxy match {
      case _: DatomicPeerProxy => (tpeId: AnyRef) =>
        tpeId.toString.toInt match {
          // cardinality
          case 35 => "one"
          case 36 => "many"

          // value type
          case 20 => "ref"
          case 22 => "long"
          case 23 => "string"
          case 24 => "boolean"
          case 25 => "instant"
          case 56 => "uuid"
          case 57 => "double"
          case 59 => "uri"
          case 60 => "bigint"
          case 61 => "bigdec"

          // unique
          case 37 => "value"
          case 38 => "identity"
        }

      case _ => (tpeId: AnyRef) =>
        tpeId.toString.toInt match {
          // cardinality
          case 35 => "one"
          case 36 => "many"

          // value type
          case 20 => "ref"
          case 22 => "long"
          case 23 => "string"
          case 24 => "boolean"
          case 25 => "instant"
          case 54 => "uuid"
          case 58 => "double"
          case 60 => "uri"
          case 61 => "bigint"
          case 62 => "bigdec"

          // unique
          case 37 => "value"
          case 38 => "identity"
        }
    }
  }

  private def row2schemaValue(schemaAttr: SchemaAttr): jList[AnyRef] => AnyRef = {
    val mandatory = schemaAttr.attrClean == schemaAttr.attr
    if (mandatory) {
      schemaAttr.attrClean match {
        case "valueType" | "cardinality" | "unique" => (row: jList[AnyRef]) => id2str(row.get(schemaValue_))
        case "ident" | "doc"                        => (row: jList[AnyRef]) => row.get(schemaValue_).toString
        case _                                      => (row: jList[AnyRef]) => row.get(schemaValue_).asInstanceOf[jBoolean]
      }
    } else if (schemaAttr.args.isEmpty) {
      // Optional values in pull result format (to be processed as non-history Schema java results)
      val value = schemaAttr.attrClean match {
        case "valueType" | "cardinality" | "unique" =>
          (row: jList[AnyRef]) => javaMap("" -> (":db.type/" + id2str(row.get(schemaValue_))))
        case _                                      =>
          (row: jList[AnyRef]) => row.get(schemaValue_)
      }
      (row: jList[AnyRef]) => if (row.get(op_).toString == "false") null else javaMap("" -> value(row))
    } else {
      // Applied optional values in pull result format (to be processed as non-history Schema java results)
      schemaAttr.attrClean match {
        case "valueType" | "cardinality" | "unique" => (row: jList[AnyRef]) => id2str(row.get(schemaValue_))
        case _                                      => (row: jList[AnyRef]) => row.get(schemaValue_)
      }
    }
  }

  private[molecule] def fetchSchemaHistory(
    schemaAttrs: Seq[SchemaAttr],
    datalogQuery: String
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = try {
    val attrs          = mutable.ListBuffer[String]()
    val requireAttrs   = mutable.ListBuffer[String]()
    val validateAttrs  = mutable.ListBuffer[String]()
    val validatedAttrs = mutable.ListBuffer[String]()
    val excludeAttrs   = mutable.ListBuffer[String]()
    val processedAttrs = mutable.ListBuffer[String]()

    val javaInputs = schemaAttrs.collect {
      case SchemaAttr(attrClean, _, "=", args) => attrClean match {
        case "t" | "tx" | "attrId" =>
          javaList(args.map(_.toLong.asInstanceOf[AnyRef]): _*)

        case "txInstant" =>
          javaList(args.map(d => str2date(d).asInstanceOf[AnyRef]): _*)

        case "isComponent" | "noHistory" | "index" | "fulltext" =>
          javaList(args.map(_.toBoolean.asInstanceOf[AnyRef]): _*)

        case _ => javaList(args.map(_.asInstanceOf[AnyRef]): _*)
      }
    }

    val validate: Map[String, (Boolean, AnyRef) => Boolean] = schemaAttrs.map {
      case SchemaAttr(attrClean, _, "", _)     => attrClean -> ((add: Boolean, _: AnyRef) => add)
      case SchemaAttr(attrClean, _, "none", _) => attrClean -> ((add: Boolean, _: AnyRef) => !add)

      case SchemaAttr(attrClean@("valueType" | "cardinality" | "unique"), _, expr, args) =>
        val test = expr match {
          case "="  => (add: Boolean, v: AnyRef) => add && args.contains(id2str(v))
          case "!=" => (add: Boolean, v: AnyRef) => add && !args.contains(id2str(v))
        }
        attrClean -> test

      case SchemaAttr(attrClean, _, expr, args) =>
        val test = expr match {
          case "="  => (add: Boolean, v: AnyRef) => add && args.contains(v.toString)
          case "!=" => (add: Boolean, v: AnyRef) => add && !args.contains(v.toString)
          case ">"  => (add: Boolean, v: AnyRef) => add && v.toString > args.head
          case ">=" => (add: Boolean, v: AnyRef) => add && v.toString >= args.head
          case "<"  => (add: Boolean, v: AnyRef) => add && v.toString < args.head
          case "<=" => (add: Boolean, v: AnyRef) => add && v.toString <= args.head
        }
        attrClean -> test
    }.toMap

    val (length, attrChecks) = schemaAttrs.foldLeft(0, Map.empty[String, (Int, jList[AnyRef] => AnyRef)]) {
      // Initial attributes filtered by query
      case ((i, acc), SchemaAttr(attrClean, attr, _, _)) if sharedAttrs.contains(attrClean) =>
        if (attr.last == '_') {
          (i, acc)
        } else {
          attrs += attrClean
          (i + 1, acc)
        }

      // Excluded optional
      case ((i, acc), schemaAttr@SchemaAttr(attrClean, attr, "none", _)) if attr.last == '$' =>
        attrs += attrClean
        excludeAttrs += attrClean
        (i + 1, acc + (attrClean -> (i, row2schemaValue(schemaAttr))))

      // Excluded
      case ((i, acc), SchemaAttr(attrClean, _, "none", _)) =>
        excludeAttrs += attrClean
        (i, acc)

      // Tacit without expression
      case ((i, acc), SchemaAttr(attrClean, attr, "", _)) if attr.last == '_' =>
        requireAttrs += attrClean
        (i, acc)

      // Tacit with expression
      case ((i, acc), schemaAttr@SchemaAttr(attrClean, attr, _, _)) if attr.last == '_' =>
        validateAttrs += attrClean
        (i, acc + (attrClean -> (i, row2schemaValue(schemaAttr))))

      // Optional without expression
      case ((i, acc), schemaAttr@SchemaAttr(attrClean, attr, "", _)) if attr.last == '$' =>
        attrs += attrClean
        (i + 1, acc + (attrClean -> (i, row2schemaValue(schemaAttr))))

      // Optional with expression
      case ((i, acc), schemaAttr@SchemaAttr(attrClean, attr, _, _)) if attr.last == '$' =>
        attrs += attrClean
        validateAttrs += attrClean
        (i + 1, acc + (attrClean -> (i, row2schemaValue(schemaAttr))))

      // Mandatory
      case ((i, acc), schemaAttr@SchemaAttr(attrClean, _, _, _)) =>
        attrs += attrClean
        requireAttrs += attrClean
        validateAttrs += attrClean
        (i + 1, acc + (attrClean -> (i, row2schemaValue(schemaAttr))))
    }

    var valid      = true
    var add        = true
    var prevTx     = 0L.asInstanceOf[AnyRef]
    var tx         = 0L.asInstanceOf[AnyRef]
    var prevAttrId = 0L.asInstanceOf[AnyRef]
    var attrId     = 0L.asInstanceOf[AnyRef]
    var attrClean  = ""
    var list       = new util.ArrayList[AnyRef](length)
    val coll       = new util.ArrayList[jList[AnyRef]]()

    def initElement(attrClean: String): jList[AnyRef] => AnyRef = attrClean match {
      case "t"         => (row: jList[AnyRef]) => row.get(t_)
      case "tx"        => (row: jList[AnyRef]) => row.get(tx_)
      case "txInstant" => (row: jList[AnyRef]) => row.get(txInstant_)
      case "attrId"    => (row: jList[AnyRef]) => row.get(attrId_)
      case "a"         => (row: jList[AnyRef]) => row.get(a_)
      case "part"      => (row: jList[AnyRef]) => row.get(part_)
      case "nsFull"    => (row: jList[AnyRef]) => row.get(nsFull_)
      case "ns"        => (row: jList[AnyRef]) => row.get(ns_)
      case "attr"      => (row: jList[AnyRef]) => row.get(attr_)
      case _           => (_: jList[AnyRef]) => null // to be set when traversing additional schema attributes
    }

    // Initializer of row lists with arity of the number of non-tacit attributes
    val initList: jList[AnyRef] => Unit = {
      length match {
        case 1  =>
          val resolve1 = initElement(attrs(0))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
          }
        case 2  =>
          val resolve1 = initElement(attrs(0))
          val resolve2 = initElement(attrs(1))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
          }
        case 3  =>
          val resolve1 = initElement(attrs(0))
          val resolve2 = initElement(attrs(1))
          val resolve3 = initElement(attrs(2))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
          }
        case 4  =>
          val resolve1 = initElement(attrs(0))
          val resolve2 = initElement(attrs(1))
          val resolve3 = initElement(attrs(2))
          val resolve4 = initElement(attrs(3))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
          }
        case 5  =>
          val resolve1 = initElement(attrs(0))
          val resolve2 = initElement(attrs(1))
          val resolve3 = initElement(attrs(2))
          val resolve4 = initElement(attrs(3))
          val resolve5 = initElement(attrs(4))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
          }
        case 6  =>
          val resolve1 = initElement(attrs(0))
          val resolve2 = initElement(attrs(1))
          val resolve3 = initElement(attrs(2))
          val resolve4 = initElement(attrs(3))
          val resolve5 = initElement(attrs(4))
          val resolve6 = initElement(attrs(5))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
          }
        case 7  =>
          val resolve1 = initElement(attrs(0))
          val resolve2 = initElement(attrs(1))
          val resolve3 = initElement(attrs(2))
          val resolve4 = initElement(attrs(3))
          val resolve5 = initElement(attrs(4))
          val resolve6 = initElement(attrs(5))
          val resolve7 = initElement(attrs(6))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
          }
        case 8  =>
          val resolve1 = initElement(attrs(0))
          val resolve2 = initElement(attrs(1))
          val resolve3 = initElement(attrs(2))
          val resolve4 = initElement(attrs(3))
          val resolve5 = initElement(attrs(4))
          val resolve6 = initElement(attrs(5))
          val resolve7 = initElement(attrs(6))
          val resolve8 = initElement(attrs(7))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
          }
        case 9  =>
          val resolve1 = initElement(attrs(0))
          val resolve2 = initElement(attrs(1))
          val resolve3 = initElement(attrs(2))
          val resolve4 = initElement(attrs(3))
          val resolve5 = initElement(attrs(4))
          val resolve6 = initElement(attrs(5))
          val resolve7 = initElement(attrs(6))
          val resolve8 = initElement(attrs(7))
          val resolve9 = initElement(attrs(8))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
          }
        case 10 =>
          val resolve1  = initElement(attrs(0))
          val resolve2  = initElement(attrs(1))
          val resolve3  = initElement(attrs(2))
          val resolve4  = initElement(attrs(3))
          val resolve5  = initElement(attrs(4))
          val resolve6  = initElement(attrs(5))
          val resolve7  = initElement(attrs(6))
          val resolve8  = initElement(attrs(7))
          val resolve9  = initElement(attrs(8))
          val resolve10 = initElement(attrs(9))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
            list.add(resolve10(row))
          }
        case 11 =>
          val resolve1  = initElement(attrs(0))
          val resolve2  = initElement(attrs(1))
          val resolve3  = initElement(attrs(2))
          val resolve4  = initElement(attrs(3))
          val resolve5  = initElement(attrs(4))
          val resolve6  = initElement(attrs(5))
          val resolve7  = initElement(attrs(6))
          val resolve8  = initElement(attrs(7))
          val resolve9  = initElement(attrs(8))
          val resolve10 = initElement(attrs(9))
          val resolve11 = initElement(attrs(10))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
            list.add(resolve10(row))
            list.add(resolve11(row))
          }
        case 12 =>
          val resolve1  = initElement(attrs(0))
          val resolve2  = initElement(attrs(1))
          val resolve3  = initElement(attrs(2))
          val resolve4  = initElement(attrs(3))
          val resolve5  = initElement(attrs(4))
          val resolve6  = initElement(attrs(5))
          val resolve7  = initElement(attrs(6))
          val resolve8  = initElement(attrs(7))
          val resolve9  = initElement(attrs(8))
          val resolve10 = initElement(attrs(9))
          val resolve11 = initElement(attrs(10))
          val resolve12 = initElement(attrs(11))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
            list.add(resolve10(row))
            list.add(resolve11(row))
            list.add(resolve12(row))
          }
        case 13 =>
          val resolve1  = initElement(attrs(0))
          val resolve2  = initElement(attrs(1))
          val resolve3  = initElement(attrs(2))
          val resolve4  = initElement(attrs(3))
          val resolve5  = initElement(attrs(4))
          val resolve6  = initElement(attrs(5))
          val resolve7  = initElement(attrs(6))
          val resolve8  = initElement(attrs(7))
          val resolve9  = initElement(attrs(8))
          val resolve10 = initElement(attrs(9))
          val resolve11 = initElement(attrs(10))
          val resolve12 = initElement(attrs(11))
          val resolve13 = initElement(attrs(12))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
            list.add(resolve10(row))
            list.add(resolve11(row))
            list.add(resolve12(row))
            list.add(resolve13(row))
          }
        case 14 =>
          val resolve1  = initElement(attrs(0))
          val resolve2  = initElement(attrs(1))
          val resolve3  = initElement(attrs(2))
          val resolve4  = initElement(attrs(3))
          val resolve5  = initElement(attrs(4))
          val resolve6  = initElement(attrs(5))
          val resolve7  = initElement(attrs(6))
          val resolve8  = initElement(attrs(7))
          val resolve9  = initElement(attrs(8))
          val resolve10 = initElement(attrs(9))
          val resolve11 = initElement(attrs(10))
          val resolve12 = initElement(attrs(11))
          val resolve13 = initElement(attrs(12))
          val resolve14 = initElement(attrs(13))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
            list.add(resolve10(row))
            list.add(resolve11(row))
            list.add(resolve12(row))
            list.add(resolve13(row))
            list.add(resolve14(row))
          }
        case 15 =>
          val resolve1  = initElement(attrs(0))
          val resolve2  = initElement(attrs(1))
          val resolve3  = initElement(attrs(2))
          val resolve4  = initElement(attrs(3))
          val resolve5  = initElement(attrs(4))
          val resolve6  = initElement(attrs(5))
          val resolve7  = initElement(attrs(6))
          val resolve8  = initElement(attrs(7))
          val resolve9  = initElement(attrs(8))
          val resolve10 = initElement(attrs(9))
          val resolve11 = initElement(attrs(10))
          val resolve12 = initElement(attrs(11))
          val resolve13 = initElement(attrs(12))
          val resolve14 = initElement(attrs(13))
          val resolve15 = initElement(attrs(14))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
            list.add(resolve10(row))
            list.add(resolve11(row))
            list.add(resolve12(row))
            list.add(resolve13(row))
            list.add(resolve14(row))
            list.add(resolve15(row))
          }
        case 16 =>
          val resolve1  = initElement(attrs(0))
          val resolve2  = initElement(attrs(1))
          val resolve3  = initElement(attrs(2))
          val resolve4  = initElement(attrs(3))
          val resolve5  = initElement(attrs(4))
          val resolve6  = initElement(attrs(5))
          val resolve7  = initElement(attrs(6))
          val resolve8  = initElement(attrs(7))
          val resolve9  = initElement(attrs(8))
          val resolve10 = initElement(attrs(9))
          val resolve11 = initElement(attrs(10))
          val resolve12 = initElement(attrs(11))
          val resolve13 = initElement(attrs(12))
          val resolve14 = initElement(attrs(13))
          val resolve15 = initElement(attrs(14))
          val resolve16 = initElement(attrs(15))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
            list.add(resolve10(row))
            list.add(resolve11(row))
            list.add(resolve12(row))
            list.add(resolve13(row))
            list.add(resolve14(row))
            list.add(resolve15(row))
            list.add(resolve16(row))
          }
        case 17 =>
          val resolve1  = initElement(attrs(0))
          val resolve2  = initElement(attrs(1))
          val resolve3  = initElement(attrs(2))
          val resolve4  = initElement(attrs(3))
          val resolve5  = initElement(attrs(4))
          val resolve6  = initElement(attrs(5))
          val resolve7  = initElement(attrs(6))
          val resolve8  = initElement(attrs(7))
          val resolve9  = initElement(attrs(8))
          val resolve10 = initElement(attrs(9))
          val resolve11 = initElement(attrs(10))
          val resolve12 = initElement(attrs(11))
          val resolve13 = initElement(attrs(12))
          val resolve14 = initElement(attrs(13))
          val resolve15 = initElement(attrs(14))
          val resolve16 = initElement(attrs(15))
          val resolve17 = initElement(attrs(16))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
            list.add(resolve10(row))
            list.add(resolve11(row))
            list.add(resolve12(row))
            list.add(resolve13(row))
            list.add(resolve14(row))
            list.add(resolve15(row))
            list.add(resolve16(row))
            list.add(resolve17(row))
          }
        case 18 =>
          val resolve1  = initElement(attrs(0))
          val resolve2  = initElement(attrs(1))
          val resolve3  = initElement(attrs(2))
          val resolve4  = initElement(attrs(3))
          val resolve5  = initElement(attrs(4))
          val resolve6  = initElement(attrs(5))
          val resolve7  = initElement(attrs(6))
          val resolve8  = initElement(attrs(7))
          val resolve9  = initElement(attrs(8))
          val resolve10 = initElement(attrs(9))
          val resolve11 = initElement(attrs(10))
          val resolve12 = initElement(attrs(11))
          val resolve13 = initElement(attrs(12))
          val resolve14 = initElement(attrs(13))
          val resolve15 = initElement(attrs(14))
          val resolve16 = initElement(attrs(15))
          val resolve17 = initElement(attrs(16))
          val resolve18 = initElement(attrs(17))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
            list.add(resolve10(row))
            list.add(resolve11(row))
            list.add(resolve12(row))
            list.add(resolve13(row))
            list.add(resolve14(row))
            list.add(resolve15(row))
            list.add(resolve16(row))
            list.add(resolve17(row))
            list.add(resolve18(row))
          }
        case 19 =>
          val resolve1  = initElement(attrs(0))
          val resolve2  = initElement(attrs(1))
          val resolve3  = initElement(attrs(2))
          val resolve4  = initElement(attrs(3))
          val resolve5  = initElement(attrs(4))
          val resolve6  = initElement(attrs(5))
          val resolve7  = initElement(attrs(6))
          val resolve8  = initElement(attrs(7))
          val resolve9  = initElement(attrs(8))
          val resolve10 = initElement(attrs(9))
          val resolve11 = initElement(attrs(10))
          val resolve12 = initElement(attrs(11))
          val resolve13 = initElement(attrs(12))
          val resolve14 = initElement(attrs(13))
          val resolve15 = initElement(attrs(14))
          val resolve16 = initElement(attrs(15))
          val resolve17 = initElement(attrs(16))
          val resolve18 = initElement(attrs(17))
          val resolve19 = initElement(attrs(18))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
            list.add(resolve7(row))
            list.add(resolve8(row))
            list.add(resolve9(row))
            list.add(resolve10(row))
            list.add(resolve11(row))
            list.add(resolve12(row))
            list.add(resolve13(row))
            list.add(resolve14(row))
            list.add(resolve15(row))
            list.add(resolve16(row))
            list.add(resolve17(row))
            list.add(resolve18(row))
            list.add(resolve19(row))
          }
      }
    }

    //    schemaAttrs.foreach(println)
    //    println("INPUTS       : " + javaInputs)
    //    println("attrs        : " + attrs)
    //    println("attrChecks   :" + (if (attrChecks.nonEmpty) "\n  " else "") + attrChecks.mkString("\n  "))
    //    println("requireAttrs : " + requireAttrs)
    //    println("validateAttrs: " + validateAttrs)
    //    println("excludeAttrs : " + excludeAttrs)
    //    println("Length       : " + length)

    conn.historyQuery(datalogQuery, javaInputs).map { jColl =>
      // Sort by tx, attrId, schemaId, op
      val rows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(jColl)
      Collections.sort(rows, new SchemaComparator())

      val last       = rows.size()
      var totalCount = 0
      var first      = true
      rows.forEach { row =>
        totalCount += 1
        add = row.get(op_).asInstanceOf[jBoolean]
        tx = row.get(tx_)
        attrId = row.get(attrId_)
        attrClean = row.get(schemaAttr_).toString

        //        if (tx != prevTx)
        //          println("")
        //        println(row)

        // Init list
        if (tx != prevTx || attrId != prevAttrId) {
          val hasRequired  = requireAttrs.intersect(processedAttrs) == requireAttrs
          val allValidated = validateAttrs.intersect(validatedAttrs) == validateAttrs
          val noExcluded   = excludeAttrs.intersect(processedAttrs).isEmpty
          if (totalCount != 1 && hasRequired && allValidated && noExcluded) {
            // add prev collected row if all is ok
            coll.add(list)
          }
          processedAttrs.clear()
          validatedAttrs.clear()
          prevTx = tx
          prevAttrId = attrId
          first = true
          list = new util.ArrayList[AnyRef](length)
          initList(row)
        }

        // Validate
        attrChecks.get(attrClean).foreach { case (attrIndex, resolver) =>
          val rawValue = row.get(schemaValue_)
          valid = validate(attrClean)(add, rawValue)
          if (valid) {
            validatedAttrs += attrClean
          } else {
            validatedAttrs -= attrClean
          }
          val value = resolver(row)
          if (attrs.contains(attrClean)) {
            // Set value of output row at attrIndex if attrClean is not tacit
            list.set(attrIndex, value)
          }
        }
        if (add) {
          processedAttrs += attrClean
        }
        first = false

        if (totalCount == last) {
          val hasRequired  = requireAttrs.intersect(processedAttrs) == requireAttrs
          val allValidated = validateAttrs.intersect(validatedAttrs) == validateAttrs
          val noExcluded   = excludeAttrs.intersect(processedAttrs).isEmpty
          if (hasRequired && allValidated && noExcluded) {
            // add last collected row if all is ok
            coll.add(list)
          }
        }
      }
      //      println("--- coll: ---------------------------")
      //      coll.forEach(row => println(row))
      //      println("#####################################")
      coll
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }
}



