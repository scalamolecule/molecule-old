package molecule.datomic.peer.facade

import java.lang.{Boolean => jBoolean, Long => jLong}
import java.util
import java.util.{Collections, Comparator, Collection => jCollection, List => jList}
import molecule.core.ast.elements._
import molecule.core.exceptions.MoleculeException
import molecule.core.util.JavaUtil
import molecule.datomic.base.facade.Conn_Jvm
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


trait QuerySchemaHistory extends JavaUtil { self: Conn_Jvm =>

  // Indexes of raw data
  private val t_           = 0
  private val tx_          = 1
  private val txInst_      = 2
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

  private val query =
    s"""[:find  ?t
       |        ?tx
       |        ?txInst
       |        ?op
       |        ?attrId
       |        ?a
       |        ?part
       |        ?nsFull
       |        ?ns
       |        ?attr
       |        ?schemaId
       |        ?schemaAttr
       |        ?schemaValue
       | :where [:db.part/db :db.install/attribute ?attrId]
       |        [?attrId :db/ident ?attrIdent]
       |        [(str ?attrIdent) ?a]
       |        [(namespace ?attrIdent) ?nsFull0]
       |
       |        ;; Remove '-' prefix if attribute is marked as retired
       |        [(if (= (subs ?nsFull0 0 1) "-") (subs ?nsFull0 1) ?nsFull0) ?nsFull]
       |
       |        ;; Exclude internal Datomic attributes
       |        [(.matches ^String ?nsFull "^(db|db.alter|db.excise|db.install|db.part|db.sys|fressian|db.entity|db.attr|:-.*)") ?sys]
       |        [(= ?sys false)]
       |
       |        [(.contains ^String ?nsFull "_") ?isPart]
       |        [(.split ^String ?nsFull "_") ?nsParts]
       |        [(first ?nsParts) ?part0]
       |        [(last ?nsParts) ?ns]
       |        [(if ?isPart ?part0 "db.part/user") ?part]
       |        [(name ?attrIdent) ?attr]
       |        [?attrId ?schemaId ?schemaValue ?tx ?op]
       |        [?schemaId :db/ident ?schemaIdent]
       |        [(name ?schemaIdent) ?schemaAttr]
       |        [(datomic.api/tx->t ?tx) ?t]
       |        [?tx :db/txInstant ?txInst]
       |]""".stripMargin

  private class SchemaComparator extends Comparator[jList[AnyRef]] {
    def compare(l1: jList[AnyRef], l2: jList[AnyRef]): Int = {
      var result = l1.get(tx_).asInstanceOf[jLong].compareTo(l2.get(tx_).asInstanceOf[jLong])
      if (result == 0) result = l1.get(attrId_).asInstanceOf[jLong].compareTo(l2.get(attrId_).asInstanceOf[jLong])
      if (result == 0) result = l1.get(schemaId_).asInstanceOf[jLong].compareTo(l2.get(schemaId_).asInstanceOf[jLong])
      if (result == 0) result = l1.get(op_).asInstanceOf[jBoolean].compareTo(l2.get(op_).asInstanceOf[jBoolean])
      result
    }
  }

  private def opt(row: jList[AnyRef], someData: AnyRef): AnyRef =
    if (row.get(op_).toString == "false") null else someData

  private def schemaElement(schemaAttr: String, mandatory: Boolean): jList[AnyRef] => AnyRef = if (mandatory) {
    // Mandatory values
    schemaAttr match {
      case "valueType"   => (row: jList[AnyRef]) =>
        row.get(schemaValue_).toString.toInt match {
          case 23 => "string"
          case 22 => "long"
          case 57 => "double"
          case 24 => "boolean"
          case 20 => "ref"
          case 25 => "instant"
          case 56 => "uuid"
          case 59 => "uri"
          case 60 => "bigint"
          case 61 => "bigdec"
        }
      case "cardinality" => (row: jList[AnyRef]) =>
        row.get(schemaValue_).toString.toInt match {
          case 35 => "one"
          case 36 => "many"
        }
      case "unique"      => (row: jList[AnyRef]) =>
        row.get(schemaValue_).toString.toInt match {
          case 37 => "value"
          case 38 => "identity"
        }
      case "ident"       => (row: jList[AnyRef]) => row.get(schemaValue_).toString
      case "doc"         => (row: jList[AnyRef]) => row.get(schemaValue_).toString
      case "isComponent" => (row: jList[AnyRef]) => row.get(schemaValue_).asInstanceOf[jBoolean]
      case "noHistory"   => (row: jList[AnyRef]) => row.get(schemaValue_).asInstanceOf[jBoolean]
      case "index"       => (row: jList[AnyRef]) => row.get(schemaValue_).asInstanceOf[jBoolean]
      case "fulltext"    => (row: jList[AnyRef]) => row.get(schemaValue_).asInstanceOf[jBoolean]
    }
  } else {
    // Optional values in pull result format to be processed as non-history Schema molecules
    schemaAttr match {
      case "valueType"   => (row: jList[AnyRef]) =>
        val tpe = row.get(schemaValue_).toString.toInt match {
          case 23 => ":db.type/string"
          case 22 => ":db.type/long"
          case 57 => ":db.type/double"
          case 24 => ":db.type/boolean"
          case 20 => ":db.type/ref"
          case 25 => ":db.type/instant"
          case 56 => ":db.type/uuid"
          case 59 => ":db.type/uri"
          case 60 => ":db.type/bigint"
          case 61 => ":db.type/bigdec"
        }
        opt(row, javaMap("" -> javaMap("" -> tpe)))
      case "cardinality" => (row: jList[AnyRef]) =>
        row.get(schemaValue_).toString.toInt match {
          case 35 => opt(row, javaMap("" -> javaMap("" -> ":db.cardinality/one")))
          case 36 => opt(row, javaMap("" -> javaMap("" -> ":db.cardinality/many")))
        }
      case "unique"      => (row: jList[AnyRef]) =>
        row.get(schemaValue_).toString.toInt match {
          case 37 => opt(row, javaMap("" -> javaMap("" -> ":db.unique/value")))
          case 38 => opt(row, javaMap("" -> javaMap("" -> ":db.unique/identity")))
        }
      case "ident"       => (row: jList[AnyRef]) => opt(row, javaMap("" -> row.get(schemaValue_)))
      case "doc"         => (row: jList[AnyRef]) => opt(row, javaMap("" -> row.get(schemaValue_)))
      case "isComponent" => (row: jList[AnyRef]) => opt(row, javaMap("" -> row.get(schemaValue_)))
      case "noHistory"   => (row: jList[AnyRef]) => opt(row, javaMap("" -> row.get(schemaValue_)))
      case "index"       => (row: jList[AnyRef]) => opt(row, javaMap("" -> row.get(schemaValue_)))
      case "fulltext"    => (row: jList[AnyRef]) => opt(row, javaMap("" -> row.get(schemaValue_)))
    }
  }


  private[molecule] def schemaHistoryQuery(
    model: Model
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = try {
    val elements = model.elements
    val generics = elements.map(_.asInstanceOf[Generic])
    if (generics.exists(_.attr == "enumm")) {
      throw MoleculeException(
        "Retrieving historical enum values with `Schema` is not supported since they " +
          "are entities having their own timeline independently from schema attributes. " +
          "Instead, please call `conn.getEnumHistory` to retrieve historical enum values."
      )
    }
    val length                 = elements.length
    var processedMandatory     = 0
    var expectedMandatoryCount = 0
    var add                    = true
    var prevTx                 = 0L.asInstanceOf[AnyRef]
    var tx                     = 0L.asInstanceOf[AnyRef]
    var prevAttrId             = 0L.asInstanceOf[AnyRef]
    var attrId                 = 0L.asInstanceOf[AnyRef]
    var schemaAttr             = ""
    var list                   = new util.ArrayList[AnyRef](length)
    val coll                   = new util.ArrayList[jList[AnyRef]]()

    val schemaAttrIndexes = generics.zipWithIndex.map { case (g, i) =>
      val mandatory = g.attr.last != '$'
      if (mandatory) {
        expectedMandatoryCount += 1
      }
      val schemaAttr        = if (mandatory) g.attr else g.attr.init
      val sharedSchemaAttrs = List("t", "tx", "txInstant", "attrId", "a", "part", "nsFull", "ns", "attr")
      val resolver          = if (sharedSchemaAttrs.contains(schemaAttr)) {
        // Resolver not needed for initiated schema attributes
        (_: jList[AnyRef]) => null
      } else {
        schemaElement(schemaAttr, mandatory)
      }
      schemaAttr -> (i, mandatory, resolver)
    }.toMap

    def initElement(schemaAttr: String): jList[AnyRef] => AnyRef = schemaAttr match {
      case "t"         => (row: jList[AnyRef]) => processedMandatory += 1; row.get(t_)
      case "tx"        => (row: jList[AnyRef]) => processedMandatory += 1; row.get(tx_)
      case "txInstant" => (row: jList[AnyRef]) => processedMandatory += 1; row.get(txInst_)
      case "attrId"    => (row: jList[AnyRef]) => processedMandatory += 1; row.get(attrId_)
      case "a"         => (row: jList[AnyRef]) => processedMandatory += 1; row.get(a_)
      case "part"      => (row: jList[AnyRef]) => processedMandatory += 1; row.get(part_)
      case "nsFull"    => (row: jList[AnyRef]) => processedMandatory += 1; row.get(nsFull_)
      case "ns"        => (row: jList[AnyRef]) => processedMandatory += 1; row.get(ns_)
      case "attr"      => (row: jList[AnyRef]) => processedMandatory += 1; row.get(attr_)
      case _           => (_: jList[AnyRef]) => null
    }

    // Initializer of row lists with arity of model elements length
    val initList: jList[AnyRef] => Unit = {
      length match {
        case 1  =>
          val resolve1 = initElement(generics(0).attr)
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
          }
        case 2  =>
          val resolve1 = initElement(generics(0).attr)
          val resolve2 = initElement(generics(1).attr)
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
          }
        case 3  =>
          val resolve1 = initElement(generics(0).attr)
          val resolve2 = initElement(generics(1).attr)
          val resolve3 = initElement(generics(2).attr)
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
          }
        case 4  =>
          val resolve1 = initElement(generics(0).attr)
          val resolve2 = initElement(generics(1).attr)
          val resolve3 = initElement(generics(2).attr)
          val resolve4 = initElement(generics(3).attr)
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
          }
        case 5  =>
          val resolve1 = initElement(generics(0).attr)
          val resolve2 = initElement(generics(1).attr)
          val resolve3 = initElement(generics(2).attr)
          val resolve4 = initElement(generics(3).attr)
          val resolve5 = initElement(generics(4).attr)
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
          }
        case 6  =>
          val resolve1 = initElement(generics(0).attr)
          val resolve2 = initElement(generics(1).attr)
          val resolve3 = initElement(generics(2).attr)
          val resolve4 = initElement(generics(3).attr)
          val resolve5 = initElement(generics(4).attr)
          val resolve6 = initElement(generics(5).attr)
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
          }
        case 7  =>
          val resolve1 = initElement(generics(0).attr)
          val resolve2 = initElement(generics(1).attr)
          val resolve3 = initElement(generics(2).attr)
          val resolve4 = initElement(generics(3).attr)
          val resolve5 = initElement(generics(4).attr)
          val resolve6 = initElement(generics(5).attr)
          val resolve7 = initElement(generics(6).attr)
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
          val resolve1 = initElement(generics(0).attr)
          val resolve2 = initElement(generics(1).attr)
          val resolve3 = initElement(generics(2).attr)
          val resolve4 = initElement(generics(3).attr)
          val resolve5 = initElement(generics(4).attr)
          val resolve6 = initElement(generics(5).attr)
          val resolve7 = initElement(generics(6).attr)
          val resolve8 = initElement(generics(7).attr)
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
          val resolve1 = initElement(generics(0).attr)
          val resolve2 = initElement(generics(1).attr)
          val resolve3 = initElement(generics(2).attr)
          val resolve4 = initElement(generics(3).attr)
          val resolve5 = initElement(generics(4).attr)
          val resolve6 = initElement(generics(5).attr)
          val resolve7 = initElement(generics(6).attr)
          val resolve8 = initElement(generics(7).attr)
          val resolve9 = initElement(generics(8).attr)
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
          val resolve1  = initElement(generics(0).attr)
          val resolve2  = initElement(generics(1).attr)
          val resolve3  = initElement(generics(2).attr)
          val resolve4  = initElement(generics(3).attr)
          val resolve5  = initElement(generics(4).attr)
          val resolve6  = initElement(generics(5).attr)
          val resolve7  = initElement(generics(6).attr)
          val resolve8  = initElement(generics(7).attr)
          val resolve9  = initElement(generics(8).attr)
          val resolve10 = initElement(generics(9).attr)
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
          val resolve1  = initElement(generics(0).attr)
          val resolve2  = initElement(generics(1).attr)
          val resolve3  = initElement(generics(2).attr)
          val resolve4  = initElement(generics(3).attr)
          val resolve5  = initElement(generics(4).attr)
          val resolve6  = initElement(generics(5).attr)
          val resolve7  = initElement(generics(6).attr)
          val resolve8  = initElement(generics(7).attr)
          val resolve9  = initElement(generics(8).attr)
          val resolve10 = initElement(generics(9).attr)
          val resolve11 = initElement(generics(10).attr)
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
          val resolve1  = initElement(generics(0).attr)
          val resolve2  = initElement(generics(1).attr)
          val resolve3  = initElement(generics(2).attr)
          val resolve4  = initElement(generics(3).attr)
          val resolve5  = initElement(generics(4).attr)
          val resolve6  = initElement(generics(5).attr)
          val resolve7  = initElement(generics(6).attr)
          val resolve8  = initElement(generics(7).attr)
          val resolve9  = initElement(generics(8).attr)
          val resolve10 = initElement(generics(9).attr)
          val resolve11 = initElement(generics(10).attr)
          val resolve12 = initElement(generics(11).attr)
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
          val resolve1  = initElement(generics(0).attr)
          val resolve2  = initElement(generics(1).attr)
          val resolve3  = initElement(generics(2).attr)
          val resolve4  = initElement(generics(3).attr)
          val resolve5  = initElement(generics(4).attr)
          val resolve6  = initElement(generics(5).attr)
          val resolve7  = initElement(generics(6).attr)
          val resolve8  = initElement(generics(7).attr)
          val resolve9  = initElement(generics(8).attr)
          val resolve10 = initElement(generics(9).attr)
          val resolve11 = initElement(generics(10).attr)
          val resolve12 = initElement(generics(11).attr)
          val resolve13 = initElement(generics(12).attr)
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
          val resolve1  = initElement(generics(0).attr)
          val resolve2  = initElement(generics(1).attr)
          val resolve3  = initElement(generics(2).attr)
          val resolve4  = initElement(generics(3).attr)
          val resolve5  = initElement(generics(4).attr)
          val resolve6  = initElement(generics(5).attr)
          val resolve7  = initElement(generics(6).attr)
          val resolve8  = initElement(generics(7).attr)
          val resolve9  = initElement(generics(8).attr)
          val resolve10 = initElement(generics(9).attr)
          val resolve11 = initElement(generics(10).attr)
          val resolve12 = initElement(generics(11).attr)
          val resolve13 = initElement(generics(12).attr)
          val resolve14 = initElement(generics(13).attr)
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
          val resolve1  = initElement(generics(0).attr)
          val resolve2  = initElement(generics(1).attr)
          val resolve3  = initElement(generics(2).attr)
          val resolve4  = initElement(generics(3).attr)
          val resolve5  = initElement(generics(4).attr)
          val resolve6  = initElement(generics(5).attr)
          val resolve7  = initElement(generics(6).attr)
          val resolve8  = initElement(generics(7).attr)
          val resolve9  = initElement(generics(8).attr)
          val resolve10 = initElement(generics(9).attr)
          val resolve11 = initElement(generics(10).attr)
          val resolve12 = initElement(generics(11).attr)
          val resolve13 = initElement(generics(12).attr)
          val resolve14 = initElement(generics(13).attr)
          val resolve15 = initElement(generics(14).attr)
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
          val resolve1  = initElement(generics(0).attr)
          val resolve2  = initElement(generics(1).attr)
          val resolve3  = initElement(generics(2).attr)
          val resolve4  = initElement(generics(3).attr)
          val resolve5  = initElement(generics(4).attr)
          val resolve6  = initElement(generics(5).attr)
          val resolve7  = initElement(generics(6).attr)
          val resolve8  = initElement(generics(7).attr)
          val resolve9  = initElement(generics(8).attr)
          val resolve10 = initElement(generics(9).attr)
          val resolve11 = initElement(generics(10).attr)
          val resolve12 = initElement(generics(11).attr)
          val resolve13 = initElement(generics(12).attr)
          val resolve14 = initElement(generics(13).attr)
          val resolve15 = initElement(generics(14).attr)
          val resolve16 = initElement(generics(15).attr)
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
          val resolve1  = initElement(generics(0).attr)
          val resolve2  = initElement(generics(1).attr)
          val resolve3  = initElement(generics(2).attr)
          val resolve4  = initElement(generics(3).attr)
          val resolve5  = initElement(generics(4).attr)
          val resolve6  = initElement(generics(5).attr)
          val resolve7  = initElement(generics(6).attr)
          val resolve8  = initElement(generics(7).attr)
          val resolve9  = initElement(generics(8).attr)
          val resolve10 = initElement(generics(9).attr)
          val resolve11 = initElement(generics(10).attr)
          val resolve12 = initElement(generics(11).attr)
          val resolve13 = initElement(generics(12).attr)
          val resolve14 = initElement(generics(13).attr)
          val resolve15 = initElement(generics(14).attr)
          val resolve16 = initElement(generics(15).attr)
          val resolve17 = initElement(generics(16).attr)
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
          val resolve1  = initElement(generics(0).attr)
          val resolve2  = initElement(generics(1).attr)
          val resolve3  = initElement(generics(2).attr)
          val resolve4  = initElement(generics(3).attr)
          val resolve5  = initElement(generics(4).attr)
          val resolve6  = initElement(generics(5).attr)
          val resolve7  = initElement(generics(6).attr)
          val resolve8  = initElement(generics(7).attr)
          val resolve9  = initElement(generics(8).attr)
          val resolve10 = initElement(generics(9).attr)
          val resolve11 = initElement(generics(10).attr)
          val resolve12 = initElement(generics(11).attr)
          val resolve13 = initElement(generics(12).attr)
          val resolve14 = initElement(generics(13).attr)
          val resolve15 = initElement(generics(14).attr)
          val resolve16 = initElement(generics(15).attr)
          val resolve17 = initElement(generics(16).attr)
          val resolve18 = initElement(generics(17).attr)
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
          val resolve1  = initElement(generics(0).attr)
          val resolve2  = initElement(generics(1).attr)
          val resolve3  = initElement(generics(2).attr)
          val resolve4  = initElement(generics(3).attr)
          val resolve5  = initElement(generics(4).attr)
          val resolve6  = initElement(generics(5).attr)
          val resolve7  = initElement(generics(6).attr)
          val resolve8  = initElement(generics(7).attr)
          val resolve9  = initElement(generics(8).attr)
          val resolve10 = initElement(generics(9).attr)
          val resolve11 = initElement(generics(10).attr)
          val resolve12 = initElement(generics(11).attr)
          val resolve13 = initElement(generics(12).attr)
          val resolve14 = initElement(generics(13).attr)
          val resolve15 = initElement(generics(14).attr)
          val resolve16 = initElement(generics(15).attr)
          val resolve17 = initElement(generics(16).attr)
          val resolve18 = initElement(generics(17).attr)
          val resolve19 = initElement(generics(18).attr)
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

    historyQuery(query).map { jColl =>
      // Sort by tx, attrId, schemaId, op
      val rows: java.util.ArrayList[jList[AnyRef]] = new java.util.ArrayList(jColl)
      Collections.sort(rows, new SchemaComparator())

      val last = rows.size()
      var i    = 0
      rows.forEach { row =>
        i += 1
        add = row.get(op_).asInstanceOf[jBoolean]
        tx = row.get(tx_)
        attrId = row.get(attrId_)
        schemaAttr = row.get(schemaAttr_).toString

        //        if (tx != prevTx)
        //          println("")
        //        println(row)

        // Init list
        if (tx != prevTx || attrId != prevAttrId) {
          if (processedMandatory >= expectedMandatoryCount) {
            // add prev collected row if all mandatory values have been collected
            coll.add(list)
            processedMandatory = 0
          }
          prevTx = tx
          prevAttrId = attrId
          list = new util.ArrayList[AnyRef](length)
          initList(row)
        }

        // Add schema attribute value if requested
        schemaAttrIndexes.get(schemaAttr).foreach { case (i, mandatory, resolver) =>
          if (mandatory) {
            processedMandatory += 1
          }
          //          println(schemaAttr + ": " + resolver(row))
          list.set(i, resolver(row))
        }

        if (i == last && processedMandatory >= expectedMandatoryCount) {
          // add last collected row if all mandatory values have been collected
          coll.add(list)
          processedMandatory = 0
        }
      }
      coll
    }
  } catch {
    case NonFatal(exc) => Future.failed(exc)
  }
}



