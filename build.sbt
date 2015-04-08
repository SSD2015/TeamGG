name := """exceedvote"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)

scalaVersion := "2.11.1"

resolvers += "VFS-S3" at "http://dl.bintray.com/content/abashev/vfs-s3"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  filters,
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.1.8",
  "com.sun.mail" % "javax.mail" % "1.5.2",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4",
  "commons-collections" % "commons-collections" % "3.2.1",
  "org.springframework.security" % "spring-security-core" % "3.2.6.RELEASE",
  "com.github.detro" % "phantomjsdriver" % "1.2.0",
  "org.apache.commons" % "commons-vfs2" % "2.1.1660580.1",
  "com.intridea.io" % "vfs-s3" % "2.3.1"
)

pipelineStages in Assets := Seq(uglify, cssCompress, digest, gzip)

// exclude web module
excludeFilter in cssCompress := new SimpleFileFilter({
  f =>
    def fileStartsWith(dir: File): Boolean = f.getPath.startsWith(dir.getPath)
    fileStartsWith((WebKeys.webModuleDirectory in Assets).value)
})

// by default uglify exclude public folder, this reinclude it back and exclude web assets
excludeFilter in uglify := new SimpleFileFilter({
  f =>
    def fileStartsWith(dir: File): Boolean = f.getPath.startsWith(dir.getPath)
    fileStartsWith((WebKeys.webModuleDirectory in Assets).value)
})