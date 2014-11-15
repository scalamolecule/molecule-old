package molecule.transform
import java.util.{List => jList}
import datomic.Connection
import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.transaction._
import molecule.util.Debug
import scala.collection.JavaConverters._


case class Model2Transaction(conn: Connection, model: Model, dataRows: Seq[Seq[Any]] = Seq(), ids: Seq[Long] = Seq()) extends DatomicFacade {
  val y = Debug("Model2Transaction", 3, 10, false, 6)


  def javaTx: (jList[jList[_]], Seq[Object]) = {
    val (stmts, tempIds) = tx
    (stmts.map(_.toJava).asJava, tempIds)
  }

  val stmtsModel = {
    val (_, _, _, stmts2) = model.elements.foldLeft('_, '_, '_, Seq[Statement]()) { case ((e, a, v, stmts), element) =>
      (e, a, v, element) match {
        case ('_, '_, '_, Atom(ns, name, _, _, VarValue, _))   => ('e, '_, '_, stmts :+ Add('id, s":$ns/$name", 'arg))
        case ('_, '_, '_, Atom(ns, name, _, _, value, prefix)) => ('e, '_, '_, stmts :+ Add('id, s":$ns/$name", Values(value, prefix)))
        case ('_, '_, '_, Bond(ns, refAttr, _))                => ('v, '_, '_, stmts :+ Add('id, s":$ns/$refAttr", 'id))

        case ('e, '_, '_, Atom(ns, name, _, _, VarValue, _))   => ('e, '_, '_, stmts :+ Add('e, s":$ns/$name", 'arg))
        case ('e, '_, '_, Atom(ns, name, _, _, value, prefix)) => ('e, '_, '_, stmts :+ Add('e, s":$ns/$name", Values(value, prefix)))
        case ('e, '_, '_, Bond(ns, refAttr, _))                => ('v, '_, '_, stmts :+ Add('e, s":$ns/$refAttr", 'id))

        case ('v, '_, '_, Atom(ns, name, _, _, VarValue, _))   => ('e, '_, '_, stmts :+ Add('v, s":$ns/$name", 'arg))
        case ('v, '_, '_, Atom(ns, name, _, _, value, prefix)) => ('e, '_, '_, stmts :+ Add('v, s":$ns/$name", Values(value, prefix)))
        case ('v, '_, '_, Bond(ns, refAttr, _))                => ('v, '_, '_, stmts :+ Add('v, s":$ns/$refAttr", 'id))
      }
    }
    stmts2
  }

  def add(stmts: Seq[Statement], e: Object, a: String, value: Any, prefix: Option[String] = None) = {
    def p(value: Any) = if (prefix.isDefined) prefix.get + value else value

    value match {
      case Replace(oldNew)      => oldNew.toSeq.flatMap {
        case (oldValue, newValue) => Seq(Retract(e, a, p(oldValue)), Add(e, a, p(newValue)))
      }
      case Remove(Seq())        => getValues1(conn.db, e, a).toSeq.map(v => Retract(e, a, p(v)))
      case Remove(removeValues) => removeValues.map(v => Retract(e, a, p(v)))
      case Eq(vs)               => stmts ++ vs.map(v => Add(e, a, p(v)))
      case vs: Set[_]           => stmts ++ vs.map(v => Add(e, a, p(v)))
      case v :: Nil             => stmts :+ Add(e, a, p(v))
      case vs: List[_]          => stmts ++ vs.map(v => Add(e, a, p(v)))
      case v                    => stmts :+ Add(e, a, p(v))
      //      case unexpected           => sys.error("[Model2Transaction:insertStmts] Unexpected statement: " + unexpected)
    }
  }

  def updateStmts = {

    //    dataRows.zip(ids).flatMap { case (args, id) =>
    val (_, stmts2) = stmtsModel.foldLeft(0, Seq[Statement]()) { case ((i, stmts), stmt) =>
      val j = i + 1
      //        val arg = args(i)
      stmt match {
        //            case Add('id, a, 'arg)                   => (j, add(stmts, tempId(), a, arg))
        case Add('id, a, Values(vs, prefix)) => (j, add(stmts, tempId(), a, vs, prefix))
        //            case Add('e, a, 'arg)                    => (j, add(stmts, stmts.last.e, a, arg))
        //            case Add('e, a, Values(EnumVal, prefix)) => (j, add(stmts, stmts.last.e, a, arg, prefix))
        case Add('e, a, Values(vs, prefix)) => (j, add(stmts, stmts.last.e, a, vs, prefix))
        case Add('e, a, 'id)                => (i, add(stmts, stmts.last.e, a, tempId()))
        //            case Add('v, a, 'arg)                    => (j, add(stmts, stmts.last.v.asInstanceOf[Object], a, arg))
        case Add('v, a, Values(vs, prefix)) => (j, add(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
        case Retract(e, a, v)               => (i, stmts)
        case unexpected                     => sys.error("[Model2Transaction:updateStmts] Unexpected statement: " + unexpected)
      }
    }
    stmts2
    //    }
  }


  def insertStmts(dataRows: Seq[Seq[Any]]) = dataRows.flatMap { args =>
    stmtsModel.foldLeft(0, Seq[Statement]()) { case ((i, stmts), stmt) =>
      val j = i + 1
      val arg = args(i)
      if (arg == null)
        (j, stmts)
      else
        stmt match {
          case Add('id, a, 'arg)                   => (j, add(stmts, tempId(), a, arg))
          case Add('id, a, Values(vs, prefix))     => (j, add(stmts, tempId(), a, vs, prefix))
          case Add('e, a, 'arg)                    => (j, add(stmts, stmts.last.e, a, arg))
          case Add('e, a, Values(EnumVal, prefix)) => (j, add(stmts, stmts.last.e, a, arg, prefix))
          case Add('e, a, Values(vs, prefix))      => (j, add(stmts, stmts.last.e, a, vs, prefix))
          case Add('e, a, 'id)                     => (i, add(stmts, stmts.last.e, a, tempId()))
          case Add('v, a, 'arg)                    => (j, add(stmts, stmts.last.v.asInstanceOf[Object], a, arg))
          case Add('v, a, Values(vs, prefix))      => (j, add(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
          case Retract(e, a, v)                    => (i, stmts)
          case unexpected                          => sys.error("[Model2Transaction:insertStmts] Unexpected statement: " + unexpected)
        }
    }._2
  }.map(_.toJava).asJava

  def saveStmts = {
    stmtsModel.foldLeft(0, Seq[Statement]()) { case ((i, stmts), stmt) =>
      val j = i + 1
      stmt match {
        case Add('id, a, Values(vs, prefix)) => (j, add(stmts, tempId(), a, vs, prefix))
        case Add('e, a, Values(vs, prefix))  => (j, add(stmts, stmts.last.e, a, vs, prefix))
        case Add('e, a, 'id)                 => (i, add(stmts, stmts.last.e, a, tempId()))
        case Add('v, a, Values(vs, prefix))  => (j, add(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
        case Retract(e, a, v)                => (i, stmts)
        case Add(_, a, 'arg)                 => sys.error(s"[Model2Transaction:insert] Attribute `$a` needs a value applied")
        case unexpected                      => sys.error("[Model2Transaction:insert] Unexpected statement: " + unexpected)
        //        case Retract(e, a, v) => (j, stmts :+ stmt)
        //        case Add(e, a, v)     => (j, stmts :+ stmt)
      }
    }._2.map(_.toJava).asJava
    //    Seq(Add('e, "attr", 'v))
  }

  def tx: (Seq[Statement], Seq[Object]) = {
    if (dataRows.isEmpty) {
      if (ids.isEmpty)
        mergeDataWithElements(model.elements)
      else if (ids.size == 1)
        mergeDataWithElements(model.elements, Seq(), ids.head)
      else
        sys.error("[Model2Transaction:tx] Unexpected ids: " + ids)
    } else {
      if (ids.isEmpty) {
        val (stmtss, tempIdss) = dataRows.map {
          dataRow =>
            mergeDataWithElements(model.elements, dataRow, 0L)
        }.unzip
        (stmtss.flatten, tempIdss.flatten.distinct)
      } else {
        assert(dataRows.size == ids.size)
        val (stmtss, tempIdss) = dataRows.zip(ids).map {
          case (dataRow, id: Long) =>
            mergeDataWithElements(model.elements, dataRow, id)
        }.unzip
        (stmtss.flatten, tempIdss.flatten.distinct)
      }
    }
  }

  private def mergeDataWithElements(elements: Seq[Element], dataRow: Seq[Any] = Seq(), id: Long = 0L): (Seq[Statement], Seq[Object]) = {
    // Start with last element of molecule and go backwards (to be able to reference nested entities)
    var n = dataRow.size
    val ((newStmts, tempIds), _, _) = elements.foldRight(((Seq[Statement](), Seq[Object]()), "": Object, "")) {
      case (element, ((stmts, tempIds), nextId, nextNs)) => {
        //        val data = if (element.isInstanceOf[Bond] || element.isInstanceOf[SubComponent] || dataRow.isEmpty)
        val data = if (element.isInstanceOf[Bond] || dataRow.isEmpty)
          0
        else {
          n -= 1
          dataRow(n)
        }

        if (data == null) {
          // When data is null, no fact is asserted (stmts passed unchanged)
          ((stmts, tempIds), null, null)
        } else {
          val (newStmts0, curId) = mkStatements(stmts, element, data, id, nextId, nextNs)
          ((newStmts0, tempIds :+ curId), curId, curNs(element))
        }
      }
    }
    (newStmts, tempIds)
  }

  private def mkStatements(stmts: Seq[Statement], element: Element, arg: Any, id0: Long, nextId: Object, nextNs: String): (Seq[Statement], Object) = {
    val e = if (curNs(element) == nextNs)
      nextId
    else if (id0 > 0L)
      id0.asInstanceOf[Object]
    else
      tempId()

    def addAtoms(ns: String, attr: String, prefix: Option[String] = None, value: Option[Any] = None): Seq[Statement] = {
      def p(value: Any) = if (prefix.isDefined) prefix.get + value else value

      // Atom value takes precedence over external argument
      value.getOrElse(arg) match {
        case Replace(oldNew)            => y(11);
          oldNew.toSeq.flatMap {
            case (oldValue, newValue) =>
              Seq(Retract(e, s":$ns/$attr", p(oldValue)), Add(e, s":$ns/$attr", p(newValue)))
          }
        case Remove(Seq())              => y(12, getValues(conn.db, e, ns, attr).toSeq.map(v => Retract(e, s":$ns/$attr", p(v))));
          getValues(conn.db, e, ns, attr).toSeq.map(v => Retract(e, s":$ns/$attr", p(v)))
        case Remove(removeValues)       => y(13, removeValues, removeValues.map(v => Retract(e, s":$ns/$attr", p(v))));
          removeValues.map(v => Retract(e, s":$ns/$attr", p(v)))
        case vs: Set[_]                 => y(14, vs, vs.toSeq.map(v => Add(e, s":$ns/$attr", p(v))));
          vs.toSeq.map(v => Add(e, s":$ns/$attr", p(v)))
        case vs: List[_] if vs.size > 1 => y(15, vs, vs.map(v => Add(e, s":$ns/$attr", p(v))));
          vs.map(v => Add(e, s":$ns/$attr", p(v)))
        case v :: Nil                   => y(16, v, Seq(Add(e, s":$ns/$attr", p(v))));
          Seq(Add(e, s":$ns/$attr", p(v)))
        case v                          => y(17, v, Seq(Add(e, s":$ns/$attr", p(v))));
          Seq(Add(e, s":$ns/$attr", p(v)))
      }
    }

    val newStmts = element match {
      case Atom(ns, attr, _, _, VarValue, _)                => y(1, s":$ns/$attr");
        stmts ++ addAtoms(ns, attr)
      case Atom(ns, attr, _, _, EnumVal, prefix)            => stmts ++ addAtoms(ns, attr, prefix)
      case Atom(ns, attr, _, _, Eq(values), prefix)         => stmts ++ addAtoms(ns, attr, prefix, Some(values))
      case Atom(ns, attr, _, _, replace@Replace(_), prefix) => y(4, stmts, addAtoms(ns, attr, prefix, Some(replace)));
        stmts ++ addAtoms(ns, attr, prefix, Some(replace))
      case Atom(ns, attr, _, _, remove@Remove(_), prefix)   => y(5, stmts, addAtoms(ns, attr, prefix, Some(remove)), stmts ++ addAtoms(ns, attr, prefix, Some(remove)));
        stmts ++ addAtoms(ns, attr, prefix, Some(remove))

      case Bond(ns, refAttr, refNs) => y(2, s":$ns/$refAttr $nextId");
        stmts :+ Add(e, s":$ns/$refAttr", nextId)

      case Group(Bond(ns, refAttr, refNs), nestedElements) => {
        val nestedDataRows = nestedData(nestedElements, arg)

        // Loop nested rows of data
        val (elementStmts, elementIds) = nestedDataRows.foldLeft((stmts, Set[Object]())) {
          case ((elementStmts1, nestedIds), nestedDataRow) =>
            // Recursively create nested elements
            val (rowStmts, _) = mergeDataWithElements(nestedElements, nestedDataRow)
            val lastId = rowStmts.last.e
            (elementStmts1 ++ rowStmts, nestedIds + lastId)
        }

        // Add references to nested entities
        val refStmts = elementIds.map(Add(e, s":$ns/$refAttr", _))
        elementStmts ++ refStmts
      }


      case Meta(ns, _, _, _, EntValue) => stmts //++ add(ns, attr, None, Some(e))

      case unexpected => sys.error("[Model2Transaction:mkStatements] Unexpected molecule element: " + unexpected)
    }
    (newStmts, e)
  }

  private def nestedData(elements: Seq[Element], data0: Any) = {
    val (dataArity, data) = data0 match {
      case d: Seq[_]  => d.head match {
        case p: Product => (p.productArity, d)
        case l: Seq[_]  => (l.size, d)
      }
      case unexpected => sys.error("[Model2Transaction:nestedData] Unexpected data: " + unexpected)
    }
    assert(dataArity == elements.size, s"[Model2Transaction:nestedData] Arity of attributes and values should match. Found: \n" +
      s"Elements (arity ${
        elements.size
      }): " + elements.mkString("\n  ", "\n  ", "\n") +
      s"Data (arity $dataArity): " + data.mkString("\n  ", "\n  ", "\n"))

    // Todo: can we convert tuples more elegantly?
    data map {
      case t: Tuple1[_]                                                          => Seq(t._1)
      case t: (_, _)                                                             => Seq(t._1, t._2)
      case t: (_, _, _)                                                          => Seq(t._1, t._2, t._3)
      case t: (_, _, _, _)                                                       => Seq(t._1, t._2, t._3, t._4)
      case t: (_, _, _, _, _)                                                    => Seq(t._1, t._2, t._3, t._4, t._5)
      case t: (_, _, _, _, _, _)                                                 => Seq(t._1, t._2, t._3, t._4, t._5, t._6)
      case t: (_, _, _, _, _, _, _)                                              => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7)
      case t: (_, _, _, _, _, _, _, _)                                           => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)
      case t: (_, _, _, _, _, _, _, _, _)                                        => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)
      case t: (_, _, _, _, _, _, _, _, _, _)                                     => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10)
      case t: (_, _, _, _, _, _, _, _, _, _, _)                                  => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _)                               => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _)                            => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _)                         => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _)                      => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)                   => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)                => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)             => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)          => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)       => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)    => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20, t._21)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20, t._21, t._22)
      case l: Seq[_]                                                             => l
      case unexpected                                                            =>
        sys.error("[Model2Transaction:mkStatements] Unexpected nested data: " + unexpected)
    }
  }
}