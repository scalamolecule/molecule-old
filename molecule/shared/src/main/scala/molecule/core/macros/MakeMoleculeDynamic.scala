package molecule.core.macros

import molecule.core.exceptions.MoleculeException
import scala.language.higherKinds
import scala.reflect.macros.blackbox


/** Macro to make output molecules. */
class MakeMoleculeDynamic(val c: blackbox.Context) extends MakeBase with TreeTransformer {

  import c.universe._

  override def abort(msg: String): Nothing = throw MoleculeException(msg)

  //  private lazy val xx = InspectMacro("MakeMolecule", 9, 8)
  //  private lazy val xx = InspectMacro("MakeMoleculeDynamic", 1, 10)
  private lazy val yy = InspectMacro("MakeMoleculeDynamic", 1, 10, mkError = true)


  final def apply[Obj: W](body: Tree): Tree = {
    val (self, rawBodyTree) = body match {
      case Function(List(ValDef(_, self, _, _)), Block(rawBodyTree, _)) => (self, rawBodyTree)
      case Function(List(ValDef(_, _, _, _)), Literal(Constant(())))    =>
        abort(s"Body of dynamic molecule can't be empty. Please define some val or def")
      case other                                                        =>
        abort(s"Unexpected body of dynamic molecule:\n$other\n" + showRaw(other))
    }

    val baseMolecule = c.prefix.tree

    // Make tx function vals and defs private to avoid refinement errors.
    // They are called by selectDynamic/applyDynamic anyway.
    val bodyElements  = rawBodyTree.map(transformer.transform).map {
      case ValDef(_, prop, tpt, rhs) =>
        ValDef(Modifiers(flags = Flag.PRIVATE), prop, tpt, rhs)

      case DefDef(_, name, tparams, vparamss, tpe, rhs) =>
        DefDef(Modifiers(flags = Flag.PRIVATE), name, tparams, vparamss, tpe, rhs)

      case other => other
    }
    var fieldClauses  = Seq.empty[Tree]
    var methodClauses = Seq.empty[Tree]

    val moleculeBody: List[Tree] = c.prefix.tree match {
      case Typed(Block(body, _), _) => body
      case other                    => abort(s"Unexpected tree for dynamic molecule: $other")
    }

    val objMaker = moleculeBody.last match {
      case ClassDef(_, _, _, Template(_, _, bodyElements)) =>
        // Since we control the Molecule structure in the macros,
        // we know that the obj marshaller is always the third element:
        // JVM: constructor, row2tpl, row2obj ...
        // JS : constructor, packed2tpl, packed2obj ...
        bodyElements(2)
      case other                                           =>
        abort(s"Unexpected tree for dynamic molecule body:\n$other\n" + showRaw(other))
    }

    val (objTypes, objBodyElements) = if (isJsPlatform) {
      // todo
      //      val DefDef(_, TermName("row2obj"), _, _, _, Block(_, Block(List(ClassDef(_, _, _, Template(objTypes, _, objBodyElements))), _))) = objMaker
      //      val DefDef(_, TermName("row2obj"), _, _, _,          Block(List(ClassDef(_, _, _, Template(objTypes, _, objBodyElements))), _))                             = objMaker
      //      val ValDef(_, TermName("row2obj"), _, Function(_, Block(List(ClassDef(_, _, _, Template(objTypes, _, objBodyElements))), _))) = objMaker
      //      (objTypes, objBodyElements)

      objMaker match {
        case DefDef(_, TermName("packed2obj"), _, _, _, Block(_, Block(List(ClassDef(_, _, _, Template(objTypes, _, objBodyElements))), _))) => (objTypes, objBodyElements)
        case ValDef(_, TermName("packed2obj"), _, Function(_, Block(List(ClassDef(_, _, _, Template(objTypes, _, objBodyElements))), _)))    => (objTypes, objBodyElements)
        case ValDef(_, TermName("packed2obj"), _, Function(_, t))                                                                            =>
          yy(1, t, t.raw)
          null
        case ValDef(_, _, _, t)                                                                                                              =>
          yy(2, t, t.raw)
          null
      }


    } else {
      val ValDef(_, TermName("row2obj"), _, Function(_, Block(List(ClassDef(_, _, _,
      Template(objTypes, _, objBodyElements))), _))) = objMaker
      (objTypes, objBodyElements)
    }

    val propTypes = objTypes.drop(2)
    var props     = List.empty[Tree]
    var refs      = List.empty[Tree]
    objBodyElements.tail.foreach {
      case ValDef(_, prop, tpt, _) =>
        props = props :+ q"final override lazy val $prop: $tpt = __obj.$prop"

      case DefDef(_, refName, _, _, refTpe, _) =>
        refs = refs :+ q"final override def $refName: $refTpe = __obj.$refName"

      case _ =>
    }


    rawBodyTree.foreach {
      case ValDef(_, TermName(field), _, _) =>
        fieldClauses = fieldClauses :+ cq"$field => ${TermName(field)}"

      case DefDef(_, TermName(method), _, Nil, _, _) =>
        fieldClauses = fieldClauses :+ cq"$method => ${TermName(method)}"

      case DefDef(_, TermName(method), _, List(Nil), _, _) =>
        methodClauses = methodClauses :+ cq"$method => ${TermName(method)}()"

      case DefDef(_, TermName(method), _, List(params), _, _) =>
        val args = params.zipWithIndex.map {
          case (ValDef(_, _, Ident(TypeName(_)), _), i) => q"args($i)"
          case (ValDef(_, _, tpe, _), i)                => q"args($i).asInstanceOf[$tpe]"
        }
        methodClauses = methodClauses :+ cq"$method => ${TermName(method)}(..$args)"

      case _ =>
    }

    val selectDynamic = if (fieldClauses.isEmpty) Nil else Seq(
      q"""
        override def selectDynamic(name: String): Any = name match {
          case ..$fieldClauses
          case other => throw new IllegalArgumentException("Please implement `" + other + "`.")
        }
       """)

    val applyDynamic = if (methodClauses.isEmpty) Nil else Seq(
      q"""
        override def applyDynamic(method: String)(args: Any*): Any = method match {
          case ..$methodClauses
          case other => throw new IllegalArgumentException("Please implement `" + other + "`.")
        }
       """)

    lazy val objBody =
      q"""
        private val $self = __obj
        ..$props
        ..$refs
        ..$bodyElements
        ..${selectDynamic ++ applyDynamic}
      """

    lazy val t0 = propTypes match {
      case List(a)                                                                => q"new DynamicMolecule with Init with $a { ..$objBody }"
      case List(a, b)                                                             => q"new DynamicMolecule with Init with $a with $b { ..$objBody }"
      case List(a, b, c)                                                          => q"new DynamicMolecule with Init with $a with $b with $c { ..$objBody }"
      case List(a, b, c, d)                                                       => q"new DynamicMolecule with Init with $a with $b with $c with $d { ..$objBody }"
      case List(a, b, c, d, e)                                                    => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e { ..$objBody }"
      case List(a, b, c, d, e, f)                                                 => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f { ..$objBody }"
      case List(a, b, c, d, e, f, g)                                              => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g { ..$objBody }"
      case List(a, b, c, d, e, f, g, h)                                           => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i)                                        => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j)                                     => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k)                                  => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l)                               => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l, m)                            => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n)                         => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)                      => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)                   => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)                => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)             => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)          => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)       => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)    => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u { ..$objBody }"
      case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) => q"new DynamicMolecule with Init with $a with $b with $c with $d with $e with $f with $g with $h with $i with $j with $k with $l with $m with $n with $o with $p with $q with $r with $s with $t with $u with $v { ..$objBody }"
      case list                                                                   => abort("Unexpected list of types:\n  " + list.mkString("\n  "))
    }

    val t =
      q"""
        import molecule.core.transform.DynamicMolecule
        import molecule.core.dsl.base.Init
        $baseMolecule.getObjs.map { objs =>
           objs.map { __obj =>
             $t0
           }
        }
      """

    //    xx(1, t)
    t
  }
}
