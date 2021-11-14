package molecule.core.marshalling

import boopickle.Default._
import scala.annotation.tailrec


object nodes {

  sealed trait Node

  object Node {
    implicit val nodesPickler = compositePickler[Node]
    nodesPickler
      .addConcreteType[Prop]
      .addConcreteType[Obj]
  }

  case class Prop(
    cls: String,
    prop: String,
    baseTpe: String,
    card: Int,
    group: String,
    optAggrTpe: Option[String] = None
  ) extends Node {
    override def toString: String = {
      s"""Prop("$cls", "$prop", "$baseTpe", $card, "$group", $optAggrTpe)"""
    }
  }

  case class Obj(
    cls: String,
    ref: String,
    nested: Boolean,
    props: List[Node]
  ) extends Node {
    override def toString: String = {
      def draw(nodes: Seq[Node], indent: Int): Seq[String] = {
        val s = "  " * indent
        nodes map {
          case Obj(cls, ref, nested, Nil)   =>
            s"""|${s}Obj("$cls", "$ref", $nested, Nil)""".stripMargin
          case Obj(cls, ref, nested, props) =>
            s"""|${s}Obj("$cls", "$ref", $nested, List(
                |${draw(props, indent + 1).mkString(s",\n")}))""".stripMargin
          case prop                         => s + prop
        }
      }
      draw(Seq(this), 0).head
    }
  }

  def isDeeper(obj: Obj) = {
    @tailrec
    def test(props: Seq[Node]): Boolean = {
      props.last match {
        case Obj(_, _, true, _) => true
        case Obj(_, _, _, refs) => test(refs)
        case _                  => false
      }
    }
    test(obj.props)
  }

  def getPropCount(nodes: List[Node]): Int = nodes.foldLeft(0) {
    case (acc, _: Prop)                    => acc + 1
    case (acc, Obj(_, _, false, refProps)) => acc + getPropCount(refProps)
    case (acc, nested: Obj)                => acc + 1 // nested counting as 1 prop
  }


  def addNode(obj: Obj, node: Node, level: Int): Obj = {
    val newProps = level match {
      case 0 =>
        node :: obj.props

      case 1 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj1a = obj1.copy(props = node :: obj1.props)
        obj1a :: obj.props.tail

      case 2 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj2a = obj2.copy(props = node :: obj2.props)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 3 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj3  = obj2.props.head.asInstanceOf[Obj]
        val obj3a = obj3.copy(props = node :: obj3.props)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 4 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj3  = obj2.props.head.asInstanceOf[Obj]
        val obj4  = obj3.props.head.asInstanceOf[Obj]
        val obj4a = obj4.copy(props = node :: obj4.props)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 5 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj3  = obj2.props.head.asInstanceOf[Obj]
        val obj4  = obj3.props.head.asInstanceOf[Obj]
        val obj5  = obj4.props.head.asInstanceOf[Obj]
        val obj5a = obj5.copy(props = node :: obj5.props)
        val obj4a = obj4.copy(props = obj5a :: obj4.props.tail)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 6 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj3  = obj2.props.head.asInstanceOf[Obj]
        val obj4  = obj3.props.head.asInstanceOf[Obj]
        val obj5  = obj4.props.head.asInstanceOf[Obj]
        val obj6  = obj5.props.head.asInstanceOf[Obj]
        val obj6a = obj6.copy(props = node :: obj6.props)
        val obj5a = obj5.copy(props = obj6a :: obj5.props.tail)
        val obj4a = obj4.copy(props = obj5a :: obj4.props.tail)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 7 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj3  = obj2.props.head.asInstanceOf[Obj]
        val obj4  = obj3.props.head.asInstanceOf[Obj]
        val obj5  = obj4.props.head.asInstanceOf[Obj]
        val obj6  = obj5.props.head.asInstanceOf[Obj]
        val obj7  = obj6.props.head.asInstanceOf[Obj]
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


  def addRef(obj: Obj, refCls: String, refName: String, nested: Boolean, level: Int): Obj = {
    val newProps = level match {
      case 0 =>
        List(Obj(refCls, refName, nested, obj.props))

      case 1 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj1a = obj1.copy(cls = refCls, ref = refName, nested = nested)
        obj1a :: obj.props.tail

      case 2 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj2a = obj2.copy(cls = refCls, ref = refName, nested = nested)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 3 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj3  = obj2.props.head.asInstanceOf[Obj]
        val obj3a = obj3.copy(cls = refCls, ref = refName, nested = nested)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 4 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj3  = obj2.props.head.asInstanceOf[Obj]
        val obj4  = obj3.props.head.asInstanceOf[Obj]
        val obj4a = obj4.copy(cls = refCls, ref = refName, nested = nested)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 5 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj3  = obj2.props.head.asInstanceOf[Obj]
        val obj4  = obj3.props.head.asInstanceOf[Obj]
        val obj5  = obj4.props.head.asInstanceOf[Obj]
        val obj5a = obj5.copy(cls = refCls, ref = refName, nested = nested)
        val obj4a = obj4.copy(props = obj5a :: obj4.props.tail)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 6 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj3  = obj2.props.head.asInstanceOf[Obj]
        val obj4  = obj3.props.head.asInstanceOf[Obj]
        val obj5  = obj4.props.head.asInstanceOf[Obj]
        val obj6  = obj5.props.head.asInstanceOf[Obj]
        val obj6a = obj6.copy(cls = refCls, ref = refName, nested = nested)
        val obj5a = obj5.copy(props = obj6a :: obj5.props.tail)
        val obj4a = obj4.copy(props = obj5a :: obj4.props.tail)
        val obj3a = obj3.copy(props = obj4a :: obj3.props.tail)
        val obj2a = obj2.copy(props = obj3a :: obj2.props.tail)
        val obj1a = obj1.copy(props = obj2a :: obj1.props.tail)
        obj1a :: obj.props.tail

      case 7 =>
        val obj1  = obj.props.head.asInstanceOf[Obj]
        val obj2  = obj1.props.head.asInstanceOf[Obj]
        val obj3  = obj2.props.head.asInstanceOf[Obj]
        val obj4  = obj3.props.head.asInstanceOf[Obj]
        val obj5  = obj4.props.head.asInstanceOf[Obj]
        val obj6  = obj5.props.head.asInstanceOf[Obj]
        val obj7  = obj6.props.head.asInstanceOf[Obj]
        val obj7a = obj7.copy(cls = refCls, ref = refName, nested = nested)
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


  def hasSameNss(obj: Obj): Boolean = {
    var dupNss: Set[String] = Set.empty
    def isDupe(ns: String) = if (dupNss(ns)) true else {
      dupNss += ns;
      false
    }
    obj.props.exists {
      case Obj(ns, _, _, _) if isDupe(ns) => true
      case _                              => false
    }
  }
}
