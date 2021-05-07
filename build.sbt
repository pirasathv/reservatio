import com.typesafe.sbt.packager.docker.Cmd
name := "reservatio"

val akkaHttpVersion = "10.1.11"
val akkaVersion = "2.6.4"
val json4sVersion = "3.7.0-M2"

version := "0.1"
scalaVersion := "2.12.10"

lazy val `root` = (project in file("."))
  .enablePlugins(DockerPlugin,AshScriptPlugin)
  .settings(
    packageName in Docker := "pirasath.vallipuram/" +  packageName.value,
    dockerBaseImage := "frolvlad/alpine-oraclejre8",
    dockerExposedPorts := Seq(9000),
    dockerExposedVolumes := Seq("/opt/docker/logs"),
    dockerCommands := dockerCommands.value.flatMap{
      case cmd@Cmd("FROM",_) => List(cmd, Cmd("RUN", "apk update && apk add bash"))
      case other => List(other)
    }
)

libraryDependencies ++= {
  Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "net.logstash.logback" % "logstash-logback-encoder" % "4.11",

    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "de.heikoseeberger" %% "akka-http-json4s" % "1.32.0",
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,

    "org.json4s" %% "json4s-jackson" % json4sVersion,
    "org.json4s" %% "json4s-ext" % json4sVersion,

    "org.scalikejdbc" %% "scalikejdbc" % "3.4.1",
    "org.scalikejdbc" %% "scalikejdbc-config" % "3.4.1",
    "mysql" % "mysql-connector-java" % "8.0.24",
    "com.zaxxer" % "HikariCP" % "3.4.5",
    "org.flywaydb" % "flyway-core" % "4.0.3",

    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "org.scalatest" %% "scalatest" % "3.0.8" % Test,
    "org.mockito" % "mockito-core" % "2.8.47" % Test
  )
}

