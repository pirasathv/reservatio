package reservatio.core.model

import java.time.LocalDate

case class Booking(uuid: String,
                   email: Option[String],
                   firstName: Option[String],
                   lastName: Option[String],
                   startDate: Option[LocalDate],
                   endDate: Option[LocalDate])