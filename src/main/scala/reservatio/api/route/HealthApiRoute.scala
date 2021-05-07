package reservatio.api.route

import akka.http.scaladsl.model.HttpEntity.Empty
import akka.http.scaladsl.model.StatusCodes.NoContent
import akka.http.scaladsl.server.Directives.{complete, get, pathPrefix, _}
import akka.http.scaladsl.server.Route

class HealthApiRoute {

  val route: Route =
    (pathPrefix("health") & get & pathEndOrSingleSlash) {
      complete(NoContent, Empty)
    }

}
