package molecule.core.transform

import java.util.Date
import molecule.core.ast.elements._
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.Conn
import molecule.datomic.base.transform.exception.Model2TransactionException
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
//import scala.concurrent.Await
//import scala.concurrent.duration.DurationInt


/** Model to Statements transformer.
  *
  * */
case class ModelTransformer(conn: Conn, model: Model) extends GenericStmts(conn, model) {

  private def getPairs(e: Any, a: String, key: String = ""): Map[String, String] = {
    val strs = if (key.isEmpty) {
      val query = "[:find ?v :in $ ?e ?a :where [?e ?a ?v]]"
      conn.q(query, e.asInstanceOf[Object], a).map(_.head)
    } else {
      val query = "[:find ?v :in $ ?e ?a ?key :where [?e ?a ?v][(.startsWith ^String ?v ?key)]]"
      conn.q(query, e.asInstanceOf[Object], a, key.asInstanceOf[Object]).map(_.head)
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
    bidirectional: GenericValue,
    otherEdgeId: Option[AnyRef]
  ): Seq[Statement] = {

    def p(v: Any): Any = v match {
//      case f: Float              => f.toString.toDouble
      case _ if prefix.isDefined => Enum(prefix.get, v.toString)
      case bd: BigDecimal        => bd + 0.0 // ensure decimal digits
      case _                     => v
    }

    def d(v: Any) = v match {
      case d: Date => date2str(d)
      case other   => other
    }

    def attrValues(id: Any, attr: String): List[AnyRef] = {
      if (id.isInstanceOf[TempId]) {
        Nil
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
        conn.q(query).map(_.head) // could be empty
      }
    }

    def edgeAB(edge1: Any, targetAttr: String): (Any, Any) = {
      val edge2 = otherEdge(edge1)

      // Edges to other ns already has one ref extra in one direction
      val s1 = conn.entity(edge1).keys.size
      val s2 = conn.entity(edge2).keys.size

      if (s1 > s2)
        (edge1, edge2)
      else if (s1 < s2)
        (edge2, edge1)
      else if (conn.entity(edge1).rawValue(targetAttr) == null)
        (edge2, edge1)
      else
        (edge1, edge2)
    }

    def otherEdge(edgeA: Any): AnyRef = {
      val query  = s"[:find ?edgeB :in $$ ?edgeA :where [?edgeA :molecule_Meta/otherEdge ?edgeB]]"
      val result = conn.q(query, edgeA.asInstanceOf[Object]).map(_.head)
      result match {
        case List(edgeB) => edgeB.asInstanceOf[Object]
        case Nil         =>
          val otherId = conn.entity(edgeA)
          err("valueStmts:biEdgeRef",
            s"Supplied id $edgeA doesn't appear to be a property edge id (couldn't find reverse edge id). " +
              s"Could it be another entity?:\n" + otherId.touchQuotedMax(2) +
              s"\nSpooky id: $otherId" +
              "\n" + stmts.size + " statements so far:\n" + stmts.mkString("\n")
          )
        case ids         =>
          err("valueStmts:biEdgeRef",
            "Unexpectedly found multiple reverse edge ids:\n" + ids.mkString("\n"))
      }
    }

    def checkDupKeys(pairs: Seq[(String, Any)], host: String, op: String): Unit = {
      val dupKeys: Seq[String] = pairs.map(_._1).groupBy(identity).collect {
        case (key, keys) if keys.size > 1 => key
      }.toSeq
      if (dupKeys.nonEmpty) {
        val dupPairs = pairs.filter(p => dupKeys.contains(p._1)).sortBy(_._1).map { case (k, v) => s"$k -> $v" }
        err(s"valueStmts:$host",
          s"Can't $op multiple key/value pairs with the same key for attribute `$a`:\n"
            + dupPairs.mkString("\n"))
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
          s"Can't replace non-existing keys of map attribute `$a`:\n"
            + unknownKeys.mkString("\n")
            + "\nYou might want to apply the values instead to replace all current values?")
    }


    // Bidirectional self-ref operations ------------------------------------------------------------------

    def biSelf(card: Int): Iterable[Statement] = arg match {

      case AssertValue(refs) => refs.flatMap { case ref: Long =>
        val reverseRetracts = if (card == 1) attrValues(e, a).map(revRef => Retract(revRef, a, e)) else Nil
        if (ref == e)
          err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same")
        reverseRetracts ++ Seq(
          Add(ref, a, e, Card(card)),
          Add(e, a, ref, Card(card))
        )
      }

      case ReplaceValue(oldNew) => attrValues(e, a).flatMap { case revRef =>
        oldNew.flatMap {
          case (oldRef, newRef) if oldRef == revRef =>
            Seq(
              // This entity e now has ref to newRef instead of oldRef
              Retract(e, a, oldRef), Add(e, a, newRef, Card(card)),
              // RevRef no longer has a ref to this entity e
              // Instead newRef has a ref to this entity e
              Retract(revRef, a, e), Add(newRef, a, e, Card(card))
            )
          case _                                    => Nil
        }
      }

      case RetractValue(removeRefs) => removeRefs.flatMap { case ref: Long => Seq(Retract(ref, a, e), Retract(e, a, ref)) }

      case Eq(newRefs) => {
        if (newRefs.contains(e))
          err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")
        val oldRefs  = attrValues(e, a)
        val retracts = oldRefs.flatMap {
          case oldRef if newRefs.contains(oldRef) => Nil
          case obsoleteRef                        =>
            Seq(
              Retract(obsoleteRef, a, e),
              Retract(e, a, obsoleteRef)
            )
        }
        val adds     = newRefs.flatMap {
          case newRef if oldRefs.contains(newRef) => Nil
          case newRef                             =>
            Seq(
              Add(newRef, a, e, Card(card)),
              Add(e, a, newRef, Card(card))
            )
        }
        retracts ++ adds
      }

      case refs: Set[_] => refs.flatMap { case ref: Long => Seq(Add(ref, a, e, Card(card)), Add(e, a, ref, Card(card))) }
      case ref          => {
        val reverseRetracts = if (card == 1) attrValues(e, a).map(revRef => Retract(revRef, a, e)) else Nil
        reverseRetracts ++ Seq(
          Add(ref, a, e, Card(card)),
          Add(e, a, ref, Card(card))
        )
      }
    }


    // Bidirectional other-ref operations ------------------------------------------------------------------

    def biOther(card: Int, revRefAttr: String): Iterable[Statement] = arg match {

      case AssertValue(refs) => refs.flatMap { case ref: Long =>
        if (ref == e)
          err("valueStmts:biOther", "Current entity and referenced entity ids can't be the same")
        val reverseRetracts = if (card == 1) attrValues(e, a).map(revRef => Retract(revRef, a, e)) else Nil
        reverseRetracts ++ Seq(
          Add(ref, revRefAttr, e, Card(card)),
          Add(e, a, ref, Card(card))
        )
      }

      case ReplaceValue(oldNew) => attrValues(e, a).flatMap { case revRef =>
        oldNew.flatMap {
          case (oldRef, newRef) if oldRef == revRef =>
            Seq(
              // This entity e now has ref to newRef instead of oldRef
              Retract(e, a, oldRef), Add(e, a, newRef, Card(card)),
              // RevRef no longer has a ref to this entity e
              // Instead newRef has a ref to this entity e
              Retract(revRef, revRefAttr, e, Card(card)), Add(newRef, revRefAttr, e, Card(card))
            )
          case _                                    => Nil
        }
      }

      case RetractValue(removeRefs) => removeRefs.flatMap {
        case ref: Long =>
          Seq(
            Retract(ref, revRefAttr, e),
            Retract(e, a, ref)
          )
      }

      case Eq(newRefs) => {
        if (newRefs.contains(e))
          err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")
        val oldRefs  = attrValues(e, a)
        val retracts = oldRefs.flatMap {
          case oldRef if newRefs.contains(oldRef) => Nil
          case obsoleteRef                        =>
            Seq(
              Retract(obsoleteRef, revRefAttr, e),
              Retract(e, a, obsoleteRef)
            )
        }
        val adds     = newRefs.flatMap {
          case newRef if oldRefs.contains(newRef) => Nil
          case newRef                             =>
            Seq(
              Add(newRef, revRefAttr, e, Card(card)),
              Add(e, a, newRef, Card(card))
            )
        }
        retracts ++ adds
      }

      case refs: Set[_] => refs.flatMap {
        case ref: Long =>
          Seq(
            Add(ref, revRefAttr, e, Card(card)),
            Add(e, a, ref, Card(card))
          )
      }

      case ref => {
        val reverseRetracts = if (card == 1) attrValues(e, a).map(revRef => Retract(revRef, revRefAttr, e)) else Nil
        reverseRetracts ++ Seq(
          Add(ref, revRefAttr, e, Card(card)),
          Add(e, a, ref, Card(card))
        )
      }
    }


    // Bidirectional edge operations -------------------------------------------------------------------

    def biEdgeRefAttr(card: Int, targetAttr: String): Iterable[Statement] = arg match {

      case AssertValue(edges) =>
        if (edges.contains(e))
          err("valueStmts:biEdgeRefAttr", "Current entity and referenced entity ids can't be the same.")
        checkDupValues(edges, "biEdgeRefAttr", "assert")
        edges.flatMap { edge =>
          val (edgeA, edgeB) = edgeAB(edge, targetAttr)
          Seq(
            Add(edgeB, targetAttr, e, Card(card)),
            Add(e, a, edgeA, Card(card))
          )
        }

      case ReplaceValue(oldNewEdges) =>
        checkDupValuesOfPairs(oldNewEdges, "biEdgeRefAttr", "replace")
        oldNewEdges.flatMap { case (oldEdge, newEdge) =>
          val (edgeA, edgeB) = edgeAB(newEdge, targetAttr)
          Seq(
            RetractEntity(oldEdge),
            Add(edgeB, targetAttr, e, Card(card)),
            Add(e, a, edgeA, Card(card))
          )
        }

      case RetractValue(edges) => edges map RetractEntity

      case Eq(newEdges) =>
        if (newEdges.contains(e))
          err("valueStmts:biEdgeRefAttr", "Current entity and referenced entity ids can't be the same.")
        checkDupValues(newEdges, "biEdgeRefAttr", "apply")
        val oldEdges = attrValues(e, a)
        val retracts = oldEdges.flatMap {
          case oldEdgeA if newEdges.contains(oldEdgeA) => Nil
          case oldEdgeA                                => Seq(RetractEntity(oldEdgeA))
        }
        val adds     = newEdges.flatMap {
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
        Seq(
          Add(edgeB, targetAttr, e, Card(card)),
          Add(e, a, edgeA, Card(card))
        )
      }

      case edge: Long =>
        val (edgeA, edgeB)  = edgeAB(edge, targetAttr)
        val reverseRetracts = if (card == 1) attrValues(e, a).map(oldEdgeA => RetractEntity(oldEdgeA)) else Nil
        reverseRetracts ++ Seq(
          Add(edgeB, targetAttr, e, Card(card)),
          Add(e, a, edgeA, Card(card))
        )
    }


    def biEdgeRef(card: Int, targetAttr: String): Seq[Statement] = arg match {

      case RetractValue(edges) => edges map RetractEntity

      case Eq(newEdges) =>
        if (newEdges.contains(e))
          err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")
        checkDupValues(newEdges, "biEdgeRef", "apply")
        val oldEdges = attrValues(e, a)
        val retracts = oldEdges.flatMap {
          case oldEdgeA if newEdges.contains(oldEdgeA) => Nil
          case oldEdgeA                                => Seq(RetractEntity(oldEdgeA))
        }
        val adds     = newEdges.flatMap {
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
        val edgeB           = otherEdgeId getOrElse err("valueStmts:biEdgeRef", "Missing id of other edge.")
        val reverseRetracts = if (card == 1) attrValues(e, a).map(oldEdgeA => RetractEntity(oldEdgeA)) else Nil
        reverseRetracts ++ Seq(
          // Interlink edge entities so that we later know which other one to update
          Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
          Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card)),
          Add(edgeB, targetAttr, e, Card(card)),
          Add(e, a, edgeA, Card(card))
        )
      }
    }


    def biEdgeProp(card: Int): Seq[Statement] = {
      val edgeA = e
      val edgeB = otherEdgeId match {
        case Some(eid) if eid == edgeA =>
          err("valueStmts:biEdgeProp", "Other edge id is unexpectedly the same as this edge id.")
        case Some(eid)                 => eid
        case None                      =>
          otherEdge(edgeA)
      }

      val edgeStmts = arg match {

        case AssertMapPairs(newPairs) =>
          checkDupKeys(newPairs, "biEdgeProp", "assert")
          val curPairs     = getPairs(edgeA, a)
          val curPairsList = curPairs.toList
          val curKeys      = curPairs.keys.toList
          newPairs.flatMap {
            case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
            case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
            case (k, v) if curKeys.contains(k)                       =>
              Seq(
                Retract(edgeB, a, k + "@" + curPairs(k)), Add(edgeB, a, k + "@" + d(v), Card(card)),
                Retract(edgeA, a, k + "@" + curPairs(k)), Add(edgeA, a, k + "@" + d(v), Card(card))
              )
            case (k, v)                                              =>
              Seq(
                Add(edgeB, a, k + "@" + d(v), Card(card)),
                Add(edgeA, a, k + "@" + d(v), Card(card))
              )
          }

        case ReplaceMapPairs(newPairs) =>
          checkDupKeys(newPairs, "biEdgeProp", "replace")
          val curPairs     = getPairs(edgeA, a)
          val curPairsList = curPairs.toList
          val curKeys      = curPairs.keys.toList
          checkUnknownKeys(newPairs, curKeys.toSeq, "biEdgeProp")
          newPairs.flatMap {
            case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
            case (k, v) if curKeys.contains(k)                       =>
              Seq(
                Retract(edgeB, a, k + "@" + curPairs(k)), Add(edgeB, a, k + "@" + d(v), Card(card)),
                Retract(edgeA, a, k + "@" + curPairs(k)), Add(edgeA, a, k + "@" + d(v), Card(card))
              )
            case (k, v)                                              =>
              Seq(
                Add(edgeB, a, k + "@" + d(v), Card(card)),
                Add(edgeA, a, k + "@" + d(v), Card(card))
              )
          }

        case RetractMapKeys(removeKeys0) =>
          val removeKeys = removeKeys0.distinct
          val oldPairs   = getPairs(edgeA, a)
          oldPairs.flatMap {
            case (oldK, oldV) if removeKeys.contains(oldK) =>
              Seq(
                Retract(edgeB, a, s"$oldK@$oldV"),
                Retract(edgeA, a, s"$oldK@$oldV")
              )
            case (oldK, oldV)                              => Nil
          }

        case MapEq(newPairs) =>
          checkDupKeys(newPairs, "biEdgeProp", "apply")
          edgeA match {
            case updateE: Long => {
              val oldPairs  = getPairs(edgeA, a)
              val newPairs1 = newPairs.map { case (k, v) => (k, d(v).toString) }
              val retracts  = oldPairs.flatMap {
                case (oldK, oldV) if newPairs1.contains((oldK, oldV)) => Nil
                case (oldK, oldV)                                     =>
                  Seq(
                    Retract(edgeB, a, s"$oldK@$oldV"),
                    Retract(edgeA, a, s"$oldK@$oldV")
                  )
              }
              val adds      = newPairs.flatMap { case (k, v) =>
                Seq(
                  Add(edgeB, a, k + "@" + d(v), Card(card)),
                  Add(edgeA, a, k + "@" + d(v), Card(card))
                )
              }
              retracts ++ adds
            }
            case newE          =>
              newPairs.flatMap { case (k, v) =>
                Seq(
                  Add(edgeB, a, k + "@" + d(v), Card(card)),
                  Add(edgeA, a, k + "@" + d(v), Card(card))
                )
              }
          }

        case AssertValue(values) =>
          values.distinct.flatMap(v =>
            Seq(
              Add(edgeB, a, p(v), Card(card)),
              Add(edgeA, a, p(v), Card(card))
            )
          )

        case ReplaceValue(oldNew) =>
          checkDupValuesOfPairs(oldNew, "biEdgeProp", "replace")
          oldNew.flatMap { case (oldValue, newValue) =>
            Seq(
              Retract(edgeB, a, p(oldValue)), Add(edgeB, a, p(newValue), Card(card)),
              Retract(edgeA, a, p(oldValue)), Add(edgeA, a, p(newValue), Card(card)))
          }

        case RetractValue(removeValues) =>
          removeValues.distinct.flatMap(v =>
            Seq(
              Retract(edgeB, a, p(v)),
              Retract(edgeA, a, p(v))
            )
          )

        case Eq(newValues) =>
          if (newValues.contains(e))
            err("valueStmts:biSelfRef", "Current entity and referenced entity ids can't be the same.")
          val curValues       = attrValues(edgeA, a)
          val newValueStrings = newValues.map(_.toString)
          val curValueStrings = curValues.map(_.toString)
          val retracts        = curValues.flatMap {
            case curValue if newValueStrings.contains(curValue.toString) => Nil
            case obsoleteValue                                           =>
              Seq(
                Retract(edgeB, a, p(obsoleteValue)),
                Retract(edgeA, a, p(obsoleteValue))
              )
          }
          val adds            = newValues.flatMap {
            case newValue if curValueStrings.contains(newValue.toString) => Nil
            case newValue                                                =>
              Seq(
                Add(edgeB, a, p(newValue), Card(card)),
                Add(edgeA, a, p(newValue), Card(card))
              )
          }
          retracts ++ adds

        case m: Map[_, _] =>
          m.flatMap { case (k, v) =>
            Seq(
              Add(edgeB, a, s"$k@${d(v)}", Card(card)),
              Add(edgeA, a, s"$k@${d(v)}", Card(card))
            )
          }

        case vs: Set[_] =>
          vs.toSeq.flatMap(v =>
            Seq(
              Add(edgeB, a, p(v), Card(card)),
              Add(edgeA, a, p(v), Card(card))
            )
          )

        case v :: Nil =>
          Seq(
            Add(edgeB, a, p(v), Card(card)),
            Add(edgeA, a, p(v), Card(card))
          )

        case vs: List[_] =>
          vs.flatMap(v =>
            Seq(
              Add(edgeB, a, p(v), Card(card)),
              Add(edgeA, a, p(v), Card(card))
            )
          )

        case v =>
          Seq(
            Add(edgeB, a, p(v), Card(card)),
            Add(edgeA, a, p(v), Card(card))
          )
      }

      val edgeConnectors = stmts.collectFirst {
        case Add(_, ":molecule_Meta/otherEdge", _, _) => Nil
      } getOrElse
        Seq(
          Add(edgeA, ":molecule_Meta/otherEdge", edgeB, Card(card)),
          Add(edgeB, ":molecule_Meta/otherEdge", edgeA, Card(card))
        )

      edgeConnectors ++ edgeStmts
    }

    def biTarget(card: Int, biEdgeRefAttr: String): Iterable[Statement] = {
      val edgeA = e
      val edgeB = otherEdgeId getOrElse err("valueStmts:biTargetRef", "Missing id of other edge.")
      arg match {
        case Eq(biTargetRef :: Nil) =>
          Seq(
            Add(biTargetRef, biEdgeRefAttr, edgeB, Card(card)),
            Add(edgeA, a, biTargetRef, Card(card))
          )
        case biTargetRef            =>
          Seq(
            Add(biTargetRef, biEdgeRefAttr, edgeB, Card(card)),
            Add(edgeA, a, biTargetRef, Card(card))
          )
      }
    }

    // Default operations -------------------------------------------------------------------

    def default(card: Int): Iterable[Statement] = arg match {

      case AssertMapPairs(newPairs) =>
        checkDupKeys(newPairs, "default", "assert")
        val curPairs     = getPairs(e, a)
        val curPairsList = curPairs.toList
        val curKeys      = curPairs.keys.toList
        newPairs.flatMap {
          case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
          case (k, v) if curKeys.contains(k)                       =>
            Seq(
              Retract(e, a, s"$k@${curPairs(k)}"),
              Add(e, a, s"$k@${d(v)}", Card(card))
            )
          case (k, v)                                              =>
            Seq(Add(e, a, s"$k@${d(v)}", Card(card)))
        }

      case ReplaceMapPairs(newPairs) =>
        checkDupKeys(newPairs, "default", "replace")
        val curPairs     = getPairs(e, a)
        val curPairsList = curPairs.toList
        val curKeys      = curPairs.keys.toList
        checkUnknownKeys(newPairs, curKeys, "default")
        newPairs.flatMap {
          case (k, v) if curPairsList.contains((k, d(v).toString)) => Nil
          case (k, v) if curKeys.contains(k)                       =>
            Seq(
              Retract(e, a, s"$k@${curPairs(k)}"),
              Add(e, a, s"$k@${d(v)}", Card(card))
            )
          case (k, v)                                              =>
            Seq(Add(e, a, s"$k@${d(v)}", Card(card)))
        }

      case RetractMapKeys(removeKeys0) =>
        val removeKeys = removeKeys0.distinct
        val oldPairs   = getPairs(e, a)
        oldPairs.flatMap {
          case (oldK, oldV) if removeKeys.contains(oldK) => Seq(Retract(e, a, s"$oldK@$oldV"))
          case (oldK, oldV)                              => Nil
        }

      case MapEq(newPairs) =>
        checkDupKeys(newPairs, "default", "apply")
        e match {
          case updateE: Long => {
            val oldPairs  = getPairs(e, a)
            val newPairs1 = newPairs.map { case (k, v) => (k, d(v).toString) }
            val retracts  = oldPairs.flatMap {
              case (oldK, oldV) if newPairs1.contains((oldK, oldV)) => Nil
              case (oldK, oldV)                                     => Seq(Retract(e, a, s"$oldK@$oldV"))
            }
            val adds      = newPairs.map { case (k, v) => Add(e, a, k + "@" + d(v), Card(card)) }
            retracts ++ adds
          }
          case newE          => newPairs.map { case (k, v) => Add(e, a, k + "@" + d(v), Card(card)) }
        }

      case AssertValue(values0) => flatten(values0).distinct.map(v => Add(e, a, p(v), Card(card)))

      case ReplaceValue(oldNew) =>
        checkDupValuesOfPairs(oldNew, "default", "replace")
        oldNew.flatMap { case (oldValue, newValue) =>
          Seq(
            Retract(e, a, p(oldValue)),
            Add(e, a, p(newValue), Card(card))
          )
        }

      case RetractValue(removeValues0) => flatten(removeValues0).distinct.map(v => Retract(e, a, p(v)))

      case Eq(newValues0) =>
        val newValues       = flatten(newValues0).distinct
        val curValues       = if (e == datomicTx) Nil else attrValues(e, a)
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

      case m: Map[_, _] => m.map { case (k, v) => Add(e, a, s"$k@${d(v)}", Card(card)) }
      case vs: Set[_]   => vs.map(v => Add(e, a, p(v), Card(card)))
      case v :: Nil     => Seq(Add(e, a, p(v), Card(card)))
      case vs: List[_]  => vs.map(v => Add(e, a, p(v), Card(card)))
      case v            => Seq(Add(e, a, p(v), Card(card)))
    }

    val newStmts = bidirectional match {
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

    stmts ++ newStmts
  }


  private def matchDataStmt(
    stmts: Seq[Statement],
    genericStmt: Statement,
    arg: Any,
    cur: Int,
    next: Int,
    forcedE: Any,
    edgeB: Option[AnyRef]
  ): (Int, Option[AnyRef], Seq[Statement]) = genericStmt match {
    // Keep current cursor (add no new data in this iteration)
    case Add("__tempId", a, refNs: String, bi) if !refNs.startsWith("__") =>
      (cur, edgeB, valueStmts(stmts, tempId(a), a, tempId(refNs), None, bi, edgeB))

    case Add(e@("e" | "ec"), a, refNs: String, bi@BiSelfRef(1)) if !refNs.startsWith("__") =>
      (cur, edgeB, valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, tempId(refNs), None, bi, edgeB))

    case Add(e@("e" | "ec"), a, refNs: String, bi@BiSelfRef(2)) if !refNs.startsWith("__") =>
      (cur, edgeB, valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, tempId(refNs), None, bi, edgeB))

    case Add(e@("e" | "ec"), a, refNs: String, bi@BiEdgeRef(_, _)) if !refNs.startsWith("__") =>
      val edgeB1 = Some(tempId(a))
      (cur, edgeB1, valueStmts(stmts, lastE(stmts, a, 0, bi, e), a, tempId(refNs), None, bi, edgeB1))

    case Add(e@("e" | "ec"), a, refNs: String, bi@BiTargetRef(_, _)) if !refNs.startsWith("__") =>
      (cur, None, valueStmts(stmts, lastE(stmts, a, 0, bi, e), a, tempId(refNs), None, bi, edgeB))

    case Add(e@("e" | "ec"), a, refNs: String, bi) if !refNs.startsWith("__") =>
      (cur, edgeB, valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, tempId(refNs), None, bi, edgeB))

    case Add("v", a, refNs: String, bi) if !refNs.startsWith("__") =>
      (cur, edgeB, valueStmts(stmts, stmts.last.v, a, tempId(refNs), None, bi, edgeB))

    case Add("v", a, "__tempId", bi) if forcedE != 0 =>
      (cur, edgeB, valueStmts(stmts, forcedE, a, tempId(a), None, bi, edgeB))

    case Add("v", a, "__tempId", bi) =>
      (cur, edgeB, valueStmts(stmts, stmts.last.v, a, tempId(a), None, bi, edgeB))

    case r: Retract =>
      (cur, edgeB, stmts)


    // Advance cursor for next value in data row
    case Add("__tempId", a, "__arg", bi@BiEdgePropAttr(_)) =>
      val edgeB1 = Some(tempId(a)); (next, edgeB1, valueStmts(stmts, tempId(a), a, arg, None, bi, edgeB1))

    case Add("__tempId", a, "__arg", bi@BiTargetRefAttr(_, _)) =>
      val edgeB1 = Some(tempId(a))
      (next, edgeB1, valueStmts(stmts, tempId(a), a, arg, None, bi, edgeB1))

    case Add("__tempId", a, Values(EnumVal, pf), bi@BiEdgePropAttr(_)) =>
      val edgeB1 = Some(tempId(a))
      (next, edgeB1, valueStmts(stmts, tempId(a), a, arg, pf, bi, edgeB1))

    case Add("__tempId", a, Values(EnumVal, pf), bi@BiTargetRefAttr(_, _)) =>
      val edgeB1 = Some(tempId(a))
      (next, edgeB1, valueStmts(stmts, tempId(a), a, arg, pf, bi, edgeB1))

    case Add("__tempId", a, Values(vs, pf), bi@BiEdgePropAttr(_)) =>
      val edgeB1 = Some(tempId(a))
      (next, edgeB1, valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB1))

    case Add("__tempId", a, Values(vs, pf), bi@BiTargetRefAttr(_, _)) =>
      val edgeB1 = Some(tempId(a))
      (next, edgeB1, valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB1))

    case Add("__tempId", a, "__arg", bi)             =>
      (next, edgeB, valueStmts(stmts, tempId(a), a, arg, None, bi, edgeB))
    case Add("__tempId", a, Values(EnumVal, pf), bi) =>
      (next, edgeB, valueStmts(stmts, tempId(a), a, arg, pf, bi, edgeB))

    case Add("__tempId", a, Values(vs, pf), bi) =>
      (next, edgeB, valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB))

    case Add("remove_me", a, "__arg", bi) =>
      (next, edgeB, valueStmts(stmts, -1, a, arg, None, bi, edgeB))

    case Add("__arg", a, "__tempId", bi) =>
      (next, edgeB, valueStmts(stmts, arg, a, tempId(a), None, bi, edgeB))

    case Add(e@("e" | "ec"), a, "__arg", bi) =>
      (next, edgeB, valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, arg, None, bi, edgeB))

    case Add(e@("e" | "ec"), a, Values(EnumVal, prefix), bi) =>
      (next, edgeB, valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, arg, prefix, bi, edgeB))

    case Add(e@("e" | "ec"), a, Values(vs, prefix), bi) =>
      (next, edgeB, valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, vs, prefix, bi, edgeB))

    case Add("v", a, "__arg", bi) if eidV(stmts) =>
      (next, edgeB, valueStmts(stmts, stmts.last.v, a, arg, None, bi, edgeB))

    case Add("v", a, "__arg", bi) =>
      (next, edgeB, valueStmts(stmts, lastV(stmts, a, forcedE), a, arg, None, bi, edgeB))

    case Add("v", a, Values(EnumVal, prefix), bi) =>
      (next, edgeB, valueStmts(stmts, lastV(stmts, a, forcedE), a, arg, prefix, bi, edgeB))

    case Add("v", a, Values(vs, prefix), bi) =>
      (next, edgeB, valueStmts(stmts, lastV(stmts, a, forcedE), a, vs, prefix, bi, edgeB))

    case Add("tx", a, "__arg", bi) =>
      (next, edgeB, valueStmts(stmts, tempId("tx"), a, arg, None, bi, edgeB))

    case Add(_, _, nestedGenStmts0: Seq[_], _) if arg == Nil =>
      (next, edgeB, stmts)


    // Nested data structures
    case Add(e, ref, nestedGenStmts0: Seq[_], bi) => {
      val parentE = if (e == "parentId")
        tempId(ref)
      else if (stmts.isEmpty)
        e
      else
        stmts.reverse.collectFirst {
          // Find entity value of Add statement with matching namespace
          case add: Add if eidV(stmts) => stmts.last.v

          case Add(e1, a, _, _)
            if a.replaceFirst("/.*", "") == ref.replaceFirst("/.*", "") => e1
        }.getOrElse(
          err("", "Couldn't find previous statement with matching namespace." +
            "\ne  : " + e +
            "\nref: " + ref)
        )

      val nestedGenStmts = nestedGenStmts0.map { case s: Statement => s }
      val nestedRows     = untupleNestedArgss(nestedGenStmts0, arg)

      val nestedInsertStmts: Seq[Statement] = bi match {

        // Bidirectional self references ...........................................................

        // Nested self references
        // living_Person.name.Friends.*(living_Person.name) insert List(...
        case BiSelfRef(2) => nestedRows.flatMap { nestedRow =>
          val forcedE     = tempId(ref)
          val bondStmts   = Seq(
            Add(forcedE, ref, parentE, Card(2)),
            Add(parentE, ref, forcedE, Card(2))
          )
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, forcedE)
          bondStmts ++ nestedStmts
        }

        // Bidirectional other references ...........................................................

        // Nested other references
        // living_Person.name.Buddies.*(living_Animal.name) insert List(...
        case BiOtherRef(2, revRefAttr) => nestedRows.flatMap { nestedRow =>
          val forcedE     = tempId(ref)
          val bondStmts   = Seq(
            Add(forcedE, revRefAttr, parentE, Card(2)),
            Add(parentE, ref, forcedE, Card(2))
          )
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, forcedE)
          bondStmts ++ nestedStmts
        }


        // Bidirectional property edges ...........................................................

        // Nested property edges
        // living_Person.name.Knows.*(living_Knows.weight.Person.name) insert List(...
        case BiEdgeRef(2, revAttr) => nestedRows.flatMap { nestedRow =>
          val edgeA       = tempId(revAttr)
          val edgeB1      = tempId(ref)
          val bondStmts   = Seq(
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
          val edgeA       = tempId(ref)
          val bondStmts   = Seq(
            Add(edgeB.get, ref, edgeA, Card(2)),
            Add(parentE, ref, edgeA, Card(2))
          )
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, edgeA)
          bondStmts ++ nestedStmts
        }


        // Uni-directional (normal) nested rows ...............................................

        // Invoice.id.InvoiceLines * InvoiceLine.no.item.price
        case _ => nestedRows.flatMap { nestedRow =>
          val forcedE     = tempId(ref)
          val bondStmt    = Add(parentE, ref, forcedE, Card(2))
          val nestedStmts = resolveStmts(nestedGenStmts, nestedRow, forcedE)
          bondStmt +: nestedStmts
        }
      }

      (next, edgeB, stmts ++ nestedInsertStmts)
    }

    case unexpected => err("matchDataStmt",
      s"Unexpected insert statement: $unexpected\nGenericStmt: $genericStmt\nStmts:\n" + stmts.mkString("\n"))
  }


  private def resolveStmts(
    genericStmts: Seq[Statement],
    row: Seq[Any],
    forcedE0: Any,
    edgeB0: Option[AnyRef] = None
  ): Seq[Statement] = {
    val stmts1 = genericStmts.foldLeft(0, edgeB0, Seq.empty[Statement]) {
      case ((cur, edgeB, stmts0), genericStmt) =>
        val arg0      = row(cur)
        val next: Int = if ((cur + 1) < row.size) cur + 1 else cur

        val (stmts, forcedE) = if (stmts0.isEmpty)
          (stmts0, forcedE0)
        else stmts0.last match {
          case Add("nsFull", _, backRef, _) => (stmts0.init, backRef)
          case Add(_, _, _: TempId, _)      => (stmts0, 0)
          case Add(-1, _, _, _)             => (stmts0, 0)
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
            (cur, edgeB, stmts :+ Add("nsFull", nsFull, backRef, bi))

          case (None, Add("e", a, refNs: String, bi@BiEdgeRef(_, _))) if !refNs.startsWith("__") =>
            val (edgeA, edgeB1) = (tempId(refNs), Some(tempId(a)))
            (cur, edgeB1, valueStmts(stmts, lastE(stmts, a, 0, bi), a, edgeA, None, bi, edgeB1))

          case (None, Add("e", a, refNs: String, bi)) if !refNs.startsWith("__") =>
            (cur, edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB))

          case (None, _) =>
            (next, edgeB, stmts)

          case (Some(arg), genericStmt) =>
            matchDataStmt(stmts, genericStmt, arg, cur, next, forcedE, edgeB)

          case (arg, Add("e", a, "__arg", bi)) if eidV(stmts) =>
            (next, edgeB, valueStmts(stmts, stmts.last.v, a, arg, None, bi, edgeB))

          case (arg, genStmt@Add("e", _, refNs: String, _)) if !refNs.startsWith("__") && eidV(stmts) =>
            matchDataStmt(stmts, genStmt.copy(e = "v"), arg, cur, next, forcedE, edgeB)

          case (arg, genStmt) =>
            matchDataStmt(stmts, genStmt, arg, cur, next, forcedE, edgeB)
        }
    }._3

    if (stmts1.isEmpty) {
      Seq.empty[Statement]
    } else if (stmts1.last.v.isInstanceOf[TempId]) {
      stmts1.init
    } else {
      stmts1
    }.filterNot(_.e == -1)
  }


  def insertStmts(dataRows: Iterable[Seq[Any]]): Seq[Statement] = {
    val (genericStmts, genericTxStmts) = splitStmts()
    lazy val dataStmts = dataRows.toSeq.flatMap(resolveStmts(genericStmts, _, 0))
    lazy val txStmts   = genericTxStmts.foldLeft(
      Seq.empty[Statement]
    ) {
      case (stmts, genericTxStmt) => {
        genericTxStmt match {
          case Add("tx", a, Values(vs, prefix), bi) =>
            if (txRefAttr(stmts))
              valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, None)
            else
              valueStmts(stmts, tempId("tx"), a, vs, prefix, bi, None)

          case Add("tx", a, refNs: String, bi) if !refNs.startsWith("__") =>
            valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(refNs), None, bi, None)

          case Add("txRef", a, Values(vs, prefix), bi) =>
            if (txRefAttr(stmts))
              valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, None)
            else
              valueStmts(stmts, stmts.last.e.asInstanceOf[Object], a, vs, prefix, bi, None)

          case Add("txRef", a, refNs: String, bi) if !refNs.startsWith("__") =>
            valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(refNs), None, bi, None)

          case unexpected =>
            err("insertStmts", "Unexpected insert statement: " + unexpected)
        }
      }
    }

    // Test that async transformer behaves the same
    //    Await.result(ModelTransformerAsync(conn, model).insertStmts(dataRows), 10.seconds)
    dataStmts ++ txStmts
  }


  def saveStmts: Seq[Statement] = {
    lazy val stmts = genericStmts.foldLeft(
      "",
      Option.empty[AnyRef],
      Seq.empty[Statement]
    ) {
      case ((backRef, edgeB, stmts), genericStmt) =>
        genericStmt match {

          case _: Retract => (backRef, edgeB, stmts)

          case Add("__tempId", a, Values(vs, pf), bi) => bi match {
            case _: BiEdgePropAttr =>
              val edgeB1 = Some(tempId(a))
              (backRef, edgeB1, valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB1))
            case _                 =>
              (backRef, edgeB, valueStmts(stmts, tempId(a), a, vs, pf, bi, edgeB))
          }

          case Add(e, a, "__tempId", bi) => (backRef, edgeB, e match {
            case "e" => valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(a), None, bi, edgeB)
            case _   => valueStmts(stmts, e, a, tempId(a), None, bi, edgeB)
          })

          case Add(e@("e" | "ec"), a, Values(vs, prefix), bi) =>
            (backRef, edgeB, valueStmts(stmts, lastE(stmts, a, 0L, bi, e), a, vs, prefix, bi, edgeB))

          case Add(e@("e" | "ec"), a, refNs: String, bi) if !refNs.startsWith("__") => bi match {
            case _: BiEdgeRef =>
              val edgeB1 = Some(tempId(a))
              (backRef, edgeB1, valueStmts(stmts, lastE(stmts, a, 0L, bi, e), a, tempId(refNs), None, bi, edgeB1))

            case _: BiTargetRef =>
              (backRef, None, valueStmts(stmts, lastE(stmts, a, 0L, bi, e), a, tempId(refNs), None, bi, edgeB))

            case _ =>
              val stmts2 = if (backRef.isEmpty) {
                valueStmts(stmts, lastE(stmts, a, 0L, bi, e), a, tempId(refNs), None, bi, edgeB)
              } else {
                val forcedE = stmts.reverse.collectFirst {
                  case Add(e: TempId, a, _, _) if a.startsWith(backRef) => e
                } getOrElse err("saveStmts",
                  s"Couldn't find backref namespace `$backRef` in any previous Add statements.\n"
                    + stmts.mkString("\n"))
                valueStmts(stmts, lastE(stmts, a, forcedE, bi, e), a, tempId(refNs), None, bi, edgeB)
              }

              ("", edgeB, stmts2)
          }

          case Add("v", a, Values(vs, prefix), bi) =>
            (backRef, edgeB, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB))

          case Add("tx", a, Values(vs, prefix), bi) =>
            val futStmts2 =
              if (txRefAttr(stmts)) {
                valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB)
              } else {
                valueStmts(stmts, datomicTx, a, vs, prefix, bi, edgeB)
              }

            (backRef, edgeB, futStmts2)

          case Add("tx", a, refNs: String, bi) if !refNs.startsWith("__") =>
            (backRef, edgeB, valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(refNs), None, bi, edgeB))

          case Add("txRef", a, Values(vs, prefix), bi) =>
            val futStmts2 = if (txRefAttr(stmts))
              valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB)
            else
              valueStmts(stmts, stmts.last.e.asInstanceOf[Object], a, vs, prefix, bi, edgeB)

            (backRef, edgeB, futStmts2)

          case Add("txRef", a, refNs: String, bi) if !refNs.startsWith("__") =>
            (backRef, edgeB, valueStmts(stmts, lastE(stmts, a, 0L, bi), a, tempId(refNs), None, bi, edgeB))

          case Add("nsFull", backRef, _, _) => (backRef, edgeB, stmts)

          case Add(id: Long, _, Values(_, _), _) =>
            err("saveStmts", s"With a given id `$id` please use `update` instead.")

          case Add(_, a, "__arg", _) =>
            err("saveStmts", s"Attribute `$a` needs a value applied")

          case unexpected =>
            err("saveStmts",
              s"Unexpected save statement: $unexpected\nStatements so far:\n" + stmts.mkString("\n")
            )
        }
    }._3

    // Test that async transformer behaves the same
    //    Await.result(ModelTransformerAsync(conn, model).saveStmts, 10.seconds)
    stmts
  }


  def updateStmts: Seq[Statement] = {
    val (genericStmts, genericTxStmts) = splitStmts()
    lazy val dataStmts: Seq[Statement] = genericStmts.foldLeft(
      0L,
      Option.empty[AnyRef],
      Seq.empty[Statement],
    ) { case ((prevE, edgeB, stmts), genericStmt) =>
      genericStmt match {
        case Add("e", a, "__tempId", bi) =>
          (0L, edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(a), None, bi, edgeB))

        case Add(e, a, "__tempId", bi@BiEdgeRef(_, _)) =>
          val edgeB1 = Some(tempId(a))
          (0L, edgeB1, valueStmts(stmts, e, a, tempId(a), None, bi, edgeB1))

        case Add(e, a, "__tempId", bi) =>
          (0L, edgeB, valueStmts(stmts, e, a, tempId(a), None, bi, edgeB))

        case Add("e", a, Values(vs, prefix), bi) if prevE == 0L =>
          (0L, edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, vs, prefix, bi, edgeB))

        case Add("e", a, Values(vs, prefix), bi) =>
          val addStmts = valueStmts(stmts, prevE, a, vs, prefix, bi, edgeB)
          if (addStmts.isEmpty)
            (prevE, edgeB, Nil)
          else
            (0L, edgeB, addStmts)

        case Add("e", a, refNs: String, bi@BiTargetRef(_, _)) if !refNs.startsWith("__") =>
          (0L, None, valueStmts(stmts, lastE(stmts, a, 0, bi), a, tempId(refNs), None, bi, edgeB))

        case Add("v", a, Values(vs, prefix), bi) =>
          (0L, edgeB, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB))

        case Add("tx", a, Values(vs, prefix), bi) =>
          (0L, edgeB, valueStmts(stmts, stmts.last.v.asInstanceOf[Object], a, vs, prefix, bi, edgeB))

        case Add(e, a, Values(vs, prefix), bi) =>
          val addStmts = valueStmts(stmts, e, a, vs, prefix, bi, edgeB)
          if (addStmts.isEmpty)
            (e.asInstanceOf[Long], edgeB, Nil) // pass eid so that we have it for subsequent stmts
          else
            (0L, edgeB, addStmts)

        case Retract("e", a, Values(vs, prefix), bi) =>
          (0L, edgeB, valueStmts(stmts, lastE(stmts, a, 0, bi), a, vs, prefix, bi, edgeB))

        case Retract(e, a, Values(vs, prefix), bi) =>
          (0L, edgeB, valueStmts(stmts, e, a, vs, prefix, bi, edgeB))

        case Add(_, a, "__arg", _) =>
          err("updateStmts", s"Attribute `$a` needs a value applied")

        case unexpected =>
          err("updateStmts", "Unexpected update statement: " + unexpected)
      }
    }._3

    lazy val txStmts = genericTxStmts.foldLeft(Seq[Statement]()) {
      case (stmts, Add("tx", a, Values(vs, prefix), bi)) =>
        valueStmts(stmts, datomicTx, a, vs, prefix, bi, None)

      case (_, unexpected) =>
        err("updateStmts", "Unexpected insert statement: " + unexpected)
    }

    // Test that async transformer behaves the same
    //    Await.result(ModelTransformerAsync(conn, model).updateStmts, 10.seconds)
    dataStmts ++ txStmts
  }
}
