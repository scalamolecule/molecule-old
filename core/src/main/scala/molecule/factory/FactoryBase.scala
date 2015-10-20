package molecule.factory
import molecule.ast.model._
import molecule.dsl.schemaDSL._
import molecule.ops.TreeOps
import molecule.transform._
import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.whitebox.Context
import java.net.URI
import java.util.{Date, UUID}
import clojure.lang.Keyword

trait FactoryBase[Ctx <: Context] extends TreeOps[Ctx] {
  import c.universe._
  val x = DebugMacro("BuildMolecule", 1, 20, false)

  def basics(dsl: c.Expr[NS]) = {

    val model: Model = Dsl2Model(c)(dsl)

    val modelE: Model = {
      def recurse(e: Element): Element = e match {
        case g: Group => Group(g.ref, Meta("", "", "e", NoValue, IndexVal) +: (g.elements map recurse))
        case other    => other
      }
      Model(Meta("", "", "e", NoValue, IndexVal) +: (model.elements map recurse))
    }

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
      import java.util.{Date, UUID, Map => jMap}
      import java.net.URI
      import clojure.lang.{PersistentHashSet, PersistentVector, LazySeq, Keyword}

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

      val model: Model = Model(resolveIdentifiers($model.elements))
      val query: Query = Model2Query(model)

      lazy val modelE = Model(resolveIdentifiers($modelE.elements))
      lazy val queryE = Model2Query(modelE)

      def debugMolecule(conn: Connection, m: Model, q: Query, args: Seq[Any] = Seq()): Unit = {
        val rows = try {
          results(conn, m, q).take(500)
        } catch {
          case e: Throwable => sys.error(e.toString)
        }
        sys.error(
          "\n--------------------------------------------------------------------------\n" +
          ${show(dsl.tree)} + "\n\n" +
          m + "\n\n" +
          q + "\n\n" +
          q.datalog + "\n\n" +
          "RULES: " + (if (q.i.rules.isEmpty) "none\n\n" else q.i.rules.map(Query2String(q).p(_)).mkString("[\n ", "\n ", "\n]\n\n")) +
          "INPUTS: " + (if (args.isEmpty) "none\n\n" else args.zipWithIndex.map(r => (r._2 + 1) + "  " + r._1).mkString("\n", "\n", "\n\n")) +
          "OUTPUTS:\n" + rows.toList.zipWithIndex.map(r => (r._2 + 1) + "  " + r._1).mkString("\n") + "\n(showing up to 500 rows...)" +
          "\n--------------------------------------------------------------------------\n"
        )
      }
    """
  }

  def cast(query: Tree, row: Tree, tpe: Type, i: Int): Tree = {
    val value: Tree = q"$row.get($i)"
    tpe match {
      case t if t <:< typeOf[Set[Int]]            => q"$value.asInstanceOf[PersistentHashSet].toSeq.map(_.asInstanceOf[jLong].toInt).toSet.asInstanceOf[$t]"
      case t if t <:< typeOf[Set[Float]]          => q"$value.asInstanceOf[PersistentHashSet].toSeq.map(_.asInstanceOf[jDouble].toFloat).toSet.asInstanceOf[$t]"
      case t if t <:< typeOf[Set[_]]              => q"$value.asInstanceOf[PersistentHashSet].toSet.asInstanceOf[$t]"
      case t if t <:< typeOf[Vector[_]]           => q"$value.asInstanceOf[PersistentVector].toVector.asInstanceOf[$t]"
      case t if t <:< typeOf[Stream[_]]           => q"$value.asInstanceOf[LazySeq].toStream.asInstanceOf[$t]"
      case t if t <:< typeOf[Int]                 => q"if($value.isInstanceOf[jLong]) $value.asInstanceOf[jLong].toInt.asInstanceOf[$t] else $value.asInstanceOf[$t]"
      case t if t <:< typeOf[Float]               => q"if($value.isInstanceOf[jDouble]) $value.asInstanceOf[jDouble].toFloat.asInstanceOf[$t] else $value.asInstanceOf[$t]"
      case t if t <:< typeOf[Option[Int]]         => q"if($value == null) None else Some($value.asInstanceOf[jMap[String, Int]].toMap.values.head)"
      case t if t <:< typeOf[Option[Float]]       => q"if($value == null) None else Some($value.asInstanceOf[jMap[String, Float]].toMap.values.head)"
      case t if t <:< typeOf[Option[Double]]      => q"if($value == null) None else Some($value.asInstanceOf[jMap[String, Double]].toMap.values.head)"
      case t if t <:< typeOf[Option[Boolean]]     => q"if($value == null) None else Some($value.asInstanceOf[jMap[String, Boolean]].toMap.values.head)"
      case t if t <:< typeOf[Option[Date]]        => q"if($value == null) None else Some($value.asInstanceOf[jMap[String, Date]].toMap.values.head)"
      case t if t <:< typeOf[Option[UUID]]        => q"if($value == null) None else Some($value.asInstanceOf[jMap[String, UUID]].toMap.values.head)"
      case t if t <:< typeOf[Option[URI]]         => q"if($value == null) None else Some($value.asInstanceOf[jMap[String, URI]].toMap.values.head)"
      case t if t <:< typeOf[Option[Long]]        =>
        q"""
          if($value == null) {
            None
          } else if ($value.toString.contains("{:db/id")) {
            // {:ns/ref1 {:db/id 3}}
            Some($value.toString.substring($value.toString.lastIndexOf(" ")+1).init.init.toLong)
            // Or this?: Some(value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[jMap[String, Long]].toMap.values.head)
          } else {
            Some($value.asInstanceOf[jMap[String, Long]].toMap.values.head)
          }
        """
      case t if t <:< typeOf[Option[String]]      =>
        q"""
          if($value == null) {
            None
          } else if ($value.toString.contains(":db/ident")) {
            // {:ns/enum {:db/ident :ns.enum/enum1}}
            val value = $value.toString
            Some(value.substring(value.lastIndexOf("/")+1).init.init)
          } else {
            Some($value.asInstanceOf[jMap[String, String]].toMap.values.head)
          }
        """
      case t if t <:< typeOf[Option[Set[Double]]] => q"""if ($value == null) None else Some($value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[PersistentVector].toSet.asInstanceOf[Set[Double]])"""
      case t if t <:< typeOf[Option[Set[Date]]]   => q"""if ($value == null) None else Some($value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[PersistentVector].toSet.asInstanceOf[Set[Date]])"""
      case t if t <:< typeOf[Option[Set[UUID]]]   => q"""if ($value == null) None else Some($value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[PersistentVector].toSet.asInstanceOf[Set[UUID]])"""
      case t if t <:< typeOf[Option[Set[URI]]]    => q"""if ($value == null) None else Some($value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[PersistentVector].toSet.asInstanceOf[Set[URI]])"""
      case t if t <:< typeOf[Option[Set[Int]]]    =>
        q"""
          if ($value == null) None else {
            val values = $value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[PersistentVector].toSeq
            Some(values.map(_.asInstanceOf[jLong].toInt).toSet.asInstanceOf[Set[Int]])
          }
        """
      case t if t <:< typeOf[Option[Set[Float]]]  =>
        q"""
          if ($value == null) None else {
            val values = $value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[PersistentVector].toSeq
            Some(values.map(_.asInstanceOf[jDouble].toFloat).toSet.asInstanceOf[Set[Float]])
          }
        """
      case t if t <:< typeOf[Option[Set[Long]]]   =>
        q"""
          if ($value == null) {
            None
          } else if ($value.toString.contains("{:db/id")) {
            // {:ns/ref1 [{:db/id 3} {:db/id 4}]}
            val idMaps = $value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[PersistentVector].toSeq
            Some(idMaps.map(_.asInstanceOf[jMap[String, Long]].toMap.values.head).toSet.asInstanceOf[Set[Long]])
          } else {
            // {:ns/longs [3 4 5]}
            Some($value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[PersistentVector].toSet.asInstanceOf[Set[Long]])
          }
        """
      case t if t <:< typeOf[Option[Set[String]]] =>
        q"""
          if($value == null) {
            None
          } else if ($value.toString.contains(":db/ident")) {
            // {:ns/enums [{:db/ident :ns.enums/enum1} {:db/ident :ns.enums/enum2}]}
            val identMaps = $value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[PersistentVector].toSeq
            val enums = identMaps.map(_.asInstanceOf[jMap[String, Keyword]].toMap.values.head.getName)
            Some(enums.toSet.asInstanceOf[Set[String]])
          } else {
            Some($value.asInstanceOf[jMap[String, PersistentVector]].toMap.values.head.asInstanceOf[PersistentVector].toSet.asInstanceOf[Set[String]])
          }
        """
      case t                                      =>
        q"""
          $query.f.outputs($i) match {
            case AggrExpr("sum",_,_) =>
              ${t.toString} match {
                case "Int"   => if($value.isInstanceOf[jLong]) $value.asInstanceOf[jLong].toInt.asInstanceOf[$t] else $value.asInstanceOf[$t]
                case "Float" => if($value.isInstanceOf[jDouble]) $value.asInstanceOf[jDouble].toFloat.asInstanceOf[$t] else $value.asInstanceOf[$t]
                case _       => $value.asInstanceOf[$t]
              }

            case AggrExpr("median",_,_) =>
              ${t.toString} match {
                case "Int"   => if($value.isInstanceOf[jLong]) $value.asInstanceOf[jLong].toInt.asInstanceOf[$t] else $value.asInstanceOf[$t]
                case "Float" => if($value.isInstanceOf[jDouble]) $value.asInstanceOf[jDouble].toFloat.asInstanceOf[$t] else $value.asInstanceOf[$t]
                case _       => $value.asInstanceOf[$t]
              }

            case other =>
//               sys.error("Default value: " + value.toString)
              $value.asInstanceOf[$t]
          }
        """
    }
  }

  def castTpl(query: Tree, row: Tree, tpes: Seq[Type]) = tpes.zipWithIndex.map { case (tpe, i) => cast(query, row, tpe, i) }

  def castTpls(query: Tree, rows: Tree, tpes: Seq[Type]) = q"$rows.map(row => (..${castTpl(query, q"row", tpes)})).toList"


  def resolveNested(query: Tree, tpes: Seq[Type], tpl: Tree, prevRow: Tree, row: Tree, rowNo: Tree, entityIndex: Int): Tree = {

    val newNested = q"if ($prevRow.head == 0L) true else $row.apply($entityIndex).asInstanceOf[Long] != $prevRow.apply($entityIndex).asInstanceOf[Long]"

    def resolve(nestedTpes: Seq[Type], i: Int) = {
      q"""
        if ($tpl == null || $newNested) {
          val nestedTpl = ${resolveNested(query, nestedTpes, q"None: Option[(..$tpes)]", prevRow, row, rowNo, entityIndex + 1 + i)}
          Seq(nestedTpl)

        } else if ($tpl.get.isInstanceOf[Seq[_]]) {

          lazy val nestedTpl = ${
            resolveNested(query, nestedTpes,
              q"Some($tpl.get.asInstanceOf[Seq[(..$tpes)]].last.asInstanceOf[(..$nestedTpes)])",
              prevRow, row, rowNo, entityIndex + 1 + i)
          }.asInstanceOf[(..$nestedTpes)]

          val newNested1 = $row.apply(${entityIndex + 1 + i}).asInstanceOf[Long] != $prevRow.apply(${entityIndex + 1 + i}).asInstanceOf[Long]

          if (newNested1)
            $tpl.get.asInstanceOf[Seq[(..$nestedTpes)]] :+ nestedTpl
          else
            $tpl.get.asInstanceOf[Seq[(..$nestedTpes)]].init :+ nestedTpl

        } else {

          lazy val nestedTpl = ${
            resolveNested(query, nestedTpes,
              q"Some($tpl.get.asInstanceOf[(..$tpes)].productElement($tpl.get.asInstanceOf[(..$tpes)].productArity - 1).asInstanceOf[Seq[(..$nestedTpes)]].last.asInstanceOf[(..$nestedTpes)])",
              prevRow, row, rowNo, entityIndex + 1 + i)
          }.asInstanceOf[(..$nestedTpes)]

          val newNested1 = $row.apply(${entityIndex + 1 + i}).asInstanceOf[Long] != $prevRow.apply(${entityIndex + 1 + i}).asInstanceOf[Long]

          if (newNested1)
            $tpl.get.asInstanceOf[(..$tpes)].productElement($tpl.get.asInstanceOf[(..$tpes)].productArity - 1).asInstanceOf[Seq[(..$nestedTpes)]] :+ nestedTpl
          else
            $tpl.get.asInstanceOf[(..$tpes)].productElement($tpl.get.asInstanceOf[(..$tpes)].productArity - 1).asInstanceOf[Seq[(..$nestedTpes)]].init :+ nestedTpl
        }
      """
      //            q"""
      ////      println("  E tpl: " + tpl.isInstanceOf[Product])
      //
      //              if ($tpl.isEmpty || $newNested) {
      //                val nestedTpl = ${resolveNested(query, nestedTpes, q"None: Option[(..$tpes)]", prevRow, row, rowNo, entityIndex + 1 + i)}
      //println("  E0 tpl: " + $tpl)
      ////      println("  E0 nestedTpl: " + nestedTpl)
      //
      //                Seq(nestedTpl)
      //
      //              } else if ($tpl.get.isInstanceOf[Seq[_]]) {
      //println("  E1 tpl: " + $tpl)
      //
      //                val nestedTpl = ${resolveNested(query, nestedTpes,
      //                  q"Some($tpl.get.asInstanceOf[Seq[(..$nestedTpes)]].last.asInstanceOf[(..$nestedTpes)])",
      //                  prevRow, row, rowNo, entityIndex + 1 + i)}.asInstanceOf[(..$nestedTpes)]
      ////      println("  E1 nestedTpl: " + nestedTpl)
      //
      //                val newNested1 = $row.apply(${entityIndex + 1 + i}).asInstanceOf[Long] != $prevRow.apply(${entityIndex + 1 + i}).asInstanceOf[Long]
      ////      println("  E1 newNested1: " + newNested1)
      //
      //                if (newNested1)
      //                  $tpl.get.asInstanceOf[Seq[(..$nestedTpes)]] :+ nestedTpl
      //                else
      //                  $tpl.get.asInstanceOf[Seq[(..$nestedTpes)]].init :+ nestedTpl
      //
      //
      //              } else {
      //println("  E2 tpl: " + $tpl)
      //
      //                val nestedTpl = ${
      //                   resolveNested(query, nestedTpes,
      //                     q"Some($tpl.get.asInstanceOf[(..$tpes)].productElement($tpl.get.asInstanceOf[(..$tpes)].productArity - 1).asInstanceOf[Seq[(..$nestedTpes)]].last.asInstanceOf[(..$nestedTpes)])",
      //                     prevRow, row, rowNo, entityIndex + 1 + i)
      //                 }.asInstanceOf[(..$nestedTpes)]
      //
      //                 val newNested1 = $row.apply(${entityIndex + 1 + i}).asInstanceOf[Long] != $prevRow.apply(${entityIndex + 1 + i}).asInstanceOf[Long]
      //
      //                 if (newNested1)
      //                   $tpl.get.asInstanceOf[(..$tpes)].productElement($tpl.get.asInstanceOf[(..$tpes)].productArity - 1).asInstanceOf[Seq[(..$nestedTpes)]] :+ nestedTpl
      //                 else
      //                   $tpl.get.asInstanceOf[(..$tpes)].productElement($tpl.get.asInstanceOf[(..$tpes)].productArity - 1).asInstanceOf[Seq[(..$nestedTpes)]].init :+ nestedTpl
      //              }
      //            """
      //      q"null.asInstanceOf[Seq[(..$nestedTpes)]]"
    }

    lazy val values = tpes.zipWithIndex.map {

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[(_, _)]] =>
        val nestedTpes = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head.baseType(weakTypeOf[(_, _)].typeSymbol).typeArgs
        resolve(nestedTpes, i)

      case (tpe, i) if tpe <:< weakTypeOf[Seq[_]] =>
        val nestedTpe = tpe.baseType(weakTypeOf[Seq[_]].typeSymbol).typeArgs.head
        q"""
          if ($tpl.isEmpty || $newNested) {
            val nestedTpl = ${resolveNested(query, Seq(nestedTpe), q"None: Option[(..$tpes)]", prevRow, row, rowNo, entityIndex + 1 + i)}
            Seq(nestedTpl)

          } else if ($tpl.get.isInstanceOf[Seq[_]]) {

            lazy val nestedTpl = ${
          resolveNested(query, Seq(nestedTpe),
            q"Some($tpl.get.asInstanceOf[Seq[$nestedTpe]].last.asInstanceOf[$nestedTpe])",
            prevRow, row, rowNo, entityIndex + 1 + i)
        }.asInstanceOf[$nestedTpe]

            val newNested1 = $row.apply(${entityIndex + 1 + i}).asInstanceOf[Long] != $prevRow.apply(${entityIndex + 1 + i}).asInstanceOf[Long]

            if (newNested1)
              $tpl.get.asInstanceOf[Seq[$nestedTpe]] :+ nestedTpl
            else
              $tpl.get.asInstanceOf[Seq[$nestedTpe]].init :+ nestedTpl

          } else {

            lazy val nestedTpl = ${
          resolveNested(query, Seq(nestedTpe),
            q"Some($tpl.get.asInstanceOf[(..$tpes)].productElement($tpl.get.asInstanceOf[(..$tpes)].productArity - 1).asInstanceOf[Seq[$nestedTpe]].last.asInstanceOf[$nestedTpe])",
            prevRow, row, rowNo, entityIndex + 1 + i)
        }.asInstanceOf[$nestedTpe]

            val newNested1 = $row.apply(${entityIndex + 1 + i}).asInstanceOf[Long] != $prevRow.apply(${entityIndex + 1 + i}).asInstanceOf[Long]

            if (newNested1)
              $tpl.get.asInstanceOf[(..$tpes)].productElement($tpl.get.asInstanceOf[(..$tpes)].productArity - 1).asInstanceOf[Seq[$nestedTpe]] :+ nestedTpl
            else
              $tpl.get.asInstanceOf[(..$tpes)].productElement($tpl.get.asInstanceOf[(..$tpes)].productArity - 1).asInstanceOf[Seq[$nestedTpe]].init :+ nestedTpl

          }
        """
      //              q"""
      //      println("  B tpl: " + $tpl)
      //
      //                val result = if ($tpl.isEmpty || $newNested) {
      //      println("  B0")
      //                  val nestedTpl = ${resolveNested(query, Seq(nestedTpe), q"None: Option[(..$tpes)]", prevRow, row, rowNo, entityIndex + 1 + i)}
      //      println("  B0 nestedTpl: " + nestedTpl)
      //                  Seq(nestedTpl)
      //
      //                } else if ($tpl.get.isInstanceOf[Seq[_]]) {
      //
      //                  lazy val nestedTpl = ${resolveNested(query, Seq(nestedTpe),
      //                    q"Some($tpl.get.asInstanceOf[Seq[$nestedTpe]].last.asInstanceOf[$nestedTpe])",
      //                    prevRow, row, rowNo, entityIndex + 1 + i)}.asInstanceOf[$nestedTpe]
      //      println("  B1 nestedTpl: " + nestedTpl)
      //
      //                  val newNested1 = $row.apply(${entityIndex + 1 + i}).asInstanceOf[Long] != $prevRow.apply(${entityIndex + 1 + i}).asInstanceOf[Long]
      //      println("  B1 newNested1: " + newNested1)
      //
      //                  if (newNested1)
      //                    $tpl.get.asInstanceOf[Seq[$nestedTpe]] :+ nestedTpl
      //                  else
      //                    $tpl.get.asInstanceOf[Seq[$nestedTpe]].init :+ nestedTpl
      //
      //
      //                } else {
      //
      //                  lazy val nestedTpl = ${resolveNested(query, Seq(nestedTpe),
      //                    q"Some($tpl.get.productElement($tpl.get.productArity - 1).asInstanceOf[Seq[$nestedTpe]].last.asInstanceOf[$nestedTpe])",
      //                    prevRow, row, rowNo, entityIndex + 1 + i)}.asInstanceOf[$nestedTpe]
      //      println("  B1 nestedTpl: " + nestedTpl)
      //
      //                  val newNested1 = $row.apply(${entityIndex + 1 + i}).asInstanceOf[Long] != $prevRow.apply(${entityIndex + 1 + i}).asInstanceOf[Long]
      //      println("  B1 newNested1: " + newNested1)
      //
      //                  if (newNested1)
      //                    $tpl.get.productElement($tpl.get.productArity - 1).asInstanceOf[Seq[$nestedTpe]] :+ nestedTpl
      //                  else
      //                    $tpl.get.productElement($tpl.get.productArity - 1).asInstanceOf[Seq[$nestedTpe]].init :+ nestedTpl
      //                }
      //
      //                println("Result B  " + $entityIndex + "  " + $i + " : " + result)
      //                result
      //              """
      //        q"null.asInstanceOf[Seq[$nestedTpe]]"


      case (tpe, i) => q"${cast(query, row, tpe, entityIndex + 1 + i)}"
      //        q"""
      //           println("  A " + $entityIndex + " tpes: " + ${tpes.toString} + " tpe: " + ${tpe.toString})
      //
      //           val result = ${cast(query, row, tpe, entityIndex + 1 + i)}
      //
      //           println("Result A " +  $entityIndex + "  " + $i + " : " + result)
      //           result
      //          """
    }
    q"(..$values).asInstanceOf[(..$tpes)]"
    //    q"""
    //      println("Types: " + ${tpes.toString})
    //
    //      val result = (..$values).asInstanceOf[(..$tpes)]
    //
    //      println("Result A  " + $entityIndex + "    : " + result)
    //      result
    //    """
  }


  def castNestedTpls(query: Tree, rows: Tree, tpes: Seq[Type]) = {
    q"""
        if ($rows.isEmpty) {
          Seq[(..$tpes)]()
        } else {
          val flatModel = {
            def recurse(element: Element): Seq[Element] = element match {
              case g: Group => g.elements flatMap recurse
              case a: Atom  => Seq(a)
              case m: Meta  => Seq(m)
              case other => Seq()
            }
            val elements = modelE.elements flatMap recurse
            if (elements.size != queryE.f.outputs.size)
              sys.error("[FactoryBase:castTplsNested]  Flattened model elements (" + elements.size + ") don't match query outputs (" + queryE.f.outputs.size + "):\n" +
                modelE + "\n----------------\n" + elements.mkString("\n") + "\n----------------\n" + queryE + "\n----------------\n")
            elements
          }

          val entityIndexes = flatModel.zipWithIndex.collect {
            case  (Meta(_, _, _, _, IndexVal), i) => i
          }

//          flatModel foreach println
//          println("---- " + entityIndexes)

          val sortedRows = entityIndexes match {
            case List(a)                               => $rows.sortBy(row => row(a).asInstanceOf[Long])
            case List(a, b)                            => $rows.sortBy(row => (row(a).asInstanceOf[Long], row(b).asInstanceOf[Long]))
            case List(a, b, c)                         => $rows.sortBy(row => (row(a).asInstanceOf[Long], row(b).asInstanceOf[Long], row(c).asInstanceOf[Long]))
            case List(a, b, c, d)                      => $rows.sortBy(row => (row(a).asInstanceOf[Long], row(b).asInstanceOf[Long], row(c).asInstanceOf[Long], row(d).asInstanceOf[Long]))
            case List(a, b, c, d, e)                   => $rows.sortBy(row => (row(a).asInstanceOf[Long], row(b).asInstanceOf[Long], row(c).asInstanceOf[Long], row(d).asInstanceOf[Long], row(e).asInstanceOf[Long]))
            case List(a, b, c, d, e, f)                => $rows.sortBy(row => (row(a).asInstanceOf[Long], row(b).asInstanceOf[Long], row(c).asInstanceOf[Long], row(d).asInstanceOf[Long], row(e).asInstanceOf[Long], row(f).asInstanceOf[Long]))
            case List(a, b, c, d, e, f, g)             => $rows.sortBy(row => (row(a).asInstanceOf[Long], row(b).asInstanceOf[Long], row(c).asInstanceOf[Long], row(d).asInstanceOf[Long], row(e).asInstanceOf[Long], row(f).asInstanceOf[Long], row(g).asInstanceOf[Long]))
            case List(a, b, c, d, e, f, g, h)          => $rows.sortBy(row => (row(a).asInstanceOf[Long], row(b).asInstanceOf[Long], row(c).asInstanceOf[Long], row(d).asInstanceOf[Long], row(e).asInstanceOf[Long], row(f).asInstanceOf[Long], row(g).asInstanceOf[Long], row(h).asInstanceOf[Long]))
            case List(a, b, c, d, e, f, g, h, i)       => $rows.sortBy(row => (row(a).asInstanceOf[Long], row(b).asInstanceOf[Long], row(c).asInstanceOf[Long], row(d).asInstanceOf[Long], row(e).asInstanceOf[Long], row(f).asInstanceOf[Long], row(g).asInstanceOf[Long], row(h).asInstanceOf[Long], row(i).asInstanceOf[Long]))
            case List(a, b, c, d, e, f, g, h, i, j)    => $rows.sortBy(row => (row(a).asInstanceOf[Long], row(b).asInstanceOf[Long], row(c).asInstanceOf[Long], row(d).asInstanceOf[Long], row(e).asInstanceOf[Long], row(f).asInstanceOf[Long], row(g).asInstanceOf[Long], row(h).asInstanceOf[Long], row(i).asInstanceOf[Long])).sortBy(row => row(j).asInstanceOf[Long])
            case List(a, b, c, d, e, f, g, h, i, j, k) => $rows.sortBy(row => (row(a).asInstanceOf[Long], row(b).asInstanceOf[Long], row(c).asInstanceOf[Long], row(d).asInstanceOf[Long], row(e).asInstanceOf[Long], row(f).asInstanceOf[Long], row(g).asInstanceOf[Long], row(h).asInstanceOf[Long], row(i).asInstanceOf[Long])).sortBy(row => (row(j).asInstanceOf[Long], row(k).asInstanceOf[Long]))
          }

//sortedRows foreach println

          val rowCount = sortedRows.length

          val casted = sortedRows.foldLeft((Seq[(..$tpes)](), None: Option[(..$tpes)], Seq(0L), Seq[Any](0L), 1)) { case ((accTpls0, tpl0, prevEntities, prevRow, r), row) =>
//println("--- " + r + " ---")
            val entities = entityIndexes.map(i => row(i).asInstanceOf[Long])

            val isLastRow = rowCount == r
            val newTpl = prevEntities.head != 0 && entities.head != prevEntities.head

            val tpl = ${resolveNested(query, tpes, q"tpl0", q"prevRow", q"row", q"r", 0)}

//println("TPL0         : " + tpl0)
//println("TPL          : " + tpl)
            val accTpls = if (isLastRow && newTpl) {
              // Add current tuple
//println("TPLS last/new: " + (accTpls0 ++ Seq(tpl0.get, tpl)).toString)
              accTpls0 ++ Seq(tpl0.get, tpl)
            } else if (isLastRow) {
              // Add current tuple
//println("TPLS last    : " + (accTpls0 :+ tpl).toString)
              accTpls0 :+ tpl
            } else if (newTpl) {
              // Add finished previous tuple
//println("TPLS next    : " + (accTpls0 :+ tpl0.get).toString)
              accTpls0 :+ tpl0.get
            } else {
              // Continue building current tuple
//println("TPLS cont    : " + accTpls0.toString)
              accTpls0
            }
            (accTpls, Some(tpl), entities, row, r + 1)
          }._1.toList

//println("Casted:\n" + casted.mkString("\n"))
          casted
        }
      """
  }

  // Todo: implement nestedTuples with HLists
  def castHListElem(query: Tree, data: Tree, tpe: Type, i: Int, hl: Tree) = q"$hl.::(${
    cast(query, data, tpe, i)
  })"

  def castHList(query: Tree, data: Tree, tpes: Seq[Type]) = tpes.zipWithIndex.foldRight(q"shapeless.HList()": Tree) {
    case ((tpe, i), hl) => castHListElem(query, data, tpe, i, hl)
  }

  def castHLists(query: Tree, data: Tree, ts: Seq[Type]) = q"$data.map(d => ${
    castHList(query, q"d", ts)
  })"
}
