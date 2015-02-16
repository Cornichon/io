name := """cornichon-io"""

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := Option(System.getProperty("scala.version")).getOrElse("2.11.1")

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT"
)
