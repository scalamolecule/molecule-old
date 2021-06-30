package molecule.core.macros

import molecule.core.ast.elements._
import molecule.core.macros.qr.CastArrays
import molecule.core.macros.lambdaTrees.{LambdaCastAggr, LambdaCastOptNested, LambdaCastTypes, LambdaJsonAggr, LambdaJsonOptNested, LambdaJsonTypes}
import molecule.core.ops.{Liftables, TreeOps}
import molecule.core.transform.Dsl2Model
import scala.collection.mutable.ListBuffer
import scala.reflect.macros.blackbox


private[molecule] trait Base extends Dsl2Model {
  val c: blackbox.Context

  import c.universe._

  val w = InspectMacro("Base", 1)

  def getImports(genericImports: List[Tree]) =
    q"""
        import java.net.URI
        import java.util.{Date, UUID}
        import molecule.core.ast.elements._
        import molecule.core.composition._
        import molecule.core.dsl.base.Init
        import molecule.core.exceptions.MoleculeException
        ..$genericImports
        import molecule.core.macros.qr.TypedCastHelpers
        import molecule.core.marshalling.{MoleculeRpc, QueryResult}
        import molecule.core.ops.ModelOps._
        import molecule.datomic.base.ast.query._
        import molecule.datomic.base.transform.{Model2Query, QueryOptimizer}
        import molecule.datomic.base.facade.Conn
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

  def compositeCasts(castss: List[List[Int => Tree]], offset: Int = 0): Seq[Tree] = {
    var i              = -1 + offset
    var subTupleFields = Seq.empty[Tree]
    val subTuples      = castss.flatMap {
      case Nil   => None
      case casts =>
        subTupleFields = casts.map { c =>
          i += 1
          c(i)
        }
        Some(q"(..$subTupleFields)")
    }
    subTuples
  }

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

  def compositeJsons(jsonss: List[List[(Int, Int) => Tree]]): ListBuffer[Tree] = {
    var fieldIndex = -1
    var firstGroup = true
    var firstPair  = true
    val buf        = new ListBuffer[Tree]
    jsonss.foreach { jsonLambdas =>
      if (firstGroup) firstGroup = false else buf.append(q"""sb.append(", ")""")
      buf.append(q"""sb.append("{")""")
      firstPair = true
      jsonLambdas.foreach { jsonLambda =>
        fieldIndex += 1
        if (firstPair) firstPair = false else buf.append(q"""sb.append(", ")""")
        buf.append(jsonLambda(fieldIndex, 0)) // level 0 ok?
      }
      buf.append(q"""sb.append("}")""")
    }
    buf
  }


  def topLevel(castss: List[List[Int => Tree]], offset: Int = 0): List[Tree] = {
    var i = -1 + offset
    castss.head.map { cast =>
      i += 1
      cast(i)
    }
  }

  def topLevelLookups(castss: List[List[Int => Tree]], lookups: List[Tree], offset: Int = 0): List[Tree] = {
    var i = -1 + offset
    castss.head.map { _ =>
      i += 1
      lookups(i)
    }
  }

  def topLevelJson(jsonss: List[List[Int => Tree]]): List[Tree] = {
    jsonss.head.zipWithIndex.flatMap {
      case (jsonLambda, 0) => Seq(jsonLambda(0))
      case (jsonLambda, i) => Seq(q"""sb.append(",\n        ")""", jsonLambda(i))
    }
  }


  case class resolveNestedTupleMethods(
    castss: List[List[Int => Tree]],
    typess: List[List[Tree]],
    OutTypes: Seq[Type],
    postTypes: List[Tree],
    postCasts: List[Int => Tree]
  ) {
    val levels = castss.size
    lazy val t1: Tree = tq"List[(..${if (levels == 2) typess(1) else typess(1) :+ t2})]"
    lazy val t2: Tree = tq"List[(..${if (levels == 3) typess(2) else typess(2) :+ t3})]"
    lazy val t3: Tree = tq"List[(..${if (levels == 4) typess(3) else typess(3) :+ t4})]"
    lazy val t4: Tree = tq"List[(..${if (levels == 5) typess(4) else typess(4) :+ t5})]"
    lazy val t5: Tree = tq"List[(..${if (levels == 6) typess(5) else typess(5) :+ t6})]"
    lazy val t6: Tree = tq"List[(..${if (levels == 7) typess(6) else typess(6) :+ t7})]"
    lazy val t7: Tree = tq"List[(..${typess(7)})]"

    var fieldIndex = levels - 1

    def castLevel(level: Int): List[Tree] = castss(level).map { castLambda =>
      fieldIndex += 1
      castLambda(fieldIndex)
    }

    def branch0until(subLevels: () => Tree) = if (postCasts.isEmpty) {
      q"""
         final override def castBranch0(row: java.util.List[AnyRef], subBranches: List[Any]): (..$OutTypes) = (..${castLevel(0)}, subBranches.asInstanceOf[$t1])
         ..${subLevels()}
       """
    } else {
      // Ensuring that post fields are last
      val pre        = castLevel(0)
      val subCastes  = subLevels()
      val postFields = postCasts.map { postCastLambda =>
        fieldIndex += 1
        postCastLambda(fieldIndex)
      }
      q"""
         final override def castBranch0(row: java.util.List[AnyRef], subBranches: List[Any]): (..$OutTypes) = (..$pre, subBranches.asInstanceOf[$t1], ..$postFields)
         ..$subCastes
       """
    }

    lazy val level1: () => Tree = () =>
      q"final override def castLeaf1(row: java.util.List[AnyRef]): Any = (..${castLevel(1)})"

    lazy val level2: () => Tree = () =>
      q"""
         final override def castBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
         final override def castLeaf2(row: java.util.List[AnyRef]): Any = (..${castLevel(2)})
        """

    lazy val level3: () => Tree = () =>
      q"""
         final override def castBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
         final override def castBranch2(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(2)}, subBranches.asInstanceOf[$t3])
         final override def castLeaf3(row: java.util.List[AnyRef]): Any = (..${castLevel(3)})
       """
    lazy val level4: () => Tree = () =>
      q"""
         final override def castBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
         final override def castBranch2(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(2)}, subBranches.asInstanceOf[$t3])
         final override def castBranch3(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(3)}, subBranches.asInstanceOf[$t4])
         final override def castLeaf4(row: java.util.List[AnyRef]): Any = (..${castLevel(4)})
       """
    lazy val level5: () => Tree = () =>
      q"""
         final override def castBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
         final override def castBranch2(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(2)}, subBranches.asInstanceOf[$t3])
         final override def castBranch3(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(3)}, subBranches.asInstanceOf[$t4])
         final override def castBranch4(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(4)}, subBranches.asInstanceOf[$t5])
         final override def castLeaf5(row: java.util.List[AnyRef]): Any = (..${castLevel(5)})
       """
    lazy val level6: () => Tree = () =>
      q"""
         final override def castBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
         final override def castBranch2(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(2)}, subBranches.asInstanceOf[$t3])
         final override def castBranch3(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(3)}, subBranches.asInstanceOf[$t4])
         final override def castBranch4(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(4)}, subBranches.asInstanceOf[$t5])
         final override def castBranch5(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(5)}, subBranches.asInstanceOf[$t6])
         final override def castLeaf6(row: java.util.List[AnyRef]): Any = (..${castLevel(6)})
       """
    lazy val level7: () => Tree = () =>
      q"""
         final override def castBranch1(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(1)}, subBranches.asInstanceOf[$t2])
         final override def castBranch2(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(2)}, subBranches.asInstanceOf[$t3])
         final override def castBranch3(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(3)}, subBranches.asInstanceOf[$t4])
         final override def castBranch4(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(4)}, subBranches.asInstanceOf[$t5])
         final override def castBranch5(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(5)}, subBranches.asInstanceOf[$t6])
         final override def castBranch6(row: java.util.List[AnyRef], subBranches: List[Any]): Any = (..${castLevel(6)}, subBranches.asInstanceOf[$t7])
         final override def castLeaf7(row: java.util.List[AnyRef]): Any = (..${castLevel(7)})
       """

    def get: Tree = levels match {
      case 2 => branch0until(level1)
      case 3 => branch0until(level2)
      case 4 => branch0until(level3)
      case 5 => branch0until(level4)
      case 6 => branch0until(level5)
      case 7 => branch0until(level6)
      case 8 => branch0until(level7)
    }
  }

  case class resolveNestedJsonMethods(
    jsonss: List[List[(Int, Int) => Tree]],
    nestedRef: List[String],
    postJsons: List[(Int, Int) => Tree]
  ) {
    var fieldIndex = jsonss.size - 1

    def branchPairs(level: Int): List[Tree] = {
      var first = true
      jsonss(level) match {
        case Nil         => List(q"sb")
        case jsonLambdas => jsonLambdas.flatMap { jsonLambda =>
          fieldIndex += 1
          if (first) {
            first = false
            List(jsonLambda(fieldIndex, level * 2))
          } else {
            List(
              q"""sb.append(","); sb.append(indent(${level * 2}))""",
              jsonLambda(fieldIndex, level)
            )
          }
        }
      }
    }
//    println(nestedRef)
    def branch0until(subLevels: () => Tree): Tree = if (postJsons.isEmpty) {
      q"""
         final override def jsonBranch0(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(0, sb, {..${branchPairs(0)}}, ${nestedRef.head}, leaf)
         ..${subLevels()}
       """
    } else {
      // Ensuring that post fields are last
      val pre        = branchPairs(0)
      val subJsons   = subLevels()
      val postFields = postJsons.flatMap { portJsonLambda =>
        fieldIndex += 1
        List(
          q"""sb.append(","); sb.append(indent(0))""",
          portJsonLambda(fieldIndex, 0)
        ) // todo: is level 0 correct here?
      }
      q"""
         final override def jsonBranch0(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(0, sb, {..$pre}, ${nestedRef.head}, leaf, {..$postFields})
         ..$subJsons
       """
    }

    lazy val level1: () => Tree = () =>
      q"""
         final override def jsonLeaf1(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = leaf(1, sb, {..${branchPairs(1)}})
       """
    lazy val level2: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(1, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonLeaf2(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = leaf(2, sb, {..${branchPairs(2)}})
       """
    lazy val level3: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(1, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonBranch2(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(2, sb, {..${branchPairs(2)}}, ${nestedRef(2)}, leaf)
         final override def jsonLeaf3(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = leaf(3, sb, {..${branchPairs(3)}})
       """
    lazy val level4: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(1, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonBranch2(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(2, sb, {..${branchPairs(2)}}, ${nestedRef(2)}, leaf)
         final override def jsonBranch3(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(3, sb, {..${branchPairs(3)}}, ${nestedRef(3)}, leaf)
         final override def jsonLeaf4(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = leaf(4, sb, {..${branchPairs(4)}})
       """
    lazy val level5: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(1, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonBranch2(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(2, sb, {..${branchPairs(2)}}, ${nestedRef(2)}, leaf)
         final override def jsonBranch3(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(3, sb, {..${branchPairs(3)}}, ${nestedRef(3)}, leaf)
         final override def jsonBranch4(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(4, sb, {..${branchPairs(4)}}, ${nestedRef(4)}, leaf)
         final override def jsonLeaf5(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = leaf(5, sb, {..${branchPairs(5)}})
       """
    lazy val level6: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(1, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonBranch2(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(2, sb, {..${branchPairs(2)}}, ${nestedRef(2)}, leaf)
         final override def jsonBranch3(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(3, sb, {..${branchPairs(3)}}, ${nestedRef(3)}, leaf)
         final override def jsonBranch4(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(4, sb, {..${branchPairs(4)}}, ${nestedRef(4)}, leaf)
         final override def jsonBranch5(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(5, sb, {..${branchPairs(5)}}, ${nestedRef(5)}, leaf)
         final override def jsonLeaf6(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = leaf(6, sb, {..${branchPairs(6)}})
       """
    lazy val level7: () => Tree = () =>
      q"""
         final override def jsonBranch1(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(1, sb, {..${branchPairs(1)}}, ${nestedRef(1)}, leaf)
         final override def jsonBranch2(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(2, sb, {..${branchPairs(2)}}, ${nestedRef(2)}, leaf)
         final override def jsonBranch3(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(3, sb, {..${branchPairs(3)}}, ${nestedRef(3)}, leaf)
         final override def jsonBranch4(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(4, sb, {..${branchPairs(4)}}, ${nestedRef(4)}, leaf)
         final override def jsonBranch5(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(5, sb, {..${branchPairs(5)}}, ${nestedRef(5)}, leaf)
         final override def jsonBranch6(sb: StringBuilder, row: java.util.List[AnyRef], leaf: StringBuilder): StringBuilder = branch(6, sb, {..${branchPairs(6)}}, ${nestedRef(6)}, leaf)
         final override def jsonLeaf7(sb: StringBuilder, row: java.util.List[AnyRef]): StringBuilder = leaf(7, sb, {..${branchPairs(7)}})
       """

    def get: Tree = jsonss.size match {
      case 2 => branch0until(level1)
      case 3 => branch0until(level2)
      case 4 => branch0until(level3)
      case 5 => branch0until(level4)
      case 6 => branch0until(level5)
      case 7 => branch0until(level6)
      case 8 => branch0until(level7)
    }
  }
}
