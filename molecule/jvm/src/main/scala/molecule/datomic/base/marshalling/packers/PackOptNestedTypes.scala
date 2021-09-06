package molecule.datomic.base.marshalling.packers

import java.util.{Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import clojure.lang.Keyword
import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.DatomicRpc.date2strLocal
import molecule.datomic.base.marshalling.PackBase


trait PackOptNestedTypes extends PackBase with Helpers {

  // packOne -------------------------------------------------------

  protected lazy val packOptNestedOneString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[String])
    end()
  }

  protected lazy val packOptNestedOneDate = (it: jIterator[_]) => add(date2strLocal(it.next.asInstanceOf[Date]))

  // Generic `v` attribute value converted to String with appended type to be packed on JS side
  protected lazy val packOptNestedOneAny = (it: jIterator[_]) => {
    val prefixed = it.next match {
      case s: java.lang.String      => "String    " + s
      case i: java.lang.Integer     => "Integer   " + i.toString
      case l: java.lang.Long        => "Long      " + l.toString
      case d: java.lang.Double      => "Double    " + d.toString
      case b: java.lang.Boolean     => "Boolean   " + b.toString
      case d: Date                  => "Date      " + date2strLocal(d)
      case u: UUID                  => "UUID      " + u.toString
      case u: java.net.URI          => "URI       " + u.toString
      case bi: java.math.BigInteger => "BigInteger" + bi.toString
      case bi: clojure.lang.BigInt  => "BigInteger" + bi.toString
      case bd: java.math.BigDecimal => "BigDecimal" + bd.toString
      case other                    =>
        throw MoleculeException(s"Unexpected generic `v` $other of type " + other.getClass)
    }
    add(prefixed)
  }

  protected lazy val packOptNestedOneEnum = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[Keyword].getName)
  }

  protected lazy val packOptNestedOneRefAttr = (it: jIterator[_]) => {
    //    add(it.next.asInstanceOf[Keyword].getName)
    add(it.next.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
  }

  protected lazy val packOptNestedOne_ = (it: jIterator[_]) =>
    add(it.next.toString)


  // packOptOne -------------------------------------------------------

  protected lazy val packOptNestedOptOneString = (it: jIterator[_]) => {
    it.next match {
      case null | "__none__" =>
      case v: jMap[_, _]     => add(v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[String])
      case v                 => add(v.asInstanceOf[String])
    }
    end()
  }

  protected lazy val packOptNestedOptOneDate = (it: jIterator[_]) => {
    it.next match {
      case null | "__none__" => end()
      case v: jMap[_, _]     => add(date2strLocal(v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[Date]))
      case d: Date           => add(date2strLocal(d))
    }
  }

  protected lazy val packOptNestedOptOneEnum = (it: jIterator[_]) => {
    it.next match {
      case null | "__none__" => end()
      case v: jMap[_, _]     => add(v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[Keyword].getName)
      case v                 => add(v.asInstanceOf[Keyword].getName)
    }
  }

  protected lazy val packOptNestedOptOneRefAttr = (it: jIterator[_]) => {
    it.next match {
      case null | "__none__" => end()
      case v: jMap[_, _]     => add(v.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
      case v                 => add(v.toString)
    }
  }

  protected lazy val packOptNestedOptOne_ = (it: jIterator[_]) => {
    it.next match {
      case null | "__none__" => end()
      case v: jMap[_, _]     => add(v.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
      case v                 => add(v.toString)
    }
  }


  // packMany -------------------------------------------------------

  protected lazy val packOptNestedManyString = (it: jIterator[_]) => {
    it.next.asInstanceOf[jList[_]].forEach { v =>
      add(v.toString)
      end()
    }
    end()
  }

  protected lazy val packOptNestedManyDate = (it: jIterator[_]) => {
    it.next.asInstanceOf[jList[_]].forEach(v => add(date2strLocal(v.asInstanceOf[Date])))
    end()
  }

  protected lazy val packOptNestedManyEnum = (it: jIterator[_]) => {
    it.next.asInstanceOf[jList[_]].forEach(v =>
      add(v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[Keyword].getName)
    )
    end()
  }

  protected lazy val packOptNestedManyRefAttr = (it: jIterator[_]) => {
    it.next.asInstanceOf[jList[_]].forEach(v =>
      add(v.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
    )
    end()
  }

  protected lazy val packOptNestedMany_ = (it: jIterator[_]) => {
    it.next.asInstanceOf[jList[_]].forEach(v => add(v.toString))
    end()
  }


  // packOptMany -------------------------------------------------------

  protected lazy val packOptNestedOptManyString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach { v =>
        add(v.toString)
        end()
      }
    }
    end()
  }

  protected lazy val packOptNestedOptManyDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach(v => add(date2strLocal(v.asInstanceOf[Date])))
    }
    end()
  }

  protected lazy val packOptNestedOptManyEnum = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach(v =>
        add(v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[Keyword].getName)
      )
    }
    end()
  }

  protected lazy val packOptNestedOptManyRefAttr = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach(v =>
        add(v.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
      )
    }
    end()
  }

  protected lazy val packOptNestedOptMany_ = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach(v => add(v.toString))
    }
    end()
  }


  // packMap -------------------------------------------------------

  protected lazy val packOptNestedMapString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected lazy val packOptNestedMap_ = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }


  // packOptMap -------------------------------------------------------

  protected lazy val packOptNestedOptMapString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach { v =>
        add(v.toString)
        end()
      }
    }
    end()
  }

  protected lazy val packOptNestedOptMap_ = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jList[_]].forEach(v => add(v.toString))
    }
    end()
  }


  // packOptApplyOne -------------------------------------------------------

  protected lazy val packOptNestedOptApplyOneString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          => add(v.asInstanceOf[String])
    }
    end()
  }

  protected lazy val packOptNestedOptApplyOne_ = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(v.toString)
    }
  }

  protected lazy val packOptNestedOptApplyOneDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(date2strLocal(v.asInstanceOf[Date]))
    }
  }



  // packOptApplyMany -------------------------------------------------------

  protected lazy val packOptNestedOptApplyManyString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach { v =>
        add(v.toString)
        end()
      }
    }
    end()
  }

  protected lazy val packOptNestedOptApplyMany_ = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach(v => add(v.toString))
    }
    end()
  }

  protected lazy val packOptNestedOptApplyManyDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach(v => add(date2strLocal(v.asInstanceOf[Date])))
    }
    end()
  }


  //  packOptApplyMap -------------------------------------------------------

  protected lazy val packOptNestedOptApplyMapString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach { v =>
        add(v.toString)
        end()
      }
    }
    end()
  }

  protected lazy val packOptNestedOptApplyMap_ = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach(v => add(v.toString))
    }
    end()
  }

  protected lazy val packOptNestedOptApplyMapDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         => vs.asInstanceOf[jSet[_]].forEach(v => add(date2strLocal(v.asInstanceOf[Date])))
    }
    end()
  }


  //  packKeyedMap -------------------------------------------------------

  protected lazy val packOptNestedKeyedMapString = (it: jIterator[_]) => {
    add(it.next.toString)
    end()
  }

  protected lazy val packOptNestedKeyedMap_    = (it: jIterator[_]) => add(it.next.toString)
  protected lazy val packOptNestedKeyedMapDate = (it: jIterator[_]) => add(date2strLocal(it.next.asInstanceOf[Date]))

  // Generic `v` attribute value converted to String}
  protected lazy val packKeyedMapAny = (it: jIterator[_]) => {
    it.next match {
      case s: String => add(s); end()
      case d: Date   => add(date2strLocal(d))
      case other     => add(other.toString)
    }
  }
}
