package molecule.datomic.base.marshalling.packers

import java.util.{Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import clojure.lang.Keyword
import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.PackBase


private[molecule] trait PackOptNestedTypes extends PackBase with Helpers {

  // packOne -------------------------------------------------------

  protected lazy val packOptNestedOneString = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, it.next.asInstanceOf[String])
    end(sb)
  }

  protected lazy val packOptNestedOneDate = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, date2str(it.next.asInstanceOf[Date]))
  }

  // Generic `v` attribute value converted to String with appended type to be packed on JS side
  protected lazy val packOptNestedOneAny = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case s: java.lang.String      => add(sb, "String    " + s); end(sb) // String can be multi-line
      case i: java.lang.Integer     => add(sb, "Int       " + i.toString)
      case l: java.lang.Long        => add(sb, "Long      " + l.toString)
      case d: java.lang.Double      => add(sb, "Double    " + d.toString)
      case b: java.lang.Boolean     => add(sb, "Boolean   " + b.toString)
      case d: Date                  => add(sb, "Date      " + date2str(d))
      case u: UUID                  => add(sb, "UUID      " + u.toString)
      case u: java.net.URI          => add(sb, "URI       " + u.toString)
      case bi: java.math.BigInteger => add(sb, "BigInt    " + bi.toString)
      case bi: clojure.lang.BigInt  => add(sb, "BigInt    " + bi.toString)
      case bd: java.math.BigDecimal => add(sb, "BigDecimal" + bd.toString)
      case other                    =>
        throw MoleculeException(s"Unexpected packOptNestedOneAny value `$other` of type " + other.getClass)
    }
  }

  protected lazy val packOptNestedOneEnum = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case kw: Keyword   => add(sb, kw.getName)
      case m: jMap[_, _] => add(sb, m.values.iterator.next.asInstanceOf[Keyword].getName)
    }
  }

  protected lazy val packOptNestedOneRefAttr = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, it.next.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
  }

  protected lazy val packOptNestedOne_ = (sb: StringBuffer, it: jIterator[_]) =>
    add(sb, it.next.toString)


  // packOptOne -------------------------------------------------------

  protected lazy val packOptNestedOptOneString = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case null | "__none__" =>
      case v: jMap[_, _]     => add(sb, v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[String])
      case v                 => add(sb, v.asInstanceOf[String])
    }
    end(sb)
  }

  protected lazy val packOptNestedOptOneDate = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case null | "__none__" => end(sb)
      case v: jMap[_, _]     => add(sb, date2str(v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[Date]))
      case d: Date           => add(sb, date2str(d))
    }
  }

  protected lazy val packOptNestedOptOneEnum = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case null | "__none__" => end(sb)
      case m: jMap[_, _]     => add(sb, m.values.iterator.next.asInstanceOf[Keyword].getName)
      case kw: Keyword       => add(sb, kw.getName)
    }
  }

  protected lazy val packOptNestedOptOneRefAttr = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case null | "__none__" => end(sb)
      case v: jMap[_, _]     => add(sb, v.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
      case v                 => add(sb, v.toString)
    }
  }

  protected lazy val packOptNestedOptOne_ = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case null | "__none__" => end(sb)
      case v: jMap[_, _]     => add(sb, v.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
      case v                 => add(sb, v.toString)
    }
  }


  // packMany -------------------------------------------------------

  protected lazy val packOptNestedManyString = (sb: StringBuffer, it: jIterator[_]) => {
    it.next.asInstanceOf[jList[_]].forEach { v =>
      add(sb, v.toString)
      end(sb)
    }
    end(sb)
  }

  protected lazy val packOptNestedManyDate = (sb: StringBuffer, it: jIterator[_]) => {
    it.next.asInstanceOf[jList[_]].forEach(v => add(sb, date2str(v.asInstanceOf[Date])))
    end(sb)
  }

  protected lazy val packOptNestedManyEnum = (sb: StringBuffer, it: jIterator[_]) => {
    it.next.asInstanceOf[jList[_]].forEach(v =>
      add(sb, v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[Keyword].getName)
    )
    end(sb)
  }

  protected lazy val packOptNestedManyRefAttr = (sb: StringBuffer, it: jIterator[_]) => {
    it.next.asInstanceOf[jList[_]].forEach(v =>
      add(sb, v.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
    )
    end(sb)
  }

  protected lazy val packOptNestedMany_ = (sb: StringBuffer, it: jIterator[_]) => {
    it.next.asInstanceOf[jList[_]].forEach(v => add(sb, v.toString))
    end(sb)
  }


  // packOptMany -------------------------------------------------------

  protected lazy val packOptNestedOptManyString = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach { v =>
        add(sb, v.toString)
        end(sb)
      }
    }
    end(sb)
  }

  protected lazy val packOptNestedOptManyDate = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach(v => add(sb, date2str(v.asInstanceOf[Date])))
    }
    end(sb)
  }

  protected lazy val packOptNestedOptManyEnum = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach(v =>
        add(sb, v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[Keyword].getName)
      )
    }
    end(sb)
  }

  protected lazy val packOptNestedOptManyRefAttr = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach(v =>
        add(sb, v.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
      )
    }
    end(sb)
  }

  protected lazy val packOptNestedOptMany_ = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach(v => add(sb, v.toString))
    }
    end(sb)
  }


  // packMap -------------------------------------------------------

  protected lazy val packOptNestedMapString = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected lazy val packOptNestedMap_ = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }


  // packOptMap -------------------------------------------------------

  protected lazy val packOptNestedOptMapString = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach { v =>
        add(sb, v.toString)
        end(sb)
      }
    }
    end(sb)
  }

  protected lazy val packOptNestedOptMap_ = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach(v => add(sb, v.toString))
    }
    end(sb)
  }


  // packOptApplyOne -------------------------------------------------------

  protected lazy val packOptNestedOptApplyOneString = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          => add(sb, v.asInstanceOf[String])
    }
    end(sb)
  }

  protected lazy val packOptNestedOptApplyOne_ = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" => end(sb)
      case v          => add(sb, v.toString)
    }
  }

  protected lazy val packOptNestedOptApplyOneDate = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" => end(sb)
      case v          => add(sb, date2str(v.asInstanceOf[Date]))
    }
  }



  // packOptApplyMany -------------------------------------------------------

  protected lazy val packOptNestedOptApplyManyString = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach { v =>
        add(sb, v.toString)
        end(sb)
      }
    }
    end(sb)
  }

  protected lazy val packOptNestedOptApplyMany_ = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach(v => add(sb, v.toString))
    }
    end(sb)
  }

  protected lazy val packOptNestedOptApplyManyDate = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach(v => add(sb, date2str(v.asInstanceOf[Date])))
    }
    end(sb)
  }


  //  packOptApplyMap -------------------------------------------------------

  protected lazy val packOptNestedOptApplyMapString = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach { v =>
        add(sb, v.toString)
        end(sb)
      }
    }
    end(sb)
  }

  protected lazy val packOptNestedOptApplyMap_ = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach(v => add(sb, v.toString))
    }
    end(sb)
  }

  protected lazy val packOptNestedOptApplyMapDate = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach(v => add(sb, date2str(v.asInstanceOf[Date])))
    }
    end(sb)
  }


  //  packKeyedMap -------------------------------------------------------

  protected lazy val packOptNestedKeyedMapString = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, it.next.toString)
    end(sb)
  }

  protected lazy val packOptNestedKeyedMap_ = (sb: StringBuffer, it: jIterator[_]) =>
    add(sb, it.next.toString)

  protected lazy val packOptNestedKeyedMapDate = (sb: StringBuffer, it: jIterator[_]) =>
    add(sb, date2str(it.next.asInstanceOf[Date]))

  // Generic `v` attribute value converted to String}
  protected lazy val packKeyedMapAny = (sb: StringBuffer, it: jIterator[_]) => {
    it.next match {
      case s: String => add(sb, s); end(sb)
      case d: Date   => add(sb, date2str(d))
      case other     => add(sb, other.toString)
    }
  }
}
