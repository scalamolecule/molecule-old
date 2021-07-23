package molecule.core.macros.build

import molecule.core.ops.TreeOps
import scala.reflect.macros.blackbox
import java.util.{Collection => jCollection, Iterator => jIterator, List => jList, Map => jMap}


trait BuildBase extends TreeOps {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("BuildBase", 1, 900)

  sealed trait BuilderNode
  case class BuilderProp(
    cls: String,
    prop: String,
    tpe: Tree,
    cast: Int => Tree,
    json: (Int, Int) => Tree,
    optAggrTpe: Option[String] = None
  ) extends BuilderNode {
    override def toString: String = {
      // Since the cast lambda is just an object reference, we simply add null so that we can copy/paste
      s"""BuilderProp("$cls", "$prop", "$tpe", <cast-lambda>, <json-lambda>, $optAggrTpe)"""
    }
  }

  case class BuilderObj(
    cls: String,
    ref: String,
    card: Int,
    props: List[BuilderNode]
  ) extends BuilderNode {
    override def toString: String = {
      def draw(nodes: Seq[BuilderNode], indent: Int): Seq[String] = {
        val s = "  " * indent
        nodes map {
          case BuilderObj(cls, ref, card, props) =>
            s"""|${s}BuilderObj("$cls", "$ref", $card, List(
                |${draw(props, indent + 1).mkString(s",\n")}))""".stripMargin
          case prop                              => s"$s$prop"
        }
      }
      draw(Seq(this), 0).head
    }
  }

  def isDeeper(obj: BuilderObj) = obj.props.last match {
    case BuilderObj(_, _, 2, _) => true
    case _                      => false
  }

  def getPropCount(nodes: List[BuilderNode]): Int = nodes.foldLeft(0) {
    case (acc, _: BuilderProp)                => acc + 1
    case (acc, BuilderObj(_, _, 1, refProps)) => acc + getPropCount(refProps)
    case (acc, _: BuilderObj)                 => acc // ignore nested - handled in macros
  }

  //  def extractFlatValues(
  //    nestedRows: jList[Any],
  //    propCount: Int,
  //    refIndexes: List[Int],
  //    tacitIndexes: List[Int],
  //    deeper: Boolean = false
  //  ): jIterator[Any] = {
  //    val rowCount             = nestedRows.size()
  //    val flatValues           = new java.util.ArrayList[Any](rowCount * propCount)
  //    val nonTacitIndexes      = (0 until propCount).diff(tacitIndexes)
  //    var testArray            = new Array[AnyRef](propCount)
  //    val testList             = new java.util.ArrayList[Any](propCount)
  //    var vs: jCollection[Any] = null
  //    var i                    = 0
  //
  //    println("================================")
  //    println("nestedRows      : " + nestedRows)
  //    println("propCount       : " + propCount)
  //    println("refIndexes      : " + refIndexes)
  //    println("tacitIndexes    : " + tacitIndexes)
  //    println("mandatoryIndexes: " + nonTacitIndexes)
  //
  //    (refIndexes.isEmpty, tacitIndexes.isEmpty) match {
  //      case (true, true) =>
  //        nestedRows.forEach { row =>
  //          vs = row.asInstanceOf[jMap[Any, Any]].values()
  //          println("-- 1 ------- " + vs.size + "  " + propCount)
  //          vs.forEach(v => println(v))
  //          if (deeper && vs.size() - 1 == propCount || vs.size() == propCount)
  //            flatValues.addAll(vs)
  //        }
  //
  //      case (true, false) =>
  //        nestedRows.forEach { row =>
  //          vs = row.asInstanceOf[jMap[Any, Any]].values()
  //          testArray = vs.toArray
  //          // Skip all values on this level if some tacit value is missing
  //          val valid: Boolean = tacitIndexes.collectFirst {
  //            case i if testArray(i) == "__none__" => true
  //          }.isEmpty
  //
  //          println("-- 2 ------- " + valid)
  //          vs.forEach(v => println(v))
  //          if (valid) {
  //            // Get non-tacit values only
  //            nonTacitIndexes.foreach { i =>
  //              flatValues.add(testArray(i))
  //            }
  //          }
  //        }
  //
  //      case (false, true) =>
  //        nestedRows.forEach { row =>
  //          vs = row.asInstanceOf[jMap[Any, Any]].values()
  //          testList.clear()
  //          i = 0
  //          def addValues(vs: jCollection[Any]): Unit = vs.forEach {
  //            case ref: jMap[_, _] => addValues(ref.asInstanceOf[jMap[Any, Any]].values())
  //            case v               => i += 1; testList.add(v)
  //          }
  //          addValues(vs)
  //          println("-- 3 -------")
  //          testList.forEach(v => println(v))
  //          if (i == propCount)
  //            flatValues.addAll(testList)
  //        }
  //
  //      case (false, false) =>
  //        nestedRows.forEach { row =>
  //          vs = row.asInstanceOf[jMap[Any, Any]].values()
  //          testList.clear()
  //          i = 0
  //          def addValues(vs: jCollection[Any]): Unit = vs.forEach {
  //            case ref: jMap[_, _]                        => addValues(ref.asInstanceOf[jMap[Any, Any]].values())
  //            case "__none__" if tacitIndexes.contains(i) => i += 1 // tacit value missing
  //            case v if tacitIndexes.contains(i)          => // tacit value exists
  //            case v                                      => i += 1; testList.add(v)
  //          }
  //          addValues(vs)
  //          println("-- 4 ------- " + i + "  " + testList.size)
  //          testList.forEach(v => println(v))
  //          if (i == testList.size)
  //            flatValues.addAll(testList)
  //        }
  //    }
  //
  //    println("-------------------------------")
  //    flatValues.forEach(v => println(v))
  //
  //    flatValues.iterator
  //  }


  def addNode(obj: BuilderObj, node: BuilderNode, level: Int): BuilderObj = {
    val newProps = level match {
      case 0 =>
        node :: obj.props

      case 1 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj1a = obj1.copy(props = node :: obj1.props)
        obj1a :: obj.props.tail

      case 2 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj2a = obj2.copy(props = node :: obj2.props)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 3 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj3  = obj2.props.head.asInstanceOf[BuilderObj]
        val obj3a = obj3.copy(props = node :: obj3.props)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 4 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj3  = obj2.props.head.asInstanceOf[BuilderObj]
        val obj4  = obj3.props.head.asInstanceOf[BuilderObj]
        val obj4a = obj4.copy(props = node :: obj4.props)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 5 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj3  = obj2.props.head.asInstanceOf[BuilderObj]
        val obj4  = obj3.props.head.asInstanceOf[BuilderObj]
        val obj5  = obj4.props.head.asInstanceOf[BuilderObj]
        val obj5a = obj5.copy(props = node :: obj5.props)
        val obj4a = obj4.copy(props = obj5a :: obj4.props.tail)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 6 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj3  = obj2.props.head.asInstanceOf[BuilderObj]
        val obj4  = obj3.props.head.asInstanceOf[BuilderObj]
        val obj5  = obj4.props.head.asInstanceOf[BuilderObj]
        val obj6  = obj5.props.head.asInstanceOf[BuilderObj]
        val obj6a = obj6.copy(props = node :: obj6.props)
        val obj5a = obj5.copy(props = obj6a :: obj5.props.tail)
        val obj4a = obj4.copy(props = obj5a :: obj4.props.tail)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 7 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj3  = obj2.props.head.asInstanceOf[BuilderObj]
        val obj4  = obj3.props.head.asInstanceOf[BuilderObj]
        val obj5  = obj4.props.head.asInstanceOf[BuilderObj]
        val obj6  = obj5.props.head.asInstanceOf[BuilderObj]
        val obj7  = obj6.props.head.asInstanceOf[BuilderObj]
        val obj7a = obj7.copy(props = node :: obj7.props)
        val obj6a = obj6.copy(props = obj7a :: obj6.props.tail)
        val obj5a = obj5.copy(props = obj6a :: obj5.props.tail)
        val obj4a = obj4.copy(props = obj5a :: obj4.props.tail)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail
    }
    obj.copy(props = newProps)
  }


  def addRef(obj: BuilderObj, refCls: String, refName: String, card: Int, objLevel: Int): BuilderObj = {
    val newProps = objLevel match {
      case 0 =>
        List(BuilderObj(refCls, refName, card, obj.props))

      case 1 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj1a = obj1.copy(cls = refCls, ref = refName, card = card)
        obj1a :: obj.props.tail

      case 2 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj2a = obj2.copy(cls = refCls, ref = refName, card = card)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 3 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj3  = obj2.props.head.asInstanceOf[BuilderObj]
        val obj3a = obj3.copy(cls = refCls, ref = refName, card = card)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 4 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj3  = obj2.props.head.asInstanceOf[BuilderObj]
        val obj4  = obj3.props.head.asInstanceOf[BuilderObj]
        val obj4a = obj4.copy(cls = refCls, ref = refName, card = card)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 5 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj3  = obj2.props.head.asInstanceOf[BuilderObj]
        val obj4  = obj3.props.head.asInstanceOf[BuilderObj]
        val obj5  = obj4.props.head.asInstanceOf[BuilderObj]
        val obj5a = obj5.copy(cls = refCls, ref = refName, card = card)
        val obj4a = obj4.copy(props = obj5a :: obj4.props.tail)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 6 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj3  = obj2.props.head.asInstanceOf[BuilderObj]
        val obj4  = obj3.props.head.asInstanceOf[BuilderObj]
        val obj5  = obj4.props.head.asInstanceOf[BuilderObj]
        val obj6  = obj5.props.head.asInstanceOf[BuilderObj]
        val obj6a = obj6.copy(cls = refCls, ref = refName, card = card)
        val obj5a = obj5.copy(props = obj6a :: obj5.props.tail)
        val obj4a = obj4.copy(props = obj5a :: obj4.props.tail)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 7 =>
        val obj1  = obj.props.head.asInstanceOf[BuilderObj]
        val obj2  = obj1.props.head.asInstanceOf[BuilderObj]
        val obj3  = obj2.props.head.asInstanceOf[BuilderObj]
        val obj4  = obj3.props.head.asInstanceOf[BuilderObj]
        val obj5  = obj4.props.head.asInstanceOf[BuilderObj]
        val obj6  = obj5.props.head.asInstanceOf[BuilderObj]
        val obj7  = obj6.props.head.asInstanceOf[BuilderObj]
        val obj7a = obj7.copy(cls = refCls, ref = refName, card = card)
        val obj6a = obj6.copy(props = obj7a :: obj6.props.tail)
        val obj5a = obj5.copy(props = obj6a :: obj5.props.tail)
        val obj4a = obj4.copy(props = obj5a :: obj4.props.tail)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail
    }
    obj.copy(props = newProps)
  }


  def hasSameNss(obj: BuilderObj): Boolean = {
    var dupNss: Set[String] = Set.empty
    def isDupe(ns: String) = if (dupNss(ns)) true else {
      dupNss += ns;
      false
    }
    obj.props.exists {
      case BuilderObj(ns, _, _, _) if isDupe(ns) => true
      case _                                     => false
    }
  }
}
