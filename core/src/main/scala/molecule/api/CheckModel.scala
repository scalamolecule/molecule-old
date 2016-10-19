package molecule.api
import molecule.ast.model._

case class CheckModel(model: Model, op: String) {

  op match {
    case "save"   => checkSave()
    case "insert" => checkInsert()
    case "update" => checkUpdate()
  }

  private def checkSave() {
    unexpectedAppliedId
    noGenericsInTail
    noTacetAttrs
    noTransitiveAttrs
    missingAttrInStartEnd
    noConflictingCardOneValues
    noNested
    noEdgePropRefs
    edgeComplete
  }

  private def checkInsert() {
    unexpectedAppliedId
    noGenericsInTail
    noTacetAttrs
    noTransitiveAttrs
    missingAttrInStartEnd
    noNestedEdgesWithoutTarget
    edgeComplete
  }

  private def checkUpdate() {
    update_onlyOneNs
    missingAppliedId
    noConflictingCardOneValues
    noEdgePropRefs
    noNested
    update_edgeComplete
  }


  // todo - avoid
  //  living_Person.knows(nil).update
  //  living_Person.knows(count).updateD

  private def iae(method: String, msg: String) = {
    throw new IllegalArgumentException(s"[molecule.api.CheckModel.$method]  $msg")
  }

  private def extractNs(fullAttr: String) = {
    val ns = fullAttr.split(":").last.split("/").head
    if (ns.contains('_')) ns else ns.capitalize
  }

  private def Ns(ns: String) = if (ns.contains('_')) ns else ns.capitalize

  // Avoid mixing insert/update style
  private def unexpectedAppliedId = model.elements.head match {
    case Meta(ns, _, "e", NoValue, Eq(List(eid))) => iae("unexpectedAppliedId",
      s"Can't $op molecule with an applied eid as in `${ns.capitalize}(eid)`. " +
        s"""Applying an eid is for updates, like `${Ns(ns)}(johnId).likes("pizza").update`""")
    case ok                                       => ok
  }
  private def missingAppliedId = model.elements.head match {
    case Meta(_, _, "e", BiEdge, Eq(List(eid))) => true
    case Meta(_, _, "e", NoValue, Eq(eids))     => true
    case Atom(ns, _, _, _, _, _, _, _)          => iae("missingAppliedId", s"Update molecule should start with an applied id: `${Ns(ns)}(<eid>)...`")
  }

  private def noGenericsInTail = model.elements.tail.collectFirst {
    case Meta(_, _, "e", _, Eq(List(eid))) => iae("noGenerics",
      s"Generic elements `e`, `a`, `v`, `ns`, `tx`, `txT`, `txInstant` and `op` " +
        s"not allowed in $op molecules. Found `e($eid)`")
  }

  private def noTacetAttrs {
    def detectTacetAttrs(elements: Seq[Element]): Seq[Element] = elements flatMap {
      case a: Atom if a.name.last == '_' => iae("noTacetAttrs", s"Tacet attributes like `${a.name}` not allowed in $op molecules.")
      case Nested(ref, es)               => detectTacetAttrs(es)
      case Composite(es)                 => detectTacetAttrs(es)
      case e: Element                    => Seq(e)
    }
    detectTacetAttrs(model.elements)
  }

  private def noTransitiveAttrs = model.elements collectFirst {
    case t: Transitive => iae("noTransitiveAttrs", s"Can't $op transitive attribute values (repeated attributes).")
  }

  private def missingAttrInStartEnd {
    model.elements.foldLeft(Seq[Element]()) {
      case (attrs, e) => e match {
        case a: Atom if a.name.last != '$'        => attrs :+ a
        case m@Meta(_, _, "e", NoValue, EntValue) => attrs :+ m
        case b: Bond if attrs.isEmpty             => iae("missingAttrInStartEnd", "Missing mandatory attributes of first namespace.")
        case _                                    => attrs
      }
    }
    def missingAttrInEnd(elements: Seq[Element]): Seq[Element] = elements.foldRight(Seq[Element]()) {
      case (e, attrs) => e match {
        case a: Atom if a.name.last != '$' => attrs :+ a
        case Nested(ref, es)               => missingAttrInEnd(es)
        case b: Bond if attrs.isEmpty      => iae("missingAttrInStartEnd", "Missing mandatory attributes of last namespace.")
        case _                             => attrs
      }
    }
    missingAttrInEnd(model.elements)
  }

  private def noConflictingCardOneValues {
    //    def abort(i: Int, ns: String, attr: String, values: Seq[Any]) =

    def catchConflictingCardOneValues(elements: Seq[Element]): Unit = elements.collectFirst {
      case Atom(ns, attr, _, 1, Eq(vs), _, _, _) if vs.length > 1 => iae("noConflictingCardOneValues",
        s"""Can't $op multiple values for cardinality-one attribute:
           |  ${Ns(ns)} ... $attr(${vs.mkString(", ")})""".stripMargin)
      case Nested(ref, es)                                        => catchConflictingCardOneValues(es)
      case Composite(es)                                          => catchConflictingCardOneValues(es)
    }
    catchConflictingCardOneValues(model.elements)
  }

  private def update_onlyOneNs = model.elements.collect {
    // Allow bidirectional references
    case Bond(_, _, _, _, Seq(BiSelfRef(_)))      => true
    case Bond(_, _, _, _, Seq(BiOtherRef(_, _)))  => true
    case Bond(_, _, _, _, Seq(BiEdgeRef(_, _)))   => true
    case Bond(_, _, _, _, Seq(BiTargetRef(_, _))) => true

    // Otherwise disallow refs in updates
    case Bond(_, _, refNs, _, _)            => iae("update_onlyOneNs", op.capitalize + s" molecules can't span multiple namespaces like `${Ns(refNs)}`.")
    case Nested(Bond(_, _, refNs, _, _), _) => iae("update_onlyOneNs", op.capitalize + s" molecules can't have nested data structures like `${Ns(refNs)}`.")
    case c: Composite                       => iae("update_onlyOneNs", op.capitalize + " molecules can't be composites.")
  }

  private def noNested {
    def checkNested(elements: Seq[Element]): Unit = elements.collectFirst {
      case n: Nested     => iae("noNested", s"Nested data structures not allowed in $op molecules")
      case Composite(es) => checkNested(es)
    }
    checkNested(model.elements)
  }

  // Todo: Might be possible to implement if we control that the molecule doesn't build further out
  private def noEdgePropRefs = model.elements.collectFirst {
    case Bond(ns, refAttr, _, _, Seq(BiEdgePropRef(_))) => iae("noEdgePropRefs",
      s"Building on to another namespace from a property edge of a $op molecule not allowed. " +
        s"Please create the referenced entity sepearately and apply the created ids to a ref attr instead, like `.$refAttr(<refIds>)`")
  }


  private def noNestedEdgesWithoutTarget {
    def checkNested(elements: Seq[Element]): Unit = elements.collectFirst {

      case Nested(Bond(baseNs, _, refNs, _, Seq(BiEdgeRef(_, _))), es)
        if !es.collectFirst {
          // One of those is expected
          case Bond(_, _, _, _, Seq(BiTargetRef(_, attr)))              => true
          case Atom(_, _, _, _, _, _, Seq(BiTargetRefAttr(_, attr)), _) => true
        }.getOrElse(false) => iae("noNestedEdgesWithoutTarget",
        s"Nested edge ns `${Ns(refNs)}` should link to target ns within the nested group of attributes.")
    }
    checkNested(model.elements)
  }


  private def edgeComplete {
    /*
      In order to maintain data consistency for bidirectional edges we need to ensure
      that no edge is connected to only the base or target entity.

      base <--> edge <--> target
    */

    def missingBase(elements: Seq[Element]): Unit = {

      def hasBase(es: Seq[Element], edgeNs: String) = elements.collectFirst {
        // Base.attr.Edge ...
        case Bond(_, _, edgeNs1, _, Seq(BiEdgeRef(_, _))) if edgeNs1 == edgeNs => true
        // Base.attr.edge ...
        case Atom(_, _, _, _, _, _, Seq(BiEdgeRefAttr(_, edgeRefAttr)), _) if extractNs(edgeRefAttr) == edgeNs => true
      }

      //      elements.collectFirst {
      // ?.. TargetNs
      //        case Bond(edgeNs, _, _, _, Seq(BiTargetRef(_, _))) => hasBase(elements, edgeNs) getOrElse
      //          iae("edgeComplete", s"Missing base namespace before edge namespace `${Ns(edgeNs)}`.")
      // ?.. targetAttr
      //        case Atom(edgeNs, _, _, _, _, _, Seq(BiTargetRefAttr(_, _)), _) => hasBase(elements, edgeNs) getOrElse
      //          iae("edgeComplete", s"Missing base namespace before edge namespace `${Ns(edgeNs)}`.")
      //      }
    }

    def missingTarget(elements: Seq[Element]): Unit = {

      def hasTarget(es: Seq[Element], edgeNs: String) = elements.collectFirst {
        // ... TargetNs
        case Bond(edgeNs1, _, _, _, Seq(BiTargetRef(_, _))) if edgeNs1 == edgeNs => true
        // ... targetAttr
        case Atom(edgeNs1, _, _, _, _, _, Seq(BiTargetRefAttr(_, attr)), _) if edgeNs1 == edgeNs => true
      }

      elements.collectFirst {
        // Base.attr.Edge ..?
        case Bond(baseNs, _, edgeNs, _, Seq(BiEdgeRef(_, _))) => hasTarget(elements, edgeNs) getOrElse
          iae("edgeComplete", s"Missing target namespace after edge namespace `${Ns(edgeNs)}`.")
        // Base.attr.edge ..?
        //        case Atom(baseNs, _, _, _, _, _, Seq(BiEdgeRefAttr(_, edgeRefAttr)), _) => hasTarget(elements, extractNs(edgeRefAttr)) getOrElse
        //          iae("edgeComplete", s"Missing target namespace after edge namespace `${extractNs(edgeRefAttr)}`.")

        // Edge.prop ..?
        case Atom(edgeNs, prop, _, _, _, _, Seq(BiEdgePropAttr(_)), _) =>
          hasTarget(elements, edgeNs) getOrElse
            iae("edgeComplete", s"Missing target namespace somewhere after edge property `${Ns(edgeNs)}/$prop`.")
      }
    }

    model.elements.head match {
      case Meta(ns, _, "e", BiEdge, Eq(List(eid))) =>
      case checkNext                               =>
        //        missingBase(model.elements)
        missingTarget(model.elements)
    }
  }


  private def update_edgeComplete {
    def missingTarget(elements: Seq[Element]): Unit = {

      // Ok if target Ns is present - then we are updating with a new edge to a new/existing target entity.
      def hasTarget(es: Seq[Element], edgeNs: String) = elements.collectFirst {
        // ... TargetNs
        case Bond(edgeNs1, _, _, _, Seq(BiTargetRef(_, _))) if edgeNs1 == edgeNs => true
        // ... targetAttr
        case Atom(edgeNs1, _, _, _, _, _, Seq(BiTargetRefAttr(_, attr)), _) if edgeNs1 == edgeNs => true
      }

      elements.collectFirst {
        // Base.attr.Edge ..?
        case Bond(baseNs, _, edgeNs, _, Seq(BiEdgeRef(_, _))) => hasTarget(elements, edgeNs) getOrElse
          iae("update_edgeComplete", s"Can't update edge `${Ns(edgeNs)}` of base entity `${Ns(baseNs)}` without knowing which target entity the edge is pointing too. " +
            s"Please update the edge itself, like `${Ns(edgeNs)}(<edgeId>).edgeProperty(<new value>).update`.")
      }
    }
    missingTarget(model.elements)
  }
}
