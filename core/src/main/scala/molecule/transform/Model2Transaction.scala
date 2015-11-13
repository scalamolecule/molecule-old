package molecule.transform
import java.util.{List => jList}

//import datomic.{Connection, Database, Peer}
import datomic._
//import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.transaction._
import molecule.util.Debug

import scala.collection.JavaConversions._


case class Model2Transaction(conn: Connection, model: Model) {
  val x = Debug("Model2Transaction", 45, 45, false, 6)

  private def tempId(attr: String) = attr match {
    case "tx"                 => Peer.tempid(s":db.part/tx")
    case s if s.contains('_') => Peer.tempid(s":" + attr.substring(1).split("(?=_)").head) // extract "partition" from ":partition_Namespace/attr"
    case _                    => Peer.tempid(s":db.part/user")
  }

  private def getValues1(db: Database, id: Any, attr: String) = {
    val query = s"[:find ?values :in $$ ?id :where [?id $attr ?values]]"
    //    x(25, query, id.asInstanceOf[Object], Peer.q(query, db, id.asInstanceOf[Object]))
    Peer.q(query, db, id.asInstanceOf[Object]).map(_.get(0))
  }

  val stmtsModel: Seq[Statement] = {

    def resolveElement(eSlot: Any, stmts: Seq[Statement], element: Element): (Any, Seq[Statement]) = (eSlot, element) match {
      case ('_, Meta(ns, "", "e", _, EntValue))          => ('arg, stmts)
      case ('_, Meta(ns, "", "e", _, Eq(Seq(id: Long)))) => (Eid(id), stmts)
      case ('_, Atom(ns, name, _, _, VarValue, _, _))    => ('e, stmts :+ Add('tempId, s":$ns/$name", 'arg))
      case ('_, Atom(ns, name, _, _, value, prefix, _))  => ('e, stmts :+ Add('tempId, s":$ns/$name", Values(value, prefix)))
      case ('_, Bond(ns, refAttr, refNs))                => ('v, stmts :+ Add('tempId, s":$ns/$refAttr", s":$refNs"))

      case (e, Group(Bond(ns, refAttr, _), elements)) =>
        val nested = elements.foldLeft('v: Any, Seq[Statement]()) {
          case ((eSlot1, stmts1), element1) => resolveElement(eSlot1, stmts1, element1)
        }._2
        val parentId = if (e == '_) 'parentId else 'e
        ('e, stmts :+ Add(parentId, s":$ns/$refAttr", nested))

      // First with id
      case (Eid(id), Atom(ns, name, _, _, value@Remove(_), prefix, _)) => ('e, stmts :+ Retract(id, s":$ns/$name", Values(value, prefix)))
      case (Eid(id), Atom(ns, name, _, _, value, prefix, _))           => ('e, stmts :+ Add(id, s":$ns/$name", Values(value, prefix)))
      case (Eid(id), Bond(ns, refAttr, refNs))                         => ('v, stmts :+ Add(id, s":$ns/$refAttr", 'tempId))

      // Same namespace
      case ('e, Atom(ns, name, _, _, value@Remove(_), prefix, _)) => ('e, stmts :+ Retract('e, s":$ns/$name", Values(value, prefix)))
      case ('e, Atom(ns, name, _, _, VarValue, _, _))             => ('e, stmts :+ Add('e, s":$ns/$name", 'arg))
      case ('e, Atom(ns, name, _, _, value, prefix, _))           => ('e, stmts :+ Add('e, s":$ns/$name", Values(value, prefix)))
      case ('e, Bond(ns, refAttr, refNs))                         => ('v, stmts :+ Add('e, s":$ns/$refAttr", s":$refNs"))
      case ('e, TxModel(elements))                                => ('e, stmts ++ elements.foldLeft('tx: Any, Seq[Statement]()) {
        case ((eSlot1, stmts1), element1) => resolveElement(eSlot1, stmts1, element1)
      }._2)

      // Continue with only transaction Atoms...
      case ('tx, Atom(ns, name, _, _, VarValue, _, _))                       => ('e, stmts :+ Add('e, s":$ns/$name", 'arg))
      case ('tx, Atom(ns, name, _, _, value, prefix, _)) if name.last == '_' => ('tx, stmts :+ Add('tx, s":$ns/${name.init}", Values(value, prefix)))
      case ('tx, Atom(ns, name, _, _, value, prefix, _))                     => ('tx, stmts :+ Add('tx, s":$ns/$name", Values(value, prefix)))

      // Next namespace
      case ('v, Atom(ns, name, _, _, VarValue, _, _))   => ('e, stmts :+ Add('v, s":$ns/$name", 'arg))
      case ('v, Atom(ns, name, _, _, value, prefix, _)) => ('e, stmts :+ Add('v, s":$ns/$name", Values(value, prefix)))
      case ('v, Bond(ns, refAttr, _))                   => ('v, stmts :+ Add('v, s":$ns/$refAttr", 'tempId))

      case ('arg, Bond(ns, refAttr, _)) => ('v, stmts :+ Add('arg, s":$ns/$refAttr", 'tempId))

      // BackRef
      case (_, ReBond(ns, _, _, _, _)) => ('e, stmts :+ Add('ns, s":$ns", ""))

      case (e, elem) => sys.error(s"[Model2Transaction:stmtsModel] Unexpected transformation:\nMODEL: $model \nPAIR: ($e, $elem)\nSTMTS: $stmts")
    }

    def replace$(elements: Seq[Element]): Seq[Element] = elements map {
      case a@Atom(_, attr, _, _, _, Some(enumPrefix), _)
        if attr.last == '$' && enumPrefix.init.last == '$'    => a.copy(name = attr.init, enumPrefix = Some(enumPrefix.init.init + "/"))
      case a@Atom(_, attr, _, _, _, _, _) if attr.last == '$' => a.copy(name = attr.init)
      case b@Bond(_, attr, _) if attr.last == '$'             => b.copy(refAttr = attr.init)
      case t@Transitive(_, attr, _, _, _) if attr.last == '$' => t.copy(refAttr = attr.init)
      case Group(ref, es)                                     => Group(ref, replace$(es))
      case other                                              => other
    }
    val model1 = Model(replace$(model.elements))
    x(50, model, model1)

    model1.elements.foldLeft('_: Any, Seq[Statement]()) {
      case ((eSlot, stmts), element) => resolveElement(eSlot, stmts, element)
    }._2
  }

  private def valueStmts(stmts: Seq[Statement], e: Any, a: String, value: Any, prefix: Option[String] = None): Seq[Statement] = {
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

  def splitStmts(): (Seq[Statement], Seq[Statement]) = {
    val (dataStmts0, txStmts0) = stmtsModel.map {
      case tx@Add('tx, _, Values(vs, prefix)) => (None, Some(tx))
      case other                              => (Some(other), None)
    }.unzip
    (dataStmts0.flatten, txStmts0.flatten)
  }

  def lastE(stmts: Seq[Statement], attr: String, nestedE: Any = 0) = {
    if (nestedE != 0)
      nestedE
    else if (stmts.isEmpty)
      tempId(attr)
    else
      stmts.last.e
  }

  def lastV(stmts: Seq[Statement], attr: String, nestedE: Any = 0) = {
    if (nestedE != 0)
      nestedE
    else if (stmts.isEmpty)
      tempId(attr)
    else
      stmts.last.v
  }

  def matchDataStmt(stmts: Seq[Statement], dataStmt: Statement, arg: Any, cur: Int, next: Int, nestedE: Any = 0) = dataStmt match {
    // Keep current cursor (add no new data in this iteration)
    case Add('tempId, a, refNs: String) => (cur, valueStmts(stmts, tempId(a), a, tempId(refNs)))
    case Add('e, a, refNs: String)      => (cur, valueStmts(stmts, lastE(stmts, a, nestedE), a, tempId(refNs)))
    case Add('v, a, 'tempId)            => (cur, valueStmts(stmts, stmts.last.v, a, tempId(a)))
    case Retract(e, a, v)               => (cur, stmts)

    // Advance cursor for next value in data row
    case Add('tempId, a, 'arg)                                                    => (next, valueStmts(stmts, tempId(a), a, arg))
    case Add('tempId, a, Values(EnumVal, prefix))                                 => (next, valueStmts(stmts, tempId(a), a, arg, prefix))
    case Add('tempId, a, Values(vs, prefix))                                      => (next, valueStmts(stmts, tempId(a), a, vs, prefix))
    case Add('arg, a, 'tempId)                                                    => (next, valueStmts(stmts, arg, a, tempId(a)))
    case Add('e, a, 'arg)                                                         => (next, valueStmts(stmts, lastE(stmts, a, nestedE), a, arg))
    case Add('e, a, Values(EnumVal, prefix))                                      => (next, valueStmts(stmts, lastE(stmts, a, nestedE), a, arg, prefix))
    case Add('e, a, Values(vs, prefix))                                           => (next, valueStmts(stmts, lastE(stmts, a, nestedE), a, vs, prefix))
    case Add('v, a, 'arg) if stmts.nonEmpty && stmts.last.v.isInstanceOf[db.DbId] => (next, valueStmts(stmts, stmts.last.v, a, arg))
    case Add('v, a, 'arg)                                                         => (next, valueStmts(stmts, lastV(stmts, a, nestedE), a, arg))
    case Add('v, a, Values(EnumVal, prefix))                                      => (next, valueStmts(stmts, lastV(stmts, a, nestedE), a, arg, prefix))
    case Add('v, a, Values(vs, prefix))                                           => (next, valueStmts(stmts, lastV(stmts, a, nestedE), a, vs, prefix))
    case Add('tx, a, 'arg)                                                        => (next, valueStmts(stmts, tempId("tx"), a, arg))
    case Add(e0, ref0, nestedStmts0: Seq[_]) if arg == Nil                        => (next, stmts)
    case Add(e, ref, nestedStmts: Seq[_])                                         => {
      val parentE = if (e == 'parentId)
        tempId(ref)
      else if (stmts.isEmpty)
        e
      else
        stmts.reverse.collectFirst {
          // Find entity value of Add statement with matching namespace
          case Add(e1, a, _) if a.replaceFirst("/.*", "") == ref.replaceFirst("/.*", "") => e1
        }.getOrElse(sys.error("[Model2Transaction:matchDataStmt] Couldn't find previous statement with matching namespace."))
      val nestedRows = untupleNestedArgss(nestedStmts, arg)
      val nestedInsertStmts = nestedRows.flatMap { nestedRow =>
        val nestedE = tempId(ref)
        val bondStmt = Add(parentE, ref, nestedE)
        val nestedStmtsCasted = nestedStmts map { case s: Statement => s }
        val nestedStmts1 = resolveStmts(nestedStmtsCasted, nestedRow, nestedE)
        bondStmt +: nestedStmts1
      }
      (next, stmts ++ nestedInsertStmts)
    }

    case unexpected => sys.error("[Model2Transaction:matchDataStmt] Unexpected insert statement: " + unexpected)
  }

  def resolveStmts(genericStmts: Seq[Statement], row: Seq[Any], nestedE0: Any = 0): Seq[Statement] = {
    genericStmts.foldLeft(0, Seq[Statement]()) { case ((cur, stmts0), dataStmt) =>
      val arg = row.get(cur)
      val next = if ((cur + 1) < row.size) cur + 1 else cur
      val (stmts, nestedE) = if (stmts0.isEmpty)
        (stmts0, nestedE0)
      else stmts0.last match {
        case Add('ns, ns, backRef) => (stmts0.init, backRef)
        case _                     => (stmts0, nestedE0)
      }
      (arg, dataStmt) match {
        case (null, _)                                                                     => sys.error(
          "[Model2Transaction:insertStmts] null values not allowed. Please use `attr$` for Option[tpe] values.")
        case (_, br@Add('ns, ns, ""))                                                      => {
          val backRef = stmts.reverse.collectFirst {
            case Add(e, a, v) if a.startsWith(ns) && e.isInstanceOf[db.DbId] => e
          }.getOrElse(sys.error(s"[Model2Transaction:insertStmts] Couldn't find namespace `$ns` in any previous Add statements.\n" + stmts.mkString("\n")))
          (cur, stmts :+ Add('ns, ns, backRef))
        }
        case (None, Add('e, a, refNs: String))                                             => (cur, valueStmts(stmts, lastE(stmts, a), a, tempId(refNs)))
        case (None, _)                                                                     => (next, stmts)
        case (Some(arg1), _)                                                               => matchDataStmt(stmts, dataStmt, arg1, cur, next, nestedE)
        case (_, Add('e, a, 'arg)) if stmts.nonEmpty && stmts.last.v.isInstanceOf[db.DbId] => (next, valueStmts(stmts, stmts.last.v, a, arg))
        case (_, _)                                                                        =>
          //          x(25, arg, dataStmt)
          matchDataStmt(stmts, dataStmt, arg, cur, next, nestedE)
        //          case (_, Add('e, a, Values(EnumVal, prefix))) if stmts.last.v.toString.startsWith("#db/id[:db.part") => (next, resolveStmts(stmts, stmts.last.v, a, arg, prefix))
        //          case (_, Add('e, a, Values(vs, prefix))) if stmts.last.v.toString.startsWith("#db/id[:db.part")      => (next, resolveStmts(stmts, stmts.last.v, a, vs, prefix))
      }

    }._2
  }

  def insertStmts(dataRows: Seq[Seq[Any]]): Seq[Seq[Statement]] = {
    val (dataStmts, txStmts) = splitStmts()
    val dataStmtss: Seq[Seq[Statement]] = dataRows.map(resolveStmts(dataStmts, _))
    val txId = tempId("tx")
    val txStmtss: Seq[Seq[Statement]] = Seq(txStmts.foldLeft(Seq[Statement]()) {
      case (stmts, Add('tx, a, Values(vs, prefix))) => valueStmts(stmts, txId, a, vs, prefix)
      case (stmts, unexpected)                      => sys.error("[Model2Transaction:insertStmts:txStmts] Unexpected insert statement: " + unexpected)
    })
    dataStmtss ++ (if (txStmtss.head.isEmpty) Nil else txStmtss)
  }

  def saveStmts(): Seq[Statement] = stmtsModel.foldLeft(0, Seq[Statement]()) {
    case ((i, stmts), stmt) =>
      val j = i + 1
      stmt match {
        case Add('tempId, a, Values(vs, prefix)) => (j, valueStmts(stmts, tempId(a), a, vs, prefix))
        case Add('e, a, Values(vs, prefix))      => (j, valueStmts(stmts, lastE(stmts, a), a, vs, prefix))
        case Add('e, a, 'tempId)                 => (i, valueStmts(stmts, lastE(stmts, a), a, tempId(a)))
        case Add(id, a, 'tempId)                 => (i, valueStmts(stmts, id, a, tempId(a)))
        case Add('e, a, refNs: String)           => (i, valueStmts(stmts, lastE(stmts, a), a, tempId(refNs)))
        case Add('v, a, Values(vs, prefix))      => (j, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
        case Retract(e, a, v)                    => (i, stmts)
        case Add(_, a, 'arg)                     => sys.error(s"[Model2Transaction:saveStmts] Attribute `$a` needs a value applied")
        case unexpected                          => sys.error("[Model2Transaction:saveStmts] Unexpected save statement: " + unexpected)
      }
  }._2


  def updateStmts(): Seq[Statement] = {
    val (dataStmts0, txStmts0) = splitStmts()
    val dataStmts: Seq[Statement] = dataStmts0.foldLeft(0, Seq[Statement]()) {
      case ((i, stmts), stmt) =>
        val j = i + 1
        stmt match {
          case Add('e, a, Values(vs, prefix))     => (j, valueStmts(stmts, lastE(stmts, a), a, vs, prefix))
          case Add('e, a, 'tempId)                => (i, valueStmts(stmts, lastE(stmts, a), a, tempId(a)))
          case Add('v, a, Values(vs, prefix))     => (j, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
          case Add('tx, a, Values(vs, prefix))    => (j, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
          case Add(e, a, Values(vs, prefix))      => (j, valueStmts(stmts, e, a, vs, prefix))
          case Retract('e, a, Values(vs, prefix)) => (j, valueStmts(stmts, lastE(stmts, a), a, vs, prefix))
          case Retract(e, a, Values(vs, prefix))  => (j, valueStmts(stmts, e, a, vs, prefix))
          case Add(_, a, 'arg)                    => sys.error(s"[Model2Transaction:updateStmts] Attribute `$a` needs a value applied")
          case unexpected                         => sys.error("[Model2Transaction:updateStmts] Unexpected update statement: " + unexpected)
        }
    }._2
    val txId = tempId("tx")
    val txStmts: Seq[Statement] = txStmts0.foldLeft(Seq[Statement]()) {
      case (stmts, Add('tx, a, Values(vs, prefix))) => valueStmts(stmts, txId, a, vs, prefix)
      case (stmts, unexpected)                      => sys.error("[Model2Transaction:insertStmts:txStmts] Unexpected insert statement: " + unexpected)
    }
    dataStmts ++ txStmts
  }


  private def untupleNestedArgss(stmts: Seq[Any], arg0: Any): Seq[Seq[Any]] = {
    val (argArity, arg) = arg0 match {
      case a: Seq[_]  => a.head match {
        case None       => sys.error("[Model2Transaction:nestedData] Please use `List()` instead of `List(None)` for nested null values.")
        case null       => sys.error("[Model2Transaction:nestedData] Please use `List()` instead of `List(null)` for nested null values.")
        case p: Product => (p.productArity, a)
        case l: Seq[_]  => (l.size, a)
        case _          => (1, a)
      }
      case unexpected => sys.error("[Model2Transaction:nestedData] Unexpected data: " + unexpected)
    }
    val argStmts = stmts.collect {
      case a@Add(_, _, 'arg)                => a
      case a@Add(_, _, Values(vs, prefix))  => a
      case a@Add(_, _, nestedStmts: Seq[_]) => a
    }
    val stmtsSize = argStmts.size
    assert(argArity == stmtsSize, s"[Model2Transaction:nestedData] Arity of statements and arguments should match. Found: \n" +
      s"Statements (arity $stmtsSize): " + stmts.mkString("\n  ", "\n  ", "\n") +
      s"Arguments0                  : " + arg0 +
      s"Arguments  (arity $argArity): " + arg.mkString("\n  ", "\n  ", "\n"))

    // Todo: can we convert tuples more elegantly?
    arg map {
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
      case a                                                                     => Seq(a)
    }
  }
}