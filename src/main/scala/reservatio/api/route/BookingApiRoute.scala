package reservatio.api.route

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.{complete, pathPrefix, _}
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory
import reservatio.api.JsonSupport._
import reservatio.api.model.{CreateBookingWS, UpdateBookingWS}
import reservatio.core.model.Booking
import reservatio.core.service.BookingService

import scala.util.{Failure, Success, Try}

class BookingApiRoute(bookingService: BookingService)(
  implicit val system: ActorSystem) {

  val route: Route = (pathPrefix("bookings") & pathEndOrSingleSlash) {
    (post & entity(as[CreateBookingWS])) { booking =>
      Try(booking.toModel()) match {
        case Success(value) =>
          onComplete(bookingService.add(value)) {
            case Success(result) => complete(Booking(result, None, None, None, None, None))
            case Failure(e) =>
              system.log
                .error(e, "Unable to reserve campsite")
              throw e
          }
        case Failure(e) => LoggerFactory
          .getLogger(getClass)
          .warn(
            s"Invalid date range provided unable to reserve campsite",
            e)
          throw new IllegalArgumentException("Invalid date range provided unable to reserve campsite")
      }
    } ~ (delete & pathEndOrSingleSlash & parameters('uuid)) { uuid =>
      onComplete(bookingService.delete(uuid)) {
        case Success(_) => complete(akka.http.scaladsl.model.StatusCodes.Success)
        case Failure(e) =>
          system.log
            .error(e, "Unable to delete reservation")
          throw e
      }
    } ~ (get & parameters('fromDate, 'toDate)) { (fromDate, toDate) =>
      Try {
        val fromLocalDate = LocalDate.parse(fromDate)
        val toLocalDate = LocalDate.parse(toDate)
        (fromLocalDate, toLocalDate)
      } match {
        case Success((fromLocalDate, toLocalDate)) =>
          onComplete(bookingService.getAvailabilities(fromLocalDate, toLocalDate)) {
            case Success(result) => complete("availableDates" -> (result mkString ","))
            case Failure(e) => throw e
          }
        case Failure(e) => system.log
          .error(e, "Unable to evaluate provided dates")
          throw e
      }
    } ~ get {
      Try {
        val fromLocalDate = LocalDate.now()
        val toLocalDate = LocalDate.now().plusMonths(1)
        (fromLocalDate, toLocalDate)
      } match {
        case Success((fromLocalDate, toLocalDate)) =>
          onComplete(bookingService.getAvailabilities(fromLocalDate, toLocalDate)) {
            case Success(result) => complete("availableDates" -> (result mkString ","))
            case Failure(e) => throw e
          }
        case Failure(e) => system.log
          .error(e, "Unable to evaluate provided dates")
          throw e
      }

    }
  } ~ (put & path("bookings" / "change") & entity(as[UpdateBookingWS])) { booking =>
    Try(booking.toModel) match {
      case Success(value) =>
        onComplete(bookingService.update(value)) {
          case Success(_) => complete(akka.http.scaladsl.model.StatusCodes.Success)
          case Failure(e) => throw e
        }
      case Failure(e) => LoggerFactory
        .getLogger(getClass)
        .warn(
          s"Invalid date range provided unable to update reservation",
          e)
        throw new IllegalArgumentException("Invalid date range provided unable to update reservation")
    }
  }
}
