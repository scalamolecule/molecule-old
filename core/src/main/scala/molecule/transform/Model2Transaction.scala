package molecule.transform
import datomic.Connection
import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.transaction._
import molecule.util.Debug
import scala.collection.JavaConverters._
import java.util.{List => jList}

case class Model2Transaction(conn: Connection, model: Model, dataRows: Seq[Seq[Any]] = Seq(), ids: Seq[Long] = Seq()) extends Debug with DatomicFacade {
  val y = debug("Model2Transaction", 3, 99, false, 2)

  def javaTx: (jList[jList[_]], Seq[Object]) = {
    val (stmts, tempIds) = tx
    (stmts.map(_.toJava).asJava, tempIds)
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
        val (stmtss, tempIdss) = (dataRows map { dataRow =>
          mergeDataWithElements(model.elements, dataRow, 0L)
        }).unzip
        (stmtss.flatten, tempIdss.flatten.distinct)
      } else {
        assert(dataRows.size == ids.size)
        val (stmtss, tempIdss) = (dataRows.zip(ids) map { case (dataRow, eid: Long) =>
          mergeDataWithElements(model.elements, dataRow, eid)
        }).unzip
        (stmtss.flatten, tempIdss.flatten.distinct)
      }
    }
  }

  def mergeDataWithElements(elements: Seq[Element], dataRow: Seq[Any] = Seq(), eid: Long = 0L): (Seq[Statement], Seq[Object]) = {
    // Start with last element of molecule and go backwards (to be able to reference nested entities)
    var n = dataRow.size
    val ((newStmts, tempIds), _, _) = elements.foldRight(((Seq[Statement](), Seq[Object]()), "": Object, "")) {
      case (element, ((stmts, tempIds), prevId, prevNs)) => {
        val data = if (element.isInstanceOf[Bond] || element.isInstanceOf[Node] || dataRow.isEmpty)
          0
        else {
          n -= 1
          dataRow(n)
        }

        if (data == null) {
          // When data is null, no fact is asserted (stmts passed unchanged)
          ((stmts, tempIds), null, null)
        } else {
          val (newStmts0, curId) = mkStatements(stmts, element, data, eid, prevId, prevNs)
          ((newStmts0, tempIds :+ curId), curId, curNs(element))
        }
      }
    }
    (newStmts, tempIds)
  }

  def mkStatements(stmts: Seq[Statement], element: Element, arg: Any, eid0: Long, prevId: Object, prevNs: String): (Seq[Statement], Object) = {
    val eid = if (curNs(element) == prevNs) prevId else if (eid0 > 0L) eid0.asInstanceOf[Object] else tempId()

    def add(ns: String, attr: String, prefix: Option[String] = None, value: Option[Any] = None): Seq[Statement] = {
      def p(value: Any) = if (prefix.isDefined) prefix.get + value else value

      // Atom value takes precedence over external argument
      value.getOrElse(arg) match {
        case Replace(oldNew)            => oldNew.toSeq.flatMap { case (oldValue, newValue) =>
          Seq(Retract(eid, s":$ns/$attr", p(oldValue)), Add(eid, s":$ns/$attr", p(newValue)))
        }
        case Remove(Seq())              => getValues(conn.db, eid, ns, attr).toSeq.map(v => Retract(eid, s":$ns/$attr", p(v)))
        case Remove(removeValues)       => removeValues.map(v => Retract(eid, s":$ns/$attr", p(v)))
        case vs: Set[_]                 => vs.toSeq.map(v => Add(eid, s":$ns/$attr", p(v)))
        case vs: List[_] if vs.size > 1 => vs.map(v => Add(eid, s":$ns/$attr", p(v)))
        case v :: Nil                   => Seq(Add(eid, s":$ns/$attr", p(v)))
        case v                          => Seq(Add(eid, s":$ns/$attr", p(v)))
      }
    }

    val newStmts = element match {
      case Atom(ns, attr, _, _, EntValue, _)                => stmts ++ add(ns, attr, None, Some(eid))
      case Atom(ns, attr, _, _, VarValue, _)                => stmts ++ add(ns, attr)
      case Atom(ns, attr, _, _, EnumVal, prefix)            => stmts ++ add(ns, attr, prefix)
      case Atom(ns, attr, _, _, Eq(values), prefix)         => stmts ++ add(ns, attr, prefix, Some(values))
      case Atom(ns, attr, _, _, replace@Replace(_), prefix) => stmts ++ add(ns, attr, prefix, Some(replace))
      case Atom(ns, attr, _, _, remove@Remove(_), prefix)   => stmts ++ add(ns, attr, prefix, Some(remove))

      case Bond(ns, refAttr, refNs) => stmts :+ Add(eid, s":$ns/$refAttr", prevId)

      case Node(ns, otherEid) => stmts :+ Add(otherEid.asInstanceOf[Object], s":$ns/tree_", eid)

      case Group(Bond(ns, refAttr, refNs), nestedElements) => {
        val nestedDataRows = nestedData(nestedElements, arg)

        // Loop nested rows of data
        val (elementStmts, elementIds) = nestedDataRows.foldLeft((stmts, Set[Object]())) { case ((elementStmts1, nestedIds), nestedDataRow) =>
          // Recursively create nested elements
          val (rowStmts, tempIds) = mergeDataWithElements(nestedElements, nestedDataRow)
          val lastId = rowStmts.last.e
          (elementStmts1 ++ rowStmts, nestedIds + lastId)
        }

        // Add references to nested entities
        val refStmts = elementIds.map(Add(eid, s":$ns/$refAttr", _))
        elementStmts ++ refStmts
      }

      case unexpected => sys.error("[Model2Transaction:mkStatements] Unexpected molecule element: " + unexpected)
    }
    (newStmts, eid)
  }

  def nestedData(elements: Seq[Element], data0: Any) = {
    val (dataArity, data) = data0 match {
      case d: Seq[_]  => d.head match {
        case p: Product => (p.productArity, d)
        case l: Seq[_]  => (l.size, d)
      }
      case unexpected => sys.error("[Model2Transaction:nestedData] Unexpected data: " + unexpected)
    }
    assert(dataArity == elements.size, s"[Model2Transaction:nestedData] Arity of attributes and values should match. Found: \n" +
      s"Elements (arity ${elements.size}): " + elements.mkString("\n  ", "\n  ", "\n") +
      s"Data (arity $dataArity): " + data.mkString("\n  ", "\n  ", "\n"))

    // Todo: can we convert tuples more elegantly?
    data map {
      case t: Tuple1[_]                                                                 => Seq(t._1)
      case t: Tuple2[_, _]                                                              => Seq(t._1, t._2)
      case t: Tuple3[_, _, _]                                                           => Seq(t._1, t._2, t._3)
      case t: Tuple4[_, _, _, _]                                                        => Seq(t._1, t._2, t._3, t._4)
      case t: Tuple5[_, _, _, _, _]                                                     => Seq(t._1, t._2, t._3, t._4, t._5)
      case t: Tuple6[_, _, _, _, _, _]                                                  => Seq(t._1, t._2, t._3, t._4, t._5, t._6)
      case t: Tuple7[_, _, _, _, _, _, _]                                               => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7)
      case t: Tuple8[_, _, _, _, _, _, _, _]                                            => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)
      case t: Tuple9[_, _, _, _, _, _, _, _, _]                                         => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)
      case t: Tuple10[_, _, _, _, _, _, _, _, _, _]                                     => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10)
      case t: Tuple11[_, _, _, _, _, _, _, _, _, _, _]                                  => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11)
      case t: Tuple12[_, _, _, _, _, _, _, _, _, _, _, _]                               => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12)
      case t: Tuple13[_, _, _, _, _, _, _, _, _, _, _, _, _]                            => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13)
      case t: Tuple14[_, _, _, _, _, _, _, _, _, _, _, _, _, _]                         => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14)
      case t: Tuple15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _]                      => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15)
      case t: Tuple16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]                   => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16)
      case t: Tuple17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]                => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17)
      case t: Tuple18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]             => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18)
      case t: Tuple19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]          => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19)
      case t: Tuple20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]       => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20)
      case t: Tuple21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]    => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20, t._21)
      case t: Tuple22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _] => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20, t._21, t._22)
      case l: Seq[_]                                                                    => l
      case unexpected                                                                   =>
        sys.error("[Model2Transaction:mkStatements] Unexpected nested data: " + unexpected)
    }
  }
}