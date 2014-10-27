package molecule.in
import molecule.ast.model.{Model, Or, TermValue}
import molecule.ast.query.{Placeholder, Query, Var}


trait InputMolecule {
  val _model: Model
  val _query: Query

  def resolveOr[I1](or: Or[I1]): Seq[I1] = {
    def traverse(expr: Or[I1]): Seq[I1] = expr match {
      case Or(TermValue(v1), TermValue(v2)) => Seq(v1, v2)
      case Or(or1: Or[I1], TermValue(v2))   => traverse(or1) :+ v2
      case Or(TermValue(v1), or2: Or[I1])   => v1 +: traverse(or2)
      case Or(or1: Or[I1], or2: Or[I1])     => traverse(or1) ++ traverse(or2)
    }
    traverse(or)
  }

  def varsAndPrefixes = _query.i.inputs.collect {
    case Placeholder(v, kw, tpeS, enumPrefix, _) => (Var(v, tpeS), enumPrefix.getOrElse(""))
  }

  def getValues(prefix: String, rawValues: Seq[Any]) = if (prefix != "") {
    rawValues.flatMap {
      case many: Set[_] => many.toList.map(setValue => prefix + setValue.toString).toSeq
      case one          => Seq(prefix + one.toString)
    }
  } else {
    rawValues.flatMap {
      case many: Set[_] => many.toSeq
      case one          => Seq(one)
    }
  }
}