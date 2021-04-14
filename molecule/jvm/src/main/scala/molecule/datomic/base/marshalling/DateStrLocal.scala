package molecule.datomic.base.marshalling

import java.text.SimpleDateFormat
import java.util.Date

trait DateStrLocal {

  def date2strLocal(date: Date): String =
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date)

  def strLocal2date(date: String): Date =
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(date)


  def localDatomicDate(date: Date): String =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(date)


}
