package molecule.core.ops

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.exceptions.MoleculeException
import molecule.core.util.DateHandling
import molecule.datomic.base.ast.query._


trait ColOps extends DateHandling {

  def marshallInputs(q: Query): (
    Seq[(Int, String, String)],
      Seq[(Int, String, Seq[String])],
      Seq[(Int, String, Seq[Seq[String]])]
    ) = {
    q.i.inputs.zipWithIndex.foldLeft(
      Seq.empty[(Int, String, String)],
      Seq.empty[(Int, String, Seq[String])],
      Seq.empty[(Int, String, Seq[Seq[String]])]
    ) {
      case ((l, ll, lll), (InVar(RelationBinding(_), tpe, argss), i)) =>
        val convertedInputs = argss.map(v => v.map(w => w.toString))
        (l, ll, lll :+ (i, tpe, convertedInputs))

      case ((l, ll, lll), (InVar(CollectionBinding(_), "Any", argss), i)) =>
        val convertedInputs = argss.head.flatMap {
          case v: String     => Seq("String    " + v)
          case v: BigInt     => Seq("BigInt    " + v.toString, "BigDecimal" + v.toString + ".0")
          case v: Long       => Seq("Long      " + v.toString, "Double    " + v.toString + ".0")
          case v: Int        => Seq("Int       " + v.toString, "Double    " + v.toString + ".0")
          case v: Boolean    => Seq("Boolean   " + v.toString)
          case v: Date       => Seq("Date      " + date2str(v))
          case v: URI        => Seq("URI       " + v.toString)
          case v: UUID       => Seq("UUID      " + v.toString)
          case v: BigDecimal => Seq("BigDecimal" + v.toString)
          case v: Double     => Seq("Double    " + v.toString)
          case v: Float      => Seq("Double    " + v.toString)
          case other         => throw MoleculeException(
            s"Unexpected marshall input value `$other` of type " + other.getClass)
        }
        (l, ll :+ (i, "Any", convertedInputs), lll)

      case ((l, ll, lll), (InVar(CollectionBinding(_), tpe, argss), i)) =>
        val convertedInputs = argss.head.map(convert(tpe, argss.head.head)(_))
        (l, ll :+ (i, tpe, convertedInputs), lll)

      case ((l, ll, lll), (InVar(ScalarBinding(_), tpe, argss), i)) =>
        val arg            = argss.head.head
        val convertedInput = convert(tpe, arg)(arg)
        (l :+ (i, tpe, convertedInput), ll, lll)

      case other =>
        throw MoleculeException(s"[molecule.core.ops.ColOps] UNEXPECTED inputs: $other")
    }
  }

  def convert(tpe: String, arg: Any): Any => String = tpe match {
    case "Date" => arg match {
      case _: Date => (v: Any) => date2str(v.asInstanceOf[Date])
      case _       => (v: Any) => v.toString
    }
    case _      => (v: Any) => v.toString
  }
}
