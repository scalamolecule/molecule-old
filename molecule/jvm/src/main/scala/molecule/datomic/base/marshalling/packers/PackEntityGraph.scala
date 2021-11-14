package molecule.datomic.base.marshalling.packers

import java.util.Date
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.PackBase


private[molecule] trait PackEntityGraph extends PackBase with Helpers {

  protected def entityGraph2packed(sb: StringBuffer, v: Any): StringBuffer = v match {
    case v: String    => add(sb, v); end(sb) // end of text lines
    case d: Date      => add(sb, date2str(d))
    case m: Map[_, _] =>
      m.foreach {
        case (ref: String, refMap: Map[_, _]) =>
          //          println("-- 11 --  " + ref + "   " + v)
          add(sb, ref)
          entityGraph2packed(sb, refMap)
          nil(sb) // end of ref Map

        case (attr: String, v) =>
          //          println("-- 12 --  " + attr + "   " + v)
          add(sb, attr)
          entityGraph2packed(sb, v)
      }
      sb

    case l: Iterable[_] =>
      l.head match {
        case (":db/id", _) =>
          // Attr-value pairs
          l.foreach {
            case (ref: String, l2: List[_]) =>
              l2.head match {
                case (":db/id", _) =>
                  //                  println("-- 22 --  " + ref + "   " + v)
                  add(sb, ref)
                  next(sb) // next ref List
                  entityGraph2packed(sb, l2)
                  nil(sb) // end of ref List

                case _ =>
                  //                  println("-- 23 --  " + ref + "   " + v)
                  add(sb, ref)
                  entityGraph2packed(sb, l2)
              }

            case (attr: String, v) =>
              //              println("-- 24 --  " + attr + "   " + v)
              add(sb, attr)
              entityGraph2packed(sb, v)
          }

        case _ =>
          //          println("-- 25 --  " + l)
          // List of values
          l.foreach(v => entityGraph2packed(sb, v))
          next(sb) // end of collection
      }
      sb


    case (attr: String, v) =>
      //      println("-- 30 --  " + attr + "   " + v)
      add(sb, attr)
      entityGraph2packed(sb, v)

    case _ =>
      //      println("-- 40 --  " + v)
      add(sb, v.toString)
  }

  def entityMap2packed(entityMap: Map[String, Any]): String = {
    //    println("------ entityMap2packed ------\n" + entityMap.mkString("\n"))
    val packed = entityGraph2packed(new StringBuffer(), entityMap).toString.tail
    //    println("------------\n" + packed)
    packed
  }

  def entityList2packed(entityList: List[(String, Any)]): String = {
    //    println("------ entityList2packed ------\n" + entityList.mkString("\n"))
    val packed = entityGraph2packed(new StringBuffer(), entityList).toString.tail
    //    println("------------\n" + packed)
    packed
  }
}
