package molecule.datomic.base.marshalling.packers

import java.util.{Date, Iterator => jIterator, List => jList, Set => jSet}
import molecule.core.util.Helpers
import molecule.datomic.base.marshalling.PackBase

private[molecule] trait PackOptNestedAggr extends PackBase with Helpers {

  protected val packAggrInt    = (sb: StringBuffer, it: jIterator[_]) => add(sb, it.next.toString)
  protected val packAggrDouble = (sb: StringBuffer, it: jIterator[_]) => add(sb, it.next.toString)

  // packAggrOneList -----------------------------------------------------

  protected val packOptNestedAggrOneListString = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packOptNestedAggrOneList_ = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packOptNestedAggrOneListDate = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }


  // packAggrManyList -----------------------------------------------------

  protected val packOptNestedAggrManyListString = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packOptNestedAggrManyList_ = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packOptNestedAggrManyListDate = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }



  // packAggrOneListDistinct -----------------------------------------------------

  protected val packOptNestedAggrOneListDistinctString = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packOptNestedAggrOneListDistinct_ = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packOptNestedAggrOneListDistinctDate = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }


  // packAggrManyListDistinct -----------------------------------------------------

  protected val packOptNestedAggrManyListDistinctString = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packOptNestedAggrManyListDistinct_ = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packOptNestedAggrManyListDistinctDate = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jSet[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }


  // packAggrOneListRand -----------------------------------------------------

  protected val packOptNestedAggrOneListRandString = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packOptNestedAggrOneListRand_ = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packOptNestedAggrOneListRandDate = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }


  // packAggrManyListRand -----------------------------------------------------

  protected val packOptNestedAggrManyListRandString = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext) {
      add(sb, vs.next.toString)
      end(sb)
    }
    end(sb)
  }

  protected val packOptNestedAggrManyListRand_ = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, vs.next.toString)
    end(sb)
  }

  protected val packOptNestedAggrManyListRandDate = (sb: StringBuffer, it: jIterator[_]) => {
    val vs = it.next.asInstanceOf[jList[_]].iterator
    while (vs.hasNext)
      add(sb, date2str(vs.next.asInstanceOf[Date]))
    end(sb)
  }


  // packAggrSingleSample -----------------------------------------------------

  protected val packOptNestedAggrSingleSampleString = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end(sb)
  }

  protected val packOptNestedAggrSingleSample_ = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, it.next.asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val packOptNestedAggrSingleSampleDate = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, date2str(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }


  // packAggrOneSingle -----------------------------------------------------

  protected val packOptNestedAggrOneSingleString = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[String])
    end(sb)
  }

  protected val packOptNestedAggrOneSingle_ = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, it.next.asInstanceOf[jList[_]].iterator.next.toString)
  }

  protected val packOptNestedAggrOneSingleDate = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, date2str(it.next.asInstanceOf[jList[_]].iterator.next.asInstanceOf[Date]))
  }


  // packAggrManySingle -----------------------------------------------------

  protected val packOptNestedAggrManySingleString = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, it.next.asInstanceOf[String])
    end(sb)
  }

  protected val packOptNestedAggrManySingle_ = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, it.next.toString)
  }

  protected val packOptNestedAggrManySingleDate = (sb: StringBuffer, it: jIterator[_]) => {
    add(sb, date2str(it.next.asInstanceOf[Date]))
  }
}
