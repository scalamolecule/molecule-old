package molecule.transform
import java.util.Date

import datomic._
import molecule.ast.model._
import molecule.ast.transaction.{RetractEntity, _}
import molecule.facade.{Conn, EntityFacade}
import molecule.util.{Debug, Helpers}

import scala.collection.JavaConverters._


case class Model2Transaction(conn: Conn, model: Model) extends Helpers {
  val x = Debug("Model2Transaction", 1, 51, false, 6)

  private def iae(method: String, msg: String) = {
    throw new IllegalArgumentException(s"[molecule.transform.Model2Transaction.$method]  $msg")
  }

  val stmtsModel: Seq[Statement] = {

    def resolveTx(elements: Seq[Element]) = elements.foldLeft('tx: Any, Seq[Statement]()) {
      case ((eSlot1, stmts1), a@Atom(ns, name, _, _, VarValue, _, _, _)) => iae("stmtsModel",
        s"Please apply transaction meta data directly to transaction attribute: `${ns.capitalize}.$name(<metadata>)`")
      case ((eSlot1, stmts1), element1)                                  => resolveElement(eSlot1, stmts1, element1)
    }._2

    def bi(gs: Seq[Generic], card: Int) = gs.collectFirst {
      case bi: Bidirectional => bi
    } getOrElse Card(card)

    def resolveElement(eSlot: Any, stmts: Seq[Statement], element: Element): (Any, Seq[Statement]) = (eSlot, element) match {

      // None
      case (eids@Eids(ids), Atom(ns, name, _, c, Fn("not", _), _, gs, _)) => (eids, stmts ++ ids.map(Retract(_, s":$ns/$name", Values(Eq(Nil)), bi(gs, c))))
      case (eid@Eid(id), Atom(ns, name, _, c, Fn("not", _), _, gs, _))    => (eid, stmts :+ Add(id, s":$ns/$name", Values(Eq(Nil)), bi(gs, c)))
      case (e, Atom(_, _, _, _, Fn("not", _), _, _, _))                   => (e, stmts)
      case (e, Atom(_, _, _, _, Eq(Seq(None)), _, _, _))                  => (e, stmts)

      case ('_, Meta(ns, _, "e", _, EntValue))              => ('arg, stmts)
      case ('_, Meta(ns, _, "e", _, Eq(Seq(id: Long))))     => (Eid(id), stmts)
      case ('_, Meta(ns, _, "e", _, Eq(ids: Seq[_])))       => (Eids(ids), stmts)
      case ('_, Atom(ns, name, _, c, VarValue, _, gs, _))   => ('e, stmts :+ Add('tempId, s":$ns/$name", 'arg, bi(gs, c)))
      case ('_, Atom(ns, name, _, c, value, prefix, gs, _)) => ('e, stmts :+ Add('tempId, s":$ns/$name", Values(value, prefix), bi(gs, c)))
      case ('_, Bond(ns, refAttr, refNs, c, gs))            => ('v, stmts :+ Add('tempId, s":$ns/$refAttr", s":$refNs", bi(gs, c)))

      case (e, Nested(Bond(ns, refAttr, _, c, gs), elements)) =>
        val parentId = if (e == '_) 'parentId else 'e
        val nested = elements.foldLeft('v: Any, Seq[Statement]()) {
          case ((eSlot1, stmts1), element1) =>
            resolveElement(eSlot1, stmts1, element1)
        }._2
        ('e, stmts :+ Add(parentId, s":$ns/$refAttr", nested, bi(gs, c)))

      // Entity ids applied to initial namespace
      case (eids@Eids(ids), Atom(ns, name, _, c, value@Remove(_), prefix, gs, _)) => (eids, stmts ++ ids.map(Retract(_, s":$ns/$name", Values(value, prefix), bi(gs, c))))
      case (eids@Eids(ids), Atom(ns, name, _, c, value, prefix, gs, _))           => (eids, stmts ++ ids.map(Add(_, s":$ns/$name", Values(value, prefix), bi(gs, c))))
      case (Eids(ids), Bond(ns, refAttr, refNs, c, gs))                           => ('v, stmts ++ ids.map(Add(_, s":$ns/$refAttr", 'tempId, bi(gs, c))))

      // Entity id applied to initial namespace
      case (eid@Eid(id), Atom(ns, name, _, c, value@Remove(_), prefix, gs, _)) => (eid, stmts :+ Retract(id, s":$ns/$name", Values(value, prefix), bi(gs, c)))
      case (eid@Eid(id), Atom(ns, name, _, c, value, prefix, gs, _))           => (eid, stmts :+ Add(id, s":$ns/$name", Values(value, prefix), bi(gs, c)))
      case (Eid(id), Bond(ns, refAttr, refNs, c, gs))                          => ('v, stmts :+ Add(id, s":$ns/$refAttr", 'tempId, bi(gs, c)))


      // Same namespace
      case ('e, Atom(ns, name, _, c, value@Remove(_), prefix, gs, _)) => ('e, stmts :+ Retract('e, s":$ns/$name", Values(value, prefix), bi(gs, c)))
      case ('e, Atom(ns, name, _, c, VarValue, _, gs, _))             => ('e, stmts :+ Add('e, s":$ns/$name", 'arg, bi(gs, c)))
      case ('e, Atom(ns, name, _, c, value, prefix, gs, _))           => ('e, stmts :+ Add('e, s":$ns/$name", Values(value, prefix), bi(gs, c)))
      case ('e, Bond(ns, refAttr, refNs, c, gs))                      => ('v, stmts :+ Add('e, s":$ns/$refAttr", s":$refNs", bi(gs, c)))

      // Transaction annotations
      case ('_, TxMetaData(elements))       => ('e, stmts ++ resolveTx(elements))
      case ('e, TxMetaData(elements))       => ('e, stmts ++ resolveTx(elements))
      case ('_, TxMetaData_(elements))      => ('e, stmts ++ resolveTx(elements))
      case ('e, TxMetaData_(elements))      => ('e, stmts ++ resolveTx(elements))
      case (Eid(id), TxMetaData(elements))  => ('e, stmts ++ resolveTx(elements))
      case (Eid(id), TxMetaData_(elements)) => ('e, stmts ++ resolveTx(elements))

      // Continue with only transaction Atoms...
      case ('tx, Atom(ns, name, _, c, VarValue, _, _, _))                                           => ('e, stmts :+ Add('e, s":$ns/$name", 'arg, Card(c)))
      case ('tx, Atom(ns, name, _, c, value, prefix, _, _)) if name.last == '_' || name.last == '$' => ('tx, stmts :+ Add('tx, s":$ns/${name.init}", Values(value, prefix), Card(c)))
      case ('tx, Atom(ns, name, _, c, value, prefix, _, _))                                         => ('tx, stmts :+ Add('tx, s":$ns/$name", Values(value, prefix), Card(c)))

      // Next namespace
      case ('v, Atom(ns, name, _, c, VarValue, _, gs, _))   => ('e, stmts :+ Add('v, s":$ns/$name", 'arg, bi(gs, c)))
      case ('v, Atom(ns, name, _, c, value, prefix, gs, _)) => ('e, stmts :+ Add('v, s":$ns/$name", Values(value, prefix), bi(gs, c)))
      case ('v, Bond(ns, refAttr, _, c, gs))                => ('v, stmts :+ Add('v, s":$ns/$refAttr", 'tempId, bi(gs, c)))

      // Add one extra generic statement to receive the eid arg for the following statement to use
      // (we then discard that temporary statement from the value statements)
      case ('arg, Atom(ns, name, _, c, VarValue, _, _, _))   => ('e, stmts :+ Add('remove_me, s":$ns/$name", 'arg, Card(c)) :+ Add('v, s":$ns/$name", 'arg, Card(c)))
      case ('arg, Atom(ns, name, _, c, value, prefix, _, _)) => ('e, stmts :+ Add('remove_me, s":$ns/$name", 'arg, Card(c)) :+ Add('v, s":$ns/$name", Values(value, prefix), Card(c)))
      case ('arg, Bond(ns, refAttr, _, c, gs))               => ('v, stmts :+ Add('arg, s":$ns/$refAttr", 'tempId, bi(gs, c)))

      // BackRef
      case (_, ReBond(ns, _, _, _, _)) => ('e, stmts :+ Add('ns, s":$ns", "", NoValue))

      case (e, c: Composite) => iae("stmtsModel", s"Composites are only for getting data:\nMODEL: $model \nPAIR: ($e, $c)\nSTMTS: $stmts")
      case (e, elem)         => iae("stmtsModel", s"Unexpected transformation:\nMODEL: $model \nPAIR: ($e, $elem)\nSTMTS: $stmts")
    }

    def replace$(elements: Seq[Element]): Seq[Element] = elements map {
      case a@Atom(_, attr, _, _, _, Some(enumPrefix), _, _)
        if attr.last == '$' && enumPrefix.init.last == '$'       => a.copy(name = attr.init, enumPrefix = Some(enumPrefix.init.init + "/"))
      case a@Atom(_, attr, _, _, _, _, _, _) if attr.last == '$' => a.copy(name = attr.init)
      case b@Bond(_, attr, _, _, _) if attr.last == '$'          => b.copy(refAttr = attr.init)
      case t@Transitive(_, attr, _, _, _) if attr.last == '$'    => t.copy(refAttr = attr.init)
      case Nested(ref, es)                                       => Nested(ref, replace$(es))
      case other                                                 => other
    }

    val model1 = Model(replace$(model.elements))
    //    x(50, model, model1)
    model1.elements.foldLeft('_: Any, Seq[Statement]()) {
      case ((eSlot, stmts), element) => resolveElement(eSlot, stmts, element)
    }._2
  }

  private def tempId(attr: String): AnyRef = attr match {
    case null                 => iae("tempId", "Attribute name unexpectedly null.")
    case "tx"                 => Peer.tempid(s":db.part/tx")
    case s if s.contains('_') => Peer.tempid(s":" + attr.substring(1).split("(?=_)").head) // extract "partition" from ":partition_Namespace/attr"
    case _                    => Peer.tempid(s":db.part/user")
  }

  // Lookup if key is already populated
  def pairStrs(e: Any, a: String, key: String) = {
    val query = s"[:find ?v :in $$ ?e ?a ?key :where [?e ?a ?v][(.startsWith ^String ?v ?key)]]"
    Peer.q(query, conn.db, e.asInstanceOf[Object], a.asInstanceOf[Object], key.asInstanceOf[Object]).asScala.map(_.get(0))
  }

  def getPairs(e: Any, a: String, key: String = "") = {
    val strs = if (key.isEmpty) {
      val query = s"[:find ?v :in $$ ?e ?a :where [?e ?a ?v]]"
      val result = Peer.q(query, conn.db, e.asInstanceOf[Object], a.asInstanceOf[Object]).asScala.map(_.get(0))
      result
    } else {
      val query = s"[:find ?v :in $$ ?e ?a ?key :where [?e ?a ?v][(.startsWith ^String ?v ?key)]]"
      val result = Peer.q(query, conn.db, e.asInstanceOf[Object], a.asInstanceOf[Object], key.asInstanceOf[Object]).asScala.map(_.get(0))
      result
    }
    strs.map {
      case str: String =>
        val Seq(key: String, value: String) = str.split("@", 2).toSeq
        key -> value
    }.toMap
  }


  private def valueStmts(
                          stmts: Seq[Statement],
                          e: Any,
                          a: String,
                          arg: Any,
                          prefix: Option[String],
                          bi: Generic,
                          otherEdgeId: Option[AnyRef])
  : Seq[Statement] = {

    def p(arg: Any) = if (prefix.isDefined) prefix.get + arg else arg

    def d(v: Any) = v match {
      case d: Date => format2(d)
      case other   => other
    }

    def attrValues(id: Any, attr: String) = {
      val query = if (prefix.isDefined)
        s"""[:find ?enums
           | :in $$ ?id
           | :where [?id $attr ?a]
           |        [?a :db/ident ?b]
           |        [(.getName ^clojure.lang.Keyword ?b) ?enums]
           |]""".stripMargin
      else
        s"[:find ?values :in $$ ?id :where [?id $attr ?values]]"

      Peer.q(query, conn.db, id.asInstanceOf[Object]).asScala.map(_.get(0))
    }

    def edgeAB(edge1: Any, targetAttr: String) = {
      val edge2 = otherEdge(edge1)

      // Edges to other ns already has one ref extra in one direction
      val s1 = conn.db.entity(edge1).keySet().size()
      val s2 = conn.db.entity(edge2).keySet().size()

      if (s1 > s2)
        (edge1, edge2)
      else if (s1 < s2)
        (edge2, edge1)
      else if (conn.db.entity(edge1).get(targetAttr) == null)
        (edge2, edge1)
      else
        (edge1, edge2)
    }

    def otherEdge(edgeA: Any): AnyRef = {
      val query = s"[:find ?edgeB :in $$ ?edgeA :where [?edgeA :molecule_Meta/otherEdge ?edgeB]]"
      val result = Peer.q(query, conn.db, edgeA.asInstanceOf[Object]).asScala.map(_.get(0))
      result match {
        case Seq(edgeB) => edgeB
        case Nil        =>
          val otherId = EntityFacade(conn.db.entity(edgeA), conn, edgeA.asInstanceOf[Object])
          iae("valueStmts:biEdgeRef", s"Supplied id $edgeA doesn't appear to be a property edge id (couldn't find reverse edge id). " +
            s"Could it be another entity?:\n" + otherId.touchQ(2) +
            s"\nSpooky id: $otherId" +
            "\n" + stmts.size + " statements so far:\n" + stmts.mkString("\n")
          )
        case ids        =>
          iae("valueStmts:biEdgeRef", "Unexpectedly found multiple reverse edge ids:\n" + ids.mkString("\n"))
      }
    }

    def checkDupKeys(pairs: Seq[(String, Any)], host: String, op: String) {
      val dupKeys: Seq[String] = pairs.map(_._1).groupBy(identity).collect { case (key, keys) if keys.size > 1 => key }.toSeq
      if (dupKeys.nonEmpty) {
        val dupPairs = pairs.filter(p => dupKeys.contains(p._1)).sortBy(_._1).map { case (k, v) => s"$k -> $v" }
        iae(s"valueStmts:$host", s"Can't $op multiple key/value pairs with the same key for attribute `$a`:\n" + dupPairs.mkString("\n"))
      }
    }

    def checkDupValuesOfPairs(pairs: Seq[(Any, Any)], host: String, op: String) {
      val dups = pairs.map(_._2).groupBy(identity).collect { case (v, vs) if vs.size > 1 => v }
      if (dups.nonEmpty)
        iae(s"valueStmts:$host", s"Can't replace with duplicate new values of attribute `$a`:\n" + dups.mkString("\n"))
    }

    def checkDupValues(values: Seq[Any], host: String, op: String) {
      val dups = values.groupBy(identity).collect { case (v, vs) if vs.size > 1 => v }
      if (dups.nonEmpty)
        iae(s"valueStmts:$host", s"Can't $op duplicate new values to attribute `$a`:\n" + dups.mkString("\n"))
    }

    def checkUnknownKeys(newPairs: Seq[(String, Any)], curKeys: Seq[String], host: String) {
      val unknownKeys = newPairs.map(_._1).diff(curKeys)
      if (unknownKeys.nonEmpty)
        iae(s"valueStmts:$host", s"Can't replace non-existing keys of map attribute `$a`:\n" + unknownKeys.mkString("\n") +
          "\nYou might want to apply the values instead to replace all current values?")
    }


    // Bidirectional self-ref operations ------------------------------------------------------------------

    def biSelf(card: Int) = arg match {

      case Add_(refs) => refs.flatMap { case ref: Long =>
        val reverseRetracts = if (card == 1) attrValues(e, a).toSeq.map(revRef => Retract(revRef, a, e)) else Nil
        if (ref == e) iae("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same")
        reverseRetracts ++ Seq(Add(ref, a, e, Card(card)), Add(e, a, ref, Card(card)))
      }

      case Replace(oldNew) => attrValues(e, a).toSeq.flatMap { case revRef =>
        oldNew.flatMap {
          case (oldRef, newRef) if oldRef == revRef => Seq(
            // This entity e now has ref to newRef instead of oldRef
            Retract(e, a, oldRef), Add(e, a, newRef, Card(card)),
            // RevRef no longer has a ref to this entity e
            // Instead newRef has a ref to this entity e
            Retract(revRef, a, e), Add(newRef, a, e, Card(card))
          )
          case _                                    => Nil
        }
      }

      case Remove(removeRefs) => removeRefs.flatMap { case ref: Long => Seq(Retract(ref, a, e), Retract(e, a, ref)) }

      case Eq(newRefs) => {
        if (newRefs.contains(e)) iae("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")
        val oldRefs = attrValues(e, a).toSeq
        val obsoleteRetracts = oldRefs.flatMap {
          case oldRef if newRefs.contains(oldRef) => Nil
          case obsoleteRef                        => Seq(Retract(obsoleteRef, a, e), Retract(e, a, obsoleteRef))
        }
        val newAdds = newRefs.flatMap {
          case newRef if oldRefs.contains(newRef) => Nil
          case newRef                             => Seq(Add(newRef, a, e, Card(card)), Add(e, a, newRef, Card(card)))
        }
        obsoleteRetracts ++ newAdds
      }

      case refs: Set[_] => refs.flatMap { case ref: Long => Seq(Add(ref, a, e, Card(card)), Add(e, a, ref, Card(card))) }
      case ref          => {
        val reverseRetracts = if (card == 1) attrValues(e, a).toSeq.map(revRef => Retract(revRef, a, e)) else Nil
        reverseRetracts ++ Seq(Add(ref, a, e, Card(card)), Add(e, a, ref, Card(card)))
      }
    }


    // Bidirectional other-ref operations ------------------------------------------------------------------

    def biOther(card: Int, revRefAttr: String) = arg match {

      case Add_(refs) => refs.flatMap { case ref: Long =>
        if (ref == e) iae("valueStmts:biOther", "Current entity and referenced entity ids can't be the same")
        val reverseRetracts = if (card == 1) attrValues(e, a).toSeq.map(revRef => Retract(revRef, a, e)) else Nil
        reverseRetracts ++ Seq(Add(ref, revRefAttr, e, Card(card)), Add(e, a, ref, Card(card)))
      }

      case Replace(oldNew) => attrValues(e, a).toSeq.flatMap { case revRef =>
        oldNew.flatMap {
          case (oldRef, newRef) if oldRef == revRef => Seq(
            // This entity e now has ref to newRef instead of oldRef
            Retract(e, a, oldRef), Add(e, a, newRef, Card(card)),
            // RevRef no longer has a ref to this entity e
            // Instead newRef has a ref to this entity e
            Retract(revRef, revRefAttr, e, Card(card)), Add(newRef, revRefAttr, e, Card(card))
          )
          case _                                    => Nil
        }
      }

      case Remove(removeRefs) => removeRefs.flatMap { case ref: Long => Seq(Retract(ref, revRefAttr, e), Retract(e, a, ref)) }

      case Eq(newRefs) => {
        if (newRefs.contains(e)) iae("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")
        val oldRefs = attrValues(e, a).toSeq
        val obsoleteRetracts = oldRefs.flatMap {
          case oldRef if newRefs.contains(oldRef) => Nil
          case obsoleteRef                        => Seq(Retract(obsoleteRef, revRefAttr, e), Retract(e, a, obsoleteRef))
        }
        val newAdds = newRefs.flatMap {
          case newRef if oldRefs.contains(newRef) => Nil
          case newRef                             => Seq(Add(newRef, revRefAttr, e, Card(card)), Add(e, a, newRef, Card(card)))
        }
        obsoleteRetracts ++ newAdds
      }

      case refs: Set[_] => refs.flatMap { case ref: Long => Seq(Add(ref, revRefAttr, e, Card(card)), Add(e, a, ref, Card(card))) }
      case ref          => {
        val reverseRetracts = if (card == 1) attrValues(e, a).toSeq.map(revRef => Retract(revRef, revRefAttr, e)) else Nil
        reverseRetracts ++ Seq(Add(ref, revRefAttr, e, Card(card)), Add(e, a, ref, Card(card)))
      }
    }


    // Bidirectional edge operations -------------------------------------------------------------------

    def biEdgeRefAttr(card: Int, targetAttr: String) = arg match {

      case Add_(edges) =>
        if (edges.contains(e))
          iae("valueStmts:biEdgeRefAttr", "Current entity and referenced entity ids can't be the same.")
        checkDupValues(edges, "biEdgeRefAttr", "add")
        edges.flatMap { edge =>
          val (edgeA, edgeB) = edgeAB(edge, targetAttr)
          Seq(Add(edgeB, targetAttr, e, Card(card)), Add(e, a, edgeA, Card(card)))
        }

      case Replace(oldNewEdges) =>
        checkDupValuesOfPairs(oldNewEdges, "biEdgeRefAttr", "replace")
        oldNewEdges.flatMap { case (oldEdge, newEdge) =>
          val (edgeA, edgeB) = edgeAB(newEdge, targetAttr)
          Seq(
            RetractEntity(oldEdge),
            Add(edgeB, targetAttr, e, Card(card)),
            Add(e, a, edgeA, Card(card))
          )
        }

      case Remove(edges) => edges map RetractEntity

      case Eq(newEdges) =>
        if (newEdges.contains(e))
          iae("valueStmts:biEdgeRefAttr", "Current entity and referenced entity ids can't be the same.")
        checkDupValues(newEdges, "biEdgeRefAttr", "apply")
        val oldEdges = attrValues(e, a).toSeq
        val retracts = oldEdges.flatMap {
          case oldEdgeA if newEdges.contains(oldEdgeA) => Nil
          case oldEdgeA                                => Seq(RetractEntity(oldEdgeA))
        }
        val adds = newEdges.flatMap {
          case edge if oldEdges.contains(edge) => Nil
          case edge                            =>
            val (edgeA, edgeB) = edgeAB(edge, targetAttr)
            Seq(
              Add(edgeB, targetAttr, e, Card(card)),
              Add(e, a, edgeA, Card(card))
            )
        }
        retracts ++ adds

      case edges: Set[_] if card == 2 => edges.flatMap { case edge: Long =>
        val (edgeA, edgeB) = edgeAB(edge, targetAttr)
        Seq(Add(edgeB, targetAttr, e, Card(card)), Add(e, a, edgeA, Card(card)))
      }

      case edge: Long =>
        val (edgeA, edgeB) = edgeAB(edge, targetAttr)
        val reverseRetracts = if (card == 1) attrValues(e, a).toSeq.map(oldEdgeA => RetractEntity(oldEdgeA)) else Nil
        reverseRetracts ++ Seq(Add(edgeB, targetAttr, e, Card(card)), Add(e, a, edgeA, Card(card)))
    }


    def biEdgeRef(card: Int, targetAttr: String) = arg match {

      case Remove(edges) => edges map RetractEntity

      case Eq(newEdges) =>
        if (newEdges.contains(e))
          iae("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")
        checkDupValues(newEdges, "biEdgeRef", "apply")
        val oldEdges = attrValues(e, a).toSeq
        val retracts = oldEdges.flatMap {
          case oldEdgeA if newEdges.contains(oldEdgeA) => Nil
          case oldEdgeA                                => Seq(RetractEntity(oldEdgeA))
        }
        val adds = newEdges.flatMap {
          case edge if oldEdges.contains(edge) => Nil
          case edge                            =>
            val (edgeA, edgeB) = edgeAB(edge, targetAttr)
            Seq(
              Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
              Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card)),
              Add(edgeB, targetAttr, e, Card(card)),
              Add(e, a, edgeA, Card(card))
            )
        }
        retracts ++ adds

      case edgeA => {
        val edgeB = otherEdgeId getOrElse iae("valueStmts:biEdgeRef", "Missing id of other edge.")
        val reverseRetracts = if (card == 1) attrValues(e, a).toSeq.map(oldEdgeA => RetractEntity(oldEdgeA)) else Nil
        reverseRetracts ++ Seq(
          // Interlink edge entities so that we later know which other one to update
          Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
          Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card)),
          Add(edgeB, targetAttr, e, Card(card)),
          Add(e, a, edgeA, Card(card))
        )
      }
    }


    def biEdgeProp(card: Int) = {

      val edgeA = e
      val edgeB = otherEdgeId match {
        case Some(eid) if eid == edgeA => iae("valueStmts:biEdgeProp", "Other edge id is unexpectedly the same as this edge id.")
        case Some(eid)                 => eid
        case None                      => otherEdge(edgeA)
      }

      val stmt = arg match {

        case MapAdd(newPairs) =>
          checkDupKeys(newPairs, "biEdgeProp", "add")
          //          val curPairs: Map[(String, String), String] = getPairs(edgeA, a)
          val curPairs = getPairs(edgeA, a)
          val curKeys = curPairs.keys
          newPairs.flatMap {
            case (k, v) if curPairs.asJavaCollection.contains((k, d(v).toString)) => Nil
            case (k, v) if curKeys.asJavaCollection.contains(k)                   => Seq(
              Retract(edgeB, a, k + "@" + curPairs(k)), Add(edgeB, a, k + "@" + d(v), Card(card)),
              Retract(edgeA, a, k + "@" + curPairs(k)), Add(edgeA, a, k + "@" + d(v), Card(card))
            )
            case (k, v)                                                           => Seq(
              Add(edgeB, a, k + "@" + d(v), Card(card)),
              Add(edgeA, a, k + "@" + d(v), Card(card))
            )
          }

        case MapReplace(newPairs) =>
          checkDupKeys(newPairs, "biEdgeProp", "replace")
          val curPairs = getPairs(edgeA, a)
          val curKeys = curPairs.keys
          checkUnknownKeys(newPairs, curKeys.toSeq, "biEdgeProp")
          newPairs.flatMap {
            case (k, v) if curPairs.asJavaCollection.contains((k, d(v).toString)) => Nil
            case (k, v) if curKeys.asJavaCollection.contains(k)                   => Seq(
              Retract(edgeB, a, k + "@" + curPairs(k)), Add(edgeB, a, k + "@" + d(v), Card(card)),
              Retract(edgeA, a, k + "@" + curPairs(k)), Add(edgeA, a, k + "@" + d(v), Card(card))
            )
            case (k, v)                                                           => Seq(
              Add(edgeB, a, k + "@" + d(v), Card(card)),
              Add(edgeA, a, k + "@" + d(v), Card(card))
            )
          }

        case MapRemove(removeKeys0) =>
          val removeKeys = removeKeys0.distinct
          val oldPairs = getPairs(edgeA, a)
          oldPairs.flatMap {
            case (oldK, oldV) if removeKeys.contains(oldK) => Seq(Retract(edgeB, a, oldK + "@" + oldV), Retract(edgeA, a, oldK + "@" + oldV))
            case (oldK, oldV)                              => Nil
          }

        case MapEq(newPairs) =>
          checkDupKeys(newPairs, "biEdgeProp", "apply")
          edgeA match {
            case updateE: Long => {
              val oldPairs = getPairs(edgeA, a)
              val newPairs1 = newPairs.map { case (k, v) => (k, d(v).toString) }
              val obsoleteRetracts = oldPairs.flatMap {
                case (oldK, oldV) if newPairs1.contains((oldK, oldV)) => Nil
                case (oldK, oldV)                                     => Seq(Retract(edgeB, a, oldK + "@" + oldV), Retract(edgeA, a, oldK + "@" + oldV))
              }
              val newAdds = newPairs.flatMap { case (k, v) => Seq(
                Add(edgeB, a, k + "@" + d(v), Card(card)),
                Add(edgeA, a, k + "@" + d(v), Card(card))
              )
              }
              obsoleteRetracts ++ newAdds
            }
            case newE          =>
              newPairs.flatMap { case (k, v) => Seq(
                Add(edgeB, a, k + "@" + d(v), Card(card)),
                Add(edgeA, a, k + "@" + d(v), Card(card))
              )
              }
          }

        case Add_(values) =>
          checkDupValues(values, "biEdgeProp", "add")
          values.flatMap(v => Seq(Add(edgeB, a, p(v), Card(card)), Add(edgeA, a, p(v), Card(card))))

        case Replace(oldNew) =>
          checkDupValuesOfPairs(oldNew, "biEdgeProp", "replace")
          oldNew.flatMap { case (oldValue, newValue) => Seq(
            Retract(edgeB, a, p(oldValue)), Add(edgeB, a, p(newValue), Card(card)),
            Retract(edgeA, a, p(oldValue)), Add(edgeA, a, p(newValue), Card(card)))
          }

        case Remove(removeValues) =>
          removeValues.distinct.flatMap(v => Seq(Retract(edgeB, a, p(v)), Retract(edgeA, a, p(v))))

        case Eq(newValues) =>
          if (newValues.contains(e))
            iae("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")
          checkDupValues(newValues, "biEdgeProp", "apply")
          val curValues = attrValues(edgeA, a).toSeq
          val obsoleteRetracts = curValues.flatMap {
            case curValue if newValues.contains(curValue) => Nil
            case obsoleteValue                            => Seq(Retract(edgeB, a, p(obsoleteValue)), Retract(edgeA, a, p(obsoleteValue)))
          }
          val newAdds = newValues.flatMap {
            case newValue if curValues.contains(newValue) => Nil
            case newValue                                 => Seq(
              Add(edgeB, a, p(newValue), Card(card)),
              Add(edgeA, a, p(newValue), Card(card))
            )
          }
          obsoleteRetracts ++ newAdds

        case m: Map[_, _] => m.flatMap { case (k, v) => Seq(Add(edgeB, a, k + "@" + d(v), Card(card)), Add(edgeA, a, k + "@" + d(v), Card(card))) }
        case vs: Set[_]   => vs.toSeq.flatMap(v => Seq(Add(edgeB, a, p(v), Card(card)), Add(edgeA, a, p(v), Card(card))))
        case v :: Nil     => Seq(Add(edgeB, a, p(v), Card(card)), Add(edgeA, a, p(v), Card(card)))
        case vs: List[_]  => vs.flatMap(v => Seq(Add(edgeB, a, p(v), Card(card)), Add(edgeA, a, p(v), Card(card))))
        case v            =>
          Seq(Add(edgeB, a, p(v), Card(card)), Add(edgeA, a, p(v), Card(card)))
      }

      val edgeConnectors = stmts.collectFirst {
        case Add(_, ":molecule_Meta/otherEdge", _, _) => Nil
      } getOrElse Seq(
        Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
        Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card))
      )
      edgeConnectors ++ stmt
    }

    def biTarget(card: Int, biEdgeRefAttr: String) = {
      val edgeA = e
      val edgeB = otherEdgeId getOrElse iae("valueStmts:biTargetRef", "Missing id of other edge.")
      arg match {
        case Eq(biTargetRef :: Nil) => Seq(
          Add(biTargetRef, biEdgeRefAttr, edgeB, Card(card)),
          Add(edgeA, a, biTargetRef, Card(card))
        )
        case biTargetRef            => Seq(
          Add(biTargetRef, biEdgeRefAttr, edgeB, Card(card)),
          Add(edgeA, a, biTargetRef, Card(card))
        )
      }
    }

    // Default operations -------------------------------------------------------------------

    def default(card: Int) = arg match {

      case MapAdd(newPairs) =>
        checkDupKeys(newPairs, "default", "add")
        val curPairs = getPairs(e, a)
        val curKeys = curPairs.keys
        newPairs.flatMap {
          case (k, v) if curPairs.asJavaCollection.contains((k, d(v).toString)) => Nil
          case (k, v) if curKeys.asJavaCollection.contains(k)                   => Seq(Retract(e, a, k + "@" + curPairs(k)), Add(e, a, k + "@" + d(v), Card(card)))
          case (k, v)                                                           => Seq(Add(e, a, k + "@" + d(v), Card(card)))
        }

      case MapReplace(newPairs) =>
        checkDupKeys(newPairs, "default", "replace")
        val curPairs = getPairs(e, a)
        val curKeys = curPairs.keys.toSeq
        checkUnknownKeys(newPairs, curKeys, "default")
        newPairs.flatMap {
          case (k, v) if curPairs.asJavaCollection.contains((k, d(v).toString)) => Nil
          case (k, v) if curKeys.asJavaCollection.contains(k)                   => Seq(Retract(e, a, k + "@" + curPairs(k)), Add(e, a, k + "@" + d(v), Card(card)))
          case (k, v)                                                           => Seq(Add(e, a, k + "@" + d(v), Card(card)))
        }

      case MapRemove(removeKeys0) =>
        val removeKeys = removeKeys0.distinct
        val oldPairs = getPairs(e, a)
        oldPairs.flatMap {
          case (oldK, oldV) if removeKeys.contains(oldK) => Seq(Retract(e, a, oldK + "@" + oldV))
          case (oldK, oldV)                              => Nil
        }

      case MapEq(newPairs) =>
        checkDupKeys(newPairs, "default", "apply")
        e match {
          case updateE: Long => {
            val oldPairs = getPairs(e, a)
            val newPairs1 = newPairs.map { case (k, v) => (k, d(v).toString) }
            val obsoleteRetracts = oldPairs.flatMap {
              case (oldK, oldV) if newPairs1.contains((oldK, oldV)) => Nil
              case (oldK, oldV)                                     => Seq(Retract(e, a, oldK + "@" + oldV))
            }
            val newAdds = newPairs.map { case (k, v) => Add(e, a, k + "@" + d(v), Card(card)) }
            obsoleteRetracts ++ newAdds
          }
          case newE          =>
            newPairs.map { case (k, v) => Add(e, a, k + "@" + d(v), Card(card)) }
        }

      case Add_(values) =>
        checkDupValues(values, "default", "add")
        values.map(v => Add(e, a, p(v), Card(card)))

      case Replace(oldNew) =>
        checkDupValuesOfPairs(oldNew, "default", "replace")
        oldNew.flatMap { case (oldValue, newValue) => Seq(Retract(e, a, p(oldValue)), Add(e, a, p(newValue), Card(card))) }

      case Remove(removeValues) =>
        removeValues.distinct.map(v => Retract(e, a, p(v)))

      case Eq(newValues) =>
        checkDupValues(newValues, "default", "apply")
        val curValues = attrValues(e, a).toSeq
        val obsoleteRetracts = if (card == 2 ||
          // Retract obsolete card-one value
          (newValues.isEmpty && curValues.nonEmpty)
        ) curValues.flatMap {
            case curValue if newValues.contains(curValue) => Nil
            case obsoleteValue                            => Seq(Retract(e, a, p(obsoleteValue)))
          } else Nil
        val newAdds = newValues.flatMap {
          case newValue if curValues.contains(newValue) => Nil
          case newValue                                 => Seq(Add(e, a, p(newValue), Card(card)))
        }
        obsoleteRetracts ++ newAdds

      case m: Map[_, _] => m.map { case (k, v) => Add(e, a, k + "@" + d(v), Card(card)) }
      case vs: Set[_]   => vs.map(v => Add(e, a, p(v), Card(card)))
      case v :: Nil     => Seq(Add(e, a, p(v), Card(card)))
      case vs: List[_]  => vs.map(v => Add(e, a, p(v), Card(card)))
      case v            => Seq(Add(e, a, p(v), Card(card)))
    }

    val newStmts = bi match {
      case BiSelfRef(card)             => biSelf(card)
      case BiSelfRefAttr(card)         => biSelf(card)
      case BiOtherRef(card, attr)      => biOther(card, attr)
      case BiOtherRefAttr(card, attr)  => biOther(card, attr)
      case BiEdgeRefAttr(card, attr)   => biEdgeRefAttr(card, attr)
      case BiEdgeRef(card, attr)       => biEdgeRef(card, attr)
      case BiEdgePropAttr(card)        => biEdgeProp(card)
      case BiEdgePropRefAttr(card)     => biEdgeProp(card)
      case BiEdgePropRef(card)         => biEdgeProp(card)
      case BiTargetRef(card, attr)     => biTarget(card, attr)
      case BiTargetRefAttr(card, attr) => biTarget(card, attr)
      case Card(card)                  => default(card)
      case other                       => sys.error(
        s"""Unexpected or missing Generic `$other`:
           |e  : $e
           |a  : $a
           |arg: $arg
         """.stripMargin)
    }

    stmts ++ newStmts
  }

  def splitStmts(): (Seq[Statement], Seq[Statement]) = {
    val (genericStmtsOpt, genericTxStmtsOpt) = stmtsModel.map {
      case tx@Add('tx, _, Values(vs, prefix), _) => (None, Some(tx))
      case other                                 => (Some(other), None)
    }.unzip
    (genericStmtsOpt.flatten, genericTxStmtsOpt.flatten)
  }

  def lastE(stmts: Seq[Statement], attr: String, nestedE: Any, bi: Generic) = {
    bi match {
      case BiTargetRef(_, _) => {
        val lastEdgeNs = attr.split("/").head
        stmts.reverse.collectFirst {
          case Add(e: db.DbId, a, v, _) if a.startsWith(lastEdgeNs) => e
        } getOrElse iae("lastE", s"Couldn't find namespace `$lastEdgeNs` in any previous Add statements:\n" + stmts.mkString("\n"))
      }
      case _                 => {
        if (nestedE != 0)
          nestedE
        else if (stmts.isEmpty)
          tempId(attr)
        else if (stmts.last.e == -1)
          stmts.last.v
        else
          stmts.last.e
      }
    }
  }

  def lastV(stmts: Seq[Statement], attr: String, nestedE: Any = 0) = {
    if (nestedE != 0)
      nestedE
    else if (stmts.isEmpty)
      tempId(attr)
    else
      stmts.last.v
  }

  def eidV(stmts: Seq[Statement]): Boolean = stmts.nonEmpty && stmts.last.v.isInstanceOf[db.DbId]

  def matchDataStmt(stmts: Seq[Statement], genericStmt: Statement, arg: Any, cur: Int, next: Int, nestedE: Any, edgeB: Option[AnyRef]) = genericStmt match {

    // Keep current cursor (add no new data in this iteration)
    case Add('tempId, a, refNs: String, bi)              => (cur, edgeB, valueStmts(stmts, tempId(a), a, tempId(refNs), None, bi, edgeB))
    case Add('e, a, refNs: String, bi@BiSelfRef(1))      => (cur, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE, bi), a, tempId(refNs), None, bi, edgeB))
    case Add('e, a, refNs: String, bi@BiSelfRef(2))      => (cur, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE, bi), a, tempId(refNs), None, bi, edgeB))
    case Add('e, a, refNs: String, bi@BiEdgeRef(_, _))   => val edgeB1 = Some(tempId(a)); (cur, edgeB1, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB1))
    case Add('e, a, refNs: String, bi@BiTargetRef(_, _)) => (cur, None, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB))
    case Add('e, a, refNs: String, bi)                   => (cur, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE, bi), a, tempId(refNs), None, bi, edgeB))
    case Add('v, a, refNs: String, bi)                   => (cur, edgeB, valueStmts(stmts, stmts.last.v, a, tempId(refNs), None, bi, edgeB))
    case Add('v, a, 'tempId, bi) if nestedE != 0         => (cur, edgeB, valueStmts(stmts, nestedE, a, tempId(a), None, bi, edgeB))
    case Add('v, a, 'tempId, bi)                         => (cur, edgeB, valueStmts(stmts, stmts.last.v, a, tempId(a), None, bi, edgeB))
    case Retract(e, a, v, bi)                            => (cur, edgeB, stmts)

    // Advance cursor for next value in data row
    case Add('tempId, a, 'arg, bi@BiEdgePropAttr(_))                    => val edgeB1 = Some(tempId(a)); (next, edgeB1, valueStmts(stmts, tempId(a), a, arg, None, bi, edgeB1))
    case Add('tempId, a, 'arg, bi@BiTargetRefAttr(_, _))                => val edgeB1 = Some(tempId(a)); (next, edgeB1, valueStmts(stmts, tempId(a), a, arg, None, bi, edgeB1))
    case Add('tempId, a, Values(EnumVal, pf), bi@BiEdgePropAttr(_))     => val edgeB1 = Some(tempId(a)); (next, edgeB1, valueStmts(stmts, tempId(a), a, arg, pf, bi, edgeB1))
    case Add('tempId, a, Values(EnumVal, pf), bi@BiTargetRefAttr(_, _)) => val edgeB1 = Some(tempId(a)); (next, edgeB1, valueStmts(stmts, tempId(a), a, arg, pf, bi, edgeB1))
    case Add('tempId, a, Values(vs, pf), bi@BiEdgePropAttr(_))          => val edgeB1 = Some(tempId(a)); (next, edgeB1, valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB1))
    case Add('tempId, a, Values(vs, pf), bi@BiTargetRefAttr(_, _))      => val edgeB1 = Some(tempId(a)); (next, edgeB1, valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB1))
    case Add('tempId, a, 'arg, bi)                                      => (next, edgeB, valueStmts(stmts, tempId(a), a, arg, None, bi, edgeB))
    case Add('tempId, a, Values(EnumVal, pf), bi)                       => (next, edgeB, valueStmts(stmts, tempId(a), a, arg, pf, bi, edgeB))
    case Add('tempId, a, Values(vs, pf), bi)                            => (next, edgeB, valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB))
    case Add('remove_me, a, 'arg, bi)                                   => (next, edgeB, valueStmts(stmts, -1, a, arg, None, bi, edgeB))
    case Add('arg, a, 'tempId, bi)                                      => (next, edgeB, valueStmts(stmts, arg, a, tempId(a), None, bi, edgeB))
    case Add('e, a, 'arg, bi)                                           => (next, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE, bi), a, arg, None, bi, edgeB))
    case Add('e, a, Values(EnumVal, prefix), bi)                        => (next, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE, bi), a, arg, prefix, bi, edgeB))
    case Add('e, a, Values(vs, prefix), bi)                             => (next, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE, bi), a, vs, prefix, bi, edgeB))
    case Add('v, a, 'arg, bi) if eidV(stmts)                            => (next, edgeB, valueStmts(stmts, stmts.last.v, a, arg, None, bi, edgeB))
    case Add('v, a, 'arg, bi)                                           => (next, edgeB, valueStmts(stmts, lastV(stmts, a, nestedE), a, arg, None, bi, edgeB))
    case Add('v, a, Values(EnumVal, prefix), bi)                        => (next, edgeB, valueStmts(stmts, lastV(stmts, a, nestedE), a, arg, prefix, bi, edgeB))
    case Add('v, a, Values(vs, prefix), bi)                             => (next, edgeB, valueStmts(stmts, lastV(stmts, a, nestedE), a, vs, prefix, bi, edgeB))
    case Add('tx, a, 'arg, bi)                                          => (next, edgeB, valueStmts(stmts, tempId("tx"), a, arg, None, bi, edgeB))
    case Add(_, _, nestedGenStmts0: Seq[_], _) if arg == Nil            => (next, edgeB, stmts)

    // Nested data structures
    case Add(e, ref, nestedGenStmts0: Seq[_], bi) => {
      val parentE = if (e == 'parentId)
        tempId(ref)
      else if (stmts.isEmpty)
        e
      else
        stmts.reverse.collectFirst {
          // Find entity value of Add statement with matching namespace
          case add: Add if eidV(stmts)                                                      => stmts.last.v
          case Add(e1, a, _, _) if a.replaceFirst("/.*", "") == ref.replaceFirst("/.*", "") => e1
        }.getOrElse(iae("", "[Model2Transaction:matchDataStmt] Couldn't find previous statement with matching namespace." +
          "\ne  : " + e +
          "\nref: " + ref))

      val nestedGenStmts = nestedGenStmts0.map { case s: Statement => s }
      val nestedRows = untupleNestedArgss(nestedGenStmts0, arg)

      val nestedInsertStmts: Seq[Statement] = bi match {

        // Bidirectional self references ...........................................................

        // Nested self references
        // living_Person.name.Friends.*(living_Person.name) insert List(...
        case BiSelfRef(2) => nestedRows.flatMap { nestedRow =>
          val nestedE = tempId(ref)
          val bondStmts = Seq(
            Add(nestedE, ref, parentE, Card(2)),
            Add(parentE, ref, nestedE, Card(2))
          )
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, nestedE)
          //            val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, nestedE, edgeB) // todo edgeB necessary?
          bondStmts ++ nestedStmts
        }

        // Bidirectional other references ...........................................................

        // Nested other references
        // living_Person.name.Buddies.*(living_Animal.name) insert List(...
        case BiOtherRef(2, revRefAttr) => nestedRows.flatMap { nestedRow =>
          val nestedE = tempId(ref)
          val bondStmts = Seq(
            Add(nestedE, revRefAttr, parentE, Card(2)),
            Add(parentE, ref, nestedE, Card(2))
          )
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, nestedE)
          //            val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, nestedE, edgeB) // todo edgeB necessary?
          bondStmts ++ nestedStmts
        }


        // Bidirectional property edges ...........................................................

        // Nested property edges
        // living_Person.name.Knows.*(living_Knows.weight.Person.name) insert List(...
        case BiEdgeRef(2, revAttr) => nestedRows.flatMap { nestedRow =>
          val edgeA = tempId(revAttr)
          val edgeB1 = tempId(ref)
          val bondStmts = Seq(
            Add(edgeA, ":molecule_Meta/otherEdge", edgeB1, Card(2)),
            Add(edgeB1, ":molecule_Meta/otherEdge", edgeA, Card(2)),
            Add(edgeB1, revAttr, parentE, Card(2)),
            Add(parentE, ref, edgeA, Card(2))
          )
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, edgeA, Some(edgeB1))
          bondStmts ++ nestedStmts
        }

        // Nested edge property values
        // living_Person.name.Knows.weight.InCommon.*(living_Quality.name)._Knows.Person.name insert List(...
        case BiEdgePropRef(2) => nestedRows.flatMap { nestedRow =>
          val edgeA = tempId(ref)
          val bondStmts = Seq(
            Add(edgeB.get, ref, edgeA, Card(2)),
            Add(parentE, ref, edgeA, Card(2))
          )
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, edgeA)
          //            val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, edgeA, edgeB) // todo edgeB necessary?
          bondStmts ++ nestedStmts
        }


        // Uni-directional (normal) nested rows ...............................................

        // Invoice.id.InvoiceLines * InvoiceLine.no.item.price
        case _ => nestedRows.flatMap { nestedRow =>
          val nestedE = tempId(ref)
          val bondStmt = Add(parentE, ref, nestedE, Card(2))
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, nestedE)
          bondStmt +: nestedStmts
        }
      }

      (next, edgeB, stmts ++ nestedInsertStmts)
    }

    case unexpected => iae("matchDataStmt", "Unexpected insert statement: " + unexpected)
  }


  def resolveStmts(genericStmts: Seq[Statement], row: Seq[Any], nestedE0: Any = 0, edgeB0: Option[AnyRef] = None): Seq[Statement] = {
    genericStmts.foldLeft(0, edgeB0, Seq[Statement]()) { case ((cur, edgeB, stmts0), genericStmt0) =>
      val arg0 = row.asJava.get(cur)
      val next = if ((cur + 1) < row.size) cur + 1 else cur

      val (stmts, nestedE) = if (stmts0.isEmpty)
        (stmts0, nestedE0)
      else stmts0.last match {
        case Add('ns, ns, backRef, _) => (stmts0.init, backRef)
        case _                        => (stmts0, nestedE0)
      }

      (arg0, genericStmt0) match {

        // null values not allowed
        case (null, _) => iae("resolveStmts", "null values not allowed. Please use `attr$` for Option[tpe] values.")

        // Backreference - with mandatory previous ref
        case (_, br@Add('ns, ns, "", bi)) =>
          val backRef = stmts.reverse.collectFirst {
            case Add(e: db.DbId, a, v, _) if a.startsWith(ns) => e
          } getOrElse iae("resolveStmts", s"Couldn't find namespace `$ns` in any previous Add statements.\n" + stmts.mkString("\n"))
          (cur, edgeB, stmts :+ Add('ns, ns, backRef, bi))

        case (None, Add('e, a, refNs: String, bi@BiEdgeRef(_, _))) =>
          val (edgeA, edgeB1) = (tempId(refNs), Some(tempId(a)))
          (cur, edgeB1, valueStmts(stmts, lastE(stmts, a, 0, bi), a, edgeA, None, bi, edgeB1))

        case (None, Add('e, a, refNs: String, bi))                      => (cur, edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB))
        case (None, _)                                                  => (next, edgeB, stmts)
        case (Some(arg), genericStmt)                                   => matchDataStmt(stmts, genericStmt, arg, cur, next, nestedE, edgeB)
        case (arg, Add('e, a, 'arg, bi)) if eidV(stmts)                 => (next, edgeB, valueStmts(stmts, stmts.last.v, a, arg, None, bi, edgeB))
        case (arg, genStmt@Add('e, _, refNs: String, _)) if eidV(stmts) => matchDataStmt(stmts, genStmt.copy(e = 'v), arg, cur, next, nestedE, edgeB)
        case (arg, genStmt)                                             => matchDataStmt(stmts, genStmt, arg, cur, next, nestedE, edgeB)
      }
    }._3.filterNot(_.e == -1)
  }


  def insertStmts(dataRows: Seq[Seq[Any]]): Seq[Seq[Statement]] = {
    val (genericStmts, genericTxStmts) = splitStmts()
    val dataStmtss: Seq[Seq[Statement]] = dataRows.map(resolveStmts(genericStmts, _))
    val txId = tempId("tx")
    val txStmtss: Seq[Seq[Statement]] = Seq(genericTxStmts.foldLeft(Seq[Statement]()) {
      case (stmts, Add('tx, a, Values(vs, prefix), bi)) => valueStmts(stmts, txId, a, vs, prefix, bi, None)
      case (stmts, unexpected)                          => iae("insertStmts", "Unexpected insert statement: " + unexpected)
    })
    dataStmtss ++ (if (txStmtss.head.isEmpty) Nil else txStmtss)
  }


  def saveStmts(): Seq[Statement] = {
    val txId = tempId("tx")
    stmtsModel.foldLeft(None: Option[AnyRef], Seq[Statement]()) { case ((edgeB, stmts), genericStmt) =>
      genericStmt match {
        case Add('tempId, a, Values(vs, pf), bi@BiEdgePropAttr(_)) => val edgeB1 = Some(tempId(a)); (edgeB1, valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB1))
        case Add('tempId, a, Values(vs, pf), bi)                   => (edgeB, valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB))
        case Add('e, a, 'tempId, bi)                               => (edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(a), None, bi, edgeB))
        case Add(e, a, 'tempId, bi)                                => (edgeB, valueStmts(stmts, e, a, tempId(a), None, bi, edgeB))
        case Add('e, a, Values(vs, prefix), bi)                    => (edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, vs, prefix, bi, edgeB))
        case Add('e, a, refNs: String, bi@BiEdgeRef(_, _))         => val edgeB1 = Some(tempId(a)); (edgeB1, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB1))
        case Add('e, a, refNs: String, bi@BiTargetRef(_, _))       => (None, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB))
        case Add('e, a, refNs: String, bi)                         => (edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB))
        case Add('v, a, Values(vs, prefix), bi)                    => (edgeB, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB))
        case Add('tx, a, Values(vs, prefix), bi)                   => (edgeB, valueStmts(stmts, txId, a, vs, prefix, bi, edgeB))
        case Add('ns, a, _, _)                                     => (edgeB, stmts)
        case Retract(_, _, _, _)                                   => (edgeB, stmts)
        case Add(id: Long, a, Values(_, _), _)                     => iae("saveStmts", s"With a given id `$id` please use `update` instead.")
        case Add(_, a, 'arg, _)                                    => iae("saveStmts", s"Attribute `$a` needs a value applied")
        case unexpected                                            => iae("saveStmts", "Unexpected save statement: " + unexpected)
      }
    }._2
  }


  def updateStmts(): Seq[Statement] = {
    val (genericStmts, genericTxStmts) = splitStmts()
    val dataStmts: Seq[Statement] = genericStmts.foldLeft(
      None: Option[AnyRef],
      Seq.empty[Statement],
      0L
    ) { case ((edgeB, stmts, prevE), genericStmt) =>
      genericStmt match {
        case Add('e, a, 'tempId, bi)                           => (edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(a), None, bi, edgeB), 0L)
        case Add(e, a, 'tempId, bi@BiEdgeRef(_, _))            => val edgeB1 = Some(tempId(a)); (edgeB1, valueStmts(stmts, e, a, tempId(a), None, bi, edgeB1), 0L)
        case Add(e, a, 'tempId, bi)                            => (edgeB, valueStmts(stmts, e, a, tempId(a), None, bi, edgeB), 0L)
        case Add('e, a, Values(vs, prefix), bi) if prevE == 0L => (edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, vs, prefix, bi, edgeB), 0L)
        case Add('e, a, Values(vs, prefix), bi)                =>
          val addStmts = valueStmts(stmts, prevE, a, vs, prefix, bi, edgeB)
          if (addStmts.isEmpty)
            (edgeB, Nil, prevE)
          else
            (edgeB, addStmts, 0L)
        case Add('e, a, refNs: String, bi@BiTargetRef(_, _))   => (None, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB), 0L)
        case Add('v, a, Values(vs, prefix), bi)                => (edgeB, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB), 0L)
        case Add('tx, a, Values(vs, prefix), bi)               => (edgeB, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB), 0L)
        case Add(e, a, Values(vs, prefix), bi)                 =>
          val addStmts = valueStmts(stmts, e, a, vs, prefix, bi, edgeB)
          if (addStmts.isEmpty)
            (edgeB, Nil, e.asInstanceOf[Long]) // pass eid so that we have it for subsequent stmts
          else
            (edgeB, addStmts, 0L)
        case Retract('e, a, Values(vs, prefix), bi)            => (edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, vs, prefix, bi, edgeB), 0L)
        case Retract(e, a, Values(vs, prefix), bi)             => (edgeB, valueStmts(stmts, e, a, vs, prefix, bi, edgeB), 0L)
        case Add(_, a, 'arg, _)                                => iae("updateStmts", s"Attribute `$a` needs a value applied")
        case unexpected                                        => iae("updateStmts", "Unexpected update statement: " + unexpected)
      }
    }._2
    val txId = tempId("tx")
    val txStmts: Seq[Statement] = genericTxStmts.foldLeft(Seq[Statement]()) {
      case (stmts, Add('tx, a, Values(vs, prefix), bi)) => valueStmts(stmts, txId, a, vs, prefix, bi, None)
      case (stmts, unexpected)                          => iae("updateStmts", "Unexpected insert statement: " + unexpected)
    }
    dataStmts ++ txStmts
  }


  private def untupleNestedArgss(stmts: Seq[Any], arg0: Any): Seq[Seq[Any]] = {
    val (argArity, arg) = arg0 match {
      case a: Seq[_]  => a.head match {
        case None       => iae("untupleNestedArgss", "Please use `List()` instead of `List(None)` for nested null values.")
        case null       => iae("untupleNestedArgss", "Please use `List()` instead of `List(null)` for nested null values.")
        case p: Product => (p.productArity, a)
        case l: Seq[_]  => (l.size, a)
        case _          => (1, a)
      }
      case unexpected => iae("untupleNestedArgss", "Unexpected data: " + unexpected)
    }
    val argStmts = stmts.collect {
      case a@Add(_, _, 'arg, _)                => a
      case a@Add(_, _, Values(vs, prefix), _)  => a
      case a@Add(_, _, nestedStmts: Seq[_], _) => a
    }
    val stmtsSize = argStmts.size
    if (argArity != stmtsSize) iae("untupleNestedArgss", "Arity of statements and arguments should match. Found: \n" +
      s"Statements (arity $stmtsSize): " + stmts.mkString("\n  ", "\n  ", "\n") +
      s"Arguments0                  : " + arg0 +
      s"Arguments  (arity $argArity): " + arg.mkString("\n  ", "\n  ", "\n"))

    arg map tupleToSeq
  }
}