package molecule.core.transform

import java.util.Date
import java.util.concurrent.atomic.AtomicInteger
import molecule.core.ast.elements._
import molecule.core.util.Helpers
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.exception.Model2TransactionException
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/** Model to Statements transformer.
  *
  * */
abstract class GenericStmts(conn: Conn, model: Model) extends Helpers {

  protected def err(method: String, msg: String) = {
    throw Model2TransactionException(s"[$method]  $msg")
  }

  val datomicTx = "datomic.tx"

  // Save attribute cardinality/type info
  val attrInfo = conn.connProxy.attrMap + (":molecule_Meta/otherEdge" -> (1, "ref"))

  val genericStmts: Seq[Statement] = {

    def resolveTx(elements: Seq[Element]): Seq[Statement] = elements.foldLeft("tx": Any, Seq[Statement]()) {
      case (_, Atom(nsFull, name, _, _, VarValue, _, _, _)) => err("stmtsModel",
        s"Please apply transaction meta data directly to transaction attribute: `${nsFull.capitalize}.$name(<metadata>)`")
      case ((eSlot1, stmts1), element1)                     => resolveElement(eSlot1, stmts1, element1)
    }._2

    def bi(gs: Seq[GenericValue], card: Int) = gs.collectFirst {
      case bi: Bidirectional => bi
    } getOrElse Card(card)

    def resolveElement(eSlot: Any, stmts: Seq[Statement], element: Element): (Any, Seq[Statement]) = {
      (eSlot, element) match {
        // None
        case (eids@Eids(ids), Atom(nsFull, name, _, c, Fn("not", _), _, gs, _)) => (eids, stmts ++ ids.map(Retract(_, s":$nsFull/$name", Values(Eq(Nil)), bi(gs, c))))
        case (eid@Eid(id), Atom(nsFull, name, _, c, Fn("not", _), _, gs, _))    => (eid, stmts :+ Add(id, s":$nsFull/$name", Values(Eq(Nil)), bi(gs, c)))
        case (e, Atom(_, _, _, _, Fn("not", _), _, _, _))                       => (e, stmts)
        case (e, Atom(_, _, _, _, Eq(Seq(None)), _, _, _))                      => (e, stmts)

        // First
        case ("_", Generic(_, "e" | "e_", _, EntValue))            => ("__arg", stmts)
        case ("_", Generic(_, "e" | "e_", _, Eq(Seq(id: Long))))   => (Eid(id), stmts)
        case ("_", Generic(_, "e" | "e_", _, Eq(ids: Seq[_])))     => (Eids(ids), stmts)
        case ("_", Atom(nsFull, name, _, c, VarValue, _, gs, _))   => ("e", stmts :+ Add("__tempId", s":$nsFull/$name", "__arg", bi(gs, c)))
        case ("_", Atom(nsFull, name, _, c, value, prefix, gs, _)) => ("e", stmts :+ Add("__tempId", s":$nsFull/$name", Values(value, prefix), bi(gs, c)))
        case ("_", Bond(nsFull, refAttr, refNs, c, gs))            => ("v", stmts :+ Add("__tempId", s":$nsFull/$refAttr", s":$refNs", bi(gs, c)))

        case (e, Nested(Bond(nsFull, refAttr, _, c, gs), elements)) =>
          val parentId = if (e == "_") "parentId" else "e"
          val nested   = elements.foldLeft("v": Any, Seq[Statement]()) {
            case ((eSlot1, stmts1), element1) => resolveElement(eSlot1, stmts1, element1)
          }._2
          ("e", stmts :+ Add(parentId, s":$nsFull/$refAttr", nested, bi(gs, c)))

        case ("tx", Composite(elements)) =>
          val associated = elements.foldLeft("tx": Any, Seq[Statement]()) {
            case ((eSlot1, stmts1), element1) => resolveElement(eSlot1, stmts1, element1)
          }._2
          ("tx", stmts ++ associated)

        case (_, Composite(elements)) =>
          // Mark the first stmt as having the shared entity for all composite sub molecules
          // Then we can go back and pick up that entity as base for the newt sub-molecule
          val associated = elements.foldLeft("ec": Any, Seq[Statement]()) {
            case ((eSlot1, stmts1), element1) => resolveElement(eSlot1, stmts1, element1)
          }._2
          ("ec", stmts ++ associated)

        case ("ec", Generic(_, "e" | "e_", _, EntValue))          => ("__arg", stmts)
        case ("ec", Generic(_, "e" | "e_", _, Eq(Seq(id: Long)))) => (Eid(id), stmts)
        case ("ec", Generic(_, "e" | "e_", _, Eq(ids: Seq[_])))   => (Eids(ids), stmts)

        // Entity ids applied to initial namespace
        case (eids@Eids(ids), Atom(nsFull, name, _, c, value@RetractValue(_), prefix, gs, _)) => (eids, stmts ++ ids.map(Retract(_, s":$nsFull/$name", Values(value, prefix), bi(gs, c))))
        case (eids@Eids(ids), Atom(nsFull, name, _, c, value, prefix, gs, _))                 => (eids, stmts ++ ids.map(Add(_, s":$nsFull/$name", Values(value, prefix), bi(gs, c))))
        case (Eids(ids), Bond(nsFull, refAttr, refNs, c, gs))                                 => ("v", stmts ++ ids.map(Add(_, s":$nsFull/$refAttr", "__tempId", bi(gs, c))))

        // Entity id applied to initial namespace
        case (eid@Eid(id), Atom(nsFull, name, _, c, value@RetractValue(_), prefix, gs, _)) => (eid, stmts :+ Retract(id, s":$nsFull/$name", Values(value, prefix), bi(gs, c)))
        case (eid@Eid(id), Atom(nsFull, name, _, c, value, prefix, gs, _))                 => (eid, stmts :+ Add(id, s":$nsFull/$name", Values(value, prefix), bi(gs, c)))
        case (Eid(id), Bond(nsFull, refAttr, _, c, gs))                                    => ("v", stmts :+ Add(id, s":$nsFull/$refAttr", "__tempId", bi(gs, c)))


        // Same namespace
        case ("e", Atom(nsFull, name, _, c, value@RetractValue(_), prefix, gs, _))   => ("e", stmts :+ Retract("e", s":$nsFull/$name", Values(value, prefix), bi(gs, c)))
        case ("e", Atom(nsFull, name, _, c, VarValue, _, gs, _)) if name.last == '$' => ("e", stmts :+ Add("e", s":$nsFull/${name.init}", "__arg", bi(gs, c)))
        case ("e", Atom(nsFull, name, _, c, VarValue, _, gs, _))                     => ("e", stmts :+ Add("e", s":$nsFull/$name", "__arg", bi(gs, c)))
        case ("e", Atom(nsFull, name, _, c, value, prefix, gs, _))                   => ("e", stmts :+ Add("e", s":$nsFull/$name", Values(value, prefix), bi(gs, c)))
        case ("e", Bond(nsFull, refAttr, refNs, c, gs))                              => ("v", stmts :+ Add("e", s":$nsFull/$refAttr", s":$refNs", bi(gs, c)))

        // Same namespace - composite
        case ("ec", Atom(nsFull, name, _, c, value@RetractValue(_), prefix, gs, _))   => ("e", stmts :+ Retract("ec", s":$nsFull/$name", Values(value, prefix), bi(gs, c)))
        case ("ec", Atom(nsFull, name, _, c, VarValue, _, gs, _)) if name.last == '$' => ("e", stmts :+ Add("ec", s":$nsFull/${name.init}", "__arg", bi(gs, c)))
        case ("ec", Atom(nsFull, name, _, c, VarValue, _, gs, _))                     => ("e", stmts :+ Add("ec", s":$nsFull/$name", "__arg", bi(gs, c)))
        case ("ec", Atom(nsFull, name, _, c, value, prefix, gs, _))                   => ("e", stmts :+ Add("ec", s":$nsFull/$name", Values(value, prefix), bi(gs, c)))
        case ("ec", Bond(nsFull, refAttr, refNs, c, gs))                              => ("v", stmts :+ Add("ec", s":$nsFull/$refAttr", s":$refNs", bi(gs, c)))

        // Transaction annotations
        case ("_", TxMetaData(elements))     => ("e", stmts ++ resolveTx(elements))
        case ("e", TxMetaData(elements))     => ("e", stmts ++ resolveTx(elements))
        case (Eid(id), TxMetaData(elements)) => ("e", stmts ++ resolveTx(elements))

        // Continue with only transaction Atoms...
        case ("tx", Atom(nsFull, name, _, c, value, prefix, _, _)) if name.last == '_' || name.last == '$'    => ("tx", stmts :+ Add("tx", s":$nsFull/${name.init}", Values(value, prefix), Card(c)))
        case ("tx", Atom(nsFull, name, _, c, value, prefix, _, _))                                            => ("tx", stmts :+ Add("tx", s":$nsFull/$name", Values(value, prefix), Card(c)))
        case ("tx", Bond(nsFull, refAttr, refNs, c, gs))                                                      => ("txRef", stmts :+ Add("tx", s":$nsFull/$refAttr", s":$refNs", bi(gs, c)))
        case ("txRef", Bond(nsFull, refAttr, refNs, c, gs))                                                   => ("txRef", stmts :+ Add("txRef", s":$nsFull/$refAttr", s":$refNs", bi(gs, c)))
        case ("txRef", Atom(nsFull, name, _, c, value, prefix, _, _)) if name.last == '_' || name.last == '$' => ("txRef", stmts :+ Add("txRef", s":$nsFull/${name.init}", Values(value, prefix), Card(c)))
        case ("txRef", Atom(nsFull, name, _, c, value, prefix, _, _))                                         => ("txRef", stmts :+ Add("txRef", s":$nsFull/$name", Values(value, prefix), Card(c)))
        case ("tx" | "txRef", other)                                                                          =>
          err("stmtsModel", s"Transaction data can only have references and attributes with applied value:\nMODEL: $model \nFOUND: $other\nSTMTS: $stmts")

        // Next namespace
        case ("v", Atom(nsFull, name, _, c, VarValue, _, gs, _))   => ("e", stmts :+ Add("v", s":$nsFull/$name", "__arg", bi(gs, c)))
        case ("v", Atom(nsFull, name, _, c, value, prefix, gs, _)) => ("e", stmts :+ Add("v", s":$nsFull/$name", Values(value, prefix), bi(gs, c)))
        case ("v", Bond(nsFull, refAttr, _, c, gs))                => ("v", stmts :+ Add("v", s":$nsFull/$refAttr", "__tempId", bi(gs, c)))

        // Add one extra generic statement to receive the eid arg for the following statement to use
        // (we then discard that temporary statement from the value statements)
        case ("__arg", Atom(nsFull, name, _, c, VarValue, _, _, _))   => ("e", stmts :+ Add("remove_me", s":$nsFull/$name", "__arg", Card(c)) :+ Add("v", s":$nsFull/$name", "__arg", Card(c)))
        case ("__arg", Atom(nsFull, name, _, c, value, prefix, _, _)) => ("e", stmts :+ Add("remove_me", s":$nsFull/$name", "__arg", Card(c)) :+ Add("v", s":$nsFull/$name", Values(value, prefix), Card(c)))
        case ("__arg", Bond(nsFull, refAttr, _, c, gs))               => ("v", stmts :+ Add("__arg", s":$nsFull/$refAttr", "__tempId", bi(gs, c)))

        // BackRef
        case (_, ReBond(backRef)) => ("e", stmts :+ Add("nsFull", s":$backRef", "", NoValue))

        case (e, elem) => err("stmtsModel", s"Unexpected transformation:\nMODEL: $model \nPAIR: ($e, $elem)\nSTMTS: $stmts")
      }
    }

    def replace$(elements: Seq[Element]): Seq[Element] = elements map {
      case a@Atom(_, attr, _, _, _, Some(enumPrefix), _, _)
        if attr.last == '$' && enumPrefix.init.last == '$'       => a.copy(attr = attr.init, enumPrefix = Some(enumPrefix.init.init + "/"))
      case a@Atom(_, attr, _, _, _, _, _, _) if attr.last == '$' => a.copy(attr = attr.init)
      case b@Bond(_, attr, _, _, _) if attr.last == '$'          => b.copy(refAttr = attr.init)
      case Nested(ref, es)                                       => Nested(ref, replace$(es))
      case other                                                 => other
    }

    val model1 = Model(replace$(model.elements))
    model1.elements.foldLeft("_": Any, Seq.empty[Statement]) {
      case ((eSlot, stmts), element) => resolveElement(eSlot, stmts, element)
    }._2
  }

  protected def tempId(attr: String): AnyRef = attr match {
    case null => err("__tempId", "Attribute name unexpectedly null.")
    case "tx" => datomicTx
    case _    =>
      val nextId = conn.tempId.next
      if (attr.contains('_')) {
        // extract "partition" from ":partition_Namespace/attr"
        TempId(":" + attr.substring(1).split("(?=_)").head, nextId)
      } else {
        TempId(":db.part/user", nextId)
      }
  }

  protected def flatten(vs: Seq[Any]): Seq[Any] = vs.flatMap {
    case set: Set[_] => set.toSeq
    case v           => Seq(v)
  }

  protected def splitStmts(): (Seq[Statement], Seq[Statement]) = genericStmts.foldLeft(
    Seq.empty[Statement],
    Seq.empty[Statement]
  ) {
    case ((stmts, txStmts), txStmt@Add("tx" | "txRef", _, _, _)) => (stmts, txStmts :+ txStmt)
    case ((stmts, txStmts), stmt)                                => (stmts :+ stmt, txStmts)
  }

  protected def lastE(
    stmts: Seq[Statement],
    attr: String,
    forcedE: Any,
    bi: GenericValue,
    composite: Any = "e"
  ): Any = {
    bi match {
      case BiTargetRef(_, _) =>
        val lastEdgeNs = attr.split("/").head
        stmts.reverse.collectFirst {
          case Add(e: TempId, a, _, _) if a.startsWith(lastEdgeNs) => e
        } getOrElse err("lastE",
          s"Couldn't find namespace `$lastEdgeNs` in any previous Add statements:\n"
            + stmts.mkString("\n"))

      case _ =>
        if (forcedE != 0L) {
          forcedE
        } else if (stmts.isEmpty) {
          tempId(attr)
        } else if (
          stmts.last.e == -1 ||
            stmts.last.e.toString.startsWith("#db/id[:db.part/tx") &&
              stmts.last.v.toString.startsWith("#db/id[:db.part/")
        ) {
          stmts.last.v
        } else if (composite == "ec") {
          stmts.head.e
        } else {
          stmts.last.e
        }
    }
  }

  protected def lastV(stmts: Seq[Statement], attr: String, forcedE: Any = 0L): Any = {
    if (forcedE != 0L)
      forcedE
    else if (stmts.isEmpty)
      tempId(attr)
    else
      stmts.last.v
  }

  protected def eidV(stmts: Seq[Statement]): Boolean = stmts.nonEmpty && stmts.last.v.isInstanceOf[TempId]

  protected def untupleNestedArgss(stmts: Seq[Any], arg0: Any): Seq[Seq[Any]] = {
    val (argArity, arg) = arg0 match {
      case a: Seq[_]  => a.head match {
        case None       =>
          err("untupleNestedArgss", "Please use `List()` instead of `List(None)` for nested null values.")
        case null       =>
          err("untupleNestedArgss", "Please use `List()` instead of `List(null)` for nested null values.")
        case p: Product => (p.productArity, a)
        case l: Seq[_]  => (l.size, a)
        case _          => (1, a)
      }
      case unexpected => err("untupleNestedArgss", "Unexpected data: " + unexpected)
    }
    val argStmts        = stmts.collect {
      case a@Add(_, _, "__arg", _)             => a
      case a@Add(_, _, Values(_, _), _)        => a
      case a@Add(_, _, nestedStmts: Seq[_], _) => a
    }
    val stmtsSize       = argStmts.size
    if (argArity != stmtsSize)
      err("untupleNestedArgss", "Arity of statements and arguments should match. Found: \n" +
        s"Statements (arity $stmtsSize): " + stmts.mkString("\n  ", "\n  ", "\n") +
        s"Arguments0                  : " + arg0 +
        s"Arguments  (arity $argArity): " + arg.mkString("\n  ", "\n  ", "\n"))

    arg map tupleToSeq
  }

  protected def txRefAttr(stmts: Seq[Statement]): Boolean = stmts.nonEmpty && stmts.last.v.isInstanceOf[TempId]
}
