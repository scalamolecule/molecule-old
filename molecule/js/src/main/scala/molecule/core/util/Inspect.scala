package molecule.core.util

import java.util.{List => jList}
import molecule.core.ast.elements._
import molecule.datomic.base.ast.transactionModel._
import molecule.datomic.base.facade.TxReport
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

private[molecule] case class Inspect(
  header: String,
  threshold: Int,
  max: Int = 9999,
  showStackTrace: Boolean = false,
  maxLevel: Int = 99,
  showBi: Boolean = false
) {

  // Helpers ..........................................

  def padS(longest: Int, str: String) = pad(longest, str.length) + "  "

  def pad(longest: Int, shorter: Int): String = if (longest > shorter) " " * (longest - shorter) + " " else ""


  def apply(id: Int, params: Any*): Unit = {
    val stackTrace = if (showStackTrace) Thread.currentThread.getStackTrace mkString "\n" else ""
    if (id >= threshold && id <= max) {

      def traverse(x: Any, level: Int, i: Int): String = {
        val pad1   = if (i == 0) "" else "  " * level
        val indent = if (i == 0) "" else pad1
        val max    = level >= maxLevel
        x match {
          case Add(e, a, stmts: Seq[_], bi) =>
            val biStr = if (showBi && bi != NoValue) s"      <$bi>" else ""
            indent + ":db/add" + padS(10, ":db/add") + e + padS(32, e.toString) + a + padS(20, a) + s"list($biStr\n" +
              stmts.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString("\n") + ")"

          case add: Add           => indent + add
          case ret: Retract       => indent + ret
          case ret: RetractEntity => indent + ret

          case l: java.util.List[_] if l.size() == 4 && l.asScala.head.toString.take(4) == ":db/" => {
            val List(action, e, a, v) = l.asScala.toList
            indent + action + padS(13, action.toString) + e + padS(34, e.toString) + a + padS(26, a.toString) + "   " + v
          }

          case l: jList[_] if max       => indent + "JavaList(" + l.asScala.mkString(",   ") + ")"
          case l: jList[_]              => indent + "JavaList(\n" + l.asScala.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString("\n") + ")"
          case l: ArrayBuffer[_] if max => indent + "ArrayBuffer(" + l.zipWithIndex.mkString(",   ") + ")"
          case l: ArrayBuffer[_]        => indent + "ArrayBuffer(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString("\n") + ")"
          case l: Map[_, _] if max      => indent + "Map(" + l.mkString(",   ") + ")"
          case l: Map[_, _]             => indent + "Map(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString("\n") + ")"

          case l: Iterable[_] =>
            l.headOption match {
              case None =>
                indent + "List()"

              case Some(stmt: Statement) =>
                if (stmt.v.isInstanceOf[AbstractValue]) {
                  if (max)
                    indent + "List(" + l.mkString(",   ") + ")"
                  else
                    indent + "List(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString(",\n") + ")"
                } else {
                  // Enable copy/paste for datomic.Util.list to raw transactions
                  if (max)
                    indent + "list(" + l.mkString(",   ") + ")"
                  else
                    indent + "list(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString(",\n") + ")"
                }

              case _ if max =>
                indent + "List(" + l.mkString(",   ") + ")"
              case _        =>
                indent + "List(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString(",\n") + ")"
            }

          case Nested(bond, nested)   => indent + "Nested(\n" + (bond +: nested).zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString("\n") + ")"
          case TxMetaData(elements)   => indent + "TxMetaData(\n" + elements.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString("\n") + ")"
          case Composite(elements)    => indent + "Composite(\n" + elements.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString("\n") + ")"
          case m: Model               => indent + "Model(\n" + m.elements.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString("\n") + ")"
          case m: java.util.Map[_, _] => {
            if (m.size() == 4 && m.asScala.keys.map(_.toString).toSeq.contains(":db-before")) {
              val tx = m.asScala.toList
              indent + "Transaction(\n" +
                traverse(tx(0), level + 1, 1) + "\n" +
                traverse(tx(1), level + 1, 2) + "\n" +
                traverse(tx(2), level + 1, 3) + "\n" +
                traverse(":tempids(\n" + tx(3)._2.asInstanceOf[java.util.Map[_, _]].asScala.iterator.zipWithIndex.map { case (y, j) => traverse(y, level + 2, j + 1) }.mkString("\n") + "))", level + 1, 4)
            } else if (max)
              indent + "JavaMap(" + m.asScala.iterator.mkString(",   ") + ")"
            else
              indent + "JavaMap(\n" + m.asScala.iterator.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1) }.mkString("\n") + ")"
          }

          case (a, b) => {
            val bb = b match {
              case it: Iterable[_]             => traverse(it, level, 0)
              case it: java.util.Collection[_] => traverse(it, level, 0)
              case other                       => other
            }
            indent + s"$a -> " + bb
          }

          case txReport: TxReport => txReport.toString

          case value => indent + value //+ s" TYPE: " + (if (value == null) "Null" else value.getClass)
        }
      }

      println(
        s"## $id ## $header \n=============================================================================\n" +
          params.toList.zipWithIndex.map {
            case (x, i) => traverse(x, 0, i + 1)
          }.mkString("\n-----------------------------------------------------------------------------\n") +
          s"\n=============================================================================\n$stackTrace"
      )
    }
  }
}