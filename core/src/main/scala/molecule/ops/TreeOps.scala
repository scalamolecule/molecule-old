package molecule.ops
import molecule.ast.query._
import molecule.dsl.schemaDSL._
import molecule.out.Molecule_0
import scala.reflect.macros.whitebox.Context
import scala.language.existentials

trait TreeOps[Ctx <: Context] extends Liftables[Ctx] {
  import c.universe._

  def firstLow(str: Any) = str.toString.head.toLower + str.toString.tail

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
    def isD0 = tpe <:< typeOf[Molecule_0]
    def nsS = nsString(tpe.typeSymbol.owner.owner.name.toString.init)
    def owner = t.symbol.typeSignature.typeParams.head.name.toString
    def alias = t.symbol.typeSignature.typeParams.head.name.toString
    def isAttr = tpe <:< typeOf[Attr]
    def isRef = tpe <:< weakTypeOf[Ref[_, _]]
    def isOneRef = tpe <:< weakTypeOf[OneRef[_, _]]
    def isManyRef = tpe <:< weakTypeOf[ManyRef[_, _]]
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
    case unknown  => abort(s"[TreeOps:tpe] Unknown type: $unknown")
  }

  def inputMolecule_i_o(inArity: Int, outArity: Int) = (inArity, outArity) match {
    case (1, 0)  => tq"InputMolecule_1_0"
    case (1, 1)  => tq"InputMolecule_1_1"
    case (1, 2)  => tq"InputMolecule_1_2"
    case (1, 3)  => tq"InputMolecule_1_3"
    case (1, 4)  => tq"InputMolecule_1_4"
    case (1, 5)  => tq"InputMolecule_1_5"
    case (1, 6)  => tq"InputMolecule_1_6"
    case (1, 7)  => tq"InputMolecule_1_7"
    case (1, 8)  => tq"InputMolecule_1_8"
    case (1, 9)  => tq"InputMolecule_1_9"
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

    case (2, 0)  => tq"InputMolecule_2_0"
    case (2, 1)  => tq"InputMolecule_2_1"
    case (2, 2)  => tq"InputMolecule_2_2"
    case (2, 3)  => tq"InputMolecule_2_3"
    case (2, 4)  => tq"InputMolecule_2_4"
    case (2, 5)  => tq"InputMolecule_2_5"
    case (2, 6)  => tq"InputMolecule_2_6"
    case (2, 7)  => tq"InputMolecule_2_7"
    case (2, 8)  => tq"InputMolecule_2_8"
    case (2, 9)  => tq"InputMolecule_2_9"
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

    case (3, 0)  => tq"InputMolecule_3_0"
    case (3, 1)  => tq"InputMolecule_3_1"
    case (3, 2)  => tq"InputMolecule_3_2"
    case (3, 3)  => tq"InputMolecule_3_3"
    case (3, 4)  => tq"InputMolecule_3_4"
    case (3, 5)  => tq"InputMolecule_3_5"
    case (3, 6)  => tq"InputMolecule_3_6"
    case (3, 7)  => tq"InputMolecule_3_7"
    case (3, 8)  => tq"InputMolecule_3_8"
    case (3, 9)  => tq"InputMolecule_3_9"
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

    case (i, o) => abort(s"[TreeOps:inputMolecule_i_o] Unsupported arity combination: $i in, $o out")
  }

  def molecule_o(outArity: Int) = outArity match {
    case 2  => tq"Molecule2"
    case 3  => tq"Molecule3"
    case 4  => tq"Molecule4"
    case 5  => tq"Molecule5"
    case 6  => tq"Molecule6"
    case 7  => tq"Molecule7"
    case 8  => tq"Molecule8"
    case 9  => tq"Molecule9"
    case 10 => tq"Molecule10"
    case 11 => tq"Molecule11"
    case 12 => tq"Molecule12"
    case 13 => tq"Molecule13"
    case 14 => tq"Molecule14"
    case 15 => tq"Molecule15"
    case 16 => tq"Molecule16"
    case 17 => tq"Molecule17"
    case 18 => tq"Molecule18"
    case 19 => tq"Molecule19"
    case 20 => tq"Molecule20"
    case 21 => tq"Molecule21"
    case 22 => tq"Molecule22"
    case o  => abort(s"[TreeOps:molecule_o] Unsupported arity for MoleculeX: $o")
  }

  def nodeMolecule_o(outArity: Int) = outArity match {
    case 2  => tq"NodeMolecule2"
    case 3  => tq"NodeMolecule3"
    case 4  => tq"NodeMolecule4"
    case 5  => tq"NodeMolecule5"
    case 6  => tq"NodeMolecule6"
    case 7  => tq"NodeMolecule7"
    case 8  => tq"NodeMolecule8"
    case 9  => tq"NodeMolecule9"
    case 10 => tq"NodeMolecule10"
    case 11 => tq"NodeMolecule11"
    case 12 => tq"NodeMolecule12"
    case 13 => tq"NodeMolecule13"
    case 14 => tq"NodeMolecule14"
    case 15 => tq"NodeMolecule15"
    case 16 => tq"NodeMolecule16"
    case 17 => tq"NodeMolecule17"
    case 18 => tq"NodeMolecule18"
    case 19 => tq"NodeMolecule19"
    case 20 => tq"NodeMolecule20"
    case 21 => tq"NodeMolecule21"
    case 22 => tq"NodeMolecule22"
    case o  => abort(s"[TreeOps:nodeMolecule_o] Unsupported arity for NodeMoleculeX: $o")
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
        abortTree(q"$unexpected", s"[TreeOps:nsp] Unexpected namespace symbol")
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
    val x = debug("TreeOps:att", 1)

    lazy val attrType = sym match {
      case t: TermSymbol if t.isLazy                                     => sym.typeSignature.typeSymbol.typeSignature
      case t: MethodSymbol if t.asMethod.returnType <:< weakTypeOf[Attr] => sym.asMethod.returnType
      case unexpected                                                    =>
        abortTree(q"$unexpected", s"[TreeOps:attrType] Unexpected attribute symbol")
    }

    lazy val tpe = sym match {
      case t: TermSymbol if t.isLazy && t.isPublic && t.typeSignature.typeSymbol.asType.toType <:< weakTypeOf[Ref[_, _]]        => {
        typeOf[Long]
      }
      case t: TermSymbol if t.isLazy && t.isPublic && t.typeSignature.typeSymbol.asType.toType <:< weakTypeOf[RefAttr[_, _, _]] => {
        typeOf[Long]
      }
      case t: TermSymbol if t.isLazy && t.isPublic                            => {
        val List(_, _, attrTpe) = t.typeSignature.baseType(weakTypeOf[ValueAttr[_, _, _]].typeSymbol).typeArgs
        attrTpe
      }
      case t: MethodSymbol if t.asMethod.returnType <:< weakTypeOf[Ref[_, _]] => NoType
      case unexpected                                                         =>
        abortTree(q"$unexpected", s"[TreeOps:tpe] ModelOps.att(sym) can only take an Attr symbol")
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
    def keyw = KW(ns.toString, this.toString)
    override def toString = sym.name.toString.head.toLower + sym.name.toString.tail
    def kw = KW(ns.toString, this.toString)
    def kwS = s":$ns/$name"
    // todo: if (nestedNS.isDefined) s":$ns.$nestedNS/$name" else s":$ns/$name"
    def enumPrefix = ns.enums.size match {
      case 0 => ""
      case _ => s":$ns.$name/"
    }
  }

  object att {
    def apply(attr: Tree) = new att(c.typecheck(attr).symbol)
    def apply(ts: TermSymbol) = new att(ts)
    def apply(tpe: Type) = new att(tpe.typeSymbol)
  }
}
