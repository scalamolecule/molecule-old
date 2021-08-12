package molecule.core.macros.attrResolvers

import java.util.{Collection => jCollection, Iterator => jIterator, List => jList, Map => jMap}
import molecule.core.util.Helpers;


private[molecule] trait JsonBase extends Helpers {


  // Shamelessly adopted from lift-json:
  // https://github.com/lift/framework/blob/db05d863c290c5fd1081a7632263433153fc9fe3/core/json/src/main/scala/net/liftweb/json/JsonAST.scala#L813-L883

  /**
    * Ranges of chars that should be escaped if this JSON is to be evaluated
    * directly as JavaScript (rather than by a valid JSON parser).
    */
  protected val jsEscapeChars: Set[Char] =
    List(
      ('\u00ad', '\u00ad'),
      ('\u0600', '\u0604'),
      ('\u070f', '\u070f'),
      ('\u17b4', '\u17b5'),
      ('\u200c', '\u200f'),
      ('\u2028', '\u202f'),
      ('\u2060', '\u206f'),
      ('\ufeff', '\ufeff'),
      ('\ufff0', '\uffff')
    ).foldLeft(Set[Char]()) {
      case (set, (start, end)) =>
        set ++ (start to end).toSet
    }

  protected def appendEscapedString(sb: StringBuffer, s: String): Unit = {
    s.foreach { c =>
      val strReplacement = c match {
        case '"'  => "\\\""
        case '\\' => "\\\\"
        case '\b' => "\\b"
        case '\f' => "\\f"
        case '\n' => "\\n"
        case '\r' => "\\r"
        case '\t' => "\\t"
        // Set.contains will cause boxing of c to Character, try and avoid this
        case c if (c >= '\u0000' && c < '\u0020') || jsEscapeChars.contains(c) =>
          "\\u%04x".format(c: Int)

        case _ => ""
      }

      // Use Char version of append if we can, as it's cheaper.
      if (strReplacement.isEmpty) {
        sb.append(c)
      } else {
        sb.append(strReplacement)
      }
    }
  }

  protected def quote(sb: StringBuffer, s: String): StringBuffer = {
    sb.append('"') //open quote
    appendEscapedString(sb, s)
    sb.append('"') //close quote
  }

  protected def quotedPair(sb: StringBuffer, field: String, value: String): StringBuffer = {
    quote(sb, field)
    sb.append(": ")
    quote(sb, value)
  }

  protected def pair(sb: StringBuffer, field: String, value: Any): StringBuffer = {
    quote(sb, field)
    sb.append(": ")
    sb.append(value)
  }

  protected def jsonAnyValue(sb: StringBuffer, v: Any): StringBuffer = v match {
    case value: String         => quote(sb, value)
    case value: Int            => sb.append(value)
    case value: Float          => sb.append(value)
    case value: Boolean        => sb.append(value)
    case value: Long           => sb.append(value)
    case value: Double         => sb.append(value)
    case value: java.util.Date => quote(sb, date2str(value))
    case value: java.util.UUID => quote(sb, value.toString)
    case value: java.net.URI   => quote(sb, value.toString)
    case value: BigInt         => sb.append(value)
    case value: BigDecimal     => sb.append(value)
    case valueOfUnknownType    => quote(sb, valueOfUnknownType.toString)
  }

  def indent(tabs: Int): String = "\n" + "  " * (3 + tabs)

  def extractFlatValues(
    nestedRows: jList[Any],
    propCount: Int,
    refIndexes: List[Int],
    tacitIndexes: List[Int],
    deeper: Boolean = false
  ): jIterator[Any] = {
    val rowCount             = nestedRows.size()
    val flatValues           = new java.util.ArrayList[Any](rowCount * propCount)
    val nonTacitIndexes      = (0 until propCount).diff(tacitIndexes)
    var testArray            = new Array[AnyRef](propCount)
    val testList             = new java.util.ArrayList[Any](propCount)
    var vs: jCollection[Any] = null
    var i                    = 0

    //    println("================================")
    //    println("nestedRows      : " + nestedRows)
    //    println("propCount       : " + propCount)
    //    println("refIndexes      : " + refIndexes)
    //    println("tacitIndexes    : " + tacitIndexes)
    //    println("deeper          : " + deeper)

    (refIndexes.isEmpty, tacitIndexes.isEmpty) match {
      case (true, true) =>
        nestedRows.forEach { row =>
          vs = row.asInstanceOf[jMap[Any, Any]].values()
          //          println("-- 1 ------- " + vs.size + "  " + propCount)
          //          vs.forEach(v => println(v))
          if (deeper && vs.size() - 1 == propCount || !deeper && vs.size() == propCount)
            flatValues.addAll(vs)
        }

      case (true, false) =>
        nestedRows.forEach { row =>
          vs = row.asInstanceOf[jMap[Any, Any]].values()
          testArray = vs.toArray
          // Skip all values on this level if some tacit value is missing
          val valid: Boolean = tacitIndexes.collectFirst {
            case i if testArray(i) == "__none__" => true
          }.isEmpty
          //          println("-- 2 ------- " + valid)
          //          vs.forEach(v => println(v))
          if (valid) {
            // Get non-tacit values only
            nonTacitIndexes.foreach { j =>
              flatValues.add(testArray(j))
            }
            if (deeper) {
              // add last
              flatValues.add(testArray(vs.size() - 1))
            }
          }
        }

      case (false, true) =>
        nestedRows.forEach { row =>
          vs = row.asInstanceOf[jMap[Any, Any]].values()
          testList.clear()
          i = 0
          def addValues(vs: jCollection[Any]): Unit = vs.forEach {
            case ref: jMap[_, _] => addValues(ref.asInstanceOf[jMap[Any, Any]].values())
            case v               => i += 1; testList.add(v)
          }
          addValues(vs)
          //          println("-- 3 -------")
          //          testList.forEach(v => println(v))
          if (i == propCount)
            flatValues.addAll(testList)
        }

      case (false, false) =>
        val ok = (presentValues: Int) => presentValues == propCount + tacitIndexes.size
        nestedRows.forEach { row =>
          vs = row.asInstanceOf[jMap[Any, Any]].values()
          testList.clear()
          var presentValues = 0
          i = 0
          def addValues(vs: jCollection[Any]): Unit = vs.forEach {
            case ref: jMap[_, _]                        =>
              //              println(s"-  -  -  " + ref.asInstanceOf[jMap[Any, Any]].values())
              addValues(ref.asInstanceOf[jMap[Any, Any]].values())
            case "__none__" if tacitIndexes.contains(i) =>
              // tacit value missing
              i += 1
            case v if tacitIndexes.contains(i) =>
              // tacit value exists
              i += 1
              presentValues += 1
            case v =>
              i += 1
              presentValues += 1
              testList.add(v)
          }
          addValues(vs)
          val ok1 = ok(presentValues)
          //          println("-- 4 ------- " + presentValues + "  " + ok1)
          //          vs.forEach(v => println(v))
          //          println("-------")
          //          testList.forEach(v => println(v))
          if (ok1)
            flatValues.addAll(testList)
        }
    }

    //    println("-------------------------------")
    //    flatValues.forEach(v => println(v))

    flatValues.iterator
  }

}
