name := """exceedvote"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  filters,
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.1.8",
  "com.sun.mail" % "javax.mail" % "1.5.2",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4"
)

pipelineStages in Assets := Seq(cssCompress, digest, gzip)

// exclude web module
excludeFilter in cssCompress := new SimpleFileFilter({
  f =>
    def fileStartsWith(dir: File): Boolean = f.getPath.startsWith(dir.getPath)
    fileStartsWith((WebKeys.webModuleDirectory in Assets).value)
})