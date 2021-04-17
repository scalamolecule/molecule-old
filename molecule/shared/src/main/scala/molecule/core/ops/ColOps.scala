package molecule.core.ops

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.ast.elements._
import molecule.core.marshalling.Column
import molecule.datomic.base.ast.metaSchema._
import molecule.datomic.base.ast.query._
import molecule.datomic.base.transform.Model2Query.coalesce
import scala.collection.mutable
import scala.collection.mutable.ListBuffer


trait ColOps  {

  // Todo - hack to transmit input types because we can't transfer them typed with boopickle - maybe use upickle instead here?
  def c(arg: Any): String = arg match {
    case _: String     => "String"
    case _: Int        => "Long"
    case _: Long       => "Long"
    case _: Float      => "Double"
    case _: Double     => "Double"
    case _: BigInt     => "BigInt"
    case _: BigDecimal => "BigDecimal"
    case _: Boolean    => "Boolean"
    case _: Date       => "Date"
    case _: UUID       => "UUID"
    case _: URI        => "URI"
  }

  def encodeInputs(q: Query): (
    Seq[(Int, (String, String))],
      Seq[(Int, Seq[(String, String)])],
      Seq[(Int, Seq[Seq[(String, String)]])]
    ) = q.i.inputs.zipWithIndex.foldLeft(
    Seq.empty[(Int, (String, String))],
    Seq.empty[(Int, Seq[(String, String)])],
    Seq.empty[(Int, Seq[Seq[(String, String)]])]
  ) {
    case ((l, ll, lll), (InVar(RelationBinding(_), argss), i))   => (l, ll, lll :+ (i, argss.map(v => v.map(w => (c(w), w.toString)))))
    case ((l, ll, lll), (InVar(CollectionBinding(_), argss), i)) => (l, ll :+ (i, argss.head.map(v => (c(v), v.toString))), lll)
    case ((l, ll, lll), (InVar(_, argss), i))                    => (l :+ (i, (c(argss.head.head), argss.head.head.toString)), ll, lll)
    case ((l, ll, lll), (InDataSource(_, argss), i))             => (l :+ (i, (c(argss.head.head), argss.head.head.toString)), ll, lll)
    case other                                                   => sys.error(s"[molecule.ops.QueryOps] UNEXPECTED inputs: $other")
  }
}
