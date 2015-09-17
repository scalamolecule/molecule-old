package molecule.transform
import java.util.{List => jList}

import datomic.{Connection, Database, Peer}
//import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.transaction._
import molecule.util.Debug

import scala.collection.JavaConversions._


case class Model2Transaction(conn: Connection, model: Model) {
  val x = Debug("Model2Transaction", 50, 21, false, 6)

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
      // First
      //      case ('_, Meta(ns, "", "e", EntValue, _))          => ('arg, stmts)
      case ('_, Meta(ns, "", "e", _, EntValue))          => ('arg, stmts)
      case ('_, Meta(ns, "", "e", _, Eq(Seq(id: Long)))) => (Eid(id), stmts)
      case ('_, Atom(ns, name, _, _, VarValue, _, _))    => ('e, stmts :+ Add('tempId, s":$ns/$name", 'arg))
      case ('_, Atom(ns, name, _, _, value, prefix, _))  => ('e, stmts :+ Add('tempId, s":$ns/$name", Values(value, prefix)))
      case ('_, Bond(ns, refAttr, refNs))                => ('v, stmts :+ Add('tempId, s":$ns/$refAttr", s":$refNs"))
      //      case ('_, Bond(ns, refAttr, _))                    => ('v, stmts :+ Add('tempId, s":$ns/$refAttr", 'tempId))

      case (e, Group(Bond(ns, refAttr, _), elements)) =>
        val nested = elements.foldLeft('v: Any, Seq[Statement]()) {
          case ((eSlot1, stmts1), element1) => resolveElement(eSlot1, stmts1, element1)
        }._2
        val parentId = if (e == '_) 'parentId else 'e
        ('e, stmts :+ Add(parentId, s":$ns/$refAttr", nested))

      // First with id
      case (Eid(id), Atom(ns, name, _, _, value@Remove(_), prefix, _)) => ('e, stmts :+ Retract(id, s":$ns/$name", Values(value, prefix)))
      case (Eid(id), Atom(ns, name, _, _, value, prefix, _))           => ('e, stmts :+ Add(id, s":$ns/$name", Values(value, prefix)))

      // Same namespace
      case ('e, Atom(ns, name, _, _, value@Remove(_), prefix, _)) => ('e, stmts :+ Retract('e, s":$ns/$name", Values(value, prefix)))
      case ('e, Atom(ns, name, _, _, VarValue, _, _))             => ('e, stmts :+ Add('e, s":$ns/$name", 'arg))
      case ('e, Atom(ns, name, _, _, value, prefix, _))           => ('e, stmts :+ Add('e, s":$ns/$name", Values(value, prefix)))
      //      case ('e, Bond(ns, refAttr, _))                             => ('v, stmts :+ Add('e, s":$ns/$refAttr", 'tempId))
      case ('e, Bond(ns, refAttr, refNs)) => ('v, stmts :+ Add('e, s":$ns/$refAttr", s":$refNs"))


      case ('e, TxModel(elements)) => ('e, stmts ++ elements.foldLeft('tx: Any, Seq[Statement]()) {
        case ((eSlot1, stmts1), element1) => resolveElement(eSlot1, stmts1, element1)
      }._2)

      // Continue with only transaction Atoms...
      case ('tx, Atom(ns, name, _, _, VarValue, _, _))                       => ('e, stmts :+ Add('e, s":$ns/$name", 'arg))
      case ('tx, Atom(ns, name, _, _, value, prefix, _)) if name.last == '_' => ('tx, stmts :+ Add('tx, s":$ns/${name.init}", Values(value, prefix)))
      case ('tx, Atom(ns, name, _, _, value, prefix, _))                     => ('tx, stmts :+ Add('tx, s":$ns/$name", Values(value, prefix)))
      //      case ('tx, a@Atom(ns, name, _, _, _, _, _))                     =>
      //        sys.error(s"[Model2Transaction:stmtsModel] Please use underscore suffix for tx attribute (`$name` -> `${name}_`}) in:\n$a")

      // Next namespace
      case ('v, Atom(ns, name, _, _, VarValue, _, _))   => ('e, stmts :+ Add('v, s":$ns/$name", 'arg))
      case ('v, Atom(ns, name, _, _, value, prefix, _)) => ('e, stmts :+ Add('v, s":$ns/$name", Values(value, prefix)))
      case ('v, Bond(ns, refAttr, _))                   => ('v, stmts :+ Add('v, s":$ns/$refAttr", 'tempId))

      case ('arg, Bond(ns, refAttr, _)) => ('v, stmts :+ Add('arg, s":$ns/$refAttr", 'tempId))

      case (e, elem) =>
        //        x(27, model, e, elem)
        //        throw new RuntimeException(s"[Model2Transaction:stmtsModel] Unexpected transformation:\n$model \n($e, $elem)")
        sys.error(s"[Model2Transaction:stmtsModel] Unexpected transformation:\n$model \n($e, $elem)")
    }
    model.elements.foldLeft('_: Any, Seq[Statement]()) {
      case ((eSlot, stmts), element) => resolveElement(eSlot, stmts, element)
    }._2
  }

  private def resolveStmts(stmts: Seq[Statement], e: Any, a: String, value: Any, prefix: Option[String] = None): Seq[Statement] = {
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

  def lastE(stmts: Seq[Statement], attr: String) = if (stmts.isEmpty) tempId(attr) else stmts.last.e

  def insertStmts(dataRows: Seq[Seq[Any]]): Seq[Seq[Statement]] = {
    val (dataStmts, txStmts) = splitStmts()
    val dataStmtss: Seq[Seq[Statement]] = dataRows.map {args =>
      x(30, args)
      dataStmts.foldLeft(0, Seq[Statement]()) {case ((cur, stmts), dataStmt) =>
        x(31, s"$cur - $dataStmt")
        val arg = args.get(cur)
        val next = if ((cur + 1) < args.size) cur + 1 else cur
        if (arg == null) {
          x(20, s"$cur - $next - $dataStmt")
          (next, stmts)
        } else
          dataStmt match {
            case Add('tempId, a, 'arg)                    => x(1, s"$cur - $dataStmt - $arg"); (next, resolveStmts(stmts, tempId(a), a, arg))
            case Add('tempId, a, Values(EnumVal, prefix)) => x(2, s"$cur - $dataStmt - $arg"); (next, resolveStmts(stmts, tempId(a), a, arg, prefix))
            case Add('tempId, a, Values(vs, prefix))      => x(3, s"$cur - $dataStmt - $arg"); (next, resolveStmts(stmts, tempId(a), a, vs, prefix))
            case Add('tempId, a, refNs: String)           => x(4, s"$cur - $dataStmt - $arg"); (cur, resolveStmts(stmts, tempId(a), a, tempId(refNs)))
            case Add('arg, a, 'tempId)                    => x(5, s"$cur - $dataStmt - $arg"); (next, resolveStmts(stmts, arg, a, tempId(a)))
            case Add('e, a, 'arg)                         => x(6, s"$cur - $dataStmt - $arg"); (next, resolveStmts(stmts, lastE(stmts, a), a, arg))
            case Add('e, a, refNs: String)                => x(7, s"$cur - $dataStmt - $arg"); (cur, resolveStmts(stmts, lastE(stmts, a), a, tempId(refNs)))
            case Add('e, a, Values(EnumVal, prefix))      => x(8, s"$cur - $dataStmt - $arg"); (next, resolveStmts(stmts, lastE(stmts, a), a, arg, prefix))
            case Add('e, a, Values(vs, prefix))           => x(9, s"$cur - $dataStmt - $arg"); (next, resolveStmts(stmts, lastE(stmts, a), a, vs, prefix))
            case Add('v, a, 'arg)                         => x(10, s"$cur - $dataStmt - $arg"); (next, resolveStmts(stmts, stmts.last.v, a, arg))
            case Add('v, a, 'tempId)                      => x(11, s"$cur - $dataStmt - $arg"); (cur, resolveStmts(stmts, stmts.last.v, a, tempId(a)))
            case Add('v, a, Values(vs, prefix))           => x(12, s"$cur - $dataStmt - $arg"); (next, resolveStmts(stmts, stmts.last.v, a, vs, prefix))
            case Add('tx, a, 'arg)                        => x(13, s"$cur - $dataStmt - $arg"); (next, resolveStmts(stmts, tempId("tx"), a, arg))
            case Retract(e, a, v)                         => x(14, s"$cur - $dataStmt - $arg"); (cur, stmts)
            case Add(e0, ref0, nestedStmts0: Seq[_])      => {
              x(15, e0, ref0, nestedStmts0, cur, dataStmt, arg)
              def resolveNested(e: Any, ref: String, nestedStmts: Seq[Any], arg1: Any, lastE: Any): Seq[Statement] = {
                if (arg1 == Nil) {
                  Nil
                } else {
                  val parentId = if (e == 'parentId) tempId(ref) else lastE // todo: check that 'ref' is always in the right partition...
                  nestedArgss(nestedStmts, arg1).flatMap {nestedArgs =>
                    val nestedId = tempId(ref)
                    val bondStmt = Add(parentId, ref, nestedId)
                    val nestedStmts1 = nestedArgs.zip(nestedStmts).flatMap {
                      case (nestedArg, Add(e1, ref1, nestedStmts1: Seq[_])) => resolveNested(nestedId, ref1, nestedStmts1, nestedArg, nestedId)
                      case (null, _)                                        => Nil
                      case (nestedArg, Add(_, a, _))                        => resolveStmts(Seq(), nestedId, a, nestedArg)
                    }
                    bondStmt +: nestedStmts1
                  }
                }
              }
              val lastE = if (stmts.isEmpty) e0 else stmts.last.e
              val nestedInsertStmts: Seq[Statement] = resolveNested(e0, ref0, nestedStmts0, arg, lastE)
              (next, stmts ++ nestedInsertStmts)
            }
            case unexpected                               => sys.error("[Model2Transaction:insertStmts:dataStmts] Unexpected insert statement: " + unexpected)
          }
      }._2
    }
    val txId = tempId("tx")
    val txStmtss: Seq[Seq[Statement]] = Seq(txStmts.foldLeft(Seq[Statement]()) {
      case (stmts, Add('tx, a, Values(vs, prefix))) => resolveStmts(stmts, txId, a, vs, prefix)
      case (stmts, unexpected)                      => sys.error("[Model2Transaction:insertStmts:txStmts] Unexpected insert statement: " + unexpected)
    })
    x(26, model, stmtsModel, dataStmts, dataStmtss, txStmts, txStmtss, dataRows)
    dataStmtss ++ txStmtss
  }

  def saveStmts(): Seq[Statement] = stmtsModel.foldLeft(0, Seq[Statement]()) {case ((i, stmts), stmt) =>
    val j = i + 1
    stmt match {
      case Add('tempId, a, Values(vs, prefix)) => (j, resolveStmts(stmts, tempId(a), a, vs, prefix))
      case Add('e, a, Values(vs, prefix))      => (j, resolveStmts(stmts, lastE(stmts, a), a, vs, prefix))
      case Add('e, a, 'tempId)                 => (i, resolveStmts(stmts, lastE(stmts, a), a, tempId(a)))
      case Add('e, a, refNs: String)           => (i, resolveStmts(stmts, lastE(stmts, a), a, tempId(refNs)))
      case Add('v, a, Values(vs, prefix))      => (j, resolveStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
      case Retract(e, a, v)                    => (i, stmts)
      case Add(_, a, 'arg)                     => sys.error(s"[Model2Transaction:saveStmts] Attribute `$a` needs a value applied")
      case unexpected                          => sys.error("[Model2Transaction:saveStmts] Unexpected save statement: " + unexpected)
    }
  }._2


  def updateStmts(): Seq[Statement] = {
    val (dataStmts0, txStmts0) = splitStmts()
    x(27, model, stmtsModel)
    val dataStmts: Seq[Statement] = dataStmts0.foldLeft(0, Seq[Statement]()) {case ((i, stmts), stmt) =>
      val j = i + 1
      stmt match {
        case Add('e, a, Values(vs, prefix))     => (j, resolveStmts(stmts, lastE(stmts, a), a, vs, prefix))
        case Add('e, a, 'tempId)                => (i, resolveStmts(stmts, lastE(stmts, a), a, tempId(a)))
        case Add('v, a, Values(vs, prefix))     => (j, resolveStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
        case Add('tx, a, Values(vs, prefix))    => (j, resolveStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix))
        case Add(e, a, Values(vs, prefix))      => (j, resolveStmts(stmts, e, a, vs, prefix))
        case Retract('e, a, Values(vs, prefix)) => (j, resolveStmts(stmts, lastE(stmts, a), a, vs, prefix))
        case Retract(e, a, Values(vs, prefix))  => (j, resolveStmts(stmts, e, a, vs, prefix))
        case Add(_, a, 'arg)                    => sys.error(s"[Model2Transaction:updateStmts] Attribute `$a` needs a value applied")
        case unexpected                         => sys.error("[Model2Transaction:updateStmts] Unexpected update statement: " + unexpected)
      }
    }._2
    val txId = tempId("tx")
    val txStmts: Seq[Statement] = txStmts0.foldLeft(Seq[Statement]()) {
      case (stmts, Add('tx, a, Values(vs, prefix))) => resolveStmts(stmts, txId, a, vs, prefix)
      case (stmts, unexpected)                      => sys.error("[Model2Transaction:insertStmts:txStmts] Unexpected insert statement: " + unexpected)
    }
    x(28, model, stmtsModel, dataStmts0, dataStmts, txStmts0, txStmts)
    dataStmts ++ txStmts
  }


  private def nestedArgss(stmts: Seq[Any], arg0: Any): Seq[Seq[Any]] = {
    val (argArity, arg) = arg0 match {
      case a: Seq[_]  => a.head match {
        case p: Product => (p.productArity, a)
        case l: Seq[_]  => (l.size, a)
        case null       => sys.error("[Model2Transaction:nestedData] Please use `List()` instead of `List(null)` for missing nested values.")
        case _          => (1, a)
      }
      case unexpected => sys.error("[Model2Transaction:nestedData] Unexpected data: " + unexpected)
    }
    assert(argArity == stmts.size, s"[Model2Transaction:nestedData] Arity of statements and arguments should match. Found: \n" +
      s"Statements (arity ${stmts.size}): " + stmts.mkString("\n  ", "\n  ", "\n") +
      s"Argumewnts (arity $argArity): " + arg.mkString("\n  ", "\n  ", "\n"))

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