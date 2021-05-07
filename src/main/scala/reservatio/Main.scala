package reservatio

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}
import reservatio.api.ApiRoutes
import reservatio.core.service.BookingService
import reservatio.repository.BookingRepository
import reservatio.util.HttpServer
import scalikejdbc.config.DBs

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.language.postfixOps
import scala.util.Failure

object Main extends App {

  val logger: Logger = LoggerFactory.getLogger(getClass)
  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val config = ConfigFactory.load()

  val jdbcUrl: String = config.getString("db.default.url")
  val dbUser: String = config.getString("db.default.user")
  val dbPassword: String = config.getString("db.default.password")

  val bookingRepository = new BookingRepository()
  val bookingService = new BookingService(bookingRepository)
  val api: ApiRoutes = new ApiRoutes(bookingService)

  new HttpServer().start(api.routes)

  DBs.setupAll()

  new DatabaseMigrator(jdbcUrl, dbUser, dbPassword).migrateDatabase() andThen {
    case Failure(e) =>
      logger.error("Unexpected failure", e)
      System.exit(1)
  }

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = {
      logger.info("Shutdown hook triggered")
      DBs.closeAll()
      logger.info("Closed all connection pool")
      system.terminate()
      Await.result(system.whenTerminated, 3 seconds)
    }
  })



}
