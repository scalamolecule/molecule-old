package molecule.transform
import java.util.{List => jList}
import datomic.{Connection, Database, Peer}
//import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.transaction._
import molecule.util.Debug
import scala.collection.JavaConversions._


//case class Model2Transaction(conn: Connection, model: Model, dataRows: Seq[Seq[Any]] = Seq(), ids: Seq[Long] = Seq()) extends DatomicFacade {
//case class Model2Transaction(conn: Connection, model: Model) extends DatomicFacade {
case class Model2Transaction(conn: Connection, model: Model) {
  val x = Debug("Model2Transaction", 24, 30, false, 6)

  def tempId(partition: String = "user") = Peer.tempid(s":db.part/$partition")

  def getValues1(db: Database, id: Any, attr: String) = {
    val query = s"[:find ?values :in $$ ?id :where [?id $attr ?values]]"
    //    x(25, query, id.asInstanceOf[Object], Peer.q(query, db, id.asInstanceOf[Object]))
    Peer.q(query, db, id.asInstanceOf[Object]).map(_.get(0))
  }
  val stmtsModel: Seq[Statement] = {
    //    x(24, model)

    val (_, _, _, stmts2) = model.elements.foldLeft('_: Any, '_, '_, Seq[Statement]()) { case ((e, a, v, stmts), element) =>
      (e, a, v, element) match {
        case ('_, '_, '_, Meta(ns, "", "e", EntValue))          => ('arg, '_, '_, stmts)
        case ('_, '_, '_, Meta(ns, "", "e", Eq(Seq(id: Long)))) => (Eid(id), '_, '_, stmts)

        case (Eid(id), '_, '_, Atom(ns, name, _, _, value@Remove(_), prefix)) => ('e, '_, '_, stmts :+ Retract(id, s":$ns/$name", Values(value, prefix)))
        case (Eid(id), '_, '_, Atom(ns, name, _, _, value, prefix))           => ('e, '_, '_, stmts :+ Add(id, s":$ns/$name", Values(value, prefix)))
        case ('_, '_, '_, Atom(ns, name, _, _, VarValue, _))                  => ('e, '_, '_, stmts :+ Add('id, s":$ns/$name", 'arg))
        case ('_, '_, '_, Atom(ns, name, _, _, value, prefix))                => ('e, '_, '_, stmts :+ Add('id, s":$ns/$name", Values(value, prefix)))
        case ('e, '_, '_, Atom(ns, name, _, _, value@Remove(_), prefix))      => ('e, '_, '_, stmts :+ Retract('e, s":$ns/$name", Values(value, prefix)))
        case ('e, '_, '_, Atom(ns, name, _, _, VarValue, _))                  => ('e, '_, '_, stmts :+ Add('e, s":$ns/$name", 'arg))
        case ('e, '_, '_, Atom(ns, name, _, _, value, prefix))                => ('e, '_, '_, stmts :+ Add('e, s":$ns/$name", Values(value, prefix)))
        case ('v, '_, '_, Atom(ns, name, _, _, VarValue, _))                  => ('e, '_, '_, stmts :+ Add('v, s":$ns/$name", 'arg))
        case ('v, '_, '_, Atom(ns, name, _, _, value, prefix))                => ('e, '_, '_, stmts :+ Add('v, s":$ns/$name", Values(value, prefix)))

        case ('arg, '_, '_, Bond(ns, refAttr, _)) => ('v, '_, '_, stmts :+ Add('arg, s":$ns/$refAttr", 'id))
        case ('e, '_, '_, Bond(ns, refAttr, _))   => ('v, '_, '_, stmts :+ Add('e, s":$ns/$refAttr", 'id))
        case ('v, '_, '_, Bond(ns, refAttr, _))   => ('v, '_, '_, stmts :+ Add('v, s":$ns/$refAttr", 'id))
        //        case ('_, '_, '_, Bond(ns, refAttr, _))                               => ('v, '_, '_, stmts :+ Add('id, s":$ns/$refAttr", 'id))

        case (e_, a_, v_, elem) => sys.error(s"[Model2Transaction:stmtsModel] Unexpected transformation:\n$model \n($e_, $a_, $v_, $elem)")
      }
    }
    stmts2
  }

  def resolve(stmts: Seq[Statement], e: Any, a: String, value: Any, prefix: Option[String] = None) = {
    def p(value: Any) = if (prefix.isDefined) prefix.get + value else value
    stmts ++ (value match {
      case Replace(oldNew)      => oldNew.toSeq.flatMap {
        case (oldValue, newValue) => Seq(Retract(e, a, p(oldValue)), Add(e, a, p(newValue)))
      }
      case Remove(Seq())        => getValues1(conn.db, e, a).toSeq.map(v => Retract(e, a, p(v)))
      case Remove(removeValues) => removeValues.map(v => Retract(e, a, p(v)))
      case Eq(vs)               => vs.map(v => Add(e, a, p(v)))
      case vs: Set[_]           => vs.map(v => Add(e, a, p(v)))
      case v :: Nil             => Seq(Add(e, a, p(v)))
      case vs: List[_]          => vs.map(v => Add(e, a, p(v)))
      case v                    => Seq(Add(e, a, p(v)))
    })
  }

  def insertStmts(dataRows: Seq[Seq[Any]]) = dataRows.map { args =>
    stmtsModel.foldLeft(0, Seq[Statement]()) { case ((i, stmts), stmt) =>
      val j = i + 1
      val arg = args(i)
      if (arg == null)
        (j, stmts)
      else
        stmt match {
          case Add('arg, a, 'id)                   => (j, resolve(stmts, arg, a, tempId()))
          case Add('id, a, 'arg)                   => (j, resolve(stmts, tempId(), a, arg))
          case Add('id, a, Values(vs, prefix))     => (j, resolve(stmts, tempId(), a, vs, prefix))
          case Add('e, a, 'arg)                    => (j, resolve(stmts, stmts.last.e, a, arg))
          case Add('e, a, Values(EnumVal, prefix)) => (j, resolve(stmts, stmts.last.e, a, arg, prefix))
          case Add('e, a, Values(vs, prefix))      => (j, resolve(stmts, stmts.last.e, a, vs, prefix))
          case Add('e, a, 'id)                     => (i, resolve(stmts, stmts.last.e, a, tempId()))
          case Add('v, a, 'arg)                    => (j, resolve(stmts, stmts.last.v, a, arg))
          case Add('v, a, Values(vs, prefix))      => (j, resolve(stmts, stmts.last.v, a, vs, prefix))
          case Retract(e, a, v)                    => (i, stmts)
          case unexpected                          => sys.error("[Model2Transaction:insertStmts] Unexpected statement: " + unexpected)
        }
    }._2
  }

  def saveStmts() = stmtsModel.foldLeft(0, Seq[Statement]()) { case ((i, stmts), stmt) =>
    val j = i + 1
    stmt match {
      case Add('id, a, Values(vs, prefix)) => (j, resolve(stmts, tempId(), a, vs, prefix))
      case Add('e, a, Values(vs, prefix))  => (j, resolve(stmts, stmts.last.e, a, vs, prefix))
      case Add('e, a, 'id)                 => (i, resolve(stmts, stmts.last.e, a, tempId()))
      case Add('v, a, Values(vs, prefix))  => (j, resolve(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
      case Retract(e, a, v)                => (i, stmts)
      case Add(_, a, 'arg)                 => sys.error(s"[Model2Transaction:saveStmts] Attribute `$a` needs a value applied")
      case unexpected                      => sys.error("[Model2Transaction:saveStmts] Unexpected statement: " + unexpected)
    }
  }._2


  def updateStmts() = stmtsModel.foldLeft(0, Seq[Statement]()) { case ((i, stmts), stmt) =>
    val j = i + 1
    stmt match {
      case Add('e, a, Values(vs, prefix))     => (j, resolve(stmts, stmts.last.e, a, vs, prefix))
      case Add('e, a, 'id)                    => (i, resolve(stmts, stmts.last.e, a, tempId()))
      case Add('v, a, Values(vs, prefix))     => (j, resolve(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
      case Add(e, a, Values(vs, prefix))      => (j, resolve(stmts, e, a, vs, prefix))
      case Retract('e, a, Values(vs, prefix)) => (j, resolve(stmts, stmts.last.e, a, vs, prefix))
      case Retract(e, a, Values(vs, prefix))  => (j, resolve(stmts, e, a, vs, prefix))
      case Add(_, a, 'arg)                    => sys.error(s"[Model2Transaction:updateStmts] Attribute `$a` needs a value applied")
      case unexpected                         => sys.error("[Model2Transaction:updateStmts] Unexpected statement: " + unexpected)
    }
  }._2


  def tx(dataRows: Seq[Seq[Any]] = Seq(), ids: Seq[Long] = Seq()): (Seq[Statement], Seq[Object]) = {
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


  def getValues(db: Database, id: Any, ns: Any, attr: Any) =
    Peer.q(s"[:find ?values :in $$ ?id :where [?id :$ns/$attr ?values]]", db, id.asInstanceOf[Object]).map(_.get(0))

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
        case Replace(oldNew)            => x(11);
          oldNew.toSeq.flatMap {
            case (oldValue, newValue) =>
              Seq(Retract(e, s":$ns/$attr", p(oldValue)), Add(e, s":$ns/$attr", p(newValue)))
          }
        case Remove(Seq())              => x(12, getValues(conn.db, e, ns, attr).toSeq.map(v => Retract(e, s":$ns/$attr", p(v))));
          getValues(conn.db, e, ns, attr).toSeq.map(v => Retract(e, s":$ns/$attr", p(v)))
        case Remove(removeValues)       => x(13, removeValues, removeValues.map(v => Retract(e, s":$ns/$attr", p(v))));
          removeValues.map(v => Retract(e, s":$ns/$attr", p(v)))
        case vs: Set[_]                 => x(14, vs, vs.toSeq.map(v => Add(e, s":$ns/$attr", p(v))));
          vs.toSeq.map(v => Add(e, s":$ns/$attr", p(v)))
        case vs: List[_] if vs.size > 1 => x(15, vs, vs.map(v => Add(e, s":$ns/$attr", p(v))));
          vs.map(v => Add(e, s":$ns/$attr", p(v)))
        case v :: Nil                   => x(16, v, Seq(Add(e, s":$ns/$attr", p(v))));
          Seq(Add(e, s":$ns/$attr", p(v)))
        case v                          => x(17, v, Seq(Add(e, s":$ns/$attr", p(v))));
          Seq(Add(e, s":$ns/$attr", p(v)))
      }
    }

    val newStmts = element match {
      case Atom(ns, attr, _, _, VarValue, _)                => x(1, s":$ns/$attr");
        stmts ++ addAtoms(ns, attr)
      case Atom(ns, attr, _, _, EnumVal, prefix)            => stmts ++ addAtoms(ns, attr, prefix)
      case Atom(ns, attr, _, _, Eq(values), prefix)         => stmts ++ addAtoms(ns, attr, prefix, Some(values))
      case Atom(ns, attr, _, _, replace@Replace(_), prefix) => x(4, stmts, addAtoms(ns, attr, prefix, Some(replace)));
        stmts ++ addAtoms(ns, attr, prefix, Some(replace))
      case Atom(ns, attr, _, _, remove@Remove(_), prefix)   => x(5, stmts, addAtoms(ns, attr, prefix, Some(remove)), stmts ++ addAtoms(ns, attr, prefix, Some(remove)));
        stmts ++ addAtoms(ns, attr, prefix, Some(remove))

      case Bond(ns, refAttr, refNs) => x(2, s":$ns/$refAttr $nextId");
        stmts :+ Add(e, s":$ns/$refAttr", nextId)

      case Group(Bond(ns, refAttr, refNs), nestedElements) => {
        val nestedDataRows = nestedData(nestedElements, arg)

        // Loop nested rows of data
        val (elementStmts, elementIds) = nestedDataRows.foldLeft((stmts, Set[Object]())) {
          case ((elementStmts1, nestedIds), nestedDataRow) =>
            // Recursively create nested elements
            val (rowStmts, _) = mergeDataWithElements(nestedElements, nestedDataRow)
            val lastId = rowStmts.last.e.asInstanceOf[Object]
            (elementStmts1 ++ rowStmts, nestedIds + lastId)
        }

        // Add references to nested entities
        val refStmts = elementIds.map(Add(e, s":$ns/$refAttr", _))
        elementStmts ++ refStmts
      }


      case Meta(ns, _, _, EntValue) => stmts //++ add(ns, attr, None, Some(e))

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