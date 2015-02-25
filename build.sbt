name := """exceedvote"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  filters,
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.1.8",
  "com.sun.mail" % "javax.mail" % "1.5.2"
)
