package molecule.core.macros

import molecule.core.ast.elements._
import molecule.core.transform.Dsl2Model
import scala.reflect.macros.blackbox


private[molecule] trait MakeBase extends Dsl2Model {
  val c: blackbox.Context

  import c.universe._

  def getImports(genericImports: List[Tree]) =
    q"""
      import java.net.URI
      import java.util.{Collections, Date, UUID, List => jList, Map => jMap, Iterator => jIterator, Set => jSet}
      import molecule.core.ast.elements._
      import molecule.core.composition._
      import molecule.core.dsl.base.Init
      import molecule.core.exceptions.MoleculeException
      ..$genericImports
      import molecule.core.marshalling.MoleculeRpc
      import molecule.core.marshalling.nodes
      import molecule.core.marshalling.nodes._
      import molecule.core.ops.ModelOps._
      import molecule.datomic.base.ast.query._
      import molecule.datomic.base.transform.{Model2Query, QueryOptimizer, Query2String}
      import molecule.datomic.base.facade.Conn
      import scala.collection.mutable.ListBuffer
      import scala.concurrent.Future

      import molecule.core.marshalling.unpackAttr.String2cast._
      import molecule.core.marshalling.unpackAttr.String2json._
     """

  def mapIdents(idents: Seq[Any]): Seq[(String, Tree)] = idents.flatMap {
    case (key: String, v: String) if key.startsWith("__ident__") && v.startsWith("__ident__") =>
      Seq(
        ArrowAssoc(key) -> q"${TermName(key.substring(9))}",
        ArrowAssoc(v) -> q"${TermName(v.substring(9))}"
      )

    case (key: String, _: Any) if key.startsWith("__ident__") =>
      Seq(ArrowAssoc(key) -> q"${TermName(key.substring(9))}")

    case (_: Any, v: String) if v.startsWith("__ident__") =>
      Seq(ArrowAssoc(v) -> q"${TermName(v.substring(9))}")

    case ident: String if ident.startsWith("__ident__") =>
      Seq(ArrowAssoc(ident) -> q"${TermName(ident.substring(9))}")

    case set: Set[_] if set.nonEmpty => set.flatMap {
      case ident if ident.toString.startsWith("__ident__") =>
        Seq(ArrowAssoc(ident.toString) -> q"${TermName(ident.toString.substring(9))}")
      case _                                               => Nil
    }
    case _                           => Nil
  }

  def mapIdentifiers(elements: Seq[Element], identifiers0: Seq[(String, Tree)] = Nil): Seq[(String, Tree)] = {
    val newIdentifiers = (elements collect {
      case Atom(_, _, _, _, Eq(idents), _, _, keyIdents, _)              => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, Neq(idents), _, _, keyIdents, _)             => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, And(idents), _, _, keyIdents, _)             => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, Lt(ident), _, _, keyIdents, _)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Gt(ident), _, _, keyIdents, _)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Le(ident), _, _, keyIdents, _)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Ge(ident), _, _, keyIdents, _)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Fulltext(idents), _, _, keyIdents, _)        => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, AssertValue(idents), _, _, keyIdents, _)     => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, RetractValue(idents), _, _, keyIdents, _)    => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, ReplaceValue(idents), _, _, keyIdents, _)    => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, MapEq(idents), _, _, keyIdents, _)           => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, AssertMapPairs(idents), _, _, keyIdents, _)  => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, ReplaceMapPairs(idents), _, _, keyIdents, _) => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, RetractMapKeys(idents), _, _, keyIdents, _)  => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, MapKeys(idents), _, _, keyIdents, _)         => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, _, _, _, keyIdents, _)                       => mapIdents(keyIdents)
      case Generic(_, _, _, Eq(idents), _)                               => mapIdents(idents)
      case Generic(_, _, _, Neq(idents), _)                              => mapIdents(idents)
      case Generic(_, _, _, Lt(ident), _)                                => mapIdents(Seq(ident))
      case Generic(_, _, _, Gt(ident), _)                                => mapIdents(Seq(ident))
      case Generic(_, _, _, Le(ident), _)                                => mapIdents(Seq(ident))
      case Generic(_, _, _, Ge(ident), _)                                => mapIdents(Seq(ident))
      case Nested(_, nestedElements)                                     => mapIdentifiers(nestedElements, identifiers0)
      case Composite(compositeElements)                                  => mapIdentifiers(compositeElements, identifiers0)
      case TxMetaData(txElements)                                        => mapIdentifiers(txElements, identifiers0)
    }).flatten
    (identifiers0 ++ newIdentifiers).distinct
  }

  def compareAttr(
    i: Int,
    attr: String,
    tpe: String,
    isEnum: Boolean,
    fn: String,
    optLimit: Option[Int],
    sort: String,
    reversed: Boolean = false
  ): (Int, Tree) = {
    if (attr.last == '$') {

      // Optional --------------------------------------------------------------------------------

      val (order, pair) = sort match {
        case r"a([1-5])$pos" => (pos.toInt, if (reversed) q"(y.get($i), x.get($i))" else q"(x.get($i), y.get($i))")
        case r"d([1-5])$pos" => (pos.toInt, if (reversed) q"(x.get($i), y.get($i))" else q"(y.get($i), x.get($i))")
      }
      val (x, y)        = tpe match {
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
      val result        =
        q"""$pair match {
             case (null, null)                     => 0
             case (null, _)                        => -1
             case (_, null)                        => 1
             case (m1: jMap[_, _], m2: jMap[_, _]) => $x.compareTo($y)
           }"""
      (order, result)

    } else {

      // Mandatory --------------------------------------------------------------------------------

      def compareType: (Tree, Tree) = {
        tpe match {
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
      def compareInt: (Tree, Tree) = (q"x.get($i).asInstanceOf[jInteger]", q"y.get($i).asInstanceOf[jInteger]")
      def compareDouble: (Tree, Tree) = (q"x.get($i).asInstanceOf[jDouble]", q"y.get($i).asInstanceOf[jDouble]")

      val (x, y) = if (fn.isEmpty) {
        compareType
      } else {
        (fn, optLimit) match {
          case ("min" | "max" | "distinct" | "rand" | "sample", Some(limit)) => abort(
            s"[Bug!] Unexpectedly trying to sort aggregate with applied limit. " +
              s"Found: $attr($fn($limit))."
          )
          case ("count" | "count-distinct", _)                               => compareInt
          case ("avg" | "stddev" | "variance", _)                            => compareDouble
          case (_, _) /* min, max, rand, sample, sum, median */              => compareType
        }
      }

      sort match {
        case r"a([1-5])$pos" => (pos.toInt, if (reversed) q"$y.compareTo($x)" else q"$x.compareTo($y)")
        case r"d([1-5])$pos" => (pos.toInt, if (reversed) q"$x.compareTo($y)" else q"$y.compareTo($x)")
      }
    }
  }

  // For flat and composite molecules
  def compare(model: Model, doSort: Boolean): Tree = {
    if (!doSort) {
      return q""
    }

    // Accumulate sort position / comparator
    val com = Seq.newBuilder[(Int, Tree)]

    var i = 0
    def addComparator(sort: String, value: Value, attr: String, tpeStr: String, isEnum: Boolean): Unit = {
      if (sort.nonEmpty) {
          val (sortPos, comparator) = value match {
            case Fn(fn, limit) => compareAttr(i, attr, tpeStr, isEnum, fn, limit, sort)
            case _             => compareAttr(i, attr, tpeStr, isEnum, "", None, sort)
          }
          com.+=((sortPos, comparator))

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

        case nested: Nested =>
          abort("Unexpectedly found nested elements in model for `compare`.")

        case Composite(elements) =>
          addOrderings(elements)

        case TxMetaData(elements) =>
          addOrderings(elements)
      }
    }

    // Resolve comparators
    addOrderings(model.elements)
    val comparatorTrees = com.result().sortBy(_._1).map(_._2)
    val comparators = comparatorTrees.size match {
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
        import java.lang.{Integer => jInteger, Long => jLong, Double => jDouble, Boolean => jBoolean}
        import java.math.{BigDecimal => jBigDec, BigInteger => jBigInt}
        ..$comparators
      }
    """
  }


  def compareOptNested(model: Model, doSort: Boolean): (Tree, Seq[Tree]) = {
    if (!doSort) {
      return (q"", Nil)
    }

    // Accumulate sort position / comparator
    val com = Seq.newBuilder[(Int, Tree)]

    // Accumulate current sort positions on this level
    val cur = Seq.newBuilder[(Int, Tree, Tree, Tree)]

    // Accumulate sort positions for each level
    var acc = Seq.empty[Seq[(Int, Tree, Tree, Tree)]]

    var i = 0
    def addComparator(sort: String, value: Value, attr: String, tpeStr: String, isEnum: Boolean, level: Int): Unit = {
      val opt = attr.last == '$'
      if (sort.nonEmpty) {
        if (level == 0) {
          val (sortPos, comparator) = value match {
            case Fn(fn, limit) => compareAttr(i, attr, tpeStr, isEnum, fn, limit, sort)
            case _             => compareAttr(i, attr, tpeStr, isEnum, "", None, sort)
          }
          com.+=((sortPos, comparator))

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
            cur.+=((sortPos, q"t.asInstanceOf[Option[$tpe]]", q"p.productElement($i).asInstanceOf[Option[$tpe]]", ordering))
          } else {
            val ordering = if (asc) q"Ordering[$tpe]" else q"Ordering[$tpe].reverse"
            cur.+=((sortPos, q"t.asInstanceOf[$tpe]", q"p.productElement($i).asInstanceOf[$tpe]", ordering))
          }
        }
        i += 1
      } else if (attr.last != '_') {
        i += 1
      }
    }

    def addOrderings(elements: Seq[Element], level: Int): Unit = {
      elements.collect {
        case Atom(_, attr, tpe, _, value, enumPrefix, _, _, sort) =>
          addComparator(sort, value, attr, tpe, enumPrefix.nonEmpty, level)

        case Generic(_, attr, tpe, value, sort) =>
          addComparator(sort, value, attr, tpe, isEnum = false, level)

        case nested: Nested =>
          acc = acc :+ cur.result().sortBy(_._1)
          // New sorting order on each nested level
          cur.clear()
          i = 0
          addOrderings(nested.elements, level + 1)
      }
    }

    // Resolve comparators
    addOrderings(model.elements, 0)
    val orderingTrees   = acc :+ cur.result().sortBy(_._1) // add last level orderings, sorted by sorting position
    val comparatorTrees = com.result().sortBy(_._1).map(_._2)
    val comparators     = comparatorTrees.size match {
      case 1 => q"${comparatorTrees.head}"
      case _ =>
        val moreComparators = comparatorTrees.tail.map(comparator => q"if (result == 0) result = $comparator")
        q"""
          var result = ${comparatorTrees.head}
          ..$moreComparators
          result
        """
    }
    val compareImpl     =
      q"""
        final override def sortRows: Boolean = true
        final override def compare(x: jList[AnyRef], y: jList[AnyRef]): Int = {
          import java.lang.{Integer => jInteger, Long => jLong, Double => jDouble, Boolean => jBoolean}
          import java.math.{BigDecimal => jBigDec, BigInteger => jBigInt}
          ..$comparators
        }
      """

    val orderings = orderingTrees.map { levelOrderings =>
      levelOrderings.size match {
        case 0 => q""
        case 1 => q"buf = buf.sortBy(t => ${levelOrderings.head._2})(${levelOrderings.head._4})"
        case n =>
          val (_, productCasts, orders) = levelOrderings.map(t => (t._2, t._3, t._4)).unzip3
          val tupleN                    = n match {
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
    (compareImpl, orderings)
  }


  def compareNested(model: Model, levels: Int, doSort: Boolean): Tree = {
    if (!doSort) {
      // Fall back on `compare` in NestedBase
      return q""
    }

    var i     = levels // skip nested indexes (1 for each level)
    var level = 0

    // Collect comparators on each level
    val cur1 = Seq.newBuilder[(Int, Tree)] // Comparators with tuples, reversed
    val cur2 = Seq.newBuilder[(Int, Tree)] // Comparators with Json, straight forward

    // Accumulate all comparator trees
    val acc1 = Seq.newBuilder[Tree]
    val acc2 = Seq.newBuilder[Tree]

    def addComparator(sort: String, value: Value, attr: String, tpe: String, isEnum: Boolean): Unit = {
      if (sort.nonEmpty) {
        val (comparator1, comparator2) = value match {
          case Fn(fn, limit) => (
            compareAttr(i, attr, tpe, isEnum, fn, limit, sort, true),
            compareAttr(i, attr, tpe, isEnum, fn, limit, sort)
          )
          case _             => (
            compareAttr(i, attr, tpe, isEnum, "", None, sort, true),
            compareAttr(i, attr, tpe, isEnum, "", None, sort)
          )
        }
        i += 1
        cur1 += comparator1
        cur2 += comparator2
      } else if (attr.last != '_') {
        i += 1
      }
    }

    def sortNestedIndex(level: Int, reversed: Boolean): Tree = if (reversed) {
      q"y.get($level).asInstanceOf[jLong].compareTo(x.get($level).asInstanceOf[jLong])"
    } else {
      q"x.get($level).asInstanceOf[jLong].compareTo(y.get($level).asInstanceOf[jLong])"
    }

    def resolveComparisons(elements: Seq[Element]): Unit = elements.collect {
      case Atom(_, attr, tpe, _, value, enumPrefix, _, _, sort) =>
        addComparator(sort, value, attr, tpe, enumPrefix.nonEmpty)

      case Generic(_, attr, tpe, value, sort) =>
        addComparator(sort, value, attr, tpe, isEnum = false)

      case nested: Nested =>
        acc1 ++= cur1.result().sortBy(_._1).map(_._2) :+ sortNestedIndex(level, true)
        acc2 ++= cur2.result().sortBy(_._1).map(_._2) :+ sortNestedIndex(level, false)
        cur1.clear()
        cur2.clear()
        level += 1
        resolveComparisons(nested.elements)

      //      case _ =>
    }

    lazy val (comparators1, comparators2) = {
      resolveComparisons(model.elements)
      (
        acc1.result() ++ cur1.result().sortBy(_._1).map(_._2) :+ sortNestedIndex(level, true),
        acc2.result() ++ cur2.result().sortBy(_._1).map(_._2) :+ sortNestedIndex(level, false)
      )
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
    lazy val ordering1 = getOrdering(comparators1)
    lazy val ordering2 = getOrdering(comparators2)

    // Override `compare` in NestedBase
    q"""
      final override def sortRows: Boolean = true
      final override def compare(x: jList[AnyRef], y: jList[AnyRef]): Int = {
        import java.lang.{Integer => jInteger, Long => jLong, Double => jDouble, Boolean => jBoolean}
        import java.math.{BigDecimal => jBigDec, BigInteger => jBigInt}
        if (isNestedTuples) {
          ..$ordering1
        } else {
          ..$ordering2
        }
      }
    """
  }
}
