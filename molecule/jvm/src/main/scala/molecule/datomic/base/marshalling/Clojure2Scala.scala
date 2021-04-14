package molecule.datomic.base.marshalling

import java.lang.{Double => jDouble, Long => jLong}
import java.util.{Date, List => jList, Map => jMap}
import clojure.lang.{Keyword, LazySeq, PersistentHashSet, PersistentVector}
import molecule.core.marshalling.Col
import molecule.core.util.DateHandling
import scala.collection.mutable.ListBuffer

class Clojure2Scala(rowCount: Int)
  extends DateHandling
    with DateStrLocal
{

  protected var strArrays     = /* 1 */ List.empty[Array[Option[String]]]
  protected var numArrays     = /* 2 */ List.empty[Array[Option[Double]]]
  protected var listStrArrays = /* 3 */ List.empty[Array[Option[List[String]]]]
  protected var listNumArrays = /* 4 */ List.empty[Array[Option[List[Double]]]]
  protected var mapStrArrays  = /* 5 */ List.empty[Array[Option[Map[String, String]]]]
  protected var mapNumArrays  = /* 6 */ List.empty[Array[Option[Map[String, Double]]]]
  protected var arrayIndexes  = Map.empty[Int, Int] // colIndex -> arrayIndex

  // uses 1-based indexes (slot 1-6, not 0-5)
  private val indexCounts = new Array[Int](7)

  def updateColTypes(colIndex: Int, arrayNo: Int): Unit = {
    arrayIndexes = arrayIndexes + (colIndex -> indexCounts(arrayNo))
    indexCounts(arrayNo) = indexCounts(arrayNo) + 1
  }


  // Card one ==================================================================

  // Mandatory ------------------------------------

  protected def castMandatoryOneString(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[String]](rowCount)
    strArrays = strArrays :+ array
    updateColTypes(colIndex, 1)
    col.attrType match {
      case "Date" if col.attrExpr == "txInstant" =>
        (row: jList[AnyRef], i: Int) =>
          array(i) = Some(date2strLocal(row.get(colIndex).asInstanceOf[Date]))
      case "Date"                                =>
        (row: jList[AnyRef], i: Int) =>
          array(i) = Some(date2str(row.get(colIndex).asInstanceOf[Date]))
      case _                                     =>
        (row: jList[AnyRef], i: Int) =>
          array(i) = Some(row.get(colIndex).toString)
    }
  }

  protected def castMandatoryOneDouble(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[Double]](rowCount)
    numArrays = numArrays :+ array
    updateColTypes(colIndex, 2)
    col.attrType match {
      case "Int" | "Long" | "ref" | "datom" => (row: jList[AnyRef], i: Int) =>
        array(i) = Some(row.get(colIndex).asInstanceOf[jLong].toDouble)
      case _                                => (row: jList[AnyRef], i: Int) =>
        array(i) = Some(row.get(colIndex).asInstanceOf[jDouble].toDouble)
    }
  }


  // Optional ------------------------------------

  protected def castOptionalOneString(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[String]](rowCount)
    strArrays = strArrays :+ array
    updateColTypes(colIndex, 1)
    col.attrType match {
      case "String" if col.enums.nonEmpty => (row: jList[AnyRef], i: Int) =>
        array(i) = row.get(colIndex) match {
          case null => Option.empty[String]
          case v    => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next
            .asInstanceOf[jMap[String, Keyword]].values.iterator.next.getName)
        }

      case "Date" => (row: jList[AnyRef], i: Int) =>
        array(i) = row.get(colIndex) match {
          case null    => Option.empty[String]
          case v: Date => Some(date2str(v))
          case v       => Some(date2str(v.asInstanceOf[jMap[String, Date]].values.iterator.next))
        }

      case _ => (row: jList[AnyRef], i: Int) =>
        array(i) = row.get(colIndex) match {
          case null          => Option.empty[String]
          case v: jMap[_, _] => Some(v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.toString)
          case v             => Some(v.toString)
        }
    }
  }

  protected def castOptionalOneDouble(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[Double]](rowCount)
    numArrays = numArrays :+ array
    updateColTypes(colIndex, 2)

    col.attrType match {
      case "Int" | "Long" =>
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null       => Option.empty[Double]
            case v: jDouble => Some(v.toDouble)
            case v          => Some(
              v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jLong].toDouble)
          }

      case "ref" =>
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null       => Option.empty[Double]
            case v: jDouble => Some(v.toDouble)
            case v          => Some(v.asInstanceOf[jMap[String, PersistentVector]].values.iterator.next
              .asInstanceOf[jMap[_, _]].get(Keyword.intern("db", "id")).asInstanceOf[jLong].toDouble)
          }

      case _ =>
        (row: jList[AnyRef], i: Int) =>
          array(i) = row.get(colIndex) match {
            case null       => Option.empty[Double]
            case v: jDouble => Some(v.toDouble)
            case v          => Some(
              v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next.asInstanceOf[jDouble].toDouble)
          }
    }
  }


  // Aggregates ------------------------------------

  protected def castAggrInt(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[Double]](rowCount)
    numArrays = numArrays :+ array
    updateColTypes(colIndex, 2)
    (row: jList[AnyRef], i: Int) => array(i) = Some(row.get(colIndex).asInstanceOf[Int].toDouble)
  }

  protected def castAggrDouble(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[Double]](rowCount)
    numArrays = numArrays :+ array
    updateColTypes(colIndex, 2)
    (row: jList[AnyRef], i: Int) => array(i) = Some(row.get(colIndex).asInstanceOf[jDouble].toDouble)
  }

  protected def castAggrSingleSample(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit =
    col.colType match {
      case "string" | "listString" =>
        val array = new Array[Option[String]](rowCount)
        strArrays = strArrays :+ array
        updateColTypes(colIndex, 1)
        col.attrType match {
          case "Date" => (row: jList[AnyRef], i: Int) =>
            array(i) = Some(date2str(row.get(colIndex)
              .asInstanceOf[PersistentVector].iterator.next.asInstanceOf[Date]))
          case _      => (row: jList[AnyRef], i: Int) =>
            array(i) = Some(row.get(colIndex)
              .asInstanceOf[PersistentVector].iterator.next.toString)
        }

      case _ =>
        val array = new Array[Option[Double]](rowCount)
        numArrays = numArrays :+ array
        updateColTypes(colIndex, 2)
        col.attrType match {
          case "Int" | "Long" | "ref" =>
            (row: jList[AnyRef], i: Int) =>
              array(i) = Some(row.get(colIndex)
                .asInstanceOf[PersistentVector].iterator.next.asInstanceOf[jLong].toDouble)
          case _                      =>
            (row: jList[AnyRef], i: Int) =>
              array(i) = Some(row.get(colIndex)
                .asInstanceOf[PersistentVector].iterator.next.asInstanceOf[jDouble].toDouble)
        }
    }

  protected def castAggrListString(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[List[String]]](rowCount)
    listStrArrays = listStrArrays :+ array
    updateColTypes(colIndex, 3)
    col.attrType match {
      case "Date" => (row: jList[AnyRef], i: Int) =>
        val it   = row.get(colIndex).asInstanceOf[PersistentVector].iterator
        var list = ListBuffer.empty[String]
        while (it.hasNext)
          list += date2str(it.next.asInstanceOf[Date])
        array(i) = Some(list.toList)

      case _ => (row: jList[AnyRef], i: Int) =>
        val it   = row.get(colIndex).asInstanceOf[PersistentVector].iterator
        var list = ListBuffer.empty[String]
        while (it.hasNext)
          list += it.next.toString
        array(i) = Some(list.toList)
    }
  }

  protected def castAggrListDouble(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[List[Double]]](rowCount)
    listNumArrays = listNumArrays :+ array
    updateColTypes(colIndex, 4)
    col.attrType match {
      case "Int" | "Long" | "ref" =>
        (row: jList[AnyRef], i: Int) =>
          val it   = row.get(colIndex).asInstanceOf[PersistentVector].iterator
          var list = ListBuffer.empty[Double]
          while (it.hasNext)
            list += it.next.asInstanceOf[jLong].toDouble
          array(i) = Some(list.toList)
      case _                      =>
        (row: jList[AnyRef], i: Int) =>
          val it   = row.get(colIndex).asInstanceOf[PersistentVector].iterator
          var list = ListBuffer.empty[Double]
          while (it.hasNext)
            list += it.next.asInstanceOf[jDouble].toDouble
          array(i) = Some(list.toList)
    }
  }

  protected def castAggrListRandString(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[List[String]]](rowCount)
    listStrArrays = listStrArrays :+ array
    updateColTypes(colIndex, 3)
    col.attrType match {
      case "Date" => (row: jList[AnyRef], i: Int) =>
        val it   = row.get(colIndex).asInstanceOf[LazySeq].iterator
        var list = ListBuffer.empty[String]
        while (it.hasNext)
          list += date2str(it.next.asInstanceOf[Date])
        array(i) = Some(list.toList)

      case _ => (row: jList[AnyRef], i: Int) =>
        val it   = row.get(colIndex).asInstanceOf[LazySeq].iterator
        var list = ListBuffer.empty[String]
        while (it.hasNext)
          list += it.next.toString
        array(i) = Some(list.toList)
    }
  }

  protected def castAggrListRandDouble(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[List[Double]]](rowCount)
    listNumArrays = listNumArrays :+ array
    updateColTypes(colIndex, 4)
    col.attrType match {
      case "Int" | "Long" | "ref" =>
        (row: jList[AnyRef], i: Int) =>
          val it   = row.get(colIndex).asInstanceOf[LazySeq].iterator
          var list = ListBuffer.empty[Double]
          while (it.hasNext)
            list += it.next.asInstanceOf[jLong].toDouble
          array(i) = Some(list.toList)
      case _                      =>
        (row: jList[AnyRef], i: Int) =>
          val it   = row.get(colIndex).asInstanceOf[LazySeq].iterator
          var list = ListBuffer.empty[Double]
          while (it.hasNext)
            list += it.next.asInstanceOf[jDouble].toDouble
          array(i) = Some(list.toList)
    }
  }


  protected def castAggrListDistinct(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = col.colType match {
    case "string" =>
      val array = new Array[Option[List[String]]](rowCount)
      listStrArrays = listStrArrays :+ array
      updateColTypes(colIndex, 3)
      col.attrType match {
        case "Date" => (row: jList[AnyRef], i: Int) =>
          val it   = row.get(colIndex).asInstanceOf[PersistentHashSet].iterator
          val list = ListBuffer.empty[String]
          while (it.hasNext)
            list += date2str(it.next.asInstanceOf[Date])
          array(i) = Some(list.toList)

        case _ => (row: jList[AnyRef], i: Int) =>
          val it   = row.get(colIndex).asInstanceOf[PersistentHashSet].iterator
          val list = ListBuffer.empty[String]
          while (it.hasNext)
            list += it.next.toString
          array(i) = Some(list.toList)
      }

    case "double" =>
      val array = new Array[Option[List[Double]]](rowCount)
      listNumArrays = listNumArrays :+ array
      updateColTypes(colIndex, 4)
      col.attrType match {
        case "Int" | "Long" | "ref" =>
          (row: jList[AnyRef], i: Int) =>
            val it   = row.get(colIndex).asInstanceOf[PersistentHashSet].iterator
            val list = ListBuffer.empty[Double]
            while (it.hasNext)
              list += it.next.asInstanceOf[jLong].toDouble
            array(i) = Some(list.toList)
        case _                      =>
          (row: jList[AnyRef], i: Int) =>
            val it   = row.get(colIndex).asInstanceOf[PersistentHashSet].iterator
            val list = ListBuffer.empty[Double]
            while (it.hasNext)
              list += it.next.asInstanceOf[jDouble].toDouble
            array(i) = Some(list.toList)
      }
  }


  // Card many ================================================================================================

  // Mandatory ------------------------------------

  protected def castMandatoryListString(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[List[String]]](rowCount)
    listStrArrays = listStrArrays :+ array
    updateColTypes(colIndex, 3)
    col.attrType match {
      case "Date" =>
        (row: jList[AnyRef], i: Int) =>
          val it   = row.get(colIndex).asInstanceOf[PersistentHashSet].iterator
          val list = ListBuffer.empty[String]
          while (it.hasNext)
            list += date2str(it.next.asInstanceOf[Date])
          array(i) = Some(list.toList)

      case _ =>
        (row: jList[AnyRef], i: Int) =>
          val it   = row.get(colIndex).asInstanceOf[PersistentHashSet].iterator
          val list = ListBuffer.empty[String]
          while (it.hasNext)
            list += it.next.toString
          array(i) = Some(list.toList)
    }
  }

  protected def castMandatoryListDouble(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[List[Double]]](rowCount)
    listNumArrays = listNumArrays :+ array
    updateColTypes(colIndex, 4)
    col.attrType match {
      case "Int" | "Long" | "ref" =>
        (row: jList[AnyRef], i: Int) =>
          val it   = row.get(colIndex).asInstanceOf[PersistentHashSet].iterator
          val list = ListBuffer.empty[Double]
          while (it.hasNext)
            list += it.next.asInstanceOf[jLong].toDouble
          array(i) = Some(list.toList)
      case _                      =>
        (row: jList[AnyRef], i: Int) =>
          val it   = row.get(colIndex).asInstanceOf[PersistentHashSet].iterator
          val list = ListBuffer.empty[Double]
          while (it.hasNext)
            list += it.next.asInstanceOf[jDouble].toDouble
          array(i) = Some(list.toList)
    }
  }

  // Optional ------------------------------------

  protected def castOptionalListString(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[List[String]]](rowCount)
    listStrArrays = listStrArrays :+ array
    updateColTypes(colIndex, 3)
    col.attrType match {
      case "String" if col.enums.nonEmpty => (row: jList[AnyRef], i: Int) =>
        array(i) = row.get(colIndex) match {
          case null => Option.empty[List[String]]
          case vs   =>
            val it   = vs.asInstanceOf[jMap[String, PersistentVector]].values.iterator.next.iterator
            val list = ListBuffer.empty[String]
            while (it.hasNext)
              list += it.next.asInstanceOf[jMap[String, Keyword]].values.iterator.next.getName
            Some(list.toList)
        }

      case "Date" => (row: jList[AnyRef], i: Int) =>
        array(i) = row.get(colIndex) match {
          case null => Option.empty[List[String]]
          case vs0  =>
            // todo: cache resulting iterator retriever from this match so that we don't match on all rows
            val it   = vs0 match {
              case vs1: PersistentHashSet => vs1.iterator
              case vs1                    => vs1
                .asInstanceOf[jMap[String, PersistentVector]].values.iterator.next.iterator
            }
            val list = ListBuffer.empty[String]
            while (it.hasNext)
              list += date2str(it.next.asInstanceOf[Date])
            Some(list.toList)
        }

      case _ => (row: jList[AnyRef], i: Int) =>
        array(i) = row.get(colIndex) match {
          case null => Option.empty[List[String]]
          case vs0  =>
            val it   = vs0 match {
              case vs1: PersistentHashSet => vs1.iterator
              case vs1                    => vs1
                .asInstanceOf[jMap[String, PersistentVector]].values.iterator.next.iterator
            }
            val list = ListBuffer.empty[String]
            while (it.hasNext)
              list += it.next.toString
            Some(list.toList)
        }
    }
  }

  protected def castOptionalListDouble(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[List[Double]]](rowCount)
    listNumArrays = listNumArrays :+ array
    updateColTypes(colIndex, 4)

    col.attrType match {
      case "ref" => (row: jList[AnyRef], i: Int) =>
        array(i) = row.get(colIndex) match {
          case null => Option.empty[List[Double]]
          case vs   =>
            val it   = vs.asInstanceOf[jMap[String, PersistentVector]].values.iterator.next.iterator
            val list = ListBuffer.empty[Double]
            while (it.hasNext)
              list += it.next.asInstanceOf[jMap[_, _]]
                .get(Keyword.intern("db", "id")).asInstanceOf[jLong].toDouble
            Some(list.toList)
        }

      case "Int" | "Long" => (row: jList[AnyRef], i: Int) =>
        array(i) = row.get(colIndex) match {
          case null => Option.empty[List[Double]]
          case vs0  =>
            val it   = vs0 match {
              case vs1: PersistentHashSet => vs1.iterator
              case vs1                    => vs1
                .asInstanceOf[jMap[String, PersistentVector]].values.iterator.next.iterator
            }
            val list = ListBuffer.empty[Double]
            while (it.hasNext)
              list += it.next.asInstanceOf[jLong].toDouble
            Some(list.toList)
        }

      case _ => (row: jList[AnyRef], i: Int) =>
        array(i) = row.get(colIndex) match {
          case null => Option.empty[List[Double]]
          case vs0  =>
            val it   = vs0 match {
              case vs1: PersistentHashSet => vs1.iterator
              case vs1                    => vs1
                .asInstanceOf[jMap[String, PersistentVector]].values.iterator.next.iterator
            }
            val list = ListBuffer.empty[Double]
            while (it.hasNext)
              list += it.next.asInstanceOf[jDouble].toDouble
            Some(list.toList)
        }
    }
  }


  // Aggregates ------------------------------------

  protected def castAggrManySingleString(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[List[String]]](rowCount)
    listStrArrays = listStrArrays :+ array
    updateColTypes(colIndex, 3)
    col.attrType match {
      case "Date" => (row: jList[AnyRef], i: Int) => array(i) = Some(List(date2str(row.get(colIndex).asInstanceOf[Date])))
      case _      => (row: jList[AnyRef], i: Int) => array(i) = Some(List(row.get(colIndex).toString))
    }
  }

  protected def castAggrManySingleDouble(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[List[Double]]](rowCount)
    listNumArrays = listNumArrays :+ array
    updateColTypes(colIndex, 4)
    col.attrType match {
      case "Int" | "Long" | "ref" =>
        (row: jList[AnyRef], i: Int) => array(i) = Some(List(row.get(colIndex).asInstanceOf[jLong].toDouble))
      case _                      =>
        (row: jList[AnyRef], i: Int) => array(i) = Some(List(row.get(colIndex).asInstanceOf[jDouble].toDouble))
    }
  }


  // Map ================================================================================================

  // Mandatory ------------------------------------

  protected def castMandatoryMapString(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[Map[String, String]]](rowCount)
    mapStrArrays = mapStrArrays :+ array
    updateColTypes(colIndex, 5)
    var vs = new Array[String](2)
    col.attrType match {
      case _ =>
        (row: jList[AnyRef], i: Int) =>
          val it  = row.get(colIndex).asInstanceOf[PersistentHashSet].iterator
          var map = Map.empty[String, String]
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map = map + (vs(0) -> vs(1))
          }
          array(i) = Some(map)
    }
  }

  protected def castMandatoryMapDouble(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[Map[String, Double]]](rowCount)
    mapNumArrays = mapNumArrays :+ array
    updateColTypes(colIndex, 6)
    var vs = new Array[String](2)
    (row: jList[AnyRef], i: Int) =>
      val it  = row.get(colIndex).asInstanceOf[PersistentHashSet].iterator
      var map = Map.empty[String, Double]
      while (it.hasNext) {
        vs = it.next.toString.split("@", 2)
        map = map + (vs(0) -> vs(1).toDouble)
      }
      array(i) = Some(map)
  }


  // Optional ------------------------------------

  protected def castOptionalMapString(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[Map[String, String]]](rowCount)
    mapStrArrays = mapStrArrays :+ array
    updateColTypes(colIndex, 5)
    var vs = new Array[String](2)
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, String]]
        case vs0  =>
          val it  = vs0 match {
            case vs1: PersistentHashSet => vs1.iterator
            case vs1                    => vs1
              .asInstanceOf[jMap[String, PersistentVector]].values.iterator.next.iterator
          }
          var map = Map.empty[String, String]
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map = map + (vs(0) -> vs(1))
          }
          Some(map)
      }
  }

  protected def castOptionalMapDouble(col: Col, colIndex: Int): (jList[AnyRef], Int) => Unit = {
    val array = new Array[Option[Map[String, Double]]](rowCount)
    mapNumArrays = mapNumArrays :+ array
    updateColTypes(colIndex, 6)
    var vs = new Array[String](2)
    (row: jList[AnyRef], i: Int) =>
      array(i) = row.get(colIndex) match {
        case null => Option.empty[Map[String, Double]]
        case vs0  =>
          val it  = vs0 match {
            case vs1: PersistentHashSet => vs1.iterator
            case vs1                    => vs1
              .asInstanceOf[jMap[String, PersistentVector]].values.iterator.next.iterator
          }
          var map = Map.empty[String, Double]
          while (it.hasNext) {
            vs = it.next.toString.split("@", 2)
            map = map + (vs(0) -> vs(1).toDouble)
          }
          Some(map)
      }
  }
}
