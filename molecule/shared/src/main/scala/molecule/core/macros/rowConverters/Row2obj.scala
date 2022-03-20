package molecule.core.macros.rowConverters

import molecule.core.macros.rowAttr.RowValue2cast
import molecule.core.marshalling.ast.nodes._
import scala.reflect.macros.blackbox


trait Row2obj extends RowValue2cast {
  val c: blackbox.Context

  import c.universe._

  //  private lazy val xx = InspectMacro("Row2obj", 1, mkError = true)
  private lazy val xx = InspectMacro("Row2obj", 1)

  def classes(nodes: List[Node]): List[Tree] = {
    var prevClasses = List.empty[String]
    nodes.flatMap {
      case Prop(cls, _, _, _, _, _) =>
        if (!prevClasses.contains(cls)) {
          prevClasses = prevClasses :+ cls
          Some(tq"${TypeName(cls)}")
        } else None

      case Obj(cls, _, true, props) => classes(props) match {
        case Nil                                                                    => Some(tq"${TypeName(cls)}[Seq[Init]]")
        case List(a)                                                                => Some(tq"${TypeName(cls)}[Seq[Init with $a]]")
        case List(a, b)                                                             => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b]]")
        case List(a, b, c)                                                          => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c]]")
        case List(a, b, c, d)                                                       => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d]]")
        case List(a, b, c, d, e)                                                    => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e]]")
        case List(a, b, c, d, e, f)                                                 => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f]]")
        case List(a, b, c, d, e, f, g)                                              => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g]]")
        case List(a, b, c, d, e, f, g, h)                                           => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h]]")
        case List(a, b, c, d, e, f, g, h, i)                                        => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i]]")
        case List(a, b, c, d, e, f, g, h, i, j)                                     => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j]]")
        case List(a, b, c, d, e, f, g, h, i, j, k)                                  => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l)                               => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m)                            => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n)                         => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)                      => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)                   => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)                => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)             => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)          => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)       => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)    => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u]]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) => Some(tq"${TypeName(cls)}[Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u with $v]]")
        case _                                                                      => None
      }

      case Obj(cls, _, _, props) => classes(props) match {
        case Nil                                                                    => Some(tq"${TypeName(cls)}[Init]")
        case List(a)                                                                => Some(tq"${TypeName(cls)}[Init with $a]")
        case List(a, b)                                                             => Some(tq"${TypeName(cls)}[Init with $a with $b]")
        case List(a, b, c)                                                          => Some(tq"${TypeName(cls)}[Init with $a with $b with $c]")
        case List(a, b, c, d)                                                       => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d]")
        case List(a, b, c, d, e)                                                    => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e]")
        case List(a, b, c, d, e, f)                                                 => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f]")
        case List(a, b, c, d, e, f, g)                                              => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g]")
        case List(a, b, c, d, e, f, g, h)                                           => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h]")
        case List(a, b, c, d, e, f, g, h, i)                                        => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i]")
        case List(a, b, c, d, e, f, g, h, i, j)                                     => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j]")
        case List(a, b, c, d, e, f, g, h, i, j, k)                                  => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l)                               => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m)                            => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n)                         => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)                      => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)                   => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)                => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)             => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)          => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)       => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)    => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u]")
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) => Some(tq"${TypeName(cls)}[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u with $v]")
        case _                                                                      => None
      }
    }
  }

  def objTree(obj: Obj, tplOpt: Option[Tree] = None, composite: Boolean = false): Tree = {
    tplOpt match {
      case Some(tpl) =>
        q"""{
            val tpl: Product = $tpl
            ${resolve(obj, -1, true, composite)._1}
          }
        """
      case None      =>
        q"{ ${resolve(obj, -1, false, composite)._1} }"
    }
  }

  def getTpe(p: Prop): Tree = {
    val typeName = TypeName(p.baseTpe match {
      case "ref"  => "Long"
      case "enum" => "String"
      case t      => t
    })
    if (p.prop.last == '$') {
      p.card match {
        case 1 => tq"Option[$typeName]"
        case 2 => tq"Option[Set[$typeName]]"
        case 3 => tq"Option[Map[String, $typeName]]"
        case 4 => tq"Option[$typeName]"
      }
    } else {
      p.card match {
        case 1 => tq"$typeName"
        case 2 => tq"Set[$typeName]"
        case 3 => tq"Map[String, $typeName]"
        case 4 => tq"$typeName"
      }
    }
  }

  def resolve(obj: Obj, colIndex0: Int, nested: Boolean, composite: Boolean): (Tree, Int) = {
    var colIndex = if ((nested || composite) && colIndex0 == -1) -1 else colIndex0

    def properties(nodes: List[Node]): List[Tree] = {
      var propNames = List.empty[String]
      val propDefs  = nodes.flatMap {
        case p@Prop(_, prop, baseTpe, card, group, optAggrTpe) =>
          val tpe = getTpe(p)
          //          xx(1, prop, baseTpe, tpe, group, optAggrTpe)
          colIndex += 1
          // Only generate 1 property, even if attribute is repeated in molecule
          if (!propNames.contains(prop)) {
            propNames = propNames :+ prop
            optAggrTpe match {
              case None if nested => Some(q"final override lazy val ${TermName(prop)}: $tpe = tpl.productElement($colIndex).asInstanceOf[$tpe]")
              case None           => Some(q"final override lazy val ${TermName(prop)}: $tpe = ${getRowValue2castLambda(group, baseTpe)(colIndex)}")

              case Some(aggrTpe) if aggrTpe == tpe.toString =>
                if (nested)
                  Some(q"final override lazy val ${TermName(prop)}: $tpe = tpl.productElement($colIndex).asInstanceOf[$tpe]")
                else
                  Some(q"final override lazy val ${TermName(prop)}: $tpe = ${getRowValue2castLambda(group, baseTpe)(colIndex)}")

              case Some(aggrTpe) =>
                val err = s"""Object property `$prop` not available since the aggregate changes its type to `$aggrTpe`. Please use tuple output instead to access aggregate value."""
                Some(q"""final override lazy val ${TermName(prop)}: $tpe = throw MoleculeException($err)""")
            }
          } else None

        case o@Obj(_, nestedRef, true, props) =>
          colIndex += 1
          val nestedAttrs = if (props.length == 1) {
            val singleNestedType = props.head match {
              case p@Prop(_, _, _, _, _, _) => getTpe(p)
              case _: Obj                   => tq"Product"
            }
            q"""
              tpl.productElement($colIndex).asInstanceOf[Seq[$singleNestedType]].map { v =>
                val tpl = Tuple1(v)
                ${resolve(o, -1, nested, false)._1}
              }
            """
          } else {
            q"tpl.productElement($colIndex).asInstanceOf[Seq[Product]].map( tpl => ${resolve(o, -1, nested, false)._1} )"
          }

          classes(props) match {
            case Nil                                                                    => Some(q"final override def ${TermName(nestedRef)}: Seq[Init] = $nestedAttrs")
            case List(a)                                                                => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a] = $nestedAttrs")
            case List(a, b)                                                             => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b] = $nestedAttrs")
            case List(a, b, c)                                                          => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c] = $nestedAttrs")
            case List(a, b, c, d)                                                       => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d] = $nestedAttrs")
            case List(a, b, c, d, e)                                                    => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e] = $nestedAttrs")
            case List(a, b, c, d, e, f)                                                 => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f] = $nestedAttrs")
            case List(a, b, c, d, e, f, g)                                              => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h)                                           => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i)                                        => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j)                                     => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k)                                  => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l)                               => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m)                            => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n)                         => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)                      => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)                   => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)                => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)             => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)          => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)       => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)    => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u] = $nestedAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) => Some(q"final override def ${TermName(nestedRef)}: Seq[Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u with $v] = $nestedAttrs")
            case _                                                                      => None
          }

        case o@Obj(_, ref, _, props) =>
          val (refAttrs, colIndexSub) = if (composite) {
            colIndex += 1
            val tree = if (props.length == 1) {
              q"""
                val tpl0: Product = tpl;
                {
                  val tpl: Product = Tuple1(tpl0.productElement($colIndex))
                  ${resolve(o, -1, nested, false)._1}
                }
              """
            } else {
              q"""
                val tpl0: Product = tpl;
                {
                  val tpl: Product = tpl0.productElement($colIndex).asInstanceOf[Product]
                  ${resolve(o, -1, nested, false)._1}
                }
              """
            }
            (tree, colIndex)
          } else {
            resolve(o, colIndex, nested, composite)
          }
          colIndex = colIndexSub
          classes(props) match {
            case Nil                                                                    => Some(q"final override def ${TermName(ref)}: Init = $refAttrs")
            case List(a)                                                                => Some(q"final override def ${TermName(ref)}: Init with $a = $refAttrs")
            case List(a, b)                                                             => Some(q"final override def ${TermName(ref)}: Init with $a with $b = $refAttrs")
            case List(a, b, c)                                                          => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c = $refAttrs")
            case List(a, b, c, d)                                                       => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d = $refAttrs")
            case List(a, b, c, d, e)                                                    => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e = $refAttrs")
            case List(a, b, c, d, e, f)                                                 => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f = $refAttrs")
            case List(a, b, c, d, e, f, g)                                              => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g = $refAttrs")
            case List(a, b, c, d, e, f, g, h)                                           => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i)                                        => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j)                                     => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k)                                  => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l)                               => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m)                            => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n)                         => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)                      => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)                   => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)                => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)             => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)          => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)       => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)    => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u = $refAttrs")
            case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) => Some(q"final override def ${TermName(ref)}: Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u with $v = $refAttrs")
            case _                                                                      => None
          }
      }
      propDefs
    }

    val tree = if (hasSameNss(obj)) {
      q"""throw MoleculeException(
            "Molecule objects are not allowed to have multiple same-named namespaces. Please use tuples instead that allow this."
          )"""
    } else {
      classes(obj.props) match {
        case Nil                                                                    => q"new Init {}"
        case List(a)                                                                => q"new Init with $a { ..${properties(obj.props)} }"
        case List(a, b)                                                             => q"new Init with $a with $b { ..${properties(obj.props)} }"
        case List(a, b, c)                                                          => q"new Init with $a with $b with $c { ..${properties(obj.props)} }"
        case List(a, b, c, d)                                                       => q"new Init with $a with $b with $c with $d { ..${properties(obj.props)} }"
        case List(a, b, c, d, e)                                                    => q"new Init with $a with $b with $c with $d with $e { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f)                                                 => q"new Init with $a with $b with $c with $d with $e with $f { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g)                                              => q"new Init with $a with $b with $c with $d with $e with $f with $g { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h)                                           => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i)                                        => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j)                                     => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k)                                  => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l)                               => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m)                            => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n)                         => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)                      => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)                   => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)                => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)             => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)          => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)       => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)    => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u { ..${properties(obj.props)} }"
        case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) => q"new Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u with $v { ..${properties(obj.props)} }"
        case list                                                                   => abort("Unexpected list of types:\n  " + list.mkString("\n  "))
      }
    }
    (tree, colIndex)
  }
}
