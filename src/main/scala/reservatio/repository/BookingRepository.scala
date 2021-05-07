package reservatio.repository

import java.time.{LocalDate, LocalDateTime}

import akka.actor.ActorSystem
import reservatio.repository.model.Booking
import scalikejdbc._

import scala.concurrent.Future

class BookingRepository(implicit val system: ActorSystem) {

  import system.dispatcher

  private val booking = Booking.syntax("booking")

  private val columnSetting: scalikejdbc.ColumnName[Booking] = Booking.column

  def addBooking(book: reservatio.core.model.Booking): Future[Long] = {
    Future {
        val dateTime: LocalDateTime = LocalDateTime.now()
        DB localTx { implicit session =>
          sql"""
               insert into booking (uuid, email, first_name, last_name, start_date, end_date, created_on, last_modified, is_deleted)
               select ${book.uuid}, ${book.email}, ${book.firstName}, ${book.lastName}, date(${book.startDate}), date(${book.endDate}), ${dateTime}, ${dateTime}, 0
               from dual
               where
               (select count(${book.uuid}) from booking
               where
               is_deleted = false AND
               CASE
                 WHEN start_date = date(${book.startDate}) THEN start_date = date(${book.startDate})
                 WHEN end_date   = date(${book.endDate})   THEN end_date   = date(${book.endDate})
                 WHEN start_date = date(${book.endDate})   THEN start_date = date(${book.endDate})
                 WHEN end_date   = date(${book.startDate}) THEN end_date   = date(${book.startDate})
                 WHEN start_date < date(${book.endDate})   AND  end_date   > date(${book.endDate}) AND DATEDIFF(${book.endDate}, ${book.startDate}) <> 1  THEN start_date <  date(${book.endDate}) AND end_date >  date(${book.endDate})
                 WHEN start_date < date(${book.startDate}) AND  end_date   > date(${book.startDate}) AND DATEDIFF(${book.endDate}, ${book.startDate}) <> 1  THEN start_date <  date(${book.startDate}) AND end_date >  date(${book.startDate})
                 WHEN DATEDIFF(${book.endDate}, ${book.startDate}) = 1 THEN  start_date < date(${book.startDate}) AND  date(${book.endDate}) < end_date
               END
               ) < 1 """.updateAndReturnGeneratedKey(1).apply()
        }
    }.recover {
      case e =>
      throw new IllegalArgumentException("Dates are not available for booking")
    }
  }

  def updateBooking(book: reservatio.core.model.Booking): Future[Int] = {
    Future {
      val fields: Map[SQLSyntax, ParameterBinder] = Map(
        columnSetting.uuid -> book.uuid,
        columnSetting.email -> book.email,
        columnSetting.firstName -> book.firstName,
        columnSetting.lastName -> book.lastName,
        columnSetting.startDate -> book.startDate,
        columnSetting.endDate -> book.endDate,
        columnSetting.lastModified -> LocalDateTime.now()
      )

      val filteredFields = fields
        .filter(entry => ParameterBinder.unapply(entry._2) != Some(null))

      DB localTx { implicit session =>
        withSQL {
          update(Booking)
            .set(
              filteredFields
            )
            .where
            .eq(columnSetting.uuid, book.uuid)
            .and
            .eq(columnSetting.isDeleted, false)
        }.update().apply()
      }
    }
  }

  def deleteBookingByUuid(bookingUuid: String): Future[Int] = {
    Future {
      DB localTx { implicit session =>
        withSQL {
          update(Booking)
            .set(
              columnSetting.lastModified -> LocalDateTime.now(),
              columnSetting.isDeleted -> true
            )
            .where
            .eq(columnSetting.uuid, bookingUuid)
        }.update().apply()
      }
    }
  }

  def getBookingAvailabilities(fromLocalDate: LocalDate, toLocalDate: LocalDate): Future[Map[LocalDate, LocalDate]] = {
    Future {
      DB localTx { implicit session =>
        withSQL {
          select(booking.startDate, booking.endDate)
            .from(Booking as booking)
            .where
            .eq(booking.isDeleted, false)
        }
          .map(rs => rs.localDate(1) -> rs.localDate(2))
          .list.apply().toMap
      }
    }
  }

}
