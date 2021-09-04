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

      // Float abandoned
      //        case f: java.lang.Float       => "Float     " + f.toString
    }

    add(prefixed)
  }

  protected lazy val packOptNestedOne_ = (it: jIterator[_]) => add(it.next.toString)


  // packOptOne -------------------------------------------------------

  protected lazy val packOptNestedOptOneEnum   = (it: jIterator[_]) => {
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
  protected lazy val packOptNestedOptOneString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          => add(v.asInstanceOf[String])
    }
    end()
  }

  protected lazy val packOptNestedOptOne_ = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(v.toString)
    }
  }

  protected lazy val packOptNestedOptOneDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(date2strLocal(v.asInstanceOf[Date]))
    }
  }

  protected lazy val packOptNestedOptOneRefAttr = (it: jIterator[_]) => {
    it.next match {
      case "__none__" => end()
      case v          => add(v.asInstanceOf[Keyword].getName)
      //        var id   = ""
      //        var done = false
      //        // Hack to avoid looking up map by clojure Keyword - there must be a better way...
      //        v.asInstanceOf[jMap[_, _]].forEach {
      //          case _ if done                        =>
      //          case (k, v) if k.toString == ":db/id" => done = true; id = v.toString
      //          case _                                =>
      //        }
      //        add(id)
    }
  }


  // packMany -------------------------------------------------------

  //  protected lazy val packOptNestedManyEnum = (it: jIterator[_]) => {
  ////    val vs = it.next.asInstanceOf[jList[_]].iterator
  ////    while (vs.hasNext)
  ////      add(vs.next.toString)
  //    it.next.asInstanceOf[jList[_]].forEach(v => add(v.toString))
  //    end()
  //  }

  protected lazy val packOptNestedManyString = (it: jIterator[_]) => {
    //    val vs = it.next.asInstanceOf[jList[_]].iterator
    //    while (vs.hasNext) {
    //      add(vs.next.toString)
    //      end()
    //    }
    it.next.asInstanceOf[jList[_]].forEach { v =>
      add(v.toString)
      end()
    }
    end()
  }

  protected lazy val packOptNestedManyDate = (it: jIterator[_]) => {
    //    val vs = it.next.asInstanceOf[jList[_]].iterator
    //    while (vs.hasNext)
    //      add(date2strLocal(vs.next.asInstanceOf[Date]))
    it.next.asInstanceOf[jList[_]].forEach(v => add(date2strLocal(v.asInstanceOf[Date])))
    end()
  }

  protected lazy val packOptNestedMany_ = (it: jIterator[_]) => {
    //    val vs = it.next.asInstanceOf[jList[_]].iterator
    //    while (vs.hasNext)
    //      add(vs.next.toString)
    it.next.asInstanceOf[jList[_]].forEach(v => add(v.toString))
    end()
  }


  // packOptMany -------------------------------------------------------

  //  protected lazy val packOptNestedOptManyEnum = (it: jIterator[_]) => {
  //    it.next match {
  //      case "__none__" =>
  //      case vs         =>
  //        //        val vs = v.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
  ////        val vs = v.asInstanceOf[jList[_]].iterator
  ////        while (vs.hasNext)
  ////          add(vs.next.toString)
  //        vs.asInstanceOf[jList[_]].forEach(v => add(v.toString))
  //    }
  //    end()
  //  }

  protected lazy val packOptNestedOptManyString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         =>
        //        val vs = v.asInstanceOf[jList[_]].iterator
        //        while (vs.hasNext) {
        //          add(vs.next.toString)
        //          end()
        //        }
        vs.asInstanceOf[jList[_]].forEach { v =>
          add(v.toString)
          end()
        }
    }
    end()
  }

  protected lazy val packOptNestedOptManyDate = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         =>
        //        val vs = v.asInstanceOf[jList[_]].iterator
        //        while (vs.hasNext)
        //          add(date2strLocal(vs.next.asInstanceOf[Date]))
        vs.asInstanceOf[jList[_]].forEach(v => add(date2strLocal(v.asInstanceOf[Date])))
    }
    end()
  }

  protected lazy val packOptNestedOptManyRefAttr = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         =>
        //        v.asInstanceOf[jList[_]].forEach(v => println(v))
        //        val vs = v.asInstanceOf[jList[_]].iterator
        //        while (vs.hasNext) {
        //          //        // Hack to avoid looking up map by clojure Keyword - there must be a better way...
        //          //          var done = false
        //          //          it.next.asInstanceOf[jMap[_, _]].forEach {
        //          //            case _ if done                        =>
        //          //            case (k, v) if k.toString == ":db/id" => done = true; add(v.toString)
        //          //            case _                                =>
        //          //          }
        //          add(vs.next.asInstanceOf[Keyword].getName)
        //        }
        vs.asInstanceOf[jList[_]].forEach(v => add(v.asInstanceOf[Keyword].getName))
    }
    end()
  }

  protected lazy val packOptNestedOptMany_ = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case vs         =>
        //        val vs = v.asInstanceOf[jList[_]].iterator
        //        while (vs.hasNext)
        //          add(vs.next.toString)
        vs.asInstanceOf[jList[_]].forEach(v => add(v.toString))
    }
    end()
  }


  // packMap -------------------------------------------------------

  protected lazy val packOptNestedMapString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected lazy val packOptNestedMap_ = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }


  // packOptMap -------------------------------------------------------

  protected lazy val packOptNestedOptMapString = (it: jIterator[_]) => {
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

  protected lazy val packOptMap_ = (it: jIterator[_]) => {
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

  protected lazy val packOptNestedOptApplyOneString = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        add(v.asInstanceOf[String])
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
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext) {
          add(vs.next.toString)
          end()
        }
    }
    end()
  }

  protected lazy val packOptNestedOptApplyMany_ = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected lazy val packOptNestedOptApplyManyDate = (it: jIterator[_]) => {
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

  protected lazy val packOptNestedOptApplyMapString = (it: jIterator[_]) => {
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

  protected lazy val packOptNestedOptApplyMap_ = (it: jIterator[_]) => {
    it.next match {
      case "__none__" =>
      case v          =>
        val vs = v.asInstanceOf[jSet[_]].iterator
        while (vs.hasNext)
          add(vs.next.toString)
    }
    end()
  }

  protected lazy val packOptNestedOptApplyMapDate = (it: jIterator[_]) => {
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
