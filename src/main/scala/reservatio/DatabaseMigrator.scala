package reservatio

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.flywaydb.core.Flyway
import scalikejdbc.{GlobalSettings, LoggingSQLAndTimeSettings}

import scala.concurrent.{ExecutionContext, Future}

class DatabaseMigrator(appJdbcUrl: String, appDbUser: String, appDbPassword: String) {

  GlobalSettings.loggingSQLAndTime = new LoggingSQLAndTimeSettings(
    enabled = true,
    singleLineMode = true,
    logLevel = 'DEBUG
  )

  val hikariConfig: HikariConfig = {
    val config = new HikariConfig()
    config.setJdbcUrl(appJdbcUrl)
    config.setUsername(appDbUser)
    config.setPassword(appDbPassword)
    config.setConnectionTimeout(45000)
    config.setInitializationFailTimeout(0)
    config
  }

  def migrateDatabase()(implicit executionContext: ExecutionContext): Future[Unit] = Future {
    val flywayDatasource = new HikariDataSource(hikariConfig)
    val flyway: Flyway = new Flyway()
    flyway.setDataSource(flywayDatasource)
    flyway.migrate()
    flywayDatasource.close()
  }

}
