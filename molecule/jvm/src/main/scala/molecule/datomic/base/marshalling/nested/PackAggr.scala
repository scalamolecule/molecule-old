package molecule.datomic.base.marshalling.nested

import java.util.{Date, Iterator => jIterator, List => jList, Set => jSet}
import molecule.datomic.base.marshalling.DatomicRpc.date2strLocal
import molecule.datomic.base.marshalling.PackBase

trait PackAggr extends PackBase {

  protected val packAggrInt    = (colIndex: Int) => (row: jList[_]) => add(row.get(colIndex).toString)
  protected val packAggrDouble = (colIndex: Int) => (row: jList[_]) => add(row.get(colIndex).toString)

  // packAggrOneList -----------------------------------------------------

  protected val packAggrOneListString = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrOneList = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrOneListDate = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrManyList -----------------------------------------------------

  protected val packAggrManyListString = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrManyList = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrManyListDate = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }



  // packAggrOneListDistinct -----------------------------------------------------

  protected val packAggrOneListDistinctString = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrOneListDistinct = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrOneListDistinctDate = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrManyListDistinct -----------------------------------------------------

  protected val packAggrManyListDistinctString = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrManyListDistinct = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrManyListDistinctDate = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrOneListRand -----------------------------------------------------

  protected val packAggrOneListRandString = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrOneListRand = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrOneListRandDate = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrManyListRand -----------------------------------------------------

  protected val packAggrManyListRandString = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(vs.next.toString)
      end()
    }
    end()
  }

  protected val packAggrManyListRand = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(vs.next.toString)
    end()
  }

  protected val packAggrManyListRandDate = (colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(date2strLocal(vs.next.asInstanceOf[Date]))
    end()
  }


  // packAggrSingleSample -----------------------------------------------------

  protected val packAggrSingleSampleString = (colIndex: Int) => (row: jList[_]) => {
    add(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end()
  }

  protected val packAggrSingleSample = (colIndex: Int) => (row: jList[_]) => {
    add(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val packAggrSingleSampleDate = (colIndex: Int) => (row: jList[_]) => {
    add(date2strLocal(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }


  // packAggrOneSingle -----------------------------------------------------

  protected val packAggrOneSingleString = (colIndex: Int) => (row: jList[_]) => {
    add(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end()
  }

  protected val packAggrOneSingle = (colIndex: Int) => (row: jList[_]) => {
    add(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val packAggrOneSingleDate = (colIndex: Int) => (row: jList[_]) => {
    add(date2strLocal(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }


  // packAggrManySingle -----------------------------------------------------

  protected val packAggrManySingleString = (colIndex: Int) => (row: jList[_]) => {
    add(row.get(colIndex).asInstanceOf[String])
    end()
  }

  protected val packAggrManySingle = (colIndex: Int) => (row: jList[_]) => {
    add(row.get(colIndex).toString)
  }

  protected val packAggrManySingleDate = (colIndex: Int) => (row: jList[_]) => {
    add(date2strLocal(row.get(colIndex).asInstanceOf[Date]))
  }
}
