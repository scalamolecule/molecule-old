package molecule.core.macros

import molecule.core.ast.elements._
import molecule.core.transform.Dsl2Model
import scala.reflect.macros.blackbox


private[molecule] trait MakeBase extends Dsl2Model {
  val c: blackbox.Context

  import c.universe._

  private lazy val xy = InspectMacro("MakeBase", 1, 10, mkError = true)
  private lazy val xx = InspectMacro("MakeBase", 1, 10)

  def getImports(genericImports: List[Tree]) =
    q"""
      import java.lang.{Integer => jInteger, Long => jLong, Double => jDouble, Boolean => jBoolean}
      import java.math.{BigDecimal => jBigDec, BigInteger => jBigInt}
      import java.net.URI
      import java.util.{Collections, Date, UUID, List => jList, Map => jMap, Iterator => jIterator, Set => jSet}
      import molecule.core.ast.elements._
      import molecule.core.composition._
      import molecule.core.dsl.base.Init
      import molecule.core.exceptions.MoleculeException
      ..$genericImports
      import molecule.core.marshalling.MoleculeRpc
      import molecule.core.marshalling.ast.nodes
      import molecule.core.marshalling.ast.nodes._
      import molecule.core.marshalling.ast.SortCoordinate
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

    case (key: String, _: Any) if key.startsWith("__ident__") =>
      Seq(ArrowAssoc(key) -> q"${TermName(key.substring(9))}")

    case (_: Any, v: String) if v.startsWith("__ident__") =>
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
      case Atom(_, _, _, _, Eq(idents), _, _, keyIdents, _)              => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, Neq(idents), _, _, keyIdents, _)             => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, And(idents), _, _, keyIdents, _)             => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, Lt(ident), _, _, keyIdents, _)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Gt(ident), _, _, keyIdents, _)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Le(ident), _, _, keyIdents, _)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Ge(ident), _, _, keyIdents, _)               => mapIdents(ident +: keyIdents)
      case Atom(_, _, _, _, Fulltext(idents), _, _, keyIdents, _)        => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, AssertValue(idents), _, _, keyIdents, _)     => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, RetractValue(idents), _, _, keyIdents, _)    => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, ReplaceValue(idents), _, _, keyIdents, _)    => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, MapEq(idents), _, _, keyIdents, _)           => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, AssertMapPairs(idents), _, _, keyIdents, _)  => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, ReplaceMapPairs(idents), _, _, keyIdents, _) => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, RetractMapKeys(idents), _, _, keyIdents, _)  => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, MapKeys(idents), _, _, keyIdents, _)         => mapIdents(idents ++ keyIdents)
      case Atom(_, _, _, _, _, _, _, keyIdents, _)                       => mapIdents(keyIdents)
      case Generic(_, _, _, Eq(idents), _)                               => mapIdents(idents)
      case Generic(_, _, _, Neq(idents), _)                              => mapIdents(idents)
      case Generic(_, _, _, Lt(ident), _)                                => mapIdents(Seq(ident))
      case Generic(_, _, _, Gt(ident), _)                                => mapIdents(Seq(ident))
      case Generic(_, _, _, Le(ident), _)                                => mapIdents(Seq(ident))
      case Generic(_, _, _, Ge(ident), _)                                => mapIdents(Seq(ident))
      case Nested(_, nestedElements)                                     => mapIdentifiers(nestedElements, identifiers0)
      case Composite(compositeElements)                                  => mapIdentifiers(compositeElements, identifiers0)
      case TxMetaData(txElements)                                        => mapIdentifiers(txElements, identifiers0)
    }).flatten
    (identifiers0 ++ newIdentifiers).distinct
  }
}
