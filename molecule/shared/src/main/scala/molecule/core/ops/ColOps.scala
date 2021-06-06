package molecule.core.ops

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.exceptions.MoleculeException
import molecule.core.util.DateHandling
import molecule.datomic.base.ast.query._


trait ColOps extends DateHandling {

  def c(arg: Any): (String, String) = arg match {
    case _: String     => ("String", arg.toString)
    case _: Int        => ("Long", arg.toString)
    case _: Long       => ("Long", arg.toString)
    case _: Float      => ("Double", arg.toString)
    case _: Double     => ("Double", arg.toString)
    case _: BigInt     => ("BigInt", arg.toString)
    case _: BigDecimal => ("BigDecimal", arg.toString)
    case _: Boolean    => ("Boolean", arg.toString)
    case d: Date       => ("Date", date2str(d))
    case _: UUID       => ("UUID", arg.toString)
    case _: URI        => ("URI", arg.toString)
  }

  def marshallInputs(q: Query): (
    Seq[(Int, (String, String))],
      Seq[(Int, Seq[(String, String)])],
      Seq[(Int, Seq[Seq[(String, String)]])]
    ) = {
    q.i.inputs.zipWithIndex.foldLeft(
      Seq.empty[(Int, (String, String))],
      Seq.empty[(Int, Seq[(String, String)])],
      Seq.empty[(Int, Seq[Seq[(String, String)]])]
    ) {
      case ((l, ll, lll), (InVar(RelationBinding(_), argss), i)) =>
        (l, ll, lll :+ (i, argss.map(v => v.map(w => c(w)))))

      case ((l, ll, lll), (InVar(CollectionBinding(_), argss), i)) =>
        (l, ll :+ (i, argss.head.map(v => c(v))), lll)

      case ((l, ll, lll), (InVar(_, argss), i)) =>
        (l :+ (i, c(argss.head.head)), ll, lll)

      case ((l, ll, lll), (InDataSource(_, argss), i)) =>
        (l :+ (i, c(argss.head.head)), ll, lll)

      case other =>
        throw MoleculeException(s"[molecule.core.ops.ColOps] UNEXPECTED inputs: $other")
    }
  }
}
