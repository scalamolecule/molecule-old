package molecule.core.transform

import java.util.Date
import molecule.core.ast.elements._
import molecule.core.util.Helpers
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.exception.Model2TransactionException
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}


/** Model to Statements transformer.
  *
  * */
case class ModelTransformerAsync(conn: Conn, model: Model) extends Helpers {

  private def err(method: String, msg: String) = {
    throw new Model2TransactionException(s"[$method]  $msg")
  }

  val datomicTx = "datomic.tx"

  // Save attribute cardinality/type info
  val attrInfo = mutable.Map(
    ":molecule_Meta/otherEdge" -> (1, "Long")
  )

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
      element match {
        case Atom(ns, attr, tpe, card, _, _, _, _) => attrInfo(s":$ns/$attr") = (card, tpe)
        case _                                     =>
      }
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
    model1.elements.foldLeft("_": Any, Seq[Statement]()) {
      case ((eSlot, stmts), element) => resolveElement(eSlot, stmts, element)
    }._2
  }

  private def tempId(attr: String): AnyRef = attr match {
    case null => err("__tempId", "Attribute name unexpectedly null.")
    case "tx" => datomicTx
    case _    =>
      if (attr.contains('_')) {
        // extract "partition" from ":partition_Namespace/attr"
        TempId(":" + attr.substring(1).split("(?=_)").head, conn.tempId.next)
      } else {
        TempId(":db.part/user", conn.tempId.next)
      }
  }

  private def getPairs(e: Any, a: String, key: String = ""): Future[Map[String, String]] = {
    //        val strs = if (key.isEmpty) {
    //          val query = "[:find ?v :in $ ?e ?a :where [?e ?a ?v]]"
    //          conn.q(query, e.asInstanceOf[Object], a).map(_.head)
    //        } else {
    //          val query = "[:find ?v :in $ ?e ?a ?key :where [?e ?a ?v][(.startsWith ^String ?v ?key)]]"
    //          conn.q(query, e.asInstanceOf[Object], a, key.asInstanceOf[Object]).map(_.head)
    //        }
    //        strs.map {
    //          case str: String =>
    //            val Seq(key: String, value: String) = str.split("@", 2).toSeq
    //            key -> value
    //        }.toMap

    val query = if (key.isEmpty) {
      s"[:find ?v :where [$e $a ?v]]"
    } else {
      s"[:find ?v :where [$e $a ?v][(.startsWith ^String ?v $key)]]"
    }
    getAttrValues(query, a).map { strs =>
      strs.map {
        case str: String =>
          val Seq(key: String, value: String) = str.split("@", 2).toSeq
          key -> value
      }.toMap
    }
  }


  private def getAttrValues(query: String, attr: String): Future[List[AnyRef]] = {
    val (card, tpe) = attrInfo(attr)
    val unMarshall  = card match {
      case 3 => (v: String) => v
      case _ => tpe match {
        case "String"     => (v: String) => v.asInstanceOf[AnyRef]
        case "Int"        => (v: String) => v.toInt.asInstanceOf[AnyRef]
        case "Long"       => (v: String) => v.toLong.asInstanceOf[AnyRef]
        case "Float"      => (v: String) => v.toDouble.asInstanceOf[AnyRef]
        case "Double"     => (v: String) => v.toDouble.asInstanceOf[AnyRef]
        case "BigInt"     => (v: String) => BigInt.apply(v).asInstanceOf[AnyRef]
        case "BigDecimal" => (v: String) => BigDecimal(v).asInstanceOf[AnyRef]
        case "Boolean"    => (v: String) => v.toBoolean.asInstanceOf[AnyRef]
        case "Date"       => (v: String) => str2date(v).asInstanceOf[AnyRef]
        case "UUID"       => (v: String) => java.util.UUID.fromString(v).asInstanceOf[AnyRef]
        case "URI"        => (v: String) => new java.net.URI(v).asInstanceOf[AnyRef]
      }
    }
    conn.getAttrValuesAsync(query, card, tpe).map { stringValues =>
      stringValues.map(unMarshall)
    }
  }

  private def valueStmts(
    stmts: Seq[Statement],
    e: Any,
    a: String,
    arg: Any,
    prefix: Option[String],
    bidirectional: GenericValue,
    otherEdgeId: Option[AnyRef]
  ): Future[Seq[Statement]] = {

    //    println(
    //      s"""e            : $e
    //         |a            : $a
    //         |arg          : $arg
    //         |prefix       : $prefix
    //         |bidirectional: $bidirectional
    //         |otherEdgeId  : $otherEdgeId
    //         |""".stripMargin
    //    )

    def p(v: Any): Any = v match {
      case f: Float              => f.toString.toDouble
      case _ if prefix.isDefined => Enum(prefix.get, v.toString)
      case bd: BigDecimal        => bd + 0.0 // ensure decimal digits
      case _                     => v
    }

    def d(v: Any) = v match {
      case d: Date => date2str(d)
      case other   => other
    }


    def attrValues(id: Any, attr: String): Future[Seq[AnyRef]] = {
      if (id.isInstanceOf[TempId]) {
        Future(Nil)
      } else {
        val query = if (prefix.isDefined) {
          s"""[:find ?enums
             | :where [$id $attr ?a]
             |        [?a :db/ident ?b]
             |        [(name ?b) ?enums]
             |]""".stripMargin
        } else {
          s"[:find ?values :where [$id $attr ?values]]"
        }
        if (conn.isJsPlatform) {
          getAttrValues(query, attr)
        } else {
          Future(conn.q(query).map(_.head)) // could be empty
        }
      }
    }

    def edgeAB(edge1: Any, targetAttr: String): Future[(Any, Any)] = {
      // todo: js-impl
      //      if (conn.isJsPlatform) {
      //      } else {
      //      }
      for {
        edge2 <- otherEdge(edge1)
        ent1 <- Future(conn.entity(edge1))
        ent2 <- Future(conn.entity(edge2))
      } yield {
        val map1 = ent1.mapOneLevel
        val map2 = ent2.mapOneLevel
        val s1   = map1.size
        val s2   = map2.size

        if (s1 > s2)
          (edge1, edge2)
        else if (s1 < s2)
          (edge2, edge1)
        else if (!map1.contains(targetAttr))
          (edge2, edge1)
        else
          (edge1, edge2)
      }
    }

    def otherEdge(edgeA: Any): Future[AnyRef] = {
      // Look for special attribute injected by Molecule if data model contains bidirectional refs
      val query     = s"[:find ?edgeB :where [$edgeA :molecule_Meta/otherEdge ?edgeB]]"
      val futResult = if (conn.isJsPlatform) {
        getAttrValues(query, ":molecule_Meta/otherEdge")
      } else {
        Future(conn.q(query).map(_.head))
      }

      futResult.map {
        case List(edgeB) => edgeB
        case Nil         =>
          val otherId = conn.entity(edgeA)
          err("valueStmts:biEdgeRef",
            s"Supplied id $edgeA doesn't appear to be a property edge id (couldn't find reverse edge id). " +
              s"Could it be another entity?:\n" + otherId.touchQuotedMax(2) +
              s"\nSpooky id: $otherId" +
              "\n" + stmts.size + " statements so far:\n" + stmts.mkString("\n")
          )
        case ids         => err("valueStmts:biEdgeRef",
          "Unexpectedly found multiple reverse edge ids:\n" + ids.mkString("\n"))
      }
    }

    def checkDupKeys(pairs: Seq[(String, Any)], host: String, op: String): Unit = {
      val dupKeys: Seq[String] = pairs.map(_._1).groupBy(identity).collect { case (key, keys) if keys.size > 1 => key }.toSeq
      if (dupKeys.nonEmpty) {
        val dupPairs = pairs.filter(p => dupKeys.contains(p._1)).sortBy(_._1).map { case (k, v) => s"$k -> $v" }
        err(s"valueStmts:$host",
          s"Can't $op multiple key/value pairs with the same key for attribute `$a`:\n" + dupPairs.mkString("\n"))
      }
    }

    def checkDupValuesOfPairs(pairs: Seq[(Any, Any)], host: String, op: String): Unit = {
      val dups = pairs.map(_._2).groupBy(identity).collect { case (v, vs) if vs.size > 1 => v }
      if (dups.nonEmpty)
        err(s"valueStmts:$host",
          s"Can't replace with duplicate new values of attribute `$a`:\n" + dups.mkString("\n"))
    }

    def checkDupValues(values: Seq[Any], host: String, op: String): Unit = {
      val dups = values.groupBy(identity).collect { case (v, vs) if vs.size > 1 => v }
      if (dups.nonEmpty)
        err(s"valueStmts:$host",
          s"Can't $op duplicate new values to attribute `$a`:\n" + dups.mkString("\n"))
    }

    def checkUnknownKeys(newPairs: Seq[(String, Any)], curKeys: Seq[String], host: String): Unit = {
      val unknownKeys = newPairs.map(_._1).diff(curKeys)
      if (unknownKeys.nonEmpty)
        err(s"valueStmts:$host",
          s"Can't replace non-existing keys of map attribute `$a`:\n" + unknownKeys.mkString("\n") +
            "\nYou might want to apply the values instead to replace all current values?")
    }


    // Bidirectional self-ref operations ------------------------------------------------------------------

    def biSelf(card: Int): Future[Iterable[Statement]] = arg match {

      case AssertValue(refs) => {
        //        refs.flatMap { case ref: Long =>
        //          val reverseRetracts = if (card == 1)
        //            attrValues(e, a).map(revRef => Retract(revRef, a, e))
        //          else
        //            Nil
        //          if (ref == e)
        //            err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same")
        //          reverseRetracts ++ Seq(Add(ref, a, e, Card(card)), Add(e, a, ref, Card(card)))
        //        }

        //        Future.sequence(
        //          refs.map {
        //            case `e`       => err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same")
        //            case ref: Long =>
        //              val refAdditions = Seq(
        //                Add(ref, a, e, Card(card)),
        //                Add(e, a, ref, Card(card))
        //              )
        //
        //              for {
        //                revRefs <- attrValues(e, a)
        //              } yield {
        //                val reverseStmts = if (card == 1) {
        //                  revRefs.map {
        //                    revRef => Retract(revRef, a, e)
        //                  }
        //                } else {
        //                  Nil
        //                }
        //                reverseStmts ++ refAdditions
        //              }
        //          }
        //        ).map(_.flatten)


        // €€€
        refs.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, ref) =>
          ref match {
            case `e` => err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same")
            case _   =>
              val refAdditions = Seq(
                Add(ref, a, e, Card(card)),
                Add(e, a, ref, Card(card))
              )
              futStmts.flatMap { stmts =>
                attrValues(e, a).map { revRefs =>
                  val reverseRetracts = if (card == 1) revRefs.map(revRef => Retract(revRef, a, e)) else Nil
                  stmts ++ reverseRetracts ++ refAdditions
                }
              }
          }
        }
      }

      case ReplaceValue(oldNew) => {
        //        attrValues(e, a).flatMap { revRef =>
        //          oldNew.flatMap {
        //            case (oldRef, newRef) if oldRef == revRef => Seq(
        //              // This entity e now has ref to newRef instead of oldRef
        //              Retract(e, a, oldRef), Add(e, a, newRef, Card(card)),
        //              // RevRef no longer has a ref to this entity e
        //              // Instead newRef has a ref to this entity e
        //              Retract(revRef, a, e), Add(newRef, a, e, Card(card))
        //            )
        //            case _                                    => Nil
        //          }
        //        }

        attrValues(e, a).map { revRefs =>
          revRefs.flatMap { revRef =>
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
        }
      }

      case RetractValue(removeRefs) => Future {
        removeRefs.flatMap {
          case ref: Long => Seq(Retract(ref, a, e), Retract(e, a, ref))
        }
      }

      case Eq(newRefs) => {
        if (newRefs.contains(e))
          err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")

        attrValues(e, a).map { oldRefs =>
          val retracts = oldRefs.flatMap {
            case oldRef if newRefs.contains(oldRef) => Nil
            case obsoleteRef                        => Seq(Retract(obsoleteRef, a, e), Retract(e, a, obsoleteRef))
          }
          val adds     = newRefs.flatMap {
            case newRef if oldRefs.contains(newRef) => Nil
            case newRef                             => Seq(Add(newRef, a, e, Card(card)), Add(e, a, newRef, Card(card)))
          }
          retracts ++ adds
        }
      }

      case refs: Set[_] => Future {
        refs.flatMap {
          case ref: Long => Seq(Add(ref, a, e, Card(card)), Add(e, a, ref, Card(card)))
        }
      }

      case ref => {
        //        val reverseRetracts = if (card == 1) attrValues(e, a).map(revRef => Retract(revRef, a, e)) else Nil
        //        reverseRetracts ++ Seq(Add(ref, a, e, Card(card)), Add(e, a, ref, Card(card)))
        val edgeStmts = Seq(
          Add(ref, a, e, Card(card)),
          Add(e, a, ref, Card(card))
        )
        if (card == 1) {
          attrValues(e, a).map { reverseRetracts =>
            reverseRetracts.map(revRef => Retract(revRef, a, e)) ++ edgeStmts
          }
        } else {
          Future(edgeStmts)
        }
      }
    }


    // Bidirectional other-ref operations ------------------------------------------------------------------

    def biOther(card: Int, revRefAttr: String): Future[Iterable[Statement]] = arg match {

      case AssertValue(refs) => {
        //        refs.flatMap { case ref: Long =>
        //          if (ref == e)
        //            err("valueStmts:biOther", "Current entity and referenced entity ids can't be the same")
        //          val reverseRetracts = if (card == 1) attrValues(e, a).map(revRef => Retract(revRef, a, e)) else Nil
        //          reverseRetracts ++ Seq(Add(ref, revRefAttr, e, Card(card)), Add(e, a, ref, Card(card)))
        //        }

        //        val res1 = Future.sequence(
        //          refs.map {
        //            case `e`       => err("valueStmts:biOther", "Current entity and referenced entity ids can't be the same")
        //            case ref: Long =>
        //              val refAdditions = Seq(
        //                Add(ref, revRefAttr, e, Card(card)),
        //                Add(e, a, ref, Card(card))
        //              )
        //
        //              for {
        //                revRefs <- attrValues(e, a)
        //              } yield {
        //                val reverseRetracts = if (card == 1) {
        //                  revRefs.map {
        //                    revRef => Retract(revRef, a, e)
        //                  }
        //                } else {
        //                  Nil
        //                }
        //                reverseRetracts ++ refAdditions
        //              }
        //          }
        //        ).map(_.flatten)
        //
        //        println(Await.result(res1, 10.seconds).mkString("=============== stmts 1:\n", "\n", ""))


        val res2 = refs.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, ref) =>
          ref match {
            case `e` => err("valueStmts:biOther", "Current entity and referenced entity ids can't be the same")
            case _   =>
              val refAdditions = Seq(
                Add(ref, revRefAttr, e, Card(card)),
                Add(e, a, ref, Card(card))
              )
              futStmts.flatMap { stmts =>
                attrValues(e, a).map { revRefs =>
                  val reverseRetracts = if (card == 1) revRefs.map(revRef => Retract(revRef, a, e)) else Nil
                  stmts ++ reverseRetracts ++ refAdditions
                }
              }
          }
        }
        //        println(Await.result(res2, 10.seconds).mkString("=============== stmts 2:\n", "\n", ""))
        res2
      }

      case ReplaceValue(oldNew) => {
        //        attrValues(e, a).flatMap { case revRef =>
        //          oldNew.flatMap {
        //            case (oldRef, newRef) if oldRef == revRef => Seq(
        //              // This entity e now has ref to newRef instead of oldRef
        //              Retract(e, a, oldRef), Add(e, a, newRef, Card(card)),
        //              // RevRef no longer has a ref to this entity e
        //              // Instead newRef has a ref to this entity e
        //              Retract(revRef, revRefAttr, e, Card(card)), Add(newRef, revRefAttr, e, Card(card))
        //            )
        //            case _                                    => Nil
        //          }
        //        }
        attrValues(e, a).map { revRefs =>
          revRefs.flatMap { revRef =>
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
        }
      }

      case RetractValue(removeRefs) => Future {
        removeRefs.flatMap {
          case ref: Long => Seq(Retract(ref, revRefAttr, e), Retract(e, a, ref))
        }
      }

      case Eq(newRefs) => {
        if (newRefs.contains(e))
          err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")

        //        val oldRefs  = attrValues(e, a)
        //        val retracts = oldRefs.flatMap {
        //          case oldRef if newRefs.contains(oldRef) => Nil
        //          case obsoleteRef                        => Seq(Retract(obsoleteRef, revRefAttr, e), Retract(e, a, obsoleteRef))
        //        }
        //        val adds     = newRefs.flatMap {
        //          case newRef if oldRefs.contains(newRef) => Nil
        //          case newRef                             => Seq(Add(newRef, revRefAttr, e, Card(card)), Add(e, a, newRef, Card(card)))
        //        }
        //        retracts ++ adds

        attrValues(e, a).map { oldRefs =>
          val retracts = oldRefs.flatMap {
            case oldRef if newRefs.contains(oldRef) => Nil
            case obsoleteRef                        => Seq(Retract(obsoleteRef, revRefAttr, e), Retract(e, a, obsoleteRef))
          }
          val adds     = newRefs.flatMap {
            case newRef if oldRefs.contains(newRef) => Nil
            case newRef                             => Seq(Add(newRef, revRefAttr, e, Card(card)), Add(e, a, newRef, Card(card)))
          }
          retracts ++ adds
        }
      }

      case refs: Set[_] => Future {
        refs.flatMap {
          case ref: Long => Seq(Add(ref, revRefAttr, e, Card(card)), Add(e, a, ref, Card(card)))
        }
      }

      case ref => {
        //        val reverseRetracts = if (card == 1) attrValues(e, a).map(revRef => Retract(revRef, revRefAttr, e)) else Nil
        //        reverseRetracts ++ Seq(Add(ref, revRefAttr, e, Card(card)), Add(e, a, ref, Card(card)))
        val edgeStmts = Seq(
          Add(ref, revRefAttr, e, Card(card)),
          Add(e, a, ref, Card(card))
        )
        if (card == 1) {
          attrValues(e, a).map { reverseRetracts =>
            reverseRetracts.map(revRef => Retract(revRef, revRefAttr, e)) ++ edgeStmts
          }
        } else {
          Future(edgeStmts)
        }
      }
    }


    // Bidirectional edge operations -------------------------------------------------------------------

    def biEdgeRefAttr(card: Int, targetAttr: String): Future[Iterable[Statement]] = arg match {

      case AssertValue(edges) => {
        if (edges.contains(e))
          err("valueStmts:biEdgeRefAttr", "Current entity and referenced entity ids can't be the same.")

        checkDupValues(edges, "biEdgeRefAttr", "assert")

        //        edges.flatMap { edge =>
        //          val (edgeA, edgeB) = edgeAB(edge, targetAttr)
        //          Seq(Add(edgeB, targetAttr, e, Card(card)), Add(e, a, edgeA, Card(card)))
        //        }

        //        Future.sequence(
        //          edges.map { edge =>
        //            for {
        //              (edgeA, edgeB) <- edgeAB(edge, targetAttr)
        //            } yield {
        //              Seq(Add(edgeB, targetAttr, e, Card(card)), Add(e, a, edgeA, Card(card)))
        //            }
        //          }
        //        ).map(_.flatten)

        // €€€
        edges.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, edge) =>
          futStmts.flatMap { stmts =>
            edgeAB(edge, targetAttr).map { case (edgeA, edgeB) =>
              stmts ++ Seq(
                Add(edgeB, targetAttr, e, Card(card)),
                Add(e, a, edgeA, Card(card))
              )
            }
          }
        }
      }

      case ReplaceValue(oldNewEdges) => {
        checkDupValuesOfPairs(oldNewEdges, "biEdgeRefAttr", "replace")
        //        oldNewEdges.flatMap { case (oldEdge, newEdge) =>
        //          val (edgeA, edgeB) = edgeAB(newEdge, targetAttr)
        //          Seq(
        //            RetractEntity(oldEdge),
        //            Add(edgeB, targetAttr, e, Card(card)),
        //            Add(e, a, edgeA, Card(card))
        //          )
        //        }

        //        Future.sequence(
        //          oldNewEdges.map {
        //            case (oldEdge, newEdge) =>
        //              for {
        //                (edgeA, edgeB) <- edgeAB(newEdge, targetAttr)
        //              } yield {
        //                Seq(
        //                  RetractEntity(oldEdge),
        //                  Add(edgeB, targetAttr, e, Card(card)),
        //                  Add(e, a, edgeA, Card(card))
        //                )
        //              }
        //          }
        //        ).map(_.flatten)

        // €€€
        oldNewEdges.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, (oldEdge, newEdge)) =>
          futStmts.flatMap { stmts =>
            edgeAB(newEdge, targetAttr).map { case (edgeA, edgeB) =>
              stmts ++ Seq(
                RetractEntity(oldEdge),
                Add(edgeB, targetAttr, e, Card(card)),
                Add(e, a, edgeA, Card(card))
              )
            }
          }
        }
      }

      case RetractValue(edges) => Future(edges map RetractEntity)

      case Eq(newEdges) => {
        if (newEdges.contains(e))
          err("valueStmts:biEdgeRefAttr", "Current entity and referenced entity ids can't be the same.")

        checkDupValues(newEdges, "biEdgeRefAttr", "apply")

        //        val oldEdges = attrValues(e, a)
        //        val retracts = oldEdges.flatMap {
        //          case oldEdgeA if newEdges.contains(oldEdgeA) => Nil
        //          case oldEdgeA                                => Seq(RetractEntity(oldEdgeA))
        //        }
        //        val adds     = newEdges.flatMap {
        //          case edge if oldEdges.contains(edge) => Nil
        //          case edge                            =>
        //            val (edgeA, edgeB) = edgeAB(edge, targetAttr)
        //            Seq(
        //              Add(edgeB, targetAttr, e, Card(card)),
        //              Add(e, a, edgeA, Card(card))
        //            )
        //        }
        //        retracts ++ adds

        attrValues(e, a).flatMap { oldEdges =>
          val retracts: Seq[Statement] = oldEdges.flatMap {
            case oldEdgeA if newEdges.contains(oldEdgeA) => Nil
            case oldEdgeA                                => Seq(RetractEntity(oldEdgeA))
          }

          //          println("@@@@@@@@@@@@@@@@ " + retracts)
          //          val res1 = Future.sequence(
          //            newEdges.map {
          //              case edge if oldEdges.contains(edge) => Future(retracts)
          //              case edge                            =>
          //                for {
          //                  (edgeA, edgeB) <- edgeAB(edge, targetAttr)
          //                } yield {
          //                  retracts ++ Seq(
          //                    Add(edgeB, targetAttr, e, Card(card)),
          //                    Add(e, a, edgeA, Card(card))
          //                  )
          //                }
          //            }
          //          ).map(_.flatten)
          //          println(Await.result(res1, 10.seconds).mkString("=============== stmts 1:\n", "\n", ""))


          //          val res2 = newEdges.foldLeft(Future(Seq.empty[Statement])) {
          val res2 = newEdges.foldLeft(Future(retracts)) {
            case (futStmts, newEdge) if oldEdges.contains(newEdge) => Future(retracts)
            case (futStmts, newEdge)                               =>

              futStmts.flatMap { stmts =>
                edgeAB(newEdge, targetAttr).map { case (edgeA, edgeB) =>
                  stmts ++ Seq(
                    Add(edgeB, targetAttr, e, Card(card)),
                    Add(e, a, edgeA, Card(card))
                  )
                }
              }
          }
          //          println(Await.result(res2, 10.seconds).mkString("=============== stmts 2:\n", "\n", ""))

          res2
        }
      }

      case edges: Set[_] if card == 2 => {
        //        edges.flatMap { case edge: Long =>
        //          val (edgeA, edgeB) = edgeAB(edge, targetAttr)
        //          Seq(Add(edgeB, targetAttr, e, Card(card)), Add(e, a, edgeA, Card(card)))
        //        }

        //        println("############")
        //        Future.sequence(
        //          edges.map { edge =>
        //            for {
        //              (edgeA, edgeB) <- edgeAB(edge, targetAttr)
        //            } yield {
        //              Seq(
        //                Add(edgeB, targetAttr, e, Card(card)),
        //                Add(e, a, edgeA, Card(card))
        //              )
        //            }
        //          }
        //        ).map(_.flatten)

        // €€€
        edges.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, edge) =>
          futStmts.flatMap { stmts =>
            edgeAB(edge, targetAttr).map { case (edgeA, edgeB) =>
              stmts ++ Seq(
                Add(edgeB, targetAttr, e, Card(card)),
                Add(e, a, edgeA, Card(card))
              )
            }
          }
        }

      }

      case edge: Long =>
        //        val (edgeA, edgeB)  = edgeAB(edge, targetAttr)
        //        val reverseRetracts = if (card == 1) attrValues(e, a).map(oldEdgeA => RetractEntity(oldEdgeA)) else Nil
        //        reverseRetracts ++ Seq(Add(edgeB, targetAttr, e, Card(card)), Add(e, a, edgeA, Card(card)))

        edgeAB(edge, targetAttr).flatMap { case (edgeA, edgeB) =>
          val addStmts = Seq(
            Add(edgeB, targetAttr, e, Card(card)),
            Add(e, a, edgeA, Card(card))
          )
          if (card == 1) {
            attrValues(e, a).map { reverseRetracts =>
              reverseRetracts.map(oldEdgeA => RetractEntity(oldEdgeA)) ++ addStmts
            }
          } else {
            Future(addStmts)
          }
        }
    }


    def biEdgeRef(card: Int, targetAttr: String): Future[Iterable[Statement]] = arg match {

      case RetractValue(edges) => Future(edges map RetractEntity)

      case Eq(newEdges) => {
        if (newEdges.contains(e))
          err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")

        checkDupValues(newEdges, "biEdgeRef", "apply")

        //        val oldEdges = attrValues(e, a)
        //        val retracts = oldEdges.flatMap {
        //          case oldEdgeA if newEdges.contains(oldEdgeA) => Nil
        //          case oldEdgeA                                => Seq(RetractEntity(oldEdgeA))
        //        }
        //        val adds     = newEdges.flatMap {
        //          case edge if oldEdges.contains(edge) => Nil
        //          case edge                            =>
        //            val (edgeA, edgeB) = edgeAB(edge, targetAttr)
        //            Seq(
        //              Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
        //              Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card)),
        //              Add(edgeB, targetAttr, e, Card(card)),
        //              Add(e, a, edgeA, Card(card))
        //            )
        //        }
        //        retracts ++ adds

        attrValues(e, a).flatMap { oldEdges =>
          val retracts = oldEdges.flatMap {
            case oldEdgeA if newEdges.contains(oldEdgeA) => Nil
            case oldEdgeA                                => Seq(RetractEntity(oldEdgeA))
          }
          //          Future.sequence(
          //            newEdges.map {
          //              case edge if oldEdges.contains(edge) => Future(retracts)
          //              case edge                            =>
          //                for {
          //                  (edgeA, edgeB) <- edgeAB(edge, targetAttr)
          //                } yield {
          //                  retracts ++ Seq(
          //                    Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
          //                    Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card)),
          //                    Add(edgeB, targetAttr, e, Card(card)),
          //                    Add(e, a, edgeA, Card(card))
          //                  )
          //                }
          //            }
          //          ).map(_.flatten)

          // €€€
          newEdges.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, edge) =>
            if (oldEdges.contains(edge)) {
              Future(retracts)
            } else {
              futStmts.flatMap { stmts =>
                edgeAB(edge, targetAttr).map { case (edgeA, edgeB) =>
                  stmts ++ retracts ++ Seq(
                    Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
                    Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card)),
                    Add(edgeB, targetAttr, e, Card(card)),
                    Add(e, a, edgeA, Card(card))
                  )
                }
              }
            }
          }
        }
      }

      case edgeA => {
        val edgeB = otherEdgeId getOrElse err("valueStmts:biEdgeRef", "Missing id of other edge.")

        //        val reverseRetracts = if (card == 1) attrValues(e, a).map(oldEdgeA => RetractEntity(oldEdgeA)) else Nil
        //        reverseRetracts ++ Seq(
        //          // Interlink edge entities so that we later know which other one to update
        //          Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
        //          Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card)),
        //          Add(edgeB, targetAttr, e, Card(card)),
        //          Add(e, a, edgeA, Card(card))
        //        )

        val addStmts = Seq(
          // Interlink edge entities so that we later know which other one to update
          Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
          Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card)),
          Add(edgeB, targetAttr, e, Card(card)),
          Add(e, a, edgeA, Card(card))
        )
        if (card == 1) {
          attrValues(e, a).map { reverseRetracts =>
            reverseRetracts.map(oldEdgeA => RetractEntity(oldEdgeA)) ++ addStmts
          }
        } else {
          Future(addStmts)
        }
      }
    }


    def biEdgeProp(card: Int): Future[Iterable[Statement]] = {
      val edgeA   : Any            = e
      val futEdgeB: Future[AnyRef] = otherEdgeId match {
        case Some(eid) if eid == edgeA =>
          err("valueStmts:biEdgeProp", "Other edge id is unexpectedly the same as this edge id.")
        case Some(eid)                 => Future(eid)
        case None                      =>
          otherEdge(edgeA)
      }

      val futArgStmts: Future[Iterable[Statement]] = arg match {

        case AssertMapPairs(newPairs) => {
          checkDupKeys(newPairs, "biEdgeProp", "assert")

          //          val curPairs     = getPairs(edgeA, a)
          //          val curPairsList = curPairs.toList
          //          val curKeys      = curPairs.keys.toList
          //          newPairs.flatMap {
          //            case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
          //            case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
          //            case (k, v) if curKeys.contains(k)                       => Seq(
          //              Retract(edgeB, a, k + "@" + curPairs(k)), Add(edgeB, a, k + "@" + d(v), Card(card)),
          //              Retract(edgeA, a, k + "@" + curPairs(k)), Add(edgeA, a, k + "@" + d(v), Card(card))
          //            )
          //            case (k, v)                                              => Seq(
          //              Add(edgeB, a, k + "@" + d(v), Card(card)),
          //              Add(edgeA, a, k + "@" + d(v), Card(card))
          //            )
          //          }

          getPairs(edgeA, a).flatMap { curPairs =>
            val curPairsList = curPairs.toList
            val curKeys      = curPairs.keys.toList
            futEdgeB.map { edgeB =>
              newPairs.flatMap {
                case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
                case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
                case (k, v) if curKeys.contains(k)                       => Seq(
                  Retract(edgeB, a, k + "@" + curPairs(k)), Add(edgeB, a, k + "@" + d(v), Card(card)),
                  Retract(edgeA, a, k + "@" + curPairs(k)), Add(edgeA, a, k + "@" + d(v), Card(card))
                )
                case (k, v)                                              => Seq(
                  Add(edgeB, a, k + "@" + d(v), Card(card)),
                  Add(edgeA, a, k + "@" + d(v), Card(card))
                )
              }
            }
          }
        }

        case ReplaceMapPairs(newPairs) => {
          checkDupKeys(newPairs, "biEdgeProp", "replace")

          //          val curPairs     = getPairs(edgeA, a)
          //          val curPairsList = curPairs.toList
          //          val curKeys      = curPairs.keys.toList
          //          checkUnknownKeys(newPairs, curKeys.toSeq, "biEdgeProp")
          //          newPairs.flatMap {
          //            case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
          //            case (k, v) if curKeys.contains(k)                       => Seq(
          //              Retract(edgeB, a, k + "@" + curPairs(k)), Add(edgeB, a, k + "@" + d(v), Card(card)),
          //              Retract(edgeA, a, k + "@" + curPairs(k)), Add(edgeA, a, k + "@" + d(v), Card(card))
          //            )
          //            case (k, v)                                              => Seq(
          //              Add(edgeB, a, k + "@" + d(v), Card(card)),
          //              Add(edgeA, a, k + "@" + d(v), Card(card))
          //            )
          //          }

          getPairs(edgeA, a).flatMap { curPairs =>
            val curPairsList = curPairs.toList
            val curKeys      = curPairs.keys.toList
            checkUnknownKeys(newPairs, curKeys, "biEdgeProp")
            futEdgeB.map { edgeB =>
              newPairs.flatMap {
                case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
                case (k, v) if curKeys.contains(k)                       => Seq(
                  Retract(edgeB, a, k + "@" + curPairs(k)), Add(edgeB, a, k + "@" + d(v), Card(card)),
                  Retract(edgeA, a, k + "@" + curPairs(k)), Add(edgeA, a, k + "@" + d(v), Card(card))
                )
                case (k, v)                                              => Seq(
                  Add(edgeB, a, k + "@" + d(v), Card(card)),
                  Add(edgeA, a, k + "@" + d(v), Card(card))
                )
              }
            }
          }
        }

        case RetractMapKeys(removeKeys0) => {
          val removeKeys = removeKeys0.distinct

          //          val oldPairs   = getPairs(edgeA, a)
          //          oldPairs.flatMap {
          //            case (oldK, oldV) if removeKeys.contains(oldK) =>
          //              Seq(Retract(edgeB, a, s"$oldK@$oldV"), Retract(edgeA, a, s"$oldK@$oldV"))
          //            case (oldK, oldV)                              => Nil
          //          }

          getPairs(edgeA, a).flatMap { oldPairs =>
            futEdgeB.map { edgeB =>
              oldPairs.flatMap {
                case (oldK, oldV) if removeKeys.contains(oldK) =>
                  Seq(Retract(edgeB, a, s"$oldK@$oldV"), Retract(edgeA, a, s"$oldK@$oldV"))
                case (oldK, oldV)                              => Nil
              }
            }
          }
        }

        case MapEq(newPairs) => {
          checkDupKeys(newPairs, "biEdgeProp", "apply")

          //          edgeA match {
          //            case updateE: Long => {
          //              val oldPairs  = getPairs(edgeA, a)
          //              val newPairs1 = newPairs.map { case (k, v) => (k, d(v).toString) }
          //              val retracts  = oldPairs.flatMap {
          //                case (oldK, oldV) if newPairs1.contains((oldK, oldV)) => Nil
          //                case (oldK, oldV)                                     =>
          //                  Seq(Retract(edgeB, a, s"$oldK@$oldV"), Retract(edgeA, a, s"$oldK@$oldV"))
          //              }
          //              val adds      = newPairs.flatMap { case (k, v) => Seq(
          //                Add(edgeB, a, k + "@" + d(v), Card(card)),
          //                Add(edgeA, a, k + "@" + d(v), Card(card))
          //              )
          //              }
          //              retracts ++ adds
          //            }
          //            case newE          =>
          //              newPairs.flatMap { case (k, v) => Seq(
          //                Add(edgeB, a, k + "@" + d(v), Card(card)),
          //                Add(edgeA, a, k + "@" + d(v), Card(card))
          //              )
          //              }
          //          }

          edgeA match {
            case updateE: Long => {
              getPairs(edgeA, a).flatMap { oldPairs =>
                val newPairs1 = newPairs.map { case (k, v) => (k, d(v).toString) }
                futEdgeB.map { edgeB =>
                  val retracts = oldPairs.flatMap {
                    case (oldK, oldV) if newPairs1.contains((oldK, oldV)) => Nil
                    case (oldK, oldV)                                     =>
                      Seq(Retract(edgeB, a, s"$oldK@$oldV"), Retract(edgeA, a, s"$oldK@$oldV"))
                  }
                  val adds     = newPairs.flatMap { case (k, v) => Seq(
                    Add(edgeB, a, k + "@" + d(v), Card(card)),
                    Add(edgeA, a, k + "@" + d(v), Card(card))
                  )
                  }
                  retracts ++ adds
                }
              }
            }

            case newE => futEdgeB.map { edgeB =>
              newPairs.flatMap {
                case (k, v) => Seq(
                  Add(edgeB, a, k + "@" + d(v), Card(card)),
                  Add(edgeA, a, k + "@" + d(v), Card(card))
                )
              }
            }
          }
        }

        case AssertValue(values) => futEdgeB.map { edgeB =>
          values.distinct.flatMap(v =>
            Seq(
              Add(edgeB, a, p(v), Card(card)),
              Add(edgeA, a, p(v), Card(card))
            )
          )
        }

        case ReplaceValue(oldNew) => futEdgeB.map { edgeB =>
          checkDupValuesOfPairs(oldNew, "biEdgeProp", "replace")
          oldNew.flatMap { case (oldValue, newValue) => Seq(
            Retract(edgeB, a, p(oldValue)), Add(edgeB, a, p(newValue), Card(card)),
            Retract(edgeA, a, p(oldValue)), Add(edgeA, a, p(newValue), Card(card)))
          }
        }

        case RetractValue(removeValues) => futEdgeB.map { edgeB =>
          removeValues.distinct.flatMap(v =>
            Seq(
              Retract(edgeB, a, p(v)),
              Retract(edgeA, a, p(v))
            )
          )
        }

        case Eq(newValues) => {
          if (newValues.contains(e))
            err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")

          //          val curValues       = attrValues(edgeA, a)
          //          val newValueStrings = newValues.map(_.toString)
          //          val curValueStrings = curValues.map(_.toString)
          //          val retracts        = curValues.flatMap {
          //            case curValue if newValueStrings.contains(curValue.toString) => Nil
          //            case obsoleteValue                                           =>
          //              Seq(Retract(edgeB, a, p(obsoleteValue)), Retract(edgeA, a, p(obsoleteValue)))
          //          }
          //          val adds            = newValues.flatMap {
          //            case newValue if curValueStrings.contains(newValue.toString) => Nil
          //            case newValue                                                => Seq(
          //              Add(edgeB, a, p(newValue), Card(card)),
          //              Add(edgeA, a, p(newValue), Card(card))
          //            )
          //          }
          //          retracts ++ adds

          attrValues(edgeA, a).flatMap { curValues =>
            val newValueStrings = newValues.map(_.toString)
            val curValueStrings = curValues.map(_.toString)

            futEdgeB.map { edgeB =>
              val retracts = curValues.flatMap {
                case curValue if newValueStrings.contains(curValue.toString) => Nil
                case obsoleteValue                                           =>
                  Seq(Retract(edgeB, a, p(obsoleteValue)), Retract(edgeA, a, p(obsoleteValue)))
              }
              val adds     = newValues.flatMap {
                case newValue if curValueStrings.contains(newValue.toString) => Nil
                case newValue                                                => Seq(
                  Add(edgeB, a, p(newValue), Card(card)),
                  Add(edgeA, a, p(newValue), Card(card))
                )
              }
              retracts ++ adds
            }
          }
        }

        case m: Map[_, _] => futEdgeB.map { edgeB =>
          m.flatMap { case (k, v) =>
            Seq(
              Add(edgeB, a, s"$k@${d(v)}", Card(card)),
              Add(edgeA, a, s"$k@${d(v)}", Card(card))
            )
          }
        }

        case vs: Set[_] => futEdgeB.map { edgeB =>
          vs.toSeq.flatMap(v =>
            Seq(
              Add(edgeB, a, p(v), Card(card)),
              Add(edgeA, a, p(v), Card(card)))
          )
        }

        case v :: Nil => futEdgeB.map { edgeB =>
          Seq(
            Add(edgeB, a, p(v), Card(card)),
            Add(edgeA, a, p(v), Card(card))
          )
        }

        case vs: List[_] => futEdgeB.map { edgeB =>
          vs.flatMap(v => Seq(
            Add(edgeB, a, p(v), Card(card)),
            Add(edgeA, a, p(v), Card(card)))
          )
        }

        case v => futEdgeB.map { edgeB =>
          Seq(
            Add(edgeB, a, p(v), Card(card)),
            Add(edgeA, a, p(v), Card(card))
          )
        }
      }

      val edgeConnectors: Future[Seq[Add]] = futEdgeB.map { edgeB =>
        stmts.collectFirst {
          case Add(_, ":molecule_Meta/otherEdge", _, _) => Nil
        } getOrElse Seq(
          Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
          Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card))
        )
      }

      for {
        argStmts <- futArgStmts
        edgeConnectors1 <- edgeConnectors
      } yield {
        edgeConnectors1 ++ argStmts
      }
    }

    def biTarget(card: Int, biEdgeRefAttr: String): Future[Iterable[Statement]] = Future {
      val edgeA = e
      val edgeB = otherEdgeId getOrElse err("valueStmts:biTargetRef", "Missing id of other edge.")
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

    def default(card: Int): Future[Iterable[Statement]] = arg match {

      case AssertMapPairs(newPairs) => {
        checkDupKeys(newPairs, "default", "assert")

        //        val curPairs     = getPairs(e, a)
        //        val curPairsList = curPairs.toList
        //        val curKeys      = curPairs.keys.toList
        //        newPairs.flatMap {
        //          case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
        //          case (k, v) if curKeys.contains(k)                       => Seq(Retract(e, a, s"$k@${curPairs(k)}"), Add(e, a, s"$k@${d(v)}", Card(card)))
        //          case (k, v)                                              => Seq(Add(e, a, s"$k@${d(v)}", Card(card)))
        //        }

        getPairs(e, a).map { curPairs =>
          val curPairsList = curPairs.toList
          val curKeys      = curPairs.keys.toList
          newPairs.flatMap {
            case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
            case (k, v) if curKeys.contains(k)                       =>
              Seq(Retract(e, a, s"$k@${curPairs(k)}"), Add(e, a, s"$k@${d(v)}", Card(card)))
            case (k, v)                                              =>
              Seq(Add(e, a, s"$k@${d(v)}", Card(card)))
          }
        }
      }

      case ReplaceMapPairs(newPairs) => {
        checkDupKeys(newPairs, "default", "replace")
        //        val curPairs     = getPairs(e, a)
        //        val curPairsList = curPairs.toList
        //        val curKeys      = curPairs.keys.toList
        //        checkUnknownKeys(newPairs, curKeys, "default")
        //        newPairs.flatMap {
        //          case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
        //          case (k, v) if curKeys.contains(k)                       => Seq(Retract(e, a, s"$k@${curPairs(k)}"), Add(e, a, s"$k@${d(v)}", Card(card)))
        //          case (k, v)                                              => Seq(Add(e, a, s"$k@${d(v)}", Card(card)))
        //        }
        getPairs(e, a).map { curPairs =>
          val curPairsList = curPairs.toList
          val curKeys      = curPairs.keys.toList
          checkUnknownKeys(newPairs, curKeys, "default")
          newPairs.flatMap {
            case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
            case (k, v) if curKeys.contains(k)                       =>
              Seq(Retract(e, a, s"$k@${curPairs(k)}"), Add(e, a, s"$k@${d(v)}", Card(card)))
            case (k, v)                                              =>
              Seq(Add(e, a, s"$k@${d(v)}", Card(card)))
          }
        }
      }

      case RetractMapKeys(removeKeys0) => {
        val removeKeys = removeKeys0.distinct
        //        val oldPairs   = getPairs(e, a)
        //        oldPairs.flatMap {
        //          case (oldK, oldV) if removeKeys.contains(oldK) => Seq(Retract(e, a, s"$oldK@$oldV"))
        //          case (oldK, oldV)                              => Nil
        //        }
        getPairs(e, a).map { oldPairs =>
          oldPairs.flatMap {
            case (oldK, oldV) if removeKeys.contains(oldK) => Seq(Retract(e, a, s"$oldK@$oldV"))
            case (oldK, oldV)                              => Nil
          }
        }
      }
      case MapEq(newPairs)             => {
        checkDupKeys(newPairs, "default", "apply")
        //        e match {
        //          case updateE: Long => {
        //            val oldPairs  = getPairs(e, a)
        //            val newPairs1 = newPairs.map { case (k, v) => (k, d(v).toString) }
        //            val retracts  = oldPairs.flatMap {
        //              case (oldK, oldV) if newPairs1.contains((oldK, oldV)) => Nil
        //              case (oldK, oldV)                                     => Seq(Retract(e, a, s"$oldK@$oldV"))
        //            }
        //            val adds      = newPairs.map { case (k, v) => Add(e, a, k + "@" + d(v), Card(card)) }
        //            retracts ++ adds
        //          }
        //          case newE          =>
        //            newPairs.map { case (k, v) => Add(e, a, k + "@" + d(v), Card(card)) }
        //        }

        e match {
          case updateE: Long => {
            getPairs(e, a).map { oldPairs =>
              val newPairs1 = newPairs.map { case (k, v) => (k, d(v).toString) }
              val retracts  = oldPairs.flatMap {
                case (oldK, oldV) if newPairs1.contains((oldK, oldV)) => Nil
                case (oldK, oldV)                                     => Seq(Retract(e, a, s"$oldK@$oldV"))
              }
              val adds      = newPairs.map { case (k, v) => Add(e, a, k + "@" + d(v), Card(card)) }
              retracts ++ adds
            }
          }

          case newE => Future(newPairs.map { case (k, v) => Add(e, a, k + "@" + d(v), Card(card)) })
        }
      }

      case AssertValue(values) => Future(flatten(values).distinct.map(v => Add(e, a, p(v), Card(card))))

      case ReplaceValue(oldNew) => Future {
        checkDupValuesOfPairs(oldNew, "default", "replace")
        oldNew.flatMap { case (oldValue, newValue) =>
          Seq(
            Retract(e, a, p(oldValue)),
            Add(e, a, p(newValue), Card(card))
          )
        }
      }

      case RetractValue(removeValues) => Future(flatten(removeValues).distinct.map(v => Retract(e, a, p(v))))

      case Eq(newValues0) => {
        val newValues    = flatten(newValues0).distinct
        val futCurValues = if (e == datomicTx) Future(Nil) else attrValues(e, a)
        futCurValues.map { curValues =>
          val newValueStrings = newValues.map(_.toString)
          val curValueStrings = curValues.map(_.toString)
          val retracts        = if (card == 2 || (newValues.isEmpty && curValues.nonEmpty))
            curValues.flatMap {
              case curValue if newValueStrings.contains(curValue.toString) => Nil
              case obsoleteValue                                           => Seq(Retract(e, a, p(obsoleteValue)))
            }
          else
            Nil
          val adds            = newValues.flatMap {
            case newValue if curValueStrings.contains(newValue.toString) => Nil
            case newValue                                                => Seq(Add(e, a, p(newValue), Card(card)))
          }
          retracts ++ adds
        }
      }

      case m: Map[_, _] => Future(m.map { case (k, v) => Add(e, a, s"$k@${d(v)}", Card(card)) })
      case vs: Set[_]   => Future(vs.map(v => Add(e, a, p(v), Card(card))))
      case v :: Nil     => Future(Seq(Add(e, a, p(v), Card(card))))
      case vs: List[_]  => Future(vs.map(v => Add(e, a, p(v), Card(card))))
      case v            => Future(Seq(Add(e, a, p(v), Card(card))))
    }

    val futNewStmts = bidirectional match {
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
      case other                       => throw new Model2TransactionException(
        s"""Unexpected Generic `$other`:
           |e  : $e
           |a  : $a
           |arg: $arg
         """.stripMargin)
    }
    //    futNewStmts.map { newStmts =>
    //      println(stmts.mkString("---------- stmts\n", "\n", ""))
    //      println(newStmts.mkString("---------- newStmts\n", "\n", "\n======================"))
    //      stmts ++ newStmts
    //    }
    futNewStmts.map(stmts ++ _)
  }


  private def flatten(vs: Seq[Any]): Seq[Any] = vs.flatMap {
    case set: Set[_] => set.toSeq
    case v           => Seq(v)
  }

  private def splitStmts(): (Seq[Statement], Seq[Statement]) = genericStmts.foldLeft(
    Seq.empty[Statement],
    Seq.empty[Statement]
  ) {
    case ((stmts, txStmts), txStmt@Add("tx" | "txRef", _, _, _)) => (stmts, txStmts :+ txStmt)
    case ((stmts, txStmts), stmt)                                => (stmts :+ stmt, txStmts)
  }

  private def lastE(
    stmts: Seq[Statement],
    attr: String,
    forcedE: Any,
    bi: GenericValue,
    composite: Any = "e"
  ): Any = {
    bi match {
      case BiTargetRef(_, _) => {
        val lastEdgeNs = attr.split("/").head
        stmts.reverse.collectFirst {
          case Add(e: TempId, a, _, _) if a.startsWith(lastEdgeNs) => e
        } getOrElse err("lastE",
          s"Couldn't find namespace `$lastEdgeNs` in any previous Add statements:\n"
            + stmts.mkString("\n"))
      }
      case _                 => {
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
  }

  private def lastV(stmts: Seq[Statement], attr: String, forcedE: Any = 0L): Any = {
    if (forcedE != 0L)
      forcedE
    else if (stmts.isEmpty)
      tempId(attr)
    else
      stmts.last.v
  }

  private def eidV(stmts: Seq[Statement]): Boolean = stmts.nonEmpty && stmts.last.v.isInstanceOf[TempId]

  private def matchDataStmt(
    stmts: Seq[Statement],
    genericStmt: Statement,
    arg: Any,
    cur: Int,
    next: Int,
    forcedE: Any,
    edgeB: Option[AnyRef]
  ): Future[(Int, Option[AnyRef], Seq[Statement])] = genericStmt match {
    // Keep current cursor (add no new data in this iteration)
    case Add("__tempId", a, refNs: String, bi) if !refNs.startsWith("__") =>
      valueStmts(stmts, tempId(a), a, tempId(refNs), None, bi, edgeB).map((cur, edgeB, _))

    case Add(e@("e" | "ec"), a, refNs: String, bi@BiSelfRef(1)) if !refNs.startsWith("__") =>
      valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, tempId(refNs), None, bi, edgeB).map((cur, edgeB, _))

    case Add(e@("e" | "ec"), a, refNs: String, bi@BiSelfRef(2)) if !refNs.startsWith("__") =>
      valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, tempId(refNs), None, bi, edgeB).map((cur, edgeB, _))

    case Add(e@("e" | "ec"), a, refNs: String, bi@BiEdgeRef(_, _)) if !refNs.startsWith("__") =>
      val edgeB1 = Some(tempId(a))
      valueStmts(stmts, lastE(stmts, a, 0L, bi, e), a, tempId(refNs), None, bi, edgeB1).map((cur, edgeB1, _))

    case Add(e@("e" | "ec"), a, refNs: String, bi@BiTargetRef(_, _)) if !refNs.startsWith("__") =>
      valueStmts(stmts, lastE(stmts, a, 0L, bi, e), a, tempId(refNs), None, bi, edgeB).map((cur, None, _))

    case Add(e@("e" | "ec"), a, refNs: String, bi) if !refNs.startsWith("__") =>
      valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, tempId(refNs), None, bi, edgeB).map((cur, edgeB, _))

    case Add("v", a, refNs: String, bi) if !refNs.startsWith("__") =>
      valueStmts(stmts, stmts.last.v, a, tempId(refNs), None, bi, edgeB).map((cur, edgeB, _))

    case Add("v", a, "__tempId", bi) if forcedE != 0L =>
      valueStmts(stmts, forcedE, a, tempId(a), None, bi, edgeB).map((cur, edgeB, _))

    case Add("v", a, "__tempId", bi) =>
      valueStmts(stmts, stmts.last.v, a, tempId(a), None, bi, edgeB).map((cur, edgeB, _))

    case r: Retract =>
      Future((cur, edgeB, stmts))


    // Advance cursor for next value in data row
    case Add("__tempId", a, "__arg", bi@BiEdgePropAttr(_)) =>
      val edgeB1 = Some(tempId(a))
      valueStmts(stmts, tempId(a), a, arg, None, bi, edgeB1).map((next, edgeB1, _))

    case Add("__tempId", a, "__arg", bi@BiTargetRefAttr(_, _)) =>
      val edgeB1 = Some(tempId(a))
      valueStmts(stmts, tempId(a), a, arg, None, bi, edgeB1).map((next, edgeB1, _))

    case Add("__tempId", a, Values(EnumVal, pf), bi@BiEdgePropAttr(_)) =>
      val edgeB1 = Some(tempId(a))
      valueStmts(stmts, tempId(a), a, arg, pf, bi, edgeB1).map((next, edgeB1, _))

    case Add("__tempId", a, Values(EnumVal, pf), bi@BiTargetRefAttr(_, _)) =>
      val edgeB1 = Some(tempId(a))
      valueStmts(stmts, tempId(a), a, arg, pf, bi, edgeB1).map((next, edgeB1, _))

    case Add("__tempId", a, Values(vs, pf), bi@BiEdgePropAttr(_)) =>
      val edgeB1 = Some(tempId(a))
      valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB1).map((next, edgeB1, _))

    case Add("__tempId", a, Values(vs, pf), bi@BiTargetRefAttr(_, _)) =>
      val edgeB1 = Some(tempId(a))
      valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB1).map((next, edgeB1, _))

    case Add("__tempId", a, "__arg", bi) =>
      valueStmts(stmts, tempId(a), a, arg, None, bi, edgeB).map((next, edgeB, _))

    case Add("__tempId", a, Values(EnumVal, pf), bi) =>
      valueStmts(stmts, tempId(a), a, arg, pf, bi, edgeB).map((next, edgeB, _))

    case Add("__tempId", a, Values(vs, pf), bi) =>
      valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB).map((next, edgeB, _))

    case Add("remove_me", a, "__arg", bi) =>
      valueStmts(stmts, -1, a, arg, None, bi, edgeB).map((next, edgeB, _))

    case Add("__arg", a, "__tempId", bi) =>
      valueStmts(stmts, arg, a, tempId(a), None, bi, edgeB).map((next, edgeB, _))

    case Add(e@("e" | "ec"), a, "__arg", bi) =>
      valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, arg, None, bi, edgeB).map((next, edgeB, _))

    case Add(e@("e" | "ec"), a, Values(EnumVal, prefix), bi) =>
      valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, arg, prefix, bi, edgeB).map((next, edgeB, _))

    case Add(e@("e" | "ec"), a, Values(vs, prefix), bi) =>
      valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, vs, prefix, bi, edgeB).map((next, edgeB, _))

    case Add("v", a, "__arg", bi) if eidV(stmts) =>
      valueStmts(stmts, stmts.last.v, a, arg, None, bi, edgeB).map((next, edgeB, _))

    case Add("v", a, "__arg", bi) =>
      valueStmts(stmts, lastV(stmts, a, forcedE), a, arg, None, bi, edgeB).map((next, edgeB, _))

    case Add("v", a, Values(EnumVal, prefix), bi) =>
      valueStmts(stmts, lastV(stmts, a, forcedE), a, arg, prefix, bi, edgeB).map((next, edgeB, _))

    case Add("v", a, Values(vs, prefix), bi) =>
      valueStmts(stmts, lastV(stmts, a, forcedE), a, vs, prefix, bi, edgeB).map((next, edgeB, _))

    case Add("tx", a, "__arg", bi) =>
      valueStmts(stmts, tempId("tx"), a, arg, None, bi, edgeB).map((next, edgeB, _))

    case Add(_, _, nestedGenStmts0: Seq[_], _) if arg == Nil => Future((next, edgeB, stmts))


    // Nested data structures
    case Add(e, ref, nestedGenStmts0: Seq[_], bi) => {
      val parentE = if (e == "parentId")
        tempId(ref)
      else if (stmts.isEmpty)
        e
      else
        stmts.reverse.collectFirst {
          // Find entity value of Add statement with matching namespace
          case add: Add if eidV(stmts)                                  => stmts.last.v
          case Add(e1, a, _, _)
            if a.replaceFirst("/.*", "") == ref.replaceFirst("/.*", "") => e1
        }.getOrElse(
          err("", "[Model2Transaction:matchDataStmt] Couldn't find previous statement with matching namespace." +
            "\ne  : " + e +
            "\nref: " + ref)
        )

      val nestedGenStmts = nestedGenStmts0.map { case s: Statement => s }
      val nestedRows     = untupleNestedArgss(nestedGenStmts0, arg)

      val futNestedInsertStmts: Future[Seq[Statement]] = bi match {

        // Bidirectional self references ...........................................................

        // Nested self references
        // living_Person.name.Friends.*(living_Person.name) insert List(...
        case BiSelfRef(2) => {
          //          nestedRows.flatMap { nestedRow =>
          //            val forcedE     = tempId(ref)
          //            val bondStmts   = Seq(
          //              Add(forcedE, ref, parentE, Card(2)),
          //              Add(parentE, ref, forcedE, Card(2))
          //            )
          //            val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, forcedE)
          //            bondStmts ++ nestedStmts
          //          }

          //          Future.sequence(
          //            nestedRows.map { nestedRow =>
          //              val forcedE   = tempId(ref)
          //              val bondStmts = Seq(
          //                Add(forcedE, ref, parentE, Card(2)),
          //                Add(parentE, ref, forcedE, Card(2))
          //              )
          //              resolveStmts(nestedGenStmts, nestedRow, forcedE).map { nestedStmts =>
          //                bondStmts ++ nestedStmts
          //              }
          //            }
          //          ).map(_.flatten)

          // €€€
          nestedRows.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, nestedRow) =>
            val forcedE   = tempId(ref)
            val bondStmts = Seq(
              Add(forcedE, ref, parentE, Card(2)),
              Add(parentE, ref, forcedE, Card(2))
            )
            futStmts.flatMap { stmts =>
              resolveStmts(nestedGenStmts, nestedRow, forcedE).map { nestedStmts =>
                stmts ++ bondStmts ++ nestedStmts
              }
            }
          }
        }

        // Bidirectional other references ...........................................................

        // Nested other references
        // living_Person.name.Buddies.*(living_Animal.name) insert List(...
        case BiOtherRef(2, revRefAttr) => {
          //          nestedRows.flatMap { nestedRow =>
          //            val forcedE     = tempId(ref)
          //            val bondStmts   = Seq(
          //              Add(forcedE, revRefAttr, parentE, Card(2)),
          //              Add(parentE, ref, forcedE, Card(2))
          //            )
          //            val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, forcedE)
          //            bondStmts ++ nestedStmts
          //          }

          //          Future.sequence(
          //            nestedRows.map { nestedRow =>
          //              val forcedE   = tempId(ref)
          //              val bondStmts = Seq(
          //                Add(forcedE, revRefAttr, parentE, Card(2)),
          //                Add(parentE, ref, forcedE, Card(2))
          //              )
          //              resolveStmts(nestedGenStmts, nestedRow, forcedE).map { nestedStmts =>
          //                bondStmts ++ nestedStmts
          //              }
          //            }
          //          ).map(_.flatten)

          // €€€
          nestedRows.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, nestedRow) =>
            val forcedE   = tempId(ref)
            val bondStmts = Seq(
              Add(forcedE, revRefAttr, parentE, Card(2)),
              Add(parentE, ref, forcedE, Card(2))
            )
            futStmts.flatMap { stmts =>
              resolveStmts(nestedGenStmts, nestedRow, forcedE).map { nestedStmts =>
                stmts ++ bondStmts ++ nestedStmts
              }
            }
          }
        }


        // Bidirectional property edges ...........................................................

        // Nested property edges
        // living_Person.name.Knows.*(living_Knows.weight.Person.name) insert List(...
        case BiEdgeRef(2, revAttr) => {
          //          nestedRows.flatMap { nestedRow =>
          //            val edgeA       = tempId(revAttr)
          //            val edgeB1      = tempId(ref)
          //            val bondStmts   = Seq(
          //              Add(edgeA, ":molecule_Meta/otherEdge", edgeB1, Card(2)),
          //              Add(edgeB1, ":molecule_Meta/otherEdge", edgeA, Card(2)),
          //              Add(edgeB1, revAttr, parentE, Card(2)),
          //              Add(parentE, ref, edgeA, Card(2))
          //            )
          //            val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, edgeA, Some(edgeB1))
          //            bondStmts ++ nestedStmts
          //          }

          //          Future.sequence(
          //            nestedRows.map { nestedRow =>
          //              val edgeA     = tempId(revAttr)
          //              val edgeB1    = tempId(ref)
          //              val bondStmts = Seq(
          //                Add(edgeA, ":molecule_Meta/otherEdge", edgeB1, Card(2)),
          //                Add(edgeB1, ":molecule_Meta/otherEdge", edgeA, Card(2)),
          //                Add(edgeB1, revAttr, parentE, Card(2)),
          //                Add(parentE, ref, edgeA, Card(2))
          //              )
          //              resolveStmts(nestedGenStmts, nestedRow, edgeA, Some(edgeB1)).map { nestedStmts =>
          //                bondStmts ++ nestedStmts
          //              }
          //            }
          //          ).map(_.flatten)


          // €€€
          nestedRows.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, nestedRow) =>
            val edgeA     = tempId(revAttr)
            val edgeB1    = tempId(ref)
            val bondStmts = Seq(
              Add(edgeA, ":molecule_Meta/otherEdge", edgeB1, Card(2)),
              Add(edgeB1, ":molecule_Meta/otherEdge", edgeA, Card(2)),
              Add(edgeB1, revAttr, parentE, Card(2)),
              Add(parentE, ref, edgeA, Card(2))
            )
            futStmts.flatMap { stmts =>
              resolveStmts(nestedGenStmts, nestedRow, edgeA, Some(edgeB1)).map { nestedStmts =>
                stmts ++ bondStmts ++ nestedStmts
              }
            }
          }
        }

        // Nested edge property values
        // living_Person.name.Knows.weight.InCommon.*(living_Quality.name)._Knows.Person.name insert List(...
        case BiEdgePropRef(2) => {
          //          nestedRows.flatMap { nestedRow =>
          //            val edgeA       = tempId(ref)
          //            val bondStmts   = Seq(
          //              Add(edgeB.get, ref, edgeA, Card(2)),
          //              Add(parentE, ref, edgeA, Card(2))
          //            )
          //            val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, edgeA)
          //            bondStmts ++ nestedStmts
          //          }

          //          Future.sequence(
          //            nestedRows.map { nestedRow =>
          //              val edgeA     = tempId(ref)
          //              val bondStmts = Seq(
          //                Add(edgeB.get, ref, edgeA, Card(2)),
          //                Add(parentE, ref, edgeA, Card(2))
          //              )
          //              resolveStmts(nestedGenStmts, nestedRow, edgeA).map { nestedStmts =>
          //                bondStmts ++ nestedStmts
          //              }
          //            }
          //          ).map(_.flatten)


          // €€€
          nestedRows.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, nestedRow) =>
            val edgeA     = tempId(ref)
            val bondStmts = Seq(
              Add(edgeB.get, ref, edgeA, Card(2)),
              Add(parentE, ref, edgeA, Card(2))
            )
            futStmts.flatMap { stmts =>
              resolveStmts(nestedGenStmts, nestedRow, edgeA).map { nestedStmts =>
                stmts ++ bondStmts ++ nestedStmts
              }
            }
          }
        }


        // Uni-directional (normal) nested rows ...............................................

        // Invoice.id.InvoiceLines * InvoiceLine.no.item.price
        case _ => {
          //          nestedRows.flatMap { nestedRow =>
          //            val forcedE     = tempId(ref)
          //            val bondStmt    = Add(parentE, ref, forcedE, Card(2))
          //            val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, forcedE)
          //            bondStmt +: nestedStmts
          //          }

          //          Future.sequence(
          //            nestedRows.map { nestedRow =>
          //              val forcedE  = tempId(ref)
          //              val bondStmt = Add(parentE, ref, forcedE, Card(2))
          //              resolveStmts(nestedGenStmts, nestedRow, forcedE).map { nestedStmts =>
          //                bondStmt +: nestedStmts
          //              }
          //            }
          //          ).map(_.flatten)

          // €€€
          nestedRows.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, nestedRow) =>
            val forcedE  = tempId(ref)
            val bondStmt = Add(parentE, ref, forcedE, Card(2))
            futStmts.flatMap { stmts =>
              resolveStmts(nestedGenStmts, nestedRow, forcedE).map { nestedStmts =>
                (stmts :+ bondStmt) ++ nestedStmts
              }
            }
          }
        }
      }

      futNestedInsertStmts.map { nestedInsertStmts =>
        (next, edgeB, stmts ++ nestedInsertStmts)
      }
    }

    case unexpected => err("matchDataStmt",
      s"Unexpected insert statement: $unexpected\nGenericStmt: $genericStmt\nStmts:\n" + stmts.mkString("\n"))
  }


  private def untupleNestedArgss(stmts: Seq[Any], arg0: Any): Seq[Seq[Any]] = {
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


  private def resolveStmts(
    genericStmts0: Seq[Statement],
    row: Seq[Any],
    forcedE0: Any,
    edgeB0: Option[AnyRef] = None
  ): Future[Seq[Statement]] = {
    genericStmts0.foldLeft(
      Future(
        0,
        edgeB0,
        Seq.empty[Statement]
      )
    ) {
      case (futData, genericStmt) =>
        //      (for {
        //        (cur, edgeB, stmts0) <- data
        //      } yield {
        //        val arg0      = row(cur)
        //        val next: Int = if ((cur + 1) < row.size) cur + 1 else cur
        //
        //        val (stmts, forcedE) = if (stmts0.isEmpty)
        //          (stmts0, forcedE0)
        //        else stmts0.last match {
        //          case Add("nsFull", _, backRef, _) => (stmts0.init, backRef)
        //          case Add(_, _, _: TempId, _)      => (stmts0, 0)
        //          case Add(-1, _, _, _)             => (stmts0, 0)
        //          case _                            => (stmts0, stmts0.last.e)
        //        }
        //
        //        (arg0, genericStmt) match {
        //          case (null, _) =>
        //            // null values not allowed
        //            err("resolveStmts", "null values not allowed. Please use `attr$` for Option[tpe] values.")
        //
        //          case (_, Add("nsFull", nsFull, "", bi)) =>
        //            // Back reference - with mandatory previous ref
        //            val backRef = stmts.reverse.collectFirst {
        //              case Add(e: TempId, a, _, _) if a.startsWith(nsFull) => e
        //            } getOrElse
        //              err("resolveStmts",
        //                s"Couldn't find namespace `$nsFull` in any previous Add statements.\n" + stmts.mkString("\n"))
        //            Future((cur, edgeB, stmts :+ Add("nsFull", nsFull, backRef, bi)))
        //
        //          case (None, Add("e", a, refNs: String, bi@BiEdgeRef(_, _))) if !refNs.startsWith("__") =>
        //            val (edgeA, edgeB1) = (tempId(refNs), Some(tempId(a)))
        //            valueStmts(stmts, lastE(stmts, a, 0, bi), a, edgeA, None, bi, edgeB1).map((cur, edgeB1, _))
        //
        //          case (None, Add("e", a, refNs: String, bi)) if !refNs.startsWith("__") =>
        //            valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB).map((cur, edgeB, _))
        //
        //          case (None, _) =>
        //            Future((next, edgeB, stmts))
        //
        //          case (Some(arg), genericStmt) =>
        //            matchDataStmt(stmts, genericStmt, arg, cur, next, forcedE, edgeB)
        //
        //          case (arg, Add("e", a, "__arg", bi)) if eidV(stmts) =>
        //            valueStmts(stmts, stmts.last.v, a, arg, None, bi, edgeB).map((next, edgeB, _))
        //
        //          case (arg, genStmt@Add("e", _, refNs: String, _)) if !refNs.startsWith("__") && eidV(stmts) =>
        //            matchDataStmt(stmts, genStmt.copy(e = "v"), arg, cur, next, forcedE, edgeB)
        //
        //          case (arg, genStmt) =>
        //            matchDataStmt(stmts, genStmt, arg, cur, next, forcedE, edgeB)
        //        }
        //      }).flatten

        futData.flatMap { case (cur, edgeB, stmts0) =>
          val arg0      = row(cur)
          val next: Int = if ((cur + 1) < row.size) cur + 1 else cur

          val (stmts, forcedE) = if (stmts0.isEmpty)
            (stmts0, forcedE0)
          else stmts0.last match {
            case Add("nsFull", _, backRef, _) => (stmts0.init, backRef)
            case Add(_, _, _: TempId, _)      => (stmts0, 0L)
            case Add(-1, _, _, _)             => (stmts0, 0L)
            case _                            => (stmts0, stmts0.last.e)
          }

          (arg0, genericStmt) match {
            case (null, _) =>
              // null values not allowed
              err("resolveStmts", "null values not allowed. Please use `attr$` for Option[tpe] values.")

            case (_, Add("nsFull", nsFull, "", bi)) =>
              // Back reference - with mandatory previous ref
              val backRef = stmts.reverse.collectFirst {
                case Add(e: TempId, a, _, _) if a.startsWith(nsFull) => e
              } getOrElse
                err("resolveStmts",
                  s"Couldn't find namespace `$nsFull` in any previous Add statements.\n" + stmts.mkString("\n"))
              Future((cur, edgeB, stmts :+ Add("nsFull", nsFull, backRef, bi)))

            case (None, Add("e", a, refNs: String, bi@BiEdgeRef(_, _))) if !refNs.startsWith("__") =>
              val (edgeA, edgeB1) = (tempId(refNs), Some(tempId(a)))
              valueStmts(stmts, lastE(stmts, a, 0L, bi), a, edgeA, None, bi, edgeB1).map((cur, edgeB1, _))

            case (None, Add("e", a, refNs: String, bi)) if !refNs.startsWith("__") =>
              valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(refNs), None, bi, edgeB).map((cur, edgeB, _))

            case (None, _) =>
              Future((next, edgeB, stmts))

            case (Some(arg), genericStmt) =>
              matchDataStmt(stmts, genericStmt, arg, cur, next, forcedE, edgeB)

            case (arg, Add("e", a, "__arg", bi)) if eidV(stmts) =>
              valueStmts(stmts, stmts.last.v, a, arg, None, bi, edgeB).map((next, edgeB, _))

            case (arg, genStmt@Add("e", _, refNs: String, _)) if !refNs.startsWith("__") && eidV(stmts) =>
              matchDataStmt(stmts, genStmt.copy(e = "v"), arg, cur, next, forcedE, edgeB)

            case (arg, genStmt) =>
              matchDataStmt(stmts, genStmt, arg, cur, next, forcedE, edgeB)
          }
        }
    }.map {
      case (_, _, stmts1) =>
        if (stmts1.isEmpty) {
          Seq.empty[Statement]
        } else if (stmts1.last.v.isInstanceOf[TempId]) {
          stmts1.init
        } else {
          stmts1
        }.filterNot(_.e == -1)
    }
  }

  private def txRefAttr(stmts: Seq[Statement]): Boolean = stmts.nonEmpty && stmts.last.v.isInstanceOf[TempId]


  def insertStmts(dataRows: Iterable[Seq[Any]]): Future[Seq[Statement]] = {
    val (genericStmts, genericTxStmts) = splitStmts()

    //    val futDataStmts0: Future[Seq[Statement]] = Future.sequence(
    //      dataRows.toSeq.map(resolveStmts(genericStmts, _, 0))
    //    ).map(_.flatten)

    val futDataStmts: Future[Seq[Statement]] = {
      dataRows.foldLeft(Future(Seq.empty[Statement])) { case (futStmts, dataRow) =>
        futStmts.flatMap(stmts =>
          resolveStmts(genericStmts, dataRow, 0L).map { newStmts =>
            stmts ++ newStmts
          }
        )
      }
    }

    val futTxStmts = genericTxStmts.foldLeft(
      Future(Seq.empty[Statement])
    ) {
      case (futStmts, genericTxStmt) => {
        genericTxStmt match {
          case Add("tx", a, Values(vs, prefix), bi) =>
            futStmts.flatMap {
              case stmts if txRefAttr(stmts) =>
                valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, None)
              case stmts                     =>
                valueStmts(stmts, tempId("tx"), a, vs, prefix, bi, None)
            }

          case Add("tx", a, refNs: String, bi) if !refNs.startsWith("__") =>
            futStmts.flatMap(stmts => valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(refNs), None, bi, None))

          case Add("txRef", a, Values(vs, prefix), bi) =>
            futStmts.flatMap {
              case stmts if txRefAttr(stmts) =>
                valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, None)
              case stmts                     =>
                valueStmts(stmts, stmts.last.e.asInstanceOf[Object], a, vs, prefix, bi, None)
            }

          case Add("txRef", a, refNs: String, bi) if !refNs.startsWith("__") =>
            futStmts.flatMap(stmts => valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(refNs), None, bi, None))

          case unexpected =>
            err("insertStmts", "Unexpected insert statement: " + unexpected)
        }
      }
    }

    for {
      dataStmts <- futDataStmts
      txStmts <- futTxStmts
    } yield {
      dataStmts ++ txStmts
    }
  }


  def saveStmts: Future[Seq[Statement]] = genericStmts.foldLeft(
    "",
    Option.empty[AnyRef],
    Future(Seq.empty[Statement])
  ) {
    case ((backRef, edgeB, futStmts), genericStmt) => genericStmt match {

      case _: Retract => (backRef, edgeB, futStmts)

      case Add("__tempId", a, Values(vs, pf), bi) => bi match {
        case _: BiEdgePropAttr =>
          val edgeB1 = Some(tempId(a))
          (backRef, edgeB1, futStmts.flatMap(valueStmts(_, tempId(a), a, vs, pf, bi, edgeB1)))
        case _                 =>
          (backRef, edgeB, futStmts.flatMap(valueStmts(_, tempId(a), a, vs, pf, bi, edgeB)))
      }

      case Add(e, a, "__tempId", bi) => (backRef, edgeB, e match {
        case "e" => futStmts.flatMap(stmts => valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(a), None, bi, edgeB))
        case _   => futStmts.flatMap(valueStmts(_, e, a, tempId(a), None, bi, edgeB))
      })

      case Add(e@("e" | "ec"), a, Values(vs, prefix), bi) =>
        (backRef, edgeB, futStmts.flatMap(stmts => valueStmts(stmts, lastE(stmts, a, 0L, bi, e), a, vs, prefix, bi, edgeB)))

      case Add(e@("e" | "ec"), a, refNs: String, bi) if !refNs.startsWith("__") => bi match {
        case _: BiEdgeRef =>
          val edgeB1 = Some(tempId(a))
          (backRef, edgeB1, futStmts.flatMap(stmts => valueStmts(stmts, lastE(stmts, a, 0L, bi, e), a, tempId(refNs), None, bi, edgeB1)))

        case _: BiTargetRef =>
          (backRef, None, futStmts.flatMap(stmts => valueStmts(stmts, lastE(stmts, a, 0L, bi, e), a, tempId(refNs), None, bi, edgeB)))

        case _ =>
          val futStmts2 = futStmts.flatMap { stmts =>
            if (backRef.isEmpty) {
              valueStmts(stmts, lastE(stmts, a, 0L, bi, e), a, tempId(refNs), None, bi, edgeB)
            } else {
              val forcedE = stmts.reverse.collectFirst {
                case Add(e: TempId, a, _, _) if a.startsWith(backRef) => e
              } getOrElse err("saveStmts",
                s"Couldn't find backref namespace `$backRef` in any previous Add statements.\n" + stmts.mkString("\n"))
              valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, tempId(refNs), None, bi, edgeB)
            }
          }
          ("", edgeB, futStmts2)
      }

      case Add("v", a, Values(vs, prefix), bi) =>
        (backRef, edgeB, futStmts.flatMap(stmts => valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB)))

      case Add("tx", a, Values(vs, prefix), bi) =>
        val futStmts2 = futStmts.flatMap { stmts =>
          if (txRefAttr(stmts)) {
            valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB)
          } else {
            valueStmts(stmts, datomicTx, a, vs, prefix, bi, edgeB)
          }
        }
        (backRef, edgeB, futStmts2)

      case Add("tx", a, refNs: String, bi) if !refNs.startsWith("__") =>
        (backRef, edgeB, futStmts.flatMap(stmts => valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(refNs), None, bi, edgeB)))

      case Add("txRef", a, Values(vs, prefix), bi) =>
        val futStmts2 = futStmts.flatMap(stmts =>
          if (txRefAttr(stmts))
            valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB)
          else
            valueStmts(stmts, stmts.last.e.asInstanceOf[Object], a, vs, prefix, bi, edgeB)
        )
        (backRef, edgeB, futStmts2)

      case Add("txRef", a, refNs: String, bi) if !refNs.startsWith("__") =>
        (backRef, edgeB, futStmts.flatMap(stmts => valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(refNs), None, bi, edgeB)))

      case Add("nsFull", backRef, _, _) => (backRef, edgeB, futStmts)

      case Add(id: Long, _, Values(_, _), _) => err("saveStmts", s"With a given id `$id` please use `update` instead.")
      case Add(_, a, "__arg", _)             => err("saveStmts", s"Attribute `$a` needs a value applied")
      case unexpected                        => err("saveStmts", s"Unexpected save statement: $unexpected")
    }
  }._3


  def updateStmts: Future[Seq[Statement]] = {
    val (genericStmts, genericTxStmts)     = splitStmts()
    val futDataStmts                       = genericStmts.foldLeft(
      Future(
        0L,
        Option.empty[AnyRef],
        Seq.empty[Statement]
      )
    ) {
      case (futData, genericStmt) => {
        futData.flatMap { case (prevE, edgeB, stmts) =>
          genericStmt match {
            case Add("e", a, "__tempId", bi) =>
              valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(a), None, bi, edgeB).map((0L, edgeB, _))

            case Add(e, a, "__tempId", bi@BiEdgeRef(_, _)) =>
              val edgeB1 = Some(tempId(a))
              valueStmts(stmts, e, a, tempId(a), None, bi, edgeB1).map((0L, edgeB1, _))

            case Add(e, a, "__tempId", bi) =>
              valueStmts(stmts, e, a, tempId(a), None, bi, edgeB).map((0L, edgeB, _))

            case Add("e", a, Values(vs, prefix), bi) if prevE == 0L =>
              valueStmts(stmts, lastE(stmts, a, 0L, bi), a, vs, prefix, bi, edgeB).map((0L, edgeB, _))

            case Add("e", a, Values(vs, prefix), bi) =>
              valueStmts(stmts, prevE, a, vs, prefix, bi, edgeB).map {
                case Nil      => (prevE, edgeB, Nil)
                case addStmts => (0L, edgeB, addStmts)
              }

            case Add("e", a, refNs: String, bi@BiTargetRef(_, _)) if !refNs.startsWith("__") =>
              valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(refNs), None, bi, edgeB).map((0L, None, _))

            case Add("v" | "tx", a, Values(vs, prefix), bi) =>
              valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB).map((0L, edgeB, _))

            case Add(e, a, Values(vs, prefix), bi) =>
              valueStmts(stmts, e, a, vs, prefix, bi, edgeB).map {
                case Nil    => (e.asInstanceOf[Long], edgeB, Nil) // pass eid so that we have it for subsequent stmts
                case stmts2 => (0L, edgeB, stmts2)
              }

            case Retract("e", a, Values(vs, prefix), bi) =>
              valueStmts(stmts, lastE(stmts, a, 0L, bi), a, vs, prefix, bi, edgeB).map((0L, edgeB, _))

            case Retract(e, a, Values(vs, prefix), bi) =>
              valueStmts(stmts, e, a, vs, prefix, bi, edgeB).map((0L, edgeB, _))

            case Add(_, a, "__arg", _) => err("updateStmts", s"Attribute `$a` needs a value applied")
            case unexpected            => err("updateStmts", "Unexpected update statement: " + unexpected)
          }
        }
      }
    }
    val futTxStmts: Future[Seq[Statement]] = genericTxStmts.foldLeft(
      Future(Seq.empty[Statement])
    ) { case (futStmts, genericTxStmt) =>
      genericTxStmt match {
        case Add("tx", a, Values(vs, prefix), bi) => futStmts.flatMap(valueStmts(_, datomicTx, a, vs, prefix, bi, None))
        case unexpected                           => err("updateStmts", "Unexpected insert statement: " + unexpected)
      }
    }

    for {
      (_, _, dataStmts) <- futDataStmts
      txStmts <- futTxStmts
    } yield {
      dataStmts ++ txStmts
    }
  }
}
