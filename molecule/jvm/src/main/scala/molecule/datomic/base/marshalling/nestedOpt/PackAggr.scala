package molecule.datomic.base.marshalling.nestedOpt

import java.util.{Date, Iterator => jIterator, List => jList, Set => jSet}
import molecule.datomic.base.marshalling.DatomicRpc.date2strLocal
import molecule.datomic.base.marshalling.PackBase

trait PackAggr extends PackBase {

  protected val packAggrInt    = (it: jIterator[_]) => add(it.next.toString)
  protected val packAggrDouble = (it: jIterator[_]) => add(it.next.toString)

  // packAggrOneList -----------------------------------------------------

  protected val packAggrOneListString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrOneList = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrOneListDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrManyList -----------------------------------------------------

  protected val packAggrManyListString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrManyList = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrManyListDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }



  // packAggrOneListDistinct -----------------------------------------------------

  protected val packAggrOneListDistinctString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrOneListDistinct = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrOneListDistinctDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrManyListDistinct -----------------------------------------------------

  protected val packAggrManyListDistinctString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrManyListDistinct = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrManyListDistinctDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrOneListRand -----------------------------------------------------

  protected val packAggrOneListRandString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrOneListRand = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrOneListRandDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrManyListRand -----------------------------------------------------

  protected val packAggrManyListRandString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrManyListRand = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrManyListRandDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrSingleSample -----------------------------------------------------

  protected val packAggrSingleSampleString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end()
  }

  protected val packAggrSingleSample = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val packAggrSingleSampleDate = (it: jIterator[_]) => {
    add(date2strLocal(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }


  // packAggrOneSingle -----------------------------------------------------

  protected val packAggrOneSingleString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end()
  }

  protected val packAggrOneSingle = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val packAggrOneSingleDate = (it: jIterator[_]) => {
    add(date2strLocal(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }


  // packAggrManySingle -----------------------------------------------------

  protected val packAggrManySingleString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[String])
    end()
  }

  protected val packAggrManySingle = (it: jIterator[_]) => {
    add(it.next.toString)
  }

  protected val packAggrManySingleDate = (it: jIterator[_]) => {
    add(date2strLocal(it.next.asInstanceOf[Date]))
  }
}
