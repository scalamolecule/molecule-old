package molecule.transform
import datomic.{Connection, Database, Peer}
import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.transaction._
import molecule.util.Debug

object Model2Transaction extends Debug with DatomicFacade {
  val y = debug("Model2Transaction", 1, 99, false, 2)

  def apply(conn: Connection, model: Model, dataRows: Seq[Seq[Any]]): Seq[Statement] = dataRows flatMap { dataRow =>
    mergeDataRow(dataRow, model.elements)
  }

  def mergeDataRow(dataRow: Seq[Any], elements: Seq[Element]) = {
    var n = dataRow.size

    // Start with last element of molecule and go backwards (to be able to reference nested entities)
    val (newStmts1, _, _) = elements.foldRight((Seq[Statement](), "": Object, "")) { case (element, (stmts, prevId, prevNs)) =>
      val data = if (element.isInstanceOf[Bond]) 0 else {n -= 1; dataRow(n)}

      if (data == null)
        // When data is null, no fact is asserted (stmts is passed on unchanged)
        (stmts, null, null)
      else {
        val (newStmts, id, _) = mkStatements(stmts, element, data, prevId, prevNs)
        (newStmts, id, getNs(element))
      }
    }
    newStmts1
  }

  def mkStatements(stmts: Seq[Statement], element: Element, data: Any, prevId: Object, prevNs: String): (Seq[Statement], Object, String) = {
    val id = if (getNs(element) != prevNs) tempId() else prevId

    def add(ns: String, attr: String, prefixString: String = "") = {
      def prefix(value: Any) = if (prefixString.toString.nonEmpty) prefixString.toString + value else value
      data match {
        case values: Set[_] => values.map(value => Add(id, s":$ns/$attr", prefix(value)))
        case value          => Seq(Add(id, s":$ns/$attr", prefix(value)))
      }
    }

    val (newStmts, ns1) = element match {
      case Atom(ns, attr, tpe, card, VarValue, _)               => (stmts ++ add(ns, attr), ns)
      case Atom(ns, attr, tpe, card, EnumVal, Some(enumPrefix)) => (stmts ++ add(ns, attr, enumPrefix), ns)
      case Bond(ns, refAttr, refNs)                             => (stmts :+ Add(id, s":$ns/$refAttr", prevId), ns)

      //            case Atom(ns, attr, tpe, card, Eq(values), prefix)         => ((ns, attr, card, prefix.getOrElse("")), values)
      //            case Atom(ns, attr, tpe, card, replace@Replace(_), prefix) => ((ns, attr, card, prefix.getOrElse("")), Seq(replace))
      //            case Atom(ns, attr, tpe, card, remove@Remove(_), prefix)   => ((ns, attr, card, prefix.getOrElse("")), Seq(remove))

      case Group(Bond(ns, refAttr, refNs), nestedElements) => {
        val nestedDataRows = nestedData(nestedElements, data)

        // Loop nested rows of data
        val (elementStmts, elementIds) = nestedDataRows.foldLeft((stmts, Set[Object]())) { case ((elementStmts1, ids), nestedDataRow) =>

          // Recursively create nested elements
          val rowStmts = mergeDataRow(nestedDataRow, nestedElements)
          val lastId = rowStmts.last.e

          (elementStmts1 ++ rowStmts, ids + lastId)
        }

        // Add references to nested entities
        val refStmts = elementIds.map(Add(id, s":$ns/$refAttr", _))
        (elementStmts ++ refStmts, ns)
      }

      case unexpected => sys.error("[Model2Transaction:apply] Unexpected molecule element: " + unexpected)
    }
    (newStmts, id, ns1)
  }


  def nestedData(elements: Seq[Element], data0: Any) = {
    val (dataArity, data) = data0 match {
      case d: Seq[_]  => d.head match {
        case p: Product => (p.productArity, d)
        case l: Seq[_]  => (l.size, d)
      }
      case unexpected => sys.error("[Model2Transaction:dataGroup] Unexpected data: " + unexpected)
    }
    assert(dataArity == elements.size, s"[Model2Transaction:dataGroup] Arity of attributes and values should match. Found: \n" +
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

  def tempId(partition: String = "user") = Peer.tempid(s":db.part/$partition")
  def getNs(e: Element) = e match {
    case Atom(ns, _, _, _, _, _)  => ns
    case Bond(ns, _, _)           => ns
    case Group(Bond(ns, _, _), _) => ns
    case unexpected               => sys.error("[Model2Transaction:getNs] Unexpected element: " + unexpected)
  }





  def applyOLD(conn: Connection, model: Model, argss: Seq[Seq[Any]]): Seq[Seq[Any]] = {
    val attrs = getEmptyAttrs(model)
    val rawMolecules = chargeMolecules(attrs, argss)
    val molecules = groupNamespaces(rawMolecules)
    upsertTransaction(conn.db, molecules)
  }

  def getEmptyAttrs(model: Model): Seq[(String, String, Int, String)] = model.elements.collect {
    case Atom(ns, n, tpe, card, VarValue, _)               => (ns, n, card, "")
    case Atom(ns, n, tpe, card, EnumVal, Some(enumPrefix)) => (ns, n, card, enumPrefix)
    case Group(Bond(ns, refAttr, refNs), elements)         => (ns, refAttr, 2, "")
  }

  def getNonEmptyAttrs(model: Model): Seq[((String, String, Int, String), Seq[Any])] = model.elements.collect {
    case Atom(ns, attr, tpe, card, Eq(values), prefix)         => ((ns, attr, card, prefix.getOrElse("")), values)
    case Atom(ns, attr, tpe, card, replace@Replace(_), prefix) => ((ns, attr, card, prefix.getOrElse("")), Seq(replace))
    case Atom(ns, attr, tpe, card, remove@Remove(_), prefix)   => ((ns, attr, card, prefix.getOrElse("")), Seq(remove))
  }

  def chargeMolecules(attrs: Seq[(String, String, Int, String)], argss: Seq[Seq[Any]]): Seq[Seq[Seq[Any]]] = {
    //    assert(attrs.size == argss.head.size, s"Arity of attributes and values should match. Found:\n" +
    //      attrs.size + " attributes     : " +
    //      attrs.map(a => ":" + a._1 + "/" + a._2).mkString(", ") + "\n" +
    //      argss.head.size + "-arity of values: " + argss +
    //      "\nMolecule should prevent us from getting here. Please file a bug!\n----------------------")

    argss.map { args =>
      // Process molecule attributes from last to first (insert related namespaces first)
      attrs.zip(args).reverse.flatMap { data =>
        val (at, values) = (data._1, data._2)
        val (ns, attr, cardinality, enumPrefix) = (at._1, at._2, at._3, at._4)
        if (values == null) {
          // No fact added when value is null
          None
        } else {

          Some(Seq(ns, attr, cardinality, enumPrefix, values))
        }
      }
    }
  }

  def groupNamespaces(rawMolecules: Seq[Seq[Seq[Any]]]): Seq[Seq[Seq[Seq[Any]]]] = {
    rawMolecules.map { rawMolecule =>
      val (molecule, _) = rawMolecule.foldLeft((Seq(Seq(Seq[Any]())), "": Any)) {
        case ((elements, prevNS), attrData) =>
          val curNS = attrData.head
          if (curNS != prevNS)
            (elements :+ Seq(attrData), curNS)
          else
            (elements.init :+ (elements.last :+ attrData), curNS)
      }
      molecule.tail // Skip initial empty head
    }
  }

  def upsertTransaction(db: Database, molecules: Seq[Seq[Seq[Seq[Any]]]], ids: Seq[Long] = Seq()): Seq[Seq[Any]] = {
    molecules.zipAll(ids, Seq(Seq(Seq[Any]())), 0L).flatMap { case (molecule, id0) =>
      val (moleculeStmts: Seq[Seq[Any]], _, _) = molecule.foldLeft((Seq[Seq[Any]](), "": Any, "": Any)) {
        case ((stmts, prevNS, prevId), namespace) => {
          val firstAttr = namespace.head
          val ns = firstAttr.head
          val id = if (id0 > 0L) id0 else Peer.tempid(":db.part/user")

          val namespaceStmts: Seq[Seq[Any]] = namespace.foldLeft(Seq[Seq[Any]]()) { case (attrStmts, atom) =>
            val (attr, prefix, values) = (atom(1), atom(3), atom(4))
            def p(value: Any) = if (prefix.toString.nonEmpty) prefix.toString + value else value

            values match {
              case Seq(Replace(oldNew))       => oldNew.foldLeft(attrStmts) { case (acc, (oldValue, newValue)) =>
                acc :+ Seq(":db/retract", id, s":$ns/$attr", p(oldValue)) :+ Seq(":db/add", id, s":$ns/$attr", p(newValue))
              }
              case Seq(Remove(Seq()))         => getValues(db, id, ns, attr).foldLeft(attrStmts) { case (acc, removeValue) =>
                acc :+ Seq(":db/retract", id, s":$ns/$attr", p(removeValue))
              }
              case Seq(Remove(removeValues))  => removeValues.foldLeft(attrStmts) { case (acc, removeValue) =>
                acc :+ Seq(":db/retract", id, s":$ns/$attr", p(removeValue))
              }
              case set: Set[_]                =>
                //                y(21, set)
                attrStmts ++ set.map(v => Seq(":db/add", id, s":$ns/$attr", p(v)))
              case vs: List[_] if vs.size > 1 =>
                //                y(22, vs)
                attrStmts ++ vs.map(v => Seq(":db/add", id, s":$ns/$attr", p(v)))
              case value :: Nil               =>
                //                y(23, value)
                attrStmts :+ Seq(":db/add", id, s":$ns/$attr", p(value))
              case value                      =>
                //                y(24, value)
                attrStmts :+ Seq(":db/add", id, s":$ns/$attr", p(value))
            }
          }

          if (stmts.size == 0) {
            // First namespace
            (stmts ++ namespaceStmts, ns, id)
          } else {
            // Reference namespaces
            val bondToPreviousNamespace = Seq(":db/add", id, s":$ns/$prevNS", prevId)
            (stmts ++ namespaceStmts :+ bondToPreviousNamespace, ns, id)
          }
        }
      }
      moleculeStmts
    }
  }
}