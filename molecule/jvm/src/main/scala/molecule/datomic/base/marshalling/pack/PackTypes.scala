package molecule.datomic.base.marshalling.pack

import java.util.{Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.DatomicRpc.date2strLocal

trait PackTypes extends PackBase with Helpers {

  // packOne -------------------------------------------------------
  protected val packOneString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[String])
    end()
  }

  protected val packOne     = (it: jIterator[_]) => add(it.next.toString)
  protected val packOneDate = (it: jIterator[_]) => add(date2strLocal(it.next.asInstanceOf[Date]))

  // Generic `v` attribute value converted to String with appended type to be packed on JS side
  protected val packOneAny = (it: jIterator[_]) => {
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

      // Float abandoned
      //        case f: java.lang.Float       => "Float     " + f.toString
    }

    add(prefixed)
  }



  // packOptOne -------------------------------------------------------

  protected val packOptOneEnum   = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(
        getKwName(
          v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next
            .asInstanceOf[jMap[_, _]].values.iterator.next.toString
        )
      )
    }
  }
  protected val packOptOneString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          =>
        add(v.asInstanceOf[String])
        end()
    }
  }

  protected val packOptOne = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(v.toString)
    }
  }

  protected val packOptOneDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(date2strLocal(v.asInstanceOf[Date]))
    }
  }

  protected val packOptOneRefAttr = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          =>
        var id   = ""
        var done = false
        // Hack to avoid looking up map by clojure Keyword - there must be a better way...
        v.asInstanceOf[jMap[_, _]].forEach {
          case _ if done                        =>
          case (k, v) if k.toString == ":db/id" => done = true; id = v.toString
          case _                                =>
        }
        add(id)
    }
  }


  // packMany -------------------------------------------------------

  protected val packManyEnum = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packManyString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packMany = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packManyDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packOptMany -------------------------------------------------------

  protected val packOptManyEnum = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected val packOptManyString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext) {
          add(vs.next.toString)
          end()
        }
    }
    end()
  }

  protected val packOptMany = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected val packOptManyDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(date2strLocal(vs.next.asInstanceOf[Date]))
    }
    end()
  }

  protected val packOptManyRefAttr = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        // Hack to avoid looking up map by clojure Keyword - there must be a better way...
        while (vs.hasNext) {
          var done = false
          it.next.asInstanceOf[jMap[_, _]].forEach {
            case _ if done                        =>
            case (k, v) if k.toString == ":db/id" => done = true; add(v.toString)
            case _                                =>
          }
        }
    }
    end()
  }


  // packMap -------------------------------------------------------

  protected val packMapString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packMap = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }


  // packOptMap -------------------------------------------------------

  protected val packOptMapString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext) {
          add(vs.next.toString)
          end()
        }
    }
    end()
  }

  protected val packOptMap = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }


  // packOptApplyOne -------------------------------------------------------

  protected val packOptApplyOneString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        add(v.asInstanceOf[String])
    }
    end()
  }

  protected val packOptApplyOne = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(v.toString)
    }
  }

  protected val packOptApplyOneDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(date2strLocal(v.asInstanceOf[Date]))
    }
  }



  // packOptApplyMany -------------------------------------------------------

  protected val packOptApplyManyString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext) {
          add(vs.next.toString)
          end()
        }
    }
    end()
  }

  protected val packOptApplyMany = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected val packOptApplyManyDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(date2strLocal(vs.next.asInstanceOf[Date]))
    }
    end()
  }


  //  packOptApplyMap -------------------------------------------------------

  protected val packOptApplyMapString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext) {
          add(vs.next.toString)
          end()
        }
    }
    end()
  }

  protected val packOptApplyMap = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected val packOptApplyMapDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(date2strLocal(vs.next.asInstanceOf[Date]))
    }
    end()
  }


  //  packKeyedMap -------------------------------------------------------

  protected val packKeyedMapString = (it: jIterator[_]) => {
    add(it.next.toString)
    end()
  }

  protected val packKeyedMap     = (it: jIterator[_]) => add(it.next.toString)
  protected val packKeyedMapDate = (it: jIterator[_]) => add(date2strLocal(it.next.asInstanceOf[Date]))

  // Generic `v` attribute value converted to String}
  protected val packKeyedMapAny = (it: jIterator[_]) => {
    it.next match {
      case s: String => add(s); end()
      case d: Date   => add(date2strLocal(d))
      case other     => add(other.toString)
    }
  }
}
