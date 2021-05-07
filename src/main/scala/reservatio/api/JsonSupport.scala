package reservatio.api

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.ext.JavaTypesSerializers
import org.json4s.{DefaultFormats, Formats, Serialization, jackson}

object JsonSupport extends Json4sSupport {

  implicit val serialization: Serialization = jackson.Serialization

  implicit val formats: Formats = new DefaultFormats {
    override val allowNull: Boolean = false
  } ++ JavaTypesSerializers.all


}
