package molecule.core.macros

import molecule.core.ast.elements._
import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox


private[molecule] trait MakeSortingJvm extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  private lazy val xy = InspectMacro("MakeSorting", 1, 10, mkError = true)
  private lazy val xx = InspectMacro("MakeSorting", 1, 10)


  def compareAttr(
    i: Int,
    attr: String,
    tpeStr: String,
    isEnum: Boolean,
    aggrFn: String,
    aggrLimit: Option[Int],
    sort: String,
  ): (Int, Tree) = {
    if (attr.last == '$') {

      // Optional --------------------------------------------------------------------------------

      val (sortPos, pair) = sort match {
        case r"a([1-5])$pos" => (pos.toInt, q"(x.get($i), y.get($i))")
        case r"d([1-5])$pos" => (pos.toInt, q"(y.get($i), x.get($i))")
      }
      val (x, y)          = tpeStr match {
        case "String" =>
          if (isEnum)
            (
              q"""getKwName(m1.values.iterator.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)""",
              q"""getKwName(m2.values.iterator.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)"""
            )
          else
            (q"m1.values.iterator.next.asInstanceOf[String]", q"m2.values.iterator.next.asInstanceOf[String]")

        case "ref" => (
          q"""{
              var id   = 0L
              var done = false
              // Hack to avoid looking up map by clojure Keyword - there must be a better way...
              m1.values.iterator.next.asInstanceOf[jMap[_, _]].forEach {
                case _ if done                        =>
                case (k, v) if k.toString == ":db/id" => done = true; id = v.asInstanceOf[jLong].toLong
                case _                                =>
              }
              id
            }""",
          q"""{
              var id   = 0L
              var done = false
              // Hack to avoid looking up map by clojure Keyword - there must be a better way...
              m2.values.iterator.next.asInstanceOf[jMap[_, _]].forEach {
                case _ if done                        =>
                case (k, v) if k.toString == ":db/id" => done = true; id = v.asInstanceOf[jLong].toLong
                case _                                =>
              }
              id
            }"""
        )

        case "Int" | "Long" => (q"m1.values.iterator.next.asInstanceOf[jLong]", q"m2.values.iterator.next.asInstanceOf[jLong]")
        case "Double"       => (q"m1.values.iterator.next.asInstanceOf[jDouble]", q"m2.values.iterator.next.asInstanceOf[jDouble]")
        case "Boolean"      => (q"m1.values.iterator.next.asInstanceOf[jBoolean]", q"m2.values.iterator.next.asInstanceOf[jBoolean]")
        case "Date"         => (q"m1.values.iterator.next.asInstanceOf[Date]", q"m2.values.iterator.next.asInstanceOf[Date]")
        case "UUID"         => (q"m1.values.iterator.next.asInstanceOf[UUID]", q"m2.values.iterator.next.asInstanceOf[UUID]")
        case "URI"          => (q"m1.values.iterator.next.asInstanceOf[URI]", q"m2.values.iterator.next.asInstanceOf[URI]")
        case "BigInt"       => (q"m1.values.iterator.next.asInstanceOf[jBigInt]", q"m2.values.iterator.next.asInstanceOf[jBigInt]")
        case "BigDecimal"   => (q"m1.values.iterator.next.asInstanceOf[jBigDec]", q"m2.values.iterator.next.asInstanceOf[jBigDec]")

        case "schema" => attr match {
          case "doc$" => (q"m1.values.iterator.next.asInstanceOf[String]", q"m2.values.iterator.next.asInstanceOf[String]")

          case "ident$" | "valueType$" | "cardinality$" | "unique$" => (
            q"""getKwName(m1.values.iterator.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)""",
            q"""getKwName(m2.values.iterator.next.asInstanceOf[jMap[_, _]].values.iterator.next.toString)"""
          )

          case _ => (q"m1.values.iterator.next.asInstanceOf[jBoolean]", q"m2.values.iterator.next.asInstanceOf[jBoolean]")
        }

        case other => abort(s"Unexpected type '$other' for optional sort attribute `$attr`")
      }
      val comparator      =
        q"""$pair match {
             case (null, null)                     => 0
             case (null, _)                        => -1
             case (_, null)                        => 1
             case (m1: jMap[_, _], m2: jMap[_, _]) => $x.compareTo($y)
           }"""
      (sortPos, comparator)

    } else {

      // Mandatory --------------------------------------------------------------------------------

      def compareType: (Tree, Tree) = {
        tpeStr match {
          case "String" | "enum"      => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
          case "Int" | "Long" | "ref" => (q"x.get($i).asInstanceOf[jLong]", q"y.get($i).asInstanceOf[jLong]")
          case "Double"               => (q"x.get($i).asInstanceOf[jDouble]", q"y.get($i).asInstanceOf[jDouble]")
          case "Boolean"              => (q"x.get($i).asInstanceOf[jBoolean]", q"y.get($i).asInstanceOf[jBoolean]")
          case "Date"                 => (q"x.get($i).asInstanceOf[Date]", q"y.get($i).asInstanceOf[Date]")
          case "UUID"                 => (q"x.get($i).asInstanceOf[UUID]", q"y.get($i).asInstanceOf[UUID]")
          case "URI"                  => (q"x.get($i).asInstanceOf[URI]", q"y.get($i).asInstanceOf[URI]")
          case "BigInt"               => (q"x.get($i).asInstanceOf[jBigInt]", q"y.get($i).asInstanceOf[jBigInt]")
          case "BigDecimal"           => (q"x.get($i).asInstanceOf[jBigDec]", q"y.get($i).asInstanceOf[jBigDec]")

          case "datom" | "log" | "eavt" | "aevt" | "avet" | "vaet" => attr match {
            case "e"         => (q"x.get($i).asInstanceOf[jLong]", q"y.get($i).asInstanceOf[jLong]")
            case "a"         => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "v"         => (q"x.get($i).toString", q"y.get($i).toString")
            case "t"         => (q"x.get($i).asInstanceOf[jLong]", q"y.get($i).asInstanceOf[jLong]")
            case "tx"        => (q"x.get($i).asInstanceOf[jLong]", q"y.get($i).asInstanceOf[jLong]")
            case "txInstant" => (q"x.get($i).asInstanceOf[Date]", q"y.get($i).asInstanceOf[Date]")
            case "op"        => (q"x.get($i).asInstanceOf[jBoolean]", q"y.get($i).asInstanceOf[jBoolean]")
          }

          case "schema" => attr match {
            case "t"           => (q"x.get($i).asInstanceOf[jLong]", q"y.get($i).asInstanceOf[jLong]")
            case "tx"          => (q"x.get($i).asInstanceOf[jLong]", q"y.get($i).asInstanceOf[jLong]")
            case "txInstant"   => (q"x.get($i).asInstanceOf[Date]", q"y.get($i).asInstanceOf[Date]")
            case "a"           => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "attrId"      => (q"x.get($i).asInstanceOf[jLong]", q"y.get($i).asInstanceOf[jLong]")
            case "part"        => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "nsFull"      => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "ns"          => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "attr"        => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "enumm"       => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "ident"       => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "valueType"   => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "cardinality" => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "doc"         => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "unique"      => (q"x.get($i).asInstanceOf[String]", q"y.get($i).asInstanceOf[String]")
            case "isComponent" => (q"x.get($i).asInstanceOf[jBoolean]", q"y.get($i).asInstanceOf[jBoolean]")
            case "noHistory"   => (q"x.get($i).asInstanceOf[jBoolean]", q"y.get($i).asInstanceOf[jBoolean]")
            case "index"       => (q"x.get($i).asInstanceOf[jBoolean]", q"y.get($i).asInstanceOf[jBoolean]")
            case "fulltext"    => (q"x.get($i).asInstanceOf[jBoolean]", q"y.get($i).asInstanceOf[jBoolean]")
          }
          case other    => abort(s"Unexpected type '$other' for sort attribute `$attr`")
        }
      }

      val (x, y) = if (aggrFn.isEmpty) {
        compareType
      } else {
        (aggrFn, aggrLimit) match {
          case ("min" | "max" | "distinct" | "rand" | "sample", Some(limit)) => abort(
            s"Unexpectedly trying to sort aggregate with applied limit. Found: $attr($aggrFn($limit)).")

          case ("count" | "count-distinct", _) =>
            (q"x.get($i).asInstanceOf[jInteger]", q"y.get($i).asInstanceOf[jInteger]")

          case ("avg" | "stddev" | "variance", _) =>
            (q"x.get($i).asInstanceOf[jDouble]", q"y.get($i).asInstanceOf[jDouble]")

          case _ => compareType // min, max, rand, sample, sum, median
        }
      }

      sort match {
        case r"a([1-5])$pos" => (pos.toInt, q"$x.compareTo($y)")
        case r"d([1-5])$pos" => (pos.toInt, q"$y.compareTo($x)")
      }
    }
  }


  // For flat and composite molecules
  def compareFlat(model: Model, doSort: Boolean): Tree = {
    if (!doSort) {
      return q""
    }

    // Accumulate sort position / comparator
    var com = Seq.empty[(Int, Tree)]

    var i = 0
    def addComparator(sort: String, value: Value, attr: String, tpeStr: String, isEnum: Boolean): Unit = {
      if (sort.nonEmpty) {
        val (sortPos, comparator) = value match {
          case Fn(aggrFn, aggrLimit) => compareAttr(i, attr, tpeStr, isEnum, aggrFn, aggrLimit, sort)
          case _                     => compareAttr(i, attr, tpeStr, isEnum, "", None, sort)
        }
        com = com :+ sortPos -> comparator

        i += 1
      } else if (attr.last != '_') {
        i += 1
      }
    }

    def addOrderings(elements: Seq[Element]): Unit = {
      elements.collect {
        case Atom(_, attr, tpe, _, value, enumPrefix, _, _, sort) =>
          addComparator(sort, value, attr, tpe, enumPrefix.nonEmpty)

        case Generic(_, attr, tpe, value, sort) =>
          addComparator(sort, value, attr, tpe, isEnum = false)

        case Nested(_, _) =>
          abort("Unexpectedly found nested elements in model for `compareFlat`.")

        case Composite(elements) =>
          addOrderings(elements)

        case TxMetaData(elements) =>
          addOrderings(elements)
      }
    }

    // Order comparators by sort position
    addOrderings(model.elements)
    val comparatorTrees = com.sortBy(_._1).map(_._2)
    val comparators     = comparatorTrees.size match {
      case 1 => q"${comparatorTrees.head}"
      case _ =>
        val moreComparisons = comparatorTrees.tail.map(comparison => q"if (result == 0) result = $comparison")
        q"""
          var result = ${comparatorTrees.head}
          ..$moreComparisons
          result
        """
    }

    q"""
      final override def sortRows: Boolean = true
      final override def compare(x: jList[AnyRef], y: jList[AnyRef]): Int = {
        ..$comparators
      }
    """
  }


  def compareNested(model: Model, levels: Int, doSort: Boolean): Tree = {
    if (!doSort) {
      // Fall back on `compare` in NestedBase
      return q""
    }

    var level         = 0
    var hasTxMetaData = false

    // Collect sort positions and comparators on current level
    var curLevel = Seq.empty[(Int, Tree)]

    // Accumulate comparator trees for all levels
    var accLevels = Seq.empty[Seq[(Int, Tree)]]

    var i = levels // skip nested indexes in raw output (1 for each level)
    def addComparator(sort: String, value: Value, attr: String, tpeStr: String, isEnum: Boolean): Unit = {
      if (sort.nonEmpty) {
        val comparator = value match {
          case Fn(aggrFn, aggrLimit) => compareAttr(i, attr, tpeStr, isEnum, aggrFn, aggrLimit, sort)
          case _                     => compareAttr(i, attr, tpeStr, isEnum, "", None, sort)
        }
        i += 1
        curLevel = curLevel :+ comparator
      } else if (attr.last != '_') {
        i += 1
      }
    }

    def sortNestedIndex: (Int, Tree) = {
      // Level sorting always last
      (6, q"x.get($level).asInstanceOf[jLong].compareTo(y.get($level).asInstanceOf[jLong])")
    }

    def resolveComparisons(elements: Seq[Element]): Unit = elements.collect {
      case Atom(_, attr, tpe, _, value, enumPrefix, _, _, sort) =>
        addComparator(sort, value, attr, tpe, enumPrefix.nonEmpty)

      case Generic(_, attr, tpe, value, sort) =>
        addComparator(sort, value, attr, tpe, isEnum = false)

      case Nested(_, elements) =>
        accLevels = accLevels :+ (curLevel :+ sortNestedIndex)
        curLevel = Seq.empty[(Int, Tree)]
        level += 1
        resolveComparisons(elements)

      case TxMetaData(txElements) =>
        accLevels = accLevels :+ (curLevel :+ sortNestedIndex)
        hasTxMetaData = true
        // Continue on top level
        curLevel = accLevels.head
        resolveComparisons(txElements)

      case Composite(elements) =>
        // Composites only allowed in tx meta data, so we can presume that we are
        // on the top level and therefore continue accumulating curLevel.
        resolveComparisons(elements)
    }

    lazy val comparators: Seq[Tree] = {
      resolveComparisons(model.elements)
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

    def getOrdering(comparators: Seq[Tree]): Tree = comparators.size match {
      case 1 => q"${comparators.head}"
      case _ =>
        val moreComparisons = comparators.tail.map(comparison => q"if (result == 0) result = $comparison")
        q"""
          var result = ${comparators.head}
          ..$moreComparisons
          result
        """
    }
    lazy val ordering = getOrdering(comparators)

    // Override `compare` in NestedBase
    q"""
      final override def sortRows: Boolean = true
      final override def compare(x: jList[AnyRef], y: jList[AnyRef]): Int = {
        ..$ordering
      }
    """
  }


  def compareOptNested(model: Model, doSort: Boolean): (Tree, Seq[Tree]) = {
    if (!doSort) {
      return (q"", Nil)
    }

    // Accumulate sort position / comparator on top level
    var top             = Seq.empty[(Int, Tree)]
    var topIndex        = 0
    var firstTxMetaData = false

    // Accumulate current sort positions on this level
    var curLevel = Seq.empty[(Int, Tree, Tree, Tree, Int)]

    // Accumulate sort positions for each level
    var nested = Seq.empty[Seq[(Int, Tree, Tree, Tree, Int)]]

    var i = 0
    def addComparator(
      sort: String,
      value: Value,
      attr: String,
      tpeStr: String,
      isEnum: Boolean,
      level: Int,
      attrOnLevel: Int
    ): Unit = {
      val opt = attr.last == '$'
      if (sort.nonEmpty) {
        if (level == 0) {
          topIndex += 1
          val (sortPos, comparator) = value match {
            case Fn(aggrFn, aggrLimit) => compareAttr(i, attr, tpeStr, isEnum, aggrFn, aggrLimit, sort)
            case _                     => compareAttr(i, attr, tpeStr, isEnum, "", None, sort)
          }
          top = top :+ (sortPos, comparator)

        } else {
          val asc     = sort.head == 'a'
          val tpe     = tpeStr match {
            case "enum" => TypeName("String")
            case "ref"  => TypeName("Long")
            case t      => TypeName(t)
          }
          val sortPos = sort match {
            case r"[ad]([1-5])$pos" => pos.toInt
          }
          if (opt) {
            val ordering = if (asc) q"Ordering.Option[$tpe]" else q"Ordering.Option[$tpe].reverse"
            curLevel = curLevel :+ (
              sortPos,
              q"t.asInstanceOf[Option[$tpe]]",
              q"p.productElement($i).asInstanceOf[Option[$tpe]]",
              ordering,
              attrOnLevel
            )
          } else {
            val ordering = if (asc) q"Ordering[$tpe]" else q"Ordering[$tpe].reverse"
            curLevel = curLevel :+ (
              sortPos,
              q"t.asInstanceOf[$tpe]",
              q"p.productElement($i).asInstanceOf[$tpe]",
              ordering,
              attrOnLevel
            )
          }
        }
        i += 1
      } else if (attr.last != '_') {
        if (level == 0) {
          topIndex += 1
        }
        i += 1
      }
    }

    def addOrderings(elements: Seq[Element], level: Int): Unit = {
      val attrOnLevel = elements.size
      elements.collect {
        case Atom(_, attr, tpe, _, value, enumPrefix, _, _, sort) =>
          addComparator(sort, value, attr, tpe, enumPrefix.nonEmpty, level, attrOnLevel)

        case Generic(_, attr, tpe, value, sort) =>
          addComparator(sort, value, attr, tpe, isEnum = false, level, attrOnLevel)

        case Nested(_, elements) =>
          nested = nested :+ curLevel
          // New sorting order on each nested level
          curLevel = Seq.empty[(Int, Tree, Tree, Tree, Int)]
          i = 0
          addOrderings(elements, level + 1)

        case TxMetaData(txElements) =>
          // Continue on top level
          i = topIndex + 1 // skip nested data
          firstTxMetaData = true
          addOrderings(txElements, 0)

        case Composite(elements) =>
          if (firstTxMetaData) {
            i = topIndex + 1 // skip nested data
            firstTxMetaData = false
          }
          // Composites only allowed in tx meta data, so we can presume that we are on the top level
          addOrderings(elements, 0)
      }
    }

    // Resolve comparators
    addOrderings(model.elements, 0)

    val topLevelComparisons = if (top.nonEmpty) {
      // Order comparators by sort position
      val comparatorTrees = top.sortBy(_._1).map(_._2)
      lazy val comparators = comparatorTrees.size match {
        case 1 => q"${comparatorTrees.head}"
        case _ =>
          val moreComparators = comparatorTrees.tail.map(comparator => q"if (result == 0) result = $comparator")
          q"""
          var result = ${comparatorTrees.head}
          ..$moreComparators
          result
        """
      }
      q"""
        final override def sortRows: Boolean = true
        final override def compare(x: jList[AnyRef], y: jList[AnyRef]): Int = {
          ..$comparators
        }
      """
    } else {
      q"""
        final override def sortRows: Boolean = true
        final override def compare(x: jList[AnyRef], y: jList[AnyRef]): Int = 0
      """
    }

    val orderings = (nested :+ curLevel).map(_.sortBy(_._1)).map { levelOrderings =>
      levelOrderings.size match {
        case 0                                => q""
        case 1 if levelOrderings.head._5 == 1 =>
          q"buf = buf.sortBy(t => ${levelOrderings.head._2})(${levelOrderings.head._4})"
        case 1                                =>
          q"""
            buf = buf.sortBy { t =>
              val p = t.asInstanceOf[Product]
              ${levelOrderings.head._3}
            }(${levelOrderings.head._4})
          """
        case n                                =>
          val (productCasts, orders) = levelOrderings.map(t => (t._3, t._4)).unzip
          val tupleN                 = n match {
            case 2 => TermName("Tuple2")
            case 3 => TermName("Tuple3")
            case 4 => TermName("Tuple4")
            case 5 => TermName("Tuple5")
          }
          q"""
            buf = buf.sortBy { t =>
              val p = t.asInstanceOf[Product]
              (..$productCasts)
            }(Ordering.$tupleN(..$orders))
          """
      }
    }
    (topLevelComparisons, orderings)
  }
}
