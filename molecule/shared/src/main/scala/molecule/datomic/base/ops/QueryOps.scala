package molecule.datomic.base.ops

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.ast.elements._
import molecule.datomic.base.ast.query._
import molecule.core.util.{Helpers, JavaUtil}
import molecule.datomic.base.ops.exception.QueryOpsException


/** Query operations */
object QueryOps extends Helpers with JavaUtil {

  def doubleEsc(arg: Any): Any = arg match {
    case s: String => s.replaceAll("\"", "\\\\\"")
    case other     => other
  }

  def esc1(arg: Any): String = escStr(arg.toString)

  def castStr(tpe: String): String = tpe match {
    case "Int" | "ref" => "Long"
    case "Date"        => "java.util.Date"
    case "UUID"        => "java.util.UUID"
    case "URI"         => "java.net.URI"
    case other         => other
  }

  def withDecimal(v: Any): String = {
    val s = v.toString
    if (s.contains(".")) s else s + ".0"
  }

  // Hack when we can't access Peer on client side
  // datomic.Peer.toTx(0) == 13194139533312L
  // datomic.Peer.toTx(1) == 13194139533313L
  // etc.
  val txBase = 13194139533312L

  implicit class QueryOps(q: Query) {

    // Find ..........................................

    def findD(v: String): Query =
      find("distinct", Seq(), v)

    def find(fn: String, args: Seq[Any], v: String): Query =
      find(AggrExpr(fn, args, Var(v)))

    def aggrV(a: Atom): Option[String] =
      q.wh.clauses.collectFirst {
        case DataClause(_, _, KW(ns, attr, _), Var(attrV), _, _)
          if a.nsFull == ns && a.attr == attr => attrV
      }

    def find(v: String): Query =
      find(Var(v))

    def finds(vs: String*): Query =
      q.copy(f = Find(q.f.outputs ++ vs.map(Var)))

    def find(o: Output): Query =
      q.copy(f = Find(q.f.outputs :+ o))


    // Pull ..........................................

    def pull(e: String, a: Atom): Query = {
      val pullScalar = e + "__" + q.f.outputs.length
      q.copy(f = Find(q.f.outputs :+ Pull(pullScalar, a.nsFull, a.attr)))
        .func("identity", Seq(Var(e)), ScalarBinding(Var(pullScalar)))
    }

    def pullEnum(e: String, a: Atom): Query = {
      val pullScalar = e + "__" + q.f.outputs.length
      q.copy(f = Find(q.f.outputs :+ Pull(pullScalar, a.nsFull, a.attr, a.enumPrefix)))
        .func("identity", Seq(Var(e)), ScalarBinding(Var(pullScalar)))
    }


    // In ..........................................

    def in(e: String, a: Atom, enumPrefix: Option[String], v: String): Query =
      q.copy(i = q.i.copy(inputs = q.i.inputs :+ Placeholder(Var(e), KW(a.nsFull, a.attr), Var(v), a.tpe, enumPrefix)))

    def in(v: String, tpe: String, nsFull: String, attr: String, e: String): Query =
      q.copy(i = q.i.copy(inputs = q.i.inputs :+ Placeholder(Var(e), KW(nsFull, attr), Var(v), tpe, None)))

    def in(tpe: String, vs: Seq[Any], v: String): Query =
      q.copy(i = q.i.copy(inputs = q.i.inputs :+ InVar(CollectionBinding(Var(v)), tpe, Seq(vs))))

    def historydb(db: String): Query =
      q.copy(i = q.i.copy(inputs = q.i.inputs ++ Seq(InVar(ImplDS, "", Nil), InVar(DS(db), "", Nil))))


    // With ...........................................

    def widh(v: String): Query = q.copy(wi = With((q.wi.variables :+ v).distinct))


    // Where ..........................................

    def where(
      e: String,
      nsFull: String,
      attr: String,
      qv: QueryValue,
      refNs: String,
      tpe: String
    ): Query = {
      val qv1 = qv match {
        case Val(arg: String) => Val(esc1(arg))
        case Val(arg)         => tpe match {
          case "Double"     => Val(double(arg))
          case "BigDecimal" => Val(bigDec(arg))
          case _            => Val(arg)
        }
        case _                => qv
      }
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e), KW(nsFull, attr, refNs), qv1, Empty, NoBinding)))
    }

    def where(e: String, a: String, v: String, tx: String, op: String): Query =
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e), Var(a), Var(v), Var(tx), Var(op))))

    def where(ds: DataSource, e: QueryValue, a: KW, v: String): Query =
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ds, e, a, Var(v), NoBinding)))

    def where(e: QueryValue, a: KW, v: String, txV: String = ""): Query = if (txV.nonEmpty)
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, e, a, Var(v), Var(txV))))
    else
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, e, a, Var(v), NoBinding)))

    def where(e: String, a: Atom, v: String): Query =
      where(e, a.nsFull, a.attr, Var(v), "", a.tpe)

    def where(e: String, a: Atom, qv: Val): Query =
      where(e, a.nsFull, a.attr, qv, "", a.tpe)

    def whereAnd[T](e: String, a: Atom, v: String, args: Seq[T], uriV: String = ""): Query =
      args.zipWithIndex.foldLeft(q) {
        case (q1, (arg, i)) if uriV.nonEmpty => q1.where(e, a, v + "_uri" + (i + 1))
          .func(s"""ground (java.net.URI. "$arg")""", Empty, v + "_uri" + (i + 1))
        case (q1, (arg, _))                  => q1.where(e, a, Val(arg))
      }.where(e, a, v)

    def whereAndEnum[T](e: String, a: Atom, v: String, prefix: String, args: Seq[T]): Query = {
      args.foldLeft(q) { case (q1, arg) => q1.where(e, a, Val("__enum__" + prefix + arg)) }.enumm(e, a, v)
    }


    // Null ..........................................

    def pre(a: Atom, arg: Any): Any = if (a.enumPrefix.isDefined)
      "__enum__" + a.enumPrefix.get + arg else arg

    def not(e: String, a: Atom): Query =
      q.copy(wh = Where(q.wh.clauses :+ NotClause(Var(e), KW(a.nsFull, a.attr))))

    def nots(e: String, a: Atom, v: String, argss: Seq[Any]): Query = {
      argss.zipWithIndex.foldLeft(q) {
        case (q1, (set: Set[_], _)) if a.tpe == "URI" =>
          val notClauses = set.toSeq.zipWithIndex.flatMap { case (uri, j) =>
            val x = Var(v + "_" + (j + 1))
            Seq(
              DataClause(ImplDS, Var(e), KW(a.nsFull, a.attr), x, Empty),
              FunctClause(s"""ground (java.net.URI. "${doubleEsc(uri)}")""", Nil, ScalarBinding(x))
            )
          }
          q1.copy(wh = Where(q1.wh.clauses :+ NotJoinClauses(Seq(Var(e)), notClauses)))
        case (q1, (set: Set[_], _))                   =>
          val notClauses = set.toSeq.map(arg =>
            DataClause(ImplDS, Var(e), KW(a.nsFull, a.attr), Val(pre(a, arg)), Empty)
          )
          q1.copy(wh = Where(q1.wh.clauses :+ NotClauses(notClauses)))
        case _                                        =>
          throw QueryOpsException(s"Expected Seq[Set[T]], got: " + argss)
      }
    }


    // Schema history attributes ..........................................

    def schemaHistory: Query = q.wh.clauses.collectFirst {
      case DataClause(_, _, KW("db.install", "attribute", _), _, _, _) => q
    }.getOrElse(
      q.finds("t", "tx", "txInstant", "op", "attrId", "a", "part", "nsFull",
        "ns", "attr", "schemaId", "schemaAttr", "schemaValue")
        .historydb("dbCurrent")
        .where(Var("_"), KW("db.install", "attribute"), "attrId")
        .where(DS("dbCurrent"), Var("attrId"), KW("db", "ident"), "attrIdent")
        .func("str", Seq(Var("attrIdent")), ScalarBinding(Var("a")))
        .func("namespace", Seq(Var("attrIdent")), ScalarBinding(Var("nsFull0")))
        .func("if",
          Seq(
            Funct(
              "=",
              Seq(Funct("subs", Seq(Var("nsFull0"), Val(0), Val(1)), Val("-"))),
              NoBinding
            ),
            Funct("subs", Seq(Var("nsFull0"), Val(1)), NoBinding),
            ScalarBinding(Var("nsFull0"))
          ),
          ScalarBinding(Var("nsFull"))
        )
        .func(".matches ^String",
          Seq(Var("nsFull"), Val(
            "^(db|db.alter|db.excise|db.install|db.part|db.sys|fressian" + // peer/client
              "|db.entity|db.attr)" // client
          )),
          ScalarBinding(Var("sys")))
        .func("=", Seq(Var("sys"), Val(false)))
        .func(".contains ^String", Seq(Var("nsFull"), Val("_")), ScalarBinding(Var("isPart")))
        .func(".split ^String", Seq(Var("nsFull"), Val("_")), ScalarBinding(Var("nsParts")))
        .func("first", Seq(Var("nsParts")), ScalarBinding(Var("part0")))
        .func("last", Seq(Var("nsParts")), ScalarBinding(Var("ns")))
        .func("if", Seq(Var("isPart"), Var("part0"), Val("db.part/user")), ScalarBinding(Var("part")))
        .func("name", Seq(Var("attrIdent")), ScalarBinding(Var("attr")))
        .where("attrId", "schemaId", "schemaValue", "tx", "op")
        .where(DS("dbCurrent"), Var("schemaId"), KW("db", "ident"), "schemaIdent")
        .func("name", Seq(Var("schemaIdent")), ScalarBinding(Var("schemaAttr")))

        // Hack to get t
        .func("-", Seq(Var("tx"), Val(txBase)), ScalarBinding(Var("t")))
        // Can't use datomic.api with Datomic Client systems
        //        .func("datomic.api/tx->t", Seq(Var("tx")), ScalarBinding(Var("t")))

        .where(Var("tx"), KW("db", "txInstant"), "txInstant")
    )


    // Schema attributes ..........................................

    def schema: Query = q.wh.clauses.collectFirst {
      case DataClause(_, _, KW("db.install", "attribute", _), _, _, _) => q
    }.getOrElse(
      q.where(Var("_"), KW("db.install", "attribute"), "attrId", "tx")
        .where(Var("attrId"), KW("db", "ident"), "attrIdent")
        .func("namespace", Seq(Var("attrIdent")), ScalarBinding(Var("nsFull")))
        .func(".matches ^String",
          Seq(Var("nsFull"), Val(
            "(db|db.alter|db.excise|db.install|db.part|db.sys|fressian" + // peer/client
              "|db.entity|db.attr" + // client
              "|-.*)" // '-' prefix to mark obsolete attributes
          )),
          ScalarBinding(Var("sys")))
        .func("=", Seq(Var("sys"), Val(false)))
    )

    def schemaResolved: Query = q.wh.clauses.collectFirst {
      case FunctClause("first", _, _) => q
    }.getOrElse(
      q.schema
        .func(".contains ^String", Seq(Var("nsFull"), Val("_")), ScalarBinding(Var("isPart")))
        .func(".split ^String", Seq(Var("nsFull"), Val("_")), ScalarBinding(Var("nsParts")))
        .func("first", Seq(Var("nsParts")), ScalarBinding(Var("part0")))
        .func("if", Seq(Var("isPart"), Var("part0"), Val("db.part/user")), ScalarBinding(Var("part")))
        .func("last", Seq(Var("nsParts")), ScalarBinding(Var("ns")))
    )

    def schemaA: Query = q.schema
      .func("str", Seq(Var("attrIdent")), ScalarBinding(Var("a")))

    def schemaAttr: Query = q.schema
      .func("name", Seq(Var("attrIdent")), ScalarBinding(Var("attr")))

    def schemaIdent: Query = q.schema
      .func("str", Seq(Var("attrIdent")), ScalarBinding(Var("ident")))

    def schemaTpe: Query = q.schema
      .where(Var("attrId"), KW("db", "valueType"), "tpeId")
      .where(Var("tpeId"), KW("db", "ident"), "tpeIdent")
      .func("name", Seq(Var("tpeIdent")), ScalarBinding(Var("valueType")))

    def schemaCard: Query = q.schema
      .where(Var("attrId"), KW("db", "cardinality"), "cardId")
      .where(Var("cardId"), KW("db", "ident"), "cardIdent")
      .func("name", Seq(Var("cardIdent")), ScalarBinding(Var("cardinality")))

    def schemaDoc: Query = q.schema
      .where(Var("attrId"), KW("db", "doc"), "doc")

    def schemaDocFulltext(arg: String): Query =
      q.func("fulltext", Seq(DS, KW("db", "doc"), Val(arg)), RelationBinding(Seq(Var("attrId"), Var("docValue"))))

    def schemaIndex: Query = q.schema
      .where(Var("attrId"), KW("db", "index"), "index")

    def schemaUnique: Query = q.schema
      .where(Var("attrId"), KW("db", "unique"), "uniqueId")
      .where(Var("uniqueId"), KW("db", "ident"), "uniqueIdent")
      .func("name", Seq(Var("uniqueIdent")), ScalarBinding(Var("unique")))

    def schemaFulltext: Query = q.schema
      .where(Var("attrId"), KW("db", "fulltext"), "fulltext")

    def schemaIsComponent: Query = q.schema
      .where(Var("attrId"), KW("db", "isComponent"), "isComponent")

    def schemaNoHistory: Query = q.schema
      .where(Var("attrId"), KW("db", "noHistory"), "noHistory")

    def schemaEnum: Query = {
      q.schemaResolved.schemaAttr
        .where(Var("_"), KW("db", "ident"), "enumIdent")
        .func("namespace", Seq(Var("enumIdent")), ScalarBinding(Var("enumNs")))
        .func("str", Seq(Var("nsFull"), Val("."), Var("attr")), ScalarBinding(Var("enumSubNs")))
        .func("=", Seq(Var("enumSubNs"), Var("enumNs")))
        .func("name", Seq(Var("enumIdent")), ScalarBinding(Var("enumm")))
    }

    def schemaT: Query = q.schema
      .func("-", Seq(Var("tx"), Val(txBase)), ScalarBinding(Var("t")))
    //      .func("datomic.api/tx->t", Seq(Var("tx")), ScalarBinding(Var("t")))

    def schemaTxInstant: Query = q.schema
      .where("tx", "db", "txInstant", Var("txInstant"), "", "")

    def schemaPullEnumValue(v: String): Query =
      q.copy(f = Find(q.f.outputs :+ Pull(v + "_pull", "db", v, Some(""))))
        .func("identity", Seq(Var("attrId")), ScalarBinding(Var(v + "_pull")))

    def schemaPull(v: String): Query =
      q.copy(f = Find(q.f.outputs :+ Pull(v + "_pull", "db", v)))
        .func("identity", Seq(Var("attrId")), ScalarBinding(Var(v + "_pull")))

    def schemaNot(attr: String): Query =
      q.copy(wh = Where(q.wh.clauses :+ NotClause(Var("attrId"), KW("db", attr))))


    // Datom attribute/values ..........................................

    def datomE(e: String, v: String, v1: String, singleElement: Boolean = false): Query = {
      if (singleElement) {
        q.where(e, "?", e + "_attr", Var(v), "", "")
          .ident(e + "_attr", v1)
          .func("namespace", Seq(Var(v1)), ScalarBinding(Var(v + "_ns")))
          .func("!=", Seq(Var(v + "_ns"), Val("db.install")))
          .func("!=", Seq(Var(v + "_ns"), Val("db")))
          .func("!=", Seq(Var(v + "_ns"), Val("fressian")))
      } else if (q.wh.clauses.isEmpty) {
        q
      } else {
        q.wh.clauses.reverse.collectFirst {
          case FunctClause("namespace", _, _)                                             => q
          case FunctClause("identity", _, _) /* Optional attributes */                    => q
          case DataClause(_, Var(e0), KW("db", "ident", _), _, _, _) if e0 == e + "_attr" => q
          case DataClause(_, _, KW("?", attr, _), _, _, _) if attr == e + "_attr"         => q.ident(e + "_attr", v1)
          case DataClause(_, Var(`e`), _, _, _, _)                                        => q
          case DataClause(_, Var("a"), _, _, _, _)                                        => q
        } getOrElse
          q.where(e, "?", e + "_attr", Var(v), "", "")
            .ident(e + "_attr", v1)
            .func("namespace", Seq(Var(v1)), ScalarBinding(Var(v + "_ns")))
            .func("!=", Seq(Var(v + "_ns"), Val("db.install")))
            .func("!=", Seq(Var(v + "_ns"), Val("db")))
            .func("!=", Seq(Var(v + "_ns"), Val("fressian")))
      }
    }

    def datomA(e: String, v: String, v1: String): Query = {
      q.wh.clauses.reverse.collectFirst {
        case FunctClause("str", Seq(Var(`v1`)), _)                                      =>
          q
        case DataClause(_, Var(e0), KW("db", "ident", _), _, _, _) if e0 == e + "_attr" =>
          q.func("str", Seq(Var(v1)), ScalarBinding(Var(v + "_a")))
            .func(".matches ^String", Seq(Var(v + "_a"), Val(":?-.*")), ScalarBinding(Var(v + "_a_test")))
            .func("=", Seq(Var(v + "_a_test"), Val(false)))

        case DataClause(_, _, KW("?", attr, _), _, _, _) if attr == e + "_attr" =>
          q.ident(e + "_attr", v1)
            .func("str", Seq(Var(v1)), ScalarBinding(Var(v + "_a")))
            .func(".matches ^String", Seq(Var(v + "_a"), Val(":?-.*")), ScalarBinding(Var(v + "_a_test")))
            .func("=", Seq(Var(v + "_a_test"), Val(false)))
        case DataClause(_, Var(`e`), KW(nsFull, attr, _), _, _, _)              =>
          q.where(KW(nsFull, attr), KW("db", "ident"), v1)
            .func("str", Seq(Var(v1)), ScalarBinding(Var(v + "_a")))
            .func(".matches ^String", Seq(Var(v + "_a"), Val(":?-.*")), ScalarBinding(Var(v + "_a_test")))
            .func("=", Seq(Var(v + "_a_test"), Val(false)))
      }.getOrElse(
        q.where(e, "?", e + "_attr", Var(v), "", "")
          .ident(e + "_attr", v1)
          .func("str", Seq(Var(v1)), ScalarBinding(Var(v + "_a")))
          .func(".matches ^String", Seq(Var(v + "_a"), Val(":?-.*")), ScalarBinding(Var(v + "_a_test")))
          .func("=", Seq(Var(v + "_a_test"), Val(false)))
          .func("namespace", Seq(Var(v1)), ScalarBinding(Var(v + "_ns")))
          .func("!=", Seq(Var(v + "_ns"), Val("db.install")))
          .func("!=", Seq(Var(v + "_ns"), Val("db")))
          .func("!=", Seq(Var(v + "_ns"), Val("fressian")))
      )
    }

    def datomV(e: String, v: String, v1: String): Query = {
      q.wh.clauses.reverse.collectFirst {
        case DataClause(_, Var(e0), KW("db", "ident", _), _, _, _) if e0 == e + "_attr" => q
        case DataClause(_, _, KW("?", attr, _), _, _, _) if attr == e + "_attr"         => q.ident(e + "_attr", v1)
        case DataClause(_, Var(`e`), KW(_, _, _), _, _, _)                              => q
          .func("identity", Seq(Var(v)), ScalarBinding(Var(v + "_v")))
      } getOrElse
        q.where(e, "?", e + "_attr", Var(v), "", "")
          .ident(e + "_attr", v1)
          .func("namespace", Seq(Var(v1)), ScalarBinding(Var(v + "_ns")))
          .func("!=", Seq(Var(v + "_ns"), Val("db.install")))
          .func("!=", Seq(Var(v + "_ns"), Val("db")))
          .func("!=", Seq(Var(v + "_ns"), Val("fressian")))
    }


    def compareToMany2[T](op: String, v: String, args: Seq[T]): Query = args.foldLeft(q) {
      case (q1, arg) => q1.func(op, Seq(Var(v), Val(arg)))
    }

    def datomTx(e: String, v: String, v1: String): Query = {
      // Ensure tx value is present
      val (hasTxV, cls0): (Int, Seq[Clause]) = q.wh.clauses.foldRight(0, Seq.empty[Clause]) {
        case (cl@DataClause(_, Var(`e`), _, _, NoBinding | Empty | Var("_"), _), (0, acc)) =>
          (1, cl.copy(tx = Var(v + "_tx")) +: acc)

        case (cl@DataClause(_, Var(`e`), _, _, Var(tx), _), (0, acc)) if tx == v + "_tx" =>
          (1, cl +: acc)

        case (cl@DataClause(_, _, _, Var(`e`), NoBinding | Empty | Var("_"), _), (0, acc)) =>
          (1, cl.copy(tx = Var(v + "_tx")) +: acc)

        case (cl@DataClause(_, _, _, Var(`e`), Var(tx), _), (0, acc)) if tx == v + "_tx" =>
          (1, cl +: acc)

        case (cl, (ok, acc)) =>
          (ok, cl +: acc)

      }

      val cls: Seq[Clause] = if (hasTxV == 1) {
        cls0
      } else {
        Seq(
          DataClause(ImplDS, Var(e), KW("?", e + "_attr"), Var(v), Var(v + "_tx"))
        )
      }

      val q1 = q.copy(wh = Where(cls))

      // Add necessary bind to ident to prepare working with tx value
      q1.wh.clauses.reverse.collectFirst {
        case DataClause(_, Var(e0), KW("db", "ident", _), _, _, _) if e0 == e + "_attr" => q1
        case DataClause(_, Var(`e`), KW("?", attr, _), _, _, _) if attr == e + "_attr"  => q1
          .ident(e + "_attr", v1)
          .func("namespace", Seq(Var(v1)), ScalarBinding(Var(v + "_ns")))
          .func("!=", Seq(Var(v + "_ns"), Val("db.install")))
          .func("!=", Seq(Var(v + "_ns"), Val("db")))
          .func("!=", Seq(Var(v + "_ns"), Val("fressian")))
        case DataClause(_, Var(`e`), _, _, _, _)                                        => q1
      } getOrElse
        q1.ident(e + "_attr", v1)
          .func("namespace", Seq(Var(v1)), ScalarBinding(Var(v + "_ns")))
          .func("!=", Seq(Var(v + "_ns"), Val("db.install")))
          .func("!=", Seq(Var(v + "_ns"), Val("db")))
          .func("!=", Seq(Var(v + "_ns"), Val("fressian")))
    }

    def datomT(e: String, v: String, v1: String): Query = {
      q.wh.clauses.reverse.collectFirst {
        case DataClause(_, _, _, _, Var(tx), _) if tx == v + "_tx" =>
          q.func("-", Seq(Var(v + "_tx"), Val(txBase)), ScalarBinding(Var(v + "_t")))
        //          q.func("datomic.api/tx->t", Seq(Var(v + "_tx")), ScalarBinding(Var(v + "_t")))

      } getOrElse
        q.datomTx(e, v, v1)
          .func("-", Seq(Var(v + "_tx"), Val(txBase)), ScalarBinding(Var(v + "_t")))
      //          .func("datomic.api/tx->t", Seq(Var(v + "_tx")), ScalarBinding(Var(v + "_t")))
    }

    def datomTxInstant(e: String, v: String, v1: String): Query = {
      q.wh.clauses.reverse.collectFirst {
        case DataClause(_, _, _, _, Var(tx), _) if tx == v + "_tx" =>
          q.where(v + "_tx", "db", "txInstant", Var(v + "_txInstant"), "", "")
      } getOrElse
        q.datomTx(e, v, v1)
          .where(v + "_tx", "db", "txInstant", Var(v + "_txInstant"), "", "")
    }

    def datomOp(e: String, v: String, v1: String): Query = {
      val (ok, cls0): (Int, Seq[Clause]) = q.wh.clauses.foldRight(0, Seq.empty[Clause]) {
        case (cl@DataClause(_, Var(`e`), _, _, Empty | NoBinding, NoBinding), (0, acc)) =>
          (1, cl.copy(tx = Var("_"), op = Var(v + "_op")) +: acc)

        case (cl@DataClause(_, Var(`e`), _, _, _, NoBinding), (0, acc)) =>
          (1, cl.copy(op = Var(v + "_op")) +: acc)

        case (cl@DataClause(_, Var(`e`), _, _, _, Var(op)), (0, acc)) if op == v + "_op" =>
          (1, cl +: acc)

        case (cl@DataClause(_, _, _, Var(`e`), Empty | NoBinding, NoBinding), (0, acc)) =>
          (1, cl.copy(tx = Var("_"), op = Var(v + "_op")) +: acc)

        case (cl@DataClause(_, _, _, Var(`e`), _, NoBinding), (0, acc)) =>
          (1, cl.copy(op = Var(v + "_op")) +: acc)

        case (cl@DataClause(_, _, _, Var(`e`), _, Var(op)), (0, acc)) if op == v + "_op" =>
          (1, cl +: acc)

        case (cl, (ok, acc)) =>
          (ok, cl +: acc)

      }
      val cls       : Seq[Clause]        = if (ok == 1) cls0 else Seq(
        DataClause(ImplDS, Var(e), KW("?", e + "_attr"), Var(v), Var("_"), Var(v + "_op"))
      )
      val q1                             = q.copy(wh = Where(cls))

      // Add necessary bind to ident to prepare working with tx value
      q1.wh.clauses.reverse.collectFirst {
        case DataClause(_, _, _, _, _, Var(op0)) if op0 == v + "_op" && ok == 1         => q1
        case DataClause(_, Var(e0), KW("db", "ident", _), _, _, _) if e0 == e + "_attr" => q1
      } getOrElse
        q1.ident(e + "_attr", v1)
          .func("namespace", Seq(Var(v1)), ScalarBinding(Var(v + "_ns")))
          .func("!=", Seq(Var(v + "_ns"), Val("db.install")))
          .func("!=", Seq(Var(v + "_ns"), Val("db")))
          .func("!=", Seq(Var(v + "_ns"), Val("fressian")))
    }


    // Extra ..........................................

    def enumm(e: String, a: Atom, v: String): Query =
      q.where(e, a, v).ident(v, v + 1).kw(v + 1, v + 2)

    def ident(v: String, v1: String): Query =
      q.where(v, "db", "ident", Var(v1), "", "")

    def kw(v1: String, v2: String): Query =
      q.func("name", Seq(Var(v1)), ScalarBinding(Var(v2)))


    def compareToMany[T](op: String, a: Atom, v: String, args: Seq[T]): Query =
      args.zipWithIndex.foldLeft(q) {
        case (q1, (arg: URI, i)) =>
          q1.func(s"""ground (java.net.URI. "$arg")""", Empty, v + "_" + (i + 1) + "a")
            .func(".compareTo ^java.net.URI", Seq(Var(v), Var(v + "_" + (i + 1) + "a")),
              ScalarBinding(Var(v + "_" + (i + 1) + "b")))
            .func(op, Seq(Var(v + "_" + (i + 1) + "b"), Val(0)))
        case (q1, (arg, i))      => q1.compareTo(op, a, v, Val(arg), i + 1)
      }

    def compareTo(op: String, a: Atom, v: String, qv: QueryValue, i: Int = 0): Query = qv match {
      case Val(arg) if a.tpe == "String"     => compareTo2(op, a.tpe, v, Val(esc1(arg)), i)
      case Val(arg) if a.tpe == "Double"     => compareTo2(op, a.tpe, v, Val(double(arg)), i)
      case Val(arg) if a.tpe == "BigDecimal" => compareTo2(op, a.tpe, v, Val(bigDec(arg)), i)
      case _                                 => compareTo2(op, a.tpe, v, qv, i)
    }

    def compareTo2(op: String, tpeS: String, v: String, qv: QueryValue, i: Int = 0): Query = {
      val w  = Var(if (i > 0) v + "_" + i else v + 2)
      val q1 = tpeS match {
        case "URI" => qv match {
          case Val(arg) => q
            .func(s"""ground (java.net.URI. "$arg")""", Empty, v + "_" + (i + 1) + "a")
            .func(".compareTo ^java.net.URI", Seq(Var(v), Var(v + "_" + (i + 1) + "a")), ScalarBinding(w))
          case _        => q
            .func(".compareTo ^" + castStr(tpeS), Seq(Var(v), qv), ScalarBinding(w))
        }

        case "BigInt" =>
          qv match {
            case Var(v1) => q
              .func("biginteger", Seq(Var(v)), ScalarBinding(Var(v + "_casted")))
              .func("biginteger", Seq(Var(v1)), ScalarBinding(Var(v1 + "_casted")))
              .func(".compareTo ^java.math.BigInteger", Seq(Var(v + "_casted"), Var(v1 + "_casted")),
                ScalarBinding(w))

            case Val(arg) => q
              .func("biginteger", Seq(Var(v)), ScalarBinding(Var(v + "_casted")))
              .func(".compareTo ^java.math.BigInteger", Seq(Var(v + "_casted"), Val(arg)), ScalarBinding(w))

            case other => throw new IllegalArgumentException("Unexpected QueryValue for BigInt resolution: " + other)
          }

        case "BigDecimal" => q.func(".compareTo ^java.math.BigDecimal", Seq(Var(v), qv), ScalarBinding(w))
        case _            => q.func(".compareTo ^" + castStr(tpeS), Seq(Var(v), qv), ScalarBinding(w))
      }
      q1.func(op, Seq(w, Val(0)))
    }

    def ground(a: Atom, arg: Any, v: String): Query = a.tpe match {
      case "String"     => q.func(s"""ground "${esc1(arg)}"""", Empty, v)
      case "Double"     => q.func(s"""ground ${withDecimal(arg)}""", Empty, v)
      case "Date"       => q.func(s"""ground #inst "${date2datomicStr(arg.asInstanceOf[Date])}"""", Empty, v)
      case "UUID"       => q.func(s"""ground #uuid "$arg"""", Empty, v)
      case "URI"        => q.func(s"""ground (java.net.URI. "$arg")""", Empty, v)
      case "BigInt"     => q.func(s"""ground (java.math.BigInteger. "$arg")""", Empty, v)
      case "BigDecimal" => q.func(s"""ground (java.math.BigDecimal. "${withDecimal(arg)}")""", Empty, v)
      case _            => q.func(s"""ground $arg""", Empty, v)
    }

    def fulltext(e: String, a: Atom, v: String, s: String): Query =
      q.func("fulltext", Seq(DS, KW(a.nsFull, a.attr), Val(esc1(s))), RelationBinding(Seq(Var(e), Var(v))))

    def fulltext(e: String, a: Atom, v: String, qv: Var): Query =
      q.func("fulltext", Seq(DS, KW(a.nsFull, a.attr), qv), RelationBinding(Seq(Var(e), Var(v))))

    def mappings(e: String, args0: Seq[(String, Any)]): Query = {
      val ruleName = "rule" + (q.i.rules.map(_.name).distinct.size + 1)
      val newRules = args0.foldLeft(q.i.rules) { case (rules, (key, value)) =>
        val dataClauses = Seq(FunctClause(".matches ^String", Seq(Var(e), Val("^(" + key + ")@(" + value + ")$")), NoBinding))
        val rule        = Rule(ruleName, Seq(Var(e)), dataClauses)
        rules :+ rule
      }
      val newIn    = q.i.copy(ds = (q.i.ds :+ DS).distinct, rules = newRules)
      val newWhere = Where(q.wh.clauses :+ RuleInvocation(ruleName, Seq(Var(e))))
      q.copy(i = newIn, wh = newWhere)
    }

    def matches(v: String, inVar: Var): Query =
      q.func(".matches ^String", Seq(Var(v), inVar))

    def matches(v: String, regEx: String): Query =
      q.func(".matches ^String", Seq(Var(v), Val(regEx)))

    def matches(v: String, keys: Seq[String], valueRegEx: String): Query = {
      val keyRegEx = if (keys.isEmpty) ".+" else "(" + keys.mkString("|") + ")"
      q.func(".matches ^String", Seq(Var(v), Val(keyRegEx + "@" + valueRegEx)))
    }

    def mapCompareTo(op: String, e: String, a: Atom, v: String, keys: Seq[String], arg: Any): Query = {
      lazy val q1 = keys match {
        case Nil   =>
          q.where(e, a, v)
            .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v + 1)))
            .func("second", Seq(Var(v + 1)), ScalarBinding(Var(v + 2)))
        case keys_ =>
          q.where(e, a, v)
            .func(".matches ^String", Seq(Var(v), Val("(" + keys_.mkString("|") + ")" + "@.*")))
            .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v + 1)))
            .func("second", Seq(Var(v + 1)), ScalarBinding(Var(v + 2)))
      }
      arg match {
        case _: String  => q1.compareTo(op, a, v + 2, Val(arg), 1)
        case _: UUID    => q1.compareTo(op, a, v + 2, Val(arg), 1)
        case _: URI     => q1.compareTo(op, a, v + 2, Val(arg), 1)
        case _: Boolean => q1.compareTo(op, a, v + 2, Val(arg), 1)
        case d: Date    => q1
          .func(".compareTo ^String", Seq(Var(v + 2), Val(date2str(d))), ScalarBinding(Var(v + 3)))
          .func(op, Seq(Var(v + 3), Val(0)))
        case number     => q1
          // todo: compare BigInt/BigDecimal instead - can't compare number strings!
          // on JS platform we can't compare types either
          .func("read-string", Seq(Var(v + 2)), ScalarBinding(Var(v + 3)))
          .func(op, Seq(Var(v + 3), Val(number)))
      }
    }

    def mapInCompareTo(op: String, a: Atom, v: String): Query = {
      val q1 = q
        // Concatenate search string from input vars
        .func("str", Seq(Val("("), Var(v + "Key"), Val(")@.*")), ScalarBinding(Var(v + 1)))
        // match key(s) (could be regex)
        .func(".matches ^String", Seq(Var(v), Var(v + 1)))
        // extract value
        .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v + 2)))
        .func("second", Seq(Var(v + 2)), ScalarBinding(Var(v + 3)))
      a.tpe match {
        case "String"  => q1.compareTo(op, a, v + 3, Var(v + "Value"), 1)
        case "Boolean" => q1.compareTo(op, a, v + 3, Var(v + "Value"), 1)
        case "Date"    => q1
          .func(".compareTo ^String", Seq(Var(v + 3), Var(v + "Value")), ScalarBinding(Var(v + 5)))
          .func(op, Seq(Var(v + 5), Val(0)))
        case "UUID"    => q1.compareTo(op, a, v + 3, Var(v + "Value"), 1)
        case "URI"     => q1.compareTo(op, a, v + 3, Var(v + "Value"), 1)
        case _         => q1
          .func("read-string", Seq(Var(v + 3)), ScalarBinding(Var(v + 4)))
          .func(op, Seq(Var(v + 4), Var(v + "Value")))
      }
    }

    def mapInCompareToK(op: String, a: Atom, v: String, key: String): Query = {
      val q1 = q
        // match key(s) (could be regex)
        .func(".matches ^String", Seq(Var(v), Val(s"($key)@.*")))
        // extract value
        .func(".split ^String", Seq(Var(v), Val("@"), Val(2)), ScalarBinding(Var(v + 1)))
        .func("second", Seq(Var(v + 1)), ScalarBinding(Var(v + 2)))
      a.tpe match {
        case "String"  => q1.compareTo(op, a, v + 2, Var(v + "Value"), 1)
        case "Boolean" => q1.compareTo(op, a, v + 2, Var(v + "Value"), 1)
        case "Date"    => q1
          .func(".compareTo ^String", Seq(Var(v + 2), Var(v + "Value")), ScalarBinding(Var(v + 4)))
          .func(op, Seq(Var(v + 4), Val(0)))
        case "UUID"    => q1.compareTo(op, a, v + 2, Var(v + "Value"), 1)
        case "URI"     => q1.compareTo(op, a, v + 2, Var(v + "Value"), 1)
        case _         => q1
          .func("read-string", Seq(Var(v + 2)), ScalarBinding(Var(v + 3)))
          .func(op, Seq(Var(v + 3), Var(v + "Value")))
      }
    }

    def mapIn(e: String, a: Atom, v: String): Query =
      q.in(e, a, None, v + "Key").in(e, a, None, v + "Value").where(e, a, v)

    def mapIn2(e: String, a: Atom, v: String): Query =
      q.in(e, a, None, v + "Value").where(e, a, v)

    def matchRegEx(v: String, regex: Seq[QueryTerm]): Query =
      q.func("str", regex, ScalarBinding(Var(v + 1))).matches(v, Var(v + 1))

    def orRules(e: String, a: Atom, args: Seq[Any], specialV: String = "", flag: Boolean = false): Query = {
      val ruleName = "rule" + (q.i.rules.map(_.name).distinct.size + 1)
      val orRules  = if (flag && a.card == 2) {
        // Fulltext search for card-many attribute
        val ruleClauses = args.zipWithIndex.map { case (arg, i) =>
          FunctClause("fulltext", Seq(DS(), KW(a.nsFull, a.attr), Val(arg)),
            RelationBinding(List(Var(e), Var(e + "_" + (i + 1)))))
        }
        Seq(Rule(ruleName, Seq(Var(e)), ruleClauses))
      } else {
        args.zipWithIndex.distinct.flatMap { case (arg, i) =>
          val ruleClauses = arg match {
            case set: Set[_] =>
              if (specialV.nonEmpty) {
                set.toSeq.zipWithIndex.flatMap { case (uri, j) =>
                  val x = Var(specialV + "_" + (j + 1))
                  Seq(
                    DataClause(ImplDS, Var(e), KW(a.nsFull, a.attr), x, Empty),
                    FunctClause(s"""ground (java.net.URI. "${doubleEsc(uri)}")""", Nil, ScalarBinding(x))
                  )
                }
              } else {
                a.tpe match {
                  case "Double" => set.toSeq.map(arg =>
                    DataClause(ImplDS, Var(e), KW(a.nsFull, a.attr), Val(double(arg)), Empty))

                  case "BigDecimal" => set.toSeq.map(arg =>
                    DataClause(ImplDS, Var(e), KW(a.nsFull, a.attr), Val(bigDec(arg)), Empty))

                  case _ => set.toSeq.map(arg =>
                    DataClause(ImplDS, Var(e), KW(a.nsFull, a.attr), Val(pre(a, arg)), Empty))
                }
              }

            case mapArg if a.card == 3 => Seq(
              FunctClause(".matches ^String", Seq(Var(e), Val(".+@" + doubleEsc(mapArg))), NoBinding))

            case _ if specialV.nonEmpty && flag => Seq(
              FunctClause("=", Seq(Var(specialV), Val(arg)), NoBinding))

            case uri if specialV.nonEmpty => Seq(
              DataClause(ImplDS, Var(e), KW(a.nsFull, a.attr), Var(specialV), Empty),
              FunctClause(s"""ground (java.net.URI. "${doubleEsc(uri)}")""", Nil, ScalarBinding(Var(specialV)))
            )
            case _ if flag                => Seq(
              FunctClause("fulltext", Seq(DS(), KW(a.nsFull, a.attr), Val(arg)),
                RelationBinding(List(Var(e), Var(e + "_" + (i + 1))))))

            case _ if a.tpe == "Double" => Seq(
              DataClause(ImplDS, Var(e), KW(a.nsFull, a.attr), Val(double(arg)), Empty))

            case _ if a.tpe == "BigDecimal" => Seq(
              DataClause(ImplDS, Var(e), KW(a.nsFull, a.attr), Val(bigDec(arg)), Empty))

            case _ => Seq(
              DataClause(ImplDS, Var(e), KW(a.nsFull, a.attr), Val(pre(a, doubleEsc(arg))), Empty))
          }
          if (ruleClauses.isEmpty) None else Some(Rule(ruleName, Seq(Var(e)), ruleClauses))
        }
      }
      val newIn    = q.i.copy(ds = (q.i.ds :+ DS).distinct, rules = q.i.rules ++ orRules)
      val newWhere = Where(q.wh.clauses :+ RuleInvocation(ruleName, Seq(Var(e))))
      q.copy(i = newIn, wh = newWhere)
    }

    def func(name: String, qt: QueryTerm, v: String): Query =
      q.copy(wh = Where(q.wh.clauses :+ FunctClause(name, Seq(qt), ScalarBinding(Var(v)))))

    def func(name: String, ins: Seq[QueryTerm], outs: Binding = NoBinding): Query =
      q.copy(wh = Where(q.wh.clauses :+ FunctClause(name, ins, outs)))

    def ref(e: String, nsFull: String, refAttr: String, v: String, refNs: String): Query =
      q.copy(wh = Where(q.wh.clauses :+ DataClause(ImplDS, Var(e), KW(nsFull, refAttr, refNs), Var(v), Empty)))




    // Java conversions ...........................................................

    private def cast(a: Any): AnyRef = a match {
      case i: Int                                => i.toLong.asInstanceOf[Object]
      case bigI: BigInt                          => bigI.bigInteger
      case bigD: BigDecimal                      => bigD.bigDecimal
      case s: String if s.startsWith("__enum__") => s.drop(8).asInstanceOf[Object]
      case other                                 => other.asInstanceOf[Object]
    }

    def inputs: Seq[AnyRef] = q.i.inputs.map {
      case InVar(RelationBinding(_), _, Nil)         => javaList()
      case InVar(RelationBinding(_), _, argss)       => javaList(argss.map(args => javaList(args map cast: _*)): _*)
      case InVar(CollectionBinding(_), _, Nil)       => javaList()
      case InVar(CollectionBinding(_), _, argss)     => javaList(argss.head map cast: _*)
      case InVar(_, _, Nil)                          => javaList()
      case InVar(_, _, argss) if argss.head.size > 1 => Nil
      case InVar(_, _, argss)                        => cast(argss.head.head)
      case other                                     => throw QueryOpsException(s"UNEXPECTED input: $other\nquery:\n$q")
    }
  }
}
