package reservatio.util

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.Flow
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success}

class HttpServer {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  def start(handler: Flow[HttpRequest, HttpResponse, Any])(implicit system: ActorSystem): Unit = {
    import system.dispatcher
    val interface = system.settings.config.getString("http.interface")
    val port = system.settings.config.getInt("http.port")

    Http()
      .bindAndHandle(handler, interface, port)
      .onComplete {
        case Success(binding) =>
          val address = binding.localAddress
          logger.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
        case Failure(e) =>
          logger.error("Failed to bind HTTP endpoint, terminating system", e)
          system.terminate()
      }
  }
}
