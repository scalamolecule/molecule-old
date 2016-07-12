package molecule.transform
import java.util.{Date, List => jList}

import molecule._
//import datomic.{Connection, Database, Peer}
import datomic._
//import molecule.DatomicFacade
import molecule.ast.model._
import molecule.ast.transaction._
import molecule.util.{Debug, Helpers}

import scala.collection.JavaConversions._


case class Model2Transaction(conn: Connection, model: Model) extends Helpers {
  val x = Debug("Model2Transaction", 25, 51, false, 6)

  private def iae(method: String, msg: String) = {
    throw new IllegalArgumentException(s"[molecule.transform.Model2Transaction.$method]  $msg")
  }

  val stmtsModel: Seq[Statement] = {

    def resolveTx(elements: Seq[Element]) = elements.foldLeft('tx: Any, Seq[Statement]()) {
      case ((eSlot1, stmts1), a@Atom(ns, name, _, _, VarValue, _, _, _)) => iae("stmtsModel",
        s"Please apply transaction meta data directly to transaction attribute: `${ns.capitalize}.$name(<metadata>)`")
      case ((eSlot1, stmts1), element1)                                  => resolveElement(eSlot1, stmts1, element1)
    }._2

    def bi(gs: Seq[Generic]) = gs.collectFirst { case bi: Bidirectional => bi } getOrElse NoValue

    def resolveElement(eSlot: Any, stmts: Seq[Statement], element: Element): (Any, Seq[Statement]) = (eSlot, element) match {
      case ('_, Meta(ns, "", "e", _, EntValue))             => ('arg, stmts)
      case ('_, Meta(ns, "", "e", _, Eq(Seq(id: Long))))    => (Eid(id), stmts)
      case ('_, Atom(ns, name, _, c, VarValue, _, gs, _))   => ('e, stmts :+ Add('tempId, s":$ns/$name", 'arg, bi(gs)))
      case ('_, Atom(ns, name, _, c, value, prefix, gs, _)) => ('e, stmts :+ Add('tempId, s":$ns/$name", Values(value, prefix), bi(gs)))
      case ('_, Bond(ns, refAttr, refNs, c, gs))            => ('v, stmts :+ Add('tempId, s":$ns/$refAttr", s":$refNs", bi(gs)))

      case (e, Nested(Bond(ns, refAttr, _, _, gs), elements)) =>
        val nested = elements.foldLeft('v: Any, Seq[Statement]()) {
          case ((eSlot1, stmts1), element1) => resolveElement(eSlot1, stmts1, element1)
        }._2
        val parentId = if (e == '_) 'parentId else 'e
        ('e, stmts :+ Add(parentId, s":$ns/$refAttr", nested, bi(gs)))

      // First with id
      case (Eid(id), Atom(ns, name, _, c, value@Remove(_), prefix, gs, _)) => ('e, stmts :+ Retract(id, s":$ns/$name", Values(value, prefix), bi(gs)))
      case (Eid(id), Atom(ns, name, _, c, value, prefix, gs, _))           => ('e, stmts :+ Add(id, s":$ns/$name", Values(value, prefix), bi(gs)))
      case (Eid(id), Bond(ns, refAttr, refNs, c, gs))                      => ('v, stmts :+ Add(id, s":$ns/$refAttr", 'tempId, bi(gs)))

      // Same namespace
      case ('e, Atom(ns, name, _, _, value@Remove(_), prefix, gs, _)) => ('e, stmts :+ Retract('e, s":$ns/$name", Values(value, prefix), bi(gs)))
      case ('e, Atom(ns, name, _, c, VarValue, _, gs, _))             => ('e, stmts :+ Add('e, s":$ns/$name", 'arg, bi(gs)))
      case ('e, Atom(ns, name, _, c, value, prefix, gs, _))           => ('e, stmts :+ Add('e, s":$ns/$name", Values(value, prefix), bi(gs)))
      case ('e, Bond(ns, refAttr, refNs, c, gs))                      => ('v, stmts :+ Add('e, s":$ns/$refAttr", s":$refNs", bi(gs)))

      // Transaction annotations
      case ('_, TxMetaData(elements))  => ('e, stmts ++ resolveTx(elements))
      case ('e, TxMetaData(elements))  => ('e, stmts ++ resolveTx(elements))
      case ('_, TxMetaData_(elements)) => ('e, stmts ++ resolveTx(elements))
      case ('e, TxMetaData_(elements)) => ('e, stmts ++ resolveTx(elements))

      // Continue with only transaction Atoms...
      case ('tx, Atom(ns, name, _, _, VarValue, _, _, _))                                           => ('e, stmts :+ Add('e, s":$ns/$name", 'arg))
      case ('tx, Atom(ns, name, _, _, value, prefix, _, _)) if name.last == '_' || name.last == '$' => ('tx, stmts :+ Add('tx, s":$ns/${name.init}", Values(value, prefix)))
      case ('tx, Atom(ns, name, _, _, value, prefix, _, _))                                         => ('tx, stmts :+ Add('tx, s":$ns/$name", Values(value, prefix)))

      // Next namespace
      case ('v, Atom(ns, name, _, _, VarValue, _, gs, _))   => ('e, stmts :+ Add('v, s":$ns/$name", 'arg, bi(gs)))
      case ('v, Atom(ns, name, _, _, value, prefix, gs, _)) => ('e, stmts :+ Add('v, s":$ns/$name", Values(value, prefix), bi(gs)))
      case ('v, Bond(ns, refAttr, _, c, gs))                => ('v, stmts :+ Add('v, s":$ns/$refAttr", 'tempId, bi(gs)))

      // Add one extra generic statement to receive the eid arg for the following statement to use
      // (we then discard that temporary statement from the value statements)
      case ('arg, Atom(ns, name, _, _, VarValue, _, _, _)) => ('e, stmts :+ Add('remove_me, s":$ns/$name", 'arg) :+ Add('v, s":$ns/$name", 'arg))
      case ('arg, Bond(ns, refAttr, _, c, gs))             => ('v, stmts :+ Add('arg, s":$ns/$refAttr", 'tempId, bi(gs)))

      // BackRef
      case (_, ReBond(ns, _, _, _, _)) => ('e, stmts :+ Add('ns, s":$ns", ""))

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
    case "tx"                 => Peer.tempid(s":db.part/tx")
    case s if s.contains('_') => Peer.tempid(s":" + attr.substring(1).split("(?=_)").head) // extract "partition" from ":partition_Namespace/attr"
    case _                    => Peer.tempid(s":db.part/user")
  }

  // Lookup if key is already populated
  def pairStrs(e: Any, a: String, key: String) = {
    val query = s"[:find ?v :in $$ ?e ?a ?key :where [?e ?a ?v][(.startsWith ^String ?v ?key)]]"
    Peer.q(query, conn.db, e.asInstanceOf[Object], a.asInstanceOf[Object], key.asInstanceOf[Object]).map(_.get(0))
  }

  def getPairs(e: Any, a: String, key: String = ""): Map[String, String] = {
    val strs = if (key.isEmpty) {
      val query = s"[:find ?v :in $$ ?e ?a :where [?e ?a ?v]]"
      Peer.q(query, conn.db, e.asInstanceOf[Object], a.asInstanceOf[Object]).map(_.get(0))
    } else {
      val query = s"[:find ?v :in $$ ?e ?a ?key :where [?e ?a ?v][(.startsWith ^String ?v ?key)]]"
      Peer.q(query, conn.db, e.asInstanceOf[Object], a.asInstanceOf[Object], key.asInstanceOf[Object]).map(_.get(0))
    }
    strs.map {
      case str: String =>
        //        str.split("@", 2)
        val Seq(key: String, value: String) = str.split("@", 2).toSeq
        //        val key = parts.head
        //        val value = parts.tail.mkString("")
        key -> value
    }.toMap
  }


  private def valueStmts(stmts: Seq[Statement], e: Any, a: String, arg: Any, prefix: Option[String] = None, bi: Generic = NoValue, otherEdgeId: Option[AnyRef] = None): Seq[Statement] = {
    def p(arg: Any) = if (prefix.isDefined) prefix.get + arg else arg

    def attrValues(id: Any, attr: String) = {
      val query = if (prefix.isDefined && prefix.get != "mapping")
        s"""[:find ?enums
            | :in $$ ?id
            | :where [?id $attr ?a]
            |        [?a :db/ident ?b]
            |        [(.getName ^clojure.lang.Keyword ?b) ?enums]
            |]""".stripMargin
      else
        s"[:find ?values :in $$ ?id :where [?id $attr ?values]]"

      Peer.q(query, conn.db, id.asInstanceOf[Object]).map(_.get(0))
    }

    def getOtherEdgeId(edgeA: Any) = {
      val query = s"[:find ?edgeB :in $$ ?edgeA :where [?edgeA :molecule_Meta/otherEdge ?edgeB]]"
      Peer.q(query, conn.db, edgeA.asInstanceOf[Object]).map(_.get(0))
    }

    def biSelfRef(card: Int) = arg match {

      case Replace(oldNew) => attrValues(e, a).toSeq.flatMap { case revRef =>
        oldNew.toSeq.flatMap {
          case (oldRef, newRef) if oldRef == revRef => Seq(
            // This entity e now has ref to newRef instead of oldRef
            Retract(e, a, oldRef), Add(e, a, newRef),
            // RevRef no longer has a ref to this entity e
            // Instead newRef has a ref to this entity e
            Retract(revRef, a, e), Add(newRef, a, e)
          )
          case _                                    => Nil
        }
      }

      case Remove(Seq())      => attrValues(e, a).toSeq.flatMap {
        case revRef => Seq(Retract(revRef, a, e), Retract(e, a, revRef))
      }
      case Remove(removeRefs) => removeRefs.flatMap {
        case ref: Long => Seq(Retract(ref, a, e), Retract(e, a, ref))
      }

      case Add_(refs) => refs.flatMap { case ref: Long =>
        val reverseRetracts = if (card == 1) attrValues(e, a).toSeq.map(revRef => Retract(revRef, a, e)) else Nil
        if (ref == e) iae("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same")
        reverseRetracts ++ Seq(Add(ref, a, e), Add(e, a, ref))
      }

      case Eq(newRefs) => {
        if (newRefs.contains(e)) iae("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")
        val oldRefs = attrValues(e, a).toSeq
        val obsoleteRetracts = oldRefs.flatMap {
          // Remaining ref
          case oldRef if newRefs.contains(oldRef) => Nil
          // Obsolete ref
          case obsoleteRef => Seq(
            Retract(obsoleteRef, a, e),
            Retract(e, a, obsoleteRef)
          )
        }
        val newAdds = newRefs.flatMap {
          // Existing ref
          case newRef if oldRefs.contains(newRef) => Nil
          // New ref
          case newRef => Seq(
            Add(newRef, a, e),
            Add(e, a, newRef)
          )
        }
        obsoleteRetracts ++ newAdds
      }

      case refs: Set[_] => refs.flatMap { case ref: Long =>
        Seq(Add(ref, a, e), Add(e, a, ref))
      }
      case ref          => {
        val reverseRetracts = if (card == 1) attrValues(e, a).toSeq.map(revRef => Retract(revRef, a, e)) else Nil
        reverseRetracts ++ Seq(Add(ref, a, e), Add(e, a, ref))
      }
    }


    def biOtherRef(card: Int) = biSelfRef(card) // Todo


    def biEdgeRefAttr(card: Int, targetAttr: String) = arg match {
      case Replace(oldNew) => iae("valueStmts:biEdgeRefAttr",
        s"Replacing edge ids with `edgeAttr.apply(old -> new)` is not allowed. It could be an indication that you are " +
          s"trying to replace the old edge with an existing edge which is not allowed.")
      case Add_(_)         => iae("valueStmts:biEdgeRefAttr",
        s"Adding edge ids with `edgeAttr.add(someEdgeId)` is not allowed. It could be an indication that you are " +
          s"trying to use an existing edge twice which is not allowed.")
      case other           => biEdgeRef(card, targetAttr)
    }

    def biEdgeRef(card: Int, targetAttr: String) = arg match {

      // BaseEntity.edge()
      case Remove(Seq()) =>
        // Retracting each edge will retract the reverse too since it is defined with `isComponent`
        attrValues(e, a).map(RetractEntity(_))

      // BaseEntity.edge.remove(edge1, edgeN)
      case Remove(edges) =>
        edges.flatMap { edgeA =>
          getOtherEdgeId(edgeA) match {
            case Seq(edgeB) =>
              // Retract both edge entities
              Seq(RetractEntity(edgeA), RetractEntity(edgeB))
            case Nil        =>
              val otherId = EntityFacade(conn.db.entity(edgeA), conn, edgeA.asInstanceOf[Object])
              iae("valueStmts:biEdgeRef", s"Supplied id $edgeA doesn't appear to be a property edge id (couldn't find reverse edge id). " +
                s"Could it be another entity?:\n" + otherId.touchQ(2) + s"\nSpooky id: $otherId")
            case ids        =>
              iae("valueStmts:biEdgeRef", "Unexpectedly found multiple reverse edge ids:\n" + ids.mkString("\n"))
          }
        }

      case Eq(edges) => edges.flatMap { case edgeA: Long =>
        val retracts = attrValues(e, a).toSeq.map(revRef => Retract(revRef, a, e))
        if (edgeA == e) iae("valueStmts:biEdgeRef", "Current entity and referenced entity ids can't be the same")
        retracts ++ Seq(Add(edgeA, a, e), Add(e, a, edgeA))

        Seq(Add(otherEdgeId, a, e), Add(e, a, edgeA))
      }

      //        case refs: Set[_]       => refs.flatMap { case ref: Long =>
      //          Seq(Add(ref, a, e), Add(e, a, ref))
      //        }
      case edgeA => {
        val edgeB = otherEdgeId getOrElse iae("valueStmts:biEdgeRef", "Missing id of other edge.")
        val reverseRetracts = if (card == 1) attrValues(e, a).toSeq.map(revRef => Retract(revRef, a, e)) else Nil
        reverseRetracts ++ Seq(
          // Interlink edge entities so that we later know which other one to update
          Add(edgeA, ":molecule_Meta/otherEdge", edgeB),
          Add(edgeB, ":molecule_Meta/otherEdge", edgeA),
          Add(edgeB, targetAttr, e),
          Add(e, a, edgeA)
        )
      }
    }


    def biEdgeProp(card: Int) = {
      val edgeA = e
      val edgeB = otherEdgeId match {
        case Some(eid) if eid == edgeA => iae("valueStmts:biEdgeProp", "Other edge id is unexpectedly the same as this edge id.")
        case Some(eid)                 => eid
        case None                      => getOtherEdgeId(edgeA) match {
          case Seq(edgeB1) => edgeB1
          case Nil         =>
            val otherId = EntityFacade(conn.db.entity(edgeA), conn, edgeA.asInstanceOf[Object])
            iae("valueStmts:biEdgeRef", s"Supplied id $edgeA doesn't appear to be a property edge id (couldn't find reverse edge id). " +
              s"Could it be another entity?:\n" + otherId.touchQ(1))
          case ids         => iae("valueStmts:biEdgeRef", "Unexpectedly found multiple reverse edge ids:\n" + ids.mkString("\n"))
        }
      }

      if (prefix.contains("mapping")) arg match {

        // Maps ......................................

        case MapEq(pairs) => pairs.flatMap {
          case (key, value) => {
            val existing = pairStrs(edgeA, a, key)
            existing.size match {
              case 0 => Seq(
                Add(edgeB, a, key + "@" + value),
                Add(edgeA, a, key + "@" + value)
              )
              case 1 => Seq(
                Retract(edgeB, a, existing.head), Add(edgeB, a, key + "@" + value),
                Retract(edgeA, a, existing.head), Add(edgeA, a, key + "@" + value)
              )
              case _ => iae("valueStmts:biEdgeProp", "Unexpected number of mapped values with the same key:\n" + existing.mkString("\n"))
            }
          }
        }
        //        case Remove(Seq())        => attrValues(e, a).toSeq.flatMap(v => Seq(Retract(edgeB, a, v), Retract(edgeA, a, v)))
        //        case Remove(removeValues) => removeValues.flatMap { case key: String =>
        //          val existing = pairStrs(e, a, key)
        //          existing.size match {
        //            case 0 => Nil
        //            case 1 => Seq(Retract(edgeB, a, existing.head), Retract(edgeA, a, existing.head))
        //            case _ => iae("valueStmts:biEdgeProp", "Unexpected number of mapped values with the same key:\n" + existing.mkString("\n"))
        //          }
        //        }
        case other => iae("valueStmts:biEdgeProp", "Unexpected arg for mapped edge property: " + other)

      } else arg match {

        // Non-Maps ..................................

        case Replace(oldNew)      => oldNew.toSeq.flatMap {
          case (oldValue, newValue) => Seq(
            Retract(edgeB, a, p(oldValue)), Add(edgeB, a, p(newValue)),
            Retract(edgeA, a, p(oldValue)), Add(edgeA, a, p(newValue))
          )
        }
        case Remove(Seq())        => attrValues(edgeA, a).toSeq.flatMap {
          case v => Seq(Retract(edgeB, a, p(v)), Retract(edgeA, a, p(v)))
        }
        case Remove(removeValues) => removeValues.flatMap {
          case v =>
            Seq(Retract(edgeB, a, p(v)), Retract(edgeA, a, p(v)))
        }
        case Add_(vs)             => vs.flatMap { case v =>
          Seq(Add(edgeA, a, p(v)), Add(edgeB, a, p(v)))
        }

        case Eq(newValues) => {
          if (newValues.contains(e)) iae("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")
          val curValues = attrValues(e, a).toSeq
          val obsoleteRetracts = curValues.flatMap {
            case curValue if newValues.contains(curValue) => Nil
            case obsoleteValue                            => Seq(Retract(edgeB, a, p(obsoleteValue)), Retract(edgeA, a, p(obsoleteValue)))
          }
          val newAdds = newValues.flatMap {
            case newValue if curValues.contains(newValue) => Nil
            case newValue                                 => Seq(Add(edgeB, a, p(newValue)), Add(edgeA, a, p(newValue)))
          }
          obsoleteRetracts ++ newAdds
        }

        //                case ref                => {
        //                  val reverseRetracts = if (meta.endsWith("1")) attrValues(edgeA, a).toSeq.map(revRef => Retract(revRef, a, edgeA)) else Nil
        //                  reverseRetracts ++ Seq(
        //                    Add(edgeB, a, ref),
        //                    Add(edgeA, a, ref)
        //                  )
        //                }

        case vs: Set[_]   => vs.toSeq.flatMap(v => Seq(Add(edgeB, a, p(v)), Add(edgeA, a, p(v))))
        case m: Map[_, _] => m.flatMap {
          case (k, d: Date) => Seq(
            Add(edgeB, a, k + "@" + format2(d)),
            Add(edgeA, a, k + "@" + format2(d))
          )
          case (k, v)       => Seq(
            Add(edgeB, a, k + "@" + v),
            Add(edgeA, a, k + "@" + v)
          )
        }
        case v :: Nil     => Seq(Add(edgeB, a, p(v)), Add(edgeA, a, p(v)))
        case vs: List[_]  => vs.flatMap(v => Seq(Add(edgeB, a, p(v)), Add(edgeA, a, p(v))))
        case v            => Seq(Add(edgeB, a, p(v)), Add(edgeA, a, p(v)))

      }
    }

    def biTargetRef(card: Int, biEdgeRefAttr: String) = {
      val edgeA = e
      val edgeB = otherEdgeId getOrElse iae("valueStmts:biTargetRef", "Missing id of other edge.")
      //      val List(card, "biEdgeRefAttr) = meta.split("@").toList.tail
      //      val biEdgeRefAttr = bi.split("@").toList.last
      arg match {

        // Each bidirectional manipulation takes care of the reverse ref

        //        case Remove(Seq())      => attrValues(e, a).toSeq.flatMap {
        //          case ref => Seq(Retract(ref, a, e), Retract(e, a, ref))
        //        }
        //        case Remove(removeRefs) => removeRefs.flatMap {
        //          case ref: Long => Seq(Retract(ref, a, e), Retract(e, a, ref))
        //        }
        //        case Eq(refs) => refs.flatMap { case ref: Long =>
        //          val reverseRetracts = if (meta.endsWith("1")) attrValues(e, a).toSeq.map(revRef => Retract(revRef, a, e)) else Nil
        //          if (ref == e) throw new IllegalArgumentException("Current entity and referenced entity ids can't be the same")
        //          reverseRetracts ++ Seq(
        //            Add(ref, a, e),
        //            Add(e, a, ref)
        //          )
        //        }
        case Eq(biTargetRef :: Nil) =>
          //          val reverseRetracts = if (meta.endsWith("1")) attrValues(edgeA, a).toSeq.map(revRef => Retract(revRef, a, edgeA)) else Nil
          //          if (biTargetRef == e) throw new IllegalArgumentException("Current entity and referenced entity ids can't be the same")
          //          reverseRetracts ++
          Seq(
            Add(biTargetRef, biEdgeRefAttr, edgeB),
            Add(edgeA, a, biTargetRef)
          )
        //        case refs: Set[_]       => refs.flatMap { case ref: Long =>
        //          Seq(Add(ref, a, e), Add(e, a, ref))
        //        }

        case biTargetRef => {
          //          val reverseRetracts = if (meta.endsWith("1")) attrValues(e, a).toSeq.map(revRef => Retract(revRef, a, e)) else Nil
          //          reverseRetracts ++
          Seq(
            Add(biTargetRef, biEdgeRefAttr, edgeB),
            Add(edgeA, a, biTargetRef)
          )
        }
      }

    }

    def mapping = arg match {
      case MapEq(newPairs) => {
        val retractCurrents = attrValues(e, a).toSeq.map(v => Retract(e, a, p(v)))
        val addNew = newPairs.map { case (newKey, newValue) => Add(e, a, newKey + "@" + newValue) }
        retractCurrents ++ addNew
      }
      //      case MapEq(pairs)    => pairs.flatMap {
      //        case (key, value) => {
      //          val existing = pairStrs(e, a, key)
      //          existing.size match {
      //            case 0 => Seq(Add(e, a, key + "@" + value))
      //            case 1 => Seq(Retract(e, a, existing.head), Add(e, a, key + "@" + value))
      //            case _ => iae("valueStmts:mapping", "Unexpected number of mapped values with the same key:\n" + existing.mkString("\n"))
      //          }
      //        }
      //      }
      //      case Remove(Seq())        => attrValues(e, a).toSeq.map(v => Retract(e, a, p(v)))
      case Remove(removeValues) => removeValues.flatMap { case removeKey: String =>
        val existing = pairStrs(e, a, removeKey)
        existing.size match {
          case 0 => Nil
          case 1 => Seq(Retract(e, a, existing.head))
          case _ => iae("valueStmts:mapping", "Unexpected number of mapped values with the same key:\n" + existing.mkString("\n"))
        }
      }
    }

    def default = arg match {

      case MapAdd(newPairs) =>
        val dups = newPairs.map(_._1).groupBy(identity).collect { case (v, vs) if vs.size > 1 => v }
        if (dups.nonEmpty) {
          val dupPairs = newPairs.filter(p => dups.contains(p._1)).sortBy(_._1).map { case (k, v) => s"$k -> $v" }
          iae("valueStmts:default", s"Can't add multiple key/value pairs with the same key for attribute `$a`:\n" + dupPairs.mkString("\n"))
        }
        val curPairs = getPairs(e, a)
        val curKeys = curPairs.keys
        newPairs.flatMap {
          case (k, v) if curPairs.contains((k, v.toString)) => Nil
          case (k, v) if curKeys.contains(k)                => Seq(Retract(e, a, k + "@" + curPairs(k)), Add(e, a, k + "@" + v))
          case (k, v)                                       => Seq(Add(e, a, k + "@" + v))
        }

      case Add_(values) =>
        val dups = values.groupBy(identity).collect { case (v, vs) if vs.size > 1 => v }
        if (dups.nonEmpty) iae("valueStmts:default", s"Can't add duplicate new values to attribute `$a`:\n" + dups.mkString("\n"))
        values.map(v => Add(e, a, p(v)))


      case Replace(oldNew) =>
        val dups = oldNew.map(_._2).groupBy(identity).collect { case (v, vs) if vs.size > 1 => v }
        if (dups.nonEmpty) iae("valueStmts:default", s"Can't replace with duplicate new values of attribute `$a`:\n" + dups.mkString("\n"))
        oldNew.toSeq.flatMap { case (oldValue, newValue) => Seq(Retract(e, a, p(oldValue)), Add(e, a, p(newValue))) }

      case Remove(removeValues) =>
        removeValues.distinct.map(v => Retract(e, a, p(v)))


      case MapEq(newPairs) =>
        val retractCurrents = attrValues(e, a).toSeq.map(v => Retract(e, a, p(v)))
        val addNew = newPairs.map { case (newKey, newValue) => Add(e, a, newKey + "@" + newValue) }
        retractCurrents ++ addNew

      case Eq(newValues) =>
        val dups = newValues.groupBy(identity).collect { case (v, vs) if vs.size > 1 => v }
        if (dups.nonEmpty) iae("valueStmts:default", s"Can't apply duplicate new values to attribute `$a`:\n" + dups.mkString("\n"))
        val curValues = attrValues(e, a).toSeq
        val obsoleteRetracts = curValues.flatMap {
          case curValue if newValues.contains(curValue) => Nil
          case obsoleteValue                            => Seq(Retract(e, a, p(obsoleteValue)))
        }
        val newAdds = newValues.flatMap {
          case newValue if curValues.contains(newValue) => Nil
          case newValue                                 => Seq(Add(e, a, p(newValue)))
        }
        obsoleteRetracts ++ newAdds

      case m: Map[_, _] => m map {
        case (k, d: Date) => Add(e, a, k + "@" + format2(d)) // Need uniform Date format
        case (k, v)       => Add(e, a, k + "@" + v)
      }
      case vs: Set[_]   => vs.map(v => Add(e, a, p(v)))
      case v :: Nil     => Seq(Add(e, a, p(v)))
      case vs: List[_]  => vs.map(v => Add(e, a, p(v)))
      case v            => Seq(Add(e, a, p(v)))
    }

    val newStmts = bi match {
      case BiSelfRef(card)             => biSelfRef(card)
      case BiSelfRefAttr(card)         => biSelfRef(card)
      case BiOtherRef(card)            => biOtherRef(card)
      case BiOtherRefAttr(card)        => biOtherRef(card)
      case BiEdgeRef(card, attr)       => biEdgeRef(card, attr)
      case BiEdgeRefAttr(card, attr)   => biEdgeRefAttr(card, attr)
      case BiEdgePropAttr(card)        => biEdgeProp(card)
      case BiEdgePropRefAttr(card)     => biEdgeProp(card)
      case BiEdgePropRef(card)         => biEdgeProp(card)
      case BiTargetRef(card, attr)     => biTargetRef(card, attr)
      case BiTargetRefAttr(card, attr) => biTargetRef(card, attr)
      //      case _ if prefix.contains("mapping") => mapping
      case _ => default
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

  def lastE(stmts: Seq[Statement], attr: String, nestedE: Any = 0, bi: Generic = NoValue) = {
    bi match {
      case BiTargetRef(_, _) => {
        val lastEdgeNs = attr.split("/").head
        stmts.reverse.collectFirst {
          case Add(e, a, v, _) if a.startsWith(lastEdgeNs) && e.isInstanceOf[db.DbId] => e
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
    //    if (bi.startsWith("biTargetRef")) {
    //      val lastEdgeNs = attr.split("/").head
    //      stmts.reverse.collectFirst {
    //        case Add(e, a, v, _) if a.startsWith(lastEdgeNs) && e.isInstanceOf[db.DbId] => e
    //      }.getOrElse(abort("", s"[Model2Transaction:lastE] Couldn't find namespace `$lastEdgeNs` in any previous Add statements:\n" + stmts.mkString("\n")))
    //    } else if (nestedE != 0)
    //      nestedE
    //    else if (stmts.isEmpty)
    //      tempId(attr)
    //    else if (stmts.last.e == -1)
    //      stmts.last.v
    //    else
    //      stmts.last.e
  }

  def lastV(stmts: Seq[Statement], attr: String, nestedE: Any = 0) = {
    if (nestedE != 0)
      nestedE
    else if (stmts.isEmpty)
      tempId(attr)
    else
      stmts.last.v
  }

  def matchDataStmt(stmts: Seq[Statement], genericStmt: Statement, arg: Any, cur: Int, next: Int, nestedE: Any, edgeB: Option[AnyRef]) = genericStmt match {

    // Keep current cursor (add no new data in this iteration)
    case Add('tempId, a, refNs: String, bi)              => (cur, edgeB, valueStmts(stmts, tempId(a), a, tempId(refNs), None, bi, edgeB))
    case Add('e, a, refNs: String, bi@BiSelfRef(1))      => (cur, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE), a, tempId(refNs), None, bi, edgeB))
    case Add('e, a, refNs: String, bi@BiSelfRef(2))      => (cur, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE), a, tempId(refNs), None, bi, edgeB))
    case Add('e, a, refNs: String, bi@BiEdgeRef(_, _))   =>
      val (edgeA, edgeB1) = (tempId(refNs), Some(tempId(a)))
      (cur, edgeB1, valueStmts(stmts, lastE(stmts, a), a, edgeA, None, bi, edgeB1))
    case Add('e, a, refNs: String, bi@BiTargetRef(_, _)) => (cur, None, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB))
    case Add('e, a, refNs: String, bi)                   => (cur, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE), a, tempId(refNs), None, bi, edgeB))
    case Add('v, a, 'tempId, bi)                         => (cur, edgeB, valueStmts(stmts, stmts.last.v, a, tempId(a), None, bi, edgeB))
    case Retract(e, a, v, bi)                            => (cur, edgeB, stmts)

    // Advance cursor for next value in data row
    case Add('tempId, a, 'arg, bi)                                                    => (next, edgeB, valueStmts(stmts, tempId(a), a, arg, None, bi, edgeB))
    case Add('tempId, a, Values(EnumVal, prefix), bi)                                 => (next, edgeB, valueStmts(stmts, tempId(a), a, arg, prefix, bi, edgeB))
    case Add('tempId, a, Values(vs, prefix), bi)                                      => (next, edgeB, valueStmts(stmts, tempId(a), a, vs, prefix, bi, edgeB))
    case Add('remove_me, a, 'arg, bi)                                                 => (next, edgeB, valueStmts(stmts, -1, a, arg, None, bi, edgeB))
    case Add('arg, a, 'tempId, bi)                                                    => (next, edgeB, valueStmts(stmts, arg, a, tempId(a), None, bi, edgeB))
    case Add('e, a, 'arg, bi@BiSelfRef(1))                                            => (next, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE), a, arg, None, bi, edgeB))
    case Add('e, a, 'arg, bi@BiSelfRef(2))                                            => (next, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE), a, arg, None, bi, edgeB))
    case Add('e, a, 'arg, bi)                                                         => (next, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE), a, arg, None, bi, edgeB))
    case Add('e, a, Values(EnumVal, prefix), bi)                                      => (next, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE), a, arg, prefix, bi, edgeB))
    case Add('e, a, Values(vs, prefix), bi)                                           => (next, edgeB, valueStmts(stmts, lastE(stmts, a, nestedE), a, vs, prefix, bi, edgeB))
    case Add('v, a, 'arg, bi) if stmts.nonEmpty && stmts.last.v.isInstanceOf[db.DbId] => (next, edgeB, valueStmts(stmts, stmts.last.v, a, arg, None, bi, edgeB))
    case Add('v, a, 'arg, bi)                                                         => (next, edgeB, valueStmts(stmts, lastV(stmts, a, nestedE), a, arg, None, bi, edgeB))
    case Add('v, a, Values(EnumVal, prefix), bi)                                      => (next, edgeB, valueStmts(stmts, lastV(stmts, a, nestedE), a, arg, prefix, bi, edgeB))
    case Add('v, a, Values(vs, prefix), bi)                                           => (next, edgeB, valueStmts(stmts, lastV(stmts, a, nestedE), a, vs, prefix, bi, edgeB))
    case Add('tx, a, 'arg, bi)                                                        => (next, edgeB, valueStmts(stmts, tempId("tx"), a, arg, None, bi, edgeB))
    case Add(_, _, nestedGenStmts0: Seq[_], _) if arg == Nil                          => (next, edgeB, stmts)

    // Nested data structures
    case Add(e, ref, nestedGenStmts0: Seq[_], bi) => {
      //      x(51, e, ref, nestedStmts, stmts)
      val parentE = if (e == 'parentId)
        tempId(ref)
      else if (stmts.isEmpty)
        e
      else
        stmts.reverse.collectFirst {
          // Find entity value of Add statement with matching namespace
          case Add(e1, a, _, _) if a.replaceFirst("/.*", "") == ref.replaceFirst("/.*", "") => e1
        }.getOrElse(iae("", "[Model2Transaction:matchDataStmt] Couldn't find previous statement with matching namespace. e: " + e + "  -- ref: " + ref.replaceFirst("/.*", "")))

      val nestedGenStmts = nestedGenStmts0.map { case s: Statement => s }
      val nestedRows = untupleNestedArgss(nestedGenStmts0, arg)

      val nestedInsertStmts: Seq[Statement] = bi match {

        // Bidirectional self references ...........................................................

        // Nested self references
        // living_Person.name.Friends.*(living_Person.name) insert List(...
        case BiSelfRef(2) => nestedRows.flatMap { nestedRow =>
          val nestedE = tempId(ref)
          val bondStmts = Seq(
            Add(nestedE, ref, parentE),
            Add(parentE, ref, nestedE)
          )
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, nestedE)
          //            val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, nestedE, edgeB) // todo edgeB necessary?
          bondStmts ++ nestedStmts
        }


        // Bidirectional propert edges ...........................................................

        // Nested property edges
        // living_Person.name.Knows.*(living_Knows.weight.Person.name) insert List(...
        case BiEdgeRef(2, revAttr) => nestedRows.flatMap { nestedRow =>
          val edgeA = tempId(revAttr)
          val edgeB1 = tempId(ref)
          val bondStmts = Seq(
            Add(edgeA, ":molecule_Meta/otherEdge", edgeB1),
            Add(edgeB1, ":molecule_Meta/otherEdge", edgeA),
            Add(edgeB1, revAttr, parentE),
            Add(parentE, ref, edgeA)
          )
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, edgeA, Some(edgeB1))
          bondStmts ++ nestedStmts
        }

        // Nested edge property values
        // living_Person.name.Knows.weight.InCommon.*(living_Quality.name)._Knows.Person.name insert List(...
        case BiEdgePropRef(2) => nestedRows.flatMap { nestedRow =>
          val edgeA = tempId(ref)
          val bondStmts = Seq(
            Add(edgeB.get, ref, edgeA),
            Add(parentE, ref, edgeA)
          )
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, edgeA)
          //            val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, edgeA, edgeB) // todo edgeB necessary?
          bondStmts ++ nestedStmts
        }


        // Uni-directional (normal) nested rows ...............................................

        // Invoice.id.InvoiceLines * InvoiceLine.no.item.price
        case _ => nestedRows.flatMap { nestedRow =>
          val nestedE = tempId(ref)
          val bondStmt = Add(parentE, ref, nestedE)
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
      val arg0 = row.get(cur)
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
        case (_, br@Add('ns, ns, "", _)) =>
          val backRef = stmts.reverse.collectFirst {
            case Add(e, a, v, _) if a.startsWith(ns) && e.isInstanceOf[db.DbId] => e
          } getOrElse iae("resolveStmts", s"Couldn't find namespace `$ns` in any previous Add statements.\n" + stmts.mkString("\n"))
          (cur, edgeB, stmts :+ Add('ns, ns, backRef))

        case (None, Add('e, a, refNs: String, _))                                               => (cur, edgeB, valueStmts(stmts, lastE(stmts, a), a, tempId(refNs)))
        case (None, _)                                                                          => (next, edgeB, stmts)
        case (Some(arg), genericStmt)                                                           => matchDataStmt(stmts, genericStmt, arg, cur, next, nestedE, edgeB)
        case (arg, Add('e, a, 'arg, _)) if stmts.nonEmpty && stmts.last.v.isInstanceOf[db.DbId] => (next, edgeB, valueStmts(stmts, stmts.last.v, a, arg))
        case (arg, genericStmt)                                                                 => matchDataStmt(stmts, genericStmt, arg, cur, next, nestedE, edgeB)
      }
    }._3.filterNot(_.e == -1)
  }


  def insertStmts(dataRows: Seq[Seq[Any]]): Seq[Seq[Statement]] = {
    val (genericStmts, genericTxStmts) = splitStmts()
    val dataStmtss: Seq[Seq[Statement]] = dataRows.map(resolveStmts(genericStmts, _))
    val txId = tempId("tx")
    val txStmtss: Seq[Seq[Statement]] = Seq(genericTxStmts.foldLeft(Seq[Statement]()) {
      case (stmts, Add('tx, a, Values(vs, prefix), _)) => valueStmts(stmts, txId, a, vs, prefix)
      case (stmts, unexpected)                         => iae("insertStmts", "Unexpected insert statement: " + unexpected)
    })
    dataStmtss ++ (if (txStmtss.head.isEmpty) Nil else txStmtss)
  }


  def saveStmts(): Seq[Statement] = {
    val txId = tempId("tx")
    stmtsModel.foldLeft(None: Option[AnyRef], Seq[Statement]()) { case ((edgeB, stmts), genericStmt) =>
      genericStmt match {

        // Beginning of edge
        case Add('e, a, refNs: String, bi@BiEdgeRef(_, _)) =>
          val (edgeA, edgeB1) = (tempId(refNs), Some(tempId(a)))
          (edgeB1, valueStmts(stmts, lastE(stmts, a), a, edgeA, None, bi, edgeB1))

        // End of edge
        case Add('e, a, refNs: String, bi@BiTargetRef(_, _)) =>
          (None, valueStmts(stmts, lastE(stmts, a), a, tempId(refNs), None, bi, edgeB))

        case Add('tempId, a, Values(vs, prefix), bi) => (edgeB, valueStmts(stmts, tempId(a), a, vs, prefix, bi, edgeB))
        case Add('e, a, Values(vs, prefix), bi)      => (edgeB, valueStmts(stmts, lastE(stmts, a), a, vs, prefix, bi, edgeB))
        case Add('e, a, 'tempId, bi)                 => (edgeB, valueStmts(stmts, lastE(stmts, a), a, tempId(a), None, bi, edgeB))
        case Add(e, a, 'tempId, bi)                  => (edgeB, valueStmts(stmts, e, a, tempId(a), None, bi, edgeB))
        case Add('e, a, refNs: String, bi)           => (edgeB, valueStmts(stmts, lastE(stmts, a), a, tempId(refNs), None, bi, edgeB))
        case Add('v, a, Values(vs, prefix), bi)      => (edgeB, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB))
        case Add('tx, a, Values(vs, prefix), bi)     => (edgeB, valueStmts(stmts, txId, a, vs, prefix, bi, edgeB))
        case Add('ns, a, _, _)                       => (edgeB, stmts)
        case Retract(_, _, _, _)                     => (edgeB, stmts)
        case Add(id: Long, a, Values(_, _), _)       => iae("saveStmts", s"With a given id `$id` please use `update` instead.")
        case Add(_, a, 'arg, _)                      => iae("saveStmts", s"Attribute `$a` needs a value applied")
        case unexpected                              => iae("saveStmts", "Unexpected save statement: " + unexpected)
      }
    }._2
  }


  def updateStmts(): Seq[Statement] = {
    val (genericStmts, genericTxStmts) = splitStmts()
    val dataStmts: Seq[Statement] = genericStmts.foldLeft(None: Option[AnyRef], Seq[Statement]()) { case ((edgeB, stmts), genericStmt) =>
      genericStmt match {

        // Beginning of edge
        case Add(e, a, 'tempId, bi@BiEdgeRef(_, _)) =>
          val (edgeA, edgeB1) = (tempId(a), Some(tempId(a)))
          (edgeB1, valueStmts(stmts, e, a, edgeA, None, bi, edgeB1))

        // End of edge
        case Add('e, a, refNs: String, bi@BiTargetRef(_, _)) =>
          (None, valueStmts(stmts, lastE(stmts, a), a, tempId(refNs), None, bi, edgeB))

        case Add('e, a, Values(vs, prefix), bi)     => (edgeB, valueStmts(stmts, lastE(stmts, a), a, vs, prefix, bi, edgeB))
        case Add('e, a, 'tempId, bi)                => (edgeB, valueStmts(stmts, lastE(stmts, a), a, tempId(a), None, bi, edgeB))
        case Add(e, a, 'tempId, bi)                 => (edgeB, valueStmts(stmts, e, a, tempId(a), None, bi, edgeB))
        case Add('v, a, Values(vs, prefix), bi)     => (edgeB, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB))
        case Add('tx, a, Values(vs, prefix), bi)    => (edgeB, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB))
        case Add(e, a, Values(vs, prefix), bi)      => (edgeB, valueStmts(stmts, e, a, vs, prefix, bi, edgeB))
        case Retract('e, a, Values(vs, prefix), bi) => (edgeB, valueStmts(stmts, lastE(stmts, a), a, vs, prefix, bi, edgeB))
        case Retract(e, a, Values(vs, prefix), bi)  => (edgeB, valueStmts(stmts, e, a, vs, prefix, bi, edgeB))
        case Add(_, a, 'arg, _)                     => iae("updateStmts", s"Attribute `$a` needs a value applied")
        case unexpected                             => iae("updateStmts", "Unexpected update statement: " + unexpected)
      }
    }._2
    val txId = tempId("tx")
    val txStmts: Seq[Statement] = genericTxStmts.foldLeft(Seq[Statement]()) {
      case (stmts, Add('tx, a, Values(vs, prefix), _)) => valueStmts(stmts, txId, a, vs, prefix)
      case (stmts, unexpected)                         => iae("updateStmts", "Unexpected insert statement: " + unexpected)
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