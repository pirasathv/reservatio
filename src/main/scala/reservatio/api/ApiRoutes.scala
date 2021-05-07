package reservatio.api

import akka.actor.ActorSystem
import akka.event.Logging.{DebugLevel, InfoLevel}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import org.slf4j.{Logger, LoggerFactory}
import reservatio.api.route.{BookingApiRoute, HealthApiRoute}
import reservatio.core.service.BookingService

import scala.util.control.NonFatal

class ApiRoutes(bookingService: BookingService)(implicit val system: ActorSystem) {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  val health = new HealthApiRoute
  val booking = new BookingApiRoute(bookingService)

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: IllegalAccessException =>
      logger.error("HTTP request failed due to an illegal access exception", e)
      complete(
        HttpResponse(status = Forbidden, entity = e.getMessage)
      )
    case e: IllegalArgumentException =>
      logger.error("HTTP request failed due to an illegal argument exception", e)
      complete(
        HttpResponse(status = BadRequest, entity = e.getMessage)
      )
    case e: UnsupportedOperationException =>
      logger.error("HTTP request failed", e)
      complete(
        HttpResponse(status = BadRequest)
      )
    case e: NoSuchElementException =>
      logger.error("HTTP request failed: the requested resource was not found", e)
      complete(
        HttpResponse(status = NotFound, entity = e.getMessage)
      )
    case e: NotImplementedError =>
      logger.error("The endpoint functionality is not yet implemented", e)
      complete(
        HttpResponse(status = NotImplemented)
      )
    case NonFatal(e) =>
      logger.error("HTTP request failed due to an internal error", e)
      complete(
        HttpResponse(status = InternalServerError)
      )
  }

  val routes: Route = {
    health.route ~
      (logRequest("reservatio", InfoLevel)
        & logResult("reservatio", DebugLevel)
        & handleExceptions(exceptionHandler)) {
        pathPrefix("api" / "reservatio" / "v1") {
          booking.route
        }
      }
  }

}
