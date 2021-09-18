package molecule.datomic.base.marshalling.packers

import java.util.{Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import clojure.lang.Keyword
import molecule.core.exceptions.MoleculeException
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.DatomicRpc.date2strLocal
import molecule.datomic.base.marshalling.PackBase

trait PackFlatTypes extends PackBase with Helpers {

  // packOne -------------------------------------------------------

  protected lazy val packOneString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    add(sb, row.get(colIndex).asInstanceOf[String])
    end(sb)
  }

  protected lazy val packOneDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) =>
    add(sb, date2strLocal(row.get(colIndex).asInstanceOf[Date]))

  // Generic `v` attribute value converted to String with appended type to be packed on JS side
  protected lazy val packOneAny = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val prefixed = row.get(colIndex) match {
      case s: java.lang.String      => "String    " + s
      case i: java.lang.Integer     => "Int       " + i.toString
      case l: java.lang.Long        => "Long      " + l.toString
      case d: java.lang.Double      => "Double    " + d.toString
      case b: java.lang.Boolean     => "Boolean   " + b.toString
      case d: Date                  => "Date      " + date2strLocal(d)
      case u: UUID                  => "UUID      " + u.toString
      case u: java.net.URI          => "URI       " + u.toString
      case bi: java.math.BigInteger => "BigInt    " + bi.toString
      case bi: clojure.lang.BigInt  => "BigInt    " + bi.toString
      case bd: java.math.BigDecimal => "BigDecimal" + bd.toString
      case other                    =>
        throw MoleculeException(s"Unexpected generic `v` $other of type " + other.getClass)
    }
    add(sb, prefixed)
  }

  protected lazy val packOneRefAttr = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) =>
    add(sb,
      row.get(colIndex).toString
//      row.get(colIndex).asInstanceOf[Keyword].getName
//      row.get(colIndex).asInstanceOf[jMap[String, Any]].values.iterator.next.toString
    )

  protected lazy val packOne = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) =>
    add(sb, row.get(colIndex).toString)


  // packOptOne -------------------------------------------------------

  protected lazy val packOptOneString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    => add(sb, v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[String])
    }
    end(sb)
  }

  protected lazy val packOptOneDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end(sb)
      case v    => add(sb,
        date2strLocal(v.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[Date])
      )
    }
  }

  protected lazy val packOptOneEnum = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end(sb)
      case v    => add(sb,
        v.asInstanceOf[jMap[String, Any]].values.iterator.next
          .asInstanceOf[jMap[String, Any]].values.iterator.next
          .asInstanceOf[Keyword].getName
      )
    }
  }

  protected lazy val packOptOneRefAttr = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end(sb)
      case v    => add(sb,
        v.asInstanceOf[jMap[String, Any]].values.iterator.next
          .asInstanceOf[jMap[String, Any]].values.iterator.next.toString
      )
    }
  }

  protected lazy val packOptOne = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end(sb)
      case v    =>
        add(sb, v.asInstanceOf[jMap[String, Any]].values.iterator().next.toString)
    }
  }


  // packMany -------------------------------------------------------

  protected lazy val packManyString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex).asInstanceOf[jSet[_]].forEach { v =>
      add(sb, v.toString)
      end(sb)
    }
    end(sb)
  }

  protected lazy val packManyDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex).asInstanceOf[jSet[_]].forEach(v => add(sb, date2strLocal(v.asInstanceOf[Date])))
    end(sb)
  }

  protected lazy val packManyEnum = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex).asInstanceOf[jSet[_]].forEach(v => add(sb, v.toString))
    end(sb)
  }

  protected lazy val packManyRefAttr = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex).asInstanceOf[jSet[_]].forEach(v => add(sb, v.toString))
    end(sb)
  }

  protected lazy val packMany = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex).asInstanceOf[jSet[_]].forEach(v => add(sb, v.toString))
    end(sb)
  }


  // packOptMany -------------------------------------------------------

  protected lazy val packOptManyString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (it.hasNext) {
          add(sb, it.next.toString)
          end(sb)
        }
    }
    end(sb)
  }

  protected lazy val packOptManyDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (it.hasNext)
          add(sb, date2strLocal(it.next.asInstanceOf[Date]))
    }
    end(sb)
  }

  protected lazy val packOptManyEnum = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (it.hasNext)
          add(sb, it.next.asInstanceOf[jMap[String, Any]].values.iterator.next.asInstanceOf[Keyword].getName)
    }
    end(sb)
  }

  protected lazy val packOptManyRefAttr = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (it.hasNext)
          add(sb, it.next.asInstanceOf[jMap[String, Any]].values.iterator.next.toString)
    }
    end(sb)
  }

  protected lazy val packOptMany = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (it.hasNext)
          add(sb, it.next.toString)
    }
    end(sb)
  }


  // packMap -------------------------------------------------------

  protected lazy val packMapString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val it = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext) {
      add(sb, it.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected lazy val packMap = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val it = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (it.hasNext)
      add(sb, it.next.toString)
    end(sb)
  }


  // packOptMap -------------------------------------------------------

  protected lazy val packOptMapString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (it.hasNext) {
          add(sb, it.next.toString)
          end(sb)
        }
    }
    end(sb)
  }

  protected lazy val packOptMap = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jMap[String, jList[_]]].values.iterator.next.iterator
        while (it.hasNext)
          add(sb, it.next.toString)
    }
    end(sb)
  }


  // packOptApplyOne -------------------------------------------------------

  protected lazy val packOptApplyOneString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case v    => add(sb, v.asInstanceOf[String])
    }
    end(sb)
  }

  protected lazy val packOptApplyOne = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end(sb)
      case v    => add(sb, v.toString)
    }
  }

  protected lazy val packOptApplyOneDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null => end(sb)
      case v    => add(sb, date2strLocal(v.asInstanceOf[Date]))
    }
  }



  // packOptApplyMany -------------------------------------------------------

  protected lazy val packOptApplyManyString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jSet[_]].iterator
        while (it.hasNext) {
          add(sb, it.next.toString)
          end(sb)
        }
    }
    end(sb)
  }

  protected lazy val packOptApplyMany = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jSet[_]].iterator
        while (it.hasNext)
          add(sb, it.next.toString)
    }
    end(sb)
  }

  protected lazy val packOptApplyManyDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jSet[_]].iterator
        while (it.hasNext)
          add(sb, date2strLocal(it.next.asInstanceOf[Date]))
    }
    end(sb)
  }


  //  packOptApplyMap -------------------------------------------------------

  protected lazy val packOptApplyMapString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jSet[_]].iterator
        while (it.hasNext) {
          add(sb, it.next.toString)
          end(sb)
        }
    }
    end(sb)
  }

  protected lazy val packOptApplyMap = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jSet[_]].iterator
        while (it.hasNext)
          add(sb, it.next.toString)
    }
    end(sb)
  }

  protected lazy val packOptApplyMapDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    row.get(colIndex) match {
      case null =>
      case vs   =>
        val it = vs.asInstanceOf[jSet[_]].iterator
        while (it.hasNext)
          add(sb, date2strLocal(it.next.asInstanceOf[Date]))
    }
    end(sb)
  }


//  //  packKeyedMap -------------------------------------------------------
//
//  protected lazy val packKeyedMapString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
//    add(sb, row.get(colIndex).toString)
//    end(sb)
//  }
}
