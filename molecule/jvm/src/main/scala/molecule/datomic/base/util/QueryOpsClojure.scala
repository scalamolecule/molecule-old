package molecule.datomic.base.util

import datomic.Util
import molecule.datomic.base.ast.query.{CollectionBinding, InVar, Query, RelationBinding}
import molecule.datomic.base.ops.exception.QueryOpsException

private[molecule] case class QueryOpsClojure(q: Query) {

  private def cast(a: Any): AnyRef = a match {
    case i: Int                                => i.toLong.asInstanceOf[Object]
    case bigI: BigInt                          => bigI.bigInteger
    case bigD: BigDecimal                      => bigD.bigDecimal
    case s: String if s.startsWith("__enum__") => Util.read(s.drop(8)) // Treat enum as Clojure Keyword
    case other                                 => other.asInstanceOf[Object]
  }

  def inputsWithKeyword: Seq[AnyRef] = q.i.inputs.map {
    case InVar(RelationBinding(_), _, Nil)         => Util.list()
    case InVar(RelationBinding(_), _, argss)       => Util.list(argss.map(args => Util.list(args map cast: _*)): _*)
    case InVar(CollectionBinding(_), _, Nil)       => Util.list()
    case InVar(CollectionBinding(_), _, argss)     => Util.list(argss.head map cast: _*)
    case InVar(_, _, Nil)                          => Util.list()
    case InVar(_, _, argss) if argss.head.size > 1 => Nil
    case InVar(_, _, argss)                        => cast(argss.head.head)
    case other                                     => throw QueryOpsException(s"UNEXPECTED input: $other\nquery:\n$q")
  }
}
