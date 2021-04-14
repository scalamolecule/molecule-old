package molecule.core.ops

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.ast.elements._
import molecule.core.marshalling.Col
import molecule.datomic.base.ast.metaSchema._
import molecule.datomic.base.ast.query._
import molecule.datomic.base.transform.Model2Query.coalesce
import scala.collection.mutable
import scala.collection.mutable.ListBuffer


trait ColOps  {

  def getCols(elements: Seq[Element])
             (implicit nsMap: Map[String, MetaNs]): Seq[Col] = {
    var i          = 0
    val cols       = new ListBuffer[Col]()
    var related    = 0
    var curNsAlias = ""
    var prevAttr   = ""
    elements.foreach {
      case Atom(nsFull, attr, tpe, card, value, enumPrefix, _, keys) if attr.last != '_' => {
        if (curNsAlias.isEmpty)
          curNsAlias = nsFull

        val enums = if (enumPrefix.isDefined) {
          val attr0 = clean(attr)
          nsMap(nsFull).attrs.collectFirst {
            case MetaAttr(_, `attr0`, _, _, enumsOpt, _, _, _, _, _, _, _, _) =>
              enumsOpt
          }.getOrElse(Seq.empty[String])
        } else {
          Seq.empty[String]
        }
        val kind  = if (keys == Seq("edit"))
          "edit"
        else if (keys.nonEmpty && keys.head == "orig")
          "orig"
        else
          ""

        cols += Col(
          i, related, curNsAlias, nsFull, attr, tpe,
//          getColType(attr, card, tpe),
          tpe,
          card,
          attr.last == '$',
          enums,
          "", //getAggrType(value),
          "", //getExpr(value),
          kind = kind
        )
        i += 1
        prevAttr = attr
      }

      case _: Atom => // tacit attribute

      case Generic(nsFull, attr, tpe, value) =>
        if (curNsAlias.isEmpty)
          curNsAlias = nsFull

        attr match {
          case "e" =>
            cols += Col(i, related, curNsAlias, nsFull, attr, tpe, "double", 1,
//              aggrType = getAggrType(value),
//              attrExpr = getExpr(value)
            )
            i += 1

          case "t" | "tx" =>
            cols += Col(i, related, curNsAlias, nsFull, prevAttr, "Long", "double", 1, kind = attr)
            i += 1

          case "txInstant" =>
            cols += Col(i, related, curNsAlias, nsFull, prevAttr, "Date", "string", 1, kind = attr)
            i += 1

          case _ =>
        }

      case Bond(_, refAttr1, _, _, _) =>
        related = 1
        curNsAlias = refAttr1.capitalize

      case _: ReBond =>
      case e         => throw new IllegalArgumentException("Unexpected element for table layout: " + e)
    }

    cols.toSeq
  }




  // Todo - hack to transmit input types because we can't transfer them typed with boopickle - maybe use upickle instead here?
  def c(arg: Any): String = arg match {
    case _: String     => "String"
    case _: Int        => "Long"
    case _: Long       => "Long"
    case _: Float      => "Double"
    case _: Double     => "Double"
    case _: BigInt     => "BigInt"
    case _: BigDecimal => "BigDecimal"
    case _: Boolean    => "Boolean"
    case _: Date       => "Date"
    case _: UUID       => "UUID"
    case _: URI        => "URI"
  }

  def encodeInputs(q: Query): (
    Seq[(Int, (String, String))],
      Seq[(Int, Seq[(String, String)])],
      Seq[(Int, Seq[Seq[(String, String)]])]
    ) = q.i.inputs.zipWithIndex.foldLeft(
    Seq.empty[(Int, (String, String))],
    Seq.empty[(Int, Seq[(String, String)])],
    Seq.empty[(Int, Seq[Seq[(String, String)]])]
  ) {
    case ((l, ll, lll), (InVar(RelationBinding(_), argss), i))   => (l, ll, lll :+ (i, argss.map(v => v.map(w => (c(w), w.toString)))))
    case ((l, ll, lll), (InVar(CollectionBinding(_), argss), i)) => (l, ll :+ (i, argss.head.map(v => (c(v), v.toString))), lll)
    case ((l, ll, lll), (InVar(_, argss), i))                    => (l :+ (i, (c(argss.head.head), argss.head.head.toString)), ll, lll)
    case ((l, ll, lll), (InDataSource(_, argss), i))             => (l :+ (i, (c(argss.head.head), argss.head.head.toString)), ll, lll)
    case other                                                   => sys.error(s"[molecule.ops.QueryOps] UNEXPECTED inputs: $other")
  }


//  def getAggrType(value: Value) = value match {
//    case fn: Fn   => fn match {
//      case Fn("count" | "count-distinct", _)                   => "aggrInt"
//      case Fn("avg" | "variance" | "stddev", _)                => "aggrDouble"
//      case Fn("min" | "max" | "rand" | "sum" | "median", None) => "aggrSingle"
//      case Fn("sample", Some(1))                               => "aggrSingleSample"
//      case Fn("min" | "max" | "sample", Some(_))               => "aggrList"
//      case Fn("rand", Some(_))                                 => "aggrListRand"
//      case other                                               =>
//        throw new RuntimeException(s"Unexpected fn: $other")
//    }
//    case Distinct => "aggrListDistinct"
//    case _        => ""
//  }
}
