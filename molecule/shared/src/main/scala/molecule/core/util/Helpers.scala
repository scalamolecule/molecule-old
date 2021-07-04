package molecule.core.util

import java.net.URI
import java.time._
import java.time.format.DateTimeFormatter
import java.util.{Date, UUID}
import molecule.core.ast.elements.{Atom, Bond, Composite, Element, Generic, Model}
import molecule.core.exceptions.MoleculeException
import molecule.datomic.base.facade.TxReport
import scala.concurrent.{ExecutionContext, Future}

object Helpers extends Helpers
trait Helpers extends DateHandling {

  final protected object mkDate {
    def apply(year: Int, month: Int = 1, day: Int = 1): Date = {
      val localDate = LocalDate.of(year, month, day)
      val instant   = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant
      Date.from(instant)
    }
  }

  def clean(attr: String): String = attr.last match {
    case '_' => attr.init
    case '$' => attr.init
    case _   => attr
  }

  def getKwName(kw: String) = kw.substring(kw.indexOf('/') + 1)

  def thousands(i: Long): String =
    i.toString.reverse.grouped(3).mkString(" ").reverse

  // Uniform Date formatting to allow text comparisons
  final protected def f(a: Any) = a match {
    case date: Date => date2str(date).replace("+", "\\\\+")
    case other      => other
  }

  def escStr(s: String) = s.replace("""\""", """\\""").replace(""""""", """\"""")

  def unescStr(s: String) = s.replace("""\"""", """"""").replace("""\\""", """\""")

  def padS(longest: Int, str: String): String = pad(longest, str.length)

  def pad(longest: Int, shorter: Int): String = if (longest > shorter) " " * (longest - shorter) else ""

  def cast(value: Any): String = value match {
    case (a, b)                             => s"(${cast(a)}, ${cast(b)})"
    case v: Long                            => v.toString + "L"
    case v: Float                           => v.toString + "f"
    case date: Date                         => "\"" + date2str(date) + "\""
    case v: String if v.startsWith("__n__") => v.drop(5) // JS number hack
    case v: String                          => "\"" + escStr(v) + "\""
    case v: UUID                            => "\"" + v + "\""
    case v: URI                             => "\"" + v + "\""
    case v                                  => v.toString
  }

  final def os(opt: Option[Set[_]]): String =
    if (opt.isEmpty) "None" else s"""Some(${opt.get.map(cast)})"""

  final def o(opt: Option[Any]): String =
    if (opt.isEmpty) "None" else s"""Some(${cast(opt.get)})"""

  final def sq[T](values: Seq[T]): String =
    values.map {
      case set: Set[_] => set.map(cast).mkString("Set(", ", ", ")")
      case seq: Seq[_] => seq.map(cast).mkString("Seq(", ", ", ")")
      case (a, b)      => s"${cast(a)} -> ${cast(b)}"
      case v           => cast(v)
    }.mkString("Seq(", ", ", ")")


  final protected def tupleToSeq(arg: Any): Seq[Any] = arg match {
    case l: Seq[_]  => l
    case Some(v)    => Seq(v)
    case None       => Seq()
    case p: Product => p match {
      case t: (_, _)                                                             => Seq(t._1, t._2)
      case t: (_, _, _)                                                          => Seq(t._1, t._2, t._3)
      case t: (_, _, _, _)                                                       => Seq(t._1, t._2, t._3, t._4)
      case t: (_, _, _, _, _)                                                    => Seq(t._1, t._2, t._3, t._4, t._5)
      case t: (_, _, _, _, _, _)                                                 => Seq(t._1, t._2, t._3, t._4, t._5, t._6)
      case t: (_, _, _, _, _, _, _)                                              => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7)
      case t: (_, _, _, _, _, _, _, _)                                           => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)
      case t: (_, _, _, _, _, _, _, _, _)                                        => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)
      case t: (_, _, _, _, _, _, _, _, _, _)                                     => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10)
      case t: (_, _, _, _, _, _, _, _, _, _, _)                                  => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _)                               => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _)                            => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _)                         => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _)                      => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)                   => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)                => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)             => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)          => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)       => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)    => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20, t._21)
      case t: (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) => Seq(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20, t._21, t._22)
    }
    case a          => Seq(a)
  }

  def firstNs(model: Model): String = {
    def getNs(element: Element): String = element match {
      case Atom(nsFull, _, _, _, _, _, _, _) => nsFull
      case Bond(nsFull, _, _, _, _)          => nsFull
      case Generic(nsFull, _, _, _)          => nsFull
      case Composite(elements)               => getNs(elements.head)
      case other                             =>
        throw MoleculeException("Unexpected first model element: " + other)
    }
    getNs(model.elements.head)
  }


  class log {
    private var buf = Seq.empty[String]
    def apply(s: String): Unit = buf = buf :+ s
    def print: Unit = println(buf.mkString("\n"))
  }

  private var time0 = System.currentTimeMillis()
  private val times = collection.mutable.Map.empty[Int, Long]

  protected final def time(n: Int, prev: Int = 0) = {
    if (n < 1 || prev < 0)
      throw new IllegalArgumentException(s"Identifiers have to be positive numbers")

    if (times.nonEmpty && n <= times.keys.max)
      throw new IllegalArgumentException(s"Identifier have to be incremental. `$n` is smaller than or equal to previous `${times.keys.max}`")

    if (times.keys.toSeq.contains(n))
      throw new IllegalArgumentException(s"Can't use same time identifier `$n` multiple times")

    val time1   = if (prev > 0) times(prev) else time0
    val time2   = System.currentTimeMillis()
    val elapsed = time2 - time1
    times += n -> time2
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    val d         = LocalDateTime.ofInstant(Instant.ofEpochMilli(elapsed), ZoneOffset.UTC)
    println(s"TIME $n: " + formatter.format(d))
    time0 = System.currentTimeMillis()
  }
}