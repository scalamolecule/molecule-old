package sbtmolecule

import java.net.URI
import java.text.SimpleDateFormat
import java.util.{Date, UUID}
import scala.util.matching


trait Helpers {

  implicit class Regex(sc: StringContext) {
    def r: matching.Regex = new scala.util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def padS(longest: Int, str: String): String = pad(longest, str.length)

  def pad(longest: Int, shorter: Int): String = if (longest > shorter) " " * (longest - shorter) else ""

  def padI(n: Int): String = if (n < 10) s"0$n" else s"$n"

  def firstLow(any: Any): String = {
    val str = any.toString
    str.length match {
      case 0 => ""
      case 1 => str.toLowerCase()
      case _ => s"${str.head.toLower}" + str.tail
    }
  }

  protected def format(date: Date): String = {
    val f = new SimpleDateFormat("'#inst \"'yyyy-MM-dd'T'HH:mm:ss.SSSXXX'\"'")
    f.format(date)
  }

  final protected def cast(value: Any): String = value match {
    case (a, b)     => s"(${cast(a)}, ${cast(b)})"
    case v: Long    => s"${v}L"
    case v: Float   => s"${v}f"
    case date: Date => "\"" + format(date) + "\""
    case v: String  => "\"" + v + "\""
    case v: UUID    => "\"" + v + "\""
    case v: URI     => "\"" + v + "\""
    case v          => v.toString
  }

  final protected def o(opt: Option[Any]): String = if (opt.isDefined) s"""Some(${cast(opt.get)})""" else "None"

  final protected def sq[T](values: Seq[T]): String = values.map {
    case set: Set[_] => set.map(cast).mkString("Set(", ", ", ")")
    case seq: Seq[_] => seq.map(cast).mkString("Seq(", ", ", ")")
    case (a, b)      => s"${cast(a)} -> ${cast(b)}"
    case v           => cast(v)
  }.mkString("Seq(", ", ", ")")
}
