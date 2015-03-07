package molecule.factory
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.TreeOps
import molecule.transform._
import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.whitebox.Context


trait FactoryBase[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
    val x = Debug("BuildMolecule", 1, 20, false)

  def basics(dsl: c.Expr[NS]) = {
    val model = Dsl2Model(c)(dsl)

    val p = dsl.tree.pos
    val dslTailCode = p.source.lineToString(p.line - 1).substring(p.column)
    val checkCorrectModel = dslTailCode match {
      // todo: lift into quasiquotes and check against resolved model
      case r".*[\.|\s]*add.*"    => "check add..."
      case r".*[\.|\s]*insert.*" => "check insert..."
      case r".*[\.|\s]*update.*" => "check update..."
      case _                     => "other..."
    }
//            x(1, dsl.tree, showRaw(dsl.tree), model, checkCorrectModel)
//        x(1, dsl.tree, model)

    def keyValues(idents: Seq[Any]) = idents.flatMap {
      case ident: String if ident.startsWith("__ident__") => Some(ident -> q"${TermName(ident.substring(9))}")
      case _                                              => None
    }

    def mapIdentifiers(elements: Seq[Element], identifiers0: Seq[(String, Tree)] = Seq()): Seq[(String, Tree)] = {
      val newIdentifiers = (elements collect {
        case atom@Atom(_, _, _, _, Eq(idents), _, _)     => keyValues(idents)
        case atom@Atom(_, _, _, _, Neq(idents), _, _)    => keyValues(idents)
        case atom@Atom(_, _, _, _, Lt(ident), _, _)      => keyValues(Seq(ident))
        case atom@Atom(_, _, _, _, Gt(ident), _, _)      => keyValues(Seq(ident))
        case atom@Atom(_, _, _, _, Le(ident), _, _)      => keyValues(Seq(ident))
        case atom@Atom(_, _, _, _, Ge(ident), _, _)      => keyValues(Seq(ident))
        case atom@Atom(_, _, _, _, Remove(idents), _, _) => keyValues(idents)
        case meta@Meta(_, _, _, _, Eq(idents))           => keyValues(idents)
        case Group(_, nestedElements)                    => mapIdentifiers(nestedElements, identifiers0)
        case TxModel(txElements)                         => mapIdentifiers(txElements, identifiers0)
      }).flatten
      (identifiers0 ++ newIdentifiers).distinct
    }
    val identMap = mapIdentifiers(model.elements).toMap

    q"""
      import molecule._
      import molecule.api._
      import molecule.ast.model._
      import molecule.ast.query._
      import molecule.ops.QueryOps._
      import molecule.transform.{Model2Query, Model2Transaction, Query2String}
      import scala.collection.JavaConversions._
      import scala.collection.JavaConverters._
      import datomic.Connection
      import shapeless._
      import java.lang.{Long => jLong, Double => jDouble}

      def getValues(idents: Seq[Any]) = idents.map {
        case ident: String if ident.startsWith("__ident__") => $identMap.get(ident).get
        case other                                          => other
      }

      def resolveIdentifiers(elements: Seq[Element]): Seq[Element] = elements map {
        case atom@Atom(_, _, _, _, Eq(idents), _, _)         => atom.copy(value = Eq(getValues(idents)))
        case atom@Atom(_, _, _, _, Neq(idents), _, _)        => atom.copy(value = Neq(getValues(idents)))
        case atom@Atom(_, _, _, _, Lt(ident), _, _)          => atom.copy(value = Lt(getValues(Seq(ident)).head))
        case atom@Atom(_, _, _, _, Gt(ident), _, _)          => atom.copy(value = Gt(getValues(Seq(ident)).head))
        case atom@Atom(_, _, _, _, Le(ident), _, _)          => atom.copy(value = Le(getValues(Seq(ident)).head))
        case atom@Atom(_, _, _, _, Ge(ident), _, _)          => atom.copy(value = Ge(getValues(Seq(ident)).head))
        case atom@Atom(_, _, _, _, Remove(Seq(ident)), _, _) => atom.copy(value = Remove(getValues(Seq(ident))))
        case meta@Meta(_, _, _, _, Eq(Seq(ident)))           => meta.copy(value = Eq(getValues(Seq(ident))))
        case Group(ns, nestedElements)                       => Group(ns, resolveIdentifiers(nestedElements))
        case TxModel(txElements)                             => TxModel(resolveIdentifiers(txElements))
        case other                                           => other
      }

      val model = Model(resolveIdentifiers($model.elements))
      val query = Model2Query(model)

      def debugMolecule(conn: Connection, q: Query, args: Seq[Any] = Seq()): Unit = {
        val rows = try {
          results(q, conn)
        } catch {
          case e: Throwable => sys.error(e.toString)
        }
        sys.error(
          "\n--------------------------------------------------------------------------\n" +
          ${show(dsl.tree)} + "\n\n" +
          model + "\n\n" +
          q + "\n\n" +
          q.datalog + "\n\n" +
          "RULES: " + (if (q.i.rules.isEmpty) "none\n\n" else q.i.rules.map(Query2String(q).p(_)).mkString("[\n ", "\n ", "\n]\n\n")) +
          "INPUTS: " + (if (args.isEmpty) "none\n\n" else args.zipWithIndex.map(r => (r._2 + 1) + "  " + r._1).mkString("\n", "\n", "\n\n")) +
          "OUTPUTS:\n" + rows.toList.zipWithIndex.map(r => (r._2 + 1) + "  " + r._1).mkString("\n") +
          "\n--------------------------------------------------------------------------\n"
        )
      }
    """
  }

  def castTpl(data: Tree, tpe: Type, i: Int) = tpe match {
    case t if t <:< typeOf[Set[Int]]   => q"$data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSeq.map(_.asInstanceOf[jLong].toInt).toSet.asInstanceOf[$t]"
    case t if t <:< typeOf[Set[Float]] => q"$data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSeq.map(_.asInstanceOf[jDouble].toFloat).toSet.asInstanceOf[$t]"
    case t if t <:< typeOf[Set[_]]     => q"$data.get($i).asInstanceOf[clojure.lang.PersistentHashSet].toSet.asInstanceOf[$t]"
    case t if t <:< typeOf[Vector[_]]  => q"$data.get($i).asInstanceOf[clojure.lang.PersistentVector].toVector.asInstanceOf[$t]"
    case t if t <:< typeOf[Stream[_]]  => q"$data.get($i).asInstanceOf[clojure.lang.LazySeq].toStream.asInstanceOf[$t]"
    case t if t <:< typeOf[Int]        =>
      q""" if($data.get($i).isInstanceOf[jLong]) $data.get($i).asInstanceOf[jLong].toInt.asInstanceOf[$t] else $data.get($i).asInstanceOf[$t] """
    case t if t <:< typeOf[Float]      =>
      q""" if($data.get($i).isInstanceOf[jDouble]) $data.get($i).asInstanceOf[jDouble].toFloat.asInstanceOf[$t] else $data.get($i).asInstanceOf[$t] """
    case t                             =>
      q"""
       query.f.outputs($i) match {
         case AggrExpr("sum",_,_) =>
           ${t.toString} match {
             case "Int"   => if($data.get($i).isInstanceOf[jLong]) $data.get($i).asInstanceOf[jLong].toInt.asInstanceOf[$t] else $data.get($i).asInstanceOf[$t]
             case "Float" => if($data.get($i).isInstanceOf[jDouble]) $data.get($i).asInstanceOf[jDouble].toFloat.asInstanceOf[$t] else $data.get($i).asInstanceOf[$t]
             case _       => $data.get($i).asInstanceOf[$t]
           }

         case AggrExpr("median",_,_) =>
           ${t.toString} match {
             case "Int"   => if($data.get($i).isInstanceOf[jLong]) $data.get($i).asInstanceOf[jLong].toInt.asInstanceOf[$t] else $data.get($i).asInstanceOf[$t]
             case "Float" => if($data.get($i).isInstanceOf[jDouble]) $data.get($i).asInstanceOf[jDouble].toFloat.asInstanceOf[$t] else $data.get($i).asInstanceOf[$t]
             case _       => $data.get($i).asInstanceOf[$t]
           }

          case _ => $data.get($i).asInstanceOf[$t]
        }
       """
  }

  def castTpls(data: Tree, ts: Seq[Type]) = ts.zipWithIndex.map { case (tpe, i) => castTpl(data, tpe, i)}

  def castHList(data: Tree, tpe: Type, i: Int, hl: Tree) = q"$hl.::(${castTpl(data, tpe, i)})"
  def castHLists(data: Tree, tpes: Seq[Type]) = tpes.zipWithIndex.foldRight(q"shapeless.HList()": Tree) {
    case ((tpe, i), hl) => castHList(data, tpe, i, hl)
  }
}
