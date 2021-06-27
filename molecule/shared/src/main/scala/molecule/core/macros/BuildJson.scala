package molecule.core.macros

import molecule.core.macros.qr.CastArrays
import scala.reflect.macros.blackbox


trait BuildJson extends BuildObj {
  val c: blackbox.Context

  import c.universe._

  lazy override val p = InspectMacro("BuildJson", 1, 900)


  def jsonCode(obj: Obj, i0: Int = -1, isNested: Boolean = false): (Tree, Int) = {
    // Property index of row/tuple
    var i = if (isNested && i0 == -1) -1 else i0

    def properties(nodes: List[Node]): List[Tree] = {
      var propNames = List.empty[String]
      val propDefs  = nodes.flatMap {
        case Prop(_, prop, tpe, _, json, optAggr) =>
          i += 1
          // Only generate 1 property, even if attribute is repeated in molecule
          if (!propNames.contains(prop)) {
            propNames = propNames :+ prop
            optAggr match {
              case None if isNested => Some(q"final override lazy val ${TermName(prop)}: $tpe = tpl.productElement($i).asInstanceOf[$tpe]")
              case None             => Some(q"final override lazy val ${TermName(prop)}: $tpe = ${json(i)}")

              case Some(aggrTpe) if aggrTpe == tpe.toString() =>
                if (isNested)
                  Some(q"final override lazy val ${TermName(prop)}: $tpe = tpl.productElement($i).asInstanceOf[$tpe]")
                else
                  Some(q"final override lazy val ${TermName(prop)}: $tpe = ${json(i)}")

              case Some(aggrTpe) =>
                val err = s"""Object property `$prop` not available since the aggregate changes its type to `$aggrTpe`. Please use tuple output instead to access aggregate value."""
                Some(q"""final override lazy val ${TermName(prop)}: $tpe = throw MoleculeException($err)""")
            }
          } else None

        case o@Obj(_, ref, 2, props) =>
          i += 1
          val productTpe = if (props.length == 1) {
            props.head match {
              case Prop(_, _, tpe, _, _, _) => tq"Tuple1[$tpe]"
              case Obj(_, _, _, _)          => tq"Tuple1[Product]"
            }
          } else {
            tq"Product"
          }
          val subObj     = q"tpl.productElement($i).asInstanceOf[Seq[$productTpe]].map( tpl => ${jsonCode(o, -1, isNested)._1} )"
          classes(props) match {
            case Nil                                                                    => Some(q"final override def ${TermName(ref)}: Seq[Init] = $subObj")
            case List(a)                                                                => Some(q"final override def ${TermName(ref)}: Seq[Init with $a] = $subObj")
            case List(a, b)                                                             => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b] = $subObj")
            case List(a, b, c)                                                          => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c] = $subObj")
            case List(a, b, c, d)                                                       => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d] = $subObj")
            case List(a, b, c, d, e)                                                    => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e] = $subObj")
            case List(a, b, c, d, e, f)                                                 => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f] = $subObj")
            case List(a, b, c, d, e, f, g)                                              => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g] = $subObj")
            case List(a, b, c, d, e, f, g, h)                                           => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h] = $subObj")
            case List(a, b, c, d, e, f, g, h, i)                                        => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j)                                     => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k)                                  => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l)                               => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m)                            => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n)                         => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)                      => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)                   => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)                => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)             => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)          => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)       => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)    => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u] = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) => Some(q"final override def ${TermName(ref)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u with $v] = $subObj")
            case _                                                                      => None
          }

        case o@Obj(_, ref, _, props) =>
          val (subObj, j) = jsonCode(o, i, isNested)
          i = j
          classes(props) match {
            case Nil                                                                    => Some(q"final override def ${TermName(ref)}: Init = $subObj")
            case List(a)                                                                => Some(q"final override def ${TermName(ref)}: Init with $a = $subObj")
            case List(a, b)                                                             => Some(q"final override def ${TermName(ref)}: Init with $a with $b = $subObj")
            case List(a, b, c)                                                          => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c = $subObj")
            case List(a, b, c, d)                                                       => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d = $subObj")
            case List(a, b, c, d, e)                                                    => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e = $subObj")
            case List(a, b, c, d, e, f)                                                 => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f = $subObj")
            case List(a, b, c, d, e, f, g)                                              => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g = $subObj")
            case List(a, b, c, d, e, f, g, h)                                           => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h = $subObj")
            case List(a, b, c, d, e, f, g, h, i)                                        => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j)                                     => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k)                                  => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l)                               => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m)                            => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n)                         => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)                      => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)                   => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)                => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)             => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)          => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)       => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)    => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u = $subObj")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u with $v = $subObj")
            case _                                                                      => None
          }
      }
      propDefs
    }


    val tree = if (hasSameNss(obj)) {
      q"""throw MoleculeException(
            "Please compose multiple same-name namespaces with `++` instead of `+` to access field values."
          )"""
    } else {
      q"{ ..${properties(obj.props)} }"
    }
    (tree, i)
  }
}
