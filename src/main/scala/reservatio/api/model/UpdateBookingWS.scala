package reservatio.api.model

import java.time.LocalDate

import reservatio.core.model.Booking

case class UpdateBookingWS(uuid: String,
                           email: Option[String],
                           firstName: Option[String],
                           lastName: Option[String],
                           startDate: Option[String],
                           endDate: Option[String]) {

  def toModel: Booking = {
    reservatio.core.model.Booking(
      uuid = this.uuid,
      email = this.email,
      firstName = this.firstName,
      lastName = this.lastName,
      startDate = this.startDate.map(LocalDate.parse(_)),
      endDate = this.endDate.map(LocalDate.parse(_))
    )
  }
}