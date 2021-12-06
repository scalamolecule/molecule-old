package molecule.core.macros

import molecule.core.ast.elements._
import molecule.core.transform.Dsl2Model
import scala.reflect.macros.blackbox


private[molecule] trait MakeBase extends Dsl2Model {
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

        import molecule.core.marshalling.unpackAttr.String2cast._
        import molecule.core.marshalling.unpackAttr.String2json._
     """

  def mapIdents(idents: Seq[Any]): Seq[(String, Tree)] = idents.flatMap {
    case (key: String, v: String) if key.startsWith("__ident__") && v.startsWith("__ident__") =>
      Seq(
        ArrowAssoc(key) -> q"${TermName(key.substring(9))}",
        ArrowAssoc(v) -> q"${TermName(v.substring(9))}"
      )

    case (key: String, v: Any) if key.startsWith("__ident__") =>
      Seq(ArrowAssoc(key) -> q"${TermName(key.substring(9))}")

    case (key: Any, v: String) if v.startsWith("__ident__") =>
      Seq(ArrowAssoc(v) -> q"${TermName(v.substring(9))}")

    case ident: String if ident.startsWith("__ident__") =>
      Seq(ArrowAssoc(ident) -> q"${TermName(ident.substring(9))}")

    case set: Set[_] if set.nonEmpty => set.flatMap {
      case ident if ident.toString.startsWith("__ident__") =>
        Seq(ArrowAssoc(ident.toString) -> q"${TermName(ident.toString.substring(9))}")
      case _                                               => Nil
    }
    case _                           => Nil
  }

  def mapIdentifiers(elements: Seq[Element], identifiers0: Seq[(String, Tree)] = Nil): Seq[(String, Tree)] = {
    val newIdentifiers = (elements collect {
      case Atom(_, _, _, _, Eq(idents), _, _, keyIdents)              => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, Neq(idents), _, _, keyIdents)             => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, And(idents), _, _, keyIdents)             => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, Lt(ident), _, _, keyIdents)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Gt(ident), _, _, keyIdents)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Le(ident), _, _, keyIdents)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Ge(ident), _, _, keyIdents)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Fulltext(idents), _, _, keyIdents)        => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, AssertValue(idents), _, _, keyIdents)     => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, RetractValue(idents), _, _, keyIdents)    => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, ReplaceValue(idents), _, _, keyIdents)    => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, MapEq(idents), _, _, keyIdents)           => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, AssertMapPairs(idents), _, _, keyIdents)  => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, ReplaceMapPairs(idents), _, _, keyIdents) => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, RetractMapKeys(idents), _, _, keyIdents)  => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, MapKeys(idents), _, _, keyIdents)         => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, _, _, _, keyIdents)                       => mapIdents(keyIdents)
      case Generic(_, _, _, Eq(idents))                               => mapIdents(idents)
      case Generic(_, _, _, Neq(idents))                              => mapIdents(idents)
      case Generic(_, _, _, Lt(ident))                                => mapIdents(Seq(ident))
      case Generic(_, _, _, Gt(ident))                                => mapIdents(Seq(ident))
      case Generic(_, _, _, Le(ident))                                => mapIdents(Seq(ident))
      case Generic(_, _, _, Ge(ident))                                => mapIdents(Seq(ident))
      case Nested(_, nestedElements)                                  => mapIdentifiers(nestedElements, identifiers0)
      case Composite(compositeElements)                               => mapIdentifiers(compositeElements, identifiers0)
      case TxMetaData(txElements)                                     => mapIdentifiers(txElements, identifiers0)
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
