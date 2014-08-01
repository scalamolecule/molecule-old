package molecule.util
import scala.collection.JavaConversions._

trait Debug {

  def hej(clazz: String, threshold: Int, max: Int = 9999, debug: Boolean = false, maxLevel: Int = 99) =
  new debug(clazz, threshold, max, debug, maxLevel)

  case class debug(clazz: String, threshold: Int, max: Int = 9999, debug: Boolean = false, maxLevel: Int = 99) {

    def apply(id: Int, params: Any*): Unit = {
      val stackTrace = if (debug) Thread.currentThread.getStackTrace mkString "\n" else ""
      if (id >= threshold && id <= max) {

        def traverse(x: Any, level: Int, i: Int): String = {
          val space = if (i < 10) "          " else "         "
          val indent = if (i == 0) "" else "  " * level + i + space
          val max = level >= maxLevel
          x match {
            case l: List[_] if max             => indent + "List(" + l.mkString(",   ") + ")"
            case l: List[_]                    => indent + "List(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1)}.mkString("\n") + ")"
            case l: java.util.List[_] if max   => indent + "JavaList(" + l.mkString(",   ") + ")"
            case l: java.util.List[_]          => indent + "JavaList(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1)}.mkString("\n") + ")"
            case l: Map[_, _] if max           => indent + "Map(" + l.mkString(",   ") + ")"
            case l: Map[_, _]                  => indent + "Map(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1)}.mkString("\n") + ")"
            case l: java.util.Map[_, _] if max => indent + "JavaMap(" + l.mkString(",   ") + ")"
            case l: java.util.Map[_, _]        => indent + "JavaMap(                          // order is randomm!\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1)}.mkString("\n") + ")"
            case (a, b)                        => {
              val bb = b match {
                case it: Iterable[_]             => traverse(it, level, 0)
                case it: java.util.Collection[_] => traverse(it, level, 0)
                case other                       => other
              }
              indent + s"$a -> " + bb
            }
            case value                         => indent + value  //+ s" TYPE: " + value.getClass
          }
        }

        println(s"## $id: $clazz \n========================================================================\n" +
          params.toList.zipWithIndex.map { case (e, i) => traverse(e, 0, i + 1)}
            .mkString("\n------------------------------------------------\n") +
          s"\n========================================================================\n$stackTrace")
      }
    }
  }

  case class trace(debug: Boolean = true) {
    def apply(id: Int, msgs: Any*) { print(s"$id -> " + msgs.mkString("  ###  ")) }
  }
}