package molecule.core.ops

import molecule.core.ast.elements.{VarValue, _}
import molecule.core.ops.exception.VerifyModelException
import molecule.core.util.Helpers


case class VerifyModel(model: Model, op: String) extends Helpers {

  val datomGenerics = Seq(
    "e", "e_", "tx", "t", "txInstant", "op", "tx_", "t_", "txInstant_", "op_", "a", "a_", "v", "v_")

  // Perform verifications upon instantiation
  op match {
    case "save"   => verifySave()
    case "insert" => verifyInsert()
    case "update" => verifyUpdate()
  }

  private def verifySave(): Unit = {
    unexpectedAppliedId
    noGenericsInTail
    noTacitAttrs
    //    missingAttrInStartEnd
    noConflictingCardOneValues
    noNested
    noEdgePropRefs
    edgeComplete
  }

  private def verifyInsert(): Unit = {
    unexpectedAppliedId
    noGenericsInTail
    onlyTacitTxAttrs
    noTacitAttrs
    //    missingAttrInStart
    noNestedEdgesWithoutTarget
    edgeComplete
  }

  private def verifyUpdate(): Unit = {
    update_onlyOneNs
    missingAppliedId
    onlyAtomsWithValue
    noConflictingCardOneValues
    noEdgePropRefs
    noNested
    update_edgeComplete
  }


  // todo - avoid
  //  living_Person.knows(Nil).update
  //  living_Person.knows(count).inspectUpdate

  private def err(method: String, msg: String): Nothing = {
    throw VerifyModelException(s"[$method]  $msg")
  }

  private def extractNs(fullAttr: String): String = {
    val ns = fullAttr.split(":").last.split("/").head
    if (ns.contains('_')) ns else ns.capitalize
  }

  private def Ns(nsFull: String): String = if (nsFull.contains('_')) nsFull else nsFull.capitalize

  // Avoid mixing insert/update style
  private def unexpectedAppliedId: Element = model.elements.head match {
    case Generic(_, "e" | "e_", _, Eq(List(_)), _) => err("unexpectedAppliedId",
      s"""Applying an eid is only allowed for updates.""")
    case ok                                        => ok
  }

  private def missingAppliedId: Boolean = model.elements.head match {
    case Generic(_, "e" | "e_", _, Eq(List(eid)), _) =>
      true
    case Generic(_, "e" | "e_", _, Eq(eids), _)      => true
    case Composite(elements)                         => elements.head match {
      case Generic(_, "e" | "e_", _, Eq(eids), _) => true
      case _                                      => false
    }
    case Atom(nsFull, _, _, _, _, _, _, _, _)        =>
      err("missingAppliedId",
        s"Update molecule should start with an applied id: `${Ns(nsFull)}(<eid>)...`")
    case other                                       =>
      err("missingAppliedId", "unexpected element: " + other)
  }

  private def onlyAtomsWithValue = model.elements.foreach {
    case a: Atom => a.value match {
      case VarValue | EntValue | EnumVal | IndexVal | Qm | Distinct | NoValue => err("onlyAtomsWithValue",
        "Update molecule can only have attributes with some value(s) applied/added/replaced etc.")
      case bi if bi.isInstanceOf[Bidirectional]                               => err("onlyAtomsWithValue",
        "Update molecule can only have attributes with some value(s) applied/added/replaced etc.")
      case other                                                              => true
    }
    case other   => true
  }

  private def noGenericsInTail: Option[Nothing] = model.elements.tail.collectFirst {
    case Generic(_, attr, _, Eq(List(eid)), _) if datomGenerics.contains(attr) => err("noGenerics",
      s"Generic elements `e`, `a`, `v`, `ns`, `tx`, `t`, `txInstant` and `op` " +
        s"not allowed in $op molecules. Found `e($eid)`")
  }

  private def onlyTacitTxAttrs: Seq[Option[Nothing]] = model.elements.collect {
    case TxMetaData(es) => es.collectFirst {
      case Atom(nsFull, attr, _, _, _, _, _, _, _) if !attr.endsWith("_") =>
        val attrClean = if (attr.endsWith("$")) attr.init else attr
        err("onlyTacitTxAttrs",
          s"For inserts, tx meta data can only be applied to tacit attributes, " +
            s"like: `${nsFull.capitalize}.${attrClean}_(<metadata>)`")
    }
  }

  private def noTacitAttrs: Unit = {
    def detectTacitAttrs(elements: Seq[Element]): Seq[Element] = elements flatMap {
      case a: Atom if a.attr.last == '_' =>
        err("noTacitAttrs", s"Tacit attributes like `${a.attr}` not allowed in $op molecules.")
      case Nested(ref, es)               => detectTacitAttrs(es)
      case Composite(es)                 => detectTacitAttrs(es)
      case e: Element                    => Seq(e)
    }
    detectTacitAttrs(model.elements)
  }

  private def missingAttrInStart: Unit = {
    model.elements.foldLeft(Seq[Element]()) {
      case (attrs, e) => e match {
        case a: Atom if a.attr.last != '$'            => attrs :+ a
        case g@Generic(_, "e" | "e_", _, EntValue, _) => attrs :+ g
        case b: Bond if attrs.isEmpty                 =>
          err("missingAttrInStartEnd", "Missing mandatory attributes of first namespace.")
        case _                                        => attrs
      }
    }
    def missingAttrInEnd(elements: Seq[Element]): Seq[Element] = elements.foldRight(Seq[Element]()) {
      case (e, attrs) => e match {
        case a: Atom if a.attr.last != '$' => attrs :+ a
        case Nested(ref, es)               => missingAttrInEnd(es)
        case b: Bond if attrs.isEmpty      =>
          err("missingAttrInStartEnd", "Missing mandatory attributes of last namespace.")
        case _                             => attrs
      }
    }
    missingAttrInEnd(model.elements)
  }

  private def noConflictingCardOneValues: Unit = {
    def catchConflictingCardOneValues(elements: Seq[Element]): Unit = elements.collectFirst {
      case Atom(nsFull, attr, tpe, 1, Eq(vs), _, _, _, _) if vs.length > 1 =>
        val format = (v: Any) => jsNumber(tpe, v)
        err("noConflictingCardOneValues",
          s"""Can't $op multiple values for cardinality-one attribute:
             |  ${Ns(nsFull)} ... $attr(${vs.map(format).mkString(", ")})""".stripMargin)
      case Nested(_, es)                                                   => catchConflictingCardOneValues(es)
      case Composite(es)                                                   => catchConflictingCardOneValues(es)
    }
    catchConflictingCardOneValues(model.elements)
  }

  private def update_onlyOneNs: Seq[Boolean] = model.elements.collect {
    // Allow bidirectional references
    case Bond(_, _, _, _, Seq(BiSelfRef(_)))      => true
    case Bond(_, _, _, _, Seq(BiOtherRef(_, _)))  => true
    case Bond(_, _, _, _, Seq(BiEdgeRef(_, _)))   => true
    case Bond(_, _, _, _, Seq(BiTargetRef(_, _))) => true

    // Otherwise disallow refs in updates
    case Bond(_, _, refNs, _, _) =>
      err("update_onlyOneNs",
        op.capitalize + s" molecules can't span multiple namespaces like `${Ns(refNs)}`.")

    case Nested(Bond(_, _, refNs, _, _), _) =>
      err("update_onlyOneNs",
        op.capitalize + s" molecules can't have nested data structures like `${Ns(refNs)}`.")

    case c: Composite =>
      err("update_onlyOneNs",
        op.capitalize + " molecules can't be composites.")
  }

  private def noNested: Unit = {
    def checkNested(elements: Seq[Element]): Unit = elements.collectFirst {
      case n: Nested     => err("noNested", s"Nested data structures not allowed in $op molecules")
      case Composite(es) => checkNested(es)
    }
    checkNested(model.elements)
  }

  // Todo: Might be possible to implement if we control that the molecule doesn't build further out
  private def noEdgePropRefs: Option[Unit] = model.elements.collectFirst {
    case Bond(_, refAttr, _, _, Seq(BiEdgePropRef(_))) => err("noEdgePropRefs",
      s"Building on to another namespace from a property edge of a $op molecule not allowed. " +
        s"Please create the referenced entity separately and apply the created ids to a ref attr instead, " +
        s"like `.$refAttr(<refIds>)`")
  }


  private def noNestedEdgesWithoutTarget: Unit = {
    def checkNested(elements: Seq[Element]): Unit = elements.collectFirst {

      case Nested(Bond(baseNs, _, refNs, _, Seq(BiEdgeRef(_, _))), es)
        if !es.collectFirst {
          // One of those is expected
          case Bond(_, _, _, _, Seq(BiTargetRef(_, attr)))                 => true
          case Atom(_, _, _, _, _, _, Seq(BiTargetRefAttr(_, attr)), _, _) => true
        }.getOrElse(false) => err("noNestedEdgesWithoutTarget",
        s"Nested edge ns `${Ns(refNs)}` should link to target ns within the nested group of attributes.")
    }
    checkNested(model.elements)
  }


  private def edgeComplete: Unit = {
    /*
      In order to maintain data consistency for bidirectional edges we need to ensure
      that no edge is connected to only the base or target entity.

      base <--> edge <--> target
    */

    def missingBase(elements: Seq[Element]): Unit = {

      def hasBase(es: Seq[Element], edgeNs: String): Option[Boolean] = elements.collectFirst {
        // Base.attr.Edge ...
        case Bond(_, _, edgeNs1, _, Seq(BiEdgeRef(_, _))) if edgeNs1 == edgeNs => true
        // Base.attr.edge ...
        case Atom(_, _, _, _, _, _, Seq(BiEdgeRefAttr(_, edgeRefAttr)), _, _) if extractNs(edgeRefAttr) == edgeNs => true
      }
    }

    def missingTarget(elements: Seq[Element]): Unit = {

      def hasTarget(es: Seq[Element], edgeNs: String): Option[Boolean] = elements.collectFirst {
        // ... TargetNs
        case Bond(edgeNs1, _, _, _, Seq(BiTargetRef(_, _))) if edgeNs1 == edgeNs => true
        // ... targetAttr
        case Atom(edgeNs1, _, _, _, _, _, Seq(BiTargetRefAttr(_, attr)), _, _) if edgeNs1 == edgeNs => true
      }

      elements.collectFirst {
        // Base.attr.Edge ..?
        case Bond(_, _, edgeNs, _, Seq(BiEdgeRef(_, _))) => hasTarget(elements, edgeNs) getOrElse
          err("edgeComplete", s"Missing target namespace after edge namespace `${Ns(edgeNs)}`.")

        // Edge.prop ..?
        case Atom(edgeNs, prop, _, _, _, _, Seq(BiEdgePropAttr(_)), _, _) =>
          hasTarget(elements, edgeNs) getOrElse
            err("edgeComplete",
              s"Missing target namespace somewhere after edge property `${Ns(edgeNs)}/$prop`.")
      }
    }

    model.elements.head match {
      case Generic(_, "e_", "e", Eq(List(eid)), _) => // BiEdge
      case checkNext                               => missingTarget(model.elements)
    }
  }


  private def update_edgeComplete: Unit = {
    def missingTarget(elements: Seq[Element]): Unit = {

      // Ok if target Ns is present - then we are updating with a new edge to a new/existing target entity.
      def hasTarget(es: Seq[Element], edgeNs: String) = elements.collectFirst {
        // ... TargetNs
        case Bond(edgeNs1, _, _, _, Seq(BiTargetRef(_, _))) if edgeNs1 == edgeNs => true
        // ... targetAttr
        case Atom(edgeNs1, _, _, _, _, _, Seq(BiTargetRefAttr(_, _)), _, _) if edgeNs1 == edgeNs => true
      }

      elements.collectFirst {
        // Base.attr.Edge ..?
        case Bond(baseNs, _, edgeNs, _, Seq(BiEdgeRef(_, _))) => hasTarget(elements, edgeNs) getOrElse
          err("update_edgeComplete",
            s"Can't update edge `${Ns(edgeNs)}` of base entity `${Ns(baseNs)}` without knowing which target " +
              s"entity the edge is pointing too. " +
              s"Please update the edge itself, like `${Ns(edgeNs)}(<edgeId>).edgeProperty(<new value>).update`.")
      }
    }
    missingTarget(model.elements)
  }
}
