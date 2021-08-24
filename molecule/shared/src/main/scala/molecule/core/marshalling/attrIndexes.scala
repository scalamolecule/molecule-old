package molecule.core.marshalling

import boopickle.Default._

object attrIndexes {

  sealed trait IndexNode

  case class AttrIndex(
    cls: String,
    attr: String,
    lambdaIndex: Int,
    post: Boolean,
  ) extends IndexNode {
    override def toString: String = {
      s"""AttrIndex("$cls", "$attr", $lambdaIndex, $post)"""
    }
  }

  case class Indexes(
    cls: String,
    ref: String,
    nested: Boolean,
    attrs: List[IndexNode]
  ) extends IndexNode {
    override def toString: String = {
      def draw(attrs: Seq[IndexNode], indent: Int): Seq[String] = {
        val s = "  " * indent
        attrs map {
          case Indexes(cls, ref, nested, Nil) =>
            s"""|${s}Indexes("$cls", "$ref", $nested, Nil)""".stripMargin
          case Indexes(cls, ref, nested, attrs) =>
            s"""|${s}Indexes("$cls", "$ref", $nested, List(
                |${draw(attrs, indent + 1).mkString(s",\n")}))""".stripMargin

          case attrIndex => s + attrIndex
        }
      }
      draw(Seq(this), 0).head
    }
  }

  object IndexNode {
    implicit val indexesPickler = compositePickler[IndexNode]
    indexesPickler
      .addConcreteType[AttrIndex]
      .addConcreteType[Indexes]
  }

  def addAttrIndex(indexes: Indexes, attr: IndexNode, level: Int): Indexes = {
    val newattrs = level match {
      case 0 =>
        attr :: indexes.attrs

      case 1 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes1a = indexes1.copy(attrs = attr :: indexes1.attrs)
        indexes1a :: indexes.attrs.tail

      case 2 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes2a = indexes2.copy(attrs = attr :: indexes2.attrs)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail

      case 3 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes3  = indexes2.attrs.head.asInstanceOf[Indexes]
        val indexes3a = indexes3.copy(attrs = attr :: indexes3.attrs)
        val indexes2a = indexes2.copy(attrs = indexes3a :: indexes2.attrs.tail)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail

      case 4 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes3  = indexes2.attrs.head.asInstanceOf[Indexes]
        val indexes4  = indexes3.attrs.head.asInstanceOf[Indexes]
        val indexes4a = indexes4.copy(attrs = attr :: indexes4.attrs)
        val indexes3a = indexes3.copy(attrs = indexes4a :: indexes3.attrs.tail)
        val indexes2a = indexes2.copy(attrs = indexes3a :: indexes2.attrs.tail)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail

      case 5 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes3  = indexes2.attrs.head.asInstanceOf[Indexes]
        val indexes4  = indexes3.attrs.head.asInstanceOf[Indexes]
        val indexes5  = indexes4.attrs.head.asInstanceOf[Indexes]
        val indexes5a = indexes5.copy(attrs = attr :: indexes5.attrs)
        val indexes4a = indexes4.copy(attrs = indexes5a :: indexes4.attrs.tail)
        val indexes3a = indexes3.copy(attrs = indexes4a :: indexes3.attrs.tail)
        val indexes2a = indexes2.copy(attrs = indexes3a :: indexes2.attrs.tail)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail

      case 6 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes3  = indexes2.attrs.head.asInstanceOf[Indexes]
        val indexes4  = indexes3.attrs.head.asInstanceOf[Indexes]
        val indexes5  = indexes4.attrs.head.asInstanceOf[Indexes]
        val indexes6  = indexes5.attrs.head.asInstanceOf[Indexes]
        val indexes6a = indexes6.copy(attrs = attr :: indexes6.attrs)
        val indexes5a = indexes5.copy(attrs = indexes6a :: indexes5.attrs.tail)
        val indexes4a = indexes4.copy(attrs = indexes5a :: indexes4.attrs.tail)
        val indexes3a = indexes3.copy(attrs = indexes4a :: indexes3.attrs.tail)
        val indexes2a = indexes2.copy(attrs = indexes3a :: indexes2.attrs.tail)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail

      case 7 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes3  = indexes2.attrs.head.asInstanceOf[Indexes]
        val indexes4  = indexes3.attrs.head.asInstanceOf[Indexes]
        val indexes5  = indexes4.attrs.head.asInstanceOf[Indexes]
        val indexes6  = indexes5.attrs.head.asInstanceOf[Indexes]
        val indexes7  = indexes6.attrs.head.asInstanceOf[Indexes]
        val indexes7a = indexes7.copy(attrs = attr :: indexes7.attrs)
        val indexes6a = indexes6.copy(attrs = indexes7a :: indexes6.attrs.tail)
        val indexes5a = indexes5.copy(attrs = indexes6a :: indexes5.attrs.tail)
        val indexes4a = indexes4.copy(attrs = indexes5a :: indexes4.attrs.tail)
        val indexes3a = indexes3.copy(attrs = indexes4a :: indexes3.attrs.tail)
        val indexes2a = indexes2.copy(attrs = indexes3a :: indexes2.attrs.tail)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail
    }
    indexes.copy(attrs = newattrs)
  }


  def addIndexes(indexes: Indexes, cls: String, ref: String, nested: Boolean, level: Int): Indexes = {
    val newAttrs = level match {
      case 0 =>
        List(Indexes(cls, ref, nested, indexes.attrs))

      case 1 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes1a = indexes1.copy(cls = cls, ref = ref, nested = nested)
        indexes1a :: indexes.attrs.tail

      case 2 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes2a = indexes2.copy(cls = cls, ref = ref, nested = nested)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail

      case 3 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes3  = indexes2.attrs.head.asInstanceOf[Indexes]
        val indexes3a = indexes3.copy(cls = cls, ref = ref, nested = nested)
        val indexes2a = indexes2.copy(attrs = indexes3a :: indexes2.attrs.tail)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail

      case 4 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes3  = indexes2.attrs.head.asInstanceOf[Indexes]
        val indexes4  = indexes3.attrs.head.asInstanceOf[Indexes]
        val indexes4a = indexes4.copy(cls = cls, ref = ref, nested = nested)
        val indexes3a = indexes3.copy(attrs = indexes4a :: indexes3.attrs.tail)
        val indexes2a = indexes2.copy(attrs = indexes3a :: indexes2.attrs.tail)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail

      case 5 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes3  = indexes2.attrs.head.asInstanceOf[Indexes]
        val indexes4  = indexes3.attrs.head.asInstanceOf[Indexes]
        val indexes5  = indexes4.attrs.head.asInstanceOf[Indexes]
        val indexes5a = indexes5.copy(cls = cls, ref = ref, nested = nested)
        val indexes4a = indexes4.copy(attrs = indexes5a :: indexes4.attrs.tail)
        val indexes3a = indexes3.copy(attrs = indexes4a :: indexes3.attrs.tail)
        val indexes2a = indexes2.copy(attrs = indexes3a :: indexes2.attrs.tail)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail

      case 6 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes3  = indexes2.attrs.head.asInstanceOf[Indexes]
        val indexes4  = indexes3.attrs.head.asInstanceOf[Indexes]
        val indexes5  = indexes4.attrs.head.asInstanceOf[Indexes]
        val indexes6  = indexes5.attrs.head.asInstanceOf[Indexes]
        val indexes6a = indexes6.copy(cls = cls, ref = ref, nested = nested)
        val indexes5a = indexes5.copy(attrs = indexes6a :: indexes5.attrs.tail)
        val indexes4a = indexes4.copy(attrs = indexes5a :: indexes4.attrs.tail)
        val indexes3a = indexes3.copy(attrs = indexes4a :: indexes3.attrs.tail)
        val indexes2a = indexes2.copy(attrs = indexes3a :: indexes2.attrs.tail)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail

      case 7 =>
        val indexes1  = indexes.attrs.head.asInstanceOf[Indexes]
        val indexes2  = indexes1.attrs.head.asInstanceOf[Indexes]
        val indexes3  = indexes2.attrs.head.asInstanceOf[Indexes]
        val indexes4  = indexes3.attrs.head.asInstanceOf[Indexes]
        val indexes5  = indexes4.attrs.head.asInstanceOf[Indexes]
        val indexes6  = indexes5.attrs.head.asInstanceOf[Indexes]
        val indexes7  = indexes6.attrs.head.asInstanceOf[Indexes]
        val indexes7a = indexes7.copy(cls = cls, ref = ref, nested = nested)
        val indexes6a = indexes6.copy(attrs = indexes7a :: indexes6.attrs.tail)
        val indexes5a = indexes5.copy(attrs = indexes6a :: indexes5.attrs.tail)
        val indexes4a = indexes4.copy(attrs = indexes5a :: indexes4.attrs.tail)
        val indexes3a = indexes3.copy(attrs = indexes4a :: indexes3.attrs.tail)
        val indexes2a = indexes2.copy(attrs = indexes3a :: indexes2.attrs.tail)
        val indexes1a = indexes1.copy(attrs = indexes2a :: indexes1.attrs.tail)
        indexes1a :: indexes.attrs.tail
    }
    indexes.copy(attrs = newAttrs)
  }
}



