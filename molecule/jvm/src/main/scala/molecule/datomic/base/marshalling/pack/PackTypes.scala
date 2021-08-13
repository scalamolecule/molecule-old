package molecule.datomic.base.marshalling.pack

import java.util.{Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.DatomicRpc.date2strLocal


trait PackTypes extends PackBase with Helpers {

  // packOne -------------------------------------------------------
  protected lazy val packOneString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[String])
    end()
  }

  protected lazy val packOne     = (it: jIterator[_]) => add(it.next.toString)
  protected lazy val packOneDate = (it: jIterator[_]) => add(date2strLocal(it.next.asInstanceOf[Date]))

  // Generic `v` attribute value converted to String with appended type to be packed on JS side
  protected lazy val packOneAny = (it: jIterator[_]) => {
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

  protected lazy val packOptOneEnum   = (it: jIterator[_]) => {
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
  protected lazy val packOptOneString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          =>
        add(v.asInstanceOf[String])
        end()
    }
  }

  protected lazy val packOptOne = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(v.toString)
    }
  }

  protected lazy val packOptOneDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(date2strLocal(v.asInstanceOf[Date]))
    }
  }

  protected lazy val packOptOneRefAttr = (it: jIterator[_]) => {
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

  protected lazy val packManyEnum = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected lazy val packManyString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected lazy val packMany = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected lazy val packManyDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packOptMany -------------------------------------------------------

  protected lazy val packOptManyEnum = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected lazy val packOptManyString = (it: jIterator[_]) => {
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

  protected lazy val packOptMany = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected lazy val packOptManyDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(date2strLocal(vs.next.asInstanceOf[Date]))
    }
    end()
  }

  protected lazy val packOptManyRefAttr = (it: jIterator[_]) => {
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

  protected lazy val packMapString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected lazy val packMap = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }


  // packOptMap -------------------------------------------------------

  protected lazy val packOptMapString = (it: jIterator[_]) => {
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

  protected lazy val packOptMap = (it: jIterator[_]) => {
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

  protected lazy val packOptApplyOneString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        add(v.asInstanceOf[String])
    }
    end()
  }

  protected lazy val packOptApplyOne = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(v.toString)
    }
  }

  protected lazy val packOptApplyOneDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(date2strLocal(v.asInstanceOf[Date]))
    }
  }



  // packOptApplyMany -------------------------------------------------------

  protected lazy val packOptApplyManyString = (it: jIterator[_]) => {
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

  protected lazy val packOptApplyMany = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected lazy val packOptApplyManyDate = (it: jIterator[_]) => {
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

  protected lazy val packOptApplyMapString = (it: jIterator[_]) => {
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

  protected lazy val packOptApplyMap = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected lazy val packOptApplyMapDate = (it: jIterator[_]) => {
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

  protected lazy val packKeyedMapString = (it: jIterator[_]) => {
    add(it.next.toString)
    end()
  }

  protected lazy val packKeyedMap     = (it: jIterator[_]) => add(it.next.toString)
  protected lazy val packKeyedMapDate = (it: jIterator[_]) => add(date2strLocal(it.next.asInstanceOf[Date]))

  // Generic `v` attribute value converted to String}
  protected lazy val packKeyedMapAny = (it: jIterator[_]) => {
    it.next match {
      case s: String => add(s); end()
      case d: Date   => add(date2strLocal(d))
      case other     => add(other.toString)
    }
  }
}
