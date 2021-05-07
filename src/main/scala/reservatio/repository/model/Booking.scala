package reservatio.repository.model

import java.time.{LocalDate, LocalDateTime}

import reservatio.core.model
import scalikejdbc.{WrappedResultSet, _}

case class Booking(id: Int,
                   uuid:String,
                   email: String,
                   firstName:String,
                   lastName:String,
                   startDate: LocalDate,
                   endDate: LocalDate,
                   createdOn: LocalDateTime,
                   lastModified: LocalDateTime,
                   isDeleted: Boolean
                  ){
  def toModel: model.Booking = {
    reservatio.core.model.Booking(
      uuid = this.uuid,
      email = Some(this.email),
      firstName = Some(this.firstName),
      lastName = Some(this.lastName),
      startDate = Some(this.startDate),
      endDate = Some(this.endDate)
    )
  }
}

object Booking extends SQLSyntaxSupport[Booking] {

  override val tableName = "booking"

  def apply(e: ResultName[Booking])(rs: WrappedResultSet): Booking =
    Booking(
      id = rs.int(e.id),
      uuid = rs.string(e.uuid),
      email = rs.string(e.email),
      firstName = rs.string(e.firstName),
      lastName = rs.string(e.lastName),
      startDate = rs.localDate(e.startDate),
      endDate = rs.localDate(e.endDate),
      createdOn = rs.localDateTime(e.createdOn),
      lastModified = rs.localDateTime(e.lastModified),
      isDeleted = rs.boolean(e.isDeleted)
    )

}
