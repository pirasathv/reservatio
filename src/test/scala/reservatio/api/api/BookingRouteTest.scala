package reservatio.api.api

import java.time.LocalDate
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestKitBase
import org.json4s.JsonAST.{JObject, JString}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import reservatio.Fixtures
import reservatio.api.JsonSupport._
import reservatio.api.model.{CreateBookingWS, UpdateBookingWS}
import reservatio.api.route.BookingApiRoute
import reservatio.core.service.BookingService

import scala.concurrent.Future

class BookingRouteTest
  extends WordSpec
    with Matchers
    with ScalatestRouteTest
    with MockitoSugar
    with TestKitBase
    with ScalaFutures
    with BeforeAndAfterAll {

  val bookingService: BookingService = mock[BookingService]
  val bookingRoute = new BookingApiRoute(bookingService)
  val route: Route = bookingRoute.route

  "Booking api" should {
    "return success when make a new reservation" in {

      val booking: CreateBookingWS = Fixtures.createBooking
      val bookingUuid = UUID.randomUUID().toString

      when {
        bookingService.add(any())
      }.thenReturn(Future.successful(bookingUuid))

      Post("/bookings", booking) ~> route ~> check {
        responseAs[JObject] shouldEqual JObject(List(("uuid",JString(bookingUuid))))
        status.value shouldEqual StatusCodes.OK.toString()
      }
    }
  }

  "Booking api" should {
    "return bad request when it fails to make a new reservation" in {

      val booking: CreateBookingWS = Fixtures.createBooking

      when {
        bookingService.add(any())
      }.thenReturn(Future.failed(new IllegalArgumentException("Dates are not available for booking")))

      Post("/bookings", booking) ~> route ~> check {
        status shouldEqual StatusCodes.InternalServerError
      }
    }
  }

  "Booking api" should {
    "return success when updating an existing reservation" in {

      val booking: UpdateBookingWS = Fixtures.updateBooking

      when {
        bookingService.update(any())
      }.thenReturn(Future.successful(1))

      Put("/bookings/change", booking) ~> route ~> check {
        status.value shouldEqual StatusCodes.OK.toString()
      }
    }
  }

  "Booking api" should {
    "return success when reservation is deleted" in {

      val uuid = UUID.randomUUID().toString

      when {
        bookingService.delete(uuid)
      }.thenReturn(Future.successful(1))

      Delete(s"/bookings?uuid=$uuid") ~> route ~> check {
        status.value shouldEqual StatusCodes.OK.toString()
      }
    }
  }

  "Booking api" should {
    "return all available dates available for reservation" in {

      val fromLocalDate = LocalDate.parse("2021-05-22")
      val toLocalDate = LocalDate.parse("2021-05-26")

      val availableDate = Seq(LocalDate.parse("2021-05-22"), LocalDate.parse("2021-05-26"))

      when {
        bookingService.getAvailabilities(fromLocalDate,toLocalDate)
      }.thenReturn(Future.successful(availableDate))

      Get(s"/bookings?fromDate=2021-05-22&toDate=2021-05-26") ~> route ~> check {
        responseAs[JObject] shouldEqual JObject(List(("availableDates",JString("2021-05-22,2021-05-26"))))
        status.value shouldEqual StatusCodes.OK.toString()
      }
    }
  }

}
