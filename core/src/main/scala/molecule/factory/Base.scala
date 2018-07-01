package molecule.factory
import molecule.ast.model._
import molecule.boilerplate.NS
import molecule.ops.TreeOps
import molecule.transform._

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.whitebox.Context

private[molecule] trait Base[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = DebugMacro("Base", 1, 20, false)


  def mapIdents(idents: Seq[Any]): Seq[(String, Tree)] = idents.flatMap {
    case (key: String, value: String) if key.startsWith("__ident__") && value.startsWith("__ident__") => Seq(key -> q"convert(${TermName(key.substring(9))})", value -> q"convert(${TermName(value.substring(9))})")
    case (key: String, value: Any) if key.startsWith("__ident__")                                     => Seq(key -> q"convert(${TermName(key.substring(9))})")
    case (key: Any, value: String) if value.startsWith("__ident__")                                   => Seq(value -> q"convert(${TermName(value.substring(9))})")
    case ident: String if ident.startsWith("__ident__")                                               => Seq(ident -> q"convert(${TermName(ident.substring(9))})")
    case other                                                                                        => Nil
  }

  def mapGenerics(gs: Seq[Generic]): Seq[Any] = gs.flatMap {
    case TxTValue(Some(ident))        => Some(ident)
    case TxTValue_(Some(ident))       => Some(ident)
    case TxInstantValue(Some(ident))  => Some(ident)
    case TxInstantValue_(Some(ident)) => Some(ident)
    case OpValue(Some(ident))         => Some(ident)
    case OpValue_(Some(ident))        => Some(ident)
    case _                            => None
  }

  def mapIdentifiers(elements: Seq[Element], identifiers0: Seq[(String, Tree)] = Seq()): Seq[(String, Tree)] = {
    val newIdentifiers = (elements collect {
      case Atom(_, _, _, _, Eq(idents), _, gs, keyIdents)         => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, Neq(idents), _, gs, keyIdents)        => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, And(idents), _, gs, keyIdents)        => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, Lt(ident), _, gs, keyIdents)          => mapIdents(ident +: (mapGenerics(gs) ++ keyIdents))
      case Atom(_, _, _, _, Gt(ident), _, gs, keyIdents)          => mapIdents(ident +: (mapGenerics(gs) ++ keyIdents))
      case Atom(_, _, _, _, Le(ident), _, gs, keyIdents)          => mapIdents(ident +: (mapGenerics(gs) ++ keyIdents))
      case Atom(_, _, _, _, Ge(ident), _, gs, keyIdents)          => mapIdents(ident +: (mapGenerics(gs) ++ keyIdents))
      case Atom(_, _, _, _, Add_(idents), _, gs, keyIdents)       => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, Remove(idents), _, gs, keyIdents)     => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, Replace(idents), _, gs, keyIdents)    => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, MapEq(idents), _, gs, keyIdents)      => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, MapAdd(idents), _, gs, keyIdents)     => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, MapReplace(idents), _, gs, keyIdents) => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, MapRemove(idents), _, gs, keyIdents)  => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, MapKeys(idents), _, gs, keyIdents)    => mapIdents(idents ++ mapGenerics(gs) ++ keyIdents)
      case Atom(_, _, _, _, _, _, gs, keyIdents)                  => mapIdents(mapGenerics(gs) ++ keyIdents)
      case Meta(_, _, _, _, Eq(idents))                           => mapIdents(idents)
      case Meta(_, _, _, Id(eid), _)                              => mapIdents(Seq(eid))
      case Nested(_, nestedElements)                              => mapIdentifiers(nestedElements, identifiers0)
      case Composite(compositeElements)                           => mapIdentifiers(compositeElements, identifiers0)
      case TxMetaData(txElements)                                 => mapIdentifiers(txElements, identifiers0)
      case TxMetaData_(txElements)                                => mapIdentifiers(txElements, identifiers0)
    }).flatten
    (identifiers0 ++ newIdentifiers).distinct
  }

  def makeModelE(model: Model): Model = {

    def recurse(elements: Seq[Element], newGroup0: Boolean): Seq[Element] = elements.foldLeft(false, false, newGroup0, Seq.empty[Element]) {
      case ((_, _, _, acc), nested: Nested)                      => (false, false, false, acc :+ Nested(nested.bond, Meta("", "", "e", NoValue, IndexVal) +: recurse(nested.elements, false)))
      case ((prevAttr, _, newGroup, acc), b@Bond(_, _, _, 2, _)) => (prevAttr, true, newGroup, acc :+ b)
      case ((true, true, true, acc), a: Atom)                    => (false, false, false, acc :+ Meta("", "many-ref", "e", NoValue, IndexVal) :+ a)
      case ((_, manyRefAttr, newGroup, acc), a: Atom)            => (true, manyRefAttr, newGroup, acc :+ a)
      case ((_, manyRefAttr, newGroup, acc), e)                  => (false, manyRefAttr, newGroup, acc :+ e)
    }._4

    val firstMeta = model.elements.head match {
      case Meta(_, _, "e", NoValue, Eq(List(eid))) => Meta("", "", "e", Id(eid), IndexVal)
      case Bond(ns, refAttr, refNs, _, _)          => Meta("", "", "r", NoValue, IndexVal)
      case _                                       => Meta("", "", "e", NoValue, IndexVal)
    }
    Model(firstMeta +: recurse(model.elements, true))
  }

  val imports =
    q"""
        import molecule.action._
        import molecule.action.MoleculeOut._
        import molecule.composition.input.InputMolecule_1._
        import molecule.composition.input.InputMolecule_2._
        import molecule.composition.input.InputMolecule_3._
        import molecule.ast.model._
        import molecule.ast.query._
        import molecule.facade.Conn
        import molecule.ops.QueryOps._
        import molecule.transform.{Model2Query, Model2Transaction, Query2String}
        import java.lang.{Long => jLong, Double => jDouble, Float => jFloat, Boolean => jBoolean}
        import java.util.{Date, UUID, Map => jMap, List => jList, Collection => jCollection, Iterator => jIterator}
        import java.net.URI
        import java.math.{BigInteger => jBigInt, BigDecimal => jBigDec}
        import clojure.lang.{PersistentHashSet, PersistentVector, LazySeq, Keyword}
        import scala.collection.JavaConverters._
     """

  def valueResolver(identMap: Map[String, Tree]) =
    q"""
      private def convert(v: Any): Any = v match {
        case seq: Seq[_]   => seq map convert
        case m: Map[_, _]  => m.toSeq map convert
        case (k, v)        => (convert(k), convert(v))
        case Some(v)       => convert(v)
        case f: Float      => f.toDouble
        case unchanged     => unchanged
      }

      private def flatSeq(a: Any): Seq[Any] = (a match {
        case seq: Seq[_] => seq
        case set: Set[_] => set.toSeq
        case v           => Seq(v)
      }) map convert

      private def getValues(idents: Seq[Any]) = idents.flatMap {
        case v: String               if v.startsWith("__ident__")                              => flatSeq($identMap.get(v).get)
        case (k: String, "__pair__") if k.startsWith("__ident__")                              => flatSeq($identMap.get(k).get)
        case (k: String, v: String)  if k.startsWith("__ident__") && v.startsWith("__ident__") => Seq(($identMap.get(k).get, $identMap.get(v).get))
        case (k: String, v: Any)     if k.startsWith("__ident__")                              => Seq(($identMap.get(k).get, convert(v)))
        case (k: Any, v: String)     if v.startsWith("__ident__")                              => Seq((convert(k), $identMap.get(v).get))
        case (k, v)                                                                            => Seq((convert(k), convert(v)))
        case seq: Seq[_]                                                                       => seq map convert
        case v                                                                                 => Seq(convert(v))
      }
     """

  def modelResolver(model: Model, modelE: Model, valueResolver: Tree) =
    q"""
      private object r {
        ..$valueResolver

        private def getKeys(keyIdents: Seq[String]): Seq[String] = getValues(keyIdents).flatMap {
          case keys: Seq[_] => keys
          case key          => Seq(key)
        }.asInstanceOf[Seq[String]]

        private def getGenerics(gs: Seq[Generic]): Seq[Generic] = gs map {
          case TxTValue(Some(ident))        => TxTValue(Some(getValues(Seq(ident)).head))
          case TxTValue_(Some(ident))       => TxTValue_(Some(getValues(Seq(ident)).head))
          case TxInstantValue(Some(ident))  => TxInstantValue(Some(getValues(Seq(ident)).head))
          case TxInstantValue_(Some(ident)) => TxInstantValue_(Some(getValues(Seq(ident)).head))
          case OpValue(Some(ident))         => OpValue(Some(getValues(Seq(ident)).head))
          case OpValue_(Some(ident))        => OpValue_(Some(getValues(Seq(ident)).head))
          case otherGeneric                 => otherGeneric
        }

        private def resolveIdentifiers(elements: Seq[Element]): Seq[Element] = elements map {
          case atom@Atom(_, _, _, _, MapEq(idents), _, gs2, keyIdents)      => idents match {
            case List((ident, "__pair__"))
              if ident.startsWith("__ident__") && getValues(Seq(ident)) == Seq(None) => atom.copy(value = Fn("not", None),                                           gs = getGenerics(gs2), keys = getKeys(keyIdents))
            case idents                                                              => atom.copy(value = MapEq(getValues(idents).asInstanceOf[Seq[(String, Any)]]), gs = getGenerics(gs2), keys = getKeys(keyIdents))
          }
          case atom@Atom(_, _, _, 2, Eq(idents), _, gs2, keyIdents)         => getValues(idents) match {
            case Seq(None) => atom.copy(value = Fn("not", None), gs = getGenerics(gs2))
            case values    => atom.copy(value = Eq(values)     , gs = getGenerics(gs2))
          }
          case atom@Atom(_, _, _, _, Eq(idents), _, gs2, keyIdents)         => getValues(idents) match {
            case Seq(None) => atom.copy(value = Fn("not", None), gs = getGenerics(gs2), keys = getKeys(keyIdents))
            case values    => atom.copy(value = Eq(values)     , gs = getGenerics(gs2), keys = getKeys(keyIdents))
          }
          case atom@Atom(_, _, _, _, Neq(idents), _, gs2, keyIdents)        => atom.copy(value = Neq(getValues(idents)),         gs = getGenerics(gs2), keys = getKeys(keyIdents))
          case atom@Atom(_, _, _, _, And(idents), _, gs2, keyIdents)        => atom.copy(value = And(getValues(idents)),         gs = getGenerics(gs2), keys = getKeys(keyIdents))
          case atom@Atom(_, _, _, _, Lt(ident), _, gs2, keyIdents)          => atom.copy(value = Lt(getValues(Seq(ident)).head), gs = getGenerics(gs2), keys = getKeys(keyIdents))
          case atom@Atom(_, _, _, _, Gt(ident), _, gs2, keyIdents)          => atom.copy(value = Gt(getValues(Seq(ident)).head), gs = getGenerics(gs2), keys = getKeys(keyIdents))
          case atom@Atom(_, _, _, _, Le(ident), _, gs2, keyIdents)          => atom.copy(value = Le(getValues(Seq(ident)).head), gs = getGenerics(gs2), keys = getKeys(keyIdents))
          case atom@Atom(_, _, _, _, Ge(ident), _, gs2, keyIdents)          => atom.copy(value = Ge(getValues(Seq(ident)).head), gs = getGenerics(gs2), keys = getKeys(keyIdents))
          case atom@Atom(_, _, _, _, Add_(idents), _, gs2, _)               => atom.copy(value = Add_(getValues(idents)),        gs = getGenerics(gs2))
          case atom@Atom(_, _, _, _, Remove(idents), _, gs2, _)             => atom.copy(value = Remove(getValues(idents)),      gs = getGenerics(gs2))
          case atom@Atom(_, _, _, _, Replace(oldNew), _, gs2, _)            => atom.copy(value = Replace(getValues(oldNew).asInstanceOf[Seq[(Any, Any)]]),       gs = getGenerics(gs2))
          case atom@Atom(_, _, _, _, MapAdd(idents), _, gs2, keyIdents)     => atom.copy(value = MapAdd(getValues(idents).asInstanceOf[Seq[(String, Any)]]),     gs = getGenerics(gs2), keys = getKeys(keyIdents))
          case atom@Atom(_, _, _, _, MapReplace(idents), _, gs2, keyIdents) => atom.copy(value = MapReplace(getValues(idents).asInstanceOf[Seq[(String, Any)]]), gs = getGenerics(gs2), keys = getKeys(keyIdents))
          case atom@Atom(_, _, _, _, MapRemove(idents), _, gs2, keyIdents)  => atom.copy(value = MapRemove(getValues(idents).map(_.toString)),                   gs = getGenerics(gs2), keys = getKeys(keyIdents))
          case atom@Atom(_, _, _, _, MapKeys(idents), _, gs2, _)            => atom.copy(value = MapKeys(getValues(idents).asInstanceOf[Seq[String]]),           gs = getGenerics(gs2))
          case atom@Atom(_, _, _, _, _, _, gs2, _)                          => atom.copy(gs = getGenerics(gs2))
          case meta@Meta(_, _, _, _, Eq(idents))                            => meta.copy(value = Eq(getValues(idents)))
          case meta@Meta(_, _, _, Id(eid), _)                               => meta.copy(generic = Id(getValues(Seq(eid)).head))
          case Nested(ns, nestedElements)                                   => Nested(ns, resolveIdentifiers(nestedElements))
          case Composite(compositeElements)                                 => Composite(resolveIdentifiers(compositeElements))
          case TxMetaData(txElements)                                       => TxMetaData(resolveIdentifiers(txElements))
          case TxMetaData_(txElements)                                      => TxMetaData_(resolveIdentifiers(txElements))
          case other                                                        => other
        }
        val model: Model = Model(resolveIdentifiers($model.elements))
        val query: Query = Model2Query(model)

        val modelE: Model = Model(resolveIdentifiers($modelE.elements))
        val queryE: Query = Model2Query(modelE)
      }
    """

  def basics(dsl: c.Expr[NS]) = {
    val model = Dsl2Model(c)(dsl)
    val modelE = makeModelE(model)
    val identMap = mapIdentifiers(model.elements).toMap
    val resolverTree = modelResolver(model, modelE, valueResolver(identMap))

    val tree =
      q"""
      ..$imports
      ..$resolverTree

      private trait Util { self: molecule.action.MoleculeBase =>
        import java.text.SimpleDateFormat

        private val m = _model
        private val q = _query

        def date(s: String): Date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(s)

        // Raw API
        //override def getRaw(implicit conn: Conn): jCollection[jList[AnyRef]] = conn.query(_model, _query)

        // Print transitions of a `get` call to console
        protected def getD_(implicit conn: Conn) {
          val rows = try {
            conn.query(m, q).asScala.take(500)
          } catch {
            case e: Throwable => sys.error(e.toString)
          }
          val ins = q.inputs
          println(
            "\n--------------------------------------------------------------------------\n" +
            ${show(dsl.tree)} + "\n\n" +
            m + "\n\n" +
            q + "\n\n" +
            q.datalog + "\n\n" +
            "RULES: " + (if (q.i.rules.isEmpty) "none\n\n" else q.i.rules.map(Query2String(q).p(_)).mkString("[\n ", "\n ", "\n]\n\n")) +
            "INPUTS: " + (if (ins.isEmpty) "none\n\n" else ins.zipWithIndex.map(r => (r._2 + 1) + "  " + r._1).mkString("\n", "\n", "\n\n")) +
            "OUTPUTS:\n" + rows.zipWithIndex.map(r => (r._2 + 1) + "  " + r._1).mkString("\n") + "\n(showing up to 500 rows...)" +
            "\n--------------------------------------------------------------------------\n"
          )
        }
      }
    """
    (model, tree)
  }
}
