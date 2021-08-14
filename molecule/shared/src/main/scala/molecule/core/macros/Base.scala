package molecule.core.macros

import molecule.core.ast.elements._
import molecule.core.macros.qr.CastArrays
import molecule.core.macros.attrResolverTrees.{LambdaCastAggr, LambdaCastNestedOpt, LambdaCastTypes, LambdaJsonAggr, LambdaJsonNestedOpt, LambdaJsonTypes}
import molecule.core.marshalling.attrIndexes._
import molecule.core.ops.{Liftables, TreeOps}
import molecule.core.transform.Dsl2Model
import scala.collection.mutable.ListBuffer
import scala.reflect.macros.blackbox


private[molecule] trait Base extends Dsl2Model {
  val c: blackbox.Context

  import c.universe._


  def getImports(genericImports: List[Tree]) =
    q"""
        import java.net.URI
        import java.util.{Collections, Date, UUID, List=>jList, Map=>jMap, Iterator=>jIterator, Set=>jSet}
        import molecule.core.ast.elements._
        import molecule.core.composition._
        import molecule.core.dsl.base.Init
        import molecule.core.exceptions.MoleculeException
        ..$genericImports
        import molecule.core.macros.qr.TypedCastHelpers
        import molecule.core.marshalling.{MoleculeRpc, QueryResult}
        import molecule.core.marshalling.attrIndexes._
        import molecule.core.ops.ModelOps._
        import molecule.datomic.base.ast.query._
        import molecule.datomic.base.transform.{Model2Query, QueryOptimizer}
        import molecule.datomic.base.facade.Conn
        import scala.collection.mutable.ListBuffer
        import scala.concurrent.Future
     """

  def mapIdents(idents: Seq[Any]): Seq[(String, Tree)] = idents.flatMap {
    case (key: String, v: String) if key.startsWith("__ident__") && v.startsWith("__ident__") => Seq(ArrowAssoc(key) -> q"convert(${TermName(key.substring(9))})", ArrowAssoc(v) -> q"convert(${TermName(v.substring(9))})")
    case (key: String, v: Any) if key.startsWith("__ident__")                                 => Seq(ArrowAssoc(key) -> q"convert(${TermName(key.substring(9))})")
    case (key: Any, v: String) if v.startsWith("__ident__")                                   => Seq(ArrowAssoc(v) -> q"convert(${TermName(v.substring(9))})")
    case ident: String if ident.startsWith("__ident__")                                       => Seq(ArrowAssoc(ident) -> q"convert(${TermName(ident.substring(9))})")
    case set: Set[_] if set.nonEmpty                                                          => set.flatMap {
      case ident if ident.toString.startsWith("__ident__") => Seq(ArrowAssoc(ident.toString) -> q"convert(${TermName(ident.toString.substring(9))})")
      case value                                           => Nil
    }
    case other                                                                                => Nil
  }

  def mapIdentifiers(elements: Seq[Element], identifiers0: Seq[(String, Tree)] = Seq()): Seq[(String, Tree)] = {
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


  def resolveIndexes(
    indexes: Indexes,
    nestedLevels: Int
  ): (List[c.universe.Tree], List[c.universe.Tree]) = {
    var colIndex = -1
    var arrays   = List.empty[c.universe.Tree]
    var lookups  = List.empty[c.universe.Tree]

    def recurse(indexes: Indexes): Unit = {
      // Nested eid indexes
      (0 until nestedLevels).toList.foreach { i =>
        colIndex += 1
        arrays = arrays :+ dataArrays(2)(colIndex, colIndex)
        lookups = lookups :+ q"${TermName("a" + colIndex)}(i)"
      }
      // Data
      indexes.attrs.foreach {
        case AttrIndex(_, castIndex, arrayType, arrayIndex, _) =>
          colIndex += 1
          arrays = arrays :+ dataArrays(arrayType)(colIndex, arrayIndex)
          val lookup = if (castIndex == 11)
            q"castV(${TermName("a" + colIndex)}(i))"
          else
            q"${TermName("a" + colIndex)}(i)"
          lookups = lookups :+ lookup

        case ii: Indexes => recurse(ii)
      }
    }
    recurse(indexes)
    (arrays, lookups)
  }


  //  def compositeCasts(castss: List[List[Int => Tree]], offset: Int = 0): Seq[Tree] = {
  //    var i              = -1 + offset
  //    var subTupleFields = Seq.empty[Tree]
  //    val subTuples      = castss.flatMap {
  //      case Nil   => None
  //      case casts =>
  //        subTupleFields = casts.map { c =>
  //          i += 1
  //          c(i)
  //        }
  //        Some(q"(..$subTupleFields)")
  //    }
  //    subTuples
  //  }

  def compositeLookups(castss: List[List[Int => Tree]], lookups: List[Tree], offset: Int = 0): Seq[Tree] = {
    var i              = -1 + offset
    var subTupleFields = Seq.empty[Tree]
    val subTuples      = castss.flatMap {
      case Nil   => None
      case casts =>
        subTupleFields = casts.map { _ =>
          i += 1
          lookups(i)
        }
        Some(q"(..$subTupleFields)")
    }
    subTuples
  }

  //  def compositeJsons(jsonss: List[List[(Int, Int) => Tree]]): ListBuffer[Tree] = {
  //    var fieldIndex = -1
  //    var firstGroup = true
  //    var firstPair  = true
  //    val buf        = new ListBuffer[Tree]
  //    jsonss.foreach { jsonLambdas =>
  //      if (firstGroup) firstGroup = false else buf.append(q"""sb.append(", ")""")
  //      buf.append(q"""sb.append("{")""")
  //      firstPair = true
  //      jsonLambdas.foreach { jsonLambda =>
  //        fieldIndex += 1
  //        if (firstPair) firstPair = false else buf.append(q"""sb.append(", ")""")
  //        buf.append(jsonLambda(fieldIndex, 0)) // level 0 ok?
  //      }
  //      buf.append(q"""sb.append("}")""")
  //    }
  //    buf
  //  }


  //  def topLevel(castss: List[List[Int => Tree]], offset: Int = 0): List[Tree] = {
  //    var i = -1 + offset
  //    castss.head.map { cast =>
  //      i += 1
  //      cast(i)
  //    }
  //  }

  def topLevelLookups(castss: List[List[Int => Tree]], lookups: List[Tree], offset: Int = 0): List[Tree] = {
    var i = -1 + offset
    castss.head.map { _ =>
      i += 1
      lookups(i)
    }
  }
}
