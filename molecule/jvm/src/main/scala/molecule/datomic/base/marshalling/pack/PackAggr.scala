package molecule.datomic.base.marshalling.pack

import java.util.{Date, Iterator => jIterator, List => jList, Set => jSet}
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.DatomicRpc.date2strLocal


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

  //  protected val packAggrOneListInt = (it: jIterator[_]) => {
  //    val vs = it.next.asInstanceOf[jList[_]].iterator
  //    while (vs.hasNext)
  //      add(vs.next.toString)
  //    end()
  //  }
  //
  //  protected val packAggrOneListLong = (it: jIterator[_]) => {
  //    val vs = it.next.asInstanceOf[jList[_]].iterator
  //    while (vs.hasNext)
  //      add(vs.next.toString)
  //    end()
  //  }
  //
  //  protected val packAggrOneListDouble = (it: jIterator[_]) => {
  //    val vs = it.next.asInstanceOf[jList[_]].iterator
  //    while (vs.hasNext)
  //      add(vs.next.toString)
  //    end()
  //  }
  //
  //  protected val packAggrOneListBoolean = (it: jIterator[_]) => {
  //    val vs = it.next.asInstanceOf[jList[_]].iterator
  //    while (vs.hasNext)
  //      add(vs.next.toString)
  //    end()
  //  }

  protected val packAggrOneListDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }

  //  protected val packAggrOneListUUID = (it: jIterator[_]) => {
  //    val vs = it.next.asInstanceOf[jList[_]].iterator
  //    while (vs.hasNext)
  //      add(vs.next.toString)
  //    end()
  //  }
  //
  //  protected val packAggrOneListURI = (it: jIterator[_]) => {
  //    val vs = it.next.asInstanceOf[jList[_]].iterator
  //    while (vs.hasNext)
  //      add(vs.next.toString)
  //    end()
  //  }
  //
  //  protected val packAggrOneListBigInt = (it: jIterator[_]) => {
  //    val vs = it.next.asInstanceOf[jList[_]].iterator
  //    while (vs.hasNext)
  //      add(vs.next.toString)
  //    end()
  //  }
  //
  //  protected val packAggrOneListBigDecimal = (it: jIterator[_]) => {
  //    val vs = it.next.asInstanceOf[jList[_]].iterator
  //    while (vs.hasNext)
  //      add(vs.next.toString)
  //    end()
  //  }


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

//  protected val packAggrManyListInt = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListLong = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListDouble = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListBoolean = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }

  protected val packAggrManyListDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }

//  protected val packAggrManyListUUID = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListURI = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListBigInt = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListBigDecimal = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }


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

//  protected val packAggrOneListDistinctInt = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListDistinctLong = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListDistinctDouble = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListDistinctBoolean = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }

  protected val packAggrOneListDistinctDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }

//  protected val packAggrOneListDistinctUUID = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListDistinctURI = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListDistinctBigInt = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListDistinctBigDecimal = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }


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

//  protected val packAggrManyListDistinctInt = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListDistinctLong = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListDistinctDouble = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListDistinctBoolean = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }

  protected val packAggrManyListDistinctDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }

//  protected val packAggrManyListDistinctUUID = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListDistinctURI = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListDistinctBigInt = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListDistinctBigDecimal = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jSet[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }


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

//  protected val packAggrOneListRandInt = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListRandLong = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListRandDouble = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListRandBoolean = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }

  protected val packAggrOneListRandDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }

//  protected val packAggrOneListRandUUID = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListRandURI = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListRandBigInt = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrOneListRandBigDecimal = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }


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

//  protected val packAggrManyListRandInt = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListRandLong = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListRandDouble = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListRandBoolean = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }

  protected val packAggrManyListRandDate = (it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }

//  protected val packAggrManyListRandUUID = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListRandURI = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListRandBigInt = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }
//
//  protected val packAggrManyListRandBigDecimal = (it: jIterator[_]) => {
//    val vs = it.next.asInstanceOf[jList[_]].iterator
//    while (vs.hasNext)
//      add(vs.next.toString)
//    end()
//  }


  // packAggrSingleSample -----------------------------------------------------

  protected val packAggrSingleSampleString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end()
  }

  protected val packAggrSingleSample = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
  }

//  protected val packAggrSingleSampleInt = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  // todo Long" | "ref" | "datom
//  protected val packAggrSingleSampleLong = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  protected val packAggrSingleSampleDouble = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  protected val packAggrSingleSampleBoolean = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }

  protected val packAggrSingleSampleDate = (it: jIterator[_]) => {
    add(date2strLocal(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }

//  protected val packAggrSingleSampleUUID = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  protected val packAggrSingleSampleURI = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  protected val packAggrSingleSampleBigInt = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  protected val packAggrSingleSampleBigDecimal = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }


  // packAggrOneSingle -----------------------------------------------------

  protected val packAggrOneSingleString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end()
  }

  protected val packAggrOneSingle = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
  }

//  protected val packAggrOneSingleInt = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  // todo Long | "ref" | "datom
//  protected val packAggrOneSingleLong = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  protected val packAggrOneSingleDouble = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  protected val packAggrOneSingleBoolean = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }

  protected val packAggrOneSingleDate = (it: jIterator[_]) => {
    add(date2strLocal(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }

//  protected val packAggrOneSingleUUID = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  protected val packAggrOneSingleURI = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  protected val packAggrOneSingleBigInt = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }
//
//  protected val packAggrOneSingleBigDecimal = (it: jIterator[_]) => {
//    add(it.next.asInstanceOf[jList[_]].iterator.next.toString)
//  }


  // packAggrManySingle -----------------------------------------------------

  protected val packAggrManySingleString = (it: jIterator[_]) => {
    add(it.next.asInstanceOf[String])
    end()
  }

  protected val packAggrManySingle = (it: jIterator[_]) => {
    add(it.next.toString)
  }

//  protected val packAggrManySingleInt = (it: jIterator[_]) => {
//    add(it.next.toString)
//  }
//
//  protected val packAggrManySingleLong = (it: jIterator[_]) => {
//    add(it.next.toString)
//  }
//
//  protected val packAggrManySingleDouble = (it: jIterator[_]) => {
//    add(it.next.toString)
//  }
//
//  protected val packAggrManySingleBoolean = (it: jIterator[_]) => {
//    add(it.next.toString)
//  }

  protected val packAggrManySingleDate = (it: jIterator[_]) => {
    add(date2strLocal(it.next.asInstanceOf[Date]))
  }

//  protected val packAggrManySingleUUID = (it: jIterator[_]) => {
//    add(it.next.toString)
//  }
//
//  protected val packAggrManySingleURI = (it: jIterator[_]) => {
//    add(it.next.toString)
//  }
//
//  protected val packAggrManySingleBigInt = (it: jIterator[_]) => {
//    add(it.next.toString)
//  }
//
//  protected val packAggrManySingleBigDecimal = (it: jIterator[_]) => {
//    add(it.next.toString)
//  }
}
