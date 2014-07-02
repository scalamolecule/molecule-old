package molecule
package ops
import scala.reflect.macros.whitebox.Context
import molecule.ast.query._
import molecule.ast.schemaDSL._

trait TreeOps[Ctx <: Context] extends Liftables[Ctx] {
  import c.universe._

  implicit class richTree(t: Tree) {
    lazy val tpe        = c.typecheck(t).tpe
    lazy val at         = att(t)
    lazy val ns         = at.ns.toString
    lazy val name       = at.toString
    lazy val tpeS       = at.tpeS
    lazy val card       = at.card
    lazy val enumPrefix = at.enumPrefix
    def isPartition = tpe <:< typeOf[Partition]
    def isNS = tpe <:< typeOf[NS]
    def isD0 = tpe <:< typeOf[Out_0]
    def nsS = nsString(tpe.typeSymbol.owner.owner.name.toString.init)
    def isAttr = tpe <:< typeOf[Attr]
    def isRef = tpe <:< weakTypeOf[Ref]
    def isOneRef = tpe <:< weakTypeOf[OneRef]
    def isManyRef = tpe <:< weakTypeOf[ManyRef]
    def isValueAttr = tpe <:< weakTypeOf[ValueAttr[_, _, _]]
    def isOne = tpe <:< weakTypeOf[One[_, _, _]]
    def isMany = tpe <:< weakTypeOf[Many[_, _, _]]
    def isEnum = tpe <:< weakTypeOf[Enum]
    def isOneEnum = tpe <:< weakTypeOf[OneEnum[_, _]]
    def isManyEnum = tpe <:< weakTypeOf[ManyEnums[_, _]]
    override def toString = t.tpe.typeSymbol.name.toString
  }
  def nsString(ns: String): String = ns.head.toLower + ns.tail
  def nsString(ns: Tree): String = nsString(ns.symbol.name.toString)
  def nsString(ns: Name): String = nsString(ns.decodedName.toString)

  // Todo more types...
  def tpe(s: String) = s match {
    case "String" => tq"String"
    case "Int"    => tq"Int"
    case unknown  => abort(s"[DslOps:tpe] Unknown type: $unknown")
  }


  def inputMolecule_i_o(in: Int, out: Int) = (in, out) match {
    case (1, 0) => tq"InputMolecule_1_0"
    case (1, 1) => tq"InputMolecule_1_1"
    case (1, 2) => tq"InputMolecule_1_2"
    case (1, 3) => tq"InputMolecule_1_3"

    case (2, 0) => tq"InputMolecule_2_0"
    case (2, 1) => tq"InputMolecule_2_1"
    case (2, 2) => tq"InputMolecule_2_2"
    case (2, 3) => tq"InputMolecule_2_3"

    case (3, 0) => tq"InputMolecule_3_0"
    case (3, 1) => tq"InputMolecule_3_1"
    case (3, 2) => tq"InputMolecule_3_2"
    case (3, 3) => tq"InputMolecule_3_3"

    case (i, o) => abort(s"[BuildInputMolecule] Unsupported arity combination: $i in, $o out")
  }

  def dataX(outTypes: Seq[Type]) = outTypes.size match {
    case 2 => tq"Out2[..$outTypes]"
    case 3 => tq"Out3[..$outTypes]"
    case 4 => tq"Out4[..$outTypes]"
    case 5 => tq"Out5[..$outTypes]"
    case 6 => tq"Out6[..$outTypes]"
    case 7 => tq"Out7[..$outTypes]"
    case 8 => tq"Out8[..$outTypes]"
    case n => abort(s"[BuildInputMolecule:await] Unsupported arity for OutX: $n")
  }

  def namespaceSymbol(tree: Tree) = {
    def traverse(t: Tree): Symbol = t match {
      case q"$a.and($b)"                               => traverse(a)
      case q"$a.and[..$ts]($b)"                        => traverse(a)
      case q"$a.or($b)"                                => traverse(a)
      case q"$a.eqs($b)"                               => traverse(a)
      case q"$a.or[..$ts]($b)"                         => traverse(a)
      case q"TermValue.apply($a)"                      => traverse(a)
      case q"immutable.this.List.apply[$tpe](..$a)"    => traverse(a.head)
      case q"collection.this.Seq.apply[$tpe](..$a)"    => traverse(a.head)
      case Select(ns, attr) if ns.tpe <:< typeOf[NS]   => ns.tpe.typeSymbol
      case ns@Select(_, name) if ns.tpe <:< typeOf[NS] => ns.tpe.typeSymbol
      case _                                           => (t collect {
        case ns@Select(_, name) if ns.tpe <:< typeOf[NS] => ns.tpe.typeSymbol
      }).distinct.reverse.head
    }
    traverse(tree)
  }

  class nsp(val sym: Symbol) {
    val x = debug("ModelOps:nsp", 8)
    lazy val nsType = sym match {
      case s: TermSymbol if s.isLazy && s.isPublic    => s.typeSignature.typeSymbol.typeSignature
      case s: MethodSymbol
        if s.asMethod.returnType <:< weakTypeOf[Attr] => s.asMethod.returnType
      case s: ClassSymbol if s.toType <:< typeOf[NS]  => s.toType
      case unexpected                                 =>
        abortTree(q"$unexpected", s"[DslOps:nsp] Unexpected namespace symbol")
    }
    override def toString = {
      val s = sym.name.toString
      s.head.toLower + s.tail.takeWhile(_ != '_')
    }
    def attrs = nsType.members.collect {
      case s: TermSymbol if s.isLazy && s.isPublic                       => new att(s)
      case s: MethodSymbol if s.asMethod.returnType <:< weakTypeOf[Attr] => new att(s)
    }.toList.reverse
    // todo: remove redundant Ref methods!!
    def enums = attrs.filter(_.isEnum).distinct
    def isNamespace = true
  }

  object nsp {
    def apply(tree: Tree) = new nsp(namespaceSymbol(tree))
    def apply(symbol: Symbol) = new nsp(symbol)
  }

  class att(val sym: Symbol) {
    val x = debug("ModelOps:att", 2)

    lazy val attrType = sym match {
      case t: TermSymbol if t.isLazy && t.isPrivate   => sym.typeSignature.typeSymbol.typeSignature
      case t: MethodSymbol
        if t.asMethod.returnType <:< weakTypeOf[Attr] => sym.asMethod.returnType
      case unexpected                                 =>
        abortTree(q"$unexpected", s"[DslOps:attrType] Unexpected attribute symbol")
    }

    lazy val tpe = sym match {
      case t: TermSymbol if t.isLazy && t.isPublic                      => {
        val TypeRef(_, _, List(_, _, attrTpe)) = t.typeSignature.baseType(weakTypeOf[ValueAttr[_, _, _]].typeSymbol)
        attrTpe
      }
      case t: MethodSymbol if t.asMethod.returnType <:< weakTypeOf[Ref] => NoType
      case unexpected                                                   =>
        abortTree(q"$unexpected", s"[DslOps:tpe] ModelOps.att(sym) can only take an Attr symbol")
    }

    def name = TermName(toString)
    def fullName = attrType.typeSymbol.fullName
    def owner = attrType.typeSymbol.owner.owner
    def ns = new nsp(owner)
    def tpeS = if (tpe =:= NoType) "Long" else tpe.toString
    def contentType = tpe
    def isOne = attrType <:< weakTypeOf[One[_, _, _]]
    def isMany = attrType <:< weakTypeOf[Many[_, _, _]]
    def isEnum = attrType <:< weakTypeOf[Enum]
    def enumValues = attrType.members.collect {
      case v: TermSymbol
        if v.isPrivate && v.isLazy && v.typeSignature.typeSymbol.asType.toType =:= typeOf[EnumValue.type]
      => v.name.decodedName.toString.trim
    }
      .toList.reverse
    def hasEnum(enumCandidate: String) = enumValues.contains(enumCandidate)
    def card = if (isOne || attrType <:< weakTypeOf[OneEnum[_, _]]) 1 else 2
    def isValue = attrType <:< weakTypeOf[One[_, _, _]] || attrType <:< weakTypeOf[Many[_, _, _]] || attrType <:< weakTypeOf[OneEnum[_, _]]
    //    def keyw = KW(Symbol(ns.toString), Symbol(this.toString))
    def keyw = KW(ns.toString, this.toString)
    override def toString = sym.name.toString.head.toLower + sym.name.toString.tail
    def kw = KW(ns.toString, this.toString)
    def kwS = s":$ns/$name"
    // todo: if (nestedNS.isDefined) s":$ns.$nestedNS/$name" else s":$ns/$name"
    def enumPrefix = ns.enums.size match {
      case 0 => ""
//      case 1 => s":$name/"
      case _ => s":$ns.$name/"
    }
  }

  object att {
    def apply(attr: Tree) = new att(c.typecheck(attr).symbol)
    def apply(ts: TermSymbol) = new att(ts)
    def apply(tpe: Type) = new att(tpe.typeSymbol)
  }
}
