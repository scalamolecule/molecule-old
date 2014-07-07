package molecule
package transform
import datomic.{Connection, Database, Peer}
import molecule.ast.model._
import molecule.db.DatomicFacade
import molecule.util.Debug


object Model2Transaction extends Debug with DatomicFacade {
  val y = debug("Model2Transaction", 1)

  def apply(conn: Connection, model: Model, argss: Seq[Seq[Any]]) = {
    val attrs = getEmptyAttrs(model)
    val rawMolecules = chargeMolecules(attrs, argss)
    val molecules = groupNamespaces(rawMolecules)
    upsertTransaction(conn.db, molecules)
  }

  def getEmptyAttrs(model: Model): Seq[(String, String, Int, String)] = model.elements.collect {
    case Atom(ns, n, tpe, card, VarValue, _)               => (ns, n, card, "")
    case Atom(ns, n, tpe, card, EnumVal, Some(enumPrefix)) => (ns, n, card, enumPrefix)
  }

  def getNonEmptyAttrs(model: Model): Seq[((String, String, Int, String), Seq[Any])] = model.elements.collect {
    case Atom(ns, attr, tpe, card, Eq(values), prefix)         => ((ns, attr, card, prefix.getOrElse("")), values)
    case Atom(ns, attr, tpe, card, replace@Replace(_), prefix) => ((ns, attr, card, prefix.getOrElse("")), Seq(replace))
    case Atom(ns, attr, tpe, card, remove@Remove(_), prefix)   => ((ns, attr, card, prefix.getOrElse("")), Seq(remove))
  }

  def chargeMolecules(attrs: Seq[(String, String, Int, String)], argss: Seq[Seq[Any]]): Seq[Seq[Seq[Any]]] = {
    assert(attrs.size == argss.head.size, s"Arity of attributes and values should match. Found:\n" +
      attrs.size + " attributes     : " +
      attrs.map(a => ":" + a._1 + "/" + a._2).mkString(", ") + "\n" +
      argss.head.size + "-arity of values: " + argss +
      "\nThe type system should prevent us from getting here. Please file a bug!\n----------------------")

    argss.map { args =>
      // Process molecule attributes from last to first (insert related namespaces first)
      attrs.zip(args).reverse.flatMap { data =>
        val (at, values) = (data._1, data._2)
        val (ns, attr, cardinality, enumPrefix) = (at._1, at._2, at._3, at._4)
        if (values == null)
        // No fact added when value is null
          None
        else
          Some(Seq(ns, attr, cardinality, enumPrefix, values))
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

  def p(prefix: Any, value: Any) = {
    // Allows value to be of another type when it's not prefixed by a String!
    if (prefix.toString.nonEmpty) prefix.toString + value else value
  }

  def upsertTransaction(db: Database, molecules: Seq[Seq[Seq[Seq[Any]]]], ids: Seq[Long] = Seq()): Seq[Seq[Any]] = {
    //    if (ids.nonEmpty)
    //      assert(molecules.size == ids.size,
    //        s"[Model2Transaction:upsertTransaction] Number of molecules and ids should match\n" +
    //          s"${molecules.size} molecules:\n  ${molecules.mkString("\n  ")} \n" +
    //          s"${ids.size} ids      : $ids")

    molecules.zipAll(ids, Seq(Seq(Seq[Any]())), 0L).flatMap { case (molecule, id0) =>
      val (moleculeStmts: Seq[Seq[Any]], _, _) = molecule.foldLeft((Seq[Seq[Any]](), "": Any, "": Any)) {
        case ((stmts, prevNS, prevId), namespace) => {
          val firstAttr = namespace.head
          val ns = firstAttr.head
          val id = if (id0 > 0L) id0 else Peer.tempid(":db.part/user")

          val namespaceStmts: Seq[Seq[Any]] = namespace.foldLeft(Seq[Seq[Any]]()) { case (attrStmts, atom) =>
            val (attr, prefix, values) = (atom(1), atom(3), atom(4))
            values match {
              case Seq(Replace(oldNew))       => oldNew.foldLeft(attrStmts) { case (acc, (oldValue, newValue)) =>
                acc :+ Seq(":db/retract", id, s":$ns/$attr", oldValue) :+ Seq(":db/add", id, s":$ns/$attr", newValue)
              }
              case Seq(Remove(Seq()))         => getValues(db, id, ns, attr).foldLeft(attrStmts) { case (acc, removeValue) =>
                acc :+ Seq(":db/retract", id, s":$ns/$attr", removeValue)
              }
              case Seq(Remove(removeValues))  => removeValues.foldLeft(attrStmts) { case (acc, removeValue) =>
                acc :+ Seq(":db/retract", id, s":$ns/$attr", removeValue)
              }
              case set: Set[_]                => attrStmts ++ set.map(Seq(":db/add", id, s":$ns/$attr", _))
              case vs: List[_] if vs.size > 1 => attrStmts ++ vs.map(Seq(":db/add", id, s":$ns/$attr", _))
              case value :: Nil               => attrStmts :+ Seq(":db/add", id, s":$ns/$attr", p(prefix, value))
              case value                      => attrStmts :+ Seq(":db/add", id, s":$ns/$attr", p(prefix, value))
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