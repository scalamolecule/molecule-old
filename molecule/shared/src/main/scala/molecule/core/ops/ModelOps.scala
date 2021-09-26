package molecule.core.ops

import java.util.Date
import molecule.core.ast.elements._
import molecule.core.util.fns


/** Model operations */
object ModelOps {

  def convert(isJsPlatform: Boolean, tpe: String, value: Any): Any = {
    val conv = if (isJsPlatform) {
      (v: Any) =>
        v match {
          case _: Int | _: Long  =>
            tpe match {
              case "Double"     => "__n__" + v + ".0"
              case "BigDecimal" => "__n__" + v + ".0M"
              case _            => v
            }
          case _: Float =>
            tpe match {
              case "Double" if v.toString.contains(".")     => "__n__" + v
              case "Double"                                 => "__n__" + v + ".0"
              case "BigDecimal" if v.toString.contains(".") => "__n__" + v + "M"
              case "BigDecimal"                             => "__n__" + v + ".0M"
              case _                                        => v.toString.toDouble
            }

          case _: Double        => tpe match {
            case "Double"     => "__n__" + v.toString + (if (v.toString.contains(".")) "" else ".0")
            case "BigDecimal" => "__n__" + v.toString + (if (v.toString.contains(".")) "" else ".0")
            case _            => v.toString
          }
          case d: Date       => fns.date2str(d)
          case _: BigDecimal => v.toString + (if (v.toString.contains(".")) "" else ".0")
          case _             => v.toString
        }
    } else {
      (v: Any) =>
        v match {
          case v: Int    => tpe match {
            case "Long"       => v.toLong
            case "Double"     => v.toDouble
            case "BigInt"     => BigInt(v)
            case "BigDecimal" => BigDecimal(v)
            case _            => v
          }
          case v: Long   => tpe match {
            case "Double"     => v.toDouble
            case "BigInt"     => BigInt(v)
            case "BigDecimal" => BigDecimal(v)
            case _            => v
          }
          case v: Float  => tpe match {
            case "Double"     => v.toDouble
            case "BigDecimal" => BigDecimal(v)
            case _            => v
          }
          case v: Double => tpe match {
            case "BigDecimal" => BigDecimal(v)
            case _            => v
          }
          case v: BigInt => tpe match {
            case "BigDecimal" => BigDecimal(v)
            case _            => v
          }
          case _         => v
        }
    }

    val res = value match {
      case set: scala.collection.Set[_]  => set.map(v => conv(v))
      case seq: scala.collection.Seq[_]  => seq.map(v => conv(v))
      case m: scala.collection.Map[_, _] => m.toSeq.map { case (k, v) => (k, conv(v)) }
      case Some(v)                       => convert(isJsPlatform, tpe, v)
      case v                             => conv(v)
    }
    //    println(s"@@@@@@  $tpe  $value  ${value.getClass}  $res")
    res
  }

  def resolveIdentifiers(isJsPlatform: Boolean, model: Model, identMap: Map[String, Any]): Model = {
    def flatSeq(tpe: String, a: Any): scala.collection.Seq[Any] = (a match {
      case seq: scala.collection.Seq[_] => seq
      case set: scala.collection.Set[_] => set.toSeq
      case v                            => Seq(v)
    }).map(v => convert(isJsPlatform, tpe, v))

    def getKeys(tpe: String, keyIdents: Seq[String]): Seq[String] = getValues(tpe, keyIdents).flatMap {
      case keys: scala.collection.Seq[_] => keys
      case key                           => Seq(key)
    }.asInstanceOf[Seq[String]]

    def getValues(tpe: String, idents: Seq[Any]): Seq[Any] = idents.flatMap {
      case set: scala.collection.Set[_] if set.nonEmpty => Seq(set.flatMap {
        case ident if ident.toString.startsWith("__ident__") => flatSeq(tpe, identMap(ident.toString))
        case value                                           => Seq(convert(isJsPlatform, tpe, value))
      })

      case v: String if v.startsWith("__ident__")                                           => flatSeq(tpe, identMap(v))
      case (k: String, "__pair__") if k.startsWith("__ident__")                             => flatSeq(tpe, identMap(k))
      case (k: String, v: String) if k.startsWith("__ident__") && v.startsWith("__ident__") => Seq((identMap(k), identMap(v)))
      case (k: String, v: Any) if k.startsWith("__ident__")                                 => Seq((identMap(k), convert(isJsPlatform, tpe, v)))
      case (k: Any, v: String) if v.startsWith("__ident__")                                 => Seq((convert(isJsPlatform, tpe, k), identMap(v)))
      case (k, v)                                                                           => Seq((convert(isJsPlatform, tpe, k), convert(isJsPlatform, tpe, v)))
      case seq: scala.collection.Seq[_]                                                     => seq.map(v => convert(isJsPlatform, tpe, v))
      case v                                                                                => Seq(convert(isJsPlatform, tpe, v))
    }

    def resolve(elements: Seq[Element]): Seq[Element] = elements map {
      case a@Atom(_, _, tpe, _, MapEq(idents), _, _, keyIdents)           => idents match {
        case List((ident, "__pair__"))
          if ident.startsWith("__ident__") && getValues(tpe, Seq(ident)) == Seq(None) =>
          a.copy(value = Fn("not", None), keys = getKeys(tpe, keyIdents))

        case idents =>
          a.copy(value = MapEq(getValues(tpe, idents).asInstanceOf[Seq[(String, Any)]]), keys = getKeys(tpe, keyIdents))
      }
      case a@Atom(_, _, tpe, 2, Eq(idents), _, _, _)                      => getValues(tpe, idents) match {
        case Seq(None) => a.copy(value = Fn("not", None))
        case values    => a.copy(value = Eq(values))
      }
      case a@Atom(_, _, tpe, _, Eq(idents), _, _, keyIdents)              => getValues(tpe, idents) match {
        case Seq(None) => a.copy(value = Fn("not", None), keys = getKeys(tpe, keyIdents))
        case values    => a.copy(value = Eq(values), keys = getKeys(tpe, keyIdents))
      }
      case g@Generic(_, _, tpe, Eq(idents))                               => getValues(tpe, idents) match {
        case Seq(None) => g.copy(value = Fn("not", None))
        case values    => g.copy(value = Eq(values))
      }
      case a@Atom(_, _, tpe, _, Neq(idents), _, _, keyIdents)             => a.copy(value = Neq(getValues(tpe, idents)), keys = getKeys(tpe, keyIdents))
      case a@Atom(_, _, tpe, _, And(idents), _, _, keyIdents)             => a.copy(value = And(getValues(tpe, idents)), keys = getKeys(tpe, keyIdents))
      case a@Atom(_, _, tpe, _, Lt(ident), _, _, keyIdents)               => a.copy(value = Lt(getValues(tpe, Seq(ident)).head), keys = getKeys(tpe, keyIdents))
      case a@Atom(_, _, tpe, _, Gt(ident), _, _, keyIdents)               => a.copy(value = Gt(getValues(tpe, Seq(ident)).head), keys = getKeys(tpe, keyIdents))
      case a@Atom(_, _, tpe, _, Le(ident), _, _, keyIdents)               => a.copy(value = Le(getValues(tpe, Seq(ident)).head), keys = getKeys(tpe, keyIdents))
      case a@Atom(_, _, tpe, _, Ge(ident), _, _, keyIdents)               => a.copy(value = Ge(getValues(tpe, Seq(ident)).head), keys = getKeys(tpe, keyIdents))
      case a@Atom(_, _, tpe, _, Fulltext(idents), _, _, keyIdents)        => a.copy(value = Fulltext(getValues(tpe, idents)), keys = getKeys(tpe, keyIdents))
      case a@Atom(_, _, tpe, _, AssertValue(idents), _, _, _)             => a.copy(value = AssertValue(getValues(tpe, idents)))
      case a@Atom(_, _, tpe, _, RetractValue(idents), _, _, _)            => a.copy(value = RetractValue(getValues(tpe, idents)))
      case a@Atom(_, _, tpe, _, ReplaceValue(oldNew), _, _, _)            => a.copy(value = ReplaceValue(getValues(tpe, oldNew).asInstanceOf[Seq[(Any, Any)]]))
      case a@Atom(_, _, tpe, _, AssertMapPairs(idents), _, _, keyIdents)  => a.copy(value = AssertMapPairs(getValues(tpe, idents).asInstanceOf[Seq[(String, Any)]]), keys = getKeys(tpe, keyIdents))
      case a@Atom(_, _, tpe, _, ReplaceMapPairs(idents), _, _, keyIdents) => a.copy(value = ReplaceMapPairs(getValues(tpe, idents).asInstanceOf[Seq[(String, Any)]]), keys = getKeys(tpe, keyIdents))
      case a@Atom(_, _, tpe, _, RetractMapKeys(idents), _, _, keyIdents)  => a.copy(value = RetractMapKeys(getValues(tpe, idents).map(_.toString)), keys = getKeys(tpe, keyIdents))
      case a@Atom(_, _, tpe, _, MapKeys(idents), _, _, _)                 => a.copy(value = MapKeys(getValues(tpe, idents).asInstanceOf[Seq[String]]))
      case g@Generic(_, _, tpe, Neq(idents))                              => g.copy(value = Neq(getValues(tpe, idents)))
      case g@Generic(_, _, tpe, Lt(ident))                                => g.copy(value = Lt(getValues(tpe, Seq(ident)).head))
      case g@Generic(_, _, tpe, Gt(ident))                                => g.copy(value = Gt(getValues(tpe, Seq(ident)).head))
      case g@Generic(_, _, tpe, Le(ident))                                => g.copy(value = Le(getValues(tpe, Seq(ident)).head))
      case g@Generic(_, _, tpe, Ge(ident))                                => g.copy(value = Ge(getValues(tpe, Seq(ident)).head))
      case Nested(bond, nestedElements)                                   => Nested(bond, resolve(nestedElements))
      case Composite(compositeElements)                                   => Composite(resolve(compositeElements))
      case TxMetaData(txElements)                                         => TxMetaData(resolve(txElements))
      case other                                                          => other
    }

    if (identMap.isEmpty) model else Model(resolve(model.elements))
  }
}