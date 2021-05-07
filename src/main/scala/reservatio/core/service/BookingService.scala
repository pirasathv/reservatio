package reservatio.core.service

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, ZoneOffset}

import akka.actor.ActorSystem
import reservatio.core.model.Booking
import reservatio.repository.BookingRepository

import scala.collection.mutable
import scala.concurrent.Future

class BookingService(bookingRepository: BookingRepository)(
  implicit val system: ActorSystem) {

  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  import system.dispatcher

  def add(booking: Booking): Future[String] = {

    val duration = evalPeriod(booking.startDate.get, booking.endDate.get)

    verifyConstraints(duration, booking.startDate.get, booking.endDate.get)

    getAvailabilities(booking.startDate.get, booking.endDate.get).map {
      dates =>
        for (i <- 0 to duration) {
          if (!dates.toSet.contains(booking.startDate.get.plusDays(i))) {
            throw new IllegalArgumentException("Dates are not available for booking")
          }
        }
    }.flatMap { _ =>
      bookingRepository.addBooking(booking).map { _ => booking.uuid }
    }
  }

  def update(booking: Booking): Future[Int] = {
    bookingRepository.updateBooking(booking)
  }

  def delete(bookingUuid: String): Future[Int] = {
    bookingRepository.deleteBookingByUuid(bookingUuid)
  }

  def getAvailabilities(fromLocalDate: LocalDate, toLocalDate: LocalDate): Future[Seq[LocalDate]] = {

    val availableDates: mutable.Set[LocalDate] =
      collection.mutable.Set(fromLocalDate.toEpochDay.until(toLocalDate.plusDays(1).toEpochDay)
        .map(LocalDate.ofEpochDay): _*)

    bookingRepository.getBookingAvailabilities(fromLocalDate, toLocalDate).map {
      reservedDates =>
        reservedDates.map {
          dbDate =>
            dbDate._1.toEpochDay.until(dbDate._2.plusDays(1).toEpochDay).map(LocalDate.ofEpochDay)
              .map(availableDates.remove)
            availableDates
        }
    }.map {
      _ => availableDates.toSeq.sorted
    }
  }

  def evalPeriod(fromLocalDate: LocalDate, toLocalDate: LocalDate): Int = {
    val daysElapsed: Int = ChronoUnit.DAYS.between(fromLocalDate, toLocalDate).toInt
    daysElapsed
  }

  def verifyConstraints(duration: Int, startDate: LocalDate, endDate: LocalDate): Unit = {

    if (startDate == endDate) {
      throw new IllegalArgumentException("start date can not be same as end date")
    }

    if (duration > 3) {
      throw new IllegalArgumentException("Max booking duration should not exceed 3 days")
    }

    if (startDate.atStartOfDay(ZoneOffset.UTC).toInstant.toEpochMilli >
      endDate.atStartOfDay(ZoneOffset.UTC).toInstant.toEpochMilli) {
      throw new IllegalArgumentException("Start date can't be later date then End date")
    }

    if (startDate.atStartOfDay(ZoneOffset.UTC).toInstant.toEpochMilli <=
      LocalDate.now().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant.toEpochMilli) {
      throw new IllegalArgumentException("Start date must be minimum 1 day(s) ahead of arrival")
    }

    if (endDate.atStartOfDay(ZoneOffset.UTC).toInstant.toEpochMilli >=
      LocalDate.now().plusMonths(1).atStartOfDay(ZoneOffset.UTC).toInstant.toEpochMilli) {
      throw new IllegalArgumentException("End date must be maximum 1 month ahead of arrival")
    }
  }

}
