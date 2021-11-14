package molecule.datomic.base.marshalling.packers

import java.util.{Date, List => jList, Set => jSet}
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.PackBase

private[molecule] trait PackFlatAggr extends PackBase with Helpers {

  protected val packAggrInt =
    (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => add(sb, row.get(colIndex).toString)

  protected val packAggrDouble =
    (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => add(sb, row.get(colIndex).toString)

  // packAggrOneList -----------------------------------------------------

  protected val packAggrOneListString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packAggrOneList = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packAggrOneListDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }


  // packAggrManyList -----------------------------------------------------

  protected val packAggrManyListString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packAggrManyList = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packAggrManyListDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }



  // packAggrOneListDistinct -----------------------------------------------------

  protected val packAggrOneListDistinctString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packAggrOneListDistinct = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packAggrOneListDistinctDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }


  // packAggrManyListDistinct -----------------------------------------------------

  protected val packAggrManyListDistinctString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packAggrManyListDistinct = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packAggrManyListDistinctDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }


  // packAggrOneListRand -----------------------------------------------------

  protected val packAggrOneListRandString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packAggrOneListRand = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packAggrOneListRandDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }


  // packAggrManyListRand -----------------------------------------------------

  protected val packAggrManyListRandString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packAggrManyListRand = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packAggrManyListRandDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    val vs = row.get(colIndex).asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }


  // packAggrSingleSample -----------------------------------------------------

  protected val packAggrSingleSampleString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    add(sb, row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end(sb)
  }

  protected val packAggrSingleSample = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    add(sb, row.get(colIndex).asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val packAggrSingleSampleDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    add(sb, date2str(row.get(colIndex).asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }


  // packAggrOneSingle -----------------------------------------------------

  protected val packAggrOneSingleString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    add(sb, row.get(colIndex).toString)
    end(sb)
  }

  protected val packAggrOneSingle = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    add(sb, row.get(colIndex).toString)
  }

  protected val packAggrOneSingleDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    add(sb, date2str(row.get(colIndex).asInstanceOf[Date]))
  }


  // packAggrManySingle -----------------------------------------------------

  protected val packAggrManySingleString = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    add(sb, row.get(colIndex).asInstanceOf[String])
    end(sb) // end of string
    end(sb) // end of elements list
  }

  protected val packAggrManySingle = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    add(sb, row.get(colIndex).toString)
    end(sb) // end of elements list
  }

  protected val packAggrManySingleDate = (sb: StringBuffer, colIndex: Int) => (row: jList[_]) => {
    add(sb, date2str(row.get(colIndex).asInstanceOf[Date]))
    end(sb) // end of elements list
  }
}
