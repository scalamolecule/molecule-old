package molecule.util
import java.time.{ZoneId, LocalDate}
import java.util.Date

trait Helpers {

  object date {
      def apply(year: Int, month: Int, day: Int): Date = {
        val localDate = LocalDate.of(year, month, day)
        val instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant
        Date.from(instant)
      }
    }
}
