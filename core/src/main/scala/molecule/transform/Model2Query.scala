package molecule
package transform
import molecule.ast.model._
import molecule.ast.query._
import molecule.ops.QueryOps._
import molecule.util.Debug

object Model2Query {
  val x = Debug("Model2Query", 1, 60, false)

  def apply(model: Model): Query = {

    def resolve(q: Query, e: String, v: String, element: Element) = {
      val (v1, v2) = (v + 1, v + 2)

      element match {
        case a0@Atom(ns, attr0, tpe, card, value, enumPrefix) if attr0.last == '_' => {
          val a = a0.copy(name = attr0.init)
          val (isEnum, eP) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {

            // Input
            case (_, Qm) if isEnum      => q.in(v, a, enumPrefix, e).where(e, a, v)
            case (_, Qm)                => q.where(e, a, v).in(v, a)
            case (_, Lt(Qm))            => q.where(e, a, v).compareTo("<", a, v, Var(v1, a.tpeS))
            case (2, Fulltext(Seq(Qm))) => q.in(v1, a).fulltext(e, a, v, Var(v1, a.tpeS))
            case (_, Fulltext(Seq(Qm))) => q.in(v1, a).fulltext(e, a, v, Var(v1, a.tpeS))

            // Output
            //            case (_, EntValue) => q.find(e, tpe)
            case (2, VarValue) => q.where(e, a, v)
            case (_, VarValue) => q.where(e, a, v)
            case (2, EnumVal)  => q.enum(e, a, v)
            case (_, EnumVal)  => q.enum(e, a, v)

            // Expressions
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(eP + _))
            case (_, Eq(ss)) if ss.size > 1           => q.orRules(e, a, ss)
            case (_, Eq(s :: Nil)) if isEnum          => q.where(e, a, Val(eP + s, a.tpeS))
            case (_, Eq(s :: Nil))                    => q.where(e, a, Val(s, a.tpeS))
            case (_, Lt(arg))                         => q.where(e, a, v).compareTo("<", a, v, Val(arg, a.tpeS))
            //            case (_, Fn("count")) if a.name == "eid"    => q
            case (_, Fn("count"))         => q.where(e, a, v).find("count", Seq(), e, "Int")
            case (2, Fulltext(qv :: Nil)) => q.fulltext(e, a, v, Val(qv, a.tpeS))
            case (_, Fulltext(qv :: Nil)) => q.fulltext(e, a, v, Val(qv, a.tpeS))
            case (_, Fulltext(qvs))       => q.orRules(v1, a, qvs).fulltext(e, a, v, Var(v1, a.tpeS))

            // Manipulate
            case (_, Replace(_)) => q
            case (_, Remove(_))  => q

            case (c, va) => sys.error(s"[Model2Query] Unresolved Atom_ with cardinality/value: $c / $va")
          }
        }

        case a0@Atom(ns, attr, tpe, card, value, enumPrefix) => {
          val a = a0
          val (isEnum, eP) = if (enumPrefix.isDefined) (true, enumPrefix.get) else (false, "")
          (card, value) match {

            // Input
            case (_, Qm) if isEnum => q.find(v2, tpe).in(v, a, enumPrefix).enum(e, a, v)
            //            case (_, Qm) if isEnum      => q.find(v2, tpe).in(v, a, enumPrefix, e).enum(e, a, v)
            case (_, Qm)                => q.find(v, tpe).in(v, a).where(e, a, v)
            case (_, Lt(Qm))            => q.find(v, tpe).in(v1, a).where(e, a, v).compareTo("<", a, v, Var(v1, a.tpeS))
            case (2, Fulltext(Seq(Qm))) => q.find("distinct", Seq(), v, tpe).in(v1, a).fulltext(e, a, v, Var(v1, a.tpeS))
            case (_, Fulltext(Seq(Qm))) => q.find(v, tpe).in(v1, a).fulltext(e, a, v, Var(v1, a.tpeS))

            // Output
            case (_, EntValue) => q.find(e, tpe)
            case (2, VarValue) => q.find("distinct", Seq(), v, tpe).where(e, a, v)
            case (_, VarValue) => q.find(v, tpe).where(e, a, v)
            //            case (_, BackValue(backNs)) => q.find(v, tpe).where(e, a.ns, a.name, a.tpeS, v, backNs)
            case (_, BackValue(backNs)) => q.find(e, tpe).where(v, a.ns, a.name, a.tpeS, e, backNs)
            case (2, EnumVal)           => q.find("distinct", Seq(), v2, tpe).enum(e, a, v)
            case (_, EnumVal)           => q.find(v2, tpe).enum(e, a, v)

            // Expressions
            case (_, Eq(ss)) if isEnum && ss.size > 1 => q.orRules(e, a, ss.map(eP + _))
            case (2, Eq(ss)) if ss.size > 1           => q.find("distinct", Seq(), v, tpe).orRules(e, a, ss).where(e, a, v)
            case (_, Eq(ss)) if ss.size > 1           => q.find(v, tpe).orRules(e, a, ss)
            case (_, Eq(s :: Nil)) if isEnum          => q.find(v2, tpe).where(e, a, Val(eP + s, a.tpeS)).enum(e, a, v) // todo: can we output a constant value instead?
            case (_, Eq(s :: Nil))                    => q.find(v, tpe).where(e, a, Val(s, a.tpeS)).where(e, a, v) // todo: can we output a constant value instead?
            case (_, Lt(arg))                         => q.find(v, tpe).where(e, a, v).compareTo("<", a, v, Val(arg, a.tpeS))
            //            case (_, Fn("count")) if a.name == "eid"  => q.find("count", Seq(), e, "Int")
            case (_, Fn("count"))         => q.find("count", Seq(), e, "Int") //.where(e, a, v)
            case (2, Fulltext(qv :: Nil)) => q.find("distinct", Seq(), v, tpe).fulltext(e, a, v, Val(qv, a.tpeS))
            case (_, Fulltext(qv :: Nil)) => q.find(v, tpe).fulltext(e, a, v, Val(qv, a.tpeS))
            case (_, Fulltext(qvs))       => q.find(v, tpe).fulltext(e, a, v, Var(v1, a.tpeS)).orRules(v1, a, qvs)


            // Manipulate
            case (_, Replace(_)) => q
            case (_, Remove(_))  => q

            case (c, va) => sys.error(s"[Model2Query] Unresolved Atom with cardinality/value: $c / $va")
          }
        }

        //        case Bond(ns, refAttr, refNs) if ns.head.isUpper => q.ref(v, ns, refAttr, e, refNs)
        case Bond(ns, refAttr, refNs) => q.ref(e, ns, refAttr, v, refNs)

        case Group(ref, elements) => q

        case Meta(ns, attr, kind, tpe, value) => value match {
          case EntValue => q.find(e, "Long")
          case _        => q
        }

        case unresolved => sys.error("[Model2Query] Unresolved model (we should never get here): " + unresolved)
      }
    }

    val query0 = new Query(Find(Seq()), With(Seq()), In(Seq()), Where(List()))
        model.elements.foldLeft((query0, "a", "b")) { case ((query, a, b), element) =>

          val ns = curNs(element)

          val (prevNs, prevAttr, prevRefNs) = if (query.wh.clauses.isEmpty)
            ("NoValueYet", "NoValueYet", "")
          else
          // Take info from last clause
            query.wh.clauses.reverse.collectFirst {
              case DataClause(_, _, KW(kwNS, attr, refNs), _, _)      => (kwNS, attr, refNs)
              case Funct("fulltext", Seq(_, KW(kwNS, attr, _), _), _) => (kwNS, attr, "")
            }.getOrElse(sys.error("[Model2Query] Missing NS and Attribute\nmodel: " + model + "\nquery: " + query.toString))

          val c = (b.toCharArray.head + 1).toChar.toString
          val (entity, value) = {
            if (prevNs == ns) {
              // Previous ns `community` == current ns `community`
              // ?a :community/name ?b                                        a   b
              // ?a :community/url ?c                                         a   c
              (a, c)
            } else if ((prevNs, prevAttr) ==("db", "ident")) {
              // Previous attr is an enumerated value
              // ?a :db/ident ?b                                              a   b
              // ?a :ns/attr ?c                                               a   c
              (a, c)
            } else if (prevAttr == ns) {
              // Previous attr `neighborhood` == current ns `neighborhood`
              // ?a :community/neighborhood ?b                                a   b
              // ?b :neighborhood/name ?c                                     b   c
              (b, c)
            } else if (prevRefNs == ns) {
              // Previous attr `lineItems` is a ref to ns `lineItem` == current ns `lineItem`
              // ?a :order/lineItems ?b                                       a   b
              // ?b :lineItem/product ?c                                      b   c
              (b, c)
            } else {
              // First clause
              // ?a :ns/attr ?b                                               a   b
              (a, b)
            }
          }
          // Add new clause(s) to query
          val updatedQuery = resolve(query, entity, value, element)
          // Take next
          (updatedQuery, entity, value)
        }._1 // Get Query

//    model.elements.foldLeft((query0, "a", "b", "", "", "")) { case ((query, a, b, prevNs, prevAttr, prevRefNs), element) =>
//      val c = (b.toCharArray.head + 1).toChar.toString
//      val (entity, value, curNs, curAttr, curRefNs) = element match {
//        case Bond(ns, refAttr, refNs)                      => x(1, a, b, ns, prevNs, prevAttr, prevRefNs); (b, c, ns, refAttr, refNs)
//        case Atom(ns, attr, _, _, _, _) if ns == prevNs    => x(2, a, b, ns, prevNs, prevAttr, prevRefNs); (a, c, ns, attr, "")
//        case Atom(ns, attr, _, _, _, _) if ns == prevAttr  => x(3, a, b, ns, prevNs, prevAttr, prevRefNs); (b, c, ns, attr, "")
//        case Atom(ns, attr, _, _, _, _) if ns == prevRefNs => x(4, a, b, ns, prevNs, prevAttr, prevRefNs); (b, c, ns, attr, "")
//        case Atom(ns, attr, _, _, _, _)                    => x(5, a, b, ns, prevNs, prevAttr, prevRefNs); (a, b, ns, attr, "")
//        case z                                             => x(33, a, b, z); (a, b, prevNs, prevAttr, prevRefNs)
//        //        case Group(Bond(ns, _, _), _)                   => (a, b, prevNs, prevAttr, prevRefNs)
//        //        case Atom(ns, _, _, _, EnumVal, _)              => (a, b, prevNs, prevAttr, prevRefNs)
//        //        case Meta(ns, _, _, _, _)                       => (a, b, prevNs, prevAttr, prevRefNs)
//        //          //        {
//        //          //          // Previous ns `community` == current ns `community`
//        //          //          // ?a :community/name ?b                                        a   b
//        //          //          // ?a :community/url ?c                                         a   c
//        //          //          (a, c)
//        //          //        } else
//        //          if ((prevNs, prevAttr) ==("db", "ident")) {
//        //            // Previous attr is an enumerated value
//        //            // ?a :db/ident ?b                                              a   b
//        //            // ?a :ns/attr ?c                                               a   c
//        //            (a, c)
//        //          } else if (prevAttr == ns) {
//        //            // Previous attr `neighborhood` == current ns `neighborhood`
//        //            // ?a :community/neighborhood ?b                                a   b
//        //            // ?b :neighborhood/name ?c                                     b   c
//        //            (b, c)
//        //          } else if (prevRefNs == ns) {
//        //            // Previous attr `lineItems` is a ref to ns `lineItem` == current ns `lineItem`
//        //            // ?a :order/lineItems ?b                                       a   b
//        //            // ?b :lineItem/product ?c                                      b   c
//        //            (b, c)
//        //          }
//      }
//      // Add new clause(s) to query
//      val updatedQuery = resolve(query, entity, value, element)
//      // Take next
//      (updatedQuery, entity, value, curNs, curAttr, curRefNs)
//    }._1 // Get Query
  }
}
