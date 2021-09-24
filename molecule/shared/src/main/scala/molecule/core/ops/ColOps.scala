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
        (l, ll, lll :+ (i, tpe, argss.map(v => v.map(w => w.toString))))

      case ((l, ll, lll), (InVar(CollectionBinding(_), tpe, argss), i)) =>
        (l, ll :+ (i, tpe, argss.head.map(v => v.toString)), lll)

      case ((l, ll, lll), (InVar(_, tpe, argss), i)) =>
        (l :+ (i, tpe, argss.head.head.toString), ll, lll)

      case other =>
        throw MoleculeException(s"[molecule.core.ops.ColOps] UNEXPECTED inputs: $other")
    }
  }
}
