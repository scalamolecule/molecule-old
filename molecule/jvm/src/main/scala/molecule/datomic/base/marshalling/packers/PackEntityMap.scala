package molecule.datomic.base.marshalling.packers

import java.util.Date
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.PackBase


trait PackEntityMap extends PackBase with Helpers {

  private def resolveMapValue(sb: StringBuffer, v: Any): StringBuffer = v match {
    case v: String => add(sb, v); end(sb) // end of text lines
    case d: Date   => add(sb, date2str(d))

    case m: Map[_, _] =>
      m.foreach {
        case (ref: String, refMap: Map[_, _]) =>
          add(sb, ref)
          next(sb) // next ref Map
          resolveMapValue(sb, refMap)
          nil(sb) // end of ref Map

        case (attr: String, v) =>
          add(sb, attr)
          resolveMapValue(sb, v)
      }
      sb

    case l: List[_] =>
      l.head match {
        case _: Map[_, _] =>
          // List of ref Maps
          l.foreach { refMap =>
            next(sb) // next ref Map
            resolveMapValue(sb, refMap)
          }
          nil(sb) // end of ref Maps

        case _ =>
          // List of values
          l.foreach(v => resolveMapValue(sb, v))
          next(sb) // end of collection
      }

    case _ => add(sb, v.toString)
  }

  private def resolveListValue(sb: StringBuffer, v: Any): StringBuffer = v match {
    case v: String => add(sb, v); end(sb) // end of text lines
    case d: Date   => add(sb, date2str(d))

    case l: List[_] =>
      l.head match {
        case (":db/id", _) =>
          // Attr-value pairs
          l.foreach {
            case (ref: String, l2: List[_]) =>
              l2.head match {
                case _: List[_] =>
                  add(sb, ref)
                  l2.foreach { refList =>
                    next(sb) // next ref List
                    resolveListValue(sb, refList)
                  }
                  nil(sb) // end of ref Lists

                case (":db/id", _) =>
                  add(sb, ref)
                  next(sb) // next ref List
                  resolveListValue(sb, l2)
                  nil(sb) // end of ref List

                case _ =>
                  add(sb, ref)
                  resolveListValue(sb, l2)
              }

            case (attr: String, v) =>
              add(sb, attr)
              resolveListValue(sb, v)
          }

        case _ =>
          // List of values
          l.foreach(v => resolveListValue(sb, v))
          next(sb) // end of collection
      }
      sb

    case _ => add(sb, v.toString)
  }

  def entityMap2packed2(entityMap: Map[String, Any]): String = {
    resolveMapValue(new StringBuffer(), entityMap).toString.tail
  }

  def entityList2packed2(entityList: List[(String, Any)]): String = {
    resolveListValue(new StringBuffer(), entityList).toString.tail
  }
}
