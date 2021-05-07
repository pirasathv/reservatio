package reservatio.api.model

import java.time.LocalDate
import java.util.UUID

import reservatio.core.model.Booking

case class CreateBookingWS(email: String,
                           firstName: String,
                           lastName: String,
                           startDate: String,
                           endDate: String) {

  def toModel(): Booking = reservatio.core.model.Booking(
    uuid = UUID.randomUUID().toString,
    email = Some(this.email),
    firstName = Some(this.firstName),
    lastName = Some(this.lastName),
    startDate = Some(LocalDate.parse(this.startDate)),
    endDate = Some(LocalDate.parse(this.endDate))
  )

}
