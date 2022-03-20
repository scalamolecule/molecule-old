package molecule.core.macros

import molecule.core.ast.elements._
import molecule.core.marshalling.ast.SortCoordinate
import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox


private[molecule] trait MakeSortingJs extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  private lazy val xy = InspectMacro("MakeSorting", 1, 10, mkError = true)
  private lazy val xx = InspectMacro("MakeSorting", 1, 10)

  def getSortCoordinate(
    i: Int,
    attr: String,
    tpeStr: String,
    isEnum: Boolean,
    aggrFn: String,
    aggrLimit: Option[Int],
    sort: String,
  ): (Int, Tree) = {

    val (sortPos, asc) = sort match {
      case r"a([1-5])$pos" => (pos.toInt, true)
      case r"d([1-5])$pos" => (pos.toInt, false)
    }
    val opt            = attr.last == '$'

    def compareType: Tree = {
      tpeStr match {
        case "String"       => q"""SortCoordinate($i, $asc, $attr, $opt, "String", $isEnum)"""
        case "enum"         => q"""SortCoordinate($i, $asc, $attr, $opt, "String", true)"""
        case "ref"          => q"""SortCoordinate($i, $asc, $attr, $opt, "ref")"""
        case "Int" | "Long" => q"""SortCoordinate($i, $asc, $attr, $opt, "Long")"""
        case "Double"       => q"""SortCoordinate($i, $asc, $attr, $opt, "Double")"""
        case "Boolean"      => q"""SortCoordinate($i, $asc, $attr, $opt, "Boolean")"""
        case "Date"         => q"""SortCoordinate($i, $asc, $attr, $opt, "Date")"""
        case "UUID"         => q"""SortCoordinate($i, $asc, $attr, $opt, "UUID")"""
        case "URI"          => q"""SortCoordinate($i, $asc, $attr, $opt, "URI")"""
        case "BigInt"       => q"""SortCoordinate($i, $asc, $attr, $opt, "BigInt")"""
        case "BigDecimal"   => q"""SortCoordinate($i, $asc, $attr, $opt, "BigDecimal")"""

        case "datom" | "log" | "eavt" | "aevt" | "avet" | "vaet" => clean(attr) match {
          case "e"         => q"""SortCoordinate($i, $asc, $attr, $opt, "Long")"""
          case "a"         => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "v"         => q"""SortCoordinate($i, $asc, $attr, $opt, "Any")"""
          case "t"         => q"""SortCoordinate($i, $asc, $attr, $opt, "Long")"""
          case "tx"        => q"""SortCoordinate($i, $asc, $attr, $opt, "Long")"""
          case "txInstant" => q"""SortCoordinate($i, $asc, $attr, $opt, "Date")"""
          case "op"        => q"""SortCoordinate($i, $asc, $attr, $opt, "Boolean")"""
        }

        case "schema" => clean(attr) match {
          case "t"           => q"""SortCoordinate($i, $asc, $attr, $opt, "Long")"""
          case "tx"          => q"""SortCoordinate($i, $asc, $attr, $opt, "Long")"""
          case "txInstant"   => q"""SortCoordinate($i, $asc, $attr, $opt, "Date")"""
          case "a"           => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "attrId"      => q"""SortCoordinate($i, $asc, $attr, $opt, "Long")"""
          case "part"        => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "nsFull"      => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "ns"          => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "attr"        => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "enumm"       => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "ident"       => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "valueType"   => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "cardinality" => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "doc"         => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "unique"      => q"""SortCoordinate($i, $asc, $attr, $opt, "String")"""
          case "isComponent" => q"""SortCoordinate($i, $asc, $attr, $opt, "Boolean")"""
          case "noHistory"   => q"""SortCoordinate($i, $asc, $attr, $opt, "Boolean")"""
          case "index"       => q"""SortCoordinate($i, $asc, $attr, $opt, "Boolean")"""
          case "fulltext"    => q"""SortCoordinate($i, $asc, $attr, $opt, "Boolean")"""
        }
        case other    => abort(s"Unexpected type '$other' for sort attribute `$attr`")
      }
    }

    val sortCoordinate = if (aggrFn.isEmpty) {
      compareType
    } else {
      (aggrFn, aggrLimit) match {
        case ("min" | "max" | "distinct" | "rand" | "sample", Some(limit)) => abort(
          s"Unexpectedly trying to sort aggregate with applied limit. Found: $attr($aggrFn($limit)).")

        case ("count" | "count-distinct", _)    => q"""SortCoordinate($i, $asc, $attr, $opt, "Integer")"""
        case ("avg" | "stddev" | "variance", _) => q"""SortCoordinate($i, $asc, $attr, $opt, "Double")"""
        case _                                  => compareType // min, max, rand, sample, sum, median
      }
    }
    (sortPos, sortCoordinate)
  }


  def sortCoordinatesFlat(model: Model, doSort: Boolean): Tree = {
    if (!doSort) {
      return q""
    }

    // Accumulate sort position / sort coordinate
    var sortCoords = Seq.empty[(Int, Tree)]

    var i = 0
    def addSortCoordinate(sort: String, value: Value, attr: String, tpeStr: String, isEnum: Boolean): Unit = {
      if (sort.nonEmpty) {
        val (sortPos, sortCoordinate) = value match {
          case Fn(aggrFn, aggrLimit) => getSortCoordinate(i, attr, tpeStr, isEnum, aggrFn, aggrLimit, sort)
          case _                     => getSortCoordinate(i, attr, tpeStr, isEnum, "", None, sort)
        }
        sortCoords = sortCoords :+ sortPos -> sortCoordinate

        i += 1
      } else if (attr.last != '_') {
        i += 1
      }
    }

    def addSortCoordinates(elements: Seq[Element]): Unit = {
      elements.collect {
        case Atom(_, attr, tpe, _, value, enumPrefix, _, _, sort) =>
          addSortCoordinate(sort, value, attr, tpe, enumPrefix.nonEmpty)

        case Generic(_, attr, tpe, value, sort) =>
          addSortCoordinate(sort, value, attr, tpe, isEnum = false)

        case Nested(_, _) =>
          abort("Unexpectedly found nested elements in model for `compareFlat`.")

        case Composite(elements) =>
          addSortCoordinates(elements)

        case TxMetaData(elements) =>
          addSortCoordinates(elements)
      }
    }

    // Recursively add sort coordinates
    addSortCoordinates(model.elements)

    // Order sort coordinates by sort position
    val sortCoordinates = sortCoords.sortBy(_._1).map(_._2)
    q"""
      final override protected def sortCoordinates: List[List[SortCoordinate]] = List(List(..$sortCoordinates))
    """
  }


  def sortCoordinatesNested(model: Model, levels: Int): Tree = {
    var level         = 0
    var hasTxMetaData = false

    // Collect sort indexes and comparators on current level
    var curLevel = Seq.empty[(Int, Tree)]

    // Accumulate comparator trees for all levels
    var accLevels = Seq.empty[Seq[(Int, Tree)]]

    var i = levels // skip nested indexes in raw output (1 for each level)
    def addSortCoordinate(sort: String, value: Value, attr: String, tpeStr: String, isEnum: Boolean): Unit = {
      if (sort.nonEmpty) {
        val comparator = value match {
          case Fn(aggrFn, aggrLimit) => getSortCoordinate(i, attr, tpeStr, isEnum, aggrFn, aggrLimit, sort)
          case _                     => getSortCoordinate(i, attr, tpeStr, isEnum, "", None, sort)
        }
        i += 1
        curLevel = curLevel :+ comparator
      } else if (attr.last != '_') {
        i += 1
      }
    }

    def sortNestedIndex: (Int, Tree) = {
      // Level sorting always last
      (6, q"""SortCoordinate($level, true, "NESTED INDEX", false, "Long")""")
    }

    def addSortCoordinates(elements: Seq[Element]): Unit = elements.collect {
      case Atom(_, attr, tpe, _, value, enumPrefix, _, _, sort) =>
        addSortCoordinate(sort, value, attr, tpe, enumPrefix.nonEmpty)

      case Generic(_, attr, tpe, value, sort) =>
        addSortCoordinate(sort, value, attr, tpe, isEnum = false)

      case Nested(_, elements) =>
        accLevels = accLevels :+ (curLevel :+ sortNestedIndex)
        curLevel = Seq.empty[(Int, Tree)]
        level += 1
        addSortCoordinates(elements)

      case TxMetaData(txElements) =>
        accLevels = accLevels :+ (curLevel :+ sortNestedIndex)
        hasTxMetaData = true
        // Continue on top level
        curLevel = accLevels.head
        addSortCoordinates(txElements)

      case Composite(elements) =>
        // Composites only allowed in tx meta data, so we can presume that we are
        // on the top level and therefore continue accumulating curLevel.
        addSortCoordinates(elements)
    }

    val sortCoordinates: Seq[Tree] = {
      addSortCoordinates(model.elements)
      accLevels = if (hasTxMetaData) {
        // Add tx meta sortings to top level
        curLevel +: accLevels.tail
      } else {
        // Add last level
        val lastNestedLevel = curLevel :+ sortNestedIndex
        accLevels :+ lastNestedLevel
      }
      // Sort by sort position on each level and return flattened comparator trees
      accLevels.flatMap(_.sortBy(_._1).map(_._2))
    }

    // Override `compare` in NestedBase
    q"""
      final override protected def sortCoordinates: List[List[SortCoordinate]] = List(List(..$sortCoordinates))
    """
  }
}
