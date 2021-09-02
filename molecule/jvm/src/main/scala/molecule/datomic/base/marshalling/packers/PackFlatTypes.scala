package molecule.datomic.base.marshalling.packers

import java.util.{Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.DatomicRpc.date2strLocal
import molecule.datomic.base.marshalling.PackBase

trait PackFlatTypes extends PackBase with Helpers {

  // packOne -------------------------------------------------------
  protected lazy val packOneString = (colIndex: Int) => (row: jList[_]) => {
    add(row.get(colIndex).asInstanceOf[String])
    end()
  }

  protected lazy val packOne = (colIndex: Int) => (row: jList[_]) => add(row.get(colIndex).toString)

  protected lazy val packOneDate = (colIndex: Int) => (row: jList[_]) => add(date2strLocal(row.get(colIndex).asInstanceOf[Date]))

  // Generic `v` attribute value converted to String with appended type to be packed on JS side
  protected lazy val packOneAny = (colIndex: Int) => (row: jList[_]) => {
    val prefixed = row.get(colIndex) match {
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

  protected lazy val packOptOneEnum   = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end()
      case v    => add(
        getKwName(
          v.asInstanceOf[jMap[String, AnyRef]].values.iterator.next
            .asInstanceOf[jMap[_, _]].values.iterator.next.toString
        )
      )
    }
  }
  protected lazy val packOptOneString = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    => add(v.asInstanceOf[jMap[String, Any]].values.iterator().next.asInstanceOf[String])
    }
    end()
  }

  protected lazy val packOptOne = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end()
      case v    => add(v.asInstanceOf[jMap[String, Any]].values.iterator().next.toString)
    }
  }

  protected lazy val packOptOneDate = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end()
      case v    => add(date2strLocal(v.asInstanceOf[jMap[String, Any]].values.iterator().next.asInstanceOf[Date]))
    }
  }

  protected lazy val packOptOneRefAttr = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end()
      case v    =>
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

  protected lazy val packManyEnum = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected lazy val packManyString = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected lazy val packMany = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected lazy val packManyDate = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packOptMany -------------------------------------------------------

  protected lazy val packOptManyEnum = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected lazy val packOptManyString = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext) {
          add(vs.next.toString)
          end()
        }
    }
    end()
  }

  protected lazy val packOptMany = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected lazy val packOptManyDate = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(date2strLocal(vs.next.asInstanceOf[Date]))
    }
    end()
  }

  protected lazy val packOptManyRefAttr = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        // Hack to avoid looking up map by clojure Keyword - there must be a better way...
        while (vs.hasNext) {
          var done = false
          row.get(colIndex).asInstanceOf[jMap[_, _]].forEach {
            case _ if done                        =>
            case (k, v) if k.toString == ":db/id" => done = true; add(v.toString)
            case _                                =>
          }
        }
    }
    end()
  }


  // packMap -------------------------------------------------------

  protected lazy val packMapString = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected lazy val packMap = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }


  // packOptMap -------------------------------------------------------

  protected lazy val packOptMapString = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext) {
          add(vs.next.toString)
          end()
        }
    }
    end()
  }

  protected lazy val packOptMap = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }


  // packOptApplyOne -------------------------------------------------------

  protected lazy val packOptApplyOneString = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        add(v.asInstanceOf[String])
    }
    end()
  }

  protected lazy val packOptApplyOne = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end()
      case v    => add(v.toString)
    }
  }

  protected lazy val packOptApplyOneDate = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end()
      case v    => add(date2strLocal(v.asInstanceOf[Date]))
    }
  }



  // packOptApplyMany -------------------------------------------------------

  protected lazy val packOptApplyManyString = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext) {
          add(vs.next.toString)
          end()
        }
    }
    end()
  }

  protected lazy val packOptApplyMany = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected lazy val packOptApplyManyDate = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(date2strLocal(vs.next.asInstanceOf[Date]))
    }
    end()
  }


  //  packOptApplyMap -------------------------------------------------------

  protected lazy val packOptApplyMapString = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext) {
          add(vs.next.toString)
          end()
        }
    }
    end()
  }

  protected lazy val packOptApplyMap = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected lazy val packOptApplyMapDate = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(date2strLocal(vs.next.asInstanceOf[Date]))
    }
    end()
  }


  //  packKeyedMap -------------------------------------------------------

  protected lazy val packKeyedMapString = (colIndex: Int) => (row: jList[_]) => {
    add(row.get(colIndex).toString)
    end()
  }

  protected lazy val packKeyedMap     = (colIndex: Int) => (row: jList[_]) => add(row.get(colIndex).toString)
  protected lazy val packKeyedMapDate = (colIndex: Int) => (row: jList[_]) => add(date2strLocal(row.get(colIndex).asInstanceOf[Date]))

  // Generic `v` attribute value converted to String}
  protected lazy val packKeyedMapAny = (colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case s: String => add(s); end()
      case d: Date   => add(date2strLocal(d))
      case other     => add(other.toString)
    }
  }
}
