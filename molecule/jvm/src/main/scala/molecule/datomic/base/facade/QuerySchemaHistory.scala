package molecule.datomic.base.facade

import java.lang.{Boolean => jBoolean, Long => jLong}
import java.util
import java.util.{Collections, Comparator, Collection => jCollection, List => jList}
import molecule.core.ast.elements._
import molecule.core.dto.SchemaAttr
import molecule.core.exceptions.MoleculeException
import molecule.core.util.JavaUtil
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


trait QuerySchemaHistory extends JavaUtil { self: Conn_Jvm =>

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

  //  private val schemaQuery =
  //    """[:find  ?t ?tx ?txInstant ?op ?attrId ?a ?part ?nsFull ?ns ?attr ?schemaId ?schemaAttr ?schemaValue
  //      | :in    $  $dbCurrent
  //      | :where [_ :db.install/attribute ?attrId]
  //      |        [$dbCurrent ?attrId :db/ident ?attrIdent]
  //      |        [(str ?attrIdent) ?a]
  //      |        [(namespace ?attrIdent) ?nsFull0]
  //      |        [(if (= (subs ?nsFull0 0 1) "-") (subs ?nsFull0 1) ?nsFull0) ?nsFull]
  //      |        [(.matches ^String ?nsFull "^(db|db.alter|db.excise|db.install|db.part|db.sys|fressian|db.entity|db.attr)") ?sys]
  //      |        [(= ?sys false)]
  //      |        [(.contains ^String ?nsFull "_") ?isPart]
  //      |        [(.split ^String ?nsFull "_") ?nsParts]
  //      |        [(first ?nsParts) ?part0]
  //      |        [(last ?nsParts) ?ns]
  //      |        [(if ?isPart ?part0 "db.part/user") ?part]
  //      |        [(name ?attrIdent) ?attr]
  //      |        [?attrId ?schemaId ?schemaValue ?tx ?op]
  //      |        [$dbCurrent ?schemaId :db/ident ?schemaIdent]
  //      |        [(name ?schemaIdent) ?schemaAttr]
  //      |        [(datomic.api/tx->t ?tx) ?t]
  //      |        [?tx :db/txInstant ?txInstant]
  //      |]""".stripMargin

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

  private def schemaElement(schemaAttr: String, mandatory: Boolean): jList[AnyRef] => AnyRef = {
    if (mandatory) {
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
  }


  private[molecule] def fetchSchemaHistory(
    schemaAttrs: Seq[SchemaAttr],
    queryString: String
  )(implicit ec: ExecutionContext): Future[jCollection[jList[AnyRef]]] = try {
    if (schemaAttrs.exists(_.attr == "enumm")) {
      throw MoleculeException(
        "Retrieving historical enum values with `Schema` is not supported since they " +
          "are entities having their own timeline independently from schema attributes. " +
          "Instead, please call `conn.getEnumHistory` to retrieve historical enum values."
      )
    }

//    schemaAttrs.foreach(println)

    val inputs = schemaAttrs.collect {
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
//    println("INPUTS: " + inputs)
    //    println("INPUTS: " + inputs.head.get(0).getClass)

    var mandatoryAttrs         = Seq.empty[String]
    var expectedMandatoryCount = 0
    var i                      = -1
    val mandatoryAttrMap       = schemaAttrs.flatMap { case SchemaAttr(attrClean, attr, _, _) =>
      val mandatory = attrClean == attr
      if (mandatory) {
        expectedMandatoryCount += 1
      }
      val sharedSchemaAttrs = List(
        "t", "tx", "txInstant", "attrId", "a", "part", "nsFull", "ns", "attr"
      )
      val resolver          = if (sharedSchemaAttrs.contains(attrClean)) {
        // Resolver not needed for initiated schema attributes
        (_: jList[AnyRef]) => null
      } else {
        schemaElement(attrClean, mandatory)
      }
      if (mandatory) {
        i += 1
        mandatoryAttrs = mandatoryAttrs :+ attrClean
        Seq(attrClean -> (i, mandatory, resolver))
      } else Nil
    }.toMap

//    println("schemaAttrIndexes:\n" + mandatoryAttrMap.mkString("\n"))

    val length = mandatoryAttrMap.size
//    println("Length: " + length)

    var processedMandatory = 0
    var add                = true
    var prevTx             = 0L.asInstanceOf[AnyRef]
    var tx                 = 0L.asInstanceOf[AnyRef]
    var prevAttrId         = 0L.asInstanceOf[AnyRef]
    var attrId             = 0L.asInstanceOf[AnyRef]
    var schemaAttr         = ""
    var list               = new util.ArrayList[AnyRef](length)
    val coll               = new util.ArrayList[jList[AnyRef]]()


    def initElement(attrClean: String): jList[AnyRef] => AnyRef = attrClean match {
      case "t"         => (row: jList[AnyRef]) => processedMandatory += 1; row.get(t_)
      case "tx"        => (row: jList[AnyRef]) => processedMandatory += 1; row.get(tx_)
      case "txInstant" => (row: jList[AnyRef]) => processedMandatory += 1; row.get(txInstant_)
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
          val resolve1 = initElement(mandatoryAttrs(0))
          (row: jList[AnyRef]) => {
            //            val v = resolve1(row)
            //            println("r: " + row)
            //            println("v: " + v)
            //            list.add(v)
            list.add(resolve1(row))
          }
        case 2  =>
          val resolve1 = initElement(mandatoryAttrs(0))
          val resolve2 = initElement(mandatoryAttrs(1))
          (row: jList[AnyRef]) => {
            //            val v1 = resolve1(row)
            //            println("v1: " + v1)
            //            list.add(v1)
            //
            //            val v2 = resolve2(row)
            //            println("v2: " + v2)
            //            list.add(v2)
            list.add(resolve1(row))
            list.add(resolve2(row))
          }
        case 3  =>
          val resolve1 = initElement(mandatoryAttrs(0))
          val resolve2 = initElement(mandatoryAttrs(1))
          val resolve3 = initElement(mandatoryAttrs(2))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
          }
        case 4  =>
          val resolve1 = initElement(mandatoryAttrs(0))
          val resolve2 = initElement(mandatoryAttrs(1))
          val resolve3 = initElement(mandatoryAttrs(2))
          val resolve4 = initElement(mandatoryAttrs(3))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
          }
        case 5  =>
          val resolve1 = initElement(mandatoryAttrs(0))
          val resolve2 = initElement(mandatoryAttrs(1))
          val resolve3 = initElement(mandatoryAttrs(2))
          val resolve4 = initElement(mandatoryAttrs(3))
          val resolve5 = initElement(mandatoryAttrs(4))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
          }
        case 6  =>
          val resolve1 = initElement(mandatoryAttrs(0))
          val resolve2 = initElement(mandatoryAttrs(1))
          val resolve3 = initElement(mandatoryAttrs(2))
          val resolve4 = initElement(mandatoryAttrs(3))
          val resolve5 = initElement(mandatoryAttrs(4))
          val resolve6 = initElement(mandatoryAttrs(5))
          (row: jList[AnyRef]) => {
            list.add(resolve1(row))
            list.add(resolve2(row))
            list.add(resolve3(row))
            list.add(resolve4(row))
            list.add(resolve5(row))
            list.add(resolve6(row))
          }
        case 7  =>
          val resolve1 = initElement(mandatoryAttrs(0))
          val resolve2 = initElement(mandatoryAttrs(1))
          val resolve3 = initElement(mandatoryAttrs(2))
          val resolve4 = initElement(mandatoryAttrs(3))
          val resolve5 = initElement(mandatoryAttrs(4))
          val resolve6 = initElement(mandatoryAttrs(5))
          val resolve7 = initElement(mandatoryAttrs(6))
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
          val resolve1 = initElement(mandatoryAttrs(0))
          val resolve2 = initElement(mandatoryAttrs(1))
          val resolve3 = initElement(mandatoryAttrs(2))
          val resolve4 = initElement(mandatoryAttrs(3))
          val resolve5 = initElement(mandatoryAttrs(4))
          val resolve6 = initElement(mandatoryAttrs(5))
          val resolve7 = initElement(mandatoryAttrs(6))
          val resolve8 = initElement(mandatoryAttrs(7))
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
          val resolve1 = initElement(mandatoryAttrs(0))
          val resolve2 = initElement(mandatoryAttrs(1))
          val resolve3 = initElement(mandatoryAttrs(2))
          val resolve4 = initElement(mandatoryAttrs(3))
          val resolve5 = initElement(mandatoryAttrs(4))
          val resolve6 = initElement(mandatoryAttrs(5))
          val resolve7 = initElement(mandatoryAttrs(6))
          val resolve8 = initElement(mandatoryAttrs(7))
          val resolve9 = initElement(mandatoryAttrs(8))
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
          val resolve1  = initElement(mandatoryAttrs(0))
          val resolve2  = initElement(mandatoryAttrs(1))
          val resolve3  = initElement(mandatoryAttrs(2))
          val resolve4  = initElement(mandatoryAttrs(3))
          val resolve5  = initElement(mandatoryAttrs(4))
          val resolve6  = initElement(mandatoryAttrs(5))
          val resolve7  = initElement(mandatoryAttrs(6))
          val resolve8  = initElement(mandatoryAttrs(7))
          val resolve9  = initElement(mandatoryAttrs(8))
          val resolve10 = initElement(mandatoryAttrs(9))
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
          val resolve1  = initElement(mandatoryAttrs(0))
          val resolve2  = initElement(mandatoryAttrs(1))
          val resolve3  = initElement(mandatoryAttrs(2))
          val resolve4  = initElement(mandatoryAttrs(3))
          val resolve5  = initElement(mandatoryAttrs(4))
          val resolve6  = initElement(mandatoryAttrs(5))
          val resolve7  = initElement(mandatoryAttrs(6))
          val resolve8  = initElement(mandatoryAttrs(7))
          val resolve9  = initElement(mandatoryAttrs(8))
          val resolve10 = initElement(mandatoryAttrs(9))
          val resolve11 = initElement(mandatoryAttrs(10))
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
          val resolve1  = initElement(mandatoryAttrs(0))
          val resolve2  = initElement(mandatoryAttrs(1))
          val resolve3  = initElement(mandatoryAttrs(2))
          val resolve4  = initElement(mandatoryAttrs(3))
          val resolve5  = initElement(mandatoryAttrs(4))
          val resolve6  = initElement(mandatoryAttrs(5))
          val resolve7  = initElement(mandatoryAttrs(6))
          val resolve8  = initElement(mandatoryAttrs(7))
          val resolve9  = initElement(mandatoryAttrs(8))
          val resolve10 = initElement(mandatoryAttrs(9))
          val resolve11 = initElement(mandatoryAttrs(10))
          val resolve12 = initElement(mandatoryAttrs(11))
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
          val resolve1  = initElement(mandatoryAttrs(0))
          val resolve2  = initElement(mandatoryAttrs(1))
          val resolve3  = initElement(mandatoryAttrs(2))
          val resolve4  = initElement(mandatoryAttrs(3))
          val resolve5  = initElement(mandatoryAttrs(4))
          val resolve6  = initElement(mandatoryAttrs(5))
          val resolve7  = initElement(mandatoryAttrs(6))
          val resolve8  = initElement(mandatoryAttrs(7))
          val resolve9  = initElement(mandatoryAttrs(8))
          val resolve10 = initElement(mandatoryAttrs(9))
          val resolve11 = initElement(mandatoryAttrs(10))
          val resolve12 = initElement(mandatoryAttrs(11))
          val resolve13 = initElement(mandatoryAttrs(12))
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
          val resolve1  = initElement(mandatoryAttrs(0))
          val resolve2  = initElement(mandatoryAttrs(1))
          val resolve3  = initElement(mandatoryAttrs(2))
          val resolve4  = initElement(mandatoryAttrs(3))
          val resolve5  = initElement(mandatoryAttrs(4))
          val resolve6  = initElement(mandatoryAttrs(5))
          val resolve7  = initElement(mandatoryAttrs(6))
          val resolve8  = initElement(mandatoryAttrs(7))
          val resolve9  = initElement(mandatoryAttrs(8))
          val resolve10 = initElement(mandatoryAttrs(9))
          val resolve11 = initElement(mandatoryAttrs(10))
          val resolve12 = initElement(mandatoryAttrs(11))
          val resolve13 = initElement(mandatoryAttrs(12))
          val resolve14 = initElement(mandatoryAttrs(13))
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
          val resolve1  = initElement(mandatoryAttrs(0))
          val resolve2  = initElement(mandatoryAttrs(1))
          val resolve3  = initElement(mandatoryAttrs(2))
          val resolve4  = initElement(mandatoryAttrs(3))
          val resolve5  = initElement(mandatoryAttrs(4))
          val resolve6  = initElement(mandatoryAttrs(5))
          val resolve7  = initElement(mandatoryAttrs(6))
          val resolve8  = initElement(mandatoryAttrs(7))
          val resolve9  = initElement(mandatoryAttrs(8))
          val resolve10 = initElement(mandatoryAttrs(9))
          val resolve11 = initElement(mandatoryAttrs(10))
          val resolve12 = initElement(mandatoryAttrs(11))
          val resolve13 = initElement(mandatoryAttrs(12))
          val resolve14 = initElement(mandatoryAttrs(13))
          val resolve15 = initElement(mandatoryAttrs(14))
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
          val resolve1  = initElement(mandatoryAttrs(0))
          val resolve2  = initElement(mandatoryAttrs(1))
          val resolve3  = initElement(mandatoryAttrs(2))
          val resolve4  = initElement(mandatoryAttrs(3))
          val resolve5  = initElement(mandatoryAttrs(4))
          val resolve6  = initElement(mandatoryAttrs(5))
          val resolve7  = initElement(mandatoryAttrs(6))
          val resolve8  = initElement(mandatoryAttrs(7))
          val resolve9  = initElement(mandatoryAttrs(8))
          val resolve10 = initElement(mandatoryAttrs(9))
          val resolve11 = initElement(mandatoryAttrs(10))
          val resolve12 = initElement(mandatoryAttrs(11))
          val resolve13 = initElement(mandatoryAttrs(12))
          val resolve14 = initElement(mandatoryAttrs(13))
          val resolve15 = initElement(mandatoryAttrs(14))
          val resolve16 = initElement(mandatoryAttrs(15))
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
          val resolve1  = initElement(mandatoryAttrs(0))
          val resolve2  = initElement(mandatoryAttrs(1))
          val resolve3  = initElement(mandatoryAttrs(2))
          val resolve4  = initElement(mandatoryAttrs(3))
          val resolve5  = initElement(mandatoryAttrs(4))
          val resolve6  = initElement(mandatoryAttrs(5))
          val resolve7  = initElement(mandatoryAttrs(6))
          val resolve8  = initElement(mandatoryAttrs(7))
          val resolve9  = initElement(mandatoryAttrs(8))
          val resolve10 = initElement(mandatoryAttrs(9))
          val resolve11 = initElement(mandatoryAttrs(10))
          val resolve12 = initElement(mandatoryAttrs(11))
          val resolve13 = initElement(mandatoryAttrs(12))
          val resolve14 = initElement(mandatoryAttrs(13))
          val resolve15 = initElement(mandatoryAttrs(14))
          val resolve16 = initElement(mandatoryAttrs(15))
          val resolve17 = initElement(mandatoryAttrs(16))
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
          val resolve1  = initElement(mandatoryAttrs(0))
          val resolve2  = initElement(mandatoryAttrs(1))
          val resolve3  = initElement(mandatoryAttrs(2))
          val resolve4  = initElement(mandatoryAttrs(3))
          val resolve5  = initElement(mandatoryAttrs(4))
          val resolve6  = initElement(mandatoryAttrs(5))
          val resolve7  = initElement(mandatoryAttrs(6))
          val resolve8  = initElement(mandatoryAttrs(7))
          val resolve9  = initElement(mandatoryAttrs(8))
          val resolve10 = initElement(mandatoryAttrs(9))
          val resolve11 = initElement(mandatoryAttrs(10))
          val resolve12 = initElement(mandatoryAttrs(11))
          val resolve13 = initElement(mandatoryAttrs(12))
          val resolve14 = initElement(mandatoryAttrs(13))
          val resolve15 = initElement(mandatoryAttrs(14))
          val resolve16 = initElement(mandatoryAttrs(15))
          val resolve17 = initElement(mandatoryAttrs(16))
          val resolve18 = initElement(mandatoryAttrs(17))
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
          val resolve1  = initElement(mandatoryAttrs(0))
          val resolve2  = initElement(mandatoryAttrs(1))
          val resolve3  = initElement(mandatoryAttrs(2))
          val resolve4  = initElement(mandatoryAttrs(3))
          val resolve5  = initElement(mandatoryAttrs(4))
          val resolve6  = initElement(mandatoryAttrs(5))
          val resolve7  = initElement(mandatoryAttrs(6))
          val resolve8  = initElement(mandatoryAttrs(7))
          val resolve9  = initElement(mandatoryAttrs(8))
          val resolve10 = initElement(mandatoryAttrs(9))
          val resolve11 = initElement(mandatoryAttrs(10))
          val resolve12 = initElement(mandatoryAttrs(11))
          val resolve13 = initElement(mandatoryAttrs(12))
          val resolve14 = initElement(mandatoryAttrs(13))
          val resolve15 = initElement(mandatoryAttrs(14))
          val resolve16 = initElement(mandatoryAttrs(15))
          val resolve17 = initElement(mandatoryAttrs(16))
          val resolve18 = initElement(mandatoryAttrs(17))
          val resolve19 = initElement(mandatoryAttrs(18))
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

    historyQuery(queryString, inputs).map { jColl =>
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
        mandatoryAttrMap.get(schemaAttr).foreach { case (i, mandatory, resolver) =>
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



