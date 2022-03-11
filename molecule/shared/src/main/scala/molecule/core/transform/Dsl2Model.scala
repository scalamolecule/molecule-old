package molecule.core.transform

import molecule.core.ast.elements._
import molecule.core.dsl.attributes._
import molecule.core.exceptions.MoleculeException
import molecule.core.generic.AEVT._
import molecule.core.generic.AVET._
import molecule.core.generic.EAVT._
import molecule.core.generic.Log._
import molecule.core.generic.Schema._
import molecule.core.generic.VAET._
import molecule.core.generic._
import molecule.core.macros.rowAttr._
import molecule.core.macros.rowConverters.{Row2jsonFlat, Row2jsonNested, Row2jsonOptNested, Row2obj, Row2tplComposite, Row2tplFlat, Row2tplNested, Row2tplOptNested}
import molecule.core.marshalling.nodes._
import molecule.core.marshalling.unpackAttr.PackedValue2json
import molecule.core.marshalling.unpackers._
import molecule.core.ops.{TreeOps, VerifyRawModel}
import molecule.core.transform.exception.Dsl2ModelException
import scala.annotation.tailrec
import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.util.{Try => Check}


private[molecule] trait Dsl2Model extends TreeOps
  with Row2tplFlat
  with Row2tplComposite
  with Row2tplNested
  with Row2tplOptNested

  with Row2obj

  with Row2jsonFlat
  with Row2jsonNested
  with Row2jsonOptNested

  with Packed2tplFlat
  with Packed2tplNested
  with Packed2tplComposite

  with Packed2jsonFlat
  with Packed2jsonNested

  with RowValue2cast
  with RowValue2castOptNested

  with RowValue2json
  with PackedValue2json
  with RowValue2jsonOptNested

  with CastTypes
  with CastAggr
  with CastOptNested

  with JsonTypes
  with JsonAggr
  with JsonOptNested {

  val c: blackbox.Context

  import c.universe._

  //  private lazy val xy = InspectMacro("Dsl2Model", 540, 550, mkError = true)
  private lazy val xx = InspectMacro("Dsl2Model", 901, 900)

  protected val isJsPlatform: Boolean = Check(getClass.getClassLoader.loadClass("scala.scalajs.js.Any")).isSuccess

  override def abort(msg: String): Nothing = throw Dsl2ModelException(msg)

  private[molecule] final def getModel(dsl: Tree): (
    List[Tree],
      Model,
      List[List[Tree]],
      List[List[Int => Tree]],
      Obj,
      List[String],
      Boolean,
      Int,
      List[(Int, Int) => Tree],
      Boolean,
      List[List[Int]],
      List[List[Int]],
      Boolean
    ) = {

    lazy val numberTypes      = Seq("Int", "Long", "Float", "Double", "BigInt", "BigDecimal")
    lazy val mandatoryGeneric = Seq("e", "tx", "t", "txInstant", "op", "a", "v", "Self")
    lazy val tacitGeneric     = Seq("e_", "ns_", "a_", "v_", "tx_", "t_", "txInstant_", "op_")
    lazy val datomGeneric     = Seq(
      "e", "e_", "tx", "t", "txInstant", "op", "tx_", "t_", "txInstant_", "op_", "a", "a_", "v", "v_"
    )
    lazy val keywords         = Seq(
      "$qmark", "Nil", "None", "count", "countDistinct", "min", "max", "sum", "avg",
      "unify", "distinct", "median", "variance", "stddev", "rand", "sample"
    )

    def badFn(fn: String) = List(
      "countDistinct", "distinct", "max", "min", "rand", "sample", "avg", "median", "stddev", "sum", "variance"
    ).contains(fn)

    var txMetaDataStarted       : Boolean = false
    var txMetaDataDone          : Boolean = false
    var txMetas                 : Int     = 0
    var objCompositesCount      : Int     = 0
    var isComposite             : Boolean = false
    var collectCompositeElements: Boolean = false

    var post     : Boolean                  = true
    var postJsons: List[(Int, Int) => Tree] = List.empty[(Int, Int) => Tree]

    var isOptNestedCheck: Boolean = false
    var isOptNested     : Boolean = dsl.toString.contains(".*?[")

    var optNestedLevel       : Int             = 0
    var optNestedRefIndexes  : List[List[Int]] = List(List.empty[Int])
    var optNestedTacitIndexes: List[List[Int]] = List(List.empty[Int])

    var genericImports: List[Tree] = List(q"import molecule.core.generic.Datom._")

    var typess: List[List[Tree]]        = List(List.empty[Tree])
    var castss: List[List[Int => Tree]] = List(List.empty[Int => Tree])

    var obj     : Obj = Obj("", "", nested = false, Nil)
    var objLevel: Int = 0

    var tx        : String       = ""
    var nestedRefs: List[String] = List.empty[String]

    var hasVariables: Boolean = false
    var standard    : Boolean = true
    var aggrType    : String  = ""
    var aggrFn      : String  = ""

    var first      : Boolean = true
    var genericType: String  = "datom"

    var sortMarker : String          = ""
    var sortIndexes: List[List[Int]] = List(List.empty[Int])

    def getType(t: richTree): Tree = {
      val typeName = TypeName(t.tpeS)
      if (t.name.last == '$')
        t.card match {
          case 1 => tq"Option[$typeName]"
          case 2 => tq"Option[Set[$typeName]]"
          case 3 => tq"Option[Map[String, $typeName]]"
          case 4 => tq"Option[$typeName]"
        }
      else
        t.card match {
          case 1 => tq"$typeName"
          case 2 => tq"Set[$typeName]"
          case 3 => tq"Map[String, $typeName]"
          case 4 => tq"$typeName"
        }
    }

    def addProp(
      t: richTree,
      baseTpe0: Option[String],
      group0: Option[String],
      optAggr: Option[(String, Tree)],
    ): Unit = {
      val aggrTpe = optAggr.map(_._2.toString)
      val cls     = t.nsFull + "_" + t.name.replace('$', '_')
      val baseTpe = baseTpe0.getOrElse(t.tpeS)
      val group   = group0 match {
        case None             => t.name.last match {
          case '$' => t.card match {
            case 1 => "OptOne"
            case 2 => "OptMany"
            case 3 => "OptMap"
          }
          case _   => t.card match {
            case 1 => "One"
            case 2 => "Many"
            case 3 => "Map"
          }
        }
        case Some("OptApply") => t.card match {
          case 1 => "OptApplyOne"
          case 2 => "OptApplyMany"
          case 3 => "OptApplyMap"
        }
        case Some(gr)         => gr
      }
      obj = addNode(obj, Prop(cls, t.name, baseTpe, t.card, group, aggrTpe), objLevel)

      //xx(803, t.name, obj)
    }


    def addSpecific(
      t: richTree,
      cast: Int => Tree,
      json: (Int, Int) => Tree,
      optTpe: Option[Tree] = None,
      doAddProp: Boolean = true,
      optAggr: Option[(String, Tree)] = None,
      group0: Option[String] = None,
      baseTpe0: Option[String] = None
    ): Unit = {
      val tpe = optTpe.getOrElse(optAggr match {
        case Some((_, aggrTpe)) => aggrTpe
        case None               => getType(t)
      })
      if (post)
        postJsons = json :: postJsons
      typess = (tpe :: typess.head) :: typess.tail
      castss = (cast :: castss.head) :: castss.tail
      if (doAddProp)
        addProp(t, baseTpe0, group0, optAggr)
    }

    def addLambdas(
      t: richTree,
      cast: richTree => Int => Tree,
      json: richTree => (Int, Int) => Tree,
      group0: Option[String] = None,
      baseTpe0: Option[String] = None
    ): Unit = {
      if (t.name.last != '_') {
        if (post)
          postJsons = json(t) :: postJsons
        typess = (getType(t) :: typess.head) :: typess.tail
        castss = (cast(t) :: castss.head) :: castss.tail
        addProp(t, baseTpe0, group0, None)
      }
    }

    def getSort = if (sortMarker.nonEmpty) {
      val sort = sortMarker
      // Reset current sort marker
      sortMarker = ""
      sort
    } else ""


    // Traverse ---------------------------------------------------------------------------------------------------

    def traverseElement(prev: Tree, p: richTree, element: Element): Seq[Element] = {
      if (p.isNS && !p.isFirstNS) {
        //xx(711, prev, p, element, typess, castss, p.isFirstNS, p.tpe_, prev.isFirstNS)
        resolve(q"$prev") :+ element
      } else {
        //xx(710, element)
        // First element
        Seq(element)
      }
    }

    def traverseElements(prev: Tree, p: richTree, elements: Seq[Element]): Seq[Element] = {
      if (isComposite) {
        //xx(640, prev, elements, obj)
        val prevElements = resolve(q"$prev")
        if (collectCompositeElements) {
          val result = prevElements :+ Composite(elements)
          //xx(641, prevElements, elements, result, collectCompositeElements, castss, typess, obj)
          result
        } else {
          val result = Seq(Composite(prevElements), Composite(elements))
          levelCompositeObj(result)
          //xx(642, prevElements, elements, result, collectCompositeElements, castss, typess, obj)
          result
        }
      } else {
        if (p.isNS && !p.isFirstNS) {
          //xx(751, elements)
          resolve(q"$prev") ++ elements
        } else {
          //xx(752, elements)
          // First elements
          elements
        }
      }
    }

    @tailrec
    def resolve(tree: Tree, forceNotOptNested: Boolean = false): Seq[Element] = {
      if (forceNotOptNested)
        isOptNested = false

      if (first) {
        val p = richTree(tree)
        if (p.tpe_ <:< typeOf[GenericNs]) p.tpe_ match {
          case t if t <:< typeOf[Schema] =>
            genericType = "schema"
            genericImports = genericImports :+ q"import molecule.core.generic.Schema._"

          case t if t <:< typeOf[EAVT] =>
            genericType = "eavt"
            genericImports = genericImports :+ q"import molecule.core.generic.EAVT._"

          case t if t <:< typeOf[AEVT] =>
            genericType = "aevt"
            genericImports = genericImports :+ q"import molecule.core.generic.AEVT._"

          case t if t <:< typeOf[AVET] =>
            genericType = "avet"
            genericImports = genericImports :+ q"import molecule.core.generic.AVET._"

          case t if t <:< typeOf[VAET] =>
            genericType = "vaet"
            genericImports = genericImports :+ q"import molecule.core.generic.VAET._"

          case t if t <:< typeOf[Log] =>
            genericType = "log"
            genericImports = genericImports :+ q"import molecule.core.generic.Log._"
        }
        first = false
        //xx(99, p.tpe_, genericType)
      }


      def disAllowSortMarker(attr: String, args: Seq[Tree] = Nil): Unit = {
        //xx(201, attr, args)
        if (args.nonEmpty && sortMarker.nonEmpty) {
          args.head match {
            case q"$_.$aggrFn" if List("rand", "sample").contains(aggrFn.toString) =>
              abort(s"Sorting by random/sample values not supported. Found: $attr($aggrFn).$sortMarker")

            case q"$_.distinct" =>
              abort(s"Can't sort list of distinct values. Found: $attr(distinct).$sortMarker")

            case q"$_.$aggrFn.apply($limit)" if List("min", "max", "rand", "sample", "distinct").contains(aggrFn.toString) =>
              abort(s"Can't sort list of values from attributes with aggregate min/max/rand/sample with applied limit. " +
                s"Found: $attr($aggrFn($limit)).$sortMarker")

            case _ => ()
          }
        } else {
          attr match {
            case r"[ad][1-5]" => abort(s"Sort marker `$attr` can't have any operation applied.")
            case _            => ()
          }
        }
      }

      tree match {
        case q"$prev.$attr" =>
          //xx(100, prev, attr, sortIndexes, typess)
          attr.toString match {
            case at@r"[ad]([1-5])$i" =>
              val sortIndex = i.toInt
              //xx(101, prev, attr, sortIndexes, typess, sortIndex, sortIndexes.head)
              if (sortIndexes.head.contains(sortIndex)) {
                abort(s"Please use unique sorting markers. Can't use `$attr` with order $sortIndex twice.")
              } else {
                sortMarker = at
                sortIndexes = (sortIndex :: sortIndexes.head) +: sortIndexes.tail
                resolve(tree = q"$prev")
              }

            case attrStr =>
              //xx(102, prev, attr, sortIndexes)
              resolveAttr(tree, richTree(tree), q"$prev", richTree(q"$prev"), attrStr)
          }

        case q"$prev.$attr.apply(..$args)" =>
          //xx(200, attr, args)
          disAllowSortMarker(attr.toString, args.asInstanceOf[Seq[Tree]])
          resolveApply(tree, richTree(q"$prev.$attr"), q"$prev", richTree(q"$prev"), attr.toString(), q"$args")

        case q"$prev.$attr.apply[..$_](..$_)" =>
          //xx(300, attr)
          disAllowSortMarker(attr.toString)
          resolveTypedApply(tree, richTree(q"$prev"))

        case q"$_.$_(..$_)" =>
          //xx(400, tree)
          resolveOperation(tree)

        case q"$prev.$manyRef.*[..$_]($nested)" =>
          //xx(500, prev, manyRef, nested, sortIndexes, q"$prev", richTree(q"$prev"), TermName(manyRef.toString), q"$nested")
          resolveNested(q"$prev", richTree(q"$prev"), TermName(manyRef.toString), q"$nested")

        case q"$prev.$manyRef.*?[..$_]($nested)" =>
          //xx(501, manyRef)
          isOptNestedCheck = true
          resolveOptNested(q"$prev", richTree(q"$prev"), TermName(manyRef.toString), q"$nested")

        case q"$prev.+[..$_]($subComposite)" =>
          //xx(600, prev, subComposite, obj)
          resolveComposite(q"$prev", richTree(q"$prev"), q"$subComposite")

        case other => abort(s"Unexpected DSL structure: $other\n${showRaw(other)}")
      }
    }

    def levelCompositeObj(subCompositeElements: Seq[Element]): Unit = {
      //xx(630, subCompositeElements, objCompositesCount)
      lazy val err = "Unexpectedly couldn't find ns in sub composite:\n  " + subCompositeElements.mkString("\n  ")
      val ns    = subCompositeElements.collectFirst {
        case Atom(ns, _, _, _, _, _, _, _, _) => ns
        case Bond(nsFull, _, _, _, _)         => nsFull
        case Composite(elements)              => elements.collectFirst {
          case Atom(ns, _, _, _, _, _, _, _, _) => ns
          case Bond(nsFull, _, _, _, _)         => nsFull
        } getOrElse abort(err)
      } getOrElse abort(err)
      val nsCls = ns + "_"

      // Prepend namespace in obj
      val newBuilderNodes: List[Node] = if (objCompositesCount > 0) {
        val (props, compositeProps) = obj.props.splitAt(obj.props.length - objCompositesCount)

        compositeProps.head match {
          case Obj("Tx_", _, _, _) =>
            // Reset obj composites count
            objCompositesCount = 0
            List(Obj(nsCls, ns, nested = false, props ++ compositeProps))

          case _ => Obj(nsCls, ns, nested = false, props) :: compositeProps
        }
      } else {
        //xx(632, typess, objCompositesCount, obj, obj.copy(props = List(Obj(nsCls, ns, nested = false, obj.props))))
        List(Obj(nsCls, ns, nested = false, obj.props))
      }

      obj = obj.copy(props = newBuilderNodes)
      //xx(633, ns, obj)
    }

    def resolveComposite(prev: Tree, p: richTree, subCompositeTree: Tree): Seq[Element] = {
      //xx(620, prev, subCompositeTree, obj)
      post = false
      isComposite = true
      collectCompositeElements = false
      val subCompositeElements = resolve(subCompositeTree)
      // Make sure we continue to collect composite elements
      collectCompositeElements = false
      //xx(621, prev, subCompositeElements, typess, castss, obj)

      // Start new level
      typess = List.empty[Tree] :: typess
      castss = List.empty[Int => Tree] :: castss
      sortIndexes = List.empty[Int] :: sortIndexes

      if (txMetaDataStarted)
        txMetas = if (txMetas == 0) 2 else txMetas + 1

      // Make composite in obj
      levelCompositeObj(subCompositeElements)
      objCompositesCount += 1

      //xx(622, prev, subCompositeElements, typess, castss, txMetas, obj, objCompositesCount)
      val elements = traverseElements(prev, p, subCompositeElements)
      collectCompositeElements = true
      //xx(623, prev, subCompositeElements, typess, castss, elements, txMetas, obj)
      elements
    }

    def resolveAttr(tree: Tree, t: richTree, prev: Tree, p: richTree, attrStr: String): Seq[Element] = attrStr.last match {
      case '$' =>
        //xx(140, attrStr)
        resolveOptionalAttr(tree, t, q"$prev", p, attrStr)

      case '_' =>
        //xx(160, attrStr)
        if (sortMarker.nonEmpty)
          abort(s"Can't sort by tacit values. Found: $attrStr.$sortMarker")
        resolveTacitAttr(tree, t, q"$prev", p, attrStr)

      case _ =>
        //xx(110, attrStr)
        resolveMandatoryAttrOrRef(tree, t, q"$prev", p, attrStr)
    }


    def resolveMandatoryAttrOrRef(tree: Tree, t: richTree, prev: Tree, p: richTree, attrStr: String): Seq[Element] = {
      if (genericType == "datom" && mandatoryGeneric.contains(attrStr)) {
        //xx(111, attrStr, t.tpeS)
        resolveMandatoryGenericAttr(t, q"$prev", p, attrStr)

      } else if (genericType == "schema") {
        resolveMandatorySchemaAttr(t, q"$prev", p, attrStr)

      } else if (genericType != "datom") { // Indexes
        //xx(120, genericType, attrStr)
        resolveMandatoryGenericAttr(t, q"$prev", p, attrStr)

      } else if (t.isEnum) {
        //xx(131, t.tpeS)
        if (isOptNested)
          addSpecific(t, castOptNestedEnum(t), jsonOptNestedEnum(t), baseTpe0 = Some("enum"))
        else
          addSpecific(t, castEnum(t), jsonEnum(t), baseTpe0 = Some("enum"))
        traverseElement(prev, p, Atom(t.nsFull, t.name, t.tpeS, t.card, EnumVal, Some(t.enumPrefix), bi(tree, t), sort = getSort))

      } else if (t.isMapAttr) {
        //xx(132, t.tpeS)
        if (isOptNested)
          addLambdas(t, castOptNestedMapAttr, jsonOptNestedMapAttr)
        else
          addLambdas(t, castMapAttr, jsonMapAttr)
        traverseElement(prev, p, Atom(t.nsFull, t.name, t.tpeS, 3, VarValue, None, bi(tree, t)))

      } else if (t.isValueAttr) {
        //xx(133, t.tpeS, t.nsFull, t.name, post, isOptNested, optNestedLevel)
        if (isOptNested)
          addLambdas(t, castOptNestedAttr, jsonOptNestedAttr)
        else
          addLambdas(t, castAttr, jsonAttr)
        traverseElement(prev, p, Atom(t.nsFull, t.name, t.tpeS, t.card, VarValue, gvs = bi(tree, t), sort = getSort))

      } else if (attrStr.head == '_') {
        //xx(134, attrStr.tail)
        obj = addNode(obj, Obj("", "", nested = false, Nil), objLevel)
        objLevel += 1
        //xx(151, obj)
        traverseElement(prev, p, ReBond(attrStr.tail))

      } else if (t.isRef) {
        //xx(135, t.tpeS, t.card, t.refCard, t.refThis, t.refNext, obj)
        // Prepend ref in obj
        val refName = t.name.capitalize
        val refCls  = t.nsFull + "__" + refName
        if (objCompositesCount > 0) {
          val (props, compositeProps) = obj.props.splitAt(obj.props.length - objCompositesCount)
          val newProps                = Obj(refCls, refName, nested = false, props) :: compositeProps
          obj = obj.copy(props = newProps)
          //xx(152, props, compositeProps, newProps, obj)

        } else if (txMetaDataDone) {
          obj.props.last match {
            case txMetaObj@Obj("Tx_", _, _, _) =>
              obj = obj.copy(props = List(Obj(refCls, refName, nested = false, obj.props.init), txMetaObj))
            case _                             =>
              obj = obj.copy(props = List(Obj(refCls, refName, nested = false, obj.props)))
          }
          objLevel = 0
          //xx(153, objLevel, isComposite, refCls, obj)

        } else {
          obj = addRef(obj, refCls, refName, nested = false, objLevel)

          //xx(154, objLevel, isComposite, refCls, obj)
          objLevel = (objLevel - 1).max(0)
        }
        //xx(155, objLevel, isComposite, objCompositesCount, refCls, obj)
        traverseElement(prev, p, Bond(t.refThis, firstLow(attrStr), t.refNext, t.refCard, bi(tree, t)))

      } else if (t.isRefAttr) {
        //xx(136, t.tpeS)
        if (isOptNested)
          addLambdas(t, castOptNestedRefAttr, jsonOptNestedRefAttr, baseTpe0 = Some("ref"))
        else
          addLambdas(t, castAttr, jsonAttr, baseTpe0 = Some("ref"))
        traverseElement(prev, p, Atom(t.nsFull, t.name, "ref", t.card, VarValue, gvs = bi(tree, t), sort = getSort))

      } else {
        abort("Unexpected mandatory attribute/reference: " + t)
      }
    }


    def resolveOptionalAttr(tree: Tree, t: richTree, prev: Tree, p: richTree, attrStr: String): Seq[Element] = {
      if (genericType == "schema") {
        //xx(112, genericType, attrStr)
        resolveOptionalSchemaAttr(t, q"$prev", p, attrStr)

      } else if (t.isEnum$) {
        //xx(141, t.tpeS, optNestedLevel)
        if (isOptNested)
          addLambdas(t, castOptNestedOptEnum, jsonOptNestedOptEnum, baseTpe0 = Some("enum"))
        else
          addLambdas(t, castOptEnum, jsonOptEnum, baseTpe0 = Some("enum"))
        traverseElement(prev, p, Atom(t.nsFull, t.name, t.tpeS, t.card, EnumVal, Some(t.enumPrefix), bi(tree, t), sort = getSort))

      } else if (t.isMapAttr$) {
        //xx(142, t.tpeS)
        if (isOptNested)
          addLambdas(t, castOptNestedOptMapAttr, jsonOptNestedOptMapAttr)
        else
          addLambdas(t, castOptMapAttr, jsonOptMapAttr)
        traverseElement(prev, p, Atom(t.nsFull, t.name, t.tpeS, 3, VarValue, None, bi(tree, t)))

      } else if (t.isValueAttr$) {
        //xx(143, t.tpeS, typess, optNestedLevel, isOptNested)
        if (isOptNested)
          addLambdas(t, castOptNestedOptAttr, jsonOptNestedOptAttr)
        else
          addLambdas(t, castOptAttr, jsonOptAttr)
        traverseElement(prev, p, Atom(t.nsFull, t.name, t.tpeS, t.card, VarValue, gvs = bi(tree, t), sort = getSort))

      } else if (t.isRefAttr$) {
        //xx(144, t.tpeS)
        if (isOptNested)
          addLambdas(t, castOptNestedOptRefAttr, jsonOptNestedOptRefAttr, baseTpe0 = Some("ref"))
        else
          addLambdas(t, castOptRefAttr, jsonOptRefAttr, baseTpe0 = Some("ref"))
        traverseElement(prev, p, Atom(t.nsFull, t.name, "ref", t.card, VarValue, gvs = bi(tree, t), sort = getSort))

      } else {
        abort("Unexpected optional attribute: " + t)
      }
    }


    def resolveTacitAttr(tree: Tree, t: richTree, prev: Tree, p: richTree, attrStr: String): Seq[Element] = {
      if (genericType == "datom" && tacitGeneric.contains(attrStr)) {
        abort(s"Tacit `$attrStr` can only be used with an applied value i.e. `$attrStr(<value>)`")

      } else if (genericType == "schema") {
        traverseElement(prev, p, Generic("Schema", attrStr, "schema", NoValue))

      } else if (t.isEnum) {
        if (optNestedLevel > 0)
          castss = (castOptNestedOptEnum(t) :: castss.head) :: castss.tail
        traverseElement(prev, p, Atom(t.nsFull, t.name, t.tpeS, t.card, EnumVal, Some(t.enumPrefix), bi(tree, t)))

      } else if (t.isMapAttr) {
        if (optNestedLevel > 0)
          castss = (castOptNestedOptMapAttr(t) :: castss.head) :: castss.tail
        traverseElement(prev, p, Atom(t.nsFull, t.name, t.tpeS, 3, VarValue, None, bi(tree, t)))

      } else if (t.isValueAttr) {
        if (optNestedLevel > 0)
          castss = (castOptNestedOptAttr(t) :: castss.head) :: castss.tail
        traverseElement(prev, p, Atom(t.nsFull, t.name, t.tpeS, t.card, VarValue, gvs = bi(tree, t)))

      } else if (t.isRefAttr) {
        if (optNestedLevel > 0)
          castss = (castOptNestedOptRefAttr(t) :: castss.head) :: castss.tail
        traverseElement(prev, p, Atom(t.nsFull, t.name, "ref", t.card, VarValue, gvs = bi(tree, t)))

      } else {
        abort("Unexpected tacit attribute: " + t)
      }
    }

    def resolveMandatoryGenericAttr(t: richTree, prev: Tree, p: richTree, attrStr: String): Seq[Element] = {
      val genericNs = genericType match {
        case "schema" => "Schema"
        case "eavt"   => "EAVT"
        case "aevt"   => "AEVT"
        case "avet"   => "AVET"
        case "vaet"   => "VAET"
        case "log"    => "Log"
        case _        => p.nsFull2
      }
      def castGeneric(tpe: String, value: Value): Seq[Element] = {
        val tpeOrAggrTpe = if (aggrType.nonEmpty) aggrType else tpe
        addSpecific(
          t,
          castOneAttr(tpeOrAggrTpe),
          if (isOptNested) jsonOptNestedOneAttr(tpeOrAggrTpe, attrStr) else jsonOneAttr(tpeOrAggrTpe, attrStr),
          Some(tq"${TypeName(tpeOrAggrTpe)}")
        )
        traverseElement(prev, p, Generic(genericNs, attrStr, genericType, value, getSort))
      }
      val elements = attrStr match {
        case "e"    => castGeneric("Long", EntValue)
        case "v"    => castGeneric("Any", NoValue)
        case "a"    => castGeneric("String", NoValue)
        case "Self" =>
          val cls = t.nsFull + "_"
          obj = addRef(obj, cls, t.nsFull, nested = false, objLevel)
          objLevel = (objLevel - 1).max(0)
          traverseElement(prev, p, Self)
        case tx     =>
          if (prev.toString.endsWith("$"))
            abort(s"Optional attributes (`${p.name}`) can't be followed by generic transaction attributes (`$attrStr`).")
          tx match {
            case "t"         => castGeneric("Long", NoValue)
            case "tx"        => castGeneric("Long", NoValue)
            case "txInstant" => castGeneric("Date", NoValue)
            case "op"        => castGeneric("Boolean", NoValue)
          }
      }
      //xx(113, attrStr, genericType, p.nsFull, p.nsFull2, obj)
      elements
    }


    def resolveMandatorySchemaAttr(t: richTree, prev: Tree, p: richTree, attrStr: String): Seq[Element] = {
      def castGeneric(tpe: String): Seq[Element] = {
        val tpeOrAggrTpe = if (aggrType.nonEmpty) aggrType else tpe
        addSpecific(
          t,
          castOneAttr(tpeOrAggrTpe),
          if (isOptNested) jsonOptNestedOneAttr(tpeOrAggrTpe, attrStr) else jsonOneAttr(tpeOrAggrTpe, attrStr),
          Some(tq"${TypeName(tpeOrAggrTpe)}")
        )
        traverseElement(prev, p, Generic("Schema", attrStr, "schema", NoValue, getSort))
      }
      //xx(122, attrStr, p.nsFull, p.nsFull2)
      attrStr match {
        case "attrId"      => castGeneric("Long")
        case "a"           => castGeneric("String")
        case "part"        => castGeneric("String")
        case "nsFull"      => castGeneric("String")
        case "ns"          => castGeneric("String")
        case "attr"        => castGeneric("String")
        case "enumm"       => castGeneric("String")
        case "ident"       => castGeneric("String")
        case "valueType"   => castGeneric("String")
        case "cardinality" => castGeneric("String")
        case "doc"         => castGeneric("String")
        case "unique"      => castGeneric("String")
        case "isComponent" => castGeneric("Boolean")
        case "noHistory"   => castGeneric("Boolean")
        case "index"       => castGeneric("Boolean")
        case "fulltext"    => castGeneric("Boolean")
        case "t"           => castGeneric("Long")
        case "tx"          => castGeneric("Long")
        case "txInstant"   => castGeneric("Date")
      }
    }


    def resolveOptionalSchemaAttr(t: richTree, prev: Tree, p: richTree, attrStr: String): Seq[Element] = attrStr match {
      case "ident$" =>
        addLambdas(t, castOptIdent, jsonOptEnum)
        traverseElement(prev, p, Generic("Schema", attrStr, "schema", NoValue, getSort))

      case "valueType$" | "cardinality$" | "unique$" =>
        addLambdas(t, castOptEnum, if (isOptNested) jsonOptNestedOptEnum else jsonOptEnum, baseTpe0 = Some("enum"))
        traverseElement(prev, p, Generic("Schema", attrStr, "schema", NoValue, getSort))

      case _ =>
        addLambdas(t, castOptAttr, if (isOptNested) jsonOptNestedOptAttr else jsonOptAttr)
        traverseElement(prev, p, Generic("Schema", attrStr, "schema", NoValue, getSort))
    }


    def resolveApply(tree: Tree, t: richTree, prev: Tree, p: richTree, attrStr: String, args: Tree): Seq[Element] = {
      if (t.isFirstNS) {
        //xx(230, attrStr, genericType, args)
        tree match {
          case q"$prev.$nsFull.apply($_.?)" =>
            traverseElement(q"$prev", p,
              Generic(nsFull.toString(), "e_", genericType, Eq(Seq(Qm)), getSort))

          case q"$prev.$nsFull.apply($eid)" if t.isBiEdge =>
            traverseElement(q"$prev", p,
              Generic(nsFull.toString(), "e_", genericType, Eq(Seq(extract(q"$eid"))), getSort))

          case q"$prev.$nsFull.apply(..$eids)" if genericType != "datom" =>
            traverseElement(q"$prev", p,
              Generic(nsFull.toString(), "args_", genericType, Eq(resolveValues(q"Seq(..$eids)")), getSort))

          case q"$prev.$nsFull.apply(..$eids)" =>
            traverseElement(q"$prev", p,
              Generic(nsFull.toString(), "e_", genericType, Eq(resolveValues(q"Seq(..$eids)")), getSort))

        }

      } else if (genericType == "datom" && datomGeneric.contains(attrStr)) {
        //xx(240, attrStr, genericType)
        resolveApplyGeneric(prev, p, attrStr, args)

      } else if (genericType != "datom") {
        //xx(250, genericType, attrStr)
        genericType match {
          case "schema" => resolveApplySchema(t, q"$prev", p, attrStr, args)

          case "log" => abort("Log attributes not allowed to have values applied.\n" +
            "Log only accepts range arguments: `Log(from, until)`.")

          case "eavt" => abort("EAVT index attributes not allowed to have values applied.\n" +
            "EAVT index only accepts datom arguments: `EAVT(<e/a/v/t>)`.")

          case "aevt" => abort("AEVT index attributes not allowed to have values applied.\n" +
            "AEVT index only accepts datom arguments: `AEVT(<a/e/v/t>)`.")

          case "avet" => abort("AVET index attributes not allowed to have values applied.\n" +
            "AVET index only accepts datom arguments: `AVET(<a/v/e/t>)` or range arguments: `AVET.range(a, from, until)`.")

          case "vaet" => abort("VAET index attributes not allowed to have values applied.\n" +
            "VAET index only accepts datom arguments: `VAET(<v/a/e/t>)`.")
        }

      } else if (tree.isMapAttrK) {
        tree match {
          case t1@q"$prev1.$mapAttr.apply($key)" =>
            val tpeStr = truncTpe(t1.tpe.baseType(weakTypeOf[One[_, _, _]].typeSymbol).typeArgs.last.toString)
            val nsFull = new nsp(t1.tpe.typeSymbol.owner).toString
            //xx(260, attrStr, tpeStr)
            if (attrStr.last != '_') {
              addSpecific(
                p,
                castKeyedMapAttr(tpeStr),
                if (isOptNested) jsonOptNestedKeyedMapAttr(tpeStr, attrStr) else jsonKeyedMapAttr(tpeStr, attrStr),
                Some(tq"${TypeName(tpeStr)}"),
                doAddProp = false,
                group0 = Some("KeyedMap")
              )
              // Have to add node manually since nsFull is resolved in a special way
              val cls     = nsFull + "_" + attrStr
              val newProp = Prop(cls, attrStr, tpeStr, 4, "KeyedMap")
              obj = addNode(obj, newProp, objLevel)
            }
            traverseElement(q"$prev1", p, Atom(nsFull, mapAttr.toString, tpeStr, 4, VarValue, None, Nil, Seq(extract(q"$key").toString)))
        }

      } else {
        tree match {
          case q"$_.$ref.apply(..$_)" if t.isRef =>
            abort(s"Can't apply value to a reference (`$ref`)")
          case q"$prev.$attr.apply(..$values)"   =>
            //xx(270, attrStr, values)
            traverseElement(q"$prev", p,
              resolveOp(q"$prev.$attr", richTree(q"$prev.$attr"), attr.toString, q"apply", q"Seq(..$values)"))
        }
      }
    }

    def resolveApplySchema(t: richTree, prev: Tree, p: richTree, attrStr: String, args: Tree) = {
      def resolve(value: Value, aggrType: String = ""): Seq[Element] = {
        def casts(mode: String, tpe: String): Seq[Element] = {
          mode match {
            case "mandatory" =>
              //xx(251, "aggrType: " + aggrType, attrStr)
              if (aggrType == "Int2") {
                // Count of generic attribute values
                addSpecific(
                  t,
                  castOneAttr("Int"),
                  if (isOptNested) jsonOptNestedOneAttr("Int", attrStr) else jsonOneAttr("Int", attrStr),
                  Some(tq"${TypeName("Int")}"),
                  doAddProp = false
                )
                val cls     = "Schema_" + attrStr
                val newProp = Prop(cls, attrStr, tpe, 1, "One", Some("Int"))
                obj = addNode(obj, newProp, objLevel)
                traverseElement(q"$prev", p, Generic("Schema", attrStr, "schema", value, getSort))
              } else if (aggrType.nonEmpty) {
                // Aggregate
                addSpecific(
                  t,
                  castOneAttr(aggrType),
                  if (isOptNested) jsonOptNestedOneAttr(aggrType, attrStr) else jsonOneAttr(aggrType, attrStr),
                  Some(tq"${TypeName(aggrType)}")
                )
                traverseElement(q"$prev", p, Generic("Schema", attrStr, "schema", value, getSort))
              } else {
                // Clean/comparison
                addSpecific(
                  t,
                  castOneAttr(tpe),
                  if (isOptNested) jsonOptNestedOneAttr(tpe, attrStr) else jsonOneAttr(tpe, attrStr),
                  Some(tq"${TypeName(tpe)}")
                )
                traverseElement(q"$prev", p, Generic("Schema", attrStr, "schema", value, getSort))
              }
            case "tacit"     =>
              if (aggrType.isEmpty) {
                traverseElement(q"$prev", p, Generic("Schema", attrStr, "schema", value, getSort))
              } else {
                abort(s"Can only apply `count` to mandatory generic attribute. Please remove underscore from `$attrStr`")
              }

            case "optional" =>
              if (aggrType.isEmpty) {
                val group0 = Some(t.card match {
                  case 1 => "OptApplyOne"
                  case 2 => "OptApplyMany"
                  case 3 => "OptApplyMap"
                })
                addLambdas(t, castOptApplyAttr, jsonOptApplyAttr, group0 = group0)
                traverseElement(q"$prev", p, Generic("Schema", attrStr, "schema", value, getSort))
              } else {
                abort(s"Can only apply `count` to mandatory generic attribute. Please remove `$$` from `$attrStr`")
              }
          }
        }
        // Sorted by usage likelihood
        attrStr match {
          case "attrId"      => casts("mandatory", "Long")
          case "a"           => casts("mandatory", "String")
          case "part"        => casts("mandatory", "String")
          case "nsFull"      => casts("mandatory", "String")
          case "ns"          => casts("mandatory", "String")
          case "attr"        => casts("mandatory", "String")
          case "enumm"       => casts("mandatory", "String")
          case "ident"       => casts("mandatory", "String")
          case "valueType"   => casts("mandatory", "String")
          case "cardinality" => casts("mandatory", "String")
          case "doc"         => casts("mandatory", "String")
          case "unique"      => casts("mandatory", "String")
          case "isComponent" => casts("mandatory", "Boolean")
          case "noHistory"   => casts("mandatory", "Boolean")
          case "index"       => casts("mandatory", "Boolean")
          case "fulltext"    => casts("mandatory", "Boolean")
          case "t"           => casts("mandatory", "Long")
          case "tx"          => casts("mandatory", "Long")
          case "txInstant"   => casts("mandatory", "Date")

          case "attrId_"      => casts("tacit", "Long")
          case "a_"           => casts("tacit", "String")
          case "part_"        => casts("tacit", "String")
          case "nsFull_"      => casts("tacit", "String")
          case "ns_"          => casts("tacit", "String")
          case "attr_"        => casts("tacit", "String")
          case "enumm_"       => casts("tacit", "String")
          case "ident_"       => casts("tacit", "String")
          case "valueType_"   => casts("tacit", "String")
          case "cardinality_" => casts("tacit", "String")
          case "doc_"         => casts("tacit", "String")
          case "unique_"      => casts("tacit", "String")
          case "isComponent_" => casts("tacit", "Boolean")
          case "noHistory_"   => casts("tacit", "Boolean")
          case "index_"       => casts("tacit", "Boolean")
          case "fulltext_"    => casts("tacit", "Boolean")
          case "t_"           => casts("tacit", "Long")
          case "tx_"          => casts("tacit", "Long")
          case "txInstant_"   => casts("tacit", "Date")

          case "ident$"       => casts("optional", "String")
          case "valueType$"   => casts("optional", "String")
          case "cardinality$" => casts("optional", "String")
          case "doc$"         => casts("optional", "String")
          case "unique$"      => casts("optional", "String")
          case "isComponent$" => casts("optional", "Boolean")
          case "noHistory$"   => casts("optional", "Boolean")
          case "index$"       => casts("optional", "Boolean")
          case "fulltext$"    => casts("optional", "Boolean")
        }
      }
      val element = args match {
        case q"scala.collection.immutable.List($_.count)"   => resolve(Fn("count"), "Int2")
        case q"scala.collection.immutable.List($_.?)"       => abort("Generic input attributes not implemented.")
        case q"scala.collection.immutable.List(scala.None)" => resolve(Fn("not"))
        case q"scala.collection.immutable.List($v)"         => resolve(modelValue("apply", null, q"$v"))
        case q"scala.collection.immutable.List(..$vs)"      => resolve(modelValue("apply", null, q"Seq(..$vs)"))
        case _                                              => abort("Unexpected value applied to generic attribute: " + args)
      }
      //xx(255, p.nsFull, attrStr, args, element)
      element
    }

    def resolveApplyGeneric(prev: Tree, t: richTree, attrStr: String, args: Tree) = {
      def resolve(value: Value, aggrType: String = ""): Seq[Element] = {
        def casts(mandatory: Boolean, tpe: String, genericAttr: String = ""): Seq[Element] = {
          if (mandatory) {
            //xx(241, "aggrType: " + aggrType, t.nsFull, t.nsFull2, tpe, genericAttr, attrStr, obj, args)
            if (aggrType == "Int2") {
              // Count of generic attribute values
              addSpecific(
                t,
                castOneAttr("Int"),
                if (isOptNested) jsonOptNestedOneAttr("Int", attrStr) else jsonOneAttr("Int", attrStr),
                Some(tq"${TypeName("Int")}"),
                doAddProp = false
              )
              if (genericAttr.nonEmpty) {
                val cls     = "Datom_" + genericAttr
                val newProp = Prop(cls, genericAttr, tpe, 1, "One", Some("Int"))
                obj = addNode(obj, newProp, objLevel)
              }
              traverseElement(q"$prev", t, Generic(t.nsFull2, attrStr, genericType, value, getSort))
            } else {
              // Clean/comparison
              addSpecific(
                t,
                castOneAttr(tpe),
                if (isOptNested) jsonOptNestedOneAttr(tpe, attrStr) else jsonOneAttr(tpe, attrStr),
                Some(tq"${TypeName(tpe)}"),
                doAddProp = false
              )
              if (genericAttr.nonEmpty) {
                val cls     = "Datom_" + genericAttr
                val newProp = Prop(cls, genericAttr, tpe, 1, "One")
                obj = addNode(obj, newProp, objLevel)
              }
              //xx(242, t, t.nsFull, t.name, obj)
              traverseElement(q"$prev", t, Generic(t.nsFull, attrStr, genericType, value, getSort))
            }
          } else {
            // Tacit
            if (aggrType.isEmpty) {
              traverseElement(q"$prev", t, Generic(t.nsFull, attrStr, genericType, value, getSort))
            } else {
              abort(s"Can only apply `count` to mandatory generic attribute. Please remove underscore from `$attrStr`")
            }
          }
        }
        attrStr match {
          case "e"         => casts(mandatory = true, "Long", "e")
          case "a"         => casts(mandatory = true, "String", "a")
          case "v"         => casts(mandatory = true, "Any", "v")
          case "t"         => casts(mandatory = true, "Long", "t")
          case "tx"        => casts(mandatory = true, "Long", "tx")
          case "txInstant" => casts(mandatory = true, "Date", "txInstant")
          case "op"        => casts(mandatory = true, "Boolean", "op")

          case "e_"         => casts(mandatory = false, "Long")
          case "a_"         => casts(mandatory = false, "String")
          case "v_"         => casts(mandatory = false, "Any")
          case "t_"         => casts(mandatory = false, "Long")
          case "tx_"        => casts(mandatory = false, "Long")
          case "txInstant_" => casts(mandatory = false, "Date")
          case "op_"        => casts(mandatory = false, "Boolean")
        }
      }
      val element = args match {
        case q"scala.collection.immutable.List($_.count)"                               => resolve(Fn("count"), "Int2")
        case q"scala.collection.immutable.List($_.?)"                                   => abort("Generic input attributes not implemented.")
        case q"scala.collection.immutable.List($_.$fn)" if badFn(fn.toString)           => abort(s"Generic attributes only allowed to aggregate `count`. Found: `$fn`")
        case q"scala.collection.immutable.List($_.$fn.apply($_))" if badFn(fn.toString) => abort(s"Generic attributes only allowed to aggregate `count`. Found: `$fn`")
        case q"scala.collection.immutable.List($v)"                                     => resolve(modelValue("apply", null, q"$v"))
        case q"scala.collection.immutable.List(..$vs)"                                  => resolve(modelValue("apply", null, q"Seq(..$vs)"))
        case _                                                                          => abort("Unexpected value applied to generic attribute: " + args)
      }
      //xx(245, t.nsFull, attrStr, args, element, obj)
      element
    }


    def resolveTypedApply(tree: Tree, p: richTree): Seq[Element] = tree match {
      case q"$prev.Tx.apply[..$_]($txMolecule)" =>
        txMetaDataStarted = true

        // tx prefix for json field names. Available for tx molecule resolve. But needs to be blanked for appending attributes of the main molecule.
        tx = "tx."
        val txMetaProps = resolve(q"$txMolecule")
        val txMetaData  = TxMetaData(txMetaProps)
        tx = ""

        val err = "Unexpectedly couldn't find ns in sub composite:\n  " + txMetaProps.mkString("\n  ")
        val ns  = txMetaProps.collectFirst {
          case Atom(ns, _, _, _, _, _, _, _, _) => ns
          case Bond(nsFull, _, _, _, _)         => nsFull
          case Composite(elements)              => elements.collectFirst {
            case Atom(ns, _, _, _, _, _, _, _, _) => ns
            case Bond(nsFull, _, _, _, _)         => nsFull
          } getOrElse abort(err)
        } getOrElse abort(err)

        if (txMetas == 0) {
          // Start non-composite tx meta data with namespace
          // (composites would have set txMetas to 2 or more and is already namespaced)
          val cls = ns + "_"
          obj = addRef(obj, cls, ns, nested = false, objLevel)
          objLevel = (objLevel - 1).max(0)
        }

        // Treat tx meta data as referenced data
        obj = addRef(obj, "Tx_", "Tx", nested = false, objLevel)
        objLevel = (objLevel - 1).max(0)
        txMetaDataStarted = false

        // Start new level
        typess = List.empty[Tree] :: typess
        castss = List.empty[Int => Tree] :: castss
        sortIndexes = List.empty[Int] :: sortIndexes

        if (txMetas == 0)
          txMetas = 1
        txMetaDataDone = true
        objCompositesCount = 0

        //xx(310, "Tx", prev, txMolecule, txMetaData, typess, castss, txMetas, objCompositesCount, ns, obj)
        traverseElement(q"$prev", p, txMetaData)

      case q"$_.e.apply[..$_]($nested)" if !p.isRef =>
        //xx(320, "e")
        Seq(Nested(Bond("", "", "", 2), Generic("", "e", "datom", EntValue, getSort) +: resolve(q"$nested")))

      case q"$_.e_.apply[..$_]($nested)" if !p.isRef =>
        //xx(330, "e_")
        Seq(Nested(Bond("", "", "", 2), resolve(q"$nested")))

      case q"$prev.$manyRef.apply[..$_]($nested)" if !q"$prev.$manyRef".isRef =>
        //xx(340, manyRef, nested)
        Seq(Nested(Bond("", "", "", 2), nestedElements(q"$prev.$manyRef", manyRef.toString, q"$nested")))

      case q"$prev.$manyRef.apply[..$_]($nested)" =>
        //xx(350, manyRef, nested)
        traverseElement(q"$prev", p, nested1(q"$prev", p, TermName(manyRef.toString), q"$nested"))
    }


    def resolveOperation(tree: Tree): Seq[Element] = tree match {
      // Attribute map using k/apply
      case q"$prev.$keyedAttr.k(..$keys).$op(..$values)" =>
        //xx(410, keyedAttr, richTree(q"$prev.$keyedAttr").tpeS)
        val element = resolveOp(q"$prev.$keyedAttr", richTree(q"$prev.$keyedAttr"), keyedAttr.toString(), q"$op", q"Seq(..$values)") match {
          case a: Atom => a.copy(keys = getValues(q"$keys").asInstanceOf[Seq[String]])
          case other   => abort("Unexpected element: " + other)
        }
        traverseElement(q"$prev", richTree(q"$prev"), element)

      // Keyed attribute map operation
      case t@q"$prev.$keyedAttr.apply($key).$op(..$values)" if q"$prev.$keyedAttr($key)".isMapAttrK =>
        val tpe     = c.typecheck(q"$prev.$keyedAttr($key)").tpe
        val attrStr = keyedAttr.toString
        val tpeStr  = truncTpe(tpe.baseType(weakTypeOf[One[_, _, _]].typeSymbol).typeArgs.last.toString)
        val nsFull  = new nsp(tpe.typeSymbol.owner).toString
        //xx(420, nsFull, keyedAttr, tpeStr)
        if (keyedAttr.toString().last != '_') {
          addSpecific(
            richTree(q"$prev.$keyedAttr"),
            castKeyedMapAttr(tpeStr),
            if (isOptNested) jsonOptNestedKeyedMapAttr(tpeStr, attrStr) else jsonKeyedMapAttr(tpeStr, attrStr),
            Some(tq"${TypeName(tpeStr)}"),
            doAddProp = false,
            group0 = Some("KeyedMap")
          )
          // Have to add node manually since nsFull is resolved in a special way
          val cls     = nsFull + "_" + attrStr
          val newProp = Prop(cls, attrStr, tpeStr, 4, "KeyedMap")
          obj = addNode(obj, newProp, objLevel)
          //xx(421, obj)
        }
        traverseElement(q"$prev", richTree(q"$prev"),
          Atom(nsFull, keyedAttr.toString, tpeStr, 4, modelValue(op.toString(), t, q"Seq(..$values)"), None, Nil, Seq(extract(q"$key").toString))
        )

      // Attribute operations -----------------------------
      case q"$prev.$attr.$op(..$values)" =>
        //xx(430, attr)
        traverseElement(q"$prev", richTree(q"$prev"), resolveOp(q"$prev.$attr", richTree(q"$prev.$attr"), attr.toString(), q"$op", q"Seq(..$values)"))
    }


    def resolveOp(tree: Tree, t: richTree, attrStr: String, op: Tree, values0: Tree): Element = {
      val value: Value = modelValue(op.toString(), tree, values0)

      if (attrStr.head.isUpper) {
        //xx(91, attrStr, value)
        if (attrStr == "AVET")
          Generic("AVET", "range", "avet", value, getSort)
        else {
          Atom(t.name, t.name, t.tpeS, t.card, value, t.enumPrefixOpt, bi(tree, t), sort = getSort)
        }

      } else if (genericType == "datom" && datomGeneric.contains(attrStr)) {
        //xx(92, attrStr, values0, value)
        resolveOpDatom(t, attrStr, value)

      } else if (genericType != "datom") {
        //xx(93, genericType, attrStr)
        genericType match {
          case "schema" => resolveOpSchema(t, attrStr, value)
          case _        => abort("Expressions on index attributes are not allowed. " +
            "Please apply expression to full index result at runtime.")
        }
      } else if (t.isMapAttr) {
        //xx(94, attrStr, value)
        addLambdas(t, castMapAttr, if (isOptNested) jsonOptNestedMapAttr else jsonMapAttr)
        Atom(t.nsFull, attrStr, t.tpeS, 3, value, None, bi(tree, t))

      } else if (t.isMapAttr$) {
        //xx(95, attrStr, value)
        addLambdas(
          t,
          castOptApplyMapAttr,
          if (isOptNested) jsonOptNestedOptApplyMapAttr else jsonOptApplyMapAttr,
          group0 = Some("OptApplyMap")
        )
        Atom(t.nsFull, attrStr, t.tpeS, 3, value, None, bi(tree, t))

      } else if (t.isRefAttr || t.isRefAttr$) {
        //xx(96, attrStr, value)
        addAttrOrAggr(attrStr, t, "ref", apply = true)
        Atom(t.nsFull, attrStr, "ref", t.card, value, t.enumPrefixOpt, bi(tree, t), sort = getSort)

      } else if (t.isAttr) {
        //xx(97, attrStr, value)
        addAttrOrAggr(attrStr, t, t.tpeS, apply = true)
        Atom(t.nsFull, attrStr, t.tpeS, t.card, value, t.enumPrefixOpt, bi(tree, t), sort = getSort)

      } else {
        abort(s"Unexpected attribute operation for `$attrStr` having value: " + value)
      }
    }

    def resolveOpSchema(t: richTree, attrStr: String, value: Value) = {
      def resolve(tpe: String): Generic = {
        addAttrOrAggr(attrStr, t, tpe)
        Generic("Schema", attrStr, "schema", value, getSort)
      }
      attrStr match {
        case "attrId" | "attrId_"           => resolve("Long")
        case "a" | "a_"                     => resolve("String")
        case "part" | "part_"               => resolve("String")
        case "nsFull" | "nsFull_"           => resolve("String")
        case "ns" | "ns_"                   => resolve("String")
        case "attr" | "attr_"               => resolve("String")
        case "enumm" | "enumm_"             => resolve("String")
        case "ident" | "ident_"             => resolve("String")
        case "valueType" | "valueType_"     => resolve("String")
        case "cardinality" | "cardinality_" => resolve("String")
        case "doc" | "doc_"                 => resolve("String")
        case "unique" | "unique_"           => resolve("String")
        case "isComponent" | "isComponent_" => resolve("Boolean")
        case "noHistory" | "noHistory_"     => resolve("Boolean")
        case "index" | "index_"             => resolve("Boolean")
        case "fulltext" | "fulltext_"       => resolve("Boolean")
        case "t" | "t_"                     => resolve("Long")
        case "tx" | "tx_"                   => resolve("Long")
        case "txInstant" | "txInstant_"     => resolve("Date")
      }
    }

    def resolveOpDatom(t: richTree, attrStr: String, value: Value) = {
      def resolve(tpe: String): Generic = {
        addAttrOrAggr(attrStr, t, tpe)
        Generic(t.nsFull, attrStr, genericType, value, getSort)
      }
      attrStr match {
        case "e" | "e_"                 => resolve("Long")
        case "a" | "a_"                 => resolve("String")
        case "v" | "v_"                 => value match {
          case Gt(v) => abort(s"Can't compare generic values being of different types. Found: $attrStr.>(${v.toString.replace("__n__", "")})")
          case Ge(v) => abort(s"Can't compare generic values being of different types. Found: $attrStr.>=(${v.toString.replace("__n__", "")})")
          case Le(v) => abort(s"Can't compare generic values being of different types. Found: $attrStr.<=(${v.toString.replace("__n__", "")})")
          case Lt(v) => abort(s"Can't compare generic values being of different types. Found: $attrStr.<(${v.toString.replace("__n__", "")})")
          case _     => resolve("Any")
        }
        case "tx" | "tx_"               => resolve("Long")
        case "t" | "t_"                 => resolve("Long")
        case "txInstant" | "txInstant_" => resolve("Date")
        case "op" | "op_"               => resolve("Boolean")
      }
    }


    def resolveNested(prev: Tree, p: richTree, manyRef: TermName, nested: Tree): Seq[Element] = {
      //xx(521, post, prev, manyRef, nested, obj, castss, txMetas, txMetaDataDone)
      if (isOptNested)
        abort("Optional nested structure can't be mixed with mandatory nested structure.")

      if (castss.head.nonEmpty && !txMetaDataDone)
        abort("Attributes after nested structure not allowed (only Tx meta data is allowed).")

      // From now on, elements are part of nested structure
      // Transfer any tempJson lambdas to postJson lambdas
      post = false
      // Add nested elements on current level
      val nestedElement = nested1(q"$prev", p, manyRef, q"$nested")
      // Start new level
      typess = List.empty[Tree] :: typess
      castss = List.empty[Int => Tree] :: castss
      sortIndexes = List.empty[Int] :: sortIndexes

      //xx(523, nestedElement, post, prev, manyRef, nested, obj)
      traverseElement(q"$prev", p, nestedElement)
    }

    def resolveOptNested(prev: Tree, p: richTree, manyRef: TermName, nested: Tree): Seq[Element] = {
      //xx(524, post)
      // From now on, elements are part of nested structure
      // Transfer any tempJson lambdas to postJson lambdas
      post = false
      optNestedLevel += 1
      // Add nested elements on current level
      //xx(525, post)
      val nestedElement = nested1(q"$prev", p, manyRef, q"$nested")
      optNestedLevel -= 1
      // Start new level
      typess = List.empty[Tree] :: typess
      castss = List.empty[Int => Tree] :: castss
      sortIndexes = List.empty[Int] :: sortIndexes
      //xx(526, nestedElement, post)
      traverseElement(q"$prev", p, nestedElement)
    }

    def nested1(prev: Tree, p: richTree, manyRef: TermName, nestedTree: Tree) = {
      val refNext  = q"$prev.$manyRef".refNext
      val parentNs = prev match {
        case q"$_.apply($_)" if p.isMapAttrK            => new nsp(c.typecheck(prev).tpe.typeSymbol.owner)
        case q"$pre.apply($_)" if p.isAttr              => richTree(q"$pre").nsFull
        case q"$pre.apply($_)"                          => richTree(q"$pre").name.capitalize
        case _ if prev.symbol.name.toString.head == '_' => prev.tpe.typeSymbol.name.toString.split("_\\d", 2).head
        case q"$pre.e" if p.isAttr                      => q"$pre".symbol.name
        case _ if p.isAttr                              => p.nsFull
        case _ if p.isRef                               => p.refNext
        case q"$prev1.$sort"                            => sort.toString match {
          case r"[ad][1-5]" => richTree(q"$prev1").nsFull
          case _            => p.name.capitalize
        }
        case _                                          => p.name.capitalize
      }
      //xx(540, prev, manyRef, q"$prev.$manyRef", refNext, parentNs)
      val opt               = if (isOptNested) "$" else ""
      val (nsFull, refAttr) = (parentNs.toString, firstLow(manyRef))
      //xx(550, q"$prev.$manyRef", prev, manyRef, refNext, parentNs, post, nsFull, refAttr, obj)
      nestedRefs = nestedRefs :+ manyRef.toString
      // park post props
      val postProps = obj.props
      obj = Obj("", "", nested = false, Nil)
      val nestedElems = nestedElements(q"$prev.$manyRef", refNext, nestedTree)
      val cls         = nsFull + "__" + manyRef
      val nestedObj   = Obj(cls, manyRef.toString, nested = true, obj.props)
      obj = obj.copy(props = nestedObj +: postProps)
      //xx(560, prev, manyRef, nestedTree, nsFull, parentNs, nestedRefs, nestedElems, postProps, obj)
      Nested(Bond(nsFull, refAttr + opt, refNext, 2, bi(q"$prev.$manyRef", richTree(q"$prev.$manyRef"))), nestedElems)
    }

    def nestedElements(manyRef: Tree, refNext: String, nested: Tree): Seq[Element] = {
      val nestedElements = resolve(nested)
      val nestedNs       = curNs(nestedElements.head)
      if (refNext != nestedNs) {
        // Find refs in `manyRef` namespace and match the target type with the first namespace of the first nested element
        val refs             = c.typecheck(manyRef).tpe.members.filter(e => e.isMethod && e.asMethod.returnType <:< weakTypeOf[Ref[_, _]])
        val refPairs         = refs.map(r => r.name -> r.typeSignature.baseType(weakTypeOf[Ref[_, _]].typeSymbol).typeArgs.last.typeSymbol.name.toString.init)
        val refPairsFiltered = refPairs.filter(_._2 == nestedNs.capitalize)
        val nestedElements2  = if (refPairsFiltered.isEmpty) {
          nestedElements
        } else if (refPairsFiltered.size == 1) {
          val (refAttr, refNs) = refPairsFiltered.head
          val opt              = if (isOptNested) "$" else ""
          Bond(refNext, firstLow(refAttr) + opt, refNs, 2, bi(manyRef, richTree(manyRef))) +: nestedElements
        } else {
          val refAttr = manyRef.toString().split('.').last
          abort(s"`$refAttr` can only nest to `$refNext`. Found: `$nestedNs`")
        }
        //xx(571, manyRef, refNext, nested, nestedNs, nestedElements, refs, refPairs, refPairsFiltered, nestedElements2, obj)
        nestedElements2
      } else {
        //xx(572, manyRef, refNext, nested, nestedNs, nestedElements, obj)
        nestedElements
      }
    }

    def bi(tree: Tree, t: richTree): Seq[GenericValue] = if (t.isBidirectional) {
      if (t.isBiSelfRef) {
        Seq(BiSelfRef(t.refCard))

      } else if (t.isBiSelfRefAttr) {
        Seq(BiSelfRefAttr(t.card))

      } else if (t.isBiOtherRef) {
        Seq(BiOtherRef(t.refCard, extractNsAttr(weakTypeOf[BiOtherRef_[_]], tree)))

      } else if (t.isBiOtherRefAttr) {
        Seq(BiOtherRefAttr(t.card, extractNsAttr(weakTypeOf[BiOtherRefAttr_[_]], tree)))

      } else if (t.isBiEdgeRef) {
        Seq(BiEdgeRef(t.refCard, extractNsAttr(weakTypeOf[BiEdgeRef_[_]], tree)))

      } else if (t.isBiEdgeRefAttr) {
        Seq(BiEdgeRefAttr(t.card, extractNsAttr(weakTypeOf[BiEdgeRefAttr_[_]], tree)))

      } else if (t.isBiEdgePropRef) {
        Seq(BiEdgePropRef(t.refCard))

      } else if (t.isBiEdgePropAttr) {
        Seq(BiEdgePropAttr(t.card))

      } else if (t.isBiEdgePropRefAttr) {
        Seq(BiEdgePropRefAttr(t.card))

      } else if (t.isBiTargetRef) {
        Seq(BiTargetRef(t.refCard, extractNsAttr(weakTypeOf[BiTargetRef_[_]], tree)))

      } else if (t.isBiTargetRefAttr) {
        Seq(BiTargetRefAttr(t.card, extractNsAttr(weakTypeOf[BiTargetRefAttr_[_]], tree)))

      } else {
        throw Dsl2ModelException("Unexpected Bidirectional: " + t)
      }
    } else {
      Seq.empty[GenericValue]
    }

    def addAttrOrAggr(attr: String, t: richTree, tpeStr: String, apply: Boolean = false): Unit = {
      if (standard) {
        //xx(81, attr, tpeStr)
        if (t.name.last != '$') {
          if (isOptNested)
            addLambdas(t, castOptNestedAttr, jsonOptNestedAttr, baseTpe0 = Some(tpeStr))
          else
            addLambdas(t, castAttr, jsonAttr, baseTpe0 = Some(tpeStr))
        } else {
          if (isOptNested)
            addLambdas(t, castOptNestedOptAttr, jsonOptNestedOptAttr, baseTpe0 = Some(tpeStr))
          else if (apply)
            addLambdas(t, castOptApplyAttr, jsonOptApplyAttr, baseTpe0 = Some(tpeStr), group0 = Some("OptApply"))
          else
            addLambdas(t, castOptAttr, jsonOptAttr, baseTpe0 = Some(tpeStr))
        }
      } else {
        //xx(82, attr, s"aggrType: '$aggrType'", t.card, t.tpeS, tpeStr)
        attr.last match {
          case '_' | '$' => abort("Only mandatory attributes are allowed to aggregate")
          case _         =>
            val tpe    = TypeName(if (tpeStr == "ref") "Long" else tpeStr)
            val propFn = attr + "_" + aggrFn
            //xx(83, aggrType)
            t.card match {
              case 2 => aggrType match {
                case "int" =>
                  addSpecific(
                    t,
                    castOneAttr("Int"),
                    if (isOptNested) jsonOptNestedOneAttr("Int", attr) else jsonOneAttr("Int", attr),
                    Some(tq"Set[$tpe]"),
                    optAggr = Some((propFn, tq"${TypeName("Int")}")),
                    baseTpe0 = Some(tpeStr),
                    group0 = Some("One")
                  )

                case "double" =>
                  addSpecific(
                    t,
                    castOneAttr("Double"),
                    if (isOptNested) jsonOptNestedOneAttr("Double", attr) else jsonOneAttr("Double", attr),
                    Some(tq"Set[$tpe]"),
                    optAggr = Some((propFn, tq"${TypeName("Double")}")),
                    baseTpe0 = Some(tpeStr),
                    group0 = Some("One")
                  )

                case "list" =>
                  addSpecific(
                    t,
                    castAggrManyList(tpeStr),
                    jsonAggrManyList(tpeStr, attr),
                    Some(tq"Set[$tpe]"),
                    optAggr = Some((propFn, tq"List[$tpe]")),
                    group0 = Some("AggrManyList")
                  )

                case "listDistinct" =>
                  addSpecific(
                    t,
                    castAggrManyListDistinct(tpeStr),
                    jsonAggrManyListDistinct(tpeStr, attr),
                    Some(tq"$tpe"),
                    optAggr = Some((propFn, tq"List[$tpe]")),
                    group0 = Some("AggrManyListDistinct")
                  )

                case "listRand" =>
                  addSpecific(
                    t,
                    castAggrManyListRand(tpeStr),
                    jsonAggrManyListRand(tpeStr, attr),
                    Some(tq"$tpe"),
                    optAggr = Some((propFn, tq"List[$tpe]")),
                    group0 = Some("AggrManyListRand")
                  )

                case "single" =>
                  addSpecific(
                    t,
                    castAggrManySingle(tpeStr),
                    jsonAggrManySingle(tpeStr, attr),
                    optAggr = Some((propFn, tq"Set[$tpe]")),
                    group0 = Some("AggrManySingle")
                  )
              }

              case _ => aggrType match {
                case "int" =>
                  //xx(84, aggrType)
                  addSpecific(
                    t,
                    castOneAttr("Int"),
                    if (isOptNested) jsonOptNestedOneAttr("Int", attr) else jsonOneAttr("Int", attr),
                    Some(tq"$tpe"),
                    optAggr = Some((propFn, tq"${TypeName("Int")}")),
                    baseTpe0 = Some(tpeStr),
                    group0 = Some("One")
                  )

                case "double" =>
                  addSpecific(
                    t,
                    castOneAttr("Double"),
                    if (isOptNested) jsonOptNestedOneAttr("Double", attr) else jsonOneAttr("Double", attr),
                    Some(tq"$tpe"),
                    optAggr = Some((propFn, tq"${TypeName("Double")}")),
                    baseTpe0 = Some(tpeStr),
                    group0 = Some("One")
                  )

                case "list" =>
                  addSpecific(
                    t,
                    castAggrOneList(tpeStr),
                    jsonAggrOneList(tpeStr, attr),
                    Some(tq"$tpe"),
                    optAggr = Some((propFn, tq"List[$tpe]")),
                    group0 = Some("AggrOneList")
                  )

                case "listDistinct" =>
                  addSpecific(
                    t,
                    castAggrOneListDistinct(tpeStr),
                    jsonAggrOneListDistinct(tpeStr, attr),
                    Some(tq"$tpe"),
                    optAggr = Some((propFn, tq"List[$tpe]")),
                    group0 = Some("AggrOneListDistinct")
                  )

                case "listRand" =>
                  addSpecific(
                    t,
                    castAggrOneListRand(tpeStr),
                    jsonAggrOneListRand(tpeStr, attr),
                    Some(tq"$tpe"),
                    optAggr = Some((propFn, tq"List[$tpe]")),
                    group0 = Some("AggrOneListRand")
                  )

                case "singleSample" =>
                  addSpecific(
                    t,
                    castAggrSingleSample(tpeStr),
                    jsonAggrSingleSample(tpeStr, attr),
                    optAggr = Some((propFn, tq"$tpe")),
                    group0 = Some("AggrSingleSample")
                  )

                case "single" =>
                  addSpecific(
                    t,
                    castAggrOneSingle(tpeStr),
                    jsonAggrOneSingle(tpeStr, attr),
                    optAggr = Some((propFn, tq"$tpe")),
                    group0 = Some("AggrOneSingle")
                  )
              }
            }
            //xx(85, aggrType)
        }
        standard = true
        aggrType = ""
      }
    }


    // Values ================================================================================

    def modelValue(op: String, attr: Tree, values0: Tree): Value = {
      val t = if (attr == null) null else richTree(attr)
      def errValue(i: Int, v: Any): Nothing = abort(s"($i) Unexpected resolved model value for `${t.name}.$op`: $v")
      val values = getValues(values0, t)
      //xx(60, op, attr, values0, values, values0.raw)
      op match {
        case "applyKey"    => NoValue
        case "apply"       => values match {
          case resolved: Value                         => resolved
          case vs: Seq[_] if t == null                 => Eq(vs)
          case vs: Seq[_] if t.isMapAttr && vs.isEmpty => MapEq(Seq())
          case vs: Seq[_]                              => Eq(vs)
          case other                                   => errValue(1, other)
        }
        case "k"           => values match {
          case vs: Seq[_] => MapKeys(vs.map(_.asInstanceOf[String]))
          case other      => errValue(2, other)
        }
        case "not"         => values match {
          case _: Qm.type                          => Neq(Seq(Qm))
          case Fn("not", None)                     => Neq(Nil)
          case (set: Set[_]) :: Nil if set.isEmpty => Neq(Nil)
          case vs: Seq[_] if vs.isEmpty            => Neq(Nil)
          case vs: Seq[_]                          => Neq(vs)
        }
        case "$bang$eq"    => values match {
          case _: Qm.type => Neq(Seq(Qm))
          case vs: Seq[_] => Neq(vs)
        }
        case "$less"       => values match {
          case _: Qm.type => Lt(Qm)
          case vs: Seq[_] => Lt(vs.head)
        }
        case "$greater"    => values match {
          case _: Qm.type => Gt(Qm)
          case vs: Seq[_] => Gt(vs.head)
        }
        case "$less$eq"    => values match {
          case _: Qm.type => Le(Qm)
          case vs: Seq[_] => Le(vs.head)
        }
        case "$greater$eq" => values match {
          case _: Qm.type => Ge(Qm)
          case vs: Seq[_] => Ge(vs.head)
        }
        case "contains"    => values match {
          case _: Qm.type => Fulltext(Seq(Qm))
          case vs: Seq[_] => Fulltext(vs)
        }
        case "assert"      => values match {
          case MapEq(pairs)  => AssertMapPairs(pairs)
          case mapped: Value => mapped
          case vs: Seq[_]    => AssertValue(vs)
        }
        case "retract"     => values match {
          case vs: Seq[_] if t.isMapAttr => RetractMapKeys(vs.map(_.toString))
          case vs: Seq[_]                => RetractValue(vs)
        }
        case "replace"     => values match {
          case MapEq(keyValues) => ReplaceMapPairs(keyValues)
          case resolved: Value  => resolved
          case Nil              => ReplaceValue(Nil)
        }
        case "range"       => values match {
          case vs: Seq[_] => Eq(vs)
        }
        case unexpected    => abort(s"Unknown operator '$unexpected'\nattr: ${t.name} \nvalue: $values0")
      }
    }


    def getValues(values: Tree, t: richTree = null): Any = {
      def aggr(fn: String, aggrTpe: String, value: Option[Int] = None, checkNum: Boolean = false) = if (t != null && t.name.last == '_') {
        abort(s"Aggregated values need to be returned. Please omit underscore from attribute `:${t.nsFull}/${t.name}`")
      } else {
        if (checkNum && !numberTypes.contains(t.tpeS))
          abort(s"Can't apply `$fn` aggregate to non-number attribute `${t.name}` of type `${t.tpeS}`.")
        standard = false
        aggrType = aggrTpe
        aggrFn = fn + value.fold("")(_ => "s")
        Fn(fn, value)
      }

      def keyw(kw: String): Value = kw match {
        case "$qmark"                      => Qm
        case "Nil"                         => Fn("not")
        case "None"                        => Fn("not")
        case "unify" if t.name.last == '_' => Fn("unify")
        case "unify"                       => abort(s"Can only unify on tacit attributes. Please add underscore to attribute: `${t.name}_(unify)`")
        case "min"                         => aggr("min", "single")
        case "max"                         => aggr("max", "single")
        case "rand"                        => aggr("rand", "single")
        case "sample"                      => aggr("sample", "singleSample", Some(1))
        case "sum"                         => aggr("sum", "single", checkNum = true)
        case "median"                      => aggr("median", "single")
        case "count"                       => aggr("count", "int")
        case "countDistinct"               => aggr("count-distinct", "int")
        case "avg"                         => aggr("avg", "double")
        case "variance"                    => aggr("variance", "double")
        case "stddev"                      => aggr("stddev", "double")
        case "distinct"                    => standard = false; aggrType = "listDistinct"; Distinct
      }

      def single(value: Tree) = value match {
        case q"$_.$kw" if keywords.contains(kw.toString)        => keyw(kw.toString)
        case q"$_.min.apply(${Literal(Constant(i: Int))})"      => aggr("min", "list", Some(i))
        case q"$_.max.apply(${Literal(Constant(i: Int))})"      => aggr("max", "list", Some(i))
        case q"$_.rand.apply(${Literal(Constant(i: Int))})"     => aggr("rand", "listRand", Some(i))
        case q"$_.sample.apply(${Literal(Constant(i: Int))})"   => aggr("sample", "list", Some(i))
        case q"$_.distinct.apply(${Literal(Constant(i: Int))})" => aggr("distinct", "list", Some(i))
        case q"$a.and[$_]($b).and[$_]($c)"                      => And(resolveValues(q"Seq($a, $b, $c)"))
        case q"$a.and[$_]($b)"                                  => And(resolveValues(q"Seq($a, $b)"))

        case q"scala.Some.apply[$_]($v)" =>
          //xx(10, v)
          v match {
            case vm if vm.tpe <:< weakTypeOf[Map[_, _]] => vm match {
              case Apply(_, pairs) => mapPairs(pairs, t)
              case ident           => mapPairs(Seq(q"$ident"), t)
            }
            case ident if t.isMapAttr || t.isMapAttr$   => mapPairs(Seq(q"$ident"), t)
            case _                                      => Eq(resolveValues(q"$v"))
          }

        case v if !(v.tpe <:< weakTypeOf[Seq[Nothing]]) && v.tpe <:< weakTypeOf[Seq[(_, _)]] =>
          //xx(11, v)
          v match {
            case Apply(_, pairs) => mapPairs(pairs, t)
            case ident           => mapPairs(Seq(ident), t)
          }

        case v if !(v.tpe <:< weakTypeOf[Set[Nothing]]) && v.tpe <:< weakTypeOf[Set[_]]
          && v.tpe.typeArgs.head <:< weakTypeOf[(_, _)] =>
          //xx(12, v)
          v match {
            case Apply(_, pairs) => mapPairs(pairs, t)
            case ident           => mapPairs(Seq(ident), t)
          }

        case v if !(v.tpe <:< weakTypeOf[Map[Nothing, Nothing]]) && v.tpe <:< weakTypeOf[Map[_, _]] =>
          //xx(13, v)
          v match {
            case Apply(_, pairs) => mapPairs(pairs, t)
            case ident           => mapPairs(Seq(ident), t)
          }

        case v if t == null =>
          //xx(14, v)
          Seq(resolveValues(q"$v"))

        case v if v.tpe <:< weakTypeOf[(_, _)] =>
          //xx(15, v)
          mapPairs(Seq(v), t)

        case v if t.isMapAttr$ =>
          //xx(16, v)
          mapPairs(Seq(v), t)

        case set if t.isMany && set.tpe <:< weakTypeOf[Set[_]] =>
          //xx(17, set)
          Seq(resolveValues(q"$set", t).toSet)

        case vs if t.isMany =>
          //xx(18, vs)
          vs match {
            case q"$_.Seq.apply[$tpe](..$sets)" if tpe.tpe <:< weakTypeOf[Set[_]] =>
              //xx(19, vs)
              sets.map(set => resolveValues(q"$set", t).toSet)

            case q"$_.List.apply[$tpe](..$sets)" if tpe.tpe <:< weakTypeOf[Set[_]] =>
              //xx(20, vs)
              sets.map(set => resolveValues(q"$set", t).toSet)

            case _ =>
              //xx(21, vs)
              resolveValues(q"$vs", t)
          }
        case other          =>
          //xx(22, other, other.raw)
          resolveValues(q"Seq($other)", t)
      }

      def multiple(values: Seq[Tree]) = values match {
        case vs if t == null =>
          //xx(30, vs)
          vs.flatMap(v => resolveValues(q"$v"))

        case vs if vs.nonEmpty && vs.head.tpe <:< weakTypeOf[(_, _)] =>
          //xx(31, vs)
          mapPairs(vs, t)

        case sets if t.isMany && sets.nonEmpty && sets.head.tpe <:< weakTypeOf[Set[_]] =>
          //xx(32, sets)
          sets.map(set => resolveValues(q"$set", t).toSet)

        case vs if t.isMany && vs.nonEmpty =>
          //xx(31, vs)
          vs.head match {
            case q"$_.Seq.apply[$tpe](..$sets)" if tpe.tpe <:< weakTypeOf[Set[_]] =>
              //xx(32, vs, sets)
              sets.map(set => resolveValues(q"$set", t).toSet)

            case q"$_.List.apply[$tpe](..$sets)" if tpe.tpe <:< weakTypeOf[Set[_]] =>
              //xx(33, vs, sets)
              sets.map(set => resolveValues(q"$set", t).toSet)

            case _ =>
              //xx(34, vs)
              vs.flatMap(v => resolveValues(q"$v", t))
          }
        case vs                            =>
          //xx(35, vs)
          vs.flatMap(v => resolveValues(q"$v", t))
      }

      values match {
        case q"Seq($value)" =>
          //xx(1, "single in Seq", value)
          single(q"$value")

        case Apply(_, List(Select(_, TermName("$qmark")))) =>
          //xx(2, "datom")
          Qm

        case q"Seq(..$values)" =>
          //xx(3, "multiple", values)
          multiple(values.asInstanceOf[Seq[Tree]])

        case other =>
          //xx(4, other)
          resolveValues(other, t)
      }
    }

    def mapPairs(vs: Seq[Tree], t: richTree = null): Value = {
      def keyValues = vs.map {
        case q"scala.Predef.ArrowAssoc[$_]($k).->[$_]($v)" => (extract(q"$k"), extract(q"$v"))
        case q"scala.Tuple2.apply[$_, $_]($k, $v)"         => (extract(q"$k"), extract(q"$v"))
        case ident                                         => (extract(ident), "__pair__")
      }
      if (t.isMapAttr || t.isMapAttr$)
        MapEq(keyValues.map(kv => (kv._1.asInstanceOf[String], kv._2)))
      else
        ReplaceValue(keyValues)
    }

    def extract(tree: Tree) = tree match {
      case Constant(v: String)                            => v
      case Literal(Constant(v))                           => v
      case Ident(TermName(v: String))                     => hasVariables = true; "__ident__" + v
      case Select(This(TypeName(_)), TermName(v: String)) => hasVariables = true; "__ident__" + v

      // Implicit widening conversions of variables
      case Select(Select(This(TypeName(_)), TermName(v)),
      TermName("toFloat" | "toDouble" | "toLong")) =>
        hasVariables = true
        "__ident__" + v

      case v => v
    }

    def resolveValues(tree: Tree, t: richTree = null): Seq[Any] = {
      //xx(41, tree)
      val at: att = if (t == null) null else t.at
      def noAppliedExpression(expr: String): Nothing = abort(
        s"Can't apply expression `$expr` here. Please assign expression to a variable and apply this instead."
      )
      def resolve(tree0: Tree, values: Seq[Tree] = Seq.empty[Tree]): Seq[Tree] = {
        //xx(42, tree0, tree0.raw)
        tree0 match {
          case q"$a.or($b)"           => resolve(q"$b", resolve(q"$a", values))
          case q"$_.string2Model($v)" => values :+ q"$v"

          case q"scala.StringContext.apply(..$_).s(..$_)" => abort(
            "Can't use string interpolation for applied values. " +
              "Please assign the interpolated value to a single variable and apply that instead.")

          // Preventing simple arithmetic operation
          case q"$_.$a.+(..$b)" => noAppliedExpression(s"$a + $b")
          case q"$_.$a.-(..$b)" => noAppliedExpression(s"$a - $b")
          case q"$_.$a.*(..$b)" => noAppliedExpression(s"$a * $b")
          case q"$_.$a./($b)"   => noAppliedExpression(s"$a / $b")
          case Apply(_, vs)     => values ++ vs.flatMap(resolve(_))
          case v                => values :+ v
        }
      }
      def validateStaticEnums(value: Any, enumValues: Seq[String]) = {
        if (value != "?" && !value.toString.startsWith("__ident__") && !enumValues.contains(value.toString)) {
          val err = s"'$value' is not among available enum values of attribute ${at.kwS}:\n  " +
            at.enumValues.sorted.mkString("\n  ")
          abort(err)
        }
        value
      }

      if (at == null || !at.isAnyEnum) {
        resolve(tree).map(extract).distinct
      } else {
        resolve(tree).map(extract).distinct.map(value =>
          validateStaticEnums(value, at.enumValues)
        )
      }
    }


    // Init ======================================================================================================

    val elements0: Seq[Element] = resolve(dsl)


    // Post-process optional nested structures

    // Re-generate if finding ".*?]" in dsl was not really a optional nested structure
    val forceNotOptNested      = isOptNested && !isOptNestedCheck
    val elements: Seq[Element] = if (forceNotOptNested) resolve(dsl, forceNotOptNested) else elements0

    if (isOptNested) {
      def markRefIndexes(elements: Seq[Element], level: Int): (Int, Int) = {
        elements.foldLeft(level, 0) {
          case ((_, _), Nested(Bond(_, refAttr, _, _, _), _)) if !refAttr.endsWith("$") =>
            abort("Optional nested structure can't be mixed with mandatory nested structure.")

          case ((l, _), Nested(_, es)) =>
            optNestedRefIndexes = optNestedRefIndexes :+ Nil
            markRefIndexes(es, l + 1)

          // Prevent input on level 0 (before nested structure)
          case ((0, _), a@Atom(_, _, _, _, value, _, _, _, _)) =>
            value match {
              case Qm | Eq(Seq(Qm)) | Neq(Seq(Qm)) | Lt(Qm) | Gt(Qm) | Le(Qm) | Ge(Qm) | Fulltext(Seq(Qm)) =>
                abort(s"Input not allowed in optional nested structures. Found: $a")

              case _ => (0, 0) // Expressions allowed before nested structure
            }
          case ((0, _), _)                                     => (0, 0)

          case ((_, _), b@Bond(_, _, _, 2, _)) =>
            abort(s"Flat card many ref not allowed with optional nesting. Found: $b")

          case ((l, i), _: Bond) =>
            optNestedRefIndexes = optNestedRefIndexes.init :+ (optNestedRefIndexes.last :+ i)
            (l, i + 1)

          case ((l, i), Atom(_, _, _, _, VarValue | EnumVal, _, _, _, _)) => (l, i + 1)

          case ((_, _), a@Atom(_, _, _, _, value, _, _, _, _)) =>
            value match {
              case Qm | Eq(Seq(Qm)) | Neq(Seq(Qm)) | Lt(Qm) | Gt(Qm) | Le(Qm) | Ge(Qm) | Fulltext(Seq(Qm)) =>
                abort(s"Input not allowed in optional nested structures. Found: $a")
              case _                                                                                       =>
                abort(s"Expressions not allowed within optional nested structures. Found: $a")
            }

          case ((l, i), _) => (l, i + 1)
        }
      }
      markRefIndexes(elements, 0)

      def markTacitIndexes(elements: Seq[Element], level: Int): (Int, Int) = {
        elements.foldLeft(level, 0) {
          case ((l, _), Nested(_, es)) =>
            optNestedTacitIndexes = optNestedTacitIndexes :+ Nil
            markTacitIndexes(es, l + 1)
          case ((0, _), _)             => (0, 0)

          case ((l, i), Atom(_, attr, _, _, _, _, _, _, _)) if attr.endsWith("_") =>
            optNestedTacitIndexes = optNestedTacitIndexes.init :+ (optNestedTacitIndexes.last :+ i)
            (l, i + 1)

          case ((l, i), _: Atom) => (l, i + 1)
          case ((l, i), _)       => (l, i)

        }
      }
      markTacitIndexes(elements, 0)
    }

    // Set outer objects ref and nested status

    @tailrec
    def streamline(element: Element): Obj = element match {
      case Atom(nsFull, _, _, _, _, _, _, _, _) => obj.copy(ref = nsFull, nested = false)
      case Bond(nsFull, _, _, _, _)             => obj.copy(ref = nsFull, nested = false)
      case Generic(nsFull, _, _, _, _)          => obj.copy(ref = nsFull, nested = false)
      case Composite(elements)                  => streamline(elements.head)
      case Nested(Bond(ns, _, _, _, _), _)      => obj.copy(ref = ns, nested = true)
      case other                                =>
        throw MoleculeException("Unexpected first model element: " + other)
    }
    val obj1 = streamline(elements.head)

    //    if (post) {
    //      // no nested, so transfer
    //      typess = List(postTypes)
    //      castss = List(postCasts)
    //      postTypes = Nil
    //      postCasts = Nil
    //      postJsons = Nil
    //    }
    //    //xx(801, elements)
    //    //xx(801, elements, types, casts)
    //xx(801, elements, typess, castss, nestedRefs, hasVariables, txMetas, post)
    //xx(802, obj1, typess, castss, txMetas)


    // Return checked model
    (
      genericImports,
      Model(VerifyRawModel(elements, allowTempGenerics = false)),
      typess, castss,
      obj1,
      nestedRefs, hasVariables, txMetas,
      postJsons,
      isOptNested,
      optNestedRefIndexes, optNestedTacitIndexes,
      sortIndexes.flatten.nonEmpty
    )
  }
}