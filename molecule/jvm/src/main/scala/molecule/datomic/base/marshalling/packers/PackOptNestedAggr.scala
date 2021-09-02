package molecule.datomic.base.marshalling.packers

import java.util.{Date, Iterator => jIterator, List => jList, Set => jSet}
import molecule.datomic.base.marshalling.DatomicRpc.date2strLocal
import molecule.datomic.base.marshalling.PackBase

trait PackOptNestedAggr extends PackBase {

  protected val packAggrInt    = (it: jIterator[_]) => add(it.next.toString)
  protected val packAggrDouble = (it: jIterator[_]) => add(it.next.toString)

  // packAggrOneList -----------------------------------------------------

  protected val packOptNestedAggrOneListString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packOptNestedAggrOneList_ = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packOptNestedAggrOneListDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrManyList -----------------------------------------------------

  protected val packOptNestedAggrManyListString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packOptNestedAggrManyList_ = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packOptNestedAggrManyListDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }



  // packAggrOneListDistinct -----------------------------------------------------

  protected val packOptNestedAggrOneListDistinctString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packOptNestedAggrOneListDistinct_ = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packOptNestedAggrOneListDistinctDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrManyListDistinct -----------------------------------------------------

  protected val packOptNestedAggrManyListDistinctString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packOptNestedAggrManyListDistinct_ = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packOptNestedAggrManyListDistinctDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrOneListRand -----------------------------------------------------

  protected val packOptNestedAggrOneListRandString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packOptNestedAggrOneListRand_ = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packOptNestedAggrOneListRandDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrManyListRand -----------------------------------------------------

  protected val packOptNestedAggrManyListRandString = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packOptNestedAggrManyListRand_ = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packOptNestedAggrManyListRandDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrSingleSample -----------------------------------------------------

  protected val packOptNestedAggrSingleSampleString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end()
  }

  protected val packOptNestedAggrSingleSample_ = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val packOptNestedAggrSingleSampleDate = (it: jIterator[_]) => {
    add(date2strLocal(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }


  // packAggrOneSingle -----------------------------------------------------

  protected val packOptNestedAggrOneSingleString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end()
  }

  protected val packOptNestedAggrOneSingle_ = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val packOptNestedAggrOneSingleDate = (it: jIterator[_]) => {
    add(date2strLocal(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }


  // packAggrManySingle -----------------------------------------------------

  protected val packOptNestedAggrManySingleString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[String])
    end()
  }

  protected val packOptNestedAggrManySingle_ = (it: jIterator[_]) => {
    add(it.next.toString)
  }

  protected val packOptNestedAggrManySingleDate = (it: jIterator[_]) => {
    add(date2strLocal(it.next.asInstanceOf[Date]))
  }
}
