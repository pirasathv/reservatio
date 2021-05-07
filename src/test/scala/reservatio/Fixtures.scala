package reservatio

import reservatio.api.model.{CreateBookingWS, UpdateBookingWS}

object Fixtures {

  val createBooking: CreateBookingWS =
    CreateBookingWS(
      "email",
      "first",
      "last",
      "2021-05-23",
      "2021-05-25")

  val updateBooking: UpdateBookingWS =
    UpdateBookingWS(
      "uuid",
      Some("email"),
      Some("first"),
      Some("last"),
      Some("2021-05-23"),
      Some("2021-05-25"))

}
