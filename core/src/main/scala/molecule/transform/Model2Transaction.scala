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
        val (stmtss, tempIdss) = (dataRows.zip(ids) map { case (dataRow, e: Long) =>
          mergeDataWithElements(model.elements, dataRow, e)
        }).unzip
        (stmtss.flatten, tempIdss.flatten.distinct)
      }
    }
  }

  def mergeDataWithElements(elements: Seq[Element], dataRow: Seq[Any] = Seq(), e: Long = 0L): (Seq[Statement], Seq[Object]) = {
    // Start with last element of molecule and go backwards (to be able to reference nested entities)
    var n = dataRow.size
    val ((newStmts, tempIds), _, _) = elements.foldRight(((Seq[Statement](), Seq[Object]()), "": Object, "")) {
      case (element, ((stmts, tempIds), prevId, prevNs)) => {
        val data = if (element.isInstanceOf[Bond] || element.isInstanceOf[SubComponent] || dataRow.isEmpty)
          0
        else {
          n -= 1
          dataRow(n)
        }

        if (data == null) {
          // When data is null, no fact is asserted (stmts passed unchanged)
          ((stmts, tempIds), null, null)
        } else {
          val (newStmts0, curId) = mkStatements(stmts, element, data, e, prevId, prevNs)
          ((newStmts0, tempIds :+ curId), curId, curNs(element))
        }
      }
    }
    (newStmts, tempIds)
  }

  def mkStatements(stmts: Seq[Statement], element: Element, arg: Any, e0: Long, prevId: Object, prevNs: String): (Seq[Statement], Object) = {
    val e = if (curNs(element) == prevNs) prevId else if (e0 > 0L) e0.asInstanceOf[Object] else tempId()

    def add(ns: String, attr: String, prefix: Option[String] = None, value: Option[Any] = None): Seq[Statement] = {
      def p(value: Any) = if (prefix.isDefined) prefix.get + value else value

      // Atom value takes precedence over external argument
      value.getOrElse(arg) match {
        case Replace(oldNew)            => oldNew.toSeq.flatMap { case (oldValue, newValue) =>
          Seq(Retract(e, s":$ns/$attr", p(oldValue)), Add(e, s":$ns/$attr", p(newValue)))
        }
        case Remove(Seq())              => getValues(conn.db, e, ns, attr).toSeq.map(v => Retract(e, s":$ns/$attr", p(v)))
        case Remove(removeValues)       => removeValues.map(v => Retract(e, s":$ns/$attr", p(v)))
        case vs: Set[_]                 => vs.toSeq.map(v => Add(e, s":$ns/$attr", p(v)))
        case vs: List[_] if vs.size > 1 => vs.map(v => Add(e, s":$ns/$attr", p(v)))
        case v :: Nil                   => Seq(Add(e, s":$ns/$attr", p(v)))
        case v                          => Seq(Add(e, s":$ns/$attr", p(v)))
      }
    }

    val newStmts = element match {
      case Atom(ns, attr, _, _, EntValue, _)                => stmts ++ add(ns, attr, None, Some(e))
      case Atom(ns, attr, _, _, VarValue, _)                => stmts ++ add(ns, attr)
      case Atom(ns, attr, _, _, EnumVal, prefix)            => stmts ++ add(ns, attr, prefix)
      case Atom(ns, attr, _, _, Eq(values), prefix)         => stmts ++ add(ns, attr, prefix, Some(values))
      case Atom(ns, attr, _, _, replace@Replace(_), prefix) => stmts ++ add(ns, attr, prefix, Some(replace))
      case Atom(ns, attr, _, _, remove@Remove(_), prefix)   => stmts ++ add(ns, attr, prefix, Some(remove))

      case Bond(ns, refAttr, refNs) => stmts :+ Add(e, s":$ns/$refAttr", prevId)

      case SubComponent(ns, parentEid) => stmts :+ Add(parentEid.asInstanceOf[Object], s":$ns/sub_", e)

//      case Group(Bond(ns, refAttr, refNs), nestedElements) => {
//        val nestedDataRows = nestedData(nestedElements, arg)
//
//        // Loop nested rows of data
//        val (elementStmts, elementIds) = nestedDataRows.foldLeft((stmts, Set[Object]())) { case ((elementStmts1, nestedIds), nestedDataRow) =>
//          // Recursively create nested elements
//          val (rowStmts, _) = mergeDataWithElements(nestedElements, nestedDataRow)
//          val lastId = rowStmts.last.e
//          (elementStmts1 ++ rowStmts, nestedIds + lastId)
//        }
//
//        // Add references to nested entities
//        val refStmts = elementIds.map(Add(e, s":$ns/$refAttr", _))
//        elementStmts ++ refStmts
//      }

      case unexpected => sys.error("[Model2Transaction:mkStatements] Unexpected molecule element: " + unexpected)
    }
    (newStmts, e)
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
      case l: Seq[_]                                                                    => l
      case unexpected                                                                   =>
        sys.error("[Model2Transaction:mkStatements] Unexpected nested data: " + unexpected)
    }
  }
}