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
    case (1, 4) => tq"InputMolecule_1_4"
    case (1, 5) => tq"InputMolecule_1_5"
    case (1, 6) => tq"InputMolecule_1_6"
    case (1, 7) => tq"InputMolecule_1_7"
    case (1, 8) => tq"InputMolecule_1_8"
    case (1, 9) => tq"InputMolecule_1_9"
    case (1, 10) => tq"InputMolecule_1_10"
    case (1, 11) => tq"InputMolecule_1_11"
    case (1, 12) => tq"InputMolecule_1_12"
    case (1, 13) => tq"InputMolecule_1_13"
    case (1, 14) => tq"InputMolecule_1_14"
    case (1, 15) => tq"InputMolecule_1_15"
    case (1, 16) => tq"InputMolecule_1_16"
    case (1, 17) => tq"InputMolecule_1_17"
    case (1, 18) => tq"InputMolecule_1_18"
    case (1, 19) => tq"InputMolecule_1_19"
    case (1, 20) => tq"InputMolecule_1_20"
    case (1, 21) => tq"InputMolecule_1_21"
    case (1, 22) => tq"InputMolecule_1_22"

    case (2, 0) => tq"InputMolecule_2_0"
    case (2, 1) => tq"InputMolecule_2_1"
    case (2, 2) => tq"InputMolecule_2_2"
    case (2, 3) => tq"InputMolecule_2_3"
    case (2, 4) => tq"InputMolecule_2_4"
    case (2, 5) => tq"InputMolecule_2_5"
    case (2, 6) => tq"InputMolecule_2_6"
    case (2, 7) => tq"InputMolecule_2_7"
    case (2, 8) => tq"InputMolecule_2_8"
    case (2, 9) => tq"InputMolecule_2_9"
    case (2, 10) => tq"InputMolecule_2_10"
    case (2, 11) => tq"InputMolecule_2_11"
    case (2, 12) => tq"InputMolecule_2_12"
    case (2, 13) => tq"InputMolecule_2_13"
    case (2, 14) => tq"InputMolecule_2_14"
    case (2, 15) => tq"InputMolecule_2_15"
    case (2, 16) => tq"InputMolecule_2_16"
    case (2, 17) => tq"InputMolecule_2_17"
    case (2, 18) => tq"InputMolecule_2_18"
    case (2, 19) => tq"InputMolecule_2_19"
    case (2, 20) => tq"InputMolecule_2_20"
    case (2, 21) => tq"InputMolecule_2_21"
    case (2, 22) => tq"InputMolecule_2_22"

    case (3, 0) => tq"InputMolecule_3_0"
    case (3, 1) => tq"InputMolecule_3_1"
    case (3, 2) => tq"InputMolecule_3_2"
    case (3, 3) => tq"InputMolecule_3_3"
    case (3, 4) => tq"InputMolecule_3_4"
    case (3, 5) => tq"InputMolecule_3_5"
    case (3, 6) => tq"InputMolecule_3_6"
    case (3, 7) => tq"InputMolecule_3_7"
    case (3, 8) => tq"InputMolecule_3_8"
    case (3, 9) => tq"InputMolecule_3_9"
    case (3, 10) => tq"InputMolecule_3_10"
    case (3, 11) => tq"InputMolecule_3_11"
    case (3, 12) => tq"InputMolecule_3_12"
    case (3, 13) => tq"InputMolecule_3_13"
    case (3, 14) => tq"InputMolecule_3_14"
    case (3, 15) => tq"InputMolecule_3_15"
    case (3, 16) => tq"InputMolecule_3_16"
    case (3, 17) => tq"InputMolecule_3_17"
    case (3, 18) => tq"InputMolecule_3_18"
    case (3, 19) => tq"InputMolecule_3_19"
    case (3, 20) => tq"InputMolecule_3_20"
    case (3, 21) => tq"InputMolecule_3_21"
    case (3, 22) => tq"InputMolecule_3_22"

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
    case 9 => tq"Out9[..$outTypes]"
    case 10 => tq"Out10[..$outTypes]"
    case 11 => tq"Out11[..$outTypes]"
    case 12 => tq"Out12[..$outTypes]"
    case 13 => tq"Out13[..$outTypes]"
    case 14 => tq"Out14[..$outTypes]"
    case 15 => tq"Out15[..$outTypes]"
    case 16 => tq"Out16[..$outTypes]"
    case 17 => tq"Out17[..$outTypes]"
    case 18 => tq"Out18[..$outTypes]"
    case 19 => tq"Out19[..$outTypes]"
    case 20 => tq"Out20[..$outTypes]"
    case 21 => tq"Out21[..$outTypes]"
    case 22 => tq"Out22[..$outTypes]"
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
