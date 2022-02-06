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

  def topLevelLookups(castss: List[List[Int => Tree]], lookups: List[Tree], offset: Int = 0): List[Tree] = {
    var i = -1 + offset
    castss.head.map { _ =>
      i += 1
      lookups(i)
    }
  }

  def compare(model0: Model, doSort: Boolean): Tree = if (doSort) {
    def comp(
      i: Int,
      attr: String,
      tpe: String,
      isEnum: Boolean,
      fn: String,
      optLimit: Option[Int],
      sort: String
    ): (Int, Tree) = {
      if (attr.last == '$') {

        // Optional --------------------------------------------------------------------------------

        val (order, pair) = sort match {
          case r"a([1-5])$order" => (order.toInt, q"(a.get($i), b.get($i))") // ascending
          case r"d([1-5])$order" => (order.toInt, q"(b.get($i), a.get($i))") // descending
        }
        val (a, b)        = tpe match {
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
             case (m1: jMap[_, _], m2: jMap[_, _]) => $a.compareTo($b)
           }"""
        (order, result)
      } else {

        // Mandatory --------------------------------------------------------------------------------

        def compareType: (Tree, Tree) = {
          tpe match {
            case "String" | "enum"      => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
            case "Int" | "Long" | "ref" => (q"a.get($i).asInstanceOf[jLong]", q"b.get($i).asInstanceOf[jLong]")
            case "Double"               => (q"a.get($i).asInstanceOf[jDouble]", q"b.get($i).asInstanceOf[jDouble]")
            case "Boolean"              => (q"a.get($i).asInstanceOf[jBoolean]", q"b.get($i).asInstanceOf[jBoolean]")
            case "Date"                 => (q"a.get($i).asInstanceOf[Date]", q"b.get($i).asInstanceOf[Date]")
            case "UUID"                 => (q"a.get($i).asInstanceOf[UUID]", q"b.get($i).asInstanceOf[UUID]")
            case "URI"                  => (q"a.get($i).asInstanceOf[URI]", q"b.get($i).asInstanceOf[URI]")
            case "BigInt"               => (q"a.get($i).asInstanceOf[jBigInt]", q"b.get($i).asInstanceOf[jBigInt]")
            case "BigDecimal"           => (q"a.get($i).asInstanceOf[jBigDec]", q"b.get($i).asInstanceOf[jBigDec]")

            case "datom" | "log" | "eavt" | "aevt" | "avet" | "vaet" => attr match {
              case "e"         => (q"a.get($i).asInstanceOf[jLong]", q"b.get($i).asInstanceOf[jLong]")
              case "a"         => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "v"         => (q"a.get($i).toString", q"b.get($i).toString")
              case "t"         => (q"a.get($i).asInstanceOf[jLong]", q"b.get($i).asInstanceOf[jLong]")
              case "tx"        => (q"a.get($i).asInstanceOf[jLong]", q"b.get($i).asInstanceOf[jLong]")
              case "txInstant" => (q"a.get($i).asInstanceOf[Date]", q"b.get($i).asInstanceOf[Date]")
              case "op"        => (q"a.get($i).asInstanceOf[jBoolean]", q"b.get($i).asInstanceOf[jBoolean]")
            }

            case "schema" => attr match {
              case "attrId"      => (q"a.get($i).asInstanceOf[jLong]", q"b.get($i).asInstanceOf[jLong]")
              case "part"        => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "nsFull"      => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "ns"          => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "a"           => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "attr"        => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "ident"       => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "valueType"   => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "cardinality" => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "doc"         => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "unique"      => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "isComponent" => (q"a.get($i).asInstanceOf[jBoolean]", q"b.get($i).asInstanceOf[jBoolean]")
              case "noHistory"   => (q"a.get($i).asInstanceOf[jBoolean]", q"b.get($i).asInstanceOf[jBoolean]")
              case "index"       => (q"a.get($i).asInstanceOf[jBoolean]", q"b.get($i).asInstanceOf[jBoolean]")
              case "fulltext"    => (q"a.get($i).asInstanceOf[jBoolean]", q"b.get($i).asInstanceOf[jBoolean]")
              case "enumm"       => (q"a.get($i).asInstanceOf[String]", q"b.get($i).asInstanceOf[String]")
              case "t"           => (q"a.get($i).asInstanceOf[jLong]", q"b.get($i).asInstanceOf[jLong]")
              case "tx"          => (q"a.get($i).asInstanceOf[jLong]", q"b.get($i).asInstanceOf[jLong]")
              case "txInstant"   => (q"a.get($i).asInstanceOf[Date]", q"b.get($i).asInstanceOf[Date]")
            }
            case other    => abort(s"Unexpected type '$other' for sort attribute `$attr`")
          }
        }
        def compareInt: (Tree, Tree) = (q"a.get($i).asInstanceOf[jInteger]", q"b.get($i).asInstanceOf[jInteger]")
        def compareDouble: (Tree, Tree) = (q"a.get($i).asInstanceOf[jDouble]", q"b.get($i).asInstanceOf[jDouble]")

        val (a, b) = if (fn.isEmpty) {
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
          case r"a([1-5])$order" => (order.toInt, q"$a.compareTo($b)") // ascending
          case r"d([1-5])$order" => (order.toInt, q"$b.compareTo($a)") // descending
        }
      }
    }

    val comparisons = model0.elements.foldLeft(0, Seq.empty[(Int, Tree)]) {
      case ((i, acc), Atom(_, attr, tpe, _, value, enumPrefix, _, _, sort)) =>
        if (sort.nonEmpty) {
          val comparator = value match {
            case Fn(fn, limit) => comp(i, attr, tpe, enumPrefix.nonEmpty, fn, limit, sort)
            case _             => comp(i, attr, tpe, enumPrefix.nonEmpty, "", None, sort)
          }
          (i + 1, acc :+ comparator)
        } else if (attr.last == '_') {
          (i, acc)
        } else {
          (i + 1, acc)
        }

      case ((i, acc), Generic(_, attr, tpe, value, sort)) =>
        if (sort.nonEmpty) {
          val comparator = value match {
            case Fn(fn, limit) => comp(i, attr, tpe, isEnum = false, fn, limit, sort)
            case _             => comp(i, attr, tpe, isEnum = false, "", None, sort)
          }
          (i + 1, acc :+ comparator)
        } else if (attr.last == '_') {
          (i, acc)
        } else {
          (i + 1, acc)
        }

      case ((i, acc), _) =>
        (i, acc)
    }._2
      .sortBy(_._1) // sort order positions
      .map(_._2)

    val ordering = comparisons.size match {
      case 1 => q"${comparisons.head}"
      case _ =>
        val moreComparisons = comparisons.tail.map(comparison => q"if (result == 0) result = $comparison")
        q"""
          var result = ${comparisons.head}
          ..$moreComparisons
          result
        """
    }

    q"""
      final override def sortRows: Boolean = true
      final override def compare(a: jList[AnyRef], b: jList[AnyRef]): Int = {
        import java.lang.{Integer => jInteger, Long => jLong, Double => jDouble, Boolean => jBoolean}
        import java.math.{BigDecimal => jBigDec, BigInteger => jBigInt}
        ..$ordering
      }
    """
  } else q""
}
