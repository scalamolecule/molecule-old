package molecule.core.marshalling

import java.net.URI
import java.util.{Date, UUID}

case class QueryResult(
  oneString: Seq[Array[String]] = Nil,
  oneInt: Seq[Array[Int]] = Nil,
  oneLong: Seq[Array[Long]] = Nil,
  oneFloat: Seq[Array[Float]] = Nil,
  oneDouble: Seq[Array[Double]] = Nil,
  oneBoolean: Seq[Array[Boolean]] = Nil,
  oneDate: Seq[Array[Date]] = Nil,
  oneUUID: Seq[Array[UUID]] = Nil,
  oneURI: Seq[Array[URI]] = Nil,
  oneBigInt: Seq[Array[BigInt]] = Nil,
  oneBigDecimal: Seq[Array[BigDecimal]] = Nil,

  optOneString: Seq[Array[Option[String]]] = Nil,
  optOneInt: Seq[Array[Option[Int]]] = Nil,
  optOneLong: Seq[Array[Option[Long]]] = Nil,
  optOneFloat: Seq[Array[Option[Float]]] = Nil,
  optOneDouble: Seq[Array[Option[Double]]] = Nil,
  optOneBoolean: Seq[Array[Option[Boolean]]] = Nil,
  optOneDate: Seq[Array[Option[Date]]] = Nil,
  optOneUUID: Seq[Array[Option[UUID]]] = Nil,
  optOneURI: Seq[Array[Option[URI]]] = Nil,
  optOneBigInt: Seq[Array[Option[BigInt]]] = Nil,
  optOneBigDecimal: Seq[Array[Option[BigDecimal]]] = Nil,

  manyString: Seq[Array[Set[String]]] = Nil,
  manyInt: Seq[Array[Set[Int]]] = Nil,
  manyLong: Seq[Array[Set[Long]]] = Nil,
  manyFloat: Seq[Array[Set[Float]]] = Nil,
  manyDouble: Seq[Array[Set[Double]]] = Nil,
  manyBoolean: Seq[Array[Set[Boolean]]] = Nil,
  manyDate: Seq[Array[Set[Date]]] = Nil,
  manyUUID: Seq[Array[Set[UUID]]] = Nil,
  manyURI: Seq[Array[Set[URI]]] = Nil,
  manyBigInt: Seq[Array[Set[BigInt]]] = Nil,
  manyBigDecimal: Seq[Array[Set[BigDecimal]]] = Nil,

  optManyString: Seq[Array[Option[Set[String]]]] = Nil,
  optManyInt: Seq[Array[Option[Set[Int]]]] = Nil,
  optManyLong: Seq[Array[Option[Set[Long]]]] = Nil,
  optManyFloat: Seq[Array[Option[Set[Float]]]] = Nil,
  optManyDouble: Seq[Array[Option[Set[Double]]]] = Nil,
  optManyBoolean: Seq[Array[Option[Set[Boolean]]]] = Nil,
  optManyDate: Seq[Array[Option[Set[Date]]]] = Nil,
  optManyUUID: Seq[Array[Option[Set[UUID]]]] = Nil,
  optManyURI: Seq[Array[Option[Set[URI]]]] = Nil,
  optManyBigInt: Seq[Array[Option[Set[BigInt]]]] = Nil,
  optManyBigDecimal: Seq[Array[Option[Set[BigDecimal]]]] = Nil,

  mapString: Seq[Array[Map[String, String]]] = Nil,
  mapInt: Seq[Array[Map[String, Int]]] = Nil,
  mapLong: Seq[Array[Map[String, Long]]] = Nil,
  mapFloat: Seq[Array[Map[String, Float]]] = Nil,
  mapDouble: Seq[Array[Map[String, Double]]] = Nil,
  mapBoolean: Seq[Array[Map[String, Boolean]]] = Nil,
  mapDate: Seq[Array[Map[String, Date]]] = Nil,
  mapUUID: Seq[Array[Map[String, UUID]]] = Nil,
  mapURI: Seq[Array[Map[String, URI]]] = Nil,
  mapBigInt: Seq[Array[Map[String, BigInt]]] = Nil,
  mapBigDecimal: Seq[Array[Map[String, BigDecimal]]] = Nil,

  optMapString: Seq[Array[Option[Map[String, String]]]] = Nil,
  optMapInt: Seq[Array[Option[Map[String, Int]]]] = Nil,
  optMapLong: Seq[Array[Option[Map[String, Long]]]] = Nil,
  optMapFloat: Seq[Array[Option[Map[String, Float]]]] = Nil,
  optMapDouble: Seq[Array[Option[Map[String, Double]]]] = Nil,
  optMapBoolean: Seq[Array[Option[Map[String, Boolean]]]] = Nil,
  optMapDate: Seq[Array[Option[Map[String, Date]]]] = Nil,
  optMapUUID: Seq[Array[Option[Map[String, UUID]]]] = Nil,
  optMapURI: Seq[Array[Option[Map[String, URI]]]] = Nil,
  optMapBigInt: Seq[Array[Option[Map[String, BigInt]]]] = Nil,
  optMapBigDecimal: Seq[Array[Option[Map[String, BigDecimal]]]] = Nil,

  listOneString: Seq[Array[List[String]]] = Nil,
  listOneInt: Seq[Array[List[Int]]] = Nil,
  listOneLong: Seq[Array[List[Long]]] = Nil,
  listOneFloat: Seq[Array[List[Float]]] = Nil,
  listOneDouble: Seq[Array[List[Double]]] = Nil,
  listOneBoolean: Seq[Array[List[Boolean]]] = Nil,
  listOneDate: Seq[Array[List[Date]]] = Nil,
  listOneUUID: Seq[Array[List[UUID]]] = Nil,
  listOneURI: Seq[Array[List[URI]]] = Nil,
  listOneBigInt: Seq[Array[List[BigInt]]] = Nil,
  listOneBigDecimal: Seq[Array[List[BigDecimal]]] = Nil,

  listManyString: Seq[Array[List[Set[String]]]] = Nil,
  listManyInt: Seq[Array[List[Set[Int]]]] = Nil,
  listManyLong: Seq[Array[List[Set[Long]]]] = Nil,
  listManyFloat: Seq[Array[List[Set[Float]]]] = Nil,
  listManyDouble: Seq[Array[List[Set[Double]]]] = Nil,
  listManyBoolean: Seq[Array[List[Set[Boolean]]]] = Nil,
  listManyDate: Seq[Array[List[Set[Date]]]] = Nil,
  listManyUUID: Seq[Array[List[Set[UUID]]]] = Nil,
  listManyURI: Seq[Array[List[Set[URI]]]] = Nil,
  listManyBigInt: Seq[Array[List[Set[BigInt]]]] = Nil,
  listManyBigDecimal: Seq[Array[List[Set[BigDecimal]]]] = Nil,

  rowCountAll: Int,
  maxRows: Int,
  queryMs: Long
) {
  override def toString: String = {
    val max = 7
    val data   = Seq(
      if (oneString.isEmpty) None else Some("oneString" -> oneString.map(_.toSeq.take(max))),
      if (oneInt.isEmpty) None else Some("oneInt" -> oneInt.map(_.toSeq.take(max))),
      if (oneLong.isEmpty) None else Some("oneLong" -> oneLong.map(_.toSeq.take(max))),
      if (oneFloat.isEmpty) None else Some("oneFloat" -> oneFloat.map(_.toSeq.take(max))),
      if (oneDouble.isEmpty) None else Some("oneDouble" -> oneDouble.map(_.toSeq.take(max))),
      if (oneBoolean.isEmpty) None else Some("oneBoolean" -> oneBoolean.map(_.toSeq.take(max))),
      if (oneDate.isEmpty) None else Some("oneDate" -> oneDate.map(_.toSeq.take(max))),
      if (oneUUID.isEmpty) None else Some("oneUUID" -> oneUUID.map(_.toSeq.take(max))),
      if (oneURI.isEmpty) None else Some("oneURI" -> oneURI.map(_.toSeq.take(max))),
      if (oneBigInt.isEmpty) None else Some("oneBigInt" -> oneBigInt.map(_.toSeq.take(max))),
      if (oneBigDecimal.isEmpty) None else Some("oneBigDecimal" -> oneBigDecimal.map(_.toSeq.take(max))),

      if (optOneString.isEmpty) None else Some("optOneString" -> optOneString.map(_.toSeq.take(max))),
      if (optOneInt.isEmpty) None else Some("optOneInt" -> optOneInt.map(_.toSeq.take(max))),
      if (optOneLong.isEmpty) None else Some("optOneLong" -> optOneLong.map(_.toSeq.take(max))),
      if (optOneFloat.isEmpty) None else Some("optOneFloat" -> optOneFloat.map(_.toSeq.take(max))),
      if (optOneDouble.isEmpty) None else Some("optOneDouble" -> optOneDouble.map(_.toSeq.take(max))),
      if (optOneBoolean.isEmpty) None else Some("optOneBoolean" -> optOneBoolean.map(_.toSeq.take(max))),
      if (optOneDate.isEmpty) None else Some("optOneDate" -> optOneDate.map(_.toSeq.take(max))),
      if (optOneUUID.isEmpty) None else Some("optOneUUID" -> optOneUUID.map(_.toSeq.take(max))),
      if (optOneURI.isEmpty) None else Some("optOneURI" -> optOneURI.map(_.toSeq.take(max))),
      if (optOneBigInt.isEmpty) None else Some("optOneBigInt" -> optOneBigInt.map(_.toSeq.take(max))),
      if (optOneBigDecimal.isEmpty) None else Some("optOneBigDecimal" -> optOneBigDecimal.map(_.toSeq.take(max))),

      if (manyString.isEmpty) None else Some("manyString" -> manyString.map(_.toSeq.take(max))),
      if (manyInt.isEmpty) None else Some("manyInt" -> manyInt.map(_.toSeq.take(max))),
      if (manyLong.isEmpty) None else Some("manyLong" -> manyLong.map(_.toSeq.take(max))),
      if (manyFloat.isEmpty) None else Some("manyFloat" -> manyFloat.map(_.toSeq.take(max))),
      if (manyDouble.isEmpty) None else Some("manyDouble" -> manyDouble.map(_.toSeq.take(max))),
      if (manyBoolean.isEmpty) None else Some("manyBoolean" -> manyBoolean.map(_.toSeq.take(max))),
      if (manyDate.isEmpty) None else Some("manyDate" -> manyDate.map(_.toSeq.take(max))),
      if (manyUUID.isEmpty) None else Some("manyUUID" -> manyUUID.map(_.toSeq.take(max))),
      if (manyURI.isEmpty) None else Some("manyURI" -> manyURI.map(_.toSeq.take(max))),
      if (manyBigInt.isEmpty) None else Some("manyBigInt" -> manyBigInt.map(_.toSeq.take(max))),
      if (manyBigDecimal.isEmpty) None else Some("manyBigDecimal" -> manyBigDecimal.map(_.toSeq.take(max))),

      if (optManyString.isEmpty) None else Some("optManyString" -> optManyString.map(_.toSeq.take(max))),
      if (optManyInt.isEmpty) None else Some("optManyInt" -> optManyInt.map(_.toSeq.take(max))),
      if (optManyLong.isEmpty) None else Some("optManyLong" -> optManyLong.map(_.toSeq.take(max))),
      if (optManyFloat.isEmpty) None else Some("optManyFloat" -> optManyFloat.map(_.toSeq.take(max))),
      if (optManyDouble.isEmpty) None else Some("optManyDouble" -> optManyDouble.map(_.toSeq.take(max))),
      if (optManyBoolean.isEmpty) None else Some("optManyBoolean" -> optManyBoolean.map(_.toSeq.take(max))),
      if (optManyDate.isEmpty) None else Some("optManyDate" -> optManyDate.map(_.toSeq.take(max))),
      if (optManyUUID.isEmpty) None else Some("optManyUUID" -> optManyUUID.map(_.toSeq.take(max))),
      if (optManyURI.isEmpty) None else Some("optManyURI" -> optManyURI.map(_.toSeq.take(max))),
      if (optManyBigInt.isEmpty) None else Some("optManyBigInt" -> optManyBigInt.map(_.toSeq.take(max))),
      if (optManyBigDecimal.isEmpty) None else Some("optManyBigDecimal" -> optManyBigDecimal.map(_.toSeq.take(max))),

      if (mapString.isEmpty) None else Some("mapString" -> mapString.map(_.toSeq.take(max))),
      if (mapInt.isEmpty) None else Some("mapInt" -> mapInt.map(_.toSeq.take(max))),
      if (mapLong.isEmpty) None else Some("mapLong" -> mapLong.map(_.toSeq.take(max))),
      if (mapFloat.isEmpty) None else Some("mapFloat" -> mapFloat.map(_.toSeq.take(max))),
      if (mapDouble.isEmpty) None else Some("mapDouble" -> mapDouble.map(_.toSeq.take(max))),
      if (mapBoolean.isEmpty) None else Some("mapBoolean" -> mapBoolean.map(_.toSeq.take(max))),
      if (mapDate.isEmpty) None else Some("mapDate" -> mapDate.map(_.toSeq.take(max))),
      if (mapUUID.isEmpty) None else Some("mapUUID" -> mapUUID.map(_.toSeq.take(max))),
      if (mapURI.isEmpty) None else Some("mapURI" -> mapURI.map(_.toSeq.take(max))),
      if (mapBigInt.isEmpty) None else Some("mapBigInt" -> mapBigInt.map(_.toSeq.take(max))),
      if (mapBigDecimal.isEmpty) None else Some("mapBigDecimal" -> mapBigDecimal.map(_.toSeq.take(max))),

      if (optMapString.isEmpty) None else Some("optMapString" -> optMapString.map(_.toSeq.take(max))),
      if (optMapInt.isEmpty) None else Some("optMapInt" -> optMapInt.map(_.toSeq.take(max))),
      if (optMapLong.isEmpty) None else Some("optMapLong" -> optMapLong.map(_.toSeq.take(max))),
      if (optMapFloat.isEmpty) None else Some("optMapFloat" -> optMapFloat.map(_.toSeq.take(max))),
      if (optMapDouble.isEmpty) None else Some("optMapDouble" -> optMapDouble.map(_.toSeq.take(max))),
      if (optMapBoolean.isEmpty) None else Some("optMapBoolean" -> optMapBoolean.map(_.toSeq.take(max))),
      if (optMapDate.isEmpty) None else Some("optMapDate" -> optMapDate.map(_.toSeq.take(max))),
      if (optMapUUID.isEmpty) None else Some("optMapUUID" -> optMapUUID.map(_.toSeq.take(max))),
      if (optMapURI.isEmpty) None else Some("optMapURI" -> optMapURI.map(_.toSeq.take(max))),
      if (optMapBigInt.isEmpty) None else Some("optMapBigInt" -> optMapBigInt.map(_.toSeq.take(max))),
      if (optMapBigDecimal.isEmpty) None else Some("optMapBigDecimal" -> optMapBigDecimal.map(_.toSeq.take(max))),

      if (listOneString.isEmpty) None else Some("listOneString" -> listOneString.map(_.toSeq.take(max))),
      if (listOneInt.isEmpty) None else Some("listOneInt" -> listOneInt.map(_.toSeq.take(max))),
      if (listOneLong.isEmpty) None else Some("listOneLong" -> listOneLong.map(_.toSeq.take(max))),
      if (listOneFloat.isEmpty) None else Some("listOneFloat" -> listOneFloat.map(_.toSeq.take(max))),
      if (listOneDouble.isEmpty) None else Some("listOneDouble" -> listOneDouble.map(_.toSeq.take(max))),
      if (listOneBoolean.isEmpty) None else Some("listOneBoolean" -> listOneBoolean.map(_.toSeq.take(max))),
      if (listOneDate.isEmpty) None else Some("listOneDate" -> listOneDate.map(_.toSeq.take(max))),
      if (listOneUUID.isEmpty) None else Some("listOneUUID" -> listOneUUID.map(_.toSeq.take(max))),
      if (listOneURI.isEmpty) None else Some("listOneURI" -> listOneURI.map(_.toSeq.take(max))),
      if (listOneBigInt.isEmpty) None else Some("listOneBigInt" -> listOneBigInt.map(_.toSeq.take(max))),
      if (listOneBigDecimal.isEmpty) None else Some("listOneBigDecimal" -> listOneBigDecimal.map(_.toSeq.take(max))),

      if (listManyString.isEmpty) None else Some("listManyString" -> listManyString.map(_.toSeq.take(max))),
      if (listManyInt.isEmpty) None else Some("listManyInt" -> listManyInt.map(_.toSeq.take(max))),
      if (listManyLong.isEmpty) None else Some("listManyLong" -> listManyLong.map(_.toSeq.take(max))),
      if (listManyFloat.isEmpty) None else Some("listManyFloat" -> listManyFloat.map(_.toSeq.take(max))),
      if (listManyDouble.isEmpty) None else Some("listManyDouble" -> listManyDouble.map(_.toSeq.take(max))),
      if (listManyBoolean.isEmpty) None else Some("listManyBoolean" -> listManyBoolean.map(_.toSeq.take(max))),
      if (listManyDate.isEmpty) None else Some("listManyDate" -> listManyDate.map(_.toSeq.take(max))),
      if (listManyUUID.isEmpty) None else Some("listManyUUID" -> listManyUUID.map(_.toSeq.take(max))),
      if (listManyURI.isEmpty) None else Some("listManyURI" -> listManyURI.map(_.toSeq.take(max))),
      if (listManyBigInt.isEmpty) None else Some("listManyBigInt" -> listManyBigInt.map(_.toSeq.take(max))),
      if (listManyBigDecimal.isEmpty) None else Some("listManyBigDecimal" -> listManyBigDecimal.map(_.toSeq.take(max))),
    ).flatten

    val dataString = if (data.isEmpty) "<no data>" else data.map {
      case (tpe, columns) =>
        val arrays = columns
          .map(col => "Array(" + col.mkString(", ") + ", ...")
          .mkString("\n    ")
        s"$tpe =\n    " + arrays
    }.mkString("\n  ")

    s"""QueryResult(
       |  $dataString
       |  rowCountAll = $rowCountAll
       |  maxRows     = $maxRows
       |  queryMs     = $queryMs
       |)""".stripMargin
  }
}