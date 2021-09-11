package molecule.datomic.base.marshalling.packers

import java.util.Date
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.PackBase


trait PackEntityMap extends PackBase with Helpers {

  private def resolveMapValue(sb: StringBuffer, v: Any): Unit = v match {
    case v: String => add(v); end() // end of text lines
    case d: Date   => add(date2str(d))

    case m: Map[_, _] =>
      m.foreach {
        case (ref: String, refMap: Map[_, _]) =>
          add(ref)
          next() // next ref Map
          resolveMapValue(sb, refMap)
          nil() // end of ref Map

        case (attr: String, v) =>
          add(attr)
          resolveMapValue(sb, v)
      }

    case l: List[_] =>
      l.head match {
        case _: Map[_, _] =>
          // List of ref Maps
          l.foreach { refMap =>
            next() // next ref Map
            resolveMapValue(sb, refMap)
          }
          nil() // end of ref Maps

        case _ =>
          // List of values
          l.foreach(v => resolveMapValue(sb, v))
          next() // end of collection
      }

    case _ => add(v.toString)
  }

  private def resolveListValue(sb: StringBuffer, v: Any): Unit = v match {
    case v: String => add(v); end() // end of text lines
    case d: Date   => add(date2str(d))

    case l: List[_] =>
      l.head match {
        case (":db/id", _) =>
          // Attr-value pairs
          l.foreach {
            case (ref: String, l2: List[_]) =>
              l2.head match {
                case _: List[_] =>
                  add(ref)
                  l2.foreach { refList =>
                    next() // next ref List
                    resolveListValue(sb, refList)
                  }
                  nil() // end of ref Lists

                case (":db/id", _) =>
                  add(ref)
                  next() // next ref List
                  resolveListValue(sb, l2)
                  nil() // end of ref List

                case _ =>
                  add(ref)
                  resolveListValue(sb, l2)
              }

            case (attr: String, v) =>
              add(attr)
              resolveListValue(sb, v)
          }

        case _ =>
          // List of values
          l.foreach(v => resolveListValue(sb, v))
          next() // end of collection
      }

    case _ => add(v.toString)
  }

  def entityMap2packed2(entityMap: Map[String, Any]): String = {
    resolveMapValue(new StringBuffer(), entityMap)
    sb.toString.tail
  }

  def entityList2packed2(entityList: List[(String, Any)]): String = {
    resolveListValue(new StringBuffer(), entityList)
    sb.toString.tail
  }
}
