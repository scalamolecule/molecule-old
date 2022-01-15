package molecule.core.macros

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/** Transaction function container object annotation
 *
 * Annotate objects containing tx function methods with `@TxFns`. A Scala macro then
 * makes the necessary preparations to use the functions in Datomic transactions.
 */
class TxFns extends scala.annotation.StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro TxFnMacro.prepareForDatalog
}

private[molecule] final class TxFnMacro(val c: blackbox.Context) extends MacroHelpers {

  import c.universe._

  //  private lazy val xx = InspectMacro("TxFns", 1, 3, mkError = true)
  //  private lazy val xx = InspectMacro("TxFns", 1, 3)

  def prepareForDatalog(annottees: Tree*): Tree = annottees match {
    case List(obj: ModuleDef) => obj match {
      case q"object $txFnContainer { ..$typedTxFns }" =>
        // Add untyped tx functions with extra initial db arg
        val t =
          q"""
              object $txFnContainer {
                // Typed tx functions as-is
                ..$typedTxFns

                // Untyped classpath transaction functions to be available on the classpath.
                // Are called with `transactFn(<classpathTxFn>(<args..>))`
                ..${typedTxFns.map(untypedClasspathTxFn(_))}
              }
          """
        //        xx(1, t)
        t

      case _ => c.abort(c.enclosingPosition, s"No self-type allowed in @TxFns-annotated container.")
    }

    case _ => c.abort(
      c.enclosingPosition,
      s"Only object definition containing tx functions should be annotated with @TxFns")
  }

  def untypedClasspathTxFn(element: Tree): Tree = element match {

    case q"def $txFn(..$args)(implicit $conn: Future[Conn], $ec: ExecutionContext): Future[Seq[Statement]] = {..$txFnBody}" =>
      val classpathTxFn = TermName(txFn.toString + "__classpathTxFn")

      /** Transaction function is invoked by Datomic with the current
       * database `txDb` invoked as first argument:
       * */
      q"""
        def $classpathTxFn(
          txDb: AnyRef,
          txMetaData: AnyRef,
          ..${args.map(untypedParam(_))}
        ): AnyRef = {
          import molecule.datomic.peer.facade.Conn_Peer
          import molecule.datomic.base.ast.transactionModel.Statement
          import scala.concurrent.duration.DurationInt
          import molecule.core.util.Executor._
          import scala.concurrent.{Await, Future}
          import java.util.{List => jList}

          // Make connection with current db available to tx function code
          val conn = Conn_Peer(txDb)
          implicit val futConn: Future[Conn] = Future(conn)

          // Typed arguments
          ..${args.map(typedParam(_))}

          // Execute body of tx function
          val futTxFnStmts: Future[Seq[Statement]] = {
            ..$txFnBody
          }
          val txFnStmts = Await.result(futTxFnStmts, 1.minute)

          // Convert Molecule statements to java statements for Datomic
          val txFnStmtsJava = conn.stmts2java(txFnStmts)

          // Optional tx meta data attached to the transaction
          val txMetaStmtsJava = txMetaData.asInstanceOf[jList[jList[_]]]

          // Merge java statements
          val stmts: jList[jList[_]] = new java.util.ArrayList()
          txMetaStmtsJava.forEach(stmt => stmts.add(stmt))
          txFnStmtsJava.forEach(stmt => stmts.add(stmt))
          stmts
       }
    """

    case other => c.abort(c.enclosingPosition,
      s"""@txFns-annotated container only allows tx functions with the following signature constraints:
         |def <txFnName>(<args..>)(implicit conn: Future[Conn], ec: ExecutionContext): Future[Seq[Statement]] = { <body> }
         |Found:
         |$other
      """.stripMargin)
  }

  def untypedParam(arg: Tree): Tree = arg match {
    case ValDef(_, TermName(param), _, _) => q"val ${TermName(param + "0")}: AnyRef"
    case other                            => c.abort(c.enclosingPosition, s"Unrecognized parameter of tx fn: " + other)
  }

  def typedParam(arg: Tree): Tree = arg match {
    case x@ValDef(_, TermName(param), tpe@Ident(TypeName(tpeName)), _) =>
      val rs = tpeName match {
        case "Int" => q"${TermName(param + "0")}.toString.toInt"

        //        // Activate if necessary
        //        case "String"     => q"${TermName(param + "0")}"
        //        case "Long"       => q"${TermName(param + "0")}.toString.toLong"
        //        case "Double"     => q"${TermName(param + "0")}.toString.toDouble"
        //        case "Boolean"    => q"${TermName(param + "0")}.toString.toBoolean"
        //        case "Date"       => q"${TermName(param + "0")} ??"
        //        case "URI"        => q"new java.net.URI(${TermName(param + "0")})"
        //        case "UUID"       => q"java.util.UUID.fromString(${TermName(param + "0")})"
        //        case "BigInt"     => q"BigInt(${TermName(param + "0")})"
        //        case "BigDecimal" => q"BigDecimal(${TermName(param + "0")})"

        case _ => q"${TermName(param + "0")}.asInstanceOf[$tpe]"
      }
      //      println("======== " + x + "  ###  " + x.raw)
      q"val ${TermName(param)}: $tpe = $rs"

    case other => c.abort(c.enclosingPosition, s"Unrecognized parameter of tx fn: " + other)
  }
}
