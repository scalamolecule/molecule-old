package molecule.core.macros

import molecule.core.ast.elements._
import molecule.core.transform.Dsl2Model
import scala.reflect.macros.blackbox


private[molecule] trait Base extends Dsl2Model {
  val c: blackbox.Context

  import c.universe._

  def getImports(genericImports: List[Tree]) =
    q"""
        import java.net.URI
        import java.util.{Collections, Date, UUID, List => jList, Map => jMap, Iterator => jIterator, Set => jSet}
        import molecule.core.ast.elements._
        import molecule.core.composition._
        import molecule.core.dsl.base.Init
        import molecule.core.exceptions.MoleculeException
        ..$genericImports
        import molecule.core.marshalling.MoleculeRpc
        import molecule.core.marshalling.nodes
        import molecule.core.marshalling.nodes._
        import molecule.core.ops.ModelOps._
        import molecule.datomic.base.ast.query._
        import molecule.datomic.base.transform.{Model2Query, QueryOptimizer, Query2String}
        import molecule.datomic.base.facade.Conn
        import scala.collection.mutable.ListBuffer
        import scala.concurrent.Future
     """

  def mapIdents(isJsPlatform: Boolean, tpe: String, idents: Seq[Any]): Seq[(String, Tree)] = idents.flatMap {
    case (key: String, v: String) if key.startsWith("__ident__") && v.startsWith("__ident__") =>
      Seq(
        ArrowAssoc(key) -> q"convert($isJsPlatform, $tpe, ${TermName(key.substring(9))})",
        ArrowAssoc(v) -> q"convert($isJsPlatform, $tpe, ${TermName(v.substring(9))})"
      )

    case (key: String, v: Any) if key.startsWith("__ident__") =>
      Seq(ArrowAssoc(key) -> q"convert($isJsPlatform, $tpe, ${TermName(key.substring(9))})")

    case (key: Any, v: String) if v.startsWith("__ident__") =>
      Seq(ArrowAssoc(v) -> q"convert($isJsPlatform, $tpe, ${TermName(v.substring(9))})")

    case ident: String if ident.startsWith("__ident__") =>
      Seq(ArrowAssoc(ident) -> q"convert($isJsPlatform, $tpe, ${TermName(ident.substring(9))})")

    case set: Set[_] if set.nonEmpty => set.flatMap {
      case ident if ident.toString.startsWith("__ident__") =>
        Seq(ArrowAssoc(ident.toString) -> q"convert($isJsPlatform, $tpe, ${TermName(ident.toString.substring(9))})")
      case value                                           => Nil
    }
    case other                       => Nil
  }

  def mapIdentifiers(
    isJsPlatform: Boolean,
    elements: Seq[Element],
    identifiers0: Seq[(String, Tree)] = Seq()
  ): Seq[(String, Tree)] = {
    val newIdentifiers = (elements collect {
      case Atom(_, _, tpe, _, Eq(idents), _, _, keyIdents)              => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, Neq(idents), _, _, keyIdents)             => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, And(idents), _, _, keyIdents)             => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, Lt(ident), _, _, keyIdents)               => mapIdents(isJsPlatform, tpe, ident +: keyIdents)
      case Atom(_, _, tpe, _, Gt(ident), _, _, keyIdents)               => mapIdents(isJsPlatform, tpe, ident +: keyIdents)
      case Atom(_, _, tpe, _, Le(ident), _, _, keyIdents)               => mapIdents(isJsPlatform, tpe, ident +: keyIdents)
      case Atom(_, _, tpe, _, Ge(ident), _, _, keyIdents)               => mapIdents(isJsPlatform, tpe, ident +: keyIdents)
      case Atom(_, _, tpe, _, Fulltext(idents), _, _, keyIdents)        => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, AssertValue(idents), _, _, keyIdents)     => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, RetractValue(idents), _, _, keyIdents)    => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, ReplaceValue(idents), _, _, keyIdents)    => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, MapEq(idents), _, _, keyIdents)           => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, AssertMapPairs(idents), _, _, keyIdents)  => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, ReplaceMapPairs(idents), _, _, keyIdents) => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, RetractMapKeys(idents), _, _, keyIdents)  => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, MapKeys(idents), _, _, keyIdents)         => mapIdents(isJsPlatform, tpe, idents ++ keyIdents)
      case Atom(_, _, tpe, _, _, _, _, keyIdents)                       => mapIdents(isJsPlatform, tpe, keyIdents)
      case Generic(_, _, tpe, Eq(idents))                               => mapIdents(isJsPlatform, tpe, idents)
      case Generic(_, _, tpe, Neq(idents))                              => mapIdents(isJsPlatform, tpe, idents)
      case Generic(_, _, tpe, Lt(ident))                                => mapIdents(isJsPlatform, tpe, Seq(ident))
      case Generic(_, _, tpe, Gt(ident))                                => mapIdents(isJsPlatform, tpe, Seq(ident))
      case Generic(_, _, tpe, Le(ident))                                => mapIdents(isJsPlatform, tpe, Seq(ident))
      case Generic(_, _, tpe, Ge(ident))                                => mapIdents(isJsPlatform, tpe, Seq(ident))
      case Nested(_, nestedElements)                                    => mapIdentifiers(isJsPlatform, nestedElements, identifiers0)
      case Composite(compositeElements)                                 => mapIdentifiers(isJsPlatform, compositeElements, identifiers0)
      case TxMetaData(txElements)                                       => mapIdentifiers(isJsPlatform, txElements, identifiers0)

    }).flatten
    (identifiers0 ++ newIdentifiers).distinct
  }

  def topLevelLookups(castss: List[List[Int => Tree]], lookups: List[Tree], offset: Int = 0): List[Tree] = {
    var i = -1 + offset
    castss.head.map { _ =>
      i += 1
      lookups(i)
    }
  }
}
